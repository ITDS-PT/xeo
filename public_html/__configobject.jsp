<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

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
    boDefAttribute atr;
    String idbolist;
    String[] ctrl;
    
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    DOC.poolSetStateFull(DOCLIST.poolUniqueId()); 
    
    // leitura de parametros
    // especificos desta JSP
    
    
    String DAOcode              = request.getParameter("DAOcode");
    String lineBridge           = request.getParameter("lineBridge");
    String ownerBoui            = request.getParameter("ownerBoui");
    String DAOBoui              = request.getParameter("DAOBoui");
    String parentAttribute      = request.getParameter("parentAttribute");
    
    String clientIDX            = request.getParameter("clientIDX");
    String parentIDX            = request.getParameter("parentIDX");
    
    
    boObject DAOObj             = DOC.getObject( ClassUtils.convertToLong( DAOBoui ) );
    boObject ownerObj           = DAOObj.getObject( ClassUtils.convertToLong( ownerBoui ) );
    
    
    
    //bridgeHandler bridgeRO      = DAOObj.getBridge("RO");
    bridgeHandler bridgeDAO     = DAOObj.getBridge("DAO");
    //bridgeHandler bridgeExtAtt  = DAOObj.getBridge("extendAttribute");
    
    //boObject configObj          =  ownerObj.getAttribute("") 
    bridgeDAO.beforeFirst();
    while ( bridgeDAO.next() )
    {
     bridgeDAO.getObject().poolSetStateFull(); 
    }
    
    bridgeDAO.moveTo( ClassUtils.convertToInt( lineBridge ) );
    
    BridgeObjAttributeHandler xobj=( BridgeObjAttributeHandler )bridgeDAO.getAttribute( "config" );
    boObject objConfig=xobj.edit().getObject();
    
    bridgeHandler extAttpar = objConfig.getBridge("extAtt_parameter");    
    if( !extAttpar.isEmpty() )
    {
        extAttpar.beforeFirst();
        while( extAttpar.next() )
        {
            extAttpar.edit();
        }
    }
   
    bridgeHandler depb = objConfig.getBridge("linedepends");
    if( !depb.isEmpty() )
    {
        depb.beforeFirst();
        while( depb.next() )
        {
            depb.edit();
        }
    }

    
    boObject dependObj          = bridgeDAO.getObject();
    StringBuffer nameH          = new StringBuffer();
    nameH.append( DAOObj.getName() ).append( "__" ).append( DAOObj.bo_boui ).append("__").append( "DAO" );
    docHTML_section sec;   
         
    
    
    
%>
<style type="text/css">
    @import url('templates/form/std/dependences.css');
</style>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>Configuracao de objecto</title>
<%@ include file='boheaders.jsp'%><script>
var objLabel='<img align="absmiddle" hspace=3  src="templates/form/std/configObj24.gif"><%="Configura袯 de links e depend魣ias"%> ';var objDescription='';</script>

<body>
<script language="javascript">

 
function updateFrame(wFrm)
{
  
   wDoc=wFrm.contentWindow;
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      if (  wDocfrms[z].id == "inc_<%=nameH%>" )
      {
            
            //wDocfrms[z].contentWindow.reloadGrid();
            wDocfrms[z].contentWindow.submitGrid();
            return;
      }
      else
      {
         updateFrame(wDocfrms[z]); 
      }
   }
           
}
 
 function updateLookupAttribute()
 {
   var windowToUpdate="<%=parentIDX%>";
   var w=parent.ndl[windowToUpdate];

   if(w)
   {
       var ifrm=w.htm.getElementsByTagName('IFRAME');
       var xw;
       for(var i=0; i < ifrm.length ; i++)
       {
           if ( ifrm[i].id == "frm$<%=parentIDX%>" )
           {
                updateFrame(ifrm[i]);
           }
       }
   }
   try
   {
       //parent.ndl[clientIDXtoClose].close();
   }
   catch(e){}
 }
</script>

<form class="objectForm" name="boForm" id="<%=IDX%>">

    <table style='background-color:#FFFFFF' class="layout" cellpadding=0 cellspacing=0 border="0" width="100%">
      <col width=20 />
      <col /> 
      <tr>
        <td  colspan=2 width="100%" style="padding:10px;background-color:#FFFFFF">
         <%=dependObj.getCARDID()%>
        </td>    
      </tr>
      <tr>
        <td  colspan=2 width="100%" style="padding:10px;background-color:#FFFFFF">
         <%
          
            String  url1 = "'__conditionFormula.jsp";
            url1 += "?daoconfig=" + objConfig.getBoui();
            url1 += "&protocolObject=" + DAOObj.getBoui();
            url1 += "&docid="+DOC.getDocIdx();
            url1+="'";
         %>
        
          <button onclick="winmain().openDoc('tall','','','','formula',<%=url1%>);">Condição para a actividade ser executada</button> 
        </td>    
      </tr>
      <tr>
        <td  colspan=2 width="100%" style="padding:10px;background-color:#FFFFFF">         
<%
        bridgeHandler bh = objConfig.getBridge("logicOperator");
        boObject boLovDef = DAOObj.getBoManager().loadObject(boctx,"SELECT Ebo_LOV WHERE NAME = 'logicOperator'");
        bridgeHandler bHandler = boLovDef.getBridge("details");                
        
        StringBuffer[] xInternal = new StringBuffer[ bHandler.getRowCount() ] ;
        StringBuffer[] xExternal = new StringBuffer[ bHandler.getRowCount() ] ;
        bHandler.beforeFirst();
        int size = bHandler.getRowCount();        
        for (byte i = 0; i < size; i++) 
        {
                bHandler.next();
                boObject objHandler = bHandler.getObject();
                xInternal[i] = new StringBuffer( objHandler.getAttribute("value").getValueString() );
                xExternal[i] = new StringBuffer( objHandler.getAttribute("description").getValueString() );
        }
        Hashtable xattributes2=new Hashtable(); 
        sec = DOC.createSection("logicOperator"+DAOBoui +objConfig.getBoui()+parentAttribute,"",false,0);        
        sec.setTitle("&nbsp;");    
        docHTML_sectionRow rowLp;     
        rowLp = sec.addRow();           
        docHTML_sectionCell cellLp = rowLp.addCellMultiField();      
        cellLp.addField(docHTML_sectionField.newCombo(
                new StringBuffer("1"),
                new StringBuffer(objConfig.getName() + "__" +objConfig.getBoui() + "logicOperator"),
                //new StringBuffer("logicOperator"+DAOBoui +objConfig.getBoui()+parentAttribute),
                new StringBuffer("Operador"),
                new StringBuffer( objConfig.getAttribute("logicOperator").getValueString() ),
                xExternal,
                xInternal,
                false,
                 new StringBuffer(""),null,null) 
                ,"",xattributes2
                ) ;          
        sec.render(pageContext,DOC,DOCLIST);
%>

        </td>    
      </tr>             

      <tr height=50%>
      <td valign=top class="headerNumber" ><span class="headerNumber">1</span></td>
       <td>
           <table cellpadding=0 cellspacing=0 height=100% width=100%>
                <tr height=23>
                    <td>
                
                      <table class="mnuBar" id="mnuBar2" cellSpacing="0" cellPadding="0">
                        <tbody>
                            <tr>
                              <td noWrap>
                                <span style="font-size:12px">Depend魣ias do Objecto</span>
                              </td>
                              
                              <td class="icMenu mnuRight" noWrap>
                              <%
                                  //String jsp=DAOObj.getName()+"_dao_generallist.jsp";
                                  
                                  String jsp="__lookupBridge.jsp";
                                  String url="?docid="+DOC.getDocIdx();
                                  url+="&look_parentAttribute=DAO";
                                  url+="&clientIDX='+getIDX()+'";
                                  url+="&listmode=searchone";
                                  url+="&canSelectRows=no";
                                  url+="&menu=no";
                                  url+="&showLines=no";
                                  url+="&bolist_query="+java.net.URLEncoder.encode("code != \""+DAOcode+"\"", "UTF-8");
                                  url+="&look_object=boObject";
                                  url+="&renderOnlyCardID=yes";
                                  url+="&look_parentObj="+DAOObj.getName();
                                  url+="&look_parentBoui="+DAOObj.getBoui();
                                  url+="&addTo_parentObj="+objConfig.getName();
                                  url+="&addTo_parentBoui="+objConfig.getBoui();
                                  url+="&addTo_parentAttribute=linedepends";

                                                                   
                                  String onclick="winmain().openDocUrl('small','"+jsp+"','"+url+"','std','"+DOC.getDocIdx()+"',getIDX());";
                              %>                                   
                                    
                                    <span class="menuFlat" onclick="<%=onclick%>" title="" style="font-weight:bolder" tabIndex="0">Adicionar</span>
                                    <span class="menuFlat" onclick="inc_Ebo_DAOConfig__<%=objConfig.getBoui()%>__linedepends.deleteSelected(1234,'linedepends',['editAttributes','yes','showIcon','no','showStatus','no','showPreview','no','showLines','no','menu','no','method','list','voui',1234] )" title="Apaga as linhas seleccionadas" style="font-weight:bolder" tabIndex="0">Apagar Selecção</span>
                              </td>
                            </tr>
                         </tbody>
                       </table>
                    </td>
                </tr>
                <tr><td>
                 
                <%
     
                String __url = "ebo_daodepends";
                
                __url+="_general"+"list.jsp";
//                String boql;
  //              boql="select Ebo_DAOdepends where 0=1";
                
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
                %>
                 
                <IFRAME id=inc_Ebo_DAOConfig__<%=objConfig.getBoui()%>__linedepends src='<%=__url+"?docid="+IDX+"&editAttributes=yes&showIcon=no&showStatus=no&showPreview=no&showLines=no&menu=no&editAttributes=yes&method=list&voui=1234&parent_attribute=linedepends&parent_boui="+objConfig.getBoui()%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                 
                 
                </td>
                </tr>
             </table>
            
        </td>
        
      </tr>
      
      
      <tr height=50%>
       <td valign=top class="headerNumber" ><span class="headerNumber">2</span></td>
       <td>
           <table cellpadding=0 cellspacing=0 height=100% width=100%>
                <tr height=23>
                    <td>
                
                      <table class="mnuBar" id="mnuBar2" cellSpacing="0" cellPadding="0">
                        <tbody>
                            <tr>
                              <td noWrap>
                                <span style="font-size:12px">Informação que o objecto vai receber</span>
                              </td>
                              <td class="icMenu mnuRight" noWrap>
                                   <%
                                  //String jsp=DAOObj.getName()+"_dao_generallist.jsp";
                                  
                                 /* jsp="__lookupBridge.jsp";
                                  url="?docid="+DOC.getDocIdx();
                                  url+="&look_parentAttribute=RO";
                                  url+="&clientIDX='+getIDX()+'";
                                  url+="&listmode=searchone";
                                  url+="&canSelectRows=no";
                                  url+="&menu=no";
                                  //url+="&bolist_query="+java.net.URLEncoder.encode("code != \""+DAOcode+"\"");
                                  url+="&look_object=boObject";
                                  url+="&renderOnlyCardID=yes";
                                  url+="&look_parentObj="+DAOObj.getName();
                                  url+="&look_parentBoui="+DAOObj.getBoui();
                                  url+="&addTo_parentObj="+objConfig.getName();
                                  url+="&addTo_parentBoui="+objConfig.getBoui();
                                  url+="&addTo_parentAttribute=RO_parameter";*/
                                  
                                  jsp="__lookupBridge.jsp";
                                  url="?docid="+DOC.getDocIdx();
                                  url+="&look_parentAttribute=extendAttribute";
                                  url+="&clientIDX='+getIDX()+'";
                                  url+="&listmode=searchone";
                                  url+="&canSelectRows=no";
                                  url+="&menu=no";
                                  //url+="&bolist_query="+java.net.URLEncoder.encode("code != \""+DAOcode+"\"");
                                  url+="&look_object=boObject";
                                  url+="&renderOnlyCardID=yes";
                                  url+="&look_parentObj="+DAOObj.getName();
                                  url+="&look_parentBoui="+DAOObj.getBoui();
                                  url+="&addTo_parentObj="+objConfig.getName();
                                  url+="&addTo_parentBoui="+objConfig.getBoui();
                                  url+="&addTo_parentAttribute=extAtt_parameter";                                 
                                

                                                                   
                                  onclick="winmain().openDocUrl('small','"+jsp+"','"+url+"','std','"+DOC.getDocIdx()+"',getIDX());";
                              %>
                                    <span class="menuFlat" onclick="<%=onclick%>" title="" style="font-weight:bolder" tabIndex="0">Adicionar</span>
                                    <span class="menuFlat" onclick="inc_Ebo_DAOConfig__<%=objConfig.getBoui()%>__extAtt_parameter.deleteSelected(1235,'extAtt_parameter',['editAttributes','yes','showIcon','no','showStatus','no','showPreview','no','showLines','no','menu','no','method','list','voui',1235])" title="Apaga as linhas seleccionadas" style="font-weight:bolder" tabIndex="0">Apagar Selec袯</span>
                              </td>
                            </tr>
                         </tbody>
                       </table>
                    </td>
                </tr>
                <tr><td style="border-bottom:1px solid #476899;">
                  <%
     
                __url = "ebo_daoparameter";
                
                __url+="_general"+"list.jsp";
                __url=__url.toLowerCase();
                
                %>
                     <IFRAME id=inc_Ebo_DAOConfig__<%=objConfig.getBoui()%>__extAtt_parameter src='<%=__url+"?docid="+IDX+"&editAttributes=yes&showIcon=no&showLines=no&showStatus=no&showPreview=no&menu=no&editAttributes=yes&method=list&voui=1235&parent_attribute=extAtt_parameter&parent_boui="+objConfig.getBoui()%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                </td>
                </tr>
             </table>
            
        </td>
    
      </tr>
      <tr height=30px>
          <td colspan=2 style="padding:5px">
        <%
          sec=DOC.getSection("confirmConfig"+DAOBoui+objConfig.getBoui()+parentAttribute);
          if(sec==null) {
             
              sec = DOC.createSection("confirmConfig"+DAOBoui +objConfig.getBoui()+parentAttribute,"",false,0);
              
    
              sec.setTitle("");
              docHTML_sectionRow row=sec.addRow();
              Hashtable xattributes=new Hashtable();
              xattributes.put("attr_td"," align='center' ");
              docHTML_sectionCell cell = row.addCellMultiField();
              
              cell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("confirm"),
                            new StringBuffer("Confirma configuração"),
                            new StringBuffer("inc_Ebo_DAOConfig__"+objConfig.getBoui()+"__linedepends.boForm.BindExt();inc_Ebo_DAOConfig__"+objConfig.getBoui()+"__extAtt_parameter.boForm.BindExt();updateLookupAttribute();")
                            ),"",xattributes
                            ) ;
              cell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("cancel"),
                            new StringBuffer("Cancelar"),
                            new StringBuffer("winmain().ndl[getIDX()].close();"))
                            ,"",xattributes
                            ) ;

           }    
           sec.render(pageContext,DOC,DOCLIST);
         
        %>
            
       </td>
            
      </tr>
    </table>

</form>


<FORM name='boFormSubmit' method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='look_parentObj' value='<%=objConfig.getName()%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=objConfig.getBoui()%>' />

    <INPUT type='hidden' name='look_parentAttribute' value='<%="linedepends"%>' />

    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='parentIDX' value='<%=parentIDX%>' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    
    <INPUT type='hidden' name='DAOcode' value='<%=DAOcode%>' />
    <INPUT type='hidden' name='lineBridge' value='<%=lineBridge%>' />
    <INPUT type='hidden' name='ownerBoui' value='<%=ownerBoui%>' />
    <INPUT type='hidden' name='DAOBoui' value='<%=DAOBoui%>' />
  
</FORM>


</body>
</html>
<%
} finally {{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}}
%>
