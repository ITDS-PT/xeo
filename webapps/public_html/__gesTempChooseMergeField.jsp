<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.system.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"/>
<%  

EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
try {
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    
    if( bosession == null )
    {
        bosession =  netgest.bo.system.boApplication.getApplicationFromStaticContext("XEO").boLogin("SYSUSER", boLoginBean.getSystemKey() ) ;
    }
    
    if(bosession== null) {
        response.sendRedirect("login.jsp");
        return;
    }
    if(boctx==null) {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }
    int IDX;int cvui;
    String[] ctrl;
    
    StringBuffer javaScriptStr=null;
    
    String templateName  = request.getParameter("templateDoc");
    String tempBoui = request.getParameter("templateObj");
    if( tempBoui != null )
    {
        javaScriptStr=netgest.bo.impl.document.merge.gestemp.presentation.MergeFieldTreeHelper.getJavaScriptTree( boctx, Long.parseLong( tempBoui ) );
    }
    else
    {
        boObjectList list = boObjectList.list( boctx, "select GESTEMP_Template where code = ? and historico='0'", new Object[] { templateName } );
        if( list.next() )
        {
            javaScriptStr=netgest.bo.impl.document.merge.gestemp.presentation.MergeFieldTreeHelper.getJavaScriptTree( boctx, list.getCurrentBoui() );
        }
    }
    %>
    <HTML>
      <HEAD>
        <TITLE>Escolher Attributo</TITLE>
        
        <META http-equiv="Content-Type" content="text/html; CHARSET=UTF-8"/>
        <LINK href="templates/form/std/bo_form_std.css" type="text/css" rel="stylesheet"/>
        <LINK href="bo_global.css" type="text/css" rel="stylesheet"/>
        
        
         <style>
   A  {text-decoration: none;
       color: black}
</style>


        <script LANGUAGE="javascript" SRC="templates/menutree/treedetectbrowser.js"></script>
        <script LANGUAGE="javascript" SRC="templates/menutree/treecore.js"></script>
        <SCRIPT language="javascript">
    
    var bChanged = false;


    function _selBookmark( bookmarkName )
    {
        if( !(typeof(dialogArguments) == "undefined") )
        {
            dialogArguments[0] = "BOOKMARK:"+bookmarkName;
            window.close();
        }
        else
        {
            selBookmark( bookmarkName );
        }
    }

    function _selBookmarkField( bookmarkName, bookmarkField )
    {
        if( !(typeof(dialogArguments) == "undefined") )
        {
            dialogArguments[0] = "BOOKMARKFIELD:" + bookmarkName + "|" + bookmarkField;
            window.close();
        }
        else
        {
            selBookmarkField( bookmarkName, bookmarkField);
        }
    }

    function _selAttribute( attName )
    {
        if( !(typeof(dialogArguments) == "undefined") )
        {
            dialogArguments[0] = "FIELD:" + attName;
            window.close();
        }
        else
        {
            selAttribute( attName );
        }
    }

	function applychanges()
	{
		
	
	}


	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}


  function document.onselectstart()
  {
    var s = event.srcElement.tagName;
    if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;
  }
  
  function document.ondragstart()
  {
    event.returnValue = false;
  }
<%
if( templateName == null )
{
%>
  function selAttribute( id )
  {
    dialogArguments[0] = id;
    window.close();
  }

  function selBookmark( id )
  {
    dialogArguments[0] = id;
    window.close();
  }

  function selBookmarkField( id )
  {
    dialogArguments[0] = id;
    window.close();
  }
  
<%
}
%>

  window.onunload=closedoc;

  function closedoc()
  {
    
  }
  
  function openAttribute()
  {

  }
  
  function openAttributebyid( id ) 
  {
	var folderObj;
	docObj = findObj("x"+id);
	docObj.forceOpeningOfAncestorFolders();
	
    
  //  debugger;    
  //  clickOnLink("x"+id,docObj.link,'_self'); 
    highlightObjLink( docObj );

    //Scroll the tree window to show the selected node
    //Other code in these functions needs to be changed to work with
    //frameless pages, but this code should, I think, simply be removed
    //if (typeof parent.treeframe.document.body != "undefined") //scroll doesn work with NS4, for example
    
    docObj.navObj.focus();
    //    document.body.scrollTop=docObj.navObj.offsetTop
  } 
 ICONPATH = 'templates/menutree/images/'; 
  <%=javaScriptStr%>
  
    USETEXTLINKS = 1;
    STARTALLOPEN = 0;
    USEFRAMES = 0;
    USEICONS = 0;
    WRAPTEXT = 1;
    PRESERVESTATE = 0;
    HIGHLIGHT = 1;

ICONPATH = 'templates/menutree/images/';
HIGHLIGHT_COLOR = 'RED';
HIGHLIGHT_BG    = '#CACACA';

    
    
  var objLabel="Escolher Atributo";
  var objDescription='';
  
</script>

      </HEAD>
      <BODY onload="openAttribute()" scroll=no style="background-color:#EEEEEE;margin:10px; ">
      <div style="position:absolute; top:0; left:0; "><table border=0><tr><td><font size=-2><a style="display:none;font-size:7pt;text-decoration:none;color:silver" href=http://www.treemenu.net/ target=_blank></a></font></td></tr></table></div>

        <TABLE style="WIDTH: 100%; HEIGHT: 100%;border:1px solid #5A8CD7" bgcolor="#FFFFFF" cellSpacing="0" cellPadding="4">
          <TBODY>
            <TR>
              <TD>
                <DIV id="divMain" style="overflow:auto;height:100%;background-color:#FFFFFF;">
                  <script>initializeDocument()</script>
                </DIV>
              </TD>
            </TR>
          </TBODY>
        </TABLE>
      </BODY>
    </HTML>
    <%  
} finally {
    if(initPage) {
    boctx.close();
    DOCLIST.releseObjects(boctx);
    }
}
%>
