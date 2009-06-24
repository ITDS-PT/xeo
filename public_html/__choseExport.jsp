<%@ page contentType="text/html;charset=utf8"%>
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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    
    boObject xobj=DOC.getObject( ClassUtils.convertToLong(request.getParameter("exportBoui")) );

    String[] availCols=null;
    String[] availColsKeys=null;


            Vector avc=new Vector();
            Vector avck=new Vector();
            
       Enumeration attrArray = xobj.getAllAttributes().elements();
        
        while(attrArray.hasMoreElements())
        {
            AttributeHandler attr =  (AttributeHandler)attrArray.nextElement();    
            
            if(attr.getName().indexOf(".")>0)
              continue;
            
            if(attr.getName().startsWith("PARENT"))
              continue;
            
            if(attr.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_STATEATTRIBUTE)
              continue;
              
              avc.add(attr.getDefAttribute().getLabel()  );
              avck.add(attr.getName()  );              

        }
        

            availCols = ( String[] )avc.toArray( new String[ avc.size() ] ) ;
            availColsKeys =( String[] ) avck.toArray( new String[ avck.size() ] );

    %>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE>Escolher Atributos a Exportar</TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <LINK href="templates/form/std/bo_form_std.css" type=text/css rel=stylesheet>
    <LINK href="templates/dialog/std/dialogs.css" type=text/css rel=stylesheet>
    <LINK href="bo_global.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
    <LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>
    
    <SCRIPT language=javascript>

	var bChanged = false;
	
	
	function applychanges()
	{
		if (bChanged)
		{
            if (tblSelected.rows.length==0)
            {
              alert("Tem que seleccionar mais que um atributo");
              
            }
            else
            {
                var selCols="";
                for ( i=0; i< tblSelected.rows.length; i++){
                  selCols+=tblSelected.rows[i].key+";";
                }
                boFormSubmit.attrPrint.value=selCols;  
                boFormSubmit.mode.value = choseExport.mode.value;
                boFormSubmit.submit();
            }
		}
    else
    {
      cancel();
    }
	}

	function exportAll()
	{
      boFormSubmit.mode.value = choseExport.mode.value;
      boFormSubmit.allAtt.value ="YES";  
      boFormSubmit.submit();
	}


  <%
    if(request.getParameter("mode")!=null)
    {
  %>
    function submitExport()
    {
      window.open('__exportObject.jsp?'+'IDX=<%=request.getParameter("IDX")%>'+'&mode=<%=request.getParameter("mode")%>'+'&allAtt=<%=request.getParameter("allAtt")%>'+'&exportBoui=<%=request.getParameter("exportBoui")%>'+'&attrPrint=<%=request.getParameter("attrPrint")%>');
    }
	<%}%>

	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}


function document.onselectstart()
{
	var s = event.srcElement.tagName;
	if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;
}

function document.ondragstart()
{
	event.returnValue = false;
}

	function window.onload()
	{
		
		setNavigationState();
	}
	
</SCRIPT>


<script>
var objLabel="Escolher Atributos";
var objDescription='Mova os atributos que estão á esquerda  para a direita, de modo a exportar esses atributos.';
</script>


<%
if(request.getParameter("mode")==null)
{%>
<BODY>
<%}
else
{
%>
<BODY onload="submitExport()">
<%}%>


<TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
  <TBODY>
  <TR>
    <TD align="center" colSpan=2 width="100%">
    <FORM name=choseExport>
	Exportar Para Formato : <select size="1" name="mode" id=mode>
	<option selected value="XML">XML</option>
	<option value="CSVC">CSV Separado por: (,)</option>
	<option value="CSVSC">CSV Separado por: (;)</option>
	</select>
</FORM>
    </TD>
  </TR>
  <TR>
    <TD class=main colSpan=2>
      <DIV id=divWarning>
      <TABLE id=tblFind height="100%" cellSpacing=0 cellPadding=0 
        width="100%"><TBODY>
        <TR height=20>
          <TD>Atributos Disponíveis:</TD>
          <TD></TD>
          <TD>Atributos a Exportar:</TD></TR>
        <TR>
          <TD width="45%">
            <DIV class=objects style="OVERFLOW: auto; WIDTH: 100%; HEIGHT: 100%">
            
            <TABLE id=tblResults hideFocus onkeydown=listKeyDown(this) 
            ondblclick=itemDoubleClick() onfocus=setTableFocus(this) 
            onclick=clickItem(this) tabIndex=4 cellSpacing=0 cellPadding=2 
            width="100%">
              <TBODY>
              
              <% for ( int i=0;i< availCols.length ; i++) {%>
              <TR key="<%=availColsKeys[i]%>">
                <TD class=listItem align=absmiddle><NOBR>&nbsp;<%=availCols[i]%> </NOBR></TD></TR>
              <%}%>
             
               
                  
             </TBODY></TABLE></DIV></TD>
          <TD align=middle width=60><BUTTON id=btnAppend 
            title="Adiciona a coluna seleccionada" style="WIDTH: 40px" disabled 
            onclick="appendSelected()" tabIndex=5>&gt;&gt;</BUTTON> 
            <P><BUTTON id=btnRemove title="Remove a coluna seleccionada" 
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
             <TR><TD class=inlineMsg align=middle>Não existem atributos seleccionados</TD></TR>
              </TBODY></TABLE>
              
              
                  
                  </DIV></TD></TR></TBODY></TABLE>
                  
                  
      <FORM name=boFormSubmit method=post action="__choseExport.jsp">
      <INPUT  id=IDX value="<%=IDX%>" name=IDX type=hidden>
      <INPUT  id="allAtt" value="" name="allAtt" type=hidden>
      <INPUT  id="mode" value="" name=mode type=hidden>
      <INPUT  id=exportBoui value="<%=xobj.getBoui()%>" name=exportBoui type=hidden>
      <INPUT  id=attrPrint value="" name=attrPrint type=hidden>
      </FORM>
      
      </DIV>
      </TD></TR>
  <TR>
    <TD style="BORDER-TOP: #ffffff 1px solid" colSpan=2 align=right>
      Exportar: &nbsp;
      <BUTTON id=butBegin onclick=exportAll();>Todos</BUTTON>
      &nbsp;
      <BUTTON id=butBegin onclick=applychanges();>Selecionados</BUTTON>
      &nbsp;
      <BUTTON id=cmdDialogCancel onclick=cancel();>Cancelar</BUTTON>
    </TD>
  </TR>
</TBODY></TABLE></BODY></HTML>
      

<%

} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}

%>
