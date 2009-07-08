/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.PrintWriter;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.net.URLDecoder;

import java.sql.SQLException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;

import netgest.bo.controller.Controller;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefMethod;
import netgest.bo.def.boDefViewer;
import netgest.bo.def.boDefViewerCategory;
import netgest.bo.def.boDefXeoCode;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.impl.templates.boTemplateManager;
import netgest.bo.localized.JSPMessages;
import netgest.bo.message.PostInformation;
import netgest.bo.presentation.manager.favoritesLookupManager;
import netgest.bo.presentation.render.elements.ExplorerServer;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boConvertUtils;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectContainer;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boThread;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.sorter.ClassSorter;
import netgest.bo.security.securityRights;
import netgest.bo.system.boMail;
import netgest.bo.system.boPoolOwner;
import netgest.bo.utils.SchemaUtils;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import org.apache.log4j.Logger;

//Há pessoas que choram por saberem que as rosas têm espinhos; outras há que sorriem por saberem que os espinhos têm rosas.
//

public final class docHTML  extends boObjectContainer implements boPoolOwner {

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.dochtml.docHTML");

//   // GRANDE MARTELADA
//   private long p_lasttemplateobui=0;
//   private String p_lasttemplateobjectname="";
//   // GRANDE MARTELADA
//
   private StringBuffer     p_errors = new StringBuffer();



   private String           p_currentBoClsName;
   private String           p_currentBoListId;

   //private ParametersHandler p_initialparameters;

   private boObjectList     p_masterBoList;
   private String           p_masterBoClsName;
   private Hashtable        p_boLists;
   private int              p_id;
   //private docHTMLerrorHandler p_errorList=new docHTMLerrorHandler();
   private boDefHandler     p_masterBodef;
   private Hashtable        p_grids;
   private Hashtable        p_groupGrids;
   private Hashtable        p_sections;
   private static int counter =0;
   private Hashtable p_values;
   private boolean docTotalRefresh = false;
   private boolean parentRefresh = false;
   private ArrayList changedValues;
   private String p_poolUniqueId;
   protected docHTML_controler    p_doccontroller;

    //wizard controller
    private docHtml_wizard p_wiz;

/*old version */


//   public String top="0";
//
//   public String left="0";
//   public String height="100%";
//   public String width="100%";

    /* SYSTEM PARAMETERS */
    private String      p_METHOD;
//  private byte        p_SCOPE;
    private long        p_BOUI;
    private long        p_PARENT_BOUI;
    private String      p_PARENT_ATTRIBUTE;

//    private int         p_PARENT_DOCID;
    private String      p_BOQL;
    private String      p_LIST_FROMMETHOD;
    private String      p_DROPINFO;
    private String      p_BOUISLIST;


    private String      p_OBJECT;
    private String      p_VALUES;
//    private byte        p_NEW_DOC;



//    public boolean ignoreClose = false;  // Flag ignore client request's for close.
//    public Vector  toClose = new Vector(); // doc's idx to close when this is close.


    private int         p_METH_LIST_PAGENUMBER;
    private int         p_METH_LIST_PAGESIZE;
    private String      p_METH_LIST_ORDERBY;
    private String      p_METH_LIST_FULLTEXT;
    private boolean     p_METH_LIST_ALIAS;
    private boolean     p_METH_LIST_WFIRST_ROWS;
    private String      p_METH_ALIAS_OBJ;
    private String      p_METH_LIST_USER_QUERY;
    private String[]    p_METH_LIST_LETTER;
    private boolean     p_METH_USE_SECURITY = true;
    
    //
    private boObject    p_MasterObject;

    private long p_OBJECT_TO_DUPLICATE = -1;
    private long p_FWD_BOUI = -1;
    private String p_TEMPLATE_DUPLICATE = null;

//   private String p_modeview=MODE_VIEW_NORMAL;

//   public static final String MODE_VIEW_EMBEBED="embebed";
//   public static final String MODE_VIEW_NORMAL="normal";

//   private byte state=0;
/*
   private int p_counter=0;
   private StringBuffer p_HTML_Name=new StringBuffer();
   private StringBuffer p_HTML_Id=new StringBuffer();

   private StringBuffer onSubmitForm=new StringBuffer();
   private StringBuffer onChangeForm=new StringBuffer();
   */
   private long p_activebo;
   private transient ArrayList undoableEditListeners = new ArrayList(2);
   //marca se está a correr em modo wizzard
   public boolean p_wizzard = false;
   public boolean p_WebForm = false;
   //private static Hashtable p_querysstore = new Hashtable();


   private boThread p_thread;

   // Para retirar
//   public docHTML_path p_path;
//   public ArrayList p_pathHistory = null;
//   public int p_pathHistoryPointer=-1;
   // end Para retirar

    //tipos utilizados para o tabIndex
    public static final byte FIELD=1;
    public static final byte GRID_CHECK_ALL=2;
    public static final byte BUTTON=3;
    public static final byte IFRAME=4;
    public static final byte AREA=5;
    public static final byte PANEL=6;
    public static final byte MENU=7;
    public static final byte SEARCH=8;
    public static final byte IMAGE=9;


   private Hashtable p_tabindex = new Hashtable();

   private Controller controller = null;
   private Hashtable controllerQueue = null;

   public docHTML(EboContext boctx, int docidx )
   {
       super(boctx);
       p_id = docidx;
//       p_DOCID = docidx;
       p_poolUniqueId =  "DOCHTML["+p_id+"]"+this.hashCode();

       p_boLists=new Hashtable();

       p_grids=new Hashtable();
       p_sections=new Hashtable();
       p_groupGrids=new Hashtable();
       counter++;
//       top="";//+counter*5;
//       left="";//+counter*5;
       p_values = new Hashtable();
       controllerQueue = new Hashtable();
   }
/*   public boObject getObject(long boui) throws boRuntimeException {
        String lctx=getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());
        boObject ret = boObject.getBoManager().loadObject(getEboContext(),boui);
        getEboContext().setPreferredPoolObjectOwner(lctx);
        return ret;
   }*/


   public String[] process(EboContext boctx,docHTML_controler DOCLIST) throws java.io.IOException,ServletException,boRuntimeException{

     return process(null,boctx,DOCLIST);

   }


   public void buildRequestBoList(docHTML_controler DOCLIST) throws boRuntimeException {

    /*
     para definir a master bolist a precedencia é
     1- Verificar se tem BOQL
     2- Verificar se tem PARENT_BOUI e PARENT_ATTRIBUTE
     3- Verificar se tem p_BOUI
     4- Verificar se o p_METH sobre o p_BOUI retorna uma lista

     */

        HttpServletRequest request = super.getEboContext().getRequest();

        try {
            // Put the defaults in boObjectList Variables
            p_METH_LIST_PAGENUMBER = p_METH_LIST_PAGENUMBER != -1?p_METH_LIST_PAGENUMBER:1;
            p_METH_LIST_PAGESIZE   = p_METH_LIST_PAGESIZE != -1?p_METH_LIST_PAGESIZE:boObjectList.PAGESIZE_DEFAULT;
            p_METH_LIST_ORDERBY    = p_METH_LIST_ORDERBY!=null?p_METH_LIST_ORDERBY:"";  // Passar para a função ler parametros;


			if(p_BOQL!=null && p_BOQL.indexOf("ORDER BY --ATT--")>1)
            {

              String orderby=null;
              try
              {
//                    boDefHandler objdef=boDefHandler.getBoDefinition(((p_BOQL.split(" ")[1])));
                    String expression = (p_BOQL.split(" ")[1]);
                    boDefHandler objdef=getBodefHandlerFromExp(null, expression);
                    String orderAtt = objdef.getViewer("general").getChildNode("forms").getChildNode("list").
                               getChildNode("grid").getChildNode("cols").getFirstChild().getChildNode("attribute").getText();
                    orderAtt = getOrderByExp(expression, orderAtt);
                    orderby = " order by "+orderAtt+" asc";
              }
              catch(Exception e) {orderby="";}

              p_BOQL=p_BOQL.replaceFirst("ORDER BY --ATT--",orderby);

            }
			if(p_METH_LIST_ORDERBY!=null && p_METH_LIST_ORDERBY.indexOf("--ATT--")>-1)
            {
              String orderby=null;
              try
              {

//                    boDefHandler objdef=boDefHandler.getBoDefinition(((p_BOQL.split(" ")[1])));

                    String[] words = p_BOQL.split(" ");
                    boDefHandler objdef=null;
                    String expression=null;
                    for (int i = 1; i < words.length; i++)
                    {
                        if( !words[i].startsWith("/*") )
                        {
                            expression = words[i];
                            objdef=getBodefHandlerFromExp(null, expression);
                            if( objdef != null )
                            {
                                break;
                            }
                        }
                    }


                    orderby = objdef.getPath("viewers.viewer.forms.list").getChildNode("grid").getChildNode("cols").getFirstChild().getChildNode("attribute").getText();
                    orderby = getOrderByExp(expression, orderby);
                    p_METH_LIST_ORDERBY=p_METH_LIST_ORDERBY.replaceFirst("--ATT--",orderby);
              }
              catch(Exception e)
              {
                    p_METH_LIST_ORDERBY=p_METH_LIST_ORDERBY.replaceFirst("--ATT--",orderby);
                    logger.warn("Erro a susbtituir o --ATT-- do select",e);
                    orderby="";
              }
            }

            boolean iseditmode = p_METHOD.equalsIgnoreCase("EDIT");

             if( (p_BOQL!=null && !"".equals(p_BOQL)) || p_METH_LIST_ALIAS) {
                 if(iseditmode)
                 {
                    if(!p_METH_LIST_WFIRST_ROWS)
                    {
                        p_masterBoList=boObjectList.edit(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY);
                    }
                    else
                    {
                        p_masterBoList=boObjectList.editWFirstRows(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY);
                    }
                 }
                 else
                 {
                    if(p_METH_LIST_ALIAS)
                    {
                        p_masterBoList=boObjectList.listUsingAlias(super.getEboContext(),p_METH_ALIAS_OBJ,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER);
                    }
                    else
                    {
                        boolean isClass = false;
                       try
                       {
                            Comparator c = (Comparator)Class.forName(p_METH_LIST_ORDERBY).newInstance() ;
                            isClass = true;
                       }
                       catch(Exception e)
                       {
                        //ignore
                        //logger.error("",e);
                       }
                        
                        String lismode = request.getParameter("listmode");
                        
                        if("SEARCHONE".equalsIgnoreCase(lismode) || "SEARMULTI".equalsIgnoreCase(lismode))
                        {
                            String expression = (p_BOQL.split(" ")[1]);
                            boDefHandler objdef=getBodefHandlerFromExp(null, expression);                        
                            String ignoreSecStr = objdef.getViewer("general").getForm("list").getChildNode("grid").getAttribute("ignoreSecurity");
                            p_METH_USE_SECURITY = !(ignoreSecStr!=null && 
                                                      ("yes".equalsIgnoreCase(ignoreSecStr) || 
                                                       "y".equalsIgnoreCase(ignoreSecStr) ||
                                                       "true".equalsIgnoreCase(ignoreSecStr) 
                                                      )
                                                     );
                        
                        }
                       if(!isClass)
                       {
                            if(!p_METH_LIST_WFIRST_ROWS)
                            {
                                if(!p_METH_USE_SECURITY)
                                    p_masterBoList=boObjectList.listNoSecurity(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY);
                                else
                                    p_masterBoList=boObjectList.list(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY);
                            }
                            else
                            {
                                p_masterBoList=boObjectList.listWFirstRows(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,p_METH_LIST_ORDERBY,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY,p_METH_USE_SECURITY);
                            }
                       }
                       else
                       {
                            if(!p_METH_LIST_WFIRST_ROWS)
                            {
                                if(!p_METH_USE_SECURITY)
                                    p_masterBoList=boObjectList.listNoSecurity(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,null,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY);
                                else
                                    p_masterBoList=boObjectList.list(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,null,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY);
                            }
                            else
                            {
                                p_masterBoList=boObjectList.listWFirstRows(super.getEboContext(),p_BOQL,p_METH_LIST_PAGENUMBER,p_METH_LIST_PAGESIZE,null,p_METH_LIST_FULLTEXT,p_METH_LIST_LETTER,p_METH_LIST_USER_QUERY, p_METH_USE_SECURITY);
                            }
                            p_masterBoList.setOrderBy(p_METH_LIST_ORDERBY);
                       }
                    }
//                    logger.info(p_DROPINFO);
                    if ( p_DROPINFO != null )
                    {
                        String[] xdraginf = p_DROPINFO.split(":");
                        if ( xdraginf.length >= 3 )
                        {
                            String drag_objname = xdraginf[1];
                            long   drag_boui    = ClassUtils.convertToLong( xdraginf[2] , 0 );
                            if( drag_boui != 0 )
                            {
                                boObject draged_obj = boObject.getBoManager().loadObject( getEboContext() , drag_boui );
                                if( draged_obj.getName().equalsIgnoreCase( "Ebo_Template" ))
                                {
                                    String xaction="winmain().openDoc('medium','"+((ObjAttHandler)draged_obj.getAttribute("masterObjectClass")).getObject().getAttribute("name").getValueString().toLowerCase()+"','edit','method=newfromtemplate&parent_boui="+draged_obj.getBoui()+"');";
                                    boObjectList xsh = boObjectList.list(super.getEboContext(),
                                    "select Ebo_ShortCut where performer="+getEboContext().getBoSession().getPerformerBoui()+
                                    " and name='" +draged_obj.getAttribute("name").getValueString() +"'");
                                    if ( xsh.isEmpty() )
                                    {
                                        boObject  xobj = boObject.getBoManager().createObject( super.getEboContext() , "Ebo_ShortCut" );
                                        xobj.getAttribute("id").setValueString( drag_objname + ":" + drag_boui );
                                        xobj.getAttribute("name").setValueString( draged_obj.getAttribute("name").getValueString() );
                                        xobj.getAttribute("performer").setValueLong( getEboContext().getBoSession().getPerformerBoui() );
                                        //xobj.getAttribute("cls").setValueString( draged_obj.bbo_classregboui );
                                        xobj.getAttribute("cls").setValueLong( draged_obj.getAttribute("masterObjectClass").getValueLong() );
                                        xobj.getAttribute("action").setValueString( xaction );

                                        xobj.setCheckSecurity(true);
                                        xobj.update();
                                        xobj.setCheckSecurity(false);

                                        p_masterBoList.refreshData();
                                    }

                                }
                                else if (draged_obj.getName().equalsIgnoreCase( "xwfProgram" ))
                                {


                                    String xaction="winmain().openDoc('medium','','edit','?method=new&masterdoc=true&fromPathItem=&defProgramBoui="+draged_obj.getBoui()+"','','__xwfWorkPlace.jsp' )";
                                    boObjectList xsh = boObjectList.list(super.getEboContext(),
                                    "select Ebo_ShortCut where performer="+getEboContext().getBoSession().getPerformerBoui()+
                                    " and name='" +draged_obj.getAttribute("name").getValueString() +"'");
                                    if ( xsh.isEmpty() )
                                    {
                                        boObject  xobj = boObject.getBoManager().createObject( super.getEboContext() , "Ebo_ShortCut" );
                                        xobj.getAttribute("id").setValueString( drag_objname + ":" + drag_boui );
                                        xobj.getAttribute("name").setValueString( draged_obj.getAttribute("name").getValueString() );
                                        xobj.getAttribute("performer").setValueLong( getEboContext().getBoSession().getPerformerBoui() );
                                        //xobj.getAttribute("cls").setValueString( draged_obj.bbo_classregboui );
//                                        xobj.getAttribute("cls").setValueLong( draged_obj.getAttribute("masterObjectClass").getValueLong() );
                                        xobj.getAttribute("action").setValueString( xaction );

                                        xobj.setCheckSecurity(true);
                                        xobj.update();
                                        xobj.setCheckSecurity(false);

                                        p_masterBoList.refreshData();
                                    }
                                }
                            }
                        }
                    }
                 }

             } else if ( p_PARENT_BOUI!=0 && p_LIST_FROMMETHOD !=null) {
                 p_currentBoListId=getBolistIdMeth(p_PARENT_BOUI,p_LIST_FROMMETHOD+"."+p_METHOD,p_METH_LIST_ORDERBY);
             } else if ( p_PARENT_BOUI!=0 && p_PARENT_ATTRIBUTE !=null) {

                // Linha a ver mas parece-me que já não é necessário.
                //p_masterBoList=boObjectList.list(super.getEboContext(),p_PARENT_ATTRIBUTE,p_PARENT_BOUI);
                //p_masterBoList=boListFromMeth(p_METHOD,p_PARENT_BOUI);
                if ( request.getParameter("template_boui")!=null){
                    //esta na edicao do template e foi redireccionado para mostrar os templates que estao nesta bridge
                    //1- vai ao template
                    p_boLists.clear();
                    p_masterBoList=null;
                    long tmpl_boui= ClassUtils.convertToLong(request.getParameter("template_boui"));
                    boObject tmpl=this.getObject( tmpl_boui );
                    bridgeHandler maps=tmpl.getBridge("mappingAttributes");
                    boolean find=false;
                    maps.beforeFirst();
                    boObject map;
                    String listTemplatesBouis="";
                    while ( maps.next() ){
                         map=maps.getObject();
                         if (map.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(p_PARENT_ATTRIBUTE )) {
                            find=true;
                            listTemplatesBouis=map.getAttribute("value").getValueString();
                            break;
                         }
                    }


                    String[] listbouis=listTemplatesBouis.split(";");
                    StringBuffer tboql=new StringBuffer("select Ebo_Template where ");
                    if( listbouis.length == 1 && listbouis[0].equals("") ) tboql.append("0=1");
                    else {
                        for (int i = 0; i < listbouis.length ; i++)  {
                            tboql.append(" boui = " );
                            tboql.append(listbouis[i]);
                            if ( i+1 < listbouis.length ) {
                                tboql.append(" or ");
                            }
                        }
                    }
                    p_currentBoListId= getBolistId( tboql.toString() );
                    long xboui=ClassUtils.convertToLong(tmpl.getParameter("relatedObjBOUI"));

                    boObject related=this.getObject(xboui);
                    AttributeHandler xatt=related.getAttribute(p_PARENT_ATTRIBUTE );

                    this.getBoObjectListByKey(p_currentBoListId).setParameter("relatedObjBOUI",tmpl.getParameter("relatedObjBOUI") );
                    this.getBoObjectListByKey(p_currentBoListId).setParameter("relatedCls",tmpl.getParameter("relatedCls") );
                    String xref=xatt.getDefAttribute().getReferencedObjectName();
                    this.getBoObjectListByKey(p_currentBoListId).setParameter("relatedClsAttribute",xref );
                    this.getBoObjectListByKey(p_currentBoListId).setParameter("template_boui",""+tmpl.bo_boui );
                    this.getBoObjectListByKey(p_currentBoListId).setParameter("relatedObjAttribute",p_PARENT_ATTRIBUTE );
                }
                else {
                    p_currentBoListId=getBolistIdMeth(p_PARENT_BOUI,p_PARENT_ATTRIBUTE+"."+p_METHOD, p_METH_LIST_ORDERBY);
                }

             }
             else if ( p_PARENT_BOUI!=0 && p_METHOD !=null)
             {
                //p_masterBoList=boListFromMeth(p_METHOD,p_PARENT_BOUI);
                if(p_METHOD.equalsIgnoreCase("newFromTemplate"))
                {
//                    if( p_lasttemplateobui != 0 )
//                    {
//                        p_masterBoList = boObjectList.edit(super.getEboContext(),p_lasttemplateobjectname,p_lasttemplateobui);
//                    }
//                    else
//                    {
                        boObject boobject = ((Ebo_TemplateImpl)getObject(p_PARENT_BOUI)).loadTemplate();
                        p_masterBoList = boObjectList.edit(super.getEboContext(),boobject.getName(),boobject.getBoui());
//                        p_lasttemplateobui = boobject.getBoui();
//                        p_lasttemplateobjectname = boobject.getName();
                        p_masterBoList.inserRow(boobject.getBoui());
                        boobject.poolSetStateFull();
                        p_masterBoList.poolSetStateFull();
//                        this.poolSetStateFull(DOCLIST.poolUniqueId());
//                    }
                }
                else
                {
                    p_currentBoListId=getBolistIdMeth(p_PARENT_BOUI,p_METHOD,p_METH_LIST_ORDERBY);
                }
             }

             else if ( p_BOUISLIST != null )
            {

                 String n[]= new String[0];
                 long[] xlistBouis= new long[0];
                 if ( p_BOUISLIST.length() > 0)
                 {
                    n=p_BOUISLIST.split(";");
                    xlistBouis= new long[n.length];
                    for (int i = 0; i < n.length ; i++)
                    {
                        xlistBouis[i]= ClassUtils.convertToLong( n[i] );
                    }


                 }
                 if ( xlistBouis.length == 0)
                 {
                        if ( request.getParameter("object")==null )
                        {
                            p_masterBoList = boObjectList.list(super.getEboContext(),"select Ebo_Perf where 0=1");
                        }
                        else
                        {
                            String xobj=request.getParameter("object");
                            p_masterBoList = boObjectList.list(super.getEboContext(),"select "+xobj +" where 0=1");
                        }


                 }
                 else
                 {
                 p_masterBoList = boObjectList.list(super.getEboContext(), xlistBouis,1,200,"");
                 }

             }
             else if ( p_BOUI !=0 )
             {
                if(p_METHOD.equalsIgnoreCase("EDIT")){



                    boObject xobj = this.getObject(p_BOUI);

//                    if( request.getParameter("ctxParent")!=null )
//                    {
//                        long ctxParent = ClassUtils.convertToLong( (String)request.getParameter("ctxParent") ,0 );
//                        if ( ctxParent!=0 )
//                        {
//                            boObject ctxOParent;
//
//                            ctxOParent=this.getObject( ctxParent  );
//                            ctxOParent.getThread().add( new BigDecimal( ctxOParent.getBoui()), new BigDecimal(p_BOUI )  );
//                        }
//                    }

//                   //  if(xobj.exists() ) { //if(xobj!=null) { //
//                        p_masterBoList = null;
//                        if( request.getParameter("ctxParent") != null
//                                &&
//                            request.getParameter( "addToCtxParentBridge" )!= null
//                                &&
//                            request.getParameter( "docid" )!= null
//                          )
//                        {
//                        //SER�? PRECISO ...... TODO
//                            boObject xparentobj = getObject( Long.parseLong( request.getParameter("ctxParent") ) );
//                            bridgeHandler bridge = xparentobj.getBridge( request.getParameter( "addToCtxParentBridge" ) );
//                            if( bridge!=null && bridge.haveBoui( p_BOUI ))
//                            {
//                                xparentobj.getUpdateQueue().add( p_BOUI, xparentobj.getUpdateQueue().MODE_SAVE );
//                                if( !xparentobj.poolIsStateFull() )
//                                {
//                                    xparentobj.poolSetStateFull();
//                                }
//                            }
//                        }

                        p_masterBoList=boObjectList.edit(super.getEboContext(),xobj.getName(),p_BOUI);
                        if( p_masterBoList.isEmpty() )
                        {
                            p_masterBoList.inserRow( p_BOUI );
                            p_masterBoList.poolSetStateFull(this.poolUniqueId());

                        }


                } else {
                    p_masterBoList=boListFromMeth(p_METHOD,p_BOUI,p_METH_LIST_ORDERBY);
                }
             }
             else if (p_OBJECT!=null && p_METHOD!=null &&
                (p_METHOD.equalsIgnoreCase("duplicate") || p_METHOD.equalsIgnoreCase("forward")
                || p_METHOD.equalsIgnoreCase("reply") || p_METHOD.equalsIgnoreCase("replyAll"))
             )
             {
                if(!this.poolIsStateFull()) {
                    this.poolSetStateFull(DOCLIST.poolUniqueId());
                }
                p_masterBoList=boObjectList.edit(super.getEboContext(),"SELECT "+p_OBJECT+" WHERE 0=1");
                docHTML relatedDoc=DOCLIST.getDocByIDX( ClassUtils.convertToInt(request.getParameter("relatedDocid")),super.getEboContext() );

                boObject relatedObj=relatedDoc.getObject(p_OBJECT_TO_DUPLICATE);

                String thisctx = getEboContext().setPreferredPoolObjectOwner( relatedDoc.poolUniqueId() );

                getEboContext().addShareOwner( thisctx );

                boObject tmpl = null;
                if("true".equalsIgnoreCase(p_TEMPLATE_DUPLICATE))
                {
                  //  tmplDuplicated=duplicateObject( relatedObj.getAttribute("TEMPLATE").getObject()  );

                    tmpl=boObject.getBoManager().createObject( super.getEboContext(),"Ebo_Template");
                    bridgeHandler mappingAttributes=tmpl.getBridge("mappingAttributes");
                    bridgeHandler origMappinfAttributes=relatedObj.getAttribute("TEMPLATE").getObject().getBridge("mappingAttributes");
                    origMappinfAttributes.beforeFirst();
                    while ( origMappinfAttributes.next() )
                    {
                        boObject map=boObject.getBoManager().createObject( super.getEboContext(), origMappinfAttributes.getObject()  );
                        mappingAttributes.add( map.getBoui() );
                    }

                }

                boObject obj = null;
                try
                {
                    obj = duplicateObject( relatedObj );

                }
                finally
                {
                    getEboContext().removeShareOwner( relatedDoc.poolUniqueId() );
                    getEboContext().setPreferredPoolObjectOwner( thisctx );
                }

                //obj.poolSetStateFull();



                p_masterBoList.inserRow( obj.getBoui() );

                if( tmpl!=null )
                {
                    obj.setModeEditTemplate();
                    //obj.poolSetStateFull(this.poolUniqueId());

                    StringBuffer cardText=obj.getTextCARDID();


                    AttributeHandler attr=tmpl.getAttribute("masterObjectClass");

                    boObject boClsReg=boObject.getBoManager().loadObject(super.getEboContext(),"select Ebo_ClsReg where name='"+obj.getName()+"'" );
                    tmpl.setParameter("relatedObjBOUI",""+obj.bo_boui);
                    tmpl.setParameter("relatedCls",""+obj.getName());
                    tmpl.getAttribute("name").setValueString(cardText.toString());
                    attr.setValueString(""+boClsReg.bo_boui );
                    attr.setDisabled();

                    //tmpl.poolSetStateFull(this.poolUniqueId());
                    obj.getAttribute("TEMPLATE").setValueString(""+tmpl.bo_boui);
                }
                //forward do email
                if(p_METHOD.equalsIgnoreCase("forward"))
                {
                     boMail.setForwardFields(obj, relatedObj, boMail.FORWARD);
                }
                //replay do email
                if(p_METHOD.equalsIgnoreCase("reply"))
                {
                    netgest.bo.system.boMail.setForwardFields(obj, relatedObj, boMail.REPLAY);
                }
                //replay do email
                if(p_METHOD.equalsIgnoreCase("replyAll"))
                {
                    netgest.bo.system.boMail.setForwardFields(obj, relatedObj, boMail.REPLAY_ALL);
                }



             }
             else if((p_OBJECT!=null && "execute".equals(p_METHOD)))
             {
                if(!this.poolIsStateFull())
                {
                    this.poolSetStateFull(DOCLIST.poolUniqueId());
                }
                p_masterBoList=boObjectList.edit(super.getEboContext(),"SELECT "+p_OBJECT+" WHERE 0=1");
                docHTML relatedDoc=DOCLIST.getDocByIDX( ClassUtils.convertToInt(request.getParameter("relatedDocid")),super.getEboContext() );

                boObject relatedObj=relatedDoc.getObject(p_FWD_BOUI);
                String thisctx = getEboContext().setPreferredPoolObjectOwner( relatedDoc.poolUniqueId() );

                getEboContext().addShareOwner( thisctx );
                String toExecute = super.getEboContext().getRequest().getParameter("toExecute");

                if( !toExecute.startsWith("STATIC-") )
                {
                    try
                    {

                        String[] mths=toExecute.split(":");

                        for( int j=0 ; j< mths.length ; j++ )
                        {


                            String method=mths[j];
                            if( method.indexOf('.')==-1 )
                            {
                                boDefMethod def = relatedObj.getBoDefinition().getBoMethod(method, new Class[0]);
                                Object obj = null;
                                if(def != null && def.getIsMenu()){
                                    Method ometh = relatedObj.getClass().getMethod(method, new Class[0]);
                                    obj = ometh.invoke(relatedObj, new Object[0]);
                                }
                                else
                                {
                                    if(relatedObj.getStateManager() != null)
                                    {
                                        Method ometh = relatedObj.getStateManager().getClass().getMethod(method,new Class[0]);
                                        obj = ometh.invoke(relatedObj.getStateManager() ,new Object[0]);
    //                                    lastobj.update();
                                        relatedObj.getStateManager().onLoad( relatedObj );
                                    }
                                    else
                                    {
                                        Method ometh = relatedObj.getClass().getMethod(method, new Class[0]);
                                        obj = ometh.invoke(relatedObj, new Object[0]);
                                    }
                                }
                                if(obj != null)
                                {
                                    boObject boObj = (boObject)obj;
                                    getEboContext().removeShareOwner( relatedDoc.poolUniqueId() );
                                    getEboContext().setPreferredPoolObjectOwner( thisctx );
                                    p_masterBoList.inserRow( boObj.getBoui() );
                                }

                            }
                            else
                            {
                               Object obj = null;
                               if ( method.startsWith("ATR-") )
                               {
                                    method=method.substring(4);
                                    String attributeName=method.split("\\.")[0];
                                    String xmeth=method.split("\\.")[1];
                                    if (relatedObj.getBridge(attributeName).first())
                                    {
                                        netgest.bo.runtime.AttributeHandler attr=relatedObj.getBridge(attributeName ).getAttribute(attributeName);
                                        Method ometh=attr.getClass().getMethod( xmeth,new Class[0] );
                                        obj = ometh.invoke(attr  ,new Object[0] );
                                    }

                               }
                               else
                               {
                                    Method ometh = relatedObj.getClass().getMethod(method.split("\\.")[1],new Class[0]);
                                    if(ometh!=null)
                                    {
                                          obj = ometh.invoke(relatedObj,new Object[0]);
                                          relatedObj.getStateManager().onLoad( relatedObj );

                                    }
                               }
                               if(obj != null)
                                {
                                    boObject boObj = (boObject)obj;
                                    getEboContext().removeShareOwner( relatedDoc.poolUniqueId() );
                                    getEboContext().setPreferredPoolObjectOwner( thisctx );
                                    p_masterBoList.inserRow( boObj.getBoui() );
                                }
                            }
                        }
                    }
                    catch( InvocationTargetException e )
                    {
                        String[] args = { toExecute };
                        if( e.getTargetException() instanceof boRuntimeException &&
                            ((boRuntimeException)e.getTargetException()).getErrorCode().equals("BO-3022")
                        )
                        {
                            addErrorMessage(JSPMessages.getString("docHTML.3"));
                        }
                        throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e.getTargetException(),args);
                    }
                    catch( Exception e) {
                        String[] args = { toExecute };
                        throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e,args);
                    }
               }
               else if( toExecute.startsWith("STATIC-") )
               {
                    String[] statOut = toExecute.split("-");
                    String[] mths = statOut[statOut.length -1].split(";");
                    String[] parms = mths[mths.length -1].split("\\|");
                    String[] path = mths[0].split("\\.");
                    String cl = mths[0].split("." + path[path.length -1])[0];
                    Class clss = Class.forName(cl);
                    Class partypes[] = new Class[parms.length ];
                    for(int i = 0; i < parms.length  ; i++)
                    {
                        if("this".equals(parms[i])){
                            partypes[i] = boObject.class;
                        }
                        else
                        {
                            partypes[i] = String.class;
                        }
                    }
                    Method ometh = clss.getMethod(path[path.length -1],partypes);

                    Object args[] = new Object[parms.length];
                    for(int i = 0; i < parms.length  ; i++)
                    {
                        if("this".equals(parms[i])){
                            args[i] = relatedObj;
                        }
                        else if("docid".equals(parms[i])){
                            args[i] = ""+this.getDocIdx();
                        }
                        else if("pooluniquedocid".equals(parms[i])){
                            args[i] = this.poolUniqueId();
                        }
                        else
                        {
                            args[i] = parms[i];
                        }
                    }
                    Object obj = ometh.invoke(null,args);
                    if(obj != null)
                    {
                        boObject boObj = (boObject)obj;
                        getEboContext().removeShareOwner( relatedDoc.poolUniqueId() );
                        getEboContext().setPreferredPoolObjectOwner( thisctx );
                        p_masterBoList.inserRow( boObj.getBoui() );
                    }
               }
             }
             else if (p_OBJECT!=null && p_METHOD!=null && p_METHOD.equalsIgnoreCase("NEW") )
             {


                String s_ctxParent = request.getParameter( "ctxParent" );
                if(!this.poolIsStateFull()) {
                    this.poolSetStateFull(DOCLIST.poolUniqueId());
                }
                p_masterBoList=boObjectList.edit(super.getEboContext(),"SELECT "+p_OBJECT+" WHERE 0=1");

                boObject obj = null;
                if( s_ctxParent != null )
                {
                    obj = boObject.getBoManager().createObjectWithParent(super.getEboContext(),p_OBJECT,Long.parseLong( s_ctxParent ));
//                    obj.addParent( this.getObject ( Long.parseLong( s_ctxParent ) ) );
                }
                else
                {
                    obj = boObject.getBoManager().createObject(super.getEboContext(),p_OBJECT);
                }

                obj.poolSetStateFull();
                p_masterBoList.inserRow( obj.getBoui() );



               }



                if ( request.getParameter("toSaveAndNew")!=null && !haveErrors())
                 {
                      boObject xobj = null;
                      if ( p_BOUI > 0 )
                      {
                           xobj = this.getObject(p_BOUI);
                           xobj.markAsRead();
                      }
                      if ( ( p_OBJECT == null || p_OBJECT.length() ==0) && xobj!=null )
                        {
                            p_OBJECT = xobj.getName();
                        }
                     String s_ctxParent = request.getParameter( "ctxParent" );
                        if(!this.poolIsStateFull()) {
                            this.poolSetStateFull(DOCLIST.poolUniqueId());
                        }
                        p_masterBoList=boObjectList.edit(super.getEboContext(),"SELECT "+p_OBJECT+" WHERE 0=1");
                        boObject obj = boObject.getBoManager().createObject(super.getEboContext(),p_OBJECT);

                        obj.poolSetStateFull();
                        if( s_ctxParent != null )
                        {
                            obj.addParent( this.getObject ( Long.parseLong( s_ctxParent ) ) );
                        }
                        p_masterBoList.inserRow( obj.getBoui() );


                 }


                if ( request.getParameter("edit_template_boui")!=null)
                 {
                    // FOI Chamado para edicao um template ;

                     String sedit_template_boui=(String) request.getParameter("edit_template_boui");
                     long edit_template_boui=ClassUtils.convertToLong( sedit_template_boui );

                     boObject objectInEdit=null;
                     if ( p_masterBoList.isEmpty() )
                     {
                        objectInEdit = boObject.getBoManager().createObject(super.getEboContext(),p_masterBoList.getBoDef().getName(),true);
                        p_masterBoList.inserRow( objectInEdit.getBoui() );
                     }
                     else
                     {
                        objectInEdit=p_masterBoList.getObject();
                     }


                     //objectInEdit.setModeEditTemplate();

                     if ( objectInEdit.getAttribute("TEMPLATE").getValueObject() == null )
                     {
                         Ebo_TemplateImpl tmpl=(Ebo_TemplateImpl)this.getObject( edit_template_boui );

                         tmpl.loadTemplate(objectInEdit);

                         AttributeHandler attr=tmpl.getAttribute("masterObjectClass");

                         tmpl.setParameter("relatedObjBOUI",""+objectInEdit.bo_boui);
                         tmpl.setParameter("relatedCls",""+objectInEdit.getName());
                         //tmpl.getAttribute("name").setValueString(cardText.toString());
                         //attr.setValueString(""+boClsReg.bo_boui );
                         attr.setDisabled();
                         objectInEdit.poolSetStateFull(this.poolUniqueId());
                         tmpl.poolSetStateFull(this.poolUniqueId());
                         objectInEdit.getAttribute("TEMPLATE").setValueString(""+tmpl.bo_boui);
                     }


                 }
         }
         catch(Exception e)
         {

            StringBuffer pars = new StringBuffer();

            Enumeration headersNames = request.getHeaderNames();
            pars.append( "User:" ).append( getEboContext().getSysUser().getUserName() ).append("\n");
            pars.append( "Headers:" ).append("\n");
            while( headersNames.hasMoreElements() )
            {
                String headerName = headersNames.nextElement().toString();
                pars.append( headerName ).append("=").append( request.getHeader( headerName ) ).append("\n");
            }

            pars.append("URL: ");
            pars.append( request.getRequestURL().toString() ).append("\n");
            pars.append("\n");
            Enumeration oEnum = request.getParameterNames();
            while( oEnum.hasMoreElements() )
            {
                String name = oEnum.nextElement().toString();
                pars.append( name );
                pars.append( "=" );
                pars.append( request.getParameter( name ) );
                pars.append("\n");
            }

            logger.error("Erro a construir o boObjectList para a JSP:\nRequest:\n" + pars, e);

            String[] args = { convertRequestToString() };
            throw new boRuntimeException(this.getClass().getName()+".buildMasterList","BO-3110",e,args);
         }
         if(p_masterBoList==null && p_currentBoListId != null) {
            p_masterBoList = this.getBoObjectListByKey(p_currentBoListId);
         }
         if(p_masterBoList!=null) {
            if(p_masterBoList.getObjectContainer()==null)
            {
                p_masterBoList.setObjectContainer(this);
            }
            p_masterBodef = p_masterBoList.getBoDef();
            if(p_masterBodef != null)
            {
                if("boObject".equals(p_masterBodef.getName()))
                {
                    boObject aux = p_masterBoList.getObject();
                    if(aux != null)
                    {
                        p_masterBodef = aux.getBoDefinition();
                    }
                }
            }
         }
   }



/*   public String[] process(String boClsName,EboContext ctx,docHTML_controler DOCLIST) throws boRuntimeException,java.io.IOException{

     String[] toReturn=new String[2];
     HttpServletRequest request = ctx.getRequest();


     super.getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());

     // Read the current request and bind values and excute the actions associated to the request.
     //     ParametersHandler crequest;
     //     readRequest(crequest=new ParametersHandler(request));
     readRequest ( request );

     if( p_VALUES!=null)
     {
         bindData(p_VALUES,DOCLIST,request);
     }

     //
     //  Return a object list to render the layout of the document.


     if ( request.getParameter("tree_key")!=null )
     {
         String drag_to_col_header  =request.getParameter("drag_to_col_header");
         String drag_to_col_group   =request.getParameter("drag_to_col_group" );
         String tree_key    =request.getParameter("tree_key" );
         String orderCol    =request.getParameter("orderCol" );
         String openGroup   =request.getParameter("openGroup" );
         String closeGroup  =request.getParameter("closeGroup" );
         int toggleOrderGroup  = ClassUtils.convertToInt( request.getParameter("toggleOrderGroup" ),-1);
         String fullText    =request.getParameter("fullTextGG" );
         String userQuery   =request.getParameter("userQuery" );
         String treeOperation=request.getParameter("treeOperation" );
         String parametersQuery=request.getParameter("parametersQuery" );

         long userQueryBoui = ClassUtils.convertToLong( request.getParameter("userQueryBoui") ,-1 );

         docHTML_treeServer.processTree(
            tree_key ,
            drag_to_col_header ,
            drag_to_col_group ,
            orderCol,
            openGroup,
            closeGroup,
            toggleOrderGroup,
            fullText,
            userQuery,
            userQueryBoui,
            treeOperation,
            parametersQuery,
            this);
     }

     p_currentBoListId=null;
     p_currentBoClsName=boClsName;

     p_currentBoListId="master";

     buildRequestBoList(DOCLIST);

     toReturn[0]=""+p_id;
     toReturn[1]=p_currentBoListId;


     return toReturn;
   }*/

    public String[] process(String boClsName,EboContext ctx,docHTML_controler DOCLIST) throws boRuntimeException,java.io.IOException{

        String[] toReturn=new String[2];
        HttpServletRequest request = ctx.getRequest();
        super.getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());

        setController(ControllerFactory.getController(this));
//        this.controller = ControllerFactory.getController(this);
//        super.getEboContext().setController(controller);

        // Read the current request and bind values and excute the actions associated to the request.
        //     ParametersHandler crequest;
        //     readRequest(crequest=new ParametersHandler(request));
        this.controller.readRequest ( request );

        if( p_VALUES != null)
        {
            this.controller.bindData(p_VALUES,DOCLIST,request);
        }

        this.controller.cleanCache();

        processTreeAndExplorer(request);

        p_currentBoListId = null;
        p_currentBoClsName = boClsName;
        p_currentBoListId="master";

        this.controller.buildRequestBoList(DOCLIST,super.getEboContext().getRequest());

        toReturn[0]=String.valueOf(p_id);
        toReturn[1]=p_currentBoListId;

        return toReturn;
    }

    private void processTreeAndExplorer(HttpServletRequest request)  throws boRuntimeException
    {
        //  Return a object list to render the layout of the document.
        if ( request.getParameter("tree_key")!=null || request.getParameter("explorer_key")!=null)
        {
            String drag_to_col_header  =request.getParameter("drag_to_col_header");
            String drag_to_col_group   =request.getParameter("drag_to_col_group" );
            String tree_key    =request.getParameter("tree_key" );
            String explorer_key    =request.getParameter("explorer_key" );
            String setDefaultExplorer =request.getParameter("setDefaultExplorer" );
            String toExecute =request.getParameter("toExecute" );
            String orderCol    =request.getParameter("orderCol" );
            String openGroup   =request.getParameter("openGroup" );
            String closeGroup  =request.getParameter("closeGroup" );
            int toggleOrderGroup  = ClassUtils.convertToInt( request.getParameter("toggleOrderGroup" ),-1);
            String fullText    =request.getParameter("fullTextGG" );
            String userQuery   =request.getParameter("userQuery" );
            String treeOperation=request.getParameter("treeOperation" );
            String parametersQuery=request.getParameter("parametersQuery" );

            long userQueryBoui = ClassUtils.convertToLong( request.getParameter("userQueryBoui") ,-1 );
            long userSvExplorer = ClassUtils.convertToLong( request.getParameter("userSvExplorer") ,-1 );
            long bouiPreview= ClassUtils.convertToLong( request.getParameter("bouiTopreview"));

            if("true".equalsIgnoreCase(setDefaultExplorer))
            {
                ExplorerServer.setDefaultExplorer(explorer_key, getEboContext().getBoSession().getPerformerBoui());
            }

            if(tree_key != null && tree_key.length() > 0)
            {
                 docHTML_treeServer.processTree(
                    tree_key ,
                    drag_to_col_header ,
                    drag_to_col_group ,
                    orderCol,
                    openGroup,
                    closeGroup,
                    toggleOrderGroup,
                    fullText,
                    userQuery,
                    userQueryBoui,
                    treeOperation,
                    parametersQuery,
                    this);
             }
             else if(explorer_key != null && explorer_key.length() > 0)
             {
                ExplorerServer.processExplorer(
                    explorer_key ,
                    drag_to_col_header ,
                    drag_to_col_group ,
                    orderCol,
                    openGroup,
                    closeGroup,
                    toggleOrderGroup,
                    fullText,
                    userQuery,
                    userQueryBoui,
                    userSvExplorer,
                    treeOperation,
                    parametersQuery,
                    bouiPreview,
                    toExecute,
                    this);
             }
        }
    }
    /*
    public void setMasterObject( boObject obj , HttpServletRequest request ) throws boRuntimeException
    {
        p_MasterObject = obj ;
        String parent_boui=request.getParameter("parent_boui");
            String method=request.getParameter( "method" );
            String inputMethod=request.getParameter( "method" );
            String requestedBoui=request.getParameter( "boui" );

            if( method !=null )
            {
                if( method.equalsIgnoreCase("newfromtemplate") || method.equalsIgnoreCase("duplicate")
                    || method.equalsIgnoreCase("forward") || method.equalsIgnoreCase("new")
                    || p_METHOD.equalsIgnoreCase("reply")
                    || p_METHOD.equalsIgnoreCase("replyAll"))
                {
                    method="edit";
                    requestedBoui = ""+obj.getBoui();
                }
            }


            Enumeration parms = request.getParameterNames();
            StringBuffer myUrl=new StringBuffer();
            Hashtable parameters= new Hashtable();
            while ( parms.hasMoreElements() )
            {
                String pname =  parms.nextElement().toString();
                String parameter ="";
                boolean addParameter =
                !(
                pname.equals("myIDX") ||
                pname.equals("boFormSubmitMode") ||
                pname.equals("boiChanged") ||
                pname.equals("toClose" ) ||
                pname.equals("orphan") ||
                pname.equals("boFormSubmitSecurity") ||
                pname.equals("boFormSubmitId") ||
                pname.equals("editingTemplate") ||
                pname.equals("parent_boui") ||
                pname.equals("boFormSubmitXml")
                );
                if( addParameter )
                {
                    if ( pname.equalsIgnoreCase("method") )
                    {

                        parameter = method;

                    }
                    else
                    {

                        parameter = java.net.URLEncoder.encode( request.getParameter( pname ) );
                    }
                    //myUrl.append("&").append( pname ).append('=' ).append(  );
                    parameters.put(pname, parameter );
                }
            }

            if ( requestedBoui != null && parameters.get( "boui" ) == null )
            {
                parameters.put("boui",requestedBoui );
            }

            if ( requestedBoui != null && parameters.get( "BOUI" ) == null )
            {
                parameters.put("BOUI",requestedBoui );
            }

            parameters.put("docid", ""+getDocIdx() );


            String url = request.getRequestURI();
            request.setAttribute("pathItem","1");


            p_path= new docHTML_path( this , url ,parameters ,  obj.getBoui() );

            p_pathHistoryPointer=0;
            p_pathHistory = new ArrayList();
            p_pathHistory.add( p_path.getPathItemById("1") );

  }
  */
  /**
   * Methodo chamado no principio das JSP
   *
   *
   */
   /*
  public String path_ProcessRequest( HttpServletRequest request , boObject obj ) throws boRuntimeException
  {
        if( obj == null ) return null;
        String id_pathItem=request.getParameter( "pathItem" );
        boolean existsPathItem=true;
        docHTML_path.PathItem pathItem = null;

        if ( id_pathItem == null )
        {
            existsPathItem=false;
            id_pathItem = (String)request.getAttribute("pathItem");
            if ( id_pathItem!=null)
            {
                pathItem = p_path.getPathItemById( id_pathItem  );
            }
        }
        else
        {
            pathItem = p_path.getPathItemById( id_pathItem  );

        }




        if ( id_pathItem == null )
        {
            // só entra aqui quando é a segunda vez

            String parent_boui=request.getParameter("parent_boui");
            String method=request.getParameter( "method" );
            String inputMethod=request.getParameter( "method" );
            String requestedBoui=request.getParameter( "boui" );

            if( method !=null )
            {
                if( method.equalsIgnoreCase("newfromtemplate") || method.equalsIgnoreCase("duplicate")
                    || method.equalsIgnoreCase("forward") || method.equalsIgnoreCase("new")
                    || p_METHOD.equalsIgnoreCase("reply") || p_METHOD.equalsIgnoreCase("replyAll"))
                {
                    method="edit";
                    requestedBoui = ""+obj.getBoui();
                }
            }


            Enumeration parms = request.getParameterNames();
            StringBuffer myUrl=new StringBuffer();
            Hashtable parameters= new Hashtable();
            while ( parms.hasMoreElements() )
            {
                String pname =  parms.nextElement().toString();
                String parameter ="";
                boolean addParameter =
                !(
                pname.equals("myIDX") ||
                pname.equals("boFormSubmitMode") ||
                pname.equals("boiChanged") ||
                pname.equals("toClose" ) ||
                pname.equals("orphan") ||
                pname.equals("boFormSubmitSecurity") ||
                pname.equals("boFormSubmitId") ||
                pname.equals("editingTemplate") ||
                pname.equals("parent_boui") ||
                pname.equals("boFormSubmitXml")
                );
                if( addParameter )

                {
                    if ( pname.equalsIgnoreCase("method") )
                    {

                        parameter = method;

                    }
                    else
                    {

                        parameter = java.net.URLEncoder.encode( request.getParameter( pname ) );
                    }
                    //myUrl.append("&").append( pname ).append('=' ).append(  );
                    parameters.put(pname, parameter );
                }
            }
            if ( requestedBoui != null && parameters.get( "boui" ) == null )
            {
                parameters.put("boui",requestedBoui );
            }

            if ( requestedBoui != null && parameters.get( "BOUI" ) == null )
            {
                parameters.put("BOUI",requestedBoui );
            }
            parameters.put("docid", ""+getDocIdx() );

            String url = request.getRequestURI();

            if (  ClassUtils.convertToLong( requestedBoui ) >0  )
            {
                pathItem = p_path.newPathItem(  url, parameters, ClassUtils.convertToLong( requestedBoui ) );
                request.setAttribute( "pathItem", ""+pathItem.getId() );
            }
            else
            {
                int xx=1;
            }

        }






        String id_fromPathItem = request.getParameter( "fromPathItem" );
        String fromAttributeName = request.getParameter( "fromAttribute" );
        docHTML_path.PathItem fromPathItem = null;
        if ( id_fromPathItem != null && !existsPathItem ) // novo ITEM PATH -- colocar parentesco
        {
            fromPathItem = p_path.getPathItemById( id_fromPathItem  );

            fromPathItem.addChildPathItem( pathItem , fromAttributeName );
            pathItem.addParentPathItem( fromPathItem , fromAttributeName );


            while ( p_pathHistory.size()-1 > p_pathHistoryPointer )
            {
                p_pathHistory.remove( p_pathHistoryPointer+1 );
            }
            p_pathHistory.add( pathItem );
            p_pathHistoryPointer = p_pathHistory.size()-1;
        }
        else
        {
            p_pathHistoryPointer = p_pathHistory.indexOf( pathItem );
            if ( p_pathHistoryPointer == -1 && pathItem != null)
            {
                p_pathHistory.add( pathItem );
                p_pathHistoryPointer = p_pathHistory.size()-1;
            }

        }


        if ( request.getParameter( "nextPage" ) != null )
        {


            String newUrl =  java.net.URLDecoder.decode( request.getParameter("nextPage") );



            if(newUrl.indexOf("?")==-1 )
            {
                 newUrl=newUrl+"?fromPathItem="+pathItem.getId();

            }
            else newUrl = newUrl +"&fromPathItem="+pathItem.getId();



            String[] parts=newUrl.split("\\?");

            String[] parms=parts[1].split("\\&");
            int x=-1;
            String fromAttribute = null;
            String fromWhere = null;
            String toBoui=null;
            for (int i = 0; i < parms.length ; i++)
            {
                if ( parms[i].startsWith( "addToCtxParentBridge" ) )
                {
                    fromAttribute = parms[i].split("=")[1];
//                    break;
                }
                else if ( parms[i].startsWith( "fromWhere" ) )
                {
                    fromWhere=parms[i].split("=")[1];
                }
                else if (  parms[i].startsWith( "boui" ) )
                {
                    toBoui=parms[i].split("=")[1];
                }
            }


            if ( fromWhere!= null && toBoui!=null && fromWhere.equals("fromNext") )
            {

              if ( !obj.haveReferencesToObject_in_Memory( ClassUtils.convertToLong( toBoui ) ) )
              {
                   return null;
              }

              newUrl = newUrl.replaceAll("fromWhere=fromNext","" );
            }

            if( fromAttribute != null )
            {
                newUrl+="&fromAttribute="+fromAttribute;
            }

            newUrl+= "&" + ControllerFactory.CONTROLLER_NAME_KEY + "=" + controller.getName();
            if("XwfController".equals(controller.getName()))
            {
                newUrl+= "&" + XwfKeys.PROGRAM_RUNTIME_BOUI_KEY +"="+ request.getParameter(XwfKeys.PROGRAM_RUNTIME_BOUI_KEY);
                newUrl+= "&" +XwfKeys.ACTIVITY_RUNNING_BOUI_KEY +"="+ request.getParameter(XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY);
            }

            return newUrl;
        }


      return null;
  }
  */
//   private ArrayList path_GetComplete(  docHTML_path.PathItem item )
//   {
//
//        ArrayList toRet = new ArrayList();
//        ArrayList parentItems = item.getParents();
//
//
//        if ( parentItems.size() > 1 )
//        {
//            toRet.add("(");
//            for (int i = 0; i < parentItems.size() ; i++)
//            {
//                toRet.add(  parentItems.get(i) );
//                toRet.add(  path_GetComplete( (docHTML_path.PathItem)parentItems.get(i)   ) );
//            }
//
//            toRet.add(")");
//        }
//        else if  ( parentItems.size() == 1 )
//        {
//            toRet.add( parentItems.get(0) );
//
//            docHTML_path.PathItem itemP = (docHTML_path.PathItem)parentItems.get(0);
//            if ( itemP.getParents() != null && itemP.getParents().size() > 0)
//            {
//                toRet.add( path_GetComplete( itemP ) );
//            }
//        }
//
//        return toRet;
//   }

// para sair daqui e ir para a Presentation
//  private StringBuffer path_Render( docHTML_path.PathItem item , ArrayList completePath ) throws boRuntimeException
//  {
//        StringBuffer toRet = new StringBuffer();
//        if ( completePath == null )
//        {
//            completePath = path_GetComplete( item  );
//        }
//
//        docHTML_path.PathItem lastItem = item;
//
//        for (int i = completePath.size()-1; i>=0 ; i--)
//        {
//            Object o = completePath.get( i );
//            if ( o instanceof docHTML_path.PathItem )
//            {
//
//                docHTML_path.PathItem ito = ( docHTML_path.PathItem)o;
//                if(toRet.length() == 0)
//                {
//                    toRet.append("<span class='breadCrumbSep'>::</span>");
//                }
//
//                toRet.append( ito.renderLink( lastItem ) );
//                lastItem = ito;
//                toRet.append("<span class='breadCrumbSep'>:</span>");
//            }
//            else if ( o instanceof ArrayList )
//            {
//               toRet.append( path_Render( null, (ArrayList)o ) );
//            }
//            else
//            {
//                //string
//                toRet.append( o );
//            }
//        }
//
//        if ( item != null )
//        {
//            boObject o = getObject( item.p_relatedBoui );
//            toRet.append("<span class='breadCrumbLastItem'>");
//            toRet.append( o.getCARDIDwNoIMG() );
//            toRet.append("</span>");
//        }
//
//
//        return toRet;
//  }


    public void setMasterObject(boObject object,boolean isMasterDoc)
    {
        if ( object != null && getMasterObject() == null && isMasterDoc)
        {
            p_MasterObject = object;
        }
    }

    public boObject getMasterObject()
    {
        return p_MasterObject;
    }

  public boObjectList getBoObjectListByKey(String key){
      boObjectList ret = p_masterBoList;
      ret=(boObjectList)p_boLists.get(key);
      if(ret==null) ret=p_masterBoList;
      return ret;
  }


  private String getBolistId(long boui,String attribute) throws boRuntimeException {
       String key=boui+".att_"+attribute;
       Object list=p_boLists.get(key);
       if(list==null){
           p_boLists.put(key,boObjectList.list(super.getEboContext(),attribute,boui));
       }

       return key;
   }

   private String getBolistId(String boql) throws boRuntimeException {
       String key="boql:"+boql;
       Object list=p_boLists.get(key);
       if(list==null){
           p_boLists.put(key,boObjectList.list(super.getEboContext(),boql));
       }

       return key;
   }
  private String getBolistId(boObject bo,String attribute) throws boRuntimeException {
       String key=bo.bo_boui+".att_"+attribute;
       Object list=p_boLists.get(key);
       if(list==null){
           p_boLists.put(key,boObjectList.list(super.getEboContext(),attribute,bo.bo_boui));
       }

       return key;
   }

   private String getBolistIdMeth(long boui,String meth, String attName) throws boRuntimeException {
       String key=boui+".meth_"+meth;
       Object list=p_boLists.get(key);
       if(list==null){
           boObjectList l=boListFromMeth(meth,boui,attName);
           if(l==null) key=null;
           else p_boLists.put(key,l);
       }

       return key;
   }


   private boObjectList boListFromMeth(String mth,long boui,String attName) throws boRuntimeException {
      boObjectList toReturn=null;
      try{
            if (mth!=null){
                Object ctxobj;

                ctxobj=this.getObject(boui);

                String method= new String(mth);

                if(ctxobj != null) {
                    int stridx=0;
                    if((stridx=method.indexOf('.',stridx+1))>-1) {

                        String[] xprops = ClassUtils.splitToArray(method,".");
                        int i;
                        boolean end = false;
                        for(i=0;i<xprops.length-1 && !end;i++) {
                            String xprop = xprops[i];
                            if( xprop.indexOf('(') >-1 )
                            {
                                String xname = xprop.substring(0,xprop.indexOf('('));
                                Class[] argsclass = null;
                                String[] methargs = null;
                                //verificação se é boql
                                if(xprop.toUpperCase().indexOf("SELECT") == -1)
                                {
                                    argsclass = null;
                                    methargs = xprop.substring(xprop.indexOf('(')+1,xprop.indexOf(')')).split(",");
                                }
                                else
                                {
                                    //boql
                                    ArrayList arr = new ArrayList();
                                    StringBuffer sb = null;
                                    if(xprops.length == 2)
                                    {
                                        sb = new StringBuffer(xprop.substring(xprop.indexOf('(') + 1,
                                                                              xprop.lastIndexOf(')')));
                                    }
                                    else
                                    {
                                        sb = new StringBuffer(xprop.substring(xprop.indexOf('(') + 1));
                                        for(int j = 1;j < xprops.length - 1; j++)
                                        {
                                            if(xprops[j].indexOf(")") != -1)
                                                sb.append(".").append(xprops[j].substring(0, xprops[j].lastIndexOf(")")));
                                            else
                                                sb.append(".").append(xprops[j]);
                                        }
                                    }
                                    methargs = new String[1];
                                    methargs[0] = sb.toString();
                                    end = true;
                                }
                                if( methargs[0].length() == 0 )
                                {
                                    methargs = new String[0];
                                }
                                argsclass = new Class[methargs.length];
                                for (byte z = 0; z < argsclass.length; z++)
                                {
                                    argsclass[z] = String.class;
                                }

                                Method xmethod = ctxobj.getClass().getMethod(xname,argsclass);
                                ctxobj = xmethod.invoke(ctxobj,methargs);
                                i = xprops.length-2;
                            }
                            else
                            {
                                try
                                {
                                    Method xmeth = ctxobj.getClass().getMethod( xprop , null );
                                    ctxobj = xmeth.invoke( ctxobj, null );
                                }
                                catch (NoSuchMethodException e)
                                {
                                    Field xfld = ctxobj.getClass().getField(xprop);
                                    ctxobj = xfld.get(ctxobj);
                                }
                            }
                        }
                        method = (String)xprops[i];
                    }

                    Method ometh = ctxobj.getClass().getMethod(method,new Class[0]);
                    if(ometh!=null) {
                        Class rettype = ometh.getReturnType();

                        if(!(""+rettype).equals("void") && (rettype.getName().endsWith("boObjectList") || rettype.getSuperclass().getName().endsWith("boObjectList")) ) {
                            String l_ctx = getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());
                            toReturn = (boObjectList)ometh.invoke(ctxobj,new Object[0]);
                            if(attName != null && !"".equals(attName))
                            {
                                toReturn.setOrderBy(attName);
                            }
                            getEboContext().setPreferredPoolObjectOwner(l_ctx);
                        }
                    }
               }

            }
     }
     catch(InvocationTargetException e) {
        String[] args = { convertRequestToString() };
        if( e.getTargetException() != null )
        {
            throw new boRuntimeException(this.getClass().getName()+".buildList","BO-3110",e.getTargetException(),args);
        }
        else
        {
            throw new boRuntimeException(this.getClass().getName()+".buildList","BO-3110",e,args);
        }
     }
     catch(Exception e) {
            String[] args = { convertRequestToString() };
            throw new boRuntimeException(this.getClass().getName()+".buildList","BO-3110",e,args);
     }

     return toReturn;
   }
    private boObjectList boListFromMeth(String mth,String classObj) throws boRuntimeException { //new att:JP
      boObjectList toReturn=null;
      try{
            toReturn=boObjectList.edit( super.getEboContext(),"select "+classObj+" where 0=1");
     }
     catch(Exception e) {
            String[] args = { convertRequestToString() };
            throw new boRuntimeException(this.getClass().getName()+".buildList","BO-3110",e,args);
     }

     return toReturn;
   }



 public void bindData(String XMLValues,docHTML_controler DOCLIST, HttpServletRequest request) throws boRuntimeException {
    try
    {
    boObject lastobj=null;
    try
    {

        long xt = System.currentTimeMillis();
        docTotalRefresh = false;
        parentRefresh = false;
        changedValues = new ArrayList();
        removeGridFromHash();
        this.poolSetStateFull(DOCLIST.poolUniqueId());
        ngtXMLHandler xx=null;
        String buttonPress = null;

        if(XMLValues != null && XMLValues.toUpperCase().startsWith("<WIZARD ") )
        {
            ngtXMLHandler xwiz=(new ngtXMLHandler(XMLValues));
            xx = xwiz.getChildNodes()[0];
            buttonPress = xx.getAttribute("buttonPress", "");
        }
        else
        {
            xx=(new ngtXMLHandler(XMLValues));
        }
        Vector boundobjs=new Vector();



        ngtXMLHandler[] childnodes = xx.getChildNodes();
        if (childnodes.length > 0)
        {
            if("boData".equals(childnodes[0].getNodeName()))
            {
                childnodes = childnodes[0].getChildNodes();
            }
            for (int x = 0; x < childnodes.length; x++)
            {
                lastobj=this.getObject(ClassUtils.convertToLong(childnodes[x].getAttribute("boui"),0));


                bindValues(lastobj,childnodes[x]);
            }

        }

        if(lastobj ==null)
        {
            long boui = ClassUtils.convertToLong(request.getParameter("BOUI"),0);
            if(boui!=0)
            {
                lastobj = this.getObject(boui);
            }
        }

        // Execute actions over all bounded objects



        if(lastobj != null)
        {
            String iMode = super.getEboContext().getRequest().getParameter("boFormSubmitMode");
            String toExecute = super.getEboContext().getRequest().getParameter("toExecute");
            String moveLines = super.getEboContext().getRequest().getParameter("moveLines");
            String applyTemplate = super.getEboContext().getRequest().getParameter("applyTemplate");
            String applyInterface = super.getEboContext().getRequest().getParameter("interface");

            if ( applyInterface != null )
            {
                AttributeHandler intf = lastobj.getAttribute("implements_" + applyInterface);
                if(intf!=null)
                {
                  intf.setValueString("S");
                }
            }

            if ( applyTemplate != null )
            {
                long template = ClassUtils.convertToLong(applyTemplate,-1);
                if(template != -1)
                {
                    lastobj = boTemplateManager.applyTemplates(super.getEboContext(),lastobj, template,this.poolUniqueId(), request);
                }
            }

            if ( moveLines != null )
            {
                if(!lastobj.poolIsStateFull()) {
                    lastobj.poolSetStateFull(this.poolUniqueId());
                }
                long xb=p_PARENT_BOUI;
                String xParent=p_PARENT_ATTRIBUTE;
                bridgeHandler bridge=lastobj.getBridge( xParent );
                String[] smovedLines=moveLines.split("_to_")[0].split(":");
                int moveTo= ClassUtils.convertToInt( moveLines.split("_to_")[1]);
                int[] movedLines= new int[ smovedLines.length-1 ] ;
                for (int i = 1; i < smovedLines.length ; i++)
                {
                    movedLines[i-1]= ClassUtils.convertToInt( smovedLines[i] );

                }

                for (int i = 0; i < movedLines.length; i++)
                {
                    bridge.moveTo( movedLines[i] );
                    bridge.moveRowTo( moveTo );

                    if ( movedLines[i] > moveTo )
                    {
                        moveTo++;
                    }
                    else
                    {
                        if ( i+1< movedLines.length && movedLines[i+1] >moveTo)
                        {
                            moveTo++;
                        }
                        for ( int j=i+1; j< movedLines.length ; j++ )
                        {
                            if ( movedLines[j] < moveTo ) movedLines[j]--;
                        }
                    }
                }

                //bridge.moveRowTo( );

            }
            executeMethod(lastobj,toExecute);
/*
            if( toExecute!=null )
            {
               if(!lastobj.poolIsStateFull()) {
                    lastobj.poolSetStateFull(this.poolUniqueId());
               }


               if( !toExecute.startsWith("STATIC-") )
               {
                try
                {

                    String[] mths=toExecute.split(":");

                    for( int j=0 ; j< mths.length ; j++ )
                    {


                        String method=mths[j];
                        if( method.indexOf('.')==-1 )
                        {
                            boDefMethod def = lastobj.getBoDefinition().getBoMethod(method, new Class[0]);
                            if(def != null && def.getIsMenu()){
                                Method ometh = lastobj.getClass().getMethod(method, new Class[0]);
                                ometh.invoke(lastobj, new Object[0]);
                            }
                            else
                            {
                                if(lastobj.getStateManager() != null)
                                {
                                    Method ometh = lastobj.getStateManager().getClass().getMethod(method,new Class[0]);
                                    ometh.invoke(lastobj.getStateManager() ,new Object[0]);
//                                    lastobj.update();
                                    lastobj.getStateManager().onLoad( lastobj );
                                }
                                else
                                {
                                    Method ometh = lastobj.getClass().getMethod(method, new Class[0]);
                                    ometh.invoke(lastobj, new Object[0]);
                                }
                            }
                        }
                        else
                        {

                               if ( method.startsWith("ATR-") )
                               {
                                    method=method.substring(4);
                                    String attributeName=method.split("\\.")[0];
                                    String xmeth=method.split("\\.")[1];
                                    if (lastobj.getBridge(attributeName).first())
                                    {
                                        netgest.bo.runtime.AttributeHandler attr=lastobj.getBridge(attributeName ).getAttribute(attributeName);
                                        Method ometh=attr.getClass().getMethod( xmeth,new Class[0] );
                                        ometh.invoke(attr  ,new Object[0] );
                                    }

                               }
                               else
                               {
                                    Method ometh = lastobj.getClass().getMethod(method.split("\\.")[1],new Class[0]);
                                    if(ometh!=null)
                                    {
                                          ometh.invoke(lastobj,new Object[0]);
                                          lastobj.getStateManager().onLoad( lastobj );

                                    }
                               }
                        }

                    }
                }
                catch( InvocationTargetException e )
                {
                    String[] args = { toExecute };
                    if( e.getTargetException() instanceof boRuntimeException &&
                        ((boRuntimeException)e.getTargetException()).getErrorCode().equals("BO-3022")
                    )
                    {
                        addErrorMessage("Não possível gravar porque o objecto já foi alterado por outro utilizador.");
                    }
                    throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e.getTargetException(),args);
                }
                catch( Exception e) {
                    String[] args = { toExecute };
                    throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e,args);
                }
               }
               else if( toExecute.startsWith("STATIC-") )
               {
                    String[] statOut = toExecute.split("-");
                    String[] mths = statOut[statOut.length -1].split(";");
                    String[] parms = mths[mths.length -1].split("\\|");
                    String[] path = mths[0].split("\\.");
                    String cl = mths[0].split("." + path[path.length -1])[0];
                    Class clss = Class.forName(cl);
                    Class partypes[] = new Class[parms.length ];
                    for(int i = 0; i < parms.length  ; i++)
                    {
                        if("this".equals(parms[i])){
                            partypes[i] = boObject.class;
                        }
                        else
                        {
                            partypes[i] = String.class;
                        }
                    }
                    Method ometh = clss.getMethod(path[path.length -1],partypes);

                    Object args[] = new Object[parms.length];
                    for(int i = 0; i < parms.length  ; i++)
                    {
                        if("this".equals(parms[i])){
                            args[i] = lastobj;
                        }
                        else if("docid".equals(parms[i])){
                            args[i] = ""+this.getDocIdx();
                        }
                        else if("pooluniquedocid".equals(parms[i])){
                            args[i] = this.poolUniqueId();
                        }
                        else
                        {
                            args[i] = parms[i];
                        }
                    }
                    ometh.invoke(null,args);
               }
//               else
//               {
//                   this.renderErrors(lastobj);
//               }
            }
       */
            if (iMode!=null){
//                    if( iMode.equals("1") || (iMode.equals("10") && request.getParameter("edit_template_boui")==null && request.getParameter("saveTemplate")!=null) )
                      if( iMode.equals("1") ||
                          (iMode.equals("10") && request.getParameter("saveTemplate")!=null) ||
                          iMode.equals("13")) //save de um que não masterobject
                       {
                            iMode1_10_13(lastobj, iMode, DOCLIST, request);
                       }
                    else if( iMode.equals("5") )
                    {
                        // Set Object in Template Mode
                        boTemplateManager.setTemplatesMode(lastobj,true);
                    }
                    else if( iMode.equals("6") )
                    {
                        // Desanexar Template
                        boTemplateManager.removeTemplate(lastobj);
                    }
                    else if( iMode.equals("3") || (iMode.equals("10") && request.getParameter("edit_template_boui")==null && request.getParameter("removeTemplate")!=null) )
                    {
                        if (iMode.equals("10") && request.getParameter("removeTemplate")!=null)
                        {
                            boObject template = this.getObject( ClassUtils.convertToLong((String) request.getParameter("editingTemplate") ) );
                            boTemplateManager.destroyTemplateChilds(template);
                            lastobj = template;
                        }
                        lastobj.destroy();
                    }
                    else if( iMode.equals("10") && lastobj.p_forceCheck && !lastobj.valid())
                    {
                        this.renderErrors(lastobj);
                    }
                    else if( iMode.equals("11"))
                    {
                        //submit das fórmulas
                        if(!lastobj.valid())
                        {
                            this.renderErrors(lastobj);
                        }
                        updateValues(lastobj);
                    }
                    else if( iMode.equals("20"))
                    {
                        //submit das fórmulas
                        if(!lastobj.valid())
                        {
                            this.renderErrors(lastobj);
                        }
                        updateSection(lastobj, buttonPress);
                    }
                    else if( iMode.equals("12"))
                    {
                        //criar esquema
                        SchemaUtils.createSchema(lastobj);
                    }
                    else
                    {
                        //logger.warn("iMode desconhecido :"+iMode);
                          if (request.getParameter("ctxParentIdx")!=null && request.getParameter("ctxParent") != null && request.getParameter("addToCtxParentBridge")!=null )
                          {
                                int docidx= ClassUtils.convertToInt( ((String) request.getParameter("ctxParentIdx")) );
                                long xboui= ClassUtils.convertToLong( ((String) request.getParameter("ctxParent")) );
                                String xbridge= (String ) request.getParameter("addToCtxParentBridge");

                                if (xboui > 0 && xbridge!=null )
                                {
                                    docHTML xpdoc = DOCLIST.getDocByIDX(docidx,super.getEboContext());
                                    boObject xparent=xpdoc.getObject( xboui );
                                    if(xparent.onChangeSubmit(xbridge))
                                    {
                                        cleanFlagToRefresh();
                                        updateValues(lastobj);
                                        parentRefresh = true;
                                    }
                                }
                          }
                    }
                }
//                if( lastobj.getObjectErrors() != null && lastobj.getObjectErrors().size() > 0 )
                if( lastobj.haveObjectErrors() )
                {
                    this.renderErrors( lastobj );
                }
            }
            lastobj.computeSecurityKeys(false);

            logger.info("Binding:"+(System.currentTimeMillis()-xt));
        }
        catch ( boRuntimeException e )
        {
            handleRuntimeException(e,lastobj);
            /*
            if ( e.getErrorCode().equals("BO-3021") )
            {
                if( (lastobj.getAttributeErrors() != null && lastobj.getAttributeErrors().size() > 0 )
                    ||
                    (lastobj.getObjectErrors() != null && lastobj.getObjectErrors().size() > 0 )
                )
                {
                    this.renderErrors( lastobj );
                }
                else
                {
                        addErrorMessage
                        (
                            "Existe um objecto inválido. "+
                            (e.getSrcObject()!=null?"["+e.getSrcObject().getCARDIDwLink(false,"docid="+this.getDocIdx()+"&ctxParentIdx="+this.getDocIdx()+"&noUpdate=y&" )+"]":"")
                        );
                }
            }
            else if ( e.getErrorCode().equals("BO-3023") )
            {
                if( e.getSrcObject().getBoui() == lastobj.getBoui() )
                {
                    addErrorMessage("Não é possível eliminar, o objecto é referenciado por outros.");
                }
                else
                {
                        addErrorMessage
                        (
                            "Não é possível eliminar, o objecto  "+
                            (e.getSrcObject()!=null?"["+e.getSrcObject().getCARDIDwLink(false,"docid="+this.getDocIdx()+"&ctxParentIdx="+this.getDocIdx()+"&noUpdate=y&" )+"]":"")+
                            " é referenciado por outros."
                        );
                }
            }
            else if ( e.getErrorCode().startsWith("BO-32") )
            {
              addErrorMessage("Não tem permissões suficientes para executar essa operação");
            }
            else if ( e.getErrorCode().startsWith("WKFL-1") )
            {
              addErrorMessage(" "+e.getMessage());
            }
            else if ( e.getErrorCode().equals("BO-3022") )
            {
                addErrorMessage("Não é possível gravar porque o objecto já foi alterado por outro utilizador.");
            }
            else if ( e.getErrorCode().equals("BO-3022") )
            {
                addErrorMessage("Não é possível gravar porque o objecto já foi alterado por outro utilizador.");
            }
            else
            {
                throw e;
            }
            */
        }
        }
        catch ( Throwable e )
        {
            CharArrayWriter cr = new CharArrayWriter();
            PrintWriter pw = new PrintWriter( cr );
            e.printStackTrace( pw );
            this.addErrorMessage( "<span style='background-color:#FFFFFF;border:2px solid red' onclick=\"displayXeoError(  errorpre.innerHTML )\" >"+JSPMessages.getString("docHTML.2")+"</span><pre class='error' id='errorpre'> \n" + cr.toString() +"</pre>" );
        }
   }
    public void executeMethod(boObject lastobj , String toExecute) throws boRuntimeException, ClassNotFoundException,NoSuchMethodException,IllegalAccessException,InvocationTargetException
    {
        if( toExecute != null )
        {
           if(!lastobj.poolIsStateFull()) {
                lastobj.poolSetStateFull(this.poolUniqueId());
           }


           if( !toExecute.startsWith("STATIC-") )
           {
            try
            {

                String[] mths=toExecute.split(":");

                for( int j=0 ; j< mths.length ; j++ )
                {


                    String method=mths[j];
                    if( method.indexOf('.')==-1 )
                    {
                        boDefMethod def = lastobj.getBoDefinition().getBoMethod(method, new Class[0]);
                        if(def != null && (def.getIsMenu() || def.getIsToolbar()) ){
                            Method ometh = lastobj.getClass().getMethod(method, new Class[0]);
                            ometh.invoke(lastobj, new Object[0]);
                        }
                        else
                        {
                            if(lastobj.getStateManager() != null)
                            {
                                Method ometh = lastobj.getStateManager().getClass().getMethod(method,new Class[0]);
                                ometh.invoke(lastobj.getStateManager() ,new Object[0]);
//                                    lastobj.update();
                                lastobj.getStateManager().onLoad( lastobj );
                            }
                            else
                            {
                                Method ometh = lastobj.getClass().getMethod(method, new Class[0]);
                                ometh.invoke(lastobj, new Object[0]);
                            }
                        }
                    }
                    else
                    {

                           if ( method.startsWith("ATR-") )
                           {
                                method=method.substring(4);
                                String attributeName=method.split("\\.")[0];
                                String xmeth=method.split("\\.")[1];
                                if (lastobj.getBridge(attributeName).first())
                                {
                                    netgest.bo.runtime.AttributeHandler attr=lastobj.getBridge(attributeName ).getAttribute(attributeName);
                                    Method ometh=attr.getClass().getMethod( xmeth,new Class[0] );
                                    ometh.invoke(attr  ,new Object[0] );
                                }

                           }
                           else
                           {
                                Method ometh = lastobj.getClass().getMethod(method.split("\\.")[1],new Class[0]);
                                if(ometh!=null)
                                {
                                      ometh.invoke(lastobj,new Object[0]);
                                      lastobj.getStateManager().onLoad( lastobj );

                                }
                           }
                    }

                }
            }
            catch( InvocationTargetException e )
            {
                String[] args = { toExecute };
                if( e.getTargetException() instanceof boRuntimeException &&
                    ((boRuntimeException)e.getTargetException()).getErrorCode().equals("BO-3022")
                )
                {
                    addErrorMessage(JSPMessages.getString("docHTML.3"));
                }
                throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e.getTargetException(),args);
            }
            catch( Exception e) {
                String[] args = { toExecute };
                throw new boRuntimeException(this.getClass().getName()+".invokeMethod","BO-3120",e,args);
            }
           }
           else if( toExecute.startsWith("STATIC-") )
           {
                executeStaticMethod(lastobj,toExecute);
/*                String[] statOut = toExecute.split("-");
                String[] mths = statOut[statOut.length -1].split(";");
                String[] parms = mths[mths.length -1].split("\\|");
                String[] path = mths[0].split("\\.");
                String cl = mths[0].split("." + path[path.length -1])[0];
                Class clss = Class.forName(cl);
                Class partypes[] = new Class[parms.length ];
                for(int i = 0; i < parms.length  ; i++)
                {
                    if("this".equals(parms[i])){
                        partypes[i] = boObject.class;
                    }
                    else
                    {
                        partypes[i] = String.class;
                    }
                }
                Method ometh = clss.getMethod(path[path.length -1],partypes);

                Object args[] = new Object[parms.length];
                for(int i = 0; i < parms.length  ; i++)
                {
                    if("this".equals(parms[i])){
                        args[i] = lastobj;
                    }
                    else if("docid".equals(parms[i])){
                        args[i] = ""+this.getDocIdx();
                    }
                    else if("pooluniquedocid".equals(parms[i])){
                        args[i] = this.poolUniqueId();
                    }
                    else
                    {
                        args[i] = parms[i];
                    }
                }
                ometh.invoke(null,args);*/
           }
        }
    }
    private void iMode1_10_13(boObject lastobj, String iMode, docHTML_controler DOCLIST, HttpServletRequest request) throws boRuntimeException
    {
        boObject previous_lastobj=lastobj;

        if (iMode.equals("10") && request.getParameter("saveTemplate")!=null)
        {
            lastobj=this.getObject( ClassUtils.convertToLong((String) request.getParameter("editingTemplate") ) );
        }
//deixou de funcionar
        //String rlb=lastobj.getParameter("relatedObjBOUI");

        if ( lastobj.getName().equals("Ebo_Template") && previous_lastobj!=null && !previous_lastobj.getName().equals("Ebo_Template") )
        {
            boTemplateManager.saveTemplates(lastobj, previous_lastobj);

//            lastobj.getUpdateQueue().clear();
        }
                        // Check if the object is ready to update;
        if ( lastobj.getMode() != boObject.MODE_EDIT_TEMPLATE  )
        {
            if(lastobj.valid())
            {
                boObject relObj = null;
 ///IMPORTANTE
                if (request.getParameter("ctxParentIdx")!=null && request.getParameter("ctxParent") != null && request.getParameter("addToCtxParentBridge")!=null )
                {
                    int docidx= ClassUtils.convertToInt( ((String) request.getParameter("ctxParentIdx")) );
                    long xboui= ClassUtils.convertToLong( ((String) request.getParameter("ctxParent")) );
                    String xbridge= (String ) request.getParameter("addToCtxParentBridge");

                    if (xboui > 0 && xbridge!=null )
                    {
                        docHTML xpdoc = DOCLIST.getDocByIDX(docidx,super.getEboContext());

//                                            lastobj.poolSetStateFull( xpdoc.poolUniqueId() );
//                                            if(!xpdoc.poolIsStateFull()) {
//                                                xpdoc.poolSetStateFull(DOCLIST.poolUniqueId());
//                                            }
                        boObject xparent=xpdoc.getObject( xboui );
                        relObj = xparent;
                        bridgeHandler b = xparent.getBridge( xbridge );
                        // ATENÇAO NOME ctxParentBridge  pode ser attribute

                        if ( b!=null )
                        {



                            //tem que adicionar a thread e não apenas quando não existe na bridge
                            xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                            if(!b.haveBoui(lastobj.getBoui()))
                            {
    //                          if(!xparent.poolIsStateFull()) {
    //                                                  xparent.poolSetStateFull(xpdoc.poolUniqueId());
    //                                                }
                                bridgeHandler xb=xparent.getBridge(xbridge);
                                xb.add(lastobj.getBoui());
                            }
                            if(xparent.onChangeSubmit(xbridge))
                            {
                                cleanFlagToRefresh();
                                updateValues(lastobj);
                                parentRefresh = true;
                            }
                        }
                        else
                        {
                            AttributeHandler atr = xparent.getAttribute( xbridge  );
                            lastobj.poolSetStateFull();
                            if( atr!= null )
                            {
                                atr.setValueLong( lastobj.getBoui() );
                                xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                            }
                        }
                    }
                    else
                    {
                        lastobj.setCheckSecurity(true);
                        lastobj.update();
                        lastobj.setCheckSecurity(false);
                    }
                }
                else
                {
                    if ( request.getParameter("noUpdate")==null )
                    {
                        lastobj.setCheckSecurity(true);
                        lastobj.update();
                        lastobj.setCheckSecurity(false);
                    }
                }
                if(iMode.equals("13")) //salvar o masterupdate
                {
                    boObject masterObject = getMasterObject();
                    if(masterObject != null)
                    {
                        if(masterObject.getName().equals("Ebo_Template"))
                        {
                            if(relObj != null)
                            {
                                boTemplateManager.saveTemplates(masterObject, relObj);
                                masterObject.update();
                            }
                        }
                        else
                        {
                            masterObject = boObject.getBoManager().loadObject(getEboContext(), masterObject.getBoui());
                            masterObject.update();
                        }
                    }
                }
            }
            else
            {
                this.renderErrors(lastobj);
            }
        }
    }

    private void bindValues(boObject ctxobj,ngtXMLHandler xml) throws boRuntimeException
    {
        ngtXMLHandler xv[];
        String attributeName = xml.getAttribute("attributeName", "");
        bridgeHandler bh = null;
        boObject newObject = null;

        //JMF 22-5-2005

                String ctxParentBridge = xml.getAttribute("addToCtxParentBridge");
                String ctxParent = xml.getAttribute("ctxParent");

                if (  ctxParentBridge!=null && ctxParent!=null  )
                {
                        long ctxParentBoui = ClassUtils.convertToLong( ctxParent );
                        boObject lastobj= ctxobj;
                        boObject xparent=this.getObject( ctxParentBoui );
                        bridgeHandler b = xparent.getBridge( ctxParentBridge );
                        // ATENÇAO NOME ctxParentBridge  pode ser attribute

                        if ( b!=null )
                        {


                            //tem que adicionar a thread e não apenas quando não existe na bridge
                            xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                            if(!b.haveBoui(lastobj.getBoui()))
                            {
                                b.add(lastobj.getBoui());
                            }
                            if(xparent.onChangeSubmit( ctxParentBridge ) )
                            {
                                cleanFlagToRefresh();
                                updateValues(lastobj);
                                parentRefresh = true;
                            }
                        }
                        else
                        {
                            AttributeHandler atr = xparent.getAttribute( ctxParentBridge  );
                            lastobj.poolSetStateFull();
                            if( atr!= null )
                            {
                                atr.setValueLong( lastobj.getBoui() );
                                xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                            }
                        }
                }
                //---




        xv=xml.getChildNodes();
        if(xv.length>0) {
            if(!ctxobj.poolIsStateFull()) {
                ctxobj.poolSetStateFull(this.poolUniqueId());
            }
        }
//        boAttributesArray xatts = ctxobj.getAllAttributes();

        for(int i=0; i< xv.length;i++) {
            String pname = xv[i].getNodeName();
            String mode = xv[i].getAttribute("mode","");
            //Vector xatt = tools.Split(pname,"__");
            String[] xatt = pname.split("__");
            if( p_datafields != null && p_datafields.containsKey( pname ) )
            {
                ((ICustomFieldDataBinding)p_datafields.get( pname )).processRequestData(
                    getEboContext(), pname, xv[i].getText()
                );
            }
            
            if(pname.equals("bo"))
            {
                long boui = ClassUtils.convertToLong(xv[i].getAttribute("boui"),0);
                if(boui != 0) {

                   boObject lastobj = ( boui==ctxobj.getBoui()?ctxobj:ctxobj.getObject(boui) );


               //JMF 22-5-2005

              ctxParentBridge = xv[i].getAttribute("addToCtxParentBridge");
               ctxParent = xv[i].getAttribute("ctxParent");

                if (  ctxParentBridge!=null && ctxParent!=null  )
                {
                        long ctxParentBoui = ClassUtils.convertToLong( ctxParent );

                        boObject xparent=this.getObject( ctxParentBoui );
                        bridgeHandler b = xparent.getBridge( ctxParentBridge );
                        // ATENÇAO NOME ctxParentBridge  pode ser attribute

                        if ( b!=null )
                        {




                            if(!b.haveBoui(lastobj.getBoui()))
                            {
                                xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                                b.add(lastobj.getBoui());
                            }
                            if(xparent.onChangeSubmit( ctxParentBridge ) )
                            {
                                cleanFlagToRefresh();
                                updateValues(lastobj);
                                parentRefresh = true;
                            }
                        }
                        else
                        {
                            AttributeHandler atr = xparent.getAttribute( ctxParentBridge  );
                            lastobj.poolSetStateFull();
                            if( atr!= null )
                            {
                                atr.setValueLong( lastobj.getBoui() );
                                xparent.getThread().add( BigDecimal.valueOf( xparent.getBoui() ) , BigDecimal.valueOf( lastobj.getBoui() ) );
                            }
                        }
                }
                //---

                  bindValues( lastobj ,xv[i]);

                }
                else
                {
                    //sem boui
                    if(!"".equals(attributeName))
                    {
                        //objecto novo dentro de um já existente
                        //vou verificar se é um atributo ou uma bridge e criar um novo objecto
                        String obType = xv[i].getAttribute("new","");
                        if( (bh != null) || (mode.equals("add") && (ctxobj.getBridge(attributeName))!= null) )
                        {
                            if(bh == null)
                            {
                                bh = ctxobj.getBridge(attributeName);
                                bh.truncate();
                            }
                            newObject = "".equals(obType) ? bh.addNewObject():bh.addNewObject(obType);
                            bindValues(newObject,xv[i]);
                        }
                        else
                        {
                            AttributeHandler att = ctxobj.getAttribute(attributeName);
                            if( att != null )
                            {
                                newObject = boObject.getBoManager().createObject( ctxobj.getEboContext() , obType);
                                att.setValueLong(newObject.getBoui());
                                bindValues(newObject,xv[i]);
                            }
                            else
                            {
                                logger.debug("Expected bobj");
                            }
                        }
                    }
                    else
                    {
                        //novo objecto
                        String obType = xv[i].getAttribute("new","");
                        newObject = boObject.getBoManager().createObject( ctxobj.getEboContext() , obType);
                        bindValues(newObject,xv[i]);
                    }
                }
            }
            else
            {
                if(xatt.length==3) {
                    //long boui = Long.parseLong((String)xatt.get(1));
                    long boui = Long.parseLong(xatt[1]);
                    if( boui != ctxobj.getBoui() )
                    {
                        ctxobj=ctxobj.getObject(boui);
                        if(!ctxobj.poolIsStateFull()) {
                             ctxobj.poolSetStateFull(this.poolUniqueId());
                        }
//                        xatts = ctxobj.getAllAttributes();
                    }

                    //String name = (String)xatt.get(2);
                    String name = xatt[2];
                    changedValues.add(name);
                    if( mode.equals("add") && ctxobj.getBridge(name)!= null )
                    {
                        bridgeHandler bridhandler = ctxobj.getBridge( name );
                        int maxOc = bridhandler.getDefAttribute().getRuntimeMaxOccurs();
                        if(xv[i].getText() == null)
                        {
                            if(maxOc == 1)
                            {
                                bridhandler.beforeFirst();
                                while(bridhandler.next())
                                {
                                    bridhandler.remove();
                                }
                            }
                        }
                        else if(bridhandler.getRowCount() < maxOc)
                        {
                            bridhandler.add( Long.parseLong( xv[i].getText() ) );
                        }
                        else if(maxOc == 1)
                        {
                            bridhandler.beforeFirst();
                            while(bridhandler.next())
                            {
                                bridhandler.remove();
                            }
                            bridhandler.add( Long.parseLong( xv[i].getText() ) );
                        }
                    }
                    else
                    {
                        AttributeHandler att = ctxobj.getAttribute(name);
                        if( att != null )
                        {
                            String parseValue=xv[i].getText();
                            if(parseValue != null)
                            {
                                if( att.getDefAttribute().getAtributeType()==boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                {
                                    String[] bouis = parseValue.split(";");
                                    for (int z = 0; z < bouis.length ; z++)
                                    {
                                        favoritesLookupManager.addToFavorites( att, ClassUtils.convertToLong( bouis[z] ) );
                                    }

                                }
                                else
                                {
                                    if( parseValue!= null )
                                    {
                                      parseValue=parseValue.replaceAll("_x_62;",">").replaceAll("_x_60;","<").replaceAll("_x_38;","&") ;
                                    }
                                }
                            }
//                            if ( att.getDefAttribute().getValueType() != boDefAttribute.VALUE_CLOB  )
//                            {
//
//                            }


                            if(XwfKeys.CONTROLLER_NAME_KEY.equals(getController().getName()))
                            {
                                if("xwfVarValue".equals(ctxobj.getName()))
                                {
                                    ((XwfController)getController()).getEngine().getBoManager().setValueString(ctxobj,parseValue, AttributeHandler.INPUT_FROM_USER);
                                }
                                else
                                {
                                    att.setValueString( parseValue );
                                }

                            }
                            else
                            {
                                att.setValueString( parseValue );
                            }
                            //calculateFormulas
                            if(att.isBridge() && att.getParent().onChangeSubmit(name))
                            {
                                 parentRefresh = true;
                            }
                        }
                        else
                        {
                            logger.debug("Expected bobj");
                        }
                    }
                }
                //caso não tenha o boui
                //else if(xatt.size()==2)
                else if(xatt.length ==2)
                {
                    //AttributeHandler att = ctxobj.getAttribute((String)xatt.get(1));
                    AttributeHandler att = ctxobj.getAttribute(xatt[1]);
                    if( att != null )
                    {
                        String t = xv[i].getText();
                        if ( xv[i].getAttribute("isXML","false").equals("true"))
                        {
                            URLDecoder x  = new URLDecoder();
                            String     xx = "";
                                t= t.replaceAll("\\+","#_MORE_#");

                            try
                            {
                                xx = x.decode( t, boConfig.getEncoding() );
                            }
                            catch( Exception ex )
                            {
                                System.out.println( ex );
                            }
                                xx=xx.replaceAll("#_MORE_#","+" );
                                att.setValueString( xx  );

                        }
                        else
                        {
                            if ( t == null ) att.setValueString("");
                            else  att.setValueString( xv[i].getText().replaceAll("_x_62;",">").replaceAll("_x_60;","<") );
                        }

                    }
                    else
                    {
                        logger.debug("Expected bobj");
                    }
                }
            }
        }
        if(xv.length == 0 && !"".equals(attributeName) && ctxobj != null)
        {
            String mode = xml.getAttribute("mode", "");
            if("".equals(mode))
            {
                if(ctxobj.getBridge(attributeName)!= null)
                {
                    ctxobj.getBridge(attributeName).truncate();
                }
                else if(ctxobj.getAttribute(attributeName)!=null)
                {
                    ctxobj.getAttribute(attributeName).setValueObject(null);
                }
            }
        }
        ctxobj.computeSecurityKeys( false );

    }

 /**
  * Called to provide information to BoRuntimeException
  *
  */
   private String convertRequestToString(){

       StringBuffer toReturn=new StringBuffer();
       toReturn.append("PAGE PARAMETERS : \n");

       if(p_BOQL!=null){
           toReturn.append("BOQL = ");
           toReturn.append(p_BOQL);
           toReturn.append('\n');
       }

       //NOT YET COMPLETED

       return toReturn.toString();
   }

   private void cleanRequest(){
        p_BOQL=null;
        p_METHOD=null;
        p_BOUI=0;
        p_VALUES=null;
     //   p_CLASSNAME=null;
        p_OBJECT=null;
     //   p_TOACTIVE=0; //method?

        p_PARENT_BOUI=0;
        p_PARENT_ATTRIBUTE=null;
//        p_DOCID=-1;
//        p_PARENT_DOCID=-1;
//        p_NEW_DOC=ClassUtils.NO;



   }

   public void readRequest(HttpServletRequest request){

    /* DEFAULT system parameters values*/
        if(request==null) return;
        cleanRequest();


        p_METH_LIST_PAGENUMBER = ClassUtils.convertToInt(request.getParameter("list_page"),-1);
        p_METH_LIST_PAGESIZE   = ClassUtils.convertToInt(request.getParameter("list_pagesize"),-1);;

        p_METH_LIST_ORDERBY    = request.getParameter("list_orderby");
        p_METH_LIST_FULLTEXT   = request.getParameter("list_fulltext")==null?"":request.getParameter("list_fulltext");
        p_METH_LIST_ALIAS      = request.getParameter("list_alias")!=null && "true".equals(request.getParameter("list_alias"));
        if(p_METH_LIST_ALIAS)
        {
            p_METH_ALIAS_OBJ   =  request.getParameter("alias_object");
        }

        p_METH_LIST_WFIRST_ROWS      = request.getParameter("list_WFirstRows")!=null && ("true".equalsIgnoreCase(request.getParameter("list_WFirstRows"))||"yes".equalsIgnoreCase(request.getParameter("list_WFirstRows")));

        String letter_field =  request.getParameter("list_letter_field");
        String letter_filter = request.getParameter("list_letter");
        if( letter_field != null  && letter_field.trim().length() > 0 &&
            letter_filter != null && letter_filter.trim().length() > 0
            )
        {
            p_METH_LIST_LETTER = new String[] { letter_field , letter_filter };
        }
        else
        {
            p_METH_LIST_LETTER = null;
        }
        p_METH_LIST_USER_QUERY= request.getParameter("userQuery");
//        p_DOCID=ClassUtils.convertToInt(request.getParameter("docid"),-1);
//        p_PARENT_DOCID=ClassUtils.convertToInt(request.getParameter("parent_docid"),-1);
        p_VALUES = request.getParameter("boFormSubmitXml");
        p_BOUISLIST = request.getParameter("bouislist");
        if(p_VALUES!=null && p_VALUES.length()==0)
        {
            p_VALUES=null;
        }
        p_METHOD=request.getParameter("method");
        p_OBJECT=request.getParameter("object");
        p_TEMPLATE_DUPLICATE = null;
        if("duplicate".equals(p_METHOD) || "forward".equals(p_METHOD)
            || "reply".equals(p_METHOD) || "replyAll".equals(p_METHOD))
        {
            p_OBJECT_TO_DUPLICATE=ClassUtils.convertToLong(request.getParameter("duplicateBoui"),0);
            p_TEMPLATE_DUPLICATE=request.getParameter("template");
        }
        if("execute".equals(p_METHOD))
        {
            p_FWD_BOUI=ClassUtils.convertToLong(request.getParameter("fwdBoui"),0);
        }
     //   p_TEMPLATE=request.getParameter("template");


        if(p_METHOD==null) p_METHOD="list";
        //else p_METHOD=p_METHOD.toUpperCase();



        p_PARENT_ATTRIBUTE=request.getParameter("parent_attribute");
        p_LIST_FROMMETHOD = request.getParameter("list_frommethod");
        p_DROPINFO = request.getParameter("drop_info");
        p_BOQL=request.getParameter("boql");
        p_PARENT_BOUI=ClassUtils.convertToLong(request.getParameter("parent_boui"),0);
        p_BOUI=ClassUtils.convertToLong(request.getParameter("boui"),0);

//        p_NEW_DOC=ClassUtils.convertToByte(request.getParameter("new_doc"),ClassUtils.NO);

   }




   public docHTML_grid getGRID(String xkey)
   {
      return (docHTML_grid)p_grids.get(xkey);
   }
   public docHTML_groupGrid getGroupGRID(String xkey)
   {
      return (docHTML_groupGrid)p_groupGrids.get(xkey);
   }

   public docHTML_grid createGRID(String xkey,String template)
   {
      docHTML_grid x=new docHTML_grid(template,this, p_values);
      p_grids.put(xkey,x);
      return x;
   }

   public docHTML_grid createGRID(String xkey,String template,boolean listBridgeAttributes)
   {
      docHTML_grid x=new docHTML_grid(template,this,listBridgeAttributes, p_values);
      p_grids.put(xkey,x);
      return x;
   }
    public docHTML_groupGrid createGroupGRID(String xkey)
    {
      docHTML_groupGrid x=new docHTML_groupGrid(this);
      p_groupGrids.put(xkey,x);
      return x;
    }

   public docHTML_section getSection(String xkey)
   {
      return (docHTML_section)p_sections.get(xkey);
   }

   public docHTML_section createSection(String xkey,String template)
   {
      docHTML_section x=new docHTML_section(template, p_values);
      p_sections.put(xkey,x);
      return x;
   }

   public docHTML_section createSection(String xkey,String template,boolean showlabel, int space_before)
   {
      docHTML_section x=new docHTML_section(template , showlabel , space_before, p_values);
      p_sections.put(xkey,x);
      return x;
   }

   public String getMenuWebForm( boObject BOI , HttpServletRequest request , docHTML_controler DOCLIST,String toSay ) throws boRuntimeException
   {

       if ( p_WebForm )
       {


        return docHTML_builderMenus.menuWEBFORM_OBJECT_NORMAL( this ,DOCLIST ,BOI, request , toSay );


       }

       return "";
   }


   public String getDocTemplateDir()
   {
       return "templates/doc/std/";
   }


   public int getDocIdx()
   {
        //p_bocidx = xvalue;
        return p_id;
   }



  public int getHEIGHT_HTMLforToolbar( PageContext pageContext,boObjectList currObjectList){

     if( currObjectList.getFormat()==boObjectList.FORMAT_ONE){
          //Hashtable[] menus=createMenusFORMATONE(currObjectList);
          //docHTMLgenerateToolBar.render(menus,"texto",pageContext,currObjectList );
          return 48;
      }
      else{
           return 24;
      }
   }

    public void writeHTMLforToolbar(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList)throws boRuntimeException,java.io.IOException{
       String textTitle="";
       if ( currObjectList.getFormat()==boObjectList.FORMAT_ONE){
            StringBuffer toRet=new StringBuffer();
//            if ( currObjectList.getObject().getStringComposedState().length() >5 )
//            {
//
            toRet.append("<span style=''><img align='absmiddle' hspace='1' src='resources/");
            toRet.append(currObjectList.getObject().getStringComposedState());
            toRet.append(".gif' width=16 height=16 /></span>");
          //  }
           textTitle=currObjectList.getObject().bo_boui+toRet.toString();

           if( currObjectList.getObject().getAttribute("nrdoc") !=null )
           {

           }

       }
       docHTMLgenerateToolBar.render( docHTML_builderMenus.createMenu( this , DOCLIST, currObjectList , pageContext ) ,
                                    textTitle,
                                    pageContext,currObjectList );
   }

   public void writeHTMLforToolbar(docHTML_controler DOCLIST, PageContext pageContext,boObjectList currObjectList, int hui)throws boRuntimeException,java.io.IOException{
       String textTitle="";
       if ( currObjectList.getFormat()==boObjectList.FORMAT_ONE){
           textTitle=""+currObjectList.getObject().bo_boui;
       }
       docHTMLgenerateToolBar.render( docHTML_builderMenus.createMenu( this , DOCLIST, currObjectList , pageContext, hui ) ,
                                    textTitle,
                                    pageContext,currObjectList );
   }



   public boolean hasCategoryRights(String categ)  throws boRuntimeException, SQLException
   {

      boDefViewer xviewer=p_masterBodef.getViewer("general"); // a ser trabalhado
      boolean hasatr=false;
      if ( xviewer!=null )
      {
          if( xviewer.getCategory( categ ) != null )
          {
              String attr[]=xviewer.getCategory( categ ).getAttributesName();
              for (int i = 0; i < attr.length ; i++)
              {
                   if (  p_masterBodef.hasAttribute( attr[i] )  )
                   {
                       hasatr=true;
                       if ( securityRights.hasRights( this.getEboContext() , p_masterBodef.getName() , attr[i] , securityRights.READ ) )
                       {
                         return true;
                       }
                   }
              }
          }
      }
      return !hasatr;

   }

   public boolean hasCategoryRights(String categ, boObject o)  throws boRuntimeException, SQLException
   {
       //return true;

    //  String xv     =o.getViewerName();
      if ( o!=null )
      {
          boDefViewer xviewer=p_masterBodef.getViewer("general");
          boolean hasatr=false;
          if ( xviewer!=null )
          {
              if( xviewer.getCategory( categ ) != null )
              {
                  String attr[]=xviewer.getCategory( categ ).getAttributesName();

                  for (int i = 0; i < attr.length ; i++)
                  {
                       AttributeHandler at= o.getAttribute( attr[i] );
                       if ( at != null )
                       {
                            hasatr=true;
                           if ( at.canAccess() )
                           {
                             return true;
                           }
                       }
                  }
              }
          }
          return !hasatr;
      }
      else
      {
          return hasCategoryRights( categ);
      }

   }


   public String getCategoryLabel_for_TAB_Header(String categ) throws boRuntimeException{
      // bolist.getObject().bo_definition.getBoViewers()

     // String xv=p_masterBoList.getObject().getViewerName();

      boDefViewer           xviewer=p_masterBodef.getViewer("general");
      boDefViewerCategory   xcat=xviewer.getCategory( categ );
      if(xcat!=null) return xcat.getLabel();
      else return "";
   }

    public static String getCategoryLabel_for_TAB_Header(String categ, boObject o) throws boRuntimeException
    {
      // bolist.getObject().bo_definition.getBoViewers()

   //   String xv=o.getViewerName();

      boDefViewer xviewer=o.getBoDefinition().getViewer("general");
      boDefViewerCategory xcat=xviewer.getCategory(categ);
      if(xcat!=null) return xcat.getLabel();
      else return "";

   }
   public String getCategoryLabel(String ViewerName,String categ) throws boRuntimeException{
    //  String xv=p_masterBoList.getObject().getViewerName();
  //    boDefViewer xviewer=bolist.getObject().getBoDefinition().getViewer(xv);
       boDefViewer xviewer=p_masterBodef.getViewer("general");
       boDefViewerCategory xcat=xviewer.getCategory(categ);
       if(xcat!=null) return xcat.getLabel();
       else return "";
   }

   public static String getCategoryLabel(boObject bo , String ViewerName,String categ) throws boRuntimeException{
   //   String xv=bo.getViewerName();
  //    boDefViewer xviewer=bolist.getObject().getBoDefinition().getViewer(xv);
       boDefViewer xviewer=bo.getBoDefinition().getViewer("general");
       boDefViewerCategory xcat=xviewer.getCategory(categ);
       if(xcat!=null) return xcat.getLabel();
       else return "";
   }

   public String getCategoryDescription(String ViewerName,String categ)throws boRuntimeException {
    //  String xv=p_masterBoList.getObject().getViewerName();
  //    boDefViewer xviewer=bolist.getObject().getBoDefinition().getViewer(xv);
       boDefViewer xviewer=p_masterBodef.getViewer("general");
      boDefViewerCategory xcat=xviewer.getCategory(categ);
      if(xcat!=null) return xcat.getDescription();
      else return "";
   }
   public String getCategoryToolTip(String ViewerName,String categ) throws boRuntimeException{
   //   String xv=p_masterBoList.getObject().getViewerName();
  //    boDefViewer xviewer=bolist.getObject().getBoDefinition().getViewer(xv);
    boDefViewer xviewer=p_masterBodef.getViewer("general");
      boDefViewerCategory xcat=xviewer.getCategory(categ);
      if(xcat!=null) return xcat.getTooltip();
      else return "";

   }
    public void addErrorMessage(String message)
    {
        //p_errors = new StringBuffer();
        p_errors
        .append("<span class='error' >")
        .append(message)
        .append("</span>\n");
    }

    public void renderErrors( boObject obj )
    {

        p_errors = new StringBuffer();
        ArrayList list = obj.getObjectErrors();
        if( list != null )
        {
            ListIterator ilt = list.listIterator();
            while ( ilt.hasNext() )
            {
                p_errors.append("<span class='error' >")
                .append(ilt.next())
                .append("</span>\n");
            }
        }

        Hashtable ht = obj.getAttributeErrors();

        if( ht != null )
        {
            Enumeration oEnum = ht.keys();
            while ( oEnum.hasMoreElements() )
            {
                AttributeHandler att = ( AttributeHandler )  oEnum.nextElement();

                p_errors
                .append( "<span class='error'   oncclick=\"activeField('" )
                .append( att.getName() )
                .append( "')\" >" )
                .append( att.getDefAttribute().getLabel() )
                .append( " : " )
                .append( ht.get( att ) )
                .append("</span>\n");
            }
        }
        obj.clearErrors();

    }

   public String getHTMLErrors(){
       //por cada erro enviar string  se for field name senão...
       //<span class='error'  onclick="activeField('name1')" >Campo name necesssita de estar preenchido</span>
       StringBuffer header = new StringBuffer("<span style='font:13px'>&nbsp;&nbsp;<b>"+JSPMessages.getString("docHTML.1")+":</b></span><br>");//"Não poderá executar a operação equanto não resolver os seguintes erros:<br/>");
       header.append(p_errors);
       return header.toString();
   }
   public boolean haveErrors() {
    //
    return  p_errors.length() > 0;

   }




    public String getScriptToRunOnClient() {
     //   p_isnew=false;
     return "function render(){}";

    }
    public String getActionOnClient() {
        return "render();";
    }


    // Specific methos for object Activate and deActivate;

    public void poolObjectActivate() {

    }
    public void poolObjectPassivate() {
//        p_lasttemplateobui=0;
//        p_lasttemplateobjectname="";
//        ignoreClose = false;  // Flag ignore client request's for close.
//        toClose = new Vector(); // doc's idx to close when this is close.

    }

    public EboContext removeEboContext() {
        // TODO:  Override this netgest.bo.system.boPoolable method
        p_boLists.clear();
        p_masterBoList=null;

       return super.removeEboContext();
    }

    public void setEboContext(EboContext boctx) {
        // TODO:  Override this netgest.bo.system.boPoolable method
        super.setEboContext(boctx);
        if(p_errors!=null)
        p_errors.delete(0,p_errors.length()); // Clear previous errors.
    }

    public boObject getObject(long boui) throws boRuntimeException
    {
        // TODO:  Override this netgest.bo.runtime.boObjectContainer method
        String l_eboctx=super.getEboContext().setPreferredPoolObjectOwner(this.poolUniqueId());
        boObject ret=null;
        try
        {
            if(p_METH_USE_SECURITY) {
                try {
                    ret = boObject.getBoSecurityManager().loadObject(getEboContext(), boui);
                } catch (boRuntimeException e) {
                    if("BO-3220".equals(e.getErrorCode())) {
                        ret=boObject.getBoManager().loadObject( getEboContext(), boui );
                        ret.setSendRedirect("../dialogBoxSecurityWarning.htm?__XEOForwared=1");
                        /*super.getEboContext().getResponse().sendRedirect("dialogBoxSecurityWarning.htm");
                        return null;*/
                    }
                }
            }
            else
                ret=boObject.getBoManager().loadObject( getEboContext(), boui );
        }
        finally
        {
            super.getEboContext().setPreferredPoolObjectOwner(l_eboctx);
        }
        return ret;
    }

    public Hashtable getValues() throws boRuntimeException
    {
        return p_values;
    }





    private void updateValues(boObject obj) throws boRuntimeException
    {
        boAttributesArray atts = obj.getAttributes();
        Enumeration xenum = atts.elements();
        AttributeHandler xat;
        String xname;
        HtmlField hf;
        try
        {
            if("message".equals(obj.getName()) ||
            "message".equals(obj.getBoDefinition().getBoSuperBo())
            )
            {
                StringBuffer sb = new StringBuffer();
                PostInformation.generateMessageInformation(sb, obj, this.getEboContext());
                if(sb.length() > 0)
                {
                    insertMessageIntoHash(obj, sb.toString());
                }
                else
                {
                    insertMessageIntoHash(obj, "");
                }
            }

        while(xenum.hasMoreElements())
        {

            xat=(AttributeHandler)xenum.nextElement();
            xname = xat.getName();
            if (
                (  xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_ATTRIBUTE ||
                   xat.getDefAttribute().renderAsLov() )
                )
            {//atributos simples
                insertIntoHash(obj, xname, xat, null);

            }
            else if(xat.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE &&
                    xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1)
            {//frames
                String ss = xat.getDefAttribute().getType();
                StringBuffer frameCode = new StringBuffer();
                StringBuffer nameH = new StringBuffer();
                StringBuffer nameInHash = new StringBuffer();
                StringBuffer id = new StringBuffer();
                nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( xat.getName() );
                nameInHash.append( obj.getName() ).append( "_" ).append( obj.bo_boui ).append("_").append( xname );
                id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( xat.getName() );
                if(p_values.get(nameInHash.toString()) != null)
                {
                    int tabIndex = 1;
                    HtmlField auxField = (HtmlField)p_values.get(nameInHash.toString());
                    if(auxField != null)
                    {
                        tabIndex = auxField.getTab();
                    }
                    netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(
                            frameCode,
                            obj,
                            xat,
                            new StringBuffer(xat.getValueString()),
                            nameH,
                            id,
                            tabIndex,
                            this,
                            xat.disableWhen(),
                            xat.isVisible(),
                            obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,true, null
                            );
                    insertIntoHash(obj, xname, xat, frameCode.toString());
                }
            }
            else if(xat.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE)
            {
                StringBuffer frameCode = new StringBuffer();
                StringBuffer nameH = new StringBuffer();
                StringBuffer id = new StringBuffer();
                nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( xat.getName() );
                id.append("inc_").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( xat.getName() );
                //if(p_values.get(xname) != null)
                //{
                StringBuffer nameInHash = new StringBuffer();
                nameInHash.append( obj.getName() ).append( "_" ).append( obj.bo_boui ).append("_").append( xname );
                if(p_values.get(nameInHash.toString()) != null)
                {
                    insertIntoHash(obj, xname, xat, frameCode.toString());
                }

                    bridgeHandler bh;
                    String attChanged;
                    for(int i = 0; i <  changedValues.size(); i++)
                    {
                        attChanged = "parent_" + obj.getName() + "." + changedValues.get(i);
                        bh = obj.getBridge(xname);
                        bh.beforeFirst();
                        //if(!bh.haveVL() && bh.next())
                        if( bh.next())
                        {
                            if(bh.getObject().onChangeSubmit(attChanged))
                            {
                                insertGridIntoHash(id.toString());
                            }
                        }
                    }
                //}
            }
        }
        }
        catch(Exception e)
        {
            String s = e.getMessage();
            e.printStackTrace();
        }
    }

    private void insertMessageIntoHash(boObject obj, String frameCode) throws boRuntimeException
    {
        HtmlField hf = (HtmlField)p_values.get("setMessageInformation");
        if(hf == null)
        {
            hf = new HtmlField(null, "setMessageInformation", null, false, false, false);
        }
        int hashcode = hf.getHashcode();
        hf.setValue(frameCode);

        if(hashcode != hf.getHashcode())
        {
            hf.setToRefresh(true);
        }

        p_values.put("setMessageInformation", hf);
    }

    private void insertIntoHash(boObject obj, String _xname, AttributeHandler xat, String frameCode) throws boRuntimeException
    {
        String xname = obj.getName() + "_" + obj.getBoui() + "_" + _xname;
        HtmlField hf = (HtmlField)p_values.get(xname);
        if(hf != null)
        {
            int hashcode = hf.getHashcode();

            boolean req = xat.required();
            boolean disab = xat.disableWhen();
            boolean hidden = !xat.isVisible();
            boDefXeoCode xCode = xat.getDefAttribute().getTotalRefresh();
            boolean attTotalRefresh = false;
            if(xCode != null && "true".equalsIgnoreCase(xCode.getSource()))
            {
                attTotalRefresh = true;
            }

            if(hidden != hf.getHidden() || "true".equalsIgnoreCase(obj.getParameter("totalRefresh"))
            )
            {
                docTotalRefresh = true;
                obj.removeParameter("totalRefresh");
            }

            hf.setDisable(disab);
            hf.setHidden(hidden);
            hf.setRequired(req);

            String val = frameCode == null ? xat.getValueString() : frameCode;
            String type = xat.getDefAttribute().getType().toLowerCase();
            if(xat.getDefAttribute().getType().toLowerCase().equals("date") ||
               xat.getDefAttribute().getType().toLowerCase().equals("datetime")
            )
            {
                val = boConvertUtils.convertToStringYFirst(xat.getValueDate(), xat);
            }
            hf.setValue(val);

            if(hashcode != hf.getHashcode())
            {
                hf.setToRefresh(true);
                if(attTotalRefresh)
                {
                    docTotalRefresh = true;
                    obj.removeParameter("totalRefresh");
                }
            }

            p_values.put(xname, hf);
        }
    }

    private void insertGridIntoHash(String xname) throws boRuntimeException
    {
        HtmlField hf = new HtmlField(xname, xname, xname, false, false, false);
        hf.setToRefresh(true);
        p_values.put(xname, hf);
    }

    private void removeGridFromHash()
    {
        try
        {
        Set keys = p_values.keySet();
        Iterator it = keys.iterator();
        String aux;
        while(it.hasNext())
        {
            aux = (String)it.next();
            if(aux != null && aux.startsWith("inc_"))
            {
                p_values.remove(aux);
            }
        }
        }catch(Exception e)
        {
            logger.error(e, e);
        }
    }

    public boolean totalRefresh()
    {
        return docTotalRefresh;
    }

    public boolean parentRefresh()
    {
        return parentRefresh;
    }

    private boObject duplicateObject( boObject toDuplicate) throws boRuntimeException
    {

        boObject newObject = boObject.getBoManager().createObject(toDuplicate.getEboContext(), toDuplicate);
        return newObject;
    }

    private void cleanFlagToRefresh()
    {
         try
        {
            Set keys = p_values.keySet();
            Iterator it = keys.iterator();
            HtmlField htmlf;
            String aux;
            while(it.hasNext())
            {
                htmlf = (HtmlField)p_values.get((String)it.next());
                htmlf.setToRefresh(false);
            }
        }catch(Exception e)
        {
            logger.error(e, e);
        }
    }

    public boThread getThread(  )
    {
        if( p_thread == null )
        {
            p_thread = new boThread();
        }
        return p_thread;
    }

    public String poolUniqueId()
    {
        //  Override this netgest.bo.system.boPoolable method
         return p_poolUniqueId;
        //return "DOCHTML["+getDocIdx()+"]"+this.hashCode();
    }

    public int getTabindex(byte type, String objName, String boui, String attName, docHTML_controler docList)
    {
        StringBuffer key = new StringBuffer();
        if(type == FIELD)
        {
            key.append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        else if(type == GRID_CHECK_ALL)
        {
            key.append("GRID_CHECK_ALL").append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        if(type == IFRAME)
        {
            key.append("IFRAME").append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        if(type == BUTTON)
        {
            key.append("BUTTON").append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        if(type == AREA)
        {
            key.append("AREA").append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        if(type == PANEL)
        {
            key.append("PANEL").append( objName ).append( "_" ).append( boui ).append("_").append( attName );
        }
        Integer ret = (Integer)p_tabindex.get(key.toString());
        if(ret == null)
        {
            ret = new Integer(docList.tabindex++);
            p_tabindex.put(key.toString(), ret);
        }
        return ret.intValue();
    }


    public int getTabindex(byte type, docHTML_controler docList)
    {
        return getTabindex(type, "",docList);
    }

    public int getTabindex(byte type, String id, docHTML_controler docList)
    {
        StringBuffer key = new StringBuffer();
        if(type == GRID_CHECK_ALL)
        {
            key.append("GRID_CHECK_ALL").append(id);
        }
        if(type == IFRAME)
        {
            key.append("IFRAME").append(id);
        }
        if(type == SEARCH)
        {
            key.append("SEARCH").append(id);
        }
        if(type == FIELD)
        {
            key.append("FIELD").append(id);
        }
        if(type == IMAGE)
        {
            key.append("IMAGE").append(id);
        }
        Integer ret = (Integer)p_tabindex.get(key.toString());
        if(ret == null)
        {
            ret = new Integer(docList.tabindex++);
            p_tabindex.put(key.toString(), ret);
        }
        return ret.intValue();
    }

	public String getCheckedValues()
	{
		return p_VALUES;
	}
    public void setController(Controller controller)
    {
        this.controller = controller;
        super.getEboContext().setController(controller);
    }
    public Controller getController()
    {
        return controller;
    }
    public Hashtable getControllerQueue()
    {
        return controllerQueue;
    }
    public String getMethod()
    {
        return p_METHOD;
    }
    public void setMasterBoList(boObjectList list)
    {
        this.p_masterBoList = list;
    }
    public boObjectList getMasterBoList()
    {
        return this.p_masterBoList;
    }
    public String getObjectType()
    {
        return this.p_OBJECT;
    }
    public void releaseObjects()
    {
        getEboContext().getApplication().getMemoryArchive().getPoolManager().realeaseObjects(this.poolUniqueId(),getEboContext());
    }
    private boolean executeStaticMethod(boObject lastobj,String toExecute) throws ClassNotFoundException,NoSuchMethodException,InvocationTargetException,IllegalAccessException
    {
        boolean result = false;
        String[] statOut = toExecute.split("-");
        String[] mths = statOut[statOut.length -1].split(";");
        String[] parms = mths[mths.length -1].split("\\|");
        String[] path = mths[0].split("\\.");
        String cl = "";
        for (int i = 0; i < path.length -1; i++)
        {
            cl +=path[i];
            if((i+1)< (path.length -1))
            {
                cl += ".";
            }
        }

        Class clss = Class.forName(cl);
        Class partypes[] = new Class[parms.length ];
        for(int i = 0; i < parms.length  ; i++)
        {
            if("this".equals(parms[i])){
                partypes[i] = boObject.class;
            }
            else
            {
                partypes[i] = String.class;
            }
        }
        Method ometh = clss.getMethod(path[path.length -1],partypes);

        Object args[] = new Object[parms.length];
        for(int i = 0; i < parms.length  ; i++)
        {
            if("this".equals(parms[i])){
                args[i] = lastobj;
            }
            else if("docid".equals(parms[i])){
                args[i] = ""+this.getDocIdx();
            }
            else if("pooluniquedocid".equals(parms[i])){
                args[i] = this.poolUniqueId();
            }
            else
            {
                args[i] = parms[i];
            }
        }
        ometh.invoke(null,args);
        result = true;
        return result;
    }
    private void handleRuntimeException(Exception exception, boObject lastobj) throws boRuntimeException
    {
        boolean handleDone = false;
        if(exception instanceof boRuntimeException)
        {
            boRuntimeException boException = (boRuntimeException)exception;
            if ( boException.getErrorCode().equals("BO-3021") )
            {
                if( (lastobj.getAttributeErrors() != null && lastobj.getAttributeErrors().size() > 0 ) ||
                    (lastobj.getObjectErrors() != null && lastobj.getObjectErrors().size() > 0 ))
                {
                    this.renderErrors( lastobj );
                }
                else
                {
                        this.addErrorMessage
                        (
                            JSPMessages.getString("docHTML.4")+
                            (boException.getSrcObject()!=null?"["+boException.getSrcObject().getCARDIDwLink(false,"docid="+this.getDocIdx()+"&ctxParentIdx="+this.getDocIdx()+"&noUpdate=y&" )+"]":"")
                        );
                }
                handleDone = true;
            }
            else if (boException.getErrorCode().equals("BO-3023") )
            {
                if( boException.getSrcObject().getBoui() == lastobj.getBoui())
                {
                    this.addErrorMessage(JSPMessages.getString("docHTML.5"));
                }
                else
                {
                        this.addErrorMessage
                        (
                            JSPMessages.getString("docHTML.6")+
                            (boException.getSrcObject()!=null?"["+boException.getSrcObject().getCARDIDwLink(false,"docid="+this.getDocIdx()+"&ctxParentIdx="+this.getDocIdx()+"&noUpdate=y&" )+"]":"")+
                            JSPMessages.getString("docHTML.7")
                        );
                }
                handleDone = true;
            }
            else if (boException.getErrorCode().equals("BO-32") )
            {
                this.addErrorMessage(JSPMessages.getString("docHTML.8"));
                handleDone = true;
            }
            else if (boException.getErrorCode().startsWith("WKFL-1") )
            {
                this.addErrorMessage(exception.getMessage());
                handleDone = true;
            }
            else if (boException.getErrorCode().equals("BO-3022") )
            {
                this.addErrorMessage(JSPMessages.getString("docHTML.3"));
                handleDone = true;
            }
            else if(exception.getMessage() != null && boException.getErrorCode().equals("BO-3055") && boException.getMessage().indexOf("ORA-00001") != -1)
            {
                this.addErrorMessage(JSPMessages.getString("docHTML.9"));
                handleDone = true;
            }
        }

        if(!handleDone)
        {
            CharArrayWriter cr = new CharArrayWriter();
            PrintWriter pw = new PrintWriter( cr );
            exception.printStackTrace( pw );
            this.addErrorMessage( "<span style='background-color:#FFFFFF;border:2px solid red' onclick=\"displayXeoError(  errorpre.innerHTML )\" >"+JSPMessages.getString("docHTML.2")+"</span><pre class='error' id='errorpre'> \n" + cr.toString() +"</pre>" );
        }
    }
    /**
     * É utilizado no Ebo_History, sendredirect
     * @param object
     */
    public void setMasterObjectOverriding(boObject object)
    {
        p_MasterObject = object;
        setController(null);
        getControllerQueue().clear();
    }

    private void updateSection(boObject obj, String buttonPress) throws boRuntimeException
    {
        boDefViewer boViewer[] = obj.getBoDefinition().getBoViewers();
        String sectionShowing = null;
        if(p_wiz == null || "start".equals(buttonPress))
        {
            p_wiz =  new docHtml_wizard(obj, obj.getBoDefinition());
        }
        else
        {
            sectionShowing = p_wiz.popFromStack();
            p_wiz.putOnStack(sectionShowing);
        }

        ArrayList names = p_wiz.getSectionNames();
        boolean value = false;
        if(sectionShowing == null || "".equals(sectionShowing))
        {
            for (int i = 0; i < names.size(); i++)
            {
                if(value == true)
                {
                    insertIntoHash(obj, (String)names.get(i), true, false, false,false);
                }
                else
                {
                    value = p_wiz.isShowing((String)names.get(i), obj);

                    if(value)
                    {
                        p_wiz.putOnStack((String)names.get(i));
                        boolean nextDisabled = p_wiz.isLastSection((String)names.get(i), obj);
                        boolean endDisabled = !nextDisabled ? p_wiz.isEnd((String)names.get(i), obj):false;
                        boolean previousDisabled = true;
                        insertIntoHash(obj, (String)names.get(i), !value, previousDisabled, nextDisabled, endDisabled);
                    }
                    else
                    {
                        insertIntoHash(obj, (String)names.get(i), true, false, false, false);
                    }
                }
            }
        }
        else
        {
            int pos = names.indexOf(sectionShowing);
            String msgError = p_wiz.isValid(sectionShowing, obj);
            if(msgError != null && msgError.trim().length()>0)
            {
                insertMessageIntoHash(obj, msgError);
                return;
            }
            else
            {
                insertMessageIntoHash(obj, "");
            }
            if("buttonPrevious".equalsIgnoreCase(buttonPress))
            {
                String previousSection = p_wiz.popFromStack();
                previousSection = p_wiz.popFromStack();
                p_wiz.putOnStack(previousSection);
                if(previousSection != null)
                {
                    for (int i = 0; i < names.size(); i++)
                    {
                        if(previousSection.equals((String)names.get(i)))
                        {
                            boolean nextDisabled = p_wiz.isLastSection((String)names.get(i), obj);
                            boolean endDisabled = !nextDisabled ? p_wiz.isEnd((String)names.get(i), obj):false;
                            boolean previousDisabled = !p_wiz.hasPrevious();
                            insertIntoHash(obj, (String)names.get(i), false, previousDisabled, nextDisabled, endDisabled);
                        }
                        else
                        {
                            insertIntoHash(obj, (String)names.get(i), true, false, false, false);
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < pos+1; i++)
                {
                    insertIntoHash(obj, (String)names.get(i), true, false, false, false);
                }
                for (int i = pos+1; i < names.size(); i++)
                {
                    if(value == true)
                    {
                        insertIntoHash(obj, (String)names.get(i), true, false, false, false);
                    }
                    else
                    {
                        value = p_wiz.isShowing((String)names.get(i), obj);
                        if(value)
                        {
                            p_wiz.putOnStack((String)names.get(i));
                            boolean nextDisabled = p_wiz.isLastSection((String)names.get(i), obj);
                            boolean endDisabled = !nextDisabled ? p_wiz.isEnd((String)names.get(i), obj):false;
                            boolean previousDisabled = !p_wiz.hasPrevious();
                            insertIntoHash(obj, (String)names.get(i), false, previousDisabled, nextDisabled, endDisabled);
                        }
                        else
                        {
                            insertIntoHash(obj, (String)names.get(i), true, false, false, false);
                        }
                    }
                }
            }
        }

    }

    private void insertIntoHash(boObject obj, String sectionName, boolean hidden, boolean buttonPrevious, boolean buttonNext, boolean buttonEnd) throws boRuntimeException
    {
        HtmlField hf = (HtmlField)p_values.get(sectionName);
        if(hf == null)
        {
            hf = new HtmlField("", sectionName, "", false, false, true);
            hf.setToRefresh(true);
        }
        int hashcode = hf.getHashcode();

        hf.setDisable(false);
        hf.setHidden(hidden);
        hf.setRequired(false);
        hf.setValue("");
        hf.setSection(true);

        hf.setButtonEnd(buttonEnd);
        hf.setButtonPrevious(buttonPrevious);
        hf.setButtonNext(buttonNext);

        if(hashcode != hf.getHashcode())
        {
            hf.setToRefresh(true);
        }

        p_values.put(sectionName, hf);
    }

    public docHTML_controler getDochtmlController()
    {
        return p_doccontroller;
    }

    public static String getOrderByExp(String expression, String orderBy)
    {
        if(expression == null || expression.length() == 0)
        {
            return orderBy;
        }
        String[] exps = expression.split("\\.");
        if(exps.length == 1)
        {
            return orderBy;
        }
        else
        {
            return expression+"."+orderBy;
        }
    }

    public static boDefHandler getBodefHandlerFromExp(boDefHandler parent, String expression)
    {
        if(expression == null || expression.length() == 0)
        {
            return null;
        }
        String[] exps = expression.split("\\.");
        if(exps.length == 1)
        {
            if(parent == null)
            {
                return boDefHandler.getBoDefinition(exps[0]);
            }
            else
            {
                return parent.getAttributeRef(exps[0]).getReferencedObjectDef();
            }
        }
        else
        {
            boDefHandler parentDef = null;
            if(parent == null)
            {
                parentDef = boDefHandler.getBoDefinition(exps[0]);

            }
            else
            {
                parentDef = parent.getAttributeRef(exps[0]).getReferencedObjectDef();
            }
            String nextExp = "";
            for (int i = 1; i < exps.length; i++)
            {
                if(nextExp.length() != 0)
                {
                    nextExp += ".";
                }
                nextExp += exps[i];
            }
            return getBodefHandlerFromExp(parentDef, nextExp);
        }
    }
    public long getContextObjectBoui() 
    {
        return this.p_BOUI;
    }
    
    public Hashtable p_datafields = null;
    
    public void registerDataBindingField( AttributeHandler oAttHandler, ICustomFieldDataBinding oCustomField )
    {
        String sFieldName = oCustomField.getHtmlInputName( oAttHandler );
        if( p_datafields == null )
        {
            p_datafields = new Hashtable();            
        }
        p_datafields.put( sFieldName, oCustomField );
    }

    public ICustomFieldDataBinding getDataBindingField( AttributeHandler oAttHandler, ICustomFieldDataBinding oCustomField )
    {
        ICustomFieldDataBinding oRet = null;
        String sFieldName = oCustomField.getHtmlInputName( oAttHandler );
        if( p_datafields != null )
        {
            oRet = (ICustomFieldDataBinding)p_datafields.get( sFieldName );
        }
        return oRet;
    }
    
    public boolean useSecurity() {
        return p_METH_USE_SECURITY;
    }
}