/*Enconding=UTF-8*/
package netgest.bo.def;
import org.w3c.dom.Node;

public interface boDefClsEvents {

    public String getEventName();
    
    public boDefXeoCode getEventCode();

    public String getAttributeName();
    
    public boolean hasBooleanReturn();
    
    public Node   getNode();
    
}