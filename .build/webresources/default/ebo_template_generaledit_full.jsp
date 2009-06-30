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
    response.sendRedirect("login.jsp?returnPage=ebo_template_generaledit_full.jsp");
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
if(request.getParameter("look_object")==null || securityRights.hasRights(boctx,request.getParameter("look_object"))){ if( currObjectList != null ) currObjectList.first();



%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
nbo «Ebo_Template»</title>

<%= DOC.getController().getPresentation().writeCSS() %>
<%= DOC.getController().getPresentation().writeJS() %>

<%@ include file='boheaders.jsp'%>
<script>
<%=DOC.getScriptToRunOnClient()%></script>
<script>var objLabel='Lista de  Modelo';</script>
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
<% if(!(request.getParameter("toClose") != null && !DOC.haveErrors())) {%>
<%if(DOC.haveErrors()) {%>
<div class='error'>
 <%=DOC.getHTMLErrors()%>
</div>
<%}%>

<% if(BOI != null) { %>
<iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>"></iframe>
<% } %>
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), null);%>
   <%if(DOC.hasCategoryRights("identification")|| DOC.hasCategoryRights("domainobjects")|| DOC.hasCategoryRights("extendAttribute")|| DOC.hasCategoryRights("DAO")|| DOC.hasCategoryRights("mappingAttributes")){%>
   <TABLE id="<%=DOCLIST.cvui%>" class="layout" bo_node="identification;dates;relations" cellspacing="0" cellpadding="0">
      <TBODY>
         <TR height="25">
            <TD>
               <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                  <TBODY>
                     <TR>
                        <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                           <%if (DOC.hasCategoryRights("identification")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("identification")%></SPAN><%}%><%if (DOC.hasCategoryRights("domainobjects")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("domainobjects")%></SPAN><%}%><%if (DOC.hasCategoryRights("extendAttribute")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_2" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_2", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("extendAttribute")%></SPAN><%}%><%if (DOC.hasCategoryRights("DAO")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_3" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_3", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("DAO")%></SPAN><%}%><%if (DOC.hasCategoryRights("mappingAttributes")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_4" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_4", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("mappingAttributes")%></SPAN><%}%>
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
                  <%{ int cvui16268600=DOCLIST.cvui;%>
                  <%
BOI=currObjectList.getObject();sec = DOC.getSection("s24858107");
if(sec != null)
  sec.setTitle(DOC.getCategoryLabel("general","identification"));
if(sec==null) { 
  sec = DOC.createSection("s24858107","");
  sec.p_name = "";
  sec.p_id = "";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("Ebo_Template");  sec.setTitle(DOC.getCategoryLabel("general","identification"));
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
boolean canAccessmasterObjectClass=BOI.getAttribute("masterObjectClass").canAccess();

if (canAccessmasterObjectClass ) { 
row=sec.addRow();
if( canAccessmasterObjectClass) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("masterObjectClass"),xattributes,"","");
} else row.addCell();
} 
boolean canAccessbeginDate=BOI.getAttribute("beginDate").canAccess();

if (canAccessbeginDate ) { 
row=sec.addRow();
if( canAccessbeginDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("beginDate"),xattributes);
} else row.addCell();
} 
boolean canAccessendDate=BOI.getAttribute("endDate").canAccess();

if (canAccessendDate ) { 
row=sec.addRow();
if( canAccessendDate) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("endDate"),xattributes);
} else row.addCell();
} 
boolean canAccessclassification1=BOI.getAttribute("classification1").canAccess();
boolean canAccessclassification2=BOI.getAttribute("classification2").canAccess();

if (canAccessclassification1 || canAccessclassification2 ) { 
row=sec.addRow();
if( canAccessclassification1) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("classification1"),xattributes,"","");
} else row.addCell();
if( canAccessclassification2) {
xattributes=new Hashtable();
xattributes.put("showlabel","yes");row.addCell(bodef.getAttributeRef("classification2"),xattributes,"","");
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%>

                  <%DOCLIST.cvui=cvui16268600;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("domainobjects")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab">
                  <%{ int cvui3986155=DOCLIST.cvui;%>
                  <div height="100%" width="100%"><% if(BOI.getAttribute("fromObjectClass").hasRights()){%> <IFRAME id='inc_Ebo_Template__<%=BOI.bo_boui%>__fromObjectClass' xsrc='<%="ebo_clsreg_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=fromObjectClass&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_Ebo_Template__" + BOI.bo_boui + "__fromObjectClass", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui3986155;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("extendAttribute")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_2" class="tab">
                  <%{ int cvui20661894=DOCLIST.cvui;%>
                  <div height="100%" width="100%"><% if(BOI.getAttribute("extendAttribute").hasRights()){%> <IFRAME id='inc_Ebo_Template__<%=BOI.bo_boui%>__extendAttribute' xsrc='<%="ebo_template_extendattribute_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=extendAttribute&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_Ebo_Template__" + BOI.bo_boui + "__extendAttribute", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui20661894;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("DAO")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_3" class="tab">
                  <%{ int cvui9646356=DOCLIST.cvui;%>
                  <div height="100%" width="100%"><% if(BOI.getAttribute("DAO").hasRights()){%> <IFRAME id='inc_Ebo_Template__<%=BOI.bo_boui%>__DAO' xsrc='<%="ebo_template_dao_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=DAO&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_Ebo_Template__" + BOI.bo_boui + "__DAO", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui9646356;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("mappingAttributes")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_4" class="tab">
                  <%{ int cvui24845688=DOCLIST.cvui;%>
                  <div height="100%" width="100%"><% if(BOI.getAttribute("mappingAttributes").hasRights()){%> <IFRAME id='inc_Ebo_Template__<%=BOI.bo_boui%>__mappingAttributes' xsrc='<%="ebo_map_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=mappingAttributes&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_Ebo_Template__" + BOI.bo_boui + "__mappingAttributes", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui24845688;}%>
               </DIV>
               <%}%>
            </TD>
         </TR>
      </TBODY>
   </TABLE>
   <%}%>
</form>
<%}%>
<FORM name='boFormSubmit' method='post' action='ebo_template_generaledit_full.jsp'>

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
 options.put("p_typeForm","1");
 options.put("jspName",this.getClass().getName());
%>

<%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>

</FORM></body></html><%
} else response.sendRedirect("dialogBoxSecurityWarning.htm"); } finally {
if (boctx!=null)boctx.close();if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
}%>
