/*Enconding=UTF-8*/
package netgest.bo.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.utils.ParametersHandler;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class DataRow extends ParametersHandler implements Serializable, Cloneable
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final BigDecimal NULL_SIMPLE_TYPE = BigDecimal.valueOf(0);

    private boolean waschanged=false;
    protected boolean wasdeleted=false;
    protected boolean isnew=false;
    private boolean wasnull=false;

    protected Object[] rowdata;
    private Object[] olddata;

    protected int     rownum=-1;
    protected DataSet rowset;

    private Hashtable p_child_nodes;

    protected DataRow( DataSet data )
    {
        this.rowset = data;
        this.rowdata = new Object[this.rowset.metaData.p_column_count];
        
    }


    public final Object clone( DataSet parentDataSet ) throws CloneNotSupportedException
    {
        DataRow ret = (DataRow)super.clone();
        ret.rowdata = new Object[ this.rowdata.length ];
        System.arraycopy( this.rowdata, 0, ret.rowdata, 0, ret.rowdata.length );
        if( ret.olddata != null )
        {
            ret.olddata = new Object[ this.rowdata.length ];
            System.arraycopy( this.olddata, 0, ret.olddata, 0, ret.olddata.length );
        }
        if( this.p_child_nodes != null )
        {
            ret.p_child_nodes = new Hashtable();
            Enumeration oEnum = this.p_child_nodes.keys();
            while( oEnum.hasMoreElements() )
            {
                String name = (String)oEnum.nextElement();
                ret.p_child_nodes.put( name, ((DataSet)this.p_child_nodes.get( name )).clone() );
            }
            ret.rowset = parentDataSet;
        }
        return ret;
    }

    public final void setForInsert()
    {
        waschanged = false;
        isnew      = true;
        olddata    = null;
        DataSet[] childs = getLoadedChildRows();
        for (byte i = 0;childs!=null && i < childs.length; i++)
        {
            childs[i].setForInsert();
        }
    }

    public final void setChanged( boolean p_waschanged )
    {
        if( !waschanged && p_waschanged )
        {
            olddata = new Object[ rowdata.length ];
            System.arraycopy( rowdata , 0 , olddata , 0 , rowdata.length );
        }
        waschanged = true;
        rowset.wasChanged = true;
    }

    public final DataRow getFlashBackRow()
    {
        try
        {
            DataRow ret = null;
            if( this.olddata != null )
            {
                ret = (DataRow)this.clone();
                ret.rowdata = ret.olddata;
                ret.olddata = null;
            }
            return ret;
        }
        catch (CloneNotSupportedException e)
        {
            throw new DataException("0000",MessageLocalizer.getMessage("CLONENOTSUPPORTED_CLONING_OBJECT_DATAROW_IN_GETFLASHBACKROW"));
        }
    }
    public final int getRow()
    {
        return rownum;
    }

    public final boolean isNew()
    {
        return isnew;
    }
    public final boolean wasChanged()
    {
        DataSet[] childs = getLoadedChildRows();
        for (byte i = 0;!waschanged && childs != null && i < childs.length; i++)
        {
            waschanged = childs[i].wasChanged();
        }
        return waschanged;
    }
    public final boolean wasDeleted()
    {
        return wasdeleted;
    }
    public final boolean wasNull()
    {
        return wasnull;
    }
    // Hierac Methods;
    
    public final DataSet getRecordChild( EboContext ctx , String nodeName )
    {
        return getChildRows( ctx, nodeName );
    }

    public final DataSet getChildDataSet( EboContext ctx , String nodeName )
    {
        return getChildRows( ctx, nodeName );
    }
    
    public void addChildDataSet( String nodeName, DataSet dataSet ) {
        if( p_child_nodes == null )
        {
            p_child_nodes = new Hashtable();
        }
        p_child_nodes.put( nodeName, dataSet );
    }

    public final DataSet[] getLoadedChildRows()
    {
        DataSet[] ret = null;
        if( p_child_nodes != null )
        {
            ret = new DataSet[ p_child_nodes.size() ];
            Enumeration oEnum = p_child_nodes.elements();
            byte cnt=0;
            while( oEnum.hasMoreElements()  )
            {
                ret[ cnt++ ] = (DataSet)oEnum.nextElement();
            }
        }
        return ret;
    }

    public final void reset()
    {
        waschanged = false;
        wasdeleted = false;
        isnew      = false;
        olddata    = null;
        DataSet[] childs = getLoadedChildRows();
        for (byte i = 0; childs != null && i < childs.length; i++)
        {
            childs[i].reset();
        }
    }

    private final DataSet getChildRows( EboContext ctx , String nodeName )
    {
        DataSet ret = null;
        if( p_child_nodes != null )
        {
            ret = ( DataSet )p_child_nodes.get( nodeName );
        }
        else
        {
            p_child_nodes = new Hashtable();
        }
        if( ret == null )
        {
            DataSetReadRelation[] relations = ((DataSetRelations)this.rowset.p_childRelations.get( nodeName )).getReadRelations() ;
            if( relations != null )
            {
                String[] dataSources = new String[ relations.length ];
                String[] querys      = new String[ relations.length ];
                ArrayList[] args        = new ArrayList[ relations.length ];
                for (byte z = 0; z < relations.length; z++ )
                {
                    DataSetReadRelation relation = relations[z];
                    String query = relation.sqlQuery;
                    ArrayList nargs  = new ArrayList();
                    for (int i = 0;query.indexOf( "PARENT." ) > - 1 &&  i < this.rowset.metaData.p_column_count ; i++)
                    {
                        String toReplace = "PARENT." + this.rowset.metaData.p_column_name[i];
                        if( query.indexOf(toReplace) > -1 )
                        {
                            query = query.replaceAll( toReplace , "?" );
                            nargs.add( this.getObject( i + 1 ) );
                        }
                    }
                    querys[ z ]      = query;
                    dataSources[ z ] = relation.dataSource;
                    args[ z ]        = nargs;
                }
                ret = DataManager.executeNativeQuery( ctx , dataSources , querys, 1, Integer.MAX_VALUE , args );
                ret.p_relation       = (DataSetRelations)this.rowset.p_childRelations.get( nodeName );
                ret.p_childRelations = ret.p_relation.p_childRelations;
                p_child_nodes.put( nodeName , ret );
            }
        }
        return ret;
    }
    public final DataSet getDataSet()
    {
        return rowset;
    }

    // Get Methods
    public final String getString(String columnName)
    {
        return getString(this.rowset.findColumnWithException( columnName ));
    }

    public final String getString(int columnIndex)
    {
        return (String)getValue( columnIndex , DataTypes.VARCHAR , false );
    }

    public final BigDecimal getBigDecimal(String columnName)
    {
        return getBigDecimal(this.rowset.findColumnWithException( columnName ));
    }

    public final BigDecimal getBigDecimal(int columnIndex)
    {
        return (BigDecimal)getValue( columnIndex , DataTypes.NUMERIC , false );
    }

    public final Date getDate( String columnName )
    {
        return getDate(this.rowset.findColumnWithException( columnName ));
    }

    public final Date getDate( int columnIndex )
    {
        Timestamp ret = ((Timestamp)getValue( columnIndex , DataTypes.TIMESTAMP , false ));
        if(ret != null)
            return new Date(ret.getTime());
        return null;
    }

    public final double getDouble( String columnName )
    {
        return getDouble(this.rowset.findColumnWithException( columnName ));
    }

    public final double getDouble( int columnIndex )
    {
        return ((BigDecimal)getValue( columnIndex , DataTypes.NUMERIC , true )).doubleValue();
    }

    public final float getFloat( String columnName )
    {
        return getFloat(this.rowset.findColumnWithException( columnName ));
    }

    public final float getFloat( int columnIndex )
    {
        return ((BigDecimal)getValue( columnIndex , DataTypes.NUMERIC , true )).floatValue();
    }

    public final int getInt( String columnName )
    {
        return getInt(this.rowset.findColumnWithException( columnName ));
    }
    public final int getInt( int columnIndex )
    {
        return ((BigDecimal)getValue( columnIndex , DataTypes.NUMERIC , true )).intValue();
    }

    public final long getLong( String columnName )
    {
        return getLong(this.rowset.findColumnWithException( columnName ));
    }

    public final long getLong( int columnIndex )
    {
        return ((BigDecimal)getValue( columnIndex , DataTypes.NUMERIC , true )).longValue();
    }

    public final Object getObject( String columnName )
    {
        return getObject(this.rowset.findColumnWithException( columnName ));
    }

    public final Object getObject( int columnIndex )
    {
        return getValue( columnIndex , DataTypes.OBJECT , false );
    }

    public final Timestamp getTimestamp( String columnName )
    {
        return getTimestamp(this.rowset.findColumnWithException( columnName ));
    }

    public final Timestamp getTimestamp( int columnIndex )
    {
        return ((Timestamp)getValue( columnIndex , DataTypes.TIMESTAMP , true ));
    }

    public final byte[] getBytes( String columnName )
    {
        return getBytes(this.rowset.findColumnWithException( columnName ));
    }

    public final byte[] getBytes( int columnIndex )
    {
        return ((byte[])getValue( columnIndex , DataTypes.BINARY , false ) );
    }


    private final Object getValue( int columnIndex , int returnType , boolean notnull )
    {
        checkColumnIndex( columnIndex );
        Object value = checkValue( rowdata[columnIndex -1 ] , this.rowset.metaData.p_column_type[columnIndex - 1 ] , returnType );
        if( value == null && notnull && returnType == DataTypes.NUMERIC )
        {
            value = NULL_SIMPLE_TYPE;   // doesn't return null;
            wasnull = true;
        }
        else
        {
            wasnull = false;
        }
        return value;

    }

    // Update Methods
    public final void updateString(String columnName , String x )
    {
        updateString( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateString(int columnIndex, String x )
    {
        updateValue( columnIndex , DataTypes.VARCHAR , x );
    }

    public final void updateBigDecimal( String columnName, BigDecimal x )
    {
        updateBigDecimal( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateBigDecimal(int columnIndex, BigDecimal x)
    {
        updateValue( columnIndex , DataTypes.NUMERIC , x );
    }

    public final void  updateDate( String columnName , Date x )
    {
        updateDate( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateDate( int columnIndex , Date x )
    {
        updateValue( columnIndex , DataTypes.TIMESTAMP , x==null?null:new Timestamp( x.getTime() ) );
    }

    public final void updateDouble( String columnName , double x )
    {
        updateDouble( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateDouble( int columnIndex , double x )
    {
        updateValue( columnIndex , DataTypes.NUMERIC , new BigDecimal( x ) );
    }

    public final void updateFloat( String columnName , float x )
    {
        updateFloat( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateFloat( int columnIndex , float x )
    {
        updateValue( columnIndex , DataTypes.NUMERIC , new BigDecimal(x) );
    }

    public final void updateInt( String columnName , int x )
    {
        updateInt( this.rowset.findColumnWithException( columnName ) , x );
    }
    public final void updateInt( int columnIndex , int x )
    {
        updateValue( columnIndex , DataTypes.NUMERIC , BigDecimal.valueOf(x) );
    }

    public void updateLong( String columnName , long x )
    {
        updateLong( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateLong( int columnIndex , long x )
    {
        updateValue( columnIndex , DataTypes.NUMERIC , BigDecimal.valueOf(x) );
    }

    public void updateObject( String columnName , Object x )
    {
        updateObject( this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateObject( int columnIndex , Object x )
    {
        updateValue( columnIndex , DataTypes.OBJECT , x );
    }

    public void updateTimestamp( String columnName , Timestamp x )
    {
        updateTimestamp(this.rowset.findColumnWithException( columnName ) , x );
    }

    public final void updateTimestamp( int columnIndex , Timestamp x )
    {
        updateValue( columnIndex , DataTypes.TIMESTAMP , x );
    }

    public final void updateBytes( String columnName, byte[] x )
    {
        updateBytes(this.rowset.findColumnWithException( columnName ), x );
    }

    public final void updateBytes( int columnIndex, byte[] x )
    {
        updateValue( columnIndex , DataTypes.BINARY , x );
    }

    private final void updateValue( int columnIndex , int valueType , Object newValue )
    {
        checkColumnIndex( columnIndex );
        Object newvalue = checkValue( newValue , valueType , this.rowset.metaData.p_column_type[columnIndex-1] );

        if( newValue != null && this.rowset.metaData.p_column_type[columnIndex-1] == DataTypes.VARCHAR )
        {
            if( newValue.toString().length() > this.rowset.metaData.p_column_display_size[ columnIndex-1 ] )
            {
                throw new DataException( "0000", MessageLocalizer.getMessage("VALUE_TO_LARGE_FOR_COLUMN")+" ["+this.rowset.metaData.p_column_name[ columnIndex-1 ] +"]" );
            }
        }

        Object oldvalue = rowdata[ columnIndex - 1 ];
        if( !(newvalue == null && oldvalue == null) )
        {
            boolean equals = false;
            if ( newvalue != null && oldvalue != null )
            {
                if( valueType == DataTypes.BINARY )
                {
                    equals = Arrays.equals( (byte[])oldvalue, (byte[])newvalue );
                }
                else
                {
                    equals = oldvalue.equals( newvalue );
                }
            }
            if( !equals )
            {
                setChanged( true );
                rowdata[ columnIndex - 1 ] = newvalue;
            }
        }
    }

    public final void fetchColumn( String columnName , Object value )
    {
        fetchColumn( this.rowset.findColumn( columnName ) , value );
    }
    public final void fetchColumn( int columnIndex , Object value )
    {
        rowdata[ columnIndex - 1 ] = value;
    }

    // Convert Values
    private final void checkColumnIndex( int columnIndex )
    {
        if( columnIndex <= 0 || columnIndex > this.rowset.metaData.p_column_count )
        {
            throw new DataException("0000",MessageLocalizer.getMessage("INVALID_COLUMN"));
        }
    }

    private final static Object checkValue( Object value , int fromType, int dataType )
    {
        Object ret = value;
        if( value != null && fromType != dataType && dataType != DataTypes.OBJECT  )
        {
            switch ( fromType )
            {
                case DataTypes.OBJECT:
                    break;
                case DataTypes.VARCHAR:
                    switch( dataType )
                    {
                        case DataTypes.CLOB:
                            ret = new DataClob( (String) ret );
                            break;

                        case DataTypes.NUMERIC:
                            ret = new BigDecimal( (String)ret );
                            break;

                        case DataTypes.TIMESTAMP:
                            ret = Timestamp.valueOf( (String)ret );
                            break;

                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM")+" VARCHAR "+MessageLocalizer.getMessage("TO")+" ["+dataType+"]");
                    }
                    break;
                case DataTypes.NUMERIC:
                    switch( dataType )
                    {

                        case DataTypes.VARCHAR:
                            ret = ret.toString();
                            break;

                        case DataTypes.TIMESTAMP:
                            ret = new Timestamp( ((BigDecimal)ret).longValue() );
                            break;

                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM")+" NUMERIC "+MessageLocalizer.getMessage("TO")+" ["+dataType+"]");
                    }
                    break;
                case DataTypes.DATE:
                case DataTypes.TIMESTAMP:
                    switch( dataType )
                    {

                        case DataTypes.VARCHAR:
                            ret = ret.toString();
                            break;

                        case DataTypes.NUMERIC:
                            ret = BigDecimal.valueOf( ((Timestamp)ret).getTime() );
                            break;
                        case DataTypes.DATE:
                            break;
                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM")+" TIMESTAMP "+MessageLocalizer.getMessage("TO")+" ["+dataType+"]");
                    }
                    break;
                case DataTypes.BINARY:
                    switch( dataType )
                    {
                        case DataTypes.VARCHAR:
                            ret = ret.toString().getBytes();
                            break;
                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM")+" TIMESTAMP "+MessageLocalizer.getMessage("TO")+" ["+dataType+"]");
                    }
                case DataTypes.BLOB:
                    switch( dataType )
                    {
                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("BLOB_TYPE_IS_NOT_IMPLEMENTED"));
                    }
//                    break;
                case DataTypes.CLOB:
                    switch( dataType )
                    {
                        case DataTypes.VARCHAR:
                            ret = ret.toString();
                            break;

                        default:
                            // Error
                            throw new DataException("0000",MessageLocalizer.getMessage("CANNOT_CONVERT_FROM")+" CLOB "+MessageLocalizer.getMessage("TO")+" ["+dataType+"]");
                    }
                    break;
                default:
                    throw new DataException("0000",MessageLocalizer.getMessage("DataType Not supported")+" ["+fromType+"]");

            }
        }
        return ret;
    }
}