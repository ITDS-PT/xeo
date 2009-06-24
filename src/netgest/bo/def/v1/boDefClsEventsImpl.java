/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import netgest.bo.def.boDefClsEvents;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.def.v1.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public class boDefClsEventsImpl extends ngtXMLHandler implements boDefClsEvents
{
    private boDefHandlerImpl    p_defhandler;    
    private String              p_name;
    private String              p_attName;
    
    public boDefClsEventsImpl(boDefHandlerImpl bodef,Node x) {
        super(x);
        p_defhandler = bodef;
        refresh();
    }
    public boolean hasBooleanReturn() 
    {
        return p_name.startsWith("onBefore");
    }
    private void refresh() {
        String nodename = super.getNodeName();
        p_name = nodename;
        if( p_name.indexOf(".") != -1 )
        {
            try
            {
                p_attName =p_name.split("\\.")[1];
                //System.out.println("Add event to "+p_attName );
            }
            catch (ArrayIndexOutOfBoundsException e)
            {
                //System.out.println("Nome do evento mal formatado ["+p_name+"]");
            }
        }
        /*if(nodename.equalsIgnoreCase("oncreate"))
            p_name = "onCreate";
        else if(nodename.equalsIgnoreCase("onsave"))
            p_name = "onSave";
        else if(nodename.equalsIgnoreCase("onchangestate"))
            p_name = "onChangeState";
        else 
            p_name = nodename;*/
    }
    public String getEventName() {
        return p_name;
    }
    public boDefXeoCode getEventCode() 
    {
        ngtXMLHandler bodynode = super.getChildNode("body");
        if(bodynode != null) 
        {
            return new boDefXeoCodeImpl( boDefXeoCodeImpl.LANG_JAVA, null, bodynode.getText() );
        }
        return null;
    }


    public String getAttributeName()
    {
        return p_attName;
    }
    
}