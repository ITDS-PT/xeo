package netgest.bo.localizations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boContext;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
/**
 * Class that loads the exception messages from the properties file
 * in the current language
 * 
 * @author ngrosskopf
 *
 */
public class MessageLocalizer {
	
	private static final Logger logger = Logger.getLogger(MessageLocalizer.class);
	
	/**
	 * 
	 * 
	 * @param whichMessage
	 * @return(String) the exception message
	 */
	public static String getMessage(String whichMessage){
	String language;
	Properties properties= new Properties();
	String message = "";
	language=getLanguage();
	if (language.length()>2){
		language=language.substring(0, 1);
	}
	language = language.toUpperCase();
	try{
		try {
			String s=("MessageLocalizer_"+language+".properties");
			final InputStream resource = MessageLocalizer.class.getResourceAsStream(s); 
			if( resource != null){
				properties = PropertiesUtils.readUtf8Properties(MessageLocalizer.class.getResourceAsStream(s));
				if (properties.getProperty(whichMessage)!=null)
					message=properties.getProperty(whichMessage);
				else{
					s = ("MessageLocalizer_"+boApplication.getDefaultApplication().getApplicationLanguage()+".properties");
					final InputStream defaultResource = MessageLocalizer.class.getResourceAsStream(s);
					if (defaultResource != null){
						properties = PropertiesUtils.readUtf8Properties(defaultResource);	
						message=properties.getProperty(whichMessage);
					}
				}
			}
		} catch (FileNotFoundException e) {		
			logger.warn(e);
		} catch (IOException e) {		
			logger.warn(e);
		}	
		if("".equalsIgnoreCase(message)){
			String s=("MessageLocalizer_"+boApplication.getDefaultApplication().getApplicationLanguage()+".properties");
			try{
				properties = PropertiesUtils.readUtf8Properties(MessageLocalizer.class.getResourceAsStream(s));
			} 
			catch (IOException e) {
				e.printStackTrace();
			}	
				message=properties.getProperty(whichMessage);
		}
	}
	catch (Throwable t){
		//NullPointer exceptions and such should not halt execution
		logger.warn(t);
	}
	
	return message;
}
	
	/**
	 * 
	 * 
	 * @return(String) the user language if not null, else the default application language
	 */
	private static String getLanguage(){
		String language;
		boApplication boApp = boApplication.currentContext().getApplication();
		language = boApp.getApplicationLanguage();
		if(boApp.getSessions()!=null){
		
			boContext boctx;
			EboContext ctx;
			boSession session;
			boSessionUser user;
			boctx = boApplication.currentContext();
			
				if (boApp.getSessions().getActiveSessions().length > 0)
					 if(boctx != null){
						 ctx = boctx.getEboContext();
						  if(ctx != null){
							  session = ctx.getBoSession();
							  if(session != null){
								  user = session.getUser();
								  if(user.getLanguage() != null)
									  language = user.getLanguage();
							  }
					 }
				}
		}
		if(language==null||language=="")
			language=boApplication.getDefaultApplication().getApplicationLanguage();
		return language;
	}
}
