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
    
    
    
    
    String sid=request.getParameter("sid");
    String object = request.getParameter("object");
    String requiredMethods =request.getParameter("rMethods"); 
    String hiddenMethods   =request.getParameter("hMethods");
    String availableMethods   =request.getParameter("aMethods");
    
    
    boDefHandler bodefObject = null; 
    
    if ( object != null && object.length() > 0 )
    {
        bodefObject = boDefHandler.getBoDefinition( object );
    }
    boDefMethod[] methods = new boDefMethod[0]; 
    if ( bodefObject != null )
    {
        methods = bodefObject.getBoMethods();   
        methods[0].getLabel();
    }   
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    idbolist=ctrl[1];
    
           
%>
<% if( true ){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>WorkFlow Designer - Helper for CallProgram</title>

<style>
 @import url('ieThemes/0/global/ui-global.css');
 
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 
 
 </style>

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
 
function init()
{
  var objectMethods = [];
  var objectMethodsLabel = [];
  <%
    for ( int i = 0 ; i< methods.length ; i++ )
    { if( !methods[i].getIsNative() ) 
    {%>objectMethods[<%=i%>]="<%=methods[i].getName()%>";
       objectMethodsLabel[<%=i%>]="<%=methods[i].getLabel()%>";
    <%}}%>
  
  var rMethods = "<%=requiredMethods%>".split(";");
  var hMethods = "<%=hiddenMethods%>".split(";");
  var aMethods = "<%=availableMethods%>".split(";");
  
  var htm =[];
  var j=0;
  var rmh = document.getElementById("rMethods");
  for ( var i=0 ; i < objectMethods.length ; i++ )
  {
    var r = rmh.insertRow();
    r.method=objectMethods[i];
    var c = r.insertCell();
    var isChecked ="";
    for( var j=0 ; j<rMethods.length && isChecked ==""; j++ )
    {
        if( rMethods[j]==objectMethods[i] )
        {
            isChecked ="checked";
        }
    }
    c.innerHTML ="<input type='checkbox' class='rad' "+isChecked+" />";
    var c = r.insertCell();
    c.innerHTML = "<span>"+objectMethodsLabel[i]+"</span>";
  }
  
  var rmh = document.getElementById("aMethods");
  for ( var i=0 ; i < objectMethods.length ; i++ )
  {
    var r = rmh.insertRow();
    r.method=objectMethods[i];
    var c = r.insertCell();
    var isChecked ="";
    for( var j=0 ; j<aMethods.length && isChecked ==""; j++ )
    {
        if( aMethods[j]==objectMethods[i] )
        {
            isChecked ="checked";
        }
    }
    c.innerHTML ="<input type='checkbox' class='rad' "+isChecked+" />";
    var c = r.insertCell();
    c.innerHTML = "<span>"+objectMethodsLabel[i]+"</span>";
  }
  
  var rmh = document.getElementById("hMethods");
  for ( var i=0 ; i < objectMethods.length ; i++ )
  {
    var r = rmh.insertRow();
    r.method=objectMethods[i]; 
    var c = r.insertCell();
    var isChecked ="";
    for( var j=0 ; j<hMethods.length && isChecked ==""; j++ )
    {
        if( hMethods[j]==objectMethods[i] )
        {
            isChecked ="checked";
        }
    }
    c.innerHTML ="<input type='checkbox' class='rad' "+isChecked+" />";
    var c = r.insertCell();
    c.innerHTML = "<span>"+objectMethodsLabel[i]+"</span>";
  }
  
  parent.document.getElementById("methods").style.height=container.offsetHeight+10;
 
}

</script>


<body scroll="no" onload="init()">
<form id="wForm">
<div id='container' style='height:100%;width:100%;padding:0px;'>

<div style='font-weight:bold'>Métodos obrigatórios</div>
<table id='rMethods' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;color:#000000;width:100%">
    <col width=30px/>
    <col width=100%/>
</table>

<div style='font-weight:bold'>Métodos Escondidos</div>
<table id='hMethods' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=30px/>
    <col width=100%/>
</table>

<div style='font-weight:bold'>Métodos disponíveis</div>
<table id='aMethods' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;width:100%">
    <col width=30px/>
    <col width=100%/>
</table>


</form>
</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
