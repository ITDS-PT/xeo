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
    
    
    
    String pboui=request.getParameter("programBoui");
    
    long programBoui= netgest.utils.ClassUtils.convertToLong(pboui,-1);
    
    String relatedClientIDX=request.getParameter("relatedIDX");
    String designerMode=request.getParameter("designerMode");
    
    
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boDefHandler bodef = null;
    boDefHandler[] Classes = boDefHandler.getAllBoDefinition();

    boObject prg = DOC.getObject( programBoui );
    String xmlSrc =  prg.getAttribute("flow").getValueString();
    if ( xmlSrc==null || xmlSrc.length() ==0)
    {
        xmlSrc=" <program versionXML='1.5.1' gui_open='true' name=\"Sem nome\">";
        xmlSrc+="<label><![CDATA[]]></label>";
        xmlSrc+="<labelFormula><![CDATA[]]></labelFormula>";
        xmlSrc+="<executewhen><![CDATA[]]></executewhen>";
        xmlSrc+="<code>";
        xmlSrc+="<defVariables>";
        xmlSrc+="</defVariables>";
        xmlSrc+="<defParticipants gui_open='true'>";
        xmlSrc+="</defParticipants>";
        xmlSrc+="<activityAlerts>";
        xmlSrc+="</activityAlerts>";
        xmlSrc+="<defProcedures>";
        xmlSrc+="</defProcedures>";
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
  @media screen {
      div.container{height:100%;overflow:auto;width:100%}
       .noprint{}
       col.col1{width:200px}
       col.col2{width:4px}
       col.col3{width:100%}
    }
 @media print{
      div.container{}
      .noprint{display:none}
       col.col1{width:0px}
       col.col2{width:0px}
       col.col3{width:100%}     
    }
 </style>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/menu/menu.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/wkfl/elements.js"></script>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_core.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_events.js"></script>
 
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
    var version = xmlSrc.selectSingleNode("/program").getAttribute("versionXML");
    if ( version == null )
    {
        version="0";
    }
    if ( version=="0" )
    {
        alert("Converting XML from version 0")
        var vars=xmlSrc.selectSingleNode("//defVariables");
        for( var i=0; i<vars.childNodes.length ; i++ )
        {
            var v = vars.childNodes(i);
            v.appendChild( newElement("linkVar","" , xmlSrc , false ));
            v.appendChild( newElement("haveLinkVars","n" , xmlSrc , false ));
		    v.appendChild( newElement("linkAttribute","" , xmlSrc , false ));
        }
        version="1.0.0";
    }
    if ( version=="1.0.0" )
    {
        alert("Converting XML from version 1.0.0 to 1.1.0");
        var nodes = xmlSrc.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {	
			var name=nodes(p).getAttribute("name");
            var nodeName = nodes(p).nodeName;
			if ( nodeName =='send' )
			{
            
			  	 nodes(p).appendChild( newElement("subject","" , xmlSrc , true ));
                 nodes(p).appendChild( xmlSrc.createElement("channel") );

            
		    }
            else if( nodeName == "program" )
            {
                 nodes(p).appendChild( newElement("labelFormula","" , xmlSrc , true ));
                 nodes(p).appendChild( newElement("label","" , xmlSrc , true ));
                 
            }
		 }
		 version="1.1.0";
    }
    if ( version=="1.1.0" )
    {
        alert("Converting XML from version 1.1.0 to 1.2.0");
         var nodes = xmlSrc.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {	
			var name=nodes(p).getAttribute("name");
            var nodeName = nodes(p).nodeName;
		
			if ( nodeName =='send' || nodeName=="activity" || nodeName=="choice" || nodeName=="fillVariable" || nodeName=="waitResponse" || nodeName=="decision" || nodeName=="userCallProgram")
			{
                 nodes(p).appendChild( xmlSrc.createElement("process") );
		    }
		 }
		 version="1.2.0";
    }
    if ( version=="1.2.0" )
    {
         alert("Converting XML from version 1.2.0 to 1.3.0");
        
         var node=xmlSrc.selectSingleNode("//defVariables");
         
         //node.insertBefore(  xmlSrc.createElement("activityAlerts") , node);
         node.parentNode.insertBefore(  xmlSrc.createElement("activityAlerts") , node.nextSibling );
		 
         var nodes = xmlSrc.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {	
			var name=nodes(p).getAttribute("name");
            var nodeName = nodes(p).nodeName;
		
			if ( nodeName =='send' || nodeName=="activity" || nodeName=="choice" || nodeName=="fillVariable" || nodeName=="waitResponse" || nodeName=="decision" || nodeName=="userCallProgram")
			{
                 nodes(p).appendChild( xmlSrc.createElement("alerts") );
		    }
		 }
         
         
		 version="1.3.0";
    }
    if ( version=="1.3.0" )
    {        
         alert("Converting XML from version 1.3.0 to 1.4.0");
        
         var node=xmlSrc.selectSingleNode("//defParticipants");

         node.parentNode.insertBefore(  xmlSrc.createElement("defProcedures"), node.nextSibling);
		 
         var nodeVariables=xmlSrc.selectSingleNode("//defVariables");
         for ( var p = 0 ; p < nodeVariables.childNodes.length;p++ )
		 {
            if(nodeVariables.childNodes[p].nodeName == 'defVariable')
            {
                var objTemplate = xmlSrc.createElement("objTemplate");
                objTemplate.setAttribute('boui', '');
                objTemplate.appendChild(xmlSrc.createElement("keyWords"))
                nodeVariables.childNodes[p].appendChild(objTemplate);
            }
         }
         var nodes = xmlSrc.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {
            var nodeName = nodes(p).nodeName;
            if ( nodeName =='send' || nodeName=="activity" || nodeName=="choice" || nodeName=="fillVariable" || nodeName=="waitResponse" || nodeName=="decision" || nodeName=="userCallProgram")
			{
                var oneShotActivity = xmlSrc.createElement("oneShotActivity");
                oneShotActivity.text = 'false';
                nodes[p].appendChild(oneShotActivity);
                
                var showTask = xmlSrc.createElement("showTask");
                showTask.text = 'false';
                nodes[p].appendChild(showTask);
                
                var showReassign = xmlSrc.createElement("showReassign");
                showReassign.text = 'false';
                nodes[p].appendChild(showReassign);
                
                var showWorkFlowArea = xmlSrc.createElement("showWorkFlowArea");
                showWorkFlowArea.text = 'false';
                nodes[p].appendChild(showWorkFlowArea);
            }
		 }
         
         var nodes = xmlSrc.selectNodes("//send");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {
            var nodeMsg = nodes[p].selectSingleNode("//message")
            nodeMsg.text = '';
            nodeMsg.setAttribute('name','');
            
            for (var j = 0; j < nodes[p].childNodes.length; j++)
            {
                if(nodes[p].childNodes[j].nodeName == 'subject')
                {
                    nodes[p].removeChild(nodes[p].childNodes[j]);    
                }
            }
		 }
         
         
		 version="1.4.0";
    }
    
    if ( version=="1.4.0" )
    { 
        alert("Converting XML from version 1.4.0 to 1.5.0");
        var alerts = xmlSrc.selectSingleNode("/program/code/activityAlerts")
        if ( alerts == null )
        {
             var node=xmlSrc.selectSingleNode("//defParticipants");
             node.parentNode.insertBefore(  xmlSrc.createElement("activityAlerts") , node.nextSibling );
             
             
        }
        alerts = xmlSrc.selectSingleNode("/program/activityAlerts")
        if ( alerts != null )
        {
            xmlSrc.selectSingleNode("/program").removeChild(alerts);
        }
        var nodes = xmlSrc.selectNodes("//*");
         for ( var p = 0 ; p < nodes.length;p++ )
		 {
            var nodeName = nodes(p).nodeName;
            if ( nodeName=="decision" )
			{
                var approval=xmlSrc.createElement("approval");
                approval.setAttribute("boui","");
                approval.setAttribute("validAnswer","TRUE");
                nodes[p].appendChild(approval);
            }
		 }
    
        version="1.5.0";
    }
    if ( version=="1.5.0" )
    {
         alert("Converting XML from version 1.5.0 to 1.5.1");
         var nodes = xmlSrc.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {	
			var name=nodes(p).getAttribute("name");
            var nodeName = nodes(p).nodeName;
		
			if ( nodeName =='send' || nodeName=="activity" || nodeName=="choice" || nodeName=="fillVariable" || nodeName=="waitResponse" || nodeName=="decision" || nodeName=="userCallProgram")
			{
                var elem = xmlSrc.createElement("executante");
                elem.setAttribute("name","");
                nodes(p).appendChild( elem );
                 
		    }
		 }
		 version="1.5.1";
         xmlSrc.firstChild.setAttribute("versionXML","1.5.1");
    }
    
    
    var participants=xmlSrc.selectSingleNode("//defParticipants");
    var haveStarter=false;
    var haveAdministrator=false;
    for ( var i=0 ; i < participants.childNodes.length ;i++)
    {
        var name = participants.childNodes(i).getAttribute("name")
        if ( name == "starter" )
        {
            haveStarter = true;
        }
        if ( name == "workFlowAdministrator" )
        {
            haveAdministrator = true;
        }
        
    }
    
    if ( !haveStarter )
    {
        var stXml=elements_getXML( xmlSrc , "NEWDEFPARTICIPANT" );
        stXml.selectSingleNode("label").firstChild.text = "Utilizador que iniciou o programa";
        stXml.setAttribute("name","starter");
        stXml.selectSingleNode("type").text="object.Ebo_Perf";
        participants.appendChild(stXml );
    }
    if ( !haveAdministrator )
    {
        var stXml=elements_getXML( xmlSrc , "NEWDEFPARTICIPANT" );
        stXml.selectSingleNode("label").firstChild.text = "Responsável pelo programa";
        stXml.setAttribute("name","workFlowAdministrator");
        stXml.selectSingleNode("type").text="object.Ebo_Perf";
        participants.appendChild(stXml );
    }
    TREE_EDIT = new xwTree( e , xmlSrc ,true, true, null, "design");

}
	
	
</script>


<body scroll="no" onload="initTree('t2')">
<form id="wForm">
<xml id='xSrc'>
<%=xmlSrc%>
</xml>
<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    
    
    <col id="dd" class='col1' >
	<col class='col2'>
	<col class='col3' />
    
    

    <tr>
        <td valign="top" >
            <style>
                 @import url('ieLibrary/listBar/listBar.css');
                 @import url('ieThemes/0/listBar/ui-listBar.css');
            </style>
            
            <table defaultItem="atributos" class='listBar_Beh listBar ' cellSpacing="0" cellPadding="2"  >
                
                
                 <tr  style='height:24px' >
                    <td class='listBar_header' >Items de Programa</td> 
                    </td>
                 </tr>
                
                 <tr style='height:100%'>
                    <td valign='top' style="padding:4px;">
                         
                         <div id="atributos"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFPARTICIPANT')" src="ieThemes/0/wkfl/defparticipant.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Participante</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_OBJECT')" src="resources/boObject/ico24.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Objecto</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_CHAR')" src="ieThemes/0/wkfl/varchar.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Texto</td>
                                    </tr>
                                     <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_NUMBER')" src="ieThemes/0/wkfl/varnumber.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Número</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_BOOLEAN')" src="ieThemes/0/wkfl/varboolean.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Verdadeiro/Falso</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_DATE')" src="ieThemes/0/wkfl/vardate.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Data</td>
                                    </tr>
                                     <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFVARIABLE_DATETIME')" src="ieThemes/0/wkfl/vardatetime.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Data/Hora</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFMESSAGE')" src="ieThemes/0/wkfl/defmessage.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Mensagem</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('DEFPROCEDURE')" src="ieThemes/0/wkfl/defprocedure.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Procedimento</td>
                                    </tr>
                                    <tr style='height:100%'>
                                        <td class='gcell_std' colspan=2 valign="middle"></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        
                       
                        <div id="tarefaspar"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                                    
                                      <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('Activity')" src="ieThemes/0/wkfl/activity.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Tarefa Simples</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('fillVariable')" src="ieThemes/0/wkfl/fillvariable.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Preencher atributos</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('decision')" src="ieThemes/0/wkfl/decision.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Tomar decisão</td>
                                    </tr>
                                    <tr>
                                       <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('poll')" src="ieThemes/0/wkfl/poll.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Votação</td>
                                    </tr>
                   
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('choice')" src="ieThemes/0/wkfl/choice.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Escolher Opção</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('answer')" src="ieThemes/0/wkfl/answer.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Opção de participante</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('createMessage')" src="ieThemes/0/wkfl/createmessage.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Criar Mensagem</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('send')" src="ieThemes/0/wkfl/send.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Enviar mensagem</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('waitResponse')" src="ieThemes/0/wkfl/waitresponse.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Esperar Resposta</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('userCallProgram')" src="ieThemes/0/wkfl/usercallprogram.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Chamar programa</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('menu')" src="ieThemes/0/wkfl/menu.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Escolher Opção do Menu</td>
                                    </tr>
                                    
                                    <tr style='height:100%'>
                                        <td class='gcell_std' colspan=2 valign="middle"></td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        
                        <div id="tarefassistema"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                                
                                <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('addProcedures')" src="ieThemes/0/wkfl/addprocedures.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Adicionar Procedimentos</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('removeProcedures')" src="ieThemes/0/wkfl/removeprocedures.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Remover Procedimentos</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('removeAllProcedures')" src="ieThemes/0/wkfl/removeallprocedures.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Remover Todos os Procedimentos</td>
                                    </tr>
                                    <tr>
                                        <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('callProcedure')" src="ieThemes/0/wkfl/callprocedure.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Chamar Procedimento</td>
                                    </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('alert')" src="ieThemes/0/wkfl/alert.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Definição de Alerta</td>
                                 </tr>
                                 
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('beginTime')" src="ieThemes/0/wkfl/begintime.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Inicio contagem tempo</td>
                                 </tr>
                                
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('stopTime')" src="ieThemes/0/wkfl/stoptime.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Fim contagem tempo</td>
                                 </tr>
                                
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('waitTime')" src="ieThemes/0/wkfl/waittime.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Espera durante..</td>
                                 </tr>
                                
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('waitThread')" src="ieThemes/0/wkfl/waitthread.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Espera por..</td>
                                 </tr>
                                
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('programlabel')" src="ieThemes/0/wkfl/programlabel.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Rótulo</td>
                                 </tr>
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('milestone')" src="ieThemes/0/wkfl/milestone.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Etapa</td>
                                 </tr>
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('comment')" src="ieThemes/0/wkfl/comment.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Comentário</td>
                                 </tr>
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('copy')" src="ieThemes/0/wkfl/copy.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Copiar atributos</td>
                                 </tr>
                                 <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('xepCode')" src="ieThemes/0/wkfl/xepcode.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Código XEP</td>
                                 </tr>
                                
                                <tr style='height:100%'>
                                    <td class='gcell_std' colspan=2 valign="middle"></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        
                        <div id="controlofluxo"  class='listBar_bodyItem' >
                            <table class='layout' cellpadding=2 cellspacing=0 style='background-color:#FFFFFF' >
                                <colgroup>
                                <col width="30" class='iconsChoose' >
                                <col width="100%">
                                </colgroup>
                                <tbody>
                               
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('callProgram')" src="ieThemes/0/wkfl/callprogram.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Invocar Programa</td>
                                </tr>     
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('Thread')" src="ieThemes/0/wkfl/thread.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Caminho</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('if')" src="ieThemes/0/wkfl/if.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Condição</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('while')" src="ieThemes/0/wkfl/while.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Ciclo</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('foreach')" src="ieThemes/0/wkfl/foreach.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Ciclo por cada ...</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('switch')" src="ieThemes/0/wkfl/switch.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Opções</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('case')" src="ieThemes/0/wkfl/case.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Opção</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('goto')" src="ieThemes/0/wkfl/goto.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Saltar para</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('exit')" src="ieThemes/0/wkfl/exit.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Sair de</td>
                                </tr>
                                <tr>
                                    <td class='gcell_std' valign="middle"><img ondragstart="startDragNew('terminateProgram')" src="ieThemes/0/wkfl/terminateprogram.gif" / WIDTH="24" HEIGHT="24"></td><td class='gcell_std' valign="middle">Fim do Programa</td>
                                </tr>
                                <tr style='height:100%'>
                                    <td class='gcell_std' colspan=2 valign="middle"></td>
                                </tr>
                                </tbody>
                            </table>
                        </div>
                        
                    </td>
                </tr>
                <tr relatedItem='atributos' style='height:25px' >
                    <td class='listBar_item' >Items do Programa</td> 
                    </td>
                </tr>
                <tr relatedItem='tarefaspar' style='height:25px' >
                    <td  class='listBar_item'>Tarefas de Participante</td> 
                    </td>
                </tr>
                
                <tr relatedItem='tarefassistema' style='height:25px' >
                    <td  class='listBar_item'>Tarefas de Sistema</td> 
                    </td>
                </tr>
                
                <tr relatedItem='controlofluxo' style='height:25px' >
                    <td  class='listBar_item'>Controlo Fluxo</td> 
                    </td>
                </tr>
                
            </table>

        
        </td>
		<td splitter width="4" ><img class="rszLeft ui-rszLeft" src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1"></td>
		
		<td valign="top" style="background-color:#FFFFFF">
     
			<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;height:100%;width:100%">
				<tbody>
				
                <style type="text/css">
                    @import url('ieLibrary/menu/menu.css');
                    @import url('ieThemes/0/menu/ui-menu.css');
                </style>
                
                <tr height=24px id="menuUI" class='noprint'>
					<td valign=top  >
                    
                     <span class="mnubar menu" colorOption="BAR">
                        <table  cellSpacing="0" cellPadding="0">
                        
                        <tbody>
                        <tr height="24px">
                            <td tabindex="0" style="padding-left:3px;font-weight:bold" >
                            <nobr>Fluxo de Trabalho</nobr>
                            </td>
                            <td tabindex="0" style='width:100%' >
                            &nbsp;
                           </td>
                           
                           <td tabindex="0" class="mnuOption" onclick="window.print()">
                                <img class="buttonMenu" src="ieThemes/0/wkfl/print.gif" WIDTH="16" HEIGHT="16">Imprimir
                           </td>
                           <td tabindex="0" class="mnuOption" onclick="TREE_EDIT.refreshHTM(true)" >
                                <img class="buttonMenu" src='ieThemes/0/wkfl/refresh.gif'  WIDTH="16" HEIGHT="16">Redesenhar
                           </td>
                           <td tabindex="0" class="mnuOption" onclick="TREE_EDIT.deleteCurrentRow()" >
                                 <img  class="buttonMenu" width="16px" height="16px" title='Arraste um item para cima desta imagem para remover , ou então seleccione a linha e clique aqui' ondragenter="dragEnterTrash()" ondragLeave="dragLeaveTrash()"  ondragover="dragOverTrash()" ondrop="dropTrash()" src='ieThemes/0/wkfl/recyclebin.gif'>Apagar
                           </td>
                           
                           <td tabindex="0" style='width:15px'>
                              &nbsp;
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
						<div class='container'>
				
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
				
				<tr  height=4px class='noprint' id="trRizeBottom" >
					<td valign=top  ><img class="rszUp ui-rszUp" hspace=0 vspace=0 src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1px" /></td>
				</tr>
				
				<tr >
					<td  valign="top" style="">
               
					<div  id="workCell" class='container noprint'>
					
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
                                    unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
                                }
        var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
        var elem = xmlQuery.createElement("xwfProgramDef__flow");
        elem.setAttribute("isXML","false");
        elem.appendChild( xmlQuery.createTextNode(xmlSrc.documentElement.xml) );
        xmlQuery.appendChild( elem );
                                
//        var xmlQuery=""; //"<bo boui=\""+boFormSubmit.programBoui.value+"\" >";
//        xmlQuery+= "<xwfProgramDef__flow isXML=\"true\" ><![CDATA[";    
             //debugger;
        //     
//        xmlQuery+=encodeURIComponent(xmlSrc.xml);
       //x='';
        //for(var i=0;i<10000;i++) x+='aaaaaa';
        
        //xmlQuery+=xmlSrc.xml;
        //<![CDATA[9]]>
     //   xmlQuery+=x;
//        xmlQuery+="]]></xwfProgramDef__flow>";
        //xmlQuery+="</bo>";
        
       // alert(xmlQuery);
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
<FORM name='boFormSubmit' action="" method='put'>
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
    if(initPage){if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
