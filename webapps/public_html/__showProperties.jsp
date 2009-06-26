<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    boObject xobj=DOC.getObject( ClassUtils.convertToLong(request.getParameter("showBoui")) );
%>

<html>
    <head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Propriedades do Objecto</title>
    <%@ include file='boheaders.jsp'%>
    </head>
    <script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/properties16.gif">Propriedades doObjecto ';
</script>

<BODY style="background-color:#F3F6F7" >
<TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
  <TBODY>
     <TD>
         <TABLE style="TABLE-LAYOUT: fixed" height="100%" cellSpacing=0  cellPadding=6 width="100%">
        <COLGROUP>
        <COL width=160>
        <COL>
        <TBODY>
        <TR>
          <TD><b><%=xobj.getBoDefinition().getLabel()%></b>:</TD>
          <TD><%=xobj.getCARDID()%></TD></TR>
        <TR>
          <TD>Criado por:</TD>
          <TD><%
          long xboui=xobj.getAttribute("CREATOR").getValueLong();
          if ( xboui >0 ){
            boObject u1=DOC.getObject( xboui );
          %>
            <%=u1.getCARDIDwLink()%>
          <%}%>&nbsp;
          </TD></TR>
          
        <TR>
          <TD>Criado em :</TD>
          <TD><%
          java.util.Date xd= xobj.getDataRow().getDate("SYS_DTCREATE");
          String mydate=null;
          if( xd != null )
          {
              mydate = DateFormat.getDateTimeInstance( DateFormat.FULL,DateFormat.FULL ).format( xd );
              out.print( mydate );
          }
          %></TD></TR>
        <TR>
          <TD>Ultima alteração por :</TD>
          <TD><%
          if ( xobj.getDataRow().getString("SYS_USER") != null )
          {
             boObject u=null;
             try
             {
              u=netgest.bo.runtime.boObjectList.edit( DOC.getEboContext(),"select Ebo_Perf where boui="+xobj.getDataRow().getString("SYS_USER")+"").getObject();
             }
             catch( Exception e){}
              if ( u != null ){
              %>
                <%=u.getCARDIDwLink()%>
              <%
              } else{
              %>          
                 <%=xobj.getDataRow().getString("SYS_USER")%>
              <%
              }
          }
          else
          {
          %>
                 <%="< Não Guardado >"%>
          <%
          }
          %>
          </TD></TR>
        <TR>
          <TD>Nº Versão:</TD>
          <TD><span onclick="alert('Esta opção permite ver as várias versões do documento;\nAinda não está disponível')" class='lui'><%= xobj.getDataRow().getLong("SYS_ICN")%></span> </TD></TR>  
        <TR>
          <TD>Ultima modificação em :</TD>
          <TD><%
                   
          xd= xobj.getDataRow().getDate("SYS_DTSAVE");
          if( xd != null )
          {
              mydate = DateFormat.getDateTimeInstance( DateFormat.FULL,DateFormat.FULL ).format( xd );
              out.print( mydate );
          }
          else
          {
              out.print( "< Não Guardado >" );
          }
          %></TD></TR>
        <% if ( xobj.getAttribute("TEMPLATE")!=null && xobj.getAttribute("TEMPLATE").getValueLong() != 0 ){ %>
        <TR>
          <TD>Baseado no modelo :</TD>
          <TD><%=DOC.getObject( xobj.getAttribute("TEMPLATE").getValueLong() ).getCARDIDwLink()%> </TD></TR>
        
        <% } %>
       </TBODY></TABLE>
      
      </TD></TR>
  <TR>
    <TD style="background-color: #D7E3E6" align=right><BUTTON id=butBegin 
      onclick="winmain().ndl[getIDX()].close();";>OK</BUTTON></TD>
      
  
 </TR></TBODY></TABLE></BODY>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
