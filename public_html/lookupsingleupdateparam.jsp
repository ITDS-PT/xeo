<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer"%>

<%@ page import="netgest.bo.controller.ControllerFactory"%>
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
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    String xmlSubmitted = request.getParameter("boFormSubmitXml");
    boolean submited = true;
    if(xmlSubmitted == null || "".equalsIgnoreCase(xmlSubmitted))
    {
        submited = false;
    }
    
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    
    String boui = null;
    if(submited)
    {
        boui = GtTemplateViewer.getBoui(look_parentObj, look_parentAttribute, xmlSubmitted);
    }
    else
    {
        boui = request.getParameter("setBoui");
    }
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);

    
    String bouiToReplace     = request.getParameter("bouiToReplace");
    String options           = request.getParameter("options");   
    
    String userHostClient    = (String) DOC.getEboContext().getXeoWin32Client_adress();
    
    StringBuffer toP=new StringBuffer();
    

    
    StringBuffer nameH = new StringBuffer();
    StringBuffer id = new StringBuffer();

    nameH.append( look_parentAttribute );
    id.append( look_parentAttribute );
    
    if ( bouiToReplace== null  )
    {
        netgest.bo.impl.document.merge.gestemp.presentation.GtTemplateViewer.writeHTML_lookup(
            new StringBuffer(look_parentObj),
            toP,
            null,
            null,
            new StringBuffer(String.valueOf(boui)),
            nameH,
            id,
            1,
            DOC,
            false,
            true,
            false,
            false,
            look_parentAttribute
            );
   }

%>

<html>
<head>
<script language="javascript">
function winmain(){var Win=window;while (!Win.openDoc){if( Win==Win.parent ) return Win;Win=Win.parent}return Win}
function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var hasdoc = false;
   try{var ddd = wDoc.document; hasdoc = true}catch(e){}
   if(hasdoc)
   {
       var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
       toRet=false;
       for(var z=0;  z < wDocfrms.length ; z++)
       {
          var xok=false;
          try
          {
            xw=wDocfrms[z].contentWindow.document.all;
            var toR=[];
            for(var j=0; j< xw.length; j++)
            {
                if ( xw[j].id == "tblLook<%=nameH%>" )
                {
                    toR[toR.length]=xw[j];
                }
            }
            for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                xok=true;
            }
          }catch(e){xw=null;}
          if ( !xok )
          {
            updateFrame(wDocfrms[z]);
          }
          else
          {
            try{wDocfrms[z].contentWindow.setPageFocus("IMG<%=nameH%>");}catch(e){}
          }
       }
    }
}
 
  function updateLookupAttribute(){
   
   var windowToUpdate="<%=clientIDX%>";
   var xok=false;
  // var w=dialogArguments.winmain().ndl[windowToUpdate];
  var w=winmain().ndl[windowToUpdate];
  
   if(w){
       var ifrm=w.htm; //.getElementsByTagName('IFRAME');
       var xw;
       
          if(ifrm.contentWindow.document.getElementById('doc_insert_classifs'))
          {
             ifrm = ifrm.contentWindow.document.getElementById('doc_insert_classifs') 
          }
          xw=ifrm.contentWindow.document.all;
          
          var toR=[];
          for(var z=0; z< xw.length; z++)
          {
            if ( xw[z].id == "tblLook<%=nameH%>" )
            {
             toR[toR.length]=xw[z];
            }
          }
          
        for(var y=0; y < toR.length; y++)
        {
                toR[y].outerHTML=toRender.innerHTML;
                xok=true;
        }

       if ( !xok )
       {
        updateFrame(ifrm);
       }
       else
       {
        try{ifrm.contentWindow.setPageFocus("IMG<%=nameH%>");}catch(e){}
       }
   }
   
   try{
      //this.close();
      parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body onload="updateLookupAttribute()">

<xml id="toRender">
 <%=toP%>
 </xml>
</body>
<%
} finally {
   boctx.close();DOCLIST.releseObjects(boctx);
}
%>
