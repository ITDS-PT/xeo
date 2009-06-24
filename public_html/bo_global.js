//<SCRIPT>
var ORG_DATE_FORMAT = 7;
var ORG_DATE_SEPARATOR = '/';
var ORG_DATE_START_DAY = 0;
var ORG_LANGUAGE_CODE = 1033;
var ORG_NUMBER_FORMAT = 'pt'; //or us
var ORG_TIME_FORMAT = 0;
var ORG_CURRENCY_SYMBOL = '$';
var ORG_SHOW_WEEK_NUMBER = '0';
var ON_SAVE_RESET = false; //se true - não executa o setFocus no gravar

//begin boheaders
document.onselectstart=function(){var s = event.srcElement.tagName;if (s != "INPUT" && s != "TEXTAREA") event.returnValue = false;}
isIE=true;
var G_onrsz=false;
var G_onmv=false;
var G_1;
var G_2;
var doc;


var _hsos=[];
var _hso=null;
var ii=0;
var lastActive=new Date('01/01/1970');
function activethis(){
 if( new Date() - lastActive > 1000)
 {
    var xwin=winmain();
    if(xwin){
        xwin.status="ok..."+(ii++);
        if(xwin.activeDocByIdx) xwin.activeDocByIdx(getIDX())
    }
  }
  lastActive=new Date();
}

document.onmousedown=activethis;

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
//end boaheaders



function openPopup()
{
	return window.createPopup();
}

//UTILS
function Trim(s)
{
	return s.replace(/^\s+|\s+$/g,'');
}

function loadArea(sArea, sParams, sUrl, throwe )
{
    try{
    setAreaWFocus(sArea, _hso.id);
	var tdAreas=document.getElementById('Areas_'+_hso.id);

	if (!_hso.oArea) _hso.oArea = tdAreas.firstChild;
	var o = document.getElementById(sArea);
	
	if (o)
	{
	//	if (o == _hso.oArea) return false;
	   
		if(_hso.oArea && _hso.oArea.style ) _hso.oArea.style.display = "none";
		o.style.display = "inline";
				 
		var xfrms=o.getElementsByTagName('iframe');
		
		for ( var i=0; i< xfrms.length ;i++){
		  var xsrc=xfrms[i].getAttribute("xsrc");
		  
		  if (xfrms[i].getAttribute("src") && xfrms[i].offsetHeight==0 && xfrms[i].getAttribute("ok")=='1' )
		  {
		   
			//workaround para o caso em que o tamanho das frames são calculados em código e quando a 
			//area fica invisivel no onload (activa outra por defeito ) -> caso dos attributos extendidos
			//if( !xsrc ) xsrc=xfrms[i].getAttribute("src");
		    var x=setUrlAttribute(xsrc,"docid",getDocId());

		    
		    xfrms[i].setAttribute("ok",'0');
		    xfrms[i].style.height=xfrms[i].contentWindow.document.body.scrollHeight;
		   
		    //como está funciona mas se vier a dar problemas deve-se colocar
		     //xfrms[i].setAttribute("src",x);
		  }
		 if ( !xfrms[i].getAttribute("src") && xsrc ){
		 
		      var x=setUrlAttribute(xsrc,"docid",getDocId());
		      xfrms[i].setAttribute("ok",'1');
		  	  xfrms[i].setAttribute("src",x);
		  }
		  
		}
	}
	selectArea(sArea);
	_hso.oArea = o;
    }catch(e){if ( throwe ) {throw e} };
}

function selectArea(sArea)
{
    var areaId = sArea.substring(1);
    var areaOb=document.getElementById(areaId);
    if(areaOb != null)
    {
        areaOb.parentElement.down(areaOb);    
    }
}
		
		
function winmain(){
	var Win=window;
	while (!Win.openDoc){
	   if( Win==Win.parent )  return Win;
	   Win=Win.parent;
		  
	}
	return Win;	
	
}

function getIDX(){
    var Win=window
    var winidx=window.windowIDX;
	while (!Win.openDoc){
	  if( Win==Win.parent )  return winidx;
	  winidx=Win.windowIDX;
	  Win=Win.parent;
	  
	}
	return winidx;
}

function getReferenceFrame()
{
	if ( !window.referenceFrame ) 
	{
		if ( boFormSubmit && boFormSubmit.boFormSubmitSecurity)
		{
			window.referenceFrame=boFormSubmit.boFormSubmitSecurity.value;
		}
		else window.referenceFrame=""+(new Date()-1);
	}
	
	return window.referenceFrame;
}


var tmpl;

function removeTemplate()
{
    saveTemplateForm(true,true);
}



function saveCloseTemplate()
{
    saveTemplateForm(true,false);
}


function saveTemplateForm(toClose,destroy)
{
    if( !destroy )
    {
        createHiddenInput( "saveTemplate", "y" );
    }
    else
    {
        createHiddenInput( "removeTemplate", "y" );
    }
    if( toClose || destroy) createHiddenInput( "toClose", "y" );
    boForm.BindValues();
  
}

function createHiddenInput( name, value )
{
    if( !boFormSubmit[name] )
    {
        var oInput = document.createElement( "input" );
        oInput.name = name;
        oInput.id = oInput.name;
        oInput.type = "hidden";
        oInput.value = value;
        boFormSubmit.appendChild( oInput );
	}
    else
    {
        boFormSubmit[name].value=value;
    }
	return oInput;
}

		
//
// Removes inputs added with 'createHiddenInput'
function deleteInput( oInput )
{
	boFormSubmit.removeNode( oInput );
}

function setUrlAttribute(href,attname,value) {
	var xqry=""
	if( href.indexOf("?") > -1) {
		xqry = href.substring(href.indexOf("?")+1);
	}
	else
	{
	 return href+'?'+attname+"="+encodeURIComponent(value);
	}
	
    var xargs = xqry.split("&");
    var fnd = false;
    for(var i=0;i<xargs.length;i++) {
        if(xargs[i].split("=")[0]==attname) {
            xargs[i] = attname+"="+encodeURIComponent(value);
            fnd = true;
        }
    }
    if(!fnd) xargs[xargs.length] = attname+"="+encodeURIComponent(value);

    return href.substring(0,href.indexOf("?")+1)+xargs.join("&"); 
        
}

function getUrlAttribute(href,attname) {
	var xqry=""
	if( href.indexOf("?") > -1) {
		xqry = href.substring(href.indexOf("?")+1);
	}
	else
	{
	 return null;
	}
	
    var xargs = xqry.split("&");
    var fnd = false;
    for(var i=0;i<xargs.length;i++) {
        if(xargs[i].split("=")[0]==attname) {
			var xs=xargs[i].split("=");
            xvalue=xs[1];
            
            for ( var j=2 ; j< xs.length ; j++ )
            {
               xvalue+="="+xs[j];
            
            }
            return xvalue;
            
        }
    }
    return null;
        
}


function removeUrlAttribute(href,attname) {
    if( href.indexOf("?") > -1) {
		xqry = href.substring(href.indexOf("?")+1);
	}
	else{
	    return href;
	}
	
	var xargs = xqry.split("&");
    var fnd = false;
    var xargs2=[];
    for(var i=0;i<xargs.length;i++) {
        if(xargs[i].split("=")[0]!=attname) {
             xargs2[xargs2.length]=xargs[i];
          }
    }
    
    if( xargs2.length >0 ){
		return href.substring(0,href.indexOf("?")+1)+xargs2.join("&"); 
    }
    else
    {
      return href.substring(0,href.indexOf("?"));
    }
	
	
}


function nextPage()
{
  var xele=window.event.srcElement;
  var pageAct=parseInt(xele.previousSibling.innerText);
  //window.location.href=setUrlAttribute(window.location.href,"list_page",(pageAct+1)+"");
  if(boFormSubmit != null)
  {
    if(boFormSubmit.list_page == null)
    {
        createHiddenInput("list_page",(pageAct+1)+"");
    }
    else
    {
        boFormSubmit.list_page.value = (pageAct+1)+"";
    }	    
  }  
  boFormSubmit.submit();
}

function previousPage()
{
  var xele=window.event.srcElement;
  var pageAct=parseInt(xele.nextSibling.nextSibling.innerText);
  if( pageAct >1 )
  {
    // window.location.href=setUrlAttribute(window.location.href,"list_page",(pageAct-1)+"");
    if(boFormSubmit != null)
    {
        if(boFormSubmit.list_page == null)
        {
            createHiddenInput("list_page",(pageAct-1)+"");
        }
        else
        {
            boFormSubmit.list_page.value = (pageAct-1)+"";
        }	    
    }
  }
  boFormSubmit.submit();
}

function openShowModal(xUrl, xArgs, xPos, yPos)
{
	
	window.showModalDialog( xUrl , xArgs, "dialogWidth:" + xPos + "px;dialogHeight:" + yPos + "px;help:0;status:0;scroll:0;center:1");
}



function getDocId()
{
  return document.forms["boFormSubmit"].docid.value; 
}

	
		
function startDragObject( objectName, boui ,exists , hui )
{
 event.dataTransfer.setData('Text','dragObject:'+objectName+":"+boui+":"+hui);
 event.dataTransfer.effectAllowed = 'copy';
}


var attTOOL=null;
var attLast='';
function attachToolTipToMouse(str){
  
  attTOOL=eval("window.document.createElement('div');");
  attTOOL.className="tooltip";
  attTOOL.innerHTML=str;
  document.body.appendChild(attTOOL);
  document.onmousemove=displayToolTip;
  attLast=str;
  
}
function removeToolTip(){
  
  
  //document.onmousemove=displayToolTip;
  try{
  if ( attTOOL ) document.body.removeChild(attTOOL); 
  attTOOL=null;
  }catch(e){}
  document.onmousemove=null;
  
}
function displayToolTip()
{
	if (!attTOOL )
	{
	   attachToolTipToMouse( attLast );
	}
		if( window.event && window.event.clientX && attTOOL.style.posLeft!=window.event.clientX+20)
		{
			attTOOL.style.posLeft=window.event.clientX+20;  
		}
		if( window.event && window.event.clientY && attTOOL.style.posTop !=window.event.clientY+20)
		{
			attTOOL.style.posTop=window.event.clientY+20;
   	    }
	
}


function runBeh( script , xvalor )
{
 var xscript= script.replace(/#VALUE#/g,xvalor); //NTRIMJS
 eval(xscript);
 
}


window.msgresponse=null;

function newDialogBox( type ,message, options ,title )
  {
  var x=1
  var xh=160;
  var xtop=(window.screen.availheight-166)/2;
  window.msgresponse=null;
  window.showModalDialog("dialogBox.htm",new Array(window,message,options,type,title),"dialogTop:"+xtop+"px;dialogHeight:"+xh+"px;edge: raised;center: yes;  help: No; scroll: yes; resizable: yes; status: no;");
  return window.msgresponse;
}

function displayXeoError(errorMessage)
{
   var resp=newDialogBox("critical",errorMessage,[jsmessage_22,jsmessage_23],"Xeo Critical Error" );
   
   if ( resp == 1)
   {
	   var t=window.navigator;
	   //errorMessage=errorMessage.replace(/\n/g,"<br/>");
	   //errorMessage=errorMessage.replace(/\l/g,"");
	   
	  // alert( encodeURIComponent(errorMessage));
		x=window.open('mailto:suporte@enlace3.pt?subject=XEO_ERROR&body='+encodeURIComponent(errorMessage).substring(0,1500)+'\n\n Browser Settings: Cookie Enable :'+t.cookieEnabled+'\n Browser: '+t.appName+'\n Version : '+t.appVersion+'\n Agente: '+t.userAgent+'\n Plataform: '+t.platform+'\n Patchs '+t.appMinorVersion+'\n SystemLaguage : '+t.systemLanguage+'\n BrowserLanguage : '+t.browserLanguage+'\n UserLanguage : '+t.userLanguage+'' ) ;
		
   }

}

function setFieldWFocus(fieldId)
{
    var frameId = window.frameElement;
    var key;

    if(getIDX() == null)
    {
        return;
    }
    if(winmain().ndl[getIDX()].focusfields == null)
    {
        winmain().ndl[getIDX()].focusfields =[];
    }
    if(frameId == null)
    {
        key = 'parent';
    }
    else
    {
        key = frameId.id;    
    }
    var obj;
    if(winmain().ndl[getIDX()].focusfields[key] == null)
    {
        obj = new Object();
        obj.focusArea = "";
        obj.focusAreaNumber = "";
        obj.focusTab = "";
        obj.focusTabNumber = "";
    }
    else
    {
        obj = winmain().ndl[getIDX()].focusfields[key];
    }
    obj.focusField = fieldId;
    winmain().ndl[getIDX()].focusfields[key] = obj;
}

function setAreaWFocus(areaId, areaNumber)
{
    var frameId = window.frameElement;
    var key;
    
    if(getIDX() == null)
    {
         return;
    }
    if(winmain().ndl[getIDX()].focusfields == null)
    {
        winmain().ndl[getIDX()].focusfields =[];
    }
    if(frameId == null)
    {
        key = 'parent';
    }
    else
    {
        key = frameId.id;    
    }
    var obj;
    if(winmain().ndl[getIDX()].focusfields[key] == null)
    {
        obj = new Object();
    }
    else
    {
        obj = winmain().ndl[getIDX()].focusfields[key];
    }
    
    obj.focusArea = areaId;
    obj.focusAreaNumber = areaNumber;
    obj.focusTab = "";
    obj.focusTabNumber = "";
    obj.focusField = "";

    winmain().ndl[getIDX()].focusfields[key] = obj;
}

function setTabWFocus(tabId, tabNumber)
{
    var frameId = window.frameElement;
    var key;
    if(getIDX() == null)
    {
        return;
    }
    if(winmain().ndl[getIDX()].focusfields == null)
    {
        winmain().ndl[getIDX()].focusfields =[];
    }
    if(frameId == null)
    {
        key = 'parent';
    }
    else
    {
        key = frameId.id;    
    }
    var obj;
    if(winmain().ndl[getIDX()].focusfields[key] == null)
    {
        obj = new Object();
    }
    else
    {
        obj = winmain().ndl[getIDX()].focusfields[key];
    }
    obj.focusTab = tabId;
    obj.focusTabNumber = tabNumber;
    obj.focusField = ""; 
    winmain().ndl[getIDX()].focusfields[key] = obj;
}

function treatFocus()
{   
    try
    {
    var frameId = window.frameElement;
    var key;
    if(getIDX() == null) return;
    
    if(ON_SAVE_RESET &&
        winmain().ndl[getIDX()].savePress != null && 
        winmain().ndl[getIDX()].savePress)
    {        
        savePressed(false);
        return;
    }
    
    if(winmain().ndl[getIDX()].focusfields == null)
    {
        winmain().ndl[getIDX()].focusfields =[];
    }
    if(frameId == null)
    {
        key = 'parent';
    }
    else
    {
        key = frameId.id;    
    }
	var f= winmain().ndl[getIDX()].focusfields;
    if( f!= null && f[key] != null )
    {
		var fc= f[key];
        var lastArea = fc.focusArea;
        var lastAreaId = fc.focusAreaNumber;
        var lastTab = fc.focusTab;
        var lastTabId = fc.focusTabNumber;
        var lastField = fc.focusField;
        
        if(lastArea != null && lastArea != "")
        {   
            var elems = document.getElementsByName(lastArea)
            if(elems != null && elems.length > 0)
            {
                so(lastAreaId);
                loadArea(lastArea);
            }
        }
        if(lastTab != null && lastTab != "")
        {   
            var elems = document.getElementsByName(lastTab);
            if(elems != null && elems.length > 0)
            {
                so(lastTabId);
                downTab_std(elems[0]);
            }
        }
        
        if(lastField != null && lastField != "")
        {
           loadField(lastField);
        }
    }
    }catch(e){}
}

var toFocus=null;
function loadField(fieldId)
{
    try {
    var obj = document.getElementsByName(fieldId);
    if(obj != null && obj.length > 0)
    {        
            toFocus=obj[0];
            window.setTimeout("setFocusAtEnd(toFocus)",200);
            setFocusAtEnd( obj[0]);        
    }
    }catch(e){}
}

function savePressed(b)
{
    var wm=winmain();
    if(wm!=window) wm.ndl[getIDX()].savePress = b;
}

function setFocusAtEnd( input )
{
    try {
     var sel;
     if(input.tagName == 'IFRAME')
     {
        input.contentWindow.focus();
        //sel = input.contentWindow.document.body.createTextRange();
     }
     else
     {   
    	 input.focus();
    	 sel = input.createTextRange();
      }
      sel.expand("textedit");
      sel.text+='';
      sel.collapse(false);
     }catch(e){}
}
function verifySize( input, size )
{
    if(input.value.length >= size)
    {  
        return false;
    }
    return true;
}

function doBeforePaste( input, size )
{
    if(size)
    {  
        return false;
    }
}

function doPaste( input, size )
{
     if(size){
          var oTR = input.document.selection.createRange();
          var iInsertLength = size - input.value.length + oTR.text.length;
          var sData = window.clipboardData.getData("Text").substr(0,iInsertLength);
          oTR.text = sData;
          return false;
     }
}

function setLastMethodToRun(methodName)
{
    if(getIDX() == null)
    {
        return;
    }
    winmain().ndl[getIDX()].lastMethodToRun = methodName;
}

function setTabWFocusByName(areaName, tabName)
{
    try{
    setAreaWFocusByName(areaName);
    var spans = document.getElementsByTagName('SPAN');
    for ( var i=0; i< spans.length;i++)
    {
	    if ( spans[i].name == tabName)
	    {
            setTabWFocus(spans[i].id, spans[i].tabNumber);
            return;
		}
    }
    }catch(e){}
}

function setAreaWFocusByName(areaName)
{
    try{
    var divs = document.getElementsByTagName('DIV');
    for ( var i=0; i< divs.length;i++)
    {
	    if ( divs[i].name == areaName)
	    {
            setAreaWFocus(divs[i].relatedAreaId, divs[i].areaNumber);
            return;
		}
    }
    }catch(e){}
}

function wait()
{
	var x=document.getElementById("wait");
	var appx=x?false:true;
	if ( !x ) var x=document.createElement("<div id=wait style='background-image:url(resources/backsenddata.gif);position:absolute;vertical-align:middle;padding:10px;align:center;height:40px;width:250px;top:150px;color:#FFFFFF'></div>");
	x.style.left="10px";
	if(window.event)
	{
	  if ( window.event.x-250 < 0)
	  {
	  x.style.left=10;
	  }
	  else
	  {
	  x.style.left=window.event.x-250;
	  }
	  
	  
	  x.style.top=window.event.y;
	}
	x.style.zIndex=1300;
	x.innerHTML='<b>'+this.parent.jsmessage_21+'</b><img src=resources/senddata.gif>'
	D=document;
	if(appx) D.body.appendChild(x);
	else x.style.display='';
	var w=document.getElementById("waitdiv");
	var appw=w?false:true;
	if(!w)  w=D.createElement("div");
	w.id='waitdiv';
	with(w.style){
	   position='absolute';
	   top=0;
	   left=0;
	   zIndex=1200;
	   width=document.body.offsetWidth;
	   height=document.body.offsetHeight;
	   cursor="wait";
	   backgroundImage='url(resources/none.gif)';
	   }
  D.body.style.cursor = "wait";
  if ( appw )D.body.appendChild(w);
  else  w.style.display='';
//  D.body.disabled=true;
}
function noWait()
{
	D=document;
	var x=document.getElementById("wait");
	if(x)x.style.display='none';
	var w=document.getElementById("waitdiv");
	if(w)w.style.display='none';
	D.body.style.cursor = "default";

}
function excludeParam(o)
{
	if ( tableParamToExclude.style.display=='none')
	{
		tableParamToExclude.style.display='';
		o.innerText=jsmessage_24;
	}
	else
	{
		tableParamToExclude.style.display='none';
 		o.innerText=jsmessage_25;
	}
}

function closeFav()
{
	if( window.fav ) window.fav.style.display='none';
	iclsf=null;
	
}
function favover()
{
	window.fav.style.display='';
	if( iclsf )clearTimeout( iclsf );
}
var iclsf=null;
function favout()
{
	
	iclsf = window.setTimeout("closeFav()",2000);
	//closeFav();
	
}


function openFav( hfav )
{
	var oSrc=window.event.srcElement;
	var f=window.fav;
	if( f )
	{	
		if( f.parentObj!=oSrc.parentObj || f.parentBoui != oSrc.parentBoui || f.parentAttribute!= oSrc.parentAttribute )
		{
		 window.fav.style.display='none';
		}
		if( iclsf )clearTimeout( iclsf );
		
	}
	
	var n1=0;
	var n2=0;
	var oPrnt = oSrc.parentElement;
	while (null != oPrnt && oPrnt.tagName!='BODY')
	{
		n1 += (oPrnt.scrollTop);
		n2 += (oPrnt.scrollLeft);
		oPrnt = oPrnt.parentElement;
	}
	var topper = window.event.clientY+n1+document.body.scrollTop-window.event.offsetY-21; //n; //calculatop( window.event.srcElement); // n; //oPrnt.offsetTop+ n - window.event.offsetY+this.offsetHeight ;
	var lefter = window.event.clientX+n2+document.body.scrollLeft-270;
	//fav.style.display='none';
	
	if(!window.fav)
	{
		window.fav =document.createElement("div");
		document.body.appendChild(fav);
	 }
	window.fav.oSrc=oSrc;
	var f=window.fav;
	
	f.onmouseover=favover;
	f.onmouseout=favout;
	f.innerHTML=hfav.innerHTML;
	f.fav=true;
 	var s=f.style;
	s.display="";

	s.position='absolute';
	s.posTop=topper; //-40; //+this.offsetHeight;
	s.zIndex=1000;
	s.posLeft=lefter;
	
	s.backgroundColor='#EEEEEE';
	s.border='1px solid #0000FF';
	s.filter="progid:DXImageTransform.Microsoft.Alpha(opacity=80)"
	s.posWidth=250;
	
	f.lookupstyle=oSrc.lookupstyle;
	f.object=oSrc.object;
	f.parentObj=oSrc.parentObj;
	f.parentBoui=oSrc.parentBoui;
	f.parentAttribute=oSrc.parentAttribute;
	if(topper+f.offsetHeight > document.body.offsetHeight )
	{
		s.posTop= document.body.offsetHeight-f.offsetHeight-30;
	}
	
}
function applyFav()
{
	var o = window.event.srcElement;
	while( o.tagName != 'TABLE' )
	{
		o=o.parentElement;
	}
	if(o )
	{
		var bouis="";
		for(var i=0;i< o.rows.length;i++ )
		{
			var f=o.rows[i].cells[0].firstChild;
			if( f && f.tagName=='INPUT' && f.checked )
			{
				if(bouis!="") bouis+=";";
				if(o.rows[i].boui!="")bouis+=o.rows[i].boui;
			}
		}
		
		var i = window.fav;
		
		var url="";
		url = "lookup";
		url += i.lookupstyle;
		url += ".jsp";
		url += "?look_object=" + i.object;
		url += "&showNew=false";
		url += "&docid="+getDocId();
	    url += "&fromSection=y";
		url += "&clientIDX="+getIDX();  //w.windowIDX;
		url += "&look_parentObj="+i.parentObj;
		url += "&look_parentBoui="+i.parentBoui;
		url +="&searchString=b:"+bouis;
		url += "&look_parentAttribute="+i.parentAttribute;
	    winmain().openDoc("tall",i.object,"","","lookup",url);
	    closeFav();
	
	}
}
function calculatop(x_ele){
var x_ret=0;
if (x_ele.tagName=='BODY') return 0;
else{
	if(x_ele.tagName=='TR' ) x_ret=0+calculatop(x_ele.parentElement);
	else x_ret=x_ele.offsetTop-x_ele.scrollTop+calculatop(x_ele.parentElement);
  }
return x_ret
}

function calculaleft(x_ele){
var x_ret=0;
if(!x_ele) return null;
x_ret=x_ele.offsetLeft-x_ele.scrollLeft+calculaleft(x_ele.parentElement);
return x_ret
}



function cleft(x_ele){
var x_ret=0;if (x_ele.tagName=='BODY') return 0;else x_ret=x_ele.offsetLeft-x_ele.scrollLeft+cleft(x_ele.parentElement);return x_ret;
}

function ctop(x_ele)
{
	var x_ret=0;
	
	if (!x_ele || x_ele.tagName=='BODY'|| x_ele.tagName=='FORM') 
	{
		return 0 ;
	}
	else 
	{
	 if (x_ele.tagName=='TR' ||x_ele.tagName=='DIV' ) x_ele=x_ele.parentElement;
	 else
	 {
	 x_ret=x_ele.offsetTop-x_ele.scrollTop;
	 }
	 x_ret+=ctop(x_ele.parentElement);
	 return x_ret;
	}
}