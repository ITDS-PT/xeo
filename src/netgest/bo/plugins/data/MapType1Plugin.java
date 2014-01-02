/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.io.*;

import netgest.bo.builder.*;
import netgest.bo.def.*;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectFinder;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.plugins.IDataManager;
import netgest.bo.plugins.IDataBuilderDB;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.runtime.EboContext;


public class MapType1Plugin implements IDataPlugin
{
    private static Logger logger = Logger.getLogger( netgest.bo.plugins.data.MapType1Plugin.class.getName() );

    public IDataManager getDataManager( boDefHandler boDef )
    {
        if ( boDef != null ) 
        {
            MapType1Def def = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( boDef );
            if( def != null && "1".equals( def.getMapType() ) )
            {
                return new MapType1DataManager();
            }
        }
        return null;
    }
    
    public IDataBuilderDB getBuilderDB( boBuildRepository repository, boDefHandler boDef)
    {
        MapType1Def def = MapType1Def.getUndeployedDataSourceDefinition( repository, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            return new MapType1DBBuilder();
        }
        return null;
    }

    public boolean deployDatabase(boBuilder builder, boDefHandler bodef)
    {
        return true;
    }

    public String getDataTableName( boBuildRepository repos, boDefHandler boDef )
    {
        MapType1Def def = MapType1Def.getUndeployedDataSourceDefinition( repos, boDef );
        if( def != null && "1".equals( def.getMapType() ) )
        {
            if( def.haveLocalTable() )
            {
                return boDef.getBoPhisicalMasterTable()+"_LOCAL";
            }
            return boDef.getBoPhisicalMasterTable();
        }
        return null;
    }
    public String getXeoTableName(boDefHandler bodef)
    {
        return null;
    }

    public boObject lookByPrimaryKey(EboContext ctx, String objectName, Object[] keys) throws boRuntimeException
    {
        return null;
    }
    
    public boObjectFinder[] getFinders( boDefHandler boDef )
    {
        return null;
    }
}