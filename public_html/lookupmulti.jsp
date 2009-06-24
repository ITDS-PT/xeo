<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>
<%@ page import="java.sql.*"%>
<%@ page import="oracle.jsp.runtime.*"%>
<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%

String look_o = request.getParameter("look_object");
if ( look_o.equalsIgnoreCase("boObject") || look_o.equalsIgnoreCase("interface"))
{
%>
    <jsp:forward page="__lookMultiGenericObject.jsp"/>
<%
}
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
    initPage=false;}int IDX;int cvui;boDefHandler bodef;boDefAttribute atr;String idbolist;String[] ctrl;
//    docParameter attr;
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    ctrl    = DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String fromSection          = request.getParameter("fromSection");
    String look_query           = request.getParameter("look_query");
    String myIDX                = request.getParameter("myIDX");
    String searchString         = request.getParameter("searchString");
    String look_action          = request.getParameter("look_action");
    String valores              = request.getParameter("options");
    
    
    Hashtable p_excludeInput = new Hashtable();
    
    
    docHTML_section sec;
    
    if( look_parentBoui != null && !"".equals(look_parentBoui)
        &&
        look_parentAttribute != null && !"".equals(look_parentAttribute))
    {
        boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
        AttributeHandler attH = attParent.getAttribute(look_parentAttribute);
        boDefHandler[] listObjs = attH.getDefAttribute().getTransformObjects();
        if(listObjs != null)
        {
            if(listObjs.length > 1)
            {
            %>
                <jsp:forward page="__lookMultiGenericObject.jsp"/>
            <%
            }
            else if(listObjs.length == 1)
            {
                look_object = listObjs[0].getName();
            }
        }
    }
    
    
    bodef=boDefHandler.getBoDefinition(look_object);
    boolean isInterface=false;
    String[] interfObj =null;
    if ( bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface )
    {
      boDefInterface bodefInterface = (boDefInterface)bodef;
      interfObj = bodefInterface.getImplObjects();
      isInterface=true;
    }
    
    
    
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");

       String valueToPut="";
       if(  searchString.startsWith("b:") )
       {
            if( searchString.length() > 2 )
            {
                valueToPut = searchString.split(":")[1];
            }
            else
            {
                valueToPut = "reset";
            }
       }
       
       String[] valuesToSearch=searchString.split(",");
       for (int i = 0; i < valuesToSearch.length; i++) 
       {
               String[] pklookobjs;
               if( isInterface )
               {
                    pklookobjs = interfObj;
               }
               else
               {
                    ArrayList objs = new ArrayList();
                    objs.add( look_object );
                    
                    boDefHandler[] defs = bodef.getBoSubClasses();
                    for (int j = 0; j < defs.length; j++) 
                    {
                        objs.add( defs[j].getName() );
                    }
//                    pklookobjs = new String[] { look_object };
                    pklookobjs = (String[])objs.toArray( new String[ objs.size() ] );
               }
               
               boObject findByKeyObj = null; 
               for (int j = 0;findByKeyObj == null && j < pklookobjs.length; j++) 
               {
                   findByKeyObj = boObject.getBoManager().lookByPrimaryKey( 
                                                            boctx, 
                                                            pklookobjs[j],  
                                                            new String[] { searchString }
                                                        );
               }
               if( findByKeyObj != null )
               {
                    if( valueToPut.length() > 0)
                    {
                        valueToPut += ";"+findByKeyObj.getBoui();
                    }
                    else
                    {
                        valueToPut += findByKeyObj.getBoui();
                    }
               }
//            boObject foundObj = boObject.getBoManager().lookByPrimaryKey( boctx, look_object, new String[] { valuesToSearch[i] }   );
//            if( foundObj != null )            
//            {
//                if( valueToPut.length() > 0)
//                {
//                    valueToPut += ";"+foundObj.getBoui();
//                }
//                else
//                {
//                    valueToPut += foundObj.getBoui();
//                }
//            }
       }

       if ( valueToPut.length() == 0 )
       {
           Connection cn = null;
           PreparedStatement pstm = null;
           ResultSet rslt = null;
           
           cn = boctx.getConnectionData();
           StringBuffer sql = new StringBuffer("SELECT /*FIRST_ROWS*/ ui$,SCORE(1) FROM  Ebo_TextIndex WHERE contains(TEXT, ?");
           sql.append(",1) > 0 and (");
           if( isInterface )
           {
               for (int i = 0; i < interfObj.length; i++) 
               {
                  sql.append(" uiclass='")
                    .append(interfObj[i])
                    .append("'");
                  if((i+1) < interfObj.length)
                  {
                    sql.append(" OR ");
                  }
                  else
                  {
                    sql.append(" ) ");
                  }
               }
            }
           else
           {
            sql.append(" uiclass='").append(look_object).append("') ");
            }
           
           sql.append(" ORDER BY 2 DESC");
           pstm = cn.prepareStatement(sql.toString(),ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
           
           try
           {
               for (int i = 0; i < valuesToSearch.length; i++) 
               {
                   
                       pstm.setString(1, netgest.bo.runtime.boObjectList.arrangeFulltext(  boctx, valuesToSearch[i] ) );    
                       pstm.setMaxRows(3);
                  
                        rslt = pstm.executeQuery();
                        while ( rslt.next() )
                        {
                        
                            if(valueToPut.length() >0 )
                            {
                                valueToPut+=";"+rslt.getLong(1);  //xlist.getCurrentBoui();
                            }
                            else
                            {
                                valueToPut+=""+rslt.getLong(1);  //xlist.getCurrentBoui();
                            }
                                                          
                        }
                        rslt.close();
                }
           }
            catch( Exception e )
            {
                
            }
            finally
            {
                try{ cn.close();} catch( Exception e){};
                try{ pstm.close();} catch( Exception e){};
                try{ rslt.close();} catch( Exception e){};
            }
           
           
       }
       if ( valueToPut.length() > 0 )
       {
            //fazer rediretc
            if(valueToPut.equals("reset"))
            {
                DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueObject( null );
            }
            else
            {
                DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueString( valueToPut );
            }
        
            String __url=OracleJspRuntime.toStr("lookupmultiupdate_section.jsp");
            String boqlToResultFrame = null;
            __url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"clientIDXtoClose"} ,
            new String[] { myIDX } );
            out.clear();
            pageContext.forward( __url);
            return;
        }
    }
    else
    {
        searchString="";
    }
%>


<%@page import="netgest.bo.data.oracle.OracleDriver"%><html>
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

var bFrameIsReady = false;

function frameReady()
{
    try
    {
        tdSearch.disabled = false;
        bFrameIsReady = true;
        document.getElementById('TEXTSEARCH').focus();
    }
    catch(e)
    {
    }
}

function frameWorking()
{
    tdSearch.disabled = true;
    bFrameIsReady = false;
}


</script>
<%@ include file='boheaders.jsp'%>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>
    var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';
    var objDescription='';
</script>
<body onload="winmain().ndl[<%=myIDX%>].htm.style.display='';frameReady();">
<form class="objectForm" name="boForm" id="<%=IDX%>">
   <table cellpadding=3px cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top" id='tdSearch' disabled='true'>
          <INPUT type='text' disabled border="0" style="display: none" >&nbsp;</input>
          <%
          
          sec=DOC.getSection("lookup"+look_object+look_parentAttribute);
          sec = null;
          
          if(sec==null)
          {
              Hashtable xattributes=new Hashtable();
              sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,2);              
//              sec.setTitle("Procura de "+bodef.getLabel() );
              sec.setTitle(netgest.bo.localized.JSPMessages.getString( "lookups.4" ) + ":");
              
              boObjectFinder[] finders = boObject.getFinders( bodef );
              if( finders != null )
              {
                  for( int f=0; f < finders.length; f++ )
                  {
                      String finder_id = finders[f].getId();
                  
                      sec.setShowTile( true );
                      
                      // Finder's dos objecto ( Só os que estão mapeados )
                      if( request.getParameterValues(finder_id + "_FINDBYKEYS") != null )
                      {
                          p_excludeInput.put(finder_id + "_FINDBYKEYS","yes");
                          
                          boObject lookObj = boObject.getBoManager().lookByPrimaryKey( boctx, look_object, request.getParameterValues(finder_id + "_FINDBYKEYS") );
                          if( lookObj != null )                  
                          {
                              boObject targetObj = boObject.getBoManager().loadObject( boctx, Long.parseLong( look_parentBoui ) );
                              targetObj.getBridge( look_parentAttribute ).add( lookObj.getBoui() );
                          }
                      }
                      docHTML_section    secFindByPK = DOC.createSection(finder_id + "_lookupByPK"+look_object+look_parentAttribute,"",true,2);
                      secFindByPK.setTitle( netgest.bo.localized.JSPMessages.getString( "lookups.3" ) + ":");
                      
                      docHTML_sectionRow rowFindByPK    = secFindByPK.addRow();
                      docHTML_sectionCell cellFindbyPK  = rowFindByPK.addCellMultiField();
                      
                      cellFindbyPK.addField( docHTML_sectionField.newText(
                                                    new StringBuffer(finder_id + "_FINDBYKEYS"),
                                                    new StringBuffer(finder_id + "_FINDBYKEYS"),
                                                    new StringBuffer( finders[f].getLabels()[0] ),
                                                    new StringBuffer(),
                                                    null,
                                                    //new StringBuffer("if(event.keyCode==13){findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}")
                                                    new StringBuffer()
                                                    ,null
                                                )
                                                ,"100%",
                                                xattributes
                                            );
                      cellFindbyPK.addField( 
                                    docHTML_sectionField.newButton(
                                                    new StringBuffer("addPk"),
                                                    new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.6" )   ),
                                                    //new StringBuffer("findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)")
                                                    new StringBuffer("boForm.submit()")
                                                )
                                                ,null
                                                ,null
                                            );
                      secFindByPK.render(pageContext,DOC,DOCLIST);
                  }
              }
              // FIM - Finder's dos objecto ( Só os que estão mapeados )
              docHTML_sectionRow row; 
              row=sec.addRow();
  
              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.7" )   ),
                            new StringBuffer(),null,
                            new StringBuffer("if(bFrameIsReady){if(event.keyCode==13){frameWorking();findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}}")
                            ,null),"100%",xattributes);

              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.1" )   ),
                             new StringBuffer("if(bFrameIsReady){frameWorking();findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"))
                            ,null,null
                            );
                     
              StringBuffer x=new StringBuffer(
                        "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                        look_object+"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())"
                    );
        
             xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.2" )   ),
                            x)
                            ,null,null
                            ) ;
                                 
          }
          sec.render(pageContext,DOC,DOCLIST);
%>
          </td>
     </tr>
          <!--início do código para seleccionar o tipo de objetos-->
     <%
     if( isInterface )
     {
//     if(bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface)
//     {
//        boDefInterface bodefInterface = (boDefInterface)bodef;
//        String[] interfObj = bodefInterface.getImplObjects();
     %>
         <tr>
            <td>
            <table style='width:100%'>
                <tr>
                    <td style='width:100%;border-bottom:1px solid #AAAAAA'>
                        <span><b><%=netgest.bo.localized.JSPMessages.getString( "lookups.8" )%></b></span>&nbsp;<input class='rad' onclick='changeExclude();' checked  id='selectedAllObject' type=checkbox />
                        <span id="sayExclude" style='display:none'><font color="red"><%=netgest.bo.localized.JSPMessages.getString( "lookups.9" )%></font></span>
                    </td>
                </tr>
                <tr>
                    <td width='100%'>
                    
                            <table id='tableToExclude' class="layout" cellpading=1 cellspacing=1 style='display:none' >
                                 <colgroup/>
                                  <col width="8"/>
                                  <col width="50"/>
                                  <col width="8"/>
                                  <col width="50"/>
                                  <col width="8"/>
                                  <col width="50"/>
                                <%
                                    boDefHandler implObjDef;
                                    boDefHandler[] subClasses;
                                    ArrayList objs = new ArrayList();
                                    for( int i=0;i < interfObj.length ;i++)
                                    {
                                        implObjDef = boDefHandler.getBoDefinition(interfObj[i]);
                                        objs.add(implObjDef);                                        
                                    }
                                        for( int i=0;i < objs.size() ;)
                                        {
                                        %>
                                        <tr>
                                        <%
                                            for(int z=0;z<3;z++, i++)
                                            { 
                                                if(i >= objs.size())
                                                {
                                                %>
                                                    <td>&nbsp;</td>
                                                <%
                                                }
                                                else
                                                {
                                                    subClasses = ((boDefHandler)objs.get(i)).getTreeSubClasses();
                                                    String subClassesName = ""; 
                                                    if(subClasses != null && subClasses.length > 0)
                                                    {
                                                        for(int j = 0; j < subClasses.length; j++)
                                                        {
                                                            subClassesName += subClasses[j].getName() + ";";
                                                        }
                                                    }
                                                %>
                                                    <td ><input subclasses='<%=subClassesName%>' class='rad' id='<%=((boDefHandler)objs.get(i)).getName()%>' type=checkbox /> </td><td  ><%=((boDefHandler)objs.get(i)).getLabel()%></td>
                                                <%
                                                }
                                            }
                                        %>
                                        </tr>
                                        <%
                                        }
                                    %>
                            </table>
                    </td>
                </tr>
            </table>
            </td>
         </tr>
     <%} %>  
     <!--fim do código para seleccionar o tipo de objetos-->

     <tr> 
          <td height="60%" width="100%" valign="top" >
                <%
               
                
                       
                
               // String __url = objdef.getBoName();
               // String boql0="select "+look_object+" where 0=1";;
               // __url+="_general"+"list.jsp";
               // String boql="select "+look_object+" where 1=1"; 
               // __url=__url.toLowerCase();
               
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
                      if( boctx.getDataBaseDriver() instanceof OracleDriver )  
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
                    if(!look_query.toLowerCase().startsWith("select"))
                    {
                        boql="select "+look_object+" where "+look_query;
                    }
                    else
                    {
                        boql= look_query;
                    }
                  }
                }
                boql=java.net.URLEncoder.encode( boql) ;
                __url=__url.toLowerCase();
           %>
              <script language="javascript">
               var AUX_SURL='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchmulti"%>';
               var SURL='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&listmode=searchmulti"%>'; 
                </script>
                  
                <IFRAME onload='frameReady();' id=findframe src='<%=__url+"?docid="+IDX+"&method=list&boql="+boql+"&list_orderby="+orderBy+"&listmode=searchmulti"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
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
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.10" )   ),
                            new StringBuffer("resultframe.submitBridge()")
                            ),null
                            ) ;

            }    


         sec.render(pageContext,DOC,DOCLIST);

    %></td></tr>
      <tr> 
          <td height="40%" width="100%" valign="top" >
                <%
               // netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                
        
                
                
                //String 
                __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                //String boql="select "+look_object+" where id=id"; 
                __url=__url.toLowerCase();
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
                String _boql = null;
                if(look_parentBoui == null || "".equals(look_parentBoui))
                {
                    _boql =  "select " + look_parentObj + " where ";
                    if(valores != null && valores.length() > 0)
                    {
                        valores = valores.substring(valores.indexOf("_")+1);
                        if(valores != null && valores.trim().length() > 0)
                        {
                            String[] bouis = valores.split(";");
                            String whereClause = "";
                            for (int i = 0; i < bouis.length; i++) 
                            {
                                if(i==0) whereClause = "(";
                                whereClause = whereClause + "boui = " + bouis[i];
                                if((i+1) < bouis.length)
                                    whereClause = whereClause + " OR ";
                                else
                                {
                                    whereClause =  whereClause + ")";
                                }
                            }
                            if("".equals(whereClause)) whereClause = "1=0";
                             _boql = _boql+whereClause;
                        }
                        else
                        {
                            _boql = _boql+"1=0";
                        }
                    }
                    else
                    {
                        _boql =  "select " + look_parentObj + " where 1=0";
                    }
                     
                }
           %>
                <IFRAME id=resultframe src='<%=__url+"?boql="+(_boql == null ? "":_boql)+"&docid="+IDX+"&listmode=resultBridge&method=list&parent_object="+look_parentObj+"&parent_boui="+look_parentBoui+"&parent_attribute=getBridge("+look_parentAttribute+")"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>
     
    
   </table>
<%
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
        String parname  = oEnum.nextElement().toString();
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
    

<% if  ( fromSection == null ) {%>    
<FORM name='boFormSubmit' action="lookupmultiupdate.jsp" method='post'>
<%} else{  %>
<FORM name='boFormSubmit' action="<%if ( look_action==null ){%>lookupmultiupdate_section.jsp<%} else {%><%=look_action%><%}%>" method='post'>
<%}%>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='look_parentObj' value='<%=look_parentObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=look_parentBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=look_parentAttribute%>' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='look_query' value='<%=request.getParameter("look_query")%>' />
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
    if(initPage) {if (boctx!=null)boctx.close();
    if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
