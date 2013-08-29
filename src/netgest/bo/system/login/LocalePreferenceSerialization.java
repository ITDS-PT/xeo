package netgest.bo.system.login;

import netgest.bo.boConfig;
import netgest.bo.preferences.Preference;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.system.locale.LocaleFormatter.CurrencyPosition;
import netgest.bo.system.locale.LocaleSettings;

import java.util.Locale;
import java.util.TimeZone;

/**
 * 
 * Class responsible for serializing and de-serializing localization information
 *
 */
public class LocalePreferenceSerialization {
	
	
	private static final String CREATED = "created";
	private static final String LOCALE = "locale";
	private static final String TIMEZONE = "timezone";
	private static final String CURRENCY_POSITION = "currencyPosition";
	private static final String CURRENCY_SYMBOL = "currencySymbol";
	private static final String GROUP_SEPARATOR = "groupSeparator";
	private static final String DECIMAL_SEPARATOR = "decimalSeparator";
	private static final String TIME_PATTERN = "timePattern";
	private static final String DATE_PATTERN = "datePattern";
	private static final String USER_LOCALIZATION_KEY = "user.localization";
	private static final String DATE_TIME_SEPARATOR = "dateTimeSeparator";

	public static void save(LocaleSettings settings, EboContext context) {
		
		boSession session = context.getBoSession();
		session.setLocale( settings.getLocale() );
		session.setTimeZone( settings.getTimezone() );
		session.setLocaleSettings( settings );
		
		Preference localization = boApplication.getXEO().getPreferencesManager()
				.getUserPreference( USER_LOCALIZATION_KEY , session.getUser().getUserName() );
		
		localization.setString( DATE_PATTERN , settings.getDatePattern() );
		localization.setString( TIME_PATTERN , settings.getTimePattern() );
		localization.setString( DATE_TIME_SEPARATOR , settings.getDateTimeSeparator() );
		localization.setString( DECIMAL_SEPARATOR , Character.toString( settings.getDecimalSeparator()) );
		localization.setString( GROUP_SEPARATOR , Character.toString( settings.getGroupSeparator()) );
		
		localization.setString( CURRENCY_SYMBOL , settings.getCurrencySymbol() );
		localization.setString( CURRENCY_POSITION , settings.getCurrencyPosition().toString());
		
		localization.setString( TIMEZONE , settings.getTimezone().getID() );
		localization.setString( LOCALE , settings.getLocale().toString() );
		
		localization.setBoolean( CREATED , true );
		
		localization.savePreference();
	}
	
	public static LocaleSettings loadFromPreference(String username) {
		
		Preference localization = boApplication.getXEO().getPreferencesManager()
				.getUserPreference( USER_LOCALIZATION_KEY , username );
		Boolean created = localization.getBoolean( CREATED );
		
		if (created) {
			Locale locale = loadLocale(localization.getString( LOCALE ));
			String timezoneId = localization.getString( TIMEZONE );
			TimeZone timezone = TimeZone.getTimeZone( timezoneId );
			String datePattern = localization.getString( DATE_PATTERN );
			String timePattern = localization.getString( TIME_PATTERN );
			String dateTimeSeparator = localization.getString( DATE_TIME_SEPARATOR );
			String groupSeparator = localization.getString( GROUP_SEPARATOR );
			String decimalSeparator = localization.getString( DECIMAL_SEPARATOR );
			String currencySymbol = localization.getString(  CURRENCY_SYMBOL );
			CurrencyPosition  currencyPosition = CurrencyPosition.
					fromString( localization.getString( CURRENCY_POSITION ) );
			
			LocaleSettings settings = new LocaleSettings( 
					locale 
					, timezone 
					, datePattern 
					, timePattern 
					, dateTimeSeparator 
					, groupSeparator 
					, decimalSeparator 
					, currencySymbol 
					, currencyPosition );
			return settings; 
		} else {
			return boConfig.getLocaleSettings();
		}
		
	}

	private static Locale loadLocale(String string) {
		
		String[] locale = string.split( "_" );
		if (locale.length > 0) {
			if (locale.length == 1) {
				return new Locale( locale[0] );
			} else if (locale.length == 2) {
				return new Locale( locale[0], locale[1] );
			} else if (locale.length == 3) {
				return new Locale( locale[0], locale[1], locale[2] );
			} 
		} 
		return boConfig.getLocaleSettings().getLocale();
	}
	
	
	
	
	
}
