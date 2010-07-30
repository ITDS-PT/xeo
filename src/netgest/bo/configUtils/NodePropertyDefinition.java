package netgest.bo.configUtils;

import netgest.io.metadata.iMetadataProperty;
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
	private ngtXMLHandler xmlHandler;
	
	/**
	 * Name of the property
	 */
	private String name;
	
	/**
	 * The datatype of the property
	 */
	private String dataType;
	
	/**
	 * The reference to a metadata node (can be null)
	 */
	private String reference;
	
	/**
	 * The label for the property
	 */
	private String label;
	
	/**
	 * Whether this property is to be saved in the main node or not
	 */
	private boolean mainNode;
	
	/**
	 * 
	 * Public constructor from a XML handler to parse the values from
	 * 
	 * @param handler The XML handler
	 */
	public NodePropertyDefinition(ngtXMLHandler handler){
		this.xmlHandler = handler;
		parse();
	}
	
	
	/**
	 * Parses the XML and loads the values
	 */
	private void parse(){
		this.name = xmlHandler.getAttribute("name");
		this.label = xmlHandler.getAttribute("label");
		this.dataType = xmlHandler.getAttribute("type");
		this.reference = xmlHandler.getAttribute("ref");
		if (xmlHandler.getAttribute("mainNode") != null)
			mainNode = Boolean.parseBoolean(xmlHandler.getAttribute("mainNode")); 
		else
			mainNode = false;
	}



	/**
	 * Retrieves the name of the property (must be unique among all properties
	 * of the item)
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * 
	 * Retrieves the data type of the property, see {@link iMetadataProperty.METADATA_TYPE}
	 * for possible values
	 * 
	 * @return the dataType
	 */
	public String getDataType() {
		return dataType;
	}


	/**
	 * 
	 * Retrieves the name of a {@link MetadataNodeConfig} item to which this
	 * property can be linked (can only have a value if the dataType is a reference)
	 * 
	 * @return the reference If the dataType is a reference, or null otherwise
	 */
	public String getReference() {
		return reference;
	}


	/**
	 * 
	 * Retrieves the label to show for this property
	 * 
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}


	/**
	 * @return the mainNode
	 */
	public boolean isMainNode() {
		return mainNode;
	}

}
