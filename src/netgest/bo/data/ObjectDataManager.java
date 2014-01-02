/*Enconding=UTF-8*/
package netgest.bo.data;

import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import netgest.bo.data.DataSetRelations;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boRepository;

import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class ObjectDataManager
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.data.ObjectDataManager");
	private static final int	LONG_MAX_SIZE = String.valueOf(Long.MAX_VALUE).length() - 1;

    /**
     *
     * @since
     */

    // Querys with boql
    public static final DataSet getEmptyDataSet(EboContext ctx, boDefHandler def)
    {
        DataSet ret = (DataSet) ctx.getApplication().getMemoryArchive()
                                   .getCachedEmptyDataSet(def.getName());

        if (ret == null)
        {
            ret = executeNativeQuery(ctx, def, " 0=1 ");

            boDefAttribute[] atts = def.getAttributesDef();
            DataRow row = ret.createRow();
            ret.insertRow(row);

            for (int i = 0; i < atts.length; i++)
            {
                if (atts[i].getDbIsTabled() ||
                        ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        (atts[i].getMaxOccurs() > 1)))
                {
                    row.getRecordChild(ctx, atts[i].getName());
                }
            }

            try
            {
                ctx.getApplication().getMemoryArchive().putCachedEmptyDataSet(def.getName(),
                    ret);
                ret = (DataSet) ret.clone();
            }
            catch (Exception e)
            {
                e = e;
            }
        }
        else
        {
            try
            {
                ret = (DataSet) ret.clone();
            }
            catch (Exception e)
            {
                e = e;
            }
        }

        return ret;
    }

    public static final DataSet createEmptyObjectBridgeDataSet(EboContext ctx, boDefHandler def, boDefAttribute defAtt ) {
    	
    	boDefBridge bodefBridge = defAtt.getBridge();
    	
    	DataSet result = (DataSet) ctx.getApplication().getMemoryArchive()
        	.getCachedEmptyDataSet(def.getName() + ":" + defAtt.getName() );
    	
    	if( result == null ) {
			boDefAttribute[] atts =  bodefBridge.getBoAttributes();			
			
			 
			
			String[] columnNames 		= new String[ atts.length + 2];
			String[] columnClassName 	= new String[ atts.length + 2]; 
			int[] columnDisplaySize 	= new int[ atts.length + 2]; 
			int[] columntype			= new int[ atts.length + 2];
			String[] columnTypeName 	= new String[ atts.length + 2];
			
			columnNames[0] = "PARENT$";
			columnClassName[0]=( "java.math.BigDecimal" );
			columntype[0] = DataTypes.NUMERIC;
			columnDisplaySize[0] = LONG_MAX_SIZE;
			
			columnNames[1] = "CHILD$";
			columnClassName[1]=( "java.math.BigDecimal" );
			columntype[1] = DataTypes.NUMERIC;
			columnDisplaySize[1] = LONG_MAX_SIZE;
			
			
			int cntr = 2;
			for( int i=0; i < atts.length; i++ ) {
				boDefAttribute att = atts[i];
				columnNames[cntr]=( att.getDbName() );
				if( att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE ) {
					// Tipo Objecto
					if( att.getRelationType() == boDefAttribute.RELATION_1_TO_1 ) {
						columnClassName[cntr]=( "java.math.BigDecimal" );
						columntype[cntr] = DataTypes.NUMERIC;
						columnDisplaySize[cntr] = LONG_MAX_SIZE;
						cntr++;
					}
				} else {
					// Tipo attribute
					switch( atts[i].getValueType() ) {
						case boDefAttribute.VALUE_BLOB:
							// unsupported:
							break;
						case boDefAttribute.VALUE_BOOLEAN:
							columnClassName[cntr]=( "java.lang.String" );
							columntype[cntr] = DataTypes.VARCHAR;
							columnTypeName[cntr] = "CHAR";
							columnDisplaySize[cntr] = 1;
							break;
						case boDefAttribute.VALUE_CHAR:
							columnClassName[cntr]=( "java.lang.String" );
							columntype[cntr] = DataTypes.VARCHAR;
							columnTypeName[cntr] = "CHAR";
							columnDisplaySize[cntr] = att.getLen();
							break;
						case boDefAttribute.VALUE_CLOB:
							columnClassName[cntr]=( "netgest.bo.data.DataClob" );
							columntype[cntr] = DataTypes.CLOB;
							columnTypeName[cntr] = "CLOB";
							columnDisplaySize[cntr] = Integer.MAX_VALUE;
							break;
						case boDefAttribute.VALUE_CURRENCY:
							columnClassName[cntr]=( "java.math.BigDecimal" );
							columntype[cntr] = DataTypes.NUMERIC;
							columnTypeName[cntr] = "NUMERIC";
							columnDisplaySize[cntr] = LONG_MAX_SIZE;
							break;
						case boDefAttribute.VALUE_DATE:
							columnClassName[cntr]=( "java.sql.Timestamp" );
							columntype[cntr] = DataTypes.TIMESTAMP;
							columnTypeName[cntr] = "DATE";
							columnDisplaySize[cntr] = 0;
							break;
						case boDefAttribute.VALUE_DATETIME:
							columnClassName[cntr]=( "java.sql.Timestamp" );
							columntype[cntr] = DataTypes.TIMESTAMP;
							columnTypeName[cntr] = "DATE";
							columnDisplaySize[cntr] = 0;
							break;
						case boDefAttribute.VALUE_DURATION:
							columnClassName[cntr]=( "java.math.BigDecimal" );
							columntype[cntr] = DataTypes.NUMERIC;
							columnDisplaySize[cntr] = LONG_MAX_SIZE;
							columnTypeName[cntr] = "NUMERIC";
							break;
						case boDefAttribute.VALUE_IFILELINK:
							columnClassName[cntr]=( "java.lang.String" );
							columntype[cntr] = DataTypes.VARCHAR;
							columnDisplaySize[cntr] = 255;
							columnTypeName[cntr] = "CHAR";
							break;
						case boDefAttribute.VALUE_NUMBER:
							columnClassName[cntr]=( "java.math.BigDecimal" );
							columntype[cntr] = DataTypes.NUMERIC;
							columnDisplaySize[cntr] = LONG_MAX_SIZE;
							columnTypeName[cntr] = "NUMERIC";
							break;
						case boDefAttribute.VALUE_SEQUENCE:
							columnClassName[cntr]=( "java.math.BigDecimal" );
							columntype[cntr] = DataTypes.NUMERIC;
							columnDisplaySize[cntr] = LONG_MAX_SIZE;
							columnTypeName[cntr] = "NUMERIC";
							break;
					}
					cntr ++;
				}
			}
		
			DataSetMetaData 
				m = new DataSetMetaData( cntr, columnNames, columnClassName, columnDisplaySize, columntype, columnTypeName );
			
			result = new DataSet( m );
			ctx.getApplication().getMemoryArchive().putCachedEmptyDataSet( def.getName() + ":" + defAtt.getName(), result ); 
    	}
		try {
			result = (DataSet)result.clone();
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException( e );
		}
		return result;
    	
    	
    }

    public static final DataSet createEmptyObjectDataSet(EboContext ctx, boDefHandler def) {
    	
    	DataSet result = (DataSet) ctx.getApplication().getMemoryArchive()
        .getCachedEmptyDataSet(def.getName());
    	
    	if( result == null ) {
    	
			boDefAttribute[] atts =  def.getAttributesDef();
			
			
			result = new DataSet( createMetaData( atts ) );
			ctx.getApplication().getMemoryArchive().putCachedEmptyDataSet( def.getName(), result ); 
			
    	}
		try {
			
			result = (DataSet)result.clone();
			
		} catch (CloneNotSupportedException e) {
			throw new RuntimeException( e );
		}
		return result;
    }
    
    private static final DataSetMetaData createMetaData( boDefAttribute[] atts ) {
		String[] columnNames 		= new String[ atts.length ];
		String[] columnClassName 	= new String[ atts.length ]; 
		int[] columnDisplaySize 	= new int[ atts.length ]; 
		int[] columntype			= new int[ atts.length ];
		String[] columnTypeName 	= new String[ atts.length ];
		
		int cntr = 0;
		for( int i=0; i < atts.length; i++ ) {
			boDefAttribute att = atts[i];
			columnNames[cntr]=( att.getDbName() );
			if( att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE ) {
				// Tipo Objecto
				if( att.getRelationType() == boDefAttribute.RELATION_1_TO_1 ) {
					columnClassName[cntr]=( "java.math.BigDecimal" );
					columntype[cntr] = DataTypes.NUMERIC;
					columnDisplaySize[cntr] = LONG_MAX_SIZE;
					cntr++;
				}
			} else {
				// Tipo attribute
				switch( atts[i].getValueType() ) {
					case boDefAttribute.VALUE_BLOB:
						// unsupported:
						break;
					case boDefAttribute.VALUE_BOOLEAN:
						columnClassName[cntr]=( "java.lang.String" );
						columntype[cntr] = DataTypes.VARCHAR;
						columnTypeName[cntr] = "CHAR";
						columnDisplaySize[cntr] = 1;
						break;
					case boDefAttribute.VALUE_CHAR:
						columnClassName[cntr]=( "java.lang.String" );
						columntype[cntr] = DataTypes.VARCHAR;
						columnTypeName[cntr] = "CHAR";
						columnDisplaySize[cntr] = att.getLen();
						break;
					case boDefAttribute.VALUE_CLOB:
						columnClassName[cntr]=( "netgest.bo.data.DataClob" );
						columntype[cntr] = DataTypes.CLOB;
						columnTypeName[cntr] = "CLOB";
						columnDisplaySize[cntr] = Integer.MAX_VALUE;
						break;
					case boDefAttribute.VALUE_CURRENCY:
						columnClassName[cntr]=( "java.math.BigDecimal" );
						columntype[cntr] = DataTypes.NUMERIC;
						columnTypeName[cntr] = "NUMERIC";
						columnDisplaySize[cntr] = LONG_MAX_SIZE;
						break;
					case boDefAttribute.VALUE_DATE:
						columnClassName[cntr]=( "java.sql.Timestamp" );
						columntype[cntr] = DataTypes.TIMESTAMP;
						columnTypeName[cntr] = "DATE";
						columnDisplaySize[cntr] = 0;
						break;
					case boDefAttribute.VALUE_DATETIME:
						columnClassName[cntr]=( "java.sql.Timestamp" );
						columntype[cntr] = DataTypes.TIMESTAMP;
						columnTypeName[cntr] = "DATE";
						columnDisplaySize[cntr] = 0;
						break;
					case boDefAttribute.VALUE_DURATION:
						columnClassName[cntr]=( "java.math.BigDecimal" );
						columntype[cntr] = DataTypes.NUMERIC;
						columnDisplaySize[cntr] = LONG_MAX_SIZE;
						columnTypeName[cntr] = "NUMERIC";
						break;
					case boDefAttribute.VALUE_IFILELINK:
						columnClassName[cntr]=( "java.lang.String" );
						columntype[cntr] = DataTypes.VARCHAR;
						columnDisplaySize[cntr] = 255;
						columnTypeName[cntr] = "CHAR";
						break;
					case boDefAttribute.VALUE_NUMBER:
						columnClassName[cntr]=( "java.math.BigDecimal" );
						columntype[cntr] = DataTypes.NUMERIC;
						columnDisplaySize[cntr] = LONG_MAX_SIZE;
						columnTypeName[cntr] = "NUMERIC";
						break;
					case boDefAttribute.VALUE_SEQUENCE:
						columnClassName[cntr]=( "java.math.BigDecimal" );
						columntype[cntr] = DataTypes.NUMERIC;
						columnDisplaySize[cntr] = LONG_MAX_SIZE;
						columnTypeName[cntr] = "NUMERIC";
						break;
				}
				cntr ++;
			}
		}
	
		DataSetMetaData 
			m = new DataSetMetaData( cntr, columnNames, columnClassName, columnDisplaySize, columntype, columnTypeName );
		
		return m;
    }
    

    public static final DataSet executeBOQL(EboContext ctx, String boql,
        ArrayList arguments, boolean cutWhere)
    {
        return executeBOQL(ctx, boql, 1, Short.MAX_VALUE, arguments, cutWhere);
    }

    public static final DataSet executeBOQL(EboContext ctx, String boql, boolean cutWhere)
    {
        return executeBOQL(ctx, boql, 1, Short.MAX_VALUE, null, cutWhere);
    }

    public static final DataSet executeBOQL(EboContext ctx, String boql,
        int page, int pageSize, ArrayList arguments, boolean cutWhere)
    {
        QLParser qp = new QLParser();
        String sqlquery;
        sqlquery = qp.getFromAndWhereClause(boql, ctx);
        if(cutWhere)
        {
            sqlquery = sqlquery.substring( sqlquery.toUpperCase().indexOf("WHERE") + 5 );
        }
        boDefHandler objdef = qp.getObjectDef();
        return executeNativeQuery(ctx, objdef, sqlquery, page, pageSize, arguments, true );
    }

    // Querys with sqlwhere
    public static final DataSet executeNativeQuery(EboContext ctx,
        boDefHandler objdef, String sqlquery, ArrayList arguments)
    {
        return executeNativeQuery(ctx, objdef, sqlquery, 1, Short.MAX_VALUE, arguments, false);
    }

    public static final DataSet executeNativeQuery(EboContext ctx,
        boDefHandler objdef, String sqlquery)
    {
        return executeNativeQuery(ctx, objdef, sqlquery, 1, Short.MAX_VALUE, null, false);
    }

    public static final DataSet executeNativeQuery(EboContext ctx,
        boDefHandler objdef, String sqlquery, int page, int pageSize, List arguments, boolean isboql )
    {
        
        IDataManager datam = null;
        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++) 
        {
            IDataManager pdatam  =  plugins[i].getDataManager( objdef );
            if( pdatam != null )
            {
                datam = pdatam;
            }
        }
        
        if( datam != null )
        {
            try
            {
                return datam.execute(ctx, objdef, sqlquery, page, pageSize, arguments, isboql);
            }
            catch (boRuntimeException e)
            {
                
            }
        }
        else
        {
            return DefaultDataManager.execute(ctx, objdef, sqlquery, page, pageSize, arguments, isboql);
        }
        return null;
    }

    public static boolean updateObjectData(boObject object)
        throws boRuntimeException
    {
        IDataManager datam = null;
        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++) 
        {
            IDataManager pdatam  =  plugins[i].getDataManager( object.getBoDefinition() );;
            if( pdatam != null )
            {
                datam = pdatam;
            }
        }
        if( datam != null )
        {
            return datam.updateObjectData( object );
        }
        else
        {
            return DefaultDataManager.updateObjectData( object );
        }
    
    }
}
