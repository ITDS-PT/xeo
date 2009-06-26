<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import=" netgest.bo.presentation.*"%>
<%@ page import=" netgest.xwf.common.xwfHelper"%>


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
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    XwfController controller = (XwfController)DOC.getController();
            
    
    idbolist=ctrl[1];
    String callProgramBouiStr = request.getParameter("callProgramBoui"); 
    long callProgramBoui= netgest.utils.ClassUtils.convertToLong(callProgramBouiStr,-1);
    String runtimeSID = null;
    String xmlSrc = null;
    String xmlSrcOriginal = null;
    String partIdXml = null;
    String varsIdXml = null;
    if(callProgramBoui != -1)
    {
        runtimeSID = controller.getRuntimeActivity().getAttribute("sid").getValueString();    
        boObject runtimeProgram = controller.getRuntimeProgram();
//        xmlSrcOriginal = runtimeProgram.getAttribute("programDef").getObject().getAttribute("flow").getValueString();
        xmlSrcOriginal = runtimeProgram.getAttribute("flow").getValueString();
        
        Hashtable partIdentification = xwfHelper.getObjectIdentification(controller.getEngine(),xwfHelper.getParticipantsBouis(xmlSrcOriginal));
        Hashtable varsIdentification = xwfHelper.getObjectIdentification(controller.getEngine(),xwfHelper.getVariableBouis(xmlSrcOriginal));
        partIdXml = xwfHelper.toXML(partIdentification);
        varsIdXml = xwfHelper.toXML(varsIdentification);
        boObject program = DOC.getObject( callProgramBoui );        
        boObject prg = program.getBridge("versions").getObject();
        xmlSrc = prg.getAttribute("flow").getValueString();
    
        
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
    }   
    String attributes;
   
    
           
%>
<% if( callProgramBoui != -1 ){%>
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
o_varType=[];
  
function init()
{  
  var xmlOriginalRun = xSrcRuntime;
  xmlSrc = new ActiveXObject("Microsoft.XMLDOM");
  xmlSrc.async	= false;
  xmlSrc.loadXML(xSrc.xml);
      

  var o_participants = xmlOriginalRun.selectSingleNode("//defParticipants");
  
  for ( var p=0 ; p < o_participants.childNodes.length ; p++ )
  {
		if ( p == 0 )
        {
            o_partInt[ p ]="";
            o_partExt[ p ]="&nbsp;"; 
        }
        
        var name = o_participants.childNodes(p).getAttribute("name");
        if(name != "<%= xwfHelper.PART_STARTER_NAME%>" && name != "<%= xwfHelper.PART_WFADMINISTRATOR_NAME%>")        
        {
            o_partInt[ o_partInt.length ] = name;
            
            var o_partId = partIdXml.selectSingleNode("//" + name);
            var label = o_participants.childNodes(p).selectSingleNode('label').text;
            
            if(o_partId != null)
            {          
                label += " <a style='COLOR:#0000ff'>["+ o_partId.text + "]</a>";
            }
            o_partExt[ o_partExt.length ] = label;
        }
  }
  
  
  d_partExt=[];
  d_partInt=[];
  var d_participants=xmlSrc.selectSingleNode("//defParticipants");
  for ( var p=0 ; p < d_participants.childNodes.length ; p++ )
  {
        var name = d_participants.childNodes(p).getAttribute("name");
        if(name != "<%= xwfHelper.PART_STARTER_NAME%>" && name != "<%= xwfHelper.PART_WFADMINISTRATOR_NAME%>")        
        {        
            d_partInt[ d_partInt.length ] = name;
            d_partExt[ d_partExt.length ] = d_participants.childNodes(p).selectSingleNode('label').text;
        }        
  }
  
  var tbl = document.getElementById("mapPar");
  
  for ( var i=0 ; i < d_partInt.length ; i++ )
  {
     var row = tbl.insertRow();     
     var c = row.insertCell(); 
     
     row.id = d_partInt[i];     
     c.innerHTML = "<span style='margin:2px;padding:2px'>"+d_partExt[i]+"</span>";     
     c.className='gCell_std';
                   
     var c = row.insertCell(); //spacer     
     var c = row.insertCell();         
     
     var mapPar = parent.xmlSrc.firstChild.selectSingleNode("mappingParticipants");
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
     

  }
  
// MAPPING DE VARIAVEIS 
  var o_variables=xmlOriginalRun.selectSingleNode("//defVariables");
  for ( var p=0 ; p < o_variables.childNodes.length ; p++ )
  {
		if ( p == 0 )
        {
            o_varInt[ p ]="";
            o_varExt[ p ]="&nbsp;";
            o_varType[ p ]="";
        }
        var name = o_variables.childNodes(p).getAttribute("name");
        o_varInt[ o_varInt.length ] = name;
        
        var o_varsId = varsIdXml.selectSingleNode("//" + name);
        var label = o_variables.childNodes(p).selectSingleNode('label').text;
        if(o_varsId != null)
        {        
            label += " <a style='COLOR:#0000ff'>["+ o_varsId.text + "]</a>";		
        }
		o_varExt[ o_varExt.length ] = label;
        o_varType[o_varType.length] = o_variables.childNodes(p).selectSingleNode('type').text;
  }
  
  
  d_varExt=[];
  d_varInt=[];
  d_varType=[];
  var d_variables=xmlSrc.selectSingleNode("//defVariables");
  for ( var p=0 ; p < d_variables.childNodes.length ; p++ )
  {
        
		d_varInt[ p ] = d_variables.childNodes(p).getAttribute("name");
		d_varExt[ p ] = d_variables.childNodes(p).selectSingleNode('label').text;
        d_varType[ p ] =d_variables.childNodes(p).selectSingleNode('type').text; 
  }
  
  var tbl = document.getElementById("mapVar");
  for ( var i=0 ; i < d_varInt.length ; i++ )
  {
     var row = tbl.insertRow();
     
     var c = row.insertCell();

     
     row.id = d_varInt[i];
     c.innerHTML = "<span style='margin:2px;padding:2px'>"+d_varExt[i]+"</span>";
     c.className='gCell_std';
     
     
     var c = row.insertCell(); //spacer
     var c = row.insertCell();
     
     var mapVar = parent.xmlSrc.firstChild.selectSingleNode("mappingVariables");
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
         var defLov = getLovByType(d_varType[i],o_varInt,o_varExt,o_varType);
         if(mainVar == undefined)
         {            
            o_variable = defLov[1][0];
         }
         var varHTML=createFieldCombo(o_variable,"variable_"+i,"variable_"+i,"1", defLov[0] , defLov[1] ,false,false,"changeAtr" );
     }
     else
     {
         var varHTML ="Não existem atributos";
     }
     c.innerHTML = varHTML
     
     
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
function getLovByType(type,internal,external,listType)
{
    var internalType;
    defLov=[2];
    varInt=[];
    varExt=[];    
    for ( var i=0 ; i < internal.length ; i++ )
    {
        internalType = listType[i];
        if(internalType == type)
        {
            varInt[varInt.length] = internal[i];
            varExt[varExt.length] = external[i];
        }
        else if(internalType.indexOf("char") != -1 && type.indexOf("char") != -1)
        {
            varInt[varInt.length] = internal[i];
            varExt[varExt.length] = external[i];        
        }      
    }

    varInt[varInt.length] = "";
    varExt[varExt.length] = "&nbsp;";    

    defLov[0] = varExt;
    defLov[1] = varInt;
    return defLov;
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
<xml id='xSrcRuntime'>
<%=xmlSrcOriginal%>
</xml>

<xml id='partIdXml'>
<%=partIdXml%>
</xml>

<xml id='varsIdXml'>
<%=varsIdXml%>
</xml>

<div id='container' style='height:100%;width:100%;padding:0px;'>


<div style='font-weight:bold'>Mapeamento de Atributos</div>
<table id='mapVar' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=40%>
    <col width=2px>
    <col width=40%>
    <col width=20%>
    <tr height='20px'>
        <td class='gh_Std'>Variáveis do programa a chamar</td><td class='gh_Sep'></td><td class='gh_Std'>Atributos deste Programa</td><td class='gh_Std'>Hard Link?</td>
    </tr>
    
   </tbody>
 </table>


<div style='font-weight:bold'>Mapeamento de Participantes</div>
<table id='mapPar' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=40%>
    <col width=2px>
    <col width=60%>    
    <tr height='20px'>
        <td class='gh_Std'>Participantes do programa a chamar</td><td class='gh_Sep'></td><td class='gh_Std'>Participante deste Programa</td>
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
