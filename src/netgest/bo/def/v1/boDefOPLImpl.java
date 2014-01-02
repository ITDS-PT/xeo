/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import java.util.Arrays;
import java.util.Vector;
import netgest.bo.def.boDefOPL;
import netgest.bo.def.v1.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public final class boDefOPLImpl extends ngtXMLHandler implements boDefOPL
{
        
    private String[] p_attributesReadKey;    
    private String[] p_attributesWriteKey;    
    private String[] p_attributesDeleteKey;    
    private String[] p_attributesFullControlKey;    
    private String[] p_methodsExecuteKey;    
    private String[] p_eventsExecuteKey;
    
    private boDefHandlerImpl p_defhandler;
    
    private String[]   p_classReadKeys = null;
    private String[]   p_classWriteKeys = null;
    private String[]   p_classDeleteKeys = null;
    private String[]   p_classFullControlKeys = null;
    
    public boDefOPLImpl(boDefHandlerImpl bodef,Node x)  
    {
        super(x);        
        p_defhandler = bodef;
        this.parseO();
    }
    private void parseO()
    {
        ngtXMLHandler xnodeAtrKey = null;   
        ngtXMLHandler[] xAttributes = null;
        
        ngtXMLHandler node = super.getChildNode("read");
        if(node != null) 
        {
            xnodeAtrKey=node.getChildNode("attributesKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_attributesReadKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_attributesReadKey[ i ] = xAttributes[i].getNodeName();
            }
            
            xnodeAtrKey = node.getChildNode("classKeys");
            if(xnodeAtrKey != null)
            {
                xAttributes= xnodeAtrKey.getChildNodes();
                if(xAttributes != null)
                {                
                    p_classReadKeys = new String[ xAttributes.length];
                    for (int i = 0; i < xAttributes.length  ; i++)            
                    {
                        p_classReadKeys[i] = xAttributes[i].getNodeName();                
                    }
                }
            }
        }   
        
        node = super.getChildNode("write");
        if(node != null) 
        {
            xnodeAtrKey=node.getChildNode("attributesKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_attributesWriteKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_attributesWriteKey[ i ] = xAttributes[i].getNodeName();
            }
            
            xnodeAtrKey = node.getChildNode("classKeys");
            if(xnodeAtrKey != null)
            {            
                xAttributes= xnodeAtrKey.getChildNodes();
                if(xAttributes != null)
                {                
                    p_classWriteKeys = new String[ xAttributes.length];
                    for (int i = 0; i < xAttributes.length  ; i++)             
                    {
                        p_classWriteKeys[i] = xAttributes[i].getNodeName();                
                    }
                }
            }
        }
        
        node = super.getChildNode("delete");
        if(node != null) 
        {
            xnodeAtrKey=node.getChildNode("attributesKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_attributesDeleteKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_attributesDeleteKey[ i ] = xAttributes[i].getNodeName();
            }
            
            xnodeAtrKey = node.getChildNode("classKeys");
            if(xnodeAtrKey != null)
            {            
                xAttributes= xnodeAtrKey.getChildNodes();
                if(xAttributes != null)
                {                
                    p_classDeleteKeys = new String[ xAttributes.length];
                    for (int i = 0; i < xAttributes.length  ; i++)             
                    {
                        p_classDeleteKeys[i] = xAttributes[i].getNodeName();                
                    }
                }
            }
        }
        node = super.getChildNode("fullControl");
        if(node != null) 
        {
            xnodeAtrKey=node.getChildNode("attributesKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_attributesFullControlKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_attributesFullControlKey[ i ] = xAttributes[i].getNodeName();
            }
            
            xnodeAtrKey = node.getChildNode("classKeys");
            if(xnodeAtrKey != null)
            {            
                xAttributes= xnodeAtrKey.getChildNodes();
                if(xAttributes != null)
                {
                    p_classFullControlKeys = new String[ xAttributes.length];
                    for (int i = 0; i < xAttributes.length  ; i++)
                    {
                        p_classFullControlKeys[i] = xAttributes[i].getNodeName();                
                    }
                }
            }
        }
        node = super.getChildNode("execute");
        if(node != null) 
        {
            xnodeAtrKey=node.getChildNode("methodsKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_methodsExecuteKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_methodsExecuteKey[ i ] = xAttributes[i].getNodeName();
            }
            xnodeAtrKey=node.getChildNode("eventsKey");
            xAttributes= xnodeAtrKey.getChildNodes();
            p_eventsExecuteKey = new String[ xAttributes.length];
            for (int i = 0; i < xAttributes.length  ; i++) 
            {
                p_eventsExecuteKey[ i ] = xAttributes[i].getNodeName();
            }
            
        }                
    }
    
    public String[] getReadKeyAttributes()
    {
        return p_attributesReadKey;
    }
    
    public String[] getWriteKeyAttributes()
    {
        return p_attributesWriteKey;
    }
    public String[] getDeleteKeyAttributes()
    {
        return p_attributesDeleteKey;
    }
    
    public String[] getFullControlKeyAttributes()
    {
        return p_attributesFullControlKey;
    }

    public String[] getMethodsExecuteKeys()
    {
        return this.p_methodsExecuteKey;
    }

    public String[] getEventsExecuteKeys()
    {
        return this.p_eventsExecuteKey;
    }
    
    public String[] getClassKeys()
    {
        Vector keys = new Vector();
        if( p_classDeleteKeys != null ) keys.addAll( Arrays.asList( p_classDeleteKeys ) );
        if( p_classReadKeys != null ) keys.addAll( Arrays.asList( p_classReadKeys ) );
        if( p_classWriteKeys != null ) keys.addAll( Arrays.asList( p_classWriteKeys ) );
        if( p_classFullControlKeys != null ) keys.addAll( Arrays.asList( p_classFullControlKeys ) );
        return (String[])keys.toArray(new String[ keys.size() ]);
    }
 /*   public String[] getClassForReadKeys()
    {
        return p_classReadKeys;
    }

    public String[] getClassForWriteKeys()
    {
        return p_classWriteKeys;
    }
    
    public String[] getClassForDeleteKeys()
    {
        return p_classDeleteKeys;
    }
    
    public String[] getClassForFullControlKeys()
    {
        return p_classFullControlKeys;
    }    
*/    
}