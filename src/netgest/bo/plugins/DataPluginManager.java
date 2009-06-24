/*Enconding=UTF-8*/
package netgest.bo.plugins;
import java.util.Hashtable;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.plugins.data.MapType1Plugin;
import netgest.bo.plugins.data.MapType2Plugin;

public class DataPluginManager 
{
    private static Hashtable p_objectPlugIn = null;
    
    private static IDataPlugin[] plugIns = { new MapType1Plugin(), new MapType2Plugin() };
    
    public static void registerPlugin( String forObject, IDataPlugin plugIn )
    {
        if( p_objectPlugIn == null ) p_objectPlugIn = new Hashtable();
        p_objectPlugIn.put( forObject, plugIn );
    }
    
    public static IDataPlugin getPlugIn( String objectName )
    {
        return (IDataPlugin)p_objectPlugIn.get(objectName);
    }
    
    public static IDataPlugin[] getPlugIns()
    {
        return plugIns;
    }
    
}