<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Relatório da Base de Dados</title>
  </head>
  <body>  
    <% String result=null;
        String file = request.getParameter("filePath");
    if(file!=null && file.length() > 0) {
          boolean initPage=true;
            EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        
            boSession bosession = (boSession)request.getSession().getAttribute("boSession");
			boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
			
            if(bosession== null) {
                bosession = boapp.boLogin("SYSTEM", boLoginBean.getSystemKey());
                request.getSession().setAttribute("boSession",bosession);
            }
            if(boctx==null) {
                boctx = bosession.createRequestContext(request,response,pageContext);
                request.setAttribute("a_EboContext",boctx);
            }

        netgest.bo.report.BDReport x=new netgest.bo.report.BDReport(boctx, request.getParameter("package"), request.getParameter("filePath"));
        result = x.createHtmlReport();
   }
   else
   {
   %>
        <p><font color="#FF0000">Preencha o caminho para o ficheiro</font></p>
    <%
   }
            

%>
    <h4>Introduza o nome do package&nbsp;que pretende criar o relat&oacute;rio ou deixe em branco para gerar um relat&oacute;rio global.</h4>
    <form>
        <p>
            <input type="text" name="package" size="50">
        </p>
        
        <h4>Introduza o caminho do ficheiro para a geração do relatório.</h4>
        <p>
            <input type="text" name="filePath" size="50">
        </p>
        <p>
            <input type="submit" value="Gerar Mapa">
        </p>
    </form>    
      <% if(result !=null) {%>
      <%= result%>
    <% }%>
  </body>
</html>
