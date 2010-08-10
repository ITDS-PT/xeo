package netgest.io.jcr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.io.metadata.MetadataException;
import netgest.io.metadata.iMetadataItem;
import netgest.io.metadata.iMetadataProperty;

/**
 * 
 * Implementation of a Metadata item for a JCR repository
 * 
 * @author Pedro
 *
 */
public class MetadataItem implements iMetadataItem {

	
	
	/**
	 * The JCR node representing this item
	 */
	private Node p_itemNode;
	
	/**
	 * The name of this item
	 */
	private String p_name;
	
	/**
	 * The identifier of this item
	 */
	private String p_identifier;
	
	/**
	 * The set of (unsaved) properties of this {@link MetadataItem}
	 * When a new {@link iMetadataItem} is save all properties are
	 * from this field are saved in the JCR Repository
	 */
	private List<iMetadataProperty> p_props;
	
	/**
	 * The set of children to remove
	 */
	private Set<String> p_childrenRemove;
	
	/**
	 * The list of children metadata items 
	 */
	private List<iMetadataItem> p_children;
	
	/**
	 * The parent metadata item
	 */
	private iMetadataItem p_parent;
	
	/**
	 * The type of this {@link iMetadataItem}
	 */
	private String p_type;
	
	/**
	 * A session with the JCR
	 */
	private Session p_session;
	
	/**
	 * The configuration of the node
	 */
	private MetadataNodeConfig p_configuration;
	
	/**
	 * A reference to all metadata node configurations
	 */
	private Map<String,MetadataNodeConfig> p_allMetadataConfigurations;
	
	public MetadataItem(Node repNode, MetadataNodeConfig config, Map<String,MetadataNodeConfig> metaSetConf){
		this.p_itemNode = repNode;
		this.p_configuration = config;
		this.p_allMetadataConfigurations = metaSetConf;
		initEmpty();
	}
	
	public MetadataItem(MetadataNodeConfig config, Map<String,MetadataNodeConfig> metaSetConf){
		this.p_itemNode = null;
		this.p_configuration = config;
		this.p_allMetadataConfigurations = metaSetConf;
		initEmpty();
	}
	
	public MetadataItem(String identifier, String name, Session session, MetadataNodeConfig config, Map<String,MetadataNodeConfig> metaSetConf){
		this.p_itemNode = null;
		this.p_identifier = identifier;
		this.p_name = name;
		this.p_session = session;
		this.p_configuration = config;
		this.p_allMetadataConfigurations = metaSetConf;
		initEmpty();
	}
	
	/**
	 * Initializes the fields with empty values
	 */
	private void initEmpty(){
		this.p_props = new LinkedList<iMetadataProperty>();
		this.p_children = new LinkedList<iMetadataItem>();
		this.p_childrenRemove = new HashSet<String>();
	}
	
	
	@Override
	public void addChild(iMetadataItem newChild) {
		this.p_children.add(newChild);
	}

	@Override
	public List<iMetadataItem> getChildren() {
		List<iMetadataItem> result = new Vector<iMetadataItem>();
		if (p_itemNode != null){
			try {
				NodeIterator itNodes = p_itemNode.getNodes();
				while (itNodes.hasNext()) {
					Node currentNode = (Node) itNodes.next();
					result.add(new MetadataItem(currentNode,p_configuration,p_allMetadataConfigurations));
				}
			} 
			catch (RepositoryException e) {
				e.printStackTrace();
			}
			return result;
		}
		else
			return p_children;
	}

	@Override
	public String getID() {
		if (p_itemNode == null)
			return p_identifier;
		else
			try {
				return p_itemNode.getPath();
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
			}
	}

	@Override
	public String getName() {
		if (p_itemNode == null)
			return p_name;
		else
			try {
				return p_itemNode.getName();
			} catch (RepositoryException e) {
				e.printStackTrace();
				return null;
			}
	}

	@Override
	public iMetadataItem getParent() {
		if (p_itemNode != null){
			try {
				MetadataNodeConfig parent = p_allMetadataConfigurations.get(this.getParent().getType());
				return new MetadataItem(p_itemNode.getParent(),parent,p_allMetadataConfigurations);
			}  catch (RepositoryException e) { e.printStackTrace();	}
		}
		return p_parent;
	}

	@Override
	public List<iMetadataProperty> getProperties() {
		try {
			Vector<iMetadataProperty> result = new Vector<iMetadataProperty>();
			if (p_itemNode != null){
				PropertyIterator itProps = p_itemNode.getProperties();
				while (itProps.hasNext()) {
					Property prop = (Property) itProps.nextProperty();
					iMetadataProperty metaProp = createMetadataProperty(prop);
					result.add(metaProp);
				}
				return result;
			}
			else{
				return p_props;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Vector<iMetadataProperty>();
	}

	@Override
	public iMetadataProperty getPropertyById(String id) {
		if (p_itemNode != null){
			try {
				Property prop = p_itemNode.getProperty(id);
				return createMetadataProperty(prop);
			} catch (RepositoryException e) {
				e.printStackTrace();
			} catch (MetadataException e) {
				e.printStackTrace();
			}
		} else {
			for (int i = 0; i < p_props.size(); i++)
				if(p_props.get(i).getPropertyIdentifier().equals(id))			
					return p_props.get(i);
		}
		return null;
	}

	@Override
	public String getType() {
		return p_type;
	}

	@Override
	public void removeChild(String childToRemoveIdentifier) {
		this.p_childrenRemove.add(childToRemoveIdentifier);
	}

	@SuppressWarnings("deprecation")
	@Override
	public void save() {
		
		try {
			if (exists()){
				for (int i = 0; i < p_props.size(); i++) {
					MetadataProperty prop = (MetadataProperty) p_props.get(i);
					setNodeProperty(p_itemNode, prop);
				}
				//Save any required children nodes
				Iterator<iMetadataItem> itChildMeta = this.p_children.iterator();
				while (itChildMeta.hasNext()) {
					iMetadataItem currMeta = (iMetadataItem) itChildMeta
							.next();
					
					MetadataItem currentItem = (MetadataItem) currMeta;
					this.recursiveSave(currentItem);
				}
				//Save the node
				if (p_itemNode.getParent() != null){
					p_itemNode.getParent().save();
				}
			}
			else{ 
				//Node does not exist in the repository
				Node metadataNode = p_session.getRootNode().addNode(this.p_identifier);
				for (int i = 0; i < p_props.size(); i++) {
					iMetadataProperty prop = p_props.get(i);
					setNodeProperty(metadataNode,(MetadataProperty) prop);
				}
				
				Iterator<iMetadataItem> itChildMeta = this.p_children.iterator();
				while (itChildMeta.hasNext()) {
					iMetadataItem currMeta = (iMetadataItem) itChildMeta
							.next();
					
					MetadataItem currentItem = (MetadataItem) currMeta;
					this.recursiveSave(currentItem);
				}
				metadataNode.save();
				
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (MetadataException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * 
	 * Recursively saves the {@link MetadataItem}
	 * 
	 * @param item The item to save
	 * 
	 * @throws MetadataException If something goes wrong
	 * 
	 */
	private void recursiveSave(MetadataItem item) throws MetadataException{
		
		try {
			Node currentNode = item.getNode();
			if (currentNode != null){
				for (int i = 0; i < p_props.size(); i++){
					MetadataProperty prop = (MetadataProperty) p_props.get(i);
					setNodeProperty(p_itemNode, prop);
				}
				Iterator<iMetadataItem> itChildMeta = this.p_children.iterator();
				while (itChildMeta.hasNext()) {
					iMetadataItem currMeta = (iMetadataItem) itChildMeta
							.next();
					
					MetadataItem currentItem = (MetadataItem) currMeta;
					currentItem.recursiveSave(currentItem);
				}
			}
			else{ //Create in the repository
				Node metadataNode = p_session.getRootNode().addNode(this.p_identifier);
				//Set the value
				for (int i = 0; i < p_props.size(); i++){
					iMetadataProperty prop = p_props.get(i);
					setNodeProperty(metadataNode,(MetadataProperty) prop);
				}
				Iterator<iMetadataItem> itChildMeta = this.p_children.iterator();
				while (itChildMeta.hasNext()) {
					iMetadataItem currMeta = (iMetadataItem) itChildMeta
							.next();
					
					MetadataItem currentItem = (MetadataItem) currMeta;
					this.recursiveSave(currentItem);
				}
			}
		}  catch (RepositoryException e) {
			throw new MetadataException(e);
		}
	}

	@Override
	public void setMetadataProperty(iMetadataProperty prop) {
		//Only set valid properties
		this.p_props.add((MetadataProperty)prop);
		
	}
	
	/**
	 * 
	 * Retrieves the node 
	 * 
	 * @return
	 */
	public Node getNode(){
		return p_itemNode;
	}

	
	/**
	 * 
	 * Creates a {@link iMetadataProperty} from an existing {@link Property}
	 * 
	 * @param prop The property to use as base
	 * 
	 * @return a {@link iMetadataProperty}
	 */
	@SuppressWarnings("deprecation")
	private iMetadataProperty createMetadataProperty(Property prop) throws MetadataException
	{
		try {
			if (prop.getType() == PropertyType.STRING)
				return new MetadataProperty(prop.getName(), prop.getString());
			if (prop.getType() == PropertyType.BOOLEAN)
				return new MetadataProperty(prop.getName(), prop.getBoolean());
			if (prop.getType() == PropertyType.LONG)
				return new MetadataProperty(prop.getName(), prop.getLong());
			if (prop.getType() == PropertyType.DATE)
				return new MetadataProperty(prop.getName(), prop.getDate().getTime());
			if (prop.getType() == PropertyType.BINARY)
				return new MetadataProperty(prop.getName(), prop.getStream());
			if (prop.getType() == PropertyType.REFERENCE)
				return new MetadataProperty(prop.getName(), new MetadataItem(prop.getNode(),null,p_allMetadataConfigurations));
			
			//FIXME: Aqui tenho de ver como descobrir qual o tipo de nó que quero
			
		} catch (RepositoryException e) {
			throw new MetadataException(e);
		}
		return null;
	}
	
	/**
	 * 
	 * Sets the property value of a {@link Node} as the value of
	 * a {@link MetadataProperty}  
	 * 
	 * @param node The node to the set the property
	 * @param prop The property to retrieve the value
	 * 
	 * @throws MetadataException
	 */
	@SuppressWarnings("deprecation")
	private void setNodeProperty(Node node, MetadataProperty prop) throws MetadataException
	{
		try {
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.BINARY.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueBinary());
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.BOOLEAN.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueBoolean());
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.STRING.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueString());
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.LONG.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueLong());
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.DATE.name()){
				Calendar cal = Calendar.getInstance();
				cal.setTime(prop.getValueDate());
				node.setProperty(prop.getPropertyIdentifier(), cal);
			}
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.REFERENCE.name()){
				iMetadataItem currentItem = prop.getReference();
				Node value = p_session.getRootNode().getNode(currentItem.getID());
				node.setProperty(prop.getPropertyIdentifier(), value);
			}
				
			
				
		} catch (Exception e) {
			throw new MetadataException(e);
		}
		
	}
	

	@Override
	public boolean exists() {
		if (p_itemNode != null) 
			return true;
		else
			return false;
	}
	
	@Override
	public String toString(){
		return this.getName() + " - " + this.getID();
	}
	
}
