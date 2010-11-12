package netgest.bo.configUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jcr.Session;

import netgest.bo.boConfig;
import netgest.bo.def.boDefDocument;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boRuntimeException2;
import netgest.io.iFileConnector;
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
	private String p_name;
	
	/**
	 * The name of the class to be invoked
	 * and create a Repository instance
	 */
	private String p_classConnections;
	
	/**
	 * The name of the class the implements the {@link iFileConnector}
	 * interface
	 */
	private String p_connectorClass;
	
	/**
	 * If this repository is the default repository
	 */
	private boolean p_isDefault;
	
	/**
	 * Whether or not when displaying files/folders all properties should be shown 
	 * or just the properties defined in the {@link FileNodeConfig}/ {@link FolderNodeConfig}
	 */
	private boolean p_showAllProperties;
	
	/**
	 * The XML handler
	 */
	private ngtXMLHandler p_handler;
	
	
	/**
	 * The File Node configurations
	 */
	private FileNodeConfig p_fileConfig;
	
	/**
	 * The folder node configurations
	 */
	private FolderNodeConfig p_folderConfig;
	
	/**
	 * Configurations for metadata nodes
	 */
	private Map<String,MetadataNodeConfig> p_metadataConfig;
	
	/**
	 * A map of parameters for a connection to a repository
	 */
	private Map<String,String> p_parameters;
	
	/**
	 * The default metadata node config type
	 */
	private MetadataNodeConfig p_defaultMetadata;
	
	/**
	 * The set of system properties (Properties that shoul'd not appear 
	 */
	private HashSet<String> p_sysProps;
	/**
	 * 
	 * Public constructor that loads the information
	 * from a XML handler
	 * 
	 * @param handler The XML handler
	 */
	public RepositoryConfig(ngtXMLHandler handler){
		this.p_handler = handler;
		this.p_parameters = new HashMap<String, String>();
		this.p_metadataConfig = new HashMap<String, MetadataNodeConfig>();
		this.p_sysProps = new HashSet<String>();
		load();
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return p_name;
	}

	/**
	 * @return the classConnections
	 */
	public String getClassConnections() {
		return p_classConnections;
	}
	
	/**
	 * @return the connectorClass
	 */
	public String getFileConnectorClass() {
		return p_connectorClass;
	}
	
	/**
	 * @return The parameters
	 */
	public Map<String,String> connectionParameters(){
		return p_parameters;
	}
	

	/**
	 * @return the isDefault
	 */
	public boolean isDefault() {
		return p_isDefault;
	}

	/**
	 * @return the fileConfig
	 */
	public FileNodeConfig getFileConfig() {
		return p_fileConfig;
	}

	/**
	 * @return the folderConfig
	 */
	public FolderNodeConfig getFolderConfig() {
		return p_folderConfig;
	}

	/**
	 * 
	 * Retrieves the list of metadata configurations
	 * 
	 * @return the metadataConfig
	 */
	public MetadataNodeConfig[] getAllMetadataConfig() {
		
		Iterator<String> it = p_metadataConfig.keySet().iterator();
		MetadataNodeConfig[] result = new MetadataNodeConfig[p_metadataConfig.keySet().size()];
		int k = 0;
		while (it.hasNext()) 
		{
			String configName = (String) it.next();
			MetadataNodeConfig metaConf = p_metadataConfig.get(configName);
			result[k] = metaConf;
			k++;
		}
		return result;
	}
	
	/**
	 * 
	 * Retrieves the map of metadata settings
	 * 
	 * @return
	 */
	public Map<String,MetadataNodeConfig> getAllMetadataConfigMap(){
		return p_metadataConfig;
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
		return this.p_metadataConfig.get(name);
	}
	
	/**
	 * 
	 * Checks if the
	 * 
	 * @param connectorClass
	 * @return
	 */
	private boolean checkNotDefaultValue(String connectorClass){
		if (connectorClass != null){
			if (connectorClass.equalsIgnoreCase(netgest.io.jcr.FileConnector.class.getName()))
				return false;
			else
				return true;
		}
		else
			return true;
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
		if (p_connectorClass != null && checkNotDefaultValue(p_connectorClass))
		{
			try {
				//Create a new Connector from the 
				connector = (iFileConnector) Class.forName(this.p_connectorClass).newInstance();
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
			
			iFileConnector fileConn = null;
			try {
				fileConn = (iFileConnector)Class.forName(config.getFileConnectorClass()).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			fileConn.initializeFileConnector(session, fileConf, folderConf, metaConf);
			fileConn.open();
			return fileConn;
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
			
			//Retrieve the repository configurations
			RepositoryConfig config = boConfig.getApplicationConfig().
				getFileRepositoryConfiguration(nameRep);
			
			FileNodeConfig fileConfig = null;
			FolderNodeConfig folderConfig = null;
			Map<String,MetadataNodeConfig> metaConfig = null;
			if (ecmDef.getFileNodeConfig() != null)
				fileConfig = ecmDef.getFileNodeConfig();
			else
				fileConfig = config.getFileConfig();
			if (ecmDef.getFileNodeConfig() != null)
				folderConfig = ecmDef.getFolderNodeConfig();
			else
				folderConfig = config.getFolderConfig();
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
			
			iFileConnector fileConn = null;
			try {
				fileConn = (iFileConnector)Class.forName(config.getFileConnectorClass()).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			fileConn.initializeFileConnector(session, fileConfig, folderConfig, metaConfig);
			fileConn.open();
			return fileConn;		
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
		this.p_name = p_handler.getAttribute("name");
		
		ngtXMLHandler classNode = p_handler.getChildNode("repositoryConnection");
		
		if (classNode != null){
			ngtXMLHandler[] params = classNode.getChildNode("parameters").getChildNodes();
			for (ngtXMLHandler p : params){
				String key = p.getAttribute("name");
				String value = p.getAttribute("value");
				this.p_parameters.put(key, value);
			}
		}
		
		//Load the class that connects to the repository
		this.p_classConnections = p_handler.getAttribute("classConnection");
		//Whether this configuration is the default repository
		String value = p_handler.getAttribute("default");
		if (value != null)
			this.p_isDefault = Boolean.parseBoolean(value);
		else
			this.p_isDefault = false;
		//Load the file connector class for this
		this.p_connectorClass = p_handler.getAttribute("fileConnector");
		
		String valueProps = p_handler.getAttribute("showAllProperties");
		if (valueProps != null)
			this.p_showAllProperties = Boolean.parseBoolean(valueProps);
		else
			this.p_showAllProperties = false;
		
		//Load the file nodes configuration
		ngtXMLHandler fileNode  = p_handler.getChildNode("fileNode");
		if (fileNode != null)
			this.p_fileConfig = new FileNodeConfig(fileNode,this);
		
		//Load the folder node configurations
        ngtXMLHandler folderNode = p_handler.getChildNode("folderNode");
        if (folderNode != null)
        	this.p_folderConfig = new FolderNodeConfig(folderNode);
        
        //Load the metadata nodes configuration
        ngtXMLHandler metadataNodes = p_handler.getChildNode("metadataNodes");
        p_defaultMetadata = null;
        if (metadataNodes != null){
        	int length = metadataNodes.getChildNodes().length;
            this.p_metadataConfig = new HashMap<String, MetadataNodeConfig>();
            ngtXMLHandler[] children = metadataNodes.getChildNodes();
            
            for (int k = 0; k < length; k++)
            {
            	MetadataNodeConfig c = new MetadataNodeConfig(children[k]);
            	if (c.isDefault())
            		p_defaultMetadata = c;
            	this.p_metadataConfig.put(c.getName(), c);
            }
            
            
        }
        //Make that all Metadata Node Configurations have access to all of the configurations
        Iterator<String> itMetaConfigNames = p_metadataConfig.keySet().iterator();
        while (itMetaConfigNames.hasNext()){
        	String currentName = itMetaConfigNames.next();
        	MetadataNodeConfig currentMetaConfig = p_metadataConfig.get(currentName);
        	currentMetaConfig.setAllRepositoryConfigurations(p_metadataConfig);
        }
        
        //Retrieve all system properties
        ngtXMLHandler sysProps = p_handler.getChildNode("system");
        if (sysProps != null){
        	ngtXMLHandler props = sysProps.getChildNode("properties");
        	ngtXMLHandler[] children = props.getChildNodes();
        	for (ngtXMLHandler t: children){
        		String name = t.getAttribute("name").trim();
        		p_sysProps.add(name);
        	}
        }
        
        
        
	}
	
	/**
	 * 
	 * Retrieves the default metadata node config
	 * 
	 * @return The default metadata node config, or null if one does not exist (it must exist in the configuration)
	 * 
	 */
	public MetadataNodeConfig getDefaultMetadataConfig(){
		if (p_defaultMetadata == null)
			throw new RuntimeException("The repository configuration " + getName() 
					+ " does not have a default metadata node configuration");
		return p_defaultMetadata;
	}
	
	
	/**
	 * 
	 * Retrieves a set of system property names. These are the names of properties
	 * that shouldn't appear
	 * 
	 * @return A set of strings with the names of the properties
	 */
	public Set<String> getSystemProperties(){
		return p_sysProps;
	}
	
	/**
	 * 
	 * 
	 * Whether or not all properties
	 * @return
	 */
	public boolean showAllProperties(){
		return p_showAllProperties;
	}
}
