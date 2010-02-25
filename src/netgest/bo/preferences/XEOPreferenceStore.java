package netgest.bo.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;

public class XEOPreferenceStore implements PreferenceStore {
	
	private static final Logger logger = Logger.getLogger(XEOPreferenceStore.class);
	
	private static final Object parseValue( boObject pobj ) throws boRuntimeException {
		String value;
		String type;
		
		type	 = pobj.getAttribute("valueType").getValueString();
		value 	= pobj.getAttribute("value").getValueString();
		
		if( "java.lang.String".equals( type ) ) {
			return value;
		}
		if( "java.lang.Integer".equals( type ) ) {
			return Integer.valueOf( value );
		}
		if( "java.lang.Long".equals( type ) ) {
			return Long.valueOf( value );
		}
		if( "java.lang.Short".equals( type ) ) {
			return Long.valueOf( value );
		}
		if( "java.lang.Boolean".equals( type ) ) {
			return Boolean.valueOf( value );
		}
		if( "java.lang.Double".equals( type ) ) {
			return Double.valueOf( value );
		}
		return value;
	}

	private static final String encodeValue( Preference p, String key ) throws boRuntimeException {
		String value = null;
		String type;
		
		type	 = p.getValueType( key );
		
		if( "java.lang.String".equals( type ) ) {
			value = p.getString(key) ;
		}
		if( "java.lang.Integer".equals( type ) ) {
			value = Long.toString( p.getLong(key) );
		}
		if( "java.lang.Long".equals( type ) ) {
			value = Long.toString( p.getLong(key) );
		}
		if( "java.lang.Short".equals( type ) ) {
			value = Long.toString( p.getLong(key) );
		}
		if( "java.lang.Boolean".equals( type ) ) {
			value = Boolean.toString( p.getBoolean(key) );
		}
		if( "java.lang.Double".equals( type ) ) {
			value = Double.toString( p.getDouble(key) );
		}
		return value;
	}
	
	
	public boolean loadPreference( Preference p ) {
		
		boolean ret = false;
		List<Object> args = new ArrayList<Object>();
		
		String query;
		query = getQuery( p, args );
		
		
		EboContext ctx = boApplication.currentContext().getEboContext();
		if( ctx != null ) {
			try {
				boObjectList list = boObjectList.list(ctx, query,
						args.toArray(), 1, 10000, false);

				while (list.next()) {
					ret = true;
					boObject pobj = list.getObject();

					p.values.put(pobj.getAttribute("key").getValueString(),
							parseValue(pobj));

				}
			} catch (Exception e) {
				logger.severe("Error loading preferences [%s]. Caused by:", e, p.getName() );
				ret = false;
			}
		}
		else {
			logger.severe("Failed to load preference [%s]. There is no EboContext associated to this Thread.", p.getName() );
		}
		return ret;
	}

	@Override
	public boolean savePreference( Preference p ) {

		boolean commit = false;
		boolean myTrans = false;
		List<Object> args = new ArrayList<Object>();
		String query;
		query = getQuery( p, args );
		
		try {
			EboContext ctx = boApplication.currentContext().getEboContext();
			if( ctx != null ) {
				try {
					
					if( !ctx.getConnectionManager().isContainerTransactionActive() ) {
						myTrans = true;
						ctx.beginContainerTransaction();
					}
					
					Map<String,Boolean> savedPrefs = new HashMap<String,Boolean>();
					
					boObjectList list = boObjectList.list(ctx, query,
							args.toArray(), 1, 10000, false);
					
					String key;
					while( list.next() ) {
						boObject o = list.getObject();
						
						key = o.getAttribute("key").getValueString();
						
						savedPrefs.put( key, true );
						
						if( p.containsPreference( key ) ) {
							// Update
							o.getAttribute("valueType").setValueString( p.getValueType(key) );
							o.getAttribute("value").setValueString( encodeValue(p, key) );
							o.update();
						}
						else {
							// Delete
							o.destroy();
						}
					}
					
					Iterator<String> it = p.getKeys();
					while( it.hasNext() ) {
						key = it.next();
						if( !savedPrefs.containsKey( key ) ) {
							boObject obj = boObject.getBoManager().createObject(ctx, "PreferenceStore");
							obj.getAttribute("name").setValueString( p.getName() );
							obj.getAttribute("key").setValueString( key );
							obj.getAttribute("contextKey").setValueString( p.getCustomContext() );
							obj.getAttribute("user").setValueString( p.getUser() );
							obj.getAttribute("profile").setValueString( p.getProfile() );
							obj.getAttribute("preferenceType").setValueString( null );
							obj.getAttribute("valueType").setValueString( p.getValueType( key ) );
							obj.getAttribute("value").setValueString( encodeValue(p, key) );
							obj.update();
						}
					}
					commit = true;
				} catch (Exception e) {
					logger.severe("Error saving preferences [%s]. Caused by:", e, p.getName() );
				} finally {
					if( myTrans ) {
						if( commit ) {
							ctx.commitContainerTransaction();
						}
						else {
							ctx.rollbackContainerTransaction();
						}
					}
					else {
						if( !commit ) {
							ctx.getConnectionManager().setContainerTransactionForRollback();
						}
					}
				}
			}
			else {
				logger.severe("Failed to save preference [%s]. There is no EboContext associated to this Thread.", p.getName() );
			}
		}
		catch ( Exception e ) {
			throw new RuntimeException( e );
		}
		
		return commit;
	}
	
	private String getQuery( Preference p, List<Object> args ) {
		String name = p.getName();
		String profile = p.getProfile();
		String user = p.getUser();
		String customContext = p.getCustomContext();
		
		StringBuilder sqlQuery = new StringBuilder("select PreferenceStore where name=?");
		args.add( name );

		if( user != null ) {
			sqlQuery.append( " AND user=?" );
			args.add( user );
		}
		else {
			sqlQuery.append( " AND user is NULL" );
		}
		
		if( profile != null ) {
			sqlQuery.append( " AND profile=?" );
			args.add( profile );
		}
		else {
			sqlQuery.append( " AND profile is NULL" );
		}
		
		if( customContext != null ) {
			sqlQuery.append( " AND contextKey=?" );
			args.add( customContext );
		}
		else {
			sqlQuery.append( " AND contextKey is NULL" );
		}
		return sqlQuery.toString();
	}
}
