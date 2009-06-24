<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.impl.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.impl.document.DocumentContainer"%>
<%@ page import="netgest.io.iFile"%>
<%@ page import="java.io.InputStream"%>
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
  String docboui = request.getParameter("parent_boui");
  long docBoui = netgest.utils.ClassUtils.convertToLong(docboui,-1);
  
  boObject document = null;
    
  if ( docBoui != -1 )
  {
      document = DOC.getObject( docBoui );      
  }
  if(document.getAttribute("file").getValueiFile() != null){
    iFile file = document.getAttribute("file").getValueiFile();
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>

<script language="javascript">

function runOperation( boui, type){                   
    if("undoCheckOut" == type){
       createHiddenInput(type + "__Ebo_Document__" + boui , boui);                               
    }

    xml=parent.parent.boForm.BuildXml(false,false);      
    boFormSubmit.boFormSubmitXml.value=xml;    
    boFormSubmit.submit();                         
    if("checkIn" == type){                     
        winmain().openDoc('fixed,450px,210px,noresize',null,'','','lookup','attachfile.jsp?docid=<%=IDX%>&checkIn=check&look_parentObj=Ebo_Document&look_parentAttribute=file&look_parentBoui=<%= document.getBoui() %>&clientIDX='+getIDX()+'');
    }else if("undoCheckOut" == type){   
        boForm.executeMethod(type);         
    }
        
}
</script>
<%@ include file='boheaders.jsp'%>
<script><%=DOC.getScriptToRunOnClient()%></script>
<script>var objLabel='Lista de Documento';</script>

<body ondragenter="activethis()" onload="render();">
<form class="objectForm" name="boForm" id="<%=IDX%>" >

  <TABLE class='section' cellSpacing='0' cellPadding='0'  ><COLGROUP/><COL width='120' /><COL /><COL style="PADDING-LEFT: 5px" width='70' /><COL /><TBODY>
    <TR height='10'><TD colspan='4'></TD></TR>
   <%String active = boConfig.getDocumentationConfig().getProperty("active");  
    if("true".equalsIgnoreCase(active)){   %>
    <tr>
        <TD><label  for='Ebo_Document__1678350__owner54'>Check Out por </label></TD>
        <td colspan='3'>
            <table id='tblLookEbo_Document__1678350__owner' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody>
                <tr>
                    <% if(file.isCheckedOut()){%>
                        <td colspan='2'><div class='lu ro lui' valido='Ebo_Perf' ><%=boObject.getBoManager().loadObject(boctx,Long.parseLong(file.getCheckOutUser())).getCARDID()%></div></td>
                    <% } else {%>
                        <td colspan='2'><div class='lu ro lui' valido='Ebo_Perf' ></div></td>
                    <% } %>
                </tr></tbody>
            </table>
        </td>
    </tr>    
  <tr>
    <TD>
      <label  for='Ebo_Document__1678350__fileVersion'>Vers⭼/label></TD><td colspan='3'>
      <% if(file.getAbsolutePath().startsWith("/")) { %>
        <input class='num'   name = 'Ebo_DocumentDetail__1678350__fileVersion' id = 'Ebo_DocumentDetail__1678350__fileVersion'  value='<%= file.getVersion() %>' returnValue='<%= file.getVersion() %>'  dt='' minAcc='0' acc='0' grp='false'  max='' min='1' disabled >
      <% } else { %>        
        <input class='num'   name = 'Ebo_DocumentDetail__1678350__fileVersion' id = 'Ebo_DocumentDetail__1678350__fileVersion'  value='' returnValue=''  dt='' acc='0' minAcc='0' grp='false'  max='' min='1' disabled >
      <% }  %>        
    </td>
  </tr>
    
    <TR>
        <TD colspan='4'>
            <table style="border:0px ;solid: #CCCCCC;align:absmiddle;width:100%;height:100%" bgcolor=#ffffff>
                <tr height='20px'>        
                    <td><button onclick="parent.downloadFile(<%= document.getBoui() %>,'getLatestVersion')">Última Versão</button></td>
                    <% if(file.isCheckedIn()){ %>
                        <td><button onclick="parent.downloadFile(<%= document.getBoui() %>,'checkOut')">Check Out</button></td>
                    <% } %>
                    <% if(file.isCheckedOut() &&  Long.parseLong(file.getCheckOutUser()) == boctx.getBoSession().getPerformerBoui() ){ %>
                        <td><button onclick="runOperation(<%= document.getBoui() %>,'checkIn')">Check In</button></td>                
                        <td><button onclick="runOperation(<%= document.getBoui() %>,'undoCheckOut')">Undo Check Out</button></td>                
                    <% } %>
                    <td style='width:100%'></td>
                </tr>                
            </table>
        </TD>
    </TR> 
    </TBODY></TABLE>
<% } %>
</form>

<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name=parent_attribute value='versions' />
    <INPUT type='hidden' name=parent_boui value='<%= docBoui %>' />
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='BOUI' value='<%= docBoui %>'/>
    <INPUT type='hidden' name='boiChanged' value='true' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />    
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>
</body>
</html>
<%
}
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
