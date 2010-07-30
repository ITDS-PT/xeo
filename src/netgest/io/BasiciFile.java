/*Enconding=UTF-8*/
package netgest.io;
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

import netgest.bo.data.Driver;
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
        StringBuffer sql = new StringBuffer("SELECT ID,FILENAME,LENGTH(BINDATA) FROM ");
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
                pstm.setBinaryStream( 1, is, Integer.MAX_VALUE );
                pstm.setLong(2,this.fileId );
                pstm.executeUpdate();
            } 
            else 
            {
                throw new RuntimeException("Cannot create file " + this.fileName);    
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
    	throw new RuntimeException( "This method was deprecated and is not available anymore!" );
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
                if(rslt.next()) 
                {
                    blob = rslt.getBlob(1);
                    result = new BasicInputStream(blob.getBinaryStream(),cn);
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
                throw new RuntimeException("Cannot create file " + this.fileName);    
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
        return new RuntimeException("Error executing "+method+".\n"+e.getClass().getName()+"\n"+e.getMessage());
    }    
    public boolean  canRead()
    {
        throw new RuntimeException("canRead:Not Impelemented");
    }
    public boolean  canWrite()
    {
        throw new RuntimeException("canWrite:Not Impelemented");
    }
    public String getParent()
    {
        throw new RuntimeException("getParent:Not Impelemented");
    }
    public iFile getParentFile()
    {
        throw new RuntimeException("getParentFile:Not Impelemented");
    }
    public boolean  isDirectory()
    {
        throw new RuntimeException("isDirectory:Not Impelemented");
    }
    public long lastModified()
    {
        throw new RuntimeException("lastModified:Not Impelemented");
    }
    public String[] list() throws iFilePermissionDenied
    {
        throw new RuntimeException("list:Not Impelemented");
    }
    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied
    {
        throw new RuntimeException("list:Not Impelemented");
    }
    public iFile[]  listFiles() throws iFilePermissionDenied
    {
        throw new RuntimeException("listFiles:Not Impelemented");
    }
    public iFile[]  listFiles(iFileFilter filter) throws iFilePermissionDenied
    {
        throw new RuntimeException("listFiles:Not Impelemented");
    }
    public boolean  mkdir() throws iFilePermissionDenied
    {
        throw new RuntimeException("mkdir:Not Impelemented");
    }
    public boolean  mkdirs() throws iFilePermissionDenied
    {
        throw new RuntimeException("mkdirs:Not Impelemented");
    }
    public boolean  renameTo(iFile newfile) throws iFilePermissionDenied
    {
        throw new RuntimeException("renameTo:Not Impelemented");
    }
    public boolean  setReadOnly() throws iFilePermissionDenied
    {
        throw new RuntimeException("setReadOnly:Not Impelemented");
    }
    public boolean checkIn() throws iFilePermissionDenied
    {
        throw new RuntimeException("checkIn:Not Impelemented");
    }
    public boolean checkOut() throws iFilePermissionDenied
    {
        throw new RuntimeException("checkOut:Not Impelemented");
    }
    public boolean isCheckedIn()
    {
        throw new RuntimeException("isCheckedIn:Not Impelemented");
    }
    public boolean isCheckedOut()
    {
        throw new RuntimeException("isCheckedOut:Not Impelemented");
    }
    public boolean isVersioned()
    {
        throw new RuntimeException("isVersioned:Not Impelemented");
    }
    public boolean makeVersioned() throws iFilePermissionDenied
    {
        throw new RuntimeException("makeVersioned:Not Impelemented");
    }
    public void setDescription(String description)
    {
        throw new RuntimeException("setDescription:Not Impelemented");
    }
    public String getDescription()
    {
        throw new RuntimeException("getDescription:Not Impelemented");
    }
    public void setAuthor(String author)
    {
        throw new RuntimeException("setAuthor:Not Impelemented");
    }
    public String getAuthor()
    {
        throw new RuntimeException("getAuthor:Not Impelemented");
    }
    public void setCategory(String author)
    {
        throw new RuntimeException("setCategory:Not Impelemented");
    }
    public String getCategory()
    {
        throw new RuntimeException("getCategory:Not Impelemented");
    }
    public void setVersionUser(String user)
    {
        throw new RuntimeException("setVersionUser:Not Impelemented");
    }
    public String getVersionUser()
    {
        throw new RuntimeException("getVersionUser:Not Impelemented");
    }
    public void setCheckOutUser(String user)
    {
        throw new RuntimeException("setCheckOutUser:Not Impelemented");
    }
    public String getCheckOutUser()
    {
        throw new RuntimeException("getCheckOutUser:Not Impelemented");
    }
    public long getVersion()
    {
        throw new RuntimeException("getVersion:Not Impelemented");
    }
    
    public long getKey()
    {
        throw new RuntimeException("getKey:Not Impelemented");
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
		throw new iFileException("Not implemented in BasiciFile");
	}
	@Override
	public boolean save(EboContext ctx) throws iFileException {
		return false;
	}
	@Override
	public String getId() {
		return null;
	}
	@Override
	public void addMetadata(iMetadataItem item) throws iFileException {
		
	}
	@Override
	public List<iMetadataItem> getMetadataByName(String name)
			throws iFileException {
		return null;
	} 
}