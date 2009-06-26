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
if( request.getParameter("docid")==null){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=actionobject_ro_generallist.jsp");
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
docHTML_groupGrid gridG;
Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
boObject BOI=( boObject )currObjectList.getObject();
String[] attributesToRender=new String[3];
attributesToRender[0]="workHistory";
attributesToRender[1]="RO";
attributesToRender[2]="DAO";



%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>
Object list</title>
<%@ include file='boheaders.jsp'%><script>
<%=DOC.getScriptToRunOnClient()%></script>
<script>
var objLabel='Lista de  Action Object';var objDescription='Solicitação';</script>
<% String scriptToRun=null;
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("addToCtxParentBridge") != null)
 {
    String look_parentBoui      = request.getParameter("ctxParent");
    String look_parentAttribute = request.getParameter("addToCtxParentBridge");
    String clientIDX            = request.getParameter("relatedClientIDX");
    String relDocid             = request.getParameter("ctxParentIdx");
    docHTML xd=DOCLIST.getDocByIDX( netgest.utils.ClassUtils.convertToInt(relDocid),DOC.getEboContext());
    boObject obj=xd.getObject(Long.parseLong(look_parentBoui));
    StringBuffer toP=new StringBuffer();
     StringBuffer nameH = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    scriptToRun="updateLookupAttribute()";
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
            var xhref=wDocfrms[z].contentWindow.location.href;
            wDocfrms[z].contentWindow.location.href=setUrlAttribute(xhref,'boFormSubmitXml','');
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
        var ifrm=w.htm.getElementsByTagName('IFRAME');
       var xw;
       for(var i=0; i < ifrm.length ; i++)
       {
           if ( ifrm[i].id == "frm$<%=clientIDX%>" )
           {
                updateFrame(ifrm[i]);
           }
       }
   }
 }
</script>
<%}%>
<% 
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("searchClientIdx") != null &&BOI.exists()){
  String searchClientIdx=request.getParameter("searchClientIdx");
  scriptToRun="updateObj()";
%>
<script language="javascript">
function updateObj(){
  var w=parent.ndl[<%=searchClientIdx%>];
  if( w ){
      var ifrm=w.htm.getElementsByTagName('IFRAME');
      var xw;
      ifrm[0].contentWindow.findframe.submitSelectOne2(<%=BOI.bo_boui%>);
  }
}
</script>
<%}%>
<% StringBuffer toP=new StringBuffer();
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("actRenderObj") != null &&BOI.exists()){
  String clientIDX=request.getParameter("actIdxClient");
  String actRenderObj=request.getParameter("actRenderObj");
  String actRenderAttribute=request.getParameter("actRenderAttribute");
  scriptToRun="updateLookupAttribute()";
  boObject obj=DOC.getObject(Long.parseLong(actRenderObj));
  AttributeHandler attr= obj.getAllAttributes().get( actRenderAttribute );
  StringBuffer v= new StringBuffer( attr.getValueString() );
  StringBuffer nameH = new StringBuffer();
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
        attr.isDisabled(),
        !attr.isVisible(),
        obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
        );
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
function updateInFrames()
 {
   var windowToUpdate="<%=clientIDX%>";
   var w=parent.ndl[windowToUpdate];
   if(w)
   {
       var ifrm=w.htm.getElementsByTagName('IFRAME');
       var xw;
       for(var i=0; i < ifrm.length ; i++)
       {
           var xok=updateFrame(ifrm[i]);
           if ( xok ) break;
       }
   }
 }
  function updateLookupAttribute(){
   var windowToUpdate="<%=clientIDX%>";
   var xok=false;
   var w=parent.ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm.getElementsByTagName('IFRAME');
       var xw;
       for(var i=0; i < ifrm.length ; i++){
          xw=ifrm[i].contentWindow.document.all;
          var toR=[];
          for(var z=0; z< xw.length; z++){
            if ( xw[z].id == "tblLook<%=nameH%>" ){
             toR[toR.length]=xw[z];
            }
          }
          for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                var xele=ifrm[i].contentWindow.document.getElementsByName('<%=nameH%>');
                for( var z=0; z<xele.length;z++)
                      xele[z].original=xele[0].value;
                xok=true;
          }
       }
       if ( !xok ) updateInFrames();
   }
 }
</script>
<%}%>
<%if (scriptToRun==null) {%>
<body onload="<%=DOC.getActionOnClient()%>">
<%}
else{%>
<body onload="<%=scriptToRun%>">
<%
}
%>
<%if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("actRenderObj") != null &&BOI.exists()){%>
    <div id="toRender" style='display:none'>
    <%=toP%>
</div>
<%}%>
<% if(!(request.getParameter("toClose") != null && !DOC.haveErrors())) {%>

<form class="objectForm" name="boForm" id="<%=IDX%>">
   <%
BOI=( boObject )currObjectList.getObject();grid = DOC.getGRID("__listcardid.jsp");
if(grid==null) { 
  bodef=boDefHandler.getBoDefinition("boObject");  grid = DOC.createGRID("g18233","std",true);
  grid.setTitle();  
xattributes=new Hashtable();
xattributes.put("width","200");grid.addColSpecial("childObject.cardId",200,xattributes);
} //endCreategrid

docHTML_grid.GridProperties gp = new docHTML_grid.GridProperties();
gp.barFilter = true;
gp.barStatus = true;
gp.canExpandRows = true;
gp.canSelectRows = true;
gp.colsFooter = false;
gp.editAttributes = false;
gp.listBridgeAttributes = false;
gp.menu = "YES";
gp.renderOnlyCardID = true;
gp.showIcon = true;
gp.showPreview = false;
gp.showSelectNone = false;
gp.showState = true;
gp.title = "Objectos Relacionados";

grid.render( pageContext, currObjectList , DOC , DOCLIST , gp );%>

</form>
<%}%>
<FORM name='boFormSubmit' method='get'>
<INPUT type='hidden' name='boFormSubmitXml' />
<% if( request.getParameter("ctxParent") != null ) { %> 
<INPUT type='hidden' name=ctxParent value='<%=request.getParameter("ctxParent")%>' />
<% } %> 
<% if( request.getParameter("addToCtxParentBridge") != null ) { %> 
<INPUT type='hidden' name=addToCtxParentBridge value='<%=request.getParameter("addToCtxParentBridge")%>' />
<% } %> 
<% if( request.getParameter("relatedDocid") != null ) { %> 
<INPUT type='hidden' name=relatedDocid value='<%=request.getParameter("relatedDocid")%>' />
<% } %> 
<% if( request.getParameter("parent_attribute") != null ) { %> 
<INPUT type='hidden' name=parent_attribute value='<%=request.getParameter("parent_attribute")%>' />
<% } %> 
<% if( request.getParameter("parent_boui") != null ) { %> 
<INPUT type='hidden' name=parent_boui value='<%=request.getParameter("parent_boui")%>' />
<% } %> 
<% if( request.getParameter("method") != null ) { %> 
<INPUT type='hidden' name=method value='<%=request.getParameter("method")%>' />
<% } %> 
<% if( request.getParameter("searchClientIdx")!=null ) { %>
<INPUT type='hidden' name='searchClientIdx' value='<%=request.getParameter("searchClientIdx")%>' />
<% } %>
<% if( request.getParameter("actIdxClient") != null ) { %> 
<INPUT type='hidden' name=actIdxClient value='<%=request.getParameter("actIdxClient")%>' />
<% } %>
<% if( request.getParameter("actRenderObj") != null ) { %> 
<INPUT type='hidden' name=actRenderObj value='<%=request.getParameter("actRenderObj")%>' />
<% } %>
<% if( request.getParameter("actRenderAttribute") != null ) { %>
<INPUT type='hidden' name=actRenderAttribute value='<%=request.getParameter("actRenderAttribute")%>' />
<% } %>
<% if( request.getParameter("ctxParentIdx") != null ) { %> 
<INPUT type='hidden' name='ctxParentIdx' value='<%=request.getParameter("ctxParentIdx")%>' />
<% } %> 
<% if( request.getParameter("toClose") != null && !DOC.haveErrors()) { %> 
<INPUT type='hidden' name='toClose' value='y' />
<% } %> 
<% if( masterdoc ) { %>
<INPUT type='hidden' name=masterdoc value=true' />
<% } %>
<INPUT type='hidden' name='boFormSubmitMode' />
<INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
<INPUT type='hidden' name='boFormSubmitId' />
<% if (currObjectList.getParent()!=null) { %><INPUT type='hidden' name='BOUI' value='<%=currObjectList.getParent().getBoui()%>'/>
<INPUT type='hidden' name='boiChanged' value='<%=currObjectList.getParent().isChanged()%>' />
<%}%>
<INPUT type='hidden' value='<%=IDX%>' name='docid' />
<INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM></body></html><%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
