/*Enconding=UTF-8*/
package netgest.bo.runtime;

import java.util.Iterator;
import java.util.Map;
import netgest.bo.system.boPoolOwner;
import netgest.bo.system.boPoolable;
import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author  João Paulo Trindade Carreira
 */
public abstract class boObjectContainer extends boPoolable
{

    private static Logger logger = Logger.getLogger( boObjectContainer.class );
    
    public static boolean LOGGER_ENABLED = false;
    boolean logged = false;

    public boObjectContainer(EboContext ctx)
    {
        super(ctx);
    }

    public boObject getObject(long boui) throws boRuntimeException
    {
        boObject ret = boObject.getBoManager().loadObject(getEboContext(), boui);

        return ret;
    }

    public boThread getThread()
    {
        boThread ret;
        String owner = getEboContext().getPreferredPoolObjectOwner();

        if (!owner.equals(getEboContext().poolUniqueId()))
        {
            boPoolOwner poolOwner = (boPoolOwner)getEboContext().getApplication()
                                     .getMemoryArchive().getPoolManager()
                                     .getObjectById(owner);
        
            if( poolOwner != null )
            {
                ret = poolOwner.getThread();
            }
            else
            {
                if( LOGGER_ENABLED )
                {
                    if( !logged )
                    {
                        String url  = "";
                        String host = "";
                        StringBuffer params = new StringBuffer();
                        try
                        {
                            if( getEboContext().getRequest() != null )
                            {
                                Map par = getEboContext().getRequest().getParameterMap();
                                url = getEboContext().getRequest().getRequestURL().toString();
                                host= getEboContext().getRequest().getRemoteHost();
                                Iterator it = par.keySet().iterator();
                                while(it.hasNext()) 
                                {
                                    String parName = it.next().toString();
                                    params.append( '&' );
                                    params.append( parName ).append('=').append( par.get( parName ) );
                                }
                            }
                        }
                        catch (Exception e)
                        {
                        } 
                        logger.warn("User:["+getEboContext().getSysUser().getUserName()+"]:["+host+"]:O poolowner deste objecto já não foi encontrado ["+owner+"] Url:["+url+"] Params:["+params.toString()+"]");
                        logged = true;
                    }
                }
                ret = getEboContext().getThread();
            }
        }
        else
        {
            ret = getEboContext().getThread();
        }

        return ret;
    }
}
