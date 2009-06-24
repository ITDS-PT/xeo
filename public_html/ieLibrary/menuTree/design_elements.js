//<SCRIPT>
function elements_afterRenderCell( node , c )
{

}

function elements_canRemove( node )
{
	return true;
}
function elements_showMessageCANNOTREMOVE( node )
{
}

function newElement(eleName , text  , xml , booleanCdata )
{
	var ele = xml.createElement( eleName );
	if ( booleanCdata )
	{
		var txte = xml.createCDATASection( text );
	}
	else
	{
		var txte = xml.createTextNode( text );
	}
	ele.appendChild(txte);
	return ele;
}

function getCdataNode( node , nodeName )
{
  var node1 = node.selectSingleNode( nodeName );
  if ( node1.firstChild )
  {
	return node1.firstChild;
  }
  else
  {
	var cdata	= xmlSrc.createCDATASection("");
	node1.appendChild(cdata);
	return node1.firstChild;
  }
}

function elements_getXML( xml , type )
{
	var xmlt=[];
	var toRet = null;
	var i=0;
	var newSid = GUIDGen();
	var otype=type;
	var type = type.toUpperCase();
	var object="";
	
	if ( type.substr(0,10) =='NEWOPTOBJ:' )
	{
		object = otype.substr(10);
		type='NEWOPTIONOBJECT';
	}
	
	if ( type =='NEWOPTION')
	{
		
		toRet = xml.createElement( "option" );
		toRet.appendChild( newElement("onclick"," " , xml , true ));
		toRet.appendChild( newElement("description"," " , xml , true ));
		toRet.appendChild( newElement("label","Opção" , xml , true ));
		toRet.setAttribute("gui_execute","false");
		toRet.setAttribute("name","s/nome");
		toRet.setAttribute("img","resources/item.gif");
		toRet.appendChild(xml.createElement("childs"));

	}
	else if ( type =='NEWOPTIONOBJECT')
	{
	
		toRet = xml.createElement( "optionObject" );
		toRet.appendChild( newElement("description"," " , xml , true ));
		toRet.appendChild( newElement("label","Opção" , xml , true ));
		toRet.setAttribute("gui_execute","false");
		toRet.setAttribute("object",object);
		toRet.setAttribute("mode","explorer");
		toRet.setAttribute("filterBoui","");
		toRet.setAttribute("form","");
		toRet.setAttribute("name",object);
		toRet.setAttribute("img","resources/"+object+"/ico16.gif");
		toRet.appendChild(xml.createElement("childs"));
	}
	else if ( type =='NEWOPTIONLINK')
	{
		toRet = xml.createElement( "optionLink" );
		toRet.appendChild( newElement("url"," " , xml , true ));
		toRet.appendChild( newElement("description"," " , xml , true ));
		toRet.appendChild( newElement("label","Página" , xml , true ));
		toRet.setAttribute("gui_execute","false");
		toRet.setAttribute("name","s/nome");
		toRet.setAttribute("img","resources/link.gif");
	}
	else if ( type =='NEWOPTIONFOLDER')
	{
		toRet = xml.createElement( "optionFolder" );
		toRet.appendChild( newElement("label","Pasta" , xml , true ));
		toRet.appendChild( newElement("description"," " , xml , true ));
		toRet.setAttribute("gui_execute","false");
		toRet.setAttribute("img","resources/folder.gif");
		toRet.setAttribute("name","s/nome");
		toRet.appendChild(xml.createElement("childs"));
		
	}
	toRet.setAttribute('sid', newSid );
	return toRet;
}

function elements_getImg( node )
{
	if ( node.getAttribute("img") )
	{
	return node.getAttribute("img");
	}
	else
	{
	return node.nodeName.toLowerCase()+".gif";
	}
}

function elements_getHtml( node )
{
	var nodeName=node.nodeName.toUpperCase();
	
	var toRet = null;

	
	switch ( nodeName )
		{
			
			case 'TREE':
			{
				
				toRet = node.selectSingleNode("description").text;
				break;
			}
			case 'OPTION':
			{
				
				toRet = node.selectSingleNode("label").text;
				break;
			}
			case 'OPTIONLINK':
			{
				
				toRet = node.selectSingleNode("label").text;
				break;
			}
			case 'OPTIONFOLDER':
			{
				
				toRet = node.selectSingleNode("label").text;
				break;
			}
			case 'OPTIONOBJECT':
			{
				toRet = node.selectSingleNode("label").text+" "+node.getAttribute("object");
				break;
			}
	
		}
	return toRet;

}



function elements_GetNodeToAppendChild( nodeName )
{
	if ( !nodeName ) return null;
	var name=nodeName.toUpperCase();
	if ( name == "OPTION" || name=="OPTIONOBJECT" || name=="OPTIONFOLDER" )
	{
		return "childs";	
	}
	else if ( name == "TREE" )
	{
		return "";
	}
	return null
}

function elements_AcceptChild( parentNodeName , childNodeName )
{
	return true;
}
 
function  elements_haveFixedChilds( node )
{
	if ( node.selectSingleNode("childs") && 
			node.selectSingleNode("childs").childNodes.length > 0 )
		{
			return true;
		}
	
	return false;
}
 
function elements_isNodeToRender( nodeName )
{
	nodeName=nodeName.toUpperCase();
		
	if ( nodeName == 'OPTION' || nodeName == "TREE" ||
		 nodeName=='OPTIONOBJECT' ||nodeName=="OPTIONFOLDER" || nodeName=="OPTIONLINK" )
	{
	   return true;
	}
	
	return false;
}


//------------


function elements_select( sid )
{
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	if ( node )
	{
	     var nodeName = node.nodeName.toUpperCase();
	     workCell.innerHTML="&nbsp";
	     if ( nodeName == "OPTION" || nodeName == "OPTIONOBJECT" || nodeName == "OPTIONFOLDER" || nodeName == "OPTIONLINK")
	     {
			elements_showOPTION( node );
	     }
	     
	     
	     
	}
	else
	{
	//	alert("ERRO !!!")	
	}

}

function elements_unselect( sid )
{
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var toRet = true;
	if ( node )
	{
	     var nodeName = node.nodeName.toUpperCase();
	     
		if ( nodeName == "OPTION" || nodeName == "OPTIONOBJECT" || nodeName == "OPTIONFOLDER" || nodeName == "OPTIONLINK")
	     {
			var toRet =elements_closeOPTION( node );
	     } 
	     
	}
	else
	{
		toRet=true;
	}
	if ( toRet )
	{
	workCell.innerHTML="&nbsp";
	}
   return toRet;
}

function changeAtr()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.lastIndexOf("_")+1 );
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	if ( nodeName == "OPTION"  || nodeName == "OPTIONOBJECT" || nodeName=="OPTIONLINK" || nodeName=="OPTIONFOLDER")
	{
		readFromHTML_OPTION( node )
	}
}

function getValue( oid )
{
	var iLen = wForm.elements.length;
	for (i = 0; i < iLen; i++)
	{
		o = wForm.elements[i];
		if ( o.name==oid )
		{
			if (o.type == "text" || o.type == "textarea")
			{
				o.value = Trim(o.value);
			}
			if( o.returnValue) return o.returnValue;
			else return o.value;
		}
	}
	
}




/************************   OPTION  **********************************/


function elements_showOPTION( node )
{
	var htm=[];
	var varHTML=[];
	var j=0;
	var i=0;
	
	var modeExt=["Explorer","Lista","Novo"] ;
	var modeInt=["explorer","list","new"] ;
	
	var sid = node.getAttribute("sid");

	var name = node.getAttribute("name");
	var img = node.getAttribute("img");
	var label = node.selectSingleNode("label").text;
	var description = node.selectSingleNode("description").text;

	var nameHTML=createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);

	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000);
	var imgHTML=createFieldText( img , "img_"+sid,"img_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);	
	
	
	if ( node.nodeName == "option" )
	{	
		var onclick = node.selectSingleNode("onclick").text;
		var onclickHTML = createFieldText( onclick , "onclick_"+sid,"onclick_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
		
		//Execute true or false
   	var guiExecExt=["Sim","Não"] ;
   	var guiExecInt=["true","false"] ;
   	var guiExec = node.getAttribute("gui_execute");
   	if(guiExec == null)
   	{
   	  guiExec = "false";
   	}
   	var guiExecHTML= createFieldCombo(guiExec,"guiExec_"+sid,"guiExec_"+sid,"1", guiExecExt , guiExecInt ,false,false,"changeAtr" );
	}
	else if ( node.nodeName == "optionLink" )
	{	
		var url = node.selectSingleNode("url").text;
		var urlHTML = createFieldText( url , "url_"+sid,"url_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
		
		//Execute true or false
   	var guiExecExt=["Sim","Não"] ;
   	var guiExecInt=["true","false"] ;
   	var guiExec = node.getAttribute("gui_execute");
   	if(guiExec == null)
   	{
   	  guiExec = "false";
   	}
   	var guiExecHTML= createFieldCombo(guiExec,"guiExec_"+sid,"guiExec_"+sid,"1", guiExecExt , guiExecInt ,false,false,"changeAtr" );
		
	}
	else if ( node.nodeName == "optionObject" )
	{
		var mode = node.getAttribute("mode");
		var object = node.getAttribute("object");
		var filterBoui = node.getAttribute("filterBoui");
		var form = node.getAttribute("form");
		if ( form == null ) form="";
		
		//Execute true or false
   	var guiExecExt=["Sim","Não"] ;
   	var guiExecInt=["true","false"] ;
   	var guiExec = node.getAttribute("gui_execute");
   	if(guiExec == null)
   	{
   	  guiExec = "false";
   	}
   	var guiExecHTML= createFieldCombo(guiExec,"guiExec_"+sid,"guiExec_"+sid,"1", guiExecExt , guiExecInt ,false,false,"changeAtr" );
		
		var formHTML = createFieldText( form , "form_"+sid,"form_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
		var modeHTML= createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", modeExt , modeInt ,false,false,"changeAtr" );
		
		var filterBouiHTML = createDetachFieldLookup(
        filterBoui,
        "filterBoui_"+sid,
        "filterBoui_"+sid,
        "Ebo_Filter",
        "Ebo_Filter",
        "Filtro",
        getDocId(), //docid
        "single", //single or multi
        1,
        false, //isdisable
        true,null,"select Ebo_Filter where masterObjectClass="+systemObjects[object]
        )
        
        
	}		
	
        
        
	htm[i++]="<table id='readSection' cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"

/*	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML;
	htm[i++]="</td></tr>";
*/	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
		
	htm[i++]="<tr HEIGHT=100PX><td valign=top>"
	htm[i++]="Descrição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=descriptionHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr ><td valign=top>"
	htm[i++]="Imagem";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= imgHTML;
	htm[i++]="</td></tr>";
	
	if ( node.nodeName == "option" )
	{	
		htm[i++]="<tr ><td valign=top>"
   	htm[i++]="Executar ao abrir";
   	htm[i++]="</td>";
   	htm[i++]="<td>";
   	htm[i++]= guiExecHTML;
   	htm[i++]="</td></tr>";

		htm[i++]="<tr ><td valign=top>"
		htm[i++]="onClick";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=onclickHTML;
		htm[i++]="</td></tr>";
	}
	else if ( node.nodeName == "optionLink" )
	{	
		htm[i++]="<tr ><td valign=top>"
   	htm[i++]="Executar ao abrir";
   	htm[i++]="</td>";
   	htm[i++]="<td>";
   	htm[i++]= guiExecHTML;
   	htm[i++]="</td></tr>";		

		htm[i++]="<tr ><td valign=top>"
		htm[i++]="Url";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=urlHTML;
		htm[i++]="</td></tr>";
	}
	else if ( node.nodeName == "optionObject" )
	{
		htm[i++]="<tr ><td valign=top>"
   	htm[i++]="Executar ao abrir";
   	htm[i++]="</td>";
   	htm[i++]="<td>";
   	htm[i++]= guiExecHTML;
   	htm[i++]="</td></tr>";
   		  
		htm[i++]="<tr><td>"
		htm[i++]="Filtro";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]= filterBouiHTML;
		htm[i++]="</td></tr>";
			
		htm[i++]="<tr><td>"
		htm[i++]="Modo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=modeHTML;
		htm[i++]="</td></tr>";
		htm[i++]="</table>";	
		
		htm[i++]="<tr><td>"
		htm[i++]="Form";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=formHTML;
		htm[i++]="</td></tr>";
		htm[i++]="</table>";	
	}		
	
	
	
	workCell.innerHTML= htm.join("");

	document.getElementById( "label_"+sid ).focus();		
	
}

function readFromHTML_OPTION( node )
{
	
	var sid = node.getAttribute("sid");
	var label = getValue( "label_"+sid );
	var description = getValue( "description_"+sid );
	var name = getValue( "name_"+sid );	
	
	
	getCdataNode( node , "description" ).text = description;
	getCdataNode( node , "label" ).text = label;
	
	node.setAttribute("name",name);
	var img = getValue( "img_"+sid );
	node.setAttribute("img",img );
		
	if ( node.nodeName == "option" )
	{
		
		var onclick = getValue( "onclick_"+sid );
		getCdataNode( node , "onclick" ).text = onclick;
		
		var guiExec = getValue( "guiExec_"+sid );
	  node.setAttribute("gui_execute",guiExec);
	}
	if ( node.nodeName == "optionLink" )
	{
		
		var url = getValue( "url_"+sid );
		getCdataNode( node , "url" ).text = url;
		//node.selectSingleNode("url").firstChild.text=url;
		var guiExec = getValue( "guiExec_"+sid );
	  node.setAttribute("gui_execute",guiExec);		
	}
	else if ( node.nodeName == "optionObject" )
	{
		var mode = getValue( "mode_"+sid );
		var form = getValue( "form_"+sid );
		//var objBoui = getValue("objName_"+sid);
		filterBoui=getValue( "filterBoui_"+sid );
		var objectName = "";
	
	/*
		for( var i in systemObjects )
		{
			if( objBoui == systemObjects[i] )
			{
			 objectName = i;
			 break;
			}
		}
		*/
			node.setAttribute("mode",mode);
			node.setAttribute("form",form);
			node.setAttribute("filterBoui",filterBoui);
			//node.setAttribute("object",objectName);
		var guiExec = getValue( "guiExec_"+sid );
	  node.setAttribute("gui_execute",guiExec);

	}
	
		
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node ); //actualiza viwer
	
	return true;
}


function elements_closeOPTION( node )
{
	readFromHTML_OPTION( node )
	return true;

}

/************************  END DEFVARIABLE ************************/

