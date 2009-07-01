/*Enconding=UTF-8*/
package netgest.bo.presentation.render.elements;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import java.math.BigDecimal;
import java.net.URLDecoder;

import java.security.Key;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import netgest.bo.boConfig;

import netgest.bo.def.boDefAttribute;
import netgest.bo.def.boDefHandler;
import netgest.bo.dochtml.docHTML;
import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.presentation.render.HTMLBuilder;
import netgest.bo.presentation.render.PageController;
import netgest.bo.presentation.render.elements.ObjectCardReport;
import netgest.bo.presentation.render.ie.components.TreeBuilder;
import netgest.bo.ql.QLParser;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectUtils;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.userquery.userquery;

import netgest.utils.ClassUtils;
import netgest.utils.ngtXMLHandler;

import netgest.utils.ngtXMLUtils;
import oracle.xml.parser.v2.XMLCDATA;
import oracle.xml.parser.v2.XMLDocument;
import oracle.xml.parser.v2.XMLElement;

import org.apache.log4j.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class Explorer implements Element {
        private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.elements.Explorer");
    private Menu p_menu = null;
    private Menu p_mouseMenu = null;
    private Group p_group = null;
    private Parameters p_param = null;
    private Tree p_tree = null;
    private Preview p_previewRight = null;
    private Preview p_previewDown = null;
    private ObjectCardReport p_cardReport = null;
    private boolean p_showPreview = false;
    private boolean p_showPreviewRight = false;
    private String p_objectName = null;
    private String p_boql = null;
    private String p_formName = null;
    private String p_pageName = null;
    private String p_xml = null;
    private boolean p_onlyparameters = false;
    //-----------    
    public static final byte ORDER_ASC = 0;
    public static final byte ORDER_DESC = 1;
    private ColumnsProvider columnsProvider;
//    private Hashtable p_attributes;
    private GroupProvider groupProvider;
//    public boolean[] p_groups_optionOPEN;
//    public int[] p_groups_optionMAXLINES;
//    public boolean[] p_groups_optionSHOWALLVALUES;
//    public byte[] p_groups_order;
    public String[] p_orders = new String[3];
    public String[] p_ordersDirection = new String[3];
    public ngtXMLHandler p_treeDef = null;
    public String p_focusGroup = null;
    public boDefHandler p_bodef = null;
    public String p_key = null;
    private Hashtable p_runtimeGroupsStatus = new Hashtable(); // tem a Key do grupo e uma String com OPEN-PAGE
    public String p_originalBOQL = null;
    public String p_resultBOQL = null;
    public String p_textFullSearch = null;
    public String p_filterName = null;
    public String p_textUserQuery = null;
	public String p_lasttextUserQuery = null;
    public StringBuffer p_extraColumns = null;
    public long p_bouiUserQuery = -1;
    public long p_svExplorer = -1;
    public String p_svExplorerName = null;
    public int p_htmlLinesPerPage = 50;
    public int p_htmlCurrentPage = 1;
    public boolean p_haveErrors = false;
	public boolean p_showUserParameters = true;
    public ArrayList p_userParameters=new ArrayList();
    public ArrayList p_userNullIgnore=new ArrayList();
    public Vector p_parameters = new Vector();
    public Hashtable p_maxCols = null; // contem o número máximo de colunas para cada bridge
    public long p_bouiPreview = -1;
    public boolean p_hintNSecurity = false;
    public String[] ctrlLines=null;
    public Hashtable groupCnt=null;
    public int ctrlLine =0;
    private long p_user = -1;
    private boolean p_hasBoqlInitJSP = false;
    private String  p_boqlInitJsp = null;
    private String  p_boqlInitJSPClassificationBoql = null;
    private String  p_boqlInitJSPDocBouisSql = null;
    
    private ExplorerOptions p_explorerOptions = null;
    
    private String checkOnClickEvent = null;
    private String checkValues = null;

    /**
     *
     * @Company Enlace3
     * @since
     */
    public static Explorer getExplorer(String objectName, docHTML doc, docHTML_controler docList, String pageName) { 
        long i = System.currentTimeMillis();
        String explorerName = getDefTreeName(objectName, null);
        if(hasExplorer(explorerName, doc.getEboContext().getBoSession().getPerformerBoui(),""))
        {
            return getExplorer(explorerName, doc.getEboContext().getBoSession().getPerformerBoui(),"");
        }
        else
        {
            boDefHandler xbodef = boDefHandler.getBoDefinition( objectName);
            ngtXMLHandler viewer = createExplorerViewer(xbodef, null);
            return getExplorer(xbodef, viewer, doc, docList, pageName,"");
        }
    }

    public static Explorer getExplorer(String objectName, String boql, docHTML doc, docHTML_controler docList, String pageName) {
        String explorerName = getDefTreeName(objectName, boql);
        if(hasExplorer(explorerName, doc.getEboContext().getBoSession().getPerformerBoui(),""))
        {
            return getExplorer(explorerName, doc.getEboContext().getBoSession().getPerformerBoui(),""+boql.hashCode());
        }
        else
        {
            boDefHandler xbodef = boDefHandler.getBoDefinition( objectName);
            ngtXMLHandler viewer = createExplorerViewer(xbodef, boql);
            return getExplorer(xbodef, viewer, doc, docList, pageName,""+boql.hashCode());
        }
    }

    public static Explorer getExplorerWithForm(String objectName, String formName, docHTML doc, docHTML_controler docList,String pageName,String boql) 
    {
        boDefHandler xbodef = boDefHandler.getBoDefinition( objectName);
        String form = "Viewers.General.forms." + formName + ".explorer";
        String subkey=formName;
        ngtXMLHandler nv = xbodef.getPath(form);
        if( boql!=null && boql.length()>1)
        {
            
            XMLDocument ndoc= new XMLDocument();
            ndoc.appendChild( ndoc.importNode(nv.getNode(),true) );
            nv = new ngtXMLHandler( ndoc.getDocumentElement() );
            if(nv.getChildNode("boql") == null)
            {
                Node x = ndoc.createElement("boql");
                ndoc.getDocumentElement().appendChild(x);
                x.appendChild(ndoc.createCDATASection(boql));
            }
            else
            {
                Node x = nv.getChildNode("boql").getNode();
                Node x1=x.getFirstChild();
                if(x1 == null)
                {
                    x.appendChild(ndoc.createCDATASection(boql));
                }
                else
                {
                    x1.setNodeValue( boql );
                }
            }
            
            //Node x2 = viewer.getAttributes().setAttribute("class", "objectForm");
            Attr x4[] =nv.getAttributes();
            for (int i = 0; i < x4.length ; i++) 
            {
                String xxx =  x4[i].getName();
                if(xxx.equalsIgnoreCase("name") )
                {
                
                    String xxkey =formName+"--"+Math.abs(boql.hashCode())+""+doc.getEboContext().getBoSession().getPerformerBoui();
                    x4[i].setValue( xxkey );
                    subkey=xxkey;
                }
            }
            
            
        }
        return getExplorer(xbodef, nv , doc, docList, pageName , subkey);  
    }   
    
    
    public static Explorer getExplorerWithForm(String objectName, String formName, docHTML doc, docHTML_controler docList,String pageName,String boql, String key) 
    {
        boDefHandler xbodef = boDefHandler.getBoDefinition( objectName);
        String form = "Viewers.General.forms." + formName + ".explorer";
        String subkey=formName;
        ngtXMLHandler nv = xbodef.getPath(form);
        if( boql!=null && boql.length()>1)
        {
            
            XMLDocument ndoc= new XMLDocument();
            ndoc.appendChild( ndoc.importNode(nv.getNode(),true) );
            nv = new ngtXMLHandler( ndoc.getDocumentElement() );
            if(nv.getChildNode("boql") == null)
            {
                Node x = ndoc.createElement("boql");
                ndoc.getDocumentElement().appendChild(x);
                x.appendChild(ndoc.createCDATASection(boql));
            }
            else
            {
                Node x = nv.getChildNode("boql").getNode();
                Node x1=x.getFirstChild();
                if(x1 == null)
                {
                    x.appendChild(ndoc.createCDATASection(boql));
                }
                else
                {
                    x1.setNodeValue( boql );
                }
            }
            
            //Node x2 = viewer.getAttributes().setAttribute("class", "objectForm");
            Attr x4[] =nv.getAttributes();
            for (int i = 0; i < x4.length ; i++) 
            {
                String xxx =  x4[i].getName();
                if(xxx.equalsIgnoreCase("name") )
                {
                
                    String xxkey =formName+"--"+Math.abs(boql.hashCode())+""+doc.getEboContext().getBoSession().getPerformerBoui();
                    x4[i].setValue( xxkey );
                    subkey=xxkey;
                }
            }
            
            
        }
        return getExplorerWKey(xbodef, nv , doc, docList, pageName , key);  
    }
   

    public Menu getMenu() {
        return this.p_menu;
    }
    public Menu getMouseMenu() {
        return this.p_mouseMenu;
    }
    public Group getGroup() {
        return this.p_group;
    }

    public Parameters getParameters() {
        return this.p_param;
    }

    public ObjectCardReport getObjectCardReport()
    {
        return this.p_cardReport;
    }
    public void setObjectCardReport(ObjectCardReport cardReport)
    {
        this.p_cardReport = cardReport;
    }
    
    public Preview getPreviewRight() {
        return this.p_previewRight;
    }
    public Preview getPreviewDown() {
        return this.p_previewDown;
    }

    public Tree getTree() {
        return this.p_tree;
    }

    public void setShowPreview(boolean value) {
        this.p_showPreview = value;
    }

    public void setPreviewRight() {
        this.p_showPreview = true;
        this.p_showPreviewRight = true;
    }

    public void setPreviewDown() {
        this.p_showPreview = true;
        this.p_showPreviewRight = false;
    }

    public boolean isPreviewOn() {
        return this.p_showPreview;
    }

    public boolean isPreviewOnRight() {
        return this.p_showPreview && p_showPreviewRight;
    }

    public boolean isPreviewOnLeft() {
        return this.p_showPreview && !p_showPreviewRight;
    }

    public void writeHTML(PrintWriter out,docHTML docHTML, docHTML_controler docList, PageController control)
        throws IOException, boRuntimeException {
        HTMLBuilder.writeExplorer(out, this, docHTML, docList, control);
    }

    public String getHTML(docHTML docHTML, docHTML_controler docList,PageController control)
        throws IOException, boRuntimeException {
        return HTMLBuilder.getExplorerHTML(this, docHTML, docList, control);
    }

    //---------------------------------------------------------------
    private static Explorer getExplorerWKey(boDefHandler bodef , ngtXMLHandler defTree, docHTML doc, docHTML_controler docList, String pageName,String key)
    {
        return ExplorerServer.getExplorerWKey(bodef, defTree, doc, docList, pageName,key);
    }
    
    private static Explorer getExplorer(boDefHandler bodef , ngtXMLHandler defTree, docHTML doc, docHTML_controler docList, String pageName,String subkey)
    {
        return ExplorerServer.getExplorer(bodef, defTree, doc, docList, pageName,subkey);
    }
    
    private static Explorer getExplorer(String defTreeName , long performerBoui, String subkey)
    {
        return ExplorerServer.getExplorer(defTreeName+subkey+"-"+ performerBoui);
    }
    
    private static boolean hasExplorer(String defTreeName , long performerBoui, String subkey)
    {
        return ExplorerServer.hasExplorer(defTreeName, performerBoui,subkey);
    }
    
    public String getKey()
    {
        return p_key;
    }
    
    public long getUser()
    {
        return p_user;
    }
    
    public Explorer(String Key, docHTML doc, docHTML_controler docList,  boDefHandler bodef,
        ngtXMLHandler treeDef, ngtXMLHandler treeUserdef,String pageName) 
    {
        this.p_explorerOptions = new ExplorerOptions(treeDef);
        p_user = doc.getEboContext().getBoSession().getPerformerBoui();
        p_bodef = bodef;
        p_key = Key;

        p_textFullSearch = "";
        p_filterName = "";
        p_svExplorerName = "";
        
        columnsProvider = new ColumnsProvider();
//        p_attributes = new Hashtable();

        p_treeDef = treeDef;

		
        if(treeDef.getChildNode("boql") != null)
        {
            String boql = treeDef.getChildNode("boql").getText();
            if(boql != null && !"".equals(boql))
            {
                QLParser ql = new QLParser();
                String sql = ql.getFromAndWhereClause(boql, doc.getEboContext());
                
                if(boql.indexOf("NO_SECURITY") > -1)
                {
                    p_hintNSecurity = true;
                }
        
                p_originalBOQL = boql.substring(boql.toUpperCase().indexOf("WHERE") +
                        5);
            }
        }
        p_formName = treeDef.getAttribute("name");

//boqlInitJsp JSP que inicia o boql do explorador
        if(treeDef.getChildNode("boqlInitJsp") != null)
        {
            p_boqlInitJsp = treeDef.getChildNode("boqlInitJsp").getText();
            p_hasBoqlInitJSP = true;
        }
        



        columnsProvider.readAttributes(treeDef, p_bodef, doc.getEboContext());

        groupProvider = new GroupProvider();
        groupProvider.readGroups(treeUserdef, doc.getEboContext(), columnsProvider);
        
        columnsProvider.readCols(treeUserdef, p_bodef, doc.getEboContext());

        clearOrders();
        columnsProvider.readOrders(treeUserdef, p_bodef, doc.getEboContext(), p_orders, p_ordersDirection);
        
        p_group = new Group(this);
        this.p_pageName = pageName;
        p_param = new Parameters(this, pageName);
        p_tree = new Tree(this);
        p_previewRight = new Preview(null, "showRight", p_key+"_previewRight");
        p_previewRight.setPreviewID("showRight");
        p_previewDown = new Preview(null, "showDown", p_key+"_previewDown");
        p_previewDown.setPreviewID("showDown");
        if(this.p_explorerOptions.isPreviousZoneVisible())
        {   
            if("right".equalsIgnoreCase(this.p_explorerOptions.getPreviousZone()))
            {
                setPreviewRight();
                p_previewRight.setDisplay(true);
            }
            if("bottom".equalsIgnoreCase(this.p_explorerOptions.getPreviousZone()))
            {
                setPreviewDown();
                p_previewDown.setDisplay(true);
            }            
        }
        netgest.bo.presentation.render.elements.Filter filter = 
        new netgest.bo.presentation.render.elements.Filter(this);
        
        netgest.bo.presentation.render.elements.SavedExplorers svExp = 
        new netgest.bo.presentation.render.elements.SavedExplorers(this);
        
        try
        {
            p_menu = Menu.getExplorerMenu(doc.getEboContext(), p_key, p_pageName, bodef, String.valueOf(doc.getDocIdx()), filter,svExp,this.p_explorerOptions, p_boqlInitJsp);
            if(p_menu != null)
            {
                p_menu.setOnClickParameters("idx", String.valueOf(doc.getDocIdx()));
                p_menu.setOnClickParameters("treeName", p_key);     
            }
        }
        catch (boRuntimeException e)
        {
            logger.error(e);
        }
        try
        {
            p_mouseMenu = Menu.getMouseMenu(p_key, bodef, String.valueOf(doc.getDocIdx()), filter);
            p_mouseMenu.setOnClickParameters("idx", String.valueOf(doc.getDocIdx()));
            p_mouseMenu.setOnClickParameters("treeName", p_key);
        }
        catch (boRuntimeException e)
        {
            
        }
		
		ngtXMLHandler aux = treeUserdef.getChildNode("fulltext");
		if(aux != null)
			this.setExplorerFullText(aux.getText());
			
		aux = treeUserdef.getChildNode("filter");
		if(aux != null)
			this.setTextUserQuery(null,aux.getText());
			
        //inicializações
        if(this.p_explorerOptions.isGroupsZoneVisible() && groupProvider.groupSize() > 0)
        {
            p_group.setDisplay(true);
        }
        else
        {
            p_group.setDisplay(false);
        }
        try
        {
            if(this.p_explorerOptions.isParametersZoneVisible() && haveParameters(doc.getEboContext()))
            {
                p_param.setDisplay(true);
            }
            else
            {
                p_param.setDisplay(false);
            }
        }
        catch (boRuntimeException e)
        {
            
        }
    }

	public void setBouiUserQuery( EboContext ctx , long bouiUserquery ) throws boRuntimeException
    {
        if ( p_bouiUserQuery != bouiUserquery )
        {
            p_userParameters = new ArrayList();
            p_userNullIgnore = new ArrayList();
            p_bouiUserQuery = bouiUserquery;
            userquery.readParameters( ctx , bouiUserquery,p_userParameters, p_userNullIgnore); 
        }
        
    }
    
    public void setBouiUserQueryAndParams( long bouiUserquery, ArrayList userParams, ArrayList userNull) throws boRuntimeException
    {
        p_userParameters = userParams;
        p_userNullIgnore = userNull;
        p_bouiUserQuery = bouiUserquery; 
    }
    
    public void setBouiSvExplorer( EboContext ctx , long svExplorer ) throws boRuntimeException
    {
        if (svExplorer > 0 && p_svExplorer != svExplorer )
        {
            p_svExplorer = svExplorer; 
            
            boObject treeDef = boObject.getBoManager().loadObject(ctx,svExplorer);
            boDefHandler xbodef = boDefHandler.getBoDefinition( treeDef.getAttribute("objectType").getValueString());
            p_textUserQuery = null;
            if(treeDef.getAttribute("userQuery").getValueString() != null && treeDef.getAttribute("userQuery").getValueString().length() > 0)
            {
                p_textUserQuery = treeDef.getAttribute("userQuery").getValueString().replaceAll("&gt;",">").replaceAll("&lt;","<");
            }
            p_filterName = null;
            if(treeDef.getAttribute("filter").getValueString() != null && treeDef.getAttribute("filter").getValueString().length() > 0)
            {
                p_filterName = treeDef.getAttribute("filter").getValueString();
            }
            
            ColumnProvider[] atts = getAttributes();
            Vector colsVec = new Vector();
            bridgeHandler treeCols = treeDef.getBridge("cols");
            treeCols.beforeFirst();
            while(treeCols.next())
            {
                boObject attDef = treeCols.getObject();
                String name = attDef.getAttribute("name").getValueString();
                long width = attDef.getAttribute("width").getValueLong();
                String order = attDef.getAttribute("order").getValueString();
                long orderPos = attDef.getAttribute("orderPos").getValueLong();
          
                clearOrders();
                for (int i = 0; i < atts.length; i++)
                {
                    if(atts[i].getName().equals(name))
                    {
                        atts[i].setWidth((int)width);
                        colsVec.add(atts[i]);
                        if(orderPos>0)
                        {
                            p_orders[(int)orderPos-1] = name;
                            p_ordersDirection[(int)orderPos-1] = order;
                        }
                        break;
                    }
                }
            }
            getColumnsProvider().setColumns((ColumnProvider[])colsVec.toArray(new ColumnProvider[colsVec.size()]));

            Vector goupsVec = new Vector();
            String orders = "";
            bridgeHandler treeGroups = treeDef.getBridge("groups");
            treeGroups.beforeFirst();
            while(treeGroups.next())
            {
              boObject attDef = treeGroups.getObject();
              String name = attDef.getAttribute("name").getValueString();
              String order = attDef.getAttribute("order").getValueString();
              orders += order + "##";
                
              for (int i = 0; i < atts.length; i++)
                if(atts[i].getName().equals(name))
                {
                  goupsVec.add(atts[i]);
                  break;
                }
            }
            getGroupProvider().setGroups((ColumnProvider[])goupsVec.toArray(new ColumnProvider[goupsVec.size()]));
            clearOpenGroups();
            String ordersArr[] = orders.split("##");
            getGroupProvider().setGroupOrder(new byte[ordersArr.length]);
            for (int i = 0; i < ordersArr.length; i++) 
            {
              if(ordersArr[i].equals("ASC"))
                getGroupProvider().setGroupOrder(i, Explorer.ORDER_ASC);
              else
                getGroupProvider().setGroupOrder(i, Explorer.ORDER_DESC);
            } 
            //inicializações
            if(this.p_explorerOptions.isGroupsZoneVisible() && groupProvider.groupSize() > 0)
            {
                p_group.setDisplay(true);
            }
            else
            {
                p_group.setDisplay(false);
            }
            try
            {
                if(this.p_explorerOptions.isParametersZoneVisible() && haveParameters(ctx))
                {
                    p_param.setDisplay(true);
                }
                else
                {
                    p_param.setDisplay(false);
                }
            }
            catch (boRuntimeException e)
            {
                
            }
            
        }
        else if(p_svExplorer != svExplorer)
        {
            p_svExplorer = svExplorer;
            backExplorer(ctx);
        }
    }
    
    public void setTextUserQuery( EboContext ctx , String textUserquery )
    {
        if ( p_textUserQuery!= textUserquery )
        {
            p_textUserQuery = textUserquery;
            p_menu.setTextUserQuery(p_textUserQuery);
        }
        
    }
    public boolean haveParameters( EboContext ctx ) throws boRuntimeException
    {
        if ( p_userParameters == null || p_userParameters.size() == 0 ) return false;
        return true;
    }
    
    public boolean haveBlankParameters( EboContext ctx ) throws boRuntimeException
    {
        boolean haveParams=false;
        if ( p_userParameters == null || p_userParameters.size() == 0 ) return false;
        for (int i = 0; i <  p_userParameters.size() ; i++) 
        {
            if ( p_userParameters.get(i) == null && "0".equals(p_userNullIgnore.get(i)))
            {
                return true;
            }
        }
        return false;
        
    }    

    public void setParametersQuery( String parametersQuery )
    {
        String paramQuery = parametersQuery;
        try{
            paramQuery = URLDecoder.decode( parametersQuery, boConfig.getEncoding() );
        }catch(Exception e){/*ignore*/}
        
        ngtXMLHandler xml= new ngtXMLHandler( paramQuery );
        ngtXMLHandler[] xmlParameters=xml.getFirstChild().getChildNodes();
        p_userParameters = new ArrayList();
        p_userNullIgnore = new ArrayList();
        for (int i = 0; i < xmlParameters.length ; i++) 
        {            
            if(xmlParameters[i].getChildNode("value") != null)
            {
                String value = xmlParameters[i].getChildNode("value").getText();
                if( value!=null && value.length() == 0 ) p_userParameters.add(null);
                else p_userParameters.add( value );
                p_userNullIgnore.add(xmlParameters[i].getChildNode("nullIgnore").getText());
                
            }
        }
        
        
    }

    public ngtXMLHandler buildUserXML() {
        XMLDocument xBody;
        org.w3c.dom.Element xnode;
        xBody = new XMLDocument();

        xnode = xBody.createElement("explorer");
        xBody.appendChild(xnode);

        org.w3c.dom.Element xCols = xBody.createElement("cols");
        xnode.appendChild(xCols);

        org.w3c.dom.Element  xGroups = xBody.createElement("groups");
        xnode.appendChild(xGroups);

        org.w3c.dom.Element  xOrders = xBody.createElement("order");
        xnode.appendChild(xOrders);

        for (int i = 0; i < columnsProvider.columnsSize(); i++) {
            org.w3c.dom.Element  xcol = xBody.createElement("col");
            org.w3c.dom.Element  xatr = xBody.createElement("attribute");
            xatr.appendChild(xBody.createTextNode(columnsProvider.getColumn(i).getName()));
            xcol.appendChild(xatr);
            xCols.appendChild(xcol);
        }

        for (int i = 0; i < groupProvider.groupSize(); i++) {
            org.w3c.dom.Element  xcol = xBody.createElement("group");
            org.w3c.dom.Element  xatr = xBody.createElement("attribute");
            xatr.appendChild(xBody.createTextNode(groupProvider.getGroup(i).getName()));
            xcol.appendChild(xatr);
            xatr.setAttribute("order",
                (groupProvider.getGroupOrder(i) == this.ORDER_ASC) ? "asc" : "desc");
            xGroups.appendChild(xcol);
        }

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) {
                org.w3c.dom.Element  xatr = xBody.createElement("attribute");
                xatr.setAttribute("direction", p_ordersDirection[i]);
                xatr.appendChild(xBody.createTextNode(p_orders[i]));
                xOrders.appendChild(xatr);
            }
        }
		
		if(p_textFullSearch != null && p_textFullSearch.length()>0)
		{
	        org.w3c.dom.Element aux = xBody.createElement("fulltext");
			aux.appendChild(xBody.createTextNode(p_textFullSearch));
		    xnode.appendChild(aux);
		}
    	
		if(p_textUserQuery != null && p_textUserQuery.length()>0)
		{
	        org.w3c.dom.Element aux = xBody.createElement("filter");
			aux.appendChild(xBody.createTextNode(p_textUserQuery));
		    xnode.appendChild(aux);
		}

        return new ngtXMLHandler(xBody);
    }

//    public String getOrderByString(EboContext ctx) {
//        String toRet = null;
//        boolean hasBoui = false;
//
//        for (int i = 0; i < p_orders.length; i++) {
//            if (p_orders[i] != null) {
//                for (int z = 0; z < columnsProvider.columnsSize(); z++) {
//                    if (columnsProvider.getColumn(z).getName().equalsIgnoreCase(p_orders[i]))  
//                    {
//                        if (toRet == null) {
//                            if (columnsProvider.getColumn(z).isAttribute()) 
//                            {
//                                if( columnsProvider.getColumn(z).getDefAttribute().getLOVName() != null &&
//                                    columnsProvider.getColumn(z).getDefAttribute().getLOVName().trim().length() > 0
//                                )
//                                {
//                                    toRet = "order by " + getLovSql(ctx, columnsProvider.getColumn(z), true, false) + " " + p_ordersDirection[i];
//                                }
//                                else
//                                {
//                                    toRet = "order by " + composeOrderByCardId( columnsProvider.getColumn(z).getName(), p_ordersDirection[i] );
//                                }
//                            }
//                            else 
//                            {
//                                toRet = "order by [\"" + columnsProvider.getColumn(z).getName() + "\" " +
//                                    p_ordersDirection[i] + "]";
//                            }
//                        } else {
//                            if (columnsProvider.getColumn(z).isAttribute()) 
//                            {
//                                if( columnsProvider.getColumn(z).getDefAttribute().getLOVName() != null &&
//                                    columnsProvider.getColumn(z).getDefAttribute().getLOVName().trim().length() > 0
//                                )
//                                {
//                                    toRet = ", " + getLovSql(ctx, columnsProvider.getColumn(z), false, true);
//                                }
//                                else
//                                {
//                                    toRet += ", " + composeOrderByCardId( columnsProvider.getColumn(z).getName(), p_ordersDirection[i] );
//                                }
////                                toRet += ("," + columnsProvider.getColumn(z).getName() + " " +
////                                p_ordersDirection[i]);
//                            }
//                            else 
//                            {
//                                toRet += (",[\"" + columnsProvider.getColumn(z).getName() + "\" " +
//                                p_ordersDirection[i] + "]" );
//                            }
//                        }
//                    }
//                }
//            }
//        }
//
//        if (toRet == null) {
//            toRet = "order by BOUI asc";
//        }
//        else if(toRet.toUpperCase().indexOf("BOUI") == -1)
//        {
//            toRet += ", BOUI asc";
//        }
//        return toRet;
//    }
    
    public String getOrderByString(EboContext ctx) 
    {
        String values[] = getOrderByStringArray(ctx);
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < values.length; i++) 
        {
            sb.append(values[i]);
        }
        return sb.toString();
    }
    
    public String[] getOrderByStringArray(EboContext ctx) {
        ArrayList toRet = null;
        boolean hasBoui = false;

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) {
                for (int z = 0; z < columnsProvider.columnsSize(); z++) {
                    if (columnsProvider.getColumn(z)!=null && columnsProvider.getColumn(z).getName().equalsIgnoreCase(p_orders[i]))  
                    {
                        if (toRet == null) {
                            if (columnsProvider.getColumn(z).isAttribute()) 
                            {
                                if( columnsProvider.getColumn(z).getDefAttribute().getLOVName() != null &&
                                    columnsProvider.getColumn(z).getDefAttribute().getLOVName().trim().length() > 0
                                )
                                {
                                    toRet = new ArrayList();
                                    toRet.add("order by " + getLovSql(ctx, columnsProvider.getColumn(z), true, false) + " " + p_ordersDirection[i]);
                                }
                                else
                                {
                                    toRet = new ArrayList();
                                    toRet.add("order by " + composeOrderByCardId( columnsProvider.getColumn(z).getName(), p_ordersDirection[i] ));
                                }
                            }
                            else 
                            {
                                toRet = new ArrayList();
                                toRet.add("order by [\"" + columnsProvider.getColumn(z).getName() + "\" " +
                                    p_ordersDirection[i] + "]");
                            }
                        } else {
                            if (columnsProvider.getColumn(z).isAttribute()) 
                            {
                                if( columnsProvider.getColumn(z).getDefAttribute().getLOVName() != null &&
                                    columnsProvider.getColumn(z).getDefAttribute().getLOVName().trim().length() > 0
                                )
                                {
                                    toRet.add(getLovSql(ctx, columnsProvider.getColumn(z), true, true) + " " + p_ordersDirection[i]);
                                }
                                else
                                {
                                    toRet.add(", " + composeOrderByCardId( columnsProvider.getColumn(z).getName(), p_ordersDirection[i] ));
                                }
//                                toRet += ("," + columnsProvider.getColumn(z).getName() + " " +
//                                p_ordersDirection[i]);
                            }
                            else 
                            {
                                toRet.add((",[\"" + columnsProvider.getColumn(z).getName() + "\" " +
                                p_ordersDirection[i] + "]" ));
                            }
                        }
                    }
                }
            }
        }

        if (toRet == null) {
            toRet = new ArrayList();
            toRet.add("order by BOUI asc");
        }
        else
        {
            boolean found = false;
            for (int i = 0; i < toRet.size() && !found; i++) 
            {
                if(((String)toRet.get(i)).toUpperCase().indexOf("BOUI") != -1 
                    && ((String)toRet.get(i)).toUpperCase().indexOf("SELECT") == -1 
                )
                {
                    found = true;
                }
            }
            if(!found)
            {
                toRet.add(", BOUI asc");
            }
        }
        return (String[])toRet.toArray(new String[toRet.size()]);
    }
    
    public String composeOrderByCardId( String attName, String dir )
    {
        String toRet = null; 
        boDefAttribute att = this.p_bodef.getAttributeRef( attName );
        if( att != null && att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
        {
            toRet = "";
            boDefHandler def = att.getReferencedObjectDef();
            
            String cardId = def.getCARDID();
            Pattern patt = Pattern.compile( "\\[(([A-Za-z0-9_])*[^\\]*])\\]" );
            Matcher matcher = patt.matcher( cardId );
            while( matcher.find() ) 
            {
                String cardAtt = matcher.group(1);
                boDefAttribute defAtt = def.getAttributeRef( cardAtt );
                if( defAtt != null )
                {
                    if( toRet.length() > 0 ) toRet +=", ";
                    toRet += attName + "." + cardAtt + " " + dir;   
                }
                else
                {
                    toRet = null;
                    break;
                }
            }
            //toRet += " " + dir;
        }
        
        if( toRet == null || "".equals(toRet))
        {
            toRet = attName + " " + dir;
        }
        return toRet;
    }
    
    public String[] getCardIdFields( String attName )
    {
        Vector toRet = new Vector(); 
        boDefAttribute att = this.p_bodef.getAttributeRef( attName );
        if( att != null && att.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
        {
            boDefHandler def = att.getReferencedObjectDef();
            
            String cardId = def.getCARDID();
            Pattern patt = Pattern.compile( "\\[(([A-Za-z0-9_])*[^\\]*])\\]" );
            Matcher matcher = patt.matcher( cardId );
            while( matcher.find() ) 
            {
                String cardAtt = matcher.group(1);
                boDefAttribute defAtt = def.getAttributeRef( cardAtt );
                if( defAtt != null )
                {
                    toRet.add( def.getBoMasterTable() + ".\"" + defAtt.getDbName() +"\"" );
                }
                else
                {
                    toRet = new Vector(); 
                    break;
                }
            }
        }
        
        if( toRet == null )
        {
            toRet.add( attName );
        }
        return (String[])toRet.toArray( new String[ toRet.size() ] )  ;
    }
    
    public String[] _getOrderByStringFields(EboContext ctx) {
        ArrayList toRet = new ArrayList();

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) 
            {
                for (int z = 0;z < columnsProvider.columnsSize(); z++) {
                    if (columnsProvider.getColumn(z).getName().equalsIgnoreCase(p_orders[i])) 
                    {
                        if (columnsProvider.getColumn(z).isAttribute()) 
                        {
                            if( columnsProvider.getColumn(z).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                            {
                                String[] cardIdFields = getCardIdFields( columnsProvider.getColumn(z).getName() );
                                for (int y = 0; y < cardIdFields.length; y++) 
                                {
                                    toRet.add("," + cardIdFields[y]);
                                }
                            } 
                            else if( columnsProvider.getColumn(z).getDefAttribute().getLOVName() != null &&
                                    columnsProvider.getColumn(z).getDefAttribute().getLOVName().trim().length() > 0
                            )
                            {
                                toRet.add(getLovSql(ctx, columnsProvider.getColumn(z), false, true));
                            }
                            else
                            {
                                toRet.add("," + columnsProvider.getColumn(z).getSQL()); 
                            }
                             // toRet = "," + p_bodef.getBoExtendedTable() +"."+ columnsProvider.getColumn(z).getName(); 
                        } 
                        else 
                        {
                            if(columnsProvider.getColumn(z).hasSpecialClauses())
                            {
                                toRet.add("," + ((ClassColumn)columnsProvider.getColumn(z)).getSQLNoParRect() + " " );
                            }
                            else
                            {
                                toRet.add(",[" + columnsProvider.getColumn(z).getSQL() + "] ");
                            }
                        }
                    }
                }
            }
        }
        if(toRet.size() == 0)
        {
            return new String[0];
        }
        String[] aux = new String[toRet.size()];
        return (String[])toRet.toArray(aux);
    }
    
    
    private static String getLovSql(EboContext ctx, ColumnProvider cp, boolean boqlTreat, boolean virgula)
    {
        try
        {
            boObjectList lovList = boObjectList.list(ctx, "select Ebo_LOV where name = ?", new Object[]{cp.getDefAttribute().getLOVName()}, 1, true);
            lovList.beforeFirst();
            if(lovList.next())
            {
                
                if(boqlTreat)
                {
                    if(virgula)
                    {
                        return ", [(select ebo_lovdetails.description from ebo_lovdetails, ebo_lov$details where ebo_lov$details.parent$ = " + lovList.getObject().getBoui() + " and ebo_lov$details.child$ = ebo_lovdetails.boui and ebo_lovdetails.value =to_char("+cp.getSQL()+"))]";
                    }
                    return " [(select ebo_lovdetails.description from ebo_lovdetails, ebo_lov$details where ebo_lov$details.parent$ = " + lovList.getObject().getBoui() + " and ebo_lov$details.child$ = ebo_lovdetails.boui and ebo_lovdetails.value =to_char("+cp.getSQL()+"))]";
                }
                if(virgula)
                {
                    return ", (select ebo_lovdetails.description from ebo_lovdetails, ebo_lov$details where ebo_lov$details.parent$ = " + lovList.getObject().getBoui() + " and ebo_lov$details.child$ = ebo_lovdetails.boui and ebo_lovdetails.value =to_char("+cp.getSQL()+"))";
                }
                return " (select ebo_lovdetails.description from ebo_lovdetails, ebo_lov$details where ebo_lov$details.parent$ = " + lovList.getObject().getBoui() + " and ebo_lov$details.child$ = ebo_lovdetails.boui and ebo_lovdetails.value =to_char("+cp.getSQL()+"))";                
            }
        }
        catch (boRuntimeException e)
        {
            //ignore
        }
        if(virgula)
        {
            return "," + cp.getSQL();
        }
        return cp.getSQL();
    }
    
     public String getOrderByStringFields() {
        String toRet = null;

        for (int i = 0; i < p_orders.length; i++) {
            if (p_orders[i] != null) 
            {
                boolean bfound = false;
                for (int z = 0; z < columnsProvider.columnsSize(); z++) 
                {
                    if( p_orders[i].equalsIgnoreCase( columnsProvider.getColumn(z).getName() ) && ( !columnsProvider.getColumn(z).isAttribute() || columnsProvider.getColumn(z).isExternalAttribute() ) )
                    {
                        bfound = true;
                        break;
                    }
                }
                for (int z = 0;!bfound && z < columnsProvider.columnsSize(); z++) {
                    if (columnsProvider.getColumn(z).getName().equalsIgnoreCase(p_orders[i])) {
                        if (toRet == null) {
                            if (columnsProvider.getColumn(z).isAttribute()) 
                            {
                                if( columnsProvider.getColumn(z).getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
                                {
                                    toRet = "";
                                    String[] cardIdFields = getCardIdFields( columnsProvider.getColumn(z).getName() );
                                    for (int y = 0; y < cardIdFields.length; y++) 
                                    {
                                        toRet += "," + cardIdFields[y];     
                                    }
                                } 
                                else
                                {
                                    toRet = "," + columnsProvider.getColumn(z).getSQL(); 
                                }
                                 // toRet = "," + p_bodef.getBoExtendedTable() +"."+ columnsProvider.getColumn(z).getName(); 
                            } 
                            else 
                            {
                                toRet = ",[" + columnsProvider.getColumn(z).getSQL() + "] " ;
                            }
                        } else {
                            if (columnsProvider.getColumn(z).isAttribute()) 
                            {
                               //toRet += ("," + p_bodef.getBoExtendedTable()+"." + columnsProvider.getColumn(z).getName() + " " );
                               toRet += "," + columnsProvider.getColumn(z).getSQL();
                            } 
                            else 
                            {
                                toRet += (",[" + columnsProvider.getColumn(z).getSQL() + "] ");
                                
                            }
                        }
                    }
                }
            }
        }

        if (toRet == null) {
            toRet = "";
        }

        return toRet;
    }

    public String getImageSort(String xcol) {
        if ((p_orders[0] != null) && xcol.equalsIgnoreCase(p_orders[0])) {
            String img = "templates/grid/std/" +
                p_ordersDirection[0].toLowerCase() + ".gif";

            return "<img style='border:0' border'0' class=direction src='" +
            img + "' hspace=3 height=10 width=10 />";
        }

        return "";
    }

    public String getImageSort(byte order) {
        String img = "templates/grid/std/" +
            ((order == this.ORDER_ASC) ? "asc" : "desc") + ".gif";

        return "<img style='border:0' border'0' class=direction src=\"" + img +
        "\" hspace=3 height=10 width=10 />";
    }

    public void setOrderCol(String xatr) { 
        if ((p_orders[0] != null) && p_orders[0].equalsIgnoreCase(xatr)) {
            if (p_ordersDirection[0].equalsIgnoreCase("asc")) {
                p_ordersDirection[0] = "desc";
            } else {
                p_ordersDirection[0] = "asc";
            }
        } else {
            for (int i = (p_orders.length-1); i > 0; i--) {
                p_orders[i] = p_orders[i - 1];
                p_ordersDirection[i] = p_ordersDirection[i - 1];
            }
            p_orders[0] = xatr;
            p_ordersDirection[0] = "desc";
            removeDuplicateds(p_orders, p_ordersDirection);
        }
    }

    public static void removeDuplicateds(String[] p_orders, String[] p_ordersDirection)
    {
        for (int i = 0; i < p_orders.length ; i++) 
        {
            String ord = p_orders[i];
            if(ord != null)
            {
                for (int j = i+1; j < p_orders.length; j++) 
                {
                    if(ord.equalsIgnoreCase(p_orders[j]))
                    {
                        if((j+1) < p_orders.length)
                        {
                            p_orders[j] = p_orders[j+1];
                            p_ordersDirection[j] = p_ordersDirection[j+1];
                            j--;
                        }
                        else
                        {
                            p_orders[j] = null;
                            p_ordersDirection[j] = null;
                        }
                    }                
                }
            }
        }
        
        
    }

    public ColumnProvider[] getAttributes() {
        return columnsProvider.getAttributes();
    }

    public ColumnProvider[] getCols() {
        return columnsProvider.getColumns();
    }

    public ColumnProvider[] getGroups() {
        return groupProvider.getGroups();
    }

    public String getGroupLabel(int groupnumber) {
        return groupProvider.getGroup(groupnumber).getLabel();
    }

    public String getGroupValue(EboContext ctx, ResultSet rslt, int groupnumber)
        throws SQLException, boRuntimeException {
        byte x = groupProvider.getGroup(groupnumber - 1).getType();
        String toRet = "";

        if(groupProvider.getGroup(groupnumber - 1).getDefAttribute() != null)
        {
            toRet = getStringValue(ctx, rslt, groupnumber, groupProvider.getGroup(groupnumber - 1).getDefAttribute());
        }
        else
        {
            toRet = getStringValue(ctx, rslt, groupnumber, groupProvider.getGroup(groupnumber - 1).getType());
        }

        if (rslt.wasNull()) {
            toRet = "";
        }

        return toRet;
    }


    public String[] getMethods()  {
       String methods[] =null;
       if (this.p_treeDef.getChildNode("methods")!=null)
       {
           ngtXMLHandler[] attributes = this.p_treeDef.getChildNode("methods").getChildNodes();
           if (attributes!=null)
           {
                methods=new String[attributes.length];
                for (int i = 0; i < attributes.length; i++) 
                {
                    if( attributes[i].getText() != null)
                    {
                        methods[i]=attributes[i].getText();
                    }
                }
           }
       }
       return methods;
    }
    
    public String getMethodOverrideJSP(String method) {
        String overrideJSP =null;
        if (this.p_treeDef.getChildNode("methods")!=null)
        {
            ngtXMLHandler[] attributes = this.p_treeDef.getChildNode("methods").getChildNodes();
            if (attributes!=null)
            {                
                 for (int i = 0; i < attributes.length; i++) 
                 {
                     if( attributes[i].getText().equals(method))
                     {
                         overrideJSP=attributes[i].getAttribute("overrideDefaultJSP");
                         break;
                     }
                 }
            }
        }
        return overrideJSP; 
    }
    
    public Hashtable getAllAttributes() {
        return columnsProvider.getAllAttributes();
    }

    public String getExtAtrHTML(ResultSet rslt, String atrname, docHTML DOC)
        throws SQLException, boRuntimeException {
        if (atrname.indexOf('$') > -1) {
            atrname = atrname.replaceAll("\\$", "\\.");
        }

        ColumnProvider atrext = (ColumnProvider) columnsProvider.getAttribute(atrname);

        if (atrname.indexOf('.') > -1) {
            atrname = atrname.replaceAll("\\.", "\\$");
        }

        if (atrext == null) {
            return "&nbsp;";
        }

        byte x = atrext.getType();

        String toRet = "";

        if (x == boDefAttribute.VALUE_BOOLEAN) {
            if (rslt.getBoolean(atrname)) {
                toRet = "1";
            } else {
                toRet = "0";
            }
        } else if (x == boDefAttribute.VALUE_CHAR) {
            toRet = rslt.getString(atrname);

            if (toRet == null) {
                toRet = "";
            }
        } else if ((x == boDefAttribute.VALUE_NUMBER) ||
                (x == boDefAttribute.VALUE_DURATION)) {
                if(rslt.getObject(atrname) != null)
                {
                    toRet = new Long(rslt.getLong(atrname)).toString();
                }
            //toRet=toRet.trim();
        } else if (x == boDefAttribute.VALUE_DATETIME) {
            Date xx = rslt.getDate(atrname);

            if (xx != null) {
                toRet = xx.toString();
            }
        } else if (x == boDefAttribute.VALUE_DATE) {
            Date xx = rslt.getDate(atrname);
            
            if (xx != null) {
                toRet = xx.toString();
            }

            //java.util.Date xdate=new  java.util.Date();
        }

        String v = atrext.getValueResult(rslt.getLong(1), toRet, DOC.getEboContext(), p_explorerOptions.showLinks());
        v = "<font color='" + atrext.getColorResult(toRet) + "' >" + v +
            "</font>";

        return v;
    }

    public void setCols(EboContext boctx, String[] cols) {
        try
        {
            columnsProvider.setCols(cols);
            ExplorerServer.saveUserTree(boctx.getBoSession().getPerformerBoui(),this);
            if(p_svExplorer > 0)
            {
                setBouiSvExplorer( boctx , -1 );
                p_svExplorerName="";
            }
        }
        catch (boRuntimeException e)
        {
            logger.error("",e);
        }
    }

    public boolean groupIsOpen(String key) {
        boolean toRet = false;
        Integer isOpen = (Integer) p_runtimeGroupsStatus.get(key);

        if (isOpen == null) {
            toRet = false; // p_groups_optionOPEN[ groupNumber ];
        } else {
            toRet = true; 
//            toRet = isOpen.booleanValue();
        }
        return toRet;
    }
    
    public int getGroupPageOpen(String key) {
        int toRet = -1;
        Integer isOpen = (Integer) p_runtimeGroupsStatus.get(key);

        if (isOpen != null) {
            toRet = isOpen.intValue();
        }
        return toRet;
    }

    public void openGroup(String key, int page) {
        //( Boolean) p_runtimeGroupsStatus.get(key);
        //p_runtimeGroupsStatus.remove( key );
        //String xkey = key.split("-->")[0];
        p_runtimeGroupsStatus.put(key, new Integer(page));
        p_focusGroup = key;
    }

    public void closeGroup(String key) {
        //p_runtimeGroupsStatus.put( key , new Boolean( false ) );
        p_runtimeGroupsStatus.remove(key);
        p_focusGroup = key;
    }
    
    public void clearOpenGroups()
    {
        p_runtimeGroupsStatus.clear();
    }

    public String groupSQL(EboContext ctx, int groupNumber, String value) {
        return groupSQL(ctx, groupNumber, value, false);
    }
    public String groupSQL(EboContext ctx, int groupNumber, String value, boolean setValue) {
        String toRet = "";

        boolean lov = false;
        if (groupProvider.getGroup(groupNumber).isAttribute()) {
            toRet = groupProvider.getGroup(groupNumber).getName();
        } else {
            toRet = groupProvider.getGroup(groupNumber).getSQL();
        }

        byte x = groupProvider.getGroup(groupNumber).getType();

        if(groupProvider.getGroup(groupNumber).getDefAttribute() != null &&  
          groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName() != null &&
           groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName().trim().length() > 0
          )
        {
            if ((value == null) || value.equals("")) {
                //toRet= toRet+" is null or "+toRet+" = '"+value+"'";
                toRet = toRet + " is null";
                lov = true;
            }
            else {
            
                try
                {
                    lovObject lObj = LovManager.getLovObject(ctx, groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName());
                    if(lObj != null)
                    {
                        lObj.beforeFirst();
                        String desc = null;
                        while(lObj.next() && !lov)
                        {
                            desc = lObj.getDescription();
                            if(value.equalsIgnoreCase(desc))
                            {
                                if(setValue)
                                {
                                    toRet += " = '"+lObj.getCode()+"'";
                                }
                                else
                                {
                                    toRet += " = ?";
                                    p_parameters.add(lObj.getCode());
                                }
                                lov = true;
                            }
                        }
                    }
                }
                catch (boRuntimeException e)
                {
                    /*IGNORE*/
                }
            }
        }
        if(!lov)
        {
            if (x == boDefAttribute.VALUE_BOOLEAN) {
                if ((value == null) || value.equals("")) {
                    //toRet= toRet+" is null or "+toRet+" = '"+value+"'";
                    toRet = toRet + " is null";
                }
                else {
                    if ("1".equals(value) || "Sim".equals(value)) {
                        toRet += " = '1'";
                    } else {
                        toRet += " = '0'";
                    }
                }
            } else if (x == boDefAttribute.VALUE_CHAR) {
                if ((value == null) || value.equals("")) {
                    //toRet= toRet+" is null or "+toRet+" = '"+value+"'";
                    //toRet= toRet+" is null or "+toRet+" = ?";
                    toRet = toRet + " is null ";
    
                    //p_parameters.add( value );
                }
                else {
                    //toRet+=" = '"+value+"'";
                    if(setValue)
                    {
                        toRet += " = '"+value+"'";
                    }
                    else
                    {
                        toRet += " = ?";
                        p_parameters.add(value);
                    }
                }
            } else if ((x == boDefAttribute.VALUE_NUMBER) ||
                    (x == boDefAttribute.VALUE_DURATION) ||
                    (x == boDefAttribute.VALUE_SEQUENCE)) {
                if ((value == null) || value.equals("")) {
                    toRet = toRet + " is null";
                }
                else {
                    
                    if(groupProvider.getGroup(groupNumber).getDefAttribute() != null &&
                       "Y".equalsIgnoreCase(groupProvider.getGroup(groupNumber).getDefAttribute().getGrouping()))
                    {
                        value = value.replaceAll(",", "");
                    }
                    //  toRet+=" = "+value+"";
                    if(setValue)
                    {
                        toRet += " = "+value+" ";                    
                    }
                    else
                    {
                        toRet += " = ?";
                        p_parameters.add(value);
                    }
                    //p_parametersType.add("N");
                }
            } else if ((x == boDefAttribute.VALUE_DATETIME) ||
                    (x == boDefAttribute.VALUE_DATE)) {
                if ((value == null) || value.equals("")) {
                    //toRet=toRet+" is null or to_Char("+toRet+",'DD-MM-YYYY') = '"+value+"'";
                    if(setValue)
                    {
                        toRet = toRet + " is null or to_Char(" + toRet +
                            ",'DD-MM-YYYY') = '"+value+"' ";
                    }
                    else
                    {
                        toRet = toRet + " is null or to_Char(" + toRet +
                            ",'DD-MM-YYYY') = ? ";
                        p_parameters.add(value);
                    }
    
                    //p_parametersType.add("S");
                } else {
                    //toRet="to_Char("+toRet+",'DD-MM-YYYY') = '"+value+"'";
                    if(setValue)
                    {
                        toRet = "to_Char(" + toRet + ",'DD-MM-YYYY') = '"+value+"' ";
                    }
                    else
                    {
                        toRet = "to_Char(" + toRet + ",'DD-MM-YYYY') = ? ";
                        p_parameters.add(value);
                    }
    
                    // p_parametersType.add("S");
                }
            }
        }

        return toRet;
    }

    public String getGroupColor(int groupNumber, String value) {
        String toRet = groupProvider.getGroup(groupNumber).getColorResult(value);

        if (toRet == null) {
            toRet = "#030303";
        }

        return toRet;
    }

    public String getGroupStringToPrint(int groupNumber, String value,
        docHTML DOC) throws boRuntimeException {
        
        boolean showLink = TreeBuilder.showLink(this, groupProvider.getGroup(groupNumber));
        
        String toRet;
        
        if( groupProvider.getGroup(groupNumber).hasSpecialClauses() )
        {
            ClassColumn classCol = (ClassColumn)groupProvider.getGroup(groupNumber);
            String bouisStr = classCol.getValueResult( 
                                    value
                                    ,
                                    DOC.getEboContext()
                                );
            toRet = bouisStr;
        } 
        else
        {
            toRet = groupProvider.getGroup(groupNumber).getValueResult(value, DOC.getEboContext(), showLink);

        if ((toRet != null) && !toRet.equals("") &&
                (groupProvider.getGroup(groupNumber).getDefAttribute() != null) &&
                groupProvider.getGroup(groupNumber).getDefAttribute().getType().equalsIgnoreCase("boolean")) {
            toRet = toRet.equals("0") ? "Não" : (toRet.equals("1") ?"Sim":toRet);
        } else if ((toRet != null) && !toRet.equals("") &&
                (groupProvider.getGroup(groupNumber).getDefAttribute() != null) &&
                (groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName() != null) &&
                !groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName().equals("")) {
            toRet = boObjectUtils.getLovDescription(DOC.getEboContext(),
                    groupProvider.getGroup(groupNumber).getDefAttribute().getLOVName(), toRet);
        }
        if(toRet != null && !toRet.equals(""))
        {
            toRet = toRet.replaceAll("\n", "").replaceAll("\r", "");
        }
        }
        return toRet;
    }

    public String getSqlGroups(EboContext ctx) throws boRuntimeException {
        if (groupProvider.groupSize() == 0) {
            return null;
        }
        boolean mixedQuery = false;
        
        StringBuffer s = new StringBuffer();

        s.append(" select ");

        for (int i = 0; i < groupProvider.groupSize(); i++) {
            if( i > 0 )
            {
                s.append(',');
            }
            if (groupProvider.getGroup(i).isAttribute()) 
            {
                if(groupProvider.getGroup(i).getType() == boDefAttribute.VALUE_DATETIME)
                {
                    s.append("["+ctx.getDataBaseDriver().getDriverUtils().fnTruncateDate( p_bodef.getBoExtendedTable()+"."+groupProvider.getGroup(i).getName() ) + "]");
                }
                else
                {
                    s.append(groupProvider.getGroup(i).getName());
                }
            } 
            else 
            {
                mixedQuery = true;
                s.append(groupProvider.getGroup(i).getSQL());
            }
            s.append(" grp");
            s.append(i);
        }

        //            i++;
        
        if( !mixedQuery )
        {
            s.append(", [count(").append(" "+p_bodef.getBoExtendedTable()+".boui )] counter ");
        }
        else
        {
            s.append(", [" + p_bodef.getBoExtendedTable()+".boui] ");
        }

        s.append(" from  ");
        s.append(p_bodef.getName());
        if(p_originalBOQL == null || "".equals(p_originalBOQL))
        {
            s.append(" ext ");
        }
        else
        {
            s.append(" ext where ( ");
            s.append(p_originalBOQL);
            s.append(" ) ");
        }
        
        
//        String specialWhere = groupProvider.getSpecialWhereClause(ctx);
//        if(specialWhere != null && specialWhere.length() > 0)
//        {
//            s.append("and ").append(specialWhere);
//        }
        
        
        

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , 
						p_bouiUserQuery , p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            s.append(" and  contains '");

            //s.append( p_textFullSearch );
            s.append(getFullTextExpression(p_textFullSearch));
            s.append("'");
        }
        
        if( !mixedQuery )
        {
            boolean hasLov = false;
            s.append(" group by ");
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                //s.append( p_groups[i].getSQL() );
                s.append(" grp");
                s.append(i);
    
                if ((i + 1) < groupProvider.groupSize()) {
                    s.append(',');
                }
    
                // i++;
            }
    
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (i == 0) {
                    s.append(" order by ");
                }
                
                if(groupProvider.getGroup(i).getDefAttribute().getLOVName() == null ||
                    groupProvider.getGroup(i).getDefAttribute().getLOVName().length() == 0
                )
                {
                    s.append(" grp");
                    s.append(i);
                }
                else
                {
                    hasLov = true;
                    s.append(" ").append(getLovSql(ctx, groupProvider.getGroup(i), true, false)).append(" ");
                }

                // s.append( p_groups[i].getSQL() );
                s.append((groupProvider.getGroupOrder(i) == this.ORDER_ASC) ? " asc" : " desc");
    
                if ((i + 1) < groupProvider.groupSize()) {
                    s.append(',');
                }
            }
            
            QLParser ql = new QLParser(true);
            if(hasLov)
            {
                ql.copyGroupToOrder(false);
            }
            p_parameters.clear(); 
    
            String auxSQL = ql.toSql(s.toString(), ctx, p_parameters);
            if(p_boqlInitJSPDocBouisSql != null && p_boqlInitJSPDocBouisSql.length() > 0)
            {
                auxSQL = remakeInnerSQLWBouis(ctx, auxSQL, null, false);
            }
            return auxSQL;
        }
        else
        {
            QLParser ql = new QLParser(true);

            StringBuffer outSql = new StringBuffer( "select " );
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append("\"");
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            outSql.append(", count(").append(" boui ) counter ");
            
            p_parameters.clear();
            
            String innerSql = ql.toSql( s.toString(), ctx, p_parameters);
            if(groupProvider.hasClassColumns())
            {
                innerSql = remakeInnerSQL(ctx, innerSql);
            }
            
            outSql.append( " from ( " ).append( innerSql ).append( ") sql_count " );
            outSql.append(" group by ");
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append( "\"" );
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (i == 0) {
                    outSql.append(" order by ");
                }
    
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append("\"");
                outSql.append((groupProvider.getGroupOrder(i) == this.ORDER_ASC) ? " asc" : " desc");
    
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            String auxSQL = outSql.toString();
            if(p_boqlInitJSPDocBouisSql != null && p_boqlInitJSPDocBouisSql.length() > 0)
            {
                auxSQL = remakeInnerSQLWBouis(ctx, auxSQL, null, false);
            }
            return auxSQL;
        }
    }
    
    public String getSqlGroupsWalias(EboContext ctx) throws boRuntimeException {
        if (groupProvider.groupSize() == 0) {
            return null;
        }
        
        boolean mixedQuery = false;
        
        StringBuffer s = new StringBuffer();

        s.append(" select ");

        for (int i = 0; i < groupProvider.groupSize(); i++) {
            if( i > 0 )
            {
                s.append(',');
            }
            if (groupProvider.getGroup(i).isAttribute()) 
            {
                if(groupProvider.getGroup(i).getType() == boDefAttribute.VALUE_DATETIME)
                {
                    s.append("[" + ctx.getDataBaseDriver().getDriverUtils().fnTruncateDate( p_bodef.getBoExtendedTable()+"."+groupProvider.getGroup(i).getName()) + "]");
                }
                else
                {
                    s.append(groupProvider.getGroup(i).getName());
                }
            } 
            else 
            {
                mixedQuery = true;
                s.append(groupProvider.getGroup(i).getSQLGroup());
            }
            s.append(" grp");
            s.append(i);
        }

        //            i++;
        
        if( !mixedQuery )
        {
            s.append(", [count(").append(" "+p_bodef.getBoExtendedTable()+".boui )] counter ");
        }
        else
        {
            s.append(", [" + p_bodef.getBoExtendedTable()+".boui \"BOUI\"] ");
        }

        

        s.append(" from  ");
        s.append(p_bodef.getName());
        if(p_originalBOQL == null || "".equals(p_originalBOQL))
        {
            s.append(" ext ");
        }
        else
        {
            s.append(" ext where ( ");
            s.append(p_originalBOQL);
            s.append(" ) ");
        }
        
//        String specialWhere = groupProvider.getSpecialWhereClause(ctx);
//        if(specialWhere != null && specialWhere.length() > 0)
//        {
//            s.append("and ").append(specialWhere);
//        }
        
        
        

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , 
						p_bouiUserQuery , p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            s.append(" and  contains '");

            //s.append( p_textFullSearch );
            s.append(getFullTextExpression(p_textFullSearch));
            s.append("'");
        }
        
        if( !mixedQuery )
        {
            boolean hasLov = false;
            s.append(" group by ");
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                //s.append( p_groups[i].getSQL() );
                s.append(" grp");
                s.append(i);
    
                if ((i + 1) < groupProvider.groupSize()) {
                    s.append(',');
                }
    
                // i++;
            }
    
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (i == 0) {
                    s.append(" order by ");
                }
                
                if(groupProvider.getGroup(i).getDefAttribute().getLOVName() == null ||
                    groupProvider.getGroup(i).getDefAttribute().getLOVName().length() == 0
                )
                {
                    s.append(" grp");
                    s.append(i);
                }
                else
                {
                    hasLov = true;
                    s.append(" ").append(getLovSql(ctx, groupProvider.getGroup(i), true, false)).append(" ");
                }
                
    
                // s.append( p_groups[i].getSQL() );
                s.append((groupProvider.getGroupOrder(i) == this.ORDER_ASC) ? " asc" : " desc");
    
                if ((i + 1) < groupProvider.groupSize()) {
                    s.append(',');
                }
            }
            
            QLParser ql = new QLParser(true);
            if(hasLov)
            {
                ql.copyGroupToOrder(false);
            }
            p_parameters.clear(); 
    
            return ql.toSql(s.toString(), ctx, p_parameters);
        }
        else
        {
            QLParser ql = new QLParser(true);

            StringBuffer outSql = new StringBuffer( "select " );
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append("\"");
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            outSql.append(", count(").append(" boui ) counter ");
            
            p_parameters.clear();
            
            String innerSql = ql.toSql( s.toString(), ctx, p_parameters);
            if(groupProvider.hasClassColumns())
            {
                innerSql = remakeInnerSQLWalias(ctx, innerSql);
            }
            
            outSql.append( " from ( " ).append( innerSql ).append( ")" );
            outSql.append(" group by ");
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append( "\"" );
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (i == 0) {
                    outSql.append(" order by ");
                }
    
                outSql.append(" \"grp");
                outSql.append(i);
                outSql.append("\"");
                outSql.append((groupProvider.getGroupOrder(i) == this.ORDER_ASC) ? " asc" : " desc");
    
                if ((i + 1) < groupProvider.groupSize()) {
                    outSql.append(',');
                }
            }
            return outSql.toString();
        }
    }
    
    public String remakeInnerSQL(EboContext boctx, String sql)
    {
        String toRet = sql;
        String selectClause = sql.substring(sql.toUpperCase().indexOf("SELECT ") + 7,sql.toUpperCase().lastIndexOf("FROM"));
        String fromClause = sql.substring(sql.toUpperCase().lastIndexOf(" FROM ") + 6,sql.toUpperCase().lastIndexOf(" WHERE "));
        String whereClause = sql.substring(sql.toUpperCase().lastIndexOf(" WHERE ") + 7);
        
        for (int i = 0; i < groupProvider.groupSize(); i++) 
        {
            if(groupProvider.getGroup(i).hasSpecialClauses())
            {
                String columns[] = selectClause.split(",");
                columns[i] = groupProvider.getGroup(i).getSqlGroupcolumn(i) ;
                selectClause = "";
                for (int j = 0; j < columns.length; j++) 
                {
                    if(j > 0 )
                    {
                        selectClause += ", ";
                    }
                    selectClause += columns[j];
                }
                
                //verificar se têm filtro aplicado
                //senão tiver vai substituir e não adicionar
                fromClause += (","+groupProvider.getGroup(i).getFromGroupcolumn(i));                
                
                //
                whereClause += "AND (" + groupProvider.getGroup(i).getWhereGroupcolumn(i) +") ";
                
                    toRet = "SELECT " +selectClause + " FROM " + fromClause + " WHERE " + whereClause;
            }
        }
        return toRet;
        
    }
    
    public String remakeInnerSQLWalias(EboContext boctx, String sql)
    {
        String toRet = sql;
        String selectClause = sql.substring(sql.toUpperCase().indexOf("SELECT ") + 7,sql.toUpperCase().lastIndexOf("FROM"));
        String fromClause = sql.substring(sql.toUpperCase().lastIndexOf(" FROM ") + 6,sql.toUpperCase().lastIndexOf(" WHERE "));
        String whereClause = sql.substring(sql.toUpperCase().lastIndexOf(" WHERE ") + 7);
        
        for (int i = 0; i < groupProvider.groupSize(); i++) 
        {
            if(groupProvider.getGroup(i).hasSpecialClauses())
            {
                String columns[] = selectClause.split(",");
                columns[i] = groupProvider.getGroup(i).getSqlGroupcolumn(i) ;
                selectClause = "";
                for (int j = 0; j < columns.length; j++) 
                {
                    if(j > 0 )
                    {
                        selectClause += ", ";
                    }
                    selectClause += columns[j];
                }
                
                if(i==0)
                {
                    String orderBy = this.getOrderByString(boctx);
                    int pos = 1;
                    if(orderBy != null && orderBy.length() > 0)
                    {
                        String fields[] = this._getOrderByStringFields(boctx);
                        for (int k = 0; k < fields.length; k++) 
                        {
                           if(fields[k].toUpperCase().indexOf(" DESC") > -1)
                           {
                            String field = fields[k].substring(0, fields[k].toUpperCase().indexOf(" DESC"));
                            selectClause += field+" \"C"+pos+"\"";
                            pos++;
                           }
                           else if(fields[k].toUpperCase().indexOf(" ASC") > -1)
                           {
                            String field = fields[k].substring(0, fields[k].toUpperCase().indexOf(" ASC"));
                            selectClause += field+" \"C"+pos+"\"";
                            pos++;
                           }
                           else if(!"".equals(fields[k]))
                           {
                            selectClause += fields[k]+" \"C"+pos+"\"";
                            pos++;
                           }
                        }
                        
                    }
                }
                
                //verificar se têm filtro aplicado
                //senão tiver vai substituir e não adicionar
                fromClause += (","+groupProvider.getGroup(i).getFromGroupcolumn(i));                
                
                //
                whereClause += "AND (" + groupProvider.getGroup(i).getWhereGroupcolumn(i) +") ";
                
                    toRet = "SELECT " +selectClause + " FROM " + fromClause + " WHERE " + whereClause;
            }
        }
        return toRet;
        
    }

    public String getSql(EboContext ctx, String[] qryG)
        throws boRuntimeException {
        return getSql(ctx, qryG, true);
    }

    public String getSql(EboContext ctx, String[] qryG, boolean attAnalysis)
        throws boRuntimeException {
        StringBuffer s = new StringBuffer();

        p_extraColumns = new StringBuffer();
        if(p_hintNSecurity)
        {
            s.append("select /*NO_SECURITY*/ [").append( p_bodef.getBoExtendedTable() ).append(".BOUI ");
        }
        else
        {
            s.append("select [").append( p_bodef.getBoExtendedTable() ).append(".BOUI ");
        }
        s.append( getOrderByStringFields() );
        s.append("] " );
        for (int i = 0; (i < columnsProvider.columnsSize()) && attAnalysis; i++) {
            if (!columnsProvider.getColumn(i).isAttribute()) {
                s.append(',');
                s.append(columnsProvider.getColumn(i).getSQL());
                s.append(" ");
                s.append(columnsProvider.getColumn(i).getName());

                if (p_extraColumns.length() != 0) {
                    p_extraColumns.append(',');
                }

                p_extraColumns.append(columnsProvider.getColumn(i).getName());
            } else {
                if (columnsProvider.getColumn(i).getDefAttribute().getAtributeDeclaredType()!=boDefAttribute.ATTRIBUTE_OBJECTCOLLECTION)
                  {
                  //if (i!=0) s.append(',');
                  //s.append( columnsProvider.getColumn(i).getName() );
                  
  //                if(columnsProvider.getColumn(i).getDefAttribute().getLOVName() != null &&
  //                    columnsProvider.getColumn(i).getDefAttribute().getLOVName().trim().length() > 0
  //                )
  //                {
  //                    s.append(getLovSql(ctx, columnsProvider.getColumn(i), true, true));
  //                }
  //                else
  //                {
                      s.append(',');
                      s.append(columnsProvider.getColumn(i).getName());
  //                }
                  
                  s.append(" ");
                  s.append(columnsProvider.getColumn(i).getName().replaceAll("\\.", "\\$"));
  
                  if (p_extraColumns.length() != 0) {
                      p_extraColumns.append(',');
                  }
  
                  p_extraColumns.append(columnsProvider.getColumn(i).getName().replaceAll("\\.",
                          "\\$"));
                }
            }
        }

        //            i++;
        s.append(" from  ");
        s.append(p_bodef.getName());
        if(p_originalBOQL == null || "".equals(p_originalBOQL))
        {
            s.append(" ext ");
        }
        else
        {
            s.append(" ext where ( ");
            s.append(p_originalBOQL);
            s.append(" ) ");
        }

        p_parameters.clear();

        boolean hasSpecialClauses = false;
        if ((qryG != null) && (groupProvider.groupSize() > 0)) {
            boolean first = true;
            
            for (int j = 0; j < groupProvider.groupSize(); j++) 
            {
                if(!groupProvider.getGroup(j).hasSpecialClauses())
                {
                    if(first)
                    {
                        s.append(" and ( ");
                        first = false;
                    }
                    else
                    {
                        s.append(" and  ");
                    }
                    s.append('(');
                    s.append(groupSQL(ctx,j, qryG[j]));
                    s.append(')');
                }
                else
                {
                    hasSpecialClauses = true;
                }
            }
            if(!first)
            {
                s.append(" ) ");
            }
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
				boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , 
						p_bouiUserQuery,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            String fullText = "";
             try
             {
                 fullText = getFullTextExpression(p_textFullSearch);
             }
             catch (Exception e)
             {
                 //ignore
             }
             
             if(!"".equals(fullText))
             {
                p_parameters.add(fullText);

                //   p_parametersType.add("S");
                s.append(" and  contains ? ");

                //s.append(" and  contains '");
                //s.append( p_textFullSearch );
                //s.append("'");
             }
        }

        s.append(" ");
        s.append(this.getOrderByString(ctx));

        QLParser ql = new QLParser(true);

        String sql = ql.toSql(s.toString(), ctx, p_parameters);

        if(hasSpecialClauses)
        {
            sql = remakeInnerSQL(ctx, sql, qryG, false);
        }
        
        if(p_boqlInitJSPDocBouisSql != null && p_boqlInitJSPDocBouisSql.length() > 0)
        {
            sql = remakeInnerSQLWBouis(ctx, sql, qryG, false);
        }
        return sql;
    }
    
    public String remakeInnerSQL(EboContext boctx, String sql, String[] qryG, boolean setValue)
    {
        String toRet = sql;
        int fromPos = getStart(sql, "FROM");
        int wherePos = getStart(sql, "WHERE");
        String selectClause = sql.substring(sql.toUpperCase().indexOf("SELECT ") + 7,fromPos);
        String fromClause = sql.substring(fromPos + 5,wherePos);
        int groupi = sql.toUpperCase().indexOf(" GROUP ");
        int orderi = sql.toUpperCase().indexOf(" ORDER ");
        String whereClause ="";
        String orderClause ="";
        String groupClause ="";
        
        if(groupi > -1)
        {
            whereClause = sql.substring(wherePos + 6, groupi);
            if(orderi > -1)
            {
                groupClause = sql.substring(groupi + 10, orderi);
                orderClause = sql.substring(orderi + 10);
            }
            else
            {
                groupClause = sql.substring(groupi + 10);
            }
        }
        else if(orderi > -1)
        {
            if(wherePos > -1)
            {
                whereClause = sql.substring(wherePos + 6, orderi);
            }
            orderClause = sql.substring(orderi + 10);
        }
        else if(wherePos > -1)
        {
            whereClause = sql.substring(wherePos + 6);
        }
        
        String columns[] = null;
        for (int i = 0; i < groupProvider.groupSize(); i++) 
        {
            if(groupProvider.getGroup(i).hasSpecialClauses())
            {
                
//                columns[i] = groupProvider.getGroup(i).getSqlGroupcolumn(i) ;
//                selectClause = "";
//                for (int j = 0; j < columns.length; j++) 
//                {
//                    if(j > 0 )
//                    {
//                        selectClause += ", ";
//                    }
//                    selectClause += columns[j];
//                }
                
                //verificar se têm filtro aplicado
                //senão tiver vai substituir e não adicionar
                fromClause += (","+groupProvider.getGroup(i).getFromGroupcolumn(i));                
                
                //
                whereClause += " AND (" + groupProvider.getGroup(i).getWhereGroupGridcolumn(i, qryG, setValue, p_parameters) +") ";
                
                toRet = "SELECT " +selectClause + " FROM " + fromClause + " WHERE " + whereClause + (groupi == -1 ? "":" GROUP BY " + groupClause) +  (orderi == -1 ? "":" ORDER BY " + orderClause);
            }
        }
        return toRet;
        
    }
    
    public String remakeInnerSQLWBouis(EboContext boctx, String sql, String[] qryG, boolean setValue)
    {
        String toRet = sql;
        int fromPos = getStart(sql, "FROM");
        int wherePos = getStart(sql, "WHERE");
        
        boolean isSelectFrom = false;
        if( wherePos == -1 )
        {
            isSelectFrom = true;
            wherePos = sql.toUpperCase().lastIndexOf("WHERE");
        }
        
        String selectClause = sql.substring(sql.toUpperCase().indexOf("SELECT ") + 7,fromPos);
        String fromClause = null;
        int groupi = sql.toUpperCase().indexOf(" GROUP ");
        int orderi = sql.toUpperCase().indexOf(" ORDER ");

        if( isSelectFrom )
        {
            if( groupi > -1 )
            {
                fromClause = sql.substring(fromPos + 5,groupi);
            }
            else if ( orderi > -1 )
            {
                fromClause = sql.substring(fromPos + 5,orderi);
            }
            else
            {
                fromClause = sql.substring(fromPos + 5);
            }
        }
        else
        {
            fromClause = sql.substring(fromPos + 5,wherePos);
        }

        String whereClause ="";
        String orderClause ="";
        String groupClause ="";
        
        if(groupi > -1)
        {
            whereClause = sql.substring(wherePos + 6, groupi);
            if(orderi > -1)
            {
                groupClause = sql.substring(groupi + 10, orderi);
                orderClause = sql.substring(orderi + 10);
            }
            else
            {
                groupClause = sql.substring(groupi + 10);
            }
        }
        else if(orderi > -1)
        {
            if(wherePos > -1)
            {
                whereClause = sql.substring(wherePos + 6, orderi);
            }
            orderClause = sql.substring(orderi + 10);
        }
        else if(wherePos > -1)
        {
            whereClause = sql.substring(wherePos + 6);
        }
         
        if( isSelectFrom )
        {
            String tempSql = "SELECT " +selectClause + " FROM ";
            tempSql += fromClause.substring( 0,fromClause.lastIndexOf(")") );
            tempSql += " AND OEEbo_Document.BOUI in ("+ p_boqlInitJSPDocBouisSql +") ";
            tempSql += " ) ";
            tempSql += (groupi == -1 ? "":" GROUP BY " + groupClause) +  (orderi == -1 ? "":" ORDER BY " + orderClause);
            toRet = tempSql;
            
            //toRet = "SELECT " +selectClause + " FROM " + fromClause + " WHERE " + whereClause +  +         
        } 
        else
        {
            toRet = "SELECT " +selectClause + " FROM " + fromClause + " WHERE " + whereClause + " AND OEEbo_Document.BOUI in ("+ p_boqlInitJSPDocBouisSql +") " + (groupi == -1 ? "":" GROUP BY " + groupClause) +  (orderi == -1 ? "":" ORDER BY " + orderClause);        
        }
        
        return toRet;
        
    }
    
    private static int getStart(String sql, String search)
    {
        String upperSQL = sql.toUpperCase();
        boolean keepgoing = true;
        String mid;
        int pos = 7, newpos =0, fromFounded = 0, selectFound = 1;
        int aux = 0;
        search = " " + search.toUpperCase() + " ";
        while(keepgoing)
        {
            newpos = upperSQL.indexOf(search, newpos);
            if(newpos != -1)
            {
                fromFounded++;
                mid = upperSQL.substring(pos, newpos);
                while((aux = mid.indexOf("SELECT ", aux)) != -1)
                {
                    selectFound++;
                    aux = aux+7;
                }
                if(fromFounded == selectFound)
                {
                    return newpos + 1;
                }
                //o primeiro select
                selectFound = 1;
                newpos = newpos+search.length();
            }
            else
            {
                keepgoing = false;
            }
        }
        return -1;
    }
    
    public String[] getSqlForCount(EboContext ctx)throws boRuntimeException 
    {        
        return getSqlForExportWBridges(ctx,new String[0],true);
    }
    public String[] getSqlForExportWBridges(EboContext ctx, String[] qryG)throws boRuntimeException 
    {
        return getSqlForExportWBridges(ctx,qryG,false);
    }

    public String getBouisSql(EboContext ctx, int min, int max)
        throws boRuntimeException {
        StringBuffer s = new StringBuffer();
        if(groupProvider.groupSize() > 0)
        {
            String sql = getSqlGroups(ctx);
            s.append("select \"BOUI\" from (");
            String selectClause = sql.substring(sql.toUpperCase().indexOf("SELECT ") + 7,sql.toUpperCase().indexOf("FROM"));
            String fromClause = sql.substring(sql.toUpperCase().indexOf(" FROM ") + 6,sql.toUpperCase().indexOf(" WHERE "));
            int groupi = sql.toUpperCase().indexOf(" GROUP ");
            int orderi = sql.toUpperCase().indexOf(" ORDER ");
            String whereClause =""; 
            String orderClause = "";
            String groupClause = "";
            
            if(groupi > -1)
            {
                whereClause = sql.substring(sql.toUpperCase().indexOf(" WHERE ") + 7, groupi);
                if(orderi > -1)
                {
                    groupClause = sql.substring(groupi + 10, orderi);
                }
            }
            if(orderi > -1)
            {
                if(groupi == -1)
                {
                    whereClause = sql.substring(sql.toUpperCase().indexOf(" WHERE ") + 7, orderi);
                }
                orderClause = sql.substring(orderi + 10);
            }
            else if("".equals(whereClause) && sql.toUpperCase().indexOf(" WHERE ") > -1)
            {
                whereClause = sql.substring(sql.toUpperCase().indexOf(" WHERE ") + 7);
            }
            //boui
            String columns[] = selectClause.split(",");
            if(!groupProvider.hasClassColumns())
            {
                columns[1] = "OE" + p_bodef.getName() + ".BOUI \"BOUI\"";
            }
            else
            {
                columns[1] = "\"BOUI\"";
            }
            selectClause = "";
            for (int j = 0; j < columns.length; j++) 
            {
                if(j > 0 )
                {
                    selectClause += ", ";
                }
                selectClause += columns[j];
            }
            groupClause = groupClause + "," + "\"BOUI\"";
            
            String orderBy = this.getOrderByString(ctx);
            int pos = 1;
            String newOrder = "";
            if(orderBy != null && orderBy.length() > 0)
            {
                String fields[] = this.getOrderByStringArray(ctx);
                fields[0] = fields[0].substring(fields[0].toUpperCase().indexOf("ORDER BY ") + 8);
                ColumnProvider colP;
                
                for (int k = 0; k < fields.length; k++) 
                {
                   if(fields[k].trim().toUpperCase().indexOf("BOUI") == -1 || fields[k].trim().toUpperCase().indexOf("SELECT") != -1)
                   {
                       if(fields[k].toUpperCase().indexOf(" DESC") > -1)
                       {
                        String field = (fields[k].substring(0, fields[k].toUpperCase().indexOf(" DESC"))).trim();;
                        if(field.startsWith("[")) field = field.substring(1);
                        colP = columnsProvider.getColumn(field);
                        if(!groupProvider.hasClassColumns())
                        {
                            if(!groupProvider.columnInGroup(colP))
                            {
                                if(!colP.hasSpecialClauses())
                                {
                                    groupClause  += ", "+field;
                                    selectClause += ", "+field;
                                    newOrder += ", "+field+" DESC";
                                }
                                else
                                {
                                    ClassColumn classC = (ClassColumn)colP; 
                                    if(groupClause.toUpperCase().indexOf(classC.getBridegAttDBName()) == -1)
                                    {
                                        selectClause += ", " + classC.getSQLNoParRect() + "\""+classC.getName()+"\"";
                                        newOrder += ", "+field+" DESC";
                                    }
                                }
                            }
                        }
                        else
                        {
                            /*groupClause  += ", \"C"+pos+"\"";
                            selectClause += ", \"C"+pos+"\"";
                            newOrder += ", \"C"+pos+"\" DESC";
                            pos++;*/
                        }
                        
                       }
                       else if(fields[k].toUpperCase().indexOf(" ASC") > -1)
                       {
                        String field = (fields[k].substring(0, fields[k].toUpperCase().indexOf(" ASC"))).trim();
                        if(field.startsWith("[")) field = field.substring(1);
                        colP = columnsProvider.getColumn(field);
                        if(!groupProvider.hasClassColumns())
                        {
                            if(!colP.hasSpecialClauses())
                            {
                                groupClause  += ", "+field;
                                selectClause += ", "+field;
                                newOrder += ", "+field+" ASC";
                            }
                            else
                            {
                                ClassColumn classC = (ClassColumn)colP; 
                                if(groupClause.toUpperCase().indexOf(classC.getBridegAttDBName()) == -1)
                                {
                                    selectClause += ", " + classC.getSQLNoParRect() + "\""+classC.getName()+"\"";
                                    newOrder += ", "+field+" ASC";
                                }
                            }
                        }
                        else
                        {
//                            groupClause  += ", \"C"+pos+"\"";
//                            selectClause += ", \"C"+pos+"\"";
//                            newOrder += ", \"C"+pos+"\" ASC";
//                            pos++;
                        }
                       }
                       else if(!"".equals(fields[k]))
                       {
                        String field = fields[k].trim();;
                        if(fields[k].indexOf("]") > -1)
                        {
                            field = field.substring(0, fields[k].indexOf("]"));
                        }
                        if(field.startsWith("[")) field = field.substring(1);
                        colP = columnsProvider.getColumn(field);
                        if(!groupProvider.hasClassColumns())
                        {
                            if(!colP.hasSpecialClauses())
                            {
                                groupClause  += ", "+field;
                                selectClause += ", "+field;
                                newOrder += ", "+field+" ASC";
                            }
                            else
                            {
                                ClassColumn classC = (ClassColumn)colP; 
                                if(groupClause.toUpperCase().indexOf(classC.getBridegAttDBName()) == -1)
                                {
                                    selectClause += ", " + classC.getSQLNoParRect() + "\""+classC.getName()+"\"";
                                    newOrder += ", "+field+" ASC";
                                }
                            }
                        }
                        else
                        {
                            groupClause  += ", \"C"+pos+"\"";
                            selectClause += ", \"C"+pos+"\"";
                            newOrder += ", \"C"+pos+"\" ASC";
                            pos++;
                        }
                       }
                   }
                }
                
            }
            
            
            s.append("SELECT ").append(selectClause)
            .append(" FROM ").append(fromClause)
            .append(" WHERE ").append(whereClause)
            .append(" GROUP BY ").append(groupClause);
            if(orderClause.length() > 0)
            {
                s.append(" ORDER BY ").append(orderClause);
                if(this.getOrderByString(ctx) != null && this.getOrderByString(ctx).length() > 0)
                {
                    s.append(newOrder);
                    if(orderClause.toUpperCase().indexOf("\"BOUI\"") == -1 && newOrder.toUpperCase().indexOf("\"BOUI\"") == -1)
                    {
                        s.append(", \"BOUI\"");
                    }
                }
            }
            else if(this.getOrderByString(ctx) != null && this.getOrderByString(ctx).length() > 0)
            {
                s.append(this.getOrderByString(ctx));
            }
            
            s.append(")");
        }
        else
        {
            s.append(getSql(ctx, new String[0]));
        }
        return s.toString();
    }

    public String[] getSqlForExportWBridges(EboContext ctx, String[] qryG, boolean forCount)
        throws boRuntimeException {
        
        Vector qryBr = new Vector();
        StringBuffer s = null;
        if(forCount)
        {
            s = new StringBuffer("select [COUNT(" + p_bodef.getBoExtendedTable() + ".BOUI)]");            
        }
        else
        {
            s = new StringBuffer("select [" + p_bodef.getBoExtendedTable() + ".CLASSNAME, " +
                p_bodef.getBoExtendedTable() + ".BOUI BPR]");            
        }

        //percorrer todos os atributos da tree
        for (int i = 0; i < columnsProvider.columnsSize(); i++) {
            
            //testar se o atributo é do tipo bridge, ou pasaa por uma
            String[] attPath = columnsProvider.getColumn(i).getName().split("\\.");
            boolean hasBr = false;

            //defenição do objecto a que o atributo pertence    
            boDefAttribute pathBoDefAtt = null;

            //defenição do atributo
            boDefHandler pathBoDef = p_bodef;

            //percorrer todos os atributos até chegar ao pretendido
            for (int j = 0; j < attPath.length; j++) {
                pathBoDefAtt = pathBoDef.getAttributeRef(attPath[j]);

                if (pathBoDefAtt == null) //significa que o atributo é de um objecto filho
                {
                    boDefHandler[] subDefs = pathBoDef.getTreeSubClasses();

                    for (int x = 0; x < subDefs.length; x++)
                    {
                        if ((pathBoDefAtt = subDefs[x].getAttributeRef(
                                        attPath[j])) != null) {
                            break;
                        }
                    }
                }
                
                if(pathBoDefAtt==null)
                  break;
                
                //testar se o atributo currente é do tipo bridge
                if( (pathBoDefAtt.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE) &&
                    (pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_MULTI_VALUES ||
                     pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_1_TO_N ||
                     pathBoDefAtt.getRelationType() == boDefAttribute.RELATION_1_TO_N_WBRIDGE))
                {
                      hasBr = true;
                      break;                  
                }
                else if( j<attPath.length-1)
                {
                    pathBoDef = pathBoDefAtt.getReferencedObjectDef();
                }
            }


            if(hasBr)
            {
                qryBr.add(columnsProvider.getColumn(i).getName());
            }
            else
            {

                if(!forCount)
                {
                    if(columnsProvider.getColumn(i).getDefAttribute()==null ||
                       !columnsProvider.getColumn(i).isAttribute()
                    )
                    {
                      s.append(", "+columnsProvider.getColumn(i).getSQL());
                    }
                    else
                    {
                      s.append(", "+columnsProvider.getColumn(i).getName() );
                    }
                }

            }
        }
        
        StringBuffer s2 = new StringBuffer();
        s2.append(" from ").append(p_bodef.getName());
        if(p_originalBOQL != null && !"".equals(p_originalBOQL))
        {
            s2.append(" where (").append(p_originalBOQL).append(")");
        }

        p_parameters.clear();

        if ((qryG != null) && (groupProvider.groupSize() > 0)) {
            s2.append(" and ( ");

            for (int j = 0; j < groupProvider.groupSize(); j++) {
                s2.append('(');
                s2.append(groupSQL(ctx,j, qryG[j]));

                if ((j + 1) < groupProvider.groupSize()) {
                    s2.append(") and  ");
                } else {
                    s2.append(')');
                }
            }

            s2.append(" ) ");
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , 
						p_bouiUserQuery ,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s2.append(" and ( ");
                s2.append(boql_user1);
                s2.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s2.append(" and ( ");
                s2.append(boql_user2);
                s2.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            p_parameters.add(getFullTextExpression(p_textFullSearch));

            s2.append(" and  contains ? ");
        }

        s2.append(" ");
        s2.append(this.getOrderByString(ctx));

        s.append(s2);
        
        QLParser ql = new QLParser(true);

        String[] qlSql = new String[(qryBr.size()*2)+1];
        qlSql[0] =  ql.toSql(s.toString(), ctx, p_parameters).replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());

        //construir a query das bridges
        for (int i = 0; i < qryBr.size(); i++) 
        {
          String name = (String)qryBr.get(i);
          StringBuffer s3 = new StringBuffer();
          s3.append("select BOUI, ");
          s3.append(name); 
          s3.append(s2);
          
          qlSql[(i*2)+1] = name;
          qlSql[(i*2)+2] = ql.toSql(s3.toString(), ctx, p_parameters).replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());
        }
        
        return qlSql;
    }

	public boolean analizeSql( EboContext ctx ) throws boRuntimeException
    {
        
        StringBuffer s=new StringBuffer();
        boolean toRet=false;
        p_extraColumns = new StringBuffer();
        if(p_hintNSecurity)
        {
            s.append("select /*NO_SECURITY*/ boui");
        }
        else
        {
            s.append("select boui");
        }
        
        for (int i = 0; i < columnsProvider.columnsSize() ; i++) 
        {
           if( columnsProvider.getColumn(i) != null )
           {
               if (!columnsProvider.getColumn(i).isAttribute()  )
               {
                    s.append(',');
                    s.append( columnsProvider.getColumn(i).getSQL() );
                    s.append(" ");
                    s.append( columnsProvider.getColumn(i).getName() );
                    if( p_extraColumns.length() !=0) 
                    {
                        p_extraColumns.append(',');
                    }
                    p_extraColumns.append( columnsProvider.getColumn(i).getName() );
                    
               }
               else if ( columnsProvider.getColumn(i).isExternalAttribute() )
               {
                   //if (i!=0) s.append(',');
                   //s.append( columnsProvider.getColumn(i).getName() );
                   
                    s.append(',');
                    s.append( columnsProvider.getColumn(i).getName());
                    s.append(" ");
                    s.append( columnsProvider.getColumn(i).getName().replaceAll("\\.","\\$") );
                    
                    if( p_extraColumns.length() !=0) 
                    {
                        p_extraColumns.append(',');
                    }
                    p_extraColumns.append( columnsProvider.getColumn(i).getName().replaceAll("\\.","\\$") );
                    
               }
           }            
        
        }
//            i++;
        
        boolean whereClause = false;
        s.append(" from  ");
        s.append( p_bodef.getName() );
        if(p_originalBOQL == null || "".equals(p_originalBOQL))
        {
            s.append(" ext ");
        }
        else
        {
            whereClause = true;
            s.append(" ext where ( ");
            s.append(p_originalBOQL);
            s.append(" ) ");
        }
                
        String specialWhere = columnsProvider.getSpecialWhereClause(ctx);
        if(specialWhere != null && specialWhere.length() > 0)
        {
            if(!whereClause)
            {
                s.append(" where ");
                whereClause = true;
            }
            else
            {
                s.append(" and ");
            }
            s.append(specialWhere);
        }
        
        
        p_parameters.clear();
        
        
        
        
        if ( p_textUserQuery!= null   || p_bouiUserQuery!=-1)
        {
            String boql_user1=null;
            String boql_user2=null;
            if ( p_textUserQuery != null )
            {   
                 boql_user1=userquery.userQueryToBoql_ClauseWhere( ctx , p_textUserQuery  );
            }
            if ( p_bouiUserQuery != -1 )
            {
                 boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , p_bouiUserQuery ,p_userParameters ) ;
            }
            
            if ( boql_user1!=null && boql_user1.length() >0 )
            {
                if(!whereClause)
                {
                    s.append(" where (");
                    whereClause = true;
                }
                else
                {
                    s.append(" and (");
                }
                s.append( boql_user1 );
                s.append( " ) ");
            }
           
            if ( boql_user2!=null && boql_user2.length() >0 )
            {
                if(!whereClause)
                {
                    s.append(" where (");
                    whereClause = true;
                }
                else
                {
                    s.append(" and (");
                }
                s.append( boql_user2 );
                s.append( " ) ");
            } 
           
            
        }
        
        

        if ( !p_textFullSearch.equals("") )
             {
             String fullText = "";
             try
             {
                 fullText = getFullTextExpression(p_textFullSearch);
             }
             catch (Exception e)
             {
                 //ignore
             }
             
             if(!"".equals(fullText))
             {
                 p_parameters.add( fullText );
                 //p_parametersType.add("S");
                 if(!whereClause)
                {
                    s.append(" where ");
                    whereClause = true;
                }
                else
                {
                    s.append(" and  ");
                }
                 s.append("contains ? ");
                 //s.append(" and  contains '");
                 //s.append( p_textFullSearch );
                 //s.append("'");
             }

             }
        
        s.append(" ");     
        s.append( this.getOrderByString(ctx) );
        
        QLParser ql =   new QLParser( true );
        String sql=null;
        
         try
         {
             sql = ql.toSql( s.toString() , ctx , p_parameters );
             toRet=true;
         }
         catch (Exception e)
         {
            e.printStackTrace();
             
             
         }
        return toRet;        
    }

    public String getSqlForExport(EboContext ctx, String[] qryG)
        throws boRuntimeException {
        StringBuffer s = new StringBuffer();
        s.append("select CLASSNAME, BOUI BPR");

        //percorrer todos os atributos da tree
        for (int i = 0; i < columnsProvider.columnsSize(); i++) {
            if(columnsProvider.getColumn(i).getDefAttribute()==null ||
               !columnsProvider.getColumn(i).isAttribute() 
            )
              s.append(", "+columnsProvider.getColumn(i).getSQL());
            else
              s.append(", "+columnsProvider.getColumn(i).getName() );
        }
        
        s.append(" from ").append(p_bodef.getName());
        if(p_originalBOQL != null && !"".equals(p_originalBOQL))
        {
            s.append(" where (").append(p_originalBOQL).append(")");
        }

        p_parameters.clear();

        if ((qryG != null) && (groupProvider.groupSize() > 0)) {
            s.append(" and ( ");

            for (int j = 0; j < groupProvider.groupSize(); j++) {
                s.append('(');
                s.append(groupSQL(ctx,j, qryG[j]));

                if ((j + 1) < groupProvider.groupSize()) {
                    s.append(") and  ");
                } else {
                    s.append(')');
                }
            }

            s.append(" ) ");
        }

        if ((p_textUserQuery != null) || (p_bouiUserQuery != -1)) {
            String boql_user1 = null;
            String boql_user2 = null;

            if (p_textUserQuery != null) {
                boql_user1 = userquery.userQueryToBoql_ClauseWhere(ctx,
                        p_textUserQuery);
            }

            if (p_bouiUserQuery != -1) {
                boql_user2=userquery.userQueryToBoql_ClauseWhere( ctx , 
						p_bouiUserQuery ,p_userParameters ) ;
            }

            if ((boql_user1 != null) && (boql_user1.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user1);
                s.append(" ) ");
            }

            if ((boql_user2 != null) && (boql_user2.length() > 0)) {
                s.append(" and ( ");
                s.append(boql_user2);
                s.append(" ) ");
            }
        }

        if (!p_textFullSearch.equals("")) {
            p_parameters.add(getFullTextExpression(p_textFullSearch));

            s.append(" and  contains ? ");
        }

        s.append(" ");
        s.append(this.getOrderByString(ctx));

        QLParser ql = new QLParser(true);

        String qlSQL = ql.toSql(s.toString(), ctx, p_parameters);
        
        qlSQL=qlSQL.replaceAll("OE"+p_bodef.getName(),"O"+p_bodef.getName());

        return qlSQL;
    }

    /**
     *
     *
     * @param srcAtr Atributo que é para mover pode estar nos Grupos ou nas colunas
     * @param destAtr Atributo de referencias para inserir o srcAtr á esquerda
     */
    public void moveAttributeToColHeader(String srcAtr, String destAtr) {
        boolean removeFromGroup = false;
        boolean removeFromOtherCol = false;

        if (srcAtr.equalsIgnoreCase(destAtr)) {
            return;
        }

        for (int i = 0; i < columnsProvider.columnsSize(); i++) {
            if (columnsProvider.getColumn(i).getName().equalsIgnoreCase(srcAtr)) {
                columnsProvider.setColumn(i, null);
                removeFromOtherCol = true;

                break;
            }
        }

        if (!removeFromOtherCol) {
            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (groupProvider.getGroup(i).getName().equalsIgnoreCase(srcAtr)) {
                    groupProvider.setGroup(i, null);
                    removeFromGroup = true;

                    break;
                }
            }
        }

        if (removeFromGroup) {
            this.p_htmlCurrentPage = 1;

            ColumnProvider[] x_groups = new ColumnProvider[groupProvider.groupSize() -
                1];
            boolean[] x_groups_optionOPEN = new boolean[groupProvider.groupSize() - 1];
            int[] x_groups_optionMAXLINES = new int[groupProvider.groupSize() - 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[groupProvider.groupSize() -
                1];
            int z = 0;

            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (groupProvider.getGroup(i) != null) {
                    x_groups[z] = groupProvider.getGroup(i);
                    x_groups_optionMAXLINES[z] = groupProvider.getGroupOptionMaxLines(i);
                    x_groups_optionOPEN[z] = groupProvider.getGroupOptionOpen(i);
                    x_groups_optionSHOWALLVALUES[z++] = groupProvider.getGroupOptionShowAllValues(i);
                }
            }

            groupProvider.setGroups(x_groups);
            clearOpenGroups();
            groupProvider.setGroupOptionMaxLines(x_groups_optionMAXLINES);
            groupProvider.setGroupOptionOpen(x_groups_optionOPEN);
            groupProvider.setGroupOptionShowAllValues(x_groups_optionSHOWALLVALUES);
        } else if (removeFromOtherCol) {
            ColumnProvider[] x_cols = new ColumnProvider[columnsProvider.columnsSize() -
                1];
            String[] x_colsWidth = new String[columnsProvider.columnsSize() - 1];
            int z = 0;

            for (int i = 0; i < columnsProvider.columnsSize(); i++) {
                if (columnsProvider.getColumn(i) != null) {
                    x_cols[z++] = columnsProvider.getColumn(i);
                }
            }

            columnsProvider.setColumns(x_cols);
        }

        if (removeFromGroup || removeFromOtherCol) {
            ColumnProvider[] x_cols = new ColumnProvider[columnsProvider.columnsSize() +
                1];

            int z = 0;
            boolean alreadydo = false;

            for (int i = 0; i < columnsProvider.columnsSize(); i++) {
                if (columnsProvider.getColumn(i).getName().equalsIgnoreCase(destAtr) && !alreadydo) {
                    x_cols[z++] = (ColumnProvider) columnsProvider.getAttribute(srcAtr);
                    alreadydo = true;
                }

                x_cols[z++] = columnsProvider.getColumn(i);
            }

            columnsProvider.setColumns(x_cols);
        }
    }

    /**
     *
     *
     * @param srcAtr Atributo que é para mover pode estar nos Grupos ou nas colunas
     * @param destAtr Atributo de referencias para inserir o srcAtr á esquerda
     */
    public void moveAttributeToColGroup(String srcAtr, String destAtr) {
        boolean removeFromGroup = false;
        boolean removeFromOtherCol = false;

        if (srcAtr.equalsIgnoreCase(destAtr)) {
            return;
        }

        this.p_htmlCurrentPage = 1;

        for (int i = 0; i < groupProvider.groupSize(); i++) {
            if (groupProvider.getGroup(i).getName().equalsIgnoreCase(srcAtr)) {
                groupProvider.setGroup(i, null);
                removeFromGroup = true;

                break;
            }
        }

        if (!removeFromGroup) {
            for (int i = 0; i < columnsProvider.columnsSize(); i++) {
                if (columnsProvider.getColumn(i).getName().equalsIgnoreCase(srcAtr)) {
                    if (columnsProvider.columnsSize() == 1) {
                        return;
                    }

                    columnsProvider.setColumn(i,null);
                    removeFromOtherCol = true;

                    break;
                }
            }
        }

        if (removeFromGroup) {
            ColumnProvider[] x_groups = new ColumnProvider[groupProvider.groupSize() -
                1];
            boolean[] x_groups_optionOPEN = new boolean[groupProvider.groupSize() - 1];
            int[] x_groups_optionMAXLINES = new int[groupProvider.groupSize() - 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[groupProvider.groupSize() -
                1];
            byte[] x_groups_order = new byte[groupProvider.groupSize()];
            int z = 0;

            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (groupProvider.getGroup(i) != null) {
                    x_groups[z] = groupProvider.getGroup(i);
                    x_groups_optionMAXLINES[z] = groupProvider.getGroupOptionMaxLines(i);
                    x_groups_optionOPEN[z] = groupProvider.getGroupOptionOpen(i);
                    x_groups_order[z] = groupProvider.getGroupOrder(i);
                    x_groups_optionSHOWALLVALUES[z++] = groupProvider.getGroupOptionShowAllValues(i);
                }
            }

            groupProvider.setGroups(x_groups);
            clearOpenGroups();
            groupProvider.setGroupOptionMaxLines(x_groups_optionMAXLINES);
            groupProvider.setGroupOptionOpen(x_groups_optionOPEN);
            groupProvider.setGroupOptionShowAllValues(x_groups_optionSHOWALLVALUES);
            groupProvider.setGroupOrder(x_groups_order);
        } else if (removeFromOtherCol) {
            ColumnProvider[] x_cols = new ColumnProvider[columnsProvider.columnsSize() -
                1];
            String[] x_colsWidth = new String[columnsProvider.columnsSize() - 1];
            int z = 0;

            for (int i = 0; i < columnsProvider.columnsSize(); i++) {
                if (columnsProvider.getColumn(i) != null) {
                    x_cols[z++] = columnsProvider.getColumn(i);
                }
            }

            columnsProvider.setColumns(x_cols);
        }

        if (removeFromGroup || removeFromOtherCol) {
            ColumnProvider[] x_groups = new ColumnProvider[groupProvider.groupSize() +
                1];
            boolean[] x_groups_optionOPEN = new boolean[groupProvider.groupSize() + 1];
            int[] x_groups_optionMAXLINES = new int[groupProvider.groupSize() + 1];
            boolean[] x_groups_optionSHOWALLVALUES = new boolean[groupProvider.groupSize() +
                1];
            byte[] x_groups_order = new byte[groupProvider.groupSize() + 1];
            int z = 0;
            boolean alreadydo = false;

            for (int i = 0; i < groupProvider.groupSize(); i++) {
                if (groupProvider.getGroup(i).getName().equalsIgnoreCase(destAtr) && !alreadydo) {
                    x_groups[z] = (ColumnProvider) columnsProvider.getAttribute(srcAtr);
                    x_groups_optionMAXLINES[z] = 30;
                    x_groups_optionOPEN[z] = true;
                    x_groups_order[z] = this.ORDER_ASC;
                    x_groups_optionSHOWALLVALUES[z++] = false;

                    alreadydo = true;
                }

                x_groups[z] = groupProvider.getGroup(i);
                x_groups_optionMAXLINES[z] = groupProvider.getGroupOptionMaxLines(i);
                x_groups_optionOPEN[z] = groupProvider.getGroupOptionOpen(i);
                x_groups_order[z] = groupProvider.getGroupOrder(i);
                x_groups_optionSHOWALLVALUES[z++] = groupProvider.getGroupOptionShowAllValues(i);
            }

            if (destAtr.equals("NoNe")) {
                x_groups[z] = (ColumnProvider) columnsProvider.getAttribute(srcAtr);
                x_groups_optionMAXLINES[z] = 30;
                x_groups_optionOPEN[z] = true;
                x_groups_order[z] = this.ORDER_ASC;
                x_groups_optionSHOWALLVALUES[z++] = false;
            }

            groupProvider.setGroups(x_groups);
            clearOpenGroups();
            groupProvider.setGroupOptionMaxLines(x_groups_optionMAXLINES);
            groupProvider.setGroupOptionOpen(x_groups_optionOPEN);
            groupProvider.setGroupOptionShowAllValues(x_groups_optionSHOWALLVALUES);
            groupProvider.setGroupOrder(x_groups_order);
        }
    }

    public void setExplorerFullText(String value)
    {
        p_textFullSearch=value;
        if(p_menu != null)
        {
            p_menu.getSearchComponent().setSearchText(value);
        }
    }
    
    public void setPreviewObject(long value)
    {
        p_bouiPreview=value;
        if(p_previewRight != null)
        {
            p_previewRight.setObject(value);
            p_previewDown.setObject(value);
        }
    }

    public static String getDefTreeName(String objName, String boql)
    {
        if(boql != null && !"".equals(boql))
        {
            return objName + boql.hashCode();
        }
        else
        {
            return objName;
        }
    }    
    
    public static ngtXMLHandler createExplorerViewer(boDefHandler obj, String boql)
    {
        XMLElement root = new XMLElement("explorer");
        String name = null;
        if(boql != null && !"".equals(boql))
        {
            name = obj.getName() + boql.hashCode();
        }
        else
        {
            name = obj.getName();
        }
        root.setAttribute("name", name);
        XMLElement attribute = new XMLElement("attributes");
        XMLElement cols = new XMLElement("cols");
        boDefAttribute[] atts= obj.getAttributesDef();
        XMLElement aux;
        XMLElement aux2;
        XMLElement aux3;
        int count = 5;
        for (int i = 0; i < atts.length; i++) 
        {
            aux = new XMLElement(atts[i].getName());
            if(atts[i].getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE ||
                atts[i].getRelationType() != boDefAttribute.TYPE_OBJECTATTRIBUTE ||
                atts[i].getRelationType() <= 1)
            {
                if(count > 0)
                {
                    aux2 = new XMLElement("col");
                    aux3 = new XMLElement("attribute");
                    aux3.addText(atts[i].getName());
                    aux2.appendChild(aux3);            
                    cols.appendChild(aux2);
                }
                count--;
                if(atts[i].getType().toLowerCase().equals("datetime"))
                {
                    aux.setAttribute("width", "120");
                }
                else if(atts[i].getType().toLowerCase().equals("date"))
                {
                    aux.setAttribute("width", "80");
                }
                else if(atts[i].getType().toLowerCase().equals("boolean"))
                {
                    aux.setAttribute("width", "20");
                }
                else if(atts[i].getType().toLowerCase().equals("number"))
                {
                    aux.setAttribute("width", "80");
                }
                else
                {
                    aux.setAttribute("width", "170");
                }
                attribute.appendChild(aux);
            }
        }
        root.appendChild(attribute);
        root.appendChild(cols);
        root.appendChild(new XMLElement("order"));
        if(boql==null)
        {            
            XMLCDATA cdata = new XMLCDATA();
            cdata.setData("select " + obj.getName() + " ext where 1=1");
            aux2 = new XMLElement("boql");
            aux2.appendChild(cdata);
            root.appendChild(aux2);
        }
        else
        {
            XMLCDATA cdata = new XMLCDATA();
            cdata.setData(boql);
            aux2 = new XMLElement("boql");
            aux2.appendChild(cdata);
            root.appendChild(aux2);
        }
        root.appendChild(new XMLElement("groups"));
        XMLDocument d = new XMLDocument();
        d.appendChild(root);
        return new ngtXMLHandler(root);
    }
    
    public void setParameterDesignMode(boolean b)
    {
        p_onlyparameters = b;
        if(p_menu != null && p_menu.getMenuItemById("buttonPanel") != null)
        {
            p_menu.getMenuItemById("buttonPanel").setDisabled(b);
        }
    }
    public boolean getParameterDesignMode()
    {
        return p_onlyparameters;
    }
    
    public String getId()
    {
        if(p_bodef != null)
        {
            return p_bodef.getLabel();
        }
        //default
        return "";
    }
    public String getImg()
    {
        if(p_bodef != null)
        {
            return p_bodef.getSrcForIcon16();
        }
        //default
        return "";
    }
    private ArrayList getTables(String sql)
    {
        if(sql == null || "".equals(sql))
        {
            return new ArrayList(0);
        }
        int sFrom = sql.toUpperCase().indexOf("FROM");
        int sWhere = sql.toUpperCase().indexOf("WHERE");
        if(sFrom > -1)
        {
            String tables;
            if(sWhere > -1)
            {
                tables = sql.substring(sFrom+5, sWhere);
            }
            else
            {
                tables = sql.substring(sFrom+5);
            }
            String sTables[] = tables.split(",");
            ArrayList r = new ArrayList();
            for (int i = 0; i < sTables.length; i++) 
            {
                r.add(sTables[i].trim());
            }
            return r;
        }
        return new ArrayList(0);
    }
    
    public String getStringValue( EboContext ctx, ResultSet rs, int index, boDefAttribute attDef) throws boRuntimeException, SQLException
    {
         if (attDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE )
         {
             return ""+rs.getLong(index);
//             if ( b > 0 )
//             {
//                 boObject o =  boObject.getBoManager().loadObject( ctx, b );
//                 return o.getCARDID().toString();
//             }
//             else return "";
         }
         else if("boolean".equalsIgnoreCase(attDef.getType()))
         {
            String value = rs.getString(index);
            if("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if("1".equals(value))
            {
                return "Sim";
            }
            return value;
         }             
         else if(attDef.getLOVName() != null &&  
            !"".equals(attDef.getLOVName()))
         {
            String xlov = attDef.getLOVName(); 
            String value = rs.getString(index);
            if(value != null && !"".equals(value))
            {
                lovObject lovObj = LovManager.getLovObject(ctx, xlov);
                if(lovObj != null)
                {
                    lovObj.beforeFirst();
                    while(lovObj.next())
                    {
                        if(value.equalsIgnoreCase(lovObj.getCode()))
                        {
                            return lovObj.getDescription();
                        }
                    }
                }
            }
            return "";
         }
         else if("dateTime".equalsIgnoreCase(attDef.getType()) || 
                 "date".equalsIgnoreCase(attDef.getType()))
         {
            Date d = rs.getDate(index);            
            if(d != null)
            {
                 String dateFormat=this.getAttributes()[index].explorerAtt.getFormat();
                SimpleDateFormat formatter = null;
                 if (dateFormat!=null && !dateFormat.equals(""))
                    formatter = new SimpleDateFormat (dateFormat);
                else
                    formatter = new SimpleDateFormat ("dd-MM-yyyy");
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            NumberFormat currencyFormatter = NumberFormat.getInstance();
            if("VALUENUMBER".equalsIgnoreCase(attDef.getName()))
            {
                return rs.getString(index);
            }
            if(attDef.getDecimals() != 0)
            {
                //currency
                currencyFormatter.setParseIntegerOnly(false);
                if("Y".equalsIgnoreCase(attDef.getGrouping()))
                {
                    currencyFormatter.setGroupingUsed(true);
                }
                currencyFormatter.setMaximumFractionDigits(attDef.getDecimals());
                currencyFormatter.setMinimumFractionDigits(attDef.getMinDecimals());
                currencyFormatter.setMinimumIntegerDigits(1);
                return currencyFormatter.format(rs.getDouble(index));
            }
            else if("Y".equalsIgnoreCase(attDef.getGrouping()))
            {
                currencyFormatter.setParseIntegerOnly(false);
                currencyFormatter.setMinimumIntegerDigits(1);
                currencyFormatter.setGroupingUsed(true);
                return currencyFormatter.format(rs.getDouble(index));
            }
            return rs.getString(index);
         }
    }
    
    public String getStringValue( EboContext ctx, ResultSet rs, int index, byte type) throws boRuntimeException, SQLException
    {
        
         if (type == boDefAttribute.VALUE_NUMBER || type == boDefAttribute.VALUE_DURATION)
         {
             return ""+rs.getLong(index);
         }
         else if(type == boDefAttribute.VALUE_BOOLEAN)
         {
            String value = rs.getString(index);
            if("0".equals(value))
            {
                //falta verificar a lingua
                return "Não";
            }
            else if("1".equals(value))
            {
                return "Sim";
            }
            return value;
         }
         else if(type == boDefAttribute.VALUE_DATETIME || type == boDefAttribute.VALUE_DATE)
         {
            Date d = rs.getDate(index);            
			if(d != null)
            {
                String dateFormat=this.getAttributes()[index].explorerAtt.getFormat();
                SimpleDateFormat formatter = null;
                 if (dateFormat!=null && !dateFormat.equals(""))
                    formatter = new SimpleDateFormat (dateFormat);
                else
                {
                    formatter = new SimpleDateFormat ("dd-MM-yyyy");                             
                }
                 return formatter.format(d);
            }
            return "";
         }
         else
         {
            String value = rs.getString(index);
            if(value != null)
            {
                return value;
            }
            return "";
         }
    }


    public ExplorerOptions getExplorerOptions()
    {
        return p_explorerOptions;
    }
    
    public String getFullTextExpression( String text ) 
    {
        return text; 
    }
    
    public ColumnsProvider getColumnsProvider()
    {
        return columnsProvider;
    }
    
    public GroupProvider getGroupProvider()
    {
        return groupProvider;
    }
    
    public String getExplorerName()
    {
        return p_formName;
    }
    
    private void backExplorer(EboContext boctx)
    {
        try {
        String xDir=boConfig.getDeploymentDir();
        long pBoui= boctx.getBoSession().getPerformerBoui();
        System.out.println("Tree def a ler:" + xDir+File.separator+pBoui+File.separator+p_key+".xml");
        File file = new File(xDir+File.separator+pBoui+File.separator+p_key+".xml");
        if ( file.exists())
        {
         ngtXMLHandler defTreeUser = new ngtXMLHandler( ngtXMLUtils.loadXMLFile( file.getAbsolutePath() ));
         defTreeUser = defTreeUser.getFirstChild();
         backDefTreeUserExplorer(defTreeUser, boctx);
         ngtXMLUtils.saveXML(defTreeUser.getDocument(), new File("C:\\aa.xml"));
        }
        else
        {
        
        }
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    private void backDefTreeUserExplorer(ngtXMLHandler defTreeUser, EboContext boctx)
    {
        p_textFullSearch = "";
        p_filterName = "";
        p_svExplorerName = "";
        
        groupProvider.readGroups(defTreeUser, boctx, columnsProvider);
        
        columnsProvider.readCols(defTreeUser, p_bodef, boctx);

        columnsProvider.readOrders(defTreeUser, p_bodef, boctx, p_orders, p_ordersDirection);
        
		ngtXMLHandler aux = defTreeUser.getChildNode("fulltext");
		if(aux != null)
			this.setExplorerFullText(aux.getText());
			
		aux = defTreeUser.getChildNode("filter");
		if(aux != null)
			this.setTextUserQuery(null,aux.getText());
			
        //inicializações
        if(this.p_explorerOptions.isGroupsZoneVisible() && groupProvider.groupSize() > 0)
        {
            p_group.setDisplay(true);
        }
        else
        {
            p_group.setDisplay(false);
        }
        try
        {
            if(this.p_explorerOptions.isParametersZoneVisible() && haveParameters(boctx))
            {
                p_param.setDisplay(true);
            }
            else
            {
                p_param.setDisplay(false);
            }
        }
        catch (boRuntimeException e)
        {
            
        }
    }
    
    private void clearOrders()
    {
        for (int i = 0; i < p_orders.length ; i++) 
        {
            p_orders[i] = null;
            p_ordersDirection[i] = null;
        }
    }
    
    public boolean hasBoqlInitJSP()
    {
        return p_hasBoqlInitJSP;
    }
    
    public boolean isBoqlInitSet()
    {
        return p_menu.isBoqlInitSet();
    }
    public String getBoqlInitJSP()
    {
        return p_boqlInitJsp;
    }
    
    public void setOriginalBoql(String boql)
    {
        p_originalBOQL = boql;
    }
    
    public String getOriginalBoql()
    {
        return p_originalBOQL;
    }
    
    public void setBoqlInitJSPClassificationBoql(String boql)
    {
        p_boqlInitJSPClassificationBoql = boql;
    }
    
    public String getBoqlInitJSPClassificationBoql()
    {
        return p_boqlInitJSPClassificationBoql;
    }
    
    public void setBoqlInitJSPDocBouisSql(String sql)
    {
        p_boqlInitJSPDocBouisSql = sql;
    }
    
    public String getBoqlInitJSPDocBouisSql()
    {
        return p_boqlInitJSPDocBouisSql;
    }
    
    public void setCheckOnclickEvent(String event)
    {
        checkOnClickEvent = event;
    }
    
    public String getCheckOnclickEvent()
    {
        return checkOnClickEvent;
    }
    
    public void setCheckValues(String values)
    {
        checkValues = values;
    }
    
    public String getCheckValues()
    {
        return checkValues;
    }
    
    public boolean isChecked(long boui)
    {
        if(checkValues == null || "".equals(checkValues))
        {
            return false;
        }
        try
        {
            String values[] = checkValues.split(";");
            for (int i = 0; i < values.length; i++) 
            {
                if(boui == Long.parseLong(values[i]))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            //ignore
        }
        return false;
    }
}
