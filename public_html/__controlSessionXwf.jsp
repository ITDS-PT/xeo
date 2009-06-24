<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

boolean initPage=true;
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");

try {
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

//    int IDX;
//    int cvui;
//    boDefHandler bodef;
//    boDefAttribute atr;
//    String idbolist;
//    String[] ctrl;
//    
//    ctrl    = DOCLIST.processRequest(boctx);
//    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
//    idbolist=ctrl[1];
//    docHTML DOC = DOCLIST.getDOC(IDX);

    String docids = request.getParameter("closeDocIds");
    
    String ownerId = boctx.getPreferredPoolObjectOwner();
    
    if( docids != null && docids.length() > 0 )
    {
        boctx.setPreferredPoolObjectOwner( DOCLIST.poolUniqueId() );
        String[] xdocids = docids.split(";");
        DOCLIST.closeDocIds( boctx, xdocids );
        
//        for (int i = 0; i < xdocids.length; i++) 
//        {
//             boctx.setPreferredPoolObjectOwner(DOCLIST.poolUniqueId() );
//             docHTML doc = DOCLIST.getDocByIDX(Integer.parseInt(xdocids[i]),boctx);
//             
//             if( doc != null ) // && !doc.ignoreClose )
//             {
//                 if( doc.toClose != null && doc.toClose.size() > 0 )
//                 {
//                    for (int z = 0; z < doc.toClose.size(); z++) 
//                    {
//                         docHTML xrdoc = DOCLIST.getDocByIDX( ((Integer)doc.toClose.get(z)).intValue() , boctx );
//                         boPoolManager.realeaseAllObjects( xrdoc.poolUniqueId() ); 
////                         boPoolManager.realeaseAllObjects( DOCLIST.poolUniqueId() , xrdoc.poolUniqueId() );
//                    }
//                 }
//                 boctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( doc.poolUniqueId() );
//                 boctx.getApplication().getMemoryArchive().getPoolManager().destroyObject( doc );
//                 boPoolManager.realeaseAllObjects( DOCLIST.poolUniqueId() , doc.poolUniqueId() );
//            }
//        }
    }
    boctx.setPreferredPoolObjectOwner( ownerId );
    
//    boObject perf=boObject.getBoManager().loadObject(boctx,"Ebo_Perf",bosession.getPerformerBoui());
%>


<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<%--
<script>
function setAnnounce()
{
try{
  parent.parent.document.getElementById("announceArea").innerText='<%=netgest.xwf.core.xwfAnnounceImpl.getAnnounces( perf )%>';
}catch(e){}

}
</script>
--%>
<META HTTP-EQUIV="Refresh" CONTENT="120">
<title>Control Session </title>
<%--<body onload="setAnnounce()">--%>
<body>
    <form id='boFormSession' method='get' >
    <input type=hidden name='closeDocIds' value="">
    
</form>

</body>
<%
} catch ( Throwable e )
{

%>
<META HTTP-EQUIV="Refresh" CONTENT="120">
<title>Control Session </title>
<body >
    <form id='boFormSession' method='get' >
    <input type=hidden name='closeDocIds' value="">
    <pre><%e.printStackTrace();%> </pre>    
</form>

</body>
</html>
<%
} finally 
{
   if ( boctx!=null ) boctx.close();
   if ( DOCLIST!=null  && boctx!=null) DOCLIST.releseObjects(boctx);

}
%>
