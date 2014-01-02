/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import oracle.xml.parser.v2.*;
import org.w3c.dom.*;
import netgest.utils.*;

public class docHTML_toolBarContainer  {
    private XMLDocument p_HtmlSource;
    private Element p_toolbarCont;
    private Element p_toolbarContBody;
    private Element p_toolbarContBodyTD;
    private int p_height=0;
    private char[] p_htmlChar;
    
    public docHTML_toolBarContainer() {
        p_HtmlSource=new XMLDocument();

        p_toolbarCont = p_HtmlSource.createElement("TABLE");
        p_toolbarCont.setAttribute("class","layout");
        p_toolbarCont.setAttribute("cellspacing","0");
        p_toolbarCont.setAttribute("cellPadding","0");
        p_HtmlSource.appendChild(p_toolbarCont);
        p_toolbarContBody   =   p_HtmlSource.createElement("TBODY");
        p_toolbarCont.appendChild(p_toolbarContBody);
        p_toolbarContBody   =   p_HtmlSource.createElement("TR");
        p_toolbarContBodyTD =   p_HtmlSource.createElement("TD");
        
        p_toolbarContBody.appendChild(p_toolbarContBodyTD);
        p_toolbarCont.appendChild(p_toolbarContBody);
        
    
    }

    public Element addToolBar(String className,String id){
        docHTML_toolbar xtoolBar;
        p_height+=24;
        p_toolbarContBody.setAttribute("height",""+p_height );
        xtoolBar=new docHTML_toolbar(p_HtmlSource,p_toolbarContBodyTD);
        
        //retorna a toolbar body
        return xtoolBar.createToolBar(className,id);
    }

    
    public char[] getHtmlSource(){
        //p_HtmlSource.
        
        p_htmlChar=ngtXMLUtils.getXML(p_HtmlSource).toCharArray();
         
        return p_htmlChar;
    }
    
    
}