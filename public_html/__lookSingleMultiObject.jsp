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
    //docParameter attr;
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_query           = request.getParameter("look_query");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String look_action          = request.getParameter("look_action");

    boDefHandler xParentDef   = boDefHandler.getBoDefinition(look_parentObj);
    boDefAttribute xatt       = xParentDef.getAttributeRef( look_parentAttribute );
  
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<base target="_self">
<%@ include file='boheaders.jsp'%>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>

var objLabel='<img align="absmiddle" hspace=3  src="templates/form/std/search24.gif"><%=xatt.getLabel()%> ';var objDescription='';</script>
<body onload="document.getElementById('TEXTSEARCH').focus()">
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
<form class="objectForm" name="boForm" id="<%=IDX%>">

   <table bgcolor="#EEEEEE" cellpading=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:70px;width="100%"' valign="top">
          <%
  docHTML_section sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,2);
  bodef=boDefHandler.getBoDefinition(look_object);
 // sec.setTitle("Procura de "+bodef.getLabel() );

  docHTML_sectionRow row; 
  
  boDefHandler[] xobjs      = xatt.getObjects();
  StringBuffer[] xInternal = new StringBuffer[ xobjs.length ] ;
  StringBuffer[] xExternal = new StringBuffer[ xobjs.length ] ;
  for (byte i = 0; i < xobjs.length; i++) 
  {
        xInternal[i] = new StringBuffer( xobjs[i].getName() );
        xExternal[i] = new StringBuffer( xobjs[i].getLabel());
  }
  
  Hashtable xattributes=new Hashtable();
  row=sec.addRow();
  row.addCell(
            docHTML_sectionField.newCombo(
            new StringBuffer("1"),
            new StringBuffer("idtipo"),
            new StringBuffer("Tipo "),
            xInternal[0],
            xExternal,
            xInternal,
            false,
            new StringBuffer("changeobject"),null,null
            ) 
            ,xattributes
            );
            
  row=sec.addRow();
  
  docHTML_sectionCell xcell=row.addCellMultiField();
  
  xcell.addField( 
                docHTML_sectionField.newText(
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("TEXTSEARCH"),
                new StringBuffer("Texto a Procurar"),
                new StringBuffer(),null,null,null),"100%",xattributes) ;

  
  xcell.addField( docHTML_sectionField.newButton(
                new StringBuffer("find"),
                new StringBuffer("Procura"),
                new StringBuffer("xurl= changeSURL(AUX_SURL);findframe.location.href=xurl+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                ,"",xattributes
                ) ;

    
//row.addCell(bodef.getAttributeRef("id"),xattributes);

  sec.render(pageContext,DOC,DOCLIST);
  
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
                String __url = objdef.getBoName();

%>
<script>
var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';
var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';
function changeobject( obj ){
    var url=(obj.returnValue+'_generallist.jsp').toLowerCase();
    var boql='select '+obj.returnValue+' where 1=1 ORDER BY --ATT--';
    xurl=url+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
    //findframe.location.href=xurl;
}
 
</script>

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
                
                __url+="_general"+"list.jsp";
                String boql;
                boql="select "+xInternal[0].toString()+" where 0=1";
                
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>
                 
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchone"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>

     
    
   </table>

</form>
    
<FORM name='boFormSubmit' action="<%if ( look_action==null ){%>lookupsingleupdate.jsp <%} else {%><%=look_action%><%} %>" method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
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
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

