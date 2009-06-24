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
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    
    String method=request.getParameter( "method" );
    String inputMethod=request.getParameter( "method" );
    String requestedBoui=request.getParameter( "boui" );    
    
    boObject BOI = DOC.getObject(Long.parseLong(requestedBoui));

    StringBuffer onclick = new StringBuffer();        
    onclick.append("winmain().openDocUrl('closeWindowOnCloseDoc=true,showCloseIcon=true,activeOnOpen=true','__xwfWorkPlace.jsp',");
    onclick.append("'?");
    onclick.append("method=edit");        
//    onclick.append("&menu=no");
    onclick.append("&masterdoc=true");
    onclick.append("&runtimeProgramBoui=OBJECT_LIST_BOUI')");
            
    String onclickStr = URLEncoder.encode(URLEncoder.encode(onclick.toString(),"UTF-8"),"UTF-8");    

    String jspName = "xwfprogramruntime_generallist.jsp?";        
    
//    StringBuffer boql = new StringBuffer("SELECT xwfProgramRuntime WHERE variables in (SELECT xwfVariable WHERE value in (SELECT xwfVarValue WHERE ");
//    boql.append(" (valueObject = ");
//    boql.append(requestedBoui);
//    boql.append(" or valueList = ");
//    boql.append(requestedBoui);
//    boql.append(" ))) ");
//    boql.append(" ORDER BY SYS_DTCREATE");   

    StringBuffer boql = new StringBuffer("SELECT [distinct OxwfProgramRuntime.BOUI], SYS_DTCREATE from xwfProgramRuntime WHERE variables.value.valueObject = ");
    boql.append(requestedBoui);
    boql.append(" or variables.value.valueList = ");
    boql.append(requestedBoui);
    boql.append(" ORDER BY SYS_DTCREATE");   

     
    StringBuffer newAct = new StringBuffer();
    newAct.append(jspName);
    newAct.append("docid=").append(IDX);
    newAct.append("&method=").append("list");
    newAct.append("&menu=").append("no");        
    newAct.append("&boql=").append(QLParser.getURLboql(boql.toString()));
//    newAct.append("&onclick=").append(onclickStr);
    newAct.append("&userClick=").append(onclickStr);
    newAct.append("&canSelectRows=no");

    
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
        <TABLE class="layout" cellspacing="0" cellpadding="0"  >     
            <TR height="100%">
               <TD>
                 <div width="100%" height="33%"> 
                    <IFRAME src='<%=newAct.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
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
