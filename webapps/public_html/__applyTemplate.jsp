<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
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
    
    String look_object          = "Ebo_Template";
    String showNew              = "no";
    String clientIDX            = request.getParameter("clientIDX");
    String options              = request.getParameter("options");
    String myIDX                = request.getParameter("myIDX");
    String bouiToApplyTemplate  = request.getParameter("bouiToApplyTemplate");
    String searchString         = request.getParameter("searchString");
    searchString="";
    
    
    if ( options == null ) options="";
    docHTML_section sec;
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP TEMPLATES</title>
<%@ include file='boheaders.jsp'%><script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';var objDescription='';</script>
<body onload="winmain().ndl[<%=myIDX%>].htm.style.display='';document.getElementById('TEXTSEARCH').focus()">
<script language="javascript">
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
          <%
         // sec=DOC.getSection("lookup"+look_object+look_parentAttribute);
        //  if(sec==null)
        //  {
              sec = DOC.createSection("lookupApply"+bouiToApplyTemplate,"",false,5);
              bodef=boDefHandler.getBoDefinition(look_object);
              docHTML_sectionRow row; 
              row=sec.addRow();
            
              Hashtable xattributes=new Hashtable();
              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("Texto a Procurar"),
                            new StringBuffer(searchString),null,null,null),"100%",xattributes) ;
            
              
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                            new StringBuffer("findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            ) ;
                
                StringBuffer x=new StringBuffer(
                    "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                    look_object+"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())");
                    
                             
                    
                    
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer("Avançada"),
                            x)
                            ,null,null
                            ) ;

          //   }  
//row.addCell(bodef.getAttributeRef("id"),xattributes);

  sec.render(pageContext,DOC,DOCLIST);

%>


          </td>
     </tr>

     <tr> 
          <td height="100%" width="100%" valign="top" >
                <%
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                //String boql;
                boObject objToApply = DOC.getObject( ClassUtils.convertToLong(bouiToApplyTemplate) );
               StringBuffer boql_ = new StringBuffer("select "+look_object+" where masterObjectClass.name='"+ objToApply.getName()+"'");
              //  if ( searchString.length() > 0 )
              //  {
                String[] objs = objToApply.getBoDefinition().canCastTo();
                if(objs != null)
                {
                    for (int i = 0; i < objs.length; i++) 
                    {
                        boql_.append(" or")
                             .append(" masterObjectClass.name='")
                             .append(objs[i])
                             .append("'");
                    }
                }
                
                //  }
                
                String boql0=java.net.URLEncoder.encode( boql_.toString(), "UTF-8") ;
                
                
                
                
             /*   if (look_query == null || look_query.equals("") ) 
                {
                   boql="select "+look_object+" where 1=1";
                }
                else
                {
                   boql="select "+look_object+" where "+look_query;
                }*/
                String boql=boql0 ;
                __url=__url.toLowerCase();
              
           %>
                <script language="javascript">                
                var SURL='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone"%>';
                </script>
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&list_fulltext="+searchString+"&options="+options+"&method=list&boql="+boql0+"&listmode=searchone"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>

     
    
   </table>
    
<FORM name='boFormSubmit' action="__applyTemplateUpdate.jsp" method='get'>
    <INPUT type='hidden' name='selectedBouis' />
    
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' value='<%=myIDX%>'/>
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=bouiToApplyTemplate%>' name='bouiToApplyTemplate' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

