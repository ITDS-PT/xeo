<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="oracle.xml.parser.v2.*"%>
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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);

    Explorer explorer = ExplorerServer.getExplorer(request.getParameter("viewer"));    
    ExplorerList explorerList = new ExplorerList(DOC.getEboContext(), explorer);    
    ExplorerPrintResultSet rslt = explorerList.getPrintResultSet();
    File file = rslt.getFileResults();    
    
    int rb=0;
    FileInputStream input = new FileInputStream(file); 

    try 
    {   
        
        byte[] buffer = new byte[4*1024];
        while ((rb = input.read(buffer)) > 0) 
        { 
            response.getOutputStream().write(buffer,0,rb); 
        } 
        input.close();
    } 
    catch (Exception ex) 
    {
    }
    finally
    {
        if(input != null)input.close();
        file.delete();
    }             
%>
    
<%  
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>