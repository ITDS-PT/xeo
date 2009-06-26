<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="oracle.jsp.runtime.*"%>

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
    
    String treeKey              = request.getParameter("treeKey");
    String selectedCols         = request.getParameter("selectedCols");
    String gridKey              = request.getParameter("gridKey");
    docHTML_treeRuntime  tree   = docHTML_treeServer.getTree( treeKey );
    docHTML_treeAttribute[] cols=null;
    docHTML_treeAttribute[] attrs=null; 
    String[] availCols=null;
    String[] availColsKeys=null;
    if ( selectedCols==null )
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
            
    }
    else
    {
        String xcols[]= selectedCols.split(";");
        tree.setCols( xcols );
        docHTML_treeServer.saveUserTree(bosession.getPerformerBoui(),tree);  
    
    }
    
    if ( tree == null ) 
    {
        String __url=OracleJspRuntime.toStr("error.jsp");
        __url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"__chooseColsToGroupGrid","objectName" } ,new String[] {OracleJspRuntime.toStr("treeKey"),OracleJspRuntime.toStr( treeKey ) } );
        out.clear();
        pageContext.forward( __url);
        return;

    }
    
    if ( selectedCols==null )
    {
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
    
    <SCRIPT language=javascript>

	var bChanged = false;
	
	
	function applychanges()
	{
		if (bChanged)
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
                boFormSubmit.submit();
            }
		}
        else
        {
         cancel();
        }

	
	}

	function cancel()
	{
		winmain().ndl[getIDX()].close();
	}


document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
document.ondragstart=function(){event.returnValue = false}
/*

	function window.onload()
	{
		
		setNavigationState();
	}
	*/
</SCRIPT>


<script>
var objLabel="Escolher Colunas";
var objDescription='Mova as colunas que estão á esquerda  para a direita, de modo a visualizar essas colunas.';
</script>


<BODY onload="setNavigationState();" >
<TABLE style="WIDTH: 100%; HEIGHT: 100%" cellSpacing=0 cellPadding=8>
  <TBODY>

  <TR>
    <TD class=main colSpan=2>
      <DIV id=divWarning>
      <TABLE id=tblFind height="100%" cellSpacing=0 cellPadding=0 
        width="100%" border="0"><TBODY>
        <TR height=20>
          <TD>Colunas Dispon쵥is:</TD>
          <TD></TD>
          <TD>Colunas Seleccionadas:</TD>
        </TR>
        <TR>
          <TD width="300px">
            <DIV class=objects style="OVERFLOW: auto; WIDTH: 300px; HEIGHT: 100%">
            
            <TABLE id=tblResults hideFocus onkeydown=listKeyDown(this) 
            ondblclick=itemDoubleClick() onfocus=setTableFocus(this) 
            onclick=clickItem(this) tabIndex=4 cellSpacing=0 cellPadding=2 
            width="100%" border="0">
              <TBODY>
              
              <% for ( int i=0;i< availCols.length ; i++) {%>
              <TR key="<%=availColsKeys[i]%>">
                <TD class=listItem align=absmiddle><NOBR>&nbsp;<%=availCols[i]%> </NOBR></TD>
              </TR>
              <%}%>
             
               
                  
             </TBODY>
             </TABLE>
             </DIV>
          </TD>
          <TD align=middle width="300px">
            <BUTTON id=btnAppend title="Adiciona a coluna seleccionada" style="WIDTH: 40px" disabled onclick="appendSelected()" tabIndex=5>&gt;&gt;</BUTTON> 
            <P><BUTTON id=btnRemove title="Remove a coluna seleccionada" style="WIDTH: 40px" disabled onclick=removeSelected(); tabIndex=6>&lt;&lt;</BUTTON> </P>
          </TD>
          <TD width="300px">
            <DIV class=objects id=rtnObjList style="OVERFLOW: auto; WIDTH: 300px; HEIGHT: 100%">

            <TABLE id=tblSelected hideFocus onkeydown=listKeyDown(this) 
            ondblclick=removeSelected() onfocus=setTableFocus(this) 
            onclick="clickItem( this )" tabIndex=7 cellSpacing=0 cellPadding=2 
            width="100%" border="0">
              <TBODY>
              
             <% for ( int i=0;i< cols.length ; i++) {%>
              <TR key="<%=cols[i].getName()%>">
                <TD class=listItem align=absmiddle><NOBR>&nbsp;<%=cols[i].getLabel()%> </NOBR></TD></TR>
              <%}%>
             
              
              </TBODY></TABLE>
            
            <TABLE class=inlineMsg id=tblNoRecords>
              <TBODY>
             <TR><TD class=inlineMsg align=middle>Não existem colunas seleccionadas</TD></TR>
              </TBODY></TABLE>
              
              
                  
                  </DIV>
            </TD>
          </TR>
        </TBODY></TABLE>
                  
                  
      <FORM name=boFormSubmit method=post>
      <INPUT  id=selectedCols value="" name=selectedCols type=hidden>
      <INPUT  id=treeKey value="<%=treeKey%>" name=treeKey type=hidden>
      <INPUT  id=gridKey value="<%=gridKey%>" name=gridKey type=hidden>
      </FORM>
      
      </DIV>
      </TD></TR>
  <TR>
    <TD>&nbsp;</TD>
    <TD style="BORDER-TOP: #ffffff 1px solid" align=right><BUTTON id=butBegin 
      onclick=applychanges();>OK</BUTTON>&nbsp;<BUTTON id=cmdDialogCancel 
      onclick=cancel();>Cancelar</BUTTON></TD></TR></TBODY></TABLE></BODY></HTML>
      

<%
}
else
{

%>


    <!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.0 Transitional//EN">
    <HTML><HEAD><TITLE>Escolher Colunas</TITLE>
    
    <META http-equiv=Content-Type content="text/html; CHARSET=UTF-8">
    
    <SCRIPT language=javascript src="bo_global.js"></SCRIPT>
   
    <SCRIPT language=javascript>
    
     function reloadGrid(w)
     {
       if(w)
       {
           var ifrm=w.document.getElementsByTagName('IFRAME');
           var xw;
           for(var i=0; i < ifrm.length ; i++)
           {
               if ( ifrm[i].contentWindow.gridKey && ifrm[i].contentWindow.gridKey=='<%=gridKey%>' )
               {
                  ifrm[i].contentWindow.reloadGrid(); 
                  break;
               }
               else
               {
                  reloadGrid(ifrm[i].contentWindow );
               }
           }
       }
       
     }
 
    function window.onload()
	{
        reloadGrid(winmain());       
        winmain().ndl[getIDX()].close();
	}
	    
	
    </SCRIPT>
    
    <BODY>
    </BODY>
    </HTML>
      



<%
}

} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
