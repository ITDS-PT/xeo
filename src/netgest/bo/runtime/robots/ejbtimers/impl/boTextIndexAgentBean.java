package netgest.bo.runtime.robots.ejbtimers.impl;

import java.util.Iterator;

import javax.ejb.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;

import netgest.bo.runtime.robots.ejbtimers.XEOTimedObjectWrapper;
import netgest.bo.runtime.robots.ejbtimers.XEOTimedObject;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginLocalHome;

import netgest.bo.system.Logger;

public class boTextIndexAgentBean extends XEOTimedObjectWrapper
implements SessionBean, TimedObject
{

    private SessionContext _context;
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.ejbtimers.boTextIndexAgent");
    
    
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
                    boTextIndexAgentBussinessLogic logic = new boTextIndexAgentBussinessLogic(timedObject.getXEOApplication());
                    logic.execute();
                    scheduleNextRun(timer);
                    timedObject.setIsRunning(false);
                }
            }
            else logger.warn("XEOTimedObject Not Found");
        }
        catch(Exception e)
        {
            logger.warn("Unexpected Error executing EJBTimer - "+(String)timer.getInfo());
            scheduleNextRun(timer);
            if (timedObject!=null)timedObject.setIsRunning(false);
        }
    }

    private boLoginLocalHome getboLoginLocalHome() throws NamingException {
        final InitialContext context = new InitialContext();
        return (boLoginLocalHome) context.lookup( "java:comp/env/ejb/local/boLogin" );
    }
}
