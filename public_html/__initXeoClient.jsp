<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>


<%
  String name = request.getParameter("name");
  String guid = request.getParameter("guid");
  javax.servlet.http.Cookie x= new javax.servlet.http.Cookie("XeoWin32Client_address",name);
  String xxx= request.getRequestURL().toString();
  
  x.setMaxAge( (int)(( System.currentTimeMillis()+3600000*24*30)/1000) );
  response.addCookie( x );
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<script>

function setCookies()
{
  var oMe = window.self;
  oMe.opener = window.self;
  oMe.close();
}

</script>
<META HTTP-EQUIV="Refresh" CONTENT="120">
<body onload="setCookies()">
</body>
