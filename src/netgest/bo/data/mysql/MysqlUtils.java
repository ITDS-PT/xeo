package netgest.bo.data.mysql;

import java.util.ArrayList;

import netgest.bo.data.DataException;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.DataTypes;
import netgest.bo.data.DriverUtils;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;

public class MysqlUtils implements DriverUtils {
    /**
     * 
     * @since 
     */

    private String p_ds;

    public MysqlUtils( String dmlds )
    {
        p_ds = dmlds;
    }
    public DataSetMetaData getMetaDataForSelect( EboContext ctx, String query , ArrayList arguments ) 
    {
        DataSetMetaData ret = null;
        MysqlReaderAdapter mysqlreader = null ;
        try
        {
            mysqlreader = new MysqlReaderAdapter( p_ds );
            mysqlreader.executeQuery( ctx, query, arguments,1,1 );
            ret = mysqlreader.getMetaData();
        }
        finally
        {
            mysqlreader.close();
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
        return "VARBINARY";
    }
 
    public final String getPKConstraintName(String tableName)
    {
        return "PRIMARY";
    }
    
	public String fnSysDateTime() {
		return "NOW()";
	}
	public String fnSysTimestamp() {
		return "NOW()";
	}

	public String fnTruncateDate(String exprString) {
		return new StringBuffer("DATE(").append( exprString ).append( ")" ).toString();
	}
	
	public String getQueryLimitStatement(int rows) {
		return " LIMIT 0,  " + rows;
	}

	public byte getQueryLimitStatementPosition() {
		return DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT;
	}
	
	public String getFullTextSearchWhere(String field, String text) {
		return "MATCH(" + field + ") AGAINST (" + text + "  IN BOOLEAN MODE)";
	}

	public String arranjeFulltextSearchText(String fulltext) {
		if( fulltext == null || fulltext.trim().length() == 0 )
			return fulltext;
		
		fulltext = fulltext.replaceAll( "\\p{Punct}" , "" );
		fulltext = fulltext.replaceAll( "\\s\\s" , " " );
		String[] tokens = fulltext.split( " " );
		StringBuffer sb = new StringBuffer();
		for( int i=0; i < tokens.length; i++ ) {
			if( tokens[i].length() > 0 ) {
				if( sb.length() > 0 ) {
					sb.append( ' ' );
				}
				sb.append( "+" ).append( tokens[i] );
			}
		}
		return sb.toString();
	}
}
