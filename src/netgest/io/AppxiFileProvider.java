package netgest.io;
import com.otg.applicaxtender.AEXDBLib.*;
import com.develop.jawin.*;

public class AppxiFileProvider extends iFileProvider  
{
    private boolean connected=false;
    private String userName;
    private String password;
    private AppXtenderApi service;
     
    public iFile getFile(String path,String uri)
    {
        if(!connected)
            throw(new RuntimeException("No active connection"));
        return (iFile)(new AppxiFile(this, path));        
    }
    
    public iFile[] listRoots()
    {
        return null;   
    }
    
    public boolean open(String servicename,String dataSource)
    {
        service = new AppXtenderApi("SYSOP","OTG",servicename, dataSource);
        connected = true;
        return connected;    
    }
    
    public boolean open(String servicename,String username,String password,String dataSource)
    {
        this.userName = username;
        this.password = password;
        service = new AppXtenderApi(username,password,servicename, dataSource);
        connected = true;
        return connected;
    }
    
    public void close()
    {
        connected=false;
    }
    
    public void setParameters(java.util.Hashtable ht)
    {
        
    }
    
    public boolean supportsVersioning() 
    {
        return false;
    }
    
    public AppXtenderApi getAPI() 
    {
        return service;
    }
    
}