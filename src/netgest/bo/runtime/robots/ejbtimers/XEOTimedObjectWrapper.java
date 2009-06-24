package netgest.bo.runtime.robots.ejbtimers;

import java.util.Hashtable;
import java.util.Iterator;

import javax.ejb.SessionContext;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.log4j.Logger;

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
            logger.info("["+timedObject.getName()+"]  Schedule next run at ["+timer.getNextTimeout()+"] with timeout ["+timedObject.getInterval()+"]");
        }
        else logger.warn("XEOTimedObject Not Found!!");
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
        else logger.warn("XEOTimedObject Not Found!!");
          
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
        else logger.warn("XEOTimedObject Not Found!!");
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
}
