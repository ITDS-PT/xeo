package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.Map;

import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents a child node of a given node, with its properties and childnodes
 * 
 * @author PedroRio
 *
 */
public class ChildNodeConfig {
	
	/**
	 * The name of this node
	 */
	private String p_nodeName;
	
	/**
	 * The node type
	 */
	private String p_nodeType;

	
	/**
	 * The XML handler to parse the values
	 */
	private ngtXMLHandler p_xmlHandler;
	
	/**
	 * The set of properties of this child node
	 */
	private Map<String,NodePropertyDefinition> p_properties;
	
	/**
	 * The set of children of this child node
	 */
	private Map<String,ChildNodeConfig> p_childNodes;
	
	/**
	 * The parent of this child configuration
	 */
	private ChildNodeConfig p_parent;
	/**
	 * 
	 * Public constructor from a XML handler
	 * 
	 * @param handler
	 */
	public ChildNodeConfig(ngtXMLHandler handler, ChildNodeConfig parent){
		this.p_xmlHandler = handler;
		this.p_properties = new HashMap<String, NodePropertyDefinition>();
		this.p_childNodes = new HashMap<String, ChildNodeConfig>();
		this.p_parent = parent;
		parse();	
	}
	
	
	/**
	 * Parses the values of the XML
	 */
	private void parse(){
		
		//Process the regular attributes
		this.p_nodeName = this.p_xmlHandler.getAttribute("name");
		this.p_nodeType = this.p_xmlHandler.getAttribute("type");
		
		//Process properties
		ngtXMLHandler[] props = this.p_xmlHandler.getChildNode("properties").getChildNodes();
		for (ngtXMLHandler p: props){
			NodePropertyDefinition currProp = new NodePropertyDefinition(p);
			this.p_properties.put(currProp.getName(), currProp);
		}
		
		//Process the child nodes
		ngtXMLHandler[] children =this.p_xmlHandler.getChildNode("childNodes").getChildNodes();
		for (ngtXMLHandler child: children){
			ChildNodeConfig conf = new ChildNodeConfig(child,this);
			this.p_childNodes.put(conf.getName(), conf);
		}
	}


	/**
	 * @return the p_nodeName
	 */
	public String getName() {
		return p_nodeName;
	}


	/**
	 * @return the p_nodeType
	 */
	public String getNodeType() {
		return p_nodeType;
	}


	/**
	 * @return the p_properties
	 */
	public Map<String, NodePropertyDefinition> getProperties() {
		return p_properties;
	}


	/**
	 * @return the p_childNodes
	 */
	public Map<String, ChildNodeConfig> getChildNodes() {
		return p_childNodes;
	}
	
	/**
	 * 
	 * Retrieve the parent of this child node 
	 * 
	 * @return A reference to the parent nod, or null if no parent exists
	 */
	public ChildNodeConfig getParent(){
		return p_parent;
	}
	
}
