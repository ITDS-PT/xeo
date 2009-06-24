/*Enconding=UTF-8*/
package netgest.bo.system;
import java.util.Hashtable;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boMemoryArchive 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boMemoryArchive()
    {
    }

    private boPoolManager   poolManager;
    private Hashtable       emptyDataSets;
    private Hashtable       emptyObjectDataSets;
    private Hashtable		dataSetRelations;
    
    
    
    private boApplication 	p_application;
    
    public boMemoryArchive( boApplication app )
    {
        p_application = app;
        initialize();
    }
    
    public void initialize()
    {
        poolManager  = new boPoolManager();
        emptyDataSets = new Hashtable();
        emptyObjectDataSets = new Hashtable();
        dataSetRelations = new Hashtable();
    }
    
    
    public void setUserAttribute( EboContext ctx, Object key, Object Value)
    {
        
    }
    public Object getUserAttribute( EboContext ctx, Object key)
    {
        return null;
    }
    public void setSessionAttribute( EboContext ctx, Object key, Object Value )
    {
        
    }
    public Object getSessionAttribute( EboContext ctx, Object key)
    {
        return null;
    }
    public void setRequestAttribute( EboContext ctx, Object key, Object Value )
    {
        return;
    }
    public Object getRequestAttribute( EboContext ctx, Object key)
    {
        return null;
    }

    public void clearSessionData( boSession session ) 
    {
        return;
    }
    
    public void clearUserData( boUserSession user ) 
    {
        return;
    }
    
    public void clearRequestData( EboContext session ) 
    {
        return;
    }
    public void clearDefinitions() 
    {
        return;
    }
    public void clearPoliceRules() 
    {
        return;
    }
    public void clear()
    {
        return;
    }
    
    public boPoolManager getPoolManager()
    {
        return this.poolManager;
    }
    
    public Object getCachedEmptyDataSetForObject( String objName ) {
    	return emptyObjectDataSets.get( objName );    
    }

    public Object putCachedEmptyDataSetForObject( String objName ) {
    	return emptyObjectDataSets.put( objName, objName );    
    }
    
    public Object getCachedEmptyDataSet( String name )
    {
        return emptyDataSets.get( name );    
    }
    public void putCachedEmptyDataSet( String name, Object dataSet )
    {
    	emptyDataSets.put( name, dataSet );
    }
    
    public Object getCachedDataSetRelation( String name )
    {
        return dataSetRelations.get( name );    
    }
    public void putCachedDataSetRelation( String name, Object relations )
    {
    	dataSetRelations.put( name, relations );
    }
    
    public void clearCachedEmptyDataSet()
    {
        emptyDataSets.clear();
        emptyObjectDataSets.clear();
        dataSetRelations.clear();
    }
    
}