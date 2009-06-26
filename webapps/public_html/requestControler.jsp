<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

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
    response.sendRedirect("login.jsp?returnPage=requestControler.jsp");
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
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
docHTML DOC = DOCLIST.getDOC(IDX);
boObject BOI = null;
if ( request.getParameter("changedObject") == null )
  return;
long bo_boui = Long.parseLong(request.getParameter("changedObject"));
BOI= BOI.getBoManager().loadObject(boctx,bo_boui);

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>«Request Controler»</title>
<%@ include file='boheaders.jsp'%>
<style>
@media screen{
  .toolbar{ 
  BORDER-RIGHT: #113469 1px solid;
	BORDER-LEFT: #A4C3F0 1px solid;
	BORDER-BOTTOM:#113469 1px solid;
	BORDER-TOP:#A4C3F0 1px solid;
	WIDTH: 100%; COLOR: #FFFFFF; 
	BACKGROUND-COLOR: #6297E5;
	padding:3px;  
   }
}
</style>
</head>
<body>

<%

if(BOI.getParameter("requestObjects")!=null)
{

    StringTokenizer st = new StringTokenizer(BOI.getParameter("requestObjects"), "-");
    boObject[] requests = new boObject[st.countTokens()];
    for(int i=0;st.hasMoreTokens();i++)
    {
       requests[i] = BOI.getBoManager().loadObject(BOI.getEboContext(),
                Long.parseLong(st.nextToken()));
      
      String mode = requests[i].getAttribute("action").getValueString();
      String label = "Pedido ";
      if("CHANGE".equals(mode))
          label += "de Alteração do";
      else if("CREATE".equals(mode))
          label += "de Criação de um";
      else if("DESTROY".equals(mode))
          label += "Para Apagar o";
      label += " Objecto " + requests[i].getAttribute("changedObject").getObject().getBoDefinition().getLabel();
                
      %>
          <div class="toolbar">
            <b><%=label%></b>
            <br class="noprint"/>
          </div>
      <FORM class="objectForm" name="boForm">
            <IFRAME id='inc_<%=requests[i].getName()%>__<%=BOI.bo_boui%>' src='<%=""+requests[i].getName().toLowerCase()+"_generalshortedit.jsp?docid="+IDX+"&menu=no&method=edit&boui="+requests[i].getBoui()+"&object="+requests[i].getName()+""%>' frameBorder=0 width='100%' scrolling=no height='50%'></IFRAME>      
            <br><br>
      <%                  
    }
}
else
{
String mode = BOI.getParameter("MODE");

boObject[] changedObjects = netgest.bo.utils.boRequest.getRequestChangedObjects(BOI);
String requestObjects = "";
for (int i = changedObjects.length-1; i >= 0; i--) 
{
    String jspName = changedObjects[i].getBoDefinition().getModifyProtocol();
    
    boObject[] logs = new boObject[0];
    if(!"DESTROY".equals(mode)){
      logs = netgest.bo.utils.boRequest.createRequestData(BOI.getEboContext(), changedObjects[i]);
      if((logs.length == 0) && "CHANGE".equals(mode))
        continue;
    }

    boObject changeRequest = changedObjects[i].getRequestObject();

    changeRequest.getAttribute("changedObject").setValueLong(changedObjects[i].getBoui());

    bridgeHandler bh = changeRequest.getBridge("log");
    for (int j = 0; j < logs.length; j++)
    {
      bh.add(logs[j].getBoui());
    }
    
    changeRequest.getAttribute("action").setValueString(mode);
    String boui = "" + changeRequest.getBoui();
    requestObjects += boui + "-";
    
    String label = "Pedido ";
    if("CHANGE".equals(mode))
        label += "de Alteração do";
    else if("CREATE".equals(mode))
        label += "de Criação de um";
    else if("DESTROY".equals(mode))
        label += "Para Apagar o";
    label += " Objecto " + changedObjects[i].getBoDefinition().getLabel();
    
%>
		<div class="toolbar">
			<b><%=label%></b>
			<br class="noprint"/>
		</div>
<FORM class="objectForm" name="boForm">
      <IFRAME id='inc_<%=jspName%>__<%=BOI.bo_boui%>' src='<%=""+jspName.toLowerCase()+"_generalshortedit.jsp?docid="+IDX+"&menu=no&method=edit&boui="+boui+"&object="+jspName+""%>' frameBorder=0 width='100%' scrolling=no height='50%'></IFRAME>      
      <br><br>
<%  
}

     BOI.setParameter("requestObjects",requestObjects);
}

%>
</FORM>

<FORM name='boFormSubmit' method='post'>
<INPUT type='hidden' name='boFormSubmitXml' />
<INPUT type='hidden' name='boFormSubmitMode' />
<INPUT type='hidden' name='boFormSubmitId' />
<INPUT type='hidden' name='BOUI' value='<%=BOI.getBoui()%>' />
<INPUT type='hidden' value='<%=IDX%>' name='docid' />
</FORM>
<%
}catch(Exception e){  out.print(e.getMessage());}
finally {
if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}%>
</body>
</html>