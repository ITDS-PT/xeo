/*Enconding=UTF-8*/
package netgest.io;

import netgest.bo.localizations.MessageLocalizer;

public class iFileService {
    public static final String PREFIX = "//";
    public String CLASSNAME,
                  NAME,
                  DESCRIPTION,
                  USERNAME,
                  PASSWORD,
                  CONNECTIONSTRING,
                  ADMINGROUP,
                  PATH,
                  ICON;
                  
    private iFileProvider p_ifile;        
    private iFileServicePermissions p_perm = new iFileServicePermissions();
    public iFileService(String classname,String name,String description,String username,String password,String path,String connectionstring,String admingroup,String icon) {
        CLASSNAME = classname;
        NAME=name;
        DESCRIPTION=description;
        USERNAME=username;
        PASSWORD=password;
        CONNECTIONSTRING=connectionstring;
        ADMINGROUP=admingroup;
        PATH=path;
        ICON=icon;
        
        Class _class;
        Object _object;
        try {
            _class = Class.forName(classname);
            _object = _class.newInstance();
           // if (!(_object instanceof iFileService)) {
             //   throw(new RuntimeException("Classe não extende iFileProvider. ["+classname+"]."));
            //}
        } catch (ClassNotFoundException e) {
            throw(new RuntimeException(MessageLocalizer.getMessage("UNABLE_TO_FIND_THE_CLASS")+" ["+classname+"]."));
        } catch (IllegalAccessException e) {
            throw(new RuntimeException(MessageLocalizer.getMessage("UNABLE_TO_ACCESS_THE_CLASS")+" ["+classname+"] (IllegalAccessException)."));
        } catch (InstantiationException e) {
            throw(new RuntimeException(MessageLocalizer.getMessage("UNABLE_TO_INSTANTIATE_THE_CLASS")+" ["+classname+"] (InstantiationException)."));
        }
        p_ifile = (iFileProvider)_object;
    }
    public boolean open() {
        return p_ifile.open(NAME,USERNAME,PASSWORD,CONNECTIONSTRING);
    }
    public boolean open(String username,String password,String connectionstring) {
        return p_ifile.open(NAME,username,password,connectionstring);
    }
    public void close() {
        p_ifile.close();
    }
    public iFile[] listRoots() {
        return p_ifile.listRoots();
    }
    public iFile getFile() {
        return (new iFileWrapper(this,p_ifile.getFile(PATH,"//"+NAME+"/")));
    }
    public iFile getFile(String pathtofile) {
        if(pathtofile.startsWith("/"))pathtofile=pathtofile.substring(1);
        return p_ifile.getFile(PATH+pathtofile,"//"+NAME+"/"+pathtofile);
    }
    public iFileServicePermissions getPermissions() {
        return p_perm;
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
        p_perm.setPermissions(group,browse,read,write,createnew,delete,versioncontrol);
    }

    public boolean supportsVersioning() {
        return p_ifile.supportsVersioning();
    }
}