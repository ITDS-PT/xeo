/*Enconding=UTF-8*/
package netgest.bo.dochtml.viewerImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import netgest.bo.dochtml.docHTML;
import netgest.bo.message.PostInformation;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.*;
import netgest.utils.ClassUtils;
import netgest.utils.XEOUserUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class Ebo_PerfViewer extends ObjectViewerImpl
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     //logger
    private static Logger logger = Logger.getLogger("netgest.bo.dochtml.viewerImpl.Ebo_PerfViewer");
    
    public String getSrcForIcon16() throws boRuntimeException
    {
        String toRet="";
        String dir = ctxObj.getName(); 
        
        if("ESTG".equals(ctxObj.getAttribute("situacao").getValueString()))
        {
            if(XEOUserUtils.isXEOUser(ctxObj))
            {
                toRet= "resources/"+dir+"/ico16_estagiario.gif";
            }
            else
            {
                toRet= "resources/"+dir+"/ico16_estagiario.gif";
            }
        }
        else if("DMTD".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "FLCD".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "RFRM".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "ISP".equals(ctxObj.getAttribute("situacao").getValueString())
        )
        {
            if(XEOUserUtils.isXEOUser(ctxObj))
            {
                toRet= "resources/"+dir+"/ico16_demitido.gif";
            }
            else
            {
                toRet= "resources/"+dir+"/ico16_demitido.gif";
            }
        }
        else
        {
            if(XEOUserUtils.isXEOUser(ctxObj))
            {
                toRet= "resources/"+dir+"/ico16.gif";
            }
            else
            {
                toRet= "resources/"+dir+"/ico16.gif";
            }
        }

        return toRet;
    }

    public StringBuffer getCARDID() throws boRuntimeException
    {
        return getCARDID(true);
    }
    
    public StringBuffer getCARDID(boolean cut) throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        toRet.append("<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='");
        
        toRet.append(getLabel());
        toRet.append("' src='");
        toRet.append(getSrcForIcon16());
        toRet.append("' width='16' height='16'/>");

        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        if( xC.toString().trim().length() < 1 && !ctxObj.exists() )
        {
            xC.setLength(0);
            xC.append("Novo(a) "+ ctxObj.getBoDefinition().getLabel() );
        }
        toRet.append("<span title='");
        toRet.append(xC);
        toRet.append("'>");

        if (cut && xC.length() > 46)
        {
            toRet.append(xC.substring(0, 45) + "...");
        }
        else
        {
            toRet.append(xC);
        }

        toRet.append("</span>");

        return toRet;
    }
    /**
      *    PRESENTATION LAYER OF OBJECT
      *
      *
      *
      */
    public StringBuffer getCARDIDwLink() throws boRuntimeException
    {
        return getCARDIDwLink(false,null);
    }
    
    public StringBuffer getCARDIDwLink(boolean doubleEscape) throws boRuntimeException
    {
        return getCARDIDwLink(false,null);
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape,String extraParameters )
        throws boRuntimeException
    {
        return getCARDIDwLink(false,doubleEscape,extraParameters );
    }
    
    public StringBuffer getCARDIDwLink(boolean newPage, boolean doubleEscape,String extraParameters )
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        if(!newPage)
        {
            if (doubleEscape)
            {
                toRet.append("<span class='lui' onclick=\\\"");
            }
            else
            {
                toRet.append("<span class='lui' onclick=\"");
            }
    
            toRet.append("winmain().openDoc('medium','");
            toRet.append(ctxObj.getName().toLowerCase());
            toRet.append("','edit','"+( extraParameters==null? "":extraParameters+"&" )+"method=edit&boui=");
            toRet.append(ctxObj.bo_boui);
            toRet.append("','");
            toRet.append("");
            toRet.append("','");
            toRet.append("");
            toRet.append("','");
            toRet.append(ctxObj.getName());
    
            if (doubleEscape)
            {
                toRet.append("',window.windowIDX)\\\">");
            }
            else
            {
                toRet.append("',window.windowIDX)\">");
            }
        }

        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");
        toRet.append("Imagem representativa do objecto ");
        
        toRet.append(getLabel());
        toRet.append("' src='");
        toRet.append(getSrcForIcon16());
        toRet.append("' width='16' height='16'/><span ");

        
        StringBuffer xC = new StringBuffer();
        xC.append( ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj) );
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</span></span>");

        return toRet;
    }

    public StringBuffer getURL() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        toRet.append("<a  href='");
        toRet.append(getBaseUrl());
        toRet.append("__viewObject.jsp");
        toRet.append("?method=edit&boui=");
        toRet.append(ctxObj.bo_boui);
        toRet.append("&object=");
        toRet.append(ctxObj.getName());
        toRet.append("'>");

        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' border='0' title='");
        toRet.append("Imagem representativa do objecto ");
        toRet.append(getLabel());
        toRet.append("' src='");
        toRet.append(getSrcForIcon16());
        toRet.append("' width='16' height='16'/><span ");

        StringBuffer xC = new StringBuffer();
        xC.append( ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</a>");

        return toRet;
    }
    public String getICONComposedState() throws boRuntimeException
    {
        String toRet = "";

        if (ctxObj.getStateManager() != null)
        {
            toRet = ctxObj.getStateManager().getStateHTMLICON(ctxObj);
        }
        else
        {
            //toRet="none";
            toRet = "<IMG src='resources/none.gif' height=16 width=16 />";
        }

        return toRet;
    }
    
    public String getLabel() throws boRuntimeException
    {
        String toRet = "";
        if("ESTG".equals(ctxObj.getAttribute("situacao").getValueString()))
        {
            if(XEOUserUtils.isXEOUser(ctxObj))
            {
                toRet= "Estagiário [SGIS]";
            }
            else
            {
                toRet= "Estagiário";
            }
        }
        else if("DMTD".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "FLCD".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "RFRM".equals(ctxObj.getAttribute("situacao").getValueString()) ||
            "ISP".equals(ctxObj.getAttribute("situacao").getValueString())
        )
        {
            toRet= "Ex-Funcionário";
        }
        else
        {
            if(XEOUserUtils.isXEOUser(ctxObj))
            {
                toRet= "Funcionário [SGIS]";
            }
            else
            {
                toRet= "Funcionário";
            }
        }
        return toRet;
    }
}