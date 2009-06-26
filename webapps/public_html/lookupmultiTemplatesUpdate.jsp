<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
EboContext boctx = (EboContext)request.getAttribute("a_EboContext");
boolean initPage=true;
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
       
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_templateBoui    = request.getParameter("look_templateBoui");
    String look_relatedBoui     = request.getParameter("look_relatedBoui");
    String look_parentClass     = request.getParameter("look_parentClass");
    String look_attributeName   = request.getParameter("look_attributeName");
    String clientIDX            = request.getParameter("clientIDX");
    String submitXML            = request.getParameter("boFormSubmitTemplateXml");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");
    
    // GRAVAR
    ngtXMLHandler xx=new ngtXMLHandler(submitXML);
    String xv=xx.getFirstChild().getText();
    
    
    
        
    long tmpl_boui= ClassUtils.convertToLong( look_templateBoui );
    boObject tmpl=DOC.getObject( tmpl_boui );
                    bridgeHandler maps=tmpl.getBridge("mappingAttributes");
                    boolean find=false;
                    maps.beforeFirst();
                    boObject map;
                    while ( maps.next() ){
                         map=maps.getObject();
                         if (map.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(look_attributeName )) {
                            find=true;
                            map.getAttribute("value").setValueString(xv);
                            map.update();    
                            break;
                         }
                    }
                    if (! find ){
                        map=boObject.getBoManager().createObject(DOC.getEboContext(),"Ebo_Map");
                        map.getAttribute("value").setValueString(xv);
                        map.getAttribute("objectAttributeName").setValueString(look_attributeName);
                        map.update();
                        maps.add( map.bo_boui );
                        
                    }
                  
                  if(!tmpl.poolIsStateFull()) {
                      tmpl.poolSetStateFull(DOC.poolUniqueId());
//                      DOC.p_changedobjects.put(""+tmpl.bo_boui,tmpl.getName());
                  }
    
    netgest.bo.runtime.boObject obj=DOC.getObject(Long.parseLong(look_relatedBoui));
 //   writeHTML_lookup(StringBuffer toPrint,boObject objParent,boDefAttribute atrParent,StringBuffer Value,StringBuffer Name,int tabIndex,docHTML doc)
    StringBuffer toP=new StringBuffer();
    
    netgest.bo.runtime.AttributeHandler atr=obj.getAttribute(look_attributeName);
    
//    StringBuffer v= new StringBuffer( atr.getValueString() );
    StringBuffer nameH = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_attributeName );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_attributeName );

    //netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(toP,obj,atrDef,v,nameH,1,DOC);
   
%>

<html>
<script language="javascript">

 function updateLookupAttribute(){

   var windowToUpdate="<%=clientIDX%>";

   
   var w=parent.ndl[windowToUpdate];
  // debugger;
   if(w){
       var ifrm=w.htm.getElementsByTagName('IFRAME');
       var xw;
       for(var i=0; i < ifrm.length ; i++){
            
            //buscar window
            if ( ifrm[i].id == "frm$<%=clientIDX%>" ){
            
                var wDoc= ifrm[i].contentWindow;
                var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
                for(var z=0;  z < wDocfrms.length ; z++){
                    if (  wDocfrms[z].id == "inc_<%=nameH%>" ){
                         //wDocfrms[z].contentWindow.location.reload();
                         //var xhref=wDocfrms[z].contentWindow.location.href;
                         //wDocfrms[z].contentWindow.location.href=setUrlAttribute(xhref,'boFormSubmitXml','');
                         wDocfrms[z].contentWindow.submitGrid();
                    }
                }
            }
            
            
            
       }
   }
   try{
       parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
</script>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<body onload="updateLookupAttribute()">
<div id="toRender">
 
 </div>
</body>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
