package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents the configuration of a Metadata
 * node in an ECM Repository
 * 
 * @author PedroRio
 *
 */
public class MetadataNodeConfig {

	/**
	 * The XML Handler 
	 */
	private ngtXMLHandler xmlHandler;
	/**
	 * The node type of this metadata node
	 */
	private String p_nodeType;
	/**
	 * A map of properties to their data types
	 */
	private Map<String,NodePropertyDefinition> p_properties;
	
	/**
	 * The names of the children metadata nodes
	 */
	private Set<String> p_childrenMetadata;
	
	/**
	 * The name of this metadata node
	 */
	private String p_name;
	
	/**
	 * If items of this type can be created
	 */
	private boolean p_canCreate;
	
	/**
	 * If this metadata item is at root level or has a parent
	 */
	private boolean p_isRoot;
	
	/**
	 * The name of the parent node (can be self-referencing)
	 */
	private String p_parentNodeName;
	
	/**
	 * If the metadata item has children items
	 */
	private boolean p_hasChildren;
	
	/**
	 * The query from the root node to reach this type of metadata node
	 */
	private String p_queryToReach;
	
	/**
	 * If the metadata node is the default metadata type
	 */
	private boolean p_isDefault;
	
	/**
	 * A reference to all metadata node definitions for the repository
	 */
	private Map<String,MetadataNodeConfig> p_allMetadataDefinitions;
	
	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return p_nodeType;
	}


	/**
	 * @return the name
	 */
	public String getName() {
		return p_name;
	}


	public MetadataNodeConfig(ngtXMLHandler handler)
	{
		this.xmlHandler = handler;
		this.p_properties = new HashMap<String, NodePropertyDefinition>();
		this.p_childrenMetadata = new HashSet<String>();
		load();
	}
	
	
	
	/**
	 * Loads the properties from the XML
	 */
	private void load()
	{
		this.p_nodeType = xmlHandler.getAttribute("type");
		this.p_name = xmlHandler.getAttribute("name");
		this.p_queryToReach = xmlHandler.getChildNode("queryToReach").getText();
		this.p_isDefault = false;
		if (xmlHandler.getAttribute("default") != null)
			this.p_isDefault = Boolean.parseBoolean(xmlHandler.getAttribute("default"));
		
		
		//Load properties
		ngtXMLHandler property = xmlHandler.getChildNode("properties");
		ngtXMLHandler[] propNodes = property.getChildNodes();
		for (ngtXMLHandler currProp : propNodes)
		{
			NodePropertyDefinition n = new NodePropertyDefinition(currProp);
			p_properties.put(n.getName(),n);
		}
		
		//Load parents
		ngtXMLHandler parent = xmlHandler.getChildNode("parent");
		this.p_parentNodeName = parent.getText();
		if (p_parentNodeName != null)
			p_isRoot = false;
		else
			p_isRoot = true;
		
		//Load the children
		ngtXMLHandler children = xmlHandler.getChildNode("children");
		ngtXMLHandler[] childNodes = children.getChildNodes();
		p_hasChildren = false;
		for (ngtXMLHandler child: childNodes){
			p_hasChildren = true;
			this.p_childrenMetadata.add(child.getAttribute("ref").trim());
		}
		
		
	}


	/**
	 * @return the properties
	 */
	public Map<String, NodePropertyDefinition> getProperties() {
		return p_properties;
	}


	/**
	 * @return the childrenMetadata
	 */
	public Set<String> getChildrenMetadata() {
		return p_childrenMetadata;
	}


	/**
	 * @return the isRoot
	 */
	public boolean isRoot() {
		return p_isRoot;
	}


	/**
	 * @return the parentNodeName
	 */
	public String getParentNodeName() {
		return p_parentNodeName;
	}
	/**
	 * 
	 * Retrieves whether the type of Nodes this configuration 
	 * represents can be created (add new) or not
	 * 
	 * @return True if metadata nodes can be created 
	 * with this configuration and false otherwise
	 */
	public boolean canCreateMetadata(){
		return p_canCreate;
	}
	
	
	/**
	 * 
	 * Retrieves whether this Metadata Node 
	 * 
	 * @return True if it's the default metadata node type and false otherwise
	 */
	public boolean isDefault(){
		return p_isDefault;
	}
	
	/**
	 * Retrieves whether or nor this configuration has child nodes
	 * 
	 * @return True if the configuration has Metadata child nodes
	 * and false otherwise
	 */
	public boolean hasChildren(){
		return p_hasChildren;
	}
	
	/**
	 * 
	 * If the Metadata node is not at the root level, this
	 * retrieves the query to reach them
	 * 
	 * @return A string with the path to the metadata node 
	 * from the root node
	 */
	public String getQueryToReach(){
		return p_queryToReach;
	}
	
	/**
	 * Retrieves a reference to all Metadata Node configurations in the repository
	 * 
	 * @return A reference to all {@link MetadataNodeConfig} elements in the repository
	 */
	public Map<String,MetadataNodeConfig> getAllRepositoryConfigurations(){
		return p_allMetadataDefinitions;
	}
	
	/**
	 * 
	 * Sets a reference to all metadata node configurations in the repository
	 * 
	 * @param configs A reference to all metadata node configurations
	 */
	public void setAllRepositoryConfigurations(Map<String,MetadataNodeConfig> configs){
		p_allMetadataDefinitions = configs;
	}
	
}
