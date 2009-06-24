/*Enconding=UTF-8*/
package netgest.bo.def.v2;
import java.util.Hashtable;

import netgest.bo.boConfig;
import netgest.bo.builder.boBuilder;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefLov;

import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import oracle.xml.parser.v2.XMLDocument;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class boDefLovImpl extends ngtXMLHandler implements boDefLov
{
    public boDefHandlerImpl p_bodef;
    private ngtXMLHandler[] p_xmlnodes;
    private static Hashtable p_cachedef = new Hashtable();
    
  //  public boDefFormPanel[] p_panels;
    public boDefLovImpl(boDefHandlerImpl bodef) 
    {
        super( bodef.getXmlNode() ); 
        p_bodef = bodef;
        p_xmlnodes= bodef.getXmlNode().getChildNodes();                      
    }
    public static boDefLov loadLov(String lovname) 
    {
        try {
            boDefLov ret = (boDefLov)p_cachedef.get(lovname);
            if(ret == null) 
            {
                boConfig boconf = new boConfig();
                XMLDocument doc = ngtXMLUtils.loadXMLFile(boconf.getDeploymentDir()+lovname+boBuilder.TYPE_LOV);
                boDefHandlerImpl def = new boDefHandlerImpl(doc,false,false);
                //def.refresh();
                ret = new boDefLovImpl(def);
                p_cachedef.put(lovname, ret);                
            }
            return ret;
        } 
        catch (Exception e) 
        {
            throw new RuntimeException("Error reading XML of ["+lovname+"]\n"+e.getClass().getName()+"\n"+e.getMessage());
        }
    }
    public boDefHandler getBoDefHandler() {
        return p_bodef;
    }
    
    public ngtXMLHandler[] getLovs(){
        return p_xmlnodes;
    }

    public ngtXMLHandler[] getChilds() {
        return super.getChildNodes();
    }
    
    public static void clearCache() {
        p_cachedef = new Hashtable();
    }
   
}