<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {

boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__workPlaceListBar.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

    String[] ctrl;
    int IDX;int cvui;
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);

    docHTML doc = DOCLIST.getDOC(IDX);
    
    boolean ok = true;
    
    String workPlaceListBarName = null;
    long workPlaceListBarBoui = 0;
    boObject listBar = null;
    String img_logo = null;
    String description  = null;
    String markedActiveItem  = null;
    String activeItem  = null;
    bridgeHandler content = null;
    try 
    {
            
        workPlaceListBarName = request.getParameter( "listName" );
        try
        {
            workPlaceListBarBoui = Long.parseLong(request.getParameter( "listBoui" ));
        }
        catch(Exception e){/*Ignore*/}
        if(workPlaceListBarBoui > 0)
        {
            listBar = uiObjectManager.getUiObject( boctx, uiObjectManager.TYPE_LISTBAR ,workPlaceListBarBoui );
        }
        else
        {
            listBar = uiObjectManager.getUiObject( boctx, uiObjectManager.TYPE_LISTBAR);
        }
        
        img_logo = listBar.getAttribute("img").getValueString();
        description  = listBar.getAttribute("description").getValueString();
    
        markedActiveItem  = listBar.getAttribute("activeItem").getValueString();
        activeItem = null;
        
        content = listBar.getBridge("content");
        content.beforeFirst();
        String nameObject;
        while ( content.next() )
        {
            boObject o = content.getObject();
            nameObject = o.getAttribute("name").getValueString();            
            if ( nameObject.equals( markedActiveItem ) )
            {
                activeItem = markedActiveItem;
            }
        }
        if ( activeItem == null )
        {
            content.first();
            activeItem = content.getObject().getAttribute("name").getValueString();
        }
        
    } 
    catch (Exception ex) 
    {
        ok = false;   
    }

    boObject perf = boObject.getBoManager().loadObject(boctx, bosession.getPerformerBoui());

    if(ok)
    {
%>

<html>
<head>
<title></title>
<style>
@import url('ieThemes/0/global/ui-global.css');
@import url('ieLibrary/listBar/listBar.css');
@import url('ieThemes/0/listBar/ui-listBar.css');
</style>

<script>
document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA" &&  !(s=="DIV" && event.srcElement.contentEditable)) event.returnValue = false;}
function refreshCounters()
{
    var xframes = document.frames;
    for( var i=0;i<xframes.length;i++ )
    {
        if ( xframes[i].refreshTreeCounters )
        {
            xframes[i].refreshTreeCounters("TREE_EDIT");
        }
    }
}

</script>
</head>

<body scroll="no" >


<table defaultItem="<%=activeItem%>" class='listBar_Beh listBar' cellSpacing="0" cellPadding="2"  >
	
	 <tr  style='height:24px' >
	 	<td class='listBar_header' >
            <table>
                <tr>
                    <td class='listBar_header_description'><%=description%></td>
                    <td><span class='lui' onclick="refreshCounters();"><img title='Actualizar' align="absmiddle" src="ieThemes/0/menuTree/ghRefresh.gif" /></span></td>
                    <% if ( netgest.bo.security.securityRights.hasRights( perf, "uiListBar", bosession.getPerformerBoui() )
                            )
                    {
                    %>
                    <td><span class='lui' onclick="parent.winmain().openDocUrl('medium','__uilistbar_edit.jsp','','lookup');"><img title='Configurar Barras' align="absmiddle" src="ieThemes/0/menuTree/confBars.gif" /></span></td>
                    <%
                    }
                    else
                    {
                    %>
                    <td>&nbsp</td>
                    <%
                    }
                    %>
                </tr>
            </table>
        </td>
     </tr>
    
     <tr style='height:100%'>
	 	<td valign='top' style="padding:4px;">
             
             <%
                content.beforeFirst();
                String uiObjectName;
                long uiObjectBoui;
                while ( content.next() )
                {
                   boObject o = content.getObject();
                   uiObjectName=o.getAttribute("name").getValueString();
                   uiObjectBoui=o.getBoui();
             %>
             
                <div id="<%=uiObjectName%>" class='listBar_bodyItem' >
                <%
                    if ( o.getName().equals("uiTreeLB") )
                    {
                %>
                    <iframe src="__workPlaceTreeLB.jsp?docid=<%=IDX%>&treeBoui=<%=uiObjectBoui%>&treeName=<%=uiObjectName%>" style="width:100%;height:100%" scrolling="no"></iframe>
                <%
                    }
                    else if ( o.getName().equals("uiObjectHTML") )
                    {
                %>
                    <iframe src="__workPlaceObjectHTML.jsp?docid=<%=IDX%>&objectHTMLBoui=<%=uiObjectBoui%>&objectHTMLName=<%=uiObjectName%>" style="width:100%;height:100%" scrolling="no"></iframe>
                <%
                    }
                %>
                </div>
	 		
            <%
                }
            %>
	 		
	 	</td>
    </tr>
    <%
    content.beforeFirst();
    while ( content.next() )
    {
       boObject o = content.getObject();
     %>
    <tr relatedItem='<%=o.getAttribute("name").getValueString()%>' style='height:24px' >
	 	<td class='listBar_item' >
             <table>
                <tr>
                    <td class='listBar_header_description'><%=o.getAttribute("description").getValueString()%></td>
                    <%if("uiTreeLB".equals(o.getName()))
                    {
                     if ( netgest.bo.security.securityRights.hasRights( perf, "uiTreeLB", bosession.getPerformerBoui() )
                            )
                    {
                    %>
                    <td><span class='lui' onclick="parent.winmain().openDocUrl('medium','__uicontent_edit.jsp?name=<%=o.getAttribute("name").getValueString()%>','','lookup');"><img title='Configurar árvore' align="absmiddle" src="ieThemes/0/menuTree/confTree.gif" /></span></td>
                    <%
                    }
                    else
                    {
                    %>
                    <td>&nbsp</td>
                    <%
                    }                   
                    }
                    else
                    {
                    if (    netgest.bo.security.securityRights.hasRights( perf, "uiObject", bosession.getPerformerBoui() )
                            )
                    {
                    %>
                    <td><span class='lui' onclick="parent.winmain().openDocUrl('medium','__uicontent_edit.jsp?name=<%=o.getAttribute("name").getValueString()%>','','lookup');"><img title='Configurar html' align="absmiddle" src="ieThemes/0/menuTree/confObjectHtml.gif" /></span></td>
                    <%
                    }
                    else
                    {
                    %>
                    <td>&nbsp</td>
                    <%                    
                    }
                    }
                    %>            
                </tr>
            </table>
        </td> 
	 	
    </tr>
    <%
    }
    %>
</table>

</body>
</html>

<%
    }
    else
    {
%>
    <P><B>Lista de Opções por definir</B></P>
<% }
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
