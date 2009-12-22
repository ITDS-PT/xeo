/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import java.math.BigDecimal;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import netgest.bo.data.*;
import netgest.bo.data.DataManager;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefHandler;
import netgest.bo.plugins.data.MapType1DataManager;
import netgest.bo.plugins.data.MapType1Def;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.*;
import netgest.utils.DataUtils;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class SyncronizeExternalTables implements boSchedule 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.SyncronizeExternalTables");
    
    /**
     * 
     * @since 
     */
     private String p_parameter;
     private static final int EVENT_BEFORE = 0;
     private static final int EVENT_AFTER = 1;
     
     private ObjectMapEvent  p_eventsclass;
     private BigDecimal      p_lastsyncboui;
     
    public SyncronizeExternalTables()
    {
    }

    public void setParameter(String parameter)
    {
        p_parameter = parameter;
    }
    
    public BigDecimal immediateSync( EboContext ctx, boDefHandler def, String query, long boui ) throws boRuntimeException
    {
        p_parameter = def.getName(  );
        doWork( ctx, null, query, BigDecimal.valueOf( boui ) );
        return p_lastsyncboui;
    }
    public boolean doWork(EboContext ctx, boObject objectSchedule) throws boRuntimeException
    {
       return doWork( ctx, objectSchedule, null, null );
    }
    

    private boolean doWork(EboContext ctx, boObject objectSchedule, String extraquery, BigDecimal boui) throws boRuntimeException
    {
    
        
        boDefHandler handler = boDefHandler.getBoDefinition( p_parameter );
//        boDefObjectDS.ObjectDS ds = handler.getBoDataSources().getObjectDataSources().getDataSources()[0];
        MapType1Def.ObjectDS ds = MapType1Def.getDataSourceDefinition( handler ).getObjectDataSources().getDataSources()[0];
        
        if ( !netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ).isDefault() )
        {
         
            String tablename = ds.getSourceObject(); 
            
//            ctx.beginContainerTransaction();
            boolean transok;

            this.fireEvent(EVENT_BEFORE, ctx,  netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ), handler );
            
    //        // MARTELADA SGES ANACOM 
    //        if( tablename.equalsIgnoreCase("V_HWSGESLOCAL") || tablename.equals("V_EBO_PERFANACOM") )
    //        {
    //            Connection cn = null;
    //            PreparedStatement pstm = null;
    //            boolean importok=true;
    //            try
    //            {
    //                ctx.beginContainerTransaction();
    //                cn = ctx.getConnectionData();
    //                if( tablename.equals("V_HWSGESLOCAL") )
    //                {
    //    //              pstm = cn.prepareStatement( "DELETE FROM V_HWSGESLOCAL WHERE " );
    //    //              pstm.executeUpdate();
    //    //              pstm.close();
    //                
    //                    pstm = cn.prepareStatement( "INSERT INTO V_HWSGESLOCAL ( SELECT * FROM V_HWSGES A WHERE NOT EXISTS ( SELECT * FROM V_HWSGESLOCAL B WHERE A.NID=B.NID ) )" );
    //                    pstm.executeUpdate();
    //                    pstm.close();
    //                }
    //                else if ( tablename.equals("V_EBO_PERFANACOM") )
    //                {
    //                    pstm = cn.prepareStatement( "INSERT INTO V_EBO_PERFANACOM (SELECT * FROM v_ebo_perfanacom_view A WHERE NOT EXISTS ( SELECT * FROM V_EBO_PERFANACOM B WHERE A.EMP_NUM=B.EMP_NUM ) )");
    //                    pstm.executeUpdate();
    //                    pstm.close();
    //                    pstm = cn.prepareStatement( "DELETE FROM v_ebo_perfanacom A WHERE NOT EXISTS ( SELECT * FROM V_EBO_PERFANACOM_VIEW B WHERE A.EMP_NUM=B.EMP_NUM )");
    //                    pstm.executeUpdate();
    //                    pstm.close();
    //                }
    //              // cn.commit();
    //               ctx.commitContainerTransaction();
    //                
    //            }
    //            catch (SQLException e)
    //            {
    //                logger.warn( " Error a sincronizar Hard Tables ", e );
    //            }
    //            finally
    //            {
    //                try
    //                {
    //                    pstm.close();
    //                    cn.close();                
    //                }
    //                catch (Exception e)
    //                {
    //                    
    //                }
    //                if( !importok )
    //                {
    //                    
    //                    ctx.rollbackContainerTransaction();
    //                }
    //                
    //            }
    //        }
    //        //
            
            if( ds.getSchema().length() > 0 )
            {
                tablename = ds.getSchema()+"."+tablename; 
            }
    
            String[] keys = ds.getKeys();
            String[] lkeys = new String[ keys.length ];
            String[] ratts = ds.getRemoteAttributes();
            String[] latts = ds.getLocalAttributes();
            
            boolean havelocal = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ).haveLocalTable();
            for (int i = 0; i < keys.length; i++) 
            {
                if( havelocal )
                {
                    lkeys[i] = keys[i] + "$R";
                }
                else
                {
                    lkeys[i] = keys[i] + "$L";
                }
             }
            String localtable = handler.getBoPhisicalMasterTable();
            if( havelocal )
            {
                localtable += "_LOCAL";
            }
             
            String fields = DataUtils.concatFields( ctx.getDataBaseDriver(), keys );
            StringBuffer query = 
            new StringBuffer("SELECT ")
            .append( fields )
            .append( ", external.*" )
            .append(" FROM ")
            .append( tablename )
            .append( " external  WHERE ");
            
            if( extraquery != null && extraquery.length() > 0 )
            {
                query.append( extraquery );            
                query.append( " AND " );
            }
            
            query.append( " NOT EXISTS ( SELECT " )
            .append( fields )
            .append(" FROM ");
            query.append( localtable ).append(" internal");
            
            query.append(" WHERE " );
            for (int i = 0; i < keys.length ; i++) 
            {
                if( i > 0 )
                {
                    query.append(" AND ");
                }
                query.append("external.")
                .append( keys[i] )
                .append("=")
                .append(" internal.")
                .append( keys[i] )
                .append("$L");
            }
            query.append( ")" );
            
            Object[] keysv = new Object[ keys.length ];
            
            DataSet extDataSet = DataManager.executeNativeQuery( ctx, ds.getDataSource(), query.toString() , 1 , 500 , null );
            if( extDataSet.getRowCount() > 0 )
            {
/*                DataSetMetaData meta = extDataSet.getMetaData();
                for (int i = 0; i < meta.getColumnCount(); i++) 
                {
                    logger.finer( meta.getColumnName( i + 1 ) + "\t|" );
                }
                
                for (int i = 0; i < extDataSet.getRowCount(); i++) 
                {
                    for (int z = 0; z < meta.getColumnCount(); z++) 
                    {
                        logger.finer( extDataSet.rows( i + 1 ).getObject( z + 1 ) + "\t|" );
                    }
                }
*/                
            
                DataSet objDataSet = ObjectDataManager.executeBOQL( ctx, "SELECT "+p_parameter+" where 0=1 ", false);
                while( extDataSet.getRowCount() > 0)
                {
                    for (int z = 0; z < extDataSet.getRowCount(); z++) 
                    {
                        DataRow newrow = objDataSet.createRow();
                        for (int i = 0; i < keys.length; i++) 
                        {
                            newrow.updateString( lkeys[i] , extDataSet.rows( z + 1 ).getString( keys[i] ) );
                            keysv[i] = extDataSet.rows( z + 1 ).getObject( keys[i] );
                        }
                        newrow.updateString( "CLASSNAME", p_parameter );
                        objDataSet.insertRow( newrow );
                    }
                    logger.finest("Registing objects ["+ handler.getBoName() +"]");
                    long ini = System.currentTimeMillis();
                    
                    if( boui != null )
                    {
                        MapType1DataManager.registerObjects( ctx, objDataSet, handler.getBoName(), new long[] { boui.longValue() } );
                    }
                    else
                    {
                        MapType1DataManager.registerObjects( ctx, objDataSet, handler.getBoName() );
                    }
                    
                    logger.finest("Importing ["+p_parameter+"], ["+objDataSet.getRowCount()+"] imported in ["+(System.currentTimeMillis() - ini )+" secs], average for each object is ["+((System.currentTimeMillis() - ini)/objDataSet.getRowCount() )+" ms]");
                    
                    if( extraquery == null )
                    {
                        ctx.close();
                    }
//                    p_lastsyncboui = decodeToBoui( ctx, handler, keysv );
                    if( extDataSet.getRowCount() >= 500 )
                    {
                        extDataSet = DataManager.executeNativeQuery( ctx, ds.getDataSource(), query.toString(), 1 , 500 , null );
                    }
                    else
                    {
                        break;
                    }
                }
            }
            if (netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ).getChecksums() != null )
            { 
    //                detectChangesByCheckSum();
            }
            if (netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ).getTriggers() != null )
            {
                detectChangesByTrigger( ctx, handler, localtable );
            }
            this.fireEvent(EVENT_AFTER, ctx,  netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( handler ), handler );
            
        }
        else
        {
            logger.finest( "Object [" + handler.getName() + "] not synchronized because is not mapped." );
        }
        
        return true; 
    }
    
    public final void detectChangesByTrigger( EboContext ctx, boDefHandler def, String localtable ) throws boRuntimeException
    {
        boolean transok = false;
        boolean my_trans = !ctx.isInTransaction();
        if( my_trans )
            ctx.beginContainerTransaction();
        try
        {
            String tablename = localtable;
            
            
            CallableStatement cstm = ctx.getConnectionData().prepareCall("UPDATE "+ tablename +" SET SYS_MPDC_LASTSYNC=TO_DATE('01-01-0001','DD-MM-YYYY') WHERE SYS_MPDC_LASTSYNC IS NULL");
            int rows = cstm.executeUpdate();
            cstm.close();
            
            String[] keys = (String[])MapType1Def.getDataSourceDefinition( def ).getObjectDataSources().getDataSources()[0].getKeys().clone();
            for (int i = 0; i < keys.length; i++) 
            {
                keys[i] += "$L";
            }
            String fields = DataUtils.concatFields( ctx.getDataBaseDriver(), keys );
             
            if( my_trans )
                ctx.commitContainerTransaction();

            StringBuffer sql = new StringBuffer();
            
            sql.append( "SELECT BOUI,")
            .append( fields )
            .append(" FROM ")
            .append( tablename )
            .append(" WHERE (sys_mpdc_trg_timestamp <> SYS_MPDC_LASTSYNC)");
    //        .append(" OR ( SYS_MPDC_TRG_TIMESTAMP IS NOT NULL AND SYS_MPDC_LASTSYNC IS NULL ) ");
            
            PreparedStatement pstm = ctx.getConnectionData().prepareStatement( sql.toString() );
            ResultSet rslt         = pstm.executeQuery();
            
            if( my_trans )
                ctx.beginContainerTransaction();

            ObjectMap mapObj = (ObjectMap)boCompilerClassLoader.getInstanceFromClassName( ctx, netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( def ).getObjectDataSources().getClassName(), null, ObjectMap.class);
            int cnt = 0;
            while( rslt.next() )
            { 
                try
                {
                    cnt++;
                    Object[] values = new Object[ keys.length ];
                    for (int i = 0; i < keys.length; i++) 
                    {
                        values[ i ] = rslt.getObject( keys[i] ); 
                    }
                    boObject bobj = boObject.getBoManager().loadObject( ctx, rslt.getLong(1) );
                    boolean ret = mapObj.onUpdateTrigger( ctx, bobj , new KeyReference( keys, values ) );
                    
                    if( ret )
                    {
                        try
                        {
                            bobj.setParameter("IsInDetectChangesByTrigger","true"); 
                            bobj.update();
                        }
                        finally 
                        {
                            bobj.removeParameter( "IsInDetectChangesByTrigger" );
                        }
                    }
                    PreparedStatement pstm2 = ctx.getConnectionData().prepareStatement("UPDATE "+tablename+" SET SYS_MPDC_LASTSYNC=sys_mpdc_trg_timestamp WHERE BOUI=? ");
                    pstm2.setLong( 1 , bobj.getBoui() );
                    pstm2.executeUpdate();
                    pstm2.close();
                    if( cnt > 50 )
                    {
                        
                        if (my_trans)
                        {
                            ctx.commitContainerTransaction();
                            ctx.beginContainerTransaction();
                        }
                        cnt =0;
                    }
                }
                catch (Exception e)
                {
                    throw new boRuntimeException2( e );
                }
            }
            rslt.close();
            pstm.close();
            transok = true;
        }
        catch (SQLException e) 
        {
            throw new boRuntimeException2( e );
        }
        finally
        {
            if (my_trans)
            {
                if( transok ) ctx.commitContainerTransaction();
                else ctx.rollbackContainerTransaction();
            }
        }
    }
    
    
    public final void fireEvent(int type, EboContext ctx, MapType1Def ds, boDefHandler def ) throws boRuntimeException
    {
        boolean my_trans = !ctx.isInTransaction();
        
        if( my_trans ) 
            ctx.beginContainerTransaction();
            
        boolean transok = false;
        try
        {
            boolean ret = true;
            Connection cn = null;
            CallableStatement cstm = null;
            
            String classname = ds.getEventsClass();
            String sqltext   = null;
            
            
            
            if( type == EVENT_BEFORE )
            {
                sqltext   = ds.getBeforeEventSQL();
            }
            else
            {
                sqltext   = ds.getAfterEventSQL();
            }
            
            if( sqltext != null )
            {
                try
                {
                    cn = ctx.getConnectionManager().getDedicatedConnection();
                    cstm = cn.prepareCall( sqltext );
                    cstm.execute();
                }
                catch( SQLException e )
                {
                    throw new boRuntimeException2( e );
                }
                finally
                {
                    try
                    {
                        if( cstm != null ) cstm.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                    try
                    {
                        if( cn != null ) cn.close();
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
            }
            
            
            if( classname != null && classname.length() > 0 )
            {
                if( type == EVENT_BEFORE )
                {
                    this.p_eventsclass = (ObjectMapEvent)boCompilerClassLoader.getInstanceFromClassName( ctx, classname, ObjectMapEvent.class, null );
                }
    
                if( p_eventsclass != null )
                {
                    if( type == EVENT_BEFORE )
                    {
                        ret = p_eventsclass.beforeSync( ctx, def );
                    }
                    else 
                    {
                        p_eventsclass.afterSync( ctx, def );
                    }
                }
                
            }
            transok = true;
        }
        finally
        {
            if (my_trans)
            {
                if( transok ) ctx.commitContainerTransaction();
                else ctx.rollbackContainerTransaction();
            }
        }
        
    }
    
    public static BigDecimal decodeToBoui( EboContext ctx, boDefHandler def, Object[] keysValues )
    {
        BigDecimal ret = null;
        String[] keys = MapType1Def.getDataSourceDefinition( def ).getObjectDataSources().getDataSources()[0].getKeys();
        String[] keysl = new String[ keys.length ];
        for (int i = 0; i < keys.length; i++) 
        {
            keysl[ i ] = keys[i] + "$L";
        }
        
        boolean havelocal = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( def ).haveLocalTable();

        String localtable = def.getBoPhisicalMasterTable(  );
        if( havelocal )
        {
            localtable += "_LOCAL";
        }
        
        ResultSet           rslt = null;
        PreparedStatement   pstm = null;
        
        try
        {
            final String SQL_TODECODE = "SELECT BOUI FROM " + localtable + " WHERE "+DataUtils.buildQuery( keysl );
            pstm = ctx.getConnectionData().prepareStatement( SQL_TODECODE );
            for (int i = 0; i < keys.length; i++) 
            {
                pstm.setObject(  i + 1, keysValues[i] );
            }
            
            rslt = pstm.executeQuery(   );
            while( rslt.next() )
            {
                ret = rslt.getBigDecimal( "BOUI" );
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2( e );
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
        return ret;
        
    }
    
    
}