/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import netgest.bo.data.DataManager;
import netgest.bo.data.DataResultSet;
import netgest.bo.data.DataSet;
import netgest.bo.data.DriverUtils;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.ql.QLParser;
import netgest.bo.userquery.userquery;
import netgest.bo.utils.XEOQLModifier;

import org.apache.log4j.Logger;

import com.lowagie.text.pdf.ArabicLigaturizer;

public class boObjectListResultFactory  {

    public static final byte TYPE_BOQL=0;
    public static final byte TYPE_SQL=1;
    private static Logger logger = Logger.getLogger(boObjectListResultFactory.class);
    

    public static final DataResultSet getResultSetBySQL(EboContext ctx,String sql,Object[] sqlargs,String orderby,int page,int pagesize)
    {
        return getResultSet(ctx,TYPE_SQL,sql,null,"",page,pagesize,null,null, null,true );               
    }
    
    public static final DataResultSet getResultSetBySQL(EboContext ctx,String sql,Object[] sqlargs,String orderby,int page,int pagesize, boolean useSecurity)
    {
        return getResultSet(ctx,TYPE_SQL,sql,null,"",page,pagesize,null,null,null,useSecurity);               
    }
    
    public static final DataResultSet getResultSetByBOQL(EboContext ctx,String boql,Object[] sqlargs,String orderby,int page,int pagesize,String fulltext,String[] letter_filter, String userQuery,boolean useSecurity)
    {
        if( fulltext == null || fulltext.trim().length() == 0 )
        {
            return getResultSet(ctx,TYPE_BOQL,boql,sqlargs,orderby,page,pagesize,null,letter_filter,userQuery,useSecurity);
        }
        else
        {
            return getResultSet(ctx,TYPE_BOQL,boql,sqlargs,orderby,page,pagesize,fulltext,letter_filter,userQuery,useSecurity);
        }
    }
    
    private static DataResultSet getResultSet(EboContext ctx,byte type,String boql,
        Object[] sqlargs,String orderby,int page,int pagesize,String fulltext,
        String[] letter_filter,String userQuery,boolean useSecurity) 
    {
    	
        ArrayList args = argumentsToArrayList( sqlargs );
        if( args == null )
        {
            args = new ArrayList();
        }
    	String sql_to_execute = composeSqlQuery(ctx, type, boql, args, orderby, fulltext, letter_filter, userQuery, useSecurity);
        
        DataSet        dr=null;
        if( sql_to_execute.indexOf("Ocustomer") != -1 )
            sql_to_execute   = sql_to_execute.replaceAll( "SELECT  Ocustomer.\"BOUI\"","SELECT  \\*" );
        dr = DataManager.executeNativeQuery( ctx, "DATA", sql_to_execute, page, pagesize, args );

        return new DataResultSet( dr );
    }

    public static String composeSqlQuery( EboContext ctx, byte type,String boql,
            List args,String orderby,String fulltext,
            String[] letter_filter,String userQuery,boolean useSecurity  ) {
    	
    	String sql_to_execute;
    	
    	String onEnd = "";
    	
        
    	DriverUtils dutl = ctx.getDataBaseDriver().getDriverUtils();
    	
        if ( type==TYPE_BOQL )
        {
            QLParser qp = new QLParser();
            qp.toSql(boql,ctx,useSecurity);
            StringBuffer sb = new StringBuffer(boql);
            
            StringBuffer concatSql = new StringBuffer();
    
            if( fulltext != null && fulltext.length() > 0 )
            {
                if( letter_filter == null 
                    && 
                    userQuery== null 
                    && 
                    !qp.getObjectDef().implementsSecurityRowObjects() 
                )
                {
                    // Verifica se o textindex é ao objecto que é retornado da query ou que faz parte do from
                    Vector bouis;
                    if( qp.isTextIndexOnReturnObject() )
                    {
                        bouis = getTextIndexBouis( ctx, qp.getSelectedObjectDef(), fulltext );
                    }
                    else
                    {
                        bouis = getTextIndexBouis( ctx, qp.getObjectDef(), fulltext );
                    }
                    if( bouis != null )
                    {
                        StringBuffer textBouis = new StringBuffer();
                        for (int i = 0; i < bouis.size(); i++) 
                        {
                            if( i > 0 ) textBouis.append( ',');
                            textBouis.append( bouis.get( i ) );
                        }
                        if( bouis.size() > 0 )
                        {
                            if( !qp.isTextIndexOnReturnObject() )
                            {
                                concatSql.append( " BOUI in (" + textBouis.toString() + ") " );
                            }
                            else
                            {
                                concatSql.append( qp.getSelectedObjectName()+".BOUI in (" + textBouis.toString() + ") " );
                            }
                        }
                        else
                        {
                            concatSql.append(" 0=1 " );
                        }
                    }
                    else
                    {
                        concatSql.append( "contains '").append(fulltext).append('\'');
                    }
                }
                else
                {
                    concatSql.append( "contains '").append(fulltext).append('\'');
                }
            }
    
            if ( userQuery!= null)
            {
            	String u;
            	if( userQuery.trim().startsWith("<") ) {
            		u=userquery.userQueryToBoql_ClauseWhere( ctx, userQuery );
            	} else {
            		u = userQuery;
            	}
                if(u != null && u.length() > 0)
                {
                    if( concatSql.length() > 0 ) 
                    {
                        concatSql.append(" AND ");
                    }
                    
                    concatSql.append( "(" ).append( u ).append(")");
                }
            }
    
            if( letter_filter != null ) 
            {
                String sql = sb.toString();
                StringBuffer xsql = new StringBuffer();
                //apagar o order by do sql actual, pois não pode conter 2.
                if(sql.toUpperCase().indexOf("ORDER BY")>0)
                {
                    sql = sql.substring(0,sql.toUpperCase().indexOf("ORDER BY") );
                    sb.delete(0, sb.length());
                    sb.append(sql);
                }
    
                if( !letter_filter[1].equals("#") )
                {
                    xsql.append(letter_filter[0])
                        .append(" startswith '")
                        .append(letter_filter[1].toUpperCase())
                        .append("'");                    
                }
                else
                {
                    xsql.append('(')
                        .append(letter_filter[0])
                        .append(" startswith '#' )");
                }

                if( concatSql.length() > 0 )
                {
                    concatSql.append( " AND " );
                }
                concatSql.append( xsql );

                sb.append( " ORDER BY " );
                sb.append( letter_filter[0] );
                
                
            }
            else if ( orderby != null && orderby.trim().length() > 0)   
            {
            	orderby = orderby.trim();
				
                XEOQLModifier qm = new XEOQLModifier( sb.toString(), args );
            	String wp = qm.getWherePart();
            	
            	
                switch( dutl.getQueryLimitStatementPosition() ) {
                	case DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT:
                		onEnd = dutl.getQueryLimitStatement( 5000 );
                		break;
                	case DriverUtils.QUERY_LIMIT_ON_SELECT_CLAUSE:
                		qm.setAfterSelect( dutl.getQueryLimitStatement( 5000 ) );
                		break;
                	case DriverUtils.QUERY_LIMIT_ON_WHERE_CLAUSE:
                		//TODO: On where clause only suports Oracle sintaxe;
                    	if( wp.length() > 0 ) {
                    		wp += " AND [rownum] < 5000";
                    	} else {
                    		wp += " [rownum] < 5000";
                    	}
                		break;
                }
            	
            	String objAtt = orderby;
            	String uOrderBy = orderby.toUpperCase();
        		if ( uOrderBy.endsWith(" DESC") ) {
        			objAtt = orderby.substring(0, orderby.length() - 5 );
        		} else if ( uOrderBy.endsWith(" ASC") ) {
        			objAtt = orderby.substring(0, orderby.length() - 4 );
        		}
        		
        		boDefAttribute defAtt;
        		
        		defAtt = qp.getObjectDef().getAttributeRef( objAtt ); 
        		
            	if( orderby.indexOf(".") == -1 && defAtt == null ) {
            		orderby = "[" + orderby + "]";
            	}
            	else {
            		if( defAtt.getBridge() != null ) {
            			String fp = qm.getFieldsPart();
            			if( fp.length() == 0 ) {
                			qm.setFieldsPart( "[distinct BOUI]," + objAtt );
            			} else {
                			qm.setFieldsPart( "[distinct " + objAtt + "]," + fp );
            			}
            		}
            	}
            	qm.setWherePart( wp );
            	qm.setOrderByPart( orderby );
            	
            	sb = new StringBuffer( qm.toBOQL( new ArrayList() ) );
                //sb.append( orderby );
            }
            if( concatSql.length() > 0 )
            {
            	
                XEOQLModifier qm = new XEOQLModifier( sb.toString(), args );
                String wherePart = qm.getWherePart();
                if( wherePart.length() > 0 ) {
                	wherePart ="(" + wherePart + ") and (" +  concatSql.toString()  + ")";
                }
                else {
                	wherePart +=  concatSql.toString() ;
                }
            	qm.setWherePart( wherePart );
            	sb = new StringBuffer( qm.toBOQL( new ArrayList(1) ) );  
            	
            }
            sql_to_execute = qp.toSql(sb.toString(),ctx,useSecurity) + " " + onEnd;
        }
        else
        {
            sql_to_execute = boql;
        }
        
    	return sql_to_execute;
    }

    
    
    public static long getRecordCount(EboContext ctx,String boql, Object[] sqlargs, String fulltext, String[] letter_filter, String userQuery, boolean useSecurity) {
        Connection        cn=null;
        PreparedStatement pstm=null;
        ResultSet         rslt=null;
        QLParser          qlp = new QLParser(  );
        long              nr=0;
        
        try {
            
            ArrayList args = argumentsToArrayList( sqlargs );
            if( args == null )
            {
                args = new ArrayList();
            }
        	
        	String sql = composeSqlQuery(ctx, TYPE_BOQL, boql, args, "", fulltext, letter_filter, userQuery, useSecurity);
            sql = "SELECT COUNT(*) FROM ("+sql+") AS SQL_COUNT";
            cn = ctx.getConnectionData();
            pstm = cn.prepareStatement(sql);

            
            for (int i = 0; i < args.size(); i++) 
            {
              pstm.setObject(i+1, args.get(i));
            }
            
            rslt = pstm.executeQuery();
            rslt.next();
            nr = rslt.getLong(1);
        } catch (SQLException e) {
            throw new boRuntimeException2("boObjectListResultFactory.getRecordCount( ctx , boql ) "+ e.getMessage());
        } finally {
            if(rslt!=null) try{rslt.close();}catch(SQLException e){ throw new boRuntimeException2("boObjectListResultFactory.getRecordCount( ctx , boql ) "+ e.getMessage()); }
            if(pstm!=null) try{pstm.close();}catch(SQLException e){ throw new boRuntimeException2("boObjectListResultFactory.getRecordCount( ctx , boql ) "+ e.getMessage()); }
        }
        return nr;
    }
    
    private static final ArrayList argumentsToArrayList( Object[] args )
    {
        ArrayList ret=null;
        if( args != null )
        {
            ret = new ArrayList( args.length );
            for (byte i = 0; i < args.length ; i++) 
            {
                ret.add( args[i] );
            }
        }
        return ret;
    }
    
    
    
    private static Vector getTextIndexBouis( EboContext ctx, boDefHandler bodef, String textQuery )
    {
    
        try
        {
        
            Vector      bouis   = new Vector();
            StringBuffer sqlTextIndex = new StringBuffer(  );

            final int maxResults = 50;
            
            DriverUtils dutl = ctx.getDataBaseDriver().getDriverUtils();
            String limitOnSelect = "";
            String limitOnWhere = "";
            String limitOnEnd = "";
            switch( dutl.getQueryLimitStatementPosition() ) {
            	case DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT:
            		limitOnEnd = dutl.getQueryLimitStatement( maxResults );
            		break;
            	case DriverUtils.QUERY_LIMIT_ON_SELECT_CLAUSE:
            		limitOnSelect = dutl.getQueryLimitStatement( maxResults );
            		break;
            	case DriverUtils.QUERY_LIMIT_ON_WHERE_CLAUSE:
            		limitOnWhere = " AND " + dutl.getQueryLimitStatement( maxResults );
            		break;
            }
            
            sqlTextIndex.append( "SELECT ")
            .append( limitOnSelect  )
            .append("ui$,uiclass from OEbo_TextIndex where " );
            
            String textIndexUiclasses = QLParser.textIndexUiClass(bodef.getName());
            if(textIndexUiclasses != null && textIndexUiclasses.length() > 0)
            {
                sqlTextIndex.append(textIndexUiclasses).append(" AND ");
            }
            sqlTextIndex.append( dutl.getFullTextSearchWhere( "text" , "?") );

            int textFnd = 0;
            sqlTextIndex.append( limitOnWhere );
            sqlTextIndex.append( ' '  ).append( limitOnEnd );
            
            long init = System.currentTimeMillis();
            
            
            PreparedStatement   pstm = ctx.getConnectionData().prepareStatement( sqlTextIndex.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY );
            pstm.setString( 1, textQuery );


            pstm.setFetchSize( maxResults );
            
            ResultSet           rslt = pstm.executeQuery();
            
            
            while( textFnd < maxResults && rslt.next() )
            {
                textFnd ++;
                bouis.add( rslt.getBigDecimal(1) );
            }
            if( textFnd == maxResults )
            {
                bouis = null;
            }
            rslt.close();
            pstm.close();
            
            if( System.currentTimeMillis() - init > 5000 )
            {
                logger.error("Query demorada TextIndex["+sqlTextIndex+"]Text:["+textQuery+"] Tempo:["+(System.currentTimeMillis() - init)+"]");
            }
            
            return bouis;
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2( e );
        }
    }
    
    
    private static Vector getAllClassNames( boDefHandler bodef )
    {
        Vector      classes = new Vector();
        
        if( bodef.getClassType() == boDefHandler.TYPE_INTERFACE )
        {
            boDefInterface bodefInterface = (boDefInterface)bodef;
            String classNames[] = bodefInterface.getImplObjects();
            for (int i = 0; i < classNames.length; i++) 
            {
                boDefHandler   cDef         = boDefHandler.getBoDefinition( classNames[i] );
                if( !classes.contains( cDef.getName() ) )
                {
                    classes.add( cDef.getName() );
                }
                boDefHandler[] childClasses = cDef.getTreeSubClasses();
                for (int j=0;j < childClasses.length; j++ ) 
                {
                    if( !classes.contains( childClasses[j].getName() ) )
                    {
                        classes.add( childClasses[j].getName() );
                    }
                }
            }
        }
        else
        {
            boDefHandler childClasses[] =  bodef.getTreeSubClasses();
            classes.add( bodef.getName() );
            for (int j=0;j < childClasses.length; j++ ) 
            {
                classes.add( childClasses[j].getName() );
            }
        }
        return classes;
    }
    
    private static final void concatExpression(StringBuffer sb, String exp)
    {
    	
        String boql = sb.toString();
        sb.delete(0, sb.length());
        int wherePos = boql.toUpperCase().indexOf("WHERE");
        if(wherePos > -1)
        {
            sb.append(boql.substring(0, wherePos) )
                .append(" where ")
                .append(exp)
                .append(" (");
            int nextPoint = boql.toUpperCase().indexOf("GROUP BY") == -1 ? boql.toUpperCase().indexOf("ORDER BY") :boql.toUpperCase().indexOf("GROUP BY");  
            if(nextPoint > -1)
            {
                sb.append(boql.substring(wherePos + 5, nextPoint))
                    .append(")")
                    .append(boql.substring(nextPoint));
            }
            else
            {
                sb.append(boql.substring(wherePos + 5)).append(")");
            }
        }
        else
        {
            int order = boql.toUpperCase().indexOf("GROUP BY") == -1 ? boql.toUpperCase().indexOf("ORDER BY") :boql.toUpperCase().indexOf("GROUP BY");
            if(order > -1)
            {
                sb.append(boql.substring(0, order) )
                    .append(" where ")
                    .append(exp)
                    .append(boql.substring(order));
            }
            else
            {
                sb.append(boql)
                    .append(" where ")
                    .append(exp);
            }
        }
    }
    
    private static final String concatFields(String[] values) 
    {
        StringBuffer x = new StringBuffer();
        x.append('(');
        for (short i = 0; i < i-1; i++)  
        {
            x.append('\'').append(values).append('\'').append('\'');
        }
        x.append('\'').append(values).append('\'');
        x.append(')');
        return x.toString();
    }
    
}