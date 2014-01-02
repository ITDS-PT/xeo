package netgest.bo.def;
import java.util.ArrayList;
import oracle.xml.parser.v2.XMLCDATA;
import oracle.xml.parser.v2.XMLElement;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class boDefUtils 
{
    public boDefUtils()
    {
    }
    
    public static final Node createAttribute(
                                String      attname, 
                                String      dbname, 
                                String      description, 
                                String      attType,
                                String      xtype,
                                int         len,
                                boolean     tabled, 
                                Document    doc    
                            )
    {
        
        Element attNode = doc.createElement( attType );
        attNode.setAttribute("name",attname);           
        if( 
            boDefAttribute.ATTRIBUTE_OBJECT.equals( attType )
            ||
            boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION.equals( attType )
        )
        {
            createTextNode(attNode,"type", xtype );
            createTextNode(attNode,"setParent", "false" );
        }
        

        if( len != 0 ) 
            createTextNode( attNode, "len", String.valueOf( len ) );
        
        createTextNode( attNode, "label", description );
        if( tabled || !attname.equalsIgnoreCase( dbname ) )
        {
            Element dataBaseNode = doc.createElement( "database" );
            attNode.appendChild( dataBaseNode );
            createTextNode(dataBaseNode,"fieldname", s(dbname) );
            createTextNode(dataBaseNode,"unique", "" );
            createTextNode(dataBaseNode,"indexfull", "" );
            createTextNode(dataBaseNode,"indexed", "" );
            createTextNode(dataBaseNode,"tabled", String.valueOf( tabled ) );
            createTextNode(dataBaseNode,"constraint", "" );
            createTextNode(dataBaseNode,"binding", "" );
        }
        
        if( "attributeNumber".equals( attType ) ) {
            createTextNode( attNode, "grouping", "false" );
            createTextNode( attNode, "decimals", "0" );
        }
        
        return attNode;
    }
    
    
    public static final Element createMethod(
                String methodName, 
                String returnType, 
                String builtFrom, 
                String toObjectName,
                boolean openDoc, 
                boolean menu, 
                boolean toolbar, 
                boolean requiredTransaction, 
                String label, 
                String enLabel, 
                boolean serverOnly, 
                String body, 
                String hiddenWhenCode, 
                ArrayList paramType, 
                ArrayList paramName,
                Document xml
        )
    {
        Element meth = xml.createElement("method");
        meth.setAttribute( "name", methodName );
        meth.setAttribute( "openDoc", String.valueOf( openDoc ) );
        meth.setAttribute( "menu", String.valueOf( menu ) );
        meth.setAttribute( "public", String.valueOf( "false" ) );
        meth.setAttribute( "requiredTransaction", String.valueOf( requiredTransaction ) );
        meth.setAttribute( "serverOnly", String.valueOf( serverOnly ) );
        if( toObjectName != null && toObjectName.length() > 0 )
            meth.setAttribute( "toObject", String.valueOf( toObjectName ) );
            
        meth.setAttribute( "toolbar", String.valueOf( toolbar ) );
        
        if(builtFrom != null && builtFrom.length() > 0)
        {
            meth.setAttribute("builtFrom", builtFrom);
        }
        
        Element assinature = xml.createElement("assinature");
        assinature.setAttribute("return", returnType);
        meth.appendChild(assinature);
        
        if(paramType != null)
        {
            Element param;
            for (int i = 0; i < paramType.size(); i++) 
            {
                param = xml.createElement((String)paramName.get(i));
                param.setAttribute("type", (String)paramType.get(i));
                assinature.appendChild(param);
            }
        }
                
        Element _body = xml.createElement("body");
        meth.appendChild(_body);
        if( body != null )
        {
            _body.appendChild( xml.createCDATASection( body ) );
        }
        else
        {
            _body.appendChild( xml.createCDATASection( "return;" ) );
        }
        
        if(hiddenWhenCode != null && hiddenWhenCode.length() > 0)
        {
            Element _hidden = xml.createElement("hiddenWhen");
            _hidden.appendChild( xml.createCDATASection( hiddenWhenCode ) );
            meth.appendChild(_hidden);
        }
        return meth;
    }
    
    private static final String EMPTY_STRING="";
    
    private static final String s( String value )
    {
        if( value == null || value.trim().length() == 0 ) return EMPTY_STRING;
        return value;
    }
    
    public static final Element createTextNode( Element node, String nodeName, String value )
    {
        return createTextNode(node, nodeName, value,null);
    }

    public static final Element createTextNode( Element node, String nodeName, String value,
    		String[][]attributes)
    {
        Element elem = node.getOwnerDocument().createElement( nodeName );
        if (attributes!=null)
        {
        	NamedNodeMap atts = elem.getAttributes();
        	String attname="";
        	String attvalue="";
    		for (int i=0;i<attributes.length;i++)
    		{
    			attname=attributes[i][0];
    			attvalue=attributes[i][1];
    			Attr attaux = elem.getOwnerDocument().createAttribute(attname);
    			attaux.setNodeValue(attvalue);
    			atts.setNamedItem(attaux);
    		}
        }
        node.appendChild( elem );
        elem.appendChild( node.getOwnerDocument().createTextNode( value ) );
        return elem;
    }

    
}