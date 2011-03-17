/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.Serializable;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataSetMetaData implements Serializable, ResultSetMetaData
{
    /**
     * 
	 */
	private static final long serialVersionUID = -4045644629659243793L;

	/**
     * 
     * @since 
     */
    
    
    protected int       p_column_count;      // Column Count    

//    protected String    p_schema_name;
//    protected String    p_tablename;
   
    protected String[]  p_column_class_name;    // Column Class Names
    protected int[]     p_column_display_size;  // Column Display Size

//    protected String[]  p_column_label;

    protected String[]  p_column_name;          // Column Name
    protected int[]     p_column_type;          // Column SQL type
    protected String[]  p_column_type_name;     // Column type Name
    
    private   HashMap 	p_column_indexes;
    

//    protected int[]     p_column_precision;
//    protected int[]     p_column_scale;
//    protected boolean[] p_column_auto_increment;
//    protected boolean[] p_column_is_case_sensitive;
//    protected boolean[] p_column_is_currency;
//    protected boolean[] p_column_is_definitely_writable;
//    protected boolean[] p_column_is_nullable;
//    protected boolean[] p_column_is_readonly;
//    protected boolean[] p_column_is_searchable;
//    protected boolean[] p_column_is_singned;
//    protected boolean[] p_column_is_writable;
    
    public final void checkIndexOfColumn( int columnIndex )
    {
        if( columnIndex < 1 || columnIndex > p_column_count )
        {
            throw new DataException("0000",MessageLocalizer.getMessage("INVALID_COLUMN_INDEX"));   
        }
        return;
    }
    public final int findColumn( String columnName )
    {
        int i;
        for ( i = 0; i < p_column_name.length ; i++) 
        {
            if( p_column_name[i].equals( columnName ) )
            {
                break;
            }
        }
        i++;
        if( i > p_column_name.length )
        {
            i = -1;
        }
        return i;
    }
    
    public Map getColumnIndexes() {
    	if( p_column_indexes == null ) {
    		p_column_indexes = new HashMap( p_column_name.length );
    		for( int i = 0; i < p_column_name.length; i++ ) {
    			
    			if( p_column_name[i] != null ) {
    			p_column_indexes.put( p_column_name[i].toUpperCase(), new Integer( i + 1 ) );
    			}
    			
    		}
    	}
    	return p_column_indexes;
    }
    
    public DataSetMetaData( int columnCount , String[] columnName , String[] columnClassName , int[] columnDisplaySize , int[] columnType , String[] columnTypeName )
    {
    
        p_column_count = columnCount;
        p_column_name = columnName;
        p_column_class_name = columnClassName;
        p_column_display_size = columnDisplaySize;
        p_column_type = columnType;
        p_column_type_name = columnTypeName;
        
    }

    /**
     * 
     * @return 

     * @since 
     */
    public int getColumnCount()
    {
        return p_column_count;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isAutoIncrement(int column)
    {
        return false;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isCaseSensitive(int column)
    {
        return true;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isSearchable(int column)
    {
        return true;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isCurrency(int column)
    {
        checkIndexOfColumn(column);
        return p_column_class_name[column-1].equals("java.math.BigDecimal");
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public int isNullable(int column)
    {
        return 0;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isSigned(int column)
    {
        return true;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public int getColumnDisplaySize(int column)
    {
        checkIndexOfColumn(column);
        return p_column_display_size[ column - 1 ];
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getColumnLabel(int column)
    {
        checkIndexOfColumn(column);
        return p_column_name[ column - 1 ];
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getColumnName(int column)
    {
        checkIndexOfColumn(column);
        return p_column_name[ column - 1 ];
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getSchemaName(int column)
    {
        return null;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public int getPrecision(int column)
    {
        return 0;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public int getScale(int column)
    {
        return 0;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getTableName(int column)
    {
        return null;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getCatalogName(int column)
    {
        return null;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public int getColumnType(int column)
    {
        checkIndexOfColumn(column);
        return p_column_type[ column - 1 ];
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getColumnTypeName(int column)
    {
        checkIndexOfColumn(column);
        return p_column_type_name[ column - 1 ];
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isReadOnly(int column)
    {
        return false;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isWritable(int column)
    {
        return true;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public boolean isDefinitelyWritable(int column)
    {
        return true;
    }

    /**
     * 
     * @param column
     * @return 

     * @since 
     */
    public String getColumnClassName(int column)
    {
        checkIndexOfColumn(column);
        return p_column_class_name[ column - 1 ];
    }
    
    public String[] getColumnNames()
    {
        return p_column_name;
    }

    // Since JDK 1.6
    public boolean isWrapperFor(Class iface)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    }
    
    // Since JDK 1.6
    public Object unwrap(Class iface)  {
        throw new RuntimeException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
    } 
}
