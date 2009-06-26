<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.impl.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.io.iFile"%>
<%@ page import="netgest.bo.impl.document.DocumentHelper"%>
<%@ page import="netgest.bo.impl.document.DocumentContainer"%>
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

  String bridge = currObjectList.getName();

  boObject BOI;
  if ( currObjectList == null ) BOI=null;
  else BOI=currObjectList.getObject();
  if( currObjectList != null ) currObjectList.first();
  
    String active = boConfig.getDocumentationConfig().getProperty("active");  
  
    String docboui = request.getParameter("parent_boui");
    String parent_attribute = request.getParameter("parent_attribute");    
    long docBoui = netgest.utils.ClassUtils.convertToLong(docboui,-1);
  
    boObject oDocument = null; 
    if ( docBoui != -1 )
    {
        oDocument = DOC.getObject( docBoui );      
    }    
         
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>

<script language="javascript">
function markChecked( gnumber, type, bridge){        
    var grid=document.getElementById("g"+gnumber+"_body");    
    var rows=grid.rows;
    var b;
    var rCheck=0;
    if( rows[0].cells[0].firstChild && rows[0].cells[0].firstChild.tagName=='IMG' ){
        rCheck=1;      
    }           

    for ( var i=0 ; i < rows.length ; i++ ){    
        if(rows[i].cells[rCheck].firstChild && rows[i].cells[rCheck].firstChild.checked  ) {            
            b = rows[i].id.split("__");
            if ( b[2] ) {                
                if("undoCheckOut" == type){
                    createHiddenInput(type + "__" + b[1] + "__" + b[2] , b[2]);
                }
            }
        }
    }              

    xml=parent.parent.boForm.BuildXml(false,false);      
    boFormSubmit.boFormSubmitXml.value=xml;    
    boFormSubmit.submit();                         
    if("checkIn" == type){                     
        winmain().openDoc('fixed,450px,210px,noresize',null,'','','lookup','attachfile.jsp?docid=<%=IDX%>&checkIn=check&look_parentObj=Ebo_Document&look_parentAttribute=<%= bridge %>&look_parentBoui=<%= oDocument.getBoui() %>&clientIDX='+getIDX()+'');
    }else if("undoCheckOut" == type){           
        if("details" == bridge){
            boForm.executeMethod(type);         
        }else{
            boForm.executeStaticMeth('netgest.bo.impl.document.DocumentHelper.' + type ,['this']);
        }        
        
    }
        
}
</script>
<%@ include file='boheaders.jsp'%>
<script><%=DOC.getScriptToRunOnClient()%></script>
<script>var objLabel='Lista de Documento';</script>

<body ondragenter="activethis()" onload="render();">

<form class="objectForm" name="boForm" id="<%=IDX%>" >  
    <% DocumentHelper.renderGrid(pageContext,oDocument,DOC,bridge,active); %>
</form>

<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name=parent_attribute value='<%=parent_attribute%>' />
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
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
