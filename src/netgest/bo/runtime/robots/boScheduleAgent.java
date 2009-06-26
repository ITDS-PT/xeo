/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.boLoginException;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.runtime.robots.blogic.boScheduleAgentBussinessLogic;
import netgest.bo.system.boLoginBean;

import netgest.bo.system.boSession;

import org.apache.log4j.Logger;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boScheduleAgent extends Thread 
{    
    /**
     * 
     * @since 
     */
    
    private boApplication p_app;
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.boScheduleAgent");
    
    public boScheduleAgent(ThreadGroup group, boApplication app, String name )
    {
        super( group , "XEO Schedule Agent Thread" );
        p_app  = app;
    }

    public void run()
    {  
        EboContext ctx = null;
        boSession session = null;
        try 
        {
        	if( boDefHandler.getBoDefinition( "iXEOUser" ) != null ) {
	            session =  p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
	            ctx = session.createRequestContext(null,null,null);
	            boObjectList scheds = boObjectList.list(ctx,"SELECT Ebo_Schedule WHERE ACTIVESTATUS=1");
	            scheds.beforeFirst();
	            while( scheds.next() )
	            {
	                boObject object = scheds.getObject();
	                object.getAttribute("activeStatus").setValueString("0");
	                object.update();
	                ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( ctx.poolUniqueId() ); 
	            }
	          while( !super.isInterrupted() )
	          {
	              boScheduleAgentBussinessLogic logic = new boScheduleAgentBussinessLogic(p_app,getThreadGroup());
	              logic.execute();
	              for (int i = 0; i < 3; i++) 
	              {
	                  try
	                  {
	                      sleep(10000);
	                  }
	                  catch( InterruptedException e )
	                  {
	                      super.interrupt();
	                  }
	              }
	          }
        	}
        }
        catch (boLoginException e) {        
            logger.error(e.getMessage());
        }
        catch (boRuntimeException e) {
            logger.error(e.getMessage());
        }
        finally
        {  
        	if( ctx != null )
        		ctx.close();
        	if( session != null )
        		session.closeSession();
        }
        
    }
    
    public synchronized void start()
    {
        // TODO:  Override this java.lang.Thread method
        super.start();
    }
    
}