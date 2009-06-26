<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=false;
try 
{
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    
    String done = request.getParameter( "done" ) == null ? "0":(String)request.getParameter( "done" );
    String s = request.getParameter( "operation" );
    if( request.getParameter( "rebuildReferences" ) != null && request.getParameter( "rebuildReferences" ).equalsIgnoreCase("start") )
    {
        EboContext threadCtx = boctx.getBoSession().createRequestContext( request, response, pageContext );
        String[] objectstobuild = request.getParameterValues( "objectsToRebuild" );
        netgest.bo.utils.rebuilder.RebuildEboReferences refs = new netgest.bo.utils.rebuilder.RebuildEboReferences( threadCtx, objectstobuild );
        refs.start();
        response.sendRedirect("__ebo_rebuild.jsp?show=status");
        pageContext.getServletContext().setAttribute( "statusObj" , refs );
    }
    else if( request.getParameter( "rebuildSecurityKeys" ) != null && request.getParameter( "rebuildSecurityKeys" ).equalsIgnoreCase("start") )
    {
        EboContext threadCtx = boctx.getBoSession().createRequestContext( request, response, pageContext );
        
        String[] objectstobuild = request.getParameterValues( "objectsToRebuild" );
        
        netgest.bo.utils.rebuilder.RebuildSecurityKeys refs = new netgest.bo.utils.rebuilder.RebuildSecurityKeys( threadCtx, objectstobuild, request.getParameter("allOnSameTable")!=null );
        refs.start();
        response.sendRedirect("__ebo_rebuild.jsp?show=status");
        pageContext.getServletContext().setAttribute( "statusObj" , refs );
    }
    else if( request.getParameter( "rebuildTextIndex" ) != null && request.getParameter( "rebuildTextIndex" ).equalsIgnoreCase("start") )
    {
        EboContext threadCtx = boctx.getBoSession().createRequestContext( request, response, pageContext );
        
        String[] objectstobuild = request.getParameterValues( "objectsToRebuild" );
        
        netgest.bo.utils.rebuilder.RebuildTextIndex refs = new netgest.bo.utils.rebuilder.RebuildTextIndex( threadCtx, objectstobuild );
        refs.start();
        response.sendRedirect("__ebo_rebuild.jsp?show=status");
        pageContext.getServletContext().setAttribute( "statusObj" , refs );
    }
    else if( request.getParameter( "rebuildMD5" ) != null && request.getParameter( "rebuildMD5" ).equalsIgnoreCase("start") )
    {
        netgest.bo.system.boApplication boapp = netgest.bo.system.boApplication.getApplicationFromStaticContext("XEO");
        netgest.bo.system.boSession sess = boapp.boLogin("SYSUSER","ABC");
        if(netgest.utils.MD5Utils.rebuildMD5UserCode(boctx))
        {
            response.sendRedirect("__ebo_rebuild.jsp?done=1");
        }
        else
        {
            response.sendRedirect("__ebo_rebuild.jsp?done=2");
        }
	}
	else if( request.getParameter( "operation" ) != null && request.getParameter( "operation" ).equalsIgnoreCase("cleanCache") )
    {
		netgest.bo.runtime.cacheBouis.cleanCacheBoui();
		response.sendRedirect("__objectMap.jsp");
	}
    else if( request.getParameter( "operation" ) != null && request.getParameter( "operation" ).equalsIgnoreCase("clearHandler") )
    {
        netgest.bo.def.boDefHandler.clearCache();
    }
    else if (request.getParameter( "show" ) != null && request.getParameter( "show" ).equalsIgnoreCase("status"))
    {
%>    
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<%
    netgest.bo.utils.rebuilder.OperationStatus statusObj = (netgest.bo.utils.rebuilder.OperationStatus)pageContext.getServletContext().getAttribute( "statusObj" );
    if( statusObj.isAlive() )
    {
%>
    <META HTTP-EQUIV="Refresh" CONTENT="1">
<%
    }
%>
    <title>Utilties to data integrity of the XEO Schema</title>
    <style>
        .red {
            color:red;
        }
        .green {
            color:green;
        }
    </style>
  </head>
  <body>    
    <pre>
<%=statusObj.getLog()%>
    <a name='end' id='end' href="_"></a>
    </pre>
    <script>
        end.focus();
    </script>
  </body>
</html>   
<%    
    }
    else
    {
%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Utilties to data integrity of the XEO Schema</title>
    <style type="text/css">
        @import url('xeo.css');
    </style>
  </head>
  <script src="../ngt_server/xeo.js">
  </script>
  <body>
    <%
        if("1".equals(done))
        {
        %>
            <p align="center"><font color="#008000" face="Arial">Reconstru袯 das chaves MD5 efectuada com sucesso.</font></p>
        <%
            done = "0";
        }
        else if("2".equals(done))
        {
            %>
                <p align="center"><font color="#FF0000" face="Arial">Reconstru袯 das chaves MD5 efectuada com insucesso.</font></p>
            <%
            done = "0";
        }
    %>
    <pre>
        Operações disponíveis

<form name="formSecurityKeys" >
  <input type="submit" value="Rebuild All Objects References" onclick="operation.name='rebuildReferences'">


    <input type='hidden' name='rebuildSecurityKeys' id="operation" value='start'></input>
  <select name='objectsToRebuild' size="20" multiple="multiple">
    <option value="[ALL]">[ All  ]</option>
<%  
    netgest.bo.def.boDefHandler[] defs = netgest.bo.def.boDefHandler.listBoDefinitions();
    for (int i = 0; i < defs.length; i++) 
    {
%>
        <option id="<%=defs[i].getName()%>"><%=defs[i].getName()%></option>
<%
    }
%>
  </select><input type="submit" value="Rebuild Selected Security Keys"> <input name='allOnSameTable' type="CHECKBOX" checked="checked" > Reconstruir todos os objectos na mesma tabela </input>

  <input type="submit" value="Rebuild Text Index " onclick="operation.name='rebuildTextIndex'" > <input name='allOnSameTable' type="CHECKBOX" checked="checked" > Reconstruir todos os objectos na mesma tabela </input>
  <br>
  <input type="submit" value="Rebuild MD5 User Keys " onclick="operation.name='rebuildMD5'" > Reconstruir as chaves MD5 </input>
  <%
		long hits = netgest.bo.runtime.cacheBouis.cacheBouisHits();
		int bouisSize = netgest.bo.runtime.cacheBouis.cacheBouisSize();
		int invalidBouis = netgest.bo.runtime.cacheBouis.cacheBouisInvalidsSize();
	%>
	<br>
Cache bouis	
	<br>
Nº de bouis = <%=bouisSize%>
	<br>
Nº de hits = <%=hits%>
	<br>
Nº de bouis inválidos = <%=invalidBouis%>
	<br>
	<input type="submit" value="Clear Boui Cache" onclick="document.location.href='__ebo_rebuild.jsp?operation=cleanCache'" ></input>
    <br>
    <input type="submit" value="Clear bodef Handlers" onclick="document.location.href='__ebo_rebuild.jsp?operation=clearHandler'" ></input>
  
</form>    

    </pre>
  </body>
</html>   
<%
    }
%>
  </xeo:Session>
  </head>
  <body>
  </body>
</html>
<%} finally 
{
    if( boctx != null ) boctx.close();
}
%>
