//<SCRIPT>
function elements_afterRenderCell( node , c )
{

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
				toRet = node.selectSingleNode("label").text;
				break;
			}
	
		}
	return toRet;

}



function elements_GetNodeToAppendChild( nodeName )
{
	
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
	if ( nodeName == 'TREE' || nodeName == 'OPTION' || nodeName =='OPTIONLINK' || nodeName=='OPTIONFOLDER' ||
		 nodeName=='OPTIONOBJECT' )
	{
	   return true;
	}
	return false;
}


//------------

function winmain(){
	var Win=window;
	while (!Win.openDoc){
	   if( Win==Win.parent )  return Win;
	   Win=Win.parent;
		  
	}
	return Win;	
	
}
var xxx=true;
function elements_select( sid )
{

	//winmain().openDocUrl("","__explorer.jsp","?objectName=xwfActivity");
	
	var htm=[];
	var varHTML=[];
	var j=0;
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var x_label = node.selectSingleNode("label").text;
	
	if ( node.nodeName == "option" )
	{	
		var onclick = node.selectSingleNode("onclick").text;
		try
		{
		eval(onclick);
		}
		catch(e)
		{
			alert(e.message);
		}
	}
	else if ( node.nodeName == "optionLink" )
	{	
		var url = node.selectSingleNode("url").text;
		
		var url1= url;
		var url2= ""
		if( url.indexOf("?")> -1 )
		{
			url1=url.substr(0,url.indexOf("?"));
			
			url2=url.substr(url.indexOf("?")+1);
			
		}
		if( url1 && url1.length>0 )
		{
			winmain().openDocUrl("externalLink=true",url1,"?"+url2,null,null,x_label );
		}
		else
		{
			alert("URL não definido");
		}
		
	}
	else if ( node.nodeName == "optionObject" )
	{
		var mode = node.getAttribute("mode");
		var object = node.getAttribute("object");
		var filterBoui = node.getAttribute("filterBoui");
		if ( mode == "explorer" )
		{
			var filter=node.getAttribute("filterBoui");
			var form=node.getAttribute("form");
			var imagem=node.getAttribute("img");
			
		    var xopt=""
		    if ( filter && filter.length > 0)
		    {
		      xopt+="&query_boui="+filter;
		    }
		    if ( form && form.length > 0)
		    {
		      xopt+="&form="+form;
		    }
		    if(x_label && x_label.length > 0)
        {
          xopt+="&label="+encodeURIComponent(x_label);
        }
        if(imagem && imagem.length > 0)
        {
          xopt+="&imagem="+encodeURIComponent(imagem);
        }
		    
			winmain().openDocUrl("","__explorer.jsp","?objectName="+object+xopt,null,null,x_label);
		}
		else if ( mode == "list" )
		{
		    // winmain().openDoc('',object.toLowerCase(),'list','method=list&boql=SELECT '+object+' where 1=1',null,null,null,null);
		    var filter=node.getAttribute("filterBoui");
		    var xopt=""
		    var form=node.getAttribute("form");
		    if ( filter && filter.length > 1)
		    {
		      xopt+="&look_query_boui="+filter;
		    }
		    if ( form && form.length > 0)
		    {
		      xopt+="&form="+form;
		    }
		    winmain().openDocUrl("","__list.jsp","?look_object="+object+xopt,null,null,x_label);
		}
		else if ( mode == "new" )
		{
		     winmain().openDoc('',object.toLowerCase(),'edit','method=new&object='+object,null,null,x_label);
		}
		
	}		

}

function elements_unselect( sid )
{
 return true;
}

