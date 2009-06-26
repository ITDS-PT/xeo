function changeExclude()
{
	var elemt;
	var objsToSear;

		try
		{
			elemt = selectedAllObject;
			var tables = document.getElementsByTagName("table");
			for(var i=0; i < tables.length ; i++)
      {
				if("tableToExclude" == tables[i].id)
				{
					objsToSear = tables[i];
				}
			}
		}
		catch(e)
		{
			elemt=boForm.selectedAllObject;
			var tables = boForm.getElementsByTagName("table");
			for(var i=0; i < tables.length ; i++)
      {
				if("tableToExclude" == tables[i].id)
				{
					objsToSear = tables[i];
				}
			}
		}

   if (elemt.checked )
   {
//			if(objsToSear.elements.length > 0)
//			{
//      	for ( var i=0 ; i< objsToSear.elements.length;i++ )
//        {
//              objsToSear.elements[i].checked=false;
//        }
//      }
      sayExclude.style.display='none';
      tableToExclude.style.display='none';
   }
   else
   {
			sayExclude.style.display='';
      tableToExclude.style.display='';
   }
}

function changeSURL(staticStr)
{
	return getBoql(staticStr);	
}
function getBoql(boql)
{
	try
	{
		var start = boql.indexOf("boql=");
		start = start + 5;
		var end = boql.indexOf("&", start);
		var toRet = boql.substring(0, start);
		var toRet2 = boql.substring(end, boql.length);
		var stBoql = boql.substring(start, end);
		var onlyObjects="";
		var isOnlyObjects=false;
		var elemt;
		var objsToSear;
		try
		{
			elemt = selectedAllObject;
			var tables = document.getElementsByTagName("table");
			for(var i=0; i < tables.length ; i++)
      {
				if("tableToExclude" == tables[i].id)
				{
					objsToSear = tables[i];
				}
			}
		}
		catch(e)
		{
			elemt=boForm.selectedAllObject;
			var tables = boForm.getElementsByTagName("table");
			for(var i=0; i < tables.length ; i++)
      {
				if("tableToExclude" == tables[i].id)
				{
					objsToSear = tables[i];
				}
			}
		}

		if( document.getElementById("selectedAllObject") )
		{
		   isOnlyObjects=!elemt.checked
		}
		if(isOnlyObjects && objsToSear)
		{
			var elementsToSear = objsToSear.getElementsByTagName("input");
			for ( var i=0 ; i< elementsToSear.length;i++ )
			{
		 		// alert( objsToSear.elements[i].id+objsToSear.elements[i].checked);
		  	if ( elementsToSear[i].checked )
		  	{
					if(onlyObjects == "")
					{
						onlyObjects+= " CLASSNAME='";
					}
					else
					{
						onlyObjects+= "or CLASSNAME='";
					}
					
					onlyObjects+= elementsToSear[i].id + "' ";
					var subclasses = elementsToSear[i].subclasses.split(";");
					for(var j=0;j<subclasses.length;j++) 
					{
							if(subclasses[j].length > 0)
							{
								onlyObjects+= "or CLASSNAME='" + subclasses[j] + "' ";
							}
			    }
		  	}
			}		
		}
		else
		{
			return boql;
		}
		if(onlyObjects == "") return boql;
		var where = stBoql.toUpperCase().indexOf("WHERE");
		if(where != -1)
		{
			var stBoql1 = stBoql.substring(0, where + 5);
			stBoql1+= " (" + encodeURIComponent(onlyObjects )+ ") and ";
			stBoql1 += stBoql .substring(where + 5, stBoql.length);
			return toRet + stBoql1 + toRet2;
		}
		else
		{
			var order = stBoql.toUpperCase().indexOf("ORDER");
			if(order != -1)
			{
				var stBoql1 = stBoql.substring(0, order);
				stBoql1+= " where (" + encodeURIComponent(onlyObjects) + ") ";
				stBoql1 += stBoql .substring(order, stBoql.length);
				return toRet + stBoql1 + toRet2;
			}
			else
			{
				 return toRet + stBoql + " where " + encodeURIComponent(onlyObjects) + " " + toRet2;
			}
		}
	}
	catch(e)
	{
		{alert(e);}
	}
}