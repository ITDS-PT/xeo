/*Enconding=UTF-8*/
package netgest.io;

public class FSiFilePropertiesDB  {
    private iFile p_path;
    public FSiFilePropertiesDB(iFile file) {
        p_path = file.getParentFile();
    }
    public FSiFilePropertiesDB compare(iFile file) {
        if(p_path!=null && file.getParent().equals(file.getPath())) {
            return this;
        } else {
            return new FSiFilePropertiesDB(file);
        }
    }
    public void loadDefinitions() {
        if(p_path.isDirectory())
            throw new RuntimeException("Location of properties repository must be a directory");
    }
}