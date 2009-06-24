<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=false;
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
%>
<html>

<head>
<%
  boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
%>
<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<title><%=perf.getAttribute("name").getValueString()%></title>
</head>

<frameset framespacing="0" border="0" rows="*" frameborder="0">
  <frame name="main" src="__start.jsp" scrolling="yes">
 <!-- <frame name="footer" src="footer.jsp" scrolling="no" target="footer" marginwidth="1" marginheight="1" noresize>-->
</frameset>

</html>

<%} finally 
{
    if( boctx != null ) boctx.close();
    // IMBR
    if( DOCLIST != null && boctx != null ) DOCLIST.releseObjects(boctx);
    //if( DOCLIST != null ) DOCLIST.releseObjects(boctx);
}
%>
