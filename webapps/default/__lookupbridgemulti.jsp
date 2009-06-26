<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
String look_o = request.getParameter("look_SourceObj");
if ( look_o.equalsIgnoreCase("boObject") )
{
%>
<jsp:forward page="__lookMultiGenericObject.jsp"/>
<%
}
boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

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

if(request.getAttribute("initPageRequest")==null && request.getParameter("notInit")==null ){
    request.setAttribute("initPageRequest","Y");
    initPage=true;
    }
else {
    initPage=false;}int IDX;int cvui;boDefHandler bodef;boDefAttribute atr;String idbolist;String[] ctrl;
   // docParameter attr;

    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String sourceObject         = request.getParameter("look_SourceObj");
    String destinationObj       = request.getParameter("look_DestinationObj");
    String showNew              = request.getParameter("showNew");
    String docId                = request.getParameter("docid");
    String clientIDX            = request.getParameter("clientIDX");
    String parentObj            = request.getParameter("look_parentObj");
    String sourceQuery          = request.getParameter("look_sourceQuery");
    String destinationQuery     = request.getParameter("look_destinationQuery");
    String sourceBoui           = request.getParameter("look_sourceBouiObj");
    String destinationBoui      = request.getParameter("look_destinationBouiObj");
    String sourceAttribute      = request.getParameter("look_sourceAttribute");
    String destinationAttribute = request.getParameter("look_destinationAttribute");
    String renderOnlyCardID     = request.getParameter("renderOnlyCardID");
    String canSelectRows        = request.getParameter("canSelectRows");
    String bolist_query         = request.getParameter("bolist_query");
    
    docHTML_section sec;
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%><script>
var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(destinationObj).getLabel()%> ';var objDescription='';</script>
<body>
<form class="objectForm" name="boForm" id="<%=clientIDX%>">

   <table cellpadding=3px cellspacing=0 style='height:"100%"'>
    <tr>
        <td style='height:40px;width="100%"' valign="top">
        </td>
    </tr>
     <tr> 
          <td height="60%" width="100%" valign="top" >
                <%
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(sourceObject);
                
                       
                
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp?";
                __url=__url.toLowerCase();
                
                __url+="docid="+IDX;
                __url+="&ctxParent="+destinationBoui;
                __url+="&addToCtxParentBridge="+destinationAttribute;
                __url+="&showSelectNone=no";
                __url+="&menu=no";
                __url+="&method=list";
                __url+="&parent_boui="+sourceBoui;
                __url+="&parent_attribute="+sourceAttribute;
                __url+="&listmode=searchmulti";                
                
                if  (renderOnlyCardID!=null)__url+="&renderOnlyCardID="+renderOnlyCardID; 
                if (bolist_query!=null) __url+="&bolist_query="+bolist_query;
                if (canSelectRows!=null) __url+="&canSelectRows="+canSelectRows;
                
                //__url=__url.toLowerCase();
                __url+="&showLines=no";
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>                  
        <IFRAME id=findframe src='<%=__url%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>
      <tr><td>
          <%
          sec=DOC.getSection("confirmlookup"+sourceObject+sourceAttribute);
          if(sec==null) {
             
              sec = DOC.createSection("confirmlookup"+sourceObject+sourceAttribute,"",false,0);
              bodef=boDefHandler.getBoDefinition(sourceObject);
    
              sec.setTitle("");
              docHTML_sectionRow row; 
              row=sec.addRow();
              Hashtable xattributes=new Hashtable();
              row.addCell( docHTML_sectionField.newButton(
                            new StringBuffer("confirm"),
                            new StringBuffer("Confirmar a lista"),
                            new StringBuffer("resultframe.submitBridge()")
                            ),null
                            ) ;

            }    


         sec.render(pageContext,DOC,DOCLIST);

    %></td></tr>
      <tr> 
          <td height="40%" width="100%" valign="top" >
                <%
               // netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                
        
                
                
                //String 
                __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                //String boql="select "+look_object+" where id=id"; 
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>
                 
                
                <IFRAME id=resultframe src='<%=__url+"?docid="+IDX+"&listmode=resultBridge&method=list&parent_object="+destinationObj+"&parent_boui="+destinationBoui+"&parent_attribute=getBridge("+destinationAttribute+")"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>

            
          </td>

      </tr>
     
    
   </table>

</form>
    

<FORM name='boFormSubmit' action="lookupmultiupdate.jsp" method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='look_parentObj' value='<%=destinationObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=destinationBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=destinationAttribute%>' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage){if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
