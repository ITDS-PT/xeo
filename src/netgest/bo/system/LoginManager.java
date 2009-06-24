/*Enconding=UTF-8*/
package netgest.bo.system;
import javax.servlet.http.*;

public interface LoginManager 
{
    long boLogin( boApplication app, String repository , String username, String password,HttpServletRequest request) throws boLoginException;
    long boLogin( boApplication app, String repository , String username, long time, long timeCheck, HttpServletRequest request) throws boLoginException;
}