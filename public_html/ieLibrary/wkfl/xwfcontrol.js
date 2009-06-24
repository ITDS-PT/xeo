//<SCRIPT>
/*
function showAndHide(id)
{    
    var o = window.event.srcElement;          
    var src = o.src;    
    if(id.style.display == "none")
    {
        id.style.display="";      
        if(o.id == 'showAndHideImage')
        {
            o.src = src.replace('more.gif','minus.gif');
        }
    }
    else
    {
        id.style.display="none";
        if(o.id == 'showAndHideImage')
        {        
            o.src = src.replace('minus.gif','more.gif');
        }
    }    
}

function changeHeight(id)
{
    
    if(id.style.height == "")
    {
        id.style.height = "100px";
    }
    else
    {
        id.style.height = "";
    }    
    
}
*/
function setActionCode(code)
{    
  boFormSubmit.actionCode.value = code;    
}
/*
function setShowWorkFlowActivity(boui)
{
  boFormSubmit.showWorkFlowActivity.value = boui;
}*/
function setStateActivity(boui)
{
  boFormSubmit.stateActivityBoui.value = boui;
}
function setViewerType(type)
{                
    boFormSubmit.xwfViewerType.value = type;
    boForm.BindValues();
}
function setActivityChoiceValue()
{            
    var value;
    var elems = document.getElementsByName("xwfChoice");    
    for ( var i=0 ; i < elems.length && value == null; i++ )
    {
        if(elems[i].checked)
        {
            value = elems[i].id;  
        }
    }      
    if(value != null)
    {  
        parent.boFormSubmit.xwfActivityValue.value = value;
        parent.boForm.BindValues();
        //parent.savePressed(true);
    }
    else
    {
        alert("Tem de escolher uma opção antes de confirmar");
    }
}
function setActivityValue(value)
{        
    parent.boFormSubmit.xwfActivityValue.value = value;
    parent.boForm.BindValues();
    parent.savePressed(true);
}
function updateFrameById(frameId,parameter,code)
{       
    if(code == null)
    {
        code = 0;
    }
    var frame = document.getElementById(frameId);
    if(frame != null)
    {        
        var repalceSrc = frame.src;
        var reg;
        if(repalceSrc.indexOf(parameter+"=-") != -1)
        {
            reg = new RegExp("\\b"+parameter+"=-[0-9]*&", "g");
        }        
        else
        {
           reg = new RegExp("\\b"+parameter+"=[0-9]*&", "g");
        }
        var newSrc = repalceSrc.replace(reg , parameter + "=" + code + "&");
        frame.src = newSrc;
        return;
    }
}