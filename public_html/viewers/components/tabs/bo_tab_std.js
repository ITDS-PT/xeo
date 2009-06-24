//<SCRIPT>
if( typeof XV21 == 'undefined') {
	window.XV21 = {};
}
  
XV21.Tabs = {};
 
XV21.Tabs.load =  function( xid, toactivate ) {
    so(xid);
 	XV21.Tabs.downTab_std( document.getElementById( toactivate ) );    
}

XV21.Tabs.loadTab_std =  function loadTab_std()
{
	var h=_hso.hb;
	if(h&&h.cells)
		XV21.Tabs.loadTab_std.downTab_std(h.cells[0].firstChild);
}


XV21.Tabs.loadTab_std = function(event) {
  	// Handle ENTER Key
	if (event.keyCode == 13)
	{
		XV21.Tabs.onClickTab_std(event);
	}
}

XV21.Tabs.onDocumentReadyTab_std = function (){
	XV21.Tabs.loadTab_std();
}

XV21.Tabs.onClickTab_std = function(event){

	XV21.Tabs.downTab_std(getElement(event));
}

XV21.Tabs.getBody = function(xid){
  return xid.replace(/header/,'body');
}


XV21.Tabs.ondragenterTab_std = function(event)
{
	_hso.timeover=new Date();
}


XV21.Tabs.ondragoverTab_std = function(event)
{
	var t=new Date();
	if ( t-_hso.timeover > 700 )
	{
		XV21.Tabs.downTab_std(getElement(event));
		_hso.timeover=new Date();
    }
}


XV21.Tabs.downTab_std = function(oTab)
{
	var o=oTab;
	
	setTabWFocus(oTab.id, _hso.id);

    var xgt=document.getElementById('hrTab'+_hso.id);
    var xgts=document.getElementById('hrSelTab'+_hso.id);
    
	if (o.className == "tab" || !_hso.oTab || o.tagName=='IMG')
	{
		debugger;
		
	    if(o.tagName=='IMG') o=o.parentNode;
		xgt.style.display = "none";

		o.className = "tab tabOn";
		
		var toLoad=true;
		if( o.offsetTop - 4 <0) 
			toLoad=false; //neste caso o tab não está visivel
		
		
		XV21.Tabs.showTab_std(xgts,o);
        
        var b=document.getElementById( getBody(o.id) );
        b.style.display="inline";
		
		if ( toLoad){
			var xfrms=b.getElementsByTagName('iframe');
			for ( var i=0; i< xfrms.length ;i++){
			  if ( xfrms[i].src=="" && xfrms[i].xsrc ){
				 var x=setUrlAttribute(xfrms[i].xsrc,"docid",getDocId());
				xfrms[i].src=x;
			  }
			}
			var tabDivs = b.getElementsByTagName("div");
			if( tabDivs!=null && tabDivs.length > 0 ) {
				if( tabDivs[0].className.indexOf("tab-load") > -1 ) {
					var sId =  tabDivs[0].id.substring(0,tabDivs[0].id.indexOf(":"));
					XVW.AjaxRenderComp( sId, tabDivs[0].id, false );
				}
			}
		}

		if (_hso.oTab)
		{
			_hso.oTab.className = "tab";
			b=document.getElementById( getBody(_hso.oTab.id) );
            b.style.display="none";
		}

		_hso.oTab = o;

	}
	else{
		if(window.event != null && window.event.type=='resize' ) {
			XV21.Tabs.showTab_std(xgts,o);
		}
		var b=document.getElementById( getBody(o.id) );
		if ( b)
		{
			b.style.display="inline";
			var xfrms=b.getElementsByTagName('iframe');
			for ( var i=0; i< xfrms.length ;i++){
			  if ( xfrms[i].src=="" && xfrms[i].xsrc ){
				 var x=setUrlAttribute(xfrms[i].xsrc,"docid",getDocId());
				xfrms[i].src=x;
			  }
			}
		}
        
	}
	ExtXeo.layoutMan.doLayout();
}

XV21.Tabs.onOverTab_std = function(event)
{
    var xgt=document.getElementById('hrTab'+_hso.id);
    XV21.Tabs.showTab_std(xgt,getElement(event));
}

XV21.Tabs.onOutTab_std = function(event)
{
    var x=document.getElementById('hrTab'+_hso.id);
	x.style.display = "none";
}

XV21.Tabs.showTab_std = function(oShow, o)
{
	try
	{
		with (oShow.style)
		{
			left	= o.offsetLeft + 1;
			top		= o.offsetTop; 
			width	= o.offsetWidth - 2;
			display = "inline";
		}
	
	}
	catch(e)
	{
		//truque  para ficar sele o tab qd existem mais areas com tabs
		XV21.Tabs.recover_Tab(oShow,o)
	}
}

XV21.Tabs.recover_Tab = function(oShow,o){
  var oa=oShow;
  
  
  while (true) 
  {
   if(!oShow||oShow.tagName=='BODY'){
       return
   }
   else if(oShow&&oShow.currentStyle.display=='none' && oShow.tagName=='DIV')
   {
    break;
   }
   else  oShow=oShow.parentElement;
  } 
    
  try{
   if(oShow&&oShow.tagName!='BODY'){
     
     oShow.style.display='inline';
    	with (oa.style)
		{
			left	= o.offsetLeft + 1;
			top		= o.offsetTop - 4;
			width	= o.offsetWidth - 2;
			display = "inline";
		}
	 oShow.style.display='none';
   }
  
   }
  catch(e){

  }
}
