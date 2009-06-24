package netgest.bo.runtime.robots.ejbtimers;

import javax.ejb.EJBLocalObject;
import netgest.bo.system.boApplication;

public interface xeoEJBTimerLocal
    extends EJBLocalObject
{

    public void start(String name);

    public void suspend(String name);
}
