<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%


boolean initPage=false;
EboContext boctx=null;

String cmd=null;
String cmd_id=null;
StringBuffer result=new StringBuffer();
String BOUI_ = null;
int IDX = -1;  
try {
    String errors = null;
    Hashtable valTable = null;
    boolean totalRefresh = false;
    boolean parentRefresh = false;
    try 
    {
    
        if( request.getParameter("toprocess") != null )
        {
        initPage=true;
        String toprocess = request.getParameter("toprocess"); 
        
        boctx = (EboContext)request.getAttribute("a_EboContext");
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if(boctx==null) {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.setAttribute("a_EboContext",boctx);
        }
    
        int cvui;
        
        boDefHandler bodef;
        boDefAttribute atr;
        String idbolist;
        String[] ctrl;
        
        ctrl    = DOCLIST.processRequest(boctx);
        idbolist=ctrl[1];
        }
       
       
       // docHTML DOC = DOCLIST.getDOC(IDX);
    } 
    catch (Throwable e)
    {
        java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter( cw );
        e.printStackTrace( pw );
        pw.close();
        cw.close();
        
        
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<SCRIPT language=javascript src="bo_global.js"></SCRIPT>
<script>
function bindMenu(treeName, menuName, menuItem, val)
{
	var xml="<explorer name='";
	xml+=treeName + "' menuName='";
	xml+=menuName+"'>";
	xml+="<"+menuItem+">"+val+"</"+menuItem+">";
	xml+="</explorer>";
    boFormSubmit.boFormSubmitXml.value=xml;
	boFormSubmit.boFormSubmitMode.value = 15;
    boFormSubmit.submit();
    
}
</script>
<title></title>
<body>

<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    
    <INPUT type='hidden' name='boiChanged' value='true' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='1' name='toprocess' />
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
