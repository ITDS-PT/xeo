/**
 * 
 */
package netgest.bo.def;

import java.util.HashMap;
import java.util.List;

import netgest.bo.configUtils.FileMetadataProperty;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.configUtils.NodePropertyDefinition;
import netgest.bo.runtime.boRuntimeException;
import netgest.io.iFileConnector;

/**
 * 
 * Represents the XML definition a document attribute
 * The document attribute is a {@link boDefAttribute.ATTRIBUTE_BINARYDATA}
 * which is stored in a Java Content Repository and has a set of properties
 * not found in a normal attribute binary data
 * 
 * @author PedroRio
 *
 */
public interface boDefDocument 
{
	/**
	 * 
	 * Retrieve the repository name
	 * in which the file(s) of this document should be stored
	 * 
	 * @return The name of the repository (if null, the default repository is used)
	 */
	public String getRepositoryName();
	
	/**
	 * Whether or not this attribute can add new files to repository
	 * or can only be associated with existing files
	 * 
	 * @return True if files can be added through this attribute and false
	 * otherwise
	 */
	public boolean canAddFiles();
	
	/**
	 * 
	 * Whether or not this attribute can add metadata items to the repository
	 * or can only be associated with existing metadata items
	 * 
	 * @return True if metadata can be added to the repository or false otherwise
	 */
	public boolean canAddMetadata();
	
	/**
	 * 
	 * Whether or not this attribute should have some associated 
	 * metadata when saved
	 * 
	 * @return True if metadata is required, or false otherwise
	 */
	public boolean isMetadataRequired();
	
	/**
	 * 
	 * If this attribute represents a folder (collection of files) or a file
	 * 
	 * @return True if the attribute represents a folder, and false if it
	 * represents a file
	 */
	public boolean isFolder();
	
	/**
	 * 
	 * In case this configuration exists, it overrides the default settings in
	 * <code>boConfig.xml</code>
	 * 
	 * @return A {@link FileNodeConfig} with the settings for this attribute
	 * or null to use default settings
	 */
	public FileNodeConfig getFileNodeConfig();
	
	/**
	 * 
	 * In case this configuration exists, it overridde the default settings
	 * in <code>boConfig.xml</code>
	 * 
	 * @return A {@link FolderNodeConfig} with the settings for this attribute
	 * or null to use the default settings
	 */
	public FolderNodeConfig getFolderNodeConfig();
	
	/**
	 * 
	 * Retrieves the list of metadata configurations for this item, it they exist
	 * they override the default settings in <code>boConfig.xml</code>
	 *  
	 * @return A map with all metadata configurations for this node, or null
	 * if the default settings should be used
	 */
	public HashMap<String,MetadataNodeConfig> getMetadataConfigs();
	
	/**
	 * 
	 * Retrieves the file connector associated to this attributeBinaryData
	 * 
	 * @return An {@link iFileConnector} implementation
	 * 
	 * @throws boRuntimeException
	 */
	public iFileConnector getFileConnector() throws boRuntimeException;
	
	
	/**
	 * 
	 * Retrieves the List of Metadata Properties that the file should/must have
	 * 
	 * 
	 * @return A list of {@link FileMetadataProperty} instances
	 * with the properties this File must have
	 */
	public List<NodePropertyDefinition> getMetadataPropertiesFile(); 
}
