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
 * 
 * 
 * Class that searches and returns messages displayed on the Logger
 * 
 * @author ngrosskopf
 * 
 */
public class LoggerMessageLocalizer {
	
	public static final Logger logger = Logger.getLogger(LoggerMessageLocalizer.class);
	
	/**
	 * gets the message in the user language, if possible else gets the message
	 * in the default application language
	 * 
	 * @param whichMessage
	 * @return (string) error message
	 */
	public static String getMessage(String whichMessage) {
		String language;
		Properties properties = new Properties();
		String message = null;
		language = getLanguage();
		if (language.length() > 2) {
			language = language.substring(0, 1);
		}
		//Language is never null from getLanguage()
		language = language.toUpperCase();
		try{
		try {
			
			String s = ("LoggerMessageLocalizer_" + language + ".properties");
			InputStream stream = LoggerMessageLocalizer.class.getResourceAsStream(s);
			if (stream != null)
				properties.load(stream);
			
			if (properties.getProperty(whichMessage) != null)
				message = properties.getProperty(whichMessage);
			else {

				s = ("LoggerMessageLocalizer_"
						+ boApplication.getDefaultApplication()
								.getApplicationLanguage() + ".properties");
				InputStream applicationLangStream = LoggerMessageLocalizer.class
					.getResourceAsStream(s); 
				if (applicationLangStream != null){
					properties.load(applicationLangStream);
					message = properties.getProperty(whichMessage);
				}
				
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} 
		if (message == null) {
			String s = ("LoggerMessageLocalizer_"
					+ boApplication.getDefaultApplication()
							.getApplicationLanguage() + ".properties");
			try {
				InputStream stream = LoggerMessageLocalizer.class
				.getResourceAsStream(s);
				if (stream != null){
					properties.load(stream);
					message = properties.getProperty(whichMessage);
				}
			} catch (IOException e) {
				logger.warn(e);
			}
		}
		}
		catch (Throwable t){
			logger.warn(t);
			//NullPointer exceptions and such should not halt the execution
		}
		return message;
	}

	/**
	 * 
	 * 
	 * @return(String) the user language if not null, else the default
	 *                 application language
	 */
	private static String getLanguage() {
		String language = "";
		boApplication boApp = boApplication.currentContext().getApplication();
		language = boApp.getApplicationLanguage();
		if (boApp.getSessions() != null) {

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
		/**
		 * if (!boApplication.currentContext().getEboContext().getBoSession().
		 * getUser().getLanguage().equals(""))
		 * if(boApplication.currentContext().
		 * getEboContext().getBoSession().getUser().getLanguage()!=null)
		 * language
		 * =boApplication.currentContext().getEboContext().getBoSession()
		 * .getUser().getLanguage();
		 */
		if (language == null || language == "")
			language = boApplication.getDefaultApplication()
					.getApplicationLanguage();

		return language;
	}
}
