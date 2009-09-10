<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.localized.*"%>
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
    String objectName=request.getParameter("object");
    String pextendObject=request.getParameter("extendObject");
    String qboiu = request.getParameter("queryBoui");
    long queryBoui= netgest.utils.ClassUtils.convertToLong(qboiu,-1);
    String relatedClientIDX=request.getParameter("relatedIDX");
    String reference=request.getParameter("reference");
    String referenceFrame=request.getParameter("referenceFrame");
    String xmlFilter=request.getParameter("xmlFilter");
    boolean explorer= "true".equalsIgnoreCase(request.getParameter("explorer"));
    boolean __list= "true".equalsIgnoreCase(request.getParameter("__list"));
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
	if (onlyObjects==null)onlyObjects="";
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
<script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=JSPMessages.getString("JSP_queryBuilder.1")%>';var objDescription='';
var objectName="<%=objectName%>";
var filterIsSaved = false;
var _attributesXml ="<%=attributesStr%>"
	//			"<attributes>"+
	//			"<name type=\"char\"><label>Assunto</label></name>"+
	//			"<description  type=\"char\"><label><%=JSPMessages.getString("JSP_queryBuilder.2")%></label></description>"+
	//			"<priority type=\"number\"><label><%=JSPMessages.getString("JSP_queryBuilder.3")%></label><lov><item><description><%=JSPMessages.getString("JSP_queryBuilder.4")%></description><value>1</value></item><item><description><%=JSPMessages.getString("JSP_queryBuilder.5")%></description><value>2</value></item></lov></priority>"+
	//			"<assignedQueue type=\"object.boObject\" relation=\"1\" ><label><%=JSPMessages.getString("JSP_queryBuilder.6")%></label><objects>Ebo_perf;workQueue;Ebo_Role;Ebo_Group</objects></assignedQueue>"+
	//			"<process type=\"object.process\" relation=\"1\"><label><%=JSPMessages.getString("JSP_queryBuilder.7")%></label></process>"+
	//			"<process.name type=\"char\" ><label><%=JSPMessages.getString("JSP_queryBuilder.8")%></label></process.name>"+
	//			"<numero type=\"number\" decimals=\"2\" ><label><%=JSPMessages.getString("JSP_queryBuilder.9")%></label></numero>"+
	//			"<data type=\"date\" ><label><%=JSPMessages.getString("JSP_queryBuilder.10")%></label></data>"+
	//			"<datahora type=\"datetime\" ><label><%=JSPMessages.getString("JSP_queryBuilder.11")%></label></datahora>"+
	//			"<workHistory type=\"object.duration\" relation=\"N\"><label><%=JSPMessages.getString("JSP_queryBuilder.12")%></label></workHistory>"+
	//			"</attributes>";
<%
if ( oFilter != null ){    
        bridgeHandler qrys=oFilter.getBridge("details");
        

%>
            filterIsSaved = true;            
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
					"<join><%=qrys.getObject().getAttribute("joinQuery").getValueString()%></join><attribute><%=qrys.getObject().getAttribute("attributeName").getValueString()%></attribute><condition><%=qrys.getObject().getAttribute("operator").getValueString()%></condition><value><![CDATA[<%=qrys.getObject().getAttribute("value").getValueString()%>]]></value><question><%=qrys.getObject().getAttribute("question").getValueString()%></question><nullIgnore><%=nullIgnore%></nullIgnore><subquery><%=qrys.getObject().getAttribute("subFilter").getValueString()%></subquery>"+
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
        var _filterXML ="<%=xmlFilter%>";
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
	oQB = new qb();
    for ( var i = 0 ; i < oQB.querys.length ; i++ )
	{
        if(oQB.querys[i].valid()){
            oQB.querys[i].htm.firstChild.firstChild.checked = true;
        }
    }
}

</script>

<BODY ondragenter="activethis()" >

<table cellpadding=0 cellspacing=0 style="height:100%;width:100%;" >
<% if ( queryBoui == -1 ) { %>

  <tr>
		<td  height='23px'>
            
        <table class="mnubar" id="mnuBar2" cellSpacing="0" cellPadding="0">
        <tbody>
        <tr>
          <td width="9"><img hspace="3" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18"></td>
          <td class="icMenu" noWrap>
            <span class="menuFlat" title="Guardar" onclick=oQB.save('<%= objectName %>');  tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_save.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.13")%></span>
			<!--<span class="menuFlat" title="Guardar e Fechar" onclick="oQB.saveClose();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_saveClose.gif" WIDTH="16" HEIGHT="16"> </span>-->
			<img src="templates/menu/std/mnu_hSpacer.gif" WIDTH="2" HEIGHT="17">
            <%
            if(!explorer)
            {
            	if(__list)
                {
                %>
                    <span class="menuFlat" title="Aplicar filtro com as linhas seleccionadas" onclick="oQB.applyList();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_filter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.14")%></span>
                    <span class="menuFlat" title="Remover filtro" onclick="oQB.applyList('remove');" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_removefilter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.15")%></span>
                <%
                }
                else
                {
                %>
                <span class="menuFlat" title="Aplicar filtro com as linhas seleccionadas" onclick="oQB.apply();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_filter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.14")%></span>
                <span class="menuFlat" title="Remover filtro" onclick="oQB.apply('remove');" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_removefilter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.15")%>o</span>
                <%
                }
            }
            else
            {
            %>
            <span class="menuFlat" title="Aplicar filtro com as linhas seleccionadas" onclick="oQB.applyToExplorer();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_filter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.14")%></span>
            <span class="menuFlat" title="Remover filtro" onclick="oQB.applyToExplorer('remove');" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_removefilter.gif" WIDTH="16" HEIGHT="16"><%=JSPMessages.getString("JSP_queryBuilder.15")%></span>
            <%
            }
            %>
		  </td>
		</tr></tbody></table>
        
        
		    
		</td>
        
  </tr>
  
  <tr>
    <td>
            <table style="align:absmiddle;width:100%;">
                <tr>
                <td colspan=3 align='left' style='color:#0000FF;font-size:10px'><i><%=JSPMessages.getString("JSP_queryBuilder.16")%></i></td>
                </tr>
		    </table>
        
    </td>
  </tr>
  
  <tr><td><br></td></tr>
  <% } %>
	<td valign=top height='20px'>
		<!--HEADER BEGIN !-->
		<table style='width:100%'>
			<tr>
				<td style='width:100%;border-bottom:1px solid #AAAAAA'>
                <span><b><%=JSPMessages.getString("JSP_queryBuilder.17")%></b></span>&nbsp;<input class='rad' onclick='changeExclude();' <%=onlyObjects.length()==0?"checked":""%>  id='selectedAllObject' type=checkbox />
                <span id="sayExclude" style='display:<%=onlyObjects.length()==0?"none":""%>'><font color="red"><%=JSPMessages.getString("JSP_queryBuilder.18")%></font></span>
				</td>
			</tr>
		<tr><td width='100%'>
          <form  id="objectsToSearch">
                
            <table id='tableToExclude' class="layout" cellpading=1 cellspacing=1 style='display:<%=onlyObjects.length()==0?"none":""%>' >
              <colgroup/>
              <col width="8"/>
              <col width="50"/>
              <col width="8"/>
              <col width="50"/>
              <col width="8"/>
              <col width="50"/>
              <col width="8"/>
              <col width="50"/>
              <col width="8"/>
              <col width="50"/>
              <col width="8"/>
              <col width="50"/>
              <%
                for( int j=0;j < subClasses.length+1 ;j+=6 )
                {
              %>
              <tr>
                 <%for(int z=0;z<6;z++){ %>
                 
                 <%if( j==0 && z==0)
                 { %>
                  <td ><input class='rad' onchange='markChange(1);' <%=onlyObjects.indexOf(bodef.getName()+";")==-1?"":"checked"%> id='<%=bodef.getName()%>' type=checkbox /> </td><td  ><%=bodef.getLabel()%></td>
                 <%}
                   else if ( j+z-1 < subClasses.length )
                   {
                   
                   %>
                   
                  <td ><input class='rad' onchange="markChange(1);" <%=onlyObjects.indexOf(subClasses[j+z-1].getName()+";")==-1?"":"checked"%>  id='<%=subClasses[j+z-1].getName()%>' type=checkbox /></td><td  ><%=subClasses[j+z-1].getLabel()%></td>                  
                   <%}
                   else{%>
                   <td>&nbsp;</td>
                   <td>&nbsp;</td>
                   <%}%>
                 <%} %>  
              </tr>
              
              <%
                }
              
              %>
              
              
            </table>
          
            
          </form>		
		</td>
		</tr>
        </table>
		<!--HEADER END !-->
		
	</td>
 </tr>
 <tr>
	<td height=100% align=top style='padding:20px'>
	
		<div style="padding:1px;border:1px solid #5A8CD7;width:100%;height:100%;overflow-x:auto">
			<table style="height:100%;width:100%;" class="g_std" cellSpacing="0" cellPadding="0" width="100%">
				 <tbody>
					  <tr height="25">
					    <td>
							<table id="g1642_body" onmouseover="so('g1642');onOver_GridHeader_std(event);" onmouseout="so('g1642');onOut_GridHeader_std(event);" onclick="so('g1642');onClick_GridHeader_std(event);" cellpading="2" cellspacing="0" style="height:25px" class="gh_std">
								<colgroup>
								<col width="18">
								<col width="2">
								<col width="50">
								<col width="100" />
								<col>
								<col width="2" />
								<col width="118" />
								<col width="2" />
								<col width="218" />
							
								<col width="2" />
								<col width="18" />
                                <col width="2" />
								<col width="18" />
								<col width="2" />
							    <col width="15" />
								<tbody>
									<tr>
									<td class="gh_std"><input id="g1643_check" class="rad" type="checkBox" id="checkBox4" name="checkBox4"></td>
									<td class="ghSep_std">&nbsp;</td>
									<td class="gh_std">&nbsp;</td>
									<td id="g$ExpanderParent" class="gh_std"><%=JSPMessages.getString("JSP_queryBuilder.19")%></td>
									<td id="g$AutoExpander" class="gh_std">&nbsp;&nbsp;</td>
									<td class="ghSep_std">&nbsp;</td>
									<td class="gh_std"><%=JSPMessages.getString("JSP_queryBuilder.20")%> </td>
									<td class="ghSep_std">&nbsp;</td>
									<td class="gh_std"><%=JSPMessages.getString("JSP_queryBuilder.21")%> </td>
									<td class="ghSep_std">&nbsp;</td>
									<td class="gh_std"></td>
                                    <td class="ghSep_std">&nbsp;</td>
									<td class="gh_std"></td>
									<td class="ghSep_std">&nbsp;</td>
									<td class="gh_std" width="14"><img src="templates/grid/std/ghRefresh.gif" width="13" height="13" /></td>
									</tr>
								</tbody>
							</table>
					   </td>
					</tr>
		 
					<tr>
						<td>
							<div id="grid$0$activitylist" class="gContainerLines_std">
									<table id="g1643_body" id="gridbody$0$activitylist" cellpadding="2" cellspacing="0" class="gBodyLines_std">
										<colgroup>
										<col width="20" >
										<col width="60">
										<col />
										<col width="120" />
										<col width="220" />
										<col width="20" />
                                        <col width="20" />
                                        <col width="1" />
		 
										 
													 
								  </table>   <!--END TABLE BODY  !-->
							</div> <!--END TABLE BODY CONTAINER  !-->
						</td>
					</tr>
				<tbody>
		  </table>
		</div>
		
	<td></tr>
	<tr>
		<td style="" height='10px'>
		    <table style="border:0px solid #CCCCCC;align:absmiddle;width:100%;">
				<tr>
					<td><button onclick='oQB.newQuery()'><%=JSPMessages.getString("JSP_queryBuilder.22")%></button></td>
					<td><button onclick='oQB.removeSelected()'><%=JSPMessages.getString("JSP_queryBuilder.23")%></button></td>
					<td style='width:100%'></td>
				</tr>
		    </table>
		</td>
	</tr>
	
	
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
          <TD><%=JSPMessages.getString("JSP_queryBuilder.24")%></TD>
          <TD></TD>
          <TD><%=JSPMessages.getString("JSP_queryBuilder.25")%></TD></TR>
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
             <TR><TD class=inlineMsg align=middle><%=JSPMessages.getString("JSP_queryBuilder.26")%></TD></TR>
              </TBODY></TABLE>
              
              
                  
                  </DIV></TD></TR></TBODY></TABLE>
                  
                  
      
      </DIV>
      </TD></TR>
  <TR>
    <TD>&nbsp;</TD>
    <TD style="BORDER-TOP: #ffffff 1px solid" align=right><BUTTON id=butBegin 
      onclick=DLG_applychanges();><%=JSPMessages.getString("JSP_queryBuilder.27")%></BUTTON>&nbsp;<BUTTON id=cmdDialogCancel 
      onclick=DLG_cancel();><%=JSPMessages.getString("JSP_queryBuilder.28")%></BUTTON></TD></TR></TBODY></TABLE>

</div>

<div style='display:none' id='lovParam'>
<LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
<SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>

<SCRIPT language=javascript>

	var bChanged = false;
	
</SCRIPT>


<TABLE style="WIDTH: 100%;" cellSpacing=0 cellPadding=8>
<TBODY>

  <TR>
    <TD class=main colSpan=2>
      <DIV id=divWarning>
      <TABLE id=tblQuestion height="100%" cellSpacing=0 cellPadding=0 
        width="100%"><TBODY>
        <TR height=20>
          <TD><%=JSPMessages.getString("JSP_queryBuilder.29")%></TD>
          <TD></TD>
        </TR>
        <TR>
          <TD width="100%">
            <input class='text' value="" id='questionValue' req=1 maxlength="3000" name = 'questionValue' tabindex='1'>
          </TD>
        </TR>
        <TR height=20>
          <TD><%=JSPMessages.getString("JSP_queryBuilder.30")%></TD>
          <TD></TD>
        </TR>
        <TR>
          <TD width="100%">
            <input class='text' value="" id='omissaoValue' req=1 maxlength="3000" name = 'omissaoValue' tabindex='1'>
          </TD>
        </TR>
      </TABLE>
      </DIV>
    </TD>
 </TR>
  <TR>
    <TD style="BORDER-TOP: #ffffff 0px solid" align=right>&nbsp;</TD>
    <TD style="BORDER-TOP: #ffffff 0px solid" align=right><BUTTON id=butBegin 
      onclick=DLG_applyParamchanges();><%=JSPMessages.getString("JSP_queryBuilder.27")%></BUTTON>&nbsp;<BUTTON id=cmdDialogCancel 
      onclick=DLG_cancel();><%=JSPMessages.getString("JSP_queryBuilder.28")%></BUTTON></TD></TR></TBODY></TABLE>

</div>    
 <FORM name='boFormSubmit' action="" method='post'>
        <INPUT type='hidden' name='boFormSubmitXml' />
        <INPUT type='hidden' name='boFormLookup' />
        <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
        <INPUT type='hidden' name='boFormSubmitId' />
        <INPUT type='hidden' value='<%=from%>' name='refreshWindowPath' />
        <INPUT type='hidden' value='<%=objectName%>' name='object' />
        <INPUT type='hidden' value='<%=queryBoui%>' name='queryBoui' />
        <INPUT type='hidden' value='<%=relatedClientIDX%>' name='clientIDX' />
<%if("y".equalsIgnoreCase(request.getParameter("toClose")))
{
%>
        <INPUT type='hidden' name='toClose' value='y'/>
<%} else {%>
        <INPUT type='hidden' name='toClose' />
<%}%>
        <INPUT type='hidden' value='<%=reference%>' name='reference' />
        <INPUT type='hidden' value='<%=referenceFrame%>' name='referenceFrame' />
        <INPUT type='hidden' value='<%=IDX%>' name='docid' />
        <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    </FORM>

</BODY></HTML>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
