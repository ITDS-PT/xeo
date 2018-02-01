/**
 * 
 */
package netgest.io;

import java.util.List;
import java.util.Map;

import javax.jcr.Session;

import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.runtime.AttributeHandler;
import netgest.io.metadata.MetadataConnector;
import netgest.io.metadata.iMetadataConnector;
import netgest.io.metadata.iSearchParameter;

/**
 * 
 * Represents a connector to manage files in a XEO instance.
 * The {@link iFileConnector} can be used to retrieve existing
 * files, created new ones and search for files
 * 
 * @author PedroRio
 *
 */
public interface iFileConnector 
{

	public void initializeFileConnector(Session session, FileNodeConfig fileConf,
			FolderNodeConfig folderConf, Map<String,MetadataNodeConfig> metaConf);
	/**
	 * 
	 * Retrieves a reference to a {@link iFile}
	 * 
	 * @param fileID The {@link iFile} identifier
	 * 
	 * @return A reference to the {@link iFile}
	 */
	public iFile getIFile(String fileID) throws iFileException;;
	
	
	/**
	 * 
	 * Creates a new {@link iFile} and returns a reference to it
	 * 
	 * @param fileName The name of the {@link iFile}
	 * 
	 * @param identifier An identifier for the {@link iFile} (could be path, database
	 * id, etc...)
	 * 
	 * @param isFolder If the created {@link iFile} is a folder
	 * (if false, creates a file)
	 * 
	 * @param inTransaction If the file should be created within a transaction
	 * 
	 * @return A reference to the created {@link iFile}
	 */
	public iFile createIFile(String fileName, String identifier, boolean isFolder, boolean inTransactiom) throws iFileException;;
	
	/**
	 * 
	 * Creates a new {@link iFile} and returns a reference to it
	 * The file is created in the default path
	 * 
	 * @param filename The name of the {@link iFile} to create
	 * 
	 * @param isFolder If the created {@link iFile} is a folder
	 * (if false, creates a file)
	 * 
	 * @param inTransaction If the file should be created within a transaction
	 * 
	 * @return A reference to the created file
	 */
	public iFile createIFile(String filename, boolean isFolder, boolean inTransaction) throws iFileException;
	
	
	
	/**
	 * 
	 * Create an iFile in text context of an object's attribute
	 * 
	 * @param filename The name of the file
	 * @param context The context (object attribute) for the file
	 * 
	 * @return The iFile
	 */
	public iFile createIFileInContext(iFile file, AttributeHandler context);
	
	/**
	 * 
	 * Issues a query and retrieves all the files that match
	 * that query (paginated results)
	 * 
	 * @param query The query to execute
	 * @param properties Map of properties to narrow the search, example
	 * createDate = '01-01-2009' its up to the implementation to recognize the
	 * properties
	 * 
	 * @return A paginated list of files that match the query (empty list if none)
	 * 
	 */
	public iFileList searchIFiles(String query, List<iSearchParameter> properties);
	
		
	/**
     * 
     * Whether this iFile supports versioning or not
     * 
     * @return True if the iFile supports versioning and false otherwise
     */
    public boolean supportsVersioning();
    
    /**
     * 
     * Whether this iFile supports metadata or not
     * 
     * @return True if the iFile supports metadata and false otherwise
     */
    public boolean supportsMetadata();
    
    
    /**
     * 
     * Opens a connection to the file connector with the default settings
     * 
     * @return True if the connection is opened and false otherwise
     */
    public boolean open();
    
    
    /**
     * 
     * Opens a connection to the file connector with specific settings
     * 
     * @param username The username for the connection
     * @param password The password of the username
     * @param connectionString A connection string if one is required
     * 
     * @return True if the connection is established and false otherwise
     */
    public boolean open(String username, String password, String connectionString);
    
    /**
     * 
     * Deletes an {@link iFile} given its identifier
     * 
     * @param fileID The file identifier
     * 
     * @return True if the file was deleted and false otherwise
     * 
     * @throws iFilePermissionDenied If the user does not have permission to delete the file
     * @throws iFileException If any other error deleting the file occurs
     */
    public boolean deleteIFile(String fileID) throws iFilePermissionDenied, iFileException;
    
    
    /**
     * 
     * Retrieves the {@link MetadataConnector} associated to the {@link iFileConnector}
     * 
     * @return A reference to the metadata connector
     */
    public iMetadataConnector getMetadataConnector();
    	
    
}
