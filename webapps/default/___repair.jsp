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
        response.sendRedirect("login.jsp?returnToPage=___repair.jsp"+ java.net.URLEncoder.encode(url.toString() ));
        return;
    }
    if(request.getParameter("execute")==null) {%>
        <html>
            <head>
                <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                <title>Builder</title>
            </head>
            <body>
                <h4>Introduza o nome do objecto que efectuar o re-index ou deixe em branco para um re-index total</h4>
                <form>
                    <p>
                        <input type="text" name="objName" size="50">
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

            boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
            java.sql.Connection cn = boctx.getConnectionData();
            java.sql.PreparedStatement ps ;
            java.sql.ResultSet rs ;
            String sql = null;

            String objectName = request.getParameter("objName");
            String operation = request.getParameter("operation");
            boDefHandler bodef = null;
            if((objectName == null || "".equals(objectName)) && "rebuildTextIndex".equals(operation))
            {
//                if(DBLock.lock(boctx, "EBO_TextIndex", 300, 10, false))
//                {
                    lock = true;
                    javax.naming.InitialContext ic = new javax.naming.InitialContext();
                    boDefHandler handlers[] = boDefHandler.getAllBoDefinition();
                    boolean wasupdated;
                    for (int k = 0; k < handlers.length; k++) 
                    {
                        if ( handlers[k].isTextIndexActive() )
                        {
                            bodef = handlers[k];
                            objectName = handlers[k].getName();
                            System.out.println("Começou o Re-Index do objecto " + objectName);
                            boObject chkobj = null;
                            ps = cn.prepareStatement("select boui from "+bodef.getBoMasterTable()+" o where not exists(select * from ebo_textindex t where t.ui$ = o.boui) and classname=?");
                            ps.setString(1, objectName);
                            rs = ps.executeQuery();
                            long bouiToexec;                        
                            ArrayList bouis = new ArrayList();
                            ArrayList bouis200 = new ArrayList();
                            while(rs.next())
                            {
                                bouis.clear();
                                bouis.add(new Long(rs.getLong(1)));
                                int n = 199;
                                for(int i = 199; i > 0 && rs.next(); i --)
                                {
                                    bouis.add(new Long(rs.getLong(1)));    
                                }
    
                                javax.transaction.UserTransaction ut = (javax.transaction.UserTransaction) ic.lookup("java:comp/UserTransaction");                
                                ut.begin();
                                boolean commit = false;
                                try
                                {
                                    for (int i = 0; i < bouis.size(); i++) 
                                    {        
                                        try
                                        {                    
                                            try
                                            {
                                                CallableStatement cstm  = null;
                                                try
                                                {
                                                    cstm  = cn.prepareCall("ALTER SESSION SET CURSOR_SHARING='EXACT'");
                                                    cstm.execute();
                                                }
                                                catch (SQLException e)
                                                {
                                                    e.printStackTrace();
                                                    // User doesn't have permissions to invoke ALTER SESSION;
                                                }
                                                if( cstm != null )
                                                {
                                                    cstm.close();
                                                }
                                                
                                                try
                                                {
                                                    wasupdated = true;
                                                    boObject object;
                                                    if( chkobj != null )
                                                    {
                                                        object = chkobj;
                                                        chkobj = null;
                                                        
                                                    }
                                                    else
                                                    {
                                                        object = boObject.getBoManager().loadObject(boctx, ((Long)bouis.get(i)).longValue());                                
                                                    }
                                                    
                                                    if( object.exists() )
                                                    {
                                                        boObject textIndex = boObject.getBoManager().loadObject( boctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { new Long( object.getBoui() )} );
                                                        textIndex.getAttribute("ui").setValueLong( object.getBoui() );
                                                        textIndex.getAttribute("uiClass").setValueString( object.getName() );
                                                        textIndex.update();
                                                    }
                                                }
                                                catch (boRuntimeException e)
                                                {
                                                    if ( e.getErrorCode().equals("BO-3015") )
                                                    {
                                                        boObject textIndex = boObject.getBoManager().loadObject( boctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { bouis.get((i))});
                                                        if(textIndex.exists()) 
                                                        {
                                                            textIndex.destroy();
                                                        }
                                                    }
                                                    else
                                                    {
                                                        throw e;
                                                    }
                                                }
                        
                                            }
                                            catch(Exception e)
                                            {
                                                e.printStackTrace();
                                                System.out.println(e.getLocalizedMessage());
                                            }
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            System.out.println(e.getLocalizedMessage());
                                        }
                                    }
                                    ut.commit();
                                    commit = true;
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                    System.out.println(e.getLocalizedMessage());
                                }
                                finally 
                                {
                                    if(!commit && ut.getStatus() == javax.transaction.Status.STATUS_ACTIVE ) ut.rollback();
                                }
                            }
                            System.out.println("Terminou o Re-Index do objecto " + objectName);
                            rs.close();
                            ps.close();
                        }
                    }
//                }
//                else
//                {
//                    System.out.println("Não conseguí o lock");
//                }
            }
            else if(objectName != null && "rebuildTextIndex".equals(operation))
            {
                boolean wasupdated = false;
                bodef = boDefHandler.getBoDefinition(objectName);
                if ( bodef.isTextIndexActive() )
                {
//                    if(DBLock.lock(boctx, "EBO_TextIndex", 300, 10, false))
//                    {
                        lock = true;
                        System.out.println("Começou o Re-Index do objecto " + bodef.getName());                    
                        boObject chkobj = null;
                        ps = cn.prepareStatement("select boui from "+bodef.getBoMasterTable()+" o where not exists(select * from ebo_textindex t where t.ui$ = o.boui) and classname=?");
                        ps.setString(1, objectName);
                        rs = ps.executeQuery();
                        long bouiToexec;
                        javax.naming.InitialContext ic = new javax.naming.InitialContext();
                        ArrayList bouis = new ArrayList();
                        ArrayList bouis200 = new ArrayList();
                        while(rs.next())
                        {
                            bouis.clear();
                            bouis.add(new Long(rs.getLong(1)));
                            int n = 199;
                            for(int i = 199; i > 0 && rs.next(); i --)
                            {                            
                                if(rs.getLong(1) != 15448688)
                                {
                                    System.out.println("EI - " + rs.getLong(1));
                                    bouis.add(new Long(rs.getLong(1)));
                                }
                                else
                                {
                                    System.out.println("Saltei");
                                }
                            }
                        
                            javax.transaction.UserTransaction ut = (javax.transaction.UserTransaction) ic.lookup("java:comp/UserTransaction");                
                            ut.begin();
                            boolean commit = false;
                            try
                            {
                                for (int i = 0; i < bouis.size(); i++) 
                                {     
                                    System.out.println("Começou o Re-Index do objecto do boui" + ((Long)bouis.get(i)).longValue());
                                    if(((Long)bouis.get(i)).longValue() != 15448688)
                                    {
                                        try
                                        {
                        //                    boctx = bosession.createRequestContext(null,null,null);                    
                                            try
                                            {
                                                CallableStatement cstm  = null;
                                                try
                                                {
                                                    cstm  = cn.prepareCall("ALTER SESSION SET CURSOR_SHARING='EXACT'");
                                                    cstm.execute();
                                                }
                                                catch (SQLException e)
                                                {
                                                    e.printStackTrace();
                                                    // User doesn't have permissions to invoke ALTER SESSION;
                                                }
                                                System.out.println("1");
                                                if( cstm != null )
                                                {
                                                    cstm.close();
                                                }
                                                System.out.println("2");
                                                try
                                                {
                                                    wasupdated = true;
                                                    boObject object;
                                                    if( chkobj != null )
                                                    {
                                                        object = chkobj;
                                                        chkobj = null;
                                                        
                                                    }
                                                    else
                                                    {
                                                        System.out.println("3");
                                                        object = boObject.getBoManager().loadObject(boctx, ((Long)bouis.get(i)).longValue());                                
                                                    }
                                                    System.out.println("4");
                                                    if( object.exists() )
                                                    {
                                                        System.out.println("5 - "+object.getBoui());
                                                        boObject textIndex = boObject.getBoManager().loadObject( boctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { new Long( object.getBoui() )} );
                                                        System.out.println("523");
                                                        textIndex.getAttribute("ui").setValueLong( object.getBoui() );
                                                        textIndex.getAttribute("uiClass").setValueString( object.getName() );
                                                        System.out.println("123");
                                                        textIndex.update();
                                                        System.out.println("6");
                                                    }
                                                    System.out.println("7");
                                                }
                                                catch (boRuntimeException e)
                                                {
                                                    e.printStackTrace();
                                                    if ( e.getErrorCode().equals("BO-3015") )
                                                    {
                                                        System.out.println("8");
                                                        boObject textIndex = boObject.getBoManager().loadObject( boctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { bouis.get((i))});
                                                        System.out.println("9");
                                                        if(textIndex.exists()) 
                                                        {
                                                            System.out.println("10");
                                                            textIndex.destroy();
                                                            System.out.println("11");
                                                        }
                                                    }
                                                    else
                                                    {
                                                        System.out.println("12");
                                                        throw e;
                                                    }
                                                }
                        
                                            }
                                            catch(Exception e)
                                            {
                                                e.printStackTrace();
                                                System.out.println(e.getLocalizedMessage());
                                            }
                                        }
                                        catch(Exception e)
                                        {
                                            e.printStackTrace();
                                            System.out.println(e.getLocalizedMessage());
                                        }
                                    }
                                    else
                                    {
                                        System.out.println("Voltei a saltar");
                                    }
                                }
                                System.out.println("13");
                                ut.commit();
                                System.out.println("14");
                                commit = true;
                            }
                            catch(Exception e)
                            {
                                e.printStackTrace();
                                System.out.println(e.getLocalizedMessage());
                            }
                            finally 
                            {
                                System.out.println("15");
                                if(!commit && ut.getStatus() == javax.transaction.Status.STATUS_ACTIVE ) ut.rollback();
                            }
                        }
                        System.out.println("Terminou o Re-Index do objecto " + bodef.getName());
                        rs.close();
                        ps.close();
//                    }
//                    else
//                    {
//                        System.out.println("Não conseguí o lock");
//                    }
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
            if(lock)
            {
//                if(DBLock.releaseLock(boctx, "EBO_TextIndex"))
//                {
//                    System.out.print("Libertei o lock");
//                }
//                else
//                {
//                    System.out.print("não Libertei o lock");
//                }
            }
            System.out.print("</textarea></p></td></tr></table></body></html>");
            if (boctx!=null)boctx.close();
            if (DOCLIST!=null && boctx!=null)DOCLIST.releseObjects(boctx);
            System.setOut(xout);
            System.setErr(xerr);
        }
    }
%>

