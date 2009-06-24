<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import=" netgest.bo.presentation.render.*"%>
<%@ page import=" netgest.bo.presentation.render.elements.*"%>
<%@ page import=" netgest.bo.presentation.render.ie.scripts.FunctionBuilder"%>
<%@ page import=" netgest.bo.presentation.render.ie.scripts.ImportBuilder"%>
<%@ page import=" netgest.bo.presentation.render.ie.scripts.ScriptBuilder"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
PrintWriter _out = new PrintWriter(out);
PageController control = new PageController();
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=task_generalmywork.jsp");
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
idbolist=ctrl[1];
docHTML doc = DOCLIST.getDOC(IDX);
boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);
boObject BOI;
String method=request.getParameter( "method" );
String inputMethod=request.getParameter( "method" );
String requestedBoui=request.getParameter( "boui" );
if ( currObjectList == null ) BOI=null;
else BOI=currObjectList.getObject();
if(request.getParameter("objectBoui")!=null){
BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
long[] a_boui = {BOI.getBoui()};
currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
if(!currObjectList.haveBoui(a_boui[0]))
currObjectList.inserRow(a_boui[0]);}
String objName = request.getParameter("objectName");
String forceKey = request.getParameter("forceKey");
String boql = request.getParameter("boql");
String label = request.getParameter("label");
String imagem = request.getParameter("imagem");
String query_boui = request.getParameter("query_boui");
String checkOnClick = request.getParameter("checkOnClickEvent");
String checkValues = request.getParameter("checkValues");
String clause_where1=null;
boolean hasHintNoSecurity = (boql!=null&&boql.indexOf("NO_SECURITY")>-1);
if ( query_boui!=null )
{
   clause_where1=netgest.bo.userquery.userquery.userQueryToBoql_ClauseWhere( boctx,ClassUtils.convertToLong( query_boui ), null );
   hasHintNoSecurity = (netgest.bo.userquery.userquery.hasHintNoSecurity( boctx,ClassUtils.convertToLong( query_boui ), null ) ? true:hasHintNoSecurity);
}
if ( boql != null )
{
    if ( clause_where1!= null )
    {
      boql = boql+" and ( "+ clause_where1+" )";
    }
    if(hasHintNoSecurity && boql.indexOf("NO_SECURITY")==-1)
    {
        int si = boql.toUpperCase().indexOf("SELECT");
        boql = boql.substring(0, si) + " /*NO_SECURITY*/" + boql.substring(si+1);  
    }
}
else if ( clause_where1!= null )
{
    if(hasHintNoSecurity)
    {
        boql =" select /*NO_SECURITY*/"+objName+" ext where "+ clause_where1;
    }
    else
    {
        boql =" select "+objName+" ext where "+ clause_where1;
    }
}

String form = request.getParameter("form");
String pageName = request.getParameter("pageName");
String url = request.getParameter("url");
if(url==null) url = "__explorer.jsp?pageName="+pageName+"&objectName=";

//Permissões de leitura para este tipo de Objecto
if ( objName!=null && !securityRights.hasRights(boctx,objName)) 
{
  response.sendRedirect("dialogBoxSecurityWarning.htm");
}
  
if(objName != null && form != null)
{

	url = url + objName + "&form="+form + ((label!= null && label.length() > 0) ? ("&label="+label):"")+
    ((imagem!= null && imagem.length() > 0) ? ("&imagem="+imagem):"")
    ;
}
else if(objName != null && boql != null)
{
	url = url + objName + "&boql="+boql + ((label!= null && label.length() > 0) ? ("&label="+label):"")+
    ((imagem!= null && imagem.length() > 0) ? ("&imagem="+imagem):"")
    ;
}
else if(objName != null)
{
	url = url + objName + ((label!= null && label.length() > 0) ? ("&label="+label):"")+
    ((imagem!= null && imagem.length() > 0) ? ("&imagem="+imagem):"")
    ;
}
else
{
	return;
}
boDefHandler xbodef = boDefHandler.getBoDefinition(objName);
String key= request.getParameter("explorer_key") ;
if ( key == null )
{
    if(form != null && !"".equals(form))
    {
        key=xbodef.getPath("Viewers.General.forms." + form + ".explorer").getAttribute("name");
        if(boql != null && !"".equals(boql))
        {
            key=key+boql.hashCode();
        }
        key = key+doc.getEboContext().getBoSession().getPerformerBoui();
    }
    else if(boql != null && !"".equals(boql))
    {
        key=objName + boql.hashCode() + doc.getEboContext().getBoSession().getPerformerBoui();    
    }
    else
    {
        key=objName + doc.getEboContext().getBoSession().getPerformerBoui();
    }
}
Explorer explorer = null;
if((explorer = ExplorerServer.getExplorer(key)) == null)
{   
    String s = pageContext.getPage().getClass().getName();
    if(objName != null && form != null && forceKey != null)
    {
        explorer = Explorer.getExplorerWithForm(objName, form, doc, DOCLIST, pageName, boql, forceKey);
    }
    else if(objName != null && form != null)
    {
        explorer = Explorer.getExplorerWithForm(objName, form, doc, DOCLIST, pageName, boql);
    }
    else if(objName != null && boql != null)
    {
        explorer = Explorer.getExplorer(objName, boql, doc, DOCLIST, pageName);
    }
    else if(objName != null)
    {
        explorer = Explorer.getExplorer(objName, doc, DOCLIST, pageName);
    }
    else
    {
        return;
    }
//    explorer.setShowPreview( false );
}

if ( true ){ if( currObjectList != null ) currObjectList.first();
Page _page = new Page();
_page.setTitle("Explorer objName["+(objName==null ? "":objName)+"] form["+(form==null ? "":form)+"] label["+(label==null ? "":label)+"] boql["+(boql==null?"":boql)+"]");
_page.setMenuFrame(String.valueOf(IDX));
_page.addImport(ImportBuilder.IMP_CSS_EXPLORER);
_page.addScript(ScriptBuilder.SC_JS_EXPLORER);
_page.addScript(ScriptBuilder.SC_JS_XEO_LANG);
_page.addFunction(ScriptBuilder.getExplorerLabel(objName, label, imagem));
_page.addFunction(doc.getScriptToRunOnClient());
_page.addFunction("document.onselectstart=function(){var s = event.srcElement.tagName;if (s != \"INPUT\" && s != \"TEXTAREA\") event.returnValue = false;}");
_page.addFunction("function runBeforeMethodExec(methodName){}");
_page.addFunction("function runAfterMethodExec(){setLastMethodToRun(\"\");}");
_page.addFunction(FunctionBuilder.TREE_VARS);
_page.addFunction(FunctionBuilder.FUNC_ACTIVETHIS);
_page.addFunction(FunctionBuilder.ON_MOUSE_DOWN);
_page.addFunction(FunctionBuilder.FUNC_GET_ELEMENT);
_page.addFunction(FunctionBuilder.FUNC_GET_TO_ELEMENT);
_page.addFunction(FunctionBuilder.FUNC_GETRUNTIME_STYLE);
_page.addFunction(FunctionBuilder.FUNC_SO);
_page.addFunction(FunctionBuilder.FUNC_SETGROUP);
_page.addFunction(FunctionBuilder.FUNC_SETPARAM);
_page.addFunction(FunctionBuilder.FUNC_SETPREVIEWDOWN);
_page.addFunction(FunctionBuilder.FUNC_SETPREVIEWRIGHT);
_page.addFunction(FunctionBuilder.FUNC_SETPREVIEWOFF);

if(checkOnClick != null && !"".equals(checkOnClick))
{
    explorer.setCheckOnclickEvent(checkOnClick);
}
else
{
    explorer.setCheckOnclickEvent(null);
}
if(checkValues != null  && !"".equals(checkValues))
{
    explorer.setCheckValues(checkValues);
}
else
{
    explorer.setCheckValues(null);
}
if(explorer.hasBoqlInitJSP() && !explorer.isBoqlInitSet())
{
    _page.setBodyParameters("ondragenter=\"activethis()\" onload=\""+doc.getActionOnClient()+"; runAfterMethodExec(); startInitialFilter('"+explorer.getBoqlInitJSP()+"','"+explorer.getKey()+"'); treatFocus();\"");
}
else
{
    _page.setBodyParameters("ondragenter=\"activethis()\" onload=\""+doc.getActionOnClient()+"; runAfterMethodExec(); treatFocus();\"");
}
Form f = new Form("boForm", null, "objectForm", String.valueOf(IDX), null);
_page.addElement(f);
f.addElement(explorer);
f.setWindowGridKey(explorer.getKey());
Form f2 = new Form("boFormSubmit", url, null, null,"post");
f2.addElement( new Input("hidden","explorer_key", explorer.getKey() ));
if( method != null ) { 
f2.addElement(new Input("hidden", "method", method));
%> 
<% } %> 
<% if( requestedBoui != null ) { 
f2.addElement(new Input("hidden", "boui", String.valueOf(BOI.getBoui())));
} 
f2.addElement(new Input("hidden", "boFormSubmitXml", null));
%> 
<% if( request.getParameter("object") != null ) { 
f2.addElement(new Input("hidden", "object", request.getParameter("object")));
} %> 
<% if( request.getParameter("menu") != null ) { 
f2.addElement(new Input("hidden", "menu", request.getParameter("menu")));
} %> 
<% if( request.getParameter("ctxParent") != null ) { 
f2.addElement(new Input("hidden", "ctxParent", request.getParameter("ctxParent")));
} %> 
<% if( request.getParameter("addToCtxParentBridge") != null ) { 
f2.addElement(new Input("hidden", "addToCtxParentBridge", request.getParameter("addToCtxParentBridge")));
} %> 
<% if( request.getParameter("relatedClientIDX") != null ) { 
f2.addElement(new Input("hidden", "relatedClientIDX", request.getParameter("relatedClientIDX")));
} %> 
<% if( request.getParameter("relatedDocid") != null ) {
f2.addElement(new Input("hidden", "relatedDocid", request.getParameter("relatedDocid")));
} %> 
<% if( request.getParameter("parent_attribute") != null ) { 
f2.addElement(new Input("hidden", "parent_attribute", request.getParameter("parent_attribute")));
} %> 
<% if( request.getParameter("relatedParentBridge") != null ) { 
f2.addElement(new Input("hidden", "relatedParentBridge", request.getParameter("relatedParentBridge")));
} %> 
<% if( request.getParameter("relatedParent") != null ) { 
f2.addElement(new Input("hidden", "relatedParent", request.getParameter("relatedParent")));
} %> 
<% if( request.getParameter("relatedParentDocid") != null ) { 
f2.addElement(new Input("hidden", "relatedParentDocid", request.getParameter("relatedParentDocid")));
} %> 
<% if( request.getParameter("parent_boui") != null ) { %> 
<%    if(  !( inputMethod != null && inputMethod.equalsIgnoreCase("newfromtemplate"))  ) { 
f2.addElement(new Input("hidden", "parent_boui", request.getParameter("parent_boui")));
}} %> 
<% if( request.getParameter("list_frommethod") != null ) { 
f2.addElement(new Input("hidden", "list_frommethod", request.getParameter("list_frommethod")));
} %> 
<% if( request.getParameter("searchClientIdx")!=null ) { 
f2.addElement(new Input("hidden", "searchClientIdx", request.getParameter("searchClientIdx")));
} %>
<% if( request.getParameter("actIdxClient") != null ) { 
f2.addElement(new Input("hidden", "actIdxClient", request.getParameter("actIdxClient")));
} %>
<% if( request.getParameter("actRenderObj") != null ) { 
f2.addElement(new Input("hidden", "actRenderObj", request.getParameter("actRenderObj")));
} %>
<% if( request.getParameter("actRenderAttribute") != null ) { 
f2.addElement(new Input("hidden", "actRenderAttribute", request.getParameter("actRenderAttribute")));
} %>
<% if( request.getParameter("actRenderDocid") != null ) { 
f2.addElement(new Input("hidden", "actRenderDocid", request.getParameter("actRenderDocid")));
} %>
<% if( request.getParameter("ctxParentIdx") != null ) { 
f2.addElement(new Input("hidden", "ctxParentIdx", request.getParameter("ctxParentIdx")));
} %> 
<% if( request.getParameter("toClose") != null && !doc.haveErrors()) { 
f2.addElement(new Input("hidden", "toClose", request.getParameter("y")));
} %> 
<% if( masterdoc ) { 
f2.addElement(new Input("hidden", "masterdoc", request.getParameter("true")));
} %>
<% if ( BOI!=null && !BOI.getBoDefinition().getBoCanBeOrphan() ){ 
f2.addElement(new Input("hidden", "orphan", request.getParameter("no")));
} 
f2.addElement(new Input("hidden", "boFormSubmitMode", null));
f2.addElement(new Input("hidden", "boFormSubmitId", null));
if (currObjectList!=null && currObjectList.getParent()!=null) { 
f2.addElement(new Input("hidden", "BOUI", String.valueOf(currObjectList.getParent().getBoui())));
f2.addElement(new Input("hidden", "boiChanged", String.valueOf(currObjectList.getParent().isChanged() || currObjectList.containsChangedObjects())));
}%>
<% if ( request.getParameter("boql")!=null ){ 
f2.addElement(new Input("hidden", "boql", request.getParameter("boql")));
} %>
<% if ( request.getParameter("list_fulltext")!=null ){ 
f2.addElement(new Input("hidden", "list_fulltext", request.getParameter("list_fulltext")));
} %>
<% if ( request.getParameter("list_letter")!=null ){ 
f2.addElement(new Input("hidden", "list_letter", request.getParameter("list_letter")));
} %>
<% if ( request.getParameter("list_page")!=null ){ 
f2.addElement(new Input("hidden", "list_page", request.getParameter("list_page")));
} %>
<% if ( request.getParameter("list_letter_field")!=null ){ 
f2.addElement(new Input("hidden", "list_letter_field", request.getParameter("list_letter_field")));
} %>
<% if ( request.getParameter("listmode")!=null ){ 
f2.addElement(new Input("hidden", "listmode", request.getParameter("listmode")));
} %>
<% if ( request.getParameter("showIcon")!=null ){ 
f2.addElement(new Input("hidden", "showIcon", request.getParameter("showIcon")));
} %>
<% if ( request.getParameter("showLines")!=null ){ 
f2.addElement(new Input("hidden", "showLines", request.getParameter("showLines")));
} %>
<% if ( request.getParameter("showStatus")!=null ){ 
f2.addElement(new Input("hidden", "showStatus", request.getParameter("showStatus")));
} %>
<% if ( request.getParameter("showPreview")!=null ){ 
f2.addElement(new Input("hidden", "showPreview", request.getParameter("showPreview")));
} %>
<% if ( request.getParameter("menu")!=null ){ 
f2.addElement(new Input("hidden", "menu", request.getParameter("menu")));
} %>
<% if ( request.getParameter("editAttributes")!=null ){ 
f2.addElement(new Input("hidden", "editAttributes", request.getParameter("editAttributes")));
}  
if ( request.getParameter("checkOnClickEvent")!=null && !"".equals(request.getParameter("checkOnClickEvent"))){ 
    f2.addElement(new Input("hidden", "checkOnClickEvent", request.getParameter("checkOnClickEvent")));
}
if ( request.getParameter("checkValues")!=null && !"".equals(request.getParameter("checkValues"))){ 
    f2.addElement(new Input("hidden", "checkValues", request.getParameter("checkValues")));
} 
f2.addElement(new Input("hidden", "docid", String.valueOf(IDX)));
f2.addElement(new Input("hidden", "boFormSubmitSecurity", "explorer"));
_page.addElement(f2);
try
{
    _page.writeHTML(_out, doc, DOCLIST, control);
}catch(Exception e)
{
    //ignore
    e.printStackTrace();
}
//HTMLBuilder html = new HTMLBuilder();
}
}
catch(Exception e)
{
%>
    <HTML>
        <HEAD>
        <TITLE></TITLE>
        <script>var objLabel='Explorador';</script>
        <META http-equiv=Content-Type content="text/html; charset=UTF-8">
        <META content="MSHTML 6.00.2900.2722" name=GENERATOR></HEAD>
        <BODY style="FONT-WEIGHT: bold; FONT-SIZE: 8pt; COLOR: #FF0000; FONT-FAMILY: Arial, Helvetica, sans-serif">
            <TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=0>
                <TBODY>
                    <TR>
                        <TD>    <P align=center><%= e.getMessage() %></P></TD>
                    </TR>
                </TBODY>
            </TABLE>
        </BODY>
    </HTML>
<%  
}
finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
