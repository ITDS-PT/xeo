/*Enconding=UTF-8*/
package netgest.bo.system.login;
import java.util.Date;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.system.*;
import netgest.bo.utils.boEncrypter;
import netgest.utils.StringEncrypter;
import netgest.utils.StringUtils;

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
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_REGISTERING_THE_LOGIN_OUT")+" ["+type+"] "+LoggerMessageLocalizer.getMessage("FROM_USER")+" ["+session.getUser().getUserName()+"]" + e.getClass() + ":" + e.getMessage(),e);
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
    
    
    /**
     * 
     * Encrypts a password (padds/trim it to 24 chars if the password if not that size) 
     * 
     * @param password The password to encrypt
     * @return A string with the encrypted password, or the empty string if the encode cannot be done
     * 
     * 
     */
    public static String encryptPassword(String password) {
    	int passLength = password.length(); 
    	int requiredSize = StringEncrypter.STRING_REQUIRED_SIZE;
    	if ( passLength > requiredSize ){
    		password = password.substring( 0, requiredSize - 1 );
    	}
    	String validPassword = StringUtils.padr( password, requiredSize, "0" );
    	try {
			return boEncrypter.staticEncrypt(validPassword);
		} catch (boRuntimeException e) {
			logger.warn("Could not encrypt the password", e);
		}
		return "";
    }
    
}