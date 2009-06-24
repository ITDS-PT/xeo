<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

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
    response.sendRedirect("login.jsp");
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
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"XwfController");
    ctrl= DOCLIST.processRequest(boctx);    
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    String input_bouiStr=request.getParameter("inputObjectBoui");
    long input_boui = -1;
    boObject input_obj = null;
    if(input_bouiStr != null && input_bouiStr.length() > 0)
    {
        try
        {
            input_boui = Long.parseLong(input_bouiStr);
            input_obj = boObject.getBoManager().loadObject(boctx, input_boui);
        }
        catch(Exception e)
        {
            //ignore
        }
    }
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
  
    String redirectUrl = request.getParameter("nextPage");
    if ( redirectUrl != null && !DOC.haveErrors() )
    {
        response.sendRedirect( java.net.URLDecoder.decode(redirectUrl)  );
        return;
    }  
  
    String inputObjectBoui = request.getParameter( XwfKeys.PROGRAM_INPUT_OBJECT_BOUI_KEY);    
//    String onclick = URLEncoder.encode(URLEncoder.encode("winmain().newPage(getIDX(),'','edit','method=new&" + ControllerFactory.CONTROLLER_NAME_KEY + "=XwfController&masterdoc=true&defProgramBoui=OBJECT_LIST_BOUI&inputObjectBoui=" + inputObjectBoui +"&relatedClientIDX="+IDX+"&ctxParentIdx="+IDX+"&docid="+ IDX +"','','__xwfMainViewer.jsp');"));
    String onclick = URLEncoder.encode(URLEncoder.encode("winmain().newPage(getIDX(),'','edit','method=new&" + ControllerFactory.CONTROLLER_NAME_KEY + "=XwfController&masterdoc=true&defProgramBoui=OBJECT_LIST_BOUI&inputObjectBoui=" + inputObjectBoui +"&relatedClientIDX="+IDX+"&ctxParentIdx="+IDX+"&docid="+ IDX +"','','__xwfWorkPlace.jsp');","UTF-8" ));

    StringBuffer src = new StringBuffer("xwfprogram_generallist.jsp?");
    src.append("docid=").append(IDX);
    src.append("&method=").append("list");
    src.append("&menu=").append("no");    
    src.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
    if(input_obj == null)
    {
        src.append("&boql=").append("SELECT+xwfProgram+WHERE+1%3D1");
    }
    else
    {
        src.append("&boql=").append(URLEncoder.encode("SELECT xwfProgram WHERE (versions.input.name is null) or (versions.input.name = '" + input_obj.getName() + "') ORDER BY name "));
    }
//    src.append("&onclick=").append(onclick);
    src.append("&userClick=").append(onclick);
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="Escolha o Programa";  
</script>
</head>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()" >
<% if(BOI != null) { %>
    <iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=BOI.getBoui()%>"></iframe>
<% } %>
<form class="objectForm" name="boForm" id="<%=IDX%>">
<TABLE class="layout" cellspacing="0" cellpadding="0">
        <TR>
           <TD>
             <div width="100%" height="100%"> 
                <IFRAME src='<%=src.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                </IFRAME> 
             </div>
           </TD>
        </TR>     
  </TABLE>
</form>
<FORM name='boFormSubmit' method='post'>

    <INPUT type='hidden' name='<%= ControllerFactory.CONTROLLER_NAME_KEY %>'  value='<%= DOC.getController().getName() %>'/>    
    <INPUT type='hidden' name='boFormSubmitXml' />        
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='boiChanged' value='true' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />        
</FORM> 
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
