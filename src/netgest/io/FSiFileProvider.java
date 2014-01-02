/*Enconding=UTF-8*/
package netgest.io;
import java.io.*;
import java.util.Hashtable;

import netgest.bo.localizations.MessageLocalizer;

public class FSiFileProvider extends iFileProvider {
    private boolean connected=false;
    private String p_servicename;
    public iFile getFile(String pathname,String uri) {
        if(!connected)
            throw(new RuntimeException(MessageLocalizer.getMessage("NO_ACTIVE_CONNECTION")));
        return (iFile)(new FSiFile(this, pathname,uri));
    }
    public iFile[] listRoots() {
        if(!connected)
            throw(new RuntimeException(MessageLocalizer.getMessage("NO_ACTIVE_CONNECTION")));
            
        return FSiFile.FilesArrayToFSiFilesArray(this,"//"+p_servicename+"/",File.listRoots(),null);
    }
    public void setParameters(Hashtable ht) {};
    public void close() {connected=false;};
    public boolean open(String servicename,String connectionstring) {
        p_servicename=servicename;
        return connected=true;
    }
    public boolean open(String service,String name,String password,String connectionstring) {
        p_servicename=service;
        return connected=true;
    }
    public boolean supportsVersioning() {
        return false;
    }
}