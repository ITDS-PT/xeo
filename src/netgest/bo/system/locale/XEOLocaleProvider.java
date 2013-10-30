package netgest.bo.system.locale;


import netgest.utils.StringUtils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class XEOLocaleProvider implements LocaleFormatter {
	
	private Locale locale;
	private LocaleSettings localeSettings;
	private TimeZone timeZone;
	private NumberFormat numberFormat;
	private NumberFormat currencyFormat;
	private NumberFormat percentageFormat;
	private ConcurrentMap<DateTimeLengh,DateFormat> dateFormaters;
	private DateFormat formaterNoTimezone;
	private ConcurrentMap<DateTimeLengh,DateFormat> timeFormaters;
	private ConcurrentMap<DateTimeLengh,DateFormat> dateTimeFormaters;
	private DateFormat hourMinuteFormater;
	
	public XEOLocaleProvider(Locale locale, TimeZone timeZone, LocaleSettings localeSettings) {
		this.locale = localeSettings.getLocale();
		this.timeZone = localeSettings.getTimezone();
		this.localeSettings = localeSettings;
		dateFormaters = new ConcurrentHashMap< LocaleFormatter.DateTimeLengh , DateFormat >();
		init();
	}
	
	private void init() {
		DateFormat dateFormat = initDateFormat();
		dateFormaters.put( DateTimeLengh.DEFAULT , dateFormat );
		
		formaterNoTimezone = new SimpleDateFormat( localeSettings.getDatePattern() , locale );
		
		DateFormat dateTimeFormat = initDateTimeFormatter();
		dateTimeFormaters.put( DateTimeLengh.DEFAULT , dateTimeFormat );
		
		DateFormat timeFormat = initTimeFormatter();
		timeFormaters.put( DateTimeLengh.DEFAULT , timeFormat );
		
		String hourMinutePattern = localeSettings.getTimePattern().replace( ":ss" , "" );
		hourMinuteFormater = new SimpleDateFormat( hourMinutePattern , locale );
		if (timeZone != null)
			hourMinuteFormater.setTimeZone(timeZone);
		
		if (localeSettings.hasNumberSettings()) {
			this.numberFormat = getNumberFormatter();
		} else {
			this.numberFormat = NumberFormat.getNumberInstance( locale );
		}
		
		if (localeSettings.hasCurrencySettings()) {
			this.currencyFormat = getCurrencyFormatter();
		}
		else {
			this.currencyFormat = NumberFormat.getCurrencyInstance( locale );
		}
		this.percentageFormat = NumberFormat.getPercentInstance( locale );
	}

	private DateFormat initTimeFormatter() {
		timeFormaters = new ConcurrentHashMap< LocaleFormatter.DateTimeLengh , DateFormat >();
		DateFormat timeFormat = new SimpleDateFormat( localeSettings.getTimePattern() , locale );
		if (timeZone != null)
			timeFormat.setTimeZone( timeZone );
		return timeFormat;
	}

	private DateFormat initDateFormat() {
		DateFormat dateFormat = new SimpleDateFormat( localeSettings.getDatePattern() , locale );
		if (timeZone != null)
			dateFormat.setTimeZone( timeZone );
		return dateFormat;
	}

	private DateFormat initDateTimeFormatter() {
		dateTimeFormaters = new ConcurrentHashMap< LocaleFormatter.DateTimeLengh , DateFormat >();
		String dateTimePattern =  localeSettings.getDatePattern()
				+ localeSettings.getDateTimeSeparator() 
				+ localeSettings.getTimePattern();
		DateFormat dateTimeFormat = new SimpleDateFormat( dateTimePattern , locale );
		if (timeZone != null)
			dateTimeFormat.setTimeZone( timeZone );
		return dateTimeFormat;
	}

	
	@Override
	public String formatDate(Date toFormat, DateTimeLengh length) {
		DateFormat formatter = dateFormaters.get( length );
		if (formatter == null) {
			formatter = DateFormat.getDateInstance( length.getFormat() , locale );
			dateFormaters.put( length , formatter );
		}
		return formatter.format( toFormat );
	}

	

	@Override
	public String formatDate(Date toFormat) {
		return dateFormaters.get( DateTimeLengh.DEFAULT ).format( toFormat );
	}

	@Override
	public String formatTime(Date toFormat) {
		return timeFormaters.get( DateTimeLengh.DEFAULT ).format( toFormat );
	}

	@Override
	public String formatTime(Date toFormat, DateTimeLengh length) {
		if (timeFormaters.containsKey( length )) {
			return timeFormaters.get( length ).format( toFormat );
		} else {
			DateFormat timeFormat = DateFormat.getTimeInstance( length.getFormat() , locale );
			if (timeZone != null)
				timeFormat.setTimeZone( timeZone );
			timeFormaters.put( length , timeFormat );
		}
		return null;
	}

	@Override
	public String formatDateTime(Date toFormat) {
		DateFormat df = dateTimeFormaters.get( DateTimeLengh.DEFAULT );
		return df.format( toFormat );
	}

	@Override
	public String formatDateTime(Date toFormat, DateTimeLengh dateLength,
			DateTimeLengh timeLength) {
		if (dateTimeFormaters.containsKey( dateLength )) {
			return dateTimeFormaters.get( dateLength ).format( toFormat );
		} else {
			DateFormat dateTimeFormat = DateFormat.getDateTimeInstance( dateLength.getFormat(), timeLength.getFormat() , locale );
			if (timeZone != null)
				dateTimeFormat.setTimeZone( timeZone );
			dateTimeFormaters.put( dateLength , dateTimeFormat );
			return dateTimeFormat.format( toFormat );
		}
		
	}

	@Override
	public Date parseDate(String date) throws ParseException{
		Iterator<DateFormat> it = dateFormaters.values().iterator();
		ParseException exception = null;
		while (it.hasNext()) {
			DateFormat current = it.next();
			try {
				Date parsed = current.parse( date );
				return parsed;
			} catch ( ParseException e ) {
				exception = e;
			}
		}
		throw exception;
	}

	@Override
	public String formatNumber(long toFormat) {
		return numberFormat.format( toFormat );
	}

	@Override
	public String formatPercent(float toFormat) {
		return percentageFormat.format( toFormat );
	}

	@Override
	public String formatCurrency(long toFormat) {
		return currencyFormat.format( toFormat );
	}

	@Override
	public String getDateFormat(DateTimeLengh length) {
		DateFormat formatter = dateFormaters.get( length );
		if (formatter != null && formatter instanceof SimpleDateFormat  ) {
			SimpleDateFormat simple = ( SimpleDateFormat ) formatter;
			return simple.toLocalizedPattern();
		}
		return new SimpleDateFormat().toLocalizedPattern();
	}

	@Override
	public String getTimeFormat(DateTimeLengh length) {
		DateFormat formatter = timeFormaters.get( length );
		if (formatter != null && formatter instanceof SimpleDateFormat  ) {
			SimpleDateFormat simple = ( SimpleDateFormat ) formatter;
			return simple.toLocalizedPattern();
		}
		return new SimpleDateFormat().toLocalizedPattern();
	}

	@Override
	public char getGroupSeparator() {
		return localeSettings.getGroupSeparator();
	}

	@Override
	public char getDecimalSeparator() {
		return localeSettings.getDecimalSeparator();
		
	}

	@Override
	public String getCurrencySymbol() {
		if (StringUtils.hasValue( localeSettings.getCurrencySymbol()))
			return localeSettings.getCurrencySymbol();
		else {
			return currencyFormat.getCurrency().getSymbol();
		}
	}
	
	private DecimalFormatSymbols decimalSymbols = null;
	
	private DecimalFormatSymbols getDecimalymbols() {
		if ( decimalSymbols == null ) {
			decimalSymbols = new DecimalFormatSymbols( locale );
			decimalSymbols.setDecimalSeparator( getDecimalSeparator() );
			decimalSymbols.setGroupingSeparator( getGroupSeparator() );
			decimalSymbols.setMonetaryDecimalSeparator( getDecimalSeparator() );
			decimalSymbols.setCurrencySymbol( getCurrencySymbol() );	
		}
		return decimalSymbols;
	}

	@Override
	public DecimalFormat getNumberFormatter() {
		String pattern = "###,##0.###";
		return new DecimalFormat(pattern,getDecimalymbols());
	}
	
	@Override
	public DecimalFormat getCurrencyFormatter() {
		String pattern = "###,##0.###";
		if (localeSettings.getCurrencyPosition() == CurrencyPosition.LEFT)
			pattern = '¤' + pattern;
		else 
			pattern = pattern + '¤';
		return new DecimalFormat(pattern,getDecimalymbols());
	}

	@Override
	public String formatHourMinute(Date date) {
		return hourMinuteFormater.format( date ); 
	}

	@Override
	public String getHourMinuteFormat(DateTimeLengh default1) {
		return localeSettings.getTimePattern().replace( ":ss" , "" );
	}

	@Override
	public Date parseDateWithCompleteTime(String dateWithTime) throws ParseException {
		DateFormat format = dateTimeFormaters.get( DateTimeLengh.DEFAULT );
		try {
			Date parsed = format.parse( dateWithTime );
			return parsed;
		} catch (ParseException e) {
			Iterator<DateFormat> it = dateTimeFormaters.values().iterator();
			ParseException exception = e;
			while (it.hasNext()) {
				DateFormat current = it.next();
				try {
					Date parsed = current.parse( dateWithTime );
					return parsed;
				} catch ( ParseException e1 ) {
					exception = e;
				}
			}
			throw exception;
		}
		
	}
	
	private DateFormat dateWithHourMinuteFormatter = null;
	
	private DateFormat getDateWithHourMinuteFormater() {
		if (dateWithHourMinuteFormatter == null) {
			String pattern = getDateFormat( DateTimeLengh.DEFAULT ) 
					+ " " + getHourMinuteFormat( DateTimeLengh.DEFAULT );
			dateWithHourMinuteFormatter = new SimpleDateFormat( pattern , locale );
			dateWithHourMinuteFormatter.setTimeZone(timeZone);
		}
		return dateWithHourMinuteFormatter;
	}
	
	@Override
	public Date parseDateHourMinute(String dateWithTime) throws ParseException {

		DateFormat format = getDateWithHourMinuteFormater();
		try {
			Date parsed = format.parse( dateWithTime );
			return parsed;
		} catch (ParseException e) {
			Iterator<DateFormat> it = dateTimeFormaters.values().iterator();
			ParseException exception = e;
			while (it.hasNext()) {
				DateFormat current = it.next();
				try {
					Date parsed = current.parse( dateWithTime );
					return parsed;
				} catch ( ParseException e1 ) {
					exception = e;
				}
			}
			throw exception;
		}
		
	}

	@Override
	public CurrencyPosition getCurrencyPosition() {
		return localeSettings.getCurrencyPosition();
	}

	@Override
	public Date parseDateWithoutTimezone(String date) throws ParseException {
		return formaterNoTimezone.parse(date);
	}

	
	
	
}
