package netgest.bo.system;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.xeomodels.system.Theme;
import netgest.bo.xeomodels.system.ThemeIncludes;

import netgest.utils.StringUtils;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * Class to help in creating and setting a session user
 */
class BoUserCreator {

	private boSessionUser user;
	
	BoUserCreator() {
		
	}

	boSessionUser create(boObject perf) throws boRuntimeException {

		user = new boSessionUser();

		if ( userHasLanguage( perf ) ) {
			setUserLanguage( perf );
		} 

		if ( userHasOwnTheme( perf ) ) {
			setUserTheme( perf );
		}
		else {
			setDefaultTheme( perf );
		}

		setUserInformation( perf );
		
		setUserContacts( perf );
		
		setUserSecurityPermissionsInformation( perf );

		return user;
	}

	private void setUserContacts(boObject perf)
			throws boRuntimeException {
		user.email = perf.getAttribute( "email" ).getValueString();
		user.mailboxes = bridgeToArray( perf.getBridge( "emailAccounts" ) );
		user.notify = perf.getAttribute( "notifica" ).getValueString();
	}

	private void setUserInformation(boObject perf)
			throws boRuntimeException {
		user.userName = perf.getAttribute( "username" ).getValueString();
		user.srName = perf.getAttribute( "lastname" ).getValueString();
		user.name = perf.getAttribute( "name" ).getValueString();
		user.boui = perf.getBoui();
	}

	private void setUserSecurityPermissionsInformation(boObject perf) 
			throws boRuntimeException {
		user.groups = bridgeToArray( perf.getBridge( "groups" ) );
		if ( user.userName.equals( "SYSUSER" ) ) {
			user.isAdministrator = true;
		}
		user.queues = bridgeToArray( perf.getBridge( "queues" ) );
		user.roles = bridgeToArray( perf.getBridge( "roles" ) );
		if ( perf.getAttribute( "securityLevel" ).getValueObject() != null ) {
			user.securityLevel = ( ( BigDecimal ) perf.getAttribute(
					"securityLevel" ).getValueObject() ).byteValue();
		}
	}

	private void setUserLanguage(boObject perf)
			throws boRuntimeException {
		boObject perfLang = perf.getAttribute( "user_language" )
				.getObject();
		String performerLanguageCode = perfLang.getAttribute( "code" )
				.toString();
		user.setLanguage( performerLanguageCode );
	}

	private boolean userHasLanguage(boObject perf) throws boRuntimeException {
		return StringUtils.hasValue( perf.getAttribute( "user_language" )
				.getValueString() );
	}

	private boolean userHasOwnTheme(boObject perf) throws boRuntimeException {
		return perf.getAttribute( "theme" ) != null
				&& StringUtils.hasValue( perf.getAttribute( "theme" )
						.getValueString() );
	}

	private void setUserTheme(boObject perf)
			throws boRuntimeException {
		boObject current = perf.getAttribute( "theme" ).getObject();
		user.setTheme( current.getAttribute( Theme.NAME ).getValueString() );

		bridgeHandler filesIncludeHandler = current.getBridge( Theme.FILES );
		if ( filesIncludeHandler != null ) {
			Map< String , String > files = new HashMap< String , String >();
			filesIncludeHandler.beforeFirst();
			while ( filesIncludeHandler.next() ) {
				boObject currentFileInclude = filesIncludeHandler
						.getObject();
				String id = currentFileInclude.getAttribute(
						ThemeIncludes.ID ).getValueString();
				String path = currentFileInclude.getAttribute(
						ThemeIncludes.FILEPATH ).getValueString();
				files.put( id , path );
			}
			user.setThemeFiles( files );
		}
	}

	private void setDefaultTheme(boObject perf)
			throws boRuntimeException {
		boObjectList list = XEO.list( perf.getEboContext() ,
				"select Theme where defaultTheme = '1'" );
		list.beforeFirst();
		list.next();
		if ( list.next() ) {
			boObject defaultTheme = list.getObject();
			user.setTheme( defaultTheme.getAttribute( Theme.NAME )
					.getValueString() );
			bridgeHandler filesIncludeHandler = defaultTheme.getBridge( Theme.FILES );
			Map< String , String > files = new HashMap< String , String >();
			filesIncludeHandler.beforeFirst();
			while ( filesIncludeHandler.next() ) {
				boObject currentFileInclude = filesIncludeHandler.getObject();
				String id = currentFileInclude.getAttribute(
						ThemeIncludes.ID ).getValueString();
				String path = currentFileInclude.getAttribute(
						ThemeIncludes.FILEPATH ).getValueString();
				files.put( id , path );
			}
			user.setThemeFiles( files );
		}
	}

	private final long[] bridgeToArray(bridgeHandler bridge)
			throws boRuntimeException {
		long[] ret = null;
		if ( !bridge.isEmpty() ) {
			ret = new long[bridge.getRowCount()];
			int rec = bridge.getRow();
			bridge.beforeFirst();

			while ( bridge.next() ) {
				ret[bridge.getRow() - 1] = bridge.getValueLong();
			}

			bridge.moveTo( rec );
		}
		return ret;
	}

}
