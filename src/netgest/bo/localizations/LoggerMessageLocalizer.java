package netgest.bo.localizations;


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
		return MessageLocalizer.getMessage(whichMessage, "LoggerMessageLocalizer_", new Object[0]);
	}

}
