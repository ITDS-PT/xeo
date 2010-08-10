/**
 * 
 */
package netgest.bo.def.v2;

import java.util.HashMap;

import netgest.bo.boConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.def.boDefDocument;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.io.iFileConnector;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * Implementation of the {@link boDefDocument} interface
 * based on the XML definition of a XEOModel attribute
 * 
 * @author PedroRio
 *
 */
public class boDefDocumentImpl implements boDefDocument {

	
	/**
	 * The reference to the XML handler
	 */
	private ngtXMLHandler handler;
	/**
	 * If the attribute can be used to add new files to the repository
	 */
	private boolean canAddFile = false;
	/**
	 * If the attribute can be used to add new metadata to the repository
	 */
	private boolean canAddMetadata = false;
	/**
	 * If metadata is required for this attribute 
	 */
	private boolean metadataRequired = false;
	/**
	 * The name of the repository in which to store the data, 
	 * if null the default is used
	 */
	private String repositoryName = null;
	/**
	 * FileNode configurations in case the default should be overridden
	 */
	private FileNodeConfig fileNodeConfig = null;
	/**
	 * FolderNode configurations in case the default should be overridden
	 */
	private FolderNodeConfig folderNodeConfig = null;
	/**
	 * If this attribute represents a collection of files
	 * or a single file
	 */
	private boolean isFolder = false;
	/**
	 * MetadataNode configuration in case the default should be overridden
	 */
	private HashMap<String, MetadataNodeConfig> metadataConfig = null;
	
	/**
	 * Public constructor from a XML handler
	 * 
	 * @param xmlHandler The XML handler to read from
	 */
	public boDefDocumentImpl(ngtXMLHandler xmlHandler)
	{
		this.handler = xmlHandler;
		load();
	}
	
	
	/**
	 * Loads the information from the XML
	 */
	private void load()
	{
		if (handler.getChildNode("canAddFiles")!= null)
			this.canAddFile = Boolean.parseBoolean(handler.getChildNode("canAddFiles").getText());
		if (handler.getChildNode("canAddMetadata")!= null)
			this.canAddMetadata = Boolean.parseBoolean(handler.getChildNode("canAddMetadata").getText());
		if (handler.getChildNode("metadataRequired")!= null)
			this.metadataRequired = Boolean.parseBoolean(handler.getChildNode("metadataRequired").getText());
		if (handler.getChildNode("repositoryName")!= null)
		{
			String name = handler.getChildNode("repositoryName").getText();
			if (name != null)
			{
				if (name.length() > 0)
					this.repositoryName = name;
				else
					this.repositoryName = null;
			}
			
		}
		if (handler.getChildNode("isFolder")!= null)
			this.isFolder = Boolean.parseBoolean(handler.getChildNode("isFolder").getText());
		if (handler.getChildNode("fileNodes")!= null)
			this.fileNodeConfig = new FileNodeConfig(handler.getChildNode("fileNodes"));
		if (handler.getChildNode("folderNodes")!= null)
			this.folderNodeConfig = new FolderNodeConfig(handler.getChildNode("folderNodes"));
		if (handler.getChildNode("metadataNodes")!= null)
		{
			ngtXMLHandler[] children = handler.getChildNode("metadataNodes").getChildNodes();
	        for (int k = 0; k < children.length; k++)
	        {
	        	MetadataNodeConfig c = new MetadataNodeConfig(children[k]);
	        	this.metadataConfig.put(c.getName(), c);
	        }
		}
			
	}
	
	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#canAddFiles()
	 */
	@Override
	public boolean canAddFiles() {
		return canAddFile;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#canAddMetadata()
	 */
	@Override
	public boolean canAddMetadata() {
		return canAddMetadata;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getFileNodeConfig()
	 */
	@Override
	public FileNodeConfig getFileNodeConfig() {
		return fileNodeConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getFolderNodeConfig()
	 */
	@Override
	public FolderNodeConfig getFolderNodeConfig() {
		return folderNodeConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getMetadataConfigs()
	 */
	@Override
	public HashMap<String, MetadataNodeConfig> getMetadataConfigs() {
		return metadataConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getRepositoryName()
	 */
	@Override
	public String getRepositoryName() {
		if (repositoryName != null)
			return repositoryName;
		else
		{
			return boConfig.getApplicationConfig().getDefaultFileRepositoryConfiguration().getName();
		}
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#isFolder()
	 */
	@Override
	public boolean isFolder() {
		return isFolder;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#isMetadataRequired()
	 */
	@Override
	public boolean isMetadataRequired() {
		return metadataRequired;
	}


	@Override
	public iFileConnector getFileConnector() throws boRuntimeException {
		String repositoryName = getRepositoryName();
		RepositoryConfig config = boConfig.getApplicationConfig().getFileRepositoryConfiguration(repositoryName);
		return config.getConnector((EboContext)null);
		
	}

}
