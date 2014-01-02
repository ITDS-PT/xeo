/*Enconding=UTF-8*/
package netgest.bo.parser.util;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import netgest.bo.def.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class Utils 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.parser.util.Utils");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private Utils()
    {
    }
    
    public static String contructAttrFromObj(String path, boDefHandler boDef, String objName) throws boRuntimeException
    {
        return objName + "." + contructAttrFromObj(path.split("\\."), boDef, 0);
    }
    private static String contructAttrFromObj(String[] path, boDefHandler boDef, int pos) throws boRuntimeException
    {
        String element = path[pos];
        boDefAttribute[] attDef =  boDef.getAttributesDef();
        for (int i = 0; i < attDef.length; i++) 
        {
            if(element.equals(attDef[i].getName()))
            {
                if(path.length == pos + 1)
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE &&
                        attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_N)
                    {
                        return "getBridge(\"" + attDef[i].getName() +"\")";
                    }
                    else
                    {
                        return "getAttribute(\"" + attDef[i].getName() +"\")";
                    }
                }
                else
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE &&
                        attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_N)
                    {
                        throw new boRuntimeException("",MessageLocalizer.getMessage("CANNOT_HAVE_A_BRIDGE_AS_MIDDLE_ATTRIBUTE"), null);
                    }
                    else
                    {
                        return "getAttribute(\"" + attDef[i].getName() +"\")" + ".getObject()." + contructAttrFromObj(path ,attDef[i].getReferencedObjectDef(), pos + 1);
                    }
                }
            }
        }
        throw new boRuntimeException("", MessageLocalizer.getMessage("INVALID_ATTRIBUTE")+" - " + joinStr(path)+ " - " + boDef.getName() , null);
    }
    private static String joinStr(String[] path)
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < path.length; i++) 
        {
            sb.append(path[i]);
            if((i+1) < path.length)
            {
                sb.append(".");    
            }
        }
        return sb.toString();
    }
    public static boolean isBridgeAttrFromObj(String path, boDefHandler boDef) throws boRuntimeException
    {
        return isBridgeAttrFromObj(path.split("\\."), boDef, 0);
    }
    private static boolean isBridgeAttrFromObj(String[] path, boDefHandler boDef, int pos) throws boRuntimeException
    {
        String element = path[pos];
        boDefAttribute[] attDef =  boDef.getAttributesDef();
        for (int i = 0; i < attDef.length; i++) 
        {
            if(element.equals(attDef[i].getName()))
            {
                if(path.length == pos + 1)
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE &&
                        attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_N)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE &&
                        attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_N)
                    {
                        throw new boRuntimeException(MessageLocalizer.getMessage("CANNOT_HAVE_A_BRIDGE_AS_MIDDLE_ATTRIBUTE"), null, null);
                    }
                    else
                    {
                        return isBridgeAttrFromObj(path, attDef[i].getReferencedObjectDef(), pos + 1);
                    }
                }
            }
        }
        throw new boRuntimeException("", MessageLocalizer.getMessage("INVALID_ATTRIBUTE")+" - " + joinStr(path) + " - " + boDef.getName() , null);
    }
    public static boolean isObjectAttrFromObj(String path, boDefHandler boDef) throws boRuntimeException
    {
        return isObjectAttrFromObj(path.split("\\."), boDef, 0);
    }
    private static boolean isObjectAttrFromObj(String[] path, boDefHandler boDef, int pos) throws boRuntimeException
    {
        String element = path[pos];
        boDefAttribute[] attDef =  boDef.getAttributesDef();
        for (int i = 0; i < attDef.length; i++) 
        {
            if(element.equals(attDef[i].getName()))
            {
                if(path.length == pos + 1)
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE)
                    {
                        return true;
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    if(attDef[i].getAtributeType() == attDef[i].TYPE_OBJECTATTRIBUTE &&
                        attDef[i].getRelationType() == boDefAttribute.RELATION_1_TO_N)
                    {
                        throw new boRuntimeException(MessageLocalizer.getMessage("CANNOT_HAVE_A_BRIDGE_AS_MIDDLE_ATTRIBUTE"), null, null);
                    }
                    else
                    {
                        return isObjectAttrFromObj(path, attDef[i].getReferencedObjectDef(), pos + 1);
                    }
                }
            }
        }
        throw new boRuntimeException("", MessageLocalizer.getMessage("INVALID_ATTRIBUTE")+" - " + joinStr(path)+ " - " + boDef.getName() , null);
    }
    
    public static void copyAttribute(AttributeHandler fromAtt, AttributeHandler toAtt) throws boRuntimeException
    {
        if(fromAtt.getDefAttribute().getAtributeType() == fromAtt.getDefAttribute().TYPE_OBJECTATTRIBUTE)
        {
            if(fromAtt.getValueObject() != null)
            {
                boDefHandler fromObjDef = fromAtt.getDefAttribute().getReferencedObjectDef();
                boDefHandler toObjDef = toAtt.getDefAttribute().getReferencedObjectDef();
                if(fromObjDef.getName().equals(toObjDef.getName()))
                {
                    if(toObjDef.getBoCanBeOrphan() || toObjDef.getBoHaveMultiParent())
                    {
                        toAtt.setValueObject(fromAtt.getValueObject(), fromAtt.getInputType());
                    }
                    else
                    {
                        boObject newOb = boObject.getBoManager().createObject(fromAtt.getEboContext(), fromAtt.getObject());
                        toAtt.setValueObject(newOb, fromAtt.getInputType());
                    }
                }
                else
                {
                    boDefForwardObject[] fwds = fromObjDef.getForwardObjects();
                    for (int i = 0; i < fwds.length; i++) 
                    {
                        if(toObjDef.getName().equals(fwds[i].toBoObject()))
                        {
                            boObject fromObj = fromAtt.getObject();
                            
                            try
                            {
                                Method ometh = fromObj.getClass().getMethod(fwds[i].getMapMethodName(), new Class[]{Long.class});
                                toAtt.setObject((boObject)ometh.invoke(fromObj, new Object[]{new Long(fromObj.getBoui())}), AttributeHandler.INPUT_FROM_INTERNAL);
                            }
                            catch (NoSuchMethodException e)
                            {
                                logger.severe(e);
                            }
                            catch (InvocationTargetException _e)
                            {
                                logger.severe(_e);
                            }
                            catch (IllegalAccessException __e)
                            {
                                logger.severe(__e);
                            }
                        }
                    }
                }
            }
        }
        else
        {
            toAtt.setValueObject(fromAtt.getValueObject(), fromAtt.getInputType());
        }
    }
    
    public static void copyBridge(bridgeHandler fromBridge, bridgeHandler toBridge) throws boRuntimeException
    {
        //faltam os atributos da bridge
        boBridgeIterator fromIT = fromBridge.iterator();
        boDefHandler toObjDef = toBridge.getDefAttribute().getReferencedObjectDef();
        boDefHandler fromObjDef;
        if(!"boObject".equals(toObjDef.getName()))
        {            
            boObject fromObj;
            while(fromIT.next())
            {
                fromObj = fromIT.currentRow().getObject();
                fromObjDef = fromObj.getBoDefinition();
                if(toObjDef.getName().equals(fromObjDef.getName()))
                {
                    if(toObjDef.getBoCanBeOrphan() || toObjDef.getBoHaveMultiParent())
                    {
                        if(!toBridge.haveBoui(fromObj.getBoui()))
                        {
                            toBridge.add(fromObj.getBoui());
                        }
                    }
                    else
                    {
                        boObject newOb = boObject.getBoManager().createObject(fromObj.getEboContext(), fromObj);
                        toBridge.add(newOb.getBoui());
                    }
                }
                else
                {
                    boDefForwardObject[] fwds = fromObjDef.getForwardObjects();
                    for (int i = 0; fwds != null && i < fwds.length; i++) 
                    {
                        if(toObjDef.getName().equals(fwds[i].toBoObject()))
                        {
//                            Class[] classtype = new Class[1];
//                            try
//                            {
//                                Class c = Class.forName("Long");
//                                classtype[0]= c;
//                            }
//                            catch (ClassNotFoundException e)
//                            {
//                                //ignore
//                                logger.severe(e);
//                            }
                            try
                            {
                                Method ometh = fromObj.getClass().getMethod(fwds[i].getMapMethodName(), new Class[0] );
                                //Method ometh = fromObj.getClass().getMethod(fwds[i].getMapMethodName(), classtype);
                                //toBridge.add(((boObject)ometh.invoke(fromObj, new Object[]{new Long(fromObj.getBoui())})).getBoui());
                                toBridge.add(((boObject)ometh.invoke(fromObj, (Object[])null )).getBoui());
                            }
                            catch (NoSuchMethodException e)
                            {
                                logger.severe(e);
                            }
                            catch (InvocationTargetException _e)
                            {
                                logger.severe(_e);
                            }
                            catch (IllegalAccessException __e)
                            {
                                logger.severe(__e);
                            }
                        }
                    }
                }
            }
        }
    }
}