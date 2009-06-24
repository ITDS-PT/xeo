<%@ page contentType="text/html;charset=windows-1252"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
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
    
    boObject xobj=DOC.getObject( ClassUtils.convertToLong(request.getParameter("exportBoui")) );
    String mode = request.getParameter("mode");

    StringTokenizer st = new StringTokenizer(request.getParameter("attrPrint"),";");

    String attArray[] = new String[st.countTokens()];
    for(int i=0; st.hasMoreTokens(); i++)
        attArray[i] = st.nextToken();
    
    netgest.bo.utils.exportObject export = new netgest.bo.utils.exportObject(xobj,attArray, (request.getParameter("allAtt").equals("YES")));
      
    StringBuffer sb = null;
    XMLDocument xmldoc = null;
    if("XML".equals(mode))
       xmldoc = export.saveXML(boObject.getBoManager().loadObject(boctx,bosession.getPerformerBoui()).getAttribute("username").getValueString());
    else if ("CSVC".equals(mode))
       sb = export.saveCSV(true,",");
    else
       sb = export.saveCSV(true,";");


        try
        {  
            if("XML".equals(mode))
            {
              response.setContentType("application/xml");
              response.setHeader("Content-Disposition","attachment; filename=\"" + xobj.getBoDefinition().getLabel() + ".xml\"");
            }
            else
            {
              response.setContentType("application/csv");
              response.setHeader("Content-Disposition","attachment; filename=\"" + xobj.getBoDefinition().getLabel() + ".csv\"");
            }

            OutputStream outres = response.getOutputStream();

            
            if("XML".equals(mode)) {
              //XMLOutputStream xmlout =  new oracle.xml.parser.v2.XMLOutputStream(outres);
              //xmlout.flush();
              xmldoc.print(outres,"ISO-8859-1");
            }
            else {
              OutputStreamWriter fw = new OutputStreamWriter(outres,"ISO-8859-1");
              fw.write(sb.toString());
              fw.close();
            }
            outres.close();

            response.flushBuffer();
 
        }
        catch (Exception e){} 
            

    
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

