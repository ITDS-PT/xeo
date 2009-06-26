<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>

<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=gestTempFillParameters.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    int IDX;int cvui;
    String idbolist;
    String[] ctrl;
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"BasicController");
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    ArrayList erros = new ArrayList();
    
    String strDocTypeBoui = request.getParameter("docType");
    long docTypeBoui  = Long.parseLong(strDocTypeBoui);
    boObject classObj = null, groupClass = null;
    
    if(docTypeBoui > 0)
    {
        classObj = boObject.getBoManager().loadObject(DOC.getEboContext(), docTypeBoui);
        groupClass = classObj.getAttribute("grupo").getObject();
    }
    
    String firstFieldID = "";/*GtTemplateViewer.getFistFieldID(template);*/

    //operation
    String operation = request.getParameter("operation");
    
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<title></title>
<script>
    var objLabel="<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='Modelos' src='resources/GESTEMP_Template/ico16.gif' width='16' height='16'/><span title='Introdução de Parâmetros'>Introdução de Parâmetros</span>"
    function keyPressed(e, buttonName)
    {
        if(e)
        {
            var keycode = e.keyCode;
            if(keycode == 32 || keycode == 13)
            {
                 sendPage(buttonName);
            }
        }
    }
    function mouseDown(e, buttonName)
    {
        if(e && event.button == 1)
        {
             sendPage(buttonName);
        }
    }
    
    function sendPage(buttonName)
    {
        boForm.operation.value=buttonName;
        boForm.submit();
        wait();
    }
    
    function setPageFocus(fieldName)
    {
        loadField(fieldName);
        treatFocus();
    }
    
    function uncheckOthers(fieldName)
    {
        var elems = document.getElementsByTagName('input');
        for ( var i=0; i< elems.length ;i++)
        {
            if(elems[i].type == 'checkbox')
            {
                if(elems[i].checked && elems[i].id != fieldName)
                {
                    elems[i].value=0;elems[i].original=1;
                    elems[i].checked = false;
                }
            }
        }
    }

</script>
<%= GtTemplateViewer.writeJS() %>

<%@ include file='boheaders2.jsp'%>
<body onload='setPageFocus("<%=firstFieldID%>")'>
<TABLE id="dBody" class="layout" style='BACKGROUND-COLOR:#F2F7FA;' cellspacing="0" cellpadding="0">
<TBODY>
<%
if(erros.size() > 0)
{
%>
<TR>
    <TD class='error'>
        <div class='error'>
            <span style='font:13px'>&nbsp;&nbsp;<b>Corrija os seguintes erros:</b></span><br>
            <%
            for (int i = 0; i < erros.size(); i++) 
            {
            %>
                <%=(String)erros.get(i)%>
            <%
            }
            %>
        </div>
    </TD>
</TR>
<%
}
%>
<TR height="100%" >
    <TD style="height:100%;width:100%;" valign=top align=left>
        <DIV style='overflow-y:auto;height:100%;'>
        
        <form class="objectForm" name="boForm" id="<%=IDX%>" >
            <INPUT type='hidden' value='' name='operation' id = 'operation' />
            <INPUT type='hidden' value='' name='inputObjectBoui' />
            <INPUT type='hidden' value='<%=IDX%>' name='docid' />
        
        </form>
        </DIV>
    </TD>
</TR>
</TBODY>
</TABLE>
<FORM name='boFormSubmit' method='post'>
    
    <%
        java.util.Hashtable options = new java.util.Hashtable();    
    %>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM> 
</body>
</html>
<%
} finally {
     boctx.close();
     DOCLIST.releseObjects(boctx);
}
%>
