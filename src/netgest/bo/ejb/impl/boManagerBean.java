/*Enconding=UTF-8*/
package netgest.bo.ejb.impl;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.EJBLocalObject;
import javax.ejb.RemoveException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.UserTransaction;

import netgest.bo.boException;
import netgest.bo.builder.boBuilder;
import netgest.bo.builder.boBuilderOptions;
import netgest.bo.builder.boBuilderProgress;
import netgest.bo.data.DataManager;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.data.DataSetMetaData;
import netgest.bo.data.IXEODataManager;
import netgest.bo.data.KeyReference;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.data.XEODataManagerKey;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.plugins.DataPluginManager;
import netgest.bo.plugins.IDataPlugin;
import netgest.bo.plugins.data.MapType1Def;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.CloneListener;
import netgest.bo.runtime.DefaultCloneListener;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boBridgeMasterAttribute;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boReferencesManager;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boRuntimeException2;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.cacheBouis;
import netgest.bo.runtime.robots.ObjectMap;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;
import netgest.bo.security.securityOPL;
import netgest.bo.system.Logger;
import netgest.bo.system.boCompilerClassLoader;
import netgest.bo.system.boLoginLocalHome;
import netgest.bo.system.boPoolable;
import netgest.bo.system.boRepository;
import netgest.bo.utils.boVersioning;
import netgest.io.iFile;
import netgest.io.iFileException;
import netgest.io.iFileTransactionManager;
import netgest.utils.DataUtils;

public class boManagerBean implements SessionBean, boManagerLocal
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	//logger
    private static Logger logger = Logger.getLogger("netgest.bo.ejb.impl.boManagerBean");

    private static final byte DEBUG = 0;

    public void ejbCreate()
    {
        my_trans = false;
    }

    public void ejbActivate()
    {
        my_trans = false;
    }

    public void ejbPassivate()
    {
        my_trans = false;
    }

    public void ejbRemove()
    {
        my_trans = false;
    }

    public void setSessionContext(SessionContext ctx)
    {
        my_trans = false;
    }

    public boObject createObject(EboContext ctx, long classboui)
        throws boRuntimeException
    {
        boObject object = this.loadObject(ctx, "Ebo_ClsReg", classboui);

        if (object.exists())
        {
            String classname = object.getAttribute("name").getValueString();

            return this.createObject(ctx, classname);
        }
        else
        {
            throw new boRuntimeException(boManagerBean.class.getName() +
                ".createObject(EboContext,long)", "BO-3020", null,
                "" + classboui);
        }
    }

    private String attributeName(boObject obj, String dbName)
    {
        boAttributesArray boArr = obj.getAttributes();
        Enumeration oEnum = boArr.elements();
        AttributeHandler attH;
        String aux;

        while (oEnum.hasMoreElements())
        {
            attH = (AttributeHandler) oEnum.nextElement();
            aux = attH.getDefAttribute().getDbName();

            if (dbName.equals(aux))
            {
                return attH.getName();
            }
        }

        return null;
    }

    private String bridgeAttributeName(bridgeHandler bridge, String dbName)
    {
        boAttributesArray boArr = bridge.getAllAttributes();
        Enumeration oEnum = boArr.elements();
        AttributeHandler attH;
        String aux;

        while (oEnum.hasMoreElements())
        {
            attH = (AttributeHandler) oEnum.nextElement();
            aux = attH.getDefAttribute().getDbName();

            if (dbName.equals(aux))
            {
                return (attH).getDefAttribute().getName();
            }
        }

        return null;
    }

    private ArrayList getAttributesObject(DataSetMetaData dmd, boObject obj)
        throws boRuntimeException
    {
        ArrayList nameArray = new ArrayList();
        String columnName;
        boDefHandler auxDef;

        for (int i = 1; i <= dmd.getColumnCount(); i++)
        {
            columnName = attributeName(obj, dmd.getColumnName(i));
            AttributeHandler attribute = obj.getAttribute(columnName);
            Object attributeValue = attribute.getValueObject();
            if ( (columnName != null) &&
                    attribute.isObject() &&
                    (attributeValue != null) && 
                    (Long.valueOf( attributeValue.toString() ) > 0) )
            {
//                auxDef = obj.getAttribute(columnName).getDefAttribute()
//                            .getReferencedObjectDef();
//                String className = auxDef.getName();
                long boui = ((BigDecimal)obj.getAttribute(columnName).getValueObject()).longValue();
                String className = getClassNameFromBOUI(obj.getEboContext(),
                                        boui);
                auxDef = boDefHandler.getBoDefinition(className);
                if ( !columnName.equalsIgnoreCase("PARENT")  )
                {
                    if (!obj.getAttribute(columnName).getDefAttribute().getChildIsOrphan() &&
                            !auxDef.getBoHaveMultiParent() )
                    {
                        nameArray.add(columnName);
                    }
                }



            }
        }

        return nameArray;
    }
/*
    private ArrayList getSimpleAttributes(DataSetMetaData dmd, boObject obj)
        throws boRuntimeException
    {
        ArrayList nameArray = new ArrayList();
        String columnName;
        boDefHandler auxDef;

        for (int i = 1; i <= dmd.getColumnCount(); i++)
        {
            columnName = attributeName(obj, dmd.getColumnName(i));

            if (!((columnName != null) &&
                    obj.getAttribute(columnName).isObject() &&
                    (obj.getAttribute(columnName).getValueObject() != null)))
            {
                nameArray.add(columnName);
            }
            else
            {
                auxDef = obj.getAttribute(columnName).getDefAttribute()
                            .getReferencedObjectDef();
                if (auxDef.getBoCanBeOrphan() || auxDef.getBoHaveMultiParent())
                {
                    nameArray.add(columnName);
                }
            }
        }

        return nameArray;
    }
*/
    private ArrayList getAttributesBridge(DataSetMetaData dmd,
        bridgeHandler bridge) throws boRuntimeException
    {
        ArrayList nameArray = new ArrayList();
        String columnName;
        boDefHandler auxDef;

        for (int i = 1; i <= dmd.getColumnCount(); i++)
        {
            if (!"LIN".equals(dmd.getColumnName(i)))
            {
                columnName = bridgeAttributeName(bridge, dmd.getColumnName(i));

                if ((columnName != null) &&
                        (bridge.getAttribute(columnName) != null) &&
                        bridge.getAttribute(columnName).isObject() &&
                        (bridge.getAttribute(columnName).getValueObject() != null))
                {
//                    auxDef = bridge.getAttribute(columnName).getDefAttribute()
//                                   .getReferencedObjectDef();
                    long boui = ((BigDecimal)bridge.getAttribute(columnName).getValueObject()).longValue();
                    String className = getClassNameFromBOUI(bridge.getEboContext(),
                                        boui);
                    auxDef = boDefHandler.getBoDefinition(className);
                    if ( !columnName.equalsIgnoreCase("PARENT")  )
                    {
                        if (!auxDef.getBoCanBeOrphan() &&
                                !auxDef.getBoHaveMultiParent())
                        {
                            nameArray.add(columnName);
                        }
                    }
                }
            }
        }

        return nameArray;
    }
/*
    private ArrayList getSimpleAttributesBridge(DataSetMetaData dmd,
        bridgeHandler bridge) throws boRuntimeException
    {
        ArrayList nameArray = new ArrayList();
        String columnName;
        boDefHandler auxDef;

        for (int i = 1; i <= dmd.getColumnCount(); i++)
        {
            if (!"LIN".equals(dmd.getColumnName(i)))
            {
                columnName = bridgeAttributeName(bridge, dmd.getColumnName(i));

                if (!((columnName != null) &&
                        (bridge.getAttribute(columnName) != null) &&
                        bridge.getAttribute(columnName).isObject() &&
                        (bridge.getAttribute(columnName).getValueObject() != null)))
                {
                    nameArray.add(columnName);
                }
                else
                {
                    auxDef = bridge.getAttribute(columnName).getDefAttribute()
                                   .getReferencedObjectDef();
                    if (auxDef.getBoCanBeOrphan() ||
                            auxDef.getBoHaveMultiParent())
                    {
                        nameArray.add(columnName);
                    }
                }
            }
        }

        return nameArray;
    }
*/
    public boObject createObject(EboContext ctx, boObject objectFrom, String classObjectTo)
        throws boRuntimeException
    {
        return createObject(ctx, objectFrom, classObjectTo, new DefaultCloneListener());
    }

    public boObject createObject(EboContext ctx, boObject objectFrom, String classObjectTo, CloneListener listener)
        throws boRuntimeException
    {

            AttributeHandler auxAtt = null, bridgeAuxAtt = null;
            boDefHandler auxDef = null;
            boObject auxObj = null;
            bridgeHandler auxBh = null, newObjAuxBh = null;
            boBridgeIterator it = null;
            boAttributesArray bridgeAtts = null;
            Enumeration bridgeEnum = null;

            ArrayList objectsToSave = new ArrayList();
            boAttributesArray atts = objectFrom.getAttributes();
            Enumeration oEnum = atts.elements();


            boObject newObject = createObject(ctx, classObjectTo);
            while (oEnum.hasMoreElements())
            {
                auxAtt = (AttributeHandler)oEnum.nextElement();
                if(newObject.getAttribute(auxAtt.getName()) != null)
                {
                    if(newObject.getAttribute(auxAtt.getName()).getDefAttribute().getType().equals(auxAtt.getDefAttribute().getType()))
                    {
                        //existe e são do mesmo tipo
                        if(auxAtt.isBridge())
                        {
                            //bridge
                            newObjAuxBh = newObject.getBridge(auxAtt.getName());
                            auxBh = objectFrom.getBridge(auxAtt.getName());
                            it = auxBh.iterator();
                            it.beforeFirst();
                            while(it.next())
                            {
                                //vou tratar do objecto
                                auxObj = it.currentRow().getObject();
                                auxDef = auxObj.getBoDefinition();
                                Object value = listener.getBridgeValue(objectFrom, auxBh, auxBh.getRow(), auxObj);
                                if(value != null)
                                {
                                    auxObj = getBoObject(ctx, value);
                                    if (!auxDef.getBoCanBeOrphan() &&
                                    !auxDef.getBoHaveMultiParent())
                                    {
                                        auxObj = createObject(ctx, auxObj, auxObj.getName(), listener);
                                        objectsToSave.add( new Long( auxObj.getBoui() ) );
                                    }
                                    newObjAuxBh.add(auxObj.getBoui());

                                    //vou tratar dos atributos
                                    bridgeAtts = it.currentRow().getLineAttributes();
                                    bridgeEnum = bridgeAtts.elements();
                                    while(bridgeEnum.hasMoreElements())
                                    {
                                        bridgeAuxAtt = (AttributeHandler)bridgeEnum.nextElement();
                                        if(newObjAuxBh.getAttribute(bridgeAuxAtt.getName()) != null &&
                                           newObjAuxBh.getAttribute(bridgeAuxAtt.getName()).getDefAttribute()
                                            .getType().equals(bridgeAuxAtt .getDefAttribute().getType())
                                        )
                                        {
                                            if(bridgeAuxAtt.isObject())
                                            {
                                                //objecto
                                                auxDef = bridgeAuxAtt.getDefAttribute().getReferencedObjectDef();
                                                value = listener.getValue(objectFrom, auxBh, auxBh.getRow(), bridgeAuxAtt);
                                                if(value != null)
                                                {
                                                    auxObj = getBoObject(ctx, value);
                                                    if (!auxDef.getBoCanBeOrphan() &&
                                                            !auxDef.getBoHaveMultiParent())
                                                    {
                                                        auxObj = createObject(ctx, auxObj, auxObj.getName(), listener);
                                                        objectsToSave.add( new Long( auxObj.getBoui() ) );
                                                    }
                                                    newObjAuxBh.getAttribute(bridgeAuxAtt.getName()).setValueObject(auxObj);
                                                    newObjAuxBh.getAttribute(bridgeAuxAtt.getName()).setInputType(bridgeAuxAtt.getInputType());
                                                }
                                            }
                                            else
                                            {
                                                newObject.getAttribute(auxAtt.getName()).setValueObject(listener.getValue(objectFrom, auxAtt));
                                                newObject.getAttribute(auxAtt.getName()).setInputType(auxAtt.getInputType());
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(auxAtt.isObject())
                            {
                                //objecto
                                auxDef = auxAtt.getDefAttribute().getReferencedObjectDef();
                                Object value = listener.getValue(objectFrom, objectFrom.getAttribute(auxAtt.getName()));
                                if(value != null)
                                {
                                    auxObj = getBoObject(ctx, value);
                                    if (!auxDef.getBoCanBeOrphan() &&
                                    !auxDef.getBoHaveMultiParent())
                                    {
                                        auxObj = createObject(ctx, auxObj, auxObj.getName(), listener);
                                        objectsToSave.add( new Long( auxObj.getBoui() ) );
                                    }
                                    newObject.getAttribute(auxAtt.getName()).setValueLong(auxObj.getBoui());
                                    newObject.getAttribute(auxAtt.getName()).setInputType(auxAtt.getInputType());
                                }
                                else
                                {
                                    newObject.getAttribute(auxAtt.getName()).setValueObject(null);
                                    newObject.getAttribute(auxAtt.getName()).setInputType(auxAtt.getInputType());
                                }
                            }
                            else
                            {
                                newObject.getAttribute(auxAtt.getName()).setValueObject(listener.getValue(objectFrom, auxAtt));
                                newObject.getAttribute(auxAtt.getName()).setInputType(auxAtt.getInputType());
                            }
                        }
                    }
                }
            }
            return newObject;
    }

    private boObject getBoObject(EboContext ctx, Object value) throws boRuntimeException
    {
        if(value instanceof BigDecimal)
        {
            return boObject.getBoManager().loadObject(ctx, ((BigDecimal)value).longValue());
        }
        else if(value instanceof Long)
        {
            return boObject.getBoManager().loadObject(ctx, ((Long)value).longValue());
        }
        else if(value instanceof boObject)
        {
            return (boObject)value;
        }
        else
        {
            throw new boRuntimeException("netgest.bo.ejb.impl.boManagerBean.createObject", MessageLocalizer.getMessage("INCORRECT_VALUE_RETURNED_BY_THE_CLONELISTENER"), null);
        }
    }

    public boObject createObject(EboContext ctx, String newObjName, boObject objectFrom)
        throws boRuntimeException
    {
        try
        {
            DataSet newData = getNewData(ctx, objectFrom);

            boObject newOb = createObject(ctx, newObjName, newData);
            newOb.setChanged( true );
            newOb = setParentBoui(newOb);
            //vou limpar os atributos "SYS_DTCREATE", "SYS_DTSAVE"
            if(newOb.getAttribute("SYS_DTCREATE") != null)
            {
               newOb.getAttribute("SYS_DTCREATE").setValueObject(null);
            }
            if(newOb.getAttribute("SYS_DTSAVE") != null)
            {
               newOb.getAttribute("SYS_DTSAVE").setValueObject(null);
            }
            if(newOb.getAttribute("CREATOR") != null)
            {
               newOb.getAttribute("CREATOR").setValueObject(null);
            }
            newOb.onAfterClone(new boEvent(newOb, "onAfterClone", objectFrom));
            return  newOb;
        }
        catch (boRuntimeException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new boRuntimeException(objectFrom,
                "boManager.cloneObjec", "BO-3025", e);
        }
    }
    private DataSet getNewData(EboContext ctx, boObject objectFrom)
        throws boRuntimeException
    {
        try
        {
            ArrayList objectsToSave = new ArrayList();
            DataSet newData = (DataSet) objectFrom.getDataSet().clone();
            String[] relations = newData.getChildrenNames(); //nome das bridges
            ArrayList relationObj = getAttributesObject(newData.getMetaData(),
                    objectFrom);
            boDefHandler auxDef;
            boObject auxObj = null;
            bridgeHandler bridge;
            int rowSave;
            String dbName;
            String className;

            //attributes
            for (int i = 0; i < relationObj.size(); i++)
            {
                dbName = objectFrom.getAttribute((String) relationObj.get(i))
                                   .getDefAttribute().getDbName();
                auxObj = objectFrom.getAttribute((String) relationObj.get(i))
                                   .getObject();
                auxObj = createObject(ctx, auxObj);
                objectsToSave.add( new Long( auxObj.getBoui() ) );
                newData.rows(1).updateLong(dbName, auxObj.getBoui());
            }

            //bridges
            for (int i = 0; i < relations.length; i++)
            {
                newData.rows(1).getChildDataSet(ctx, relations[i]);

                if ((newData.rows(1).getChildDataSet(ctx, relations[i])
                                .getRowCount() > 0) &&
                        objectFrom.getAttribute(relations[i]).isBridge())
                {
                    bridge = objectFrom.getBridge(relations[i]);

                    if (bridge != null)
                    {
                        rowSave = bridge.getRow();
                        bridge.beforeFirst();

                        DataSet newBData = newData.rows(1).getChildDataSet(ctx, relations[i]);

                        if (bridge.next())
                        {

                            bridge.beforeFirst();

                            while (bridge.next())
                            {
                                className = getClassNameFromBOUI(ctx,
                                        bridge.getValueLong());
                                auxDef = boDefHandler.getBoDefinition(className);

                                if (!auxDef.getBoCanBeOrphan() &&
                                        !auxDef.getBoHaveMultiParent())
                                {
                                    auxObj = createObject(ctx,
                                            bridge.getObject());
                                    newBData.rows(bridge.getRow()).updateLong("child$",
                                        auxObj.getBoui());
                                    objectsToSave.add( new Long( auxObj.getBoui() ) );

                                }
                                relationObj = getAttributesBridge(newBData.getMetaData(), bridge);
                                for (int j = 0; j < relationObj.size();
                                        j++)
                                {
                                    dbName = bridge.getAttribute((String) relationObj.get(
                                                j)).getDefAttribute()
                                                   .getDbName();
                                    auxObj = bridge.getAttribute((String) relationObj.get(
                                                j)).getObject();
                                    auxObj = createObject(ctx, auxObj);
                                    objectsToSave.add( new Long( auxObj.getBoui() ) );

                                    newBData.rows(bridge.getRow()).updateLong( dbName, auxObj.getBoui() );
                                }
                            }
                        }
                        bridge.moveTo(rowSave);
                    }
                }
            }
            return newData;
        }
        catch (CloneNotSupportedException e)
        {
            throw new boRuntimeException(objectFrom,
                "boManager.cloneObjec", "BO-3025", e);
        }
    }

    public boObject createObject(EboContext ctx, boObject objectFrom)
        throws boRuntimeException
    {
        return createObject(ctx, objectFrom.bo_name, objectFrom);
    }

    private boObject setParentBoui(boObject newOb) throws boRuntimeException
    {
        boAttributesArray boArr = newOb.getAttributes(), boArr2;
        Enumeration oEnum = boArr.elements(), enum2;
        AttributeHandler attH, attH2;
        bridgeHandler bridge;
        int rowSave;

        while (oEnum.hasMoreElements())
        {
            attH = (AttributeHandler) oEnum.nextElement();

            if (newOb.getAttribute(attH.getName()).isObject() &&
                    !newOb.getAttribute(attH.getName()).isBridge())
            {
                if ((newOb.getAttribute(attH.getName()).getObject() != null) &&
                        canAddAsParent(
                            (ObjAttHandler) newOb.getAttribute(attH.getName())))
                {
                    newOb.getAttribute(attH.getName()).getObject().addParent(newOb);
                }
            }
            else if (newOb.getAttribute(attH.getName()).isBridge())
            {
                bridge = newOb.getBridge(attH.getName());
                rowSave = bridge.getRow();
                bridge.beforeFirst();

                while (bridge.next())
                {
                    boArr2 = bridge.getAllAttributes();
                    enum2 = boArr2.elements();

                    while (enum2.hasMoreElements())
                    {
                        attH2 = (AttributeHandler) enum2.nextElement();

                        if ((bridge.getAttribute(attH2.getName()) != null) &&
                                bridge.getAttribute(attH2.getName()).isObject())
                        {
                            if ((bridge.getAttribute(attH2.getName()).getObject() != null) &&
                                    canAddAsParent(
                                        (ObjAttHandler) bridge.getAttribute(
                                            attH2.getName())))
                            {
                                bridge.getAttribute(attH2.getName()).getObject()
                                      .addParent(newOb);
                            }
                        }
                    }

                    if ((bridge.getObject() != null) &&
                            canAddAsParent(
                                (ObjAttHandler) bridge.getAttribute(
                                    bridge.getName())))
                    {
                        bridge.getObject().addParent(newOb);
                    }
                }

                bridge.moveTo(rowSave);
            }
        }

        return newOb;
    }

    private boolean canAddAsParent(ObjAttHandler attHandler)
    {
        if ((attHandler.getDefAttribute().getSetParent() != boDefAttribute.SET_PARENT_NO) &&
                !attHandler.getName().equals("PARENT"))
        {
            if (!attHandler.getDefAttribute().getChildIsOrphan() ||
                    (attHandler.getDefAttribute().getSetParent() == boDefAttribute.SET_PARENT_YES))
            {
                return true;
            }
        }

        return false;
    }

    public boObject createObjectWithParent(EboContext ctx, String name, long parentBoui )
        throws boRuntimeException
    {
        return createNewObject(ctx,name,parentBoui);
    }
    public boObject createObject(EboContext ctx, String name)
        throws boRuntimeException
    {
        return createNewObject(ctx,name,-1);
    }

    private boObject createNewObject(EboContext ctx, String name, long parentBoui)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        boObject ret = getObject(ctx, name);
        ret.setEboContext(ctx);
//        ret.create( createNewBoui( ctx ) );
        long boui =createNewBoui( ctx );
        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:BOUI[" + boui + "]:");
        ret.create( boui, false, null,parentBoui);
//        ret.poolSetSharable( true );
//        this._seedObject(ctx, ret);
        ret.setChanged(false);
        ret.poolSetStateFull();
        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:BOUI[" + ret.getBoui() + "]:");

        if (DEBUG > 0)
        {
            if (DEBUG > 0)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_CREATE")+" [" + name + "]:" +
                    (System.currentTimeMillis() - mili));
            }
        }

        if(!"Ebo_ClsReg".equals(name))
        {
            Long defaultTemplate = cacheBouis.getClassDefaultTemplate( name );
            if( defaultTemplate == null )
            {
                defaultTemplate = new Long(0);
                boObject object = this.loadObject(ctx, "Ebo_ClsReg", "name='"+name+"'");
                if (object.exists())
                {
                    AttributeHandler att=object.getAttribute("defaulttemplate");
                    if (att!=null)
                    {
                        BigDecimal templateBoui = (BigDecimal)att.getValueObject();
                        if( templateBoui != null )
                        {
                            defaultTemplate = new Long( templateBoui.longValue() );
                        }
                    }
                }
                cacheBouis.putClassDefaultTemplate( name, defaultTemplate );
            }
            if ( defaultTemplate.longValue() != 0 )
            {
                ret.applyTemplate(name, defaultTemplate.longValue() );
            }
        }
        return ret;
    }



    public boObject createObject(EboContext ctx, String name, long withBoui)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        boObject ret = getObject(ctx, name);
        ret.setEboContext(ctx);
        ret.create( withBoui );
//        ret.poolSetSharable( true );

//        this._seedObject(ctx, ret);

        ret.setChanged(false);
        ret.poolSetStateFull();

        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:BOUI[" + withBoui + "]:");

        if (DEBUG > 0)
        {
            if (DEBUG > 0)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_CREATE")+" [" + name + "]:" +
                    (System.currentTimeMillis() - mili));
            }
        }

//        ret.setBoui(withBoui);

        return ret;
    }

    public boObject createObject(EboContext ctx, String name,
        boolean modeTemplate) throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        boObject ret = getObject(ctx, name);
        ret.setEboContext(ctx);


        ret.create( createNewBoui( ctx ), modeTemplate);
//        ret.poolSetSharable( true );
//        this._seedObject(ctx, ret);

        ret.setChanged(false);
        ret.poolSetStateFull();

        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:BOUI[" + ret.getBoui() + "]:");

        if (DEBUG > 0)
        {
            if (DEBUG > 0)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_CREATE")+" [" + name + "]:" +
                    (System.currentTimeMillis() - mili));
            }
        }

        return ret;
    }

    private long createNewBoui( EboContext ctx )
    {
            return DataUtils.getSystemDBSequence( 
            		ctx,
                    "borptsequence", 
                    DataUtils.SEQUENCE_NEXTVAL
               );
    }

    public boObject createObject(EboContext ctx, String name, DataSet data)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        boObject ret = getObject(ctx, name);

        ret.setEboContext(ctx);
        ret.create( createNewBoui( ctx ) , data);

        data.setForInsert();
        ret.poolSetStateFull();

        boDefHandler def = ret.getBoDefinition();
        boDefAttribute[] atts = def.getBoAttributes();
        BigDecimal boui = BigDecimal.valueOf(ret.getBoui());

        for (short i = 0; i < atts.length; i++)
        {
            if (atts[i].getDbIsTabled() ||
                    ((atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (atts[i].getMaxOccurs() > 1)))
            {
                if (atts[i].getDbIsTabled())
                {
                    DataSet childs = data.rows(1).getChildDataSet(ctx,
                            atts[i].getName());

                    for (int z = 0; z < childs.getRowCount(); z++)
                    {
                        childs.rows(z + 1).updateBigDecimal("T$PARENT$", boui);
                    }
                }
                else
                {
                    DataSet childs = data.rows(1).getChildDataSet(ctx,
                            atts[i].getName());

                    for (int z = 0; z < childs.getRowCount(); z++)
                    {
                        childs.rows(z + 1).updateBigDecimal("PARENT$", boui);
                    }
                }
            }
        }
        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:BOUI[" + ret.getBoui() + "]:");
        if (DEBUG > 0)
        {
            logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_CREATE")+" [" + name + "]:" +
                (System.currentTimeMillis() - mili));
        }

        return ret;
    }

    public boObject loadObject(EboContext ctx, String name, long boui)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        boObject ret = null;

        if (boui != 0)
        {
            ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                    "BOOBJECT:BOUI[" + boui + "]:");
        }
        else
        {
            ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                    "BOOBJECT:CLASS[" + name + "]:");
        }

        if (ret == null)
        {
            ret = getObject(ctx, getClassNameFromBOUI( ctx, boui ) );
            ret.setEboContext(ctx);
            if (!ret.getName().equals("Ebo_Registry"))
            {
                if (boui != 0)
                {
                    ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret,
                        "BOOBJECT:BOUI[" + boui + "]:");
                }
                else
                {
                    ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:CLASS[" + name +
                        "]:");
                }
            }
            ret.load(boui);
            if( ret.getBoDefinition().haveVersionControl())
            {
                doVersionControl(ret);
            }

        }

        if (DEBUG > 0)
        {
            logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOD_NAME_LONG")+" [" + name + "/" + boui +
                "]:" + (System.currentTimeMillis() - mili));
        }
        if( ctx.isInTransaction() )
        {
            ctx.addTransactedObject( ret );
        }

        return ret;
    }
    
    
    public boObject loadObject(EboContext ctx, String boql, Object[] sqlargs)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        QLParser qp = new QLParser();
        String sql = qp.toSql(boql, ctx,false);
        boObject ret = loadObject(ctx, qp.getObjectDef().getBoName(),
                "BOUI=(" + sql + ")", sqlargs);

        if (DEBUG > 0)
        {
            logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOAD_BOQL")+" [" + qp.getObjectDef().getBoName() +
                "/" + sql + "]:" + (System.currentTimeMillis() - mili));
        }
        return ret;
    }
    
    public boObject loadObject( EboContext ctx, String objName, long boui, DataSet data ) throws boRuntimeException {
    	boObject ret = null;
        if (boui != 0)
        {
            ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                    "BOOBJECT:BOUI[" + boui + "]:");
        }
        if (ret == null)
        {
	    	ret = getObject(ctx, objName);
	        ret.setEboContext(ctx);
	        ret.load( data );
	        ret.setBoui( boui );
	        
	        ret.setChanged( false );
	        
	        ret.poolUnSetStateFull();
	        
            if (boui != 0)
            {
                ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret,
                    "BOOBJECT:BOUI[" + boui + "]:");
            }
        }
        return ret;
    } 

    public boObject loadObject(EboContext ctx, String boql)
        throws boRuntimeException
    {
        long mili = System.currentTimeMillis();

        try
        {
            return loadObject(ctx, boql, (Object[]) null);
        }
        finally
        {
            if (DEBUG > 0)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOAD_BOQL")+" [" + boql + "]:" +
                    (System.currentTimeMillis() - mili));
            }
        }
    }

    public boObject loadObject(EboContext ctx, String name, String sql,
        Object[] sqlargs) throws boRuntimeException
    {
        long mili = System.currentTimeMillis();
        StringBuffer sb = new StringBuffer();

        for (short i = 0; (sqlargs != null) && (i < sqlargs.length); i++)
            sb.append( String.valueOf( sqlargs[i] ) ).append(',');

        String bosqlui = "BOOBJECT:SQL[" + sql + ":ARGUMENTS[" + sb + "]]";
        boObject ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx, bosqlui);

        if (ret == null)
        {
            ResultSet rslt = null;
            PreparedStatement pstm = null;
            try
            {
                boDefHandler def = boDefHandler.getBoDefinition( name );
                pstm = ctx.getConnectionData().prepareStatement("select classname from "+ def.getBoMasterTable() + " where ("+ sql +")" );
                for (int i = 0;sqlargs != null && i < sqlargs.length; i++)
                {
                    if( sqlargs[i] != null )
                    {
                        pstm.setObject( i + 1, sqlargs[i] );
                    }
                    else
                    {
                        pstm.setString( i + 1, null );
                    }
                }
                rslt = pstm.executeQuery();
                if( rslt.next() )
                {
                    String objName = rslt.getString( 1 );
                    ret = getObject(ctx, objName);
                    ret.setEboContext(ctx);

                    ArrayList args = null;

                    if (sqlargs != null)
                    {
                        args = new ArrayList();

                        for (byte i = 0; i < sqlargs.length; i++)
                        {
                            args.add(sqlargs[i]);
                        }
                    }

                    ret.load(sql, args);
                }
                else
                {
                    boDefHandler boDef = boDefHandler.getBoDefinition( name );
                    if( boDef != null && boDef.getClassType() == boDefHandler.TYPE_CLASS )
                    {
                        ret = createObject(ctx, name);
                    }
                    else
                    {
                        ret = null;
                    }
                }
            }
            catch( SQLException e )
            {
                throw new RuntimeException(e);
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
            if ((ret.getBoui() != 0) &&
                    !ret.getName().equals("Ebo_Registry"))
            {
                ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret,
                    new String[]
                    {
                        "BOOBJECT:BOUI[" + ret.getBoui() + "]:", bosqlui
                    });
            }
        }

        if (DEBUG > 0)
        {
            logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOAD_BOQL")+" [" + sql + "+" + sqlargs + "]:" +
                (System.currentTimeMillis() - mili));
        }
        if( ctx.isInTransaction() )
        {
            ctx.addTransactedObject( ret );
        }

        String objClassName = ret.getAttribute("CLASSNAME").getValueString();
        if( !"".equals( objClassName ) && !ret.getName().equals( objClassName ) )
        {
            ctx.getApplication().getMemoryArchive().getPoolManager().destroyObject(ret);
            throw new RuntimeException(MessageLocalizer.getMessage("THE_OBJECT_NAME_DOES_NOT_CORRESPOND_TO_THE_QUERY")+" [ "+ret.getName()+" ] ["+ret.getAttribute("CLASSNAME").getValueString()+"]");
        }

        return ret;
    }

    public boObject loadObject(EboContext ctx, String name, String sql)
        throws boRuntimeException
    {
        return loadObject(ctx, name, sql, null);
    }

    public void preLoadObjects(EboContext ctx, long[] bouis) throws boRuntimeException
    {
        try
        {
            StringBuffer sb = new StringBuffer();
            ArrayList args = new ArrayList();

            Hashtable classes = new Hashtable();

            for (int i = 0; i < bouis.length; i++)
            {
                String classname;
                if (ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                            "BOOBJECT:BOUI[" + bouis[i] + "]:") == null)
                {
                    if( (classname=cacheBouis.getClassName( bouis[i] )) == null )
                    {
                        if (sb.length() > 0)
                        {
                            sb.append(" OR ");
                        }

                        sb.append("UI$=?");
                        args.add(BigDecimal.valueOf(bouis[i]));
                    }
                    else
                    {
                        ArrayList uis;
                        if ((uis = (ArrayList) classes.get(classname)) == null)
                        {
                            classes.put(classname, uis = new ArrayList());
                        }

                        uis.add(BigDecimal.valueOf( bouis[i] ) );
                    }
                }
            }

            if ( classes.size() > 0 || args.size() > 0 )
            {
                Connection cn = ctx.getConnectionData();
                ResultSet rslt = null;
                PreparedStatement pstm = null;

                try
                {
                    if( sb.length() > 0 )
                    {
                        pstm = cn.prepareStatement(
                                "SELECT /*+ FIRST_ROWS */ UI$,CLSID FROM OEBO_REGISTRY WHERE (" +
                                sb.toString() + ")");

                        for (int i = 0; i < args.size(); i++)
                        {
                            pstm.setObject(i + 1, args.get(i));
                        }

                        rslt = pstm.executeQuery();

                        while (rslt.next())
                        {
                            BigDecimal boui = rslt.getBigDecimal(1);
                            String classname = rslt.getString(2);
                            ArrayList uis;

                            if ((uis = (ArrayList) classes.get(classname)) == null)
                            {
                                classes.put(classname, uis = new ArrayList());
                            }
                            cacheBouis.putBoui( boui.longValue(), classname );
                            uis.add(boui);
                        }
                    }
                }
                finally
                {
                    if (rslt != null)
                    {
                        rslt.close();
                    }

                    if (pstm != null)
                    {
                        pstm.close();
                    }
                }

                Enumeration classnames = classes.keys();
                int clscnt = 0;
                while (classnames.hasMoreElements())
                {
                    clscnt ++;
                    args.clear();

                    String classname = (String) classnames.nextElement();
                    ArrayList uis = (ArrayList) classes.get(classname);
                    sb.delete(0, sb.length());

                    for (int i = 0; i < uis.size(); i++)
                    {
                        if (sb.length() > 0)
                        {
                            sb.append(" OR ");
                        }

                        sb.append("BOUI="+uis.get(i).toString());
                        //args.add(uis.get(i));
                    }
                    boDefHandler bodef = boDefHandler.getBoDefinition(classname);
                    DataSet dataSet = ObjectDataManager.executeNativeQuery(ctx,
                            bodef, "(" + (sb.toString()) + ")", args);
                    preLoadObjects( ctx, dataSet );
                }
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }

    public void preLoadObjects(EboContext ctx, DataSet dataSet ) throws boRuntimeException
    {
        DataSet[] objDataSet = dataSet.split();
        for (int i = 0; i < objDataSet.length; i++)
        {
            boObject pobj = getObject(ctx, objDataSet[i].rows( 1 ).getString("CLASSNAME") );
            long     boui = objDataSet[i].rows( 1 ).getLong("BOUI");
            if (ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx, "BOOBJECT:BOUI[" + boui + "]:") == null)
            {
                pobj.setEboContext(ctx);
                pobj.load(objDataSet[i]);
                pobj.setChanged( false );
                
                if( ctx.isInTransaction() )
                {
                    ctx.addTransactedObject( pobj );
                }

                ctx.getApplication().getMemoryArchive().getPoolManager().putObject(pobj,
                    "BOOBJECT:BOUI[" + pobj.getBoui() + "]:");
            }
        }
    }

    public boObject getObjectInContext( EboContext ctx, long boui ) throws boRuntimeException
    {
        boObject ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                "BOOBJECT:BOUI[" + boui + "]:");
        return ret;
    }

    private static boObject getObjectInContextStatic( EboContext ctx, long boui ) throws boRuntimeException
    {
        boObject ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                "BOOBJECT:BOUI[" + boui + "]:");
        return ret;
    }

    public boObject loadObjectAs(EboContext ctx, long boui, String className)
        throws boRuntimeException
    {
        long mili;
        if (DEBUG > 0)
        {
            mili= System.currentTimeMillis();
        }

        if (boui == 0)
        {
            throw new boRuntimeException(boManagerBean.class.getName() +
                ".loadObject(EboContext,long)", "BO-3018", null, "" + boui);
        }

        boObject ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                "BOOBJECT:BOUI[" + boui + "]:");
        String auxN =ret.getClass().getName();
        if(auxN != null && auxN.startsWith("v1_0."))
        {
            auxN = auxN.substring(5);
        }

        if ((boui == 0) || (ret == null) || (!className.equals(auxN)))
        {
            if(!className.equals(auxN) && ret != null)
            {
                boObject aux = ret;
                ret = getObject(ctx, className);
                ret.setEboContext(ctx);
                ret.load(aux.getDataSet());
            }
            else
            {
                ret = getObject(ctx, className);
                ret.setEboContext(ctx);
                ret.load(boui);
            }


            if (!ret.getName().equals("Ebo_Registry"))
            {
                if (boui != 0)
                {
                    ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret,
                            "BOOBJECT:BOUI[" + ret.getBoui() + "]:", true);
                }
                else
                {
                    ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret, "BOOBJECT:CLASS[" + className +
                            "]:");
                }
            }

            if (DEBUG > 0)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOAD_BOUI")+" [" + className + "/" + boui +
                    "]:" + (System.currentTimeMillis() - mili));
            }
        }
        if( ctx.isInTransaction() )
        {
            ctx.addTransactedObject( ret );
        }
        return ret;
    }

    public boObject loadObject(EboContext ctx, long boui) throws boRuntimeException {
    	return loadObject(ctx, boui, true);
    }

    /**
     * 
     * Loads an object (optionally uses cache)
     * 
     * @param ctx The context to load the object to
     * @param boui The boui to load
     * @param useCache If the object cache should be used
     * 
     * @return
     * 
     * @throws boRuntimeException
     * 
     */
    public boObject loadObject(EboContext ctx, long boui, boolean useCache)
        throws boRuntimeException
    {
            long mili = 0;
            if (logger.isFinestEnabled())
            {
                mili= System.currentTimeMillis();
            }

            if (boui == 0)
            {
                throw new boRuntimeException(boManagerBean.class.getName() +
                    ".loadObject(EboContext,long)", "BO-3018", null, "" + boui);
            }

            
            boObject ret = null;
            
            if (useCache){
            	ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
                    "BOOBJECT:BOUI[" + boui + "]:");
            }

            if ((boui == 0) || (ret == null))
            {
            	XEODataManagerKey 	dmKey 	= cacheBouis.getRemoteBouiKey( new Long(boui) );
            	if( dmKey != null ) {
            		
            		IXEODataManager 	dm 		= dmKey.getDataManager();
            		
            		DataSet objectDataSet = ObjectDataManager.createEmptyObjectDataSet(
            				ctx, 
            				boDefHandler.getBoDefinition( dmKey.getObjectName() )
            		);
            		
            		dm.fillObjectDataSet( ctx, objectDataSet, dmKey );
            		ret = getObject( ctx, dmKey.getObjectName() );
            		ret.setEboContext( ctx );
            		ret.load( objectDataSet );
            		ret.setBoui( boui );
            		ret.setChanged( false );
            		ret.poolUnSetStateFull();
            		
                    ctx.getApplication().getMemoryArchive().getPoolManager().putObject(ret,
                            "BOOBJECT:BOUI[" + boui + "]:");
                    
            	} else {
	                ret = loadObject(ctx, getClassNameFromBOUI(ctx, boui) , boui);
	
	                if (logger.isFinestEnabled())
	                {
	                    logger.finest(LoggerMessageLocalizer.getMessage("BOBJ_LOAD_BOUI")+" [" + getClassNameFromBOUI(ctx, boui) + "/" + boui +
	                        "]:" + (System.currentTimeMillis() - mili));
	                }
            	}
            }

            if( ret.getBoDefinition().haveVersionControl())
            {
                doVersionControl(ret);
            }

            if( ctx.isInTransaction() )
            {
                ctx.addTransactedObject( ret );
            }

            return ret;
    }

    public String getClassNameFromBOUI(EboContext ctx, long boui)
        throws boRuntimeException
    {
    	Long oBoui = new Long( boui );
    	String classname = null;
    	
    	XEODataManagerKey key = cacheBouis.getRemoteBouiKey( oBoui );
    	if( key != null ) {
    		classname = key.getObjectName();
    	}
    	
        if ( classname==null )
        {
	        classname = cacheBouis.getClassName( oBoui );
	
	        if ( classname==null )
	        {
	            boObject ret = (boObject) ctx.getApplication().getMemoryArchive().getPoolManager().getObject(ctx,
	                    "BOOBJECT:BOUI[" + boui + "]:");
	            if( ret != null )
	            {
	                classname = ret.getName();
	            }
	            else
	            {
	
	                Connection cn = null;
	                PreparedStatement pstm = null;
	                ResultSet rslt = null;
	
	                try
	                {
	                    cn = ctx.getConnectionData();
	                    //pstm = cn.prepareStatement(
	                    //        "SELECT CLSID FROM EBO_REGISTRY WHERE UI$=?");
	                    pstm = cn.prepareStatement(
	                            "SELECT CLSID FROM OEBO_REGISTRY WHERE UI$=?",  ResultSet.TYPE_FORWARD_ONLY,ResultSet.CONCUR_READ_ONLY );
	
	                    pstm.setLong(1, boui);
	                    rslt = pstm.executeQuery();
	
	                    if (rslt.next())
	                    {
	                        classname = rslt.getString(1);
	                    }
	                    else
	                    {
	                          throw new boRuntimeException(boManagerBean.class.getName() +
	                        ".getClassNameFromBoui(EboContext,long) User:["+ctx.getSysUser().getUserName()+"]", "BO-3015", null, "" + boui);
	                    }
	                }
	                catch (SQLException e)
	                {
	                    throw new boRuntimeException(boManagerBean.class.getName() +
	                        ".loadObject(EboContext,long) User:["+ctx.getSysUser().getUserName()+"]", "BO-3015", e, "" + boui);
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
	                      logger.severe( LoggerMessageLocalizer.getMessage("EXCEPTION_IN_BOMANAGERBEAN_GETCLASSFROMBOUI_TRYING_TO_")+"  "+e.getMessage() );
	                    }
	
	
	                }
	            }
	            cacheBouis.putBoui( boui , classname );
	        }
        }
        return classname;
    }

    public boObject seedObject(EboContext ctx, boObject obj)
        throws boRuntimeException
    {
        _seedObject(ctx, obj);
        ctx.getApplication().getMemoryArchive().getPoolManager().putObject(obj, "BOOBJECT:BOUI[" + obj.getBoui() + "]:");

        return obj;
    }

    public boObject _seedObject(EboContext ctx, boObject obj)
        throws boRuntimeException
    {
            long nr = createNewBoui( ctx );
            obj.setBoui(nr);
            return obj;
    }

    public boObject bindObject(EboContext ctx, String name, DataSet data)
        throws boRuntimeException
    {
        boObject ret = getObject(ctx, name);
        ret.setEboContext(ctx);
        ret.bindObject(data);

        return ret;
    }

    public boObject updateObject(EboContext ctx, boObject bobj)
        throws  boRuntimeException
    {
        return updateObject(ctx, bobj, true, false);
    }

    public boObject updateObject(EboContext ctx, boObject bobj, boolean runEvents, boolean forceAllInTransaction)
        throws boRuntimeException
    {

            //if( bobj.getAttribute("attributeBase") != null )
             //   System.out.println( bobj.getAttribute("attributeBase").getObject().getAttribute("id1").getValueString() );

        // Isto aqui é um WORKAROUND
        // o problema é que quando se criava um TEMPLATE a partir de um objecto já existente o sistema tentava gravar
        // o object original.Não consegui descobrir onde é que estava a guardar referência.JMF 8-4-2005
        if ( bobj.getParameter("neverUpdate")!=null )
        {
            return bobj;
        }
        //------
        if(!bobj.isOkToSave )
          return bobj;
        bobj.wasSerialChecked = false;


        try
        {
            if(forceAllInTransaction)
            {
                ctx.forceAllInTransaction(forceAllInTransaction);
            }

        if (bobj.get_IsInOnSave() != boObject.UPDATESTATUS_IDLE && bobj.get_IsInOnSave() != boObject.UPDATESTATUS_WAITING_COMMIT )
        {
            return bobj;
        }

//        try
//        {
        
        	boolean isXeoCompatible = bobj.getBoDefinition().getDataBaseManagerXeoCompatible();


            bobj.set_IsInOnSave(boObject.UPDATESTATUS_UPDATING);
            boolean dook = false;
            beginTransaction(ctx, bobj);
            bobj.setEboContext(ctx);

            String ebo_registryFullTableName = "";
            String ebo_textIndexFullTableName = "";
            String ebo_referencesFullTableName = "";


            String schemaName = boRepository.getDefaultSchemaName(ctx.getApplication());
            if(schemaName != null && !"".equals(schemaName))
            {
                ebo_registryFullTableName = schemaName + ".";
                ebo_textIndexFullTableName = schemaName + ".";
            }
            ebo_registryFullTableName += "ebo_registry";
            ebo_textIndexFullTableName += "ebo_textindex";
            ebo_referencesFullTableName  += "ebo_references";

            // TODO: Fazer isto com o nov esquema de mappings
            boolean saveObjectData = fireMappingEvents( bobj );

            try
            {
                // Update class name of the Object
                bobj.getAttribute("CLASSNAME").setValueString( bobj.getName() );

                long currentboui = bobj.getBoui();
                if (!(bobj.getMode() == boObject.MODE_DESTROY) && saveObjectData)
                {
                	if( isXeoCompatible ) {
	                    PreparedStatement pstm = ctx.getConnectionData()
	                                                .prepareStatement("SELECT COUNT(*) FROM OEBO_REGISTRY WHERE UI$=?");
	                    pstm.setLong(1, currentboui);
	                    ResultSet rslt = pstm.executeQuery();
	                    rslt.next();
	
	                    boolean exists = rslt.getLong(1) > 0;
	                    rslt.close();
	                    pstm.close();
	
	                    if (!exists)
	                    {
	                    	String fieldSysDate = ctx.getDataBaseDriver().getDatabaseTimeConstant();
	                        pstm = ctx.getConnectionData().prepareStatement("insert into " +
	                                ebo_registryFullTableName +
	                                "   (sys_user, sys_icn, sys_dtcreate, sys_dtsave, ui$, ui_version, name, clsid, clsid_major_version, clsid_minor_version, boui, classname) " +
	                                "   values " +
	                                "  ( ? , 1 , " + fieldSysDate + " ,  " + fieldSysDate + " , ?, ?, ?, ?, ? , ?, ?, ?) ");
	                        pstm.setString(1, ctx.getSysUser().getUserName());
	                        pstm.setLong(2, bobj.getBoui());
	                        pstm.setLong(3, 1);
	                        pstm.setString(4, bobj.getName());
	                        pstm.setString(5, bobj.getName());
	                        pstm.setLong(6, bobj.bo_major_version);
	                        pstm.setLong(7, bobj.bo_minor_version);
	
	                        //                            pstm.setLong(10,DataUtils.getDBSequence(ctx.getConnectionSystem(),"borptsequence","nextval"));
	                        pstm.setLong(8, bobj.getBoui());
	                        pstm.setString(9, "Ebo_Registry");
	                        pstm.executeUpdate();
	                        pstm.close();
	                    }
	                    else if ( bobj.mustCast() )
	                    {
	                        pstm = ctx.getConnectionData().prepareStatement("update " +
	                                    ebo_registryFullTableName +
	                                    " set name = ? , clsid = ?  where boui = ?");
	                        pstm.setString(1, bobj.getName());
	                        pstm.setString(2, bobj.getName());
	                        pstm.setLong(3, bobj.getBoui());
	                        pstm.executeUpdate();
	                        pstm.close();
	                    }
                	}
                }

                boolean canUpdate = bobj.doWorkBeforeUpdate(runEvents, ctx.getForceAllInTransaction());

                if (canUpdate && bobj.getMode() == boObject.MODE_DESTROY )
                {

                    bobj.deleteBridgesAndObjectAttributes();

                    // ATENÇÃO : se alguma coisa falhar tem que se fazer o rollback ao objecto ..
                    if ( !bobj.getBoDefinition().getBoCanBeOrphan() )
                    {
                        // verify if nao orphan pode ser apagado
                        //Long[] oToDestroy = bobj.getUpdateQueue().getObjectsToRemove();

                        Long[] oToDestroy = buildDestroyQueue( ctx, bobj ).getObjectsToRemove();


                        StringBuffer xsql= new StringBuffer("SELECT COUNT(*) FROM " +  ebo_referencesFullTableName + " WHERE REFBOUI$= ? ");

                        for (int i = 0; i < oToDestroy.length ; i++)
                        {
                            xsql.append(" and boui!= ? ");
                        }

                        PreparedStatement pstm = ctx.getConnectionData()
                                                    .prepareStatement( xsql.toString() );

                        pstm.setLong(1, currentboui);

                        for (int i = 0; i < oToDestroy.length ; i++)
                        {
                            pstm.setLong(i+2, oToDestroy[i].longValue() );
                        }

                        ResultSet rslt = pstm.executeQuery();
                        rslt.next();
                        long xr=rslt.getLong(1);

                        canUpdate = xr == 0;

                        if( !canUpdate )
                        {
                        	bobj.set_IsInOnSave( boObject.UPDATESTATUS_IDLE );
                            logger.warn( LoggerMessageLocalizer.getMessage("OBJECT")+" "+bobj.getName()+":"+currentboui+" "+LoggerMessageLocalizer.getMessage("NOT_DELETED_STILL_HAVE_REFERENCES")  );
                            //bobj.setUpdateMode(boObject.MODE_EDIT);
                            //canUpdate = true;

                        }
                        rslt.close();
                        pstm.close();
                    }
                }

                canUpdate = saveObjectData && canUpdate;

                boObjectUpdateQueue q=null;

                if ( canUpdate )
                {
                    ArrayList[] refs = boReferencesManager.getReferencesList( bobj );

                    boolean waschanged = bobj.isChanged() ||
                        bobj.getDataSet().wasChanged();

                    bobj.set_IsInOnSave( boObject.UPDATESTATUS_UPDATING_REFERENCES );

                    if (bobj.getMode() == boObject.MODE_DESTROY)
                    {
                        // Remove references from Ebo_References
                        boReferencesManager.removeReferences(bobj);
                        
                       // constroi a queue para apagar ou gravar para os objectos dependentes do objecto que está a ser removido
                        boObjectUpdateQueue dq = buildDestroyQueue( ctx, bobj );
                        Long[] objsToSave = dq.getObjectsToSave();
                        Long[] destroyQueue = dq.getObjectsToRemove();
                        
                        updateQueue( ctx,  objsToSave, boObjectUpdateQueue.MODE_SAVE_FORCED );
                        
                        //setCycleReferencesToNull( ctx, bobj, destroyQueue );

                        if( isXeoCompatible ) {
                            // Guarda o objecto com as brigdes limpas e atributos a null devido às foreign keys
                            try {
                            	bobj.setUpdateMode( boObject.MODE_EDIT );
                            	ObjectDataManager.updateObjectData( bobj );
                            }
                            finally {
                            	bobj.setUpdateMode( boObject.MODE_DESTROY );
                            }
                        	DataManager.updateDataSet(bobj.getEboContext(), (DataSet)bobj.getDataSet(), false,bobj.isCheckICN());
                        }
                        else {
                        	IXEODataManager dm = ctx.getApplication().getXEODataManager( bobj.getBoDefinition() );
                        	dm.destroyDataSet(bobj.getEboContext(), bobj.getDataSet(), bobj );
                        }
                        updateQueue( ctx, destroyQueue,boObjectUpdateQueue.MODE_DESTROY_FORCED );

                        // Destroy DESTROY_FORCED in object queue
                       // updateObjectQueue( ctx,  bobj.getUpdateQueue() , true  ); este trabalho é feito agora no buildDestroyQueueave

                    }
                    else
                    {
                    	
                        if ( refs != null )
                        {
                            if( isXeoCompatible ) {
                            	boReferencesManager.updateReferencesLists( bobj , null, refs[ boReferencesManager.ARRAY_TO_REMOVE ] );
                            }
                        }

                        // Update related changed objects
                        if( !("Ebo_TextIndex".equalsIgnoreCase( bobj.getName() )  ) )
                        {
                        	q = buildUpdateQueueToSave( ctx, bobj, refs != null ?refs[ boReferencesManager.ARRAY_TO_REMOVE ]:null );
                            updateQueue( ctx, q.getObjectsToSave() , boObjectUpdateQueue.MODE_SAVE_FORCED  );
                        }


                        // Update SAVE_FORCED in objectqueue
                       // updateObjectQueue( ctx,  bobj.getUpdateQueue(), false ); este trabalho é feito agora no buildQueueToSave

                    }

                    bobj.set_IsInOnSave( boObject.UPDATESTATUS_UPDATING );

                    if (
                        waschanged ||
                        ( !bobj.exists()  && bobj.getMode() != boObject.MODE_DESTROY ) ||
                        ( bobj.getMode() == boObject.MODE_DESTROY && bobj.exists() )
                        )
                    {
                        //se estiver indicado, criar um registo de versão do objecto

                        if (bobj.getVersioning())
                        {
                            boObject version = null;
                            boObject[] logs = boVersioning.createVersionData(ctx,
                                    bobj, true);
                            version = boVersioning.createVersionObject(ctx, logs,
                                    bobj.getBoui());

                            if (version != null)
                            {
                                version.update();
                            }
                        }

                        if( bobj.getMode() != boObject.MODE_DESTROY )
                        {
                            // Compute security keys
                            if (bobj.getBoDefinition().implementsSecurityRowObjects())
                            {
                                securityOPL.setSecurityKeys(bobj);
                            }

                            // Update Ebo_References table
                            if ( refs != null )
                            {
                                if( isXeoCompatible ) {
                                	boReferencesManager.updateReferencesLists( bobj , refs[ boReferencesManager.ARRAY_TO_ADD ], null );
                                }
                            }
                        }

                        // Update the Object Data
                        if( isXeoCompatible ) {
                        	ObjectDataManager.updateObjectData(bobj);
                        }
                        else {
                        	IXEODataManager dm = ctx.getApplication().getXEODataManager( bobj.getBoDefinition() );
                        	dm.updateDataSet( ctx, bobj.getDataSet(), bobj );
                        }
                        bobj.set_IsInOnSave( boObject.UPDATESTATUS_WAITING_ENDTRANSACTION );

                    }

                    if( bobj.getMode() != boObject.MODE_DESTROY && !"Ebo_TextIndex".equals(bobj.getName()) )
                    {

                        // Destroy !orphans no longer referenced by this object
                        Long[] cc = q.getObjectsToRemove();
                        long xx=currentboui;

                        updateQueue( ctx,
                                buildUpdateQueueToDestroy(
                                    ctx,
                                    bobj,
                                    refs==null?null:refs[ boReferencesManager.ARRAY_TO_REMOVE ],
                                    refs==null?null:refs[ boReferencesManager.ARRAY_TO_ADD ]
                                ).getObjectsToRemove() ,
                                boObjectUpdateQueue.MODE_DESTROY_FORCED
                            );

                        updateQueue( ctx, q.getObjectsToRemove() , boObjectUpdateQueue.MODE_DESTROY_FORCED  );

                        //updateQueue( ctx, q.getObjectsToRemove() , boObjectUpdateQueue.MODE_DESTROY_FORCED  );

                        // Destroy DESTROY_FORCED in object queue
                        //updateObjectQueue( ctx,  bobj.getUpdateQueue() , true  ); passou para o buildUpdateQueueToDestroy


                    }


//                    if ( my_trans )
//                    {
//
//                        Long [] toSaveInEboContext = ctx.getUpdateQueue().getObjectsToSave();
//                        Long [] toDestroyInEboContext = ctx.getUpdateQueue().getObjectsToRemove();
//                        ctx.getUpdateQueue().clear();
//                        updateQueue( ctx, toDestroyInEboContext , boObjectUpdateQueue.MODE_SAVE_FORCED  );
//                        updateQueue( ctx, toSaveInEboContext , boObjectUpdateQueue.MODE_SAVE_FORCED  );
//                    }


                    //If mode is destroy destroy the text index and the registry of the object
                    if ( (bobj.getMode() == boObject.MODE_DESTROY) )
                    {
                          PreparedStatement pstm = ctx.getConnectionData()
                                            .prepareStatement("delete from " + ebo_textIndexFullTableName + " where ui$=?");
                          pstm.setLong(1, bobj.getBoui());
                          try
                          {
                              pstm.executeUpdate();
                          }
                          catch (SQLException e)
                          {
                                int errorCode = e.getErrorCode( );
                                logger.finer(LoggerMessageLocalizer.getMessage("ERROR_DELETING_EBO_TEXTINDEX")+" "+e.getMessage());
                                //ignore if error occur in delete Ebo_TextIndex
                          }
                          finally
                          {
                             if( pstm!=null)  pstm.close();
                          }
                          PreparedStatement pstm2 = ctx.getConnectionData()
                                            .prepareStatement("delete from " + ebo_registryFullTableName + " where ui$=?");
                          pstm2.setLong(1, bobj.getBoui());
                          try
                          {
                              pstm2.executeUpdate();
                          }
                          catch (SQLException e)
                          {
                                logger.severe(e);
                                StringBuffer xsql= new StringBuffer("SELECT boui FROM " +  ebo_referencesFullTableName + " WHERE REFBOUI$= ? ");

                                PreparedStatement pst = ctx.getConnectionData()
                                                            .prepareStatement( xsql.toString() );

                                pst.setLong(1, bobj.getBoui());

                                ResultSet rslt = pst.executeQuery();
                                while(rslt.next())
                                {
                                    logger.finer(LoggerMessageLocalizer.getMessage("BOUI_NOT_DELETED")+": " + rslt.getLong(1));
                                }
                                rslt.close();
                                pstm.close();

                                int errorCode = e.getErrorCode( );
                                //TODO: SQLState for the Constraint error is 23000 for MySQL, Oracle and SQLServer
                                //we should consider changing to sql state instead of vendor specific getErrorCode()
                                switch (errorCode)
                                {
                                    case 2292:  // Referenced constraint error code, Oracle
                                    {
                                    	throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3023",e);
                                    }
                                    case 547:  // Referenced constraint error code, SQLServer
                                    {
                                    	throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3023",e);
                                    }   
                                    case 1451 : // Referenced constraint error code, MySQL
                                    {
                                    	throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3023",e);
                                    }
                                    default:
                                        throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3055",e);
                                }
                          }
                          finally
                          {
                              if(pstm2!=null) pstm2.close();
                          }
                    }

                    // if object is new and sharable turn sharable to false
//                    if( bobj.poolIsSharable() )
//                    {
//                        bobj.poolSetSharable( false );
//                    }

                    bobj.doWorkAfterUpdate(runEvents, ctx.getForceAllInTransaction());

                   // bobj.isInOnSave = boObject.UPDATESTATUS_IDLE;

                    bobj.p_exists = bobj.getMode()!=bobj.MODE_DESTROY;



                    dook = true;
                }
                else
                {
                    dook = true;
                    // do nothing ... objecto era não orphan e mantem refrencias a outros objectos ou no event onsave foi cancelado
                }

                return bobj;
            }
            catch (boRuntimeException e)
            {
                if ((e.getSrcObject() != null) &&
                        e.getErrorCode().equals("BO-3023") &&
                        e.getSrcObject().getName().equals("Ebo_Registry"))
                {
                    throw new boRuntimeException(loadObject(ctx, bobj.getBoui()),
                        "ObjectDataManager.updateObjectData(boObject)",
                        "BO-3023", e);
                }

                rollBackTransaction(ctx);

                throw (e);
            }
            catch (SQLException e)
            {
                logger.severe(e);
                if ((e.getMessage() != null) &&
                        e.getMessage().startsWith("Registo já foi alterado por outro utilizador"))
                {
                    throw new boRuntimeException(boManagerBean.class.getName() +
                        ".updateObject(EboContext, boObject)", "BO-3022", e,
                        bobj.bo_name + "(" + bobj.bo_boui + ")");
                }
                else if (e.getErrorCode() == 2292 || e.getErrorCode() == 1451) //mysql 
                {
                    throw new boRuntimeException(boManagerBean.class.getName() +
                        ".updateObject(EboContext, boObject)", "BO-3023", e,
                        bobj.bo_name + "(" + bobj.bo_boui + ")");
                }
                else if (e.getErrorCode() == 1 || e.getErrorCode() == 1062) //mysql 
                {
                    throw new boRuntimeException(boManagerBean.class.getName() +
                        ".updateObject(EboContext, boObject)", "BO-3054", e,
                        bobj.bo_name + "(" + bobj.bo_boui + ")");
                }
                throw new boRuntimeException(boManagerBean.class.getName() +
                    ".updateObject(EboContext, boObject)", "BO-3016", e,
                    bobj.bo_name + "(" + bobj.bo_boui + ")");
            }
            catch (Exception e)
            {
                String    msg  = "";
                Throwable thrw = new Throwable();
                StackTraceElement[] ste = null;

                msg += "Username    : " + ctx.getSysUser().getUserName() + "\n";

                if(bobj != null)
                    msg += "Obj Boui    : " + bobj.getBoui() + "\n";
                else
                    msg += "Obj Boui    : IS NULL\n";

                if(thrw != null && thrw.getStackTrace() != null)
                {
                    msg += "Cause      1: " + thrw.getCause() + "\n";
                    msg += "Message    1: " + thrw.getMessage() + "\n";
                    msg += "StackTrace 1: ";
                    ste = thrw.getStackTrace();

                    for(int z=0; z < ste.length; z++)
                    {
                        msg += ste[z].getClassName() + "." + ste[z].getMethodName() + "(" +
                               ste[z].getFileName() + ":" + String.valueOf(ste[z].getLineNumber()) + ")\n";
                    }
                }

                if(e != null && e.getStackTrace() != null)
                {
                    msg += "Cause      2: " + e.getCause() + "\n";
                    msg += "Message    2: " + e.getMessage() + "\n";
                    msg += "StackTrace 2: ";
                    ste = e.getStackTrace();

                    for(int z=0; z < ste.length; z++)
                    {
                        msg += ste[z].getClassName() + "." + ste[z].getMethodName() + "(" +
                               ste[z].getFileName() + ":" + String.valueOf(ste[z].getLineNumber()) + ")\n";
                    }
                }

                logger.severe(msg, e);

                throw new boRuntimeException(boManagerBean.class.getName() +
                    ".updateObject(EboContext, boObject)", "BO-3016", e,
                    bobj.bo_name + "(" + bobj.bo_boui + ")");
            }
            finally
            {
                if (dook)
                {
                    commitTransaction(ctx);
                	//Retrieve all iFiles from the attributes and "commit" them
                	List iFilesAttributes = bobj.getAttributes(boDefAttribute.VALUE_IFILELINK);
                    for (int i = 0; i < iFilesAttributes.size(); i++)
                    {
                    	//Retrieve the current attribute handler
                    	AttributeHandler currHandler = (AttributeHandler) iFilesAttributes.get(i);
                    	if (currHandler.getDefAttribute().getECMDocumentDefinitions() != null)
                    	{
                    			iFile currentFile = currHandler.getValueiFile();
                    			if (currentFile != null){
	                    			try {	iFileTransactionManager.commitIFile(currentFile, 
	                    					bobj.getEboContext());
									} catch (iFileException e) { throw new boRuntimeException2(e);	}
                    			}
                    	}
                    }
                }
                else
                {
                    rollBackTransaction(ctx);
                	try {
	                	List iFilesAttributes = bobj.getAttributes(boDefAttribute.VALUE_IFILELINK);
	                    for (int i = 0; i < iFilesAttributes.size(); i++)
	                    {
								//Retrieve the current attribute handler
								AttributeHandler currHandler = (AttributeHandler) iFilesAttributes.get(i);
								if (currHandler.getDefAttribute().getECMDocumentDefinitions() != null)
								{
										iFile currentFile = currHandler.getValueiFile();
										try {
											iFileTransactionManager.rollbackIFile(currentFile.getId(), currentFile, bobj.getEboContext());
										} catch (iFileException e) {
											//throw new boRuntimeException2(e);
										}
								}
	                    }
					} catch (Exception e) {
						logger.severe("Error Rolliback files boManagerBean [" + e.getMessage() + "]");
					}
                }
            }
      //  }
        }
        finally
        {
            if(forceAllInTransaction)
            {
                ctx.forceAllInTransaction(false);
            }
        }


    }
/*
    public static void setCycleReferencesToNull( EboContext ctx, boObject obj , Long[] qToDestroy ) throws SQLException {
    	List		bouisToRemover 	= Arrays.asList( qToDestroy );
    	
    	if( bouisToRemover.size() == 0 )
    		return;
    	
    	
    	StringBuffer 	setNullAtts		   = new StringBuffer();

    	Enumeration oEnum = obj.getAttributes().elements();

        while(oEnum.hasMoreElements())
        {
            AttributeHandler att = (AttributeHandler)oEnum.nextElement();
            if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && !(att instanceof boBridgeMasterAttribute) )
            {
                if( att.getDefAttribute().getDbIsBinding() )
                {
                    if( att.getDefAttribute().getDbIsTabled())
                    {
                    	
                    	PreparedStatement pstm = ctx.getConnectionData().prepareStatement(
                    			"delete from " + att.getDefAttribute().getDbTableName() + 
                    			" where " + 
                    			att.getDefAttribute().getDbTableFatherFieldName() + " = ? AND " +
                    			att.getDefAttribute().getDbTableChildFieldName() + " = ? "
                    	);
                    	
                    	try
                    	{
                    	
	                        DataSet childs = obj.getDataSet().rows( 1 ).getChildRows( ctx, att.getDefAttribute().getName() );
	                        for (int i = 0; i < childs.getRowCount(); i++)
	                        {
	                            if ( !childs.rows( i + 1 ).isNew() )
	                            {
	                                DataRow row = childs.rows( i + 1 ).getFlashBackRow() != null?
	                                                childs.rows( i + 1 ).getFlashBackRow():
	                                                childs.rows( i + 1 );
	                                
	                                BigDecimal removeBoui = row.getBigDecimal( att.getDefAttribute().getDbName() );
	                                if( removeBoui != null )
	                                {
		                                if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
		                                	pstm.setLong( 1 , obj.getBoui() );
		                                	pstm.setBigDecimal( 2, removeBoui );
		                                	pstm.addBatch();
		                                }
	                                }
	                                //addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue,  att.getDefAttribute() );
	                            }
	                        }
	
	                        for (int i = 0; i < childs.getDeletedRowsCount(); i++)
	                        {
	                            DataRow row = childs.deletedRows( i + 1 ).getFlashBackRow() != null?
	                                            childs.deletedRows( i + 1 ).getFlashBackRow():
	                                            childs.deletedRows( i + 1 );
	
	                            BigDecimal removeBoui = row.getBigDecimal( att.getDefAttribute().getDbName() );
	                            if( removeBoui != null )
	                            {
	                                if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
	                                	pstm.setLong( 1 , obj.getBoui() );
	                                	pstm.setBigDecimal( 2, removeBoui );
	                                	pstm.addBatch();
	                                }
	                            }
	                            //addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue, att.getDefAttribute() );
	                        }
	                        //pstm.executeBatch();
                    	}
                    	finally
                    	{
                    		pstm.close();
                    	}
                        
                    }
                    else
                    {
                        DataRow row = obj.getDataRow().getFlashBackRow() != null?
                                        obj.getDataRow().getFlashBackRow():
                                        obj.getDataRow();

                        BigDecimal removeBoui = row.getBigDecimal( att.getDefAttribute().getDbName() );
                        if( removeBoui != null )
                        {
                            if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
                            	if( setNullAtts.length() > 0 ) setNullAtts.append(',');
                            	setNullAtts.append( att.getDefAttribute().getDbName() ).append("=NULL");
                            }
                        }
                        //addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue, att.getDefAttribute() );
                    }
                }
            }
        }

        if( setNullAtts.length() > 0 ) {
        	
        	String sSql = 
    			"UPDATE  " + obj.getBoDefinition().getBoMasterTable() +
    			" SET " + setNullAtts.toString() +
    			" WHERE " + 
    			" BOUI = ?";
        	
        	PreparedStatement pstm = ctx.getConnectionData().prepareStatement( sSql );
        	try {
        		pstm.setLong(1, obj.getBoui());
        		pstm.executeUpdate();
        	}
        	finally
        	{
        		pstm.close();
        	}
        	
        }
        
        
        boDefAttribute[] allatts = obj.getBoDefinition().getAttributesDef();

        for (int i = 0; i < allatts.length; i++)
        {
            if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 )
            {
                bridgeHandler bridge = obj.getBridge( allatts[i].getName() );
                if( bridge != null )
                {
                	PreparedStatement pstm = ctx.getConnectionData().prepareStatement(
                			"delete from " + bridge.getDefAttribute().getDbTableName() + 
                			" where " + 
                			bridge.getDefAttribute().getDbTableFatherFieldName() + " = ? AND " +
                			bridge.getDefAttribute().getDbTableChildFieldName() + " = ? "
                	);

                	try {
                	
	                	boDefAttribute[] bridgeatts = bridge.getDefAttribute().getBridge().getBoAttributes();
	
	                    boBridgeIterator it = bridge.iterator();
	                    while( it.next() )
	                    {
	                        if ( !it.currentRow().getDataRow().isNew() )
	                        {
	                            DataRow row = it.currentRow().getDataRow().getFlashBackRow() != null?
	                                            it.currentRow().getDataRow().getFlashBackRow():
	                                            it.currentRow().getDataRow();
	
	
	                            //addToDestroyQueue( ctx, row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() ), queue, bridge.getDefAttribute() );
	                            BigDecimal removeBoui = row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() );
	                            if( removeBoui != null )
	                            {
	                                if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
	                                	pstm.setLong( 1 , obj.getBoui() );
	                                	pstm.setBigDecimal( 2, removeBoui );
	                                	pstm.addBatch();
	                                }
	                            }
	                            
	
	                            for (int z = 0; z < bridgeatts.length ; z++)
	                            {
	                                if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
	                                {
	                                    removeBoui = row.getBigDecimal( bridgeatts[z].getDbName() );
	                                    if( removeBoui != null )
	                                    {
	                                        if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
	    	                                	pstm.setLong( 1 , obj.getBoui() );
	    	                                	pstm.setBigDecimal( 2, removeBoui );
	    	                                	pstm.addBatch();
	                                        }
	                                    }
	                                    //addToDestroyQueue( ctx, row.getBigDecimal( bridgeatts[z].getDbName() ) , queue, bridgeatts[z] );
	                                }
	                            }
	                        }
	                    }
	                    DataSet delrows = bridge.getRslt().getDataSet();
	                    for (int y = 0; y < delrows.getDeletedRowsCount(); y++)
	                    {
	
	                        DataRow row = delrows.deletedRows( y + 1 ).getFlashBackRow() != null?
	                                        delrows.deletedRows( y + 1 ).getFlashBackRow():
	                                        delrows.deletedRows( y + 1 );
	
	                        BigDecimal removeBoui = row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() );
	                        //addToDestroyQueue( ctx, row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() ), queue, bridge.getDefAttribute() );
	                        if( removeBoui != null )
	                        {
	                            if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
	                            	pstm.setLong( 1 , obj.getBoui() );
	                            	pstm.setBigDecimal( 2, removeBoui );
	                            	pstm.addBatch();
	                            }
	                        }
	
	                        for (int z = 0; z < bridgeatts.length ; z++)
	                        {
	                            if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
	                            {
	                            	removeBoui = row.getBigDecimal( bridgeatts[z].getDbName() );
	                                if( removeBoui != null )
	                                {
	                                    if( bouisToRemover.contains( new Long(removeBoui.longValue() ) ) ) {
		                                	pstm.setLong( 1 , obj.getBoui() );
		                                	pstm.setBigDecimal( 2, removeBoui );
		                                	pstm.addBatch();
	                                    }
	                                }
	                                
	                                //addToDestroyQueue( ctx, row.getBigDecimal( bridgeatts[z].getDbName() ) , queue, bridgeatts[z] );
	                            }
	                        }
	                    }
	                    //pstm.executeBatch();
                	}
                	finally {
                		pstm.close();
                	}
                	
                }
            }
        }
        
        // Execute de delete statements
    
    
    }
*/    

    public static void updateQueue( EboContext ctx, Long[]toupdate , byte mode   )
        throws SQLException, boRuntimeException
    {
    	for ( int i = 0; i < toupdate.length; i++)
        {
             boObject xobj = boObject.getBoManager().loadObject( ctx,  toupdate[i].longValue() );
             
             boolean checkSecurity = xobj.isCheckSecurity();
             try
             {
                 xobj.setCheckSecurity( false );
                 if ( mode == boObjectUpdateQueue.MODE_SAVE_FORCED )
                 {
                     xobj.update();
                 }
                 else if ( mode == boObjectUpdateQueue.MODE_DESTROY_FORCED )
                 {
                     xobj.destroy();
                 }
             }
             finally
             {
                 xobj.setCheckSecurity( checkSecurity );
             }
        }
    }


    /*
     *
     * constroi a queue para os objectos dependentes do objecto que está a ser removido
     */
    private boObjectUpdateQueue buildDestroyQueue( EboContext ctx, boObject obj ) throws boRuntimeException
    {
        boObjectUpdateQueue queue = new boObjectUpdateQueue();
        boObjectUpdateQueue queueObejct = obj.getUpdateQueue();

        long[][] xobjqueue = obj.getUpdateQueue().getObjects();
        for (int i = 0; i < xobjqueue.length; i++)
        {
            if( xobjqueue[i][1] == boObjectUpdateQueue.MODE_DESTROY_FORCED )
            {
                queue.add( xobjqueue[i][0] , boObjectUpdateQueue.MODE_DESTROY_FORCED );
            }
            else if( xobjqueue[i][1] == boObjectUpdateQueue.MODE_SAVE_FORCED )
            {
                queue.add( xobjqueue[i][0] , boObjectUpdateQueue.MODE_SAVE_FORCED );
            }

        }

        Enumeration oEnum = obj.getAttributes().elements();
        BigDecimal refboui;
        while(oEnum.hasMoreElements())
        {
            AttributeHandler att = (AttributeHandler)oEnum.nextElement();
            if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && !(att instanceof boBridgeMasterAttribute) )
            {
                if( att.getDefAttribute().getDbIsBinding() )
                {
                    if( att.getDefAttribute().getDbIsTabled())
                    {
                        DataSet childs = obj.getDataSet().rows( 1 ).getChildDataSet( ctx, att.getDefAttribute().getName() );
                        for (int i = 0; i < childs.getRowCount(); i++)
                        {
                            if ( !childs.rows( i + 1 ).isNew() )
                            {
                                DataRow row = childs.rows( i + 1 ).getFlashBackRow() != null?
                                                childs.rows( i + 1 ).getFlashBackRow():
                                                childs.rows( i + 1 );
                                addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue,  att.getDefAttribute() );
                            }
                        }

                        for (int i = 0; i < childs.getDeletedRowsCount(); i++)
                        {
                            DataRow row = childs.deletedRows( i + 1 ).getFlashBackRow() != null?
                                            childs.deletedRows( i + 1 ).getFlashBackRow():
                                            childs.deletedRows( i + 1 );

                            addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue, att.getDefAttribute() );
                        }
                    }
                    else
                    {
                        DataRow row = obj.getDataRow().getFlashBackRow() != null?
                                        obj.getDataRow().getFlashBackRow():
                                        obj.getDataRow();
                        addToDestroyQueue( ctx, row.getBigDecimal( att.getDefAttribute().getDbName() ) , queue, att.getDefAttribute() );
                    }
                }
            }
        }

        boDefAttribute[] allatts = obj.getBoDefinition().getAttributesDef();

        for (int i = 0; i < allatts.length; i++)
        {
            if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 )
            {
                bridgeHandler bridge = obj.getBridge( allatts[i].getName() );
                if( bridge != null )
                {
                    boDefAttribute[] bridgeatts = bridge.getDefAttribute().getBridge().getBoAttributes();

                    boBridgeIterator it = bridge.iterator();
                    while( it.next() )
                    {
                        if ( !it.currentRow().getDataRow().isNew() )
                        {
                            DataRow row = it.currentRow().getDataRow().getFlashBackRow() != null?
                                            it.currentRow().getDataRow().getFlashBackRow():
                                            it.currentRow().getDataRow();
                            addToDestroyQueue( ctx, row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() ), queue, bridge.getDefAttribute() );

                            for (int z = 0; z < bridgeatts.length ; z++)
                            {
                                if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                {
                                    addToDestroyQueue( ctx, row.getBigDecimal( bridgeatts[z].getDbName() ) , queue, bridgeatts[z] );
                                }
                            }
                        }
                    }
                    DataSet delrows = bridge.getRslt().getDataSet();
                    for (int y = 0; y < delrows.getDeletedRowsCount(); y++)
                    {

                        DataRow row = delrows.deletedRows( y + 1 ).getFlashBackRow() != null?
                                        delrows.deletedRows( y + 1 ).getFlashBackRow():
                                        delrows.deletedRows( y + 1 );

                        addToDestroyQueue( ctx, row.getBigDecimal( bridge.getDefAttribute().getBridge().getChildFieldName() ), queue, bridge.getDefAttribute() );

                        for (int z = 0; z < bridgeatts.length ; z++)
                        {
                            if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                addToDestroyQueue( ctx, row.getBigDecimal( bridgeatts[z].getDbName() ) , queue, bridgeatts[z] );
                            }
                        }
                    }
                }
            }
        }
        return queue;
    }
/*
 *
 * chamado do buildDestroyQueue
 */
    private void addToDestroyQueue( EboContext ctx, BigDecimal boui, boObjectUpdateQueue queue, boDefAttribute attdef ) throws boRuntimeException
    {
        if( boui != null )
        {
            long cboui = boui.longValue();

            String classname;
            classname = getClassNameFromBOUI( ctx , cboui );

            if( classname != null ) 
            {
                boDefHandler refdef = boDefHandler.getBoDefinition( classname );
                boolean isnotorphan = ( attdef==null || !attdef.getChildIsOrphan() ) || !refdef.getBoCanBeOrphan();
                if ( isnotorphan )
                {
                    queue.add( cboui, boObjectUpdateQueue.MODE_DESTROY_FORCED );
                }
            }
        }
    }


    /*
     * Depois de gravar o objecto vai construir uma queue de objectos não orfãos
     * para destruir
     */
    private boObjectUpdateQueue buildUpdateQueueToDestroy( EboContext ctx , boObject obj, ArrayList remrefs, ArrayList addrefs ) throws boRuntimeException
    {
        boObjectUpdateQueue queue = new boObjectUpdateQueue();
        if( remrefs != null )
        {
            for (int i = 0; i < remrefs.size(); i++)
            {
                // Só adiciona a queue to Destroy se o objecto não tiver mais referencias nele.
                // Isto para evitar que se mova as linhas e a gravação tente remover os objectos relacionados.
                // Verifica se a referencia do objecto não foi removida mas apenas trocada.

                if( addrefs == null || addrefs.indexOf( remrefs.get( i ) ) == -1 )
                {
                    boReferencesManager.referenceHandler refh = (boReferencesManager.referenceHandler)remrefs.get( i );
                    long cboui = refh.attributeValue.longValue();
                    
                    if( cboui != 0 ) {
	                    boObject refobj = boObject.getBoManager().getObjectInContext( obj.getEboContext(), cboui );
	
	                    String classname;
	
	                    if( refobj == null )
	                    {
	                        classname = boObject.getBoManager().getClassNameFromBOUI( obj.getEboContext(), cboui );
	                    }
	                    else
	                    {
	                        classname = refobj.getName();
	                    }
	
	                    if( classname != null )
	                    {
	                        boDefHandler refdef = boDefHandler.getBoDefinition( classname );
	                        if (
	                            !( refh.attributeDef.getChildIsOrphan() || refdef.getBoCanBeOrphan())
	                            &&
	                            ( refobj==null || (refobj != null && refobj.exists() ) )
	                           )
	                        {
	
	                            queue.add( cboui, boObjectUpdateQueue.MODE_DESTROY_FORCED );
	                        }
	                    }
                    }
                }
            }
        }
        return queue;
    }

    /*
     * constroi queue para gravar dependencias do objecto que está a ser gravado
     *
     */
    public static boObjectUpdateQueue buildUpdateQueueToSave(EboContext ctx,  boObject obj , ArrayList remrefs   ) throws boRuntimeException
    {
        boObjectUpdateQueue queue = new boObjectUpdateQueue();


        long[][] xobjqueue = obj.getUpdateQueue().getObjects();
        for (int i = 0; i < xobjqueue.length; i++)
        {
            boObject xobj = boObject.getBoManager().loadObject( ctx, xobjqueue[i][0] );
            if( xobjqueue[i][1] == boObjectUpdateQueue.MODE_SAVE_FORCED )
            {
                queue.add( xobjqueue[i][0] , boObjectUpdateQueue.MODE_SAVE_FORCED );
            }
            else if ( xobjqueue[i][1] == boObjectUpdateQueue.MODE_DESTROY_FORCED )
            {
                queue.add( xobjqueue[i][0] , boObjectUpdateQueue.MODE_DESTROY_FORCED );
            }
        }

        Enumeration oEnum = obj.getAttributes().elements();
        BigDecimal refboui;
        while(oEnum.hasMoreElements())
        {
            AttributeHandler att = (AttributeHandler)oEnum.nextElement();
            if( att.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && !(att instanceof boBridgeMasterAttribute) )
            {
                if( att.getDefAttribute().getDbIsBinding() )
                {
                    if( att.getDefAttribute().getDbIsTabled())
                    {
                        BigDecimal[] refbouis = (BigDecimal[])att.getValueObject();
                        if( refbouis != null )
                        {
                            for (int i = 0; i < refbouis.length; i++)
                            {
                                addToQueue( ctx, refbouis[i], queue, boObjectUpdateQueue.MODE_SAVE_FORCED );
                            }
                        }
                    }
                    else
                    {
                        addToQueue( ctx, (BigDecimal)att.getValueObject(), queue , boObjectUpdateQueue.MODE_SAVE_FORCED );
                    }
                }
            }
        }

        boDefAttribute[] allatts = obj.getBoDefinition().getAttributesDef();

        for (int i = 0; i < allatts.length; i++)
        {
            if( allatts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && allatts[i].getMaxOccurs() > 1 && !allatts[i].getDbIsTabled() )
            {
            	
            	bridgeHandler bridge;
            	if( ctx.isInModeBatch() )
            		bridge = obj.getBridges().get( allatts[i].getName() );
            	else 
            		bridge = obj.getBridge( allatts[i].getName() );
            	
                //bridgeHandler bridge = obj.getBridge( allatts[i].getName() ); 
                if( bridge != null )
                {
                    boDefAttribute[] bridgeatts = bridge.getDefAttribute().getBridge().getBoAttributes();
                    bridge.beforeFirst();
                    while(bridge.next())
                    {
                        refboui = BigDecimal.valueOf( bridge.getCurrentBoui() ) ;
                        addToQueue( ctx, refboui, queue , boObjectUpdateQueue.MODE_SAVE_FORCED );
                        for (int z = 0; z < bridgeatts.length ; z++)
                        {
                            if( bridgeatts[z].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                refboui = bridge.getAttribute( bridgeatts[z].getName() ).getValueObject() == null ? 
                                		null : 
                                		BigDecimal.valueOf(	bridge.getAttribute( bridgeatts[z].getName() ).getValueLong() );
                                if( refboui != null ) {
                                	addToQueue( ctx, refboui , queue , boObjectUpdateQueue.MODE_SAVE_FORCED);
                                }
                            }
                        }
                    }
                }
            }
        }


       if( remrefs != null )
        {
            for (int i = 0; i < remrefs.size(); i++)
            {
                boReferencesManager.referenceHandler refh = (boReferencesManager.referenceHandler)remrefs.get( i );
                long cboui = refh.attributeValue.longValue();

                boObject refobj = boObject.getBoManager().getObjectInContext( obj.getEboContext(), cboui );

                String classname;

                if( refobj != null )
                {
                    classname = refobj.getName();
                    boDefHandler refdef = boDefHandler.getBoDefinition( classname );
                    if (
                        !( refh.attributeDef.getChildIsOrphan() || refdef.getBoCanBeOrphan())
                        &&
                        ( refobj==null || (refobj != null && refobj.exists() ) )
                       )
                    {

                    		// ntohing
                    }
                    else if ( refobj.exists() )
                    {
                         queue.add( cboui, boObjectUpdateQueue.MODE_SAVE_FORCED );
                    }
                }
            }
        }

        xobjqueue = obj.getUpdateQueue().getObjects();

        return queue;
    }
    /*
     * chamado do buildUpdateQueueToSave
     */
    public static final void addToQueue( EboContext ctx, BigDecimal boui, boObjectUpdateQueue queue , byte mode ) throws boRuntimeException
    {
        if( boui != null )
        {


            if ( mode == boObjectUpdateQueue.MODE_SAVE_FORCED )
            {
                boObject refobj = getObjectInContextStatic( ctx, boui.longValue() );
                if( refobj != null )
                {
                    if( refobj.isChanged() || !refobj.exists() )
                    {
                        queue.add( refobj, mode );
                    }
                }
            }
            else
            {
                queue.add( boui.longValue(), mode );
            }
        }
    }


    private boObject getObject(EboContext ctx, String name)
        throws boRuntimeException
    {
        try
        {
            boDefHandler bodef = boDefHandler.getBoDefinition(name);

            if (bodef == null)
            {
                throw new boRuntimeException(this.getClass().getName() +
                    ".getObject(...)", "BO-3019", null, name);
            }

            String version = "v" + bodef.getBoVersion().replace('.', '_');
            name = version + "." + name;

            Class xclass   = Class.forName( name, true, ctx.getApplication().getClassLoader() );

            //Class xclass = boApplicationThread.getActiveClassLoader().loadClass(name);
            //Class xclass = Class.forName(name, true,
            //        Thread.currentThread().getContextClassLoader());

            //            ((ContextClassLoader)Thread.currentThread().getContextClassLoader()).flushCache();
            //
            //            xclass =Class.forName(name,false,Thread.currentThread().getContextClassLoader());

            /*boClassLoader objldr = new boClassLoader(Thread.currentThread().getContextClassLoader());
            Class xclass = objldr.loadClass(name);*/
            /*Class xclass = Thread.currentThread().getContextClassLoader().loadClass(name);
            ContextClassLoader xxxxx = (ContextClassLoader)xclass.getClassLoader();
            xxxxx.clean();
            xxxxx.flushCache();
            xclass = Thread.currentThread().getContextClassLoader().loadClass(name);

            logger.finest(xxxxx);*/

            //            Class xclass = Class.forName();
            boObject retobj = (boObject) xclass.newInstance();

            return retobj;
        }
        catch (ClassNotFoundException e)
        {
            throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
                "BO-2101", e, name);
        }
        catch (IllegalAccessException e)
        {
            throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
                "BO-2101", e, name);
        }
        catch (InstantiationException e)
        {
            throw new boException("netgest.bo.runtime.boObjectLoader.loadObject(String)",
                "BO-2101", e, name);
        }
    }

    public void makeAllObject(EboContext eboctx) throws boRuntimeException
    {
        try
        {
            boBuilder.buildAll(eboctx, new boBuilderOptions(), new boBuilderProgress());
        }
        catch (Exception e)
        {
            throw new boRuntimeException(this.getClass().getName() +
                ".makeAllObject(EboContext ctx)", "", e);
        }
    }

    public void makeAllObject(EboContext eboctx, String schema) throws boRuntimeException
    {
        try
        {
            boBuilder.buildAll(eboctx, new boBuilderOptions(), new boBuilderProgress());
        }
        catch (Exception e)
        {
            throw new boRuntimeException(this.getClass().getName() +
                ".makeAllObject(EboContext ctx)", "", e);
        }
    }


    private boolean my_trans    = false;
    private boolean entry_ejb   = false;

    private static ThreadLocal transactionState = new ThreadLocal() 
    {
        protected synchronized Object initialValue() 
        {
            return Boolean.FALSE;
        }
    };


    private void beginTransaction(EboContext ctx, boObject object)
        throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");

            if ( !((Boolean)transactionState.get()).booleanValue() )
            {
				// Marca este objecto como inicial na transacção.
                transactionState.set( Boolean.TRUE );
                if (object!=null)object.setMyTransaction(true);
                //
                entry_ejb = true;
                
            }

            
            if( !ctx.getConnectionManager().isInOnlyDatabaseTransaction() )
            {
            	int status = ut.getStatus();
            	
              if (status == Status.STATUS_NO_TRANSACTION )
              {
                  ctx.beginTransaction();
                  my_trans = true;
                  ut.begin();
              }
            }
            else
            {
                my_trans = true;                
            }

            // Create a save point in the Object
            if (object != null)
            {
//                object.transactionBegins();

                if (!object.getName().equals("Ebo_TextIndex") &&
                        !object.getName().equals("Ebo_Registry"))
                {
                    ctx.addTransactedObject(object);
                }
            }

            //
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boManagerBean.commitTransaction( EboContext )",
                "BO-3150", e, "commiting");
        }
    }

    private void commitTransaction(EboContext ctx) throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");
            
            
            if (my_trans)
            {
                int status=ut.getStatus();



                if (status == Status.STATUS_MARKED_ROLLBACK)
                {
                    ctx.endTransaction();
                    rollBackTransaction(ctx);
                    throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_COMMIT_A_TRANSACTION_MARKED_FOR_ROLLBACK_"));
                }
                else if (ut.getStatus() == Status.STATUS_ACTIVE)
                {
                    try
                    {
                        boObject[] objects = ctx.getObjectsInTransaction();
                        boTextIndexAgentBussinessLogic.addToQueue(objects);
                        if( !ctx.getConnectionManager().isInOnlyDatabaseTransaction() )
                        {
                            ut.commit();
                        }
                        ctx.endTransaction();
                        for (short i = 0; i < objects.length; i++)
                        {
                            objects[i].onCommit();
                            objects[i].transactionEnds(true);
                        }
                    }
                    catch( Throwable e )
                    {
                        rollBackTransaction(ctx);
                        throw new RuntimeException( e );
                    }
                    boPoolable doc = ctx.getApplication().getMemoryArchive().getPoolManager().getObject( ctx , ctx.getPreferredPoolObjectOwner() );
                    // apagar boThread
                    if ( doc instanceof docHTML )
                    {
                        (( docHTML )doc).getThread().clear();
                    }
                }
                else
                {
                    ctx.endTransaction();
                    rollBackTransaction(ctx);
                }

                my_trans = false;


            }
            else if ( ((Boolean)transactionState.get()).booleanValue() && entry_ejb )  
            {
            	boolean modeBatch = false;
                if( ctx.isInModeBatch() && !ctx.isInModeBatch( EboContext.MODE_BATCH_USE_OBJECT_SAVEPOINT ) ) {
                	modeBatch = true;
                }
                
                boObject[] obj = ctx.getObjectsInTransaction();
                if( modeBatch ) {
                    boTextIndexAgentBussinessLogic.addToQueue(obj);
                	ctx.clearObjectInTransaction();
                }
                
                for (int i = 0; i < obj.length; i++) 
                {
                    if( modeBatch ) {
                    	obj[i].transactionEnds( true );
                    }
                    else {
                        obj[i].set_IsInOnSave( boObject.UPDATESTATUS_WAITING_COMMIT );
                    }
                }
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boManagerBean.commitTransaction( EboContext )",
                "BO-3150", e, "commiting");
        }
        finally
        {
            if ( ((Boolean)transactionState.get()).booleanValue() && entry_ejb )  
            {
                transactionState.set( Boolean.FALSE );
                entry_ejb = false;
            }
            
        }
    }

    private void rollBackTransaction(EboContext ctx) throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");

            if (my_trans)
            {
                try
                {
                    boObject[] objects = ctx.getObjectsInTransaction();
                    ctx.endTransaction();

                    for (short i = 0; i < objects.length; i++)
                    {
                        objects[i].transactionEnds(false);
                        objects[i].onRollBack();
                    }

                    if( !ctx.getConnectionManager().isInOnlyDatabaseTransaction() )
                    {
                        ut.rollback();
                    }
                    my_trans = false;
                }
                catch(Exception e)
                {
                    logger.finer(LoggerMessageLocalizer.getMessage("ROLLBACK_TRANSACTIN_EBO_CONTEXT_CTX"), e);
                }
            }
            else
            {
                try
                {
                    if (ut.getStatus() == Status.STATUS_ACTIVE)
                    {
                        if( !ctx.getConnectionManager().isInOnlyDatabaseTransaction() )
                        {
                            if( ctx.getConnectionManager().getAutoMarkForRollback() )
                            {
                            	ut.setRollbackOnly();
                            }
                        }
                    }








                }
                catch(Exception e)
                {
                    logger.finer(LoggerMessageLocalizer.getMessage("ROLLBACK_TRANSACTIN_EBO_CONTEXT_CTX"), e);
                }
                

                if ( ((Boolean)transactionState.get()).booleanValue() && entry_ejb )  
                {

                    boObject[] obj = ctx.getObjectsInTransaction();
                    for (int i = 0; i < obj.length; i++) 
                    {
                        obj[i].set_IsInOnSave( boObject.UPDATESTATUS_WAITING_COMMIT );
                    }

                    transactionState.set( Boolean.FALSE );
                    entry_ejb = false;
                }
                
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boManagerBean.rollBackTransaction( EboContext )",
                "BO-3150", e, "rolling back");
        }
        finally
        {
            if ( ((Boolean)transactionState.get()).booleanValue() && entry_ejb )  
            {
                transactionState.set( Boolean.FALSE );
                entry_ejb = false;
            }
        }
    }

    private boLoginLocalHome getboLoginLocalHome() throws NamingException
    {
        final InitialContext context = new InitialContext();
        return (boLoginLocalHome)context.lookup("java:comp/env/ejb/boLoginLocal");
    }

    public void registerObjects( EboContext ctx, DataSet dataSet, String string, long[] bouis ) throws boRuntimeException
    {
        return;
    }

    public void registerObjects( EboContext ctx, DataSet dataSet, String string ) throws boRuntimeException
    {
        return;
    }


    public void registerRemoteKey( EboContext ctx, XEODataManagerKey remoteKey ) throws boRuntimeException {
    	cacheBouis.createRemoteBoui( ctx, remoteKey );
	}

	public boObject destroyForced(EboContext ctx, boObject bobj) throws boRuntimeException
    {


        String ebo_registryFullTableName = "";
        String ebo_textIndexFullTableName = "";
        String ebo_referencesFullTableName = "";

        try
        {
            bobj.set_IsInOnSave( boObject.UPDATESTATUS_UPDATING );
            boolean dook = false;
            beginTransaction(ctx, bobj);
            bobj.setEboContext(ctx);

            String schemaName = boRepository.getDefaultSchemaName(ctx.getApplication());
            if(schemaName != null && !"".equals(schemaName))
            {
                ebo_registryFullTableName = schemaName + ".";
                ebo_textIndexFullTableName = schemaName + ".";
            }
            ebo_registryFullTableName += "ebo_registry";
            ebo_textIndexFullTableName += "ebo_textindex";
            ebo_referencesFullTableName  += "ebo_references";

            // ATT - não pode ficar assim
            // boolean saveObjectData = fireMappingEvents( bobj );

            try
            {

                boolean canUpdate = true;

                bobj.setCheckSecurity( false);

                boReferencesManager.referenceHandler[] refs = boReferencesManager.getObjectReferences( ctx , bobj.getBoui(), (byte) 0 );

                ArrayList bouisToDestroy = new ArrayList();
                ArrayList bouisRemoveFromBridges = new ArrayList();


                boDefAttribute[] atts = bobj.getBoDefinition().getBoAttributes();

                //remover bridges do objecto
                for (int i = 0; i < atts.length; i++)
                {
                    if (atts[i].getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                    {
                        String attname = atts[i].getName();

                        if (atts[i].getMaxOccurs() > 1)
                        {
                            bridgeHandler bridge = bobj.getBridge(attname);
                            if (bridge != null)
                            {
                                bridge.first();
                                int bcnt = bridge.getRowCount();
                                for (int z = 0; z < bcnt; z++)
                                {
                                    // a ver  bridge.getLineAttributes().elements();
                                    bouisRemoveFromBridges.add( bridge.getValue() );
                                    bridge.remove();
                                }
                            }
                        }
                    }
                }

                //construir lista de objectos não orfãos para anular
                for (int i = 0; i < refs.length ; i++)
                {
                    if(refs[i].attributeDef != null)
                    {
                        if ( !refs[i].attributeDef.getReferencedObjectDef().getBoCanBeOrphan() )
                        {
                        	boolean found = false;
                        // bouisToDestroy.add( refs[i].object_boui  );
                            bouisToDestroy.add( refs[i].object_refBoui  );
                        }
                    }
                }


                // Verificar onde é que este objecto está referenciado value e attributo de modo a fazer o set null ou remover da bridge
                boReferencesManager.referenceHandler[] refBy = boReferencesManager.getObjectReferences( ctx , bobj.getBoui() , (byte)1 ); //refBy
                ArrayList objsToUpdate = new ArrayList();
                List refByToDelete = new ArrayList();
                boObject o = null;
                for (int i = 0; i < refBy.length ; i++)
                {

                    o = loadObject( ctx , refBy[i].object_boui.longValue() );

                    //AttributeHandler atr = o.getAttribute( refBy[i].attributeName );
                    if(o.exists())
                    {
                        o.getAttribute( refBy[i].attributeDef.getName() ).setValueObject( null );
                        //ainda falta anular bridge element
                        if ( !objsToUpdate.contains(o) )
                        {
                            objsToUpdate.add( o );
                        }
                    }
                    else
                    {
                        refByToDelete.add(refBy[i]);
//                        boReferencesManager.removeReference(ctx,refBy[i]);
                    }
                }

                //update de Objectos relacionados
                for (int i = 0; i < objsToUpdate.size(); i++)
                {
                    ((boObject)objsToUpdate.get(i)).setModeAlter();
                    ((boObject)objsToUpdate.get(i)).update();
                }


                boReferencesManager.removeReferences(bobj);

                for (int i = 0; i < refByToDelete.size(); i++)
                {
                    boReferencesManager.removeReference(ctx,(boReferencesManager.referenceHandler)refByToDelete.get(i));
                }

                // anular referencias do objecto



                //APAGAR EBO_TEXTINDEX
                  PreparedStatement pstm = ctx.getConnectionData()
                                    .prepareStatement("delete from " + ebo_textIndexFullTableName + " where ui$=?");
                  pstm.setLong(1, bobj.getBoui());
                  try
                  {
                      pstm.executeUpdate();
                  }
                  catch (SQLException e)
                  {
                        int errorCode = e.getErrorCode( );
                        logger.finer(LoggerMessageLocalizer.getMessage("ERROR_DELETING_EBO_TEXTINDEX")+" "+e.getMessage());
                        //ignore if error occur in delete Ebo_TextIndex
                  }
                  finally
                  {
                     if( pstm!=null)  pstm.close();
                  }
                  PreparedStatement pstm2 = ctx.getConnectionData()
                                    .prepareStatement("delete from " + ebo_registryFullTableName + " where ui$=?");
                  pstm2.setLong(1, bobj.getBoui());
                  try
                  {
                      pstm2.executeUpdate();
                  }
                  catch (SQLException e)
                  {
                        logger.severe(e);

                        int errorCode = e.getErrorCode( );
                        switch (errorCode)
                        {
                            case 2292:  // Referenced constrainr error code
                                {
                                   throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3023",e);
                                }
                            default:
                                throw new boRuntimeException(bobj,"ObjectDataManager.updateObjectData(boObject)","BO-3055",e);
                        }
                  }
                  finally
                  {
                      if(pstm2!=null) pstm2.close();
                  }

                //apagar referencias


                ObjectDataManager.updateObjectData(bobj);
                 //anular objectos não orfãos
                for (int i = 0; i < bouisToDestroy.size(); i++)
                {
                    boObject norphan = loadObject(ctx, ( (BigDecimal)bouisToDestroy.get(i)).longValue() );
                    norphan.destroyForce();
                }


                /*
                StringBuffer xsql= new StringBuffer("SELECT  FROM " +  ebo_referencesFullTableName + " WHERE REFBOUI$= ? ");
                for (int i = 0; i < oToDestroy.length ; i++)
                {
                         xsql.append(" and boui!= ? ");
                }

                PreparedStatement pstm = ctx.getConnectionData()
                                            .prepareStatement( xsql.toString() );

                // ATENÇÃO : se alguma coisa falhar tem que se fazer o rollback ao objecto ..

                if ( !bobj.getBoDefinition().getBoCanBeOrphan() )
                {

                    Long[] oToDestroy = buildDestroyQueue( ctx, bobj ).getObjectsToRemove();


                    StringBuffer xsql= new StringBuffer("SELECT COUNT(*) FROM " +  ebo_referencesFullTableName + " WHERE REFBOUI$= ? ");

                    for (int i = 0; i < oToDestroy.length ; i++)
                    {
                        xsql.append(" and boui!= ? ");
                    }

                    PreparedStatement pstm = ctx.getConnectionData()
                                                .prepareStatement( xsql.toString() );

                    pstm.setLong(1, currentboui);

                    for (int i = 0; i < oToDestroy.length ; i++)
                    {
                        pstm.setLong(i+2, oToDestroy[i].longValue() );
                    }

                    ResultSet rslt = pstm.executeQuery();
                    rslt.next();
                    long xr=rslt.getLong(1);

                    canUpdate = xr == 0;

                    if( !canUpdate )
                    {
                        logger.warn( "Object "+bobj.getName()+":"+currentboui+" not deleted,still have references"  );


                    }
                    rslt.close();
                    pstm.close();
                }
                */



                    dook = true;


                bobj.p_exists = false;
                return bobj;
            }
            catch (boRuntimeException e)
            {
                if ((e.getSrcObject() != null) &&
                        e.getErrorCode().equals("BO-3023") &&
                        e.getSrcObject().getName().equals("Ebo_Registry"))
                {
                    logger.severe( e.getMessage() );
                    throw new boRuntimeException(loadObject(ctx, bobj.getBoui()),
                        "ObjectDataManager.updateObjectData(boObject)",
                        "BO-3023", e);
                }

                rollBackTransaction(ctx);

                throw (e);
            }
            catch (SQLException e)
            {
                logger.severe(e);
                if ((e.getMessage() != null) &&
                        e.getMessage().startsWith(MessageLocalizer.getMessage("REGISTRY_HAS_BEEN_CHANGED_BY_ANOTHER_USER")))
                {
                    throw new boRuntimeException(boManagerBean.class.getName() +
                        ".destroyForced(EboContext, boObject)", "BO-3022", e,
                        bobj.bo_name + "(" + bobj.bo_boui + ")");
                }
                else if (e.getErrorCode() == 2292)
                {
                    throw new boRuntimeException(boManagerBean.class.getName() +
                        ".destroyForced(EboContext, boObject)", "BO-3023", e,
                        bobj.bo_name + "(" + bobj.bo_boui + ")");
                }

                throw new boRuntimeException(boManagerBean.class.getName() +
                    ".destroyForced(EboContext, boObject)", "BO-3016", e,
                    bobj.bo_name + "(" + bobj.bo_boui + ")");
            }
            catch (Exception e)
            {
                logger.severe(e);
                throw new boRuntimeException(boManagerBean.class.getName() +
                    ".destroyForced(EboContext, boObject)", "BO-3016", e,
                    bobj.bo_name + "(" + bobj.bo_boui + ")");
            }
            finally
            {
                if (dook)
                {


                    commitTransaction(ctx);
                }
                else
                {
                    rollBackTransaction(ctx);

                }
                bobj.set_IsInOnSave( boObject.UPDATESTATUS_IDLE );
            }
        }
        finally
        {
//            if(forceAllInTransaction)
//            {
                ctx.forceAllInTransaction(false);
//            }
        }


    }

   private final boolean fireMappingEvents( boObject object )  throws boRuntimeException
    {
        boolean isOnMappingEvent = object.getParameter("IsInDetectChangesByTrigger") != null
                                    && object.getParameter("IsInDetectChangesByTrigger").equals( "true" );
        MapType1Def mapDef = MapType1Def.getDataSourceDefinition( object.getBoDefinition() );
        boolean ret = true;
        if( mapDef != null && !mapDef.isDefault() && !isOnMappingEvent )
        {
            String className = mapDef.getObjectDataSources().getClassName();
            EboContext ctx = object.getEboContext();
            if( className != null && className.length() > 0 )
            {
                boolean havelocaltable = false;
                String[] keys = mapDef.getObjectDataSources().getDataSources()[0].getKeys();
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

    public boObject lookByPrimaryKey(EboContext eboctx, String objectName, Object[] keys) throws boRuntimeException
    {
        boObject ret = null;
        IDataPlugin[] plugins = DataPluginManager.getPlugIns();
        for (int i = 0; i < plugins.length; i++)
        {
            ret = plugins[i].lookByPrimaryKey( eboctx, objectName, keys );
        }
//        if( ret == null )
//        {
//            if( keys.length == 1 )
//            {
//                if ( keys[0] instanceof Long )
//                {
//                    return loadObject( eboctx, ((Long)keys[1]).longValue() );
//                }
//                else if ( keys[0] instanceof BigDecimal )
//                {
//                    return loadObject( eboctx, ((BigDecimal)keys[1]).longValue() );
//                }
//                else if ( keys[0] instanceof String )
//                {
//                    try
//                    {
//                        long boui = Long.parseLong( keys[0].toString() );
//                        return loadObject( eboctx, boui );
//                    }
//                    catch (NumberFormatException ex)
//                    {
//
//                    }
//
//                }
//            }
//        }
        return ret;
    }

    private void doVersionControl(boObject object) throws boRuntimeException
    {
        if(object.getObjectVersionControl().canCheckIn() && !object.getObjectVersionControl().isDeserialized())
        {
            object.getObjectVersionControl().getObjectFromCheckOut();
            object.setChanged(false);
        }
        else if(object.getObjectVersionControl().canCheckOut() && object.exists())
        {
            object.setDisabled();
        }
        else if(!object.getObjectVersionControl().canCheckIn() && object.exists())
        {
            object.setDisabled();
        }
    }

    public EJBLocalHome getEJBLocalHome() throws EJBException
    {
        return null;
    }

    public Object getPrimaryKey() throws EJBException
    {
        return null;
    }

    public void remove() throws RemoveException, EJBException
    {
    }

    public boolean isIdentical(EJBLocalObject p0) throws EJBException
    {
        return false;
    }
}
