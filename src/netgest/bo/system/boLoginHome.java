/*Enconding=UTF-8*/
package netgest.bo.system;
import javax.ejb.EJBHome;
import netgest.bo.system.boLogin;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface boLoginHome extends EJBHome  {
    boLogin create() throws RemoteException, CreateException;
}