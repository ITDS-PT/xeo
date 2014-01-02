/*Enconding=UTF-8*/
package netgest.bo.def;


import java.io.File;

import java.util.Hashtable;

import netgest.bo.boConfig;

import netgest.bo.builder.boBuilder;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;
/**
 * 
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class boDefWds extends ngtXMLHandler
{
    public  ngtXMLHandler p_bodef;
    private ngtXMLHandler[] p_xmlnodes;
    private static Hashtable p_cachedef = new Hashtable();
    
    public boDefWds(ngtXMLHandler bodef) {
        super( bodef ); 
        p_bodef = bodef;
        p_xmlnodes=p_bodef.getChildNodes();                      
    }
    
    public static boDefWds loadWds(String object) 
    {
        boDefWds ret = null;
        try {
            ret = (boDefWds)p_cachedef.get(object);
            if(ret == null) {
                boConfig boconf = new boConfig();
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getWordTemplateConfig().getProperty("path")+ File.separator + object + boBuilder.TYPE_WSD);
                ngtXMLHandler def = new ngtXMLHandler( doc );
                //def.refresh();
                ret = new boDefWds(def);
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
    public boolean isInTemplateDefinition(String bridgeName)
    {      
        boolean found = false;
        String bn = null;
        ngtXMLHandler dsNode = this.getChildNode("bridges");        
        ngtXMLHandler[] bridges = dsNode.getChildNodes();      
        for (int i = 0; i < bridges.length && !found ; i++) 
        { 
            bn = bridges[i].getNodeName();
            if(bridgeName.equals(bn)) found = true;
        }
        return found;
    }
}