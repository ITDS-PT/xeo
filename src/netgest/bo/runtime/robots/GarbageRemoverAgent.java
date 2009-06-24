/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;

import netgest.bo.system.boApplication;
import netgest.bo.runtime.robots.blogic.GarbageRemoverAgentBussinessLogic;
/**
 * Classe responsável pela remoção de objectos de negócio em background. 
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public class GarbageRemoverAgent  extends Thread
{
    //logger
    private boApplication p_app = null;    
    private static long WAIT_TIME = 5000000;
    public static long totalWorkTime = 0;
    
    /**
     * Constroi um agente responável pela remoção de objectos de negócio que já não são necessários.     
     * @param group 
     * @param app 
     * @param name 
     */
    public GarbageRemoverAgent(ThreadGroup group, boApplication app, String name)
    {
        super(group, "XEO GarbageRemoverAgent");
        p_app = app;
    }
    public void run()
    {
      try
      {
        while( !this.isInterrupted() )
        {
            GarbageRemoverAgentBussinessLogic logic= new GarbageRemoverAgentBussinessLogic(p_app);
            logic.execute();            
            sleep( WAIT_TIME );
        }
      }
      catch( InterruptedException e )
      {
          super.interrupt();
      }
    }   
}