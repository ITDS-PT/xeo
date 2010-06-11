/**
 * 
 */
package netgest.io.metadata;

import java.util.List;

import netgest.io.iFile;
import netgest.io.iFileList;

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

	/**
	 * 
	 * Retrieves a reference to a file
	 * 
	 * @param fileID The file identifier
	 * 
	 * @return A reference to the file
	 */
	public iFile getFile(String fileID);
	
	
	/**
	 * 
	 * Creates a new (empty) file and returns a reference to it
	 * 
	 * @param identifierPath An identifier for the file (could be path, database
	 * id, etc...)
	 * @param fileName The name of the file
	 * 
	 * @return A reference to the created file
	 */
	public iFile createFile(String identifier, String fileName);
	
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
	public iFileList searchFiles(String query, List<iSearchParameter> properties);
	
		
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
    
}
