/**
 * 
 */
package netgest.bo.data.sqlserver;

import java.util.ArrayList;

import netgest.bo.data.DataException;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.DataTypes;
import netgest.bo.data.DriverUtils;
import netgest.bo.runtime.EboContext;

/**
 * @author vcardoso
 * 
 */
public class SqlServerUtils implements DriverUtils {

	private String p_ds;

	public SqlServerUtils(String dmlds) {
		p_ds = dmlds;
	}

	public DataSetMetaData getMetaDataForSelect(EboContext ctx, String query,
			ArrayList arguments) {
		DataSetMetaData ret = null;
		SqlServerReaderAdapter sqlServerReader = null;
		try {
			sqlServerReader = new SqlServerReaderAdapter(p_ds);
			sqlServerReader.executeQuery(ctx, query, arguments,1,1);
			ret = sqlServerReader.getMetaData();
		} finally {
			sqlServerReader.close();
		}
		return ret;
	}

	public final String hintStart() {
		return "/*+";
	}

	public final String hintEnd() {
		return "*/";
	}

	public final String hintForFirstRows() {
		return "FIRST_ROWS";
	}

	public final String convert(int fromDataType, int toDataType,
			String expression) {
		switch (fromDataType) {
		case DataTypes.NUMERIC:
			switch (toDataType) {
			case DataTypes.VARCHAR:
				return "STR(" + expression + ")";
			case DataTypes.NUMERIC:
				return expression;
			default:
				throw new DataException("0000",
						"Cannot conver from to [NUMERIC] type to ["
								+ toDataType + "]");
			}
		case DataTypes.VARCHAR:
			switch (toDataType) {
			case DataTypes.VARCHAR:
				return expression;
			case DataTypes.NUMERIC:
				return "CONVERT(numeric," + expression + ")";
			default:
				throw new DataException("0000",
						"Cannot conver from to [VARCHAR] type to ["
								+ toDataType + "]");
			}
		default:
			throw new DataException("0000", "Cannot conver from to ["
					+ fromDataType + "] type to [" + toDataType + "]");
		}
	}

	public final String getRAWDataType() {
		return "VARBINARY";
	}

	public final String getPKConstraintName(String tableName) {
		return "PRIMARY";
	}

	public String fnSysDateTime() {
		return "getdate()";
	}

	public String fnSysTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String fnTruncateDate(String exprString) {
		return new StringBuffer("CONVERT(datetime,").append(exprString).append(
				")").toString();
	}

	public String getQueryLimitStatement(int rows) {
		return "/*TOP " + rows +"*/ ";
	}

	public byte getQueryLimitStatementPosition() {
		return DriverUtils.QUERY_LIMIT_ON_SELECT_CLAUSE;
	}

	public String getFullTextSearchWhere(String field, String text) {
		return "CONTAINS(" + field + "," + text + ")";
	}

	public String arranjeFulltextSearchText(String fulltext) {
		if (fulltext == null || fulltext.trim().length() == 0)
			return fulltext;

		fulltext = fulltext.replaceAll("\\p{Punct}", "");
		fulltext = fulltext.replaceAll("\\s\\s", " ");
		String[] tokens = fulltext.split(" ");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < tokens.length; i++) {
			if (tokens[i].length() > 0) {
				if (sb.length() > 0) {
					sb.append(' ');
				}
				sb.append("+").append(tokens[i]);
			}
		}
		return sb.toString();
	}

}
