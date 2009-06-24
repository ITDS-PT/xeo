/*Enconding=UTF-8*/
package netgest.bo.runtime;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boReverseAttributesArray extends boAttributesArray
{
    /**
     * 
     * @since 
     */
    public void add(String bridge,AttributeHandler attr,int line)
    {
        // TODO:  Override this netgest.bo.runtime.boAttributesArray method
        super.p_attributes.put(bridge+"."+attr.getName()+"."+line,attr);
    }
}