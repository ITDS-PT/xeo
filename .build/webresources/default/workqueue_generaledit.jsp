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
    response.sendRedirect("login.jsp?returnPage=workqueue_generaledit.jsp");
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
if ((BOI==null && securityRights.hasRights(boctx,"workQueue")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"workQueue") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"workQueue",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"workQueue",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
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
nbo «workQueue»</title>

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
   <%if(DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("roles")|| DOC.hasCategoryRights("roles")|| DOC.hasCategoryRights("")){%>
   <TABLE id="princ_<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0" name="geral">
      <COLGROUP/>
      <COL width="140"/>
      <COL/>
      <TBODY>
         <TR>
            <TD class="leftbar"><SPAN class="leftbar" id="<%=DOCLIST.cvui%>" defaultArea="area_<%=DOCLIST.cvui%>_0">
                  <%if (DOC.hasCategoryRights("identification")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_0" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_0" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_0", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_0" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_0');"><IMG src="resources/area.gif"/>Geral</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("roles")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_1" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_1" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_1", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_1" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_1');"><IMG src="resources/area.gif"/>Membros</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("roles")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_2" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_2" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_2", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_2" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_2');"><IMG src="resources/area.gif"/>Referênciado por</DIV>
                  <%}%>
                  <%if (DOC.hasCategoryRights("")){ %>
                  <DIV class="lbItem" id="area_<%=DOCLIST.cvui%>_3" tabIndex="<%=DOC.getTabindex(DOC.AREA, BOI==null?"area_" + String.valueOf(DOCLIST.cvui) + "_3" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "area_" + String.valueOf(DOCLIST.cvui) + "_3", DOCLIST)%>" relatedAreaId="barea_<%=DOCLIST.cvui%>_3" areaNumber="<%=DOCLIST.cvui%>" onclick="so('<%=DOCLIST.cvui%>');loadArea('barea_<%=DOCLIST.cvui%>_3');"><IMG src="resources/area.gif"/>Contas de Correio</DIV>
                  <%}%>
               </SPAN></TD>
            <TD id="Areas_<%=DOCLIST.cvui%>">
               <%if (DOC.hasCategoryRights("identification")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_0" class="areaSEL">
                  <%{ int cvui12536809=DOCLIST.cvui;%>
                  <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), "barea_" + DOCLIST.cvui + "_0");%>
                  <%if(DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("correspondencia_address")){%>
                  <TABLE id="<%=DOCLIST.cvui%>" class="layout" voui="panel" cellspacing="0" cellpadding="0">
                     <TBODY>
                        <TR height="25">
                           <TD>
                              <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                                 <TBODY>
                                    <TR>
                                       <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                                          <%if (DOC.hasCategoryRights("identification")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("identification")%></SPAN><%}%><%if (DOC.hasCategoryRights("correspondencia_address")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("correspondencia_address")%></SPAN><%}%>
                                       </TD>
                                    </TR>
                                 </TBODY>
                              </TABLE>
                              <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
                              <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
                           </TD>
                        </TR>
                        <TR>
                           <TD><%if (DOC.hasCategoryRights("identification")){ %>
                              <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab">
                                 <%{ int cvui17873059=DOCLIST.cvui;%>
                                 <%
BOI=currObjectList.getObject();sec = DOC.getSection("s13230902");
if(sec != null)
  sec.setTitle(DOC.getCategoryLabel("general","identification"));
if(sec==null) { 
  sec = DOC.createSection("s13230902","");
  sec.p_name = "";
  sec.p_id = "";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("workQueue");  sec.setTitle(DOC.getCategoryLabel("general","identification"));
  docHTML_sectionRow row; 
boolean canAccessid=BOI.getAttribute("id").canAccess();

if (canAccessid ) { 
row=sec.addRow();
if( canAccessid) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("id"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessname=BOI.getAttribute("name").canAccess();

if (canAccessname ) { 
row=sec.addRow();
if( canAccessname) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("name"),xattributes);
} else row.addCell();
} 
boolean canAccessadministrator=BOI.getAttribute("administrator").canAccess();

if (canAccessadministrator ) { 
row=sec.addRow();
if( canAccessadministrator) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("administrator"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessemail=BOI.getAttribute("email").canAccess();

if (canAccessemail ) { 
row=sec.addRow();
if( canAccessemail) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("email"),xattributes);
} else row.addCell();
} 
boolean canAccessnotifica=BOI.getAttribute("notifica").canAccess();

if (canAccessnotifica ) { 
row=sec.addRow();
if( canAccessnotifica) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("notifica"),xattributes);
} else row.addCell();
} 
boolean canAccesstemplateEmail=BOI.getAttribute("templateEmail").canAccess();

if (canAccesstemplateEmail ) { 
row=sec.addRow();
if( canAccesstemplateEmail) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("templateEmail"),xattributes,"","");
} else row.addCell();
} 
boolean canAccesspreferedMedia=BOI.getAttribute("preferedMedia").canAccess();

if (canAccesspreferedMedia ) { 
row=sec.addRow();
if( canAccesspreferedMedia) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("preferedMedia"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessworkplace=BOI.getAttribute("workplace").canAccess();
boolean canAccessworkSchedule=BOI.getAttribute("workSchedule").canAccess();

if (canAccessworkplace || canAccessworkSchedule ) { 
row=sec.addRow();
if( canAccessworkplace) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("workplace"),xattributes,"","");
} else row.addCell();
if( canAccessworkSchedule) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("workSchedule"),xattributes,"","");
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                                 <%DOCLIST.cvui=cvui17873059;}%>
                              </DIV>
                              <%}%>
                              <%if (DOC.hasCategoryRights("correspondencia_address")){ %>
                              <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab">
                                 <%{ int cvui6562546=DOCLIST.cvui;%>
                                 <div width="100%" height="100%"><% if(BOI.getAttribute("correspondencia_address").hasRights()){%> <IFRAME id='inc_workQueue__<%=BOI.bo_boui%>__correspondencia_address' xsrc='<%="address_generaledit.jsp?docid="+IDX+"&method=edit&parent_attribute=correspondencia_address&parent_attribute_label=Morada%20de%20Correspondência&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_workQueue__" + BOI.bo_boui + "__correspondencia_address", DOCLIST)%>'></IFRAME><% } %> </div>
                                 <%DOCLIST.cvui=cvui6562546;}%>
                              </DIV>
                              <%}%>
                           </TD>
                        </TR>
                     </TBODY>
                  </TABLE>
                  <%}%>
                  <%DOCLIST.cvui=cvui12536809;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("roles")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_1" class="area">
                  <%{ int cvui3303193=DOCLIST.cvui;%>
                  <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), "barea_" + DOCLIST.cvui + "_1");%>
                  <%if(DOC.hasCategoryRights("roles")){%>
                  <TABLE id="<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
                     <TBODY>
                        <TR height="25">
                           <TD>
                              <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                                 <TBODY>
                                    <TR>
                                       <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                                          <%if (DOC.hasCategoryRights("roles")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>">Membros</SPAN><%}%>
                                       </TD>
                                    </TR>
                                 </TBODY>
                              </TABLE>
                              <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
                              <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
                           </TD>
                        </TR>
                        <TR>
                           <TD><%if (DOC.hasCategoryRights("roles")){ %>
                              <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab">
                                 <%{ int cvui17746591=DOCLIST.cvui;%>
                                 <div width="100%" height="100%"><IFRAME id='inc_workQueue__<%=BOI.bo_boui%>__dynbrige_iXEOUser' xsrc='<%="ixeouser_generallist.jsp?docid="+IDX+"&method=list&list_frommethod=referencedBy%28iXEOUser%2Cqueues%29&parent_attribute=dynbrige_iXEOUser&parent_boui="+BOI.bo_boui%>' frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_iXEOUser__" + String.valueOf(BOI.bo_boui) + "__dynbrige_iXEOUser", DOCLIST)%>'></IFRAME></div>
                                 <%DOCLIST.cvui=cvui17746591;}%>
                              </DIV>
                              <%}%>
                           </TD>
                        </TR>
                     </TBODY>
                  </TABLE>
                  <%}%>
                  <%DOCLIST.cvui=cvui3303193;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("roles")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_2" class="area">
                  <%{ int cvui1617168=DOCLIST.cvui;%>
                  <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), "barea_" + DOCLIST.cvui + "_2");%>
                  <%if(DOC.hasCategoryRights("roles")){%>
                  <TABLE id="<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
                     <TBODY>
                        <TR height="25">
                           <TD>
                              <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                                 <TBODY>
                                    <TR>
                                       <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                                          <%if (DOC.hasCategoryRights("roles")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>">Membros</SPAN><%}%>
                                       </TD>
                                    </TR>
                                 </TBODY>
                              </TABLE>
                              <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
                              <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
                           </TD>
                        </TR>
                        <TR>
                           <TD><%if (DOC.hasCategoryRights("roles")){ %>
                              <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab">
                                 <%{ int cvui665799=DOCLIST.cvui;%>
                                 <div width="100%" height="100%"><IFRAME id='inc_workQueue__<%=BOI.bo_boui%>__dynbrige_boObject' xsrc='<%="__listcardid.jsp?docid="+IDX+"&method=list&list_frommethod=referencedBy%28%29&parent_attribute=dynbrige_boObject&parent_boui="+BOI.bo_boui%>' frameBorder=0 width='100%' scrolling=no height='100%'tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_boObject__" + String.valueOf(BOI.bo_boui) + "__dynbrige_boObject", DOCLIST)%>'></IFRAME></div>
                                 <%DOCLIST.cvui=cvui665799;}%>
                              </DIV>
                              <%}%>
                           </TD>
                        </TR>
                     </TBODY>
                  </TABLE>
                  <%}%>
                  <%DOCLIST.cvui=cvui1617168;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("")){ %>
               <DIV id="barea_<%=DOCLIST.cvui%>_3" class="area">
                  <%{ int cvui16460043=DOCLIST.cvui;%>
                  <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), "barea_" + DOCLIST.cvui + "_3");%>
                  <%if(DOC.hasCategoryRights("")){%>
                  <TABLE id="<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
                     <TBODY>
                        <TR height="25">
                           <TD>
                              <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                                 <TBODY>
                                    <TR>
                                       <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                                          <%if (DOC.hasCategoryRights("")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>">Contas de Correio</SPAN><%}%>
                                       </TD>
                                    </TR>
                                 </TBODY>
                              </TABLE>
                              <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
                              <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
                           </TD>
                        </TR>
                        <TR>
                           <TD><%if (DOC.hasCategoryRights("")){ %>
                              <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab">
                                 <%{ int cvui21206734=DOCLIST.cvui;%>
                                 <div width="100%" height="100%"><% if(BOI.getAttribute("emailAccounts").hasRights()){%> <IFRAME id='inc_workQueue__<%=BOI.bo_boui%>__emailAccounts' xsrc='<%="workqueue_emailaccounts_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=emailAccounts&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_workQueue__" + BOI.bo_boui + "__emailAccounts", DOCLIST)%>'></IFRAME><% } %> </div>
                                 <%DOCLIST.cvui=cvui21206734;}%>
                              </DIV>
                              <%}%>
                           </TD>
                        </TR>
                     </TBODY>
                  </TABLE>
                  <%}%>
                  <%DOCLIST.cvui=cvui16460043;}%>
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
<FORM name='boFormSubmit' method='post' action='workqueue_generaledit.jsp'>

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
