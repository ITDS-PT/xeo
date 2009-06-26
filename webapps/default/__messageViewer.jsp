<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>

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
if(bosession== null)
{

    StringBuffer url = new StringBuffer("?");
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String pname = oEnum.nextElement().toString();
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ,"UTF-8") );
    }
    response.sendRedirect("login.jsp?returnToPage=__messagePost.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
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

Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML doc = DOCLIST.getDOC(IDX);
boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);
long bouiProgram= ((XwfController)doc.getController()).getRuntimeProgramBoui();
if(bouiProgram > 0)
{
    PrintWriter _out = new PrintWriter(out);
    PageController controller = new PageController();
    String explorerKey = request.getParameter("explorerKey");
    MessageViewer mv = new MessageViewer("messageViewer", bouiProgram);            
    mv.writeHTML(_out, doc, DOCLIST,controller);
}
else
{
    %>
        <html></html>
    <%
}
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
