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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);

    String viewer = request.getParameter("viewer");
    String name = request.getParameter("name");
    
    boObject treeDef = null;
    if(name!=null)
    {
        docHTML_treeRuntime xtree=docHTML_treeServer.getTree(viewer);
        
        treeDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDef");
        String defName = xtree.p_bodef.getName();
        
        treeDef.getAttribute("name").setValueString(name);

        ngtXMLHandler def = xtree.p_treeDef;
        String path = def.getNodeName();
        def = def.getParentNode();
        while (!def.getNodeName().equals(defName)) 
        {
            path = def.getNodeName() + "." + path;
            def = def.getParentNode();
        }
        treeDef.getAttribute("viewer").setValueString(path);
              
        if(xtree.p_filterName!=null)
          treeDef.getAttribute("filter").setValueString(xtree.p_filterName);
        
        if(xtree.p_textUserQuery!=null)
          treeDef.getAttribute("userQuery").setValueString(xtree.p_textUserQuery.replaceAll("<","&lt;").replaceAll(">","&gt;"));
        
        treeDef.getAttribute("objectType").setValueString(defName);
        
        bridgeHandler treeCols = treeDef.getBridge("cols");
        for (int i = 0; i < xtree.p_cols.length; i++) 
        {
          boObject attDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDefAttribute");
          attDef.getAttribute("name").setValueString(xtree.p_cols[i].p_name);
          attDef.getAttribute("width").setValueLong(xtree.p_cols[i].getWidth());
          for (int j = 0; j < xtree.p_orders.length; j++) 
          {
            if( xtree.p_cols[i].p_name.equals(xtree.p_orders[j]))
            {
              attDef.getAttribute("order").setValueString(xtree.p_ordersDirection[j]);
              attDef.getAttribute("orderPos").setValueLong(j+1);
            }
          }
          
          treeCols.add(attDef.getBoui());
          treeDef.getUpdateQueue().add(attDef.getBoui(), boObjectUpdateQueue.MODE_SAVE);
        }

        bridgeHandler treeGroups = treeDef.getBridge("groups");
        for (int i = 0; i < xtree.p_groups.length; i++) 
        {
          boObject attDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDefAttribute");
          attDef.getAttribute("name").setValueString(xtree.p_groups[i].p_name);
          
          if(xtree.p_groups_order[i] == docHTML_treeRuntime.ORDER_ASC)
              attDef.getAttribute("order").setValueString("ASC");
          else
              attDef.getAttribute("order").setValueString("DESC");
          
          treeGroups.add(attDef.getBoui());
          treeDef.getUpdateQueue().add(attDef.getBoui(), boObjectUpdateQueue.MODE_SAVE);
        }
        
        treeDef.update();
    }
    
   %>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE>Gravar Definição de Listagem</TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <LINK href="templates/form/std/bo_form_std.css" type=text/css rel=stylesheet>
    <LINK href="templates/dialog/std/dialogs.css" type=text/css rel=stylesheet>
    <LINK href="bo_global.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
    <LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>
    
<script language="javascript">

	
	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}
  
</script>

<script>
var objLabel="Escolha Nome Para a Listagem";
var objDescription='Escolha Nome Para a Listagem';
</script>
</html>
<body>

<%
if(name==null)
{
%>
<form name="saveExport" id="exportList" method=post action="__saveTree.jsp">
  <INPUT  id=IDX value="<%=IDX%>" name=IDX type=hidden>
  <INPUT  id=viewer value="<%=viewer%>" name=viewer type=hidden>
  
  <table border="0" cellpadding="0" style="border-collapse: collapse" width="100%">
		<tbody>
		<tr>
			<td width="100%" colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td width="10%">&nbsp;</td>
			<td width="80%" colspan="2" class="objectForm" align="center">
        Insira um nome para definição: <input type="text" name="name" size="15" class="text">
			</td>
			<td width="10%">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" colspan="4" class="listHeaderBar" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" colspan="4">&nbsp;</td>
		</tr>
		<tr>
			<td width="50%" colspan="2" align="center"><BUTTON id=butBegin onclick=submit();>Gravar</BUTTON></td>
			<td width="50%" colspan="2" align="center"><BUTTON id=cmdDialogCancel onclick=cancel();>Cancelar</BUTTON></td>
		</tr>
	</table>
</form>
<%
}
else
{
%>

<form name="saveExport" id="exportList" method=post>
  <%
    String boui = ""+treeDef.getBoui();
  %>
  <table border="0" cellpadding="0" style="border-collapse: collapse" width="100%">
		<tbody>
		<tr>
			<td width="100%" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" class="objectForm" align="center" colspan="2">
        <font size="4" color='blue'>Gravação Concluida</font>
			</td>
		</tr>
		<tr>
			<td width="100%" class="listHeaderBar" colspan="2">&nbsp;</td>
		</tr>
		<tr>
			<td width="100%" colspan="2">&nbsp;</td>
		</tr>
		<tr>
      <td width="50%" align="center"><BUTTON id=cmdDialogCancel onclick="winmain().openDoc('medium','ebo_treedef','savedef','method=edit&boui=<%=boui%>');">Partilhar</BUTTON></td>
			<td width="50%" align="center"><BUTTON id=cmdDialogCancel onclick=cancel();>Fechar</BUTTON></td>
		</tr>
	</table>
</form>


<%
}
%>
</body>

</html>


<%

} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}

%>
