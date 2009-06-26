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
    if(request.getParameter("objName")==null) {%>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Builder</title>
            </head>
            <body>
                <h4>Refactoring de Objectos</h4>
                <form>
                    <table>
                    <tr>
                        <td>
                            <p>Nome do Objecto:</p>
                        </td>
                        <td>
                            <input type="text" name="objName" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Novo nome:</p>
                        </td>
                        <td>
                            <input type="text" name="newObjName" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p><input type="submit" value="Refactoring" ></p>
                        </td>
                    </tr>
                    </table>
                    
                </form>
            </body>
        </html>
    <%}
    %>

    
    <%
    if(request.getParameter("objName")!=null && request.getParameter("newObjName")!=null) {
    	String objName = request.getParameter("objName");
    	String newObjName = request.getParameter("newObjName");
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
            javax.transaction.UserTransaction ut=null;
            ok = false;
            try 
            {
                System.out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
                System.out.print("<title>Refactoring</title></head><body>");                
                System.out.print("<tr>");
                System.out.print("<td>");
                System.out.print("<h4>Refactoring de Objectos</h4>");
                System.out.print("<form>");
                
                System.out.print("<table>");
                    System.out.print("<tr>");
                       System.out.print(" <td>");
                            System.out.print("<p>Nome do Objecto:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"objName\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Novo nome:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"newObjName\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p><input type=\"submit\" value=\"Refactoring\" ></p>");
                        System.out.print("</td>");
                   System.out.print(" </tr>");
                    System.out.print("</table>");
                System.out.print("</form>");
                System.out.print("</td>");
                System.out.print("</tr>");
                System.out.print("<tr>");
                System.out.print("<td>");
                System.out.print("<p><textarea rows=\"20\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
                
                final InitialContext ic = new InitialContext();
                ut = (javax.transaction.UserTransaction) ic.lookup("java:comp/UserTransaction");
                ut.begin();
                boctx.close();
                boRefactoring boRefact = new boRefactoring(boctx); 
                int ret = boRefact.objectRefactoring(objName, newObjName);                
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
                    System.out.print("Objecto está actualizado");
                }
                else if(ret == -4)
                {
                    System.out.print("Não tem permissão de escrita");
                }
                ok = true;
                System.out.print("</textarea></p></td></tr></table></body></html>");
            }
            catch (Exception e)
            {
            	System.out.print(e.getMessage());
                System.out.print("</textarea></p></td></tr></body></html>");
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

