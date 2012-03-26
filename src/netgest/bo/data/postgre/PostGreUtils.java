/*Enconding=UTF-8*/
package netgest.bo.data.postgre;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;

import netgest.bo.data.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class PostGreUtils  implements DriverUtils
{
    /**
     * 
     * @since 
     */

    private String p_ds;

    public PostGreUtils( String dmlds )
    {
        p_ds = dmlds;
    }
    public DataSetMetaData getMetaDataForSelect( EboContext ctx, String query , ArrayList arguments ) 
    {
        DataSetMetaData ret = null;
        PostGreReaderAdapter orclreader = null ;
        try
        {
            orclreader = new PostGreReaderAdapter( p_ds );
            orclreader.executeQuery( ctx, query, arguments,1,1 );
            ret = orclreader.getMetaData();
        }
        finally
        {
            orclreader.close();
        }
        return ret;
    }
    
    public final String hintStart()
    {
        return "/*+";
    }
    
    public final String hintEnd() 
    {
        return "*/";
    }
    
    public final String hintForFirstRows()
    {
        return "FIRST_ROWS";
    }
    
    public final String convert( int fromDataType, int toDataType, String expression )
    {
        switch( fromDataType )
        {
            case DataTypes.NUMERIC:
                switch ( toDataType )
                {
                    case DataTypes.VARCHAR:
                        return "TO_CHAR(" + expression + ")";
                    case DataTypes.NUMERIC:
                        return expression;
                    default:
                        throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+" [NUMERIC] "+MessageLocalizer.getMessage("TYPE_TO")+" ["+toDataType+"]");
                }
            case DataTypes.VARCHAR:
                switch ( toDataType )
                {
                    case DataTypes.VARCHAR:
                        return expression;
                    case DataTypes.NUMERIC:
                        return "TO_CHAR(" + expression + ")";
                    default:
                        throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+" [VARCHAR] "+MessageLocalizer.getMessage("TYPE_TO")+" ["+toDataType+"]");
                }
            default:
                throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM_TO")+" ["+fromDataType+"] "+MessageLocalizer.getMessage("TYPE_TO")+" ["+toDataType+"]");
        }
    }
    
    public final String getRAWDataType()
    {
        return "RAW";
    }    

    public final String getPKConstraintName(String tableName)
    {
        if(tableName.indexOf('$')>-1)
            return tableName+"_PK";
        else
            return "PK_"+tableName;
    }    
    
	public String fnSysDateTime() {
		return "NOW()";
	}
	public String fnSysTimestamp() {
		return "CURRENT_TIMESTAMP";
	}

	public String fnTruncateDate(String exprString) {
		return new StringBuffer("DATE_TRUNC('microseconds',").append(exprString).append(
		"::timestamp)").toString();
	}
	
	public String getQueryLimitStatement(int rows) {
		return " LIMIT  " + rows;
	}
	
	public byte getQueryLimitStatementPosition() {
		return DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT;
	}
	
	public String getFullTextSearchWhere(String field, String text) {
		return " to_tsvector("+field+") @@ to_tsquery("+text+")"; 
	}
	
	public String arranjeFulltextSearchText(String fulltext) {
		if (fulltext == null || fulltext.length() == 0)
			return fulltext;
		if (fulltext.startsWith("\\"))
			return fulltext.substring(1);

		// Materlada Lusitania
		fulltext = fulltext.replaceAll("\\.", " ");

		String toRet = fulltext;
		boolean logicalExp = false;

		if (fulltext.toUpperCase().indexOf(" E ") != -1
				|| fulltext.toUpperCase().indexOf(" OU ") != -1
				|| fulltext.toUpperCase().indexOf(" AND ") != -1
				|| fulltext.toUpperCase().indexOf(" OR ") != -1
				|| fulltext.toUpperCase().indexOf(" & ") != -1
				|| fulltext.toUpperCase().indexOf(" | ") != -1) {
			if (fulltext.toUpperCase().indexOf(" E ") != -1) {
				fulltext = fulltext.replaceAll(" e ", " and ");
				fulltext = fulltext.replaceAll(" E ", " and ");
			}
			if (fulltext.toUpperCase().indexOf(" OU ") != -1) {
				fulltext = fulltext.replaceAll(" ou ", " or ");
				fulltext = fulltext.replaceAll(" OU ", " or ");
			}
			logicalExp = true;
		}

		String patterns = "((\")(?:[^\\\\\"]|\\\\.)*(\"))|\\S*";

		Pattern p = Pattern.compile(patterns);
		Matcher m = p.matcher(fulltext);
		Vector v = new Vector();
		while (m.find())
			if (m.group().length() > 0)
				v.add(m.group());

		Object[] words = v.toArray();
		toRet = "";
		for (int i = 0; i < words.length; i++) {
			String x = ((String) words[i]).toUpperCase();
			if (x.indexOf("#£#") == -1) {
				x = x.replaceAll("\\{", "{{#£#");
				x = x.replaceAll("}", "{{}}}");
				x = x.replaceAll("#£#", "}");
			} else if (x.indexOf("£££") == -1) {
				x = x.replaceAll("\\{", "{{£££");
				x = x.replaceAll("}", "{}}");
				x = x.replaceAll("£££", "}");
			}
			// x=x.replaceAll("\\{","{{}");
			// x=x.replaceAll("}","{}}");
			if (!logicalExp || (i + 1) == words.length)
				x = x.replaceAll("&", "{&}");
			if (!logicalExp || (i + 1) == words.length)
				x = x.replaceAll("\\|", "{|}");
			x = x.replaceAll("\\(", "{(}");
			x = x.replaceAll("\\)", "{)}");
			x = x.replaceAll("!", "{!}");
			x = x.replaceAll("-", "{-}");
			x = x.replaceAll(",", "{,}");
			x = x.replaceAll(":", "{:}");
			x = x.replaceAll(";", "{;}");
			x = x.replaceAll("=", "{=}");
			x = x.replaceAll(">", "{>}");
			x = x.replaceAll("\\?", "{?}");
			x = x.replaceAll("@", "{@}");
			x = x.replaceAll("#", "{#}");
			x = x.replaceAll("\\*", "{*}");
			x = x.replaceAll("\\$", "{$}");
			x = x.replaceAll("\\\\", "{\\\\\\\\}");
			x = x.replaceAll("_", "{_}");
			x = x.replaceAll("\\[", "{[}");
			x = x.replaceAll("\\]", "{]}");
			x = x.replaceAll("~", "{~}");
			x = specialString(x, "ACCUM");
			if (!logicalExp || (i + 1) == words.length) {
				x = specialString(x, "AND");
			}
			x = specialString(x, "BT");
			x = specialString(x, "BTG");
			x = specialString(x, "BTI");
			x = specialString(x, "BTP");
			x = specialString(x, "EQUIV");
			x = specialString(x, "MINUS");
			x = specialString(x, "NEAR");
			x = specialString(x, "NOT");
			x = specialString(x, "NT");
			x = specialString(x, "NTG");
			x = specialString(x, "NTI");
			x = specialString(x, "NTP");
			if (!logicalExp || (i + 1) == words.length) {
				x = specialString(x, "OR");
			}
			x = specialString(x, "PT");
			x = specialString(x, "RT");
			x = specialString(x, "SQE");
			x = specialString(x, "SYN");
			x = specialString(x, "TT");
			x = specialString(x, "WITHIN");
			if (x.equals("{\\\\}"))
				x = "";
			toRet += x;
			if (i + 1 < words.length && x.length() > 0) {
				if (logicalExp) {
					if ((i + 2 == words.length)
							&& ("&".equals(words[i + 1])
									|| "|".equals(((String) words[i + 1]))
									|| "AND".equals(((String) words[i + 1])
											.toUpperCase()) || "OR"
									.equals(((String) words[i + 1])
											.toUpperCase()))) {
						toRet += " & ";
					} else {
						toRet += " ";
					}
				} else {
					toRet += " & ";
				}
			}
		}
		return toRet;
	}
	
		
	private static String specialString(String aux, String special) {
		int from = 0;
		String toRet = aux;
		while (from >= 0 && from < aux.length()) {
			from = aux.indexOf(special, from);
			if (from >= 0) {
				if ((from == 0 || toRet.charAt(from - 1) == '}')
						&& ((from + special.length()) == toRet.length() || toRet
								.charAt(from + special.length()) == '{')) {
					toRet = toRet.substring(0, from) + "{"
							+ toRet.substring(from, from + special.length())
							+ "}" + toRet.substring(from + special.length());
				}
				from = from + special.length();
			}
		}
		return toRet;
	}
	
	
    public static String prepareSQLForPostGres(String dml)
    {
    	String[] tokens=dml.split("\"");
        boolean endsWithQuote=dml.endsWith("\"");
        dml="";
        for (int i=0;i<tokens.length;i++)
        {
        	String currToken=tokens[i];
        	if (currToken.indexOf(" ")==-1 && (i%2!=0) )
        	{
        		currToken=currToken.toLowerCase();
        	}
        	if (i==tokens.length-1 && !endsWithQuote)
        		dml+=currToken;
        	else
        		dml+=currToken+"\"";
        }
        
        //Arrange UNION ALL for PostGres
        if (dml.indexOf("CREATE OR REPLACE VIEW")>-1)
        {
        	String newdml=dml.substring(0,dml.indexOf("\n"));
        	tokens=dml.substring(dml.indexOf("\n"),dml.length()).split("UNION ALL");
        	int mincount=100000000;
        	int indexofMinCount=0;
        	if (tokens.length>1)
        	{
	            for (int i=0;i<tokens.length;i++)
	            {
	            	String currToken=tokens[i];
	            	int countNulls=countNulls(currToken);
	            	if (countNulls<mincount) 
	        		{
	            		mincount=countNulls;
	            		indexofMinCount=i;
	        		}
	            }
	            dml=newdml+tokens[indexofMinCount]+" \nUNION ALL \n";
	            for (int i=0;i<tokens.length;i++)
	            {
	            	String currToken=tokens[i];   
	            	//if (i!=indexofMinCount)
	            	dml+=currToken+(i==(tokens.length-1)?"":"UNION ALL");
	            }        		           
        	}
        }
        return dml;
    }
    
    private static int countNulls(String text)
    {
    	int toRet=text.split("NULL").length;    	    	    	
    	return toRet;
    }
	
    @Override
	public String getSumForAggregate(String aggregateField) {
		return "''' || coalesce(SUM (" + aggregateField + "),0) || '''";
	}
	
	@Override
	public String getAvgForAggregate(String aggregateField) {
		return "''' || round(coalesce(AVG (" + aggregateField
			+ "),0),2) || '''";
	}
	@Override
	public String getMinForAggregate(String aggregateField) {
		return "''' || coalesce(MIN (" + aggregateField + "),0) || '''";
	}
	
	@Override
	public String getMaxForAggregate(String aggregateField) {
		return "''' || coalesce(MAX (" + aggregateField + "),0) || '''";
	}
	
	@Override
	public String getAggregateExpression(String aggregateFieldId,
			String aggregateFieldDesc, String sum, String avg, String min,
			String max) {
		try {
			return "'{name: ''" + aggregateFieldId + "'',desc: ''" + URLEncoder.encode(aggregateFieldDesc.replaceAll("€", "&euro;"), "UTF-8")
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
		return " || ',' || ";
	}

	@Override
	public String getConcatFunction(String aggregateFields)
	{
		return aggregateFields;
	}
}