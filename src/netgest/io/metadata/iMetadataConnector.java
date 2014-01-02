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
	public void addMetadataItem(iMetadataItem parent, iMetadataItem itemToAdd) throws MetadataException;
	
	/**
	 * 
	 * Adds a new {@link iMetadataItem} as child of an existing parent
	 * or as root metadata item
	 * 
	 * @param parent The parent identifier (can be null, to add to root)
	 * @param itemToAdd The item to add
	 */
	public void addMetadataItem(String parentIdentifier, iMetadataItem itemToAdd) throws MetadataException;
	
	/**
	 * 
	 * Removes a metadata item, which must not have any children
	 * 
	 * @param itemId The identifier of the {@link iMetadataItem}
	 * 
	 * @throws MetadataException If an error occurs while removing the 
	 * 
	 */
	public void removeMetadataItem(String itemId) throws MetadataException;

	/**
	 * 
	 * Creates a new empty metadata item with a given identifier and 
	 *  name a returns a reference to it
	 * 
	 * @param name A name for the metadata item
	 * 
	 * @param type The type of the metadata item
	 * 
	 * @return A reference to an empty identifier 
	 */
	public iMetadataItem createMetadataItem(String name, String type) throws MetadataException;
	
	
	/**
	 * 
	 * Creates a new empty metadata item with an identifier
	 *  and returns a reference to it
	 * 
	 * 
	 * @param id An idenfifier for the metadata item
	 * 
	 * @return A reference to an empty identifier 
	 */
	public iMetadataItem createMetadataItem(String id) throws MetadataException;
	
	
	
	/**
	 * 
	 * Creates an {@link iMetadataProperty} that can be used to set as a value of
	 * a {@link iMetadataItem}
	 * 
	 * 
	 * 
	 * @param propId The identifier of the property
	 * @param propValue The value of the property (if the type is a String, a string object should be passed as parameter) 
	 * @param propType The data type of the property, from {@link iMetadataProperty.METADATA_TYPE}
	 * 
	 * @return A reference to an {@link iMetadataProperty} 
	 */
	public iMetadataProperty createProperty(String propId, Object propValue, iMetadataProperty.METADATA_TYPE propType);
	
	
	/**
	 * 
	 * Creates an {@link iMetadataProperty} that can be used to set as a value of
	 * a {@link iMetadataItem}, the property has multiple values
	 * 
	 * @param propId The identifier of the property
	 * @param propValue The array of values of the property (if the type is a String, a String[] should be passed
	 * as parameter) 
	 * 
	 * @param propType The data type of the property, from {@link iMetadataProperty.METADATA_TYPE}
	 * 
	 * @return A reference to an {@link iMetadataProperty} 
	 */
	public iMetadataProperty createProperty(String propId, Object[] propValues, iMetadataProperty.METADATA_TYPE propType);
	
}
