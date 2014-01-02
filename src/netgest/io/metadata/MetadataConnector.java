package netgest.io.metadata;

import java.io.InputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import netgest.bo.boConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.localizations.MessageLocalizer;
import netgest.io.jcr.MetadataItem;
import netgest.io.jcr.MetadataProperty;
import netgest.io.metadata.iMetadataProperty.METADATA_TYPE;

/**
 * 
 * Implements the {@link iMetadataConnector} interface to create
 * new Metadata items to the repository, create empty items
 * 
 * @author Pedro
 *
 */
public class MetadataConnector implements iMetadataConnector {

	
	/**
	 * The set of definitions for this connector
	 */
	Map<String,MetadataNodeConfig> p_configs;
	
	/**
	 * A connection to the metadata repository
	 */
	Session p_session;
	
	
	/**
	 * 
	 * Public constructor for a new {@link MetadataConnector}
	 * 
	 * @param configs The set of different metadata items this connector accepts
	 * @param session A connection to the repository
	 */
	public MetadataConnector(Map<String,MetadataNodeConfig> configs, Session session){
		p_configs = configs;
		p_session = session;
	}
	
	
	@Override
	public void addMetadataItem(iMetadataItem parent, iMetadataItem itemToAdd) {
		parent.addChild(itemToAdd);
		parent.save();
	}

	@Override
	public void addMetadataItem(String parentIdentifier, iMetadataItem itemToAdd) {
		iMetadataItem parent = getMetadataItem(parentIdentifier);
		parent.addChild(itemToAdd);
		parent.save();
	}

	
	@Override
	public iMetadataItem createMetadataItem(String id) throws MetadataException {
		
		MetadataNodeConfig metaConfig = getDefaultMetadataConfig();
		try {
			MetadataItem itemToReturn = new MetadataItem(id,p_session, metaConfig);
			return itemToReturn;
		}  
		catch (Exception e) { throw new MetadataException(e); }
	}
	
	@Override
	public iMetadataItem createMetadataItem(String name, String type) throws MetadataException{
		
		MetadataNodeConfig metaConfig = getDefaultMetadataConfig();
		
		try {
			//Retrieve the parent node of the metadata node
			String query = metaConfig.getQueryToReach();
			Node parentNode;
			if (query != null)
				parentNode = p_session.getRootNode().getNode(query);
			else
				parentNode = p_session.getRootNode();
			
			//Add the new node to that parent (assuming it exists)
			Node metaNode = parentNode.addNode(name, metaConfig.getNodeType());
			MetadataItem itemToReturn = new MetadataItem(metaNode, metaConfig);
			return itemToReturn;
		}  
		catch (PathNotFoundException e) 
		{
			throw new MetadataException(MessageLocalizer.getMessage("COULD_NOT_CREATE_METADATA_ITEM_WITH_ID")+" '" 
					+ name + "' "+MessageLocalizer.getMessage("NO_PATH_TO_THE_ITEM"));
		}  catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<iMetadataItem> getMetadataAllItems() {
		Vector<iMetadataItem> result = new Vector<iMetadataItem>();
		Map<String,MetadataNodeConfig> configs = this.p_configs;
		Iterator<String> itMeta = configs.keySet().iterator();
		while (itMeta.hasNext())
		{
			String metaName = itMeta.next();
			MetadataNodeConfig currentConfig = p_configs.get(metaName);
			String queryToReach = currentConfig.getQueryToReach();
			try {
				Node parent = p_session.getRootNode().getNode(queryToReach);
				NodeIterator itNodes = parent.getNodes();
				while (itNodes.hasNext()){
					Node curr = itNodes.nextNode();
					iMetadataItem nodeAddResult = getMetadataItem(curr.getPath());
					result.add(nodeAddResult);
				}
			}  catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public iMetadataItem getMetadataItem(String identifier) {
		
		try {
			//Retrieve the node that represents the MetadataItem
			Node metaItemNode = p_session.getRootNode().getNode(identifier);
			//Get the node type, to find which configuration should be passed
			String metaItemType = metaItemNode.getPrimaryNodeType().getName();
			//Given the node type, retrieve the Metadata Node configuration
			MetadataNodeConfig current = p_configs.get(metaItemType);
			//Return an item with the given type
			return new MetadataItem(metaItemNode, current);
			
		} catch (PathNotFoundException e1){
			try
			{
				MetadataNodeConfig meta = getDefaultMetadataConfig();
				if (meta.getQueryToReach() != null){
					String id = meta.getQueryToReach() + "/" + identifier;
					Node metaItemNode = p_session.getRootNode().getNode(id);
					//Get the node type, to find which configuration should be passed
					String metaItemType = metaItemNode.getPrimaryNodeType().getName();
					//Given the node type, retrieve the Metadata Node configuration
					MetadataNodeConfig current = p_configs.get(metaItemType);
					//Return an item with the given type
					return new MetadataItem(metaItemNode, current);
				}
				
				
			}catch (PathNotFoundException e2){ /*Do nothing*/ }
			
			 catch (RepositoryException e) { e.printStackTrace(); }
		}
		catch (RepositoryException e) {	e.printStackTrace(); }
		return null;
	}

	@Override
	public List<iMetadataItem> getMetadataItemsByType(String type) {
		MetadataNodeConfig config = p_configs.get(type);
		String queryToReach = config.getQueryToReach();
		Vector<iMetadataItem> result = new Vector<iMetadataItem>();
		try {
			if (queryToReach != null){
				Node parent = p_session.getRootNode().getNode(queryToReach);
				NodeIterator it = parent.getNodes();
				while (it.hasNext()) {
					Node currentNode = (Node) it.next();
					iMetadataItem resultToAdd = getMetadataItem(currentNode.getPath());
					result.add(resultToAdd);
				}
				return result;
			}
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void removeMetadataItem(String itemId) throws MetadataException{
		Node current = null;
		try {
			current = p_session.getRootNode().getNode(itemId);
		}
		catch (PathNotFoundException e){
			
			MetadataNodeConfig metaConfig = getDefaultMetadataConfig();
			
			if (metaConfig.getQueryToReach() != null){
				try {
					current = p_session.getRootNode().getNode(metaConfig.getQueryToReach() +
							"/" + itemId);
				} catch (PathNotFoundException e1) {
					throw new MetadataException("Item " + itemId + " "+MessageLocalizer.getMessage("DOESNT_EXIST"));
				} catch (RepositoryException e1) {
					throw new MetadataException(e1);
				}
			}
			
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		try	{
			if (current != null){
				Node parent = current.getParent();
				current.remove();
				parent.save();
			}
			else
				throw new MetadataException("Item " + itemId + " "+MessageLocalizer.getMessage("DOESNT_EXIST"));
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new MetadataException(e);
		}
	}

	@Override
	public List<iMetadataItem> searchMetadataItems(String query,
			Map<String, iSearchParameter> paramsAnd,
			Map<String, iSearchParameter> paramsOr) {
		return new Vector<iMetadataItem>();
	}


	@Override
	public iMetadataProperty createProperty(String propName, Object propValue,
			METADATA_TYPE propType) {
		switch (propType){
			case BOOLEAN: return new MetadataProperty(propName, (Boolean) propValue);
			case BINARY: return new MetadataProperty(propName, (InputStream) propValue);
			case TIME: return new MetadataProperty(propName, (Date) propValue);
			case DATE: return new MetadataProperty(propName, (Date) propValue);
			case DATETIME: return new MetadataProperty(propName, (Date) propValue);
			case STRING: return new MetadataProperty(propName, (String) propValue);
			case LONG: return new MetadataProperty(propName, (Long) propValue);
			case REFERENCE : return new MetadataProperty(propName, (iMetadataItem) propValue);
			case STRING_ARRAY : return new MetadataProperty(propName, (String[]) propValue);
		}
		return null;
	}


	@Override
	public iMetadataProperty createProperty(String propName,
			Object[] propValues, METADATA_TYPE propType) {
		switch (propType){
		case STRING: return new MetadataProperty(propName, (String[]) propValues);
	}
	return null;
	}
	
	/**
	 * 
	 * Retrieves the configuration for the default metadata node config
	 * 
	 * @return
	 */
	private MetadataNodeConfig getDefaultMetadataConfig(){
		//Find the repository with the definition for this MetadataItem
		RepositoryConfig repoConfig = boConfig.getApplicationConfig().getDefaultFileRepositoryConfiguration();
		//Get the definition for the meta data node configuration
		MetadataNodeConfig metaConfig = repoConfig.getDefaultMetadataConfig();
		
		return metaConfig;
	}
	
}
