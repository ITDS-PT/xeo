package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.Map;

import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents the configuration of a Folder Node in
 * an ECM Repository
 * 
 * @author PedroRio
 *
 */
public class FolderNodeConfig 
{
	/**
	 * The xml handler
	 */
	private ngtXMLHandler xmlHandler;
	
	/**
	 * The type of the node
	 */
	private String nodeType;
	
	/**
	 * The create date property
	 */
	private String createDateProperty;
	
	/**
	 * the date of the last update
	 */
	private String updateDateProperty;
	
	/**
	 * The type of file nodes
	 */
	private String p_fileNodeTypes;
	
	/**
	 * The set of properties of this folder
	 */
	private Map<String,NodePropertyDefinition> p_properties;
	
	
	/**
	 * The set of child nodes of this folder
	 */
	private Map<String,ChildNodeConfig> p_childNodes;
	
	
	public FolderNodeConfig(ngtXMLHandler handler)
	{
		this.xmlHandler = handler;
		this.p_childNodes = new HashMap<String, ChildNodeConfig>();
		this.p_properties = new HashMap<String, NodePropertyDefinition>();
		load();
	}

	/**
	 * Loads the information from the XML
	 */
	private void load(){
		this.nodeType = xmlHandler.getAttribute("type");
		this.createDateProperty = xmlHandler.getAttribute("createDate");
		this.updateDateProperty = xmlHandler.getAttribute("updateDate");
		this.p_fileNodeTypes = xmlHandler.getAttribute("childFilesType");
		
		
		//Load all property definitions
		ngtXMLHandler[] props = xmlHandler.getChildNode("properties").getChildNodes();
		for (ngtXMLHandler prop: props){
			NodePropertyDefinition currPropDefinition = new NodePropertyDefinition(prop);
			p_properties.put(currPropDefinition.getName(), currPropDefinition);
		}
		
		//Load all child node definitions
		ngtXMLHandler[] childNodes = xmlHandler.getChildNode("childNodes").getChildNodes();
		for (ngtXMLHandler childNode: childNodes){
			ChildNodeConfig currentChildNode = new ChildNodeConfig(childNode,null);
			p_childNodes.put(currentChildNode.getName(), currentChildNode);
		}
		
	}
	
	/**
	 * @return the nodeType
	 */
	public String getNodeType() {
		return nodeType;
	}

	/**
	 * @return the createDateProperty
	 */
	public String getCreateDateProperty() {
		return createDateProperty;
	}

	/**
	 * @return the updateDateProperty
	 */
	public String getUpdateDateProperty() {
		return updateDateProperty;
	}
	
	/**
	 * 
	 * Retrieves the name of the node type for files in this folder
	 * Since a folder node can have other types of nodes other than 
	 * the files it holds, we need to be able to distinguish them
	 * 
	 * @return A string with the file node type if children files in this folder
	 */
	public String getFileNodeTypes(){
		return p_fileNodeTypes;
	}

	/**
	 * 
	 * Retrieve the set of properties of this Folder
	 * 
	 * @return the p_properties
	 */
	public Map<String, NodePropertyDefinition> getProperties() {
		return p_properties;
	}

	/**
	 * 
	 * Retrieve the hierarchy structure of this Folder
	 * 
	 * @return the p_childNodes
	 */
	public Map<String, ChildNodeConfig> getChildNodes() {
		return p_childNodes;
	}
	
	
}
