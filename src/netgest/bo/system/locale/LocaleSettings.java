package netgest.bo.system.locale;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import netgest.bo.system.locale.LocaleFormatter.CurrencyPosition;
import netgest.utils.StringUtils;

public class LocaleSettings {

	public static final CurrencyPosition CURRENCY_POSITION = CurrencyPosition.RIGHT;
	public static final String CURRENCY_SYMBOL = "€";
	public static final String DECIMAL_SEPARATOR = ",";
	public static final String DATE_FORMAT = "dd/MM/yyyy";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_TIME_SEPARATOR = " ";
	public static final String GROUP_SEPARATOR = "."; 
	public static final Locale LOCALE = new Locale("pt","PT");
	public static final TimeZone TIMEZONE = TimeZone.getDefault();
	public static final List<Locale> AVAILABLE_LOCALES = 
			Arrays.asList(new Locale("pt","PT"), Locale.ENGLISH, new Locale("es","ES"));
	
	
	public static final LocaleSettings DEFAULT = new LocaleSettings( 
			LOCALE
			, TIMEZONE
			, DATE_FORMAT 
			, TIME_FORMAT 
			, DATE_TIME_SEPARATOR 
			, GROUP_SEPARATOR 
			, DECIMAL_SEPARATOR 
			, CURRENCY_SYMBOL
			, CURRENCY_POSITION
			, AVAILABLE_LOCALES
			);
	
	public Locale getLocale() {
		return locale;
	}

	public TimeZone getTimezone() {
		return timeZone;
	}

	private String datePattern;
	private String timePattern;
	private String dateTimeSeparator;
	private char groupSeparator;
	private char decimalSeparator;
	private String currencySymbol;
	private CurrencyPosition currencyPosition;
	private Locale locale;
	private TimeZone timeZone;
	private List<Locale> availableLocales;
	
	
	public LocaleSettings(Locale locale, TimeZone timeZone, String datePattern, String timePattern, String dateTimeSeparator,
			String groupSeparator, String decimalSeparator,
			String currencySymbol, CurrencyPosition currencyPosition, List<Locale> availableLocales) {
		this.locale = locale;
		this.timeZone = timeZone;
		this.datePattern = datePattern;
		this.timePattern = timePattern;
		this.dateTimeSeparator = dateTimeSeparator;
		this.groupSeparator = groupSeparator.charAt( 0 );
		this.decimalSeparator = decimalSeparator.charAt( 0 );
		this.currencySymbol = currencySymbol;
		this.currencyPosition = currencyPosition;
		this.availableLocales = availableLocales;
	}

	public String getDatePattern() {
		return this.datePattern;
	}
	
	public String getDateTimeSeparator() {
		return dateTimeSeparator;
	}

	public char getGroupSeparator() {
		return this.groupSeparator;
	}

	public String getTimePattern() {
		return timePattern;
	}

	public char getDecimalSeparator() {
		return this.decimalSeparator;
	}

	public String getCurrencySymbol() {
		return currencySymbol;
	}
	
	public CurrencyPosition getCurrencyPosition() {
		return currencyPosition;
	}
	
	public boolean hasCurrencySettings() {
		return StringUtils.hasValue( currencySymbol ) 
				&& isNotEmptyChar(decimalSeparator) 
				&& isNotEmptyChar(groupSeparator) ;
				
	}

	private boolean isNotEmptyChar(char toCheck) {
		return "".equalsIgnoreCase( Character.toString( toCheck ) );
	}
	
	public boolean hasNumberSettings() {
		return isNotEmptyChar( decimalSeparator ) 
				&& isNotEmptyChar( groupSeparator );
				
	}
	
	public void setLocale(Locale newLocale) {
		this.locale = newLocale;
	}
	
	public List<Locale> getAvailableLocales(){
		return availableLocales;
	}
	
}
