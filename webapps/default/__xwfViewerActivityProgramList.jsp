<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>


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
 if(bosession== null)
 {
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

 Hashtable xattributes;
 request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");
 ctrl= DOCLIST.processRequest(boctx);
 IDX= ClassUtils.convertToInt(ctrl[0],-1);
 idbolist=ctrl[1];
 docHTML doc = DOCLIST.getDOC(IDX);
 boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);
        
    XwfController controller = (XwfController)doc.getController();
    boObject activity = controller.getRuntimeActivity();
    boObject variable = activity.getAttribute("message").getObject();
    boObject value = variable.getAttribute("value").getObject();
    boObject message = value.getAttribute("valueObject").getObject();
    
    ArrayList toRet = new ArrayList();    
    MenuItem svItem = new MenuItem();
    svItem.setId("add");
    svItem.setLabel("Associar a outros Programas");
    svItem.setImgURL("resources/xwfProgramRuntime/ico16.gif");
//    String sql = " boui <> "+controller.getRuntimeProgramBoui()+" AND runningState <> 90" ;
    
    StringBuffer boqlClause = new StringBuffer( " boui <> ");
    boqlClause.append(controller.getRuntimeProgramBoui());
    boqlClause.append(" AND runningState <> 90 ");
   
    
    svItem.setOnClickCode("LookupObjects('"+boqlClause+"','multi','xwfProgramRuntime','"+message.getName()+"','"+ message.getBoui() +"','dynbrige_xwfProgramRuntime','1')");
    svItem.setVisible(true);
    svItem.setDisabled(false);
    svItem.setAccessKey('A');     
    toRet.add(svItem);            
    PageController pageController = new PageController();
    Menu menu= new Menu(toRet);
    
    Element programBridge = xwfActivityViewer.getProgramReverseBridgeElement((XwfController)doc.getController(),IDX); 
%>
<html>
<script language="javascript">
function updateWaitFrame(program)
{           
    var waitList = null; 
    var wait = parent.document.getElementById('waitViewer');
    if(wait != null)
    {        
        wait.contentWindow.boFormSubmit.program.value = program;
        wait.contentWindow.submitGrid();
        return;
    }
}
</script>
<head>
</head>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()"  >
<form class="objectForm" name="boForm" id="<%=IDX%>">
    <table class='layout' cellpadding="0" cellspacing="0">
        <tr height='30px'>
            <td>
            <%= menu.getHTML(doc,DOCLIST,pageController) %>
            </td>
        </tr>
        <tr height='100%'>
            <td>    
            <%= programBridge.getHTML(doc,DOCLIST,pageController) %>
            </td>
        </tr>
    </table>            
</form>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='<%= ControllerFactory.CONTROLLER_NAME_KEY %>'  value='<%= controller.getName() %>'/>
    <INPUT type='hidden' name='<%= XwfKeys.PROGRAM_RUNTIME_BOUI_KEY %>'  value='<%= controller.getRuntimeProgramBoui() %>'/>
    <INPUT type='hidden' name='<%= XwfKeys.ACTIVITY_RUNTIME_BOUI_KEY %>' value='<%= controller.getRuntimeActivityBoui() %>'/>
    <INPUT type='hidden' name='boFormSubmitXml' />        
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='edit' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='boiChanged' value='true' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM> 
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
