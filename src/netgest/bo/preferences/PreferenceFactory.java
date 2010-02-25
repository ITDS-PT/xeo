package netgest.bo.preferences;

public interface PreferenceFactory {

	public Preference createPreference( PreferenceStore store, String name, String profile, String username, String customContext, Preference parent );
	
}
