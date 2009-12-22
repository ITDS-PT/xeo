/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.system.boApplication;
import netgest.bo.runtime.robots.blogic.xwfTimeAgentBussinessLogic;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class xwfTimeAgent extends Thread 
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.runtime.robots.xwfTimeAgent");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boApplication p_boapp;
    public xwfTimeAgent(ThreadGroup tGroup, boApplication boapp, String name )
    {
        super( tGroup, name );
        p_boapp = boapp;
        
    }
    
    public void run()
    {
        try
        {
            try
            {
                Thread.sleep( 6000 );
            }
            catch (InterruptedException e)
            {
            }
          
            while( !this.isInterrupted() )
            {
                xwfTimeAgentBussinessLogic logic = new xwfTimeAgentBussinessLogic(p_boapp);
                logic.execute();
                try
                {
                    Thread.sleep( 30000 );
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