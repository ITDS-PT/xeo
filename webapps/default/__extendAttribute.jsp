<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.impl.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.workflow.DocWfHTML"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>

<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
 

try {
  boolean masterdoc=false;
  if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
          masterdoc=true;
  }
  boSession bosession = (boSession)request.getSession().getAttribute("boSession");
  if(bosession== null) {
      response.sendRedirect("login.jsp?returnPage=__document_list.jsp");
      return;
  }
  if(boctx==null) {
      boctx = bosession.createRequestContext(request,response,pageContext);
      request.setAttribute("a_EboContext",boctx);
  }
 
  int IDX;
  int cvui;
  boDefHandler bodef;
  boDefAttribute atr;
  String idbolist;
  String[] ctrl;
  docHTML_section sec;
  docHTML_grid grid;
  Hashtable xattributes;
  ctrl= DOCLIST.processRequest(boctx);
  IDX= ClassUtils.convertToInt(ctrl[0],-1);
  
  idbolist=ctrl[1];
  docHTML DOC = DOCLIST.getDOC(IDX);
  boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);  

  boObject BOI;
  if ( currObjectList == null ) BOI=null;
  else BOI=currObjectList.getObject();
  if( currObjectList != null ) currObjectList.first();
  
  String bridge = currObjectList.getName();
  String parent_attribute = request.getParameter("parent_attribute");    
  String parent_boui = request.getParameter("parent_boui");  
  boObject actionObject = DOC.getObject(  netgest.utils.ClassUtils.convertToLong(parent_boui,-1) );      
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<%@ include file='boheaders.jsp'%>
<script language="javascript">
function render()
{
 window.setTimeout('setHeight()',200);
}
function setHeight(){        
    //if(all.offsetHeight > 200){
        window.frameElement.style.height = all.offsetHeight;
    //}
}

</script>
<script>var objLabel='Lista de Documento';</script>

<body ondragenter="activethis()" onload="render();">
<div id='all' style='vertical-align=top;position:absolute;height:10px;overflow:visible'>
<form class="objectForm" name="boForm" id="<%=IDX%>" > 

<%DocWfHTML.renderExtendAttributes(actionObject,(bridgeHandler)currObjectList,DOC,DOCLIST,pageContext,IDX);%>
</form>
</div>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name=parent_attribute value='<%= parent_attribute %>' />
    <INPUT type='hidden' name=parent_boui value='<%= parent_boui %>' />
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
<%
if (BOI!=null)
{
%>
    <INPUT type='hidden' name='BOUI' value='<%= BOI.getBoui() %>'/>
    <INPUT type='hidden' name='boiChanged' value='<%=BOI.isChanged()%>' />
<%    
}
%>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />    
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>
</body>
</html>
<%
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

