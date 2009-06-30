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
    response.sendRedirect("login.jsp?returnPage=xwfdefactivity_generaledit.jsp");
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
if ((BOI==null && securityRights.hasRights(boctx,"xwfDefActivity")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"xwfDefActivity") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"xwfDefActivity",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"xwfDefActivity",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
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
nbo «xwfDefActivity»</title>

<%= DOC.getController().getPresentation().writeCSS() %>
<%= DOC.getController().getPresentation().writeJS() %>

<%@ include file='boheaders.jsp'%>
<script>
<%=DOC.getScriptToRunOnClient()%></script>
<%
String labelAux = "Tarefa";
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
   <%if(DOC.hasCategoryRights("activity")|| DOC.hasCategoryRights("periodicidade")|| DOC.hasCategoryRights("")){%>
   <TABLE id="<%=DOCLIST.cvui%>" class="layout" voui="panel" cellspacing="0" cellpadding="0">
      <TBODY>
         <TR height="25">
            <TD>
               <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                  <TBODY>
                     <TR>
                        <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                           <%if (DOC.hasCategoryRights("activity")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" name="tab_Activity" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_Activity" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>">Tarefa</SPAN><%}%><%if (DOC.hasCategoryRights("periodicidade")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" name="tab_periodicidade" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_periodicidade" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>">Periodicidade</SPAN><%}%><%if (DOC.hasCategoryRights("")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_2" name="tab_actvGenerated" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_actvGenerated" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_2", DOCLIST)%>">Tarefas Geradas</SPAN><%}%>
                        </TD>
                     </TR>
                  </TBODY>
               </TABLE>
               <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
               <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
            </TD>
         </TR>
         <TR>
            <TD><%if (DOC.hasCategoryRights("activity")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab" style="overflow-y:auto">
                  <%{ int cvui23660426=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s7206597");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s7206597","");
  sec.p_name = "section_ProgramActivity";
  sec.p_id = "section_ProgramActivity";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("");
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
BOI=currObjectList.getObject();sec = DOC.getSection("s23343877");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s23343877","");
  sec.p_name = "section_Activity";
  sec.p_id = "section_Activity";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("");
  sec.p_height="100%";
  docHTML_sectionRow row; 
boolean canAccesslabel=BOI.getAttribute("label").canAccess();

if (canAccesslabel ) { 
row=sec.addRow();
if( canAccesslabel) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("label"),xattributes);
} else row.addCell();
} 
boolean canAccessassignedQueue=BOI.getAttribute("assignedQueue").canAccess();
boolean canAccessto=BOI.getAttribute("to").canAccess();

if (canAccessassignedQueue || canAccessto ) { 
row=sec.addRow();
if( canAccessassignedQueue) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("assignedQueue"),xattributes,"","");
} else row.addCell();
if( canAccessto) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("to"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessperformer=BOI.getAttribute("performer").canAccess();

if (canAccessperformer ) { 
row=sec.addRow();
if( canAccessperformer) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("performer"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessdescription=BOI.getAttribute("description").canAccess();

if (canAccessdescription ) { 
row=sec.addRow();
if( canAccessdescription) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");xattributes.put("height","100%");row.addCell(bodef.getAttributeRef("description"),xattributes);
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
boolean canAccessdocuments=BOI.getAttribute("documents").canAccess();

if (canAccessdocuments ) { 
row=sec.addRow();
if( canAccessdocuments) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("documents"),xattributes,"","");
} else row.addCell();
} 
boolean canAccesstoReturn=BOI.getAttribute("toReturn").canAccess();
boolean canAccessreportType=BOI.getAttribute("reportType").canAccess();

if (canAccesstoReturn || canAccessreportType ) { 
row=sec.addRow();
if( canAccesstoReturn) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("toReturn"),xattributes);
} else row.addCell();
if( canAccessreportType) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("reportType"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessnotified=BOI.getAttribute("notified").canAccess();

if (canAccessnotified ) { 
row=sec.addRow();
if( canAccessnotified) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("notified"),xattributes);
} else row.addCell();
} 
boolean canAccesstype_recursive=BOI.getAttribute("type_recursive").canAccess();

if (canAccesstype_recursive ) { 
row=sec.addRow();
if( canAccesstype_recursive) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("type_recursive"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s22136421");
if(sec != null)
  sec.setTitle("Não Periodica");
if(sec==null) { 
  sec = DOC.createSection("s22136421","");
  sec.p_name = "section_noPeriodic";
  sec.p_id = "section_noPeriodic";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Não Periodica");
  sec.p_height="80px";
  docHTML_sectionRow row; 
boolean canAccessbeginDate=BOI.getAttribute("beginDate").canAccess();

if (canAccessbeginDate ) { 
row=sec.addRow();
if( canAccessbeginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("beginDate"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccesssetDuration=BOI.getAttribute("setDuration").canAccess();
boolean canAccessduration=BOI.getAttribute("duration").canAccess();

if (canAccesssetDuration || canAccessduration ) { 
row=sec.addRow();
if( canAccesssetDuration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("setDuration"),xattributes);
} else row.addCell();
if( canAccessduration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");xattributes.put("defaultState","disabled");row.addCell(bodef.getAttributeRef("duration"),xattributes);
} else row.addCell();
} 
boolean canAccesssetUtilDays=BOI.getAttribute("setUtilDays").canAccess();
boolean canAccessutilDays=BOI.getAttribute("utilDays").canAccess();

if (canAccesssetUtilDays || canAccessutilDays ) { 
row=sec.addRow();
if( canAccesssetUtilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("setUtilDays"),xattributes);
} else row.addCell();
if( canAccessutilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("utilDays"),xattributes);
} else row.addCell();
} 
boolean canAccesssetEndDate=BOI.getAttribute("setEndDate").canAccess();
boolean canAccessendDate=BOI.getAttribute("endDate").canAccess();

if (canAccesssetEndDate || canAccessendDate ) { 
row=sec.addRow();
if( canAccesssetEndDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("setEndDate"),xattributes);
} else row.addCell();
if( canAccessendDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("endDate"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%DOCLIST.cvui=cvui23660426;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("periodicidade")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab" style="overflow-y:auto">
                  <%{ int cvui28843750=DOCLIST.cvui;%>
                  <table>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s16029997");
if(sec != null)
  sec.setTitle("Tipo Periodicidade");
if(sec==null) { 
  sec = DOC.createSection("s16029997","");
  sec.p_name = "section_periodicidade";
  sec.p_id = "section_periodicidade";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Tipo Periodicidade");
  docHTML_sectionRow row; 
boolean canAccessperiod=BOI.getAttribute("period").canAccess();

if (canAccessperiod ) { 
row=sec.addRow();
if( canAccessperiod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("period"),xattributes);
} else row.addCell();
row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s7639094");
if(sec != null)
  sec.setTitle("Vigência");
if(sec==null) { 
  sec = DOC.createSection("s7639094","");
  sec.p_name = "section_vigencia";
  sec.p_id = "section_vigencia";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Vigência");
  docHTML_sectionRow row; 
boolean canAccessvig_beginDate=BOI.getAttribute("vig_beginDate").canAccess();

if (canAccessvig_beginDate ) { 
row=sec.addRow();
if( canAccessvig_beginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_beginDate"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessvig_setDuration=BOI.getAttribute("vig_setDuration").canAccess();
boolean canAccessvig_duration=BOI.getAttribute("vig_duration").canAccess();

if (canAccessvig_setDuration || canAccessvig_duration ) { 
row=sec.addRow();
if( canAccessvig_setDuration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_setDuration"),xattributes);
} else row.addCell();
if( canAccessvig_duration) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");xattributes.put("defaultState","disabled");row.addCell(bodef.getAttributeRef("vig_duration"),xattributes);
} else row.addCell();
} 
boolean canAccessvig_setUtilDays=BOI.getAttribute("vig_setUtilDays").canAccess();
boolean canAccessvig_utilDays=BOI.getAttribute("vig_utilDays").canAccess();

if (canAccessvig_setUtilDays || canAccessvig_utilDays ) { 
row=sec.addRow();
if( canAccessvig_setUtilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_setUtilDays"),xattributes);
} else row.addCell();
if( canAccessvig_utilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_utilDays"),xattributes);
} else row.addCell();
} 
boolean canAccessvig_setOccur=BOI.getAttribute("vig_setOccur").canAccess();
boolean canAccessvig_occur=BOI.getAttribute("vig_occur").canAccess();

if (canAccessvig_setOccur || canAccessvig_occur ) { 
row=sec.addRow();
if( canAccessvig_setOccur) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_setOccur"),xattributes);
} else row.addCell();
if( canAccessvig_occur) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_occur"),xattributes);
} else row.addCell();
} 
boolean canAccessvig_setEndDate=BOI.getAttribute("vig_setEndDate").canAccess();
boolean canAccessvig_endDate=BOI.getAttribute("vig_endDate").canAccess();

if (canAccessvig_setEndDate || canAccessvig_endDate ) { 
row=sec.addRow();
if( canAccessvig_setEndDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_setEndDate"),xattributes);
} else row.addCell();
if( canAccessvig_endDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("vig_endDate"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s11008691");
if(sec != null)
  sec.setTitle("Horária");
if(sec==null) { 
  sec = DOC.createSection("s11008691","");
  sec.p_name = "section_hour";
  sec.p_id = "section_hour";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Horária");
  docHTML_sectionRow row; 
boolean canAccesshour_setPeriod=BOI.getAttribute("hour_setPeriod").canAccess();

if (canAccesshour_setPeriod ) { 
row=sec.addRow();
if( canAccesshour_setPeriod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("hour_setPeriod"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccesshour_period=BOI.getAttribute("hour_period").canAccess();

if (canAccesshour_period ) { 
row=sec.addRow();
if( canAccesshour_period) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("hour_period"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccesshour_setNext=BOI.getAttribute("hour_setNext").canAccess();
boolean canAccesshour_next=BOI.getAttribute("hour_next").canAccess();

if (canAccesshour_setNext || canAccesshour_next ) { 
row=sec.addRow();
if( canAccesshour_setNext) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("hour_setNext"),xattributes);
} else row.addCell();
if( canAccesshour_next) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("hour_next"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s3932200");
if(sec != null)
  sec.setTitle("Diária");
if(sec==null) { 
  sec = DOC.createSection("s3932200","");
  sec.p_name = "section_daily";
  sec.p_id = "section_daily";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Diária");
  docHTML_sectionRow row; 
boolean canAccessdaily_setPeriod=BOI.getAttribute("daily_setPeriod").canAccess();

if (canAccessdaily_setPeriod ) { 
row=sec.addRow();
if( canAccessdaily_setPeriod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("daily_setPeriod"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessdaily_period=BOI.getAttribute("daily_period").canAccess();

if (canAccessdaily_period ) { 
row=sec.addRow();
if( canAccessdaily_period) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("daily_period"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessdaily_setNext=BOI.getAttribute("daily_setNext").canAccess();
boolean canAccessdaily_next=BOI.getAttribute("daily_next").canAccess();

if (canAccessdaily_setNext || canAccessdaily_next ) { 
row=sec.addRow();
if( canAccessdaily_setNext) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("daily_setNext"),xattributes);
} else row.addCell();
if( canAccessdaily_next) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("daily_next"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s19815125");
if(sec != null)
  sec.setTitle("Semanal");
if(sec==null) { 
  sec = DOC.createSection("s19815125","");
  sec.p_name = "section_weekly";
  sec.p_id = "section_weekly";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Semanal");
  docHTML_sectionRow row; 
boolean canAccessweekly_setPeriod=BOI.getAttribute("weekly_setPeriod").canAccess();

if (canAccessweekly_setPeriod ) { 
row=sec.addRow();
if( canAccessweekly_setPeriod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_setPeriod"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessweekly_monday=BOI.getAttribute("weekly_monday").canAccess();
boolean canAccessweekly_tuesday=BOI.getAttribute("weekly_tuesday").canAccess();

if (canAccessweekly_monday || canAccessweekly_tuesday ) { 
row=sec.addRow();
if( canAccessweekly_monday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_monday"),xattributes);
} else row.addCell();
if( canAccessweekly_tuesday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_tuesday"),xattributes);
} else row.addCell();
} 
boolean canAccessweekly_wednesday=BOI.getAttribute("weekly_wednesday").canAccess();
boolean canAccessweekly_thursday=BOI.getAttribute("weekly_thursday").canAccess();

if (canAccessweekly_wednesday || canAccessweekly_thursday ) { 
row=sec.addRow();
if( canAccessweekly_wednesday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_wednesday"),xattributes);
} else row.addCell();
if( canAccessweekly_thursday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_thursday"),xattributes);
} else row.addCell();
} 
boolean canAccessweekly_friday=BOI.getAttribute("weekly_friday").canAccess();
boolean canAccessweekly_saturday=BOI.getAttribute("weekly_saturday").canAccess();

if (canAccessweekly_friday || canAccessweekly_saturday ) { 
row=sec.addRow();
if( canAccessweekly_friday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_friday"),xattributes);
} else row.addCell();
if( canAccessweekly_saturday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_saturday"),xattributes);
} else row.addCell();
} 
boolean canAccessweekly_sunday=BOI.getAttribute("weekly_sunday").canAccess();
boolean canAccessweekly_period=BOI.getAttribute("weekly_period").canAccess();

if (canAccessweekly_sunday || canAccessweekly_period ) { 
row=sec.addRow();
if( canAccessweekly_sunday) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_sunday"),xattributes);
} else row.addCell();
if( canAccessweekly_period) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_period"),xattributes);
} else row.addCell();
} 
boolean canAccessweekly_setNext=BOI.getAttribute("weekly_setNext").canAccess();
boolean canAccessweekly_next=BOI.getAttribute("weekly_next").canAccess();

if (canAccessweekly_setNext || canAccessweekly_next ) { 
row=sec.addRow();
if( canAccessweekly_setNext) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_setNext"),xattributes);
} else row.addCell();
if( canAccessweekly_next) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("weekly_next"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s5074995");
if(sec != null)
  sec.setTitle("Mensal");
if(sec==null) { 
  sec = DOC.createSection("s5074995","");
  sec.p_name = "section_monthly";
  sec.p_id = "section_monthly";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Mensal");
  docHTML_sectionRow row; 
boolean canAccessmonthly_setPeriod=BOI.getAttribute("monthly_setPeriod").canAccess();

if (canAccessmonthly_setPeriod ) { 
row=sec.addRow();
if( canAccessmonthly_setPeriod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_setPeriod"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessmonthly_day=BOI.getAttribute("monthly_day").canAccess();
boolean canAccessmonthly_period_day=BOI.getAttribute("monthly_period_day").canAccess();

if (canAccessmonthly_day || canAccessmonthly_period_day ) { 
row=sec.addRow();
if( canAccessmonthly_day) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_day"),xattributes);
} else row.addCell();
if( canAccessmonthly_period_day) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_period_day"),xattributes);
} else row.addCell();
} 
boolean canAccessmonthly_setPeriod2=BOI.getAttribute("monthly_setPeriod2").canAccess();

if (canAccessmonthly_setPeriod2 ) { 
row=sec.addRow();
if( canAccessmonthly_setPeriod2) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_setPeriod2"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessmonthly_each=BOI.getAttribute("monthly_each").canAccess();
boolean canAccessmonthly_l_day=BOI.getAttribute("monthly_l_day").canAccess();

if (canAccessmonthly_each || canAccessmonthly_l_day ) { 
row=sec.addRow();
if( canAccessmonthly_each) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_each"),xattributes);
} else row.addCell();
if( canAccessmonthly_l_day) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");row.addCell(bodef.getAttributeRef("monthly_l_day"),xattributes);
} else row.addCell();
} 
boolean canAccessmonthly_period=BOI.getAttribute("monthly_period").canAccess();

if (canAccessmonthly_period ) { 
row=sec.addRow();
if( canAccessmonthly_period) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_period"),xattributes);
} else row.addCell();
} 
boolean canAccessmonthly_setNext=BOI.getAttribute("monthly_setNext").canAccess();
boolean canAccessmonthly_next=BOI.getAttribute("monthly_next").canAccess();

if (canAccessmonthly_setNext || canAccessmonthly_next ) { 
row=sec.addRow();
if( canAccessmonthly_setNext) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_setNext"),xattributes);
} else row.addCell();
if( canAccessmonthly_next) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("monthly_next"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s80112");
if(sec != null)
  sec.setTitle("Anual");
if(sec==null) { 
  sec = DOC.createSection("s80112","");
  sec.p_name = "section_yearly";
  sec.p_id = "section_yearly";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("Anual");
  docHTML_sectionRow row; 
boolean canAccessyearly_month=BOI.getAttribute("yearly_month").canAccess();

if (canAccessyearly_month ) { 
row=sec.addRow();
if( canAccessyearly_month) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_month"),xattributes);
} else row.addCell();
} 
boolean canAccessyearly_setPeriod=BOI.getAttribute("yearly_setPeriod").canAccess();
boolean canAccessyearly_day=BOI.getAttribute("yearly_day").canAccess();

if (canAccessyearly_setPeriod || canAccessyearly_day ) { 
row=sec.addRow();
if( canAccessyearly_setPeriod) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_setPeriod"),xattributes);
} else row.addCell();
if( canAccessyearly_day) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_day"),xattributes);
} else row.addCell();
} 
boolean canAccessyearly_setPeriod2=BOI.getAttribute("yearly_setPeriod2").canAccess();

if (canAccessyearly_setPeriod2 ) { 
row=sec.addRow();
if( canAccessyearly_setPeriod2) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_setPeriod2"),xattributes);
} else row.addCell();
row.addCell();
} 
boolean canAccessyearly_each=BOI.getAttribute("yearly_each").canAccess();
boolean canAccessyearly_l_day=BOI.getAttribute("yearly_l_day").canAccess();

if (canAccessyearly_each || canAccessyearly_l_day ) { 
row=sec.addRow();
if( canAccessyearly_each) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_each"),xattributes);
} else row.addCell();
if( canAccessyearly_l_day) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");row.addCell(bodef.getAttributeRef("yearly_l_day"),xattributes);
} else row.addCell();
} 
boolean canAccessyearly_setNext=BOI.getAttribute("yearly_setNext").canAccess();
boolean canAccessyearly_next=BOI.getAttribute("yearly_next").canAccess();

if (canAccessyearly_setNext || canAccessyearly_next ) { 
row=sec.addRow();
if( canAccessyearly_setNext) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_setNext"),xattributes);
} else row.addCell();
if( canAccessyearly_next) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("yearly_next"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s4280335");
if(sec != null)
  sec.setTitle("As acções deverão ser geradas apenas:");
if(sec==null) { 
  sec = DOC.createSection("s4280335","");
  sec.p_name = "section_special";
  sec.p_id = "section_special";
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("As acções deverão ser geradas apenas:");
  docHTML_sectionRow row; 
boolean canAccessno_utilDays=BOI.getAttribute("no_utilDays").canAccess();

if (canAccessno_utilDays ) { 
row=sec.addRow();
if( canAccessno_utilDays) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("no_utilDays"),xattributes);
} else row.addCell();
row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s32458884");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s32458884","");
  sec.p_name = "section_taskReturn";
  sec.p_id = "section_taskReturn";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("xwfDefActivity");  sec.setTitle("");
  docHTML_sectionRow row; 
boolean canAccesstaskReturn=BOI.getAttribute("taskReturn").canAccess();
boolean canAccesstaskReportType=BOI.getAttribute("taskReportType").canAccess();

if (canAccesstaskReturn || canAccesstaskReportType ) { 
row=sec.addRow();
if( canAccesstaskReturn) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("taskReturn"),xattributes);
} else row.addCell();
if( canAccesstaskReportType) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("taskReportType"),xattributes,"","");
} else row.addCell();
} 
boolean canAccesstaskNotified=BOI.getAttribute("taskNotified").canAccess();

if (canAccesstaskNotified ) { 
row=sec.addRow();
if( canAccesstaskNotified) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("taskNotified"),xattributes);
} else row.addCell();
row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                  </table>
                  <%DOCLIST.cvui=cvui28843750;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_2" class="tab">
                  <%{ int cvui8512650=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("defActivityDepends").hasRights()){%> <IFRAME id='inc_xwfDefActivity__<%=BOI.bo_boui%>__defActivityDepends' xsrc='<%="xwfactivity_generallist.jsp?docid="+IDX+"&method=list&list_orderby=SYS_DTCREATE&parent_attribute=defActivityDepends&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_xwfDefActivity__" + BOI.bo_boui + "__defActivityDepends", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui8512650;}%>
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
<FORM name='boFormSubmit' method='post' action='xwfdefactivity_generaledit.jsp'>

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
