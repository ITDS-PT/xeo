/*Enconding=UTF-8*/
package netgest.bo.system;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boCompilerClassLoader extends ClassLoader 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boCompilerClassLoader( ClassLoader parent, boApplication app )
    {
        super( parent );
    }
    
    public static final boolean checkForInterface( Class objclass, Class ointerface )
    {
        boolean ret = false;
        Class[] xinter = objclass.getInterfaces();
        for (int i = 0; i < xinter.length; i++) 
        {
            if(  xinter[i].getName().equals( ointerface.getName() ) )
            {
                ret = true;
                break;
            }
        }
        return ret;        
    }
    public static final boolean checkForClassExtends( Class xclass, Class xextends )
    {
        while( xclass != null )
        {
            if( xclass.getName().equals(xextends.getName()) )
            {
                return true;
            }
            xclass = xclass.getSuperclass();
        }
        return false;
    }
    
    public static final Object getInstanceFromClassName( EboContext ctx, String classname, Class xinterface, Class xextends )
    {
        Object retObj = null;
        try
        {
            boCompilerClassLoader classLoader = ctx.getApplication().getCompilingClassLoader();
            Class events = Class.forName(classname);
//            Class events = classLoader.loadClass( classname );
            if( xinterface == null || classLoader.checkForInterface( events, xinterface ) )
            {
                if( xextends == null || classLoader.checkForClassExtends( events, xextends ) )
                {
                    try
                    {
                       retObj = events.newInstance();
                    }
                    catch (InstantiationException e)
                    {
                        throw new boRuntimeException2( e );
                    }
                    catch (IllegalAccessException e)
                    {
                        throw new boRuntimeException2( e );
                    }
                }
                else
                {
                    throw new boRuntimeException2("Class ["+classname+"] must extends Class ["+xextends.getName()+"]");
                }
            }
            else
            {
                throw new boRuntimeException2("Class ["+classname+"] must implement interface ["+xinterface.getName()+"]");
            }
        }
        catch (ClassNotFoundException e)
        {
            throw new boRuntimeException2( e );
        }
        return retObj;
    }
    
    
}