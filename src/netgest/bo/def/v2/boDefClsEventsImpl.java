/*Enconding=UTF-8*/
package netgest.bo.def.v2;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.v2.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public class boDefClsEventsImpl extends ngtXMLHandler implements boDefClsEvents
{
    private boDefHandlerImpl    p_defhandler;    
    private boDefXeoCodeImpl    p_code;

    private String              p_name;
    private String              p_attName;
    
    public boDefClsEventsImpl(boDefHandlerImpl bodef, Node x) 
    {
        this( bodef, x, null );
    }
    
    public boDefClsEventsImpl( boDefHandlerImpl bodef, Node x, String attName ) 
    {
        super(x);
        p_defhandler = bodef;
        p_attName    = attName;
        refresh();
    }
    
    public boolean hasBooleanReturn() 
    {
        return p_name.startsWith("onBefore");
    }
    
    private void refresh() 
    {
        p_name  = getAttribute("name","");
        ngtXMLHandler bodyNode = getChildNode("body");
        if( bodyNode != null )
        {
            p_code = new boDefXeoCodeImpl( bodyNode.getAttribute("language"), null, bodyNode.getText() );
        }
    }
    public String getEventName() {
        return p_name;
    }

    public boDefXeoCode getEventCode() 
    {
        return p_code;
    }

    public String getAttributeName()
    {
        return p_attName;
    }
    
}