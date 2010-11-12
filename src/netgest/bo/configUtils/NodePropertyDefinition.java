package netgest.bo.configUtils;

import netgest.io.metadata.iMetadataProperty;
import netgest.io.metadata.iMetadataProperty.METADATA_TYPE;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * 
 * Represents a Node property definition
 * 
 * @author PedroRio
 *
 */
public class NodePropertyDefinition {
	
	
	/**
	 * A reference to the XML handler to parse the value
	 */
	private ngtXMLHandler p_xmlHandler;
	
	/**
	 * Name of the property
	 */
	private String p_name;
	
	/**
	 * The datatype of the property
	 */
	private METADATA_TYPE p_dataType;
	
	/**
	 * The reference to a metadata node (can be null)
	 */
	private String p_reference;
	
	/**
	 * The label for the property
	 */
	private String p_label;
	
	/**
	 * Whether this property is to be saved in the main node or not
	 */
	private boolean p_mainNode;
	
	/**
	 * Whether this property is required or not
	 */
	private boolean p_required;
	
	/**
	 * Whether this property can have multiple values or not
	 */
	private boolean p_multiple;
	
	/**
	 * Lov configuration for this property
	 */
	private LovConfig p_lovConfig;
	
	/**
	 * 
	 * Public constructor from a XML handler to parse the values from
	 * 
	 * @param handler The XML handler
	 */
	public NodePropertyDefinition(ngtXMLHandler handler){
		this.p_xmlHandler = handler;
		parse();
	}
	
	
	/**
	 * Parses the XML and loads the values
	 */
	private void parse(){
		this.p_name = p_xmlHandler.getAttribute("name");
		this.p_label = p_xmlHandler.getAttribute("label");
		this.p_dataType = findDataType(p_xmlHandler.getAttribute("type"));
		this.p_reference = p_xmlHandler.getAttribute("ref");
		if (p_xmlHandler.getAttribute("mainNode") != null)
			p_mainNode = Boolean.parseBoolean(p_xmlHandler.getAttribute("mainNode")); 
		else
			p_mainNode = false;
		if (p_xmlHandler.getAttribute("required") != null)
			p_required = Boolean.parseBoolean(p_xmlHandler.getAttribute("required")); 
		else
			p_required = false;
		if (p_xmlHandler.getAttribute("multiple") != null)
			p_multiple= Boolean.parseBoolean(p_xmlHandler.getAttribute("multiple")); 
		else
			p_multiple = false;
		
		if (p_xmlHandler.getChildNode("lov") != null)
			p_lovConfig = new LovConfig(p_xmlHandler.getChildNode("lov")); 
		else
			p_lovConfig = null;
		
	}

	/**
	 * 
	 * Retrieves a {@link METADATA_TYPE} given a name (string,long,etc)
	 * 
	 * @param name The name of the data type
	 * @return
	 */
	private METADATA_TYPE findDataType(String name){
		
		if (name.equalsIgnoreCase(METADATA_TYPE.STRING.name()))
			return METADATA_TYPE.STRING;
		else if (name.equalsIgnoreCase(METADATA_TYPE.LONG.name()))
			return METADATA_TYPE.LONG;
		else if (name.equalsIgnoreCase(METADATA_TYPE.DATE.name()))
			return METADATA_TYPE.DATE;
		else if (name.equalsIgnoreCase(METADATA_TYPE.DATETIME.name()))
			return METADATA_TYPE.DATETIME;
		else if (name.equalsIgnoreCase(METADATA_TYPE.STRING_ARRAY.name()))
			return METADATA_TYPE.STRING_ARRAY;
		else if (name.equalsIgnoreCase(METADATA_TYPE.DATE.name()))
			return METADATA_TYPE.DATE;
		else if (name.equalsIgnoreCase(METADATA_TYPE.BINARY.name()))
			return METADATA_TYPE.BINARY;
		else if (name.equalsIgnoreCase(METADATA_TYPE.BOOLEAN.name()))
			return METADATA_TYPE.BOOLEAN;
		else if (name.equalsIgnoreCase(METADATA_TYPE.REFERENCE.name()))
			return METADATA_TYPE.REFERENCE;
		else
			return METADATA_TYPE.STRING;
	}

	/**
	 * Retrieves the name of the property (must be unique among all properties
	 * of the item)
	 * 
	 * @return the name
	 */
	public String getName() {
		return p_name;
	}


	/**
	 * 
	 * Retrieves the data type of the property, see {@link iMetadataProperty.METADATA_TYPE}
	 * for possible values
	 * 
	 * @return the dataType
	 */
	public METADATA_TYPE getDataType() {
		return p_dataType;
	}


	/**
	 * 
	 * Retrieves the name of a {@link MetadataNodeConfig} item to which this
	 * property can be linked (can only have a value if the dataType is a reference)
	 * 
	 * @return the reference If the dataType is a reference, or null otherwise
	 */
	public String getReference() {
		return p_reference;
	}


	/**
	 * 
	 * Retrieves the label to show for this property
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return p_label;
	}


	/**
	 * @return the mainNode
	 */
	public boolean isMainNode() {
		return p_mainNode;
	}
	
	
	/**
	 * 
	 * Retrieves whether or not this property is required
	 * 
	 * @return True if the property is required and false otherwise
	 */
	public boolean isRequired(){
		return p_required;
	}
	
	/**
	 * 
	 * Retrieves whether or not this property can have multiple values
	 * 
	 * @return True if the property can have multiple values
	 */
	public boolean hasMultipleValues(){
		return p_multiple;
	}
	
	/**
	 * 
	 * Retrieves the lov configuration (if any)
	 * for this {@link NodePropertyDefinition}
	 * 
	 * @return A {@link LovConfig} instance or null if no
	 * configuration exists
	 * 
	 */
	public LovConfig getLovConfig(){
		return p_lovConfig;
	}

}
