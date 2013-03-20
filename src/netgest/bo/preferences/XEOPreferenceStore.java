package netgest.bo.preferences;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.Logger;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

public class XEOPreferenceStore implements PreferenceStore {
	
	private static final Logger logger = Logger.getLogger(XEOPreferenceStore.class);
	
	private static boolean UPDATED_MSG = false;
	
	private static BigDecimal SYSUSER_BOUI = null;
	
	private static final Object parseValue( boObject pobj ) throws boRuntimeException {
		String value;
		String type;
		
		type	 = pobj.getAttribute("valueType").getValueString();
		try {
			if( "1".equals(pobj.getAttribute("isLob").getValueString()) ) {
				value 	= pobj.getAttribute("clobValue").getValueString();
			}
			else {
				value 	= pobj.getAttribute("value").getValueString();
			}
		}
		catch( Exception e ) {
			if( !UPDATED_MSG ) {
				UPDATED_MSG = true;
				logger.severe( "The object PreferenceStore is out of date (Definition or Database Tables). See error stack for details.",e );
			}
			value 	= pobj.getAttribute("value").getValueString();
		}
		
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
		
		boolean localContext = false;
		boSession session = null;
		
		EboContext ctx = boApplication.currentContext().getEboContext();
		try {
			if( ctx == null ) {
				localContext = true;
				session = boApplication.getApplicationFromStaticContext("XEO")
					.boLogin("SYSUSER",  boLoginBean.getSystemKey() );
				ctx = session.createRequestContext(null, null, null);
			}

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
					logger.severe(LoggerMessageLocalizer.getMessage("ERROR_LOADING_PREFERENCES")+" [%s]. "+LoggerMessageLocalizer.getMessage("CAUSED_BY")+":", e, p.getName() );
					ret = false;
				}
			}
			else {
				logger.severe(LoggerMessageLocalizer.getMessage("FAILED_TO_LOAD_PREFERENCES")+" [%s]. "+LoggerMessageLocalizer.getMessage("THERE_IS_NO_EBOCONTEXT_ASSOCIATED_TOTHIS_THREAD"), p.getName() );
			}
		}
		catch( boLoginException e ) {
			throw new RuntimeException("Failed to login as SYSUSER loading preference [" + p.getName() + "]!");
		}
		finally {
			if ( localContext ) {
				if( ctx != null )
					ctx.close();
				if( session != null )
					session.closeSession();
			}
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
							try {
								String encodedValue = encodeValue(p, key);
								String valueType = o.getAttribute("valueType").getValueString();
								String value     = 
									o.getAttribute("value").getValueObject() == null?
											o.getAttribute("clobValue").getValueString():
											o.getAttribute("value").getValueString();
																			
								boolean changed = false;
								if( value == null && encodedValue != null ) {
									changed = encodedValue.length()>0?true:false;
								}
								else if( value != null && encodedValue == null ) {
									changed = value.length()>0?true:false;
								}
								else if( value == null && encodedValue == null ) {
								}
								else if( value.length()==0 && encodedValue.length()==0 ) {
								}
								else if( !value.equals( encodedValue ) ) {
									changed = true;
								}
									
								if( changed || (encodedValue != null && !valueType.equals( p.getValueType(key))))				
								{
									o.getAttribute("valueType").setValueString( p.getValueType(key) );
									
									if( encodedValue != null && encodedValue.getBytes("UTF-8").length >= 3900 ) {
										o.getAttribute("isLob").setValueString( "1" );	
										o.getAttribute("clobValue").setValueString( encodedValue );	
										o.getAttribute("value").setValueString( null );	
									}
									else {
										o.getAttribute("isLob").setValueString( "0" );	
										o.getAttribute("clobValue").setValueString( null );	
										o.getAttribute("value").setValueString( encodedValue );	
									}
									o.update();
								}
							}
							catch( Exception e ) {
								e.printStackTrace();
							};
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
							try {
								boObject obj = boObject.getBoManager().createObject(ctx, "PreferenceStore");
								
								// Evitar de gravar o utilizador corrente quando 
								// se modifica a preference store. (Quando este é o objecto a ser editado, dá erro)
								obj.getAttribute("CREATOR").setValueObject( getSysUserBoui( ctx ) );
								obj.getAttribute("name").setValueString( p.getName() );
								obj.getAttribute("key").setValueString( key );
								obj.getAttribute("contextKey").setValueString( p.getCustomContext() );
								obj.getAttribute("user").setValueString( p.getUser() );
								obj.getAttribute("profile").setValueString( p.getProfile() );
								obj.getAttribute("preferenceType").setValueString( null );
								obj.getAttribute("valueType").setValueString( p.getValueType( key ) );
								
								String encodedValue = encodeValue(p, key);
								if( encodedValue != null && encodedValue.getBytes("UTF-8").length >= 3900 ) {
									obj.getAttribute("isLob").setValueString( "1" );	
									obj.getAttribute("clobValue").setValueString( encodedValue );	
									obj.getAttribute("value").setValueString( null );	
								}
								else {
									obj.getAttribute("isLob").setValueString( "0" );	
									obj.getAttribute("clobValue").setValueString( null );	
									obj.getAttribute("value").setValueString( encodedValue );	
								}
								obj.update();
							}
							catch( Exception e ) {};
						}
					}
					commit = true;
				} catch (Exception e) {
					logger.severe(LoggerMessageLocalizer.getMessage("ERROR_SAVING_PREFERENCES")+" [%s]. "+LoggerMessageLocalizer.getMessage("CAUSED_BY")+":", e, p.getName() );
				} finally {
					if( myTrans ) {
						if( commit ) {
							ctx.commitContainerTransaction();
						}
						else {
							ctx.rollbackContainerTransaction();
						}
					}else {
						if( !commit ) {
							ctx.getConnectionManager().setContainerTransactionForRollback();
						}
					}
				}
			}
			else {
				logger.severe(LoggerMessageLocalizer.getMessage("FAILED_TO_SAVE_PREFERENCES")+" [%s]. "+LoggerMessageLocalizer.getMessage("THERE_IS_NO_EBOCONTEXT_ASSOCIATED_TOTHIS_THREAD"), p.getName() );
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
		sqlQuery.append( " AND 0 < " + System.currentTimeMillis() );
		return sqlQuery.toString();
	}
	
	private static final BigDecimal getSysUserBoui( EboContext ctx ) {
		if( SYSUSER_BOUI == null ) {
			boObjectList list = boObjectList.list(ctx, "select iXEOUser where username='SYSUSER'");
			if(list.next()) {
				SYSUSER_BOUI = BigDecimal.valueOf( list.getCurrentBoui() );
			}
		}
		return SYSUSER_BOUI;
	}
	
}
