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
    response.sendRedirect("login.jsp?returnPage=ebo_filter_generaledit.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

int IDX;int cvui;
String idbolist;
String[] ctrl;
docHTML_section sec;
docHTML_grid grid;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
boObject BOI;
if ( currObjectList == null ) BOI=null;
else BOI=currObjectList.getObject();
 if( currObjectList != null ) currObjectList.first();
String name = null;
String URLquery = null;
String viewer = null;
String result = null;
String whereSql = null;
boolean clean = false;
%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
function refresh(query, objName, result)
{
    if(result)
    {
        alert(result);
    }
	else if(query && objName)
	{
		var ifrm=parent.document.getElementsByTagName('IFRAME');
		var found = false;
		for(var i=0; i < ifrm.length && !found; i++)
		{
			if ( ifrm[i].id == 'frQueryResult')
			{
				ifrm[i].contentWindow.location.href='__list.jsp?menu=no&showSearchComponents=false&look_query='+query+'&look_object='+objName;
				found=true;
			}
		}
	}
}
function clean(objName)
{
	var text=parent.document.getElementsByTagName('textArea');
	var found = false;
	for(var i=0; i < text.length && !found; i++)
	{
        if ( text[i].name &&  text[i].name.indexOf('_boql') != -1)
		{
            if(objName && objName!="")
            {
                text[i].value = 'select ' + objName + ' where ';
            }
            else
            {
                text[i].value = '';
            }
		}
	}
}
</script>

<%
        String boqlQuery      = request.getParameter("boqlQuery");
		String clsRegBoui      = request.getParameter("objectBoui");
		String loading      = request.getParameter("loading");
        String clsName = null;
    
        if(clsRegBoui != null && !"".equals(clsRegBoui))
        {
            long boui = 0;
            try
            {
                boui = Long.parseLong(clsRegBoui);
                boObject clsReg = boObject.getBoManager().loadObject(boctx, boui);
                clsName = clsReg.getAttribute("name").getValueString();
            }catch(Exception e){}
            if(boqlQuery == null)
            {
                if(clsRegBoui != null && !"".equals(clsRegBoui))
                {
                    clean = true;
                }            
            }
            else
            {
                netgest.bo.ql.QLParser x= new netgest.bo.ql.QLParser();            
                try
                {
                    result=x.toSql(boqlQuery,boctx);
                    name = x.getObjectName();
                    viewer = x.getViewer();
                    URLquery = x.getURLboql();
                    whereSql = URLquery.substring(URLquery.toUpperCase().indexOf("WHERE") + 6);
                    result = null;
                    if(!name.equals(clsName))
                    {
                        result = "Erro: o select deve referir o mesmo objecto das definições [" + clsName +"]";
                    }
                 }
                 catch(netgest.bo.ql.boqlParserException boqlE)
                 {  
                    result = "Erro: " + boqlE.getMessage();
                            result=result.replaceAll("\\\\", "\\\\\\\\");
                    result = result.replaceAll("'", "\\\\'");
                    result = result.replaceAll("\n", "\\\\\\n");
                    result = result.replaceAll("\r", "\\\\\\r");
                 }
            }
        }
        else
        {
			if(!"true".equalsIgnoreCase(loading))
            {
                result = "Erro: Prencha o atributo objecto nas defini败s";
            }
        }
 %>
<%
if(clean)
{
%>
    <body onload="clean('<%=clsName%>')">
<%
}
else if(result == null)
{
    if(URLquery != null && clsName != null)
    {%>
    <body onload="refresh('<%=whereSql%>', '<%=clsName%>')">
<%      }
    else
    {
%>
        <body>
<%
    }
}
else{%>
    <body onload="refresh('<%=whereSql%>', '<%=clsName%>', '<%=result%>')">
<%}%>
</body></html><%
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
