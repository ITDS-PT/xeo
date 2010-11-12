package netgest.io.jcr;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.nodetype.ConstraintViolationException;

import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.io.metadata.MetadataException;
import netgest.io.metadata.iMetadataItem;
import netgest.io.metadata.iMetadataProperty;
import netgest.io.metadata.iMetadataProperty.METADATA_TYPE;

/**
 * 
 * Implementation of a Metadata item for a JCR repository
 * 
 * @author Pedro
 *
 */
/**
 * @author PedroRio
 *
 */
public class MetadataItem implements iMetadataItem {

	
	
	/**
	 * The JCR node representing this item
	 */
	private Node p_itemNode;
	
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
	
	public MetadataItem(Node repNode, MetadataNodeConfig config){
		this.p_itemNode = repNode;
		this.p_configuration = config;
		initEmpty();
	}
	
	public MetadataItem(MetadataNodeConfig config){
		this.p_itemNode = null;
		this.p_configuration = config;
		initEmpty();
	}
	
	public MetadataItem(String identifier, Session session, MetadataNodeConfig config){
		this.p_itemNode = null;
		this.p_identifier = identifier;
		this.p_session = session;
		this.p_configuration = config;
		initEmpty();
	}
	
	/**
	 * Initializes the fields with empty values
	 */
	private void initEmpty(){
		this.p_props = new LinkedList<iMetadataProperty>();
		this.p_children = new LinkedList<iMetadataItem>();
		this.p_childrenRemove = new HashSet<String>();
		if (p_configuration != null)
			this.p_allMetadataConfigurations = p_configuration.getAllRepositoryConfigurations();
		
	}
	
	
	@Override
	public void addChild(iMetadataItem newChild) {
		this.p_children.add(newChild);
		((MetadataItem) newChild).setParent(this);
	}

	@Override
	public List<iMetadataItem> getChildren() {
		List<iMetadataItem> result = new Vector<iMetadataItem>();
		if (p_itemNode != null){
			try {
				NodeIterator itNodes = p_itemNode.getNodes();
				while (itNodes.hasNext()) {
					Node currentNode = (Node) itNodes.next();
					result.add(new MetadataItem(currentNode,p_configuration));
				}
			} 
			catch (RepositoryException e) {
				e.printStackTrace();
			}
			//Check if other children were added to the metadata item
			if (!p_children.isEmpty())
			{
				Iterator<iMetadataItem> lstItems = p_children.iterator();
				while (lstItems.hasNext()) {
					iMetadataItem curreItem = (iMetadataItem) lstItems.next();
					result.add(curreItem);
				}
			}
			return result;
		}
		else
			return p_children;
	}

	@Override
	public String getID() 
	{
		if (p_itemNode != null)
			try {
				return p_itemNode.getName();
			} catch (RepositoryException e) {
				//Return null;
			}
		return p_identifier;
	}

	@Override
	public iMetadataItem getParent() {
		if (p_itemNode != null){
			try {
				//Checks if we are in a valid node (JCR-wise) but not a valid Metadata location
				if (p_configuration != null){
					MetadataNodeConfig parent = p_allMetadataConfigurations.get(getType());
					return new MetadataItem(p_itemNode.getParent(),parent);
				}
				else
					return null; 
			}  catch (ItemNotFoundException e ){
				return null;
			}  catch (RepositoryException e) { 
				e.printStackTrace();
			}
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
				//Check if other properties have been set
				if (!p_props.isEmpty()){
					Iterator<iMetadataProperty> it = p_props.iterator();
					while (it.hasNext()) {
						iMetadataProperty currProperty = (iMetadataProperty) it
								.next();
						result.add(currProperty);
					}
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

	/**
	 * 
	 * Sets the parent item of this {@link MetadataItem}
	 * 
	 * @param parent The parent item
	 */
	public void setParent(iMetadataItem parent){
		p_parent = parent;
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
				
				Node metadataNode = null;
				if (getParent() == null){
				
					//Build the appropriate ID (may the the "queryToReach" from the XML definition)
					String id = "";
					if (p_configuration.getQueryToReach() != null)
						id = p_configuration.getQueryToReach() + "/" + p_identifier;
					else
						id = p_identifier;
					
					id = FileJCR.cleanJCRId(id);
					
					metadataNode = p_session.getRootNode().addNode(id,p_configuration.getNodeType());
					p_itemNode = metadataNode;
				}
				else
				{
					MetadataItem item = (MetadataItem) getParent();
					metadataNode = item.getNode().addNode(getID(),p_configuration.getNodeType());
					p_itemNode = metadataNode;
				}
					 
				
				for (int i = 0; i < p_props.size(); i++) {
					iMetadataProperty prop = p_props.get(i);
					setNodeProperty(metadataNode,(MetadataProperty) prop);
				}
				
				
				
				//Save the node, so that children can be created
				if (metadataNode.getParent() != null)
					metadataNode.getParent().save();
				
				Iterator<iMetadataItem> itChildMeta = this.p_children.iterator();
				while (itChildMeta.hasNext()) {
					iMetadataItem currMeta = (iMetadataItem) itChildMeta
							.next();
					MetadataItem currentItem = (MetadataItem) currMeta;
					this.recursiveSave(currentItem);
				}
				
				//Save the node so that the child nodes are persisted
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
				
				List<iMetadataProperty> lstProps = item.getProperties();
				Iterator<iMetadataProperty> itProps = lstProps.iterator();
				while (itProps.hasNext()) {
					iMetadataProperty currProp = (iMetadataProperty) itProps
							.next();
					setNodeProperty(currentNode,(MetadataProperty)currProp);
				}
				
				List<iMetadataItem> childMeta = item.getChildren();
				if (childMeta != null){
					Iterator<iMetadataItem> itChildMeta = childMeta.iterator();
					while (itChildMeta.hasNext()) {
						iMetadataItem currMeta = (iMetadataItem) itChildMeta
								.next();
						
						MetadataItem currentItem = (MetadataItem) currMeta;
						currentItem.recursiveSave(currentItem);
					}
				}
			}
			else{ //Create in the repository
				
				//Get the Path to the Item
				/*String id = FileJCR.getFullPathFromMetadataItem(item);
				id = FileJCR.cleanJCRId(id);*/
				
				MetadataItem parent = (MetadataItem) item.getParent();
				Node parentNode = parent.getNode();
				
				Node metadataNode = parentNode.addNode(item.getID());
				item.setJCRNode(metadataNode);
				//Set the value for the properties
				
				List<iMetadataProperty> lstProps = item.getProperties();
				Iterator<iMetadataProperty> itProps = lstProps.iterator();
				while (itProps.hasNext()) {
					iMetadataProperty currProp = (iMetadataProperty) itProps
							.next();
					setNodeProperty(metadataNode,(MetadataProperty)currProp);
				}
				
				//Process the child elements of this 
				List<iMetadataItem> childMetaNoExist = item.getChildren();
				if (childMetaNoExist != null)
				{
					Iterator<iMetadataItem> itChildMeta = childMetaNoExist.iterator();
					while (itChildMeta.hasNext()) {
						iMetadataItem currMeta = (iMetadataItem) itChildMeta
								.next();
						
						MetadataItem currentItem = (MetadataItem) currMeta;
						this.recursiveSave(currentItem);
					}
				}
			}
		}  catch (RepositoryException e) {
			throw new MetadataException(e);
		}
	}

	@Override
	public void setMetadataProperty(iMetadataProperty prop) {
		//Only set valid properties
		
		for (int i = 0; i < p_props.size(); i++)
			if(p_props.get(i).getPropertyIdentifier().equals(prop.getPropertyIdentifier()))
			{
				p_props.set(i, prop);
				return;
			}
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
			
			try{
				prop.getValue();
				if (prop.getType() == PropertyType.STRING)
					return new MetadataProperty(prop.getName(), prop.getString());
				else if (prop.getType() == PropertyType.BOOLEAN)
					return new MetadataProperty(prop.getName(), prop.getBoolean());
				else if (prop.getType() == PropertyType.LONG)
					return new MetadataProperty(prop.getName(), prop.getLong());
				else if (prop.getType() == PropertyType.DATE)
					return new MetadataProperty(prop.getName(), prop.getDate().getTime());
				else if (prop.getType() == PropertyType.BINARY)
					return new MetadataProperty(prop.getName(), prop.getStream());
				else if (prop.getType() == PropertyType.REFERENCE)
					return new MetadataProperty(prop.getName(), new MetadataItem(prop.getNode(),p_configuration));
				else if (prop.getType() == PropertyType.NAME)
					return new MetadataProperty(prop.getName(), prop.getString());
				else if (prop.getType() == PropertyType.PATH)
					return new MetadataProperty(prop.getName(), prop.getString());
				
			}
			catch (ValueFormatException e){
				
				Value[] vals = prop.getValues();
				if (prop.getType() == PropertyType.STRING)
					return new MetadataProperty(prop.getName(), vals, METADATA_TYPE.STRING_ARRAY);
					else if (prop.getType() == PropertyType.NAME)
					return new MetadataProperty(prop.getName(), vals, METADATA_TYPE.STRING_ARRAY);
				else if (prop.getType() == PropertyType.PATH)
					return new MetadataProperty(prop.getName(), vals, METADATA_TYPE.STRING_ARRAY);
			}
			
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
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.BOOLEAN.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueBoolean());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.STRING.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueString());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.LONG.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueLong());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.DATE.name()){
				Calendar cal = Calendar.getInstance();
				cal.setTime(prop.getValueDate());
				node.setProperty(prop.getPropertyIdentifier(), cal);
			}
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.REFERENCE.name()){
				iMetadataItem currentItem = prop.getReference();
				Node value = p_session.getRootNode().getNode(currentItem.getID());
				node.setProperty(prop.getPropertyIdentifier(), value);
			}
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.STRING_ARRAY.name()){
				node.setProperty(prop.getPropertyIdentifier(), prop.getValuesString());
			}
				
		} catch (ConstraintViolationException e) {
			//This situation means that a particular 
			//implementation specific property was being updated
			//which can't happen, but since it already exists in the node
			//we let it continue,
			e.printStackTrace();
		}
		catch (RepositoryException e){
			throw new MetadataException(e);
		} catch (netgest.io.metadata.ValueFormatException e) {
			throw new MetadataException(e);
		}
	}
	
	
	/**
	 * 
	 * Sets the node of the {@link MetadataItem}
	 * 
	 * @param node The JCR node
	 */
	public void setJCRNode(Node node){
		p_itemNode = node;
	}
	
	
	/**
	 * 
	 * Sets the identifier of the node
	 * 
	 * @param identifier
	 */
	public void setIdentifier(String identifier){
		p_identifier = identifier;
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
		return this.getID();
	}
	
	
}
