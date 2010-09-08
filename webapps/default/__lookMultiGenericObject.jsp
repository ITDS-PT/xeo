<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.*"%>
<%@ page import="netgest.bo.controller.ControllerFactory"%>

<%@ page import="java.sql.*"%>
<%@ page import="javax.sql.*,java.awt.*"%>
<%@ page import="javax.naming.*"%>

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
    
    boDefHandler xParentDef     = boDefHandler.getBoDefinition(look_parentObj);
    boDefAttribute xatt         = xParentDef.getAttributeRef( look_parentAttribute );
    String myIDX                = request.getParameter("myIDX");
    netgest.bo.def.boDefViewer vw=xParentDef.getViewer("general");
    String searchString         = request.getParameter("searchString");
    
    Hashtable p_excludeInput = new Hashtable();
    
    
    
     bodef=boDefHandler.getBoDefinition(look_object);
  boDefHandler[] xobjs = null;
 // sec.setTitle("Procura de "+bodef.getLabel() );

  if( look_parentBoui != null && !"".equals(look_parentBoui)
        &&
        look_parentAttribute != null && !"".equals(look_parentAttribute))
    {
            boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
            AttributeHandler attH = attParent.getAttribute(look_parentAttribute);
            xobjs = attH.getDefAttribute().getTransformObjects();            
    }

  docHTML_sectionRow row; 
  if(xobjs == null || xobjs.length == 0)
  {
        if("valueList".equals(look_parentAttribute))
        {
            boObject value = DOC.getController().getObject(ClassUtils.convertToLong(look_parentBoui));
            boObject object =  value.getAttribute("object").getObject();            
            look_object = object.getAttribute("name").getValueString();
            boObject aux = boObject.getBoManager().createObject(DOC.getEboContext(),look_object);
            xobjs = new boDefHandler[]{aux.getBoDefinition()};               
        }
        else
        {
            xobjs = xatt.getObjects();
        }
  }
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");
       String[] valuesToSearch=searchString.split(",");
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
       if(valueToPut.length()==0)
       {
            boolean martelada=false;
           for (int i = 0; i < valuesToSearch.length && !martelada ; i++) 
           {
                
           
            //martelada ANACOM
            
                for (int j = 0; j < xobjs.length ; j++) 
                {
                    if ( xobjs[j].getName().equalsIgnoreCase("Ebo_Document") )
                    {
                        martelada=true;
                    }
                }
                if ( martelada )
                {
                    String ccadid = valuesToSearch[i];
                    boObjectList xlist=boObjectList.list(boctx,"select Ebo_Document where gdid='"+ccadid+"'");
                    xlist.beforeFirst();
                    boolean ff = false;
                    if ( xlist.next() )
                    {
                       valueToPut=""+xlist.getObject().getBoui();
                       ff=true;
                    }
                    if ( !ff )
                    {
                        //procurar no ccad
                        
                            final InitialContext ic = new InitialContext();
                            Connection cn = null;
                            PreparedStatement pstm = null;
                            ResultSet rslt = null;
                            try 
                            {
                                    cn = ((DataSource)ic.lookup("jdbc/ccad")).getConnection();
                                    String sid_doc = ccadid;
                                    pstm = null;
                                    long id_doc;
                                    if ( sid_doc.startsWith("E-") || sid_doc.startsWith("e-") )
                                    {
                                        pstm = cn.prepareStatement("SELECT ID_DOC,sua_ref,assunto FROM DOC_E WHERE num_eis=?");
                                        id_doc  = Long.parseLong( sid_doc.substring(2) );
                                    }
                                    else if ( sid_doc.startsWith("I-") || sid_doc.startsWith("i-") )
                                    {
                                        pstm = cn.prepareStatement("SELECT ID_DOC,sua_ref,assunto FROM DOC_I WHERE num_eis=?");
                                        id_doc  = Long.parseLong( sid_doc.substring(2) );
                                    }
                                    else if ( sid_doc.startsWith("S-") || sid_doc.startsWith("s-") )
                                    {
                                        pstm = cn.prepareStatement("SELECT ID_DOC,sua_ref,assunto FROM DOC_S WHERE num_eis=?");
                                        id_doc  = Long.parseLong( sid_doc.substring(2) );
                                    }
                                    else
                                    {
                                        pstm = cn.prepareStatement("SELECT ID_DOC,sua_ref,assunto FROM DOC_E WHERE num_eis=?");
                                        id_doc = Long.parseLong( sid_doc );
                                    }
                                    
                                    pstm.setLong(1,id_doc);
                                    rslt = pstm.executeQuery();
                                    boolean ok=true;
                                    String ref="";
                                    String assunto="";
                                    
                                    if(rslt.next()) 
                                    {
                                      ref = rslt.getString(2);
                                      assunto=rslt.getString(3);
                                    } 
                                    else ok= false;
                                    rslt.close();
                                    pstm.close();
                                    if ( ok )
                                    {
                                        try
                                        {
                                            boObject newdoc = netgest.bo.runtime.boObject.getBoManager().createObject(boctx,"Ebo_Document");
                                            newdoc.getAttribute("gdid").setValueString( sid_doc );
                                            newdoc.getAttribute("fileName").setValueString( ref );
                                            if ( assunto.length() > 200)
                                            {
                                                assunto=assunto.substring(0,199);
                                            }
                                            System.out.println("entrou"+assunto);
                                            newdoc.getAttribute("description").setValueString( assunto );
                                            
                                            newdoc.update();
                                            valueToPut=""+newdoc.getBoui();
                                            
                                        }
                                        catch(Exception e){e.printStackTrace(); }
                                    }
                                    
                            }
                            catch(Exception e){}
                            finally
                            {
                                try
                                {
                                    if(rslt != null) rslt.close();
                                }catch(Exception e){}
                                try
                                {
                                if(pstm != null) pstm.close();
                                }catch(Exception e){}
                                try
                                {
                                    if(cn!=null) cn.close();
                                }catch(Exception e){}
                            }
                            
                    }
            
            
            
                }
                if ( !martelada )
                {
                //fim da mrtelada
                        boolean found=false;
                        for (int j = 0; j < xobjs.length && !found ; j++) 
                        {
                    
                            boObjectList xlist=netgest.bo.runtime.boObjectList.list(boctx,"select "+xobjs[j].getName() +" where 1=1",1,30,"",valuesToSearch[i],null,null);
                            xlist.beforeFirst();
                            while (xlist.next() )
                            {
                                found=true;
                                if(valueToPut.length() >0 )
                                {
                                    valueToPut+=";"+xlist.getCurrentBoui();
                                }
                                else
                                {
                                    valueToPut+=""+xlist.getCurrentBoui();
                                }
                            }
                        
                        }
                }
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
        
        	String __url="lookupmultiupdate_section.jsp";
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
    
    
    String jspName              = null;
    
    
    if (  vw.HasForm( look_parentAttribute+"_list"  )  ) //look_parentAttribute.equalsIgnoreCase("TO") || look_parentAttribute.equalsIgnoreCase("CC"))
    {
            jspName = xParentDef.getName()+"_"+look_parentAttribute+"_generallist.jsp";
    }
    
    boolean acceptTemplates     = false;
    boolean acceptClasses       = false;
    
    if ( look_parentAttribute.equals("DAO")  )
    {
        acceptTemplates = true ;
    }
    else if ( look_parentAttribute.equals("RO") )
    {
        acceptClasses = true;
    }
    docHTML_section sec;   
%>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<%@ include file='boheaders.jsp'%>
<base target="_self">
<script>

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
        
        frameWorking();
        findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
        
    }
</script>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>
var objLabel='<img align="absmiddle" hspace=3  src="templates/form/std/search16.gif"><%=xatt.getLabel()%> ';var objDescription='';</script>
<body onload="changeUrl();">
<form class="objectForm" name="boForm" id="<%=IDX%>">

   <table cellpadding=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:40px;width="100%"' valign="top" id='tdSearch' disabled='true' >
          <%
         
         
 
  StringBuffer[] xInternal = new StringBuffer[ xobjs.length ] ;
  StringBuffer[] xExternal = new StringBuffer[ xobjs.length ] ;
  String[] xQueries  = new String[ xobjs.length ] ;
  
  boObject          oParentObj = DOC.getObject( Long.parseLong( look_parentBoui ) );
  AttributeHandler  oParentAtt = oParentObj.getAttribute( look_parentAttribute );
  
  
  for (byte i = 0; i < xobjs.length; i++) 
  {
        xInternal[i] = new StringBuffer( xobjs[i].getName() );
        xExternal[i] = new StringBuffer( xobjs[i].getLabel());
        xQueries[i]  = oParentAtt.getFilterBOQL_query( xobjs[i].getName() );
        
        if( xQueries[i] == null || xQueries[i].length() == 0 )
        {
            xQueries[i] = "select "+xInternal[i]+" where 1=1 ORDER BY --ATT--";
        }
         xQueries[i] =  xQueries[i].replaceAll("\"","\\\"");
  }

  netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());

  sec = DOC.getSection("lookupGeneric"+look_object+look_parentAttribute);
  sec = null;
  if ( sec == null ) {
     sec = DOC.createSection("lookupGeneric"+look_object+look_parentAttribute,"",false,2);
     if (  acceptClasses )
     {
          java.util.Hashtable xattributes=new java.util.Hashtable();
          row=sec.addRow();
          StringBuffer[] itipo = new StringBuffer[ 3 ] ;
          StringBuffer[] etipo = new StringBuffer[ 3 ] ;
          
          etipo[0] = new StringBuffer( "Categoria de Objecto" );
          itipo[0] = new StringBuffer( "1" );
          
          etipo[1] = new StringBuffer( "Modelo de Objecto" );
          itipo[1] = new StringBuffer( "2" );
                
          etipo[2] = new StringBuffer( "Objecto" );
          itipo[2] = new StringBuffer( "3" );
          
          row.addCell(
                    docHTML_sectionField.newCombo(
                    new StringBuffer("rep"),
                    new StringBuffer("idrep"),
                    new StringBuffer("Adicionar"),
                    itipo[0],
                    etipo,
                    itipo,
                    false,
                    false,
                    new StringBuffer("changeacceptc"),null,null
                    ) 
                    ,xattributes
                    );
         
     }


      
      Hashtable xattributes=new Hashtable();
      row=sec.addRow();
      row.addCell(
                docHTML_sectionField.newCombo(
                new StringBuffer("selTypeObject"),
                new StringBuffer("nametipo"),
                new StringBuffer("Objecto"),
                xInternal[0],
                xExternal,
                xInternal,
                acceptClasses,false,
                new StringBuffer("changeobject"),null,null
                ) 
                ,xattributes
                );
                
     
      
      
     if ( acceptTemplates )
     {
          xattributes=new java.util.Hashtable();
          row=sec.addRow();
          StringBuffer[] itipo = new StringBuffer[ 2 ] ;
          StringBuffer[] etipo = new StringBuffer[ 2 ] ;
          
          etipo[0] = new StringBuffer( "Modelo" );
          itipo[0] = new StringBuffer( "0" );
          
          etipo[1] = new StringBuffer( "Normal" );
          itipo[1] = new StringBuffer( "1" );
          
          row.addCell(
                    docHTML_sectionField.newCombo(
                    new StringBuffer("mod"),
                    new StringBuffer("idmod"),
                    new StringBuffer("Formato "),
                    itipo[0],
                    etipo,
                    itipo,
                    false,
                    new StringBuffer("changeacceptt"),null,null
                    ) 
                    ,xattributes
                    );
         
     }
     
      

              sec.setTitle("Procura texo livre:");
              
              boObjectFinder[] finders = boObject.getFinders( objdef );
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
                          
                          boObject lookObj = boObject.getBoManager().lookByPrimaryKey( boctx, objdef.getName(), request.getParameterValues(finder_id + "_FINDBYKEYS") );
                          if( lookObj != null )                  
                          {
                              boObject targetObj = boObject.getBoManager().loadObject( boctx, Long.parseLong( look_parentBoui ) );
                              bridgeHandler bh = targetObj.getBridge( look_parentAttribute );
                              if( !bh.haveBoui( lookObj.getBoui() ) )
                              {
                                  targetObj.getBridge( look_parentAttribute ).add( lookObj.getBoui() );
                              }
                          }
                      }
                      docHTML_section    secFindByPK = DOC.createSection(finder_id + "_lookupByPK"+objdef.getName()+look_parentAttribute,"",true,2);
                      secFindByPK.setTitle("Procura por Chave:");
                      
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
                                                    new StringBuffer("Adicionar"),
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

     
        row=sec.addRow();
      docHTML_sectionCell xcell=row.addCellMultiField();
      
      xcell.addField( 
                    docHTML_sectionField.newText(
                    new StringBuffer("TEXTSEARCH"),
                    new StringBuffer("TEXTSEARCH"),
                    new StringBuffer("Texto a Procurar"),
                    new StringBuffer(),null,null,null),"100%",xattributes) ;
    
     
    
     xcell.addField( docHTML_sectionField.newButton(
                                    new StringBuffer("find"),
                                    new StringBuffer("Procura"),
                                    new StringBuffer("if(bFrameIsReady){frameWorking();findframe.wait();xurl= changeSURL(AUX_SURL);findframe.location.href=xurl+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)}"))
                                    ,"",xattributes
                                    ) ;
                    
                             StringBuffer x=new StringBuffer(
                                        "if(bFrameIsReady){winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                                        "\"+objectNameToSearch+\"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())}");
                            
                                 xcell.addField( docHTML_sectionField.newButton(
                                                new StringBuffer("queryUserButton"),
                                                new StringBuffer("Avancada"),
                                                x)
                                                ,null,null
                                                ) ;

    
 }

  sec.render(pageContext,DOC,DOCLIST);
  String __url = objdef.getBoName();
%>
<script>
var selectedObj="<%=xInternal[0]%>";
var aObjectBoql=[];
<%
for (int i = 0; i < xInternal.length; i++) 
{
%>
aObjectBoql['<%=xInternal[i]%>'] = "<%=xQueries[i]%>"
<%
}
%>

<% if ( acceptTemplates ){%>
    var acceptt=true;
    var acceptc=false;
		var AUX_SURL=('ebo_template_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent("select Ebo_Template where masterObjectClass.name='<%=xInternal[0]%>'")+'&listmode=searchmulti';
    var xurl=('ebo_template_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent("select Ebo_Template where masterObjectClass.name='<%=xInternal[0]%>'")+'&listmode=searchmulti';
<%} else if ( acceptClasses ) {%>
    var acceptt=false;
    var acceptc=true;
		var AUX_SURL=('ebo_clsreg_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent("select Ebo_ClsReg where 1=1")+'&listmode=searchmulti';
    var xurl=('ebo_clsreg_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent("select Ebo_ClsReg where 1=1")+'&listmode=searchmulti';    
<%} else  {%>
    var acceptt=false;
    var acceptc=false;
		var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchmulti';
    var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchmulti';

<%}
if ( jspName == null ) jspName=xInternal[0]+"_generallist.jsp";

%>


function changeacceptt( obj ){
    if ( obj.returnValue=="0") acceptt=true;
    else acceptt=false;
    changeUrl();
}

function changeacceptc( obj ){
    if ( obj.returnValue=="3") {
        acceptc=false;
           document.getElementById("selTypeObject").enable();
    }
    else if ( obj.returnValue=="2") {
        acceptc=true;
           document.getElementById("selTypeObject").enable();
    }
    else{
        acceptc=true;
        document.getElementById("selTypeObject").disable();
    }
    changeUrl(obj.returnValue);
}

function changeUrl()
{
   
    if ( acceptt )
    {
      var url=('ebo_template_generallist.jsp').toLowerCase();
      var boql="select Ebo_Template where masterObjectClass.name='"+selectedObj+"'";
      xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
      AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
      objectNameToSearch='Ebo_Template';
    }
    else if ( acceptc && (rep.returnValue=='1' || rep.returnValue=='2') )
    {
      if ( rep.returnValue =='1' )
      {
          var url=('ebo_clsreg_generallist.jsp').toLowerCase();
          var boql="select Ebo_ClsReg where 1=1";
          xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
          AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
          objectNameToSearch='Ebo_ClsReg';
      }
      else {
      
          var url=('ebo_template_generallist.jsp').toLowerCase();
          var boql="select Ebo_Template where masterObjectClass.name='"+selectedObj+"'";
          xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
          AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
          objectNameToSearch='Ebo_Template';
      }
    }
    else
    {
      var url=(selectedObj+'_generallist.jsp').toLowerCase();
      //var boql='select '+selectedObj+' where 1=1 ORDER BY --ATT--';
      var boql=aObjectBoql[selectedObj];
      objectNameToSearch=selectedObj;
      xurl=url+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti' ;
      AUX_SURL=url+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=yes&boql='+encodeURIComponent(boql)+'&listmode=searchmulti';
    }
    frameWorking();
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value);
    document.getElementById('queryUserButton').style.color="";
    document.getElementById('queryUserButton').xmlFilter=null;
    
}

window.objectNameToSearch='<%=xInternal[0]%>';
function changeobject( obj ){
    selectedObj=obj.returnValue;
    objectNameToSearch=obj.returnValue;
    
    changeUrl();
}

</script>

          </td>
     </tr>
     <!--início do código para seleccionar o tipo de objetos-->
     <%
     if(bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface)
     {
        boDefInterface bodefInterface = (boDefInterface)bodef;
        String[] interfObj = bodefInterface.getImplObjects();
     %>
         <tr>
            <td>
            <table style='width:100%'>
                <tr>
                    <td style='width:100%;border-bottom:1px solid #AAAAAA'>
                        <span><b>Procurar em todos os objectos</b></span>&nbsp;<input class='rad' onclick='changeExclude();' checked  id='selectedAllObject' type=checkbox />
                        <span id="sayExclude" style='display:none'><font color="red">Seleccione os objectos que deseja procurar</font></span>
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
        /*
                netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
                String __url = objdef.getBoName();
                
                __url+="_general"+"list.jsp";
                String boql;
                boql="select "+xInternal[0].toString()+" where 0=1";
                
                __url=__url.toLowerCase();
                */
          
                
                __url+="_general"+"list.jsp";
                String boql;
                String boql0="select "+xInternal[0].toString()+" where 0=1";;
                boql0=java.net.URLEncoder.encode( boql0,"UTF-8") ;
                
                
                String boqlCond = boObject.getBoManager().loadObject(boctx, 
                      Long.parseLong(look_parentBoui)).getAttribute(look_parentAttribute).
                      getFilterBOQL_query(xInternal[0].toString());
                
                if (look_query == null || look_query.equals("") ) 
                {
                  if(boqlCond.length()>1)
                  {
                    boql=boqlCond;
                  }
                  else
                  {
                   boql="select "+xInternal[0].toString()+" where 1=1 ORDER BY --ATT--";
                  }
                }
                else
                {
                  if(boqlCond.length()>1)
                  {
                    int whereInd = boqlCond.indexOf("where");
                    boql=boqlCond.substring(0,whereInd) + "where (" + look_query + ") AND " + boqlCond.substring(whereInd+6)+" ORDER BY --ATT--";
                  }
                  else
                  {
                   boql="select "+xInternal[0].toString()+" where "+look_query+" ORDER BY --ATT--";
                  }
                }
                
                
                
                
                boql=java.net.URLEncoder.encode( boql,"UTF-8") ;
                __url=__url.toLowerCase();
                
            
                //__url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"boql","method","listmode" } ,new String[] {boql,"list","searchOne"} );
                //pageContext.include(__url);
           %>
                <IFRAME id=findframe onload='frameReady();' src='' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
                

            
          </td>
      </tr>
      <tr><td>
          <%
          sec=DOC.getSection("confirmlookup"+look_object+look_parentAttribute);
          if(sec==null) {
             
              sec = DOC.createSection("confirmlookup"+look_object+look_parentAttribute,"",false,0);
              bodef=boDefHandler.getBoDefinition(look_object);
    
              sec.setTitle("");
              
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
                //__url=jspName.toLowerCase();
                __url="__genericResults.jsp";%>
                <IFRAME id=resultframe src='<%=__url+"?docid="+IDX+"&listmode=resultBridge&renderOnlyCardID=yes&menu=yes&method=list&parent_object="+look_parentObj+"&parent_boui="+look_parentBoui+"&parent_attribute="+look_parentAttribute+"&controllerName="+DOC.getController().getName() %>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
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
<FORM name='boFormSubmit' action="lookupmultiupdate.jsp" method='get'>
<%} else{  %>
<FORM name='boFormSubmit' action="lookupmultiupdate_section.jsp" method='get'>
<%}%>
    <INPUT type='hidden' name='boFormSubmitXml' />
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
    <INPUT type='hidden' value='controllerName' name='<%=DOC.getController().getName()%>' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
