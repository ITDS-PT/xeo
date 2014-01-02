/*Enconding=UTF-8*/
package netgest.io;

import netgest.bo.localizations.MessageLocalizer;

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
            throw new RuntimeException(MessageLocalizer.getMessage("LOCATION_OF_PROPERTIES_REPOSITORY_MUST_BE_A_DIRECTORY"));
    }
}