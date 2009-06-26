<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
    netgest.bo.system.boSession bosession = (netgest.bo.system.boSession)request.getSession().getAttribute("boSession");    
    boolean close = "true".equalsIgnoreCase((String)request.getParameter("close"));
    if(bosession != null)
    {
        bosession.closeSession();
    }
    else
    {
        close = true;
    }
    request.getSession().invalidate();
    if(!close)
    {
        %>
            <jsp:forward page="login.jsp"/>
        <%
    }
    else
    {
        %>
            <html><body onload='window.parent.close();'></body></html>
        <%
    }
%>

