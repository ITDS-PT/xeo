/*Enconding=UTF-8*/
package netgest.bo.data;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataSetRelations 
{
    /**
     * 
     * @since 
     */

    protected ArrayList p_read_relations;       // Handles the defintion to read childNodes
    protected ArrayList p_write_relations;    // Handles the definition how this node is write to physical storage
    protected Hashtable p_childRelations;
    
    protected String    p_name;

    public DataSetRelations( String name )
    {
        p_name = name;
    }
    
    public final void addReadRelation( DataSetReadRelation dataSetRelation )
    {
        if( p_read_relations == null ) p_read_relations = new ArrayList();
        p_read_relations.add( dataSetRelation );
    }
    
    public final void addWriteRelation( DataSetWriteRelation dataRelation )
    {
        if( p_write_relations == null ) p_write_relations = new ArrayList();
        p_write_relations.add( dataRelation );
    }
    
    public final void clearWriteRelations()
    {
        if( p_write_relations != null )
        {
            p_write_relations.clear();
        }
    }
    
    public final void clearReadRelations()
    {
        if( p_read_relations != null )
        {
            p_read_relations.clear();
        }
    }
    
    public final DataSetReadRelation[] getReadRelations( )
    {
        return p_read_relations==null?null:(DataSetReadRelation[])p_read_relations.toArray( new DataSetReadRelation[ p_read_relations.size() ] );
    }
    public final DataSetWriteRelation[] getWriteRelations()
    {
        return p_write_relations==null?null:(DataSetWriteRelation[])p_write_relations.toArray( new DataSetWriteRelation[ p_write_relations.size() ] );
    }

    public String getName()
    {
        return p_name;
    }

    public void setName(String p_name)
    {
        this.p_name = p_name;
    }
    
}