/*Enconding=UTF-8*/
package netgest.bo.controller.basic;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.Presentation;
import netgest.bo.controller.common.PathItem;
import netgest.bo.def.boDefAttribute;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_builderMenus;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.dochtml.docHTMLgenerateToolBar;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

/**
 * <p>Title: BasicPresentation </p>
 * <p>Description:</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public class BasicPresentation  implements Presentation
{
    private Controller controller = null;
    private String type = null;
    
    public static final String  OPTION_METHOD = "method";
    public static final String  OPTION_INPUT_METHOD = "inputMethod";
    public static final String  OPTION_REQUESTED_BOUI = "requestedBoui";
    public static final String  OPTION_TYPE_FORM = "p_typeForm";
    public static final String  OPTION_JSP_NAME = "jspName";
    public static final String  OPTION_FORCE_NO_CHANGE = "forceNoChange";
    
          
    public BasicPresentation(Controller controller )
    {
        this.controller = controller;                     
    }
    public String getType()
    {
        return type;
    }
    public void setType(String type)
    {
        this.type = type;
    }    
    public void writeToolBar(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList) throws boRuntimeException,IOException
    {
        JspWriter out = pageContext.getOut();
        out.print("<TR height=\""); 
        out.print(getToolbarHeight(currObjectList));
        out.print("\">\n");
        out.print("<TD>");
        writeToolBarHtml(DOCLIST, pageContext,currObjectList);  
        out.print("</TD>\n");
        out.print("</TR>\n");                
    }   
    private int getToolbarHeight(boObjectList currObjectList)
    {        
        if( currObjectList.getFormat() == boObjectList.FORMAT_ONE)
        {
            return 48;
        }
        else
        {
            return 24;
        }
   }

    private void writeToolBarHtml(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList) throws boRuntimeException,java.io.IOException{
       String textTitle="";
       if ( currObjectList.getFormat()==boObjectList.FORMAT_ONE)
       {
            StringBuffer toRet=new StringBuffer();
            toRet.append("<span style=''><img align='absmiddle' hspace='1' src='resources/");
            toRet.append(currObjectList.getObject().getStringComposedState());
            toRet.append(".gif' width=16 height=16 /></span>");
            textTitle=currObjectList.getObject().bo_boui+toRet.toString();
           
            if( currObjectList.getObject().getAttribute("nrdoc") !=null )
            {
               
            }
           
       }
       docHTMLgenerateToolBar.render( docHTML_builderMenus.createMenu( getDocHTML() , DOCLIST, currObjectList , pageContext ) ,
                                    textTitle,
                                    pageContext,currObjectList );
    }    
    public void writeHeaderHandler(docHTML_controler DOCLIST,PageContext pageContext) throws boRuntimeException,IOException
    {
        
    }
    /*public StringBuffer pathRender( HttpServletRequest request ) throws boRuntimeException
    {
        return controller.getDocHTML(). path_Render(request);        
    }    */
    public String writeFooterHandler() throws boRuntimeException
    {
        docHTML doc=getDocHTML();
        if( doc.p_WebForm )
        {
            //return "<tr height=24><td><button>vvv</button></td></tr>";
            //<TR height=24><TD ><button>Cancelar</button></TD></TR>
        }
        return "";
    } 
    public String writeCSS() throws boRuntimeException
    {
        return "";
    }
    public String writeJS() throws boRuntimeException
    {
        StringBuffer result = new StringBuffer();
        boolean found = false;
        AttributeHandler attribute = null;
        long boui = this.getDocHTML().getContextObjectBoui();
        if(boui <= 0 && this.getDocHTML().getMasterObject() != null)
        {
            boui = this.getDocHTML().getMasterObject().getBoui();
        }
        if(boui > 0)
        {
            boObject object = this.getDocHTML().getObject(boui);
        if(object != null)
        {
            List attributes = object.getAttributes(boDefAttribute.VALUE_CLOB);
            for (int i = 0; i < attributes.size() && !found;  i++) 
            {
                attribute = (AttributeHandler)attributes.get(i);
                if(!"text".equalsIgnoreCase(attribute.getDefAttribute().getEditorType()))
                {
                    result.append("\n<script type=\"text/javascript\" src=\"FCKeditor/fckeditor.js\"></script>");
                    found = true;
                    }
                }
            }
        }
        return result.toString();
    }    
    public String writeJspHeader() throws boRuntimeException
    {
        return "";
    }
    
//    public String writeJspFooter(boObject object, boObjectList currObjectList, String method, String inputMethod, String requestedBoui, boolean isMasterDoc, String p_typeForm,HttpServletRequest request,String jspName) throws boRuntimeException
    public String writeJspFooter(boObject object,boObjectList currObjectList, Hashtable options, boolean isMasterDoc,HttpServletRequest request) throws boRuntimeException
    {
        StringBuffer JSPFooter = new StringBuffer();
        
        String method = (String)options.get(OPTION_METHOD);
        String inputMethod = (String)options.get(OPTION_INPUT_METHOD);
        String requestedBoui = (String)options.get(OPTION_REQUESTED_BOUI);
        String p_typeForm = (String)options.get(OPTION_TYPE_FORM);
        String jspName = (String)options.get(OPTION_JSP_NAME);
        
        writeCommonFormFooter(JSPFooter,getDocHTML(), object , method,inputMethod, requestedBoui,isMasterDoc,request,jspName);
        
        if ("1".equals(p_typeForm)) 
        {
            writeListFormFooter(JSPFooter,currObjectList,request);                                 
        } 
        else 
        {        
            writeTemplateFormFooter(JSPFooter,object);
            writeEditFormFooter(JSPFooter,getDocHTML(),object,request,options);        
        }
        return JSPFooter.toString();
    }
    private static void writeCommonFormFooter(StringBuffer JSPFooter,docHTML DOC, boObject BOI, String method,String inputMethod, String requestedBoui,boolean masterdoc,HttpServletRequest request,String jspName) throws boRuntimeException
    {
        
        JSPFooter.append("<INPUT type='hidden' name=").append(ControllerFactory.CONTROLLER_NAME_KEY).append(" value='").append(DOC.getController().getName()).append("' />\n");
        
        if( method != null ) 
        {        
            JSPFooter.append("<INPUT type='hidden' name=method value='").append(method).append("' />\n");
        }
        
        if ("resultBridge".equalsIgnoreCase( request.getParameter("listmode") ))
        {
            JSPFooter.append("<INPUT type='hidden' name='resultBridge' value='true' />");
        }

        if( request.getParameter("renderOnlyCardID") != null ) 
        {
            JSPFooter.append("<INPUT type='hidden' name='renderOnlyCardID' value='").append(request.getParameter("renderOnlyCardID")).append("'/>\n");
        }

        if( requestedBoui != null && BOI != null) 
        { 
            JSPFooter.append("<INPUT type='hidden' name=boui value='").append(BOI.getBoui()).append("' />\n");
        }


        JSPFooter.append("<INPUT type='hidden' name='boFormSubmitXml' />\n");

        if ( request.getParameter("object") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='object' value='").append(request.getParameter("object")).append("'/>\n");
        }

        if ( request.getParameter("menu") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='menu' value='").append(request.getParameter("menu")).append("'/>\n");
        }

        if ( request.getParameter("ctxParent") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='ctxParent' value='").append(request.getParameter("ctxParent")).append("'/>\n");
        }

        if ( request.getParameter("addToCtxParentBridge") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='addToCtxParentBridge' value='").append(request.getParameter("addToCtxParentBridge")).append("'/>\n");
        }


        if ( request.getParameter("relatedClientIDX") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='relatedClientIDX' value='").append(request.getParameter("relatedClientIDX")).append("'/>\n");
        }

        if ( request.getParameter("relatedDocid") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='relatedDocid' value='").append(request.getParameter("relatedDocid")).append("'/>\n");
        }    

        if ( request.getParameter("parent_attribute") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='parent_attribute' value='").append(request.getParameter("parent_attribute")).append("'/>\n");
        }
        
        if ( request.getParameter("relatedParentBridge") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='relatedParentBridge' value='").append(request.getParameter("relatedParentBridge")).append("'/>\n");
        }        
    
        if ( request.getParameter("relatedParent") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='relatedParent' value='").append(request.getParameter("relatedParent")).append("'/>\n");
        }        

        if ( request.getParameter("relatedParentDocid") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='relatedParentDocid' value='").append(request.getParameter("relatedParentDocid")).append("'/>\n");
        }        

        if( request.getParameter("parent_boui") != null ) 
        {

            if(  !( inputMethod != null && inputMethod.equalsIgnoreCase("newfromtemplate"))  ) 
            {
                JSPFooter.append( "<INPUT type='hidden' name=parent_boui value='").append(request.getParameter("parent_boui")).append("' />\n");
            }
        }


        if ( request.getParameter("list_frommethod") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='list_frommethod' value='").append(request.getParameter("list_frommethod")).append("'/>\n");
        }        

        if ( request.getParameter("searchClientIdx") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='searchClientIdx' value='").append(request.getParameter("searchClientIdx")).append("'/>\n");
        }        

        if ( request.getParameter("actIdxClient") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='actIdxClient' value='").append(request.getParameter("actIdxClient")).append("'/>\n");
        }        

        if ( request.getParameter("actRenderObj") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='actRenderObj' value='").append(request.getParameter("actRenderObj")).append("'/>\n");
        }        

        if ( request.getParameter("actRenderAttribute") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='actRenderAttribute' value='").append(request.getParameter("actRenderAttribute")).append("'/>\n");
        }        

        if ( request.getParameter("actRenderDocid") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='actRenderDocid' value='").append(request.getParameter("actRenderDocid")).append("'/>\n");
        }        

        if ( request.getParameter("ctxParentIdx") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='ctxParentIdx' value='").append(request.getParameter("ctxParentIdx")).append("'/>\n");
        }        

        if( request.getParameter("toClose") != null && !DOC.haveErrors()) 
        { 
            JSPFooter.append("<INPUT type='hidden' name='toClose' value='y' />\n");
        }
        if( request.getParameter("toNew") != null && !DOC.haveErrors()) 
        { 
            JSPFooter.append("<INPUT type='hidden' name='toNew' value='y' />\n");
        }
        if( request.getParameter("toPrint") != null && !DOC.haveErrors()) 
        { 
            JSPFooter.append("<INPUT type='hidden' name='toPrint' value='y' />\n");
        }

        if( masterdoc ) 
        {
            JSPFooter.append("<INPUT type='hidden' name=masterdoc value='true' />\n");
        }

        if( BOI != null && !BOI.getBoDefinition().getBoCanBeOrphan() )
        {
            JSPFooter.append("<INPUT type='hidden' value='no' name='orphan' /> \n");
        }
        if ( request.getParameter("boql") != null && requestedBoui==null  )            
        {
            JSPFooter.append("<INPUT type='hidden' name='boql' value=\"").append(request.getParameter("boql")).append("\"/>\n");
        }
        if ( request.getParameter("list_orderby") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='list_orderby' value='").append(request.getParameter("list_orderby")).append("'/>\n");
        }
        if ( request.getParameter("helperURL") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='helperURL' value='").append(request.getParameter("helperURL")).append("'/>\n");
        }

        JSPFooter.append("<INPUT type='hidden' name='boFormSubmitMode' />\n");
        
        JSPFooter.append("<INPUT type='hidden' name='boFormSubmitId' />\n");
        

        JSPFooter.append("<INPUT type='hidden' value='").append(DOC.getDocIdx()).append("' name='docid' />\n");
        
        JSPFooter.append("<INPUT type='hidden' value='").append(jspName).append("' name='boFormSubmitSecurity' />\n");
        
    }     
    private static void writeTemplateFormFooter(StringBuffer JSPFooter,boObject object) throws boRuntimeException
    {
        if (object != null && !object.getBoDefinition().getBoName().equalsIgnoreCase("Ebo_Template"))
        {
            if (object.getMode() == boObject.MODE_EDIT_TEMPLATE )
            {     
                JSPFooter.append("<INPUT type='hidden' name='editingTemplate' value='" + object.getAttribute("TEMPLATE").getValueString() + "' />\n");
            }
        }
    }       
        
    private static void writeEditFormFooter(StringBuffer JSPFooter,docHTML DOC,boObject BOI,HttpServletRequest request, Hashtable options) throws boRuntimeException
    {
        
        if ( request.getParameter("pathItem") != null )            
        {
            JSPFooter.append("<INPUT type='hidden' name='pathItem' value='").append(request.getParameter("pathItem")).append("'/>\n");
        }          
        else if( request.getAttribute( "pathItem" ) != null ) 
        { 
            JSPFooter.append("<INPUT type='hidden' name=pathItem value='").append(request.getAttribute("pathItem")).append("' />\n");
        }
        String forceNoChange = (String)options.get(OPTION_FORCE_NO_CHANGE);
        if ( DOC.getController().getNavigator() != null && !"true".equals(forceNoChange))
        { 
            JSPFooter.append("<INPUT type='hidden' name='boiChanged' value='").append(DOC.getController().getNavigator().isPathObjectsChanged()).append("' />\n");
        }
        if(BOI != null)
        {
            JSPFooter.append("<INPUT type='hidden' name='BOUI' value='").append(BOI.getBoui()).append("' />\n");
        }
        
    }
    private static void writeListFormFooter(StringBuffer JSPFooter,boObjectList currObjectList,HttpServletRequest request) throws boRuntimeException
    {        

        if (currObjectList != null && currObjectList.getParent() != null) 
        { 
            JSPFooter.append("<INPUT type='hidden' name='BOUI' value='").append(currObjectList.getParent().getBoui()).append("'/>\n");
            
            JSPFooter.append("<INPUT type='hidden' name='boiChanged' value='").append(currObjectList.getParent().isChanged() || currObjectList.containsChangedObjects()).append("'/>\n");                    
        }            
        
            if ( request.getParameter("list_fulltext")!=null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_fulltext' value='").append(request.getParameter("list_fulltext")).append("'/>\n");
            }
            
            if ( request.getParameter("list_letter") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_letter' value='").append(request.getParameter("list_letter")).append("'/>\n");
            }

            if ( request.getParameter("list_page") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_page' value='").append(request.getParameter("list_page")).append("'/>\n");
            }
            if ( request.getParameter("list_pagesize") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_pagesize' value='").append(request.getParameter("list_pagesize")).append("'/>\n");
            }
            if ( request.getParameter("list_WFirstRows") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_WFirstRows' value='").append(request.getParameter("list_WFirstRows")).append("'/>\n");
            }

            if ( request.getParameter("list_letter_field") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_letter_field' value='").append(request.getParameter("list_letter_field")).append("'/>\n");
            }

            if ( request.getParameter("listmode") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='listmode' value='").append(request.getParameter("listmode")).append("'/>\n");
            }


            if ( request.getParameter("showIcon") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showIcon' value='").append(request.getParameter("showIcon")).append("'/>\n");
            }

            if ( request.getParameter("showLines") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showLines' value='").append(request.getParameter("showLines")).append("'/>\n");
            }

            if ( request.getParameter("showStatus") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showStatus' value='").append(request.getParameter("showStatus")).append("'/>\n");
            }

            if ( request.getParameter("showPreview") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showPreview' value='").append(request.getParameter("showPreview")).append("'/>\n");
            }

            if ( request.getParameter("menu") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='menu' value='").append(request.getParameter("menu")).append("'/>\n");
            }
            
            if ( request.getParameter("canSelectRows") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='canSelectRows' value='").append(request.getParameter("canSelectRows")).append("'/>\n");
            }
            
            if ( request.getParameter("showBarFilter") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showBarFilter' value='").append(request.getParameter("showBarFilter")).append("'/>\n");
            }
            
            if ( request.getParameter("showBarStatus") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='showBarStatus' value='").append(request.getParameter("showBarStatus")).append("'/>\n");
            }
 
            if ( request.getParameter("editAttributes") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='editAttributes' value='").append(request.getParameter("editAttributes")).append("'/>\n");
            }

            if ( request.getParameter("waitingDetachAttribute") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='waitingDetachAttribute' value='").append(request.getParameter("waitingDetachAttribute")).append("'/>\n");
            }
            
//            if ( request.getParameter("onclick") != null )
//            {
//                JSPFooter.append("<INPUT type='hidden' name='onclick' value='").append(request.getParameter("onclick")).append("'/>\n");
//            }
            if ( request.getParameter("userClick") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='userClick' value='").append(request.getParameter("userClick")).append("'/>\n");
            }            
            if ( request.getParameter("userDblClick") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='userDblClick' value='").append(request.getParameter("userDblClick")).append("'/>\n");
            }  
            if ( request.getParameter("objectBoui") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='objectBoui' value='").append(request.getParameter("objectBoui")).append("'/>\n");
            }
            if ( request.getParameter("lineColorState") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='lineColorState' value='").append(request.getParameter("lineColorState")).append("'/>\n");
            }
            if( request.getParameter("list_orderby") != null )
            {
                JSPFooter.append("<INPUT type='hidden' name='list_orderby' value='").append(request.getParameter("list_orderby")).append("'/>\n");
            }

          
        //}
    }
   public StringBuffer renderPath( HttpServletRequest request ) throws boRuntimeException
   {        
        String pathItem=request.getParameter( "pathItem" );
        if ( pathItem == null )
        {
            pathItem=(String)request.getAttribute("pathItem");
        }    
        StringBuffer toRet = new StringBuffer();
        if(pathItem != null)
        {
            PathItem currentItem = controller.getNavigator().getPathItemById( pathItem ) ;
                        
            if ( currentItem.getParents()!= null && currentItem.getParents().size() > 0 )
            {
                toRet.append( "<TR height='1%'>" );
                toRet.append("<TD valign=top align=left class='breadCrumb'>" );                
                toRet.append( renderPath( currentItem , null ) );                                
                toRet.append("</TD></TR>");
            }
        }
    
     return toRet;
        
   }   
  private StringBuffer renderPath( PathItem item , ArrayList completePath ) throws boRuntimeException
  {
        StringBuffer toRet = new StringBuffer();
        if ( completePath == null )
        {
            completePath = controller.getNavigator().getCompletePath( item  );
        }
             
        PathItem lastItem = item;
        
        for (int i = completePath.size()-1; i>=0 ; i--) 
        {
            Object o = completePath.get( i );
            if ( o instanceof PathItem )
            {
         
                PathItem ito = ( PathItem)o;
                if(toRet.length() == 0)
                {
                    toRet.append("<span class='breadCrumbSep'>::</span>");
                }
                
                toRet.append( ito.renderLink( lastItem ) );
                lastItem = ito;
                toRet.append("<span class='breadCrumbSep'>:</span>");
            }
            else if ( o instanceof ArrayList )
            {
               toRet.append( renderPath( null, (ArrayList)o ) );    
            }
            else
            {
                //string
                toRet.append( o );
            }
        }
        
        if ( item != null )
        {
//            boObject o = controller.getDocHTML().getObject( item.getRelatedBoui() );
            toRet.append("<span class='breadCrumbLastItem'>");
//            toRet.append( o.getCARDIDwNoIMG() );
            toRet.append( item.getLabel() );
            toRet.append("</span>");
        }
        
        
        return toRet;
  }
   
    public docHTML getDocHTML()
    {
        return controller.getDocHTML();
    }    
    public Controller getController()
    {
        return controller;
    }    
}