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
                <h4>Verificador de Integridade dos Objectos</h4>
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
                            <p>Lista Objectos p/ clonar (Ebo_DAO; EBO_Template):</p>
                        </td>
                        <td>
                            <input type="text" name="listClone" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Lista de Boui (123; 124):</p>
                        </td>
                        <td>
                            <input type="text" name="listBoui" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td align="right">
                            <input type=CHECKBOX name="onlyTest">
                        </td>
                        <td>
                            <p>Modo Teste</p>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p><input type="submit" value="Verificar" ></p>
                        </td>
                    </tr>
                    </table>
                    
                </form>
            </body>
        </html>
         <%}
    %>

    
    <%
    if(request.getParameter("objName")!=null || request.getParameter("listBoui")!=null || 
        request.getParameter("listClone")!=null) {
        String objName = request.getParameter("objName");
        String listBoui = request.getParameter("listBoui");
        String listClone = request.getParameter("listClone");
    	boolean onlyTest = "on".equalsIgnoreCase(request.getParameter("onlyTest"));
        
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            bosession = boapp.boLogin("SYSTEM", boLoginBean.getSystemKey() );
            request.getSession().setAttribute("boSession",bosession);
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
            try 
            {
                System.out.print("<html><head><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\"/>");
                System.out.print("<title>Verificador de Integridade dos Objectos</title></head><body>");
                System.out.print("<h4>Verificador de Integridade dos Objectos</h4>");
                System.out.print("<form>");
                System.out.print("<table>");
                System.out.print("<tr>");
                    System.out.print("<td>");
                        System.out.print("<p>Nome do Objecto:</p>");
                    System.out.print("</td>");
                    System.out.print("<td>");
                    System.out.print("<input type=\"text\" name=\"objName\" size=\"50\">");
                    System.out.print("</td>");
                System.out.print("</tr>");
                System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Lista Objectos p/ clonar (Ebo_DAO; EBO_Template):</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"listClone\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Lista de Boui (123; 124):</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"listBoui\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                System.out.print("<tr>");
                    System.out.print("<td align=\"right\">");
                        System.out.print("<input type=CHECKBOX name=\"onlyTest\">");
                    System.out.print("</td>");
                    System.out.print("<td>");
                        System.out.print("<p>Modo Teste</p>");
                    System.out.print("</td>");
                System.out.print("</tr>");
                System.out.print("<tr>");
                    System.out.print("<td>");
                        System.out.print("<input type=\"submit\" value=\"Verificar\" >");
                    System.out.print("</td>");
                System.out.print("</tr>");
            System.out.print("</table>");
            System.out.print("</form>");            
                System.out.print("<tr>");
                System.out.print("<td>");
                if(onlyTest)
                    System.out.print("<p>Resultados para objectos do tipo: " + objName + " em modo teste </p>");
                else
                    System.out.print("<p>Resultados para objectos do tipo: " + objName + "</p>");
                System.out.print("<p><textarea rows=\"20\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
                new netgest.bo.builder.BDIntegrity(boctx, objName, listClone, listBoui, onlyTest).verifyDuplicatedNoOrphans();
                System.out.print("</textarea></p></td></tr></table></body></html>");
            }
            catch (Exception e)
            {
            	System.out.print(e.getMessage());
                System.out.print("</textarea></p></td></tr></body></html>");
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

