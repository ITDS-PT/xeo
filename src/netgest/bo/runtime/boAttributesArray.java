/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.*;

public class boAttributesArray  {
	
	private static final Enumeration EMPTY_ENUMERATION = new Hashtable(1).elements();
    Hashtable p_attributes=null;
    
    private final void initialize() {
    	if( p_attributes == null ) {
    		p_attributes = new Hashtable( 5, 0.50f );
    	}
    }
    
    public void add(AttributeHandler attr) {
    	initialize();
        p_attributes.put(attr.getName(),attr);
    }
    public AttributeHandler get(AttributeHandler attr) {
    	if( p_attributes != null ) {
        return (AttributeHandler)p_attributes.get(attr);
    }
    	return null;
    }
    public Enumeration elements() {
    	if( p_attributes != null ) {
        return p_attributes.elements();
    }
    	else {
    		return EMPTY_ENUMERATION;    		
    	}
    }
    
    public AttributeHandler get(String name) {
    	if( p_attributes != null ) {
        return (AttributeHandler)p_attributes.get(name);
    	}
    	return null;
    }
}