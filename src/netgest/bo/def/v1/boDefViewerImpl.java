/*Enconding=UTF-8*/
package netgest.bo.def.v1;
import java.util.Hashtable;

import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.boDefViewerCategory; 

import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public class boDefViewerImpl extends ngtXMLHandler implements boDefViewer
{
    
    private boDefHandler    p_bodef;
    private Node            p_xmlnode;
    private Hashtable       p_categories;
    
  //  public boDefFormPanel[] p_panels;
    public boDefViewerImpl(boDefHandler bodef,Node x) {
        super(x);
        p_xmlnode=x;
        p_bodef = bodef;
    }
    
    public String getViewerName() {
        return p_xmlnode.getNodeName();
    }

    public ngtXMLHandler getForm(String xform) {
        return super.getChildNode("forms").getChildNode(xform);
    }

    public boolean HasForm(String xform) {
        if(super.getChildNode("forms").getChildNode(xform)==null) return false;
        else return true;

    }

    public ngtXMLHandler[] getForms() {
        return super.getChildNode("forms").getChildNodes();
    }
    
    public boDefHandler getBoDefHandler()
    {
        return p_bodef;
    }
    
    public boDefViewerCategory  getCategory( String category ) 
    {
        boDefViewerCategoryImpl ret = null;
        if ( p_categories == null || ( ret = (boDefViewerCategoryImpl)p_categories.get( category ) ) == null )
        {
            String cats[]=category.split("\\.");
            ngtXMLHandler node=super.getChildNode("categories");
    
            for (int i = 0; i < cats.length &&node!=null; i++)  
            {
                if(i>0) node=node.getChildNode("categories");
                if(node!=null) node=node.getChildNode((String)cats[i]);
            }
    
            if ( node != null )
            {
                ret = new boDefViewerCategoryImpl( node.getNode(), this );
            }
            else
            {
                ret = new boDefViewerCategoryImpl( p_bodef.getAttributeRef( category ), this );
            }
        }
        if( ret != null )
        {
            if( p_categories == null ) p_categories = new Hashtable();
            p_categories.put( category, ret );
        }
        return ret;
    }

/*
    public ngtXMLHandler getCategoryLabel(String category) {
        String cats[]=category.split("\\.");
        ngtXMLHandler node=super.getChildNode("categories");
        
        for (int i = 0; i < cats.length &&node!=null; i++)  {
            if(i>0) node=node.getChildNode("categories");
            if(node!=null) node=node.getChildNode((String)cats[i]);
            
        }
        if ( node == null )
        {
            if (p_bodef.hasAttribute(category) )
            {
                //TODO:Ver o que se passa com isto
                return null; //p_bodef.getAttributeRef( category );
            }
        }
        return node;
    }

    public String[] getCategoryAttributes( String category)
    {
        String cats[]=category.split("\\.");
        ngtXMLHandler node=super.getChildNode("categories");
        
        String toRet[] = new String[0];
        for (int i = 0; i < cats.length &&node!=null; i++)  {
            if(i>0) node=node.getChildNode("categories");
            if(node!=null) node=node.getChildNode((String)cats[i]);
            
        }
        if ( node == null )
        {
            if (p_bodef.hasAttribute(category) )
            {
                toRet       = new String[1];
                toRet[0]    = category;  
            }
        }
        else
        {
            ngtXMLHandler child=node.getChildNode("attributes");
            if ( child != null )
            {
                ngtXMLHandler[] childs=child.getChildNodes();
                toRet=new String[ childs.length ];
                for (int i = 0; i <  childs.length ; i++) 
                {
                    toRet[ i ] = childs[i].getNodeName();
                }
                
            }
            
            
        }
        
        return toRet;
        
        
    }
    
    public String getCategoryLabel(ngtXMLHandler categoryNode) {
        String toReturn="";
        String lang=p_bodef.getBoLanguage();
        ngtXMLHandler xx=categoryNode.getChildNode("label").getChildNode(lang);
        if(xx==null) xx=super.getChildNode("label").getChildNode(p_bodef.getBoDefaultLanguage());
        if(xx!=null) toReturn=xx.getText();
        if(toReturn==null){
            toReturn="";
            
        }
        return toReturn;
    }

    public String getCategoryDescription(ngtXMLHandler categoryNode) {
        String toReturn="";
        String lang=p_bodef.getBoLanguage();
        ngtXMLHandler xx=categoryNode.getChildNode("description").getChildNode(lang);
        if(xx==null) xx=super.getChildNode("description").getChildNode(p_bodef.getBoDefaultLanguage());
        if(xx!=null) toReturn=xx.getText();
        if(toReturn==null)toReturn="";
        return toReturn;
    }
   public String getCategoryTooltip(ngtXMLHandler categoryNode) {
        String toReturn="";
        String lang=p_bodef.getBoLanguage();
        ngtXMLHandler xx=categoryNode.getChildNode("tooltip").getChildNode(lang);
        if(xx==null) xx=super.getChildNode("tooltip").getChildNode(p_bodef.getBoDefaultLanguage());
        if(xx!=null) toReturn=xx.getText();
        if(toReturn==null)toReturn="";
        return toReturn;
    }


    public boDefAttribute[] getCategoryAttributes(ngtXMLHandler categoryNode){
        ngtXMLHandler xnode;
        ngtXMLHandler[] xchilds;
        boDefAttribute[] toReturn;
        xnode = categoryNode.getChildNode("attributes");
        if(xnode!=null) {
            xchilds = xnode.getChildNodes();
            toReturn = new boDefAttribute[xchilds.length];
            for(int i=0;i<xchilds.length;i++) {
                toReturn[i] = p_bodef.getAttributeRef(xchilds[i].getNodeName());
            }
        }
        else toReturn=new boDefAttribute[0];

        return toReturn;
        
 
    }
*/    
    public String getObjectViewerClass()
    {
        return super.getAttribute( "viewerClass","netgest.bo.dochtml.viewerImpl.ObjectViewerImpl" );
    }       
}