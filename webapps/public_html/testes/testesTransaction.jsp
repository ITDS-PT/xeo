<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=false;
boolean commit = false;
boObject userObject = null;
try 
{

    boSession bosession = boApplication.getApplicationFromStaticContext("XEO")
        .boLogin("SYSUSER","ABC");
    
    boctx = bosession.createRequestContext(request,response,pageContext);
    
    userObject = boObject.getBoManager().loadObject( 
            boctx,
            boctx.getBoSession().getPerformerBoui()        
        );
    boctx.beginContainerTransaction();
    
    userObject.getAttribute("name").setValueString("teste3");
    userObject.getAttribute("preferedMedia").setValueString("1");
    userObject.update();
    
    System.out.println( userObject.get_IsInOnSave() == userObject.UPDATESTATUS_WAITING_COMMIT );
    
    userObject.getAttribute("name").setValueString("teste4");
    userObject.update();
    
    System.out.println( userObject.get_IsInOnSave() == userObject.UPDATESTATUS_WAITING_COMMIT );

    //boctx.getConnectionManager().setContainerTransactionForRollback();
    
    commit = true;
    
} 
finally 
{
    if( commit ) boctx.commitContainerTransaction();
    else boctx.rollbackContainerTransaction();
    
    if( boctx != null ) boctx.close();

    System.out.println( userObject.get_IsInOnSave() == userObject.UPDATESTATUS_WAITING_COMMIT );

}
%>
