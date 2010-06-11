package netgest.io.metadata;

import java.util.List;

/**
 * 
 * Represents the metadata of a given item in the content repository
 * 
 * @author PedroRio
 *
 */
public interface iMetadataItem 
{
		/**
		 * 
		 * Retrieves the name of this metadata item
		 * 
		 * @return The name of the metadata item
		 */
		public String getName();
		
		/**
		 * Retrieves an identifier for this metadata item
		 * which is used to create relations with other metadata
		 * items
		 * 
		 * @return An identifier of the item in the repository
		 */
		public String getID();
		
		/**
		 * 
		 * Retrieves the type of this Metadata item
		 * which represents a category/class of this Metadata item
		 * Mainly intended for grouping purposes
		 * 
		 * @return A category for the item, or null none
		 */
		public String getType();
		
		/**
		 * 
		 * Retrieve the properties for this item
		 * 
		 * @return A list of properties, if the there are no properties
		 * returns an empty list
		 */
		public List<iMetadataProperty> getProperties();
		
		/**
		 * 
		 * Retrieve a property by its name
		 * 
		 * @param name The name of the property
		 * 
		 * @return The {@link iMetadataProperty} with the given name
		 * or null if no property by that name exists
		 */
		public iMetadataProperty getPropertyByName(String name);
		
		/**
		 * 
		 * Add or replace a metadata property in this {@link iMetadataItem}
		 * 
		 * @param name The name of the property to add/replace
		 * @param prop The property to add/replace
		 * 
		 */
		public void setMetadataProperty(String name, iMetadataProperty prop);
		
		/**
		 * 
		 * Retrieve the parent item of this {@link iMetadataItem}
		 * 
		 * @return The parent {@link iMetadataItem} of this item
		 * or null if it has no parent
		 */
		public iMetadataItem getParent();
		
		/**
		 * 
		 * Retrieve the list children items of this item
		 * 
		 * @return A list of items related to this type
		 */
		public List<iMetadataItem> getChildren();
		
		/**
		 * 
		 * Adds a new child to the list of children of this
		 * item
		 * 
		 * @param newChild The new child
		 */
		public void addChild(iMetadataItem newChild);
		
		/**
		 * 
		 * Removes a child item given its identifier
		 * 
		 * @param childToRemoveIdentifier The identifier
		 * of the child to remove
		 */
		public void removeChild(String childToRemoveIdentifier);
		
		
		/**
		 * Saves the metadata item (updates/creates the record in the database,
		 * filesystem or content repository, etc...) 
		 */
		public void save();
}
