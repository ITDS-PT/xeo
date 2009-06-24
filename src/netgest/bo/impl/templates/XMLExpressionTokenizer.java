/*Enconding=UTF-8*/
package netgest.bo.impl.templates;
import java.util.*;
import netgest.bo.runtime.boAttributesArray;
import netgest.utils.*;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLNode;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 */
public class XMLExpressionTokenizer extends boAttributesArray 
{
    
    public static final byte VALUE_ATTRIBUTE=0;
    public static final byte VALUE_DATELITERAL=1;
    public static final byte VALUE_NUMBER=2;
    public static final byte VALUE_OPERATOR=3;
    public static final byte VALUE_PARENTESES=4;
    public static final byte VALUE_TEXT=5;
    
    public static final String  XMLATT_ATTRIBUTE     = "atr";
    public static final String  XMLATT_DATELITERAL   = "dateLiteral";
    public static final String  XMLATT_NUMBER        = "number";
    public static final String  XMLATT_OPERATOR      = "oper";
    public static final String  XMLATT_PARENTESES    = "par";
    public static final String  XMLATT_TEXT    = "text";
    
    
    
    public static final LinkedList getTokens(Node exp) 
    {
        // to Debug
        String zexp = ngtXMLUtils.getXML( (XMLDocument) exp.getOwnerDocument() );
        

        NodeList nodes = exp.getChildNodes();
        LinkedList list = new LinkedList(); 
        
        for (short i = 0 ; i < nodes.getLength() ; i++ ) 
        {
            
            processToken((XMLNode)nodes.item(i),list);
    
        }
        return list;
        
    }
    
    private static void processToken(XMLNode tkn,LinkedList tokens) 
    {
        String attname = tkn.getNodeName();
        if ( attname.equalsIgnoreCase( XMLATT_ATTRIBUTE ) )
        {
            tokens.add(new XMLExpressionTokenizer.Token(tkn.getText(),VALUE_ATTRIBUTE));
        }
        else if ( attname.equalsIgnoreCase( XMLATT_DATELITERAL ) )
        {
            tokens.add(new XMLExpressionTokenizer.Token( tkn.getText() , VALUE_DATELITERAL ) );
        }
        else if ( attname.equalsIgnoreCase( XMLATT_NUMBER ) )
        {
            tokens.add(new XMLExpressionTokenizer.Token( tkn.getText() , VALUE_NUMBER ) );
        }
        else if ( attname.equalsIgnoreCase( XMLATT_OPERATOR ) )
        {
            tokens.add( new XMLExpressionTokenizer.Token( tkn.getText() , VALUE_OPERATOR ) );
        }
        else if ( attname.equalsIgnoreCase( XMLATT_PARENTESES ) )
        {
            tokens.add( new XMLExpressionTokenizer.Token( tkn.getText() , VALUE_PARENTESES ) );
        }
        else if ( attname.equalsIgnoreCase( XMLATT_TEXT ) )
        {
            tokens.add( new XMLExpressionTokenizer.Token( tkn.getText() , VALUE_TEXT ) );
        }
    }
    
    
    public static class Token {
        private byte p_type;
        private String p_value;
        Token(String value,byte type) {
            p_type = type;
            p_value=value;
        }
        public byte getType() {
            return p_type;
        }
        public String getString() {
            return p_value;
        }
    }
    
}