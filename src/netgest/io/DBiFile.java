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
import java.sql.Timestamp;

import java.util.Vector;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.UserTransaction;

import netgest.bo.data.Driver;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.utils.DataUtils;


public class DBiFile implements iFile  {

    public static final String IFILE_SERVICE_NAME = "ngtbo";
    private static final String PATH_SEP="/";

    private static final byte TYPE_DIRECTORY=0;
    private static final byte TYPE_FILE=1;
    
    private static final byte UNLOCK = 0;
    private static final byte LOCK = 1;

    private long p_dbfs_dir_id;
    private long p_dbfs_file_id;
    
    private long p_dbfs_file_key = -1;
    
    private String  p_absolutepath;
    private String  p_path;
    private String  p_uri;

    private String  p_filename;
    private long    p_lastModified=-1;
    private long    p_length=-1;
    private boolean p_isdir; 
    private boolean p_exists=false;
    private boolean p_parentexist=false;
    
    private DBiFileProvider p_dbfp;

    private String p_tablename;
    private String tableVersion;

    private String author; 
    private String versionUser;    
    private long activeVersion = -1;
    private long version = -1;
    private boolean lock = false;
    private String lockUser;
    
    private String currentUser;    
    
    public DBiFile(DBiFileProvider dbfp, String path,String uri) {
        init(dbfp,path,uri,null);
    }
    public DBiFile(DBiFileProvider dbfp, String path,String uri,Connection cn) {
        init(dbfp,path,uri,cn);
    }
    private void init(DBiFileProvider dbfp,String path,String uri,Connection cn) 
    {
        p_tablename = dbfp.p_dbfs_table_file;        
        tableVersion = p_tablename.split("_")[0] + "_version";
        p_dbfp = dbfp;
        p_absolutepath = path;
        p_uri = uri;
        
        parsePath(path);
        loadFile(cn);
    }
    
    public void loadFile(Connection cn) 
    {
        PreparedStatement pstm=null;
        ResultSet rslt=null;

        boolean mycn = false;
        try
        {
            if(cn == null)
            {
                cn = p_dbfp.getConnection();
                mycn = true;
            }
            p_dbfs_dir_id = getParentId(false,cn);
            
            //pstm=cn.prepareStatement("SELECT T.FILENAME,T.DATECREATE,T.DATEMODIFIED,T.ID,T.PARENT_ID,T.TYPE,DBMS_LOB.getLength(DBFS_VERSION.BINDATA) FROM "+p_tablename+" T , DBFS_VERSION WHERE T.PARENT_ID=? AND T.FILENAME=? AND T.ID=DBFS_VERSION.ID",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);            
            pstm=cn.prepareStatement("SELECT FILENAME,DATECREATE,DATEMODIFIED,ID,PARENT_ID,TYPE,USERCREATE,ACTIVE,STATUS,STATUSUSER,KEY FROM "
                                      +p_tablename+" WHERE PARENT_ID=? AND FILENAME=? ",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);            
            if(this.p_filename.equals("root"))            
                pstm.setLong(1,-1);
            else
                pstm.setLong(1,p_dbfs_dir_id);
            
            pstm.setString(2,p_filename);
            rslt = pstm.executeQuery();
            if(rslt.next()) 
            {
                Timestamp xx = rslt.getTimestamp(3);                
                p_lastModified=xx!=null?xx.getTime():0;    //Para retirar            
                p_dbfs_file_id=rslt.getLong(4);
                p_isdir=rslt.getByte(6)==TYPE_DIRECTORY;
                if(!p_isdir)
                {
                  author = rslt.getString(7);
                  activeVersion = rslt.getLong(8);
                  lock = rslt.getLong(9) == LOCK;
                  if(lock) lockUser = rslt.getString(10);
                }                                  
                p_dbfs_dir_id=p_dbfs_dir_id;
                p_dbfs_file_key = rslt.getLong(11);
                p_exists=true;
            }
            if(!p_isdir && p_exists)
            {
              getFileVersion(cn);
            }
            
        }
        catch (SQLException e)
        {
            throw createRuntimeException("loadFile()",e);
        } 
        finally 
        {
            try
            {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e)
            {
                
            }
            try
            {
                if(pstm != null) pstm.close();
            }
            catch (Exception e)
            {
                
            }
            try
            {
                if(mycn && cn!=null)  cn.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
    public void getFileVersion(Connection cn) 
    {
        PreparedStatement pstm=null;
        ResultSet rslt=null;
        boolean mycn = false;
        try
        {
            if(cn == null)
            {
                cn = p_dbfp.getConnection();
                mycn = true;
            }        
            pstm=cn.prepareStatement("SELECT DBMS_LOB.getLength(DBFS_VERSION.BINDATA),VERSION,VERSIONDATE,VERSIONUSER FROM " + tableVersion + " WHERE ID = ? AND VERSION = (SELECT MAX(VERSION) FROM " + tableVersion + " WHERE ID = ?)",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);            
            pstm.setLong(1,p_dbfs_file_id);
            pstm.setLong(2,p_dbfs_file_id);
                                                
            rslt = pstm.executeQuery();
            if(rslt.next()) 
            {
                p_length = rslt.getLong(1);                
                version = rslt.getLong(2);    
                Timestamp xx = rslt.getTimestamp(3);                
                p_lastModified=xx!=null?xx.getTime():0;                 
                versionUser = rslt.getString(4);
            }
        }
        catch (SQLException e)
        {
            throw createRuntimeException("getFileVersion()",e);
        } 
        finally 
        {
            try
            {
                if(rslt!=null) rslt.close();
            }
            catch (Exception e)
            {
                
            }
            try
            {
                if(pstm != null) pstm.close();
            }
            catch (Exception e)
            {
                
            }
            try
            {
                if(mycn && cn!=null)  cn.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }    
    public void parsePath(String path) 
    {
        String name;
        path = path.trim();
        if( path.length()==0 || path.equalsIgnoreCase( PATH_SEP ) ) 
        {
            name = "root";
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
        
        p_path=path;
        p_filename = name;
    }
    public long getParentId(boolean createdirs,Connection cn) 
    {
        long parent = -1;
        String relpath="";
        
        //Vector dirs=null;
        //if(p_path.length()>0) 
        //{
        //    dirs = tools.Split(p_path,PATH_SEP);
        //}
        String[] dirs = null;
        if(p_path.length()>0)
        {
            dirs= p_path.split( PATH_SEP );    
        }
        if(dirs!=null && dirs.length>0) 
        {
            //Connection cn=null;
            PreparedStatement pstm=null;
            ResultSet rslt=null;
            try
            {
                //cn = p_dbfp.getConnection();
                pstm = cn.prepareStatement("SELECT ID FROM "+p_tablename+" WHERE PARENT_ID=? AND FILENAME=?");
    
                for (int i = 0; i < dirs.length ; i++) 
                {
                    pstm.setLong(1,parent);
                    if(i==0) {
                        pstm.setObject(2,"root");
                    } else { 
                        pstm.setObject(2,dirs[i]);
                        relpath += "/"+dirs[i];
                    }
                        
                        
                    rslt = pstm.executeQuery();
                    if(rslt.next())
                    {
                        parent = rslt.getLong(1);
                        p_parentexist = true;
                    }
                    else if (parent==-1 && i==0)  
                    {
                        PreparedStatement pstm2=cn.prepareStatement("INSERT INTO "+p_tablename+" (ID,FILENAME,TYPE,PARENT_ID) VALUES (0,'root',0,-1)");
                        pstm2.executeUpdate();
                        pstm2.close();
                        p_parentexist=true;
                        parent=0;
                    } 
                    else 
                    {
                        p_parentexist=false;
                        if(createdirs) {
                            DBiFile x = new DBiFile(p_dbfp,relpath,iFileUtils.concatPath(p_uri,relpath,PATH_SEP),cn);
                            x.mkdir(); 
                            /*p_dbfs_file_id =DataUtils.getDBSequence(cn,""+p_tablename+"_SEQ","nextval"); 
                            pstmmkdir.setLong(1,p_dbfs_file_id);
                            pstmmkdir.setString(2,p_filename);
                            pstmmkdir.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
                            pstmmkdir.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
                            pstmmkdir.setLong(5,parent);
                            pstmmkdir.setLong(6,TYPE_DIRECTORY);
                            pstmmkdir.executeUpdate();*/
                            p_parentexist=true;
                            parent = x.p_dbfs_file_id;
                        } 
                        break;    
                    }
                    rslt.close();
                }
            }
            catch (SQLException e)
            {
                throw createRuntimeException("getParentId()",e);
            } 
            finally 
            {
                try
                {
                    if(rslt!=null) rslt.close();
                }
                catch (Exception e)
                {
                    
                }
                try
                {
                    if(pstm!= null) pstm.close();
                }
                catch (Exception e)
                {
                    
                }
//                try
//                {
//                    if(cn!=null) cn.close();
//                }
//                catch (Exception e)
//                {
//                    
//                }
            }
        }
        return parent;
    }
    
    public boolean mkdir() 
    {
        return _mkdir(false);
    }
    public boolean mkdirs()
    {
        boolean ret = _mkdir(true);
        if(ret) 
        {
            ret = ret && mkdir();
        }
        return ret;
    }
    public boolean exists() 
    {
        return p_exists;
    }
    public boolean _mkdir(boolean all) 
    {
        if(!this.exists()) 
        {
            Connection cn=null;
            PreparedStatement pstm=null;
            try
            {
                cn = p_dbfp.getConnection();

                long parent = getParentId(all,cn);

                pstm = cn.prepareStatement("INSERT INTO "+p_tablename+" "+
                                                          //"(ID,FILENAME,DATECREATE,DATEMODIFIED,PARENT_ID,TYPE)"+
                                                          "(ID,FILENAME,DATECREATE,PARENT_ID,TYPE,KEY)"+
                                                          " VALUES "+
                                                          "(?,?,?,?,?,?)");                        
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
            		                
                p_dbfs_file_id = DataUtils.getDataDBSequence(ctx, ""+p_tablename+"_SEQ", Driver.SEQUENCE_NEXTVAL);
                pstm.setLong(1,p_dbfs_file_id);
                pstm.setString(2,p_filename);
                pstm.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
                //pstm.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
                pstm.setLong(4,parent);
                pstm.setLong(5,TYPE_DIRECTORY);
                this.p_dbfs_file_key = DataUtils.getDataDBSequence(ctx, p_tablename + "_SEQKEY", Driver.SEQUENCE_NEXTVAL);
                pstm.setLong(6,p_dbfs_file_key);
                pstm.executeUpdate();
                if (newCtx)
                {
                	if (session!=null)session.closeSession();
                	if (ctx!=null)ctx.close();
                }
            }
            catch (SQLException e)
            {
                throw createRuntimeException("mkdir()",e);
            } 
            finally 
            {
                try
                {
                    if(pstm!=null) pstm.close();
                }
                catch (Exception e)
                {
                    
                }
                try
                {
                    if(cn!=null) cn.close();
                }
                catch (Exception e)
                {
                    
                }
            }
            p_exists=true;
            return true;
        }
        return false;
    }
    
    public RuntimeException createRuntimeException(String method,Exception e) 
    {
        return new RuntimeException("Error executing "+method+".\n"+e.getClass().getName()+"\n"+e.getMessage());
    }
    

    public boolean createNewFile() throws IOException, iFilePermissionDenied
    {
        boolean ok=false;
        if(!exists() && p_parentexist) 
        {
            Connection cn=null;
            PreparedStatement pstm = null;            
            try
            {
                cn = p_dbfp.getConnection();
                long dirid = getParentId(false,cn); 
                       
                pstm = cn.prepareStatement("INSERT INTO "+p_tablename+" "+
                                                          //"(ID,FILENAME,DATECREATE,DATEMODIFIED,PARENT_ID,TYPE,BINDATA)"+
                                                          "(ID,FILENAME,DATECREATE,PARENT_ID,TYPE,USERCREATE,STATUS,KEY)"+
                                                          " VALUES "+
                                                          //"(?,?,?,?,?,?,?)");
                                                          "(?,?,?,?,?,?,?,?)");
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
                p_dbfs_file_id =DataUtils.getDataDBSequence(ctx, ""+p_tablename+"_SEQ", Driver.SEQUENCE_NEXTVAL ); 
                pstm.setLong(1,p_dbfs_file_id);
                pstm.setString(2,p_filename);
                pstm.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
                //pstm.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
                pstm.setLong(4,p_dbfs_dir_id);
                pstm.setLong(5,TYPE_FILE);
                pstm.setString(6,author);
                pstm.setLong(7,UNLOCK);
                p_dbfs_file_key =DataUtils.getDataDBSequence(ctx, ""+p_tablename+"_SEQKEY" , Driver.SEQUENCE_NEXTVAL );
                pstm.setLong(8,p_dbfs_file_key);
                //pstm.setBlob(7,BLOB.empty_lob());
                
                createNewVersion();
                pstm.executeUpdate();
                pstm.close();
                
                this.loadFile( cn );
                
                cn.close();
                ok=true;
                if (newCtx)
                {
                	if (session!=null)session.closeSession();
                	if (ctx!=null)ctx.close();
                }
            }
            catch (SQLException e)
            {
                throw createRuntimeException("createNewFile()",e);
            }
            finally 
            {
                if(!ok) {
                    try
                    {
                        if(pstm!=null) pstm.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    try
                    {
                        if(cn!=null) cn.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            return true;
        }
        return false;
    }
    public boolean createNewVersion() throws IOException, iFilePermissionDenied
    {
        boolean ok=false;
            Connection cn=null;
            PreparedStatement pstm = null;
            try
            {
                cn = p_dbfp.getConnection();
                long dirid = getParentId(false,cn);                                           
        
                pstm = cn.prepareStatement("INSERT INTO "+ tableVersion +
                                                          " (ID,VERSION,VERSIONDATE,VERSIONUSER,BINDATA)"+                                                          
                                                          " VALUES "+
                                                          "(?,?,?,?,?)");                                                                          
                                                          
                this.version = getCurrentVersion(p_dbfs_file_id) + 1;// for update
                pstm.setLong(1,p_dbfs_file_id);                
                pstm.setLong(2,version);
                pstm.setTimestamp(3,new Timestamp(System.currentTimeMillis()));
                pstm.setString(4,versionUser);                
                pstm.setBlob(5,((Blob)null));                
               
                pstm.executeUpdate();                  
                pstm.close();
                
                this.loadFile( cn );
                
                cn.close();
                ok=true;
                
            }
            catch (SQLException e)
            {
                throw createRuntimeException("createNewVersion()",e);
            }
            finally 
            {
                if(!ok) {
                    try
                    {
                        if(pstm!=null) pstm.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    try
                    {
                        if(cn!=null) cn.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            return true;

    }
    public long getCurrentVersion(long id) throws IOException, iFilePermissionDenied
    {
            long currentVersion = -1;
            boolean ok = false;
            Connection cn = null;
            PreparedStatement pstm = null;
            ResultSet rslt = null;
            try
            {
                cn = p_dbfp.getConnection();
                long dirid = getParentId(false,cn);                                           

                pstm = cn.prepareStatement("SELECT MAX(VERSION) FROM " + tableVersion +" WHERE ID = ? ",ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY);
    
                pstm.setLong(1,id);
                
                rslt = pstm.executeQuery();
                if(rslt.next()) 
                {
                  currentVersion = rslt.getLong(1);
                 }

                pstm.close();                                                
                cn.close();
                ok=true;
                
            }
            catch (SQLException e)
            {
                throw createRuntimeException("getCurrentVersion()",e);
            }
            finally 
            {
                if(!ok) {
                    try
                    {
                        if(pstm!=null) pstm.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    try
                    {
                        if(cn!=null) cn.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            return currentVersion;
    }    
    public boolean delete() throws iFilePermissionDenied
    {
        if(exists()) 
        {
            Connection cn=null,cn2=null;
            PreparedStatement pstm=null,pstm2=null;
            ResultSet rslt=null,rslt2=null;
            boolean ok = true;
            
            try
            {
                cn = p_dbfp.getConnection();
                pstm2 = cn.prepareStatement("SELECT ID FROM "+p_tablename+" WHERE PARENT_ID=?");
                pstm2.setLong(1,p_dbfs_file_id);
                rslt2 = pstm2.executeQuery();
                if(!rslt2.next()) 
                {
                    ok = false;
                    pstm = cn.prepareStatement("DELETE "+p_tablename+" "+
                                                              " WHERE "+
                                                              "ID=?");
                    pstm.setLong(1,p_dbfs_file_id);
                    pstm.executeUpdate();
                    pstm.close();

                    pstm = cn.prepareStatement("DELETE "+ tableVersion +" "+
                                                              " WHERE "+
                                                              "ID=?");
                    pstm.setLong(1,p_dbfs_file_id);
                    pstm.executeUpdate();
                    pstm.close();

                    cn.close();
                    
                    p_exists=false;
                    p_length=0;
                    p_lastModified=0;
                    ok = true;
                    
                }
            }
            catch (SQLException e)
            {
                throw createRuntimeException("delete()",e);
            }
            finally 
            {
                try {if(rslt2!=null) rslt2.close();} catch (SQLException e){}
                try {if(pstm2!=null) pstm2.close();} catch (SQLException e){}
                if(!ok) 
                {
                    try {if(pstm != null) pstm.close();} catch (SQLException e){}
                    try {if(rslt != null) rslt.close();} catch (SQLException e){}
                    
                }
                try {if(cn!=null) cn.close();} catch (SQLException e){}
                
            }
            return true;
        }
        return false;
    }

    public String getAbsolutePath()
    {
        return p_path+PATH_SEP+p_filename;
    }

    public String getName()
    {
        return p_filename;
    }

    public String getParent()
    {
        return p_path;
    }

    public iFile getParentFile()
    {
        return new DBiFile(p_dbfp,p_path,iFileUtils.getParentPath(p_uri,PATH_SEP));
    }

    public String getPath()
    {
        return p_path;
    }

    public boolean isDirectory()
    {
        return exists() && p_isdir;
    }

    public boolean isFile()
    {
        return exists() && !p_isdir;
    }

    public long lastModified()
    {
        return p_lastModified;
    }

    public long length()
    {
        return p_length;
    }

    public String[] list()  throws iFilePermissionDenied
    {
        return _list(null);
    }
    private String[] _list(iFilenameFilter filter) throws iFilePermissionDenied
    {
        Vector ret = new Vector();
        if(exists()) 
        {
            Connection cn=null;
            PreparedStatement pstm=null;
            ResultSet rslt=null;
            try
            {
                cn          = p_dbfp.getConnection();
                pstm = cn.prepareStatement("SELECT FILENAME FROM "+p_tablename+" WHERE PARENT_ID=?");
                pstm.setLong(1,p_dbfs_file_id);
                rslt = pstm.executeQuery();
                while(rslt.next()) 
                {
                    String name = rslt.getString(1);
                    if(filter==null || filter.accept(name)) {
                        ret.add(name);
                    }
                }
            }
            catch (SQLException e)
            {
                throw createRuntimeException("list()",e);
            } 
            finally 
            {
                
                try {if(rslt != null) rslt.close();} catch (SQLException e) {};
                try {if(pstm != null) pstm.close();} catch (SQLException e) {};
                try {if(cn!=null ) cn.close();} catch (SQLException e) {};
            }
        }
        String[] _ret = (String[])ret.toArray(new String[ret.size()]);
        return _ret;
    }

    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied
    {
        return _list(filter);
    }

    public iFile[] listFiles(iFileFilter filter) throws iFilePermissionDenied
    {
        String[] files = _list(null);
        Vector ret=new Vector();
        for (int i = 0;files != null && i < files.length; i++) 
        {
            
            iFile xifile =  new DBiFile(p_dbfp,iFileUtils.concatPath(p_absolutepath,files[i],PATH_SEP),iFileUtils.concatPath(p_uri,files[i],PATH_SEP));
            if(filter == null || filter.accept(xifile))
                ret.add(xifile);
        }
        iFile _ret[] = new iFile[ret.size()];
        _ret = (iFile[])ret.toArray(_ret);
        return _ret;
    }
    public iFile[] listFiles() throws iFilePermissionDenied
    {
        return listFiles(null);
    }

    public boolean renameTo(iFile newfile) throws iFilePermissionDenied
    {
        return false;
    }

    public boolean setReadOnly() throws iFilePermissionDenied
    {
        return false;
    }

    public OutputStream getOutputStream() throws iFilePermissionDenied
    {
        if(this.isDirectory())
            throw new RuntimeException("File "+p_uri+" is directory.");
        Blob blob;
        OutputStream os=null;
        ResultSet rslt=null;
        PreparedStatement pstm=null;
        boolean checkIn = true;
        Connection cn = null;
        try
        {
            if(!exists()) 
            {
                createNewFile();
            }
            else
            {
               createNewVersion();
            }
            if(exists() && checkIn) {            
                cn = p_dbfp.getConnection();
				//JBOSS
                //cn.setAutoCommit(false);
                //pstm = cn.prepareStatement("SELECT BINDATA FROM "+p_tablename+" WHERE ID=? FOR UPDATE");
                pstm = cn.prepareStatement("SELECT BINDATA FROM "+tableVersion+" WHERE ID=? AND VERSION=? FOR UPDATE WAIT 300");
                pstm.setLong(1,p_dbfs_file_id);
                pstm.setLong(2,version);
                rslt = pstm.executeQuery();
                if(rslt.next()) 
                {
                    blob = (Blob)rslt.getBlob(1);
                    os = new DBiOutputStream(this,blob,cn);
                }
            } 
            else 
            {
                throw new RuntimeException("Cannot create file "+p_absolutepath);    
            }
        }
        catch (IOException e)
        {
            createRuntimeException("getOutputStream()",e);
        }
        catch (SQLException e)
        {
            createRuntimeException("getOutputStream()",e);
        } 
        finally 
        {
            try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
            try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
        }
        return os;
    }

    public InputStream getInputStream() throws iFilePermissionDenied
    {
        if(this.isDirectory())
            throw new RuntimeException("File "+p_uri+" is directory.");
        Blob blob;
        InputStream is = null;

        ResultSet rslt = null;
        PreparedStatement pstm = null;
        Connection cn = null;
        boolean close = false;

        try
        {
            if(exists()) {
                cn = p_dbfp.getConnection(); 
                //pstm = cn.prepareStatement("SELECT BINDATA FROM "+p_tablename+" WHERE ID=?");
                pstm = cn.prepareStatement("SELECT BINDATA FROM "+tableVersion+" WHERE ID=? AND VERSION=?");
                pstm.setLong(1,p_dbfs_file_id);
                pstm.setLong(2,version);
                rslt = pstm.executeQuery();
                if(rslt.next()) 
                {
                    blob = rslt.getBlob(1);
                    is = new DBiInputStream(blob.getBinaryStream(),cn);
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
                throw new RuntimeException("Cannot create file "+p_absolutepath);    
            }
        }
        catch (SQLException e)
        {
            createRuntimeException("getOutputStream()",e);
        }
        finally 
        {
            try {if(rslt!=null) rslt.close();} catch (SQLException e) {};
            try {if(pstm!=null) pstm.close();} catch (SQLException e) {};
            try {if(close && cn!=null) cn.close();} catch (SQLException e) {};
        }
        return is;
    }

    public String getURI()
    {
        return p_uri;
    }
    public void setVersionUser(String versionUser)  {          
        this.versionUser = versionUser;
    }    
    public String getVersionUser() {          
        return this.versionUser;
    }        
    public boolean checkIn() throws iFilePermissionDenied {          
      if(lock && versionUser.equals(lockUser))
      {        
          try
          {
            lockFile(UNLOCK);
            return true;
          }
          catch (IOException e)
          {
            createRuntimeException("checkIn()",e);
          }       
      }
      return false; 
    }
    public boolean checkOut() throws iFilePermissionDenied { 
      if(!lock)
      {
          try
          {
            lockFile(LOCK);  
            return true;
          }
          catch (IOException e)
          {
            createRuntimeException("checkOut()",e);
          }
      }      
      return false; 
    }    
    
    private boolean lockFile(byte statusLock) throws IOException, iFilePermissionDenied
    {
            boolean ok = false;
            UserTransaction ut = null;
            Connection cn = null;
            InitialContext ic = null;
            PreparedStatement pstm = null;
            try
            {
                cn = p_dbfp.getConnection();
                ic = new InitialContext();
                ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");  
                ut.begin();

                pstm = cn.prepareStatement("UPDATE "+ p_tablename + " SET STATUS = ? , STATUSUSER = ? WHERE ID = ? ");
                
                                                         
                pstm.setLong(1,statusLock);                
                if(statusLock == LOCK )
                {
                  pstm.setString(2,lockUser);
                }
                else
                {
                  pstm.setString(2,"");  
                }
                
                pstm.setLong(3,p_dbfs_file_id);
               
                pstm.executeUpdate();                                                                
                //this.loadFile( cn );                
                ut.commit();
                pstm.close();
                cn.close(); 
                if(statusLock == LOCK)
                {
                  lock = true;
                  lockUser = versionUser;
                }
                else
                {
                  lock = false;
                  lockUser = "";                
                }
                ok = true; 
            }
            catch (Exception e)
            {
                throw createRuntimeException("makeLock()",e);
            }
            finally 
            {
                if(!ok) {
                    try
                    {
                        if(pstm!=null) pstm.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    try
                    {
                        if(cn!=null) cn.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            return true;
    }    

    public void setCurrentUser(String user)
    {
        this.currentUser = user;
    }
    public String getCurrentUser()
    {
        return currentUser;
    }
    public void setCheckOutUser(String lockUser)
    {
        this.lockUser = lockUser;
    }
    public String getCheckOutUser()
    {
        return lockUser;
    }
    public long getVersion()
    {
        return version;
    }   
    public void setAuthor(String author) {
        this.author = author;
    }
    public String   getAuthor() {
        return author;
    }    
    public boolean isCheckedIn() { 
      return !lock; 
    }
    public boolean isCheckedOut() { 
      return lock;
    }    
    public long getKey()
    {
        return this.p_dbfs_file_key;
    }
    
    public iFile getCopy()
    {
        throw new RuntimeException("getCopy:Not Impelemented");
    }
    public boolean canRead() { return true; }
    public boolean canWrite() { return true; }
    public boolean isVersioned() { return false; }
    public boolean makeVersioned() throws iFilePermissionDenied { return false; }
    public void     setDescription(String description) {throw new RuntimeException("setDescription:Not Impelemented");}
    public String   getDescription() {throw new RuntimeException("getDescription:Not Impelemented");}
    public void     setCategory(String author) {throw new RuntimeException("setCategory:Not Impelemented");}
    public String   getCategory() {throw new RuntimeException("getCategory:Not Impelemented");}
    
	public void setBinaryStream(InputStream is) throws iFilePermissionDenied,
			iFilePermissionDenied {
		throw new RuntimeException("Not implemented after version of mysql!!!");
		
	}    
}