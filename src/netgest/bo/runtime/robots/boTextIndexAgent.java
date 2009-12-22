/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;

import netgest.bo.system.boApplication;
import netgest.bo.system.Logger;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boTextIndexAgent extends Thread 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.boTextIndexAgent");

    /**
     * 
     * @since 
     */
    private boApplication p_app;
    
    public static String ThreadID = String.valueOf( System.currentTimeMillis() );

    public boTextIndexAgent(ThreadGroup group, boApplication app, String name  )
    {
        super( group, "XEO TextIndex QUEUE " + ThreadID );
        p_app = app;
    }
    
/*    public static void addToQueue( EboContext ctx, ArrayList objects, String className )
    {
        if( !Thread.currentThread().getName().endsWith( ThreadID ) )
            queue.addItens( ctx, objects, className );
    }
    
    public static void addToQueue( boObject[] objects )  throws boRuntimeException
    {
        if( !Thread.currentThread().getName().endsWith( ThreadID ) )
            queue.addItens( objects );
    }
   */
    
    private static long WAIT_TIME = 15000;
    
  /*  public static final long setThreadSleep( long timems )
    {
        long ret = WAIT_TIME;
        WAIT_TIME = timems;
        return ret;
    }
    */

    public static long totalWorkTime=0;

    public void run()
    {
        try
        {
            while( !super.isInterrupted() )
            {
                boTextIndexAgentBussinessLogic logic = new boTextIndexAgentBussinessLogic(p_app);
                long workTime=logic.execute();
                               
                totalWorkTime += workTime;
                if( workTime < WAIT_TIME )
                {
                    long sleeptime = (WAIT_TIME - workTime)/2;
                    for (int i = 0; i < 2; i++) 
                    {
                            sleep( sleeptime );
                    }
                }
            }
        }
        catch (InterruptedException e)
        {
            //logger.severe("Error Rebuilding Template TextIndex \n" + e.getMessage(), e);
            // Thread was iterruped... terminate normaly
            // logger.finer( " Exiting boTextIndex Thread, thread was interrupted!!!" );
        }
        catch (Throwable e)
        {
            logger.severe( "Error building TextIndex \n"+e.getMessage(), e );
        }
        finally
        {                  
        }
    }
}