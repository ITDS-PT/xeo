<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>


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
    
    
    
    String pboui=request.getParameter("runtimeProgramBoui");
    
    long programBoui= netgest.utils.ClassUtils.convertToLong(pboui,-1);
    
    String relatedClientIDX=request.getParameter("relatedIDX");
    String designerMode=request.getParameter("designerMode");
    
    
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boDefHandler bodef = null;
    boDefHandler[] Classes = boDefHandler.getAllBoDefinition();

    boObject prg = DOC.getObject( programBoui );
    String xmlSrc = prg.getAttribute("flow").getValueString();
    if ( xmlSrc==null || xmlSrc.length() ==0)
    {
        xmlSrc=" <program versionXML='1.2.0' gui_open='true' name=\"Sem nome\">";
        xmlSrc+="<label><![CDATA[]]></label>";
        xmlSrc+="<labelFormula><![CDATA[]]></labelFormula>";
        xmlSrc+="<executewhen><![CDATA[]]></executewhen>";
        xmlSrc+="<code>";
        xmlSrc+="<defVariables>";
        xmlSrc+="</defVariables>";
        xmlSrc+="<defParticipants gui_open='true'>";
        xmlSrc+="</defParticipants>";
        xmlSrc+="<comment><![CDATA[-- início do fluxo --]]></comment>";
        xmlSrc+="</code>";
        xmlSrc+="</program>";
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
<% if( programBoui != -1222 ){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>WorkFlow Designer</title>

<style type="text/css">
    @import url('xeoNew.css');
 </style>

<style>
 @import url('ieThemes/0/global/ui-global.css');
 
 @import url('ieLibrary/splitter/splitter.css');
 @import url('ieThemes/0/splitter/ui-splitter.css');
 
 @import url('ieLibrary/menu/menu.css');
 @import url('ieThemes/0/menu/ui-menu.css');
 
 
    
 
 @import url('ieLibrary/wkfl/wkfl.css');
 @import url('ieThemes/0/wkfl/ui-wkfl.css');
  
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 
 
 </style>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/menu/menu.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/wkfl/elements.js"></script>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_core.js"></script>
  
 <script LANGUAGE="javascript" SRC="ieLibrary/form/jsObjects.js"></script>
<script LANGUAGE="javascript" SRC="xeo.js"></script>
</head>



</head>
<script>

TREE_TEMA_DIR = 'ieThemes/0/tree/';
WKFL_TEMA_DIR = 'ieThemes/0/wkfl/';
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
        unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
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
    TREE_EDIT = new xwTree( e , xmlSrc ,true, false , true, "design");

}
	
	
</script>


<body scroll="no" onload="initTree('t2')">
<form id="wForm">
<xml id='xSrc'>
<%=xmlSrc%>
</xml>
<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
	<col width=100% />
    <tr>
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
                            <nobr>Defini败s do programa</nobr>
                            </td>
                            <td tabindex="0" style='width:100%' >
                            &nbsp;
                           </td>
                          
                           
                          
                        </tr>
                        </tbody>
                        </table>
                    </span>
        
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
    for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
                                {
                                    unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
                                }

        var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
        var elem = xmlQuery.createElement("xwfProgramDef__flow");
        elem.setAttribute("isXML","false");
        elem.appendChild( xmlQuery.createTextNode(xmlSrc.documentElement.xml) );
        xmlQuery.appendChild( elem );
        

//        var xmlQuery=""; //"<bo boui=\""+boFormSubmit.programBoui.value+"\" >";
//        xmlQuery+= "<xwfProgramDef__flow isXML=\"true\" ><![CDATA[";    
//        //xmlQuery+=xmlSrc.xml;
//        xmlQuery+=encodeURIComponent(xmlSrc.xml);
//        //<![CDATA[9]]>
//        xmlQuery+="]]></xwfProgramDef__flow>";
//        //xmlQuery+="</bo>";
//        //alert(xmlQuery);
        
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
    <INPUT type='hidden' value='<%=programBoui%>' name='programBoui' />
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
