/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.util.ArrayList;
import java.util.Arrays;

import netgest.bo.builder.boBuildRepository;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.IDataBuilderDB;
import netgest.bo.plugins.IDataManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.plugins.data.MapType2Def.ObjectDS;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectFinder;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;


public class MapType2Plugin implements IDataPlugin
{
    private static Logger logger = Logger.getLogger( MapType2Plugin.class.getName() );
    
    public static final String FLAG_FIELD_NAME = "SYS_LOCAL_OBJECT";
    
    public IDataManager getDataManager( boDefHandler boDef )
    {
        if( boDef != null )
        {
            MapType2Def def = netgest.bo.plugins.data.MapType2Def.getDataSourceDefinition( boDef );
            if( def != null && "2".equals( def.getMapType() ) )
            {
                return new MapType2DataManager();
            }
        }
        return null;
    }

    public IDataBuilderDB getBuilderDB( boBuildRepository repository, boDefHandler boDef)
    {
        MapType2Def def = MapType2Def.getUndeployedDataSourceDefinition( repository, boDef );
        if( def != null && "2".equals( def.getMapType() ) )
        {
            return new MapType2DBBuilder();
        }
        return null;
    }

    public String getDataTableName(boBuildRepository repos, boDefHandler boDef)
    {
        return null;
    }

    public String getXeoTableName(boDefHandler boDef)
    {
        MapType2Def def = MapType2Def.getDataSourceDefinition( boDef );
        if( def != null && "2".equals( def.getMapType() ) )
        {
            return boDef.getBoPhisicalMasterTable()+"$M";
        }
        return null;
    }

    public boObject lookByPrimaryKey(EboContext boctx, String objectName, Object[] keys) throws boRuntimeException
    {
        boDefHandler boDef = boDefHandler.getBoDefinition( objectName );
        if( boDef != null )
        {
            MapType2Def def = MapType2Def.getDataSourceDefinition( boDef );
            if( def != null && "2".equals( def.getMapType() ) )
            {
                // Materalada Finders
                if( def.getFinders() == null || def.getFinders().length == 0 )
                {
                    return MapType2DataManager.immediateMap( boctx, objectName, keys );                
                }
                else
                {
                    return invokeFinder( boctx, objectName ,def, keys );
                }
            }
        }
        return null;
    }

    public boObjectFinder[] getFinders( boDefHandler boDef )
    {
        if( boDef != null )
        {
            MapType2Def def = MapType2Def.getDataSourceDefinition( boDef );
            if( def != null && "2".equals( def.getMapType() ) )
            {
                ArrayList toRet = new ArrayList();
                ObjectDS[] ds = def.getObjectDataSources().getDataSources();
                for (int d = 0; d < ds.length; d++) 
                {
                    if( ds[d].publishFinder() )
                    {
                        String[] keys = ds[d].getKeys();
                        String[] lbl  = new String[ keys.length ];
                        for (int i = 0; i < keys.length; i++) 
                        {
                            lbl[i] = boDef.getAttributeRef( keys[i].toLowerCase() ).getLabel();
                        }
                        toRet.add( new boObjectFinder( boObjectFinder.UNIQUE, ds[d].getName(), keys, lbl ) );
                    }
                }
                boObjectFinder[] xf = def.getFinders();
                if( xf != null )
                {
                    toRet.addAll( Arrays.asList( xf ) );
                }
                return (boObjectFinder[])toRet.toArray(new boObjectFinder[ toRet.size() ]);
            }
        }
        return null;
    }
    
    private boObject invokeFinder( EboContext boctx, String objectName, MapType2Def def, Object[] keys ) throws boRuntimeException
    {
        // Martelada Finders
        String      gdid = keys[0].toString().toUpperCase();
        Object[]    nKeys = new Object[ 2 ];
        boolean ok = true;
        if( gdid.startsWith( "E-" ) )
        {
            nKeys[0] = "E";
            nKeys[1] = gdid.substring(2);
        }
        else if ( gdid.startsWith( "S-" ) )
        {
            nKeys[0] = "S";
            nKeys[1] = gdid.substring(2);
        }
        else if ( gdid.startsWith( "I-" ) )
        {
            nKeys[0] = "I";
            nKeys[1] = gdid.substring(2);
        }
        else
        {
            ok = false;
        }
        
        boObject ret = null;
        if( ok )
        {
            ret = MapType2DataManager.immediateMap( boctx, objectName, nKeys );
        }
        return ret;
    }
    
}