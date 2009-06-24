<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.impl.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>

<%
response.setDateHeader ("Expires", -1);

boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext"); 

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

  String idbolist;
  String[] ctrl;

  ctrl= DOCLIST.processRequest(boctx);
  IDX= ClassUtils.convertToInt(ctrl[0],-1);
  String value;
  
  idbolist=ctrl[1];
  docHTML DOC = DOCLIST.getDOC(IDX);
  
  String parentObj = request.getParameter("parentObj");
  String myIDX = request.getParameter("myIDX");
  String parentBoui = request.getParameter("parentBoui");
  String parent_attribute = request.getParameter("parentAttribute");
  String actIdxClient = request.getParameter("actIdxClient");
  String result = request.getParameter("attName");
  String resultMess = result + "MessageBody";
  String disabled = request.getParameter("disabled");
  String editorType = request.getParameter("editor");
  String editor = "on";
  if("on".equals(disabled))
  {
    editor = "off";
  }
  
  boObject attParent = DOC.getObject(Long.parseLong(parentBoui));
  AttributeHandler attH = attParent.getAttribute(parent_attribute);
  value = attH.getValueString();
%>

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Edição Texto</title>
    <%@ include file='boheaders.jsp'%>
	<script LANGUAGE="javascript" SRC="templates/form/std/jsObjects.js"></script>
    <script>
        var objLabel='<img align="absmiddle" hspace=3 src=\"templates/form/std/imgEditor/cmd-justifyleft.gif\">Edição de Texto';
		var objectName="<%=parentObj%>";
		var objDescription='';
        function updateAttribute()
        {
            var windowToUpdate="<%=actIdxClient%>";
            var frameToUpdate="<%=result%>" + "MessageBody";
            var xok=false;
            var w=parent.ndl[windowToUpdate];
            if(w)
            {
                var ifrm=w.htm.document.getElementsByTagName('IFRAME');
                var xw;
                for(var i=0; i < ifrm.length ; i++)
                {
                    xw=ifrm[i].contentWindow.document.getElementById(frameToUpdate);
                    if(xw)
                    {
                        xw.contentWindow.document.body.innerHTML = this.document.getElementById("messageBody").contentWindow.document.body.innerHTML;
                    }                    
                }
            }           
           try
           {
               parent.ndl[<%=myIDX%>].close();
           }
           catch(e){}
        }

    </script>
</head>
<body>    

<!--BEGIN -->
<%
if(value != null && value.length() > 0)
{
%>
<xml id='valueFor' name='text' > <%=value%></xml>
<%
}
else
{
%>
<xml id='valueFor' name='text' ></xml>
<%
}
%>
<%if("on".equals(disabled)){%>
<table id='tableId' style="table-layout:fixed;height:100%;width:100%;" disabled cellSpacing="0" cellPadding="0" height="100%" width="100%">
<%}else{%>    
<table id='tableId' style="table-layout:fixed;height:100%;width:100%;" cellSpacing="0" cellPadding="0" height="100%" width="100%">
<%}%>
    <tr>
        <td height="23px" width="100%">
            <table class="mnubar" height="100%" cellSpacing="0" cellPadding="0" width="100%">
                <tbody>
                    <tr>
                        <%if(!"on".equals(disabled)){%>
                        
                        <td width="9">
                            <img hspace="1" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18">
                        </td>                        
                        <td class="icMenu" noWrap>
                            
                                <span class="menuFlat" title="Aplicar texto" onclick="updateAttribute();" tabIndex="0">
                                <img class="mnuBtn" src="templates/menu/std/16_filter.gif" WIDTH="16" HEIGHT="16">Aplicar Texto</span>
                            
                        </td>
                        <%}%>
                    </tr>
                </tbody>
            </table>
        </td>
    </tr>
		    
		
    
        <tr>
            <td colspan="5" height="100%" style="background-color:#EAEAEA">
    	        <table height="100%" cellSpacing="0" cellPadding="0" width="100%"><tbody>
                    <tr id="tableHtmlBarGLOB" height="26">
                       <td>
                           <table class="htmlBar" id="tableHtmlBar" cellSpacing="0" cellPadding="2">
                              <tbody>
                       <tr>
                    <%if(!"on".equals(disabled)){%>
                        <td class="htmlBtn" title="Cut" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('cut')" noWrap><img src="templates/form/std/imgEditor/cmd-cut.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Copy" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('copy')" noWrap><img src="templates/form/std/imgEditor/cmd-copy.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Paste" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('paste')" noWrap><img src="templates/form/std/imgEditor/cmd-paste.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>              
                            <td class="htmlBtn" title="Undo" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('Undo')" noWrap><img src="templates/form/std/imgEditor/cmd-undo.gif" ></td>        
                            <td class="htmlBtn" title="Redo" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('Redo')" noWrap><img src="templates/form/std/imgEditor/cmd-redo.gif" ></td>
                        <%}%>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Bold" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('bold')" noWrap tabindex='140'><b>B</b></td>
                        <td class="htmlBtn" title="Italic" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('italic')" noWrap tabindex='140'><b><i>I</i></b></td>
                        <td class="htmlBtn" title="Underline" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('underline')" noWrap tabindex='140'><b><u>U</u></b></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            <td class="htmlBtn" title="SuperScript" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('superscript')" noWrap><img src="templates/form/std/imgEditor/cmd-superscript.gif" ></td>
                            <td class="htmlBtn" title="Subscript" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('subscript')" noWrap><img src="templates/form/std/imgEditor/cmd-subscript.gif" ></td>
                        <%}%>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Align Left" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('justifyleft')" noWrap><img src="templates/form/std/imgEditor/cmd-justifyleft.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Center" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('justifycenter')" noWrap><img src="templates/form/std/imgEditor/cmd-justifycenter.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Align Right" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('justifyright')" noWrap><img src="templates/form/std/imgEditor/cmd-justifyright.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Numbering" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('insertOrderedList')" noWrap><img src="templates/form/std/imgEditor/cmd-insertOrderedList.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Bullets" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('insertUnorderedList')" noWrap><img src="templates/form/std/imgEditor/cmd-insertUnorderedList.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Increase Indent" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('indent')" noWrap><img src="templates/form/std/imgEditor/cmd-indent.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Decrease Indent" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('outdent')" noWrap><img src="templates/form/std/imgEditor/cmd-outdent.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            </tr><tr>
                            <td class="htmlBtn" title="Insert Horizontal Rule" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('InsertHorizontalRule')" noWrap><img src="templates/form/std/imgEditor/cmd-hr.gif" ></td>
                        <%}else{%>            
                            <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                            <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <%}%>
                        <td class="htmlBtn" title="Font Name" style="WIDTH: 30px" noWrap command="fontname" dropdown="true"><img src="templates/form/std/imgEditor/cmd-fontname.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Font Size" style="WIDTH: 30px" noWrap command="fontsize" dropdown="true"><img src="templates/form/std/imgEditor/cmd-fontsize.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Font Color" style="WIDTH: 30px" noWrap command="fgcolor" dropdown="true"><img src="templates/form/std/imgEditor/cmd-fgcolor.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            <td class="htmlBtn" title="Insert Table" style="WIDTH: 26px" onclick="tableHtmlBar.insertTable("messageBody");" noWrap><img src="templates/form/std/imgEditor/cmd-table.gif" ></td>
                            <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                            <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                            <td class="htmlBtn" title="Insert Link" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('createlink',null,true)" noWrap><img src="templates/form/std/imgEditor/cmd-link.gif" ></td>
                            <td class="htmlBtn" title="Insert Link to Content" style="WIDTH: 26px" onclick="tableHtmlBar.insertLinkToContent(messageBody,'messageBody');" noWrap><img src="templates/form/std/imgEditor/cmd-linkcontent.gif" ></td>               
                            <td class="htmlBtn" title="Insert Link to Local Image" style="WIDTH: 26px" onclick="tableHtmlBar.insertLinkToImage(messageBody,'<%=result%>');" noWrap><img src="templates/form/std/imgEditor/cmd-imagelocal.gif" ></td>
                            <td class="htmlBtn" title="Insert Link to External Image" style="WIDTH: 26px" onclick="tableHtmlBar.htmlExec('insertimage')" noWrap><img src="templates/form/std/imgEditor/cmd-imagelink.gif" ></td>
                        <%}%>
                    <%}else{%>
                        <td class="htmlBtn" title="Cut" style="WIDTH: 26px"  noWrap><img src="templates/form/std/imgEditor/cmd-cut.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Copy" style="WIDTH: 26px"  noWrap><img src="templates/form/std/imgEditor/cmd-copy.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Paste" style="WIDTH: 26px"  noWrap><img src="templates/form/std/imgEditor/cmd-paste.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>              
                            <td class="htmlBtn" title="Undo" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-undo.gif" ></td>        
                            <td class="htmlBtn" title="Redo" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-redo.gif" ></td>
                        <%}%>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Bold" style="WIDTH: 26px" noWrap tabindex='140'><b>B</b></td>
                        <td class="htmlBtn" title="Italic" style="WIDTH: 26px" noWrap tabindex='140'><b><i>I</i></b></td>
                        <td class="htmlBtn" title="Underline" style="WIDTH: 26px"  noWrap tabindex='140'><b><u>U</u></b></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            <td class="htmlBtn" title="SuperScript" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-superscript.gif" ></td>
                            <td class="htmlBtn" title="Subscript" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-subscript.gif" ></td>
                        <%}%>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Align Left" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-justifyleft.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Center" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-justifycenter.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Align Right" style="WIDTH: 26px"  noWrap><img src="templates/form/std/imgEditor/cmd-justifyright.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Numbering" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-insertOrderedList.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Bullets" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-insertUnorderedList.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Increase Indent" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-indent.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Decrease Indent" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-outdent.gif" WIDTH="16" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            </tr><tr>
                            <td class="htmlBtn" title="Insert Horizontal Rule" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-hr.gif" ></td>
                        <%}else{%>            
                            <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                            <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <%}%>
                        <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                          <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                        <td class="htmlBtn" title="Font Name" style="WIDTH: 30px" noWrap command="fontname" dropdown="false"><img src="templates/form/std/imgEditor/cmd-fontname.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Font Size" style="WIDTH: 30px" noWrap command="fontsize" dropdown="false"><img src="templates/form/std/imgEditor/cmd-fontsize.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <td class="htmlBtn" title="Font Color" style="WIDTH: 30px" noWrap command="fgcolor" dropdown="false"><img src="templates/form/std/imgEditor/cmd-fgcolor.gif" WIDTH="24" HEIGHT="16" tabindex='140'></td>
                        <%if ("htmladvanced".equalsIgnoreCase(editorType)){%>
                            <td class="htmlBtn" title="Insert Table" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-table.gif" ></td>
                            <td style="PADDING-LEFT: 4px; WIDTH: 10px">
                            <div style="BORDER-LEFT: #c5c2b8 1px solid">&nbsp;</div></td>
                            <td class="htmlBtn" title="Insert Link" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-link.gif" ></td>
                            <td class="htmlBtn" title="Insert Link to Content" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-linkcontent.gif" ></td>               
                            <td class="htmlBtn" title="Insert Link to Local Image" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-imagelocal.gif" ></td>
                            <td class="htmlBtn" title="Insert Link to External Image" style="WIDTH: 26px" noWrap><img src="templates/form/std/imgEditor/cmd-imagelink.gif" ></td>
                        <%}%>
                    <%}%>
                    <td style="PADDING-RIGHT: 0px; PADDING-LEFT: 0px; PADDING-BOTTOM: 0px; PADDING-TOP: 0px">
                      <table height="100%" cellSpacing="0" cellPadding="2" width="100%">
                        <tbody>
                        <tr>
                        <td class="htmlBtn" title="" style="TEXT-ALIGN: left"  noWrap></td>
                        <td width="100%">&nbsp;</td></tr></tbody></table></td></tr></tbody></table></td></tr>
            <tr>
              <td>
                <iframe class="editPage" 
                        onfocus='setFieldWFocus("messageBody")' 
                        id="messageBody"
                        
                        onload="messageBody.document.designMode = '<%=editor%>';
                                    UpdateMessageBody(messageBody,valueFor);
                                    window.event.cancelBubble=true;"
                        onmouseout="tableHtmlBar.forceHTMLMode(messageBody,tableHtmlBarGLOB,tableHtmlBarchk);"
                        tabIndex="140" 
                        src="templates/form/std/msgBody.htm" 
                        frameBorder="0" sxecurity="restricted"></iframe>
</td></tr></tbody></table>
</td> </tr><tr height="26" width='100%'><td><input style='border:0px' id="tableHtmlBarchk" type="checkbox" onclick="tableHtmlBar.doToggleView(messageBody,tableHtmlBarGLOB);" value="OFF">Editar  HTML </td></tr></tbody></table>
</td> </tr></tbody></table>
<!--END -->
</body>
</html>
<%
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
