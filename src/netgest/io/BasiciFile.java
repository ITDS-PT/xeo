/*Enconding=UTF-8*/
package netgest.io;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.boConfig;
import netgest.bo.data.Driver;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.io.metadata.iMetadataItem;
import netgest.utils.ClassUtils;
import netgest.utils.DataUtils;

public class BasiciFile implements iFile  {
    
    public static final String IFILE_SERVICE_NAME = "basic";
    private static final String PATH_SEP = "/";
    
    private BasiciFileProvider provider = null;
    
    // File Attributes    
    private long fileId = -1;
    private String fileName = null;
    private long fileLength = -1;
    private boolean exists = false;
    private String filePath = null;
        
    public BasiciFile(BasiciFileProvider provider, String path) 
    {
        init(provider,path,null);
    }
    public BasiciFile(BasiciFileProvider provider, String path,Connection cn) 
    {
        init(provider,path,cn);
    }
    private void init(BasiciFileProvider provider,String path,Connection cn) 
    {
        this.provider = provider;
        boolean myConn = false;
        if(cn == null)
        {
            cn = this.provider.getConnection();
            myConn = true;
        }             
        if(cn != null)
        {
            parsePath(path,cn);
            loadFileInformation(cn);
        }
        if(myConn)
        {
            try
            {
                if(cn != null) cn.close();
            }
            catch (SQLException e)
            {
                
            }
        }        
    }
    private void parsePath(String path,Connection cn) 
    {
        String name = null;
        path = path.trim();
        if( path.length()==0 || path.equalsIgnoreCase( PATH_SEP ) ) 
        {
            name = IFILE_SERVICE_NAME;
        } 
        else 
        {
            if( path.endsWith(PATH_SEP) ) 
            {
                path = path.substring(0,path.length()-1);
            }
            name = path.substring(path.lastIndexOf( PATH_SEP ) + 1);
            path = path.substring(0,path.lastIndexOf( PATH_SEP ));
        }
        if(!path.startsWith(PATH_SEP)) 
        {
            path = PATH_SEP+path;
        }
        if(!path.endsWith(PATH_SEP)) 
        {
            path = path.concat(PATH_SEP);
        }
        
        this.filePath = path;
        this.fileName = name;
        setFileId(cn);
    }  
    
    private void setFileId(Connection cn)
    {        
        String prefixPath = PATH_SEP + PATH_SEP + IFILE_SERVICE_NAME + PATH_SEP;
        if(prefixPath.equals(this.filePath) || this.filePath == null)
        {
        	//Workaround Files old Viewers
        	EboContext ctx=boApplication.currentContext().getEboContext();
        	boSession session=null;
        	boolean newCtx=false;
        	if (ctx==null)
        	{
				try {
					session = boApplication.getApplicationFromConfig("XEO").
						boLogin("SYSTEM", boLoginBean.getSystemKey());
				} catch (boLoginException e) {
					// TODO Auto-generated catch block
				}
				if (session!=null)
				{
					ctx =session.createRequestContext(null, null, null);
					newCtx=true;
				}
        	}
        		
            this.fileId = DataUtils.getDataDBSequence( ctx , this.provider.p_dbfs_table_file + "_SEQ", Driver.SEQUENCE_NEXTVAL );            
            this.filePath = this.filePath + this.fileId + PATH_SEP;
            if (newCtx)
            {
            	if (session!=null)session.closeSession();
            	if (ctx!=null)ctx.close();
            }
        }
        else
        {
            String id = this.filePath.substring(prefixPath.length(),this.filePath.lastIndexOf(PATH_SEP));
            this.fileId = ClassUtils.convertToLong(id,-1);   
        }             
    }
    private void loadFileInformation(Connection cn)
    {
        PreparedStatement pstm = null;
        ResultSet rslt = null;      
        
        StringBuffer sql = new StringBuffer();

		
		String database = boConfig.getApplicationConfig().getDataDataSourceClassName();
		if (database.equalsIgnoreCase(OracleDBM.SQLSERVER_IMPL)){
			sql.append("SELECT ID,FILENAME,DATALENGTH(BINDATA) FROM ");
		}			
		else 
			sql.append("SELECT ID,FILENAME,LENGTH(BINDATA) FROM ");

        
        sql.append(this.provider.p_dbfs_table_file).append(" WHERE ID = ?");
        boolean mycn = false;
        try
        {
            if(cn == null)
            {
                cn = provider.getConnection();
                mycn = true;
            }
            pstm = cn.prepareStatement(sql.toString(),ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);            
            pstm.setLong(1,this.fileId);            
            rslt = pstm.executeQuery();
            if(rslt.next()) 
            {
                this.fileName = rslt.getString(2);
                this.fileLength = rslt.getLong(3);
                this.exists = true;
            }
            
        }
        catch (SQLException e)
        {
            throw createRuntimeException("loadFileInformation()",e);            
        } 
        finally 
        {
            try
            {
                if(rslt!=null) rslt.close();
                if(pstm!=null) pstm.close();
                if(mycn && cn!=null)  cn.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }        
    }
    public boolean delete() throws iFilePermissionDenied
    {
        boolean result = false;
        if(exists()) 
        {
            Connection cn = null;
            PreparedStatement pstm = null;            
            try
            {
                cn = provider.getConnection();
                pstm = cn.prepareStatement("DELETE FROM "+provider.p_dbfs_table_file+" WHERE ID = ?");
                pstm.setLong(1,this.fileId);
                pstm.executeUpdate();  
                pstm.close();
                this.exists = false;
                this.fileLength = -1;
                result = true;
            }
            catch (SQLException e)
            {
                throw createRuntimeException("delete()",e);                
            }
            finally 
            {
                try 
                {
                    if(pstm != null) pstm.close();
                    if(cn != null) cn.close();
                } 
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }            
        }
        return result;
    }
    
    public void setBinaryStream( InputStream is ) throws iFilePermissionDenied {
        Connection cn = null;
        ResultSet rslt = null;
        PreparedStatement pstm = null;
        try
        {    
            cn = this.provider.getConnection();
            if(!exists()) 
            {
                createNewFile(cn);
            }
            if(exists())
            {   
				//JBOSS
                //cn.setAutoCommit(false);                                
                pstm = cn.prepareStatement("UPDATE " + this.provider.p_dbfs_table_file+"" +
                		" SET BINDATA=? WHERE ID=?");
                
				int fLen = Integer.MAX_VALUE;				
				if (boApplication.currentContext()!=null)
				{
					String dsName = boConfig.getApplicationConfig().getDataDataSourceClassName();
					if (dsName.equalsIgnoreCase(OracleDBM.SQLSERVER_IMPL )|| dsName.toUpperCase().indexOf("POSTGRE")>-1){
						fLen = is.available();
					}
				}
				pstm.setBinaryStream(1, is, fLen);                              
                pstm.setLong(2,this.fileId );
                pstm.executeUpdate();
            } 
            else 
            {
                throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_CREATE_FILE")+" " + this.fileName);    
            }
        }
        catch (IOException e)
        {
            throw createRuntimeException("getOutputStream()",e);
        }
        catch (SQLException e)
        {
            throw createRuntimeException("getOutputStream()",e);
        } 
        finally 
        {
            try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
            try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            try {if(cn!=null) cn.close();} catch (SQLException e) {};
        }
    }
    
    public OutputStream getOutputStream() throws iFilePermissionDenied
    {
    	throw new RuntimeException(MessageLocalizer.getMessage("THIS_METHOD_WAS_DEPRECATED_AND_IS_NOT_AVAILABLE_ANYMORE") );
    }

    public InputStream getInputStream() throws iFilePermissionDenied
    {
        InputStream result = null;
        Blob blob = null;
        ResultSet rslt = null;
        PreparedStatement pstm = null;
        Connection cn = null;
        boolean close = false;
        try
        {
            if(exists()) 
            {
                cn = this.provider.getConnection();                 
                pstm = cn.prepareStatement("SELECT BINDATA FROM "+provider.p_dbfs_table_file+" WHERE ID=?");
                pstm.setLong(1,this.fileId);
                rslt = pstm.executeQuery();
                InputStream is=null;
                if(rslt.next()) 
                {
                	String dsName = boConfig.getApplicationConfig().getDataDataSourceClassName();
					if (dsName.toUpperCase().indexOf("POSTGRE")>-1){
						 byte[] imgBytes = rslt.getBytes(1);
						 
						 is= new ByteArrayInputStream(imgBytes);
					}
					else
						is = rslt.getBlob(1).getBinaryStream();
					
					
                    result = new BasicInputStream(is,cn);                    
                }
                else
                {
                    close = true;
                }
                rslt.close();
                pstm.close();
            } 
            else 
            {
                throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_CREATE_FILE")+" " + this.fileName);    
            }
        }
        catch (SQLException e)
        {
            createRuntimeException("getInputStream()",e);
        }
        finally 
        {
            try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
            try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            try {if(close && cn!=null) cn.close();} catch (SQLException e) {};
        }
        return result;
    }    
    private boolean createNewFile(Connection cn) throws IOException, iFilePermissionDenied
    {
        boolean result = false;
        if(cn == null)
        {
            result = createNewFile();
        }
        else
        {
            result = createNewDataBaseFile(cn);
        }
        return result;
    }
    public boolean createNewFile()throws IOException, iFilePermissionDenied
    {
        boolean result = false;
        Connection cn = null;        
        try
        {            
            cn = this.provider.getConnection();
            result = createNewDataBaseFile(cn);
            if(cn!=null)
            {
                cn.close();
            }            
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }    
        return result;
    }
    private boolean createNewDataBaseFile(Connection cn) throws IOException, iFilePermissionDenied
    {
        boolean result = false;
        if(!exists())
        {
            boolean mycn = false;
            PreparedStatement pstm = null;
            String sql = "INSERT INTO dbfs_file (ID,FILENAME) VALUES (?,?)";
            try
            {
                if(cn == null)
                {
                    cn = this.provider.getConnection();
                    mycn = true;
                }                                                                         
                pstm = cn.prepareStatement(sql.toString());                 
                pstm.setLong(1,this.fileId);
                pstm.setString(2,this.fileName); 
                
                //pstm.setBlob(3,(Blob)null);                                
                pstm.executeUpdate();    
                pstm.close();
                this.loadFileInformation( cn );                                
                result = true;
                
            }
            catch (SQLException e)
            {
                throw createRuntimeException("createNewDataBaseFile()",e);
            }
            finally 
            {
                try
                {
                    if(pstm!=null) pstm.close();
                    if(mycn)
                    {
                        if(cn!=null) cn.close();    
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }            
        }
        return result;
    }    
    public String getURI()
    {
        return this.getAbsolutePath();
    }    
    public long  length()
    {
        return this.fileLength;
    }    
    public boolean  exists()
    {
        return this.exists;
    }    
    public String getName()
    {
        return this.fileName;
    }    
    public boolean  isFile()
    {
        return true;
    }    
    public String  getAbsolutePath()
    {
        String result = null;
        if(this.filePath != null && this.fileName != null)
        {
            result = this.filePath + this.fileName;
        }
        return result;
    }    
    public String   getPath()
    {
        return this.filePath;
    }    
    private RuntimeException createRuntimeException(String method,Exception e) 
    {
        return new RuntimeException(MessageLocalizer.getMessage("ERROR_EXECUTING")+" "+method+".\n"+e.getClass().getName()+"\n"+e.getMessage());
    }    
    public boolean  canRead()
    {
        return true;
    }
    public boolean  canWrite()
    {
        return true;
    }
    public String getParent()
    {
        throw new RuntimeException("getParent:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public iFile getParentFile()
    {
        throw new RuntimeException("getParentFile:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public boolean  isDirectory()
    {
        return false;
    }
    public long lastModified()
    {
        throw new RuntimeException("lastModified:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String[] list() throws iFilePermissionDenied
    {
        return new String[0];
    }
    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied
    {
        return new String[0];
    }
    public iFile[]  listFiles() throws iFilePermissionDenied
    {
    	return new iFile[0];
    }
    public iFile[]  listFiles(iFileFilter filter) throws iFilePermissionDenied
    {
    	return new iFile[0];
    }
    public boolean  mkdir() throws iFilePermissionDenied
    {
        return false;
    }
    public boolean  mkdirs() throws iFilePermissionDenied
    {
        return false;
    }
    public boolean  renameTo(iFile newfile) throws iFilePermissionDenied
    {
		boolean result = false;

		if (exists() && newfile != null) {
			String newFilename = newfile.getName();

			if (newFilename != null && !newFilename.trim().isEmpty()) {
				Connection cn = null;
				PreparedStatement pstm = null;

				try {
					cn = provider.getConnection();
					pstm = cn.prepareStatement("UPDATE " + this.provider.p_dbfs_table_file + " SET FILENAME = ? WHERE ID = ?");
					pstm.setString(1, newFilename);
					pstm.setLong(2, this.fileId);
					pstm.executeUpdate();

					this.fileName = newFilename;
					result = true;
				} catch (SQLException e) {
					throw createRuntimeException("renameTo()", e);
				} finally {
					try {
						if (pstm != null) {
							pstm.close();
						}

						if (cn != null) {
							cn.close();
						}
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}

		return result;
    }
    public boolean  setReadOnly() throws iFilePermissionDenied
    {
        throw new RuntimeException("setReadOnly:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public boolean checkIn() throws iFilePermissionDenied
    {
        return false;
    }
    public boolean checkOut() throws iFilePermissionDenied
    {
        return false;
    }
    public boolean isCheckedIn()
    {
        return false;
    }
    public boolean isCheckedOut()
    {
        return false;
    }
    public boolean isVersioned()
    {
        return false;
    }
    public boolean makeVersioned() throws iFilePermissionDenied
    {
        return false;
    }
    public void setDescription(String description)
    {
        throw new RuntimeException("setDescription:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String getDescription()
    {
        return "";
    }
    public void setAuthor(String author)
    {
        throw new RuntimeException("setAuthor:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String getAuthor()
    {
        return "";
    }
    public void setCategory(String author)
    {
        throw new RuntimeException("setCategory:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String getCategory()
    {
        throw new RuntimeException("getCategory:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public void setVersionUser(String user)
    {
        throw new RuntimeException("setVersionUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String getVersionUser()
    {
        return null;
    }
    public void setCheckOutUser(String user)
    {
        throw new RuntimeException("setCheckOutUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public String getCheckOutUser()
    {
        return null;
    }
    public long getVersion()
    {
        return -1;
    }
    
    public long getKey()
    {
        throw new RuntimeException("getKey:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    public iFile getCopy()
    {    
        iFile result = null;
        try
        {
            String filedir = "//" + IFILE_SERVICE_NAME + PATH_SEP + this.fileName;            
            iFileServer fs = new iFileServer();
            fs.mount();            
            result = fs.getFile(filedir);                                                                           
            InputStream input = this.getInputStream();
            result.setBinaryStream( input );
            input.close();
            
            loadFileInformation(null);        
        }
        catch (IOException e)
        {
            
        }
        catch (iFilePermissionDenied e)
        {
            
        }
        return result;
    }
   @Override
	public List<iMetadataItem> getAllMetadata() {
		//File system does not implement metadata
		return new LinkedList<iMetadataItem>();
	}
	@Override
	public iMetadataItem getMetadata(String name) {
		//File system does not implement metadata
		return null;
	}
	@Override
	public List<String> getMetadataTypes() {
		//File system does not implement metadata
		return new LinkedList<String>();
	}
	@Override
	public void removeMetadata(String metadataItemId) {
		//File system does not implement metadata
	}
	@Override
	public boolean close() {
		return false;
	}
	
	@Override
	public iMetadataItem getDefaultMetadata() {
		return null;
	}
	@Override
	public boolean addChild(iFile file) throws iFileException {
		throw new iFileException(MessageLocalizer.getMessage("NOT_IMPLEMENTED_IN_BASICIFILE"));
	}
	@Override
	public boolean save(EboContext ctx) throws iFileException {
		return false;
	}
	@Override
	public String getId() {
		return String.valueOf(fileId);
	}
	@Override
	public void addMetadata(iMetadataItem item) throws iFileException {
		
	}
	@Override
	public List<iMetadataItem> getMetadataByName(String name)
			throws iFileException {
		return null;
	}
	@Override
	public boolean inTransaction() {
		return false;
	}
	@Override
	public void updateFile(iFile newVal) {
	}
	@Override
	public String[] getFileToDeleteOnCommit() {
		return null;
	}
	@Override
	public String[] getFileToDeleteOnRollback() {
		return null;
	}
	@Override
	public void rollback(EboContext ctx) {
	} 
}