//<SCRIPT>
/*
function LookupArgsClass()
{
	
	
	this.items = null;
	
}
function LookupObjects(lookupField, lookupStyle, lookupClass, lookupTypes, lookupBrowse, bindingColumns, additionalParams, showNew, showProp)
{	
	var url;
	
	url = "/_controls/lookup/lookup";
	url += lookupStyle;
	url += ".aspx";
	url += "?class=" + lookupClass;
	url += "&objecttypes=" + lookupTypes;
	url += "&browse=" + lookupBrowse;
	
	if (bindingColumns)
	{
		url += "&bindingcolumns=" + bindingColumns;
	}
	
	if (additionalParams)
	{
		if (additionalParams.charAt(0) != "&")
		{
			url += "&";
		}
		url += additionalParams;
	}
	
	if (showNew)
	{
		url += "&ShowNewButton=" + showNew;
	}
	if (showProp)
	{
		url += "&ShowPropButton=" + showProp;
	}
	var args = new LookupArgsClass();
	if (lookupField != null)
	{		
		args.items = lookupField.getElementsByTagName("SPAN");
	}
	
	var features = BuildFeatures(lookupStyle);
	
	if (features == null)
		return;
	var lookupItems = window.showModalDialog(url, args, features);
	
	if (lookupItems != null && lookupField != null)
	{
		BuildField(lookupField, lookupItems);
	}
	
	return lookupItems;	
}
function BuildFieldSpan(lookupField, lookupItems)
{
	var html = "";
	var len = lookupItems.items.length;
	for (var i = 0; i < len; ++i)
	{
		var item = lookupItems.items[i];
		html += (i > 0 ? " " : "");
		var e = parent.document.createElement("SPAN");
		
		e.className	= "lui";
		e.oid		= item.id;
		e.otype		= item.type;
		e.innerHTML	= item.html;
		e.onclick	= "openlui();";
		
		html += e.outerHTML;
	}
	if (html.length == 0)
	{
		html = "&nbsp;";
	}
	return html;
}
function BuildField(lookupField, lookupItems)
{	
	lookupField.innerHTML = BuildFieldSpan(lookupField, lookupItems);
}
function BuildFeatures(lookupStyle)
{
	var height;
	var width;
	
	switch (lookupStyle)
	{
		case "multi":
			height = "460px";
			width = "520px";
			break;
		case "single":
			height = "488px";
			width = "498px";
			break;
		case "subject":
			height = "450px";
			width = "500px";
			break;
		default:
			alert("unknown lookup style - your properties xml is probably not set properly - " + lookupStyle);
			return null;
	}
	
	return "dialogHeight:" + height + " ;dialogWidth:" + width + ";resizable:yes;center:yes;status:no;help:no;scroll:no;";
}
function openlui()
{
	var o = event.srcElement;
	while (o.tagName != "SPAN")
	{
		o = o.parentElement;
	}
	if (o.otype == UnresolvedAddress)
	{
		resolveAddress(o.otype, o.oid);
	}
	else
	{
		openObj(o.otype, o.oid);
	}
}
function ClearField(dataInputs)
{
	for (var di = 0; di < dataInputs.length; ++di)
	{
		dataInputs[di].value = "";
	}
}
function BindField(lookupItems, dataInputs)
{
	ClearField(dataInputs);
	var html = "";
	var len = lookupItems.items.length;
	for (var i = 0; i < len; ++i)
	{
		var item = lookupItems.items[i];
		
		
		
		
		for (var di = 0; di < dataInputs.length; ++di)
		{
			var input = dataInputs[di];
			
			if (input.otype == item.type)
			{
				input.value = item.id;
			}
			else
			{
				input.value = "";
			}
		}
	}
}
*/