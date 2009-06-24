<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="netgest.bo.system.*,netgest.bo.runtime.*,java.sql.*"%> 
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>untitled</title>
  </head>
  <body>
  <%
    netgest.bo.system.boApplication boapp = netgest.bo.system.boApplication.getApplicationFromStaticContext("XEO");
    boSession sess = boapp.boLogin( "SYSUSER", "ABC" );
    EboContext ctx = sess.createRequestContext( null,null,null );
    Connection cn = ctx.getConnectionData();
    PreparedStatement pstm = cn.prepareStatement("SELECT UI$, CLSID FROM EBO_REGISTRY");
    ResultSet rslt = pstm.executeQuery();
    while( rslt.next() )
    {
        netgest.bo.runtime.cacheBouis.putBoui( rslt.getLong(1), rslt.getString(2) );
    }
    rslt.close();
    pstm.close();
    ctx.close();
    sess.closeSession();
  
  %>
    <b>Bouis in cache <%=netgest.bo.runtime.cacheBouis.cacheBouisSize()%> </b>
  </body>
</html>
