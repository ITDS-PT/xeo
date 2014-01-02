/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.io.File;
import java.util.Comparator;

/**
 * 
 * @Company Enlace3
 * @author Francisco Câmara
 * @version 1.0
 * @since 
 */
public class boFileComparator implements Comparator 
{
    private Long id;
    
    public boFileComparator()
    {
    }
    public int compare(Object o1, Object o2)
    {
        return ((File)o1).getAbsolutePath().toLowerCase().compareTo(((File)o2).getAbsolutePath().toLowerCase());
    }
}