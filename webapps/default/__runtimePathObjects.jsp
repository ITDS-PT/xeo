<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="java.io.*"%>
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
if(bosession== null)
{

    StringBuffer url = new StringBuffer("?");
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String pname = oEnum.nextElement().toString();
        url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ), "UTF-8" ) );
    }
    response.sendRedirect("login.jsp?returnToPage=__runtimePathObjects.jsp"+ java.net.URLEncoder.encode(url.toString(), "UTF-8" ));
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

Hashtable xattributes;
ctrl= DOCLIST.processRequest(boctx);
IDX= ClassUtils.convertToInt(ctrl[0],-1);
idbolist=ctrl[1];
docHTML DOC = DOCLIST.getDOC(IDX);
boObjectList currObjectList = DOC.getBoObjectListByKey(idbolist);
boObject BOI;
if ( currObjectList == null ) BOI=null;
else BOI=currObjectList.getObject();
if( currObjectList != null ) currObjectList.first();


%>
<%!


public StringBuffer getStringsObject( boObject o,bridgeHandler bridge, boolean renderMore, String Icon ) throws boRuntimeException
{
    StringBuffer toRet=new StringBuffer();
    String imgmore=Icon;
    if ( imgmore==null ) imgmore="down";
    
    toRet.append("<table cellpadding='0' cellspacing='0' style='font:9px;table-layout:fixed'><colgroup/>");
    toRet.append("<col width=21 /><col width=21><col width=21><col/><col width=2/><col width=50/><col width=16px/>");
    
    toRet.append("<tr><td style='padding:2px'>");
    if ( bridge!=null )
    {
        toRet.append("<img src='resources/numbers/");
        toRet.append( o.getParentBridgeRow().getAttribute("LIN").getValueString()) ;
        toRet.append(".gif' />");
    }
    toRet.append("</td><td>" );
    
    
    toRet.append( o.getICONComposedState() );
    toRet.append("</td><td>" );
    toRet.append("<img style='cursor:hand' hspace='3' align='absmiddle' class='lui' title='");
    toRet.append("Imagem representativa do objecto " );
    toRet.append( o.getBoDefinition().getLabel() );
    toRet.append("' src='resources/"+o.getName()+"/ico16.gif");
    toRet.append("' width='16' height='16'/>");
    toRet.append("</td><td style='padding:2px'>" );    
 
    toRet.append("<span class='lui' onclick=\"");
    toRet.append("winmain().openDoc('medium','");
    toRet.append( o.getName().toLowerCase() );
    toRet.append("','edit','method=edit&boui=");
    toRet.append( o.bo_boui);
    toRet.append("','");
    toRet.append("");
    toRet.append("','");
    toRet.append("");
    toRet.append("','");
    toRet.append( o.getName() );
    toRet.append("',window.windowIDX)\">");
        
    toRet.append("<span ");
    StringBuffer xC= new StringBuffer();
    xC.append( o.mergeAttributes(o.getBoDefinition().getCARDID(),o));
    toRet.append("title='");
    toRet.append(xC);
    toRet.append("'>");
    toRet.append(xC);
    toRet.append("</span></span>");
    
 
   
    toRet.append("</td><td >" );
    toRet.append("</td><td>" );
    if ( bridge!=null)
    {
        AttributeHandler config=bridge.getAttribute("config");
        if ( config != null && config.getObject()!=null )
        {   
            toRet.append( "<div class='lu ro lui' >");
            
            toRet.append( config.getObject().getTextCARDID());
            toRet.append("</div>");
        }
    }
    toRet.append("</td><td>" );
    
    if ( renderMore && o.getBridge("DAO").getRowCount()>0 )
    {
         toRet.append("<img src='resources/"+imgmore+".gif' ");
         toRet.append("boui='");
         toRet.append( o.getBoui());
         toRet.append("' onclick='buildIframe()' />");
    }
     
    toRet.append("</td></tr>");
    toRet.append("<tr><td> </td> <td> </td><td colspan=3>");
     AttributeHandler assign=o.getAttribute("assignedQueue");
    if ( assign != null )
    {   
      //  toRet.append( "<div class='lu ro lui' >");
        toRet.append("<span style='font:tahoma 6px;padding-left:20px' >");
        toRet.append( assign.getObject().getCARDID());
        toRet.append("</span>");
    }
    
    toRet.append("</td></tr>");
    toRet.append("</table>" );
    return toRet;
 
}

%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Query Builder</title>
<%@ include file='boheaders.jsp'%>

<style>
 td.parentObject{
   
   F-ILTER: progid:DXImageTransform.Microsoft.Gradient(GradientType=0, StartColorStr=#E6AEAA, EndColorStr=#F8DCDC);
   b-order:1px solid #F09C38;
   background-color:#CCCCCC;
   padding:3px;
   border-bottom:1px solid #AAAAAA;
 }
 table.parentBridge
 {
    b-order:1px solid #CCCCCC;
    b-ackground-color:#EEEEEE;
    width:320px;
    border-left:1px solid #6A87B1 ;
    border-right:1px solid #6A87B1 ;
    font:9px;
 }
 table.top
 {
   border-top:1px solid #6A87B1 ;
 }

 table.bottom
 {
   border-bottom:1px solid #6A87B1 ;
 }
 
 table.left
 {
   border-left:1px solid #6A87B1 ;
 }
 table.right
 {
   border-right:1px solid #6A87B1 ;
 }
 
 table.childBridge
 {
    b-order:1px solid #CCCCCC;
    ba-ckground-color:#D9E5E8;
    b-order-left:1px solid #6A87B1 ;
    border-right:1px solid #6A87B1 ;
    font:9px;
    width:320px;
 }
 table.objectCentral
 {
   b-order:1px solid #4CD02E;
   background-color:#EEEEEE;
   height:25px;
   
 }
 table{
 w-idth:100%;
 }
 
</style>
<script language="javascript">
  function buildIframe()
  {
    var e=window.event.srcElement;
    if( e.boui )
    {
   // ifrm=document.createElement("iframe");
// ifrm.width='100%';
  //  ifrm.height='100%';
    window.location.href="__runtimePathObjects.jsp?boui="+e.boui+"&method=edit&docid=<%=IDX%>";
   // e.appendChild(ifrm);
    }
  }
</script>
</head>
<script>
<%
String ic="<img align='absmiddle' src='resources/viewpath24.gif' /><span>&nbsp;Interdepend魣ias da "+BOI.getBoDefinition().getLabel()+"&nbsp;</span>";
%>
var objLabel="<%=ic+BOI.getTextCARDID()%>";var objStatus="";</script>
<%if ( request.getParameter("print")==null ){ %>
<body onload="central.focus()" >
      <%} else
      {%>
<body onload="window.print()" >      
    <%}%>
   <table cellpadding="0" cellspacing="0" style="width:100%;height:100%;table-layout:fixed;">
    <tr><td height="23px">
      <%if ( request.getParameter("print")==null ){ %>
        <table class="mnubar" id="mnuBar2" cellSpacing="0" cellPadding="0">
        <tbody>
        <tr>
          <td width="9"><img hspace="3" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18"></td>
          <td class="icMenu" noWrap>
            <span class="menuFlat" title="Imprimir" onclick="window.open(window.location.href+'&print=y');" ><img class="mnuBtn" src="templates/menu/std/16_print.gif" WIDTH="16" HEIGHT="16">Imprimir</span>
    		<img src="templates/menu/std/mnu_hSpacer.gif" WIDTH="2" HEIGHT="17">
			
		  </td>
		</tr></tbody></table>
      <%}%>
        </td>
     </tr>  
     
     <tr><td style="width:100%;">
     <div style="padding:10px;height:100%;width:100%;overflow:auto;">    

     
     <table cellpadding="0" cellspacing="0" >
        
         <tr>
            <td align="left" valign="top">
                <table cellpadding="0"  xx="1" cellspacing="0" ><tr>
            <%//pais primeira parte até apanharem o BOI 
            boObject[] parents = BOI.getParents();
            for (int i = 0;parents!=null && i < parents.length ; i++) 
            {
            %><td align="left" valign="bottom">
            
                <table cellpadding="0" cellspacing="0" class="parentBridge top" >
                    <tr>
                        <td class="parentObject" boui="<%=parents[i].getBoui()%>"  >
                        <%=getStringsObject(parents[i],null,true,"left" ) %>
                        </td>
                    </tr>
                    <%
                    bridgeHandler bridge=parents[i].getBridge("DAO");
                    bridge.beforeFirst();
                    boolean found=false;
                    while ( bridge.next() && !found )
                    {
                        boObject o=bridge.getObject();
                        if ( o.getBoui() != BOI.getBoui() )
                        {
                    %>
                    <tr>
                        <td class="gCell_std" boui="<%=o.getBoui()%>" >
                            <%=getStringsObject(o,bridge,true,"")%>
                        </td>
                    </tr>
                    <%  }
                        else
                        {
                            found=true;
                        }
                    }
                    %>
                 </table>
                 
              </td>
           <%
            }
            if ( parents==null || parents.length==0)
            { //nao tem pais
            %>
            <td align="left" valign="bottom">
            
                <table cellpadding="0" cellspacing="0" class="parentBridge top" >
                    <tr>
                        <td class="parentObject"  >
                            Objecto sem pai
                        </td>
                    </tr>
                 </table>
                 
              </td>
            
            <%}%>
            </tr></table> <!--fim dos pais - parte 1 -->
           
            </td><td>&nbsp;</td>
         </tr>
         <tr>
            
            <td align="left" valign="middle" id="central"  colspan="2">
            
            <table cellpadding="0" xx="1" class="objectCentral" cellspacing="0"><tr>
            
            <%// parte central  
            parents = BOI.getParents();
            boolean haveParents= (parents!=null && parents.length>0);
            if ( haveParents )
            {
            for (int i = 0;parents!=null && i < parents.length ; i++) 
            {
            %>
                <td>
                <table class="<%=i==0?"left":""%> <%=i==parents.length-1?"right":""%>" clabss="parentBridge" style="width:320px" >
                    
                    <%
                    bridgeHandler bridge=parents[i].getBridge("DAO");
                    bridge.beforeFirst();
                    
                  
                    while ( bridge.next() )
                    {
                        boObject o=bridge.getObject();
                        if ( o.getBoui() == BOI.getBoui() )
                        {
                    %>
                    <tr>
                        <td class="" boui="<%=o.getBoui()%>"  >
                            <%=getStringsObject(o,bridge,false,null)%>
                        </td>
                    </tr>
                    
                    <%  break;
                        }
                      
                    }
                                        
                    %>

                 </table>
                 </td>
            <%}
            } else
            {
            %>  
                <td>
                <table cellpadding="0"  cellspacing="0"  class="parentBridge top" >
                    
                                    
                    <tr>
                        <td boui="<%=BOI.getBoui()%>"  >
                            <%=getStringsObject(BOI,null,false,null)%>
                        </td>
                    </tr>
                    
                  
                 </table>
                 </td>
            
            <%}%>
            </tr></table>
            
             </td>
         </tr>
         
         <% 
                bridgeHandler bridge=BOI.getBridge("DAO");
                bridge.beforeFirst();
                bridge.next();
                if ( !bridge.isEmpty() )
                {
         %>
         <tr>
            
            <td style="border-left:1px solid #6A87B1 ;background-color:#EEEEEE">&nbsp;</td>
            <td>
               
                    <!--bridge do filho-->
                   <table  class="childBridge top bottom <%=(BOI.getParents()==null || BOI.getParents().length==0)?"":"" %>"  >
                    <%
                    
                    bridge.beforeFirst();
                    
                    while ( bridge.next() )
                    {
                        boObject o=bridge.getObject();
                    
                    %>
                    <tr>
                        <td class="gCell_std" boui="<%=o.getBoui()%>"  >
                            <%=getStringsObject(o,bridge,true,null)%>
                        </td>
                    </tr>
                    <% 
                    }
                    %>
                 </table>
               
                 
            </td>
         </tr>
            <%}%>      
         
         <tr>
            <td align="left" valign="top">
                      
                <table cellpadding="0" cellspacing="0" ><tr>
            <%//pais 2* parte dp de apanharem o BOI 
            parents = BOI.getParents();
            
            for (int i = 0;parents!=null && i < parents.length ; i++) 
            {
            %><td align="left" valign="top">
            
                <table class="parentBridge bottom" >
                    
                    <%
                    bridge=parents[i].getBridge("DAO");
                    bridge.beforeFirst();
                    
                    boolean alreadyFound=false;
                    boolean haveRecords=false;
                    while ( bridge.next()  )
                    {
                        boObject o=bridge.getObject();
                        if ( o.getBoui() != BOI.getBoui() )
                        {
                            if ( alreadyFound )
                            {
                                haveRecords=true;
                    %>
                    <tr>
                        <td class="gCell_std" boui="<%=o.getBoui()%>"  >
                            <%=getStringsObject(o,bridge,true,null)%>
                        </td>
                    </tr>
                    
                    <%      }
                        }
                        else
                        {
                            alreadyFound=true;
                            
                        }
                    }
                    if ( !haveRecords )
                    {
                    
                    %>
                    <tr><td>&nbsp;</td></tr>
                    <%
                    }
                    %>
                 </table>
                 
              </td>
           <%
            }
            if ( parents==null || parents.length==0)
            {%>
            <td align="left" valign="top">
            
                <table class="parentBridge bottom" >
                    
                 
                    <tr>
                        <td class="gCell_std"  >
                         
                        </td>
                    </tr>
                 </table>
                 
              </td>
            <%}
           %></tr></table> <!--fim dos pais - parte 2 -->
            
            
        
            </td><td>
              &nbsp;
              </td>
         </tr>
     </table>

        </div></td></tr></table>
</body>
</html>
<%
} finally {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}%>
