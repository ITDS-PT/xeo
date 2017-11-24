package netgest.bo.data.postgre.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
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
import java.util.Map;

import netgest.bo.data.postgre.PostGreUtils;


public class PostGresCallableStatement  extends PostGresPreparedStatement implements java.sql.CallableStatement{

	private CallableStatement cstm;

	
	public PostGresCallableStatement(CallableStatement cstm)
	{
		super(cstm);
		this.cstm = cstm;
	}
	
	public ResultSet executeQuery(String sql) throws SQLException {
		return cstm.executeQuery(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		return cstm.unwrap(iface);
	}

	public ResultSet executeQuery() throws SQLException {
		return cstm.executeQuery();
	}

	public int executeUpdate(String sql) throws SQLException {
		return cstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public void registerOutParameter(int parameterIndex, int sqlType)
			throws SQLException {
		cstm.registerOutParameter(parameterIndex, sqlType);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return cstm.isWrapperFor(iface);
	}

	public int executeUpdate() throws SQLException {
		return cstm.executeUpdate();
	}

	public void close() throws SQLException {
		cstm.close();
	}

	public void setNull(int parameterIndex, int sqlType) throws SQLException {
		cstm.setNull(parameterIndex, sqlType);
	}

	public int getMaxFieldSize() throws SQLException {
		return cstm.getMaxFieldSize();
	}

	public void registerOutParameter(int parameterIndex, int sqlType, int scale)
			throws SQLException {
		cstm.registerOutParameter(parameterIndex, sqlType, scale);
	}

	public void setBoolean(int parameterIndex, boolean x) throws SQLException {
		cstm.setBoolean(parameterIndex, x);
	}

	public void setMaxFieldSize(int max) throws SQLException {
		cstm.setMaxFieldSize(max);
	}

	public void setByte(int parameterIndex, byte x) throws SQLException {
		cstm.setByte(parameterIndex, x);
	}

	public void setShort(int parameterIndex, short x) throws SQLException {
		cstm.setShort(parameterIndex, x);
	}

	public int getMaxRows() throws SQLException {
		return cstm.getMaxRows();
	}

	public boolean wasNull() throws SQLException {
		return cstm.wasNull();
	}

	public void setInt(int parameterIndex, int x) throws SQLException {
		cstm.setInt(parameterIndex, x);
	}

	public void setMaxRows(int max) throws SQLException {
		cstm.setMaxRows(max);
	}

	public String getString(int parameterIndex) throws SQLException {
		return cstm.getString(parameterIndex);
	}

	public void setLong(int parameterIndex, long x) throws SQLException {
		cstm.setLong(parameterIndex, x);
	}

	public void setEscapeProcessing(boolean enable) throws SQLException {
		cstm.setEscapeProcessing(enable);
	}

	public boolean getBoolean(int parameterIndex) throws SQLException {
		return cstm.getBoolean(parameterIndex);
	}

	public void setFloat(int parameterIndex, float x) throws SQLException {
		cstm.setFloat(parameterIndex, x);
	}

	public int getQueryTimeout() throws SQLException {
		return cstm.getQueryTimeout();
	}

	public void setDouble(int parameterIndex, double x) throws SQLException {
		cstm.setDouble(parameterIndex, x);
	}

	public byte getByte(int parameterIndex) throws SQLException {
		return cstm.getByte(parameterIndex);
	}

	public void setQueryTimeout(int seconds) throws SQLException {
		cstm.setQueryTimeout(seconds);
	}

	public void setBigDecimal(int parameterIndex, BigDecimal x)
			throws SQLException {
		cstm.setBigDecimal(parameterIndex, x);
	}

	public short getShort(int parameterIndex) throws SQLException {
		return cstm.getShort(parameterIndex);
	}

	public void cancel() throws SQLException {
		cstm.cancel();
	}

	public void setString(int parameterIndex, String x) throws SQLException {
		cstm.setString(parameterIndex, x);
	}

	public int getInt(int parameterIndex) throws SQLException {
		return cstm.getInt(parameterIndex);
	}

	public SQLWarning getWarnings() throws SQLException {
		return cstm.getWarnings();
	}

	public long getLong(int parameterIndex) throws SQLException {
		return cstm.getLong(parameterIndex);
	}

	public void setBytes(int parameterIndex, byte[] x) throws SQLException {
		cstm.setBytes(parameterIndex, x);
	}

	public float getFloat(int parameterIndex) throws SQLException {
		return cstm.getFloat(parameterIndex);
	}

	public void clearWarnings() throws SQLException {
		cstm.clearWarnings();
	}

	public void setDate(int parameterIndex, Date x) throws SQLException {
		cstm.setDate(parameterIndex, x);
	}

	public void setCursorName(String name) throws SQLException {
		cstm.setCursorName(name);
	}

	public double getDouble(int parameterIndex) throws SQLException {
		return cstm.getDouble(parameterIndex);
	}

	public void setTime(int parameterIndex, Time x) throws SQLException {
		cstm.setTime(parameterIndex, x);
	}

	public BigDecimal getBigDecimal(int parameterIndex, int scale)
			throws SQLException {
		return cstm.getBigDecimal(parameterIndex, scale);
	}

	public void setTimestamp(int parameterIndex, Timestamp x)
			throws SQLException {
		cstm.setTimestamp(parameterIndex, x);
	}

	public boolean execute(String sql) throws SQLException {
		return cstm.execute(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public void setAsciiStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		cstm.setAsciiStream(parameterIndex, x, length);
	}

	public byte[] getBytes(int parameterIndex) throws SQLException {
		return cstm.getBytes(parameterIndex);
	}

	public Date getDate(int parameterIndex) throws SQLException {
		return cstm.getDate(parameterIndex);
	}

	public ResultSet getResultSet() throws SQLException {
		return cstm.getResultSet();
	}

	public void setUnicodeStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		cstm.setUnicodeStream(parameterIndex, x, length);
	}

	public Time getTime(int parameterIndex) throws SQLException {
		return cstm.getTime(parameterIndex);
	}

	public int getUpdateCount() throws SQLException {
		return cstm.getUpdateCount();
	}

	public Timestamp getTimestamp(int parameterIndex) throws SQLException {
		return cstm.getTimestamp(parameterIndex);
	}

	public boolean getMoreResults() throws SQLException {
		return cstm.getMoreResults();
	}

	public void setBinaryStream(int parameterIndex, InputStream x, int length)
			throws SQLException {
		cstm.setBinaryStream(parameterIndex, x, length);
	}

	public Object getObject(int parameterIndex) throws SQLException {
		return cstm.getObject(parameterIndex);
	}

	public void setFetchDirection(int direction) throws SQLException {
		cstm.setFetchDirection(direction);
	}

	public void clearParameters() throws SQLException {
		cstm.clearParameters();
	}

	public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
		return cstm.getBigDecimal(parameterIndex);
	}

	public int getFetchDirection() throws SQLException {
		return cstm.getFetchDirection();
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType)
			throws SQLException {
		cstm.setObject(parameterIndex, x, targetSqlType);
	}

	public Object getObject(int parameterIndex, Map<String, Class<?>> map)
			throws SQLException {
		return cstm.getObject(parameterIndex, map);
	}

	public void setFetchSize(int rows) throws SQLException {
		cstm.setFetchSize(rows);
	}

	public int getFetchSize() throws SQLException {
		return cstm.getFetchSize();
	}

	public void setObject(int parameterIndex, Object x) throws SQLException {
		cstm.setObject(parameterIndex, x);
	}

	public Ref getRef(int parameterIndex) throws SQLException {
		return cstm.getRef(parameterIndex);
	}

	public int getResultSetConcurrency() throws SQLException {
		return cstm.getResultSetConcurrency();
	}

	public Blob getBlob(int parameterIndex) throws SQLException {
		return cstm.getBlob(parameterIndex);
	}

	public int getResultSetType() throws SQLException {
		return cstm.getResultSetType();
	}

	public void addBatch(String sql) throws SQLException {
		cstm.addBatch(PostGreUtils.prepareSQLForPostGres(sql));
	}

	public Clob getClob(int parameterIndex) throws SQLException {
		return cstm.getClob(parameterIndex);
	}

	public void clearBatch() throws SQLException {
		cstm.clearBatch();
	}

	public boolean execute() throws SQLException {
		return cstm.execute();
	}

	public Array getArray(int parameterIndex) throws SQLException {
		return cstm.getArray(parameterIndex);
	}

	public int[] executeBatch() throws SQLException {
		return cstm.executeBatch();
	}

	public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
		return cstm.getDate(parameterIndex, cal);
	}

	public void addBatch() throws SQLException {
		cstm.addBatch();
	}

	public void setCharacterStream(int parameterIndex, Reader reader, int length)
			throws SQLException {
		cstm.setCharacterStream(parameterIndex, reader, length);
	}

	public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
		return cstm.getTime(parameterIndex, cal);
	}

	public void setRef(int parameterIndex, Ref x) throws SQLException {
		cstm.setRef(parameterIndex, x);
	}

	public Connection getConnection() throws SQLException {
		return cstm.getConnection();
	}

	public Timestamp getTimestamp(int parameterIndex, Calendar cal)
			throws SQLException {
		return cstm.getTimestamp(parameterIndex, cal);
	}

	public void setBlob(int parameterIndex, Blob x) throws SQLException {
		cstm.setBlob(parameterIndex, x);
	}

	public void registerOutParameter(int parameterIndex, int sqlType,
			String typeName) throws SQLException {
		cstm.registerOutParameter(parameterIndex, sqlType, typeName);
	}

	public void setClob(int parameterIndex, Clob x) throws SQLException {
		cstm.setClob(parameterIndex, x);
	}

	public boolean getMoreResults(int current) throws SQLException {
		return cstm.getMoreResults(current);
	}

	public void setArray(int parameterIndex, Array x) throws SQLException {
		cstm.setArray(parameterIndex, x);
	}

	public ResultSetMetaData getMetaData() throws SQLException {
		return cstm.getMetaData();
	}

	public void registerOutParameter(String parameterName, int sqlType)
			throws SQLException {
		cstm.registerOutParameter(parameterName, sqlType);
	}

	public ResultSet getGeneratedKeys() throws SQLException {
		return cstm.getGeneratedKeys();
	}

	public void setDate(int parameterIndex, Date x, Calendar cal)
			throws SQLException {
		cstm.setDate(parameterIndex, x, cal);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys)
			throws SQLException {
		return cstm.executeUpdate(sql, autoGeneratedKeys);
	}

	public void setTime(int parameterIndex, Time x, Calendar cal)
			throws SQLException {
		cstm.setTime(parameterIndex, x, cal);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			int scale) throws SQLException {
		cstm.registerOutParameter(parameterName, sqlType, scale);
	}

	public int executeUpdate(String sql, int[] columnIndexes)
			throws SQLException {
		return cstm.executeUpdate(sql, columnIndexes);
	}

	public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal)
			throws SQLException {
		cstm.setTimestamp(parameterIndex, x, cal);
	}

	public void registerOutParameter(String parameterName, int sqlType,
			String typeName) throws SQLException {
		cstm.registerOutParameter(parameterName, sqlType, typeName);
	}

	public void setNull(int parameterIndex, int sqlType, String typeName)
			throws SQLException {
		cstm.setNull(parameterIndex, sqlType, typeName);
	}

	public int executeUpdate(String sql, String[] columnNames)
			throws SQLException {
		return cstm.executeUpdate(PostGreUtils.prepareSQLForPostGres(sql), columnNames);
	}

	public boolean execute(String sql, int autoGeneratedKeys)
			throws SQLException {
		return cstm.execute(PostGreUtils.prepareSQLForPostGres(sql), autoGeneratedKeys);
	}

	public URL getURL(int parameterIndex) throws SQLException {
		return cstm.getURL(parameterIndex);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException {
		cstm.setURL(parameterIndex, x);
	}

	public void setURL(String parameterName, URL val) throws SQLException {
		cstm.setURL(parameterName, val);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException {
		return cstm.getParameterMetaData();
	}

	public void setNull(String parameterName, int sqlType) throws SQLException {
		cstm.setNull(parameterName, sqlType);
	}

	public void setRowId(int parameterIndex, RowId x) throws SQLException {
		cstm.setRowId(parameterIndex, x);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException {
		return cstm.execute(PostGreUtils.prepareSQLForPostGres(sql), columnIndexes);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException {
		cstm.setBoolean(parameterName, x);
	}

	public void setNString(int parameterIndex, String value)
			throws SQLException {
		cstm.setNString(parameterIndex, value);
	}

	public void setByte(String parameterName, byte x) throws SQLException {
		cstm.setByte(parameterName, x);
	}

	public void setNCharacterStream(int parameterIndex, Reader value,
			long length) throws SQLException {
		cstm.setNCharacterStream(parameterIndex, value, length);
	}

	public void setShort(String parameterName, short x) throws SQLException {
		cstm.setShort(parameterName, x);
	}

	public void setInt(String parameterName, int x) throws SQLException {
		cstm.setInt(parameterName, x);
	}

	public boolean execute(String sql, String[] columnNames)
			throws SQLException {
		return cstm.execute(PostGreUtils.prepareSQLForPostGres(sql), columnNames);
	}

	public void setNClob(int parameterIndex, NClob value) throws SQLException {
		cstm.setNClob(parameterIndex, value);
	}

	public void setLong(String parameterName, long x) throws SQLException {
		cstm.setLong(parameterName, x);
	}

	public void setClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		cstm.setClob(parameterIndex, reader, length);
	}

	public void setFloat(String parameterName, float x) throws SQLException {
		cstm.setFloat(parameterName, x);
	}

	public void setDouble(String parameterName, double x) throws SQLException {
		cstm.setDouble(parameterName, x);
	}

	public int getResultSetHoldability() throws SQLException {
		return cstm.getResultSetHoldability();
	}

	public void setBlob(int parameterIndex, InputStream inputStream, long length)
			throws SQLException {
		cstm.setBlob(parameterIndex, inputStream, length);
	}

	public boolean isClosed() throws SQLException {
		return cstm.isClosed();
	}

	public void setBigDecimal(String parameterName, BigDecimal x)
			throws SQLException {
		cstm.setBigDecimal(parameterName, x);
	}

	public void setPoolable(boolean poolable) throws SQLException {
		cstm.setPoolable(poolable);
	}

	public void setString(String parameterName, String x) throws SQLException {
		cstm.setString(parameterName, x);
	}

	public void setNClob(int parameterIndex, Reader reader, long length)
			throws SQLException {
		cstm.setNClob(parameterIndex, reader, length);
	}

	public boolean isPoolable() throws SQLException {
		return cstm.isPoolable();
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException {
		cstm.setBytes(parameterName, x);
	}

	public void setDate(String parameterName, Date x) throws SQLException {
		cstm.setDate(parameterName, x);
	}

	public void setSQLXML(int parameterIndex, SQLXML xmlObject)
			throws SQLException {
		cstm.setSQLXML(parameterIndex, xmlObject);
	}

	public void setTime(String parameterName, Time x) throws SQLException {
		cstm.setTime(parameterName, x);
	}

	public void setObject(int parameterIndex, Object x, int targetSqlType,
			int scaleOrLength) throws SQLException {
		cstm.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
	}

	public void setTimestamp(String parameterName, Timestamp x)
			throws SQLException {
		cstm.setTimestamp(parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length)
			throws SQLException {
		cstm.setAsciiStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length)
			throws SQLException {
		cstm.setBinaryStream(parameterName, x, length);
	}

	public void setAsciiStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		cstm.setAsciiStream(parameterIndex, x, length);
	}

	public void setObject(String parameterName, Object x, int targetSqlType,
			int scale) throws SQLException {
		cstm.setObject(parameterName, x, targetSqlType, scale);
	}

	public void setBinaryStream(int parameterIndex, InputStream x, long length)
			throws SQLException {
		cstm.setBinaryStream(parameterIndex, x, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader,
			long length) throws SQLException {
		cstm.setCharacterStream(parameterIndex, reader, length);
	}

	public void setObject(String parameterName, Object x, int targetSqlType)
			throws SQLException {
		cstm.setObject(parameterName, x, targetSqlType);
	}

	public void setAsciiStream(int parameterIndex, InputStream x)
			throws SQLException {
		cstm.setAsciiStream(parameterIndex, x);
	}

	public void setObject(String parameterName, Object x) throws SQLException {
		cstm.setObject(parameterName, x);
	}

	public void setBinaryStream(int parameterIndex, InputStream x)
			throws SQLException {
		cstm.setBinaryStream(parameterIndex, x);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			int length) throws SQLException {
		cstm.setCharacterStream(parameterName, reader, length);
	}

	public void setCharacterStream(int parameterIndex, Reader reader)
			throws SQLException {
		cstm.setCharacterStream(parameterIndex, reader);
	}

	public void setDate(String parameterName, Date x, Calendar cal)
			throws SQLException {
		cstm.setDate(parameterName, x, cal);
	}

	public void setNCharacterStream(int parameterIndex, Reader value)
			throws SQLException {
		cstm.setNCharacterStream(parameterIndex, value);
	}

	public void setTime(String parameterName, Time x, Calendar cal)
			throws SQLException {
		cstm.setTime(parameterName, x, cal);
	}

	public void setClob(int parameterIndex, Reader reader) throws SQLException {
		cstm.setClob(parameterIndex, reader);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal)
			throws SQLException {
		cstm.setTimestamp(parameterName, x, cal);
	}

	public void setBlob(int parameterIndex, InputStream inputStream)
			throws SQLException {
		cstm.setBlob(parameterIndex, inputStream);
	}

	public void setNull(String parameterName, int sqlType, String typeName)
			throws SQLException {
		cstm.setNull(parameterName, sqlType, typeName);
	}

	public void setNClob(int parameterIndex, Reader reader) throws SQLException {
		cstm.setNClob(parameterIndex, reader);
	}

	public String getString(String parameterName) throws SQLException {
		return cstm.getString(parameterName);
	}

	public boolean getBoolean(String parameterName) throws SQLException {
		return cstm.getBoolean(parameterName);
	}

	public byte getByte(String parameterName) throws SQLException {
		return cstm.getByte(parameterName);
	}

	public short getShort(String parameterName) throws SQLException {
		return cstm.getShort(parameterName);
	}

	public int getInt(String parameterName) throws SQLException {
		return cstm.getInt(parameterName);
	}

	public long getLong(String parameterName) throws SQLException {
		return cstm.getLong(parameterName);
	}

	public float getFloat(String parameterName) throws SQLException {
		return cstm.getFloat(parameterName);
	}

	public double getDouble(String parameterName) throws SQLException {
		return cstm.getDouble(parameterName);
	}

	public byte[] getBytes(String parameterName) throws SQLException {
		return cstm.getBytes(parameterName);
	}

	public Date getDate(String parameterName) throws SQLException {
		return cstm.getDate(parameterName);
	}

	public Time getTime(String parameterName) throws SQLException {
		return cstm.getTime(parameterName);
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException {
		return cstm.getTimestamp(parameterName);
	}

	public Object getObject(String parameterName) throws SQLException {
		return cstm.getObject(parameterName);
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException {
		return cstm.getBigDecimal(parameterName);
	}

	public Object getObject(String parameterName, Map<String, Class<?>> map)
			throws SQLException {
		return cstm.getObject(parameterName, map);
	}

	public Ref getRef(String parameterName) throws SQLException {
		return cstm.getRef(parameterName);
	}

	public Blob getBlob(String parameterName) throws SQLException {
		return cstm.getBlob(parameterName);
	}

	public Clob getClob(String parameterName) throws SQLException {
		return cstm.getClob(parameterName);
	}

	public Array getArray(String parameterName) throws SQLException {
		return cstm.getArray(parameterName);
	}

	public Date getDate(String parameterName, Calendar cal) throws SQLException {
		return cstm.getDate(parameterName, cal);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException {
		return cstm.getTime(parameterName, cal);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal)
			throws SQLException {
		return cstm.getTimestamp(parameterName, cal);
	}

	public URL getURL(String parameterName) throws SQLException {
		return cstm.getURL(parameterName);
	}

	public RowId getRowId(int parameterIndex) throws SQLException {
		return cstm.getRowId(parameterIndex);
	}

	public RowId getRowId(String parameterName) throws SQLException {
		return cstm.getRowId(parameterName);
	}

	public void setRowId(String parameterName, RowId x) throws SQLException {
		cstm.setRowId(parameterName, x);
	}

	public void setNString(String parameterName, String value)
			throws SQLException {
		cstm.setNString(parameterName, value);
	}

	public void setNCharacterStream(String parameterName, Reader value,
			long length) throws SQLException {
		cstm.setNCharacterStream(parameterName, value, length);
	}

	public void setNClob(String parameterName, NClob value) throws SQLException {
		cstm.setNClob(parameterName, value);
	}

	public void setClob(String parameterName, Reader reader, long length)
			throws SQLException {
		cstm.setClob(parameterName, reader, length);
	}

	public void setBlob(String parameterName, InputStream inputStream,
			long length) throws SQLException {
		cstm.setBlob(parameterName, inputStream, length);
	}

	public void setNClob(String parameterName, Reader reader, long length)
			throws SQLException {
		cstm.setNClob(parameterName, reader, length);
	}

	public NClob getNClob(int parameterIndex) throws SQLException {
		return cstm.getNClob(parameterIndex);
	}

	public NClob getNClob(String parameterName) throws SQLException {
		return cstm.getNClob(parameterName);
	}

	public void setSQLXML(String parameterName, SQLXML xmlObject)
			throws SQLException {
		cstm.setSQLXML(parameterName, xmlObject);
	}

	public SQLXML getSQLXML(int parameterIndex) throws SQLException {
		return cstm.getSQLXML(parameterIndex);
	}

	public SQLXML getSQLXML(String parameterName) throws SQLException {
		return cstm.getSQLXML(parameterName);
	}

	public String getNString(int parameterIndex) throws SQLException {
		return cstm.getNString(parameterIndex);
	}

	public String getNString(String parameterName) throws SQLException {
		return cstm.getNString(parameterName);
	}

	public Reader getNCharacterStream(int parameterIndex) throws SQLException {
		return cstm.getNCharacterStream(parameterIndex);
	}

	public Reader getNCharacterStream(String parameterName) throws SQLException {
		return cstm.getNCharacterStream(parameterName);
	}

	public Reader getCharacterStream(int parameterIndex) throws SQLException {
		return cstm.getCharacterStream(parameterIndex);
	}

	public Reader getCharacterStream(String parameterName) throws SQLException {
		return cstm.getCharacterStream(parameterName);
	}

	public void setBlob(String parameterName, Blob x) throws SQLException {
		cstm.setBlob(parameterName, x);
	}

	public void setClob(String parameterName, Clob x) throws SQLException {
		cstm.setClob(parameterName, x);
	}

	public void setAsciiStream(String parameterName, InputStream x, long length)
			throws SQLException {
		cstm.setAsciiStream(parameterName, x, length);
	}

	public void setBinaryStream(String parameterName, InputStream x, long length)
			throws SQLException {
		cstm.setBinaryStream(parameterName, x, length);
	}

	public void setCharacterStream(String parameterName, Reader reader,
			long length) throws SQLException {
		cstm.setCharacterStream(parameterName, reader, length);
	}

	public void setAsciiStream(String parameterName, InputStream x)
			throws SQLException {
		cstm.setAsciiStream(parameterName, x);
	}

	public void setBinaryStream(String parameterName, InputStream x)
			throws SQLException {
		cstm.setBinaryStream(parameterName, x);
	}

	public void setCharacterStream(String parameterName, Reader reader)
			throws SQLException {
		cstm.setCharacterStream(parameterName, reader);
	}

	public void setNCharacterStream(String parameterName, Reader value)
			throws SQLException {
		cstm.setNCharacterStream(parameterName, value);
	}

	public void setClob(String parameterName, Reader reader)
			throws SQLException {
		cstm.setClob(parameterName, reader);
	}

	public void setBlob(String parameterName, InputStream inputStream)
			throws SQLException {
		cstm.setBlob(parameterName, inputStream);
	}

	public void setNClob(String parameterName, Reader reader)
			throws SQLException {
		cstm.setNClob(parameterName, reader);
	}

	@Override
	public void closeOnCompletion() throws SQLException {
		cstm.closeOnCompletion();
	}

	@Override
	public boolean isCloseOnCompletion() throws SQLException {
		return cstm.isCloseOnCompletion();
	}

	@Override
	public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
		return cstm.getObject( parameterIndex, type);
	}

	@Override
	public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
		return cstm.getObject( parameterName, type);
	}
	
}
