<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=false;
String option = null;

docHTML_controler doc = ( docHTML_controler )request.getSession().getAttribute( "DOCLIST" );
try 
{
/*
if( doc != null )
{
    ngtOut.DebugInf("Home page refresh.. release all objects:" + doc.poolUniqueId());
    boPoolManager.realeaseAllObjects( doc.poolUniqueId() );
}
*/

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnToPage=homeXdev.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
%>
<html>

<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<title>XDeveloper-HomePage</title>
<style>v\:* { behavior: url("#default#VML"); }</style>
<xml:namespace NS="urn:schemas-microsoft-com:vml" PREFIX="v" />
<style type='text/css'>
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
</head>
<script LANGUAGE='javascript' SRC='bo_config.js'></script>
<script LANGUAGE='javascript' SRC='bo_main.js'></script>

<script LANGUAGE='javascript'>
var controlArea=null;
function ini(){
openWALL("XDV_WallObjMap");		
}

function load(obj)
{
        top.main.closeWALL();
        top.main.openWALL(obj);
				
}
</script>
<%
  boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
%>

<body scroll='no' onload="ini()">
<DIV style="position:relative;z-Index:500;height:24px;width:100%">
<table style='width:100%' class='layout' cellSpacing='0' cellPadding='0'>
    <tbody>
        <tr height='24'>
            <td>                        
                <table class='mnubarFlat' id='mnuBar1' cellSpacing='0' cellPadding='0'>
                    <tbody>
                    <tr><td width='9'><img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/></td>
                    <td class='icMenu' noWrap='1'>
                        <span class='menu' tabIndex='1' accessKey='F'  menu='SUBMENU_OBJECTS'><u>F</u>icheiros
                        <table class='mnuList' id='SUBMENU_OBJECTS' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>    
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallObjMap", bosession.getPerformerBoui() )) { %>  
                                <tr tabIndex='0'  accessKey='M'  onclick="load('XDV_WallObjMap');"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>M</u>apa Objectos</td></tr>
                                <%}%>
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallBuilder", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='B'  onclick="load('XDV_WallBuilder')"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>B</u>uilder</td></tr>
                                <%}%>
                   
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallBuilderApplet", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='B'  onclick="load('XDV_WallBuilderApplet')"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>B</u>uilder Applet</td></tr>
                                <%}%>
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallRefactoring", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='R'  onclick="load('XDV_WallRefactoring')"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>R</u>efactoring</td></tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallObjDelete", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='R'  onclick="load('XDV_WallObjDelete')"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Remover Objecto</td></tr>
                                <%}%>
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallAdHoc", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="load('XDV_WallAdHoc')"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Criação <u>S</u>ysuser</td></tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallInvTables", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="load('XDV_WallInvTables')"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Tabelas Inválidas</td></tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallInvTables", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="load('XDV_EboRebuild')"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Integridade Esquema XEO </td></tr>
                                <%}%>
                                <!--
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"XDV_WallInvBoui", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="load('XDV_WallInvBoui')"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Boui Inválidos</td></tr>
                                <%}%>-->
                                <tr tabIndex='0'  onclick="window.close()"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Fechar</td></tr>
                           </tbody>
                        </table>
                        </span>                                                                       
                        <span class='menu' tabIndex='0' menu=SUBMENU_AJUDA accessKey='A'><u>A</u>juda
                        <table class='mnuList' id='SUBMENU_AJUDA' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="window.open('help/manual_sgis.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'>Manual</td></tr>
                             <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>    
                              <tr tabIndex='0'  onclick="window.showModalDialog('about/about.htm','0.5','dialogHeight: 390px; dialogWidth: 610px;edge: Raised; center: Yes; help: No; resizable: no; status: no;');"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Acerca do SGIS</td></tr>
                            </tbody>
                         </table>
                        </span>
                        
                    </td>
                    <td class='mnuTitle mnuRight' id='tdTitle' noWrap='1'>
                    <span title=''>Bem vindo(a), <%=perf.getAttribute("name").getValueString()%></span></span>
                    </td>
                    </tr>
                    </tbody>
               </table>
                
              
            </td>
            
        </tr>
    </tbody>
</table>
</DIV>
<DIV id="NGTBODY" border="2"  style="position:relative;z-Index:10;height:98%;width:100%;"></DIV>
<iframe style="display:none"  scrolling="auto" id = bocmds1 src=""></iframe>
<iframe style="display:none" scrolling="auto" id = bocmds2 src=""></iframe>
<iframe style="display:none" scrolling="auto" onreadystatechange=verifyCmdsSession() id = controlSession src="objectMap.jsp"></iframe>
</body>
</html>
<%
} finally {
    if (boctx!=null)boctx.close();
    if (boctx!=null && doc!=null)doc.releseObjects(boctx);
}
%>

