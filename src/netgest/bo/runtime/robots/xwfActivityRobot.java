/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.dochtml.docHTML;
import netgest.bo.message.MessageServer;
import netgest.bo.system.boApplication;
import netgest.bo.runtime.robots.blogic.xwfActivityRobotBussinessLogic;
/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class xwfActivityRobot extends Thread 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boApplication p_boapp;
    public xwfActivityRobot(ThreadGroup tGroup, boApplication boapp, String name )
    {
        super( tGroup, name );
        p_boapp = boapp;
        
    }
    
    public void run()
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
            xwfActivityRobotBussinessLogic logic = new xwfActivityRobotBussinessLogic(p_boapp);
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
    
    
}