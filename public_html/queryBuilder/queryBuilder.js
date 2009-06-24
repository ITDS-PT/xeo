//<SCRIPT>    
var KA_DC_CHAR=[];
var KA_IC_CHAR=[];
var i=0;
KA_DC_CHAR[i]	=" Igual a ";
KA_IC_CHAR[i++]	="EQUAL";

KA_DC_CHAR[i]	=" diferente de ";
KA_IC_CHAR[i++]	="NOTEQUAL";
	
KA_DC_CHAR[i]	=" a começar por ";
KA_IC_CHAR[i++]	="START";

KA_DC_CHAR[i]	=" a acabar em ";
KA_IC_CHAR[i++]	="FINISH";

KA_DC_CHAR[i]	=" contendo ";
KA_IC_CHAR[i++]	="IN";
	
KA_DC_CHAR[i]	=" não contem ";
KA_IC_CHAR[i++]	="NOTIN";
	
KA_DC_CHAR[i]	=" maior que ";
KA_IC_CHAR[i++]	="GREATER";

KA_DC_CHAR[i]	=" maior ou igual que ";
KA_IC_CHAR[i++]	="GREATER_EQUAL";

KA_DC_CHAR[i]	=" menor que ";
KA_IC_CHAR[i++]	="LESSER";

KA_DC_CHAR[i]	=" menor ou igual que ";
KA_IC_CHAR[i++]	="LESSER_EQUAL";

KA_DC_CHAR[i]	=" contem dados ";
KA_IC_CHAR[i++]	="ISNOTNULL";

KA_DC_CHAR[i]	=" não contem dados ";
KA_IC_CHAR[i++]	="ISNULL";



var KA_DC_NUMBER=[];
var KA_IC_NUMBER=[];
var i=0;
KA_DC_NUMBER[i]		=" Igual a ";
KA_IC_NUMBER[i++]	="EQUAL";

KA_DC_NUMBER[i]		=" diferente de ";
KA_IC_NUMBER[i++]	="NOTEQUAL";

KA_DC_NUMBER[i]		=" maior que ";
KA_IC_NUMBER[i++]	="GREATER";

KA_DC_NUMBER[i]		=" maior ou igual que ";
KA_IC_NUMBER[i++]	="GREATER_EQUAL";

KA_DC_NUMBER[i]		=" menor que ";
KA_IC_NUMBER[i++]	="LESSER";

KA_DC_NUMBER[i]		=" menor ou igual que ";
KA_IC_NUMBER[i++]	="LESSER_EQUAL";

KA_DC_NUMBER[i]		=" contem dados ";
KA_IC_NUMBER[i++]	="ISNOTNULL";

KA_DC_NUMBER[i]		=" não contem dados ";
KA_IC_NUMBER[i++]	="ISNULL";


var KA_DC_NOBJECT=[];
var KA_IC_NOBJECT=[];
var i=0;
KA_DC_NOBJECT[i]	=" Igual a ";
KA_IC_NOBJECT[i++]	="EQUAL";

KA_DC_NOBJECT[i]	=" diferente de ";
KA_IC_NOBJECT[i++]	="NOTEQUAL";
	
KA_DC_NOBJECT[i]	=" contendo ";
KA_IC_NOBJECT[i++]	="IN";
	
KA_DC_NOBJECT[i]	=" não contem ";
KA_IC_NOBJECT[i++]	="NOTIN";

KA_DC_NOBJECT[i]	=" contem dados ";
KA_IC_NOBJECT[i++]	="ISNOTNULL";

KA_DC_NOBJECT[i]	=" não contem dados ";
KA_IC_NOBJECT[i++]	="ISNULL";
	

var KA_DC_OBJECT=[];
var KA_IC_OBJECT=[];
var i=0;
KA_DC_OBJECT[i]	=" Igual a ";
KA_IC_OBJECT[i++]	="EQUAL";

KA_DC_OBJECT[i]	=" diferente de ";
KA_IC_OBJECT[i++]	="NOTEQUAL";
	
KA_DC_OBJECT[i]	=" contendo ";
KA_IC_OBJECT[i++]	="IN";
	
KA_DC_OBJECT[i]	=" não contem ";
KA_IC_OBJECT[i++]	="NOTIN";

KA_DC_OBJECT[i]	=" contem dados ";
KA_IC_OBJECT[i++]	="ISNOTNULL";

KA_DC_OBJECT[i]	=" não contem dados ";
KA_IC_OBJECT[i++]	="ISNULL";


var KA_DC_LOV=[];
var KA_IC_LOV=[];
var i=0;
KA_DC_LOV[i]	=" Igual a ";
KA_IC_LOV[i++]	="EQUAL";

KA_DC_LOV[i]	=" diferente de ";
KA_IC_LOV[i++]	="NOTEQUAL";
	
KA_DC_LOV[i]	=" contendo ";
KA_IC_LOV[i++]	="IN";
	
KA_DC_LOV[i]	=" não contem ";
KA_IC_LOV[i++]	="NOTIN";

KA_DC_LOV[i]	=" contem dados ";
KA_IC_LOV[i++]	="ISNOTNULL";

KA_DC_LOV[i]	=" não contem dados ";
KA_IC_LOV[i++]	="ISNULL";


//--------------------------------------------------------------------------------------------------------
function query( qXML , aXML , parentHTML , index, build, askingForParams )
{
  this.pos=0;
  this._qXML = qXML;
  this.attrXML=aXML;
  this.checked = false;
  this.join			= null;
	this.nullIgnore = false;

  if ( qXML )
  {
		if(qXML.nodeName != 'boql')
		{
			this.attributeName	= qXML.selectSingleNode("attribute").text;
			this.condition		= qXML.selectSingleNode("condition").text;
			this.value			= changeToSpecialCode(qXML.selectSingleNode("value").firstChild.nodeValue);
			this.valueAux = changeToSpecialCode(qXML.selectSingleNode("value").firstChild.nodeValue);
			this.subquery		= qXML.selectSingleNode("subquery").text;
			
		  if(qXML.selectSingleNode("question"))
		  {
		    this.etiqueta = qXML.selectSingleNode("question").text;
		    if(this.etiqueta=="") this.etiqueta=null;
				this.nullIgnore = "1" == qXML.selectSingleNode("nullIgnore").text;
		  }
		  else
		  {
		    this.etiqueta = null;
		  }
		}
  }
  else
  {
	this.attributeName	= "_x_";
	this.condition		= "";
	this.value			= "";
  this.valueAux			= "";
	this.subquery		= "";
	this.join			= "";
  }
  
  

	if (qXML )	this.join		= qXML.selectSingleNode("join").text;
	else this.join="";
	if ( this.join == "" || this.join == "null" ) this.join="EMPTY"
  
  
  
  this.parentHTML	= parentHTML;
  this.htm			= null;
  this.index		= index;
  
  this.cellCheck	= null;
  this.cellJoin		= null;
  this.cellAttribute= null;
  this.cellCondition= null;
  this.cellValue	= null;
  this.cellAux	= null;
  this.cellParam	= null;
  this.cellSubQuery	= null;
  
  this.lov=false;
 
  if(askingForParams)
  {
    if(build)
    {
      this.cellAskParam	= null;  
      this.buildHTMLParams();
    }
  }
  else
  {
    if(build)
    {  
      this.buildHTML();
      this.fetchValues()
    }
  }
}


query.prototype.askParameters=function()
{
  if(this.etiqueta && this.etiqueta.length > 0)
  {
    return true;
  }
  return false;
}
query.prototype.fetchValues=function()
{
    var displayValues=[];
	var internalValues=[];

/*
	for ( var i= 0; i< this.attrXML.childNodes.length ; i++ )
	{
	    
		displayValues[displayValues.length]		= this.attrXML.childNodes[i].selectSingleNode("label").text;
		//displayValues[displayValues.length]		= this.attrXML.childNodes[i].nodeName;
		internalValues[internalValues.length]	= this.attrXML.childNodes[i].nodeName;
	}
*/

	
	//NOVA VERSÂO
		var xx1=
		createFieldText(
			this.attributeName=='_x_'? " ":this.attributeName,		//Value,
			"attr_"+this.index,		//name
			"attr_"+this.index,		//id,
			2*this.index,				//tabIndex,
			false,			//isDisabled ,
			true,			//isVisible ,
			"changeAttribute()",				//onChange  ,
			false,			//isRequired,
			false,			//isRecommend,
			100				//charLen
			);
		var xx2="&nbsp;<button style='width:25px;height:20px' onclick='showTreeAttr("+this.index+")'>....</button>";
		
		//alert(this.cellValue.innerHTML)
		//LOV CASE
		if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
    )
    {
		  this.cellAttribute.innerHTML="&nbsp";
    }
    else
    {
      this.cellAttribute.innerHTML="<table width=100% cellpadding=0 cellspacing=0><tr><td width=100%>"+xx1+"</td><td>"+xx2+"</td></tr></table>";
      this.cellAttribute.firstChild.rows[0].cells[0].firstChild.contentEditable=false;
		  this.cellAttribute.firstChild.rows[0].cells[0].firstChild.indexQry=this.index;
		  this.cellAttribute.firstChild.rows[0].cells[0].firstChild.onclick = showTreeAttr ;
    }		
		
		
		var nodeAttr = null;
    if(this.attrXML)
    {
		    nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
    }
	if ( nodeAttr )
	{
		Ntype=nodeAttr.getAttribute("type");
		if ( Ntype.indexOf(".") > -1 )
		{
			
			Ntype=Ntype.split(".")[0];
		}		
    if(!(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR")
    )
    {
		  this.cellAttribute.firstChild.rows[0].cells[0].firstChild.value=nodeAttr.selectSingleNode("label").text;
      this.resetBorder();
    }
		
	}
	
	
		//this.cellAttribute.firstChild.style.width="100%";
	

		
	//this.cellAttribute.firstChild.style.width="100%";
	
	/*antiga versão
	this.cellAttribute.innerHTML=createFieldCombo(
				this.attributeName=='_x_'? " ":this.attributeName,			//value
				"attr_"+this.index,			//Name
				"attr_"+this.index,		//id
				"2",			//tabIndex
				displayValues,	
				internalValues,
				false,			//allowValueEdit
				false,			//isDisable
				"changeAttribute")

*/

	if ( this.index > 1)  
	{
		displayValues=[];
		internalValues=[];

        displayValues[displayValues.length]="&nbsp";
		internalValues[internalValues.length]="EMPTY";

		displayValues[displayValues.length]=" E ";
		internalValues[internalValues.length]="AND";

		displayValues[displayValues.length]=" OU ";
		internalValues[internalValues.length]="OR";

        displayValues[displayValues.length]=" ( ";
		internalValues[internalValues.length]="LPAR";

        displayValues[displayValues.length]=" ) ";
		internalValues[internalValues.length]="RPAR";

        displayValues[displayValues.length]=" E( ";
		internalValues[internalValues.length]="ELPAR";

        displayValues[displayValues.length]=" )E ";
		internalValues[internalValues.length]="ERPAR";

        displayValues[displayValues.length]=" )E( ";
		internalValues[internalValues.length]="EBPAR";

		displayValues[displayValues.length]=" OU( ";
		internalValues[internalValues.length]="OLPAR";

        displayValues[displayValues.length]=" )OU ";
		internalValues[internalValues.length]="ORPAR";

        displayValues[displayValues.length]=" )OU( ";
		internalValues[internalValues.length]="OBPAR";



		this.cellJoin.innerHTML=
		createFieldCombo(
				this.join,				//value
				"lig_"+this.index,		//Name
				"lig_"+this.index,		//id
				"1",					//tabIndex
				displayValues,	
				internalValues,
				false,					//allowValueEdit
				false,					//isDisable
				"changeJoin")	
	}
    else
    {
        displayValues=[];
		    internalValues=[];

        displayValues[displayValues.length]="&nbsp";
		    internalValues[internalValues.length]="EMPTY";

    		displayValues[displayValues.length]=" ( ";
    		internalValues[internalValues.length]="LPAR";
    
    		displayValues[displayValues.length]=" ) ";
    		internalValues[internalValues.length]="RPAR";


    if(this.join == null || this.join=="AND" || this.join=="OR")
    {
      this.join="EMPTY";
    }
		this.cellJoin.innerHTML=
		createFieldCombo(
				this.join,				//value
				"lig_"+this.index,		//Name
				"lig_"+this.index,		//id
				"1",					//tabIndex
				displayValues,	
				internalValues,
				false,					//allowValueEdit
				false,					//isDisable
				"changeJoin")	
    }
		this.buildCondition();
		this.buildCellValue();
    this.buildCellParam();
    
	
}


query.prototype.buildCondition=function( newValue )
{
  
  
	var displayCondition;
	var internalCondition;
	var nodeAttr=null;
	if ( this.attributeName!=" " && this.attributeName!="_x_" ) 
  {
      if(this.attrXML)
      {
         nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
      }
   }
	if ( nodeAttr )
	{
		var lov=nodeAttr.selectSingleNode("lov");	
		if ( lov ) this.lov = true;
		
		var type=nodeAttr.getAttribute("type");
		var subtype="";
		var relation=nodeAttr.getAttribute("relation");
		if ( type.indexOf(".") > -1 )
		{
			subtype=type.split(".")[1];
			type=type.split(".")[0];
		}
		if ( lov )
		{
			displayCondition	=KA_DC_LOV;
			internalCondition	=KA_IC_LOV;
		
		}
		else if ( type == "char"  )
		{
			displayCondition	=KA_DC_CHAR;
			internalCondition	=KA_IC_CHAR;
		}
		else if ( type == "number" || type=="duration")
		{
			displayCondition	=KA_DC_NUMBER;
			internalCondition	=KA_IC_NUMBER;
		}
		else if ( type == "date" || type=="datetime" )
		{
			displayCondition	=KA_DC_NUMBER;
			internalCondition	=KA_IC_NUMBER;
		}
		else if ( type == "object" )
		{
			if ( relation == "1" )
			{
				displayCondition	=KA_DC_OBJECT;
				internalCondition	=KA_IC_OBJECT;
			}
			else if ( relation == "N" )
			{
				displayCondition	=KA_DC_NOBJECT;
				internalCondition	=KA_IC_NOBJECT;
			}
			
		}
		
		var exists=false;
		for ( var i=0 ; !exists && i < internalCondition.length ; i++)
		{
			if ( this.condition==internalCondition[i] )
			{
				exists=true;
			}
		}
		if ( !exists )
		{
			this.condition=internalCondition[0];
		}
		
		this.cellCondition.innerHTML=
		createFieldCombo(
				this.condition,				//value
				"cond_"+this.index,		//Name
				"cond_"+this.index,		//id
				"1",					//tabIndex
				displayCondition,	
				internalCondition,
				false,					//allowValueEdit
				false,					//isDisable
				"changeCondition")	
  }
}

query.prototype.changeAttribute=function( newValue )
{
  if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
    )
  {
    return;
  }
  if ( !this.htm.auto)
  {
	  this.htm.auto=true;
	  this.htm.firstChild.firstChild.checked=true;
  }
  var nodeAttr=null;
  if(this.attrXML)
  {
	   nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
	var Atype="";
	var Ntype="";
	if ( nodeAttr )
	{
		Atype=nodeAttr.getAttribute("type");
		if ( Atype.indexOf(".") > -1 )
		{
			
			Atype=Atype.split(".")[0];
		}
		
		
		
	}
	else Atype="...l";
  
	this.attributeName=newValue;
	var nodeAttr=null;
  if(this.attrXML)
  {
	  nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
	if ( nodeAttr )
	{
		Ntype=nodeAttr.getAttribute("type");
		if ( Ntype.indexOf(".") > -1 )
		{
			
			Ntype=Ntype.split(".")[0];
		}
		this.cellAttribute.firstChild.rows[0].cells[0].firstChild.value=nodeAttr.selectSingleNode("label").text;
		
	}
	this.resetBorder();
	
	if ( Atype!=Ntype ){
    this.value="";
    this.valueAux="";
  }
	
	this.buildCondition();
	this.buildCellValue();    
    this.buildCellParam();
	
}

query.prototype.resetBorder=function()
{
	this.cellAttribute.firstChild.rows[0].cells[0].firstChild.runtimeStyle.border="1px solid #6B8C9C";
}

query.prototype.countPar=function(join, pCount)
{
  if(
    this.join == "LPAR" ||
    this.join == "ELPAR" ||
    this.join == "OLPAR"
  )
  {   
    return 1;
  }

  if(pCount == 0)
  {
    if(      
      this.join == "RPAR" ||
      this.join == "ERPAR" ||
      this.join == "ORPAR" ||
      this.join == "EBPAR" ||
      this.join == "OBPAR"
    )
    {
      return -999;//tem de dar erro
    }
  }
  else if(
    this.join == "RPAR" ||
    this.join == "ERPAR" ||
    this.join == "ORPAR"
  )
  {   
    return -1;
  }
  
  return 0;
}

query.prototype.valid=function(lastJoin)
{
  var toRet=true;

  if(lastJoin != null && lastJoin != "" && lastJoin != "EMPTY")
  {
    if(lastJoin == "LPAR" || lastJoin == "ELPAR" ||
       lastJoin == "OLPAR" || lastJoin == "EBPAR" ||
       lastJoin == "OBPAR"
    )
    {
      if(
          this.join == "OR" ||this.join == "AND" || 
          this.join == "RPAR" ||
          this.join == "ELPAR" || this.join == "ERPAR" ||
          this.join == "OLPAR" || this.join == "ORPAR" ||
          this.join == "EBPAR" || this.join == "OBPAR"
      )
      {
        toRet = false;
      }
    }
    else if(lastJoin == "RPAR")
    {
      if(this.join == "LPAR")
      {
        toRet = false;
      }
    }
    else if(lastJoin == "ERPAR"||lastJoin =="ORPAR")
    {
      if(this.join == "LPAR" || this.join == "OR" ||this.join == "AND")
      {
        toRet = false;
      }
    }
    else if(lastJoin == "OR" || lastJoin == "AND")
    {
      if(
          this.join == "LPAR"
      )
      {   
        toRet=false;
      }
    }
    else
    {
      if(
          this.join == "LPAR" || this.join == "RPAR" ||
          this.join == "ELPAR" || this.join == "ERPAR" ||
          this.join == "OLPAR" || this.join == "ORPAR" ||
          this.join == "EBPAR" || this.join == "OBPAR"
      )
      {   
        toRet=true;
      }
      else if ( this.attributeName=="" || this.attributeName=="_x_" || this.condition=="" || ( this.value=="" && !(  (filterIsSaved&&this.etiqueta) || this.condition=="ISNOTNULL" || this.condition=="ISNULL")) )
      {
        toRet=false;
      }
    }
  }
  else if(
          this.join == "LPAR" || this.join == "RPAR" ||
          this.join == "ELPAR" || this.join == "ERPAR" ||
          this.join == "OLPAR" || this.join == "ORPAR" ||
          this.join == "EBPAR" || this.join == "OBPAR"
  )
  {   
    toRet=true;
  }
	else if(lastJoin != null && (this.join == null || this.join == "" || this.join == "EMPTY"))
	{
		toRet = false;
	}
  else if ( this.attributeName=="" || this.attributeName=="_x_" || this.condition=="" ||( this.value=="" && !(   (filterIsSaved&&this.etiqueta) ||this.condition=="ISNOTNULL" || this.condition=="ISNULL")) )
  {
	  toRet=false;
  }
  
  return toRet;
}

query.prototype.changeJoin=function( newValue )
{
	
	this.join=newValue;
	this.buildJoinCell(newValue);
	
}

query.prototype.changeCondition=function( newValue )
{
	
	this.condition=newValue;
	this.buildCellValue();
	this.buildCellParam();
}

query.prototype.changeValue=function( newValue )
{
  
	this.value=newValue;
    
	//this.buildCellValue();
	
}

query.prototype.buildJoinCell=function(newValue)
{
    if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
    )
    {   
        this.htm.firstChild.firstChild.checked=true;
        this.cellAttribute.innerHTML='&nbsp;';
        this.cellCondition.innerHTML='&nbsp;';
        this.cellValue.innerHTML='&nbsp;';
        this.cellAux.innerHTML='&nbsp;';
        this.cellParam.innerHTML='&nbsp;';
    }
    else
    {
        this.fetchValues();
        this.buildCondition();
        this.buildCellValue();
        this.buildCellParam();
    }
}

query.prototype.buildCellParam=function()
{
  if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
  )
  {
     return;
  }
  if (!this.attributeName || this.attributeName==" ") return;
  var nodeAttr=null;
  if(this.attrXML)
  {
	   nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
  if ( !nodeAttr ) return;
  if ( this.condition=="ISNOTNULL" || this.condition=="ISNULL" || !filterIsSaved )
	{
		this.cellParam.innerHTML="&nbsp;";
		//this.value="";
	
	}
  else if(!this.askParameters())
  {
  	this.cellParam.innerHTML='<img title="Configurar parâmetro" onclick="buildParam(' + this.index + ')"src="templates/grid/std/ghParam.gif" width="13" height="13" />';
  }
  else
  {
	  this.cellParam.innerHTML='<img title="Configurar parâmetro" onclick="buildParam(' + this.index + ')"src="templates/grid/std/ghParamA.gif" width="13" height="13" />';
  }
  
}
query.prototype.getCellValue=function(myValue)
{    
	if (!this.attributeName || this.attributeName==" ") return myValue;
	var nodeAttr=null;
  if(this.attrXML)
  {
	  nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
	if ( !nodeAttr ) return myValue;
	var type=nodeAttr.getAttribute("type");
	
	if ( type.indexOf(".") > -1 )
	{
		subtype=type.split(".")[1];
		type=type.split(".")[0];
	}
	
	var lov=nodeAttr.selectSingleNode("lov");
	if (lov)
	{
		var value=changeToSpecialCode(myValue);
		var xv=this.value.split(";");
		value="";
		for( var j=0;j<xv.length;j++ )
		{
			for ( var i=0; i<lov.childNodes.length; i++ )
			{
				var l=lov.childNodes[i].selectSingleNode("description").text;
				var v=lov.childNodes[i].selectSingleNode("value").text;
				if ( v == xv[j] )
				{
					value+=l+";";
				}
			}		
		}
			return value;
	}
	return myValue;
}
query.prototype.buildCellAux=function(query)
{
    if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
    )
    {
     return;
    }
	if (!this.attributeName || this.attributeName==" ") return;
	var nodeAttr=null;
  if(this.attrXML)
  {
	  nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
	if ( !nodeAttr ) return;
	var type=nodeAttr.getAttribute("type");
	
	if ( type.indexOf(".") > -1 )
	{
		subtype=type.split(".")[1];
		type=type.split(".")[0];
	}
	
	var lov=nodeAttr.selectSingleNode("lov");
	if ( this.condition=="ISNOTNULL" || this.condition=="ISNULL")
	{
		this.cellAux.innerHTML="&nbsp;";
		//this.valueAux="";
	
	}
	else if ( type == 'char' || lov )
	{
		var value=this.valueAux;		
		if ( lov )
		{
			var xv=this.valueAux.split(";");
			value="";
			for( var j=0;j<xv.length;j++ )
			{
					for ( var i=0; i<lov.childNodes.length; i++ )
					{
						var l=lov.childNodes[i].selectSingleNode("description").text;
						var v=lov.childNodes[i].selectSingleNode("value").text;
						if ( v == xv[j] )
						{
						 value+=l+";";	  
						}
					}		
			}
		}
		var xx1=
		createFieldText(
			value,		//Value,
			"param_name_"+this.index,		//name
			"param_field_"+this.index,		//id,
			4*this.index,				//tabIndex,
			false,			//isDisabled ,
			true,			//isVisible ,
			"changeValueAux(value,"+this.index+")",				//onChange  ,
			false,			//isRequired,
			false,			//isRecommend,
			100				//charLen
			)
		var xx2=lov?"&nbsp;<button style='width:25px;height:20px' onclick='buildlov("+this.index+", true)' id=button1 name=button1>....</button>":"";
		
		//alert(this.cellAux.innerHTML)
		//LOV CASE
		
		this.cellAux.innerHTML=xx1+xx2;
		if ( lov )
		{
			this.cellAux.firstChild.contentEditable=false;
			this.cellAux.firstChild.indexQry=this.index;
			this.cellAux.firstChild.onclick=buildlov;
			this.cellAux.firstChild.style.width="160px";
		}    
		
		//this.cellSubQuery.innerHTML="<button style='width:25px' id=button1 name=button1>....</button>"
			
	
	}
	else if ( type == 'number' )
	{
		
		this.cellAux.innerHTML=
			createFieldNumber(
			this.valueAux,		//Value,
            "param_name_"+this.index,		//name
            "param_field_"+this.index,		//id,
            4*this.index,	//tabIndex,
			"",				//type,
            nodeAttr.getAttribute("decimals"),				//decimals,
            false,			//grouping,
            999999999,		//max,
            -99999999,		//min,
            false,			//isDisabled ,
            true,			//isVisible ,
            "changeValueAux(value,"+this.index+")",				//onChange  ,
            false,			//isRequired,
            false			//isRecommend
            )
	
	
	}
	else if ( type == 'date' )
	{
		
		//var xt=this.valueAux.substr(
		
		var x=this.valueAux.substr(0,10).split("-");
		var v="";
		if ( x.length >=3) {
			this.valueAux=x[2]+"-"+x[1]+"-"+x[0];
			var v=x[0]+"-"+x[1]+"-"+x[2];
			
		}
		
		if (!ParseDate(this.valueAux,true))
		{
		 if ( !ParseDate(v,true) )	 this.valueAux="";
		 else this.valueAux=v;
		 
		}
		this.cellAux.innerHTML=
		createFieldDate(
			this.valueAux ,	// Value,
			"param_name_"+this.index,		//name
            "param_field_"+this.index,		//id,
            4*this.index,				// tabIndex,
            false,			// isDisabled ,
            true,			// isVisible ,
            "changeValueAux(returnValue,"+this.index+")"				//onChange  ,
            )
	
	
	}
	else if ( type == 'datetime' )
	{
		
		
		var x=this.valueAux.split("-");
		var v=""
		var xt="";
		if ( x.length >=3) {
			var x1=this.valueAux.split("T");
			if(x1.length>1) xt="T"+x1[1];
			else xt="12:00";
			
  		this.valueAux=x[0]+"-"+x[1]+"-"+x[2].substr(0,2);
		}
		if(xt) this.valueAux+=xt;
		this.cellAux.innerHTML=
		createFieldDateTime(
            this.valueAux ,	// Value,
            "param_name_"+this.index,		//name
            "param_field_"+this.index,		//id,
            4*this.index,				// tabIndex,
			true,			//allowValueEdit,
            false,			// isDisabled ,
            true,			// isVisible ,
            "changeValueAux(returnValue,"+this.index+")"				//onChange  ,
            )
	
	}
	else if ( type == 'duration' )
	{
	
	
	this.cellAux.innerHTML=
	createFieldDuration(
        this.valueAux,			//Value,
        "param_name_"+this.index,		//name
        "param_field_"+this.index,		//id,
        4*this.index,				//tabIndex,
        true,			//allowValueEdit,
        false,			//isDisabled ,
        true,			//isVisible ,
        "changeValueAux(value,"+this.index+")",				//onChange  ,
        false,			//isRequired,
        true,			//isRecommend,
        false			//clock
        )

	
	}
	else if ( type == 'object' )
	{
		var subtype="";
		var relation=nodeAttr.getAttribute("relation");
		if ( type.indexOf(".") > -1 )
		{
			subtype=type.split(".")[1];
			type=type.split(".")[0];
		}
	
		var lookupStyle;
		if ( relation=="1") lookupStyle="single";
		else lookupStyle="multi";
		
		
				
		var cardObjects=this.cards;
		var objectLabel=nodeAttr.selectSingleNode( "objectLabel" ).text;
		var objectName=nodeAttr.selectSingleNode( "objectName" ).text;
		var nodeObj=nodeAttr.selectSingleNode( "objects" ); //lista de objectos separados por ;

		
        
		if ( nodeObj ) nodeObjects=nodeObj.text;
		else nodeObjects=objectName;

		
		this.cellAux.id="look"+new Date();
		this.cellAux.callBackObject=query;
		
		lookupStyle='multi';
		this.cellAux.innerHTML=
		createDetachFieldLookup(
			this.valueAux,				//Value
			"param_name_"+this.index,		//Name
			"param_field_"+this.index,	//id
			nodeObjects,			//listOfValidObjects
			objectName,				//objectName
			objectLabel,			//objectLabel
			getDocId(),						//docID 
			lookupStyle,			//lookupStyle->single or multi
			4*this.index,			//tabIndex
			false ,					//isDisabled
			true 					//isVisible
			)

		//	winmain().sendCmd("GETCARDID","bouis="+b[2]+"&docid="+getDocId(),winmain().lixo);
		//this.cellAux.firstChild.rows[0].cells[1].firstChild //aponta para a img
		
					
	
	}
}
query.prototype.buildCellValue=function()
{    
    if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
    )
    {
     return;
    }
	if (!this.attributeName || this.attributeName==" ") return;
	var nodeAttr=null;
  if(this.attrXML)
  {
	  nodeAttr=this.attrXML.selectSingleNode( this.attributeName );
  }
	if ( !nodeAttr ) return;
	var type=nodeAttr.getAttribute("type");
	
	if ( type.indexOf(".") > -1 )
	{
		subtype=type.split(".")[1];
		type=type.split(".")[0];
	}
	
	var lov=nodeAttr.selectSingleNode("lov");
	if ( this.condition=="ISNOTNULL" || this.condition=="ISNULL")
	{
		this.cellValue.innerHTML="&nbsp;";
		//this.value="";
	
	}
	else if ( type == 'char' || lov )
	{
		var value=changeToSpecialCode(this.value);
		if ( lov )
		{
			var xv=this.value.split(";");
			value="";
			for( var j=0;j<xv.length;j++ )
			{
					for ( var i=0; i<lov.childNodes.length; i++ )
					{
						var l=lov.childNodes[i].selectSingleNode("description").text;
						var v=lov.childNodes[i].selectSingleNode("value").text;
						if ( v == xv[j] )
						{
						 value+=l+";";	  
						}
					}		
			}
		}
		var xx1=
		createFieldText(
			value,		//Value,
			"name_"+this.index,		//name
			"field_"+this.index,		//id,
			4*this.index,				//tabIndex,
			false,			//isDisabled ,
			true,			//isVisible ,
			"changeValue()",				//onChange  ,
			false,			//isRequired,
			false,			//isRecommend,
			100				//charLen
			)
		var xx2=lov?"&nbsp;<button style='width:25px;height:20px' onclick='buildlov("+this.index+")' id=button1 name=button1>....</button>":"";
		
		//alert(this.cellValue.innerHTML)
		//LOV CASE
		
		this.cellValue.innerHTML=xx1+xx2;
		if ( lov )
		{
			this.cellValue.firstChild.contentEditable=false;
			this.cellValue.firstChild.indexQry=this.index;
			this.cellValue.firstChild.onclick=buildlov;
			this.cellValue.firstChild.style.width="160px";
		}    
		
		//this.cellSubQuery.innerHTML="<button style='width:25px' id=button1 name=button1>....</button>"
			
	
	}
	else if ( type == 'number' )
	{
		
		this.cellValue.innerHTML=
			createFieldNumber(
			this.value,		//Value,
            "name_"+this.index,		//name
            "field_"+this.index,		//id,
            4*this.index,	//tabIndex,
			"",				//type,
            nodeAttr.getAttribute("decimals"),				//decimals,
            false,			//grouping,
            999999999,		//max,
            -99999999,		//min,
            false,			//isDisabled ,
            true,			//isVisible ,
            "changeValue()",	//onChange 
            false,			//isRequired,
            false			//isRecommend
            )
	
	
	}
	else if ( type == 'date' )
	{
		
		//var xt=this.value.substr(
		
		var x=this.value.substr(0,10).split("-");
		var v="";
		if ( x.length >=3) {
			this.value=x[2]+"-"+x[1]+"-"+x[0];
			var v=x[0]+"-"+x[1]+"-"+x[2];
			
		}
		
		if (!ParseDate(this.value,true))
		{
		 if ( !ParseDate(v,true) )	 this.value="";
		 else this.value=v;
		 
		}
		this.cellValue.innerHTML=
		createFieldDate(
			this.value ,	// Value,
			"name_"+this.index,		//name
            "field_"+this.index,		//id,
            4*this.index,				// tabIndex,
            false,			// isDisabled ,
            true,			// isVisible ,
            "changeValue()"				//onChange  ,
            )
	
	
	}
	else if ( type == 'datetime' )
	{
		
		
		var x=this.value.split("-");
		var v=""
		var xt="";
		if ( x.length >=3) {
			var x1=this.value.split("T");
			if(x1.length>1) xt="T"+x1[1];
			else xt="12:00";
			
			this.value=x[0]+"-"+x[1]+"-"+x[2].substr(0,2);
			v=x[2].substr(0,2)+"/"+x[1]+"/"+x[0];
			
		}

// flc: retirei para os horas apareçam após aplicar o filtro		
//		if (!ParseDate(this.value,true))
//		{
//			 if ( !ParseDate(v,true) )	 this.value="";
//			 else this.value=v;
//		}
		if(xt) this.value+=xt;
		this.cellValue.innerHTML=
		createFieldDateTime(
            this.value ,	// Value,
            "name_"+this.index,		//name
            "field_"+this.index,		//id,
            4*this.index,				// tabIndex,
			true,			//allowValueEdit,
            false,			// isDisabled ,
            true,			// isVisible ,
            "changeValue()"				//onChange  ,
            )
	
	}
	else if ( type == 'duration' )
	{
	
	
	this.cellValue.innerHTML=
	createFieldDuration(
        this.value,			//Value,
        "name_"+this.index,		//name
        "field_"+this.index,		//id,
        4*this.index,				//tabIndex,
        true,			//allowValueEdit,
        false,			//isDisabled ,
        true,			//isVisible ,
        "changeValue()",				//onChange  ,
        false,			//isRequired,
        true,			//isRecommend,
        false			//clock
        )

	
	}
	else if ( type == 'object' )
	{
		var subtype="";
		var relation=nodeAttr.getAttribute("relation");
		if ( type.indexOf(".") > -1 )
		{
			subtype=type.split(".")[1];
			type=type.split(".")[0];
		}
	
		var lookupStyle;
		if ( relation=="1") lookupStyle="single";
		else lookupStyle="multi";
		
		
				
		var cardObjects=this.cards;
		var objectLabel=nodeAttr.selectSingleNode( "objectLabel" ).text;
		var objectName=nodeAttr.selectSingleNode( "objectName" ).text;
		var nodeObj=nodeAttr.selectSingleNode( "objects" ); //lista de objectos separados por ;

		
        
		if ( nodeObj ) nodeObjects=nodeObj.text;
		else nodeObjects=objectName;

		this.cellValue.id="look"+new Date();
		this.cellValue.callBackObject=this;
		
		lookupStyle='multi';
		this.cellValue.innerHTML=
		createDetachFieldLookup(
			this.value,				//Value
			"name_"+this.index,		//Name
			"field_"+this.index,	//id
			nodeObjects,			//listOfValidObjects
			objectName,				//objectName
			objectLabel,			//objectLabel
			getDocId(),						//docID 
			lookupStyle,			//lookupStyle->single or multi
			4*this.index,			//tabIndex
			false ,					//isDisabled
			true 					//isVisible
			)

		//	winmain().sendCmd("GETCARDID","bouis="+b[2]+"&docid="+getDocId(),winmain().lixo);
		//this.cellValue.firstChild.rows[0].cells[1].firstChild //aponta para a img
		
					
	
	}
}


query.prototype.buildHTMLParams=function()
{ 
	var rows=this.parentHTML.rows;
  if(this.etiqueta != null)
  {
    this.htm=this.parentHTML.insertRow();
    this.htm.noStyle=true;
    this.htm.style.height="24px";
    //this.htm.style.backgroundColor="red";
    this.htm.id="Line"+this.index;
    this.cellAskParam=this.htm.insertCell();
    //this.cellAskParam.style.fontFamily="Arial, Helvetica, sans-serif;";
    this.cellAskParam.style.fontWeight="bold";
    this.cellAskParam.style.fontSize="11px";
    this.cellAskParam.innerHTML=this.etiqueta;
    
    //this.cellValue=this.htm.insertCell();
    //this.cellValue.innerHTML='&nbsp;';
    //this.htm=this.parentHTML.insertRow();
    //this.cellAskParam=this.htm.insertCell();
    //this.cellAskParam.innerHTML='<td width="0">&nbsp;</td>';
    this.cellAux=this.htm.insertCell();
     this.cellAux.innerHTML='&nbsp;';
    this.buildCellAux(this);
  }
}

query.prototype.buildHTML=function()
{ 
  
	var rows=this.parentHTML.rows;
	this.htm=this.parentHTML.insertRow();
	this.htm.noStyle=true;											
	this.htm.id="Line"+this.index;
	
	this.cellCheck =this.htm.insertCell();
	this.cellCheck.className="gCell_std";
	//this.cellCheck.innerHTML="<input onclick=\"so('g1643');onClick_GridBody_std(event)\" class=\"rad\" type=\"checkBox\" id=\"checkBox"+this.index+"\" name=\"checkBox"+this.index+"\">"
  if(this.checked)
  {
    this.cellCheck.innerHTML="<input onclick=\"markChecked(" +this.index+ ");\" class=\"rad\" CHECKED onchange=\"markChange(2);\" type=\"checkBox\" id=\"checkBox"+this.index+"\" name=\"checkBox"+this.index+"\">"
  }
  else
  {
	  this.cellCheck.innerHTML="<input onclick=\"markChecked(" +this.index+ ");\" class=\"rad\" onchange=\"markChange(2);\" type=\"checkBox\" id=\"checkBox"+this.index+"\" name=\"checkBox"+this.index+"\">"
  }
	//JOIN entre querys
	this.cellJoin=this.htm.insertCell();
	this.cellJoin.className="gCell_std";
	
	
	//attributes
	this.cellAttribute=this.htm.insertCell();
	this.cellAttribute.className="gCell_std";
	
	
	//condicao
	this.cellCondition=this.htm.insertCell();
	this.cellCondition.className="gCell_std";
	this.cellCondition.innerHTML='&nbsp;';
	this.cellCondition.id="cCond_"+this.index;
	//valor
	
	this.cellValue=this.htm.insertCell();
	this.cellValue.className="gCell_std";
	this.cellValue.innerHTML='&nbsp;';
	this.cellValue.id="cValue_"+this.index;  

  //parameters
	this.cellParam=this.htm.insertCell();
	this.cellParam.className="gCell_std";
	this.cellParam.innerHTML='&nbsp;';
	this.cellParam.id="cParam_"+this.index;

	//subQuery
	this.cellSubQuery=this.htm.insertCell();
	this.cellSubQuery.className="gCell_std";
	this.cellSubQuery.innerHTML='<img title="Adicionar linha" onclick="oQB.newQuery(' + this.index + ')"src="templates/grid/std/ghAdd.gif" width="13" height="13" />';
	this.cellSubQuery.id="cSubQuery_"+this.index;

  //escondido
	this.cellAux=this.htm.insertCell();
	this.cellAux.className="gCell_std";
	this.cellAux.innerHTML='&nbsp;';
	this.cellAux.id="cValue_"+this.index;

	if(this.join == "LPAR" || this.join == "RPAR" ||
     this.join == "ELPAR" || this.join == "ERPAR" ||
     this.join == "OLPAR" || this.join == "ORPAR" ||
     this.join == "EBPAR" || this.join == "OBPAR"
  )
  {
    this.cellAttribute.innerHTML='&nbsp;';
    this.cellCondition.innerHTML='&nbsp;';
    this.cellValue.innerHTML='&nbsp;';
    this.cellParam.innerHTML='&nbsp;';
  }
}


//---------------------------------------------------------------------------------------------------------




//---------------------------------------------------------------------------------------------------------
function qb(askParams)
{
    this.changed;
    
	this.htmlRows=0;
	this.querys=[];
	this.htm=document.getElementById("g1643_body");
	
	this.attrDoc		= new ActiveXObject("Microsoft.XMLDOM");
	this.attrDoc.async	= false;
	this.attrDoc.loadXML(_attributesXml);
	
	this.attrs			= this.attrDoc.selectSingleNode("*");
	
	this.filterDOC		= new ActiveXObject("Microsoft.XMLDOM");
	this.filterDOC.async	= false;
	this.filterDOC.loadXML(_filterXML);
	this.XMLquerys		= this.filterDOC.selectSingleNode("*");
	this.avancada = "";
	
	
	var pos = 1;
	for ( var i=0; i< this.XMLquerys.childNodes.length; i++,pos++ )
	{
		//query=this.XMLquerys.childNodes[i];
		if(this.XMLquerys.childNodes(i).nodeName != 'boql')
		{
			this.querys[this.querys.length] = new query( this.XMLquerys.childNodes(i), this.attrs , this.htm , pos, true,askParams );
		}
		else
		{
			this.avancada = this.XMLquerys.childNodes(i).text;
			pos--;
		}
	}



}
//*****
qb.prototype.resetAll=function()
{
  this.htm=document.getElementById("g1643_body");
  var tam = this.querys.length;
  for(var i = 0; i < tam - 1; i++)
  {
    this.htm.deleteRow(0);
  }

  for(var i=0; i < this.querys.length;i++)
  {
    this.querys[i].index		= i+1;
    if(this.querys[i].checked)
    {
      this.htm.firstChild.firstChild.checked = true;
    }

    this.querys[i].buildHTML();
    this.querys[i].fetchValues();
  }
}
qb.prototype.newQuery=function(pos)
{
  if(pos == null || this.querys.length == pos)
  {
	  this.querys[this.querys.length] = new query( null , this.attrs , this.htm , this.querys.length+1, true );
  }
  else
  {
    this.querys[this.querys.length] = new query( null , this.attrs , this.htm , this.querys.length+1, false );
    
    for ( var i=this.querys.length - 1 ; i>=pos;i--) 
	  {
      this.querys[i] = this.querys[i-1];
      this.querys[i].htm.firstChild.firstChild.checked = this.querys[i-1].htm.firstChild.firstChild.checked;
    }
    
    this.querys[pos - 1] = new query( null , this.attrs , this.htm , this.querys.length+1, false );
    
    this.resetAll();
  }
}


qb.prototype.removeSelected=function()
{
	for ( var i=0 ; i< this.querys.length;i++ ) 
	{
		var l=this.querys[i].htm.firstChild.firstChild.checked;
		if ( l )
		{
		   this.querys[i].htm.firstChild.firstChild.checked=false;;
		   this.querys[i].attributeName="_x_";
		   this.querys[i].condition="";
		   this.querys[i].value="";
		   this.querys[i].cellValue.innerHTML="&nbsp;";
       this.querys[i].cellParam.innerHTML="&nbsp;";
		   this.querys[i].cellCondition.innerHTML='&nbsp;';
			 this.querys[i].changeJoin("EMPTY");
		   this.querys[i].fetchValues();
		}
	
	}
   
}
function markChange(mode){
    this.changed = mode;
}

function markChecked(index){
    var qry=oQB.querys[index-1];
    if(qry.htm.firstChild.firstChild.checked)
    {
      qry.checked = true;
    }
    else
    {
      qry.checked = false;
    }
}

function buildElem(o)
{
    var xml = "";             
    xml+="<Ebo_FilterQuery__joinQuery>" + o.join + "</Ebo_FilterQuery__joinQuery>";       
    xml+="<Ebo_FilterQuery__attributeName>" + o.attributeName + "</Ebo_FilterQuery__attributeName>";
    xml+="<Ebo_FilterQuery__operator>" + o.condition + "</Ebo_FilterQuery__operator>";
    xml+="<Ebo_FilterQuery__value>" + changeFromSpecialCode(o.value) + "</Ebo_FilterQuery__value>";
    
    if( o.etiqueta )
    {
    xml+="<Ebo_FilterQuery__question>" + o.etiqueta + "</Ebo_FilterQuery__question>";
    }
    else
    {
    xml+="<Ebo_FilterQuery__question> </Ebo_FilterQuery__question>";
    }

		if( o.nullIgnore )
    {
    xml+="<Ebo_FilterQuery__nullIgnore>1</Ebo_FilterQuery__nullIgnore>";
    }
    else
    {
    xml+="<Ebo_FilterQuery__nullIgnore>0</Ebo_FilterQuery__nullIgnore>";
    }
    if(o.htm.firstChild.firstChild.checked){
        xml+="<Ebo_FilterQuery__active>" + 1 + "</Ebo_FilterQuery__active>";
    }else{
        xml+="<Ebo_FilterQuery__active>" + 0 + "</Ebo_FilterQuery__active>";
    }
    //xml+="<Ebo_FilterQuery__subFilter>" + o.subfilter + "</Ebo_FilterQuery__subFilter>";              
    //alert(xml);
    return xml;

}
// Constroi o xml da iframe

function getonlyObjects()
{
       var onlyObjects="";
       if(selectedAllObject.checked==true) return"";
        if(objectsToSearch.elements.length > 0){
            for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
            {
    
              if ( objectsToSearch.elements[i].checked )
              {
                onlyObjects+= objectsToSearch.elements[i].id;
                onlyObjects+=";";
              }      
            }        
            
        }
        return onlyObjects;
}

function BuildXml()
{
    var xmlQuery="";
   // if(this.changed == 1){                             
        var onlyObjects="";
        if(objectsToSearch.elements.length > 0){
            onlyObjects+= "<Ebo_Filter__"+ boFormSubmit.queryBoui.value  + "__onlyObjects>";    
            for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
            {
    
              if ( objectsToSearch.elements[i].checked )
              {
                onlyObjects+= objectsToSearch.elements[i].id;
                onlyObjects+=";";
              }      
            }        
            onlyObjects+= "</Ebo_Filter__"+ boFormSubmit.queryBoui.value  + "__onlyObjects>";
        }
        xmlQuery+=onlyObjects;
  //  }
  //  if(this.changed == 2){
        xmlQuery+= "<bo boui='" + boFormSubmit.queryBoui.value;
        xmlQuery+= "' attributeName='details'>";
        for ( var i = 0 ; i < oQB.querys.length ; i++ )
        {
           o = oQB.querys[i];       
           
           if(o.valid()){
           
               xmlQuery += "<bo new='Ebo_FilterQuery' mode='add'>";
               xmlQuery += buildElem(o);        
               xmlQuery += "</bo>";    
           }
        }
        xmlQuery += "</bo>";            
   // }
    //alert("The end : "+  xmlQuery );
    return xmlQuery;  
}
function buildInternalElem(o)
{                    
    var xml = "";             
    xml+="<join>" + o.join + "</join>";       
    xml+="<attribute>" + o.attributeName + "</attribute>";
    xml+="<condition>" + o.condition + "</condition>";
    xml+="<value><![CDATA[" + changeFromSpecialCode(o.value)+"]]></value>";
    if(o.etiqueta && o.etiqueta.length > 0 && o.etiqueta != 'null')
    {
      xml+="<question>" + o.etiqueta + "</question>"
    }
		if(o.nullIgnore)
    {
      xml+="<nullIgnore>1</nullIgnore>"
    }
		else
		{
  		xml+="<nullIgnore>0</nullIgnore>"
		}
    
    return xml;

}
qb.prototype.save=function(objectName)
{
    var onlyObjects="";
	for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
	{
	  if ( objectsToSearch.elements[i].checked )
	  {
		onlyObjects += objectsToSearch.elements[i].id + ";";
	  }
	}
	if(selectedAllObject.checked==true) onlyObjects="";
    var xmlQuery="<xmlFilter>";
    var lastJoin = null;
    var pCount = 0;
		if(oQB.avancada && "" != oQB.avancada)
		{
			xmlQuery += "<boql><![CDATA[" +oQB.avancada + "]]></boql>"
		}
    for ( var i = 0 ; i < oQB.querys.length ; i++ )
    {
        if(oQB.querys[i].valid(lastJoin)){
           xmlQuery +="<query>"
           xmlQuery += buildInternalElem(oQB.querys[i]);        
           xmlQuery +="</query>"
        }
      lastJoin = oQB.querys[i].join;
      pCount = pCount + this.querys[i].countPar(lastJoin, pCount);
    }
    xmlQuery +="</xmlFilter>";			
    winmain().openDoc('medium','ebo_filter','edit','method=new&object=Ebo_Filter&masterObjectClass=' + objectName + '&onlyObjects=' + onlyObjects + '&xmlFilter=' + xmlQuery);
}

qb.prototype.verify=function()
{
	var onlyObjects="";
	for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
	{
	 // alert( objectsToSearch.elements[i].id+objectsToSearch.elements[i].checked);
	  if ( objectsToSearch.elements[i].checked )
	  {
		onlyObjects+= objectsToSearch.elements[i].id+";";
	  }
	}
	if(selectedAllObject.checked==true) onlyObjects="";
	
	var nr=0;
	var lastJoin = null;
	var _valid = true;
	var pCount = 0;
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	//   	alert(this.querys[i].join+this.querys[i].attributeName+""+this.querys[i].condition+""+this.querys[i].value);
	   var l=this.querys[i].htm.firstChild.firstChild.checked;
	   if ( l )
	   {
			if ( this.querys[i].valid(lastJoin) )
			{
					nr++;
			}
			else
			{
				_valid = false;
				alert("ATENÇÃO: A linha "+(i+1)+" está inválida" );
			}
      lastJoin = this.querys[i].join;
      pCount = pCount + this.querys[i].countPar(lastJoin, pCount);
      if(pCount < 0)
      {
        pCount = -999;//tem de dar erro
      }
	   }
	}

  if(pCount != 0)
  {
    _valid = false;
    alert("Número de paranteses incorrecto." );
  }
	if(_valid)
  {
    return true;
  }
	return false;
}

qb.prototype.applyParamExplorer=function(option)
{
	//var xmlQuery="<parameters>";
	var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
	var elemParams = xmlQuery.createElement("parameters");
	xmlQuery.appendChild(elemParams);
	var nr=0;
	var pos = 0;
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	//   	alert(this.querys[i].join+this.querys[i].attributeName+""+this.querys[i].condition+""+this.querys[i].value);
      nr++;
      if(this.querys[i].askParameters())
      {
				var val = changeFromSpecialCode(this.querys[i].value);
				var elemParam = xmlQuery.createElement("parameter");
				var elemValue = xmlQuery.createElement("value");
				elemValue.appendChild(xmlQuery.createTextNode(val));
				var elemNull = xmlQuery.createElement("nullIgnore");
				if(this.querys[i].nullIgnore)
				{					
					elemNull.appendChild(xmlQuery.createTextNode("1"));
			  	//xmlQuery+="<parameter><value><![CDATA["+val+"]]></value><nullIgnore>1</nullIgnore></parameter>";
				}
				else
				{
					elemNull.appendChild(xmlQuery.createTextNode("0"));
					//xmlQuery+="<parameter><value><![CDATA["+val+"]]></value><nullIgnore>0</nullIgnore></parameter>";
				}
				elemParam.appendChild(elemNull);
				elemParam.appendChild(elemValue);
				elemParams.appendChild(elemParam);
      }
	}

//	xmlQuery+="</parameters>";
//	xmlQuery = encodeURIComponent(xmlQuery);
	
			
	var xidx=getIDX();
	var rfrm=boFormSubmit.referenceFrame.value;

	var xw= eval("parent.parent.frameElement.contentWindow.frm$"+xidx);

	if ( xw && xw.window)
	{
 			xw.setParametersExplorerQuery( xmlQuery.documentElement.xml , boFormSubmit.reference.value );
	}
}

qb.prototype.applyParam=function(option)
{
	//var xmlQuery="<parameters>";
	var xmlQuery = new ActiveXObject("MSXML.DOMDocument");
	var elemParams = xmlQuery.createElement("parameters");
	xmlQuery.appendChild(elemParams);

	var nr=0;
	var pos = 0;
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	//   	alert(this.querys[i].join+this.querys[i].attributeName+""+this.querys[i].condition+""+this.querys[i].value);
      nr++;
      if(this.querys[i].askParameters())
      {
      	var elemParam = xmlQuery.createElement("parameter");
				var elemValue = xmlQuery.createElement("value");
				elemValue.appendChild(xmlQuery.createTextNode(val));
				var elemNull = xmlQuery.createElement("nullIgnore");

				if(this.querys[i].nullIgnore)
				{
					elemNull.appendChild(xmlQuery.createTextNode("1"));
			  	//xmlQuery+="<parameter><value><![CDATA["+changeFromSpecialCode(this.querys[i].value)+"]]></value><nullIgnore>1</nullIgnore></parameter>";
				}
				else
				{
					elemNull.appendChild(xmlQuery.createTextNode("0"));
					//xmlQuery+="<parameter><value><![CDATA["+changeFromSpecialCode(this.querys[i].value)+"]]></value><nullIgnore>0</nullIgnore></parameter>";
				}
				elemParam.appendChild(elemNull);
				elemParam.appendChild(elemValue);
				elemParams.appendChild(elemParam);
      }
	}

//	xmlQuery+="</parameters>";	
//	xmlQuery = encodeURIComponent(xmlQuery);
	var xidx=getIDX();
	var rfrm=boFormSubmit.referenceFrame.value;

	var xw= eval("winmain().frm$"+xidx);

	if ( xw && xw.window)
	{
		xw2=getFrame( xw.window , rfrm ); 
			
		if(xw2)
		{
				xw2.setParametersQuery( xmlQuery.documentElement.xml , boFormSubmit.reference.value );	
		}
		else if (  rfrm.indexOf("chooseExportData") > 1 )
		{
				xw.setParametersQuery( xmlQuery.documentElement.xml , boFormSubmit.reference.value );
		}
	}
}
qb.prototype.applyToExplorer=function(option)
{
	var onlyObjects="";
	for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
	{
	 // alert( objectsToSearch.elements[i].id+objectsToSearch.elements[i].checked);
	  if ( objectsToSearch.elements[i].checked )
	  {
		onlyObjects+= objectsToSearch.elements[i].id+";";
	  }
	}
	if(selectedAllObject.checked==true) onlyObjects="";
	
	var xmlQuery="<filter object='"+boFormSubmit.object.value+"' onlyObjects='"+onlyObjects+"' queryBoui='"+boFormSubmit.queryBoui.value+"' >";
	var nr=0;
	var lastJoin = null;
	var _valid = true;
	var pCount = 0;
	if(this.avancada && "" != this.avancada)
	{
		xmlQuery += "<boql><![CDATA[" +this.avancada + "]]></boql>"
	}
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	   var l=this.querys[i].htm.firstChild.firstChild.checked;
	   if ( l )
	   {
			if ( this.querys[i].valid(lastJoin) )
			{
					nr++;
					xmlQuery+="<query><join>"+this.querys[i].join+"</join><attribute>"+this.querys[i].attributeName+"</attribute><condition>"+this.querys[i].condition+"</condition><value><![CDATA["+changeFromSpecialCode(this.querys[i].value)+"]]></value><question>" + (this.querys[i].etiqueta?this.querys[i].etiqueta:"")+ "</question><nullIgnore>"+(this.querys[i].nullIgnore?"0":"1")+"</nullIgnore><subquery>"+this.querys[i].subquery+"</subquery></query>";
			}
			else
			{
	        _valid = false;
				if(!option)alert("ATENÇÃO: A linha "+(i+1)+" está inválida" );
			}
      lastJoin = this.querys[i].join;
      pCount = pCount + this.querys[i].countPar(lastJoin, pCount);
      if(pCount < 0)
      {
        pCount = -999;//tem de dar erro
      }
	   }
	}

	if(pCount != 0)
	{
	  _valid = false;
	  if(!option)alert("Número de paranteses incorrecto." );
	}
	if(_valid)
	{
  		xmlQuery+="</filter>";
  		if ( (onlyObjects=="" && nr==0) || (option&&option=='remove' )) xmlQuery="<cleanFilter/>";
  	
  		var xidx=boFormSubmit.clientIDX.value;
  		var rfrm=boFormSubmit.referenceFrame.value;
  	 	
  		var xw;
      try
      {
         xw = eval("parent.frameElement.contentWindow.frm$"+xidx);
      }
      catch(e)
      {
         xw=parent.frames[xidx];
      }
			if(xw)
			{
				if(xw.boFormSubmit && xw.boFormSubmit.explorer_key)
				{
					xw.setUserExplorerQuery( xmlQuery , boFormSubmit.reference.value );
					boFormSubmit.toClose.value="y";
					boFormSubmit.submit();
				}
				else
				{
					for ( var i=0 ; i< xw.frames.length ; i++ )
					{
						if(xw.frames[i].boFormSubmit && xw.frames[i].boFormSubmit.explorer_key)
						{
							xw.frames[i].setUserExplorerQuery( xmlQuery , boFormSubmit.reference.value );
							boFormSubmit.toClose.value="y";
							boFormSubmit.submit();
						}
					}
				}
			}
//  		if ( xw && xw.window)
//  		{
//  			xw2=getFrame( xw.window , rfrm ); 
  			
//  			if(xw2) xw2.setUserExplorerQuery( xmlQuery , boFormSubmit.reference.value );
//  			else if (  rfrm.indexOf("chooseExportData") > 1 )
//  			{
//  				xw.setUserExplorerQuery( xmlQuery , boFormSubmit.reference.value );
//  			}
//  		}
	}
	
}
qb.prototype.apply=function(option)
{
	var onlyObjects="";
	for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
	{
	 // alert( objectsToSearch.elements[i].id+objectsToSearch.elements[i].checked);
	  if ( objectsToSearch.elements[i].checked )
	  {
		onlyObjects+= objectsToSearch.elements[i].id+";";
	  }
	}
	if(selectedAllObject.checked==true) onlyObjects="";
	
	var xmlQuery="<filter object='"+boFormSubmit.object.value+"' onlyObjects='"+onlyObjects+"' queryBoui='"+boFormSubmit.queryBoui.value+"' >";
	var nr=0;
	var lastJoin = null;
	var _valid = true;
	var pCount = 0;
	if(this.avancada && "" != this.avancada)
	{
		xmlQuery += "<boql><![CDATA[" +this.avancada + "]]></boql>"
	}
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	   var l=this.querys[i].htm.firstChild.firstChild.checked;
	   if ( l )
	   {
			if ( this.querys[i].valid(lastJoin) )
			{
					nr++;
					xmlQuery+="<query><join>"+this.querys[i].join+"</join><attribute>"+this.querys[i].attributeName+"</attribute><condition>"+this.querys[i].condition+"</condition><value><![CDATA["+changeFromSpecialCode(this.querys[i].value)+"]]></value><question>" + (this.querys[i].etiqueta?this.querys[i].etiqueta:"")+ "</question><nullIgnore>"+(this.querys[i].nullIgnore?"0":"1")+"</nullIgnore><subquery>"+this.querys[i].subquery+"</subquery></query>";
			}
			else
			{
	        _valid = false;
				if(!option)alert("ATENÇÃO: A linha "+(i+1)+" está inválida" );
			}
      lastJoin = this.querys[i].join;
      pCount = pCount + this.querys[i].countPar(lastJoin, pCount);
      if(pCount < 0)
      {
        pCount = -999;//tem de dar erro
      }
	   }
	}

	if(pCount != 0)
	{
	  _valid = false;
	  if(!option)alert("Número de paranteses incorrecto." );
	}
	if(_valid)
	{
  		xmlQuery+="</filter>";
  		if ( (onlyObjects=="" && nr==0) || (option&&option=='remove' )) xmlQuery="<cleanFilter/>";
  	
  		var xidx=boFormSubmit.clientIDX.value;
  		var rfrm=boFormSubmit.referenceFrame.value;
  	 	
  		var xw= eval("winmain().frm$"+xidx);
  		if ( xw && xw.window)
  		{
  			xw2=getFrame( xw.window , rfrm ); 
  			
  			if(xw2) xw2.setUserQuery( xmlQuery , boFormSubmit.reference.value );
  			else if (  rfrm.indexOf("chooseExportData") > 1 )
  			{
  				xw.setUserQuery( xmlQuery , boFormSubmit.reference.value );
  			}
  		}
	}
	
}

qb.prototype.applyList=function(option)
{
	var onlyObjects="";
	for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
	{
	 // alert( objectsToSearch.elements[i].id+objectsToSearch.elements[i].checked);
	  if ( objectsToSearch.elements[i].checked )
	  {
		onlyObjects+= objectsToSearch.elements[i].id+";";
	  }
	}
	if(selectedAllObject.checked==true) onlyObjects="";
	
	var xmlQuery="<filter object='"+boFormSubmit.object.value+"' onlyObjects='"+onlyObjects+"' queryBoui='"+boFormSubmit.queryBoui.value+"' >";
	var nr=0;
	var lastJoin = null;
	var _valid = true;
	var pCount = 0;
	if(this.avancada && "" != this.avancada)
	{
		xmlQuery += "<boql><![CDATA[" +this.avancada + "]]></boql>"
	}
	for ( var i=0 ; i< this.querys.length ; i++ )
	{
	   var l=this.querys[i].htm.firstChild.firstChild.checked;
	   if ( l )
	   {
			if ( this.querys[i].valid(lastJoin) )
			{
					nr++;
					xmlQuery+="<query><join>"+this.querys[i].join+"</join><attribute>"+this.querys[i].attributeName+"</attribute><condition>"+this.querys[i].condition+"</condition><value><![CDATA["+changeFromSpecialCode(this.querys[i].value)+"]]></value><question>" + (this.querys[i].etiqueta?this.querys[i].etiqueta:"")+ "</question><nullIgnore>"+(this.querys[i].nullIgnore?"0":"1")+"</nullIgnore><subquery>"+this.querys[i].subquery+"</subquery></query>";
			}
			else
			{
	        _valid = false;
				if(!option)alert("ATENÇÃO: A linha "+(i+1)+" está inválida" );
			}
      lastJoin = this.querys[i].join;
      pCount = pCount + this.querys[i].countPar(lastJoin, pCount);
      if(pCount < 0)
      {
        pCount = -999;//tem de dar erro
      }
	   }
	}

	if(pCount != 0)
	{
	  _valid = false;
	  if(!option)alert("Número de paranteses incorrecto." );
	}
	if(_valid)
	{
  		xmlQuery+="</filter>";
  		if ( (onlyObjects=="" && nr==0) || (option&&option=='remove' )) xmlQuery="<cleanFilter/>";
  	
  		var xidx=boFormSubmit.clientIDX.value;
  		var rfrm=boFormSubmit.referenceFrame.value;
  	 	
  		var xw= eval("winmain().frm$"+xidx);
  		if ( xw && xw.window)
  		{  			
  				xw.setUserQuery( xmlQuery , boFormSubmit.reference.value );
  				boFormSubmit.toClose.value="y";
					boFormSubmit.submit();
  		}
	}
	
}

function getFrame(wFrm,ref)
{
   wDoc=wFrm;
   var ret
   var wDocfrms=wDoc.document.getElementsByTagName('IFRAME');
   for(var z=0;  z < wDocfrms.length ; z++)
   {
      if (  wDocfrms[z].contentWindow.boFormSubmit && wDocfrms[z].contentWindow.boFormSubmit.boFormSubmitSecurity &&  wDocfrms[z].contentWindow.boFormSubmit.boFormSubmitSecurity.value==ref )
      {
            
            ret=wDocfrms[z].contentWindow;
            break;
      }
      else
      {
           ret= getFrame(wDocfrms[z].contentWindow,ref); 
           if ( ret ) break;
      }
   }
   return ret; 
}


//------------------------------------------------------------------------------------------------
function changeAttribute()
{    
    this.changed = 2;

	var o=event.srcElement;
	var attributeName=o.returnValue;
	var row=o.name.split("_")[1];
	var q=oQB.querys[row-1];
	q.changeAttribute( attributeName );
}

function changeJoin()
{
    this.changed = 2;

	var o=event.srcElement;
	var newJoin=o.returnValue;
	var row=o.name.split("_")[1];
	var q=oQB.querys[row-1];
    q.changeJoin( newJoin );
	
}
//------------------------------------------------------------------------------------------------
function changeCondition()
{
    this.changed = 2;

    var o=event.srcElement;
	var newCondition=o.returnValue;
	var row=o.name.split("_")[1];
	var q=oQB.querys[row-1];
	q.changeCondition( newCondition );
}
//------------------------------------------------------------------------------------------------
function changeValue()
{
    this.changed = 2;

//oQB.querys["+this.index-1+"].value=field_"+this.index+".returnValue

    var o=event.srcElement;
    var o1=null;
    try{
        o1=o.parentNode.parentNode.parentNode.parentNode.parentNode.previousSibling;
       }
    catch(e){}
    
    if ( o1 && o1.className=='duration') o=o1;
    
	if ( o.returnValue ) newValue=o.returnValue;
	else  newValue=o.value;
	
	var row=o.name.split("_")[1];
	var q=oQB.querys[row-1];
	q.changeValue( newValue );
}
/************************************/
wmodal=null;
wmodalc=null;

wmodalp=null;
wmodalpc=null;
function getLovParamDiv(query)
{
  var toRet = "";

  query.valueAux = query.value;
  //toRet += "<div style='display:none' id='lovParam'>";
  toRet +=    "<LINK href=\"templates/dialog/std/lookupdialog.css\" type=text/css rel=stylesheet>";
  toRet +=    "<SCRIPT language=javascript src=\"templates/dialog/std/lookupdialog.js\"></SCRIPT>";
              
  toRet +=    "<SCRIPT language=javascript>";
              
  toRet +=    	"var bChanged = false;";
              	
  toRet +=    "</SCRIPT>";
              
              
  toRet +=    "<TABLE style=\"WIDTH: 100%;\" cellSpacing=0 cellPadding=8>";
  toRet +=    "<TBODY>";

  toRet +=      "<TR>";
  toRet +=        "<TD class=main colSpan=2>";
  toRet +=          "<DIV id=divWarning>";
  toRet +=          "<TABLE id=tblQuestion height=\"100%\" cellSpacing=0 cellPadding=0 ";
  toRet +=            "width=\"100%\"><TBODY>";
  toRet +=            "<TR height=20>";
  toRet +=              "<TD>Etiqueta/Pergunta a efectuar:</TD>";
  toRet +=              "<TD></TD>";
  toRet +=            "</TR>";
  toRet +=            "<TR>";
  toRet +=              "<TD width=\"100%\">";
  if(!query.etiqueta || query.etiqueta.length == 0)
  {
    toRet +=                "<input class='text' value=\"\" id='questionValue' req=1 maxlength=\"3000\" name = 'questionValue' tabindex='1'>";
  }
  else
  {
    toRet +=                "<input class='text' value=\"" + query.etiqueta + "\" id='questionValue' req=1 maxlength=\"3000\" name = 'questionValue' tabindex='1'>";
  }
  toRet +=              "</TD>";
  toRet +=            "</TR>";
  toRet +=            "<TR height=20>";
  toRet +=              "<TD>Valor por omissão deste parâmetro:</TD>";
  toRet +=              "<TD></TD>";
  toRet +=            "</TR>";
  toRet +=            "<TR>";
  toRet +=              "<TD id='cParamVal' width=\"100%\">";
  query.buildCellAux(query);
  toRet +=                query.cellAux.innerHTML;
  toRet +=              "</TD>";
  toRet +=            "</TR>";
  toRet +=            "<TR>";
  toRet +=              "<TD width=\"100%\">";
	if(query.nullIgnore)
	{
		toRet +=            "<input value=\"ON\" style='border:0px' id=\"chknullIgnore\" type=\"checkbox\" onclick=\"marknullIgnore();\" checked>";
	}
	else
	{
		toRet +=            "<input value=\"OFF\" style='border:0px' id=\"chknullIgnore\" type=\"checkbox\" onclick=\"marknullIgnore();\">";
	}
	toRet += "Se valor não preenchido ignorar linha";
  toRet +=              "</TD>";
  toRet +=            "</TR>";
  toRet +=          "</TABLE>";
  toRet +=          "</DIV>";
  toRet +=        "</TD>";
  toRet +=     "</TR>";
  toRet +=      "<TR>";
  toRet +=        "<TD style=\"BORDER-TOP: #ffffff 0px solid\" align=right>&nbsp;</TD>";
  toRet +=        "<TD style=\"BORDER-TOP: #ffffff 0px solid\" align=right><BUTTON id=butBegin ";
  toRet +=          "onclick=DLG_applyParamchanges();>OK</BUTTON>&nbsp;<BUTTON id=cmdDialogCancel ";
  toRet +=          "onclick=DLG_cancelp();>Cancela</BUTTON></TD></TR></TBODY></TABLE>";

  //toRet +="</div>";
  return toRet;
}


function buildParam(index, resetPos)
{
	if ( index+""=="undefined") index=event.srcElement.indexQry;
	var ev=window.event;

	qry=oQB.querys[index-1];
  var div = document.getElementById("lovParam");

  div.innerHTML = getLovParamDiv(qry);

  if(div.innerHTML.indexOf('multi') != -1)
  {
    div.document.getElementById('cParamVal').callBackObject=qry;
  }

	var value=qry.value;

	var div2=document.createElement("div");
	wmodalpc=div2;
	div2.style.position='absolute';
	div2.style.top=20;
	div2.style.left=0;
	div2.style.posWidth=document.body.clientWidth;
	div2.style.posHeight=document.body.clientHeight;
	div2.style.zIndex=49;
	div2.style.filter="progid:DXImageTransform.Microsoft.Alpha( Opacity=10, Style=0)"
	wmodalp=div;
	div2.onclick=function y(){ wmodalp.style.visibility='hidden';window.setTimeout("wmodalp.style.visibility='visible'",50);  }
	document.body.appendChild(div2);
	
	div.className='doc_std';
	div.style.display='';
	div.style.zIndex=50;
	div.style.posWidth=400;
	div.style.posHeight=250;
  if(resetPos && qry.pos)
  {
    div.style.posTop=qry.pos;
  }
  else
  {
	  div.style.posTop=ev.y;
    qry.pos=ev.y;
  }
	div.style.posLeft=ev.x;	
  div.style.backgroundColor='#A6C6EC';
    
    setNavigationState();
    
	if ( div.offsetWidth+div.style.posLeft > document.body.clientWidth-20 )
	{
		div.style.posLeft= document.body.clientWidth-div.offsetWidth-20;
	}
  tblQuestion.qry=index;
	
}


/************************************/

function buildlov(index, param)
{
 
	if ( index+""=="undefined") index=event.srcElement.indexQry;
	var ev=window.event;
	var div=document.getElementById("lov");
	
	qry=oQB.querys[index-1];
	var value=qry.value;
	
	var nodeAttr=qry.attrXML.selectSingleNode( qry.attributeName )
	var lov=nodeAttr.selectSingleNode("lov");
	
	
	
	
	var div2=document.createElement("div");
	wmodalc=div2;
	div2.style.position='absolute';
	div2.style.top=0;
	div2.style.left=0;
	div2.style.posWidth=document.body.clientWidth;	
  div2.style.posHeight=document.body.clientHeight;
	div2.style.zIndex=49;
	div2.style.backgroundColor='#EEEEEE';
	div2.style.filter="progid:DXImageTransform.Microsoft.Alpha( Opacity=10, Style=0)"
	wmodal=div;
	div2.onclick=function y(){ wmodal.style.visibility='hidden';window.setTimeout("wmodal.style.visibility='visible'",50);  }
	document.body.appendChild(div2);
	
	div.className='doc_std';
	div.style.display='';
	div.style.zIndex=100;
	div.style.posWidth=400;
	

  if(param)
  {
    div.style.posTop=0;
  	div.style.posLeft=0;
    div.style.posHeight=100;  	
  }
  else
  {    
	  div.style.posTop=ev.y;
  	div.style.posLeft=ev.x;
    div.style.posHeight=250;
  }
	
    while (tblResults.rows.length>0)
    {
       tblResults.deleteRow(0);
    }	
    while (tblSelected.rows.length>0)
    {
       tblSelected.deleteRow(0);
    }	
    
    var values=value.split(";");
    
	if ( lov )
	{
		for ( var i=0; i<lov.childNodes.length; i++ )
		{
			var l=lov.childNodes[i].selectSingleNode("description").text;
			var v=lov.childNodes[i].selectSingleNode("value").text;
			if ( value.indexOf( v+";") == -1)
			{
				var oTR=tblResults.insertRow();
			}
			else
			{
				var oTR=tblSelected.insertRow();
				
			}
			oTR.key=v;
			tblSelected.qry=index;
			var oC=oTR.insertCell();
			oC.className="listItem";
			oC.style.align="absmiddle";
			oC.innerHTML="<NOBR>&nbsp;"+l+"</NOBR>";
			//oTR.innerHTML="<TD class=listItem align=absmiddle><NOBR>&nbsp;"+l+"</NOBR></TD>";
			
		}
		
	}
	
	
    
    setNavigationState();
    
	if ( div.offsetWidth+div.style.posLeft > document.body.clientWidth-20 )
	{
		div.style.posLeft= document.body.clientWidth-div.offsetWidth-20;
	}

	
}


function showTreeAttr( index )
{    
    if ( index+""=="undefined") index=event.srcElement.indexQry;
	ngtbody=winmain().ndl[getIDX()].htm;    
    qry=oQB.querys[index-1];
	var value=qry.value;
    if(qry.cellAttribute.disabled) return;
	
	
	
	var nodeAttr=qry.attrXML.selectSingleNode( qry.attributeName )
	
    
    qry.cellAttribute.firstChild.rows[0].cells[0].firstChild.runtimeStyle.border="1px solid red";

	var url="__chooseAttribute.jsp?object="+objectName+"&selectedAttribute="+qry.attributeName+"&onlyObjects="+getonlyObjects();
	var topper=event.screenY+15;  //winmain().screenTop+ngtbody.style.pixelTop+30;
	var lefter=event.screenX-290;  //winmain().screenLeft+ngtbody.style.pixelLeft+400
	
	window.showModalDialog(url,qry,"dialogHeight: 500px; dialogWidth: 290px; dialogTop: "+(topper)+"; dialogLeft: "+( lefter)+"; edge: raised;center: No;  help: No; scroll: yes; resizable: yes; status: no;");

	
	
	

}

function DLG_applychanges()
{
	//if (bChanged)
//	{
		var qry=oQB.querys[tblSelected.qry-1];
		if (tblSelected.rows.length==0)
		{
		   qry.value="";
		}
		else
		{
		    var selCols="";
		    for ( i=0; i< tblSelected.rows.length; i++){
		      selCols+=tblSelected.rows[i].key+";";
		    }
			qry.value=selCols;
      var fieldId = 'param_field_' + qry.index;
      if(this.document.getElementById(fieldId) != null)
      {
		    this.document.getElementById(fieldId).value=qry.getCellValue(selCols);
      }
		}
    try
    {
		  qry.buildCellValue();
    }catch(e){}
    
    try
    {
      qry.buildCellParam();    
    }catch(e){}
    try
    {
      buildHTMLParams();
    }catch(e){}
    
    try
    {
      buildParam(qry.index, true);
    }catch(e){}
//	}
    DLG_cancel()
	
}
function marknullIgnore(){
    var qry=oQB.querys[tblQuestion.qry-1];
    qry.nullIgnore = !qry.nullIgnore;
}
function DLG_applyParamchanges()
{
		var qry=oQB.querys[tblQuestion.qry-1];
    qry.etiqueta = questionValue.value;

    qry.buildCellValue();
    qry.cellAux.innerHTML='&nbsp;';
    qry.buildCellParam();    
    DLG_cancelp();
}

function DLG_cancel()
{
	
	document.body.removeChild(wmodalc);
	if(wmodal)
	{
		wmodal.style.display='none';
	}
	if(wmodalp)
	{
		wmodalp.style.display='none';
	}
	wmodalc=null;
	
}
function DLG_cancelp()
{
	
	document.body.removeChild(wmodalpc);
	if(wmodal)
	{
		wmodal.style.display='none';
	}
	if(wmodalp)
	{
		wmodalp.style.display='none';
	}
	wmodalpc=null;
	
}
function excludeParam()
{
   if (showingParameters.value == 'true')
   {
        tableParamToExclude.style.display='none';
        showingParameters.value == 'false';
   }
   else
   {
        tableToExclude.style.display='';
        showingParameters.value == 'true';
   }
}
function changeValueAux(newValue, index)
{
  if(newValue)
  {
    try
    {
      qry.value = newValue      
    }catch(e)    
    {
      this.oQB.querys[index-1].value = newValue;
    }
  }
	else
	{
		try
    {
      qry.value = "";
    }catch(e)    
    {
      this.oQB.querys[index-1].value = "";
    }
	}
}
function changeExclude()
{
   if (selectedAllObject.checked )
   {
       if(objectsToSearch.elements.length > 0){
            for ( var i=0 ; i< objectsToSearch.elements.length;i++ )
            {
              //objectsToSearch.elements[i].checked=false;
            }
        }
        sayExclude.style.display='none';
        tableToExclude.style.display='none';
   }
   else
   {
        sayExclude.style.display='';
        tableToExclude.style.display='';
   }
}
function changeFromSpecialCode(str)
{
	//carcteres especiais
	if(str)
	{						
		str = str.replace(/&#39/g, '\'');
		str = str.replace(/&#34/g, '"');
		str = str.replace(/&#60/g, '\\');
		str = str.replace(/&#62/g, '/');
	}
	return str;
}

function changeToSpecialCode(str)
{
	if(str)
	{
		//caracteres especiais
		str = str.replace(/\'/g, '&#39');
    str = str.replace(/\"/g, '&#34');
    str = str.replace(/\</g, '&#60');
    str = str.replace(/\>/g, '&#62');
	}
	return str;
}