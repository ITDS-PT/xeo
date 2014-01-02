/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;

import netgest.bo.system.boApplication;

import netgest.bo.runtime.robots.blogic.boQueueAgentBussinessLogic;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boQueueAgent extends Thread 
{

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boApplication p_boapp;
    private boolean       p_runNow;  
    public boQueueAgent(ThreadGroup tGroup, boApplication boapp, String name )
    {
        super( tGroup, name );
        p_boapp = boapp;
        
    }
    
    public void run()
    {
        try
        {            
            while( !this.isInterrupted() )
            {
                boQueueAgentBussinessLogic logic = new boQueueAgentBussinessLogic(p_boapp);
                logic.execute();
                try
                {
                    int cnt = 0;
                    while( !this.p_runNow && cnt < 15 )  // Ciclo de 15 vezes com uma espera de 2000ms
                    {
                        cnt++;
                        Thread.sleep( 1000 );
                    }
                    this.p_runNow = false;
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
    
    public void runNow()
    {
        this.p_runNow = true;
    }
    
}