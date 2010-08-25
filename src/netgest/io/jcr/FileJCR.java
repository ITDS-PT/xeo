/**
 * 
 */
package netgest.io.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.ValueFormatException;

import netgest.bo.boConfig;
import netgest.bo.configUtils.ChildNodeConfig;
import netgest.bo.configUtils.FileNodeConfig;
import netgest.bo.configUtils.FolderNodeConfig;
import netgest.bo.configUtils.MetadataNodeConfig;
import netgest.bo.configUtils.NodePropertyDefinition;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.io.iFile;
import netgest.io.iFileException;
import netgest.io.iFileFilter;
import netgest.io.iFilePermissionDenied;
import netgest.io.iFilenameFilter;
import netgest.io.metadata.iMetadataItem;
import netgest.io.metadata.iMetadataProperty;

/**
 * 
 * Represents an {@link iFile} that wraps a Java Content Repository (JCR)
 * {@link Node}
 * 
 * 
 * @author PedroRio
 * 
 */
public class FileJCR implements iFile {

	/**
	 * The logger for this class
	 */
	private static Logger logger = Logger.getLogger("netgest.io.jcr.FileJCR");

	/**
	 * The JCR node representing this iFile
	 */
	private Node p_node;
	/**
	 * Configurations for a file node
	 */
	private FileNodeConfig p_fileNodeConfig;
	/**
	 * Configurations for a folder node
	 */
	private FolderNodeConfig p_folderNodeConfig;
	/**
	 * A reference to the JCR session so that commands can be issued against the
	 * repository
	 */
	private Session p_session;

	/**
	 * A map with metadata of the file
	 */
	private HashMap<String, iMetadataItem> p_metadata;

	/**
	 * A map with the metadata definitions for this file
	 */
	private Map<String, MetadataNodeConfig> p_metadataDefinition;

	/**
	 * If the current {@link iFile} is a folder
	 */
	private boolean p_isFolder;

	/**
	 * The path for the current File
	 */
	private String p_path;

	/**
	 * The filename of the current File
	 */
	private String p_filename;

	/**
	 * Children files of this folder
	 */
	private List<iFile> p_childrenFiles;
	
	/**
	 * If the iFile is in transaction or not
	 */
	private boolean p_inTransaction = false;

	/**
	 * 
	 * Protected constructor for a {@link FileJCR} because only an instance of
	 * {@link FileConnector} can create new instances of a {@link FileJCR}
	 * 
	 * @param node
	 *            The node representing this File (can be null if the node
	 *            doesn't exist)
	 * @param configFile
	 *            Configuration Information for files in the current repository
	 * @param configFolder
	 *            Configuration information for folders in the current
	 *            repository
	 * @param session
	 *            A session with the Repository FIXME: Javadoc for parameters
	 */
	protected FileJCR(Node node, FileNodeConfig configFile,
			FolderNodeConfig configFolder, Session session,
			Map<String, MetadataNodeConfig> metadata, boolean isFolder,
			String path) {
		this.p_node = node;
		this.p_fileNodeConfig = configFile;
		this.p_folderNodeConfig = configFolder;
		this.p_session = session;
		this.p_metadataDefinition = metadata;
		this.p_isFolder = isFolder;
		this.p_path = path;
		this.p_metadata = new HashMap<String, iMetadataItem>();
		this.p_filename = getFilenameFromPath();
		// Create the default metadata item
		this.p_metadata.put(DEFAULT_METADATA, createDefaultItem());
		this.p_childrenFiles = null;
		p_inTransaction = false;
	}
	
	protected FileJCR(Node node, String repositoryName){
		
		try {
			RepositoryConfig config = boConfig.getApplicationConfig().getFileRepositoryConfiguration(repositoryName);
			p_node = node;
			p_session = node.getSession();
			p_path = p_node.getPath();
			if (p_node.getPrimaryNodeType().getName().equals(
					p_folderNodeConfig.getNodeType()))
				p_isFolder = true;
			else
				p_isFolder = false;
					
			p_fileNodeConfig = config.getFileConfig();
			p_folderNodeConfig = config.getFolderConfig();
			p_metadataDefinition = config.getAllMetadataConfigMap();
			p_metadata.put(DEFAULT_METADATA, createDefaultItem());
			p_childrenFiles = null;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * 
	 * Generates the default {@link iMetadataItem}
	 * 
	 * @return
	 */
	private iMetadataItem createDefaultItem() {
		try {
			Map<String, NodePropertyDefinition> fileProps = p_fileNodeConfig
					.getProperties();
			iMetadataItem defaultItem = new MetadataItem(null,
					DEFAULT_METADATA, p_session, null,null);
			
			if (fileProps != null) {
				if (p_node != null) {
					PropertyIterator itProps = p_node.getProperties();
					// Iterate through all properties in the node
					while (itProps.hasNext()) {
						Property currentProp = itProps.nextProperty();
						if (fileProps.containsKey(currentProp.getName())) 
						{
							NodePropertyDefinition def = fileProps.get(currentProp.getName());
							// Any property which is not a
							if (def.getReference() == null) {
								createMetadataItemPropertyFromJCRProperty(defaultItem, currentProp);
							}
						}
						else
						{
							if (!currentProp.isNode())
								createMetadataItemPropertyFromJCRProperty(defaultItem, currentProp);
							/*try { currentProp.getNode(); } 
							catch (ValueFormatException e) {
								createMetadataItemPropertyFromJCRProperty(defaultItem, currentProp); }
							catch (PathNotFoundException e ){
								e.printStackTrace();
							}*/
						}
					}
				}
			}
			// Now find every other property in children
			Map<String, ChildNodeConfig> childNodes = p_fileNodeConfig
					.getChildNodes();
			if (childNodes != null) {
				if (p_node != null) {
					NodeIterator itNodes = p_node.getNodes();
					while (itNodes.hasNext()) {
						Node currentChildNode = (Node) itNodes.next();
						if (childNodes.containsKey(currentChildNode.getName())) 
						{
							PropertyIterator itProps = currentChildNode
									.getProperties();
							// Iterate through all properties in the node
							while (itProps.hasNext()) 
							{
								Property currentProp = itProps.nextProperty();
								if (fileProps
										.containsKey(currentProp.getName())) 
								{
									NodePropertyDefinition def = fileProps
											.get(currentProp.getName());
									//Any property which is not a reference should be kept as default metadata
									if (def.getReference() == null) 
									{
										createMetadataItemPropertyFromJCRProperty(
												defaultItem, currentProp);
									}
								}
							}
						}
					}
				}
			}

			return defaultItem;
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return new MetadataItem(null, DEFAULT_METADATA, p_session, null,null);
	}

	/**
	 * 
	 * Converts the path of the {@link iFile} to its filename
	 * 
	 * @return A string with the filename of the {@link iFile}
	 */
	private String getFilenameFromPath() {
		String result = p_path;
		if (p_path.endsWith("/"))
			result = result.substring(0, p_path.length() - 1);

		int lastSeparator = result.lastIndexOf("/");
		result = result.substring(lastSeparator + 1);

		return result;

	}

	@SuppressWarnings("deprecation")
	private void createMetadataItemPropertyFromJCRProperty(iMetadataItem item,
			Property prop) throws RepositoryException {

		String name = prop.getName();
		if (prop.getType() == PropertyType.BINARY) {
			item.setMetadataProperty(new MetadataProperty(name, prop
					.getStream()));
		} else if (prop.getType() == PropertyType.DATE) {
			item.setMetadataProperty(new MetadataProperty(name, prop
					.getDate().getTime()));
		} else if (prop.getType() == PropertyType.BOOLEAN) {
			item.setMetadataProperty(new MetadataProperty(name, prop
					.getBoolean()));
		} else if (prop.getType() == PropertyType.STRING) {
			item.setMetadataProperty(new MetadataProperty(name, prop
					.getString()));
		} else if (prop.getType() == PropertyType.LONG) {
			item.setMetadataProperty(new MetadataProperty(name, prop
					.getLong()));
		} else if (prop.getType() == PropertyType.REFERENCE) {
			iMetadataItem itemNew = new MetadataItem(prop.getNode(), null,null);
			item.setMetadataProperty(new MetadataProperty(name, itemNew));
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#addMetadata(java.lang.String,
	 * netgest.io.metadata.iMetadataItem)
	 */
	@Override
	public void addMetadata(iMetadataItem item) 
	{
		String id = getFullPathFromMetadataItem(item);
		if (id != null)
			this.p_metadata.put(id, item);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#canRead()
	 */
	@Override
	public boolean canRead() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#canWrite()
	 */
	@Override
	public boolean canWrite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#checkIn()
	 */
	@Override
	public boolean checkIn() throws iFilePermissionDenied {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#checkOut()
	 */
	@Override
	public boolean checkOut() throws iFilePermissionDenied {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#close()
	 */
	@Override
	public boolean close() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#createNewFile()
	 */
	@Override
	public boolean createNewFile() throws IOException, iFilePermissionDenied {
		return this.save(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#delete()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public boolean delete() throws iFilePermissionDenied {
		if (exists()) {
			try {
				Node parent = p_node.getParent();
				p_node.remove();
				if (parent != null)
					parent.save();
				else
					p_session.save();

			} catch (RepositoryException e) {
				throw new iFilePermissionDenied(e);
			}
				return true;
			
		} else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#exists()
	 */
	@Override
	public boolean exists() {
		if (p_node != null)
			return true;
		else
			return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getAbsolutePath()
	 */
	@Override
	public String getAbsolutePath() {
		return p_path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getAllMetadata()
	 */
	@Override
	public List<iMetadataItem> getAllMetadata() {
		Vector<iMetadataItem> result = new Vector<iMetadataItem>();
		if (p_node != null) {
			try {
				PropertyIterator itProps = p_node.getProperties();
				while (itProps.hasNext()) {
					Property currProp = (Property) itProps.next();
					if (isMetadataProperty(currProp.getName())) {
						// Properties that link
						if (currProp.getType() == PropertyType.REFERENCE) {
							MetadataItem current = new MetadataItem(currProp
									.getNode(), null,null);
							result.add(current);
						}
					}
				}

				Iterator<String> it = p_metadata.keySet().iterator();
				while (it.hasNext()) {
					String metaName = (String) it.next();
					iMetadataItem currMeta = p_metadata.get(metaName);
					result.add(currMeta);
				}

			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		} else {
			return new ArrayList<iMetadataItem>(this.p_metadata.values());
		}
		return result;
	}

	/**
	 * 
	 * Checks if a given property name belongs the to internal properties of the
	 * file
	 * 
	 * @param propName
	 *            The name of the Property
	 * 
	 * @return True if the property is not an internal property and false other
	 *         otherwise
	 */
	private boolean isMetadataProperty(String propName) {
		if (propName.equals(p_fileNodeConfig.getBinaryPropertyName())
				|| propName
						.equals(p_fileNodeConfig.getDateCreatePropertyName())
				|| propName
						.equals(p_fileNodeConfig.getDateUpdatePropertyName())
				|| propName.equals(p_fileNodeConfig.getMimeTypePropertyName()))
			return false;
		else
			return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getAuthor()
	 */
	@Override
	public String getAuthor() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getCategory()
	 */
	@Override
	public String getCategory() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getCheckOutUser()
	 */
	@Override
	public String getCheckOutUser() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getCopy()
	 */
	@Override
	public iFile getCopy() {
		// Not implemented, we don't duplicate files
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getInputStream()
	 */
	@SuppressWarnings("deprecation")
	@Override
	public InputStream getInputStream() throws iFilePermissionDenied {
		String binProp = p_fileNodeConfig.getBinaryPropertyName();
		try {
			Property binAttemp = p_node.getProperty(binProp);
			return binAttemp.getStream();
		} catch (PathNotFoundException e) {
			// Means there is no property in the node, so we must check the
			// children
			Map<String, ChildNodeConfig> children = this.p_fileNodeConfig
					.getChildNodes();
			if (children != null) {
				Iterator<String> it = children.keySet().iterator();
				while (it.hasNext()) {
					String childName = (String) it.next();
					ChildNodeConfig currentConfig = children.get(childName);
					if (currentConfig.getProperties().containsKey(binProp)) {
						try {
							return p_node.getNode(childName).getProperty(
									binProp).getStream();
						} catch (RepositoryException e1) {
							e1.printStackTrace();
						}
					} else
						return getRecursiveInputStream(currentConfig, "",
								binProp);
				}
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * Checks child nodes of the Node for the binary property
	 * 
	 * @param config
	 *            The childNode configuration to node
	 * @param pathParent
	 *            The path to the parent node
	 * @param binProp
	 *            The binary property
	 * @return
	 */
	@SuppressWarnings("deprecation")
	private InputStream getRecursiveInputStream(ChildNodeConfig config,
			String pathParent, String binProp) {

		Map<String, ChildNodeConfig> children = config.getChildNodes();
		if (children != null) {
			Iterator<String> it = children.keySet().iterator();
			while (it.hasNext()) {
				String childName = (String) it.next();
				ChildNodeConfig currentConfig = children.get(childName);
				if (currentConfig.getProperties().containsKey(binProp)) {
					try {
						if (pathParent.length() > 0)
							return p_node.getNode(pathParent + "/" + childName)
									.getProperty(binProp).getStream();
						else
							return p_node.getNode(childName).getProperty(
									binProp).getStream();
					} catch (RepositoryException e1) {
						e1.printStackTrace();
					}
				} else {
					return getRecursiveInputStream(currentConfig, pathParent
							+ "/" + childName, binProp);
				}
			}
		}
		return null;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getKey()
	 */
	@Override
	public long getKey() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getMetadata(java.lang.String)
	 */
	@Override
	public iMetadataItem getMetadata(String name) {
		try {
			if (name.equalsIgnoreCase(DEFAULT_METADATA))
				return p_metadata.get(name);
				
			if (p_node != null) 
			{
				try 
				{
					if (p_node.getProperty(name).getType() == PropertyType.REFERENCE)
					{
						MetadataNodeConfig conf = p_metadataDefinition.get(name);
						return new MetadataItem(p_node.getProperty(name).getNode(), conf , p_metadataDefinition);
					}
				} catch (PathNotFoundException e) {
					return p_metadata.get(name);
				}

			} else {
				return p_metadata.get(name);
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getMetadataTypes()
	 */
	@Override
	public List<String> getMetadataTypes() {
		if (p_metadataDefinition != null) {
			Iterator<String> it = p_metadataDefinition.keySet().iterator();
			Vector<String> result = new Vector<String>();
			while (it.hasNext()) {
				result.add(it.next());
			}
			return result;
		} else
			return new Vector<String>();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getName()
	 */
	@Override
	public String getName() {
		return p_filename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getParent()
	 */
	@Override
	public String getParent() {

		try {
			if (p_node != null) {
				if (p_node.getParent() != null)
					return p_node.getParent().getPath();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
			return null;
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getParentFile()
	 */
	@Override
	public iFile getParentFile() {
		try {
			String path = (String) p_path.subSequence(0, p_path
					.lastIndexOf("/"));
			if (p_node != null) {
				if (p_node.getParent() != null) {
					return new FileJCR(p_node.getParent(), p_fileNodeConfig,
							p_folderNodeConfig, p_session,
							p_metadataDefinition, true, // isFolder
							path);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getPath()
	 */
	@Override
	public String getPath() {
		if (p_node != null)
			try {
				return p_node.getPath();
			} catch (RepositoryException e) {
				return p_path;
			}
		else
			return p_path;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getURI()
	 */
	@Override
	public String getURI() {
		return getPath();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getVersion()
	 */
	@Override
	public long getVersion() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#getVersionUser()
	 */
	@Override
	public String getVersionUser() {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#isCheckedIn()
	 */
	@Override
	public boolean isCheckedIn() {
		return !isCheckedOut();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#isCheckedOut()
	 */
	@Override
	public boolean isCheckedOut() {
		try {
			if (p_node != null) {
				return p_node.isCheckedOut();
			} else
				return false;
		} catch (RepositoryException e) {

			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#isDirectory()
	 */
	@Override
	public boolean isDirectory() {
		try {
			if (p_node != null) {
				if (p_node.getPrimaryNodeType().getName().equals(
						p_folderNodeConfig.getNodeType()))
					return true;
				if (p_node.getPrimaryNodeType().getName().equals(
						p_fileNodeConfig.getNodeType()))
					return false;
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return p_isFolder;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#isFile()
	 */
	@Override
	public boolean isFile() {
		return !isDirectory();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#isVersioned()
	 */
	@Override
	public boolean isVersioned() {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#lastModified()
	 */
	@Override
	public long lastModified() {
		if (exists()){
			try {
				if (isDirectory()){
					return p_node.getProperty(
							p_folderNodeConfig.getUpdateDateProperty())
							.getLong();
				}else{
					Property prop = findProperty(p_fileNodeConfig.getDateUpdatePropertyName());
					if (prop != null)
						return prop.getDate().getTimeInMillis();
					else
						return -1;
				}
		} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return new Date().getTime();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#length()
	 */
	@Override
	public long length() {
		if (exists() && !isDirectory()){
			Property prop = findProperty(p_fileNodeConfig.getBinaryPropertyName());
			try {
				if (prop != null)
					return prop.getLength();
			} catch (ValueFormatException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
		return -1;
	}
	
	
	/**
	 * 
	 * Searches and retrieves a reference to a property with a given name
	 * 
	 * @param name The name of the property
	 * 
	 * @return A reference to the property or null if it
	 * does not exist
	 */
	private Property findProperty(String name){
		try {
			if (exists()){
				//Iterate through all properties
				PropertyIterator itProps = p_node.getProperties();
				while (itProps.hasNext()) {
					Property currentProp = (Property) itProps.next();
					if (currentProp.getName().equalsIgnoreCase(name)){
						return currentProp;
					}
				}
				
				//Iterate through child elements
				NodeIterator itNodes = p_node.getNodes();
				while (itNodes.hasNext()){
					Node currentChild = (Node) itNodes.next();
					Property prop = recursiveFindProperty(name, currentChild);
					if (prop != null)
						return prop;
				}
				
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 
	 * Traverses the entire node structure and finds a given property
	 * 
	 * @param name The name of the property
	 * @param node The node reference
	 * 
	 * @return The property or null if it's not found
	 */
	private Property recursiveFindProperty(String name, Node node){
		try {
			PropertyIterator itProps = node.getProperties();
			while (itProps.hasNext()) {
				Property currentProp = (Property) itProps.next();
				if (currentProp.getName().equalsIgnoreCase(name)){
					return currentProp;
				}
			}
			NodeIterator itNodes = node.getNodes();
			while (itNodes.hasNext()){
				Node currentChild = (Node) itNodes.next();
				Property prop = recursiveFindProperty(name, currentChild);
				if (prop != null)
					return prop;
			}
			
		} catch (RepositoryException e) {
			e.printStackTrace();
			
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#list()
	 */
	@Override
	public String[] list() throws iFilePermissionDenied {
		return list(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#list(netgest.io.iFilenameFilter)
	 */
	@Override
	public String[] list(iFilenameFilter filter) throws iFilePermissionDenied {
		if (isDirectory()) {
			Vector<String> list = new Vector<String>();
			try {
				if (isDirectory()) {
					if (p_node != null) {
						NodeIterator it = p_node.getNodes(p_folderNodeConfig
								.getFileNodeTypes());
						while (it.hasNext()) {
							Node current = it.nextNode();
							String nodeType = current.getPrimaryNodeType()
									.getName();
							if (nodeType.equals(p_fileNodeConfig.getNodeType())
									|| nodeType.equals(p_folderNodeConfig
											.getNodeType())) {

								if (filter != null) {
									if (filter.accept(current.getName()))
										list.add(current.getName());
								} else
									list.add(current.getName());
							}
						}
					}
					String[] result = (String[]) list.toArray(new String[list
							.size()]);
					return result;
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
			return new String[0];
		} else
			return new String[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#listFiles()
	 */
	@Override
	public iFile[] listFiles() throws iFilePermissionDenied {
		return listFiles(null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#listFiles(netgest.io.iFileFilter)
	 */
	@Override
	public iFile[] listFiles(iFileFilter filter) throws iFilePermissionDenied {
		Vector<iFile> filesReturn = new Vector<iFile>();
		if (isDirectory()) {
			if (exists()) {
				try {
					NodeIterator itFiles = p_node.getNodes();
					while (itFiles.hasNext()) {
						Node currentFile = (Node) itFiles.next();
						boolean isFolder = false;
						String childNodeType = currentFile.getPrimaryNodeType().getName();
						if (childNodeType.equalsIgnoreCase(p_fileNodeConfig.getNodeType())
								|| childNodeType.equalsIgnoreCase(p_folderNodeConfig.getNodeType())){
							
							if (childNodeType.equalsIgnoreCase(p_folderNodeConfig
											.getNodeType()))
								isFolder = true;
							
							iFile toAdd = new FileJCR(currentFile,
									p_fileNodeConfig, p_folderNodeConfig,
									p_session, p_metadataDefinition, isFolder,
									currentFile.getPath());
							filesReturn.add(toAdd);
						}
					}
				} catch (RepositoryException e) {
					e.printStackTrace();
				}
			}
			if (p_childrenFiles != null) {
				Iterator<iFile> itFiles = p_childrenFiles.iterator();
				while (itFiles.hasNext()) {
					iFile currentFile = (iFile) itFiles.next();
					filesReturn.add(currentFile);
				}
			}

			Iterator<iFile> itFiles = filesReturn.iterator();
			iFile[] result = new iFile[filesReturn.size()];
			int k = 0;
			while (itFiles.hasNext()) {
				iFile current = (iFile) itFiles.next();
				result[k] = current;
				k++;
			}
			return result;
		} else
			return new iFile[0];
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#makeVersioned()
	 */
	@Override
	public boolean makeVersioned() throws iFilePermissionDenied {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#mkdir()
	 */
	@Override
	public boolean mkdir() throws iFilePermissionDenied {
		try {
			String path = p_path;
			Node current = p_session.getRootNode();
			String[] parts = path.split("/");
			// Iterate through all minus the last (the one we want to create)
			String name = "";
			for (int k = 0; k < parts.length - 1; k++) {
				name = parts[k];
				if (name.length() > 0) {
					current = current.getNode(name);
				}
			}

			// If we reach the last node, create it
			current.addNode(name, p_folderNodeConfig.getNodeType());
			return true;

		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#mkdirs()
	 */
	@Override
	public boolean mkdirs() throws iFilePermissionDenied {
		try {
			String path = p_path;
			Node current = p_session.getRootNode();
			String[] parts = path.split("/");
			// Iterate through all minus the last (the one we want to create)
			String name = "";
			for (int k = 0; k < parts.length; k++) {
				name = parts[k];
				if (name.length() > 0) {
					try {
						current = current.getNode(name);
					} catch (PathNotFoundException e) {
						current = current.addNode(name, p_folderNodeConfig
								.getNodeType());
						if (k == (parts.length - 1))
							p_node = current; // If we are creating "the" node,
						// keep the reference to it
						// Calendar cal = Calendar.getInstance();
						// cal.setTime(new Date(System.currentTimeMillis()));
						// current.setProperty(p_folderNodeConfig.getCreateDateProperty(),
						// cal);
						// current.setProperty(p_folderNodeConfig.getUpdateDateProperty(),
						// cal);
					} catch (RepositoryException e) {
						e.printStackTrace();
						return false;
					}
				}
			}
			return true;

		} catch (RepositoryException e) {
			e.printStackTrace();
			return false;
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#removeMetadata(java.lang.String)
	 */
	@Override
	public void removeMetadata(String metadataItemId) {
		if (p_metadata.containsKey(metadataItemId))
			this.p_metadata.remove(metadataItemId);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#renameTo(netgest.io.iFile)
	 */
	@Override
	public boolean renameTo(iFile newfile) throws iFilePermissionDenied {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setAuthor(java.lang.String)
	 */
	@Override
	public void setAuthor(String author) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setBinaryStream(java.io.InputStream)
	 */
	@Override
	public void setBinaryStream(InputStream is) throws iFilePermissionDenied {
		// Retrieve the binary property of the file
		String binProp = p_fileNodeConfig.getBinaryPropertyName();
		iMetadataItem defaultMetadata = getDefaultMetadata();
		defaultMetadata.setMetadataProperty(new MetadataProperty(
				binProp, is));
		updateLastModifiedDate();
	}

	/**
	 * Updates the last update date property
	 */
	private void updateLastModifiedDate() {
		iMetadataItem metadataItem = getDefaultMetadata();
		String dateProp = p_fileNodeConfig.getDateUpdatePropertyName();
		metadataItem.setMetadataProperty(new MetadataProperty(
				dateProp, new Date(System.currentTimeMillis())));

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setCategory(java.lang.String)
	 */
	@Override
	public void setCategory(String author) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setCheckOutUser(java.lang.String)
	 */
	@Override
	public void setCheckOutUser(String user) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setDescription(java.lang.String)
	 */
	@Override
	public void setDescription(String description) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setReadOnly()
	 */
	@Override
	public boolean setReadOnly() throws iFilePermissionDenied {
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see netgest.io.iFile#setVersionUser(java.lang.String)
	 */
	@Override
	public void setVersionUser(String user) {
		// Not implemented
	}

	/**
	 * 
	 * Retrieves the {@link Node} representing this {@link iFile}
	 * 
	 * @return a JCR Node
	 */
	public Node getNode() {
		return p_node;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean save(EboContext ctx) {
		boolean result = false;
		try {

			if (exists()) { // iFile exists, must update attributes

				// If we have a directory, save the children
				if (isDirectory()) {
					iFile[] files = this.listFiles();
					for (iFile f : files)
						f.save(ctx);

					createAndSetPropertiesOfNode(p_node, 
							p_folderNodeConfig.getProperties(), 
							p_folderNodeConfig.getChildNodes());
				} else {
					createAndSetPropertiesOfNode(p_node, 
							p_fileNodeConfig.getProperties(), 
							p_fileNodeConfig.getChildNodes());
					
				}

				// Save any metadata items
				saveMetadataItems();

				p_node.save();
				
				p_node.getNode("jcr:content").save();
				
				// Save the Node (by saving the parent)
				/*if (p_node.getParent() != null) {
					Node parent = p_node.getParent();
					//parent.save();
					p_node.save();
					result = true;
				}*/

			} else { // Create the new iFile

				if (isDirectory()) { // If we have a folder we must create the
					// directory
					if (this.mkdirs()) {
						// Save metadata items
						saveMetadataItems();
						// Save ther folder to the repository, then create the
						// files
						Node parent = getParentNode(p_path);
						parent.save();

						// Save the files
						iFile[] files = this.listFiles();
						for (iFile f : files)
							f.save(ctx);

						createAndSetPropertiesOfNode(p_node, 
								p_folderNodeConfig.getProperties(), 
								p_folderNodeConfig.getChildNodes());

						result = true;
					}
				} else { // We have to create the file

					// Retrieve the parent node
					Node parent = getParentNode(p_path);
					if (parent != null) {
						// Add the node type for files
						Node current = parent.addNode(p_filename,
								p_fileNodeConfig.getNodeType());
						p_node = current;
						
						
						createAndSetPropertiesOfNode(current, 
								p_fileNodeConfig.getProperties(), 
								p_fileNodeConfig.getChildNodes());
						
						
						// Save any metadata items
						saveMetadataItems();

						printNode(p_node);
						
						parent.save();

						result = true;
					}
				}
			}

		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (iFilePermissionDenied e) {
			e.printStackTrace();
		} catch (iFileException e) {
			e.printStackTrace();
		}
		return result;
	}

		private void printNode(Node nodeToPrint){
		
			try {
				System.out.println("*** First Level Properties ****");
				PropertyIterator itProps = nodeToPrint.getProperties();
				while (itProps.hasNext()) {
					Property currProp = itProps.nextProperty();
					System.out.print(currProp.getName()+"|");
				}
				System.out.println("");
				System.out.println("*** Second Level Properties ****");
				
				NodeIterator itNodes = nodeToPrint.getNodes();
				while (itNodes.hasNext()) {
					Node currNode = itNodes.nextNode();
					System.out.println("--- "+ currNode.getName() +"  ---");
					PropertyIterator itProps2 = currNode.getProperties();
					while (itProps2.hasNext()) {
						Property currProp = itProps2.nextProperty();
						System.out.print(currProp.getName()+"|");
					}
				}
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		
		
		
		
	}
	
	/**
	 * 
	 * Sets a {@link Node} property with the value from an
	 * {@link iMetadataProperty}
	 * 
	 * @param prop
	 *            The property to get the value from
	 * @param node
	 *            The node to set the property with the value retrieved from the
	 *            {@link iMetadataProperty}
	 */
	@SuppressWarnings("deprecation")
	private void setPropertyValueFromMetadata(iMetadataProperty prop, Node node) {
		try {
			if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.BOOLEAN.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueBoolean());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.LONG
					.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueLong());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.STRING
					.name())
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueString());
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.BINARY
					.name()){
				System.out.println("BINARY *********" + prop.getPropertyIdentifier());
				node.setProperty(prop.getPropertyIdentifier(), prop.getValueBinary());
			}
			else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.DATE
					.name()) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(prop.getValueDate());
				node.setProperty(prop.getPropertyIdentifier(), cal);
			} else if (prop.getMetadataType() == iMetadataProperty.METADATA_TYPE.REFERENCE
					.name()) {
				String identifier = prop.getReference().getID();
				Node target = node.getSession().getRootNode().getNode(
						identifier);
				node.setProperty(prop.getPropertyIdentifier(), target);
			}
			System.out.println("Added Property  " + prop.getPropertyIdentifier() + " to " + node.getName());
		} catch (RepositoryException e) {
			e.printStackTrace();
		} catch (netgest.io.metadata.ValueFormatException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * Given a repository path returns the parent node of that path
	 * 
	 * @param path
	 *            The path to the node
	 * @return A reference to the parent node, or null if that parent does not
	 *         exist
	 */
	private Node getParentNode(String path) {

		try {
			String[] parts = path.split("/");
			Node root = p_session.getRootNode();

			for (int k = 0; k < parts.length - 1; k++) {
				String p = parts[k];
				if (p.length() > 0) {
					root = root.getNode(p);
				}
			}
			return root;
		} catch (RepositoryException e) {
			logger.severe("Could not return node identified by " + path, e);
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Saves all metadata items associated to this  
	 */
	private void saveMetadataItems() {
		List<iMetadataItem> listMeta = this.getAllMetadata();
		Iterator<iMetadataItem> it = listMeta.iterator();
		while (it.hasNext()) {
			iMetadataItem currItem = (iMetadataItem) it.next();
			// Only save regular metadata items, the regular save will deal with
			// the default metadata items
			if (!currItem.getName().equalsIgnoreCase(DEFAULT_METADATA)) {
				// Save the metadata item in case it does not exist or update
				// its properties
					try {
						Node metaItemNode = p_session.getRootNode().getNode(currItem.getID());
						p_node.setProperty(currItem.getID(), metaItemNode);
						
					} catch (RepositoryException e) {
						e.printStackTrace();
					}
			}
		}
	}

	@Override
	public iMetadataItem getDefaultMetadata() {
		return this.getMetadata(iFile.DEFAULT_METADATA);
	}

	@Override
	public boolean addChild(iFile file) throws iFileException {
		if (p_childrenFiles == null)
			p_childrenFiles = new LinkedList<iFile>();
		p_childrenFiles.add(file);
		return true;
	}

	/**
	 * 
	 * Sets the properties of a node, creating the entire node structure
	 * 
	 * 
	 * @param reference The base node to create the structure
	 * @param nodeProps The set of properties for this node
	 * @param childNodes The child node definitions of this node
	 * 
	 * @return True if the properties were created and false otherwise
	 */
	private boolean createAndSetPropertiesOfNode(Node reference,
			Map<String, NodePropertyDefinition> nodeProps,
			Map<String, ChildNodeConfig> childNodes) {
		// Lets checks all properties of this Node
		Iterator<String> itProps = nodeProps.keySet().iterator();
		Set<String> propsUsed = new HashSet<String>(); 
		while (itProps.hasNext()) {
			String propName = (String) itProps.next();
			NodePropertyDefinition currentPropDef = nodeProps.get(propName);
			// If this property is a main property
			if (currentPropDef.isMainNode()) {
				iMetadataProperty property = getDefaultMetadata().getPropertyById(propName);
				//Confirm this property has handled
				propsUsed.add(propName);
				
				if (property != null) //If it's a default metadata property, set it
					setPropertyValueFromMetadata(property, reference);
				else{
					//If it's a reference to another node, get the identifier and
					iMetadataItem item = getMetadata(propName);
					if (item != null){
						String identifier = item.getID();
						Node referenceMetadata;
						try {
							referenceMetadata = p_session.getRootNode().getNode(identifier);
							p_node.setProperty(propName, referenceMetadata);
						} catch (PathNotFoundException e) {
							e.printStackTrace();
							return false;
						} catch (RepositoryException e) {
							e.printStackTrace();
							return false;
						}
					}
				}
			}
		}
		
		//Deal with the remaining properties (only if they are main node properties)
		List<iMetadataProperty> propsDefault = getDefaultMetadata().getProperties();
		if (propsUsed.size() < propsDefault.size()){
			Iterator<iMetadataProperty> it = propsDefault.iterator();
			while (it.hasNext()) 
			{
				iMetadataProperty currProp = (iMetadataProperty) it.next();
				if (!propsUsed.contains(currProp.getPropertyIdentifier())){
					NodePropertyDefinition prop = nodeProps.get(currProp.getPropertyIdentifier());
					if (prop != null){
						if (prop.isMainNode())
							setPropertyValueFromMetadata(currProp, reference);
					}
				}
			}
		}
		
		// Lets now check the child nodes
		Iterator<String> itChild = childNodes.keySet().iterator();
		while (itChild.hasNext()) {
			String childName = (String) itChild.next();
			ChildNodeConfig currentChild = childNodes.get(childName);
			// Check all properties of a child node
			Map<String, NodePropertyDefinition> nodePropsChild = currentChild
					.getProperties();
			Iterator<String> itChildProps = nodePropsChild.keySet().iterator();
			while (itChildProps.hasNext()) {
				String propName = (String) itChildProps.next();
				
				// If our property is found in the list of properties go check
				// if it's a main prop or not
				try {
					if (reference.hasNode(childName)) {
						Node toSet = reference.getNode(childName);
						iMetadataProperty property = getDefaultMetadata().getPropertyById(propName);
						if (property != null) //If it's a default metadata property, set it
							setPropertyValueFromMetadata(property, toSet);
						else{
							//If it's a reference to another node, get the identifier and
							iMetadataItem item = getMetadata(propName);
							if (item != null){
								String identifier = item.getID();
								Node referenceMetadata = p_session.getRootNode().getNode(identifier);
								p_node.setProperty(propName, referenceMetadata);
							}
							
						}
					} else {
						Node toSet = reference.addNode(childName,currentChild.getNodeType());
						System.out.println("Added child " + childName);
						iMetadataProperty property = getDefaultMetadata().getPropertyById(propName);
						if (property != null) //If it's a default metadata property, set it
							setPropertyValueFromMetadata(property, toSet);
						else{
							//If it's a reference to another node, get the identifier and
							iMetadataItem item = getMetadata(propName);
							if (item != null){
								String identifier = item.getID();
								Node referenceMetadata = p_session.getRootNode().getNode(identifier);
								p_node.setProperty(propName, referenceMetadata);
							}
							else
								System.out.println(propName + " does not exist anywhere");
						}
					}
					
					Node toPass = reference.getNode(childName);
					// Check for child nodes under this node
					recursiveFindAndSetProperty(toPass, null);
				}
				catch (RepositoryException e) {
					e.printStackTrace();
					return false;
				}
			}
			
		}
		return true;
	}

	//Atravessa toda a estrutura de nós e cria as properiedades com for necessário
	/**
	 * 
	 * Recursively processes the node structure and sets/creates the nodes and properties
	 * as determined by the node's structural information
	 * 
	 * @param reference The current child node to process
	 * @param childNodes The configuration for the child nodes of this node
	 * 
	 * @throws RepositoryException If something goes wrong
	 */
	private void recursiveFindAndSetProperty(Node reference, Map<String, ChildNodeConfig> childNodes) throws RepositoryException {
		
		if (childNodes != null){
			Iterator<String> itChild = childNodes.keySet().iterator();
			while (itChild.hasNext()) {
				String childName = (String) itChild.next();
				ChildNodeConfig currentChild = childNodes.get(childName);
				// Check all properties of a child node
				Map<String, NodePropertyDefinition> nodePropsChild = currentChild
						.getProperties();
				Iterator<String> itChildProps = nodePropsChild.keySet().iterator();
				while (itChildProps.hasNext()) {
					String propName = (String) itChildProps.next();
					
					if (reference.hasNode(childName)) {
							Node toSet = reference.getNode(childName);
							iMetadataProperty property = getDefaultMetadata().getPropertyById(propName);
							if (property != null) //If it's a default metadata property, set it
								setPropertyValueFromMetadata(property, toSet);
							else{
								//If it's a reference to another node, get the identifier and
								iMetadataItem item = getMetadata(propName);
								String identifier = item.getID();
								Node referenceMetadata = p_session.getRootNode().getNode(identifier);
								p_node.setProperty(propName, referenceMetadata);
							}
					}else {
							Node toSet = reference.addNode(childName,currentChild.getNodeType());
							iMetadataProperty property = getDefaultMetadata().getPropertyById(propName);
							if (property != null) //If it's a default metadata property, set it
								setPropertyValueFromMetadata(property, toSet);
							else{
								//If it's a reference to another node, get the identifier and
								iMetadataItem item = getMetadata(propName);
								String identifier = item.getID();
								Node referenceMetadata = p_session.getRootNode().getNode(identifier);
								p_node.setProperty(propName, referenceMetadata);
							}
					}
					Node toPass = reference.getNode(childName);
					// Check for child nodes under this node
					if (currentChild.getChildNodes() != null)
					recursiveFindAndSetProperty(toPass, currentChild.getChildNodes());
				}
			}
		}
		}

	@Override
	public String getId() {
		return this.getPath();
	}

	@Override
	public List<iMetadataItem> getMetadataByName(String name)
			throws iFileException {
		// TODO Auto-generated method stub
		return null;
	}

	
	/**
	 * 
	 * Retrieves the full path of a Metadata Item from from the root path
	 * 
	 * @param item The {@link iMetadataItem} from which to get the path
	 * 
	 * @return A string builder with
	 */
	private String getFullPathFromMetadataItem(iMetadataItem item){
		
		if (item != null){
			StringBuilder b = new StringBuilder();
			recursiveGetFullPathFromMetadataItem(item, b);
		}
		return null;
	}
	
	/**
	 * 
	 * Retrieves the full path of a metadata item recursively searching the parent
	 * 
	 * @param item The item to retrieve the full path to
	 * @param b The {@link StringBuilder} to keep the result on
	 * 
	 * @return A {@link StringBuilder} with the full path
	 */
	private StringBuilder recursiveGetFullPathFromMetadataItem(iMetadataItem item, StringBuilder b){
		
		if (item != null){
			iMetadataItem p = item.getParent();
			b.insert(0, item.getID());
			if (p != null){
				recursiveGetFullPathFromMetadataItem(p, b);
			}
			else
			{
				if (item instanceof MetadataItem)
				{
					MetadataItem itemMeta = (MetadataItem) item;
					Node itemNode = itemMeta.getNode();
					if (itemNode != null);
						try {
							Node parent = itemNode.getParent();
							if (parent != null)
								b.insert(0, itemNode.getPath());
						} catch (RepositoryException e) {
							e.printStackTrace();
						}
				}
				return b;	
			}
				
		}
		else
			return b;
		
		return null;
	}

	@Override
	public boolean inTransaction() {
		return p_inTransaction;
	}

	@Override
	public void updateFile(iFile newVal) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String[] getFileToDeleteOnCommit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String[] getFileToDeleteOnRollback() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void rollback(EboContext ctx) {
		// TODO Auto-generated method stub
		
	}
	
}
