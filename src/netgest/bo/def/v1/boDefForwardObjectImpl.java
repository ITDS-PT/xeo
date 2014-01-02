/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import java.util.HashMap;

import netgest.bo.def.boDefForwardObject;
import netgest.bo.def.v1.boDefHandlerImpl;

import netgest.utils.ngtXMLHandler;

import netgest.bo.system.Logger;

import org.w3c.dom.Node;
/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class boDefForwardObjectImpl extends ngtXMLHandler implements boDefForwardObject
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.def.boDefForwardObject");
    
    private static final String ATTR_ORIGIN = "attr_origin";
    private static final String ATTR_DESTINY = "attr_destiny";
    private static final String AFTER_MAP_CLASS = "afterMapClass";
    private static final String BEFORE_MAP_CLASS = "beforeMapClass";
    private static final String FWD_OBJECT = "fwdObject";
    private static final String PREFIX_MAP_METHOD = "forwardTo";
    private static final String PREFIX_ONSAVE_FWD_METHOD = "onSaveFwdObjectTo";
    private static final String PREFIX_BEFORE_MAP_METHOD = "beforeMapTo";
    private static final String PREFIX_AFTER_MAP_METHOD = "afterMapTo";

    private boDefHandlerImpl p_defhandler;
    private HashMap p_maps;
    private String p_toName;
    private String p_methodName;
    private String p_beforeMapClass;
    private String p_afterMapClass;
    private String p_onSaveFwdObject;
    private String p_openDoc;
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public boDefForwardObjectImpl(boDefHandlerImpl bodef,Node x)
    {
        super(x);        
        p_defhandler = bodef;
        parse();
    }
    private void parse() {
        p_toName = super.getAttribute("name");
        p_openDoc = super.getAttribute("openDoc");
        
        ngtXMLHandler node = super.getChildNode("maps");
        if(node != null) 
        {
            ngtXMLHandler[] maps = node.getChildNodes();
            if(maps != null) 
            {
                int mapsLength = maps.length;
                String orgAttr, dstAttr;
                for (byte i = 0; i < maps.length; i++) 
                {
                    orgAttr = maps[i].getAttribute(ATTR_ORIGIN);
                    dstAttr = maps[i].getAttribute(ATTR_DESTINY);
                    if(p_maps == null)
                    {
                        p_maps = new HashMap();
                    }
                    p_maps.put(orgAttr, dstAttr);
                }
            }
        }
        node = super.getChildNode("afterMapClass");
        if(node != null) 
        {
            p_afterMapClass = node.getText();
        }
        node = super.getChildNode("beforeMapClass");
        if(node != null) 
        {
            p_beforeMapClass = node.getText();
        }
        
        node = super.getChildNode("onSaveFwdObject");
        if(node != null) 
        {
            p_onSaveFwdObject = node.getText();
        }
    }
    
    public HashMap getMaps()
    {
        return p_maps;
    }
    
    public String getAfterMapClass()
    {
        return p_afterMapClass;
    }
    public String getBeforeMapClass()
    {
        return p_beforeMapClass;
    }
    public String getOnSaveFwdObject()
    {
        return p_onSaveFwdObject;
    }
    public String toBoObject()
    {
        return p_toName;
    }
    public boolean openDoc()
    {
        if(p_openDoc != null)
        {
            return "true".equalsIgnoreCase(p_openDoc) || "y".equalsIgnoreCase(p_openDoc);
        }
        return false;
    }
    
    public String getMapMethodName()
    {
        return getMapMethodName(this);
    }
    public String getAfterMapMethodName()
    {
        return getAfterMapMethodName(this);
    }
    public String getBeforeMapMethodName()
    {
        return getBeforeMapMethodName(this);
    }
    public String getOnSaveFwdObjectMethodName()
    {
        return getOnSaveFwdObjectMethodName(this);
    }
    
    public static final String getMapMethodName(boDefForwardObjectImpl fwdDef)
    {
        return PREFIX_MAP_METHOD + fwdDef.toBoObject();
    }
    
    public static final String getAfterMapMethodName(boDefForwardObjectImpl fwdDef)
    {
        return PREFIX_AFTER_MAP_METHOD + fwdDef.toBoObject();
    }
    
    public static final String getBeforeMapMethodName(boDefForwardObjectImpl fwdDef)
    {
        return PREFIX_BEFORE_MAP_METHOD + fwdDef.toBoObject();
    }
    
    public static final String getOnSaveFwdObjectMethodName(boDefForwardObjectImpl fwdDef)
    {
        return PREFIX_ONSAVE_FWD_METHOD + fwdDef.toBoObject();
    }
    
    public String getLabel()
    {
        String ret = null;
        ngtXMLHandler labnode = super.getChildNode("label");

        if ((labnode.getChildNode(p_defhandler.getBoDefaultLanguage())) != null)
        {
            ret = (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())).getText();
        }
        else if (labnode.getChildNode(p_defhandler.getBoDefaultLanguage()) != null)
        {
            ret = (labnode.getChildNode(p_defhandler.getBoDefaultLanguage())).getText();
        }

        return ret;
    }
}