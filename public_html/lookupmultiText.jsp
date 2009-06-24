<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>
<%@ page import="java.sql.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

String look_o = request.getParameter("look_object");
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

if(request.getAttribute("initPageRequest")==null && request.getParameter("notInit")==null ){
    request.setAttribute("initPageRequest","Y");
    initPage=true;
    }
else {
    initPage=false;
    }
    int IDX;
    int cvui;
    boDefHandler bodef;boDefAttribute atr;String idbolist;String[] ctrl;
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    String lookText          = request.getParameter("lookupTextBoui");
    String lookTextName      = request.getParameter("lookTextNameId");
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String fieldID           = request.getParameter("fieldID");
    String look_query           = request.getParameter("look_query");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String myIDX                = request.getParameter("myIDX");
    String values                = request.getParameter("bouisValues");
    String look_object          = "GESTEMP_LTextDetails";
    
    String searchString         = "";
    Hashtable p_excludeInput = new Hashtable();
    
    
    docHTML_section sec;
    
    
    bodef=boDefHandler.getBoDefinition(look_object);
    long valueToPut=0;
    StringBuffer bouisSelected = null;
    
  if(values != null && values.length() > 0)
  {
    String[] vs = values.split(";");
    bouisSelected = new StringBuffer("select GESTEMP_LTextDetails where (" +searchString.toString());
    for (int i = 0; i < vs.length; i++) 
    {
        bouisSelected.append("boui = ").append(vs[i]);
        if((i+1) < vs.length)
        {
            bouisSelected.append(" OR ");
        }
    }
    bouisSelected.append( " )" );
  }
  else
  {
    bouisSelected = new StringBuffer("select GESTEMP_LTextDetails where 1=0");      
  }

    if((lookTextName != null && lookTextName.length() > 0) || (lookText!= null && lookText.length() > 0))
    {
      long lText = -1;
      if(lookText == null || lookText.length() == 0)
      {
          look_query = "1=([select 1 from ogestemp_ltext l,ogestemp_ltext$details ld where l.name = '"+lookTextName+"' and ld.child$ = ogestemp_ltextDetails.boui and l.boui = ld.parent$])";
      }
      else
      {
        lText = Long.parseLong(lookText);
        look_query = "1=([select 1 from ogestemp_ltext$details where parent$ = "+lText+" and child$ = boui])";
      }
    }
    
    if(look_query == null || look_query.length() == 0)
    {
      look_query = "parent = 0";
      bouisSelected = new StringBuffer("select GESTEMP_LTextDetails where 1=0"); 
    }
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<base target="_self">
<script>
function setUserQuery(text, reference)
{
    createHiddenInput("userQuery",text);
    if (  text != "<cleanFilter/>")
    {
        document.getElementById('queryUserButton').style.color="#990000";        
    }
    else
    {
        document.getElementById('queryUserButton').style.color="";
    }
    document.getElementById('queryUserButton').xmlFilter=text;
    findframe.location.href=SURL+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
}

function confirmarEscolha()
{
    var elems=resultframe.document.getElementsByTagName("TABLE");
    var tblToadd=null;
    for (var i=0 ;elems.length; i++)
    {
        if(elems[i].container)
        {
            tblToadd=elems[i];
            break;
        }
    }
    if(tblToadd!=null )
    {
        var rows=tblToadd.rows;
        var bouis=[];
        var b;
        for (var i=0 ; i< rows.length ; i++ )
        {
            b=rows[i].id.split("__");
            if(b[2] && b[2]!="null")
            {
                bouis[ bouis.length ]=b[2];
            }
        }
        var waitingDetachAttribute=tblToadd.waitingDetachAttribute;
        if(""+waitingDetachAttribute=="undefined" )
        {
            if(boFormSubmit.boFormSubmitXml)
            {
                with (boFormSubmit )
                {
                    var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;
                    boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";
                    clientIDXtoClose.value=windowIDX;
                    submit();
                }
            }
            else 
            {
                with (boFormSubmit )
                {
                    var xtag="Ebo_Template__"+look_templateBoui.value;
                    boFormSubmitTemplateXml.value="<"+xtag+">"+bouis.join(';')+"</"+xtag+">";
                    clientIDXtoClose.value=windowIDX;
                    submit();
                }
            }  		
        }
        else
        {
            winmain().setWDA (waitingDetachAttribute , bouis.join(";"), getDocId());
            winmain().ndl[getIDX()].close();
        }
    }	
}

</script>
<%@ include file='boheaders.jsp'%>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>
    var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';
    var objDescription='';
</script>
<body onload="winmain().ndl[<%=myIDX%>].htm.style.display='';document.getElementById('TEXTSEARCH').focus()">
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <table cellpadding=3px cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top">
          <INPUT type='text' disabled border="0" style="display: none" >&nbsp;</input>
          <%
          
          sec=DOC.getSection("lookup"+look_object+look_parentAttribute);
          sec = null;
          
          if(sec==null)
          {
              Hashtable xattributes=new Hashtable();
              sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,2);              
//              sec.setTitle("Procura de "+bodef.getLabel() );
              sec.setTitle("Procura texto livre:");
              docHTML_sectionRow row; 
              row=sec.addRow();
  
              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("Texto a Procurar"),
                            new StringBuffer(),null,
                            new StringBuffer("if(event.keyCode==13){findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}")
                            ,null),"100%",xattributes);

              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                             new StringBuffer("findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                            ,null,null
                            );
                     
              StringBuffer x=new StringBuffer(
                        "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                        look_object+"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())"
                    );
        
             xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer("Avançada"),
                            x)
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
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                String boql;
                String orderBy="--ATT--";
                String boql0="select "+look_object+" where 0=1";
                boql0=java.net.URLEncoder.encode( boql0) ;
                
                String boqlCond="";
                if(look_parentBoui != null && !"".equals(look_parentBoui))
                {
                    boqlCond = boObject.getBoManager().loadObject(boctx, 
                          Long.parseLong(look_parentBoui)).getAttribute(look_parentAttribute).getFilterBOQL_query();
                }
                
                
                if (look_query == null || look_query.equals("") ) 
                {
                  if(boqlCond.length()>1)
                  {
                        boql=boqlCond;
                  }
                  else
                  {
                      if( boctx.getDataBaseDriver() instanceof netgest.bo.data.oracle.OracleDriver )  
                  	  	boql="select "+look_object+" where [rownum] < 1000";
                      else 
                    	boql="select "+look_object+" where 1=1";
                  }
                }
                else
                {
                  if(boqlCond.length()>1)
                  {
                    int whereInd = boqlCond.indexOf("where");
                    boql=boqlCond.substring(0,whereInd) + "where (" + look_query + ") AND " + boqlCond.substring(whereInd+6);
                  }
                  else
                  {
                    boql="select "+look_object+" where "+look_query;
                  }
                }
                boql=java.net.URLEncoder.encode( boql) ;
                __url=__url.toLowerCase();
           %>
              <script language="javascript">
               var AUX_SURL='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchmulti"%>';
               var SURL='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchmulti"%>'; 
                </script>
                  
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&list_orderby="+orderBy+"&listmode=searchmulti"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>
      <tr><td>
          <%
          sec=DOC.getSection("confirmlookup"+look_object+look_parentAttribute);
          if(sec==null) {
             
              sec = DOC.createSection("confirmlookup"+look_object+look_parentAttribute,"",false,0);
              bodef=boDefHandler.getBoDefinition(look_object);
    
              sec.setTitle("");
              docHTML_sectionRow row; 
              row=sec.addRow();
              Hashtable xattributes=new Hashtable();
              row.addCell( docHTML_sectionField.newButton(
                            new StringBuffer("confirm"),
                            new StringBuffer("Confirmar a lista"),
                            new StringBuffer("confirmarEscolha();")
                            ),null
                            ) ;

            }    


         sec.render(pageContext,DOC,DOCLIST);

    %></td></tr>
      <tr> 
          <td height="40%" width="100%" valign="top" >
                <%
                __url = objdef.getBoName();
                __url+="_general"+"list.jsp";
                __url=__url.toLowerCase();
           %>
                <IFRAME id=resultframe src='<%=__url+"?docid="+IDX+"&listmode=resultBridge&method=list&boql="+java.net.URLEncoder.encode(bouisSelected.toString())%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>
     
    
   </table>
<%
    Enumeration enum = request.getParameterNames();
    while( enum.hasMoreElements() )
    {
        String parname  = enum.nextElement().toString();
        String parvalue = request.getParameter( parname );
        if( p_excludeInput.get( parname ) == null )
        {
            %>
                <input type="HIDDEN" name="<%=parname%>" value="<%=parvalue%>" />
            <%
        }
    }
%>
</form>
    

    
<FORM name='boFormSubmit' action="lookupmultiupdatetext.jsp" method='post'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='lookupTextId' value='<%=lookText%>' />
    <INPUT type='hidden' name='fieldID' value='<%=fieldID%>' />
    <INPUT type='hidden' name='look_parentObj' value='<%=look_parentObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=look_parentBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=look_parentAttribute%>' />
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
} 
finally 
{
    if(initPage) {boctx.close();
    DOCLIST.releseObjects(boctx);}
}
%>
