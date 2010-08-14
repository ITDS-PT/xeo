/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.servlet.http.*;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;
import netgest.bo.def.*;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.TemplateWord;
import netgest.bo.localized.JSPMessages;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;
import netgest.bo.utils.SchemaUtils;

import netgest.utils.ClassUtils;

public final class docHTML_builderMenus  {

    private static final byte MODE_EDIT_OBJECT = 0;
    private static final byte MODE_LIST_OBJECTS = 1;
    private static final byte MODE_LIST_OBJECT_BRIDGE = 2;
    
    private static final byte EDIT_NORMAL   =0;
    
    private static final byte EDIT_TEMPLATE =1;
    private static final byte EDIT_OBJECT_TEMPLATE =2;
    
    private static final byte IN_NORMAL_DOC = 0;
    private static final byte IN_SEARCH_DOC = 1;
    
    public docHTML_builderMenus() {
        
    }
    
     private static String getViewerInAction(PageContext page)
    {
        String viewer = "";
        String tmpViewer = page.getPage().toString();  
        String[] part = null;
        if(tmpViewer.indexOf("@") != -1)
        {
            part = tmpViewer.split("@");   
        }
        String[] part2 = null;
        if(part != null && part[0].indexOf("general") != -1)
        {
            part2 = part[0].split("general");   
            viewer = part2[1];
        }                
        return viewer;
    }

    public static Hashtable[] createMenu(docHTML DOC , docHTML_controler DOCLIST,  boObjectList bolist , PageContext page  ) throws boRuntimeException{
        return createMenu( DOC , DOCLIST,  bolist ,  page , -1 );
    }
    public static Hashtable[] createMenu(docHTML DOC , docHTML_controler DOCLIST, boObjectList bolist , PageContext page , int hui ) throws boRuntimeException{
        boObject obj            = bolist.getObject();
        boolean inEditTemplate  = false;
        boObject objParent      =null;
     
        boolean objIstemplate   = false;
        boolean objIstemplateDet= false;
        long relatedObjBOUI     = 0;
        String masterObjClass   = null;

        byte format             = bolist.getFormat();
        
        byte mode   = MODE_EDIT_OBJECT;
        byte edit   = EDIT_NORMAL;
        byte in_doc = IN_NORMAL_DOC;
        
        
        
        
        objParent       = bolist.getParent();
        objIstemplate   = bolist.getBoDef().getName().equalsIgnoreCase("Ebo_Template");
        objIstemplateDet= bolist.getBoDef().getName().equalsIgnoreCase("Ebo_Map");

        if ( obj != null )
        {
            inEditTemplate  = obj.getMode() == boObject.MODE_EDIT_TEMPLATE;
        }
        else if ( objParent != null )
        {
            inEditTemplate  = objParent.getMode() == boObject.MODE_EDIT_TEMPLATE;
        }
        
        
        String attributeRelated="";
        String clsRelated="";
        String relatedClsAttribute="";
        long templateBoui=0;
        
        
        if ( objIstemplate ){
            String r=null;
            if ( obj != null )
            {
                r=obj.getParameter("relatedObjBOUI");
            }
            if( r !=null ) relatedObjBOUI=ClassUtils.convertToLong(r,0);
            
            if( relatedObjBOUI > 0 )
            {
                masterObjClass = obj.getAttribute("masterObjectClass").getValueString();
                edit = EDIT_OBJECT_TEMPLATE;
            }
            else 
            {
                r=bolist.getParameter("relatedObjBOUI") ;
                if( r !=null ) relatedObjBOUI=ClassUtils.convertToLong(r,0);
                if ( relatedObjBOUI >0 )
                {    
                    attributeRelated= bolist.getParameter("relatedObjAttribute") ;
                    clsRelated      = bolist.getParameter("relatedCls") ;
                    relatedClsAttribute = bolist.getParameter("relatedClsAttribute") ; 
                    templateBoui    = ClassUtils.convertToLong(bolist.getParameter("template_boui"),0);
                    edit = EDIT_TEMPLATE;
                    mode = MODE_LIST_OBJECT_BRIDGE;    
                }
            }
        }
        
        
        else if ( objIstemplateDet )
        {
            //boObject t=bolist.getParent();
            String r = null;
            if(objParent != null)
                r=objParent.getParameter("relatedObjBOUI");
            if( r !=null ) relatedObjBOUI=ClassUtils.convertToLong(r,0);
            if( relatedObjBOUI > 0 ) 
            {
                masterObjClass = objParent.getAttribute("masterObjectClass").getValueString();
                edit = EDIT_OBJECT_TEMPLATE;
                mode = MODE_LIST_OBJECT_BRIDGE;
            }
            else 
            {
                mode = MODE_LIST_OBJECT_BRIDGE;
            }
            
        }
        if ( edit!= EDIT_OBJECT_TEMPLATE ) 
        {
            
            if ( objParent != null )
            {
                if( format == boObjectList.FORMAT_MANY )
                {
                    mode = MODE_LIST_OBJECT_BRIDGE;    
                }
                else 
                {
                    mode = MODE_EDIT_OBJECT;
                }
                
                if ( objParent.getMode() == boObject.MODE_EDIT_TEMPLATE )
                {
                    edit = EDIT_TEMPLATE; 
                }
            }
            else  if ( objParent == null && mode != MODE_LIST_OBJECT_BRIDGE )
            {
                if ( format == boObjectList.FORMAT_MANY )
                {
                    mode = MODE_LIST_OBJECTS;    
                }
                else 
                {
                    if ( inEditTemplate ) 
                    {
                        edit = EDIT_TEMPLATE;
                    }
                }
            }
        }
        String searchMode=page.getRequest().getParameter("listmode");
       // boolean parentWin = (page.getRequest().getParameter("relatedClientIDX") == null 
       //                     || "".equals(page.getRequest().getParameter("relatedClientIDX")))
                            
        boolean parentWin= (page.getRequest().getParameter("ctxParentIdx") == null 
                            || "".equals(page.getRequest().getParameter("ctxParentIdx")));
                            
        if ( searchMode != null )
        {
            in_doc = IN_SEARCH_DOC;
        }
        
        Hashtable toRet[]=new Hashtable[0];
        
       
        if ( mode == MODE_EDIT_OBJECT &&  edit == EDIT_NORMAL && in_doc == IN_NORMAL_DOC )
        {
            toRet = menuEDIT_OBJECT_NORMAL(DOC, DOCLIST, obj, parentWin ) ;
        }
        else if ( mode == MODE_EDIT_OBJECT &&  edit == EDIT_TEMPLATE && in_doc == IN_NORMAL_DOC ) {
            toRet = menuEDIT_OBJECT_TEMPLATE( obj ) ;
        }
        else if ( mode == MODE_EDIT_OBJECT &&  edit == EDIT_OBJECT_TEMPLATE && in_doc == IN_NORMAL_DOC ) {
            toRet = menuEDIT_OBJECT_NORMAL(DOC, DOCLIST, obj, parentWin ) ;
        }
        else if ( mode == MODE_LIST_OBJECT_BRIDGE &&  edit == EDIT_NORMAL &&  in_doc == IN_NORMAL_DOC ) {
            toRet = menuLIST_NORMAL_BRIDGE( obj , objParent , bolist , DOC, DOCLIST , hui, getViewerInAction(page) ) ;
        }
        else if ( mode == MODE_LIST_OBJECT_BRIDGE &&  edit == EDIT_NORMAL &&  in_doc == IN_SEARCH_DOC  ) {
            toRet = menuLIST_NORMAL_BRIDGE_SEARCH( obj , objParent , bolist , DOC, DOCLIST , hui ) ;
            
        }
        else if ( mode == MODE_LIST_OBJECT_BRIDGE &&  edit == EDIT_TEMPLATE ) {
            //aqui
            //obj=DOC.getObject( templateBoui );
            
            //toRet = menuLIST_TEMPLATE_BRIDGE( obj ,templateBoui ,relatedClsAttribute, clsRelated , attributeRelated , relatedObjBOUI , bolist , DOC , hui) ;
            toRet = menuLIST_NORMAL_BRIDGE( obj , objParent , bolist , DOC, DOCLIST , hui ,getViewerInAction(page)) ;            
            
            
        }
        else if ( mode == MODE_LIST_OBJECT_BRIDGE &&  edit == EDIT_OBJECT_TEMPLATE && in_doc == IN_NORMAL_DOC ) {
            toRet = menuLIST_NORMAL_BRIDGE( obj , objParent , bolist , DOC, DOCLIST , hui ,getViewerInAction(page)) ;
        }
        else if ( mode == MODE_LIST_OBJECTS &&  edit == EDIT_NORMAL && in_doc == IN_NORMAL_DOC ) {
            toRet = menuLIST_NORMAL( obj , bolist , DOC, DOCLIST , hui ) ;
        }
        else if ( mode == MODE_LIST_OBJECTS &&  edit == EDIT_NORMAL && in_doc == IN_SEARCH_DOC  ) {
            toRet = menuLIST_SEARCH( obj , bolist , DOC, DOCLIST , hui , page ) ;
        }
        else if ( mode == MODE_LIST_OBJECTS &&  edit == EDIT_OBJECT_TEMPLATE && in_doc == IN_NORMAL_DOC ) {
            toRet = menuLIST_NORMAL( obj , bolist , DOC, DOCLIST , hui) ;
        }
        
        
        
        return toRet;
    }
    
 
    private static Hashtable[] menuEDIT_OBJECT_NORMAL (docHTML DOC, docHTML_controler DOCLIST,  boObject obj, boolean parentWin) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuBar=new Hashtable();
        Hashtable toRet[]=new Hashtable[2];
        
        byte menuNumber=0;
        
        
        String[][] opt =new String[3][];
        menuFlat.put("MENUFLAT",opt);
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.24");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), JSPMessages.getString("docHTML_builderMenus.25"), DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "DOCUMENTO", DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_DOCUMENTO";
    
        menuNumber++;
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.23");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Accoes", DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";


        menuNumber++;

        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.34");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Help", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Help", DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="J";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_AJUDA";

     //SUBMENU DOCUMENTO   
         menuNumber=0;
         String[][] opt2 =new String[20][];

        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.39");
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="N";
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_NEW";
        menuNumber++;


        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;
        boObject masterObj = obj;
        if("BasicController".equals(DOC.getController().getName()))        
        {
        
            
              if(!parentWin && !obj.getBoDefinition().getBoCanBeOrphan())
                masterObj = boObject.getBoManager().loadObject(DOC.getEboContext(), DOC.getMasterObject().getBoui());//  p_path.p_masterObjectBoui);
            
          if (securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
              && securityOPL.canWrite(masterObj)|| (!masterObj.exists() && securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
          {
        //    if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
        //          && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
         //   {
                if( masterObj.isEnabled  )
                {
                      opt2[menuNumber]=new String[5];
                      opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.45");
                      if("Ebo_Filter".equals(obj.getName()))
                      {
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(!this.document.getElementById('frIDQueryBuilder').contentWindow.oQB||this.document.getElementById('frIDQueryBuilder').contentWindow.oQB.verify()) {boForm.Save(); savePressed(true);}";
                      }
                      else
                      {
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Save(); savePressed(true);wait();";
                      }
                      opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                      opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
                      opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                      menuNumber++;
                }
            }
            
             if (securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
              && securityOPL.canWrite(masterObj)|| (!masterObj.exists() && securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
              {
//            if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
//                  && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {
                    if( masterObj.isEnabled )  
                    {
                        opt2[menuNumber]=new String[5];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.51");
                        if("Ebo_Filter".equals(obj.getName()))
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(!this.document.getElementById('frIDQueryBuilder').contentWindow.oQB||this.document.getElementById('frIDQueryBuilder').contentWindow.oQB.verify()) {boForm.SaveAndClose(); savePressed(true);}";
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveAndClose(); savePressed(true);wait();";
                        }
                        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
                        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                        menuNumber++;
                    }
            }
        }
        
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Duplicate",obj.getEboContext().getBoSession().getPerformerBoui())
            && obj.getBoDefinition().getBoCanBeOrphan()
        ) 
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.466");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.duplicate('" + obj.getName() + "', "+ obj.getBoui() + ");wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
       }

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"CreateTemplate",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
            boObject template = obj.getAttribute("TEMPLATE").getObject();
            if(template == null)
            {
              opt2[menuNumber]=new String[5];
              opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.66");
              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveAsTemplate()";
              opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
              opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
              opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
              menuNumber++;
            }
            else
            {
              opt2[menuNumber]=new String[5];
              opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.22");
              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.RemoveTemplate();wait();";
              opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
              opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
              opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
              menuNumber++;
                
            }
        }
 
        if("BasicController".equals(DOC.getController().getName()))        
        {       
            if (securityRights.hasRightsToMethod(obj,obj.getName(),"LaunchProgram",obj.getEboContext().getBoSession().getPerformerBoui()))        
            {
                String objName = obj.getBoDefinition().getName();
                if("xwfProgram".equals(objName))
                {         
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.21");
    //              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'','list','method=new&masterdoc=true&fromPathItem=&defProgramBoui=" + obj.getBoui() +"&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'"+"','','__xwfMainViewer.jsp' )";
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'','list','method=new&masterdoc=true&programMode=1&fromPathItem=&defProgramBoui=" + obj.getBoui() +"&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'"+"','','__xwfWorkPlace.jsp' )";
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="T";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;
                  
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.83");
    //              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'','list','method=new&masterdoc=true&fromPathItem=&defProgramBoui=" + obj.getBoui() +"&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'"+"','','__xwfMainViewer.jsp' )";
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'','list','method=new&masterdoc=true&fromPathItem=&defProgramBoui=" + obj.getBoui() +"&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'"+"','','__xwfWorkPlace.jsp' )";
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="P";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;
                }
                else
                {
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.89");
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'','list','method=new&inputObjectBoui=" + obj.getBoui() +"&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&docid='+getDocId()+'"+"','','__xwfProgramStartViewer.jsp' )";
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="P";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;                
                }
            }
        }
        
        //APLICAR INTERFACE
        if(obj.getBoDefinition().getBoInterfaces()!=null && obj.getBoDefinition().getBoInterfaces().length>0 && 
          securityRights.hasRightsToMethod(obj,obj.getName(),"ActivateInterface",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.96");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_INTERFACE";
            menuNumber++;
        }
        
        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Print",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          String  XeoWin32Client_address = obj.getEboContext().getXeoWin32Client_adress();
          boolean wordTemplate = boDefPrinterDefinitions.existsWordTemplatesFor(obj);
          if(XeoWin32Client_address == null || !wordTemplate)
          {
                if(!wordTemplate)
                {
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.102");
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Print()";          
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;                    
                }
                else
                {
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.106");
                  //String param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx();                 
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;//"boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";                   
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_IMPRIMIR";
                  menuNumber++;                                   
                }
          }                    
          else // Para alterar, não é necessário ter cliente 
          {
              opt2[menuNumber]=new String[5];
              opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.110");              
              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;//"boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printObject',['this'])";          
              opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
              opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
              opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_IMPRIMIR";
              menuNumber++;          
              
              opt2[menuNumber]=new String[5];
              opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.114");
              //String param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx();                 
              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;//"boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";                   
              opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
              opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="V";
              opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_VISUALIZAR";
              menuNumber++; 
              
              if(DocumentHelper.existsDocuments(obj))
              {
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.118");
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printAllDocuments',['this'])";
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;
              }
          }
        }

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"ExportObject",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.123");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('small','__choseExport.jsp','?docid='+getDocId()+'&exportBoui="+obj.getBoui()+"','lookup') ";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="O";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="E";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"ExportProgramDef",obj.getEboContext().getBoSession().getPerformerBoui()) &&
            "xwfProgramDef".equals(obj.getName()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.130");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('small','__exportProgramDef.jsp','?docid='+getDocId()+'&exportBoui="+obj.getBoui()+"&modeType=export','lookup') ";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="O";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="x";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
          
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.20");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('small','__exportProgramDef.jsp','?docid='+getDocId()+'&exportBoui="+obj.getBoui()+"&modeType=import','lookup');  ";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="O";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
          
        }


        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Properties",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.141");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.getProperties()";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
        if (obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
            && securityOPL.hasFullControl(obj))
        { 
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.144");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('0,670px,350px','add_keys_permissions.jsp','?docid='+getDocId()+'&method=edit&boui="+obj.getBoui()+"','std')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }               
        if ( obj.exists() && securityRights.hasRightsToMethod(obj,obj.getName(),"ReferencedBy",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.149");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__ebo_references.jsp','?referencedby=yes&boui="+obj.getBoui()+"','lookup')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
          
        }          
        if ( obj.exists() && securityRights.hasRightsToMethod(obj,obj.getName(),"References",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.154");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__ebo_references.jsp','?references=yes&boui="+obj.getBoui()+"','lookup')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
          
        }
        
        if ( obj.exists() && obj.getBridge("DAO") != null && securityRights.hasRightsToMethod(obj,obj.getName(),"Tree",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.19");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__runtimePathObjects.jsp','?method=edit&docid='+getDocId()+'&boui="+obj.getBoui()+"','lookup') ";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            menuNumber++;
            /*
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="Grafo";
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__runtimeGraphObjects.jsp','?method=edit&docid='+getDocId()+'&boui="+obj.getBoui()+"','lookup') ";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            menuNumber++;*/            
        }
        
        String[][] optSUBMENUDOCUMENTO = null;
        
        if("BasicController".equals(DOC.getController().getName()))        
        {
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            menuNumber++;
    
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.166");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Close()";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="F";
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
            optSUBMENUDOCUMENTO = new String[menuNumber+1][];    
            System.arraycopy(opt2,0,optSUBMENUDOCUMENTO,0,menuNumber+1);            
        }
        else
        {
            optSUBMENUDOCUMENTO = new String[menuNumber][];
            System.arraycopy(opt2,0,optSUBMENUDOCUMENTO,0,menuNumber);
        }

//        String[][] optSUBMENUDOCUMENTO = new String[menuNumber+1][];        
        menuFlat.put("SUBMENU_DOCUMENTO",optSUBMENUDOCUMENTO);

     //---------------------------------- 

     //SUBMENU NOVO   
        menuNumber=0;
        opt2 =new String[20][];
        
        if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))
        {
        
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=obj.getBoDefinition().getLabel();
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+obj.getBoDefinition().getName().toLowerCase()+"','edit','ctxParent="+obj.bo_boui+"&method=new&object="+obj.getBoDefinition().getName()+"')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
          
        boDefHandler[] subClasses=obj.getSubClasses();
        for (int i = 0; i < subClasses.length; i++)  {
            if ( i == 0 ){
                opt2[menuNumber]=new String[5];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                menuNumber++;        
            }
            if (securityRights.hasRights(obj,subClasses[i].getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))
            {            
              opt2[menuNumber]=new String[5];
              opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=subClasses[i].getLabel();
              opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+subClasses[i].getName().toLowerCase()+"','edit','ctxParent="+obj.bo_boui+"&method=new&object="+subClasses[i].getName()+"')";
              opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
              opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
              opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
              menuNumber++;
            }
            
        }
        
        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;
                
        if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.183");      
          StringBuffer onclick=new StringBuffer();
          onclick.append("winmain().openDocUrl('','__newFromTemplate.jsp','");
          onclick.append( "?objectName=");
          onclick.append( obj.getName() );
          onclick.append("','lookup');");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=onclick.toString();
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
        
        
        



        String[][] optSUBMENU_NOVO =new String[menuNumber][];
        System.arraycopy(opt2,0,optSUBMENU_NOVO,0,menuNumber);
        menuFlat.put("SUBMENU_NEW",optSUBMENU_NOVO);


        //SUBMENU INTERFACE
         menuNumber=0;
         opt2 =new String[20][];
        
        if(obj.getBoDefinition().getBoInterfaces() !=null)
        {
            boDefHandler[] interfs = obj.getBoDefinition().getBoInterfaces();
            for(int x=0; x<interfs.length;x++)
            {
                if (interfs[x].getInterfaceType() == boDefHandler.INTERFACE_OPTIONAL && securityRights.hasRights( obj.getEboContext() , interfs[x].getName()))
                {
                
                  opt2[menuNumber]=new String[5];
                  if(obj.getAttribute("implements_" + interfs[x].getName()).getValueString().equals("S"))
                  {
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="<b>"+interfs[x].getLabel()+"</b>";
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
                  }
                  else
                  {
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=interfs[x].getLabel();
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.applyInterface('"+interfs[x].getName()+"');";
                  }
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;
                }
            }
    
            String[][] optSUBMENU_INTERFACE =new String[menuNumber][];
            System.arraycopy(opt2,0,optSUBMENU_INTERFACE,0,menuNumber);
            menuFlat.put("SUBMENU_INTERFACE",optSUBMENU_INTERFACE);

        }


        //SUBMENU IMPRIMIR        
        if(boDefPrinterDefinitions.existsWordTemplatesFor(obj))
        {
            menuNumber=0;
            opt2 =new String[20][];
            String[][] opt3 =new String[20][];
            
            boDefPrinterDefinitions bodefPrinter = boDefPrinterDefinitions.loadPrinterDefinitions(obj.getName());
            int nWord = bodefPrinter.getNumberOfWordTemplates();
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.197");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Print()";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
            opt3[menuNumber]=new String[5];
            opt3[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.201");
            opt3[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=JSPMessages.getString("docHTML_builderMenus.202");
            opt3[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt3[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
            opt3[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
            menuNumber++;
              
            ArrayList l = bodefPrinter.getTemplateWord();
            String param = null;
            for (int i = 0; i < l.size(); i++) 
            {
                opt2[menuNumber]=new String[5];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=((TemplateWord)l.get(i)).getLabel();
                param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx()+"&templateName="+((TemplateWord)l.get(i)).getName();
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                
                opt3[menuNumber]=new String[5];
                opt3[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=((TemplateWord)l.get(i)).getLabel();
                param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx()+"&templateName="+((TemplateWord)l.get(i)).getName();
                opt3[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printObject',['this', '"+((TemplateWord)l.get(i)).getName()+"'])";
                opt3[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                opt3[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                opt3[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                menuNumber++;
              
            }
    
            String[][] optSUBMENU_IMPRIMIR =new String[menuNumber][];
            System.arraycopy(opt3,0,optSUBMENU_IMPRIMIR,0,menuNumber);
            menuFlat.put("SUBMENU_IMPRIMIR",optSUBMENU_IMPRIMIR);
            
            String[][] optSUBMENU_VISUALIZAR =new String[menuNumber][];
            System.arraycopy(opt2,0,optSUBMENU_VISUALIZAR,0,menuNumber);
            menuFlat.put("SUBMENU_VISUALIZAR",optSUBMENU_VISUALIZAR);            

            
            
        }


        //SUBMENU ACCOES   
       boDefMethod[] userMethods= obj.getMenuMethods();
       menuNumber=0;
       opt2 =new String[20][];
       
       for (int i=0; i< userMethods.length ; i++ ) 
       {
        if (securityRights.hasRightsToMethod(obj,obj.getName(),userMethods[i].getName(),obj.getEboContext().getBoSession().getPerformerBoui()))
        {  
          if(!obj.methodIsHidden(userMethods[i].getName()))
          {
            opt2[menuNumber]=new String[5];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=userMethods[i].getLabel();
            if(!userMethods[i].openDoc())
            {
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + userMethods[i].getName() + "'); setLastMethodToRun('" + userMethods[i].getName() + "'); boForm.executeMethod('"+userMethods[i].getName()+"');wait();";
            }
            else
            {
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + userMethods[i].getName() + "'); setLastMethodToRun('" + userMethods[i].getName() + "'); boForm.executeMethodOpenDoc('"+userMethods[i].getName()+"');";
            }            
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            menuNumber++;
          }
        }
        
       }
       //FALTAM SEGURANCAS !!!
       if ( obj.getStateManager() != null )
       {
           String meths[]=obj.getStateManager().getStateMethods( obj );
           if( meths!=null)
           {
                boDefMethod meth;
               for (int i = 0; i < meths.length ; i++) 
               {              
                  String xmth[]=meths[i].split(";");
                  opt2[menuNumber]=new String[5];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=xmth[0] ;
                  meth = obj.getBoDefinition().getBoMethod(removeObject(xmth[1]));
                  if(meth != null)
                  {
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + removeObject(xmth[1]) + "');setLastMethodToRun('" + removeObject(xmth[1]) + "'); boForm.executeMethod('"+xmth[1]+"');wait();";
                  }
                  else
                  {
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=" boForm.executeMethod('"+xmth[1]+"');wait();";
                  }                  
                  //opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="alert('1'); boForm.executeMethod('"+xmth[1]+"'); alert('2');";
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                  opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                  menuNumber++;
               }    
           }
       }

    if(menuNumber>0)
    {
         String[][] optSUBMENUACCOES =new String[menuNumber][];
         System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber);
         menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
    }

//------- MENU BAR
        menuNumber=0;
        opt2 =new String[20][];

        if(!obj.getBoDefinition().haveVersionControl() || 
           !obj.exists() ||  
           (obj.getBoDefinition().haveVersionControl() && obj.getObjectVersionControl().canCheckIn()))
        {                    
            if("BasicController".equals(DOC.getController().getName()))        
            {
                if( parentWin ) //obj.getParent() == null ) // || obj.getBoDefinition().getBoCanBeOrphan() )
                {            
                  if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                      && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {      
                    if( obj.isEnabled )                      
                    {
                        opt2[menuNumber]=new String[7];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.240");
                        if("Ebo_Filter".equals(obj.getName()))
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(!this.document.getElementById('frIDQueryBuilder').contentWindow.oQB||this.document.getElementById('frIDQueryBuilder').contentWindow.oQB.verify()) {boForm.Save(); savePressed(true);}";
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Save(); savePressed(true);wait();";
                        }
                        if(obj != null)
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Guardar", DOCLIST));
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Guardar", DOCLIST));
                        }
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                        opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_save.gif";
                        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.248");        
                        menuNumber++;
                    }
                  }
                  if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                        && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {
                        if( obj.isEnabled )
                        {
                            opt2[menuNumber]=new String[7];
                            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
                            if("Ebo_Filter".equals(obj.getName()))
                            {
                                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(this.document.getElementById('frIDQueryBuilder').contentWindow.oQB.verify()) {boForm.SaveAndNew();wait(); }";
                            }
                            else
                            {
                                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveAndNew(); savePressed(true);wait();";
                            }
                            if(obj != null)
                            {
                                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "SaveAndNew", DOCLIST));
                            }
                            else
                            {
                                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "SaveAndNew", DOCLIST));
                            }
                            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                            opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveNew.gif";
                            opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.257");            
                            menuNumber++;
                        }
                  }
                  if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                        && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {
                    if( obj.isEnabled )
                    {
                        opt2[menuNumber]=new String[7];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
                        if("Ebo_Filter".equals(obj.getName()))
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(this.document.getElementById('frIDQueryBuilder').contentWindow.oQB.verify()) {boForm.SaveAndClose(); savePressed(true);}";
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveAndClose(); savePressed(true);wait();";
                        }
                        if(obj != null)
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "SaveAndClose", DOCLIST));
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "SaveAndClose", DOCLIST));
                        }
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                        opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveClose.gif";
                        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.266");            
                        menuNumber++;
                    }
                  }
                }
                else
                {
                   if (securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                     && securityOPL.canWrite(masterObj)|| (!masterObj.exists() && securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
                  {
    //                 if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
    //                  && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
    //            {      
                    if( obj.isEnabled )
                    {
                        opt2[menuNumber]=new String[7];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.267");
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveMaster(); savePressed(true);wait();";
                        if(obj != null)
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Guardar", DOCLIST));
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Guardar", DOCLIST));
                        }
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                        opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_save.gif";
                        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.273");        
                        menuNumber++;
                    }
                  }
                   if (securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                  && securityOPL.canWrite(masterObj)|| (!masterObj.exists() && securityRights.hasRights(masterObj,masterObj.getName(),masterObj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
                    {
    //              if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
    //                    && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {
                    if( obj.isEnabled )    
                    {
                        opt2[menuNumber]=new String[7];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveMasterAndClose(); savePressed(true);wait();";
                        if(obj != null)
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "SaveAndClose", DOCLIST));
                        }
                        else
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "SaveAndClose", DOCLIST));
                        }
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                        opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveClose.gif";
                        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.280");            
                        menuNumber++;
                    }
                  }
                
        //          if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
        //              && securityOPL.canWrite(obj)) {
        //            opt2[menuNumber]=new String[7];
        //            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="Aplicar";
        //            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Save(true); savePressed(true); ";
        //            if(obj != null)
        //            {
        //                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Aplicar", DOCLIST));
        //            }
        //            else
        //            {
        //                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Aplicar", DOCLIST));
        //            }
        //            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
        //            opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveact.gif";
        //            opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Aplicar Documento";        
        //            menuNumber++;
        //          }
        //          if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
        //             && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) {
        //            opt2[menuNumber]=new String[7];
        //            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
        //            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.SaveAndClose(true); savePressed(true);";
        //            if(obj != null)
        //            {
        //                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "SaveAndApply", DOCLIST));
        //            }
        //            else
        //            {
        //                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "SaveAndApply", DOCLIST));
        //            }
        //            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="F";
        //            opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveactClose.gif";
        //            opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Fechar e Aplicar";            
        //            menuNumber++;            
        //          }
                }
                
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                menuNumber++;        
            }// != XwfController
        }
        
        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
//        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]= DOC.p_path.renderLinkForPriorPage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]= DOC.getController().getNavigator().renderLinkForPriorPage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="P";
        //opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.p_path.getImageLinkForPriorPage();
        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.getController().getNavigator().getImageLinkForPriorPage();
        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.284");
        menuNumber++;  
        
        
        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
//        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.p_path.renderLinkForNextPage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.getController().getNavigator().renderLinkForNextPage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="N";
//        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.p_path.getImageLinkForNextPage();
        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.getController().getNavigator().getImageLinkForNextPage();        
        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.287");
        menuNumber++;
        
        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
//        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.getController().getNavigator().p_path.renderLinkForCancelEdit().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.getController().getNavigator().renderLinkForCancelEdit().toString() ;        
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="X";
//        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.p_path.getImageLinkForCancelEdit();
        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.getController().getNavigator().getImageLinkForCancelEdit();        
        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.18");
        menuNumber++;
        
        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
//        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.p_path.renderLinkForHomePage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=DOC.getController().getNavigator().renderLinkForHomePage().toString() ;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
//        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.p_path.getImageLinkForHomePage();
        opt2[menuNumber][docHTMLgenerateToolBar.IMG]=DOC.getController().getNavigator().getImageLinkForHomePage();
        opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Inicio";
        menuNumber++;
        
        
        
        
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Print",obj.getEboContext().getBoSession().getPerformerBoui()) && securityOPL.canWrite(obj)) 
        {        
              String  XeoWin32Client_address = obj.getEboContext().getXeoWin32Client_adress();
              boolean wordTemplate = boDefPrinterDefinitions.existsWordTemplatesFor(obj);
              if(XeoWin32Client_address == null || !wordTemplate)
              {
                 if(!wordTemplate)
                 {
                      opt2[menuNumber]=new String[7];
                      opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.106");
                      opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Print();";
                      if(obj != null)
                      {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Imprimir", DOCLIST));
                      }
                      else
                      {
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, "Imprimir", DOCLIST));
                      }
                      opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
                      opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif";
                      opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.118");
                      menuNumber++;           
                 }
                 else
                  {
                      opt2[menuNumber]=new String[7];
                      opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.106");                  
                      String param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx();                 
                      opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";                   
                      opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                      opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                      opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif"; 
                      opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.310");
                      menuNumber++;                                     
                  }                 
              }
              else
              {
                  opt2[menuNumber]=new String[7];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.106");              
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printObject',['this'])";          
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                  opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif";                  
                  opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.316");
                  menuNumber++;          
                  
                  opt2[menuNumber]=new String[7];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.317");                  
                  String param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx();                 
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";                   
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="V";
                  opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif"; 
                  opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.325");
                  menuNumber++;                   
                                                  
              }

        }

        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;       
        
        //se ele implementa stateControl não pode ser apagado
        boolean delete = true;
        if(obj.getStateManager() != null)
        {
            delete = obj.getStateManager().getCanRemove(obj);
        }
        
        if (obj.exists() && 
            delete && 
            !"XwfController".equals(DOC.getController().getName()) &&
            (!obj.getBoDefinition().haveVersionControl() || (obj.getBoDefinition().haveVersionControl() && obj.getObjectVersionControl().canCheckIn())))
        {
            if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.DELETE)
                && securityOPL.canDelete(obj)) {
            if( obj.isEnabled )
            {
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.17");            
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="if(boForm.Destroy('" + getRemoveMessage(obj.getBoDefinition().getLabel(), getCardId(obj.getBoDefinition().getCARDID(), obj)) + "')){wait();}";
                
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Remover", DOCLIST));
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
                opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_delete.gif";
                opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.334")+obj.getBoDefinition().getLabel();
                menuNumber++;            
            }
          }
        }
        if (obj.exists() &&  boConfig.aspmodeOn()
            && securityRights.hasRightsToMethod(obj,obj.getName(),"CreateSchema",obj.getEboContext().getBoSession().getPerformerBoui())
            && obj.getBoDefinition().getASPMode() == boDefHandler.ASP_CONTROLLER 
            && !SchemaUtils.existSchema(obj.getEboContext(), obj.getName() + "_" + obj.getBoui())
        ) {
          opt2[menuNumber]=new String[7];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="Criar Esquema";
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.createSchema(); wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "createSchema", DOCLIST));
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="S";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          
          opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_schema.gif";
          opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Criação do esquema "+ obj.getName() + "_" + obj.getBoui();
          
          menuNumber++;
       }
       
        if(obj.exists() && obj.getBoDefinition().haveVersionControl())
        {
            opt2[menuNumber]=new String[7];
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            menuNumber++;                        
            
            
            if(obj.getObjectVersionControl().canCheckOut())
            {            
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.345");            
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]= "runBeforeMethodExec('checkOut'); setLastMethodToRun('checkOut'); boForm.executeMethod('checkOut');wait();";                        
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Remover", DOCLIST));
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
                opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_delete.gif";
                opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.350")+obj.getBoDefinition().getLabel();
                menuNumber++;
            }
            
            
            if(obj.getObjectVersionControl().canUndoCheckOut())
            {
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.16");
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]= "runBeforeMethodExec('undoCheckOut'); setLastMethodToRun('undoCheckOut'); boForm.executeMethod('undoCheckOut');wait();";                                    
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Remover", DOCLIST));
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
                opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_delete.gif";
                opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.356")+obj.getBoDefinition().getLabel();
                menuNumber++;        
            }
            
            if(obj.getObjectVersionControl().canCheckIn())
            {
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.357");
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]= "runBeforeMethodExec('checkIn'); setLastMethodToRun('checkIn'); boForm.executeMethod('checkIn');wait();";                        
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), JSPMessages.getString("docHTML_builderMenus.359"), DOCLIST));
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
                opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_delete.gif";
                opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Reservar "+obj.getBoDefinition().getLabel();
                menuNumber++;
            }
        }
       
       
       
       //boDefMethod[] userMethods= obj.getMenuMethods();
       userMethods= obj.getToolbarMethods();
       //se for xwfController os userMethods são controlados pelo wkflw
       if(!"xwfController".equalsIgnoreCase(obj.getEboContext().getController().getName()))
       {
           for (int i=0; i< userMethods.length ; i++ ) {
           
               if (securityRights.hasRightsToMethod(obj,obj.getName(),userMethods[i].getName(),obj.getEboContext().getBoSession().getPerformerBoui())) {            
                  
                  if(!obj.methodIsHidden(userMethods[i].getName()))
                  {
                    opt2[menuNumber]=new String[7];
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=userMethods[i].getLabel();
                    if(!userMethods[i].openDoc())
                    {
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + userMethods[i].getName() +  "'); setLastMethodToRun('" + userMethods[i].getName() + "'); boForm.executeMethod('"+userMethods[i].getName()+"');wait();";
                    }
                    else
                    {
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + userMethods[i].getName() +  "'); setLastMethodToRun('" + userMethods[i].getName() + "'); boForm.executeMethodOpenDoc('"+ userMethods[i].getObjectName() +"', '"+ obj.getBoui() +"', '" + userMethods[i].getName() + "');";
                    }
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), userMethods[i].getName(), DOCLIST));
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_method.gif";
                    opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=userMethods[i].getLabel();
                    menuNumber++;
                  }
              }
           }
       }
       
       if ( obj.getStateManager() != null )
       {
           String meths[]=obj.getStateManager().getStateMethods( obj );
           if( meths!=null)
           {
               boDefMethod meth; 
               for (int i = 0; i < meths.length ; i++) 
               {
                String xmth[]=meths[i].split(";");
                opt2[menuNumber]=new String[7];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=xmth[0] ;
                meth = obj.getBoDefinition().getBoMethod(removeObject(xmth[1]));
                if(meth != null)
                {
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="runBeforeMethodExec('" + removeObject(xmth[1]) +  "'); setLastMethodToRun('" + removeObject(xmth[1]) + "'); boForm.executeMethod('"+xmth[1]+"');wait();";
                }
                else
                {
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=" boForm.executeMethod('"+xmth[1]+"');wait();";
                }             
                //opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="alert('1'); boForm.executeMethod('"+xmth[1]+"'); alert('2');";
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(DOC.getTabindex(DOC.MENU, obj.getName(), String.valueOf(obj.getBoui()), xmth[0], DOCLIST));
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_method.gif";
                opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=xmth[0];              
                menuNumber++;
               }    
           }
       }
       
                
     if(menuNumber>0){
         String[][] optBAR =new String[menuNumber][];
         System.arraycopy(opt2,0,optBAR,0,menuNumber);
         menuBar.put("MENUBAR",optBAR);
    }   
        
        
    if("XwfController".equals(DOC.getEboContext().getController().getName()))
    {
        menuFlat.put("MENUFLAT",new String[0][0]);
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
    }
    else
    {
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
    }
    return toRet;
        

    }
    
    
    
 public static String menuWEBFORM_OBJECT_NORMAL (docHTML DOC, docHTML_controler DOCLIST,  boObject obj, HttpServletRequest request,String toSay ) throws boRuntimeException
    {
        String toRet = "";     
        
        
        boolean parentWin= ( request.getParameter("ctxParentIdx") == null 
                            || "".equals( request.getParameter("ctxParentIdx")));


            if( parentWin ) //obj.getParent() == null ) // || obj.getBoDefinition().getBoCanBeOrphan() )
            {
              if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                  && securityOPL.canWrite(obj)|| (!obj.exists() && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD))) 
              {      
                toRet+="<button onclick='boForm.SaveAndClose()'>"+toSay+"</button>";
              }
            }
            else
            {
                
                
             String onclick =  DOC.getController().getNavigator().renderLinkForPriorPage().toString();
            toRet+="<button style='margin:4px' onclick=\""+onclick+JSPMessages.getString("docHTML_builderMenus.393");  
                
             onclick = DOC.getController().getNavigator().renderLinkForCancelEdit().toString() ;        
        //opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Cancelar últimas alterações efectuadas e voltar para o ecran anterior";
        //menuNumber++;            
            toRet+="<button style='margin:4px' onclick=\""+onclick+JSPMessages.getString("docHTML_builderMenus.15");
            }
            
        
        
        
        
        
        
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Print",obj.getEboContext().getBoSession().getPerformerBoui()) && securityOPL.canWrite(obj)) 
        {        
             /*
                  opt2[menuNumber]=new String[7];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="<u>I</u>mprimir";              
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.executeStaticMeth('netgest.bo.impl.document.print.PrintHelper.printObject',['this'])";          
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="I";
                  opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif";                  
                  opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Imprimir Documento";
                  menuNumber++;          
                  
                  opt2[menuNumber]=new String[7];
                  opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="<u>V</u>isualizar";                  
                  String param = "look_parentBoui=" + obj.getBoui() + "&att_display=y&docid=" + DOC.getDocIdx();                 
                  opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.BindExt();window.open('__document_preview.jsp?"+param+"')";                   
                  opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                  opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="V";
                  opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif"; 
                  opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Visualizar Documento";
                  menuNumber++;                   
                                                  
              */

        }

       
    
    return toRet;
        

    }

    private static String removeObject(String meth)
    {
        if(meth.startsWith("object."))
        {   
            int end = meth.indexOf(":", 0);
            return meth.substring(7, end == -1 ? meth.length(): end);
        }
        return meth;
    }

private static Hashtable[] menuEDIT_OBJECT_TEMPLATE ( boObject obj) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuBar=new Hashtable();
        Hashtable toRet[]=new Hashtable[2];
        
        byte menuNumber=0;
        
        
        String[][] opt =new String[2][];
        menuFlat.put("MENUFLAT",opt);
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.400");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_DOCUMENTO";
    
        menuNumber++;
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.404");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="J";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_AJUDA";

     //SUBMENU DOCUMENTO   
         menuNumber=0;
         String[][] opt2 =new String[20][];

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"CreateTemplate",obj.getEboContext().getBoSession().getPerformerBoui())
            && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
            && securityOPL.canWrite(obj)) {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.14");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="saveTemplateForm();wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
  
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"CreateTemplate",obj.getEboContext().getBoSession().getPerformerBoui())
            && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
            && securityOPL.canWrite(obj)) {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.414");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="saveTemplateForm(true);wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
  
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Duplicate",obj.getEboContext().getBoSession().getPerformerBoui())
            && obj.getBoDefinition().getBoCanBeOrphan()
        ) {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.419");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.duplicateTemplate('" + obj.getName() + "', "+ obj.getBoui() + ")";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
      
        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Print",obj.getEboContext().getBoSession().getPerformerBoui())) {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.427");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Print()";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="G";
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Properties",obj.getEboContext().getBoSession().getPerformerBoui())) {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.432");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="templateForm().getProperties()";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"ReferencedBy",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.436");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__ebo_references.jsp','?referencedby=yes&boui="+obj.getBoui()+"','lookup')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"References",obj.getEboContext().getBoSession().getPerformerBoui()))
        {
          opt2[menuNumber]=new String[5];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.441");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDocUrl('medium','__ebo_references.jsp','?references=yes&boui="+obj.getBoui()+"','lookup')";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
          opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
          menuNumber++;
        }
        

        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;

        opt2[menuNumber]=new String[5];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.446");
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Close()";
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="F";
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;


        String[][] optSUBMENUDOCUMENTO =new String[menuNumber+1][];
        System.arraycopy(opt2,0,optSUBMENUDOCUMENTO,0,menuNumber+1);
        menuFlat.put("SUBMENU_DOCUMENTO",optSUBMENUDOCUMENTO);

     

     
//------- MENU BAR
        menuNumber=0;
        opt2 =new String[20][];
        
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"CreateTemplate",obj.getEboContext().getBoSession().getPerformerBoui())
            && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
            && securityOPL.canWrite(obj)) {
          opt2[menuNumber]=new String[7];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.452");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="saveTemplateForm();wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
          opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_save.gif";
          opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.457");        
          menuNumber++;
        }

        if (securityRights.hasRightsToMethod(obj,obj.getName(),"CreateTemplate",obj.getEboContext().getBoSession().getPerformerBoui())
            && securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
            && securityOPL.canWrite(obj)) {
          opt2[menuNumber]=new String[7];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="";
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="saveTemplateForm(true);wait();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
          opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_saveClose.gif";
          opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.464");
          menuNumber++;
        }
        
        opt2[menuNumber]=new String[7];
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="SPACER";
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        menuNumber++;        
        
        if (securityRights.hasRightsToMethod(obj,obj.getName(),"Print",obj.getEboContext().getBoSession().getPerformerBoui()))
        {  
          opt2[menuNumber]=new String[7];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.13");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="boForm.Print()" ;
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="D";
          opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_print.gif";
          opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.472");
          menuNumber++;
        }

        if (securityRights.hasRights(obj,obj.getName(),obj.getEboContext().getBoSession().getPerformerBoui(),securityRights.DELETE)
            && securityOPL.canDelete(obj)) {        
          opt2[menuNumber]=new String[7];
          opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.473");
          opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="removeTemplate();";
          opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="1";
          opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="R";
          opt2[menuNumber][docHTMLgenerateToolBar.IMG]="16_delete.gif";
          opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.478");
        }
        
        
     if(menuNumber>0){
         String[][] optBAR =new String[menuNumber+1][];
         System.arraycopy(opt2,0,optBAR,0,menuNumber+1);
         menuBar.put("MENUBAR",optBAR);
    }   
        
        
    toRet[0]=menuFlat;
    toRet[1]=menuBar;
    return toRet;
        

    }




//FCAMARA: ATENÇÃO HOUVE MARTELADA COM O EBO_PERF PARA NÃO APARACER O ADICIONAR
private  static Hashtable[] menuLIST_NORMAL_BRIDGE( boObject obj, boObject objParent , boObjectList bolist, docHTML doc, docHTML_controler DOCLIST, int hui, String viewer) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuFlatRight=new Hashtable();
        Hashtable menuBar=null;
        Hashtable toRet[]=new Hashtable[3];
        byte menuNumber=0;
        String[][] opt;
        String bridgeFilter = null;
        boolean suportManualAdd = true;
        boolean suportManualCreate = true;
        boolean suportShowLookup = false;
                
        String objName = bolist.getBoDef().getName();       
        if(objParent != null && bolist != null){
            
            boDefHandler def = objParent.getBoDefinition();
            if(def != null){
                boDefAttribute attr = def.getAttributeRef(bolist.getParentAtributeName());
                if(attr != null){
                    suportManualAdd = attr.supportManualAdd();
                    suportManualCreate = attr.supportManualCreate();
                    suportShowLookup = attr.getShowLookup();
                    bridgeFilter = attr.getBridgeFilter();
                    boolean canAlter = 
                            !objParent.getBridge(bolist.getParentAtributeName()).disableWhen();
                    if(canAlter)
                    {
                        if(obj != null && !obj.isEnabledforRequest)
                        {
                            canAlter = false;
                        }
                    }
                    boolean canWrite=
                            securityRights.hasRights(bolist.getEboContext(),objParent.getName(),securityRights.WRITE)
                            && securityRights.hasRights(bolist.getEboContext(),objParent.getName(),bolist.getParentAtributeName(),securityRights.WRITE)
                            && securityOPL.canWrite(objParent);
                            
                    if((!canAlter || !canWrite) || !objParent.isEnabledforRequest){ 
                        
                        opt =new String[1][];
                        menuFlat.put("MENUFLAT",opt);
                        opt[menuNumber]=new String[5];
                        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.481");
                        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
                        if(obj != null)
                        {
                            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
                        }
                        else
                        {
                            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, "Accoes", DOCLIST));
                        }
                        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
                        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";
                        
                        String[][] opt2 =new String[20][];
                        menuNumber=0;
                        opt2[menuNumber]=new String[5];

                      //bolist
                        if(!bolist.isReadOnly() && canAlter && canWrite)
                        {
                            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.12");
                            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
                            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                            
                    
                             //if(menuNumber>0){
                            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
                            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
                            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
                        }
                        else
                        {
                            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
                        }
                
         
       
                        toRet[0]=menuFlat;
                        toRet[1]=menuBar;
                        toRet[2]=menuFlatRight;
                        return toRet;
                    }                    
                }
            }
        }
        
        if(bridgeFilter != null)
        {
            int grau = countParent(bridgeFilter, "parent");
            int nAttrib = countAttribute(bridgeFilter, grau);
            boObject sourceParent = getSourceObject(bridgeFilter, objParent, grau, nAttrib);
            return menuLIST_BRIDGE_TO_BRIDGE(obj, objParent , bolist, doc, DOCLIST, hui, sourceParent, bridgeFilter);
        }
        
        
        if ( !objName.equalsIgnoreCase("boObject") )
        {
            opt =new String[1][];
        }
        else
        {
           opt =new String[2][];
        }
        
        menuFlat.put("MENUFLAT",opt);
        

        bridgeHandler bridge= (bridgeHandler) bolist;        
        boDefMethod[] xmth=bridge.getDefAttribute().getMethods();
        //xmth[0].getLabel();
        
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.495");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.getName(), String.valueOf(objParent.getBoui()), "Accoes", DOCLIST));
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";
        
        

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];

        //bolist
        if(!bolist.isReadOnly() && securityRights.hasRights(bolist.getEboContext(),objName) && 
        securityRights.hasRights(bolist.getEboContext(),objName,securityRights.DELETE))
        {
                        
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.499");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
            if ( xmth!=null)
            {
                for (int i = 0; i < xmth.length ; i++) 
                {
                   if ( 
                    (xmth[i].templateMode() && objParent.getMode()== boObject.MODE_EDIT_TEMPLATE)
                     ||
                    (!xmth[i].templateMode() && objParent.getMode()!= boObject.MODE_EDIT_TEMPLATE) 
                   )
                   {
                    menuNumber++;
                    opt2[menuNumber]=new String[5];
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=xmth[i].getLabel();
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="executeBridgeMeth("+hui+",'"+bridge.getAttributeName()+"','"+xmth[i].getName()+"')";
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                   }
                    
                }
                
            }
             //if(menuNumber>0){
            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
            
        }
        else
        {
            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
        }
                        

        
         if (suportManualAdd && objName.equalsIgnoreCase("boObject") )
         {
            menuNumber=1;
            opt[menuNumber]=new String[5];
            opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.511");
            opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="C";
            opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_CRIAR";
        
            boDefAttribute xatt         = objParent.getBoDefinition().getAttributeRef( bolist.getParentAtributeName() );
            boDefHandler[] xdefs=xatt != null ? xatt.getObjects():null;
                        
            String[][] opt2r =new String[30][];
            int menuNumber2=0;
              
            if ( xdefs != null)
            {
                for ( int i=0 ; i< xdefs.length ; i++ )
                {
                    if
                    (
                      !"guiding".equals(viewer) 
                      && (
                            securityRights.hasRights(bolist.getEboContext(),xdefs[i].getName(),securityRights.ADD)
                            && 
                            (
                                objParent.isEnabledforRequest 
                                && 
                                securityRights.canWrite(bolist.getEboContext(),bolist.getBoDef().getModifyProtocol())
                            )
                         )
                    )
                    {
                        opt2r[menuNumber2]=new String[5];
                        opt2r[menuNumber2][docHTMLgenerateToolBar.TEXT_MENU]= xdefs[i].getLabel();
                        opt2r[menuNumber2][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage(getIDX() ,'"+xdefs[i].getName().toLowerCase()+"','edit','method=new&controllerName=DOC.getController().getName()&relatedClientIDX='+getIDX()+'&docid='+getDocId()+'&ctxParentIdx='+getDocId()+'&ctxParent="+objParent.bo_boui+"&object="+xdefs[i].getName()+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"');";
                        opt2r[menuNumber2][docHTMLgenerateToolBar.TABINDEX]="0";
                        opt2r[menuNumber2][docHTMLgenerateToolBar.ACCESSKEY]=null;
                        opt2r[menuNumber2][docHTMLgenerateToolBar.SUBMENU]=null;
                        menuNumber2++;
                    }                     
                    else
                    {
                        if
                        (
                          ("Res_Master".equalsIgnoreCase(xdefs[i].getName()) || "Res_Guiding".equalsIgnoreCase(xdefs[i].getName())) 
                          &&
                          securityRights.hasRights(bolist.getEboContext(), xdefs[i].getName() ,securityRights.ADD)
                        )
                        {
                            opt2r[menuNumber2]=new String[5];
                            opt2r[menuNumber2][docHTMLgenerateToolBar.TEXT_MENU]= xdefs[i].getLabel();
                            opt2r[menuNumber2][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'"+xdefs[i].getName().toLowerCase()+"','edit','method=new&relatedClientIDX='+getIDX()+'&docid='+getDocId()+'&ctxParentIdx='+getDocId()+'&ctxParent="+objParent.bo_boui+"&object="+xdefs[i].getName()+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"');";
                            opt2r[menuNumber2][docHTMLgenerateToolBar.TABINDEX]="0";
                            opt2r[menuNumber2][docHTMLgenerateToolBar.ACCESSKEY]=null;
                            opt2r[menuNumber2][docHTMLgenerateToolBar.SUBMENU]=null;
                            menuNumber2++;                        
                        }
                    }
                }
                
            }
            String[][] optSUBMENUCRIAR =new String[menuNumber2][];
            System.arraycopy(opt2r,0,optSUBMENUCRIAR,0,menuNumber2);
            menuFlat.put("SUBMENU_CRIAR",optSUBMENUCRIAR);
            
        }

        boolean flat = false;
        boolean showAddinNoOrphan= suportShowLookup && suportManualAdd && !bolist.getBoDef().getBoCanBeOrphan();
        if ( (suportManualAdd||suportManualCreate) && !objName.equalsIgnoreCase("boObject") && !"Ebo_Perf".equals(objName) && !"Ebo_PerfAnacom".equals(objName))
        {
            if (
                  (bolist.getBoDef().getBoCanBeOrphan() || bolist.getBoDef().getBoHaveMultiParent()
                  || showAddinNoOrphan)
                  && securityRights.hasRights(bolist.getEboContext() , objName , securityRights.ADD)
               )
               {
            	if( (suportManualAdd && suportManualCreate) || showAddinNoOrphan) {
            		opt =new String[2][];
            	} else {
            		opt =new String[1][];
            	}
                menuFlatRight.put("MENUFLATRIGHT",opt);
            }
            else
            {                    
                    if( bolist.getBoDef().getInterfaceType()!=boDefHandler.INTERFACE_STANDARD )
                    {
                        opt =new String[1][];
                        menuFlatRight.put("MENUFLATRIGHT",opt);
                    }
                    else
                    {
                        opt =new String[2][];
                        opt[0] = ((String[][])menuFlat.get("MENUFLAT"))[0];
                        menuFlat.put("MENUFLAT",opt);
                        flat = true;
                    }
                }
        }
        else if(suportManualAdd||suportManualCreate)
        {
            opt =new String[1][];
            menuFlatRight.put("MENUFLATRIGHT",opt);
        }
        
        menuNumber=0;
        if ( suportManualCreate && 
            !objName.equalsIgnoreCase("boObject") && !"Ebo_Perf".equals(objName) && !"Ebo_PerfAnacom".equals(objName)
            && !bolist.isReadOnly() 
            && securityRights.hasRights(bolist.getEboContext(), objName ,securityRights.ADD)
            )
        {
            if( bolist.getBoDef().getInterfaceType()!=boDefHandler.INTERFACE_STANDARD )
            {
                opt[menuNumber]=new String[7];
                
                if ( doc.p_WebForm )
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.11");
                }
                else
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.10")+bolist.getBoDef().getLabel();
                }
                if(bolist.getBoDef().getBoCanBeOrphan())
                {
                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage( getIDX() ,'"+objName.toLowerCase()+"','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&ctxParent="+objParent.bo_boui+"&object="+objName+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"&docid='+getDocId()+'"+"');";
                }    
                else
                {
                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().newPage(getIDX(),'"+objName.toLowerCase()+"','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&ctxParent="+objParent.bo_boui+"&object="+objName+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"&docid='+getDocId()+'"+"');";
                }
                opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+objName+"/ico16.gif" ;
                
                opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.559")+bolist.getBoDef().getLabel()+JSPMessages.getString("docHTML_builderMenus.560") ;
            }
            else
            {
                if(flat)
                {
                    menuNumber=1;
                }
                opt[menuNumber]=new String[7];
                opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.9")+bolist.getBoDef().getLabel();
                opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_CRIAR";
                opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
                if(obj != null)
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Criar", DOCLIST));
                }
                else
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
                }
                opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="c";

                menuNumber=0;
                boDefInterface bodI = boDefHandler.getInterfaceDefinition(objName);                
                String implObjs[] = bodI.getImplObjects();
                opt2 =new String[implObjs.length][7];
                String searchMode=doc.getController().getRequest().getParameter("listmode");                
                String option="";
                if ( "searchone".equalsIgnoreCase(searchMode) )
                {
                    option+="&searchClientIdx='+getIDX()+'";
                }
                for (int i = 0; i < implObjs.length; i++) 
                {
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="Criar "+boDefHandler.getBoDefinition(implObjs[i]).getLabel();
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+implObjs[i].toLowerCase()+"','edit','method=new&object="+implObjs[i]+option+"');";
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+implObjs[i]+"/ico16.gif" ;
                    opt2[menuNumber][docHTMLgenerateToolBar.TITLE]="Criar novo objecto do tipo "+boDefHandler.getBoDefinition(implObjs[i]).getLabel()+JSPMessages.getString("docHTML_builderMenus.579") ;
                    menuNumber++;
                }
                if(flat)
                {
                    menuFlat.put("SUBMENU_CRIAR",opt2);
                }
                else
                {
                    menuFlatRight.put("SUBMENU_CRIAR",opt2);
                }
            }
        }
                
        if(suportManualAdd &&  (bolist.getBoDef().getBoCanBeOrphan() || bolist.getBoDef().getBoHaveMultiParent() || showAddinNoOrphan)  && !bolist.isReadOnly()
          && securityRights.hasRights(bolist.getEboContext(),objName) )
        {
            menuNumber = 0;
            if ( !objName.equalsIgnoreCase("boObject") && !"Ebo_Perf".equals(objName) && !"Ebo_PerfAnacom".equals(objName))
            {
              if (opt[0]!=null) menuNumber++;
            }                        
            opt[menuNumber]=new String[7];
            
            if (bolist.getBoDef().getLabel() != null && !"boObject".equals( bolist.getBoDef().getName() ) )
            {
                opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.8")+bolist.getBoDef().getLabel();
            }
            else
            {
                opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.7");
            }
            StringBuffer onclick=new StringBuffer();
            onclick.append("LookupObjects('','multi','");
            onclick.append( objName );
            onclick.append("','");
            onclick.append( bolist.getParent().getName() );
            onclick.append("','");
            onclick.append( bolist.getParent().bo_boui );
            onclick.append("','");
            onclick.append( bolist.getParentAtributeName() );
            onclick.append("','1')");
        
            //onclick.append("','1','");
            //onclick.append( doc.getDocIdx() );
            //onclick.append("');");

        
            //LookupObjects(lookupField, lookupStyle, lookupObject, parentObj , parentBoui, parentAttribute , showNew, docid)
        
        
            opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=onclick.toString();

        
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+objName+"/ico16.gif" ;
            opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.596");//onclick.toString().replace('"',' ').replace('\'',' ');
            //"Adicionar objectos já criados do tipo "+bolist.getBoDef().getLabel() ;
            
        }
        if(bolist.isReadOnly() || !(suportManualAdd||suportManualCreate))
        {
            opt =new String[0][];
            menuFlatRight.put("MENUFLATRIGHT",opt);
        }
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;

    return toRet;

}

private  static Hashtable[] menuLIST_NORMAL_BRIDGE_SEARCH( boObject obj, boObject objParent , boObjectList bolist, docHTML doc, docHTML_controler DOCLIST, int hui) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuFlatRight=new Hashtable();
        Hashtable menuBar=null;
        Hashtable toRet[]=new Hashtable[3];
        byte menuNumber=0;

        String[][] opt =new String[1][];
        
        menuFlat.put("MENUFLAT",opt);
        
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="<u>A</u>cções";
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if( obj != null )
        { 
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];

         //bolist
        if(!bolist.isReadOnly())
        {
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.603");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
    
             //if(menuNumber>0){
            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
        }
        else
        {
            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
        }


        
        opt =new String[0][]; 
        
        menuFlatRight.put("MENUFLATRIGHT",opt);
           
       
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;

    return toRet;

}


private  static Hashtable[] menuLIST_TEMPLATE_BRIDGE( boObject obj,
        long templateBoui ,
        String relatedClsAttribute, 
        String clsRelated ,
        String attributeRelated ,
        long relatedObjBoui ,
        boObjectList bolist, docHTML doc , docHTML_controler DOCLIST, int hui) throws boRuntimeException{

//menuLIST_TEMPLATE_BRIDGE( obj , clsRelated , attributeRelated , relatedObjBoui , bolist , DOC ) ;

        Hashtable menuFlat=new Hashtable();
        Hashtable menuFlatRight=new Hashtable();
        Hashtable menuBar=null;
        Hashtable toRet[]=new Hashtable[3];
        byte menuNumber=0;

        String[][] opt =new String[1][];
        
        menuFlat.put("MENUFLAT",opt);
        
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.611");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, "Accoes", DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];

         //bolist
        if(!bolist.isReadOnly())
        {
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.616");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
    
             //if(menuNumber>0){
            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
        }
        else
        {
            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
        }



        opt =new String[2][];
        menuNumber=0;
        menuFlatRight.put("MENUFLATRIGHT",opt);
        opt[menuNumber]=new String[7];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.623")+boDefHandler.getBoDefinition(relatedClsAttribute ).getLabel();
        //opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+obj.getBoDefinition().getName()+"','edit','method=new&ctxParent="+objParent.bo_boui+"&object="+obj.getBoDefinition().getName()+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"');";
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="alert('ja la vamos')";
        opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+relatedClsAttribute+"/ico16.gif" ;
        opt[menuNumber][docHTMLgenerateToolBar.TITLE]="" ;
        
       
        menuNumber++;
        opt[menuNumber]=new String[7];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.6")+boDefHandler.getBoDefinition(relatedClsAttribute ).getLabel();
        StringBuffer onclick=new StringBuffer();
        onclick.append("LookupTemplates('lookupmultiTemplates.jsp','");
        onclick.append( templateBoui );
        onclick.append("','");
        onclick.append( clsRelated );
        onclick.append("','");
        onclick.append( relatedClsAttribute );
        onclick.append("','");
        onclick.append( attributeRelated );
        onclick.append("','");
        onclick.append( relatedObjBoui );
        onclick.append("','1')");
        //onclick.append( doc.getDocIdx() );
       // onclick.append("');");
        
       // LookupTemplates(lookupJSP, templateBoui , clsTemplate  , attributeName , relatedBoui , showNew, docid)
        
        
        
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=onclick.toString();

        
        opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+relatedClsAttribute+"/ico16.gif" ;
        opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.5");
        //onclick.toString().replace('"',' ').replace('\'',' ');
        //"Adicionar objectos já criados do tipo "+bolist.getBoDef().getLabel() ;
            
       
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;

    return toRet;

}

private  static Hashtable[] menuLIST_NORMAL( boObject obj, boObjectList bolist, docHTML doc, docHTML_controler DOCLIST, int hui) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuBar=null;
        
        Hashtable menuFlatRight=new Hashtable();
        Hashtable toRet[]=new Hashtable[3];
        
        byte menuNumber=0;

        String[][] opt =new String[1][];
        
        menuFlat.put("MENUFLAT",opt);
        
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.4");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];

         //bolist
        if(!bolist.isReadOnly())
        {
            if(bolist.getBoDef() instanceof boDefInterface)
            {
                boDefInterface def = (boDefInterface)bolist.getBoDef();
                if( def.getInterfaceType() == boDefHandler.INTERFACE_STANDARD )
                {
                    int size = 0;
                    if(def.getImplObjects() != null)
                    {
                        size = def.getImplObjects().length;
                    }
                    opt2 =new String[size + 1][];
                    String[] objs = def.getImplObjects();
                    boDefHandler objDef;
                    for (int i = 0; i < objs.length; i++) 
                    { 
                        objDef = boDefHandler.getBoDefinition(objs[i]);
                        opt2[menuNumber]=new String[6];
                        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]="Criar " + objDef.getLabel();
                        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+objs[i].toLowerCase()+"','edit','method=new&object="+objs[i]+"');";
                        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                        opt2[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+objs[i]+"/ico16.gif" ;;
                        menuNumber++;
                    }
                    opt2[menuNumber]=new String[5];
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.652");
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
    
                }
                else
                {
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.656");
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
                }
            }
            else
            {
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.660");
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            }
             //if(menuNumber>0){
            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
        }
        else
        {
            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
        }
        
        if ( ! bolist.getBoDef().getName().equals("Ebo_Template") && !bolist.isReadOnly())
        {
            if(bolist.getBoDef() instanceof boDefInterface)
            {
                boDefInterface def = (boDefInterface)bolist.getBoDef();
                if( def.getInterfaceType() != boDefHandler.INTERFACE_STANDARD )
                {
                    opt =new String[1][];
                    menuNumber=0;
                    menuFlatRight.put("MENUFLATRIGHT",opt);
                    opt[menuNumber]=new String[7];
                    opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.3")+bolist.getBoDef().getLabel();
                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+bolist.getBoDef().getName().toLowerCase()+"','edit','method=new&object="+bolist.getBoDef().getName()+"');";
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
                    opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.675")+bolist.getBoDef().getLabel() +JSPMessages.getString("docHTML_builderMenus.676") ;
                }
            }
            else
            {
                opt =new String[1][];
                    menuNumber=0;
                    menuFlatRight.put("MENUFLATRIGHT",opt);
                    opt[menuNumber]=new String[7];
                    opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.678")+bolist.getBoDef().getLabel();
                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+bolist.getBoDef().getName().toLowerCase()+"','edit','method=new&object="+bolist.getBoDef().getName()+"');";
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
                    opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.685")+bolist.getBoDef().getLabel() +JSPMessages.getString("docHTML_builderMenus.686") ;
            }
            
        }
        else
        {
            opt =new String[0][];
            menuFlatRight.put("MENUFLATRIGHT",opt);
            
        }
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;
        return toRet;

}


private  static Hashtable[] menuLIST_SEARCH( boObject obj, boObjectList bolist, docHTML doc, docHTML_controler DOCLIST, int hui , PageContext page ) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuBar=null;
        
        Hashtable menuFlatRight=new Hashtable();
        Hashtable toRet[]=new Hashtable[3];
        
        byte menuNumber=0;

        String[][] opt =new String[1][];
        
        menuFlat.put("MENUFLAT",opt);
        
/* isto estva comentado ...PORQUE? - preciso p causa dos DETACH_ATTRIBUTES */        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.2");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];


        boolean canWrite=false;
        if(obj != null)
        {
            canWrite=
            securityRights.hasRights(bolist.getEboContext(),obj.getName(),securityRights.WRITE)
            && securityRights.hasRights(bolist.getEboContext(),obj.getName(),bolist.getParentAtributeName(),securityRights.WRITE)
            && securityOPL.canWrite(obj);
        }
        else if(bolist.getBoDef() != null && bolist.getBoDef().getName() != null)
        {
            String boName = bolist.getBoDef().getName();
            canWrite=
            securityRights.hasRights(bolist.getEboContext(),boName,securityRights.WRITE)
            && securityRights.hasRights(bolist.getEboContext(),boName,bolist.getParentAtributeName(),securityRights.WRITE);
        }

         //bolist
        if(!bolist.isReadOnly() && canWrite)
        {
            opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.693");
            opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
            opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
            
    
             //if(menuNumber>0){
            String[][] optSUBMENUACCOES =new String[menuNumber+1][];
            System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
            menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
        }
        else
        {
            menuFlat.put("SUBMENU_ACCOES",new String[0][]);
        }

/* ATE AQUI ... descomentei - 21-2-2004 JMF  */
       
 
       if ( !bolist.getBoDef().getName().equals("Ebo_Template") && canWrite)
        {
            if( bolist.getBoDef().getInterfaceType()!=boDefHandler.INTERFACE_STANDARD )
            {
                opt =new String[1][];
                menuNumber=0;
                menuFlatRight.put("MENUFLATRIGHT",opt);
                opt[menuNumber]=new String[7];
                opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.701")+bolist.getBoDef().getLabel();
                
                String searchMode=page.getRequest().getParameter("listmode");   
                
                String option="";
//                if ( "searchone".equalsIgnoreCase(searchMode) )
//                {
                   // option="&ctxParent="+page.getRequest().getParameter("look_parentBoui");
                  //  option+="&addToCtxAttribute="+page.getRequest().getParameter("look_parentAttribute");
                  //  option+="&relatedClientIDX="+page.getRequest().getParameter("clientIDX");
                    option+="&searchClientIdx='+getIDX()+'";
                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+bolist.getBoDef().getName().toLowerCase()+"','edit','method=new&object="+bolist.getBoDef().getName()+option+"');";
                    //opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="parent.newObject('"+bolist.getBoDef().getName()+"');";
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
                    opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.711")+bolist.getBoDef().getLabel() +JSPMessages.getString("docHTML_builderMenus.1") ;
//                }
//                else
//                {
//                    opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+bolist.getBoDef().getName().toLowerCase()+"','edit','method=new&object="+bolist.getBoDef().getName()+option+"');";
//                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
//                    opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
//                    opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
//                    opt[menuNumber][docHTMLgenerateToolBar.TITLE]="Criar novo objecto do tipo "+bolist.getBoDef().getLabel() +" e associar a esta lista" ;
//                }
            }
            else
            {
                opt =new String[2][];
                opt[0] = ((String[][])menuFlat.get("MENUFLAT"))[0];
                menuNumber=1;                
                opt[menuNumber]=new String[7];
                opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.0")+bolist.getBoDef().getLabel();
                opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_CRIAR";
                opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
                if(obj != null)
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Criar", DOCLIST));
                }
                else
                {
                    opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
                }
                opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="c";
                
                menuFlat.put("MENUFLAT",opt);

                menuNumber=0;
                String implObjs[] = ((boDefInterface)bolist.getBoDef()).getImplObjects();
                opt2 =new String[implObjs.length][7];
                String searchMode=page.getRequest().getParameter("listmode");                
                String option="&searchClientIdx='+getIDX()+'";
//                if ( "searchone".equalsIgnoreCase(searchMode) )
//                {
//                    option+="&searchClientIdx='+getIDX()+'";
//                }
                for (int i = 0; i < implObjs.length; i++) 
                {
                    opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.723")+boDefHandler.getBoDefinition(implObjs[i]).getLabel();
                    opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+implObjs[i].toLowerCase()+"','edit','method=new&object="+implObjs[i]+option+"');";
                    opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                    opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                    opt2[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+implObjs[i]+"/ico16.gif" ;
                    opt2[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.730")+boDefHandler.getBoDefinition(implObjs[i]).getLabel()+JSPMessages.getString("docHTML_builderMenus.731") ;
                    menuNumber++;
                }
                menuFlat.put("SUBMENU_CRIAR",opt2);

            }
        }
        else
        {
            opt =new String[0][];
            menuFlatRight.put("MENUFLATRIGHT",opt);
            
        }
 
      
        
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;
        return toRet;

}

    private  static Hashtable[] menuLIST_BRIDGE_TO_BRIDGE( boObject obj, boObject objParent , boObjectList bolist, docHTML doc, docHTML_controler DOCLIST, int hui, boObject sourceObject, String bridgeValue) throws boRuntimeException{
        Hashtable menuFlat=new Hashtable();
        Hashtable menuFlatRight=new Hashtable();
        Hashtable menuBar=null;
        Hashtable toRet[]=new Hashtable[3];
        byte menuNumber=0;
        String[][] opt;
        if ( !bolist.getBoDef().getName().equalsIgnoreCase("boObject") )
            opt =new String[1][];
        else
            opt =new String[2][];
        
        menuFlat.put("MENUFLAT",opt);
        
        bridgeHandler bridge= (bridgeHandler) bolist;
        boDefMethod[] xmth=bridge.getDefAttribute().getMethods();
        //xmth[0].getLabel();
        
        
        opt[menuNumber]=new String[5];
        opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.736");
        opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
        if(obj != null)
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, obj.getName(), String.valueOf(obj.getBoui()), "Accoes", DOCLIST));
        }
        else
        {
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]=String.valueOf(doc.getTabindex(doc.MENU, bolist.poolUniqueId(), DOCLIST));
        }
        opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="A";
        opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_ACCOES";
        
        

        String[][] opt2 =new String[20][];
        menuNumber=0;
        opt2[menuNumber]=new String[5];

         //bolist
         
        opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.740");
        opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="deleteSelected("+hui+")";
        opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
        opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
        opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
        
        if ( xmth!=null)
        {
            for (int i = 0; i < xmth.length ; i++) 
            {
               if ( 
                (xmth[i].templateMode() && objParent.getMode()== boObject.MODE_EDIT_TEMPLATE)
                 ||
                (!xmth[i].templateMode() && objParent.getMode()!= boObject.MODE_EDIT_TEMPLATE) 
               )
               {
                menuNumber++;
                opt2[menuNumber]=new String[5];
                opt2[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=xmth[i].getLabel();
                opt2[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]="executeBridgeMeth("+hui+",'"+bridge.getAttributeName()+"','"+xmth[i].getName()+"')";
                opt2[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
                opt2[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2[menuNumber][docHTMLgenerateToolBar.SUBMENU]=null;
               }
                
            }
            
        }
         //if(menuNumber>0){
        String[][] optSUBMENUACCOES =new String[menuNumber+1][];
        System.arraycopy(opt2,0,optSUBMENUACCOES,0,menuNumber+1);
        menuFlat.put("SUBMENU_ACCOES",optSUBMENUACCOES);
        
         if ( sourceObject != null &&  bolist.getBoDef().getName().equalsIgnoreCase("boObject") )
        {
            menuNumber=1;
            opt[menuNumber]=new String[5];
            opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.751");
            opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=null;
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]="C";
            opt[menuNumber][docHTMLgenerateToolBar.SUBMENU]="SUBMENU_CRIAR";
        
            boDefAttribute xatt         = objParent.getBoDefinition().getAttributeRef( bolist.getParentAtributeName() );
            boDefHandler[] xdefs=xatt.getObjects();
                        
            String[][] opt2r =new String[30][];
            int menuNumber2=0;
              
            for ( int i=0 ; i< xdefs.length ; i++ )
            {
                opt2r[menuNumber2]=new String[5];
                opt2r[menuNumber2][docHTMLgenerateToolBar.TEXT_MENU]= xdefs[i].getLabel();
                opt2r[menuNumber2][docHTMLgenerateToolBar.ONCLICK_MENU]="winmain().openDoc('medium','"+xdefs[i].getName().toLowerCase()+"','edit','method=new&relatedClientIDX='+getIDX()+'&ctxParentIdx='+getDocId()+'&ctxParent="+objParent.bo_boui+"&object="+xdefs[i].getName()+"&addToCtxParentBridge="+bolist.getParentAtributeName()+"');";
                opt2r[menuNumber2][docHTMLgenerateToolBar.TABINDEX]="0";
                opt2r[menuNumber2][docHTMLgenerateToolBar.ACCESSKEY]=null;
                opt2r[menuNumber2][docHTMLgenerateToolBar.SUBMENU]=null;
                menuNumber2++;
            }
            
            String[][] optSUBMENUCRIAR =new String[menuNumber2][];
            System.arraycopy(opt2r,0,optSUBMENUCRIAR,0,menuNumber2);
            menuFlat.put("SUBMENU_CRIAR",optSUBMENUCRIAR);
            
        }
      
        

        if(sourceObject != null ){
            opt =new String[1][];
            
            menuNumber=0;
            menuFlatRight.put("MENUFLATRIGHT",opt);
            opt[menuNumber]=new String[7];                    
            if (bolist.getBoDef().getLabel() != null)
            opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.763")+bolist.getBoDef().getLabel();
            else
            opt[menuNumber][docHTMLgenerateToolBar.TEXT_MENU]=JSPMessages.getString("docHTML_builderMenus.764");
            
            String attributeName = getAttribute(bridgeValue);
        
            String sourceType = getAttributeType(sourceObject, attributeName);
            String sourceSelect = contructSourceSelect(bridgeValue, objParent, sourceObject);
            String destinationSelect = contructDestinationSelect(bridgeValue, objParent, bolist);
            
            StringBuffer onclick=new StringBuffer();
            //onclick.append("LookupBridge('" + sourceSelect + "',");
            onclick.append("LookupBridge('','', ");
            //onclick.append("'" + destinationSelect + "',");
            //onclick.append("'select ISM_ApoliceResponsab.extras where boui=" + objParent.getBoui() + "',");
            onclick.append("'multi','");
            onclick.append(sourceType);
            //onclick.append("DMS_CarExtras");
            onclick.append("','");
            onclick.append(objParent.getName());        
            onclick.append("','");
            onclick.append( sourceObject.getBoui() );
            //onclick.append( parentBoui );
            onclick.append("','");
            onclick.append( objParent.getBoui() );
            onclick.append("','");
            onclick.append( attributeName );        
            //onclick.append( "extras" );
            onclick.append("','");
            onclick.append( bolist.getName() );
            //onclick.append( "extras" );
            onclick.append("','");
            onclick.append( "Y" );
            onclick.append("','");
            onclick.append( "Y" );
            onclick.append("','");
            onclick.append( "Y" );
            onclick.append("','");
            onclick.append( doc.getDocIdx() );
            onclick.append("','1')");        
            //onclick.append("','1','");
            //onclick.append( doc.getDocIdx() );
            //onclick.append("');");
    
            
            //LookupObjects(lookupField, lookupStyle, lookupObject, parentObj , parentBoui, parentAttribute , showNew, docid)
            
            
            opt[menuNumber][docHTMLgenerateToolBar.ONCLICK_MENU]=onclick.toString();
    
            
            opt[menuNumber][docHTMLgenerateToolBar.TABINDEX]="0";
            opt[menuNumber][docHTMLgenerateToolBar.ACCESSKEY]=null;
            opt[menuNumber][docHTMLgenerateToolBar.IMG]="resources/"+bolist.getBoDef().getName()+"/ico16.gif" ;
            opt[menuNumber][docHTMLgenerateToolBar.TITLE]=JSPMessages.getString("docHTML_builderMenus.821");
            //onclick.toString().replace('"',' ').replace('\'',' ');
            //"Adicionar objectos já criados do tipo "+bolist.getBoDef().getLabel() ;
        }    
       
        toRet[0]=menuFlat;
        toRet[1]=menuBar;
        toRet[2]=menuFlatRight;

        return toRet;
    }

    private static String contructSourceSelect(String bridge, boObject parent, boObject sourceObj) throws boRuntimeException{
        String attribute = getAttribute(bridge);        
        StringBuffer sb = new StringBuffer();        
        sb.append("select ");                
        sb.append(sourceObj.getName()).append(".").append(attribute).append(" where boui = ").append(sourceObj.getBoui());        
        return sb.toString();
    }

    private static boObject getSourceObject(String bridge, boObject parent, int grau, int nAttrib) throws boRuntimeException{        
        if(grau == 0){
            throw new boRuntimeException("netgest.bo.dochtml.docHTML_builderMenus.menuLIST_BRIDGE_TO_BRIDGE", "Declaração inválida da bridge", null, ""); 
        }
        boObject obj = parent;
        for(int i = grau; i > 1; i--){
            obj = obj.getParent();
            if(obj == null){
                return null;
            }
        }
        if(nAttrib != 0){
            obj = getAttribute(bridge, obj, nAttrib, grau);
        }        
        return obj;
    }

    private static boObject getAttribute(String bridge, boObject parent, int counterAttrib, int grau) throws boRuntimeException{
        if(counterAttrib == 0){
            return null; 
        }

        String s = bridge.substring((grau * 6)+ grau);
        String attName = null;
        boObject aux = parent;
        AttributeHandler tt = null;
        for(int i = 0; i < counterAttrib; i++){            
            attName = getAttributeName(s, i + 1);
            tt = aux.getAttribute(attName);
            if(tt == null){
                throw new boRuntimeException("netgest.bo.dochtml.docHTML_builderMenus.menuLIST_BRIDGE_TO_BRIDGE", "Atributo inválido: " + attName, null, "");
            }
            aux = tt.getObject();
            if(aux == null) return null;
        }
        
        return aux;
    }

    private static String getAttributeName(String bridge, int nAt){
        int pos = 0;
        String ret = null;
        for(int i = 0; i < nAt; i++){
            ret = bridge.substring(pos == 0 ? pos:pos + 1,bridge.indexOf(".", pos + 1));
            pos = bridge.indexOf(".");
        }
        return ret;
    }

    private static String contructDestinationSelect(String bridge, boObject obj, boObjectList list) throws boRuntimeException{
        StringBuffer sb = new StringBuffer();
        sb.append("select ");
        sb.append(obj.getName()).append(".").append(list.getName()).append(" where boui = ").append(obj.getBoui());        
        return sb.toString();
    }

    private static int countAttribute(String bridge, int numberOfParents) throws boRuntimeException{
        int pos = -1;
        int ret = 0;
        int finalParent = (numberOfParents * 6) + numberOfParents;
        String attributesString = bridge.substring(finalParent);
        if(attributesString == null ||  attributesString.length() == 0){
            throw new boRuntimeException("netgest.bo.dochtml.docHTML_builderMenus.menuLIST_BRIDGE_TO_BRIDGE", "Bridge inexistente", null, "");
        }
        while((pos = attributesString.indexOf(".")) != -1){
            attributesString = attributesString.substring(pos +1);
            ret++;
        }
        return ret;
    }
    
    private static int countParent(String bridge, String word){
        int pos = -1;
        if((pos = bridge.indexOf(word)) == -1) return 0;
        else{
            return countParent(bridge.substring(pos + word.length()), word) + 1;
        }
    } 
    
    private static String getAttributeType(boObject obj, String attributeName) throws boRuntimeException{
        AttributeHandler tt = obj.getAttribute(attributeName);
        if(tt != null){
            boDefAttribute bda =  tt.getDefAttribute();
            if(bda != null){
                return removeObjectWord(bda.getType());
            }
        }
        bridgeHandler bh = obj.getBridge(attributeName);
        if(bh != null){
            boDefAttribute bda = bh.getDefAttribute();
            if(bda != null){
                return removeObjectWord(bda.getType());
            }
        }
        throw new boRuntimeException("netgest.bo.dochtml.docHTML_builderMenus.menuLIST_BRIDGE_TO_BRIDGE", "Bridge inexistente", null, "");
    }
    
    private static String getAttribute(String bridge){
        String ret = null;
        for(int i = (bridge.length() - 1); i >= 0; i--){
            if(bridge.charAt(i) != '.'){
                ret = bridge.charAt(i) + (ret == null ? "":ret); 
            }
            else
                return ret;
        }
        return ret;
    }
    
    private static String removeObjectWord(String s){
        if(s != null){
            int pos = 0;
            if((pos = s.indexOf("object.")) != -1){
                return s.substring(pos + 7, s.length());
            }
        }
        return s;
    }
    
    private static String getRemoveMessage(String label, String cardId)
    {
        if((label == null || "".equals(label.trim())) && (cardId == null || "".equals(cardId.trim())))
        {
            return JSPMessages.getString("docHTML_builderMenus.822");
        }
        
        String s = JSPMessages.getString("docHTML_builderMenus.823");
        if(label != null && !"".equals(label.trim()))
        {
            s += " " + label; 
        }
        
        if(cardId != null && !"".equals(cardId.trim()))
        {
            s += JSPMessages.getString("docHTML_builderMenus.814") + cardId.toString().trim().replaceAll("&#39;"," ").replaceAll("&#34;"," ") + "]"; 
        }
        s +=JSPMessages.getString("docHTML_builderMenus.824");
        return s;
    }
    
    private static String getCardId(String text, boObject obj)
    {
        try
        {
            StringBuffer sb = boObject.mergeAttributes(text, obj);
            if(sb != null && sb.length() > 0)
            {
                return boObject.mergeAttributes(text, obj).toString();
            }
        }
        catch (boRuntimeException e)
        {
            //ignora
        }
        return null;
    }
}