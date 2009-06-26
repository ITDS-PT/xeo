<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%@ page import="javax.naming.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.builder.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

    <% 
    if(request.getParameter("object")==null) {%>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Builder</title>
            </head>
            <body>
                <h4>Introduza o nome do objecto que pretende refazer o XML</h4>
                <form>
                    <p>
                        <input type="text" name="object" size="50">            
                        <input type="submit" value="     Build     " >
                    </p>
                </form>
            </body>
        </html>
    <%}
    %>

    
    <%
    if(request.getParameter("object")!=null) {
    	String s = request.getParameter("object");
        boolean initPage=true;
        final boolean AUTO_LOGIN = false;
        boolean ok=false;
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
        
        if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
        {
            ok=true;            
        }
        if(AUTO_LOGIN) {
            boSession bosession = boapp.boLogin("SYSUSER","ABC");
                
            request.getSession().setAttribute("boSession",bosession);
            String refer = request.getParameter("returnPage");
            if(refer==null) refer = "";
            response.sendRedirect(refer);
            return;
        }
        String errormessage="";
        boSession bosession = null;
        if ( !ok )
        {
            if(request.getParameter("USERNAME")!=null && request.getParameter("PASSWORD") != null) 
            {
                try {
                    //boSession bosession = boSession.boLogin(request.getParameter("USERNAME"),request.getParameter("PASSWORD"));
                    bosession = boapp.boLogin(request.getParameter("USERNAME"),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request);
                    request.getSession().setAttribute("boSession",bosession);
                    ok=true;
                } catch (netgest.bo.system.boLoginException e) {
                    if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                        errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    } else {
                        errormessage = "Nome de utilizador ou palavra passe inválida.";
                    }
                } catch (Exception e) {
                    errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    throw(e);
                }
            }
            else if ( request.getRemoteUser() != null && request.getRemoteUser().length() > 0)
            {
                try {
                    bosession = boapp.boLogin(request.getRemoteUser(),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request );
                    request.getSession().setAttribute("boSession",bosession);
                    ok=true;
                } catch (netgest.bo.system.boLoginException e) {
                    if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                        errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    } else {
                        errormessage = "Nome de utilizador ou palavra passe inválida.";
                    }
                } catch (Exception e) {
                    errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    throw(e);
                }
            }
        }
        if(boctx==null)
        {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.setAttribute("a_EboContext",boctx);
        }
        PrintStream xout=System.out;
        PrintStream xerr=System.err;
        try
        { 
            PrintStream _out = new PrintStream(response.getOutputStream(),true);
            System.setOut(_out);
            System.setErr(_out);
            ok = false;
            try 
            {
                System.out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
                System.out.print("<title>Builder</title></head><body>");
                System.out.print("<table height=\"100%\" width=\"100%\">");
                System.out.print("<tr height=\"1%\">");
                System.out.print("<td width=\"100%\">");
                System.out.print("<h4>Introduza o nome do objecto que pretende refazer o XML</h4>");
                System.out.print("<form>");
                System.out.print("<p>");
                System.out.print("<input type=\"text\" name=\"object\" size=\"50\">");            
                System.out.print("<input type=\"submit\" value=\"     Build     \">");
                System.out.print("</p>");
                System.out.print("</form>");
                System.out.print("</td>");
                System.out.print("</tr>");
                System.out.print("<tr height=\"99%\">");
                System.out.print("<td width=\"100%\">");
                System.out.print("<p><textarea rows=\"20\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
                boRefactoring boRefact = new boRefactoring(boctx);
                boRefact.toNewXMLVersion(s);
                System.out.print("</textarea></p></td></tr></table></body></html>");
            } 
            catch (Exception e)
            {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
            finally 
            {
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(boctx != null) boctx.close();
            System.setOut(xout);
            System.setErr(xerr);
        }
   }
%>    

