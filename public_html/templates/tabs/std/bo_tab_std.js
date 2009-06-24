//<SCRIPT>
function loadTab_std()
{
	var h=_hso.hb;
	if(h&&h.cells)
	downTab_std(h.cells[0].firstChild);
}


function onKeyUpTab_std(event){
  	// Handle ENTER Key
	if (event.keyCode == 13)
	{
		//event.srcElement.click();
		onClickTab_std(event)
	}
}

function onDocumentReadyTab_std(){
   loadTab_std();
}

function onClickTab_std(event){

 downTab_std(getElement(event));
}

function getBody(xid){
  return xid.replace(/header/,'body');
}


function ondragenterTab_std(event)
{
_hso.timeover=new Date();
}


function ondragoverTab_std(event)
{
	var t=new Date();
	if ( t-_hso.timeover > 700 )
	{
		downTab_std(getElement(event));
		_hso.timeover=new Date();
    }
}


function downTab_std(oTab)
{

	var o=oTab;
	
	setTabWFocus(oTab.id, _hso.id);

    var xgt=document.getElementById('hrTab'+_hso.id);
    var xgts=document.getElementById('hrSelTab'+_hso.id);
    
	if (o.className == "tab" || !_hso.oTab || o.tagName=='IMG')
	{
	    if(o.tagName=='IMG') o=o.parentNode;
		xgt.style.display = "none";

		o.className = "tab tabOn";
		
		var toLoad=true;
		if( o.offsetTop - 4 <0) toLoad=false; //neste caso o tab não está visivel
		
		
		showTab_std(xgts,o);
        
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
			  showTab_std(xgts,o);
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
}

function onOverTab_std(event)
{
    var xgt=document.getElementById('hrTab'+_hso.id);
	showTab_std(xgt,getElement(event));
}

function onOutTab_std(event)
{
    var x=document.getElementById('hrTab'+_hso.id);
	x.style.display = "none";
}

function showTab_std(oShow, o)
{
	try
	{
		with (oShow.style)
		{
			left	= o.offsetLeft + 1;
			top		= o.offsetTop - 4;
			width	= o.offsetWidth - 2;
			display = "inline";
		}
	
	}
	catch(e)
	{
	 //truque  para ficar sele o tab qd existem mais areas com tabs
	 recover_Tab(oShow,o)
	  }
}

function recover_Tab(oShow,o){
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
