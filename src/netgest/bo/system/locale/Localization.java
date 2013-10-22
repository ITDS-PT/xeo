package netgest.bo.system.locale;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

import netgest.bo.system.locale.LocaleFormatter.CurrencyPosition;
import netgest.utils.StringUtils;
import oracle.xml.parser.v2.XMLNode;
import oracle.xml.parser.v2.XSLException;

import org.w3c.dom.NodeList;

public class Localization {

	private String decimalSeparator;
	private String groupSeparator;
	private String currencySymbol; 
	private CurrencyPosition currencyPosition;
	private Locale locale;
	private TimeZone timeZone;
	private String dateFormat;
	private String dateTimeSeparator;
	private String timeFormat;
	private List<Locale> availableLocales = new ArrayList<Locale>();
	
	private List<String> availableLanguages =  new ArrayList<String>();
 	
	private static final String INVALID = "";
	
	private LocaleSettings result; 
	private XMLNode node;
	private Logger logger;
	
	public Localization(XMLNode mainNode, Logger logger, List<String> availableLanguages) {
		this.node = mainNode;
		this.logger = logger;
		this.availableLanguages = availableLanguages;
	}
	
	public LocaleSettings getSettings() throws XSLException {
		if (result == null) {
			
			parseDecimalSeparator();
			parseGroupSeparator();
			parseCurrencySymbol();
			parseCurrencyPosition();
			parseDateFormat();
			parseTimeFormat();
			parseLocale();
			parseTimeZone();
			parseDateTimeSeparator();
			parseAvailableLocales();
			
			if (allFieldsAreValid()) {
				return new LocaleSettings(
						locale ,
						timeZone ,
						dateFormat , 
						timeFormat , 
						dateTimeSeparator , 
						groupSeparator , 
						decimalSeparator , 
						currencySymbol , 
						currencyPosition,
						availableLocales );
			} else {
				result = LocaleSettings.DEFAULT; 
			}
				
			
		}
		
		return result;
	}

	private void parseAvailableLocales() throws XSLException {
		XMLNode availableLocalesNode =  (XMLNode ) node.selectSingleNode( "availableLocales" );
		if (availableLocalesNode != null) {
			NodeList locales = availableLocalesNode.getChildNodes();
			for (int i = 0 ; i < locales.getLength() ; i++){
				XMLNode locale = (XMLNode) locales.item(i);
				String localeText = locale.getText();
				Locale newLocale = createLocaleFromString(localeText);
				this.availableLocales.add(newLocale);
			}

		} else {
			if (!availableLanguages.isEmpty()){
				for (String language : availableLanguages){
					Locale newLocale = createLocaleFromString( language );
					this.availableLocales.add(newLocale); 
				}
			} else {
				this.availableLocales = LocaleSettings.DEFAULT.getAvailableLocales();
			}
			
		}
	}

	private boolean allFieldsAreValid() {
		if (this.dateFormat.equals(INVALID) 
				|| this.timeFormat.equals(INVALID) 
				|| this.currencySymbol.equals(INVALID)
				|| this.groupSeparator.equals(INVALID)
				|| this.decimalSeparator.equals(INVALID) 
				|| this.dateFormat.equals(INVALID)
				|| this.dateTimeSeparator.equals(INVALID)
				|| this.timeZone == null
				|| this.locale == null) 
			return false;
		return true;
		
	}
	
	private void parseTimeZone() throws XSLException {
		XMLNode timeZone =  (XMLNode ) node.selectSingleNode( "timezone" );
		if (timeZone != null) { 
			String timeZoneXML = timeZone.getText();
			if (StringUtils.hasValue( timeZoneXML )){
				this.timeZone = TimeZone.getTimeZone( timeZoneXML );
			} else
				this.timeZone = null;
			
		} else
			this.timeZone = null;
	}
	
	private void parseLocale() throws XSLException {
		XMLNode locale =  (XMLNode ) node.selectSingleNode( "locale" );
		if (locale != null) {
			String localeText = locale.getText();
			if (StringUtils.hasValue( localeText )) {
				parseLocaleFromString(localeText);
			}
		}
		else 
			this.locale = null;
	}

	private void parseLocaleFromString(String localeText) {
		this.locale = createLocaleFromString(localeText);
	}
	
	public static Locale createLocaleFromString(String localeText) {
		Locale result = null;
		String[] localeParts = localeText.split( "_" );
		if (localeParts.length == 1)
			result = new Locale( localeParts[0] );
		else if (localeParts.length == 2) {
			result = new Locale( localeParts[0], localeParts[1] );
		} else if (localeParts.length == 3) {
			result = new Locale( localeParts[0], localeParts[1], localeParts[2] );
		} 
		return result;
	}

	private void parseDateTimeSeparator() throws XSLException {
		XMLNode dateTimeSeparator =  (XMLNode ) node.selectSingleNode( "dateTimeSeparator" );
		if (dateTimeSeparator != null) {
			this.dateTimeSeparator = dateTimeSeparator.getText(); 
			if ("".equals( this.dateTimeSeparator ))
				this.dateTimeSeparator = " ";
		} else {
			this.dateTimeSeparator = INVALID;
		}
			
			
	}


	private void parseTimeFormat() throws XSLException {
		XMLNode timeFormat =  (XMLNode ) node.selectSingleNode( "timeFormat" );
		if (timeFormat != null) {
			this.timeFormat = timeFormat.getText();
			try {
				new SimpleDateFormat( this.timeFormat );
			} catch ( IllegalArgumentException e ) {
				logger.log( Level.CONFIG , "Could not parse date format in BoConfig" + this.timeFormat );
				this.timeFormat = INVALID;
			}
		} else
			this.timeFormat = INVALID;
	}

	private void parseDateFormat() throws XSLException {
		XMLNode dateFormat =  (XMLNode ) node.selectSingleNode( "dateFormat" );
		if (dateFormat != null) {
			this.dateFormat = dateFormat.getText();
			try {
				new SimpleDateFormat( this.dateFormat );
			} catch ( IllegalArgumentException e ) {
				logger.log( Level.CONFIG , "Could not parse time format in BoConfig" + this.dateFormat );
				this.dateFormat = INVALID;
			}
		} else
			this.dateFormat = INVALID;
	}

	private void parseCurrencyPosition() throws XSLException {
		XMLNode currencyPos = (XMLNode ) node.selectSingleNode( "currencyPosition" ); 
		if (currencyPos  != null) {
			currencyPosition = CurrencyPosition.fromString( currencyPos.getText() );
		}
	}

	private void parseCurrencySymbol() throws XSLException {
		XMLNode currency = (XMLNode ) node.selectSingleNode( "currencySymbol" ); 
		if (currency  != null) {
			currencySymbol = currency.getText();
			if (StringUtils.isEmpty( currencySymbol ))
				currencySymbol = INVALID;
		} else  
			currencySymbol = INVALID;
	}

	private void parseGroupSeparator() throws XSLException {
		XMLNode group = (XMLNode ) node.selectSingleNode( "groupSeparator" ); 
		if (group  != null) {
			groupSeparator = group.getText();
			if(notValidOneCharacterSymbol( groupSeparator ))
				groupSeparator = INVALID;
		} else {
			groupSeparator = INVALID;
		}
	}

	private void parseDecimalSeparator() throws XSLException {
		XMLNode decimal =  (XMLNode ) node.selectSingleNode( "decimalSeparator" );
		if (decimal != null) {
			decimalSeparator = decimal.getText();
			if (notValidOneCharacterSymbol(decimalSeparator)) {
				decimalSeparator = INVALID;
			}
		} else {
			decimalSeparator = INVALID;
		}
		
		
	}

	private boolean notValidOneCharacterSymbol(String value) {
		return StringUtils.isNull( value ) || value.length() > 1;
	}
	

}
