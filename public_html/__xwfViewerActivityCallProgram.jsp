<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.*"%>



<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
 boolean masterdoc=false;
 if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
 }
 boSession bosession = (boSession)request.getSession().getAttribute("boSession");
 if(bosession== null)
 {
    response.sendRedirect("login.jsp");
    return; 
 }
 if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
 }
 int IDX;int cvui;
 boDefHandler bodef;
 boDefAttribute atr;
 String idbolist;
 String[] ctrl;

 Hashtable xattributes;
 request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");
 ctrl= DOCLIST.processRequest(boctx);
 IDX= ClassUtils.convertToInt(ctrl[0],-1);
 idbolist=ctrl[1];
 docHTML DOC = DOCLIST.getDOC(IDX);
 boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
                
    XwfController controller = (XwfController)DOC.getController();
    boObject activity = controller.getRuntimeActivity();
    boObject runtimeProgram = controller.getRuntimeProgram();
//    boObject callProgram = activity.getAttribute("callProgram").getObject();
    String xmlStep = activity.getAttribute("xmlStep").getValueString();
    String filter = activity.getAttribute("filter").getValueString();
    String mode = activity.getAttribute("mode").getValueString();
    
//    long callProgramBoui = -1;
//    if(callProgram != null)
//    {
//        callProgramBoui = callProgram.getBoui();
//    }
    String sid = activity.getAttribute("sid").getValueString();
    bodef = boDefHandler.getBoDefinition("xwfUserCallProgram");
    
         
%>
<html>
<head>
<script LANGUAGE="javascript" SRC="ieLibrary/form/jsObjects.js"></script>
<script LANGUAGE="javascript">

function init(mode)
{                
    xmlSrc = new ActiveXObject("Microsoft.XMLDOM");
    xmlSrc.async	= false;    
    if(xmlStep.firstChild == null)
    {
        xmlStep.outerHTML = "<xml id=xmlStep><callProgram async='false' name='' programBoui='' mode='"+mode+"'><mappingVariables></mappingVariables><mappingParticipants></mappingParticipants><code/></callProgram></xml>";    
    }        
    xmlSrc.loadXML(xmlStep.innerHTML);
}
function elements_showCALLPROGRAM(sid,boqlFilter)
{
    
	var htm=[];
	var i=0;	
    var mode = xmlSrc.firstChild.getAttribute("mode");
	var programBoui = xmlSrc.firstChild.getAttribute("programBoui");
    
	var programHTML = createDetachFieldLookup(
        programBoui,
        "programBoui_"+sid,
        "programBoui_"+sid,
        "xwfProgram",
        "xwfProgram",
        "Programa",
        getDocId(), //docid
        "single", //single or multi
        1,
        false, //isdisable
        true,
        "newCallProgram(\""+sid+"\")",
        boqlFilter
        )
        

	var modeInt=["embedded","outsource"];
	var modeExt=["Embebido","Isolado"];
    var modeHTML=createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", modeExt , modeInt ,false,false,"" );

        
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"

	
    
	htm[i++]="<tr><td>"
	htm[i++]="Programa";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= programHTML;
	htm[i++]="</td></tr>";
		

	htm[i++]="<tr><td>"
	htm[i++]="Modo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= modeHTML;
	htm[i++]="</td></tr>";

	htm[i++]="<tr><td>"
	htm[i++]="</td>";
    htm[i++]="&nbsp;";    
	htm[i++]="<td>";
    htm[i++]="&nbsp;";
	htm[i++]="</td></tr>";    

	
	htm[i++]="<tr id='mapping' height=250px><td colspan=2 id='setPar' >"
	htm[i++]= "";
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr height=100%><td>&nbsp;"
	htm[i++]="</td></tr>";

	htm[i++]="</table>";
    
	xForm.innerHTML= htm.join("");
			
	
}

function newCallProgram(sid)
{
 
	var programBoui = getValue("programBoui_"+sid);
	var ifrm=[];
	var ii=0;
	<!--boFormSubmit.callProgram.value = programName;-->
	ifrm[ii++]="<iframe id='frameMapping' scrolling=no frameborder=0 style='height:100%;width:100%' src='";
	ifrm[ii++]="__xwfViewerActivityCallProgramMapAttr.jsp?callProgramBoui=";
	ifrm[ii++]=programBoui;
	ifrm[ii++]="&docid=";
	ifrm[ii++]=getDocId();
	ifrm[ii++]="&sid="
	ifrm[ii++]=sid;
	ifrm[ii++]="&controllerName=XwfController"	
	ifrm[ii++]="&runtimeProgramBoui=<%=runtimeProgram.getBoui()%>"	
	ifrm[ii++]="'";
	ifrm[ii++]=" ></iframe>";
	document.getElementById("setPar").innerHTML=ifrm.join("");
	
}
function getValue( oid )
{
	var iLen = xForm.elements.length;
	for (i = 0; i < iLen; i++)
	{
		o = xForm.elements[i];
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

function readFromHTML_CALLPROGRAM(sid)
{
    var programBoui = getValue("programBoui_"+sid);
    xmlSrc.firstChild.setAttribute("programBoui", programBoui);
    var mode = getValue("mode_" + sid);
    xmlSrc.firstChild.setAttribute("mode", mode);
    
	var frm = document.getElementById("frameMapping");
	if ( frm && frm.contentWindow.document &&frm.contentWindow.document.readyState=="complete")
	{
		 var d=frm.contentWindow.document;
		 var w=frm.contentWindow;
		 var mapVarRows = d.getElementById("mapVar").rows;
		 var mapParRows = d.getElementById("mapPar").rows;
		 
		 var mapVars=xmlSrc.firstChild.selectSingleNode("mappingVariables");
		 while ( mapVars.childNodes.length > 0 )
		 {
			mapVars.removeChild( mapVars.firstChild );
		 }
		 for ( var i=1 ; i< mapVarRows.length ; i++ )
		 {
			var o_var = w.getValue("variable_"+(i-1));	
			var hlink = w.getValue("hlink_"+(i-1));	
			if (o_var && o_var.length > 0 )
			{
				var mapVar = xmlSrc.createElement("mappingVariable");
				mapVar.setAttribute("mainVar",o_var);
				mapVar.setAttribute("progVar", w.d_varInt[i-1] );
				mapVar.setAttribute("hardLink",hlink);
				mapVars.appendChild( mapVar );
			}
		 }
		 
		 var mapPars=xmlSrc.firstChild.selectSingleNode("mappingParticipants");
		 while ( mapPars.childNodes.length > 0 )
		 {
			mapPars.removeChild( mapPars.firstChild );
		 }
		 for ( var i=1 ; i< mapParRows.length ; i++ )
		 {
			var o_par = w.getValue("participant_"+(i-1));	
			
			if (o_par&& o_par.length > 0 )
			{
				var mapPar = xmlSrc.createElement("mappingParticipant");
				mapPar.setAttribute("mainPar",o_par);
				mapPar.setAttribute("progPar", w.d_partInt[i-1] );
				mapPars.appendChild( mapPar );
			}
		 }
		 
		 
		 
		 
	}	
}
</script>
</head>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()"   onload="init('<%=mode%>');elements_showCALLPROGRAM('<%=sid%>','<%=filter%>');" >
<form  id="xForm"  > 
    <div id='workCell' style='height:100%;width:100%;padding:0px;'>
    </div>
</form>
<xml id='xmlStep'>
<%=xmlStep%>
</xml>
<script language="jscript">
function BuildXml()
{           
    try 
    {        
        var xmlQuery="";            

        var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
        var elem = xmlQuery.createElement("xwfUserCallProgram__xmlStep");
        elem.setAttribute("isXML","false");
        readFromHTML_CALLPROGRAM('<%=sid%>');
        elem.appendChild( xmlQuery.createTextNode(xmlSrc.documentElement.xml) );
        xmlQuery.appendChild( elem );

//        xmlQuery+= "<xwfUserCallProgram__xmlStep isXML=\"true\" ><![CDATA[";
//        readFromHTML_CALLPROGRAM('<%=sid%>');
//        xmlQuery+=encodeURIComponent(xmlSrc.xml);        
//        xmlQuery+="]]></xwfUserCallProgram__xmlStep>";
 
        return xmlQuery.documentElement.xml; 
    }
    catch(e)
    {
        alert(e.message);
        return "";
    }
}
</script>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='<%= ControllerFactory.CONTROLLER_NAME_KEY %>'  value='<%= controller.getName() %>'/>
    <INPUT type='hidden' name='<%= XwfKeys.PROGRAM_RUNTIME_BOUI_KEY %>'  value='<%= controller.getRuntimeProgramBoui() %>'/>
    <INPUT type='hidden' name='<%= XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY %>' value='<%= controller.getRuntimeActivityBoui() %>'/>
    <!--<INPUT type='hidden' name='callProgram' />-->
    <INPUT type='hidden' name='boFormSubmitXml' />        
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='edit' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='boiChanged' value='true' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM> 
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
