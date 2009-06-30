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
    response.sendRedirect("login.jsp?returnPage=ebo_filter_generaledit.jsp");
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
if ((BOI==null && securityRights.hasRights(boctx,"Ebo_Filter")) || 
(BOI!=null && BOI.exists() && securityRights.hasRights(boctx,"Ebo_Filter") && securityOPL.canRead(BOI)) || 
(BOI!=null && !BOI.exists() && securityRights.hasRights(boctx,"Ebo_Filter",securityRights.ADD)) 
) {
if (BOI!=null && BOI.exists() && !BOI.isEnabledforRequest &&(!securityRights.hasRights(boctx,"Ebo_Filter",securityRights.WRITE) ||  !securityOPL.canWrite(BOI) && !BOI.isEnabledforRequest)) { 
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

 if(!BOI.exists() && request.getParameter("xmlFilter") != null){

     boObject newObject = boObject.getBoManager().loadObject( boctx , "Ebo_ClsReg","NAME='" + (String)request.getParameter("masterObjectClass")+"'");
     BOI.getAttribute("masterObjectClass").setValueLong(newObject.getBoui());
     BOI.getAttribute("onlyObjects").setValueString((String)request.getParameter("onlyObjects"));
     
     bridgeHandler bdetails = BOI.getBridge("details");
     if(bdetails.isEmpty()){ 
         String xmlFilter = (String)request.getParameter("xmlFilter");
         ngtXMLHandler xh = new ngtXMLHandler(xmlFilter).getFirstChild();
         ngtXMLHandler[] childnodes = xh.getChildNodes(); 
         if (childnodes.length > 0) {
                for (int i = 0; i < childnodes.length; i++) 
                {
                    boObject aux = bdetails.addNewObject("Ebo_FilterQuery");
                    ngtXMLHandler[] child = childnodes[i].getChildNodes();
                    for(int j=0; j< child.length;j++) {
                        String pname = child[j].getNodeName();
                        if("join".equals(pname)){
                            aux.getAttribute("joinQuery").setValueString(child[j].getText());
                        } else  if("attribute".equals(pname)){
                            aux.getAttribute("attributeName").setValueString(child[j].getText());
                        } else  if("condition".equals(pname)){
                            aux.getAttribute("operator").setValueString(child[j].getText());
                        } else  if("value".equals(pname)){
                            aux.getAttribute("value").setValueString(child[j].getText());
                        } else  if("question".equals(pname)){                       
                            aux.getAttribute("question").setValueString(child[j].getText());
                        }
                    }
                }
         }
    }
}
%><script language="javascript">
function buttonExecute(){
		var element = document.getElementsByTagName('textArea');
		var boui = boFormSubmit.BOUI.value;
		for(var i=0; i < element.length ; i++)
    {
			if(element[i].name && element[i].name.indexOf('__boql') != -1)
			{
				var value = escape(element[i].value);
				boqlFrame.location.href='__queryResult.jsp?boqlQuery='+value+'&objectBoui='+getObjectBoui();	
			}
		}    
}
function buttonClean(){
		var element = document.getElementsByTagName('textArea');
		var boui = boFormSubmit.BOUI.value;
		boqlFrame.location.href='__queryResult.jsp?objectBoui='+getObjectBoui();		
		frQueryResult.location.href='__queryResultEmpty.jsp';
}
function getObjectBoui()
{
	var boui = boFormSubmit.BOUI.value;
	var obj = document.getElementById('tblLookEbo_Filter__'+boui+'__masterObjectClass');
	if(obj)
	{
		try
		{
			return obj.firstChild.firstChild.firstChild.firstChild.firstChild.boui;
		}
		catch(e){}
	}
	return "";
}
</script><%

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
nbo «Ebo_Filter»</title>

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
<TR><TD valign=top style="padding:10px" align=left>
<%}}%>

<% if(BOI != null) { %>
<iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>"></iframe>
<% } %>
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <%DOCLIST.cvui=DOCLIST.getVui(IDX, BOI.getBoui(), null);%>
   <%if(DOC.hasCategoryRights("global")|| DOC.hasCategoryRights("queryBoql")|| DOC.hasCategoryRights("share")){%>
   <TABLE id="<%=DOCLIST.cvui%>" class="layout" cellspacing="0" cellpadding="0">
      <TBODY>
         <TR height="25">
            <TD>
               <TABLE cellpadding="0" cellspacing="0" class="tabBar" id="<%=DOCLIST.cvui%>_body" onkeyup="so('<%=DOCLIST.cvui%>');onKeyUpTab_std(event)" onmouseover="so('<%=DOCLIST.cvui%>');onOverTab_std(event)" onmouseout="so('<%=DOCLIST.cvui%>');onOutTab_std(event)" ondragenter="so('<%=DOCLIST.cvui%>');ondragenterTab_std(event)" ondragover="so('<%=DOCLIST.cvui%>');ondragoverTab_std(event)" onclick="so('<%=DOCLIST.cvui%>');onClickTab_std(event)">
                  <TBODY>
                     <TR>
                        <TD style="padding:0px" id="<%=DOCLIST.cvui%>_tabs" valign="bottom" noWrap="yes">
                           <%if (DOC.hasCategoryRights("global")){ %><SPAN class="tab tabOn" id="<%=DOCLIST.cvui%>_tabheader_0" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"null" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_0", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("global")%></SPAN><%}%><%if (DOC.hasCategoryRights("queryBoql")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_1" name="tab_boql" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_boql" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_1", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("queryBoql")%></SPAN><%}%><%if (DOC.hasCategoryRights("share")){ %><SPAN class="tab" id="<%=DOCLIST.cvui%>_tabheader_2" name="tab_share" tabNumber="<%=DOCLIST.cvui%>" tabIndex="<%=DOC.getTabindex(DOC.PANEL, BOI==null?"tab_share" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), String.valueOf(DOCLIST.cvui) + "_tabheader_2", DOCLIST)%>"><%=DOC.getCategoryLabel_for_TAB_Header("share")%></SPAN><%}%>
                        </TD>
                     </TR>
                  </TBODY>
               </TABLE>
               <HR class="tabGlow" id="hrSelTab<%=DOCLIST.cvui%>"/>
               <HR class="tabGlow" id="hrTab<%=DOCLIST.cvui%>"/>
            </TD>
         </TR>
         <TR>
            <TD><%if (DOC.hasCategoryRights("global")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_0" class="tab">
                  <%{ int cvui16229835=DOCLIST.cvui;%>
                  <table height="100%" width="100%">
                     <tr height="1%">
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s10868697");
if(sec != null)
  sec.setTitle(DOC.getCategoryLabel("general","global"));
if(sec==null) { 
  sec = DOC.createSection("s10868697","");
  sec.p_name = "";
  sec.p_id = "";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("Ebo_Filter");  sec.setTitle(DOC.getCategoryLabel("general","global"));
  sec.p_height="30px";
  docHTML_sectionRow row; 
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
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr>
                        <td>
                           <%
String parameters1246043465847 = "object=" + (BOI.getAttribute("masterObjectClass").getObject() != null ? BOI.getAttribute("masterObjectClass").getObject().getAttribute("name").getValueString() : "")+"&queryBoui=" + (String.valueOf(BOI.getBoui()))+"&docid=" + ( DOC.getDocIdx() ) ;
%>
<IFRAME  src="__queryBuilder.jsp?<%=parameters1246043465847%>" xsrc="__queryBuilder.jsp?<%=parameters1246043465847%>" frameBorder='0' width='100%' height='100%' scrolling='no' id='frIDQueryBuilder' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null? "parameters" : BOI.getName(), String.valueOf(parent_boui==null?( BOI==null?0 : BOI.getBoui()  ): new Long(parent_boui).longValue()), "frIDQueryBuilder", DOCLIST)%>'></IFRAME></td>
                     </tr>
                  </table>
                  <%DOCLIST.cvui=cvui16229835;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("queryBoql")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_1" class="tab">
                  <%{ int cvui33433207=DOCLIST.cvui;%>
                  <table height="100%" width="100%">
                     <tr height="0%">
                        <td valign="top">
                           <iframe style="display:none" id="boqlFrame" src="__queryResult.jsp?loading=true">
                              <table/>
                           </iframe></td>
                     </tr>
                     <tr height="1%">
                        <td class="sec bar">Query</td>
                     </tr>
                     <tr height="40%">
                        <td>
                           <%
BOI=currObjectList.getObject();sec = DOC.getSection("s9395715");
if(sec != null)
  sec.setTitle("Query");
if(sec==null) { 
  sec = DOC.createSection("s9395715","");
  sec.p_name = "";
  sec.p_id = "";
sec.p_showLabel=false;
  bodef=boDefHandler.getBoDefinition("Ebo_Filter");  sec.setTitle("Query");
  sec.p_height="100%";
  docHTML_sectionRow row; 
boolean canAccessboql=BOI.getAttribute("boql").canAccess();

if (canAccessboql ) { 
row=sec.addRow();
if( canAccessboql) {
xattributes=new Hashtable();
xattributes.put("showlabel","no");row.addCell(bodef.getAttributeRef("boql"),xattributes);
} else row.addCell();
} 
} //endCreateSection
sec.render(pageContext,currObjectList,DOCLIST,DOC);%></td>
                     </tr>
                     <tr height="2%">
                        <td align="right">
                           <table>
                              <tr>
                                 <td>
                                    <button style="width:70px" onClick="buttonClean()" tabIndex="<%=DOC.getTabindex(DOC.IFRAME, BOI==null? "Ebo_Filter" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "button" + String.valueOf(BOI==null?0:BOI.bo_boui), DOCLIST)%>">Reiniciar</button></td>
                                 <td>
                                    <button style="width:70px" onClick="buttonExecute()" tabIndex="<%=DOC.getTabindex(DOC.IFRAME, BOI==null? "Ebo_Filter" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "button" + String.valueOf(BOI==null?0:BOI.bo_boui), DOCLIST)%>">Executar</button></td>
                              </tr>
                           </table></td>
                     </tr>
                     <tr height="1%">
                        <td class="sec bar">Resultado</td>
                     </tr>
                     <tr height="56%">
                        <td>
                           <%
String parameters1246043465848 = "docid=" + ( DOC.getDocIdx() ) ;
%>
<IFRAME  src="__queryResultEmpty.jsp?<%=parameters1246043465848%>" xsrc="__queryResultEmpty.jsp?<%=parameters1246043465848%>" frameBorder='0' width='100%' height='100%' scrolling='no' id='frQueryResult' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI==null? "parameters" : BOI.getName(), String.valueOf(parent_boui==null?( BOI==null?0 : BOI.getBoui()  ): new Long(parent_boui).longValue()), "frQueryResult", DOCLIST)%>'></IFRAME></td>
                     </tr>
                  </table>
                  <%DOCLIST.cvui=cvui33433207;}%>
               </DIV>
               <%}%>
               <%if (DOC.hasCategoryRights("share")){ %>
               <DIV id="<%=DOCLIST.cvui%>_tabbody_2" class="tab">
                  <%{ int cvui6628237=DOCLIST.cvui;%>
                  <div width="100%" height="100%"><% if(BOI.getAttribute("share").hasRights()){%> <IFRAME id='inc_Ebo_Filter__<%=BOI.bo_boui%>__share' xsrc='<%="ebo_filter_share_generallist.jsp?docid="+IDX+"&method=list&parent_attribute=share&parent_boui="+BOI.bo_boui %>' frameBorder=0 width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "inc_Ebo_Filter__" + BOI.bo_boui + "__share", DOCLIST)%>'></IFRAME><% } %> </div>
                  <%DOCLIST.cvui=cvui6628237;}%>
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
<FORM name='boFormSubmit' method='post' action='ebo_filter_generaledit.jsp'>

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
