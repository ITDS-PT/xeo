<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.xwf.common.*"%>
<%@ page import="netgest.bo.message.*"%>
<%@ page import="netgest.bo.impl.document.print.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>



<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

    try {
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            response.sendRedirect("login.jsp");
         return;
    }

    if(boctx==null) 
    {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    int IDX;
    int cvui;
  
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    String[] selectBouis = null;
    String[] printBouis = null;
    boolean toPrint = false;    
    boObject program = null;
    boObject message = null;
    docHTML DOC = DOCLIST.getDOC(IDX);

    boolean xeoControl=PrintHelper.isXeoControlActive(boctx);
    if ( request.getParameter("printBouis")!=null )
    {
        toPrint=true;
        printBouis = ((String)request.getParameter("printBouis")).split(";");
        String flow = null;        
        boObjectList list = null;
        boObject document = null;
        
        if(XwfKeys.CONTROLLER_NAME_KEY.equals(DOC.getController().getName()))
        {                
            message = xwfHelper.getMasterObject(((XwfController)DOC.getController()).getEngine(),((XwfController)DOC.getController()).getRuntimeActivity());
            if("message".equals(message.getName()) || "message".equals(message.getBoDefinition().getBoSuperBo()))
            {
                MessageServer.mergeMessage(((XwfController)DOC.getController()).getEngine(),message,((XwfController)DOC.getController()).getRuntimeActivityBoui());
            }
        }            
        if(!xeoControl)
        {        
            for (int i = 0; i < printBouis.length ; i++) 
            {
                document = DOC.getObject( ClassUtils.convertToLong( printBouis[i] ));            
                PrintHelper.printDocument(document);
            }
        }
    }
    
    if ( request.getParameter("selectedObjects")!=null && !"".equals(request.getParameter("selectedObjects")))
    {
        selectBouis = ((String)request.getParameter("selectedObjects")).split(";");
    }

     
%>
<% if( true ){%>
<html>
<style>
 @import url('ieThemes/0/global/ui-global.css');
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 @import url('xeoNew.css');
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Imprimir Documentos Seleccionados</title>
<base target="_self">
</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

</head>
<script>

var objectAttributes = [];
function init()
{
  <%
  if(xeoControl && request.getParameter("printBouis")!=null)
  {
  %>
    xeoControlprint();
    window.close();
  <%
  }else if ( toPrint ){
  %>    
        window.close();
<%}%>   
}

function choose()
   {
     //dialogArguments
     var bouisToPrint ="";
     for ( var i=0 ; i< boForm.elements.length;i++ )
     {
       if( boForm.elements[i].checked )
       {
            if(bouisToPrint!="")bouisToPrint+=";";
           bouisToPrint+=boForm.elements[i].id
       }
     }
     if ( bouisToPrint=="" )
     {
        alert("Não seleccionou nenhum documento para imprimir")
        window.close();   
     }
     else
     {
        boFormSubmit.printBouis.value=bouisToPrint;
        boFormSubmit.submit();
     }
     
     //window.close();
   }
function xeoControlprint()
{
    <%
        if(xeoControl && request.getParameter("printBouis")!=null)
        {
            String docBouis = (String)request.getParameter("printBouis");
    %>
                dialogArguments[0].top.XEOControl.documentManager.PrintWordDocument('<%=DOC.getEboContext().getBoSession().getId()%>','<%=DOC.getDochtmlController().poolUniqueId()%>|<%=DOC.getDocIdx()%>',<%=message.getBoui()%>,'<%=docBouis%>');
    <%
        }
    %>
}
</script>


<body scroll="no" onload="init()">

<form name="boForm">
<table class='layout' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;color:#000000">
    <col width=35px/>
    <col width=100%/>
    
    <tr height=25px>
        <td colspan=3 class='docHeaderTit_stdSEL'>Imprimir Documentos Seleccionados</td>
    </tr>
    <tr height=35px>
        <td class='gh_std'></td>
        <td class='gh_std'>Objecto</td>
    </tr>
   <tr height=100%><td colspan=3>
        <div id='container' style='height:100%;width:100%;padding:0px;overflow:auto'>   
            <table id='attrs' class='layout' cellSpacing="0" cellPadding="4" style="table-layout:fixed;background-color:#FFFFFF;color:#000000">
            <col width=5px/>
            <col width=30px/>
            <col width=100%/>
            <%
            if(selectBouis != null)
            {
                for (int i = 0; i < selectBouis.length; i++) 
                {
             %>
             <tr >
            <td style='border-bottom:1px solid #CCCCCC' valign=top >&nbsp;</td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><input id='<%=selectBouis[i]%>' style='border:0' checked type="CHECKBOX"></input></td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><%=DOC.getObject( ClassUtils.convertToLong(selectBouis[i]) ).getCARDID()%> </td>
            </tr>
                <%}%>
            <%}%>
             <tr style='height:100%'>
             <td colspan=3/>
             </tr>
            </table>
        </div>
    </td>
    </tr>

    <tr style='height:35px;'  class='docFooter_std'>
        <td colspan=3  style='border-top:2px solid #CCCCCC'><center>
            <button onclick='choose();' >Imprimir documentos seleccionados</button> &nbsp; &nbsp; &nbsp; &nbsp;
             <button onclick='window.close()'>Cancelar</button></center>
        </td>
    </tr>
</table>

</div>
</form>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value="" name='printBouis' />
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>

</body></html>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
