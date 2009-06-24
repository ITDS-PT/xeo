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

    if(boctx==null) 
    {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    int IDX;
    int cvui;
  
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    
    
    
    String sid=request.getParameter("sid");
    String object = request.getParameter("object");
    
    
    
    boDefHandler bodefObject = null; 
    
    if ( object != null && object.length() > 0 )
    {
        bodefObject = boDefHandler.getBoDefinition( object );
    }
    boDefAttribute[] attributes = new boDefAttribute[0]; 
    if ( bodefObject != null )
    {
        attributes = bodefObject.getAttributesDef();    
    }   
//    docHTML DOC = DOCLIST.getDOC(IDX);
    
//    for ( int i=0; i< attributes.length ; i++ )
//    {
//        attributes[i].getName();
//        attributes[i].getLabel();
//        attributes[i].getType();
//    }
//    idbolist=ctrl[1];
    
           
%>
<% if( true ){%>
<html>
<style>
 @import url('ieThemes/0/global/ui-global.css');
 @import url('ieLibrary/form/form.css');
 @import url('ieThemes/0/form/ui-form.css');
 @import url('xeoNew.css');
</style>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>WorkFlow Designer - Escolha de atributos a incluir no programa</title>

</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

</head>
<script>

var objectAttributes = [];
function init()
{
  
  var atrDef = [];
  <%
    for ( int i = 0 ; i< attributes.length ; i++ )
    { if( true ) 
    {%>objectAttributes[<%=i%>]=["<%=attributes[i].getName()%>","<%=attributes[i].getLabel()%>","<%=attributes[i].getType()%>"];

    <%}}%>
   var h = document.getElementById("attrs");
   for(var i=0;i<objectAttributes.length;i++)
   {
        var r = h.insertRow();
        r.height="20px";
        r.attribute=objectAttributes[i][0];
        var c = r.insertCell();
        var c = r.insertCell();
        c.className='gcell_std';
        var isChecked ="";
        /*
        for( var j=0 ; j<rMethods.length && isChecked ==""; j++ )
        {
            if( rMethods[j]==objectMethods[i] )
            {
                isChecked ="checked";
            }
        }
        */
        c.innerHTML ="<input type='checkbox' class='rad' "+isChecked+" />";
        var c = r.insertCell();
        c.className='gcell_std';
        c.innerHTML = objectAttributes[i][1];
        
        var c = r.insertCell();
        c.className='gcell_std';
        c.innerHTML = objectAttributes[i][0];
        
   }
   

  //parent.includeAttr_2("<%=sid%>",objectAttributes);
 
}

function choose()
   {
     var selectedObjects=[];
     var h = document.getElementById("attrs");
     
     for ( var i=0; i< h.rows.length ; i++ )
     {
        if ( h.rows[i].cells[1].firstChild.checked )
        {
              selectedObjects[selectedObjects.length]= objectAttributes[i];
        }
     }
     
     dialogArguments.includeAttr_2("<%=sid%>",selectedObjects);   
     window.close();
   }
</script>


<body scroll="no" onload="init()">


<table class='layout' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;color:#000000">
    <col width=35px/>
    <col width=100%/>
    <col width=100px/>
    <tr height=20px>
        <td colspan=3 class='docHeaderTit_stdSEL'>Seleccionar atributos para o programa</td>
    </tr>
    <tr height=25px>
        <td class='gh_std'></td>
        <td class='gh_std'>Descrição</td>
        <td class='gh_std'>Nome</td>
    </tr>
   <tr height=100%><td colspan=3>
        <div id='container' style='height:100%;width:100%;padding:0px;overflow:auto'>   
        <table id='attrs' class='layout' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#FFFFFF;color:#000000">
        <col width=5px/>
        <col width=30px/>
        <col width=100%/>
        <col width=100px/>
       </table>
        </div>
    </td>
    </tr>

    <tr style='height:25px;' class='docFooter_std'>
        <td colspan=3 ><center>
            <button onclick='choose();' >Confirmar</button> &nbsp; &nbsp; &nbsp; &nbsp;
             <button onclick='window.close()'>Cancelar</button></center>
        </td>
    </tr>
</table>

</div>

</body></html>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
