package netgest.bo.runtime.robots.ejbtimers;

import javax.ejb.EJBLocalObject;

public interface xeoEJBTimerLocal
    extends EJBLocalObject
{

    public void start(String name);

    public void suspend(String name);
    
    public boolean isActive(String name);

    public boolean isRunning(String name);
    
}
