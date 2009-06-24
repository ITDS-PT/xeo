/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;

import netgest.bo.system.boApplication;
import netgest.bo.runtime.robots.blogic.TemplateTextIndexAgentBussinessLogic;

/**
 * 
 * @Company Enlace3
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class TemplateTextIndexAgent  extends Thread
{
    //logger
    private boApplication p_app;
    public static long totalWorkTime=0;
    private static long WAIT_TIME = 100000;

    public TemplateTextIndexAgent(ThreadGroup group, boApplication app, String name)
    {
        super(group, "XEO TemplateTextIndexAgent Rebuilder");
        p_app = app;
    }

    public void run()
    {    
        while( !this.isInterrupted() )
        {
            TemplateTextIndexAgentBussinessLogic logic = new TemplateTextIndexAgentBussinessLogic(p_app);
            logic.execute();
            try
            {
                sleep( WAIT_TIME );
            }
            catch( InterruptedException e )
            {
                super.interrupt();
            }
        }
    }    
}
