package netgest.bo.preferences;

public class PreferenceFactoryImpl implements PreferenceFactory {

	@Override
	public Preference createPreference(
			PreferenceStore store,
			String name, 
			String profile, 
			String username, 
			String customContext,
			Preference parent ) {
		// TODO Auto-generated method stub
		return new Preference(store, name, username, profile, customContext, parent);
	}

}
