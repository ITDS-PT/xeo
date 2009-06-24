<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*, netgest.bo.utils.SchemaUtils"%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<LINK REL="SHORTCUT ICON" HREF="favicon.ico">
<title>netgest.net Login Page</title>

<%
    boSession netgestSession;
    boolean ok = false;
    String[] schemas = null;
    String fullwindow=request.getParameter("fullwindow");
    String errormessage="";
   
    if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
    {
        netgestSession = (boSession)request.getSession().getAttribute("boSession");
        schemas = SchemaUtils.getSchemas(netgestSession);
        if(schemas.length == 1)
        {
            ok = true;
        }
        if(!ok && request.getParameter("SCHEMA") != null)
        {
            netgestSession.setRepository(boRepository.getRepository(netgestSession.getApplication(),request.getParameter("SCHEMA"))) ;
            ok = true;
        }
    }
    else 
    {
        //retornar a página de login
        response.sendRedirect("login.jsp");
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

</style>
<% if (ok){ 
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
</head>
<body onload="document.getElementById('user').focus()">



<table cellpadding="0" cellspacing="0" width="100%" height="100%" border="0">
    <tr><td align="center">
        <form method='post' action='login.jsp'>
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
                        <tr><td align='right' class="texto">Esquema:</td><td><select name="SCHEMA">
                            <%
                                for(int i = 0; i < schemas.length; i++)
                                {
                                    %>
                                    <option><%=schemas[i]%></option>
                                    <%
                                }
                            %>
                        </td></tr>
                    </table>
                </td>
                <td></td></tr>
                <tr height='20px'><td></td></tr>
                <tr height='30px'>
                    <td></td>
                    <td valign='middle' align='center'>
                        <input class='fldbutton' type='submit' value='Validar'>
                        <input class='fldbutton' type='button' value='Sair' onClick='javascript: window.close()'>
                    </td>
                    <td></td>
                </tr>
                
                <tr height='70px'>
                   
                     <td colspan=3 valign="bottom" align="right">
                        <table width="100%">
                        <tr><td width='1%'></td>
                        <td width='99%' align="right" >
                        <img src='about/power_netgestbo.gif' style="cursor:hand;" onclick="javascript: window.location='http://www.netgest.net';">
                        </td>
                        </tr>
                        </table>
                    </td>
                </tr>
                
                
                
            </table>
        </form>
    </td></tr>
</table>
<%}%>
</body>
</html>

