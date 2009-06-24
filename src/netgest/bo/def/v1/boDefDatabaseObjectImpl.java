/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import netgest.bo.def.boDefDatabaseObject;
import netgest.bo.def.v1.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public class boDefDatabaseObjectImpl extends ngtXMLHandler implements boDefDatabaseObject
{
    private boDefHandlerImpl p_bodef;
    private byte p_type;
    public boDefDatabaseObjectImpl(boDefHandlerImpl bodef,Node x) {
        super(x);
        String nn =x.getNodeName();
        if( x.getAttributes().getNamedItem("type") != null )
        	nn = x.getAttributes().getNamedItem("type").getNodeValue();
        
        if(nn.equalsIgnoreCase("primary")) {
            p_type = boDefDatabaseObject.DBOBJECT_PRIMARY;
        } else if(nn.equalsIgnoreCase("index")) {
            p_type = boDefDatabaseObject.DBOBJECT_INDEX;
        } else if (nn.equalsIgnoreCase("unique")) {
            p_type = boDefDatabaseObject.DBOBJECT_UNIQUEKEY;
        }
        p_bodef = bodef;
        parse();
    }
    private void parse() {
        
    }
    public String getExpression() {
        return super.getChildNodeText("expression","");
    }
    public String getId() {
        return super.getAttribute("id","");
    }
    public String getLabel() {
        String ret=null;
        ngtXMLHandler labnode = super.getChildNode("label");
        if( labnode != null )
        {
            if((labnode.getChildNode(p_bodef.getBoLanguage()))!=null)
            {
                ret = (labnode.getChildNode(p_bodef.getBoLanguage())).getText();
            }
            else if (labnode.getChildNode(p_bodef.getBoDefaultLanguage())!=null)
            {
                ret = (labnode.getChildNode(p_bodef.getBoDefaultLanguage())).getText();
            }
        }
        else
        {
            ret = "";
        }
        return ret;      
    }
    public byte getType() {
        return p_type;        
    }
}