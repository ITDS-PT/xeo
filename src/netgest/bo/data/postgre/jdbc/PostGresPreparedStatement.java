package netgest.bo.data.postgre.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;

import netgest.bo.data.postgre.PostGreUtils;



public class PostGresPreparedStatement extends PostGresStatement implements PreparedStatement {

	
	private PreparedStatement pstm;

	public PostGresPreparedStatement(PreparedStatement pstm)
	{
		super(pstm);
		this.pstm = pstm;
	}
	
	public ResultSet executeQuery(String sql) throws SQLException {
		return pstm.executeQuery(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return pstm.unwrap(iface);
	}

	public ResultSet executeQuery() throws SQLException {
		return pstm.executeQuery();
	}

	public int executeUpdate(String sql) throws SQLException {
		return pstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return pstm.isWrapperFor(iface);
	}

	public int executeUpdate() throws SQLException {
		return pstm.executeUpdate();
	}

	public void close() throws SQLException {
		pstm.close();
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		pstm.setNull(parameterIndex, sqlType);
	}

	public int getMaxFieldSize() throws SQLException {
		return pstm.getMaxFieldSize();
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		pstm.setBoolean(parameterIndex, x);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		pstm.setMaxFieldSize(max);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		pstm.setByte(parameterIndex, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		pstm.setShort(parameterIndex, x);
	}

	public int getMaxRows() throws SQLException {
		return pstm.getMaxRows();
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		pstm.setInt(parameterIndex, x);
	}

	public void setMaxRows(int max) throws SQLException {
		pstm.setMaxRows(max);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		pstm.setLong(parameterIndex, x);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		pstm.setEscapeProcessing(enable);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		pstm.setFloat(parameterIndex, x);
	}

	public int getQueryTimeout() throws SQLException {
		return pstm.getQueryTimeout();
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		pstm.setDouble(parameterIndex, x);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		pstm.setQueryTimeout(seconds);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		pstm.setBigDecimal(parameterIndex, x);
	}

	public void cancel() throws SQLException {
		pstm.cancel();
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		pstm.setString(parameterIndex, x);
	}

	public SQLWarning getWarnings() throws SQLException {
		return pstm.getWarnings();
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		pstm.setBytes(parameterIndex, x);
	}

	public void clearWarnings() throws SQLException {
		pstm.clearWarnings();
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		pstm.setDate(parameterIndex, x);
	}

	public void setCursorName(String name) throws SQLException {
		pstm.setCursorName(name);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		pstm.setTime(parameterIndex, x);
	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		pstm.setTimestamp(parameterIndex, x);
	}

	public boolean execute(String sql) throws SQLException {
		return pstm.execute(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		pstm.setAsciiStream(parameterIndex, x, length);
	}

	public ResultSet getResultSet() throws SQLException {
		return pstm.getResultSet();
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		pstm.setUnicodeStream(parameterIndex, x, length);
	}

	public int getUpdateCount() throws SQLException {
		return pstm.getUpdateCount();
	}

	public boolean getMoreResults() throws SQLException {
		return pstm.getMoreResults();
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		pstm.setBinaryStream(parameterIndex, x, length);
	}

	public void setFetchDirection(int direction) throws SQLException {
		pstm.setFetchDirection(direction);
	}

	public void clearParameters() throws SQLException {
		pstm.clearParameters();
	}

	public int getFetchDirection() throws SQLException {
		return pstm.getFetchDirection();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		pstm.setObject(parameterIndex, x, targetSqlType);
	}

	public void setFetchSize(int rows) throws SQLException {
		pstm.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException {
		return pstm.getFetchSize();
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		pstm.setObject(parameterIndex, x);
	}

	public int getResultSetConcurrency() throws SQLException {
		return pstm.getResultSetConcurrency();
	}

	public int getResultSetType() throws SQLException {
		return pstm.getResultSetType();
	}

	public void addBatch(String sql) throws SQLException {
		pstm.addBatch(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public void clearBatch() throws SQLException {
		pstm.clearBatch();
	}

	public boolean execute() throws SQLException {
		return pstm.execute();
	}

	public int[] executeBatch() throws SQLException {
		return pstm.executeBatch();
	}

	public void addBatch() throws SQLException {
		pstm.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		pstm.setCharacterStream(parameterIndex, reader, length);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		pstm.setRef(parameterIndex, x);
	}

	public Connection getConnection() throws SQLException {
		return pstm.getConnection();
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		pstm.setBlob(parameterIndex, x);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		pstm.setClob(parameterIndex, x);
	}

	public boolean getMoreResults(int current) throws SQLException {
		return pstm.getMoreResults(current);
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		pstm.setArray(parameterIndex, x);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return pstm.getMetaData();
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return pstm.getGeneratedKeys();
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		pstm.setDate(parameterIndex, x, cal);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		return pstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql), autoGeneratedKeys);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		pstm.setTime(parameterIndex, x, cal);
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		return pstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql), columnIndexes);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		pstm.setTimestamp(parameterIndex, x, cal);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		pstm.setNull(parameterIndex, sqlType, typeName);
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		return pstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql), columnNames);
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		return pstm.execute(PostGreUtils.prepareSQLForPostGres(sql), autoGeneratedKeys);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		pstm.setURL(parameterIndex, x);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return pstm.getParameterMetaData();
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		pstm.setRowId(parameterIndex, x);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return pstm.execute(PostGreUtils.prepareSQLForPostGres(sql), columnIndexes);
	}

	public void setNString(int parameterIndex, String value)
			throws SQLException {
		pstm.setNString(parameterIndex, value);
	}

	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		pstm.setNCharacterStream(parameterIndex, value, length);
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		return pstm.execute(PostGreUtils.prepareSQLForPostGres(sql), columnNames);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		pstm.setNClob(parameterIndex, value);
	}

	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		pstm.setClob(parameterIndex, reader, length);
	}

	public int getResultSetHoldability() throws SQLException {
		return pstm.getResultSetHoldability();
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		pstm.setBlob(parameterIndex, inputStream, length);
	}

	public boolean isClosed() throws SQLException {
		return pstm.isClosed();
	}

	public void setPoolable(boolean poolable) throws SQLException {
		pstm.setPoolable(poolable);
	}

	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		pstm.setNClob(parameterIndex, reader, length);
	}

	public boolean isPoolable() throws SQLException {
		return pstm.isPoolable();
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		pstm.setSQLXML(parameterIndex, xmlObject);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		pstm.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		pstm.setAsciiStream(parameterIndex, x, length);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		pstm.setBinaryStream(parameterIndex, x, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		pstm.setCharacterStream(parameterIndex, reader, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		pstm.setAsciiStream(parameterIndex, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		pstm.setBinaryStream(parameterIndex, x);
	}

	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		pstm.setCharacterStream(parameterIndex, reader);
	}

	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		pstm.setNCharacterStream(parameterIndex, value);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		pstm.setClob(parameterIndex, reader);
	}

	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		pstm.setBlob(parameterIndex, inputStream);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		pstm.setNClob(parameterIndex, reader);
	}
}
