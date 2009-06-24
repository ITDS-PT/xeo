<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

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
if(bosession== null)
{

    StringBuffer url = new StringBuffer("?");
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String pname = oEnum.nextElement().toString();
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) );
    }
    response.sendRedirect("login.jsp?returnToPage=boql_result.jsp"+ java.net.URLEncoder.encode(url.toString() ));
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
%>
<html>
  <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
  <title>SGIS-HomePage</title>
  <style>v\:* { behavior: url("#default#VML"); }</style>
  <xml:namespace NS="urn:schemas-microsoft-com:vml" PREFIX="v"/>
  <style type="text/css">
    @import url('bo_global.css');
    @import url('templates/doc/std/bo_doc_std.css');
    @import url('templates/grid/std/bo_grid_std.css');
    @import url('templates/menu/std/bo_menu_std.css');

body{
	BORDER-RIGHT: 0px;
	BORDER-TOP: 0px;
	FONT-SIZE: 11px;
	MARGIN: 0px;
	BORDER-LEFT:0px;-classpath c:\oc4j10g\j2ee\home\oc4j.jar -Dnetgest.home=c:\netgest\projects\bo\ -Dhttp.proxyHost=192.168.131.5 -Dhttp.proxyPort=8080 -Dhttp.nonProxyHosts=*.nca.pt|*.enlace3.pt|192.*|*.itds.pt|localhost|127.0.0.1 -Xverify:none -Ddisable.checkForUpdate=true -Doracle.j2ee.dont.use.memory.archive=true -Doracle.j2ee.http.socket.timeout=500 -Doracle.dms.sensors=NONE -Doc4j.jms.usePersistenceLockFiles=false com.evermind.server.OC4JServer -config c:\oc4j10g\j2ee\home\config\server.xml -userThreads 
	CURSOR: default;
	BORDER-BOTTOM: 0px;
	FONT-FAMILY: Verdana, Arial;
	BACKGROUND-COLOR: #F2F7FA;
}


</style>

<script LANGUAGE="javascript" SRC="bo_config.js" ></script>
<script LANGUAGE="javascript" SRC="bo_main.js"></script>
<script LANGUAGE="javascript" SRC="xeo.js"></script>

<script LANGUAGE="javascript">

var controlArea=null;
function ini(){

//openDoc("small","ebo_template","list","method=list&boql="+escape("SELECT Ebo_Template WHERE 1=1"),null,null,null,null);

	//openDoc("medium","activity","edit","method=edit&boui=1262",null,null,null);

openWALL("wallp_boql");

	//openDoc("medium","activity","edit","method=new&object=activity",null,null,null);
//	openDoc("medium","activity","edit","method=edit&boui=1262");
//openDoc("medium","activity","list","method=list&boql="+escape("SELECT ACTIVITY WHERE id=id"),null,null,null,null,"lookup");



//	openDoc("medium","process","list","method=list&boql="+escape("SELECT process WHERE id=id"));		
	//openDoc("medium","process","edit","method=edit&boql="+escape("SELECT process WHERE id=id"));		
}

function load(obj)
{
        top.main.closeWALL();
        top.main.openWALL(obj);
				
}

</script>
  <% 
  boObject perf=boObject.getBoManager().loadObject(boctx,bosession.getPerformerBoui());
%>
  <body scroll="no" onload="ini()">
<div style="position:absolute;top:34;left:70%;rigth:10%;"><span id='announceArea' onclick="winmain().openDocUrl('medium','__showAnnounce.jsp','','lookup')" style="text-decoration:underline;cursor:hand;color:#000000;font-weight:bolder"><%=netgest.bo.impl.Ebo_AnnounceImpl.getAnnounces( perf )%></span></div>
    <table height="100%" cellspacing="0" width="100%" style="table-layout:fixed">
      <tr height="100%">
        <td style="height:100%" id="BODY">
          <div id="NGTBODY" style="position:relative;height:100%;width:100%"/>
        </td>
      </tr>
    </table>
    <div id="announceArea" style="display:none" />

    
    <iframe style="display:none" id = bocmds1 src="__cmds.jsp?cmds=1"></iframe>
    <iframe style="display:none" id = bocmds2 src="__cmds.jsp?cmds=2"></iframe>
    <iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSession.jsp?cmds=3"></iframe>


  </body>
</html>
<% 
} finally {
  if (boctx!=null)boctx.close();
  if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}%>
