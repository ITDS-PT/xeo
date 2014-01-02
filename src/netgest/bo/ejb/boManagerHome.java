/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.EJBHome;
import java.rmi.RemoteException;
import javax.ejb.CreateException;

public interface boManagerHome extends EJBHome  {
    boManager create() throws RemoteException, CreateException;
}