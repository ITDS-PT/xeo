/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.io.File;
import java.util.*;

import netgest.bo.boConfig;
import netgest.bo.builder.*;
import netgest.bo.data.Driver;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.plugins.IDataBuilderDB;
import netgest.bo.plugins.data.MapType2Def;
import netgest.bo.plugins.data.MapType2Def.ObjectDS;
import netgest.bo.plugins.data.MapType2Def.ObjectDataSource;

import netgest.utils.DataUtils;
import netgest.utils.ngtXMLUtils;

public class MapType2DBBuilder implements IDataBuilderDB 
{
    private EboContext          p_ctx;
    private boBuildDB           p_bdb;
    private boBuildRepository   p_repository;
    
    private Hashtable           p_extraAtts;
    private ArrayList           p_extraFlds;
    
    public void initialize(EboContext ctx, boBuildDB dbbuilder, boBuildRepository repository, int mode, Hashtable objectInterfaceMap, boolean createdFwdMethods)
    {
        p_ctx           = ctx;
        p_bdb           = dbbuilder;
        p_repository    = repository;
        netgest.bo.plugins.data.MapType2Def.clearCache();
    }

    public String getPhisicalTableName(boDefHandler boDef)
    {
        // O nome da tabela fisica local fica é acrecentado o sufixo
        // para não entrar em conflito com a View que junta os dois objectos.
        return boDef.getBoPhisicalMasterTable();
    }

    public void beforeDataBaseScripts( boDefHandler boDef)
    {
        deployDataSource( boDef );

        // Cria o campo que serve para saber se o objecto é local ou remoto.
        MapType2Def mapdef = MapType2Def.getDataSourceDefinition( boDef );
            
        p_bdb.addField( p_bdb.getTableName(), 
                        MapType2Plugin.FLAG_FIELD_NAME, 
                        "char(1)", 
                        "Field internal used to know if the object is local or remote" ,
                        true,
                        "X", 
                        null,
                        "Y"
                    );
                    
        if ( mapdef.getObjectDataSources().getDataSources().length > 1 )
        {
            ObjectDS[] ads = mapdef.getObjectDataSources().getDataSources();
            for (int i = 0; i < ads.length; i++ ) 
            {
                p_bdb.addIndex( 
                                    "IDX_"+p_bdb.getTableName()+"_MAP_"+i,"Unique of remote Objects", 
                                    p_bdb.getTableName(),
                                    DataUtils.concatFields( 
                                    		p_ctx.getDataBaseDriver(), ads[i].getKeys() 
                                    )
                                );
            }
        }
        else
        {
            p_bdb.addUnique( 
                                "IDX_"+p_bdb.getTableName()+"_MAPPING","Unique of remote Objects", 
                                p_bdb.getTableName(),
                                DataUtils.concatFields( 
                                		p_ctx.getDataBaseDriver(), mapdef.getObjectDataSources().getDataSources()[0].getKeys() 
                                )
                            );
        }
        buildAttributes( boDef );
                                
        buildExternalTables( boDef );
        
    }
    
    
    public void afterDataBaseScript(boDefHandler boDef)
    {
        
    }

    public void inheritObject(boDefHandler boDef)
    {
    }

    public void beforeInheritViewes(boDefHandler boDef)
    {
    } 

    public void afterInheritViewes(boDefHandler boDef)
    {
        try
        {
            MapType2Def def = MapType2Def.getDataSourceDefinition( boDef );
            String schedName    = "MAP_REGISTER_" + boDef.getName();
            EboContext ctx      = this.p_ctx;
            boObject scheduleObj = boObject.getBoManager().loadObject(
                                        ctx,
                                        "Ebo_Schedule",
                                        "ID=?",
                                        new Object[] { schedName }
                                    );
            if( def.getPreRegisterObjects() )
            {
                p_bdb.logger.finer(" Adding Register Schedule for mapping:" + boDef.getName() );
                scheduleObj.getAttribute( "id" ).setValueString( schedName );
                scheduleObj.getAttribute( "description" ).setValueString( "Pré registo do Objecto mapeado " + boDef.getName() );
                scheduleObj.getAttribute( "javaclass" ).setValueString( "netgest.bo.plugins.data.MapType2Register" );
                scheduleObj.getAttribute( "parameters" ).setValueString( boDef.getName() );
                scheduleObj.getAttribute( "state" ).setValueString( "1" );
                if(!scheduleObj.exists())
                {
                    scheduleObj.getAttribute( "type" ).setValueString( "REPEATLY" );
                    scheduleObj.getAttribute( "interval" ).setValueString( def.getScheduleInterval() );                
                    scheduleObj.getAttribute( "every" ).setValueString( "ALLDAYS" );
                }
                scheduleObj.update();
            }
            else if( scheduleObj.exists() )
            {
                scheduleObj.destroy();
            }
            
            ctx         = this.p_ctx;
            schedName   = "MAP_TEXTINDEX_" + boDef.getName();
            scheduleObj = boObject.getBoManager().loadObject( 
                                        ctx, 
                                        "Ebo_Schedule",
                                        "ID=?",
                                        new Object[] { schedName }
                                    );
                                    
            if( def.getSyncTextIndex() )
            {
                p_bdb.logger.finer(" Adding TextIndex Schedule for mapping:" + boDef.getName() );
                scheduleObj.getAttribute( "id" ).setValueString( schedName );
                scheduleObj.getAttribute( "description" ).setValueString( "Sincronização Ebo_TextIndex do Objecto mapeado "+boDef.getName() );
                scheduleObj.getAttribute( "javaclass" ).setValueString( "netgest.bo.plugins.data.MapType2SyncTextIndex" );
                scheduleObj.getAttribute( "parameters" ).setValueString( boDef.getName() );
                scheduleObj.getAttribute( "type" ).setValueString( "REPEATLY" );
                scheduleObj.getAttribute( "interval" ).setValueString( def.getSyncTextIndexInterval() );
                scheduleObj.getAttribute( "every" ).setValueString( "ALLDAYS" );
                scheduleObj.getAttribute( "state" ).setValueString( "1" );
                scheduleObj.update();
            }
            else if( scheduleObj.exists() )
            {
                scheduleObj.destroy();
            }
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
        
    
    }
    
    public void addViewFields(ArrayList flds, boDefHandler boDef)
    {
        MapType2Def def = MapType2Def.getUndeployedDataSourceDefinition( p_repository, boDef );
        if( def != null && "2".equals( def.getMapType() ) )
        {
           flds.add( MapType2Plugin.FLAG_FIELD_NAME );
        }

        buildAttributes( boDef );

        if( p_extraFlds != null )
        {
            flds.addAll( p_extraFlds );
        }
        
    }
    
    private void buildExternalTables( boDefHandler undeployedDef )
    {
        // Le os DataSources do Object
        boDefHandler       deployedDef  = boDefHandler.getBoDefinition( undeployedDef.getName() );
        MapType2Def        allds        = MapType2Def.getUndeployedDataSourceDefinition( p_repository, deployedDef );
        ObjectDataSource   ods          = allds.getObjectDataSources();
        
        ObjectDS allods[] = ods.getDataSources();
        StringBuffer viewSql = new StringBuffer();
        
        for (int d = 0; d < allods.length; d++) 
        {
            if( d > 0 )
            {
                viewSql.append( " \n\t UNION ALL \n\t " );
            }
            
            ObjectDS          ds    = allods[d];
            
            // Lê os Atributos Mapeados
            String[]    rematts = ds.getRemoteAttributes();
            String[]    sqlatts = ds.getSQLExpressions();
            String[]    locatts = ds.getLocalAttributes();

            String[][]  objkeys     = ds.getObjectRelationLocalKeys();
            
            // Lê as chaves do Objecto
            String[] keys   = ds.getKeys();
//            String[] keysl  = ds.getLocalKeys();
            
            // Constroi dos Hashtables com os atributos
            // para facilitar os próximos passos
            Hashtable hrematts = new Hashtable();
            Hashtable hlocatts = new Hashtable();
            
            for (int i = 0; i < rematts.length; i++) 
            {
                boDefAttribute attDef = deployedDef.getAttributeRef( locatts[i] );
                if( attDef != null )
                {
                    if( rematts[i] != null )
                    {
                        hrematts.put( rematts[i].toUpperCase(), new Integer(i) );
                    }
                    hlocatts.put( attDef.getDbName(), new Integer(i) );
                }
            }
            
            // Array com todos os attributos necessários ao object
            String[] a_atts = p_bdb.getObjectAttributes( deployedDef );
    
            boolean appendComma;
            if( d == 0 && allds.canHaveLocalObjects() )
            {
                // SQL Para objectos locais, só para o primeiro mapeamento.
                StringBuffer localSql = new StringBuffer();
                localSql.append( " SELECT " );
        
                appendComma = false;
                for (int k=0; k < a_atts.length ; k++ ) 
                {
                    if( appendComma )
                    {
                        localSql.append( ", " );
                    }
                    String attname = a_atts[k];
                    localSql.append( "\"" );
                    localSql.append( attname.toUpperCase()  );
                    localSql.append( "\"" );
                    appendComma = true;
                }
                localSql.append( " FROM " ).append( p_bdb.getTableName() );
                localSql.append( " WHERE "+MapType2Plugin.FLAG_FIELD_NAME+"='Y' " );

                viewSql.append( localSql.toString() );
                viewSql.append( "\n\r UNION ALL \n\r" );
                
            }
    
            // SQL para objectos remotos
            StringBuffer remSql = new StringBuffer();
            remSql.append( " SELECT " );
            
            appendComma = false;
            for (int k=0; k < a_atts.length ; k++ ) 
            {
                if( appendComma )
                {
                    remSql.append( ", " );
                }
                String attname = a_atts[k];
                
                // Verifica se o Atributo está mapeado. Se sim terá de ser lido da tabela remota.
                if( hlocatts.get( attname.toUpperCase() ) != null )
                {
                    int attPos = ((Integer)hlocatts.get( attname.toUpperCase())).intValue();
                    // Verifica se este mapeamento, corresponde a uma relação com um object remoto.
                    // Se sim mapeia as chaves que correspondem a primary key do objecto.
                    if( objkeys[ attPos ] != null )
                    {
                        remSql.append( p_bdb.getTableName() ).append( "." );
                        remSql.append( "\"" );
                        remSql.append( attname.toUpperCase()  );
                        remSql.append( "\"" );
                    }
                    else
                    {   
                        // Atributo que é um valor... 
                        if( sqlatts[ attPos ] != null && !sqlatts[ attPos ].equals( rematts[ attPos ] ) )
                        {
                            remSql.append( sqlatts[ attPos ]  );
                        }
                        else
                        {
                            remSql.append( ds.getSourceObject() ).append( "." );
                            remSql.append( rematts[ attPos ]  );
                        }
                        remSql.append( " AS " );
                        remSql.append("\"");
                        remSql.append( locatts[ attPos ].toUpperCase()  );
                        remSql.append("\"");
                    }
                    
                }
                else
                {
                    if( p_extraAtts.get( attname ) == null )
                    {
                        remSql.append( p_bdb.getTableName() ).append( "." );
                        remSql.append( "\"" );
                        remSql.append( attname.toUpperCase()  );
                        remSql.append( "\"" );
                    }
                    else
                    {
                        String[] objrelatt = (String[])p_extraAtts.get( attname );
                        int attPos = ((Integer)hlocatts.get( objrelatt[1].toUpperCase())).intValue();
                        if( objkeys[ attPos ] != null )
                        {
                            for (int z=0;z < objkeys[attPos].length ; z++ )  
                            {
                                if( objkeys[attPos][z] != null )
                                {
                                    remSql.append( ds.getSourceObject() ).append( "." );
                                    remSql.append( ( objkeys[attPos][z] ).toUpperCase() );
                                }
                            }
                        }
                    }
                }
                appendComma = true; 
            }
    //        remSql.append( ", " );
    //        remSql.append( " NULL AS \"" ).append( "SYS_LOCAL_OBJECT" ).append("\"");
            
            remSql.append( " FROM " ).append( ds.getSourceObject() );
            remSql.append( ", " ).append( p_bdb.getTableName() );


            if( allds.canHaveLocalObjects() )
            {
                remSql.append( " WHERE " );
                remSql.append( MapType2Plugin.FLAG_FIELD_NAME );
                remSql.append("='N' " );
                remSql.append( " AND " );
            } 
            else
            {
                remSql.append( " WHERE " );
            }

            for (int i = 0; i < keys.length; i++) 
            {
                if( i > 0 )
                {
                    remSql.append( " AND " );
                }
                remSql.append( ds.getSourceObject() );
                remSql.append( ".\"" ).append( keys[i] ).append( "\"" );
                remSql.append( "=" );
                remSql.append( p_bdb.getTableName() );
                remSql.append( ".\"" ).append( keys[i] ).append( "\"" );
            }
            viewSql.append( remSql );
        }
        p_bdb.addView(  deployedDef.getBoPhisicalMasterTable()+"$M", 
                        "Tabela que junta objectos remotos e locais do objecto " + deployedDef.getName(),
                        viewSql.toString()
                    );
   }
   
   public void buildAttributes( boDefHandler undeployedDef )
   {
        boDefHandler deployedDef = boDefHandler.getBoDefinition( undeployedDef.getName() );
        
        MapType2Def          localobjdef = MapType2Def.getDataSourceDefinition( deployedDef );
        MapType2Def.ObjectDS localods    = localobjdef.getObjectDataSources().getDataSources()[0];
        
         
        String[]    locatts = localods.getLocalAttributes();
        String[]    rematts = localods.getRemoteAttributes();
        String[][]  relkeys = localods.getObjectRelationLocalKeys();
        
        ArrayList locfields = new ArrayList();
        for (int i = 0; i < locatts.length; i++) 
        {
            if( localods.getBridgeTableName()[i] == null )
            {
                locfields.add( deployedDef.getAttributeRef( locatts[i]  ).getDbName().toUpperCase() );
            }
        }
        
        p_extraAtts = new Hashtable();
        p_extraFlds = new ArrayList();
        for (int i = 0; i < locatts.length; i++) 
        {
            if( relkeys[i] != null )
            {
                
                boDefAttribute att = deployedDef.getAttributeRef( locatts[i] );
                boDefHandler refObjDef = att.getReferencedObjectDef();
                
                MapType2Def refobjmap = MapType2Def.getUndeployedDataSourceDefinition( p_repository, refObjDef );

                if( localods.getBridgeTableName()[i] == null )
                {
                    if( refobjmap != null )
                    {
                        ObjectDS[] allods = refobjmap.getObjectDataSources().getDataSources();
                        for (int d=0;d < allods.length;d++ ) 
                        {
                            ObjectDS ods = allods[d];
                            String[] lockeys = ods.getLocalKeys();
                            String[] typkeys = ods.getKeysDataTypes();
                            
                            for (int k=0;k < relkeys[i].length ; k++) 
                            {
                                if ( relkeys[i][k] != null )
                                {
                                    String fieldName = relkeys[i][k].toUpperCase();
                                    if( locfields.indexOf( fieldName ) == -1 )
                                    {
                                        p_bdb.addField( 
                                                        deployedDef.getBoPhisicalMasterTable(), 
                                                        fieldName,
                                                        typkeys[k],
                                                        "Chave da tabela remota",
                                                        false,
                                                        null,
                                                        null,
                                                        null
                                                    );
                                        p_extraFlds.add( fieldName );
                                        p_extraAtts.put( fieldName, new String[] { att.getName(), att.getDbName() } );
                                    }
                                }
                            }
                        }
                    }
                    else
                    {
                        for (int k=0;k < relkeys[i].length ; k++)  
                        {
                            if ( relkeys[i][k] != null )
                            {
                                String fieldName = relkeys[i][k].toUpperCase();
                                if( locfields.indexOf( fieldName ) == -1 )
                                {
                                    p_bdb.addField( 
                                                    undeployedDef.getBoPhisicalMasterTable(), 
                                                    fieldName,
                                                    "CHAR(100)",  // TODO: HANDLE DATATYPES FOR RELATIONS
                                                    "Chave da tabela remota",
                                                    false,
                                                    null,
                                                    null,
                                                    null
                                                );
                                    p_extraFlds.add( fieldName );
                                    p_extraAtts.put( fieldName, new String[] { att.getName(), att.getDbName() } );
                                }
                            }
                        }
                    }
                }
            }
        }
   }
   
   private final void deployDataSource( boDefHandler objDef)
   {
        File origFile = p_repository.getDataSourceFile( objDef.getName() );
        ngtXMLUtils.saveXML(    
                        ngtXMLUtils.loadXMLFile( origFile.getAbsolutePath() )
                        , 
                        boConfig.getDeploymentDir() + objDef.getName() + boBuilder.TYPE_DS 
                    );
    }
}