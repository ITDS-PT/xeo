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
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"BasicController");
    ctrl= DOCLIST.processRequest(boctx);    
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    String input_bouiStr=request.getParameter("inputObjectBoui");
    boolean testMode = "true".equals(request.getParameter("testMode"));
    
    String sendType=request.getParameter("sendType");
    String msgBoui=request.getParameter("msgBoui");
    String actvBoui=request.getParameter("actvBoui");
    
    long input_boui = -1;
//    boObject input_obj = null;
//    if(input_bouiStr != null && input_bouiStr.length() > 0)
//    {
//        try
//        {
//            input_boui = Long.parseLong(input_bouiStr);
//            input_obj = boObject.getBoManager().loadObject(boctx, input_boui);
//        }
//        catch(Exception e)
//        {
//            //ignore
//        }
//    }
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
  
    String inputObjectBoui = request.getParameter( "templateBoui");    
    String onclick = URLEncoder.encode(
                    URLEncoder.encode("winmain().newPage(getIDX(),'','edit','method=new&" + 
                                    ControllerFactory.CONTROLLER_NAME_KEY + 
                                    "=BasicController&masterdoc=true&inputObjectBoui=OBJECT_LIST_BOUI&relatedClientIDX="+IDX+
                                    "&ctxParentIdx="+IDX+
                                    "&sendType="+(sendType==null?"":sendType)+
                                    "&msgBoui="+(msgBoui==null?"":msgBoui)+
                                    "&actvBoui="+(actvBoui==null?"":actvBoui)+
                                    "&testMode="+(testMode ? "true":"false") +"&docid="+ 
                                    IDX +"','','__gestTempFillParameters.jsp');","UTF-8" ));
    String manualURL = "help/modelosCartas.pdf";

    StringBuffer src = new StringBuffer("gestemp_template_generallist.jsp?");
    src.append("docid=").append(IDX);
    src.append("&method=").append("list");
    src.append("&menu=").append("no");    
    src.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("BasicController");
//    src.append("&listmode=templateApply&boql=").append("SELECT+GESTEMP_Template+WHERE+activo%3D1+order+by+code");
//    String filterBoql = "select GESTEMP_Template where activo=1 and ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI ) order by ordem";
    String filterBoql = "";
    if(testMode)
    {
//        filterBoql = "select GESTEMP_Template where ( share in ( select iXEOUser.queues where boui=CTX_PERFORMER_BOUI) or  share in ( select iXEOUser.groups where boui=CTX_PERFORMER_BOUI) or share in ( select iXEOUser.roles where boui=CTX_PERFORMER_BOUI) or share =CTX_PERFORMER_BOUI ) order by ordem";
        filterBoql = "select GESTEMP_Template where historico = '0' order by ordem";
    }
    else
    {
        filterBoql = "select GESTEMP_Template where historico = '1' and activo=1 order by ordem";
    }
    String templateBoql = URLEncoder.encode(filterBoql);
    src.append("&listmode=templateApply&helperURL=").append(manualURL).append("&boql=").append(templateBoql);
    src.append("&userClick=").append(onclick);
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="Escolha o template que pretende aplicar";
    var objLabel="<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='Modelos' src='resources/GESTEMP_Template/ico16.gif' width='16' height='16'/><span title='Escolha o template que pretende aplicar'>Escolha o template que pretende aplicar</span>"
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
    <%if(sendType != null)%>
    <INPUT type='hidden' name='sendType' value=<%=sendType%> />
    <%if(msgBoui != null)%>
    <INPUT type='hidden' name='msgBoui' value=<%=msgBoui%> />
    <%if(actvBoui != null)%>
    <INPUT type='hidden' name='actvBoui' value=<%=actvBoui%> />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />        
</FORM> 
</body>
</html>
<%
} finally {
     boctx.close();
     DOCLIST.releseObjects(boctx);
}
%>
