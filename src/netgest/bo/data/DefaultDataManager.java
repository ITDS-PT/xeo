/*Enconding=UTF-8*/
package netgest.bo.data;
import java.math.BigDecimal;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import netgest.bo.data.DataSetRelations;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefBridge;
import netgest.bo.def.boDefHandler;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boRepository;
import netgest.bo.system.Logger;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.plugins.IDataManager;

public class DefaultDataManager
{

    private static Logger logger = Logger.getLogger( DefaultDataManager.class.getName() );

    public static final DataSet execute(EboContext ctx, boDefHandler objdef,
        String sqlquery, int page, int pageSize, List arguments,
        boolean isboql)
    {

        boolean allNative = true;
        String schemaAux = ctx.getBoSession().getRepository().getSchemaName();

        String xobjname = objdef.getName();

        
        Hashtable relations;
        DataSetRelations rootDsr; 
        
        DataSet dataSet;
        
        DataSet cachedEmptyDataSet = (DataSet)ctx.getApplication().getMemoryArchive().getCachedDataSetRelation( objdef.getName() );
        
        if( cachedEmptyDataSet == null ) {
        	
        boDefAttribute[] atts = objdef.getBoAttributes();
        int i = 0;

            relations = new Hashtable();
        	
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
                    relations.put(dsr.p_name, dsr);
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
                    relations.put(dsr.p_name, dsr);
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
                        relations.put(dsr.p_name, dsr);
                    }
                }
            }
	            
	            
        }

        //        finalsql.append( " from " ).append(' ').append( sqlquery );
	        rootDsr = new DataSetRelations("root");

        String[] sds = new String[ 1 ];
        String[] qrys = new String[ 1 ];
        List[] args = new ArrayList[ 1 ];

            sds[ 0 ] = "DATA";
            qrys[ 0 ] = "SELECT * FROM " + objdef.getBoMasterTable() + " WHERE " + sqlquery;
            args[ 0 ] = arguments;

	        rootDsr.addReadRelation( new DataSetReadRelation
                                    (
                                        "Default",
                                        sds[ 0 ],
                                        null,
                                        qrys[ 0 ],
                                        null,
                                        null
                                    )
                                );


	        dataSet = DataManager.executeNativeQuery(ctx, sds, qrys, page,
                pageSize, args);

        dataSet.p_childRelations = relations;
	        dataSet.p_relation = rootDsr;


        dataSet.p_relation.addWriteRelation(
            new DataSetWriteRelation(
                    "DefaultWriter",
                    sds[0],
                    objdef.getName(),
                    objdef.getBoPhisicalMasterTable(),
                    schemaAux,
                    new String[] { "SYS_ICN" },
                    new String[] { "BOUI" },
                    new String[] { "BOUI" },
                    dataSet.getMetaData().getColumnNames(),
                    dataSet.getMetaData().getColumnNames()
                )
            );

        Enumeration oEnum = dataSet.p_childRelations.keys();

        while (oEnum.hasMoreElements())
        {
            String key = (String) oEnum.nextElement();
            DataSetRelations crel = (DataSetRelations) dataSet.p_childRelations.get(key);

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

            DataSet toCache = new DataSet( dataSet.getMetaData() );
            toCache.p_relation = dataSet.p_relation;
            toCache.p_childRelations = dataSet.p_childRelations;
	        ctx.getApplication().getMemoryArchive().putCachedDataSetRelation( objdef.getName(), toCache );
        }
        else {
	        String[] sds = new String[ 1 ];
	        String[] qrys = new String[ 1 ];
	        List[] args = new ArrayList[ 1 ];
	
	        sds[ 0 ] = "DATA";
	        qrys[ 0 ] = "SELECT * FROM " + objdef.getBoMasterTable() + " WHERE " + sqlquery;
	        args[ 0 ] = arguments;
	        dataSet = DataManager.executeNativeQuery(ctx, cloneDataSet(cachedEmptyDataSet),sds, qrys, page,
	                pageSize, args );

        }
        return dataSet;
    }
    
    private static final DataSet cloneDataSet( DataSet dataSet ) {
    	try {
			return (DataSet)dataSet.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			throw new RuntimeException( e );
		}
    }

    public static boolean updateObjectData(boObject object)
        throws boRuntimeException
    {
        return DefaultDataManager.updateObjectData( object, true );
    }


    private static boolean updateObjectData(boObject object,
        boolean retryConcurrency) throws boRuntimeException
    {
        boolean isForDestroy = (object.getMode() == object.MODE_DESTROY);

        int rowsUpdated = 0;
        String schemaAux;
        boDefHandler objdef = object.getBoDefinition();

        try
        {
//            if ((objdef.getASPMode() == boDefHandler.ASP_GLOBAL) ||
//                    (objdef.getASPMode() == boDefHandler.ASP_CONTROLLER))
//            {
                schemaAux = boRepository.getRepository(object.getEboContext()
                                                             .getApplication(),
                        "default").getSchemaName();
//            }
//            else
//            {
//                if (objdef.getASPMode() == boDefHandler.ASP_PRIVATE)
//                {
//                    schemaAux = object.getEboContext().getBoSession()
//                                      .getRepository().getSchemaName();
//                }
//                else
//                {
//                    schemaAux = boRepository.getRepository(object.getEboContext()
//                                                                 .getApplication(),
//                            writeToSchema(object)).getSchemaName();
//                }
//            }

            String auxObjName;

            for (int i = 0;
                    i < object.getDataSet().p_relation.getWriteRelations().length;
                    i++)
            {
                auxObjName = object.getDataSet().p_relation.getWriteRelations()[i].getXMLObjectName();

//                if (boDefHandler.getBoDefinition(auxObjName).getBoDataSources()
//                                    .isDefault())
//                {
                    object.getDataSet().p_relation.getWriteRelations()[i].setSchemaName(schemaAux);

                    if (object.getDataSet().p_childRelations != null)
                    {
                        Enumeration oEnum = object.getDataSet().p_childRelations.elements();

                        while (oEnum.hasMoreElements())
                        {
                            DataSetWriteRelation[] rels =  ((DataSetRelations) oEnum.nextElement()).getWriteRelations();
                            for (int z = 0; rels != null &&
                                    z < rels.length;
                                    z++)
                            {
                                rels[z].setSchemaName(schemaAux);
                            }
                        }
                    }
//                }
            }

            rowsUpdated = DataManager.updateDataSet(object.getEboContext(),
                    object.getDataSet(), isForDestroy);
        }
        catch (WriterException e)
        {
            if (e.getType() == WriterException.CONCURRENCY_FAILED)
            {
                logger.finest(
                   LoggerMessageLocalizer.getMessage("CONCURRENCY_IN_DATA_DETECTED_TRYING_TO_MERGE_CHANGES"));

                if (retryConcurrency && mergeObjectData(object))
                {
                    updateObjectData(object, false);
                }
                else
                {
                    throw new boRuntimeException(object,
                        "ObjectDataManager.updateObjectData(boObject)",
                        "BO-3022", e);
                }
            }
            else if (e.getType() == WriterException.INCONSISTENT_DATA)
            {
                throw new boRuntimeException(object,
                    "ObjectDataManager.updateObjectData(boObject)", "BO-3022", e);
            }
            else if (e.getType() == WriterException.REFERENCED_CONTRAINTS)
            {
                throw new boRuntimeException(object,
                    "ObjectDataManager.updateObjectData(boObject)", "BO-3023", e);
            }
            else if (e.getType() == WriterException.UNIQUE_KEY_VIOLATED)
            {
                throw new boRuntimeException(object,
                    "ObjectDataManager.updateObjectData(boObject)", "BO-3054", e);
            }
            else
            {
                e.printStackTrace();
                throw new boRuntimeException(object,
                    "ObjectDataManager.updateObjectData(boObject)", "BO-3055", e);
            }
        }

        return rowsUpdated > 0;
    }

    public static final boolean mergeObjectData(boObject object)
        throws boRuntimeException
    {
        ArrayList args = new ArrayList();
        args.add(BigDecimal.valueOf(object.getBoui()));

        DataSet dbData = ObjectDataManager.executeBOQL(object.getEboContext(),
                "SELECT " + object.getName() + " where boui=?", args, true);
        DataSet objData = object.getDataSet();
        boolean ok = mergeRowData(objData.rows(1), dbData.rows(1));

        if (ok)
        {
            boDefAttribute[] atts = object.getBoDefinition().getBoAttributes();

            for (int i = 0; i < atts.length; i++)
            {
                if (atts[i].getDbIsTabled() ||
                        ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                        (atts[i].getMaxOccurs() > 1)))
                {
                    DataSet subDbData = dbData.rows(1).getChildDataSet(object.getEboContext(),
                            atts[i].getName());
                    DataSet subobjData = objData.rows(1).getChildDataSet(object.getEboContext(),
                            atts[i].getName());

                    ok = mergeNode(subobjData, subDbData);

                    if (atts[i].getMaxOccurs() > 1)
                    {
                        bridgeHandler bridge = object.getBridge(atts[i].getName());

                        if (bridge != null)
                        {
                            bridge.refreshBridgeData();
                        }
                    }
                }

                if (!ok)
                {
                    break;
                }
            }
        }

        return ok;
    }


    private static final boolean mergeRowData(DataRow objData, DataRow dbData)
    {
        boolean ok = true;
        DataRow fbrow = objData.getFlashBackRow();

        objData.isnew = false;

        int columns = dbData.getDataSet().getMetaData().getColumnCount();

        for (int i = 1; i <= columns; i++)
        {
            String columnName = dbData.getDataSet().getMetaData().getColumnName(i);
            boolean isinternal = columnName.equals("SYS_ICN") ||
                columnName.equals("SYS_USER") ||
                columnName.equals("SYS_DTCREATE") ||
                columnName.equals("SYS_DTSAVE");

            if (!isinternal)
            {
                if ((fbrow != null) &&
                        !compareObject(objData.getObject(i), fbrow.getObject(i)))
                {
                    if (!compareObject(dbData.getObject(i), objData.getObject(i)))
                    {
                        if (!compareObject(fbrow.getObject(i),
                                    dbData.getObject(i)))
                        {
                            ok = false;

                            break;
                        }
                    }
                }
                else
                {
                    objData.updateObject(i, dbData.getObject(i));
                }
            }
            else
            {
                if (columnName.equals("SYS_ICN"))
                {
                    objData.updateObject(i, dbData.getObject(i));
                }
            }
        }

        return ok;
    }

    private static final boolean compareObject(Object left, Object right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) && (right != null))
        {
            ret = false;
        }
        else if ((left != null) && (right == null))
        {
            ret = false;
        }
        else
        {
            ret = left.equals(right);
        }

        return ret;
    }

    private static final boolean mergeNode(DataSet objData, DataSet dbData)
    {
        boolean ret = true;

        if (objData.wasChanged())
        {
            ret = (objData.getRowCount() >= dbData.getRowCount()) &&
                (objData.getDeletedRowsCount() == 0);

            if (ret)
            {
                for (int i = 0; ret && (i < dbData.getRowCount()); i++)
                {
                    ret = mergeRowData(objData.rows(i + 1), dbData.rows(i + 1));
                }
            }
        }
        else
        {
            int i;

            for (i = 1; i <= dbData.getRowCount(); i++)
            {
                DataRow objrow = null;

                if (i < objData.getRowCount())
                {
                    objrow = objData.rows(i);
                }
                else
                {
                    objrow = objData.createRow();
                }

                objrow.rowdata = dbData.rows(i).rowdata;
            }

            while (objData.getRowCount() > i)
            {
                objData.deleteRow(i);
            }

            objData.reset();
        }

        return ret;
    }


}