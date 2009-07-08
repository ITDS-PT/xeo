/*Enconding=UTF-8*/
package netgest.bo.dochtml;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;

import netgest.bo.boConfig;

import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.def.boDefAttribute;
import netgest.bo.localized.JSPMessages;
import netgest.bo.runtime.*;
import netgest.bo.security.securityOPL;
import netgest.bo.security.securityRights;

import netgest.utils.ClassUtils;


public final class docHTML_grid  {
    private Vector  p_cols;
    private String  p_title;
    private boolean p_canSelectRows;
    private boolean p_showIcon;
    private boolean p_showState;
    private boolean p_canExpandRows;
    private boolean p_barStatus;
    private boolean p_colsFooter;
    private boolean p_barFilter;
    private boolean p_listBridgeAttributes;
//    private boolean p_editAttributes=fa3lse;
    private boolean p_showSelectNone=false;
    private docHTML p_doc;
    private String  p_bolistQuery = null;
//    private String p_template;
    private Hashtable p_values;

    
    public docHTML_grid(String template, docHTML doc, Hashtable p_values) {
        p_cols=new Vector();
        p_title=""; //$NON-NLS-1$
        p_canSelectRows=true;
        p_showIcon  =true;
        p_showState =true;
        p_canExpandRows= true;
        

        p_barStatus=true;
        p_colsFooter=false;
        p_barFilter=true;;
        p_doc=doc;
        p_listBridgeAttributes=false;
        
     //   p_template=template;
        this.p_values = p_values;
        
    
    }
    public docHTML_grid(String template, docHTML doc, boolean listBridgeAttributes, Hashtable p_values) {
        p_cols=new Vector();
        p_title=""; //$NON-NLS-1$
        p_canSelectRows=true;
        p_showIcon  =true;
        p_showState =true;
        p_canExpandRows= true;
        p_listBridgeAttributes=listBridgeAttributes;

        p_barStatus=true;
        p_colsFooter=false;
        p_barFilter=true;;
        p_doc=doc;
     //   p_template=template;
        this.p_values = p_values;
    
    }


    public void setTitle(String xtitle){
        p_title=xtitle;

    }
    public void setTitle(){
        p_title=""; //$NON-NLS-1$

    }

    public void addColAtr(boDefAttribute atr,int width,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atr,false,width,attributes, p_values) );
    }
    public void addColAtr(boDefAttribute atr,int width,String method,String viewmode,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atr,false,width,method,viewmode,attributes, p_values) );
    }
    
    public void addColBridgeAtr(boDefAttribute atr,int width,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atr,true,width,attributes, p_values) );
    }
    
    public void addColBridgeAtr(boDefAttribute atr,int width,String method,String viewmode,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atr,true,width,method,viewmode,attributes, p_values) );
    }
    
    public void addColAbstractAtr(String atrName,int width,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atrName,false,width,attributes, p_values) );
    }
    
    public void addColSpecial(String specialName,int width,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(specialName,width,attributes, p_values) );
    }
    
    public void addColAbstractAtr(String atrName,int width,String method,String viewmode,Hashtable attributes) {
        p_cols.add( new docHTML_gridCol(atrName,false,width,method,viewmode,attributes, p_values) );
    }
    
        public static class GridProperties
    {
        public String  title=""; //$NON-NLS-1$
        public boolean templateApply=false;
        public String  helperUrl=null;
        public boolean canSelectRows=false;
        public boolean showIcon=false;
        public boolean showState=false;
        public boolean canExpandRows=true;
        public boolean barStatus=true;
        public boolean colsFooter=true;
        public boolean barFilter=true;
        public boolean listBridgeAttributes=false;
        public boolean editAttributes=false;
        public boolean showSelectNone=false;
        public boolean showLines=true;
        public String  bolistQuery = null;
        public boolean renderOnlyCardID=false;
        public Vector columns;
        public boolean showPreview=true;
        public String  menu = null;
        public String  mode = "normal"; //$NON-NLS-1$
        public String  options = ""; //$NON-NLS-1$
        public String  waitingDetachAttribute=""; //$NON-NLS-1$
        public String  userClick = null;
        public String  userDblClick = null;
        public String  lineColorState = null;
    }


    public void render(PageContext page,boObjectList bolist,docHTML DOC, docHTML_controler DOCLIST ) throws boRuntimeException,java.io.IOException 
    {
        if(bolist instanceof bridgeHandler && bolist.getOrderBy() != null && !"".equals(bolist.getOrderBy()) && !bolist.ordered()) //$NON-NLS-1$
        {
            boBridgeIterator it = new boBridgeIterator((bridgeHandler)bolist, bolist.getOrderBy());
            render(page,it,DOC,DOCLIST);
            return;
        }
        GridProperties props = new GridProperties(); 
        
        props.canSelectRows=true;
        props.showIcon  =true;
        props.showState =true;
        props.canExpandRows= true;


        props.barStatus=true;
        props.colsFooter=false;
        props.barFilter=true;
        
        props.columns=p_cols;
        
        if ( page.getRequest().getParameter("showLines") != null ) //$NON-NLS-1$
        {
            props.showLines=page.getRequest().getParameter("showLines").equalsIgnoreCase("Yes");     //$NON-NLS-1$ //$NON-NLS-2$
        }
        
        if ( page.getRequest().getParameter("helperURL") != null ) //$NON-NLS-1$
        {
            props.helperUrl =page.getRequest().getParameter("helperURL");     //$NON-NLS-1$
        }
        
        props.userClick = page.getRequest().getParameter("userClick"); //$NON-NLS-1$
        if(props.userClick == null)
        {
            // Antes era onclick, para manter a compatiblidade!
            props.userClick = page.getRequest().getParameter("onclick"); //$NON-NLS-1$
        }
        props.userDblClick = page.getRequest().getParameter("userDblClick"); //$NON-NLS-1$
        props.lineColorState = page.getRequest().getParameter("lineColorState"); //$NON-NLS-1$
        
        props.mode=page.getRequest().getParameter("listmode"); //$NON-NLS-1$
        props.options=page.getRequest().getParameter("options"); //$NON-NLS-1$
        props.waitingDetachAttribute = page.getRequest().getParameter("waitingDetachAttribute"); //$NON-NLS-1$
        
        if(props.options==null) props.options=""; //$NON-NLS-1$
        if(props.waitingDetachAttribute==null) props.waitingDetachAttribute=""; //$NON-NLS-1$
        if(props.mode==null) props.mode="normal"; //$NON-NLS-1$

        if( "templateApply".equalsIgnoreCase(props.mode)){ //$NON-NLS-1$
            props.templateApply = true;
            props.canSelectRows=false;
            props.showSelectNone=false;
        }
        if( props.mode.equalsIgnoreCase("SEARCHONE")){ //$NON-NLS-1$
            props.canSelectRows=false;
            props.showSelectNone=true;
        }
        else if( props.mode.equalsIgnoreCase("RESULTBRIDGE")) //$NON-NLS-1$
        {
            props.canSelectRows=true;
            props.barStatus=false;
            props.colsFooter=false;
            props.barFilter=false;
        }
        
        props.menu = page.getRequest().getParameter("menu");       //$NON-NLS-1$
        if ( bolist.getParent()!=null ) 
        {
            if(bolist instanceof bridgeHandler )
            {
                boDefAttribute atrdef=((bridgeHandler)bolist).getDefAttribute();
                if ( !atrdef.supportManualOperation() )
                {
                    props.menu="no"; //$NON-NLS-1$
                }
            }
            props.canSelectRows=true;
            props.barStatus=false;
            props.colsFooter=false;
            props.barFilter=false;
        }
        String show=page.getRequest().getParameter("showBarStatus"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.barStatus = false;  
        }
        show=page.getRequest().getParameter("showBarFilter"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.barFilter = false;  
        }
        
        
        props.renderOnlyCardID=false;
        
        String xRender=page.getRequest().getParameter("renderOnlyCardID"); //$NON-NLS-1$
        if( xRender!=null && (xRender.equalsIgnoreCase("y") || xRender.equalsIgnoreCase("yes"))) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.renderOnlyCardID=true;
            props.columns=new Vector();
            props.showIcon  =false;
            Hashtable attributes= new Hashtable();
            props.columns.add( new docHTML_gridCol("CARDID",100,attributes, p_values) ); //$NON-NLS-1$
        }
/*        
        if ( bolist.getParentAtributeName() != null &&
             bolist.getParentAtributeName().equalsIgnoreCase("RO") && 
             bolist.getParent() != null &&
             bolist.getParent().getMode() != boObject.MODE_EDIT_TEMPLATE 
             )
        {
            props.renderOnlyCardID=true;
            props.columns=new Vector();
            props.canSelectRows=false;
            props.showIcon  =false;
            Hashtable attributes= new Hashtable();
            props.columns.add( new docHTML_gridCol("CARDID",100,attributes, p_values) );
            props.columns.add( new docHTML_gridCol("CHOOSE_I",150,attributes, p_values) );
            props.columns.add( new docHTML_gridCol("inout",true,150,attributes, p_values) );
        }
        */
        String editAttributes=page.getRequest().getParameter("editAttributes"); //$NON-NLS-1$
        if( editAttributes!=null && (editAttributes.equalsIgnoreCase("Yes") || editAttributes.equalsIgnoreCase("y")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.editAttributes=true;    
        }

        String bolist_Query=page.getRequest().getParameter("bolist_query"); //$NON-NLS-1$
        if ( bolist_Query!=null)
        {
            props.bolistQuery=bolist_Query;
        }
        else
        {
            props.bolistQuery=null;
        }
        
                                  
        String showSelectNone=page.getRequest().getParameter("showSelectNone"); //$NON-NLS-1$
        if( showSelectNone!=null && ( showSelectNone.equalsIgnoreCase("NO") || showSelectNone.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showSelectNone=false;    
        }
        
        showSelectNone=page.getRequest().getParameter("showSelectNoneByForce"); //$NON-NLS-1$
        if( showSelectNone!=null && ( "YES".equalsIgnoreCase(showSelectNone) || "y".equalsIgnoreCase(showSelectNone)) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showSelectNone=true;
        }
        
        show=page.getRequest().getParameter("showIcon"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showIcon = false;  
        }
        
        show=page.getRequest().getParameter("showStatus"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showState = false;  
        }
        
        
        show=page.getRequest().getParameter("showPreview"); //$NON-NLS-1$
        
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showPreview = false;  
        }
        
        show=page.getRequest().getParameter("canSelectRows"); //$NON-NLS-1$

        boObject objParent=bolist.getParent();        
        if (objParent!=null)
        {
          boolean disabled=objParent.getAttribute(bolist.getParentAtributeName()).isDisabled();
          if (disabled) show="NO";
        }        
        
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.canSelectRows = false;  
        }
        
        render( page , bolist , DOC , DOCLIST , props );
    }
    
    public void render(PageContext page,boBridgeIterator boIterator,docHTML DOC, docHTML_controler DOCLIST ) throws boRuntimeException,java.io.IOException 
    {
        
        GridProperties props = new GridProperties(); 
        
        props.canSelectRows=true;
        props.showIcon  =true;
        props.showState =true;
        props.canExpandRows= true;


        props.barStatus=true;
        props.colsFooter=false;
        props.barFilter=true;
        
        props.columns=p_cols;
        
        if ( page.getRequest().getParameter("showLines") != null ) //$NON-NLS-1$
        {
            props.showLines=false;    
        }
        
        props.userClick = page.getRequest().getParameter("userClick"); //$NON-NLS-1$
        if(props.userClick == null)
        {
            // Antes era onclick, para manter a compatiblidade!
            props.userClick = page.getRequest().getParameter("onclick"); //$NON-NLS-1$
        }
        
        
        props.mode=page.getRequest().getParameter("listmode"); //$NON-NLS-1$
        props.options=page.getRequest().getParameter("options"); //$NON-NLS-1$
        props.waitingDetachAttribute = page.getRequest().getParameter("waitingDetachAttribute"); //$NON-NLS-1$
        
        if(props.options==null) props.options=""; //$NON-NLS-1$
        if(props.waitingDetachAttribute==null) props.waitingDetachAttribute=""; //$NON-NLS-1$
        if(props.mode==null) props.mode="normal"; //$NON-NLS-1$

        if( props.mode.equalsIgnoreCase("SEARCHONE")){ //$NON-NLS-1$
            props.canSelectRows=false;
            props.showSelectNone=true;
        }
        else if( props.mode.equalsIgnoreCase("RESULTBRIDGE")) //$NON-NLS-1$
        {
            props.canSelectRows=true;
            props.barStatus=false;
            props.colsFooter=false;
            props.barFilter=false;
        }
        
        props.menu = page.getRequest().getParameter("menu");       //$NON-NLS-1$
        if ( boIterator.getBridgeHandler().getParent()!=null ) 
        {
            props.canSelectRows=true;
            props.barStatus=false;
            props.colsFooter=false;
            props.barFilter=false;
        }
        String show=page.getRequest().getParameter("showBarStatus"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.barStatus = false;  
        }
        show=page.getRequest().getParameter("showBarFilter"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.barFilter = false;  
        }
        
        
        props.renderOnlyCardID=false;
        
        String xRender=page.getRequest().getParameter("renderOnlyCardID"); //$NON-NLS-1$
        if( xRender!=null && (xRender.equalsIgnoreCase("y") || xRender.equalsIgnoreCase("yes"))) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.renderOnlyCardID=true;
            props.columns=new Vector();
            props.showIcon  =false;
            Hashtable attributes= new Hashtable();
            props.columns.add( new docHTML_gridCol("CARDID",100,attributes, p_values) ); //$NON-NLS-1$
        }
/*        
        if ( bolist.getParentAtributeName() != null &&
             bolist.getParentAtributeName().equalsIgnoreCase("RO") && 
             bolist.getParent() != null &&
             bolist.getParent().getMode() != boObject.MODE_EDIT_TEMPLATE 
             )
        {
            props.renderOnlyCardID=true;
            props.columns=new Vector();
            props.canSelectRows=false;
            props.showIcon  =false;
            Hashtable attributes= new Hashtable();
            props.columns.add( new docHTML_gridCol("CARDID",100,attributes, p_values) );
            props.columns.add( new docHTML_gridCol("CHOOSE_I",150,attributes, p_values) );
            props.columns.add( new docHTML_gridCol("inout",true,150,attributes, p_values) );
        }
        */
        String editAttributes=page.getRequest().getParameter("editAttributes"); //$NON-NLS-1$
        if( editAttributes!=null && (editAttributes.equalsIgnoreCase("Yes") || editAttributes.equalsIgnoreCase("y")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.editAttributes=true;    
        }

        String bolist_Query=page.getRequest().getParameter("bolist_query"); //$NON-NLS-1$
        if ( bolist_Query!=null)
        {
            props.bolistQuery=bolist_Query;
        }
        else
        {
            props.bolistQuery=null;
        }
        
                                  
        String showSelectNone=page.getRequest().getParameter("showSelectNone"); //$NON-NLS-1$
        if( showSelectNone!=null && ( showSelectNone.equalsIgnoreCase("NO") || showSelectNone.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showSelectNone=false;    
        }
        
        showSelectNone=page.getRequest().getParameter("showSelectNoneByForce"); //$NON-NLS-1$
        if( showSelectNone!=null && ( "YES".equalsIgnoreCase(showSelectNone) || "y".equalsIgnoreCase(showSelectNone)) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showSelectNone=true;
        }
        
        show=page.getRequest().getParameter("showIcon"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showIcon = false;  
        }
        
        show=page.getRequest().getParameter("showStatus"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showState = false;  
        }
        
        
        show=page.getRequest().getParameter("showPreview"); //$NON-NLS-1$
        
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.showPreview = false;  
        }
        
        boObject objParent=boIterator.getBridgeHandler().getParent();
        if (objParent!=null)
        {
          if (objParent.getAttribute(boIterator.getBridgeHandler().getParentAtributeName())!=null)
          { 
            boolean disabled=objParent.getAttribute(boIterator.getBridgeHandler().getParentAtributeName()).isDisabled();
            if (disabled) show="NO";
          }
        }            
        show=page.getRequest().getParameter("canSelectRows"); //$NON-NLS-1$
        if( show!=null && ( show.equalsIgnoreCase("NO") || show.equalsIgnoreCase("n")) ) //$NON-NLS-1$ //$NON-NLS-2$
        {
            props.canSelectRows = false;  
        }
        
        render( page , boIterator , DOC , DOCLIST , props );
    }
    
    public void render(PageContext page,boObjectList bolist,docHTML DOC, docHTML_controler DOCLIST , GridProperties props ) throws boRuntimeException,java.io.IOException {
        if(bolist instanceof bridgeHandler && !bolist.ordered() && bolist.getOrderBy() != null && !"".equals(bolist.getOrderBy())) //$NON-NLS-1$
        {
            boBridgeIterator it = new boBridgeIterator((bridgeHandler)bolist, bolist.getOrderBy());
            render(page,it,DOC,DOCLIST, props);
            return;
        }
        JspWriter out=page.getOut();
        String xvalue=null;
        
        if( props.bolistQuery != null  )
        {
            bolist.setFilter( props.bolistQuery );
        }
        
        if( props.columns == null )
        {
            props.columns = p_cols;
        }
        
        int hui=this.hashCode();
        int hui2=out.hashCode();
        
        Vector cols = props.columns;
        String mode = props.mode;
        String options=props.options;
        String waitingDetachAttribute=props.waitingDetachAttribute;
        int countSelectd=0;
        
        //p_listBridgeAttributes=props.listBridgeAttributes;


        boObject objParent=bolist.getParent();
        if (objParent!=null)
        {
          if (objParent.getAttribute(bolist.getParentAtributeName())!=null)
          { 
            boolean disabled=objParent.getAttribute(bolist.getParentAtributeName()).isDisabled();
            if (disabled) props.canSelectRows=false;
          }
        }    
        
        p_canSelectRows=props.canSelectRows;
        p_showIcon  =props.showIcon;
        p_showState =props.showState;
        p_canExpandRows= props.canExpandRows;


        p_barStatus=props.barStatus;
        p_colsFooter=props.colsFooter;
        p_barFilter=props.barFilter;

        boolean showIcon     =p_showIcon=props.showIcon;
        boolean showState    =p_showState=props.showState;
        boolean showPreview  =p_canExpandRows=props.showPreview;
        boolean canSelectRows=p_canSelectRows=props.canSelectRows;
        
        String voui=page.getRequest().getParameter("voui"); //$NON-NLS-1$
        if ( voui != null )
        {
            hui= ClassUtils.convertToInt(voui);
        }
        

        out.write(Gtxt.text[Gtxt.OPEN_GRID]);
        
        //Ajuda
        if(props.helperUrl != null && !"".equals(props.helperUrl)) //$NON-NLS-1$
        {
            out.write(Gtxt.text[Gtxt.OPEN_GRID_HLP]);
            out.write(props.helperUrl.toCharArray());
            out.write(Gtxt.text[Gtxt.OPEN_GRID_HLP +1]);
        }
        
        String menu=props.menu;
        if ( menu == null || menu.equalsIgnoreCase("YES")  ){ //$NON-NLS-1$
        
            out.write(Gtxt.text[Gtxt.OPEN_MENU]);
            out.print(p_doc.getHEIGHT_HTMLforToolbar(page,bolist)); //width;
            out.write(Gtxt.text[Gtxt.OPEN_MENU+1]);
            p_doc.writeHTMLforToolbar(DOCLIST, page,bolist,hui);
            out.write(Gtxt.text[Gtxt.CLOSE_MENU]);
            
        }

        
        
        out.write(Gtxt.text[Gtxt.OPEN_GRID+1]);
        out.write("<table cellspacing=\"0\" cellpadding=\"0\" style=\"background-color:#FFFFFF;height:100%;width:100%;table-layout:fixed\">".toCharArray()); //$NON-NLS-1$
        out.write("<tr>".toCharArray()); //$NON-NLS-1$
        out.write("<td>".toCharArray()); //$NON-NLS-1$
		out.write("<div id=\"head\" style=\"width:100%;overflow:hidden\">".toCharArray()); //$NON-NLS-1$
        out.write("<table  cellSpacing=\"0\" cellPadding=\"0\"><tbody><tr><td valign=\"top\">".toCharArray()); //$NON-NLS-1$
        
        out.write(Gtxt.text[Gtxt._GRID_HEADER  ]);
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+1]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+2]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+3]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+4]);        
 
        
        out.write(Gtxt.text[Gtxt.COLGROUP]);
        
        boolean canShowLines=false;
        if (  bolist instanceof bridgeHandler )
        {
            canShowLines=true;            
            //int xlin=(int) (( bridgeHandler) bolist).getAttribute("LIN").getValueDouble();
            
        }
        
        if ( !props.showLines && canShowLines )
        {
            canShowLines=false;
        }
        if ( canShowLines )
        {
            if (showPreview||showIcon||showState || canSelectRows){
                
                //out.write(Gtxt.text[Gtxt.COL_20]);
                out.print( "<COL width='25'/>"); //$NON-NLS-1$
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
            
            
            
        }
        if (canSelectRows){
            if (showPreview||showIcon||showState){
                
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showPreview){
            if (showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showIcon){
            if (showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if(showState){
               out.write(Gtxt.text[Gtxt.COL_18 ]);
               out.write(Gtxt.text[Gtxt.COL_2 ]);
        }
        
/*
<COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="18">
   <col width="2">
   <col width="100" />
   <col >
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="148" />
   <col width="2" />
   
   <col width="15" />
   */       
        
        int nrCols=cols.size();
        docHTML_gridCol c;

        //1ª COLUNA QUE É A RESIZABLE
        c=(docHTML_gridCol)cols.get(0);
        int hui3=c.hashCode();
        out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
        
        out.print( '\'' );
        out.print( c.p_width-2 );
        out.print( '\'' );
             
        out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        out.write(Gtxt.text[Gtxt.COL_EXPAND]);

        //------------
        
        for (int i = 1; i < nrCols; i++)  {
            c=(docHTML_gridCol)cols.get(i);

            out.write(Gtxt.text[Gtxt.COL_2 ]); //spacer

            out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
            out.print('\'');
            out.print(c.p_width-2);
            out.print('\'');
            out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        }
        out.write( Gtxt.text[ Gtxt.COL_2 ] );
        out.write( Gtxt.text[ Gtxt.COL_15 ] );
        
/*
   <tbody>
   <TR>
   <TD class="gh_std"><input id="g1643_check" class="rad" type="checkBox" id="checkBox4" name="checkBox4"></TD>

   <TD class="gh_std">&nbsp;</TD>

   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>

   <TD class="gh_std">&nbsp;</TD>
   
   <TD class="ghSep_std" >&nbsp;</TD>

   <TD id="g1643_ExpanderParent" class="ghSort_std">Nome</TD>
   <TD id="g1643_AutoExpander" class="ghSort_std">&nbsp;&nbsp;</TD>
   <TD class="ghSep_std">&nbsp;</TD>
   
   <TD class="ghSort_std">Início</TD>
    <TD class="ghSep_std">&nbsp;</TD>
    
   <TD class="ghSort_std">Fim<img class="ghSort_std" src="templates/grid/std/ghDown.gif" WIDTH="13" HEIGHT="5"></TD>
   <TD class="ghSep_std">&nbsp;</TD>
   
   <TD class="gh_std">limite</TD>
   <TD class="ghSep_std">&nbsp;</TD>
      
   <TD class="gh_std">Processo associado</TD>
      <TD class="ghSep_std">&nbsp;</TD>
      

   <TD class="gh_std" width="14"><img src="templates/grid/std/ghRefresh.gif" width="13" height="13" /></TD>
   
   </tr>
   </tbody>
   </table>
*/
        out.write(Gtxt.text[ Gtxt.OPEN_TBODY ]);
        out.write(Gtxt.text[ Gtxt.OPEN_TR ]);
        if ( canShowLines )
        {
        
            
            
            //.----------------------para modo template
            objParent = null;
            if (  bolist instanceof bridgeHandler )
            {
                objParent = bolist.getParent();
            }
     
            if( objParent!=null && objParent.getMode() == boObject.MODE_EDIT_TEMPLATE  )
                {
                // && xattributes.get("noRenderTemplate")==null){
                StringBuffer toPrint = new StringBuffer();
                toPrint.append("<img onclick=\"openAttributeMap('"); //$NON-NLS-1$
                String atrName = ((bridgeHandler)bolist).getName();
                StringBuffer nameH = new StringBuffer();
                nameH.append( objParent.getName() ).append( "__" ).append( objParent.bo_boui ).append("__").append( atrName ); //$NON-NLS-1$ //$NON-NLS-2$
               
               
                toPrint.append( nameH );
                toPrint.append( "','" ); //$NON-NLS-1$
                toPrint.append( DOC.getDocIdx() );
                toPrint.append( "','" ); //$NON-NLS-1$
                toPrint.append( objParent.getAttribute("TEMPLATE").getValueString() ); //$NON-NLS-1$
                toPrint.append("')\" src='templates/form/std/iconformula_on.gif' class='imgonoff' />"); //$NON-NLS-1$
                out.write("<TD class='gh_std' >"); //$NON-NLS-1$
                out.write( toPrint.toString() );
                out.write("</TD>"); //$NON-NLS-1$
              }
              else
              {
                  out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
              }

            
            //-------------------------
             if (!( showPreview || showIcon || showState||!canSelectRows )){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
        
        if ( canSelectRows ){
            int tab = 1;
            if(page.getRequest().getParameter("myIDX") == null) //$NON-NLS-1$
            {
                //parent_attribute=detail&parent_boui=2129992
                String att = page.getRequest().getParameter("parent_attribute"); //$NON-NLS-1$
                String boui = page.getRequest().getParameter("parent_boui"); //$NON-NLS-1$
                tab = DOC.getTabindex(DOC.GRID_CHECK_ALL, null, boui, att, DOCLIST) ;
            }
            else
            {
                tab = DOC.getTabindex(DOC.GRID_CHECK_ALL, DOCLIST) ;   
            }
            out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL  ] );
            out.print( hui);
            out.write( "_check' class='rad' type='checkBox' tabindex='" + tab  + "'/></TD>" ); //$NON-NLS-1$ //$NON-NLS-2$
            //out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL+1] );
            //out.print(hui);
            //out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL+2] );

            if (!( showPreview || showIcon || showState )){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
                   
        if (showPreview){
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            if (!(showIcon||showState)){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
        if (showIcon){
            //devera´ser assim
            //   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            
            if (!showState){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
        
        if(showState){
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        }

        //1ª COLUNA QUE É A RESIZABLE
        //    <TD id="g1643_ExpanderParent" class="ghSort_std">Nome</TD>
        // <TD id="g1643_AutoExpander" class="ghSort_std">&nbsp;&nbsp;</TD>
        // <TD class="ghSep_std">&nbsp;</TD>
        c=(docHTML_gridCol)cols.get(0);
        out.write( Gtxt.text[ Gtxt.GRIDTH_EXPANDER ] );
        out.print(hui2);
        out.write( Gtxt.text[ Gtxt.GRIDTH_EXPANDER+1 ] );
        out.print( c.getLabel() );

        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        
     //   out.write( Gtxt.text[ Gtxt.GRIDTH_AUTOEXPANDER  ] );
     //   out.print(hui2);
     //   out.write( Gtxt.text[ Gtxt.GRIDTH_AUTOEXPANDER+1 ] );
     //   out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        //------------

        //columns header when not storted
        //<TD class="gh_std">limite</TD>
        //<TD class="ghSep_std">&nbsp;</TD>
        
        //column header when sorted
        //<TD class="ghSort_std">Fim<img class="ghSort_std" src="templates/grid/std/ghDown.gif" WIDTH="13" HEIGHT="5"></TD>
         //<TD class="ghSep_std">&nbsp;</TD>
   
        for (int i = 1; i < nrCols; i++)  {
             c=(docHTML_gridCol)cols.get(i);
             switch (c.typeSorted()){
                 case ' ' : // not sorted
                 {
                      out.write( Gtxt.text[ Gtxt.OPEN_GRIDTH ] );
                      out.print(c.getLabel());
                      out.write(Gtxt.text[ Gtxt.CLOSE_TD ]);
                      break;
                 }
                 case 'A' : // Ascendente
                 {
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTHSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.IMG_TH_SORTED_ASC ] );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
                 case 'D' : { // descendente
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTHSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.IMG_TH_SORTED_DESC ] );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
             }
             out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        }

        //<TD class='gh_std' width='14'><img src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>
        out.write( Gtxt.text[ Gtxt.IMG_REFRESH ] );;
                
        
        
 /*
   </tr>
   </tbody>
   </table>
   </TD>
   </tr> close tr =25
   */
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TBODY ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        
// ---------------------------------------- HEADER GRID END ------------------  //

    out.write("</td></tr></tbody></table>".toCharArray()); //$NON-NLS-1$
	out.write("	</div>".toCharArray()); //$NON-NLS-1$
	out.write(" </td> <!--novo-->".toCharArray()); //$NON-NLS-1$
	out.write(" </tr>".toCharArray()); //$NON-NLS-1$
	out.write(" <tr style=\"height:100%\" >".toCharArray()); //$NON-NLS-1$
	out.write("	 <td style=\"height:100%;width:100%\">".toCharArray());		  //$NON-NLS-1$
	out.write("	 	     <div onscroll=\"document.getElementById('head').scrollLeft=this.scrollLeft\" style=\"width:100%;height:100%;overflow:scroll\">".toCharArray()); //$NON-NLS-1$
	out.write("				<table style=\"height:100%;width:100%;\" cellspacing=\"0\" cellpadding=\"0\">".toCharArray()); //$NON-NLS-1$
	out.write("					<tbody>".toCharArray()); //$NON-NLS-1$
	out.write("						<tr style=\"height:100%\">".toCharArray()); //$NON-NLS-1$
    out.write(" 							<td valign=\"top\" style=\"background-Color:#ffffff;height:100%;width:100%;\">".toCharArray()); //$NON-NLS-1$

/* ------------BEGIN BODY GRID ---------------------------
<TR>
    <TD>
    
   <div id="g1643_divc" class="gContainerLines_std">
   <table id="g1643_body" onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643');onClick_GridBody_std(event)" id="gridbody$0$activitylist" cellpading="2" cellspacing="0" class="gBodyLines_std">
   <COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="20">
   <col />
   <col width="100" />
   <col style="background-color:#EAEDEE;" width="100" /> //COLUNA SELECIONADA
   <col width="100" />
   <col width="150" />

   ROW NORMAL
   
   <TR id="g1643$1222" >
		<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
		<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">01/01/2003</TD>
		<TD class="gCell_std">2654</TD>
   </tr>

   ROW SELECTED
   
   <TR id="B$123455">
		<TD class="gCellSel_std"><input class="rad" type="checkBox" id="checkBox1" value="on" checked name="checkBox1"></TD>
		<TD class="gCellSel_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCellSel_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCellSel_std"><img src="resources/activity/state_archive.gif" height="16" width="16"></TD>
		<TD class="gCellSel_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCellSel_std">&nbsp;</TD>
		<TD class="gCellSel_std">&nbsp;</TD>
		<TD class="gCellSel_std">01/01/2003</TD>
		<TD class="gCellSel_std">2654</TD>
   </tr>

   ROW COM EXPNDE ROW
 
   <TR id="B$1222" >
		<TD class="gCellExpanded_std"><input class="rad" type="checkBox" id=checkBox3 name=checkBox3></TD>
		<TD class="gCellExpanded_std"><img src="templates/grid/std/quickview_on.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCellExpanded_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCellExpanded_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCellExpanded_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCellExpanded_std">&nbsp;</TD>
		<TD class="gCellExpanded_std">&nbsp;</TD>
		<TD class="gCellExpanded_std">01/01/2003</TD>
		<TD class="gCellExpanded_std">2654</TD>
   </tr>
  

   ROW EXPANDED
   <TR id="expandedRow"  >
		<TD class="gCell_std"></TD>
		<TD colspan="8" class="gCellQuickView_std">

            //FORM QUICKVIEW
        
		</TD>
   </tr>
   

   <!-- FIM DE ROW EXPANDIDA -->

 </table>   <!--END TABLE BODY  !-->
</div> <!--END TABLE BODY CONTAINER  !-->


</TD>
</tr>
   
   */
        
//        out.write( Gtxt.text[ Gtxt.OPEN_TR ] );
//        out.write( Gtxt.text[ Gtxt.OPEN_TD ] );
/*    
   <div id="g1643_divc" class="gContainerLines_std">
   <table id="g1643_body" onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643');onClick_GridBody_std(event)" id="gridbody$0$activitylist" cellpading="2" cellspacing="0" class="gBodyLines_std">
   <COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="20">
   <col />
   <col width="100" />
   <col style="background-color:#EAEDEE;" width="100" /> //COLUNA SELECIONADA
   <col width="100" />
   <col width="150" />
 */
        //out.write( Gtxt.text[ Gtxt.GRIDDIV_CONT ] );
        //out.print( hui );
        //out.write( Gtxt.text[ Gtxt.GRIDDIV_CONT+1 ] );

        boObject obj;
        
        if ( p_bolistQuery != null )
        {
              bolist.setFilter( p_bolistQuery );                      
        }
            
 
        
        bolist.first();
        obj=bolist.getObject();
        String firstColName = null;
        for (int i = 0; i <  p_cols.size() ; i++) 
        {
             boDefAttribute atrdef = ((docHTML_gridCol)p_cols.get(i)).p_atr;
             if( atrdef!=null)
             {
                firstColName=atrdef.getName();
                break;
             }
            
        }
        
        

        if(!bolist.isEmpty()){
            
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT   ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 1 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 2 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 3 ] );
            out.print( hui );
//            if(props.onclick != null && !"".equals(props.onclick))
//            {
//                out.print("');");        
//                out.print("\"  cellpadding='2' cellspacing='0' ");
//            }
//            else
//            {
                out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 4 ] );
//            }
            
            out.print(" options='"+options+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !waitingDetachAttribute.equals("") ) //$NON-NLS-1$
            { 
                out.print("waitingDetachAttribute='"+waitingDetachAttribute+"' " );   //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print(" mode='"+mode+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(" letter_field='"+firstColName+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(" ondblclick=\"so('g"+hui+"');onDoubleClick_GridBody(event)\" " ); //$NON-NLS-1$ //$NON-NLS-2$
                           
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );
        
            
        }
        else{
            
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT   ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT_NONE   ] );
            //out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 1 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 2 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 3 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 4 ] );
            out.print(" mode='' " ); //$NON-NLS-1$
            out.print(" options='"+options+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !waitingDetachAttribute.equals("") ) //$NON-NLS-1$
            { 
                out.print("waitingDetachAttribute='"+waitingDetachAttribute+"' " );   //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print(" letter_field='"+firstColName+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            //out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT_NONE + 1   ] );  
        }
        
        
//        out.print( hui );
//        out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );

        out.write( Gtxt.text[ Gtxt.COLGROUP ] );
        
        if ( canShowLines  )  out.print( "<COL width='25'/>"); //$NON-NLS-1$
        if ( canSelectRows  )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showPreview  )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showIcon       )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showState      )  out.write( Gtxt.text[ Gtxt.COL_20  ] );

        //primeira coluna é sempre resizable
//        out.write( Gtxt.text[ Gtxt.COL_EXPAND ] );
        
     //<col width="100" />
     //<col class='sel' width="100" /> //COLUNA SELECIONADA
   
        for (int i = 0; i < nrCols; i++)  {

             c=(docHTML_gridCol)cols.get(i);
             out.write( Gtxt.text[ Gtxt.OPEN_COL_WIDTH ] );
             out.print( '\'' );
             out.print( c.p_width );
             out.print( '\'' );
             if (c.typeSorted()!=' '){ 
                out.write( Gtxt.text[ Gtxt.CLASS_SEL ] );
             }
             
             out.write(Gtxt.text[ Gtxt.CLOSE_COL_WIDTH ]);
             if(i==0)
             {
                out.write("<COL width=100%  />".toCharArray()); //$NON-NLS-1$
             }
        }

        

/*
        ROW NORMAL
   
   <TR id="g1643$1222" boui='123456' >
		<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
		<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">01/01/2003</TD>
		<TD class="gCell_std">2654</TD>
   </tr>
*/
        bolist.beforeFirst();

        
        boolean isSelect;
        boolean isExpanded;
        String xExp;
        String objName;
        
         if(props.showSelectNone)
           {
             out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] ); 
             
             out.print(hui);
             out.print("__"); //$NON-NLS-1$
             //out.print( obj.getName() );
             out.print(bolist.getBoDef().getName());
             out.print("__"); //$NON-NLS-1$
             out.print(0);
             out.print('\'');

            if(props.userClick != null && !"".equals(props.userClick)) //$NON-NLS-1$
            {       
                String aux = URLDecoder.decode( props.userClick, boConfig.getEncoding() );
//                out.print(" onclick=\"");
                out.print(" userClick=\""); //$NON-NLS-1$
                out.print(aux.replaceAll("OBJECT_LIST_BOUI",String.valueOf(""))); //$NON-NLS-1$ //$NON-NLS-2$
                out.print(";\" "); //$NON-NLS-1$
            }
            
             
             out.print(" selectRecordNone style='height:25'>"); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
             out.print("<IMG selectRecordNone src='templates/grid/std/none.gif' height=16 width=16 />"); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
             out.print("<TD  style='color:#BC9C5E' selectRecordNone class='gCell_std' COLSPAN='"+(nrCols+(canShowLines?1:0)+(showIcon?1:0)+(showState?1:0)+(canSelectRows?1:0)+1)+"'>"); //$NON-NLS-1$ //$NON-NLS-2$
             out.print(JSPMessages.getString("docHTML_grid.171")); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
             out.write( Gtxt.text[ Gtxt.CLOSE_TR ]);
            
        }
        byte isOrphan=0;
        byte hasRigthsToSaveParent=-1;
        
        
        if ( bolist.getRowCount() > 0 )
        {
            while (bolist.next()){
                 try
                 {
                    
                     obj=bolist.getObject();
                     objName=obj.getName();
                     isSelect=bolist.currentObjectIsSelected();
                     xExp=bolist.getRowProperty("EXPANDED"); //$NON-NLS-1$
                     if (xExp!=null)isExpanded=bolist.getRowProperty("EXPANDED").equals("Y"); //$NON-NLS-1$ //$NON-NLS-2$
                     else isExpanded=false;
                     //obj.getName();

                  
                    out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] );
                    
                    
                     if(!DOC.useSecurity() || (securityRights.canRead(obj.getEboContext(), obj.getName() ) && securityOPL.canRead( obj )  )) {
                        out.print(hui);
                        out.print("__"); //$NON-NLS-1$
                        out.print( obj.getName() );
                        out.print("__"); //$NON-NLS-1$
                        out.print(obj.bo_boui);
                     }
                     out.print('\'');

                    if(props.userClick != null && !"".equals(props.userClick)) //$NON-NLS-1$
                    {       
                        String aux = URLDecoder.decode( props.userClick, boConfig.getEncoding() );
//                        out.print(" onclick=\"");
                        out.print(" userClick=\""); //$NON-NLS-1$
                        out.print(aux.replaceAll("OBJECT_LIST_BOUI",String.valueOf(obj.getBoui()))); //$NON-NLS-1$
                        out.print(";wait();\" "); //$NON-NLS-1$
                    }
                    
                    if(props.userDblClick != null && !"".equals(props.userDblClick)) //$NON-NLS-1$
                    {       
                        String aux = URLDecoder.decode( props.userDblClick, boConfig.getEncoding() );
                        out.print(" userDblClick=\""); //$NON-NLS-1$
                        out.print(aux.replaceAll("OBJECT_LIST_BOUI",String.valueOf(obj.getBoui()))); //$NON-NLS-1$
                        out.print(";wait();\" "); //$NON-NLS-1$
                    }
                    else if(DOC.getController() != null && 
                            "XwfController".equals(DOC.getController().getName()) && //$NON-NLS-1$
                            ("xwfActivity".equals(bolist.getBoDef().getName()) || //$NON-NLS-1$
                             "xwfProgramRuntime".equals(bolist.getBoDef().getName())) //$NON-NLS-1$
                            )
                    {
                        out.print(" userDblClick=\"parent.parent.setActionCode('"); //$NON-NLS-1$
                        out.print(XwfKeys.ACTION_SHOW_KEY+"["+String.valueOf(obj.getBoui())+"]');parent.parent.boForm.BindValues()"); //$NON-NLS-1$ //$NON-NLS-2$
                        out.print(";wait();\" ");                 //$NON-NLS-1$
                    }                    

             
                     if ( !obj.exists() ) out.print(" exists=no "); //$NON-NLS-1$
                     
                     if ( isOrphan==0 )
                     {
                         if ( !obj.getBoDefinition().getBoCanBeOrphan() )
                         {
                             isOrphan=2;
                         }
                         else isOrphan=1;
                     }
                     if ( isOrphan==2 )
                     {
                             out.print(" orphan=no "); //$NON-NLS-1$
                     }
                     if ( hasRigthsToSaveParent==-1  )
                     {
                         boObject p=bolist.getParent();
                         if ( p !=null )
                         {
                           if ( securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                                && securityOPL.canWrite(p)|| (!p.exists() && securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD)))
                                {
                                   hasRigthsToSaveParent=1;
                                }
                                else
                                {
                                    hasRigthsToSaveParent=0;
                                }
                         }
                     }
                     
                     if ( hasRigthsToSaveParent==0 )
                     {
                         out.print(" hasRightsToSaveParent=no "); //$NON-NLS-1$
                     }

                    if(props.lineColorState != null)
                    {                
                        boObjectStateHandler state = obj.getStateAttribute("runningState"); //$NON-NLS-1$
                        if(state != null)
                        {
                            if(props.lineColorState.indexOf(state.getValueString()) != -1)
                            {
                                out.write(" style='COLOR: #0000ff' "); //$NON-NLS-1$
                            }
                        }
                    }
                     out.print('>');
                     //out.print( Gtxt.text[ Gtxt.CLOSE_TR_ID ] );
                     
        //<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
                    
                    if( canShowLines ){
                            out.print("<TD style='' class='gCellNumber_std'>"); //$NON-NLS-1$
                            AttributeHandler xlinatt;
                            int xlin;
                            if (  (xlinatt=(( bridgeHandler) bolist).getAttribute("LIN") ) != null )
                            {
                                xlin = (int)xlinatt.getValueLong();
                            }
                            else
                            {
                                xlin = bolist.getRow();
                            }
                            
                           out.print("<img width=18 title='"+JSPMessages.getString("docHTML_grid.199")+"'  class=\"numberLine\" ondragstart=\"grid_StartMoveLine()\"  height=16 lin="+xlin+" src=resources/numbers/"+xlin+".gif>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                           out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     }
                    
                     if( canSelectRows ){
                        if ( isExpanded ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                            if ( isSelect ) {
                                out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL ] );
                                out.print( objName );
                                out.print("__"); //$NON-NLS-1$
                                out.print(obj.bo_boui);
                                
                                out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL+1 ] );
                            }
                            else out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL ] );
                            
                        }
                        else if ( isSelect ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL ] );
                            out.print( objName );
                            out.print("__"); //$NON-NLS-1$
                            out.print(obj.bo_boui);
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL+1 ] );
                        }
                        else {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL ] );
                            out.print( objName );
                            out.print("__"); //$NON-NLS-1$
                            out.print(obj.bo_boui);
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL+1 ] );
                        }
                     }
        
        //<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
                     if (showPreview){
                         
                         if ( isExpanded ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_ON ] );
                        }
                        else if ( isSelect ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_OFF ] );
                         
                        }
                        else {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_OFF ] );
                        }
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     }
        
        //<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
        
                     if ( showIcon ) {
                        if      ( isExpanded ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                        else if ( isSelect ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                        else  out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                        
                        
                        boObject o=null;
                        if ( obj.getName().equals("Ebo_Template") ) //$NON-NLS-1$
                        {   
                            o=obj.getBoManager().loadObject( obj.getEboContext(),"Ebo_ClsReg",obj.getAttribute("masterObjectClass").getValueLong());                 //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else if ( obj.getName().equals("Ebo_ClsReg") ) //$NON-NLS-1$
                        {
                            o=obj;   
                        }
                             
                        out.write( Gtxt.text[ Gtxt.IMG_SRC1 ] );
                        out.print( obj.getLabel() );
                        if( o!= null ) out.print(JSPMessages.getString("docHTML_grid.120")+o.getAttribute("description").getValueString() ); //$NON-NLS-1$ //$NON-NLS-2$
                        
                        out.write( Gtxt.text[ Gtxt.IMG_SRC2 ] );                        
                        if ( o==null)
                        {                            
                            out.print( obj.getSrcForIcon16() );
                            out.print( "'" ); //$NON-NLS-1$
                            out.write( " ondragstart=\"startDragObject(".toCharArray()); //$NON-NLS-1$
//                            out.write( Gtxt.text[ Gtxt.ICON_16 ] );
                        }
                        else
                        {
                            out.write( Gtxt.text[ Gtxt.RESOURCES_DIR ] );
                            out.print(o.getAttribute("name").getValueString()); //$NON-NLS-1$
                            if ( o.getName().equals("Ebo_ClsReg") ) //$NON-NLS-1$
                            {
                                out.write( Gtxt.text[ Gtxt.ICON_16 ] );
                            }
                            else
                            {                                
                                out.write( Gtxt.text[ Gtxt.ICON_16_TMPL ] );
                            }
                        }
                        
                        out.print( "'"+obj.getName()+"',"+obj.getBoui()+","+obj.exists()+","+hui ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        out.write( Gtxt.text[ Gtxt.ICON_16_REST ] );
                            
                        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     }
        
        //<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
        
                     if ( showState ){
                        if      ( isExpanded ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                        else if ( isSelect ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                        else  out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                        out.print( obj.getICONComposedState() );
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        
                     }
        
        //<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
        
                     for (int i = 0; i < nrCols; i++)  {
                        c=(docHTML_gridCol)cols.get(i);
                        if (objParent!=null)
                        {
                          if (objParent.getAttribute(bolist.getParentAtributeName())!=null)
                          { 
                            boolean disabled=objParent.getAttribute(bolist.getParentAtributeName()).isDisabled();
                            if (disabled && c.p_isBridgeAttr)
                            {
                              objParent.getBridge(bolist.getParentAtributeName()).getAttribute(c.p_atr.getName()).setDisabled();
                            }
                          }
                        }    
                                      
                        if  ( isExpanded ){
                            if(i == 0)
                            {
                                out.write("<TD class='gCellExpanded_std'>".toCharArray()); //$NON-NLS-1$
                            }
                            else
                            {
                                out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                            }
                        }
                        else if ( isSelect ) {
                            if(i == 0)
                            {
                                out.write("<TD class='gCellSel_std'>".toCharArray()); //$NON-NLS-1$
                            }
                            else
                            {
                                out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                            }
                            countSelectd++;
                        }
                        else{
                            if(i == 0)
                            {
                                out.write( "<TD colspan=2 class='gCell_std'>".toCharArray() ); //$NON-NLS-1$
                            }
                            else
                            {
                                out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                            }
                        }
        
        //atencao aqui tem que ser diferenciado consoante o tipo do atributo
                        //xvalue=obj.getAttribute(c.p_atr.getName()).getValueString();
                        if ( p_listBridgeAttributes )
                        {
                            xvalue=c.getHTML( obj , (bridgeHandler)  bolist , DOC , DOCLIST );
                        }
                        else if ( props.editAttributes )
                        {
                            xvalue=c.getHTML( obj , DOC , DOCLIST );
                        }
                        else
                        {
                            xvalue=c.getHTML( obj );
                        }
                        
                        
                        if(xvalue.equals("")) out.write( Gtxt.text[ Gtxt.NBSP ] ); //$NON-NLS-1$
                        else out.print( xvalue );
        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                        
                     }
                     
                     
        //escrever a row Expandida
                     if (showPreview && isExpanded){
        
             /*        ROW EXPANDED
           <TR id="expandedRow"  >
                <TD class="gCell_std"></TD>
                <TD colspan="8" class="gCellQuickView_std">
        
                    //FORM QUICKVIEW
                
                </TD>
           </tr>
           */          
                        out.write( Gtxt.text[ Gtxt.EXPANDED_ROW ] );
                        out.print( nrCols+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)-1); //colSpan
                        out.write( Gtxt.text[ Gtxt.EXPANDED_ROW+1 ] );
        
                        //chamar quickViewForm do objecto
                        out.print("not implemented yet"); //$NON-NLS-1$
                        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
                            
                        
                     }
        //NOVA LINHA
                out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
                // se quiser acrescentar alguma cois debaixo da lina é aqui
                
                 }
                 catch (Exception e)
                 {
                     CharArrayWriter ecr = new CharArrayWriter();
                     
                     PrintWriter pr = new PrintWriter(ecr);
                     e.printStackTrace( pr );
                     pr.flush();
                     ecr.flush();
                     pr.close();
                     ecr.close();
                     out.print("<TR><TD  COLSPAN='"+(nrCols+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)+1)+"' >"+ //$NON-NLS-1$ //$NON-NLS-2$
                     e.getClass().getName()+":"+e.getMessage() //$NON-NLS-1$
                     +"<span style='display:none' >"+ecr.toString()+"</span>" //$NON-NLS-1$ //$NON-NLS-2$
                     +"</TD></TR>" ); //$NON-NLS-1$
                 }
                 
            }
        }
        else
        {
            //rowCount ==0 
             
             out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] );
             
             out.print(hui);
             out.print("__"); //$NON-NLS-1$
             out.print( bolist.getBoDef().getName() );
             out.print("__"); //$NON-NLS-1$
             out.print("null"); //$NON-NLS-1$
             //out.print(obj.bo_boui);
             out.print('\'');
             out.print(" exists=no "); //$NON-NLS-1$
             
              if ( hasRigthsToSaveParent==-1  )
             {
                 boObject p=bolist.getParent();
                 if ( p !=null )
                 {
                   if ( securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                        && securityOPL.canWrite(p)|| (!p.exists() && securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD)))
                        {
                           hasRigthsToSaveParent=1;
                        }
                        else
                        {
                            hasRigthsToSaveParent=0;
                        }
                 }
             }
             
             if ( hasRigthsToSaveParent==0 )
             {
                 out.print(" hasRightsToSaveParent=no "); //$NON-NLS-1$
             }
             out.print('>');
            
            out.print("<TD select='none' COLSPAN='"+(nrCols+1+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)+1)+"'><TABLE id='g"); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(hui);
            out.print("' select='none' style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD select='none' style='COLOR: #999999; BORDER:0px' align=middle width='100%'>"); //$NON-NLS-1$
            if( bolist.getBoDef().getLabel() == null )
            {
                out.print(JSPMessages.getString("docHTML_grid.238"));     //$NON-NLS-1$
            }
            else
            {
            out.print(JSPMessages.getString("docHTML_grid.239")+bolist.getBoDef().getLabel()+JSPMessages.getString("docHTML_grid.240")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print("</TD></TR></TBODY></TABLE></TD>"); //$NON-NLS-1$
            
        }

//    out.write( Gtxt.text[ Gtxt.END_BODY_GRID ] );
    out.write( " </TABLE></TD></TR></TBODY></TABLE></DIV></TD></TR>".toCharArray()); //$NON-NLS-1$
    out.write("						        </td>".toCharArray()); //$NON-NLS-1$
	out.write(" 					</tr>".toCharArray()); //$NON-NLS-1$
    if ( p_colsFooter ){
            out.write("						<tr style=\"height:100%\">".toCharArray()); //$NON-NLS-1$
            out.write(" 							<td valign=\"top\" style=\"background-Color:#ffffff;height:100%;width:100%;\">".toCharArray()); //$NON-NLS-1$
/*
<!-- begin footer columns -->
<TR  height=25>
   <TD valign=top >
   <table id="g1642_body" cellpadding="2" cellspacing="0" style="height:25px" class="gfc_std">*/

       out.write( Gtxt.text[ Gtxt.GRID_COLS_FOOTERH ]);  
       out.print( hui3 );
       out.write( Gtxt.text[ Gtxt.GRID_COLS_FOOTERH+1 ]);
       
        
        out.write(Gtxt.text[Gtxt.COLGROUP]);
        if (canSelectRows){
            if (showPreview||showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showPreview){
            if (showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showIcon){
            if (showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if(showState){
               out.write(Gtxt.text[Gtxt.COL_18 ]);
               out.write(Gtxt.text[Gtxt.COL_2 ]);
        }


        

        //1ª COLUNA QUE É A RESIZABLE
        c=(docHTML_gridCol)cols.get(0);
        out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
        out.print('\'');
        out.print(c.p_width-2);
        out.print('\'');
        out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
//        out.write(Gtxt.text[Gtxt.COL_EXPAND]);
        out.write("<COL width=100%  />".toCharArray()); //$NON-NLS-1$

        //------------
        
        for (int i = 1; i < nrCols; i++)  {
            c=(docHTML_gridCol)cols.get(i);

            out.write(Gtxt.text[Gtxt.COL_2 ]); //spacer

            out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
            out.print('\'');
            out.print(c.p_width-2);
            out.print('\'');
            out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        }
        out.write( Gtxt.text[ Gtxt.COL_2 ] );
        out.write( Gtxt.text[ Gtxt.COL_15 ] );
  
  /*      
   <tbody>
   <TR>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfcSep_std" >&nbsp</TD>
   <TD id="g$ExpanderParent" class="gfcSort_std">Nome</TD>
   <TD id="g$AutoExpander" class="gfcSort_std">&nbsp;&nbsp;</TD>
   <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfcSort_std">Início</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfcSort_std">Fim</TD>
      <TD class="ghSep_std">&nbsp;</TD>
   <TD class="gfc_std">limite</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfc_std">Processo associado</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
      

   <TD class="gfc_std" width="14"></TD>
   </tr>
   </tbody>
   </table>
 

   </TD>
   </tr>
   <!-- end footer COlUMNS -->
*/
        out.write(Gtxt.text[ Gtxt.OPEN_TBODY ]);
        
        out.write(Gtxt.text[ Gtxt.OPEN_TR ]);
        
        if ( canSelectRows ){
            
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
  
            if (!( showPreview || showIcon || showState )){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
                   
        if (showPreview){
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            if (!(showIcon||showState)){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
        if (showIcon){
            //devera´ser assim
            //   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            
            if (!showState){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
        if(showState){
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        }

        
        c=(docHTML_gridCol)cols.get(0);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EXPANDER ] );
        out.print(hui3);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EXPANDER+1 ] );
        out.print( c.getLabel() );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_AUTOEXPANDER  ] );
        out.print(hui3);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_AUTOEXPANDER+1 ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        //------------

        
        for (int i = 1; i < nrCols; i++)  {
             c=(docHTML_gridCol)cols.get(i);
             switch (c.typeSorted()){
                 case ' ' : // not sorted
                 {
                      out.write( Gtxt.text[ Gtxt.OPEN_GRIDTH ] );
                      out.print(c.getLabel());
                      out.write(Gtxt.text[ Gtxt.CLOSE_TD ]);
                      break;
                 }
                 case 'A' : // Ascendente
                 {
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTDFCOLSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
                 case 'D' : { // descendente
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTDFCOLSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
             }
             out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        }

        //<TD class='gh_std' width='14'><img src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>
        out.write( Gtxt.text[ Gtxt.IMG_REFRESH ] );;
                
        
        
 /*
   </tr>
   </tbody>
   </table>
   </TD>
   </tr> close tr =25
   */
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TBODY ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        
        out.write("						        </td>".toCharArray()); //$NON-NLS-1$
        out.write(" 					</tr>".toCharArray()); //$NON-NLS-1$
    }
    
    if( p_barStatus){
       out.write("						<tr style=\"height:100%\">".toCharArray()); //$NON-NLS-1$
       out.write(" 							<td valign=\"top\" style=\"background-Color:#ffffff;height:100%;width:100%;\">".toCharArray()); //$NON-NLS-1$

       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR   ] );
       out.print(hui);
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR+1   ] );
       
       
      // if (props.canSelectRows)
      // {
           //out.print(countSelectd);
          // out.print("&nbsp;de&nbsp;");
          if ( !bolist.isEmpty())
          {
            out.print(bolist.getRowCount());
            if (bolist.getRowCount()==30)
                out.print(JSPMessages.getString("docHTML_grid.252"));             //$NON-NLS-1$
            else
                out.print(JSPMessages.getString("docHTML_grid.253"));           //$NON-NLS-1$
          }
          // out.print("&nbsp;seleccionados&nbsp;");
      // }
       if ( bolist.getUserQuery() != null && !bolist.getUserQuery().equals("<cleanFilter/>") ) //$NON-NLS-1$
       {
           out.print("<span style='color:#990000'> C/Filtro</span>"); //$NON-NLS-1$
       }
              
       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR +2]  );
       out.print(hui);
       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR +3  ]  );
       out.print(hui);
       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR  +4 ] );

//       templates/grid/std/
 //page_l0.gif
 
       out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
       out.write( Gtxt.text[ Gtxt.SETA_LEFT ] );
       if ( bolist.getPage() > 1)
       {
           out.print("a.gif"); //$NON-NLS-1$
       }
       else
       {
           out.print(".gif"); //$NON-NLS-1$
       }
        
        
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +5  ]  );
//       Page  //Page ou Página;
       out.print( JSPMessages.getString( "docHTML_grid.465" )   ); 
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +6  ]  );
       out.print(hui);
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +7  ]  );
       //1 //page NUMBER
       out.print(bolist.getPage());
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +8  ]  );
       out.print(hui);
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +9  ]  );
       //templates/grid/std/page_r0.gif //path para a seta direita
       out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
       out.write( Gtxt.text[ Gtxt.SETA_RIGHT ] );
       if ( bolist.haveMorePages() )
       {
           out.print("a.gif"); //$NON-NLS-1$
       }
       else
       {
           out.print(".gif"); //$NON-NLS-1$
       }
       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +10  ]  );

        out.write("						        </td>".toCharArray()); //$NON-NLS-1$
        out.write(" 					</tr>".toCharArray()); //$NON-NLS-1$
    }
    if( p_barFilter && firstColName!=null ){
        out.write("						<tr style=\"height:100%\">".toCharArray()); //$NON-NLS-1$
        out.write(" 							<td valign=\"top\" style=\"background-Color:#ffffff;height:100%;width:100%;\">".toCharArray()); //$NON-NLS-1$

        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR   ]  );
        out.print(hui);
     
        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR +1  ]  );
        out.print(hui);

        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR +2  ]  );
        out.print(hui);

        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +3 ]  );
        out.print(hui);
        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +4 ]  );
        
        String letter=bolist.getSearchLetter();
        if ( letter==null )
        {
            letter=JSPMessages.getString("docHTML_grid.265"); //$NON-NLS-1$
        }
        
        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +5 ]  );
        
        String bar=JSPMessages.getString("docHTML_grid.119")+"</TD><TD>#</TD><TD>a</TD><TD>b</TD><TD>c</TD><TD>d</TD><TD>e</TD><TD>f</TD><TD>g</TD><TD>h</TD><TD>i</TD><TD>j</TD><TD>k</TD><TD>l</TD><TD>m</TD><TD>n</TD><TD>o</TD><TD>p</TD><TD>q</TD><TD>r</TD><TD>s</TD><TD>t</TD><TD>u</TD><TD>v</TD><TD>w</TD><TD>x</TD><TD>y</TD><TD>z</TD></TR></TBODY></TABLE></TD></TR>"; //$NON-NLS-1$
        bar=bar.replaceAll(letter,"<font color='#990000'><b>"+letter+"</b></font>" ); //$NON-NLS-1$ //$NON-NLS-2$
        out.print( bar );
       //out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +6 ]  );
     
        out.write("						        </td>".toCharArray()); //$NON-NLS-1$
        out.write(" 					</tr>".toCharArray()); //$NON-NLS-1$
    }
    
    
 
    if( p_colsFooter || p_barStatus || p_barFilter )
    {
        out.write( Gtxt.text[ Gtxt.GRID_SPACER_TOSCROLL ] );
    }
 
     out.print("<script>window.recs="+(bolist.getRowCount())+";window.onload=actNumberOfArea</script> "); //$NON-NLS-1$ //$NON-NLS-2$
 
/*
         
</TABLE></DIV></TD></TR></DIV></TD></TR>
*/



        
        


        
//end GRID
    
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
        bolist.removeFilter();
        out.write("                             </tbody>".toCharArray()); //$NON-NLS-1$
        out.write("                        </table>".toCharArray()); //$NON-NLS-1$
        out.write("                    </div>".toCharArray()); //$NON-NLS-1$
        out.write("                </td>".toCharArray()); //$NON-NLS-1$
        out.write("            </tr>".toCharArray()); //$NON-NLS-1$
        out.write("        </table>".toCharArray()); //$NON-NLS-1$
    }


    public void render(PageContext page,boBridgeIterator boIterator,docHTML DOC, docHTML_controler DOCLIST , GridProperties props ) throws boRuntimeException,java.io.IOException {
        JspWriter out=page.getOut();
        String xvalue=null;

        if( props.columns == null )
        {
            props.columns = p_cols;
        }
        
        int hui=this.hashCode();
        int hui2=out.hashCode();
        
        Vector cols = props.columns;
        String mode = props.mode;
        String options=props.options;
        String waitingDetachAttribute=props.waitingDetachAttribute;
        int countSelectd=0;
        
        //p_listBridgeAttributes=props.listBridgeAttributes;
        p_canSelectRows=props.canSelectRows;
        p_showIcon  =props.showIcon;
        p_showState =props.showState;
        p_canExpandRows= props.canExpandRows;


        p_barStatus=props.barStatus;
        p_colsFooter=props.colsFooter;
        p_barFilter=props.barFilter;

        boolean showIcon     =p_showIcon=props.showIcon;
        boolean showState    =p_showState=props.showState;
        boolean showPreview  =p_canExpandRows=props.showPreview;
        boolean canSelectRows=p_canSelectRows=props.canSelectRows;
        
        String voui=page.getRequest().getParameter("voui"); //$NON-NLS-1$
        if ( voui != null )
        {
            hui= ClassUtils.convertToInt(voui);
        }
        

        out.write(Gtxt.text[Gtxt.OPEN_GRID]);

        String menu=props.menu;
        if ( menu == null || menu.equalsIgnoreCase("YES")  ){ //$NON-NLS-1$
        
            out.write(Gtxt.text[Gtxt.OPEN_MENU]);
            out.print(p_doc.getHEIGHT_HTMLforToolbar(page,(boObjectList)boIterator.getBridgeHandler())); //width;
            out.write(Gtxt.text[Gtxt.OPEN_MENU+1]);
            p_doc.writeHTMLforToolbar(DOCLIST, page,(boObjectList)boIterator.getBridgeHandler(),hui);
            out.write(Gtxt.text[Gtxt.CLOSE_MENU]);
            
        }

        
        out.write(Gtxt.text[Gtxt.OPEN_GRID+1]);
        out.write(Gtxt.text[Gtxt._GRID_HEADER  ]);
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+1]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+2]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+3]);        
        out.print(hui2);
        out.write(Gtxt.text[Gtxt._GRID_HEADER+4]);        
 
        
        out.write(Gtxt.text[Gtxt.COLGROUP]);
        
        boolean canShowLines=false;
//        if (  bolist instanceof bridgeHandler )
//        {
//            canShowLines=true;
//        }
        
//        if ( !props.showLines && canShowLines )
//        {
//            canShowLines=false;
//        }
//        if ( canShowLines )
//        {
//            if (showPreview||showIcon||showState || canSelectRows){
//                
//                //out.write(Gtxt.text[Gtxt.COL_20]);
//                out.print( "<COL width='25'/>");
//            }
//            else {
//                out.write(Gtxt.text[Gtxt.COL_18 ]);
//                out.write(Gtxt.text[Gtxt.COL_2 ]);
//            }
//            
//            
//            
//        }
        if (canSelectRows){
            if (showPreview||showIcon||showState){
                
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showPreview){
            if (showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showIcon){
            if (showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if(showState){
               out.write(Gtxt.text[Gtxt.COL_18 ]);
               out.write(Gtxt.text[Gtxt.COL_2 ]);
        }
        
/*
<COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="18">
   <col width="2">
   <col width="100" />
   <col >
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="98" />
   <col width="2" />
   <col width="148" />
   <col width="2" />
   
   <col width="15" />
   */       
        
        int nrCols=cols.size();
        docHTML_gridCol c;

        //1ª COLUNA QUE É A RESIZABLE
        c=(docHTML_gridCol)cols.get(0);
        int hui3=c.hashCode();
        out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
        
        out.print( '\'' );
        out.print( c.p_width-2 );
        out.print( '\'' );
             
        out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        out.write(Gtxt.text[Gtxt.COL_EXPAND]);

        //------------
        
        for (int i = 1; i < nrCols; i++)  {
            c=(docHTML_gridCol)cols.get(i);

            out.write(Gtxt.text[Gtxt.COL_2 ]); //spacer

            out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
            out.print('\'');
            out.print(c.p_width-2);
            out.print('\'');
            out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        }
        out.write( Gtxt.text[ Gtxt.COL_2 ] );
        out.write( Gtxt.text[ Gtxt.COL_15 ] );
        
/*
   <tbody>
   <TR>
   <TD class="gh_std"><input id="g1643_check" class="rad" type="checkBox" id="checkBox4" name="checkBox4"></TD>

   <TD class="gh_std">&nbsp;</TD>

   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>

   <TD class="gh_std">&nbsp;</TD>
   
   <TD class="ghSep_std" >&nbsp;</TD>

   <TD id="g1643_ExpanderParent" class="ghSort_std">Nome</TD>
   <TD id="g1643_AutoExpander" class="ghSort_std">&nbsp;&nbsp;</TD>
   <TD class="ghSep_std">&nbsp;</TD>
   
   <TD class="ghSort_std">Início</TD>
    <TD class="ghSep_std">&nbsp;</TD>
    
   <TD class="ghSort_std">Fim<img class="ghSort_std" src="templates/grid/std/ghDown.gif" WIDTH="13" HEIGHT="5"></TD>
   <TD class="ghSep_std">&nbsp;</TD>
   
   <TD class="gh_std">limite</TD>
   <TD class="ghSep_std">&nbsp;</TD>
      
   <TD class="gh_std">Processo associado</TD>
      <TD class="ghSep_std">&nbsp;</TD>
      

   <TD class="gh_std" width="14"><img src="templates/grid/std/ghRefresh.gif" width="13" height="13" /></TD>
   
   </tr>
   </tbody>
   </table>
*/
        out.write(Gtxt.text[ Gtxt.OPEN_TBODY ]);
        
        out.write(Gtxt.text[ Gtxt.OPEN_TR ]);
//        if ( canShowLines )
//        {
//        
//            
//            
//            //.----------------------para modo template
//            boObject objParent = boIterator.getBridgeHandler().getParent();
////            if (  bolist instanceof bridgeHandler )
////            {
////                objParent = bolist.getParent();
////            }
//     
//            if( objParent!=null && objParent.getMode() == boObject.MODE_EDIT_TEMPLATE  )
//                {
//                // && xattributes.get("noRenderTemplate")==null){
//                StringBuffer toPrint = new StringBuffer();
//                toPrint.append("<img onclick=\"openAttributeMap('");
//                String atrName = boIterator.getBridgeHandler().getName();
//                StringBuffer nameH = new StringBuffer();
//                nameH.append( objParent.getName() ).append( "__" ).append( objParent.bo_boui ).append("__").append( atrName );
//               
//               
//                toPrint.append( nameH );
//                toPrint.append( "','" );
//                toPrint.append( DOC.getDocIdx() );
//                toPrint.append( "','" );
//                toPrint.append( objParent.getAttribute("TEMPLATE").getValueString() );
//                toPrint.append("')\" src='templates/form/std/iconformula_on.gif' class='imgonoff' />");
//                out.write("<TD class='gh_std' >");
//                out.write( toPrint.toString() );
//                out.write("</TD>");
//              }
//              else
//              {
//                  out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
//              }
//
//            
//            //-------------------------
//             if (!( showPreview || showIcon || showState||!canSelectRows )){
//                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
//            }
//        }
        
        if ( canSelectRows ){
            int tab = 1;
            if(page.getRequest().getParameter("myIDX") == null) //$NON-NLS-1$
            {
                //parent_attribute=detail&parent_boui=2129992
                String att = page.getRequest().getParameter("parent_attribute"); //$NON-NLS-1$
                String boui = page.getRequest().getParameter("parent_boui"); //$NON-NLS-1$
                tab = DOC.getTabindex(DOC.GRID_CHECK_ALL, null, boui, att, DOCLIST) ;
            }
            else
            {
                tab = DOC.getTabindex(DOC.GRID_CHECK_ALL, DOCLIST) ;   
            }
            out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL  ] );
            out.print( hui);
            out.write( "_check' class='rad' type='checkBox' tabindex='" + tab  + "'/></TD>" ); //$NON-NLS-1$ //$NON-NLS-2$
            //out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL+1] );
            //out.print(hui);
            //out.write( Gtxt.text[ Gtxt.GRIDTH_SELECTALL+2] );

            if (!( showPreview || showIcon || showState )){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
                   
        if (showPreview){
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            if (!(showIcon||showState)){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
        if (showIcon){
            //devera´ser assim
            //   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            
            if (!showState){
                out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
            }
        }
        
        if(showState){
            out.write( Gtxt.text[ Gtxt.GRIDTH_EMPTY ] );
            out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        }

        //1ª COLUNA QUE É A RESIZABLE
        //    <TD id="g1643_ExpanderParent" class="ghSort_std">Nome</TD>
        // <TD id="g1643_AutoExpander" class="ghSort_std">&nbsp;&nbsp;</TD>
        // <TD class="ghSep_std">&nbsp;</TD>
        c=(docHTML_gridCol)cols.get(0);
        out.write( Gtxt.text[ Gtxt.GRIDTH_EXPANDER ] );
        out.print(hui2);
        out.write( Gtxt.text[ Gtxt.GRIDTH_EXPANDER+1 ] );
        out.print( c.getLabel() );

        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        
     //   out.write( Gtxt.text[ Gtxt.GRIDTH_AUTOEXPANDER  ] );
     //   out.print(hui2);
     //   out.write( Gtxt.text[ Gtxt.GRIDTH_AUTOEXPANDER+1 ] );
     //   out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        //------------

        //columns header when not storted
        //<TD class="gh_std">limite</TD>
        //<TD class="ghSep_std">&nbsp;</TD>
        
        //column header when sorted
        //<TD class="ghSort_std">Fim<img class="ghSort_std" src="templates/grid/std/ghDown.gif" WIDTH="13" HEIGHT="5"></TD>
         //<TD class="ghSep_std">&nbsp;</TD>
   
        for (int i = 1; i < nrCols; i++)  {
             c=(docHTML_gridCol)cols.get(i);
             switch (c.typeSorted()){
                 case ' ' : // not sorted
                 {
                      out.write( Gtxt.text[ Gtxt.OPEN_GRIDTH ] );
                      out.print(c.getLabel());
                      out.write(Gtxt.text[ Gtxt.CLOSE_TD ]);
                      break;
                 }
                 case 'A' : // Ascendente
                 {
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTHSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.IMG_TH_SORTED_ASC ] );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
                 case 'D' : { // descendente
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTHSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.IMG_TH_SORTED_DESC ] );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
             }
             out.write( Gtxt.text[ Gtxt.GRIDTH_SEP ] );
        }

        //<TD class='gh_std' width='14'><img src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>
        out.write( Gtxt.text[ Gtxt.IMG_REFRESH ] );;
                
        
        
 /*
   </tr>
   </tbody>
   </table>
   </TD>
   </tr> close tr =25
   */
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TBODY ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        
// ---------------------------------------- HEADER GRID END ------------------  //

/* ------------BEGIN BODY GRID ---------------------------
<TR>
    <TD>
    
   <div id="g1643_divc" class="gContainerLines_std">
   <table id="g1643_body" onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643');onClick_GridBody_std(event)" id="gridbody$0$activitylist" cellpading="2" cellspacing="0" class="gBodyLines_std">
   <COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="20">
   <col />
   <col width="100" />
   <col style="background-color:#EAEDEE;" width="100" /> //COLUNA SELECIONADA
   <col width="100" />
   <col width="150" />

   ROW NORMAL
   
   <TR id="g1643$1222" >
		<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
		<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">01/01/2003</TD>
		<TD class="gCell_std">2654</TD>
   </tr>

   ROW SELECTED
   
   <TR id="B$123455">
		<TD class="gCellSel_std"><input class="rad" type="checkBox" id="checkBox1" value="on" checked name="checkBox1"></TD>
		<TD class="gCellSel_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCellSel_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCellSel_std"><img src="resources/activity/state_archive.gif" height="16" width="16"></TD>
		<TD class="gCellSel_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCellSel_std">&nbsp;</TD>
		<TD class="gCellSel_std">&nbsp;</TD>
		<TD class="gCellSel_std">01/01/2003</TD>
		<TD class="gCellSel_std">2654</TD>
   </tr>

   ROW COM EXPNDE ROW
 
   <TR id="B$1222" >
		<TD class="gCellExpanded_std"><input class="rad" type="checkBox" id=checkBox3 name=checkBox3></TD>
		<TD class="gCellExpanded_std"><img src="templates/grid/std/quickview_on.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCellExpanded_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCellExpanded_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCellExpanded_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCellExpanded_std">&nbsp;</TD>
		<TD class="gCellExpanded_std">&nbsp;</TD>
		<TD class="gCellExpanded_std">01/01/2003</TD>
		<TD class="gCellExpanded_std">2654</TD>
   </tr>
  

   ROW EXPANDED
   <TR id="expandedRow"  >
		<TD class="gCell_std"></TD>
		<TD colspan="8" class="gCellQuickView_std">

            //FORM QUICKVIEW
        
		</TD>
   </tr>
   

   <!-- FIM DE ROW EXPANDIDA -->

 </table>   <!--END TABLE BODY  !-->
</div> <!--END TABLE BODY CONTAINER  !-->


</TD>
</tr>
   
   */
        
        out.write( Gtxt.text[ Gtxt.OPEN_TR ] );
        out.write( Gtxt.text[ Gtxt.OPEN_TD ] );
/*    
   <div id="g1643_divc" class="gContainerLines_std">
   <table id="g1643_body" onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643');onClick_GridBody_std(event)" id="gridbody$0$activitylist" cellpading="2" cellspacing="0" class="gBodyLines_std">
   <COLGROUP>
   <col width="20">
   <col width="20">
   <col width="20">
   <col width="20">
   <col />
   <col width="100" />
   <col style="background-color:#EAEDEE;" width="100" /> //COLUNA SELECIONADA
   <col width="100" />
   <col width="150" />
 */
        out.write( Gtxt.text[ Gtxt.GRIDDIV_CONT ] );
        out.print( hui );
        out.write( Gtxt.text[ Gtxt.GRIDDIV_CONT+1 ] );

        boObject obj;
            
 
        

        String firstColName = null;
        for (int i = 0; i <  p_cols.size() ; i++) 
        {
             boDefAttribute atrdef = ((docHTML_gridCol)p_cols.get(i)).p_atr;
             if( atrdef!=null)
             {
                firstColName=atrdef.getName();
                break;
             }
            
        }
        
        

        if(!boIterator.getBridgeHandler().isEmpty())
        {
            
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT   ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 1 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 2 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 3 ] );
            out.print( hui );
            if(props.userClick != null && !"".equals(props.userClick)) //$NON-NLS-1$
            {
                out.print("');");         //$NON-NLS-1$
                out.print("\"  cellpadding='2' cellspacing='0' "); //$NON-NLS-1$
            }
            else
            {
                out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 4 ] );
            }
            
            out.print(" options='"+options+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !waitingDetachAttribute.equals("") ) //$NON-NLS-1$
            { 
                out.print("waitingDetachAttribute='"+waitingDetachAttribute+"' " );   //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print(" mode='"+mode+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(" letter_field='"+firstColName+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(" ondblclick=\"so('g"+hui+"');onDoubleClick_GridBody(event)\" " ); //$NON-NLS-1$ //$NON-NLS-2$
                           
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );
        
            
        }
        else{
            
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT   ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT_NONE   ] );
            //out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 1 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 2 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 3 ] );
            out.print( hui );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 4 ] );
            out.print(" mode='' " ); //$NON-NLS-1$
            out.print(" options='"+options+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            if ( !waitingDetachAttribute.equals("") ) //$NON-NLS-1$
            { 
                out.print("waitingDetachAttribute='"+waitingDetachAttribute+"' " );   //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print(" letter_field='"+firstColName+"' " ); //$NON-NLS-1$ //$NON-NLS-2$
            //out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );
            out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT_NONE + 1   ] );  
        }
        
        
//        out.print( hui );
//        out.write( Gtxt.text[ Gtxt.GRIDBODY_CONT + 5 ] );

        out.write( Gtxt.text[ Gtxt.COLGROUP ] );
        
        if ( canShowLines  )  out.print( "<COL width='25'/>"); //$NON-NLS-1$
        if ( canSelectRows  )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showPreview  )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showIcon       )  out.write( Gtxt.text[ Gtxt.COL_20  ] );
        if ( showState      )  out.write( Gtxt.text[ Gtxt.COL_20  ] );

        //primeira coluna é sempre resizable
        out.write( Gtxt.text[ Gtxt.COL_EXPAND ] );
        
     //<col width="100" />
     //<col class='sel' width="100" /> //COLUNA SELECIONADA
   
        for (int i = 1; i < nrCols; i++)  {

             c=(docHTML_gridCol)cols.get(i);
             out.write( Gtxt.text[ Gtxt.OPEN_COL_WIDTH ] );
             out.print( '\'' );
             out.print( c.p_width );
             out.print( '\'' );
             if (c.typeSorted()!=' '){ 
                out.write( Gtxt.text[ Gtxt.CLASS_SEL ] );
             }
             
             out.write(Gtxt.text[ Gtxt.CLOSE_COL_WIDTH ]);
        }

        

/*
        ROW NORMAL
   
   <TR id="g1643$1222" boui='123456' >
		<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
		<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
		<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
		<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
		
		<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">&nbsp;</TD>
		<TD class="gCell_std">01/01/2003</TD>
		<TD class="gCell_std">2654</TD>
   </tr>
*/
        boIterator.beforeFirst();

        
        boolean isSelect;
        boolean isExpanded;
        String xExp;
        String objName;
        
         if(props.showSelectNone)
           {
             out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] ); 
             
             out.print(hui);
             out.print("__"); //$NON-NLS-1$
             //out.print( obj.getName() );
             out.print(boIterator.getBridgeHandler().getBoDef().getName());
             out.print("__"); //$NON-NLS-1$
             out.print(0);
             out.print('\'');

            if(props.userClick != null && !"".equals(props.userClick)) //$NON-NLS-1$
            {       
                String aux = URLDecoder.decode( props.userClick, boConfig.getEncoding() );
//                out.print(" onclick=\"");
                out.print(" userClick=\""); //$NON-NLS-1$
                out.print(aux.replaceAll("OBJECT_LIST_BOUI",String.valueOf(""))); //$NON-NLS-1$ //$NON-NLS-2$
                out.print(";\" "); //$NON-NLS-1$
            }

             
             out.print(" selectRecordNone style='height:25'>"); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
             out.print("<IMG selectRecordNone src='templates/grid/std/none.gif' height=16 width=16 />"); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
             out.print("<TD  style='color:#BC9C5E' selectRecordNone class='gCell_std' COLSPAN='"+(nrCols+(canShowLines?1:0)+(showIcon?1:0)+(showState?1:0)+(canSelectRows?1:0))+"'>"); //$NON-NLS-1$ //$NON-NLS-2$
             out.print(JSPMessages.getString("docHTML_grid.118")); //$NON-NLS-1$
             out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
             out.write( Gtxt.text[ Gtxt.CLOSE_TR ]);
            
        }
        byte isOrphan=0;
        byte hasRigthsToSaveParent=-1;
        
        
        if ( boIterator.getBridgeHandler().getRowCount() > 0 )
        {
            while (boIterator.next()){
                 try
                 {
                     obj=boIterator.currentRow().getObject();
                     objName=obj.getName();
//                     isSelect=true;
//                     isSelect=bolist.currentObjectIsSelected();
                     boIterator.getBridgeHandler().rows(boIterator.getRow());
                     isSelect=boIterator.getBridgeHandler().currentObjectIsSelected();
                     xExp=boIterator.getBridgeHandler().getRowProperty("EXPANDED"); //$NON-NLS-1$
                     xExp=null;
                     isExpanded=false;
                     if (xExp!=null)isExpanded=boIterator.getBridgeHandler().getRowProperty("EXPANDED").equals("Y"); //$NON-NLS-1$ //$NON-NLS-2$
                     else isExpanded=false;
                     //obj.getName();

                  
                    out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] );
                    
                     
                     out.print(hui);
                     out.print("__"); //$NON-NLS-1$
                     out.print( obj.getName() );
                     out.print("__"); //$NON-NLS-1$
                     out.print(obj.bo_boui);
                     out.print('\'');

                    if(props.userClick != null && !"".equals(props.userClick)) //$NON-NLS-1$
                    {       
                        String aux = URLDecoder.decode( props.userClick, boConfig.getEncoding() );
//                        out.print(" onclick=\"");
                        out.print(" userClick=\""); //$NON-NLS-1$
                        out.print(aux.replaceAll("OBJECT_LIST_BOUI",String.valueOf(obj.getBoui()))); //$NON-NLS-1$
                        out.print(";\" "); //$NON-NLS-1$
                    }
             
                     if ( !obj.exists() ) out.print(" exists=no "); //$NON-NLS-1$
                     
                     if ( isOrphan==0 )
                     {
                         if ( !obj.getBoDefinition().getBoCanBeOrphan() )
                         {
                             isOrphan=2;
                         }
                         else isOrphan=1;
                     }
                     if ( isOrphan==2 )
                     {
                             out.print(" orphan=no "); //$NON-NLS-1$
                     }
                     if ( hasRigthsToSaveParent==-1  )
                     {
                         boObject p=boIterator.getBridgeHandler().getParent();
                         if ( p !=null )
                         {
                           if ( securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                                && securityOPL.canWrite(p)|| (!p.exists() && securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD)))
                                {
                                   hasRigthsToSaveParent=1;
                                }
                                else
                                {
                                    hasRigthsToSaveParent=0;
                                }
                         }
                     }
                     
                     if ( hasRigthsToSaveParent==0 )
                     {
                         out.print(" hasRightsToSaveParent=no "); //$NON-NLS-1$
                     }
                     out.print('>');
                     //out.print( Gtxt.text[ Gtxt.CLOSE_TR_ID ] );
                     
        //<TD class="gCell_std"><input class="rad" type="checkBox" id=checkBox2 name=checkBox2></TD>
                    
//                    if( canShowLines ){
//                            out.print("<TD style='' class='gCellNumber_std'>");
//                            AttributeHandler xlinatt;
//                            int xlin;
//                            if (  (xlinatt=(( bridgeHandler) bolist).getAttribute("LIN") ) != null )
//                            {
//                                xlin = (int)xlinatt.getValueLong();
//                            }
//                            else
//                            {
//                                xlin = boIterator.getRow();
//                            }
//                            
//                            out.print("<img width=18 title='Seleccione a linha e depois clique arraste para mover a linha'  class=\"numberLine\" ondragstart=\"grid_StartMoveLine()\"  height=16 lin="+xlin+" src=resources/numbers/"+xlin+".gif>");
//                           out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
//                     }
                    
                     if( canSelectRows ){
                        if ( isExpanded ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                            if ( isSelect ) {
                                out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL ] );
                                out.print( objName );
                                out.print("__"); //$NON-NLS-1$
                                out.print(obj.bo_boui);
                                
                                out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL+1 ] );
                            }
                            else out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL ] );
                            
                        }
                        else if ( isSelect ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL ] );
                            out.print( objName );
                            out.print("__"); //$NON-NLS-1$
                            out.print(obj.bo_boui);
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_SEL+1 ] );
                        }
                        else {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL ] );
                            out.print( objName );
                            out.print("__"); //$NON-NLS-1$
                            out.print(obj.bo_boui);
                            out.write( Gtxt.text[ Gtxt.GRID_INPUT_NOTSEL+1 ] );
                        }
                     }
        
        //<TD class="gCell_std"><img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>
                     if (showPreview){
                         
                         if ( isExpanded ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_ON ] );
                        }
                        else if ( isSelect ) {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_OFF ] );
                         
                        }
                        else {
                            out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                            out.write( Gtxt.text[ Gtxt.IMG_SRC ] );
                            out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
                            out.write( Gtxt.text[ Gtxt.IMG_QUICK_VIEW_OFF ] );
                        }
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     }
        
        //<TD class="gCell_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
        
                     if ( showIcon ) {
                        if      ( isExpanded ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                        else if ( isSelect ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                        else  out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                        
                        
                        boObject o=null;
                        if ( obj.getName().equals("Ebo_Template") ) //$NON-NLS-1$
                        {   
                            o=obj.getBoManager().loadObject( obj.getEboContext(),"Ebo_ClsReg",obj.getAttribute("masterObjectClass").getValueLong());                 //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        else if ( obj.getName().equals("Ebo_ClsReg") ) //$NON-NLS-1$
                        {
                            o=obj;   
                        }
                             
                        out.write( Gtxt.text[ Gtxt.IMG_SRC1 ] );
                        out.print( obj.getLabel() );
                        if( o!= null ) out.print(JSPMessages.getString("docHTML_grid.340")+o.getAttribute("description").getValueString() ); //$NON-NLS-1$ //$NON-NLS-2$
                        
                        out.write( Gtxt.text[ Gtxt.IMG_SRC2 ] );                        
                        if ( o==null)
                        {                            
                            out.print( obj.getSrcForIcon16() );
                            out.print( "'" ); //$NON-NLS-1$
                            out.write( " ondragstart=\"startDragObject(".toCharArray()); //$NON-NLS-1$
//                            out.write( Gtxt.text[ Gtxt.ICON_16 ] );
                        }
                        else
                        {
                            out.write( Gtxt.text[ Gtxt.RESOURCES_DIR ] );
                            out.print(o.getAttribute("name").getValueString()); //$NON-NLS-1$
                            if ( o.getName().equals("Ebo_ClsReg") ) //$NON-NLS-1$
                            {
                                out.write( Gtxt.text[ Gtxt.ICON_16 ] );
                            }
                            else
                            {                                
                                out.write( Gtxt.text[ Gtxt.ICON_16_TMPL ] );
                            }
                        }
                        
                        out.print( "'"+obj.getName()+"',"+obj.getBoui()+","+obj.exists()+","+hui ); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        out.write( Gtxt.text[ Gtxt.ICON_16_REST ] );
                            
                        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     }
        
        //<TD class="gCell_std"><img src="resources/activity/state_hold.gif" height="16" width="16"></TD>
        
                     if ( showState ){
                        if      ( isExpanded ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                        else if ( isSelect ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                        else  out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
                        out.print( obj.getICONComposedState() );
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        
                     }
        
        //<TD class="gCell_std">2323 2390239 23908 23908230 09238 0239823098 230982309823 </TD>
        
                     for (int i = 0; i < nrCols; i++)  {
                        c=(docHTML_gridCol)cols.get(i);
                                        
                        if  ( isExpanded ) out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_EXPAND ] );
                        else if ( isSelect ) {
                        out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_SELECT ] );
                        countSelectd++;
                        }
                        else  out.write( Gtxt.text[ Gtxt.GRIDTD_CELL_NORMAL ] );
        
        //atencao aqui tem que ser diferenciado consoante o tipo do atributo
                        //xvalue=obj.getAttribute(c.p_atr.getName()).getValueString();
                        if ( p_listBridgeAttributes )
                        {
                            xvalue=c.getHTML( obj , (bridgeHandler)  boIterator.getBridgeHandler() , DOC , DOCLIST );
                        }
                        else if ( props.editAttributes )
                        {
                            xvalue=c.getHTML( obj , DOC , DOCLIST );
                        }
                        else
                        {
                            xvalue=c.getHTML( obj );
                        }
                        
                        
                        if(xvalue.equals("")) out.write( Gtxt.text[ Gtxt.NBSP ] ); //$NON-NLS-1$
                        else out.print( xvalue );
        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                        
                     }
                     
                     
        //escrever a row Expandida
                     if (showPreview && isExpanded){
        
             /*        ROW EXPANDED
           <TR id="expandedRow"  >
                <TD class="gCell_std"></TD>
                <TD colspan="8" class="gCellQuickView_std">
        
                    //FORM QUICKVIEW
                
                </TD>
           </tr>
           */          
                        out.write( Gtxt.text[ Gtxt.EXPANDED_ROW ] );
                        out.print( nrCols+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)-1); //colSpan
                        out.write( Gtxt.text[ Gtxt.EXPANDED_ROW+1 ] );
        
                        //chamar quickViewForm do objecto
                        out.print("not implemented yet"); //$NON-NLS-1$
                        
                        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
                            
                        
                     }
        //NOVA LINHA
                out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
                // se quiser acrescentar alguma cois debaixo da lina é aqui
                
                 }
                 catch (Exception e)
                 {
                     CharArrayWriter ecr = new CharArrayWriter();
                     
                     PrintWriter pr = new PrintWriter(ecr);
                     e.printStackTrace( pr );
                     pr.flush();
                     ecr.flush();
                     pr.close();
                     ecr.close();
                     out.print("<TR><TD  COLSPAN='"+(nrCols+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)+1)+"' >"+ //$NON-NLS-1$ //$NON-NLS-2$
                     e.getClass().getName()+":"+e.getMessage() //$NON-NLS-1$
                     +"<span style='display:none' >"+ecr.toString()+"</span>" //$NON-NLS-1$ //$NON-NLS-2$
                     +"</TD></TR>" ); //$NON-NLS-1$
                 }
                 
            }
        }
        else
        {
            //rowCount ==0 
             
             out.write( Gtxt.text[ Gtxt.OPEN_TR_ID ] );
             
             out.print(hui);
             out.print("__"); //$NON-NLS-1$
             out.print( boIterator.getBridgeHandler().getBoDef().getName() );
             out.print("__"); //$NON-NLS-1$
             out.print("null"); //$NON-NLS-1$
             //out.print(obj.bo_boui);
             out.print('\'');
             out.print(" exists=no "); //$NON-NLS-1$
             
              if ( hasRigthsToSaveParent==-1  )
             {
                 boObject p=boIterator.getBridgeHandler().getParent();
                 if ( p !=null )
                 {
                   if ( securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.WRITE)
                        && securityOPL.canWrite(p)|| (!p.exists() && securityRights.hasRights(p,p.getName(),p.getEboContext().getBoSession().getPerformerBoui(),securityRights.ADD)))
                        {
                           hasRigthsToSaveParent=1;
                        }
                        else
                        {
                            hasRigthsToSaveParent=0;
                        }
                 }
             }
             
             if ( hasRigthsToSaveParent==0 )
             {
                 out.print(" hasRightsToSaveParent=no "); //$NON-NLS-1$
             }
             out.print('>');
            
            out.print("<TD select='none' COLSPAN='"+(nrCols+(showIcon?1:0)+(canShowLines?1:0)+(showState?1:0)+(canSelectRows?1:0)+1)+"'><TABLE id='g"); //$NON-NLS-1$ //$NON-NLS-2$
            out.print(hui);
            out.print("' select='none' style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD select='none' style='COLOR: #999999; BORDER:0px' align=middle width='100%'>"); //$NON-NLS-1$
            if( boIterator.getBridgeHandler().getBoDef().getLabel() == null )
            {
                out.print(JSPMessages.getString("docHTML_grid.366"));     //$NON-NLS-1$
            }
            else
            {
            out.print(JSPMessages.getString("docHTML_grid.367")+boIterator.getBridgeHandler().getBoDef().getLabel()+JSPMessages.getString("docHTML_grid.368")); //$NON-NLS-1$ //$NON-NLS-2$
            }
            out.print("</TD></TR></TBODY></TABLE></TD>"); //$NON-NLS-1$
            
        }

    out.write( Gtxt.text[ Gtxt.END_BODY_GRID ] );
    if ( p_colsFooter ){

/*
<!-- begin footer columns -->
<TR  height=25>
   <TD valign=top >
   <table id="g1642_body" cellpadding="2" cellspacing="0" style="height:25px" class="gfc_std">*/

       out.write( Gtxt.text[ Gtxt.GRID_COLS_FOOTERH ]);  
       out.print( hui3 );
       out.write( Gtxt.text[ Gtxt.GRID_COLS_FOOTERH+1 ]);
       
        
        out.write(Gtxt.text[Gtxt.COLGROUP]);
        if (canSelectRows){
            if (showPreview||showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showPreview){
            if (showIcon||showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if (showIcon){
            if (showState){
                out.write(Gtxt.text[Gtxt.COL_20]);                
            }
            else {
                out.write(Gtxt.text[Gtxt.COL_18 ]);
                out.write(Gtxt.text[Gtxt.COL_2 ]);
            }
        }
        if(showState){
               out.write(Gtxt.text[Gtxt.COL_18 ]);
               out.write(Gtxt.text[Gtxt.COL_2 ]);
        }


        

        //1ª COLUNA QUE É A RESIZABLE
        c=(docHTML_gridCol)cols.get(0);
        out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
        out.print('\'');
        out.print(c.p_width-2);
        out.print('\'');
        out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        out.write(Gtxt.text[Gtxt.COL_EXPAND]);

        //------------
        
        for (int i = 1; i < nrCols; i++)  {
            c=(docHTML_gridCol)cols.get(i);

            out.write(Gtxt.text[Gtxt.COL_2 ]); //spacer

            out.write(Gtxt.text[Gtxt.OPEN_COL_WIDTH]);
            out.print('\'');
            out.print(c.p_width-2);
            out.print('\'');
            out.write(Gtxt.text[Gtxt.CLOSE_COL_WIDTH]);
        }
        out.write( Gtxt.text[ Gtxt.COL_2 ] );
        out.write( Gtxt.text[ Gtxt.COL_15 ] );
  
  /*      
   <tbody>
   <TR>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfc_std">&nbsp;</TD>
   <TD class="gfcSep_std" >&nbsp</TD>
   <TD id="g$ExpanderParent" class="gfcSort_std">Nome</TD>
   <TD id="g$AutoExpander" class="gfcSort_std">&nbsp;&nbsp;</TD>
   <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfcSort_std">Início</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfcSort_std">Fim</TD>
      <TD class="ghSep_std">&nbsp;</TD>
   <TD class="gfc_std">limite</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
   <TD class="gfc_std">Processo associado</TD>
      <TD class="gfcSep_std">&nbsp;</TD>
      

   <TD class="gfc_std" width="14"></TD>
   </tr>
   </tbody>
   </table>
 

   </TD>
   </tr>
   <!-- end footer COlUMNS -->
*/
        out.write(Gtxt.text[ Gtxt.OPEN_TBODY ]);
        
        out.write(Gtxt.text[ Gtxt.OPEN_TR ]);
        
        if ( canSelectRows ){
            
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
  
            if (!( showPreview || showIcon || showState )){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
                   
        if (showPreview){
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            if (!(showIcon||showState)){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
        if (showIcon){
            //devera´ser assim
            //   <TD class="gh_std"><img src="resources/activity/ico16.gif" height="16" width="16"></TD>
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            
            if (!showState){
                out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
            }
        }
        if(showState){
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EMPTY  ] );
            out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        }

        
        c=(docHTML_gridCol)cols.get(0);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EXPANDER ] );
        out.print(hui3);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_EXPANDER+1 ] );
        out.print( c.getLabel() );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_AUTOEXPANDER  ] );
        out.print(hui3);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_AUTOEXPANDER+1 ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ]);
        out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        //------------

        
        for (int i = 1; i < nrCols; i++)  {
             c=(docHTML_gridCol)cols.get(i);
             switch (c.typeSorted()){
                 case ' ' : // not sorted
                 {
                      out.write( Gtxt.text[ Gtxt.OPEN_GRIDTH ] );
                      out.print(c.getLabel());
                      out.write(Gtxt.text[ Gtxt.CLOSE_TD ]);
                      break;
                 }
                 case 'A' : // Ascendente
                 {
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTDFCOLSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
                 case 'D' : { // descendente
                     out.write( Gtxt.text[ Gtxt.OPEN_GRIDTDFCOLSORTED ] );
                     out.print( c.getLabel() );
                     out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
                     break;
                 }
             }
             out.write( Gtxt.text[ Gtxt.GRIDTDFCOL_SEP ] );
        }

        //<TD class='gh_std' width='14'><img src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>
        out.write( Gtxt.text[ Gtxt.IMG_REFRESH ] );;
                
        
        
 /*
   </tr>
   </tbody>
   </table>
   </TD>
   </tr> close tr =25
   */
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TBODY ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TD ] );
        out.write( Gtxt.text[ Gtxt.CLOSE_TR ] );
        
    }
    
//    if( p_barStatus){
//
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR   ] );
//       out.print(hui);
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR+1   ] );
//       
//       
//      // if (props.canSelectRows)
//      // {
//           //out.print(countSelectd);
//          // out.print("&nbsp;de&nbsp;");
//          if ( !boIterator.getBridgeHandler().isEmpty())
//          {
//            out.print(boIterator.getBridgeHandler().getRowCount());
//            if (boIterator.getBridgeHandler().getRowCount()==30)
//                out.print("&nbsp Registos por Página");            
//            else
//                out.print("&nbsp Registos");          
//          }
//          // out.print("&nbsp;seleccionados&nbsp;");
//      // }
////       if ( bolist.getUserQuery() != null && !bolist.getUserQuery().equals("<cleanFilter/>") )
////       {
////           out.print("<span style='color:#990000'> C/Filtro</span>");
////       }
//              
//       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR +2]  );
//       out.print(hui);
//       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR +3  ]  );
//       out.print(hui);
//       out.print(Gtxt.text[ Gtxt.GRID_STATUS_BAR  +4 ] );
//
////       templates/grid/std/
// //page_l0.gif
// 
//       out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
//       out.write( Gtxt.text[ Gtxt.SETA_LEFT ] );
//       if ( boIterator..getPage() > 1)
//       {
//           out.print("a.gif");
//       }
//       else
//       {
//           out.print(".gif");
//       }
//        
//        
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +5  ]  );
////       Page  //Page ou Página;
//       out.print("página");
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +6  ]  );
//       out.print(hui);
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +7  ]  );
//       //1 //page NUMBER
//       out.print(bolist.getPage());
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +8  ]  );
//       out.print(hui);
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +9  ]  );
//       //templates/grid/std/page_r0.gif //path para a seta direita
//       out.write( Gtxt.text[ Gtxt.TEMPLATE_DIR ] );
//       out.write( Gtxt.text[ Gtxt.SETA_RIGHT ] );
//       if ( bolist.haveMorePages() )
//       {
//           out.print("a.gif");
//       }
//       else
//       {
//           out.print(".gif");
//       }
//       out.write( Gtxt.text[ Gtxt.GRID_STATUS_BAR +10  ]  );
//
//        
//    }
//    if( p_barFilter && firstColName!=null ){
//
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR   ]  );
//        out.print(hui);
//     
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR +1  ]  );
//        out.print(hui);
//
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR +2  ]  );
//        out.print(hui);
//
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +3 ]  );
//        out.print(hui);
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +4 ]  );
//        
//        String letter=bolist.getSearchLetter();
//        if ( letter==null )
//        {
//            letter="Todos";
//        }
//        
//        out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +5 ]  );
//        
//        String bar="Todos</TD><TD>#</TD><TD>a</TD><TD>b</TD><TD>c</TD><TD>d</TD><TD>e</TD><TD>f</TD><TD>g</TD><TD>h</TD><TD>i</TD><TD>j</TD><TD>k</TD><TD>l</TD><TD>m</TD><TD>n</TD><TD>o</TD><TD>p</TD><TD>q</TD><TD>r</TD><TD>s</TD><TD>t</TD><TD>u</TD><TD>v</TD><TD>w</TD><TD>x</TD><TD>y</TD><TD>z</TD></TR></TBODY></TABLE></TD></TR>";
//        bar=bar.replaceAll(letter,"<font color='#990000'><b>"+letter+"</b></font>" );
//        out.print( bar );
//       //out.write( Gtxt.text[ Gtxt.GRID_FILTER_BAR  +6 ]  );
//     
//        
//    }
    
    
 
//    if( p_colsFooter || p_barStatus || p_barFilter )
//    {
//        out.write( Gtxt.text[ Gtxt.GRID_SPACER_TOSCROLL ] );
//    }
// 
//     out.print("<script>window.recs="+(bolist.getRowCount())+";window.onload=actNumberOfArea</script> ");
 
/*
         
</TABLE></DIV></TD></TR></DIV></TD></TR>
*/



        
        


        
//end GRID
    
        out.write( Gtxt.text[ Gtxt.CLOSE_TABLE ] );
//        bolist.removeFilter();
    }

    private static class Gtxt {
      private static final char text[][]=new char[140][];
      private final static byte OPEN_TD          = 0;
      private final static byte CLOSE_TD         = 1;
      private final static byte EMPTY_TD         = 2;
      private final static byte CLOSE_SIMPLE_TAG = 3;
      private final static byte CLOSE_TAG        = 4;
      private final static byte COLGROUP         = 5;
      private final static byte OPEN_TABLE       = 6;
      private final static byte CLOSE_TABLE      = 7;
      private final static byte OPEN_TR          = 8;
      private final static byte CLOSE_TR         = 9;
      private final static byte OPEN_TBODY       = 10;
      private final static byte CLOSE_TBODY      = 11;
      private final static byte OPEN_TR_ID       = 12;
      private final static byte CLOSE_TR_ID      = 13;
      private final static byte IMG_SRC          = 14;
      private final static byte CLOSE_DIV        = 15;
      private final static byte OPEN_DIV         = 16;
      private final static byte NBSP             = 17;
      
      private final static byte IMG_SRC1         = 18;
      private final static byte IMG_SRC2         = 19;
      
      private final static byte TEMPLATE_DIR         = 30;
      private final static byte IMG_QUICK_VIEW_OFF   = 31;
      private final static byte IMG_QUICK_VIEW_ON    = 32;
      private final static byte RESOURCES_DIR        = 33;
      
      private final static byte IMG_16_REST          = 35;

      private final static byte OPEN_GRIDTDFCOL       = 36;
      private final static byte OPEN_GRIDTDFCOLSORTED = 37;
      
      private final static byte COL_20           = 40;
      private final static byte COL_18           = 41;
      private final static byte COL_2            = 42;
      private final static byte COL_15           = 43;
      private final static byte COL_EXPAND       = 44;

      private final static byte  GRIDTDFCOL_AUTOEXPANDER  = 45; //+46

      private final static byte SETA_LEFT = 47;
      private final static byte SETA_RIGHT = 48;
      
      
      private final static byte OPEN_COL_WIDTH   = 51;
      private final static byte CLOSE_COL_WIDTH  = 3;
      
      private final static byte OPEN_COL         = 52;
      private final static byte CLOSE_COL        = 3;

      private final static byte GRIDTD_CELL_NORMAL = 53;
      private final static byte GRIDTD_CELL_SELECT = 54;
      private final static byte GRIDTD_CELL_EXPAND = 55;
      private final static byte  GRIDTH_SELECTALL  = 56; //+57
      
      
      private final static byte GRID_COLS_FOOTERH  = 58; //+59
      

      private final static byte _GRID_HEADER       = 60; //61+62+63+64

      private final static byte  GRIDTDFCOL_EMPTY  = 65;
      private final static byte  GRIDTDFCOL_SEP    = 66;
      private final static byte  GRIDTDFCOL_EXPANDER  = 67; //+68
      private final static byte  GRID_SPACER_TOSCROLL = 69;
      
      
      private final static byte  GRIDTH_SEP        = 72;
      private final static byte  GRIDTH_EMPTY      = 73;
      private final static byte  GRIDTH_EXPANDER   = 74; //+75
      private final static byte  GRIDTH_AUTOEXPANDER = 76; //+77
      private final static byte  IMG_TH_SORTED_DESC  = 78;
      private final static byte  IMG_TH_SORTED_ASC   = 79;
      private final static byte  OPEN_GRIDTHSORTED   = 80;
      private final static byte  OPEN_GRIDTH         = 81;
      private final static byte  IMG_REFRESH         = 82;
      private final static byte  GRIDDIV_CONT        = 83; //+84
      private final static byte  GRIDBODY_CONT       = 85; //+86+87+88+89+90
      private final static byte  EXPANDED_ROW        = 91; //+92
      private final static byte  END_BODY_GRID       = 93;
      private final static byte  CLASS_SEL = 94; //era 91

      private final static byte GRID_STATUS_BAR = 95; //ate 105
      private final static byte GRID_FILTER_BAR = 106; //ate 112

      private final static byte GRID_INPUT_SEL     = 113; //+114
      private final static byte GRID_INPUT_NOTSEL  = 115; //+116

      private final static byte OPEN_GRID        = 117;//+118
      private final static byte OPEN_MENU        = 119;//+120
      private final static byte CLOSE_MENU       = 121;
      private final static byte  GRIDBODY_CONT_NONE = 122; //+123
      private final static byte ICON_16              = 125;
      private final static byte ICON_16_REST         = 126;
      private final static byte ICON_16_TMPL         = 127;
      
      private final static int OPEN_GRID_HLP        = 130;//13
      
      
      public static char TXTDEBUG = '\n';
      static {
        text[ NBSP  ]               = "&nbsp;".toCharArray(); //$NON-NLS-1$
        text[ OPEN_TD  ]            = "<TD>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TD ]            = "</TD>".toCharArray(); //$NON-NLS-1$

        text[ OPEN_TABLE  ]         = "<TABLE>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TABLE ]         = "</TABLE>".toCharArray(); //$NON-NLS-1$

        text[ OPEN_TBODY  ]         = "<TBODY>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TBODY ]         = "</TBODY>".toCharArray(); //$NON-NLS-1$

        text[ OPEN_DIV ]            = "<DIV>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_DIV ]           = "</DIV>".toCharArray(); //$NON-NLS-1$

        text[ OPEN_TR_ID ]          = "<TR ondragleave='grid_DragLeave()' ondrop='grid_Drop()' ondragover='grid_DragOver()' ondragenter='grid_DragEnter()' id='".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TR_ID ]         = "' >".toCharArray(); //$NON-NLS-1$

        text[ OPEN_TR ]          = "<TR>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TR ]            = "</TR>".toCharArray(); //$NON-NLS-1$
        
        text[ EMPTY_TD ]            = "<TD>&nbsp;</TD>".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_SIMPLE_TAG ]    =" />".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_TAG ]           =" >".toCharArray(); //$NON-NLS-1$

        text[ OPEN_COL_WIDTH ] ="<COL width=".toCharArray(); //$NON-NLS-1$
        text[ OPEN_COL ] ="<COL ".toCharArray(); //$NON-NLS-1$
        text[ COLGROUP ] ="<COLGROUP/>".toCharArray(); //$NON-NLS-1$
        text[ COL_20 ] = "<COL width='20'/>".toCharArray(); //$NON-NLS-1$
        text[ COL_18 ] = "<COL width='18'/>".toCharArray(); //$NON-NLS-1$
        text[ COL_2 ] = "<COL width='2'/>".toCharArray(); //$NON-NLS-1$
        text[ COL_15 ] = "<COL width='15'/>".toCharArray(); //$NON-NLS-1$
        text[ COL_EXPAND ] = "<COL />".toCharArray(); //$NON-NLS-1$
        
        text[ OPEN_GRID ] =(
        "<TABLE cellSpacing='0' cellPadding='0' style='height:100%;width:100%;table-layout:fixed'>").toCharArray(); //$NON-NLS-1$

        text[ OPEN_MENU   ]="<!-- BEGIN MENU -->\n<TR height='".toCharArray(); //$NON-NLS-1$
        text[ OPEN_MENU+1 ]="'><TD>\n".toCharArray(); //$NON-NLS-1$
        text[ CLOSE_MENU ]="</TD></TR> <!--END MENU -->\n".toCharArray(); //$NON-NLS-1$
        
        text[ OPEN_GRID+1] =(
          "<TR>"+TXTDEBUG+ //abrir uma TR ANTES pra colocar menu //$NON-NLS-1$
            "<TD style='height:100%;width:100%' >"+TXTDEBUG+ //$NON-NLS-1$
              "<DIV style='width:100%;height:100%;overflow-x:auto'>"+TXTDEBUG+ //$NON-NLS-1$
               "<TABLE style='height:100%;width:100%;' class='g_std' cellSpacing='0' cellPadding='0' width='100%'>"+TXTDEBUG+ //$NON-NLS-1$
                 "<TBODY>"+TXTDEBUG+ //$NON-NLS-1$
                  "<TR height='20'>"+TXTDEBUG+ //$NON-NLS-1$
                     "<TD >").toCharArray(); //$NON-NLS-1$
        
        text[ OPEN_GRID_HLP] =(
          "<TR>"+TXTDEBUG+ //$NON-NLS-1$
            "<TD align='right' style='height:30px;width:100%'><DIV onclick='window.open(\"" ).toCharArray(); //$NON-NLS-1$
            
        text[ OPEN_GRID_HLP+1] =(
            "\");' style='WIDTH:185px;HEIGHT:30px;COLOR:#4A869C;FONT:normal normal x-small verdana;' >"+ //$NON-NLS-1$
            JSPMessages.getString("docHTML_grid.408")+ //$NON-NLS-1$
            JSPMessages.getString("docHTML_grid.409")+ //$NON-NLS-1$
            JSPMessages.getString("docHTML_grid.117") //$NON-NLS-1$
        ).toCharArray();
            
        text[ _GRID_HEADER   ]="<TABLE id='g".toCharArray(); //$NON-NLS-1$
        text[ _GRID_HEADER+1 ]="_body' onmouseover=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ _GRID_HEADER+2 ]= "');onOver_GridHeader_std(event);\" onmouseout=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ _GRID_HEADER+3 ]= "');onOut_GridHeader_std(event);\" onclick=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ _GRID_HEADER+4 ]= "');onClick_GridHeader_std(event);\" cellpadding='2' cellspacing='0' style=\"height:25px\" class='gh_std'>".toCharArray(); //$NON-NLS-1$

        /*
                out.print("<table id='g"+hui+"_body' onmouseover=\"so('g"+hui+
        "');onOver_GridHeader_std(event);\" onmouseout=\"so('g"+hui+
        "');onOut_GridHeader_std(event);\" onclick=\"so('g"+hui+
        "');onClick_GridHeader_std(event);\" cellpadding='2' cellspacing='0' style=\"height:25px\" class='gh_std'>");
        */

/*   <TD class="gh_std"><input id='g1643_check' class='rad' type='checkBox' </TD>*/
        text[ GRIDTH_SELECTALL  ]="<TD class='gh_std'><INPUT id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDTH_SELECTALL+1]="_check' class='rad' type='checkBox'/></TD>".toCharArray(); //$NON-NLS-1$

        text[ GRIDTH_SEP   ] =" <TD class='ghSep_std' >&nbsp;</TD>".toCharArray(); //$NON-NLS-1$
        text[ GRIDTH_EMPTY ] =" <TD class='gh_std' >&nbsp;</TD>".toCharArray(); //$NON-NLS-1$

        text[ GRIDTDFCOL_EMPTY ] ="<TD class='gfc_std'>&nbsp;</TD>".toCharArray(); //$NON-NLS-1$
        text[ GRIDTDFCOL_SEP   ] ="<TD class='gfcSep_std' >&nbsp;</TD>".toCharArray(); //$NON-NLS-1$

        text[ GRIDTDFCOL_EXPANDER   ] ="<TD id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDTDFCOL_EXPANDER+1 ] ="_ExpanderParent' class='gfcSort_std'>".toCharArray(); //$NON-NLS-1$

        text[ GRIDTDFCOL_AUTOEXPANDER   ] = "<TD id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDTDFCOL_AUTOEXPANDER+1 ] = "_AutoExpander' class='gfcSort_std'>&nbsp;&nbsp;".toCharArray(); //$NON-NLS-1$
        text[ OPEN_GRIDTDFCOL ]          = "<TD class='gfc_std'>".toCharArray(); //$NON-NLS-1$
        text[ OPEN_GRIDTDFCOLSORTED ]    = "<TD class='gfcSort_std'>".toCharArray(); //$NON-NLS-1$
        
        
        text[ GRIDTH_EXPANDER   ] ="<TD id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDTH_EXPANDER+1 ] ="_ExpanderParent' colspan=2 class='ghSort_std'>".toCharArray(); //$NON-NLS-1$

        
        text[ GRIDTH_AUTOEXPANDER   ] = "<TD id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDTH_AUTOEXPANDER+1 ] = "_AutoExpander' class='ghSort_std'>&nbsp;&nbsp;".toCharArray(); //$NON-NLS-1$

        text[ OPEN_GRIDTH ]          = "<TD class='gh_std'>".toCharArray(); //$NON-NLS-1$
        text[ OPEN_GRIDTHSORTED ]    = "<TD class='ghSort_std'>".toCharArray(); //$NON-NLS-1$
        
        text[ IMG_TH_SORTED_DESC ]   = "<IMG class='ghSort_std' src='templates/grid/std/ghDown.gif' WIDTH='13' HEIGHT='5' />".toCharArray(); //$NON-NLS-1$
        text[ IMG_TH_SORTED_ASC  ]   = "<IMG class='ghSort_std' src='templates/grid/std/ghUp.gif' WIDTH='13' HEIGHT='5' />".toCharArray(); //$NON-NLS-1$
        text[ IMG_REFRESH        ]   = "<TD class='gh_std' width='14'><img onclick='submitGrid();' title='Clique aqui para actualizar a lista' src='templates/grid/std/ghRefresh.gif' width='13' height='13' /></TD>".toCharArray(); //$NON-NLS-1$

        text[ GRIDDIV_CONT       ]   = "<DIV id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDDIV_CONT + 1   ]   = "_divc' class='gContainerLines_std'>".toCharArray(); //$NON-NLS-1$
        
       //  "<table id='g1643_body' onmouseover=\"so('g1643');onOver_GridBody_std(event)\" onmouseout=\"so('g1643');onOut_GridBody_std(event)\" onclick=\"so('g1643');onClick_GridBody_std(event)\" cellpading='2' cellspacing='0' class='gBodyLines_std'>"
        text[ GRIDBODY_CONT     ] = "<TABLE id='g".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT + 1 ] = "_body' container='1' onmouseover=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT + 2 ] = "');onOver_GridBody_std(event)\" onmouseout=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT + 3 ] = "');onOut_GridBody_std(event)\" onclick=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT + 4 ] = "');onClick_GridBody_std(event)\" cellpadding='2' cellspacing='0' ".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT + 5 ] = " class='gBodyLines_std'>".toCharArray(); //$NON-NLS-1$

        //text[ GRIDBODY_CONT_NONE     ] = "<TABLE id='g".toCharArray();
        //text[ GRIDBODY_CONT_NONE  ] = "_body' container='1' style='height:100%' >".toCharArray();
        text[ GRIDBODY_CONT_NONE   ] = "_body' container='1' style='height:100%' onmouseover=\"so('g".toCharArray(); //$NON-NLS-1$
        text[ GRIDBODY_CONT_NONE+1 ] = " class='gBodyLines_std'>".toCharArray(); //$NON-NLS-1$
        
        text[ GRIDTD_CELL_NORMAL ] = "<TD class='gCell_std'>".toCharArray();         //$NON-NLS-1$
        text[ GRIDTD_CELL_SELECT ] = "<TD class='gCellSel_std'>".toCharArray(); //$NON-NLS-1$
        text[ GRIDTD_CELL_EXPAND ] = "<TD class='gCellExpanded_std'>".toCharArray(); //$NON-NLS-1$

        text[ GRID_INPUT_NOTSEL  ] = "<INPUT class='rad' type='checkBox' name='".toCharArray();  //$NON-NLS-1$
        text[ GRID_INPUT_NOTSEL+1] = "'/></TD>".toCharArray(); //$NON-NLS-1$

        text[ GRID_INPUT_SEL  ]   = "<INPUT class='rad' checked type='checkBox' name='".toCharArray(); //$NON-NLS-1$
        text[ GRID_INPUT_SEL+1]    = "'/></TD>".toCharArray(); //$NON-NLS-1$
        
        text[ CLASS_SEL ] = " class='sel' ".toCharArray(); //$NON-NLS-1$
        
//<img src="templates/grid/std/quickview.gif" WIDTH="13" HEIGHT="13"></TD>

        text[ IMG_QUICK_VIEW_OFF ]  = "quickview.gif' width='13' height='13'/>".toCharArray() ; //$NON-NLS-1$
        text[ IMG_QUICK_VIEW_ON ]   = "quickview_on.gif width='13' height='13'/>".toCharArray() ; //$NON-NLS-1$
        text[ ICON_16 ]             = "/ico16.gif' ondragstart=\"startDragObject( ".toCharArray(); //$NON-NLS-1$
        text[ ICON_16_TMPL ]        = "/ico16tmpl.gif' ondragstart=\"startDragObject( ".toCharArray(); //$NON-NLS-1$
        text[ ICON_16_REST ]        = " )\"  height='16' width='16'/>".toCharArray(); //$NON-NLS-1$
        text[ IMG_16_REST ]         = ".gif' height='16' width='16'/>".toCharArray(); //$NON-NLS-1$
        
        text[ TEMPLATE_DIR ]        = "templates/grid/std/".toCharArray(); //$NON-NLS-1$
        text[ RESOURCES_DIR ]       = "resources/".toCharArray();  //$NON-NLS-1$

    text[ IMG_SRC ] = "<IMG src='".toCharArray(); //$NON-NLS-1$
    text[ IMG_SRC1 ] = ("<IMG title='"+JSPMessages.getString("docHTML_grid.464")).toCharArray(); //$NON-NLS-1$
    text[ IMG_SRC2 ] = "' src='".toCharArray(); //$NON-NLS-1$
        text[ EXPANDED_ROW  ] = "<TR id='expandedRow' ><TD class='gCell_std'></TD><TD colspan='".toCharArray(); //$NON-NLS-1$
        text[ EXPANDED_ROW+1] = "' class='gCellQuickView_std'>".toCharArray(); //$NON-NLS-1$
        
        
           /*        ROW EXPANDED
   <TR id="expandedRow"  >
		<TD class="gCell_std"></TD>
		<TD colspan="8" class="gCellQuickView_std">

            //FORM QUICKVIEW
        
		</TD>
   </tr>
   */
/*   <TR  height=25>
   <TD valign=top >
   <table id="g1642_body" cellpadding="2" cellspacing="0" style="height:25px" class="gfc_std">
   */
        text [ GRID_COLS_FOOTERH  ] = "<TR  height=25><TD valign=top ><table id='g".toCharArray(); //$NON-NLS-1$
        text [ GRID_COLS_FOOTERH+1] = "_body' cellpadding='2' cellspacing='0' style='height:25px' class='gfc_std'>".toCharArray(); //$NON-NLS-1$

      // text [ END_BODY_GRID ] = "</TABLE></DIV></TD></TR></DIV></TD></TR>".toCharArray();

        text [ END_BODY_GRID ] = " </TABLE></DIV></TD></TR></TBODY></TABLE></DIV></TD></TR>".toCharArray(); //$NON-NLS-1$
        
//STATUS BAR
        
       text[ GRID_STATUS_BAR   ] ="<TR height='22'><TD><TABLE class='gfBar_status gfBar_std' id='g".toCharArray(); //$NON-NLS-1$
       //1642
       text[ GRID_STATUS_BAR+1 ] ="_status' cellSpacing='0' cellPadding='0'><TBODY><TR><TD style='PADDING-LEFT: 8px'>&nbsp;".toCharArray(); //$NON-NLS-1$
      // 0 of 4 selected
       text[ GRID_STATUS_BAR+2 ] ="</TD><TD id='g".toCharArray(); //$NON-NLS-1$
      // 1682
       text[ GRID_STATUS_BAR+3 ] =" _PageInfo' class='statusTextPage' align='right'><IMG id='g".toCharArray(); //$NON-NLS-1$
       //1682
       text[ GRID_STATUS_BAR+4 ] ="_prevPageImg' style='cursor:hand' onclick='previousPage();' hspace='6'  src='".toCharArray(); //$NON-NLS-1$
       //templates/grid/std/page_l0.gif
       text [ SETA_LEFT ] ="page_l0".toCharArray(); //$NON-NLS-1$
       text[ GRID_STATUS_BAR+5 ] ="'  align='absMiddle' />".toCharArray(); //$NON-NLS-1$
       //Page
       text[ GRID_STATUS_BAR+6 ] ="&nbsp;<SPAN id='g".toCharArray(); //$NON-NLS-1$
      // 1682
       text[ GRID_STATUS_BAR+7 ] ="_PageNum'>".toCharArray(); //$NON-NLS-1$
      // 1
       text[ GRID_STATUS_BAR+8 ] ="</SPAN><IMG id='g".toCharArray(); //$NON-NLS-1$
     //  1682
       text[ GRID_STATUS_BAR+9 ] ="_nextPageImg' style='cursor:hand' onclick='nextPage();'   hspace='6' src='".toCharArray(); //$NON-NLS-1$
       //templates/grid/std/page_r0.gif
       text [ SETA_RIGHT ] ="page_r0".toCharArray(); //$NON-NLS-1$
       text[ GRID_STATUS_BAR+10 ] ="'   align='absMiddle' />&nbsp;</TD></TR></TBODY></TABLE></TD></TR>".toCharArray(); //$NON-NLS-1$


//FILTER BAR

       
     text[ GRID_FILTER_BAR ] ="<TR height='22'><TD><TABLE id='g".toCharArray(); //$NON-NLS-1$
    // 1642
     text[ GRID_FILTER_BAR +1 ] ="_filter' onmouseover=\"so('g".toCharArray(); //$NON-NLS-1$
    // 1642
     text[ GRID_FILTER_BAR +2 ] ="');setBarFilterStyle(true,event);\" onmouseout=\"so('g".toCharArray(); //$NON-NLS-1$
    // 1642
     text[ GRID_FILTER_BAR +3 ] ="');setBarFilterStyle(false,event);\" onclick=\"so('g".toCharArray(); //$NON-NLS-1$
     //1642
     text[ GRID_FILTER_BAR +4 ] ="');onClick_BarFilter(event,false);\" class='gfFilterBar_std gfBar_std' cellSpacing='0' cellPadding='1'>".toCharArray(); //$NON-NLS-1$
    
     text[ GRID_FILTER_BAR +5 ] ="<TBODY><TR ><TD class='statusALL' noWrap='1' width='5%'>".toCharArray(); //$NON-NLS-1$
     
     //text[ GRID_FILTER_BAR +6 ] ="Todos</TD><TD>#</TD><TD>a</TD><TD>b</TD><TD>c</TD><TD>d</TD><TD>e</TD><TD>f</TD><TD>g</TD><TD>h</TD><TD>i</TD><TD>j</TD><TD>k</TD><TD>l</TD><TD>m</TD><TD>n</TD><TD>o</TD><TD>p</TD><TD>q</TD><TD>r</TD><TD>s</TD><TD>t</TD><TD>u</TD><TD>v</TD><TD>w</TD><TD>x</TD><TD>y</TD><TD>z</TD></TR></TBODY></TABLE></TD></TR>".toCharArray();

                
/*        <!-- space para ascrrolll quando tem footer -->
<TR class="gfSpacer_std">      <TD></TD> </TR>
*/
        text [ GRID_SPACER_TOSCROLL ] ="<TR class='gfSpacer_std'>      <TD></TD> </TR>".toCharArray(); //$NON-NLS-1$
      }
    }



    
}