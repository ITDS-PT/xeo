<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.services.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
String sessionid=request.getParameter("sessionid");
boolean initPage=false;
try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null || !bosession.getUser().getUserName().equals("SYSUSER")) {
    response.sendRedirect("login.jsp?returnToPage="+request.getRequestURL());
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
if (sessionid!=null) HubSessions.sessions.remove(sessionid);
%>
<table border=1>
<tr>
<td>Username</td>
<td>Session Id</td>
<td>Idle Time (seconds)</td>
<td>State</td>
</tr>
<%
  Enumeration oEnum=HubSessions.sessions.elements();    
  while(oEnum.hasMoreElements())              
  { 
    out.print("<tr>");
    HubSession hsession=(HubSession)oEnum.nextElement();
    Date d1=hsession.getTimeStamp();
    out.print("<td>"+hsession.getSession().getUser().getUserName()+"</td>");
    out.print("<td>"+hsession.getSessionID()+"</td>");
    long idletime=HubSessions.getHubSessionIdleTime(d1,new Date());
    out.print("<td>"+idletime+"</td>");
    if (idletime>HubSessions.sessiontimeout)    
      out.print("<td color=red>Timed Out</td>");
    else
      out.print("<td color=green>Ok</td>");
    out.print("<td border=0 style='border-left-style: none; border-right-style: none; border-bottom-style: none; border-top-style:none'>");
    out.print("<form name='kill' action='__checkHubSessions.jsp' method='POST'>");
    out.print("<input type=hidden name='sessionid' value ='"+hsession.getSessionID()+"'>");
    out.print("<input type=submit value ='Kill' name='Kill'>");
    out.print("</td>");
    out.print("</tr>");
  } 
%>
</table>
<%
}
finally {if (boctx!=null)boctx.close();}
%>