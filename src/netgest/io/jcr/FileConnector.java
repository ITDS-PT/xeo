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
import netgest.io.metadata.MetadataConnector;
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
	private Session p_sessionJCR;
	/**
	 * Represents the configuration of file nodes in a JCR repository
	 */
	private FileNodeConfig p_fileConfig;
	/**
	 * Represents the configuration of metadata nodes in a JCR Repository
	 */
	private Map<String,MetadataNodeConfig> p_metaConfig;
	/**
	 * Represents the configuration of folder nodes in a JCR repository
	 */
	private FolderNodeConfig p_folderConfig;
	/**
	 * A reference to the {@link iMetadataConnector} for this JCR repository
	 */
	private MetadataConnector p_metadataConnector; 
	
	public FileConnector(){}
	
	public FileConnector(Session session, FileNodeConfig fileConf,
			FolderNodeConfig folderConf, Map<String,MetadataNodeConfig> metaConf) 
	{
		p_sessionJCR = 	session;
		p_fileConfig = 	fileConf;
		p_folderConfig = 	folderConf;
		p_metaConfig = metaConf;
		p_metadataConnector = new MetadataConnector(p_metaConfig, p_sessionJCR);
	}
	
	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#createIFile(java.lang.String, java.lang.String, boolean)
	 */
	@Override
	public iFile createIFile(String fileName, String identifier,
			boolean isFolder) throws iFileException{
		return new FileJCR(null, p_fileConfig, p_folderConfig, p_sessionJCR, p_metaConfig, isFolder, identifier);
		
	}

	
	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#createIFile(java.lang.String, boolean)
	 */
	@Override
	public iFile createIFile(String filename, boolean isFolder) throws iFileException{
		return new FileJCR(null, p_fileConfig, p_folderConfig, p_sessionJCR, p_metaConfig, isFolder, filename);
	}

	/* (non-Javadoc)
	 * @see netgest.io.iFileConnector#getIFile(java.lang.String)
	 */
	@Override
	public iFile getIFile(String fileID) throws iFileException{
		try {
			if (fileID == null)
				return null;
			
			Node node = this.p_sessionJCR.getRootNode().getNode(fileID);
			boolean isFolder = false;
			if (node.isNodeType(p_folderConfig.getNodeType()))
				isFolder = true;
			return new FileJCR(
					node, //The node to wrap the file
					p_fileConfig, //The fileNode config
					p_folderConfig, //The folderNode config
					p_sessionJCR, //The session with the repository
					p_metaConfig, 
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
		if (p_sessionJCR.getRepository().
				getDescriptor(Repository.OPTION_VERSIONING_SUPPORTED) != null)
			return true;
		return false;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean deleteIFile(String fileID) throws iFilePermissionDenied,
			iFileException {
		try {
			Node currentNode = p_sessionJCR.getRootNode().getNode(fileID);
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
		return p_metadataConnector;
	}
	
	

}
