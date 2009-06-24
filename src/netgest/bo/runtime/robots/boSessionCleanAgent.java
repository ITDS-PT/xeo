/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import java.util.Date;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import org.apache.log4j.Logger;
import netgest.bo.runtime.robots.blogic.boSessionCleanAgentBussinessLogic;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boSessionCleanAgent extends Thread 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.boSessionCleanAgent");

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boApplication p_boapp;
    
    public boSessionCleanAgent(ThreadGroup tGroup, boApplication boapp, String name )
    {
        super( tGroup, name );
        p_boapp = boapp;
        
    }
    
    public void run()
    {
        boSession session = null;
        try
        {
            try
            {
                Thread.sleep( 12000 );
            }
            catch (InterruptedException e)
            {
            }            
            while( !this.isInterrupted() )
            {
                boSessionCleanAgentBussinessLogic logic = new boSessionCleanAgentBussinessLogic(this.p_boapp);
                logic.execute();
                try
                {
                    Thread.sleep( 600000 );
                }
                catch (InterruptedException e)
                {
                    break;
                }
            }
        }
        finally
        {
        }
    }

}