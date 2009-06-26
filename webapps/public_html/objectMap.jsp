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
    <title>Constructor de Mapas de Objectos</title>
  </head>
  <body>  
    <% String result=null;
    if(request.getParameter("object")!=null) {
          boolean initPage=true;
          final boolean AUTO_LOGIN = false;
          boolean ok=false;
            EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        
            boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
            boSession bosession = (boSession)request.getSession().getAttribute("boSession");
            if(bosession== null) {
                bosession = boapp.boLogin("SYSTEM", boLoginBean.getSystemKey() );
                request.getSession().setAttribute("boSession",bosession);
            }
          

        netgest.bo.mapConstructor.MapConstructor x=new netgest.bo.mapConstructor.MapConstructor();
        result = x.contruct(request.getParameter("object"),boctx);
   }

            

%>
    <h4>Introduza o nome do objecto ou deixe em branco para um Mapa de todos os objectos</h4>
    <form>
        <p>
            <input type="text" name="object" size="50">
            <input type="submit" value="Gerar Mapa">
        </p>
    </form>    
    <p>
      <% if(result !=null) {%>
      
      <center><h4>Mapa</h4></center>
    </p>
    <div>
      <%= result%>
    </div>    
    <% }%>
  </body>
</html>
