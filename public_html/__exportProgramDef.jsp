<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.impl.document.*"%>
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
    boObject BOI = DOC.getObject( ClassUtils.convertToLong(request.getParameter("exportBoui")) );
    
    String contentType = request.getContentType();
    String mode = request.getParameter("modeType");
    XMLDocument xmldoc = null;
    if("export".equals(mode))
    {
        ngtXMLHandler xmlHandler = new ngtXMLHandler(BOI.getAttribute("flow").getValueString());    
        xmldoc = xmlHandler.getDocument();
    
        try
        {  
            response.setContentType("application/xml");
            String filename = BOI.getAttribute("name").getValueString() + " v("+BOI.getAttribute("version").getValueString()+")";
            response.setHeader("Content-Disposition","attachment; filename=\"" + filename + ".xml\"");
            OutputStream outres = response.getOutputStream();            
            xmldoc.print(outres);
            outres.close();
            response.flushBuffer(); 
        }
        catch (Exception e)
        {
        }
    }
    else if(contentType != null && !"".equals(contentType))
    {    
        try 
        {            
            MultipartRequest mr=new MultipartRequest(request,DocumentHelper.getTempDir(),15*1024*1024);
            Enumeration oEnum=mr.getFileNames();
            iFile file = new FSiFile(null,mr.getFile("FILEBROWSE"),null);                                                 
            xmldoc = ngtXMLUtils.loadXML(file.getInputStream());    
            String xml = ngtXMLUtils.getXML(xmldoc);
            BOI.getAttribute("flow").setValueString(xml);        
            BOI.update();
%>
        <html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title></title>
    <%@ include file='boheaders.jsp'%>
    <body onload="onloadclose()" >
    <script language="javascript">
        function onloadclose() {
            window.setTimeout("winmain().ndl[getIDX()].close();",100);            
        }
    </script>    
    </body>
    </html>

<%
        return;   
        } 
        catch (Exception ex) 
        {
%>
Erro na Importação do ficheiro. Mensagem de erro :  <%=ex.getMessage()%>
<%
        } 
        
    }
    else if("import".equals(mode))
    {    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="multipart/form-data; charset=UTF-8">
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%><script>
var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif">Importar Programa ';
var objDescription='Escolha um ficheiro que esteja no seu computador, de modo a enviá-lo para o server';
</script>
<body onload="document.getElementById('FILEBROWSE').focus();">
<FORM id='boFormSubmit' method="POST" enctype="multipart/form-data">
   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
<%
  docHTML_section sec = DOC.createSection("lookupEbo_Document","",false,5);
  
  bodef=boDefHandler.getBoDefinition("Ebo_Document");
  docHTML_sectionRow row; 
  Hashtable xattributes=new Hashtable();
  row=sec.addRow();   
  row.addCell(docHTML_sectionField.newFileBrowse(
                new StringBuffer("FILEBROWSE"),
                new StringBuffer("FILEBROWSE"),
                new StringBuffer("Selecione o ficheiro "),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer(""),
                new StringBuffer("")) 
                ,xattributes);
                
    sec.render(pageContext,DOC,DOCLIST);       
    
    
    
    sec = DOC.createSection("__lookupEbo_Document","",true,5);
    sec.setTitle("&nbsp;");
    row=sec.addRow();
    
    docHTML_sectionCell cell = row.addCellMultiField();
    
    xattributes.put("attr_td"," align='center' ");
    cell.addField( docHTML_sectionField.newButton(
                new StringBuffer("confirm"),
                new StringBuffer("Importar"),
                new StringBuffer("filesubmit()"))
                ,"",xattributes
                ) ;

    cell.addField( docHTML_sectionField.newButton(
                new StringBuffer("cancel"),
                new StringBuffer("Cancelar"),
                new StringBuffer("winmain().ndl[getIDX()].close();"))
                ,"",xattributes
                ) ;
  sec.render(pageContext,DOC,DOCLIST);
    
%>  
        </td>
     </tr>
</table>
<script>
    function filesubmit() {        
        if(boFormSubmit.FILEBROWSE.value.length > 0) {       
            boFormSubmit.submit();
        } else {
            alert("Tem de selecionar um ficheiro.");
        }
    }        
</script>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />    
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    
    <INPUT type='hidden' name='modeType'  value='<%=request.getParameter("modeType")%>' />
    <INPUT type='hidden' name='exportBoui' value='<%=request.getParameter("exportBoui")%>' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM>
</body>
</html>
<%
    }
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

