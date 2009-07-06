try
{
	if (js_message1) okxpto=true;		

}
catch(e)
{
	addJavascript('jsmessages/jsmessages.jsp','head'); 
}
ORG_DATE_FORMAT=7;var ORG_DATE_SEPARATOR='/';var ORG_DATE_START_DAY=0;var ORG_LANGUAGE_CODE=1033;var ORG_NUMBER_FORMAT='pt';var ORG_TIME_FORMAT=0;var ORG_CURRENCY_SYMBOL='$';var ORG_SHOW_WEEK_NUMBER='0';var ON_SAVE_RESET=false;document.onselectstart=function(){var s=event.srcElement.tagName;if(s!="INPUT" && s!="TEXTAREA")event.returnValue=false;}
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
function nextPage()
{var xele=window.event.srcElement;var pageAct=parseInt(xele.previousSibling.innerText);if(boFormSubmit!=null)
{if(boFormSubmit.list_page==null)
{createHiddenInput("list_page",(pageAct+1)+"");}
else
{boFormSubmit.list_page.value=(pageAct+1)+"";}	
}
boFormSubmit.submit();}
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
{if(document.forms["boFormSubmit"]) return document.forms["boFormSubmit"].docid.value; else return null;}
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
{var resp=newDialogBox("critical",errorMessage,[window.jsmessage_22,window.jsmessage_23],"Xeo Critical Error" );if(resp==1)
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
x.style.zIndex=1300;x.innerHTML='<b>'+window.jsmessage_21+'</b><img src=resources/senddata.gif>'
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
var G_debug=true;var G_virtualdir="";var G_htm="";SELBOUI=null;function reloadGrid(){submitGrid()
}
function submitGrid(){boFormSubmit.boFormSubmitXml.value=boForm.BuildXml(false,false);;boFormSubmit.submit();}
function onOver_GridHeader_std(event){var e=getElement(event);var c=e.className;var tpl=c.substr(c.indexOf('_')+1);switch (e.tagName)
{case "IMG": case "NOBR": return;}
if(e.className=="ghSort_"+tpl && getRuntimeStyle(e).backgroundImage!="url(templates/grid/"+tpl+"/ghBackSel.gif)")
{getRuntimeStyle(e).backgroundImage="url(templates/grid/"+tpl+"/ghBackSel.gif)";if(e.id.indexOf("ExpanderParent")>-1)
{getRuntimeStyle(e.nextSibling).backgroundImage="url(templates/grid/"+tpl+"/ghBackSel.gif)";}
_hso.lastOver=e;if(e.id.indexOf("AutoExpander")>-1)
{getRuntimeStyle(e.previousSibling).backgroundImage="url(templates/grid/"+tpl+"/ghBackSel.gif)";_hso.lastOver=e.previousSibling;}
}
}
function onOut_GridHeader_std(event){var e=getToElement(event);if(!e)var tpl='std';else{var c=e.className;var tpl=c.substr(c.indexOf('_')+1);}
if(_hso.lastOver && !(e && (e==_hso.lastOver || e.parentNode==_hso.lastOver || e.parentNode.parentNode==_hso.lastOver)))
{if(!(e && e.id.indexOf("AutoExpander")>-1 && getRuntimeStyle(e.previousSibling).backgroundImage=="url(templates/grid/"+tpl+"/ghBackSel.gif)"))
{getRuntimeStyle(_hso.lastOver).backgroundImage="";if(_hso.lastOver.id.indexOf("ExpanderParent")>-1)
{getRuntimeStyle(_hso.lastOver.nextSibling).backgroundImage="";}
else if(e && e.id.indexOf("AutoExpander")>-1)
{getRuntimeStyle(_hso.lastOver.previousSibling).backgroundImage="";}
_hso.lastOver=null;}
}
}
function onClick_GridHeader_std(event){var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
switch (o.tagName)
{case "INPUT":	selectAllGridRow_std(o); break;case "IMG":		break;default:
if(oTR.otype!=undefined )
{}
else
{}
}
}
function onOver_GridBody_std(event){var e=getElement(event);if(e == null) return;var c=e.className;if(""+e.select=="none"){return;}
var tpl=c.substr(c.indexOf('_')+1);if(_hso.lastGlow)rowOff_Std(_hso.lastGlow);_hso.lastGlow=e;while (_hso.lastGlow.tagName!="TR")
{_hso.lastGlow=_hso.lastGlow.parentNode;}
if(_hso.lastGlow.id.indexOf('expandedRow')>-1){_hso.lastGlow=null;return;}
if(_hso.hb.rows[0].cells[0].colSpan==1)
getRuntimeStyle(_hso.lastGlow).backgroundColor=colorRowOver;event.cancelBubble=true;}
function onOut_GridBody_std(event){rowOff_Std(_hso.lastGlow);event.cancelBubble=true;}
function rowOn_Std(e){getRuntimeStyle(e).backgroundColor=colorRowOver;}
function rowOff_Std(e){if(e){getRuntimeStyle(e).backgroundColor="";e=null;}
}
function deleteSelected(gnumber , xParent_attribute, otherPars ){var grid=document.getElementById("g"+gnumber+"_body");var cols=0;var r=grid.rows;var xfaz=false;var rCheck=0;if(r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){rCheck=1;}
for (var i=0 ; i<r.length ; i++ ){if(r[i].cells[rCheck].firstChild && r[i].cells[rCheck].firstChild.checked  ){cols=r[i].cells.length;grid.deleteRow(i);xfaz=true;i--;}
}
if(xfaz ){var xattributeName="";var xparent_boui="";if(boFormSubmit!=null)
{if(boFormSubmit.parent_attribute!=null)
{xattributeName=boFormSubmit.parent_attribute.value;}
if(boFormSubmit.parent_boui!=null)
{xparent_boui=boFormSubmit.parent_boui.value;}
}		
if(grid.mode !='resultBridge' &&  xattributeName!="" && xparent_boui !="" ){var rows=grid.rows;var bouis=[];var b;for (var i=0 ; i< rows.length ; i++ ){b=rows[i].id.split("__");if(b[2] ){bouis[ bouis.length ]=b[2];}
}
if(parent && parent.boFormSubmit ){if(!boFormSubmit.look_parentAttribute && xParent_attribute )createHiddenInput("look_parentAttribute","")
if(otherPars ){for (i=0 ; i< otherPars.length ;i+=2){createHiddenInput(otherPars[i],otherPars[i+1])	
}
}
xxx=boForm.BuildXml(false,false);if(parent.boFormSubmit.boFormSubmitXml )
{with (boFormSubmit )
{var xtag="object__"+xparent_boui+"__"+xattributeName ;boFormSubmitXml.value="<bo boui='"+xparent_boui+"'>"+xxx+"<"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";if(xParent_attribute )
{look_parentAttribute.value=xParent_attribute;}
submit();}
}
else if(parent.boFormSubmit.selectedBouis )
{with (boFormSubmit )
{selectedBouis.value=bouis.join(';');submit();}
}
}  		
}
var rows=grid.rows;if(grid.rows.length==0 && cols>0 ){var newTR=grid.insertRow();var newTD=newTR.insertCell(0);grid.style.height="100%";newTD.colSpan=cols;var xINNER="<TABLE  style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD style='COLOR: #999999; BORDER:0px' align=middle width='100%'>";xINNER+="Não existem objectos";xINNER+="</TD></TR></TBODY></TABLE>";newTD.innerHTML=xINNER;}
}
}
function addRemoveObjToFrame(oTR , o ){var resultFrame=eval("window.parent.resultframe");var elems=resultFrame.document.getElementsByTagName("TABLE");var tblToadd=null
for (var i=0 ;elems.length; i++){if(elems[i].container){tblToadd=elems[i];break;}
}
if(tblToadd!=null ){if(o.checked){						
var xBOUI=oTR.id.split("__")[2];var found=false;for (var i=0 ; i<tblToadd.rows.length ; i++ ){if(tblToadd.rows[i].id.split("__")[2]==xBOUI ){found=true;}
}
if(! found ){var rows=tblToadd.rows;if(tblToadd.rows[0].cells[0].colSpan>1){var newTR=tblToadd.rows[0];newTR.deleteCell(0);tblToadd.style.height="";}
else{var newTR=tblToadd.insertRow();}
var xid=tblToadd.id.split("_")[0];var xid1=_hso.hb.id.split("_")[0];var cells=oTR.cells;var oTD=null;newTR.id=oTR.id.replace(new RegExp('#'+xid1+'#','g'),xid );for (var i=-1; i<cells.length ; i++)
{if(i==-1)
{oTD=newTR.insertCell();}
else{oTD=newTR.insertCell();oTD.className=cells[i].className;oTD.innerHTML=cells[i].innerHTML;oTD.colSpan=cells[i].colSpan;}
}
}
}
else{var xBOUI=oTR.id.split("__")[2];for (var i=0 ; i<tblToadd.rows.length ; i++ ){if(tblToadd.rows[i].id.split("__")[2]==xBOUI ){tblToadd.deleteRow(i);i--;}
}
var rows=tblToadd.rows;if(tblToadd.rows.length==0){var newTR=tblToadd.insertRow();var newTD=newTR.insertCell(0);tblToadd.style.height="100%";newTD.colSpan=oTR.cells.length +2;var xINNER="<TABLE  style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD style='COLOR: #999999; BORDER:0px' align=middle width='100%'>";xINNER+="Não existem objectos";xINNER+="</TD></TR></TBODY></TABLE>";newTD.innerHTML=xINNER;}
}
}
}
var cancelOpen=false;function onDoubleClick_GridBody(event)
{var e=getElement(event);var o=getElement(event);var oTR=o;var mode=_hso.hb.mode;var waitingDetachAttribute=_hso.hb.waitingDetachAttribute;cancelOpen=false;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
var xid=oTR.id;var bouiClicked="";if(oTR.userDblClick)
{eval(oTR.userDblClick);return;}
if(xid)
{var df=xid.split("__");open_Obj(oTR,df[1],df[2]);bouiClicked=df[2];}
if(!cancelOpen)
{var r=_hso.hb.rows;var rCheck=0;if(r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){rCheck=1;}
for(var i=0;i<r.length;i++)
{if(r[i].cells[rCheck].firstChild&&r[i].cells[rCheck].firstChild.checked )
{xid=r[i].id;var df=xid.split("__");if(df[2]!=bouiClicked)open_Obj(r[i],df[1],df[2]);}
}
}
}
function onClick_GridBody_std(event){var e=getElement(event);var o=getElement(event);var oTR=o;var mode=_hso.hb.mode;var waitingDetachAttribute=_hso.hb.waitingDetachAttribute;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
if(mode=="searchone" && o.tagName!="IMG")
{if(oTR.id){if(""+waitingDetachAttribute!="undefined" )
{var b=oTR.id.split("__");if(""+oTR.selectRecordNone!="undefined" )b[2]="";winmain().setWDA (waitingDetachAttribute , b[2] , getDocId());winmain().ndl[getIDX()].close();}
else
submitSelectOne(oTR );if(oTR.userClick)eval(oTR.userClick);}
}
else
{switch (o.tagName)
{case "INPUT":	{selectGridRow_std(o);if(mode=="searchmulti")
{addRemoveObjToFrame(oTR , o);}
break;}
case "IMG":		if(o.src.indexOf('quickview')>0){expandGridRow_std(o); break; }
default:
var r=oTR;var rCheck=0;if(r.cells[0].firstChild && r.cells[0].firstChild.tagName=='IMG' )
{rCheck=1;}
var i=r.cells[rCheck].firstChild;if(i && i.checked!=null)
{if(i.checked )i.checked=false;else i.checked=true;selectGridRow_std(i );if(mode=="searchmulti")
{addRemoveObjToFrame(oTR , i);}
}
if(r.userClick)eval(r.userClick);}
}
}
function open_Obj(oTR,objectName,ObjectBoui){if(objectName=='Ebo_ShortCut' && event.srcElement.tagName!='IMG' )
{var cells=oTR.cells;for (var i=0; i<cells.length; i++)
{if(cells[i].firstChild && cells[i].firstChild.exec=='yes' )
{eval(cells[i].firstChild.innerText );return
}
}
}
var xurl="";var isProtocol=false;if(parent && parent.boFormSubmit && parent.boFormSubmit.editingTemplate &&boFormSubmit.parent_attribute.value=='DAO')
{isProtocol=true;}
if(oTR.isextendlist || ( oTR.hasRightsToSaveParent && oTR.hasRightsToSaveParent=='no')|| objectName.toLowerCase()=="ebo_template" )
{isProtocol=true;}
if( boFormSubmit.docid && boFormSubmit.docid.value
&& boFormSubmit.parent_boui && boFormSubmit.parent_boui.value
&& boFormSubmit.parent_attribute && boFormSubmit.parent_attribute.value
&& !boFormSubmit.resultBridge
)
{if(!isProtocol )
{var xurl="&ctxParentIdx="+boFormSubmit.docid.value;xurl+="&ctxParent="+boFormSubmit.parent_boui.value;xurl+="&addToCtxParentBridge="+boFormSubmit.parent_attribute.value;xurl+="&docid="+boFormSubmit.docid.value;xurl+="&relatedClientIDX="+getIDX();cancelOpen=true;winmain().nextPage(getIDX(), objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl );;}
else
{var xurl="&relatedParentDocid="+boFormSubmit.docid.value;xurl+="&relatedParent="+boFormSubmit.parent_boui.value;xurl+="&relatedParentBridge="+boFormSubmit.parent_attribute.value;xurl+="&relatedClientIDX="+getIDX();winmain().openDoc("medium",objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl);}
}
else
{winmain().openDoc("medium",objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl);}
}
function selectAllGridRow_std(o)
{var c=o.parentNode.className;var tpl=c.substr(c.indexOf('_')+1);var isExpanded=c.indexOf('Expanded')>-1;var isSelect=o.checked;var x=o.id;var refered_Obj=x.substr(0,x.indexOf('_' ));so(refered_Obj);var r=_hso.hb.rows;var mode=_hso.hb.mode;var rCheck=0;if(r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){rCheck=1;}
for(var i=0;i<r.length;i++){if(r[i].cells[rCheck]&&r[i].cells[rCheck].firstChild){r[i].cells[rCheck].firstChild.checked=isSelect;selectGridRow_std(r[i].cells[rCheck].firstChild);if(mode=="searchmulti"){addRemoveObjToFrame(r[i] , r[i].cells[rCheck].firstChild );}
}
}
}
function selectForceGridRow_std(exclusive,ele){if(ele )var o=ele
else var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
var c=oTR.cells(0).className;var tpl=c.substr(c.indexOf('_')+1);var isExpanded=c.indexOf('Expanded')>-1;var s='gCell'+(isExpanded? 'Expanded':'' )+('Sel' )+'_'+tpl;applyStyle(oTR, s);if(oTR.relatedElement )
{var el=document.getElementById(oTR.relatedElement );if(el )el.style.display='';}
if(exclusive )
{var oRows=oTR.parentNode.rows;for (var i=0 ; i<oRows.length ; i++ )
{if(oTR!=oRows[i] )
{c=oRows[i].cells(0).className;tpl=c.substr(c.indexOf('_')+1);isExpanded=c.indexOf('Expanded')>-1;s='gCell'+(isExpanded? 'Expanded':'' )+('' )+'_'+tpl;applyStyle(oRows[i] , s);if(oRows[i].relatedElement )
{var el=document.getElementById(oRows[i].relatedElement );if(el )el.style.display='none';}
}
}
}
}
function selectGridRow_std(o)
{var c=o.parentNode.className;var tpl=c.substr(c.indexOf('_')+1);var isExpanded=c.indexOf('Expanded')>-1;var isSelect=o.checked;var row=o.parentNode.parentNode;var s='gCell'+(isExpanded? 'Expanded':'' )+(isSelect? 'Sel':'' )+'_'+tpl;applyStyle(row, s);if(!_hso.boselect &&_hso.boselect!=0)refreshStatusGrid_std();if(isSelect)_hso.boselect++;else _hso.boselect--;refreshStatusGrid_std();window.status=_hso.boselect;}
function grid_DragOver(){var info=event.dataTransfer.getData("Text");if(info&& info.indexOf('moveLin')>-1){event.dataTransfer.effectAllowed='move';window.event.returnValue=false;var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
for (var i=0; i< oTR.cells.length;i++)
{oTR.cells[i].runtimeStyle.borderBottom='2px solid #246BD6';}
}
else if(info && info.indexOf('dragObject:')>-1 &&
(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
)
{event.dataTransfer.effectAllowed='copy';window.event.returnValue=false;var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
for (var i=0; i< oTR.cells.length;i++)
{oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';}
}
else event.dataTransfer.effectAllowed='none';}
function grid_Drop(){var info=event.dataTransfer.getData("Text");if(info&& info.indexOf('moveLin')>-1){var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
var href=window.location.href;var xqry=href.substring(href.indexOf("?")+1);var xargs=xqry.split("&");var xattributeName="";var xparent_boui="";if(boFormSubmit!=null)
{if(boFormSubmit.parent_attribute!=null)
{xattributeName=boFormSubmit.parent_attribute.value;}
if(boFormSubmit.parent_boui!=null)
{xparent_boui=boFormSubmit.parent_boui.value;}
}
var bouis=[];var rows=oTR.parentNode.rows;for (var i=0 ; i< rows.length ; i++ ){b=rows[i].id.split("__");if(b[2] ){bouis[ bouis.length ]=b[2];}
}
if(parent && parent.boFormSubmit ){createHiddenInput("moveLines",info+"_to_"+oTR.cells[0].firstChild.lin)
xxx=boForm.BuildXml(false,false);with (boFormSubmit ){var xtag="object__"+xparent_boui+"__"+xattributeName ;boFormSubmitXml.value="<bo boui='"+xparent_boui+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+">"+xxx+"</bo>";submit();}
}  		
}
else if(info && info.indexOf('dragObject:')>-1 &&
(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
)
{var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
var href=window.location.href;var xqry=href.substring(href.indexOf("?")+1);var xargs=xqry.split("&");var xattributeName="";var xparent_boui="";if(boFormSubmit!=null)
{if(boFormSubmit.parent_attribute!=null)
{xattributeName=boFormSubmit.parent_attribute.value;}
if(boFormSubmit.parent_boui!=null)
{xparent_boui=boFormSubmit.parent_boui.value;}
}
if(xattributeName!="" && xparent_boui!="" )
{}
else
{for (var i=0 ; i<xargs.length ; i++ ){var x=xargs[i].split("=");if(x[0]=='boql'){x[1]=unescape(x[1]);x[1]=x[1].replace(/\+/g," ");}
createHiddenInput(x[0],x[1]);}
createHiddenInput("drop_info", info );boFormSubmit.submit();}
}
}
function executeBridgeMeth(gnumber , attributeName , methodName )
{var grid=document.getElementById("g"+gnumber+"_body");var r=grid.rows;var xfaz=false;var rCheck=0;if(r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){rCheck=1;}
var selectedLines="";for (var i=0 ; i<r.length ; i++ ){if(r[i].cells[rCheck].firstChild && r[i].cells[rCheck].firstChild.checked  )
{selectedLines+=(i+1)+";";}
}
createHiddenInput("selectedLines", selectedLines);createHiddenInput("toExecute", "ATR-"+attributeName+"."+methodName);var xst=winmain().getStatus(getIDX())
boForm.BindValues();winmain().setStatus(getIDX(),xst);}
function grid_DragLeave(){var info=event.dataTransfer.getData("Text");if((info&& info.indexOf('moveLin')>-1)||
(
info && info.indexOf('dragObject:')>-1 &&
(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
)
)
{var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
for (var i=0; i< oTR.cells.length;i++)
{oTR.cells[i].runtimeStyle.borderBottom='1px solid #CCCCCC';}
}
}
function grid_DragEnter()
{var info=event.dataTransfer.getData("Text");if(info&& info.indexOf('moveLin')>-1){event.dataTransfer.effectAllowed='move';window.event.returnValue=false;var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
for (var i=0; i< oTR.cells.length;i++)
{oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';}
}
else if(info && info.indexOf('dragObject:')>-1 &&
(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  ))
{event.dataTransfer.effectAllowed='copy';window.event.returnValue=false;var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
for (var i=0; i< oTR.cells.length;i++)
{oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';}
}
else event.dataTransfer.effectAllowed='none';}
function grid_StartMoveLine(){var startline=event.srcElement.lin+":";var movelines="";var o=getElement(event);var oTR=o;while (oTR.tagName!="TR")
{oTR=oTR.parentNode;if(oTR==null)return; 	
}
var r=oTR.parentNode.rows;for (var i=0 ; i<r.length ; i++ ){if(r[i].cells[1].firstChild && r[i].cells[1].firstChild.checked  )
{movelines+=((i+1)+':');}
}
if(movelines.indexOf(startline )>-1 )
{event.dataTransfer.setData('Text','moveLin:'+movelines);event.dataTransfer.effectAllowed='move';}
}
function refreshStatusGrid_std(){_hso.boselect=0;var r=_hso.hb.rows;var rtot=0;var rCheck=0;if(r.length>0)
{if(r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' )
{rCheck=1;}
}
for(var i=0;i<r.length;i++){if(r[i].cells[rCheck].firstChild){if(r[i].cells[rCheck].firstChild.checked)_hso.boselect++;rtot++
}
}
var x=document.getElementById(_hso.id+'_check');if(x){if(_hso.boselect==rtot)x.checked=true;else  x.checked=false;}
}
function collapseGridRow_std(iRow)
{if(iRow>-1)
{var s="";var row	=_hso.hb.rows[iRow];var imgqv	 =null;var c		 =row.cells[0].className;for (var j=0;j< row.cells.length;j++)
{if(row.cells[j].firstChild && row.cells[j].firstChild.src && row.cells[j].firstChild.src.indexOf('quick')>-1 )
{imgqv=row.cells[j].firstChild;break;}
}	
if(imgqv )
{_hso.hb.rows[iRow+1].style.display="none";var tpl	  	 =c.substr(c.indexOf('_')+1 );var isSelect =row.cells[0].firstChild?row.cells[0].firstChild.checked:row.cells[1].firstChild.checked;var s='gCell'+''+(isSelect? 'Sel':'' )+'_'+tpl;imgqv.src="templates/grid/"+tpl+"/quickview.gif";applyStyle(row, s);}
}
}
function expandGridRow_std(o)
{var row		 =o.parentNode.parentNode;var iIndex	 =row.rowIndex;var c		 =o.parentNode.className;var tpl	  	 =c.substr(c.indexOf('_')+1 );var isExpanded=c.indexOf('Expanded')>-1;var isSelect =row.cells[0].firstChild?row.cells[0].firstChild.checked:row.cells[1].firstChild.checked;var imgqv	 =null;for (var j=0;j< row.cells.length;j++)
{if(row.cells[j].firstChild && row.cells[j].firstChild.src && row.cells[j].firstChild.src.indexOf('quick')>-1 )
{imgqv=row.cells[j].firstChild;break;}
}	
if(isExpanded)
{row.loaded=true;collapseGridRow_std(iIndex);_hso.lastRowIndex=-1;return;}
collapseGridRow_std(_hso.lastRowIndex);_hso.lastRowIndex=iIndex;if(row.loaded)
{_hso.hb.rows[iIndex+1].style.display="";}
else
{var oNewTr=_hso.hb.insertRow(iIndex+1);oNewTr.id="ExpandedRow";oNewTr.onmouseover	=function y(){window.event.cancelBubble=true };oNewTr.onclick		=function x(){window.event.cancelBubble=true };var oNewTd=oNewTr.insertCell(0);with (oNewTd)
{className	="gCellQuickView_std";colSpan		=row.cells.length;var boui=row.id.split("__")[2];innerHTML	="<iframe style='width:100%' src='__buildPreview.jsp?designHeader=false&designPrint=false&bouiToPreview="+boui+"&docid="+getDocId()+"' ></iframe>";}
oNewTd=oNewTr.insertCell(0);oNewTd.className="gCell_std";row.loaded=true;}
var s='gCell'+'Expanded'+(isSelect? 'Sel':'' )+'_'+tpl;imgqv.src="templates/grid/"+tpl+"/quickview_on.gif";applyStyle(row, s);}
function applyStyle(o, sClass )
{var i=0;var ii=o.cells.length;if(o.noStyle )return;while (i<ii)
{o.cells[i].className=sClass;i++;}
}
function setBarFilterStyle(bOn,event)
{var o=event.srcElement;if(o.tagName!="TD")return;with (o.runtimeStyle)
{if(bOn)
{color="#000000";fontWeight="bold";}
else
{color="";fontWeight="";}
}
}
function onClick_BarFilter(event,bReset)
{var e=getElement(event);var xtbl=e.parentElement.parentElement;if(bReset || e.tagName=="TD")
{if(bReset)
{_hso.oDataSrc=null;}
if(_hso.oLast)
{with (_hso.oLast.style)
{color="#74736B";fontWeight="normal";}
}
else
{with(xtbl.rows[0].cells[0].style)
{color="#74736B";fontWeight="normal";}
}
bReset ? _hso.oLast=xtbl.rows[0].cells[0] : _hso.oLast=e;var s=_hso.oLast.innerText;with (_hso.oLast.style)
{fontWeight="bold";color="#000088";}
if(!bReset)
{_hso.pageNum=1;_hso.filter=(s=="Todos" ? "" : s);if(boFormSubmit.list_letter==null)
{createHiddenInput("list_letter",_hso.filter);}
else
{boFormSubmit.list_letter.value=_hso.filter;}				
var xboql=boFormSubmit.boql.value;if(xboql )
{var x=xboql.split("+");if(x.length==4 && x[3]==encodeURIComponent("0=1"))
{if(typeof(parent.xurl)!="undefined" )
{if(boFormSubmit.list_letter==null)
{createHiddenInput("list_letter",_hso.filter);}
else
{boFormSubmit.list_letter.value=_hso.filter;}					
}
else
{					
x[3]="1=1";var xx=x.join("+");if(boFormSubmit.list_letter==null)
{createHiddenInput("boql",xx);}
else
{boFormSubmit.boql.value=xx;}							
}
}
}
if(boFormSubmit.list_page==null)
{createHiddenInput("list_page","1");}
else
{boFormSubmit.list_page.value="1";}
if(boFormSubmit.list_letter_field==null)
{createHiddenInput("list_letter_field",_hso.hb.letter_field);}
else
{boFormSubmit.list_letter_field.value=_hso.hb.letter_field;}			
boFormSubmit.submit();}
}
}
function submitSelectOne(oTR ){var b=oTR.id.split("__");if(b[2]){if(""+oTR.selectRecordNone!="undefined" )b[2]="";if(parent.boFormSubmit )
{with (parent.boFormSubmit ){if(parent.boFormSubmit.boFormSubmitXml )
{var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+" mode='add' >"+b[2]+"</"+xtag+"></bo>";clientIDXtoClose.value=parent.windowIDX;}
else if(parent.boFormSubmit.selectedBouis )
{selectedBouis.value=b[2];}
submit();		
}	
}
else
{alert("ERROR !?!! Parent.boForSubmit not defined");}
}
}
function submitSelectOne2(boui ){if(parent.boFormSubmit )
{with (parent.boFormSubmit ){if(parent.boFormSubmit.boFormSubmitXml )
{var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+" mode='add' >"+boui+"</"+xtag+"></bo>";clientIDXtoClose.value=parent.windowIDX;}
else if(parent.boFormSubmit.selectedBouis )
{selectedBouis.value=boui;}	
submit();		
}	
}
else
{alert("ERROR !?!! Parent.boForSubmit not defined");}
}
function submitBridge2(newBoui){var elems=parent.resultframe.document.getElementsByTagName("TABLE");var tblToadd=null;for (var i=0 ;elems.length; i++){if(elems[i].container){tblToadd=elems[i];break;}
}
if(tblToadd!=null ){var rows=tblToadd.rows;var bouis=[];var b;for (var i=0 ; i< rows.length ; i++ ){b=rows[i].id.split("__");if(b[2] && b[2]!="null"){bouis[ bouis.length ]=b[2];}
}
bouis[ bouis.length ]=newBoui;var waitingDetachAttribute=tblToadd.waitingDetachAttribute;if(""+waitingDetachAttribute=="undefined" )
{if(parent.boFormSubmit.boFormSubmitXml){with (parent.boFormSubmit )
{var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";clientIDXtoClose.value=parent.windowIDX;submit();}
}
else {with (parent.boFormSubmit )
{var xtag="Ebo_Template__"+look_templateBoui.value;boFormSubmitTemplateXml.value="<"+xtag+">"+bouis.join(';')+"</"+xtag+">";clientIDXtoClose.value=parent.windowIDX;submit();}
}  		
}
else
{winmain().setWDA (waitingDetachAttribute , bouis.join(";"), getDocId());winmain().ndl[getIDX()].close();}
}	
}
function submitBridge(){var elems=parent.resultframe.document.getElementsByTagName("TABLE");var tblToadd=null;for (var i=0 ;elems.length; i++){if(elems[i].container){tblToadd=elems[i];break;}
}
if(tblToadd!=null ){var rows=tblToadd.rows;var bouis=[];var b;for (var i=0 ; i< rows.length ; i++ ){b=rows[i].id.split("__");if(b[2] && b[2]!="null"){bouis[ bouis.length ]=b[2];}
}
var waitingDetachAttribute=tblToadd.waitingDetachAttribute;if(""+waitingDetachAttribute=="undefined" )
{if(parent.boFormSubmit.boFormSubmitXml){with (parent.boFormSubmit ){var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";clientIDXtoClose.value=parent.windowIDX;submit();}
}
else {with (parent.boFormSubmit ){var xtag="Ebo_Template__"+look_templateBoui.value;boFormSubmitTemplateXml.value="<"+xtag+">"+bouis.join(';')+"</"+xtag+">";clientIDXtoClose.value=parent.windowIDX;submit();}
}  		
}
else
{winmain().setWDA (waitingDetachAttribute , bouis.join(";"), getDocId());winmain().ndl[getIDX()].close();}
}	
}
function actNumberOfArea()
{if(document.forms["boFormSubmit"] && document.forms["boFormSubmit"].parent_attribute && document.forms["boFormSubmit"].parent_boui )
{try
{parent.document.getElementById(document.forms["boFormSubmit"].parent_attribute.value+"_"+document.forms["boFormSubmit"].parent_boui.value).innerText="("+window.recs+")";}
catch(e){}
}
}
function onOver_Form_std(event){var e=getElement(event);var c=e.className;if(event)
event.cancelBubble=true;}
function onOut_Form_std(event){var e=getElement(event);var c=e.className;if(event)
event.cancelBubble=true;}
function onClick_Form_std(event){e=getElement(event);if(event)
event.cancelBubble=true;}
function LookupObjects(lookupQuery , lookupStyle, lookupObject, parentObj , parentBoui, parentAttribute , showNew, docid , look_action ,otherpar,fromSection,toSearch,options){try{FCK.LinkedField.whereTo = FCK.EditorDocument.selection.createRange();}catch(E){};
var url;url="lookup";url +=lookupStyle;url +=".jsp";url +="?look_object=" + lookupObject;url +="&showNew=" + showNew;if(getDocId()!=null)url +="&docid="+getDocId();else if(docid !=null) url +="&docid="+docid;if(fromSection)url +="&fromSection=y";url +="&clientIDX="+getIDX();url +="&look_parentObj="+parentObj;url +="&look_query="+lookupQuery;url +="&look_parentBoui="+parentBoui;if(otherpar )url+="&"+otherpar;if(toSearch)url+="&searchString="+encodeURIComponent(toSearch.replace(/%/g,'_*_'));if(look_action ){url +="&look_action="+look_action;}
url +="&look_parentAttribute="+parentAttribute;if(options)url+="&options="+options;winmain().openDoc("tall",lookupObject,"","","lookup",url);}
function LookupBridge(searchSourceQuery , searchDestinationQuery, lookupStyle, lookupSourceObj, lookupDestinationObj,
sourceBouiObj, destinationBouiObj, sourceAttribute, destinationAttribute,
canSelectRows, bolist_query, renderOnlyCardID, docid , showNew, otherpar,  look_action )
{	
var url;url="__lookupbridge";url +=lookupStyle;url +=".jsp";url +="?look_SourceObj=" + lookupSourceObj;url +="&look_DestinationObj=" + lookupDestinationObj;url +="&showNew=" + showNew;url +="&docid="+getDocId();url +="&clientIDX="+getIDX();url +="&look_sourceQuery="+searchSourceQuery;url +="&look_destinationQuery="+searchDestinationQuery;url +="&look_sourceBouiObj="+sourceBouiObj;url +="&look_destinationBouiObj="+destinationBouiObj;url +="&renderOnlyCardID="+renderOnlyCardID;url +="&canSelectRows="+canSelectRows;url +="&ctxParent="+destinationBouiObj;url +="&ctxParentIdx="+getDocId();url +="&relatedClientIDX="+getIDX();if(otherpar )url+="&"+otherpar;	
if(look_action ){url +="&look_action="+look_action;}
url +="&look_sourceAttribute="+sourceAttribute;url +="&look_destinationAttribute="+destinationAttribute;winmain().openDoc("tall",lookupDestinationObj,"","","lookup",url);}
function LookupObjectsDetachField(lookupQuery , lookupStyle, divObject, lookupObject, waitingAttribute , attributeValue, validObjects , showNew, docid , look_action , otherpar,toSearch)
{	
var url;waitingAttribute=""+getIDX()+"-"+waitingAttribute+"-"+divObject.id;url="__look";url +=lookupStyle;url +="_detachAttribute.jsp";url +="?look_object=" + lookupObject;url +="&showNew=" + showNew;url +="&docid="+getDocId();url +="&validObjects="+validObjects;url +="&attributeValue="+attributeValue;url +="&clientIDX="+getIDX();url +="&waitingAttribute="+waitingAttribute;url +="&look_query="+lookupQuery;if(toSearch)url+="&searchString="+encodeURIComponent(toSearch.replace(/%/g,'_*_'));if(otherpar )url+="&"+otherpar;if(look_action ){url +="&look_action="+look_action;}
winmain().createWDA(divObject , waitingAttribute );winmain().openDoc("tall",lookupObject,"","","lookup",url);}
function LookupTemplates(lookupJSP, templateBoui , clsRelatedAttribute , clsTemplate  , attributeName , relatedBoui , showNew, docid)
{	
var url;url=lookupJSP;url +="?look_object=" + clsTemplate;url +="&docid="+getDocId();url +="&clientIDX="+getIDX();url +="&look_parentClass="+clsRelatedAttribute;url +="&look_templateBoui="+templateBoui;url +="&look_relatedBoui="+relatedBoui;url +="&look_attributeName="+attributeName;winmain().openDoc("tall","ebo_template","","","lookup",url);}
function openAttributeMap(name,docid,template_BOUI){var url;url="templateEditMappingAttribute.jsp";url +="?attributeName=" + name;url +="&docid="+getDocId();url +="&templateBOUI="+template_BOUI;url +="&clientIDX="+getIDX();winmain().openDoc("tall",'','','',"formula",url);}
function loadTab_std()
{var h=_hso.hb;if(h&&h.cells)
downTab_std(h.cells[0].firstChild);}
function onKeyUpTab_std(event){if(event.keyCode==13)
{onClickTab_std(event)
}
}
function onDocumentReadyTab_std(){loadTab_std();}
function onClickTab_std(event){downTab_std(getElement(event));}
function getBody(xid){return xid.replace(/header/,'body');}
function ondragenterTab_std(event)
{_hso.timeover=new Date();}
function ondragoverTab_std(event)
{var t=new Date();if(t-_hso.timeover>700 )
{downTab_std(getElement(event));_hso.timeover=new Date();}
}
function downTab_std(oTab)
{var o=oTab;setTabWFocus(oTab.id, _hso.id);var xgt=document.getElementById('hrTab'+_hso.id);var xgts=document.getElementById('hrSelTab'+_hso.id);if(o.className=="tab" || !_hso.oTab || o.tagName=='IMG')
{if(o.tagName=='IMG')o=o.parentNode;xgt.style.display="none";o.className="tab tabOn";var toLoad=true;if(o.offsetTop - 4 <0)toLoad=false;showTab_std(xgts,o);var b=document.getElementById(getBody(o.id));b.style.display="inline";if(toLoad){var xfrms=b.getElementsByTagName('iframe');for (var i=0; i< xfrms.length ;i++){if(xfrms[i].src=="" && xfrms[i].xsrc ){var x=setUrlAttribute(xfrms[i].xsrc,"docid",getDocId());xfrms[i].src=x;}
}
}
if(_hso.oTab)
{_hso.oTab.className="tab";b=document.getElementById(getBody(_hso.oTab.id));b.style.display="none";}
_hso.oTab=o;}
else{if(window.event!=null && window.event.type=='resize' ){showTab_std(xgts,o);}
var b=document.getElementById(getBody(o.id));if(b)
{b.style.display="inline";var xfrms=b.getElementsByTagName('iframe');for (var i=0; i< xfrms.length ;i++){if(xfrms[i].src=="" && xfrms[i].xsrc ){var x=setUrlAttribute(xfrms[i].xsrc,"docid",getDocId());xfrms[i].src=x;}
}
}
}
}
function onOverTab_std(event)
{var xgt=document.getElementById('hrTab'+_hso.id);showTab_std(xgt,getElement(event));}
function onOutTab_std(event)
{var x=document.getElementById('hrTab'+_hso.id);x.style.display="none";}
function showTab_std(oShow, o)
{try
{with (oShow.style)
{left	=o.offsetLeft + 1;top		=o.offsetTop - 4;width	=o.offsetWidth - 2;display="inline";}
}
catch(e)
{recover_Tab(oShow,o)
}
}
function recover_Tab(oShow,o){var oa=oShow;while (true)
{if(!oShow||oShow.tagName=='BODY'){return
}
else if(oShow&&oShow.currentStyle.display=='none' && oShow.tagName=='DIV')
{break;}
else  oShow=oShow.parentElement;}
try{if(oShow&&oShow.tagName!='BODY'){oShow.style.display='inline';with (oa.style)
{left	=o.offsetLeft + 1;top		=o.offsetTop - 4;width	=o.offsetWidth - 2;display="inline";}
oShow.style.display='none';}
}
catch(e){}
}
var _oCalPopUp;var _oCalInput;var _sCalMonths		=	new	Array
(
window.jsmessage_26,
window.jsmessage_27,
window.jsmessage_28,
window.jsmessage_29,
window.jsmessage_30,
window.jsmessage_31,
window.jsmessage_32,
window.jsmessage_33,
window.jsmessage_34,
window.jsmessage_35,
window.jsmessage_36,
window.jsmessage_37
);var _sCalLongMonths	=	new	Array
(
window.jsmessage_38,
window.jsmessage_39,
window.jsmessage_40,
window.jsmessage_41,
window.jsmessage_42,
window.jsmessage_43,
window.jsmessage_44,
window.jsmessage_45,
window.jsmessage_46,
window.jsmessage_47,
window.jsmessage_48,
window.jsmessage_49
);var _sCalDays		=	new Array
(
window.jsmessage_50,
window.jsmessage_51,
window.jsmessage_52,
window.jsmessage_53,
window.jsmessage_54,
window.jsmessage_55,
window.jsmessage_56
);var _sCalLongDays	=new Array
(
window.jsmessage_57,
window.jsmessage_58,
window.jsmessage_59,
window.jsmessage_60,
window.jsmessage_61,
window.jsmessage_62,
window.jsmessage_63
);/*
_iCalFormat Formats
0  - M/d/yy
1  - M/d/yyyy
2  - MM/dd/yy
3  - MM/dd/yyyy
4  - d/M/yy
5  - d/M/yyyy
6  - dd/MM/yy
7  - dd/MM/yyyy
8  - yy/M/d
9  - yyyy/M/d
10 - yy/MM/dd
11 - yyyy/MM/dd
*/
var _sDateFormats	=new Array
(
"M/d/yy",
"M/d/yyyy",
"MM/dd/yy",
"MM/dd/yyyy",
"d/M/yy",
"d/M/yyyy",
"dd/MM/yy",
"dd/MM/yyyy",
"yy/M/d",
"yyyy/M/d",
"yy/MM/dd",
"yyyy/MM/dd"
);var _sTimeFormats	=new Array
(
"h:mm tt",
"hh:mm tt",
"H:mm",
"HH:mm",
"h:mm:ss tt",
"hh:mm:ss tt",
"H:mm:ss",
"HH:mm:ss"
);var _iCalStartDay	=ORG_DATE_START_DAY;var _dCalMinDate	=new Date(1800, 4, 1);var _dCalMaxDate	=new Date(2100, 11, 31);var _sCalSeperator	=ORG_DATE_SEPARATOR;var _iCalFormat		=ORG_DATE_FORMAT;var _sShowWeekNumbers=ORG_SHOW_WEEK_NUMBER;var iShowWeekCalWidth=170;var iNoWeekCalWidth =150;var iCalHeight      =144;function InitCalendar(iDateFormat, sSeperator, iStartDay, dMinDate, dMaxDate)
{_iCalFormat=iDateFormat;if(sSeperator)
{_sCalSeperator=sSeperator;if(iStartDay)
{_iCalStartDay=iStartDay;if(dMinDate)
{_dCalMinDate=dMinDate;if(dMaxDate)
{_dCalMaxDate=dMaxDate;}
}
}
}
}
function LaunchCalendar(oInputElem, dInitDate)
{if(isNaN(dInitDate))
{dInitDate=new Date();}
_oCalInput=oInputElem;_oCalPopUp=window.createPopup();_oCalPopUp.document.body.innerHTML=DrawMonth(dInitDate, "parent.ReturnDate(this);", HILITE_NONE, null );var iCalWidth=_sShowWeekNumbers=="1" ? iShowWeekCalWidth : iNoWeekCalWidth;_oCalPopUp.show(-iCalWidth+40, 20, iCalWidth, iCalHeight, event.srcElement);}
function SetDateValues(oInput)
{oInput.value=Trim(oInput.value);var s=oInput.value;if(s.length>0)
{var D=ParseDate(s, _iCalFormat);if(D)
{oInput.value		=FormatDate(D);oInput.returnValue	=FormatUtcDate(D);return true;}
else
{oInput.select();return false;}
}
else
{oInput.returnValue="";return true;}
}
function GetFirstDayInCalendar(D)
{D.setDate(1);var i=D.getDay()- _iCalStartDay;if(i<0)
{i +=7;}
if(i==0)
{return D;}
D.setDate((i * -1)+ 1);D.setHours(0);D.setMinutes(0);D.setSeconds(0);return D;}
var HILITE_NONE=0;var HILITE_DAY=1;var HILITE_WEEK=2;function DrawMonth(D, sOnClick, hiliteCode, hiliteDay )
{var dHiliteStart=null;var dHiliteEnd =null;switch(hiliteCode )
{case HILITE_DAY:
dHiliteStart=new Date(hiliteDay );dHiliteEnd =new Date(hiliteDay );break;case HILITE_WEEK:
dHiliteStart=new GetFirstDayOfWeek(hiliteDay );dHiliteEnd =new GetLastDayOfWeek(hiliteDay );break;default:
break;}
if(dHiliteStart!=null )
{dHiliteStart.setMilliseconds(0 );dHiliteStart.setSeconds(0 );dHiliteStart.setMinutes(0 );dHiliteStart.setHours(0 );}
if(dHiliteEnd!=null )
{dHiliteEnd.setMilliseconds(999 );dHiliteEnd.setSeconds(59 );dHiliteEnd.setMinutes(59 );dHiliteEnd.setHours(23 );}
var tmpDate=new Date(D );tmpDate.setHours(0,0,0,0);var dToday		=new Date();dToday.setHours(0,0,0,0);var iToday		=dToday.valueOf();var dInitDate	=new Date(tmpDate.valueOf());var iInitMonth	=dInitDate.getMonth();tmpDate=GetFirstDayInCalendar(tmpDate);var s="<table cellpadding='0' cellspacing='0' style='border-collapse: collapse;table-layout:fixed'><tr><td style='text-align:center;border:1px solid #7b9ebd;height:18px;filter:progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr=#ffffff,EndColorStr=#CDDDE1);' colspan='";if(_sShowWeekNumbers=="1" )
{s +="8";}
else
{s +="7";}
s+="'><table cellpadding='0' cellspacing='0' style='table-layout:fixed'><tr height='18' style='font-family:verdana;font-size:8pt;cursor:hand;'>";var iValue=dInitDate.valueOf();if(tmpDate<_dCalMinDate)
{s +="<td onclick='" + sOnClick + "' align='center' width='22' style='cursor: default;' nav='0'>&nbsp;</td>";}
else
{s +="<td onclick='" + sOnClick + "' align='center' width='22' d='" + iValue + "' nav='1' onmouseover='this.runtimeStyle.color=\"#e89f2e\";' onmouseout='this.runtimeStyle.color=\"\";'>&lt;</td>";}
s +="<td onclick='" + sOnClick + "' align='center' width='104' d='" + iValue + "' nav='2' onmouseover='this.runtimeStyle.color=\"#e89f2e\";' onmouseout='this.runtimeStyle.color=\"\";'>" + _sCalMonths[iInitMonth] + " " + dInitDate.getFullYear()+ "</td>";var dLastDay=new Date(tmpDate.valueOf());dLastDay.setDate(dLastDay.getDate()+ 42);if(dLastDay>_dCalMaxDate)
{s +="<td onclick='" + sOnClick + "' align='center' width='22' style='cursor: default;' nav='0'>&nbsp;</td>";}
else
{s +="<td onclick='" + sOnClick + "' align='center' width='22' d='" + iValue + "' nav='1' onmouseover='this.runtimeStyle.color=\"#e89f2e\";' onmouseout='this.runtimeStyle.color=\"\";'>&gt;</td>";}
s +="</tr></table></td></tr>";s +="<tr>";var i	=0;var ii	=_iCalStartDay;;if(_sShowWeekNumbers=="1" )
{s +="<td style='font-family:verdana;font-size:8pt;background-color:#eeeeee;text-align:center;border:1px solid #7b9ebd;width:20px;height:18px;cursor:default;' nav='0'></td>";}
while (i<7)
{s +="<td style='font-family:verdana;font-size:8pt;background-color:#eeeeee;text-align:center;border:1px solid #7b9ebd;width:20px;height:18px;cursor:default;' nav='0'>" + _sCalDays[ii] + "</td>";i++;ii++;if(ii>6)
{ii=0
}
}
s +="</tr>";var iDate	=0;var sStyle	="";for (i=0; i<6; i++)
{s +="<tr onmouseover='if(event.srcElement.noHl!=1){event.srcElement.runtimeStyle.color=\"#e89f2e\";}' onmouseout='if(event.srcElement.noHl!=1){event.srcElement.runtimeStyle.color=\"\";}'>";if(_sShowWeekNumbers=="1" )
{var iWeekNumber=getWeekNumber(GetLastDayOfWeek(tmpDate ));s +="<td noHl='1' style='cursor:default;font-family:verdana;font-size:8pt;color:#0000ff;text-align:center;background-color:#eeeeee;border:1px solid #7288ac;width:20px;height:18px;'>" + iWeekNumber + "</td>";}
for(ii=0; ii<7; ii++)
{iDate	=tmpDate.getDate();iValue	=tmpDate.valueOf();sStyle="font-family:verdana;font-size:8pt;text-align:center;border:1px solid #7288ac;width:20px;height:18px;";if(iValue==iToday)
{sStyle +="border:2px solid #e89f2e;";}
var dayIsInThisMonth=(tmpDate.getMonth()==iInitMonth );if((hiliteCode==HILITE_DAY || hiliteCode==HILITE_WEEK )&&
(tmpDate.getTime()>=dHiliteStart.getTime())&&  (tmpDate.getTime()<=dHiliteEnd.getTime()))
{sStyle +="background-color:#cccccc;";if(!dayIsInThisMonth )
{sStyle +="color:#ffffff;";}
}
else
{if(!dayIsInThisMonth )
{sStyle +="color:#cccccc;";}
}
if(tmpDate<_dCalMinDate || tmpDate>_dCalMaxDate)
{s +="<td style='cursor:default;" + sStyle + "' nav='0'>&nbsp;</td>";}
else if(tmpDate.getMonth()!=iInitMonth)
{s +="<td onclick='" + sOnClick + "' style='cursor:hand;" + sStyle + "' d='" + iValue + "'>" + iDate + "</td>";}
else
{s +="<td onclick='" + sOnClick + "' style='cursor:hand;" + sStyle + "' d='" + iValue + "'>" + iDate + "</td>";}
tmpDate.setDate(iDate + 1);}
s +="</tr>";}
s +="</table>";return s;}
function getWeekNumber(oDate )
{var oYearStart=new Date(oDate.valueOf());oYearStart.setMonth(0, 1 );oYearStart.setHours(0, 0, 0, 0 );var iMSSinceYearStart=oDate.valueOf()- oYearStart.valueOf();var fWeeksSinceYearStart=iMSSinceYearStart / 604800000;var iWeeksSinceYearStart=Math.ceil(fWeeksSinceYearStart );if(iWeeksSinceYearStart==0 )
{iWeeksSinceYearStart=53;}
return iWeeksSinceYearStart;}
function DrawYear(D, sOnClick)
{var tmpDate=new Date(D );tmpDate.setMonth(0);tmpDate.setDate(1);var s="<table cellpadding='0' cellspacing='0' width='";s +=_sShowWeekNumbers=="1" ? iShowWeekCalWidth : iNoWeekCalWidth;s +="' style='border-collapse:collapse;table-layout:fixed'><tr><td style='font-family:verdana;font-size:8pt;text-align:center;border:1px solid #7b9ebd;height:18px;filter:progid:DXImageTransform.Microsoft.Gradient(GradientType=0,StartColorStr=#ffffff,EndColorStr=#cecfde);' colspan='3'><table cellpadding='0' cellspacing='0' style='table-layout:fixed'><tr height='18' style='font-family:verdana;font-size:8pt;cursor:hand;'>";var iValue	=tmpDate.valueOf();var iYear	=tmpDate.getFullYear();if(iYear <=_dCalMinDate.getFullYear())
s +="<td align='center' width='22' style='cursor:default;' nav='0'>&nbsp;</td>";else
s +="<td onclick='" + sOnClick + "' align='center' width='22' d='" + iValue + "' nav='2' onmouseover='this.runtimeStyle.color=\"#e89f2e\";' onmouseout='this.runtimeStyle.color=\"\";'>&lt;</td>";s +="<td align='center' style='cursor:default;' nav='0'>" + iYear + "</td>";if(iYear >=_dCalMaxDate.getFullYear())
s +="<td align='center' width='22' style='cursor:default;' nav='0'>&nbsp;</td>";else
s +="<td onclick='" + sOnClick + "' align='center' width='22' d='" + iValue + "' nav='2' onmouseover='this.runtimeStyle.color=\"#e89f2e\";' onmouseout='this.runtimeStyle.color=\"\";'>&gt;</td>";s +="</tr></table></td></tr>";var iMonth=0;for (i=0; i<4; i++)
{s +="<tr style='height:" + (31 + (i % 2))+ "px;' onmouseover='event.srcElement.runtimeStyle.color=\"#e89f2e\";' onmouseout='event.srcElement.runtimeStyle.color=\"\";'>";for(ii=0; ii<3; ii++)
{iMonth	=tmpDate.getMonth();iValue	=tmpDate.valueOf();s +="<td onclick='" + sOnClick + "' style='font-family:verdana;font-size:8pt;text-align:center;border:1px solid #7288ac;cursor:hand;' d='" + iValue + "' nav='1'>" + _sCalMonths[iMonth] + "</td>";tmpDate.setMonth(iMonth + 1);}
s +="</tr>";}
s +="</table>";return s;}
function ReturnDate(o)
{var D=new Date(parseInt(o.d, 10));if(o.nav)
{switch (parseInt(o.nav, 10))
{case 1:
var m=D.getMonth();var d=D.getDate();var a=new Array(31,28,31,30,31,30,31,31,30,31,30,31);if(D.getYear()% 4==0)
{a[1]=29;}
if(o.innerHTML=="&lt;")
{m--;if((m>0)&& (d>a[m]))
{						
D.setDate(a[m]);}
D.setMonth(m);}
else if(o.innerHTML=="&gt;")
{					
m++;if((m<12)&& (d>a[m]))
{						
D.setDate(a[m]);}
D.setMonth(m);}
_oCalPopUp.document.body.innerHTML=DrawMonth(D, "parent.ReturnDate(this);", HILITE_NONE, null);break;case 2:
if(o.innerHTML=="&lt;")
{D.setYear(D.getFullYear()- 1);}
else if(o.innerHTML=="&gt;")
{D.setYear(D.getFullYear()+ 1);}
_oCalPopUp.document.body.innerHTML=DrawYear(D, "parent.ReturnDate(this);");break;}
}
else
{_oCalInput.value		=FormatDate(D);_oCalInput.returnValue	=FormatUtcDate(D);_oCalInput.fireEvent("onchange");_oCalPopUp.hide();}
}
function ParseDate(s,silent)
{try
{var a=new Array(31,28,31,30,31,30,31,31,30,31,30,31);var S="";var i;for (i=0; i<s.length; i++)
{switch (s.charAt(i))
{case "/": S="/"; break;case ".": S="."; break;case "-": S="-"; break;}
if(S.length>0)
{break;}
}
var c;var iS=0;for (i=0; i<s.length; i++)
{c=s.charAt(i);if(c!=S && !IsValidNumber(c))
{throw  "Data inválida !" ;}
if(c==S)
{iS++;}
}
if(iS!=2)
{throw  "Data inválida !" ;}
var m, d, y;if(_iCalFormat<4)
{m=s.substring(0, s.indexOf(S));d=s.substring(m.length + 1, s.indexOf(S, m.length + 1));y=s.substr(m.length + d.length + 2);}
else if(_iCalFormat>3 && _iCalFormat<8)
{d=s.substring(0, s.indexOf(S));m=s.substring(d.length + 1, s.indexOf(S, d.length + 1));y=s.substr(m.length + d.length + 2);}
else
{y=s.substring(0, s.indexOf(S));m=s.substring(y.length + 1, s.indexOf(S, y.length + 1));d=s.substr(m.length + y.length + 2);}
m=parseInt(m, 10)- 1;if(m<0 || m>11)
{throw  "Data inválida !" ;}
y=parseInt(PadYear(y), 10);if(y<_dCalMinDate.getFullYear()|| y>_dCalMaxDate.getFullYear())
{throw  "Data inválida !" ;}
if(y % 4==0)
{a[1]=29;}
d=parseInt(d, 10);if(d==0 || d>a[m])
{throw  "Data inválida !" ;}
var D=new Date(y, m, d);if((D<_dCalMinDate)|| (D>_dCalMaxDate))
{throw  "Data inválida !" ;}
return D;}
catch(e)
{if(!silent)alert(e);return false;}
}
function ParseUtcDate(s)
{if(s.length>10)
{return new Date(parseInt(s.substr(0, 4), 10), (parseInt(s.substr(5, 2), 10)- 1), parseInt(s.substr(8, 2), 10), parseInt(s.substr(11, 2), 10), parseInt(s.substr(14, 2), 10), parseInt(s.substr(17, 2), 10 ));}
else
{return new Date(parseInt(s.substr(0, 4), 10), (parseInt(s.substr(5, 2), 10)- 1), parseInt(s.substr(8, 2), 10));}
}
function FormatDate(D)
{var m=D.getMonth()+ 1;var d=D.getDate();var y=D.getFullYear();if(_iCalFormat % 2==0 )
{y=String(y).substr(2, 2);}
if(_iCalFormat==2 || _iCalFormat==3 || _iCalFormat==6 || _iCalFormat==7 || _iCalFormat==10 || _iCalFormat==11)
{m=PadNumber(m);d=PadNumber(d);}
if(_iCalFormat<4)
{return m + _sCalSeperator + d + _sCalSeperator + y;}
if(_iCalFormat>3 && _iCalFormat<8)
{return d + _sCalSeperator + m + _sCalSeperator + y;}
return y + _sCalSeperator + m + _sCalSeperator + d;}
function FormatUtcDate(D)
{return String(D.getFullYear())+ "-" + PadNumber(String(D.getMonth()+ 1))+ "-" + PadNumber(String(D.getDate()))+ "T" + PadNumber(String(D.getHours()))+ ":" + PadNumber(String(D.getMinutes()))+ ":00";}
function PadNumber(s)
{if(String(s).length==1)
{return "0" + s;}
return s;}
function PadYear(s)
{s=new String(s);if(s.length==4)
{return s;}
if(s.length==1)
{s=0 + s;}
if(parseInt(s, 10)<30)
{return "20" + s;}
return "19" + s;}
function FormatUtcDateTime(s)
{var f=0;var hh=parseInt(s.substr(11, 2), 10);var mm=s.substr(14, 2);var ss=s.substr(17, 2);var tt="";switch (f)
{case 0: case 1: case 4: case 5:
if(hh==12)
{tt	=" PM";}
else if(hh>12)
{hh	=hh - 12;tt	=" PM";}
else
{tt	=" AM";}
break;}
switch (f)
{case 1: case 3: case 5: case 7:
hh=PadNumber(hh);break;}
if(f>3)
{return FormatDate(ParseUtcDate(s))+ " " + hh + ":" + mm + ":" + ss + tt;}
else
{return FormatDate(ParseUtcDate(s))+ " " + hh + ":" + mm + tt;}
}
function GetFirstDayOfWeek(D )
{var firstDay=new Date(D.getTime()- (D.getDay()* 24 * 60 * 60 * 1000 ));return new Date(firstDay.getTime()+ (_iCalStartDay * 24 * 60 * 60 * 1000 ));}
function GetLastDayOfWeek(D )
{var firstDay=GetFirstDayOfWeek(D );return new Date(firstDay.getTime()+ (6 * 24 * 60 * 60 * 1000 ));}
function IsValidNumber(n)
{if(n==null)
return false;var l=n.length;if(l==0)
return false;var s=0;if(n.charAt(0)=="-")
s=1;var i,c;for (i=s; i<l; i++)
{c=n.charCodeAt(i);if(c<46 || c>57)
return false;}
return true;}
// See the UTC time format definition at http://www.w3.org/TR/NOTE-datetime
var sAM="AM";var sPM="PM";function parseTime(sTime, iFormat )
{var sTime=sTime.toLowerCase();var rValidTimeElement=/^[0-9]{1,2}$/
var iHour;var iMinute;var iSecond;var iHourDiv=sTime.indexOf(":" );var sHour=sTime.substring(0, iHourDiv );if(! sHour.match(rValidTimeElement ))
{return new Date(NaN );}
var iEndOfDigits;if(iFormat==4 ||
iFormat==5 ||
iFormat==6 ||
iFormat==7 )
{var iMinDiv=sTime.indexOf(":", iHourDiv+1 );var sMinute=sTime.substring(iHourDiv+1, iMinDiv );if(! sMinute.match(rValidTimeElement ))
{return new Date(NaN );}
iMinute=parseInt(sMinute, 10 );var sSecond;if(iFormat==4 ||
iFormat==5 )
{var iSecDiv=sTime.indexOf(sAM.toLowerCase(), iMinDiv+1 );if(iSecDiv==-1 )
{iSecDiv=sTime.indexOf(sPM.toLowerCase(), iMinDiv+1 );}
sSecond=sTime.substring(iMinDiv+1, iSecDiv );}
else
{sSecond=sTime.substring(iMinDiv+1, sTime.length );}
sSecond=sSecond.replace(/ /, "" );if(! sSecond.match(rValidTimeElement ))
{return new Date(NaN );}
iSecond=parseInt(sSecond, 10 );iEndOfDigits=iSecDiv + sSecond.length + 1;}
else
{var sMinute;if(iFormat==2 ||
iFormat==3 )
{sMinute=sTime.substring(iHourDiv+1, sTime.length );}
else
{var iMinDiv=sTime.indexOf(sAM.toLowerCase(), iHourDiv+1 );if(iMinDiv==-1 )
{iMinDiv=sTime.indexOf(sPM.toLowerCase(), iHourDiv+1 );}
sMinute=sTime.substring(iHourDiv+1, iMinDiv );}
sMinute=sMinute.replace(/ /, "" );if(! sMinute.match(rValidTimeElement ))
{return new Date(NaN );}
iMinute=parseInt(sMinute, 10 );iSecond=0;iEndOfDigits=iMinDiv + sMinute.length + 1;}
if(iFormat==0 ||
iFormat==1 ||
iFormat==4 ||
iFormat==5 )
{iHour=parseInt(sHour, 10 );if(iHour <=0 || iHour>12 )
{return new Date(NaN );}
if(iHour==12 )
{iHour=0;}
sAmPm=sTime.substring(sTime.length - 2, sTime.length );if(sAmPm.toLowerCase()==sPM.toLowerCase())
{	
iHour +=12;}
else if(sAmPm.toLowerCase()==sAM.toLowerCase())
{}
else
{return new Date(NaN );}
}
else
{iHour=parseInt(sHour, 10 );}
if(iHour>23 || iHour<0 ||
iMinute>59 || iMinute<0 ||
iSecond>59 || iSecond<0 )
{return new Date(NaN );}
return new Date(2000, 0, 1, iHour, iMinute, iSecond, 0 );}
function parseUTCTime(sTime )
{var ss=sTime.split('-' );var s=ss[ 0 ];ss=s.split('+' );s=ss[ 0 ];ss=s.split('.' );var s=ss[ 0 ];s=s.replace(/Z/, "" );var oDate=new Date("1/1/00 " + s );if(ss.length==2 )
{sMilliseconds=ss[ 1 ];oDate.setMilliseconds(parseInt(sMilliseconds ));}
return oDate;}
function timeToString(oTime, iFormat )
{switch(iFormat )
{case 0:
{return get12HourClockHours(oTime )+ ":" + makeTwoDigitString(oTime.getMinutes())+ " " + makeAMPM(oTime );}
case 1:
{return makeTwoDigitString(get12HourClockHours(oTime ))+ ":" + makeTwoDigitString(oTime.getMinutes())+ " " + makeAMPM(oTime );}
case 2:
{return oTime.getHours()+ ":" + makeTwoDigitString(oTime.getMinutes());}
case 3:
{return makeTwoDigitString(oTime.getHours())+ ":" + makeTwoDigitString(oTime.getMinutes());}
case 4:
{return get12HourClockHours(oTime )+ ":" + makeTwoDigitString(oTime.getMinutes())+ ":" + makeTwoDigitString(oTime.getSeconds())+ " " + makeAMPM(oTime );}
case 5:
{return makeTwoDigitString(get12HourClockHours(oTime ))+ ":" + makeTwoDigitString(oTime.getMinutes())+ ":" + makeTwoDigitString(oTime.getSeconds())+ " " + makeAMPM(oTime );}
case 6:
{return oTime.getHours()+ ":" + makeTwoDigitString(oTime.getMinutes())+ ":" + makeTwoDigitString(oTime.getSeconds());}
case 7:
{return makeTwoDigitString(oTime.getHours())+ ":" + makeTwoDigitString(oTime.getMinutes())+ ":" + makeTwoDigitString(oTime.getSeconds());}
}
}
function timeToUTCString(oTime )
{if(isNaN(oTime))
{return null;}
var sRVal=makeTwoDigitString(oTime.getHours())+ ":"
+ makeTwoDigitString(oTime.getMinutes())
+ ":" + makeTwoDigitString(oTime.getSeconds());/*if(oTime.getMilliseconds()>0 )
{sRVal +="." + oTime.getMilliseconds();}*/
return sRVal;}
function makeTwoDigitString(iNumber )
{if(iNumber>9 )
{return iNumber.toString();}
else
{return "0" + iNumber.toString();}
}
function get12HourClockHours(oTime )
{if(oTime.getHours()>12 )
{return oTime.getHours()- 12;}
else if(oTime.getHours()==0 )
{return 12;}
else
{return oTime.getHours();}
}
function makeAMPM(oTime )
{if(oTime.getHours()>=12 )
{return sPM;}
else
{return sAM;}
}
function formatFloat(sNum)
{var sBase="";var sRem="";var bRem=false;;for(i=0; i<sNum.length; i++)
{curChar=sNum.charAt(i);if(bRem)
{sRem +=curChar;if(sRem.length==2)break;}
else
{sBase +=curChar;}
if(curChar=='.')
{bRem=true;continue;}
}
return sBase + sRem;}
function formatDuration(iMinutes )
{if(isNaN(parseInt(iMinutes, 10))|| (iMinutes<0 ))
{iMinutes=0;}
var rVal;if(iMinutes<60)
{if(iMinutes==1)
{rVal=""+iMinutes +" minute";}
else
{rVal=""+iMinutes+" minutes";}
} else if(iMinutes >=60 && iMinutes<1440 )
{var iHours=iMinutes / 60;if(iHours==1)
{rVal=""+formatFloat(iHours.toString())+" hour";}
else
{rVal=""+formatFloat(iHours.toString())+" hours";}
} else if(iMinutes >=1440 )
{var iHours=iMinutes / 60;var iDays=iHours / 24;if(iDays==1)
{rVal=""+formatFloat(iDays.toString())+" day";}
else
{rVal=""+formatFloat(iDays.toString())+" days";}
}
return rVal;}
function UpdateMessageBody(fieldh,valuefor)
{	
if(fieldh.document.readyState=="complete")
{fieldh.document.body.innerHTML=valuefor.innerHTML;fieldh.document.body.original=fieldh.document.body.innerHTML;}
}
function Save(event)
{UpdateMessageBody();}
function FormatInteger(vValue, iMinValue, iMaxValue, bGrouping, sFormat)
{try
{vValue=parseInt(RemoveFormatting(vValue, sFormat), 10);if(isNaN(vValue)|| vValue<iMinValue || vValue>iMaxValue)
{throw  "O valor tem que estar entre "  + iMinValue + " e " + iMaxValue + ".";}
return AddFormatting(vValue, false, 0, bGrouping, sFormat);}
catch(e)
{alert(e);return false;}
}
function FormatFloat(vValue, iMinValue, iMaxValue, bGrouping, sFormat, iAccuracy, minDec)
{try
{		
var sNewNum=RemoveFormatting(vValue, sFormat);sNewNum=MakeValidFloat(sNewNum, iAccuracy, minDec);var iVal=parseFloat(sNewNum);sNewNum=AddFormatting(sNewNum, true, iAccuracy, bGrouping, sFormat);if((iVal<iMinValue)|| (iVal>iMaxValue))
{throw "O valor tem que estar entre " + iMinValue + " e " + iMaxValue + ".";}
return sNewNum;}
catch(e)
{alert(e);return false;}
}
function AddFormatting(vValue, bIsFloat, iAccuracy, bGrouping, sFormat)
{var i;var iLen;var sFormatted="";var sValue=vValue.toString();var sSign=("-"==sValue.charAt(1))? "-": "";while (sValue.charAt(0)=="0" && sValue.length>1)
{sValue=sValue.substring(1, sValue.length);}
if(isNaN(sValue.charAt(0)))
{if(	sValue.charAt(0)!="-" )
{sValue="0" + sValue;}
}
if(isNaN(sValue.charAt(sValue.length-1)))
{sValue=sValue.substr(0,sValue.length-1);}
var sGrpSym	=(sFormat=="us")? "," : ".";var	sDecSym	=(sFormat=="us")? "." : ",";if(sSign.length)
{sValue=sValue.slice(sSign.length);}
if(!bIsFloat || (-1==(iLen=sValue.indexOf("."))) )
{iLen=sValue.length;}
for (i=0; i<iLen; i++)
{sFormatted=sValue.charAt(iLen - 1 - i)+ sFormatted;if(bGrouping)
{if((i + 1)% 3==0 && (iLen - 1 - i)!=0 && sValue.charAt(iLen - 2 - i)!="-")
{sFormatted=sGrpSym + sFormatted;}
}
}
if(bIsFloat && iAccuracy>0)
{if(sValue.slice(iLen + 1)!=null && sValue.slice(iLen + 1)!="")
{sFormatted +=(sDecSym + sValue.slice(iLen + 1));}
}
sValue=sSign + sFormatted;return sValue;}
function RemoveFormatting(vValue, sFormat)
{vValue=new String(vValue);if(sFormat=="us")
{vValue=vValue.replace(/\,/g, "");}
else
{vValue=vValue.replace(/\./g, "");vValue=vValue.replace(/\,/g, ".");}
if(!IsValid(vValue))
{throw  "Número inválido" ;}
return vValue;}
function IsValid(sValue)
{var sPattern=new RegExp(/^-?(\d|\.|\,){0,}$/);	
var validPattern=sValue.match(sPattern);return (null!=validPattern);}
function MakeValidFloat(sValue, iAccuracy, minDec)
{var nResults=sValue.split(".");if(nResults.length==1 && minDec==0)
{return nResults[0];}
var sNewNum=nResults[0] + ".";var decNum;if(nResults.length==2)
{decNum=nResults[1];}
else
{decNum="";}
if(decNum.length >=iAccuracy)
{var sPattern="-?\\d*\\.\\d{" + iAccuracy + "}";sNewNum +=decNum;nResults=sNewNum.match(sPattern);	
return nResults[0];}
for (i=0; i<iAccuracy; i++)
{if(decNum.length >=minDec)
{return sNewNum + decNum;}
else
{decNum +="0";}
}
return sNewNum + decNum;}

function addJavascript(jsname,pos) {
	var th = document.getElementsByTagName(pos)[0];
	var s = document.createElement('script');
	s.setAttribute('type','text/javascript');
	s.setAttribute('src',jsname);
	th.appendChild(s);
	} 