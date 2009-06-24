<%@ page contentType="text/xml;charset=UTF-8"%>
<%@ page import="java.util.*,java.sql.*,oracle.xml.parser.v2.*,org.w3c.dom.*"%>
<%@ page import="java.io.PrintWriter"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%> 
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import=" netgest.bo.presentation.render.*"%>
<%@ page import=" netgest.bo.presentation.render.elements.Explorer,netgest.bo.presentation.render.elements.ExplorerServer"%>
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
String boql = request.getParameter("boql");
String label = request.getParameter("label");
String imagem = request.getParameter("imagem");
String query_boui = request.getParameter("query_boui");
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
    if(objName != null && form != null)
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
    explorer.setShowPreview( false );
}


String sql = explorer.getSql( boctx, null, true );
PreparedStatement pstm = null;
ResultSet         rslt = null;
int               allcnt  = 0;
try
{
    pstm  = boctx.getConnectionData().prepareStatement( "select count(*) from ("+sql+")" );
    for (int ip = 0; ip < explorer.p_parameters.size();
            ip++) {
        pstm.setObject(ip + 1,
            (Object) explorer.p_parameters.get(ip));
    }
    rslt  = pstm.executeQuery();
    if( rslt.next() )
    {
        allcnt = rslt.getInt(1);
    }
    else
    {
        allcnt = 0;
    }
}
finally
{
    if(rslt!=null) rslt.close();   
    if(pstm!=null) pstm.close();
}


XMLDocument xmldoc = new XMLDocument();
Element cntrs = (XMLElement)xmldoc.createElement("ExplorerCounters") ;
Element cntr = xmldoc.createElement("counter");
cntr.setAttribute("sid",request.getParameter("sid"));
cntr.setAttribute("all", String.valueOf( allcnt ) );
cntrs.appendChild( cntr );
xmldoc.appendChild( cntrs );
xmldoc.print( new java.io.PrintWriter( out ) );
} 
finally 
{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
