<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;
    String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);

    boObjectList styles = boObjectList.list(boctx,"SELECT Ebo_styleSheet WHERE 1=1");
    String viewer = request.getParameter("viewer");

    Explorer e=ExplorerServer.getExplorer(viewer);
    
    Vector b_cols = new Vector();
    for (int i = 0; i < e.getColumnsProvider().columnsSize();i++)
    {
      if(e.getColumnsProvider().getColumn(i).bridgeInd() >-1)
        b_cols.add(e.getColumnsProvider().getColumn(i));
    }
    
    String mode = request.getParameter("mode");
    String sep = request.getParameter("sep");
    String csvmode = request.getParameter("CSVMode");
    String headers = request.getParameter("headers");
    String xsl = request.getParameter("XSL");
    Hashtable cols = new Hashtable();
    if(csvmode!=null)
    {
      for (int i = 0; i < b_cols.size(); i++) 
      {
        ColumnProvider att = (ColumnProvider)b_cols.elementAt(i);
        String valAtt = request.getParameter("ATT_"+att.getName());
        String onChk = request.getParameter("CHK_"+att.getName());
        
        if((onChk!=null) && (onChk.equals("ON")))
          continue;
        
        if(valAtt!=null)
          cols.put(((ColumnProvider)b_cols.elementAt(i)).getName(), new Integer(valAtt));
      }
    }
    
    if(sep!=null)
      mode += "_"+sep+"_"+csvmode;


    String exp = "";
    if(mode!=null){
      exp = "docid="+IDX+"&viewer="+viewer+"&mode="+mode;
      e.p_maxCols = cols;
    }
    
    if(headers!=null)
      exp += "&headers="+headers;
    
    if(xsl!=null)
      exp += "&XSL="+xsl; 
   %>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE>Escolher Mode de Exportação</TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <LINK href="templates/form/std/bo_form_std.css" type=text/css rel=stylesheet>
    <LINK href="templates/dialog/std/dialogs.css" type=text/css rel=stylesheet>
    <LINK href="bo_global.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
    <LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>
    
<script language="javascript">

	function applychanges()
	{
    exportList.submit();
	}

	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}
  
  
  var inhibit = false;

  function validate(e) {
  		
  		var x = parseInt(e.value);
  		
  		if(isNaN(x) || x<1)
  		{
  			alert("Valor tem que ser maior que 0"); 
  			e.value=1;
  		}
  		else
	  		e.value= x;
  	}
  <%
    if(mode!=null)
    {
  %>
    function submitExport()
    {
        var iframe = winmain().document.createElement("IFRAME");
        winmain().document.body.appendChild( iframe );
        iframe.src = '__explorerList.jsp?'+'<%=exp%>';
        
      //window.showModelessDialog('__explorerList.jsp?'+'<%=exp%>');
      //winmain().open('__printTree.jsp?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','_blank');
      //winmain().openDocUrl('fixed,730px,450px','__explorerList.jsp','?<%=exp%>','lookup');
    }
	<%}%>
  
  function modeXML(){

    var tables = document.body.getElementsByTagName('table');
    tables[1].style.display='none';
    tables[1].disabled=true;
 <%		if(b_cols.size()>0)
      {
%>   
  	//so se houver bridges
  	tables[2].style.display='none';
  	tables[2].disabled=true;
    tables[3].style.display='none';
  	tables[3].disabled=true;
<%
      }
      if(styles.getRowCount()>0)
      {
%>
  var select = document.body.getElementsByTagName('span');
  select [0].style.display='inline';
  select [0].disabled=false;
<%}%>
  }
  
  function modeCSV(){

    var tables = document.body.getElementsByTagName('table');
    tables[1].style.display='inline';
    tables[1].disabled=false;
<%		if(b_cols.size()>0)
      {
%>  
		//so se houver bridges
	  tables[2].style.display='inline';
	  tables[2].disabled=false;
	  tables[3].style.display='inline';
	  tables[3].disabled=false;
<%
      }
      if(styles.getRowCount()>0)
      {
%>    
  var select = document.body.getElementsByTagName('span');
  select [0].style.display='none';
  select [0].disabled=true;
<%}%>
  }
  
  function attOn(){
	  var tables = document.body.getElementsByTagName('table');
	  tables[5].style.display='inline';
	  tables[5].disabled=false;
  }
  
  function attOff(){
	  var tables = document.body.getElementsByTagName('table');
	  tables[5].style.display='none';
	  tables[5].disabled=true;
  }

  function allOn(){
	  var tables = document.body.getElementsByTagName('table');
	  tables[6].style.display='inline';
	  tables[6].disabled=false;
  }
  
  function allOff(){
	  var tables = document.body.getElementsByTagName('table');
	  tables[6].style.display='none';
	  tables[6].disabled=true;
  }
  
  function checkAtt(e, pos){
  	var tables = document.body.getElementsByTagName('table');
    var inp=tables[5].getElementsByTagName('input');
    if(e.checked)
    {
      inp[pos].disabled=true;
    }
    else
    {
      inp[pos].disabled=false;
    }
  }

</script>

<script>
var objLabel="Escolher Modo de Exportação";
var objDescription='Escolha para que tipo de formato que exportar a listagem';
</script>

<%
if(mode==null)
{%>
<BODY>
<%}
else
{
%>
<BODY onload="submitExport();cancel();">
<%}%>
</HEAD>

<form name="export" id="exportList" method=post action="__chooseExplorerList.jsp">
  <INPUT  id=IDX value="<%=IDX%>" name=IDX type=hidden>
  <INPUT  id=viewer value="<%=viewer%>" name=viewer type=hidden>
  
  <table border="0" cellpadding="0" style="border-collapse: collapse" width="100%">
		<tbody>
		<tr>
			<td colspan="6" class="bar" align="left" width="100%"><font size="4">
			Escolha Formato</font></td>
		</tr>
		<tr>
			<td width="8%" class="objectForm" align="center">
        <input type="radio" value="XML" checked="" name="mode" id="mode" onactivate="modeXML()" class="radio" />
      </td>
      <td width="25%" class="radioLabel" align="left">XML</td>
			<td width="65%" class="label" align="left">
<%
      if(styles.getRowCount()>0)
      {
%>
        <span>Aplicar Folha de Estilos
				<select size="1" name="XSL" class="htmlBar">
				<option selected></option>
        
<%      
        styles.beforeFirst();
        while(styles.next())
        {
          boObject style = styles.getObject();
%>
          <option value="<%=style.getBoui()%>"><%=style.getAttribute("name").getValueString()%></option>
<%      }
      }
      else
      {
       out.print("&nbsp;"); 
      }      
%>
				
        
        </select></span>


			</td>
		</tr>
		<tr>
			<td width="8%" class="objectForm" align="center">
			<input type="radio" name="mode" name="mode" value="CSV" onactivate="modeCSV()" class="radio" /></td>
			<td width="89%" class="radioLabel" colspan="2" align="left">CSV</td>
		</tr>
	</tbody>
	</table>
	<table border="0" cellpadding="0" style="border-collapse:collapse;display:none" width="100%">
		<tbody>
		<tr>
			<td width="5%" align="center"> &nbsp;</td>
			<td width="15%" class="section" align="left"><font size="2">Separado por:</font></td>
			<td width="8%" class="radioLabel" align="right">
			(<b>;</b>)
			<input type="radio" name="sep" value="SC" checked="" onactivate="modeCSV()" class="radio" /></td>
			<td width="7%" class="radioLabel" align="right">
			(<b>,</b>)
			<input type="radio" name="sep" value="C" onactivate="modeCSV()" class="radio" /></td>
			<td>&nbsp;</td>
		</tr>
		<tr>
			<td width="5%" align="center"> &nbsp;</td>
			<td width="15%" class="section" align="left"><font size="2">Colocar Cabeçalho</font></td>
			<td class="radioLabel" align="right">
			Sim
			<input type="radio" name="headers" checked="" value="true" class="radio" /></td>
			<td width="7%" class="radioLabel" align="right">
			Não
			<input type="radio" name="headers" value="false" class="radio" /></td>
			<td>&nbsp;</td>
		</tr>		
	</tbody>		
	</table>
	
	<br>
<%		if(b_cols.size()>0)
      {
%>	


<table border="0" cellspacing="0" width="100%" disabled style="border-collapse:collapse;display:none">
		<tr>
			<td colspan="7">&nbsp;</td>
		</tr>
		<tr>
			<td colspan="7">&nbsp;</td>
		</tr>
		<tr>
			<td class="listHeaderBar" align="left" width="861" colspan="7"><font size="4">
			Atributos Multi-valores</font></td>
		</tr>
		<tr>
			<td align="left" width="861" colspan="7">&nbsp;</td>
		</tr>	    
		<tr>
			<td class="bar" align="left" width="861" colspan="7"><font size="3">Extracção de Valores</font></td>
		</tr>
		<tr>
			<td width="1%">&nbsp;</td>
			<td width="5%" class="radio" align="right">
			<input type="radio" name="CSVMode" checked value="11" onactivate="attOn();allOn();" class="radio" ></td>
			<td width="20%">Listas de Valores</td>
			<td width="5%" align="right" class="radio">
			<input type="radio" name="CSVMode" value="N1" onactivate="attOff();allOff()" class="radio" ></td>
			<td width="20%">Desnormalizado por Linhas</td>
			<td width="5%" align="right" class="radio">
			<input type="radio" name="CSVMode" value="1N" onactivate="attOn();allOff()" class="radio" ></td>
			<td width="49%">Desnormalizado por Colunas</td>
		</tr>
		</table>
		<table border="0" cellspacing="0" width="100%" disabled style="border-collapse:collapse;display:none">
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>    
      <tr>
				<td width="100%" class="bar" align="left" colspan="5"><font size="3">Escolha Nº Mínimo de Valores por Atributo</font></td>
			</tr>
      <tr>
        <td width="1%" height="100%">&nbsp;</td>
				<td width="20%" height="100%" class="label" align="left">
					<table width="100%" height="100%"  border="0" cellspacing="0">
<% 
      //imprimir labels de atributos
      for (int i = 0; i <b_cols.size();i++) 
      {
        ColumnProvider att = (ColumnProvider)b_cols.elementAt(i);
%>
            <tr>
							<td class="label" align="left"><%=att.getLabel()%></td>
						</tr>
<%
      }
      //**************
%>            
					</table>
				</td>
        <td width="5%" height="100%" class="center" align="left">
					<table width="100%" height="100%"  border="0" cellspacing="0">
						
<% 
      //imprimir text de atributos
      for (int i = 0; i <b_cols.size();i++) 
      {
        ColumnProvider att = (ColumnProvider)b_cols.elementAt(i);
%>            
            <tr>
							<td align="left"><input type="text" name="ATT_<%=att.getName()%>" value="1" size="4" class="text" onChange="validate(this);"></td>
						</tr>
<%
      }
      //**************
%>
            
					</table>
        </td>
        <td width="20%" height="100%" align="center">
					<table width="100%" height="100%"  border="0" cellspacing="0">
            
            <% 
      //imprimir check de atributos
      for (int i = 0; i <b_cols.size();i++) 
      {
        ColumnProvider att = (ColumnProvider)b_cols.elementAt(i);
%>            
						<tr>
							<td align="center"><input type="checkbox" name="CHK_<%=att.getName()%>" value="ON" size="4" class="checkbox" onclick="checkAtt(this,<%=i%>);" ></td>
							<td align="left">Todos os Valores</td>
						</tr>
<%
      }
      //**************
%>
            
					</table>        		
        </td>
        <td width="54%" height="100%">&nbsp;</td>	
			</tr>
			<tr>
				<td colspan="5">&nbsp;</td>
			</tr>      
		</table>
	<br>

<%
    }
%> 
	<table border="0" cellpadding="0" cellspacing="0" style="border-collapse: collapse" width="100%">
		<tr>
			<td width="43%" align="center"><BUTTON id=butBegin onclick=applychanges();>OK</BUTTON></td>
			<td width="57%" ><BUTTON id=cmdDialogCancel onclick=cancel();>Cancelar</BUTTON></td>
		</tr>
	</table>
</form>



</body>

</html>


<%

} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}

%>
