<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
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
    //ctrl    = DOCLIST.processRequest(boctx);
    //IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    String objectName       =request.getParameter("object");
    String pextendObject    =request.getParameter("extendObject");
    String selectedAttribute=request.getParameter("selectedAttribute");
    String onlyObjects   =request.getParameter("onlyObjects");
    
    
    boolean extendObject=true;
    if ( pextendObject!=null && pextendObject.equalsIgnoreCase("NO") )
    {
        extendObject=false;
    }
    
    
    
    
    boDefHandler bodef = null;
    StringBuffer javaScriptStr=null;
    if(!"".equals(objectName))
    {
        bodef = boDefHandler.getBoDefinition( objectName );
        netgest.utils.Counter index=new netgest.utils.Counter();
        
        javaScriptStr=netgest.bo.userquery.queryBuilderHelper.getJavaScriptTree( bodef , onlyObjects , true, extendObject,index );

    }
    else
    {
        javaScriptStr=new StringBuffer();
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
  
  function selAttribute( number )
  {
   //alert(attributes[number]);
   //dialogArguments.cellAttribute.firstChild.rows[0].cells[0].firstChild.value=attributes[number];
   dialogArguments.changeAttribute(attributes[number]);
   window.close();
  }
  window.onunload=closedoc;
  function closedoc()
  {
    dialogArguments.resetBorder();
  }
  
  function openAttribute()
  {
     if ( dialogArguments.attributeName!='_x_' && dialogArguments.attributeName!=' ')
     {
        for ( i=0 ; i< attributes.length; i++ )
        {
           if ( attributes[i]==dialogArguments.attributeName)
           {
            openAttributebyid(i);
            break;
           }
        }
     }
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
    if (boctx!=null)boctx.close();
    if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
    }
}
%>
