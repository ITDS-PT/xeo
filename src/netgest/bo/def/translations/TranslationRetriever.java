package netgest.bo.def.translations;

import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.def.v2.boDefHandlerImpl;
import netgest.bo.def.v2.boDefInterfaceImpl;
import netgest.bo.system.XEO;

import netgest.utils.StringUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TranslationRetriever {
	
	private static ConcurrentMap< String , String > cache = new ConcurrentHashMap< String , String >();
	
	public static String findInCache(String key) {
		if (cache.containsKey( key ))
			return cache.get( key );
		return "";
	}
	
	public static String translate(String className, String defaultValue, String type, String attribute, String whichText) {
		
		if (StringUtils.isEmpty(className))
			return defaultValue;
		
		String cacheKey = new StringBuffer().append( className ).append( defaultValue ).append( type ).append( attribute ).append( whichText ).toString();
//		String cacheHit = findInCache( cacheKey );
//		if (StringUtils.hasValue( cacheHit ))
//			return cacheHit;
		
		String translatedMessage = null;
		Locale locale = XEO.getCurrentLocale();
		
		HashMap<String,Properties> translationsMap = boDefHandlerImpl.getLanguagesMap();
		
		String key = findPropertiesFileNameForLocale( className, locale  );
		if (StringUtils.hasValue( key )) {
			Properties prop = translationsMap.get( key );
			if ( type != null && attribute != null){
				String propertyToRetrieve = type + "." + attribute + "." + whichText;
				String retrievedMessage = prop.getProperty( propertyToRetrieve );
				if( !StringUtils.isEmpty( retrievedMessage ) ){
					translatedMessage = retrievedMessage;
					cache.put( cacheKey , translatedMessage );
				}
			} else if( !StringUtils.isEmpty( prop.getProperty( whichText ) ) ){
				translatedMessage = prop.getProperty( whichText );
				cache.put( cacheKey , translatedMessage );
			}
		}
		
		if ( StringUtils.isEmpty(translatedMessage) ){
			//getSuper
			boDefHandler currentModel = boDefHandlerImpl.getBoDefinition(className);
			if( currentModel != null ) {
				if ( modelExtendsFromOtherModel( currentModel ) ){
					translatedMessage = translate(currentModel.getBoExtendsClass(), defaultValue, type, attribute, whichText);
				}
				//Interfaces
				if (StringUtils.isEmpty(translatedMessage)){
					translatedMessage = findMessageInInterfaces( defaultValue ,
							type , attribute , whichText , translatedMessage ,
							currentModel );
				}
			}
		}
		
		if (!StringUtils.isEmpty( translatedMessage ))
			return translatedMessage;
		
		return defaultValue;
	}

	private static String findMessageInInterfaces(String defaultValue,		String type, 
												  String attribute, 		String whichText,
												  String translatedMessage, boDefHandler currentModel) {
		String[] interfaces = currentModel.getImplements();	
		
		for ( int i = 0 ; interfaces != null && i < interfaces.length; i++)
		{
			boDefInterface interfimpl = boDefInterfaceImpl.getInterfaceDefinition( interfaces[i] );
			translatedMessage = translate( interfimpl.getName(), defaultValue, type, attribute, whichText );
			
			if (!StringUtils.isEmpty(translatedMessage))
				break;
		}
			
		return translatedMessage == null ? defaultValue: translatedMessage;
	}

	private static boolean modelExtendsFromOtherModel(boDefHandler currentModel) {
		return currentModel.getBoExtendsClass()!=null && !currentModel.getBoExtendsClass().equals("");
	}
	
	public static String findPropertiesFileNameForLocale(String classname , Locale locale) {
	
		Map<String,Properties> translations = boDefHandlerImpl.getLanguagesMap();
		
		String fullString = locale.toString() ;
		if (translations.containsKey( createKeyName(classname, fullString) ))
			return createKeyName(classname, fullString);
		if (translations.containsKey( createKeyName(classname, fullString.toLowerCase() )))
			return createKeyName(classname, fullString.toLowerCase());
		if (translations.containsKey( createKeyName(classname, fullString.toUpperCase() )))
			return createKeyName(classname, fullString.toUpperCase());
		
		String language =  locale.getLanguage();
		if (translations.containsKey( createKeyName(classname, language) ))
			return createKeyName(classname, language);
		if (translations.containsKey( createKeyName(classname, language.toLowerCase() )))
			return createKeyName(classname, language.toLowerCase());
		if (translations.containsKey( createKeyName(classname, language.toUpperCase() )))
			return createKeyName(classname, language.toUpperCase());
		
		String countryLanguage = locale.getLanguage() + "_" + locale.getCountry();
		if (translations.containsKey( createKeyName(classname, countryLanguage )))
			return createKeyName(classname, countryLanguage);
		if (translations.containsKey( createKeyName(classname, countryLanguage.toLowerCase() )))
			return createKeyName(classname, countryLanguage.toLowerCase());
		if (translations.containsKey( createKeyName(classname, countryLanguage.toUpperCase() )))
			return createKeyName(classname, countryLanguage.toUpperCase());
		
		return "";
		
	}
	
	private static String createKeyName(String className, String language) {
		return className + "_" + language + ".properties";
				
	}
	
	
	
	
}
