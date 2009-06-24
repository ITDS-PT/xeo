<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*,netgest.bo.localized.*"%>



<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

    try {
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            response.sendRedirect("login.jsp");
         return;
    }

    if(boctx==null) 
    {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    int IDX;
    int cvui;
  
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    
    
    String tboui=request.getParameter("treeBoui");
    
    long treeBoui= netgest.utils.ClassUtils.convertToLong(tboui,-1);
    
    String relatedClientIDX=request.getParameter("relatedIDX");
    String designerMode=request.getParameter("designerMode");
    
    
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boDefHandler bodef = null;
    boDefHandler[] Classes = boDefHandler.getAllBoDefinition();

    boObject prg = DOC.getObject( treeBoui );
    String xmlSrc = prg.getAttribute("xmlTree").getValueString();
    if ( xmlSrc==null || xmlSrc.length() ==0)
    {
        xmlSrc="<tree name=\"bpmDefault\">";
        xmlSrc+="	<description>"+JSPMessages.getString("uiDesigner.1")+"</description>";
        xmlSrc+="</tree>";
    }
    
    /*else
    {
        char gg= xmlSrc.charAt( xmlSrc.length()-1);
        
        
        while ( xmlSrc.charAt( xmlSrc.length()-1) == 13 || xmlSrc.charAt( xmlSrc.length()-1) == 10 )
        {
            xmlSrc = xmlSrc.substring(0,xmlSrc.length()-1);
        
        }
    }
    */
    
    idbolist=ctrl[1];
    
   
    String attributes;
   
    
           
%>
<% if( treeBoui != -1222 ){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Tree Designer</title>

<style type="text/css">
    @import url('xeoNew.css');
 </style>

<style>
 @import url('ieThemes/0/global/ui-global.css');
 @import url('ieLibrary/splitter/splitter.css');
 @import url('ieThemes/0/splitter/ui-splitter.css');
 @import url('ieThemes/0/menuTree/ui-menuTree.css');
 </style>
 

 <script LANGUAGE="javascript" SRC="ieLibrary/menuTree/design_elements.js"></script>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_core.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_events.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/form/jsObjects.js"></script>
</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

</head>
<script>
TREE_TEMA_DIR = 'ieThemes/0/tree/';
WKFL_TEMA_DIR = 'ieThemes/0/menuTree/';
TREEROW_COLOR_SEL ="#DAE6E9";


var _hsos=[];
var _hso=null;
var ii=0;
function getElement(e){
  if(e&&e.target) return e.target;
  return window.event.srcElement;
}
 
function getToElement(e){
  if(e&&e.target) return e.relatedTarget;
  return window.event.toElement;
}

function getRuntimeStyle(o){
  if(o.runtimeStyle) return o.runtimeStyle;
  else return o.style;
}


function so(id){
  if(!_hsos[id]){
   _hsos[id]=new Object();	
   _hsos[id].hb=document.getElementById(id+'_body');
   _hsos[id].id=id;
   }
   _hso=_hsos[id];
}



var systemObjects=[];
<%
String[] clsBoui = netgest.xwf.designer.cacheClsReg.getBouiClassName( boctx ) ;
for( int i=0; i< clsBoui.length ;i++ )  {
%>
systemObjects['<%=clsBoui[i].split(":")[0]%>' ] = <%=clsBoui[i].split(":")[1] %>;<%}%>


var programObjects=[];
<%

 boObjectList listPrgs = boObjectList.list( boctx,"select xwfProgram ",new Object[]{},1,1000,"name",false);
            int i=0;
            while ( listPrgs.next() ) 
            {
                %>programObjects['<%=listPrgs.getObject().getAttribute("name")%>']=<%=listPrgs.getObject().getBoui()%>;
          <%}%>

            



document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}
window.onbeforeunload=readPendentes;

function readPendentes()
{
    
    for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
    {
        //unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
        unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
    }
    
    
}

var TREE_EDIT=null;	
var xmlSrc=null;	
function initTree( treeiD )
{
    var e = document.getElementById(treeiD);
    xmlSrc = new ActiveXObject("Microsoft.XMLDOM");
    xmlSrc.async	= false;
    xmlSrc.loadXML(xSrc.xml);
    var actualVersion="1.0.0";
    var root = xmlSrc.selectSingleNode("/tree");
    var version = root.getAttribute("versionXML");
    if ( version == null )
    {
        version="0";
    }
    if ( version==0 )
    {
        
        
    }
    
    root.setAttribute("versionXML","1.0.0");
    
    TREE_EDIT = new xwTree( e , xmlSrc ,true, true, null, "design");

}
	
	
</script>


<body scroll="no" onload="initTree('t2')">
<form id="wForm">
<xml id='xSrc'>
<%=xmlSrc%>
</xml>
<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    
    
    <col id="dd" width="200">
	<col width="4">
	<col />
    
    

    <tr>
        <td valign="top">
            <style>
                 @import url('ieLibrary/listBar/listBar.css');
                 @import url('ieThemes/0/listBar/ui-listBar.css');
            </style>
            
            </style>
            
            <%
            boObjectList packages= boObjectList.list( boctx , "select Ebo_Package where deployed=1 order by description ",1,1000);
            //packages.first();
            boObject pack;//= packages.getObject();
            
            %>
            <table defaultItem="__elementos" class='listBar_Beh listBar' cellSpacing="0" cellPadding="2"  >
                
                
                
                 <tr  style='height:30px' >
                    <td class='listBar_header' >XXXXXXXXXX</td> 
                    </td>
                 </tr>
                
                 <tr style='height:50%'>
                    <td valign='top' style="padding:4px;">
                         
                          <div id="__elementos"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                                    
                                    <tr>
                                        <td style='padding-top:5px;padding:bottom:5px' class='gcell_std' valign="middle">
                                            <img ondragstart="startDragNew('OPTIONLINK')" src="ieThemes/0/workPlace/webpage.gif" WIDTH="16" HEIGHT="16">
                                        </td>
                                        <td class='gcell_std' valign="middle"><%=JSPMessages.getString("uiDesigner.2")%></td>
                                    </tr>
                                    
                                    <tr>
                                        <td style='padding-top:5px;padding:bottom:5px' class='gcell_std' valign="middle">
                                            <img ondragstart="startDragNew('OPTIONFOLDER')" src="ieThemes/0/workPlace/folder.gif" WIDTH="16" HEIGHT="16">
                                        </td>
                                        <td class='gcell_std' valign="middle"><%=JSPMessages.getString("uiDesigner.3")%></td>
                                    </tr>
                                    <tr>
                                        <td style='padding-top:5px;padding:bottom:5px' class='gcell_std' valign="middle">
                                            <img ondragstart="startDragNew('OPTION')" src="ieThemes/0/workPlace/item.gif" WIDTH="16" HEIGHT="16">
                                        </td>
                                        <td class='gcell_std' valign="middle"><%=JSPMessages.getString("uiDesigner.4")%></td>
                                    </tr>

                                    <tr height=100%>
                                        <td colspan=2 class='gcell_std' valign="middle"></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                         
                         
                         <% 
                         packages.beforeFirst();             
                         while ( packages.next() )
                         {
                            pack = packages.getObject();
                         %>
                         
                         <div id="<%=pack.getAttribute("name").getValueString()%>"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                                    
                                    <% 
                                    boObjectList clsObjects = boObjectList.list( boctx," select Ebo_ClsReg where deployed=1 and xeopackage="+pack.getBoui()+" order by description " ,1,3000); 
                                    clsObjects.beforeFirst();
                                    while ( clsObjects.next() )
                                    {
                                        boObject o = clsObjects.getObject();
                                    %>
                                    <tr>
                                        <td style='padding-top:5px;padding:bottom:5px' class='gcell_std' valign="middle"><img ondragstart="startDragNew('OPTOBJ:<%=o.getAttribute("name").getValueString()%>')" src="resources/<%=o.getAttribute("name").getValueString()%>/ico16.gif" / WIDTH="16" HEIGHT="16"></td><td class='gcell_std' valign="middle"><%=o.getAttribute("description").getValueString()%></td>
                                    </tr>
                                    <%
                                    }
                                    %>
                                    <tr height=100%>
                                        <td colspan=2 class='gcell_std' valign="middle"></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        <%
                        }
                        %>
                    </td>
                </tr>
                <tr style='height=50%' relatedContent='tblct' ><td style="padding:4px;">
                     <div style='height:100%;width:100%;overflow:auto' >
                     
                         <table id='tblct' style='width:100%' cellpadding="0" cellspacing="0">
                                <tr relatedItem='__elementos' style='height:25px' >
                                    <td class='listBar_item' style='padding-left:3px'><%=JSPMessages.getString("uiDesigner.5")%></td> 
                                    </td>
                                </tr>
                           <%
                                packages.beforeFirst();             
                                while ( packages.next() )
                                {
                                    pack = packages.getObject();
                                 %>
                                         
                                         
                                <tr relatedItem='<%=pack.getAttribute("name").getValueString()%>' style='height:25px' >
                                    <td class='listBar_item' style='padding-left:3px'><%=pack.getAttribute("description").getValueString()%></td> 
                                    </td>
                                </tr>
                                <%
                                }
                                %>
                         </table>
                    </div>
                    </td>
                </tr>
                
                
            </table>

        
        </td>
		<td splitter width="4"><img class="rszLeft ui-rszLeft" src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1"></td>
		
		<td valign="top" style="background-color:#FFFFFF">
     
			<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;height:100%;width:100%">
				<tbody>
				
                <style type="text/css">
                    @import url('ieLibrary/menu/menu.css');
                    @import url('ieThemes/0/menu/ui-menu.css');
                </style>
                
                <tr height=30px id="menuUI" >
					<td valign=top  >
                    
                     <span class="mnubar menu" colorOption="BAR">
                        <table  cellSpacing="0" cellPadding="0">
                        
                        <tbody>
                        <tr height="30">
                            <td tabindex="0" style="padding-left:3px;font-weight:bold" >
                            <nobr><%=JSPMessages.getString("uiDesigner.6")%></nobr>
                            </td>
                            <td tabindex="0" style='width:100%' >
                            &nbsp;
                           </td>

                           <td tabindex="0" >
                            <img title='Imprimir desenho' style='cursor:hand' onclick="window.print()" src='ieThemes/0/wkfl/print.gif' />
                           </td>                           
                           <td tabindex="0" >
                            <img onclick="TREE_EDIT.refreshHTM(true)" src='ieThemes/0/wkfl/refresh.gif' />
                           </td>
                           <td tabindex="0">
                                 <img ondragenter="dragEnterTrash()" ondragLeave="dragLeaveTrash()"  ondragover="dragOverTrash()" ondrop="dropTrash()" src='ieThemes/0/wkfl/recyclebin.gif' />
                           </td>
                        </tr>
                        </tbody>
                        </table>
                    </span>
        
                    <!--
                    <table class='layout'   cellpadding="0" cellspacing="0">
                        <col width=100% >
                        <col width=50>
                        <col width=100>
                        <tr>
                            <td class='headerProgram' >
                                Definição do programa 
                            <td class='headerProgram'>&nbsp;</td>
                            <td class='headerProgram' >
                                <table>
                                <tr>
                                <td>
                                <img ondragenter="dragEnterTrash()" ondragLeave="dragLeaveTrash()"  ondragover="dragOverTrash()" ondrop="dropTrash()" src='ieThemes/0/wkfl/recyclebin.gif' />
                                </td>
                                <td>
                                <img onclick="TREE_EDIT.refreshHTM(true)" src='ieThemes/0/wkfl/refresh.gif' />
                                </td>
                                </tr>
                                </table>
                            <td>
                            <td>
                                &nbsp;    
                            <td>
                        </tr>
                    </table>
                    -->
                    </td>
				</tr>
                
				<tr height="70%">
					<td valign="top">
						<div style="overflow:auto;height:100%;width:100%">
				
						<table id="t2" cellSpacing="0" BORDER="0" cellPadding="0" style="width:100%;table-layout:fixed;">
							<colgroup>
							<col WIDTH="20px">
							<col WIDTH="15px">
							<col WIDTH="15px">
							<col WIDTH="100%" />
						</table>
				
						</div>
				
					</td>
				</tr>
				
				<tr  height=4px id="trRizeBottom" >
					<td valign=top  ><img class="rszUp ui-rszUp" hspace=0 vspace=0 src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1px" /></td>
				</tr>
				
				<tr>
					<td  valign="top" style="">
               
					<div  id="workCell" style='height:100%;overflow:auto'>
					
					</div>
                    
					</td>
				</tr>
				
				</tbody>
			</table>	
		
		
		</td>
		
	</tr>
	
   </tbody>
  </table>
</form>

<script language="jscript">
function BuildXml()
{   
try 
{
    var xmlQuery="";
    for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
    {
        //unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
        unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
    }
    var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
    var elem = xmlQuery.createElement("uiTreeLB__xmlTree");
    elem.setAttribute("isXML","false");
    elem.appendChild( xmlQuery.createTextNode(xmlSrc.documentElement.xml) );
    xmlQuery.appendChild( elem );
//    xmlQuery+= "<uiTreeLB__xmlTree isXML=\"false\" ><![CDATA[";    
//    xmlQuery+=;
//    xmlQuery+="]]></uiTreeLB__xmlTree>";
    return xmlQuery.documentElement.xml;  
}
catch(e)
{
    alert(e.message);
    return "";
}
}
</script>
<iframe id='helperFrame' style='display:none' ></iframe>
<FORM name='boFormSubmit' action="" method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=treeBoui%>' name='treeBoui' />
    <INPUT type='hidden' value='<%=relatedClientIDX%>' name='clientIDX' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>

</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
