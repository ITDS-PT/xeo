<%@ page contentType="text/html;UTF-8"%>
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
boolean found = false;
%>
<html>
<%
  boObject perf=boObject.getBoManager().loadObject(boctx,bosession.getPerformerBoui());
%>

<meta http-equiv='Content-Type' content='text/html; charset=windows-1252'>
<title><%=perf.getAttribute("name").getValueString()%></title>

<style type='text/css'>
    
    @import url('ieThemes/0/global/ui-global.css');
    
    @import url('xeo.css');
    
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

<script LANGUAGE='javascript' SRC='jsmessages/jsmessages.jsp'></script>
<script LANGUAGE='javascript' SRC='ieLibrary/main.js'></script>

<script LANGUAGE='javascript'>
var controlArea=null;
window.onbeforeunload=logout;
window.onunload=null;
window.logoutWindow=true;
function getXEOSessionId()
{
    var xeosessionid="<%=boctx.getBoSession().getId()%>";
    return xeosessionid;
}
function logout()
{
for(var i=0;i<ndl.length;i++)
{
 if(ndl[i] && ndl[i].className!='walp') ndl[i].close();
}
if(window.logoutWindow)
{
	window.event.returnValue=jsmessage_18;
}
//return false;
}

function winmain()
{
  return document.getElementById("work").contentWindow;
}
function ini()
{
window.status='starter loaded';
}

function ajaxKeepAlive()
{
    var xmlhttp = new ActiveXObject( "Microsoft.XMLHTTP" );
    xmlhttp.open("POST","__controlSession.jsp?"+((new Date())+1), true );
    xmlhttp.send();
}
window.setInterval("ajaxKeepAlive();",90000);

</script>

<body scroll='no' onload="ini()">

<DIV id="menuAPP" style="position:absolute;z-Index:500;top:0;height:24px;width:100%">   
<table style='width:100%' class='layout' cellSpacing='0' cellPadding='0'>
    <tbody>
        <tr height='24'>
            <td>
         
                <table class='mnubarFlat' id='mnuBar1' cellSpacing='0' cellPadding='0'>
                    <tbody>
                    <tr>
                        <td width='9'>
                            <img hspace='3' src='templates/menu/std/mnu_vSpacer.gif' WIDTH='5' HEIGHT='18'/>
                        </td>
                        <td class='icMenu' noWrap='1'>
                            <span class='menu' tabIndex='1' accessKey='F'  menu='SUBMENU_OBJECTS'>
                            <%=netgest.bo.localized.JSPMessages.getString( "__start.11" )%>
                            <table class='mnuList' id='SUBMENU_OBJECTS' cellSpacing='0' cellPadding='3'>
                                <colgroup/><col class='mnuLeft'/><col/>
                                <tbody>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_LOV", bosession.getPerformerBoui() )) { %>
                                    <tr tabIndex='0'  accessKey='L' onclick="winmain().openDoc('small','ebo_lov','list','method=list&boql=SELECT+Ebo_LOV+WHERE+1%3D1',null,null,null,null);">
                                        <td>&nbsp;</td><td class='mnuItm'> <%=boDefHandler.getBoDefinition("Ebo_LOV").getLabel()%> </tr>
                                    <%found = true;
                                    }%>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"yearHolidays", bosession.getPerformerBoui() )) { %>    
                                    <tr tabIndex='0'  onclick="winmain().openDoc('small','yearholidays','list','method=list&boql=SELECT+yearHolidays+WHERE+1%3D1',null,null,null,null);"  >
                                       <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("yearHolidays").getLabel()%></td></tr>
                                    <%found = true;
                                    }%>
                                    <%if(found){%>
                                    <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                    <%found = false;
                                    }%>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Perf", bosession.getPerformerBoui() )) { %> 
                                    <tr tabIndex='0'  accessKey='U'  onclick="winmain().openDoc('small','ebo_perf','list','method=list&boql=SELECT+Ebo_Perf+WHERE+1%3D1',null,null,null,null);"  >
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_Perf").getLabel()%></td></tr>
                                    <%found = true;
                                    }%>
                                   <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Role", bosession.getPerformerBoui() )) { %>
                                    <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_role','list','method=list&boql=SELECT+Ebo_Role+WHERE+1%3D1',null,null,null,null);"  >
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_Role").getLabel()%></td></tr>
                                    <%found = true;
                                    }%>
                                   <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Group", bosession.getPerformerBoui() )) { %>
                                   <tr tabIndex='0'  accessKey='G'  onclick="winmain().openDoc('small','ebo_group','list','method=list&boql=SELECT+Ebo_Group+WHERE+1%3D1',null,null,null,null);"  >
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_Group").getLabel()%></tr>
                                    <%found = true;
                                    }%>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"workQueue", bosession.getPerformerBoui() )) { %>    
                                    <tr tabIndex='0'  accessKey='P'  onclick="winmain().openDoc('small','workqueue','list','method=list&boql=SELECT+workQueue+WHERE+1%3D1',null,null,null,null);"  >
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("workQueue").getLabel()%></tr>
                                    <%found = true;
                                    }%>
                                    <%if(found){%>
                                   <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>
                                    <%found = false;
                                    }%>                               
                                   <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Policy", bosession.getPerformerBoui() )) { %> 
                                   <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_policy','list','method=list&boql=SELECT+Ebo_Policy+WHERE+1%3D1',null,null,null,null);"  >
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_Policy").getLabel()%></td></tr>
                                    <%found = true;
                                    }%>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_ClsReg", bosession.getPerformerBoui() )) { %> 
                                       <tr tabIndex='0'  onclick="winmain().openDoc('small','ebo_clsreg','list','method=list&boql=SELECT+Ebo_ClsReg+WHERE+1%3D1',null,null,null,null);"  >
                                            <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_ClsReg").getLabel()%></td></tr>
                                        <%found = true;
                                    }%>
                                    <%if ( netgest.bo.security.securityRights.hasRights(perf ,"xwfReassignHist", bosession.getPerformerBoui() )) { %> 
                                   <tr tabIndex='0'  onclick="winmain().openDocUrl('medium','__explorer.jsp','?objectName=xwfReassignHist&form=expReassignHist','lookup');"  >                                                          
                                        <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("xwfReassignHist").getLabel()%></td></tr>
                                    <%found = true;
                                    }%>
                                   
                               </tbody>
                            </table>
                        </span>
             
              
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
							<td>&nbsp;</td><td class='mnuItm'>Apóli<u>c</u>es</td>
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
                                   <td>&nbsp;</td><td class='mnuItm'><u>S</u>ugestão</td>
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
                        <span class='menu' tabIndex='0' menu=SUBMENU_TOOLS accessKey='T'><%=netgest.bo.localized.JSPMessages.getString( "__start.2" )  %>
                        <table class='mnuList' id='SUBMENU_TOOLS' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="winmain().openDocUrl('small','__changePassword.jsp','','std') ">
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.3" )%></td></tr>
                              <tr tabIndex='0'  onclick="winmain().openDocUrl('small','__setUserDefaultConfig.jsp','','std') ">
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.4" )%></td></tr>
                             <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Filter", bosession.getPerformerBoui() )) { %>
                               <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','ebo_filter','list','method=list&boql=SELECT+Ebo_Filter+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.5" )%></td></tr>
                             <%}%>
                             <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_treeDef", bosession.getPerformerBoui() )) { %>
                               <tr tabIndex='0'  accessKey='V'  onclick="winmain().openDoc('small','ebo_treedef','list','method=list&boql=SELECT+Ebo_treeDef+WHERE+CREATOR%3D<%=bosession.getPerformerBoui()%>',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_treeDef").getLabel()%></td></tr>
                             <%}%>
                            <%if ( netgest.bo.security.securityRights.hasRights(perf ,"Ebo_Schedule", bosession.getPerformerBoui() )) { %>
                                <tr tabIndex='4'  accessKey='I' onclick="winmain().openDoc('small','ebo_schedule','list','method=list&boql=SELECT+Ebo_Schedule+WHERE+1%3D1',null,null,null,null);">
                                   <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("Ebo_Schedule").getLabel()%></td>
                                </tr>
                            <%}%>        
                             <%if ( netgest.bo.security.securityRights.hasRights(perf ,"xwfProgram", bosession.getPerformerBoui() )) { %>   
                              <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','xwfprogram','list','method=list&boql=SELECT+xwfProgram+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("xwfProgram").getLabel()%></td></tr>
                              <%}%>
                               <%if ( netgest.bo.security.securityRights.hasRights(perf ,"uiWorkPlace", bosession.getPerformerBoui() )) { %> 
                              <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','uiworkplace','list','method=list&boql=SELECT+uiWorkPlace+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("uiWorkPlace").getLabel()%></td></tr>  
                              <%}%>   
                              <%if ( netgest.bo.security.securityRights.hasRights(perf ,"uiListBar", bosession.getPerformerBoui() )) { %>
                              <tr tabIndex='0'  accessKey='F'  onclick="winmain().openDoc('small','uilistbar','list','method=list&boql=SELECT+uiListBar+WHERE+1%3D1',null,null,null,null);"  >
                                    <td>&nbsp;</td><td class='mnuItm'><%=boDefHandler.getBoDefinition("uiListBar").getLabel()%></td></tr> 
                               <%}%> 
                            </tbody>
                            </table>
                        </span>
                        
                        <span class='menu' tabIndex='0' menu=SUBMENU_AJUDA accessKey='A'><%=netgest.bo.localized.JSPMessages.getString( "__start.12" )%>
                        <table class='mnuList' id='SUBMENU_AJUDA' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                             <tr tabIndex='0'  onclick="window.open('help/Manual Utilizador v2.1.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.5" )%></td>
                             </tr>
                             <tr tabIndex='0'  onclick="window.open('help/Boas praticas v2.doc')">
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.6" )%></td>
                             </tr>
                             
                            <% if ( netgest.bo.security.securityRights.hasRightsToPackage( perf, "ADM" ) ){ %>
                                <tr tabIndex='4'  accessKey='A' onclick="winmain().openDocUrl('externalLink=true','poolstats.jsp?debug=true;','',null,null,'Estatisticas');">
                                
                                    <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.7" )%></td>
                                 </tr>
                            <% } %>
                                                             
                             <tr class='mnuSpacer'><td>&nbsp;</td><td><hr class='mnuSpacer'/></td></tr>    
                              <tr tabIndex='0'  onclick="window.showModalDialog('about/about.htm','0.5','dialogHeight: 390px; dialogWidth: 610px;edge: Raised; center: Yes; help: No; resizable: no; status: no;');"  >
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.8" )%></td></tr>
                            </tbody>
                         </table>
                        </span>
                        
                        <span class='menu' tabIndex='0' menu=SUBMENU_SAIR accessKey='S'><%=netgest.bo.localized.JSPMessages.getString( "__start.13" )%>
                        <table class='mnuList' id='SUBMENU_SAIR' cellSpacing='0' cellPadding='3'>
                            <colgroup/><col class='mnuLeft'/><col/>
                            <tbody>
                                   
                            <tr tabIndex='0'  onclick="try{window.parent.location.href='logout.jsp'}catch(e){}"  >
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.9" )%> <%=perf.getAttribute("name").getValueString()%></td></tr>
	                        
                            <tr tabIndex='0'  onclick="window.parent.close()"  >
                                   <td>&nbsp;</td><td class='mnuItm'><%=netgest.bo.localized.JSPMessages.getString( "__start.10" )%></td></tr>
                                                             
                                 
                              
                            </tbody>
                         </table>
                        </span>
                        
                    </td>
                    <td class='mnuTitle mnuRight' id='tdTitle' noWrap='1'>
                    <span title=''><%=netgest.bo.localized.JSPMessages.getString( "__start.17" )%>, <%=perf.getAttribute("name").getValueString()%></span></span>
                    </td>
                    </tr>
                    </tbody>    
               </table>
                
              
            </td>
            
        </tr>
    </tbody>
</table>  

<div style="position:absolute;top:34;left:70%;rigth:10%;"><span id='announceArea' onclick="winmain().openDocUrl('medium','__showAnnounceXwf.jsp','','lookup')" style="text-decoration:underline;cursor:hand;color:#FFFFFF;font-weight:bolder"><%=netgest.xwf.core.xwfAnnounceImpl.getAnnounces( perf )%></span></div>
</DIV>


<table height="100%" cellspacing='0' cellpadding=0 width="100%" style=""><col width='100%'>

<tr height="24px"><td style="height:24px;background-color:#5BAF50">
<DIV id="menuAPP2" style="position:relative;z-Index:50;top:0;height:24px;width:100%"></div>
</td>
</tr>
<tr height="100%" >
      <td style="height:100%" id="BODY">
        <iframe id='work' src='__workPlace.jsp?name=default' id='contentFrame' frameborder="0" scrolling="no" style="position:relative;height:100%;width:100%"></iframe>
     </td>
</tr>
</table>




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

