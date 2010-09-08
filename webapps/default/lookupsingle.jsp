<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory,netgest.bo.plugins.data.*"%>
<%@ page import="netgest.utils.*,netgest.bo.localized.JSPMessages, netgest.bo.system.boSession"%>
<%@ page import="java.sql.*"%>

<jsp:useBean id="DOCLIST" scope="session" class="netgest.bo.dochtml.docHTML_controler"></jsp:useBean>
<%
String look_o = request.getParameter("look_object");
if ( look_o.equalsIgnoreCase("boObject") || look_o.indexOf(";")!=-1 || look_o.indexOf("$boconfig_contents$")!=-1 || look_o.indexOf("$boconfig_images$")!=-1)
{
%>
<jsp:forward page="__lookSingleGenericObject.jsp"/>
<%
}
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
if(request.getAttribute("initPageRequest")==null && request.getParameter("notInit")==null ){    request.setAttribute("initPageRequest","Y")
;    initPage=true
;}else {    initPage=false;
}int IDX;int cvui;
boDefHandler bodef;
boDefAttribute atr;
String idbolist;
String[] ctrl;
//    docParameter attr;
AttributeHandler attH=null;
    request.setAttribute(ControllerFactory.CONTROLLER_STICK_KEY,"true");
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_parentObj       = request.getParameter("look_parentObj");
    String look_parentBoui      = request.getParameter("look_parentBoui");
    String look_query           = request.getParameter("look_query");
    String look_parentAttribute = request.getParameter("look_parentAttribute");
    String clientIDX            = request.getParameter("clientIDX");
    String look_action          = request.getParameter("look_action");
    String options              = request.getParameter("options");
    String myIDX                = request.getParameter("myIDX");
    
    String searchString         = request.getParameter("searchString");
    
    Hashtable p_excludeInput = new Hashtable();
    

    if( look_parentBoui != null && !"".equals(look_parentBoui)
        &&
        look_parentAttribute != null && !"".equals(look_parentAttribute))
    {
        boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
        attH = attParent.getAttribute(look_parentAttribute);
        boDefHandler[] listObjs = attH.getDefAttribute().getTransformObjects();
        if(listObjs != null)
        {
            if(listObjs.length > 1)
            {
            %>
            <jsp:forward page="__lookSingleGenericObject.jsp"/>
            <%
            }
            else if(listObjs.length == 1)
            {
                look_object = listObjs[0].getName();
            }
        }
    }

    bodef=boDefHandler.getBoDefinition(look_object);
    boDefHandler[] filterObjectsInterface = null;
    boolean isInterface=false;
    String[] interfObj =null;
    if ( bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface )
    {
      boDefInterface bodefInterface = (boDefInterface)bodef;
      interfObj = bodefInterface.getImplObjects();
      isInterface=true;
      boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
      if(attParent != null)
      {
        attH = attParent.getAttribute(look_parentAttribute);
        filterObjectsInterface = attH.getDefAttribute().getObjects();
      }
    }

    
    
  boObjectFinder[] finders = boObject.getFinders( bodef );
  if( finders != null )
  {
      docHTML_section    secFindByPK = DOC.createSection("lookupByPK"+look_object+look_parentAttribute,"",true,2);
      secFindByPK.setTitle( netgest.bo.localized.JSPMessages.getString( "lookups.3" )+ ":");
      for( int f=0; f < finders.length; f++ )
      {
          String finder_id = finders[f].getId();
          // Finder's dos objecto ( Só os que estão mapeados )
          if( request.getParameterValues(finder_id + "_FINDBYKEYS") != null )
          {
              p_excludeInput.put(finder_id + "_FINDBYKEYS","yes");
              
              boObject lookObj = boObject.getBoManager().lookByPrimaryKey( boctx, look_object, request.getParameterValues(finder_id + "_FINDBYKEYS") );
              if( lookObj != null )                  
              {
                  if((look_parentBoui == null || "".equals(look_parentBoui)) && look_action != null && !"".equals(look_action))
                  {
                      String __url=look_action;
                      __url+="?clientIDXtoClose="+myIDX+"&setBoui="+String.valueOf(lookObj.getBoui());
                        out.clear();
                        pageContext.forward( __url);
                        return;
                  }
                  boObject targetObj = boObject.getBoManager().loadObject( boctx, Long.parseLong( look_parentBoui ) );
                  targetObj.getAttribute( look_parentAttribute ).setObject( lookObj );
                  String __url="lookupsingleupdate.jsp";
                  __url+="?clientIDXtoClose="+myIDX;
                  out.clear();
                  pageContext.forward( __url);
                  return;
              }
          }
      }
    }
    
    long valueToPut=0;
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");
       boObjectList xlist=null;
       if(  searchString.startsWith("b:") )
       {
           String[] bs;
           if( searchString.length() > 2 )
           {
             bs= searchString.split(":")[1].split(";");
             xlist=netgest.bo.runtime.boObjectList.list(boctx, look_object, bs );
             if ( xlist.getRowCount()==1 )
             {
                valueToPut = xlist.getObject().getBoui();
             }
           }
           else
           {
            valueToPut = -1;
           }
           
            
                 
       }
       else
       {

                
            //xlist=netgest.bo.runtime.boObjectList.list(boctx,"select "+look_object+" where 1=1",1,30,"",searchString,null,null);
            //if ( xlist.getRowCount()==1 )
            //{
            //    valueToPut = xlist.getObject().getboui();
            //}
           Connection cn = null;
           PreparedStatement pstm = null;
           ResultSet rslt = null;
           try
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
                    for (int i = 0; i < defs.length; i++) 
                    {
                        objs.add( defs[i].getName() );
                    }
//                    pklookobjs = new String[] { look_object };
                    pklookobjs = (String[])objs.toArray( new String[ objs.size() ] );
               }
               
               boObject findByKeyObj = null; 
               for (int i = 0;findByKeyObj == null && i < pklookobjs.length; i++) 
               {
                   findByKeyObj = boObject.getBoManager().lookByPrimaryKey( 
                                                            boctx, 
                                                            pklookobjs[i],  
                                                            new String[] { searchString }
                                                        );
               }
               if( findByKeyObj != null )
               {
                   valueToPut = findByKeyObj.getBoui();
               }
               else
               {
                 String  boql=attH.getFilterBOQL_query();
                  
                  if (boql==null || boql.equals(""))
                  {
                    boql="select "+look_object +" where 1=1 ";
                  }
                                    
                  int i = boql.toUpperCase().indexOf("SELECT", 0);
                  if(i > -1) boql="SELECT /*+ FIRST_ROWS */ " + boql.substring(i+6);                
                  i=boql.toUpperCase().lastIndexOf("ORDER BY");
                  if(i > -1) boql=boql.substring(0,i);                  
                  boObjectList list=boObjectList.list(boctx,boql,1,3,"",searchString,null,null,true);                  
                  int ii=0;
                  while ( list.next() )
                  {
                      valueToPut=list.getCurrentBoui();  //xlist.getCurrentBoui();
                      ii++;                            
                  }
                  if( ii>1 ) valueToPut=0;
                }
            }
            catch(Exception e ){ e.printStackTrace();}
            finally
            {
                try{ rslt.close();} catch( Exception e){};
                try{ pstm.close();} catch( Exception e){};
                try{ cn.close();} catch( Exception e){};
            }
       }
       
       if (valueToPut!=0 )
       {
       //fazer rediretc
            if(look_parentBoui != null && !"".equals(look_parentBoui))
            {
                if(valueToPut==-1)
                {
                    DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueObject(null);;
                }
                else
                {
                    DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueLong( valueToPut );
                }
            }
            else if(look_action != null && !"".equals(look_action))
            {
            	String __url=look_action;
                __url+="?clientIDXtoClose="+myIDX+"&setBoui="+String.valueOf(valueToPut);               
                out.clear();
                pageContext.forward( __url);
                return;
            }
        
            String __url="lookupsingleupdate.jsp";
            __url+="?clientIDXtoClose="+myIDX;
            
            out.clear();
            pageContext.forward( __url);
            return;

        
        }
    }
    else
    {
        searchString="";
    }
    if ( options == null ) options="";
    docHTML_section sec;
%>

<html> 
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%>
<% if ( isInterface ){ %>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<% } %>
<script>

var bFrameIsReady = false;
function frameReady()
{
    try
    {
        tdSearch.disabled = false;
        bFrameIsReady = true;
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
</script>
<script>

var objLabel='<img align="absmiddle" hspace=3 src="templates/form/std/search16.gif"><%=boDefHandler.getBoDefinition(look_object).getLabel()%> ';var objDescription='';</script>
<body onload="try{winmain().activeDocByIdx(<%=myIDX%>);document.getElementById('TEXTSEARCH').focus()}catch(e){};frameReady();">
<script language="javascript">
    function setUrlAttribute(href,attname,value) {
        var xqry = href.substring(href.indexOf("?")+1);
        var xargs = xqry.split("&");
        var fnd = false;
        for(var i=0;i<xargs.length;i++) {
            if(xargs[i].split("=")[0]==attname) {
                xargs[i] = attname+"="+encodeURIComponent(value);
                fnd = true;
            }
        }
        if(!fnd) xargs[xargs.length] = attname+"="+encodeURIComponent(value);
        return href.substring(0,href.indexOf("?")+1)+xargs.join("&"); 
        
    }
</script>
   
   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td id='tdSearch' style='height:40px;width="100%"' valign="top" disabled='true'>
<form class="objectForm" name="boForm" id="<%=IDX%>">
          <%
         // sec=DOC.getSection("lookup"+look_object+look_parentAttribute);
        //  if(sec==null)
        //  {
              sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,5);
              
              
              sec.setTitle( JSPMessages.getString( "lookups.4" ) + ":");
              
              if( finders != null )
              {
                  sec.setShowTile( true );
                  docHTML_section    secFindByPK = DOC.createSection("lookupByPK"+look_object+look_parentAttribute,"",true,2);
                  secFindByPK.setTitle( netgest.bo.localized.JSPMessages.getString( "lookups.3" ) );
                  for( int f=0; f < finders.length; f++ )
                  {
                      String finder_id = finders[f].getId();
                      docHTML_sectionRow rowFindByPK    = secFindByPK.addRow();
                      docHTML_sectionCell cellFindbyPK  = rowFindByPK.addCellMultiField();
                      
                      cellFindbyPK.addField( docHTML_sectionField.newText(
                                                    new StringBuffer(finder_id + "_FINDBYKEYS"),
                                                    new StringBuffer(finder_id + "_FINDBYKEYS"),
                                                    new StringBuffer( finders[f].getLabels()[0] ),
                                                    new StringBuffer(),
                                                    null,
                                                    //new StringBuffer("if(event.keyCode==13){findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}")
                                                    null
                                                    ,null
                                                )
                                                ,"100%",
                                                new Hashtable()
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
                  }
                  secFindByPK.render(pageContext,DOC,DOCLIST);
              }
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
              // FIM - Finder's dos objecto ( Só os que estão mapeados )
%>
</form>    

<%
              
              
              docHTML_sectionRow row; 
              row=sec.addRow();
            
              Hashtable xattributes=new Hashtable();
              docHTML_sectionCell xcell=row.addCellMultiField();
              xcell.addField( docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.7" )   ),
                            new StringBuffer(searchString),null,
                            new StringBuffer( isInterface ? 
                            "if(event.keyCode==13){findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"
                            :
                            "if(event.keyCode==13){findframe.wait();findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"
                            )
                            ,null),"100%",xattributes) ;
            
              
               if ( isInterface )
               { 
                            xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.1" )   ),
                            new StringBuffer("if(bFrameIsReady){frameWorking();findframe.wait();SURL= changeSURL(AUX_SURL);findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"))
                            ,null,null
                            ) ;
                } else
                { 
                            xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer( netgest.bo.localized.JSPMessages.getString( "lookups.1" )   ),
                            new StringBuffer("if(bFrameIsReady){frameWorking();findframe.wait();findframe.location.href=SURL+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"))
                            ,null,null
                            ) ;  
                }
                StringBuffer x=new StringBuffer(
                    "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                    look_object+"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())");
                    
                             
                    
                    
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("queryUserButton"),
                            new StringBuffer(netgest.bo.localized.JSPMessages.getString( "lookups.2" )  ),
                            x)
                            ,null,null
                            ) ;

          //   }  
//row.addCell(bodef.getAttributeRef("id"),xattributes);

  sec.render(pageContext,DOC,DOCLIST);

%>


          </td>
     </tr>
          <!--início do ciclo para seleccionar o tipo de objetos-->
     <%
     ArrayList objs = new ArrayList();
     if( isInterface )
     {
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
                                    for( int i=0;i < interfObj.length ;i++)
                                    {
                                        if(filterObjectsInterface == null || filterObjectsInterface.length == 0)
                                        {
                                            implObjDef = boDefHandler.getBoDefinition(interfObj[i]);
                                            objs.add(implObjDef);
                                        }
                                        else
                                        {
                                            for( int j=0;j < filterObjectsInterface.length;j++)
                                            {
                                                if( filterObjectsInterface[j].getInterfaceType() == boDefHandler.INTERFACE_STANDARD )
                                                {
                                                   String[] implObjs = ((boDefInterface)filterObjectsInterface[j]).getImplObjects();
                                                   for(int k = 0; implObjs != null && k < implObjs.length; k++)
                                                   {
                                                        if(interfObj[i].equals(implObjs[k]))
                                                        {
                                                            implObjDef = boDefHandler.getBoDefinition(interfObj[i]);
                                                            objs.add(implObjDef);
                                                        }
                                                   }
                                                }
                                                else
                                                {
                                                    if(interfObj[i].equals(filterObjectsInterface[j].getName()))
                                                    {
                                                        implObjDef = boDefHandler.getBoDefinition(interfObj[i]);
                                                        objs.add(implObjDef);
                                                    }
                                                }
                                            }
                                        }                                        
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
     <!--fim do ciclo para seleccionar o tipo de objetos-->

     <tr> 
          <td height="100%" width="100%" valign="top" >
                <%
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(look_object);
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                String boql;
                String boql0="select "+look_object+" where 0=1";

                String orderBy ="";
                if ( searchString.length() > 0 )
                {
                   boql0="select "+look_object+" where 1=1";
                   orderBy = "--ATT--";
                }

                boql0=java.net.URLEncoder.encode( boql0) ;
                
                String boqlCond="";
                if (look_action==null || !(look_action.equals("__writeURLtoHTMLEditor.jsp"))) 
                {
                    if(look_parentBoui != null && !"".equals(look_parentBoui))
                    {
                      boqlCond = boObject.getBoManager().loadObject(boctx, 
                          Long.parseLong(look_parentBoui)).getAttribute(look_parentAttribute).getFilterBOQL_query();
                    }
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
                      
                      orderBy = "--ATT--";
                   
                  }
                }
                else if(look_query.indexOf("SELECT") != -1)
                {
					if(boqlCond.length()>1 && boqlCond.indexOf("SELECT") != -1)
						boql = boqlCond;
					else
						boql = look_query;
                }
                else
                {
                  if(boqlCond.length()>1)
                  {
                    int whereInd = boqlCond.indexOf("where");
                    boql=boqlCond.substring(0,whereInd) + "where (" + look_query + ") AND " + boqlCond.substring(whereInd+6);
                    orderBy = "--ATT--";
                  }
                  else
                  {
                    boql="select "+look_object+" where "+look_query;
                    orderBy = "--ATT--";
                  }
                }
                boql=java.net.URLEncoder.encode( boql) ;
                orderBy = java.net.URLEncoder.encode( orderBy ) ;
                __url=__url.toLowerCase();
//                searchString=java.net.URLEncoder.encode(searchString);
              
           %>
                <script language="javascript">
                var AUX_SURL='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone"%>';
                var SURL='<%=__url+"?docid="+IDX+"&options="+options+"&method=list&boql="+boql+"&listmode=searchone"%>';
                </script>
                <IFRAME onload='frameReady();' id=findframe src='<%=__url+"?docid="+IDX+"&list_fulltext="+searchString+"&options="+options+"&method=list&boql="+boql+"&list_orderby="+orderBy+"&listmode=searchone&controllerName="+DOC.getController().getName()%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>

     
    
   </table>
<FORM name='boFormSubmit' action="<%if ( look_action==null ){%>lookupsingleupdate.jsp <%} else {%><%=look_action%><%}%>" method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    <INPUT type='hidden' name='look_parentObj' value='<%=look_parentObj%>' />
    <INPUT type='hidden' name='look_parentBoui' value='<%=look_parentBoui%>' />
    <INPUT type='hidden' name='look_parentAttribute' value='<%=look_parentAttribute%>' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormLookup' />
    <INPUT type='hidden' value='1' name='boFormSubmitObjectType' />
    <INPUT type='hidden' name='boFormSubmitId' />
    <INPUT type='hidden' value='<%=options%>' name='options' />
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

