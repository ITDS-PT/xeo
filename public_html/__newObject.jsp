<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import=" netgest.bo.presentation.*"%>
<%@ page import=" netgest.bo.presentation.manager.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {


boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null )
{
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__newObject.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
    String[] ctrl;
    int IDX;int cvui;
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    
    docHTML doc = DOCLIST.getDOC(IDX);
    
    
    String objectName=request.getParameter( "objectName" );
    boDefHandler bodef= boDefHandler.getBoDefinition(objectName);
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>novo(a) <%=bodef.getLabel()%></title>

<style type="text/css">
    @import url('ieThemes/0/global/ui-global.css');
    @import url('ieLibrary/scrollContainer/scrollContainer.css');
    @import url('ieThemes/0/scrollContainer/ui-scrollContainer.css');
    @import url('ieThemes/0/taskBar/ui-taskBar.css');
</style>
<script LANGUAGE="javascript" SRC="ieLibrary/main.js"></script>
<script>
function ini()
{
     winmain().openDoc('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','<%=objectName.toLowerCase()%>','edit','searchClientIdx=dialog&method=new&object=<%=objectName%>');
}
</script>
</head>


<body style='padding:4px;background-color:#CCCCCC' scroll="no" onload='ini()'>



<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    <col width=100%/>
	
	
    <tr style="height:60px;">
	 	<td style="font-size:13px;font-weight:bold">
	 		   
	 		   <table style='width:100%;height:100%' cellpadding=0 cellspacing=0 >
                    <tr style='height:30px'>
                        <td style='color:#000000' >
                           Preencha os campo do <b>novo(a)  <%=bodef.getLabel()%></b> e em seguida clique no icon "guardar e fechar" <img hspace=2 vspace=2 align='top' src='templates/menu/std/16_saveClose.gif' />. 
                        </td>
	 		       </tr>
	 		       <tr style='height:24px'>
	 		          <td id="">
	 		               <span class='scrollContainer header' scrollVertical='0' id='containerTaskBar' style='height:100%;width:100%'>
	 							<table id='taskbar' style='height:100%;' cellpadding=0 cellspacing=0>
	 							    <tr>
	 								
	 								</tr>
	 							</table>
	 					   </span>
	 		          </td>
	 		       </tr>
	 		       <tr class="taskUnderline" >
	 					<td>&nbsp;</td>
	 		       </tr>
	 		   </table>
	 		   
	 	</td>
     </tr>
	

	
    <tr>
		<td id='docs' valign="top">
		
	<!--		<iframe src="blank.htm" style="width:100%;height:100%" scrolling="no"></iframe>-->
		
		</td>
		
	</tr>
	
	
	
</table>

<iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSession.jsp?cmds=3"></iframe>
</body></html>
<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
