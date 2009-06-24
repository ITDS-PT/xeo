//<SCRIPT>
//var colorRowOver="#CCCCCC";

function onOver_Form_std(event){
  	var e = getElement(event);
  	var c=e.className;
  	if(event)
  	event.cancelBubble=true;
}

function onOut_Form_std(event){
	var e = getElement(event);
  	var c=e.className;
  	if(event)
  	event.cancelBubble=true;
  	
}

function onClick_Form_std(event){
	e=getElement(event);
	if(event)
	event.cancelBubble=true;
      
}

