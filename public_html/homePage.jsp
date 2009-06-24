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

docHTML_controler doc = ( docHTML_controler )request.getSession().getAttribute( "DOCLIST" );
try 
{

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=homePage.jsp");
    return;
}

if(boctx==null) 
{
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

if( doc != null )
{
    ngtOut.DebugInf("Home page refresh.. release all objects:" + doc.poolUniqueId());
    boctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects(doc.poolUniqueId() );
}

%>
<html>

<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>
<title>HomePage</title>
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
window.onbeforeunload=logout;
window.onunload=null;

function logout()
{
for(var i=0;i<ndl.length;i++)
{
 if(ndl[i] && ndl[i].className!='walp') ndl[i].close();
}
window.event.returnValue="ATENȃO : VAI SAIR DO SISTEMA !!!\n ================================== \n SE TIVER DADOS QUE AINDA NAO EST GRAVADOS IR�PERDE-LOS ";
//return false;
}


function ini(){

openWALL("wallpxwf");
		
}

function load(obj)
{
        top.main.closeWALL();
        top.main.openWALL(obj);
				
}

</script>
<%
  boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
  //bosession.setUserName(perf.getAttribute("name").getValueString());
  
%>

<body scroll='no' onload="ini()">
<DIV id="applet" style="position:absolute;z-Index:500;top:0;height:0px;width:0px">   
    <!--<APPLET CODE="XeoThinClient.class" archive="xeoClient/xeoApplet.jar" HEIGHT="0"  WIDTH="0" id = "xeoClient" mayscript="mayscript"></APPLET>-->                    
</DIV>
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
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Repository", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_repository','list','method=list&boql=SELECT+Ebo_Repository+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Repositórios</td></tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"yearHolidays", bosession.getPerformerBoui() )) { %>    
                                <tr tabIndex='0'  onclick="winmain().openDoc('small','yearholidays','list','method=list&boql=SELECT+yearHolidays+WHERE+1%3D1',null,null,null,null);"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Feriados</td></tr>
                                <%}%>
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Template", bosession.getPerformerBoui() )) { %>  
                                <!--<tr tabIndex='0'  accessKey='M'  onclick="winmain().openDoc('small','ebo_template','list','method=list&boql=SELECT+Ebo_Template+WHERE+1%3D1',null,null,null,null);"  >-->
                                <tr tabIndex='0'  accessKey='M'  onclick="winmain().openDoc('small','ebo_template','list','method=list&boql=SELECT+Ebo_Template+WHERE+PARENT is null',null,null,null,null);"  >
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
                               
                           </tbody>
                        </table>
                        </span>
               <%/* if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "XEODOC" ) ){ */ %>
                                            
                <%/*}*/ %>
              
                         <% if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "ISM" ) ){ %>
                         <span class='menu' tabIndex='1' accessKey='G'  menu='SUBMENU_OBJECTS2'><u>G</u>estão de Seguros
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
                                <!--
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"DMS_Merchandise", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='6'  accessKey='E' onclick="load('ISM_WallMerchand');">
	                                    <td>&nbsp;</td><td class='mnuItm'>M<u>e</u>rcadorias Transportadas</td>
	                                 </tr>
                                <%}%>
                                -->
                                <%if ( netgest.bo.security.securityRights.hasRights(perf ,"IMO_PatrFilatelico", bosession.getPerformerBoui() )) { %>
	                                <tr tabIndex='7'  accessKey='R' onclick="load('ISM_WallFilatelico');">
	                                    <td>&nbsp;</td><td class='mnuItm'>Pat<u>r</u>imónio Filatélico</td>
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
							<td>&nbsp;</td><td class='mnuItm'>Apóli>u>c</u>es</td>
						</tr>
						<tr tabIndex='10'  accessKey='C' onclick="load('ISM_WallTaxs');">
							<td>&nbsp;</td><td class='mnuItm'>Taxas/Encargos</td>
						</tr>
                        <tr tabIndex='10'  accessKey='C' onclick="load('ISM_WallTables');">
							<td>&nbsp;</td><td class='mnuItm'>Tabelas de Desvalorização</td>
						</tr>
                                <%}%>
                                 <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
    						    <tr tabIndex='7'  accessKey='O' onclick="load('wallpxwf');">
	                                   <td>&nbsp;</td><td class='mnuItm'>Mesa de Trabalho</td>
	                             </tr>

                        </tbody>
                        </table>
                        </span>                     
                   
                <%}%>
                <% if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "ATENDIMENTO" ) ){ %>                
                        <!-- ATENDIMENTO -->
                        <span class='menu' tabIndex='0' menu=SUBMENU_TOOLS accessKey='A'><u>A</u>tendimento
                        <table class='mnuList' id='SUBMENU_TOOLS' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>

                             <%if ( netgest.bo.security.securityRights.hasRights(perf ,"claim", bosession.getPerformerBoui() )) { %>
                               <tr tabIndex='0'  accessKey='R'  onclick="winmain().openDoc('medium','claim','edit','method=new&object=claim');"  >
                                    <td>&nbsp;</td><td class='mnuItm'><u>R</u>eclamação</td></tr>
                             <%}%>
                              
                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"information", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='P' onclick="winmain().openDoc('medium','information','edit','method=new&object=information');">
                                   <td>&nbsp;</td><td class='mnuItm'><u>P</u>edido Informação</td>
                                </tr>
                            <%}%>        

                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"suggestion", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='S' onclick="winmain().openDoc('medium','suggestion','edit','method=new&object=suggestion');">
                                   <td>&nbsp;</td><td class='mnuItm'><u>S</u>ugestões/td>
                                </tr>
                            <%}%>
                            
                            <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                            
                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ITED_CertConformity", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='C' onclick="winmain().openDoc('medium','ited_certconformity','edit','method=new&object=ITED_CertConformity');">
                                   <td>&nbsp;</td><td class='mnuItm'><u>C</u>ertificado Conformidade</td>
                                </tr>
                            <%}%>                              

                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"ITED_ResponsibilityTerm", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='T' onclick="winmain().openDoc('medium','ited_responsibilityterm','edit','method=new&object=ITED_ResponsibilityTerm');">
                                   <td>&nbsp;</td><td class='mnuItm'><u>T</u>ermo Responsabilidade</td>
                                </tr>
                            <%}%> 
                             
                             
                            </tbody>
                            </table>
                        </span>
                        <!-- FIM ATENDIMENTO -->
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
                                
                              <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','xwfprogram','list','method=list&boql=SELECT+xwfProgram+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'>Programas</td></tr>
                              <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','uiworkplace','list','method=list&boql=SELECT+uiWorkPlace+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'>WorkPlaces</td></tr>      
                            </tbody>
                            </table>
                        </span>
                        
                        <span class='menu' tabIndex='0' menu=SUBMENU_AJUDA accessKey='A'><u>A</u>juda
                        <table class='mnuList' id='SUBMENU_AJUDA' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="window.open('help/Manual Utilizador v2.1.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'>Manual</td>
                             </tr>
                             <tr tabIndex='0'  onclick="window.open('help/Boas praticas v2.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'>Manual de Boas Prácticas</td>
                             </tr>
                             
                                
                            
                            <% if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "ADM" ) ){ %>
                                <tr tabIndex='4'  accessKey='A' onclick="window.open('poolstats.jsp')">
                                    <td>&nbsp;</td><td class='mnuItm'>Administração</td>
                                 </tr>
                            <% } %>
                                                             
                             <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>    
                              <tr tabIndex='0'  onclick="window.showModalDialog('about/about.htm','2','dialogHeight: 390px; dialogWidth: 610px;edge: Raised; center: Yes; help: No; resizable: no; status: no;');"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Acerca do SGIS</td></tr>
                            </tbody>
                         </table>
                        </span>
                        
                        <span class='menu' tabIndex='0' menu=SUBMENU_SAIR accessKey='S'><u>S</u>air
                        <table class='mnuList' id='SUBMENU_SAIR' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                                   
                            <tr tabIndex='0'  onclick="try{window.parent.location.href='logout.jsp'}catch(e){}"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Terminar sessão em <%=perf.getAttribute("name").getValueString()%></td></tr>
	                        
                            <tr tabIndex='0'  onclick="window.parent.close()"  >
                                   <td>&nbsp;</td><td class='mnuItm'>Fechar janela</td></tr>
                                                             
                                 
                              
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


<table height="100%" cellspacing='0' width="100%" style=""><col width='100%'>
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
} finally {
    if( boctx != null )
    {
        boctx.close();
    }
    if(doc!=null && boctx!=null) doc.releseObjects(boctx);
}
%>

