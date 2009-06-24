/*Enconding=UTF-8*/
package netgest.io;

public abstract class iFileProvider  {
    public abstract iFile getFile(String path,String uri);
    public abstract iFile[] listRoots();
    public String pathSeparator() {return "/";}
    public char pathSeparatorChar() {return '/';}
    public String separator() {return "/";}
    public char separatorChar() {return '/';}
    public abstract boolean open(String servicename,String connectionstring);
    public abstract boolean open(String servicename,String username,String password,String connectionstring);
    public abstract void close();
    public abstract void setParameters(java.util.Hashtable ht);
    public abstract boolean supportsVersioning();
}