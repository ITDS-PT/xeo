package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.jcr.Session;

import netgest.bo.boConfig;
import netgest.bo.def.boDefDocument;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boRuntimeException2;
import netgest.io.iFileConnector;
import netgest.io.jcr.FileConnector;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * Represents the configurations of a repository in boConfig.xml.
 * 
 * Minimum configuration is to have the connector class defined, but the nodes representing files, folder, metadata
 * and a way
 * 
 * @author PedroRio
 *
 */
public class RepositoryConfig 
{
		
	/**
	 * The repository name
	 */
	private String name;
	
	/**
	 * The name of the class to be invoked
	 * and create a Repository instance
	 */
	private String classConnections;
	
	/**
	 * The name of the class the implements the {@link iFileConnector}
	 * interface
	 */
	private String connectorClass;
	
	/**
	 * If this repository is the default repository
	 */
	private boolean isDefault;
	
	/**
	 * The XML handler
	 */
	private ngtXMLHandler handler;
	
	
	/**
	 * The File Node configurations
	 */
	private FileNodeConfig fileConfig;
	
	/**
	 * The folder node configurations
	 */
	private FolderNodeConfig folderConfig;
	
	/**
	 * Configurations for metadata nodes
	 */
	private Map<String,MetadataNodeConfig> metadataConfig;
	
	/**
	 * A map of parameters for a connection to a repository
	 */
	private Map<String,String> parameters;
	
	
	/**
	 * 
	 * Public constructor that loads the information
	 * from a XML handler
	 * 
	 * @param handler The XML handler
	 */
	public RepositoryConfig(ngtXMLHandler handler){
		this.handler = handler;
		this.parameters = new HashMap<String, String>();
		this.metadataConfig = new HashMap<String, MetadataNodeConfig>();
		load();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the classConnections
	 */
	public String getClassConnections() {
		return classConnections;
	}
	
	/**
	 * @return The parameters
	 */
	public Map<String,String> connectionParameters(){
		return parameters;
	}
	

	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return isDefault;
	}

	/**
	 * @return the fileConfig
	 */
	public FileNodeConfig getFileConfig() {
		return fileConfig;
	}

	/**
	 * @return the folderConfig
	 */
	public FolderNodeConfig getFolderConfig() {
		return folderConfig;
	}

	/**
	 * @return the metadataConfig
	 */
	public MetadataNodeConfig[] getAllMetadataConfig() {
		
		Iterator<String> it = metadataConfig.keySet().iterator();
		MetadataNodeConfig[] result = new MetadataNodeConfig[metadataConfig.keySet().size()];
		int k = 0;
		while (it.hasNext()) 
		{
			String configName = (String) it.next();
			MetadataNodeConfig metaConf = metadataConfig.get(configName);
			result[k] = metaConf;
			k++;
		}
		return result;
	}
	
	/**
	 * 
	 * Retrieves a given metadata node config given its name
	 * 
	 * @param name The name of the metadata node config
	 * @return A reference to the metadata node config
	 */
	public MetadataNodeConfig getMetadataConfigByName(String name)
	{
		return this.metadataConfig.get(name);
	}
	
	/**
	 * 
	 * Retrieves the {@link iFileConnector} for this Repository
	 * configuration
	 * 
	 * @return An {@link iFileConnector} 
	 * 
	 * @throws boRuntimeException If the connector cannot be created 
	 */
	public iFileConnector getConnector(EboContext ctx) throws boRuntimeException{
		iFileConnector connector;
		if (connectorClass != null)
		{
			try {
				//Create a new Connector from the 
				connector = (iFileConnector) Class.forName(this.connectorClass).newInstance();
				return connector;
			} catch (ClassNotFoundException e) {
				throw new boRuntimeException2(e);
			} catch (InstantiationException e) {
				throw new boRuntimeException2(e);
			} catch (IllegalAccessException e) {
				throw new boRuntimeException2(e);
			}
		}
		else
		{
			//Get the name for the repository
			String repositoryName = this.getName();

			//Retrieve the existing session for this user
			Session session = ctx.getBoSession().getECMRepositorySession(repositoryName);
			
			//Retrieve the repository configurations
			RepositoryConfig config = boConfig.getApplicationConfig().
				getFileRepositoryConfiguration(repositoryName);
			//Including the Nodes representing files
			FileNodeConfig fileConf = config.getFileConfig();
			//... the Nodes representing folders
			FolderNodeConfig folderConf = config.getFolderConfig();
			//... and the nodes representing metadata
			MetadataNodeConfig[] metaArray = config.getAllMetadataConfig();
			Map<String,MetadataNodeConfig> metaConf = new HashMap<String, MetadataNodeConfig>();
			for (MetadataNodeConfig p : metaArray){
				metaConf.put(p.getName(), p);
			}
			//Create the standard connector and return it
			FileConnector conn = new FileConnector(session, fileConf, folderConf, metaConf);
			return conn;
		}
	}
	
	/**
	 * 
	 * Retrieves an {@link iFileConnector} for the given {@link AttributeHandler}
	 * 
	 * @param handler The attribute handler for which to retrieve a connector
	 * @return A connector to use with the definition 
	 * @throws boRuntimeException
	 */
	public iFileConnector getConnector(AttributeHandler handler) throws boRuntimeException{
			
		if (handler.getDefAttribute().getECMDocumentDefinitions() != null)
		{
			boDefDocument ecmDef = handler.getDefAttribute().getECMDocumentDefinitions();
			
			String nameRep = handler.getDefAttribute().getECMDocumentDefinitions().getRepositoryName();
			if (nameRep == null)
				nameRep = boConfig.getApplicationConfig().getDefaultFileRepositoryConfiguration().getName();
			
			FileNodeConfig fileConfig = null;
			FolderNodeConfig folderConfig = null;
			Map<String,MetadataNodeConfig> metaConfig = null;
			if (ecmDef.getFileNodeConfig() != null)
				fileConfig = ecmDef.getFileNodeConfig();
			else
				fileConfig = boConfig.getApplicationConfig().getFileRepositoryConfiguration(nameRep).getFileConfig();
			if (ecmDef.getFileNodeConfig() != null)
				folderConfig = ecmDef.getFolderNodeConfig();
			else
				folderConfig = boConfig.getApplicationConfig().getFileRepositoryConfiguration(nameRep).getFolderConfig();
			if (ecmDef.getMetadataConfigs() != null)
				metaConfig = ecmDef.getMetadataConfigs();
			else
			{
				MetadataNodeConfig[] metaArray = boConfig.getApplicationConfig().
				getFileRepositoryConfiguration(nameRep).getAllMetadataConfig();
				Map<String,MetadataNodeConfig> metaConfigReturn = new HashMap<String, MetadataNodeConfig>();
				for (MetadataNodeConfig p : metaArray){
					metaConfigReturn.put(p.getName(), p);
				}
				metaConfig = metaConfigReturn;
			}
				
			Session session = handler.getEboContext().getBoSession().getECMRepositorySession(nameRep);
			iFileConnector conn = new FileConnector(session, fileConfig, folderConfig, metaConfig);
			return conn;		
		}
		else{
			return getConnector(handler.getEboContext());
		}
	}

	/**
	 * Loads the values from the XML definition
	 */
	private void load()
	{
		this.name = handler.getAttribute("name");
		
		ngtXMLHandler classNode = handler.getChildNode("repositoryConnection");
		
		if (classNode != null){
			ngtXMLHandler[] params = classNode.getChildNode("parameters").getChildNodes();
			for (ngtXMLHandler p : params){
				String key = p.getAttribute("name");
				String value = p.getAttribute("value");
				this.parameters.put(key, value);
			}
		}
		
		//Load the class that connects to the repository
		this.classConnections = classNode.getAttribute("classConnection");
		//Whether this configuration is the default repository
		String value = handler.getAttribute("default");
		if (value != null)
			this.isDefault = Boolean.parseBoolean(value);
		else
			this.isDefault = false;
		//Load the file connector class for this
		this.connectorClass = classNode.getAttribute("fileConnector");
		
		//Load the file nodes configuration
		ngtXMLHandler fileNode  = handler.getChildNode("fileNode");
		if (fileNode != null)
			this.fileConfig = new FileNodeConfig(fileNode);
		
		//Load the folder node configurations
        ngtXMLHandler folderNode = handler.getChildNode("folderNode");
        if (folderNode != null)
        	this.folderConfig = new FolderNodeConfig(folderNode);
        
        //Load the metadata nodes configuration
        ngtXMLHandler metadataNodes = handler.getChildNode("metadataNodes");
        if (metadataNodes != null){
        	int length = metadataNodes.getChildNodes().length;
            this.metadataConfig = new HashMap<String, MetadataNodeConfig>();
            ngtXMLHandler[] children = metadataNodes.getChildNodes();
            
            for (int k = 0; k < length; k++)
            {
            	MetadataNodeConfig c = new MetadataNodeConfig(children[k]);
            	this.metadataConfig.put(c.getName(), c);
            }
        }
        
	}
	
}
