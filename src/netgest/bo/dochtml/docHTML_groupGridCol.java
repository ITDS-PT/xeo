/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import netgest.bo.def.*;
import java.util.*;

public final class docHTML_groupGridCol  {
    public int p_width;
    public String p_atr;
    public String p_method;
    public String p_viewmode;
    public Hashtable p_attributes;
    public docHTML_groupGridCol(String atr,int width,Hashtable attributes){
        p_width=width;
        p_atr=atr;
        p_attributes=attributes;
    }
    
    public char typeSorted(){
       // returns ' ' if not sorted
       // return 'A' se a coluna estiver ordenada ascedente
       // return 'D' se a coluna estiver ordenada descendente
        return ' ';
    }
}

    