<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import=" netgest.bo.presentation.*"%>
<%@ page import=" netgest.bo.presentation.manager.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__workPlaceObjectHTML.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    String[] ctrl;
    int IDX;int cvui;
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);

    docHTML doc = DOCLIST.getDOC(IDX);
    String objectHTMLName=request.getParameter( "objectHTMLName" );
    long objectHTMLBoui=0;
    try
    {
        objectHTMLBoui = Long.parseLong(request.getParameter( "objectHTMLBoui" ));
    }
    catch(Exception e){/*Ignore*/}
    boObject objectHTML = uiObjectManager.getUiObject( boctx, uiObjectManager.TYPE_OBJECTHTML , objectHTMLBoui );
    
    
%>

<html>
<head>
<title></title>
<style>
 @import url('ieThemes/0/global/ui-global.css');
</style>

</head>


</head>
<script>
document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}
</script>


<body scroll="no" >
<div style='height:100%;width:100%;overflow:auto'>
<%=objectHTML.getAttribute("htmlcode").getValueString()%>
</div>
</body>
</html>


<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
