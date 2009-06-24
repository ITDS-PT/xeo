<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.Browser"%>

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
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    if(request.getParameter("objectBoui")!=null)
    {
        BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
        long[] a_boui = {BOI.getBoui()};
        currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
        if(!currObjectList.haveBoui(a_boui[0]))
        {
            currObjectList.inserRow(a_boui[0]);
        }
    }      
    String previewBoui =  request.getParameter("previewBoui");
    String frameId =  request.getParameter("frameID");
    boObject activity = DOC.getController().getObject(ClassUtils.convertToLong(previewBoui));
    
    String question = activity.getAttribute("question").getValueString();
    String answer = activity.getAttribute("answer").getValueString();
    
    StringBuffer result = new StringBuffer();
    boObject option = null;
    bridgeHandler bridge = activity.getBridge("options");
    bridge.beforeFirst();
    while(bridge.next())
    {
        option = bridge.getObject();
        result.append("<p>").append(option.getAttribute("labelOption").getValueString());
        if(answer.equals(option.getAttribute("codeOption").getValueString()))
        {
            result.append("&nbsp;").append("V");
        }   
        result.append("</p>");
    }
    
    String cssURL = Browser.getThemeDir() + "report/report.css;";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
 <style type="text/css">
    @import url('<%=cssURL%>');
</style>
<script>
function init()
{
    <%
        if(frameId != null && frameId.length() > 0)
        {
    %>
        parent.document.getElementById('<%=frameId%>').style.height=container.offsetHeight+10;
    <%}%>
}
</script>
</head>
<body  onload="init();" >
    <div id='container' style='width:100%;padding:0px;'>
    <div class="reportInnerBody">
        <div class="area_title">
            <p><%=question%></p>
        </div>       
        <div class="tab_title">
            <%=result.toString()%>
        </div>
        
        <table class="section">
            <tr>
                <td class="label" width="12%">Justificação</td>                            
            </tr>                    
            <tr>
                <td width="100%" colspan="4" class="input">
                    <%=activity.getAttribute("justification").getValueString()%>
                </td>
            </tr>            
        </table>         
    </div>
    </div>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
