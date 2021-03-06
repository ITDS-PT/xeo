/*Enconding=UTF-8*/
package netgest.io;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import netgest.bo.runtime.EboContext;
import netgest.io.metadata.iMetadataItem;

/**
 * 
 * An abstract representation of a file in XEO
 * 
 * 
 * @author João Paulo Carreira
 * @author PedroRio
 *
 */
public interface iFile {
	
	
	/**
	 * The name of the default metadata item
	 */
	public static final String DEFAULT_METADATA = "default";
	
    public boolean  canRead();
    public boolean  canWrite();
    public boolean  createNewFile() throws IOException,iFilePermissionDenied;
    /**
     * 
     * Attempts to delete the existing {@link iFile}, if it's a folder
     * it must not have any children in order for delete to succeed
     * 
     * @return True if the file/folder was successfully deleted
     * and false otherwise
     * 
     * @throws iFilePermissionDenied If the user does not have permission
     * to delete this file
     */
    public boolean  delete() throws iFilePermissionDenied;
    /**
     * 
     * Checks whether or not whether this {@link iFile} physically
     * exists in the implementation persistence
     * layer
     * 
     * @return True if the file exists physically and false otherwise
     * 
     * 
     */
    public boolean  exists();
    /**
     * 
     * Retrieves the absolute path to this {@link iFile}
     * 
     * @return A string with the absolute path to this item
     * 
     */
    public String   getAbsolutePath();
    /**
	 * 
	 * Retrieves the filename
	 * 
	 * @return The file name
	 */
    public String   getName();
    /**
	 * 
	 * Retrieves the filename of the parent folder
	 * 
	 * @return The file name of the parent folder
	 */
    public String   getParent();
    /**
     * 
     * Retrieves a reference to the parent folder
     * 
     * @return A {@link iFile} representing the parent folder
     */
    public iFile    getParentFile();
    public String   getPath();
    /**
     * 
     * Retrieves whether this {@link iFile} represents
     * a directory
     * 
     * @return True if it represents a folder/directory
     * and false otherwise
     */
    public boolean  isDirectory();
    /**
     * 
     * Retrieves whether this {@link iFile} represents 
     * a file
     * 
     * @return True if it represents a file and false otherwise
     */
    public boolean  isFile();
    /**
     * 
     * The date of the last modified in Unix time
     * see {@link File#lastModified()}
     * 
     * @return
     */
    public long     lastModified();
    /**
     * 
     * Retrieves the lenght of the binary part of this file
     * 
     * @return The number of bytes of the file
     */
    public long     length();
    
    /**
     * 
     * Retrieves the name of all files that are children
     * of this folder
     * 
     * @return An array of strings with the name of the files/folder under this folder, 
     * empty list if none and null if the {@link iFile } is not a folder
     * 
     * @throws iFilePermissionDenied If some of the files in the list
     * have their {@link #canRead()} method return false
     */
    public String[] list() throws iFilePermissionDenied;
    /**
     * 
     * Retrieves the name of all files/folders that are children of this folder
     * that match a giving {@link iFilenameFilter}
     * 
     * @param filter The filter implementation
     * @return An array of strings with list of filenames that match the filter
     * 
     * @throws iFilePermissionDenied If some of the files in the list
     * have their {@link #canRead()} method return false
     */
    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied;
    /**
     * 
     * Retrieves a reference to all {@link iFile } (files/folders) 
     * that are children of this {@link iFile }
     * 
     * @return An array of references to child {@link iFile }
     * 
     * @throws iFilePermissionDenied If some of the files in the list
     * have their {@link #canRead()} method return false
     */
    public iFile[]  listFiles() throws iFilePermissionDenied;
    
    
    /**
     * 
     * Retrieves a reference to all {@link iFile } (files/folders)
     * that are children of this {@link iFile } and match the filter
     * passed as parameter
     * 
     * @param filter The filter implementation
     * 
     * @return An array of references to child {@link iFile }
     * that match the given filter
     * 
     * @throws iFilePermissionDenied If some of the files in the list
     * have their {@link #canRead()} method return false
     */
    public iFile[]  listFiles(iFileFilter filter) throws iFilePermissionDenied;
    
 
    /**
     * 
     * Creates the directory named by this abstract pathname.
     * Does not create multiple directories, use {@link iFile#mkdirs()} to achieve
     * that
     * 
     * @return True if the directory was created and false otherwise
     *  
     */
    public boolean  mkdir() throws iFilePermissionDenied;
    /**
     * 
     * Creates the directory named by this abstract pathname, including 
     * any necessary but nonexistent parent directories. 
     * Note that if this operation fails it may have succeeded in 
     * creating some of the necessary parent directories.
     * 
     * @return True if all directories were created and false otherwise
     * 
     */
    public boolean  mkdirs() throws iFilePermissionDenied;
    
    public boolean  renameTo(iFile newfile) throws iFilePermissionDenied;
    public boolean  setReadOnly() throws iFilePermissionDenied;
    /**
	 * 
	 * Sets the content of the file with the {@link InputStream} passed
	 * as parameter. See ({@link #setBinaryContent(byte[]))}
	 * 
	 * @param is The input stream with the binary content of the file
	 * 
	 */
    public void setBinaryStream( InputStream is ) throws iFilePermissionDenied;
    /**
     * 
     * Retrieves a stream pointing to the binary content of the file
     * 
     * @return A stream to the binary content of the file
     */
    public InputStream getInputStream() throws iFilePermissionDenied;

    public boolean checkIn() throws iFilePermissionDenied;
    public boolean checkOut() throws iFilePermissionDenied;
    /**
     * 
     * Checks whether or not this file is checked out
     * 
     * @return True if the file is checked out and false otherwise
     */
    public boolean isCheckedOut();
    /**
     * 
     * Checks whether or not this file is checked in
     * 
     * @return True if the file is checked in and false otherwise
     * 
     */
    public boolean isCheckedIn();
    public boolean isVersioned();
    
  
    public boolean makeVersioned() throws iFilePermissionDenied;

    public void     setDescription(String description);
    public String   getDescription();
    public void     setAuthor(String author);
    public String   getAuthor();
    public void     setCategory(String author);
    public String   getCategory();
    public String   getURI();
    public void     setVersionUser(String user);
    public String   getVersionUser(); 
    public void     setCheckOutUser(String user);
    public String   getCheckOutUser();         
    /**
     * 
     * Retrieves the version number of this iFile
     * 
     * @return the version number of the iFile or null
     * if versioning is not supported
     */
    public long     getVersion();
    
    public long     getKey();
    
    /**
     * 
     * Retrieves the Key of the {@link iFile} as a string value
     * 
     * @return A string with the key of the object
     */
    public String     getId();
    
    public iFile    getCopy();
    
    
    
    /**
	 * 
	 * Retrieves a list of {@link iMetadataItem} about the file given
	 * its name.
	 * 
	 * @param identifier The identifier of the metadata item 
	 * 
	 * 
	 * @return An <code>iFileMetadata</code>
     * @throws iFileException 
	 */
	public iMetadataItem getMetadata(String identifier) throws iFileException;
	
	/**
	 * 
	 * Adds a reference of a given {@link iMetadataItem} to
	 * this {@link iFile} with a given type
	 * 
	 * 
	 * @param item A reference to item to reference
	 * @throws iFileException 
	 */
	public void addMetadata(iMetadataItem item) throws iFileException;
	

	
	/**
	 * 
	 * Removes the association of this {@link iFile} with a given
	 * {@link iMetadataItem} identified by the value passed as parameter
	 * If such association does not exist, nothing happens
	 * 
	 * @param identifier The identifier of a {@link iMetadataItem}
	 * 
	 */
	public void removeMetadata(String identifier);
	
	
	/**
	 * 
	 * Retrieves a list with all meta data items from this file
	 * 
	 * @return A list of meta data items (empty list if none)
	 * @throws iFileException 
	 */
	public List<iMetadataItem> getAllMetadata() throws iFileException;
	
	/**
	 * 
	 * Retrieves a list of meta data items with a given name from this file 
	 * 
	 * @param name The name of metadata items
	 * 
	 * @return A list of {@link iMetadataItem}
	 * 
	 * @throws iFileException
	 */
	public List<iMetadataItem> getMetadataByName(String name) throws iFileException;
	
	
	/**
	 * 
	 * Retrieves the list of type of relations of this {@link iFile}
	 * with {@link iMetadataItem}s, a file can be related to
	 * a meta data item by a given type in order to organize its meta data
	 * 
	 * @return A list of string with the types of meta data
	 * relations this {@link iFile} has. Empty list if none
	 * 
	 *
	 */
	public List<String> getMetadataTypes();
	
	
	/**
	 * 
	 * Closes any resources being held up by the file
	 * 
	 * @return True if all resources were closed
	 * and false otherwise
	 */
	public boolean close();
	
	/**
	 * 
	 * The default metadata item is the set of properties that are
	 * not grouped under a name, like other metadata items.
	 * If one wants to simply set properties in an item
	 * the default metadata item should be used
	 * 
	 * 
	 * @return THe default metadata item
	 */
	public iMetadataItem getDefaultMetadata();
	
	
	/**
	 * 
	 * Adds a new file to this {@link iFile}, only works when {@link #isDirectory()} returns true
	 * otherwise throws an {@link iFileException}
	 * 
	 * @param file The file to add
	 * 
	 * @return True if the file is added to this folder and false otherwise
	 * 
	 * @throws iFileException If the 
	 */
	public boolean addChild(iFile file) throws iFileException;
	
	
	/**
	 * 
	 * Saves the current iFile (persistently)
	 * 
	 * @param ctx The XEO context
	 * @return True if the {@link iFile} is saved and false otherwise
	 * 
	 * @throws iFileException If any error occurs
	 */
	public boolean save(EboContext ctx) throws iFileException;
	
	
	/**
	 * 
	 * Retrieves whether or not the {@link iFile} is in a transaction
	 * 
	 * @return True if the iFile is in a Transaction and false otherwise
	 */
	public boolean inTransaction();
	
	/**
	 * 
	 * Retrieves the files that need to be removed on an successful commit
	 * 
	 * @return The list of files ID or null if none need to be removed
	 */
	public String[] getFileToDeleteOnCommit();
	
	/**
	 * 
	 * Retrieves the files that need to be removed on an rollback
	 * 
	 * @return The list of files ID or null if none need to be removed
	 */
	public String[] getFileToDeleteOnRollback();

	/**
	 * 
	 * Updates the file with the contents and name of an FSIfile
	 * 
	 */
	public void updateFile(iFile newVal);
	
	/**
	 * 
	 * Rollsback the ifile
	 * 
	 */	
	public void rollback(EboContext ctx);
}