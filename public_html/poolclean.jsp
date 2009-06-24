<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="java.util.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="java.util.Enumeration"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="xeo.client.business.helper.*"%>
<%@ page import="xeo.client.business.events.*"%>

<%
    
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<META HTTP-EQUIV="Refresh" CONTENT="120">
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
Free Memory   : <%=Runtime.getRuntime().freeMemory()/1024%> KB  <% System.gc(); %>


  <%
  Enumeration oEnum;
  %>

Entertrise eXtensible Objects Pool Statistics:
Number Of Objects in Pool   :
Number of Reserved Obects   :





BOUIS cache <%= netgest.bo.runtime.cacheBouis.getSizeCacheBouis() %>
Users readlist cache <%= netgest.bo.runtime.cacheBouis.getSizeUsers() %>
</PRE>

<%
if( request.getParameter("clearCacheExplorer")!=null )
{
netgest.bo.presentation.render.elements.ExplorerServer.clearCache();
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
netgest.bo.system.boPoolManager pool = bosession.getApplication().getMemoryArchive().getPoolManager();
boSession[] sessionsActive = bosession.getApplication().getSessions().getActiveSessions();
%>
<%="<html><head><title>asasdasd</title></head><body><h1>qweqweqwe</h1><div>This is the content</div></body></html>".replaceAll("<(.|\n)*?>"," ")%>
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
<%

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
//    ClientEvents clientEvents = ServiceHelper.getClientInfo(context,clientObj.getName());
%>

<tr style="height=25">

 
 <td class='gCell_std'><%= client[0] %></td>
 <td class='gCell_std'><%= client[1] %></td>
 
</tr>    

<%}%>
<%
}
catch(Exception en)
{ 
//    en.printStackTrace();
}
%>
</table>
<%if ( true )
{
%>

<%
 //Object o =pool.ObjectPool.remove( request.getParameter("cleanBoui") );
 String toRet="xxxx";
 int i=0;
  java.util.Enumeration xobjpool =pool.ObjectPool.keys(); 
   while ( xobjpool.hasMoreElements() )
        {
         i++;   
            Object key = xobjpool.nextElement();
            boPoolable o = ( boPoolable )pool.ObjectPool.get( key );
            String username = ( String )pool.ObjectPoolUSERNAMES.get( key );
            toRet+="<br>"+key+" -- ";
            if ( o instanceof boObject )
            {
                boObject bo=(( netgest.bo.runtime.boObject) o);
                toRet+="<br>"+key+" -> "+bo.getBoui()+" <span style='color:blue'>"+bo.toString() +"</span> "+( bo.isChanged()?"<b>statefull</b>":"")+( bo.isChanged()?"<b> changed</b>":"") +"<span style='font:8px arial;color:green' >"+username+"</span>";
                if( o.getEboContext() != null )
                {
                  toRet+= "<span style='color:red'>"+o.getEboContext().poolUniqueId()+"</span>";
                  if ( request.getParameter("cleanBoui")!= null )
                  {
                     pool.ObjectPool.remove( key );
                  }
                }
            
            }
            
        }
        
 
 out.print( toRet );
 out.print(i);
%>
<%}%>
</div>
</body>
</html>
