<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>
<%@ page import="org.w3c.dom.*"%>
<%@ page import="netgest.bo.ql.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=claim_generaledit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    int IDX;int cvui;
    boDefHandler bodef;
    boDefAttribute atr;
    String idbolist;
    String[] ctrl;
    docHTML_section sec;
    docHTML_grid grid;
    Hashtable xattributes;    
    request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    String method=request.getParameter( "method" );
    String inputMethod=request.getParameter( "method" );
    String requestedBoui=request.getParameter( "boui" );
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    
    if(request.getParameter("objectBoui")!=null)
    {
        BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
        long[] a_boui = {BOI.getBoui()};
        currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
        if(!currObjectList.haveBoui(a_boui[0]))
            currObjectList.inserRow(a_boui[0]);
    }
    if( currObjectList != null ) 
        currObjectList.first();

    XwfController controller = (XwfController)DOC.getController();
    
    StringBuffer waitingOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysWaiting.jsp");

    StringBuffer newOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysNew.jsp");    

    StringBuffer doneOnClick = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramActivitysDone.jsp");    

    StringBuffer messagesOnClick = xwfMainViewer.buildMessagesListOpenDoc(controller,null);    
    StringBuffer viewFlow = xwfMainViewer.buildViewFlowOpenDoc(controller, null);
    
    StringBuffer attributesOnClick = xwfMainViewer.buildProgramAttributesOpenDoc(controller,null);
    
    long waiting = controller.getEngine().getPendingActivityList().getRecordCount();    
    long newAct = controller.getEngine().getCreateActivityList().getRecordCount();   
    long done = controller.getEngine().getCloseActivityList().getRecordCount(); 
    
    StringBuffer newTaskOnClick = new StringBuffer();
//    xwfMainViewer.buildDefActivitysOpenDoc(controller,newTaskOnClick,xwfHelper.STEP_ACTIVITY);
//    
    StringBuffer newWorkFlowOnClick = new StringBuffer();
//    xwfMainViewer.buildDefActivitysOpenDoc(controller,newWorkFlowOnClick,xwfHelper.STEP_USER_CALL_PROG);

    boolean workflowAdministrator = xwfHelper.isWorkFlowAdministrator(controller.getEngine());    
     
    
    StringBuffer connect = new StringBuffer();
    boObjectList wflow = null;  
    boolean flow = true;
    String exec_flow = null;
    String flowStr = controller.getRuntimeProgram().getAttribute("flow").getValueString();

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
//        
//        connect = xwfMainViewer.buildProgramOpenDoc(controller, null,"__xwfViewerProgramToAssociate.jsp");        
    }
        

    
%>

<html>
<head>
<STYLE>
.linkAction
{    
    COLOR: #000000;    
    TEXT-DECORATION: none;
    CURSOR:Hand;
}
.linkAction:hover
{
    COLOR: #da0303;    
    TEXT-DECORATION: underline;
    CURSOR:Hand;
}

.linkShow
{    
    COLOR: #000000;    
    TEXT-DECORATION: none;
    CURSOR:Hand;
}
.linkShow:hover
{
    COLOR: 0000FF;    
    TEXT-DECORATION: underline;
    CURSOR:Hand;
}
</STYLE>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="Informações e Opções";  
</script>
<%= DOC.getController().getPresentation().writeCSS() %>
</head>   
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()" style='background-color:#de8e29' >

<form style='height:100%;' class="objectForm" name="boForm" id="<%=IDX%>">    
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
                    <a href="#" class="linkShow" onmouseover="window.status='';return true;">                        
                        <span onclick="<%=waitingOnClick.toString()%>"  title="Tarefas Pendentes do utilizador" >                            
                            Tarefas em espera (<b><%=waiting%></b>)
                        </span>
                    </a>
                    <%}else{%>
                            Tarefas em espera (<b><%=waiting%></b>)
                    <%}%>
               </TD>
            </TR>        
            <TR>
               <TD valign="top" style='padding-left:9px;'>
                <% if(newAct > 0) {%>
                  <a href="#" class="linkShow" onmouseover="window.status='';return true;">
                      <span onclick="<%=newOnClick.toString()%>" title="Novas tarefas do utilizador">
                        Tarefas Pendentes (<b><%=newAct%></b>)
                      </span>
                  </a>
                <%}else{%>
                        Tarefas Pendentes (<b><%=newAct%></b>)
                <%}%>                  
               </TD>
            </TR>                 
            <TR >
               <TD valign="top" style='padding-left:9px;'>
               <% if(done > 0) {%>
                  <a href="#" class="linkShow" onmouseover="window.status='';return true;">
                      <span onclick="<%=doneOnClick.toString()%>"  title="Tarefas Concluídas pelo utilizador" >
                        Tarefas Concluídas (<b><%=done%></b>)
                      </span>
                  </a>
                <%}else{%>
                        Tarefas Concluídas (<b><%=done%></b>)
                <%}%>                                    
               </TD>
            </TR>

            <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>              
<!--
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
-->            
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <a href="#" class="linkShow" onmouseover="window.status='';return true;">
                        <span onclick="<%=attributesOnClick.toString()%>" title="Atributos do fluxo de trabalho">
                        Informação                         
                        </span>
                    </a>
               </TD>
            </TR>              
            <%if(controller.getRuntimeProgram().exists()){ %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <a href="#" class="linkShow" onmouseover="window.status='';return true;">
                        <span onclick="<%=messagesOnClick.toString()%>" title="Mensagens do fluxo de trabalho">
                        Mensagens
                        </span>
                    </a>
               </TD>
            </TR>
            <%}%>
            <% if(workflowAdministrator) { %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                    <a href="#" class="linkShow" onmouseover="window.status='';return true;">
                        <span onclick="<%=viewFlow.toString()%>" title="Visualizar fluxo de trabalho">
                        Fluxo de Trabalho
                        </span>
                    </a>                    
               </TD>
            </TR>
            <%}%>
<!--            
            <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>               
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                    <b>Criar</b>     
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>            
            
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="parent.setActionCode('<%=XwfKeys.ACTION_LAUNCH_KEY+"[]["+xwfHelper.STEP_ACTIVITY+"]"%>');parent.boForm.BindValues();" title="Criar nova tarefa">
                        Tarefa                       
                        </span>
                    </a>
               </TD>
            </TR>   
            <% if(workflowAdministrator) { %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="parent.setActionCode('<%=XwfKeys.ACTION_LAUNCH_KEY+"[]["+xwfHelper.STEP_USER_CALL_PROG+"]"%>');parent.boForm.BindValues();" title="Criar novo Fluxo de Trabalho">
                        Fluxo de Trabalho                       
                        </span>
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
                    <b>Atribuir</b>     
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>
            
            <TR>
               <TD valign="top" style='padding-left:9px;'>                            
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="<%=newTaskOnClick.toString()%>" title="Atribuir nova Tarefa a outro utilizador">
                        Tarefa                                                            
                        </span>
                    </a>
               </TD>
            </TR>                   
            <% if(workflowAdministrator) { %>
            
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="<%=newWorkFlowOnClick.toString()%>" title="Atribuir novo Fluxo de Trabalho a outro utilizador">
                        Fluxo de Trabalho                       
                        </span>
                    </a>
               </TD>
            </TR>    
            
            <TR>
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>
            <% } %>
            
            <% if(!flow || workflowAdministrator){ %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>   
                    <b>Outras op败s</b>     
               </TD>
            </TR>
            <TR>
                <TD valign="top">
                    <hr>
                </TD>
            </TR>
            <%}%>
            <% if(workflowAdministrator) { %>
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="if(confirm('Quer remover este fluxo de trabalho ?')){parent.setActionCode('<%=XwfKeys.ACTION_REMOVE_PROGRAM_KEY%>');parent.boForm.BindValues();parent.wait();}" title="Remover Fluxo de Trabalho">
                        Remover Fluxo                       
                        </span>
                    </a>
               </TD>
            </TR>
            <TR>
               <TD valign="top" style='padding-left:9px;'>                       
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="if(confirm('Quer cancelar este fluxo de trabalho ?')){parent.setActionCode('<%=XwfKeys.ACTION_CANCEL_PROGRAM_KEY%>');parent.boForm.BindValues();parent.wait();}" title="Cancelar Fluxo de Trabalho">
                        Cancelar Fluxo                       
                        </span>
                    </a>
               </TD>
            </TR>            
            <%}%>
            <% if(!flow){ %>
            <TR>
               <TD valign="top">   
                    &nbsp;&nbsp;
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="<%=connect.toString()%>" title="Clique aqui para associar a outro programa">
                        Associar a um Fluxo                       
                        </span>
                    </a>
               </TD>
            </TR>  
            <%}%>
            -->
            <TR height="100%">
               <TD valign="top">   
                    &nbsp;                        
               </TD>
            </TR>
            <%
                ArrayList list = xwfHelper.getProcedures(controller.getEngine());
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
                String nr = null;
                ngtXMLHandler nx = null;                
                for(int i=0; i < list.size() && i < 5; i++)
                {
                        nx = (ngtXMLHandler)list.get(i);
                        name = nx.getAttribute("name");   
                        label = nx.getChildNode("label").getCDataText();   
                        nr = nx.getAttribute("count");
            %>
            <TR>
               <TD valign="top" style='padding-left:9px;padding-bottom:5px'>
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span  onclick="parent.setActionCode('<%=XwfKeys.ACTION_PROCEDURE+"["+name+"]"%>');parent.boForm.BindValues();parent.wait();" title="<%=label%>">                                                
                        <%=label%> 
                        <% if(!"0".equals(nr)){ %> 
                            <span style="color:red">(<b><%=nr%></b>)</span>
                        <%}%>                    
                        </span>
                    </a>
               </TD>
            </TR>   
            <% 
                } 
                
                if(list.size() > 5)
                {
            %>
       
            <TR>
               <TD valign="top">   
                    &nbsp;&nbsp;
                    <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                        <span onclick="<%=xwfMainViewer.buildProgramOpenDoc(controller,null,"__xwfViewerProcedure.jsp","closeWindowOnCloseDoc=false,showCloseIcon=true,activeOnOpen=true").toString()%>" title="Clique aqui para mais opções">
                        Mais...                       
                        </span>
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
                    while(wflow != null && wflow.next())
                    {
                        program = wflow.getObject();
                        label = program.getAttribute("name").getValueString();
                        programBoui = program.getBoui(); 
                        if(exec_flow != null)
                        {
                            boObjectList lexec = boObjectList.list(boctx, exec_flow+programBoui);
                            p_executed = lexec.getRowCount();
                        }
                            
                %>
                    <TR>
                       <TD valign="top" style='padding-left:9px;padding-bottom:5px'>
                            <a href="#" class="linkAction" onmouseover="window.status='';return true;">
                                <span onclick="parent.setActionCode('<%=XwfKeys.ACTION_CREATE_PROGRAM_KEY+"["+programBoui+"]"%>');parent.boForm.BindValues();parent.wait();" title="Criar novo Fluxo de Trabalho">                                                
                                <%=label%> 
                                <% if(p_executed > 0){ %> 
                                    <span style="color:red">(<b><%=Long.toString(p_executed)%></b>)</span>
                                <%}%>   
                                </span>
                            </a>
                       </TD>
                    </TR>   
                
                <%}%>
            <%}%>
                
            <TR height="100%" >
            </TR>                             
      </TABLE>                
</form>
<FORM name='boFormSubmit' method='post'>

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
        options.put(BasicPresentation.OPTION_FORCE_NO_CHANGE,"true");
    %>
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>  
</FORM>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
