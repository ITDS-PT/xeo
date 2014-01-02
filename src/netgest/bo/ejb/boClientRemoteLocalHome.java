/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface boClientRemoteLocalHome extends EJBLocalHome 
{
    boClientRemoteLocal create() throws CreateException;
}