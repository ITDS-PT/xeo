package netgest.bo.preferences;

public interface PreferenceStore {

	/**
	 * Gets the preference.
	 * 
	 * @param name the name
	 * @param user the user
	 * @param profile the profile
	 * @param customContext the custom context
	 * 
	 * @return the preference
	 */
	public boolean loadPreference( Preference p );  

	public boolean savePreference( Preference p );  
	
}
