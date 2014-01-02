/*Enconding=UTF-8*/
package netgest.bo.def;
import java.util.HashMap;

import org.w3c.dom.Node;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public interface boDefForwardObject
{
    public HashMap getMaps();
    
    public String getAfterMapClass();
    
    public String getBeforeMapClass();
    
    public String getOnSaveFwdObject();
    
    public String toBoObject();
    
    public boolean openDoc();
    
    public String getMapMethodName();
    
    public String getAfterMapMethodName();
    
    public String getBeforeMapMethodName();
    
    public String getOnSaveFwdObjectMethodName();
    
    public String getLabel();
    
    //XML
    public Node   getNode();
    
    
}