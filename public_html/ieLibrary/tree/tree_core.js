//<script>

function GUIDGen() 
{
    try
    {
       var x = ""+((new Date()) - new Date(1970,01,01 )+ Math.random() * 100000);
       x=x.replace(/\./g,"");
	       
    return x;

    }
    catch (e)
    {
    return ("error creating GUID");
    }
}


function  xwTree( e , XMLsrc  , showLineNumbers , editMode , renderFirstNode, mode )
{
	this.htm = e;
	this.code = XMLsrc;

	this.runningMode = mode;
	
	this.currentLine = 0;
	this.selectLines = [];
	
	this.startClose  = true;
	if( renderFirstNode==null || renderFirstNode+""=="undefined" )
	{
		this.renderFirstNode=true;
	}
	else
	{
		this.renderFirstNode=renderFirstNode;
	}
	
	this.showLineNumbers = showLineNumbers ;
	this.editMode = editMode ;
	
	
	if ( this.showLineNumbers )	this.ncell = 3;
	else this.ncell = 0

	var x=GUIDGen();
	
//	for(var i=0;i<100;i++) this.addRow();
	this.render( this.code , 0) ;
	
	
	//refreshTreeCounters( this );
	//alert(x);

}

function refreshTreeCounters( treeStr )
{
    var tree = eval(treeStr)
    var xCntrs = tree.code.selectNodes("//counter");
    for(var i=0;i<xCntrs.length;i++)
    {
        var opt = xCntrs[i];
        var referNode = opt.parentNode;
        if( opt.getAttribute("refer") != null )
        {
            var search="//*[@name='"+opt.getAttribute("refer")+"']"; //NTRIMJS
            referNode = tree.code.selectSingleNode( search );
        }
        var elementNode = document.getElementById( "cont"+opt.parentNode.getAttribute("sid") );
        if(elementNode != null)
        {
           if( !elementNode.origHTML ) elementNode.origHTML = elementNode.innerHTML;
           queryExplorer( referNode,elementNode );
        }
    }
}

function queryExplorer( opt,elementNode )
{
    var xmlHttp = new ActiveXObject("Microsoft.XMLHTTP");
    var xUrl = getCounterUrl( opt );
    xmlHttp.open("GET",xUrl,true);
    xmlHttp.onreadystatechange = function()
    {
        if( xmlHttp.readyState == 4 ) 
        {
            if( xmlHttp.responseXML.parseError == 0 )
            {
                try
                {
                    var allcnt = xmlHttp.responseXML.selectSingleNode("//counter").getAttribute("all");
                    elementNode.innerHTML = elementNode.origHTML + "<font class='itemTreeCounter'>&nbsp;("+allcnt+")</font>";
                }
                catch(e)
                {}
            }
        }
    }
    xmlHttp.send();
}




function getCounterUrl( node )
{
		var mode = node.getAttribute("mode");
		var object = node.getAttribute("object");
		var filterBoui = node.getAttribute("filterBoui");
        var filter=node.getAttribute("filterBoui");
        var form=node.getAttribute("form");
        var xopt=""
        if ( filter && filter.length > 0)
        {
          xopt+="&query_boui="+filter;
        }
        if ( form && form.length > 0)
        {
          xopt+="&form="+form;
        }
        return "__explorerCounter.jsp?objectName="+object+xopt;
}

xwTree.prototype.refreshHTM=function( clear )
{
	var time1 = new Date();
	if(this.htm.rows.length<60) clear=true;
	for ( var i = 0;i<this.htm.rows.length;i++ )
	{
		
		
		if ( this.showLineNumbers )
		{
			var c=this.htm.rows[i].cells[3];
		}
		else
		{
			var c=this.htm.rows[i].cells[0];
		}
		
		this.htm.rows[i].style.display='';
		if( clear)
		{
		 c.innerHTML="&nbsp;";
		 c.level = null;
		 c.sid = null;
		}
			
	}
	
	window.status="fase 1";
	this.currentLine = 0;
	this.render( this.code , 0  );
	
	for ( var i = this.currentLine  ;i<this.htm.rows.length;i++ )
	{
		
		
		if ( this.showLineNumbers )
		{
			var c=this.htm.rows[i].cells[3];
		}
		else
		{
			var c=this.htm.rows[i].cells[0];
		}
		
		this.htm.rows[i].style.display='';
		
		c.innerHTML="&nbsp;";
		c.level = null;
		c.sid = null;
			
	}
	
	window.status=(new Date()) - time1;

}

xwTree.prototype.nodeToRender=function( nodeName )
{
	return elements_isNodeToRender( nodeName );
}

xwTree.prototype.displayNode=function( node  )
{
   
   var nodeName=node.nodeName.toUpperCase();
   
   var toRet=true;
   var nodex=node;
   while ( true )
   {
		var nodex = nodex.parentNode;	
		if ( nodex.nodeName=="#document")
		{
			return true;
		}
		
		if ( this.nodeToRender( nodex.nodeName ) )
		{
			if ( this.nodeIsOpen( nodex ) && this.nodeIsVisible( nodex ) )
			{
				toRet=true;
				break;
			}
			else
			{
				toRet=false;
				break;
			}
		}
		
   }
   
   return toRet;
   
}

xwTree.prototype.renderNode=function( node , level )
{
		
		
		 if ( this.showLineNumbers )
		 {
			var c=this.htm.rows[this.currentLine-1].cells[3];
		 }
		 else
		 {
			var c=this.htm.rows[this.currentLine-1].cells[0];
		 }
		
		
			var rfh = true;
			if ( c.firstChild && c.firstChild.id )
			{
			
				if ( c.firstChild.id.substr(3) == node.getAttribute("sid") && c.firstChild.level ==""+level)
				{
					rfh=false;
				}
				
			}
				
			if ( this.displayNode( node ) )
			{
				if ( rfh )
				{
					var img = elements_getImg( node );
					var html= elements_getHtml( node );
						
					c.innerHTML = this.getHTMLNode(node , level , img , html );
				}
				node.setAttribute("gui_visible","true");
				node.setAttribute("source",this.currentLine);
				c.level = level;
				c.sid = node.getAttribute("sid");
				
				elements_afterRenderCell( node , c );
					
					
			}
			else
			{
					
				//c.innerHTML = "&nbsp;";
				c.level = level;
				c.sid = node.getAttribute("sid");
				node.setAttribute("source",this.currentLine);
				node.setAttribute("gui_visible","false")
				this.htm.rows[this.currentLine-1].style.display="none";
			}
				
            // psantos ini
	    // se o node da Ã¡rvore tiver o atributo gui_execute a true abre esse node
	    // sÃ³ funciona se o node for um objecto for um explorer, se for outro nÃ£o funciona
            if ( this.nodeIsExecute( node ) )
			{             
               var mode = node.getAttribute("mode");
			   var object = node.getAttribute("object");
		       var filterBoui = node.getAttribute("filterBoui");
		       var x_label = node.selectSingleNode("label").text;
		       var x_img = node.getAttribute("img");
		       
		       if ( mode == "explorer" ) {
			     var filter=node.getAttribute("filterBoui");
			     var form=node.getAttribute("form");
			
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
		         if(x_img && x_img.length > 0)
		         {
		            xopt+="&imagem="+encodeURIComponent(x_img);
		         }
  		    
	  		     winmain().openDocUrl("","__explorer.jsp","?objectName="+object+xopt,null,null,x_label);
			  }		
			} 
            // psantos fim				
			

}

xwTree.prototype.getHTMLNode=function( xmlNode ,level , img , nodeInnerHTML )
{
	var r=[];
	var i=0;
	
	var sid = xmlNode.getAttribute("sid");
	r[i++]='<table  id="sid';
	r[i++]=sid;
	r[i++]='" sid="';
	r[i++]=sid;
	r[i++]='" nodeXML="';
	r[i++]= xmlNode.nodeName;
	r[i++]='" level="';

	r[i++]=level;
	r[i++]='" cellSpacing="0" cellPadding="0" width="100%" border="0">'
	
	r[i++]='<tbody><tr>';

	//--level
	var xm = xmlNode;
	var ctr=[];
	var z = 0;
	var levels=0;
	for ( var j=1;j<level;j++)
	{
		xm=xm.parentNode;
		if(xm.nodeName=="code") xm=xm.parentNode;
		ctr[ z++ ]=this.isLastNode(xm);
		levels++;
	}
	z--;
    
    
	for ( var j=1;j<level;j++)
	{
		
			r[i++]='<td vAlign="top" background="';
		
			if( !ctr[ z ] )
			{
			r[i++]=TREE_TEMA_DIR+'vertLine.gif"';
			}
		
			r[i++]='"><img height="22" src="';
		
			if( ! ctr[z--] )
			{
				r[i++]=TREE_TEMA_DIR+'vertLine.gif"';
			}
			else
			{
				r[i++]=TREE_TEMA_DIR+'blank.gif"';
			}
			
			if (  j==1 && !this.renderFirstNode  )
			{
				r[i++]='width="0px"></td>';
			}
			else
			{
				r[i++]='width="16px"></td>';
			}
					
		
	}
	
	//--


	r[i++]='<td vAlign="top" background="';
	if( !this.isLastNode(xmlNode) )
	{
		r[i++]=TREE_TEMA_DIR+'vertLine.gif"';
	}
	r[i++]='"><a onclick="';
	r[i++]='javascript:clickCtrlImg()"><img  height="22" src="';

	r[i++]=this.getControlImg(xmlNode, level);
	
	r[i++]='" width="16" border="0" ></a></td>';
	
	
	
	r[i++]='<td vAlign="top"><img class="sentence" '
	
	if ( this.editMode )
	{
		r[i++]='ondragstart="startDragImg()" '; 
	}
	
	r[i++]='src="';
	
	
	if ( img.indexOf("/")> -1 )
	{
		r[i++]=img;
		r[i++]='" height=16 hspace=4 vspace=4 width=16/></td>';
	}
	else
	{
		r[i++]=WKFL_TEMA_DIR+img;
		r[i++]='" height=24 width=24/></td>';
	}
	
	
	r[i++]='<td class="itemTree" id=cont';
	r[i++]=sid
	r[i++]=' vAlign="center" width="100%">'
	r[i++]= nodeInnerHTML
	r[i++]='</td></tr></tbody></table>';

	
	return r.join("");
}


xwTree.prototype.nodeIsOpen=function( xmlNode )
{
	var g = xmlNode.getAttribute("gui_open");
	if (g && g=="true")
	{
		return true;
	}
	else
	{
		return false;
	}
}

xwTree.prototype.nodeIsVisible=function( xmlNode )
{
	if (!xmlNode.parentNode) return true ; //isRoot
	
	var g = xmlNode.getAttribute("gui_visible");
	if (  g && g=="true")
	{
		return true;
	}
	else
	{
		return false;
	}
}

//psantos ini
// indica se o node tem o atributo gui_execute a true
xwTree.prototype.nodeIsExecute=function( xmlNode )
{
	if (!xmlNode.parentNode) return true ; //isRoot
	
	var g = xmlNode.getAttribute("gui_execute");
	if (this.runningMode && this.runningMode == "execute" && g && g=="true")
	{
		return true;
	}
	else
	{
		return false;
	}
}
// psantos fim

xwTree.prototype.getControlImg=function( xmlNode , level )
{
	var childs=this.haveChilds(xmlNode);
	var	lastNode = this.isLastNode(xmlNode);
	var	firstNode = this.isFirstNode(xmlNode);
	var toRet="";
	if ( childs )
	{
		var isOpen = this.nodeIsOpen( xmlNode );
		var v = isOpen?"open":"close";
		
		if ( firstNode && lastNode)
		{
			toRet=TREE_TEMA_DIR+'singlenode-'+v+'.gif'
		}
		else if( firstNode )
		{
			toRet=TREE_TEMA_DIR+'firstnode-'+v+'.gif'
		}
		else if( lastNode )
		{
			toRet=TREE_TEMA_DIR+'lastnode-'+v+'.gif'
		}
		else
		{
			toRet=TREE_TEMA_DIR+'node-'+v+'.gif'
		}

	}
	else
	{
		
		if( lastNode )
		{
			toRet=TREE_TEMA_DIR+'lastnode.gif'
			
		}
		else
		{
			toRet=TREE_TEMA_DIR+'node.gif'
		}
		
	}
	return toRet;
}


xwTree.prototype.render=function( node , level )
{
	var nodeName=node.nodeName.toUpperCase();
    
    if ( this.nodeToRender( nodeName ) && (  level>1 || this.renderFirstNode  ) )
	{
		var sid = node.getAttribute("sid");
		var existsid = true;
		this.currentLine++;
		if ( sid == null )
		{
			node.setAttribute('sid',GUIDGen());
			existsid=false;
		}
		if(!this.editMode)
		{
			if ( this.htm.rows.length < this.currentLine+1 )
			{
				this.addRow(-1);
			}
			
	
		}
		else
		{
			if ( this.htm.rows.length < this.currentLine+5 )
			{
				for(var y=0;y<10;y++)this.addRow(-1);
			}
		}
		
		
		
		this.renderNode ( node , level );
		
		
		
	}
	
	xCode = node.selectSingleNode('code') ;
	if ( xCode != null )
	{
		this.render(xCode,level);
		
	
	}
	else
	{
		level++;
		for (var z=0;z<node.childNodes.length;z++)
		{
			this.render(node.childNodes(z),level);
		}
		
	}	
		
	
}


function selectRow ( row )
{
	if ( row )
	{
		e=row;
	}
	else
	{
		e = window.event.srcElement;
	}
	
		while ( e && !e.lineW )
		{
			e=e.parentElement;
		}
	
	
	if ( e && ( !e.selected || !TREE_EDIT.editMode ) )
	{
			
			for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
			{
				//unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
				unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
			}
			
					
			if ( e.childNodes( TREE_EDIT.ncell ).firstChild )
			{
				e.selected=true;
				for ( var i = 0 ; i< e.childNodes.length ; i++ )
				{
					e.childNodes(i).runtimeStyle.backgroundColor=TREEROW_COLOR_SEL;
				}
				if ( TREE_EDIT.showLineNumbers )
				{
				//	e.childNodes(1).firstChild.checked=true;
				}
				TREE_EDIT.selectLines[ TREE_EDIT.selectLines.length ]=e.rowIndex;
				//DEBUG
				
				//if ( TREE_EDIT.editMode )
				//{		 
					elements_select( e.childNodes( TREE_EDIT.ncell ).firstChild.sid );	
				//}
				
			}
	}
	
}

function unselectRow( row )
{
	var e;
	
	if ( row )
	{
		e=row;
	}
	else
	{
	
		e = window.event.srcElement;
		while ( e && !e.lineW )
		{
			e=e.parentElement;
		}
	}
	
	if ( e )
	{
	
		if ( e.selected && e.childNodes( TREE_EDIT.ncell ).firstChild )
		{
			var cont=true;
			//if ( TREE_EDIT.editMode )
			//{
				cont=elements_unselect( e.childNodes( TREE_EDIT.ncell ).firstChild.sid )
			//}
			
			if ( cont )
			{
				e.selected=false;
				for ( var i = 0 ; i< e.childNodes.length ; i++ )
				{
					e.childNodes(i).runtimeStyle.backgroundColor="";
				}
			
				TREE_EDIT.selectLines=[];
				/*
				for ( i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
				{
					if ( TREE_EDIT.selectLines[i]== e.lineW )
					{
						TREE_EDIT.selectLines.splice( i ,1 );
						break;
					}
				}
				if ( TREE_EDIT.showLineNumbers )
				{			
					e.childNodes(1).firstChild.checked=false;
				}
				*/
			    return true;	
			}
		}
	
	}
	return false;
}

function toggleRow( row )
{
	var e;
	
	if ( row )
	{
		e=row;
	}
	else
	{
	
		e = window.event.srcElement;
		while ( e && !e.lineW )
		{
			e=e.parentElement;
		}
	}
	
	if ( e )
	{
	
		if ( !e.selected )
		{
			e.selected=true;
			for ( var i = 0 ; i< e.childNodes.length ; i++ )
			{
				e.childNodes(i).runtimeStyle.backgroundColor=TREEROW_COLOR_SEL;
			}
			
		//	if ( TREE_EDIT.showLineNumbers )
		//	{
		//		e.childNodes(1).firstChild.checked=true;
		//	}
			
			TREE_EDIT.selectLines[ TREE_EDIT.selectLines.length ]=e.lineW;
			
			//if ( TREE_EDIT.editMode )
			//{
			elements_select( e.childNodes(TREE_EDIT.ncell).firstChild.sid );
			//}
			
		}
		else
		{
			var cont = true;
			//if ( TREE_EDIT.editMode )
			//{
				cont = elements_unselect( e.childNodes( TREE_EDIT.ncell ).firstChild.sid );
			//}
			
			if ( cont )
			{
				e.selected=false;
				for ( var i = 0 ; i< e.childNodes.length ; i++ )
				{
					e.childNodes(i).runtimeStyle.backgroundColor="";
				}
				
				TREE_EDIT.selectLines=[];
				//for ( i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
				//{
				//	if ( TREE_EDIT.selectLines[i]== e.lineW )
				//	{
				//		TREE_EDIT.selectLines.splice( i ,1 );
				//		break;
				//	}
				//}
				//if ( TREE_EDIT.showLineNumbers )
				//{			
				//e.childNodes(1).firstChild.checked=false;
				//}
				
			}
		}
	}
}


xwTree.prototype.addRow=function( pos )
{
	//if( pos+""=="undefined") pos=-1;
	
	if( pos!=-1)
	{
		for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
		{
			//unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
			unselectRow ( TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ] );
		}
		TREE_EDIT.selectLines=[];
	}
    var r =this.htm.insertRow( pos );
    
    if ( this.editMode )
	{
		r.ondragenter = dragEnter; 
		r.ondragleave =dragLeave;
		r.ondragover = dragOver
		r.ondrop = drop;
    }
    r.selected=false;
    //r.id ="treeline"+r.rowIndex;
    r.onclick = selectRow;
    r.lineW = 1; //"l"+ r.rowIndex;
	var i = 0;
	
	var c=r.insertCell();
	
	if ( this.showLineNumbers )
	{

		c.className="cell lcell";
		c.align='center';
		c.innerHTML = this.htm.rows.length
		//'<img class="number" src="resources/numbers/'+this.htm.rows.length+'.gif" / WIDTH="18" HEIGHT="16">'
	
		c=r.insertCell();
		c.className="cell lcell";
		c.innerHTML="&nbsp;";
		//c.innerHTML='<input class="rad" tabindex=-1 type="checkbox"  id=checkbox1 name=checkbox1>';
	
		c=r.insertCell();
		c.className="cell lcell divider";
		c.innerHTML='&nbsp';
		c=r.insertCell();
	
	}
	c.className="cell";
	c.innerHTML='&nbsp;';
	
	
	if( pos!= -1 )
	{
	  		
		
	  for( var j=pos;j<this.htm.rows.length;j++)
	  {
	    this.htm.rows[j].cells[0].innerHTML = j+1;
	    //this.htm.rows[j].id ="treeline"+this.htm.rows[j].rowIndex;
        //this.htm.rows[j].lineW = "l"+ this.htm.rows[j].rowIndex;
	  }
	  
	}

	

}







function getSID(srcEle)
{
	var toRet=null;
	while (srcEle.tagName!='BODY')
	{
		if ( srcEle.id && srcEle.id.substr(0,3)=='sid' )
		{
			return srcEle.id.substr(3);
		}
		srcEle=srcEle.parentElement;
	}
	
	return toRet;
}

function clickCtrlImg()
{
	var e=window.event.srcElement;
	
	while( e.tagName!='TABLE' && !e.id.substr(0,3)!='sid') e=e.parentElement;
	if( e.id.substr(0,3)=='sid' )
	{
	  TREE_EDIT.toggle( e );
	}
	//alert(e.id);
	
	//var line = e.parentElement.parentElement;
	//alert(line.tagName);
}

xwTree.prototype.markToOpenNode=function( node )
{
	node.setAttribute("gui_open","true");
	node.setAttribute("gui_visible","true");
	
	for (var z=0;z<node.childNodes.length;z++)
	{
		var nodeName = node.childNodes(z).nodeName;
		if ( this.nodeToRender( nodeName ) )
		{
		    node.childNodes(z).setAttribute("gui_visible","true");
		    
		}
		else
		{
			this._mark( node.childNodes(z) );
		}
		
	}
	
}
xwTree.prototype._mark=function( node )
{
   var nodeName = node.nodeName;
   	if ( this.nodeToRender( nodeName ) )
   	{
   		
   		node.setAttribute("gui_visible","true");
   	}
   	else
   	{
   		for (var z=0;z<node.childNodes.length;z++)
		{
			
			this._mark( node.childNodes(z) );
		}
   	}
}

xwTree.prototype.toggle=function( e )
{
	
	//var search="/descendant::*[@sid='"+sid+"']"; //NTRIMJS
	var sid = e.id.substr(3);
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=this.code.firstChild.selectSingleNode(search);
	var toOpen=false;
	
	
	
	if( node != null )
	{
				
		var e1=window.document.getElementById("SID"+node.getAttribute("sid"));
		var tr = e1.parentElement.parentElement;
		var trIdx = tr.rowIndex;
		var level = parseInt(e1.level,10);
		
		var img=e.cells[level-1].firstChild.firstChild;
		
		if (img.src.indexOf('-open') > -1 )
		{
			img.src=img.src.replace(/-open/,"-close");
			
		}
		else
		{
			img.src=img.src.replace(/-close/,"-open");
			toOpen=true;
		}
		
		if ( toOpen )
		{
			node.setAttribute("gui_open","true");
		}
		else
		{
			node.setAttribute("gui_open","false");
		}
		
		
		var ix=trIdx+1;
				
		while ( ix < this.htm.rows.length  )
		{
			var t = this.htm.rows[ ix ];
			
			if ( this.showLineNumbers )
			{
				var c=t.cells[3];
			}
			else
			{
				var c=t.cells[0];
			}
			
			if ( !c.level ) break;
			var tb = c.firstChild;
			var rowLevel = c.level;
			
			
			search="//*[@sid='"+c.sid+"']"; //NTRIMJS
			var xnode=this.code.firstChild.selectSingleNode(search);
			
			var rfh = true;
			if ( c.firstChild && c.firstChild.id )
			{
			
				if ( c.firstChild.id.substr(3) == xnode.getAttribute("sid") )
				{
					rfh=false;
				}
			}
	
		
			if ( rfh || !tb || !tb.rows )
			{
				var img = elements_getImg( xnode );
				var html= elements_getHtml( xnode );
				xnode.setAttribute("gui_visible","true")
				c.innerHTML = this.getHTMLNode(xnode , c.level , img , html );
				tb = c.firstChild;
			}
					
			var isOpen = tb.rows[0].cells[parseInt(tb.level,10)-1].firstChild.firstChild.src.indexOf('-open')>-1;
			var isSingle = !isOpen && tb.rows[0].cells[parseInt(tb.level,10)-1].firstChild.firstChild.src.indexOf('close')==-1;
			
			
			if( rowLevel > level )
			{
				if ( toOpen )
				{
					t.style.display='';
					if ( !isSingle && !isOpen )
					{
						//skip until level > rowLevel
						var currLevel = rowLevel;
						ix++;
						while ( ix < this.htm.rows.length  )
						{
							t = this.htm.rows[ ix ];
							if ( this.showLineNumbers )
							{
								c=t.cells[3];
							}
							else
							{
								c=t.cells[0];
							}
							//tb = c.firstChild;
							
							//if ( parseInt(tb.level,10) <= currLevel )
							if ( c.level <= currLevel )
							{
								ix--;
								break;
							}
							else
							{
								ix++;
							}
								
						}
					}
				}
				else
				{
					t.style.display='none';
				}
						
			}
			else
			{
				break;
			}
					
			ix++;
		}
		if(toOpen)
    {
      try
		  {
			   if(parent.refreshCounters)
			   {
			      parent.refreshCounters();
			   }
			}catch(x){}
    }
		
	}
	
	
}


xwTree.prototype.haveChilds=function( xNode )
{

	var xCode = xNode.selectSingleNode('code') ;
	if( xCode != null )
	{
		for (var z=0;z<xCode.childNodes.length;z++)
		{
 			var xfieldname=xCode.childNodes(z).nodeName;
 			if ( this.nodeToRender( xfieldname ) )
 			{
 			  return true;
 			}
		}		
	}
	else
	{
	    var toRet = elements_haveFixedChilds( xNode );
	    if ( !toRet )
	    {
			for (var z=0;z<xNode.childNodes.length;z++)
			{
 				var xfieldname=xNode.childNodes(z).nodeName;
 				if ( this.nodeToRender( xfieldname )  )
 				{
 				  return true;
 				}
 				
			}		
		}
		else
		{
		  return true;
		}
	}
	
	return false;
}

xwTree.prototype.isLastNode=function( xNode )
{
	xNode = xNode.nextSibling;
	while ( xNode )
	{
 	    var xfieldname=xNode.nodeName;
 	    if ( this.nodeToRender( xfieldname ) )
 	    {
 	      return false;
 	    }
 	    xNode = xNode.nextSibling;
	}	
	return true;
}

xwTree.prototype.isFirstNode=function( xNode )
{
	xNode = xNode.previousSibling;
	while ( xNode )
	{
 	    var xfieldname=xNode.nodeName;
 	    if ( this.nodeToRender( xfieldname ) )
 	    {
 	      return false;
 	    }
 	    xNode = xNode.nextSibling;
	}	
	return true;
}


xwTree.prototype.parseXml=function( xmlNode , level )
{
	var x = xmlNode;
	this.currentLine++;
    this.render( xmlNode , level );
    
	
	for (var z=0;z<x.childNodes.length;z++)
	{
		 
 	    var xfieldname=x.childNodes(z).nodeName;
		this.parseXml(x.childNodes(z),level++);	   
	}	

}


xwTree.prototype.deleteCurrentRow=function()
{

		var SidToDelete=null;
		for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
		{
		    unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
		    SidToDelete = TREE_EDIT.htm.rows[ TREE_EDIT.selectLines[i] ].childNodes( TREE_EDIT.ncell ).firstChild.sid 
		    
		}
		//xmlNode.getAttribute("sid")
		if ( SidToDelete!=null )
		{
			if ( window.confirm("A T E N Ç Ã O \nDeseja mesmo apagar linha seleccionada ?") )
			{
			var search;
			var nodeFrom;
			search="//*[@sid='"+SidToDelete+"']"; //NTRIMJS
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



}

