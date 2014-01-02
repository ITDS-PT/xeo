/*Enconding=UTF-8*/
package netgest.bo.def;

import java.util.Hashtable;

import netgest.bo.boConfig;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
/**
 * <p>Title: boDefWsd </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class boDefWsd extends ngtXMLHandler
{
    public ngtXMLHandler p_bodef;
    private ngtXMLHandler[] p_xmlnodes;
    private static Hashtable p_cachedef = new Hashtable();
    
    public boDefWsd(ngtXMLHandler bodef) {
        super( bodef ); 
        p_bodef     = bodef;
        p_xmlnodes  = super.getChildNodes();                      
    }
    public static boDefWsd loadWsd(String object) 
    {
        boDefWsd ret = null;
        try {
            ret = (boDefWsd)p_cachedef.get(object);
            if(ret == null) {
                boConfig boconf = new boConfig();
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getDeploymentDir()+object+netgest.bo.builder.boBuilder.TYPE_WSD);
                ngtXMLHandler def = new ngtXMLHandler( doc );
                //def.refresh();
                ret = new boDefWsd( def );
                p_cachedef.put(object, ret);                
            }
            return ret;
        } catch (Exception e) {
            return ret;
        }
    }
    public ngtXMLHandler getBoDefHandler() {
        return p_bodef;
    }
    
    public ngtXMLHandler[] getWsds(){
        return p_xmlnodes;
    }

    public ngtXMLHandler[] getChilds() {
        return super.getChildNodes();
    }
}