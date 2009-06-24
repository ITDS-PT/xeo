/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import netgest.bo.runtime.boRuntimeException;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class ObjectComparator implements Comparator
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public ObjectComparator()
    {
    }
    
    public int compare(Object o1, Object o2)// throws boRuntimeException
    {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) return 1;
        if(o2 == null) return -1;
        
        if(o1 instanceof Date)
        {
            return compare((Date)o1, (Date)o2); 
        }
        if(o1 instanceof String)
        {
            return compare((String)o1, (String)o2); 
        }
        if(o1 instanceof Long)
        {
            return compare((String)o1, (String)o2); 
        }
        if(o1 instanceof Date)
        {
            return compare((Date)o1, (Date)o2); 
        }
        return compare(o1.toString(), o1.toString());
//        throw new boRuntimeException("","Not implemented for class type:" + o1.getClass().getName(),null );
    }
    
    public int compare(Date o1, Date o2)
    {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) return 1;
        if(o2 == null) return -1;
        return o1.compareTo(o2);
    }
    
    public int compare(String o1, String o2)
    {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) return 1;
        if(o2 == null) return -1;
        return o1.compareTo(o2);
    }
    
    public int compare(Long o1, Long o2)
    {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) return 1;
        if(o2 == null) return -1;
        return o1.compareTo(o2);
    }
    
    public int compare(BigDecimal o1, BigDecimal o2)
    {
        if(o1 == null && o2 == null) return 0;
        if(o1 == null) return 1;
        if(o2 == null) return -1;
        return o1.compareTo(o2);
    }
    
    public boolean equals(Object o1, Object o2) throws boRuntimeException
    {
        if(o1 == null && o2 == null) return true;
        if(o1 == null) return false;
        if(o2 == null) return false;
        
        if(o1 instanceof Date)
        {
            return compare((Date)o1, (Date)o2) == 0; 
        }
        if(o1 instanceof String)
        {
            return compare((String)o1, (String)o2) == 0; 
        }
        if(o1 instanceof Long)
        {
            return compare((String)o1, (String)o2) == 0; 
        }
        if(o1 instanceof Date)
        {
            return compare((Date)o1, (Date)o2) == 0; 
        }
        throw new boRuntimeException("","Not implemented for class type:" + o1.getClass().getName(),null );
    }
}