package netgest.bo.runtime.robots.ejbtimers;

import javax.ejb.*;
import netgest.bo.system.boApplication;

import netgest.bo.system.Logger;


public class XEOTimedObject
{

    private String p_name=null;
    private boolean p_init=false;
    private long p_interval=0;
    private boolean p_isRunning = false;
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.ejbtimers.XEOTimedObject");


    public XEOTimedObject(String name) {
        p_name=name;
    }
    
    public String getName() {
        return p_name;
    }

    public void setName(String name) {
        p_name=name;
    }

    
    public void setInterval(long interval) {
        p_interval=interval;
    }
    
    public long getInterval()
    {   
        int index=0;
        if (p_interval==0)
        {
           p_interval=10000; //Default Value
            String threads_name[]=getXEOApplication().getApplicationConfig().getThreadsName();
            for (int i=0;i<threads_name.length;i++) {
                if (threads_name[i].equals(p_name)) {
                    index=i;
                    break;
                }
                
            }
            String threadInterval=getXEOApplication().getApplicationConfig().getThreadsInterval()[index];
            if (threadInterval!=null && !threadInterval.equals("")) p_interval=new Long(threadInterval).longValue();            
        }
        return p_interval;
    }

    public boApplication getXEOApplication()
    {
        return boApplication.getApplicationFromStaticContext("XEO");
    }


    public boolean isActive()
    {
        return p_init;
    }

    public boolean isRunning()
    {
        return p_isRunning;
    }

    public void setIsRunning(boolean isRunning)
    {
        p_isRunning=isRunning;
    }

    public void setIsActive(boolean isActive)
    {
        p_init=isActive;
    }

}
