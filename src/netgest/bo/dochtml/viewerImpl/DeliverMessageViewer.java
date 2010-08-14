/*Enconding=UTF-8*/
package netgest.bo.dochtml.viewerImpl;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.dochtml.viewerImpl.ObjectViewer;
import netgest.bo.dochtml.docHTML;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.message.PostInformation;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.*;
import netgest.bo.system.Logger;
import org.w3c.dom.Element;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class DeliverMessageViewer implements ObjectViewer
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     public boObject ctxObj;
     //logger
    private static Logger logger = Logger.getLogger("netgest.bo.dochtml.viewerImpl.DeliverMessageViewer");
     
    public void setContextObject(boObject objTarget)
    {
        ctxObj = objTarget;
    }

    public boObject getContextObject()
    {
        return ctxObj;
    }
    
    public StringBuffer getCARDIDwNoIMG() throws boRuntimeException
    {
        return getCARDIDwNoIMG(true);
    }

    public StringBuffer getCARDIDwNoIMG(boolean cut) throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        StringBuffer xC = new StringBuffer();
        xC.append(getCardText());
        
         if( xC.toString().trim().length() < 1 && !ctxObj.exists() )
        {
            xC.setLength(0);
            xC.append("Novo(a) "+ ctxObj.getBoDefinition().getLabel() );
        }

        if (xC.length() > 46 && cut)
        {
            toRet.append(xC.substring(0, 45) + "...");
        }
        else
        {
            toRet.append(xC);
        }

        //        }
        return toRet;
    }

    public String getSrcForIcon16() throws boRuntimeException
    {
        String toRet="";
            
        boObject oref = ctxObj.getAttribute("refObj").getObject();
       
        if( oref!= null )
        {
            toRet= "resources/" + oref.getName() + "/ico16.gif";    
        }
        else
        {
            toRet= "resources/" + ctxObj.getName() + "/ico16.gif";
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

        if (ctxObj.getName().equals("Ebo_Template"))
        {
            //toRet.append("Objecto ");

            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(), "Ebo_ClsReg", ctxObj.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " + o.getAttribute("description").getValueString());
            toRet.append("' src='" + getBaseUrl() + "resources/" + o.getAttribute("name").getValueString() + "/ico16tmpl.gif");
            toRet.append("' width='16' height='16'/>");
        }        
        else
        {
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getName() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");            
        }

        StringBuffer xC = new StringBuffer();
        xC.append(getCardText());
        if( xC.toString().trim().length() < 1 && !ctxObj.exists() )
        {
            xC.setLength(0);
            xC.append("Novo(a) "+ ctxObj.getBoDefinition().getLabel() );
        }
        toRet.append("<span title='");
        toRet.append("");
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

        //        }
        return toRet;
    }
    public Element getCARDID(ngtXMLHandler xmlToPrint, Element root, boolean cut) throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();
        Element img = xmlToPrint.getDocument().createElement("img");
        img.setAttribute("style", "cursor:hand");
        img.setAttribute("hspace", "3");
        img.setAttribute("border", "0");
        img.setAttribute("align", "absmiddle");
        img.setAttribute("class", "lui");

        if (ctxObj.getName().equals("Ebo_Template"))
        {
            toRet.append(ctxObj.getBoDefinition().getLabel());
            

            boObject o = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(), "Ebo_ClsReg", ctxObj.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " + o.getAttribute("description").getValueString());
            img.setAttribute("title", toRet.toString());
            img.setAttribute("src", getBaseUrl() + "resources/" + o.getAttribute("name").getValueString() + "/ico16tmpl.gif");
            img.setAttribute("width", "16");
            img.setAttribute("height", "16");
        }
        else
        {
            toRet.append(ctxObj.getBoDefinition().getLabel());
            img.setAttribute("title", toRet.toString());
            img.setAttribute("src", getBaseUrl() + "resources/" + ctxObj.getName() + "/ico16.gif");
            img.setAttribute("width", "16");
            img.setAttribute("height", "16");
        }

        StringBuffer xC = new StringBuffer();
        xC.append(ctxObj.mergeAttributes(ctxObj.getBoDefinition().getCARDID(), ctxObj));
        if( xC.toString().trim().length() < 1 && !ctxObj.exists() )
        {
            xC.setLength(0);
            xC.append("Novo(a) "+ ctxObj.getBoDefinition().getLabel() );
        }
        root.appendChild(img);
        
        Element span = xmlToPrint.getDocument().createElement("span");
        span.setAttribute("title", xC.toString());
        
        toRet.delete(0, toRet.length());
        if (cut && xC.length() > 46)
        {
            toRet.append(xC.substring(0, 45) + "...");
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
        return getCARDIDwLink(false, doubleEscape, extraParameters );
    }

    public StringBuffer getCARDIDwLink(boolean newPage, boolean doubleEscape,String extraParameters )
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
        toRet.append(ctxObj.getBoDefinition().getLabel());
        toRet.append("' src='" + getSrcForIcon16());
        toRet.append("' width='16' height='16'/></span>");
        toRet.append(getCardText(doubleEscape, extraParameters));
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
        toRet.append(ctxObj.getBoDefinition().getLabel());
        toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getName() + "/ico16.gif");
        toRet.append("' width='16' height='16'/><span ");

        //  if ( !ctxObj.exists() )
        //  {   
        //      toRet.append('>');
        //      toRet.append(ctxObj.getBoDefinition().getLabel());
        //      toRet.append("</span>");
        // }
        //  else
        //  {
        StringBuffer xC = new StringBuffer();
        xC.append( getCardText());
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</a>");

        //  }
        // toRet.append("</td></tr></tbody></table>");
        return toRet;
    }

    public StringBuffer getExplainProperties(docHTML doc) throws boRuntimeException {
        StringBuffer toRet = new StringBuffer();
        
        StringBuffer boql = null;
        boObjectList bolist = null;
        StringBuffer pbouis = null; 
        if(ctxObj.exists())
        {
            boql = new StringBuffer("SELECT xwfProgramRuntime WHERE variables.value.valueObject = ");
            boql.append( ctxObj.getBoui() );
            boql.append(" or variables.value.valueList = ");
            boql.append( ctxObj.getBoui() );
            boql.append(" ORDER BY SYS_DTCREATE");
            bolist = boObjectList.list( doc.getEboContext(), boql.toString(),1, false );
            bolist.beforeFirst();
            pbouis = new StringBuffer();
            while ( bolist.next() )
            {
                if ( pbouis.indexOf( bolist.getCurrentBoui()+";") == -1 )
                {
                    pbouis.append( bolist.getCurrentBoui()+";" );
                    toRet.append( bolist.getObject().getCARDIDwLink() );
                    toRet.append("&nbsp;&nbsp;");
                }
            }
        }
    
        if("message".equals(ctxObj.getName()) ||
            "message".equals(ctxObj.getBoDefinition().getBoSuperBo())
        )
        {
            if(ctxObj.exists())
            {
                boql = new StringBuffer("SELECT xwfProgramRuntime WHERE message = ");
                boql.append( ctxObj.getBoui() );
                boql.append(" ORDER BY SYS_DTCREATE");
                bolist = boObjectList.list( doc.getEboContext(), boql.toString(),1, false );
                bolist.beforeFirst();
                while ( bolist.next() )
                {   
                    if ( pbouis.indexOf( bolist.getCurrentBoui()+";") == -1 )
                    {
                        toRet.append( bolist.getObject().getCARDIDwLink() );
                        toRet.append("&nbsp;&nbsp;");
                    }
                }
            }
            
            PostInformation.generateMessageInformation(toRet, ctxObj, doc.getEboContext());
            return toRet;
        }
    

        if (ctxObj.getMode() == boObject.MODE_EDIT_TEMPLATE) 
        {
        } 
        else if ((ctxObj.getMode() == boObject.MODE_EDIT) ||
                (ctxObj.getMode() == boObject.MODE_NEW)) 
        {
            if (ctxObj.getAttribute("TEMPLATE") != null) 
            {
                if (ctxObj.getAttribute("TEMPLATE").getValueLong() == 0) 
                {
                    toRet.append(" Para aplicar modelo ");
                    toRet.append("<span class='lui' onclick=\"winmain().openDocUrl('tall','__applyTemplate.jsp','?clientIDX='+getIDX()+'&operation=applyTemplate&docid='+getDocId()+'&bouiToApplyTemplate=");
                    toRet.append(ctxObj.getBoui());
                    toRet.append("','lookup') \" >clique aqui</span>");
                } 
                else 
                {
                    toRet.append(" Foi utilizado o modelo ");
                    toRet.append(ctxObj.getObject(ctxObj.getAttribute("TEMPLATE")
                                                    .getValueLong())
                                     .getCARDIDwLink());
                    toRet.append(", para aplicar outro modelo ");
                    toRet.append("<span class='lui' onclick=\"winmain().openDocUrl('tall','__applyTemplate.jsp','?clientIDX='+getIDX()+'&operation=applyTemplate&docid='+getDocId()+'&bouiToApplyTemplate=");
                    toRet.append(ctxObj.getBoui());
                    toRet.append("','lookup') \" >clique aqui</span>");                    
                }
                if("email".equalsIgnoreCase(ctxObj.getName()))
                {
                    if(ctxObj.exists() && !"E".equalsIgnoreCase(ctxObj.getAttribute("nature").getValueString()))
                    {
                        if(!"1".equals(ctxObj.getAttribute("already_send").getValueString()))
                        {
                            toRet.append("</td></tr><tr><td colspan='4'>")
                            .append("<font color=\"#FF0000\">Esta mensagem não foi enviada.</font>");
                        }
                        else
                        {
                            toRet.append("</td></tr><tr><td colspan='4'>")
                                .append("Esta mensagem foi enviada.");
                        }
                    }
                    if(ctxObj.exists() && "E".equalsIgnoreCase(ctxObj.getAttribute("nature").getValueString()))
                    {
                        //prioridade
                        if("0".equals(ctxObj.getAttribute("priority").getValueString()))
                        {
                            toRet.append("</td></tr><tr><td colspan='4'>");
                            //toRet.append("<IMG id=\"idImgStatusBar\" SRC=\"resources/emailInfo.gif\" VALIGN=\"middle\">&nbsp;");
                            toRet.append("Esta mensagem foi enviada com o grau de importância baixa.");
                        }
                        if("2".equals(ctxObj.getAttribute("priority").getValueString()))
                        {
                            toRet.append("</td></tr><tr><td colspan='4'>");
                            toRet.append("Esta mensagem foi enviada com o grau de importância alta.");
                        }
                    
                        if("1".equals(ctxObj.getAttribute("send_read_receipt").getValueString()) &&
                            ctxObj.getAttribute("send_date_read_receipt").getValueDate() == null)
                        {
                            toRet.append("</td></tr><tr><td colspan='4'>");
                            toRet.append("O remetente desta mensagem solicitou um recibo de leitura. ")
                            .append("<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                            .append(" >Clique aqui para enviar um recibo</span>");
                            
                        }
                        else if("1".equals(ctxObj.getAttribute("send_read_receipt").getValueString()) &&
                            ctxObj.getAttribute("send_date_read_receipt").getValueDate() != null)
                        {
                            String dt = null;
                            try
                            {
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");            
                                dt = df.format(ctxObj.getAttribute("send_date_read_receipt").getValueDate());                        
                            }catch(Exception e)
                            {
                                logger.warn("Erro ao efectuar o set das datas do email.", e);
                            }
                            if(dt != null)
                            {
                                toRet.append("</td></tr><tr><td colspan='4'>");
                                toRet.append("Recibo de leitura enviado na data ").append(dt).append(".");
                            }
                        }
                    }
                }
            }
        }

        return toRet;
    }

    public StringBuffer getCARDIDwState() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        //toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' src='");
        //toRet.append("resources/"+ctxObj.getName()+"/ico16.gif");
        //toRet.append("' width='16' height='16'/> ");
        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");

        if (ctxObj.getName().equals("Ebo_Template"))
        {
            toRet.append("Imagem representativa do objecto ");

            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(), "Ebo_ClsReg", ctxObj.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " + o.getAttribute("description").getValueString());
            toRet.append("' src='" + getBaseUrl() + "resources/" + o.getAttribute("name").getValueString() + "/ico16tmpl.gif");
            toRet.append("' width='16' height='16'/>");
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg"))
        {
            toRet.append("Imagem representativa da Classe do objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getAttribute("name").getValueString() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        }
        else
        {
            toRet.append("Imagem representativa do objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getName() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
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
        xC.append( getCardText());
        toRet.append(ctxObj.getICONComposedState());

        // toRet.append("<img align='absmiddle' hspace='1' src='resources/");
        // toRet.append(getStringComposedState());
        //  toRet.append(".gif' width=16 height=16 />");
        toRet.append("<span ");
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</span>");

        //     }
        return toRet;
    }
    
    public StringBuffer getCARDIDwStatewLink() throws boRuntimeException
    {
        StringBuffer toRet = new StringBuffer();

        //toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' src='");
        //toRet.append("resources/"+ctxObj.getName()+"/ico16.gif");
        //toRet.append("' width='16' height='16'/> ");
        toRet.append("<span class='lui' onclick=\"");
        toRet.append("winmain().openDoc('medium','");
        toRet.append(ctxObj.getName().toLowerCase());
        toRet.append("','edit','"+"method=edit&boui=");
        toRet.append(ctxObj.bo_boui);
        toRet.append("','");
        toRet.append("");
        toRet.append("','");
        toRet.append("");
        toRet.append("','");
        toRet.append(ctxObj.getName());       
        toRet.append("',window.windowIDX)\">");
        
        toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");

        if (ctxObj.getName().equals("Ebo_Template"))
        {
            toRet.append("Imagem representativa do objecto ");

            toRet.append(ctxObj.getBoDefinition().getLabel());

            boObject o = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(), "Ebo_ClsReg", ctxObj.getAttribute("masterObjectClass").getValueLong());
            toRet.append(" de " + o.getAttribute("description").getValueString());
            toRet.append("' src='" + getBaseUrl() + "resources/" + o.getAttribute("name").getValueString() + "/ico16tmpl.gif");
            toRet.append("' width='16' height='16'/>");
        }
        else if (ctxObj.getName().equals("Ebo_ClsReg"))
        {
            toRet.append("Imagem representativa da Classe do objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getAttribute("name").getValueString() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
        }
        else
        {
            toRet.append("Imagem representativa do objecto ");
            toRet.append(ctxObj.getBoDefinition().getLabel());
            toRet.append("' src='" + getBaseUrl() + "resources/" + ctxObj.getName() + "/ico16.gif");
            toRet.append("' width='16' height='16'/>");
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
        xC.append( getCardText());
        toRet.append(ctxObj.getICONComposedState());

        // toRet.append("<img align='absmiddle' hspace='1' src='resources/");
        // toRet.append(getStringComposedState());
        //  toRet.append(".gif' width=16 height=16 />");
        toRet.append("<span ");
        toRet.append("title='");
        toRet.append(xC);
        toRet.append("'>");
        toRet.append(xC);
        toRet.append("</span>");
        toRet.append("</span>");

        //     }
        return toRet;
    }

    public StringBuffer getSTATUS() throws boRuntimeException, SQLException
    {
        StringBuffer toRet = new StringBuffer();

        if (!ctxObj.exists() && (ctxObj.getMode() != ctxObj.MODE_EDIT_TEMPLATE))
        {
            toRet.append("&nbsp;ESTADO : <b>Novo</b>");
        }
        else if (ctxObj.getMode() == ctxObj.MODE_EDIT)
        {
            long xboui = ctxObj.getAttribute("CREATOR").getValueLong();

            if (xboui > 0)
            {
                boObject u1 = ctxObj.getObject(xboui);
                toRet.append("&nbsp;ESTADO : <b>Em edição</b>" + " ( " + ctxObj.getDataRow().getLong("SYS_ICN") + " ) Criado por :");
                toRet.append(u1.getCARDID());
                toRet.append(" Em ");

                toRet.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(ctxObj.getDataRow().getDate("SYS_DTCREATE")));
            }
            else
            {
                toRet.append("&nbsp;ESTADO : <b>Em edição</b>" + " ( " + ctxObj.getDataRow().getLong("SYS_ICN") + " )");
            }
        }
        else if (ctxObj.getMode() == ctxObj.MODE_EDIT_TEMPLATE)
        {
            boObject tmpl=ctxObj.getAttribute("TEMPLATE").getObject();
            if( tmpl.exists() )
            {
                toRet.append("&nbsp;ESTADO : <b>A editar modelo</b> " );
                toRet.append(" ( " ).append( tmpl.getDataRow().getLong("SYS_ICN") ).append(" ) Criado por " );
                long xboui = tmpl.getAttribute("CREATOR").getValueLong();
                if (xboui > 0)
                {
                    boObject u1 = ctxObj.getObject(xboui);
                    toRet.append(u1.getCARDID());
                    toRet.append(" Em ");
                    toRet.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(tmpl.getDataRow().getDate("SYS_DTCREATE")));
                    toRet.append(" Última Act. em  ");
                    toRet.append(DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(tmpl.getDataRow().getDate("SYS_DTSAVE")));
                    
                }
                //toRet.append("</span>");
                
            }
            else
            {
              toRet.append("&nbsp;ESTADO : <b>A criar modelo</b>");
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

    public StringBuffer getTextCARDID() throws boRuntimeException
    {
        return new StringBuffer(getCardText());
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

    public String getStringComposedState() throws boRuntimeException
    {
        String toRet = "";

        if (ctxObj.getStateManager() != null)
        {
            toRet = ctxObj.getStateManager().getStateString(ctxObj);
        }
        else
        {
            toRet = "none";
        }

        return toRet;
    }
    
    public StringBuffer getOpenObjectScript() throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
        sb.append("winmain().openDoc('medium','");
        sb.append( ctxObj.getName().toLowerCase() );
        sb.append("','edit','method=edit&boui=");
        sb.append( ctxObj.getBoui() );
        sb.append("')");
        return sb;
    }
     public String getLabel() throws boRuntimeException
     {
        return ctxObj.bo_definition.getLabel();
     }
     private String getCardText()
     {
        StringBuffer sb = new StringBuffer("");
        //E-MAIL Francisco Câmara - francisco.camara@itds.pt
        //Carta Francisco Câmara - francisco.camara@itds.pt - doc1
        try
        {
            
            String mediaType = ctxObj.getAttribute("media").getValueString();
            
            String mediaLabel ="";
            boObject p = ctxObj.getParent();
            if(p != null && "".equals(mediaType))
            {
                if("messageLetter".equals(p.getName()))
                {
                    mediaType = "Letter";
                }
                else if("messageFax".equals(p.getName()))
                {
                    mediaType = "Fax";
                }
                else if("messageSMS".equals(p.getName()))
                {
                    mediaType = "SMS";
                }
                else if("messageMail".equals(p.getName()))
                {
                    mediaType = "E-Mail";
                }
            }
            /*
            if(mediaType != null && !"".equals(p.getName()))
            {
            
                lovObject lov = LovManager.getLovObject( ctxObj.getEboContext(), "mediaMessage" );
                lov.beforeFirst();
                while( lov.next() )
                    {
                     if ( mediaType.equalsIgnoreCase( lov.getCode() ) )
                        {
                         mediaLabel = lov.getDescription();
                    }
                }
            }

            sb.append(mediaLabel);
            */
            String channelId = getChannelId(ctxObj);
            if(!".".equals(channelId) && !"".equals(channelId))
            {
                sb.append(channelId);
            }
            if( sb.length() == 0 )
            {
            String lastname = ctxObj.getAttribute("lastname").getValueString();
            String name = ctxObj.getAttribute("name").getValueString();
            if(name.endsWith(lastname))
            {
                sb.append(name);
            }
            else
            {
                sb.append(lastname).append(", ").append(name);
            }
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return sb.toString();
     }
     
     private String getChannelId(boObject ctxObj) throws boRuntimeException
     {
        boObject msg = ctxObj.getParent();
        boolean isFrom = msg.getAttribute("from").getValueLong() == ctxObj.getBoui(); 
        boObject p = ctxObj.getParent();
        if("messageLetter".equals(p.getName()))
        {
            return "";
        }
        else if("messageFax".equals(p.getName()))
        {
            if(!isFrom || !isSending(ctxObj.getEboContext(), msg.getBoui()))
            {
                return ctxObj.getAttribute("fax").getValueString();
            }
            else
            {
                return ctxObj.getAttribute("email").getValueString();
            }
        }
        else if("messageSMS".equals(p.getName()))
        {
            if(!isFrom)
            {
                return ctxObj.getAttribute("telemovel").getValueString();
            }
            return "";
        }
        else if("messageMail".equals(p.getName()))
        {
            return ctxObj.getAttribute("email").getValueString();
        }
        return "";
     }
     
     private boolean isSending(EboContext ctx, long msg)
     {
        PreparedStatement pst = null;
        ResultSet rs = null;
        try
        {
            Connection con = ctx.getConnectionData();
            pst = con.prepareStatement("select 1 from oxwfactivitysend s, oxwfvariable v, oxwfvarvalue vv where s.MESSAGE$ = v.BOUI and v.VALUE$ = vv.BOUI and vv.VALUEOBJECT$ = ?");
            pst.setLong(1,msg);
            rs = pst.executeQuery();
            if(rs.next())
            {
                return true;
            }
        }
        catch(Exception e)
        {
            //ignore
        }
        finally
        {
            try{if(rs != null) {rs.close();}}catch(Exception e){}
            try{if(pst != null) {pst.close();}}catch(Exception e){}
        }
        return false;
     }
     
     private String getCardText(boolean doubleEscape, String extraParameters)
     {
        StringBuffer sb = new StringBuffer("");
        //E-MAIL Francisco Câmara - francisco.camara@itds.pt
        //Carta Francisco Câmara - francisco.camara@itds.pt - doc1
        try
        {
            String mediaType = ctxObj.getAttribute("media").getValueString();
            boObject obj = ctxObj.getParent();
            String mediaLabel =null;
            if(obj != null)
            {
                String prefered = obj.getAttribute("preferedMedia").getValueString();
                if(MessageUtils.LETTER.equals(prefered))
                {
                    mediaLabel = "Carta";
                }
                else if(MessageUtils.FAX.equals(prefered))
                {
                    mediaLabel = "Fax";
                }
                else if(MessageUtils.SGIS.equals(prefered))
                {
                    mediaLabel = "SGIS";
                }
                else if(MessageUtils.SMS.equals(prefered))
                {
                    mediaLabel = "SMS";
                }
                else if(MessageUtils.EMAIL.equals(prefered))
                {
                    mediaLabel = "E-Mail";
                }
                else
                {
                    mediaLabel = prefered;
                }
            }
            
            
            if(mediaLabel == null && (mediaType != null && !"".equals(mediaType)))
            {
                boObject lov;
                lov = ctxObj.getBoManager().loadObject(ctxObj.getEboContext(),"Ebo_LOV","name='mediaMessage'");
                mediaLabel ="";
                if(lov.exists())
                {
                    boBridgeIterator lovdetails= lov.getBridge("details").iterator();
                    lovdetails.beforeFirst();
                    boObject det;
                    while(lovdetails.next())
                    {
                        det = lovdetails.currentRow().getObject();
                        if(mediaType.equalsIgnoreCase(det.getAttribute("value").getValueString()))
                        {
                            mediaLabel = det.getAttribute("description").getValueString();
                        }
                    }
                }
            }
            boolean hasWord = false;
            if(MessageUtils.LETTER.equals(mediaType) || MessageUtils.FAX.equals(mediaType))
            {
                boObject doc = MessageUtils.getBinaryBySourceBoui(obj, ctxObj.getBoui());
                if(doc != null)
                {
                    sb.append(getWordDocumentLink(mediaLabel, doc, ctxObj));
                    hasWord = true;
                }
            }
            if(hasWord)
            {
                sb.append(" - ");
            }
        
            if (doubleEscape)
            {
                sb.append("<span class='lui' onclick=\\\"");
            }
            else
            {
                sb.append("<span class='lui' onclick=\"");
            }
            
            sb.append("winmain().newPage(getIDX(),'");
            sb.append(ctxObj.getName().toLowerCase());
            sb.append("','edit','"+( extraParameters==null? "":extraParameters+"&" )+"method=edit&boui=");
            sb.append(ctxObj.bo_boui);
            sb.append("','");
            sb.append("");
            sb.append("','");
            sb.append("");
            sb.append("','");
            sb.append(ctxObj.getName());
    
            if (doubleEscape)
            {
                sb.append("',window.windowIDX)\\\">");
            }
            else
            {
                sb.append("',window.windowIDX)\">");
            }
            if(!hasWord)
            {
                sb.append(mediaLabel);
                String channelId = getChannelId(ctxObj);
                if(!"".equals(channelId))
                {
                    sb.append("[").append(channelId).append("]");
                }
                sb.append(" - ");
            }
            String lastname = ctxObj.getAttribute("lastname").getValueString();
            String name = ctxObj.getAttribute("name").getValueString();
            if(name.endsWith(lastname))
            {
                sb.append(name);
            }
            else
            {
                sb.append(lastname).append(", ").append(name);
            }
            sb.append("</span>");

        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return sb.toString();
     }
     
     private String getWordDocumentLink(String mediaLabel, boObject docObj, boObject ctxObj)
     {
        StringBuffer sb = new StringBuffer("");
        try
        {
            long boui = docObj.getBoui();
            String docName = docObj.getAttribute("description").getValueString();

            sb.append("</span><span class=lui onclick='javascript:boForm.executeStaticMeth(")
              .append("\"netgest.bo.impl.document.template.TemplateHelper.editWordDocumentInClientLocal\", ")
              .append("[\"this\", \""+boui+"\"]);'>")
              .append(mediaLabel);
            String channelId = getChannelId(ctxObj);
            if(!"".equals(channelId))
            {
                sb.append("[").append(channelId).append("]");
            }
            sb.append("</span>");
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return sb.toString();
     }
     
     public final String getBaseUrl(  )
     {
         String url = ctxObj.getEboContext().getApplicationUrl();
         if( url == null )
         {
             url = "";
         }
         else
         {
             if( !url.endsWith("/") )
             {
                 url += "/";
             }
         }
         return url;
     }
     
}