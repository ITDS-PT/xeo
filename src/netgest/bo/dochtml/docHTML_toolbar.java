/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import oracle.xml.parser.v2.*;
import org.w3c.dom.*;
import netgest.utils.*;

public class docHTML_toolbar  {
    XMLDocument p_SourceXML;
    Element p_NodeParent;
    Element p_toolbar;
    Element p_toolbarBody;
    
    public docHTML_toolbar(XMLDocument xmlSource,Element xParent) {
        p_SourceXML=xmlSource;
        p_NodeParent=xParent;
    
    }
    
    public Element createToolBar(String ClassName,String Id){
        Element x;
        Element xi;
        p_toolbar=p_SourceXML.createElement("TABLE");
        p_toolbar.setAttribute("class",ClassName);
        p_toolbar.setAttribute("ID",Id);
        p_toolbar.setAttribute("cellSpacing","0");
        p_toolbar.setAttribute("cellPadding","0");
        
        p_NodeParent.appendChild(p_toolbar);
        p_toolbarBody=p_SourceXML.createElement("TBODY");
        p_toolbar.appendChild(p_toolbarBody);
        x=p_SourceXML.createElement("TR");
        p_toolbarBody.appendChild(x);
        p_toolbarBody=x;
        x=p_SourceXML.createElement("TD");
        x.setAttribute("width","9");
        xi=p_SourceXML.createElement("IMG");
        xi.setAttribute("hspace","3");
        xi.setAttribute("src","templates/menu/std/mnu_vSpacer.gif");
        xi.setAttribute("width","5");
        xi.setAttribute("height","18");
        x.appendChild(xi);
        p_toolbarBody.appendChild(x);
        x=p_SourceXML.createElement("TD");
        p_toolbarBody.appendChild(x);
        p_toolbarBody=x;
        return p_toolbarBody;        
        
/*
        <table class="mnubarFlat" id="mnuBar1" cellSpacing="0" cellPadding="0">
        <tbody>
        <tr>
          <td width="9"><img hspace="3" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18"></td>
          <td>
             .......
          </td>
        </tr>
          */
    }
    
}