/*Enconding=UTF-8*/
package netgest.bo.events;
import java.util.Iterator;
import netgest.bo.runtime.*;
import netgest.bo.utils.*;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class MailAccountEvents 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.events.MailAccountEvents");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private MailAccountEvents()
    {
    }
    
    public static boolean inactiveMailAccounts(boObject accountParent, String attName) throws boRuntimeException 
    {
        //boolean result = false;
        EboContext boctx2 = null;
        try
        {
            AttributeHandler att = accountParent.getAttribute(attName);
            boctx2 = accountParent.getEboContext().getBoSession().createRequestContext(null, null, null);
            boObject objDataBase = boObject.getBoManager().loadObject(boctx2,accountParent.getBoui());
            boObject objToSet = null;
            if(att.isBridge())
            {
                DifferenceContainer dc =  DifferenceHelper.showDifferences(objDataBase,accountParent);
                if(dc != null)
                {
                    DifferenceElement diffElem = null;
                    Iterator objects = dc.getBridgeSrcDiffIterator();
                    if(objects != null)
                    {
                        while (objects.hasNext()) 
                        {
                            diffElem = (DifferenceElement)objects.next();
                            if(attName.equals(diffElem.getBridgeName()))
                            {
                                objToSet = accountParent.getObject(diffElem.getBoui());
                                objToSet.getAttribute("active").setValueString("0");
                            }
                        }
                    }
                }
            }
            else
            {
                if(objDataBase != null)
                {
                    if(objDataBase.getAttribute(attName).getValueObject() != null &&
                       objDataBase.getAttribute(attName).getValueLong() !=  accountParent.getAttribute(attName).getValueLong()
                    )
                    {
                        objToSet = accountParent.getObject(objDataBase.getAttribute(attName).getValueLong());
                        objToSet.getAttribute("active").setValueString("0");
                    }
                }
            }
            //result = true;
        }
        catch(Exception e)
        {
            logger.severe("EMAILACCOUNT ERROR ", e);
        }
        finally
        {
            if(boctx2 != null)
            {
                boctx2.close();
            }
        }
        return true;
    }
}