package netgest.bo.presentation.render.elements;
import java.util.Hashtable;
import netgest.bo.runtime.EboContext;

public interface ColumnsProvInt 
{
    
    public void setColumns(EboContext boctx, Hashtable table);
    
    public String[] getColumnsName();
}