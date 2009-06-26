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
<%@ page import="netgest.xwf.common.xwfHelper"%>
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
 request.setAttribute("inMainController","true");
 ctrl= DOCLIST.processRequest(boctx);
 IDX= ClassUtils.convertToInt(ctrl[0],-1);
 idbolist=ctrl[1];
 docHTML DOC = DOCLIST.getDOC(IDX);
 boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
        
    XwfController controller = (XwfController)DOC.getController();   
    boObject activity = controller.getRuntimeActivity();
    long activityWaitBoui = controller.getRuntimeActivityBoui();
    long receiveActivityBoui = ClassUtils.convertToLong(activity.getAttribute("receiveActivity").getValueString(),-1);
    
    StringBuffer sql = new StringBuffer("select xwfActivityReceive where ");
    sql.append("program = ").append(controller.getRuntimeProgramBoui());
    sql.append(" and ");
    sql.append(xwfHelper.PERFORMER_CLAUSE_ALL);
//    sql.append(" and ");
//    boObject from = activity.getAttribute("waitFrom").getObject();
//    sql.append("message.value.valueObject in (select message where message.from.refObj = ").append(from.getAttribute("refObj").getObject().getBoui()).append(")");
    
    boObjectList activityReceiveBridge = controller.getEngine().getBoManager().listObject(sql.toString());
    activityReceiveBridge.beforeFirst();
%>
<html>
<script language="javascript">
function updateMessageFrame(message)
{       
    var o = window.event.srcElement;
    var tbl = o;
    while ( tbl.tagName!='TABLE') tbl=tbl.parentElement;
    var tr = tbl.getElementsByTagName("TR");

    var found = -1;
    for(i = 0; i < tr.length ; i++ && found != -1)
    {        
        if(tr[i].runtimeStyle.backgroundColor == "#DAE6E9".toLowerCase())
        {
           tr[i].runtimeStyle.backgroundColor="";
           found = i;
        }
    }
    while ( o.tagName!='TR' ) o=o.parentElement;
    o.runtimeStyle.backgroundColor="#DAE6E9";
    parent.updateFrameById('preview','bouiToPreview',message);
}

function clickOnRowVarActivity(activityReceive, waitboui )
{        
   var o = window.event.srcElement;
   while ( o.tagName!='TR' ) o=o.parentElement;
   var isChecked = o.cells[1].firstChild.checked;
   
   var tbl = o;
   while ( tbl.tagName!='TABLE') tbl=tbl.parentElement;
   
   o.runtimeStyle.backgroundColor="#DAE6E9";
   tbl.rowActive = o.rowIndex;
   tbl.varChecked = isChecked;
   
   if ( isChecked )
   {
        var s = "<bo boui ='"+waitboui+"'><xwfWaitResponse__" + waitboui + "__receiveActivity>" + activityReceive +"</xwfWaitResponse__" + waitboui + "__receiveActivity></bo>";
        document.boFormSubmit.boFormSubmitXml.value = s;
   }
   else
   {
        var s = "<bo boui ='"+waitboui+"'><xwfWaitResponse__" + waitboui + "__receiveActivity></xwfWaitResponse__" + waitboui + "__receiveActivity></bo>";
        document.boFormSubmit.boFormSubmitXml.value = s;        
   }   
}

</script>

<head>
<style type="text/css">
@import url('ieThemes/0/wkfl/ui-wkfl.css');
</style>
</head>
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()"  >

    <table style='width:100%;height:100%' cellpadding=0 cellspacing=0>
        <tr height='26px'>            
            <TD class='gh_std'>&nbsp;&nbsp;Lista de Mensagens Recebidas </TD>
        </tr>
        <tr height='100%'>
            <td valign=top align=left style='width:50%'>
                <div style='width:100%;height:100%;overflow:auto'>
                    <table style='table-layout:fixed;' cellpadding=0 cellspacing=0>
                        <colgroup>
                        <col width=5/>
                        <col width=30/>
                        <col/>
                        </colgroup>
                    <% 
                        boObject receive = null;
                        boObject variable = null;
                        boObject value = null;
                        boObject valueObject = null;
                        while(activityReceiveBridge.next()) { 
                           receive = activityReceiveBridge.getObject();
                           if(receive != null)
                           {
                               variable = receive.getAttribute("message").getObject();
                               value = variable.getAttribute("value").getObject();
                               if(variable != null)
                               {                
                                    valueObject = value.getAttribute("valueObject").getObject(); %>
                                    <tr onclick="updateMessageFrame(<%=valueObject.getBoui()%>)" > 
                                        <td class='underline'>
                                        </td>
                                        <td class='underline'>
                                            <% if(receiveActivityBoui != -1 && receiveActivityBoui == receive.getBoui()){%>
                                                <input class='rad' type='radio' checked name='radiobutton' id='<%=receive.getBoui()%>'>
                                            <%} else {%>
                                                <input class='rad' type='radio'  onclick="clickOnRowVarActivity(<%=receive.getBoui()%>,<%=activityWaitBoui%>)" name='radiobutton' id='<%=receive.getBoui()%>'>
                                            <% } %>
                                        </td>
                                        <td class='underline'>
                                            <%= valueObject.getCARDID() %> 
                                        </td>
                                    </tr>                
                            <%}%>
                        <%}%>
                    <%}%>                      
                    </table>
                </div>
            </td>
        </tr>
    </table>

<script language="jscript">
function BuildXml()
{       
    var xmlQuery = document.boFormSubmit.boFormSubmitXml.value;        
    return xmlQuery;  
}
</script>
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
