/*Enconding=UTF-8*/
package netgest.bo.def;
import java.util.Hashtable;
import netgest.bo.def.v1.boDefViewerCategoryImpl;
import netgest.utils.ngtXMLHandler;

import org.w3c.dom.Node;

public interface boDefViewer 
{
    public String getViewerName();

    public ngtXMLHandler getForm(String xform);

    public boolean HasForm(String xform);

    public ngtXMLHandler[] getForms();
    
    public boDefHandler getBoDefHandler();
    
    public boDefViewerCategory  getCategory( String category );
    
    public String getObjectViewerClass();
    
    public ngtXMLHandler getChildNode( String nodeName );
    
}