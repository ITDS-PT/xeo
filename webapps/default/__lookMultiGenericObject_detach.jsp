<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

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
//    docParameter attr;

    ctrl    = DOCLIST.processRequest(boctx);
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
    String validObjects          = request.getParameter("validObjects");
    if (options==null) options="";
    String waitingAttribute     = request.getParameter("waitingAttribute");
    String attributeValue       = request.getParameter("attributeValue");
    
    
    
    
    String jspName              = null;
    
    
    
    boolean acceptTemplates     = false;
    boolean acceptClasses       = false;
    
    docHTML_section sec;
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>
var objLabel='<img align="absmiddle" hspace=3  src="templates/form/std/search16.gif"> ';var objDescription='';</script>
<body onload="changeUrl();document.getElementById('TEXTSEARCH').focus()">
<form class="objectForm" name="boForm" id="<%=IDX%>">

   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
          <%
         
         
  
 // sec.setTitle("Procura de "+bodef.getLabel() );

  docHTML_sectionRow row; 
  
  String[] xobjects=validObjects.split(";");
  boDefHandler[] xobjs = null;
  if(xobjects.length == 1 && "".equals(xobjects[0])){
      xobjs = netgest.bo.def.boDefHandler.getAllBoDefinition();
 }else{
      xobjs = new boDefHandler[ xobjects.length ];
       for (int i = 0; i < xobjects.length ; i++) 
      {
          xobjs[i]= netgest.bo.def.boDefHandler.getBoDefinition( xobjects[i] );
      }
  }
  
  
  StringBuffer[] xInternal = new StringBuffer[ xobjs.length ] ;
  StringBuffer[] xExternal = new StringBuffer[ xobjs.length ] ;
  for (int i = 0; i < xobjs.length; i++) 
  {  
        xInternal[i] = new StringBuffer( xobjs[i].getName() );
        xExternal[i] = new StringBuffer( xobjs[i].getLabel());
  }
  bodef=boDefHandler.getBoDefinition(look_object);
  sec = DOC.getSection("lookupGeneric2"+waitingAttribute );
  if ( sec == null ) {
     sec = DOC.createSection("lookupGeneric"+waitingAttribute,"",false,2);
          
      Hashtable xattributes=new Hashtable();
      row=sec.addRow();
      row.addCell(
                docHTML_sectionField.newCombo(
                new StringBuffer("selTypeObject"),
                new StringBuffer("nametipo"),
                new StringBuffer("Objecto"),
                xInternal[0],
                xExternal,
                xInternal,
                acceptClasses,false,
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
                    new StringBuffer("findframe.wait();xurl= changeSURL(AUX_SURL);findframe.location.href=xurl+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                    ,"",xattributes
                    ) ;
    
                    
     StringBuffer x=new StringBuffer(
                 "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                 "\"+objectNameToSearch+\"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())");
                            
        
             xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer("Avançada"),
                            x)
                            ,null,null
                            ) ;
            
    
 }

  sec.render(pageContext,DOC,DOCLIST);


                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
                String __url = objdef.getBoName();
 

%>
<script>
var selectedObj="<%=xInternal[0]%>";
var histObj = new Array("GESTEMP_Query"
   ,"GESTEMP_JavaQuery"
   ,"GESTEMP_CampoNFormula"
   ,"GESTEMP_CampoNJava"
   ,"GESTEMP_CampoNManual"
   ,"GESTEMP_CampoNObjecto"
   ,"GESTEMP_CampoFormula"
   ,"GESTEMP_CampoJava"
   ,"GESTEMP_CampoManual"
   ,"GESTEMP_CampoObjecto"
   ,"GESTEMP_CampoObjecto"
   ,"GESTEMP_EMAILTexto"
   ,"GESTEMP_SMSTexto"
   ,"GESTEMP_Template"
   ,"GESTEMP_Campo");
var acceptt=false;
var acceptc=false;
var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchmulti';
var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchmulti';

<%
if ( jspName == null ) jspName=xInternal[0]+"_generallist.jsp";

%>


function changeacceptt( obj ){
    if ( obj.returnValue=="0") acceptt=true;
    else acceptt=false;
    changeUrl();
}


function changeacceptc( obj ){
    if ( obj.returnValue=="3") {
        acceptc=false;
           document.getElementById("selTypeObject").enable();
    }
    else if ( obj.returnValue=="2") {
        acceptc=true;
           document.getElementById("selTypeObject").enable();
    }
    else{
        acceptc=true;
        document.getElementById("selTypeObject").disable();
    }
    changeUrl(obj.returnValue);
}

function changeUrl()
{
    var url=(selectedObj+'_generallist.jsp').toLowerCase();
    var filter = "1=1";
    if(isHistoric(selectedObj)){ filter = "historico = '0'"; }
    var boql="select "+selectedObj+" where "+filter+" ORDER BY --ATT--";
    xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value);
    document.getElementById('queryUserButton').style.color="";
    document.getElementById('queryUserButton').xmlFilter=null;

}

function isHistoric(oName)
{
    for (var i=0; i< histObj.length ;i++)
    {
        if(oName == histObj[i])
        {
            return true;
        }
    }
    return false;
}


window.objectNameToSearch='<%=xInternal[0]%>';

function changeobject( obj ){
    selectedObj=obj.returnValue;
    objectNameToSearch=obj.returnValue;
    changeUrl();
}

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
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
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
          <td height="60%" width="100%" valign="top" >
        <%
                
                __url+="_general"+"list.jsp";
                String boql;
                boql="select "+xInternal[0].toString()+" where 0=1";
                
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>
                <IFRAME id=findframe src='' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>

            
          </td>
      </tr>
      <tr><td>
          <%
          sec=DOC.getSection("confirmlookup"+waitingAttribute );
          
          if(sec==null) {
             
              sec = DOC.createSection("confirmlookup"+waitingAttribute,"",false,0);              
    
              sec.setTitle("");
              
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
                <%__url=jspName.toLowerCase();%>
                <IFRAME id=resultframe src='<%=__url+"?docid="+IDX+"&object="+xInternal[0]+"&listmode=resultBridge&bouislist="+attributeValue+"&method=list&renderOnlyCardID=yes&menu=yes&waitingDetachAttribute="+waitingAttribute%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>
     
    
   </table>

</form>
    


<FORM name='boFormSubmit'  method='get'>
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
