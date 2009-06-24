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
         if(request.getParameter("table")==null) {%>
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
                            <p>Tabela:</p>
                        </td>
                        <td>
                            <input type="text" name="table" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Chave:</p>
                        </td>
                        <td>
                            <input type="text" name="keyTable" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Campo a Transformar :</p>
                        </td>
                        <td>
                            <input type="text" name="transformTable" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Tabela Temporária:</p>
                        </td>
                        <td>
                            <input type="text" name="temp" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Chave:</p>
                        </td>
                        <td>
                            <input type="text" name="keyTemp" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Campo a Transformar :</p>
                        </td>
                        <td>
                            <input type="text" name="transformTemp" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p>Classe Transformadora:</p>
                        </td>
                        <td>
                            <input type="text" name="transformClass" size="50">
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <p><input type="submit" value="Transformar" ></p>
                        </td>
                    </tr>
                    </table>
                    
                </form>
            </body>
        </html>
         <%}
    %>

    
    <%
    if(request.getParameter("table")!=null && !"".equals(request.getParameter("table")) && 
        request.getParameter("temp")!=null && !"".equals(request.getParameter("temp")) &&
        request.getParameter("keyTable")!=null &&  !"".equals(request.getParameter("keyTable")) &&
        request.getParameter("transformTable")!=null && !"".equals(request.getParameter("transformTable")) &&
        request.getParameter("transformClass")!=null && !"".equals(request.getParameter("transformClass"))
    )
    {
    
        String table = request.getParameter("table");
        String keyTable = request.getParameter("keyTable");
        String transformTable = request.getParameter("transformTable");
        String temp = request.getParameter("temp");
        String keyTemp = request.getParameter("keyTemp");
        String transformTemp = request.getParameter("transformTemp");
        String transformClass = request.getParameter("transformClass");
        
        if(keyTemp == null || "".equals(keyTemp))
        {
            keyTemp = keyTable;
        }
        
        if(transformTemp == null || "".equals(transformTemp))
        {
            transformTemp = transformTable;
        }
        
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
                System.out.print("<title>Transform</title></head><body>");
                System.out.print("<h4>Transform/h4>");
                System.out.print("<form>");
                System.out.print("<table>");
                System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Tabela:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"table\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Chave:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"keyTable\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Campo a Transformar :</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"transformTable\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Tabela Temporária:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"temp\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Chave:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"keyTemp\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Campo a Transformar :</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"transformTemp\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<tr>");
                        System.out.print("<td>");
                            System.out.print("<p>Classe Transformadora:</p>");
                        System.out.print("</td>");
                        System.out.print("<td>");
                            System.out.print("<input type=\"text\" name=\"transformClass\" size=\"50\">");
                        System.out.print("</td>");
                    System.out.print("</tr>");
                    System.out.print("<td>");
                        System.out.print("<input type=\"submit\" value=\"Transformar\" >");
                    System.out.print("</td>");
                System.out.print("</tr>");
            System.out.print("</table>");
            System.out.print("</form>");            
                System.out.print("<tr>");
                System.out.print("<td>");
                System.out.print("<p><textarea rows=\"20\" name=\"S1\" readonly=\"readonly\" cols=\"80\">");
                netgest.bo.builder.boRefactoring boRefact = new netgest.bo.builder.boRefactoring(boctx);
                boRefact.transform(boctx, table, temp, keyTable, keyTemp, transformTable, transformTemp, transformClass);   
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

