<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="netgest.bo.localized.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
int IDX;int cvui;
String message = null;
boDefHandler bodef;
boDefAttribute atr;
String idbolist;
String[] ctrl;
ctrl= DOCLIST.processRequest(boctx);
IDX     = ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
String myIDX                = request.getParameter("myIDX");
String s = request.getParameter( "operation" );
if(s != null)
{
    if("explorer".equalsIgnoreCase(s))
    {
        ConfigUtils.setDefaultExplorer(bosession.getPerformerBoui());
        message = JSPMessages.getString("defaultConfig.1");
    }
    else if("favorites".equalsIgnoreCase(s))
    {
        ConfigUtils.cleanFavorites(bosession.getPerformerBoui());
        message = JSPMessages.getString("defaultConfig.2");
    }
}
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Inserir Alias</title>
<style type="text/css">
    @import url('xeo.css');
</style>
</head>
<script LANGUAGE="javascript" SRC="xeo.js"></script>
<%@ include file='boheaders.jsp'%>
<script>
document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
isIE=true;
var G_onrsz=false;
var G_onmv=false;
var G_1;
var G_2;
var doc;


var _hsos=[];
var _hso=null;
var ii=0;
var lastActive=new Date('01/01/1970');
function activethis(){
 if( new Date() - lastActive > 1000)
 {
    var xwin=winmain();
    if(xwin){
        xwin.status="ok..."+(ii++);
        if(xwin.activeDocByIdx) xwin.activeDocByIdx(getIDX())
    }
  }
  lastActive=new Date();
}

document.onmousedown=activethis;

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
var objLabel='<%=JSPMessages.getString("defaultConfig.3")%>';
</script>
<% if(message !=null) {%>
    <body ondragenter="activethis()" onload="winmain().activeDocByIdx(<%=myIDX%>);noWait();alert('<%= message%>');">
    <% }else{%>
    <body ondragenter="activethis()" onload="winmain().activeDocByIdx(<%=myIDX%>);noWait();">
    <% }%>
<form>
<input type='hidden' id="operation" name="operation" value='explorer'></input>
  
                                 
                                 <TABLE class='section' style="" cellSpacing='0' cellPadding='3' height = '100%'>
                                    <COLGROUP/>
                                    <COL width='60' />
                                    <COL width='400'/>
                                    <COL />
                                    <TBODY>
                                        <TR height='20px'>
                                        </TR>
                                        <TR height='0'>
                                            <TD colspan='4'><span style='width:100%;border-bottom:1px solid #00000;font:14px'><b><%=JSPMessages.getString("defaultConfig.3")%></b></span></TD>
                                        </TR>
                                        
                                        <TR>
                                            <TD>&nbsp;</TD>
                                            <TD><font style="font:14px"><input type="radio" name="check1" checked onclick="operation.value='explorer';" >&nbsp;<%=JSPMessages.getString("defaultConfig.4")%></input></font></TD>
                                            <TD>&nbsp;</TD>
                                        </TR>
                                        <TR>
                                            <TD>&nbsp;</TD>
                                            <TD><font style="font:14px"><input type="radio" name="check1" onclick="operation.value='favorites';" >&nbsp;<%=JSPMessages.getString("defaultConfig.5")%></input></font></TD>
                                            <TD>&nbsp;</TD>
                                        </TR>
                                        <TR>
                                            <TD>&nbsp;</TD>
                                            <TD><input type="submit" value="&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<%=JSPMessages.getString("defaultConfig.6")%>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" onclick="wait();" ></input></TD>
                                            <TD>&nbsp;</TD>
                                        </TR>
                                        <TR height = '100%'>
                                            <TD>&nbsp;</TD>
                                            <TD>&nbsp;</TD>
                                            <TD>&nbsp;</TD>
                                        </TR>
                                    </TBODY>
                                </TABLE>

</form>
</body></html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

