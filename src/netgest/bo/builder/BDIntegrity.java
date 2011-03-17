/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;

import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.bo.system.Logger;

/**
 *
 * @author JMF
 */
public class BDIntegrity
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.builder.BDIntegrity");

    public static final List ATTRIBUTES = Arrays.asList(new String[]{"CREATOR", "PARENT", "PARENTCTX", "TEMPLATE"});
    public static final  List OBJECTS= Arrays.asList(new String[]{"EBO_PERF", "EBO_DOCUMENT"});
    private static final boolean DEBUG = false;

    private long objsToDupl = 0;
    private EboContext ctx;
    private String objName;
    private boolean onlyTest = false;
    private String ebo_registryFullTableName;
    private ArrayList listBoui;
    private ArrayList listClone;

    public BDIntegrity(EboContext ctx, String objName, String listClone, String listBoui, boolean onlyTest)
    {
        this.ctx = ctx;
        this.objName = objName;
        this.onlyTest = onlyTest;

//        depends(42267);
        ebo_registryFullTableName = "";
        String schemaName = ctx.getBoSession().getRepository().getDefaultSchemaName(ctx.getApplication());
        if(schemaName != null && !"".equals(schemaName))
        {
            ebo_registryFullTableName = schemaName + ".";
        }
        ebo_registryFullTableName += "ebo_registry";
        if(listClone != null && !"".equals(listClone))
        {
            this.listClone = new ArrayList();
            StringTokenizer st = new StringTokenizer(listClone, ";");
            while(st.hasMoreElements())
            {
                this.listClone.add(((String)st.nextElement()).trim());
            }
        }
        if(listBoui != null && !"".equals(listBoui))
        {
            this.listBoui = new ArrayList();
            StringTokenizer st = new StringTokenizer(listBoui, ";");
            while(st.hasMoreElements())
            {
                this.listBoui.add(((String)st.nextElement()).trim());
            }
        }
    }


    public int verifyDuplicatedNoOrphans()
    {
        int toRet = 0;
        objsToDupl = 0;
        long auxL;
        boObject auxObj;
        try
        {
            if(listBoui != null && listBoui.size() > 0)
            {
                for (int i = 0; i < listBoui.size(); i++)
                {
                    try
                    {
                        auxL = Long.parseLong((String)listBoui.get(i));
                        auxObj = boObject.getBoManager().loadObject(ctx, auxL);
                        verifierDuplicatedNoOrphans(auxObj);
                    }
                    catch(Exception e)
                    {
                        logger.warn(LoggerMessageLocalizer.getMessage("INVALID_BOUI")+": " + listBoui.get(i));
                    }
                }
            }
            else if(objName != null && !"".equals(objName))
            {
                //logger.finest("-------------------------");
                //logger.finest("Started Verifying : " + objName);
                toRet = verifierDuplicatedNoOrphans();
                //logger.finest("End Cleaning : "  + objName);
                //logger.finest("-------------------------");
            }
            else
            {
                toRet = verifyAll();
            }
            logger.finest(LoggerMessageLocalizer.getMessage("TOTAL_OF_OBJECTS_TO_DUPLICATE")+" : " + objsToDupl);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            toRet = -1;
        }
        logger.finest(LoggerMessageLocalizer.getMessage("FINISHED"));
        return toRet;
    }

    private  int verifierDuplicatedNoOrphans() throws boRuntimeException
    {
        try{
            boolean keepgoing = true;
            boObjectList list = null;
            boObject aux, aux2;
            Enumeration oEnum;
            boAttributesArray boArr;
            AttributeHandler attH;
            boolean it = false;
            for(int i = 0; keepgoing; i++)
            {
                list = boObjectList.list(ctx, "select " + objName + " order by boui", i, 90);
//                list = boObjectList.list(ctx, 156696);

                if(list != null)
                {
                    list.beforeFirst();
                    while (list.next())
                    {
                        try
                        {
                            it = true;
                            aux2 = null;
                            try{
                                aux2 = list.getObject();
                            }
                            catch(Exception _e)
                            {
                                logger.severe("?????????????????????????? "+LoggerMessageLocalizer.getMessage("INVALID_OBJECT")+": " + list.getCurrentBoui());
                            }
                            if(aux2 != null)
                            {
                                logger.finest("*****"+LoggerMessageLocalizer.getMessage("STARTING_VERIFIER_FOR_OBJECT")+": " + aux2.getBoui());
                                verifierDuplicatedNoOrphans(aux2);
                                logger.finest("*****"+LoggerMessageLocalizer.getMessage("ENDING_VERIFIER_FOR_OBJECT")+": " + aux2.getBoui());
                            }
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace();
                        }
                    }
                    if(it)
                    {
                        it = false;
                        keepgoing = true;
                    }
                    else
                    {
                        keepgoing = false;
                    }
                }
                else
                {
                    return -1;
                }
                ctx.releaseAllObjects();
            }
            ctx.getApplication().getMemoryArchive().getPoolManager()
                    .destroyObject(list);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
        }
        return 0;
    }


    private int verifyAll()
    {
        boBuildRepository br = new boBuildRepository(ctx.getBoSession().getRepository());
        File[] f = br.getXMLFiles();
        String boname = null;
        int toRet = 0;
        int total = 0;
        for(int i = 0; i < f.length; i++)
        {
            if(f[i].getName().toUpperCase().endsWith("$BO.XML"))
            {
                boname = f[i].getName().substring(0,
                            f[i].getName().toUpperCase().indexOf("$BO"));
                if(boDefHandler.getBoDefinition(boname).getClassType() !=
                        boDefHandler.TYPE_ABSTRACT_CLASS)
                {
                    //logger.finest("-------------------------");
                    //logger.finest("Started Verifying : " + boname);
                    toRet = verifyDuplicatedNoOrphans();
                    //logger.finest("End Cleaning : "  + boname);
                    //logger.finest("-------------------------");
                    total++;
                }
            }
        }
        //logger.finest("Total : "  + total);
        return toRet;
    }

    private  void verifierDuplicatedNoOrphans(boObject aux) throws boRuntimeException
    {
        boAttributesArray boArr = aux.getAttributes();
        if(boArr != null)
        {
            verifyAttributes(aux, boArr, null, null);
        }
    }

    private void verifyAttributes(boObject parent, boAttributesArray boArr, String bridgeName, String bridgeTableName) throws boRuntimeException
    {
        Enumeration oEnum;
        AttributeHandler attH;
        oEnum = boArr.elements();

        while (oEnum.hasMoreElements())
        {
            attH = (AttributeHandler) oEnum.nextElement();

            if(bridgeName == null || (attH.getName().indexOf(bridgeName + ".LIN") < 0 &&
                                      attH.getName().indexOf(bridgeName + "." + bridgeName) < 0))
            {
                if(attH.isObject() || attH.isBridge())
                {
                    if(attH.getName() == null)
                    {
                        logger.severe("attH.getName() = a null");
                    }
                    if(attH.getName() != null && ATTRIBUTES.indexOf(attH.getName().toUpperCase()) < 0)
                    {
                        if(attH.isBridge())
                        {
                             treatAttribute(parent, attH, bridgeName, bridgeTableName);
                        }
                        else if(attH.getValueObject() != null)
                        {
                            if(!attH.getObject().getBoDefinition().getBoCanBeOrphan())
                            {
                                if (!attH.getObject().getBoDefinition().getBoHaveMultiParent())
                                {
                                    //logger.finest("*****");
                                    //logger.finest("Verifying Attribute: " + attH.getName());
                                    treatAttribute(parent, attH, bridgeName, bridgeTableName);
                                    //logger.finest("*****");
                                }
                                else
                                {
                                    if(DEBUG)
                                        logger.finest(LoggerMessageLocalizer.getMessage("ATTRIBUTE")+": " + attH.getName() + " (multiparent)");
                                }
                            }
                            else
                            {
                                if(DEBUG)
                                    logger.finest(LoggerMessageLocalizer.getMessage("ATTRIBUTE")+": " + attH.getName() + " (orphan)");
                            }
                        }
                        else
                        {
                            if(DEBUG)
                                logger.finest(LoggerMessageLocalizer.getMessage("ATTRIBUTE")+": " + attH.getName() + " (null value)");
                        }
                    }
                }
                else
                {
                    if(DEBUG)
                        logger.finest(LoggerMessageLocalizer.getMessage("ATTRIBUTE")+": " + attH.getName() + " (not to treat)");
                }
            }
            else
            {
                //ignore
            }
        }
    }

/*
    private  void verifyObject(boObject parent, AttributeHandler attH,
        boObject aux, String bridgeTableName, String attName) throws boRuntimeException
    {
        Connection con = null;
        PreparedStatement ps = null;
        boObject newObject;
        boObject[] objs;
        String auxTableName;
        String update1 = null;

        try
        {
            if(OBJECTS.indexOf(aux.getName().toUpperCase()) < 0)
            {
                if(referencedBy(aux) > 1)
                {
                    objsToDupl++;
                    if(onlyTest)
                    {
                        logger.finest("Object should be duplicated");
                    }
                    else
                    {
                        logger.finest("Going to duplicate object: " + aux.getBoui() + " of type: " + aux.getName());
                        ctx.beginContainerTransaction();
                        con = ctx.getConnectionData();
                        newObject = boObject.getBoManager().createObject(ctx, aux);
                        //logger.finest("Finished duplication new object identification: " + newObject.getBoui() + " of type: " + newObject.getName());
                        newObject.getDataSet().setForInsert();
                        ObjectDataManager.updateObjectData(newObject);

                        if(bridgeTableName != null)
                        {
                            auxTableName = bridgeTableName;
                        }
                        else
                        {
                            auxTableName = parent.getBoDefinition().getBoPhisicalMasterTable();
                        }

                        logger.finest("Setting parent: " + parent.getBoui() + " table: " + auxTableName + " field " + attH.getDefAttribute().getDbName() + "attribute.");

                        ps = con.prepareStatement("insert into " +
                                ebo_registryFullTableName +
                                "   (sys_user, sys_org, sys_icn, sys_app, sys_dtcreate, sys_dtsave, ui$, ui_version, name, clsid, clsid_major_version, clsid_minor_version, boui, classname) " +
                                "   values " +
                                "  ( ? , ?, 1 , ? , sysdate ,  sysdate , ?, ?, ?, ?, ? , ?, ?, ?) ");
                        ps.setString(1, ctx.getSysUser().getUserName());
                        ps.setString(2, ctx.getBoSession().getRepository().getName());
                        ps.setString(3, ctx.getApplication().getName());
                        ps.setLong(4, newObject.getBoui());
                        ps.setLong(5, 1);
                        ps.setString(6, newObject.getName());
                        ps.setString(7, newObject.getName());
                        ps.setLong(8, newObject.bo_major_version);
                        ps.setLong(9, newObject.bo_minor_version);
                        ps.setLong(10, newObject.getBoui());
                        ps.setString(11, "Ebo_Registry");
                        ps.executeUpdate();
                        ps.close();

                        update1 = "Update " + auxTableName + " set " + attName + " = ? where parent$ = ? and " + attName + " = ?";
                        ps = con.prepareStatement(update1);

                        ps.setLong(1, newObject.getBoui());
                        ps.setLong(2, parent.getBoui());
                        ps.setLong(3, aux.getBoui());
                        ps.executeUpdate();
                        ps.close();

                        ctx.commitContainerTransaction();
                        //logger.finest("End setting parent attribute.");
                        //logger.finest("Start Verifying new object: " + newObject.getBoui());
                        verifierDuplicatedNoOrphans(newObject);
                        //logger.finest("End Verifying new object.");
                    }
                }
                else
                {
                    if(DEBUG)
                        logger.finest("Object/Attribute is correct.");
                }
            }
            else
            {
                if(DEBUG)
                    logger.finest("Object not to treat.");
            }
        }
        catch(Exception e)
        {
            if(con != null)
            {
                try
                {
                    con.rollback();
                }
                catch (Exception _e)
                {

                }
            }
            if(ctx != null)
            {
                try
                {
                    ctx.rollbackContainerTransaction();
                }
                catch (Exception _e)
                {

                }
            }
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
            if(ps != null)
            {
                try
                {
                    ps.close();
                }
                catch (Exception e)
                {

                }
            }
            if(con != null)
            {
                try
                {
                    con.close();
                }
                catch (Exception e)
                {

                }
            }
        }
    }
*/

    private  void verifyObject(boObject parent, bridgeHandler bh, AttributeHandler attH,
        boObject aux, String bridgeTableName, String attName) throws boRuntimeException
    {
        boObject newObject;
        try
        {
            if(OBJECTS.indexOf(aux.getName().toUpperCase()) < 0)
            {
                if(referencedBy(aux) > 1)
                {
                    if(onlyTest)
                    {
                        if(isInListClone(aux.getName()))
                        {
                            objsToDupl++;
                            logger.finest(LoggerMessageLocalizer.getMessage("OBJECT_SHOULD_BE_DUPLICATED"));
                        }
                    }
                    else
                    {
                        try
                        {
                            if(isInListClone(aux.getName()))
                            {
                                objsToDupl++;
                                logger.finest(LoggerMessageLocalizer.getMessage("GOING_TO_DUPLICATE_OBJECT")+": " + aux.getBoui() + " "+LoggerMessageLocalizer.getMessage("OF_TYPE")+": " + aux.getName());
                                ctx.beginContainerTransaction();
                                newObject = boObject.getBoManager().createObject(ctx, aux);
                                //logger.finest("Finished duplication new object identification: " + newObject.getBoui() + " of type: " + newObject.getName());
                                if(bh != null)
                                {
                                    String s = bh.getName();
                                    bh.getAttribute(s).setValueLong(newObject.getBoui());
                                    bh.poolSetStateFull();
                                }
                                else
                                {
                                    attH.setValueLong(newObject.getBoui());
                                }
                                parent.getUpdateQueue().add(newObject, boObjectUpdateQueue.MODE_SAVE);

                                parent.update(false, true);
                                ctx.commitContainerTransaction();
                                //logger.finest("End setting parent attribute.");
                                //logger.finest("Start Verifying new object: " + newObject.getBoui());
                                verifierDuplicatedNoOrphans(newObject);
                                //logger.finest("End Verifying new object.");
                            }
                        }catch(Exception e)
                        {
                            logger.severe(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+": ", e);
                        }
                    }
                }
                else
                {
                    if(DEBUG)
                        logger.finest(LoggerMessageLocalizer.getMessage("OBJECT_ATTRIBUTE_IS_CORRECT"));
                }
            }
            else
            {
                if(DEBUG)
                    logger.finest(LoggerMessageLocalizer.getMessage("OBJECT_NOT_TO_TREAT"));
            }
        }
        catch(boRuntimeException _e)
        {
            throw _e;
        }
        catch(Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
        }
    }
    private  void treatAttribute(boObject parent, AttributeHandler attH,
        String bridgeName, String bridgeTableName) throws boRuntimeException
    {
        try{
            boObject[] objs;
            String auxTableName;

            if(!attH.isBridge())
            {
                boObject aux = attH.getObject();
                if(!aux.getBoDefinition().getBoCanBeOrphan())
                {
                    if (!aux.getBoDefinition().getBoHaveMultiParent())
                    {
                        verifyObject(parent, null, attH, aux, bridgeTableName, attH.getDefAttribute().getDbName());
                    }
                }
            }
            else
            {
//                logger.finest("Preparing to verify bridge: " + attH.getName());
                bridgeHandler bh = parent.getBridge(attH.getName());
                //int sv = bh.getRow();
                bh.beforeFirst();
                boObject lineObject;
                while(bh.next())
                {
                    lineObject = null;
                    if(bh.getAllAttributes() != null)
                    {
//                        logger.finest("Started verifying bridge Attributes.");
                        verifyAttributes(parent, bh.getAllAttributes(), attH.getName(), bh.getDefAttribute().getBridge().getBoPhisicalMasterTable());
//                        logger.finest("Ended verifying bridge Attributes.");
                    }
                    else
                    {
                        if(DEBUG)
                            logger.finest(LoggerMessageLocalizer.getMessage("BRIDGE_DOESNT_HAVE_ATTRIBUTES"));
                    }
                    try
                    {
                        lineObject = bh.getObject();
                    }
                    catch(Exception r)
                    {
                        if(r != null)
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR")+": " + r.getMessage());
                        }
                        //ignore
                    }
                    if(lineObject != null)
                    {
                        if(!lineObject.getBoDefinition().getBoCanBeOrphan())
                        {
                            if (!lineObject.getBoDefinition().getBoHaveMultiParent())
                            {
                                if(DEBUG)
                                    logger.finest(LoggerMessageLocalizer.getMessage("STARTED_VERIFYING_BRIDGE_VALUEOBJECT"));
                                verifyObject(parent,bh, attH, lineObject, bh.getDefAttribute().getBridge().getBoPhisicalMasterTable(), "child$");
                                if(DEBUG)
                                    logger.finest(LoggerMessageLocalizer.getMessage("ENDED_VERIFYING_BRIDGE_VALUEOBJECT"));
                            }
                            else
                            {
                                if(DEBUG)
                                    logger.finest(LoggerMessageLocalizer.getMessage("OBJECT")+": " + lineObject.getBoui() + " "+LoggerMessageLocalizer.getMessage("OF_TYPE")+": " + lineObject.getBoDefinition().getName() + " (multiparent)");
                            }
                        }
                        else
                        {
                            if(DEBUG)
                                logger.finest(LoggerMessageLocalizer.getMessage("OBJECT")+": " + lineObject.getBoui() + " "+LoggerMessageLocalizer.getMessage("OF_TYPE")+": " + lineObject.getBoDefinition().getName() + " (orphan)");
                        }

                    }
                }
//                logger.finest("Ended verifying bridge.");
            }
        }
        catch(boRuntimeException _e)
        {
            throw _e;
        }
        catch(Exception e)
        {
            throw new boRuntimeException("", e.getMessage(), null);
        }
        finally
        {
            //ignorar
        }

    }

    private final static int referencedBy(boObject obj) throws boRuntimeException
    {
        int toRet = 0;
        boObject[] objs = null;
        try{
            objs = obj.getReferencedByObjects();

            boObject aux;
            boObject[] parents;
            long pBoui;
            boolean isParent;

            if(objs != null)
            {
                if(objs.length <= 1)
                {
                    return objs.length;
                }
                for (int i = 0; i < objs.length; i++)
                {
                    aux = objs[i];
                    if(aux.getBoDefinition().getBoHaveMultiParent())
                    {
                        parents = aux.getParents();
                        if(parents != null)
                        {
                            isParent = false;
                            for (int j = 0; j < parents.length; j++)
                            {
                                if(parents[j].getBoui() == obj.getBoui())
                                {
                                    isParent = true;
                                }
                            }
                            if(!isParent)
                            {
                                toRet++;
                            }
                        }
                        else
                        {
                            toRet++;
                        }
                    }
                    else
                    {
                        pBoui = objs[i].getParent() == null ? -1:objs[i].getParent().getBoui();
                        if(pBoui != obj.getBoui())
                        {
                            toRet++;
                        }
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return 0;
        }
        return toRet;
    }

    private boolean isInListClone(String name)
    {
        if(listClone != null && listClone.size() > 0)
        {
            for (int i = 0; i < listClone.size(); i++)
            {
                if(((String)listClone.get(i)).equalsIgnoreCase(name))
                {
                    return true;
                }
            }
            return false;
        }
        return true;
    }
}