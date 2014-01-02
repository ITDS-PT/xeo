/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.Serializable;

import java.util.*;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.utils.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
    /**
     * 
     * @since 
     */

public class DataSet extends ParametersHandler implements Serializable, Cloneable
{


    //    WARNING: 
    //    ATTENTION!!! WHEN ADD CLASS VARIABLES, CHECK Clone METHOD
    
    private   Map            	columnsIndexes;
    protected Hashtable            p_childRelations;
    protected DataSetRelations     p_relation;
    
    protected DataSetMetaData      metaData;
    
    protected boolean              wasChanged=false;
    
    private ArrayList p_rows;
    private ArrayList p_deletedRows;
    
    public DataSet( DataSetMetaData metaData )
    {
        this.p_rows     = new ArrayList();
        this.metaData = metaData;
        this.columnsIndexes = metaData.getColumnIndexes();
        
    }

    private DataSet( DataSetMetaData metaData, Map columnIndexes )
    {
        this.p_rows     = new ArrayList();
        this.metaData = metaData;
        this.columnsIndexes = columnIndexes;
    }
    
    public DataRow deletedRows( int position )
    {
        
        if(p_deletedRows == null || ( position <= 0 && position > p_deletedRows.size() ) )
        {
            throw new DataException("0000",MessageLocalizer.getMessage("DELETED_ROW_DOES_NOT_EXIST_AT_POSITION")+" ["+position+"]");
        }
        return (DataRow)p_deletedRows.get( position - 1 );
        
    }
    public int getDeletedRowsCount()
    {
        if( p_deletedRows == null) return 0;
        return p_deletedRows.size();
    }
    
    protected final int findColumnWithException( String columnName )
    {
        int colidx = findColumn( columnName );
        if( colidx == -1 )
        {
            throw new DataException("0000",MessageLocalizer.getMessage("THE_COLUMN")+" ["+columnName+"] "+MessageLocalizer.getMessage("DOESNT_EXIST")+".");
        }
        return colidx;
    }
    
    
    public final int findColumn( String columnName )
    {
        int ret = -1;
        Integer idx = (Integer)columnsIndexes.get( columnName );
        if( idx == null )
        {
            idx = (Integer)columnsIndexes.get( columnName.toUpperCase() );
        if( idx != null )
        {
            ret = idx.intValue();
            }
        } else {
        	ret = idx.intValue();
        }
        return ret;
    }

    public final DataSetMetaData getMetaData()
    {
        return metaData;        
    }
    public final int getRowCount()
    {
        return p_rows.size();
    }
    

    public final DataRow getParentRows( ) 
    {
        return null;
    }
    
    /**
     * 
     * Position of the row in the data set (starts at row 1)
     * 
     * @param position The position of the row (must be size >= position >= 1)
     * 
     * @return The row in the given position 
     */
    public final DataRow rows( int position ) 
    {
        checkPosition(position);
        return (DataRow)p_rows.get(position-1);
    }

    protected void insertFetchedRow( DataRow newRow )
    {
        p_rows.add( newRow );
        newRow.rownum = p_rows.size();
    }
    protected DataRow createRowToFetch()
    {
        return new DataRow( this );
    }

    public final DataRow createRow( ) 
    {
        DataRow ret = new DataRow( this );
        ret.isnew = true;
        return ret;
    }

    public synchronized final void insertRow( DataRow newRow )
    {
        if( newRow.rowset == this )
        {
            p_rows.add( newRow );
            newRow.rownum = p_rows.size();
        }
        else
        {
            throw new DataException("0000",MessageLocalizer.getMessage("ROW_IS_NOT_A_MEMBER_OF_THIS_DATASET"));
        }
    }
    public synchronized final void deleteRow( int position )
    {
        checkPosition( position );
        if( this.p_deletedRows == null ) this.p_deletedRows = new ArrayList();
        DataRow deletedRow = ( DataRow )this.p_rows.remove( position - 1 );
        if( !deletedRow.isNew() )
        {
            deletedRow.wasdeleted = true;
            this.p_deletedRows.add( deletedRow );
        }
    }
    

    private final void checkPosition( int position )
    {
        if(position <= 0 || position > p_rows.size()  )
        {
            throw new DataException("0000", MessageLocalizer.getMessage("ROW_DOESNT_EXIST")+" ["+position+"]");
        }
    }
    
    public final boolean wasChanged()
    {   
        if(!wasChanged)
        {
            for (int i = 0;!wasChanged && i < p_rows.size(); i++) 
            {
                 wasChanged = rows( i + 1 ).wasChanged();
            }
        }
        return wasChanged;
    }
    
    public final void moveRow(int srcPosition, int destPosition )
    {
        p_rows.add( destPosition - 1 , p_rows.remove( srcPosition - 1 ));
    }
    
    public final DataSet[] split() {
    	return split( this.getRowCount() );
    }
    
    public final DataSet[] split( int limit )
    {
    	int count = Math.min( this.getRowCount(), limit );
        DataSet[] ret= new DataSet[ count ];
        for (short i = 0; i < ret.length; i++) 
        {
            ret[i] = new DataSet( this.getMetaData(), this.columnsIndexes );
            ret[i].p_childRelations = this.p_childRelations;
            ret[i].p_relation       = this.p_relation;            
            DataRow row = (DataRow)this.p_rows.remove( 0 );
            row.rowset = ret[i];
            ret[i].insertRow( row );
        }
        return ret;
    }
    public DataSetRelations getDataSetRelations()
    {
        return p_relation;
    }
    public String[] getChildrenNames()
    {
        String[] ret=null;
        if( p_childRelations != null )
        {
            Set set = p_childRelations.keySet();
            ret = (String[])set.toArray( new String[set.size()] );
        }
        return ret;
    }
    
    public void reset()
    {
        this.wasChanged = false;    
        if( p_deletedRows != null ) p_deletedRows.clear();
        for (int i = 1; i <=  this.getRowCount(); i++) 
        {
            rows(i).reset();
        }
    }
//    public void refresh( EboContext ctx )
//    {
//        DataSet dataSet = cloneFromDataBase( ctx );
//        int i;
//        for ( i = 0; i < dataSet.getRowCount() ; i++) 
//        {
//            if( i < this.getRowCount() )
//            {
//                this.rows( i + 1 ).reset();
//                this.rows( i + 1 ).rowdata = dataSet.rows( i + 1 ).rowdata;
//                this.rows( i + 1 ).refreshChilds( ctx );
//            }
//        }
//        
//        if( i < this.getRowCount() )
//        {
//             while (i < this.getRowCount() ) 
//             {
//                this.deleteRow( i );
//             }
//        }
//        this.reset();
//        
//    }
//    public DataSet cloneFromDataBase( EboContext ctx )
//    {
//        DataSetReadRelation[] relations = p_relation.getReadRelations();
//        String[] dataSources = new String[ relations.length ]; 
//        String[] querys      = new String[ relations.length ];
//        ArrayList[] args        = new ArrayList[ relations.length ];
//        for (byte z = 0; z < relations.length; z++ ) 
//        {
//            DataSetReadRelation relation = relations[z];
//            String query = relation.sqlQuery;
//            querys[ z ]      = query;
//            dataSources[ z ] = relation.dataSource;
//            args[ z ]        = relation.arguments;
//        }
//        DataSet ret = DataManager.executeNativeQuery( ctx , dataSources , querys, 1, Short.MAX_VALUE , args );
//        ret.p_childRelations = (Hashtable)this.p_childRelations.clone();
//        
//        return ret;
//    }
    
    public final Object clone( ) throws CloneNotSupportedException
    {
        DataSet ret = (DataSet)super.clone();
        ret.p_rows          = new ArrayList( this.getRowCount() );
        for (int i = 0; i < this.getRowCount(); i++) 
        {
            ret.p_rows.add( (DataRow)this.rows( i + 1  ).clone( ret ) );
        }
        if( p_deletedRows != null )
        {
            ret.p_deletedRows = new ArrayList( p_deletedRows.size() );
            for (int i = 0 ; i < p_deletedRows.size() ; i++ ) 
            {
                ret.p_deletedRows.add( this.deletedRows( i + 1 ).clone( ret ) );
            }
        }
        return ret;
    }
    public final void setForInsert()
    {
        if( p_deletedRows!=null ) p_deletedRows.clear();
        for (int i = 0; i < getRowCount(); i++) 
        {
            rows(i + 1 ).setForInsert();
        }
        
    }

    public Hashtable getChildRelations()
    {
        return p_childRelations;
    }

    public void setChildRelations(Hashtable p_childRelations)
    {
        this.p_childRelations = p_childRelations;
    }

    public DataSetRelations getRelation()
    {
        return p_relation;
    }

    public void setRelation(DataSetRelations p_relation)
    {
        this.p_relation = p_relation;
    }
    
}

