package netgest.bo.runtime.robots.ejbtimers;

import java.rmi.RemoteException;

import javax.ejb.EJBObject;
import javax.ejb.Timer;

public interface xeoEJBTimer
    extends EJBObject
{

    public void start(String name)
        throws RemoteException;

    public void suspend(String name)
        throws RemoteException;

    public void scheduleNextRun(Timer timer)
        throws RemoteException;		
    
    public boolean isActive(String name)
    	throws RemoteException;	
    
    public boolean isRunning(String name)
    	throws RemoteException;		
    
}
