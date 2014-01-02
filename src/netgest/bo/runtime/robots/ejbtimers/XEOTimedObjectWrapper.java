package netgest.bo.runtime.robots.ejbtimers;

import java.util.Hashtable;
import java.util.Iterator;

import javax.ejb.SessionContext;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public abstract class XEOTimedObjectWrapper {
    
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.ejbtimers.XEOTimedObjectWrapper");
    
    private static Hashtable xeoTimedObjects = new Hashtable();
    
    public void scheduleNextRun(Timer timer,SessionContext context)
    {
        XEOTimedObject timedObject =this.getXEOTimedObject((String)timer.getInfo());
        if (timedObject!=null)
        {
            TimerService timerService = context.getTimerService();
            timer = timerService.createTimer(timedObject.getInterval(), timedObject.getName());
            logger.finer("["+timedObject.getName()+"]  "+LoggerMessageLocalizer.getMessage("SCHEDULE_NEXT_RUN_AT")+" ["+timer.getNextTimeout()+"] "+LoggerMessageLocalizer.getMessage("WITH_TIMEOUT")+" ["+timedObject.getInterval()+"]");
        }
        else logger.warn(LoggerMessageLocalizer.getMessage("XEOTIMEDOBJECT_NOT_FOUND"));
    }

    
    public XEOTimedObject getXEOTimedObject(String name) {
        return (XEOTimedObject)xeoTimedObjects.get(name);
    }

    public void addXEOTimedObject(XEOTimedObject timedObject) {
        xeoTimedObjects.put(timedObject.getName(),timedObject);
    }
    


    public void start(String name,SessionContext context)
    {
        //Cancel Persisted Timers Before creating a new one
        XEOTimedObject timedObject=new XEOTimedObject(name);
        if (timedObject!=null)
        {
            this.suspendForce(name,context);           
            
            if(!timedObject.isActive())
            {
                TimerService timerService = context.getTimerService();
                Timer timer = timerService.createTimer(timedObject.getInterval(), timedObject.getName());
                timedObject.setIsActive(true);
            }
            addXEOTimedObject(timedObject);
        }
        else logger.warn(LoggerMessageLocalizer.getMessage("XEOTIMEDOBJECT_NOT_FOUND"));
          
    }

    public void suspend(String name,SessionContext context)
    {
        XEOTimedObject timedObject =this.getXEOTimedObject(name);
        if (timedObject!=null)
        {
            if(timedObject.isActive())
            {
                Timer currTimer;
                Iterator it = context.getTimerService().getTimers().iterator();
                while (it.hasNext())
                {
                    currTimer = (Timer)it.next();
                    currTimer.cancel();
                }
    
                timedObject.setIsActive(false);
            }
        }
        else logger.warn(LoggerMessageLocalizer.getMessage("XEOTIMEDOBJECT_NOT_FOUND"));
        this.suspendForce(name,context);
    }

    public void suspendForce(String name,SessionContext context)
    {
        Timer currTimer;
        Iterator it = context.getTimerService().getTimers().iterator();
        while (it.hasNext())
        {
            currTimer = (Timer)it.next();
            currTimer.cancel();
        }    
    } 
    
    public boolean isActive(String name) {
        return this.getXEOTimedObject(name).isActive();
    }

    public boolean isRunning(String name) {
    	 return this.getXEOTimedObject(name).isRunning();
    }
    
}
