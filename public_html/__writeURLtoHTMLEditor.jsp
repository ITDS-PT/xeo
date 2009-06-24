<%@ page contentType="text/html;charset=UTF-8"%>

<%@ page import="java.util.*"%>
<%@ page import="javax.naming.InitialContext"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.ejb.boClientRemoteHome"%>
<%@ page import="netgest.bo.ejb.*"%>
<%@ page import="netgest.bo.system.boSession"%>
<%@ page import="netgest.utils.*"%>
<%@ page import="org.w3c.dom.Element"%>
<%@ page import="oracle.xml.parser.v2.*"%>


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

    
    String bouiToReplace     = request.getParameter("bouiToReplace");
    String options           = request.getParameter("options");   
    
    
    StringBuffer toP=new StringBuffer();
    
%>

<html>

<SCRIPT language=javascript src="bo_global.js"></SCRIPT>
<script language="javascript">
<%
  XMLDocument auxxml=ngtXMLUtils.loadXML(DOC.getCheckedValues());
  if (auxxml.getFirstChild().getFirstChild().getFirstChild()!=null ) 
  {

      String currBoui=auxxml.getFirstChild().getFirstChild().getFirstChild().getNodeValue();
      
      boObject currOBJ=boObject.getBoSecurityManager().loadObject(boctx,new Long(currBoui).longValue());  
      String derivesfrom=currOBJ.getBoDefinition().getBoSuperBo();
      String objName=currOBJ.getName();
      String alt="";
      String MM_FileType = null;
      if(objName.equalsIgnoreCase("XEOCM_MMFile"))
        MM_FileType = currOBJ.getAttribute("tipo").getValueString();
      StringBuffer url=new StringBuffer();        
      if (objName.equalsIgnoreCase("XEOCM_Contents_Link") || objName.startsWith("XEOCM_Document") || (derivesfrom.equalsIgnoreCase("XEOCM_Base")) || (MM_FileType != null && !MM_FileType.equalsIgnoreCase("IMG")) && !(objName.equalsIgnoreCase("XEOCM_Image") && look_parentObj.equals("XEOCM_Image")) || (derivesfrom!=null && derivesfrom.equalsIgnoreCase("XEOCM_Image")))
      { 
        url.append("http://#URL:"+currBoui+"#");    
    %>
            var url="<%=url%>";
            var cardid="<%=currOBJ.getCARDID()%>";
            
            toPaste=parent.ndl[<%=clientIDX%>].htm.contentWindow.document.all.<%=options%>.whereTo;
            
            if (toPaste.htmlText=="" && url!="")
                    toPaste.pasteHTML("<a href='"+url+"'>"+cardid+"</a>");
            else
                    toPaste.pasteHTML( "<a href='"+url+"'>" + toPaste.htmlText + "</a>" );		
    <%
        }
        else
        {
    %>
    var imgurl='<%=boctx.getApplicationUrl()+"/attachfileu.jsp?look_parentBoui="+currBoui+"&att_display=n&att_download=y"%>';
    toPaste=parent.ndl[<%=clientIDX%>].htm.contentWindow.document.all.<%=options%>.whereTo;
    toPaste.pasteHTML("<img src='"+imgurl+"' alt='<%=currOBJ.getAttribute("description").getValueString()%>'></img>");
    <%}
}%>
parent.ndl[<%=clientIDXtoClose%>].close();
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<body>
<%
if( DOC.haveErrors() )
{
%>
<textarea id="errorText">
<%=DOC.getHTMLErrors()%>
</textarea>
<!--<script>
    newDialogBox("critical",errorText.value,["Continuar" ]," Atenção, Ocorreu um erro na aplicação!!!");
</script>     -->
<%
}
%>
<div id="toRender">
 <%=toP%>
 </div>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
