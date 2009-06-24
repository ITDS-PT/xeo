<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>

<%@ page import="netgest.bo.impl.document.merge.gestemp.presentation.*"%>
<%@ page import="netgest.bo.impl.document.merge.gestemp.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>
<%@ page import="pt.lusitania.gd.GDHelper"%>

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
    response.sendRedirect("login.jsp?returnPage=gestTempFillParameters.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    int IDX;int cvui;
    String idbolist;
    String[] ctrl;
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"BasicController");
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    ArrayList erros = new ArrayList();
    
    String strTempBoui = request.getParameter("inputObjectBoui");
    boolean testMode = "true".equals(request.getParameter("testMode"));
    String generatedBoui = request.getParameter("generatedBOUI");
    long templateBoui  = Long.parseLong(strTempBoui);
    GtTemplate template = null;
    
    String strOwfActvBoui = request.getParameter("owfActvBoui");
    String strActvSendBoui = request.getParameter("actvSendBoui");
    
    String sendType = request.getParameter("sendType");
    String msgRBoui = request.getParameter("msgBoui");
    String actvRBoui = request.getParameter("actvBoui");
    String progRBoui = request.getParameter("programCotacaoBoui");
    String cotacaoRBoui = request.getParameter("cotacaoBoui");
    
    String previousOperation = request.getParameter("previousOperation");

    long actvBoui = -1, owfActvBoui = -1;
    if(strActvSendBoui != null && !"".equals(strActvSendBoui))
    {//já existe uma actividade de envio
        actvBoui = Long.parseLong(strActvSendBoui);
        template = GtTemplateFactory.getTemplateByBoui(DOC, templateBoui, actvBoui);
        template.setActvSendBoui(actvBoui);
    }
    else if(testMode && generatedBoui != null && !"".equals(generatedBoui))
    {
        actvBoui = Long.parseLong(generatedBoui);
        template = GtTemplateFactory.getTemplateByBoui(DOC, templateBoui, actvBoui);
        template.setActvSendBoui(actvBoui);
    }
    else if(strOwfActvBoui != null && !"".equals(strOwfActvBoui))
    {
        owfActvBoui = Long.parseLong(strOwfActvBoui);
        if(owfActvBoui > 0 )
        {
            boObject owfObj = boObject.getBoManager().loadObject(DOC.getEboContext(), owfActvBoui);
            long tempBoui = GDHelper.getTemplateBoui(owfObj);
            if(tempBoui > 0)
            {
                template = GtTemplateFactory.getTemplateByBoui(DOC, templateBoui);
                template.setOwfBoui(owfActvBoui);
                if(GDHelper.fillChannel(owfObj, template))
                {
                    GDHelper.fillTemplate(owfObj, template);
                }
            }
        }
    }
    if(template == null)
    {//primeira vez que é gerado
        template = GtTemplateFactory.getTemplateByBoui(DOC, templateBoui);
    }
    template.setModeTest(testMode);
    
    if(sendType != null && !"".equals(sendType) && (template.getSendType() == null || "".equals(template.getSendType())))
    {
        template.setSendType(sendType);
        template.setMsgReplyFrom(Long.parseLong(msgRBoui));
        template.setActvReplyFrom(Long.parseLong(actvRBoui));
        template.setAnexos(DOC.getEboContext());
    }

/*    
    if(progRBoui != null && !"".equals(progRBoui) && cotacaoRBoui != null && !"".equals(cotacaoRBoui))
    {
        template.setProgramCotacaoBoui(Long.parseLong(progRBoui));
        template.setCotacaoBoui(Long.parseLong(cotacaoRBoui));
        template.setAnexos(DOC.getEboContext());
    }
*/    
    
    String nextPage = request.getParameter("nextPage");
    
    String firstFieldID = GtTemplateViewer.getFistFieldID(template);

    //operation
    String operation = request.getParameter("operation");
    if("typeSelected".equals(operation))
    {
        template.clearErrors();
        template.setTypeValues(boctx);
        if(template.getOwfBoui() > 0)
        {
             boObject owfObj = boObject.getBoManager().loadObject(DOC.getEboContext(), template.getOwfBoui());
             GDHelper.fillTemplate(owfObj, template);
        }
        if(template.getErro() != null && template.getErro().length() > 0)
        {
            erros.add(template.getErro());
        }
    }
    else if("letterTypeSelected".equals(operation))
    {
        long ti = System.currentTimeMillis();
        template.clearErrors();
        template.setLetterTypeValues(boctx);
        if(template.getErro() != null && template.getErro().length() > 0)
        {
            erros.add(template.getErro());
        }
    }
    else if("listFillSelected".equals(operation))
    {
        long ti = System.currentTimeMillis();
        template.clearErrors();
        template.listValidate(boctx, erros);
        if(template.getErro() != null && template.getErro().length() > 0)
        {
            erros.add(template.getErro());
            template.setFillListParams(false);
        }
        if(erros.size() == 0)
        {
            template.templateValidate(boctx, erros);
            if(erros.size() == 0)
            {
                boObject result = template.process(boctx);
                long tf = System.currentTimeMillis();
                System.out.println("Tempo Total (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)");
                if(result != null)
                {
                    if( testMode )
                    {
                        DOC.setMasterObjectOverriding( template.generate(  boctx ));
                    }
                    String redirectPage= Helper.getResultPage(request, DOC, result);
                    response.sendRedirect(redirectPage);
                    return;
                }
                else
                {
                    erros.add(template.getErro());
                }
            }
            else
            {
                template.setFillListParams(false);
                template.setFillParams(false);
            }
        }
        else
        {
            template.setFillListParams(false);
        }
    }
    else if("faxTypeSelected".equals(operation))
    {
        long ti = System.currentTimeMillis();
        template.clearErrors();
        template.setFaxTypeValues(boctx);
        
        
        if(template.getErro() != null && template.getErro().length() > 0)
        {
            erros.add(template.getErro());
        }
        if( previousOperation.equals("generateAnexos"))
        {
            template.validateAnexos( boctx, erros );
        }
        if(erros.size() == 0)
        {
            boObject result = template.process(boctx);
            long tf = System.currentTimeMillis();
            System.out.println("Tempo Total (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)");
            if(result != null)
            {
                if( testMode )
                {
                    DOC.setMasterObjectOverriding( result /*template.generate(  boctx )*/ );
                }
                String redirectPage= Helper.getResultPage(request, DOC, result);
                response.sendRedirect(redirectPage);
                return;
            }
            else
            {
                erros.add(template.getErro());
            }
        }
    }
    else if("generate".equals(operation))
    {
        long ti = System.currentTimeMillis();
        template.clearErrors();
        
        if( previousOperation.equals("generateAnexos"))
        {
            template.validateAnexos( boctx, erros );
        }
        if( erros.size() == 0 )
        {
            template.validate(boctx, erros);
            if(template.getErro() != null && !"".equals(template.getErro()))
            {
                if(erros.size() == 0)
                {
                    erros.add(template.getErro());
                }
            }
            if(erros.size() == 0)
            {
                boObject result = template.process(boctx);
                long tf = System.currentTimeMillis();
                System.out.println("Tempo Total (" + (float)(Math.round((float)(tf-ti)/100f))/10f +"s)");
                if( testMode )
                {
                    DOC.setMasterObjectOverriding( template.generate(  boctx ));
                }
                if(result != null)
                {
                    String redirectPage= Helper.getResultPage(request, DOC, result);
                    response.sendRedirect(redirectPage);
                    return;
                }
                else
                {
                    erros.add(template.getErro());
                }
            }
            else
            {
                if  ( 
                        "letterTypeSelected".equals(  previousOperation ) 
                        ||
                        "faxTypeSelected".equals(  previousOperation ) 
                        ||
                        "typeSelected".equals(  previousOperation ) 
                    )
                { 
                    template.setFillParams( true );
                }
            }
        }
    }
    else if("generateAnexos".equals(operation) || "listFill".equals(operation) )
    {
        long ti = System.currentTimeMillis();
        template.clearErrors();
        // Verifica se quais os paramteros a validar. Se hasFillListParams
        if( "listFill".equals( previousOperation ) && !"listFill".equals( operation ))
        {
            template.listValidate(boctx, erros);
            if(template.getErro() != null && !"".equals(template.getErro()))
            {
                template.setFillListParams(false);
                if(erros.size() == 0)
                {
                    erros.add(template.getErro());
                }
            }
            if(erros.size() > 0)
            {
                template.setFillListParams(false);
            }
        }
        else
        {
            template.validate(boctx, erros);
            if(template.getErro() != null && !"".equals(template.getErro()))
            {
                template.setFillParams(false);
                template.setFillListParams(false);
                if(erros.size() == 0)
                {
                    erros.add(template.getErro());
                }
            }
            else
            {
                // Codigo para forçar a gravação dos dados de objectos mapeados.
                // Serve para gravar BOUI's de tabelas relacionadas e as queries BOQL funcionarem correctamente nas dependencias
                // A razao inicial foi derivado aos intervenientes dos sinistros que por vezes ainda não existiam nas bridges e o BOQL falhava.
                GtQuery[] q = template.getQueries();
                for (int i = 0; i < q.length; i++) 
                {
                    if( q[i].getParametro().getObjecto() != 0 )
                    {
                        Object vobj = q[i].getParametro().getValue().getValue();
                        
                        // Correcção para quando existe um parametro do tipo objecto
                        // que não esteja preenchido não dê erro
                        if( vobj != null )
                        {
                            boObject obj = boObject.getBoManager().loadObject( boctx, ((Long)vobj).longValue() );
                            netgest.bo.plugins.data.MapType2Def mdef = netgest.bo.plugins.data.MapType2Def.getDataSourceDefinition( obj.getBoDefinition() );
                            if( mdef != null )
                            {
                                netgest.bo.plugins.data.MapType2Def.ObjectDS ds = mdef.getObjectDataSources().getDataSources()[0];
                                String[] atts = ds.getLocalAttributes();
                                for (int z = 0; z < atts.length; z++) 
                                {
                                    AttributeHandler att = obj.getAttribute( atts[z] );
                                    if( att != null )
                                    {
                                        try
                                        {
                                            att.getValueString();
                                        }
                                        catch( Exception e )
                                        {};
                                    }
                                }
                                if( obj.getDataSet().wasChanged() )
                                {
                                    obj.setCheckSecurity( false );
                                    obj.update();
                                }
                            }
                        }
                    }
                }
            }
            
            if(erros.size() > 0)
            {
                template.setFillParams(false);
                template.setFillListParams(false);
            }
        }
    }
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="<img style='cursor:hand' hspace='3' border='0' align='absmiddle' class='lui' title='Modelos' src='resources/GESTEMP_Template/ico16.gif' width='16' height='16'/><span title='Introdução de Parâmetros'>Introdução de Parâmetros</span>"
    function keyPressed(e, buttonName)
    {
        if(e)
        {
            var keycode = e.keyCode;
            if(keycode == 32 || keycode == 13)
            {
                 sendPage(buttonName);
            }
        }
    }
    function mouseDown(e, buttonName)
    {
        if(e && event.button == 1)
        {
             sendPage(buttonName);
        }
    }
    
    function sendPage(buttonName)
    {
        boForm.operation.value=buttonName;
        boForm.submit();
        wait();
    }
    
    function setPageFocus(fieldName)
    {
        loadField(fieldName);
        treatFocus();
    }
    
    function uncheckOthers(fieldName)
    {
        var elems = document.getElementsByTagName('input');
        for ( var i=0; i< elems.length ;i++)
        {
            if(elems[i].type == 'checkbox')
            {
                if(!elems[i].disabled && elems[i].checked && elems[i].id != fieldName)
                {
                    elems[i].value=0;elems[i].original=1;
                    elems[i].checked = false;
                }
            }
        }
    }
    function getValorInput(fieldId)
    {
        debugger;
    }

</script>
<%= GtTemplateViewer.writeJS() %>

<%@ include file='boheaders2.jsp'%>
<body onload='setPageFocus("<%=firstFieldID%>")'>
<TABLE id="dBody" class="layout" style='BACKGROUND-COLOR:#F2F7FA;' cellspacing="0" cellpadding="0">
<TBODY>
<%
if(erros.size() > 0)
{
%>
<TR>
    <TD class='error'>
        <div class='error'>
            <span style='font:13px'>&nbsp;&nbsp;<b>Corrija os seguintes erros:</b></span><br>
            <%
            for (int i = 0; i < erros.size(); i++) 
            {
                if( i > 0 )
                {
                    out.print("<br>");
                }
            %>
                <%
                
                String sMessage = (String)erros.get(i);
                if( sMessage != null )
                {
                    sMessage = sMessage.replaceAll( "\n", "<br>" );
                }

                out.print( sMessage );                
                %>
            <%
            } 
            %>
        </div>
    </TD>
</TR>
<%
}
%>
<TR height="100%" >
    <TD style="height:100%;width:100%;" valign=top align=left>
        <DIV style='overflow-y:auto;height:100%;'>
        
        <form class="objectForm" name="boForm" id="<%=IDX%>" method="POST" >
            <INPUT type='hidden' value='' name='operation' id = 'operation' />
            <INPUT type='hidden' value='<%=strTempBoui%>' name='inputObjectBoui' />
            <INPUT type='hidden' value='<%=IDX%>' name='docid' />
            <INPUT type='hidden' value='<%=testMode%>' name='testMode' />
            <INPUT type='hidden' value='<%=erros.size()>0?previousOperation:operation%>' name='previousOperation' />
            
                <%template.renderParameters(DOC.getController(),pageContext,IDX);%>        
        </form>
        </DIV>
    </TD>
</TR>
</TBODY>
</TABLE>
<FORM name='boFormSubmit' method='post'>
    
    <%
        java.util.Hashtable options = new java.util.Hashtable();    
    %>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM> 
</body>
</html>
<%
} finally {
     boctx.close();
     DOCLIST.releseObjects(boctx);
}
%>
