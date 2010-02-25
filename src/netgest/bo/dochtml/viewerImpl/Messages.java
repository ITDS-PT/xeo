package netgest.bo.dochtml.viewerImpl;

import java.util.MissingResourceException;

public class Messages {
	private static final String BUNDLE_NAME = "netgest.bo.dochtml.viewerImpl.messages"; //$NON-NLS-1$

	public static String getString(String key) {
		try {
			return netgest.bo.system.boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
