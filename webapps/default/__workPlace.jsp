<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.uiObjectManager"%>
<%@ page import="netgest.bo.utils.IProfileUtils"%>
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
    response.sendRedirect("login.jsp?returnPage=__workPlace.jsp");
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
    
    
    String workPlaceName=request.getParameter( "name" );
    String img_logo = "ieThemes/0/resources/client-logo.gif";
	String largura = "190";
    String listBarName = null;
	
    long listBarBoui = 0;
    try 
    {    
        long iprofile = bosession.getPerformerIProfileBoui();
        
        boObject work = uiObjectManager.getUiObject( boctx, uiObjectManager.TYPE_WORKPLACE);
		
		if(work.getAttribute("img").getValueString()!=null & work.getAttribute("img").getValueString().trim().length() > 0)
        	img_logo = work.getAttribute("img").getValueString();
        
		if(work.getAttribute("largura").getValueString()!=null & work.getAttribute("largura").getValueString().trim().length() > 0)
        	largura = work.getAttribute("largura").getValueString();
		
        if(work.getAttribute("listbar").getObject()!=null )
        {
        	if( work.getAttribute("listbar").getObject().getAttribute("name").getValueString()!=null ) 
        		listBarName = work.getAttribute("listbar").getObject().getAttribute("name").getValueString();
        	
        	listBarBoui = work.getAttribute("listbar").getValueLong();
        }
    } catch (Exception ex) 
    {
        ex.printStackTrace();
    } 
    
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>

<style type="text/css">
    @import url('ieThemes/0/global/ui-global.css');
    
    @import url('ieLibrary/splitter/splitter.css');
    @import url('ieThemes/0/splitter/ui-splitter.css');
    
    @import url('ieLibrary/scrollContainer/scrollContainer.css');
    @import url('ieThemes/0/scrollContainer/ui-scrollContainer.css');
    @import url('ieThemes/0/taskBar/ui-taskBar.css');
  
    .header
    {
    F-ILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=1, StartColorStr=#FFFFFF, EndColorStr=#6297E5);
    }
</style>
<script LANGUAGE="javascript" SRC="ieLibrary/main.js"></script>
</head>


<body scroll="no" >



<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    
    <col id="dd" width="<%= largura %>">
	<col width="4">
	<col />
	
	
    <tr style="height:47px;background-color:#FFFFFF">
	 	<td>
<%
		if(img_logo!=null && img_logo.trim().length()>0)
		{
			out.print("<img src=\"" + img_logo + "\" WIDTH=\"190\" HEIGHT=\"60\">");
		}
%>
		</td> 
	 	</td>
	 	<td></td>
	 	<td style="font-size:13px;color:white;font-weight:bold">
	 		   
	 		   <table style='width:100%;height:100%' cellpadding=0 cellspacing=0 >
	 		       <tr style='height:24px'>
                        <td id='announceAreaTD' class='header' style="font-size:11px;font-weight:bold">
                           
                        </td>
	 		       </tr>
	 		       <tr style='height:20px'>
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
        <td>
			<iframe src="__workPlaceListBar.jsp?docid=<%=IDX%>&listBoui=<%=listBarBoui%>&listName=<%=listBarName%>" style="width:100%;height:100%" scrolling="no"></iframe>           
        </td>
		<td splitter width="4"><img class="rszLeft ui-rszLeft" src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1"></td>
		
		<td id='docs' valign="top">
		
	<!--		<iframe src="blank.htm" style="width:100%;height:100%" scrolling="no"></iframe>-->
		
		</td>
		
	</tr>
	
	
	
</table>


<iframe style="display:none" id = bocmds1 src="__cmds.jsp?cmds=1"></iframe>
<iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSessionXwf.jsp?cmds=3"></iframe>

</body></html>
<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
