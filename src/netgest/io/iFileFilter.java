/*Encoding=UTF-8*/
package netgest.io;

/**
 * 
 * A filter for {@link iFile} given a reference to the file
 * 
 * @author PedroRio
 *
 */
public class iFileFilter {
    public iFileFilter() {
    }

    /**
     * 
     * Checks whether this file passed as parameter 
     * 
     * @param p0 The file to check
     * 
     * @return True if the file is accepted and false otherwise
     */
    public boolean accept(iFile p0) {
        return true;
    }
}