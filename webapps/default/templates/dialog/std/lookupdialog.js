//<SCRIPT language="JSCRIPT">
var SelectedColorFocused = "#adc3e7";
var SelectedColorUnfocused = "#eeeeee";
var _mode = 0x4;
var LookupMultiSelect = 0x4;
function checkMode(mode, option)
{
	return ((mode & option) == option);
}
function initSelectedItems(table)
{
	if (table.selectedItems == undefined)
	{
		table.selectedItems = new Array();
		
		table.onactivate=activateItems;
		table.ondeactivate=deactivateItems;
	}
}
function getActiveItem(elem)
{
	while (elem.tagName != "TR")
	{
		elem = elem.parentElement;
	}
	
	return elem;
}
function unselectItems(table)
{
	var multiSelect = checkMode(_mode, LookupMultiSelect);
	if (table.selectedItems == undefined) return;
	if (multiSelect && event.shiftKey) return;
	if (multiSelect && event.ctrlKey && event.keyCode == 0) return;
	
	while (table.selectedItems.length > 0)
	{
		unselectItem(table, table.selectedItems[0]);	
	}
}
function unselectItem(table, item)
{
	if (table.selectedItems == undefined) return;
	var items = table.selectedItems;
	for (var i = 0; i < items.length; i++)
	{
		if (items[i] == item)
		{
			items[i].selected = 0;
			items[i].runtimeStyle.backgroundColor = "";
			table.selectedItems.splice(i, 1);
			break;
		}
	}
}
function selectItem(table, item, focused)
{
	if (item.unselectable != undefined) return;
	
	
	initSelectedItems(table);
	item.selected = 1;
	
	if (focused)
		item.runtimeStyle.backgroundColor = SelectedColorFocused;
	else
		item.runtimeStyle.backgroundColor = SelectedColorUnfocused;
	
	table.lastSelected = item;
	
	
	
	table.selectedItems.push (item);
	
	
	
	if ( table.id == "tblResults" && "undefined" != typeof(parent.btnProperties))
	{
		//parent.btnProperties.disabled = (item.key == undefined);
	}
}
function shiftSelectItems(table, o)
{
	if (table.lastSelected != undefined)
	{
		var lastSelected = table.lastSelected;
		
		if (lastSelected.rowIndex >= o.rowIndex)
		{
			var rows = table.rows;
			for (var i = o.rowIndex; i < lastSelected.rowIndex; i++)
			{
				selectItem(table, rows[i], true);
			}
		} 
		else
		{
			var rows = table.rows;
			
			for (var i = lastSelected.rowIndex + 1; i <= o.rowIndex; i++)
			{
				selectItem(table, rows[i], true);
			}
		}
	}
}
function clickItem(table)
{
	if (event.srcElement.tagName == "TBODY") return;
	var item = getActiveItem(event.srcElement);
	var multiSelect = checkMode(_mode, LookupMultiSelect);
	
	if (multiSelect && event.shiftKey && !item.selected)
	{
		shiftSelectItems(table, item);
	}
	else if (multiSelect && event.ctrlKey && item.selected)
	{
		unselectItem(table, item, true);
	}
	else
	{
		unselectItems(table);
		
		selectItem(table, item, true);
	}
  setNavigationState(); 
	return item;
}
function findValueKeyDown()
{
	if (event.keyCode == 13)
	{
		search();	
	}
}
function listKeyDown(table)
{
	if (event.keyCode == 32)
	{
		table.ondblclick();
	}
	else if (event.keyCode == 38)
	{
		var item = table.lastSelected;
		
		if (item && item.rowIndex > 0)
		{
			item = item.previousSibling;
			
			if (item.unselectable != undefined)
			{
				item = item.previousSibling;
				
				if (item == null)
					return;
			}
			unselectItems(table);
			
			selectItem(table, item, true);
			
			
			
			item.scrollIntoView(true);
		}
	} 
	else if (event.keyCode == 40)
	{
		var item = table.lastSelected;
		if (item && item.rowIndex < table.rows.length- 1)
		{
			item = item.nextSibling;
			
			if (item.unselectable != undefined)
			{
				item = item.nextSibling;
				
				if (item == null)
					return;
			}
			unselectItems(table);
			
			selectItem(table, item, true);
			
			
			
			item.scrollIntoView(false);
		}
	}
	else if (!(event.shiftKey || event.ctrlKey) && ((event.keyCode > 47 && event.keyCode < 58) || (event.keyCode > 64 && event.keyCode < 91)))
	{
		var code;
		var len = table.rows.length;
		
		for (i = (checkMode(_mode, LookupMultiSelect)) ? 0 : 2; i < len; i++)
		{
			code = table.rows[i].cells[0].innerText.charCodeAt(0);
			
			if ((code == event.keyCode) || ((event.keyCode > 64 && event.keyCode < 91) && (code == event.keyCode + 32)))
			{
				item = table.rows[i];
				
				unselectItems(table);
			
				selectItem(table, item, true);
				item.scrollIntoView(true);
				break;
			}
		}
	}
}
function focusSelectedItems(table, focused)
{
	if (table.selectedItems == undefined) return;
	var items = table.selectedItems;
	
	
	
	if (items.length == 0 && table.rows.length > 0)
	{
		selectItem(table, table.rows[0], true);
	}
		
	for (var i = 0; i < items.length; i++)
	{
		if (focused)
		{
			items[i].runtimeStyle.backgroundColor = SelectedColorFocused;
		}
		else
		{
			items[i].runtimeStyle.backgroundColor = SelectedColorUnfocused;
		}
	}
}
function activateItems()
{
	if (!this.contains (event.toElement))
	{
		focusSelectedItems(this, true);
	}
}
function deactivateItems()
{
	if (!this.contains (event.toElement))
	{
		focusSelectedItems(this, false);
	}
}




function duplicateSelection( key )
	{
		var len = tblSelected.rows.length;
		for ( var i = 0; i < len; i++ )
		{
			if ( tblSelected.rows[i].key == key )
			{
				return true;
			}
		}
		return false;
	}

//	remove the selected items from the right side
//
function removeSelected()
{
	// bail if no items are selected
	//
	if (tblSelected.selectedItems==undefined)
	{
		return;
	}

	var items = tblSelected.selectedItems;
	for ( var i = 0; i < items.length; i++ )
	{
		if ( items[i].cells.length==0) return;
		appendItem( tblResults, items[i].key, items[i].cells[0].innerHTML);

		items[i].removeNode( true )
	}
	//	clear the selected items collection
	//
	items.splice( 0, items.length );
	clearSelected( tblSelected );

	setNavigationState();

	bChanged = true;
}

//	append all selected items from the left to the right
//
function appendSelected()
{
	if (tblResults == undefined)
	{
		return;
	}

	var items = tblResults.selectedItems;

	if ( items )
	{
		var len	= items.length;

		for ( var i = 0; i < len; i++ )
		{
			var o = items[i];
			if ( o.cells.length==0) return
			if ( !duplicateSelection(o.key) )
			{
				// append to Assigned list
				//
				appendItem( tblSelected, o.key, o.cells[0].innerHTML );

				// remove from Unassigned list
				//
				o.removeNode( true );
				clearSelected( tblResults );
			}
			
		}
		setNavigationState();
	}
}

function appendItem( table, id, html )
{
	var tr		= table.insertRow();
	tr.key		= id;

	var td	= tr.insertCell();
	td.className	= "sel";
	td.noWrap		= true;
	td.innerHTML	= html;

	if ( tr.rowIndex == 0 )
	{
		selectItem( table, tr, false );
	}

	bChanged = true;
}

function setNavigationState()
{
	if (tblResults != undefined)
	{
		btnAppend.disabled = ( tblResults.rows.length == 0 || tblResults.selectedItems == undefined || tblResults.selectedItems.length == 0);
		btnRemove.disabled = ( tblSelected.rows.length == 0 || tblSelected.selectedItems == undefined || tblSelected.selectedItems.length == 0);
    try
    {
       if(btnUp != undefined)
       {
         btnUp.disabled = ( tblSelected.rows.length <= 1 || tblSelected.selectedItems == undefined || tblSelected.selectedItems.length != 1);
       }
       if(btnDown != undefined)
       {
         btnDown.disabled = ( tblSelected.rows.length <= 1  || tblSelected.selectedItems == undefined || tblSelected.selectedItems.length != 1);
       }
    }catch(e){}

		tblNoRecords.runtimeStyle.display = ( tblSelected.rows.length == 0 ? "" : "none" );
	}
}

function itemDoubleClick()
{
	appendSelected();
}

function setTableFocus( table )
{
	if ( table.rows.length > 0 )
	{
		if ( table.selectedItems == undefined )
		{
			selectItem( table, table.rows[0] )
		}

		focusSelectedItems( table, true );
	}
}

function clearSelected( table )
{
	table.selectedItems = null;

	if (table.rows.length > 0)
	{
		selectItem(table, table.rows[0]);
	}
}
function moveUp()
{
  // bail if no items are selected
	//
  var rowIndex = -1;
	if (tblSelected.selectedItems==undefined || tblSelected.selectedItems.length != 1)
	{
		return;
	}

	var items = tblSelected.selectedItems;
	for ( var i = 0; i < items.length; i++ )
	{
    var len = tblSelected.rows.length;
    var o = items[i];
    var previousLineKey = null;
    var previousLineHTML = null;
    var switched = false; 
		for ( var j = 0; j < len && !switched; j++ )
    {
      if ( tblSelected.rows[j].key == o.key )
      {
         var tr		= tblSelected.rows[j-1];
	       tr.key		= o.key;

	       var td	= tr.cells[0];
	       td.className	= "sel";
	       td.noWrap		= true;
	       td.innerHTML	= o.cells[0].innerHTML;
         
         tr		= tblSelected.rows[j];
	       tr.key		= previousLineKey;

	       td	= tr.cells[0];
	       td.className	= "sel";
	       td.noWrap		= true;
	       td.innerHTML	= previousLineHTML;
         switched = true;
         rowIndex = j-1;
      }
      else
      {
         previousLineKey = tblSelected.rows[j].key;
         previousLineHTML = tblSelected.rows[j].cells[0].innerHTML;
      }
    }
	}
	//mantém a linha seleccionada excepto quando esta atinge a linha 0
	unselectItems(tblSelected);

  if(rowIndex > 0)
     selectItem(tblSelected, tblSelected.rows[rowIndex], true);

	setNavigationState();

	bChanged = true;

}

function moveDown()
{
  // bail if no items are selected
	//
	if (tblSelected.selectedItems==undefined || tblSelected.selectedItems.length != 1)
	{
		return;
	}

	var items = tblSelected.selectedItems;
	for ( var i = 0; i < items.length; i++ )
	{
    var len = tblSelected.rows.length;
    var o = items[i];
    var nextLineKey = null;
    var nextLineHTML = null;
    var switched = false; 
		for ( var j = 0; j < len && !switched; j++ )
    {
      if ( tblSelected.rows[j].key == o.key )
      {
         nextLineKey = tblSelected.rows[j+1].key;
         nextLineHTML = tblSelected.rows[j+1].cells[0].innerHTML;

         var tr		= tblSelected.rows[j+1];
	       tr.key		= o.key;

	       var td	= tr.cells[0];
	       td.className	= "sel";
	       td.noWrap		= true;
	       td.innerHTML	= o.cells[0].innerHTML;
         
         tr		= tblSelected.rows[j];
	       tr.key		= nextLineKey;

	       td	= tr.cells[0];
	       td.className	= "sel";
	       td.noWrap		= true;
	       td.innerHTML	= nextLineHTML;
         switched = true;
         rowIndex = j+1;
      }
    }
	}
	//mantém a linha seleccionada excepto quando esta atinge a última linha
	unselectItems(tblSelected);

  if((rowIndex+1) < tblSelected.rows.length)
     selectItem(tblSelected, tblSelected.rows[rowIndex], true);

	setNavigationState();

	bChanged = true;

}