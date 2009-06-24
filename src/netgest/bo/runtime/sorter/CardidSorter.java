/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;
import java.util.ArrayList;
import java.util.Arrays;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.sorter.ClassSorter;
import org.apache.log4j.Logger;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class CardidSorter implements ClassSorter
{
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.sorter.CardidSorter");
    private static final int LESS = -1;
    private static final int EQUAL = 0;
    private static final int GREATER = 1;
    private final int ID = "netgest.bo.runtime.sorter.CardidSorter".hashCode();
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public CardidSorter()
    {
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
                list.add(new SorterNode(o.getBoui(), o.getTextCARDID().toString()));
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
    
    public int compareboObject(SorterNode firstAct, SorterNode secAct) throws boRuntimeException
    {
        if(firstAct == null && secAct == null) return 0;
        if(firstAct == null) return 1;
        if(secAct == null) return -1;
        String s1 = (String)firstAct.getValue();
        String s2 = (String)secAct.getValue();
        return s1.compareToIgnoreCase(s2);
    }
    
    public boolean equals(Object obj)
    {
        return this.getClass().getName().equals(obj.getClass().getName());
    }
    public int getAlgorithm()
    {
        return boObjectList.BIDIR_BUBBLE_SORTER;
    }
    public boolean boObjectListOrder()
    {
        return true;
    }
}