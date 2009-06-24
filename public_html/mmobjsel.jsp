<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="oracle.xml.parser.v2.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
response.setDateHeader ("Expires", -1);
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
String objName = "iContact";
int IDX;
try {
PageController control = new PageController();
boolean masterdoc=false;
if( request.getParameter("docid")==null ||request.getParameter("masterdoc")!=null ){
        masterdoc=true;
}
boSession bosession = (boSession)request.getSession().getAttribute("boSession");
if(bosession== null) {
    response.sendRedirect("login.jsp?returnPage=task_generalmywork.jsp");
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
docHTML_section sec;
docHTML_grid grid;
Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML doc = DOCLIST.getDOC(IDX);
boObjectList currObjectList = doc.getBoObjectListByKey(idbolist);
boObject BOI;

boObject mm = null;

if(request.getParameter("object")!=null)
{
	String sboui = request.getParameter("object");
	long lboui = netgest.utils.ClassUtils.convertToLong(sboui,-1);
	mm = doc.getObject(lboui);
}
String key = objName + "-" + doc.getEboContext().getBoSession().getPerformerBoui();
Explorer explorer = ExplorerServer.getExplorer(key);
if(mm != null)
{
	if(request.getParameter("addobjs") != null)
	{
		String[] objectos = request.getParameter("addobjs").split(";");
		for(int i = 0; i < objectos.length; ++i)
		{
			long boui = Long.parseLong(objectos[i]);
			boolean found = false;
			boObjectList list = mm.getBridge("selected_objects");
			list.beforeFirst();
			while(list.next() && !found)
				if(mm.getBridge("selected_objects").getObject().getBoui() == boui) found = true;
			if(!found) mm.getBridge("selected_objects").add(boui);
		}
	}
	else if(request.getParameter("gravar")!=null)
	{		
		if(explorer != null)
		{
			ngtXMLHandler treeUser=explorer.buildUserXML();
			ByteArrayOutputStream outba = new ByteArrayOutputStream();
			(( XMLDocument )treeUser.getNode()).print(outba);
			String s = outba.toString();
			mm.getAttribute("objects").setValueString(outba.toString());
		}
	}
	else
	{
		String conf = mm.getAttribute("objects").getValueString();
		if(conf.length()>0)
		{
			boDefHandler xbodef = boDefHandler.getBoDefinition(objName);
			ngtXMLHandler defTree = new ngtXMLHandler(conf);
			explorer = new Explorer(key,doc,DOCLIST,xbodef,Explorer.createExplorerViewer(xbodef,null),defTree.getFirstChild(),null);
			explorer.setShowPreview( false );
			ExplorerServer.addExplorer(key,explorer);
		}
	}
}
if(request.getParameter("object") != null)
{
	out.print("<form><input type='HIDDEN' name='object' id='object' value='"+request.getParameter("object")+"'>");
	out.print("<input type='HIDDEN' name='docid' id='docid' value='"+doc.getDocIdx()+"'>");
}
else out.print("<form>"); 

%>
<script language="JavaScript">
function addSelLines()
{
  //alert("Removing selecting lines"+boFormSubmit.docid.value);
  if ( window.rowSel.length>0 )
  {
	var bouis="";
	for(var i=0;i< window.rowSel.length;i++ )
	{
		if(bouis!="") bouis+=";";
		bouis+=window.rowSel[i].boui;
	}
	location.href="mmobjsel.jsp?object=<%=request.getParameter("object")%>&docid=<%=doc.getDocIdx()%>&addobjs=" + bouis;
	var win = parent.document.getElementById('inc_XEO_MailMerge__<%=mm.getBoui()%>__selected_objects');
	if(win.src)
		win.contentWindow.submitGrid();
  }
  else
  {
	alert( jsmessage_19 );
  }
}
</script>


<table cellpadding="0" cellspacing="0" border="0" width="100%">
<tr>
	<td>
		<SPAN CLASS="mnubar menu" colorOption="BAR">
		<table class='explorerfullTextSearch' cellpadding="0" cellspacing="2" border="0" width="100%">
			<tr>
				<td>
					<input type="SUBMIT" name="gravar" id="gravar" value="Gravar filtragem"/>
					<input type="BUTTON" name="addobjs" id="addobjs" onclick="addSelLines()" value="Adicionar aos objectos a aplicar"/>
				</td>
			</tr></form>
		</table>
		</SPAN>
	</td>
</tr>
<tr>
	</td>
<jsp:include page='__explorer.jsp' flush='true'>
<jsp:param name='objectName' value='<%=objName%>'/>
<jsp:param name='url' value='mmobjsel.jsp?pageName=null&objectName=<%=objName%>'/>
</jsp:include>
	</td>
</tr>
</table>

<%

} finally {
if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}
%>