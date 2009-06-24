<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.xwf.*"%>
<%@ page import="netgest.xwf.common.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.data.Driver"%>
<%
String message=null;
EboContext boctx = null;
boSession bosession =null;
boApplication boapp =null;
try {
    FileReader ixeouser=null;
    try
    {
      bosession = (boSession)request.getSession().getAttribute("boSession");
      ixeouser=new FileReader(boConfig.getDeploymentDir()+"iXEOUser.xeoimodel");
      boapp = boApplication.getApplicationFromStaticContext("XEO");
      boapp.boLogin( "SYSUSER" , boLoginBean.getSystemKey() , boapp.getDefaultRepositoryName() );        
      ixeouser.close();
      if(bosession== null || !bosession.getUser().getUserName().equalsIgnoreCase("SYSUSER")) {
          response.sendRedirect("login.jsp?returnToPage=adhoc.jsp");
          return;
      }
    }
    catch (Exception e )
    {
      if (ixeouser!=null)ixeouser.close();
    }    
        boolean initPage=true;
        final boolean AUTO_LOGIN = false;
        boolean ok=false;
        java.util.Enumeration oEnum = request.getHeaderNames();
       
        if ( request.getSession() != null && request.getSession().getAttribute("boSession")!=null )
        {
            bosession = (boSession)request.getSession().getAttribute("boSession");
            ok=true;
        }
        if(!ok){
            bosession = boapp.boLogin("SYSTEM",boLoginBean.getSystemKey());

            request.getSession().setAttribute("boSession",bosession);
            
            ok=true;
        }
        if(boctx == null && ok)
        {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.getSession().setAttribute("a_EboContext",boctx);
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
                        errormessage = "Nome de utilizador ou palavra passe inv?da.";
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
                        errormessage = "Nome de utilizador ou palavra passe inv?da.";
                    }
                } catch (Exception e) {
                    errormessage = "Ocorreu um erro inesperado ao efectuar login";
                    throw(e);
                }
            }
        }

        if(request.getParameter("option")!=null) {
            short option = Short.parseShort(request.getParameter("option"));
            switch(option) {
                case 1:
                    boObject perf;
                    perf =boObject.getBoManager().loadObject(boctx,"Ebo_Perf","username='SYSUSER'");
                    if(!perf.exists()) {
                        perf =boObject.getBoManager().createObject(boctx,"Ebo_Perf");
                    }
                    perf.getAttribute("name").setValueString("Default user");
                    perf.getAttribute("id").setValueString("SYSUSER");
                    perf.getAttribute("username").setValueString("SYSUSER");
                    perf.getAttribute("password").setValueString("ABC");
                    perf.update();
                    boctx.close();
                    message = "Utilizador criado com sucesso";
                    break;
                case 2:
                    boObject group;
                    group = boObject.getBoManager().loadObject(boctx,"Ebo_Group","name='PUBLIC'");
                    if(!group.exists()) {
                        group = boObject.getBoManager().createObject(boctx,"Ebo_Group");
                    }
                    group.getAttribute("name").setValueString("PUBLIC");
                    group.getAttribute("id").setValueString("PUBLIC");
                    group.setBoui(-2);
                    group.update();
                    boctx.close();
                    message = "Grupo criado com sucesso";
                    break;
                case 3:
                    {
                    boObject perfr;
                    perfr = boObject.getBoManager().loadObject(boctx,"Ebo_Perf","username='ROBOT'");
                    if(!perfr.exists()) {
                        perfr = boObject.getBoManager().createObject(boctx,"Ebo_Perf");
                    }
                    perfr.getAttribute("name").setValueString("ROBOT");
                    perfr.getAttribute("id").setValueString("ROBOT");
                    perfr.getAttribute("username").setValueString("ROBOT");
                    perfr.getAttribute("password").setValueString("ROBOT");
                    perfr.update();
                    boctx.close();
                    message = "Utilizador Robot criado com sucesso";
                    break;
                    }
                case 4:
                    {
                    if(request.getParameter("2_icrementby")!=null && request.getParameter("2_icrementby").length()>0) {
                        long x = Long.parseLong(request.getParameter("2_icrementby"));
                        for (int i = 0; i < x; i++) 
                        {
                            netgest.utils.DataUtils.getDataDBSequence(boctx,"BORPTSEQUENCE", Driver.SEQUENCE_NEXTVAL);
                        }
                        message="BOUI incrementado em ["+x+"] com sucesso.";
                    }
                    }
                case 5:
                        double v = 0; 
                        Date beginDate = null;
                        Date endDate = null;
                        AttributeHandler totalDuration = null;
                        bridgeHandler wrk = null;
                        boObject object = null;
                       boObjectList list = boObjectList.list(boctx,"select atendimento where workHistory is null and beginDate is not null and endDate is not null",1,9999);
                       list.beforeFirst();
                       while(list.next())
                       {
                            object = list.getObject();
                            
                            
                            totalDuration = object.getAttribute("totalDuration");
                            beginDate = object.getAttribute("beginDate").getValueDate();
                            endDate = object.getAttribute("endDate").getValueDate();
                            if(beginDate != null && endDate != null)
                            {
                                if((endDate.getTime() - beginDate.getTime()) > 0)
                  {
                                    v = (endDate.getTime() - beginDate.getTime())/60/1000;
                                    if( v > 0)
                                    {
                                        double xdurt = totalDuration.getValueDouble();
                                        if (Double.isNaN(xdurt))
                                        {
                                            xdurt = 0;
                                        }
                                        totalDuration.setValueDouble(xdurt + v);
                                        wrk = object.getBridge("workHistory");
                                        boObject it = wrk.addNewObject();
                                        it.getAttribute("duration").setValueDouble(v);
                                        
                                        it.getAttribute("performer").setValueLong(object.getAttribute("performer").getValueLong());

                                        it.getAttribute("workDate").setValueDate(endDate);
                                        object.update(false,false);
                                    }                                    
                                }
                            }                       
                       }
                       break;
                    case 6:
                    boObject superGroup;
                    superGroup = boObject.getBoManager().loadObject(boctx,"Ebo_Group","name='SUPERVISOR'");
                    if(!superGroup.exists()) {
                        superGroup = boObject.getBoManager().createObject(boctx,"Ebo_Group");
                    }
                    superGroup.getAttribute("name").setValueString("SUPERVISOR");
                    superGroup.getAttribute("id").setValueString("SUPERVISOR");
                    superGroup.setBoui(-3);
                    superGroup.update();
                    boctx.close();
                    message = "Grupo criado com sucesso";
                    break;
                default:
                    ;    
            }
        }
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>netgest.net business objects</title>
</head>
<body>
<h1 align="center" >netgest.net Business Object System Administration</h1>
<form id="sysform" method="GET">
<input type="HIDDEN" name="option" value="0" ></input>
<table align="center" border="1" width="40%" >
    <tr>
        <th><h2>Links de administração</h2></th>
    </tr>
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='1';sysform.submit();">1. Criar utilizador SYSUSER</a>
        </td>
    </tr>
    
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='2';sysform.submit();">2. Criar Grupo PUBLIC</a>
        </td>
    </tr>
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='3';sysform.submit();">3. Criar utilizador ROBOT</a>
        </td>
    </tr>
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='4';sysform.submit();">Next BOUI [<%=netgest.utils.DataUtils.getDataDBSequence(boctx,"BORPTSEQUENCE", Driver.SEQUENCE_CURRENTVAL)%>],Incrementar em:<input size="6" name="2_icrementby" type="TEXT" ></INPUT</a>
        </td>
    </tr>
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='5';sysform.submit();">4. Atendimento</a>
        </td>
    </tr>
    <tr>
        <td align="center">
            <a href="javascript:sysform.option.value='6';sysform.submit();">5. Criar Grupo SUPERVISOR</a>
        </td>
    </tr>
    
</table>
</form>
<%
if (message != null) {
    out.println("<p></p><b>"+message+"</b>");
}
%>
</body>
</html>
<%
}catch(Exception e){e.printStackTrace();} finally {
    if(boctx != null)
        boctx.close();
}

%>
