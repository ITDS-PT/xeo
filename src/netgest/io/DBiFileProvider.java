/*Enconding=UTF-8*/
package netgest.io;
import java.util.Hashtable;
import java.sql.*;
import javax.sql.DataSource;
import javax.naming.*;
import netgest.bo.system.Logger;

public class DBiFileProvider extends iFileProvider  {
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.server.mail.MailUtil");

    private String p_connectionstring;
    String p_dbfs_table_dir;
    String p_dbfs_table_file;
    String p_servicename;
    private boolean connected = false;

    private Connection cn;
    
    public void setParameters(Hashtable ht) {}

    public void close() {
        try { if (cn!=null && !cn.isClosed()) cn.close(); } catch (Exception e) { }
        // throw(new NullPointerException("Este metodo não pode ser utilizado."));
        ;
    }

    public boolean open(String name,String connectstring) {
        p_connectionstring=connectstring.substring(0,connectstring.indexOf("$"));
        //p_dbfs_table_dir=connectstring.substring(p_connectionstring.length()+1,connectstring.lastIndexOf("$"));
        p_dbfs_table_file=connectstring.substring(connectstring.lastIndexOf("$")+1);
        p_servicename=name;
        return true;
    }

    public boolean open(String name,String username,String password,String connectstring) {
        p_connectionstring=connectstring.substring(0,connectstring.indexOf("$"));
        //p_dbfs_table_dir=connectstring.substring(p_connectionstring.length()+1,connectstring.lastIndexOf("$"));
        p_dbfs_table_file=connectstring.substring(connectstring.lastIndexOf("$")+1);
        p_servicename=name;
        return true;
    }

    public iFile[] listRoots() {
        return null;
    }

    public iFile getFile(String pathname,String uri) {
        // return (new DBiFile(cn,pathname));
        // return (new DBiFile(this,p_dbfs_table_dir,p_dbfs_table_file,pathname));
        return (new DBiFile(this, pathname,uri));
    }

    Connection getConnection() {
        try {
            InitialContext ic=new InitialContext();
            DataSource ds=(DataSource)ic.lookup(p_connectionstring);
            cn=ds.getConnection();
			//JBOSS
            //cn.setAutoCommit(false);
        } catch (Exception e) {
            logger.severe("", e);
            cn=null;
        }
        return cn;            
    }

    public boolean supportsVersioning() {
        return false;
    }

}