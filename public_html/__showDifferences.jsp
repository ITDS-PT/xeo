<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.data.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%


EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp");
    return;
}
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }
    EboContext boctx2 = bosession.createRequestContext(request,response,pageContext);
    int IDX;
    int cvui;
    boDefHandler bodef;    
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boObject boChanged = DOC.getObject( ClassUtils.convertToLong(request.getParameter("Boui")) );
    boObject objDataBase= null;
    if ( boChanged.exists() ) objDataBase= boObject.getBoManager().loadObject(boctx2,boChanged.getBoui());
    else
    {
      objDataBase = boChanged;
      }
      
    DifferenceContainer listDif  = DifferenceHelper.showDifferences(objDataBase,boChanged);
    PrintWriter fw = new PrintWriter(out);
    netgest.bo.report.ShowDiff.createObjectCardReport( listDif,DOC, objDataBase,boChanged ,"General","edit",fw,boctx2 );
    
%>


<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
