/*Enconding=UTF-8*/
package netgest.system.spy;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.log4j.Logger;

public class XEOSpyConnection implements java.sql.Connection {

    //logger
    private static Logger logger = Logger.getLogger("netgest.system.spy.XEOSpyConnection");

    protected static int counter=0;
    protected int id = counter++;
    protected Connection passthru;
    private ArrayList psList = new ArrayList();
    private ArrayList stList = new ArrayList();
    private ArrayList callabList = new ArrayList();
    private boolean close = false;

    public XEOSpyConnection(Connection conn) throws SQLException 
    {
        this.passthru = conn;
    }

    public void setReadOnly(boolean p0) throws SQLException 
    {
        passthru.setReadOnly(p0);
    }

    public void close() throws SQLException 
    {
        passthru.close();
        Iterator it=psList.iterator();
        XEOSpyPreparedStatement xeoPs=null;
        while(it.hasNext() )
        {
            xeoPs=(XEOSpyPreparedStatement)it.next();
            if( !xeoPs.isClosed() ){
                xeoPs.close();                
                logger.debug("------------------------------------------------");
                logger.debug("O PreparedStatement não foi fechado: " + xeoPs.getQuery() );
                xeoPs.printStackTrace();
                logger.debug("------------------------------------------------");
            }
        }
        
        XEOSpyStatement xeoSt=null;
        it=stList.iterator();
        while(it.hasNext() ){
            xeoSt=(XEOSpyStatement)it.next();
            if( !xeoSt.isClosed() ){
                xeoSt.close();                
                logger.debug("------------------------------------------------");
                logger.debug("O Statement não foi fechado: " + xeoSt.getQuery() );
                xeoSt.printStackTrace();
                logger.debug("------------------------------------------------");
            }
        }
        
        XEOSpyCallableStatement xeoCa=null;
        it=callabList.iterator();
        while(it.hasNext() ){
            xeoCa=(XEOSpyCallableStatement)it.next();
            if( !xeoCa.isClosed() ){
                xeoCa.close();                
                logger.debug("------------------------------------------------");
                logger.debug("O CallableStatement não foi fechado: " + xeoCa.getQuery() );
                xeoCa.printStackTrace();
                logger.debug("------------------------------------------------");
            }
        }
        
        close = true;
        passthru.close();    
    }

    public int getId() {
        return this.id;
    }

    public boolean isClosed() throws SQLException {
        return(passthru.isClosed());
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
    
    // Since JDK 1.6
    public Array createArrayOf(String typeName,
                               Object[] elements)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Blob createBlob()  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Clob createClob()  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public NClob createNClob() {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public SQLXML createSQLXML()  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Struct createStruct(String typeName,
                               Object[] attributes)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Properties getClientInfo() {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public String getClientInfo(String name)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public boolean isValid(int timeout)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public boolean isWrapperFor(Class iface)  {
        throw new RuntimeException("Not Implemented");
    }
    
    
    // Since JDK 1.6
    public void setClientInfo(Properties properties)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClientInfo(String name,
                              String value) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Object unwrap(Class iface)  {
        throw new RuntimeException("Not Implemented");
    }
}
