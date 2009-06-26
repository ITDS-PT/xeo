<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*, netgest.bo.*, netgest.bo.system.*, netgest.bo.utils.SchemaUtils, netgest.bo.utils.IProfileUtils"%>
<html>
<head>
<script>
function markButtonClick()
{
  var oInput = document.createElement( "input" );  
  oInput.name = "buttonClick";
  oInput.id = "buttonClick";
  oInput.type = "hidden";
  oInput.value = "true";  
  document.loginForm.appendChild( oInput );
  //document.loginForm.submit();
}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK REL="SHORTCUT ICON" HREF="favicon.ico">
<title>netgest.net Login Page</title>

<%
    final boolean AUTO_LOGIN = false;
    boolean ok=false;
    boolean showSchemas=false;
    boolean showIProfiles=false;
    java.util.Enumeration oEnum = request.getHeaderNames();
    boSession bosession = null;
	
    String fullwindow=request.getParameter("fullwindow");
    
    if(!"true".equals(request.getParameter("buttonClick")))
    {
        request.getSession().removeAttribute("firstTime");
        request.getSession().removeAttribute("boSession");
    }

    if(request.getSession() != null && request.getSession().getAttribute("firstTime") == null)
    {
        request.getSession().setAttribute("firstTime", Boolean.FALSE);
        if( fullwindow == null )
            fullwindow="on";
    }        
    String[] schemas = null;
    
    String[] iProfiles = null;
   
	boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
    if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
    {
        bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession.getRepository() != null && 
           bosession.getRepository().getName().equals(request.getParameter("SCHEMA")) &&
           bosession.getPerformerIProfileBouiAsString() != null && 
           bosession.getPerformerIProfileBouiAsString().equals(request.getParameter("IPROFILE")) 
           )
        {
            ok=true;
        }
        else if(bosession.getRepository() != null && 
           request.getParameter("SCHEMA") == null &&
           bosession.getRepository().getName() != null &&
           bosession.getPerformerIProfileBouiAsString() != null && 
           bosession.getPerformerIProfileBouiAsString().equals(request.getParameter("IPROFILE")) 
           )
        {
            ok=true;
        }
    }
    
    // Verificar se estamos no gestor de conteúdos
    Properties prop = boConfig.getContentMngmConfig();
    if(!prop.isEmpty())
    {
      // Tirar as keys com mais de 3 dias
      Iterator i = netgest.utils.AuthenticatedKeys.keys.iterator();
      while(i.hasNext()) {
        long ckey = ((Long)i.next()).longValue();
        if((System.currentTimeMillis() - ckey) > 259200000)
          i.remove();
      }
      Long key = new Long(System.currentTimeMillis());
      netgest.utils.AuthenticatedKeys.keys.add(key);
      request.getSession().setAttribute("key",key);
    }
    
    if(AUTO_LOGIN) {
        bosession = boapp.boLogin("SYSUSER","ABC");
        
        request.getSession().setAttribute("boSession",bosession);
        String refer = request.getParameter("returnPage");
        if(refer==null) refer = "";
        response.sendRedirect(refer);
        return;
    }
    String errormessage="";
    if ( !ok )
    {
        if((request.getParameter("USERNAME")!=null && request.getParameter("PASSWORD") != null) ||
            (request.getSession() != null && request.getSession().getAttribute("boSession")!=null)
            ) {
            try {
                if(request.getSession() != null && request.getSession().getAttribute("boSession")!=null)
                {
                    bosession = (boSession)request.getSession().getAttribute("boSession");
                }
                else
                {
					if(request.getSession()!=null)
						request.getSession().setAttribute("origem", "backend");
					
                    bosession = boapp.boLogin(request.getParameter("USERNAME"),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request);
                    request.getSession().setAttribute("boSession",bosession);
                }
                if(schemas == null)
                {
                    schemas = SchemaUtils.getSchemas(bosession);
                }
                if(schemas.length <= 1)
                {
                    ok=true;
                    showSchemas = false;
                    if(schemas.length == 1)
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),schemas[0])) ;
                    }
                    else
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),"default")) ;
                    }
                }
                else
                {
                    showSchemas = true;
                }
                if(iProfiles == null)
                {
                    iProfiles = IProfileUtils.getIProfiles(bosession);
                }
                if(iProfiles.length <= 1)
                {
                    String iprof = IProfileUtils.DEFAULT;
                    if(iProfiles.length > 0)
                    {
                        iprof = iProfiles[0].split(";")[0];
                    }
                    showIProfiles = false;
                    bosession.setPerformerIProfileBoui(iprof);
                }
                else
                {
                    ok=false;
                    showIProfiles = true;
                }
                if(!ok && request.getParameter("SCHEMA") != null)
                {
                    if(!"default".equalsIgnoreCase(request.getParameter("SCHEMA")))
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),request.getParameter("SCHEMA"))) ;
                    }                    
                    ok = true;
                }
                if(request.getParameter("IPROFILE") != null)
                {
                    bosession.setPerformerIProfileBoui(request.getParameter("IPROFILE"));
                    ok = true;
                }
            } catch (netgest.bo.system.boLoginException e) {
                if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                    errormessage = netgest.bo.localized.JSPMessages.getString( "login.8" );
                } else {
                    errormessage = netgest.bo.localized.JSPMessages.getString( "login.9" );
                }
            } catch (Exception e) {
                errormessage = netgest.bo.localized.JSPMessages.getString( "login.8" );
                throw(e);
            }
        }
        else if ( request.getRemoteUser() != null && request.getRemoteUser().length() > 0)
        {
            try {
                if(request.getSession() != null && request.getSession().getAttribute("boSession")!=null)
                {
                    bosession = (boSession)request.getSession().getAttribute("boSession");
                }
                else
                {
                    String user = request.getRemoteUser().replaceAll("LUSITANIA\\\\","");                    
                    bosession = boapp.boLogin( user , boLoginBean.getSystemKey() , boapp.getDefaultRepositoryName() , request );
                    request.getSession().setAttribute("boSession",bosession);
                    fullwindow = "";
                }
                if(schemas == null)
                {
                    schemas = SchemaUtils.getSchemas(bosession);
                }
                if(schemas.length == 1)
                {
                    ok=true;
                    showSchemas = false;
                }
                else
                {
                    showSchemas = true;
                }
                if(iProfiles == null)
                {
                    iProfiles = IProfileUtils.getIProfiles(bosession);
                }
                if(iProfiles.length <= 1)
                {
                    String iprof = IProfileUtils.DEFAULT;
                    if(iProfiles.length > 0)
                    {
                        iprof = iProfiles[0].split(";")[0];
                    }
                    showIProfiles = false;
                    bosession.setPerformerIProfileBoui(iprof);
                }
                else
                {
                    ok=false;
                    showIProfiles = true;
                }
                if(!ok && request.getParameter("SCHEMA") != null && request.getParameter("IPROFILE") != null)
                {
                    if(!"default".equalsIgnoreCase(request.getParameter("SCHEMA")))
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),request.getParameter("SCHEMA"))) ;
                    }
                    bosession.setPerformerIProfileBoui(request.getParameter("IPROFILE"));
                    ok = true;
                }
            } catch (netgest.bo.system.boLoginException e) {
                if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                    errormessage = netgest.bo.localized.JSPMessages.getString( "login.8" );
                } else {
                    errormessage = netgest.bo.localized.JSPMessages.getString( "login.9" );
                }
            } catch (Exception e) {
                errormessage = netgest.bo.localized.JSPMessages.getString( "login.8" );
                throw(e);
            }
        }
    }
%>

<style>
BODY {
    background-color:#FFFFFF;
    margin-left:0;
    margin-right:0;
    margin-top:0;
    margin-bottom:0;
}

.fldbutton {
    border: 1px solid #6283A3;
    PADDING-RIGHT: 5px;
    color: #000000;
    PADDING-LEFT: 5px;
    FONT: 11px;
    FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr=#ffffff, EndColorStr=#DBDBD1);
    CURSOR: hand;
    HEIGHT:20px;
    WIDTH:90px;
}

.texto
{
    FONT-WEIGHT: bold;
    FONT-SIZE: 9px;
    COLOR: #8D9CB3;
    FONT-STYLE: normal;
    FONT-FAMILY: Verdana, Arial, Helvetica, sans-serif
}

.textopq
{
    FONT-SIZE: 9px;
    COLOR: #8D9CB3;
    FONT-STYLE: normal;
    FONT-FAMILY: Verdana, Arial, Helvetica, sans-serif
}

.moldura {
    border: 1px solid #6297E5;
    background-color: #EEEEEE;
}

.titulo {
    FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr=#6297E5, EndColorStr=#5383CB);
}

.erro {
    color: red;
    font: bold 14px Arial;
}

.combo {
    width: 144px;
}

</style>
<% if (ok){
    bosession.markLogin();
    request.getSession().removeAttribute("firstTime");
    if(request.getParameter("returnToPage")!=null && !request.getParameter("returnToPage").equals(""))
    {
        response.sendRedirect(request.getParameter("returnToPage"));
    }
    else if ( fullwindow==null ) 
    {
        response.sendRedirect("main.jsp");
    }
    else{
%>

<script>

function load()
{
    <%
         java.util.Random r=new java.util.Random();
         int numb=java.lang.Math.abs(r.nextInt() );
    %>
    if (window.screen.width < 800)
    {
        alert("<%=netgest.bo.localized.JSPMessages.getString( "login.10" )%>")
    }
    
    
    // Carrega GS
    childWnd = window.open("main.jsp","GS<%=numb%>", "height=" + (window.screen.availHeight - 60) + ", width=" + (window.screen.availWidth - 14) + ", top=0, left=0, menubar=0, location=0, resizable=1, status=1");

    // Do not close the window if it is the Application
    if (window.name != "GS<%=numb%>")
    {
        // Destroy this Window
        var version = parseFloat(navigator.appVersion.split("MSIE")[1]);
        if( version < 7 )
        {
            var oMe = window.self;
            oMe.opener = window.self;
            oMe.close(); 
        }
        else
        {
            document.write("<h3><%=netgest.bo.localized.JSPMessages.getString( "login.11" )%></h3>")
        }
    }
    
    
}    
</script>

</head>
<body onload="load()">
<%}
}
else {%>
<script>
    function setFocus()
    {
        if(!document.getElementById('user').disabled)
        {
            document.getElementById('user').focus()
        }
        else if(document.getElementById('schema'))
        {
            document.getElementById('schema').focus()
        }
    }
   
    function getClientHost()
    {                    
        try
        {
        //document.getElementById('host').value = xeoClient.getHost();
        }
        catch(e){}
        
    }

</script>
</head>
<body onload="getClientHost();setFocus()">

<!--<APPLET CODE="XeoThinClient.class" archive="xeoClient/xeoApplet.jar" HEIGHT="0"  WIDTH="0" id = "xeoClient" mayscript="mayscript"></APPLET>-->                    

<table cellpadding="0" cellspacing="0" width="100%" height="100%" border="0">
    <tr><td align="center">
        <form method='post' action='login.jsp' name='loginForm'>
            <input name='returnToPage' type='hidden' value='<%out.print(request.getParameter("returnToPage")==null?"":request.getParameter("returnToPage"));%>'/>
            <table border="0" width="50%" class="moldura" cellpadding="0" cellspacing="0">
                <tr>
                    <td height="30px" colspan="3" class="titulo"><img src='images/netgest_login3.gif'></td>
                </tr>
                <tr height='60px'>
                    <td colspan="3" align="center" class="erro" id='error_msg'><%=errormessage%></td>
                </tr>
                <tr><td></td>
                <td align="center">
                    <table border="0">
                        <%if(showSchemas)
                        {
                        %>
                        <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.1" )%>:</td><td><input type='text' id='user' name='USERNAME' disabled="disabled" value="<%=bosession.getUser().getUserName()%>"></td></tr>
                        <tr></tr>
                        <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.2" )%>:</td><td><select id='schema' size='1' name="SCHEMA" class="combo">
                        <%
                            for(int i = 0; i < schemas.length; i++)
                            {
                                %>
                                    <option><%=schemas[i]%></option>
                                <%
                            }
                        %>
                        </td></tr>
                        <%
                            if(showIProfiles)
                            {
                                %>
                                <tr></tr>
                                <tr><td align='right' class="texto">Perfil:</td><td><select id='iprofile' size='1' name="IPROFILE" class="combo">
                                <%
                                    String []auxx;
                                    for(int i = 0; i < iProfiles.length; i++)
                                    {
                                        auxx = iProfiles[i].split(";"); 
                                        %>
                                            <option value='<%=auxx[0]%>'><%=auxx[1]%></option>
                                        <%
                                    }
                                %>
                                </td></tr>
                                <%
                            }
                        }
                        else if(showIProfiles)
                        {
                            %>
                            <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.1" )%>:</td><td><input type='text' id='user' name='USERNAME' disabled="disabled" value="<%=bosession.getUser().getUserName()%>"></td></tr>
                            <tr></tr>
                            <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.3" )%>:</td><td><select id='iprofile' size='1' name="IPROFILE" class="combo">
                            <%
                                String []auxx;
                                for(int i = 0; i < iProfiles.length; i++)
                                {
                                    auxx = iProfiles[i].split(";"); 
                                    %>
                                        <option value='<%=auxx[0]%>'><%=auxx[1]%></option>
                                    <%
                                }
                            %>
                            </td></tr>
                            <%
                        }
                        else
                        {
                        %>
                            <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.1" )%>:</td><td><input type='text' id='user' name='USERNAME'></td></tr>
                            <tr></tr>
                            <tr><td align='right' class="texto"><%=netgest.bo.localized.JSPMessages.getString( "login.4" )%>:</td><td><input type='password' name='PASSWORD'></td></tr>
                        <%
                        }
                        %>
                    </table>
                </td>
                <td></td></tr>
                <tr height='20px'><td></td></tr>
                <tr height='30px'>
                    <td></td>
                    <td valign='middle' align='center'>
                        <input class='fldbutton' type='submit' value='<%=netgest.bo.localized.JSPMessages.getString( "login.5" )%>' onclick='markButtonClick()'>
                        <input class='fldbutton' type='button' value='<%=netgest.bo.localized.JSPMessages.getString( "login.6" )%>' onClick='javascript: window.close()'>
                    </td>
                    <td></td>
                </tr>
                
                <tr height='70px'>
                   
                     <td colspan=3 valign="bottom" align="right">
                        <table width="100%">
                        <tr><td width='1%'>                        
                        <%if("on".equals(fullwindow))
                        {
                        %>
                            <span class="textopq"><nobr><%=netgest.bo.localized.JSPMessages.getString( "login.7" )%></nobr></span><input type='checkbox' checked name='fullwindow'/>
                        <%
                        }
                        else
                        {
                        %>
                            <span class="textopq"><nobr><%=netgest.bo.localized.JSPMessages.getString( "login.7" )%></nobr></span><input type='checkbox' name='fullwindow'/>
                        <%
                        }
                        %>
                        </td>
                        <td width='99%' align="right" >
                        <img src='about/power_netgestbo.gif' style="cursor:hand;" onclick="javascript: window.location='http://www.netgest.net';">
                        </td>
                        </tr>
                        </table>
                    </td>
                </tr>
                
                
                
            </table>
          <!--  <INPUT type='hidden' id='host' name='USERHOSTCLIENT' />--> 
        </form>
    </td></tr>
</table>
<%}%>
</body>
</html>

