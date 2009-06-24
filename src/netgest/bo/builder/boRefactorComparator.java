/*Enconding=UTF-8*/
package netgest.bo.builder;
import java.util.Comparator;
import java.util.Date;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

/**
 * 
 * @Company Enlace3
 * @author Francisco Câmara
 * @version 1.0
 * @since 
 */
public class boRefactorComparator implements Comparator 
{
    private Long id;
    
    public boRefactorComparator()
    {
    }
    public int compare(Object o1, Object o2)
    {
        ngtXMLHandler xmlO1 = (ngtXMLHandler)o1;
        ngtXMLHandler xmlO2 = (ngtXMLHandler)o2;
        Date dateO1 = ClassUtils.convertToDate(xmlO1.getAttribute("date"));
        Date dateO2 = ClassUtils.convertToDate(xmlO2.getAttribute("date"));
        return (dateO1.compareTo(dateO2));
    }
}