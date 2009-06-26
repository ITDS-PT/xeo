<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*, netgest.bo.utils.SchemaUtils"%>
<%
    if(netgest.bo.builder.boBuilder.isRunning())
    {
%>
<jsp:forward page="__buildRunning.jsp"/>
<%
    }
%>
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
    boolean showSchema=false;
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
        fullwindow="on";
    }        
    String[] schemas = null;
   
    boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
    String ssi = request.getParameter("ssi");
    bosession = boSessionLessManager.getSessionById( ssi );
    if ( bosession != null )
    {
         ok=true;
    }
//    if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
//    {
//        bosession = (boSession)request.getSession().getAttribute("boSession");
//        if(bosession.getRepository() != null && bosession.getRepository().getName().equals(request.getParameter("SCHEMA")))
//        {
//            ok=true;
//        }
//    }
    if(AUTO_LOGIN) {
        bosession = boapp.boLogin("SYSUSER","ABC");
        
        //request.getSession().setAttribute("boSession",bosession);
        boSessionLessManager.putSession( bosession );
        String refer = request.getParameter("returnPage");
        if(refer==null) refer = "";
        response.sendRedirect(refer);
        return;
    }
    String errormessage="";
    if ( !ok )
    {
        String xxx= (String) request.getParameter("USERNAME");
        String xxx1= (String) request.getParameter("PASSWORD");
        
        if((request.getParameter("USERNAME")!=null && request.getParameter("PASSWORD") != null) )
        {
            try {
//                if(request.getSession() != null && request.getSession().getAttribute("boSession")!=null)
//                {
//                    bosession = (boSession)request.getSession().getAttribute("boSession");
//                }
//                else
//                {
                    bosession = boapp.boLogin(request.getParameter("USERNAME"),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request);
                    
                   // request.getSession().setAttribute("boSession",bosession);
                   
                   if ( bosession!=null )
                   {
                        boSessionLessManager.putSession( bosession );
                        ssi=bosession.getId();
                    }
//                }
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
                if(!ok && request.getParameter("SCHEMA") != null)
                {
                    if(!"default".equalsIgnoreCase(request.getParameter("SCHEMA")))
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),request.getParameter("SCHEMA"))) ;
                    }
                    ok = true;
                }
            } catch (netgest.bo.system.boLoginException e) {
                if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                    errormessage = "Ocorreu um erro inesperado ao efectuar login";
                } else {
                    errormessage = "Nome de utilizador ou palavra passe inválida.";
                }
            } catch (Exception e) {
                errormessage = "Ocorreu um erro inesperado ao efectuar login";
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
                    bosession = boapp.boLogin(request.getRemoteUser(),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request );
                    //request.getSession().setAttribute("boSession",bosession);
                    boSessionLessManager.putSession( bosession );
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
                if(!ok && request.getParameter("SCHEMA") != null)
                {
                   if(!"default".equalsIgnoreCase(request.getParameter("SCHEMA")))
                    {
                        bosession.setRepository(boRepository.getRepository(bosession.getApplication(),request.getParameter("SCHEMA"))) ;
                    }
                    ok = true;
                }
            } catch (netgest.bo.system.boLoginException e) {
                if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                    errormessage = "Ocorreu um erro inesperado ao efectuar login";
                } else {
                    errormessage = "Nome de utilizador ou palavra passe inválida.";
                }
            } catch (Exception e) {
                errormessage = "Ocorreu um erro inesperado ao efectuar login";
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
    request.getSession().removeAttribute("firstTime");
    if(request.getParameter("returnToPage")!=null && !request.getParameter("returnToPage").equals(""))
    {
        response.sendRedirect( ((String)request.getParameter("returnToPage"))+"&ssi="+ssi );
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
			alert("É necessário uma resolução minima de 800X600")
            
		}
		
		
		// Carrega GS
		window.open("main.jsp","GS<%=numb%>", "height=" + (window.screen.availHeight - 60) + ", width=" + (window.screen.availWidth - 14) + ", top=0, left=0, menubar=0, location=0, resizable=1, status=1");
		

		// Do not close the window if it is the Application
		if (window.name != "GS<%=numb%>")
		{
			// Destroy this Window
			    var oMe = window.self;
				oMe.opener = window.self;
				oMe.close();
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
        <form method='post' action='login_ssi.jsp' name='loginForm'>
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
                        <tr><td align='right' class="texto">Utilizador:</td><td><input type='text' id='user' name='USERNAME' disabled="disabled" value="<%=bosession.getUser().getUserName()%>"></td></tr>
                        <tr></tr>
                        <tr><td align='right' class="texto">Esquema:</td><td><select id='schema' size='1' name="SCHEMA" class="combo">
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
                        }
                        else
                        {
                        %>
                            <tr><td align='right' class="texto">Utilizador:</td><td><input type='text' id='user' name='USERNAME'></td></tr>
                            <tr></tr>
                            <tr><td align='right' class="texto">Password:</td><td><input type='password' name='PASSWORD'></td></tr>
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
                        <input class='fldbutton' type='submit' value='Validar' onclick='markButtonClick()'>
                        <input class='fldbutton' type='button' value='Sair' onClick='javascript: window.close()'>
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
                            <span class="textopq"><nobr>Abrir em janela Inteira</nobr></span><input type='checkbox' checked name='fullwindow'/>
                        <%
                        }
                        else
                        {
                        %>
                            <span class="textopq"><nobr>Abrir em janela Inteira</nobr></span><input type='checkbox' name='fullwindow'/>
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

