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
    String sid=request.getParameter("sid");
    
    long programBoui= netgest.utils.ClassUtils.convertToLong(pboui,-1);
    
        
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    idbolist=ctrl[1];
    boObject program = DOC.getObject( programBoui );
    boObject prg = program.getBridge("versions").getObject();
    String xmlSrc = prg.getAttribute("flow").getValueString();
    
    if ( xmlSrc==null || xmlSrc.length() ==0)
    {
        xmlSrc=" <program  name=\"Sem nome\">";
        xmlSrc+="<executewhen><![CDATA[]]></executewhen>";
        xmlSrc+="<code>";
        xmlSrc+="<defVariables>";
        xmlSrc+="</defVariables>";
        xmlSrc+="<defParticipants>";
        xmlSrc+="</defParticipants>";
        xmlSrc+="</code>";
        xmlSrc+="</program>";
    }
   
    String attributes;
   
    
           
%>
<% if( programBoui != -1222 ){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>WorkFlow Designer - Helper for CallProgram</title>

<style type="text/css">
    @import url('xeoNew.css');
 </style>

<style>
 @import url('ieThemes/0/global/ui-global.css');
 
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 
 
 </style>
 
 <script LANGUAGE="javascript" SRC="ieLibrary/form/jsObjects.js"></script>

</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

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



document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}

var TREE_EDIT=null;	
var xmlSrc=null;

function changeAtr()
{

}

o_partExt=[];
o_partInt=[];

o_varExt=[];
o_varInt=[];

  
function init()
{

  xmlSrc = new ActiveXObject("Microsoft.XMLDOM");
  xmlSrc.async	= false;
  xmlSrc.loadXML(xSrc.xml);
 
  var search="//*[@sid='<%=sid%>']"; //NTRIMJS
  var node=parent.xmlSrc.firstChild.selectSingleNode(search);
  
  
 
  var o_participants=parent.xmlSrc.selectSingleNode("//defParticipants");
  
  for ( var p=0 ; p < o_participants.childNodes.length ; p++ )
  {
		if ( p == 0 )
        {
            o_partInt[ p ]="";
            o_partExt[ p ]="&nbsp;"; 
        }
        o_partInt[ o_partInt.length ] = o_participants.childNodes(p).getAttribute("name");
		o_partExt[ o_partExt.length ] = o_participants.childNodes(p).selectSingleNode('label').text;
  }
  
  
  d_partExt=[];
  d_partInt=[];
  var d_participants=xmlSrc.selectSingleNode("//defParticipants");
  for ( var p=0 ; p < d_participants.childNodes.length ; p++ )
  {
        
		d_partInt[ p ] = d_participants.childNodes(p).getAttribute("name");
		d_partExt[ p ] = d_participants.childNodes(p).selectSingleNode('label').text;
  }
  
  var tbl = document.getElementById("mapPar");
  for ( var i=0 ; i < d_partInt.length ; i++ )
  {
     var row = tbl.insertRow();
     
     var c = row.insertCell();
     
     var mapPar = node.selectSingleNode("mappingParticipants");
     var o_participant="";
     for ( var j = 0 ; j< mapPar.childNodes.length;j++ )
     {
        var mainPar = mapPar.childNodes(j).getAttribute("mainPar");
        var progPar = mapPar.childNodes(j).getAttribute("progPar");
        if ( progPar == d_partInt[i] )
        {
            o_participant = mainPar;
        }
     }
     c.className='gCell_std';
     if ( o_partExt.length > 0 )
     {
         var parHTML=createFieldCombo(o_participant,"participant_"+i,"participant_"+i,"1", o_partExt , o_partInt ,false,false,"changeAtr" );
     }
     else
     {
         var parHTML ="<b>Não existem participantes</b>";
     }
     c.innerHTML = parHTML
     
     var c = row.insertCell(); //spacer
     
     var c = row.insertCell();
     row.id = d_partInt[i];
     
     c.innerHTML = "<img align='middle' src='ieThemes/0/wkfl/into.gif'><span style='margin:2px;padding:2px'>"+d_partExt[i]+"</span>";
     c.className='gCell_std';
        
     
     var c = row.insertCell();
     c.innerHTML='&nbsp';
     c.className='gCell_std';
  }
  
// MAPPING DE VARIAVEIS 
  var o_variables=parent.xmlSrc.selectSingleNode("//defVariables");
  
  for ( var p=0 ; p < o_variables.childNodes.length ; p++ )
  {
		if ( p == 0 )
        {
            o_varInt[ p ]="";
            o_varExt[ p ]="&nbsp;"; 
        }
        o_varInt[ o_varInt.length ] = o_variables.childNodes(p).getAttribute("name");
		o_varExt[ o_varExt.length ] = o_variables.childNodes(p).selectSingleNode('label').text;
  }
  
  
  d_varExt=[];
  d_varInt=[];
  var d_variables=xmlSrc.selectSingleNode("//defVariables");
  for ( var p=0 ; p < d_variables.childNodes.length ; p++ )
  {
        
		d_varInt[ p ] = d_variables.childNodes(p).getAttribute("name");
		d_varExt[ p ] = d_variables.childNodes(p).selectSingleNode('label').text;
  }
  
  var tbl = document.getElementById("mapVar");
  for ( var i=0 ; i < d_varInt.length ; i++ )
  {
     var row = tbl.insertRow();
     
     var c = row.insertCell();
     
     var mapVar = node.selectSingleNode("mappingVariables");
     var o_variable="";
     var o_hardLink="";
     for ( var j = 0 ; j< mapVar.childNodes.length;j++ )
     {
        var mainVar = mapVar.childNodes(j).getAttribute("mainVar");
        var progVar = mapVar.childNodes(j).getAttribute("progVar");
        var hardLink = mapVar.childNodes(j).getAttribute("hardLink");
        if ( progVar == d_varInt[i] )
        {
            o_variable = mainVar;
            o_hardLink = hardLink;
        }
     }
     c.className='gCell_std';
     if ( o_varExt.length > 0 )
     {
         var varHTML=createFieldCombo(o_variable,"variable_"+i,"variable_"+i,"1", o_varExt , o_varInt ,false,false,"changeAtr" );
     }
     else
     {
         var varHTML ="Não existem atributos";
     }
     c.innerHTML = varHTML
     
     var c = row.insertCell(); //spacer
     
     var c = row.insertCell();
     row.id = d_varInt[i];
     c.innerHTML = "<img align='middle' src='ieThemes/0/wkfl/into.gif'><span style='margin:2px;padding:2px'>"+d_varExt[i]+"</span>";
     c.className='gCell_std';
     
     if ( o_varExt.length > 0 )
     {
        var hlinkInt=["","Y","N"];
        var hlinkExt=["","Sim","Não"];
        var hlinkHTML=createFieldCombo(o_hardLink,"hlink_"+i,"hlink_"+i,"1", hlinkExt , hlinkInt ,false,false,"changeAtr" );
	 }
     else
     {
        var hlinkHTML='&nbsp;';
     }
     var c = row.insertCell();
     c.innerHTML=hlinkHTML;
     c.className='gCell_std';
  }
 
  parent.document.getElementById("mapping").style.height=container.offsetHeight+10;
 
}
	
function getValue( oid )
{
	var iLen = wForm.elements.length;
	for (i = 0; i < iLen; i++)
	{
		o = wForm.elements[i];
		if ( o.name==oid )
		{
			if (o.type == "text" || o.type == "textarea")
			{
				o.value = Trim(o.value);
			}
			if( o.returnValue) return o.returnValue;
			else return o.value;
		}
	}
	
}

</script>


<body scroll="no" onload="init()">
<form id="wForm">
<xml id='xSrc'>
<%=xmlSrc%>
</xml>
<div id='container' style='height:100%;width:100%;padding:0px;'>


<div style='font-weight:bold'>Mapeamento de Atributos</div>
<table id='mapVar' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=40%>
    <col width=2px>
    <col width=40%>
    <col width=20%>
    <tr height='20px'>
        <td class='gh_Std'>Atributos deste Programa</td><td class='gh_Sep'></td><td class='gh_Std'>Variáveis do programa a chamar</td><td class='gh_Std'>Hard Link?</td>
    </tr>
    
   </tbody>
 </table>


<div style='font-weight:bold'>Mapeamento de Participantes</div>
<table id='mapPar' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=40%>
    <col width=2px>
    <col width=40%>
    <col width=20%>
    <tr height='20px'>
        <td class='gh_Std'>Participante deste Programa</td><td class='gh_Sep'></td><td class='gh_Std'>Participantes do programa a chamar</td><td class='gh_Std'>&nbsp;</td>
    </tr>
    
   </tbody>
 </table>



</div>
</form>
</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
