<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.security.*"%>
<%@ page import="netgest.xwf.presentation.*"%>
<%@ page import="netgest.bo.controller.xwf.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="netgest.bo.controller.*"%>
<%@ page import="netgest.xwf.common.xwfHelper"%>
<%@ page import="netgest.bo.ql.*"%>
<%@ page import="netgest.bo.controller.basic.BasicPresentation"%>
<%@ page import="netgest.bo.presentation.render.elements.*"%>
<%@ page import="netgest.bo.presentation.render.*"%>
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
    response.sendRedirect("login.jsp?returnPage=claim_generaledit.jsp");
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
    request.setAttribute(XwfKeys.MAIN_CONTROLLER_KEY,"true");
    ctrl= DOCLIST.processRequest(boctx);
    IDX= ClassUtils.convertToInt(ctrl[0],-1);
    String parent_boui=request.getParameter("parent_boui");
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
    boObject BOI;
    String method=request.getParameter( "method" );
    String inputMethod=request.getParameter( "method" );
    String requestedBoui=request.getParameter( "boui" );
    if ( currObjectList == null ) BOI=null;
    else BOI=currObjectList.getObject();
    
    if(request.getParameter("objectBoui")!=null)
    {
        BOI = boObject.getBoManager().loadObject(boctx, Long.parseLong(request.getParameter("objectBoui")));
        long[] a_boui = {BOI.getBoui()};
        currObjectList = boObjectList.list(boctx, BOI.getName(),a_boui);
        if(!currObjectList.haveBoui(a_boui[0]))
            currObjectList.inserRow(a_boui[0]);
    }
    if( currObjectList != null ) 
        currObjectList.first();

    XwfController controller = (XwfController)DOC.getController();
    boObject activity = controller.getRuntimeActivity();
    String allTemplates = request.getParameter("allTemplates");
    String var = request.getParameter("variable");
    boObject variable = controller.getObject(Long.parseLong(var));
    boObject object = controller.getEngine().getBoManager().getValueBoObject(variable.getAttribute("value").getObject());             
    
    String msgLabel = null;
    String orderBy = null;
    StringBuffer boql = new StringBuffer();
    if(allTemplates != null && "false".equals(allTemplates))
    {
        String words = variable.getAttribute("keyWords").getValueString();
        words  = boObjectList.arrangeFulltext(boctx, words);               
//        boql.append("SELECT Ebo_Template WHERE");
        boql.append(" masterObjectClass.name='").append(object.getName()).append("'");

        String[] objs = object.getBoDefinition().canCastTo();
        if(objs != null)
        {
            for (int i = 0; i < objs.length; i++) 
            {
                boql.append(" or")
                     .append(" masterObjectClass.name='")
                     .append(objs[i])
                     .append("'");
            }
        }        
        
        boql.append(" and 1=[1 and contains(keyWords, '");
        boql.append(words);        
        boql.append("',1) > 0] ");// ORDER BY [SCORE(1)] DESC ");
        orderBy = "[SCORE(1)] DESC"; 
        msgLabel = "O sistema encontrou os seguintes Modelos para aplicar em ";
    }
    else
    {        
//        boql.append("SELECT Ebo_Template WHERE");        
        boql.append(" masterObjectClass.name='").append(object.getName()).append("' ");
        String[] objs = object.getBoDefinition().canCastTo();
        if(objs != null)
        {
            for (int i = 0; i < objs.length; i++) 
            {
                boql.append(" or")
                     .append(" masterObjectClass.name='")
                     .append(objs[i])
                     .append("'");
            }
        }      
        
        msgLabel = "O sistema não encontrou Modelos relacionados, para aplicar em ";
    }    
     
    StringBuffer templates = new StringBuffer();
    
//    String onclick = URLEncoder.encode(URLEncoder.encode("parent.parent.setActionCode('"+XwfKeys.ACTION_PROCESS_TEMPLATE_KEY+"[OBJECT_LIST_BOUI,"+var+"]');parent.parent.boForm.BindValues();"));    
//    String jspName = "ebo_template_generallist.jsp?";
//    templates.append(jspName);
//    templates.append("docid=").append(IDX);
//    templates.append("&method=").append("list");
//    templates.append("&menu=").append("no");    
//    templates.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");
//    templates.append("&boql=").append(QLParser.getURLboql(boql.toString()));
//    templates.append("&onclick=").append(onclick);
//    templates.append("&canSelectRows=no");
//    templates.append("&showSelectNone=yes");

    String onclick = URLEncoder.encode(URLEncoder.encode("parent.parent.parent.setActionCode('"+XwfKeys.ACTION_PROCESS_TEMPLATE_KEY+"[OBJECT_LIST_BOUI,"+var+"]');parent.parent.parent.boForm.BindValues();parent.parent.parent.wait();","UTF-8"),"UTF-8");
    String jspName = "__list.jsp?";
    templates.append(jspName);   
    templates.append("docid=").append(IDX);
    templates.append("&method=").append("list");
    templates.append("&menu=").append("no");    
    templates.append("&").append(ControllerFactory.CONTROLLER_NAME_KEY).append("=").append("XwfController");    
//    templates.append("&boql=").append(QLParser.getURLboql(boql.toString()));
    templates.append("&look_query=").append(QLParser.getURLboql(boql.toString()));
    if(orderBy != null)
    {
        templates.append("&orderBy=").append(orderBy);
    }
//    templates.append("&onclick=").append(onclick);
    templates.append("&userClick=").append(onclick);
    templates.append("&look_object=Ebo_Template");
    templates.append("&showNew=n");
    templates.append("&canSelectRows=no");
    templates.append("&showSelectNoneByForce=yes");
    


    Element up = new AdHocElement("<IFRAME src='"+templates.toString()+"' frameBorder=0 width='100%' scrolling=no height='100%'></IFRAME>");  
    
    boObject valueObject = variable.getAttribute("value").getObject().getAttribute(xwfHelper.VALUE_OBJECT).getObject();
//    Element down = new Preview(valueObject,"previewObjectToTemplate",null);        
//    Splitter splitter = Splitter.getSplitter(up,"50%",down,"50%");
//    String result = splitter.getHTML(DOC,DOCLIST,new PageController());
    
%>

<html>
<head>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title></title>
<script>
    var objLabel="&nbsp;&nbsp;Lista de Modelos";  
</script
<%= DOC.getController().getPresentation().writeCSS() %>
</head>   
<%@ include file='boheaders2.jsp'%>
<body ondragenter="activethis()" style='background-color:black' >

<form style='height:100%;' class="objectForm" name="boForm" id="<%=IDX%>">
    <DIV style='width:100%;height:100%;overflow:auto;background-color:#D4E5FB'>
        <TABLE class="layout" cellspacing="0" cellpadding="0" >
            <TR height="50px" bgcolor="White">
               <TD>   
                    <br>
                    <b>&nbsp;&nbsp;<%= msgLabel %></b> <%= valueObject.getCARDID() %><b>&nbsp; , aplique um dos seguintes Modelos:</b>
                    <hr>                         
               </TD>
            </TR>        
            <TR height="100%">
               <TD>
                 <div width="100%" height="33%"> 
                    <%= up.getHTML(DOC,DOCLIST,new PageController()) %>
                 </div>
               </TD>
            </TR>     
          </TABLE>            
    </DIV>
</form>
<FORM name='boFormSubmit' method='post'>
    <%
        java.util.Hashtable options = new java.util.Hashtable();    
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "method" ) != null){
            options.put(BasicPresentation.OPTION_INPUT_METHOD,request.getParameter( "method" ));
        }
        if(request.getParameter( "boui" ) != null){
            options.put(BasicPresentation.OPTION_REQUESTED_BOUI,request.getParameter( "boui" ));
        }
        options.put(BasicPresentation.OPTION_TYPE_FORM,"1");
        options.put(BasicPresentation.OPTION_JSP_NAME,this.getClass().getName());                 
    %>    
    <%= DOC.getController().getPresentation().writeJspFooter(BOI ,currObjectList,options,masterdoc,request) %>    
    
</FORM>
</body>
</html>
<%
} finally{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>
