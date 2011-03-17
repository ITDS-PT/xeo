package netgest.bo.runtime.robots.ejbtimers.impl;

import javax.ejb.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.rmi.PortableRemoteObject;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.robots.blogic.boScheduleAgentBussinessLogic;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boSession;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;

import netgest.bo.runtime.robots.ejbtimers.XEOTimedObject;
import netgest.bo.runtime.robots.ejbtimers.XEOTimedObjectWrapper;
import netgest.bo.runtime.robots.ejbtimers.boScheduleThreadEJBHome;
import netgest.bo.runtime.robots.ejbtimers.boScheduleThreadEJBLocalHome;
import netgest.bo.system.boLoginLocalHome;


import netgest.bo.system.Logger;

public class boScheduleAgentBean extends XEOTimedObjectWrapper
    implements SessionBean, TimedObject
{
    private static String p_name=null;
    private static boolean p_init=false;
    private static long p_interval=0;
    private static boolean p_isRunning = false;
    private SessionContext _context;
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.ejbtimers.boScheduleAgent");
    
    public void ejbCreate()
    {
    }

    public void setSessionContext(SessionContext context)
        throws EJBException
    {
        _context = context;
    }

    public void ejbRemove()
        throws EJBException
    {
    }

    public void ejbActivate()
        throws EJBException
    {
    }

    public void ejbPassivate()
        throws EJBException
    {
    }
    
    
    public void start(String name) {
        super.start(name,_context);
    }

    public void suspend(String name) {
        super.suspend(name,_context);
    }

    public void scheduleNextRun(Timer timer) {
        super.scheduleNextRun(timer,_context);
    }    
    
    public void ejbTimeout(Timer timer)
    {
        XEOTimedObject timedObject=null;
        try
        {
            timedObject = super.getXEOTimedObject((String)timer.getInfo());
            if (timedObject!=null)
            {
                if(timedObject.isActive() && !timedObject.isRunning())
                {
                    timedObject.setIsRunning(true);
                    boScheduleAgentBussinessLogic logic = new boScheduleAgentBussinessLogic(timedObject.getXEOApplication(), null);
                    logic.execute();
                    scheduleNextRun(timer);
                    timedObject.setIsRunning(false);
                }
            }
            else logger.warn(LoggerMessageLocalizer.getMessage("XEOTIMEDOBJECT_NOT_FOUND"));
        }
        catch(Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("UNEXPECTED_ERROR_EXECUTING_EJBTIMER")+" - "+(String)timer.getInfo());
            scheduleNextRun(timer);
            if (timedObject!=null)timedObject.setIsRunning(false);
        }
    }

    private boLoginLocalHome getboLoginLocalHome() throws NamingException {
        final InitialContext context = new InitialContext();
        return (boLoginLocalHome) context.lookup( "java:comp/env/ejb/local/boLogin" );
    }

    private boScheduleThreadEJBHome getboScheduleThreadEJBHome() throws NamingException {
        final InitialContext context = new InitialContext();
        return (boScheduleThreadEJBHome) PortableRemoteObject.narrow( context.lookup( "java:comp/env/ejb/boScheduleThread" ), boScheduleThreadEJBHome.class );
    }

    private boScheduleThreadEJBLocalHome getboScheduleThreadEJBLocalHome() throws NamingException {
        final InitialContext context = new InitialContext();
        return (boScheduleThreadEJBLocalHome) context.lookup( "java:comp/env/ejb/boScheduleThreadLocal" );
    }
    
    private void resetEboSchedules() {
        EboContext ctx = null;
        boSession session = null;
        try 
        {            
            boApplication p_boapp = boApplication.getApplicationFromStaticContext("XEO");
            session =  p_boapp.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_boapp.getDefaultRepositoryName() );
            ctx = session.createRequestContext(null,null,null);
            boObjectList scheds = boObjectList.list(ctx,"select Ebo_Schedule where activeStatus=1");            
            scheds.beforeFirst();
            while( scheds.next() )
            {
                boObject object = scheds.getObject();
                object.getAttribute("activeStatus").setValueString("0");
                object.update();
                ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( ctx.poolUniqueId() ); 
            } 
        }
        catch (boLoginException e) {
            logger.severe(e.getMessage());
        }
        catch (boRuntimeException e) {
            logger.severe(e.getMessage());
        }        
        finally
        {  
            ctx.close();
            session.closeSession();
        }        
    }
}
