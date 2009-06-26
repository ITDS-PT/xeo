<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.ie.components.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=false;
EboContext boctx=null;
 
try {
    
        String group_key="";
        String keyExplorer = request.getParameter("explorer_key");

        int IDX = ClassUtils.convertToInt( request.getParameter("docid") );

        Explorer exp=null;
        docHTML DOC = null;
        if( request.getParameter("group_key") != null )
        {
           initPage=true;
           boctx = (EboContext)request.getAttribute("a_EboContext");
           boSession bosession = (boSession)request.getSession().getAttribute("boSession");
           if(bosession== null) {
               response.sendRedirect("login.jsp");
               return;
           }
           if(boctx==null) {
               boctx = bosession.createRequestContext(request,response,pageContext);
               request.setAttribute("a_EboContext",boctx);
           }
           
           
           group_key = request.getParameter("group_key");
           
           exp = ExplorerServer.getExplorer( keyExplorer );
           String[] ctrl= DOCLIST.processRequest(boctx);
           DOC = DOCLIST.getDOC(IDX);
        }
        
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<body onload="">
<div id='content'>
<%if ( initPage )
    { 
        TreeBuilder.closeGroupTree(exp.getTree(), group_key);        
    }
%>
     
</div>

<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=keyExplorer%>' name='explorer_key' />
    <INPUT type='hidden' value='<%=group_key%>' name='group_key' />
</FORM>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>