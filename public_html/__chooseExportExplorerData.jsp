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

    int IDX;int cvui;
    String idbolist;
    String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);    
    
    String treeKey             = request.getParameter("treeKey");
    String selectedCols        = request.getParameter("selectedCols");
    String treeKeyTemp         = request.getParameter("treeKeyTemp");
    String treeDef             = request.getParameter("treeDef");
    String mode                = request.getParameter("mode");
    String action              = request.getParameter("action");
    String pageName            = request.getParameter("pageName");
    
    Explorer  tree=null;
    if((treeKey!=null) && ((treeKeyTemp==null) || ((treeDef!=null) && (treeDef.equals("")) && (mode.equals("CHANGE_DEF")))))
    {
       Explorer origTree = ExplorerServer.getExplorer(treeKey);
       tree = ExplorerServer.copyTreeToTemp(origTree,DOC, DOCLIST, pageName );
       tree.setTextUserQuery(DOC.getEboContext(),  origTree.p_textUserQuery);
       treeKeyTemp = tree.p_key;
       tree.setExplorerFullText(origTree.p_textFullSearch);
       tree.setBouiUserQueryAndParams(origTree.p_bouiUserQuery, origTree.p_userParameters, origTree.p_userNullIgnore);
    }
    else if(treeKeyTemp!=null)
    {
      Explorer origTree = ExplorerServer.getExplorer(treeKey);
      tree = ExplorerServer.getExplorer( treeKeyTemp );
      tree.setTextUserQuery(DOC.getEboContext(),  origTree.p_textUserQuery);
      tree.setExplorerFullText(origTree.p_textFullSearch);
      tree.setBouiUserQueryAndParams(origTree.p_bouiUserQuery, origTree.p_userParameters, origTree.p_userNullIgnore);
      if(tree==null || mode.equals("CHANGE_DEF"))
      {
        tree = ExplorerServer.loadTree(boctx, Long.parseLong(treeDef), DOC, DOCLIST,pageName);
        treeKeyTemp = tree.p_key;
      }
    }

    if ( tree!=null && selectedCols!=null && !selectedCols.equals(""))
    { 
        String xcols[]= selectedCols.split(";");
        tree.setCols(boctx, xcols );    
    }
    
    ColumnProvider[] cols=null;
    ColumnProvider[] attrs=null; 
    String[] availCols= null;
    String[] availColsKeys= null;
    if ( selectedCols==null || selectedCols.equals(""))
    {

            boObjectList treeDefs = null;
            
            if(tree!=null)
            {
                cols  = tree.getCols();
                attrs = tree.getAttributes();
                
                Vector avc=new Vector();
                Vector avck=new Vector();
            
             
                int z=0;
                for (int i = 0; i < attrs.length ; i++) 
                {
                    boolean found=false;
                    for (int j = 0; j < cols.length ; j++) 
                    {
                        if ( cols[j].getName().equals( attrs[i].getName()) )
                        {
                            found=true;
                        }
                    }
                    if ( !found )
                    {
                        avc.add(attrs[i].getLabel()  );
                        avck.add(attrs[i].getName()  );
                    }
                    
                }
                availCols = ( String[] )avc.toArray( new String[ avc.size() ] ) ;
                availColsKeys =( String[] ) avck.toArray( new String[ avck.size() ] );
                
    
    
             /*   ngtXMLHandler def = tree.p_treeDef;
                String path = "";
                try
                {
                    path = def.getNodeName();
                    while (!def.getParentNode().getNodeName().equals("#document")) 
                    {
                        path = def.getNodeName() + "." + path;
                        def = def.getParentNode();
                    }
                }catch(Exception e){}*/
                
                String bouiStr = "" + bosession.getPerformerBoui();
                String name=tree.getExplorerName();
                treeDefs = boObjectList.list(boctx,"SELECT Ebo_treeDef WHERE ( share in ( SELECT Ebo_Perf.groups where boui="+bouiStr+") OR "+
                                                                              "share in ( SELECT Ebo_Perf.Roles  where boui="+bouiStr+") OR "+
                                                                              "share in ( SELECT Ebo_Perf.Queues  where boui="+bouiStr+") OR "+
                                                                              "share="+bouiStr+" OR "+
                                                                              "CREATOR="+bouiStr+""+
                                                                            ") AND expName='"+name+"'");
            }
            else
            {
                cols=new ColumnProvider[0];
                attrs=new ColumnProvider[0]; 
                availCols= new String[0];
                availColsKeys= new String[0];

                String bouiStr = "" + bosession.getPerformerBoui();
                treeDefs = boObjectList.list(boctx,"SELECT Ebo_treeDef WHERE ( share in ( SELECT Ebo_Perf.groups where boui="+bouiStr+") OR "+
                                                                              "share in ( SELECT Ebo_Perf.Roles  where boui="+bouiStr+") OR "+
                                                                              "share in ( SELECT Ebo_Perf.Queues  where boui="+bouiStr+") OR "+
                                                                              "share="+bouiStr+" OR "+
                                                                              "CREATOR="+bouiStr+""+
                                                                            ")");
            }
    
    %>

    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE>Escolher Colunas</TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <LINK href="templates/form/std/bo_form_std.css" type=text/css rel=stylesheet>
    <LINK href="templates/dialog/std/dialogs.css" type=text/css rel=stylesheet>
    <LINK href="bo_global.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
    <LINK href="templates/dialog/std/lookupdialog.css" type=text/css rel=stylesheet>
    <SCRIPT language=javascript src="templates/dialog/std/lookupdialog.js"></SCRIPT>
    <SCRIPT language=javascript src="xeo.js"></SCRIPT>
    
    <SCRIPT language=javascript>

	var bChanged = false;
	
	function applychanges(m)
	{
            if (tblSelected.rows.length==0)
            {
              alert("Tem que seleccionar mais que uma coluna");
              
            }
            else
            {
                var selCols="";
                for ( i=0; i< tblSelected.rows.length; i++){
                  selCols+=tblSelected.rows[i].key+";";
                }
                boFormSubmit.selectedCols.value=selCols;
                boFormSubmit.mode.value=m;
                boFormSubmit.submit();
            }
	
	}
  
  function changeDef(s)
	{
      boFormSubmit.mode.value="CHANGE_DEF";  
      boFormSubmit.treeDef.value= s.options[s.selectedIndex].value;  
      boFormSubmit.submit();
	
	}

	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}


document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
document.ondragstart=function(){event.returnValue = false}

</SCRIPT>


<script>
var objLabel="Escolher Condições de Exportação";
var objDescription='Mova as colunas que estão à esquerda  para a direita, de modo a visualizar essas colunas.';
</script>


<BODY onload="setNavigationState();" >
<TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
  <TBODY>

		<tr>
			<td width="10%" colSpan=1><font size="2"><b><%=JSPMessages.getString("ExportData.5")%></b></font></td>
      <td width="90%" colSpan=2 align='left'><%=(tree==null)?"":tree.p_filterName%>
				<select size="1" name="treeDef" class="htmlBar" onchange="changeDef(this)">
                                <%
                                String toWrite=JSPMessages.getString("ExportData.21");
                                String toWrite2=JSPMessages.getString("ExportData.22");
                                %>
				<option <%=(("".equals(treeDef))?"selected":"")%>><%=(tree==null)?toWrite:toWrite2%></option>
		
<%
      if(treeDefs.getRowCount()>0)
      {
%>
        
<%      
        treeDefs.beforeFirst();
        while(treeDefs.next())
        {
          boObject treeDefe = treeDefs.getObject();
%>
          <option <%=(((""+treeDefe.getBoui()).equals(treeDef))?"selected":"")%> value="<%=treeDefe.getBoui()%>"><%=treeDefe.getAttribute("name").getValueString()%></option>
<%      }
      }
      else
      {
       out.print("&nbsp;"); 
      }      
%>
    </select>
    </td>
    </tr> 
		<tr>
			<td width="10%" colSpan=1><font size="2" ><b><%=JSPMessages.getString("docHTML_treeView.466")%></b></font></td>
      <td width="90%" colSpan=2 align='left'>
<%
    if(tree!=null)
    {
        for (int i = 0; i < tree.getGroupProvider().groupSize(); i++) 
        {
          out.print("["+tree.getGroupProvider().getGroup(i).getLabel() + "]&nbsp;&nbsp;&nbsp;");
        }
    }
%>
      </td>
		</tr>
		<tr>
			<td width="10%" colSpan=1><font size="2" ><b><%=JSPMessages.getString("ExportData.6")%></b></font></td>
      <td width="90%" colSpan=2 align='left'><%=(tree==null)?"":tree.p_filterName%></td>
		</tr>   
		<tr>
			<td width="100%" colSpan=3 ><font size="2" ><b><%=JSPMessages.getString("ExportData.7")%></b>&nbsp;&nbsp;</font>
    
<%    
    if(tree!=null)
    {
        out.print("<button title='"+JSPMessages.getString("ExportData.8")+"' style='height:20px;width:62px;"+
        (tree.p_textUserQuery==null? "":"color:#990000;") +
        "' onclick=\"winmain().openDocUrl('medium','__queryBuilder.jsp','?docid="+DOC.getDocIdx()+"&relatedIDX='+getIDX()+'&object="+
        tree.p_bodef.getName()+"&referenceFrame=___chooseExplorerData&reference="+
        java.net.URLEncoder.encode( tree.p_key, "UTF-8" )+
        (tree.p_textUserQuery!=null?"&xmlFilter="+ java.net.URLEncoder.encode( tree.p_textUserQuery, "UTF-8" ):"") +"','lookup')\">"+JSPMessages.getString("ExportData.9")+"</button>");
    }
%>
</td>
		</tr>      
  <TR>
    <TD class=main colSpan=2>
      <DIV id=divWarning>
      <TABLE id=tblFind height="100%" cellSpacing=0 cellPadding=0 
        width="100%"><TBODY>
        <TR height=20>
          <TD><%=JSPMessages.getString("ExportData.10")%></TD>
          <TD></TD>
          <TD><%=JSPMessages.getString("ExportData.11")%></TD>
        </TR>
        <TR>
          <TD width="300px">
            <DIV class=objects style="OVERFLOW: auto; WIDTH: 300px; HEIGHT: 100%">
            
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
          <TD align=middle width=300px><BUTTON id=btnAppend 
            title="<%=JSPMessages.getString("ExportData.19")%>" style="WIDTH: 40px" disabled 
            onclick="appendSelected()" tabIndex=5>&gt;&gt;</BUTTON> 
            <P><BUTTON id=btnRemove title="<%=JSPMessages.getString("ExportData.20")%>" 
            style="WIDTH: 40px" disabled onclick=removeSelected(); 
            tabIndex=6>&lt;&lt;</BUTTON> </P></TD>
          <TD width="300px">
            <DIV class=objects id=rtnObjList 
            style="OVERFLOW: auto; WIDTH: 300px; HEIGHT: 100%">

            <TABLE id=tblSelected hideFocus onkeydown=listKeyDown(this) 
            ondblclick=removeSelected() onfocus=setTableFocus(this) 
            onclick="clickItem( this )" tabIndex=7 cellSpacing=0 cellPadding=2 
            width="100%">
              <TBODY>
              
             <% for ( int i=0;i< cols.length ; i++) {%>
              <TR key="<%=cols[i].getName()%>">
                <TD class=listItem align=absmiddle><NOBR>&nbsp;<%=cols[i].getLabel()%> </NOBR></TD></TR>
              <%}%>
             
              
              </TBODY></TABLE>
            
            <TABLE class=inlineMsg id=tblNoRecords>
              <TBODY>
             <TR><TD class=inlineMsg align=middle><%=JSPMessages.getString("ExportData.12")%></TD></TR>
              </TBODY></TABLE>
              
              
                  
                  </DIV></TD></TR></TBODY></TABLE>
                  
                  
      <FORM name=boFormSubmit method=post>
      <INPUT  id=docid value="<%=IDX%>" name=docid type=hidden>
      <INPUT  id=selectedCols value="" name=selectedCols type=hidden>
      <INPUT  id=treeKey value="<%=treeKey%>" name=treeKey type=hidden>
      <INPUT  id=treeKeyTemp value="<%=treeKeyTemp%>" name=treeKeyTemp type=hidden>
      <INPUT  id=treeDef value="<%=treeDef%>" name=treeDef type=hidden>
      <INPUT  id=mode value='' name=mode type=hidden>
      <%if(action!=null){%>
        <INPUT  id=action value="<%=action%>" name=action type=hidden>
      <%}%>
      
      </FORM>
      
      </DIV>
      </TD></TR>
    <TR>
    <TD style="BORDER-TOP: #ffffff 1px solid" align=center>
      <%if(action==null){%>
        <BUTTON id=butBegin onclick="applychanges('Save')"><%=JSPMessages.getString("ExportData.14")%></BUTTON>&nbsp;&nbsp;
      <%}%>    
    </TD>
    <TD style="BORDER-TOP: #ffffff 1px solid" align=right>
      <%if((action==null) || (action!=null && action.equals("imprimir"))){%>
        <BUTTON id=butBegin onclick="applychanges('Imprimir')"><%=JSPMessages.getString("ExportData.15")%></BUTTON>&nbsp;&nbsp;
      <%}%>
      <%if((action==null) || (action!=null && action.equals("exportar"))){%>
        <BUTTON id=butBegin onclick="applychanges('Exportar')"><%=JSPMessages.getString("ExportData.16")%></BUTTON>&nbsp;&nbsp;
      <%}%>
        <BUTTON id=cmdDialogCancel  onclick=cancel();><%=JSPMessages.getString("ExportData.17")%></BUTTON>
    </TD></TR></TBODY></TABLE></BODY></HTML>
      

<%
}
else
{
%>


    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE><%=JSPMessages.getString("ExportData.18")%></TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
   
    <SCRIPT language=javascript>
    
  function window.onload()
	{
<%
        //Exportação de Listas
        if(mode.equals("Exportar"))
          out.print("winmain().openDocUrl('fixed,730px,450px','__chooseExplorerList.jsp','?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','lookup');\n");
          
        //Impressão de Listas
        if(mode.equals("Imprimir"))
            out.print("winmain().openDocUrl('medium','__printExplorer.jsp','?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','lookup');\n");
          //out.print("winmain().open('__printExplorer.jsp?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','_blank');\n");

        //gravar definições
        if(mode.equals("Save"))
          out.print("winmain().openDocUrl('fixed,400px,150px,noresize','__saveExplorer.jsp','?docid="+DOC.getDocIdx()+"&viewer="+tree.p_key+"','lookup');\n");          
          
          
%>
        winmain().ndl[getIDX()].close();
	}
	    
	
    </SCRIPT>
    
    <BODY>
    </BODY>
    </HTML>

<%        
        
}


} finally {
    if(initPage){if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
