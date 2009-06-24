package netgest.bo.runtime.robots.ejbtimers;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface xeoEJBTimerHome
    extends EJBHome
{

    public xeoEJBTimer create()
        throws RemoteException, CreateException;   
}
