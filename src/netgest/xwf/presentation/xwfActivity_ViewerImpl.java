/*Enconding=UTF-8*/
package netgest.xwf.presentation;

import netgest.bo.def.*;

import netgest.bo.dochtml.viewerImpl.ObjectViewer;
import netgest.bo.dochtml.docHTML;

import netgest.bo.message.PostInformation;

import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;

import netgest.utils.*;
import netgest.bo.system.Logger;

import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import org.w3c.dom.Element;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class xwfActivity_ViewerImpl implements ObjectViewer
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    public boObject ctxObj;

    //logger
    public void setContextObject(boObject objTarget)
    {
        ctxObj = objTarget;
    }

    public boObject getContextObject()
    {
        return ctxObj;
    }

    public StringBuffer getCARDIDwNoIMG()
        throws boRuntimeException
    {
        return getCARDIDwNoIMG(true);
    }
    public StringBuffer getCARDIDwNoIMG(boolean cut)
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        StringBuffer xC    = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));

        if ((xC.toString().trim().length() < 1) && !ctxObj.exists())
        {
            xC.setLength(0);
            xC.append(Messages.getString("xwfActivity_ViewerImpl.0") + ctxObj.getBoDefinition().getLabel()); //$NON-NLS-1$
        }

        if (xC.length() > 46 && cut)
        {
            toRet.append(xC.substring(0, 45) + "..."); //$NON-NLS-1$
        }
        else
        {
            toRet.append(xC);
        }

        //        }
        return toRet;
    }

    public String getSrcForIcon16()
        throws boRuntimeException
    {
        String toRet = ""; //$NON-NLS-1$
        //toRet ="ieThemes/0/tasks"+ctxObj.getName()+(ctxObj.userReadThis()?"":"_unread");
        toRet ="resources/"+ctxObj.getName()+"/ico16.gif"; //$NON-NLS-1$ //$NON-NLS-2$
        return toRet;
    }

    public StringBuffer getCARDID()
        throws boRuntimeException
    {
        return getCARDID(true);
    }

    public StringBuffer getCARDID(boolean cut)
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        toRet.append("<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='"); //$NON-NLS-1$

        if (ctxObj.getName().equals("Ebo_Template")) //$NON-NLS-1$
        {
            //toRet.append("Objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(
                    ctxObj.getEboContext(), "Ebo_ClsReg", //$NON-NLS-1$
                    ctxObj.getAttribute("masterObjectClass").getValueLong() //$NON-NLS-1$
                );
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.9") + o.getAttribute("description").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                o.getAttribute("name").getValueString() + "/ico16tmpl.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.15")); //$NON-NLS-1$
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg")) //$NON-NLS-1$
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.237")); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                ctxObj.getAttribute("name").getValueString() + "/ico16.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else if (ctxObj.getName().equals("runtimeAddress")) //$NON-NLS-1$
        {
            boObject oref = ctxObj.getAttribute("refObj").getObject(); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());

            if (oref != null)
            {
                toRet.append(
                    "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + oref.getName() + //$NON-NLS-1$ //$NON-NLS-2$
                    "/ico16.gif" //$NON-NLS-1$
                );
            }
            else
            {
                toRet.append(
                    "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                    ctxObj.getName() + "/ico16.gif" //$NON-NLS-1$
                );
            }

            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else
        {
            // toRet.append("Objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + //$NON-NLS-1$ //$NON-NLS-2$
                "/ico16.gif" //$NON-NLS-1$
            );
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.35")); //$NON-NLS-1$
        }

        //        if ( !ctxObj.exists() )
        //        {   
        //            toRet.append("<span>");
        //            toRet.append(ctxObj.getBoDefinition().getLabel());
        //            toRet.append("</span>");
        //        }
        //        else
        //        {
        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));

        if ((xC.toString().trim().length() < 1) && !ctxObj.exists())
        {
            xC.setLength(0);
            xC.append(Messages.getString("xwfActivity_ViewerImpl.36") + ctxObj.getBoDefinition().getLabel()); //$NON-NLS-1$
        }

        toRet.append("<span title='"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("'>"); //$NON-NLS-1$

        if (cut && (xC.length() > 46))
        {
            toRet.append(xC.substring(0, 45) + "..."); //$NON-NLS-1$
        }
        else
        {
            toRet.append(xC);
        }

        toRet.append("</span>"); //$NON-NLS-1$

        //        }
        return toRet;
    }

    public Element getCARDID(ngtXMLHandler xmlToPrint, Element root, boolean cut) throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        Element img = xmlToPrint.getDocument().createElement("img"); //$NON-NLS-1$
        img.setAttribute("style", "cursor:hand"); //$NON-NLS-1$ //$NON-NLS-2$
        img.setAttribute("hspace", "3"); //$NON-NLS-1$ //$NON-NLS-2$
        img.setAttribute("border", "0"); //$NON-NLS-1$ //$NON-NLS-2$
        img.setAttribute("align", "absmiddle"); //$NON-NLS-1$ //$NON-NLS-2$
        img.setAttribute("class", "lui"); //$NON-NLS-1$ //$NON-NLS-2$
        

        if (ctxObj.getName().equals("Ebo_Template")) //$NON-NLS-1$
        {

            toRet.append(ctxObj.getBoDefinition().getLabel());
            

            boObject o = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(), "Ebo_ClsReg", ctxObj.getAttribute("masterObjectClass").getValueLong()); //$NON-NLS-1$ //$NON-NLS-2$
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.238") + o.getAttribute("description").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            img.setAttribute("title", toRet.toString()); //$NON-NLS-1$
            img.setAttribute("src", ctxObj.getEboContext().getApplicationUrl() + "/resources/" + o.getAttribute("name").getValueString() + "/ico16tmpl.gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            img.setAttribute("width", "16"); //$NON-NLS-1$ //$NON-NLS-2$
            img.setAttribute("height", "16"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg")) //$NON-NLS-1$
        {
            
            toRet.append("Classe do objecto "); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            img.setAttribute("title", toRet.toString()); //$NON-NLS-1$
            img.setAttribute("src", ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getAttribute("name").getValueString() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            img.setAttribute("width", "16"); //$NON-NLS-1$ //$NON-NLS-2$
            img.setAttribute("height", "16"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else if (ctxObj.getName().equals("runtimeAddress")) //$NON-NLS-1$
        {
            
            boObject oref = ctxObj.getAttribute("refObj").getObject(); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            img.setAttribute("title", toRet.toString()); //$NON-NLS-1$
            if( oref!= null )
            {
               img.setAttribute("src", ctxObj.getEboContext().getApplicationUrl() + "/resources/" + oref.getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            else
            {
               img.setAttribute("src", ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
            img.setAttribute("width", "16"); //$NON-NLS-1$ //$NON-NLS-2$
            img.setAttribute("height", "16"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        else
        {
            toRet.append(ctxObj.getBoDefinition().getLabel());
            img.setAttribute("title", toRet.toString()); //$NON-NLS-1$
            img.setAttribute("src", ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            img.setAttribute("width", "16"); //$NON-NLS-1$ //$NON-NLS-2$
            img.setAttribute("height", "16"); //$NON-NLS-1$ //$NON-NLS-2$
        }

        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        if( xC.toString().trim().length() < 1 && !ctxObj.exists() )
        {
            xC.setLength(0);
            xC.append(Messages.getString("xwfActivity_ViewerImpl.99")+ ctxObj.getBoDefinition().getLabel() ); //$NON-NLS-1$
        }
        root.appendChild(img);
        
        Element span = xmlToPrint.getDocument().createElement("span"); //$NON-NLS-1$
        span.setAttribute("title", xC.toString()); //$NON-NLS-1$
        
        
        toRet.delete(0, toRet.length());
        if (cut && xC.length() > 46)
        {
            toRet.append(xC.substring(0, 45) + "..."); //$NON-NLS-1$
        }
        else
        {
            toRet.append(xC);
        }
        span.appendChild(xmlToPrint.getDocument().createTextNode(toRet.toString()));
        root.appendChild(span);
        return root;
    }

    /**
      *    PRESENTATION LAYER OF OBJECT
      *
      *
      *
      */
    public StringBuffer getCARDIDwLink()
        throws boRuntimeException
    {
        return getCARDIDwLink(false, false, null);
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape)
        throws boRuntimeException
    {
        return getCARDIDwLink(false, false, null);
    }

    public StringBuffer getCARDIDwLink(boolean doubleEscape, String extraParameters)
        throws boRuntimeException
    {
        return getCARDIDwLink(false, doubleEscape, extraParameters);
    }

    public StringBuffer getCARDIDwLink(boolean newPage, boolean doubleEscape, String extraParameters)
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        //    toRet.append("<table id='");
        //  toRet.append(ctxObj.bo_boui);
        //   toRet.append("' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>");
        //    toRet.append("<div class='lu ro'><span class='lui' onclick=\"");        

        if(!newPage)
        {
            if (doubleEscape)
            {
                toRet.append("<span class='lui' onclick=\\\""); //$NON-NLS-1$
            }
            else
            {
                toRet.append("<span class='lui' onclick=\""); //$NON-NLS-1$
            }
            
            toRet.append("winmain().openDoc('medium','"); //$NON-NLS-1$
            toRet.append(ctxObj.getName().toLowerCase());
            toRet.append(
                "','edit','" + ((extraParameters == null) ? "" : (extraParameters + "&")) + "method=edit&boui=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            );
            toRet.append(ctxObj.bo_boui);
            toRet.append("','"); //$NON-NLS-1$
            toRet.append(""); //$NON-NLS-1$
            toRet.append("','"); //$NON-NLS-1$
            toRet.append(""); //$NON-NLS-1$
            toRet.append("','"); //$NON-NLS-1$
            toRet.append(ctxObj.getName());
    
            if (doubleEscape)
            {
                toRet.append("',window.windowIDX)\\\">"); //$NON-NLS-1$
            }
            else
            {
                toRet.append("',window.windowIDX)\">"); //$NON-NLS-1$
            }
        }

        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='"); //$NON-NLS-1$
        toRet.append(Messages.getString("xwfActivity_ViewerImpl.239")); //$NON-NLS-1$
        toRet.append(ctxObj.getBoDefinition().getLabel());
        toRet.append("' src='resources/" + ctxObj.getName() + "/ico16.gif"); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.append("' width='16' height='16'/><span "); //$NON-NLS-1$

        //  if ( !ctxObj.exists() )
        //  {   
        //      toRet.append('>');
        //      toRet.append(ctxObj.getBoDefinition().getLabel());
        //      toRet.append("</span>");
        // }
        //  else
        //  {
        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        toRet.append("title='"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("'>"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("</span></span>"); //$NON-NLS-1$

        //  }
        // toRet.append("</td></tr></tbody></table>");
        return toRet;
    }

    public StringBuffer getURL()
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        toRet.append("<a  href='"); //$NON-NLS-1$
        toRet.append(ctxObj.getEboContext().getApplicationUrl());
        toRet.append("/"); //$NON-NLS-1$
        toRet.append("__viewObject.jsp"); //$NON-NLS-1$
        toRet.append("?method=edit&boui="); //$NON-NLS-1$
        toRet.append(ctxObj.bo_boui);
        toRet.append("&object="); //$NON-NLS-1$
        toRet.append(ctxObj.getName());
        toRet.append("'>"); //$NON-NLS-1$

        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' border='0' title='"); //$NON-NLS-1$
        toRet.append(Messages.getString("xwfActivity_ViewerImpl.132")); //$NON-NLS-1$
        toRet.append(ctxObj.getBoDefinition().getLabel());
        toRet.append(
            "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + //$NON-NLS-1$ //$NON-NLS-2$
            "/ico16.gif" //$NON-NLS-1$
        );
        toRet.append("' width='16' height='16'/><span "); //$NON-NLS-1$

        //  if ( !ctxObj.exists() )
        //  {   
        //      toRet.append('>');
        //      toRet.append(ctxObj.getBoDefinition().getLabel());
        //      toRet.append("</span>");
        // }
        //  else
        //  {
        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        toRet.append("title='"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("'>"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("</a>"); //$NON-NLS-1$

        //  }
        // toRet.append("</td></tr></tbody></table>");
        return toRet;
    }

    public StringBuffer getExplainProperties(docHTML doc)
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        return toRet;
    }
    public StringBuffer getCARDIDwStatewLink()
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        //toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' src='");
        //toRet.append("resources/"+ctxObj.getName()+"/ico16.gif");
        //toRet.append("' width='16' height='16'/> ");
        toRet.append("<span class='lui' onclick=\""); //$NON-NLS-1$
        toRet.append("winmain().openDoc('medium','"); //$NON-NLS-1$
        toRet.append(ctxObj.getName().toLowerCase());
        toRet.append("','edit','"+"method=edit&boui="); //$NON-NLS-1$ //$NON-NLS-2$
        toRet.append(ctxObj.bo_boui);
        toRet.append("','"); //$NON-NLS-1$
        toRet.append(""); //$NON-NLS-1$
        toRet.append("','"); //$NON-NLS-1$
        toRet.append(""); //$NON-NLS-1$
        toRet.append("','"); //$NON-NLS-1$
        toRet.append(ctxObj.getName());       
        toRet.append("',window.windowIDX)\">"); //$NON-NLS-1$
        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='"); //$NON-NLS-1$

        if (ctxObj.getName().equals("Ebo_Template")) //$NON-NLS-1$
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.132")); //$NON-NLS-1$

            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(
                    ctxObj.getEboContext(), "Ebo_ClsReg", //$NON-NLS-1$
                    ctxObj.getAttribute("masterObjectClass").getValueLong() //$NON-NLS-1$
                );
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.155") + o.getAttribute("description").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                o.getAttribute("name").getValueString() + "/ico16tmpl.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg")) //$NON-NLS-1$
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.163")); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                ctxObj.getAttribute("name").getValueString() + "/ico16.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.169")); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + //$NON-NLS-1$ //$NON-NLS-2$
                "/ico16.gif" //$NON-NLS-1$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }

        //        if ( !ctxObj.exists() )
        //        {   
        //            toRet.append("<span>");
        //            toRet.append(ctxObj.getBoDefinition().getLabel());
        //            toRet.append("</span>");
        //        }
        //        else
        //        {
        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        toRet.append(ctxObj.getICONComposedState());

        // toRet.append("<img align='absmiddle' hspace='1' src='resources/");
        // toRet.append(getStringComposedState());
        //  toRet.append(".gif' width=16 height=16 />");
        toRet.append("<span "); //$NON-NLS-1$
        toRet.append("title='"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("'>"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("</span>"); //$NON-NLS-1$
        toRet.append("</span>"); //$NON-NLS-1$

        //     }
        return toRet;
    }

    public StringBuffer getCARDIDwState()
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        //toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' src='");
        //toRet.append("resources/"+ctxObj.getName()+"/ico16.gif");
        //toRet.append("' width='16' height='16'/> ");
        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='"); //$NON-NLS-1$

        if (ctxObj.getName().equals("Ebo_Template")) //$NON-NLS-1$
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.181")); //$NON-NLS-1$

            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(
                    ctxObj.getEboContext(), "Ebo_ClsReg", //$NON-NLS-1$
                    ctxObj.getAttribute("masterObjectClass").getValueLong() //$NON-NLS-1$
                );
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.184") + o.getAttribute("description").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                o.getAttribute("name").getValueString() + "/ico16tmpl.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg")) //$NON-NLS-1$
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.192")); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + //$NON-NLS-1$ //$NON-NLS-2$
                ctxObj.getAttribute("name").getValueString() + "/ico16.gif" //$NON-NLS-1$ //$NON-NLS-2$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }
        else
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.198")); //$NON-NLS-1$
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append(
                "' src='" + ctxObj.getEboContext().getApplicationUrl() + "/resources/" + ctxObj.getName() + //$NON-NLS-1$ //$NON-NLS-2$
                "/ico16.gif" //$NON-NLS-1$
            );
            toRet.append("' width='16' height='16'/>"); //$NON-NLS-1$
        }

        //        if ( !ctxObj.exists() )
        //        {   
        //            toRet.append("<span>");
        //            toRet.append(ctxObj.getBoDefinition().getLabel());
        //            toRet.append("</span>");
        //        }
        //        else
        //        {
        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        toRet.append(ctxObj.getICONComposedState());

        // toRet.append("<img align='absmiddle' hspace='1' src='resources/");
        // toRet.append(getStringComposedState());
        //  toRet.append(".gif' width=16 height=16 />");
        toRet.append("<span "); //$NON-NLS-1$
        toRet.append("title='"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("'>"); //$NON-NLS-1$
        toRet.append(xC);
        toRet.append("</span>"); //$NON-NLS-1$

        //     }
        return toRet;
    }

    public StringBuffer getSTATUS()
        throws boRuntimeException, SQLException
    {
        StringBuffer toRet = new StringBuffer();

        if (!ctxObj.exists() && (ctxObj.getMode() != ctxObj.MODE_EDIT_TEMPLATE))
        {
            toRet.append(Messages.getString("xwfActivity_ViewerImpl.240")); //$NON-NLS-1$
        }
        else if (ctxObj.getMode() == ctxObj.MODE_EDIT)
        {
            long xboui = ctxObj.getAttribute("CREATOR").getValueLong(); //$NON-NLS-1$

            if (xboui > 0)
            {
                boObject u1 = ctxObj.getObject(xboui);
                toRet.append(
                    Messages.getString("xwfActivity_ViewerImpl.209") + " ( " + ctxObj.getDataRow().getLong("SYS_ICN") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    Messages.getString("xwfActivity_ViewerImpl.241") //$NON-NLS-1$
                );
                toRet.append(u1.getCARDID());
                toRet.append(Messages.getString("xwfActivity_ViewerImpl.213")); //$NON-NLS-1$

                toRet.append(
                    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(
                        ctxObj.getDataRow().getDate("SYS_DTCREATE") //$NON-NLS-1$
                    )
                );
            }
            else
            {
                toRet.append(
                    Messages.getString("xwfActivity_ViewerImpl.215") + " ( " + ctxObj.getDataRow().getLong("SYS_ICN") + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    " )" //$NON-NLS-1$
                );
            }
        }
        else if (ctxObj.getMode() == ctxObj.MODE_EDIT_TEMPLATE)
        {
            boObject tmpl = ctxObj.getAttribute("TEMPLATE").getObject(); //$NON-NLS-1$

            if (tmpl.exists())
            {
                toRet.append(Messages.getString("xwfActivity_ViewerImpl.220")); //$NON-NLS-1$
                toRet.append(" ( ").append(tmpl.getDataRow().getLong("SYS_ICN")).append(Messages.getString("xwfActivity_ViewerImpl.223")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                long xboui = tmpl.getAttribute("CREATOR").getValueLong(); //$NON-NLS-1$

                if (xboui > 0)
                {
                    boObject u1 = ctxObj.getObject(xboui);
                    toRet.append(u1.getCARDID());
                    toRet.append(Messages.getString("xwfActivity_ViewerImpl.225")); //$NON-NLS-1$
                    toRet.append(
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(
                            tmpl.getDataRow().getDate("SYS_DTCREATE") //$NON-NLS-1$
                        )
                    );
                    toRet.append(Messages.getString("xwfActivity_ViewerImpl.227")); //$NON-NLS-1$
                    toRet.append(
                        DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(
                            tmpl.getDataRow().getDate("SYS_DTSAVE") //$NON-NLS-1$
                        )
                    );
                }

                //toRet.append("</span>");
            }
            else
            {
                toRet.append(Messages.getString("xwfActivity_ViewerImpl.229")); //$NON-NLS-1$
            }
        }

        /*
        if(ctxObj.isChanged())
        {
            toRet.append("&nbsp;(Alterado)&nbsp;");
        }
        else
        {
            toRet.append("&nbsp;(Não Alterado)&nbsp;");
        }
        */
        return toRet;
    }

    public StringBuffer getTextCARDID()
        throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        toRet.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));

        return toRet;
    }

    public String getICONComposedState()
        throws boRuntimeException
    {
        String toRet = ""; //$NON-NLS-1$

        if (ctxObj.getStateManager() != null)
        {
            toRet = ctxObj.getStateManager().getStateHTMLICON(ctxObj);
        }
        else
        {
            //toRet="none";
            toRet = "<IMG src='resources/none.gif' height=16 width=16 />"; //$NON-NLS-1$
        }

        return toRet;
    }

    public String getStringComposedState()
        throws boRuntimeException
    {
        String toRet = ""; //$NON-NLS-1$

        if (ctxObj.getStateManager() != null)
        {
            toRet = ctxObj.getStateManager().getStateString(ctxObj);
        }
        else
        {
            toRet = "none"; //$NON-NLS-1$
        }

        return toRet;
    }

    public StringBuffer getOpenObjectScript()
        throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc('medium','"); //$NON-NLS-1$
        sb.append(ctxObj.getName().toLowerCase());
        sb.append("','edit','method=edit&boui="); //$NON-NLS-1$
        sb.append(ctxObj.getBoui());
        sb.append("')"); //$NON-NLS-1$

        return sb;
    }

    public String getLabel()
        throws boRuntimeException
    {
        return ctxObj.bo_definition.getLabel();
    }
}
