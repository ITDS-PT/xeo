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
    response.sendRedirect("login.jsp?returnPage=messageletter_generaledit.jsp");
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
if ((BOI==null && securityRights.hasRights(boctx,"messageLetter")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"messageLetter") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"messageLetter",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"messageLetter",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
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
nbo «messageLetter»</title>

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
<TR><TD valign=top style="padding:6px;" align=left>
<%}}%>

<% if(BOI != null) { %>
<iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>"></iframe>
<% } %>
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), null);%>
   <%if(DOC.hasCategoryRights("settings")|| DOC.hasCategoryRights("settingsRES")|| DOC.hasCategoryRights("options")|| DOC.hasCategoryRights("receipts")|| DOC.hasCategoryRights("documents")|| DOC.hasCategoryRights("attachedObjects")|| DOC.hasCategoryRights("")){%>
   <TABLE id="<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
      <TBODY>
         <TR height="25">
            <TD>
               <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                  <TBODY>
                     <TR>
                        <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                           <%if (DOC.hasCategoryRights("settings")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" name="tab_settings" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_settings" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("settings")%></SPAN><%}%><%if (DOC.hasCategoryRights("settingsRES")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" name="tab_settingsRES" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_settingsRES" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("settingsRES")%></SPAN><%}%><%if (DOC.hasCategoryRights("options")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_2" name="tab_settingsoptions" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_settingsoptions" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_2", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("options")%></SPAN><%}%><%if (DOC.hasCategoryRights("receipts")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_3" name="tab_settingsRelatorio" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_settingsRelatorio" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_3", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("receipts")%></SPAN><%}%><%if (DOC.hasCategoryRights("documents")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_4" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_4", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("documents")%></SPAN><%}%><%if (DOC.hasCategoryRights("attachedObjects")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_5" name="tab_attachedObjects" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_attachedObjects" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_5", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("attachedObjects")%></SPAN><%}%><%if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_6" name="modelo" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"modelo" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_6", DOCLIST)%>">Modelo</SPAN><%}%>
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
                  <%{ int cvui31887262=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s31380787");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s31380787","");
  sec.p_name = "section_settingsRES";
  sec.p_id = "section_settingsRES";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("messageLetter");  sec.setTitle("");
  sec.p_height="520px";
  docHTML_sectionRow row; 
boolean canAccessname=BOI.getAttribute("name").canAccess();

if (canAccessname ) { 
row=sec.addRow();
if( canAccessname) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");xattributes.put("reserveLabelSpace","no");xattributes.put("relatedAttribute","name");row.addCell("object.getExplainProperties",bodef.getAttributeRef("name"),xattributes);
} else row.addCell();
} 
boolean canAccessregister=BOI.getAttribute("register").canAccess();

if (canAccessregister ) { 
row=sec.addRow();
if( canAccessregister) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("register"),xattributes);
} else row.addCell();
} 
if (canAccessname ) { 
row=sec.addRow();
if( canAccessname) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("name"),xattributes);
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
boolean canAccessfrom=BOI.getAttribute("from").canAccess();

if (canAccessfrom ) { 
row=sec.addRow();
if( canAccessfrom) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("from"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessto=BOI.getAttribute("to").canAccess();

if (canAccessto ) { 
row=sec.addRow();
if( canAccessto) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("to"),xattributes,"","");
} else row.addCell();
} 
boolean canAccesscc=BOI.getAttribute("cc").canAccess();

if (canAccesscc ) { 
row=sec.addRow();
if( canAccesscc) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("cc"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessbcc=BOI.getAttribute("bcc").canAccess();

if (canAccessbcc ) { 
row=sec.addRow();
if( canAccessbcc) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("bcc"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessdtdoc=BOI.getAttribute("dtdoc").canAccess();

if (canAccessdtdoc ) { 
row=sec.addRow();
if( canAccessdtdoc) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("dtdoc"),xattributes);
} else row.addCell();
} 
boolean canAccesspreferedMedia=BOI.getAttribute("preferedMedia").canAccess();

if (canAccesspreferedMedia ) { 
row=sec.addRow();
if( canAccesspreferedMedia) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("preferedMedia"),xattributes);
} else row.addCell();
} 
boolean canAccessbinaryDocuments=BOI.getAttribute("binaryDocuments").canAccess();

if (canAccessbinaryDocuments ) { 
row=sec.addRow();
if( canAccessbinaryDocuments) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("binaryDocuments"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessletterFormat=BOI.getAttribute("letterFormat").canAccess();

if (canAccessletterFormat ) { 
row=sec.addRow();
if( canAccessletterFormat) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("letterFormat"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessfaxFormat=BOI.getAttribute("faxFormat").canAccess();

if (canAccessfaxFormat ) { 
row=sec.addRow();
if( canAccessfaxFormat) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("faxFormat"),xattributes,"","");
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%DOCLIST.cvui=cvui31887262;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("settingsRES")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab" style="overflow-y:auto">
                  <%{ int cvui8328592=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s23118603");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s23118603","");
  sec.p_name = "section_settingsRES";
  sec.p_id = "section_settingsRES";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("messageLetter");  sec.setTitle("");
  docHTML_sectionRow row; 
boolean canAccessforwarding=BOI.getAttribute("forwarding").canAccess();

if (canAccessforwarding ) { 
row=sec.addRow();
if( canAccessforwarding) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("forwarding"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessarquive=BOI.getAttribute("arquive").canAccess();

if (canAccessarquive ) { 
row=sec.addRow();
if( canAccessarquive) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("arquive"),xattributes);
} else row.addCell();
} 
boolean canAccesslocalArquive=BOI.getAttribute("localArquive").canAccess();

if (canAccesslocalArquive ) { 
row=sec.addRow();
if( canAccesslocalArquive) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("localArquive"),xattributes);
} else row.addCell();
} 
boolean canAccessreference=BOI.getAttribute("reference").canAccess();

if (canAccessreference ) { 
row=sec.addRow();
if( canAccessreference) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("reference"),xattributes);
} else row.addCell();
} 
row=sec.addRow();
row.addCell();
boolean canAccessstatusMessage=BOI.getAttribute("statusMessage").canAccess();

if (canAccessstatusMessage ) { 
row=sec.addRow();
if( canAccessstatusMessage) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("statusMessage"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%DOCLIST.cvui=cvui8328592;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("options")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_2" class="tab" style="overflow-y:auto">
                  <%{ int cvui27597400=DOCLIST.cvui;%>
                  <table>
                     <tr height="60px">
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s6603448");
if(sec != null)
  sec.setTitle("");
if(sec==null) { 
  sec = DOC.createSection("s6603448","");
  sec.p_name = "section_settingsoptions";
  sec.p_id = "section_settingsoptions";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("messageLetter");  sec.setTitle("");
  docHTML_sectionRow row; 
boolean canAccesswaitForResponse=BOI.getAttribute("waitForResponse").canAccess();

if (canAccesswaitForResponse ) { 
row=sec.addRow();
if( canAccesswaitForResponse) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("waitForResponse"),xattributes);
} else row.addCell();
} 
boolean canAccessrequestDeliveryReceipt=BOI.getAttribute("requestDeliveryReceipt").canAccess();

if (canAccessrequestDeliveryReceipt ) { 
row=sec.addRow();
if( canAccessrequestDeliveryReceipt) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("requestDeliveryReceipt"),xattributes);
} else row.addCell();
} 
boolean canAccessrequestReadReceipt=BOI.getAttribute("requestReadReceipt").canAccess();

if (canAccessrequestReadReceipt ) { 
row=sec.addRow();
if( canAccessrequestReadReceipt) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("requestReadReceipt"),xattributes);
} else row.addCell();
} 
boolean canAccesspriority=BOI.getAttribute("priority").canAccess();

if (canAccesspriority ) { 
row=sec.addRow();
if( canAccesspriority) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("priority"),xattributes);
} else row.addCell();
} 
boolean canAccessresponseTo=BOI.getAttribute("responseTo").canAccess();

if (canAccessresponseTo ) { 
row=sec.addRow();
if( canAccessresponseTo) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("responseTo"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessforwardFrom=BOI.getAttribute("forwardFrom").canAccess();

if (canAccessforwardFrom ) { 
row=sec.addRow();
if( canAccessforwardFrom) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("forwardFrom"),xattributes,"","");
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                  </table>
                  <%DOCLIST.cvui=cvui27597400;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("receipts")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_3" class="tab" style="overflow-y:auto">
                  <%{ int cvui19656043=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("receipts").hasRights()){%> <IFRAME id='inc_messageLetter__<%=BOI.bo_boui%>__receipts' xsrc='<%="messagesystem_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=receipts&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_messageLetter__" + BOI.bo_boui + "__receipts", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui19656043;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("documents")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_4" class="tab" style="overflow-y:auto">
                  <%{ int cvui15743632=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("documents").hasRights()){%> <IFRAME id='inc_messageLetter__<%=BOI.bo_boui%>__documents' xsrc='<%="ebo_document_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=documents&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_messageLetter__" + BOI.bo_boui + "__documents", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui15743632;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("attachedObjects")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_5" class="tab" style="overflow-y:auto">
                  <%{ int cvui26531028=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("attachedObjects").hasRights()){%> <IFRAME id='inc_messageLetter__<%=BOI.bo_boui%>__attachedObjects' xsrc='<%="messageletter_attachedobjects_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=attachedObjects&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_messageLetter__" + BOI.bo_boui + "__attachedObjects", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui26531028;}%>
               </DIV>
               <%}%>
               <%if (BOI.getMode()==netgest.bo.runtime.boObject.MODE_EDIT_TEMPLATE ){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_6" class="tab" style="overflow-y:auto">
                  <IFRAME   onload="winmain().loaded(getIDX())" id='inc_TEMPLATE__<%=BOI.bo_boui%>' xsrc='<%="ebo_template_generaledit.jsp?docid="+IDX+"&menu=no&fromObj=y&method=edit&boui="+BOI.getAttribute("TEMPLATE").getValueString()+"&ctx_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.AREA, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_TEMPLATE__" + String.valueOf(BOI.bo_boui), DOCLIST)%>'></IFRAME>
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
<FORM name='boFormSubmit' method='post' action='messageletter_generaledit.jsp'>

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
