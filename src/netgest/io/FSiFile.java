/*Enconding=UTF-8*/
package netgest.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.io.metadata.iMetadataItem;

public class FSiFile implements iFile {
    private File p_file;
    private FSiFileProvider p_fs;
    private String p_uri;
    private static Logger logger = Logger.getLogger(
            "netgest.io.FSiFile");

    public FSiFile(FSiFileProvider fs,File f,String uri) {
        p_fs = fs;
        p_file = new File(f,"");
        p_uri = uri;
    }
    public FSiFile(FSiFileProvider fs,String pathname,String uri) {
        p_fs = fs;
        p_file = new File(pathname);
        p_uri = uri;
    }
    
    public FSiFileProvider getFileProvider()  
    {
        return p_fs;
    }

    public boolean renameTo(iFile newfile) {
        return p_file.renameTo(new File(newfile.getAbsolutePath()));
    }
    public String[] list(iFilenameFilter filter) {
        return filterNames(p_file.list(),filter);
    }
    public String[] list() {
        return p_file.list();
    }
    public iFile[] listFiles(iFileFilter filter) {
        return FilesArrayToFSiFilesArray(p_fs,p_uri,p_file.listFiles(),filter);
    }
    public iFile[] listFiles() {
        return FilesArrayToFSiFilesArray(p_fs,p_uri,p_file.listFiles(),null);
    }
    public iFile getParentFile() {
        String parent=p_file.getParent();
        if(parent==null) return null;
        return (new FSiFile(p_fs,parent,iFileUtils.getParentPath(p_uri,p_fs.separator())));
    }
    public String getAbsolutePath() {
        return p_file.getAbsolutePath();
    }
    public boolean canRead() {
        return p_file.canRead();
    }
    public boolean canWrite() {
        return p_file.canWrite();
    }
    public boolean createNewFile() throws IOException {
        return p_file.createNewFile();
    }

    public boolean delete() {
        return p_file.delete();
    }

    public boolean exists() {
        return p_file.exists();
    }

    public String getName() {
        return p_file.getName();
    }

    public String getParent() {
        return p_file.getParent();
    }

    public String getPath() {
        return p_file.getPath();
    }

    public boolean isDirectory() {
        return p_file.isDirectory();
    }

    public boolean isFile() {
        return p_file.isFile();
    }

    public long lastModified() {
        return p_file.lastModified();
    }

    public long length() {
        return p_file.length();
    }

    public boolean mkdir() {
        return p_file.mkdir();
    }

    public boolean mkdirs() {
        return p_file.mkdirs();
    }

    public boolean setReadOnly() {
        return p_file.setReadOnly();
    }
    public static String[] filterNames(String[] files,iFilenameFilter filter) {
        Vector validfiles = new Vector();
        for(int i=0;i<files.length;i++) {
            if(filter == null) {
                validfiles.add(files[i]);
            }
            else {
                if(filter.accept(files[i]))
                    validfiles.add(files[i]);
            }
        }
        return (String[])validfiles.toArray();
    }
    public static iFile[] FilesArrayToFSiFilesArray(FSiFileProvider fs,String uri,File[] files,iFileFilter filter) {
        
        Vector validfiles = new Vector();
        for(int i=0;i<files.length;i++) {
            if(filter == null) {
                validfiles.add((iFile)(new FSiFile(fs,files[i],iFileUtils.concatPath(uri,files[i].getName(),fs.separator()))));
            }
            else {
                iFile cfile = new FSiFile(fs,files[i],iFileUtils.concatPath(uri,files[i].getName(),fs.separator()));
                if(filter.accept(cfile))
                    validfiles.add(cfile);
            }
        }
        iFile[] ifiles = new iFile[0];
        Object[] obj = validfiles.toArray(ifiles);
        ifiles = (iFile[])obj;
        
        return ifiles;
    }
    
    public void setBinaryStream(InputStream is) throws iFilePermissionDenied,
    		iFilePermissionDenied {
    	
    	netgest.utils.IOUtils.copy(is, p_file);
    	
    }
    
    public OutputStream getOutputStream() {
        try {
            return (new FileOutputStream(p_file));
        } catch(FileNotFoundException e) {
            throw(new RuntimeException(e.getMessage()));
        }
    }
    public InputStream getInputStream() {
        try {
            return (new FileInputStream(p_file));
        } catch(FileNotFoundException e) {
            throw(new RuntimeException(e.getMessage()));
        }
    }
    public boolean checkIn() throws iFilePermissionDenied {
        return false;
    }

    public boolean checkOut() throws iFilePermissionDenied {
        return false;
    }

    public boolean isCheckedIn() {
        return false;
    }

    public boolean isCheckedOut() {
        return false;
    }

    public boolean isVersioned() {
        return false;
    }

    public boolean makeVersioned() throws iFilePermissionDenied {
        return false;
    }
    public String getURI() 
    {
        if(p_uri!=null)
            return p_uri;
        else
            return p_file.getAbsolutePath();
    }
    public void     setDescription(String description) {throw new RuntimeException("setDescription:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public String   getDescription() {throw new RuntimeException("getDescription:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public void     setAuthor(String author) {throw new RuntimeException("setAuthor:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public String   getAuthor() {throw new RuntimeException("getAuthor:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public void     setCategory(String author) {throw new RuntimeException("setCategory:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public String   getCategory() {throw new RuntimeException("getCategory:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}    
    public void     setVersionUser(String user) {throw new RuntimeException("setVersionUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public String   getVersionUser() {throw new RuntimeException("getVersionUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public void     setCheckOutUser(String user) {throw new RuntimeException("setCheckOutUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public String   getCheckOutUser() {throw new RuntimeException("getCheckOutUser:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}    
    public long     getVersion() {throw new RuntimeException("getVersion:"+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    
    public long     getKey() {throw new RuntimeException("getKey : "+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}
    public iFile     getCopy() {throw new RuntimeException("getCopy : "+MessageLocalizer.getMessage("NOT_IMPLEMENTED"));}

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
		throw new iFileException(MessageLocalizer.getMessage("NOT_IMPLEMENTED"));
	}
	@Override
	public boolean save(EboContext ctx) throws iFileException {
		return false;
	}
	@Override
	public String getId() {
		return p_file.getName();
	}
	@Override
	public void addMetadata(iMetadataItem item) throws iFileException {
		
	}
	@Override
	public List<iMetadataItem> getMetadataByName(String name)
			throws iFileException {
		return null;
	}
	@Override
	public boolean inTransaction() {
		return false;
	}
	@Override
	public void updateFile(iFile newVal) {
	}
	@Override
	public String[] getFileToDeleteOnCommit() {
		return null;
	}
	@Override
	public String[] getFileToDeleteOnRollback() {
		return null;
	}
	@Override
	public void rollback(EboContext ctx) {
	}
}
