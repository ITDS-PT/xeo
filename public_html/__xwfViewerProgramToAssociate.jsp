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
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.ql.*"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>
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
    String onclick = null;
    String moveWhat =  request.getParameter( "moveWhat" ); 
    if(moveWhat != null && "activity".equals(moveWhat))
    {
        onclick = URLEncoder.encode(URLEncoder.encode("parent.parent.parent.setActionCode('"+XwfKeys.ACTION_MOVE_ACTIVITY_KEY+"[OBJECT_LIST_BOUI]');parent.parent.parent.boForm.BindValues();parent.parent.parent.wait();","UTF-8"),"UTF-8");
    }
    else if(moveWhat != null && "program".equals(moveWhat))
    {
        onclick = URLEncoder.encode(URLEncoder.encode("parent.parent.parent.setActionCode('"+XwfKeys.ACTION_MOVE_PROGRAM_KEY+"[OBJECT_LIST_BOUI]');parent.parent.parent.boForm.BindValues();parent.parent.parent.wait();","UTF-8"),"UTF-8");
    }        
           
    StringBuffer move = new StringBuffer();


//    StringBuffer boql = new StringBuffer("SELECT xwfProgramRuntime WHERE 1=1 ORDER BY SYS_DTCREATE");
//    String jspName = "xwfprogramruntime_generallist.jsp?";    
//    associate.append(jspName);    
//    associate.append("docid=").append(IDX);
//    associate.append("&method=").append("list");
//    associate.append("&menu=").append("no");    
//    associate.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
//    associate.append("&boql=").append(QLParser.getURLboql(boql.toString()));
//    associate.append("&onclick=").append(onclick);    
//    associate.append("&canSelectRows=no");
    
    String jspName = "__list.jsp?";
    move.append(jspName);   
    move.append("docid=").append(IDX);
    move.append("&method=").append("list");
    move.append("&menu=").append("no");    
    move.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");    
//    move.append("&onclick=").append(onclick);
    move.append("&userClick=").append(onclick);
    move.append("&look_object=xwfProgramRuntime");
    move.append("&showNew=n");
    move.append("&canSelectRows=no");
    move.append("&look_query=boui<>" +controller.getRuntimeProgramBoui() );
    
    
    
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="&nbsp;&nbsp;Lista de Fluxos de Trabalho";  
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
                    <b>&nbsp;&nbsp;Fluxos de Trabalho</b>
                    <hr>                         
               </TD>
            </TR>        
            <TR height="100%">
               <TD>
                 <div width="100%" height="33%"> 
                    <IFRAME src='<%=move.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                    </IFRAME> 
                 </div>
               </TD>
            </TR>     
          </TABLE>            
    </DIV>
</form>
<FORM name='boFormSubmit' method='post'>
    <%
        java.util.Hashtable options = new java.util.Hashtable();    
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_INPUT_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "boui" ) != null){
            options.put(BasicPresentation.OPTION_REQUESTED_BOUI,request.getParameter( "boui" ));
        }
        options.put(BasicPresentation.OPTION_TYPE_FORM,"1");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());                 
    %>    
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>    
</FORM>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
