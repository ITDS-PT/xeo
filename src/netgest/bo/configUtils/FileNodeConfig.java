package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
	private ngtXMLHandler p_xmlHandler;
	
	/**
	 * The JCR type of node (with the prefix included if it exists)
	 */
	private String p_nodeType;
	
	/**
	 * The property name of this file that represents the binary content
	 */
	private String p_binaryPropertyName;
	
	/**
	 * The property name of this file that represents the creation date (can be null)
	 */
	private String p_dateCreatePropertyName;
	
	/**
	 * The property name of this file that represents the last update date (can be null)
	 */
	private String p_dateUpdatePropertyName;
	
	/**
	 * The property name of this file that represents the mimeType (can be null)
	 */
	private String p_mimeTypePropertyName;
	
	/**
	 * The list of properties of this
	 */
	private Map<String,NodePropertyDefinition> p_properties;
	
	
	/**
	 * The list of child nodes
	 */
	private Map<String,ChildNodeConfig> p_childNodes;
	
	/**
	 * Keeps a set of the node types used in child node definition
	 */
	private Set<String> p_childNodesNodeType; 
	
	/**
	 * A reference to the RepositoryConfig
	 * 
	 */
	private RepositoryConfig p_repoConfig;
	
	/**
	 * 
	 * Public constructor from a XML handler
	 * 
	 * @param handler The XML handler
	 */
	public FileNodeConfig(ngtXMLHandler handler, RepositoryConfig conf)
	{
		this.p_xmlHandler = handler;
		this.p_properties = new HashMap<String, NodePropertyDefinition>();
		this.p_childNodes = new HashMap<String, ChildNodeConfig>();
		this.p_repoConfig = conf;
		load();
	}
	
	/**
	 * Loads the data from the XML to the internal structures
	 */
	private void load()
	{
		//Load the properties
		this.p_nodeType = p_xmlHandler.getAttribute("type");
		this.p_binaryPropertyName =  p_xmlHandler.getAttribute("binaryProperty");
		this.p_dateCreatePropertyName = p_xmlHandler.getAttribute("createDate");
		this.p_dateUpdatePropertyName = p_xmlHandler.getAttribute("updateDate");
		this.p_mimeTypePropertyName = p_xmlHandler.getAttribute("mimeType");
		
		//Load all property definitions
		ngtXMLHandler[] props = p_xmlHandler.getChildNode("properties").getChildNodes();
		for (ngtXMLHandler prop: props){
			NodePropertyDefinition currPropDefinition = new NodePropertyDefinition(prop);
			p_properties.put(currPropDefinition.getName(), currPropDefinition);
		}
		
		//Create the collection to hold nodetypes for child nodes
		p_childNodesNodeType = new HashSet<String>();
		
		//Load all child node definitions
		ngtXMLHandler[] childNodes = p_xmlHandler.getChildNode("childNodes").getChildNodes();
		for (ngtXMLHandler childNode: childNodes){
			ChildNodeConfig currentChildNode = new ChildNodeConfig(childNode,null);
			p_childNodesNodeType.add(currentChildNode.getNodeType());
			this.p_childNodes.put(currentChildNode.getName(), currentChildNode);
		}
	}

	/**
	 * @return the properties
	 */
	public Map<String, NodePropertyDefinition> getProperties() {
		return p_properties;
	}

	/**
	 * @return the childNodes
	 */
	public Map<String, ChildNodeConfig> getChildNodes() {
		return p_childNodes;
	}

	/**
	 * @return the nodeType	
	 */
	public String getNodeType() {
		return p_nodeType;
	}

	/**
	 * @return the binaryPropertyName
	 */
	public String getBinaryPropertyName() {
		return p_binaryPropertyName;
	}

	/**
	 * @return the dateCreatePropertyName
	 */
	public String getDateCreatePropertyName() {
		return p_dateCreatePropertyName;
	}

	/**
	 * @return the dateUpdatePropertyName
	 */
	public String getDateUpdatePropertyName() {
		return p_dateUpdatePropertyName;
	}

	/**
	 * @return the mimeTypePropertyName
	 */
	public String getMimeTypePropertyName() {
		return p_mimeTypePropertyName;
	}
	
	/**
	 * 
	 * Checks whether a given node type belongs to the ChildNode configurations
	 * 
	 * @param nodeType The node type of the node
	 * 
	 * @return True if the node type passed as parameter is part of a child node configuration
	 */
	public boolean nodeBelongsToChildConfig(String nodeType){
		return p_childNodesNodeType.contains(nodeType);
	}

	
	/**
	 * 
	 * Retrieves the repository configuration associated to this {@link FileNodeConfig}
	 * 
	 * @return
	 */
	public RepositoryConfig getRepositoryConfiguration(){
		return p_repoConfig;
	}
}
