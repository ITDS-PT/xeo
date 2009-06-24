/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.util.Date;
import netgest.bo.runtime.*;
import netgest.bo.system.*;
import org.apache.log4j.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class LoginUtil 
{
    private static Logger logger = Logger.getLogger("netgest.bo.system.LoginUtil");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public LoginUtil()
    {
    }
    private static void log(boSession session, String type)
    {
        EboContext boctx = null;
        try
        {            
            boctx = session.createRequestContext(null, null, null);
            boObject login = boObject.getBoManager().createObject(boctx, "Ebo_Login");
            login.getAttribute("user").setValueLong(session.getPerformerBoui());
            login.getAttribute("data").setValueDate(new Date());
            login.getAttribute("movementType").setValueString(type);
            if(session.getRepository() != null && session.getRepository().getName() != null &&
                session.getRepository().getName().length() > 0)
            {
                login.getAttribute("repository").setValueString(session.getRepository().getName());
            }
            if(session.getPerformerIProfileBoui() > 0)
            {
                login.getAttribute("iProfile").setValueLong(session.getPerformerIProfileBoui());
            }
            login.getAttribute("remoteAddr").setValueString(session.getRemoteAddress());
            login.getAttribute("remoteHost").setValueString(session.getRemoteHost());
            login.getAttribute("remoteUser").setValueString(session.getRemoteUser());
            login.getAttribute("remoteSessionId").setValueString(session.getRemoteSessionId());
            
            login.update();
        }
        catch (Exception e)
        {
            logger.error("Erro a registar o LOGIN/OUT ["+type+"] do user ["+session.getUser().getUserName()+"]" + e.getClass() + ":" + e.getMessage(),e);
        }
        finally
        {
            boctx.close();
        }
    }
    
    public static void logout(boSession session)
    {
        log(session, "out");
    }
    
    public static void login(boSession session)
    {
        log(session, "in");
    }
}