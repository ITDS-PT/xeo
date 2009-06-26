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
   // docParameter attr;
    ctrl= DOCLIST.processRequest(boctx);
    IDX     = ClassUtils.convertToInt(ctrl[0],-1);
    idbolist=ctrl[1];
    docHTML DOC = DOCLIST.getDOC(IDX);
    // leitura de parametros
    // especificos do Lookup
    
    String par1                 = request.getParameter("attributeName");
    String clientIDX            = request.getParameter("clientIDX");
    String templateBOUI         = request.getParameter("templateBOUI");
    String valueToSave          = request.getParameter("submitXml");
    
    String[] par=netgest.utils.ClassUtils.splitToArray(par1,"__");
    String objClass     =   par[0];
    long boui_in_edit   =   netgest.utils.ClassUtils.convertToLong(par[1],0);
    String attributeName=   par[2];
    //DOC.getObject()
    //netgest.bo.runtime.boObjectList OBJL=netgest.bo.runtime.boObjectList.edit(boctx,,boui_in_edit);
    
    boObject OBJ=DOC.getObject( boui_in_edit );
    boObject templateOBJ=DOC.getObject( netgest.utils.ClassUtils.convertToLong(templateBOUI) );
    AttributeHandler AttrEdit=OBJ.getAttribute(attributeName);
    boDefAttribute AttrDef=AttrEdit.getDefAttribute();
    bridgeHandler details=templateOBJ.getBridge("mappingAttributes");
    details.beforeFirst();
    String fTransform="";
    
    netgest.bo.runtime.boObject objDetails;
    if ( valueToSave !=null ){
        boolean find=false;
        while ( details.next() ){
             objDetails=details.getObject();
             if (objDetails.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(attributeName)) {
                find=true;
                details.edit();
                objDetails.getAttribute("transformation").setValueString(valueToSave);
                //objDetails.update();
             }
        }
        if (!find){
            //netgest.bo.runtime.boObject templateDetails=netgest.bo.runtime.boObject.getBoManager().createObject(DOC.getEboContext(),"Ebo_Map");
            details.addNewObject();
            netgest.bo.runtime.boObject templateDetails = details.edit().getObject();
            templateDetails.getAttribute("transformation").setValueString(valueToSave);
            templateDetails.getAttribute("objectAttributeName").setValueString(attributeName);
            //templateDetails.update();
            //details.add( templateDetails.bo_boui );
        }
    }
    details.beforeFirst();
    while ( details.next() ){
         objDetails=details.getObject();
         if (objDetails.getAttribute("objectAttributeName").getValueString().equalsIgnoreCase(attributeName)) {
            
            fTransform=objDetails.getAttribute("transformation").getValueString();
         }
    }
    
    bridgeHandler fromObject=templateOBJ.getBridge("fromObjectClass");

    
    fromObject.beforeFirst();
    boDefHandler[] fromObjectsDef= new boDefHandler[ fromObject.getRowCount() ];
    int nrFrom=0;
    boObject oCls;
    while (fromObject.next())
    {
        oCls=fromObject.getObject();
        if ( oCls!= null && oCls.exists() )
        {
            fromObjectsDef[nrFrom++] = boDefHandler.getBoDefinition( oCls.getAttribute("name").getValueString() );
        }
    }
    if ( fromObjectsDef.length ==0 || fromObjectsDef[0]==null){
        fromObjectsDef= new boDefHandler[ 1 ];
        fromObjectsDef[0] = OBJ.getBoDefinition();
    }
    %>

<%@ include file='boheaders.jsp'%>


<script LANGUAGE="javascript" SRC="templates/form/std/formulaEditor.js"></script>
<style type="text/css">
    @import url('templates/form/std/formulaEditor.css');
</style>
<script LANGUAGE="javascript" >
var _hsos=[];
var _hso=null;


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




formula="<%=fTransform%>";
var xmlFormula = new ActiveXObject("MSXML.DOMDocument");

function ini(){
  if(formula=="") formula="<none/>";  
  xmlFormula.loadXML(formula); 
 if( xmlFormula.parseError!=0){
  alert("Parser Error");
  return
 }
 var childs=xmlFormula.childNodes(0).childNodes;
 for ( var i=0; i<  childs.length ;i++){
	var xtype=childs[i].nodeName;
	var xValue=childs[i].text;
	if(xtype == "par" ){
		addParentises(xValue);
	}
	else if(xtype == "atr" ){
		var x=xValue.split('.');
		var x1=childs[i].getAttribute("external").split('.');
		addAttribute(x[0],x[1],x1[0],x1[1]);
	}
	else if(xtype == "oper" ){
		addSinal(xValue);
	}
	else if(xtype == "dateLiteral" ){
		addDateLiteral(xValue);
	}
	else if(xtype == "number" ){
		addNumber(xValue);
	}
	else if(xtype == "text" ){
		addText(xValue);
	}
 }


}

function compoe_formula(fClose){
  var divFormula=document.getElementById("formula");
  var aS = divFormula.getElementsByTagName("a");
  var xf=""
  for ( var i=0; i<aS.length ; i++) {
	 //alert(aS[i].innerText+aS[i].className+aS[i].internalv);
     if( aS[i].className=="formula_attribute"){
      xf+="<atr external='"+aS[i].innerText+"'>"+aS[i].internalv+"</atr>";
     }
     else if ( aS[i].className=="formula_sinal"){
        xf+="<oper>"+aS[i].innerText+"</oper>";
     }
     else if ( aS[i].className=="formula_parentises"){
        xf+="<par>"+aS[i].innerText+"</par>";
     }
     else if ( aS[i].className=="formula_parentises"){
        xf+="<par>"+aS[i].innerText+"</par>";
     }
     else if ( aS[i].className=="formula_dateLiteral"){
        xf+="<dateLiteral>"+aS[i].innerText+"</dateLiteral>";
     }
     else if ( aS[i].className=="formula_number"){
        xf+="<number>"+aS[i].innerText+"</number>";
     }
     else if ( aS[i].className=="formula_text"){
        xf+="<text>"+aS[i].innerText+"</text>";
     }
	}
    boFormSubmit.submitXml.value="<formula>"+xf+"</formula>";
    if (fClose) createHiddenInput( "toClose", "y" );
    boFormSubmit.submit();

//formula="<formula><par>(</par><atr external='Solicitaçao.Data Inicio'>rs.beginDate</atr><oper>+</oper><dateLiteral>10days</dateLiteral></formula>";
}

function cleanFormula(){
 var divFormula=document.getElementById("formula");
 divFormula.innerHTML='<img id="sepFinal" type="sep" ondragstart="cancelDrag()" ondrop="movePointer()" ondragenter="enterMovePointerTo()" ondragover="overMovePointerTo()" ondragleave="leaveMovePointerTo()" src="templates/form/std/formulaEditor/sep.gif" style="position:relative;top:5;border:0;" / WIDTH="6" HEIGHT="15" /><img id="pointer" ondragstart="setInfoPointer()"  style="cursor:hand;position:relative;top:5" border="0" src="templates/form/std/formulaEditor/position.gif" width="9" height="7">'
}

function addRowAttribute(){

	var e = getElement(event);
	var o = getElement(event);
    var oTR = o;
    var mode=_hso.hb.mode;

	while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	
addAttribute(oTR.cells[1].internalv,oTR.cells[0].internalv,oTR.cells[1].innerText,oTR.cells[0].innerText);

}
var objLabel='<span style="color:#CCCCCC">Attributo </span><b><%=AttrDef.getLabel()%></b>';var objDescription='';
</script>

</head>

<body onload="ini()">


<table style='background-color:#FFFFFF' class="layout" cellpadding=0 cellspacing=0 border="0" width="100%">
  <col width=30 />
  <col /> 
 
  <tr height="20%">
    <td colspan='2' height width="100%" style>
   		<div id="formula" style="height:100%;background-color:#FFFFFF">

		<img id="sepFinal" type="sep" ondragstart="cancelDrag()" ondrop="movePointer()" ondragenter="enterMovePointerTo()" ondragover="overMovePointerTo()" ondragleave="leaveMovePointerTo()" src="templates/form/std/formulaEditor/sep.gif" style="position:relative;top:5;border:0;" / WIDTH="6" HEIGHT="15" /><img id="pointer" ondragstart="setInfoPointer()"  style="cursor:hand;position:relative;top:5" border="0" src="templates/form/std/formulaEditor/position.gif" width="9" height="7">
		
		
		</div>
    </td>
  </tr>
  <tr height="25">
   <td style="background-color:#4B5975" colspan='2' width="100%">
		<table class="mnubarFlat" id="mnuBar2" cellSpacing="0" cellPadding="0">
		      <tbody>
		      <tr>
		        <td width="9"><img hspace="3" src="templates/menu/std/mnu_vSpacer.gif" WIDTH="5" HEIGHT="18"></td>
		        <td class="icMenu" noWrap>
					<span class="menuFlat" title="Guardar Formula" onclick="compoe_formula();" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_save.gif" WIDTH="16" HEIGHT="16"> Guardar Formula</span>
					<span class="menuFlat" title="Guardar e fechar" onclick="compoe_formula(true);" tabIndex="0"><img class="mnuBtn" src="templates/menu/std/16_saveClose.gif" WIDTH="16" HEIGHT="16"> </span>
					
					<img src="templates/menu/std/mnu_hSpacer.gif" WIDTH="2" HEIGHT="17">
					<span class="menuFlat" title="Forward" onclick="cleanFormula() " tabIndex="0">Limpar</span>
					
				  </td>
				  <td class="icMenu mnuRight" noWrap>
					<span class="menuFlat" title="Remover" tabIndex="0"><img class="mnuBtn" src="templates/form/std/formulaEditor/recycle.gif" WIDTH="16" HEIGHT="16" 
					ondrop="removeItemFromFormula()"
					ondragenter="enterRecycleBin()" ondragover="overRecycleBin()"></span>
					
				  </td>
 			   </tr></tbody></table>
    </td>
  </tr>
  
  <tr height=80%>
   <td valign=top class="headerNumber"><span class="headerNumber">1</span></td>
   <td>
   <table cellpadding=0 cellspacing=0 height=100% width=100%>
        <tr height=25><td><span height='25' class="headerChoice">Atributos</span> </td>
		<tr><td>
		
		
     <!--BEGIN LIST OF ATTRIBUTES -->
    
		<table cellSpacing="0" cellPadding="0&quot;" style="height:100%;width:100%;table-layout:fixed;">
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
									<col width="188" />
									<col width="2" />
								    <col width="15" />
 								    <tbody>
										<tr>
											<td id="g$ExpanderParent" class="gh_std">Atributo</td>
											<td id="g$AutoExpander" class="gh_std">&nbsp;</td>
											<td class="ghSep_std">&nbsp;</td>
											<td class="ghSort_std">Objecto</td>
											<td class="ghSep_std">&nbsp;</td>
										    <td class="gh_std" width="14"><img src="templates/grid/std/ghRefresh.gif" width="13" height="13" /></td>
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
										<col width="190" />
                                        <%
                                        //tratar os pais
                                        //boDefAttribute[] ;
                                        Vector attrToRender=new Vector();  
                                        boDefHandler xDef=null;
                                        
                                        boolean exists;
                                        if ( fromObjectsDef.length > 0 && fromObjectsDef[0] != null ) 
                                        {
                                            String xname=fromObjectsDef[0].getName();
                                            
                                            while ( true )
                                            {
                                               String xx= fromObjectsDef[0].getBoSuperBo();
                                               if ( xx!= null )
                                               {
                                                    xname=xx;
                                                    fromObjectsDef[0]=netgest.bo.def.boDefHandler.getBoDefinition( xname );
                                               }
                                               else
                                               {
                                                 break;
                                               }
                                            }                                            
                                            xDef = netgest.bo.def.boDefHandler.getBoDefinition( xname );
                                            
                                            netgest.bo.def.boDefAttribute[] attrDefs=xDef.getAttributesDef( true );
                                            
                                            for (int i = 0; i < attrDefs.length ; i++) 
                                            {
                                                exists=true;
//                                                for (int z = 1; z < fromObjectsDef.length && exists; i++) 
//                                                {
//                                                    exists=fromObjectsDef[z].hasAttribute( attrDefs[i].getName());
//                                                }
                                                if ( exists )
                                                {
                                                    attrToRender.add( attrDefs[i] );
                                                }
                                            }
                                    
                                          boDefAttribute xa;
                                          boolean xok;
                                          for ( int i=0; i< attrToRender.size() ;i++) {
                                             xa =(netgest.bo.def.boDefAttribute) attrToRender.get(i);
                                             if ( xa.getAtributeType()==AttrDef.getAtributeType()  )
                                             {
                                                
                                                xok =AttrDef.getAtributeType() != boDefAttribute.TYPE_OBJECTATTRIBUTE || ( AttrDef.getAtributeType() == boDefAttribute.TYPE_OBJECTATTRIBUTE && xa.getReferencedObjectDef()!= null );
                                                if (xok){
                                            %>
										<tr>
												<td class="gCell_std" internalv="<%=xa.getName()%>"><%=xa.getLabel()%></td>
												<td class="gCell_std" internalv="<%="#PARENT#"%>">Objecto Pai</td>
										</tr>
										<%}}}}%>
			   
                                        
                                        <%
                                        //attributos do objects
                                        netgest.bo.runtime.boAttributesArray xatr=OBJ.getAttributes();
                                        java.util.Enumeration xenum=xatr.elements();
                                        netgest.bo.runtime.AttributeHandler x;
                                        
                                          while (xenum.hasMoreElements()) {
                                            x=(netgest.bo.runtime.AttributeHandler)xenum.nextElement();
                                             if ( x.getDefAttribute().getAtributeType()==AttrDef.getAtributeType()
                                                  
                                             ){
                                             %>
										<tr>
												<td class="gCell_std" internalv="<%=x.getName()%>"><%=x.getDefAttribute().getLabel()%></td>
												<td class="gCell_std" internalv="<%=OBJ.getName()%>"><%=OBJ.getBoDefinition().getLabel()%></td>
										</tr>
										<%}}%>
                                        
                                        <%
                                        //extendAttribute do template
                                        bridgeHandler bridgeHandler = OBJ.getBridge("extendAttribute");
                                        if(bridgeHandler != null)
                                        {
                                            bridgeHandler.beforeFirst();                                      
                                            boObject extAtt = null;
                                            while (bridgeHandler.next()) {
                                                extAtt = bridgeHandler.getObject();    
                                                String alias = extAtt.getAttribute("alias").getValueString();
                                                String shortAlias = extAtt.getAttribute("shortAlias").getValueString();
                                                long cadinalidade =  extAtt.getAttribute("attributeCardinal").getValueLong();
                                                 %>
    										<tr>
    												<td class="gCell_std" internalv="<%=shortAlias%>"><%=alias%></td>
    												<td class="gCell_std" internalv="<%="extendAttribute." + shortAlias%>"><%=OBJ.getBoDefinition().getLabel()%></td>
    										</tr>
    										<%           
                                                if(cadinalidade == 1)
                                                {
                                                    long attributeType = extAtt.getAttribute("attributeType").getValueLong();
                                                    if(attributeType == 0)
                                                    {
                                                        boObject detail = null;
                                                        boObject object =  DOC.getObject( extAtt.getAttribute("object").getValueLong() );
                                                        bridgeHandler bhAtr = object.getBridge("attributes");
                                                        bhAtr.beforeFirst();                                                                                
                                                        while (bhAtr.next()) {
                                                             detail = bhAtr.getObject();
                                                             String label = detail.getAttribute("description").getValueString();
                                                             if(!"".equals(label)){ %>
                                                        <tr>
                                                                <td class="gCell_std" internalv="<%=detail.getAttribute("name").getValueString()%>"><%=detail.getAttribute("description").getValueString()%></td>
                                                                <td class="gCell_std" internalv="<%="extendAttribute." + shortAlias%>"><%=alias%></td>
                                                        </tr>
                                                            <%}%>                                                                                                                        
                                                        <%}%>
                                                    <%}%>
                                                <%}%>
                                           <% }%>
                                       <% }%>
               
                                         
			   
									</table>   <!--END TABLE BODY  !-->
								</div> <!--END TABLE BODY CONTAINER  !-->
						   </td>
					   </tr>
					</table>
			   
				</div>
			   
		  	</td>
			</tr>
		</table>
			    
     
    <!-- END LIST OF ATTRIBUTES -->
     </tr></td></table>
	
    </td>
    
  </tr>
  <tr height="55">
   <td valign=top class="headerNumber"><span class="headerNumber">2</span></td>
   <td><span class="headerChoice">Constantes</span>
    <table width='100%'>
      <col width=100/>
      <col width=100/>
      <col width=100/>
      <col />
	  <tr>
	     <td>
	       <input id='text' contentEditable=true value=""  /> 
	     </td>
		  <td>
			       <span class="selectBox"  id="tipo" tabIndex="11" value="N" name="prioritycode">
			              <table style="DISPLAY: none" cellSpacing="0" cellPadding="2">
			                <tbody>
			                <tr>
			                  <td val="N">Numero</td></tr>
			                <tr>
			                  <td val="S">Cadeia de caracteres</td></tr>
			                <tr>  
			                  <td val="Dias">Dias</td></tr>
			                <tr>
			                  <td val="Horas">Horas</td></tr>
			                  
			                 </tbody></table></span>
		 </td>
		 <td><button onclick='if(tipo.returnValue=="S") addText(text.value);else if (tipo.returnValue=="Dias" || tipo.returnValue=="Horas"){ addDateLiteral(text.value+tipo.returnValue)} else addNumber(text.value+tipo.returnValue)' id=button1 name=button1>Confirma</button></td>
		 <td>&nbsp;</td>
	 </tr>
	</table>
	
	  
	</td>
  </tr>
  <tr height"55">
	<td valign=top class="headerNumber"><span class="headerNumber">3</span></td>
	<td ><span class="headerChoice">Operadores</span>
		<table width='100%'>
			<tr>
				<td>
					<img style="cursor:hand" onclick="addSinal('+')" height='30' width='30' src="templates/form/std/formulaEditor/adicao.gif" />
					<span>&nbsp;</span>
					<img style="cursor:hand" onclick="addSinal('-')" height='30' width='30' src="templates/form/std/formulaEditor/subtracao.gif" />
					<span>&nbsp;</span>
					<img style="cursor:hand" onclick="addSinal('*')" height='30' width='30' src="templates/form/std/formulaEditor/multiplicacao.gif" />
					<span>&nbsp;</span>
					<img style="cursor:hand" onclick="addSinal('/')" height='30' width='30' src="templates/form/std/formulaEditor/divisao.gif" />
					<span>&nbsp;</span>
				</td>
			</tr>
		</table>
	</td>

  </tr>
 
  <tr height"55">
	<td valign=top class="headerNumber"><span class="headerNumber">4</span></td>
	<td ><span class="headerChoice">Parentises</span>
		<table width='100%'>
			<tr>
				<td>
					<img onclick="addParentises('(')" height='30' width='30' src="templates/form/std/formulaEditor/openParentises.gif" />
					<span>&nbsp;</span>
					<img onclick="addParentises(')')" height='30' width='30' src="templates/form/std/formulaEditor/closeParentises.gif" />
					<span>&nbsp;</span>
				</td>
			</tr>
		</table>
	</td>

  </tr>
</table>


</body>

</html>


    
<FORM name='boFormSubmit'  method='get'>
    <INPUT type='hidden' name='submitXml' />
    <INPUT type='hidden' name='clientIDX' value='<%=clientIDX%>' />
    <INPUT type='hidden' name='templateBOUI' value='<%=templateBOUI%>' />
    <INPUT type='hidden' name='attributeName' value='<%=par1%>' />
    <% if( request.getParameter("toClose") != null) { %> 
    <INPUT type='hidden' name='toClose' value='y' />
    <% } %> 
    <INPUT type='hidden' name='boui_in_edit' value='<%=boui_in_edit%>' />
    <INPUT type='hidden' name='clientIDXtoClose' />
    <INPUT type='hidden' name='boFormSubmitId' />
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

