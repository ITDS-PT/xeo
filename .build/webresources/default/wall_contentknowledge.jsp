<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
if( request.getParameter("docid")==null){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=wall_knowledge_generaledit.jsp");
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
docHTML_section sec;
docHTML_grid grid;
docHTML_groupGrid gridG;
Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<META HTTP-EQUIV='Refresh' CONTENT='3000'><title>
</title>
<%@ include file='boheaders.jsp'%>
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <div width="100%" height="100%"/>
</form>
</body></html><%
} finally {
if (boctx!=null)boctx.close();if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
}%>
