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
        
    String onclick = URLEncoder.encode(URLEncoder.encode("parent.parent.setActionCode('"+XwfKeys.ACTION_SHOW_KEY+"[OBJECT_LIST_BOUI]');parent.parent.boForm.BindValues();","UTF-8"),"UTF-8");

    String jspName = "xwfactivity_generallist.jsp?";
    
//    StringBuffer boql = new StringBuffer("SELECT xwfActivity STATE close WHERE program = ");
    StringBuffer boql = new StringBuffer("SELECT /*NO_SECURITY*/xwfActivity WHERE program = ");
    boql.append(controller.getRuntimeProgramBoui());
//    if(!xwfHelper.isWorkFlowAdministrator(controller.getEngine()))
//    {
//        boql.append(" AND ");
//        boql.append(xwfHelper.PERFORMER_CLAUSE_ALL);
//    }
    boql.append(" ORDER BY beginDate,endDate,BOUI");   
     
    StringBuffer done = new StringBuffer();
    done.append(jspName);
    done.append("docid=").append(IDX);
    done.append("&method=").append("list");
    done.append("&menu=").append("no");    
    done.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
    done.append("&boql=").append(boql.toString()/*"SELECT xwfActivity where 1<>1"*/);
    done.append("&onclick=").append(onclick);
    done.append("&userClick=").append(onclick);
    done.append("&userDblClick=window;");
    done.append("&canSelectRows=no");
//    done.append("&list_orderby=netgest.bo.runtime.sorter.ActivtiyDatesComparator");

    
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="&nbsp;&nbsp;Lista de Actividades";  
</script>
<%= DOC.getController().getPresentation().writeCSS() %>
</head>   

<body style='margin:0px'>

<form style='height:100%;' class="objectForm" name="boForm" id="<%=IDX%>">
    <IFRAME src='<%=done.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
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
