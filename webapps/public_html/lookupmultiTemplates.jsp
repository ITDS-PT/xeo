<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
boolean initPage=true;
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

    int IDX;int cvui;boDefHandler bodef;boDefAttribute atr;String idbolist;String[] ctrl;
    
//    docParameter attr;

    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_templateBoui    = request.getParameter("look_templateBoui");
    String look_relatedBoui     = request.getParameter("look_relatedBoui");
    String look_parentClass     = request.getParameter("look_parentClass");
    String look_attributeName   = request.getParameter("look_attributeName");
    String clientIDX            = request.getParameter("clientIDX");
    
    docHTML_section sec;   
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%><script>

var objLabel='Procura Templates de <%=netgest.bo.def.boDefHandler.getBoDefinition(look_object).getLabel()%>';
var objDescription='<%=netgest.bo.def.boDefHandler.getBoDefinition(look_object).getDescription()%>'

</script>
<body onload="document.getElementById('TEXTSEARCH').focus()">

<form class="objectForm" name="boForm" id="<%=IDX%>">

   <table style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
          <%
          sec=DOC.getSection("lookupTemplate"+look_object+look_attributeName);
          if(sec==null) {
         
              sec = DOC.createSection("lookupTemplate"+look_object+look_attributeName,"",false,5);
              bodef=boDefHandler.getBoDefinition(look_object);
              sec.setTitle("Procura de Templates de "+bodef.getLabel() );
              docHTML_sectionRow row; 
              row=sec.addRow();
  
              Hashtable xattributes=new Hashtable();

              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("Texto a Procurar"),
                            new StringBuffer(),null,null,null),"100%",xattributes) ;
            
              
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                             new StringBuffer("findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            ) ;
                         


            }    


         sec.render(pageContext,DOC,DOCLIST);

%>


          </td>
     </tr>
     <tr> 
          <td height="60%" width="100%" valign="top" >
             <%
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                
                       
                String __url = "Ebo_Template";
                String boql0="select Ebo_Template where 0=1";
                __url+="_general"+"list.jsp";
                
                String boql="select Ebo_Template where masterObjectClass.name='"+look_object+"'";
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>
              <script language="javascript">                
               var SURL="<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchmulti"%>"; 
                </script>
                  
                 
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&method=list&boql="+boql0+"&listmode=searchmulti"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>
      <tr><td>
          <%
          sec=DOC.getSection("confirmlookup"+look_object+look_attributeName);
          if(sec==null) {
             
              sec = DOC.createSection("confirmlookup"+look_object+look_attributeName,"",false,0);
              bodef=boDefHandler.getBoDefinition(look_object);
    
              sec.setTitle("");
              docHTML_sectionRow row; 
              row=sec.addRow();
              Hashtable xattributes=new Hashtable();
              row.addCell( docHTML_sectionField.newButton(
                            new StringBuffer("confirm"),
                            new StringBuffer("Confirmar a lista"),
                            new StringBuffer("resultframe.submitBridge()")
                            ),null
                            ) ;

            }    


         sec.render(pageContext,DOC,DOCLIST);

    %></td></tr>
      <tr> 
          <td height="40%" width="100%" valign="top" >
                <%
                //netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                
        
                
                
                //String 
                
                __url = "Ebo_Template";
                __url+="_general"+"list.jsp";
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
                    long tmpl_boui= ClassUtils.convertToLong( look_templateBoui );
                    boObject tmpl=DOC.getObject( tmpl_boui );
                    bridgeHandler maps=tmpl.getBridge("mappingAttributes");
                    boolean find=false;
                    maps.beforeFirst();
                    boObject map;
                    String listTemplatesBouis="";
                    while ( maps.next() ){
                         map=maps.getObject();
                         if (map.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(look_attributeName )) {
                            find=true;
                            listTemplatesBouis=map.getAttribute("value").getValueString();
                            break;
                         }
                    }
                    
                    
                    String[] listbouis=listTemplatesBouis.split(";");
                    StringBuffer tboql=new StringBuffer("select Ebo_Template where ");
                    if( listbouis.length == 1 && listbouis[0].equals("") ) tboql.append("0=1");
                    else {
                        for (int i = 0; i < listbouis.length ; i++)  {
                            tboql.append(" boui = " );
                            tboql.append(listbouis[i]);
                            if ( i+1 < listbouis.length ) {
                                tboql.append(" and ");
                            }
                        }
                    }
                    
           %>
                 
                <IFRAME id=resultframe src='<%=__url+"?docid="+IDX+"&listmode=resultBridge&method=list&boql="+java.net.URLEncoder.encode(tboql.toString())%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>
     
    
   </table>

</form>
    
<FORM name='boFormSubmit' action="lookupmultiTemplatesUpdate.jsp" method='get'>
    <INPUT type='hidden' name='boFormSubmitTemplateXml' />
    <INPUT type='hidden' name='look_object' value='<%=look_object%>' />
    <INPUT type='hidden' name='look_templateBoui' value='<%=look_templateBoui%>' />
    <INPUT type='hidden' name='look_attributeName' value='<%=look_attributeName%>' />
    <INPUT type='hidden' name='look_relatedBoui' value='<%=look_relatedBoui%>' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null)boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
