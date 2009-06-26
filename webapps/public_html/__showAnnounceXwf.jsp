<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.xwf.core.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*"%>
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
        response.sendRedirect("login.jsp?returnToPage=__showAnnounceXwf.jsp");
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
    
    String operation=request.getParameter("operation");
    
    boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
    
    if ( operation != null )
    {
        if ( operation.equalsIgnoreCase("my") )
        {
            xwfAnnounceImpl.removeAnnounces( perf  , netgest.xwf.core.xwfAnnounceImpl.MY_MESSAGES );
        }
        else if ( operation.equalsIgnoreCase("mygroup") )
        {
            xwfAnnounceImpl.removeAnnounces( perf  , netgest.xwf.core.xwfAnnounceImpl.MY_GROUP_MESSAGES );
        }
        else if ( operation.equalsIgnoreCase("myrole") )
        {
            xwfAnnounceImpl.removeAnnounces( perf  , netgest.xwf.core.xwfAnnounceImpl.MY_ROLE_MESSAGES );
        }
        else if ( operation.equalsIgnoreCase("myworkqueue") )
        {
            xwfAnnounceImpl.removeAnnounces( perf  , netgest.xwf.core.xwfAnnounceImpl.MY_WORKQUEUE_MESSAGES );
        }
        else if ( operation.equalsIgnoreCase("all") )
        {
           xwfAnnounceImpl.removeAnnounces( perf  ,
               (byte) (xwfAnnounceImpl.MY_WORKQUEUE_MESSAGES+
                xwfAnnounceImpl.MY_GROUP_MESSAGES+
                xwfAnnounceImpl.MY_ROLE_MESSAGES+
                xwfAnnounceImpl.MY_MESSAGES)
           );
        }
    
    }
    
    
%>

<style>
.fldbutton {
    border: 1px solid #6283A3;
    PADDING-RIGHT: 5px;
    color: #000000;
    PADDING-LEFT: 5px;
    FONT: 11px;
    FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr=#ffffff, EndColorStr=#DBDBD1);
    CURSOR: hand;
    HEIGHT:20px;
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
</head>

<script>
var objLabel='<img align="absmiddle" hspace=3 src="resources/xwfAnnounceDetails/ico16.gif">Mensagens SGIS ';

function setAnnounce()
{

parent.parent.announceArea.innerText='<%=netgest.xwf.core.xwfAnnounceImpl.getAnnounces( perf )%>';
}

</script>
<body onload="setAnnounce()">

<table style='layout:fixed;background-color:#EEEEEE' cellpadding="10" cellspacing="0" width="100%" height="100%" border="0">
   <col width='100%'/>
    <tr height='100%'>
        <td  >
          
             <div style='border:1px solid #5991E4'width="100%" height="100%">
				<IFRAME id='myannounces' src='xwfannouncedetails_generalmyannounces.jsp?docid=<%=IDX%>&form=myAnnounces' frameBorder=0 width='100%' scrolling=no height='100%'></IFRAME>
             </div>
          
        </td>
    </tr>
    <tr height='35px'>
        <td style='background-color:#EEEEEE'>
               <span style="padding-left:10px;font-weight:bolder">Limpar mensagens</span>&nbsp;
               
               <button onclick="boForm.operation.value='my';boForm.submit()" class='fldbutton' >minhas</button>&nbsp;
               <button onclick="boForm.operation.value='mygroup';boForm.submit()" class='fldbutton' >do meu grupo</button>&nbsp;
               <button onclick="boForm.operation.value='myrole';boForm.submit()" class='fldbutton' >da minha função</button>&nbsp;
               <button onclick="boForm.operation.value='myworkqueue';boForm.submit()" class='fldbutton' >da minha pool</button>&nbsp;
               <button onclick="boForm.operation.value='all';boForm.submit()" class='fldbutton' >todas</button> 

        </td>
    </tr>
          
               
</table>
<form style='display:none' name="boForm"  method="POST" >
  <input name="operation" type="HIDDEN" value=""/>
</form>

</body>
</html>

<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
