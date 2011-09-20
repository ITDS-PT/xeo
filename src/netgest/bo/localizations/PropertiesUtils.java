package netgest.bo.localizations;
import java.io.IOException; 
import java.io.InputStream; 
import java.util.Properties; 
  
/** 
 * Utility class for reading properties. 
 */
public class PropertiesUtils { 
  
    /** 
     * Private constructor. 
     */
    private PropertiesUtils() { 
    } 
  
    /** 
     * Read a properties file with the utf-8 encoding. 
     * Based on play.libs.IO#readUtf8Properties(InputStream is), version 1.0.x. 
     * @see Play framework (http://www.playframework.org/) 
     * @param is Stream to properties file 
     * @return The Properties object 
     * @throws java.io.IOException 
     */
    public static Properties readUtf8Properties(InputStream is) throws IOException { 
        Properties properties = new Properties(); 
        properties.load(is); 
        for (Object key : properties.keySet()) { 
            String value = properties.getProperty(key.toString()); 
            String goodValue = new String(value.getBytes("iso8859-1"), "utf-8"); 
            properties.setProperty(key.toString(), goodValue); 
        } 
        is.close(); 
        return properties; 
    } 
} 