<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.bo.data.*"%>

<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=false;
try 
{
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }
%>

<html>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Dependencias de Um Objecto</title>
    <style type="text/css">
        @import url('xeo.css');
    </style>
    <style>
        TH
        {
            border-bottom:1px solid #6283A3;
            border-right:1px solid #6283A3;
        }
        TD
        {
            border-bottom:1px solid #6283A3;
            border-right:1px solid #6283A3;
        }
        TABLE
        {
            border-top:1px solid #6283A3;
            border-left:1px solid #6283A3;
        }
    </style>
    
  </head>
  <script src="xeo.js">
  </script>
  
  <body>
  <div class="areaSEL">
  <div  >
  <xeo:Session>
  <%
        byte REFERENCES=0;
        ArrayList arguments = new ArrayList(1);
        Long currBoui = new Long(request.getParameter("boui") );
        arguments.add( currBoui );
        boObject parentObj = boObject.getBoManager().loadObject( boctx, currBoui.longValue() ); 
        DataSet dataSet = null;
        if( request.getParameter( "references" ) != null )
        {
            dataSet = DataManager.executeNativeQuery( boctx, "DATA", "SELECT REFBOUI$,ATTRIBUTE FROM EBO_REFERENCES WHERE BOUI=? ORDER BY ATTRIBUTE", 1, 30, arguments );
            REFERENCES = 0;
        }
        else if( request.getParameter( "referencedby" ) != null )
        {
            dataSet = DataManager.executeNativeQuery( boctx, "DATA", "SELECT BOUI AS REFBOUI$,ATTRIBUTE FROM EBO_REFERENCES WHERE REFBOUI$=? ORDER BY ATTRIBUTE", 1, 30, arguments );
            REFERENCES = 1;
        }
        if ( dataSet != null )
        {
  
   %>
  <script>
    var objLabel = "<%=(parentObj.getCARDID().append(REFERENCES==1?"( Dependentes )":"( Dependencias )"))%>";
    var objStatus = "<%=(parentObj.getCARDID().append(REFERENCES==1?"( Dependentes )":"( Dependencias )"))%>";
  </script>
    <table width="100%"  cellpadding="5" cellspacing="0" >

        <tr >
            <th>
                Nome do Objecto
            </th>
            <th>
                Objecto Referenciado
            </th>
            <th>
                Atributo
            </th>
        </tr>
    <%
        for (int i = 1; i <= dataSet.getRowCount(); i++) 
        {
            long boui   = dataSet.rows( i ).getLong("REFBOUI$");
            
            String cardId  = null;
            String nameObj = null;
            String attribute = dataSet.rows( i ).getString( "ATTRIBUTE" );
            if( attribute != null && parentObj.getAttribute( attribute ) != null )
            {
                attribute = parentObj.getAttribute( attribute ).getDefAttribute().getLabel();
            }
            try 
            {
                boObject object = boObject.getBoManager().loadObject( boctx, boui );
                cardId  = object.getCARDIDwLink().toString();
                nameObj = object.getBoDefinition().getDescription();
            }
            catch( boRuntimeException e )
            {
                cardId = "ERRO!!!";
                nameObj = e.getMessage();
            }
            
        %>
        <tr>
            <td><%=nameObj%></td>
            <td><%=cardId%></td>
            <td><%=attribute%></td>
        </tr>
    <%
        }
    %>
    </table>
    <%
    }
    %>
  </xeo:Session>
  </div>
  </div>
  </body>
</html>
<%} finally 
{
    if( boctx != null ) boctx.close();
}
%>
