package netgest.utils;

import netgest.bo.def.boDefAttribute;

/**
 * A collection of utility methods regarding object/attribute metadata
 *
 */
public class MetadataUtils {

	/**
	 * Checks whether a given attribute's type is a collection 
	 * 
	 */
	public static boolean isCollection( boDefAttribute attributeMetadata ) {
		return boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is an object (1:1 relation)
	 * 
	 */
	public static boolean isObject(boDefAttribute attributeMetadata){
		return boDefAttribute.ATTRIBUTE_OBJECT.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is small text 
	 * 
	 */
	public static boolean isText( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_TEXT.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is long text 
	 * 
	 */
	public static boolean isLongText( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_LONGTEXT.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type textual (small or long text) 
	 * 
	 */
	public static boolean isTextualType(boDefAttribute attributeMetadata){
		return isText( attributeMetadata ) || isLongText( attributeMetadata );
	}
	
	/**
	 * Checks whether a given attribute's type is a number 
	 * 
	 */
	public static boolean isNumber( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_NUMBER.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is a date (without time) 
	 * 
	 */
	public static boolean isDate( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_DATE.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is a date with time 
	 * 
	 */
	public static boolean isDateTime( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_TEXT.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is boolean 
	 * 
	 */
	public static boolean isBoolean( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_BOOLEAN.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is binary 
	 * 
	 */
	public static boolean isBinary( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_BINARYDATA.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type is duration 
	 * 
	 */
	public static boolean isDuration( boDefAttribute attributeMetadata ){
		return boDefAttribute.ATTRIBUTE_DURATION.equalsIgnoreCase( attributeMetadata.getAtributeDeclaredType() );
	}
	
	/**
	 * Checks whether a given attribute's type some sort of date (date, datetime, or duration) 
	 * 
	 */
	public static boolean isDateType( boDefAttribute attributeMetadata ){
		return isDate( attributeMetadata ) || isDateTime( attributeMetadata ) || isDuration( attributeMetadata );
	}
	
	/**
	 * Checks whether a given attribute's type is a relation with other Models (object or collection) 
	 * 
	 */
	public static boolean isObjectOrCollection(boDefAttribute attributeMetadata){
		return isCollection( attributeMetadata ) || isObject( attributeMetadata );
	}
	
	/**
	 * Checks whether a given attribute's type a Lov 
	 * 
	 */
	public static boolean isLov(boDefAttribute att ){
		return StringUtils.hasValue( att.getLOVName() );
	}
	
	/**
	 * Returns the path to icon for a model, given its name
	 * 
	 * @param modelName The name of the model
	 * @return The path to the model's icon
	 */
	public static String getPathModelIcon(String modelName){
		if (StringUtils.hasValue( modelName ))
			return new StringBuilder(35).append("resources/").append(modelName).append("/ico16.gif").toString();
		return "";
	}
	
}
