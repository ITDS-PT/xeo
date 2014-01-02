package netgest.bo.presentation.render.elements;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.*;

public class GesClfObjColProv implements ColumnsProvInt
{
    public GesClfObjColProv(EboContext boctx)
    {
        
    }
    
    public void setColumns(EboContext boctx, Hashtable table)
    {
        try
        {
            boObjectList list = boObjectList.list(boctx, "select GESDocClf where tipoClf <> '1'");
            //TODO:Implement Interface LUSITANIA
            //String[] segmentos = Confidencialidade.getSegmentos(boctx, boctx.getBoSession().getPerformerBoui());
            String[] segmentos = {"1"};
            
            list.beforeFirst();
            boObject o, o2, og;
            boBridgeIterator bit;
            String seg;
            ArrayList aux = new ArrayList();
            while(list.next())
            {
                o = list.getObject();
                seg = o.getAttribute("segmento").getValueString();
                if(existsIn(segmentos, seg))
                {
                    og = o.getAttribute("grupo").getObject();
                    if(og != null)
                    {
                        bit = og.getBridge("classificacao").iterator();
                        bit.beforeFirst();
                        while(bit.next())
                        {
                            o2 = bit.currentRow().getObject();
                            if(!aux.contains(String.valueOf(o2.getBoui())))
                            {
                                setColumn(table, o, o2);
                                aux.add(String.valueOf(o2.getBoui()));
                            }
                        }
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            e.printStackTrace();
        }
    }
    
    private void setColumn(Hashtable table, boObject clf, boObject clfObj)
    {
        ClassColumn c = new ClassColumn(clf, clfObj);
        table.put(c.getName(), c);
    }
    
    public String[] getColumnsName()
    {
        return null;
    }
    
    private static boolean existsIn(String[] list, String seg)
    {
        if(list == null || seg == null) return false;
        for (int i = 0; i < list.length; i++) 
        {
            if(seg.equals(list[i]))
            {
                return true;
            }
        }
        return false;
    }
}