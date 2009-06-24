<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
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
if(bosession== null)
{

    StringBuffer url = new StringBuffer("?");
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String pname = oEnum.nextElement().toString();
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ), "UTF-8" ) );
    }
    response.sendRedirect("login.jsp?returnToPage=__buildReport.jsp"+ java.net.URLEncoder.encode(url.toString(), "UTF-8" ));
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
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
boObject BOI;
if ( currObjectList == null ) BOI=null;
else BOI=currObjectList.getObject();
if( currObjectList != null ) currObjectList.first();

 

//response.setContentType("application/pdf");
//OutputStream out = response.getOutputStream();


//FileOutputStream os = new FileOutputStream("c:\\myreport.pdf");

//File xf= new File( "templateReports\\boform.xml");
//netgest.bo.report.buildReport.createPDF( xf , response.getOutputStream() );
OutputStreamWriter fw = new OutputStreamWriter(response.getOutputStream(),"UTF-8");
netgest.bo.report.buildReport.createObjectCardReport( DOC, BOI ,"General","edit",fw );
fw.close();


/*
os.close();
File xfile = new File("c:\\myreport.pdf");
response.setContentLength( (int)xfile.length() );
FileInputStream is = new FileInputStream(xfile);
byte buff[] = new byte[4096];
int br;
while( ( br=is.read( buff) ) > 0  )
{
    out.write( buff,0,br );
}
        
  */      

 

%>
<%
} finally {
if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}%>
