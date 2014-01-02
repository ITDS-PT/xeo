/*Enconding=UTF-8*/
package netgest.bo.def.v2;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.v2.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class boDefDatabaseObjectImpl extends ngtXMLHandler implements boDefDatabaseObject
{
    private boDefHandlerImpl    p_bodef;
    private byte                p_type;
    private String              p_id;
    private String              p_label;
    private String              p_expression;
    
    public boDefDatabaseObjectImpl(boDefHandlerImpl bodef,Element x) 
    {
        super(x);
        this.p_bodef = bodef;
        
        String strType = x.getAttribute( "type" );
        if(strType.equalsIgnoreCase("primary")) {
            p_type = DBOBJECT_PRIMARY;
        }
        else if(strType.equalsIgnoreCase("index")) {
            p_type = DBOBJECT_INDEX;
        } else if (strType.equalsIgnoreCase("unique")) {
            p_type = DBOBJECT_UNIQUEKEY;
        }
        p_bodef = bodef;
        
        p_id         = super.getAttribute("id","");
        p_label      = super.getChildNodeText("label","");
        p_expression = super.getChildNodeText("expression","");
    }
    
    public String getExpression() 
    {
        return p_expression;
    }
    
    public String getId() 
    {
        return p_id;
    }
    
    public String getLabel() 
    {
        return p_label;
    }
    
    public byte getType() 
    {
        return p_type;        
    }
}