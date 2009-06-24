/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import javax.naming.InitialContext;
import netgest.bo.def.*;
import javax.naming.NamingException;
import netgest.bo.data.DataException;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginHome;
import netgest.bo.system.boSession;
import netgest.xwf.core.*;
import org.apache.log4j.Logger;

import netgest.xwf.common.xwfBoManager;
import netgest.xwf.core.xwfControlFlow;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class xwfDefActivAgent extends Thread 
{
    public static final int SLEEP_TIME_IN_MILLS = 30000; //30 segundos
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.runtime.robots.xwfDefActivAgent");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private boApplication p_app;
    public xwfDefActivAgent(ThreadGroup tGroup, boApplication app, String name)
    {
        super( tGroup, "XEO Schedule Agent for Def Activity Thread" );
        p_app = app;
        
    }
    
    public void run()
    {
        logger.debug("Starting XEO Schedule Agent for Def Activity Thread.....");
        InitialContext ic = null;
        boSession session = null;
        try 
        {
//            sleep(30000);
            boDefHandler perfdef = boDefHandler.getBoDefinition("iXEOUser");
            boDefHandler scheddef = boDefHandler.getBoDefinition("xwfDefActivity");
            if( !( perfdef == null || scheddef==null ))
            {
            
                ic = new InitialContext();
                boLogin login = ((boLoginHome)ic.lookup("boLogin")).create();
//                boSession session =  login.boLogin( p_app, "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
                session =  p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
                EboContext ctx = session.createRequestContext(null,null,null);
                try 
                {
                    boObjectList scheds = boObjectList.list(ctx,"SELECT xwfDefActivity WHERE ACTIVESTATUS=1");
                    scheds.beforeFirst();
                    while( scheds.next() )
                    {
                        boObject object = scheds.getObject();
                        object.getStateAttribute("activeStatus").setValue("0");
                        object.update();
                        ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( ctx.poolUniqueId() );
                    }
                    ctx.close();
                    
                    while( !super.isInterrupted() )
                    {
                        ctx = session.createRequestContext(null,null,null);
                        try
                        {
                            boObjectList list = boObjectList.list(ctx,"SELECT xwfDefActivity WHERE ACTIVESTATUS=0 AND STATE=1 AND (NEXTRUNTIME<=sysdate OR (NEXTRUNTIME IS NULL AND LASTRUNTIME IS NULL))");
                            list.beforeFirst();
                            boObject def = null;
                            while(list.next()) 
                            {                                
                                try {
                                    def = list.getObject();
                                    def.getStateAttribute("activeStatus").setValue("1");
                                    def.update();
                                    xwfDefActivity defactivThread = new xwfDefActivity( getThreadGroup(), def, p_app, SLEEP_TIME_IN_MILLS );                        
                                    defactivThread.start();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        finally
                        {
                            ctx.close();
                        } 
                        for (int i = 0; i < 3; i++) 
                        {
                            try
                            {
                                sleep(SLEEP_TIME_IN_MILLS/3);
                                ic.lookup("boLogin");  // Test if OC4J is up and running;
                            }
                            catch( InterruptedException e )
                            {
                                super.interrupt();
                            }
                            catch( NamingException e )
                            {
                                logger.error( "OC4J Shutdown or XEO not deployed.  [boLogin] JNDI name not found" );
                                super.interrupt();
                                break;
                            }
                        }
                    }
                } 
                finally
                {
                    if( ctx != null )
                    {
                        ctx.close();
                        session.closeSession();
                    }
                }
            }
            else
            {
                logger.debug("Stopping agent, iXEOUser or Ebo_Schedule not deployed.");
            }
        }
        catch ( DataException e )
        {
            logger.debug("Error reading schedule table.\n"+e.getMessage());
        }
        catch ( Exception e )
        {
            logger.debug("Schedule agent finished with errors ");
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if(session != null)
                {
                    session.closeSession();
                }
            }
            catch (Exception e)
            {
                
            }
            try
            {
                if(ic != null)
                {
                    ic.close();
                }
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    
}