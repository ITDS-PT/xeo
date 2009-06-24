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

    int IDX;
    int cvui;
  
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    
    
    String from=request.getParameter("refreshWindowPath");
    boolean explorer="true".equals(request.getParameter("explorer"));
    String objectName=request.getParameter("object");
    String pextendObject=request.getParameter("extendObject");
    String qboiu = request.getParameter("queryBoui");
    long queryBoui= netgest.utils.ClassUtils.convertToLong(qboiu,-1);
    String relatedClientIDX=request.getParameter("relatedIDX");
    String reference=request.getParameter("reference");
    String referenceFrame=request.getParameter("referenceFrame");
    String xmlFilter=request.getParameter("xmlFilter");
    if ( xmlFilter!= null && xmlFilter.equals("")) xmlFilter=null;
 //   String xmlf= java.net.URLDecoder.decode( xmlFilter ) ;
    
    boolean extendObject=true;
    if ( pextendObject!=null && pextendObject.equalsIgnoreCase("NO") )
    {
        extendObject=false;
    }
    
    
    String filterName="";
    String onlyObjects="";
    
    if ( xmlFilter != null )
    {
      ngtXMLHandler x=new ngtXMLHandler(  xmlFilter ) ;
      onlyObjects=x.getFirstChild().getAttribute("onlyObjects");
       
    }
    boObject oFilter=null;
    docHTML DOC = DOCLIST.getDOC(IDX);
    if ( queryBoui != -1 )
    {
        oFilter= DOC.getObject( queryBoui );
        oFilter.poolSetStateFull( DOC.poolUniqueId() );
        filterName=oFilter.getAttribute("name").getValueString();
        onlyObjects=oFilter.getAttribute("onlyObjects").getValueString();
    }
    boDefHandler bodef = null;
    boDefHandler[] subClasses = null;
    if(!"".equals(objectName)){
        bodef = boDefHandler.getBoDefinition( objectName );
        
        subClasses=bodef.getTreeSubClasses();
    }    
       
    
    
    idbolist=ctrl[1];
    
   
    String attributes;
   
    
    StringBuffer attributesStr = netgest.bo.userquery.queryBuilderHelper.getXMLAttributes(DOC , bodef , true , extendObject );
           
%>
<% if(!"".equals(objectName)){%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Query Builder</title>
<%@ include file='boheaders.jsp'%>

<script LANGUAGE="javascript" SRC="templates/form/std/jsObjects.js"></script>
<script LANGUAGE="javascript" SRC="queryBuilder/queryBuilder.js"></script>
<STYLE type="text/css">
@import url('ieThemes/0/global/ui-global.css');
@import url('ieThemes/0/explorer/parameters.css');
</STYLE>
<script>




var objLabel="";
var objDescription='';
var objectName="<%=objectName%>";
var _attributesXml ="<%=attributesStr%>";
	
<%
if ( oFilter != null ){    
        bridgeHandler qrys=oFilter.getBridge("details");
        

%>
            var _filterXML =
			"<filter object=\"<%=objectName%>\" onlyObjects=\"<%=onlyObjects%>\" name=\"teste\">"+
            <%
              String nullIgnore = "0";
              qrys.beforeFirst();
              while ( qrys.next() )
              {
                if("1".equals(qrys.getObject().getAttribute("nullIgnore").getValueString()))
                {
                    nullIgnore = "1";
                }
                else
                {
                        nullIgnore = "0";
                }
              
                %>
				"<query>"+
					"<join><%=qrys.getObject().getAttribute("joinQuery").getValueString()%></join><attribute><%=qrys.getObject().getAttribute("attributeName").getValueString()%></attribute><condition><%=qrys.getObject().getAttribute("operator").getValueString()%></condition><value><![CDATA[<%=qrys.getObject().getAttribute("value").getValueString() == null ? null : qrys.getObject().getAttribute("value").getValueString().replaceAll("'", "\\\\'")%>]]></value><question><%=qrys.getObject().getAttribute("question").getValueString()%></question><nullIgnore><%=nullIgnore%></nullIgnore><subquery><%=qrys.getObject().getAttribute("subFilter").getValueString()%></subquery>"+
				"</query>"+
              <%}%>
                "<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
				"<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
			"</filter>"
<%} else if ( xmlFilter!= null ){%>
        var _filterXML ='<%=xmlFilter == null ? null : xmlFilter.replaceAll("'", "\\\\'") %>';
<%} else{  %>
    var _filterXML =
			"<filter object=\"<%=objectName%>\" onlyObjects=\"\" name=\"<%=filterName%>\">"+
               "<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
				"<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
				"<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
				"<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
				"<query>"+
					"<join></join><attribute>_x_</attribute><condition></condition><value><![CDATA[]]></value><subquery></subquery>"+
				"</query>"+
            "</filter>"		
<%}   %>        			
var _att;
var _filter;

var _attDOC;
var _filterDOC;

//---------------------------------------------------------------------------------------------------------
function refresh()
{
	_attDOC	= new ActiveXObject("Microsoft.XMLDOM");
	_attDOC.async	= false;
	_attDOC.loadXML(_attributesXml);
	_att = _attDOC.selectSingleNode("*");
	
	_filterDOC	= new ActiveXObject("Microsoft.XMLDOM");
	_filterDOC.async	= false;
	_filterDOC.loadXML(_filterXML);
	_filter = _filterDOC.selectSingleNode("*");
	
	var query;
	for ( var i=0; i< _filter.childNodes.length; i++ )
	{
		query=_filter.childNodes[i];
	}

	/*
	var text=_filter.childNodes(0).selectSingleNode("value")
	var v=text.firstChild.nodeValue;
	
	var newQuery=_filterDOC.createElement("query");
	
	var oValue=_filterDOC.createCDATASection("....");
	newQuery.appendChild(oValue);
	_filter.appendChild(newQuery);

    */

}


//****


var oQB = null;
function window.onload()
{
	self.focus();
	oQB = new qb(true);
}

</script>

<BODY style="height:100%" scroll=no>

<div style='display:none' id='lov'>
<LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
<SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>

<SCRIPT language=javascript>

	var bChanged = false;
	
</SCRIPT>


<TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
<TBODY>

  <TR>
    <TD class=main colSpan=2>
      <DIV id=divWarning>
      <TABLE id=tblFind height="100%" cellSpacing=0 cellPadding=0 
        width="100%"><TBODY>
        <TR height=20>
          <TD>Valores Possiveis:</TD>
          <TD></TD>
          <TD>Valores Seleccionados:</TD></TR>
        <TR>
          <TD width="45%">
            <DIV class=objects style="OVERFLOW: auto; WIDTH: 100%; HEIGHT: 100%">
            
            <TABLE id=tblResults hideFocus onkeydown=listKeyDown(this) 
            ondblclick=itemDoubleClick() onfocus=setTableFocus(this) 
            onclick=clickItem(this) tabIndex=4 cellSpacing=0 cellPadding=2 
            width="100%">
              <TBODY>
              
             </TBODY></TABLE></DIV></TD>
          <TD align=middle width=60><BUTTON id=btnAppend 
            title="Add the selected record" style="WIDTH: 40px" disabled 
            onclick="appendSelected()" tabIndex=5>&gt;&gt;</BUTTON> 
            <P><BUTTON id=btnRemove title="Remove the selected record" 
            style="WIDTH: 40px" disabled onclick=removeSelected(); 
            tabIndex=6>&lt;&lt;</BUTTON> </P></TD>
          <TD>
            <DIV class=objects id=rtnObjList 
            style="OVERFLOW: auto; WIDTH: 100%; HEIGHT: 100%">

            <TABLE id=tblSelected hideFocus onkeydown=listKeyDown(this) 
            ondblclick=removeSelected() onfocus=setTableFocus(this) 
            onclick="clickItem( this )" tabIndex=7 cellSpacing=0 cellPadding=2 
            width="100%">
              <TBODY>

              </TBODY></TABLE>
            
            <TABLE class=inlineMsg id=tblNoRecords>
              <TBODY>
             <TR><TD class=inlineMsg align=middle>Não existem items seleccionados</TD></TR>
              </TBODY></TABLE>
              
              
                  
                  </DIV></TD></TR></TBODY></TABLE>
                  
                  
      
      </DIV>
      </TD></TR>
  <TR>
    <TD>&nbsp;</TD>
    <TD style="BORDER-TOP: #ffffff 1px solid" align=right><BUTTON id=butBegin 
      onclick=DLG_applychanges();>OK</BUTTON>&nbsp;<BUTTON id=cmdDialogCancel 
      onclick=DLG_cancel();>Cancela</BUTTON></TD></TR></TBODY></TABLE>

</div>
<div id=xxx2 style='height:100%;overflow:auto' class='parameterZone'>
<table height='100%' id=xxx1 id='parameterZone' cellpadding=0 cellspacing=0>
    <tr height='10px'>
		<td style="" height='10px'>
		    <table style="border:0px solid #CCCCCC;width:100%;">
                <colgroup>
                <col width="105px" style="padding-right:10px "/>
                <col />	 
				<tr>                    
					<td>&nbsp;</td>
                    <%
                        if(!explorer)
                        {
                    %>
                    <td><button onclick=" oQB.applyParam();">Aplicar Parâmetros de Pesquisa</button></td>
                    <%
                        }
                        else
                        {
                    %>
                    <td><button onclick="oQB.applyParamExplorer();">Aplicar Parâmetros de Pesquisa</button></td>
                    <%    
                        }
                    %>
				</tr>
                
		    </table>
		</td>
  </tr>
 <tr height='100%'>
	<td  valign=top style='padding:0px'>
		
          <table style="TABLE-LAYOUT: fixed;width:100%;" id="g1643_body" valign=top cellpadding="1px" cellspacing="0">
                <colgroup>
                <col width="120px" style="padding-right:10px "/>
                <col />                
          </table>   <!--END TABLE BODY  !-->
				
	</td>
  </tr>  
  <tr height='10px'>
		<td style="" height='10px'>
		    <table style="border:0px solid #CCCCCC;width:100%;">
                <colgroup>
                <col width="105px" style="padding-right:10px "/>
                <col />	 
				<tr>                    
					<td>&nbsp;</td>
                    <%
                        if(!explorer)
                        {
                    %>
                    <td><button onclick=" oQB.applyParam();">Aplicar Parâmetros de Pesquisa</button></td>
                    <%
                        }
                        else
                        {
                    %>
                    <td><button onclick="oQB.applyParamExplorer();">Aplicar Parâmetros de Pesquisa</button></td>
                    <%    
                        }
                    %>
				</tr>
                
		    </table>
		</td>
  </tr>
  <tr><td>&nbsp;</td></tr>
</table>
<span style='display:none'>
<script>document.write(createFieldDateTime(
            "01/10/2003T11:20:00" ,	// Value,
            "date12t",		// Name,
            "dateidt2",		// id,
            1,				// tabIndex,
			true,			//allowValueEdit,
            false,			// isDisabled ,
            true,			// isVisible ,
            ""		//onChange 
            ))
</script>
</span>

 <FORM name='boFormSubmit' action="" method='post'>
        <INPUT type='hidden' name='boFormSubmitXml' />
        <INPUT type='hidden' name='boFormLookup' />
        <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
        <INPUT type='hidden' name='boFormSubmitId' />
        <INPUT type='hidden' value='<%=from%>' name='refreshWindowPath' />
        <INPUT type='hidden' value='<%=objectName%>' name='object' />
        <INPUT type='hidden' value='<%=queryBoui%>' name='queryBoui' />
        <INPUT type='hidden' value='<%=relatedClientIDX%>' name='clientIDX' />
        <INPUT type='hidden' value='<%=reference%>' name='reference' />
        <INPUT type='hidden' value='<%=referenceFrame%>' name='referenceFrame' />
        <INPUT type='hidden' value='<%=explorer%>' name='explorer' />
        <INPUT type='hidden' value='<%=IDX%>' name='docid' />
        <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />        
    </FORM>
</div>    
</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
