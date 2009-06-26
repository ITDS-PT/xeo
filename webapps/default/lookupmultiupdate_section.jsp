<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

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
    
    String bouiToReplace     = request.getParameter("bouiToReplace");  
    
    netgest.bo.runtime.boObject obj=DOC.getObject(Long.parseLong(look_parentBoui));
    StringBuffer toP=new StringBuffer();
    
    AttributeHandler atr= obj.getAllAttributes().get( look_parentAttribute );
  
    StringBuffer v= new StringBuffer( atr.getValueString() );
    
    
    StringBuffer nameH = new StringBuffer();
    StringBuffer id = new StringBuffer();
    //boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );

    //AttributeHandler attr= obj.getAllAttributes().get( look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    
    if ( bouiToReplace== null  )
    {
    
    netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookupN(
        toP,
        obj,
        obj.getBridge(look_parentAttribute), 
        atr,
        v,
        nameH,
        id,
        1,
        DOC,
        atr.isDisabled(),
        atr.isVisible(),
        obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
        );
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
      <%
        if(obj.onChangeSubmit(atr.getName()))
        {
      %>
            if(xok)
            {
                var Win=ifrm[i].contentWindow;
                Win.document.getElementById("refreshframe").contentWindow.BindToValidate();
            }
      <% } %>
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
          <%
                if(obj.onChangeSubmit(atr.getName())){
           %>
              if(xok)
              {
                //var Win=ifrm[i].contentWindow;
                var Win=ifrm.contentWindow;
                Win.document.getElementById("refreshframe").contentWindow.BindToValidate();
              }
          <% } %>
          
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

 <%=toP%>
 </div>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
