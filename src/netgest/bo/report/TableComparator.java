/*Enconding=UTF-8*/
package netgest.bo.report;
import java.util.Comparator;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class TableComparator implements Comparator
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public TableComparator()
    {
    }

    public int compare(Object o1, Object o2)
    {
        XMLObject ob1 = (XMLObject) o1;
        XMLObject ob2 = (XMLObject) o2;
        return ob1.getTableName().compareToIgnoreCase(ob2.getTableName());
    }
}