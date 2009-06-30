<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
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
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=wallp1_generaledit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

int IDX;int cvui;
boDefHandler bodef;
boDefAttribute atr;
String idbolist;
String[] ctrl;
docHTML_section sec;
docHTML_grid grid;
Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
String parent_boui=request.getParameter("parent_boui");
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
boObject BOI;
String method=request.getParameter( "method" );
String inputMethod=request.getParameter( "method" );
String requestedBoui=request.getParameter( "boui" );
if ( currObjectList == null ) BOI=null;
else BOI=currObjectList.getObject();
if(request.getParameter("objectBoui")!=null){
if(request.getParameter("objectBoui").indexOf(';') < 0)
{BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
long[] a_boui = {BOI.getBoui()};
currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
if(!currObjectList.haveBoui(a_boui[0]))
currObjectList.inserRow(a_boui[0]);}
else{StringTokenizer st = new StringTokenizer(request.getParameter("objectBoui"), ";", false);
String newB = null; while(st.hasMoreElements()){
newB = st.nextToken();
if(newB != null && newB.length() > 1)    {
currObjectList.inserRow(Long.parseLong(newB));    } BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(newB));}}}

 DOC.setMasterObject(BOI,masterdoc);
 DOC.getController().getNavigator().setRoot( BOI, masterdoc, request);

String redirectUrl = DOC.getController().getNavigator().processPathRequest( BOI, masterdoc, request);
if(!DOC.haveErrors() && BOI != null && BOI.getSendRedirect() == null && redirectUrl != null)
{
    BOI.setSendRedirect(redirectUrl);
}

if(BOI != null && BOI.getSendRedirect() != null){response.sendRedirect( BOI.getSendRedirect() + "&docid=" + IDX + "&myIDX=" + IDX  );BOI.cleanSendRedirect();return;}
if( method !=null ) { 
   if( method.equalsIgnoreCase("newfromtemplate") || method.equalsIgnoreCase("forward") || method.equalsIgnoreCase("execute") || method.equalsIgnoreCase("reply") || method.equalsIgnoreCase("replyAll")|| method.equalsIgnoreCase("duplicate") || method.equalsIgnoreCase("new") ) { 
       method="edit";
       requestedBoui = ""+BOI.getBoui();
    }
}
if ((BOI==null && securityRights.hasRights(boctx,"wallp1")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"wallp1") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"wallp1",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"wallp1",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
  BOI.setDisabled(); }
else if (BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"null",securityRights.WRITE)) { 
  BOI.setEnabled(true); }
if ( BOI.exists() ){ 
 BOI.markAsRead();
} 
String parent_attribute=request.getParameter("parent_attribute");
 if( parent_attribute != null && parent_boui != null ) 
 { 
   boObject pObject = boObject.getBoManager().loadObject( boctx, Long.parseLong( parent_boui ) ); 
   AttributeHandler oParentAttribute = pObject.getAttribute( parent_attribute );
   if( oParentAttribute != null )
   {
       if( oParentAttribute.disableWhen() && BOI.isEnabled ) 
       {
           BOI.setDisabled();
       }
   }
 
 }
 
 
 if( currObjectList != null ) currObjectList.first();



%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
nbo «wallp1»</title>

<%= DOC.getController().getPresentation().writeCSS() %>
<%= DOC.getController().getPresentation().writeJS() %>

<%@ include file='boheaders.jsp'%>
<script>
<%=DOC.getScriptToRunOnClient()%></script>
<script>var objLabel="<%=BOI.getCARDID()%>";var objStatus="<%=BOI.getSTATUS()%>";</script>
<% String scriptToRun=null;
if ( request.getParameter("boFormSubmitXml")!= null && ( request.getParameter("addToCtxParentBridge") != null || request.getParameter("relatedParentBridge") != null ))
 {
    String look_parentBoui      = request.getParameter("ctxParent");
    String look_parentAttribute = request.getParameter("addToCtxParentBridge");
    String clientIDX            = request.getParameter("relatedClientIDX");
    String relDocid             = request.getParameter("ctxParentIdx");
if ( look_parentAttribute== null )
 {
   look_parentAttribute = request.getParameter("relatedParentBridge");
   look_parentBoui      = request.getParameter("relatedParent");
   relDocid             = request.getParameter("relatedParentDocid");
 }
    docHTML xd=DOCLIST.getDocByIDX( netgest.utils.ClassUtils.convertToInt(relDocid),DOC.getEboContext());
    boObject obj=xd.getObject(Long.parseLong(look_parentBoui));
    StringBuffer toP=new StringBuffer();
     StringBuffer nameH = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    if(!xd.parentRefresh())
    {
        scriptToRun="updateLookupAttribute()";
    }
    else
    {
        scriptToRun="updateLookupAttribute(); callParentRefreshFrame()";
    }
 %>
<script language="javascript">
function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      if (  wDocfrms[z].id == "inc_<%=nameH%>" )
      {
  wDocfrms[z].contentWindow.submitGrid();
            return;
      }
      else
      {
         updateFrame(wDocfrms[z]);
      }
   }
}
 function updateLookupAttribute()
 {
   var windowToUpdate="<%=clientIDX%>";
   var w=parent.ndl[windowToUpdate];
    if(w)
    {
        var ifrm=w.htm;
       var xw;
           if ( ifrm.id == "frm$<%=clientIDX%>" )
           {
                updateFrame(ifrm);
           }
   }
 }
 function callParentRefreshFrame()
 {
   var windowToUpdate="<%=clientIDX%>";
   var w=parent.ndl[windowToUpdate];
    if(w)
    {
       var ifrm=w.htm;
                var wDocfrms=ifrm.contentWindow.document.getElementsByTagName('IFRAME');
                for(var z=0;  z < wDocfrms.length ; z++)
                {
                    if (  wDocfrms[z].id == "refreshframe" )
                    {
                        wDocfrms[z].contentWindow.refreshValues();
                    }
                }
   }
}
</script>
<%}%>
<% 
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("searchClientIdx") != null && BOI != null && BOI.exists()){
  String searchClientIdx=request.getParameter("searchClientIdx");
  scriptToRun="updateObj()";
%>
<script language="javascript">
function updateObj(){
  var w=parent.ndl[<%=searchClientIdx%>];
  if( w ){
      var ifrm=w.htm;
      var xw;
      if(ifrm.contentWindow.resultframe != null)      {          ifrm.contentWindow.findframe.submitBridge2(<%=BOI.bo_boui%>);      }      else if(ifrm.contentWindow.findframe != null)      {          ifrm.contentWindow.findframe.submitSelectOne2(<%=BOI.bo_boui%>);
      }  }
}
</script>
<%}%>
<% StringBuffer toP=new StringBuffer();
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("actRenderObj") != null && ( BOI!=null && BOI.exists() ) ){
  String clientIDX=request.getParameter("actIdxClient");
  String actRenderObj=request.getParameter("actRenderObj");
  String actRenderAttribute=request.getParameter("actRenderAttribute");
  scriptToRun="updateLookupAttribute()";
 int actRenderDocid= netgest.utils.ClassUtils.convertToInt(request.getParameter("actRenderDocid"),-1);
 boObject obj=null;
 if ( actRenderDocid != -1 )
 {
 netgest.bo.dochtml.docHTML docr=DOCLIST.getDocByIDX( actRenderDocid, DOC.getEboContext() );
 if( docr!= null )
 {
 obj=docr.getObject(Long.parseLong(actRenderObj));
 }
 }
 else
 {
 obj=DOC.getObject(Long.parseLong(actRenderObj));
 }
 StringBuffer v= new StringBuffer( );
 StringBuffer nameH = new StringBuffer();
 AttributeHandler attr=null;
 if ( obj!= null )
 {
  attr= obj.getAttribute(actRenderAttribute);
  v= new StringBuffer( attr.getValueString() );
  StringBuffer id = new StringBuffer();
  boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(actRenderAttribute );
  nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( actRenderAttribute );
  id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( actRenderAttribute );
  netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(
        toP,
        obj,
        attr,
        v,
        nameH,
        id,
        1,
        DOC,
        attr.disableWhen(),
        !attr.hiddenWhen(),
        obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
        );
}
%>
<script language="javascript">
function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   toRet=false;
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      var xok=false;
      xw=wDocfrms[z].contentWindow.document.all;
      var toR=[];
      for(var j=0; j< xw.length; j++){
        if ( xw[j].id == "tblLook<%=nameH%>" ){
         toR[toR.length]=xw[j];
        }
      }
      for(var y=0; y < toR.length; y++){
            toR[y].outerHTML=toRender.innerHTML;
      }
         updateFrame(wDocfrms[z]);
   }
 }
  function updateLookupAttribute(){
   var windowToUpdate="<%=clientIDX%>";
   var xok=false;
   var w=parent.ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm;
       var xw;
          xw=ifrm.contentWindow.document.all;
          var toR=[];
          for(var z=0; z< xw.length; z++){
            if ( xw[z].id == "tblLook<%=nameH%>" ){
             toR[toR.length]=xw[z];
            }
          }
          for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                var xele=ifrm.contentWindow.document.getElementsByName('<%=nameH%>');
                for( var z=0; z<xele.length;z++)
                      xele[z].original=xele[0].value;
                xok=true;
          }
       if ( !xok ) updateFrame(ifrm);
   }
 }
</script>
<%}%>
<script>
function runBeforeMethodExec(methodName)
{
}
function runAfterMethodExec()
{
setLastMethodToRun("");}
</script>
<%if (scriptToRun==null) {%>
<body ondragenter="activethis()" onload=" <%=DOC.getActionOnClient()%>; runAfterMethodExec(); treatFocus(); ">
<%}
else{%>
<body ondragenter="activethis()" onload=" <%=scriptToRun%>; runAfterMethodExec(); treatFocus(); ">
<%
}
%>
<%if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("actRenderObj") != null && ( BOI!=null && BOI.exists() )){%>
    <div id="toRender" style='display:none'>
    <%=toP%>
</div>
<%}%>
<%boObject xobj=(boObject)currObjectList.getParent();
if( request.getParameter("toClose") == null || DOC.haveErrors() ) {
if(xobj == null) {
if (request.getParameter("menu")==null || (request.getParameter("menu").equalsIgnoreCase("yes"))){%>
<TABLE id="dBody" class="layout" cellspacing="0" cellpadding="0">
<TBODY>
<%if( !DOC.p_WebForm ){ %>
<%DOC.getController().getPresentation().writeToolBar(DOCLIST, pageContext,currObjectList);%>
<%}%>
<%DOC.getController().getPresentation().writeHeaderHandler(DOCLIST,pageContext);%>
<%=DOC.getController().getPresentation().renderPath( request )%>
<%if(DOC.haveErrors()) {%>
<TR><TD class='error'><div class='error'>
<%=DOC.getHTMLErrors()%>
</div></TD></TR>
<%}%>
<TR height="100%" ><TD valign=top align=left>
<%}}%>

<% if(BOI != null) { %>
<iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>"></iframe>
<% } %>
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui());%>
   <%if(DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("identification2")|| DOC.hasCategoryRights("identification2")){%>
   <TABLE id="princ_<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
      <COLGROUP/>
      <COL width="140"/>
      <COL/>
      <TBODY>
         <TR>
            <TD class="leftbar"><SPAN class="leftbar" id="<%=DOCLIST.cvui%>" defaultArea="area_<%=DOCLIST.cvui%>_0">
                  <%if (DOC.hasCategoryRights("identification")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_0" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_0" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_0", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_0" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_0');"><IMG src="resources/area.gif"/>O meu Trabalho</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("identification")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_1" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_1" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_1", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_1" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_1');"><IMG src="resources/area.gif"/>Em espera</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("identification")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_2" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_2" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_2", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_2" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_2');"><IMG src="resources/area.gif"/>Completo</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("identification")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_3" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_3" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_3", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_3" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_3');"><IMG src="resources/area.gif"/>Todas</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("identification2")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_4" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_4" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_4", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_4" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_4');"><IMG src="resources/area.gif"/>O meu Grupo</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("identification2")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_5" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_5" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_5", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_5" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_5');"><IMG src="resources/area.gif"/>O que registei</DIV>
                  <%}%>
               </SPAN></TD>
            <TD id="Areas_<%=DOCLIST.cvui%>">
               <%if (DOC.hasCategoryRights("identification")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_0" class="areaSEL">
                  <%{ int cvui16901335=DOCLIST.cvui;%>
                  <table height="100%" width="100%">
                     <tr>
                        <td>
                           <DIV class="frame" height="99%" style="padding-bottom:3px">
                              <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                 <TBODY>
                                    <TR height="100%">
                                       <TD>
                                          <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                             <COLGROUP/>
                                             <COL width="42"/>
                                             <COL/>
                                             <COL width="6"/>
                                             <TBODY>
                                                <TR height="13" colspan="3">
                                                   <TD>
                                                      <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                                   </TD>
                                                </TR>
                                                <TR height="20">
                                                   <TD>
                                                      <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                                   </TD>
                                                   <TD class="frameTitle">As minhas tarefas</TD>
                                                   <TD align="right">
                                                      <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                                   </TD>
                                                </TR>
                                                <TR height="100%">
                                                   <TD class="frame_out" colspan="3">
                                                      <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                         <COLGROUP/>
                                                         <COL width="100%"/>
                                                         <COL width="115"/>
                                                         <TBODY>
                                                            <TR>
                                                               <TD><IFRAME   id=' ' xsrc='<%="wall_contentready.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contentready.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                               <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                                  <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                                     <COLGROUP/>
                                                                     <COL width="18"/>
                                                                     <COL width="97"/>
                                                                     <TBODY>
                                                                        <TR>
                                                                           <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                           <TD>
                                                                              <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Solicitação</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                           <TD>
                                                                              <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Tarefa</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                           <TD>
                                                                              <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Email</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                           <TD>
                                                                              <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Carta/Oficio</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                           <TD>
                                                                              <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Fax</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                           <TD>
                                                                              <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Inf. Interna</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                           <TD>
                                                                              <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Entradas</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                           <TD>
                                                                              <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Telefonema</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                           <TD>
                                                                              <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Conversa</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                           <TD>
                                                                              <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Reunião</TD>
                                                                        </TR>
                                                                        <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                           <TD>
                                                                              <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                           </TD>
                                                                           <TD>Fórum</TD>
                                                                        </TR>
                                                                        <TR>
                                                                           <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                              <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                                 <COLGROUP/>
                                                                                 <COL width="18"/>
                                                                                 <COL width="97"/>
                                                                                 <TBODY>
                                                                                    <TR>
                                                                                       <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                                    </TR>
                                                                                    <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                                       <TD>
                                                                                          <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                                       </TD>
                                                                                       <TD>Reclamação</TD>
                                                                                    </TR>
                                                                                    <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                                       <TD>
                                                                                          <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                                       </TD>
                                                                                       <TD>Ped.Informação</TD>
                                                                                    </TR>
                                                                                    <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                                       <TD>
                                                                                          <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                                       </TD>
                                                                                       <TD>Sugestão</TD>
                                                                                    </TR>
                                                                                 </TBODY>
                                                                              </TABLE>
                                                                           </TD>
                                                                        </TR>
                                                                        <TR height="100%" class="framemenuLastRow">
                                                                           <TD colspan="2">&nbsp;</TD>
                                                                        </TR>
                                                                     </TBODY>
                                                                  </TABLE>
                                                               </TD>
                                                            </TR>
                                                         </TBODY>
                                                      </TABLE>
                                                   </TD>
                                                </TR>
                                             </TBODY>
                                          </TABLE>
                                       </TD>
                                    </TR>
                                 </TBODY>
                              </TABLE>
                           </DIV></td>
                     </tr>
                     <tr height="1%">
                        <td style="padding-left:9px;padding-right:9px">
                           <TABLE height="1%" cellSpacing="0" cellPadding="0" width="100%">
                              <colgroup/>
                              <col width="16"/>
                              <col/>
                              <col width="22"/>
                              <TBODY>
                                 <tr height="20">
                                    <TD class="box_header">
                                       <IMG src="templates/boxinfo/std/box_on_left.gif" width="16" height="20"/>
                                    </TD>
                                    <TD class="box_header" noWrap="1">Os meus Atalhos</TD>
                                    <TD class="box_header" style="CURSOR:HAND" onclick="showboxinfo(this)"><img src="templates/boxinfo/std/box_on_right_up.gif" width="22" height="20"/>
                                    </TD>
                                 </tr>
                                 <TR>
                                    <TD colspan="3" valign="top" style="height:145px" class="box_body">
                                       <div width="100%" height="100%">
                                          <IFRAME id='inc_wallp1_Ebo_ShortCut' src='ebo_shortcut_generallist.jsp?docid=<%=IDX%>&form=list&menu=no&canSelectRows=no&showPreview=no&boql=select+Ebo_ShortCut+where+performer%3DCTX_PERFORMER_BOUI' frameBorder=0 width='100%' scrolling=no height='145px' tabindex='<%=DOC.getTabindex(DOC.IFRAME, "inc_wallp1_Ebo_ShortCut", DOCLIST)%>'></IFRAME>
                                       </div>
                                    </TD>
                                 </TR>
                              </TBODY>
                           </TABLE></td>
                     </tr>
                  </table>
                  <%DOCLIST.cvui=cvui16901335;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("identification")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_1" class="area">
                  <%{ int cvui22471122=DOCLIST.cvui;%>
                  <DIV class="frame" height="100%" style="padding-bottom:3px">
                     <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                        <TBODY>
                           <TR height="100%">
                              <TD>
                                 <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                    <COLGROUP/>
                                    <COL width="42"/>
                                    <COL/>
                                    <COL width="6"/>
                                    <TBODY>
                                       <TR height="13" colspan="3">
                                          <TD>
                                             <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                          </TD>
                                       </TR>
                                       <TR height="20">
                                          <TD>
                                             <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                          </TD>
                                          <TD class="frameTitle">Em espera</TD>
                                          <TD align="right">
                                             <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                          </TD>
                                       </TR>
                                       <TR height="100%">
                                          <TD class="frame_out" colspan="3">
                                             <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                <COLGROUP/>
                                                <COL width="100%"/>
                                                <COL width="115"/>
                                                <TBODY>
                                                   <TR>
                                                      <TD><IFRAME   id=' ' xsrc='<%="wall_contentwaiting.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contentwaiting.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                      <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                         <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                            <COLGROUP/>
                                                            <COL width="18"/>
                                                            <COL width="97"/>
                                                            <TBODY>
                                                               <TR>
                                                                  <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                  <TD>
                                                                     <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Solicitação</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                  <TD>
                                                                     <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Tarefa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                  <TD>
                                                                     <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Email</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                  <TD>
                                                                     <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Carta/Oficio</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                  <TD>
                                                                     <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fax</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                  <TD>
                                                                     <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Inf. Interna</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                  <TD>
                                                                     <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Entradas</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                  <TD>
                                                                     <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Telefonema</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                  <TD>
                                                                     <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Conversa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                  <TD>
                                                                     <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Reunião</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                  <TD>
                                                                     <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fórum</TD>
                                                               </TR>
                                                               <TR>
                                                                  <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                     <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                        <COLGROUP/>
                                                                        <COL width="18"/>
                                                                        <COL width="97"/>
                                                                        <TBODY>
                                                                           <TR>
                                                                              <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                              <TD>
                                                                                 <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Reclamação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                              <TD>
                                                                                 <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Ped.Informação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                              <TD>
                                                                                 <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Sugestão</TD>
                                                                           </TR>
                                                                        </TBODY>
                                                                     </TABLE>
                                                                  </TD>
                                                               </TR>
                                                               <TR height="100%" class="framemenuLastRow">
                                                                  <TD colspan="2">&nbsp;</TD>
                                                               </TR>
                                                            </TBODY>
                                                         </TABLE>
                                                      </TD>
                                                   </TR>
                                                </TBODY>
                                             </TABLE>
                                          </TD>
                                       </TR>
                                    </TBODY>
                                 </TABLE>
                              </TD>
                           </TR>
                        </TBODY>
                     </TABLE>
                  </DIV>
                  <%DOCLIST.cvui=cvui22471122;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("identification")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_2" class="area">
                  <%{ int cvui17023610=DOCLIST.cvui;%>
                  <DIV class="frame" height="100%" style="padding-bottom:3px">
                     <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                        <TBODY>
                           <TR height="100%">
                              <TD>
                                 <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                    <COLGROUP/>
                                    <COL width="42"/>
                                    <COL/>
                                    <COL width="6"/>
                                    <TBODY>
                                       <TR height="13" colspan="3">
                                          <TD>
                                             <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                          </TD>
                                       </TR>
                                       <TR height="20">
                                          <TD>
                                             <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                          </TD>
                                          <TD class="frameTitle">Fechadas</TD>
                                          <TD align="right">
                                             <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                          </TD>
                                       </TR>
                                       <TR height="100%">
                                          <TD class="frame_out" colspan="3">
                                             <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                <COLGROUP/>
                                                <COL width="100%"/>
                                                <COL width="115"/>
                                                <TBODY>
                                                   <TR>
                                                      <TD><IFRAME   id=' ' xsrc='<%="wall_contentclose.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contentclose.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                      <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                         <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                            <COLGROUP/>
                                                            <COL width="18"/>
                                                            <COL width="97"/>
                                                            <TBODY>
                                                               <TR>
                                                                  <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                  <TD>
                                                                     <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Solicitação</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                  <TD>
                                                                     <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Tarefa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                  <TD>
                                                                     <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Email</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                  <TD>
                                                                     <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Carta/Oficio</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                  <TD>
                                                                     <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fax</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                  <TD>
                                                                     <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Inf. Interna</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                  <TD>
                                                                     <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Entradas</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                  <TD>
                                                                     <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Telefonema</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                  <TD>
                                                                     <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Conversa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                  <TD>
                                                                     <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Reunião</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                  <TD>
                                                                     <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fórum</TD>
                                                               </TR>
                                                               <TR>
                                                                  <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                     <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                        <COLGROUP/>
                                                                        <COL width="18"/>
                                                                        <COL width="97"/>
                                                                        <TBODY>
                                                                           <TR>
                                                                              <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                              <TD>
                                                                                 <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Reclamação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                              <TD>
                                                                                 <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Ped.Informação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                              <TD>
                                                                                 <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Sugestão</TD>
                                                                           </TR>
                                                                        </TBODY>
                                                                     </TABLE>
                                                                  </TD>
                                                               </TR>
                                                               <TR height="100%" class="framemenuLastRow">
                                                                  <TD colspan="2">&nbsp;</TD>
                                                               </TR>
                                                            </TBODY>
                                                         </TABLE>
                                                      </TD>
                                                   </TR>
                                                </TBODY>
                                             </TABLE>
                                          </TD>
                                       </TR>
                                    </TBODY>
                                 </TABLE>
                              </TD>
                           </TR>
                        </TBODY>
                     </TABLE>
                  </DIV>
                  <%DOCLIST.cvui=cvui17023610;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("identification")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_3" class="area">
                  <%{ int cvui30738776=DOCLIST.cvui;%>
                  <DIV class="frame" height="100%" style="padding-bottom:3px">
                     <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                        <TBODY>
                           <TR height="100%">
                              <TD>
                                 <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                    <COLGROUP/>
                                    <COL width="42"/>
                                    <COL/>
                                    <COL width="6"/>
                                    <TBODY>
                                       <TR height="13" colspan="3">
                                          <TD>
                                             <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                          </TD>
                                       </TR>
                                       <TR height="20">
                                          <TD>
                                             <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                          </TD>
                                          <TD class="frameTitle">Todas</TD>
                                          <TD align="right">
                                             <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                          </TD>
                                       </TR>
                                       <TR height="100%">
                                          <TD class="frame_out" colspan="3">
                                             <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                <COLGROUP/>
                                                <COL width="100%"/>
                                                <COL width="115"/>
                                                <TBODY>
                                                   <TR>
                                                      <TD><IFRAME   id=' ' xsrc='<%="wall_contenttotal.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contenttotal.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                      <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                         <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                            <COLGROUP/>
                                                            <COL width="18"/>
                                                            <COL width="97"/>
                                                            <TBODY>
                                                               <TR>
                                                                  <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                  <TD>
                                                                     <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Solicitação</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                  <TD>
                                                                     <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Tarefa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                  <TD>
                                                                     <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Email</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                  <TD>
                                                                     <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Carta/Oficio</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                  <TD>
                                                                     <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fax</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                  <TD>
                                                                     <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Inf. Interna</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                  <TD>
                                                                     <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Entradas</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                  <TD>
                                                                     <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Telefonema</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                  <TD>
                                                                     <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Conversa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                  <TD>
                                                                     <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Reunião</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                  <TD>
                                                                     <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fórum</TD>
                                                               </TR>
                                                               <TR>
                                                                  <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                     <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                        <COLGROUP/>
                                                                        <COL width="18"/>
                                                                        <COL width="97"/>
                                                                        <TBODY>
                                                                           <TR>
                                                                              <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                              <TD>
                                                                                 <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Reclamação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                              <TD>
                                                                                 <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Ped.Informação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                              <TD>
                                                                                 <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Sugestão</TD>
                                                                           </TR>
                                                                        </TBODY>
                                                                     </TABLE>
                                                                  </TD>
                                                               </TR>
                                                               <TR height="100%" class="framemenuLastRow">
                                                                  <TD colspan="2">&nbsp;</TD>
                                                               </TR>
                                                            </TBODY>
                                                         </TABLE>
                                                      </TD>
                                                   </TR>
                                                </TBODY>
                                             </TABLE>
                                          </TD>
                                       </TR>
                                    </TBODY>
                                 </TABLE>
                              </TD>
                           </TR>
                        </TBODY>
                     </TABLE>
                  </DIV>
                  <%DOCLIST.cvui=cvui30738776;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("identification2")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_4" class="area">
                  <%{ int cvui22581278=DOCLIST.cvui;%>
                  <DIV class="frame" height="100%" style="padding-bottom:3px">
                     <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                        <TBODY>
                           <TR height="100%">
                              <TD>
                                 <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                    <COLGROUP/>
                                    <COL width="42"/>
                                    <COL/>
                                    <COL width="6"/>
                                    <TBODY>
                                       <TR height="13" colspan="3">
                                          <TD>
                                             <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                          </TD>
                                       </TR>
                                       <TR height="20">
                                          <TD>
                                             <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                          </TD>
                                          <TD class="frameTitle">O meu Grupo</TD>
                                          <TD align="right">
                                             <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                          </TD>
                                       </TR>
                                       <TR height="100%">
                                          <TD class="frame_out" colspan="3">
                                             <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                <COLGROUP/>
                                                <COL width="100%"/>
                                                <COL width="115"/>
                                                <TBODY>
                                                   <TR>
                                                      <TD><IFRAME   id=' ' xsrc='<%="wall_contentgroup.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contentgroup.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                      <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                         <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                            <COLGROUP/>
                                                            <COL width="18"/>
                                                            <COL width="97"/>
                                                            <TBODY>
                                                               <TR>
                                                                  <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                  <TD>
                                                                     <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Solicitação</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                  <TD>
                                                                     <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Tarefa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                  <TD>
                                                                     <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Email</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                  <TD>
                                                                     <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Carta/Oficio</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                  <TD>
                                                                     <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fax</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                  <TD>
                                                                     <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Inf. Interna</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                  <TD>
                                                                     <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Entradas</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                  <TD>
                                                                     <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Telefonema</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                  <TD>
                                                                     <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Conversa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                  <TD>
                                                                     <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Reunião</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                  <TD>
                                                                     <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fórum</TD>
                                                               </TR>
                                                               <TR>
                                                                  <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                     <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                        <COLGROUP/>
                                                                        <COL width="18"/>
                                                                        <COL width="97"/>
                                                                        <TBODY>
                                                                           <TR>
                                                                              <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                              <TD>
                                                                                 <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Reclamação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                              <TD>
                                                                                 <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Ped.Informação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                              <TD>
                                                                                 <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Sugestão</TD>
                                                                           </TR>
                                                                        </TBODY>
                                                                     </TABLE>
                                                                  </TD>
                                                               </TR>
                                                               <TR height="100%" class="framemenuLastRow">
                                                                  <TD colspan="2">&nbsp;</TD>
                                                               </TR>
                                                            </TBODY>
                                                         </TABLE>
                                                      </TD>
                                                   </TR>
                                                </TBODY>
                                             </TABLE>
                                          </TD>
                                       </TR>
                                    </TBODY>
                                 </TABLE>
                              </TD>
                           </TR>
                        </TBODY>
                     </TABLE>
                  </DIV>
                  <%DOCLIST.cvui=cvui22581278;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("identification2")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_5" class="area">
                  <%{ int cvui22927783=DOCLIST.cvui;%>
                  <DIV class="frame" height="100%" style="padding-bottom:3px">
                     <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                        <TBODY>
                           <TR height="100%">
                              <TD>
                                 <TABLE style="TABLE-LAYOUT:fixed;height:100%;width:100%" cellSpacing="0" cellPadding="0">
                                    <COLGROUP/>
                                    <COL width="42"/>
                                    <COL/>
                                    <COL width="6"/>
                                    <TBODY>
                                       <TR height="13" colspan="3">
                                          <TD>
                                             <IMG src="resources/todayWork_top.gif" width="42" height="13"/>
                                          </TD>
                                       </TR>
                                       <TR height="20">
                                          <TD>
                                             <IMG src="resources/todayWork_bottom.gif" width="42" height="20"/>
                                          </TD>
                                          <TD class="frameTitle">O que registei</TD>
                                          <TD align="right">
                                             <IMG src="resources/todayWork_right.gif" width="6" height="20"/>
                                          </TD>
                                       </TR>
                                       <TR height="100%">
                                          <TD class="frame_out" colspan="3">
                                             <TABLE height="100%" width="100%" cellSpacing="0" cellPadding="0">
                                                <COLGROUP/>
                                                <COL width="100%"/>
                                                <COL width="115"/>
                                                <TBODY>
                                                   <TR>
                                                      <TD><IFRAME   id=' ' xsrc='<%="wall_contentregistry.jsp?docid="+IDX%>'  frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null?"wall_contentregistry.jsp" + String.valueOf(IDX) : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "  ", DOCLIST)%>'></IFRAME></TD>
                                                      <TD style="PADDING-RIGHT: 7px; PADDING-LEFT: 7px; PADDING-BOTTOM: 10px; PADDING-TOP: 0px" vAlign="top">
                                                         <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellpadding="3" cellSpacing="0">
                                                            <COLGROUP/>
                                                            <COL width="18"/>
                                                            <COL width="97"/>
                                                            <TBODY>
                                                               <TR>
                                                                  <TD class="frameMenuHeader" colspan="2">Criar</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova solicitação" onclick="winmain().openDoc('medium','sr','edit','method=new&amp;object=sr');">
                                                                  <TD>
                                                                     <IMG src="resources/sr/ico16.gif" tabIndex="resources/sr/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Solicitação</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma tarefa normal" onclick="winmain().openDoc('medium','task','edit','method=new&amp;object=task');">
                                                                  <TD>
                                                                     <IMG src="resources/task/ico16.gif" tabIndex="resources/task/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Tarefa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Email" onclick="winmain().openDoc('medium','email','edit','method=new&amp;object=email');">
                                                                  <TD>
                                                                     <IMG src="resources/email/ico16.gif" tabIndex="resources/email/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Email</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Carta/Oficio" onclick="winmain().openDoc('medium','letter','edit','method=new&amp;object=letter');">
                                                                  <TD>
                                                                     <IMG src="resources/letter/ico16.gif" tabIndex="resources/letter/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Carta/Oficio</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Fax" onclick="winmain().openDoc('medium','fax','edit','method=new&amp;object=fax');">
                                                                  <TD>
                                                                     <IMG src="resources/fax/ico16.gif" tabIndex="resources/fax/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fax</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Informação Interna" onclick="winmain().openDoc('medium','internalinf','edit','method=new&amp;object=internalInf');">
                                                                  <TD>
                                                                     <IMG src="resources/internalInf/ico16.gif" tabIndex="resources/internalInf/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Inf. Interna</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Entradas" onclick="winmain().openDoc('medium','res','edit','method=new&amp;object=RES');">
                                                                  <TD>
                                                                     <IMG src="resources/RES/ico16.gif" tabIndex="resources/RES/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Entradas</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Telefonema" onclick="winmain().openDoc('medium','phonecall','edit','method=new&amp;object=phonecall');">
                                                                  <TD>
                                                                     <IMG src="resources/phonecall/ico16.gif" tabIndex="resources/phonecall/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Telefonema</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Registar Conversa" onclick="winmain().openDoc('medium','chat','edit','method=new&amp;object=chat');">
                                                                  <TD>
                                                                     <IMG src="resources/chat/ico16.gif" tabIndex="resources/chat/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Conversa</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Marcar Reunião" onclick="winmain().openDoc('medium','meeting','edit','method=new&amp;object=meeting');">
                                                                  <TD>
                                                                     <IMG src="resources/meeting/ico16.gif" tabIndex="resources/meeting/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Reunião</TD>
                                                               </TR>
                                                               <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Forum" onclick="winmain().openDoc('medium','forumtopic','edit','method=new&amp;object=forumtopic');">
                                                                  <TD>
                                                                     <IMG src="resources/forumtopic/ico16.gif" tabIndex="resources/forumtopic/ico16.gif"/>
                                                                  </TD>
                                                                  <TD>Fórum</TD>
                                                               </TR>
                                                               <TR>
                                                                  <TD style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px" colspan="2" vAlign="top">
                                                                     <TABLE style="WIDTH: 115px;HEIGHT: 100%" class="frameMenu" cellspacing="0" cellPadding="3">
                                                                        <COLGROUP/>
                                                                        <COL width="18"/>
                                                                        <COL width="97"/>
                                                                        <TBODY>
                                                                           <TR>
                                                                              <TD class="frameMenuHeader" colspan="2">Do exterior</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Reclamação" onclick="winmain().openDoc('medium','claim','edit','method=new&amp;object=claim');">
                                                                              <TD>
                                                                                 <IMG src="resources/claim/ico16.gif" tabIndex="resources/claim/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Reclamação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar um novo Pedido de Informação" onclick="winmain().openDoc('medium','information','edit','method=new&amp;object=information');">
                                                                              <TD>
                                                                                 <IMG src="resources/information/ico16.gif" tabIndex="resources/information/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Ped.Informação</TD>
                                                                           </TR>
                                                                           <TR class="hand" onmouseover="fmenuOn(this)" onmouseout="fmenuOff(this)" title="Criar uma nova Sugestão" onclick="winmain().openDoc('medium','suggestion','edit','method=new&amp;object=suggestion');">
                                                                              <TD>
                                                                                 <IMG src="resources/suggestion/ico16.gif" tabIndex="resources/suggestion/ico16.gif"/>
                                                                              </TD>
                                                                              <TD>Sugestão</TD>
                                                                           </TR>
                                                                        </TBODY>
                                                                     </TABLE>
                                                                  </TD>
                                                               </TR>
                                                               <TR height="100%" class="framemenuLastRow">
                                                                  <TD colspan="2">&nbsp;</TD>
                                                               </TR>
                                                            </TBODY>
                                                         </TABLE>
                                                      </TD>
                                                   </TR>
                                                </TBODY>
                                             </TABLE>
                                          </TD>
                                       </TR>
                                    </TBODY>
                                 </TABLE>
                              </TD>
                           </TR>
                        </TBODY>
                     </TABLE>
                  </DIV>
                  <%DOCLIST.cvui=cvui22927783;}%>
               </DIV>
               <%}%>
            </TD>
         </TR>
      </TBODY>
   </TABLE>
   <%}%>
</form>
<%if (request.getParameter("menu")==null || (request.getParameter("menu").equalsIgnoreCase("yes"))){%>
</TD></TR><%= DOC.getController().getPresentation().writeFooterHandler() %></TBODY></TABLE>
<%}%>
<%} else {%>
<font size='5' color='Green' >Gravação OK</font>
<%}%>
<FORM name='boFormSubmit' method='post' action='wallp1_generaledit.jsp'>

<%
 java.util.Hashtable options = new java.util.Hashtable();
if(method != null){
      options.put("method",method);
}
if(inputMethod != null){
      options.put("inputMethod",inputMethod);
}
if(requestedBoui != null){
      options.put("requestedBoui",requestedBoui);
}
 options.put("p_typeForm","0");
 options.put("jspName",this.getClass().getName());
%>

<%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>

</FORM></body></html><%
} else response.sendRedirect("dialogBoxSecurityWarning.htm"); } finally {
if (boctx!=null)boctx.close();if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
}%>
