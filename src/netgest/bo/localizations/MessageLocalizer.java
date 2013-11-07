package netgest.bo.localizations;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boContext;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
import netgest.bo.system.locale.XEOLocaleProvider;
import netgest.utils.StringUtils;
/**
 * Class that loads the exception messages from the properties file
 * in the current language
 * 
 * 
 */
public class MessageLocalizer {
	
	private static final Logger logger = Logger.getLogger(MessageLocalizer.class);
	
	private static final Map<String,Properties> translations = new HashMap<String,Properties>();
	
	public static String getMessage(String message){
		return getMessage(message, new Object[0]);
	}
	
	public static String getMessage(String whichMessage, Object... args){
		return getMessage(whichMessage, "MessageLocalizer_", args);
	}
	
	/**
	 * 
	 * Retrieve a localized message
	 * 
	 * @param whichMessage The identifier of the message
	 * 
	 * @return the localized message
	 */
	public static String getMessage(String whichMessage, String file, Object... args){
		String language;
		Properties properties= new Properties();
		String message = "";
		language=truncate2CharsMax(getLanguage());
		language = language.toUpperCase();
		String defaultLanguage = boApplication.getXEO().getApplicationLanguage().toUpperCase();
		try{
			try {
				String properttResourceFilename=(file+language+".properties");
				if (translations.containsKey(properttResourceFilename)){
					message = translations.get(properttResourceFilename).getProperty(whichMessage);
				} else {
					final InputStream resource = MessageLocalizer.class.getResourceAsStream(properttResourceFilename); 
					if( resource != null){
						//Cache file
						properties = PropertiesUtils.readUtf8Properties(MessageLocalizer.class.getResourceAsStream(properttResourceFilename));
						translations.put(properttResourceFilename, properties);
					}
					if (properties.getProperty(whichMessage)!=null)
						message=properties.getProperty(whichMessage);
					else{
						properttResourceFilename = "MessageLocalizer_"+truncate2CharsMax(defaultLanguage)+".properties";
						final InputStream defaultResource = MessageLocalizer.class.getResourceAsStream(properttResourceFilename);
						if (defaultResource != null){
							Properties defaultProperties = PropertiesUtils.readUtf8Properties(defaultResource);
							translations.put(properttResourceFilename, defaultProperties);
							message = defaultProperties.getProperty(whichMessage);
						}
					}
				}
			} catch (FileNotFoundException e) {		
				logger.warn(e);
			} catch (IOException e) {		
				logger.warn(e);
			}	
			if("".equalsIgnoreCase(message)){
				String s=("MessageLocalizer_"+truncate2CharsMax(defaultLanguage)+".properties");
				try{
					properties = PropertiesUtils.readUtf8Properties(MessageLocalizer.class.getResourceAsStream(s));
				} 
				catch (IOException e) {
					logger.warn(e);
				}	
				message=properties.getProperty(whichMessage);
			}
		}
		catch (Throwable t){
			//NullPointer exceptions and such should not halt execution
			logger.warn(t);
		}
		
		if (args.length > 0)
			return String.format(message, args);
		
		return message;
}
	private static String truncate2CharsMax(String string){
		if (StringUtils.hasValue(string)){
			if (string.length() > 1){
				return string.substring(0, 2);
			}
		}
		return "";
	}
	
	/**
	 * The user language if not null, else the default application language
	 * 
	 */
	private static String getLanguage() {
		return XEO.getCurrentLocale().toString();
	}
}
