<%@ page contentType="text/html;charset=UTF-8"%>
<%@page import="
netgest.bo.runtime.*,
netgest.bo.system.*
"%>

<%!
//public static Interpreter i=new Interpreter();

%>

<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>untitled</title>
  </head>
  <body>
<%
    boApplication bapp = boApplication.getApplicationFromStaticContext("XEO");
    boSession bosess = bapp.boLogin( "SYSUSER", netgest.bo.system.boLoginBean.getSystemKey() );
    
    EboContext ctx = bosess.createRequestContext(request,response,pageContext);
    
    ctx.beginContainerTransaction();    
    
    boObject perf = boObject.getBoManager().loadObject( ctx, bosess.getPerformerBoui() );
    perf.getAttribute("name").setValueString( perf.getAttribute("name").getValueString() + "1"  );
    perf.update();
    perf.getAttribute("name").setValueString( perf.getAttribute("name").getValueString() + "2"  );
    perf.update();
    
    ctx.commitContainerTransaction();

%>
  </body>
</html>