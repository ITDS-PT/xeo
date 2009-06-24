package netgest.bo.runtime.robots.ejbtimers.impl;

import java.util.Enumeration;

import javax.ejb.*;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Hashtable;
import java.util.Iterator;

import javax.rmi.PortableRemoteObject;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.robots.blogic.boScheduleThreadBussinessLogic;

import netgest.bo.runtime.robots.ejbtimers.boScheduleThreadHelper;

import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginHome;
import netgest.bo.system.boLoginLocalHome;

import org.apache.log4j.Logger;

public class boScheduleThreadBean 
    implements SessionBean, TimedObject
{

    private SessionContext _context;
    private static Hashtable  p_schedule = null;

    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.ejbtimers.boScheduleThread");
    
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
    
    public void start(String name)
    {    
        TimerService timerService = _context.getTimerService();
        Timer timer = timerService.createTimer(1000, name);
    }
 
    public void suspend()
    {
        Timer currTimer;
        Iterator it = _context.getTimerService().getTimers().iterator();
        while (it.hasNext())
        {
            currTimer = (Timer)it.next();
            currTimer.cancel();
        }
    } 
    
    public void ejbTimeout(Timer timer)
    {
        boScheduleThreadHelper currSchedule =null;
        try
        {            
 
                Enumeration schedulesToRunQueue=p_schedule.elements();
                
                while(schedulesToRunQueue.hasMoreElements()) {
                    currSchedule=(boScheduleThreadHelper)schedulesToRunQueue.nextElement();
                    if (!currSchedule.isLocked())
                    {
                        currSchedule.setLocked(true);                    
                        break;
                    }
                }
                
           boScheduleThreadBussinessLogic logic = 
            new boScheduleThreadBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"), (String)timer.getInfo());
            logic.setSchedule(currSchedule.getSchedule());
            logic.execute();
            timer.cancel();
        }
        catch(Exception e)
        {
            logger.warn("Unexpected Error executing EJBTimer - "+(String)timer.getInfo());
            timer.cancel();
        }
        finally {
            if (currSchedule!=null)
                p_schedule.remove(new Long(currSchedule.getSchedule().getBoui()));
        }
    }

    public void addSchedule(boObject schedule)
    {
        if (p_schedule==null) p_schedule = new Hashtable();
        boScheduleThreadHelper scheduleToRun = new boScheduleThreadHelper();
        scheduleToRun.setLocked(false);
        scheduleToRun.setSchedule(schedule);
        p_schedule.put(new Long(schedule.getBoui()),scheduleToRun);
    }

    private boLoginLocalHome getboLoginLocalHome() throws NamingException {
        final InitialContext context = new InitialContext();
        return (boLoginLocalHome) context.lookup( "java:comp/env/ejb/local/boLogin" );
    }
}
