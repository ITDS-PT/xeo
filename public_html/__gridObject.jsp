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
    response.sendRedirect("login.jsp?returnPage=_gridObject.jsp");
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
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);



%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Object list</title>
<%@ include file='boheaders.jsp'%>

<body>


<form class="objectForm" name="boForm" id="<%=IDX%>">


</form>

<FORM name='boFormSubmit' method='get'>
<INPUT type='hidden' name='boFormSubmitXml' />
<% if( request.getParameter("ctxParent") != null ) { %> 
<INPUT type='hidden' name=ctxParent value='<%=request.getParameter("ctxParent")%>' />
<% } %> 
<% if( request.getParameter("addToCtxParentBridge") != null ) { %> 
<INPUT type='hidden' name=addToCtxParentBridge value='<%=request.getParameter("addToCtxParentBridge")%>' />
<% } %> 
<% if( request.getParameter("relatedDocid") != null ) { %> 
<INPUT type='hidden' name=relatedDocid value='<%=request.getParameter("relatedDocid")%>' />
<% } %> 
<% if( request.getParameter("parent_attribute") != null ) { %> 
<INPUT type='hidden' name=parent_attribute value='<%=request.getParameter("parent_attribute")%>' />
<% } %> 
<% if( request.getParameter("parent_boui") != null ) { %> 
<INPUT type='hidden' name=parent_boui value='<%=request.getParameter("parent_boui")%>' />
<% } %> 
<% if( request.getParameter("method") != null ) { %> 
<INPUT type='hidden' name=method value='<%=request.getParameter("method")%>' />
<% } %> 
<% if( request.getParameter("searchClientIdx")!=null ) { %>
<INPUT type='hidden' name='searchClientIdx' value='<%=request.getParameter("searchClientIdx")%>' />
<% } %>
<% if( request.getParameter("actIdxClient") != null ) { %> 
<INPUT type='hidden' name=actIdxClient value='<%=request.getParameter("actIdxClient")%>' />
<% } %>
<% if( request.getParameter("actRenderObj") != null ) { %> 
<INPUT type='hidden' name=actRenderObj value='<%=request.getParameter("actRenderObj")%>' />
<% } %>
<% if( request.getParameter("actRenderAttribute") != null ) { %>
<INPUT type='hidden' name=actRenderAttribute value='<%=request.getParameter("actRenderAttribute")%>' />
<% } %>
<% if( request.getParameter("ctxParentIdx") != null ) { %> 
<INPUT type='hidden' name='ctxParentIdx' value='<%=request.getParameter("ctxParentIdx")%>' />
<% } %> 
<% if( request.getParameter("toClose") != null && !DOC.haveErrors()) { %> 
<INPUT type='hidden' name='toClose' value='y' />
<% } %> 
<% if( masterdoc ) { %>
<INPUT type='hidden' name=masterdoc value='true' />
<% } %>
<INPUT type='hidden' name='boFormSubmitMode' />
<INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
<INPUT type='hidden' name='boFormSubmitId' />
<% if (currObjectList.getParent()!=null) { %><INPUT type='hidden' name='BOUI' value='<%=currObjectList.getParent().getBoui()%>'/>
<INPUT type='hidden' name='boiChanged' value='<%=currObjectList.getParent().isChanged()%>' />
<%}%>
<INPUT type='hidden' value='<%=IDX%>' name='docid' />
<INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM></body></html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
