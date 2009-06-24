<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory,netgest.bo.plugins.data.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="java.sql.*"%>

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
if(request.getAttribute("initPageRequest")==null && request.getParameter("notInit")==null ){    request.setAttribute("initPageRequest","Y")
;    initPage=true
;}else {    initPage=false;
}int IDX;int cvui;
boDefHandler bodef;
boDefAttribute atr;
String idbolist;
String[] ctrl;

    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    String lookText          = request.getParameter("lookupTextBoui");
    String lookTextName      = request.getParameter("lookTextNameId");
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String fieldID           = request.getParameter("fieldID");
    String look_query           = request.getParameter("look_query");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String myIDX                = request.getParameter("myIDX");
    String look_object          = "GESTEMP_LTextDetails";
    
    String searchString         = request.getParameter("searchString");
    
    long valueToPut=0;
    if ( lookText!= null )
    {
      //searchString = "where parent = "+lookText;
      searchString = "where 1=([select 1 from ogestemp_ltext$details where parent$ = "+lookText+" and child$ = boui])";
    }
    else if(lookTextName != null && lookTextName.length() > 0)
    {
      searchString = "where 1=([select 1 from ogestemp_ltext l,ogestemp_ltext$details ld where l.name = '"+lookTextName+"' and ld.child$ = ogestemp_ltextDetails.boui and l.boui = ld.parent$])";
    }
    else if(searchString == null || searchString.length() == 0)
    {
      searchString = "where parent = 0";
    }
    
    docHTML_section sec;
%>

<html> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%>
<script>
function setUserQuery(text, reference)
    {
        createHiddenInput("userQuery",text);
        if (  text != "<cleanFilter/>")
        {
            document.getElementById('queryUserButton').style.color="#990000";        
        }
        else
        {
            document.getElementById('queryUserButton').style.color="";
        }
        document.getElementById('queryUserButton').xmlFilter=text;
        findframe.location.href=SURL+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
    }
</script>
<script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';var objDescription='';</script>
<body onload="winmain().activeDocByIdx(<%=myIDX%>);document.getElementById('TEXTSEARCH').focus()">
<script language="javascript">
    function setUrlAttribute(href,attname,value) {
        var xqry = href.substring(href.indexOf("?")+1);
        var xargs = xqry.split("&");
        var fnd = false;
        for(var i=0;i<xargs.length;i++) {
            if(xargs[i].split("=")[0]==attname) {
                xargs[i] = attname+"="+encodeURIComponent(value);
                fnd = true;
            }
        }
        if(!fnd) xargs[xargs.length] = attname+"="+encodeURIComponent(value);
        return href.substring(0,href.indexOf("?")+1)+xargs.join("&"); 
        
    }
</script>
   
   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
<form class="objectForm" name="boForm" id="<%=IDX%>">
          <%
              sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,5);
              
              
              sec.setTitle("Procura texto livre:");
               
%>
</form>    

<%
              
              
              docHTML_sectionRow row; 
              row=sec.addRow();
            
              Hashtable xattributes=new Hashtable();
              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("Texto a Procurar"),
                            new StringBuffer(""),null,
                            new StringBuffer( 
                            "if(event.keyCode==13){findframe.wait();findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"
                            )
                            ,null),"100%",xattributes) ;
            
              
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                            new StringBuffer("findframe.wait();findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            ) ;  
  sec.render(pageContext,DOC,DOCLIST);

%>


          </td>
     </tr>

     <tr> 
          <td height="100%" width="100%" valign="top" >
                <%
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                String __url = "gestemp_ltextdetails_general"+"list.jsp";
                String boql;
                String boql0="select GESTEMP_LTextDetails " + searchString ;

                boql=java.net.URLEncoder.encode( boql0) ;
                String orderBy = java.net.URLEncoder.encode( "texto" ) ;
                __url=__url.toLowerCase();
           %>
                <script language="javascript">
                var AUX_SURL='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchone"%>';
                var SURL='<%=__url+"?docid="+IDX+"&&method=list&boql="+boql+"&listmode=searchone"%>';
                </script>
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&list_orderby=ID&listmode=searchone&controllerName="+DOC.getController().getName()%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>

     
    
   </table>
<FORM name='boFormSubmit' action="lookupupdatetext.jsp" method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='lookupTextId' value='<%=lookText%>' />
    <INPUT type='hidden' name='fieldID' value='<%=fieldID%>' />
    <INPUT type='hidden' name='look_parentObj' value='<%=look_parentObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=look_parentBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=look_parentAttribute%>' />
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
    if(initPage) {boctx.close();DOCLIST.releseObjects(boctx);}
}
%>

