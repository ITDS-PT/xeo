/*Enconding=UTF-8*/
package netgest.bo.runtime.sorter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.sorter.ClassSorter;
import netgest.xwf.common.xwfFunctions;
import netgest.bo.system.Logger;

public class ActivtiyDatesComparator implements ClassSorter
{
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.sorter.ActivtiyDatesComparator");

    public ActivtiyDatesComparator()
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
            Date dt1 = null, dt2 = null;
            bolist.beforeFirst();
            boObject o = null;
            while(bolist.next())
            {
                o = bolist.getObject();
                dt1 = xwfFunctions.extractActvDate(o);
                dt2 = o.getAttribute("beginDate").getValueDate();
                list.add(new SorterNode(o.getBoui(), new ValueNode(dt1, dt2)));
            }
            sn = new SorterNode[list.size()];
            sn = (SorterNode[])list.toArray(sn);
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return sn;
    }
    
    public int compare(Object o1, Object o2) {
    
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
        Date dt1 = ((ValueNode)firstAct.getValue()).d1;
        Date dt1_2 = ((ValueNode)firstAct.getValue()).d2;
        Date dt2 = ((ValueNode)secAct.getValue()).d1;
        Date dt2_2 = ((ValueNode)secAct.getValue()).d2;
        if(dt1 != null && dt2 != null)
        {
            int i_comp = dt1.compareTo(dt2); 
            if(i_comp == 0)
            {
                 if(dt1_2 != null && dt2_2 != null)
                    return dt1_2.compareTo(dt2_2);
                else
                    return i_comp;
            }
            else
                return i_comp;
        }
        else
            return 0;
    }
    public int getAlgorithm()
    {
        return boObjectList.QUICK_SORTER;
    }
    public boolean boObjectListOrder()
    {
        return true;
    }
    
    class ValueNode
    {
        public Date d1;
        public Date d2;
        
        public ValueNode(Date d1, Date d2)
        {
            this.d1 = d1;
            this.d2 = d2;
        }
    }
}