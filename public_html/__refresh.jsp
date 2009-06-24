<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=true;
EboContext boctx=null;

//String cmd=null;
//String cmd_id=null;
//StringBuffer result=new StringBuffer();
String BOUI_ = (String)request.getParameter("BOUI");
int IDX = ClassUtils.convertToInt( (String)request.getParameter("docid") );  
try {
    String errors = null;
    Hashtable valTable = null;
    boolean totalRefresh = false;
    boolean parentRefresh = false;
    try 
    {
    
       if ( request.getParameter("boFormSubmitMode")!=null && ("11".equals((String)request.getParameter("boFormSubmitMode")) || "20".equals((String)request.getParameter("boFormSubmitMode"))))
        {
        
        boctx = (EboContext)request.getAttribute("a_EboContext");
        boSession bosession = (boSession)request.getSession().getAttribute("boSession");
        if(bosession== null) {
            response.sendRedirect("login.jsp");
            return;
        }
        if(boctx==null) {
            boctx = bosession.createRequestContext(request,response,pageContext);
            request.setAttribute("a_EboContext",boctx);
        }
    
        int cvui;
        
        boDefHandler bodef;
        boDefAttribute atr;
        String idbolist;
        String[] ctrl;
        
        BOUI_    = request.getParameter("BOUI");
        request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
        ctrl    = DOCLIST.processRequest(boctx);
        IDX     = ClassUtils.convertToInt(ctrl[0],-1);
        idbolist=ctrl[1];
        docHTML DOC = DOCLIST.getDOC(IDX);
        valTable = DOC.getValues();
        totalRefresh = DOC.totalRefresh();
        parentRefresh = DOC.parentRefresh();
       }
        else
        {
          initPage=false;
       }
    } 
    catch (Throwable e)
    {
        java.io.CharArrayWriter cw = new java.io.CharArrayWriter();
        java.io.PrintWriter pw = new java.io.PrintWriter( cw );
        e.printStackTrace( pw );
        pw.close();
        cw.close();
        
        
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<SCRIPT language=javascript src="rfh.js"></SCRIPT>
<script>
var cmdsPool=[];
function executeCmds()
{
    var meth;
    var ready;
    var exit = false;

    while(cmdsPool.length > 0 && !exit)
    {
        exit = false;
        
        meth = cmdsPool[cmdsPool.length - 1][1];
        if(meth.indexOf('setValueBoolean') != -1 || 
            meth.indexOf('setVisibleBoolean') != -1 ||
            meth.indexOf('setDisableBoolean') != -1
            )
        {
            var xw = parent.document.getElementsByName(cmdsPool[cmdsPool.length - 1][0]);
            ready = true;
            if(xw && xw.length > 0)
            {
                for(var j=0; j< xw.length; j++)
                {
                    if(xw[j].readyState != 'complete')
                    {
                        ready = false;   
                    }
                }
            }
            else
            {
                cmdsPool.pop();
            }
            if(ready)
            {
                if(meth.indexOf('setValueBoolean') != -1)                
                    setValueBoolean(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                if(meth.indexOf('setVisibleBoolean') != -1 )
                    setVisibleBoolean(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3], cmdsPool[cmdsPool.length - 1][4]);
                if(meth.indexOf('setDisableBoolean') != -1 )
                    setDisableBoolean(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3], cmdsPool[cmdsPool.length - 1][4]);
                cmdsPool.pop();
            }
            else
            {
                exit = true;
            }
        }
        else if(meth.indexOf('setRequiredBoolean') != -1)
        {
            var hiddenName = cmdsPool[cmdsPool.length - 1][0].substr(8);
            var xw = parent.document.getElementsByName(hiddenName);
            if(xw && xw.length > 0)
            {               
                if(xw[0].readyState == 'complete')
                {
                    setRequiredBoolean(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3], cmdsPool[cmdsPool.length - 1][4]);
                    cmdsPool.pop();
                }
                else
                {
                    exit = true;
                }              
            }
            else
            {
                cmdsPool.pop();
            }    
        }
        else if(meth.indexOf('updateInFrames') != -1)
        {
            var ifrm=parent.document.getElementsByTagName('IFRAME');
            var found = false;
            //debugger;
            for(var i=0; i < ifrm.length ; i++)
            {
                if ( ifrm[i].id == cmdsPool[cmdsPool.length - 1][0] )
                {
                    found = true;
                    if(ifrm[i].readyState=='complete')
                    {
                        updateInFrames(cmdsPool[cmdsPool.length - 1][2]);
                        cmdsPool.pop();
                    }
                    else
                    {
                        exit = true;
                    }
                }
            }
            if(!found)
            {
                cmdsPool.pop();
            }
        }
        else if(meth.indexOf('refreshDocument') != -1)
        {
            if(parent.document.readyState=='complete')
            {
                refreshDocument();
                cmdsPool.pop();
            }
            else
            {
                exit = true;
            }
        }
        else if(meth.indexOf('refreshParentField') != -1)
        {
            refreshParentField(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
            cmdsPool.pop();
        }
        else if(meth.indexOf('updateLookupAttribute') != -1)
        {
            var obj = parent.document.getElementById('tblLook'+cmdsPool[cmdsPool.length - 1][0]);
            if(obj != null)
            {
                if(obj.readyState == 'complete')
                {
                    updateLookupAttribute(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                    cmdsPool.pop();
                }
                else
                {
                    exit = true;
                }
            }
            else
            {
                cmdsPool.pop();
            }
        }
        else if(meth.indexOf('setRequiredLookup') != -1 ||
            meth.indexOf('setDisableLookup') != -1
            )
        {
            var obj = parent.document.getElementById(cmdsPool[cmdsPool.length - 1][3]);
            if(obj != null)
            {
                if(obj.readyState == 'complete')
                {
                    if(meth.indexOf('setRequiredLookup') != -1)
                    {
                        setRequiredLookup(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3], cmdsPool[cmdsPool.length - 1][4]);
                    }
                    else
                    {
                        setDisableLookup(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3], cmdsPool[cmdsPool.length - 1][4]);
                    }
                    cmdsPool.pop();
                }
                else
                {
                    exit = true;
                }
            }
            else
            {
                cmdsPool.pop();
            }
        }
        else if(meth.indexOf('setValue') != -1 ||
            meth.indexOf('setDisable') != -1 ||
            meth.indexOf('setVisible') != -1 ||
            meth.indexOf('setRequired') != -1
            )
        {
            var obj = parent.document.getElementById(cmdsPool[cmdsPool.length - 1][0]);
            if(obj != null)
            { 
                if(obj.readyState == 'complete')
                {
                    if(meth.indexOf('setValue') != -1)
                    {
                        setValue(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                    }
                    if(meth.indexOf('setDisable') != -1)
                    {
                        setDisable(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                    }
                    if(meth.indexOf('setVisible') != -1)
                    {
                        setVisible(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                    }
                    if(meth.indexOf('setRequired') != -1)
                    {
                        setRequired(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
                    }
                    cmdsPool.pop();
                }
                else
                {
                    exit = true;
                }
            }
            else
            {
                cmdsPool.pop();
            }
        }
        else if(meth.indexOf('refreshValues') != -1)
        {
            if(parent.document.readyState=='complete')
            {
                refreshParentValues();
                cmdsPool.pop();
            }
            else
            {
                exit = true;
            }
        }
		else if(meth.indexOf('setMessageInformation') != -1)
		{
			setMessageInfo(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
            cmdsPool.pop();            
		}
    else if(meth.indexOf('setSectionShowing') != -1)
		{
			setSectionShowing(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
            cmdsPool.pop();            
		}
    else if(meth.indexOf('setButtonDisabled') != -1)
		{
			setButtonDisabled(cmdsPool[cmdsPool.length - 1][2], cmdsPool[cmdsPool.length - 1][3]);
            cmdsPool.pop();            
		}
        else
        {
            //alert('Atenção:' + meth);
        }
    }
    if(cmdsPool.length > 0)
    {
        window.setTimeout('executeCmds()',200);
    }
}
function loadValues()
{
    //alert(loadValues);

<%  
        boolean codeToExecute = true;
        if(totalRefresh)
        {
            codeToExecute = true;
%>
    cmdsPool[cmdsPool.length] = ['parentFrame', 'refreshDocument']; 
<%         
        }
        else if( valTable!= null && valTable.size() > 0)
        {
            Set keys = valTable.keySet();
            Iterator it = keys.iterator();
            String aux, value;
            String visible;
            String disabled;
            String required;
            String butNext;
            String butPrev;
            String butEnd;
            HtmlField field;
            String labelName;
            boolean isBool;
            boolean isSection;
            while(it.hasNext())
            {
                field = (HtmlField)valTable.get(it.next());
                if(field.isToRefresh())
                {
                    codeToExecute = true;
                    aux = field.getHtmlId();
                    value = field.getTreatValue();
                    labelName = field.getLabelName();
                    visible = field.getHidden() ? "false":"true";
                    disabled = field.getDisable() ? "true":"false";
                    required = field.getRequired() ? "true":"false";
                    isBool = field.getBool();
										isSection = field.isSection();
										
                    if(isBool)
                    {
                        %>
                        cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setValueBoolean', '<%=aux%>', '<%=value%>' ];
                        cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setVisibleBoolean', '<%=aux%>', '<%=labelName%>', '<%=visible%>'];
                        cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setRequiredBoolean', '<%=aux%>', '<%=labelName%>', '<%=required%>'];
                        cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setDisableBoolean', '<%=aux%>','<%=labelName%>', '<%=disabled%>'];
                        <%
                        
                    }
                    else if(isSection)
                    {
                      
                        %>
                        cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setSectionShowing', '<%=aux%>', '<%=visible%>'];
                        <%
                        if("true".equals(visible))
                        {
                          butNext = field.isButtonNextDisabled() ? "true":"false";
                          butPrev = field.isButtonPreviousDisabled() ? "true":"false";
                          butEnd = field.isButtonEndDisabled() ? "true":"false";
                        %>
                        cmdsPool[cmdsPool.length] = ['buttonNext', 'setButtonDisabled', 'buttonNext', '<%=butNext%>'];
                        cmdsPool[cmdsPool.length] = ['buttonPrevious', 'setButtonDisabled', 'buttonPrevious', '<%=butPrev%>'];
                        cmdsPool[cmdsPool.length] = ['buttonEnd', 'setButtonDisabled', 'buttonEnd', '<%=butEnd%>'];
                        <%
                        }
                    }
                    else if(aux.equals("setMessageInformation"))
                    {
        %>
            cmdsPool[cmdsPool.length] = ['messageInformation', 'setMessageInformation', '<%=value%>']; 
        <%                           
                    }
                    else if(aux.startsWith("inc_"))
                    {
        %>
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'updateInFrames', '<%=aux%>']; 
        <%                
                    }
                    else if(aux.startsWith("submit_"))
                    {
        %>        
            cmdsPool[cmdsPool.length] = ['<%=value%>', 'refreshParentField', '<%=value%>', '<%=field.getBdId()%>']; 
        <%                
                    }
                    else
                    {
    
                        if(value != null && value.indexOf("tblLook") != -1)
                        {
        %>
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'updateLookupAttribute', '<%=aux%>', '<%=value%>']; 
        <%
                        }
                        else
                        {
        %>
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setValue', '<%=aux%>', '<%=value%>']; 
        <%
                        }
        %>    
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setVisible', '<%=aux%>', '<%=visible%>'];
        <%
                        if(labelName == null)
                        {
        %>
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setRequired', '<%=aux%>', '<%=required%>'];
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setDisable', '<%=aux%>', '<%=disabled%>'];
        <%
                        }
                        else
                        {
         %>
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setRequiredLookup', '<%=labelName%>','<%=aux%>', '<%=required%>'];
            cmdsPool[cmdsPool.length] = ['<%=aux%>', 'setDisableLookup', '<%=labelName%>','<%=aux%>', '<%=disabled%>'];
         <%
                        }
                    }
                }
            }
            //verificar se é para mandar o pai efectuar o refresh
            if(parentRefresh)
            {
                codeToExecute = true;
            %>
                cmdsPool[cmdsPool.length] = ['parentFrame', 'refreshValues'];
            <%
            }
        }
        if(codeToExecute)
        {
        %>
            executeCmds();
        <%
        }
%>
}
</script>
<title></title>
<%if( initPage ){ %>
<body onload = "try{loadValues();}catch(e){parent.noWait();} parent.noWait();">
<%} else{%>
<body>
<%}%>
<FORM name='boFormSubmit' method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='clientIDXtoClose' />    
    <INPUT type='hidden' name=method value='list' /> 
    <INPUT type='hidden' name='boFormSubmitMode' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' name='BOUI' value='<%= BOUI_ %>'/>
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />    
    
</FORM>
</body>
<%
} finally {
    if(initPage){if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
