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
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
    }
    response.sendRedirect("login.jsp?returnToPage=__viewObject.jsp"+ java.net.URLEncoder.encode(url.toString() ,"UTF-8"));
    return;
}
if(boctx==null) 
{
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

String boui=request.getParameter("boui");
String method=request.getParameter("edit");
String object=request.getParameter("object");

%>
<html>

<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<title>XEO Object</title>
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
	BORDER-LEFT:0px; 
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


openDoc("medium","<%=object.toLowerCase()%>","edit","method=edit&object=<%=object%>&boui=<%=boui%>");

}


</script>
<%
  boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
%>

<body scroll='no' onload="ini()">

<DIV id="menuAPP" style="position:absolute;z-Index:500;top:0;height:24px;width:100%">   
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
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_LOV", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='0'  accessKey='L' onclick="winmain().openDoc('small','ebo_lov','list','method=list&boql=SELECT+Ebo_LOV+WHERE+1%3D1',null,null,null,null);">
                                    <td>&nbsp;</td><td class='mnuItm'><u>L</u>ista de Valores</td></tr>
                                <%}%>    
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Template", bosession.getPerformerBoui() )) { %>  
                                <tr tabIndex='0'  accessKey='M'  onclick="winmain().openDoc('small','ebo_template','list','method=list&boql=SELECT+Ebo_Template+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>M</u>odelos</td></tr>
                                <%}%>    
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"workQueue", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='P'  onclick="winmain().openDoc('small','workqueue','list','method=list&boql=SELECT+workQueue+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>P</u>ool de Recursos</td></tr>
                                <%}%>
                                
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"TI_SwApp", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="winmain().openDoc('small','ti_swapp','list','method=list&boql=SELECT+TI_SwApp+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Aplicações <u>S</u>oftware</td></tr>
                                <%}%>
                                
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"TI_HwUnit", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='H'  onclick="winmain().openDoc('small','ti_hwunit','list','method=list&boql=SELECT+TI_HwUnit+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'><u>H</u>ardware</td></tr>
                                <%}%>
                               
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"TI_SwContrat", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  onclick="winmain().openDoc('small','ti_swcontrat','list','method=list&boql=SELECT+TI_SwContrat+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Contrato de Software</td></tr>
                                <%}%>
                                
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"TI_HwContrat", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  onclick="winmain().openDoc('small','ti_hwcontrat','list','method=list&boql=SELECT+TI_HwContrat+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Contrato de Hardware</td></tr>
                                <%}%>
                                
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ANC_srvcom", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='S'  onclick="winmain().openDoc('small','anc_srvcom','list','method=list&boql=SELECT+ANC_srvcom+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'><u>S</u>erviço de Comunicações</td></tr>
                                <%}%>
                               
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ANC_entity", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  accessKey='E'  onclick="winmain().openDoc('small','anc_entity','list','method=list&boql=SELECT+ANC_entity+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'><u>E</u>ntidades</td></tr>
                                <%}%>
                                
                                <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Perf", bosession.getPerformerBoui() )) { %> 
                                <tr tabIndex='0'  accessKey='U'  onclick="winmain().openDoc('small','ebo_perf','list','method=list&boql=SELECT+Ebo_Perf+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>U</u>tilizadores</td></tr>
                                <%}%>
                               <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Role", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_role','list','method=list&boql=SELECT+Ebo_Role+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Funções</td></tr>
                               <%}%>
                               <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Group", bosession.getPerformerBoui() )) { %>
                               <tr tabIndex='0'  accessKey='G'  onclick="winmain().openDoc('small','ebo_group','list','method=list&boql=SELECT+Ebo_Group+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>G</u>rupos</td></tr>
                                <%}%>
                               <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                               
                               <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Policy", bosession.getPerformerBoui() )) { %> 
                               <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_policy','list','method=list&boql=SELECT+Ebo_Policy+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Politicas de Segurança/td></tr>
                                <%}%>
                               <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_ClsReg", bosession.getPerformerBoui() )) { %> 
                               <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_clsreg','list','method=list&boql=SELECT+Ebo_ClsReg+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Objectos</td></tr>
                                <%}%>    
                                <tr tabIndex='0'  onclick="window.close()"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Fechar</td></tr>
                           </tbody>
                        </table>
                        </span>
               <% if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "ISM" ) ){ %>
                         <span class='menu' tabIndex='1' accessKey='G'  menu='SUBMENU_OBJECTS2'><u>G</u>est?de Seguros
                        <table class='mnuList' id='SUBMENU_OBJECTS2' cellSpacing='0' cellPadding='3'>
                        <colgroup/><col class='mnuLeft'/><col/>
                        <tbody>
	                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"DMS_Car", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='2'  accessKey='V' onclick="load('ISM_WallCar');">
	                                    <td>&nbsp;</td><td class='mnuItm'><u>V</u>eículo</td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ISM_WallInstallation", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='3'  accessKey='I' onclick="load('ISM_WallInstallation');">
	                                    <td>&nbsp;</td><td class='mnuItm'><u>I</u>nstalações
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Perf", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='4'  accessKey='T' onclick="load('ISM_WallWorker');">
	                                    <td>&nbsp;</td><td class='mnuItm'><u>T</u>rabalhador</td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"DMS_Trip", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='5'  accessKey='G' onclick="load('ISM_WallTrip');">
	                                    <td>&nbsp;</td><td class='mnuItm'>Via<u>g</u>em</td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"DMS_Merchandise", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='6'  accessKey='E' onclick="load('ISM_WallMerchand');">
	                                    <td>&nbsp;</td><td class='mnuItm'>M<u>e</u>rcadorias Transportadas</td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"IMO_PatrFilatelico", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='7'  accessKey='R' onclick="load('ISM_WallFilatelico');">
	                                    <td>&nbsp;</td><td class='mnuItm'>Pat<u>r</u>imómio Filatélico</td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"DMS_Events", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='8'  accessKey='O' onclick="load('ISM_WallEvent');">
	                                    <td>&nbsp;</td><td class='mnuItm'>Event<u>o</u></td>
	                                 </tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ISM_ApoliceObject", bosession.getPerformerBoui() )) { %>
					<tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
						<tr tabIndex='9'  accessKey='C' onclick="load('ISM_WallApolices');">
							<td>&nbsp;</td><td class='mnuItm'>Apóli<u>c</u>es</td>
						</tr>
						<tr tabIndex='10'  accessKey='C' onclick="load('ISM_WallTaxs');">
							<td>&nbsp;</td><td class='mnuItm'>Taxas/Encargos</td>
						</tr>
                                <%}%>
                                 <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
    						    <tr tabIndex='7'  accessKey='O' onclick="load('wallp1');">
	                                   <td>&nbsp;</td><td class='mnuItm'>Mesa de Trabalho</td>
	                             </tr>

                        </tbody>
                        </table>
                        </span>                     
                   
                <%}%>
                        <span class='menu' tabIndex='0' menu=SUBMENU_TOOLS accessKey='T'>Ferramen<u>t</u>as
                        <table class='mnuList' id='SUBMENU_TOOLS' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="winmain().openDocUrl('small','__changePassword.jsp','','std') ">
                                   <td>&nbsp;</td><td class='mnuItm'>Alterar password</td></tr>

                             <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Filter", bosession.getPerformerBoui() )) { %>
                               <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','ebo_filter','list','method=list&boql=SELECT+Ebo_Filter+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>F</u>iltros</td></tr>
                             <%}%>
                              
                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Schedule", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='I' onclick="winmain().openDoc('small','ebo_schedule','list','method=list&boql=SELECT+Ebo_Schedule+WHERE+1%3D1',null,null,null,null);">
                                   <td>&nbsp;</td><td class='mnuItm'><u>A</u>gendamentos</td>
                                </tr>
                            <%}%>        
                             
                            </tbody>
                            </table>
                        </span>
                        
                        <span class='menu' tabIndex='0' menu=SUBMENU_AJUDA accessKey='A'><u>A</u>juda
                        <table class='mnuList' id='SUBMENU_AJUDA' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="window.open('help/manual_sgis.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'>Manual</td></tr>
                                
	                                <tr tabIndex='4'  accessKey='I' onclick="load('wall_knowledge');">
	                                    <td>&nbsp;</td><td class='mnuItm'><u>I</u>nformações</td>
	                                 </tr>
                                                             
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
<div style="position:absolute;top:34;left:70%;rigth:10%;"><span id='announceArea' onclick="winmain().openDocUrl('medium','__showAnnounce.jsp','','lookup')" style="text-decoration:underline;cursor:hand;color:#000000;font-weight:bolder"><%=netgest.bo.impl.Ebo_AnnounceImpl.getAnnounces( perf )%></span></div>
</DIV>


<table height="100%" cellspacing='0' width="100%" style="table-layout:fixed"><col width='100%'>
<tr height="24px"><td style="height:24px;background-color:#A4C3F0">
<DIV id="menuAPP2" style="position:relative;z-Index:50;top:0;height:24px;width:100%"></div>
</td>
</tr>
<tr height="100%" >
      <td style="height:100%" id="BODY">
       <div id="NGTBODY" style="position:relative;height:100%;width:100%"></div>                 
     </td>
</tr>
</table>



<iframe style="display:none" id = bocmds1 src="__cmds.jsp?cmds=1"></iframe>
<iframe style="display:none" id = bocmds2 src="__cmds.jsp?cmds=2"></iframe>
<iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSession.jsp?cmds=3"></iframe>

</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

