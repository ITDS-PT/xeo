/*Enconding=UTF-8*/
package netgest.utils;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class XEOUserUtils 
{
    private static final boDefInterface XEOUSER_INTF = (boDefInterface)boDefHandler.getBoDefinition("iXEOUser");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private XEOUserUtils()
    {
    }
    
    public static boolean isXEOUser(EboContext boctx, long boui)
    {
        try
        {
            boObject obj = boObject.getBoManager().loadObject(boctx, boui);
            return isXEOUser(obj);
        }
        catch(boRuntimeException e)
        {
            //ignore
        }
        return false;
    }
    
    public static String[] getXEOUserObjectsNames()
    {
        return XEOUSER_INTF.getImplObjects();
    }
    
    public static boolean isXEOUser(boObject obj)
    {
        String[] implObjs = XEOUSER_INTF.getImplObjects();
        if(implObjs != null)
        {
            for (int i = 0; i < implObjs.length; i++) 
            {
                if(implObjs[i].equals(obj.getName()))
                {
                    return true;
                }
            }
        }
        
        return false;
        
        
    }
}