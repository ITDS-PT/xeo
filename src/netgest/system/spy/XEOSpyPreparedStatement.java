/*Enconding=UTF-8*/
package netgest.system.spy;

import java.io.*;
import java.sql.*;
import java.math.*;
import java.util.ArrayList;
import java.util.Iterator;
import netgest.bo.system.Logger;


public class XEOSpyPreparedStatement extends XEOSpyStatement implements PreparedStatement {

    //logger
    private static Logger logger = Logger.getLogger("netgest.system.spy.XEOSpyPreparedStatement");

    public final static int P6_MAX_FIELDS = 32;
    protected static int P6_GROW_MAX = 32;
    protected PreparedStatement prepStmtPassthru;
    protected String preparedQuery;
    protected Object values[];
    protected boolean isString[];
    protected boolean close = false;
    private ArrayList rsList = new ArrayList();
    private Throwable t = new Throwable();
    
    public XEOSpyPreparedStatement(PreparedStatement statement, Connection conn, String query) {
        super(statement, conn);
        prepStmtPassthru = statement;
        this.preparedQuery = new String(query);
        initValues();
    }

    
    public boolean isClosed() {
        return close;
    }

    public void close() throws java.sql.SQLException {
        Iterator it=rsList.iterator();
        XEOSpyResultSet xeoRs=null;
        while(it.hasNext() ){
            xeoRs=(XEOSpyResultSet)it.next();
            if( !xeoRs.isClosed() ){
                xeoRs.close();
                logger.warn("------------------------------------------------");
                logger.warn("O ResultSet não foi fechado: " + xeoRs.getQuery() );
                xeoRs.printStackTrace();
                logger.warn("------------------------------------------------");
            }
        }
        rsList.clear();
        close = true;
        prepStmtPassthru.close();
    }

    protected void initValues() {
        values = new Object[P6_MAX_FIELDS+1];
        isString = new boolean[P6_MAX_FIELDS+1];
    }

    public void addBatch() throws SQLException {
        prepStmtPassthru.addBatch();
    }

    public void clearParameters() throws SQLException {
        prepStmtPassthru.clearParameters();
    }

    public boolean execute() throws SQLException {
        long timeI = System.currentTimeMillis();
        boolean aux = prepStmtPassthru.execute();
        long timeF = System.currentTimeMillis() - timeI;
        if(timeF > 3000){
            writeToLog(timeF, preparedQuery);
        }
        return aux;
    }

    public ResultSet executeQuery() throws SQLException {
        long timeI = System.currentTimeMillis();
        String s = getQueryFromPreparedStatement();
        ResultSet rs = new XEOSpyResultSet(prepStmtPassthru.executeQuery(), this, preparedQuery, s);
        long timeF = System.currentTimeMillis() - timeI;
        if(timeF > 3000){
            writeToLog(timeF, s);
        }
        rsList.add(rs);
        return rs;
    }

    public int executeUpdate() throws SQLException {
        long timeI = System.currentTimeMillis();
        int aux = prepStmtPassthru.executeUpdate();
        long timeF = System.currentTimeMillis() - timeI;
        if(timeF > 3000){
            writeToLog(timeF, preparedQuery);
        }
        return aux;
    }

    public ResultSetMetaData getMetaData() throws SQLException {
        return prepStmtPassthru.getMetaData();
    }

    public void setArray(int p0, Array p1) throws SQLException {
            prepStmtPassthru.setArray(p0,p1);
    }

    public void setAsciiStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setAsciiStream(p0,p1,p2);
    }

    public void setBigDecimal(int p0, BigDecimal p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBigDecimal(p0,p1);
    }

    public void setBinaryStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBinaryStream(p0,p1,p2);
    }

    public void setBlob(int p0, Blob p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBlob(p0,p1);
    }

    public void setBoolean(int p0, boolean p1) throws SQLException {
        setObjectAsString(p0, new Boolean(p1));
        prepStmtPassthru.setBoolean(p0,p1);
    }

    public void setByte(int p0, byte p1) throws SQLException {
        setObjectAsString(p0, new Byte(p1));
        prepStmtPassthru.setByte(p0,p1);
    }

    public void setBytes(int p0, byte[] p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setBytes(p0,p1);
    }

    public void setCharacterStream(int p0, Reader p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setCharacterStream(p0,p1,p2);
    }

    public void setClob(int p0, Clob p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setClob(p0,p1);
    }

    public void setDate(int p0, Date p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setDate(p0,p1);
    }

    public void setDate(int p0, Date p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setDate(p0,p1,p2);
    }

    public void setDouble(int p0, double p1) throws SQLException {
        setObjectAsInt(p0, new Double(p1));
        prepStmtPassthru.setDouble(p0,p1);
    }

    public void setFloat(int p0, float p1) throws SQLException {
        setObjectAsInt(p0, new Float(p1));
        prepStmtPassthru.setFloat(p0,p1);
    }

    public void setInt(int p0, int p1) throws SQLException {
        setObjectAsInt(p0, new Integer(p1));
        prepStmtPassthru.setInt(p0,p1);
    }

    public void setLong(int p0, long p1) throws SQLException {
        setObjectAsInt(p0, new Long(p1));
        prepStmtPassthru.setLong(p0,p1);
    }

    public void setNull(int p0, int p1, String p2) throws SQLException {
        setObjectAsString(p0, null);
        prepStmtPassthru.setNull(p0,p1,p2);
    }

    public void setNull(int p0, int p1) throws SQLException {
        setObjectAsString(p0, null);
        prepStmtPassthru.setNull(p0,p1);
    }

    public void setObject(int p0, Object p1, int p2, int p3) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0,p1,p2,p3);
    }

    public void setObject(int p0, Object p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0,p1,p2);
    }

    public void setObject(int p0, Object p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setObject(p0,p1);
    }

    public void setRef(int p0, Ref p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setRef(p0,p1);
    }

    public void setShort(int p0, short p1) throws SQLException {
        setObjectAsString(p0, new Short(p1));
        prepStmtPassthru.setShort(p0,p1);
    }

    public void setString(int p0, String p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setString(p0,p1);
    }

    public void setTime(int p0, Time p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTime(p0,p1,p2);
    }

    public void setTime(int p0, Time p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTime(p0,p1);
    }

    public void setTimestamp(int p0, Timestamp p1, java.util.Calendar p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTimestamp(p0,p1,p2);
    }

    public void setTimestamp(int p0, Timestamp p1) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setTimestamp(p0,p1);
    }

    public void setUnicodeStream(int p0, InputStream p1, int p2) throws SQLException {
        setObjectAsString(p0, p1);
        prepStmtPassthru.setUnicodeStream(p0,p1,p2);
    }

    /* we override this because the p6statement version will not be able to return
     * the accurate prepared statement or query information */
    public java.sql.ResultSet getResultSet() throws java.sql.SQLException {
        logger.warn("Metodo não preparado ! : " + getQueryFromPreparedStatement() );
        return new XEOSpyResultSet(passthru.getResultSet(), this, preparedQuery, getQueryFromPreparedStatement());
    }

    /*
     * P6Spy specific functionality
     */
    public final String getQueryFromPreparedStatement() {
        int len = preparedQuery.length();
        StringBuffer t = new StringBuffer(len * 2);

        if (values != null) {
            int i = 1, limit = 0, base = 0;

            while ((limit = preparedQuery.indexOf('?',limit)) != -1) {
                if (isString[i]) {
                    t.append(preparedQuery.substring(base,limit));
                    t.append("'");
                    t.append(values[i]);
                    t.append("'");
                } else {
                    t.append(preparedQuery.substring(base,limit));
                    t.append(values[i]);
                }
                i++;
                limit++;
                base = limit;
            }
            if (base < len) {
                t.append(preparedQuery.substring(base));
            }
        }

        return t.toString();
    }

    protected void growValues(int newMax) {
	int size = values.length;
	Object [] values_tmp = new Object[newMax + P6_GROW_MAX];
	boolean [] isString_tmp = new boolean[newMax + P6_GROW_MAX];
	System.arraycopy(values, 0, values_tmp,  0, size);
	values = values_tmp;
	System.arraycopy(isString, 0, isString_tmp, 0, size);
	isString = isString_tmp;
    }


    protected  void setObjectAsString(int i, Object o) {
        if (values != null) {
            if (i >= 0) {
	       if ( i >= values.length) {
		   growValues(i);
		}
                values[i] = (o == null) ? "" : o.toString();
                isString[i]  = true;
            }
        }
    }

    protected  void setObjectAsInt(int i, Object o) {
        if (values != null) {
            if (i >=0) {
                if (i >= values.length) {
                    growValues(i);
                }
                values[i] = (o == null) ? "" : o.toString();
                isString[i]  = false;
            }
        }
    }

     //Since JDK 1.4
    public void setURL(int p0, java.net.URL p1) throws java.sql.SQLException {
        prepStmtPassthru.setURL(p0, p1);
    }

    // Since JDK 1.4
    public java.sql.ParameterMetaData getParameterMetaData() throws java.sql.SQLException {
        return(prepStmtPassthru.getParameterMetaData());
    }
    public boolean isClose(){
        return close;
    }


    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.PreparedStatement).
     * <p>
     * The returned object is a java.sql.Statement due
     * to inheritance reasons, so you'll need to cast
     * appropriately.
     *
     * @return the wrapped JDBC object
     */
    public Statement getJDBC() {
	Statement wrapped = (prepStmtPassthru instanceof XEOSpyPreparedStatement) ?
	    ((XEOSpyPreparedStatement) prepStmtPassthru).getJDBC() :
	    prepStmtPassthru;

	return wrapped;
    }

    public String getQuery(){
        return preparedQuery;
    }

    private static void writeToLog(long time, String query){
        logger.warn("Query demorada (" + (float)(Math.round((float)(time)/100f))/10f +"s): " + query);
    }

    public void printStackTrace()
    {
        t.printStackTrace();        
    }
    
    // Since JDK 1.6
    public void setAsciiStream(int parameterIndex,
                               InputStream x) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void setAsciiStream(int parameterIndex, InputStream x,
                               long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBinaryStream(int parameterIndex,
                                InputStream x) throws SQLException {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBinaryStream(int parameterIndex, InputStream x,
                                long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBlob(int parameterIndex,
                        InputStream inputStream) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBlob(int parameterIndex, InputStream inputStream,
                        long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setCharacterStream(int parameterIndex,
                                   Reader reader) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setCharacterStream(int parameterIndex, Reader reader,
                                   long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClob(int parameterIndex,
                        Reader reader) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClob(int parameterIndex, Reader reader,
                        long length) {
        throw new RuntimeException("Not Implemented");
    }

    // Since JDK 1.6
    public void setNCharacterStream(int parameterIndex,
                                    Reader value) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNCharacterStream(int parameterIndex, Reader value,
                                    long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(int parameterIndex, NClob value) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(int parameterIndex,
                         Reader reader) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(int parameterIndex, Reader reader,
                         long length) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNString(int parameterIndex,
                           String value) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setRowId(int parameterIndex, RowId x) {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setSQLXML(int parameterIndex,
                          SQLXML xmlObject) {
        throw new RuntimeException("Not Implemented");
    }
}
