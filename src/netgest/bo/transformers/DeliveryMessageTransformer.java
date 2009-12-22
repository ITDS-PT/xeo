/*Enconding=UTF-8*/
package netgest.bo.transformers;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.utils.ClassUtils;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class DeliveryMessageTransformer implements Transformer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.transformers.DeliveryMessageTransformer");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public DeliveryMessageTransformer()
    {
    }
    
    private long transformForAncEntity(EboContext ctx, boObject entity) throws boRuntimeException
    {
        boObjectList list = boObjectList.list(ctx, "select BCM_Contact where [entity$] = " + entity.getBoui());
        list.beforeFirst();
        if(list.next())
        {
            boObject bcmAddress = list.getObject();
            boObject toRet = boObject.getBoManager().createObject(ctx, "deliveryMessage");
            //tratamento do nome e apelido
            String name = bcmAddress.getAttribute("contactName").getValueString();
            String lastName = null;
            if (name != null && !"".equals(name))
            {
                lastName = treatLastName(name);
                name = treatName(name);
            }
            
            toRet.getAttribute("ac").setValueObject(
                ClassUtils.capitalize(name), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("name").setValueObject(
                ClassUtils.capitalize(entity.getAttribute("name").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("lastname").setValueObject(
                ClassUtils.capitalize(lastName), AttributeHandler.INPUT_FROM_INTERNAL);
    
            
            toRet.getAttribute("rua").setValueObject(
                    ClassUtils.capitalize(bcmAddress.getAttribute("street").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("cpostal").setValueObject(bcmAddress.getAttribute("postalCode").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("localcpostal").setValueObject(bcmAddress.getAttribute("postalLocal").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("localidade").setValueObject(
                    ClassUtils.capitalize(bcmAddress.getAttribute("local").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("media").setValueObject("Letter", AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("refObj").setValueLong(bcmAddress.getBoui(), AttributeHandler.INPUT_FROM_INTERNAL);
            return toRet.getBoui();
        }
        return -1;
    }
    
     public long transform(EboContext ctx, boObject msg, long bouiOfObj)
        throws boRuntimeException
    {
        boObject obj = boObject.getBoManager().loadObject(ctx, bouiOfObj);
        if("ANC_entity".equals(obj.getName()))
        {
            long toRet = transformForAncEntity(ctx, obj);
            if(toRet != -1) return toRet;
        }
        if("BCM_Contact".equals(obj.getName()))
        {
            long toRet = transformForAncEntity(ctx, obj.getAttribute("entity").getObject());
            if(toRet != -1) return toRet;
        }
        
        if (transformsTo().equals(obj.getName()))
        {
            return bouiOfObj;
        }

        boObject toRet = boObject.getBoManager().createObject(ctx, "deliveryMessage");
        //tratamento do nome e apelido
        String name = obj.getAttribute("name").getValueString();
        String lastName = obj.getAttribute("lastname").getValueString();
        if(lastName != null && !"".equals(lastName))
        {
            if(name != null && !"".equals(name))
            {
                if(name.endsWith(lastName) && !name.trim().equals(lastName))
                {
                    name = name.substring(0, name.indexOf(lastName));
                }
                name = treatName(name);
            }
        }
        else
        {
            if (name != null && !"".equals(name))
            {
                lastName = treatLastName(name);
                name = treatName(name);
            }
        }
        
        toRet.getAttribute("name").setValueObject(
            ClassUtils.capitalize(name), AttributeHandler.INPUT_FROM_INTERNAL);
        toRet.getAttribute("lastname").setValueObject(
            ClassUtils.capitalize(lastName), AttributeHandler.INPUT_FROM_INTERNAL);
        toRet.getAttribute("email").setValueObject(obj.getAttribute("email").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
        toRet.getAttribute("telemovel").setValueObject(obj.getAttribute("telemovel").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
        toRet.getAttribute("fax").setValueObject(obj.getAttribute("fax").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);


        if (obj.getAttribute("correspondencia_address") != null && obj.getAttribute("correspondencia_address").getObject() != null)
        {
            boObject correspAdd = obj.getAttribute("correspondencia_address").getObject();
            toRet.getAttribute("rua").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("rua").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("localidade").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("localidade").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            toRet.getAttribute("cpostal").setValueObject(correspAdd.getAttribute("cpostal").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            if(correspAdd.getAttribute("localcpostal").getValueString() != null &&
                correspAdd.getAttribute("localcpostal").getValueString().length() > 0)
            {
                toRet.getAttribute("localcpostal").setValueObject(
                    ClassUtils.capitalize(correspAdd.getAttribute("localcpostal").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            }
            else
            {
                toRet.getAttribute("localcpostal").setValueObject(
                    ClassUtils.capitalize(correspAdd.getAttribute("localidade").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);
            }
            toRet.getAttribute("country").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("country").getValueString()), AttributeHandler.INPUT_FROM_INTERNAL);

            if (correspAdd.getAttribute("email").getValueString().length() > 0)
            {
                toRet.getAttribute("email").setValueObject(correspAdd.getAttribute("email").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
            if (correspAdd.getAttribute("telefone").getValueString().length() > 0)
            {
                toRet.getAttribute("telemovel").setValueObject(correspAdd.getAttribute("telefone").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
            if (correspAdd.getAttribute("fax").getValueString().length() > 0)
            {
                toRet.getAttribute("fax").setValueObject(correspAdd.getAttribute("fax").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
        }

        if(obj.getAttribute("preferedMedia").getValueObject() != null && !"".equals(obj.getAttribute("preferedMedia").getValueObject()))
        {
            toRet.getAttribute("media").setValueObject(obj.getAttribute("preferedMedia").getValueObject(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        toRet.getAttribute("refObj").setValueLong(bouiOfObj, AttributeHandler.INPUT_FROM_INTERNAL);
        return toRet.getBoui();
    }
    public String transformsTo()
    {
        return "deliveryMessage";
    }
    private static String treatName(String name)
    {
        String toRet = name;
        try
        {
            if(name != null && name.trim().length()>0)
            {
                if(name.indexOf(",") != -1)
                {
                    toRet = name.substring(name.indexOf(",") + 1).trim();
                    if(toRet == null || toRet.trim().length() == 0)
                    {
                        toRet = name;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return name;
        }
        return toRet;
    }

    private static String treatLastName(String name)
    {
        String toRet = null;
        try
        {
            if(name != null && name.trim().length()>0)
            {
                if(name.indexOf(",") != -1)
                {
                    toRet = name.substring(0, name.indexOf(",")).trim();
                    if(toRet != null && (toRet.trim().length() == 0 || toRet.split(" ").length > 1 || toRet.length() > 49))
                    {
                        toRet = null;
                    }
                }
            }
        }
        catch (Exception e)
        {
            return null;
        }
        return toRet;
    }
}