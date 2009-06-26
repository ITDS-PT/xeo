<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.utils.lock.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="java.sql.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
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
        response.sendRedirect("login.jsp?returnToPage=___sendToIndex.jsp"+ java.net.URLEncoder.encode(url.toString() ));
        return;
    }
    if(request.getParameter("execute")==null) {%>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Builder</title>
            </head>
            <body>
                <h4>Introduza os bouis dos objectos para index separados por ponto e virgula(;)</h4>
                <form>
                    <p>
                        <input type="text" name="bouiIndex" size="50">
                        <input type="hidden" name="operation" value="rebuildTextIndex">
                        <INPUT type='hidden' value='1' name='execute'>
                        <input type="submit" value="     Re-Index     " >
                    </p>
                </form>
            </body>
        </html>
    <%
    }
    else
    {
        response.setDateHeader ("Expires", -1);
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        PrintStream xout=System.out;
        PrintStream xerr=System.err;
        boolean lock = false;
        try 
        {
            
            PrintStream _out = new PrintStream(response.getOutputStream(),true);
            System.setOut(_out);
            System.setErr(_out);            
            System.out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
            System.out.print("<title>Re-Index</title></head><body>");
            System.out.print("<table height=\"100%\" width=\"100%\">");
            System.out.print("<tr height=\"100%\">");
            System.out.print("<td width=\"100%\">");
            System.out.print("<p><textarea rows=\"50\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
            if(boctx==null) 
            {
                boctx = bosession.createRequestContext(request,response,pageContext);
                request.setAttribute("a_EboContext",boctx);
            }
            
            String bouisToIndex = request.getParameter("bouiIndex");
            String operation = request.getParameter("operation");
            boDefHandler bodef = null;
            if(bouisToIndex != null && !"".equals(bouisToIndex) && "rebuildTextIndex".equals(operation))
            {
                String [] bouisList =  bouisToIndex.split(";");
                long aux;
                for (int i = 0; i < bouisList.length; i++) 
                {
                    aux = Long.parseLong(bouisList[i]);
                    if(aux > 0)
                    {
                        
                        netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic.queue.addToQueue(boctx, aux );
                        System.out.print("Adicionei o boui: " + aux + "\n");
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            System.out.println(e.getLocalizedMessage());
        }
        finally 
        {
            System.out.print("</textarea></p></td></tr></table></body></html>");
            if (boctx!=null)boctx.close();
            if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
            System.setOut(xout);
            System.setErr(xerr);
        }
    }
%>

