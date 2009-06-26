<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>


<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {


boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null )
{
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__xwfWorkPlace.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}
    String[] ctrl;
    String idbolist;
    int IDX;int cvui;
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"XwfController");
    request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");    
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);        
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);    
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    if(request.getParameter("objectBoui")!=null)
    {
        BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
        long[] a_boui = {BOI.getBoui()};
        currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
        if(!currObjectList.haveBoui(a_boui[0]))
        {
            currObjectList.inserRow(a_boui[0]);
        }
    }
    
    if(BOI != null && BOI.getSendRedirect() != null){response.sendRedirect( BOI.getSendRedirect() + "&docid=" + IDX + "&myIDX=" + IDX  );BOI.cleanSendRedirect();return;}

    XwfController controller = (XwfController)DOC.getController();
        
    String showWorkFlowArea = null;
    boObject activity = controller.getRuntimeActivity();
    if(activity != null)
    {
        showWorkFlowArea = activity.getAttribute("showWorkFlowArea").getValueString();
    }
    
    //begin viewr
    StringBuffer waitingOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysWaiting.jsp");

    StringBuffer newOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysNew.jsp");    

    StringBuffer doneOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysDone.jsp");
    
    StringBuffer tasksOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitys.jsp");

    StringBuffer messagesOnClick = xwfMainViewer.buildMessagesListOpenDoc(controller,null);    
    StringBuffer viewFlow = xwfMainViewer.buildViewFlowOpenDoc(controller, null);
    
    StringBuffer attributesOnClick = xwfMainViewer.buildProgramAttributesOpenDoc(controller,null);
    
    long waiting = controller.getEngine().getPendingActivityList().getRowCount();    
    long newAct = controller.getEngine().getCreateActivityList().getRowCount();   
    long done = controller.getEngine().getCloseActivityList().getRowCount(); 
    
    StringBuffer newTaskOnClick = new StringBuffer();
    xwfMainViewer.buildDefActivitysOpenDocUrl(controller,newTaskOnClick,xwfHelper.STEP_ACTIVITY);
    
    StringBuffer agendadas = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerDefActivitys.jsp");
    
    StringBuffer newWorkFlowOnClick = new StringBuffer();
//    xwfMainViewer.buildDefActivitysOpenDoc(controller,newWorkFlowOnClick,xwfHelper.STEP_USER_CALL_PROG);

    boolean workflowAdministrator = xwfHelper.isWorkFlowAdministrator(controller.getEngine());    
     
    
    StringBuffer connect = new StringBuffer();
    boObjectList wflow = null;  
    boolean flow = true;
    String exec_flow = null;
    String flowStr = null;
    if(controller.getRuntimeProgram() != null)
    {
        flowStr = controller.getRuntimeProgram().getAttribute("flow").getValueString();
    }

    if(flowStr == null || "".equals(flowStr))
    {
        boObject object = xwfHelper.getMasterObject(controller.getEngine(), controller.getRuntimeActivity());
        if(object != null) 
        {
            exec_flow = "select xwfProgram where versions in (select xwfProgramRuntime.programDef where variables.value.valueObject = "+ object.getBoui()+ 
            " or variables.value.valueList = "+ object.getBoui()+") and boui=";
            wflow = boObjectList.list(DOC.getEboContext(),"SELECT xwfProgram WHERE versions.input.name = '" + object.getName() + "' ORDER BY BOUI");
            wflow.beforeFirst();
            
            if(wflow.getRecordCount() > 0)
            {
                flow = false;
            }
                
        }
    }
    
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title></title>





<script>var objLabel="<%=controller.getRuntimeProgram().getCARDID()%>";</script>

<STYLE>
a.linkAction{COLOR: #000000;TEXT-DECORATION: none;CURSOR:Hand}
a.linkAction:hover{COLOR: #da0303;TEXT-DECORATION: underline;CURSOR:Hand}
a.linkShow{COLOR: #000000;TEXT-DECORATION: none;CURSOR:Hand}
a.linkShow:hover{COLOR: 0000FF;TEXT-DECORATION: underline;CURSOR:Hand}
.header{background-color:#e2f1f6}
form.objectForm{BEHAVIOR: url(templates/form/std/form.htc);BACKGROUND-COLOR:#F2F7FA}
span.error{color:#990000; TEXT-DECORATION: underline;cursor:hand;padding-left:10px}
div.error{overflow-y:auto;padding:2px;	height:40px;background-color:#FFFFFF}
td.error{background-color:#FFFFFF;padding:10px;height:60px}
</STYLE>

<link rel="stylesheet" type="text/css" href="wf.css">
</head>
<script LANGUAGE="javascript" src="jsmessages/jsmessages.jsp" type="text/javascript"></script>
<script LANGUAGE="javascript" src="wf.js" type="text/javascript"></script>
<script LANGUAGE="javascript">
function init()
{ 
    <%=xwfMainViewer.buildOpenDocList(controller)%>        
}
</script>




<body scroll="no" onload="init()">
<%--
<% if(controller.getRuntimeActivity() != null) { %>
    <iframe style="display:none" id = refreshframe src="__refresh.jsp?docid=<%=IDX%>&BOUI=<%=controller.getRuntimeActivityBoui()%>"></iframe>
<% } %>
--%>
<form class="objectForm" name="boForm" id="<%=IDX%>">

<%if(!xwfHelper.programNotExists(controller.getEngine()) || activity != null){%>
<table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
    
    <col />
	<col width="4">
    <% 
        if(activity == null || "1".equals(showWorkFlowArea))
        {
    %>    
	<col id="infoCol" width="140">
    <%
        }
    %>        
    <tr>
		
		<td id='' valign="top">			
            <table cellSpacing="0" cellPadding="0" style="table-layout:fixed;background-color:#EEEEEE;height:100%;width:100%">
                
                <col id="dd" width="190">
                <col width="4">
                <col />
                
               
                <%xwfMenuViewer.render(controller,pageContext);%>
              
                
                <%if(DOC.haveErrors()) {%>
                    <tr>
                        <td class='error' colspan="3">
                            <div class='error'>
                                <%=DOC.getHTMLErrors()%>
                            </div>
                        </td>
                    </tr>
                <%}%>       
            
                <tr style="height:47px;background-color:#FFFFFF">
                    <td style="font-size:13px;color:white;font-weight:bold" colspan="3">
                           
                           <table style='width:100%;height:100%' cellpadding=0 cellspacing=0 >
                               <tr style='height:24px'>
                                    <td id='infoArea' class='header' style="font-size:11px;font-weight:bold">
                                       &nbsp;&nbsp;
                                       <%= controller.getRuntimeActivityBoui() != -1 ?  controller.getRuntimeActivity().getAttribute("label").getValueString() : ""%>                 
                                    </td>
                                    <!--
                                    <td id='announceArea' class='header' style="font-size:11px;color:white;font-weight:bold">
                                       
                                    </td>
                                    -->
                               </tr>
                               <tr style='height:20px'>
                                  <td id="">
                                       <span class='scrollContainer header' scrollVertical='0' id='containerTaskBar' style='height:100%;width:100%'>
                                            <table id='taskbar' style='height:100%;' cellpadding=0 cellspacing=0>
                                                <tr>
                                                
                                                </tr>
                                            </table>
                                       </span>
                                  </td>
                               </tr>
                               <tr class="taskUnderline" >
                                    <td>&nbsp;</td>
                               </tr>
                           </table>
                           
                    </td>
                 </tr>
                
                <tr>
                    <td id='docs' valign="top" colspan="3">
                    
                    </td>		
                </tr>
            </table>
            
        </td>
        <% 
            if(activity == null || "1".equals(showWorkFlowArea))
            {
        %>
        <td splitter width="4">
            <img class="rszLeft ui-rszLeft" src="ieThemes/0/splitter/resize-dot.gif" WIDTH="1" HEIGHT="1">
        </td>
    
        <td id='info' valign="top">
           
            <!--<iframe xsrc="__xwfViewerInformation.jsp?docid=<%=IDX%>&<%=ControllerFactory.CONTROLLER_NAME_KEY%>=<%=controller.getName()%>&<%=XwfKeys.PROGRAM_RUNTIME_BOUI_KEY%>=<%=controller.getRuntimeProgramBoui()%>" style="width:100%;height:100%" scrolling="no"></iframe>-->
                  <TABLE cellspacing="0" cellpadding="0" width="100%" style='background-color:#FFFFFF'>
            <TR align="right">
               <TD valign="top" >   
                    <%=controller.getRuntimeProgramBoui()%>                        
               </TD>
            </TR>                          
                    
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <b>Consultar</b>                            
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>            
            <TR>
            
               <TD valign="top" style='padding-left:9px;'>                  
                    <% if(waiting > 0) {%>
                        <a class="linkShow" href="javascript:<%=waitingOnClick.toString()%>"  title="Tarefas Pendentes do utilizador" >                            
                            Tarefas em espera (<b><%=waiting%></b>)
                        </a>
                    
                    <%}else{%>
                            Não existem tarefas em espera
                    <%}%>
               </TD>
            </TR>        
            <TR>
               <TD valign="top" style='padding-left:9px;'>
                <% if(newAct > 0) {%>
                  <a href="javascript:<%=newOnClick.toString()%>;"  class="linkShow" >
                        Tarefas Pendentes (<b><%=newAct%></b>)
                  </a>
                <%}else{%>
                        Não existem tarefas pendentes
                <%}%>                  
               </TD>
            </TR>                 
            <TR >
               <TD valign="top" style='padding-left:9px;'>
               <% if(done > 0) {%>
                  
                      <a class="linkShow" href="javascript:<%=doneOnClick.toString()%>"  title="Tarefas Concluídas pelo utilizador" >
                        Tarefas Concluídas (<b><%=done%></b>)
                      </a>
                  
                <%}else{%>
                        Não existem tarefas Concluídas
                <%}%>                                    
               </TD>
            </TR>
            <TR >
               <TD valign="top" style='padding-left:9px;'>
                  <a class="linkShow" href="javascript:<%=tasksOnClick.toString()%>"  title="Tarefas Concluídas pelo utilizador" >
                    Ver Tarefas
                  </a>   
               </TD>
            </TR>

            <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>              
 
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                        <a class="linkShow" href="javascript:<%=attributesOnClick.toString()%>" title="Atributos do fluxo de trabalho">
                            Informação                         
                        </a>
               </TD>
            </TR>              
            <%if(!xwfHelper.programNotExists(controller.getEngine())){ %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                        <a class="linkShow" href="javascript:<%=messagesOnClick.toString()%>" title="Mensagens do fluxo de trabalho">
                        Mensagens
                        </a>
               </TD>
            </TR>
            <%}%>
                                                        
            
            <% if(workflowAdministrator) { %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                   <a class="linkShow" href="javascript:<%=viewFlow.toString()%>" title="Visualizar fluxo de trabalho">
                        Fluxo de Trabalho
                    </a>
                </TD>
            </TR>
            <%}%>




           <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR> 
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <b>Acções</b>                            
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>
            <TR>
               <TD valign="top" style='padding-left:9px;'>                  
                    <a class="linkShow" href="javascript:<%=newTaskOnClick.toString()%>"  title="Atribuir tarefa" >                            
                        Atribuir
                    </a>                                    
               </TD>
            </TR>
            <TR>
                <TD valign="top" style='padding-left:9px;'>
                  <a class="linkShow" href="javascript:<%=agendadas.toString()%>"  title="Agendamentos de acções" >                            
                        Planos de Acções
                  </a>
                </TD>
            </TR>



            <TR height="100%">
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>
            <%
                java.util.ArrayList list = xwfHelper.getProcedures(controller.getEngine());
                if(list != null)
                {
                    if(list.size() > 0)
                    {                
            %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <b>Menu</b>     
               </TD>
            </TR>     
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>            
            <% 
                }
                String name = null;
                String label = null;
                String title = null;
                String nr = null;
                Node node = null;
                ngtXMLHandler nx = null; 
                String needQuestion = "true";
                String answerQuest = "yes";
                for(int i=0; i < list.size() && i < 7; i++)
                {
                        nx = (ngtXMLHandler)list.get(i);
                        name = nx.getAttribute("name");   
                        label = nx.getChildNode("label").getCDataText();   
                        nr = nx.getAttribute("count");
                        
                        if(nr == null || nr.equals("0")  )
                            title = "Esta acção ainda não foi executada.";
                        else
                            if(nr.equals("1")) title = "Esta acção foi executada 1 vez.";
                            else title = "Esta acção foi executada "+nr+" vezes.";
                        needQuestion = Boolean.toString(xwfHelper.needPressMenuQuestion(controller));
                        answerQuest = Boolean.toString(xwfHelper.getUserMenuAnswer(boctx));
            %>
            <TR>
               <TD valign="top" style='padding-left:9px;padding-bottom:5px'>
                        <a  class="linkAction" href="javascript:if(<%=needQuestion%>)if(<%=answerQuest%> || confirm('Deseja Completar a ACTIVIDADE DE LEITURA DE MENSAGEM Antes de Prosseguir?\n\n \n                                OK                Para a Completar. \n\n                                CANCELAR   Para a Deixar Aberta.')){setActionCode('<%=XwfKeys.ACTION_CLOSE_KEY+";"+XwfKeys.ACTION_PROCEDURE+"["+name+"]"%>');boForm.BindValues();wait();} else {setActionCode('<%=XwfKeys.ACTION_PROCEDURE+"["+name+"]"%>');boForm.BindValues();wait();} else {setActionCode('<%=XwfKeys.ACTION_PROCEDURE+"["+name+"]"%>');boForm.BindValues();wait();}" title="<%=title%>"">                                                
                        <%=label%> 
                        <% if(!"0".equals(nr)){ %> 
                            <span><b>*</b></span>
                        <%}%>                    
                        </a>
               </TD>
            </TR>   
            <% 
                } 
                
                if(list.size() > 7)
                {
            %>
       
            <TR>
               <TD valign="top">   
                    &nbsp;&nbsp;
                    
                        <a class="linkAction" href="javascript:<%=xwfMainViewer.buildProgramOpenDoc(controller,null,"__xwfViewerProcedure.jsp","closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true").toString()%>" title="Clique aqui para mais opções">
                        Mais...                       
                        </a>
               </TD>
            </TR>            
            <%
                }
            }
            %>
            <% if(wflow != null && wflow.getRowCount() > 0){ %>
            <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>
            
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <b>Fluxo de Trabalho Disponíveis</b>     
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>
                <%                    
                    boObject program = null;
                    long programBoui = 0;
                    long p_executed = 0;
                    String label = null;
                    String needQuestion = "true";
                    String answerQuest = "yes";
                    while(wflow != null && wflow.next())
                    {
                        program = wflow.getObject();
                        label = program.getAttribute("name").getValueString();
                        programBoui = program.getBoui(); 
                        if(exec_flow != null)
                        {
                        //    boObjectList lexec = boObjectList.list(boctx, exec_flow+programBoui);
                        //    p_executed = lexec.getRowCount();
                            p_executed = 0;
                        }
                        needQuestion = Boolean.toString(xwfHelper.needPressMenuQuestion(controller));
                        answerQuest = Boolean.toString(xwfHelper.getUserMenuAnswer(boctx));
                            
                %>
                    <TR>
                       <TD valign="top" style='padding-left:9px;padding-bottom:5px'>
                            
                                <a class="linkAction" href="javascript:if(<%=needQuestion%>){if(<%=answerQuest%> || confirm('Deseja Completar a ACTIVIDADE DE LEITURA DE MENSAGEM Antes de Prosseguir?\n\n \n                                OK                Para a Completar. \n\n                                CANCELAR   Para a Deixar Aberta.')){setActionCode('<%=XwfKeys.ACTION_CLOSE_KEY+";"+XwfKeys.ACTION_CREATE_PROGRAM_KEY+"["+programBoui+"]"%>');boForm.BindValues();wait();}else{setActionCode('<%=XwfKeys.ACTION_CREATE_PROGRAM_KEY+"["+programBoui+"]"%>');boForm.BindValues();wait();}}else{setActionCode('<%=XwfKeys.ACTION_CREATE_PROGRAM_KEY+"["+programBoui+"]"%>');boForm.BindValues();wait();}" title="Criar novo Fluxo de Trabalho">                                                
                                <%=label%> 
                                <% if(p_executed > 0){ %> 
                                    <span style="color:red">(<b><%=Long.toString(p_executed)%></b>)</span>
                                <%}%>   
                                </a>
                            </a>
                       </TD>
                    </TR>   
                
                <%}%>
            <%}%>
                                         
      </TABLE>      
            
            <!--- -->
        </td>
        <%
            }
        %>
    </tr>
    </table>
    <%}else{%>    
        <TABLE class="layout" style='height:100%;width:100%;' cellspacing="0" cellpadding="0" style='background-color:#FFFFFF' >
            <tr>
                <td  align=center>
                    <%if(xwfHelper.programNotExists(controller.getEngine())){ %>
                        <h4>O fluxo de trabalho foi removido.</h4>
                    <%}else{%>
                        <h4>Não exitem Tarefas.</h4>
                    <%}%>
                </td>
            </tr>
        </table>
    <%}%>
    

<iframe style="display:none" id = bocmds1 src="__cmds.jsp?cmds=1"></iframe>


</form>
<FORM name='boFormSubmit' method='post' action="__xwfWorkPlace.jsp">

    <%
        java.util.Hashtable options = new java.util.Hashtable();    
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_INPUT_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "boui" ) != null){
            options.put(BasicPresentation.OPTION_REQUESTED_BOUI,request.getParameter( "boui" ));
        }
        options.put(BasicPresentation.OPTION_TYPE_FORM,"0");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());                                                  
    %>
    
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>     

</FORM> 
<iframe style="display:none" onreadystatechange=verifyCmdsSession() id = controlSession src="__controlSessionXwf.jsp?cmds=3"></iframe>
</body></html>
<%

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
