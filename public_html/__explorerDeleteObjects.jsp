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
    String[] removeBouis = null;
    String validRemovedBouis = "";
    boolean toDelete=false;        
    boObject object = null;
    docHTML DOC = DOCLIST.getDOC(IDX);
    String error = null;
    if ( request.getParameter("removeBouis")!=null )
    {
        toDelete=true;        
        removeBouis = ((String)request.getParameter("removeBouis")).split(";");
        boObject variable = null;
        boObject value = null;
        AttributeHandler programAttr = null;
        boObjectList list = null;        
        try
        {
            for (int i = 0; i < removeBouis.length ; i++) 
            {
                object = DOC.getObject( ClassUtils.convertToLong( removeBouis[i] ));   
                if(object.exists() && object != null)
                {
                    if(DestroyBusinessObject.destroy(object))
                    {
                        validRemovedBouis += object.getBoui() + ";"; 
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
        
    boolean inXwf = false;
    if(toDelete)
    {
        if("XwfController".equals(DOC.getController().getName()))
        {
            inXwf = true;
        }
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
<title>Apagar Objectos Seleccionados</title>
<base target="_self">
</head>

<script LANGUAGE="javascript" SRC="xeo.js"></script>

</head>
<script>

var objectAttributes = [];
function init()
{
  <%
  if(error == null)
  {
      if ( toDelete )
      {
      
        if(!inXwf)
        {
      %>    
            dialogArguments.deleteRows('<%=validRemovedBouis%>');
            window.close();
        <%
        }
        else
        {
            boObject program = ((XwfController)DOC.getEboContext().getController()).getRuntimeProgram();        
            if(program != null)
            {
                 if(!program.exists())
                 {
                    %>            
                    window.close();
                    dialogArguments.winmain().parent.winmain().ndl[dialogArguments.winmain().getIDX()].close();
                    <% 
              }else{ 
                    %>    
                    dialogArguments.setActionCode('<%=XwfKeys.ACTION_CLEAR_CACHE_KEY%>');
                    dialogArguments.boForm.BindValues();
                    parent.wait();
                    window.close();
                    <% 
                } 
            }
            else
            {
                %>
                    window.close();
                    dialogArguments.winmain().parent.winmain().ndl[dialogArguments.winmain().getIDX()].close();            
                <%
            }
        }
    }
    %> 
<%}%>   
}

function choose()
   {
     //dialogArguments
     var bouisToDelete ="";
     for ( var i=0 ; i< boForm.elements.length;i++ )
     {
       if( boForm.elements[i].checked )
       {
            if(bouisToDelete!="")bouisToDelete+=";";
           bouisToDelete+=boForm.elements[i].id
       }
     }
     if ( bouisToDelete=="" )
     {
        alert("Não seleccionou nenhum objecto para apagar")
        window.close();   
     }
     else
     {
        boFormSubmit.removeBouis.value=bouisToDelete;
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
        <td colspan=3 class='docHeaderTit_stdSEL'>Apagar Objectos seleccionados</td>
    </tr>
    <%if(error != null) {%>
        <tr>
            <td class='error' colspan="3">
                <div class='error'>
                    <span style='font:13px'>
                        &nbsp;&nbsp;<b><%=netgest.bo.localized.JSPMessages.getString( "__explorerDeleteObjects.1" )%>:</b>
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
            <col width=100%/>
            <% for (int i = 0; i < selectBouis.length; i++) 
            {
                boObject obj = DOC.getObject( ClassUtils.convertToLong(selectBouis[i]) );
                boolean canDelete = true;
                if(obj.getStateManager() != null)
                {
                    canDelete = obj.getStateManager().getCanRemove( obj );
                }
            %>
             <tr >
            <td style='border-bottom:1px solid #CCCCCC' valign=top >&nbsp;</td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><input id='<%=selectBouis[i]%>' style='border:0' <%=canDelete?"checked":"disabled=1"%> type="CHECKBOX" ></input></td>
            <td style='border-bottom:1px solid #CCCCCC' valign=top><%=obj.getCARDID()%> </td>
            </tr>
            <%}%>
             <tr style='height:100%'>
             <td colspan=3/>
             </tr>
            </table>
        </div>
    </td>
    </tr>

    <tr style='height:35px;'  class='docFooter_std'>
        <td colspan=3  style='border-top:2px solid #CCCCCC'><center>
            <button onclick='choose();' >Apagar objectos seleccionados</button> &nbsp; &nbsp; &nbsp; &nbsp;
             <button id="cancel" onclick='window.close()'>Cancelar</button></center>
        </td>
    </tr>
</table>

</div>
</form>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value="" name='removeBouis' />
    <INPUT type='hidden' value='<%=this.getClass().getName()%>' name='boFormSubmitSecurity' />    
</FORM>

</body></html>
<% } %>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
