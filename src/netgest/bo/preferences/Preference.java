package netgest.bo.preferences;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Preference {
	
	private Preference 		parent = null;
	private PreferenceStore store = null;
	private String			name;
	private String			user;
	private String			profile;
	private String 			customContext;
	
	Map<String,Object> values = new HashMap<String,Object>();
	
	public Preference( PreferenceStore store, String name, String user, String profile, String customContext, Preference parent ) {
		this.store = store;
		this.name = name;
		this.user = user;
		this.profile = profile;
		this.customContext = customContext;
		this.parent = parent;
	}
	
	public String getString( String key ) {
		String ret = System.getProperty( this.name + "." + key );
		if( ret == null ) {
			if( containsPreference( key ) ) {
				try {
					return (String)values.get( key );
				} catch (ClassCastException e) {
					return null;
				}
			}
			if( parent != null ) {
				return parent.getString(key);
			}
		}
		return ret;
	}
	
	public void setString( String key, String value ) {
		values.put( key, value);
	}

	public long getLong( String key ) {
		String ret = System.getProperty( this.name + "." + key );
		if( ret != null ) {
			try {
				return Long.valueOf( ret );
			} catch (Exception e) {
				return 0;
			}
		}
		try {
			return (Long)values.get( name );
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void setBoolean( String name, boolean value ) {
		values.put( name, value );
	}

	public boolean getBoolean( String key ) {
		String ret = System.getProperty( this.name + "." + key );
		if( ret != null ) {
			try {
				return Boolean.valueOf( ret );
			} catch (Exception e) {
				return false;
			}
		}
		try {
			Object value = values.get(key);
			if (value != null){
				return Boolean.valueOf(value.toString());
			}
		} catch (ClassCastException e) {
			return false;
		}
		return false;
	}
	
	public void setLong( String name, long value ) {
		values.put( name, value );
	}
	
	public double getDouble( String key ) {
		String ret = System.getProperty( this.name + "." + key );
		if( ret != null ) {
			try {
				return Double.valueOf( ret );
			} catch (Exception e) {
				return 0;
			}
		}
		try {
			return (Double)values.get( name );
		} catch (ClassCastException e) {
			return 0;
		}
	}
	
	public void setDouble( String name, double value ) {
		values.put( name, value );
	}

	/**
	 * @return the store
	 */
	public PreferenceStore getStore() {
		return store;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @return the profile
	 */
	public String getProfile() {
		return profile;
	}

	public String getValueType( String key ) {
		Object value = this.values.get( key );
		if( value != null ) {
			return value.getClass().getName();
		}
		return null;
	}
	
	/**
	 * @return the customContext
	 */
	public String getCustomContext() {
		return customContext;
	}

	public Preference getParentPreference() {
		return parent;
	}
	
	public boolean containsPreference( String name ) {
		return this.values.containsKey( name );
	}
	
	public Iterator<String> getKeys() {
		return this.values.keySet().iterator();
	}

	public Iterator<Object> getValues() {
		return this.values.values().iterator();
	}

	/**
	 * 
	 * Put a new value in the preference given the key
	 * 
	 * @param key The key
	 * @param value The value (must a serialzed object as a string) although the parameter
	 * takes an Object you must pass a string
	 */
	public void put(String key, Object value ) {
		this.values.put(key, value);
	}

	public Object get(String key ) {
		return this.values.get(key);
	}
	
	public boolean savePreference() {
		return this.store.savePreference( this );
	}

	public boolean loadPreference() {
		return this.store.loadPreference( this );
	}
	
}
