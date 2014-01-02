/**
 * 
 */
package netgest.bo.data.sqlserver;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import netgest.bo.data.DataException;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.WriterAdapter;
import netgest.bo.data.WriterException;
import netgest.bo.data.constraints.UniqueContraintViolationReporter;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;

/**
 * @author vcardoso
 * 
 */
public class SqlServerWriterAdapter implements WriterAdapter {

	// logger
	private static Logger logger = Logger
			.getLogger(SqlServerWriterAdapter.class);
	/**
	 * 
	 * @since
	 */
	private String p_objectName;
	private String[] p_internalAttributes;
	private String[] p_externalAttributes;
	private String[] p_parentFields;
	private String[] p_childFields;
	private String[] p_icnFields;
	private String p_objectname;
	private String p_schemaname;
	private String p_fulltablename;
	private PreparedStatement p_activeInsertPstm;
	private PreparedStatement p_activeUpdatePstm;
	private PreparedStatement p_activeUpdateClobPstm;
	private PreparedStatement p_activeDeletePstm;
	private String dmlDs;

	public SqlServerWriterAdapter(String dmlds) {
		dmlDs = dmlds;
	}

	public void setParameters(String objectName, String schemaName,
			String[] icnFields, String[] internalAttributes,
			String[] externalAttributes, String[] parentFields,
			String[] childFields) {
		this.p_objectName = objectName;
		this.p_schemaname = schemaName;

		this.p_internalAttributes = internalAttributes;
		this.p_externalAttributes = externalAttributes;

		this.p_parentFields = parentFields;
		this.p_childFields = childFields;

		this.p_icnFields = icnFields;

		this.p_fulltablename = ((schemaName == null) || (schemaName.length() == 0)) ? objectName
				: (schemaName + "." + objectName);
	}

	public void close() {
		if (p_activeInsertPstm != null) {
			try {
				p_activeInsertPstm.close();
			} catch (SQLException e) {
			}

			;
		}

		if (p_activeUpdatePstm != null) {
			try {
				p_activeUpdatePstm.close();
			} catch (SQLException e) {
			}

			;
		}

		if (p_activeDeletePstm != null) {
			try {
				p_activeDeletePstm.close();
			} catch (SQLException e) {
			}

			;
		}

		// if ( p_activeConnection != null )
		// {
		// try { p_activeInsertPstm.close(); } catch ( SQLException e ) {};
		// }
		p_activeInsertPstm = null;
		p_activeUpdatePstm = null;
		p_activeDeletePstm = null;
	}

	private final void checkColumns(DataRow row) {
		if ((p_internalAttributes == null) || (p_externalAttributes == null)) {
			DataSetMetaData meta = row.getDataSet().getMetaData();
			p_internalAttributes = new String[meta.getColumnCount()];
			p_externalAttributes = new String[meta.getColumnCount()];

			for (short i = 0; i < p_internalAttributes.length; i++) {
				p_internalAttributes[i] = meta.getColumnName(i + 1);
				p_externalAttributes[i] = p_internalAttributes[i];
			}
		}
	}

	private boolean checkICN(EboContext ctx, DataRow row) throws SQLException,
			WriterException {
		boolean ret = true;

		if (p_icnFields != null) {
			StringBuffer fields = new StringBuffer("SELECT ");

			for (int i = 0; i < p_icnFields.length; i++) {
				if (i > 0) {
					fields.append(',');
				}

				fields.append(p_icnFields[i]);
			}

			fields.append(" FROM ").append(this.p_fulltablename).append(
					" WHERE ").append(getWhereKey(null, false));

			PreparedStatement pstm = null;
			ResultSet rslt = null;

			try {
				pstm = ctx.getConnectionData().prepareStatement(
						fields.toString());
				setPreparedStatement(row, pstm, 1);
				rslt = pstm.executeQuery();

				if (rslt.next()) {
					throw new WriterException(
							WriterException.CONCURRENCY_FAILED, "["
									+ this.p_fulltablename + "] "+MessageLocalizer.getMessage("LOCAL_ICN_IS")+" ["
									+ rslt.getString(1)
									+ "] "+MessageLocalizer.getMessage("AND_REMOTE_ICN_IS")+" ["
									+ row.getBigDecimal("SYS_ICN") + "]");
				}
			} finally {
				if (rslt != null) {
					rslt.close();
				}

				if (pstm != null) {
					pstm.close();
				}
			}
		}

		return ret;
	}

	private final void setPreparedStatement(DataRow row,
			PreparedStatement pstm, int position) throws SQLException {
		for (int i = 0; i < p_childFields.length; i++) {
			pstm.setObject(i + position, row.getObject(p_parentFields[i]));
		}
	}

	private final PreparedStatement getClobStatement(EboContext ctx,
			DataRow row, ArrayList columns) throws SQLException {
		StringBuffer sb = new StringBuffer("SELECT ");

		for (byte i = 0; i < columns.size(); i++) {
			if (i > 0) {
				sb.append(',');
			}

			sb.append(p_externalAttributes[((Integer) columns.get(i))
					.intValue()]);
		}

		sb.append(" FROM ").append(p_fulltablename).append(" WHERE ").append(
				getWhereKey(null, false));
		sb.append(" FOR UPDATE");
		PreparedStatement pstm = ctx.getConnectionData().prepareStatement(
				sb.toString());
		setPreparedStatement(row, pstm, 1);

		return pstm;
	}

	private String getWhereKey(DataRow row, boolean withICN) {
		StringBuffer where = new StringBuffer();
		short i = 0;

		for (; i < (p_childFields.length - 1); i++) {
			where.append('"').append(p_childFields[i]).append('"').append(
					" = ? AND ");
		}

		where.append('"').append(p_childFields[i]).append('"').append(" = ? ");

		if (withICN) {
			if ((p_icnFields != null) && (p_icnFields.length > 0)) {
				where.append(" AND ");

				byte z;

				for (z = 0; z < (p_icnFields.length - 1); z++) {
					if (row.getObject(p_icnFields[i]) == null) {
						where.append(p_icnFields[i]).append(" IS NULL AND ");
					} else {
						where.append(p_icnFields[i]).append(" = ? AND ");
					}
				}

				if (row.getObject(p_icnFields[i]) == null) {
					where.append(p_icnFields[z]).append(" IS NULL ");
				} else {
					where.append(p_icnFields[z]).append(" = ? ");
				}
			}
		}

		return where.toString();
	}

	public boolean insertRow(EboContext ctx, DataRow dataRow)
			throws WriterException {
		checkColumns(dataRow);

		boolean ret = false;
		Connection conn = null;
		try {
			conn = ctx.getConnectionData();
			if (p_activeInsertPstm == null) {
				StringBuffer insSql1 = new StringBuffer("INSERT INTO ").append(
						p_fulltablename).append(" (");
				StringBuffer insSql2 = new StringBuffer(" VALUES ( ");
				int cc = p_internalAttributes.length;
				int i = 0;

				for (i = 0; i < (cc - 1); i++) {
					insSql1.append('"').append(p_externalAttributes[i]).append(
							'"').append(',');
					insSql2.append('?').append(',');
				}

				insSql1.append('"').append(p_externalAttributes[i]).append('"')
						.append(')');
				insSql2.append('?').append(')');

				insSql1.append(insSql2);
				p_activeInsertPstm = conn.prepareStatement(insSql1.toString());				
			}

			int pstmpos = 0;

			int colidx;
			DataSetMetaData meta = dataRow.getDataSet().getMetaData();
			DataSet data = dataRow.getDataSet();

			ArrayList clobs = null;
			short i;

			for (i = 0; i < p_internalAttributes.length; i++) {
				pstmpos++;
				colidx = data.findColumn(p_internalAttributes[i]);

				Object value;

				if (((value = dataRow.getObject(colidx)) == null)
						|| !(meta.getColumnClassName(colidx).equals(
								"netgest.bo.data.DataClob") && (dataRow
								.getObject(colidx).toString().length() > 3000))) {
					if ((p_icnFields != null)
							&& p_externalAttributes[i].equals(p_icnFields[0])) {
						if (value == null) {
							p_activeInsertPstm.setLong(pstmpos, 1);
							dataRow.updateLong(colidx, 1);
						} else {
							p_activeInsertPstm.setBigDecimal(pstmpos, dataRow
									.getBigDecimal(colidx));
						}
					} else if (value != null) {
						if (meta.getColumnClassName(colidx).equals(
								"netgest.bo.data.DataClob")) {
							p_activeInsertPstm.setString(pstmpos, value
									.toString());
						} else {

							p_activeInsertPstm.setObject(pstmpos, value, meta
									.getColumnType(colidx));
						}
					} else {						
						p_activeInsertPstm.setNull(pstmpos, meta
							.getColumnType(colidx));
					}
				} else {
					if (clobs == null) {
						clobs = new ArrayList();
					}

					clobs.add(new Integer(i));
					// EC p_activeInsertPstm.setClob(pstmpos, CLOB.empty_lob());
					p_activeInsertPstm.setString(pstmpos, " ");
				}

			}

			ret = p_activeInsertPstm.executeUpdate() > 0;

			if (!ret) {
				throw new WriterException(WriterException.CONCURRENCY_FAILED);
			}

			if ((clobs != null) && (clobs.size() > 0)) {
				updateClobs(ctx, dataRow, clobs);
			}
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
        	
        	switch (errorCode){
        	case 2627:
        		reportUniqueContraintViolation( ctx.getDedicatedConnectionData(), e );
        	default: throw new WriterException(WriterException.UNKNOWN_EXECEPTION,
                    e.getMessage(), e);
        	}
		}

		return ret;
	}

	private boolean doesColHaveUniqueIndex(final Connection conn,
			String tableName, final String colName) {

		boolean retVal = false;

		if (tableName.indexOf('.') > -1) {
			tableName = tableName.substring(tableName.indexOf('.') + 1,
					tableName.length());
		}

		StringBuffer sql = new StringBuffer();

		sql.append("select count(1) ");
		sql.append("from sys.indexes ind ");
		sql
				.append("inner join sys.index_columns ic on ind.object_id = ic.object_id and ind.index_id = ic.index_id ");
		sql
				.append("inner join sys.columns col on ic.object_id = col.object_id and ic.column_id = col.column_id ");
		sql
				.append("inner join sys.tables tab on col.object_id = tab.object_id ");
		sql
				.append("where upper(tab.name) = ? and upper(col.name) = ? and ind.is_unique = 1 ");

		PreparedStatement pStmt = null;
		ResultSet rs = null;

		try {
			pStmt = conn.prepareStatement(sql.toString());
			pStmt.setString(1, tableName.toUpperCase());
			pStmt.setString(2, colName.toUpperCase());
			rs = pStmt.executeQuery();

			if (rs.next()) {
				retVal = (rs.getLong(1) == 1);
			}
		} catch (SQLException e) {
			logger.warn(e);
		} finally {

			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
			if (pStmt != null) {
				try {
					pStmt.close();
				} catch (SQLException e) {
					logger.warn(e);
				}
			}
		}

		return retVal;

	}

	private final void updateClobs(EboContext ctx, DataRow dataRow,
			ArrayList clobs) throws SQLException, WriterException {
		try {
			PreparedStatement pstm = getClobStatement(ctx, dataRow, clobs);

			ResultSet rslt = pstm.executeQuery();

			if (rslt.next()) {
				for (byte y = 0; y < clobs.size(); y++) {
					Clob clob = rslt.getClob(y + 1);
					clob.truncate(0);
					String strclob = dataRow
							.getString(p_internalAttributes[((Integer) clobs
									.get(y)).intValue()]);
					clob.setString(1, strclob);
					// CLOB clob = (CLOB) rslt.getClob(y + 1);
					// clob.open(CLOB.MODE_READWRITE);
					//
					// Writer wr = clob.getCharacterOutputStream();
					// String strclob =
					// dataRow.getString(p_internalAttributes[((Integer)
					// clobs.get(
					// y)).intValue()]);
					// int chunksize = clob.getChunkSize();
					// int offset = 0;
					// int endoffset;
					// int chunks = 1;
					//
					// while (offset < strclob.length())
					// {
					// endoffset = Math.min(chunksize * chunks,
					// strclob.length());
					// chunks++;
					// wr.write(strclob.substring(offset, endoffset));
					// offset = endoffset;
					// }
					//
					// wr.flush();
					// clob.close();
				}

				rslt.close();
				pstm.close();
			} else {
				throw new DataException("0000",MessageLocalizer.getMessage("NO_DATA_FOUND_BUT_EXPECTED_ERROR_UPDATING_")+
						" ["
								+ p_fulltablename + "]");
			}
		} catch (Exception e) {
			throw new WriterException(WriterException.UNKNOWN_EXECEPTION,
					"IOException "+MessageLocalizer.getMessage("WRITING_CLOB_TO") + this.p_fulltablename
							+ "\n" + e.getMessage(), e);
		}
	}

	public boolean updateRow(EboContext ctx, DataRow dataRow, boolean checkICN)
			throws WriterException {
		checkColumns(dataRow);

		boolean ret = false;

		try {
			if (p_activeUpdatePstm == null) {
				StringBuffer insSql1 = new StringBuffer(" UPDATE ").append(
						p_fulltablename).append(" ");
				insSql1.append(" SET ");

				int cc = p_internalAttributes.length;
				int i = 0;

				for (i = 0; i < (cc - 1); i++) {
					insSql1.append('"').append(p_externalAttributes[i]).append(
							'"').append("=?").append(',');
				}

				insSql1.append('"').append(p_externalAttributes[i]).append('"')
						.append("=?");
				insSql1.append(" WHERE ").append(getWhereKey(dataRow, true));

				p_activeUpdatePstm = ctx.getConnectionData().prepareStatement(
						insSql1.toString());
			}

			int colidx;
			DataSetMetaData meta = dataRow.getDataSet().getMetaData();
			DataSet data = dataRow.getDataSet();

			int pstmpos = 0;

			ArrayList clobs = null;
			short i;

			for (i = 0; i < p_internalAttributes.length; i++) {
				pstmpos++;
				colidx = data.findColumn(p_internalAttributes[i]);

				Object value;

				if (((value = dataRow.getObject(colidx)) == null)
						|| !(meta.getColumnClassName(colidx).equals(
								"netgest.bo.data.DataClob") && (dataRow
								.getObject(colidx).toString().length() > 3000))) {
					if ((p_icnFields != null)
							&& p_externalAttributes[i].equals(p_icnFields[0])) {
						p_activeUpdatePstm.setLong(pstmpos,
								((value == null) ? 1 : (((BigDecimal) value)
										.longValue() + 1)));
					} else if (value != null) {
						if (meta.getColumnClassName(colidx).equals(
								"netgest.bo.data.DataClob")) {
							p_activeUpdatePstm.setString(pstmpos, value
									.toString());
						} else {
							p_activeUpdatePstm.setObject(pstmpos, value);
						}
					} else {
						p_activeUpdatePstm.setNull(pstmpos, meta
								.getColumnType(colidx));
					}
				} else {
					if (clobs == null) {
						clobs = new ArrayList();
					}

					clobs.add(new Integer(i));
					p_activeUpdatePstm.setString(pstmpos, " ");
				}
			}

			for (byte z = 0; z < p_parentFields.length; z++) {
				pstmpos++;
				if (dataRow.getFlashBackRow() != null) {
					p_activeUpdatePstm.setObject(pstmpos, dataRow
							.getFlashBackRow().getObject(p_parentFields[z]));
				} else {
					p_activeUpdatePstm.setObject(pstmpos, dataRow
							.getObject(p_parentFields[z]));
				}
			}

			for (byte z = 0; (p_icnFields != null) && (z < p_icnFields.length); z++) {
				pstmpos++;

				Object value = dataRow.getObject(p_icnFields[z]);

				if (value != null) {
					p_activeUpdatePstm.setObject(pstmpos, value);
				}
			}

			ret = p_activeUpdatePstm.executeUpdate() > 0;

			if (!ret) {
				if (checkICN)
            		ret = checkICN(ctx, dataRow);
            	else
            		ret = true;
			} else if ((p_icnFields != null) && (p_icnFields.length > 0)) {
				dataRow.updateLong(p_icnFields[0], dataRow
						.getLong(p_icnFields[0]) + 1);
			}

			if ((clobs != null) && (clobs.size() > 0)) {
				updateClobs(ctx, dataRow, clobs);
			}
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			switch (errorCode){
        	case 2627:
        		reportUniqueContraintViolation( ctx.getDedicatedConnectionData(), e );
        	default: throw new WriterException(WriterException.UNKNOWN_EXECEPTION,
                    e.getMessage(), e);
        	}
		} 

		return ret;
	}
	

	private void reportUniqueContraintViolation( Connection c, SQLException e) throws WriterException {
		
		
		String selectColumnsOfUniqueConstraint = "SELECT COLUMN_NAME FROM " +
			" INFORMATION_SCHEMA.CONSTRAINT_COLUMN_USAGE where CONSTRAINT_NAME = ?" ;
    	
    	String patternFindUniqueContraintName = "'(.*?)'";
    		new UniqueContraintViolationReporter( patternFindUniqueContraintName, selectColumnsOfUniqueConstraint )
				.reportUniqueContraint( c, e );
		
	}

	public boolean deleteRow(EboContext ctx, DataRow dataRow)
			throws WriterException {
		checkColumns(dataRow);

		boolean ret = false;

		try {
			if (p_activeDeletePstm == null) {
				StringBuffer insSql1 = new StringBuffer(" DELETE FROM ")
						.append(p_fulltablename);
				insSql1.append(" WHERE ").append(getWhereKey(dataRow, true));

				p_activeDeletePstm = ctx.getConnectionData().prepareStatement(
						insSql1.toString());
			}

			int colidx;
			DataSetMetaData meta = dataRow.getDataSet().getMetaData();
			DataSet data = dataRow.getDataSet();
			int pstmpos = 0;

			for (byte z = 0; z < p_parentFields.length; z++) {
				pstmpos++;
				p_activeDeletePstm.setObject(pstmpos, dataRow
						.getObject(p_parentFields[z]));
			}

			for (byte z = 0; (p_icnFields != null) && (z < p_icnFields.length); z++) {
				pstmpos++;

				Object value = dataRow.getObject(p_icnFields[z]);

				if (value != null) {
					p_activeDeletePstm.setObject(pstmpos, value);
				}
			}

			ret = p_activeDeletePstm.executeUpdate() > 0;

			if (!ret) {
				logger.warn(LoggerMessageLocalizer.getMessage("EXPECTED_ROW_NOT_LONGER_EXISTS_IN")+" ["
						+ p_fulltablename + "]. ( "+LoggerMessageLocalizer.getMessage("DELETING_ROW")+" )");
			}

			ret = true;
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();

			switch (errorCode) {
			case 2292: // Referenced constrainr error code
				throw new WriterException(
						WriterException.REFERENCED_CONTRAINTS, e.getMessage(),
						e);

			default:
				throw new WriterException(WriterException.UNKNOWN_EXECEPTION, e
						.getMessage(), e);
			}
		}

		return ret;
	}

}
