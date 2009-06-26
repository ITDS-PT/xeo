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
					this.delay = false;
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
		h[i++]="<img onclick='ndl["+idx+"].close()' src='ieThemes/0/taskBar/buttclose.gif' width='12px' height='12px'/>";
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

              if ( xd && xd.boFormSubmit && ( xd.boFormSubmit.docid.value == x.boFormSubmit.docid.value || ( xd.boFormSubmit.ctxParentIdx && xd.boFormSubmit.ctxParentIdx.value==x.boFormSubmit.docid.value ) ) ){
              
                continue_toClose=ndl[i].close();
                if (!continue_toClose) return false;
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
if (controlSession != null &&  controlSession.document != null && controlSession.document.readyState=='complete' ){
    if(docServerToClose.length > 0 ){
        controlSession.boFormSession.closeDocIds.value=docServerToClose.join(';');
        docServerToClose=[]; 
        controlSession.boFormSession.submit();
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
function proxyOpenDocUrl(features,jspURL,params,className,parentNDL,title){    
    var w = winmain();
    if(w.document.readyState != 'complete')
    {
        var cmd = "proxyOpenDocUrl('"+features+"','"+jspURL+"','"+params+"','"+className+"'";
        if(parentNDL)
        {
            cmd += ",'" + parentNDL + "'";
        }
        if(title)
        {
            if(parentNDL)
            {
                cmd += ",'" + title + "'";  
            }
            else
            {
                cmd += ",,'" + title + "'";  
            }
        }
        cmd += ")";  
        window.setTimeout(cmd,500);
    }
    else
    {
        winmain().openDocUrl(features,jspURL,params,className,parentNDL,title);
    }
}
function openDocUrl(features,jspURL,params,className,parentNDL,title){
    var found=false;
    
    if(!className) className="std";
    
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
function nextPage( idx , xobject,form,params)
{
   ndl[idx].nextPage( 'bind',xobject,form,params);
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
}var ORG_DATE_FORMAT=7;var ORG_DATE_SEPARATOR='/';var ORG_DATE_START_DAY=0;var ORG_LANGUAGE_CODE=1033;var ORG_NUMBER_FORMAT='pt';var ORG_TIME_FORMAT=0;var ORG_CURRENCY_SYMBOL='$';var ORG_SHOW_WEEK_NUMBER='0';var ON_SAVE_RESET=false;document.onselectstart=function(){var s=event.srcElement.tagName;if(s!="INPUT" && s!="TEXTAREA")event.returnValue=false;}
isIE=true;var G_onrsz=false;var G_onmv=false;var G_1;var G_2;var doc;var _hsos=[];var _hso=null;var ii=0;var lastActive=new Date('01/01/1970');function activethis(){if(new Date()- lastActive>1000)
{var xwin=winmain();if(xwin){xwin.status="ok..."+(ii++);if(xwin.activeDocByIdx)xwin.activeDocByIdx(getIDX())
}
}
lastActive=new Date();}
document.onmousedown=activethis;function getElement(e){if(e&&e.target)return e.target;return window.event.srcElement;}
function getToElement(e){if(e&&e.target)return e.relatedTarget;return window.event.toElement;}
function getRuntimeStyle(o){if(o.runtimeStyle)return o.runtimeStyle;else return o.style;}
function so(id){if(!_hsos[id]){_hsos[id]=new Object();	
_hsos[id].hb=document.getElementById(id+'_body');_hsos[id].id=id;}
_hso=_hsos[id];}
function openPopup()
{return window.createPopup();}
function Trim(s)
{return s.replace(/^\s+|\s+$/g,'');}
function loadArea(sArea, sParams, sUrl, throwe )
{try{setAreaWFocus(sArea, _hso.id);var tdAreas=document.getElementById('Areas_'+_hso.id);if(!_hso.oArea)_hso.oArea=tdAreas.firstChild;var o=document.getElementById(sArea);if(o)
{if(_hso.oArea && _hso.oArea.style )_hso.oArea.style.display="none";o.style.display="inline";var xfrms=o.getElementsByTagName('iframe');for (var i=0; i< xfrms.length ;i++){var xsrc=xfrms[i].getAttribute("xsrc");if(xfrms[i].getAttribute("src")&& xfrms[i].offsetHeight==0 && xfrms[i].getAttribute("ok")=='1' )
{var x=setUrlAttribute(xsrc,"docid",getDocId());xfrms[i].setAttribute("ok",'0');xfrms[i].style.height=xfrms[i].contentWindow.document.body.scrollHeight;}
if(!xfrms[i].getAttribute("src")&& xsrc ){var x=setUrlAttribute(xsrc,"docid",getDocId());xfrms[i].setAttribute("ok",'1');xfrms[i].setAttribute("src",x);}
}
}
selectArea(sArea);_hso.oArea=o;}catch(e){if(throwe ){throw e} };}
function selectArea(sArea)
{var areaId=sArea.substring(1);var areaOb=document.getElementById(areaId);if(areaOb!=null)
{areaOb.parentElement.down(areaOb);}
}
function winmain(){var Win=window;while (!Win.openDoc){if(Win==Win.parent ) return Win;Win=Win.parent;}
return Win;	
}
function getIDX(){var Win=window
var winidx=window.windowIDX;while (!Win.openDoc){if(Win==Win.parent ) return winidx;winidx=Win.windowIDX;Win=Win.parent;}
return winidx;}
function getReferenceFrame()
{if(!window.referenceFrame )
{if(boFormSubmit && boFormSubmit.boFormSubmitSecurity)
{window.referenceFrame=boFormSubmit.boFormSubmitSecurity.value;}
else window.referenceFrame=""+(new Date()-1);}
return window.referenceFrame;}
var tmpl;function removeTemplate()
{saveTemplateForm(true,true);}
function saveCloseTemplate()
{saveTemplateForm(true,false);}
function saveTemplateForm(toClose,destroy)
{if(!destroy )
{createHiddenInput("saveTemplate", "y" );}
else
{createHiddenInput("removeTemplate", "y" );}
if(toClose || destroy)createHiddenInput("toClose", "y" );boForm.BindValues();}
function createHiddenInput(name, value )
{if(!boFormSubmit[name] )
{var oInput=document.createElement("input" );oInput.name=name;oInput.id=oInput.name;oInput.type="hidden";oInput.value=value;boFormSubmit.appendChild(oInput );}
else
{boFormSubmit[name].value=value;}
return oInput;}
function deleteInput(oInput )
{boFormSubmit.removeNode(oInput );}
function setUrlAttribute(href,attname,value){var xqry=""
if(href.indexOf("?")>-1){xqry=href.substring(href.indexOf("?")+1);}
else
{return href+'?'+attname+"="+encodeURIComponent(value);}
var xargs=xqry.split("&");var fnd=false;for(var i=0;i<xargs.length;i++){if(xargs[i].split("=")[0]==attname){xargs[i]=attname+"="+encodeURIComponent(value);fnd=true;}
}
if(!fnd)xargs[xargs.length]=attname+"="+encodeURIComponent(value);return href.substring(0,href.indexOf("?")+1)+xargs.join("&");}
function getUrlAttribute(href,attname){var xqry=""
if(href.indexOf("?")>-1){xqry=href.substring(href.indexOf("?")+1);}
else
{return null;}
var xargs=xqry.split("&");var fnd=false;for(var i=0;i<xargs.length;i++){if(xargs[i].split("=")[0]==attname){var xs=xargs[i].split("=");xvalue=xs[1];for (var j=2 ; j< xs.length ; j++ )
{xvalue+="="+xs[j];}
return xvalue;}
}
return null;}
function removeUrlAttribute(href,attname){if(href.indexOf("?")>-1){xqry=href.substring(href.indexOf("?")+1);}
else{return href;}
var xargs=xqry.split("&");var fnd=false;var xargs2=[];for(var i=0;i<xargs.length;i++){if(xargs[i].split("=")[0]!=attname){xargs2[xargs2.length]=xargs[i];}
}
if(xargs2.length >0 ){return href.substring(0,href.indexOf("?")+1)+xargs2.join("&");}
else
{return href.substring(0,href.indexOf("?"));}
}

function previousPage()
{var xele=window.event.srcElement;var pageAct=parseInt(xele.nextSibling.nextSibling.innerText);if(pageAct >1 )
{if(boFormSubmit!=null)
{if(boFormSubmit.list_page==null)
{createHiddenInput("list_page",(pageAct-1)+"");}
else
{boFormSubmit.list_page.value=(pageAct-1)+"";}	
}
}
boFormSubmit.submit();}
function openShowModal(xUrl, xArgs, xPos, yPos)
{window.showModalDialog(xUrl , xArgs, "dialogWidth:" + xPos + "px;dialogHeight:" + yPos + "px;help:0;status:0;scroll:0;center:1");}
function getDocId()
{return document.forms["boFormSubmit"].docid.value;}
function startDragObject(objectName, boui ,exists , hui )
{event.dataTransfer.setData('Text','dragObject:'+objectName+":"+boui+":"+hui);event.dataTransfer.effectAllowed='copy';}
var attTOOL=null;var attLast='';function attachToolTipToMouse(str){attTOOL=eval("window.document.createElement('div');");attTOOL.className="tooltip";attTOOL.innerHTML=str;document.body.appendChild(attTOOL);document.onmousemove=displayToolTip;attLast=str;}
function removeToolTip(){try{if(attTOOL )document.body.removeChild(attTOOL);attTOOL=null;}catch(e){}
document.onmousemove=null;}
function displayToolTip()
{if(!attTOOL )
{attachToolTipToMouse(attLast );}
if(window.event && window.event.clientX && attTOOL.style.posLeft!=window.event.clientX+20)
{attTOOL.style.posLeft=window.event.clientX+20;}
if(window.event && window.event.clientY && attTOOL.style.posTop !=window.event.clientY+20)
{attTOOL.style.posTop=window.event.clientY+20;}
}
function runBeh(script , xvalor )
{var xscript= script.replace(/#VALUE#/g,xvalor); //NTRIMJS

eval(xscript);}
window.msgresponse=null;function newDialogBox(type ,message, options ,title )
{var x=1
var xh=160;var xtop=(window.screen.availheight-166)/2;window.msgresponse=null;window.showModalDialog("dialogBox.htm",new Array(window,message,options,type,title),"dialogTop:"+xtop+"px;dialogHeight:"+xh+"px;edge: raised;center: yes;  help: No; scroll: yes; resizable: yes; status: no;");return window.msgresponse;}
function displayXeoError(errorMessage)
{var resp=newDialogBox("critical",errorMessage,['Enviar por Email','Não Enviar'],"Xeo Critical Error" );if(resp==1)
{var t=window.navigator;x=window.open('mailto:suporte@enlace3.pt?subject=XEO_ERROR&body='+encodeURIComponent(errorMessage).substring(0,1500)+'\n\n Browser Settings: Cookie Enable :'+t.cookieEnabled+'\n Browser: '+t.appName+'\n Version : '+t.appVersion+'\n Agente: '+t.userAgent+'\n Plataform: '+t.platform+'\n Patchs '+t.appMinorVersion+'\n SystemLaguage : '+t.systemLanguage+'\n BrowserLanguage : '+t.browserLanguage+'\n UserLanguage : '+t.userLanguage+'' );}
}
function setFieldWFocus(fieldId)
{var frameId=window.frameElement;var key;if(getIDX()==null)
{return;}
if(winmain().ndl[getIDX()].focusfields==null)
{winmain().ndl[getIDX()].focusfields=[];}
if(frameId==null)
{key='parent';}
else
{key=frameId.id;}
var obj;if(winmain().ndl[getIDX()].focusfields[key]==null)
{obj=new Object();obj.focusArea="";obj.focusAreaNumber="";obj.focusTab="";obj.focusTabNumber="";}
else
{obj=winmain().ndl[getIDX()].focusfields[key];}
obj.focusField=fieldId;winmain().ndl[getIDX()].focusfields[key]=obj;}
function setAreaWFocus(areaId, areaNumber)
{var frameId=window.frameElement;var key;if(getIDX()==null)
{return;}
if(winmain().ndl[getIDX()].focusfields==null)
{winmain().ndl[getIDX()].focusfields=[];}
if(frameId==null)
{key='parent';}
else
{key=frameId.id;}
var obj;if(winmain().ndl[getIDX()].focusfields[key]==null)
{obj=new Object();}
else
{obj=winmain().ndl[getIDX()].focusfields[key];}
obj.focusArea=areaId;obj.focusAreaNumber=areaNumber;obj.focusTab="";obj.focusTabNumber="";obj.focusField="";winmain().ndl[getIDX()].focusfields[key]=obj;}
function setTabWFocus(tabId, tabNumber)
{var frameId=window.frameElement;var key;if(getIDX()==null)
{return;}
if(winmain().ndl[getIDX()].focusfields==null)
{winmain().ndl[getIDX()].focusfields=[];}
if(frameId==null)
{key='parent';}
else
{key=frameId.id;}
var obj;if(winmain().ndl[getIDX()].focusfields[key]==null)
{obj=new Object();}
else
{obj=winmain().ndl[getIDX()].focusfields[key];}
obj.focusTab=tabId;obj.focusTabNumber=tabNumber;obj.focusField="";winmain().ndl[getIDX()].focusfields[key]=obj;}
function treatFocus()
{try
{var frameId=window.frameElement;var key;if(getIDX()==null)return;if(ON_SAVE_RESET &&
winmain().ndl[getIDX()].savePress!=null &&
winmain().ndl[getIDX()].savePress)
{savePressed(false);return;}
if(winmain().ndl[getIDX()].focusfields==null)
{winmain().ndl[getIDX()].focusfields=[];}
if(frameId==null)
{key='parent';}
else
{key=frameId.id;}
var f=winmain().ndl[getIDX()].focusfields;if(f!=null && f[key]!=null )
{var fc=f[key];var lastArea=fc.focusArea;var lastAreaId=fc.focusAreaNumber;var lastTab=fc.focusTab;var lastTabId=fc.focusTabNumber;var lastField=fc.focusField;if(lastArea!=null && lastArea!="")
{var elems=document.getElementsByName(lastArea)
if(elems!=null && elems.length>0)
{so(lastAreaId);loadArea(lastArea);}
}
if(lastTab!=null && lastTab!="")
{var elems=document.getElementsByName(lastTab);if(elems!=null && elems.length>0)
{so(lastTabId);downTab_std(elems[0]);}
}
if(lastField!=null && lastField!="")
{loadField(lastField);}
}
}catch(e){}
}
var toFocus=null;function loadField(fieldId)
{try {var obj=document.getElementsByName(fieldId);if(obj!=null && obj.length>0)
{toFocus=obj[0];window.setTimeout("setFocusAtEnd(toFocus)",200);setFocusAtEnd(obj[0]);}
}catch(e){}
}
function savePressed(b)
{var wm=winmain();if(wm!=window)wm.ndl[getIDX()].savePress=b;}
function setFocusAtEnd(input )
{try {var sel;if(input.tagName=='IFRAME')
{input.contentWindow.focus();}
else
{input.focus();sel=input.createTextRange();}
sel.expand("textedit");sel.text+='';sel.collapse(false);}catch(e){}
}
function verifySize(input, size )
{if(input.value.length >=size)
{return false;}
return true;}
function doBeforePaste(input, size )
{if(size)
{return false;}
}
function doPaste(input, size )
{if(size){var oTR=input.document.selection.createRange();var iInsertLength=size - input.value.length + oTR.text.length;var sData=window.clipboardData.getData("Text").substr(0,iInsertLength);oTR.text=sData;return false;}
}
function setLastMethodToRun(methodName)
{if(getIDX()==null)
{return;}
winmain().ndl[getIDX()].lastMethodToRun=methodName;}
function setTabWFocusByName(areaName, tabName)
{try{setAreaWFocusByName(areaName);var spans=document.getElementsByTagName('SPAN');for (var i=0; i< spans.length;i++)
{if(spans[i].name==tabName)
{setTabWFocus(spans[i].id, spans[i].tabNumber);return;}
}
}catch(e){}
}
function setAreaWFocusByName(areaName)
{try{var divs=document.getElementsByTagName('DIV');for (var i=0; i< divs.length;i++)
{if(divs[i].name==areaName)
{setAreaWFocus(divs[i].relatedAreaId, divs[i].areaNumber);return;}
}
}catch(e){}
}
function wait()
{var x=document.getElementById("wait");var appx=x?false:true;if(!x )var x=document.createElement("<div id=wait style='background-image:url(resources/backsenddata.gif);position:absolute;vertical-align:middle;padding:10px;align:center;height:40px;width:250px;top:150px;color:#FFFFFF'></div>");x.style.left="10px";if(window.event)
{if(window.event.x-250<0)
{x.style.left=10;}
else
{x.style.left=window.event.x-250;}
x.style.top=window.event.y;}
x.style.zIndex=1300;x.innerHTML='<b>'+jsmessage_21+'</b><img src=resources/senddata.gif>'
D=document;if(appx)D.body.appendChild(x);else x.style.display='';var w=document.getElementById("waitdiv");var appw=w?false:true;if(!w) w=D.createElement("div");w.id='waitdiv';with(w.style){position='absolute';top=0;left=0;zIndex=1200;width=document.body.offsetWidth;height=document.body.offsetHeight;cursor="wait";backgroundImage='url(resources/none.gif)';}
D.body.style.cursor="wait";if(appw )D.body.appendChild(w);else  w.style.display='';}
function noWait()
{D=document;var x=document.getElementById("wait");if(x)x.style.display='none';var w=document.getElementById("waitdiv");if(w)w.style.display='none';D.body.style.cursor="default";}
function excludeParam(o)
{if(tableParamToExclude.style.display=='none')
{tableParamToExclude.style.display='';o.innerText="Esconder parâmetros";}
else
{tableParamToExclude.style.display='none';o.innerText="Mostrar parâmetros";}
}
function closeFav()
{if(window.fav )window.fav.style.display='none';iclsf=null;}
function favover()
{window.fav.style.display='';if(iclsf )clearTimeout(iclsf );}
var iclsf=null;function favout()
{iclsf=window.setTimeout("closeFav()",2000);}
function openFav(hfav )
{var oSrc=window.event.srcElement;var f=window.fav;if(f )
{	
if(f.parentObj!=oSrc.parentObj || f.parentBoui!=oSrc.parentBoui || f.parentAttribute!=oSrc.parentAttribute )
{window.fav.style.display='none';}
if(iclsf )clearTimeout(iclsf );}
var n1=0;var n2=0;var oPrnt=oSrc.parentElement;while (null!=oPrnt && oPrnt.tagName!='BODY')
{n1 +=(oPrnt.scrollTop);n2 +=(oPrnt.scrollLeft);oPrnt=oPrnt.parentElement;}
var topper=window.event.clientY+n1+document.body.scrollTop-window.event.offsetY-21;var lefter=window.event.clientX+n2+document.body.scrollLeft-270;if(!window.fav)
{window.fav=document.createElement("div");document.body.appendChild(fav);}
window.fav.oSrc=oSrc;var f=window.fav;f.onmouseover=favover;f.onmouseout=favout;f.innerHTML=hfav.innerHTML;f.fav=true;var s=f.style;s.display="";s.position='absolute';s.posTop=topper;s.zIndex=1000;s.posLeft=lefter;s.backgroundColor='#EEEEEE';s.border='1px solid #0000FF';s.filter="progid:DXImageTransform.Microsoft.Alpha(opacity=80)"
s.posWidth=250;f.lookupstyle=oSrc.lookupstyle;f.object=oSrc.object;f.parentObj=oSrc.parentObj;f.parentBoui=oSrc.parentBoui;f.parentAttribute=oSrc.parentAttribute;if(topper+f.offsetHeight>document.body.offsetHeight )
{s.posTop=document.body.offsetHeight-f.offsetHeight-30;}
}
function applyFav()
{var o=window.event.srcElement;while(o.tagName!='TABLE' )
{o=o.parentElement;}
if(o )
{var bouis="";for(var i=0;i< o.rows.length;i++ )
{var f=o.rows[i].cells[0].firstChild;if(f && f.tagName=='INPUT' && f.checked )
{if(bouis!="")bouis+=";";if(o.rows[i].boui!="")bouis+=o.rows[i].boui;}
}
var i=window.fav;var url="";url="lookup";url +=i.lookupstyle;url +=".jsp";url +="?look_object=" + i.object;url +="&showNew=false";url +="&docid="+getDocId();url +="&fromSection=y";url +="&clientIDX="+getIDX();url +="&look_parentObj="+i.parentObj;url +="&look_parentBoui="+i.parentBoui;url +="&searchString=b:"+bouis;url +="&look_parentAttribute="+i.parentAttribute;winmain().openDoc("tall",i.object,"","","lookup",url);closeFav();}
}
function calculatop(x_ele){var x_ret=0;if(x_ele.tagName=='BODY')return 0;else{if(x_ele.tagName=='TR' )x_ret=0+calculatop(x_ele.parentElement);else x_ret=x_ele.offsetTop-x_ele.scrollTop+calculatop(x_ele.parentElement);}
return x_ret
}
function calculaleft(x_ele){var x_ret=0;if(!x_ele)return null;x_ret=x_ele.offsetLeft-x_ele.scrollLeft+calculaleft(x_ele.parentElement);return x_ret
}
function cleft(x_ele){var x_ret=0;if(x_ele.tagName=='BODY')return 0;else x_ret=x_ele.offsetLeft-x_ele.scrollLeft+cleft(x_ele.parentElement);return x_ret;}
function ctop(x_ele)
{var x_ret=0;if(!x_ele || x_ele.tagName=='BODY'|| x_ele.tagName=='FORM')
{return 0 ;}
else
{if(x_ele.tagName=='TR' ||x_ele.tagName=='DIV' )x_ele=x_ele.parentElement;else
{x_ret=x_ele.offsetTop-x_ele.scrollTop;}
x_ret+=ctop(x_ele.parentElement);return x_ret;}
}
var colorRowOver="#ADC3E7";/*
function showAndHide(id)
{var o=window.event.srcElement;var src=o.src;if(id.style.display=="none")
{id.style.display="";if(o.id=='showAndHideImage')
{o.src=src.replace('more.gif','minus.gif');}
}
else
{id.style.display="none";if(o.id=='showAndHideImage')
{o.src=src.replace('minus.gif','more.gif');}
}
}
function changeHeight(id)
{if(id.style.height=="")
{id.style.height="100px";}
else
{id.style.height="";}
}
*/
function setActionCode(code)
{boFormSubmit.actionCode.value=code;}
/*
function setShowWorkFlowActivity(boui)
{boFormSubmit.showWorkFlowActivity.value=boui;}*/
function setStateActivity(boui)
{boFormSubmit.stateActivityBoui.value=boui;}
function setViewerType(type)
{boFormSubmit.xwfViewerType.value=type;boForm.BindValues();}
function setActivityChoiceValue()
{var value;var elems=document.getElementsByName("xwfChoice");for (var i=0 ; i<elems.length && value==null; i++ )
{if(elems[i].checked)
{value=elems[i].id;}
}
if(value!=null)
{parent.boFormSubmit.xwfActivityValue.value=value;parent.boForm.BindValues();}
else
{alert("Tem de escolher uma opção antes de confirmar");}
}
function setActivityValue(value)
{parent.boFormSubmit.xwfActivityValue.value=value;parent.boForm.BindValues();parent.savePressed(true);}
function updateFrameById(frameId,parameter,code)
{if(code==null)
{code=0;}
var frame=document.getElementById(frameId);if(frame!=null)
{var repalceSrc=frame.src;var reg;if(repalceSrc.indexOf(parameter+"=-")!=-1)
{reg=new RegExp("\\b"+parameter+"=-[0-9]*&", "g");}
else
{reg=new RegExp("\\b"+parameter+"=[0-9]*&", "g");}
var newSrc=repalceSrc.replace(reg , parameter + "=" + code + "&");frame.src=newSrc;return}
}
function runBeforeMethodExec(methodName)
{try{
for( vari=0 ;i< ndl.length;i++)
{ndl[i].frm.contentWindow.runBeforeMethodExec(methodName);}
}catch(e){}
}
function runAfterMethodExec()
{try{for( var i=0 ; i< ndl.length; i++)
{ndl[i].frm.contentWindow.runAfterMethodExec();}
}catch(e){}
}
function setLastMethodToRun(methodName)
{try{for( var i=0 ; i< ndl.length; i++)
{ndl[i].lastMethodToRun=methodName;}
}catch(e){}
}