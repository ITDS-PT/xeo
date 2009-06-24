/*Enconding=UTF-8*/
package netgest.bo.ejb;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;

public interface boManagerLocalHome extends EJBLocalHome  {
    boManagerLocal create() throws CreateException;
}