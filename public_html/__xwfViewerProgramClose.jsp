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

    String toPrint = null;    
    if("close".equals(controller.getProgramState()))
    {
        toPrint = "O fluxo de trabalho encontra-se concluido.";
    }
    else
    {
        toPrint = "Não existem tarefas neste momento.";        
    }    
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="<%=controller.getRuntimeProgram().getCARDID()%>";
</script>
<script>
function xxclose()
{
    try
    {
        if(parent != null && parent.parent != null)
        {
            parent.parent.ndl[parent.getIDX()].close();
        }
        else
        {
            parent.ndl[getIDX()].close();
        }
    }catch(e){}
    
}
</script>
<%@ include file='boheaders2.jsp'%>
<body onload="xxclose();" ondragenter="activethis();"  >
<div  style='height:100%;overflow:auto'>
<form class="objectForm" name="boForm" id="<%=IDX%>" >

<TABLE class="layout" style='height:100%;width:100%;' cellspacing="0" cellpadding="0" style='background-color:#FFFFFF' >
    <tr>
        <td  align=center>
            <h4><%=toPrint.toString()%></h4>
        </td>
    </tr>
</table>
    
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
        options.put(BasicPresentation.OPTION_FORCE_NO_CHANGE,"true");
    %>    
    
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>      
</FORM> 
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
