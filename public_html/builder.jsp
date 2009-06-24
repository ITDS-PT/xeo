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
                <h4>Introduza o nome do objecto que pretende efectuar o Build ou deixe em branco para um build total</h4>
                <form>
                    <p>
                        <input type="text" name="object" size="50">            
                        <input type="submit" value="     Build     " >
                    </p>
                    <p><input type="checkbox" value="ON" name="C1">Apagar a directoria bodef-deployment</p>
                    <p><input type="checkbox" value="ON" name="C2">For?ar deploy</p>
                </form>
            </body>
        </html>
    <%}
    %>

    
    <%
    if(request.getParameter("object")!=null) {
    	String s = request.getParameter("object");
        String s2 = request.getParameter("C1");
        String s3 = request.getParameter("C2");
        boolean initPage=true;
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
            
       final boolean AUTO_LOGIN = false;
        boolean ok=false;
        java.util.Enumeration oEnum = request.getHeaderNames();
       
        boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
        boSession bosession = null;
        if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
        {
            ok=true;
        }
        if(AUTO_LOGIN) {
            bosession = boapp.boLogin("SYSUSER","ABC");
            
            request.getSession().setAttribute("boSession",bosession);
            String refer = request.getParameter("returnPage");
            if(refer==null) refer = "";
            response.sendRedirect(refer);
            return;
        }
        String errormessage="";
        if ( !ok )
        {
            if(request.getParameter("USERNAME")!=null && request.getParameter("PASSWORD") != null) {
                try {
                    bosession = boapp.boLogin(request.getParameter("USERNAME"),request.getParameter("PASSWORD"), boapp.getDefaultRepositoryName() , request);
                    request.getSession().setAttribute("boSession",bosession);
                    ok=true;
                } catch (netgest.bo.system.boLoginException e) {
                    if(e.getErrorCode().equals(boLoginException.UNEXPECTED_ERROR)) {
                        errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    } else {
                        errormessage = "Nome de utilizador ou palavra passe inv?lida.";
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
                        errormessage = "Nome de utilizador ou palavra passe inv?lida.";
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
            javax.transaction.UserTransaction ut=null;
            ok = false;
            try 
            {
                System.out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
                System.out.print("<title>Builder</title></head><body>");
                System.out.print("<table height=\"100%\" width=\"100%\">");
                System.out.print("<tr height=\"1%\">");
                System.out.print("<td width=\"100%\">");
                System.out.print("<h4>Introduza o nome do objecto que pretende efectuar o Build ou deixe em branco para um build total</h4>");
                System.out.print("<form>");
                System.out.print("<p>");
                System.out.print("<input type=\"text\" name=\"object\" size=\"50\">");            
                System.out.print("<input type=\"submit\" value=\"     Build     \">");
                System.out.print("</p>");
                System.out.print("<p><input type=\"checkbox\" value=\"ON\" name=\"C1\">Apagar a directoria bodef-deployment</p>");
                System.out.print("<p><input type=\"checkbox\" value=\"ON\" name=\"C2\">For?ar deploy</p>");                
                System.out.print("</form>");
                System.out.print("</td>");
                System.out.print("</tr>");
                System.out.print("<tr height=\"99%\">");
                System.out.print("<td width=\"100%\">");
                System.out.print("<p><textarea rows=\"20\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
                
                final InitialContext ic = new InitialContext();
                ut = (javax.transaction.UserTransaction) ic.lookup("java:comp/UserTransaction");
                ut.begin();
                //boctx.close();                
                if("ON".equals(s2))
                {
                    boBuilder.cleanboDefDeployment();
                }
                if("ON".equals(s3))
                {
                    if("".equals(s) && !"ON".equals(s2))
                    {
                        boBuilder.cleanboDefDeployment();
                    }
                    else
                    {
                        boBuilder.forceDeploy(s);
                    }
                }
                if("".equals(s))
                {
                    boBuilder.buildAll(boctx);
                }
                else
                {
                    /*int ret = boBuilder.build(boctx, s);
                    if(ret == -1)
                    {
                        System.out.print("Objecto desconhecido");
                    }
                    else if(ret == -2)
                    {
                        System.out.print("Existe um build a decorrer");
                    }
                    else if(ret == -3)
                    {
                        System.out.print("Objecto est? actualizado");
                    } 
                    */
                }
                //boctx.close();
                ok = true;
                System.out.print("</textarea></p></td></tr></table></body></html>");
            } 
            catch (NamingException e)
            {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
            catch (Exception e)
            {
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
            finally 
            {
                if(ok) ut.commit();
                else ut.rollback();
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

