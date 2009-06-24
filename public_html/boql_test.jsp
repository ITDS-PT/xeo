<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>


<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null)
{

    StringBuffer url = new StringBuffer("?");
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String pname = oEnum.nextElement().toString();
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ) ) );
    }
    response.sendRedirect("login.jsp?returnToPage=boql_test.jsp"+ java.net.URLEncoder.encode(url.toString() ));
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
%>
<html>
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=windows-1252"/>
    
    <% int sel = -1;
       Vector queries=new Vector();
       netgest.bo.ql.QLParser x=null;
    %>
       
  </head>
  <body>

    <% String result=null;
        Enumeration oEnum = request.getParameterNames();
        String olds = request.getParameter("olds");
        if (olds != null)
        {
            StringTokenizer st_olds = new StringTokenizer(olds, ";");
            String oldquer = null;
            while(st_olds.hasMoreTokens())
            {
                oldquer = st_olds.nextToken();
                String newboql = request.getParameter("boql");
                if((newboql==null) || (newboql!=null && !oldquer.equalsIgnoreCase(newboql)))
                    queries.add(oldquer);
            }
        }
    if(request.getParameter("boql")!=null) {
        
        
        
        queries.add(request.getParameter("boql"));
        
/*        boolean initPage=true;
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
    
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            bosession = boSession.boLogin("SYSUSER","ABC");
            request.getSession().setAttribute("boSession",bosession);
        }
        if(boctx==null) {
            boctx = bosession.createRequestContext(request,response,pageContext);
            //long xxx=boctx.getBoSession()
            request.setAttribute("a_EboContext",boctx);
        }
*/
        x=new netgest.bo.ql.QLParser();
        try{
          if(request.getParameter("sec")!=null &&  request.getParameter("sec").equalsIgnoreCase("OFF"))
              result=x.toSql(request.getParameter("boql"),boctx, false);
          else
              result=x.toSql(request.getParameter("boql"),boctx);
        }catch(netgest.bo.ql.boqlParserException boqlE)
        {  
          result = boqlE.getMessage();
        }
   }


            

%>

    
    
    <form action="boql_test.jsp" method="GET" >
        <p> <b>Old Queries:</b> 

     <select name="latest">
            <option value="0" SELECTED>-----Latest Queries------</option>
        <%
            for (int i = queries.size()-1; i >= 0; i--){
        %>
                <option value="<% out.print(i); %>">  <% out.print(queries.get(i)); %>
        <% } %>
        
      </select>
      
      <textarea  style="display:none" name="olds"  cols="1" rows="1"><%
            for (int i = 0; i < queries.size(); i++)
                out.print(queries.get(i)+";");
        %></textarea>
        
        <input  name="Copy"  type="SUBMIT"  value="Copy Query" />
    </form>
      <form action="boql_test.jsp" method="Get" >
      <table width="100%">
      <tr>
      <td><b>Query to Execute:</b></td> 
      
      <td>
      Security:
      
            <% if(request.getParameter("sec")==null || request.getParameter("sec").equalsIgnoreCase("ON")) {%> 
                <input type="RADIO" name=sec value="ON" checked="checked"> ON
                <input type="RADIO" name=sec value="OFF"> OFF
            <%} else {%>
                <input type="RADIO" name=sec value="ON" > ON
                <input type="RADIO" name=sec value="OFF" checked="checked"> OFF
            <% } %>
        </td>
        <td>
        SQL Result:
            <% if(request.getParameter("sql")==null || request.getParameter("sql").equalsIgnoreCase("OFF")) {%> 
                
                <input type="RADIO" name=sql value="ON" > ON
                <input type="RADIO" name=sql value="OFF" checked="checked"> OFF
                
            <%} else {%>
                <input type="RADIO" name=sql value="ON" checked="checked"> ON
                <input type="RADIO" name=sql value="OFF"> OFF
            <% } %>
        </td>
        </tr>
        </table>      

      <textarea name="boql" style='width:100%' rows="5" ><% if(request.getParameter("boql")!=null) 
                                                        out.print(request.getParameter("boql")); 
                                                   else {
                                                        String op = request.getParameter("latest");
                                                        if(op!=null) 
                                                        {
                                                            int sel_op = new Integer(op).intValue();    
                                                            out.print(queries.get(sel_op));
                                                        }
                                                    } %></textarea>
     
      <textarea style="display:none"  name="olds"  cols="1" rows="1"><%
            for (int i = 0; i < queries.size(); i++)
                out.print(queries.get(i)+";");
        %></textarea>
      <input type="submit" value="Run Query" />
    </form>

      <% if((result !=null && x!= null && !x.Sucess()) ) {
    %>
          <b>Error:      </b> 
          
          <%= result%>
      <% } else if((request.getParameter("sql")!= null && request.getParameter("sql").equalsIgnoreCase("ON") && x.Sucess() && x.Sucess())) {%>
                  <b>Result:      </b> 
                    <textarea disabled='1' style="color:black;width:100%;height:100px"><%=result%></textarea>
                  <% } %>
      <% if(x!=null && x.Sucess()) {
            String name = x.getObjectName().toLowerCase();
            String viewer = x.getViewer();
            String URLquery = java.net.URLEncoder.encode( request.getParameter("boql") );
            String script = "<script language='JavaScript'> "+
                            "function view(){ "+
                            "parent.announceArea.winmain().openDoc('small', '"+ name + "' , '"+viewer+"', 'method=list&boql="+
                            URLquery+"',null,null,null,null); "+
                            "} view();   </script>";
          %>
            <script LANGUAGE='javascript' SRC='bo_config.js'></script>
            <script LANGUAGE='javascript' SRC='bo_main.js'></script>
            <p>
              <%
                out.print(script);
            %>
            </p>
    <% } %>    
    
  </body>
</html>
<% 
} finally {
  if (boctx!=null)boctx.close();
  if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}%>