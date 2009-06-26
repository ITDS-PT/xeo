<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
 
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    String bouiToApplyTemplate  = request.getParameter("bouiToApplyTemplate");
    String templateBoui         = request.getParameter("selectedBouis");

  
%>

<html>

<SCRIPT language=javascript src="bo_global.js"></SCRIPT>
<script language="javascript">

  function submitDocument(){

   var windowToUpdate="<%=clientIDX%>";

   var xok=false;
   var w=parent.ndl[windowToUpdate];
   if(w){
   
       var ifrm=parent.document.getElementById('frm$<%=clientIDX%>');
       if ( ifrm && ifrm.contentWindow.boForm )
       {
          ifrm.contentWindow.createHiddenInput( "applyTemplate", <%=templateBoui%>);
          ifrm.contentWindow.boForm.BindValues();
       }
   }
   
   try{
       parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
 
 
 
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<body onload="submitDocument()">
<%
if( DOC.haveErrors() )
{
%>
<textarea id="errorText">
<%=DOC.getHTMLErrors()%>
</textarea>
<script>
    newDialogBox("critical",errorText.value,["Continuar" ]," Atenção, Ocorreu um erro na aplicação!!!");
</script>    
<%
}
%>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
