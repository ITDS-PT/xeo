<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.xwf.common.*"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.bo.presentation.render.elements.Element"%>
<%@ page import="netgest.bo.presentation.render.elements.Preview"%>
<%@ page import="netgest.bo.presentation.render.PageController"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.Browser"%>

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
    response.sendRedirect("login.jsp");
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
    request.setAttribute(ControllerFactory.CONTROLLER_NAME_KEY,"XwfController");
    ctrl= DOCLIST.processRequest(boctx);    
    IDX= ClassUtils.convertToInt(ctrl[0],-1);    
    String parent_boui=request.getParameter("parent_boui");
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

    String previewBoui =  request.getParameter("previewBoui");
    String frameId =  request.getParameter("frameID");
    boObject activity = DOC.getController().getObject(ClassUtils.convertToLong(previewBoui));
    
    StringBuffer result = new StringBuffer();
    
    AttributeHandler message =  activity.getAttribute("message");
    if ( message != null && message.getValueLong()!=0 )
    {
        //render da mensagens
       if ( message.getObject().getAttribute("value").getValueLong() !=0 )
       {
                message = message.getObject().getAttribute("value").getObject().getAttribute("valueObject");
                if( message.getValueLong() == 0 )
                {
                    message=null;
                }
        }
       else
       {
            message = null;
       }
       
       
    }
    else
    {
        message= null;
    }
    bridgeHandler bridge = activity.getBridge("variables");
    
    bridge.beforeFirst();
    boObject variable = null;
    boObject value = null;
    String label = null;
    String attrName = null;    
    long type = -1;
    long maxoccurs =  -1;
    String cssURL = Browser.getThemeDir() + "report/report.css;";
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
 <style type="text/css">
    @import url('<%=cssURL%>');
</style>
<script>
function init()
{
    <%
        if(frameId != null && frameId.length() > 0)
        {
    %>  
        
          parent.document.getElementById('<%=frameId%>').style.height=container.offsetHeight+20;
    <%}%>
}
</script>
</head>
<body onload='init();'>

    <div id='container' style='width:100%;padding:0px;'>
    
    <div class="reportInnerBody">
        <%if( message != null )
         {
         
                Preview mpreview = new Preview( message.getObject(),"preview",null);
                String iframeName = "iframe" + System.currentTimeMillis();
                mpreview.setPreviewID(iframeName);
                
                mpreview.setParameters("frameID="+iframeName+"&designPrint=false&designHeader=false");
            %>
        <%= mpreview.getHTML(DOC,DOCLIST,new PageController())%>        
        <%}
        if(bridge.getRowCount() > 0)
        {
        %>
        
        <div class="area_title">
            <p>Atributos</p>
        </div>    
        
        <table class="section">
            <tr>
                <td width="10%"/>
                <td width="40%"/>
                <td width="10%"/>
                <td width="40%"/>
            </tr>
        </table>            
        
        <% while(bridge.next())
           { 
                variable = bridge.getObject();
                value = variable.getAttribute("value").getObject();
                label = variable.getAttribute("label").getValueString();
                attrName = xwfHelper.getTypeName(value);
                type = value.getAttribute("type").getValueLong();
                
                if(type == boDefAttribute.VALUE_UNKNOWN)
                {
                    maxoccurs =  value.getAttribute("maxoccurs").getValueLong();
                    if(maxoccurs == 1)
                    {
                        Preview preview = new Preview(value.getAttribute(attrName).getObject(),"preview",null);
                        String iframeName = "iframe" + System.currentTimeMillis();
                        preview.setPreviewID(iframeName);
                        preview.setParameters("frameID="+iframeName+"&designPrint=false&designHeader=false");
                    %>
                        <%= preview.getHTML(DOC,DOCLIST,new PageController())%>
                    <%
                    }
                    else if(maxoccurs > 1)
                    {
                        boObject clsreg = value.getAttribute("object").getObject();
                        boDefHandler bDefHandler = boDefHandler.getBoDefinition(clsreg.getAttribute("name").getValueString());
                        ngtXMLHandler[] columns = bDefHandler.getViewer("general").getForm("list").getChildNode("grid").getChildNode("cols").getChildNodes();                
                    
                    %>
                        <div class="area_title">
                            <p><%=label%></p>
                        </div>
                        
                        <table class="grid">
                            <thead>      
                            <TR>
                    <%
                        //Colmuns
                        for (int i = 0; i < columns.length; i++)
                        {                                
                    %>
                            <TH class="gridCHeader"><%=bDefHandler.getAttributeRef(columns[i].getChildNode("attribute").getText()).getLabel()%></TH>
                      <%}%>
                            </TR>
                            </thead>
                            <tbody>                            
                    <%
                        // Body
                        boObject valueObject = null;
                        bridgeHandler list = value.getBridge(attrName);                        
                        list.beforeFirst();
                        while(list.next())
                        {
                    %>
                            <TR>
                    <%
                            valueObject = list.getObject();
                            for (int i = 0; i < columns.length; i++)
                            {       
                                AttributeHandler attrHandler = valueObject.getAttribute(columns[i].getChildNode("attribute").getText());
                                if(attrHandler.getDefAttribute().getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE)
                                {
                                    if(attrHandler.getDefAttribute().getRelationType() == boDefAttribute.RELATION_1_TO_1){
                                        result.append(attrHandler.getObject().getCARDID());
                                    }
                                }
                                else
                                {
                                    result.append(attrHandler.getValueString());   
                                }                                                        
                    %>
                            <TD class="gridCBody">
                                <%=result.toString()%>
                            </TD>
                    <%      }%>
                            </TR>
                    <%   }%>                    
                            </tbody>
                        </table>                    
                    <%                    
                    }                    
                }
                else if(type == boDefAttribute.VALUE_BOOLEAN)
                {
                    String bol = value.getAttribute(attrName).getValueString();
                    if(bol != null && !"".equals(bol))
                    {
                        if("1".equals(bol))
                        {
                            result.append("Sim");
                        }
                        else if("0".equals(bol))
                        {
                            result.append("Não");
                        }
                    }
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=result.toString()%>
                            </td>
                        </tr>
                    </table>   
                <%                
                }
                else if(type == boDefAttribute.VALUE_NUMBER)
                {
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>
                    </table>   
                <%                                            
                }
                else if(type == boDefAttribute.VALUE_DATETIME)
                {
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>
                    </table>   
                <%                
                }       
                else if(type == boDefAttribute.VALUE_DATE)
                {
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>
                    </table>   
                <%                
                }    
                else if(type == boDefAttribute.VALUE_CLOB)
                {
                %>
                    <table class="section">
                        <tr>
                            <td class="label" width="10%"><%=label%></td>                            
                        </tr>                    
                        <tr>
                            <td width="100%" colspan="4" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>            
                    </table>
                <%
                } 
                else if(type == boDefAttribute.VALUE_CHAR)
                {
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>
                    </table>   
                <%
                }        
                else if(type == 12)
                {
                %>
                    <table class="section">            
                        <tr>
                            <td class="label" width="10%"><%=label%></td>
                            <td width="90%" colspan="3" class="input">
                                <%=value.getAttribute(attrName).getValueString()%>
                            </td>
                        </tr>
                    </table>   
                <%                
                }           
        } }%>
    </div>
    </div>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
