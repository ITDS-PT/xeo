/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface boClientRemoteHome extends EJBHome 
{
    boClientRemote create() throws RemoteException, CreateException;
}