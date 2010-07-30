package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.Map;

import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents the configuration of file nodes in an ECM repository
 * 
 * 
 * @author PedroRio
 *
 */
public class FileNodeConfig {

	
	/**
	 * The xml handler to parse the values from the XML
	 */
	private ngtXMLHandler xmlHandler;
	
	/**
	 * The JCR type of node (with the prefix included if it exists)
	 */
	private String nodeType;
	
	/**
	 * The property name of this file that represents the binary content
	 */
	private String binaryPropertyName;
	
	/**
	 * The property name of this file that represents the creation date (can be null)
	 */
	private String dateCreatePropertyName;
	
	/**
	 * The property name of this file that represents the last update date (can be null)
	 */
	private String dateUpdatePropertyName;
	
	/**
	 * The property name of this file that represents the mimeType (can be null)
	 */
	private String mimeTypePropertyName;
	
	/**
	 * The list of properties of this
	 */
	private Map<String,NodePropertyDefinition> properties;
	
	
	/**
	 * The list of child nodes
	 */
	private Map<String,ChildNodeConfig> childNodes;
	
	/**
	 * 
	 * Public constructor from a XML handler
	 * 
	 * @param handler The XML handler
	 */
	public FileNodeConfig(ngtXMLHandler handler)
	{
		this.xmlHandler = handler;
		this.properties = new HashMap<String, NodePropertyDefinition>();
		this.childNodes = new HashMap<String, ChildNodeConfig>();
		load();
	}
	
	/**
	 * Loads the data from the XML to the internal structures
	 */
	private void load()
	{
		//Load the properties
		this.nodeType = xmlHandler.getAttribute("type");
		this.binaryPropertyName =  xmlHandler.getAttribute("binaryProperty");
		this.dateCreatePropertyName = xmlHandler.getAttribute("createDate");
		this.dateUpdatePropertyName = xmlHandler.getAttribute("updateDate");
		this.mimeTypePropertyName = xmlHandler.getAttribute("mimeType");
		
		//Load all property definitions
		ngtXMLHandler[] props = xmlHandler.getChildNode("properties").getChildNodes();
		for (ngtXMLHandler prop: props){
			NodePropertyDefinition currPropDefinition = new NodePropertyDefinition(prop);
			properties.put(currPropDefinition.getName(), currPropDefinition);
		}
		
		//Load all child node definitions
		ngtXMLHandler[] childNodes = xmlHandler.getChildNode("childNodes").getChildNodes();
		for (ngtXMLHandler childNode: childNodes){
			ChildNodeConfig currentChildNode = new ChildNodeConfig(childNode,null);
			this.childNodes.put(currentChildNode.getName(), currentChildNode);
		}
		
	}

	/**
	 * @return the properties
	 */
	public Map<String, NodePropertyDefinition> getProperties() {
		return properties;
	}

	/**
	 * @return the childNodes
	 */
	public Map<String, ChildNodeConfig> getChildNodes() {
		return childNodes;
	}

	/**
	 * @return the nodeType	
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @return the binaryPropertyName
	 */
	public String getBinaryPropertyName() {
		return binaryPropertyName;
	}

	/**
	 * @return the dateCreatePropertyName
	 */
	public String getDateCreatePropertyName() {
		return dateCreatePropertyName;
	}

	/**
	 * @return the dateUpdatePropertyName
	 */
	public String getDateUpdatePropertyName() {
		return dateUpdatePropertyName;
	}

	/**
	 * @return the mimeTypePropertyName
	 */
	public String getMimeTypePropertyName() {
		return mimeTypePropertyName;
	}
	
}
