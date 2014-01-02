/*Enconding=UTF-8*/
package netgest.utils;
import java.util.Properties;

public class ngtProperties extends Properties  {
    public String getProperty(String p0) {
        // TODO:  Override this java.util.Properties method
        return super.getProperty(p0.toUpperCase());
    }

    public synchronized Object setProperty(String p0, String p1) {
        // TODO:  Override this java.util.Properties method
        return super.setProperty(p0.toUpperCase(), p1);
    }
}