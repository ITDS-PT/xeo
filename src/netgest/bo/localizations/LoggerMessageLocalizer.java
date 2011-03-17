package netgest.bo.localizations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import netgest.bo.system.boApplication;
import netgest.bo.system.boApplicationConfig;

/**
 * 
 * 
 * Class that searches and returns messages displayed on the Logger
 * 
 * @author ngrosskopf
 * 
 */
public class LoggerMessageLocalizer {
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
		try {
			String s = ("LoggerMessageLocalizer_" + language + ".properties");
			if (LoggerMessageLocalizer.class.getResourceAsStream(s) != null)
				properties.load(LoggerMessageLocalizer.class
						.getResourceAsStream(s));
			// file=new File(local+language+".properties");
			// properties.load(new FileInputStream(file));
			if (properties.getProperty(whichMessage) != null)
				message = properties.getProperty(whichMessage);
			else {

				s = ("LoggerMessageLocalizer_"
						+ boApplication.getDefaultApplication()
								.getApplicationLanguage() + ".properties");
				properties.load(LoggerMessageLocalizer.class
						.getResourceAsStream(s));
				message = properties.getProperty(whichMessage);
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
				properties.load(LoggerMessageLocalizer.class
						.getResourceAsStream(s));
			} catch (IOException e) {

				e.printStackTrace();
			}
			message = properties.getProperty(whichMessage);
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

			boApplicationConfig cf = boApp.getApplicationConfig();
			if (boApp.getSessions().getActiveSessions().length > 0)
				 if(boApplication.currentContext() != null)
					   if(boApplication.currentContext().getEboContext() != null)
						   if(boApplication.currentContext().getEboContext().getBoSession() != null)
							   if(boApplication.currentContext().getEboContext().getBoSession().getUser() != null){
								   if(boApplication.currentContext().getEboContext().getBoSession().getUser().getLanguage() != null)
				language = boApplication.currentContext().getEboContext().getBoSession().getUser().getLanguage();

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
