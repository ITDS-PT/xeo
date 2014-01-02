/*Enconding=UTF-8*/
package netgest.bo.system;
import javax.ejb.EJBLocalHome;
import javax.ejb.CreateException;

public interface boLoginLocalHome extends EJBLocalHome  {
    boLoginLocal create() throws CreateException;
}