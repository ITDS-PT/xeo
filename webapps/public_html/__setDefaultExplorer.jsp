<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

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
if(request.getParameter("userName") != null || request.getParameter("explorerName") != null)
{
    boObject performer = null;
    Explorer explorer = null;
    String userName = null, explorerName = null;
    boolean setUser = false, setExplorer = false;
    
    try
    {
        userName = request.getParameter("userName");
        explorerName = request.getParameter("explorerName");
        if(userName != null && !"".equals(userName))
        {
            boObjectList l = boObjectList.list(boctx, 
                                "Select iXEOUser where username = '" + userName + "'");
            l.beforeFirst();
            if(l.next())
            {
                performer = l.getObject();
            }
            else
            {
                message = "Utilizador inexistente!";
            }
            setUser = true;
        }
         if(explorerName != null && !"".equals(explorerName))
        {
            explorer = ExplorerServer.getExplorer(explorerName);
            if(explorer == null) message = "Explorador inexistente!";;
            setExplorer = true;
        }
        
        if(message == null)
        {
            if(setExplorer && setUser)
            {
                ExplorerServer.setDefaultExplorer(explorerName, performer.getBoui());
            }
            else if(setExplorer)
            {
                ExplorerServer.setDefaultExplorer(explorerName);
            }
            else
            {
                ExplorerServer.setDefaultExplorer(performer.getBoui());
            }
            message = "Operação efectuada com sucesso!";
        }
        
    }
    catch(Exception e)
    {
        message = e.getMessage();
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
var objLabel='Definir Configurações de iniciais de exploradores ';
</script>
<% if(message !=null) {%>
    <body ondragenter="activethis()" onload="try{winmain().activeDocByIdx(<%=myIDX%>);}catch(e){}noWait();alert('<%= message%>');">
    <% }else{%>
    <body ondragenter="activethis()" onload="try{winmain().activeDocByIdx(<%=myIDX%>);}catch(e){}noWait();">
    <% }%>
<form>
  
                                 
                                 <TABLE class='section' style="" cellSpacing='0' cellPadding='3'  >
                                    <COLGROUP/>
                                    <COL width='320' />
                                    <COL />
                                    <TBODY>
                                        <TR height='0'>
                                            <TD colspan='4'><span style='width:100%;border-bottom:1px solid #00000;font:14px'><b>Transferir Movimentos</b></span></TD>
                                        </TR>
                                        <tr>
                                            <TD><label class=req>Username </label></TD>
                                            <td colspan='3'>
                                                <input class='text' returnValue=<%=request.getParameter("userName") == null ? "\"\"":"\"" + request.getParameter("userName") + "\""%> value=<%=request.getParameter("userName") == null ? "\"\"":"\"" + request.getParameter("userName") + "\""%> id='userName' onfocus='setFieldWFocus("userName")'  maxlength="500" name = 'userName' tabindex='22'>
                                            </td>
                                        </tr>
                                        <tr>
                                            <TD><label class=req>Identificador do Explorador </label></TD>
                                            <td colspan='3'>
                                                <input class='text' returnValue=<%=request.getParameter("explorerName") == null ? "\"\"":"\"" + request.getParameter("explorerName") + "\""%> value=<%=request.getParameter("explorerName") == null ? "\"\"":"\"" + request.getParameter("explorerName") + "\""%> onfocus='setFieldWFocus("explorerName")'  maxlength="500" name = 'explorerName' tabindex='22'>
                                            </td>
                                        </tr>
                                        <tr height="24px">
                                            <td>&nbsp;</td> 
                                            <td colspan='3' >
                                                <p align="left">
                                                <button onclick="wait();location.href='__setDefaultExplorer.jsp?userName='+getElementById('userName').value+'&explorerName='+getElementById('explorerName').value+'&myIDX='+getIDX()" tabIndex="100">Configurar</button>
                                                </p>
                                            </td>
                                        </tr>
                                    </TBODY>
                                </TABLE>

</form>
</body></html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

