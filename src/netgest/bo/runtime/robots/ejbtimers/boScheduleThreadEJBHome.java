package netgest.bo.runtime.robots.ejbtimers;

import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface boScheduleThreadEJBHome
    extends EJBHome
{

    public boScheduleThreadEJB create()
        throws RemoteException, CreateException;   
}
