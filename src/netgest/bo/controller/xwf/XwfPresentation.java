/*Enconding=UTF-8*/
package netgest.bo.controller.xwf;

import java.io.IOException;

import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.basic.BasicPresentation;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.Element;
import netgest.bo.presentation.render.elements.Splitter;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.xwf.presentation.xwfActivityViewer;
import netgest.xwf.presentation.xwfMainViewer;
import netgest.xwf.presentation.xwfMenuViewer;

/**
 * <p>Title: XwfPresentation </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class XwfPresentation  extends BasicPresentation
{    
    
    
    public XwfPresentation(XwfController controller)
    {
        super(controller);                     
    }        
    public void writeToolBar(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList) throws boRuntimeException,IOException
    {                
        if("xwfDefActivity".equals(currObjectList.getBoDef().getName()))
        {
            xwfMenuViewer.renderDefActivity(getXwfController(),currObjectList.getObject().getBoui() ,pageContext);
        }
        else if("xwfReassign".equals(currObjectList.getBoDef().getName()))
        {
            xwfMenuViewer.renderReassignActivity(getXwfController(),currObjectList.getObject().getBoui() ,pageContext);
        }
        else if("xwfActivityTransfer".equals(currObjectList.getBoDef().getName()))
        {
            xwfMenuViewer.renderTransferProcess(getXwfController(),currObjectList.getObject().getBoui() ,pageContext);
        }
        else
        {
            super.writeToolBar(DOCLIST,pageContext,currObjectList);
        }
//        xwfMenuViewer.render(getXwfController(),pageContext);
    }   
    public void writeHeaderHandler(docHTML_controler DOCLIST,PageContext pageContext) throws boRuntimeException,IOException
    {
//        StringBuffer toPrint = new StringBuffer("<tr><td valign=\"top\"><div  style=\"padding:2px;background-color:#D4E5FB\">");
//        xwfMainViewer.renderHeader(getXwfController(),toPrint);
//		toPrint.append("</div>");
//        toPrint.append("<table id=\"activityData\" class=\"section\" style=\"display:none;background-color:#D4E5FB\" cellSpacing=\"0\" cellPadding=\"3\"><tr><td  valign=\"top\">");
//        pageContext.getOut().print(toPrint.toString());
//        xwfMainViewer.renderActivityData(getXwfController(),getDocHTML(),DOCLIST,pageContext);								
//        pageContext.getOut().print("</td></tr></table></td></tr>");        
    }    
    public String writeFooterHandler() throws boRuntimeException
    {
        StringBuffer sb = new StringBuffer();
//        sb.append("<tr>");
//        sb.append(" <td valign=bottom>");        
//        sb.append("     <div id = \"choiceDiv\" style=\"padding:0px;height:100px;background-color:#D4E5FB\">\n");        
//        try 
//        {
//            sb.append("<table cellpadding=\"0\" cellspacing=\"0\"  style=\"width:100%;height:100%\">\n");
//            sb.append("<tr>\n");
//            sb.append("<td>\n");            
//            sb.append(xwfChoiceViewer.renderHeader(getXwfController()));
//            sb.append("\n");
//            sb.append("</td>\n");
//            sb.append("</tr>\n");            
//            sb.append("<tr height=\"100%\">\n");
//            sb.append("<td>\n");            
//            sb.append(xwfMainViewer.renderProgramControl(getXwfController(),getController().getDocHTML().getDocIdx()));
//            sb.append("\n");
//            sb.append("</td>\n");
//            sb.append("</tr>\n");            
//            sb.append("</table>\n");
//            sb.append("\n</div>");            
//        } 
//        catch (IOException ex) 
//        {     
//            sb = new StringBuffer();
//        }        
//        sb.append(" </td>");
//        sb.append("</tr>");                    
        return sb.toString();    
    }
    
//    public String writeJspFooter(boObject object, boObjectList currObjectList, String method, String inputMethod, String requestedBoui, boolean isMasterDoc, String p_typeForm,HttpServletRequest request,String jspName) throws boRuntimeException
    public String writeJspFooter(boObject object,boObjectList currObjectList, Hashtable options, boolean isMasterDoc,HttpServletRequest request) throws boRuntimeException
    {
        StringBuffer JSPFooter = new StringBuffer();
        JSPFooter.append(super.writeJspFooter( object,  currObjectList, options,  isMasterDoc, request));        
        JSPFooter.append("<INPUT type='hidden' name='").append(ControllerFactory.CONTROLLER_NAME_KEY).append("'  value='").append(getController().getName()).append("'/>\n");
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY).append("' value='").append(getXwfController().getRuntimeProgramBoui()).append("'/>\n");        
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY).append("' value='").append(getXwfController().getRuntimeActivityBoui()).append("'/>\n");
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.ACTIVITY_STATE_BOUI_KEY).append("'/>\n");
//        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.ACTIVITY_ACTION_CODE_KEY).append("'/>\n");
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.ACTION_CODE_KEY).append("'/>\n");
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.ACTIVITY_VALUE_KEY).append("'/>\n");
        JSPFooter.append("<INPUT type='hidden' name='").append(XwfKeys.VIEWER_TYPE_KEY).append("'/>\n");
        
        return JSPFooter.toString();
    }
    public String writeCSS() throws boRuntimeException
    {
//        StringBuffer sb = new StringBuffer("<style type=\"text/css\">\n");
//        sb.append("@import url('ieThemes/0/global/ui-global.css');\n");
//        sb.append("@import url('ieLibrary/splitter/splitter.css');\n");
//        sb.append("@import url('ieThemes/0/splitter/ui-splitter.css');\n");
//        sb.append("@import url('ieLibrary/menu/menu.css');\n");
//        sb.append("@import url('ieThemes/0/menu/ui-menu.css');\n");
//        sb.append(" </style>\n");            
//        return sb.toString();
        return "";
    }
    public String writeJS() throws boRuntimeException
    {
        StringBuffer result = new StringBuffer();
        result.append(super.writeJS());
        if(result.toString().indexOf("FCKeditor") == -1)
        {
            result.append("\n<script type=\"text/javascript\" src=\"FCKeditor/fckeditor.js\"></script>");
        }
        result.append("<script LANGUAGE=\"javascript\" SRC=\"ieLibrary/wkfl/xwfcontrol.js\"></script>\n");
        return result.toString();
    }
    
    public XwfController getXwfController()
    {
        return (XwfController)super.getController();
    }      
    
    public StringBuffer renderPath( HttpServletRequest request ) throws boRuntimeException
    {
        StringBuffer result = new StringBuffer();
        boObject object = null;
        if(getDocHTML().getMasterBoList() != null)
        {
            object = getDocHTML().getMasterBoList().getObject();
            if(object != null)
            {
                if(!"xwfReassign".equals(object.getName()) && !"xwfActivityTransfer".equals(object.getName()))
                {
                    result =  super.renderPath(request);    
                }
            }
        }  
        return result;
    }
}