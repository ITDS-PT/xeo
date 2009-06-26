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
<%@ page import=" netgest.bo.runtime.job.BoJob"%>

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
    response.sendRedirect("login.jsp?returnPage=task_generalmywork.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

int IDX;int cvui;
String[] ctrl;
String idbolist;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML doc = DOCLIST.getDOC(IDX);
boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);
String className=request.getParameter( "job" );
if(!className.startsWith("netgest.bo"))
{
    className = "netgest.bo.runtime.job."+className;
}
BoJob boJob=null;
try
{
    boJob = (netgest.bo.runtime.job.BoJob)Class.forName(className).newInstance();
    boJob.run(boctx);
    response.sendRedirect( boJob.getNextUrl(boctx) + "&docid=" + IDX + "&myIDX=" + IDX  );
}
catch (InstantiationException _e){/*CLASS ERRO*/}
catch (IllegalAccessException _e){/*CLASS ERRO*/}
catch (Exception _e){/*CLASS ERRO*/}
%>
<%
}   
finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>