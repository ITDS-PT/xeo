e<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Classificação</title>


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

<%
    String id = request.getParameter("id");
    String title = request.getParameter("title");
    String btnOkLabel = request.getParameter("btnOkLabel");

    //("__gesdocclf_bridge.jsp?docid="+DOC.getDocIdx()+"&method=list&parent_boui="+document_boui+"&document_boui="+document_boui+"&checkedbouis=&boql=select GESDocRunClf where 1=0")

    String frmUrl = "";
    if( "AddClf".equals( id ) )
    {
        frmUrl =   "__gesdocclf_insertframe.jsp"+url;
    }
    else if ( "EditClf".equals( id ) ) 
    {
        frmUrl =   "__gesdocclf_insertframe.jsp"+url+"&operation=editar";
    }
    else if ( "ShowClf".equals( id ) ) 
    {
        frmUrl = "__gesdocclf_bridge.jsp?docid="+DOC.getDocIdx()+"&method=list&parent_boui="+document_boui+"&document_boui="+document_boui+"&checkedbouis=&boql=select GESDocRunClf where 1=0";
    }
    
%>

<%@ include file='boheaders.jsp'%>
<script>
var objLabel='Classificação';
var dialogId = "<%=id%>"

function showAddClassification()
{
    if( document.getElementById('addClassification') == null )
    {
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
}

function closeAddClassification( id )
{
    if( id == "AddClf" )
    {
        var dialogDiv = window.parent.document.getElementById("addClassification");
        dialogDiv.document.body.removeChild( dialogDiv );
    }
    if( id == "ShowClf" )
    {
        var dialogDiv = window.parent.document.getElementById("showClassification");
        dialogDiv.document.body.removeChild( dialogDiv );
    }
    if( id == "EditClf" )
    {
        var dialogDiv = window.parent.document.getElementById("editClassification");
        dialogDiv.document.body.removeChild( dialogDiv );
    }
    
}

function closeEditClassification()
{
    var dialogDiv = window.parent.document.getElementById("showClassification");
    if( dialogDiv != null )
    {
        dialogDiv.contentWindow.location.reload();
    }
    closeAddClassification( dialogId );    
}

function ClickBtnOkShowClf()
{
    if(checkedBouis && checkedBouis.length > 0)
    {
        var dialogDiv = window.parent.document.getElementById("showClassification");
        window.parent.showEditClassification( checkedBouis, parseInt(dialogDiv.style.left) + dialogDiv.offsetWidth - 50 );
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

    var dialogDiv = window.parent.document.getElementById("showClassification");
    if( dialogDiv != null )
    {
        dialogDiv.contentWindow.location.reload();
    }
    
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
        if( id == "AddClf" )
        {
            dialogDiv = window.parent.document.getElementById("addClassification");
        }
        if( id == "ShowClf" )
        {
            dialogDiv = window.parent.document.getElementById("showClassification");
        }
        if( id == "EditClf" )
        {
            dialogDiv = window.parent.document.getElementById("editClassification");
        }

        oSrcDialog.lastX = event.x + parseInt(dialogDiv.style.left);
        oSrcDialog.lastY = event.y + parseInt(dialogDiv.style.top);

        dialogDiv.onmousemove = function() { dialogMouseMove( id ) };
        dialogDiv.onmouseup = function() { dialogMouseUp( id ) };
        oSrcDialog.srcElement = dialogDiv;
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
    var frmNames = new Array("addClassification","addClassification","editClassification");
    for (var i = 0; i < frmNames.length; i++) 
    {
        var dialogDiv = window.parent.document.getElementById( frmNames[i] );
        if( dialogDiv != null )
        {
            dialogDiv.style.zIndex = 0;
        }
    }

    var dialogDiv = null;
    if( id == "AddClf" )
    {
        dialogDiv = window.parent.document.getElementById("addClassification");
    }
    if( id == "ShowClf" )
    {
        dialogDiv = window.parent.document.getElementById("showClassification");
    }
    if( id == "EditClf" )
    {
        dialogDiv = window.parent.document.getElementById("editClassification");
    }
    
    dialogDiv.style.zIndex = 1;
    activateDialog = dialogDiv;
}

function dialogMouseMove( id )
{
    var event = window.parent.event;
    var oSrcElement = event.srcElement;
    if( moveState[ id ] )
    {
        var oSrcDialog = moveState[ id ];
        
        var difX = event.x - oSrcDialog.lastX;
        var difY = event.y - oSrcDialog.lastY;
        
        window.top.status = "X:" +event.x + "Y:" +event.y;

        if( id == "AddClf" )
        {
            dialogDiv = window.parent.document.getElementById("addClassification");
        }
        if( id == "ShowClf" )
        {
            dialogDiv = window.parent.document.getElementById("showClassification");
        }
        if( id == "EditClf" )
        {
            dialogDiv = window.parent.document.getElementById("editClassification");
        }
        //var dialogDiv = oSrcDialog.srcElement.parentElement.parentElement.parentElement.parentElement;
        //var dialogDiv = window.parent.document.getElementById("showClassification");
        
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
/* Rule 2 of embedded stylesheet */ 
* {
	box-sizing: border-box;
	-moz-box-sizing: border-box;
	-khtml-box-sizing: border-box;
	-webkit-box-sizing: border-box
}

/* Rule 807 of embedded stylesheet */ 
.dDialog {
	Z-INDEX: 10020;
	OVERFLOW: visible;
	CURSOR: default;
	POSITION: absolute
}

/* Rule 845 of embedded stylesheet */ 
.dDialog {
	FONT-SIZE: 11px;
	FONT-FAMILY: tahoma, helvetica, arial, sans-serif
}

/* Rule 1 of embedded stylesheet */ 
* {
	box-sizing: border-box;
	-moz-box-sizing: border-box;
	-khtml-box-sizing: border-box;
	-webkit-box-sizing: border-box
}

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
	BACKGROUND-COLOR: #fff9e6
}

/* Rule 4 of embedded stylesheet */ 
HTML {
	OVERFLOW: hidden
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
	BACKGROUND: url(GesdocClfDialog/images2.gif) no-repeat -1710px 0px;
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
	BACKGROUND: url(GesdocClfDialog/images1.gif) no-repeat -1710px 0px
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
	BORDER-LEFT-COLOR: #debd8e;
	BORDER-TOP-COLOR: #debd8e;
	BORDER-BOTTOM: #debd8e 1px solid;
	BACKGROUND-COLOR: #fff8e0;
	BORDER-RIGHT-COLOR: #debd8e
}

/* Rule 838 of embedded stylesheet */ 
.dDialog TFOOT .dialogFooterLeft {
	BACKGROUND: url(GesdocClfDialog/images2.gif) no-repeat -1740px 0px;
	WIDTH: 3px;
	HEIGHT: 3px
}

/* Rule 117 of embedded stylesheet */ 
.dDialog TFOOT .dialogFooterLeft {
	BACKGROUND: url(GesdocClfDialog/images1.gif) no-repeat -1740px 0px
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
	BORDER-RIGHT: #debd8e 1px solid;
	BORDER-LEFT-COLOR: #debd8e;
	BORDER-BOTTOM-COLOR: #debd8e;
	BORDER-TOP-COLOR: #debd8e;
	BACKGROUND-COLOR: #fff8e0
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
	BACKGROUND-COLOR: #fff8e0
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
.dDialog .buttons BUTTON .btnContent {
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
	BORDER-BOTTOM-COLOR: #debd8e;
	BORDER-LEFT: #debd8e 1px solid;
	BORDER-TOP-COLOR: #debd8e;
	BACKGROUND-COLOR: #fff8e0;
	BORDER-RIGHT-COLOR: #debd8e
}

/* Rule 827 of embedded stylesheet */ 
.dDialog TBODY {
	BACKGROUND-COLOR: #f4f5eb
}

/* Rule 108 of embedded stylesheet */ 
.dDialog TBODY {
	BACKGROUND-COLOR: #fff8e0
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
	BORDER-RIGHT: #debd8e 1px solid;
	BORDER-LEFT-COLOR: #debd8e;
	BORDER-BOTTOM-COLOR: #debd8e;
	BORDER-TOP-COLOR: #debd8e;
	BACKGROUND-COLOR: #fff8e0
}

/* Rule 828 of embedded stylesheet */ 
.dDialog TBODY .content {
	PADDING-RIGHT: 1.7em;
	PADDING-LEFT: 1.7em;
	PADDING-BOTTOM: 0em;
	PADDING-TOP: 0em
}

/* Rule 836 of embedded stylesheet */ 
.dDialog TBODY P {
	MARGIN: 1em 0em
}

/* Rule 16 of embedded stylesheet */ 
.inputField {
	BORDER-RIGHT: #ced1c9 1px solid;
	PADDING-RIGHT: 6px;
	BORDER-TOP: #ced1c9 1px solid;
	PADDING-LEFT: 6px;
	FONT-SIZE: 11px;
	PADDING-BOTTOM: 3px;
	BORDER-LEFT: #ced1c9 1px solid;
	PADDING-TOP: 3px;
	BORDER-BOTTOM: #ced1c9 1px solid;
	FONT-FAMILY: tahoma, helvetica, arial, sans-serif;
	BACKGROUND-COLOR: white
}

/* Rule 418 of embedded stylesheet */ 
.inputField {
	BORDER-LEFT-COLOR: #debd8e;
	BORDER-BOTTOM-COLOR: #debd8e;
	BORDER-TOP-COLOR: #debd8e;
	BORDER-RIGHT-COLOR: #debd8e
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
	BORDER-BOTTOM-COLOR: #debd8e;
	BORDER-LEFT: #debd8e 1px solid;
	BORDER-TOP-COLOR: #debd8e;
	BACKGROUND-COLOR: #fff8e0;
	BORDER-RIGHT-COLOR: #debd8e
}

/* Rule 816 of embedded stylesheet */ 
.dDialog THEAD .dialogRight {
	FONT-SIZE: 0px;
	BACKGROUND: url(GesdocClfDialog/images2.gif) no-repeat -1380px 0px;
	OVERFLOW: hidden;
	WIDTH: 3px;
	HEIGHT: 25px
}

/* Rule 114 of embedded stylesheet */ 
.dDialog THEAD .dialogRight {
	BACKGROUND: url(GesdocClfDialog/images1.gif) no-repeat -1380px 0px
}

/* Rule 817 of embedded stylesheet */ 
.dDialog THEAD .closeBtnContainer {
	BACKGROUND: url(GesdocClfDialog/images3.gif) repeat-x 0px -1100px;
	WIDTH: 16px;
	POSITION: relative;
	TEXT-ALIGN: right
}

/* Rule 116 of embedded stylesheet */ 
.dDialog THEAD .closeBtnContainer {
	BACKGROUND: url(GesdocClfDialog/images4.gif) repeat-x 0px -1100px
}

/* Rule 819 of embedded stylesheet */ 
.dDialog THEAD .close {
	BACKGROUND-POSITION: -732px 0px;
	RIGHT: 5px;
	BACKGROUND-IMAGE: url(GesdocClfDialog/images5.gif);
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
	BACKGROUND-IMAGE: url(GesdocClfDialog/images6.gif)
}

/* Rule 813 of embedded stylesheet */ 
.dDialog THEAD .content {
	BACKGROUND: url(GesdocClfDialog/images3.gif) repeat-x 0px -1100px;
	PADDING-BOTTOM: 0px;
	CURSOR: move;
	PADDING-TOP: 1px;
	WHITE-SPACE: nowrap;
	HEIGHT: 25px
}

/* Rule 115 of embedded stylesheet */ 
.dDialog THEAD .content {
	BACKGROUND: url(GesdocClfDialog/images4.gif) repeat-x 0px -1100px
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
	BACKGROUND: url(GesdocClfDialog/images2.gif) no-repeat -1350px 0px;
	OVERFLOW: hidden;
	WIDTH: 3px;
	HEIGHT: 25px
}

/* Rule 113 of embedded stylesheet */ 
.dDialog THEAD .dialogLeft {
	BACKGROUND: url(GesdocClfDialog/images1.gif) no-repeat -1350px 0px
}

</STYLE>
</head>

<body ondragenter="activethis()">

<DIV class="dDialog" id="dDialog<%=id%>" onmousedown="activeDialog('<%=id%>');" style="LEFT: 0px; VISIBILITY: visible; WIDTH: auto; TOP: 0px; HEIGHT: auto" unselectable="on">
 <DIV class="frame1" unselectable="on">
  <TABLE class="frame2" style="WIDTH: 450px; HEIGHT: auto" cellSpacing="0" cellPadding="0" border="0" unselectable="on">
   <THEAD unselectable="on">
    <TR unselectable="on" id='TrHeader' onmousedown="dialogMouseDown('<%=id%>');"  onmouseup="dialogMouseUp('<%=id%>');">
     <TD class="dialogLeft" unselectable="on">
      &nbsp;
     </TD>
     <TD class="content" unselectable="on">
      <H1 unselectable="on">
       <%=title%>
      </H1>
     </TD>
     <TD class="closeBtnContainer" align="right" unselectable="on">
      <DIV onclick="closeAddClassification('<%=id%>');" class="close" id="dialogCloseBtn" unselectable="on" normalclass="close" downclass="close down" overclass="close over">
      </DIV>
     </TD>
     <TD class="dialogRight" unselectable="on">
      &nbsp;
     </TD>
    </TR>
   </THEAD>
   <TBODY unselectable="on">
    <TR unselectable="on">
     <TD class="dialogLeft" unselectable="on">
      &nbsp;
     </TD>
     <TD class="content" colSpan="2" unselectable="on">
     </TD>
     <TD class="dialogRight" unselectable="on">
      &nbsp;
     </TD>
    </TR>
   </TBODY>
   <TFOOT unselectable="on">
    <TR style="HEIGHT: 1px" unselectable="on">
     <TD class="dialogLeft" unselectable="on">
      &nbsp;
     </TD>
     <TD class="content" colSpan="2" unselectable="on">
      <DIV style='height:250px;width:100%'>
        <IFRAME width="100%" height="100%" src="<%=frmUrl%>" frameborder="0" scrolling="auto" id='contentFrame<%=id%>' ></IFRAME>
      </DIV>
        <br>
      <DIV class="buttons" unselectable="on">
       <BUTTON id="dlgBtnOk" unselectable="off" btnId="dlgBtnOk" onclick="ClickBtnOk<%=id%>();" >
       <DIV class="btnContent">
        <%=btnOkLabel%>
       </DIV>
       </BUTTON>
       <BUTTON id="dlgBtnCancel" unselectable="off" btnId="dlgBtnCancel" onclick="closeAddClassification('<%=id%>');">
       <DIV class="btnContent">
        Fechar
       </DIV>
       </BUTTON>
      </DIV>
     </TD>
     <TD class="dialogRight" unselectable="on">
      &nbsp;
     </TD>
    </TR>
    <TR unselectable="on">
     <TD class="dialogFooterLeft" unselectable="on">
     </TD>
     <TD colSpan="2" unselectable="on">
      <DIV class="dialogFooterCenter" unselectable="on">
       &nbsp;
      </DIV>
     </TD>
     <TD class="dialogFooterRight" unselectable="on">
     </TD>
    </TR>
   </TFOOT>
  </TABLE>
 </DIV>
</DIV>
</body>
</html>
<%
} finally {
    boctx.close();
    DOCLIST.releseObjects(boctx);
}
%>
