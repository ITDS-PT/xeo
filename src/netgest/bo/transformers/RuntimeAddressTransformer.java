/*Enconding=UTF-8*/
package netgest.bo.transformers;

import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

import netgest.utils.*;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class RuntimeAddressTransformer implements Transformer
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    public RuntimeAddressTransformer()
    {
    }

    public String transformsTo()
    {
        return "runtimeAddress";
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

    public long transform(EboContext ctx, boObject msg, long bouiOfObj)
        throws boRuntimeException
    {
        boObject obj = boObject.getBoManager().loadObject(ctx, bouiOfObj);
        boObject objp = obj.getParent();
        if (transformsTo().equals(obj.getName()))
        {
            return bouiOfObj;
        }

        boObject toRet = boObject.getBoManager().createObject(ctx, "runtimeAddress");
        toRet.getAttribute("name").setValueObject(
            ClassUtils.capitalize(treatName(obj.getAttribute("name").getValueString())));
        toRet.getAttribute("lastname").setValueObject(
            ClassUtils.capitalize(treatLastName(obj.getAttribute("lastname").getValueString())));
        toRet.getAttribute("email").setValueObject(obj.getAttribute("email").getValueObject());
        toRet.getAttribute("telemovel").setValueObject(obj.getAttribute("telemovel").getValueObject());
        toRet.getAttribute("fax").setValueObject(obj.getAttribute("fax").getValueObject());

        boObject correspAdd = obj.getAttribute("correspondencia_address").getObject();
        if (correspAdd != null)
        {
            toRet.getAttribute("rua").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("rua").getValueString()));
            toRet.getAttribute("localidade").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("localidade").getValueString()));
            toRet.getAttribute("cpostal").setValueObject(correspAdd.getAttribute("cpostal").getValueObject());
            toRet.getAttribute("localcpostal").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("localcpostal").getValueString()));
            toRet.getAttribute("country").setValueObject(
                ClassUtils.capitalize(correspAdd.getAttribute("country").getValueString()));

            if (correspAdd.getAttribute("email").getValueString().length() > 0)
            {
                toRet.getAttribute("email").setValueObject(correspAdd.getAttribute("email").getValueObject());
            }
            if (correspAdd.getAttribute("telemovel").getValueString().length() > 0)
            {
                toRet.getAttribute("telemovel").setValueObject(correspAdd.getAttribute("telemovel").getValueObject());
            }
            if (correspAdd.getAttribute("fax").getValueString().length() > 0)
            {
                toRet.getAttribute("fax").setValueObject(correspAdd.getAttribute("fax").getValueObject());
            }
        }

        toRet.getAttribute("refObj").setValueLong(bouiOfObj);
        return toRet.getBoui();
    }
}
