<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="java.io.ByteArrayOutputStream"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.data.*"%>
<%@ page import="netgest.xwf.core.*"%>
<%@ page import="netgest.xwf.common.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.utils.*"%>
<%@ page import="oracle.xml.parser.v2.*"%>



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
    response.sendRedirect("login.jsp?returnToPage=xwfECMAparser.jsp");
    return;
}
if(boctx==null) {
    boctx = bosession.createRequestContext(request,response,pageContext);
    request.setAttribute("a_EboContext",boctx);
}

int IDX;int cvui;
String[] ctrl;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
docHTML DOC = DOCLIST.getDOC(IDX);
EboContext ctx = DOC.getEboContext();
int xxxx = 1;





%>

  
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    
    <% int sel = -1;
   
       Vector queries=new Vector();
       netgest.xwf.core.xwfECMAevaluator x=new xwfECMAevaluator();
       String result=null;
    %>
       
  </head>
<body>
<%

    boolean sucess = true;
    String s = request.getParameter("C");
    String ss = request.getParameter("E");
    String st = request.getParameter("T");
    xwfManager xwm = new xwfManager(DOC, null);
    
    
    
    if(s!=null && request.getParameter("xep")!=null)
    { 
      netgest.xwf.core.xwfECMAparser xp=new xwfECMAparser();
      result = xp.toJava(request.getParameter("xep"));
      sucess = xp.success();
   //   xwfECMAtokenizer tok = new xwfECMAtokenizer(request.getParameter("xep"));tok.htmlToText(request.getParameter("xep"));
//      netgest.xwf.utfcode uc = new netgest.xwf.utfcode();
//      uc.mainP(request.getParameter("xep"), "-8");    
      
    }
    else
    if(ss!=null && request.getParameter("xep")!=null)
    { 

      Object xo= x.eval(xwm.getBoManager(), request.getParameter("xep"));
      if(xo != null)
        result = xo.toString();
    }
    else
    if(st!=null && request.getParameter("xep")!=null)
    { 
      String reqP = request.getParameter("xep");

      if(!reqP.equals(""))
      {
        ngtXMLHandler xhand = new ngtXMLHandler(reqP);
        boObject bo = objectSerialization.xmlToBoObject(xhand.getDocument(), xwm.getBoManager().getContext());
        result = ""+bo.getBoui();
      }
      else
      {
        boObject bo = xwm.getBoManager().createObject("email");
        XMLDocument xdoc = objectSerialization.boObjectToXML(bo); 
      /*  bo.destroy();
        bo = objectSerialization.xmlToBoObject(xdoc, xwm.getBoManager().getContext());*/
        ByteArrayOutputStream barrOS = new ByteArrayOutputStream();
        xdoc.print(barrOS);
        result = barrOS.toString();
      }
      //     boObject p1 = DOC.getObject(2220226);
//     
//     xwm.initProgram(2220226, new Hashtable(), 0);
//     xwfControlFlow cwf = new xwfControlFlow(DOC, p1.getBoui());
//     result = cwf.ControlFlow();
    }



%>

    
    
    </form>
      <table>
      <td>
        <form id='myform' action="xwfECMAparser.jsp" method="Post" >
           <textarea name="xep" cols="50" rows="25" ><%if(request.getParameter("xep")!=null)
                                                    {
                                                      out.print(request.getParameter("xep"));
                                                    }%></textarea>
           <input type="submit" name="C" value="Compile" />
           <input type="submit" name="E" value="Eval" />
           <input type="submit" name="T" value="Try" />
           <button onclick="myform.docid.value='-1'"> Reset</button>
           <INPUT type='hidden' value='<%=IDX%>' name='docid' />
        </form>
      </td>
      <td>
        <textarea name="res" cols="70" rows="25">
        <% if(result!=null && sucess)
          out.print(result);%>
        </textarea>
      </td>
      </table>
      <% if(result!=null && !sucess)
          out.print(result);%>
  </body>
</html>
<% 
} finally {
  if (boctx!=null)boctx.close();
  if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}%>