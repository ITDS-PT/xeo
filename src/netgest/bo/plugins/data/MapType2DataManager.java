/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.lang.reflect.Field;
import java.math.*;

import java.sql.*;

import java.util.*;

import netgest.bo.*;
import netgest.bo.data.*;
import netgest.bo.def.*;
import netgest.bo.plugins.*;
import netgest.bo.runtime.*;
import netgest.bo.plugins.data.MapType2Def.ObjectDS;

import netgest.utils.*;
import netgest.bo.system.Logger;

public class MapType2DataManager implements IDataManager 
{
    public static final String FLAG_MAPTYPE2_DISABLEWRITERS   = "90001";
    public static final String FLAG_MAPTYPE2_ALLREADYFILLED   = "90002";
    public static final String FLAG_MAPTYPE2_DISABLEEVENTS    = "90003";

    public static Hashtable     objectKeysMapped = new Hashtable();
    public static WeakHashMap   flaggedThreads   = new WeakHashMap();
    
    private static Logger logger = Logger.getLogger( MapType2DataManager.class.getName() );
    

    // Class que apanha os eventos de objectos mapeados e que permite ler os
    // dados das tabelas estrangeiras dinamicamente.
    final boEventListener eventListener = new boEventListener()
            {
                public void onEvent( boEvent event )
                {
                    if(!threadIsFlaged( FLAG_MAPTYPE2_DISABLEEVENTS ) )
                    {
                        try
                        {
                            if( event.getEvent() == boEvent.EVENT_AFTER_CHANGE )
                            {
                                AttributeHandler att = (AttributeHandler)event.getSourceObject();
                                boolean changed = att.getParent().isChanged();
                                if( !att.isObject() )
                                {
                                    loadExternalData(
                                        att.getParent()
                                    );
                                }
                                else
                                {
                                    reverseMap( att, event );
                                }
                                att.getParent().setChanged( changed );
                            }
                            
                            if( event.getEvent() == boEvent.EVENT_BEFORE_GETVALUE )
                            {
                                AttributeHandler att = (AttributeHandler)event.getSourceObject();
                                boolean changed = att.getParent().isChanged();
                                String attname = att.getName();
                                if( att.isObject() )
                                {
                                    String lastGet = att.getParent().getParameter( "MATYPE2LASTGET_"+att.getName() );
                                    if( 
                                            lastGet == null || 
                                            (System.currentTimeMillis() - Long.parseLong( lastGet )) > 2000
                                    )
                                    {
                                        mapObjectAttribute( att );
                                        att.getParent().setParameter( "MATYPE2LASTGET_"+att.getName(),String.valueOf( System.currentTimeMillis() ) );
                                    }
                                    att.getParent().setChanged( changed );
                                }
                            }
                            
                            if( event.getEvent() == boEvent.EVENT_AFTER_LOADBRIDGE )
                            {
                                bridgeHandler bh  = (bridgeHandler)event.getSourceObject();
                                boDefHandler  def = bh.getParent().getBoDefinition();
                                MapType2Def mapdef = MapType2Def.getDataSourceDefinition( def );
                                if( mapdef != null )
                                {
                                    ObjectDS allds[] = mapdef.getObjectDataSources().getDataSources();
                                    for (int i = 0; i < allds.length; i++) 
                                    {
                                        mapBridge(
                                                bh.getParent(),
                                                bh.getName(),
                                                def.getAttributeRef( bh.getName() ).getReferencedObjectDef(),
                                                allds[i],
                                                bh.getParent().getDataRow(),
                                                bh
                                            );
                                    }
                                }
                                
                            }
                        }
                        catch (boRuntimeException e)
                        {
                             logger.warn("Error on MapType2DataManager.eventListener", e);
                        }
                    }
                }
            };


    public DataSet execute(EboContext ctx, boDefHandler objdef, String sqlquery, int page, int pageSize, List arguments, boolean isboql) throws boRuntimeException
    {
        return DefaultDataManager.execute( ctx, objdef, sqlquery, page, pageSize, arguments, isboql );
    }

    public boolean updateObjectData(boObject obj ) throws boRuntimeException
    {
        boDefHandler objdef = obj.getBoDefinition();
        MapType2Def mapdef = MapType2Def.getDataSourceDefinition( objdef );
        if( mapdef != null )
        {
            DataSet objDataSet = obj.getDataSet();
            
            //Martelada TOny
            
            if (mapdef.canHaveLocalObjects()) 
            {
              objDataSet.rows( 1 ).updateObject( MapType2Plugin.FLAG_FIELD_NAME , "Y" );
            }
            else 
            {
              objDataSet.rows( 1 ).updateObject( MapType2Plugin.FLAG_FIELD_NAME , "N" );
            }
            // Determina qual a fonte do object mapeado.. quando existem varias tabelas 
            // Juntas no mesmo objecto.
            
            ObjectDS[] allds = mapdef.getObjectDataSources().getDataSources();
            ObjectDS   sds   = null;
 
            boolean allkeysfilled = false;
            for (int i = 0; i < allds.length; i++) 
            {
                String keys[] = allds[i].getLocalKeys();
                allkeysfilled = true;
                for (int k=0 ; k < keys.length ; k++ ) 
                {
                    if( objDataSet.rows( 1 ).getObject( keys[k] ) == null )
                    {
                        allkeysfilled = false;
                        break;
                    }
                }
                if( allkeysfilled )  
                {
                    sds = allds[i];
                    break;
                }
            }
            
            if( sds == null )
            {
                objDataSet.rows( 1 ).updateObject( MapType2Plugin.FLAG_FIELD_NAME, "Y" );
            }
            
            
            objDataSet.getRelation().clearWriteRelations();

            if( sds != null )
            {
                List   arrayLocalCols   = new ArrayList();
                List   arrayRemoteCols   = new ArrayList();
                
                String[]    columnNames      = objDataSet.getMetaData().getColumnNames();
                List        mapedColumns     = convertToListInUpperCase( sds.getLocalAttributes() );
                List        localAttributes  = Arrays.asList( sds.getLocalAttributes() );
                List        keys             = convertToListInUpperCase( sds.getLocalKeys() ); 
                
                for (int i = 0; i < columnNames.length; i++) 
                {
                    if( "N".equals( objDataSet.rows( 1 ).getString( MapType2Plugin.FLAG_FIELD_NAME ) ) )
                    {
                        // Verifica se a coluna é mapeada se for não continua.
                        if  ( 
                                keys.contains( columnNames[i] )
                                || 
                                !mapedColumns.contains( columnNames[i] )
                            )
                        {
                            arrayLocalCols.add( columnNames[i] );
                        }
                    }
                    else
                    {
                        arrayLocalCols.add( columnNames[i] );
                    }
                }
                String[] localCols = (String[])arrayLocalCols.toArray( new String[ arrayLocalCols.size() ]  );
                
                objDataSet.getRelation().addWriteRelation( 
                    new DataSetWriteRelation(
                            "DefaultWriter", 
                            sds.getDataSource(),
                            objdef.getName(),
                            objdef.getBoPhisicalMasterTable(),
                            null, 
                            new String[] { "SYS_ICN" }, 
                            new String[] { "BOUI" },
                            new String[] { "BOUI" },
                            localCols,
                            localCols
                        )
                );
                
                
                List mapLocAttributes = new ArrayList( Arrays.asList( sds.getLocalAttributes() ) );
                List mapRemAttributes = new ArrayList( Arrays.asList( sds.getRemoteAttributes() ) );
                
                String[] sqlfields  = sds.getSQLExpressions();

                // Remove the attributes of the object.
                int vi = 0;
                for (int i = 0; i < mapLocAttributes.size(); i++) 
                {
                    if( sqlfields[vi] != null && sqlfields[vi].length() > 0 )
                    {
                        mapLocAttributes.remove( i );
                        mapRemAttributes.remove( i );
                        i--;
                    }
                    vi++;
                }

                
                for (int i = 0; i < mapLocAttributes.size(); i++) 
                {
                    boDefAttribute defatt = objdef.getAttributeRef( mapLocAttributes.get( i ).toString() );
                    if( defatt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {
                        if (defatt.getRelationType() != boDefAttribute.RELATION_1_TO_1 )
                        {
                            mapLocAttributes.remove( i );
                            mapRemAttributes.remove( i );
                            i--;
                        }
                    }
                }
                
                
                String[][] objLocKeys = sds.getObjectRelationLocalKeys();
                int limit = mapLocAttributes.size();
                for (int i = 0; i < limit; i++) 
                {
                    String attName = mapLocAttributes.get( i ).toString();
                    boDefAttribute defatt = objdef.getAttributeRef( mapLocAttributes.get( i ).toString() );
                    if( defatt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {
                        if (defatt.getRelationType() == boDefAttribute.RELATION_1_TO_1 )
                        {
                            int idx = localAttributes.indexOf( attName );
                            if ( objLocKeys[idx] != null )
                            {
                                for (int k=0 ; k < objLocKeys[idx].length ; k++ ) 
                                { 
                                    if( objLocKeys[idx][k] != null )
                                    {
                                        if ( mapRemAttributes.indexOf( objLocKeys[idx][k].toUpperCase() ) == -1 )
                                        {
                                            mapLocAttributes.add( objLocKeys[idx][k].toUpperCase() );
                                            mapRemAttributes.add( objLocKeys[idx][k].toUpperCase() );
                                        }
                                        mapLocAttributes.remove( i );
                                        mapRemAttributes.remove( i );
                                        limit--;
                                        i--;
                                    }
                                }
                            }
                        }
                    }
                }
                if( !threadIsFlaged( FLAG_MAPTYPE2_DISABLEWRITERS )
                    &&
                    obj.getParameter( FLAG_MAPTYPE2_DISABLEWRITERS ) == null
                )
                {
                    invokeWriterClass( sds, obj );
                } 
                boolean isNew=obj.getDataRow().isNew();
                DefaultDataManager.updateObjectData( obj );
                if (isNew)obj.getDataSet().setForInsert();               

                if( !threadIsFlaged( FLAG_MAPTYPE2_DISABLEWRITERS )
                    &&
                    obj.getParameter( FLAG_MAPTYPE2_DISABLEWRITERS ) == null
                    )
                {
                    if( sds.useDefaulfWriter() )
                    {
                        if( "N".equals( objDataSet.rows( 1 ).getString( MapType2Plugin.FLAG_FIELD_NAME ) ) )
                        {
                            objDataSet.getRelation().clearWriteRelations();
                            if( !sds.isReadOnly() && ( sds.canUpdate() || (sds.canInsert() && !obj.exists() ) ) )
                            {
                                objDataSet.rows( 1 ).updateLong( "SYS_ICN", objDataSet.rows( 1 ).getLong("SYS_ICN") - 1 );
                                objDataSet.getRelation().addWriteRelation( 
                                    new DataSetWriteRelation(
                                            "ExternalTableWriter", 
                                            sds.getDataSource(),
                                            objdef.getName(),
                                            sds.getSourceObject(),
                                            null, 
                                            null, 
                                            sds.getLocalKeys(),
                                            sds.getKeys(),
                                            (String[])mapLocAttributes.toArray( new String[mapLocAttributes.size()] ),
                                            (String[])mapRemAttributes.toArray( new String[mapRemAttributes.size()] )
                                        )
                                );
                                DefaultDataManager.updateObjectData( obj );
                            }
                         }
                    }
                }
            }
            else
            {
                for (int i = 0; i < allds.length; i++) 
                {
                    invokeWriterClass( allds[i], obj  );
                }
//                if( mapdef.canHaveLocalObjects() )
//                {
                    objDataSet.getRelation().addWriteRelation( 
                        new DataSetWriteRelation(
                                "DefaultWriter", 
                                "DATA",
                                objdef.getName(),
                                objdef.getBoPhisicalMasterTable(),
                                null, 
                                new String[] { "SYS_ICN" }, 
                                new String[] { "BOUI" },
                                new String[] { "BOUI" },
                                objDataSet.getMetaData().getColumnNames(),
                                objDataSet.getMetaData().getColumnNames()
                            )
                    );
                    DefaultDataManager.updateObjectData( obj );
//                }
            }
        }
        
        
        return false;
    }
    
    public void beforeObjectUpdate( boObject object ) throws boRuntimeException
    {
        
    }
    
    public void beforeObjectLoad(boObject object) throws boRuntimeException
    {
    }

    public void afterObjectLoad(boObject object) throws boRuntimeException
    {
        long ts = System.currentTimeMillis();
        if( !threadIsFlaged( FLAG_MAPTYPE2_DISABLEEVENTS ) )
        {
            boolean isChanged = object.isChanged();
            long init = System.currentTimeMillis();
        
            MapType2Def mapdef = MapType2Def.getDataSourceDefinition( object.getBoDefinition() );
            boDefHandler boDef = object.getBoDefinition();
            
            ObjectDS[] allods = mapdef.getObjectDataSources().getDataSources();
            
            for (int d = 0; d < allods.length; d++) 
            {
                ObjectDS ods = allods[d];
    
                String[] locAtts        = ods.getLocalAttributes();
                
                // Se o objecto estiver marcado como local. Tentar mapear com o estrangeiro
                if( !"N".equals( object.getDataRow().getString( MapType2Plugin.FLAG_FIELD_NAME ) ) )
                {
                    loadExternalData( object );
                }
                
                for (int i = 0; i < locAtts.length; i++) 
                {
                    boDefAttribute defAtt = boDef.getAttributeRef( locAtts[i] );
                    if( defAtt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                    {
                        if( defAtt.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                        {
                            object.addEventListener( this.eventListener );
                            break;
                        }
                    }
                }
                for (int i = 0; i < ods.getLocalKeys().length; i++) 
                {
                    AttributeHandler att = object.getAttribute( ods.getLocalKeys()[i].toLowerCase() );
                    if( att != null )
                    {
                        att.addEventListener( eventListener );
                    }
                }
    
                List keysList = Arrays.asList( ods.getKeys() );
                
                for (int i = 0; i < locAtts.length; i++) 
                {
    
                    AttributeHandler att = object.getAttribute( ods.getLocalAttributes()[i] );
                    if( att != null )
                    {
                        if( !ods.canUpdate() )
                        {
                            att.setDisabled();
                            att.setDisabledforRequest();
                        }
                        if ( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                        {
                            att.addEventListener( eventListener );
                        }
                        else
                        {
                            if( keysList.indexOf( locAtts[i].toUpperCase( ) ) > -1 )
                            {
                                att.getParent().addDependenceField( locAtts[i] );
                            }
                        }
                    }
                    else
                    {
                        String toBreak = "yes";
                    }
                }
            }
            object.setChanged( isChanged );
        }
//        logger.finest( "onafterLoad ["+object.getName()+"] time:" + (System.currentTimeMillis() - ts) );
//        if( object.getName().equals("ANC_entity") )
//        {
//            logger.finest( object.getAttribute("id") + ":" + object.getBoui() );
//        }
        
    }
    
    private final void mapBridge(boObject parentObj, String attName, boDefHandler remoteObj, ObjectDS locods, DataRow objRow, bridgeHandler bh )
    {
        
        long init = System.currentTimeMillis();
        
        MapType2Def  remmapdef = MapType2Def.getDataSourceDefinition( remoteObj );
        int idx = Arrays.asList( locods.getLocalAttributes() ).indexOf( attName );
        if( idx != -1 )
        {
            if( locods.getBridgeTableName()[idx] == null )
            {
                mapBridgeWithoutTable(remmapdef, idx, parentObj, attName, remoteObj, locods, objRow, bh );
            }
            else
            {
                mapBridgeWithTable(remmapdef, idx, parentObj, attName, remoteObj, locods, objRow, bh );
            }
        }
        
        if( logger.isFinerEnabled() )
        {
            if( System.currentTimeMillis() - init > 10000  )
            {
                logger.finer( "Brige a carregar muito lenta: ["+parentObj.getName()+"("+parentObj.getBoui()+")].["+attName+"]" );
            }
        }
    }


    private final void mapBridgeWithTable(MapType2Def remmapdef, int idx, boObject parentObj, String attName, boDefHandler remoteObj, ObjectDS locods, DataRow objRow, bridgeHandler bh )
    {
        
        PreparedStatement pstm = null;
        ResultSet         rslt = null;
        boolean allkeysfilled = true;

        String[] remobjkeys = locods.getObjectRelationRemoteKeys()[idx];
        String[] locobjkeys = locods.getObjectRelationLocalKeys()[idx];
        String[] locobjlit  = locods.getObjectRelationLocalKeysLiteral()[idx];

        String[] bremkeys = locods.getBridgeRemoteFields()[idx];
        String[] blockeys = locods.getBridgeLocalFields()[idx];
        
        String remoteTable = locods.getBridgeTableName()[idx];
        //String[] bridgeKeys = new String[ remobjkeys.length ];
        
        if (blockeys!=null)
        {
          for (int i = 0; i < blockeys.length; i++) 
          {
              if( blockeys[i] != null )
              {
                  if( objRow.getObject( blockeys[i] ) == null )
                  {
                      allkeysfilled = false;
                  }
              }
          }
        }
        
        if( allkeysfilled && remmapdef != null )
        {
            try 
            {
                // Seleciona o data-source remoto
                ObjectDS[] allremds = remmapdef.getObjectDataSources().getDataSources();
                ObjectDS   remods   = null;
                for (int i = 0; i < allremds.length; i++) 
                {
                    boolean dsfound = true;
                    for (int j=0;j < remobjkeys.length; j++ )
                    {
                        if( 
                            Arrays.asList(allremds[i].getKeys()).indexOf( remobjkeys[j].toUpperCase() ) == -1
                            &&
                            Arrays.asList( toUpper( allremds[i].getLocalAttributes() ) ).indexOf( remobjkeys[j].toUpperCase() ) == -1
                        )
                        {
                            dsfound = false;
                            break;
                        }
                    }
                    if(dsfound )
                    {
                        remods = allremds[ i ];
                        break;
                    }
                }
                
                if( remods != null )
                {

                    boolean appendComma = false;
                    
            
                    //String[] rempk      =  remods.getKeys();
                    String[] rempkalias =  remods.getKeys();
                    
                    StringBuffer sql = new StringBuffer("SELECT ");
                    
                    for (int i = 0; i < rempkalias.length; i++) 
                    {
                        if( appendComma )
                        {
                            sql.append( ", " );
                        }
                        sql.append( locobjkeys[i] );
                        sql.append( " AS " );
                        sql.append( rempkalias[i] );
                        appendComma = true;
                    }
                    sql.append( " FROM " ).append( remoteTable );
                    sql.append( " WHERE " );
                    
                    appendComma = false;
                    for (int i = 0; i < blockeys.length; i++) 
                    {
                        if( appendComma ) 
                        {
                            sql.append( " AND " );
                        }
                        sql.append( blockeys[ i ] );
                        sql.append( "=?" );
                        appendComma = true;
                    }
                    
                    
                    String extraQuery = locods.getObjectRelationExtraQuery()[ idx ];
                    if( extraQuery != null && extraQuery.length() > 0 )
                    {
                        sql.append( " AND " );
                        sql.append( "( "  );
                        sql.append( extraQuery );
                        sql.append( " )"  );
                    }
                    
                    String orderBy   = locods.getObjectRelationOrderBy()[ idx ];
                    if( orderBy != null && orderBy.length() > 0 )
                    {
                        sql.append( " ORDER BY " );
                        sql.append( orderBy );
                    }
                    
                    EboContext ctx = parentObj.getEboContext();
                    
                    Connection cn           = ctx.getConnectionData();
                    pstm  = cn.prepareStatement( sql.toString() );
                    for (int i = 0; i < blockeys.length; i++) 
                    {
                        pstm.setObject( i + 1, objRow.getObject( blockeys[i] ) );
                    }
                    rslt = pstm.executeQuery();
                    boBridgeIterator it = bh.iterator();
                    int rowindex = 0;
                    while( rslt.next() )
                    {
                        rowindex ++;
                        if ( it.absolute( rowindex ) )
                        {
                            boObject remObj = reverseMapBridgeObject( it, rslt, locods,remods, rempkalias,locobjlit, remobjkeys, objRow );
                            if( it.currentRow().getValueLong() != remObj.getBoui() )
                            {
                                if( bh.haveBoui( remObj.getBoui() ) )
                                {
                                    bh.moveRowTo( it.getRow() );
                                }
                                else
                                {
                                    bh.add( remObj.getBoui(), it.getRow() );
                                    bh.moveTo( it.getRow() + 1 );
                                    bh.remove( );
                                }
                            }
                        }
                        else
                        {
                            boObject remObj = mapBridgeObject( 
                                    parentObj,
                                    remoteObj.getName(),
                                    rslt,
                                    rempkalias,
                                    locobjlit,
                                    remobjkeys,
                                    remods,
                                    objRow
                            );
                            if( remObj != null )
                            {
                            bh.add( remObj.getBoui() );
                            it.absolute( bh.getRow() );
                            }
                            else
                            {
                                String msg = "Object Map referes to a non existing object. Object is  ["+remoteObj.getName()+"], parent is ["+parentObj.getBoui()+"]";
                                logger.warn( msg );
                            }
                        }
                    }
                    while( it.next() )
                    {
                        bh.moveTo( it.getRow() );
                        bh.remove();
                        it.previous();
                    }
                }
            } 
            catch (Exception ex) 
            {
                 logger.warn("Error on mapBridge", ex);
            } 
            finally 
            {
                try
                {
                    if( rslt != null ) rslt.close();
                    if( pstm != null ) pstm.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }

    }


    private final void mapBridgeWithoutTable(MapType2Def remmapdef, int idx, boObject parentObj, String attName, boDefHandler remoteObj, ObjectDS locods, DataRow objRow, bridgeHandler bh )
    {
        PreparedStatement pstm = null;
        ResultSet         rslt = null;
        boolean allkeysfilled = true;

        String[] remobjkeys = locods.getObjectRelationRemoteKeys()[idx];
        String[] locobjkeys = locods.getObjectRelationLocalKeys()[idx];
        String[] locobjlit  = locods.getObjectRelationLocalKeysLiteral()[idx];
            
            
        String[] bridgeKeys = new String[ remobjkeys.length ];
        
        for (int i = 0; i < locobjkeys.length; i++) 
        {
            if( locobjkeys[i] != null )
            {
                if( objRow.getObject( locobjkeys[i] ) == null )
                {
                    allkeysfilled = false;
                }
            }
        }
        
        
        if( allkeysfilled && remmapdef != null )
        {
            try 
            {
                // Seleciona o data-source remoto
                ObjectDS[] allremds = remmapdef.getObjectDataSources().getDataSources();
                ObjectDS   remods   = null;
                for (int i = 0; i < allremds.length; i++) 
                {
                    boolean dsfound = true;
                    for (int j=0;j < remobjkeys.length; j++ )
                    {
                        if( 
                            Arrays.asList(allremds[i].getKeys()).indexOf( remobjkeys[j].toUpperCase() ) == -1
                            &&
                            Arrays.asList( toUpper( allremds[i].getLocalAttributes() ) ).indexOf( remobjkeys[j].toUpperCase() ) == -1
                        )
                        {
                            dsfound = false;
                            break;
                        }
                    }
                    if(dsfound )
                    {
                        remods = allremds[ i ];
                        break;
                    }
                }
                
                if( remods != null )
                {

                    for (int i = 0; i < remobjkeys.length; i++) 
                    {
                        int tmpidx;
                        if( Arrays.asList(remods.getKeys()).indexOf( remobjkeys[i].toUpperCase() ) != -1 )
                        {
                            bridgeKeys[i] = remobjkeys[i];
                        }
                        else if ( (tmpidx=Arrays.asList( toUpper( remods.getLocalAttributes() ) ).indexOf( remobjkeys[i].toUpperCase() )) != -1 )
                        {
                            bridgeKeys[i] = remods.getRemoteAttributes()[ tmpidx ];
                        }
                    }

                    boolean appendComma = false;
                    
                    String remoteTable = remods.getSourceObject();
            
                    String[] rempk      =  remods.getKeys();
                    String[] rempkalias =  remods.getKeys();
                    
                    StringBuffer sql = new StringBuffer("SELECT ");
                    
                    // SELECT CASO EXISTA UMA BRIDGE
                    if ( locods.getBridgeTableName()[idx] != null )
                    {
                        remoteTable = locods.getBridgeTableName()[idx];
                        rempk       = locods.getBridgeRemoteFields()[idx];
                        bridgeKeys  = locods.getBridgeLocalFields()[idx];
                    }
                    for (int i = 0; i < rempk.length; i++) 
                    {
                        if( appendComma )
                        {
                            sql.append( ", " );
                        }
                        sql.append( rempk[i] );
                        sql.append( " AS " );
                        sql.append( rempkalias[i] );
                        appendComma = true;
                    }
                    sql.append( " FROM " ).append( remoteTable );
                    sql.append( " WHERE " );
                    
                    appendComma = false;
                    for (int i = 0; i < bridgeKeys.length; i++) 
                    {
                        if( appendComma ) 
                        {
                            sql.append( " AND " );
                        }
                        sql.append( bridgeKeys[ i ] );
                        sql.append( "=?" );
                        appendComma = true;
                    }
                    
                    
                    String extraQuery = locods.getObjectRelationExtraQuery()[ idx ];
                    if( extraQuery != null && extraQuery.length() > 0 )
                    {
                        sql.append( " AND " );
                        sql.append( "( "  );
                        sql.append( extraQuery );
                        sql.append( " )"  );
                    }
                    
                    String orderBy   = locods.getObjectRelationOrderBy()[ idx ];
                    if( orderBy != null )
                    {
                        sql.append( " ORDER BY " );
                        sql.append( orderBy );
                    }
                    
                    EboContext ctx = parentObj.getEboContext();
                    
                    Connection cn           = ctx.getConnectionData();
                    pstm  = cn.prepareStatement( sql.toString() );
                    for (int i = 0; i < locobjkeys.length; i++) 
                    {
                        pstm.setObject( i + 1, objRow.getObject( locobjkeys[i] ) );
                    }
                    rslt = pstm.executeQuery();
                    boBridgeIterator it = bh.iterator();
                    int rowindex = 0;
                    while( rslt.next() )
                    {
                        rowindex ++;
                        if ( it.absolute( rowindex ) )
                        {
                            boObject remObj = reverseMapBridgeObject( it, rslt, locods,remods, locobjkeys,locobjlit, remobjkeys, objRow );
                            if( it.currentRow().getValueLong() != remObj.getBoui() )
                            {
                                if( bh.haveBoui( remObj.getBoui() ) )
                                {
                                    bh.moveRowTo( it.getRow() );
                                }
                                else
                                {
                                    bh.add( remObj.getBoui(), it.getRow() );
                                    bh.moveTo( it.getRow() + 1 );
                                    bh.remove( );
                                }
                            }
                        }
                        else
                        {
                            boObject remObj = mapBridgeObject( 
                                    parentObj,
                                    remoteObj.getName(),
                                    rslt,
                                    locobjkeys,
                                    locobjlit,
                                    remobjkeys,
                                    remods,
                                    objRow
                            );
                            bh.add( remObj.getBoui() );
                            it.absolute( bh.getRow() );
                        }
                    }
                    while( it.next() )
                    {
                        bh.moveTo( it.getRow() );
                        bh.remove();
                        it.previous();
                    }
                }
            } 
            catch (Exception ex) 
            {
                 logger.warn("Error on mapBridge", ex);
            } 
            finally 
            {
                try
                {
                    if( rslt != null ) rslt.close();
                    if( pstm != null ) pstm.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }
    }
    
    private final boObject reverseMapBridgeObject( boBridgeIterator it, ResultSet rslt,ObjectDS locods, ObjectDS remods, String[] locKeys, String[] loclitKeys, String[] remKeys, DataRow locrow ) 
                                                                            throws boRuntimeException, SQLException
    {
        boObject remObj = it.currentRow().getObject();
        DataRow row = remObj.getDataRow();
        String[] extpk = remods.getKeys();
        for (int i = 0; i < extpk.length; i++) 
        {
            if ( compareValues( row.getObject( extpk[i] ), rslt.getObject( extpk[i] ) ) )
            {
                remObj = mapBridgeObject( it.getBridgeHandler().getParent(), remObj.getName(), rslt, locKeys,loclitKeys, remKeys, remods, locrow );
                break;
            }
        }
        return remObj;
    }
    
    private final boObject mapBridgeObject( boObject parentObj, String refObjName, ResultSet rslt, String[] locKeys,String[] locLitKeys, String[] remKeys, ObjectDS remods, DataRow row ) throws SQLException, boRuntimeException
    {
        StringBuffer qry    = new StringBuffer();

        String[]     extpk  = remods.getKeys();
        Object[]     qryPar = new Object[ extpk.length ];
        
        for (int i = 0; i < extpk.length; i++) 
        {
            if( i > 0 ) qry.append( " AND " );
            qry.append( " " );
            qry.append( extpk[i] );
            qry.append( "=" );
            qry.append( "?" );
            if( locKeys.length > i )
            {
                if( locKeys[ i ] != null )
                {
                    qryPar[i] = rslt.getObject( locKeys[ i ] );
                }
                else
                {
                    qryPar[i] = locLitKeys[i];
                }
            }
            else
            {
                qryPar[i] = rslt.getObject( extpk[i] );
                if( qryPar[i] instanceof java.sql.Date )
                {
                    qryPar[i] = rslt.getTimestamp( extpk[i] );    
                }
            }
        }
        EboContext ctx = parentObj.getEboContext();
        
        boObject obj = getObjectFromPrimaryKey( ctx, refObjName, qryPar );
        if( obj == null )
        {
            obj = boObject.getBoManager().loadObject( 
                                        ctx, 
                                        refObjName,
                                        qry.toString(),
                                        qryPar
                                    );
            if ( !obj.exists() && obj.getParameter( FLAG_MAPTYPE2_ALLREADYFILLED.toString() ) == null )
            {
//                obj = boObject.getBoManager().createObjectWithParent( ctx, refObjName, parentObj.getBoui() );
//                obj.setParameter( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS, "YES" );
//                for (int i = 0; i < extpk.length; i++) 
//                {
//                    if( locKeys.length > i )
//                    {
//                        obj.getAttribute( remKeys[i] ).setValueObject( row.getObject( locKeys[i] ) );
//                    }
//                    else
//                    {
//                        Object xvalue = rslt.getObject( extpk[i] );
//                        
//                        if( xvalue instanceof java.sql.Date )
//                        {
//                            xvalue = rslt.getTimestamp( extpk[i] );
//                        }
//                        
//                        obj.getAttribute( extpk[i].toLowerCase() ).setValueObject( xvalue );
//                        
//                    }
//                }
//                obj.setParameter( MapType2DataManager.FLAG_MAPTYPE2_ALLREADYFILLED.toString() ,"YES");
//                afterObjectLoad( obj );
//                putObjectFromPrimaryKey( refObjName, qryPar, new Long( obj.getBoui() ) );

                Object key[] = new Object[ extpk.length ];
                for (int i = 0; i < extpk.length; i++) 
                {
                    Object xvalue = rslt.getObject( extpk[i] );
                    
                    if( xvalue instanceof java.sql.Date )
                    {
                        xvalue = rslt.getTimestamp( extpk[i] );
                    }
                    key[i] = xvalue;
                }
                obj = immediateMapInternal(   
                            ctx, 
                            boDefHandler.getBoDefinition( refObjName ),
                            remods.getKeys(),
                            remods.getSourceObject(),
                            remods.getKeys(),
                            decodeObjKeysToTabKeys( remods, remods.getKeys() ), 
                            key,
                            remods.getWhereClause()
                      );
            }
        }
        return obj;
    }
    

    private final boolean loadExternalData( boObject obj ) throws boRuntimeException
    {
        MapType2Def def = MapType2Def.getDataSourceDefinition( obj.getBoDefinition() );
        ObjectDS[] allods = def.getObjectDataSources().getDataSources();
        boolean allKeysFilled = false;
        if( def != null )
        {
        
            EboContext ctx = obj.getEboContext();
            
            for (int d=0;!allKeysFilled && d < allods.length; d++ ) 
            {
                
                ObjectDS ods = allods[d];
                
                String[] localAtts = ods.getLocalAttributes();
    
                String[] remKeys      = ods.getKeys();
                String[] locKeys      = ods.getLocalKeys();
                
                DataRow dataRow = obj.getDataRow();
                
                allKeysFilled = true;
                for (int i = 0; i < locKeys.length; i++) 
                {
                    if ( dataRow.getObject( locKeys[i] ) == null )
                    {
                        allKeysFilled  = false;
                    }
                }
                if( allKeysFilled )
                {
                    String[] rematts = ods.getRemoteAttributes();
                    String[] sqlatts = ods.getSQLExpressions();
                    String[][] locobjrel  = ods.getObjectRelationLocalKeys();
                    String[] locatts = ods.getLocalAttributes();
                    
                    StringBuffer sqlQuery = new StringBuffer();
                    sqlQuery.append( "SELECT " );
                    
                    boolean appendComma = false;
                    for (int i = 0; i < rematts.length; i++) 
                    {
                        if( rematts[i] != null )
                        {
                            if( appendComma ) sqlQuery.append( ", " );
                            if( sqlatts[i] != null )
                            {
                                sqlQuery.append( sqlatts[i] );
                                sqlQuery.append( " AS ");
                                sqlQuery.append( rematts[i] );
                                
                            }
                            else
                            {
                                sqlQuery.append( rematts[i] ); 
                            }
                            appendComma = true;
                        }
                        else
                        {
                            boDefAttribute att = obj.getBoDefinition().getAttributeRef( localAtts[i] );
                            if( att.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE || att.getRelationType() != boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                            {
                                for (int k=0 ;k < locobjrel[i].length ;k++ ) 
                                {
                                    if( appendComma ) sqlQuery.append( ", " );
                                    sqlQuery.append( locobjrel[i][k] ); 
                                    appendComma = true;
                                }
                            }
                        }
                    }
                    
    //                sqlQuery.append( 
    //                    DataUtils.concatFields( rematts ).toUpperCase()
    //                );
    //                
                    sqlQuery.append( " FROM " );
                    sqlQuery.append( ods.getSourceObject() );
                    sqlQuery.append( " WHERE " );
    
                    for (int i = 0; i < remKeys.length; i++) 
                    {
                        
                        if( i > 0 ) sqlQuery.append( " AND " );
                        
                        sqlQuery.append( ods.getSourceObject() );
                        sqlQuery.append( ".\"" ).append( remKeys[i] ).append( "\"" );
                        sqlQuery.append( " = ? " );
                    }
                    
                    PreparedStatement pstm = null;
                    ResultSet         rslt = null;
                    
                    Connection          cn = ctx.getConnectionData();
                    try
                    {
                        pstm = cn.prepareStatement( sqlQuery.toString() );
                        
                        for (int i = 0; i < locKeys.length; i++) 
                        {
                            Object xvalue = dataRow.getObject( locKeys[i] );
                            if( xvalue instanceof Timestamp )
                            {
                                pstm.setTimestamp( i + 1, (Timestamp)xvalue );
                            }
                            else
                            {
                                pstm.setObject( i + 1, xvalue );
                            }
                        }
                        rslt = pstm.executeQuery();
                        if( rslt.next() )
                        {
                            for (int i = 0; i < rematts.length; i++) 
                            {
                                if( rematts[i] != null )
                                {
                                    
                                    Object xvalue = rslt.getObject( rematts[i] );
                                    if ( xvalue instanceof java.sql.Date )
                                    {
                                        xvalue = rslt.getTimestamp( rematts[i] );
                                    }
                                    dataRow.updateObject(
                                            locatts[i],
                                            xvalue
                                            
                                    );
                                }
                                else
                                {
                                    boDefAttribute att = obj.getBoDefinition().getAttributeRef( localAtts[i] );
                                    if( att.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE || att.getRelationType() != boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                                    {
                                        for (int k=0 ;k < locobjrel[i].length ;k++ ) 
                                        {
                                            if( locobjrel[i][k] != null ) 
                                            {
                                                    
                                                Object xvalue = rslt.getObject( locobjrel[i][k] );
                                                if ( xvalue instanceof java.sql.Date )
                                                {
                                                    xvalue = rslt.getTimestamp( locobjrel[i][k] );
                                                }
                                                dataRow.updateObject(
                                                    locobjrel[i][k],
                                                    xvalue
                                                );
                                            }
                                        }
                                    }
                                    obj.removeParameter( "MATYPE2LASTGET_"+localAtts[i] );
                                }
                            }
                            dataRow.updateString( MapType2Plugin.FLAG_FIELD_NAME , "N" );
                            afterObjectLoad( obj );
                        }
                    }
                    catch ( SQLException e )
                    {
                        e.printStackTrace();
                        throw new boRuntimeException2( e );
                    }
                    finally
                    {
                        try
                        {
                            if( rslt != null ) rslt.close();
                            if( pstm != null ) pstm.close();
                        }
                        catch (Exception ex)
                        {
                            
                        }
                    }
                }
                else
                {
                    dataRow.updateString( MapType2Plugin.FLAG_FIELD_NAME, "Y" );
                }
            }
        }
        return true;
    }
    
    private static final void mapObjectAttribute( AttributeHandler att ) throws boRuntimeException
    {
        DataRow row = att.getParent().getDataRow();

//        MapType2Def relref = MapType2Def.getDataSourceDefinition( att.getDefAttribute().getReferencedObjectDef() );
//        if( relref != null )
//        {
            MapType2Def mapdef = MapType2Def.getDataSourceDefinition( att.getParent().getBoDefinition() );
            ObjectDS[]   allods    = mapdef.getObjectDataSources().getDataSources();
            
            for (int d=0 ;d < allods.length ; d++ ) 
            {
                ObjectDS ods = allods[d];
                List atts             = Arrays.asList( ods.getLocalAttributes() );
                int idx               = atts.indexOf( att.getDefAttribute().getName() );
                String[][] objrelkeys = ods.getObjectRelationRemoteKeys();
        
                if( objrelkeys != null )
                {
                    boolean     allKeysField = true;
                    
                    String[]    remkeys         = objrelkeys[idx];
                    String[]    lockeys         = ods.getObjectRelationLocalKeys()[idx];
                    String[]    lockeysLit      = ods.getObjectRelationLocalKeysLiteral()[idx];
                    
                    String      extraQuery      = ods.getObjectRelationExtraQuery()[idx];
                    
                    
                    for (int i = 0; i < lockeys.length; i++) 
                    {
                        if( lockeys[i] != null && row.getObject( lockeys[i] ) == null )
                        {
                            allKeysField = false;
                            break;
                        }
                    }
                    
                    if( allKeysField )
                    {
                        boolean changed = att.getParent().isChanged();
                        long boui = att.getValueLong();
                        if( boui == 0 )
                        {
                            boObject refobj = loadExternalObject(
                                                att.getEboContext(), 
                                                att.getDefAttribute().getReferencedObjectName(), 
                                                lockeys, 
                                                remkeys, 
                                                lockeysLit,
                                                extraQuery,
                                                row 
                                            );
                            if( refobj != null )
                            {
                                row.updateLong( att.getDefAttribute().getDbName(), refobj.getBoui() );
                            }
                            att.getParent().poolSetStateFull();
                        }
                        else
                        {
                            boObject obj = att.getObject();
                            for (int i = 0; i < lockeys.length; i++) 
                            {
                                if( lockeys[i] != null )
                                {
                                    Object remval = att.getObject().getAttribute( remkeys[i] ).getValueObject();
                                    Object locval = row.getObject( lockeys[i] );
            
            
                                    if( compareValues( remval, locval ) )
                                    {
                                        boObject refobj = loadExternalObject( 
                                                            att.getEboContext(),
                                                            att.getDefAttribute().getReferencedObjectName(), 
                                                            lockeys, 
                                                            remkeys, 
                                                            lockeysLit,
                                                            extraQuery,
                                                            row 
                                                        );
                                        if( refobj != null )
                                        {
                                            row.updateLong( att.getDefAttribute().getDbName(), refobj.getBoui() );
                                        }
                                        att.getParent().poolSetStateFull();
                                    }
                                }
                            }
                        }
                        att.getParent().setChanged( changed );
                    }
                }
            }
//        }
    }
    
    private static final void  reverseMap( AttributeHandler att, boEvent event )
    {
        BigDecimal value = (BigDecimal)event.getNewValue();
        
        if( value != null )
        {
            try
            {
                boObject relObj = att.getObject();
                
                MapType2Def mapdef = MapType2Def.getDataSourceDefinition( att.getParent().getBoDefinition() );
                ObjectDS[]   allods    = mapdef.getObjectDataSources().getDataSources();
                
                for (int d=0;d < allods.length; d++ ) 
                {
                    ObjectDS ods = allods[d];
                    List atts = Arrays.asList( ods.getLocalAttributes() );
                    List keys = Arrays.asList( ods.getLocalKeys() );
                    int idx = atts.indexOf( att.getDefAttribute().getName() );
                    
                    String[][] locrelkeys = ods.getObjectRelationLocalKeys();
                    String[][] remrelkeys = ods.getObjectRelationRemoteKeys();
                    
                    if( idx > -1 && locrelkeys[ idx ] != null )
                    {
                        DataRow row = att.getParent().getDataRow();
                        for (int i = 0; i < locrelkeys[ idx ].length; i++) 
                        {
                            if( locrelkeys[ idx ][i] != null )
                            {
                                if( keys.indexOf( remrelkeys[ idx ][i].toUpperCase() ) == -1 )
                                {
                                    Object remValue = att.getObject().getAttribute( remrelkeys[ idx ][i] ).getValueObject(); 
                                    row.updateObject( locrelkeys[ idx ][i], remValue );
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception e)
            {
                logger.warn("Error on reverseMap", e);
            }
        }
        else
        {
            MapType2Def mapdef = MapType2Def.getDataSourceDefinition( att.getParent().getBoDefinition() );
            ObjectDS[]   allods    = mapdef.getObjectDataSources().getDataSources();
            for (int d = 0; d < allods.length; d++) 
            {
                ObjectDS ods = allods[ d ];
                List atts = Arrays.asList( ods.getLocalAttributes() );
                int idx = atts.indexOf( att.getDefAttribute().getName() );
                String[][] objrelkeys = ods.getObjectRelationLocalKeys();
                if( idx > -1 && objrelkeys[ idx ] != null )
                {
                    DataRow row = att.getParent().getDataRow();
                    for (int i = 0; i < objrelkeys[ idx ].length; i++) 
                    {
                        row.updateObject( objrelkeys[ idx ][i], null );
                    }
                }
            }
        }
        
    }
    
    private static final boObject loadExternalObject( EboContext ctx, String refObjName, String[] locKeys, String[] remKeys, String[] lockeysLit,String extraQuery, DataRow row ) throws boRuntimeException
    {
//        StringBuffer qry = new StringBuffer();
        ArrayList    qryPar = new ArrayList( locKeys.length );
        for (int i = 0; i < locKeys.length; i++) 
        {
//            if( i > 0 ) qry.append( " AND " );
//            qry.append( " " );
//            qry.append( remKeys[i] );
//            qry.append( "=" );
            if( locKeys[ i ] != null )
            {
//                qry.append( "?" );
                qryPar.add( row.getObject( locKeys[ i ] ) );
            }
            else
            {
//                qry.append( "?" );
                qryPar.add( lockeysLit[ i ] );
            }
        }
        return loadExternalObject( ctx, refObjName, locKeys, remKeys, lockeysLit,extraQuery, qryPar.toArray(), false );
    }   
    
    protected static final boObject loadExternalObject( EboContext ctx, String refObjName, String[] locKeys, String[] remKeys, String[] lockeysLit,String extraQuery, Object[] attributeValues, boolean preRegister ) throws boRuntimeException
    {
        boDefHandler objDef = boDefHandler.getBoDefinition( refObjName );
        String table = objDef.getBoMasterTable();
        
        ArrayList lookObjs = new ArrayList();
        lookObjs.add( objDef );
        lookObjs.addAll(
            Arrays.asList(objDef.getBoSubClasses())
        );
        boObject  obj = null;
        for (int i = 0;obj == null && i < lookObjs.size(); i++) 
        {
            boDefHandler refObjDef = (boDefHandler)lookObjs.get( i );
            if( refObjDef.getClassType() == boDefHandler.TYPE_CLASS )
            {
                MapType2Def mapdef = MapType2Def.getDataSourceDefinition( refObjDef );
                if( mapdef != null && mapdef.getMapType().equals("2") )
                {
                    ObjectDS[] allods = mapdef.getObjectDataSources().getDataSources();
                    for (int d = 0; obj==null && d < allods.length; d++) 
                    {
                        // Flag que faz com que retorne null se o
                        // obejcto relacionado não tiver registo e 
                        // estiver na segubnda faze do registo de outro
                        if( !preRegister || (mapdef.getPreRegisterObjects()) )
                        {
                            ObjectDS   remods   = allods[d];
                            boolean dsfound = true;
                            for (int j=0;j < remKeys.length; j++ )
                            {
                                if(
                                        Arrays.asList( allods[i].getLocalAttributes() ).indexOf( remKeys[j] ) == -1
                                        &&
                                        Arrays.asList( remods.getKeys() ).indexOf( remKeys[j].toUpperCase() ) == -1 
                                    )
                                {
                                    
                                    dsfound = false;
                                    break;
                                }
                                else
                                {
                                    if( 
                                        remods.getKeysDataTypes()[j].toLowerCase().startsWith("char")
                                        &&
                                        !(attributeValues[j] instanceof String)
                                    )
                                    {
                                        attributeValues[j] = attributeValues[j].toString();
                                    }
                                }
                            }
                            if(dsfound )
                            {
                                obj = immediateMapInternal( 
                                                        ctx, 
                                                        refObjDef,
                                                        allods[d].getKeys(),
                                                        allods[d].getSourceObject(),
                                                        remKeys,
                                                        decodeObjKeysToTabKeys( allods[d], remKeys ),
                                                        attributeValues,
                                                        null
                                                    );
                            }
                        }
                    }
                }
                else
                {
                        obj = immediateMapInternal( 
                                                ctx, 
                                                refObjDef,
                                                null,
                                                refObjDef.getBoMasterTable(),
                                                remKeys,
                                                remKeys,
                                                attributeValues,
                                                null
                                            );
                }
            }
        }
//        if( extraQuery != null && extraQuery.length() > 0 )
//        {
//            qry.append( " AND ( " ).append( extraQuery ).append( " )" );
//        }
//        
//        boObject obj = getObjectFromPrimaryKey( ctx, refObjName, qryPar.toArray() );
//        if( obj == null )
//        {
//            boDefHandler boDef = boDefHandler.getBoDefinition( refObjName );
//            boDefHandler[] subdef = boDef.getBoSubClasses();
//            for (int i = 0;obj == null && i < subdef.length; i++) 
//            {
//                obj = immediateMap( ctx, subdef[i].getName(), qryPar.toArray() );
//            }
//            if( obj == null )
//            {
//                obj = boObject.getBoManager().loadObject( 
//                                        ctx, 
//                                        refObjName,
//                                        qry.toString(),
//                                        qryPar.toArray()
//                                    );
//            }
//            if ( !obj.exists() )
//            {
//    //            obj = boObject.getBoManager().createObject( ctx, refObjName );
//                if( !obj.exists() )
//                {
//                    for (int i = 0; i < locKeys.length; i++) 
//                    { 
//                        if( locKeys[i] != null )
//                        {
//                            obj.getAttribute( remKeys[i] ).setValueObject( row.getObject( locKeys[i] ) );
//                        }
//                        else
//                        {
//                            String value = lockeysLit[i].replaceAll("'","");
//                            obj.getAttribute( remKeys[i] ).setValueString( value );
//                        }
//                    }
//                }
//            }
//            row.updateLong( att.getDefAttribute().getDbName(), obj.getBoui() );
//            putObjectFromPrimaryKey( refObjName, qryPar.toArray(), new Long( obj.getBoui() ) );
//        }
        return obj;
    }
    
    
    
    private static final List convertToListInUpperCase( String[] array )
    {
        List retArray = new ArrayList( array.length );
        for (int i = 0; i < array.length; i++) 
        {
            retArray.add( array[i].toUpperCase() );
        }
        return retArray;
    }
    
    private static final boolean compareValues( Object remval, Object locval )
    {
        boolean changed = false;
        if( remval == null && locval != null )
        {
            changed = true;
        }
        else if ( remval != null && locval == null )
        {
            changed = true;
        }
        else
        {
            changed = !remval.equals( locval );
        }
        return changed;        
    }
    
    public static final boObject immediateMap( EboContext ctx, 
                                               String objectName, 
                                               Object[] objectKeysValues 
                                            ) throws boRuntimeException
                                               
                                               
    {
        boDefHandler objdef = boDefHandler.getBoDefinition( objectName );
        MapType2Def mapdef = MapType2Def.getDataSourceDefinition( objdef );
        if( mapdef != null && objectKeysValues != null )
        {
            ObjectDS[] allods = mapdef.getObjectDataSources().getDataSources();
            for (int d = 0; d < allods.length; d++) 
            {
                ObjectDS ods = allods[d];
                String[] keys = ods.getKeys();
                if( keys.length == objectKeysValues.length )
                {
                    boObject retObj = immediateMapInternal(   
                                ctx, 
                                objdef,
                                ods.getKeys(),
                                ods.getSourceObject(),
                                ods.getKeys(),
                                decodeObjKeysToTabKeys( ods, ods.getKeys() ), 
                                objectKeysValues,
                                ""
                                );
                    if( retObj != null )
                    {
                        return retObj;
                    }
                }
            }
        }
        else
        {
            // Pecorre as SubClasses para verificar se alguma está mapeada.
            boDefHandler subClassesDef[] = objdef.getBoSubClasses();
            for (int i = 0;subClassesDef != null && i < subClassesDef.length; i++) 
            {
                if( subClassesDef[i].getClassType() == boDefHandler.TYPE_CLASS )
                {
                    boObject retObj = immediateMap( ctx, subClassesDef[i].getName(), objectKeysValues );
                    if( retObj != null )
                    {
                        return retObj;
                    }
                }
            }
        }
        return null;
    }
    
    
    protected static boObject immediateMapInternal( EboContext ctx,boDefHandler bodef, String[] keysAtts, String sourceTable, String objkeys[], String[] tabkeys, Object[] objectKeysValues, String extraWhere )
                throws boRuntimeException
    {
        long init = System.currentTimeMillis();
        Connection cn = ctx.getConnectionData();
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        
        if( bodef.getClassType() == boDefHandler.TYPE_CLASS )
        {
            try
            {
                boolean found = true;
                if( keysAtts != null )
                {
                    StringBuffer sql = new StringBuffer("SELECT 1 FROM ");
                    StringBuffer sqlWhere = new StringBuffer();
                    sql.append( sourceTable );
                    sql.append( " WHERE " );
                    for (int i = 0; i < tabkeys.length; i++) 
                    {
                        if( i > 0 ) sqlWhere.append( " AND " );
                        sqlWhere.append( tabkeys[i] );
                        sqlWhere.append( " = ? " );
                    }
                    sql.append( "( " ).append( sqlWhere ).append(  " )" );
                    if( extraWhere != null && extraWhere.length() > 0 )
                    {
                        sql.append( " AND ( " ).append( extraWhere ).append( " )" );
                    }
                    pstm = cn.prepareStatement( sql.toString() );
                    for (int i = 0; i < tabkeys.length; i++) 
                    {
                        if ( objectKeysValues[i] != null )
                        {
                            pstm.setObject( i + 1, objectKeysValues[i] );
                        }
                        else
                        {
                            pstm.setString( i + 1, null );
                        }
                    }
                    rslt = pstm.executeQuery();
                    found = rslt.next();
                }
//                System.out.println( bodef.getName() + ":" + (System.currentTimeMillis() - init) );
    
                // Se as 
                if( found || keysAtts == null )
                {
                    StringBuffer objWhere = new StringBuffer();
                    for (int i = 0; i < objkeys.length; i++) 
                    {
                        if( i > 0 ) objWhere.append( " AND " );
                        objWhere.append( objkeys[i] );
                        objWhere.append( " = ? " );
                    }
    
                    boObject obj = getObjectFromPrimaryKey( ctx, bodef.getName(), objectKeysValues );
                    if( obj == null )
                    {
                        obj = boObject.getBoManager().loadObject( ctx, bodef.getName(), objWhere.toString(), objectKeysValues );
                        if( (!obj.exists() && found ) && keysAtts != null )
                        {
                            for (int i = 0; i < keysAtts.length ; i++) 
                            {
                                obj.getAttribute( keysAtts[i].toLowerCase() ).setValueString( objectKeysValues[i].toString() );
                            }
                            putObjectFromPrimaryKey( bodef.getBoName(), objectKeysValues, new Long( obj.getBoui() ) );
                            obj.setChanged( false );
                        }
                        else if ( !obj.exists() )
                        {
                            obj = null;
                        }
                    }
//                    System.out.println( bodef.getBoName() + ":" + (System.currentTimeMillis() - init) );
                    return obj;  
                }
            }
            catch (Exception e)
            {
                logger.warn("Error on immediateMapInternal:"+e.getMessage());
            }
            finally
            {
                try
                {
                    rslt.close();
                    pstm.close();
                }
                catch (Exception e)
                {
                    
                }
            }
        }
        return null;
    }
    
    public static final boolean isObjectMapped( String objectName )
    {
        boDefHandler objdef = boDefHandler.getBoDefinition( objectName );
        MapType2Def mapdef = MapType2Def.getDataSourceDefinition( objdef );
        return mapdef != null;
    }
    
    public final void invokeWriterClass( ObjectDS sds, boObject object ) throws boRuntimeException
    {
        String writerClassName = sds.getWriterClass(); 
        if( 
            writerClassName != null
            &&
            writerClassName.trim().length() > 0
        )
        {
            writerClassName = writerClassName.trim();
            try
            {
                IDataType2Writer objWriter = (IDataType2Writer)Class.forName( writerClassName ).newInstance();
                objWriter.writeObject( sds, object );    
            }
            catch (InstantiationException e)
            {
                throw new boRuntimeException2("InstantiationException ["+writerClassName+"].\n" + e.getMessage() );
            }
            catch (IllegalAccessException e)
            {
                throw new boRuntimeException2("IllegalAccessException ["+writerClassName+"].\n" + e.getMessage() );
            }
            catch (ClassNotFoundException e)
            {
                throw new boRuntimeException2("ClassNotFoundException ["+writerClassName+"].\n" + e.getMessage() );
            }
        }
    }
    
    
    public static boObject getObjectFromPrimaryKey( EboContext ctx, String objectName, Object[] keysValues )
    {
        boObject ret = null;
        String unique = ExternalPrimaryKey.composeUniqueIdString( objectName, keysValues ) ;
        Long boui = (Long)objectKeysMapped.get( unique );
        // Check if the object still available... 
        if( boui != null )
        {
            try
            {
                ret = boObject.getBoManager().loadObject( ctx, boui.longValue() );
            }
            catch( boRuntimeException e )
            {
                objectKeysMapped.remove( unique );
            }
        }
        return ret;
    }
    public static void putObjectFromPrimaryKey( String objectName, Object[] keysValues, Long boui )
    {
        objectKeysMapped.put( 
                     new ExternalPrimaryKey( objectName, keysValues )
                    ,boui
                );
    }
    
    
    public static class ExternalPrimaryKey
    {
        private String uniqueId;
        public ExternalPrimaryKey( String objectName, Object[] keysValues )
        {
            uniqueId = composeUniqueIdString( objectName, keysValues );
        }
        
        public static String composeUniqueIdString( String objectName, Object[] keysValues )
        {
            String ret = objectName;
            for (int i = 0; i < keysValues.length; i++) 
            {
                ret += String.valueOf( keysValues[i] );     
            }
            return ret;
        }
        
        public int hashCode()
        {
            // TODO:  Override this java.lang.Object method
            return uniqueId.hashCode();
        }
    
        public String toString()
        {
            // TODO:  Override this java.lang.Object method
            return uniqueId.toString();
        }
    
        public boolean equals(Object obj)
        {
            // TODO:  Override this java.lang.Object method
            return uniqueId.equals(obj);
        }
    }
    
    public static final void flagThread( String flag )
    {
        Thread ct = Thread.currentThread();
        ArrayList flags = (ArrayList)flaggedThreads.get( ct );
        if( flags == null ) 
        {
            flags = new ArrayList( 1 );
            flaggedThreads.put( ct, flags );
        }
        if( flags.indexOf( flag ) == -1 )
        {
            flags.add( flag );
        }
    }
    public static final void   unFlagThread( String flag )
    {
        Thread ct = Thread.currentThread();
        ArrayList flags = (ArrayList)flaggedThreads.get( ct );
        if( flags != null )
        {
            flags.remove( flag );
            if( flags.size() == 0 )
            {
                flaggedThreads.remove( ct );
            }
        }
    }
    public static final boolean threadIsFlaged( String flag )
    {
        Thread ct = Thread.currentThread();
        ArrayList flags = (ArrayList)flaggedThreads.get( ct );
        if( flags != null )
        {
            return flags.contains( flag );
        }
        return false;
    }
    
    
    private static String[] decodeObjKeysToTabKeys( ObjectDS ds, String[] objKeys )
    {
        String[] ret = new String[ objKeys.length ];
        List locatts = Arrays.asList( ds.getLocalAttributes() );
        for (int i = 0; i < objKeys.length; i++) 
        {
            int idx = locatts.indexOf( objKeys[i].toLowerCase() );
            if( idx != -1 )
            {
                if( ds.getSQLExpressions()[idx] != null )
                {
                    ret[i] = ds.getSQLExpressions()[idx]; 
                }
                else
                {
                    ret[i] = ds.getRemoteAttributes()[idx]; 
                }
            }
            else
            {
                ret[i] = objKeys[i];
            }
        }
        return ret;
    }
    
    public static final String[] toUpper( String[] arr )
    {
        String[] ret = null;
        if ( arr != null  )
        {
            ret = new String[ arr.length ];
            for (int i = 0; i < arr.length; i++) 
            {
                ret[i] = arr[i]!=null?arr[i].toUpperCase():null;
            }
        }
        return ret;
    }
    
}