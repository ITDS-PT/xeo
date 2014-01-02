package netgest.bo.runtime.robots.ejbtimers;

import java.rmi.RemoteException;
import javax.ejb.EJBObject;

import netgest.bo.runtime.boObject;


public interface boScheduleThreadEJB
    extends EJBObject
{

    public void addSchedule(boObject boobject)
        throws RemoteException;

    public void start(String name)
        throws RemoteException;		

    public void suspend()
        throws RemoteException;			
}
