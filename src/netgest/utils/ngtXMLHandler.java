/*Enconding=UTF-8*/
package netgest.utils;
import oracle.xml.parser.v2.*;
import netgest.utils.*;
import netgest.exceptions.*;
import netgest.bo.builder.*;
import org.w3c.dom.*;
import java.util.*;
import netgest.bo.boException;

public class ngtXMLHandler  {
    XMLDocument p_xmldoc;
    Node p_node;
    public ngtXMLHandler(String xml) {
//        try {
            p_node = ngtXMLUtils.loadXML(xml);
//            String sInitial = new String( xml.getBytes("UTF-8"), "UTF-8");
//            p_node = ngtXMLUtils.loadXML(xml.getBytes("UTF-8"));
            p_xmldoc = (XMLDocument)p_node;
//        } catch (Exception e) {
//            String[] emsg = {"<xml>"};
//            throw new boException(this.getClass().getName()+":init(String xml)","BO-1201",e,emsg);
//        }
    }

    public ngtXMLHandler(Node node) {
        if(node !=null) {
            if(node.getNodeType()==Node.DOCUMENT_NODE) p_xmldoc = (XMLDocument)node;
            else p_xmldoc = (XMLDocument)node.getOwnerDocument();
        }
        p_node = node;
    }

    public ngtXMLHandler(ngtXMLHandler ngt) {
        if(ngt !=null) {
        Node node = ngt.getNode();
        if(node.getNodeType()==Node.DOCUMENT_NODE) p_xmldoc = (XMLDocument)node;
        else p_xmldoc = (XMLDocument)node.getOwnerDocument();
        p_node = node;
        }
    }
    
    public String getNodeName() {
        return p_node.getNodeName();
    }
    public String getAttribute(String attname,String defaultvalue) {
        String x = getAttribute(attname);
        if(x==null||x.length()==0)
            x = defaultvalue;
        return x;        
    }
    public String getAttribute(String attname) {
        NamedNodeMap att;
        if((att=p_node.getAttributes())!=null) {
            Node x = att.getNamedItem(attname);
            return x==null?null:x.getNodeValue();
        }
        return null;
    }

    public Attr[] getAttributes()
    {
        NamedNodeMap xattr=p_node.getAttributes();
        Attr[] toReturn=new Attr[xattr.getLength()];
        for (int i = 0; i < xattr.getLength(); i++) toReturn[i]=(Attr)xattr.item(i);
        return toReturn;
    }
    public String getText()
    {
        return getText( p_node );
    }

    private static final String getText( Node node )
    {
        short ntype;
        Node x = node.getFirstChild();
        if( x != null )
        {
            do
            {
                ntype = x.getNodeType();
                if( ntype == Node.TEXT_NODE || ntype == Node.CDATA_SECTION_NODE )
                    return x.getNodeValue();
            }
            while( (x=x.getNextSibling())!=null );
        }
        return null;
    }

    public String getCDataText()
    {
        return getText();
//        NodeList x = p_node.getChildNodes();
//        int i;
//        for(i=0;x!=null && i<x.getLength();i++) {
//            if(x.item(i).getNodeType()==Node.CDATA_SECTION_NODE)
//                return x.item(i).getNodeValue();
//        }
//        return null;
    }
    
    public ngtXMLHandler[] getChildNodes() {
        Vector ret = new Vector();
        NodeList x = p_node.getChildNodes();
        int i;
        for(i=0;x!=null && i<x.getLength();i++) {
            if(x.item(i).getNodeType()==Node.ELEMENT_NODE)
                ret.add(new ngtXMLHandler(x.item(i)));
        }
        return (ngtXMLHandler[])ret.toArray(new ngtXMLHandler[0]);
    }
    public String getChildNodeText(String nodename,String xdefault) 
    {
        String ret = null;
        Node x = _getChildNode( nodename );
        
        if( x != null )
        {
            ret = getText( x );
        }
        return ret==null||ret.length()==0?xdefault:ret;            
            
    }

    public ngtXMLHandler getChildNode(String nodename) 
    {
        Node ret = _getChildNode( nodename );
        if( ret != null )
        {
            return new ngtXMLHandler( ret );
        }
        return null;
    }

    private Node _getChildNode(String nodename) 
    {
        Node x = p_node.getFirstChild();
        if( x != null )
        {
            do
            {
                if( x.getNodeName().equalsIgnoreCase( nodename ) )
                {
                    return x;
                }
            } while( (x=x.getNextSibling())!= null );
        }
        return x;
    }
    public ngtXMLHandler getParentNode() {
        return new ngtXMLHandler(p_node.getParentNode());
    }
    public ngtXMLHandler getFirstChild() {
        Node x = p_node.getFirstChild();
        while(x!=null && x.getNodeType()!=Node.ELEMENT_NODE)
            x = x.getNextSibling();
        if(x!=null)
            return new ngtXMLHandler(x);
        return null;    
    }
    public ngtXMLHandler next() {
    
        //return new ngtXMLHandler(p_node.getNextSibling());
        Node x = p_node.getNextSibling();
        while(x!=null && x.getNodeType()!=Node.ELEMENT_NODE)
            x = x.getNextSibling();
        if(x!=null)
            return new ngtXMLHandler(x);
        return null;
        
    }
    public ngtXMLHandler previous() {
      //  return new ngtXMLHandler(p_node.getPreviousSibling());
         Node x = p_node.getPreviousSibling();
        while(x!=null && x.getNodeType()!=Node.ELEMENT_NODE)
            x = x.getPreviousSibling();
        if(x!=null)
            return new ngtXMLHandler(x);
        return null;
    }

    public void goDocumentElement() {
        p_node = p_xmldoc.getDocumentElement();
    }
    public void goChildNode(String nodename) {
        NodeList x = p_node.getChildNodes();
        int i;
        for(i=0;x!=null && i<x.getLength();i++) {
            if(x.item(i).getNodeName().equalsIgnoreCase(nodename))
                p_node=x.item(i);
        }
    }
    public void goParentNode() {
        p_node=p_node.getParentNode();
    }
    public void goFirstChild() {
        p_node=p_node.getFirstChild();
    }
    public void goNext() {
        p_node=p_node.getNextSibling();
    }
    public void goPrevious() {
        p_node=p_node.getPreviousSibling();
    }
    public Node getNode() {
        return p_node;
    }
    public XMLDocument getDocument() {
        return p_xmldoc;
    }

}