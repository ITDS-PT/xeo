<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.manager.uiObjectManager"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
String name = request.getParameter("name");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__uitreelb_edit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
}

    boObject workPlace = uiObjectManager.getUiObject(boctx, uiObjectManager.TYPE_WORKPLACE);
    boObject list, auxBar, content = null;
    String s = workPlace.getAttribute("user").getValueString(); 
    if(workPlace.getAttribute("user").getValueString() == null || "".equals(workPlace.getAttribute("user").getValueString()))
    {
        workPlace = workPlace.cloneObject();
        workPlace.getAttribute("user").setValueLong(boctx.getBoSession().getPerformerBoui());
        list = workPlace.getAttribute("listbar").getObject();
        list.getAttribute("user").setValueLong(boctx.getBoSession().getPerformerBoui());
        ArrayList r = new ArrayList();
        bridgeHandler bh = list.getBridge("content");
        boBridgeIterator bhIt = bh.iterator();
        bhIt.beforeFirst();
        while(bhIt.next())
        {
            auxBar = bhIt.currentRow().getObject().cloneObject();
            auxBar.getAttribute("user").setValueLong(boctx.getBoSession().getPerformerBoui());
            if(name.equals(auxBar.getAttribute("name").getValueString()))
            {
                content = auxBar;
            }
            r.add(auxBar);
        }
        bh.truncate();
        for (int i = 0; i < r.size(); i++) 
        {
            bh.add(((boObject)r.get(i)).getBoui());
        }
        workPlace.update();
    }
    else
    {
        list = workPlace.getAttribute("listbar").getObject();
        bridgeHandler bh = list.getBridge("content");
        boBridgeIterator bhIt = bh.iterator();
        bhIt.beforeFirst();
        while(bhIt.next())
        {
            auxBar = bhIt.currentRow().getObject();
            if(name.equals(auxBar.getAttribute("name").getValueString()))
            {
                content = auxBar;
            }
        }
    }
    if("uiTreeLB".equals(content.getName()))
    {
        response.sendRedirect("uitreelb_generaledit.jsp?method=edit&boui="+content.getBoui());
    }
    else
    {
        response.sendRedirect("uiobjecthtml_generaledit.jsp?method=edit&boui="+content.getBoui());
    }

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>