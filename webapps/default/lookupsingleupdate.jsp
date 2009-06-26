<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>

<%@ page import="netgest.bo.controller.ControllerFactory"%>
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
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
       
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String clientIDXtoClose     = request.getParameter("clientIDXtoClose");

    
    String bouiToReplace     = request.getParameter("bouiToReplace");
    String options           = request.getParameter("options");   
    
    String userHostClient    = (String) DOC.getEboContext().getXeoWin32Client_adress();
    
    boolean forWorkFlow      = false;
    boolean forWorkFlowActivity   = false;
    if ( options != null )
    {
        if(options.equals("forWorkFlow") )
        {
            forWorkFlow = true;
        }
        else if(options.startsWith("forWorkFlowActivity") )
        {
            forWorkFlowActivity = true;
        }
    }
    
    StringBuffer toP=new StringBuffer();
    
    netgest.bo.runtime.boObject obj=DOC.getObject(Long.parseLong(look_parentBoui));
    AttributeHandler atr= obj.getAttribute( look_parentAttribute );
    
    boObject boWordTemplate = atr.getObject();
    String wordTemplate = null;
    if(boWordTemplate != null) wordTemplate = boWordTemplate.getName();
//    String newWordBoui = null;
    if(boWordTemplate != null && userHostClient!=null&& !"".equals(userHostClient) && !"-1".equals(userHostClient) && userHostClient.indexOf("@") != -1 && ("Ebo_WordTemplate".equals(wordTemplate) || "Ebo_DocumentTemplate".equals(wordTemplate)))
    {
        if("merge".equals(options))
        {
            //TemplateHelper.merge(obj,look_parentAttribute);
            //TemplateHelper.merge(bosession,userHostClient,obj,atr,look_parentBoui,look_parentAttribute);
        }
/*        try
        {
            InitialContext context = new InitialContext();
            boClientRemote remote = RegistryHelper.getClientRemote(context);
            boClientRemoteLocal remotelocal = ((boClientRemoteLocalHome)context.lookup("java:comp/env/ejb/boClientRemoteLocal")).create();
            String fileName = atr.getObject().getAttribute("fileName").getValueString();
            String associateObjBoui = boWordTemplate.getAttribute("object").getValueString();
            String bouiObj = String.valueOf(atr.getObject().getBoui()); 
            
            
            remote.getDocument(bosession.getId(),userHostClient,bouiObj);    
            remotelocal.getDataSource(obj.getEboContext(),userHostClient,look_parentBoui,String.valueOf(boWordTemplate.getBoui()),true);    

            WordEvents we = RegistryHelper.getOfficeWord(context,userHostClient);
            newWordBoui = we.merge(bosession.getId(),DOC.poolUniqueId(),fileName,bouiObj);          
            obj.getAttribute(look_parentAttribute).setValueString(newWordBoui);
            atr = obj.getAttribute( look_parentAttribute );            
                      
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }          */
    }    
    
    StringBuffer v= new StringBuffer( atr.getValueString() );
    
    if ( bouiToReplace!= null  ){
           
       bridgeHandler b=obj.getBridge( look_parentAttribute );
       b.moveTo(b.getRowCount());
       long toSet=b.getObject().getBoui();
       b.remove();
       b.beforeFirst();
       while ( b.next() )
       {
          if ( (b.getObject().getBoui()+"").equals( bouiToReplace ) )
          {
             String xin=b.getAttribute("inout").getValueString();
             b.setValue( toSet );
             b.getAttribute("inout").setValueString(xin);
             
             
             
             
          }
         
       }
       /*
       String vv= v.toString();
       String[] varr = vv.split(";");
       vv="";
       String bouiToSet=varr[ varr.length-1];
       for (int i = 0; i < varr.length-1 ; i++) 
       {
          if ( bouiToReplace.equals( varr[i] ))
          {
            vv+=bouiToSet+( i+1 <varr.length-1?";":""  );
          }
          else
          {
            vv+=varr[i]+( i+1 <varr.length-1?";":""  ) ;
          }
       }
    
       
       //vv=vv.replaceAll(bouiToReplace+";","");
       //vv=vv.replaceAll(bouiToReplace,"");
       
       atr.setValueString(vv);
       */
    }
    
    StringBuffer nameH = new StringBuffer();
    StringBuffer id = new StringBuffer();
    boDefAttribute atrDef= obj.getBoDefinition().getAttributeRef(look_parentAttribute );

    //AttributeHandler attr= obj.getAllAttributes().get( look_parentAttribute );
    nameH.append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    id.append("tblLook").append( obj.getName() ).append( "__" ).append( obj.bo_boui ).append("__").append( look_parentAttribute );
    
    int tabIndex = DOC.getTabindex(DOC.FIELD, obj.getName(), String.valueOf(obj.bo_boui), look_parentAttribute, DOCLIST);

    if ( forWorkFlow  )
    {
        boObject boDef = obj.getAttribute("object").getObject();  
        String dds= boDef.getAttribute("name").getValueString();
        
        netgest.bo.workflow.DocWfHTML.writeHTML_lookup(
            new StringBuffer( boDef.getAttribute("name").getValueString()),
            toP,
            obj,
            atr,
            v,
            nameH,
            id,
            tabIndex,
            DOC,
            atr.isDisabled(),
            atr.isVisible(),
            obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
            );
    }
    else if ( forWorkFlowActivity  )
    {
        String[] s = options.split("\\.");
        netgest.xwf.presentation.xwfActivityViewer.writeHTML_lookup(
            toP,
            atr,
            Long.parseLong(s[1]),
            Long.parseLong(s[2]),
            DOC
            );    
    }
    else if ( bouiToReplace== null  )
    {
        netgest.bo.dochtml.docHTML_renderFields.writeHTML_lookup(
            toP,
            obj,
            atr,
            v,
            nameH,
            id,
            tabIndex,
            DOC,
            atr.isDisabled(),
            atr.isVisible(),
            obj.getMode() == boObject.MODE_EDIT_TEMPLATE,false,false,null
            );
   }

%>

<html>
<head>
<script language="javascript">
function winmain(){var Win=window;while (!Win.openDoc){if( Win==Win.parent ) return Win;Win=Win.parent}return Win}
function updateFrame(wFrm)
{
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   toRet=false;
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      var xok=false;
      xw=wDocfrms[z].contentWindow.document.all;
      var toR=[];
      for(var j=0; j< xw.length; j++){
        if ( xw[j].id == "<%if(forWorkFlow || forWorkFlowActivity){%>ext<%}%>tblLook<%=nameH%>" ){
         toR[toR.length]=xw[j];
        }

      }
      for(var y=0; y < toR.length; y++){
            toR[y].outerHTML=toRender.innerHTML;
            <%
                if(obj.onChangeSubmit(atr.getName())){
%>
                   
                   //debugger;
                    var Win=wDocfrms[z].contentWindow;
                    //while (!Win.parent.openDoc){
                    //   Win=Win.parent;
                  //	}
                   Win.document.getElementById("refreshframe").contentWindow.BindToValidate();
     <% } %>
      }
        
         //toRet=
         updateFrame(wDocfrms[z]); 
     
   }
   //return toRet
           
}
 
  function updateLookupAttribute(){
   
   var windowToUpdate="<%=clientIDX%>";
  
   var xok=false;
  // var w=dialogArguments.winmain().ndl[windowToUpdate];
  var w=winmain().ndl[windowToUpdate];
   if(w){
       var ifrm=w.htm; //.getElementsByTagName('IFRAME');
       var xw;
       
          xw=ifrm.contentWindow.document.all;
          var toR=[];
          for(var z=0; z< xw.length; z++){
            if ( xw[z].id == "<%if(forWorkFlow || forWorkFlowActivity){%>ext<%}%>tblLook<%=nameH%>" ){
             toR[toR.length]=xw[z];
            }
          }
          
          for(var y=0; y < toR.length; y++){
                toR[y].outerHTML=toRender.innerHTML;
                xok=true;
              
<%
                if(obj.onChangeSubmit(atr.getName())){
%>
                   var Win=ifrm.contentWindow;
                   Win.document.getElementById("refreshframe").contentWindow.BindToValidate();
     <% } %>
        }
          
       
       if ( !xok ) updateFrame(ifrm);
   }
   
   try{
      //this.close();
      parent.ndl[<%=clientIDXtoClose%>].close();
   }catch(e){}
 }
</script>

<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
</head>
<body onload="updateLookupAttribute()">
<%
if( DOC.haveErrors() )
{
%>
<textarea id="errorText">
<%=DOC.getHTMLErrors()%>
</textarea>
<script>
    newDialogBox("critical",errorText.value,[ jsmessage_2 ],jsmessage_6);
</script>    
<%
}
%>
<xml id="toRender">
 <%=toP%>
 </xml>
</body>
<%
} finally {
   if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);
}
%>
