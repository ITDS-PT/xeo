/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.sorter.ClassSorter;
import netgest.bo.def.boDefAttribute;
import org.apache.log4j.Logger;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class AttributeSorter implements ClassSorter
{
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.sorter.AttributeSorter");
    private static final int LESS = -1;
    private static final int EQUAL = 0;
    private static final int GREATER = 1;
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private String attName = null;
    private String boDefName = null;
    private ObjectComparator oc = new ObjectComparator();
    private boDefHandler bodef = null;
    
    public AttributeSorter(String boDefName, String attName)
    {
        this.attName = attName;
        this.boDefName = boDefName;
        this.bodef = boDefHandler.getBoDefinition(boDefName);
    }
    public void sort(SorterNode[] objects)
    {
        Arrays.sort(objects, this);
    }
    public SorterNode[] getValues(boObjectList bolist)
    {
        SorterNode sn[] = null;
        try
        {
            ArrayList list = new ArrayList();
            bolist.beforeFirst();
            boObject o = null;
            while(bolist.next())
            {
                o = bolist.getObject();
                list.add(new SorterNode(o.getBoui(), o.getAttribute(attName).getValueObject()));
            }
            sn = new SorterNode[list.size()];
            sn = (SorterNode[])list.toArray(sn);
        }
        catch (boRuntimeException e)
        {
            logger.error(e);
        }
        return sn;
    }
    
    public int compare(Object o1, Object o2)
    {
        try
        {
            return compareboObject((SorterNode)o1,(SorterNode)o2);
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public int compareboObject(SorterNode o1, SorterNode o2) throws boRuntimeException
    {
        Object _o1=o1.getValue(), _o2=o2.getValue();
        if(_o1 == null)
        {
            _o1 = getDefaultValue(bodef.getAttributeRef(attName).getValueType());
            
        }
        if(_o2 == null)
        {
            _o2 = getDefaultValue(bodef.getAttributeRef(attName).getValueType());
        }
        return oc.compare(_o1,_o2);
    }
    
    public boolean equals(Object obj)
    {
        return this.getClass().getName().equals(obj.getClass().getName());
    }
    
    private Object getDefaultValue(byte type)
    {
        switch(type)
        {
            case boDefAttribute.VALUE_DATE:
            case boDefAttribute.VALUE_DATETIME:
                    Calendar c = Calendar.getInstance();
                    c.set(Calendar.YEAR, 1900);
                    c.set(Calendar.MONTH, Calendar.JANUARY);
                    c.set(Calendar.DATE, 1);
                    return new Timestamp(c.getTimeInMillis());
            case boDefAttribute.VALUE_NUMBER:
            case boDefAttribute.VALUE_DURATION:
            case boDefAttribute.VALUE_SEQUENCE:
                    return BigDecimal.valueOf(0);
            default:
                 return "";
        }
    }
    public int getAlgorithm()
    {
        return boObjectList.ORDER_LIST;
    }
    public boolean boObjectListOrder()
    {
        return true;
    }
}