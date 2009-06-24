<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<%@ page import="oracle.xml.parser.v2.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
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
    
    Explorer e=ExplorerServer.getExplorer(request.getParameter("viewer"));
    boDefHandler xbodef = e.p_bodef;

/*
    boObject xobj=DOC.getObject( ClassUtils.convertToLong(request.getParameter("exportBoui")) );

    StringTokenizer st = new StringTokenizer(request.getParameter("attrPrint"),";");

    String attArray[] = new String[st.countTokens()];
    for(int i=0; st.hasMoreTokens(); i++)
        attArray[i] = st.nextToken();
*/        

    String mode = request.getParameter("mode");
    netgest.bo.utils.ExplorerList export = new netgest.bo.utils.ExplorerList(boctx, e);
    
    StringBuffer sb = null;
    XMLDocument xmldoc = null;
    boolean headers=true;
    if((request.getParameter("headers")!=null) && (request.getParameter("headers").equals("false")))
      headers=false;
    XSLStylesheet xslDoc = null;
    
    if ("CSV_C_11".equals(mode))
       sb = export.saveCSV_1Line_1Column(",", headers);
    else if ("CSV_SC_11".equals(mode))
       sb = export.saveCSV_1Line_1Column(";", headers);
    else if ("CSV_C_1N".equals(mode))
       sb = export.saveCSV_1Line_NColumns(",", headers);
    else if ("CSV_SC_1N".equals(mode))
       sb = export.saveCSV_1Line_NColumns(";", headers);
    else if (mode.startsWith("CSV_C_"))
       sb = export.saveCSV_NLine_1Column(",", headers);
    else if (mode.startsWith("CSV_SC_"))
       sb = export.saveCSV_NLine_1Column(";", headers);
    else
    {
       xmldoc = export.saveXML(boObject.getBoManager().loadObject(boctx,bosession.getPerformerBoui()).getAttribute("username").getValueString());
       String xsl = request.getParameter("XSL");
       if(xsl!=null && !xsl.equals(""))
       {
          boObject style = boObject.getBoManager().loadObject(boctx, Long.parseLong(xsl));
          InputStream xslIn = style.getAttribute("document").getObject().getAttribute("file").getValueiFile().getInputStream();
          xslDoc = new XSLStylesheet(xslIn, new java.net.URL("http://www.w3.org/1999/XSL/Transform"));
          xslDoc.getOutputEncoding();
        }
    }


        try
        {  
            OutputStream outres = response.getOutputStream();

            if(mode.indexOf("CSV")>-1)
            {
              response.setContentType("application/csv");
              response.setHeader("Content-Disposition","attachment; filename=\"Listagem de " + xbodef.getLabel() + ".csv\"");
              OutputStreamWriter fw = new OutputStreamWriter(outres,"ISO-8859-1");
              fw.write(sb.toString());
              fw.close();
            }
            else if(mode.indexOf("XML")>-1 && xslDoc==null)
            {
              response.setContentType("application/xml");
              response.setHeader("Content-Disposition","attachment; filename=\"Listagem de " + xbodef.getLabel() + ".xml\"");
              xmldoc.print(outres,"UTF-8");
            }
            else
            {
              String fileType = xslDoc.getOutputMediaType();
              response.setContentType(fileType);
              response.setHeader("Content-Disposition","attachment; filename=\"Listagem de " + xbodef.getLabel()+"\"");
              PrintStream xslOut = new PrintStream(outres, true, "UTF-8");
              (new XSLProcessor()).processXSL(xslDoc, xmldoc, xslOut);
              xslOut.close();
            }
            
            outres.close();

            response.flushBuffer();
 
        }
        catch (Exception _e){}           

    
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>