<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
String lastEntriesBoql = null;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__gesdocclf_insert.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
int IDX;int cvui;
String idbolist;
String[] ctrl;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);





String document_boui=request.getParameter("document_boui"); 
Enumeration enum = request.getParameterNames();
 StringBuffer url = new StringBuffer("");
 boolean first = true;
 while( enum.hasMoreElements() )
 {
    String pname = enum.nextElement().toString();
    if(first)
    {
        url.append("?").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
        first = false;
    }
    else
    {
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
    }
 }

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Classificação</title>


<%@ include file='boheaders.jsp'%>
<script>
var objLabel='Classificação';

function showAddClassification()
{
    if( document.getElementById('addClassification') == null )
    {
        var posX = event.x;
        var div=document.createElement("IFRAME");
        div.id ='addClassification';
        div.style.zIndex = 1000;
        div.style.height="343px"
        div.style.width="450px"
        div.style.position="absolute";
        div.style.top = "30px";
        div.style.left = "0px";
        div.src = "__gesdocclf_dialog.jsp<%=url%>&id=AddClf&title=Classificacoes&btnOkLabel=Adicionar";
        document.body.appendChild( div  );
        
        div.style.left = ( posX - div.offsetWidth )        

/*
        var posX = event.x;
        var div=document.createElement("DIV");
        div.id ='addClassification';
        var x = new ActiveXObject("Microsoft.XMLHTTP");
        x.onreadystatechange = function(){
            if (x.readyState == 4 && x.status == 200) {
                if (x.responseText)
                {
                    div.innerHTML = x.responseText;
                    var dstyle = div.firstChild;
                    dstyle.style.left = ( posX - dstyle.offsetWidth )
                    document.getElementById("contentFrameAddClf").src="__gesdocclf_insertframe.jsp<%=url%>"
                }
            }
        };
        document.body.appendChild( div  );
        x.open("GET","__gesdocclf_dialog.jsp?id=AddClf&title=Classificacoes&btnOkLabel=Adicionar",true);
        x.send();
    }
    else
    {
        alert('Already Open');
    }
*/
    }
}

function closeAddClassification( id )
{
    if( id == "AddClf" )
    {
        document.body.removeChild( document.getElementById('addClassification'));
    }
    if( id == "ShowClf" )
    {
        document.body.removeChild( document.getElementById('showClassification'));
    }
    if( id == "EditClf" )
    {
        document.body.removeChild( document.getElementById('editClassification'));
    }
    
}

function refreshShowClassification()
{
     document.getElementById('editClassification').location-reload();
}

function closeEditClassification()
{
    
    document.body.removeChild( document.getElementById('editClassification'));
    if( document.frames["contentFrameShowClf"] )
    {
        document.frames["contentFrameShowClf"].location.reload();
    }
}

function showClassification()
{
    if( document.getElementById('showClassification') == null )
    {
        var posX = event.x; 
        var div=document.createElement("IFRAME");
        div.id ='showClassification';
        div.name=div.id;
        div.style.zIndex = 1000;
        div.style.height="343px"
        div.style.width="450px"
        div.style.position="absolute";
        div.style.top = "30px";
        div.style.left = "0px";
        div.src = "__gesdocclf_dialog.jsp?docid=<%=IDX%>&document_boui=<%=document_boui%>&id=ShowClf&title=Classificacoes&btnOkLabel=Editar";
        document.body.appendChild( div  );
        
        div.style.left = ( posX - div.offsetWidth )        
        
/*
        var x = new ActiveXObject("Microsoft.XMLHTTP");
        x.onreadystatechange = function(){
            if (x.readyState == 4 && x.status == 200) {
                if (x.responseText)
                {
                    div.innerHTML = x.responseText;
                    var dstyle = div.firstChild;
                    dstyle.style.left = ( posX - dstyle.offsetWidth )
                    document.getElementById("contentFrameShowClf").src="<%="__gesdocclf_bridge.jsp?docid="+DOC.getDocIdx()+"&method=list&parent_boui="+document_boui+"&document_boui="+document_boui+"&checkedbouis=&boql=select GESDocRunClf where 1=0"%>"
                }
            }
        };
        document.body.appendChild( div  );
        x.open("GET","__gesdocclf_dialog.jsp?id=ShowClf&title=Classificacoes&btnOkLabel=Editar",true);
        x.send();
*/        
    }
}

function showEditClassification( checkedBouis, posX )
{
    if( document.getElementById('editClassification') == null )
    {
        var div=document.createElement("IFRAME");
        div.id ='editClassification';
        div.name=div.id;
        div.style.zIndex = 1000;
        div.style.height="343px"
        div.style.width="450px"
        div.style.position="absolute";
        div.style.top = "30px";
        div.style.left = "0px";
        div.src = "__gesdocclf_dialog.jsp<%=url%>&id=EditClf&title=Classificacoes&btnOkLabel=Modificar&bouisChecked="+checkedBouis+"&bouiEditar="+checkedBouis;
        document.body.appendChild( div  );
        
        div.style.left = ( posX - div.offsetWidth )        

/*
        var x = new ActiveXObject("Microsoft.XMLHTTP");
        x.onreadystatechange = function(){
            if (x.readyState == 4 && x.status == 200) {
                if (x.responseText)
                {
                    div.innerHTML = x.responseText;
                    var dstyle = div.firstChild;
                    dstyle.style.left = ( posX - dstyle.offsetWidth )
                    document.getElementById("contentFrameEditClf").src="__gesdocclf_insertframe.jsp<%=url%>&operation=editar&bouisChecked="+checkedBouis+"&bouiEditar="+checkedBouis;
                    activeDialog('EditClf');
                }
            }
        };
        document.body.appendChild( div  );
        x.open("GET","__gesdocclf_dialog.jsp?id=EditClf&title=Classificacoes&btnOkLabel=Modificar",true);
        x.send();
*/       
    }
}


function ClickBtnOkShowClf()
{
    if(checkedBouis && checkedBouis.length > 0)
    {
        showEditClassification();
    }
    else
    {
        alert("Seleccione a classificação que quer editar");
    }
}

function ClickBtnOkEditClf()
{   
    document.frames['contentFrameEditClf'].window.mouseDown(event,'adicionar');
}

function ClickBtnOkAddClf()
{
    document.frames['contentFrameAddClf'].window.mouseDown(event,'adicionar');
}

var moveState = new Object();
function dialogMouseDown( id )
{
    var oSrcElement = event.srcElement;
    
    if( !moveState[ id ] ) 
    {
        moveState[ id ] = new Object();
        var oSrcDialog = moveState[ id ];
        oSrcDialog.moving = true;
        oSrcDialog.lastX = event.x;
        oSrcDialog.lastY = event.y;
        oSrcDialog.srcElement = getTrHeader(oSrcElement);
        oSrcDialog.srcElement.setCapture(true);
    }
    
}

function getTrHeader( srcElement )
{
    while( srcElement.id != 'TrHeader' && srcElement.parentElement != null )
    {
        srcElement =  srcElement.parentElement;
    }
    return srcElement;
}

function dialogMouseUp( id )
{
    var oSrcDialog = moveState[ id ];
    if( oSrcDialog )
    {
        oSrcDialog.srcElement.releaseCapture();
    }
    moveState[ id ] = null;
    
}


var activateDialog = null;
function activeDialog( id )
{
    if ( activateDialog != null )
    {
        activateDialog.style.zIndex = 10020;
    }
    document.getElementById("dDialog"+id).style.zIndex = 10021;
    activateDialog = document.getElementById("dDialog"+id);
}

function dialogMouseMove( id )
{
    var oSrcElement = event.srcElement;
    if( moveState[ id ] )
    {
        var oSrcDialog = moveState[ id ];
        
        var difX = event.x - oSrcDialog.lastX;
        var difY = event.y - oSrcDialog.lastY;

        var dialogDiv = oSrcDialog.srcElement.parentElement.parentElement.parentElement.parentElement;
        
        if( dialogDiv.style.top == "" ) dialogDiv.style.top = "0"
        if( dialogDiv.style.left == "" ) dialogDiv.style.left = "0"

        var newY = Math.max(0,parseInt(dialogDiv.style.top) + difY);
        var newX = Math.max(0,parseInt(dialogDiv.style.left) + difX);
        
        dialogDiv.style.top = newY;
        dialogDiv.style.left = newX;

        oSrcDialog.lastX = event.x;
        oSrcDialog.lastY = event.y;
        
    }
}


</script>
<style type="text/css">
@import url('ieLibrary/splitter/ui-splitter.css');
@import url('ieLibrary/splitter/splitter.css');
</style>
<STYLE>
/* Rule 805 of embedded stylesheet */ 

.dDialogCover {

	Z-INDEX: 10010;

	LEFT: 0px;

	OVERFLOW: hidden;

	WIDTH: 100%;

	POSITION: absolute;

	TOP: 0px;

	HEIGHT: 100%;

	-moz-user-focus: ignore

}



/* Rule 850 of embedded stylesheet */ 

.dDialogCover {

	Z-INDEX: 10010;

	LEFT: 0px;

	OVERFLOW: hidden;

	WIDTH: 100%;

	POSITION: absolute;

	TOP: 0px;

	HEIGHT: 100%;

	-moz-user-focus: ignore

}



/* Rule 1 of embedded stylesheet */ 

* {

	box-sizing: border-box;

	-moz-box-sizing: border-box;

	-khtml-box-sizing: border-box;

	-webkit-box-sizing: border-box

}



/* Rule 3 of embedded stylesheet */ 

BODY {

	PADDING-RIGHT: 0px;

	PADDING-LEFT: 0px;

	PADDING-BOTTOM: 0px;

	OVERFLOW: hidden;

	PADDING-TOP: 0px;

	HEIGHT: 100%;

	BACKGROUND-COLOR: #f4f5eb

}



/* Rule 9 of embedded stylesheet */ 

.appBody {

	BORDER-RIGHT: 0px solid;

	MARGIN: 0px;

	OVERFLOW: hidden;

	BORDER-LEFT: 0px solid;

	BORDER-BOTTOM: 0px solid

}



/* Rule 1 of embedded stylesheet */ 

BODY {

	BACKGROUND-COLOR: #feeef4

}



/* Rule 4 of embedded stylesheet */ 

HTML {

	OVERFLOW: hidden

}



/* Rule 807 of embedded stylesheet */ 

.dDialog {
	Z-INDEX: 10020;
	OVERFLOW: visible;
	CURSOR: default;
	POSITION: absolute;
}



/* Rule 845 of embedded stylesheet */ 

.dDialog {

	FONT-SIZE: 11px;

	FONT-FAMILY: tahoma, helvetica, arial, sans-serif

}



/* Rule 810 of embedded stylesheet */ 

.dDialog .shadow {

	Z-INDEX: -10;

	LEFT: -4px;

	WIDTH: 102%;

	POSITION: absolute;

	TOP: 3px;

	HEIGHT: 101%

}



/* Rule 811 of embedded stylesheet */ 

.ie .dDialog .shadow {

	FILTER: progid:DXImageTransform.Microsoft.AlphaImageLoader(src="http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/afb258a78efa8efc9b5583e083c2220b_1.png", sizingMethod="scale");

	WIDTH: 104%;

	HEIGHT: 101%

}



/* Rule 1603 of embedded stylesheet */ 

.shadow {

	PADDING-RIGHT: 0px;

	PADDING-LEFT: 0px;

	PADDING-BOTTOM: 0px;

	PADDING-TOP: 0px;

	BACKGROUND-COLOR: transparent

}



/* Rule 808 of embedded stylesheet */ 

.dDialog .frame1 {

	

}



/* Rule 5 of embedded stylesheet */ 

TABLE {

	

}



/* Rule 809 of embedded stylesheet */ 

.dDialog .frame2 {

	

}



/* Rule 840 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterRight {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/d092529c49e229680f4e80c5a603857f_1.gif) no-repeat -1710px 0px;

	WIDTH: 3px;

	HEIGHT: 3px

}



/* Rule 846 of embedded stylesheet */ 

.dDialog TD {

	FONT-SIZE: 11px;

	FONT-FAMILY: tahoma, helvetica, arial, sans-serif

}



/* Rule 120 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterRight {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/9b019f9fc34ecb7acad0e74d55922886_1.gif) no-repeat -1710px 0px

}



/* Rule 839 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterCenter {

	FONT-SIZE: 0px;

	BORDER-LEFT-COLOR: #b3b6b0;

	BORDER-TOP-COLOR: #b3b6b0;

	BORDER-BOTTOM: #b3b6b0 1px solid;

	HEIGHT: 3px;

	BACKGROUND-COLOR: #f4f5eb;

	BORDER-RIGHT-COLOR: #b3b6b0

}



/* Rule 118 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterCenter {

	BORDER-LEFT-COLOR: #e6b5b5;

	BORDER-TOP-COLOR: #e6b5b5;

	BORDER-BOTTOM: #e6b5b5 1px solid;

	BACKGROUND-COLOR: #ffedf4;

	BORDER-RIGHT-COLOR: #e6b5b5

}



/* Rule 838 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterLeft {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/d092529c49e229680f4e80c5a603857f_1.gif) no-repeat -1740px 0px;

	WIDTH: 3px;

	HEIGHT: 3px

}



/* Rule 117 of embedded stylesheet */ 

.dDialog TFOOT .dialogFooterLeft {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/9b019f9fc34ecb7acad0e74d55922886_1.gif) no-repeat -1740px 0px

}



/* Rule 834 of embedded stylesheet */ 

.dDialog TFOOT .dialogRight {

	BORDER-RIGHT: #b3b6b0 1px solid;

	BORDER-LEFT-COLOR: #b3b6b0;

	BORDER-BOTTOM-COLOR: #b3b6b0;

	WIDTH: 3px;

	BORDER-TOP-COLOR: #b3b6b0;

	BACKGROUND-COLOR: #f4f5eb

}



/* Rule 111 of embedded stylesheet */ 

.dDialog TFOOT .dialogRight {

	BORDER-RIGHT: #e6b5b5 1px solid;

	BORDER-LEFT-COLOR: #e6b5b5;

	BORDER-BOTTOM-COLOR: #e6b5b5;

	BORDER-TOP-COLOR: #e6b5b5;

	BACKGROUND-COLOR: #ffedf4

}



/* Rule 830 of embedded stylesheet */ 

.dDialog TFOOT .content {

	PADDING-RIGHT: 8px;

	PADDING-LEFT: 8px;

	PADDING-BOTTOM: 8px;

	PADDING-TOP: 8px;

	BACKGROUND-COLOR: #f4f5eb

}



/* Rule 379 of embedded stylesheet */ 

.dDialog TFOOT .content {

	BACKGROUND-COLOR: #ffedf4

}



/* Rule 841 of embedded stylesheet */ 

.dDialog .buttons {

	PADDING-RIGHT: 3px;

	WHITE-SPACE: nowrap;

	TEXT-ALIGN: right

}



/* Rule 842 of embedded stylesheet */ 

.dDialog .buttons BUTTON {

	MARGIN-LEFT: 0.7em

}



/* Rule 843 of embedded stylesheet */ 

.ie .dDialog .buttons BUTTON {

	OVERFLOW: visible;

	WIDTH: 85px

}



/* Rule 849 of embedded stylesheet */ 

.dDialog BUTTON {

	PADDING-RIGHT: 0.5em;

	PADDING-LEFT: 0.5em;

	FONT-SIZE: 11px;

	PADDING-BOTTOM: 0.2em;

	PADDING-TOP: 0.2em

}



/* Rule 844 of embedded stylesheet */ 

.dDialog .buttons BUTTON .btnContent 
{
	MIN-WIDTH: 70px
}



/* Rule 832 of embedded stylesheet */ 

.dDialog TFOOT .dialogLeft {

	BORDER-BOTTOM-COLOR: #b3b6b0;

	BORDER-LEFT: #b3b6b0 1px solid;

	WIDTH: 3px;

	BORDER-TOP-COLOR: #b3b6b0;

	BACKGROUND-COLOR: #f4f5eb;

	BORDER-RIGHT-COLOR: #b3b6b0

}



/* Rule 109 of embedded stylesheet */ 

.dDialog TFOOT .dialogLeft {

	BORDER-BOTTOM-COLOR: #e6b5b5;

	BORDER-LEFT: #e6b5b5 1px solid;

	BORDER-TOP-COLOR: #e6b5b5;

	BACKGROUND-COLOR: #ffedf4;

	BORDER-RIGHT-COLOR: #e6b5b5

}



/* Rule 827 of embedded stylesheet */ 

.dDialog TBODY {

	BACKGROUND-COLOR: #f4f5eb

}



/* Rule 108 of embedded stylesheet */ 

.dDialog TBODY {

	BACKGROUND-COLOR: #ffedf4

}



/* Rule 835 of embedded stylesheet */ 

.dDialog TBODY .dialogRight {

	BORDER-RIGHT: #b3b6b0 1px solid;

	BORDER-LEFT-COLOR: #b3b6b0;

	BORDER-BOTTOM-COLOR: #b3b6b0;

	WIDTH: 3px;

	BORDER-TOP-COLOR: #b3b6b0;

	BACKGROUND-COLOR: #f4f5eb

}



/* Rule 112 of embedded stylesheet */ 

.dDialog TBODY .dialogRight {

	BORDER-RIGHT: #e6b5b5 1px solid;

	BORDER-LEFT-COLOR: #e6b5b5;

	BORDER-BOTTOM-COLOR: #e6b5b5;

	BORDER-TOP-COLOR: #e6b5b5;

	BACKGROUND-COLOR: #ffedf4

}



/* Rule 828 of embedded stylesheet */ 

.dDialog TBODY .content {

	PADDING-RIGHT: 1.7em;

	PADDING-LEFT: 1.7em;

	PADDING-BOTTOM: 0em;

	PADDING-TOP: 0em

}



/* Rule 848 of embedded stylesheet */ 

.dDialog SELECT {

	FONT-SIZE: 11px;

	FONT-FAMILY: tahoma, helvetica, arial, sans-serif

}



/* Rule 833 of embedded stylesheet */ 

.dDialog TBODY .dialogLeft {

	BORDER-BOTTOM-COLOR: #b3b6b0;

	BORDER-LEFT: #b3b6b0 1px solid;

	WIDTH: 3px;

	BORDER-TOP-COLOR: #b3b6b0;

	BACKGROUND-COLOR: #f4f5eb;

	BORDER-RIGHT-COLOR: #b3b6b0

}



/* Rule 110 of embedded stylesheet */ 

.dDialog TBODY .dialogLeft {

	BORDER-BOTTOM-COLOR: #e6b5b5;

	BORDER-LEFT: #e6b5b5 1px solid;

	BORDER-TOP-COLOR: #e6b5b5;

	BACKGROUND-COLOR: #ffedf4;

	BORDER-RIGHT-COLOR: #e6b5b5

}



/* Rule 816 of embedded stylesheet */ 

.dDialog THEAD .dialogRight {

	FONT-SIZE: 0px;

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/d092529c49e229680f4e80c5a603857f_1.gif) no-repeat -1380px 0px;

	OVERFLOW: hidden;

	WIDTH: 3px;

	HEIGHT: 25px

}



/* Rule 114 of embedded stylesheet */ 

.dDialog THEAD .dialogRight {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/9b019f9fc34ecb7acad0e74d55922886_1.gif) no-repeat -1380px 0px

}



/* Rule 817 of embedded stylesheet */ 

.dDialog THEAD .closeBtnContainer 
{
	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/8799673c9c17a91b6e021acc6a66ccf9_1.gif) repeat-x 0px -1100px;
	WIDTH: 16px;
	POSITION: relative;
	TEXT-ALIGN: right
}



/* Rule 116 of embedded stylesheet */ 

.dDialog THEAD .closeBtnContainer {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/3c47cf570492c559d13b9db991b038e6_1.gif) repeat-x 0px -1100px

}



/* Rule 819 of embedded stylesheet */ 

.dDialog THEAD .close {

	BACKGROUND-POSITION: -732px 0px;

	RIGHT: 5px;

	BACKGROUND-IMAGE: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/5d4b4e342873c997922af24cd7602843_1.gif);

	WIDTH: 16px;

	CURSOR: hand;

	BACKGROUND-REPEAT: no-repeat;

	POSITION: absolute;

	TOP: 5px;

	HEIGHT: 16px

}



/* Rule 821 of embedded stylesheet */ 

.ie .dDialog THEAD .close {

	RIGHT: 1px

}



/* Rule 166 of embedded stylesheet */ 

.dDialog THEAD .close {

	BACKGROUND-IMAGE: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/c3d4ad52625c81c862de13f257c48c0d_1.gif)

}



/* Rule 813 of embedded stylesheet */ 

.dDialog THEAD .content {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/8799673c9c17a91b6e021acc6a66ccf9_1.gif) repeat-x 0px -1100px;

	PADDING-BOTTOM: 0px;

	CURSOR: move;

	PADDING-TOP: 1px;

	WHITE-SPACE: nowrap;

	HEIGHT: 25px

}



/* Rule 115 of embedded stylesheet */ 

.dDialog THEAD .content {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/3c47cf570492c559d13b9db991b038e6_1.gif) repeat-x 0px -1100px

}



/* Rule 831 of embedded stylesheet */ 

.dDialog THEAD H1 {

	PADDING-RIGHT: 6px;

	PADDING-LEFT: 6px;

	FONT-WEIGHT: bold;

	FONT-SIZE: 12px;

	PADDING-BOTTOM: 4px;

	MARGIN: 0px;

	COLOR: white;

	PADDING-TOP: 4px

}



/* Rule 815 of embedded stylesheet */ 

.dDialog THEAD .dialogLeft {

	FONT-SIZE: 0px;

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/d092529c49e229680f4e80c5a603857f_1.gif) no-repeat -1350px 0px;

	OVERFLOW: hidden;

	WIDTH: 3px;

	HEIGHT: 25px

}



/* Rule 113 of embedded stylesheet */ 

.dDialog THEAD .dialogLeft {

	BACKGROUND: url(http://l.yimg.com/us.yimg.com/i/us/pim/dclient/g/img/md5/9b019f9fc34ecb7acad0e74d55922886_1.gif) no-repeat -1350px 0px

}


</STYLE>
<body ondragenter="activethis()">
<% if(!(request.getParameter("toClose") != null && !DOC.haveErrors())) {%>
<table AQUI=1 cellSpacing="0" cellPadding="0" style="height:100%;width:100%">
    <TR id='classificationEdit' name='classificationEdit' style='height:25px;width:100%' >
        <td align="right" style="color:white" bgcolor="#89c681">
            <span style="cursor:hand" onclick="showAddClassification();" >
            <b>Nova Classificação </b><img align="middle" src="images/ui_om_expand.gif">
            </span>
            <span style="cursor:hand" onclick="showClassification();" >
            <b>Classificações </b><img align="middle" src="images/ui_om_expand.gif">
            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
            </div> 
        </td>
    </tr>
    <tr id=\"trResizeBottom\" height="1px">
        <td colspan='1'><img class="rszUp ui-rszUp" src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1px" HEIGHT="4px"></td>
    </tr>
    <tr id='docPreview' name='docPreview' height="100%" cellpadding="0" cellspacing="0" cellspacing="0">
      <td>
        <table style="width:100%;height:100%;background-color:#FEF8E0" cellpadding="0" cellspacing="0" cellspacing="0">
            <tr height="100%"> 
                <TD width="100%"> 
                    <iframe scrolling=no frameborder="0" marginheight="0" marginwidth="0" width="100%" height="100%" src="__gesdocclf_previewdoc.jsp?autoConvert=false&docBoui=<%=document_boui%>&docid=<%=request.getParameter("docid")%>"></iframe>
                </TD>
            </TR>
        </table>
      </td>
    <tr>
</table>
<%}%>
</body>
</html>
<% 
} finally {
boctx.close();DOCLIST.releseObjects(boctx);
}%>
