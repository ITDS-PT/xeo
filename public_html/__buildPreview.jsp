<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
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
long bouiPreview= ClassUtils.convertToLong( request.getParameter("bouiToPreview"));
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
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ), "UTF-8") ); 
    }
    response.sendRedirect("login.jsp?returnToPage=__buildPreview.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
    return;
}
if(boctx==null && bouiPreview>0 ) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

String id = request.getParameter("docid");
if( id != null && bouiPreview>0)
{   
    docHTML xx = DOCLIST.getDocByIDX( Integer.parseInt( id ),boctx );
    if( xx == null )
    {
        String url = request.getRequestURI() + "?";
        
        Enumeration parEnum = request.getParameterNames();
        while( parEnum.hasMoreElements() )
        {
            String key = parEnum.nextElement().toString();
            if( !"docid".equals(key) )
            {
                url += "&" + key + "=" + java.net.URLEncoder.encode( request.getParameter( key ) );            
            }
        }
        response.sendRedirect(  url );
        return ;
        
    }
}

int IDX;int cvui;
boDefHandler bodef;
boDefAttribute atr;
String idbolist=null;
String[] ctrl=null;

Hashtable xattributes;



String frameId =  request.getParameter("frameID");
boolean designHeader = !"false".equals(request.getParameter("designHeader"));
boolean designPrint = !"false".equals(request.getParameter("designPrint"));
if(bouiPreview > 0)
{
    ctrl= DOCLIST.processRequest(boctx);
    idbolist=ctrl[1];
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    docHTML doc = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);

    PrintWriter _out = new PrintWriter(out);
    PageController controller = new PageController();
    String explorerKey = request.getParameter("explorerKey");
    ObjectCardReport oc = null;
    if(explorerKey != null)
    {
        Explorer e = ExplorerServer.getExplorer( explorerKey );         
        oc = e.getObjectCardReport();
        if(oc == null)
        {
            oc = new ObjectCardReport(bouiPreview);
            e.setObjectCardReport(oc);
        }
    }
    else
    {
        oc = new ObjectCardReport(bouiPreview);        
    }
    if(oc != null)
    {
        if(frameId != null && !"".equals(frameId))
        {
            oc.setParentFrameId(frameId);        
        }
        if(!designHeader)
        {
            oc.designHeader(false);
        }
        if(!designPrint)
        {
            oc.designPrint(false);
        }
        oc.setObject(bouiPreview);
        
        oc.writeHTML(_out, doc, DOCLIST,controller);
        doc.getObject( bouiPreview ).markAsRead();
    }
}
else
{
    %>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                    <title>Relatótio de Impressão/title>
                <style type="text/css">@import url(ieThemes/0/report/report.css);</style>
                <script>
                    function init(){}
                </script>
            </head>
            <body margin="0pt" scroll="auto" onload="init();">
            </body>
        </html>
        
    <%
}
//ServletOutputStream _out = response.getOutputStream();
//Explorer exp = ExplorerServer.getExplorer(explorer);
//Preview p = exp.getPreview();
//p.setObject(bouiPreview);
//PageController controller = new PageController();
//p.writeHTML(_out, doc, docList, controller);

} finally {
if( bouiPreview > 0 )
{
if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}
}%>
