//<script>

function dragEnterTrash()
{
	var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveSID')>-1 &&  info.substr(8).substr(0,3)!='new')
	{
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
		var o = window.event.srcElement;
		
	}
	else event.dataTransfer.effectAllowed = 'none';
}

function dragOverTrash()
{
	var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveSID')>-1 &&  info.substr(8).substr(0,3)!='new' )
	{
		if ( false )
		{
			event.dataTransfer.effectAllowed = 'none';
			window.event.returnValue=false;
			return;
		}
		else
		{
			event.dataTransfer.effectAllowed = 'move';
			window.event.returnValue=false;
		}
				
		window.event.srcElement.runtimeStyle.border='1px solid blue';
		
		//elements_GetNodeToAppendChild( nodeName )
		
	}
	else event.dataTransfer.effectAllowed = 'none';
}

function dropTrash()
{

	var info=event.dataTransfer.getData("Text");
	
	if( info&& info.indexOf('moveSID')>-1 &&  info.substr(8).substr(0,3)!='new' )
	{
		
		var SidToMove = info.substr(8);
		
  	    for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
		{
		    //unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
		    unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
		}
		//xmlNode.getAttribute("sid")
		window.event.srcElement.runtimeStyle.border='';
		var search;
		var nodeFrom;
		search="//*[@sid='"+SidToMove+"']"; //NTRIMJS
		nodeFrom=TREE_EDIT.code.firstChild.selectSingleNode(search);
		try
		{
					
			var removedElement ;
			if ( elements_canRemove( nodeFrom ) )
			{
				removedElement = nodeFrom.parentNode.removeChild( nodeFrom );
			}
			else
			{
				elements_showMessageCANNOTREMOVE( nodeFrom )
			}
				
		}
		catch(e)
		{
		  alert(e.description);
			  
		}
	    
	    TREE_EDIT.refreshHTM();
	
	}


}
function dragLeaveTrash()
{
	window.event.srcElement.runtimeStyle.border='';
}

function dragEnter()
{
	var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveSID')>-1)
	{
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
		var o = window.event.srcElement;
		
	/*			
		var oT = o;
		while (!oT.sid)
		{
			oT = oT.parentNode;
			if (oT == null) return; 	
		}
		var cells= oT.rows[0].cells;
		cells[ cells.length-1 ].runtimeStyle.borderBottom='2px solid blue';
		cells[ cells.length-2 ].runtimeStyle.borderBottom='2px solid blue';
	*/	

	
	}
	else event.dataTransfer.effectAllowed = 'none';
}


function dragLeave()
{
	var info=event.dataTransfer.getData("Text");
	
	if( info&& info.indexOf('moveSID')>-1)
	{
		var o = window.event.srcElement;
		
		var oT = o;
		while (!oT.sid)
		{
			oT = oT.parentNode;
			if (oT == null) break; 	
		}
		if ( oT )
		{
			if ( !oT.rows ) return;
		
			var cells= oT.rows[0].cells;
			cells[ cells.length-1 ].runtimeStyle.borderBottom='';
			cells[ cells.length-2 ].runtimeStyle.borderBottom='';
		}
		else
		{
			var oT = o;
			while (!oT.lineW )
			{
				oT = oT.parentNode;
				if (oT == null) return;
			}
			if ( oT.cells )
			{
				var cells= oT.cells;
				cells[ cells.length-1 ].runtimeStyle.borderBottom='1px solid #C1D1E9';
				//cells[ cells.length-1 ].className="cell";
			}
		}
	
	}
}

function dragOver()
{
	var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveSID')>-1)
	{
		
    
		var o = window.event.srcElement;
		
		var oT = o;
		while (!oT.sid )
		{
			oT = oT.parentNode;
			if (oT == null) break; 	
		}
		
		if ( oT != null )
		{
		
			search="//*[@sid='"+oT.sid+"']"; //NTRIMJS
			var nodeTo=TREE_EDIT.code.firstChild.selectSingleNode(search);
			oT_parent = nodeTo.parentNode.nodeName;
			if ( oT_parent == "code")
			{
				oT_parent = nodeTo.parentNode.parentNode.nodeName;
			}
		
			var SidToMove = info.substr(8);
			var movingNodeName ="";
			if ( SidToMove.substr(0,3)=='new')
			{
				movingNodeName = SidToMove.substr(3);
			}
			else
			{
				var search="//*[@sid='"+SidToMove+"']"; //NTRIMJS
				var nodeFrom=TREE_EDIT.code.firstChild.selectSingleNode(search);
				if ( nodeFrom ) movingNodeName = nodeFrom.nodeName;
			}
		
			var acceptThisChild= elements_AcceptChild( oT_parent, movingNodeName );
		
		
		
			if ( !acceptThisChild )
			{
				event.dataTransfer.effectAllowed = 'none';
				window.event.returnValue=false;
				return;
			}
			else
			{
				event.dataTransfer.effectAllowed = 'move';
				window.event.returnValue=false;
			}
		
			var acceptChilds = elements_GetNodeToAppendChild( oT.nodeXML ) != null;
		
			if ( oT.rows )
			{
				var cells= oT.rows[0].cells;
				cells[ cells.length-1 ].runtimeStyle.borderBottom='2px solid blue';
				if (!cells[ cells.length-1 ].contains(o) || !acceptChilds )
				{
					cells[ cells.length-2 ].runtimeStyle.borderBottom='2px solid blue';
				}
			}
			
			
		}
		else
		{
		  //está over uma linha vazia
			event.dataTransfer.effectAllowed = 'move';
			
			window.event.returnValue=false;
			var o = window.event.srcElement;
			var oT = o;
			while (!oT.lineW )
			{
				oT = oT.parentNode;
				if (oT == null) return;
			}
			if ( oT.cells )
			{
				var cells= oT.cells;
				cells[ cells.length-1 ].runtimeStyle.borderBottom='2px solid blue';
			}
			
		}
		
		//elements_GetNodeToAppendChild( nodeName )
		
	}
	else event.dataTransfer.effectAllowed = 'none';
}

function drop()
{

	var info=event.dataTransfer.getData("Text");
	
	if( info&& info.indexOf('moveSID')>-1)
	{
		var o = window.event.srcElement;
		var oT = o;
		while (!oT.sid)
		{
			oT = oT.parentNode;
			if (oT == null) break; 	
		}
		if ( oT!= null )
		{
			var cells= oT.rows[0].cells;
		
			cells[ cells.length-1 ].runtimeStyle.borderBottom='';
			cells[ cells.length-2 ].runtimeStyle.borderBottom='';
		
		
			var mySid=getSID(o);
		}
		else
		{
		
			var oT = o;
			while (!oT.lineW )
			{
				oT = oT.parentNode;
				if (oT == null) return;
			}
			if ( oT.cells )
			{
				var cells= oT.cells;
				cells[ cells.length-1 ].runtimeStyle.borderBottom='1px solid #C1D1E9';
			}
			oT=null;
			var mySid = null;
			try
			{		
			mySid = TREE_EDIT.code.firstChild.getAttribute("sid");
			}
			catch(e)
			{
				mySid = TREE_EDIT.code.childNodes(1).getAttribute("sid");
			}
		}
		var SidToMove = info.substr(8);
		
		//xmlNode.getAttribute("sid")
		var search;
		var nodeFrom;
		var newNode = false;
		
		
		var appendedNode=null;
		search="//*[@sid='"+mySid+"']"; //NTRIMJS
		var nodeTo=TREE_EDIT.code.firstChild.selectSingleNode(search);
		
		
		if ( SidToMove.substr(0,3)=='new')
		{
			nodeFrom = elements_getXML( TREE_EDIT.code , SidToMove );
			newNode = true;
			var lineto = parseInt( nodeTo.getAttribute("source"),10 );
			if(oT)	TREE_EDIT.addRow(lineto);
			
			
		}
		else
		{
			search="//*[@sid='"+SidToMove+"']"; //NTRIMJS
			nodeFrom=TREE_EDIT.code.firstChild.selectSingleNode(search);
		}
		
		
		
		var insertChild = false;
		var acceptChilds = true;
		if( oT )
		{
			var acceptChilds = elements_GetNodeToAppendChild( oT.nodeXML ) != null
		
			if (cells[ cells.length-1 ].contains(o) && acceptChilds)
			{
				insertChild = true;
			}
		}
		else
		{
			insertChild = true;
		}
		

		try
		{
		window.status='initDrop'
			
			for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
			{
				//unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
				unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
			}
				
			if ( !insertChild )
			{
				if ( nodeTo.nextSibling )
				{
					nodeTo.parentNode.insertBefore(  nodeFrom , nodeTo.nextSibling );
				}
				else
				{
					nodeTo.parentNode.appendChild(  nodeFrom );
					//nodeTo.parentNode.insertBefore(  removedElement , nodeTo );
				}
				appendedNode=nodeFrom;
			}
			else
			{
					
					
				var name = nodeTo.nodeName.toUpperCase();
					
					
					
				var  nodeToAppend = elements_GetNodeToAppendChild( name );
					
				if ( nodeToAppend != null )
				{
					
					var removedElement ;
					if ( !newNode )
					{
						removedElement = nodeFrom.parentNode.removeChild( nodeFrom );
					}
					else
					{
						removedElement = nodeFrom
					}
						
					//abrir no se estiver fechado
						
					var ele = window.document.getElementById("sid"+nodeTo.getAttribute("sid"));
					var xlevel = parseInt(ele.level,10);
					if ( ele.rows[0].cells[xlevel-1].firstChild.firstChild.src.indexOf('-close')>-1 )
					{
						//TREE_EDIT.toggle( ele );
					}
						
							
					if ( nodeToAppend.length > 0 )
					{
						var code = nodeTo.selectSingleNode( nodeToAppend );
						code.appendChild(removedElement);
						TREE_EDIT.markToOpenNode( nodeTo );
						//code.setAttribute("gui_open","true");
					}
					else
					{
						nodeTo.appendChild( removedElement );
						TREE_EDIT.markToOpenNode( nodeTo );
						//nodeTo.setAttribute("gui_open","true");
							
					}
						
					appendedNode=removedElement;
				}
					
				
			}
				
				
		}
		catch(e)
		{
		  alert(e.description);
			  
		}
	    
		window.status='gotoRefresh'
	    
	    TREE_EDIT.refreshHTM();
	    
	    if ( appendedNode )
	    {
			var ele = window.document.getElementById("sid"+appendedNode.getAttribute("sid"))
			selectRow(ele);
	    }

	
	}

}


function startDragImg()
{
	
	var sid = getSID(window.event.srcElement);
	if ( sid != null )
	{
		event.dataTransfer.setData('Text','moveSID:'+sid);
		event.dataTransfer.effectAllowed = 'move';
	}
	
}


function startDragNew( type )
{
	var sid = 'new'+type;
	if ( sid != null )
	{
		event.dataTransfer.setData('Text','moveSID:'+sid);
		event.dataTransfer.effectAllowed = 'move';
	}
	
}
