<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
FileReader ixeouser= null;
try {
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    ixeouser=new FileReader(boConfig.getDeploymentDir()+"iXEOUser.xeoimodel");
    boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
    boapp.boLogin( "SYSUSER" , boLoginBean.getSystemKey() , boapp.getDefaultRepositoryName() );    
    
    if(bosession== null || !bosession.getUser().getUserName().equalsIgnoreCase("SYSUSER")) {
        response.sendRedirect("login.jsp?returnToPage=builderApplet.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }
}
catch (Exception e)
{
if (ixeouser!=null)ixeouser.close();
System.out.println(e.getMessage());
}
%>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Builder</title>
            </head>
            <body bgcolor="#DFDFDF">
                <div id="Layer1" align="center">
                    <applet	code="builderExplorer.BuilderApplet.class" archive="builder/builderApplet.jar" width=800 height=688 align="center" id = "Builder" mayscript="mayscript">
                        <param name=DEVELOPER_MODE value="false"></param>
                        <%
                        if(netgest.bo.boConfig.getWebContextRoot() != null && netgest.bo.boConfig.getWebContextRoot().length() > 0)
                        {
                        %>
                        <param name=WEB_CONTEXT_ROOT value="<%=netgest.bo.boConfig.getWebContextRoot()%>"></param>
                        <%
                        }
                        %>
                    </applet>                     
                </div>
            </body>
        </html>   


