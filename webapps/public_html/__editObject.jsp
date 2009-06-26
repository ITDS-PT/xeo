<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.uiObjectManager"%>

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
    if(bosession== null) 
    {
        StringBuffer url = new StringBuffer("?");
        Enumeration oEnum = request.getParameterNames();
        while( oEnum.hasMoreElements() )
        {
            String pname = oEnum.nextElement().toString();
            url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
        }
        response.sendRedirect("login.jsp?returnToPage=__editObject.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
        return;
    }
    if(boctx==null) 
    {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
    }
    String[] ctrl;
    int IDX;int cvui;
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    docHTML doc = DOCLIST.getDOC(IDX);
    String object = request.getParameter("object");
    String boui = request.getParameter("boui");
    
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
    FILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=1, StartColorStr=#FFFFFF, EndColorStr=#6297E5);
    }
</style>
<script LANGUAGE="javascript" SRC="ieLibrary/main.js"></script>
<script>
function ini()
{
 openDoc("","<%=object.toLowerCase()%>","edit","method=edit&object=<%=object%>&boui=<%=boui%>");
 //openDoc("","quest_software","edit","method=edit&object=QUEST_Software&boui=869836");
}
</script>
</head>


<body scroll="no" onload="ini()">



<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    
   
	<col />

	
    <tr>
		<td id='docs' valign="top">
		
	<!--		<iframe src="blank.htm" style="width:100%;height:100%" scrolling="no"></iframe>-->
		
		</td>
		
	</tr>
	
	
	
</table>


<iframe style="display:none" id = bocmds1 src="__cmds.jsp?cmds=1"></iframe>
<iframe style="display:none" id = bocmds2 src="__cmds.jsp?cmds=2"></iframe>
<iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSession.jsp?cmds=3"></iframe>

</body></html>
<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
