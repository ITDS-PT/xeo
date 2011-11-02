package netgest.bo.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import netgest.bo.data.postgre.PostGreUtils;
import netgest.bo.system.boApplication;

public class XEOQLModifier {

    private static final String patterns = "(order|group)[\\s+]by|\\/\\*\\+|\\*\\/|\\/\\*|\\{\\{|\\}\\}|[\\.\\,\\(\\)\\[\\]=<>!\\+\\-\\*/'\\n\\t\\?]|[a-zA-Z_$0-9]++|\\w++|\\W";
    private static final Pattern tokenizerRegExp = Pattern.compile(patterns,Pattern.CASE_INSENSITIVE);
		
	private StringBuffer 	originalQL;
	private List 	originalQLParameters;
	
	private String statementTypePart;
	private String hintsPart;
	private String fieldsPart;
	private String objectPart;
	private String wherePart;
	private String groupByPart;
	private String orderByPart;
	
	private Map qlFields = new LinkedHashMap(); 
	
	private List fieldsPartParameters = new ArrayList(0);
	private List wherePartParameters = new ArrayList(0);
	private List fromPartParameters = new ArrayList(0);
	private List groupByPartParameters = new ArrayList(0);
	private List orderByPartParameters = new ArrayList(0);
	
	private String afterSelect = "";
	private String afterWhere = "";
	private String afterOrderBy = "";

	private int	paramIdx = 0;
	
    public static void main( String[] args ) {
    	
    	List params = new ArrayList();
    	
    	System.out.println( 
	    	new XEOQLModifier(
    			"select /*+HELLO WORLD*/ joao,f,m,\"from\",'from' from dual where 1=1 and x in (select 1,'from' from dual) order by 3"
	    	,
	    	null
	    	).toBOQL( params )
    	);
    }
    
	public XEOQLModifier( String boql, List originalParams ) {
		String g;
		String word;
		
		this.originalQL = new StringBuffer( boql );
		this.originalQLParameters = originalParams;
		
		StringBuffer sbStatementTypePart = new StringBuffer();
		StringBuffer sbHintsPart = new StringBuffer();
		StringBuffer sbFieldsPart = new StringBuffer();
		StringBuffer sbFromPart = new StringBuffer();
		StringBuffer sbWherePart = new StringBuffer();
		StringBuffer sbGroupByPart = new StringBuffer();
		StringBuffer sbOrderByPart = new StringBuffer();
		
		StringBuffer posBuffer = new StringBuffer();
		List  posParam  = null;
		
        Matcher m = tokenizerRegExp.matcher( boql );
        while (m.find())
        {
        	g = getGroupFromOriginal( this.originalQL, m );
        	if ("/*+".equals( g ) && posBuffer == sbFieldsPart ) {
        		sbHintsPart.append( skipPair( this.originalQL, m, null, "*/", null ) );
        	}
        	else {
	        	g = skipQuotedOrComment( this.originalQL, m, g ).toString();
	        	word = g.toLowerCase();
	        	if( "?".equals( word ) ) {
	        		setParameter( posParam );
	        		posBuffer.append( word );
	        	}
	        	else if( "select".equals( word ) ) {
	        		
	        		sbStatementTypePart.append( posBuffer );
	        		sbStatementTypePart.append( g );
	        		
	        		posBuffer = sbFieldsPart;
	        		posParam  = this.fieldsPartParameters;

	        	} else if ( "(".equals( g ) ) {
	        		
	        		posBuffer.append( "(" );
	        		posBuffer.append( skipPair( this.originalQL, m, "(", ")", posParam ) );
	        		posBuffer.append( ")" );
	        		
	        	} else if ( "from".equals( word ) ) {
	        		
	        		posBuffer = sbFromPart;
	        		posParam  = fromPartParameters;
	        		
	        	} else if ( "where".equals( word ) ) {
	        		// N�o tr�s clausula from... a parte dos fields s�o o nome do objecto
	        		if( sbFromPart.length() == 0 ) {
	        			sbFromPart.append( sbFieldsPart );
	        			sbFieldsPart.delete(0,sbFieldsPart.length());
	        		}
	        		posBuffer = sbWherePart;
	        		posParam = this.wherePartParameters;
	        		
	        	} else if ( "group by".equals( word ) ) {
	        		
	        		posBuffer = sbGroupByPart;
	        		posParam = this.groupByPartParameters;
	        		
	        	} else if ( "order by".equals( word ) ) {
	        		
	        		posBuffer = sbOrderByPart;
	        		posParam = this.orderByPartParameters;
	        		
	        	} else {
	        		posBuffer.append( g );
	        	}
        	}
        }
        
        this.statementTypePart = sbStatementTypePart.toString().trim();
        this.hintsPart = sbHintsPart.toString().trim();
        this.fieldsPart = sbFieldsPart.toString().trim();
        this.objectPart = sbFromPart.toString().trim();
        this.wherePart = sbWherePart.toString().trim();
        this.groupByPart = sbGroupByPart.toString().trim();
        this.orderByPart = sbOrderByPart.toString().trim();

        if( sbFromPart.length() == 0 ) {
        	this.objectPart = sbFieldsPart.toString().trim();
        	this.fieldsPart = "";
        }        
        
        parseCommaSeparated( sbFieldsPart );

	}
	
	private String getGroupFromOriginal( StringBuffer original, Matcher m ) {
		return original.substring( m.start(), m.end() );
	}
	
	private void parseCommaSeparated( StringBuffer part ) {
		String g;
		StringBuffer fieldExpression = new StringBuffer();
		String 		  fieldAlias      = null;
		Matcher m = tokenizerRegExp.matcher( part );
        while (m.find())
        {
        	g = skipQuotedOrComment( part, m, getGroupFromOriginal( part, m) ).toString();
        	if ( "(".equals( g ) ) {
        		fieldExpression.append( "(" );
        		fieldExpression.append( skipPair( part, m, "(", ")", null ) );
        		fieldExpression.append( ")" );
        	} else if ( "[".equals( g ) ) {
        		fieldExpression.append( "[" );
        		fieldExpression.append( skipPair( part, m, "[", "]", null ) );
        		fieldExpression.append( "]" );
        	} else if ( "as".equals( g ) ) {
        		m.find();
        		while( " ".equals( m.group() ) ) {
        			m.find();
        		}
        		fieldAlias = m.group();
        		fieldAlias = fieldAlias.trim();
        	} else if ( ",".equals( g ) ) {
        		QLField x = new QLField( fieldAlias, fieldExpression.toString().trim() );
        		this.qlFields.put( x.getAlias(), x );
        		fieldExpression = new StringBuffer();
        	}
        	else {
        		fieldExpression.append( g );
        	}
        	
        	String finalExpr = fieldExpression.toString().trim();
        	if( finalExpr.length() > 0 ) {
        		QLField x = new QLField( fieldAlias, finalExpr );
        		this.qlFields.put( x.getAlias(), x );
        	}
        }
	}
	
	private void setParameter( List posParamList ) {
		try {
			if( this.originalQLParameters != null && this.originalQLParameters.size() > 0 )
				posParamList.add( this.originalQLParameters.get( this.paramIdx++ ) );
		}
		catch( Exception e ) {
			e.printStackTrace();
		}
	}
	
	private StringBuffer skipQuotedOrComment( StringBuffer original, Matcher m, String current ) {
		String g;
		String endChar;
		StringBuffer ret = new StringBuffer( current );
		if( "'".equals( current ) || "\"".equals( current ) || "/*".equals( current ) ) {
			
			if(  "/*".equals( current ) )
				endChar = "*/";
			else
				endChar = current;
			
			while( m.find() ) {
	        	//g = m.group();
	        	g = getGroupFromOriginal( original, m);
				ret.append( g );
	        	if( endChar.equals( g ) ) {
	        		break;
	        	}
			}
		}
		return ret;
	}
	
	private StringBuffer skipPair( StringBuffer original, Matcher m, String beginSeq, String endSeq, List posParamList ) {
		String g;
		int deep = 1;
		StringBuffer ret = new StringBuffer();
		while( m.find() ) {
        	g = skipQuotedOrComment( original, m, getGroupFromOriginal(original, m) ).toString();
        	if( "?".equals( g ) ) {
        		setParameter( posParamList );
        	}
        	else if( g.equals( endSeq ) ) {
        		deep --;
        		if( deep == 0 ) {
        			break;
        		}
        	} 
        	else if( beginSeq != null &&  beginSeq.equals( g ) ) 
        	{
				deep ++;
			}
			ret.append( skipQuotedOrComment( original, m, g ) );
			
		}
		return ret;
	}

	public String getWherePart() {
		return wherePart;
	}

	public void setWherePart(String wherePart) {
		this.wherePart = wherePart;
	}

	public String getObjectPart() {
		return objectPart;
	}

	public void setObjectPart(String objectPart) {
		this.objectPart = objectPart;
	}

	public String getOrderPart() {
		return orderByPart;
	}

	public void setOrderPart(String orderPart) {
		this.orderByPart = orderPart;
	}

	public String getGroupByPart() {
		return groupByPart;
	}

	public void setGroupByPart(String groupByPart) {
		this.groupByPart = groupByPart;
	}
	
	public String getStatementTypePart() {
		return statementTypePart;
	}

	public void setStatementTypePart(String statementTypePart) {
		this.statementTypePart = statementTypePart;
	}

	public String getHintsPart() {
		return hintsPart;
	}

	public void setHintsPart(String hintsPart) {
		this.hintsPart = hintsPart;
	}

	public String getFieldsPart() {
		return fieldsPart;
	}

	public void setFieldsPart(String fieldsPart) {
		this.fieldsPart = fieldsPart;
	}

	public String getOrderByPart() {
		return orderByPart;
	}

	public void setOrderByPart(String orderByPart) {
		this.orderByPart = orderByPart;
	}
	
	public List getFieldsPartParameters() {
		return fieldsPartParameters;
	}

	public void setFieldsPartParameters(List fieldsPartParameters) {
		this.fieldsPartParameters = fieldsPartParameters;
	}

	public List getWherePartParameters() {
		return wherePartParameters;
	}

	public void setWherePartParameters(List wherePartParameters) {
		this.wherePartParameters = wherePartParameters;
	}

	public List getGroupByPartParameters() {
		return groupByPartParameters;
	}

	public void setGroupByPartParameters(List groupByPartParameters) {
		this.groupByPartParameters = groupByPartParameters;
	}

	public List getOrderByPartParameters() {
		return orderByPartParameters;
	}

	public void setOrderByPartParameters(List orderByPartParameters) {
		this.orderByPartParameters = orderByPartParameters;
	}
	
	public String getOrinialQL() {
		return this.originalQL.toString();
	}
	
	public List getOriginalParameters() {
		return this.originalQLParameters;
	}

	public String getAfterSelect() {
		return afterSelect;
	}

	public void setAfterSelect(String afterSelect) {
		this.afterSelect = afterSelect;
	}

	public String getAfterWhere() {
		return afterWhere;
	}

	public void setAfterWhere(String afterWhere) {
		this.afterWhere = afterWhere;
	}

	public String getAfterOrderBy() {
		return afterOrderBy;
	}

	public void setAfterOrderBy(String afterOrderBy) {
		this.afterOrderBy = afterOrderBy;
	}

	public String toBOQL( List listOfParametersToFill ) {
		
		StringBuffer retBOQL = new StringBuffer();
		retBOQL.append( this.statementTypePart );
		if( this.hintsPart.length() > 0 ) {
			retBOQL.append( " /*+" ).append( this.hintsPart ).append( "*/" );
		}
		
		if( this.afterSelect != null )
			retBOQL.append(' ').append( this.afterSelect );

		if( this.fieldsPart.length() > 0 ) {
			retBOQL.append( " " ).append( this.fieldsPart );
			if( this.fieldsPartParameters != null )
				listOfParametersToFill.addAll( this.fieldsPartParameters );
		}

		if( this.objectPart.length() > 0 ) {
			if( fieldsPart.length() > 0 ) {
				retBOQL.append( " from " );				
			}
			else {
				retBOQL.append(' ');
			}
			retBOQL.append( this.objectPart );
			
			if(this.fromPartParameters != null ) {
				listOfParametersToFill.addAll( this.fromPartParameters );
			}
		}
		
		if( this.wherePart.length() > 0 ) {
			retBOQL.append( " where " ).append( this.wherePart );
			if( this.wherePartParameters != null )
				listOfParametersToFill.addAll( this.wherePartParameters );
		}

		if( this.afterWhere != null )
			retBOQL.append(' ').append( this.afterWhere );
		
		if( this.groupByPart.length() > 0 ) {
			retBOQL.append( " group by " ).append( this.groupByPart );
			if( this.groupByPartParameters != null )
				listOfParametersToFill.addAll( this.groupByPartParameters );
		}
		if( this.orderByPart.length() > 0 ) {
			retBOQL.append( " order by " ).append( this.orderByPart );
			if( this.orderByPartParameters != null )
				listOfParametersToFill.addAll( this.orderByPartParameters );
		}
		
		if( this.afterOrderBy != null ) 
			retBOQL.append(' ').append( afterOrderBy );
		String toRet=retBOQL.toString();
		
		return toRet;
	}
	
	private class QLField {
		public String expression;
		public String alias;
		
		public QLField( String alias, String expression) {
			this.expression = expression;
			if( alias != null ) {
				this.alias = alias;
			}
			else {
				this.alias = expression;
			}
		}
		
		public String getExpression() {
			return this.expression;
		}
		
		public String getAlias() {
			return this.alias;
		}
		
	}
}
