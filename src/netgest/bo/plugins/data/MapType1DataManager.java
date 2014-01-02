/*Enconding=UTF-8*/
package netgest.bo.plugins.data;
import java.math.*;

import java.sql.*;

import java.util.*;

import javax.transaction.*;

import netgest.bo.data.*;
import netgest.bo.def.*;
import netgest.bo.ejb.impl.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.robots.*;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;
import netgest.bo.system.*;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.plugins.data.MapType1Def;
import netgest.bo.plugins.data.MapType1Def.ObjectDS;
import netgest.bo.plugins.data.MapType1Def.ObjectDataSource;
import netgest.bo.plugins.data.MapType1Def.ChangeDetectionHandler;

import netgest.utils.*;

import netgest.bo.system.Logger;
import netgest.bo.plugins.IDataManager;


public class MapType1DataManager implements IDataManager
{

    private static Logger logger = Logger.getLogger( netgest.bo.plugins.data.MapType1DataManager.class.getName() );

    public static void registerObjects(EboContext ctx, DataSet data, String objectName) throws boRuntimeException
    {
        registerObjects(ctx, data, objectName, null );
    }


    public static void registerObjects(EboContext ctx, DataSet data, String objectName, long[] pboui )
        throws boRuntimeException
    {
        boDefHandler objdef = boDefHandler.getBoDefinition(objectName);
        boolean register = false;
        boolean havelocaltable = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).haveLocalTable();
        ArrayList toRegister = new ArrayList();
        UserTransaction ut = null;

        try
        {
        	String sSysDate = ctx.getDataBaseDriver().getDatabaseTimeConstant();

        	String ebo_registryFullTableName = "";
            String schemaName = boRepository.getDefaultSchemaName(ctx.getApplication());
            if(schemaName != null && !"".equals(schemaName))
            {
                ebo_registryFullTableName = schemaName + ".";
            }
            ebo_registryFullTableName += "ebo_registry";

            for (int i = 0; i < data.getRowCount(); i++)
            {
                if ((data.rows(i + 1).getObject("BOUI")) == null)
                {
                    register = true;
                    break;
                }
            }

            if (register)
            {
                boolean ok = false;

                try
                {
//                    beginTransaction(ctx, null);
                    int i = 0;

                    boolean havedata = false;

                    while (i < data.getRowCount())
                    {
                        String classname = data.rows(i + 1).getString("CLASSNAME");
                        boDefHandler classdef = boDefHandler.getBoDefinition(classname);

                        DataSet dtattoupd = ObjectDataManager.executeBOQL( ctx, "SELECT "+classname+" where 0=1 ", false);
                        //DataSet dtattoupd = ObjectDataManager.getEmptyDataSet(ctx,
                        //        classdef);
                        boolean nextset = false;

                        for (int z = i; z < data.getRowCount(); z++)
                        {
                            DataRow currrow = data.rows(z + 1);
                            Object boui = currrow.getObject("BOUI");

                            if ((boui == null) &&
                                    classname.equals(currrow.getString(
                                            "CLASSNAME")))
                            {
                                //                                havedata = true;

                                BigDecimal nr = null;

                                if( pboui != null && pboui.length > ( z ) )
                                {
                                    nr = BigDecimal.valueOf( pboui[z] );
                                }
                                else
                                {
                                    nr = BigDecimal.valueOf(
                                    			DataUtils.getSystemDBSequence(ctx, "borptsequence", DataUtils.SEQUENCE_NEXTVAL )
                                    		);
                                }
                                currrow.updateBigDecimal("BOUI", nr);


                                toRegister.add(new Long(nr.longValue()));
                                boTextIndexAgentBussinessLogic.addToQueue(ctx,toRegister, objectName);
                                toRegister.clear();

                                DataRow newrow = dtattoupd.createRow();
                                int cc = data.getMetaData().getColumnCount();

                                for (int g = 1; g <= cc; g++)
                                {
                                    newrow.updateObject(data.getMetaData()
                                                            .getColumnName(g),
                                        currrow.getObject(g));
                                }

                                dtattoupd.insertRow(newrow);
                            }
                            else if (!nextset && (boui == null))
                            {
                                nextset = true;
                                i = z;
                            }
                        }

                        boolean everyOneCommit = true;
                        if( !everyOneCommit )
                        {
                            ctx.beginContainerTransaction();
                        }

                        if (dtattoupd.getRowCount() > 0)
                        {
                            String hclassname  = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).getObjectDataSources().getClassName();
                            boolean fireEvents = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).getObjectDataSources().getFireEvents();

                            if( hclassname != null && hclassname.length() > 0 )
                            {
                                try
                                {
                                    boCompilerClassLoader cl = ctx.getApplication().getCompilingClassLoader();
                                    Class       objclass        = cl.loadClass( hclassname );
                                    ObjectMap   objmaphandler   = ( ObjectMap )objclass.newInstance();

                                    DataSet[] objectdatas = dtattoupd.split();
                                    for (int z = 0; z < objectdatas.length; z++)
                                    {
                                        long cboui = objectdatas[z].rows( 1 ).getLong("BOUI");
                                        boObject newobj = null;
//                                        boObject newobj = getObject( ctx, objectName );
//                                        newobj.setEboContext( ctx );
                                        objectdatas[z].setForInsert();

                                        if( everyOneCommit )
                                        {
                                            ctx.beginTransaction();
                                        }
                                        boolean commit = false;
                                        try
                                        {

                                            String[] keys = MapType1Def.getDataSourceDefinition( objdef ).getObjectDataSources().getDataSources()[0].getKeys();
                                            Object values[] = new Object[ keys.length ];
                                            for (int k = 0; k < keys.length; k++)
                                            {
                                                if( havelocaltable )
                                                {
                                                    values[k] = objectdatas[z].rows( 1 ).getObject( keys[k]+"$R" );
                                                }
                                                else
                                                {
                                                    values[k] = objectdatas[z].rows( 1 ).getObject( keys[k]+"$L" );
                                                }
                                            }

                                            try
                                            {
                                                objectdatas[i].rows( 1 ).updateLong( "BOUI", cboui );
                                                newobj = newobj.getBoManager().createObject( ctx, objectName, objectdatas[z] );
                                                newobj.setParameter("IsInDetectChangesByTrigger","true");
                                                ctx.getApplication().getMemoryArchive().getPoolManager().putObject( newobj, "BOOBJECT:BOUI[" + cboui + "]:" );
                                                boolean objisok = objmaphandler.onInsertTrigger( ctx, newobj, new KeyReference( keys, values ) );
                                                if( objisok )
                                                {
                                                    if( fireEvents )
                                                    {
                                                            newobj.update();
                                                    }
                                                    else
                                                    {
                                                        PreparedStatement pstm = ctx.getConnectionData()
                                                                                    .prepareStatement("insert into " + ebo_registryFullTableName +

                                                                "   (sys_user, sys_icn, sys_dtcreate, sys_dtsave, ui$, ui_version, name, clsid, clsid_major_version, clsid_minor_version, boui, classname) " +
                                                                "   values " +
                                                                "  ( ? ,1 , " + sSysDate + " ,  " + sSysDate + " , ?, ?, ?, ?, ? , ?, ?, ?) ");

                                                        pstm.setString(1, ctx.getSysUser().getUserName());
                                                        pstm.setLong(2, cboui );
                                                        pstm.setLong(3, 1);
                                                        pstm.setString(4, classdef.getName());
                                                        pstm.setString(5, classdef.getName());
                                                        pstm.setString(6, classdef.getBoMajorVersion());
                                                        pstm.setString(7, classdef.getBoMinorVersion());
                                                        pstm.setLong(8, cboui);
                                                        pstm.setString(9, "Ebo_Registry");
                                                        pstm.executeUpdate();
                                                        pstm.close();
                                                        newobj.set_IsInOnSave( boObject.UPDATESTATUS_UPDATING );
                                                        boObjectUpdateQueue queue = boManagerBean.buildUpdateQueueToSave( ctx, newobj, null );
//                                                        boObject.getBoManager().
                                                        boManagerBean.updateQueue( ctx, queue.getObjectsToSave(), boObjectUpdateQueue.MODE_SAVE_FORCED );
                                                        ObjectDataManager.updateObjectData( newobj );
                                                        boReferencesManager.updateReferences( newobj );
                                                        newobj.set_IsInOnSave( boObject.UPDATESTATUS_IDLE );
                                                        ctx.addTransactedObject( newobj );
                                                    }
                                                }
                                                newobj.removeParameter("IsInDetectChangesByTrigger");
                                                ctx.getApplication().getMemoryArchive().getPoolManager().destroyObject( newobj );
                                                commit = true;

                                            }
                                            catch (Exception e)
                                            {
                                                e.printStackTrace();
                                                StringBuffer logKeys = new StringBuffer();
                                                for (int y = 0; y < keys.length; y++)
                                                {
                                                    logKeys.append("[").append(keys[y]).append("=").append(values[y]).append("]");
                                                }

                                                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_IMPORTING")+" ["+ objdef.getName() +"]. "+LoggerMessageLocalizer.getMessage("THE_FOLOWING_KEY_HAS_GENERATED_ERRORS")+" ["+logKeys.toString()+"]" );

                                            }
                                        }
                                        finally
                                        {
                                            if( everyOneCommit )
                                            {
                                                if( commit )
                                                {
                                                    boTextIndexAgentBussinessLogic.addToQueue( ctx.getObjectsInTransaction() );
                                                    ctx.endTransaction();
                                                    ctx.commitContainerTransaction();
                                                }
                                                else
                                                {
                                                    ctx.rollbackContainerTransaction();
                                                }
                                            }


                                        }
                                    }
                                }
                                catch (InstantiationException e)
                                {
                                    throw new boRuntimeException2( e );
                                }
                                catch (IllegalAccessException e)
                                {
                                    throw new boRuntimeException2( e );
                                }
                                catch (ClassNotFoundException e)
                                {
                                    throw new boRuntimeException2( e );
                                }
                            }
                            else
                            {

                                boolean commit = false;
                                try
                                {
                                    if( everyOneCommit )
                                    {
                                        ctx.beginContainerTransaction();
                                    }

                                    PreparedStatement pstm = ctx.getConnectionData()
                                                                .prepareStatement("insert into " + ebo_registryFullTableName +

                                            "   (sys_user, sys_icn, sys_dtcreate, sys_dtsave, ui$, ui_version, name, clsid, clsid_major_version, clsid_minor_version, boui, classname) " +
                                            "   values " +
                                            "  ( ? , 1 , " + sSysDate +  " ,  " + sSysDate +  ", ?, ?, ?, ?, ? , ?, ?, ?) ");

                                    for (int g = 0; g < dtattoupd.getRowCount(); g++)
                                    {
                                        BigDecimal nr = dtattoupd.rows( g + 1 ).getBigDecimal("BOUI");
                                        pstm.setString(1, ctx.getSysUser().getUserName());
                                        pstm.setObject(2, nr);
                                        pstm.setLong(3, 1);
                                        pstm.setString(4, classdef.getName());
                                        pstm.setString(5, classdef.getName());
                                        pstm.setString(6, classdef.getBoMajorVersion());
                                        pstm.setString(7, classdef.getBoMinorVersion());
                                        pstm.setObject(8, nr);
                                        pstm.setString(9, "Ebo_Registry");
                                        pstm.addBatch();
                                    }
                                    pstm.executeBatch();
                                    pstm.close();
                                    try
                                    {
                                        DataManager.updateDataSet(ctx, dtattoupd, false,true);
                                        commit = true;
                                    }
                                    catch (Exception e)
                                    {
                                        logger.warn(LoggerMessageLocalizer.getMessage("ERROR_IMPORTING")+" ["+ objdef.getName() +"]. "+LoggerMessageLocalizer.getMessage("THE_FOLOWING_KEY_HAS_GENERATED_ERRORS"));
                                    }
                                }
                                finally
                                {
                                    if( everyOneCommit )
                                    {
                                        if( commit )
                                        {
                                            ctx.commitContainerTransaction();;
                                        }
                                        else
                                        {
                                            ctx.commitContainerTransaction();
                                        }
                                    }
                                }
                            }
                        }
                        if( everyOneCommit )
                        {
                            ctx.commitContainerTransaction();
                        }

                        if (!nextset)
                        {
                            break;
                        }
                    }

                    // TextIndex passou a ser transactional
                    //boTextIndexAgent.addToQueue(ctx,toRegister, objectName);
                    ok = true;
                }
                finally
                {
                    if (!ok)
                    {
//                        rollBackTransaction(ctx);
                    }
                }
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException("boManagerBean.registerObjects(...)",
                "BO-3056", e);
        }
    }

     private final boolean fireMappingEvents( boObject object )  throws boRuntimeException
    {
        boolean isOnMappingEvent = object.getParameter("IsInDetectChangesByTrigger") != null
                                    && object.getParameter("IsInDetectChangesByTrigger").equals( "true" );
        boDefHandler objdef = object.getBoDefinition();
        boolean ret = true;
        if( !netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).isDefault() && !isOnMappingEvent )
        {
            String className = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).getObjectDataSources().getClassName();
            EboContext ctx = object.getEboContext();
            if( className != null && className.length() > 0 )
            {
                boolean havelocaltable = false;
                String[] keys = MapType1Def.getDataSourceDefinition( objdef ).getObjectDataSources().getDataSources()[0].getKeys();
                Object values[] = new Object[ keys.length ];
                for (int k = 0; k < keys.length; k++)
                {
                    if( havelocaltable )
                    {
                        values[k] = object.getDataSet().rows( 1 ).getObject( keys[k]+"$R" );
                    }
                    else
                    {
                        values[k] = object.getDataSet().rows( 1 ).getObject( keys[k]+"$L" );
                    }
                }
                KeyReference keysref = new KeyReference( keys, values );
                 ObjectMap map = (ObjectMap)boCompilerClassLoader.getInstanceFromClassName( object.getEboContext(), className, null, ObjectMap.class );
                 if ( object.getMode() == boObject.MODE_NEW )
                 {
                    ret = map.onInsertObject( ctx, object, keysref );
                 }
                 else if ( object.getMode() == boObject.MODE_DESTROY )
                 {
                    ret = map.onDeleteObject(  ctx, object, keysref );
                 }
                 else
                 {
                    ret = map.onUpdateObject( ctx, object, keysref );
                 }

                 //update
                 for (int k = 0; k < keys.length; k++)
                 {
                    if( havelocaltable )
                    {
                        object.getDataSet().rows(1).updateObject(keys[k]+"$R", keysref.getKeyValue(keys[k]));
                    }
                    else
                    {
                        object.getDataSet().rows( 1 ).updateObject(keys[k]+"$L", keysref.getKeyValue(keys[k]));
                    }
                }

            }
        }
        return ret;
    }

    public final DataSet execute(EboContext ctx, boDefHandler objdef,
        String sqlquery, int page, int pageSize, List arguments,
        boolean isboql) throws boRuntimeException
    {
        boolean allNative = true;
        String schemaAux = ctx.getBoSession().getRepository().getSchemaName();
        Hashtable relations = new Hashtable();

        String xobjname = objdef.getName();

        MapType1Def ovjds = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef );

        boDefAttribute[] atts = objdef.getBoAttributes();
        int i = 0;

        for (; i < atts.length; i++)
        {
            if (atts[i].getDbIsTabled() ||
                    ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (atts[i].getMaxOccurs() > 1)))
            {
                if (atts[i].getDbIsTabled())
                {
                    StringBuffer nodeQuery = new StringBuffer("SELECT * FROM ");
                    //.append( atts[i].getDbTableName() )
                    nodeQuery.append(atts[i].getTableName())
                             .append(" WHERE PARENT.").append("BOUI").append('=')
                             .append(atts[i].getDbTableFatherFieldName());

                    DataSetRelations dsr = new DataSetRelations(atts[i].getName());
                    dsr.addReadRelation(new DataSetReadRelation(
                            atts[i].getName(), "DATA", "",
                            nodeQuery.toString(), new String[] { "BOUI" },
                            new String[] { "T$PARENT$" }));
                    relations.put(dsr.getName(), dsr);
                }
                else if ( atts[i].getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                {
                    StringBuffer nodeQuery = new StringBuffer("SELECT * FROM ");
                    boDefBridge bridge = atts[i].getBridge();
                    nodeQuery.append(bridge.getBoMasterTable())
                             .append(" WHERE PARENT.").append("BOUI").append('=')
                             .append(bridge.getFatherFieldName()).append(" ORDER BY LIN ");

                    DataSetRelations dsr = new DataSetRelations(atts[i].getName());
                    dsr.addReadRelation(new DataSetReadRelation(
                            bridge.getName(), "DATA", "", nodeQuery.toString(),
                            new String[] { "BOUI" }, new String[] { "PARENT$" }));
                    relations.put(dsr.getName(), dsr);
                }
                else
                {
                    boDefBridge  bdef   = atts[i].getBridge();
                    boDefHandler refObj = atts[i].getReferencedObjectDef();
                    if( refObj != null )
                    {
                        StringBuffer nodeQuery = null;
                        if(objdef.getBoMarkInputType())
                        {
                            nodeQuery = new StringBuffer("SELECT PARENT$, BOUI, LIN, SYS_FLDINPUTTYPE FROM ");
                        }
                        else
                        {
                            nodeQuery = new StringBuffer("SELECT PARENT$, BOUI, LIN FROM ");
                        }
                        nodeQuery.append( refObj.getBoMasterTable()  )
                                 .append(" WHERE PARENT.").append( "BOUI" ).append('=')
                                 .append( "PARENT$" ).append(" ORDER BY LIN ");

                        DataSetRelations dsr = new DataSetRelations(atts[i].getName());
                        dsr.addReadRelation(new DataSetReadRelation(
                                bdef.getName(), "DATA", "", nodeQuery.toString(),
                                new String[] { "BOUI" }, new String[] { "PARENT$" }));
                        relations.put(dsr.getName(), dsr);
                    }
                }
            }
        }

        //        finalsql.append( " from " ).append(' ').append( sqlquery );
        DataSetRelations dsr = new DataSetRelations("root");

        MapType1Def.ObjectDS[] ds = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef ).getReaderDataSource()
                                            .getDataSources();

        String[] sds = new String[ds.length];
        String[] qrys = new String[ds.length];
        List[] args = new ArrayList[ds.length];

        for (i = 0; i < ds.length; i++)
        {
            String[] dsatts = ds[i].getRemoteAttributes();
            StringBuffer finalsql = new StringBuffer("SELECT ");

            for (int fnr = 0; fnr < dsatts.length; fnr++)
            {
                if (fnr > 0)
                {
                    finalsql.append(',').append(' ');
                }

                finalsql.append(ds[i].getSourceObject()).append('.').append('"')
                        .append(dsatts[fnr]).append('"');
            }

            if (isboql)
            {
                finalsql.append(" from ").append(' ').append(sqlquery);
            }
            else
            {
                finalsql.append(" from ").append(objdef.getBoMasterTable())
                        .append(" WHERE ").append(sqlquery);
            }

            sds[i] = ds[i].getDataSource();
            qrys[i] = finalsql.toString();
            args[i] = arguments;
            dsr.addReadRelation(new DataSetReadRelation(ds[i].getName(),
                    ds[i].getDataSource(), ds[i].getSchema(),
                    finalsql.toString(), null, null));
        }

        MapType1Def.ObjectDataSource allds = netgest.bo.plugins.data.MapType1Def.getDataSourceDefinition( objdef )
                                                     .getWriterDataSource();
        ds = allds.getDataSources();

        DataSet dataSet = DataManager.executeNativeQuery(ctx, sds, qrys, page,
                pageSize, args);

        dataSet.setChildRelations( relations );
        dataSet.setRelation( dsr );

        DataSetRelations dr = dataSet.getRelation();

        if (dr.getWriteRelations() == null)
        {
            for (i = 0; i < ds.length; i++)
            {
                if (!ds[i].isReadOnly())
                {

                    dr.addWriteRelation(new DataSetWriteRelation(
                            ds[i].getName(), ds[i].getDataSource(),
                            ds[i].getXMLObjName(), ds[i].getSourceObject(),
                            schemaAux, ds[i].getICNFields(), ds[i].getKeys(),
                            ds[i].getKeys(), ds[i].getLocalAttributes(),
                            ds[i].getRemoteAttributes()));
                }
            }
        }

        Enumeration oEnum = dataSet.getChildRelations().keys();

        while (oEnum.hasMoreElements())
        {
            String key = (String) oEnum.nextElement();
            DataSetRelations crel = (DataSetRelations) dataSet.getChildRelations().get(key);

            if (crel.getWriteRelations() == null)
            {
                boDefAttribute att = objdef.getAttributeRef(key);

                if (att.getDbIsTabled() ||
                        ((att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        (att.getMaxOccurs() > 1)))
                {
                    if (att.getDbIsTabled())
                    {
                        crel.addWriteRelation(new DataSetWriteRelation(
                                att.getName(), "DATA", objdef.getName(),
                                att.getDbTableName(), "", null,
                                new String[] { "T$PARENT$", att.getDbName() },
                                new String[] { "T$PARENT$", att.getDbName() },
                                null, null));
                    }
                    else if ( att.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE )
                    {
                        crel.addWriteRelation(new DataSetWriteRelation(
                                att.getName(), "DATA", objdef.getName(),
                                att.getBridge().getBoPhisicalMasterTable(), "",
                                null, new String[] { "PARENT$", "CHILD$" },
                                new String[] { "PARENT$", "CHILD$" }, null, null));
                    }
                }
            }
        }

        return dataSet;
    }


    private void registerObjects(EboContext ctx, DataSet data,
        boDefHandler objdef) throws boRuntimeException
    {
        registerObjects(ctx, data, objdef.getName());
    }

    public boolean updateObjectData(
                                    boObject object
                                    ) throws boRuntimeException
    {
        return updateObjectData( object, true );
    }


    private boolean updateObjectData(boObject object, boolean retryConcurrency) throws boRuntimeException
    {

        boolean allNative = true;
        boDefHandler objdef = object.getBoDefinition();

        String xobjname = objdef.getName();

        MapType1Def ovjds =  MapType1Def.getDataSourceDefinition( objdef );

        boDefAttribute[] atts = objdef.getBoAttributes();
        int i = 0;

        //        finalsql.append( " from " ).append(' ').append( sqlquery );
        DataSetRelations dsr = new DataSetRelations("root");

        ObjectDS[] ds = ovjds.getReaderDataSource().getDataSources();

        ObjectDataSource allds = ovjds.getWriterDataSource();
        ds = allds.getDataSources();

        DataSet dataSet = object.getDataSet();
        DataSetRelations dr = dataSet.getDataSetRelations();
        dr.clearWriteRelations();

//        if (dr.getWriteRelations())
//        {
            for (i = 0; i < ds.length; i++)
            {
                if (!ds[i].isReadOnly())
                {
                    dr.addWriteRelation(new DataSetWriteRelation(
                            ds[i].getName(), ds[i].getDataSource(),
                            ds[i].getXMLObjName(), ds[i].getSourceObject(),
                            null, ds[i].getICNFields(), ds[i].getKeys(),
                            ds[i].getKeys(), ds[i].getLocalAttributes(),
                            ds[i].getRemoteAttributes()));
                }
            }
//        }
        return DefaultDataManager.updateObjectData( object );
    }
    private static final String writeToSchema(boObject object)
        throws boRuntimeException
    {
        Connection con = null;
        ResultSet rs = null;
        PreparedStatement ps = null;
        String value = null;

        try
        {
            con = object.getEboContext().getConnectionData();
            ps = con.prepareStatement("select SYS_ORIGIN from " +
                    object.getBoDefinition().getBoMasterTable() +
                    " where boui = ?");
            ps.setLong(1, object.getBoui());
            rs = ps.executeQuery();

            if (rs.next())
            {
                value = rs.getString(1);
            }
        }
        catch (SQLException e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if (ps != null)
                {
                    ps.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }

            try
            {
                if (rs != null)
                {
                    rs.close();
                }
            }
            catch (Exception e)
            {
                //ignore
            }
        }

        if ((value == null) || (value.length() == 0))
        {
            return object.getEboContext().getBoSession().getRepository()
                         .getName();
        }

        return value;
    }

    public void beforeObjectLoad(boObject object) throws boRuntimeException
    {
    }

    public void afterObjectLoad(boObject object) throws boRuntimeException
    {
    }

 }