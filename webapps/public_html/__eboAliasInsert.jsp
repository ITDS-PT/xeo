<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
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
if(request.getParameter("ObjBoui") != null && request.getParameter("alias") != null)
{
    boObject obj = null;
    try
    {
        obj = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("ObjBoui")));
    }
    catch(Exception e)
    {
        message = "Não existe um objecto com o boui referido, ou o objecto ainda não foi gravado.";
    }
    if(message == null )
    {
        if(obj == null || !obj.exists())
        {
            message = "Não existe um objecto com o boui referido, ou o objecto ainda não foi gravado.";
        }
        else
        {
            boObjectList l = boObjectList.list(boctx, "select Ebo_Alias where ui = " + request.getParameter("ObjBoui") + " and upper(alias) = upper('" + request.getParameter("alias")+"')");
            l.beforeFirst();
            if(!l.next())
            {
                boObject aliasObj = boObject.getBoManager().createObject(boctx, "Ebo_Alias");
                aliasObj.getAttribute("ui").setValueLong(Long.parseLong(request.getParameter("ObjBoui")));
                aliasObj.getAttribute("alias").setValueString(request.getParameter("alias"));
                aliasObj.update();
                message = "Alias gravado com sucesso.";
            }
            else
            {
                message = "Alias já existe.";
            }
        }
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
var objLabel='Inserir Alias';
</script>
<% if(message !=null) {%>
    <body ondragenter="activethis()" onload="winmain().activeDocByIdx(<%=myIDX%>);alert('<%= message%>');">
    <% }else{%>
    <body ondragenter="activethis()" onload="winmain().activeDocByIdx(<%=myIDX%>);">
    <% }%>
<form>
  
                                 
                                 <TABLE class='section' style="" cellSpacing='0' cellPadding='3'  >
                                    <COLGROUP/>
                                    <COL width='120' />
                                    <COL />
                                    <TBODY>
                                        <TR height='0'>
                                            <TD colspan='4'><span style='width:100%;border-bottom:1px solid #00000;font:14px'><b>Inserção de Alias</b></span></TD>
                                        </TR>
                                        <tr>
                                            <TD><label class=req>Boui do Objecto</label></TD>
                                            <td colspan='3'>
                                                <input class='num' returnValue=<%=request.getParameter("ObjBoui") == null ? "\"\"":"\"" + request.getParameter("ObjBoui") + "\""%> value=<%=request.getParameter("ObjBoui") == null ? "\"\"":"\"" + request.getParameter("ObjBoui") + "\""%> acc='0' minAcc='0' max='' min='-999999999' dt='' required grp='false' id='ObjBoui' onfocus='setFieldWFocus("ObjBoui")'  maxlength="500" name = 'ObjBoui' tabindex='22'>
                                            </td>
                                        </tr>
                                        <tr>
                                            <TD><label class=req>Alias</label></TD>
                                            <td colspan='3'>
                                                <input class='text' value=<%=request.getParameter("alias") == null ? "\"\"":"\"" + request.getParameter("alias") + "\""%> id='alias' onfocus='setFieldWFocus("alias")'  maxlength="500" name = 'alias' tabindex='22'>
                                            </td>
                                        </tr>
                                        <tr height="24px">
                                            <td>&nbsp;</td> 
                                            <td colspan='3' >
                                                <p align="right">
                                                <button onclick="location.href='__eboAliasInsert.jsp?ObjBoui='+getElementById('ObjBoui').value+'&alias='+getElementById('alias').value+'&myIDX='+getIDX()" tabIndex="100">Adicionar</button>
                                                </p>
                                            </td>
                                        </tr>
                                    </TBODY>
                                </TABLE>

</form>
</body></html>
<%
} finally {
    if(initPage){if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

