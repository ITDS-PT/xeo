/*Enconding=UTF-8*/
package netgest.io;
import java.util.Hashtable;

public class iFileServicePermissions  {
    private Hashtable ht = new Hashtable();
    public iFileServicePermissions() {
        setPermissions("public",true,true,false,false,false,false);
    }
    public void setPermissions(String group,
                          boolean browse,
                          boolean read,
                          boolean write,
                          boolean createnew,
                          boolean delete,
                          boolean versioncontrol
                          ) 
    {

        iFileServicePermissionsGroup ip=null;

        if(ht.size()>0)
            ip = (iFileServicePermissionsGroup)ht.get(group);
            
        if(ip==null)
            ip = new iFileServicePermissionsGroup();
        ip.browse=browse;
        ip.read=read;
        ip.write=write;
        ip.createnew=createnew;
        ip.delete=delete;
        ip.versioncontrol=versioncontrol;
        ht.put(group,ip);
        
    }
    public boolean canBrowse(String groupname) {
        return getGroup(groupname).browse;
    }
    public boolean canRead(String groupname) {
        return getGroup(groupname).read;
    }
    public boolean canWrite(String groupname) {
        return getGroup(groupname).write;
    }
    public boolean canCreateNew(String groupname) {
        return getGroup(groupname).createnew;
    }
    public boolean canDelete(String groupname) {
        return getGroup(groupname).delete;
    }
    public boolean canVersionControl(String groupname) {
        return getGroup(groupname).versioncontrol;
    }
    private iFileServicePermissionsGroup getGroup(String groupname) {
        iFileServicePermissionsGroup ip = (iFileServicePermissionsGroup)ht.get(groupname);
        if(ip==null) {
            ip = (iFileServicePermissionsGroup)ht.get("Public");
        }
        return ip;
    }
    private class iFileServicePermissionsGroup {
        public String groupname;
        public boolean browse=true,read=true,write=true,versioncontrol=true,createnew=true,delete=true;
    }
}
