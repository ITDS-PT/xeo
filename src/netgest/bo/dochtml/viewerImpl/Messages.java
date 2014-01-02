package netgest.bo.dochtml.viewerImpl;

import java.util.MissingResourceException;

public class Messages {
	private static final String BUNDLE_NAME = "netgest.bo.dochtml.viewerImpl.messages";

	public static String getString(String key) {
		try {
			return netgest.bo.system.boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
