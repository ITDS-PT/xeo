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

function  chooseCols(key)
{
	//alert("Para escolher colunas "+key);
	
  winmain().openDocUrl(',740,430','__chooseColsToGroupGrid.jsp',"?treeKey="+key+"&gridKey="+window.gridKey,'lookup');
        
}

function buildGridSubmit()
{
	wait();
	
	return;
	
	var href = window.location.href;
	var xqry = href.substring(href.indexOf("?")+1);
	var xargs = xqry.split("&");
		
		
	for ( var i = 0 ; i < xargs.length ; i++ ){
		var x= xargs[i].split("=");
		if(x[0]=='boql') {
			
			x[1]=unescape(x[1]);
			x[1]=x[1].replace(/\+/g," ");
			
			 var y=x[1].split(" ");
			  			  
			  if ( y.length == 4 && y[3]=="0=1")
			  {
					 y[3]="1=1";
					 x[1]=y.join(" ");;
			  }
		}
		else if ( x[0]=='fullTextGG' )
		{
		    x[1]=unescape(x[1]);
		}
		

		if ( "fullTextGG,openGroup,closeGroup,drag_to_col_header,drag_to_col_group,orderCol,userQuery,toggleOrderGroup,userQueryBoui,tree_key,treeOperation,".indexOf(x[0]+"," )==-1)
		{
		createHiddenInput(x[0],x[1]);
		}
		else
		{
		//createHiddenInput(x[0],"");
		}		

	}
	
	wait();

}

function orderCol(key,col)
{
	buildGridSubmit();
	createHiddenInput("orderCol",col);
	createHiddenInput("tree_key", key );
	boFormSubmit.submit();


}

function openGroup(key,keyGroup)
{

	buildGridSubmit();
	createHiddenInput("openGroup",keyGroup);
	createHiddenInput("tree_key", key );
	
	boFormSubmit.submit();


}


function closeGroup(key,keyGroup)
{

	buildGridSubmit();
	createHiddenInput("closeGroup",keyGroup);
	createHiddenInput("tree_key", key );
	boFormSubmit.submit();


}

function toggleOrderGroup( key , groupNumber )
{

	buildGridSubmit();
	createHiddenInput("toggleOrderGroup",groupNumber);
	createHiddenInput("tree_key", key );
	boFormSubmit.submit();
	
}

function setFullTextGroup(key,text)
{

	buildGridSubmit();
	createHiddenInput("fullTextGG",text);
	createHiddenInput("tree_key", key );
	boFormSubmit.submit();


}

function setUserQuery(text, reference)
{

	buildGridSubmit();
	createHiddenInput("userQuery",text);
	createHiddenInput("tree_key", reference );
	
	if ( parent && parent.document.getElementById("queryUserButton"))
	{
		var e=parent.document.getElementById("queryUserButton");
		if (  text != "<cleanFilter/>")
		e.style.color="#990000";
		else
		e.style.color="";
		e.xmlFilter=text;
	}
	boFormSubmit.submit();
}

function setParametersQuery(text, reference)
{

	buildGridSubmit();
	createHiddenInput("parametersQuery",text);
	createHiddenInput("tree_key", reference );
	boFormSubmit.submit();
}


function treeOperation(key, operation)
{

	buildGridSubmit();
	createHiddenInput("tree_key", key);
	createHiddenInput("treeOperation", operation);
	
	boFormSubmit.submit();
}

function setUserQueryBoui(o)
{
	
	buildGridSubmit();
	
	createHiddenInput("userQueryBoui",o.returnValue);
	createHiddenInput("tree_key", o.name.split("__")[1] );
	
	
	boFormSubmit.submit();
}