package netgest.bo.localized;
import java.util.MissingResourceException;

public class JSMessages extends Thread 
{
	private static final String BUNDLE_NAME = "netgest.bo.localized.jsmessages";

	public static final String getString(String key) {
		try {
            String sReturnValue = netgest.bo.system.boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
            return sReturnValue.replaceAll( "\n","\\\\n" );
            
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
        
}