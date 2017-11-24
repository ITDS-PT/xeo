/*Enconding=UTF-8*/
package netgest.system.spy;

import java.sql.*;
import java.util.*;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class XEOSpyStatement implements Statement {
    //logger
    private static Logger logger = Logger.getLogger("netgest.system.spy.XEOSpyStatement");
    
    protected Statement passthru;
    public boolean isPoolable() throws SQLException {
		return passthru.isPoolable();
	}


	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return passthru.isWrapperFor(iface);
	}


	public void setPoolable(boolean poolable) throws SQLException {
		passthru.setPoolable(poolable);
	}


	public <T> T unwrap(Class<T> iface) throws SQLException {
		return passthru.unwrap(iface);
	}

	protected Connection connection;
    protected String statementQuery;
    private ArrayList rsList = new ArrayList();
    private boolean close = false;
    private Throwable t = new Throwable();
    
    public XEOSpyStatement(Statement statement, Connection conn) {
        this.passthru = statement;
        this.connection = conn;
        this.statementQuery = "";
    }


    public void close() throws java.sql.SQLException {
        for(int i = 0; i < rsList.size(); i++){
            if(!((XEOSpyResultSet)rsList.get(i)).isClosed()){
                ((XEOSpyResultSet)rsList.get(i)).close();
                logger.warn("------------------------------------------------");
                logger.warn(LoggerMessageLocalizer.getMessage("THE_RESULTSET_WAS_NOT_CLOSED")+": " + ((XEOSpyResultSet)rsList.get(i)).getQuery() );
                ((XEOSpyResultSet)rsList.get(i)).printStackTrace();
                logger.warn("------------------------------------------------");
            }
        }
        rsList.clear();
        close = true;
        passthru.close();
    }

    public boolean execute(String p0) throws java.sql.SQLException {
        statementQuery = new String(p0);
        long timeI = System.currentTimeMillis();
        boolean aux = passthru.execute(p0);
        long timeF = System.currentTimeMillis() - timeI;
        if(timeF > 3000){
            writeToLog(timeF, p0);
        }
        return aux;
    }

    public ResultSet executeQuery(String p0) throws java.sql.SQLException {
        statementQuery = new String(p0);
        long timeI = System.currentTimeMillis();
        ResultSet rs=new XEOSpyResultSet(passthru.executeQuery(p0), this, "", p0);
        long timeF = System.currentTimeMillis() - timeI;
        if(timeF > 3000){
            writeToLog(timeF, p0);
        }
        rsList.add(rs);
        return rs;
    }

    public int executeUpdate(String p0) throws java.sql.SQLException {
        statementQuery = new String(p0);
        long timeI = System.currentTimeMillis();
        int aux = passthru.executeUpdate(p0);
        long timeF = System.currentTimeMillis() - timeI;
        if((timeF) > 3000){
            writeToLog((timeF), statementQuery);
        }
        return(aux);

    }

    public int getMaxFieldSize() throws java.sql.SQLException {
        return(passthru.getMaxFieldSize());
    }

    public void setMaxFieldSize(int p0) throws java.sql.SQLException {
        passthru.setMaxFieldSize(p0);
    }

    public int getMaxRows() throws java.sql.SQLException {
        return(passthru.getMaxRows());
    }

    public void setMaxRows(int p0) throws java.sql.SQLException {
        passthru.setMaxRows(p0);
    }

    public void setEscapeProcessing(boolean p0) throws java.sql.SQLException {
        passthru.setEscapeProcessing(p0);
    }

    public int getQueryTimeout() throws java.sql.SQLException {
        return(passthru.getQueryTimeout());
    }

    public void setQueryTimeout(int p0) throws java.sql.SQLException {
        passthru.setQueryTimeout(p0);
    }

    public void cancel() throws java.sql.SQLException {
        passthru.cancel();
    }

    public java.sql.SQLWarning getWarnings() throws java.sql.SQLException {
        return(passthru.getWarnings());
    }

    public void clearWarnings() throws java.sql.SQLException {
        passthru.clearWarnings();
    }

    public void setCursorName(String p0) throws java.sql.SQLException {
        passthru.setCursorName(p0);
    }

    public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
        ResultSet rs=new XEOSpyResultSet(passthru.getResultSet(), this, "", statementQuery);
        rsList.add(rs);
        return rs;
    }

    public int getUpdateCount() throws java.sql.SQLException {
        return(passthru.getUpdateCount());
    }

    public boolean getMoreResults() throws java.sql.SQLException {
        return(passthru.getMoreResults());
    }

    public void setFetchDirection(int p0) throws java.sql.SQLException {
        passthru.setFetchDirection(p0);
    }

    public int getFetchDirection() throws java.sql.SQLException {
        return(passthru.getFetchDirection());
    }

    public void setFetchSize(int p0) throws java.sql.SQLException {
        passthru.setFetchSize(p0);
    }

    public int getFetchSize() throws java.sql.SQLException {
        return(passthru.getFetchSize());
    }

    public int getResultSetConcurrency() throws java.sql.SQLException {
        return(passthru.getResultSetConcurrency());
    }

    public int getResultSetType() throws java.sql.SQLException {
        return(passthru.getResultSetType());
    }

    public void addBatch(String p0) throws java.sql.SQLException {
        statementQuery = new String(p0);
        passthru.addBatch(p0);
    }

    public void clearBatch() throws java.sql.SQLException {
        passthru.clearBatch();
    }

    public int[] executeBatch() throws java.sql.SQLException {
        return(passthru.executeBatch());
    }

    // returns the p6connection
    public java.sql.Connection getConnection() throws java.sql.SQLException {
        return connection;
    }

    // Since JDK 1.4
    public boolean getMoreResults(int p0) throws java.sql.SQLException {
        return(passthru.getMoreResults(p0));
    }

    // Since JDK 1.4
    public java.sql.ResultSet getGeneratedKeys() throws java.sql.SQLException {
        return(passthru.getGeneratedKeys());
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, int p1) throws java.sql.SQLException {
        return(passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, int p1[]) throws java.sql.SQLException {
        return(passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public int executeUpdate(String p0, String p1[]) throws java.sql.SQLException {
        return(passthru.executeUpdate(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, int p1) throws java.sql.SQLException {
        return(passthru.execute(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, int p1[]) throws java.sql.SQLException {
        return(passthru.execute(p0, p1));
    }

    // Since JDK 1.4
    public boolean execute(String p0, String p1[]) throws java.sql.SQLException {
        return(passthru.execute(p0, p1));
    }
    
    // Since JDK 1.4
    public int getResultSetHoldability() throws java.sql.SQLException {
        return(passthru.getResultSetHoldability());
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.Statement)
     * @return the wrapped JDBC object
     */

    public boolean isClosed(){
        return close;
    }

    public String getQuery(){
        return statementQuery;
    }

    public Statement getJDBC() {
		Statement wrapped = (passthru instanceof XEOSpyStatement) ?
		    ((XEOSpyStatement) passthru).getJDBC() :
		    passthru;
	
		return wrapped;
    }

    private static void writeToLog(long time, String query){
        logger.warn(LoggerMessageLocalizer.getMessage("DELAYED_QUERY")+" (" + (float)(Math.round((float)(time)/100f))/10f +"s): " + query);
    }
    
    public void printStackTrace()
    {
        t.printStackTrace();        
    }


	@Override
	public void closeOnCompletion() throws SQLException {
		passthru.closeOnCompletion();
		
	}


	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return passthru.isCloseOnCompletion();
	}

}
