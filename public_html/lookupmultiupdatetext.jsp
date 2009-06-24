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
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
       
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    String fieldID           = request.getParameter("fieldID");
    String xmlSubmitted = request.getParameter("boFormSubmitXml");
    boolean submited = true;
    if(xmlSubmitted == null || "".equalsIgnoreCase(xmlSubmitted))
    {
        submited = false;
    }
    String[] boui = null;
    long b = -1;
    if(submited)
    {
        boui = GtTemplateViewer.getBouis(null, null, xmlSubmitted);
    }
    else
    {
        if(request.getParameter("setBoui") != null && request.getParameter("setBoui").length() > 0)
        {
            boui = request.getParameter("setBoui").split(";"); 
        }
    }
    
    StringBuffer description = new StringBuffer("");
    String valor = null;
    if(boui != null && boui.length > 0)
    {
        boObject o = null;
        for (int i = 0; i < boui.length; i++) 
        {
            try
            {
                b = Long.parseLong(boui[i]);                
                o = boObject.getBoManager().loadObject(boctx, b);
                description.append(i+1).append(" - ")
                    .append(o.getAttribute("texto").getValueString());
                if((i+1) < boui.length)
                {
                    description.append(" / ");
                    valor = (valor == null ? (b +";"):(valor + b +";"));
                }
                else
                {
                    valor = (valor == null ? String.valueOf(b):(valor + b));
                }
            }
            catch(Exception e){/*ignore*/}
        }
    }
    String s = java.net.URLEncoder.encode(description.toString()); 
%>

<html>
<head>
<script language="javascript">
function winmain(){var Win=window;while (!Win.openDoc){if( Win==Win.parent ) return Win;Win=Win.parent}return Win}

  function updateTextAttribute(){
   var windowToUpdate="<%=clientIDX%>";
  
   var xok=false;
  // var w=dialogArguments.winmain().ndl[windowToUpdate];
  var w=winmain().ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm; //.getElementsByTagName('IFRAME');
       var xw;

          xw=ifrm.contentWindow.document.getElementById('viewer<%=fieldID%>');
          if(xw)
          {
            xw.value=URLDecode('<%=s%>');
          }

          xw=ifrm.contentWindow.document.getElementById('<%=fieldID%>');
          if(xw)
          {
            <%if(valor == null){%>
            xw.value=URLDecode('<%=s%>');
            <%}else{%>
            xw.value='<%=valor%>';
            <%}%>
            
          }          
   }
   
   try{
      //this.close();
      parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
 function URLDecode(psEncodeString) 
 {
  // Create a regular expression to search all +s in the string
  var lsRegExp = /\+/g;
  // Return the decoded string
  return unescape(String(psEncodeString).replace(lsRegExp, " ")); 
 }

</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body onload="updateTextAttribute()">

<xml id="toRender">
 <%=s%>
 </xml>
</body>
<%
} finally {
   boctx.close();DOCLIST.releseObjects(boctx);
}
%>
