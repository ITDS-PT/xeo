<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>
<%@ page import="org.w3c.dom.*"%>

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
    
    
%>

<html>
<head>
<STYLE>
.message
{
    FONT-WEIGHT: bold;
    COLOR: #000000;
    TEXT-DECORATION: none;    
}
.procedureLink
{
    letter-spacing:-0.1mm;
    COLOR: #3A6F81;
    font-size:12px;
    font-weight:normal;
    TEXT-DECORATION: none;
    CURSOR:Hand;
}
.procedureLink:hover
{
    
    COLOR: #da0303;
    TEXT-DECORATION: underline;
    CURSOR:Hand;
}
</STYLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="&nbsp;&nbsp;Menu do fluxo de trabalho";  
</script>
<%= DOC.getController().getPresentation().writeCSS() %>
</head>   
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()" style='background-color:#de8e29' >

<form style='height:100%;' class="objectForm" name="boForm" id="<%=IDX%>">

   
    <br>    
    <TABLE style="table-layout:fixed;margin:20px;width:100%" cellspacing="0" cellpadding="0"  >
            <colgroup>
            <col width="30px">
            <col width="100%">
            </colgroup>
            <TR >
            <TD valign="middle" colspan="2">   
                <%if(controller.getRuntimeActivity() == null){ %>
                    &nbsp;&nbsp;&nbsp;&nbsp;Não existem tarefas, mas tem as seguintes opções disponíveis :
                <%} else {%>
                    &nbsp;&nbsp;&nbsp;&nbsp;Tem as seguintes opções disponíveis :
                <%}%>
            </TD>
            </TR>
            <TR style="height:30px">
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>     
                        

            <% 
                String name = null;
                String label = null;                
                Node node = null;
                ngtXMLHandler nx = null;
                java.util.ArrayList list = xwfHelper.getProcedures(controller.getEngine());
                if(list != null)
                {                
                    for(int i=0; i < list.size(); i++)
                    {
                        nx = (ngtXMLHandler)list.get(i);
                        name = nx.getAttribute("name");   
                        label = nx.getChildNode("label").getCDataText(); 
            %>
            <TR >
               <TD width="18" valign="top" >
                <img src="ieThemes/0/wkfl/ponto_content.gif" width="18" height="20" border="0">
               </TD>
               <TD valign="top">     
                    <a href="#" class="procedureLink" onmouseover="window.status='';return true;">
                    <span onclick="parent.setActionCode('<%=XwfKeys.ACTION_PROCEDURE+"["+name+"]"%>');parent.boForm.BindValues();parent.wait();"  title="<%=label%>">
                    <%=label%>                       
                    </span>
                    </a>
               </TD>
            </TR>
            <TR>
               <TD width="18" valign="top" >                
               </TD>
               <TD valign="top">   
                    &nbsp;&nbsp;        
                    <% if ( netgest.utils.ClassUtils.convertToInt(nx.getAttribute("count"),0) == 0 ) {%>
                    Ainda não foi executado  
                    <%}else{%>
                    Número de execuções <span style="color:red">(<b><%=nx.getAttribute("count")%></b>)</span>
                    <%}%>
                    <hr style='border:1px solid #EEEEEE'>
               </TD>
            </TR>   
            <TR>
            <TD valign="top" >&nbsp;</TD>            
            </TR> 
            
            <% 
                    } 
                 }
            %>        

            <TR height="100%" >
            </TR>                             
      </TABLE>          
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
