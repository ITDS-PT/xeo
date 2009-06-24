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
        response.sendRedirect("login.jsp?returnToPage=___setEmailPasswords.jsp");
        return;
    }
        response.setDateHeader ("Expires", -1);
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
        if(boctx==null) 
        {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.setAttribute("a_EboContext",boctx);
        }
        java.sql.Connection cn = null;
        try 
        {
            cn = boctx.getConnectionData();
            java.sql.PreparedStatement ps, ps2 ;
            java.sql.ResultSet rs ;
            String sql = "select boui, password from recipient where password is not null and username is not null and length(password) < 20";

            ps = cn.prepareStatement(sql);
            rs = ps.executeQuery();
            long bouiToexec;
            String pass, md5pass;
            while(rs.next())
            {
                bouiToexec = rs.getLong(1);
                pass = rs.getString(2);
                if(bouiToexec > 0)
                {
                    ps2 = cn.prepareStatement("update recipient set password = ? where boui = ?");
                    md5pass = netgest.utils.MD5Utils.toHexMD5(pass);
                    ps2.setString(1, md5pass);
                    ps2.setLong(2, bouiToexec);
                    ps2.executeUpdate();
                    ps2.close();
                }
            }
            rs.close();
            ps.close();
            cn.close();
            %>
            <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                    <title>Builder</title>
                </head>
                <body>
                    <h4>Operação efectuada com sucesso</h4>
                </body>
            </html>
            <%
        }
        catch(Exception e)
        {
            %>
            <html>
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                    <title>Builder</title>
                </head>
                <body>
                    <h4>Operação efectuada com insucesso</h4>
                </body>
            </html>
            <%
        }
        finally 
        {
            if(cn != null)
            {
                cn.close();
            }
            if (boctx!=null)boctx.close();
            if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
        }
%>

