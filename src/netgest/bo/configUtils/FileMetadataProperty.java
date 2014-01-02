package netgest.bo.configUtils;

import netgest.io.iFile;
import netgest.io.metadata.iMetadataProperty.METADATA_TYPE;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents a Metadata Property for an {@link iFile}
 * 
 * @author PedroRio
 *
 */
public class FileMetadataProperty {

	/**
	 * The name of the Property
	 */
	private String p_name;
	/**
	 * The label of the property
	 */
	private String p_label;
	/**
	 * Whether or not the property is required
	 */
	private boolean p_required;
	/**
	 * Whether or not the property can have multiple values
	 */
	private boolean p_multiple;
	/**
	 * The data type of the property
	 */
	private METADATA_TYPE p_dataType;
	
	/**
	 * Name of the XEO Lov to use for the list of values
	 */
	private String p_xeoLov;
	
	/**
	 * The JCR query to execute when there's the need to show 
	 * a list of values to select
	 */
	private String p_jcrQuery;
	
	/**
	 * 
	 * Loads the values from the XML to the class
	 * 
	 * @param handler The XML handler to load the values from
	 */
	private void load(ngtXMLHandler handler){
		
		p_name = handler.getAttribute("name");
		p_label = handler.getAttribute("label");
		p_multiple = Boolean.valueOf(handler.getAttribute("multiple"));
		p_required = Boolean.valueOf(handler.getAttribute("required"));
		p_xeoLov = handler.getAttribute("xeoLov");
		p_dataType = convertMetadataType(handler.getAttribute("dataType"));
		p_jcrQuery = handler.getAttribute("jcrQuery");
	}
	
	
	/**
	 * 
	 * Given a data type from the XML returns the corresponding
	 * {@link METADATA_TYPE} enumeration value
	 * 
	 * @param name The data type from the XML
	 * @return The {@link METADATA_TYPE} value
	 */
	private METADATA_TYPE convertMetadataType(String name)
	{
		if (name.equalsIgnoreCase("String"))
			return METADATA_TYPE.STRING;
		else if (name.equalsIgnoreCase("Number"))
			return METADATA_TYPE.LONG;
		else if (name.equalsIgnoreCase("Date"))
			return METADATA_TYPE.DATE;
		else if (name.equalsIgnoreCase("Datetime"))
			return METADATA_TYPE.DATETIME;
		else if (name.equalsIgnoreCase("Boolean"))
			return METADATA_TYPE.BOOLEAN;
		else if (name.equalsIgnoreCase("Reference"))
			return METADATA_TYPE.REFERENCE;
		else
			return METADATA_TYPE.STRING;	
	}
	
	public FileMetadataProperty(ngtXMLHandler handler){
		load(handler);
	}
	
	/**
	 * 
	 * Returns the property name
	 * 
	 * @return The name of the property
	 */
	public String getName(){
		return p_name;
	}
	
	
	/**
	 * 
	 * Returns the label to show in a form, for this property
	 * 
	 * @return A label for the property
	 */
	public String getLabel(){
		return p_label;
	}
	
	
	/**
	 * 
	 * Returns whether or not this property is required (must
	 * have a non-empty value)
	 * 
	 * @return True if the property is required and false otherwise
	 */
	public boolean isRequired(){
		return p_required;
	}
	
	/**
	 * 
	 * Returns whether or not this property can have
	 * multiple values (array)
	 * 
	 * @return True if the property can have multiple values and false otherwise
	 */
	public boolean hasMultipleValues(){
		return p_multiple;
	}
	
	
	/**
	 * 
	 * Retrieves the data type for this property
	 * 
	 * @return A {@link METADATA_TYPE} type
	 */
	public METADATA_TYPE getDataType(){
		return p_dataType;
	}
	
	/**
	 * 
	 * Retrieves the name of the XEO Lov to use
	 * when showing a list of values to select from
	 * 
	 * @return The name of a XEO Lov, or null
	 */
	public String getXEOLovName(){
		return p_xeoLov;
	}
	
	/**
	 * 
	 * Retrieves the JCR Query to execute when a list of
	 * values is to be displayed for selection of a value
	 * 
	 * @return A string representing a JCR Query (XPath) or null
	 */
	public String getJCRQuery(){
		return p_jcrQuery;
	}
	
}
