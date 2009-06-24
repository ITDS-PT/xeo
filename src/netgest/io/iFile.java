/*Enconding=UTF-8*/
package netgest.io;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface iFile {
    public boolean  canRead();
    public boolean  canWrite();
    public boolean  createNewFile() throws IOException,iFilePermissionDenied;
    public boolean  delete() throws iFilePermissionDenied;
    public boolean  exists();
    public String   getAbsolutePath();
    public String   getName();
    public String   getParent();
    public iFile    getParentFile();
    public String   getPath();
    public boolean  isDirectory();
    public boolean  isFile();
    public long     lastModified();
    public long     length();
    public String[] list() throws iFilePermissionDenied;
    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied;
    public iFile[]  listFiles() throws iFilePermissionDenied;
    public iFile[]  listFiles(iFileFilter filter) throws iFilePermissionDenied;
    public boolean  mkdir() throws iFilePermissionDenied;
    public boolean  mkdirs() throws iFilePermissionDenied;
    public boolean  renameTo(iFile newfile) throws iFilePermissionDenied;
    public boolean  setReadOnly() throws iFilePermissionDenied;
    public OutputStream getOutputStream() throws iFilePermissionDenied;
    public InputStream getInputStream() throws iFilePermissionDenied;
    public boolean checkIn() throws iFilePermissionDenied;
    public boolean checkOut() throws iFilePermissionDenied;
    public boolean isCheckedIn();
    public boolean isCheckedOut();
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
    public long     getVersion();
    
    public long     getKey();
    public iFile    getCopy();
}