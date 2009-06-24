/*Enconding=UTF-8*/
package netgest.bo.utils;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import org.apache.log4j.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class AliasUtils 
{

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.utils.AliasUtils");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public AliasUtils()
    {
    }
    public static void setAlias(boObject obj)
    {
        EboContext boctx = obj.getEboContext();
        boObjectList list = boObjectList.list(obj.getEboContext(),
                        "select Ebo_Alias where [ui$] = " + obj.getBoui() +
                        " and upper(alias) = '" + obj.getBoui() + "'",
                        1, 1);
        list.beforeFirst();
        if(!list.next())
        {
            try
            {
                boObject auxAlias = boObject.getBoManager().createObject(obj.getEboContext(),
                                "Ebo_Alias");
                auxAlias.getAttribute("ui").setValueLong(obj.getBoui());
                auxAlias.getAttribute("alias").setValueString(String.valueOf(obj.getBoui()));
                auxAlias.update();
            }
            catch (boRuntimeException e)
            {
                e.printStackTrace();
            }
        }
    }
    public static void setPerfAlias(boObject obj)
    {
        try
        {
            EboContext boctx = obj.getEboContext();
            String perfID = obj.getAttribute("id").getValueString();
            if(perfID != null && perfID.startsWith("ICP"))
            {
                String alias = perfID.substring(3);
                boObjectList list = boObjectList.list(obj.getEboContext(),
                            "select Ebo_Alias where [ui$] = " + obj.getBoui() +
                            " and upper(alias) = '" + alias + "'",
                            1, 1);
                list.beforeFirst();
                if(!list.next())
                {
                    try
                    {
                        boObject auxAlias = boObject.getBoManager().createObject(obj.getEboContext(),
                                        "Ebo_Alias");
                        auxAlias.getAttribute("ui").setValueLong(obj.getBoui());
                        auxAlias.getAttribute("alias").setValueString(alias);
                        auxAlias.update();
                    }
                    catch (boRuntimeException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            setAlias(obj);
        }
        catch (boRuntimeException e)
        {
            logger.error(e);
        }
    }
}