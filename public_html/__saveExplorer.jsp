<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="netgest.bo.localized.*"%>

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
    String erro = null;
    Explorer e = null; 
    if(viewer == null)
    {
        //erro não existe nenhum explorador associado
        erro = JSPMessages.getString("saveExplorer.6");
    }
    else if((e  = ExplorerServer.getExplorer( viewer )) == null)
    {
        erro = JSPMessages.getString("saveExplorer.7");
    }

    if(erro != null)
    {
        %>
            <HTML>
                <HEAD>
                    <TITLE>Escolher Colunas</TITLE>
                    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
                </HEAD>
                <script>
                    var objLabel="Escolher Colunas";
                </script>
                <BODY>
                    <TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
                      <TBODY>
                            <TR>
                                <TD align='center'>
                                    <%=erro%>
                                </TD>
                            </TR>
                        </TBODY>
                    </table>
                </BODY>
            </HTML>
        <%
        return;
    }
    if(name!=null)
    {
        treeDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDef");
        String defName = e.p_bodef.getName();
        
        treeDef.getAttribute("name").setValueString(name);

        ngtXMLHandler def = e.p_treeDef;
        String path = def.getNodeName();
        def = def.getParentNode();
        try
        {
            while (!"xeoModel".equals(def.getNodeName())) 
            {
                path = def.getNodeName() + "." + path;
                def = def.getParentNode();
            }
        }catch(Exception __e)
        {
            path = "xeoModel.viewers.viewer.forms.form.explorer."+path;
        }
        treeDef.getAttribute("viewer").setValueString(path);
        String expName = e.getExplorerName() == null ? "":e.getExplorerName();
        int pos = -1;
        if((pos = expName.indexOf("--")) > 0)
        {
            expName = expName.substring(0, pos);
        }
        treeDef.getAttribute("expName").setValueString(expName);
              
        if(e.p_filterName!=null)
          treeDef.getAttribute("filter").setValueString(e.p_filterName);
        
        if(e.p_textUserQuery!=null)
          treeDef.getAttribute("userQuery").setValueString(e.p_textUserQuery.replaceAll("<","&lt;").replaceAll(">","&gt;"));
        
        treeDef.getAttribute("objectType").setValueString(defName);
        
        bridgeHandler treeCols = treeDef.getBridge("cols");
        for (int i = 0; i < e.getColumnsProvider().columnsSize(); i++) 
        {
          boObject attDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDefAttribute");
          attDef.getAttribute("name").setValueString(e.getColumnsProvider().getColumn(i).getName());
          attDef.getAttribute("width").setValueLong(e.getColumnsProvider().getColumn(i).getWidth());
          for (int j = 0; j < e.p_orders.length; j++) 
          {
            if( e.getColumnsProvider().getColumn(i).getName().equals(e.p_orders[j]))
            {
              attDef.getAttribute("order").setValueString(e.p_ordersDirection[j]);
              attDef.getAttribute("orderPos").setValueLong(j+1);
            }
          }
          
          treeCols.add(attDef.getBoui());
          treeDef.getUpdateQueue().add(attDef.getBoui(), boObjectUpdateQueue.MODE_SAVE);
        }

        bridgeHandler treeGroups = treeDef.getBridge("groups");
        for (int i = 0; i < e.getGroupProvider().groupSize(); i++) 
        {
          boObject attDef = boObject.getBoManager().createObject(boctx, "Ebo_treeDefAttribute");
          attDef.getAttribute("name").setValueString(e.getGroupProvider().getGroup(i).getName());
          
          if(e.getGroupProvider().getGroupOrder(i) == Explorer.ORDER_ASC)
              attDef.getAttribute("order").setValueString("ASC");
          else
              attDef.getAttribute("order").setValueString("DESC");
          
          treeGroups.add(attDef.getBoui());
          treeDef.getUpdateQueue().add(attDef.getBoui(), boObjectUpdateQueue.MODE_SAVE);
        }
        
        bridgeHandler shared = treeDef.getBridge("share");
        shared.add(boctx.getBoSession().getPerformerBoui());
        
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
<form name="saveExport" id="exportList" method=post action="__saveExplorer.jsp">
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
        <%=JSPMessages.getString("saveExplorer.1")%> <input type="text" name="name" size="15" class="text">
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
			<td width="50%" colspan="2" align="center"><BUTTON id=butBegin onclick=submit();><%=JSPMessages.getString("saveExplorer.2")%></BUTTON></td>
			<td width="50%" colspan="2" align="center"><BUTTON id=cmdDialogCancel onclick=cancel();><%=JSPMessages.getString("saveExplorer.3")%></BUTTON></td>
		</tr>
	</table>
</form>
<%
}
else
{
%>

<form name="saveExplorer" id="exportList" method=post>
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
        <font size="4" color='blue'><%=JSPMessages.getString("saveExplorer.4")%></font>
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
			<td width="50%" align="center"><BUTTON id=cmdDialogCancel onclick=cancel();><%=JSPMessages.getString("saveExplorer.5")%></BUTTON></td>
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
