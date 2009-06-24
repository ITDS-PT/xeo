<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
String lastEntriesBoql = null;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__gesdocclf_insertframe.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

int IDX;int cvui;
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
if(request.getParameter("objectBoui")!=null){
BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
long[] a_boui = {BOI.getBoui()};
currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
if(!currObjectList.haveBoui(a_boui[0]))
currObjectList.inserRow(a_boui[0]);}


 DOC.setMasterObject(BOI,masterdoc);
 DOC.getController().getNavigator().setRoot( BOI, masterdoc, request);
 
String document_boui=request.getParameter("document_boui");
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Classificação</title>
        <link rel="stylesheet" type="text/css" href="xeo.css"> 
    </head>

<%--

//String document_boui="91026999";
if(document_boui == null || document_boui.length() == 0 )
{
%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
        <title>Classificação</title>
        <link rel="stylesheet" type="text/css" href="xeo.css"> 
    </head>
    <body style="background-color:#FEF8E0">
        <table style="width:100%;" cellpadding="0" cellspacing="0" border="0">
            <COLGROUP/>
            <COL width='50px' /> 
            <COL />
            <tbody>
                <TR style="background-color:#FEF8E0" height='20px'>
                    <TD colspan=2 nowrap style="border-bottom:2 groove;font:8pt;font-family:Arial,Helvetica,sans-serif;" ><B>&nbsp;&nbsp;Classificador de documentos</B></TD>
                </TR>
                <TR>
                    <TD colspan=2 style='FONT-WEIGHT: bold; color:#990000'>&nbsp;</TD>
                </TR>
                <TR>
                    <TD>&nbsp;</TD>
                    <TD style='FONT-WEIGHT: bold; color:#990000'>Deverá seleccionar o documento que pretende classificar.</TD>
                </TR>
            </tbdoy>
        </table>
    </body>
</html>
<%
return;
}
--%>
<%

String parent_boui=request.getParameter("parent_boui");
StringBuffer sb = null;
String method=request.getParameter( "method" );
String inputMethod=request.getParameter( "method" );
String requestedBoui=request.getParameter( "boui" );
String toNew=request.getParameter("toNew");
String bouisToRemove=request.getParameter("bouisToremove");
String bouiAlterar=request.getParameter("bouiEditar");
String strDocTypeBoui = request.getParameter("docTypeBoui");
String strBouisChecked = request.getParameter("bouisChecked");
String tipoDocValue = request.getParameter("tipoDoc");
String tipoDocValue2 = null;
String operation = request.getParameter("operation");
ArrayList erros = new ArrayList();



boolean c = !DOC.haveErrors();
boolean u = "y".equalsIgnoreCase("toNew");

GesDocViewer viewer = GesDocViewer.gesDocByBoui(DOC, BOI.getBoui());

if( bouiAlterar == null || bouiAlterar.trim().length() == 0)
{
    viewer.setEditing( false );
}


if(document_boui != null && document_boui.length() > 0)
{
    viewer.setDocument(document_boui);
    tipoDocValue2 = viewer.getTipoDocValue(DOC);
}

if("clear".equalsIgnoreCase(request.getParameter("operation")))
{
    viewer.clear();
}
else if("adicionar".equalsIgnoreCase(request.getParameter("operation")))
{
    viewer.setValues(boctx);
    viewer.validate(boctx, erros);
    if(erros.size() == 0)
    {
        viewer.setClassification(DOC, boctx);
        %>
            <html>
                <head>        
                <script>
                    function init()
                    {
                        <%
                        if( bouiAlterar != null && bouiAlterar.length() > 0 && !"null".equals( bouiAlterar ) )
                        {
                        %>
                            parent.closeEditClassification();
                        <%   
                        }
                        else
                        {
                        %>
                            location.href='__gesdocclf_insertframe.jsp?method=new&object=GESDocRunClf&document_boui=<%=document_boui%>';
                        <%
                        }
                        %>
                    }
                </script>
                </head>
                <body onload='init();'>
                </body>
            </html>
        <%
        return;
    }
}
else if("remover".equalsIgnoreCase(request.getParameter("operation")))
{
    viewer.remove(DOC, boctx, bouisToRemove);
}
else if("editar".equalsIgnoreCase(request.getParameter("operation")))
{
    viewer.validate(boctx, erros);
    if(erros.size() == 0)
    {
        viewer.alterar(DOC, boctx, bouiAlterar);
    }
}
else if("tipoDoc".equalsIgnoreCase(request.getParameter("operation")))
{
    viewer.changeTipoDoc(boctx, tipoDocValue);
}

if(erros.size() == 0)
{
    
    if(strDocTypeBoui != null && !"".equals(strDocTypeBoui))
    {
        viewer.setClassBoui(boctx, strDocTypeBoui);
    }
    else if(!"editar".equalsIgnoreCase(request.getParameter("operation")))
    {
        viewer.setClassBoui(boctx, "");
    }
}

String redirectUrl = DOC.getController().getNavigator().processPathRequest( BOI, masterdoc, request);
if(!DOC.haveErrors() && BOI != null && BOI.getSendRedirect() == null && redirectUrl != null)
{
    BOI.setSendRedirect(redirectUrl);
}

if(BOI != null && BOI.getSendRedirect() != null){response.sendRedirect( BOI.getSendRedirect() + "&docid=" + IDX + "&myIDX=" + IDX  );BOI.cleanSendRedirect();return;}
if ( !(!DOC.haveErrors() && request.getParameter("toNew")!=null) ){ if( currObjectList != null ) currObjectList.first();
boObject docObj = null;
if(document_boui == null || document_boui.length() == 0)
{
    document_boui = String.valueOf(BOI.getAttribute("doc").getValueLong());
    docObj = BOI.getAttribute("doc").getObject();
}
else
{
    docObj = DOC.getObject(Long.parseLong(document_boui));
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Classificação</title>
<%= DOC.getController().getPresentation().writeCSS() %>
<%= DOC.getController().getPresentation().writeJS() %>
<%@ include file='boheaders.jsp'%>

<style type="text/css">
BODY
{
	BACKGROUND-COLOR: #fff8e0;
}
</style>

<script>
<%=DOC.getScriptToRunOnClient()%></script>
<script>var objLabel='Classificação';</script>
<% String scriptToRun=null;
if ( request.getParameter("boFormSubmitXml")!= null && ( request.getParameter("addToCtxParentBridge") != null || request.getParameter("relatedParentBridge") != null ))
 {
    String look_parentBoui      = request.getParameter("ctxParent");
    String look_parentAttribute = request.getParameter("addToCtxParentBridge");
    String clientIDX            = request.getParameter("relatedClientIDX");
    String relDocid             = request.getParameter("ctxParentIdx");
if ( look_parentAttribute== null )
 {
   look_parentAttribute = request.getParameter("relatedParentBridge");
   look_parentBoui      = request.getParameter("relatedParent");
   relDocid             = request.getParameter("relatedParentDocid");
 }
    docHTML xd=DOCLIST.getDocByIDX( netgest.utils.ClassUtils.convertToInt(relDocid),DOC.getEboContext());
    boObject obj=xd.getObject(Long.parseLong(look_parentBoui));
    StringBuffer toP=new StringBuffer();
     StringBuffer nameH = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    if(!xd.parentRefresh())
    {
        scriptToRun="updateLookupAttribute()";
    }
    else
    {
        scriptToRun="updateLookupAttribute(); callParentRefreshFrame()";
    }
 %>
<script language="javascript">

function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      if (  wDocfrms[z].id == "inc_<%=nameH%>" )
      {
  wDocfrms[z].contentWindow.submitGrid();
            return;
      }
      else
      {
         updateFrame(wDocfrms[z]);
      }
   }
}
 function updateLookupAttribute()
 {
   var windowToUpdate="<%=clientIDX%>";
   var w=parent.ndl[windowToUpdate];
    if(w)
    {
        var ifrm=w.htm;
       var xw;
           if ( ifrm.id == "frm$<%=clientIDX%>" )
           {
                updateFrame(ifrm);
           }
   }
 }
</script>
<%}%>
<% 
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("searchClientIdx") != null && BOI != null && BOI.exists()){
  String searchClientIdx=request.getParameter("searchClientIdx");
  scriptToRun="updateObj()";
%>
<script language="javascript">
function updateObj(){
  var w=parent.ndl[<%=searchClientIdx%>];
  if( w ){
      var ifrm=w.htm;
      var xw;
      ifrm.contentWindow.findframe.submitSelectOne2(<%=BOI.bo_boui%>);
  }
}
</script>
<%}%>
<% StringBuffer toP=new StringBuffer();
if ( request.getParameter("boFormSubmitXml")!= null && request.getParameter("actRenderObj") != null && ( BOI!=null && BOI.exists() ) ){
  String clientIDX=request.getParameter("actIdxClient");
  String actRenderObj=request.getParameter("actRenderObj");
  String actRenderAttribute=request.getParameter("actRenderAttribute");
  scriptToRun="updateLookupAttribute()";
 int actRenderDocid= netgest.utils.ClassUtils.convertToInt(request.getParameter("actRenderDocid"),-1);
 boObject obj=null;
 if ( actRenderDocid != -1 )
 {
 netgest.bo.dochtml.docHTML docr=DOCLIST.getDocByIDX( actRenderDocid, DOC.getEboContext() );
 if( docr!= null )
 {
 obj=docr.getObject(Long.parseLong(actRenderObj));
 }
 }
 else
 {
 obj=DOC.getObject(Long.parseLong(actRenderObj));
 }
 StringBuffer v= new StringBuffer( );
 StringBuffer nameH = new StringBuffer();
 AttributeHandler attr=null;
 if ( obj!= null )
 {
  attr= obj.getAttribute(actRenderAttribute);
  v= new StringBuffer( attr.getValueString() );
  StringBuffer id = new StringBuffer();
  boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(actRenderAttribute );
  nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( actRenderAttribute );
  id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( actRenderAttribute );
  netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(
        toP,
        obj,
        attr,
        v,
        nameH,
        id,
        1,
        DOC,
        attr.disableWhen(),
        !attr.hiddenWhen(),
        obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
        );
}
%>
<script language="javascript">
function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   toRet=false;
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      var xok=false;
      xw=wDocfrms[z].contentWindow.document.all;
      var toR=[];
      for(var j=0; j< xw.length; j++){
        if ( xw[j].id == "tblLook<%=nameH%>" ){
         toR[toR.length]=xw[j];
        }
      }
      for(var y=0; y < toR.length; y++){
            toR[y].outerHTML=toRender.innerHTML;
      }
         updateFrame(wDocfrms[z]);
   }
 }
  function updateLookupAttribute(){
   var windowToUpdate="<%=clientIDX%>";
   var xok=false;
   var w=parent.ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm;
       var xw;
          xw=ifrm.contentWindow.document.all;
          var toR=[];
          for(var z=0; z< xw.length; z++){
            if ( xw[z].id == "tblLook<%=nameH%>" ){
             toR[toR.length]=xw[z];
            }
          }
          for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                var xele=ifrm.contentWindow.document.getElementsByName('<%=nameH%>');
                for( var z=0; z<xele.length;z++)
                      xele[z].original=xele[0].value;
                xok=true;
          }
       if ( !xok ) updateFrame(ifrm);
   }
 }
</script>
<%}%>
<script>
var checkedBouis ='<%=strBouisChecked == null ? "":strBouisChecked%>';
function init()
{
<%if("tipoDoc".equalsIgnoreCase(request.getParameter("operation"))){%>
    parent.parent.window.location.reload();
<%}%>
}

function BuildXml(type)
{
    try 
    {
        
    }
    catch(e)
    {
        alert(e.message);        
    }
    return "";
}
function escolhaDocumento()
{
    boForm.docTypeBoui.value = document.getElementById('docType').returnValue;
    boForm.submit();
}
function getSURL()
{    

}

function buttonRemove()
{
    if(checkedBouis && checkedBouis.length > 0)
    {
        boForm.operation.value = 'remover';
        boForm.bouisToremove.value = checkedBouis;
        boForm.submit();
    }
    else
    {
        alert('Seleccione as classificações que pretende eliminar.');
    }
}

function buttonEditar()
{
    if(checkedBouis && checkedBouis.length > 0)
    {
        var l = checkedBouis.split(";");
        if(l.length == 1 || (l.length == 2 && l[1] == ""))
        {
            boForm.operation.value = 'editar';
            boForm.bouisChecked.value = checkedBouis;
            boForm.bouiEditar.value = checkedBouis;
            boForm.submit();
        }
        else
        {
            alert('Para editar deve seleccionar apenas uma classificação.');
        }
    }
    else
    {
        alert('Seleccione a classificação que pretende editar.');
    }
}

function buttonTipoDoc()
{
    if(document.getElementById('ebodocTipoDocumento_lovValue').value != "")
    {
        boForm.operation.value = 'tipoDoc';
        boForm.tipoDoc.value = document.getElementById('ebodocTipoDocumento_lovValue').value; 
        boForm.submit();
    }
    else
    {
        alert('Seleccione o tipo de documento.');
    }
}

function rowEdit()
{
    if(checkedBouis && checkedBouis.length > 0)
    {
        var l = checkedBouis.split(";");
        var eBoui;
        if(l[l.length-1] == "")
        {
            eBoui = l[l.length-2];
        }
        else
        {
            eBoui = l[l.length-1];
        }
        boForm.operation.value = 'editar';
        boForm.bouisChecked.value = checkedBouis;
        boForm.bouiEditar.value = eBoui;
        boForm.submit();
    }
    else
    {
        boForm.operation.value = 'clear';
        boForm.bouisChecked.value = checkedBouis;
        boForm.submit();
    }
}

function keyPressed(e, buttonName)
{
    if(e)
    {
        var keycode = e.keyCode;
        if(keycode == 32 || keycode == 13)
        {
             sendPage(buttonName);
        }
    }
}
function mouseDown(e, buttonName)
{
    sendPage(buttonName);
}
    
function sendPage(buttonName)
{
    boForm.operation.value=buttonName;
    boForm.submit();
    wait();
}
    
function setPageFocus(fieldName)
{
    loadField(fieldName);
    treatFocus();
}
    
function runBeforeMethodExec(methodName)
{
}
function runAfterMethodExec()
{
    setLastMethodToRun("");
}

</script>

<%if (scriptToRun==null) {%>
<body ondragenter="activethis()" onload=" init();" bgcolor="Blue"  scroll="no">
<%}
else{%>
<body ondragenter="activethis()" onload=" init();" margin-right='0px' bgcolor="Blue" scroll="no"   >
<%
}
%>
<% if(!(request.getParameter("toClose") != null && !DOC.haveErrors())) {%>
    
<table style="width:100%;height:100%" cellpadding="0" cellspacing="0" border="0">
<%if(erros.size() > 0)
{
%>
    <TR>
        <TD class='error'>
            <div class='error'>
                <span style='font:13px'>&nbsp;&nbsp;<b>Corrija os seguintes erros:</b></span><br>
                <%
                for (int i = 0; i < erros.size(); i++) 
                {
                %>
                    <%=(String)erros.get(i)%>
                <%
                }
                %>
            </div>
        </TD>
    </TR>
<%
}%>    
    <tr>
                                <TD width="100%" >
                                    <DIV style='overflow-y:auto;height:100%;width:100%'>
                                        <form name="boForm" id="<%=IDX%>" action='__gesdocclf_insertframe.jsp'>
                                            <INPUT type='hidden' name=controllerName value='BasicController' />
                                            <INPUT type='hidden' name=method value='edit' />
                                            <INPUT type='hidden' value='<%=IDX%>' name='docid' />
                                            <INPUT type='hidden' name=docTypeBoui value='<%=(strDocTypeBoui == null) ? "":strDocTypeBoui%>' />
                                            <INPUT type='hidden' name=boui value='<%=BOI.getBoui()%>' />
                                            <INPUT type='hidden' name=operation value='' />
                                            <INPUT type='hidden' name=bouisToremove value='' />
                                            <INPUT type='hidden' name=bouisChecked value='' />
                                            <INPUT type='hidden' name=bouiEditar value='<%=bouiAlterar%>' />

                                            <INPUT type='hidden' name='object' value='GESDocRunClf'/>
                                            <INPUT type='hidden' name=masterdoc value='true' />
                                            <INPUT type='hidden' name='tipoDoc' value='<%=tipoDocValue2 == null ? "":tipoDocValue2%>'/>
                                            <INPUT type='hidden' name=document_boui value='<%=document_boui%>'
                                            <%viewer.render(DOC.getController(),pageContext,IDX);%>
                                        </form>
                                    </DIV>
                                </td>
<%--
                                <td width="48%">
                                     <div style='border:1px solid #FAE500' width="100%" height="100%">
                                        <IFRAME id='docClassif' frameborder='0' src='<%="__gesdocclf_bridge.jsp?docid="+DOC.getDocIdx()+"&method=list&parent_boui="+BOI.getBoui()+"&document_boui="+document_boui+"&checkedbouis="+strBouisChecked+"&boql=select GESDocRunClf where 1=0"%>' width='100%' scrolling=no height='100%' tabindex='<%=DOC.getTabindex(DOC.IFRAME, BOI.getName(), String.valueOf(BOI.bo_boui), "lastMovements", DOCLIST)%>'></IFRAME>
                                     </div>
                                </td>
--%>                                
<%--

                            <%if(viewer.tipoDocFilled(DOC)){%>
                            <tr style="background-color:#FEF8E0" height="22px">
                              <td align="right" colspan=2> 
                                <table>
                                    <tr>
                                        <td width="50%">&nbsp;</td>
                                        <td width="150px">
                                            <%
                                            StringBuffer sb1 = new StringBuffer();
                                            viewer.renderAdicionarButton(sb1, viewer.isEditing() ? "Alterar":"Adicionar");
                                            out.print( sb1 );
                                            %>
                                        </td>
                                        <td width="150px">
                                            <button style='width:150px;height:18px' accesskey='E' onClick="buttonEditar()" tabIndex="<%=DOC.getTabindex(DOC.IFRAME, BOI==null? "GESDocClf" : BOI.getName(), String.valueOf(BOI==null?0:BOI.bo_boui), "buttonEdit" + String.valueOf(BOI==null?0:BOI.bo_boui), DOCLIST)%>"><u>E</u>ditar</button>
                                        </td>
                                        <td width="50%">&nbsp;</td>
                                    </tr>
                                </table>
                              </td>
                            </tr>
                            <%}%>
--%>                            
    </tr>
</table>
<%}%>
<FORM name='boFormSubmit' method='post' action='__gesdocclf_insertframe.jsp'>
<INPUT type='hidden' name=controllerName value='BasicController' />
<INPUT type='hidden' name=method value='edit' />
<INPUT type='hidden' name=boui value='<%=BOI.getBoui()%>' />
<INPUT type='hidden' name=docTypeBoui value='<%=(strDocTypeBoui == null) ? "":strDocTypeBoui%>' />
<INPUT type='hidden' name='boFormSubmitXml' />
<INPUT type='hidden' name='object' value='GESDocRunClf'/>
<INPUT type='hidden' name=masterdoc value='true' />
<INPUT type='hidden' name='boFormSubmitMode' />
<INPUT type='hidden' name='boFormSubmitId' />
<INPUT type='hidden' value='<%=IDX%>' name='docid' />
<INPUT type='hidden' value='_____gesdocclf__insert' name='boFormSubmitSecurity' />
</FORM></body></html><%
} else
{
%>
    <html>
        <head>        
        <script>
            function init()
            {    
                window.open("__acs_relatorio.jsp?bouiToPreview=<%=BOI.getBoui()%>&method=edit" );
                location.href='__gesdocclf_insertframe.jsp?method=new&object=GESDocRunClf&sucess=true';
            }
        </script>
        </head>
        <body onload='init();'>
        </body>
    </html>
<%
}
} finally {
boctx.close();DOCLIST.releseObjects(boctx);
}%>
