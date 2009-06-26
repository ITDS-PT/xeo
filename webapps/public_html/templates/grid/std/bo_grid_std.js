//<SCRIPT language="Jscript">

//function cleanUrl(x){
//  var x=removeUrlAttribute(x,"moveLines");
//  x=removeUrlAttribute(x,"toExecute");
//  x=removeUrlAttribute(x,"drop_info");
//  x=removeUrlAttribute(x,"drag_to_col_header");
//  x=removeUrlAttribute(x,"drag_to_col_group");
//  x=removeUrlAttribute(x,"tree_key");
//  x=removeUrlAttribute(x,"orderCol");
//  x=removeUrlAttribute(x,"openGroup");
//  x=removeUrlAttribute(x,"closeGroup");
//  x=removeUrlAttribute(x,"selectedLines");
//  return x;
//}


function reloadGrid(){
//var x=setUrlAttribute(window.location.href,"docid",getDocId());
//x=cleanUrl(x);
//wait();
//window.location.href=x;
submitGrid()
}
function submitGrid(){
//var x=setUrlAttribute(window.location.href,"docid",getDocId());
//x=cleanUrl(x);
//xxx=boForm.BuildXml(false,false);
boFormSubmit.boFormSubmitXml.value=boForm.BuildXml(false,false);;
boFormSubmit.submit();
}


function onOver_GridHeader_std(event){
  	var e = getElement(event);
  	var c=e.className;
  	var tpl=c.substr(c.indexOf('_')+1);
  	
	switch (e.tagName)
	{
		case "IMG": case "NOBR": return;
	}
	if (e.className == "ghSort_"+tpl && getRuntimeStyle(e).backgroundImage != "url(templates/grid/"+tpl+"/ghBackSel.gif)")
	{
		getRuntimeStyle(e).backgroundImage = "url(templates/grid/"+tpl+"/ghBackSel.gif)";
		
		if (e.id.indexOf("ExpanderParent")>-1)
		{
			
			getRuntimeStyle(e.nextSibling).backgroundImage = "url(templates/grid/"+tpl+"/ghBackSel.gif)";
		}
		
		_hso.lastOver = e;
		
		if (e.id.indexOf("AutoExpander")>-1)
		{
			
			getRuntimeStyle(e.previousSibling).backgroundImage = "url(templates/grid/"+tpl+"/ghBackSel.gif)";
			_hso.lastOver = e.previousSibling;
		}
	}
}

function onOut_GridHeader_std(event){
    
    var e = getToElement(event); 
  	//var e = event.toElement;
  	
  	if(!e) var tpl='std';
  	else{
  		var c=e.className;
  		var tpl=c.substr(c.indexOf('_')+1);
  		}

	if (_hso.lastOver && !(e && (e == _hso.lastOver || e.parentNode == _hso.lastOver || e.parentNode.parentNode == _hso.lastOver)))
	{
		if (!(e && e.id.indexOf("AutoExpander")>-1 && getRuntimeStyle(e.previousSibling).backgroundImage == "url(templates/grid/"+tpl+"/ghBackSel.gif)"))
		{
			//_hso.lastOver.runtimeStyle.backgroundImage = "";
			getRuntimeStyle(_hso.lastOver).backgroundImage = "";
			if (_hso.lastOver.id.indexOf("ExpanderParent")>-1)
			{
				//_hso.lastOver.nextSibling.runtimeStyle.backgroundImage = "";
				getRuntimeStyle(_hso.lastOver.nextSibling).backgroundImage = "";
			}
			else if (e && e.id.indexOf("AutoExpander")>-1)
			{
				//e.previousSibling.runtimeStyle.backgroundImage = "";
				getRuntimeStyle(_hso.lastOver.previousSibling).backgroundImage = "";
			}
			_hso.lastOver = null;
		}
	}
}

function onClick_GridHeader_std(event){
	//var o = event.srcElement;
	var o = getElement(event);
    var oTR = o;
	while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	
	switch (o.tagName)
	{
		case "INPUT":	selectAllGridRow_std(o); break;
		case "IMG":		break; 
		default:
			if( oTR.otype != undefined )
			{
			//	window.parent.parent.openObj(oTR.otype, oTR.oid);
			}
			else
			{
			//	window.parent.parent.openObj(gridBodyTable.oname, oTR.oid);
			}
				
	}


  
}



function onOver_GridBody_std(event){
  	//var e = event.srcElement;
  	var e = getElement(event);
  	if(e == null) return;
  	var c=e.className;
  	//window.status=e.select;
  	
 	if ( ""+e.select=="none"){
  	   return;
  	}
  	
  	var tpl=c.substr(c.indexOf('_')+1);
 	if (_hso.lastGlow) rowOff_Std(_hso.lastGlow);
	_hso.lastGlow = e;
	while (_hso.lastGlow.tagName != "TR")
	{
		_hso.lastGlow = _hso.lastGlow.parentNode;
	}
	if(_hso.lastGlow.id.indexOf('expandedRow') >-1){
		_hso.lastGlow=null;
		return;
	} 
	
	//_hso.lastGlow.runtimeStyle.backgroundColor = colorRowOver;
	if(_hso.hb.rows[0].cells[0].colSpan==1)
	getRuntimeStyle(_hso.lastGlow).backgroundColor = colorRowOver;
	event.cancelBubble=true;
}

function onOut_GridBody_std(event){
	rowOff_Std(_hso.lastGlow);
	event.cancelBubble=true;
}

function rowOn_Std(e){
 getRuntimeStyle(e).backgroundColor = colorRowOver;
}

function rowOff_Std(e){
  if(e){
  	 getRuntimeStyle(e).backgroundColor = "";
	 e=null;
  }
}


function deleteSelected( gnumber , xParent_attribute, otherPars ){
  var grid=document.getElementById("g"+gnumber+"_body");

  
  var cols=0;
  var r=grid.rows;
  var xfaz=false;
  var rCheck=0;
  if( r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){
    rCheck=1;  
  }
  
  for ( var i=0 ; i < r.length ; i++ ){
    //if(r[i].cells[0].firstChild && r[i].cells[0].firstChild.checked  ) {
	if(r[i].cells[rCheck].firstChild && r[i].cells[rCheck].firstChild.checked  ) {
	    cols=r[i].cells.length;
		grid.deleteRow(i);
		xfaz=true;
	  i--;
	}
  }
  if ( xfaz ){
		//var href = window.location.href;
		//var xqry = href.substring(href.indexOf("?")+1);
		//var xargs = xqry.split("&");
		var xattributeName="";
		var xparent_boui="";
		
		//for ( var i = 0 ; i < xargs.length ; i++ ){
		//	var x= xargs[i].split("=");
			//alert(x[0]+" igaul a "+ x[1]);
		//	if ( x[0] == "parent_attribute" ){
		//	   xattributeName = x[1]
		//	}
		//	else if (x[0] == "parent_boui") {
		//	  xparent_boui=x[1];
		//	}           
	//	}
        if(boFormSubmit != null)
        {
            if(boFormSubmit.parent_attribute != null)
            {                
		        xattributeName = boFormSubmit.parent_attribute.value;
		    }
		    if(boFormSubmit.parent_boui != null)
		    {
		        xparent_boui = boFormSubmit.parent_boui.value;
		    }
		}		
		//xattributeName = boFormSubmit.parent_attribute.value;
		//xparent_boui = boFormSubmit.parent_boui.value;
		
		
		if ( grid.mode !='resultBridge' &&  xattributeName != "" && xparent_boui !="" ){
  
				var rows=grid.rows;
				var bouis=[];
				var b;
				for ( var i = 0 ; i< rows.length ; i++ ){
					
					b = rows[i].id.split("__");
					if ( b[2] ) {
						bouis[ bouis.length ] = b[2];
					}
				}
				
				
				 if( parent && parent.boFormSubmit ) {
		//			
		//			with ( parent.boFormSubmit ){
		//				var xtag="object__"+xparent_boui+"__"+xattributeName ;
		//				boFormSubmitXml.value="<bo boui='"+xparent_boui+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";
		//				
		//				if ( xParent_attribute ){
		//					look_parentAttribute.value=xParent_attribute;
		//				}
		//				submit();
		//			}
		//			
					if ( !boFormSubmit.look_parentAttribute && xParent_attribute ) createHiddenInput("look_parentAttribute","")
					
					if ( otherPars ){
						for ( i =0 ; i< otherPars.length ;i+=2){
							createHiddenInput(otherPars[i],otherPars[i+1])	
						}
					
					}
					xxx=boForm.BuildXml(false,false);
							
					
					    if ( parent.boFormSubmit.boFormSubmitXml )
					    {
							with ( boFormSubmit )
							{
								var xtag="object__"+xparent_boui+"__"+xattributeName ;
								boFormSubmitXml.value="<bo boui='"+xparent_boui+"'>"+xxx+"<"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";
								if ( xParent_attribute )
								{
									look_parentAttribute.value=xParent_attribute;
								}
								submit();
							}
						}
						else if ( parent.boFormSubmit.selectedBouis )
						{
							with ( boFormSubmit )
							{
								selectedBouis.value=bouis.join(';');
								submit();
							}
						}
						
				
					
				}  		

  
  
		}

		var rows=grid.rows;
		if (grid.rows.length==0 && cols>0 ){
			// a tabela está vazia
			var newTR=grid.insertRow();
			var newTD=newTR.insertCell(0);
			grid.style.height="100%";
			newTD.colSpan=cols;
												
			var xINNER="<TABLE  style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD style='COLOR: #999999; BORDER:0px' align=middle width='100%'>";
			xINNER+="Não existem objectos";
			xINNER+="</TD></TR></TBODY></TABLE>";
			newTD.innerHTML=xINNER;
		 }
   }
}


function addRemoveObjToFrame( oTR , o ){
   var resultFrame=eval("window.parent.resultframe");
	var elems=resultFrame.document.getElementsByTagName("TABLE");
	var tblToadd=null
	for (var i=0 ;elems.length; i++){
	  if(elems[i].container){
	     tblToadd=elems[i];
	     break;
	  }
						  
	}
						
	if( tblToadd!=null ){
		if (o.checked){						    
			var xBOUI=oTR.id.split("__")[2];
			var found=false;
			for ( var i=0 ; i < tblToadd.rows.length ; i++ ){
					if ( tblToadd.rows[i].id.split("__")[2] == xBOUI ){
										
						found=true;  
										  
					}
			}
			if ( ! found ){
				var rows=tblToadd.rows;
				if (tblToadd.rows[0].cells[0].colSpan>1){
					// a tabela está vazia
					var newTR=tblToadd.rows[0];
					newTR.deleteCell(0);
					tblToadd.style.height="";
					}
				else{
					var newTR=tblToadd.insertRow();
					}
										
				var xid=tblToadd.id.split("_")[0];
				var xid1=_hso.hb.id.split("_")[0];
				var cells=oTR.cells;
				var oTD=null;
				newTR.id=oTR.id.replace( new RegExp('#'+xid1+'#','g'),xid ) ;
				for ( var i=-1; i<cells.length ; i++)
					{
						if( i == -1)
						{
							oTD=newTR.insertCell();
							//imagem
						}
						else{
						
						oTD=newTR.insertCell();
						oTD.className=cells[i].className;
						oTD.innerHTML=cells[i].innerHTML;
            oTD.colSpan=cells[i].colSpan;
						}
					}
		   }
		}
		else{
			var xBOUI=oTR.id.split("__")[2];
			for ( var i=0 ; i < tblToadd.rows.length ; i++ ){
					if ( tblToadd.rows[i].id.split("__")[2] == xBOUI ){
						tblToadd.deleteRow(i);
					  i--;
										  
					}
			}
			var rows=tblToadd.rows;
			if (tblToadd.rows.length==0){
				// a tabela está vazia
				var newTR=tblToadd.insertRow();
				var newTD=newTR.insertCell(0);
				tblToadd.style.height="100%";
				newTD.colSpan=oTR.cells.length+2;
									
				var xINNER="<TABLE  style='height:100%;width:100%;border:0px' morerecords='0'><TBODY><TR><TD style='COLOR: #999999; BORDER:0px' align=middle width='100%'>";
				xINNER+="Não existem objectos";
				xINNER+="</TD></TR></TBODY></TABLE>";
				newTD.innerHTML=xINNER;
									
									
				}
									
								
								
		}
							
	}

}
var cancelOpen=false;
function onDoubleClick_GridBody(event)
{
	var e = getElement(event);
	var o = getElement(event);
    var oTR = o;
    var mode=_hso.hb.mode;
	var waitingDetachAttribute= _hso.hb.waitingDetachAttribute;
	cancelOpen=false;
	while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	
	var xid=oTR.id;
	var bouiClicked="";
	if( oTR.userDblClick)
	{
		eval(oTR.userDblClick);
		return;
	}
	if(xid)
	{
	  var df=xid.split("__");
	  //if ( df[2] =='0' && ""+oTR.selectRecordNone!="undefined") submitSelectOne( oTR )			  
	  //else  
	  open_Obj(oTR,df[1],df[2]);
	  bouiClicked=df[2];
	}
	if(!cancelOpen)
	{
		
		var r=_hso.hb.rows;
		var rCheck=0;
		if( r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){
			rCheck=1;  
		}
		for(var i=0;i<r.length;i++)
		{
			if (r[i].cells[rCheck].firstChild&&r[i].cells[rCheck].firstChild.checked )
			{
				xid=r[i].id;
				var df=xid.split("__");
				if ( df[2]!= bouiClicked)open_Obj(r[i],df[1],df[2]);
			}
		}
	} 
}

function onClick_GridBody_std(event){
	//e=window.event.srcElement;
	
	var e = getElement(event);
	var o = getElement(event);
    var oTR = o;
    var mode=_hso.hb.mode;
	var waitingDetachAttribute= _hso.hb.waitingDetachAttribute;
	while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	
	
	if ( mode =="searchone" && o.tagName!="IMG")
	{
		if(oTR.id){
		    
		    if ( ""+waitingDetachAttribute!="undefined" )
		    {
				var b=oTR.id.split("__");// b[2] tem o BOUI do objecto
				if ( ""+oTR.selectRecordNone!="undefined" ) b[2]="";
				
				winmain().setWDA ( waitingDetachAttribute , b[2] , getDocId() );
				winmain().ndl[getIDX()].close();
		    }
		    else
			submitSelectOne( oTR );
			if( oTR.userClick) eval(oTR.userClick);
		}  
			
	}
	else
	{
		switch (o.tagName)
		{
			case "INPUT":	{
					selectGridRow_std(o);
					if( mode=="searchmulti")
					{
						addRemoveObjToFrame(oTR , o);
					}
					break;
					}
			case "IMG":		if (o.src.indexOf('quickview') > 0) { expandGridRow_std(o); break; }
			
			default:
					
					var r=oTR;
					
					var rCheck=0;
					if( r.cells[0].firstChild && r.cells[0].firstChild.tagName=='IMG' )
					{
					  rCheck=1;  
					}
					var i=r.cells[rCheck].firstChild;
					if(i && i.checked != null)
					{
					    if( i.checked ) i.checked=false;
					    else i.checked=true;
				        selectGridRow_std( i );
				        if( mode=="searchmulti")
					    {
						    addRemoveObjToFrame(oTR , i);
					    }
				    }
				    
				    if( r.userClick) eval(r.userClick);
			 		//selectGridRow_std(o);
			 // var xid=oTR.id;
			 // if(xid){
			 //    var df=xid.split("__");
			 //    if ( df[2] =='0' && ""+oTR.selectRecordNone!="undefined") submitSelectOne( oTR )			  
			 //    else  open_Obj(oTR,df[1],df[2]);
			 // }
				
				
		}
	}

  
}

function open_Obj(oTR,objectName,ObjectBoui){

    if ( objectName == 'Ebo_ShortCut' && event.srcElement.tagName!='IMG' )
    {
      var cells=oTR.cells;
      for ( var i=0; i<cells.length; i++)
      {
         if (cells[i].firstChild && cells[i].firstChild.exec=='yes' )
         {
            eval( cells[i].firstChild.innerText );
            return
         }
      }
    }
	  
	
	
	
    var xurl="";
    var isProtocol=false;
    if ( parent && parent.boFormSubmit && parent.boFormSubmit.editingTemplate &&boFormSubmit.parent_attribute.value=='DAO')
    {
    isProtocol=true;
    }
    
    if ( oTR.isextendlist || (  oTR.hasRightsToSaveParent && oTR.hasRightsToSaveParent=='no') || objectName.toLowerCase()=="ebo_template" )
    {
       isProtocol=true;
    }
    if (  boFormSubmit.docid && boFormSubmit.docid.value 
		 && boFormSubmit.parent_boui && boFormSubmit.parent_boui.value
		 && boFormSubmit.parent_attribute && boFormSubmit.parent_attribute.value 
		 && !boFormSubmit.resultBridge
		 )
		 {
			if ( !isProtocol )
			{
				var xurl="&ctxParentIdx="+boFormSubmit.docid.value;
					 xurl+="&ctxParent="+boFormSubmit.parent_boui.value;
					 xurl+="&addToCtxParentBridge="+boFormSubmit.parent_attribute.value;
					xurl+="&docid="+boFormSubmit.docid.value;
					xurl+="&relatedClientIDX="+getIDX();
					cancelOpen=true;
					
					winmain().nextPage( getIDX() , objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl );
					;
			}
			else
			{
				    var xurl="&relatedParentDocid="+boFormSubmit.docid.value;
					xurl+="&relatedParent="+boFormSubmit.parent_boui.value;
					xurl+="&relatedParentBridge="+boFormSubmit.parent_attribute.value;
					xurl+="&relatedClientIDX="+getIDX();
					winmain().openDoc("medium",objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl);
			}
			
			
		 } 
		 else
		 {
			winmain().openDoc("medium",objectName.toLowerCase(),"edit","method=edit&boui="+ObjectBoui+xurl);
	     }
	
}

function selectAllGridRow_std(o)
{
 var c=o.parentNode.className;
 var tpl=c.substr(c.indexOf('_')+1);
 var isExpanded=c.indexOf('Expanded')>-1;
 var isSelect=o.checked;
 
 var x=o.id;
 var refered_Obj=x.substr(0,x.indexOf('_' ) );
 so(refered_Obj);
 var r=_hso.hb.rows;
 var mode=_hso.hb.mode;
 var rCheck=0;
  if( r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){
    rCheck=1;  
  }
  
 
 for(var i=0;i<r.length;i++){
   if(r[i].cells[rCheck]&&r[i].cells[rCheck].firstChild){
     r[i].cells[rCheck].firstChild.checked=isSelect;
     selectGridRow_std(r[i].cells[rCheck].firstChild);
     if( mode=="searchmulti"){
		addRemoveObjToFrame(r[i] , r[i].cells[rCheck].firstChild );
  	 }
     
     
    }
 }

 
 //var rows=e.parentNode.parentNode.parentNode.rows;
 
  
}

function selectForceGridRow_std(exclusive,ele){

	if (ele ) var o=ele
	else var o = getElement(event);
    var oTR = o;
    while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	var c=oTR.cells(0).className;
	var tpl=c.substr(c.indexOf('_')+1);
	var isExpanded=c.indexOf('Expanded')>-1;
	var s='gCell'+(isExpanded? 'Expanded':'' )+('Sel' )+'_'+tpl;
	
	applyStyle(oTR, s);
	
	if ( oTR.relatedElement )
	{
		var el =document.getElementById( oTR.relatedElement );
		if ( el ) el.style.display='';
	}
	
	if ( exclusive )
	{
		var oRows=oTR.parentNode.rows;
		for ( var i = 0 ; i < oRows.length ; i++ )
		{
			if ( oTR!=oRows[i] ) 
			{
				c=oRows[i].cells(0).className;
				tpl=c.substr(c.indexOf('_')+1);
				isExpanded=c.indexOf('Expanded')>-1;
				s='gCell'+(isExpanded? 'Expanded':'' )+('' )+'_'+tpl;
				applyStyle(oRows[i] , s);
				if ( oRows[i].relatedElement )
				{
					var el =document.getElementById( oRows[i].relatedElement );
					if ( el ) el.style.display='none';
				}
			}
		}
	}
	
}

function selectGridRow_std(o)
{
 var c=o.parentNode.className;
 var tpl=c.substr(c.indexOf('_')+1);
 var isExpanded=c.indexOf('Expanded')>-1;
 var isSelect=o.checked;
 
 var row=o.parentNode.parentNode;
 var s='gCell'+(isExpanded? 'Expanded':'' )+(isSelect? 'Sel':'' )+'_'+tpl;
 
 applyStyle(row, s);
 
 if(!_hso.boselect &&_hso.boselect!=0) refreshStatusGrid_std();
 if(isSelect)_hso.boselect++;
 else _hso.boselect--;
  refreshStatusGrid_std();
 window.status=_hso.boselect;
 
 //var rows=o.parentNode.parentNode.parentNode.rows;
 
  
}

function grid_DragOver(){
 var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveLin')>-1){
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
    
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
	
		for ( var i=0; i< oTR.cells.length;i++)
		{

			oTR.cells[i].runtimeStyle.borderBottom='2px solid #246BD6';
		    //oTR.cells[i].runtimeStyle.borderBottomStyle='solid';
		    //oTR.cells[i].runtimeStyle.borderBottomColor='#000000';
  		    //oTR.cells[i].runtimeStyle.borderBottomWidth=2;
			
		}
	}
else if ( info && info.indexOf('dragObject:')>-1 && 
	  	  (!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
  	    ) 
	{
		event.dataTransfer.effectAllowed = 'copy';
		window.event.returnValue=false;
    
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
	
		for ( var i=0; i< oTR.cells.length;i++)
		{

			oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';
			
		}
	}
	else event.dataTransfer.effectAllowed = 'none';
}

function grid_Drop(){
    var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveLin')>-1){
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
		var href = window.location.href;
		var xqry = href.substring(href.indexOf("?")+1);
		var xargs = xqry.split("&");
		var xattributeName="";
		var xparent_boui="";
//		
//		for ( var i = 0 ; i < xargs.length ; i++ ){
//			var x= xargs[i].split("=");
//			if ( x[0] == "parent_attribute" ){
//			   xattributeName = x[1]
//			}
//			else if (x[0] == "parent_boui") {
//			  xparent_boui=x[1];
//			}
//
//		}
//
        if(boFormSubmit != null)
        {
            if(boFormSubmit.parent_attribute != null)
            {                
		        xattributeName = boFormSubmit.parent_attribute.value;
		    }
		    if(boFormSubmit.parent_boui != null)
		    {
		        xparent_boui = boFormSubmit.parent_boui.value;
		    }
		}
		
         var bouis=[];
		 var rows=oTR.parentNode.rows;
         for ( var i = 0 ; i< rows.length ; i++ ){
		    b = rows[i].id.split("__");
			if ( b[2] ) {
				bouis[ bouis.length ] = b[2];
			}
		 }
				
				
		 if( parent && parent.boFormSubmit ) {
			createHiddenInput("moveLines",info+"_to_"+oTR.cells[0].firstChild.lin)
			
			xxx=boForm.BuildXml(false,false);
			with ( boFormSubmit ){
				var xtag="object__"+xparent_boui+"__"+xattributeName ;
				boFormSubmitXml.value="<bo boui='"+xparent_boui+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+">"+xxx+"</bo>";
						
				submit();
			}
					
		}  		
	
	}
	else if ( info && info.indexOf('dragObject:')>-1 && 
	  	  (!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
  	    ) 
	{
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
		var href = window.location.href;
		var xqry = href.substring(href.indexOf("?")+1);
		var xargs = xqry.split("&");
		var xattributeName="";
		var xparent_boui="";
//		
//		for ( var i = 0 ; i < xargs.length ; i++ ){
//			var x= xargs[i].split("=");
//			if ( x[0] == "parent_attribute" ){
//			   xattributeName = x[1]
//			}
//			else if (x[0] == "parent_boui") {
//			  xparent_boui=x[1];
//			}
//
//		}


        if(boFormSubmit != null)
        {
            if(boFormSubmit.parent_attribute != null)
            {                
		        xattributeName = boFormSubmit.parent_attribute.value;
		    }
		    if(boFormSubmit.parent_boui != null)
		    {
		        xparent_boui = boFormSubmit.parent_boui.value;
		    }
		}

        if ( xattributeName!="" && xparent_boui!="" )
        {
			//é bridge
		
		}
		else
		{
		
			for ( var i = 0 ; i < xargs.length ; i++ ){
				var x= xargs[i].split("=");
				if(x[0]=='boql') {
				x[1]=unescape(x[1]);
				x[1]=x[1].replace(/\+/g," ");
				}
				createHiddenInput(x[0],x[1]);
				
				//alert(x[0]+" ------- "+x[1]);
			}
            
			createHiddenInput("drop_info", info );
			
			boFormSubmit.submit();
			
		}
	}

}

function executeBridgeMeth( gnumber , attributeName , methodName )
{
  var grid=document.getElementById("g"+gnumber+"_body");
  var r=grid.rows;
  var xfaz=false;
  var rCheck=0;
  if( r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' ){
    rCheck=1;  
  }
  var selectedLines="";  
  for ( var i=0 ; i < r.length ; i++ ){

	if(r[i].cells[rCheck].firstChild && r[i].cells[rCheck].firstChild.checked  )
	{
		selectedLines+=(i+1)+";";
	}
  }
  createHiddenInput( "selectedLines", selectedLines);
  createHiddenInput( "toExecute", "ATR-"+attributeName+"."+methodName);
  var xst=winmain().getStatus( getIDX() )
  boForm.BindValues();
  winmain().setStatus( getIDX(),xst);
	
}

function grid_DragLeave(){

	
    var info=event.dataTransfer.getData("Text");
	if( (info&& info.indexOf('moveLin')>-1) ||
	     ( 
			info && info.indexOf('dragObject:')>-1 && 
			(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )
		 )
	   )
	 {
	
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
	
		for ( var i=0; i< oTR.cells.length;i++)
		{

		    oTR.cells[i].runtimeStyle.borderBottom='1px solid #CCCCCC';
		 	
		}
	
	}

}

function grid_DragEnter()
{
    
    var info=event.dataTransfer.getData("Text");
	if( info&& info.indexOf('moveLin')>-1){
		event.dataTransfer.effectAllowed = 'move';
		window.event.returnValue=false;
    
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
	
		for ( var i=0; i< oTR.cells.length;i++)
		{
			oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';
		}
	
	}
	else if ( info && info.indexOf('dragObject:')>-1 && 
			(!_hso ||!_hso.hb || _hso.hb.id.indexOf(info.split(":")[3])==-1  )) //( o !_hso é para não fazer para  amesmoma grid)
	{
		event.dataTransfer.effectAllowed = 'copy';
		window.event.returnValue=false;
    
		var o = getElement(event);
		var oTR = o;
		while (oTR.tagName != "TR")
		{
			oTR = oTR.parentNode;
			if (oTR == null) return; 	
		}
	
		for ( var i=0; i< oTR.cells.length;i++)
		{
			oTR.cells[i].runtimeStyle.borderBottom='2px solid #000000';
		}
	}
	else event.dataTransfer.effectAllowed = 'none';
}

	
	

function grid_StartMoveLine(){
//+event.srcElement.lin
	
	var startline=event.srcElement.lin+":";
	var movelines="";
	var o = getElement(event);
	var oTR = o;
	while (oTR.tagName != "TR")
	{
		oTR = oTR.parentNode;
		if (oTR == null) return; 	
	}
	
	var r=oTR.parentNode.rows;
 	for ( var i=0 ; i < r.length ; i++ ){
    	if(r[i].cells[1].firstChild && r[i].cells[1].firstChild.checked  )
    	{
 	      
 			movelines+=((i+1)+':');
 	      
		
		}
 	}
	if ( movelines.indexOf( startline ) > -1 )
	{
		event.dataTransfer.setData('Text','moveLin:'+movelines);
		event.dataTransfer.effectAllowed = 'move';
	}
	
}
 
    


function refreshStatusGrid_std(){
	_hso.boselect=0;
	var r=_hso.hb.rows;
	var rtot=0;
    var rCheck=0;
    if(r.length>0)
    {
       if( r[0].cells[0].firstChild && r[0].cells[0].firstChild.tagName=='IMG' )
       {
         rCheck=1;  
       }
	}
	
	for(var i=0;i<r.length;i++){
	 
	// if(r[i].cells[0].firstChild){
	//  if (r[i].cells[0].firstChild.checked) _hso.boselect++;
	//  rtot++
	//  }
  
	 if(r[i].cells[rCheck].firstChild){
	  if (r[i].cells[rCheck].firstChild.checked) _hso.boselect++;
	  rtot++
	  }
	}
	var x=document.getElementById(_hso.id+'_check');
	if(x){
		if(_hso.boselect==rtot) x.checked=true; 
		else  x.checked=false; 
		
	}
	
}


function collapseGridRow_std(iRow)
{
	if (iRow > -1)
	{
		var s = "";
		var row	  = _hso.hb.rows[iRow];
		var imgqv	   = null;
		var c		   = row.cells[0].className;
		
		for ( var j=0;j< row.cells.length;j++)
		{
			if( row.cells[j].firstChild && row.cells[j].firstChild.src && row.cells[j].firstChild.src.indexOf('quick')>-1 )
			{
				imgqv=row.cells[j].firstChild;
				break;
			}
		}	
	
		
		if ( imgqv )
		{
		
			_hso.hb.rows[iRow+1].style.display = "none";
			var tpl	  	   = c.substr( c.indexOf('_')+1 );
			var isSelect   = row.cells[0].firstChild?row.cells[0].firstChild.checked:row.cells[1].firstChild.checked;
			var s='gCell'+''+(isSelect? 'Sel':'' )+'_'+tpl;
		
			imgqv.src = "templates/grid/"+tpl+"/quickview.gif";
			applyStyle(row, s);
		}
		
	}
}

function expandGridRow_std(o)
{
	var row		   = o.parentNode.parentNode;
	var iIndex	   = row.rowIndex;
	var c		   = o.parentNode.className;
    var tpl	  	   = c.substr( c.indexOf('_')+1 );
    var isExpanded = c.indexOf('Expanded')>-1;
    var isSelect   = row.cells[0].firstChild?row.cells[0].firstChild.checked:row.cells[1].firstChild.checked;
    var imgqv	   = null;

	for ( var j=0;j< row.cells.length;j++)
	{
		if( row.cells[j].firstChild && row.cells[j].firstChild.src && row.cells[j].firstChild.src.indexOf('quick')>-1 )
		{
			imgqv=row.cells[j].firstChild;
			break;
		}
	}	
	if (isExpanded)
	{
		row.loaded=true;
		collapseGridRow_std(iIndex);
		_hso.lastRowIndex = -1;
		return;
	}
	
	collapseGridRow_std(_hso.lastRowIndex);
	_hso.lastRowIndex = iIndex;
	
	
	if (row.loaded)
	{
	   _hso.hb.rows[iIndex+1].style.display = "";
		//row.nextSibling.style.display = "inline";
	}
	else
	{
		
		var oNewTr = _hso.hb.insertRow(iIndex+1);
			oNewTr.id="ExpandedRow";
			
			oNewTr.onmouseover	= function y() { window.event.cancelBubble = true };
			oNewTr.onclick		= function x() { window.event.cancelBubble = true };
		var oNewTd = oNewTr.insertCell(0);
			
			
			
		with (oNewTd)
		{
			className	= "gCellQuickView_std";
			colSpan		= row.cells.length-1;
			
			var boui=row.id.split("__")[2];
			innerHTML	= "<iframe style='width:100%' src='__buildPreview.jsp?designHeader=false&designPrint=false&bouiToPreview="+boui+"&docid="+getDocId()+"' ></iframe>";
			//innerHTML	= "<iframe frameborder=0 style='width:100%' src='__buildPreview.jsp?bouiToPreview="+boui+"&docid="+getDocId()+"' ></iframe>";
		
		}
		oNewTd = oNewTr.insertCell(0);
		oNewTd.className="gCell_std";
		row.loaded=true;
	}
	var s='gCell'+'Expanded'+(isSelect? 'Sel':'' )+'_'+tpl;
	imgqv.src = "templates/grid/"+tpl+"/quickview_on.gif";
	
	applyStyle(row, s);
}
function applyStyle(o, sClass )
{
	var i = 0;
	var ii = o.cells.length;
	if ( o.noStyle ) return;
	while (i < ii)
	{
		o.cells[i].className = sClass;
		i++;
	}
}

function setBarFilterStyle(bOn,event)
{
	var o = event.srcElement;
	if (o.tagName != "TD") return;
	with (o.runtimeStyle)
	{
		if (bOn)
		{
			color = "#000000";
			fontWeight = "bold";
		}
		else
		{
			color = "";
			fontWeight = "";
		}
	}
}

function onClick_BarFilter(event,bReset)
{    
	var e = getElement(event);
	var xtbl=e.parentElement.parentElement;
	if (bReset || e.tagName == "TD")
	{
		if (bReset)
		{
			_hso.oDataSrc = null;
		}
		if (_hso.oLast)
		{
			with (_hso.oLast.style)
			{
				color = "#74736B";
				fontWeight = "normal";
			}
		}
		else
		{
			with(xtbl.rows[0].cells[0].style)
			{
				color = "#74736B";
				fontWeight = "normal";
			}
		}
		bReset ? _hso.oLast = xtbl.rows[0].cells[0] : _hso.oLast = e;
		var s = _hso.oLast.innerText;
		with (_hso.oLast.style)
		{
			fontWeight = "bold";
			color = "#000088";
		}
		if (!bReset)
		{
			_hso.pageNum = 1;
			_hso.filter = (s == "Todos" ? "" : s);
			//var xhref=setUrlAttribute(window.location.href,"list_letter", _hso.filter);
            if(boFormSubmit.list_letter == null)
            {
                createHiddenInput("list_letter",_hso.filter);
            }
            else
            {
                boFormSubmit.list_letter.value = _hso.filter;
            }				
			//var xboql=getUrlAttribute(window.location.href,"boql");
			var xboql=boFormSubmit.boql.value;
			
			if ( xboql )
			{
			
			  	
			  var x=xboql.split("+");
			  			  
			  if ( x.length == 4 && x[3]==encodeURIComponent("0=1") )
			  {
				  if( typeof(parent.xurl)!="undefined" )
				  {
					  //xhref=parent.xurl;
					  //xhref=setUrlAttribute(xhref,"list_letter", _hso.filter);
        			  if(boFormSubmit.list_letter == null)
        			  {
        			    createHiddenInput("list_letter",_hso.filter);
        			  }
        		      else
        		      {
        		        boFormSubmit.list_letter.value = _hso.filter;
        		      }					  
				  }
				  else
				  {					 
					  x[3]="1=1";
					  var xx=x.join("+");
					  //xhref=setUrlAttribute(xhref,"boql", xx );
        			  if(boFormSubmit.list_letter == null)
        			  {
        			    createHiddenInput("boql",xx);
        			  }
        		      else
        		      {
        		        boFormSubmit.boql.value = xx;
        		      }							 
					 //alert(xhref);
				  }
				  
			  }
			  
			}
			//xhref=setUrlAttribute(xhref,"list_page","1");
			if(boFormSubmit.list_page == null)
			{
			    createHiddenInput("list_page","1");
			}
		    else
		    {
		        boFormSubmit.list_page.value = "1";
		    }
			//xhref=setUrlAttribute(xhref,"list_letter_field", _hso.hb.letter_field );
			if(boFormSubmit.list_letter_field == null)
			{
			    createHiddenInput("list_letter_field",_hso.hb.letter_field);
			}
		    else
		    {
		        boFormSubmit.list_letter_field.value = _hso.hb.letter_field;
		    }			
		    boFormSubmit.submit();
			//window.location.href=xhref;						
			//alert('nova pagina'+_hso.pageNum+' '+_hso.filter);
		}
		
	}
}


function submitSelectOne( oTR ){
	var b=oTR.id.split("__");
	if(b[2]){ // b[2] tem o BOUI do objecto


//	winmain().sendCmd("GETCARDID","bouis="+b[2]+"&docid="+getDocId(),winmain().lixo);
	
	
      if ( ""+oTR.selectRecordNone!="undefined" ) b[2]="";
      
      if ( parent.boFormSubmit )
      {
		with ( parent.boFormSubmit ){
		
			if ( parent.boFormSubmit.boFormSubmitXml )
				{
				var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;
				boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+" mode='add' >"+b[2]+"</"+xtag+"></bo>";
				clientIDXtoClose.value=parent.windowIDX;
				}
			else if ( parent.boFormSubmit.selectedBouis )
				{
					selectedBouis.value=b[2];
					
				}
			submit();		
		  }	
	  }
	  else
	  {
		alert("ERROR !?!! Parent.boForSubmit not defined");
	  }
	}
}

function submitSelectOne2( boui ){
	if ( parent.boFormSubmit )
      {
		with ( parent.boFormSubmit ){
		
			if ( parent.boFormSubmit.boFormSubmitXml )
				{
				var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;
				boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+" mode='add' >"+boui+"</"+xtag+"></bo>";
				clientIDXtoClose.value=parent.windowIDX;
				}
			else if ( parent.boFormSubmit.selectedBouis )
				{
					selectedBouis.value=boui;
				}	
				submit();		
		  }	
	  }
	  else
	  {
		alert("ERROR !?!! Parent.boForSubmit not defined");
	  }
	
}


function submitBridge(){
 	var elems=parent.resultframe.document.getElementsByTagName("TABLE");
	var tblToadd=null;
	for (var i=0 ;elems.length; i++){
		
		if(elems[i].container){
		    
		    tblToadd=elems[i];
		    break;
		}
						  
	}
					
	if( tblToadd!=null ) {
	
		var rows=tblToadd.rows;
		var bouis=[];
		var b;
		for ( var i = 0 ; i< rows.length ; i++ ){
			
			b = rows[i].id.split("__");
			if ( b[2] && b[2] != "null") {
				bouis[ bouis.length ] = b[2];
			}
		}
		
		var waitingDetachAttribute= tblToadd.waitingDetachAttribute;
				
		if ( ""+waitingDetachAttribute=="undefined" )
		{
			if (parent.boFormSubmit.boFormSubmitXml) {
			   with ( parent.boFormSubmit ){
					var xtag=look_parentObj.value+"__"+look_parentBoui.value+"__"+look_parentAttribute.value;
					boFormSubmitXml.value="<bo boui='"+look_parentBoui.value+"'><"+xtag+">"+bouis.join(';')+"</"+xtag+"></bo>";
					clientIDXtoClose.value=parent.windowIDX;
					submit();
					}
			}
			else {
				with ( parent.boFormSubmit ){
					var xtag="Ebo_Template__"+look_templateBoui.value;
					boFormSubmitTemplateXml.value="<"+xtag+">"+bouis.join(';')+"</"+xtag+">";
					
									
					clientIDXtoClose.value=parent.windowIDX;
					submit();
				}
			}  		
		
		}
		else
		{
				winmain().setWDA ( waitingDetachAttribute , bouis.join(";") , getDocId() );
				winmain().ndl[getIDX()].close();
		
		}
		
	
	}	 
  
}

function actNumberOfArea( )
{
 if ( document.forms["boFormSubmit"] && document.forms["boFormSubmit"].parent_attribute && document.forms["boFormSubmit"].parent_boui )
 {
    try
    {
	 parent.document.getElementById( document.forms["boFormSubmit"].parent_attribute.value+"_"+document.forms["boFormSubmit"].parent_boui.value).innerText="("+window.recs+")";
	}
	catch(e){}
 }

}
