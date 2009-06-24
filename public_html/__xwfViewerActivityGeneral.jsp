<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
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
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"XwfController");
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
           
    XwfController controller = (XwfController)DOC.getController();       
    
    String result = null;
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="<%=xwfMainViewer.getProgramContextCardId(controller)%>";
</script>
<%= DOC.getController().getPresentation().writeJS() %>
</script>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()"  >

<div  style='height:100%;overflow:auto'>
<form class="objectForm" name="boForm" id="<%=IDX%>" >    
    
    <% if(controller.getPresentation().getType() == null || "".equals(controller.getPresentation().getType())){ %>
        <% if(controller.getRuntimeActivity() != null ){ %>    
            <% if("xwfActivityReceive".equals(controller.getRuntimeActivity().getName())){            
                    Element up = xwfActivityViewer.getPreviewReceiveElement(controller);
                    if(XwfKeys.ACTION_ASSOCIATE_KEY.equals(controller.getActionCode()))
                    {
                        Element right = xwfActivityViewer.getProgramElement(controller,IDX);
                        Element left = xwfActivityViewer.getWaitElement(controller,IDX);     
                        Splitter splitter = Splitter.getSplitter(up,left,right);
                        result = splitter.getHTML(DOC,DOCLIST,new PageController());
                    }
                    else
                    {
                        result = up.getHTML(DOC,DOCLIST,new PageController());
                    }
                    
            %>            
                <%= result%>
                
            <% } else if("xwfWaitResponse".equals(controller.getRuntimeActivity().getName())){        
                    Element up = xwfActivityViewer.getPreviewSendElement(controller);
                    Element downLeft = xwfActivityViewer.getProgramMessagesElement(controller,IDX);
                    Element downRight = xwfActivityViewer.getEmptyPreview();
                    Splitter splitter = Splitter.getSplitter(up,downLeft,"30%",downRight,"70%");
            %>   
                <%= splitter.getHTML(DOC,DOCLIST,new PageController()) %>
                
            <% } else if("xwfUserCallProgram".equals(controller.getRuntimeActivity().getName())){ 
                    Element callProgram = xwfActivityViewer.getCallProgramElement(controller,IDX);                                
            %>   
                <%= callProgram.getHTML(DOC,DOCLIST,new PageController()) %>
                
            <% } else { %>        
                <%xwfActivityViewer.renderActivity(controller,pageContext,IDX);%>
            <% } %>
        <% } %>        
    <% } else {%>    
        <%xwfActivityViewer.renderVariables(controller,pageContext,IDX);%>        
    <% } %>        
</form>
<div>
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
        options.put(BasicPresentation.OPTION_TYPE_FORM,"0");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());    
        if("show.attributes".equals(controller.getPresentation().getType())){ 
            options.put(BasicPresentation.OPTION_FORCE_NO_CHANGE,"true");
        }
    %>
    
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>  
    
    
<!--    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='<%= ControllerFactory.CONTROLLER_NAME_KEY %>'  value='XwfController'/>        
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='edit' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <% if(controller.getRuntimeActivity() != null) { %>
        <INPUT type='hidden' name='BOUI' value='<%= controller.getRuntimeActivityBoui() %>'/>
    <%}%>
    <% if(controller.getRuntimeActivity() != null) { %>
        <INPUT type='hidden' name='boiChanged' value='<%= controller.getRuntimeActivity().isChanged() %>'/>
    <%}%>    
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <%if(controller.getRuntimeActivity() != null){%>
        <INPUT type='hidden' value='<%=controller.getRuntimeActivity().getClass().getName()%>' name='boFormSubmitSecurity' />
    <%}%>-->
</FORM> 
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
