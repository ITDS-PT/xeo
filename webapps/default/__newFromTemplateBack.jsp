<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page import="java.util.*"%>
<%@ page import="netgest.bo.*"%>
<%@ page import="netgest.bo.dochtml.*"%>
<%@ page import="netgest.bo.runtime.*"%>
<%@ page import="netgest.bo.def.*"%>
<%@ page import="netgest.utils.*,netgest.bo.system.boSession"%>
<%@ page import="oracle.jsp.runtime.*"%>

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

    int IDX;int cvui;
    boDefHandler bodef;
    boDefAttribute atr;
    
    String idbolist;
    String[] ctrl;
//    docParameter attr;
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    
    String objectName              = request.getParameter("objectName");
    bodef = boDefHandler.getBoDefinition(objectName);
    boDefHandler[] bodefRelated = new boDefHandler[0];
    if ( bodef != null ) {
           bodefRelated =  bodef.getBoSubClasses();
    
    }
    else{
        String __url=OracleJspRuntime.toStr("error.jsp");
        __url=OracleJspRuntime.genPageUrl(__url,request,response,new String[] {"__newFromTemplate","objectName" } ,new String[] {OracleJspRuntime.toStr("objectName"),OracleJspRuntime.toStr( objectName ) } );
        out.clear();
        pageContext.forward( __url);
        return;

    }
    boolean hasSubClasses = bodefRelated.length > 0;  
    %>

<%@ include file='boheaders.jsp'%>
<script>
function getElement(e){
  if(e&&e.target) return e.target;
  return window.event.srcElement;
}
 
function getToElement(e){
  if(e&&e.target) return e.relatedTarget;
  return window.event.toElement;
}

function getRuntimeStyle(o){
  if(o.runtimeStyle) return o.runtimeStyle;
  else return o.style;
}


function so(id){
  if(!_hsos[id]){
   _hsos[id]=new Object();	
   _hsos[id].hb=document.getElementById(id+'_body');
   _hsos[id].id=id;
   }
   _hso=_hsos[id];
}



function ini(){
if(document.getElementById('first'))
selectForceGridRow_std(true,document.getElementById('first'));
}

</script>
<script>
var objLabel="<%="<img align='top' src='resources/"+objectName+"/ico16.gif' />"+bodef.getLabel()+" a partir de modelo"%>";
var objDescription='Clique no modelo pretendido para fazer um novo documento com as características previamente definidas nesse modelo';</script>
</head>

<body onload="ini()">


<table class="layout" cellpadding=0 cellspacing=0 border="0" width="100%">
  <% if( hasSubClasses ){ %>
  <col />
  <% } %>
  <col /> 
  <tr height=100%>
  <% if( hasSubClasses ){ %>
   <td >
   <table cellpadding=0 cellspacing=0 height=100% width=100%>
        <!--<tr height=25><td><span height='25' class="headerChoice">Escolha o tipo
            de objecto</span> </td>-->
		<tr><td>
		
		
     <!--BEGIN LIST OF OBJECTS -->
     
		<table cellSpacing="0" cellPadding="0" style="height:100%;width:100%;table-layout:fixed;">
		   <tr>
			 <td style="height:100%;width:100%">
			   
			   <div style="width:100%;height:100%;overflow-x:auto">
				   <table style="height:100%;width:100%;" class="g_std" cellSpacing="0" cellPadding="0" width="100%">
					  <tbody>
						  <tr height="25">
						    <td>
							   <table id="g1642_body"  cellpadding="2" cellspacing="0" style="height:25px" class="gh_std">
									<colgroup>
									<col width="100" />
									<col>
									<col width="2" />
									 <col width="15" />
 								    <tbody>
										<tr>
											<td id="g$ExpanderParent" class="gh_std">Tipo de Objecto</td>
											<td id="g$AutoExpander" class="gh_std">&nbsp;</td>
											<td class="ghSep_std">&nbsp;</td>
									        <td class="gh_std" width="14"></td>
										</tr>
									</tbody>
								</table>
							</td>
					    </tr>
		
						<tr>
							<td>
							   <div id="grid$0$activitylist"  class="gContainerLines_std">
									<table id="g1643_body"  onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643');addRowAttribute()"  cellpadding="2" cellspacing="0" class="gBodyLines_std">
										<colgroup>
										<col />
                                        
										<tr relatedElement='<%=objectName%>' >
											<td id='first' class="gCell_std" onclick="selectForceGridRow_std(true);window.event.cancelBubble=true" ><%=bodef.getLabel()%></td>
										</tr>
										<%for ( int i =0 ; i < bodefRelated.length ; i++ ){ %>
                                            <tr relatedElement='<%=bodefRelated[i].getName()%>' >
                                                    <td  class="gCell_std" onclick="selectForceGridRow_std(true);window.event.cancelBubble=true" ><%=bodefRelated[i].getLabel()%></td>
                                            </tr>
                                        <%}%>
			   
									</table>   <!--END TABLE BODY  !-->
								</div> <!--END TABLE BODY CONTAINER  !-->
						   </td>
					   </tr>
					</table>
			   
				</div>
			   
		  	</td>
			</tr>
		</table>
			    
     
    <!-- END LIST OF OBJECTS -->
     </tr></table>
	
    </td>
  
  <% }//end hasSbClasses %>   
   <td>
  
   <table cellpadding=0 cellspacing=0 height=100% width=100%>
    <!--    <tr height=25><td><span height='25' >Modelos Disponíveis</span> </td>-->
		<tr><td>
		
		
     <!--BEGIN LIST OF TEMPLATES -->
    
		<table cellSpacing="0" cellPadding="0&quot;" style="height:100%;width:100%;table-layout:fixed;">
		   <tr>
			 <td style="height:100%;width:100%">
			   <%
                boObjectList tmpls=boObjectList.list(DOC.getEboContext(),"select Ebo_Template where masterObjectClass.name='"+bodef.getName()+"'" );
                tmpls.beforeFirst();
               %>
			   <div id='<%=objectName%>' style="width:100%;height:100%;overflow-x:auto">
				   <table style="height:100%;width:100%;" class="g_std" cellSpacing="0" cellPadding="0" width="100%">
					  <tbody>
						  <tr height="25">
						    <td>
							   <table id="g999"  cellpadding="2" cellspacing="0" style="height:25px" class="gh_std">
									<colgroup>
									<col width="100" />
									<col>
									<col width="2" />
									 <col width="15" />
 								    <tbody>
										<tr>
											<td id="g$ExpanderParent" class="gh_std">Modelos</td>
											<td id="g$AutoExpander" class="gh_std">&nbsp;</td>
											<td class="ghSep_std">&nbsp;</td>
									       <td class="gh_std" width="14"></td>
										</tr>
									</tbody>
								</table>
							</td>
					    </tr>
		
						<tr>
							<td>
							   <div id="grid$0$activitylist0"  class="gContainerLines_std">
									<table id="g1000_body"  onmouseover="so('g1000');onOver_GridBody_std(event)" onmouseout="so('g1000');onOut_GridBody_std(event)" onclick="so('g1000')"  cellpadding="2" cellspacing="0" class="gBodyLines_std">
										<colgroup>
										<col />
										<%while ( tmpls.next() ){%>
										<tr><td class="gCell_std " onclick='winmain().openDoc("medium","<%=((ObjAttHandler)tmpls.getObject().getAttribute("masterObjectClass")).getObject().getAttribute("name").getValueString()%>","edit","method=newfromtemplate&parent_boui=<%=tmpls.getObject().getBoui()%>");winmain().ndl[getIDX()].close();' ><%=tmpls.getObject().getAttribute("name").getValueString()%> </td></tr>
                                        <%}%>
									</table>   <!--END TABLE BODY  !-->
								</div> <!--END TABLE BODY CONTAINER  !-->
						   </td>
					   </tr>
					</table>
				</div>
                <% 
                for (int i = 0; i < bodefRelated.length ; i++) 
                {%>
                
                <%
                tmpls=boObjectList.list(DOC.getEboContext(),"select Ebo_Template where masterObjectClass.name='"+bodefRelated[i].getName()+"'" );
                tmpls.beforeFirst();
               %>
			
                <div id='<%=bodefRelated[i].getName()%>' style="display:none;width:100%;height:100%;overflow-x:auto">
				   <table style="height:100%;width:100%;" class="g_std" cellSpacing="0" cellPadding="0" width="100%">
					  <tbody>
						  <tr height="25">
						    <td>
							   <table id="g999<%=i%>"  cellpadding="2" cellspacing="0" style="height:25px" class="gh_std">
								<colgroup>
									<col width="100" />
									<col>
									<col width="2" />
									 <col width="15" />
 								    <tbody>
										<tr>
											<td id="g$ExpanderParent" class="gh_std">Modelos</td>
											<td id="g$AutoExpander" class="gh_std">&nbsp;</td>
											<td class="ghSep_std">&nbsp;</td>
									       <td class="gh_std" width="14"></td>
										</tr>
									</tbody>
								</table>
							</td>
					    </tr>
		
						<tr>
                        
							<td>
							   <div id="grid$0$activitylist0"  class="gContainerLines_std">
									<table id="g1000<%=i%>"  onmouseover="so('g1643');onOver_GridBody_std(event)" onmouseout="so('g1643');onOut_GridBody_std(event)" onclick="so('g1643')"  cellpadding="2" cellspacing="0" class="gBodyLines_std">
										<colgroup>
										<col />
										<col width="190" />
                                        <%while ( tmpls.next() ){%>
										<tr><td class="gCell_std" onclick='winmain().openDoc("medium","<%=((ObjAttHandler)tmpls.getObject().getAttribute("masterObjectClass")).getObject().getAttribute("name").getValueString()%>","edit","method=newfromtemplate&parent_boui=<%=tmpls.getObject().getBoui()%>");winmain().ndl[getIDX()].close();' ><%=tmpls.getObject().getAttribute("name").getValueString()%> </td></tr>
                                        <%}%>
									</table>   <!--END TABLE BODY  !-->
								</div> <!--END TABLE BODY CONTAINER  !-->
						   </td>
					   </tr>
					</table>
				</div>	
                
                
                <%}%>
				
		       
			   
		  	</td>
			</tr>
		</table>
			    
     
    <!-- END LIST OF TEMPLATES -->
     </tr></table>
	
   
	
    </td>
    
  </tr>
</table>


</body>

</html>
<%
} finally {
    if(initPage) {if (boctx!=null )boctx.close();if (boctx!=null && DOCLIST!=null)DOCLIST.releseObjects(boctx);}
}
%>
