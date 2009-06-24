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
    
        StringBuffer url = new StringBuffer("?");
        Enumeration oEnum = request.getParameterNames();
        while( oEnum.hasMoreElements() )
        {
            String pname = oEnum.nextElement().toString();
            url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ), "UTF-8" ) );
        }
        response.sendRedirect("login.jsp?returnToPage=__list.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
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

    String myIDX= request.getParameter("myIDX");

    boDefHandler xParentDef   = boDefHandler.getBoDefinition(look_parentObj);
    boDefAttribute xatt       = xParentDef.getAttributeRef( look_parentAttribute );

    netgest.bo.def.boDefViewer vw=xParentDef.getViewer("general");

    boDefHandler[] xobjs = null;
    if ( xatt == null )
    {
        String[] x_1 = look_parentAttribute.split("\\.");
        if(x_1 != null && x_1.length == 2)
        {
            xatt=xParentDef.getAttributeRef(x_1[0]).getBridge().getAttributeRef(x_1[1]);
            xobjs = xatt.getObjects();
        }
        else
        {
            if( look_parentBoui != null && !"".equals(look_parentBoui)
                &&
                look_parentAttribute != null && !"".equals(look_parentAttribute))
            {
                boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
                xatt = attParent.getAttribute(look_parentAttribute).getDefAttribute();
                xobjs = xatt.getObjects();
            }
        }
    }
    else
    {
        try
		{
			xobjs = xatt.getObjects();
			
		}
		catch(Exception e)
		{
			/*IGNORE*/
		}
    }
    
     String searchString         = request.getParameter("searchString");
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");
       
       boolean found=false;
       long bouitoput=0;
       if(  searchString.startsWith("b:") )
       {
           String valueToPut;
           if( searchString.length() > 2 )
           {
           valueToPut = searchString.split(":")[1].split(";")[0];
           }
           else
           {
           valueToPut = "-1";
           }
           bouitoput=netgest.utils.ClassUtils.convertToLong( valueToPut );
       }

        String[] valuesToSearch=searchString.split(",");
        searchString="";


    
        
    if ( bouitoput == 0 && xobjs != null && valuesToSearch != null)
    {
       for (int i = 0; i < valuesToSearch.length && !found; i++) 
       {
            
            for (int j = 0; j < xobjs.length && !found ; j++) 
            {
            
                boObjectList xlist=netgest.bo.runtime.boObjectList.list(boctx,"select "+xobjs[j].getName() +" where 1=1",1,30,"",valuesToSearch[i],null,null);
                xlist.beforeFirst();
                while (xlist.next() )
                {
                    found=true;
                    bouitoput = xlist.getCurrentBoui();
                    
                }
            
            }
        
              
       }
       }
       
       
       if ( bouitoput != 0 )
       {
       //fazer rediretc
            if(bouitoput==-1)
            {
            DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueObject( null );
            }
            else
            {
            DOC.getObject( netgest.utils.ClassUtils.convertToLong(look_parentBoui) ).getAttribute( look_parentAttribute ).setValueLong( bouitoput  );
            }
        
            String __url=OracleJspRuntime.toStr("lookupsingleupdate.jsp");
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


<%@page import="oracle.jsp.runtime.OracleJspRuntime"%><html>
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
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
}
</script>
<%@ include file='boheaders.jsp'%>
<script LANGUAGE="javascript" SRC="ieLibrary/lookup/interface.js"></script>
<script>


</script>
<body onload="winmain().ndl[<%=myIDX%>].htm.style.display='';document.getElementById('TEXTSEARCH').focus()">
<form class="objectForm" name="boForm" id="<%=IDX%>">

   <table bgcolor="#EEEEEE" cellpading=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:30px;width="100%"' valign="top">
          <%
          sec=DOC.getSection("lookup"+look_object+look_parentAttribute);
          if( look_parentBoui != null && !"".equals(look_parentBoui)
	        &&
	        look_parentAttribute != null && !"".equals(look_parentAttribute))
		  {
		    boObject attParent = DOC.getObject(Long.parseLong(look_parentBoui));
		    AttributeHandler attH = attParent.getAttribute(look_parentAttribute);
		    xobjs = attH.getDefAttribute().getTransformObjects();
		  }
		  if(xobjs == null)
		  {
          	xobjs      = xatt.getObjects();
            if (xobjs==null)
            {
				if ("$boconfig_contents$".equals(look_object) || "$boconfig$".equals(look_object))
				{	  
					Properties prop = boConfig.getContentMngmConfig();
					look_object = (String)prop.getProperty("Contents_Type");
					String objects[]=look_object.split(";");
					xobjs=new boDefHandler[objects.length];
					for (byte i=0;i<objects.length;i++)
					{
						boDefHandler currDef=boDefHandler.getBoDefinition(objects[i]);
						xobjs[i]=currDef;
					}
				}
				else if ("$boconfig_images$".equals(look_object))
				{	  
					Properties prop = boConfig.getContentMngmConfig();
					look_object = (String)prop.getProperty("Images_Type");
					String objects[]=look_object.split(";");
					xobjs=new boDefHandler[objects.length];
					for (byte i=0;i<objects.length;i++)
					{
						boDefHandler currDef=boDefHandler.getBoDefinition(objects[i]);
						xobjs[i]=currDef;
					}
				}
				else
				{
					String objects[]=look_object.split(";");
					xobjs=new boDefHandler[objects.length];
					for (byte i=0;i<objects.length;i++)
					{
						boDefHandler currDef=boDefHandler.getBoDefinition(objects[i]);
						xobjs[i]=currDef;
					}
				}
            }            
      }


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
          
          if(true ) //sec==null)
          {
                      sec = DOC.createSection("lookup"+look_object+look_parentAttribute,"",false,2);
                      bodef=boDefHandler.getBoDefinition(look_object);
                     // sec.setTitle("Procura de "+bodef.getLabel() );
                    
                      docHTML_sectionRow row; 
                      
                      
                    
                      Hashtable xattributes=new Hashtable();
                      row=sec.addRow();
                      row.addCell(
                                docHTML_sectionField.newCombo(
                                new StringBuffer("1"),
                                new StringBuffer("idtipo"),
                                new StringBuffer("Objecto "),
                                xInternal[0],
                                xExternal,
                                xInternal,
                                false,
                                new StringBuffer("changeobject"),null,null
                                ) 
                                ,xattributes
                                );
                                
                      row=sec.addRow();
                      
                      docHTML_sectionCell xcell=row.addCellMultiField();
                      
                      xcell.addField( 
                                    docHTML_sectionField.newText(
                                    new StringBuffer("TEXTSEARCH"),
                                    new StringBuffer("TEXTSEARCH"),
                                    new StringBuffer("Texto a Procurar"),
                                    new StringBuffer(searchString),null,null,null),"100%",xattributes) ;
                    
                      
                      xcell.addField( docHTML_sectionField.newButton(
                                    new StringBuffer("find"),
                                    new StringBuffer("Procura"),
                                    new StringBuffer("findframe.wait();xurl= changeSURL(AUX_SURL);findframe.location.href=xurl+\"&list_fulltext=\"+encodeURIComponent(TEXTSEARCH.value)"))
                                    ,"",xattributes
                                    ) ;
                    
                             StringBuffer x=new StringBuffer(
                                        "winmain().openDocUrl(\"medium\",\"__queryBuilder.jsp\",\"?__list=true&docid=\"+getDocId()+\"&relatedIDX=\"+getIDX()+\"&object="+
                                        "\"+objectNameToSearch+\"&referenceFrame=\"+findframe.getReferenceFrame()+(this.xmlFilter?\"&xmlFilter=\"+this.xmlFilter:\"\"),null,getIDX())");
                            
                                 xcell.addField( docHTML_sectionField.newButton(
                                                new StringBuffer("queryUserButton"),
                                                new StringBuffer("Avançada"),
                                                x)
                                                ,null,null
                                                ) ;
    

            }
        sec.render(pageContext,DOC,DOCLIST);
  
                 netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
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
		var AUX_SURL=('ebo_template_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent("select Ebo_Template where masterObjectClass.name='<%=xInternal[0]%>'")+'&listmode=searchone';
    var xurl=('ebo_template_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent("select Ebo_Template where masterObjectClass.name='<%=xInternal[0]%>'")+'&listmode=searchone';
<%} else if ( acceptClasses ) {%>
    var acceptt=false;
    var acceptc=true;
		var AUX_SURL=('ebo_clsreg_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent("select Ebo_ClsReg where 1=1")+'&listmode=searchone';
    var xurl=('ebo_clsreg_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent("select Ebo_ClsReg where 1=1")+'&listmode=searchone';    
<%} else  {%>
    var acceptt=false;
    var acceptc=false;
		var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';
    var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';

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
      xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
      AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
      objectNameToSearch='Ebo_Template';
    }
    else if ( acceptc && (rep.returnValue=='1' || rep.returnValue=='2') )
    {
      if ( rep.returnValue =='1' )
      {
          var url=('ebo_clsreg_generallist.jsp').toLowerCase();
          var boql="select Ebo_ClsReg where 1=1";
          xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
          AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
          objectNameToSearch='Ebo_ClsReg';
      }
      else {
      
          var url=('ebo_template_generallist.jsp').toLowerCase();
          var boql="select Ebo_Template where masterObjectClass.name='"+selectedObj+"'";
          xurl=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
          AUX_SURL=url+'?docid=<%=IDX%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
          objectNameToSearch='Ebo_Template';
      }
    }
    else
    {
      var url=(selectedObj+'_generallist.jsp').toLowerCase();
      //var boql='select '+selectedObj+' where 1=1 ORDER BY --ATT--';
      var boql=aObjectBoql[selectedObj];
      objectNameToSearch=selectedObj; 
      xurl=url+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
      AUX_SURL=url+'?docid=<%=IDX%>&controllerName=<%=DOC.getController().getName()%>&method=list&renderOnlyCardID=no&boql='+encodeURIComponent(boql)+'&listmode=searchone';
    }
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

<%--

var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';
var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent('select <%=xInternal[0]%> where 1=1 ORDER BY --ATT--')+'&listmode=searchone';
window.objectNameToSearch='<%=xInternal[0]%>';

function changeobject( obj ){
    var url=(obj.returnValue+'_generallist.jsp').toLowerCase();
    //var boql='select '+obj.returnValue+' where 1=1 ORDER BY --ATT--';
    
	boqlArr=[];
<%
	if(look_parentBoui==null || look_parentBoui.equalsIgnoreCase(""))
	{
		look_parentBoui = options.substring( options.indexOf("__")+2, options.lastIndexOf("__") );
	}
	
  	for (byte i = 0; i < xobjs.length; i++) 
  	{
		boObject opObj = boObject.getBoManager().loadObject(boctx, Long.parseLong(look_parentBoui)); // "Select " + look_parentObj + " Where 0=1"
		
		String boql_op_query = "";
		try 
		{
			boql_op_query = opObj.getAttribute(look_parentAttribute).getFilterBOQL_query(xInternal[i].toString());
		}
		catch (Exception ex) 
		{
			boql_op_query = "Select " + xInternal[i].toString() + " Where 1=1";
		}		
%>		
		boqlArr['<%=xInternal[i].toString()%>']=["<%= boql_op_query %>"];
<%
  	}  
%>
    var boql='';
	var xboql=boqlArr[obj.returnValue][0];
	
	if ( xboql.length > 0 )
	{
		boql=boqlArr[obj.returnValue];
	}
	else
	{
		boql='select '+obj.returnValue+' where 1=1';
	}
	
	boql+=' ORDER BY --ATT--';
    
    xurl=url+'?docid=<%=IDX%>&method=list&boql='+encodeURIComponent(boql)+'&listmode=searchone' ;
    objectNameToSearch=obj.returnValue;
    document.getElementById('queryUserButton').style.color="";
    document.getElementById('queryUserButton').xmlFilter=null;
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value);    
}
--%>

</script>

          </td>
     </tr>
    <!--início do código para seleccionar o tipo de objetos-->
     <%
     if(bodef!=null && bodef.getClassType() == bodef.TYPE_INTERFACE && bodef instanceof boDefInterface)
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
          <td height="100%" width="100%" valign="top" >
                 
                  <%
              /*  netgest.bo.def.boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
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
               
               String boqlCond="";
               if (look_action==null || !(look_action.equals("__writeURLtoHTMLEditor.jsp")))
               {
                 boqlCond = boObject.getBoManager().loadObject(boctx, 
                      Long.parseLong(look_parentBoui)).getAttribute(look_parentAttribute).
                      getFilterBOQL_query(xInternal[0].toString());
               }
                
                if (look_query == null || look_query.equals("") ) 
                {
					if(boqlCond==null || boqlCond.length()==0)
					{
						boqlCond = boObject.getBoManager().loadObject(boctx, 
							Long.parseLong(look_parentBoui)).getAttribute(look_parentAttribute).
							getFilterBOQL_query(xInternal[0].toString());
					}
					
                  if(boqlCond!=null && boqlCond.length()>1)
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
                  if(boqlCond!=null && boqlCond.length()>1)
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
              
            
           %>
                 
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&list_fulltext="+java.net.URLEncoder.encode(searchString,"UTF-8")+"&method=list&boql="+boql+"&listmode=searchone"%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>

     
    
   </table>

</form>
    
<FORM name='boFormSubmit' action="<%if( look_action==null ){%>lookupsingleupdate.jsp <%} else %><%=look_action%>" method='get'>
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
    <INPUT type='hidden' value='<%=options%>' name='options' />
</FORM>
</body>
</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>

