<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="netgest.bo.controller.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=claim_generaledit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    int IDX;int cvui;
    boDefHandler bodef;
    boDefAttribute atr;
    String idbolist;
    String[] ctrl;
    docHTML_section sec;
    docHTML_grid grid;
    Hashtable xattributes;    
    request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    String method=request.getParameter( "method" );
    String inputMethod=request.getParameter( "method" );
    String requestedBoui=request.getParameter( "boui" );
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    
    if(request.getParameter("objectBoui")!=null)
    {
        BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
        long[] a_boui = {BOI.getBoui()};
        currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
        if(!currObjectList.haveBoui(a_boui[0]))
            currObjectList.inserRow(a_boui[0]);
    }
    if( currObjectList != null ) 
        currObjectList.first();

    XwfController controller = (XwfController)DOC.getController();
    
    
    String onclick = URLEncoder.encode(URLEncoder.encode("winmain().newPage(getIDX(),'','edit','method=new&" + ControllerFactory.CONTROLLER_NAME_KEY + "=XwfController&masterdoc=true&"+XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY+"=OBJECT_LIST_BOUI&relatedClientIDX="+IDX+"&ctxParentIdx="+IDX+"&docid="+ IDX +"','','__xwfWorkPlace.jsp');","UTF-8"),"UTF-8");    

    String jspName = "xwfactivity_generallist.jsp?";
    StringBuffer boql = new StringBuffer();
    
    StringBuffer wainting = new StringBuffer();
    wainting.append(jspName);
    wainting.append("docid=").append(IDX);
    wainting.append("&method=").append("list");
    wainting.append("&menu=").append("no");    
    wainting.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
    wainting.append("&boql=").append("SELECT+xwfActivity+WHERE+1%3D1");
//    wainting.append("&onclick=").append(onclick);
    wainting.append("&userClick=").append(onclick);
    

    StringBuffer next = new StringBuffer();
    next.append(jspName);
    next.append("docid=").append(IDX);
    next.append("&method=").append("list");
    next.append("&menu=").append("no");    
    next.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
    next.append("&boql=").append("SELECT+xwfActivity+WHERE+1%3D1");
//    next.append("&onclick=").append(onclick);  
    next.append("&userClick=").append(onclick);

    StringBuffer done = new StringBuffer();
    done.append(jspName);
    done.append("docid=").append(IDX);
    done.append("&method=").append("list");
    done.append("&menu=").append("no");    
    done.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
    done.append("&boql=").append("SELECT+xwfActivity+WHERE+1%3D1");
//    done.append("&onclick=").append(onclick);    
    done.append("&userClick=").append(onclick);

    
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="Lista de Actividades";  
</script
<%= DOC.getController().getPresentation().writeCSS() %>
</head>   
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()" style='background-color:black' >

<form style='height:100%;' class="objectForm" name="boForm" id="<%=IDX%>">
    <DIV style='width:100%;height:100%;overflow:auto;background-color:#D4E5FB'>
        <TABLE class="layout" cellspacing="0" cellpadding="0" >
                <TR height="50px" bgcolor="White">
                   <TD>   
                        <br>
                        <b>&nbsp;&nbsp;Actividades Pendentes</b>
                        <hr>                         
                   </TD>
                </TR>        
                <TR height="32%">
                   <TD>
                     <div width="100%" height="33%"> 
                        <IFRAME src='<%=wainting.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                        </IFRAME> 
                     </div>
                   </TD>
                </TR>     
                <TR height="50px"  bgcolor="White">
                   <TD>
                      <br>
                      <b>&nbsp;&nbsp;Próximas Actividades</b>
                      <hr> 
                   </TD>
                </TR>                 
                <TR height="32%">
                   <TD>
                     <div width="100%" height="33%"> 
                        <IFRAME src='<%=next.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                        </IFRAME> 
                     </div>
                   </TD>
                </TR>     
                <TR height="50px"  bgcolor="White">
                   <TD>
                      <br>
                      <b>&nbsp;&nbsp;Actividades Executadas</b>
                     <hr> 
                   </TD>
                </TR>                 
                <TR height="32%">
                   <TD>
                     <div width="100%" height="33%"> 
                        <IFRAME src='<%=done.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                        </IFRAME> 
                     </div>
                   </TD>
                </TR>     
          </TABLE>            
    </DIV>
</form>
<FORM name='boFormSubmit' method='post'>
    <% if(controller.getRuntimeActivity() != null) { %>            
        <INPUT type='hidden' value='<%=controller.getRuntimeActivity().getClass().getName()%>' name='boFormSubmitSecurity' />
        <INPUT type='hidden' name='BOUI' value='<%= controller.getRuntimeActivityBoui() %>'/>
    <% } else { %>
        <INPUT type='hidden' value='<%=controller.getRuntimeProgram().getClass().getName()%>' name='boFormSubmitSecurity' />
        <INPUT type='hidden' name='BOUI' value='<%= controller.getRuntimeProgramBoui()%>'/>
    <% } %>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <!--<INPUT type='hidden' name=parent_attribute value='<parent_attribute>' />-->
    <INPUT type='hidden' name=parent_boui value='<%= parent_boui %>' />
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='boiChanged' value='false' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />    
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
