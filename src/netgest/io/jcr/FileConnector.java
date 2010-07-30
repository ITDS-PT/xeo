/**
 * 
 */
package netgest.io.jcr;

import java.util.List;
import java.util.Map;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.io.iFile;
import netgest.io.iFileConnector;
import netgest.io.iFileException;
import netgest.io.iFileList;
import netgest.io.iFilePermissionDenied;
import netgest.io.metadata.iMetadataConnector;
import netgest.io.metadata.iSearchParameter;

/**
 * @author PedroRio
 *
 */
public class FileConnector implements iFileConnector {

	
	/**
	 * Represents a connector a given JCR Repository
	 */
	private Session sessionJCR;
	private FileNodeConfig fileConfig;
	private Map<String,MetadataNodeConfig> metaConfig;
	private FolderNodeConfig folderConfig;
	
	public FileConnector(){}
	
	public FileConnector(Session session, FileNodeConfig fileConf,
			FolderNodeConfig folderConf, Map<String,MetadataNodeConfig> metaConf) 
	{
		sessionJCR = 	session;
		fileConfig = 	fileConf;
		folderConfig = 	folderConf;
		metaConfig = metaConf;
	}
	
	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#createIFile(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public iFile createIFile(String fileName, String identifier,
			boolean isFolder) throws iFileException{
		return new FileJCR(null, fileConfig, folderConfig, sessionJCR, metaConfig, isFolder, identifier);
		
	}

	
	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#createIFile(java.lang.String, boolean)
	 */
	@Override
	public iFile createIFile(String filename, boolean isFolder) throws iFileException{
		return new FileJCR(null, fileConfig, folderConfig, sessionJCR, metaConfig, isFolder, filename);
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#getIFile(java.lang.String)
	 */
	@Override
	public iFile getIFile(String fileID) throws iFileException{
		try {
			if (fileID == null)
				return null;
			
			Node node = this.sessionJCR.getRootNode().getNode(fileID);
			boolean isFolder = false;
			if (node.isNodeType(folderConfig.getNodeType()))
				isFolder = true;
			return new FileJCR(
					node, //The node to wrap the file
					fileConfig, //The fileNode config
					folderConfig, //The folderNode config
					sessionJCR, //The session with the repository
					metaConfig, 
					isFolder, 
					fileID);
		}  catch (RepositoryException e) {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#open()
	 */
	@Override
	public boolean open() {
		return true;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#open(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public boolean open(String username, String password,
			String connectionString) {
		return true;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#searchIFiles(java.lang.String, java.util.List)
	 */
	@Override
	public iFileList searchIFiles(String query,
			List<iSearchParameter> properties) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#supportsMetadata()
	 */
	@Override
	public boolean supportsMetadata() {
		return true;
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#supportsVersioning()
	 */
	@Override
	public boolean supportsVersioning() {
		if (sessionJCR.getRepository().
				getDescriptor(Repository.OPTION_VERSIONING_SUPPORTED) != null)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean deleteIFile(String fileID) throws iFilePermissionDenied,
			iFileException {
		try {
			Node currentNode = sessionJCR.getRootNode().getNode(fileID);
			Node parent = currentNode.getParent();
			currentNode.remove();
			parent.save();
			return true;
		} catch (RepositoryException e) {
			throw new iFileException(e);
		}
	}

	@Override
	public iMetadataConnector getMetadataConnector() {
		// TODO Auto-generated method stub
		return null;
	}
	
	

}
