/**
 * 
 */
package netgest.bo.def.v2;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.boConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.configUtils.NodePropertyDefinition;
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
	private ngtXMLHandler p_handler;
	/**
	 * If the attribute can be used to add new files to the repository
	 */
	private boolean p_canAddFile = false;
	/**
	 * If the attribute can be used to add new metadata to the repository
	 */
	private boolean p_canAddMetadata = false;
	/**
	 * If metadata is required for this attribute 
	 */
	private boolean p_metadataRequired = false;
	/**
	 * The name of the repository in which to store the data, 
	 * if null the default is used
	 */
	private String p_repositoryName = null;
	/**
	 * FileNode configurations in case the default should be overridden
	 */
	private FileNodeConfig p_fileNodeConfig = null;
	/**
	 * FolderNode configurations in case the default should be overridden
	 */
	private FolderNodeConfig p_folderNodeConfig = null;
	/**
	 * If this attribute represents a collection of files
	 * or a single file
	 */
	private boolean p_isFolder = false;
	/**
	 * MetadataNode configuration in case the default should be overridden
	 */
	private HashMap<String, MetadataNodeConfig> p_metadataConfig = null;
	
	/**
	 * The list of metadata properties for this file
	 */
	private List<NodePropertyDefinition> p_fileMetadataProperties = null; 
	
	/**
	 * Public constructor from a XML handler
	 * 
	 * @param xmlHandler The XML handler to read from
	 */
	public boDefDocumentImpl(ngtXMLHandler xmlHandler)
	{
		this.p_handler = xmlHandler;
		p_fileMetadataProperties = new LinkedList<NodePropertyDefinition>();
		p_metadataConfig = new HashMap<String, MetadataNodeConfig>();
		load();
	}
	
	
	/**
	 * Loads the information from the XML
	 */
	private void load()
	{
		if (p_handler.getChildNode("canAddFiles")!= null)
			this.p_canAddFile = Boolean.parseBoolean(p_handler.getChildNode("canAddFiles").getText());
		if (p_handler.getChildNode("canAddMetadata")!= null)
			this.p_canAddMetadata = Boolean.parseBoolean(p_handler.getChildNode("canAddMetadata").getText());
		if (p_handler.getChildNode("metadataRequired")!= null)
			this.p_metadataRequired = Boolean.parseBoolean(p_handler.getChildNode("metadataRequired").getText());
		if (p_handler.getChildNode("repositoryName")!= null)
		{
			String name = p_handler.getChildNode("repositoryName").getText();
			if (name != null)
			{
				if (name.length() > 0)
					this.p_repositoryName = name;
				else
					this.p_repositoryName = null;
			}
			
		}
		if (p_handler.getChildNode("isFolder")!= null)
			this.p_isFolder = Boolean.parseBoolean(p_handler.getChildNode("isFolder").getText());
		if (p_handler.getChildNode("fileNodes")!= null)
			this.p_fileNodeConfig = new FileNodeConfig(p_handler.getChildNode("fileNodes"),null);
		//FIXME: Aquele null no construtor do FileNodeConfig, n deve trazer chatices já que não é usado, mas.....
		if (p_handler.getChildNode("folderNodes")!= null)
			this.p_folderNodeConfig = new FolderNodeConfig(p_handler.getChildNode("folderNodes"));
		if (p_handler.getChildNode("metadataNodes")!= null)
		{
			ngtXMLHandler[] children = p_handler.getChildNode("metadataNodes").getChildNodes();
	        for (int k = 0; k < children.length; k++)
	        {
	        	MetadataNodeConfig c = new MetadataNodeConfig(children[k]);
	        	this.p_metadataConfig.put(c.getName(), c);
	        }
		}
		if (p_handler.getChildNode("metadataProperties") != null){
			ngtXMLHandler[] children = p_handler.getChildNode("metadataProperties").getChildNodes();
	        for (int k = 0; k < children.length; k++)
	        {
	        	NodePropertyDefinition p = new NodePropertyDefinition(children[k]);
	        	this.p_fileMetadataProperties.add(p);
	        }
		}
			
	}
	
	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#canAddFiles()
	 */
	@Override
	public boolean canAddFiles() {
		return p_canAddFile;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#canAddMetadata()
	 */
	@Override
	public boolean canAddMetadata() {
		return p_canAddMetadata;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getFileNodeConfig()
	 */
	@Override
	public FileNodeConfig getFileNodeConfig() {
		return p_fileNodeConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getFolderNodeConfig()
	 */
	@Override
	public FolderNodeConfig getFolderNodeConfig() {
		return p_folderNodeConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getMetadataConfigs()
	 */
	@Override
	public HashMap<String, MetadataNodeConfig> getMetadataConfigs() {
		return p_metadataConfig;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#getRepositoryName()
	 */
	@Override
	public String getRepositoryName() {
		if (p_repositoryName != null)
			return p_repositoryName;
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
		return p_isFolder;
	}

	/* (non-Javadoc)
	 * @see netgest.bo.def.boDefDocument#isMetadataRequired()
	 */
	@Override
	public boolean isMetadataRequired() {
		return p_metadataRequired;
	}


	@Override
	public iFileConnector getFileConnector() throws boRuntimeException {
		String repositoryName = getRepositoryName();
		RepositoryConfig config = boConfig.getApplicationConfig().getFileRepositoryConfiguration(repositoryName);
		return config.getConnector((EboContext)null);
		
	}

	@Override
	public List<NodePropertyDefinition> getMetadataPropertiesFile() {
		return p_fileMetadataProperties;
	}

}
