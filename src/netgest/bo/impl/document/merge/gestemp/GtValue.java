package netgest.bo.impl.document.merge.gestemp;
import java.util.ArrayList;

public class GtValue 
{
    private ArrayList listValues = null;
    private Object value = null;
    
    public GtValue()
    {
    }
    
    public static GtValue getDefault()
    {
        GtValue v = new GtValue();
        v.addValue("Valor por omissão");
        return v;
    }
    
    public static GtValue getDefaultList()
    {
        GtValue v = new GtValue();
        ArrayList d = new ArrayList();
        d.add("Valor por omissão 1");
        d.add("Valor por omissão 2");
        d.add("Valor por omissão 3");
        d.add("Valor por omissão 4");
        d.add("Valor por omissão 5");
        v.addValues(d);
        return v;
    }
    
    public void clear()
    {
        listValues = null;
        value = null;
    }
    
    public void addValues(ArrayList newValues)
    {
        this.listValues = newValues;
    }
    
    public void addValue(Object newValue)
    {
        this.value = newValue;
    }
    
    public ArrayList getValues()
    {
        return listValues;
    }
    
    public Object getValue()
    {
        return value;
    }
}