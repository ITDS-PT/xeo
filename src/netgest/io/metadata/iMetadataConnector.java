package netgest.io.metadata;

import java.util.List;
import java.util.Map;

/**
 *
 *
 * Interface which contains the methods necessary to have access to
 * the existing metadata items in the repository 
 *
 * @author PedroRio
 *
 */
public interface iMetadataConnector {
	
	
	/**
	 * Retrieve a <code> {@link iMetadataItem} </code> given
	 * its identifier
	 * 
	 * @param identifier The identifier of the item
	 * 
	 * @return The {@link iMetadataItem} identifier by <code>identifier</code>
	 */
	public iMetadataItem getMetadataItem(String identifier);
	
	/**
	 * 
	 * Retrieve the list of all metadata items (first level items,
	 * if the results have children one has to use {@link iMetadataItem#getChildren()}
	 * to reach them.
	 * 
	 * @return A list with all metadata items
	 */
	public List<iMetadataItem> getMetadataAllItems();
	
	/**
	 * 
	 * Retrieve all metadata items of a given type (only first level items
	 * not children items)
	 * 
	 * @param type The type of the items
	 * 
	 * @return A list of {@link iMetadataItem} items with the given
	 *  <code>type</code>
	 */
	public List<iMetadataItem> getMetadataItemsByType(String type);
	
	/**
	 * 
	 * Search for all {@link iMetadataItem} that match a particular query and 
	 * parameters
	 * 
	 * @param query The query to execute
	 * @param paramsAnd The set of key/value pairs to check as properties in the query
	 * with the logical AND clause
	 * @param paramsOr The set of key/value pairs to check as properties in the query
	 * with the local OR clause
	 * 
	 * @return The list of all {@link iMetadataItem} that match the search query
	 */
	public List<iMetadataItem> searchMetadataItems(String query, 
			Map<String,iSearchParameter> paramsAnd,
			Map<String,iSearchParameter> paramsOr);
	
	/**
	 * 
	 * Adds a new {@link iMetadataItem} as child of an existing parent
	 * or as root metadata item
	 * 
	 * @param parent The parent to add (can be null)
	 * @param itemToAdd The item to add
	 */
	public void addMetadataItem(iMetadataItem parent, iMetadataItem itemToAdd);
	
	/**
	 * 
	 * Adds a new {@link iMetadataItem} as child of an existing parent
	 * or as root metadata item
	 * 
	 * @param parent The parent identifier (can be null, to add to root)
	 * @param itemToAdd The item to add
	 */
	public void addMetadataItem(String parentIdentifier, iMetadataItem itemToAdd);
	
	/**
	 * 
	 * Removes a metadata item, which must not have any children
	 * 
	 * @param itemId The identifier of the {@link iMetadataItem}
	 * 
	 */
	public void removeMetadataItem(String itemId);
	
}
