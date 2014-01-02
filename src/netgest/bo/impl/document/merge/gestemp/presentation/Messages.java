package netgest.bo.impl.document.merge.gestemp.presentation;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "netgest.bo.impl.document.merge.gestemp.presentation.messages";




	private Messages() {
	}

	public static String getString(String key) {
		try {
			return netgest.bo.system.boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
