
contextDisabled=false;     // Set this parameter to disable or enable right click, context menu at runtime.
contextMenu="explorerMouseMenu"; // Default name for the contextMenu
contextObject="";          // This is the object the right click occured on, could be an image, link whatever was under the mouse at the point of right click.

function popup(menu)
{
	menu.style.display="inline";
	menu.style.position='absolute';
	menu.style.posTop=  event.y;
	menu.style.posLeft= event.x;
}

function popupClose()
{
	this.releaseCapture();
	this._capture=false;
	if ( this.activeOption )
  {
		this.setOptionInactive( this.activeOption )
	}
	this.activeOption=null;
  this.style.display='none';
}

function rclick(e){

	if(contextDisabled)
	{
		document.oncontextmenu=null
		return true;
	}
	if(document.all)
	{
		ev=event.button;
		contextObject=event.srcElement
	}
	else 
	{
		ev=e.which
		contextObject=e.target
	}
	if(ev==2||ev==3){
		_gm=document.getElementById(contextMenu)
		if(_gm!=null)
		{
			popup(_gm);
		}
		return false;
	}
	else{
		//if(ev==1)closeAllMenus();
	}
	return true;
}


if(ns4){
	document.captureEvents(Event.MOUSEDOWN);
	document.onmousedown=rclick;
}
else{
	document.onmouseup=rclick
	document.oncontextmenu=new Function("return false")
}