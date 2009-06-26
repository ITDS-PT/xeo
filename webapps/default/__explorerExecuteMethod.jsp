<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.xwf.common.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.utils.DestroyBusinessObject"%>
<%@ page import="netgest.bo.security.securityRights"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>



<jsp:useBean id="DOCLIST"  scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);

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
    
    String[] selectBouis = null;
    String[] executeBouis = null;
    String validExecuteBouis = "";
    boolean toExecute=false;        
    boObject object = null;
    docHTML DOC = DOCLIST.getDOC(IDX);
    String error = "";
    String methodLabel = null;
    String methodName=request.getParameter("methodName")!=null?request.getParameter("methodName"):null;
    String explorerName=request.getParameter("explorerName")!=null?request.getParameter("explorerName"):null;
    String toObject=request.getParameter("toObject")!=null?request.getParameter("toObject"):null;
    Explorer currExplorer=ExplorerServer.getExplorer(explorerName);
    String overrideJSP=currExplorer.getMethodOverrideJSP(methodName);
    if (overrideJSP!=null && !overrideJSP.equals(""))
    {
        String parameters="?toObject="+toObject+"&methodName="+methodName+"&explorerName="+explorerName+
            "&selectedObjects="+request.getParameter("selectedObjects");
        response.sendRedirect(overrideJSP+parameters);
    }    
    if (request.getParameter("toObject")!=null)
    {
        
        methodLabel=boDefHandler.getBoDefinition(toObject).getBoMethod(methodName).getLabel();    
    }
    if ( request.getParameter("executeBouis")!=null )
    {
        toExecute=true;        
        executeBouis = ((String)request.getParameter("executeBouis")).split(";");
        boObject variable = null;
        boObject value = null;
        AttributeHandler programAttr = null;
        boObjectList list = null;        
        try
        {
            for (int i = 0; i < executeBouis.length ; i++) 
            {
                object = DOC.getObject( ClassUtils.convertToLong( executeBouis[i] ));   
                if(object.exists() && object != null)
                {
                    try
                    {                       
                        DOC.executeMethod(object,methodName);
                        validExecuteBouis += object.getBoui() + ";"; 
                        
                    }
                    catch (Exception e)
                    {
                         error += object.getCARDID()+": "+ e.getMessage()+"<br><br>";
                    }
                    
                }                
            }
        } 
        catch (Exception e)
        {
            error = e.getMessage();
        }
    }
    
    if ( request.getParameter("selectedObjects")!=null )
    {
        selectBouis = ((String)request.getParameter("selectedObjects")).split(";");
    }
            
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
<title>Executar Método sobre os Objectos Seleccionados</title>
<base target="_self">
</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

</head>
<script>

var objectAttributes = [];
function init()
{
  <%
  if(error.equals(""))
  {
      if ( toExecute )
      {      
      %>    
            //dialogArguments.deleteRows('<%=validExecuteBouis%>');
            dialogArguments.refreshExplorer('<%=explorerName%>');
            window.close();
        <%
    }
    %> 
<%}%>   
}

function choose()
   {
     //dialogArguments
     var bouisToExecute ="";
     for ( var i=0 ; i< boForm.elements.length;i++ )
     {
       if( boForm.elements[i].checked )
       {
            if(bouisToExecute!="")bouisToExecute+=";";
           bouisToExecute+=boForm.elements[i].id
       }
     }
     if ( bouisToExecute=="" )
     {
        alert("Não seleccionou nenhum objecto")
        window.close();   
     }
     else
     {
        boFormSubmit.executeBouis.value=bouisToExecute;
        boFormSubmit.submit();
        wait();
     }
     document.getElementById("cancel").style.display='none';
     //window.close();
   }
</script>


<body scroll="no" onload="init()">

<form name="boForm">
<table class='layout' cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;color:#000000">
    <col width=35px/>
    <col width=100%/>
    
    <tr height=25px>
        <td colspan=3 class='docHeaderTit_stdSEL'>Executar <u><i><%=methodLabel%></i></u> sobre os Objectos seleccionados</td>
    </tr>
    <%if(!error.equals("")) {%>
        <tr>
            <td class='error' colspan="3">
                <div class='error'>
                    <span style='font:13px'>
                        &nbsp;&nbsp;<b>Corrija os seguintes erros:</b>
                    </span>
                    <br>
                    <%=error%>
                </div>
            </td>
        </tr>
    <%}%>      
    <tr height=35px>
        <td class='gh_std'></td>
        <td class='gh_std'>Objecto</td>
    </tr>
   <tr height=100%><td colspan=3>
        <div id='container' style='height:100%;width:100%;padding:0px;overflow:auto'>   
            <table id='attrs' class='layout' cellSpacing="0" cellPadding="4" style="table-layout:fixed;background-color:#FFFFFF;color:#000000">
            <col width=5px/>
            <col width=30px/>
            <col width=80%/>
            <col width=20%/>
            <% for (int i = 0; i < selectBouis.length; i++) 
            {
                boObject obj = DOC.getObject( ClassUtils.convertToLong(selectBouis[i]) );
                boolean canExecute = true;
                
                if(!securityRights.canExecute (obj.getEboContext(),obj.getName(),methodName ))
                {
                    canExecute =false;
                }
            %>
             <tr >
            <td style='border-bottom:1px solid #CCCCCC' valign=top >&nbsp;</td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><input id='<%=selectBouis[i]%>' style='border:0' <%=canExecute?"checked":"disabled=1"%> type="CHECKBOX" ></input></td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><%=obj.getCARDID()%> </td>
            <%
                if (validExecuteBouis.indexOf(new Long(obj.getBoui()).toString())!=-1)
                {
            %>
            <td style='border-bottom:1px solid #CCCCCC' valign=top>OK </td>
            <%
                }
                else if (!validExecuteBouis.equals(""))
                {
            %>
                <td style='border-bottom:1px solid #CCCCCC' valign=top>FAILED </td>
            <%
                }
                else
                {
            %>
                <td style='border-bottom:1px solid #CCCCCC' valign=top>&nbsp; </td>
            <%
                }
            %>
            </tr>
            <%}%>
             <tr style='height:100%'>
             <td colspan=4/>
             </tr>
            </table>
        </div>
    </td>
    </tr>

    <tr style='height:35px;'  class='docFooter_std'>
        <td colspan=3  style='border-top:2px solid #CCCCCC'><center>
            <button onclick='choose();' ><%=methodLabel%></button> &nbsp; &nbsp; &nbsp; &nbsp;
             <button id="cancel" onclick='window.close()'>Cancelar</button></center>
        </td>
    </tr>
</table>

</div>
</form>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value="" name='executeBouis' />
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>

</body></html>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
