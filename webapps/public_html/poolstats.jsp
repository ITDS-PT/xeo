<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="java.util.Enumeration"%>

<%@ page import="xeo.client.business.helper.*"%>
<%@ page import="xeo.client.business.events.*"%>



<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<meta http-equiv="refresh" content="30">

<title>bo Poll Statistics</title>
<style type='text/css'>
       @import url('xeo.css');

body{
	BORDER-RIGHT: 0px;
	BORDER-TOP: 0px;
	FONT-SIZE: 11px;
	PADDING: 10px;
	BORDER-LEFT:0px; 
	CURSOR: default;
	BORDER-BOTTOM: 0px;
	FONT-FAMILY: Verdana, Arial;
	BACKGROUND-COLOR: #CCCCCC;
    SCROLL:NO;
}
h1{
	FONT-FAMILY: Verdana, Arial;
    FONT-SIZE:13px;
    color:#0009FF;
}

</style>
</head>
<body scroll=no>

<div style='overflow:auto;height:100%;border:1px solid #333333;padding:2px;background-color:#FFFFFF'>
<h1 >Informações sobre JVM</h1>
<pre>

Java Name     : <%=System.getProperty( "java.vm.name" )%>
Java Vendor   : <%=System.getProperty( "java.vm.vendor" )%>
Java Version  : <%=System.getProperty( "java.version" )%>
                <%=System.getProperty( "java.vm.version" )%>
OS Name       : <%=System.getProperty( "os.name" )%>
OS Version    : <%=System.getProperty( "os.version" )%>
Max Memory    : <%=Runtime.getRuntime().maxMemory()/1024%> KB
Total Memory  : <%=Runtime.getRuntime().totalMemory()/1024%> KB
Free Memory   : <%=Runtime.getRuntime().freeMemory()/1024%> KB

  <%
  Enumeration oEnum;
  System.gc();
  %>

Entertrise eXtensible Objects Pool Statistics:
Number Of Objects in Pool   :
Number of Reserved Obects   :


<%
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
netgest.bo.system.boPoolManager pool = bosession.getApplication().getMemoryArchive().getPoolManager();
boSession[] sessionsActive = bosession.getApplication().getSessions().getActiveSessions();
%>


BOUIS cache <%= netgest.bo.runtime.cacheBouis.getSizeCacheBouis() %>
Users readlist cache <%= netgest.bo.runtime.cacheBouis.getSizeUsers() %>
</PRE>


<h1 >Sessões activas</h1>
<table cellSpacing='0' cellPadding='0' class='g_std' style='background-color:#FFFFFF;width:100%;table-layout:fixed'>
<tr style="height:25">

<td class='gh_std'>UTILIZADOR</td>
<td class='gh_std'>SISTEMA</td>
<td class='gh_std'>INICIO</td>
<td class='gh_std'>LAST REQUEST</td>

</tr>

<%
for (int i = 0; i <  sessionsActive.length; i++) 
{
%>
<% if ( !sessionsActive[i].getUser().getName().equalsIgnoreCase("SYSTEM") ){ %>
<tr style="height=25">
 
 <td class='gCell_std'><%=sessionsActive[i].getUser().getName() %></td>
 <td class='gCell_std'><%=sessionsActive[i].getClientName()%></td>
 <td class='gCell_std'><%=sessionsActive[i].getCreatedTime()%></td>
 <td class='gCell_std'><%=sessionsActive[i].getLastActivity()%></td>
 
</tr>    
<%}%>
<%}%>

</table>
<h1 >Sessões fechadas</h1>
<table cellSpacing='0' cellPadding='0' class='g_std' style='background-color:#FFFFFF;width:100%;table-layout:fixed'>
<tr style="height:25">

<td class='gh_std'>UTILIZADOR</td>
<td class='gh_std'>SISTEMA</td>
<td class='gh_std'>INICIO</td>
<td class='gh_std'>LAST REQUEST</td>

</tr>

<%
java.util.Calendar c = java.util.Calendar.getInstance();
c.roll(Calendar.HOUR_OF_DAY, -2);
java.util.Date d4 = c.getTime();
java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
String s = sdf.format(d4);
for (int i = 0; i <  sessionsActive.length; i++) 
{
   if ( d4.after(sessionsActive[i].getLastActivity()) || d4.equals(sessionsActive[i].getLastActivity()))
   {
        %>
        <tr style="height=25">
         
         <td class='gCell_std'><%=sessionsActive[i].getUser().getName() %></td>
         <td class='gCell_std'><%=sessionsActive[i].getClientName()%></td>
         <td class='gCell_std'><%=sessionsActive[i].getCreatedTime()%></td>
         <td class='gCell_std'><%=sessionsActive[i].getLastActivity()%></td>
         
        </tr>    
        <%
   
        sessionsActive[i].closeSession();
   }
}%>

</table>

<%
if(request.getParameter("clients")!= null)
{

    try
    {
        final InitialContext context = new InitialContext();
        context.lookup(ServiceType.XEO_CLIENT);
        Enumeration clientsList = context.list(ServiceType.XEO_CLIENT);
        
    %>
    <h1 >Clientes activos</h1>
    <table cellSpacing='0' cellPadding='0' class='g_std' style='background-color:#FFFFFF;width:100%;table-layout:fixed'>
    <tr style="height:25">
    <td class='gh_std'>UTILIZADOR</td>
    <td class='gh_std'>HOST</td>
    <td class='gh_std'>IP</td>
    
    
    </tr>
    
    <%
    NameClassPair clientObj = null;
    String[] client = null;
    while(clientsList.hasMoreElements()) 
    {
        clientObj = (NameClassPair)clientsList.nextElement();        
        client = clientObj.getName().split("@");
        //ClientEvents clientEvents = RegistryHelper.getClientInfo(context,clientObj.getName());
    %>
    
    <tr style="height=25">
    
     <td class='gCell_std'><%= "clientEvents.toString()" %></td> 
     <td class='gCell_std'><%= client[0] %></td>
     <td class='gCell_std'><%= client[1] %></td>
     
    </tr>    
    
    <%}%>
    </table>
    <%
    }
    catch(Exception en)
    { 
    //    en.printStackTrace();
    }
}
%>
<%if ( request.getParameter("debug")!= null )
{
%>

<%=pool.dumpPool()%>
<%}%>
</div>
</body>
</html>
