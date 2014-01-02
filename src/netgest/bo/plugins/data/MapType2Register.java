/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import netgest.bo.data.DataManager;
import netgest.bo.data.Driver;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.robots.boSchedule;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.plugins.data.MapType2Def.ObjectDS;
import netgest.bo.runtime.robots.boTextIndexAgent;
import netgest.utils.DataUtils;
import netgest.bo.system.Logger;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;

public class MapType2Register implements boSchedule 
{

    private static Logger logger = Logger.getLogger(MapType2Register.class.getName());
    
    public static final Hashtable CurrentRegisterObjects = new Hashtable();

    String p_name;
    public void setParameter(String parameter)
    {
        p_name = parameter;
    }

    public boolean doWork(EboContext ctx, boObject objectSchedule) throws boRuntimeException
    {
        return registerObject( ctx, boDefHandler.getBoDefinition( p_name ), new Stack(), true );
    }
    public boolean registerObject(EboContext ctx, boDefHandler def, Stack stack, boolean secondPhase ) throws boRuntimeException
    {
        // Check if the current Object is already bean Register
        try
        {
            int wcounter = 0;
            while( CurrentRegisterObjects.containsKey( def.getName() ) )
            {
                if( CurrentRegisterObjects.get( def.getName() ) != Thread.currentThread() )
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("WAITING_CONCURRENT_OBJECT_MAPPING")+" ["+def.getName()+"]");
                    wcounter ++;
                    Thread.sleep( 1000 );
                    if ( wcounter > 300 )
                    {
                        logger.severe(LoggerMessageLocalizer.getMessage("DEADLOCK_DETECTED_IN_OBJECT_MAPPING_300_SEC_OF_WAIT")+" ["+def.getName()+"]");
                        return true;
                    }
                }
                else
                {
                    break;
                }
            }
            CurrentRegisterObjects.put( def.getName(), Thread.currentThread() );
            
            if( def != null )
            {
                if( stack.indexOf( def.getName() ) > -1 )
                {
                    return true;
                }
                
                String sSysDate = ctx.getDataBaseDriver().getDatabaseTimeConstant();
                
                stack.add( def.getName() );    
            
                MapType2Def mapdef = MapType2Def.getDataSourceDefinition( def );
                if( mapdef != null && mapdef.getMapType().equals("2") && mapdef.getPreRegisterObjects() )
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("BEFORE_REGISTER")+" :"  + ctx.getConnectionData().hashCode() );
                    ObjectDS[] allods = mapdef.getObjectDataSources().getDataSources();
                    for (int d = 0 ; d < allods.length; d ++ ) 
                    {
                        ArrayList objattsFlds   = new ArrayList();
                    
                        ObjectDS ods = allods[ d ];
                        String remotTable = ods.getSourceObject();
                        String localTable  = def.getBoPhisicalMasterTable();
                        
                        StringBuffer sbSelect = new StringBuffer();
                        sbSelect.append( "SELECT " );
                        String[] keys = ods.getKeys();
                        for (int i = 0; i < keys.length; i++) 
                        {   
                            if( i > 0 ) sbSelect.append( ", " );
                            sbSelect.append( keys[i] );
                        }
    
                        String[]    locatts         = ods.getLocalAttributes();
                        
                        String[][]  remobjrel       = ods.getObjectRelationRemoteKeys();
                        String[][]  locobjrel       = ods.getObjectRelationLocalKeys();
                        String[][]  locobjrellit    = ods.getObjectRelationLocalKeysLiteral();
                        
                        for (int i = 0; i < locatts.length; i++) 
                        {
                            if( locobjrel[ i ] != null )
                            {
                                boDefAttribute defatt = def.getAttributeRef( locatts[i] );
                                if( !defatt.getDbIsTabled() )
                                {
                                    if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
                                    {
                                        for (int j = 0; j < locobjrel[i].length; j++) 
                                        {
                                            if( locobjrellit[i][j] == null )
                                            {
                                                sbSelect.append( ", " );
                                                sbSelect.append( locobjrel[i][j] );
                                            }
                                        }
                                    }
                                }
                                objattsFlds.add( locatts[i] );
                            }
                        }
                        
                        
                        StringBuffer sb = new StringBuffer( sbSelect.toString() );
                        sb.append( " FROM " );
                        sb.append( remotTable ).append( " REMOTETABLE" );
                        
                        sb.append( " WHERE NOT EXISTS ( SELECT 1 FROM " );
                        sb.append( localTable ).append( " LOCALTABLE" );
                        sb.append( " WHERE " );
        
                        for (int i = 0; i < keys.length; i++) 
                        {   
                            if( i > 0 ) sb.append( " AND " );
                            sb.append( "LOCALTABLE."  );
                            sb.append( keys[i] );
                            sb.append( " = " );
                            sb.append( "REMOTETABLE." );
                            sb.append( keys[i] );
                        }
                        sb.append( ")" );
                        if( ods.getWhereClause() != null && ods.getWhereClause().length() > 0 )
                        {
                            sb.append(" AND ( ");
                            sb.append( ods.getWhereClause() );
                            sb.append(" ) ");
                        }
    
                        StringBuffer inslocsql = new StringBuffer();
                        inslocsql.append( "INSERT INTO " );
                        inslocsql.append( localTable );
                        inslocsql.append( " ( BOUI, CLASSNAME, " );
                        inslocsql.append( MapType2Plugin.FLAG_FIELD_NAME );
                        
                        for (int i = 0; i < keys.length; i++) 
                        {
                            inslocsql.append( ", " );
                            inslocsql.append( keys[i] );
                        }
                        
    //                    for (int i = 0; i < objattsFlds.size(); i++) 
    //                    {
    //                        boDefAttribute defatt = def.getAttributeRef( objattsFlds.get( i ).toString() );
    //                        inslocsql.append( ", " );
    //                        inslocsql.append( defatt.getDbName() );
    //                    }
                        
                        inslocsql.append( ") VALUES ( ?, ?,'N'" );
                        for (int i = 0; i < keys.length; i++) 
                        {
                            inslocsql.append( ", " );
                            inslocsql.append( "?" );
                        }
    
    //                    for (int i = 0; i < objattsFlds.size(); i++)
    //                    {
    //                        inslocsql.append( ", ?" );
    //                    }
    
                        inslocsql.append( ")" );
                        
                        
                        
                        StringBuffer regsql = new StringBuffer();
                        regsql.append( "insert into ebo_registry" );
                        regsql.append(         "   (sys_user, sys_icn, sys_dtcreate, sys_dtsave, ui$, ui_version, name, clsid, clsid_major_version, clsid_minor_version, boui, classname) ");
                        regsql.append(         "   values " );
                        regsql.append(         "  ( ? , 1 , " + sSysDate + " ,  " + sSysDate + " , ?, ?, ?, ?, ? , ?, ?, ?) ");
                        
                        PreparedStatement pstmExternal  = null;
                        PreparedStatement pstmInsert    = null;
                        PreparedStatement pstmRegistry  = null;
                        ResultSet         rsltExternal  = null;
                        
                        ArrayList   regBouis = new ArrayList();
                        
                        try 
                        {
                            Connection cn = ctx.getConnectionData();
                            pstmExternal = cn.prepareStatement( sb.toString(), ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
                            rsltExternal = pstmExternal.executeQuery();
                            ctx.beginContainerTransaction();
                            Connection tcn = ctx.getConnectionData();
                            pstmRegistry = tcn.prepareStatement( regsql.toString() );
                            pstmInsert   = tcn.prepareStatement( inslocsql.toString() );
                            
                            int counter = 0;
                            int totalcounter = 0;
                            long initTime = System.currentTimeMillis();
                            while( rsltExternal.next() )
                            {
                                
                                counter++;
                                totalcounter++;
                                
                                Object[] pkvalues = new Object[ keys.length ];
                                for (int i = 0; i < keys.length; i++) 
                                {
                                    pkvalues[i] = rsltExternal.getObject( keys[i] );
                                }
                                boObject obj = MapType2DataManager.getObjectFromPrimaryKey( ctx, def.getName(), pkvalues );
                                
                                long newboui = 0;
                                if( obj != null )
                                {
                                    newboui = obj.getBoui();
                                }
                                else
                                {
                                    newboui = 
                                    	DataUtils.getSystemDBSequence( 
                                                    ctx,
                                                    "borptsequence",
                                                    DataUtils.SEQUENCE_NEXTVAL
                                                );
                                }
                                pstmRegistry.setString(1, ctx.getSysUser().getUserName());
                                pstmRegistry.setLong(2,  newboui );
                                pstmRegistry.setLong(3, 1);
                                pstmRegistry.setString(4, def.getName() );
                                pstmRegistry.setString(5, def.getName() );
                                pstmRegistry.setLong(6, Long.parseLong(def.getBoMajorVersion()) );
                                pstmRegistry.setLong(7, Long.parseLong(def.getBoMinorVersion()) );
                                pstmRegistry.setLong(8, newboui );
                                pstmRegistry.setString(9, "Ebo_Registry");
                                pstmRegistry.addBatch();
                                
                                if( obj == null || !obj.exists() )
                                {
                                    // Register Object
                                    pstmInsert.setLong  ( 1, newboui );
                                    pstmInsert.setString( 2, def.getName() );
                                    
                                    int pstmInsertPos = 3;
                                    
                                    for (int i = 0; i < keys.length; i++) 
                                    {
                                        pstmInsert.setObject( pstmInsertPos, pkvalues[i] );
                                        pstmInsertPos++;
                                    }
                                    pstmInsert.addBatch();
                                    regBouis.add( new Long( newboui ) );
                                }
    
                                if( counter >= 1000 )
                                {
                                    pstmRegistry.executeBatch();
                                    pstmInsert.executeBatch();
                                    
                                    pstmRegistry.close();
                                    pstmInsert.close();
                                    
                                    ctx.commitContainerTransaction();
    //                                boTextIndexAgent.addToQueue( regBouis, def.getName() );
                                    regBouis = new ArrayList();
                                    ctx.beginContainerTransaction();
                                    counter = 0;

                                    Connection tcn1 = ctx.getConnectionData();
                                    pstmRegistry = tcn1.prepareStatement( regsql.toString() );
                                    pstmInsert   = tcn1.prepareStatement( inslocsql.toString() );

                                    
                                    logger.finest( LoggerMessageLocalizer.getMessage("COMMITTING")+" [ "+totalcounter+" ] [ Ebo_Registry/INSERT ] "+LoggerMessageLocalizer.getMessage("AVERAGED_REGISTRY_IS")+" ["+
                                                    (System.currentTimeMillis()-initTime) / totalcounter
                                                    +"] ms" 
                                                );
                                    
                                }
                                
                            }
                            if( counter > 0 )
                            {
                                pstmRegistry.executeBatch();
                                pstmInsert.executeBatch();
                                boTextIndexAgentBussinessLogic.addToQueue( ctx,regBouis, def.getName() );
                                regBouis = new ArrayList();
                                ctx.commitContainerTransaction();
    
                                logger.finest( LoggerMessageLocalizer.getMessage("COMMITTING")+" [ "+totalcounter+" ] [ Ebo_Registry/INSERT ] "+LoggerMessageLocalizer.getMessage("AVERAGED_REGISTRY_IS")+" ["+
                                                (System.currentTimeMillis()-initTime) / totalcounter
                                                +"] ms" 
                                            );
    
                            }
                            
                            for ( int i = 0; i < locatts.length; i++ )
                            {
                                if( locobjrel[ i ] != null )
                                {
                                    boDefAttribute defatt = def.getAttributeRef( locatts[i] );
                                    if( !defatt.getDbIsTabled() )
                                    {
                                        if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
                                        {
                                            boDefHandler refdef = defatt.getReferencedObjectDef();
                                            this.registerObject( ctx, refdef, stack, false );
                                            
                                            boDefHandler[] allsubs = refdef.getBoSubClasses();
                                            for (int s=0;allsubs != null && s < allsubs.length ; s++) 
                                            {
                                                this.registerObject( ctx, allsubs[s] , stack, false );
                                            }
                                        }
                                    }
                                }
                            }
                            
                            rsltExternal.close();
                            if( secondPhase )
                            {
                                MapType2DataManager.flagThread( MapType2DataManager.FLAG_MAPTYPE2_DISABLEEVENTS );
                                MapType2DataManager.flagThread( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS );
                            
                                StringBuffer sqlp2 = new StringBuffer(sbSelect.toString());
                                sqlp2.append( " FROM " ).append( def.getBoMasterTable() );
                                sqlp2.append( " WHERE SYS_ICN IS NULL AND CLASSNAME='"+def.getName()+"'" );
                                
                                PreparedStatement   pstmp2 = cn.prepareStatement( sqlp2.toString() );
                                ResultSet           rsltp2 = pstmp2.executeQuery();
    
                                StringBuffer sqlUpdate = new StringBuffer();
                                sqlUpdate.append( "UPDATE " );
                                sqlUpdate.append( def.getBoPhisicalMasterTable() );
                                sqlUpdate.append( " SET SYS_ICN = 0" );
                                
                                initTime = System.currentTimeMillis();
                                counter = 0;
                                totalcounter = 0;
                                
                                for (int i = 0; i < objattsFlds.size(); i++) 
                                {
                                    sqlUpdate.append( ", " );
                                    boDefAttribute defatt = def.getAttributeRef( objattsFlds.get( i ).toString() );
                                    sqlUpdate.append( defatt.getDbName() );
                                    sqlUpdate.append( " = ? " );
                                }
                                
                                sqlUpdate.append( " WHERE " );
                                
                                StringBuffer sqlUpdateWhere = new StringBuffer();
                                for (int i = 0; i < keys.length; i++) 
                                {
                                    if( i > 0 ) sqlUpdateWhere.append( " AND " );
                                    sqlUpdateWhere.append( keys[i] );
                                    sqlUpdateWhere.append( "=?" );
                                }
                                sqlUpdate.append( sqlUpdateWhere );
                                
                                List localAttsList = Arrays.asList( locatts );
                                
                                boolean securityRows = true;//def.implementsSecurityRowObjects();
                                
                                Object[] pkobj = new Object[keys.length];
                                
                                
                                ctx.beginContainerTransaction();
                                PreparedStatement pstmUpd = ctx.getConnectionData().prepareStatement( sqlUpdate.toString() );
                                
                                while( rsltp2.next() )
                                {
                                    try
                                    {
                                        counter++;
                                        totalcounter++;
                                        
                                        boObject obj = null;
                                        if( securityRows )
                                        {
                                            for (int i = 0; i < keys.length; i++) 
                                            {
                                                pkobj[i] = rsltp2.getObject( keys[i] );
                                                if( pkobj[i] instanceof java.sql.Date )
                                                {
                                                    pkobj[i] = rsltp2.getTimestamp( keys[i] );     
                                                }
                                            }
                                            obj = boObject.getBoManager().loadObject( ctx, def.getName(), sqlUpdateWhere.toString(), pkobj );
                                        }
                                        int pstmInsertPos = 1;
                                        for (int oai = 0; oai < objattsFlds.size(); oai++) 
                                        {
                                            int i = localAttsList.indexOf( objattsFlds.get( oai ) );
                                            if( locobjrel[ i ] != null )
                                            {
                                                boDefAttribute defatt = def.getAttributeRef( locatts[i] );
                                                if( !defatt.getDbIsTabled() )
                                                {
                                                    if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
                                                    {
                                                        String[]    remkeys         = remobjrel[i];
                                                        String[]    lockeys         = ods.getObjectRelationLocalKeys()[i];
                                                        String[]    lockeysLit      = ods.getObjectRelationLocalKeysLiteral()[i];
                                                        
                                                        Object[]    keysvalues = new Object[ remkeys.length ];
                                                        boolean allkeysfilled = true;
                                                        for (int j=0;j < keysvalues.length; j++ ) 
                                                        {
                                                            if( lockeysLit[j] == null )
                                                            {
                                                                keysvalues[j] =  rsltp2.getObject( lockeys[j] );
                                                                if( keysvalues[j] == null )
                                                                {
                                                                    allkeysfilled = false;   
                                                                    break;
                                                                }
                                                            }
                                                            else
                                                            {
                                                                keysvalues[j] =  lockeysLit[j];
                                                            }
                                                        }
                                                        boObject refobj = null;
                                                        if( allkeysfilled )
                                                        {
                                                            refobj = MapType2DataManager.loadExternalObject
                                                                                                ( 
                                                                                                    ctx,
                                                                                                    defatt.getReferencedObjectName(),
                                                                                                    lockeys,
                                                                                                    remkeys,
                                                                                                    lockeysLit,
                                                                                                    "",
                                                                                                    keysvalues,
                                                                                                    true
                                                                                                );
                                                        }
                                                        
                                                        
                                                        if( securityRows )
                                                        {
                                                            if( refobj != null )
                                                            {
                                                                defatt = def.getAttributeRef( objattsFlds.get( oai ).toString() );
                                                                obj.getDataRow().updateLong( defatt.getDbName() , refobj.getBoui() );
                                                            }
                                                        }
                                                        else
                                                        {
                                                            if( refobj != null )                   
                                                            {
                                                                pstmUpd.setLong( pstmInsertPos , refobj.getBoui() );
                                                            }
                                                            else
                                                            {
                                                                pstmUpd.setBigDecimal( pstmInsertPos, null );
                                                            }
                                                            pstmInsertPos++;
                                                        }
                                                        
                                                    }
                                                }
                                            }
                                        }
                                        
                                        if( securityRows )
                                        {
                                            // IF Object compute secorityKeys.. must save via convencional update
                                            obj.computeSecurityKeys( true );
                                            obj.getDataRow().setChanged(true);
                                            ObjectDataManager.updateObjectData( obj );
                                        }
                                        else
                                        {
                                            for (int i = 0; i < keys.length; i++) 
                                            {
                                                pstmUpd.setObject( pstmInsertPos++, rsltp2.getObject( keys[i] ) );
                                            }
                                            pstmUpd.addBatch();
                                        }
        
                                        if( counter >= 1000 )
                                        {
                                            counter = 0;
                                            int[] x = pstmUpd.executeBatch();
                                            ctx.commitContainerTransaction();
                                            ctx.beginContainerTransaction();
                                            pstmUpd.close();
                                            pstmUpd = ctx.getConnectionData().prepareStatement( sqlUpdate.toString() );
                                            
                                            
                                            logger.finest( LoggerMessageLocalizer.getMessage("COMMITTING")+" [ "+totalcounter+" ] "+LoggerMessageLocalizer.getMessage("AVERAGED_REGISTRY_IS")+" ["+
                                                            (System.currentTimeMillis()-initTime) / totalcounter
                                                            +"] ms" 
                                                        );
                                            ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( ctx.poolUniqueId() );
                                            
                                        }
                                    }
                                    catch (Exception e)
                                    {
                                        String error = MessageLocalizer.getMessage("ERROR_REGISTERING_OBJECT")+" ["+def.getName()+"] "+MessageLocalizer.getMessage("WITH_KEY")+" ["  ;
                                        for (int i = 0; i < pkobj.length; i++) 
                                        {
                                            error += String.valueOf( pkobj[i] );
                                        }
                                        error += "]";
                                        
                                        logger.severe( error, e );
                                    }
                                    
                                }
                                rsltp2.close();
                                pstmp2.close();
                                
                                if( counter > 0 )
                                {
                                    pstmUpd.executeBatch();
                                    pstmUpd.close();
                                    ctx.commitContainerTransaction();
                                    logger.finest(LoggerMessageLocalizer.getMessage("COMMITTING")+" [ "+totalcounter+" ] "+LoggerMessageLocalizer.getMessage("AVERAGED_REGISTRY_IS")+" ["+
                                                    (System.currentTimeMillis()-initTime) / totalcounter
                                                    +"] ms" 
                                                );
                                }
                                if( pstmUpd != null )
                                	pstmUpd.close();
                            }
    
    
    //                        StringBuffer sqlWhere = new StringBuffer();
    //                        boolean appendAnd = false;
    //                        for ( int i = 0; i < locatts.length; i++ )
    //                        {
    //                            if( locobjrel[ i ] != null )
    //                            {
    //                                boDefAttribute defatt = def.getAttributeRef( locatts[i] );
    //                                if( !defatt.getDbIsTabled() )
    //                                {
    //                                    if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
    //                                    {
    //                                        boDefHandler refdef = defatt.getReferencedObjectDef();
    //                                        this.setParameter( refdef.getName() );
    //                                        this.doWork( ctx, objectSchedule );
    //                                        
    //                                        boDefHandler[] allsubs = refdef.getBoSubClasses();
    //                                        for (int s=0;allsubs != null && s < allsubs.length ; s++) 
    //                                        {
    //                                            this.setParameter( allsubs[s].getName() );
    //                                            this.doWork( ctx, objectSchedule );
    //                                        }
    //                                    }
    //                                    String[]    remkeys         = remobjrel[i];
    //                                    String[]    lockeys         = ods.getObjectRelationLocalKeys()[i];
    //                                    String[]    lockeysLit      = ods.getObjectRelationLocalKeysLiteral()[i];
    //                                    
    //                                    Object[]    keysvalues = new Object[ remkeys.length ];
    //                                    
    //                                    if( appendAnd ) sqlWhere.append( " AND " );
    //                                    
    //                                    sqlWhere.append( "(  " );
    //                                    sqlWhere.append( defatt.getDbName() );
    //                                    sqlWhere.append( " IS NULL AND ( " );
    //                                    
    //                                    boolean appendAnd2 = false;;
    //                                    for (int j=0;j < keysvalues.length; j++ ) 
    //                                    {
    //                                        if( lockeysLit[j] == null )
    //                                        {
    //                                            if( appendAnd2 ) sqlWhere.append( " AND " );
    //                                            sqlWhere.append( lockeys[j] );
    //                                            sqlWhere.append( " IS NOT NULL " );
    //                                            appendAnd2 = true;
    //                                        }
    //                                        else
    //                                        {
    //                                            keysvalues[j] =  lockeysLit[j];
    //                                        }
    //                                    }
    //                                    sqlWhere.append( ")" );
    //                                    appendAnd = true;
    //                                }
    //                            }
    //                        }
    //                        if( sqlWhere.length() > 0 )
    //                        {
    //                            StringBuffer select = new StringBuffer();
    //                            select.append( "SELECT " );
    //                            for (int i = 0; i < objattsFlds.size(); i++) 
    //                            {
    //                                if( i > 0 ) select.append( ", " );
    //                                select.append( objattsFlds.get( i ) );
    //                            }
    //                            select.append( " FROM " );
    //                            select.append( def.getBoMasterTable() );
    //                            select.append( " WHERE " );
    //                            select.append( sqlWhere );
    //                        }
    
                            
                            
                        } 
                        catch (Exception ex)  
                        {
                            ex.printStackTrace();
                            throw new RuntimeException( ex );
                        }   
                        finally 
                        {
                            try
                            {
                                if( pstmRegistry != null )  pstmRegistry.close();
                                if( pstmInsert   != null )  pstmInsert.close();
                                if( pstmExternal != null )  pstmExternal.close();
                                if( rsltExternal != null )  rsltExternal.close();
                            }
                            catch (Exception e){}
                            ctx.rollbackContainerTransaction();
                            MapType2DataManager.unFlagThread( MapType2DataManager.FLAG_MAPTYPE2_DISABLEEVENTS );
                            MapType2DataManager.unFlagThread( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS );
                        }
                    }
                }
                logger.finest(LoggerMessageLocalizer.getMessage("AFTER_REGISTER")+" :"  + ctx.getConnectionData().hashCode() );
                
            }
        }
        catch( InterruptedException e )
        {
            
        }
        finally
        {
            CurrentRegisterObjects.remove( def.getName() );
        }
        return true;
    }
}

//                                for (int i = 0; i < locatts.length; i++) 
//                                {
//                                    if( locobjrel[ i ] != null )
//                                    {
//                                        boDefAttribute defatt = def.getAttributeRef( locatts[i] );
//                                        if( !defatt.getDbIsTabled() )
//                                        {
//                                            if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
//                                            {
//                                                String[]    remkeys         = remobjrel[i];
//                                                String[]    lockeys         = ods.getObjectRelationLocalKeys()[i];
//                                                String[]    lockeysLit      = ods.getObjectRelationLocalKeysLiteral()[i];
//                                                
//                                                Object[]    keysvalues = new Object[ remkeys.length ];
//                                                for (int j=0;j < keysvalues.length; j++ ) 
//                                                {
//                                                    if( lockeysLit[j] == null )
//                                                    {
//                                                        keysvalues[j] =  rsltExternal.getObject( lockeys[j] );
//                                                    }
//                                                    else
//                                                    {
//                                                        keysvalues[j] =  lockeysLit[j];
//                                                    }
//                                                }
//                                                boObject refobj = MapType2DataManager.loadExternalObject
//                                                                                    ( 
//                                                                                        ctx,
//                                                                                        defatt.getReferencedObjectName(),
//                                                                                        lockeys,
//                                                                                        remkeys,
//                                                                                        lockeysLit,
//                                                                                        ods.getWhereClause(),
//                                                                                        keysvalues
//                                                                                    );
//                                                if( refobj != null )                   
//                                                {
//                                                    pstmInsert.setLong( pstmInsertPos , refobj.getBoui() );
//                                                    refobj.update();
//                                                }
//                                                else
//                                                {
//                                                    pstmInsert.setBigDecimal( pstmInsertPos, null );
//                                                }
//                                                pstmInsertPos++;
//                                            }
//                                        }
//                                    }
//                                }


//                                if( true )
//                                {
//                                    if( obj == null )
//                                    {
//                                        obj = boObject.getBoManager().createObject( ctx, def.getName(), newboui );
//                                        for (int i = 0; i < keys.length; i++) 
//                                        {
//                                            obj.getAttribute( keys[i].toLowerCase() ).setValueObject( pkvalues[i] );
//                                        }
//                                    }
//                                    obj.getDataRow().updateString("CLASSNAME", def.getName() );
//                                    obj.setParameter( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS ,"YES" );
//                                    obj.computeSecurityKeys( true );
//
//
//                                    for (int i = 0; i < locatts.length; i++) 
//                                    {
//                                        if( locobjrel[ i ] != null )
//                                        {
//                                            boDefAttribute defatt = def.getAttributeRef( locatts[i] );
//                                            if( !defatt.getDbIsTabled() )
//                                            {
//                                                if( defatt.getRelationType() == defatt.RELATION_1_TO_1 )
//                                                {
//                                                    String[]    remkeys         = remobjrel[i];
//                                                    String[]    lockeys         = ods.getObjectRelationLocalKeys()[i];
//                                                    String[]    lockeysLit      = ods.getObjectRelationLocalKeysLiteral()[i];
//                                                    
//                                                    Object[]    keysvalues = new Object[ remkeys.length ];
//                                                    for (int j=0;j < keysvalues.length; j++ ) 
//                                                    {
//                                                        if( lockeysLit[j] == null )
//                                                        {
//                                                            keysvalues[j] =  rsltExternal.getObject( lockeys[j] );
//                                                        }
//                                                        else
//                                                        {
//                                                            keysvalues[j] =  lockeysLit[j];
//                                                        }
//                                                    }
//                                                    boObject refobj = MapType2DataManager.loadExternalObject
//                                                                                        ( 
//                                                                                            ctx,
//                                                                                            defatt.getReferencedObjectName(),
//                                                                                            lockeys,
//                                                                                            remkeys,
//                                                                                            lockeysLit,
//                                                                                            ods.getWhereClause(),
//                                                                                            keysvalues
//                                                                                        );
//                                                    if( refobj != null )                   
//                                                    {
//                                                        obj.getAttribute( locatts[i] ).setObject( refobj );
//                                                        if( !refobj.exists() )
//                                                        {
//                                                            refobj.setParameter( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS,"YES" );
//                                                            ObjectDataManager.updateObjectData( obj );
//                                                            refobj.removeParameter( MapType2DataManager.FLAG_MAPTYPE2_DISABLEWRITERS );
//                                                        }
//                                                    }
//                                                }
//                                            }
//                                        }
//                                    }
//
//
//                                    ObjectDataManager.updateObjectData( obj );
//                                    newboui = obj.getBoui();
//                                    
//                                }
//                                else
//                                {
