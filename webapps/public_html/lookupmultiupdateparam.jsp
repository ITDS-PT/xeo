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
    String bouiToReplace     = request.getParameter("bouiToReplace");
    String look_query     = request.getParameter("look_query");
    boolean reset = "true".equals(request.getParameter("reset"));
    
    String boui = null;
    if(reset)
    {
        boui = "";
    }
    else if(submited)
    {
        boui = netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer.getBoui(look_parentObj, look_parentAttribute, xmlSubmitted);
    }
    else
    {
        boui = request.getParameter("setBoui");
    }
    
    StringBuffer nameH = new StringBuffer();
    StringBuffer id = new StringBuffer();
    StringBuffer toP=new StringBuffer();

    nameH.append( look_parentAttribute );
    id.append("tblLook").append(look_parentAttribute );
    
    if ( bouiToReplace== null  )
    {
     netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer.writeHTML_lookupN(
                              toP, null, null, null, new StringBuffer(boui), 
                              nameH, 
                              id, 1, DOC, false, true, false, false, false, true, 
                              null, null, null, new StringBuffer(look_parentObj), "".equals(look_query) ? null:look_query);
   }
%>

<html>

<script language="javascript">

function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   toRet=false;
   var xok = false;
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      var xok=false;
      xw=wDocfrms[z].contentWindow.document.all;
      var toR=[];
      for(var j=0; j< xw.length; j++){
        if ( xw[j].id == "tblLook<%=nameH%>" ){
         toR[toR.length]=xw[j];
        }
      }
      for(var y=0; y < toR.length; y++){
            toR[y].outerHTML=toRender.innerHTML;
            xok = true;
      }
         //toRet=
         updateFrame(wDocfrms[z]); 
     
   }
   //return toRet
           
}
 
  function updateLookupAttribute(){
    
   var windowToUpdate="<%=clientIDX%>";

   var xok=false;
   var w=parent.ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm ;//.getElementsByTagName('IFRAME');
       var xw;
      // for(var i=0; i < ifrm.length ; i++){
          //xw=ifrm[i].contentWindow.document.all;
          xw=ifrm.contentWindow.document.all;
          var toR=[];
          for(var z=0; z< xw.length; z++){
            if ( xw[z].id == "tblLook<%=nameH%>" ){
             toR[toR.length]=xw[z];
            }
          }
          for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                xok=true;
          }
          
      // }
       if ( !xok ) updateFrame(ifrm);//updateInFrames();
   }
   
   try{
       parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
 
 
 
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<body onload="updateLookupAttribute()">
<div id="toRender">
 <%=toP%>
 </div>
</body>
<%
} finally {
    if(initPage) {boctx.close();DOCLIST.releseObjects(boctx);}
}
%>