package netgest.io.metadata;

import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.io.jcr.MetadataItem;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void addMetadataItem(String parentIdentifier, iMetadataItem itemToAdd) {
		// TODO Auto-generated method stub

	}

	@Override
	public iMetadataItem createMetadataItem(String name, String identifier) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public iMetadataItem createMetadataItem(String name) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public iMetadataItem createMetadataItem(String name, String identifier, String type){
		return null;
	}

	@Override
	public List<iMetadataItem> getMetadataAllItems() {
		// TODO Auto-generated method stub
		return null;
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
			return new MetadataItem(metaItemNode, current, p_configs);
			
		} catch (PathNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
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
		try {
			Node current = p_session.getRootNode().getNode(itemId);
			if (current != null){
				Node parent = current.getParent();
				current.remove();
				parent.save();
			}
			else
				throw new MetadataException("Item " + itemId + " does not exist");
		} catch (RepositoryException e) {
			e.printStackTrace();
			throw new MetadataException(e);
		}
	}

	@Override
	public List<iMetadataItem> searchMetadataItems(String query,
			Map<String, iSearchParameter> paramsAnd,
			Map<String, iSearchParameter> paramsOr) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public iMetadataProperty createProperty(String propName, Object propValue,
			METADATA_TYPE propType) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public iMetadataProperty createProperty(String propName,
			Object[] propValues, METADATA_TYPE propType) {
		// TODO Auto-generated method stub
		return null;
	}

}
