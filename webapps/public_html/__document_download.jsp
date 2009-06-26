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
<%@ page import="netgest.utils.tools"%>

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
      response.sendRedirect("login.jsp?returnPage=__document_download.jsp");
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
  String parent_attribute = request.getParameter("parent_attribute");
     
    InputStream is =null;
    DocumentContainer docContainer = (DocumentContainer)request.getAttribute("docContainer");    
    if(docContainer != null && docContainer.getSize() != -1){
        response.setHeader("Cache-Control","private"); 
        ServletContext context = pageContext.getServletContext();
        
        String mimetype = context.getMimeType( docContainer.getFilename() );      
        Long FileSize = new Long(docContainer.getSize()); 
        int xfsize = FileSize.intValue();       
        
        response.setContentType(mimetype); 
        response.setContentLength(xfsize); 
        if (!docContainer.display())
            response.setHeader("Content-Disposition","attachment; filename=" + docContainer.getFilename());        
        
        int rb = 0; 
        ServletOutputStream so = response.getOutputStream();                 
        try {   
            is = docContainer.getInputStream();
            int c;
            while((c = is.read()) != -1)
            {
                so.write(c);  
            }
            is.close();   
            docContainer.release();
            so.close();  
           
        }catch (Exception e) {
            
        }        
        return; 
    }
%>
<style type="text/css">@import url('xeo.css');</style>
<script LANGUAGE="javascript" SRC="xeo.js"></script>
<script language="javascript">
function errorController(){

<% if(DOC.haveErrors() ) { 
    String error =  DOC.getHTMLErrors() ; 
    String[] aux = error.split("</span>");
    String desc = "";    
    for(int i = 0 ; i < aux.length-1 ; i++){
        String[] aux2 = aux[i].split(">");
        desc += aux2[aux2.length-1];
        desc +="</br>";
    }
    String msg = "Não pode executar a operação enquanto não resolver os seguintes erros:";     

%>        
    newDialogBox("critical",'<%=msg + "</br>" + desc %>',["Continuar"]," Atenção !");
    
<% } %>    
}
</script>
<html>
    <body onLoad = "errorController();">    
    <FORM class="objectForm" name="boForm" id="<%=IDX%>">        
    </FORM>
    <FORM name='boFormSubmit' method='post'>
        <INPUT type='hidden' name='boFormSubmitXml' />
        <INPUT type='hidden' name=parent_attribute value='<%= parent_attribute %>' />
        <INPUT type='hidden' name=parent_boui value='<%= docBoui %>' />
        <INPUT type='hidden' name=method value='list' /> 
        <INPUT type='hidden' name='boFormSubmitMode' />
        <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
        <INPUT type='hidden' name='boFormSubmitId' />
        <INPUT type='hidden' name='BOUI' value='<%= docBoui %>'/>
        <INPUT type='hidden' name='boiChanged' value='true' />
        <INPUT type='hidden' value='<%=IDX%>' name='docid' />    
        <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />    
    </FORM>
    </body>
</html>

<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>