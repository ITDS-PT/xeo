//<SCRIPT>

/*
function document.oncontextmenu()
{
	var s = event.srcElement.tagName;
	
	if (s && s != "INPUT" && s != "TEXTAREA" || event.srcElement.disabled || document.selection.createRange().text.length == 0)
	{
		event.returnValue = false;
	}
}
*/



document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
document.ondragstart=function(){event.returnValue = false}

isIE=true;
var G_onrsz=false;
var G_onmv=false;
var G_1;
var G_2;
var O_1;
var O_2;
var doc;
window.main=true;

function winmain(){
 return window;
}
function ngt_mousedown(){
  var x_obj,x_ndd,x_nsd,x_nfd;
  var ev=window.event;
  var e=ev.srcElement;
  var x_loc='';
  var df;
  if ( !e.id )
  {
	 while ( e )
	 {
	   e=e.parentElement;
	   if ( e && e.id && e.id.indexOf('$') >-1) break;
	 }
	 
  }
  
  if( e && e.id) {
        
        df=e.id.split('$');
        
        
        var xd=document.getElementById("doc$"+df[1])
        
       
        if(xd){
          var x_loc=df[0];
          if (x_loc!='min')   activedoc(xd,df[1]);
        }
        x_loc=df[0];
		if(x_loc=='rsz'){
		  G_onrsz=true;
		  G_1=ev.clientX;
		  G_2=ev.clientY;
		  doc=[];
		  doc['htm']=document.getElementById("doc$"+df[1]);
		  
		  doc['h']=doc['htm'].clientHeight;
		  doc['w']=doc['htm'].clientWidth;
		  doc['htm'].style.pixelWidth=doc['w'];
		  doc['htm'].style.pixelHeight=doc['h'];
		  G_aux2=doc['htm'].style.pixelHeight;
		  G_aux1=doc['htm'].style.pixelWidth;
		  _backColor=doc['htm'].runtimeStyle.backgroundColor;
	      doc['htm'].runtimeStyle.backgroundColor='transparent';
	      
          doc['htm'].firstChild.style.display='none';
          
		  doc['htm'].setCapture();
		  
		  //xRRR=doc['htm'].innerHTML;
		  //doc['htm'].innerHTML="";
		  
	    }
	    else if (x_loc.substr(0,3)=='hdr'){
	      G_onmv=true;
	      
	      doc=[];
	      doc['htm']=document.getElementById("doc$"+df[1]);
	      doc['htm'].setCapture();
	      _backColor=doc['htm'].runtimeStyle.backgroundColor;
	      doc['htm'].runtimeStyle.backgroundColor='transparent';
	      G_1=ev.clientY-doc['htm'].style.pixelTop;
		  G_2=ev.clientX-doc['htm'].style.pixelLeft;
		  //doc['htm'].firstChild.style.display='none';
          //doc['htm'].firstChild.style.visibility='hidden';
		  
	    }
  }

}


var doc;
var notRest=false;
var _backColor="";

function ngt_mouseclick(){
  var x_obj,x_ndd,x_nsd,x_nfd;
  var ev=window.event;
  var e=ev.srcElement;
  var x_loc='';
  var x_loc2='';
  if(e.id) {
        //x_loc=e.id.substring(0,3);
        df=e.id.split('$');
        x_loc=df[0];
        doc=[];
        if(df[1]){
            doc['htm']=document.getElementById("doc$"+df[1]);
            if(doc['htm'].style.zoom!="100%" &&doc['htm'].style.zoom!="" && !notRest){
                x_loc="rest";
                
            }
        }
        x_loc2=x_loc.substring(0,1);
        
		if(x_loc=='clo')
		{
		  ndl[df[1]].close();
		  ev.cancelBubble=true;
	    }
	    else if (x_loc=='max'){
	      ndl[df[1]].maximize();
	    }
	    else if (x_loc=='rest'){
	      ndl[df[1]].restore();
	    }
	    else if (x_loc=='min'){
	      ndl[df[1]].minimize();
	    }
	    

  }
  ++window.clickCount;

}


function ngt_mouseup(){
  var x_obj,x_ndd,x_nsd,x_nfd;
  var ev=window.event;
  var e=ev.srcElement;
  var x_loc='';
  if(G_onrsz) {
  G_onrsz=false;
// setTimeout("xi()",0);
    doc['htm'].firstChild.style.display='';
    doc['htm'].releaseCapture();
    doc['htm'].runtimeStyle.backgroundColor=_backColor;
  }
  if(G_onmv) {
     G_onmv=false;
     if(doc['htm'].firstChild.style.display=='none') doc['htm'].firstChild.style.display='';
     
     //doc['htm'].firstChild.style.visibility='visible';
     doc['htm'].releaseCapture();
     
	 doc['htm'].runtimeStyle.backgroundColor=_backColor;
      G_aux1=ev.clientY;
      G_aux2=ev.clientX;
      //doc['htm'].style.pixelTop=G_aux1-G_1;doc['htm'].style.pixelLeft=G_aux2-G_2;
    
      notRest=true;
      window.setTimeout("notRest=false",500);
  }

}


var G_aux1;
var G_aux2;

function ngt_mousemove(){
  var x_obj,x_ndd,x_nsd,x_nfd;
  var ev=window.event;
  
  var e=ev.srcElement;
  var x_loc='';
  
  if(G_onrsz){
       
       //doc['htm'].style.pixelWidth+=(ev.clientX-G_1);
       //doc['htm'].style.pixelHeight+=(ev.clientY-G_2);
       
       
       G_aux1+=ev.clientX-G_1;
       G_aux2+=ev.clientY-G_2;
       //window.clearTimeout();
       //window.setTimeout("
       doc['htm'].style.pixelWidth=G_aux1;
       doc['htm'].style.pixelHeight=G_aux2;
       //",50);
       
       if (ev.button==1){
         ev.returnValue = false;
         G_1=ev.clientX;
         G_2=ev.clientY
       }
       
  }
  else if(G_onmv){
       G_aux1=ev.clientY;
       G_aux2=ev.clientX;
       if(ev.button==0){
        G_onmv=false;
        return;
       }
       //doc['htm'].style.pixelTop=ev.clientY-G_1;
       //doc['htm'].style.pixelLeft=ev.clientX-G_2;
       //window.clearTimeout();
       //window.setTimeout("
       //doc['htm'].style.pixelTop=G_aux1-G_1;doc['htm'].style.pixelLeft=G_aux2-G_2
       var xd=doc['htm'];    
       if(xd.firstChild.style.display!='none') xd.firstChild.style.display='none';
       
       xd.style.pixelTop=Math.max(G_aux1-G_1,0);
       xd.style.pixelLeft=Math.max(G_aux2-G_2,xd.style.pixelWidth*-1+100);
       
       if ( xd.style.pixelLeft > window.document.body.offsetWidth-100){
       
        xd.style.pixelLeft = window.document.body.offsetWidth-100;
       }
       if ( xd.style.pixelTop > window.document.body.offsetHeight-50){
       
        xd.style.pixelTop = window.document.body.offsetHeight-50;
       }
       //",0);
       
       if (ev.button==1){
         ev.returnValue = false;      
       //  G_1=ev.clientX;
       //  G_2=ev.clientY
       }
  }
  
  
}


function ngt_keydown(){
  var ev=window.event;
  var e=ev.srcElement;

}

window.document.onmousedown=ngt_mousedown;
window.document.onmouseup=ngt_mouseup;
window.document.onmousemove=ngt_mousemove;
window.document.onclick=ngt_mouseclick;
window.document.onkeydown=ngt_keydown;

 

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




//x.formName="edit";
//x.obj="activity";




var obfl=[];

var xINDEX_ON=[];
var xINDEX_OFF=[];

xINDEX_ON['std']=20;
xINDEX_OFF['std']=10;


xINDEX_ON['lookup']=20;
xINDEX_OFF['lookup']=10;

xINDEX_ON['formula']=20;
xINDEX_OFF['formula']=10;


xINDEX_ON['walp']=5;
xINDEX_OFF['walp']=4;

	
function setStatus(xid,str){
  if ( ndl[xid] ){
    if (ndl[xid].msg) ndl[xid].msg.innerHTML=str;
  }
}


function getStatus(xid,str){
  if ( ndl[xid] ){
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
  
 
  if(ndl[xid].msg) ndl[xid].msg.innerHTML='';
  var x=eval('frm$'+xid);
  x.window.windowIDX=xid;
  ndl[xid].loaded=true;
  if (ndl[xid].htm.firstChild.style.display=='none')
  {
	ndl[xid].htm.firstChild.style.display='';
  }
  try {
	x.window.document.body.focus();
	}
  catch(e){}
  if(x.window.objLabel) {
    if(ndl[xid].hdrlabel) ndl[xid].hdrlabel.innerHTML=x.window.objLabel;
    //+(new Date()-ndl[xid].startTime )+"--"+(new Date()-ndl[xid].start2 ) ;
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
  activeDocByIdx(xid)
  if( x.boFormSubmit && x.boFormSubmit.toClose && x.boFormSubmit.toClose.value=='y' ){
    //if (x.body.onload)
    if (x.document.body.onload) {
        idToClose=xid;
        window.setTimeout("ndl[idToClose].close()",200);
    }
    else ndl[xid].close();
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
//events

function mouseOverImgClose(event ){getElement(event).src="templates/doc/std/buttclose_over.gif";}
function mouseOutImgClose(event ){getElement(event).src="templates/doc/std/buttclose.gif";}
function mouseOverImgMax(event ){getElement(event).src="templates/doc/std/buttmax_over.gif";}
function mouseOutImgMax(event ){getElement(event).src="templates/doc/std/buttmax.gif";}
function mouseOverImgMin(event ){getElement(event).src="templates/doc/std/buttmin_over.gif";}
function mouseOutImgMin(event ){getElement(event).src="templates/doc/std/buttmin.gif";}
function mouseOverImgRest(event ){getElement(event).src="templates/doc/std/buttrest_over.gif";}
function mouseOutImgRest(event ){getElement(event).src="templates/doc/std/buttrest.gif";}


function registEvent( o , eventName , eventHandler )
{
   if ( document.attachEvent )
   {
	  if ( eventName=="mouseover") eventName="onmouseover";
	  if ( eventName=="mouseout") eventName="onmouseout";
      o.attachEvent( eventName , eventHandler )
      
   }
   else //W3C mode
  {
      o.addEventListener( eventName , eventHandler , true )
  }

}

var dcache=document;	
function docWindow(idx,position,object,form,params,className,jspURL){
	this.position=position;
	this.object=object;
	this.form=form;
	this.params=params;
	this.idx=idx;
	
	
    this.status=STATUS_CREATING;
	if(className==null) className="std";
	this.className=className;
	this.loaded=false
//	var xh=fmtDocs[this.className];
//	xh=xh.replace(/#IDX#/g,idx);
	this.jspURL=jspURL;
    if(jspURL) this.url=jspURL;
    else
	this.url=this.object+"_general"+form+".jsp?";
	this.qry=params;
	
	if ( this.className!='walp') {
	//lixo();
	//return;
	}
	//xh=xh.replace(/#SRC#/g,this.url+this.qry);
  
	var d=document.createElement('div');
	this.htm=d;
	
	
	d.setAttribute("template","std");
	d.id='doc$'+idx;
	
	this.childs=[];
	d.className='doc_'+this.className;
	d.style.position='absolute';
	
	if(position==null || position==''){
	 position="small";
	}
	var lastTop=0;
	var lastLeft=0;
    var mW='780px';
    var mH='590px';
    
    var resize=true;
    
	if ( position.indexOf(',')!=-1 ){
      mW=position.split(',')[1];
      mH=position.split(',')[2];
      if ( position.indexOf('noresize') != -1 )
      {
       resize=false;
      }
      position='medium';
    }
    
	if(position=="medium"){
		
		if(actived&& (actived.style.zoom=='100%'||actived.style.zoom=='' )){
		   
           lastTop=actived.style.pixelTop;
		   lastLeft=actived.style.pixelLeft;
           if (lastLeft > window.document.body.offsetWidth-200) lastLeft = 100;
           if (lastTop > window.document.body.offsetHeight-250)lastTop = 100;
       

		}
		d.style.pixelTop=lastTop+21;
		d.style.pixelLeft=lastLeft+21;
		d.style.width=mW;
		d.style.height=mH;
	
		d.originalTop=lastTop+21;
		d.originalLeft=lastLeft+21;
		d.originalWidth=mW;
		d.originalHeight=mH;
	
	
	}
    else if(position=="small"){
		if(actived&& (actived.style.zoom=='100%'||actived.style.zoom=='' )){
		   lastTop=actived.style.pixelTop;
		   lastLeft=actived.style.pixelLeft;
           if (lastLeft > window.document.body.offsetWidth-200) lastLeft = 100;
           if (lastTop > window.document.body.offsetHeight-250)lastTop = 100;

		}
		d.style.pixelTop=lastTop+21;
		d.style.pixelLeft=lastLeft+21;
		d.style.width="500px";
		d.style.height="390px";
	
		d.originalTop=lastTop+21
		d.originalLeft=lastLeft+21
		d.originalWidth="500px";
		d.originalHeight="390px";
	
	
	}
	else if( position=="tall"){
		
		d.style.pixelTop=0;
		d.style.pixelLeft=90;
		d.style.width="470px";
		d.style.height="100%";
	
		d.originalTop=0
		d.originalLeft=90
		d.originalWidth="470px";
		d.originalHeight="100%";
	}
	else if( position=="full"){
		
		d.style.pixelTop=0;
		d.style.pixelLeft=0;
		d.style.width="100%";
		d.style.height="100%";

		d.originalTop=0
		d.originalLeft=0
		d.originalWidth="100%";
		d.originalHeight="100%";
		
	}
	else if( position=="large"){
		d.style.pixelTop=0;
		d.style.pixelLeft=0;
		d.style.width="90%";
		d.style.height="90%";
	
		d.originalTop=lastTop+0
		d.originalLeft=lastLeft+0
		d.originalWidth="90%";
		d.originalHeight="90%";
	
	}
	
	//****
	xtbl=document.createElement("TABLE");
	xtbl.cellPadding=0;
	xtbl.cellSpacing=0;
	
	xtbl.id='cnt$'+this.idx;
	xtbl.style.height='100%';
	xtbl.style.width='100%';
    var rn=0;
	if(this.className!='walp') xRowH=xtbl.insertRow(rn++);
	xRowB=xtbl.insertRow(rn++);
	if(this.className!='walp') xRowF=xtbl.insertRow(rn++);
	
	this.tit=null;
	this.hdrlabel=null;
	this.msg=null;
	
	if ( this.className!='walp')
	{
		
		cellH=xRowH.insertCell(0);
		cellH.style.width='100%';
		cellH.id='hdr_$'+this.idx;
		cellH.style.height='20px';
		
		cellH.style.width='100%';
		cellH.className='docHeaderTit_'+this.className;
		
		this.tit=cellH;
	
    
    
		var im =document.createElement("IMG");im.border='0';im.align='right';im.style.cursor='hand';
		im.src='templates/doc/std/buttclose.gif';
		registEvent( im , "mouseover" , mouseOverImgClose );
		registEvent( im , "mouseout" , mouseOutImgClose );
		im.width='16';im.height='16';im.id='clo$'+this.idx;
		cellH.appendChild(im);
		
		im =document.createElement("IMG");im.border='0';im.align='right';im.style.cursor='hand';
		im.src='templates/doc/std/buttmax.gif';
		registEvent( im , "mouseover" , mouseOverImgMax );
		registEvent( im , "mouseout" , mouseOutImgMax );
		im.width='16';im.height='16';im.id='max$'+this.idx;
		cellH.appendChild(im);
		
		im =document.createElement("IMG");im.border='0';im.align='right';im.style.cursor='hand';im.style.display='none';
		im.src='templates/doc/std/buttrest.gif';
		registEvent( im , "mouseover" , mouseOverImgRest );
		registEvent( im , "mouseout" , mouseOutImgRest );
		im.width='16';im.height='16';im.id='rest$'+this.idx;
		cellH.appendChild(im);
		
		im =document.createElement("IMG");im.border='0';im.align='right';im.style.cursor='hand';
		im.src='templates/doc/std/buttmin.gif';
		registEvent( im , "mouseover" , mouseOverImgMin );
		registEvent( im , "mouseout" , mouseOutImgMin );
		im.width='16';im.height='16';im.id='min$'+this.idx;
		cellH.appendChild(im);
		
		
		
		
		
		var xs=document.createElement("span");
		xs.id='hdr_label$'+this.idx;
		cellH.appendChild(xs);
		this.hdrlabel=xs;
		
	}
	cellB=xRowB.insertCell(0);
	cellB.id='dbody$'+this.idx;
	cellB.style.width='100%';
	cellB.style.height='100%';	
	
	if ( this.className!='walp')
	{
		
		cellF=xRowF.insertCell(0);
		cellF.style.width='100%';
		cellF.id='docFooter$'+this.idx;
		cellF.className='docFooter_'+this.className;
		cellF.style.height='20px';
		cellF.style.width='100%';
		
		im =document.createElement("IMG");im.border='0';im.align='right';im.style.cursor='se-resize';
		im.src='templates/doc/std/doc-rsz-std.gif';
		im.width='13';im.height='13';im.id='rsz$'+this.idx;
		cellH.appendChild(im);
		cellF.appendChild(im);
		
		var xs=document.createElement("span");
		xs.id='msgdoc$'+this.idx;
		cellF.appendChild(xs);
		this.msg=xs;
		

	}
		
	d.appendChild(xtbl);
	
	if ( this.url.indexOf("searchString") > -1 )
    {
		d.style.display='none';
		
    }
    
	dcache.getElementById("NGTBODY").appendChild(d);
      
       // d.innerHTML=xh;
             
    if ( !resize )
    {
        if(document.getElementById('docFooter$'+idx))
        {
            dcache.getElementById('docFooter$'+idx).style.display='none';
        }
    }
	if(this.msg)
	{
         //setStatus(idx,"<b>Abrindo documento </b><img src=resources/senddata.gif>");
         this.msg.innerHTML="<b>Abrindo documento </b><img src=resources/senddata.gif>";
    }
	
	this.hdrDescr=null //document.getElementById('hdr_descr$'+idx);
	this.savePress=false; //activo qdo o botão do save é carregado
    var dbody=cellB;  //document.getElementById('dbody$'+idx);
   	if ( pool.length > 0 ){
		    var xfrm=pool.pop();
		    //alert(1);
		}
		else{
		    var xfrm=dcache.createElement("iframe");
		    xfrm.onreadystatechange=loaded;
			xfrm.onload=loaded;
			xfrm.frameBorder=0;
			xfrm.scrolling='no';
			xfrm.width="100%";
			xfrm.height="100%";
		}
		xfrm.id="frm$"+idx;
		this.frm=xfrm;
		var xxsrc=this.url+this.qry;
		if(xxsrc.indexOf("?")==-1 ) 
		{
			 xfrm.src=this.url+this.qry+"?myIDX="+this.idx;
			 
		}
		else xfrm.src=this.url+this.qry+"&myIDX="+this.idx;
	
    dbody.appendChild(xfrm);
    this.status=STATUS_NORMAL;
	this.start2=new Date();
}

var toReload=null;


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
       this.url=xobject+"_general"+form+".jsp?";
    }   
	this.qry=params;
	var xxsrc=this.url+this.qry;
	if(xxsrc.indexOf("?")==-1 ) 
	{
		 xxsrc=this.url+this.qry+"?myIDX="+this.idx;
			 
	}
	else xxsrc=this.url+this.qry+"&myIDX="+this.idx;
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
docWindow.prototype.close=function(anyway){
  
  if( !this.loaded )
  {
    var x=this.frm;
	//var x=document.getElementById('frm$'+this.idx).contentWindow //  eval('frm$'+this.idx);
	if ( x) 
	{
		if ( this.htm )
		{
		var x1=document.getElementById('dbody$'+this.idx);
		if(x1)
		{
		//if( x.document)
		//{
		//x.document.open();
		//x.document.write("")
		//x.document.close();
	    //}
		 if(x.location) x.location.href="blank.htm";
    
		//x.location.replace("blank.htm");
		 pool[pool.length]=x1.removeChild(x1.firstChild);
		}
		 
		  //this.htm.parentElement.removeChild(this.htm);
		  this.htm.removeNode(true);
		  
		  this.dbody=null;
		  this.d=null;
		  this.position=null;
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
		  
		//  ndl[this.idx]=null;
		  x1=null;
		  actived=null;
		  var found=false;
		  
		  
		  for ( var i=this.idx-1 ; i>=0 && !found; i-- )
		  {
		     if(ndl[i] && ndl[i].htm && ndl[i].className!='walp')
		     {
		       activedoc( ndl[i].htm );
		       found=true;
		     }
		  }
		  if(!found )
		  {
			for ( var i=this.idx+1 ; i<ndl.length ; i++ )
			{
			   if(ndl[i] && ndl[i].htm && ndl[i].className!='walp') activedoc( ndl[i].htm );
			}
		  }
			ndl[this.idx]=null; 
			//setTimeout("destroyNDL("+this.idx+")",30 );
	
	
		
		
		return true
		}
		//CLOSE SERVERDOC ?
    }
 
  }
  
  var x=eval('frm$'+this.idx);//  document.getElementById('frm$'+this.idx).contentWindow ;
  //var x1=eval('dbody$'+this.idx);
  var x1=document.getElementById('dbody$'+this.idx)
  var verifyToClose=true;
  if ( x )
  {
	activedoc( this.htm );
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
   
// if(x &&( anyway || !x.boFormSubmit || !x.boFormSubmit.BOUI || !x.boForm || x.boForm.canClose()) )
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
             // var xd=document.getElementById('frm$'+ndl[i].idx).contentWindow ; ás vezes dá - object not support
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
	x.document.open();
	x.document.write("");
	x.document.close();
	
    //x.location.href="blank.htm";
    
    //x.location.replace("blank.htm");
    pool[pool.length]=x1.removeChild(x1.firstChild);
    this.frm=null;
    //x1.innerHTML="";  
   //x.removeNode(true);
    
  //}
  
  
  
  //this.htm.parentElement.removeChild(this.htm);
  this.htm.removeNode(true);
  
  this.dbody=null;
  this.d=null;
  this.position=null;
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
  
//  ndl[this.idx]=null;
  x1=null;
  actived=null;
  var found=false;
  
  
  for ( var i=this.idx-1 ; i>=0 && !found; i-- )
  {
     if(ndl[i] && ndl[i].htm && ndl[i].className!='walp')
     {
       activedoc( ndl[i].htm );
       found=true;
     }
  }
  if(!found )
  {
	for ( var i=this.idx+1 ; i<ndl.length ; i++ )
	{
	   if(ndl[i] && ndl[i].htm && ndl[i].className!='walp') activedoc( ndl[i].htm );
	}
  }
	//ndl[this.idx]=null; 
	
	setTimeout("destroyNDL("+this.idx+")",30 );
	//CollectGarbage();
	return true;
 }
 
 return false;
//  window.setTimeout('destroyNDL('+this.idx+')',30);
}
function destroyNDL(i)
{
ndl[i]=null;
}

docWindow.prototype.restore=function()
{			
	this.htm.className="doc_"+this.className;
	this.htm.style.top=this.htm.originalTop;
	this.htm.style.left=this.htm.originalLeft;
	this.htm.style.width=this.htm.originalWidth;
	this.htm.style.height=this.htm.originalHeight;
	var x=document.getElementById("max$"+this.idx);
	x.style.display='';
	var x=document.getElementById("rest$"+this.idx);
	x.style.display='none';
	this.htm.style.zoom="100%";
	this.status=STATUS_NORMAL
}

docWindow.prototype.maximize=function()
{
	
	this.htm.style.posTop=0;
	this.htm.style.posLeft=0;
	this.htm.style.width="100%";
	this.htm.style.height="100%";
	this.htm.style.zoom="100%";
	var x=document.getElementById("rest$"+this.idx);
	x.style.display='';
	var x=document.getElementById("max$"+this.idx);
	x.style.display='none';
	this.status=STATUS_MAXIMIZED;
}

docWindow.prototype.minimize=function()
{
	this.htm.style.top=window.document.body.clientHeight-300;
	this.htm.style.left=30;
	this.htm.style.width="200";
	this.htm.style.height="150";
	this.htm.style.zoom="30%";
	var x=document.getElementById("max$"+this.idx);
	x.style.display='';
	var x=document.getElementById("rest$"+this.idx);
	x.style.display='none';
	this.status=STATUS_MINIMEZED;
}

function existsOpenDoc()
{
 for ( var i=0 ; i < ndl.lenght ; i++) ; 
 {
	if ( ndl[i] && ndl[i].htm && ndl[i].status!= STATUS_MINIMEZED && ndl[i].className!='walp' )
	{
	   return true;
	}
 }
 return false;
}


function closeWALL()
{
    for ( var i=0 ; i< ndl.length ; i++ )
    {
        if( ndl[i] && ndl[i].object && ndl[i].object.toLowerCase().indexOf('wall')>-1 )
        {
            ndl[i].close(true);        
        }
    }
}
  
function openWALL(objID)
{
	openDoc("full",objID.toLowerCase(),"edit","method=new&object="+objID+"&menu=no","walp");
	//openDoc("full",objID,"edit","menu=no","walp");
}
  

var docServerToClose=[];
function closeServerDoc(xdocid)
{
    docServerToClose[docServerToClose.length]=xdocid;
    verifyCmdsSession();
}

function verifyCmdsSession()
{
if ( controlSession != null &&  controlSession.document != null && controlSession.document.readyState=='complete' ){
    if(docServerToClose.length > 0 ){
        controlSession.boFormSession.closeDocIds.value=docServerToClose.join(';');
        docServerToClose=[]; 
        controlSession.boFormSession.submit();
    }
}

}


function openModeDocUrl(idx, mode, position,jspURL,params,className,parentNDL){
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
	openDocUrl(position,jspURL,params,className,parentNDL);
}

function isopenDocUrl(position,jspURL,params,className,parentNDL){
    var found=false;
    if(!className) className="std";
    
    for(var i=0;i<ndl.length&&!found;i++)
    {
		found=ndl[i]&& ndl[i].htm && ndl[i].jspURL==jspURL &&  ndl[i].className==className && ndl[i].params==params
		if(found)
		{
			 return true; 
		}  
    }
	return false;    
}

function openDocUrl(position,jspURL,params,className,parentNDL){
   openDocUrl(position,jspURL,params,className,parentNDL, true);
}

function openDocUrl(position,jspURL,params,className,parentNDL,verifyIsOpen){
    var found=false;
    if(!className) className="std";

    if(verifyIsOpen)
    {
        for(var i=0;i<ndl.length&&!found;i++)
        {
		    found=ndl[i]&& ndl[i].htm && ndl[i].jspURL==jspURL &&  ndl[i].className==className && ndl[i].params==params
		    if(found)
		    {
			  weffect=ndl[i].htm;
			 
			  activedoc(ndl[i].htm); 
			  weffect.style.visibility='hidden';	 
			  window.setTimeout("weffect.style.visibility='visible'",50);
		    }  
        }
    }
    if (!found)
    {
		ndl[ ndl.length ]=new docWindow(ndl.length,position,"","",params,className,jspURL);
		if ( parentNDL && ndl[parentNDL] )
		{
			ndl[ parentNDL ].addChild( ndl.length-1 );
		}
		toFocus=ndl[ndl.length-1].htm;
		window.setTimeout('activedoc(toFocus)', 2000);
		activedoc(ndl[ndl.length-1].htm);
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


function openDoc(position,xobject,form,params,className,jspURL)
{
    var found=false;
    if(!className) className="std";
    
    if( params.indexOf("method=new")==-1)
    {
		for(var i=0; i<ndl.length && !found ; i++)
		{
			found=ndl[i]&& ndl[i].htm && ndl[i].object==xobject&&ndl[i].form==form && ndl[i].className==className && ndl[i].jspURL==jspURL &&ndl[i].params==params;
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
		ndl[ ndl.length ]=new docWindow(ndl.length,position,xobject,form,params,className,jspURL);
		activedoc(ndl[ndl.length-1].htm);
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
	dfa=d.id.split('$');
	var dc=ndl[df[1]];
	
	var xcls=ndl[df[1]].className;
	d.style.zIndex=xINDEX_ON[xcls];
    if(dc.className=='walp') return;
    
	if(dc.tit){
        dc.tit.className="docHeaderTit_"+dc.className+"SEL";
    }

	if( actived && d!=actived ){

		//actived.style.zIndex=10;
		df=actived.id.split('$');
		dc=ndl[df[1]];
		if(dc){
			var xcls=ndl[df[1]].className;
            
			actived.style.zIndex=xINDEX_OFF[xcls];
            df=actived.id.split('$');
            dc=ndl[df[1]]
    		if(dc.tit) {
               dc.tit.className="docHeaderTit_"+dc.className;
            }
            
            
			
            
            
		}

	}
	
	if ( ndl[dfa[1]] && ndl[dfa[1]].status==STATUS_MINIMEZED )
	{
		ndl[dfa[1]].restore();
	}
	actived=d;
}


//*****
//lookFields=[];
//function createLoopUpListenerField( o )
//{
//	lookFields[lookFields.length]=o;
//}


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
			
			//alert(waitingAttribute);
			if ( waitat[i][0].parentElement )
			{
			waitat[i][0].parentElement.nextSibling.children[1].value=value;

			if (  waitat[i][0].parentElement.parentElement.parentElement.parentElement.parentElement.callBackObject )
			{
				//cointaner o lookupobject
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
  if ( bocmds1.frameElement.readyState=='complete' ||  bocmds2.frameElement.readyState=='complete' )
  {
	 
     if ( cmdsPool.length > 0 )
     {
         var c=cmdsPool.pop();
         if ( bocmds1.frameElement.readyState=='complete' )
         {
			c.send(bocmds1);
		 }
		 else
		 {
		 	c.send(bocmds2);
		 }
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
    
    this.frm.src="__cmds.jsp?cmd_id="+this.idx+"&cmd="+this.cmd+"&"+this.parameters;
    //document.body.appendChild(this.frm);
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
	//eval(cmd.resultCall+"(r)");
	cmd.resultCall(cmd.object, r);
	
	
}