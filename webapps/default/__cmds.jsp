<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
if ( request.getParameter("docid")!= null )
{
boolean initPage=true;
EboContext boctx=null;

String cmd=null;
String cmd_id=null;
StringBuffer result=new StringBuffer();

try {
    String errors = null;
    
    try 
    {
    
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
    
        int IDX;
        int cvui;
        boDefHandler bodef;
        boDefAttribute atr;
        String idbolist;
        String[] ctrl;
        
            ctrl    = DOCLIST.processRequest(boctx);
            IDX     = ClassUtils.convertToInt(ctrl[0],-1);
            idbolist=ctrl[1];
            docHTML DOC = DOCLIST.getDOC(IDX);
            
            cmd= request.getParameter("cmd");
            cmd_id= request.getParameter("cmd_id");
            if ( cmd!=null && cmd.equalsIgnoreCase("GETCARDIDWLINK") )
            {
                String xparameters=request.getParameter("bouis");
                String[] bouis=xparameters.split(";");
                for (int i = 0; i < bouis.length ; i++) 
                {
                    boObject o=DOC.getObject( ClassUtils.convertToLong( bouis[i] ) );
                    result.append("<result><![CDATA[");
                    result.append( o.getCARDIDwLink() );
                    result.append("]]></result>");
                } 
            }
            if( DOC.haveErrors() )
            {
                errors = DOC.getHTMLErrors();        
            }
        
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
<title></title>
<body>
 <%
 if( errors != null )
 {
 %>
 <script language="javascript" >
    newDialogBox("critical",errors,["Continuar" ]," Atenção, Ocorreu um erro!!!");
 </script>
<<%
 } 
 else
 {
 %>
    <xml id='results' cmd_id='<%=cmd_id%>'><results><%=result%></results></xml>
 <%
 }
 %>

 </body>
<%
} finally 
{
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
}
else
{%>
<html><head><title></title></head><body></body></html>
<%}%>