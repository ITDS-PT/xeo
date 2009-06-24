<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
String look_o = request.getParameter("look_object");
if ( look_o.equalsIgnoreCase("boObject") )
{
%>
<jsp:forward page="__lookSingleGenericObject_detach.jsp"/>
<%
}
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
//    docParameter attr;
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String look_object          = request.getParameter("look_object");
    String look_query           = request.getParameter("look_query");
    String showNew              = request.getParameter("showNew");
    String clientIDX            = request.getParameter("clientIDX");
    String look_action          = request.getParameter("look_action");
    String options              = request.getParameter("options");
    if (options==null) options="";
    String waitingAttribute     = request.getParameter("waitingAttribute");
    String attributeValue       = request.getParameter("attributeValue");

    if ( options == null ) options="";
    bodef=boDefHandler.getBoDefinition(look_object);
    
    boolean isInterface=false;
    if ( bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface )
    {
      isInterface=true;
    }
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%>
<% if ( isInterface ){ %>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<% } %>

<script>
var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';var objDescription='';
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
<body onload="document.getElementById('TEXTSEARCH').focus()">

   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
          <%
  docHTML_section sec = DOC.createSection("lookup","",false,5);
  bodef=boDefHandler.getBoDefinition(look_object);
  docHTML_sectionRow row; 
  row=sec.addRow();

  Hashtable xattributes=new Hashtable();
  docHTML_sectionCell xcell=row.addCellMultiField();
  xcell.addField( docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("Texto a Procurar"),
                new StringBuffer(),null,null,null),"100%",xattributes) ;

                 if ( isInterface )
               { 
                            xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                            new StringBuffer("findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            ) ;
                } else
                { 
                            xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                            new StringBuffer("findframe.wait();findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            ) ;  
                }
  
                StringBuffer x=new StringBuffer(
                    "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                    look_object+"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())");
                    
                             
                    
                    
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer("Avancada"),
                            x)
                            ,null,null
                            ) ;
    
//row.addCell(bodef.getAttributeRef("id"),xattributes);

  sec.render(pageContext,DOC,DOCLIST);

%>


          </td>
     </tr>
     <!--início do código para seleccionar o tipo de objetos-->
     <%
     if(bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface)
     {
        boDefInterface bodefInterface = (boDefInterface)bodef;
        String[] interfObj = bodefInterface.getImplObjects();
     %>
         <tr>
            <td>
            <table style='width:100%'>
                <tr>
                    <td style='width:100%;border-bottom:1px solid #AAAAAA'>
                        <span><b>Procurar em todos os objectos</b></span>&nbsp;<input class='rad' onclick='changeExclude();' checked  id='selectedAllObject' type=checkbox />
                        <span id="sayExclude" style='display:none'><font color="red">Seleccione os objectos que deseja procurar</font></span>
                    </td>
                </tr>
                <tr>
                    <td width='100%'>
                    
                            <table id='tableToExclude' class="layout" cellpading=1 cellspacing=1 style='display:none' >
                                 <colgroup/>
                                  <col width="8"/>
                                  <col width="50"/>
                                  <col width="8"/>
                                  <col width="50"/>
                                  <col width="8"/>
                                  <col width="50"/>
                                <%
                                    boDefHandler implObjDef;
                                    boDefHandler[] subClasses;
                                    ArrayList objs = new ArrayList();
                                    for( int i=0;i < interfObj.length ;i++)
                                    {
                                        implObjDef = boDefHandler.getBoDefinition(interfObj[i]);
                                        objs.add(implObjDef);                                        
                                    }
                                        for( int i=0;i < objs.size() ;)
                                        {
                                        %>
                                        <tr>
                                        <%
                                            for(int z=0;z<3;z++, i++)
                                            { 
                                                if(i >= objs.size())
                                                {
                                                %>
                                                    <td>&nbsp;</td>
                                                <%
                                                }
                                                else
                                                {
                                                    subClasses = ((boDefHandler)objs.get(i)).getTreeSubClasses();
                                                    String subClassesName = ""; 
                                                    if(subClasses != null && subClasses.length > 0)
                                                    {
                                                        for(int j = 0; j < subClasses.length; j++)
                                                        {
                                                            subClassesName += subClasses[j].getName() + ";";
                                                        }
                                                    }
                                                %>
                                                    <td ><input subclasses='<%=subClassesName%>' class='rad' id='<%=((boDefHandler)objs.get(i)).getName()%>' type=checkbox /> </td><td  ><%=((boDefHandler)objs.get(i)).getLabel()%></td>
                                                <%
                                                }
                                            }
                                        %>
                                        </tr>
                                        <%
                                        }
                                    %>
                            </table>
                    </td>
                </tr>
            </table>
            </td>
         </tr>
     <%} %>  
     <!--fim do código para seleccionar o tipo de objetos-->

     <tr> 
          <td height="100%" width="100%" valign="top" >
                <%                
                
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                String boql;
                String boql0="select "+look_object+" where 0=1";;
                boql0=java.net.URLEncoder.encode( boql0,"UTF-8") ;
                if (look_query == null || look_query.equals("") ) 
                {
                   boql="select "+look_object+" where 1=1 ORDER BY --ATT-- ";
                }
                else
                {
                   boql=look_query;
                }
                boql=java.net.URLEncoder.encode( boql,"UTF-8") ;
                __url=__url.toLowerCase();
                
           %>
                <script language="javascript">
                var AUX_SURL='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone&waitingDetachAttribute="+waitingAttribute %>';
                var SURL='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone&waitingDetachAttribute="+waitingAttribute %>';
                
                </script>
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone&waitingDetachAttribute="+waitingAttribute%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>

     
    
   </table>
    
<FORM name='boFormSubmit' method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=waitingAttribute%>' name='waitingAttribute' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

