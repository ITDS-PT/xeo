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

        String aux = request.getParameter("lineControl");
        int lineControl = -1;
        if(aux != null && aux.length() > 0)
        {
            lineControl =   ClassUtils.convertToInt( aux ,-1);
        }
        
        aux = request.getParameter("group_page");
        int group_page = -1;
        if(aux != null && aux.length() > 0)
        {
            group_page =   ClassUtils.convertToInt( aux ,-1);
        }

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
<script>
 function refreshTree()
 {
    parent.refreshExplorer();
 }
 function bindgroup()
 {
    <%if ( lineControl > -1 ) { %> 
       var o = window.parent.document.getElementById("<%=group_key%>");
       var w = window.parent.document.getElementById("waitingRecs");
       if ( w )
       { 
         w.parentElement.removeChild(w);
         window.parent.noWait();
       }
       if ( o )
       {
           // alert(content.innerHTML); 
            window.parent.document.getElementById("<%=group_key%>").insertAdjacentHTML("afterEnd", content.innerHTML );
       }
    <%}%>
 }
</script>
<title></title>
<%if(exp == null && request.getParameter("group_key") != null){%>
<body onload="refreshTree();">
<%}else{%>
<body onload="bindgroup()">
<iframe style="display:none" id = helperCloseGroup src="__treeHelperCloseGroup.jsp?docid=<%=IDX%>&explorer_key=<%=keyExplorer%>"></iframe>
<div id='content'>
<%if ( lineControl > -1 )
    { 
        out.flush();
        PrintWriter _out = new PrintWriter(out);
        TreeBuilder.openGroupTree( _out , exp.getTree() , lineControl , group_key,group_page, DOC,DOCLIST);
     }
   %>
</div>


<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=keyExplorer%>' name='explorer_key' />
    <INPUT type='hidden' value='<%=group_key%>' name='group_key' />
    <INPUT type='hidden' value='<%=group_page%>' name='group_page' />
    <INPUT type='hidden' value='<%=lineControl%>' name='lineControl' />
    <INPUT type='hidden' value='' name='toExecute' />
</FORM>
<%}%>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
