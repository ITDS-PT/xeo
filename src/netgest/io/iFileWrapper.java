/*Enconding=UTF-8*/
package netgest.io;
import java.io.IOException;
import java.io.FilenameFilter;
import java.io.FileFilter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import netgest.bo.runtime.EboContext;
import netgest.io.metadata.iMetadataItem;

public class iFileWrapper implements iFile {
    private iFile p_ifile;
    private iFileService p_ifileservice;
    public iFileWrapper(iFileService ifs,iFile file) {
        p_ifileservice = ifs;
        p_ifile = file;
    }

    public boolean canRead() {
        return p_ifile.canRead();
    }

    public boolean canWrite() {
        return p_ifile.canWrite();
    }

    public boolean createNewFile() throws IOException,iFilePermissionDenied {
        return p_ifile.createNewFile();
    }

    public boolean delete() throws iFilePermissionDenied {
        return p_ifile.delete();
    }

    public boolean exists() {
        return p_ifile.exists();
    }

    public String getAbsolutePath() {
        return p_ifile.getAbsolutePath();
    }

    public String getName() {
        return p_ifile.getName();
    }

    public String getParent() {
        return p_ifile.getParent();
    }

    public iFile getParentFile() {
        return this.convertToiFileWrapper(p_ifile.getParentFile());
    }

    public String getPath() {
        return p_ifile.getPath();
    }

    public boolean isDirectory() {
        return p_ifile.isDirectory();
    }

    public boolean isFile() {
        return p_ifile.isDirectory();
    }

    public long lastModified() {
        return p_ifile.lastModified();
    }

    public long length() {
        return p_ifile.length();
    }

    public String[] list() throws iFilePermissionDenied {
        return p_ifile.list();
    }

    public String[] list(iFilenameFilter filter) throws iFilePermissionDenied {
        return p_ifile.list(filter);
    }

    public iFile[] listFiles() throws iFilePermissionDenied {
        return this.convertToiFileWrapper(p_ifile.listFiles());
    }

    public iFile[] listFiles(iFileFilter filter) throws iFilePermissionDenied {
        return this.convertToiFileWrapper(p_ifile.listFiles(filter));
    }

    public boolean mkdir() throws iFilePermissionDenied {
        return p_ifile.mkdir();
    }

    public boolean mkdirs() throws iFilePermissionDenied {
        return p_ifile.mkdirs();
    }

    public boolean renameTo(iFile newfile) throws iFilePermissionDenied {
        return p_ifile.renameTo(newfile);
    }

    public boolean setReadOnly() throws iFilePermissionDenied {
        return p_ifile.setReadOnly();
    }

    private iFile[] convertToiFileWrapper(iFile[] ifile) {
        if(ifile == null) return null;
        iFileWrapper[] retif = new iFileWrapper[ifile.length];
        for(int i=0;i<ifile.length;i++) {
            retif[i] = new iFileWrapper(p_ifileservice,ifile[i]);
        }
        return retif;
    }
    private iFile convertToiFileWrapper(iFile ifile) {
        if(ifile == null) return null;
        iFileWrapper retif = new iFileWrapper(p_ifileservice,p_ifile);
        return retif;
    }

    public void setBinaryStream(InputStream is) throws iFilePermissionDenied,
    		iFilePermissionDenied {
    
    	p_ifile.setBinaryStream( is );

    }
    
    public InputStream getInputStream()  throws iFilePermissionDenied {
        return p_ifile.getInputStream();
    }

    public boolean checkIn() throws iFilePermissionDenied {
        return p_ifile.checkIn();
    }

    public boolean checkOut() throws iFilePermissionDenied {
        return p_ifile.checkOut();
    }

    public boolean isCheckedIn() {
        return p_ifile.isCheckedIn();
    }

    public boolean isCheckedOut() {
        return p_ifile.isCheckedOut();
    }
    
    public boolean isVersioned() {
        return p_ifile.isVersioned();
    }

    public boolean makeVersioned() throws iFilePermissionDenied {
      return p_ifile.makeVersioned();
    }
    public String getURI() 
    {
        return p_ifile.getURI();
    }
    
    public void     setDescription(String description) {throw new RuntimeException("setDescription:Not Impelemented");}
    public String   getDescription() {throw new RuntimeException("getDescription:Not Impelemented");}
    public void     setAuthor(String author) {throw new RuntimeException("setAuthor:Not Impelemented");}
    public String   getAuthor() {throw new RuntimeException("getAuthor:Not Impelemented");}
    public void     setCategory(String author) {throw new RuntimeException("setCategory:Not Impelemented");}
    public String   getCategory() {throw new RuntimeException("getCategory:Not Impelemented");}
    
    public long     getKey() {throw new RuntimeException("getKey : Not Impelemented");}
    public iFile     getCopy() {throw new RuntimeException("getCopy : Not Impelemented");}
    
    public void setVersionUser(String user)
    {
        p_ifile.setVersionUser(user);
    }
    public String getVersionUser()
    {
      return p_ifile.getVersionUser();
    }
    public void setCheckOutUser(String user)
    {
        p_ifile.setCheckOutUser(user);
    }
    public String getCheckOutUser()
    {
      return p_ifile.getCheckOutUser();
    }    
    public long getVersion()
    {
      return p_ifile.getVersion();
    }

 
	@Override
	public List<iMetadataItem> getAllMetadata() {
		//File system does not implement metadata
		return new LinkedList<iMetadataItem>();
	}
	@Override
	public iMetadataItem getMetadata(String name) {
		//File system does not implement metadata
		return null;
	}
	@Override
	public List<String> getMetadataTypes() {
		//File system does not implement metadata
		return new LinkedList<String>();
	}
	@Override
	public void removeMetadata(String metadataItemId) {
		//File system does not implement metadata
	}
	@Override
	public boolean close() {
		return false;
	}

		@Override
	public iMetadataItem getDefaultMetadata() {
		return null;
	}

	@Override
	public boolean addChild(iFile file) throws iFileException {
		throw new iFileException("Not implemented");
	}

	@Override
	public boolean save(EboContext ctx) throws iFileException {
		return false;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public void addMetadata(iMetadataItem item) throws iFileException {
		
	}

	@Override
	public List<iMetadataItem> getMetadataByName(String name)
			throws iFileException {
		return null;
	}  
}
