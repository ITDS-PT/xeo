//<SCRIPT language="Jscript">
function fmenuOn(o)
{
	o.runtimeStyle.color = "#000000";
	o.runtimeStyle.backgroundColor = "#CCCCCC";
}

function fmenuOff(o)
{
	o.runtimeStyle.color = "";
	o.runtimeStyle.backgroundColor = "";
}

function  explorerChooseCols(key)
{
	winmain().openDocUrl(',740,430','__explorerChooseColsToGroupGrid.jsp',"?explorer_key="+key,'lookup');
}
function buildGridSubmit()
{
wait();
}

function orderExplorerCol(key,col)
{
	createHiddenInput("orderCol",col);
	createHiddenInput("explorer_key", key );
	wait();
	boFormSubmit.submit();
}
function openExplorerGroup(key,keyGroup,control_line)
{
	var o = window.event.srcElement;

	if ( key == null )
	{
		key=o.key;
		keyGroup=o.keyGroup;
		control_line=o.control_line;
	}
	o.title="Clique para fechar grupo";
	o.src="resources/minus.gif";
	o.key=key;
	o.keyGroup=keyGroup;
	o.control_line = control_line;
	o.onclick=closeExplorerGroup;
	while ( o.tagName != 'DIV' )
	{
		o=o.parentElement;
	}
	o.insertAdjacentHTML("afterEnd", "<div id='waitingRecs'><b color='blue'>A ler dados, aguarde ...</b></div>");
	
	var f = document.getElementById("treeHelper"+key).contentWindow.boFormSubmit;
	
	f.lineControl.value=control_line;
	f.group_key.value=o.id;
	f.group_page.value=1;
	f.submit();
  wait();

}


function openPageExplorerGroup(key,keyGroup,control_line,page)
{
	
	var o = window.event.srcElement;

	if ( key == null )
	{
		key=o.key;
		keyGroup=o.keyGroup;
		control_line=o.control_line;
	}
	var o = document.getElementById( keyGroup );
	
	closeExplorerGroupNoSubmit(key,keyGroup,control_line,o);
	
	o.insertAdjacentHTML("afterEnd", "<div id='waitingRecs'><b color='blue'>A ler dados, aguarde ...</b></div>");
	
	//var f = document.getElementById("treeHelper"+key).frmHlp.contentWindow.boFormSubmit;
	var f = document.getElementById("treeHelper"+key).contentWindow.boFormSubmit;
	f.lineControl.value=control_line;
	f.group_key.value=keyGroup;
	f.group_page.value=page;
	f.submit();
  wait();
}


function closeExplorerGroupNoSubmit(key,keyGroup,control_line,o)
{
	if( !o )
	{
		var o = window.event.srcElement;
    o.title="Clique para expandir grupo";
		o.src="resources/more.gif";
		o.onclick=openExplorerGroup;
		if ( key == null )
		{
			key=o.key;
			keyGroup=o.keyGroup;
		}
		else
		 {
		   o.key=key;
	     o.keyGroup=keyGroup;
	     o.control_line = control_line;
		 }
	
		while ( o.tagName != 'DIV' )
		{
			o=o.parentElement;
		}
	}
	
	var level = parseInt(o.style.paddingLeft,10) /13;
	
	while ( o.nextSibling )
	{
		var o2 = o.nextSibling;
		var level2 = parseInt(o2.style.paddingLeft,10) /13;
		if ( (level2 <= level && o2.className!='footerGroup') || (o2.className == 'navGroup' || o2.className == 'nav') )
		{
			break;
		}
		o.parentElement.removeChild( o2 );
	}

}
function closeExplorerGroup(key,keyGroup,control_line, o)
{
  var expKey, expkeyGroup;
   
  if( !o )
	{
	   var e = window.event.srcElement;
	   if ( key == null )
		 {
			expKey=e.key;
			expkeyGroup=e.keyGroup;
		 }
		else
		{
		  expKey=key;
			expkeyGroup=keyGroup;
		}
	}
  else
  {
      expKey=o.key;
			expkeyGroup=o.keyGroup;
  }

  closeExplorerGroupNoSubmit(key,keyGroup,control_line,o)
  var treeHelperFrame = document.getElementById("treeHelper"+expKey);
  var helperCloseGroup = treeHelperFrame.contentWindow.document.getElementById("helperCloseGroup");
  var f = helperCloseGroup.contentWindow.boFormSubmit;
	f.group_key.value=expkeyGroup;
	f.submit();

}

function toggleExplorerOrderGroup(key , groupNumber )
{
	createHiddenInput("toggleOrderGroup",groupNumber);
	createHiddenInput("explorer_key", key );
	wait();
	boFormSubmit.submit();
}

function setExplorerFullTextGroup(key,text)
{
	createHiddenInput("fullTextGG",text);
	createHiddenInput("explorer_key", key );
	wait();
	boFormSubmit.submit();
}
function setUserExplorerQuery(text, reference)
{

	
	createHiddenInput("userQuery",text);
	createHiddenInput("explorer_key", reference );
	if(parent && parent.document.getElementById("queryUserButton"))
	{
		var e=parent.document.getElementById("queryUserButton");
		if( text!="<cleanFilter/>")
			e.style.color="#990000";
		else
			e.style.color="";e.xmlFilter=text;
	}
	wait();
	boFormSubmit.submit();
}

function setParametersExplorerQuery(text, reference)
{
	createHiddenInput("parametersQuery",text);
	createHiddenInput("explorer_key", reference );
	wait()
	boFormSubmit.submit();
}
function refreshExplorer()
{
    wait();
	boFormSubmit.submit();
}
function explorerOperation(key, operation)
{
	createHiddenInput("explorer_key", key);
	createHiddenInput("treeOperation", operation);
	wait();
	boFormSubmit.submit();
}

function setUserExplorerQueryBoui(o)
{
	createHiddenInput("userQueryBoui",o.returnValue);
	createHiddenInput("explorer_key", o.name.split("__")[1] );
	wait();
	boFormSubmit.submit();
}

function setUserSvExplorerBoui(o)
{
	createHiddenInput("userSvExplorer",o.returnValue);
	createHiddenInput("explorer_key", o.name.split("__")[1] );
	wait();
	boFormSubmit.submit();
}

function showPreview(boui, treeName)
{
	wait();
	createHiddenInput("bouiTopreview",boui);
	createHiddenInput("explorer_key", treeName );
	boFormSubmit.submit();
	activeLine( event.SrcElement )
}
function showOnPreview(boui, treeName)
{
	url = '__buildPreview.jsp?bouiToPreview='+boui+'&explorerKey='+treeName;
	var r=activeLine( event.srcElement )
	
	if(document.getElementById('previewBottom').style.display != 'none')
	{
		if(showDown)
		{
			showDown.location.href = url;
			r.style.fontWeight='normal';
		}
	}
	else if(showRight)
	{		
		if(document.getElementById('previewRight').style.display != 'none')
		{
		 showRight.location.href = url;
		 r.style.fontWeight='normal';
		}
	}
	
}
function doubleClick(objName, boui)
{
	//alert('doubleclick');
	//parent.parent.openObj(this.oType, this.oId)
	//
	winmain().openDoc("medium",objName.toLowerCase(),"edit","method=edit&boui="+boui,null,null,null);
	activeLine( event.srcElement );
}
window.rowSel=[];
function activeLine(r)
{
	
	while( r && r.tagName != 'TR' )
	{
		r=r.parentElement;
	}
	
	if ( r && r.tagName =='TR' )
	{
	   //Gskey=ev.shiftKey;
       //Gckey=ev.ctrlKey;
    if (window.event.shiftKey)		
		{
      t = r;
      while( t && t.tagName != 'TABLE' )
      {
      		t=t.parentElement;
      }
      if(window.rowSel.length==0)
      {
         if(t)
         {
           var end = false;
           for(var i=0;!end && i< t.rows.length; i++)
      		 {            
              window.rowSel[ window.rowSel.length ]=t.rows[i];
         			t.rows[i].className ='rowGroupSel';
         			for(var j=0;j< r.cells.length; j++)
         			{
         			 t.rows[i].cells[j].runtimeStyle.borderTop='1px solid #FFFFFF';
         			 t.rows[i].cells[j].runtimeStyle.borderBottom='1px solid #AF9D71';
         			}
              if(t.rows[i] == r)
              {
               end = true;
              }
           }         
         }
      }
      else
      {
         lastRow = window.rowSel[ window.rowSel.length - 1];
         if(t)
         {
            var end = false, foundFirst = false, foundLast = false;
            for(var i=0;!end && i< t.rows.length; i++)
      		  {  
              if(t.rows[i] == r)
              {
               if(foundFirst)
               {
                  foundLast = true;
                  end = true;
               }
               else
               {
                  foundFirst = true;
               }
              }
              else if(t.rows[i] == lastRow)
              {
               if(foundFirst)
               {
                  foundLast = true;
                  end = true;
               }
               else
               {
                  foundFirst = true;
               }
              }
              if(foundFirst)
              {
                if(t.rows[i].className != 'rowGroupSel')
                {
                  window.rowSel[ window.rowSel.length ]=t.rows[i];
            			t.rows[i].className ='rowGroupSel';
            			for(var j=0;j< t.rows[i].cells.length; j++)
            			{
            			 t.rows[i].cells[j].runtimeStyle.borderTop='1px solid #FFFFFF';
            			 t.rows[i].cells[j].runtimeStyle.borderBottom='1px solid #AF9D71';
            			}
                }  
              }
            }            
         }
      }
		}
    else
    {  
   		if ( window.rowSel.length>0 && !window.event.ctrlKey)		
   		{
   			inactiveLine();
   		}
   		if( r.className == 'rowGroupSel' && window.event.ctrlKey)
   		{
   			inactiveLine(r);
   		}
   		else
   		{
   			window.rowSel[ window.rowSel.length ]=r;
   			r.className ='rowGroupSel';
   			for(var i=0;i< r.cells.length; i++)
   			{
   			 r.cells[i].runtimeStyle.borderTop='1px solid #FFFFFF';
   			 r.cells[i].runtimeStyle.borderBottom='1px solid #AF9D71';
   			}
   		}
    }
	}
	return r;
}
function removeSelectedLines()
{
  //alert("Removing selecting lines"+boFormSubmit.docid.value);
  if ( window.rowSel.length>0 )
  {
	var bouis="";
	for(var i=0;i< window.rowSel.length;i++ )
	{
		if(bouis!="") bouis+=";";
		bouis+=window.rowSel[i].boui;
	}
	
	
	var xsrc = "__explorerDeleteObjects.jsp?selectedObjects="+bouis+"&docid="+getDocId();
	window.showModalDialog(xsrc,this,"dialogHeight:400px;dialogWidth;400px;scroll=yes;status=no;resizable=yes;help=no;unadorned=yes"); 
  }
  else
  {
	alert( jsmessage_19 );
  }
}

function executeMethod(methodName,toObject,explorerName)
{
  //alert("Removing selecting lines"+boFormSubmit.docid.value);
  if ( window.rowSel.length>0 )
  {
	var bouis="";
	for(var i=0;i< window.rowSel.length;i++ )
	{
		if(bouis!="") bouis+=";";
		bouis+=window.rowSel[i].boui;
	}
	
	
	var xsrc = "__explorerExecuteMethod.jsp?toObject="+toObject+"&methodName="+methodName+"&selectedObjects="+bouis+"&docid="+getDocId()+"&explorerName="+explorerName;
	window.showModalDialog(xsrc,this,"dialogHeight:400px;dialogWidth;400px;scroll=yes;status=no;resizable=yes;help=no;unadorned=yes"); 
  }
  else
  {
	alert( jsmessage_19 );
  }
}

function adicionarSelectedLines()
{
  //alert("Removing selecting lines"+boFormSubmit.docid.value);
  if ( window.rowSel.length>0 )
  {
	var bouis="";
	for(var i=0;i< window.rowSel.length;i++ )
	{
    parent.window.addBoui(window.rowSel[i].boui);		
	}
  
  }
  else
  {
	alert( jsmessage_19 );
  }
}
function adicionarSelectedLine(boui)
{
  //alert("Removing selecting lines"+boFormSubmit.docid.value);  
  parent.window.addBoui(boui);		
}
function deleteRows(bouis)
{
  var r=window.rowSel[0];
  var abouis=bouis.split(";");
  while (r&& r.tagName!='TABLE')
  {
	r=r.parentElement;
  }
  if( r.tagName=='TABLE')
  {
	 
	 for(var i=0;i<r.rows.length;i++)
	 {
		
		var b=r.rows[i].boui ;
		for( j=0;j< abouis.length;j++)
		{
			if ( b == abouis[j] )
			{
				r.rows[i].style.display='none';
			}
		}
	 }
	 inactiveLine();
  }
}

function inactiveLine( row )
{

	for(var i=0;i<window.rowSel.length;i++)
	{
	  r=window.rowSel[i];
	  if ( !row || row == r )
	  {
	  	r.className ='rowGroup';
	  	for(var j=0;j< r.cells.length; j++)
	  	{
	  	 r.cells[j].runtimeStyle.borderTop='';
	  	 r.cells[j].runtimeStyle.borderBottom='';
	  	}
	  	window.rowSel.splice(i,1);
	  	i=-1;
	  	if( window.rowSel.length==0) break;
	  }
	}
}
function setDefaultExplorer()
{
  var resp= newDialogBox("critical",jsmessage_4,[jsmessage_1, jsmessage_7 ], jsmessage_3 )
  if ( resp == 1)
  {
    createHiddenInput("setDefaultExplorer","true");
  	wait();
  	boFormSubmit.submit();
  }
}

function executeStaticMeth(key, methodName, parametersArray )
{
	  var f = document.getElementById("treeHelper"+key).contentWindow.boFormSubmit;
   	f.toExecute.value = "STATIC-"+methodName+";"+parametersArray.join("|");
   	f.lineControl.value="";
   	f.submit();
}
