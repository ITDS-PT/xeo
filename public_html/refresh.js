//<SCRIPT language='JAVASCRIPT'>
//UTILS
// tipos
//rad - check
//dtm - data\hora 
//text - texto
//num - número
//lui - lookup
function BindToWizzard(buttonName)
{
		xml = parent.document.boForm.BuildXml(false, false, false);
		wizardXml = '<wizard buttonPress="'+buttonName+'">';
		
    boFormSubmit.boFormSubmitXml.value=wizardXml + xml + '</wizard>';    
    boFormSubmit.boFormSubmitMode.value = 20;
    boFormSubmit.submit(); 
}
function BindToValidate(fieldName)
{
    var xml;
    var x = parent.document.getElementsByName(fieldName);
    
    if(x && x.length > 0)
    {        
        if(x[0].original != x[0].value && x[0].className == "dtm")
        {
            if(x[0].original != x[0].returnValue)
            {
                xml = parent.document.boForm.BuildXml(false, false, false);
                boFormSubmit.boFormSubmitXml.value=xml;    
                boFormSubmit.boFormSubmitMode.value = 11;
                parent.wait();
                winmain().ndl[getIDX()].formulaChanged = true;
                boFormSubmit.submit(); 
            }
        }
        else if(x[0].original != x[0].value)
        {
            xml = parent.document.boForm.BuildXml(false, false, false);
            boFormSubmit.boFormSubmitXml.value=xml;    
            boFormSubmit.boFormSubmitMode.value = 11;
            parent.wait();
            winmain().ndl[getIDX()].formulaChanged = true;
            boFormSubmit.submit();
        }
        else
        {
            if(fieldName.indexOf("inc_") == 0)
            {
                xml = parent.document.boForm.BuildXml(false, false, false);
                boFormSubmit.boFormSubmitXml.value=xml;    
                boFormSubmit.boFormSubmitMode.value = 11;
                parent.wait();
                winmain().ndl[getIDX()].formulaChanged = true;
                boFormSubmit.submit();
            }
        }
    }
    else
    {
        xml = parent.document.boForm.BuildXml(false, false, false);
        boFormSubmit.boFormSubmitXml.value=xml;    
        boFormSubmit.boFormSubmitMode.value = 11;
        parent.wait();
        winmain().ndl[getIDX()].formulaChanged = true;
        boFormSubmit.submit();
    }
}

function BindToClean(fieldName)
{
    var xml;
    var x = parent.document.getElementsByName(fieldName);
    
    xml = "<bo boui='" + boFormSubmit.BOUI.value + "'><" + fieldName+ " mode='add'></" + fieldName+ "></bo>";
    boFormSubmit.boFormSubmitXml.value=xml;
    boFormSubmit.boFormSubmitMode.value = 11;
    parent.wait();
    winmain().ndl[getIDX()].formulaChanged = true;
    boFormSubmit.submit();
}

function refreshValues()
{
    var xml;
    xml = parent.document.boForm.BuildXml(false, false, false);
    parent.wait();
    boFormSubmit.boFormSubmitXml.value=xml;    
    boFormSubmit.boFormSubmitMode.value = 11;
    boFormSubmit.submit(); 
}

function refreshParentValues()
{
    var windowToUpdate=getIDX();
    var w=winmain().ndl[windowToUpdate];

    if(w)
    {
        var ifrm=w.htm.getElementsByTagName('IFRAME');
        for(var i=0; i < ifrm.length ; i++)
        {
            if ( ifrm[i].id == 'frm$'+getIDX() )
            {
                var wDocfrms=ifrm[i].contentWindow.document.getElementsByTagName('IFRAME');
                for(var z=0;  z < wDocfrms.length ; z++)
                {
                    if (  wDocfrms[z].id == 'refreshframe' )
                    {
                        try
                        {
                            wDocfrms[z].contentWindow.refreshValues();
                        }
                        catch(e){}
                    }
                }
            }
        }
    }
}

function refreshDocument()
{
  var xml;
  xml=parent.document.boForm.BuildXml(false, false, false);
  parent.wait();
  parent.document.boFormSubmit.boFormSubmitXml.value=xml;
  parent.document.boFormSubmit.boFormSubmitMode.value=110;
  parent.document.boFormSubmit.submit();
}

function setVisible(fieldName, fieldValue)
{
   var obj = parent.document.getElementById(fieldName);
   if(obj != null)
   { 
        if(fieldValue == 'false')
            obj.style.display = 'none';
        else
            obj.style.display = '';
   }
   
   //label
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == fieldName )
        {
            if(fieldValue == 'false')
                xw[j].style.display = 'none';
            else
                xw[j].style.display = '';
        }
   }
}

function setRequired(fieldName, fieldValue)
{
   var obj = parent.document.getElementById(fieldName);
   if(obj != null)
   { 
        if(fieldValue == 'false')
            obj.req = '0';
        else
            obj.req = '1';
   }
   
   //label
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == fieldName )
        {
            if(fieldValue == 'false')
                xw[j].className = '';
            else
                xw[j].className = 'req';
        }
   }
}

function setRequiredLookup(labelName, fieldName, fieldValue)
{
   var obj = parent.document.getElementById(fieldName);
   if(obj != null)
   { 
        if(fieldValue == 'false')
            obj.req = '0';
        else
            obj.req = '1';
   }
   
   //label
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == labelName )
        {
            if(fieldValue == 'false')
                xw[j].className = '';
            else
                xw[j].className = 'req';
        }
   }
}

function setDisableLookup(labelName, fieldName, fieldValue)
{     
   var obj = parent.document.getElementById(fieldName);
   if(obj != null)
   { 
        if(fieldValue == 'false')
            obj.disabled = false;
        else
            obj.disabled = true;
   }
   
   //label
   var img;
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == labelName )
        {
            try{
              img = xw[j].parentElement.nextSibling.firstChild.rows[0].cells[1].firstChild;
              if(fieldValue == 'false')
              {
                  xw[j].disabled = false;   
                  img.disabled = false;      
                  img.src = img.src.replace('btn_dis_lookup','btn_on_lookup');   
                  img.src = img.src.replace('btn_off_lookup','btn_on_lookup');   
              }
              else
              {                
                  xw[j].disabled = true;
                  img.disabled = true;           
                  img.src = img.src.replace('btn_on_lookup','btn_off_lookup');    
              }
          }catch(e){}
        }
        else if(labelName.indexOf(xw[j].htmlFor) > -1)
        {
            var nStr = labelName.substring(xw[j].htmlFor.length);
            if(!isNaN(nStr))
            {
               if(fieldValue == 'false')
                   xw[j].disabled = false;
               else
                   xw[j].disabled = true;
            }
        } 
   }   
}
//----------------------BOOLEAN
function setValueBoolean(fieldName, fieldValue)
{
   //campo escondido
   var hiddenName = fieldName.substr(8);   
   var xw = parent.document.getElementsByName(hiddenName);
   if(xw && xw.length > 0)
   {
     xw[0].value = fieldValue;
   }
   
   xw = parent.document.getElementsByName(fieldName);
   if(xw && xw.length > 0)
   {
        for(var j=0; j< xw.length; j++)
        {
            xw[j].setValueByRefresh(fieldValue);
        }
   }
}
function setVisibleBoolean(fieldName, fieldNumber, fieldValue)
{
   var xw = parent.document.getElementsByName(fieldName);
   if(xw && xw.length > 0)
   {
        for(var j=0; j< xw.length; j++)
        {
            if(fieldValue == 'false')
                xw[j].style.display = 'none';
            else
                xw[j].style.display = '';
        }
   }

   //label's
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == fieldNumber )
        {
            if(fieldValue == 'false')
                xw[j].style.display = 'none';
            else
                xw[j].style.display = '';
        }
   }
}

function setRequiredBoolean(fieldName, fieldNumber, fieldValue)
{
   //campo escondido
   var hiddenName = fieldName.substr(8);   
   var xw = parent.document.getElementsByName(hiddenName);
   if(xw && xw.length > 0)
   {
       if(fieldValue == 'false')
           xw[0].req = '0';
       else
           xw[0].req = '1';
   }

   //label's
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == fieldNumber )
        {
            if(fieldValue == 'false')
                xw[j].className = '';
            else
                xw[j].className = 'req';
        }
   }
}
function setDisableBoolean(fieldName, fieldNumber, fieldValue)
{
   var xw = parent.document.getElementsByName(fieldName);
   if(xw && xw.length > 0)
   {
        for(var j=0; j< xw.length; j++)
        {
            if(fieldValue == 'false') 
            {
                xw[j].enable();
            }
            else
            {
                xw[j].disable();
            }
        }
   }

   //label's
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++){
        if ( xw[j].htmlFor == fieldNumber || xw[j].htmlFor == fieldNumber + '0' ||
            xw[j].htmlFor == fieldNumber + '1' )
        {
            if(fieldValue == 'false')
                xw[j].disabled = false;
            else
                xw[j].disabled = true;
        }
   }
}


function setDisable(fieldName, fieldValue)
{
   var obj = parent.document.getElementById(fieldName);
   if(obj != null)
   {
        if(obj.className == 'selectBox' || obj.className == 'dtm' ||
            obj.className == 'num' || obj.className=='duration')
        {            
            if(fieldValue == 'false') 
            {                
                obj.enable();
            }
            else
            {
                //alert("setting disable field: " + fieldName);
                obj.disable();                
            }
        }
        else
        {
            if(fieldValue == 'false')
            { 
                obj.disabled = false;
                obj.runtimeStyle.borderColor = "";
            }
            else
            {
                //alert("setting disable field: " + fieldName);
                obj.disabled = true;
                obj.runtimeStyle.borderColor = "#cccccc";
            }
        }
   }
   
   //label
   var xw;
   xw = parent.document.getElementsByTagName("label");
   for(var j=0; j< xw.length; j++)
   {
        if ( xw[j].htmlFor == fieldName )
        {
            if(fieldValue == 'false')
                xw[j].disabled = false;
            else
                xw[j].disabled = true;
        }
        else if(fieldName.indexOf(xw[j].htmlFor) > -1)
        {
            var nStr = fieldName.substring(xw[j].htmlFor.length);
            if(!isNaN(nStr))
            {
               if(fieldValue == 'false')
                   xw[j].disabled = false;
               else
                   xw[j].disabled = true;
            }
        }
   }
}


function setValue(fieldName, fieldValue)
{
        //alert('Valores ' + fieldName + ' ' + fieldValue);
        var obj = parent.document.getElementById(fieldName);

        if(obj != null)
        {
            if(obj.className == 'text')
            {
                //alert('setting text');                
                obj.value = fieldValue;
                obj.original = obj.value;
                //obj.fireEvent("onchange");
            }
            else if(obj.className == 'num')
            {
                //alert('setting num');
                obj.setValueByRefresh(fieldValue);
                //obj.fireEvent("onchange");
            }
            else if(obj.className == 'rad')
            {
                //alert('setting rad');
                obj.setValueByRefresh(fieldValue);
                
            }
            else if(obj.className == 'lui')
            {
                //alert('setting lui');
                obj.innerHTML = fieldValue;
                obj.original = obj.innerHTML;
                
            }
            else if(obj.className == 'dtm')
            {
                //alert(fieldName + ":" + fieldValue);
                //debugger;
                //alert('setting');
                
                obj.setValueByRefresh(fieldValue);
            }
            else if(obj.className.indexOf('selectBox') > -1)
            {
                //alert(fieldName + ":" + fieldValue);
                obj.setValueByRefresh(fieldValue);
            }
            else if(obj.type == "checkbox")
            {
               if(fieldValue == '1')
               {
                  obj.value = '1';
                  obj.checked = true;
               }
               else
               {
                  obj.value = '0';
                  obj.checked = false;
               }
            }
        }
        try
        {
         
         var nd=parent.winmain().ndl[ parent.getIDX() ];
         var fr=parent.frameElement.id;
         if ( nd.focusfields[fr] && fieldName.indexOf(nd.focusfields[fr].focusField)>-1 )
         {
           obj.select();
           obj.focus();
         }
        
        
        }
        catch(e){}
}


function setOnchangeSubmit(fieldName, htmlCode)
{        
    var line = 'document.getElementById("refreshframe").contentWindow.BindToValidate("'+ fieldName +'")';
    var obj = parent.document.getElementsByName(fieldName);
    //debugger;

        if(obj && obj.length > 0)
        {
            obj = obj[0];
            obj.outerHTML = htmlCode;
            
        }
}

function updateInFrames(nameH)
{
    var ifrm=parent.document.getElementsByTagName('IFRAME');
    var xw;
    //debugger;
    for(var i=0; i < ifrm.length ; i++)
    {
        if ( ifrm[i].id == nameH )
        {
            ifrm[i].contentWindow.reloadGrid();
        }
    }
 }

function updateLookupAttribute(nameH, frameCode){
    var xw;
    
        var obj = parent.document.getElementById('tblLook'+nameH);
        if(obj != null)
        {
            obj.outerHTML=frameCode;
        }
}

function refreshParentField(fieldName, htmlCode)
{
    var windowToUpdate=getIDX();
    var w=winmain().ndl[windowToUpdate];
    //debugger;
    if(w)
    {
        var ifrm=w.htm.getElementsByTagName('IFRAME');
        for(var i=0; i < ifrm.length ; i++)
        {
            if ( ifrm[i].id == 'frm$'+getIDX() )
            {
                var wDocfrms=ifrm[i].contentWindow.document.getElementsByTagName('IFRAME');
                for(var z=0;  z < wDocfrms.length ; z++)
                {
                    if (  wDocfrms[z].id == 'refreshframe' )
                    {
                        wDocfrms[z].contentWindow.setOnchangeSubmit(fieldName, htmlCode);
                    }
                }
            }
        }
    }    
}

function setMessageInfo(fieldName, messagesValue)
{
	var table = parent.document.getElementById('messageZone');
	var tam = table.rows.length;
	var lines=[];
	while ( table.rows.length>0 )	
	{
			 table.deleteRow(0);
	}

	var aux = fieldName;
	var pos = -1;
	var line;
	while(aux && aux != "")
	{
		pos = aux.indexOf("</td></tr>");
		if(pos == -1)
		{
			pos = aux.length;
		}
		line = aux.substr(0,pos);
		if(aux.indexOf("<tr><td colspan='4'>", pos) != -1)
		{
			aux = aux.substr(aux.indexOf("<tr><td colspan='4'>", pos) + ("<tr><td colspan='4'>".length));
		}
		else
		{
			aux = "";
		}
		lines[lines.length] = line
	}
	var oTR;
	var oC;
	for(var i = 0; i < lines.length; i++)
	{
		oTR=table.insertRow();
		oC=oTR.insertCell();
		oC.innerHTML=lines[i];
	}

  function setSectionShowing(sectionName, fieldValue)
  {
    var xw=parent.document.getElementById(sectionName);
    if(fieldValue=='false')
      xw.style.display='none';
    else
      xw.style.display='';
  }
  
  function setButtonDisabled(buttonName, fieldValue)
  {
    var xw=parent.document.getElementById(buttonName);
    if(fieldValue=='false')
      xw.disabled=false;
    else
      xw.disabled=true;
  }
}