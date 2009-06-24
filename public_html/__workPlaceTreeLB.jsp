<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__workPlaceTreeLB.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    String[] ctrl;
    int IDX;int cvui;
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);

    docHTML doc = DOCLIST.getDOC(IDX);
    
    
    String treeLBName=request.getParameter( "treeName" );
    long treeLBBoui=0;
    try
    {
        treeLBBoui = Long.parseLong(request.getParameter( "treeBoui" ));
    }
    catch(Exception e){/*Ignore*/}
    boObject treeLB = uiObjectManager.getUiObject( boctx, uiObjectManager.TYPE_TREELB , treeLBBoui );
    
    
%>

<html>
<head>
<title></title>
<style>
 @import url('ieThemes/0/global/ui-global.css');
 @import url('ieThemes/0/menuTree/ui-menuTree.css');
</style>

 <script LANGUAGE="javascript" SRC="ieLibrary/menuTree/runtime_elements.js"></script>
 <script LANGUAGE="javascript" SRC="ieLibrary/tree/tree_core.js"></script>
</head>


</head>
<script>

TREE_TEMA_DIR = 'ieThemes/0/tree/';
WKFL_TEMA_DIR = 'ieThemes/0/menuTree/';
TREEROW_COLOR_SEL ="transparent";

document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}

var xmlt=new Array();
var i=0;


var TREE_EDIT=null;	
var xmlSrc=null;	
function initTree( treeiD )
{
 var e = document.getElementById(treeiD);
 xmlSrc = new ActiveXObject("Microsoft.XMLDOM");
 xmlSrc.async	= false;
 xmlSrc.loadXML(xSrc.xml);
 TREE_EDIT = new xwTree( e , xmlSrc , false , false,false, "execute"  );
 window.setTimeout('refreshTreeCounters("TREE_EDIT")',15)
 window.setInterval('refreshTreeCounters("TREE_EDIT")',600000)
}
	
	
</script>


<body scroll="no" onload="initTree('t2')">
<xml id='xSrc'>
<%=treeLB.getAttribute("xmlTree").getValueString()%>
</xml>
<div class="treeContainer" style='height:100%;width:100%;overflow:auto;'>
<table id=t2   cellSpacing="0" cellPadding="0" >
	<colgroup>
	<col WIDTH="100%" />
   </tbody>
 </table>
</div>


</body>
</html>


<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
