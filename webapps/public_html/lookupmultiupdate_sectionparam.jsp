<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>

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
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boolean submited = true;
    String xmlSubmitted = request.getParameter("boFormSubmitXml");
    if(xmlSubmitted == null || "".equalsIgnoreCase(xmlSubmitted))
    {
        submited = false;
    }
       
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    String myIDX                = request.getParameter("myIDX");
    
    String boui = null;
    if(submited)
    {
        boui = netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer.getBoui(look_parentObj, look_parentAttribute, xmlSubmitted);
    }
    else
    {
        boui = request.getParameter("setBoui");
    }
    
    StringBuffer nameH = new StringBuffer();
    StringBuffer id = new StringBuffer();

    nameH.append( look_parentAttribute );
    id.append( look_parentAttribute );
   
%>

<html>
<script language="javascript">

 function setUrlAttribute(href,attname,value) {
	var xqry=""
	if( href.indexOf("?") > -1) {
		xqry = href.substring(href.indexOf("?")+1);
	}
	else
	{
	 return href+'?'+attname+"="+encodeURIComponent(value);
	}
	
    var xargs = xqry.split("&");
    var fnd = false;
    for(var i=0;i<xargs.length;i++) {
        if(xargs[i].split("=")[0]==attname) {
            xargs[i] = attname+"="+encodeURIComponent(value);
            fnd = true;
        }
    }
    if(!fnd) xargs[xargs.length] = attname+"="+encodeURIComponent(value);

    return href.substring(0,href.indexOf("?")+1)+xargs.join("&"); 
        
}

function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      if (  wDocfrms[z].id == "inc_<%=nameH%>" )
      {            
            //var xhref=wDocfrms[z].contentWindow.location.href;
            //wDocfrms[z].contentWindow.location.href=setUrlAttribute(xhref,'boFormSubmitXml','');             
            wDocfrms[z].contentWindow.submitGrid();
            return;
      }
      else
      {
         updateFrame(wDocfrms[z]); 
      }
   }
           
}
 
 function updateLookupAttribute()
 {
   var windowToUpdate="<%=clientIDX%>";
   var w=parent.ndl[windowToUpdate];
    if(w)
   {
       var ifrm=w.htm; //.getElementsByTagName('IFRAME');
       var xw;
                updateFrame(ifrm);
   }
   try
   {
       parent.ndl[<%=clientIDXtoClose%>].close();
   }
   catch(e){}
 }
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<body onload="updateLookupAttribute()">
<%
if( DOC.haveErrors() )
{
%>
<textarea id="errorText">
<%=DOC.getHTMLErrors()%>
</textarea>
<script>
    newDialogBox("critical",errorText.value,["Continuar" ]," Aten��o, Ocorreu um erro na aplica��o!!!");
</script>    
<%
}
%>
<div id="toRender">
 
 </div>
</body>
<%
} finally {
    if(initPage) {boctx.close();DOCLIST.releseObjects(boctx);}
}
%>
