/*Enconding=UTF-8*/
package netgest.bo.data;
import java.util.ArrayList;
import java.util.List;

import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface DriverUtils 
{
	public static final byte QUERY_LIMIT_ON_SELECT_CLAUSE = 0;
	public static final byte QUERY_LIMIT_ON_WHERE_CLAUSE = 1;
	public static final byte QUERY_LIMIT_ON_END_OF_STATEMENT = 2;
	
    public DataSetMetaData getMetaDataForSelect( EboContext ctx, String query , ArrayList arguments );
    
    public String hintStart();
    
    public String hintEnd() ;
    
    public String hintForFirstRows();
    
    public String convert( int fromDataType, int toDataType, String expression );

    public String getRAWDataType();

    public String getPKConstraintName(String tableName);
    
    public String fnTruncateDate( String exprString );
	
	public String fnSysDateTime();
	
	public String fnSysTimestamp();
	
	public byte getQueryLimitStatementPosition();
	
	public String getQueryLimitStatement( int rows );
	
	public String getFullTextSearchWhere( String field, String text );
	
	public String arranjeFulltextSearchText(String fulltext);
	
	public String getSumForAggregate(String aggregateField);
	
	public String getMinForAggregate(String aggregateField);
	
	public String getMaxForAggregate(String aggregateField);
	
	public String getAvgForAggregate(String aggregateField);
	
	public String getAggregateExpression(String aggregateFieldID,String aggregateFieldDesc,String sum,String avg,String min,String max);
	
	public String getAggregateConcatenation();
	
	public String getConcatFunction(String aggregateFields); 
	
	public String concatColumnsWithSeparator(List<String> columns, String separator);
	
}