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

try {
    boSession bosession = (boSession)request.getSession().getAttribute("boSession");
    if(bosession== null)
    {
        
            StringBuffer url = new StringBuffer("?");
            Enumeration oEnum = request.getParameterNames();
            while( oEnum.hasMoreElements() )
            {
                String pname = oEnum.nextElement().toString();
                url.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
            }
            response.sendRedirect("login.jsp?returnToPage=__list.jsp"+ java.net.URLEncoder.encode(url.toString(),"UTF-8" ));
            return;
    
       
    }
    if(boctx==null) 
    {
        boctx = bosession.createRequestContext(request,response,pageContext);
        request.setAttribute("a_EboContext",boctx);
    }

    int IDX;
    
    boDefHandler bodef;
    boDefAttribute atr;
    String idbolist;
    String[] ctrl;

    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    String look_object          = request.getParameter("look_object");
    String showNew              = request.getParameter("showNew");
    String look_query_ini       = request.getParameter("look_query");
    String options              = request.getParameter("options");
    String searchString         = request.getParameter("searchString");  
    String orderBy              = request.getParameter("orderBy");
    
    String look_query_boui       = request.getParameter("look_query_boui");
    String clause_where1="";
    if ( look_query_boui!=null )
    {
       clause_where1=netgest.bo.userquery.userquery.userQueryToBoql_ClauseWhere( boctx,ClassUtils.convertToLong( look_query_boui ), null );
                   
    }
    
    String look_query="";
    if ( look_query_ini != null && look_query_ini.length()>0 )
    {
        look_query = look_query_ini;
    }
    if (  clause_where1.length() > 1 )
    {
        if ( look_query.length() > 0 )
        {
             look_query=look_query+" and ( "+clause_where1+" ) ";
        }
        else
        {
            look_query = clause_where1;
        }
    }
    
    bodef = boDefHandler.getBoDefinition( look_object );
    
    if ( searchString!= null )
    {
       searchString=searchString.replaceAll("_\\*_","%");
    }
    else
    {
        searchString="";
    }
     docHTML_section sec;
     
     
     
    StringBuffer parameters = new StringBuffer();
    Enumeration oEnum = request.getParameterNames();
    while( oEnum.hasMoreElements() )
    {
      String pname = oEnum.nextElement().toString();
      if( !pname.equalsIgnoreCase("method") && 
          !pname.equalsIgnoreCase("docid") && 
          !pname.equalsIgnoreCase("searchString") &&
          !pname.equalsIgnoreCase("boql")
        )
      {
          parameters.append("&").append( pname ).append( "=" ).append( java.net.URLEncoder.encode( request.getParameter( pname ),"UTF-8" ) );
      }
    }   
    parameters.append("&canSelectRows=NO");
     
    %>

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
<title>LOOKUP OBJECTS</title>
<base target="_self">
<%@ include file='boheaders.jsp'%>
<script>
var objLabel='<img align="absmiddle" hspace=3  src="resources/<%=bodef.getName()%>/ico16.gif">Lista [<%=bodef.getLabel()%>] ';var objDescription='';
</script>
<body onload="document.getElementById('TEXTSEARCH').focus()">
<!--<form class="objectForm" name="boForm" id="<%=IDX%>">-->

   <table bgcolor="#EEEEEE" cellpading=0 cellspacing=0 style='height:"100%"'>
      <tr>
          <td style='height:30px;width="100%"' valign="top">
          <%
           
            boDefHandler[] xobjs = null;
          	xobjs      =  bodef.getTreeSubClasses();
            
            StringBuffer[] xInternal = new StringBuffer[ xobjs.length+1 ] ;
            StringBuffer[] xExternal = new StringBuffer[ xobjs.length+1 ] ;
            xInternal[0] = new StringBuffer( bodef.getName() );
            xExternal[0] = new StringBuffer( bodef.getLabel());
            for (byte i = 0; i < xobjs.length; i++) 
            {
                
                xInternal[i+1] = new StringBuffer( xobjs[i].getName() );
                xExternal[i+1] = new StringBuffer( xobjs[i].getLabel());
            }
          
              sec = DOC.createSection("list"+look_object,"",false,2);
              
              docHTML_sectionRow row; 
              
              Hashtable xattributes=new Hashtable(); 
              if( xInternal.length > 1)
              {
             
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
             }         
              row=sec.addRow();
              
              docHTML_sectionCell xcell=row.addCellMultiField();
              
              xcell.addField( 
                            docHTML_sectionField.newText(
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("TEXTSEARCH"),
                            new StringBuffer("Texto a Procurar"),
                            new StringBuffer(searchString),null,new StringBuffer("if(window.event.keyCode==13){findframe.wait();findObjects()}"),null),"100%",xattributes) ;
            
              
              xcell.addField( docHTML_sectionField.newButton(
                            new StringBuffer("find"),
                            new StringBuffer("Procura"),
                            new StringBuffer("findframe.wait();findObjects()"))
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
    

            
                sec.render(pageContext,DOC,DOCLIST);
  
                boDefHandler objdef=netgest.bo.def.boDefHandler.getBoDefinition(xInternal[0].toString());
                String __url = objdef.getBoName();



                __url+="_general"+"list.jsp";
                String boql;
                String boqlCond="";
                if (look_query == null || look_query.equals("") ) 
                {
                   look_query=" 1=1 ";                   
                }
                if(boqlCond.length()>1)
                {
                    int whereInd = boqlCond.indexOf("where");
                    boql=boqlCond.substring(0,whereInd) + "where (" + look_query + ") AND " + boqlCond.substring(whereInd+6);//+" ORDER BY --ATT--";
                }
                else
                {
                   boql="select "+xInternal[0].toString()+" where "+look_query;//+" ORDER BY --ATT--";
                }
                boql +=" ORDER BY ";
                if(orderBy != null && !"".equals(orderBy))
                {
                    boql +=orderBy; 
                }
                else
                {
                    boql +="--ATT--"; 
                }
                String look =java.net.URLEncoder.encode( look_query ,"UTF-8") ; 
                boql=java.net.URLEncoder.encode( boql,"UTF-8" ) ;
                __url=__url.toLowerCase();
              
            

               

%>
<script>
var AUX_SURL=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql=<%=boql%>';
var xurl=('<%=xInternal[0]%>_generallist.jsp').toLowerCase()+'?docid=<%=IDX%>&method=list&boql=<%=boql%>';

window.objectNameToSearch='<%=xInternal[0]%>';

function changeobject( obj ){
    
    window.objectNameToSearch=obj.returnValue;    
    findObjects();
}

function findObjects()
{
    var url=(window.objectNameToSearch+'_generallist.jsp').toLowerCase();
        
    var look_query='<%=look%>';
    
    var subquery=fromInterface();
    if ( subquery.length > 0 )
    {
     boql="select "+window.objectNameToSearch+" where ("+look_query+") and ("+ subquery +")   ORDER BY --ATT--";
    }
    else
    {
        boql="select "+window.objectNameToSearch+" where "+look_query+" ORDER BY --ATT--";
    }
    
    xurl=url+'?docid=<%=IDX%>&method=list&boql='+boql+'<%=parameters%>';
    
    
    document.getElementById('queryUserButton').style.color="";
    document.getElementById('queryUserButton').xmlFilter=null;
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value);
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
    findframe.location.href=xurl+"&list_fulltext="+encodeURIComponent(document.getElementById('TEXTSEARCH').value)+"&userQuery="+text;
}

function fromInterface()
{
    var onlyObjects="";
    var isOnlyObjects=false;
    if( document.getElementById("selectedAllObject") )
    {
       isOnlyObjects=!selectedAllObject.checked
    }
    if(isOnlyObjects)
    {
        for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
        {
            // alert( objectsToSearch.elements[i].id+objectsToSearch.elements[i].checked);
        if ( objectsToSearch.elements[i].checked )
        {
                if(onlyObjects == "")
                {
                    onlyObjects+= " CLASSNAME='";
                }
                else
                {
                    onlyObjects+= "or CLASSNAME='";
                }
                
                onlyObjects+= objectsToSearch.elements[i].id + "' ";
                var subclasses = objectsToSearch.elements[i].subclasses.split(";");
                for(var j=0;j<subclasses.length;j++) 
                {
                        if(subclasses[j].length > 0)
                        {
                            onlyObjects+= "or CLASSNAME='" + subclasses[j] + "' ";
                        }
            }
        }
        }		
    }
    return onlyObjects;   
}

function changeExclude()
{
   if (selectedAllObject.checked )
   {
		if(objectsToSearch.elements.length > 0)
		{
            for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
            {
                  //objectsToSearch.elements[i].checked=false;
            }
          }
          sayExclude.style.display='none';
          tableToExclude.style.display='none';
   }
   else
   {
	  sayExclude.style.display='';
      tableToExclude.style.display='';
   }
}
</script>

          </td>
     </tr>
    <!--in?o do c??o para seleccionar o tipo de objetos-->
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
                        <form  id="objectsToSearch">
                    
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
                        </form>
                    </td>
                </tr>
            </table>
            </td>
         </tr>
     <%} %>  
     <!--fim do c??o para seleccionar o tipo de objetos-->
     <tr> 
          <td height="100%" width="100%" valign="top" >
                 
                <IFRAME id=findframe src='<%=__url+"?docid="+IDX+"&list_fulltext="+java.net.URLEncoder.encode(searchString)+"&method=list&boql="+boql + parameters%>' frameBorder=0 width='100%' scrolling=no  height='100%'></IFRAME>
          </td>
      </tr>

     
    
   </table>

<!--</form>-->
    
<FORM name='boFormSubmit'  method='get'>
    <INPUT type='hidden' name='boFormSubmitXml' />
    
    <INPUT type='hidden' value='<%=IDX%>' name='docid' />
    <INPUT type='hidden' value='<%=DOC.hashCode()%>' name='boFormSubmitSecurity' />
    <INPUT type='hidden' value='<%=options%>' name='options' />
</FORM>
</body>
</html>
<%
} finally 
{if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
%>

