<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*,java.io.*"%>
<%@ page import="java.text.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession,netgest.io.*,com.oreilly.servlet.*"%>
<%@ page import="oracle.xml.parser.v2.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%


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

    int IDX;
    int cvui;
    boDefHandler bodef;
    //boDefAttribute atr;
    String idbolist;String[] ctrl;
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);

    docHTML_treeRuntime xtree=docHTML_treeServer.getTree(request.getParameter("viewer"));
    if(xtree==null)
    {
      bodef = boDefHandler.getBoDefinition(request.getParameter("vobject"));
      xtree=docHTML_treeServer.getTree(bodef, bodef.getViewer("general").getChildNode("forms").getChildNode(request.getParameter("vname")).getChildNode("treeView"), DOC);
    }
    
    exportList ex = new exportList(DOC.getEboContext(), xtree);
    StringBuffer sb = ex.getPrintList();
    
%>
<html>
	<head>
		<style type="text/css">

.footer{width:100%;border-top:2px  #6297E5;}
.credentials{font-size:7pt;}
.labelDst{color:green;font-weight:bold}

.grid{ width:100%;cellpadding:2pt; cellmargin:1;font-size:9pt; table-layout:fixed }
.gridCHeader{ font-weight:normal; background-color:#C9DBF7;border:1px solid #6297E5;padding:1px}
.gridCBody{ border-bottom:1px solid #CCCCCC }

body { font-size:8pt; font-family:Helvetica, Symbol ;padding:0pt;background-color:#D9E5E8;margin:0}

div.titleReport{font-size:21px;font:arial;text-align:center;width:100%;background-color:#FFFFFF;border-top:1px solid #475A76;border-left:1px solid #475A76;border-right:1px solid #475A76   }
div.reportBody{ background-color:#FFFFFF;padding:5px;border-right:1px solid #475A76;border-left:1px solid #475A76;border-bottom:1px solid #475A76;width:100%;}
div.report{ background-color:#D9E5E8;font:Arial;padding:10pt;width:100%;}

@media print{
  .print { display:none }
  .noprint{display:none}
  .toolbar{display:none}
}
@media screen{
  .print {
  	border:1px solid #FFFFFF;PADDING-RIGHT: 5px;color:#000000;PADDING-LEFT: 5px;FONT:13px;
  	background-color:#C9DBF7;
	CURSOR: hand;
  }
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
		<title>netgest.XEO Impressão de Listagem de <%=xtree.p_bodef.getLabel()%></title>
	</head>
	<body margin="0pt" scroll="auto">
<table width='100%'>
<tr>
<td width='100%'>

		<div class="toolbar">
			<button class="print" onclick="window.print()">Imprimir</button>
			<br class="noprint"/>
		</div>
    
		<div class="report">
			<div class="titleReport">
				<br/>Listagem de <%=xtree.p_bodef.getLabel()%><br/>
      </div>
			<div class="reportBody">

<%
        out.print(sb);
%>    
      </div>
      <div class="credentials">
        Data de Impressão: <%=(new java.util.Date()).toString()%> por <%=boObject.getBoManager().loadObject(DOC.getEboContext(),DOC.getEboContext().getBoSession().getPerformerBoui()).getAttribute("username").getValueString()%>
      </div>  
    </div>
		
    <br class="noprint"/>
		<div class="toolbar">
			<button class="print" onclick="window.print()">Imprimir</button>
		<br class="noprint"/>
		</div>
	</td>
	</tr>
	</table>	
  </body>
</html>

<%    
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>