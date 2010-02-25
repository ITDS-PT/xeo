package netgest.bo.preferences;

import java.util.HashMap;
import java.util.Map;

public class PreferenceManager {
	
	PreferenceFactory defaultFactory = new	PreferenceFactoryImpl();
	
	public Map<String, Preference> systemCache = new HashMap<String, Preference>(); 
	
	public Map<String, HashMap<String,Preference>> profileCache = 
		new HashMap<String, HashMap<String,Preference>>();
	
	public Map<String, HashMap<String,Preference>> userCache = 
		new HashMap<String, HashMap<String,Preference>>(); 
	
	public Map<String, HashMap<String,Preference>> userProfileCache = 
		new HashMap<String, HashMap<String,Preference>>(); 
	
	public Preference getSystemPreference( String name ) {
		return getSystemPreference( name , null );
	}
	
	public Preference getSystemPreference( String name, String currentContext ) {
		Preference p = createPreference( name, null, null, null, null );
		p.loadPreference();		
		if( currentContext != null ) {
			p = createPreference(name, null, null, currentContext, p);
		}
		return p;
	}

	public Preference getProfilePreference( String name, String profileName, String customContext ) {
		Preference p;
		p = getFromCache(profileCache, profileName + contextKey(customContext), name );
		if( p == null ) {
			p = createPreference( name, profileName, profileName, customContext, 
					getSystemPreference( name, customContext )				
				);
			p.loadPreference();
			putInCache(profileCache, profileName + contextKey( customContext ), p);
		}
		return p;
	}

	public Preference getProfilePreference( String name, String profileName ) {
		return getProfilePreference(name, profileName, null );
	}

	public Preference getUserPreference( String name, String userName, String customContext ) {
		Preference p;
		p = getFromCache( userCache, userName + contextKey(customContext), name);
		if( p == null ) {
			p = createPreference( name, userName, null, customContext, 
				getSystemPreference( name, customContext )				
			);
			p.loadPreference();
			putInCache(userCache, userName + contextKey( customContext ), p);
		}
		return p;
	}

	public Preference getUserPreference( String name, String userName ) {
		return getUserPreference(name, userName, null);
	}

	public Preference getUserPreferenceInProfile( String name, String userName, String profileName, String customContext ) {
		Preference p;
		p = getFromCache( userProfileCache, userName+profileName+contextKey(customContext), name);
		if( p == null ) {
			p = createPreference( name, userName, null, customContext, 
						getProfilePreference(name, profileName, customContext )				
				);
			p.loadPreference();
			putInCache(userCache, userName+profileName+contextKey( customContext ), p);
		}
		return p;
	}

	public Preference getUserPreferenceInProfile( String name, String userName, String profileName ) {
		return getUserPreferenceInProfile(name, profileName, userName, null);
	}

	private Preference createPreference( String name, String userName, String profileName, String customContext, Preference parent ) {
		return new Preference(getPreferenceStore(name), name, userName, profileName, customContext, parent);
	}
	
	private PreferenceStore getPreferenceStore( String name ) {
		return new XEOPreferenceStore(  );
	}
	
	private static final Preference getFromCache( 
			Map<String, HashMap<String,Preference>> cache, String name ,String key 
		)
	{
		Preference p = null;
		HashMap<String, Preference> subCacheMap = cache.get( name );
		if( subCacheMap != null ) {
			p = subCacheMap.get( name );
		}
		return p;
		
	}
	
	private static final void putInCache(
			Map<String, HashMap<String,Preference>> cache, String name , Preference p 
		) {
		synchronized (cache) {
			HashMap<String, Preference> subCacheMap = cache.get( name );
			if( subCacheMap == null ) {
				subCacheMap  = new HashMap<String, Preference>();
				cache.put(  name, subCacheMap );
			}
			subCacheMap.put( name, p );
		}
	}
	
	public void clearCache() {
		systemCache.clear();
		profileCache.clear();
		userCache.clear();
		userProfileCache.clear();
	}
	
	public void clearCache( String name ) {
		systemCache.remove( name );
		removeCache( name, profileCache );
		removeCache( name, userCache );
		removeCache( name, userProfileCache );
	}
	
	private static final void removeCache( String name, Map<String, HashMap<String,Preference>> cache ) {
		for( HashMap<String, Preference> entry : cache.values() ) {
			entry.remove( name );
		}
	}
	
	private static final String contextKey( String key ) {
		return key != null ? key : "";
	}

}
