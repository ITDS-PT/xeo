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
    response.sendRedirect("login.jsp?returnPage=xwfactivitypoll_generaledit.jsp");
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
if ((BOI==null && securityRights.hasRights(boctx,"xwfActivityPoll")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"xwfActivityPoll") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"xwfActivityPoll",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"xwfActivityPoll",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
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

String doNotRedirect = request.getParameter("doNotRedirect");
if((doNotRedirect == null || "".equals(doNotRedirect)) && BOI.exists() && !"XwfController".equals(DOC.getController().getName()))
{
  String programBouiStr = BOI.getAttribute("program").getValueString();
  if(programBouiStr != null && !"".equals(programBouiStr))
  {
      StringBuffer url = new StringBuffer("?method=edit&docid="+IDX);
      Enumeration enum = request.getParameterNames();
      while( enum.hasMoreElements() )
      {
          String pname = enum.nextElement().toString();
          if( !pname.equalsIgnoreCase("method") && !pname.equalsIgnoreCase("docid") )
          {
              url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) );
          }
      }
      response.sendRedirect( "__xwfWorkPlace.jsp" + url.toString() + "&masterdoc=true&runtimeProgramBoui="+programBouiStr);
      return;         
  }
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
nbo «xwfActivityPoll»</title>

<%= DOC.getController().getPresentation().writeCSS() %>
<%= DOC.getController().getPresentation().writeJS() %>

<%@ include file='boheaders.jsp'%>
<script>
<%=DOC.getScriptToRunOnClient()%></script>
<%
String labelAux = "Escolha";
%>
<script>var objLabel="<%=boObject.mergeAttributes(labelAux,BOI)%>";var objStatus="<%=BOI.getSTATUS()%>";</script>
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
   <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), null);%>
   <%if(DOC.hasCategoryRights("settings")|| DOC.hasCategoryRights("workHistory")|| DOC.hasCategoryRights("reassigns")|| DOC.hasCategoryRights("documents")|| DOC.hasCategoryRights("n_conclusions")){%>
   <TABLE id="<%=DOCLIST.cvui%>" class="layout" style="padding:10px" cellspacing="0" cellpadding="0">
      <TBODY>
         <TR height="25">
            <TD>
               <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                  <TBODY>
                     <TR>
                        <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                           <%if (DOC.hasCategoryRights("settings")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" name="tab_settings" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_settings" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("settings")%></SPAN><%}%><%if (DOC.hasCategoryRights("workHistory")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" name="tab_workHistory" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_workHistory" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("workHistory")%></SPAN><%}%><%if (DOC.hasCategoryRights("reassigns")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_2" name="tab_reassigns" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_reassigns" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_2", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("reassigns")%></SPAN><%}%><%if (DOC.hasCategoryRights("documents")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_3" name="tab_documents" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_documents" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_3", DOCLIST)%>">Documentos</SPAN><%}%><%if (DOC.hasCategoryRights("n_conclusions")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_4" name="tab_conclusions" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_conclusions" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_4", DOCLIST)%>">Resumo</SPAN><%}%>
                        </TD>
                     </TR>
                  </TBODY>
               </TABLE>
               <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
               <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
            </TD>
         </TR>
         <TR>
            <TD><%if (DOC.hasCategoryRights("settings")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab" style="overflow-y:auto">
                  <%{ int cvui346720=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s22418911");
if(sec != null)
  sec.setTitle("Assunto");
if(sec==null) { 
  sec = DOC.createSection("s22418911","");
  sec.p_name = "section_settingslabelActivity";
  sec.p_id = "section_settingslabelActivity";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle("Assunto");
  sec.p_height="20px";
  docHTML_sectionRow row; 
boolean canAccesslabel=BOI.getAttribute("label").canAccess();

if (canAccesslabel ) { 
row=sec.addRow();
if( canAccesslabel) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("label"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s11936443");
if(sec != null)
  sec.setTitle("Assunto");
if(sec==null) { 
  sec = DOC.createSection("s11936443","");
  sec.p_name = "section_xwfProgram";
  sec.p_id = "section_xwfProgram";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle("Assunto");
  sec.p_height="20px";
  docHTML_sectionRow row; 
boolean canAccessprogram=BOI.getAttribute("program").canAccess();

if (canAccessprogram ) { 
row=sec.addRow();
if( canAccessprogram) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("program"),xattributes,"","");
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s27413602");
if(sec != null)
  sec.setTitle("Descrição");
if(sec==null) { 
  sec = DOC.createSection("s27413602","");
  sec.p_name = "section_settingsDescActivity";
  sec.p_id = "section_settingsDescActivity";
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle("Descrição");
  sec.p_height="200px";
  docHTML_sectionRow row; 
boolean canAccessdescription=BOI.getAttribute("description").canAccess();

if (canAccessdescription ) { 
row=sec.addRow();
if( canAccessdescription) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");xattributes.put("height","100%");row.addCell(bodef.getAttributeRef("description"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s4516367");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s4516367","");
  sec.p_name = "section_settingsActivity";
  sec.p_id = "section_settingsActivity";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle("");
  sec.p_height="20px";
  docHTML_sectionRow row; 
boolean canAccessassignedQueue=BOI.getAttribute("assignedQueue").canAccess();
boolean canAccessperformer=BOI.getAttribute("performer").canAccess();

if (canAccessassignedQueue || canAccessperformer ) { 
row=sec.addRow();
if( canAccessassignedQueue) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("assignedQueue"),xattributes,"","");
} else row.addCell();
if( canAccessperformer) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("performer"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessbeginDate=BOI.getAttribute("beginDate").canAccess();
boolean canAccessendDate=BOI.getAttribute("endDate").canAccess();

if (canAccessbeginDate || canAccessendDate ) { 
row=sec.addRow();
if( canAccessbeginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("beginDate"),xattributes);
} else row.addCell();
if( canAccessendDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("endDate"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%if(DOC.hasCategoryRights("settings.prvDatesAndTimes")){ %>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s6455282");
if(sec != null)
  sec.setTitle(DOC.getCategoryLabel("general","settings.prvDatesAndTimes"));
if(sec==null) { 
  sec = DOC.createSection("s6455282","");
  sec.p_name = "section_prvDatas";
  sec.p_id = "section_prvDatas";
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle(DOC.getCategoryLabel("general","settings.prvDatesAndTimes"));
  sec.p_height="40px";
  docHTML_sectionRow row; 
boolean canAccessprvBeginDate=BOI.getAttribute("prvBeginDate").canAccess();
boolean canAccessdeadLineDate=BOI.getAttribute("deadLineDate").canAccess();

if (canAccessprvBeginDate || canAccessdeadLineDate ) { 
row=sec.addRow();
if( canAccessprvBeginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("prvBeginDate"),xattributes);
} else row.addCell();
if( canAccessdeadLineDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("deadLineDate"),xattributes);
} else row.addCell();
} 
boolean canAccessprv_Duration=BOI.getAttribute("prv_Duration").canAccess();
boolean canAccessutilDays=BOI.getAttribute("utilDays").canAccess();

if (canAccessprv_Duration || canAccessutilDays ) { 
row=sec.addRow();
if( canAccessprv_Duration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("prv_Duration"),xattributes);
} else row.addCell();
if( canAccessutilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("utilDays"),xattributes);
} else row.addCell();
} 
boolean canAccesspriority=BOI.getAttribute("priority").canAccess();

if (canAccesspriority ) { 
row=sec.addRow();
if( canAccesspriority) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("priority"),xattributes);
} else row.addCell();
row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%}%>
                  <%if(DOC.hasCategoryRights("settings.datesAndTimes")){ %>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s12461987");
if(sec != null)
  sec.setTitle(DOC.getCategoryLabel("general","settings.datesAndTimes"));
if(sec==null) { 
  sec = DOC.createSection("s12461987","");
  sec.p_name = "section_datas";
  sec.p_id = "section_datas";
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle(DOC.getCategoryLabel("general","settings.datesAndTimes"));
  sec.p_height="80px";
  docHTML_sectionRow row; 
boolean canAccessjournal=BOI.getAttribute("journal").canAccess();

if (canAccessjournal ) { 
row=sec.addRow();
if( canAccessjournal) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("journal"),xattributes);
} else row.addCell();
} 
boolean canAccessduration=BOI.getAttribute("duration").canAccess();
boolean canAccesshistBeginDate=BOI.getAttribute("histBeginDate").canAccess();

if (canAccessduration || canAccesshistBeginDate ) { 
row=sec.addRow();
if( canAccessduration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("duration"),xattributes);
} else row.addCell();
if( canAccesshistBeginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("histBeginDate"),xattributes);
} else row.addCell();
} 
boolean canAccesshistEndDate=BOI.getAttribute("histEndDate").canAccess();

if (canAccesshistEndDate ) { 
row=sec.addRow();
row.addCell();
if( canAccesshistEndDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("histEndDate"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%}%>
                  <%DOCLIST.cvui=cvui346720;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("workHistory")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab">
                  <%{ int cvui1862617=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("workHistory").hasRights()){%> <IFRAME id='inc_xwfActivityPoll__<%=BOI.bo_boui%>__workHistory' xsrc='<%="duration_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=workHistory&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_xwfActivityPoll__" + BOI.bo_boui + "__workHistory", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui1862617;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("reassigns")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_2" class="tab">
                  <%{ int cvui32279086=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("reassigns").hasRights()){%> <IFRAME id='inc_xwfActivityPoll__<%=BOI.bo_boui%>__reassigns' xsrc='<%="xwfreassign_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=reassigns&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_xwfActivityPoll__" + BOI.bo_boui + "__reassigns", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui32279086;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("documents")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_3" class="tab">
                  <%{ int cvui6558360=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("documents").hasRights()){%> <IFRAME id='inc_xwfActivityPoll__<%=BOI.bo_boui%>__documents' xsrc='<%="ebo_document_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=documents&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_xwfActivityPoll__" + BOI.bo_boui + "__documents", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui6558360;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("n_conclusions")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_4" class="tab">
                  <%{ int cvui54279=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s6300378");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s6300378","");
  sec.p_name = "section_settingsConclusions";
  sec.p_id = "section_settingsConclusions";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfActivityPoll");  sec.setTitle("");
  sec.p_height="100%";
  docHTML_sectionRow row; 
boolean canAccessjustification=BOI.getAttribute("justification").canAccess();

if (canAccessjustification ) { 
row=sec.addRow();
if( canAccessjustification) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");xattributes.put("height","100%");row.addCell(bodef.getAttributeRef("justification"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%DOCLIST.cvui=cvui54279;}%>
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
<FORM name='boFormSubmit' method='post' action='xwfactivitypoll_generaledit.jsp'>

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
