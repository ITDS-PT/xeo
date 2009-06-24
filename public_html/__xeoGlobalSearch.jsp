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
<%@ page import="netgest.bo.controller.basic.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.localized.*"%>



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
  
 ctrl= DOCLIST.processRequest(boctx);
 IDX= ClassUtils.convertToInt(ctrl[0],-1);
 idbolist=ctrl[1];
 docHTML DOC = DOCLIST.getDOC(IDX);
 boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
        
    String searchString         = request.getParameter("searchString");   
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");
    }
    else
    {
        searchString="";
    }  
    String jspName = "__xeoGlobalSearchResults.jsp";
    StringBuffer jspResults = new StringBuffer();
    jspResults.append(jspName);
    jspResults.append("?docid=").append(IDX);
    jspResults.append("&method=").append("list");
    jspResults.append("&menu=").append("no");    
    jspResults.append("&canSelectRows=no");
    
    
 
%>
<html>
<head>
<style type="text/css">
@import url('ieThemes/0/wkfl/ui-wkfl.css');
</style>
<script>
var objLabel="&nbsp;&nbsp;Pesquisa Global";

function findObjects()
{
    xurl = '<%=jspResults%>';   
    findframe.location.href=xurl+"&searchString="+encodeURIComponent(document.getElementById('TEXTSEARCH').value);
}
</script>
</head>
<%@ include file='boheaders2.jsp'%>
<body scroll='no' ondragenter="activethis()" onload="TEXTSEARCH.focus()" >
    <table style='table-layout:fixed;width:100%;height:100%' cellpadding=0 cellspacing=0>
      <tr style='height:40px'>
          <td style='height:40px;width="100%"' valign="top">
          <table style='table-layout:fixed;width:100%'>
          <colgroup>
          <col width=100px>
          <col width=100%>
          <col width=100px>
          </colgroup>
          <tr>
          <td><span><%=JSPMessages.getString("GlobalSearch.1")%></span></td>
          <td><input onkeypress="if(window.event.keyCode==13){findframe.wait();findObjects()}" id='TEXTSEARCH' style='width:100%' type='text' value='<%=searchString%>' ></td>
          <td><button onclick="findframe.wait();findObjects()"><%=JSPMessages.getString("GlobalSearch.2")%></button></td>
          </tr>
          </table>
          <%/*
docHTML_section sec = DOC.createSection("listGoogle","",false,2);          
docHTML_sectionRow row = sec.addRow();
Hashtable xattributes=new Hashtable();
docHTML_sectionCell xcell=row.addCellMultiField();

 xcell.addField( 
            docHTML_sectionField.newText(
            new StringBuffer("TEXTSEARCH"),
            new StringBuffer("TEXTSEARCH"),
            new StringBuffer("Texto a Procurar"),
            new StringBuffer(searchString),null,null,new StringBuffer("findframe.wait();findObjects()") ),"100%",xattributes) ;


xcell.addField( docHTML_sectionField.newButton(
            new StringBuffer("find"),
            new StringBuffer("Procura"),
            new StringBuffer("findframe.wait();findObjects()"))
            ,"",xattributes
            ) ;
sec.render(pageContext,DOC,DOCLIST); 
*/
          %>
          
        </td>
     </tr>     
      <tr style='height:100%'>
          <td style='padding:2px'>
                    <IFRAME id=findframe src='<%=jspResults.toString()%>' frameBorder=0 width='100%' scrolling=no height='100%' >
                    </IFRAME> 
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
        options.put(BasicPresentation.OPTION_TYPE_FORM,"1");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());                 
    %>    
    <%= DOC.getController().getPresentation().writeJspFooter(null ,currObjectList,options,masterdoc,request) %>  
</FORM> 
</body>
</html>
<%
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
