<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.presentation.manager.uiObjectManager"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=__uilistbar_edit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
}

    boObject workPlace = uiObjectManager.getUiObject(boctx, uiObjectManager.TYPE_WORKPLACE);
    boObject list, auxBar;
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
    }
    response.sendRedirect("uilistbar_generaledit.jsp?method=edit&boui="+list.getBoui());

} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
