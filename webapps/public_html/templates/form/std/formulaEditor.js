//<script LANGUAGE="javascript" >

function getInfo(){
	
	
}

function setInfoItemFormula(){
	var e=event.srcElement;
	event.dataTransfer.setData('Text',"formula:"+e.id);
	event.dataTransfer.effectAllowed = 'copyMove';
	
}


function removeItemFromFormula(){

var divFormula=document.getElementById("formula");
var	txtobj=window.event.dataTransfer.getData('Text').split(":");
	
	if ( txtobj[1] && txtobj[0] == "formula" ) {
		
		var e=document.getElementById( txtobj[1] );
		var e1=e.nextSibling;
		var e2=e.previousSibling;
		if(e2.id=="pointer") e2=e1.nextSibling;
		divFormula.removeChild(e);
		divFormula.removeChild(e1);
		divFormula.removeChild(e2);
	}
}					
function click_onremoveItem()
{
var divFormula=document.getElementById("formula");
	window.event.returnValue=false;
	var e=window.event.srcElement.previousSibling;
	var e1=e.nextSibling;
	var e2=e.previousSibling;
	if(e2.id=="pointer") e2=e1.nextSibling;
	divFormula.removeChild(e);
	divFormula.removeChild(e1);
	divFormula.removeChild(e2);

}

function enterRecycleBin(){
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('formula')>-1){
		event.dataTransfer.effectAllowed = 'copyMove';
		window.event.returnValue=false;
	}
	else event.dataTransfer.effectAllowed = 'none';

}

function overRecycleBin(){
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('formula')>-1){
		event.dataTransfer.effectAllowed = 'copyMove';
		window.event.returnValue=false;
	}
	else event.dataTransfer.effectAllowed = 'none';
	
}

function movePointer(){
//xy=xx.parentElement;xi=xy.removeChild(xx);xpp=this;xy.insertBefore(xi,xpp)
	var divFormula=document.getElementById("formula");
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('pointer')>-1 ) {
		var p = document.getElementById("pointer");
			p =divFormula.removeChild(p);
		    divFormula.insertBefore(p,event.srcElement.nextSibling);
	}
	else {
	//mover formula
		var	txtobj=info.split(":");
	
		if ( txtobj[1] && txtobj[0] == "formula" ) {
			var e=document.getElementById( txtobj[1] );
			var e1=e.nextSibling;
			var e2=e.previousSibling;
			
			var e_before=event.srcElement.nextSibling;
				if(e_before!=e){
				if(e2.id=="pointer") e2=e1.nextSibling;
				divFormula.removeChild(e);
				divFormula.removeChild(e1);
				divFormula.removeChild(e2);
			
			
				divFormula.insertBefore(e,e_before);
				divFormula.insertBefore(e1,e_before);
				divFormula.insertBefore(e2,e_before);
			}
		}

	
	
	}

}

function setInfoPointer(){
	event.dataTransfer.setData('Text','pointer');
	event.dataTransfer.effectAllowed = 'move';
}

function enterMovePointerTo(){
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('pointer')>-1 ||info.indexOf('formula')>-1 ){
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
		window.event.srcElement.src='templates/form/std/formulaEditor/sep_sel.gif';
	}
	else event.dataTransfer.effectAllowed = 'none';
}

function leaveMovePointerTo(){
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('pointer')>-1 ||info.indexOf('formula')>-1 ){
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
		event.srcElement.src='templates/form/std/formulaEditor/sep.gif';
	}
	else event.dataTransfer.effectAllowed = 'none';

}

function overMovePointerTo(){
	var info=event.dataTransfer.getData("Text");
	if( info.indexOf('pointer')>-1 || info.indexOf('formula')>-1 ){
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
		window.event.srcElement.src='templates/form/std/formulaEditor/sep_sel.gif';
	}
	else event.dataTransfer.effectAllowed = 'none'; 
}



function cancelDrag(){
window.event.returnValue=false;
}

var counter=0;

function addItem(text,className,internalv){

var divFormula=document.getElementById("formula");

var a = document.createElement("a");
var id = document.createElement("img");
var sep = document.createElement("img");
	a.id=text+counter;
	a.href="javascript:";
	a.className=className;
	if(internalv)a.internalv=internalv;
	a.ondragstart=setInfoItemFormula;
	a.innerText=text;

	id.style.position="relative";
	id.style.top="5";
	id.style.cursor="hand";
	id.title="Clique para apagar Item";
	id.src="templates/form/std/formulaEditor/delete.gif" 
	id.onclick=click_onremoveItem;
	id.width=5 ;
	id.height=5;
	
	sep.id=text+counter+"s";
	sep.ondrop=movePointer;
	sep.ondragenter=enterMovePointerTo;
	sep.ondragover=overMovePointerTo;
	sep.ondragleave=leaveMovePointerTo;
	sep.ondragstart=cancelDrag;
	sep.src="templates/form/std/formulaEditor/sep.gif";
	sep.style.position="relative";
	sep.type="sep";
	sep.style.top="5";
	sep.width="6";
	sep.height=15;

counter++;
var p = document.getElementById("pointer");
if(p.previousSibling.type=="sep") p=p.previousSibling;
divFormula.insertBefore(id ,p);
divFormula.insertBefore(a,id);
divFormula.insertBefore(sep,a); 


}

function addAttribute(objectName,attributeName,ext_obj,ext_atr){
	addItem(ext_obj+"."+ext_atr,"formula_attribute",objectName+"."+attributeName);
}

function addSinal(sinal){
	addItem(sinal,"formula_sinal");
}

function addText(text){
	addItem(text,"formula_text");
}

function addNumber(text){
	addItem(text,"formula_number");
}

function addDateLiteral(text){
	addItem(text,"formula_dateLiteral");
}


function addParentises(text){
	addItem(text,"formula_parentises");
}




