/*Enconding=UTF-8*/
package netgest.system.spy;

import java.io.CharArrayWriter;
import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;
import java.util.concurrent.Executor;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class XEOSpyConnection implements java.sql.Connection {

	public static Vector<XEOSpyConnection> connections = new Vector<XEOSpyConnection>();
	
    //logger
    private static Logger logger = Logger.getLogger("netgest.system.spy.XEOSpyConnection");

    protected static int counter=0;
    protected int id = counter++;
    protected Connection passthru;

	private ArrayList psList = new ArrayList();
    private ArrayList stList = new ArrayList();
    private ArrayList callabList = new ArrayList();
    private boolean close = false;
    private String createStack; 

    public XEOSpyConnection(Connection conn) throws SQLException 
    {
    	connections.add( this );
    	Throwable e = new Throwable();
    	CharArrayWriter cw = new CharArrayWriter();
    	PrintWriter pw = new PrintWriter(cw);
    	e.printStackTrace( pw );
    	pw.close();
    	cw.close();
    	this.createStack = cw.toString();
        this.passthru = conn;
    }
    
    public String getStackStrace() {
    	return this.createStack;
    }

    public void setReadOnly(boolean p0) throws SQLException 
    {
        passthru.setReadOnly(p0);
    }

    public void close() throws SQLException 
    {
        if( passthru != null ) {
	        passthru.close();
	        Iterator it=psList.iterator();
	        XEOSpyPreparedStatement xeoPs=null;
	        while(it.hasNext() )
	        {
	            xeoPs=(XEOSpyPreparedStatement)it.next();
	            if( !xeoPs.isClosed() ){
	                xeoPs.close();                
	                logger.warn("------------------------------------------------");
	                logger.warn(LoggerMessageLocalizer.getMessage("THE_PREPAREDSTATEMENT_WAS_NOT_CLOSED")+": " + xeoPs.getQuery() );
	                xeoPs.printStackTrace();
	                logger.warn("------------------------------------------------");
	            }
	        }
	        
	        XEOSpyStatement xeoSt=null;
	        it=stList.iterator();
	        while(it.hasNext() ){
	            xeoSt=(XEOSpyStatement)it.next();
	            if( !xeoSt.isClosed() ){
	                xeoSt.close();                
	                logger.warn("------------------------------------------------");
	                logger.warn(LoggerMessageLocalizer.getMessage("THE_STATEMENT_WAS_NOT_CLOSED")+": " + xeoSt.getQuery() );
	                xeoSt.printStackTrace();
	                logger.warn("------------------------------------------------");
	            }
	        }
	        
	        XEOSpyCallableStatement xeoCa=null;
	        it=callabList.iterator();
	        while(it.hasNext() ){
	            xeoCa=(XEOSpyCallableStatement)it.next();
	            if( !xeoCa.isClosed() ){
	                xeoCa.close();                
	                logger.warn("------------------------------------------------");
	                logger.warn(LoggerMessageLocalizer.getMessage("THE_CALLABLESTATEMENT_WAS_NOT_CLOSED")+": " + xeoCa.getQuery() );
	                xeoCa.printStackTrace();
	                logger.warn("------------------------------------------------");
	            }
	        }
	        close = true;
	        connections.remove( this );
	        passthru = null;
	    }
    }

    public int getId() {
        return this.id;
    }

    public boolean isClosed() throws SQLException {
    	return close;
//        return(passthru.isClosed());
    }

    public boolean isReadOnly() throws SQLException {
        return(passthru.isReadOnly());
    }

    public Statement createStatement() throws SQLException {
        Statement statement = new XEOSpyStatement(passthru.createStatement(), passthru);
        stList.add(statement);
        return(statement);
    }

    public Statement createStatement(int p0, int p1) throws SQLException {
        Statement statement = new XEOSpyStatement(passthru.createStatement(p0,p1), this);
        stList.add(statement);
        return(statement);
    }

    public PreparedStatement prepareStatement(String p0) throws SQLException {
        PreparedStatement ps = new XEOSpyPreparedStatement(passthru.prepareStatement(p0), this, p0);
        psList.add(ps);
        return ps;
    }

    public PreparedStatement prepareStatement(String p0, int p1, int p2) throws SQLException {
        PreparedStatement ps = new XEOSpyPreparedStatement(passthru.prepareStatement(p0,p1,p2), this, p0);
        psList.add(ps);
        return ps;
    }

    public CallableStatement prepareCall(String p0) throws SQLException {
        CallableStatement callab = new XEOSpyCallableStatement(passthru.prepareCall(p0), this, p0);
        callabList.add(callab);
        return(callab);
    }

    public CallableStatement prepareCall(String p0, int p1, int p2) throws SQLException {
        CallableStatement callab = new XEOSpyCallableStatement(passthru.prepareCall(p0,p1,p2), this, p0);
        callabList.add(callab);
        return(callab);
    }

    public String nativeSQL(String p0) throws SQLException {
        return(passthru.nativeSQL(p0));
    }

    public void setAutoCommit(boolean p0) throws SQLException {
        passthru.setAutoCommit(p0);
    }

    public boolean getAutoCommit() throws SQLException {
        return(passthru.getAutoCommit());
    }

    public void commit() throws SQLException {
        passthru.commit();
    }

    public void rollback() throws SQLException {
        passthru.rollback();
    }

    public DatabaseMetaData getMetaData() throws SQLException {
        return passthru.getMetaData();
    }

    public void setCatalog(String p0) throws SQLException {
        passthru.setCatalog(p0);
    }

    public String getCatalog() throws SQLException {
        return(passthru.getCatalog());
    }

    public void setTransactionIsolation(int p0) throws SQLException {
        passthru.setTransactionIsolation(p0);
    }

    public int getTransactionIsolation() throws SQLException {
        return(passthru.getTransactionIsolation());
    }

    public SQLWarning getWarnings() throws SQLException {
        return(passthru.getWarnings());
    }

    public void clearWarnings() throws SQLException {
        passthru.clearWarnings();
    }

    public java.util.Map getTypeMap() throws SQLException {
        return(passthru.getTypeMap());
    }

    public void setTypeMap(java.util.Map p0) throws SQLException {
        passthru.setTypeMap(p0);
    }

    // Since JDK 1.4
    public void setHoldability(int p0) throws SQLException {
        passthru.setHoldability(p0);
    }

    // Since JDK 1.4
    public int getHoldability() throws SQLException {
        return(passthru.getHoldability());
    }

    // Since JDK 1.4
    public Savepoint setSavepoint() throws SQLException {
        return(passthru.setSavepoint());
    }

    // Since JDK 1.4
    public Savepoint setSavepoint(String p0) throws SQLException {
        return(passthru.setSavepoint(p0));
    }

    // Since JDK 1.4
    public void rollback(Savepoint p0) throws SQLException {
        passthru.rollback(p0);
    }

    // Since JDK 1.4
    public void releaseSavepoint(Savepoint p0) throws SQLException {
        passthru.releaseSavepoint(p0);
    }

    // Since JDK 1.4
    public Statement createStatement(int p0, int p1, int p2) throws SQLException {
        Statement statement = new XEOSpyStatement(passthru.createStatement(p0,p1,p2), this);
        stList.add(statement);
        return(statement);
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1, int p2, int p3) throws SQLException {
        XEOSpyPreparedStatement statement = new XEOSpyPreparedStatement(passthru.prepareStatement(p0, p1, p2, p3), this, p0);
        psList.add(statement);
        return(statement);
    }

    // Since JDK 1.4
    public CallableStatement prepareCall(String p0, int p1, int p2, int p3) throws SQLException {
        XEOSpyCallableStatement callabStatement = new XEOSpyCallableStatement(passthru.prepareCall(p0, p1, p2, p3), this, p0);
        callabList.add(callabStatement);
        return(callabStatement);
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1) throws SQLException {        
        XEOSpyPreparedStatement statement = new XEOSpyPreparedStatement(passthru.prepareStatement(p0, p1), this, p0);
        psList.add(statement);
        return(statement);
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, int p1[]) throws SQLException {
        XEOSpyPreparedStatement statement = new XEOSpyPreparedStatement(passthru.prepareStatement(p0, p1), this, p0);
        psList.add(statement);
        return(statement);
    }

    // Since JDK 1.4
    public PreparedStatement prepareStatement(String p0, String p1[]) throws SQLException {
        XEOSpyPreparedStatement statement = new XEOSpyPreparedStatement(passthru.prepareStatement(p0, p1), this, p0);
        psList.add(statement);
        return(statement);
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.Connection)
     * @return the wrapped JDBC object
     */
    public Connection getJDBC() {
	Connection wrapped = (passthru instanceof XEOSpyConnection) ?
	    ((XEOSpyConnection) passthru).getJDBC() :
	    passthru;

	return wrapped;
    }

    public Array createArrayOf(String typeName, Object[] elements)
	throws SQLException {
return passthru.createArrayOf(typeName, elements);
}

public Blob createBlob() throws SQLException {
return passthru.createBlob();
}

public Clob createClob() throws SQLException {
return passthru.createClob();
}

public NClob createNClob() throws SQLException {
return passthru.createNClob();
}

public SQLXML createSQLXML() throws SQLException {
return passthru.createSQLXML();
}

public Struct createStruct(String typeName, Object[] attributes)
	throws SQLException {
return passthru.createStruct(typeName, attributes);
}

public Properties getClientInfo() throws SQLException {
return passthru.getClientInfo();
}

public String getClientInfo(String name) throws SQLException {
return passthru.getClientInfo(name);
}

public boolean isValid(int timeout) throws SQLException {
return passthru.isValid(timeout);
}

public boolean isWrapperFor(Class<?> iface) throws SQLException {
return passthru.isWrapperFor(iface);
}

public void setClientInfo(Properties properties)
	throws SQLClientInfoException {
passthru.setClientInfo(properties);
}

public void setClientInfo(String name, String value)
	throws SQLClientInfoException {
passthru.setClientInfo(name, value);
}

public <T> T unwrap(Class<T> iface) throws SQLException {
return passthru.unwrap(iface);
}

@Override
public void setSchema(String schema) throws SQLException {
	passthru.setSchema(schema);
}

@Override
public String getSchema() throws SQLException {
	return passthru.getSchema();
}

@Override
public void abort(Executor executor) throws SQLException {
	passthru.abort(executor);
}

@Override
public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
	passthru.setNetworkTimeout(executor, milliseconds);
}

@Override
public int getNetworkTimeout() throws SQLException {
	return passthru.getNetworkTimeout();
}

}
