/**
 * 
 */
package netgest.bo.data.sqlserver;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import netgest.bo.data.DataException;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.DataTypes;
import netgest.bo.data.DriverUtils;
import netgest.bo.localizations.MessageLocalizer;
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
				throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+
						" [NUMERIC] "+MessageLocalizer.getMessage("TYPE_TO")+" ["
								+ toDataType + "]");
			}
		case DataTypes.VARCHAR:
			switch (toDataType) {
			case DataTypes.VARCHAR:
				return expression;
			case DataTypes.NUMERIC:
				return "CONVERT(numeric," + expression + ")";
			default:
				throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+
						" [VARCHAR] "+MessageLocalizer.getMessage("TYPE_TO")+" ["
								+ toDataType + "]");
			}
		default:
			throw new DataException("0000", MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+" ["
					+ fromDataType + "] "+MessageLocalizer.getMessage("TYPE_TO")+" [" + toDataType + "]");
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
		return new StringBuffer("CONVERT(date,").append(exprString).append(
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
	    fulltext = fulltext.replaceAll("[\\p{Punct}&&[^\\*]]", "");
	
	    fulltext = fulltext.replaceAll("\\s\\s", " ");
	    String[] tokens = fulltext.split(" ");
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < tokens.length; i++) {
	            if (tokens[i].length() > 0) {
	                    if (sb.length() > 0) {
	                            sb.append(' ');
	                    }
	                    sb.append("\"");
	                    sb.append(tokens[i]);
	                    sb.append("\"");
	                    if (i<tokens.length-1)sb.append("&");
	            }
	    }
	    return sb.toString();
	}

	@Override
	public String getSumForAggregate(String aggregateField) {
		return "''' + CAST(ISNULL(SUM (" + aggregateField + "),0) as varchar) + '''";
	}
	
	@Override
	public String getAvgForAggregate(String aggregateField) {
		return "''' + CAST(round(ISNULL(AVG (" + aggregateField
			+ "),0),2) as varchar) + '''";
	}
	@Override
	public String getMinForAggregate(String aggregateField) {
		return "''' + CAST(ISNULL(MIN (" + aggregateField + "),0) as varchar) + '''";
	}
	
	@Override
	public String getMaxForAggregate(String aggregateField) {
		return "''' + CAST(ISNULL(MAX (" + aggregateField + "),0) as varchar) + '''";
	}
	
	@Override
	public String getAggregateExpression(String aggregateFieldId,
			String aggregateFieldDesc, String sum, String avg, String min,
			String max) {
		try {
			return "'{name: ''" + aggregateFieldId + "'',desc: ''" + URLEncoder.encode(aggregateFieldDesc.replaceAll("‚Ç¨", "&euro;"), "UTF-8")
				+ "'', SUM: " + sum + ", AVG: " + avg + ", SMIN: "
				+ min + ", SMAX: " + max + "}' ";
		} catch (UnsupportedEncodingException e) {
			return "'{name: ''" + aggregateFieldId + "'',desc: ''" + aggregateFieldId
				+ "'', SUM: " + sum + ", AVG: " + avg + ", SMIN: "
				+ min + ", SMAX: " + max + "}' ";
		}
	}
	@Override
	public String getAggregateConcatenation() {
		return " + ',' + ";
	}

	@Override
	public String getConcatFunction(String aggregateFields)
	{
		return aggregateFields;
	}

	@Override
	public String concatColumnsWithSeparator(List< String > columns,
			String separator) {

		StringBuilder b = new StringBuilder();
		int length = columns.size();
		for (int k = 0 ; k < length ; k++){
			b.append( String.format( "UPPER(%s)" , columns.get( k ) ) );
			if ( ( k + 1 ) < length ){
				b.append(" + ");
				b.append(separator);
				b.append(" + ");
			}
		}
		
		return b.toString();
		
	}
	
	@Override
	public String getSelectTimeQuery() {
		return "SELECT CURRENT_TIMESTAMP";
	}
}
