/*Enconding=UTF-8*/
package netgest.io;

/**
 * 
 * A filter for {@link iFile} based on their name
 * 
 * @author PedroRio
 *
 */
public class iFilenameFilter {
    public iFilenameFilter() {
    }

    /**
     * 
     * Checks whether or not a given file name is accepted 
     * by the filter
     * 
     * @param p1 The name of the file
     * @return True if the file is accepted and false otherwise
     */
    public boolean accept(String p1) {
        return true;
    }
}