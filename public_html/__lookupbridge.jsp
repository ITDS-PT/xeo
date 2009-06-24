<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
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

boDefHandler bodef;
boDefAttribute atr;
String idbolist;
String[] ctrl;
//docParameter attr;
ctrl= DOCLIST.processRequest(boctx);
IDX = ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);

    // leitura de parametros
    // especificos do Lookup
    
String look_object          = request.getParameter("look_object");
String showNew              = request.getParameter("showNew");
String look_parentObj       = request.getParameter("look_parentObj");
String look_parentBoui      = request.getParameter("look_parentBoui");
String bolist_query         = request.getParameter("bolist_query");
String look_parentAttribute = request.getParameter("look_parentAttribute");
String clientIDX            = request.getParameter("clientIDX");
String look_action          = request.getParameter("look_action");

String renderOnlyCardID     = request.getParameter("renderOnlyCardID");

String addTo_parentObj       = request.getParameter("addTo_parentObj");
String addTo_parentBoui      = request.getParameter("addTo_parentBoui");
String addTo_parentAttribute = request.getParameter("addTo_parentAttribute");
String canSelectRows         = request.getParameter("canSelectRows");

  
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%><script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search24.gif"><%=boDefHandler.getBoDefinition(look_parentObj).getAttributeRef(look_parentAttribute).getLabel()%> ';var objDescription='';</script>
<body>
   <%   
    
            boDefHandler objdef=boDefHandler.getBoDefinition(look_parentObj);
                
            String __url = objdef.getBoName()+"_"+look_parentAttribute;
            
            __url+="_general"+"list.jsp?";
            __url=__url.toLowerCase();
            
            __url+="docid="+IDX;
            __url+="&showSelectNone=no";
            __url+="&menu=no";
            __url+="&method=list";
            __url+="&parent_boui="+look_parentBoui;
            __url+="&&parent_attribute="+look_parentAttribute;
            __url+="&listmode=searchone";
                        
            if  (renderOnlyCardID!=null)__url+="&renderOnlyCardID="+renderOnlyCardID; 
            if (bolist_query!=null) __url+="&bolist_query="+java.net.URLEncoder.encode( bolist_query ,"UTF-8");
            if (canSelectRows!=null) __url+="&canSelectRows="+canSelectRows;
            
            //__url=__url.toLowerCase();
            __url+="&showLines=no";
       %>
 
    <IFRAME id=findframe src='<%=__url%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>


    <FORM name='boFormSubmit' action="<%if ( look_action==null ){ %>lookupmultiupdate.jsp <%} else {%><%=look_action%><%}%>" method='get'>
        <INPUT type='hidden' name='boFormSubmitXml' />
        <INPUT type='hidden' name='look_parentObj' value='<%=addTo_parentObj%>' />
        <INPUT type='hidden' name='look_parentBoui' value='<%=addTo_parentBoui%>' />
        <INPUT type='hidden' name='look_parentAttribute' value='<%=addTo_parentAttribute%>' />
        <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
        <INPUT type='hidden' name='clientIDXtoClose' />
        <INPUT type='hidden' name='boFormLookup' />
        <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
        <INPUT type='hidden' name='boFormSubmitId' />
        <INPUT type='hidden' value='<%=IDX%>' name='docid' />
        <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    </FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

