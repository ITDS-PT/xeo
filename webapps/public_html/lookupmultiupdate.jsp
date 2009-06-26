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
       
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    String myIDX                = request.getParameter("myIDX");
    netgest.bo.runtime.boObject obj=DOC.getObject(Long.parseLong(look_parentBoui));
 //   writeHTML_lookup(StringBuffer toPrint,boObject objParent,boDefAttribute atrParent,StringBuffer Value,StringBuffer Name,int tabIndex,docHTML doc)
 //   StringBuffer toP=new StringBuffer();
    
    netgest.bo.runtime.AttributeHandler atr=obj.getAttribute(look_parentAttribute);
    
//    StringBuffer v= new StringBuffer( atr.getValueString() );
    StringBuffer nameH = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );

    //netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(toP,obj,atrDef,v,nameH,1,DOC);
   
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
      // for(var i=0; i < ifrm.length ; i++)
      // {
         //  if ( ifrm[i].id == "frm$<%=clientIDX%>" )
         //  {
               // updateFrame(ifrm[i]);
                updateFrame(ifrm);
                <%
                if(obj.onChangeSubmit(atr.getName())){
                %>
                   
                   
                    //var Win=ifrm[i].contentWindow;
                    var Win=ifrm.contentWindow;
                    
                   Win.document.getElementById("refreshframe").contentWindow.BindToValidate();
            <% } %>
        //   }
      // }
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
    newDialogBox("critical",errorText.value,[ jsmessage_2 ],jsmessage_6);
</script>    
<%
}
%>
<div id="toRender">
 
 </div>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
