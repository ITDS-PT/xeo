<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.impl.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
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
      response.sendRedirect("login.jsp?returnPage=__document.jsp");
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
  String parent_boui = request.getParameter("parent_boui");
  String parent_attribute = request.getParameter("parent_attribute");    
  long docBoui = netgest.utils.ClassUtils.convertToLong(parent_boui,-1);
  String listmode = request.getParameter("listmode");
  String boql = request.getParameter("boql");
  
  boObject oDocument = null;
    
  if ( docBoui != -1 )
  {
      oDocument = DOC.getObject( docBoui );      
  }     


  String parameters = "parent_boui=" + (oDocument != null ? String.valueOf(oDocument.getBoui()) : "" )+"&method=list&parent_attribute=" + parent_attribute + "&docid=" + IDX;
  String fsd = parameters;
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>

<script language="javascript">

function createHiddenInput( name, value , iframe)
{
  var oInput = iframe.contentWindow.document.createElement( "input" );  
  oInput.name = name;
  oInput.id = oInput.name;
  oInput.type = "hidden";
  oInput.value = value;  
  iframe.contentWindow.document.boFormSubmit.appendChild( oInput );

}
function downloadDocument(gnumber, type, bridge) { 

    var iframe = document.createElement("IFRAME");        
	document.body.appendChild(iframe);  
    //iframe.src = "__download.jsp?parent_boui=<%=String.valueOf(oDocument.getBoui()) %>&method=list&parent_attribute=details&docid=<%=IDX%>";    
    iframe.src = "__document_download.jsp?<%=parameters%>";    

	iframe.onreadystatechange = function()
	{
        if( event.srcElement.readyState == 'complete')
		{   
            
            createHiddenInput("no_cache" , new Date().getTime(),iframe);            
            var grid=document.frames[0].document.getElementById("g"+gnumber+"_body");             
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
                        createHiddenInput(type + "__" + b[1] + "__" + b[2] , b[2],iframe);     
                    }
                }
             }                             
            xml=parent.boForm.BuildXml(false,false);        
            iframe.contentWindow.document.boFormSubmit.boFormSubmitXml.value=xml;                            
            iframe.contentWindow.document.boFormSubmit.submit();                                      
            if("details" == bridge){
                iframe.contentWindow.document.boForm.executeMethod(type);                          
            }else{                
                //iframe.contentWindow.document.boForm.executeStaticMeth('netgest.bo.document.DocumentHelper.' + type ,[bridge]);
                iframe.contentWindow.document.boForm.executeStaticMeth('netgest.bo.impl.document.DocumentHelper.' + type ,['this',bridge]);
            }
            
            //window.setTimeout("document.frames[0].location.href ='__document_list.jsp?<%=parameters%>",5000);   
            
            window.setTimeout("document.frames[0].location.href ='__document_list.jsp?parent_boui=<%=String.valueOf(oDocument.getBoui()) %>&method=list&parent_attribute=<%=parent_attribute%>&docid=<%=IDX%>'",5000);                
            
            //window.setTimeout("document.frames[0].location.reload();",5000);                
            iframe.onreadystatechange = null;               
       }      
    }    
}

function downloadFile(boui,type) {     
    
    parent.winmain().window.parent.window.logoutWindow = false;
    var iframe = document.createElement("IFRAME");        
	document.body.appendChild(iframe);  
    iframe.src = "__document_download.jsp?<%=parameters%>";    

	iframe.onreadystatechange = function()
	{
        if( event.srcElement.readyState == 'complete')
		{                          
            createHiddenInput("no_cache" , new Date().getTime(),iframe);
            createHiddenInput(type + "__Ebo_Document__" + boui , boui,iframe);     
            xml=parent.boForm.BuildXml(false,false);        
            iframe.contentWindow.document.boFormSubmit.boFormSubmitXml.value=xml;                            
            iframe.contentWindow.document.boFormSubmit.submit();                   
            iframe.contentWindow.document.boForm.executeMethod(type);     
            if(type == "checkOut"){
                window.setTimeout("document.frames[0].location.href ='__document_controls.jsp?parent_boui=<%=String.valueOf(oDocument.getBoui()) %>&method=list&parent_attribute=<%=parent_attribute%>&docid=<%=IDX%>'",5000);                            
            }
            iframe.onreadystatechange = null;
            window.setTimeout("parent.winmain().window.parent.window.logoutWindow = true;",5000000);
       }      
    }    
}
</script>
<frameset rows="100%"> 
<% //if("details".equals(parent_attribute)) {%>
  <!--<frame src="__document_list.jsp?<%=parameters%>" id='doclist$frm' frameBorder='0' scrolling='no' >  -->
<% //} else%>
<%if("versions".equals(parent_attribute)) {%>
    <frame src="__document_controls.jsp?<%=parameters%>" id='doclist$frm' frameBorder='0' scrolling='no'>
    <!--<frame src="__document_version.jsp?<%=parameters%>" id='doclist$frm' frameBorder='0' scrolling='no'>-->
<% } else {%>
    <frame src="__document_list.jsp?<%=parameters%>" id='doclist$frm' frameBorder='0' scrolling='no' >  
<% }%>    
</frameset>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name=parent_attribute value='<%=parent_attribute%>' />
    <INPUT type='hidden' name=parent_boui value='<%= parent_boui %>' />
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
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
