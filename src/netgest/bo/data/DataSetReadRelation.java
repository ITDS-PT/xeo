/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataSetReadRelation implements Serializable
{
     protected String   sqlQuery;
     protected String   name;
     protected String[] parentFields;
     protected String[] childFields;
     protected String   dataSource;
     protected String   schema;
     
     protected ArrayList childNodes;
     
     public DataSetReadRelation( String name, String dataSource, String schema, String sqlQuery , String[] parentFields , String[] childFields )
     {
        this.name            = name;
        this.sqlQuery        = sqlQuery;
        this.parentFields    = parentFields;
        this.childFields     = childFields;
        this.dataSource      = dataSource;
        this.schema          = schema;
     }

    public void addDataSetRelations( DataSetRelations relations )
    {
        if( childNodes == null ) childNodes = new ArrayList();
        childNodes.add( relations );
    }

    public String getSqlQuery()
    {
        return sqlQuery;
    }


    public String getName()
    {
        return name;
    }


    public String[] getParentFields()
    {
        return parentFields;
    }


    public String[] getChildFields()
    {
        return childFields;
    }


    public String getDataSource()
    {
        return dataSource;
    }
}