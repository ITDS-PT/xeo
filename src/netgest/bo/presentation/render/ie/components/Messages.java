package netgest.bo.presentation.render.ie.components;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
	private static final String BUNDLE_NAME = "netgest.bo.presentation.render.ie.components.messages";

	private Messages() {
	}

	public static final String getString(String key) {
		try {
			return netgest.bo.system.boSession.getResourceBundle( BUNDLE_NAME ).getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
