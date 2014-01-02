/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.*;

public class boBridgesArray  {
    
	private static final Enumeration EMPTY_ENUMERATION = new Hashtable(1).elements();

	Hashtable p_bridges;
    
    public void add(bridgeHandler bridge) 
    {
    	initialize();
        p_bridges.put(bridge.getName(),bridge);
        bridge.getParent().fireEvent( new boEvent( boEvent.EVENT_AFTER_LOADBRIDGE, bridge, null ) );
    }

    private final void initialize() {
    	if( p_bridges == null ) {
    		p_bridges = new Hashtable( 2, 0.50f );
    	}
    }

    public bridgeHandler get(bridgeHandler bridge) 
    { 
    	if( p_bridges != null ) {
        return (bridgeHandler)p_bridges.get(bridge);
    }
    	return null;
    }
    
    public bridgeHandler get(String bridgeName) 
    {
    	if( p_bridges != null ) {
        return (bridgeHandler)p_bridges.get(bridgeName);
    }
    	return null;
    }
    
    public bridgeHandler remove(String bridgeName) 
    {
    	if( p_bridges != null ) {
        return (bridgeHandler)p_bridges.remove(bridgeName);
    }
    	return null;
    }

    public Enumeration elements() 
    {
    	if( p_bridges != null ) {
        return p_bridges.elements();
    }
    	return EMPTY_ENUMERATION;
    }
    
    public void clear()
    {
    	if( p_bridges != null ) {
        p_bridges.clear();
    	}
    }
}