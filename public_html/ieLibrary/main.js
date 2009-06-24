//<SCRIPT>

document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
document.ondragstart=function(){event.returnValue = false}

isIE=true;
var G_1;
var G_2;
var O_1;
var O_2;
var doc;
window.main=true;
function winmain(){return window}


var doc;
var notRest=false;
var _backColor="";

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

var obfl=[];

	
function setStatus(xid,str){
  if ( ndl[xid] ){
    if (ndl[xid].msg) ndl[xid].msg.innerHTML=str;
  }
}


function getStatus(xid,str){
  if ( ndl[xid] && ndl[xid].msg){
    return ndl[xid].msg.innerHTML;
  }
  else return '';
}



var idToClose;

function  loaded(xid){

 
 if(!event) return;

 if (!xid){
   if (!event.srcElement){
     return;
   }
   xid=event.srcElement.id.split('$')[1];
 }
  
  if (event.srcElement.readyState!='complete') return;

  ndl[xid].loaded=true;  
 
  if(ndl[xid].msg) ndl[xid].msg.innerHTML='';
  var x=eval('frm$'+xid);
  
  if( !ndl[xid].externalLink )
  {
		x.window.windowIDX=xid;
  
		if(x.window.objLabel) {
		  if(ndl[xid].hdrlabel) ndl[xid].hdrlabel.innerHTML=x.window.objLabel;
		  //+(new Date()-ndl[xid].startTime )+"--"+(new Date()-ndl[xid].start2 ) ;
		}
		else
		{
			var x_label="Página "+xid;
			if(ndl[xid].title )
			{
				x_label=ndl[xid].title;
			}
			if(ndl[xid].hdrlabel)
			ndl[xid].hdrlabel.innerHTML=x_label;
		
		}
  
		if ( x.window.objDescription ){
		  if(ndl[xid].hdrDescr){
		   ndl[xid].hdrDescr.innerHTML=x.window.objDescription;
		   ndl[xid].hdrDescr.style.display='';
		   }
		}
  
  
		if(x.window.objStatus){
		 if(ndl[xid].msg)  ndl[xid].msg.innerHTML=x.window.objStatus;
		}
  
  
		if ( ndl[xid].activeOnOpen ) activeDocByIdx(xid)
  
       
		if( x.boFormSubmit && x.boFormSubmit.toClose && x.boFormSubmit.toClose.value=='y' ){
		  //if (x.body.onload)
		  if (x.document.body.onload) {
		      idToClose=xid;
		      window.setTimeout("ndl[idToClose].close()",200);
		  }
		  else ndl[xid].close();
		  
		}
		else
		{
		  if ( ndl[xid].onLoaded ) ndl[xid].onLoaded();
		}
  }
  else
  {
		var x_label="Página";
		if(ndl[xid].title )
		{
		var x_label=ndl[xid].title;
		}
		ndl[xid].hdrlabel.innerHTML=x_label;
  }
}



function ro(){
	this.formName=null;
	this.obj=null;
	this.boql=null;
	this.docid=null;
	
}


var ndl=[];
var actived=null;

var STATUS_CREATING = 0;
var STATUS_NORMAL = 1;
var STATUS_MINIMEZED = 2;
var STATUS_MAXIMIZED = 3;

var dcache=document;

	
function docWindow(idx,features,object,form,params,className,jspURL,title){

	this.object=object;
	this.form=form;
	this.params=params;
	this.idx=idx;
	this.title=title;
	this.onclose=null;
	this.onLoaded=null;
    this.status=STATUS_CREATING;
	this.loaded=false
	this.jspURL=jspURL;
	this.delay=false;
    if(jspURL) this.url=jspURL;
    else
	this.url=this.object+"_general"+form+".jsp?";//NTRIMJS
	
	this.qry=params;
	
	this.childs=[];
//read features    
	
	this.activeOnOpen = true;
	this.closeWindowOnCloseDoc = false;
	this.showCloseIcon = true;
	this.externalLink=false;
	
	if ( features )
	{
		var f = features.split(",");
		for( var j=0 ; j< f.length; j++)
		{
			var fi = f[j].split("=");
			if ( fi[0].toUpperCase()=="CLOSEWINDOWONCLOSEDOC" )
			{
				if ( fi[1] == '1' || fi[1].toUpperCase()=="YES"|| fi[1].toUpperCase()=="TRUE"  )
				{
					this.closeWindowOnCloseDoc = true;
				}
				else
				{
					this.closeWindowOnCloseDoc = false;
				}
			}
			else if ( fi[0].toUpperCase()=="SHOWCLOSEICON" )
			{
				if ( fi[1] == '1' || fi[1].toUpperCase()=="YES"|| fi[1].toUpperCase()=="TRUE"  )
				{
					this.showCloseIcon = true;
				}
				else
				{
					this.showCloseIcon = false;
				}
			}
			else if ( fi[0].toUpperCase()=="DELAY" )
			{
				if ( fi[1] == '1' || fi[1].toUpperCase()=="YES"|| fi[1].toUpperCase()=="TRUE"  )
				{
					this.delay = true;
				}
				else
				{
					this.delay = false;
				}
			}
			else if ( fi[0].toUpperCase()=="ACTIVEONOPEN" )
			{
				if ( fi[1] == '1' || fi[1].toUpperCase()=="YES"|| fi[1].toUpperCase()=="TRUE"  )
				{
					this.activeOnOpen = true;
				}
				else
				{
					this.activeOnOpen = false;
				}
			}
			else if ( fi[0].toUpperCase()=="EXTERNALLINK" )
			{
				if ( fi[1] == '1' || fi[1].toUpperCase()=="YES"|| fi[1].toUpperCase()=="TRUE"  )
				{
					this.externalLink=true;
					
				}
				else
				{
					this.externalLink=false;
				}
				
			}
		}
	}
		
    this.addToTaskBar( idx);
	
	this.taskItem=document.getElementById("task$"+idx);
	this.hdrlabel=document.getElementById("hdr$"+idx);
	this.msg=null;
	
	if ( this.url.indexOf("searchString") > -1 )//NTRIMJS
    {
	
		this.taskItem.style.display='none';
    }
    
	
      

	if(this.msg && !this.delay)
	{
	
         this.msg.innerHTML="<b>Abrindo documento </b><img src=resources/senddata.gif>";
    }
	

	this.savePress=false; //activo qdo o botão do save é carregado

   	if ( pool.length > 0 )
   	{
	    var xfrm=pool.pop();
	}
	else
	{
	    var xfrm=dcache.createElement("iframe");
	    xfrm.onreadystatechange=loaded;
		xfrm.onload=loaded;
		xfrm.frameBorder=0;
		xfrm.scrolling='no';
		xfrm.width="100%";
		xfrm.height="100%";
		
	}
	if( this.externalLink )
	{
		xfrm.scrolling='yes';
	}
	xfrm.id="frm$"+idx;
	this.frm=xfrm;
	this.htm=xfrm;
	xfrm.style.display='none';
	
	var xxsrc=this.url+this.qry;
	
	this.previousIdx=null;
	if( actived )
	{
		this.previousIdx=actived.id.split("$")[1];
	}
	
	
	if(xxsrc.indexOf("?")==-1 ) 
	{
		this.src=this.url+this.qry+"?myIDX="+this.idx;
		if(this.delay )
		{
			window.setTimeout("loadfrm("+this.idx+")",500);
		}
		else	
		{
		 xfrm.src=this.src;
		}
	}
	else
	{
		this.src=this.url+this.qry+"&myIDX="+this.idx;
		if(this.delay)
		{
			window.setTimeout("loadfrm("+this.idx+")",1500);
		}
		else
		{
		 xfrm.src=this.src;
		}
	}
	
	docs.appendChild(xfrm)
	
	
	
    this.status=STATUS_NORMAL;
	this.start2=new Date();
	
}

var toReload=null;

function loadfrm(idx)
{
	var dd= ndl[ idx ];
	if (dd) dd.frm.src = dd.src;
} 
docWindow.prototype.addToTaskBar=function( idx )
{
	var h=[];
	var i=0;
	//var isSel=false;
	
	h[i++]="<table id='task$";
	h[i++]=idx;
	h[i++]="' style='height:100%;' cellborder='0' cellpadding=0 cellspacing=0 ";
	h[i++]=" ondragenter='activeDocByIdx(";
	h[i++]=idx;
	h[i++]=")' >";
		
	h[i++]="<tr id='ticRow";
	h[i++]=idx;
	h[i++]="' style='height:100%' class=";
	
	
	h[i++]="'taskNormal'";
	
	
	h[i++]="><td class='taskBar'tabIndex="
	h[i++]=idx;
	h[i++]=" onclick='activeDocByIdx(";
	h[i++]=idx;
	h[i++]=")' "
	//if(isSel) h[i++]="_sel";
	h[i++]=" noWrap ";
	h[i++]=" id=hdr$";
	h[i++]= idx; 
	
	//h[i++]=" colspan=2>";
	h[i++]=" >";
	
	if( this.title )
	{
	h[i++]=this.title;
	}
	h[i++]="<img hspace=2 src=resources/senddata.gif>";
	h[i++]="</td><td id='clo"
	h[i++]=idx;
	h[i++]="' class='taskBarClose'>";
	
	if ( this.showCloseIcon )
	{
		h[i++]="<img onclick='ndl["+idx+"].close();' src='ieThemes/0/taskBar/buttclose.gif' width='12px' height='12px'/>";
	}
	
	h[i++]="</td><td class='tasksep'></td></tr></table>"
	var tskBar = document.getElementById("taskbar");
	if ( tskBar )
	{
		var c=tskBar.rows[0].insertCell();
		c.innerHTML=h.join("");
		document.getElementById("containerTaskBar").setVisible(c);
	}
	
}

docWindow.prototype.removeFromTaskBar=function()
{
	var x=document.getElementById("task$"+this.idx);
	if (x )
	{
	x.parentElement.parentElement.removeChild(x.parentElement)
	document.getElementById("containerTaskBar").checkBounds();
	}
}

docWindow.prototype.refresh=function(){
  var x=eval('frm$'+this.idx);
  
  if(x){
    x.document.boForm.BindValues();
  }
 
}


docWindow.prototype.nextPage=function( mode , xobject,form,params,className,jspURL )
{    
	if(jspURL)
	{
	    this.url= jspURL + "?";	    
	}
    else
    {
       this.url=xobject+"_general"+form+".jsp?";//NTRIMJS
    }   
    
	this.qry=params;
	var xxsrc=this.url+this.qry;
	if(xxsrc.indexOf("?")==-1 ) 
	{
		 xxsrc=this.url+this.qry+"?myIDX="+this.idx;//NTRIMJS
			 
	}
	else xxsrc=this.url+this.qry+"&myIDX="+this.idx;//NTRIMJS
	var x=eval('frm$'+this.idx);
	x.createHiddenInput( "nextPage", encodeURIComponent( xxsrc ) );
	
	if( mode=='save' )
	{
	
	x.createHiddenInput( "noUpdate","true" );
	x.document.boForm.Save(true);
	}
	else if ( mode =='bind' )
	{
	
	x.document.boForm.BindValues(true);
	}
	else if ( mode =='cancel' )
	{
	
	x.document.boFormSubmit.submit();
	}	
	
}
 
var pool=[];
docWindow.prototype.addChild=function( nr )
{
   this.childs[ this.childs.length ] = nr;
}

docWindow.prototype.activePrevious=function()
{

	if ( this.previousIdx != null && ndl[ this.previousIdx ] )
	{
		activedoc( ndl[this.previousIdx].htm );
	}
	else
	{
		var found=false;
		for ( var i=this.idx-1 ; i>=0 && !found; i-- )
		{
		   if(ndl[i] && ndl[i].frm )
		   {
		     activedoc( ndl[i].htm );
		     found=true;
		   }
		}
		if(!found )
		{
		  for ( var i=this.idx+1 ; i<ndl.length ; i++ )
		  {
		     if(ndl[i] && ndl[i].frm ) activedoc( ndl[i].frm);
		  }
		}
	}
	
}

docWindow.prototype.close=function(anyway){
  
  if( !this.loaded )
  {
    var x=this.frm;
	//var x=document.getElementById('frm$'+this.idx).contentWindow //  eval('frm$'+this.idx);
	if ( x) 
	{
		
		 if(x.location) x.location.href="blank.htm";
    	 pool[pool.length]=docs.removeChild( this.frm  );
		 x.style.display='none';
		 
		 this.removeFromTaskBar();
		  
		  this.object=null;
		  this.form=null;
		  this.params=null;
		  this.className=null;
		  this.url=null;
		  this.jspURL=null;
		  this.qry=null;
		  this.childs=null;
		  this.tit=null;
		  this.hdrlabel=null;
		  this.msg=null;
		  this.status=null;
		  this.start2=null;
		  this.frm=null;
		  
		  x1=null;
		  actived=null;
  
		  this.activePrevious();
		  
		  ndl[this.idx]=null; 
		if ( this.closeWindowOnCloseDoc )
		{
		  window.close();
		}
		return true;
    }
 
  }
  
  var x=eval('frm$'+this.idx);//  document.getElementById('frm$'+this.idx).contentWindow ;
  //var x1=eval('dbody$'+this.idx);
 
  var verifyToClose=true;
  if ( x )
  {
	activedoc( this.frm );
	if (anyway || !x.boFormSubmit || !x.boFormSubmit.BOUI || !x.boForm )
	{
		verifyToClose=false;
	}
  }
  var continue_toClose=true;
  if (  verifyToClose )
  {
	 continue_toClose=x.boForm.canClose()
  
  
  }
   

 if ( continue_toClose )
  {
  
  //if(x){
 
    
    if ( x.boFormSubmit && x.boFormSubmit.docid && x.boFormSubmit.masterdoc ){
       if (!anyway )
       {
           
           //se este for o master doc verificar se existem mais doc clientes com o mesmo docid para fechar
          
           for ( var i=0 ; i < ndl.length ; i ++)  if ( ndl[i] && ndl[i]!=this )
           {
              var xd=null;
              
              try {xd=eval('frm$'+ndl[i].idx);} catch(e){}

              try { 
              if ( xd && xd.boFormSubmit && ( xd.boFormSubmit.docid.value == x.boFormSubmit.docid.value || ( xd.boFormSubmit.ctxParentIdx && xd.boFormSubmit.ctxParentIdx.value==x.boFormSubmit.docid.value ) ) ){
              
                continue_toClose=ndl[i].close();
                if (!continue_toClose) return false;
              }
              } 
              catch(e)
              {
                try{continue_toClose=ndl[i].close();if (!continue_toClose) return false;}catch(er){}
              }
           }
           closeServerDoc( x.boFormSubmit.docid.value );
           
           
       }
    }
    for ( var i=0 ; i < this.childs.length; i++)
    {
	  if ( ndl[ this.childs[i] ] && this.idx!=this.childs[i] ) ndl[ this.childs[i] ].close();
	}
    x.location.href="blank.htm";
    

    pool[pool.length]=docs.removeChild(this.frm);
    this.frm=null;
  
  this.removeFromTaskBar();
  
    
  this.d=null;
  
  this.object=null;
  this.form=null;
  this.params=null;
  this.className=null;
  this.url=null;
  this.jspURL=null;
  this.qry=null;
  this.childs=null;
  this.tit=null;
  this.hdrlabel=null;
  this.msg=null;
  this.status=null;
  this.start2=null;
  
 
	
  actived=null;
  this.activePrevious();
  if(ndl[this.idx].onclose!=null)ndl[this.idx].onclose();
  ndl[this.idx]=null; 
	if ( this.closeWindowOnCloseDoc )
		{
		  window.close();
		}
	return true;
 }
 
 return false;

}


function existsOpenDoc()
{
 for ( var i=0 ; i < ndl.lenght ; i++) ; 
 {
	if ( ndl[i] && ndl[i].frm && ndl[i].status!= STATUS_MINIMEZED  )
	{
	   return true;
	}
 }
 return false;
}

 

var docServerToClose=[];
function closeServerDoc(xdocid)
{
    docServerToClose[docServerToClose.length]=xdocid;
    verifyCmdsSession();
}

function verifyCmdsSession()
{
if ( controlSession.document.readyState=='complete' ){
    if(docServerToClose.length > 0 ){
        if(controlSession.boFormSession != null)
        {
           controlSession.boFormSession.closeDocIds.value=docServerToClose.join(';');
           docServerToClose=[]; 
           controlSession.boFormSession.submit();
        }
    }
}

}


function openModeDocUrl(idx, mode, features ,jspURL,params,className,parentNDL){
	var x=eval('frm$'+idx);
	if( mode=='save' )
	{
		x.createHiddenInput( "noUpdate","true" );
		x.document.boForm.Save(true);
	}
	else if ( mode =='bind' )
	{
		x.document.boForm.BindValues();
	}
	else if ( mode =='cancel' )
	{
		x.document.boFormSubmit.submit();
	}
	openDocUrl( features ,jspURL,params,className,parentNDL);
}

function isopenDocUrl(features,jspURL,params,className,parentNDL){
    var found=false;
    if(!className) className="std";
    
    for(var i=0;i<ndl.length&&!found;i++)
    {
		found=ndl[i]&& ndl[i].frm && ndl[i].jspURL==jspURL &&  ndl[i].className==className && ndl[i].params==params
		if(found)
		{
			 return true; 
		}  
    }
	return false;    
}
function openDocUrl(features,jspURL,params,className,parentNDL,title, verifyIsOpen){
    var found=false;
    if(!className) className="std";
    
    if(verifyIsOpen == null || verifyIsOpen)
    {
       for(var i=0;i<ndl.length&&!found;i++)
       {
   		found=ndl[i]&& ndl[i].frm && ndl[i].jspURL==jspURL && ndl[i].params==params
   		if(found)
   		{
   			 //weffect=ndl[i].htm;
   			 
   			 activedoc(ndl[i].frm); 
   			 //weffect.style.visibility='hidden';	 
   			 //window.setTimeout("weffect.style.visibility='visible'",50);
   			 
   		}  
       }
    }
    if (!found)
    {
		ndl[ ndl.length ]=new docWindow(ndl.length,features,"","",params,className,jspURL,title);
		if ( parentNDL && ndl[parentNDL] )
		{
			ndl[ parentNDL ].addChild( ndl.length-1 );
		}
		toFocus=ndl[ndl.length-1].frm;
		
		if ( ndl[ ndl.length-1].activeOnOpen  && ndl[ ndl.length-1].url.indexOf("searchString")==-1)
		{
		  
			window.setTimeout('activedoc(toFocus)', 2000);
			activedoc(ndl[ndl.length-1].frm);
		}
		 
    }
    
}

weffect=null;

function nextPage( idx , xobject,form,params,className,jspURL )
{
   ndl[idx].nextPage( 'bind',xobject,form,params,className,jspURL );
}
function saveAndNextPage( idx , xobject,form,params,className,jspURL )
{
   ndl[idx].nextPage( 'save',xobject,form,params,className,jspURL );
}
function cancelAndNextPage( idx , xobject,form,params,className,jspURL )
{
   ndl[idx].nextPage( 'cancel',xobject,form,params,className,jspURL );
}
function newPage( idx , xobject,form,params,className,jspURL )
{
   ndl[idx].nextPage( 'bind',xobject,form,params,className,jspURL );
}


function openDoc(features,xobject,form,params,className,jspURL,title)
{
    var found=false;
    if(!className) className="std";
    
    if( params.indexOf("method=new")==-1)
    {
		for(var i=0; i<ndl.length && !found ; i++)
		{
			found=ndl[i]&& ndl[i].htm && ndl[i].object==xobject&&ndl[i].form==form && ndl[i].jspURL==jspURL &&ndl[i].params==params;
			if(found)
			{
				 weffect=ndl[i].htm;
				 
				 activedoc(ndl[i].htm); 
				 weffect.style.visibility='hidden';	 
				 window.setTimeout("weffect.style.visibility='visible'",50);
				 return ndl[i];
				 
			}
		}
	}
    if (!found)
    {
		ndl[ ndl.length ]=new docWindow(ndl.length,features,xobject,form,params,className,jspURL,title);
		if ( ndl[ ndl.length-1].activeOnOpen  && ndl[ ndl.length-1].url.indexOf("searchString")==-1 )
		{
		activedoc(ndl[ndl.length-1].htm);
		}
		ndl[ ndl.length-1 ].startTime=new Date();
		return ndl[ndl.length-1];
    }
    
}



function activeDocByIdx(xidx){
 var dd=ndl[xidx] ;
 if(dd && dd.htm) activedoc(dd.htm);
}



function activedoc(d)
{
	df=d.id.split('$');

	var dc=ndl[df[1]];
	if(!dc) return;
	var idx=dc.idx;
	var tskBar = document.getElementById("taskbar");
	if ( tskBar )
	{
		dc.taskItem.style.display='';
		document.getElementById("ticRow"+idx).className="taskSel";
		dc.hdrlabel.className="taskBar_sel";
		document.getElementById("clo"+idx).className="taskBarClose_sel";
	}	
	dc.frm.style.display='';
    
	if( actived && d!=actived ){

	
		df=actived.id.split('$');
		dc=ndl[df[1]];
		if(dc){
	        
			try{actived.contentWindow.closeFav()}catch(e){}
			actived.style.display='none';
            df=actived.id.split('$');
            dc=ndl[df[1]]
            
            var idx=dc.idx;
            if ( tskBar )
            {
			document.getElementById("clo"+idx).className="taskBarClose";
			document.getElementById("ticRow"+idx).className="taskNormal";
			dc.hdrlabel.className="taskBar";
    		}
            
		}

	}
	actived=d;
	if(document.getElementById("containerTaskBar"))
	{
	  document.getElementById("containerTaskBar").checkBounds();
	}
	
}



waitat=[];
function createWDA( divObject , waitingAttribute )
{
	waitat[waitat.length]=[divObject,waitingAttribute];
}

function setWDA( waitingAttribute , value , docid )
{
	for ( var i=0 ; i< waitat.length ; i++ )
	{
		if ( waitat[i][1] == waitingAttribute )
		{
			getObjectsCardIdWLink( waitat[i][0] , value , docid );
			if ( waitat[i][0].parentElement )
			{
			waitat[i][0].parentElement.nextSibling.children[1].value=value;

			if (  waitat[i][0].parentElement.parentElement.parentElement.parentElement.parentElement.callBackObject )
			{
				waitat[i][0].parentElement.parentElement.parentElement.parentElement.parentElement.callBackObject.value=value;
				
			}
			}
			waitat[i][1]='null';
			waitat[i][0]=null;
		}
		
	}	
	
}



function getObjectsCardIdWLink( divObject , bouis ,docid )
{
   sendCmd("GETCARDIDWLINK","bouis="+bouis+"&docid="+docid ,divObject, setListenerField);
}

function setListenerField( object , values )
{
    if(object&&object.innerHTML)object.innerHTML=values.join("");
     
     if( object.onChange )
     {
		object.ownerDocument.parentWindow.eval( object.onChange );
     }
    
}

//***CMDS********************************//
var cmds=[];
var cmdsPool=[];

function sendCmd(xcmd,parameters,object,resultCall)
{
	
	cmds[cmds.length]=new Xcmd( cmds.length,xcmd,parameters,object,resultCall);
}

function Xcmd(index,xcmd,parameters,object,resultCall)
{
  this.cmd=xcmd;
  this.resultCall=resultCall;
  this.frm=null;
  this.idx=index;
  this.parameters=parameters;
  this.object=object;
  cmdsPool[ cmdsPool.length ]=this;
  sendCmds();
  
  
}

function sendCmds()
{
  if ( bocmds1.frameElement.readyState=='complete' )
  {
	 
     if ( cmdsPool.length > 0 )
     {
         var c=cmdsPool.pop();
 		 c.send(bocmds1);
     }
  }
  else
  {
    if ( cmdsPool.length > 0 )
    {
      window.setTimeout('sendCmds()',200);
    }
  }
}

Xcmd.prototype.send=function(f)
{
	this.frm=f.frameElement; // document.createElement("iframe");
    this.frm.onload=cmdResult;
    this.frm.onreadystatechange=cmdResult;  
    this.frm.style.display='none';
    this.frm.xid='cmd'+this.idx;
    
    this.frm.src="__cmds.jsp?cmd_id="+this.idx+"&cmd="+this.cmd+"&"+this.parameters; //NTRIMJS
}


function cmdResult()
{
	
	if (event.srcElement.readyState!='complete') return; 
		
	var idx=event.srcElement.xid.substr(3);
    var cmd=cmds[ parseInt(idx) ];
	var _results	= new ActiveXObject("Microsoft.XMLDOM");
	event.srcElement.onreadystatechange=null;
	_results.async	= false;
	
	_res=cmd.frm.contentWindow.document.getElementById('results');
	_results.loadXML(_res.innerHTML) ;
    _r = _results.selectSingleNode("*");
    
    r=[];
    
    for ( var i=0; i< _r.childNodes.length ; i++)
    {
		r[r.length]=_r.childNodes(i).firstChild.nodeValue;
		
    }
	cmd.resultCall(cmd.object, r);
}