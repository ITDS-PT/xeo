/*Enconding=UTF-8*/
package netgest.io;
import java.sql.Connection;

import java.util.Hashtable;

import javax.naming.InitialContext;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

public class BasiciFileProvider extends iFileProvider  {
    //logger
    private static Logger logger = Logger.getLogger("netgest.io.BasiciFileProvider");

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
        p_dbfs_table_file=connectstring.substring(connectstring.lastIndexOf("$")+1);
        p_servicename=name;
        return true;
    }

    public boolean open(String name,String username,String password,String connectstring) {
        p_connectionstring=connectstring.substring(0,connectstring.indexOf("$"));
        p_dbfs_table_file=connectstring.substring(connectstring.lastIndexOf("$")+1);
        p_servicename=name;
        return true;
    }

    public iFile[] listRoots() {
        return null;
    }

    public iFile getFile(String pathname,String uri) {
        return (new BasiciFile(this, uri));
    }

    Connection getConnection() {
        try {
            InitialContext ic=new InitialContext();
            DataSource ds=(DataSource)ic.lookup(p_connectionstring);
            cn=ds.getConnection();
			//JBOSS
            //cn.setAutoCommit(false);
        } catch (Exception e) {
            logger.error("", e);
            cn=null;
        }
        return cn;            
    }

    public boolean supportsVersioning() {
        return false;
    }

}