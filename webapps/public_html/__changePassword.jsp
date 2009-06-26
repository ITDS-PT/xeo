<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*"%>
<%@ page import="netgest.bo.localized.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

    response.setDateHeader ("Expires", -1);
    
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
    
    boolean ok=false;
    java.util.Enumeration oEnum = request.getHeaderNames();
    String errormessage=null;
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null) {
        response.sendRedirect("login.jsp?returnPage=__changePassword.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        
    }
    int IDX;
    int cvui;
    String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    
    if ( request.getParameter("OLD_PASSWORD") != null )
    {
        String inputOldPass=request.getParameter("OLD_PASSWORD");
        String inputNewPass=request.getParameter("NEW_PASSWORD");
        String inputNewPass1=request.getParameter("NEW_PASSWORD1");
        boObject perf=DOC.getObject( bosession.getPerformerBoui() );
        if (! DOC.poolIsStateFull() )
        {
            DOC.poolSetStateFull( DOCLIST.poolUniqueId()); 
        }
        if(!perf.poolIsStateFull())
        {
            perf.poolSetStateFull(DOC.poolUniqueId());
        }
        String oldPass=perf.getAttribute("password").getValueString();
        if ( !oldPass.equals( netgest.utils.MD5Utils.toHexMD5(inputOldPass) ) )
        {
            errormessage=JSPMessages.getString("changePassword.1");
        }
        else
        {
            if ( !inputNewPass.equals( inputNewPass1 ) )
            {
                errormessage=JSPMessages.getString("changePassword.2");
            }
            else
            {
               if ( inputNewPass.equals("") )
               {
                   errormessage=JSPMessages.getString("changePassword.3");
               }
               
            }
        }
        if ( errormessage==null )
        {
            perf.getAttribute("password").setValueString( netgest.utils.MD5Utils.toHexMD5( inputNewPass ) );
            perf.update();
            ok=true;
        }
        perf.poolUnSetStateFull();
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
<%@ include file='boheaders.jsp'%>
<% if (ok)
 { 
    
%>

<script>

function load()
	{
      
     winmain().ndl[getIDX()].close();  
	}
</script>

</head>
<body onload="load()">
<%
}
else {%>
</head>

<script>
var objLabel='<img align="absmiddle" hspace=3 src="resources/Ebo_Perf/ico16.gif">Alterar Password ';
</script>
<body onload="document.getElementById('old').focus()">



<table cellpadding="0" cellspacing="0" width="100%" height="100%" border="0">
    <tr><td align="center">
        <form method='post'>
            <table border="0" width="100%" cellpadding="0" cellspacing="0">
                
                <tr><td></td>
                <td align="center">
                    <table border="0">
                    <% if ( errormessage!=null) { %>
                    <tr><td colspan=2 class='erro'><%=errormessage%></td></tr>
                    <%}%>
                        <tr><td align='right' >JSPMessages.getString("changePassword.4")</td><td><input type='password' id='old' name='OLD_PASSWORD'></td></tr>
                        
                        <tr><td align='right' >JSPMessages.getString("changePassword.5")</td><td><input type='password' name='NEW_PASSWORD'></td></tr>
                        <tr><td align='right' >JSPMessages.getString("changePassword.6")</td><td><input type='password' name='NEW_PASSWORD1'></td></tr>
                    </table>
                </td>
                <td></td></tr>
                <tr height='20px'><td></td></tr>
                <tr height='30px'>
                    <td></td>
                    <td valign='middle' align='center'>
                        <input class='fldbutton' type='submit' value='<%=JSPMessages.getString("changePassword.7")%>'>
                        <input class='fldbutton' type='button' value='<%=JSPMessages.getString("changePassword.8")%>' onClick='javascript:winmain().ndl[getIDX()].close()'>
                    </td>
                    <td></td>
                </tr>
                
            </table>
        </form>
    </td></tr>
</table>
<%}%>
</body>
</html>

<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
