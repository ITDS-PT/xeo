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
<%@ page import="java.sql.Connection"%>
<%@ page import="java.sql.PreparedStatement"%>
<%@ page import="java.sql.ResultSet"%>
<%@ page import="netgest.bo.runtime.EboContext"%>
<%@ page import="netgest.bo.runtime.boRuntimeException"%>

<html><head><meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Tabelas Inválidas</title></head><body>
<h4>Tabelas Inválidas</h4>
<textarea rows="20" name="S1" readonly="readonly" cols="80"><%
    
        EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
            
        boolean initPage=true;
        final boolean AUTO_LOGIN = false;
        boolean ok=false;
        java.util.Enumeration oEnum = request.getHeaderNames();
        String fullwindow=request.getParameter("fullwindow");
       
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
                    fullwindow = "";
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
        
        Connection con = null;
        boConfig p_bcfg = new boConfig();
        int size = 0;
        PreparedStatement ps = null, ps2 = null;
        ResultSet rs = null, rs2 = null;
        StringBuffer sb = new StringBuffer();
        boolean tableWithBoui = false;
        try 
        {
            long iT = System.currentTimeMillis();
            File eboobjdir = new File(p_bcfg.getDeploymentDir());
            String boname, auxName;
            File aux;
            con = boctx.getConnectionData();
            ps = con.prepareStatement("select table_name from user_tables where table_name not like '%$%'");
            rs = ps.executeQuery();
            String tableName;
            boolean found = false;
            File[] files = eboobjdir.listFiles();
            while(rs.next())
            {
                found = false;
                tableName = rs.getString(1);
                if(!tableName.equalsIgnoreCase("DBFS_FILE") || !tableName.equalsIgnoreCase("SYSNGT_SEQUENCES"))
                {
                    //verificar no XML
                    char l = tableName.charAt(tableName.length() -1);
                    boolean isNumber = false;
                    try{
                        Integer.parseInt(String.valueOf(l));
                        isNumber = true;
                    }
                    catch(NumberFormatException e)
                    {
                        //ignora
                    }
                    
                    if(!isNumber)
                    {
                        for(int i =0; i < files.length && !found; i++)
                        {
                            aux = files[i];
                            if(aux.getName().toUpperCase().endsWith("$BO.XML"))
                            {                        
                                boname = aux.getName().substring(0,aux.getName().toUpperCase().indexOf("$BO"));
                                boDefHandler boDef = boDefHandler.getBoDefinition(boname);
                                if(tableName.equalsIgnoreCase(boDef.getBoPhisicalMasterTable()) || 
                                    (("".equals(boDef.getBoPhisicalMasterTable()) || boDef.getBoPhisicalMasterTable() == null)
                                    && tableName.equalsIgnoreCase(boname))
                                )
                                {
                                    found = true;
                                }                 
                            }
                        }                
                        if(!found)
                        {                        
                            ps2 = con.prepareStatement("select table_name from user_tables where table_name like '" + tableName + "$%'");
                            rs2 = ps2.executeQuery();                    
                            while(rs2.next())
                            {
                                sb.append("drop table " + rs2.getString(1) + ";\n");
                                sb.append("delete from ngtdic where tablename = '" + rs2.getString(1) + "';\n");
                                size++;
                            }                        
                            sb.append("drop table " + tableName + ";\n");
                            sb.append("delete from ngtdic where tablename = '" + tableName + "';\n");
                            size++;
                            ps2.close();
                            rs2.close();                    
                        }
                    }
                }
            }
            rs.close();
            ps.close();            
            sb.append("N. de tabelas inválidas: " + size  + "\n");
            sb.append("Tempo total(seg): " + ((System.currentTimeMillis() - iT)/1000));            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally 
        {
            try{
                if(rs != null)
                {
                    rs.close();
                }
            }catch(Exception e){}
            try{
                if(ps != null)
                {
                    ps.close();
                }
            }catch(Exception e){}
            
             try{
                if(rs2 != null)
                {
                    rs2.close();
                }
            }catch(Exception e){}
            try{
                if(ps2 != null)
                {
                    ps2.close();
                }
            }catch(Exception e){}
            try{
                if(con != null)
                {
                    con.close();
                }
            }catch(Exception e){}
        }
%>
<%= sb.toString()%>
</textarea></body></html>
