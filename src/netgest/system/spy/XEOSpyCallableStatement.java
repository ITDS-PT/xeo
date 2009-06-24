/*Enconding=UTF-8*/
package netgest.system.spy;

import java.io.InputStream;
import java.io.Reader;

import java.sql.*;

public class XEOSpyCallableStatement extends XEOSpyPreparedStatement implements java.sql.CallableStatement {


    protected CallableStatement callStmtPassthru;
    protected String callableQuery;
    private boolean closed = false;
    private Throwable t = new Throwable();
    
    public XEOSpyCallableStatement(CallableStatement statement, Connection conn, String query) {
        super(statement, conn, query);
        this.callableQuery = query;
        this.callStmtPassthru = statement;
    }

    public String getString(int p0) throws SQLException {
        return callStmtPassthru.getString(p0);
    }

    public void registerOutParameter(int p0, int p1) throws SQLException {
        callStmtPassthru.registerOutParameter(p0, p1);
    }

    public void registerOutParameter(int p0, int p1, int p2) throws SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    public void registerOutParameter(int p0, int p1, String p2) throws SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    public boolean wasNull() throws SQLException {
        return callStmtPassthru.wasNull();
    }

    public java.sql.Array getArray(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getArray(p0);
    }

    public java.math.BigDecimal getBigDecimal(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBigDecimal(p0);
    }

    public java.math.BigDecimal getBigDecimal(int p0, int p1) throws java.sql.SQLException {
        return callStmtPassthru.getBigDecimal(p0,p1);
    }

    public java.sql.Blob getBlob(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBlob(p0);
    }

    public boolean getBoolean(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBoolean(p0);
    }

    public byte getByte(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getByte(p0);
    }

    public byte[] getBytes(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getBytes(p0);
    }

    public java.sql.Clob getClob(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getClob(p0);
    }

    public java.sql.Date getDate(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getDate(p0);
    }

    public java.sql.Date getDate(int p0, java.util.Calendar calendar) throws java.sql.SQLException {
        return callStmtPassthru.getDate(p0,calendar);
    }

    public double getDouble(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getDouble(p0);
    }

    public float getFloat(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getFloat(p0);
    }

    public int getInt(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getInt(p0);
    }

    public long getLong(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getLong(p0);
    }

    public Object getObject(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getObject(p0);
    }

    public Object getObject(int p0, java.util.Map p1) throws java.sql.SQLException {
        return callStmtPassthru.getObject(p0, p1);
    }

    public java.sql.Ref getRef(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getRef(p0);
    }

    public short getShort(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getShort(p0);
    }

    public java.sql.Time getTime(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getTime(p0);
    }

    public java.sql.Time getTime(int p0, java.util.Calendar p1) throws java.sql.SQLException {
        return callStmtPassthru.getTime(p0,p1);
    }

    public java.sql.Timestamp getTimestamp(int p0) throws java.sql.SQLException {
        return callStmtPassthru.getTimestamp(p0);
    }

    public java.sql.Timestamp getTimestamp(int p0, java.util.Calendar p1) throws java.sql.SQLException {
        return callStmtPassthru.getTimestamp(p0,p1);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    // Since JDK 1.4
    public void registerOutParameter(String p0, int p1, String p2) throws java.sql.SQLException {
        callStmtPassthru.registerOutParameter(p0, p1, p2);
    }

    // Since JDK 1.4
    public java.net.URL getURL(int p0) throws java.sql.SQLException {
        return(callStmtPassthru.getURL(p0));
    }

    // Since JDK 1.4
    public void setURL(String p0, java.net.URL p1) throws java.sql.SQLException {
        callStmtPassthru.setURL(p0, p1);
    }

    // Since JDK 1.4
    public void setNull(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.setNull(p0, p1);
    }

    // Since JDK 1.4
    public void setBoolean(String p0, boolean p1) throws java.sql.SQLException {
        callStmtPassthru.setBoolean(p0, p1);
    }

    // Since JDK 1.4
    public void setByte(String p0, byte p1) throws java.sql.SQLException {
        callStmtPassthru.setByte(p0, p1);
    }

    // Since JDK 1.4
    public void setShort(String p0, short p1) throws java.sql.SQLException {
        callStmtPassthru.setShort(p0, p1);
    }

    // Since JDK 1.4
    public void setInt(String p0, int p1) throws java.sql.SQLException {
        callStmtPassthru.setInt(p0, p1);
    }

    // Since JDK 1.4
    public void setLong(String p0, long p1) throws java.sql.SQLException {
        callStmtPassthru.setLong(p0, p1);
    }

    // Since JDK 1.4
    public void setFloat(String p0, float p1) throws java.sql.SQLException {
        callStmtPassthru.setFloat(p0, p1);
    }

    // Since JDK 1.4
    public void setDouble(String p0, double p1) throws java.sql.SQLException {
        callStmtPassthru.setDouble(p0, p1);
    }

    // Since JDK 1.4
    public void setBigDecimal(String p0, java.math.BigDecimal p1) throws java.sql.SQLException {
        callStmtPassthru.setBigDecimal(p0, p1);
    }

    // Since JDK 1.4
    public void setString(String p0, String p1) throws java.sql.SQLException {
        callStmtPassthru.setString(p0, p1);
    }

    // Since JDK 1.4
    public void setBytes(String p0, byte p1[]) throws java.sql.SQLException {
        callStmtPassthru.setBytes(p0, p1);
    }

    // Since JDK 1.4
    public void setDate(String p0, java.sql.Date p1) throws java.sql.SQLException {
        callStmtPassthru.setDate(p0, p1);
    }

    // Since JDK 1.4
    public void setTime(String p0, java.sql.Time p1) throws java.sql.SQLException {
        callStmtPassthru.setTime(p0, p1);
    }

    // Since JDK 1.4
    public void setTimestamp(String p0, java.sql.Timestamp p1) throws java.sql.SQLException {
        callStmtPassthru.setTimestamp(p0, p1);
    }

    // Since JDK 1.4
    public void setAsciiStream(String p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setAsciiStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setBinaryStream(String p0, java.io.InputStream p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setBinaryStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1, int p2, int p3) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1, p2, p3);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setObject(String p0, Object p1) throws java.sql.SQLException {
        callStmtPassthru.setObject(p0, p1);
    }

    // Since JDK 1.4
    public void setCharacterStream(String p0, java.io.Reader p1, int p2) throws java.sql.SQLException {
        callStmtPassthru.setCharacterStream(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setDate(String p0, java.sql.Date p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setDate(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setTime(String p0, java.sql.Time p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setTime(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setTimestamp(String p0, java.sql.Timestamp p1, java.util.Calendar p2) throws java.sql.SQLException {
        callStmtPassthru.setTimestamp(p0, p1, p2);
    }

    // Since JDK 1.4
    public void setNull(String p0, int p1, String p2) throws java.sql.SQLException {
        callStmtPassthru.setNull(p0, p1, p2);
    }

    // Since JDK 1.4
    public String getString(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getString(p0));
    }

    // Since JDK 1.4
    public boolean getBoolean(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBoolean(p0));
    }

    // Since JDK 1.4
    public byte getByte(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getByte(p0));
    }

    // Since JDK 1.4
    public short getShort(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getShort(p0));
    }

    // Since JDK 1.4
    public int getInt(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getInt(p0));
    }

    // Since JDK 1.4
    public long getLong(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getLong(p0));
    }

    // Since JDK 1.4
    public float getFloat(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getFloat(p0));
    }

    // Since JDK 1.4
    public double getDouble(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getDouble(p0));
    }

    // Since JDK 1.4
    public byte[] getBytes(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBytes(p0));
    }

    // Since JDK 1.4
    public java.sql.Date getDate(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getDate(p0));
    }

    // Since JDK 1.4
    public java.sql.Time getTime(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getTime(p0));
    }

    // Since JDK 1.4
    public java.sql.Timestamp getTimestamp(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getTimestamp(p0));
    }

    // Since JDK 1.4
    public Object getObject(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getObject(p0));
    }

    // Since JDK 1.4
    public java.math.BigDecimal getBigDecimal(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBigDecimal(p0));
    }

    // Since JDK 1.4
    public Object getObject(String p0, java.util.Map p1) throws java.sql.SQLException {
        return(callStmtPassthru.getObject(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Ref getRef(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getRef(p0));
    }

    // Since JDK 1.4
    public java.sql.Blob getBlob(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getBlob(p0));
    }

    // Since JDK 1.4
    public java.sql.Clob getClob(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getClob(p0));
    }

    // Since JDK 1.4
    public java.sql.Array getArray(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getArray(p0));
    }

    // Since JDK 1.4
    public java.sql.Date getDate(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getDate(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Time getTime(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getTime(p0, p1));
    }

    // Since JDK 1.4
    public java.sql.Timestamp getTimestamp(String p0, java.util.Calendar p1) throws java.sql.SQLException {
        return(callStmtPassthru.getTimestamp(p0, p1));
    }

    // Since JDK 1.4
    public java.net.URL getURL(String p0) throws java.sql.SQLException {
        return(callStmtPassthru.getURL(p0));
    }

    /**
     * Returns the underlying JDBC object (in this case, a
     * java.sql.CallableStatement).
     * <p>
     * The returned object is a java.sql.Statement due
     * to inheritance reasons, so you'll need to cast
     * appropriately.
     *
     * @return the wrapped JDBC object
     */
    public Statement getJDBC() {
	Statement wrapped = (callStmtPassthru instanceof XEOSpyCallableStatement) ?
	    ((XEOSpyCallableStatement) callStmtPassthru).getJDBC() :
	    callStmtPassthru;

	return wrapped;
    }

    public void printStackTrace()
    {
        t.printStackTrace();        
    }

    // Since JDK 1.6
    public Reader getCharacterStream(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Reader getCharacterStream(String parameterName)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Reader getNCharacterStream(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Reader getNCharacterStream(String parameterName)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public NClob getNClob(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public NClob getNClob(String parameterName)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public String getNString(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public String getNString(String parameterName)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public RowId getRowId(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public RowId getRowId(String parameterName)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(int parameterIndex)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public SQLXML getSQLXML(String parameterName)  {
        return null;
    }
    
    // Since JDK 1.6
    public void setAsciiStream(String parameterName,
                               InputStream x)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setAsciiStream(String parameterName, InputStream x,
                               long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBinaryStream(String parameterName,
                                InputStream x)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBinaryStream(String parameterName, InputStream x,
                                long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBlob(String parameterName, Blob x)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBlob(String parameterName,
                        InputStream inputStream)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setBlob(String parameterName, InputStream inputStream,
                        long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setCharacterStream(String parameterName,
                                   Reader reader)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setCharacterStream(String parameterName, Reader reader,
                                   long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClob(String parameterName, Clob x)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClob(String parameterName,
                        Reader reader)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setClob(String parameterName, Reader reader,
                        long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNCharacterStream(String parameterName,
                                    Reader value)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNCharacterStream(String parameterName, Reader value,
                                    long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(String parameterName,
                         NClob value)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(String parameterName,
                         Reader reader)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNClob(String parameterName, Reader reader,
                         long length)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setNString(String parameterName,
                           String value)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setRowId(String parameterName, RowId x)  {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public void setSQLXML(String parameterName,
                          SQLXML xmlObject)  {
        throw new RuntimeException("Not Implemented");
    }
}
