/**
 * 
 */
package netgest.bo.data.sqlserver;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import netgest.bo.data.Driver;
import netgest.bo.data.DriverUtils;
import netgest.bo.data.ReaderAdapter;
import netgest.bo.data.WriterAdapter;
import netgest.bo.data.oracle.OracleDBM;
import netgest.bo.runtime.EboContext;

/**
 * @author vcardoso
 * 
 */
public class SqlServerDriver implements Driver {

	private String p_ddlds;
	private String p_dmlds;
	private String p_name;

	/**
	 * 
	 * @since
	 */
	public SqlServerDriver() {
	}

	public ReaderAdapter createReaderAdapter(EboContext ctx) {
		return new SqlServerReaderAdapter(p_dmlds);
	}

	public WriterAdapter createWriterAdapter(EboContext ctx) {
		return new SqlServerWriterAdapter(p_dmlds);
	}

	public void initializeDriver(String name, String dmlDataSource,
			String ddlDataSource) {
		p_ddlds = ddlDataSource;
		p_dmlds = dmlDataSource;
		p_name = name;
	}

	public DriverUtils getDriverUtils() {
		return new SqlServerUtils(p_dmlds);
	}

	private Connection getConnection(String dataSource, String username,
			String password) {
		final int retrycount = 5;
		int retries = 0;
		Connection ret = null;

		while (ret == null) {
			try {
				ret = getDataSource(dataSource).getConnection(username,
						password);
				// IMBR
				// ret.setAutoCommit(false);
				return ret;
			} catch (SQLException e) {
				if (retries >= retrycount) {
					throw new RuntimeException(
							" Failed to create connection. \n"
									+ e.getClass().getName() + "\n"
									+ e.getMessage());
				}
			}

			retries++;
		}

		return ret;
	}

	private static final Connection getConnection(String dataSource) {
		final int retrycount = 5;
		int retries = 0;
		Connection ret = null;

		while (ret == null) {
			try {
				ret = getDataSource(dataSource).getConnection();
				// IMBR
				// ret.setAutoCommit(false);

				return ret;
			} catch (SQLException e) {
				ret = null;
				if (retries >= retrycount) {
					throw new RuntimeException(
							" Failed to create connection. \n"
									+ e.getClass().getName() + "\n"
									+ e.getMessage());
				}
			}

			retries++;
		}

		return ret;
	}

	private static final DataSource getDataSource(String dataSourceName) {
		try {
			final InitialContext ic = new InitialContext();

			return (DataSource) ic.lookup(dataSourceName);
		} catch (NamingException e) {
			throw new RuntimeException("Error looking for DataSource name ["
					+ dataSourceName + "].\n" + e.getMessage());
		}
	}

	public Connection getDedicatedConnection() {
		Connection cn = null;
		try {
			cn = getConnection(p_ddlds);
			cn.setAutoCommit(false);
		} catch (SQLException e) {
			System.out
					.println("Failed to obtain Dedicated Connection or disable AutoCommit");
		}
		return cn;
	}

	public Connection getConnection() {
		return getConnection(p_dmlds);
	}

	public Connection getDedicatedConnection(String username, String password) {
		Connection cn = null;
		try {
			cn = getConnection(p_ddlds, username, password);
			cn.setAutoCommit(false);
		} catch (SQLException e) {
			System.out
					.println("Failed to obtain Dedicated Connection or disable AutoCommit");
		}
		return cn;
	}

	public Connection getConnection(String username, String password) {
		return getConnection(p_dmlds, username, password);
	}

	public OracleDBM getDBM() {
		return new SqlServerDBM();
	}

	public String getName() {
		return p_name;
	}

	public void setName(String name) {
		p_name = name;
	}

	public String getEscapeCharStart() {
		return "\"";
	}

	public String getEscapeCharEnd() {
		return "\"";
	}

	public long getDBSequence(EboContext ctx, String seqname, int dsType,
			int operation) {
		try {
			Connection cn;
			switch (dsType) {
			case Driver.SEQUENCE_SYSTEMDS:
				cn = ctx.getConnectionSystem();
				break;
			default:
				cn = ctx.getConnectionData();
				break;
			}

			if (cn.getAutoCommit() == true)
				cn.setAutoCommit(false);

			long ret = 0;

			String sql = "";
			boolean seqNextVal = false;
			boolean error = false;
			if (operation == SEQUENCE_NEXTVAL) {
				CallableStatement cs = null;
				try {
					String query = "{ ? = call "
							+ ctx.getBoSession().getRepository()
									.getSchemaName() + ".nextval( ? ) }";
					cs = cn.prepareCall(query);
					cs.registerOutParameter(1, java.sql.Types.INTEGER);
					cs.setString(2, seqname);
					cs.execute();
					ret = cs.getLong(1);
					// sql = "SELECT " + seqname + ".nextval FROM DUAL";
					seqNextVal = true;
				} catch (Exception e) {
					error = true;
					throw new RuntimeException(e);
				} finally {
					if (cs != null) {
						cs.close();
					}
					if (cn != null) {

						//if (!ctx.isInTransaction()) {
							if (!error) {
								cn.commit();
							} else {
								cn.rollback();
							}
						//}
					}
				}
			} else {
				sql = "SELECT sequence_cur_value from sys_sequences where upper(sequence_name) = "
						+ seqname.toUpperCase();
			}

			if (!seqNextVal) {

				PreparedStatement pstm = null;
				boolean erro = false;
				try {
					ResultSet rslt = (pstm = cn.prepareStatement(sql))
							.executeQuery();
					if (rslt.next()) {
						ret = rslt.getLong(1);

						rslt.close();
						pstm.close();
						return ret;
					} else {
						rslt.close();
						pstm.close();
						throw (new SQLException("Erro a obter sequencia ["
								+ seqname + "]"));
					}
				} catch (SQLException e) {
					erro = true;
					if (e.getMessage().indexOf("08002") == -1) {
						pstm.close();
						Connection cnded = null;
						try {
							switch (dsType) {
							case Driver.SEQUENCE_SYSTEMDS:
								cnded = ctx.getConnectionManager()
										.getSystemDedicatedConnection();
								break;
							default:
								cnded = ctx.getDedicatedConnectionData();
								break;
							}
							cnded = ctx.getDedicatedConnectionData();
							pstm = cnded.prepareStatement("CREATE SEQUENCE "
									+ seqname + " CACHE 20 NOCYCLE ORDER");
							pstm.execute();
							pstm.close();
						} finally {
							if (cnded != null)
								cnded.close();
						}
					}
					if (operation == Driver.SEQUENCE_NEXTVAL) {
						pstm = cn.prepareStatement("SELECT " + seqname
								+ ".nextval FROM DUAL");
						pstm.execute();
						pstm.close();
					}
					ResultSet rslt = (pstm = cn.prepareStatement(sql))
							.executeQuery();
					if (rslt.next()) {
						ret = rslt.getLong(1);
						// cn.commit();
						rslt.close();
						pstm.close();
						return ret;
					} else {
						rslt.close();
						pstm.close();
						throw (new SQLException("Erro a obter sequencia ["
								+ seqname + "]"));
					}
				} finally {
					if (cn != null) {
						if (!erro) {
							cn.commit();
						} else {
							cn.rollback();
						}
					}
				}
			} else {
				return ret;
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDatabaseTimeConstant() {
		return "getdate()";
	}

	public List<HashMap<String, String>> getSeqsData(final EboContext ctx) {

		Connection cn = ctx.getConnectionSystem();

		java.sql.Statement stmt = null;
		java.sql.ResultSet rs = null;

		List<HashMap<String, String>> retList = new ArrayList<HashMap<String, String>>();

		try {
			stmt = cn.createStatement();

			rs = stmt.executeQuery("select * from "
					+ ctx.getBoSession().getRepository().getSchemaName()
					+ ".SYS_SEQUENCES");

			java.sql.ResultSetMetaData metaData = rs.getMetaData();

			final int colCount = metaData.getColumnCount();

			HashMap<String, String> colHash = null;
			String colName = "";
			while (rs.next()) {

				colHash = new HashMap<String, String>();

				for (int index = 1; index <= colCount; index++) {
					colName = metaData.getColumnName(index);
					colHash.put(colName, rs.getString(colName));
				}

				retList.add(colHash);
			}

		} catch (SQLException e) {

		} finally {
			try {
				if (rs != null) {
					rs.close();
				}

				if (stmt != null) {
					stmt.close();
				}
			} catch (SQLException e) {
			}
		}

		return retList;

	}

	public void restoreSeqs(final EboContext ctx,
			final List<HashMap<String, String>> seqsData) {

		java.util.Iterator<HashMap<String, String>> itSeqs = seqsData
				.iterator();

		HashMap<String, String> seqDataMap = null;

		java.util.Iterator<String> keyIt = null;
		String curKey = "";

		StringBuffer colNames = new StringBuffer();
		StringBuffer colValues = new StringBuffer();
		String insertStmt = "INSERT INTO ["
				+ ctx.getBoSession().getRepository().getSchemaName()
				+ "].[SYS_SEQUENCES] (";
		String colsStr = "";
		String colValsStr = "";

		while (itSeqs.hasNext()) {
			seqDataMap = itSeqs.next();

			keyIt = seqDataMap.keySet().iterator();

			while (keyIt.hasNext()) {
				curKey = keyIt.next();

				colNames.append(curKey).append(",");
				colValues.append("'").append(seqDataMap.get(curKey)).append(
						"',");
			}

			colsStr = colNames.toString();
			colNames.setLength(0);

			colValsStr = colValues.toString();
			colValues.setLength(0);

			if (colsStr.endsWith(",")) {
				colsStr = colsStr.substring(0, colsStr.length() - 1);
			}
			if (colValsStr.endsWith(",")) {
				colValsStr = colValsStr.substring(0, colValsStr.length() - 1);
			}

			insertStmt += colsStr + ") VALUES (" + colValsStr + ")";

			executeInsert(ctx, insertStmt);

		}

	}

	private void executeInsert(final EboContext ctx, final String insertSmt) {
		java.sql.Connection conn = ctx.getConnectionSystem();

		Statement ps = null;
		boolean erro = false;
		try {
			ps = conn.createStatement();
			ps.executeUpdate(insertSmt);
			erro = false;
		} catch (SQLException e) {
			
			if ( e.toString().indexOf("PK__SYS_SEQUENCES_") < 0 ){			
				erro = true;
			}else{
				//update
			}

			System.out.println(insertSmt);

			e.printStackTrace();
		} finally {
			try {
				if (conn != null) {
					if (!erro) {
						conn.commit();
					} else {
						conn.rollback();
					}
				}

				if (ps != null) {

					ps.close();

				}
			} catch (SQLException e) {
			}
		}

	}

}
