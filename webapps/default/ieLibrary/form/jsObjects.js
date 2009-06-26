//<SCRIPT>
//----------------------------------------------------------------------------------
function createFieldCombo(
			Value,
			Name,
			id,
			tabIndex,
			displayValues,
			internalValues,
			allowValueEdit,
			isDisabled,
			onChange
			 ){
	var toRet="";
	toRet="<SPAN class=selectBox ";
	toRet+=" name= '";
	toRet+= Name;
	toRet+="' id = '";
	toRet+=id;
	toRet+="'";
	if ( isDisabled )
	{
		toRet+=" disabled ";
	}  
	if ( onChange )
	{
	
		toRet+=" changeHandler='";
		toRet+=onChange;
		toRet+="'";
	}
	
	toRet+=" tabbingIndex='";
	toRet+=tabIndex;
	toRet+="'";
	if ( allowValueEdit )
	{
		toRet+=" allowValueEdit='true' "
	}
	toRet+=" value='"+Value+"'";
	toRet+=" >";
	
	toRet+="<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>";
	for (var i=0 ; i < displayValues.length ; i++ ){
       toRet+="<TR><TD val='";
       toRet+=internalValues[i];
       toRet+="'>";
       toRet+=displayValues[i];
       toRet+="<TD></TR>"; 
    }
    toRet+="</TBODY></TABLE></SPAN>";
	return toRet;
}

//----------------------------------------------------------------------------------
function createFieldLookup(
        bouiObjParent,
        parentObjectName,
        parentAttributeName,
        Value,
        Name,
        id,
        listOfValidObjects,
        objectName,
        objectLabel,
        cardID,
        docID,
        lookupStyle, //single or multi
        tabIndex,
        isDisabled ,
        isVisible ,
        options
        )
        {
        
		var toRet=[];
		var i=0;
		
        toRet[i++]="<table id='";
        toRet[i++]=id;
        toRet[i++]="' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>";
        toRet[i++]="<div class='lu ro lui' ";
        toRet[i++]="valido='";
        toRet[i++]=listOfValidObjects;
        toRet[i++]="' ><span class='lui' onclick=\"";
        toRet[i++]="winmain().openDoc('medium','";
        
        
        toRet[i++]=objectName.toLowerCase();
        toRet[i++]="','edit','method=edit&boui=";
        toRet[i++]=Value;
        toRet[i++]=("&actRenderObj=");
        toRet[i++]=bouiObjParent;
        toRet[i++]=("&actRenderAttribute=");
        toRet[i++]= parentAttributeName ;
        toRet[i++]=("&actIdxClient='+getIDX()+'");
        
   
        toRet[i++]="')\"";

        toRet[i++]=" boui='";
        toRet[i++]=Value;
        toRet[i++]="' object='";
        toRet[i++]=objectName;
        toRet[i++]="'>";

        
        
        if( Value >0){
        
             
             toRet[i++]="<img  class='lui' title='";
             toRet[i++]="Imagem representativa do objecto ";
             toRet[i++]= objectLabel;
             toRet[i++]="' src='";
   
             toRet[i++]="resources/"+objectName+"/ico16.gif";
             toRet[i++]="'";
             
             if ( !isVisible ) {
                toRet[i++]=" style='display:none' ";
             }
             toRet[i++]=" object='";
             toRet[i++]=objectName;
             toRet[i++]="'";
             
             toRet[i++]=" boui='";
             toRet[i++]= Value;
             toRet[i++]="'";
             
             toRet[i++]=(" width='16' height='16'/>");

             toRet[i++]= cardID;  
   
             
             toRet[i++]="</span>";
             
        }
         
        
		toRet[i++]="</div>";
        toRet[i++]="</td>";
        toRet[i++]="<td style='TEXT-ALIGN: right' width='25'><img class='lu' id style='CURSOR: default' tabIndex='";
        toRet[i++]=tabIndex;
        
        if ( isDisabled ) {
                toRet[i++]="' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ";
        }
        else {
        
           toRet[i++]=("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='"+lookupStyle+"' ");
            if ( !isVisible ) {
                toRet[i++]=" style='display:none' ";
            }
        }
             
        toRet[i++]=" shownew='";
        toRet[i++]="1'";
        toRet[i++]=" parentBoui='";
        toRet[i++]=bouiObjParent;
        toRet[i++]="' parentObj='";
        toRet[i++]=parentObjectName;
        toRet[i++]="' options='";
        toRet[i++]=options;
        toRet[i++]="' parentAttribute='";
        toRet[i++]= parentAttributeName ;
        toRet[i++]="' object='";
        toRet[i++]=objectName;
        toRet[i++]="'  docid='";
        toRet[i++]=docID;
        toRet[i++]="' width='21' height='19'><input type='hidden' value='";
        toRet[i++]=Value;
        toRet[i++]="' name='";
        toRet[i++]=(Name);
        toRet[i++]="' object='";
        toRet[i++]=objectName;
        toRet[i++]=("' req='");
        
        toRet[i++]=0;
        toRet[i++]="' boType='lu'>";
        toRet[i++]="</td></tr></tbody></table>";
        return toRet.join("");
        
    }
    

//----------------------------------
//----------------------------------------------------------------------------------


function createDetachFieldLookup(
        Value,
        Name,
        id,
        listOfValidObjects,
        objectName,
        objectLabel,
        docID,
        lookupStyle, //single or multi
        tabIndex,
        isDisabled ,
        isVisible,
        onChange,
        filter
        )
        {
        
		var toRet=[];
		var i=0;
		
		//if(Value+""=="null")Value="";
        toRet[i++]="<table id='";
        toRet[i++]=id;
        toRet[i++]="' style='TABLE-LAYOUT: fixed' cellSpacing='0' cellPadding='0' width='100%'><tbody><tr><td>";
        toRet[i++]="<div class='lu ro lui' ";
        if ( onChange )
        {
			toRet[i++]="onChange='"
			toRet[i++]=onChange;
			toRet[i++]="'";
        }
        if ( filter )
        {
			toRet[i++]=" lookupQuery='"
			toRet[i++]=filter;
			toRet[i++]="'";
        }
        toRet[i++]=" valido='";
        toRet[i++]=listOfValidObjects;
        toRet[i++]="' detachValues='";
        toRet[i++]=Value;
        toRet[i++]="' ><span></span>";
        
        
        toRet[i++]="</div>";
        toRet[i++]="</td>";
        toRet[i++]="<td style='TEXT-ALIGN: right' width='25'><img class='lu' id style='CURSOR: default' tabIndex='";
        toRet[i++]=tabIndex;
        
        if ( isDisabled ) {
                toRet[i++]="' disabled src='templates/form/std/btn_dis_lookup.gif' lookupstyle='single' ";
        }
        else {
        
           toRet[i++]=("' src='templates/form/std/btn_off_lookup.gif' lookupstyle='"+lookupStyle+"' ");
            if ( !isVisible ) {
                toRet[i++]=" style='display:none' ";
            }
        }
             
        toRet[i++]=" shownew='";
        toRet[i++]="1'";
        toRet[i++]=" lookupDetachField='";
        toRet[i++]=id;
        
        toRet[i++]="' validObjects='";
        toRet[i++]=listOfValidObjects;
        toRet[i++]="' object='";
        toRet[i++]=objectName;
        toRet[i++]="'  docid='";
        toRet[i++]=docID;
        toRet[i++]="'";
        if ( filter )
        {
			toRet[i++]=" lookupQuery='"
			toRet[i++]=filter;
			toRet[i++]="' ";
        }
        toRet[i++]=" width='21' height='19'>"
        
        toRet[i++]="<input type='hidden' value='";
        toRet[i++]=Value;
        toRet[i++]="' name='";
        toRet[i++]=(Name);
        toRet[i++]="' object='";
        toRet[i++]=objectName;
        toRet[i++]=("' req='");
        toRet[i++]=0;
        toRet[i++]="' boType='lu'>";
        
        toRet[i++]="</td></tr></tbody></table>";
        return toRet.join("");
        
    }
    

//---------------------------------------

 function createFieldDate(
            Value,
            Name,
            id,
            tabIndex,
            isDisabled ,
            isVisible ,
            onChange ,
            isRequired,
            isRecommend
            )

            {
			var toRet=[];
			var i=0;
		
          toRet[i++]="<TABLE style='TABLE-LAYOUT: fixed' cellSpacing=0 cellPadding=0 ";
          toRet[i++]="width='100%'>";
          toRet[i++]="<COLGROUP>";

          toRet[i++]="<COL>";
          toRet[i++]="<COL width=40>";
          
          
          toRet[i++]="<TBODY>";
          toRet[i++]="<TR>";

          toRet[i++]="<TD><INPUT class=dtm maxLength=10";
        
          if ( onChange ) {
             toRet[i++]=" onreturnvaluechange='";
             toRet[i++]=onChange
             toRet[i++]="'";
          }  
          
          if ( isDisabled ) {
             toRet[i++]=" disabled ";
          }
          
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }

           
          toRet[i++]=" tabindex = '";
          toRet[i++]=tabIndex;
          toRet[i++]="' name = '";
          toRet[i++]=Name;
          toRet[i++]="' id = '";
          toRet[i++]=id;
          toRet[i++]="' boType='dtm' returnValue='";
          toRet[i++]=Value;
          toRet[i++]="' value='";
          toRet[i++]=Value;
          toRet[i++]="'/></TD>";
          toRet[i++]="<TD style='PADDING-LEFT: 4px'><IMG class=dtm ";
          if ( isDisabled ) {
             toRet[i++]=" disabled ";
          }
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }
          toRet[i++]=" src='templates/form/std/btn_off_Cal.gif' />";
          
          toRet[i++]="</TD></TR></TBODY></TABLE>";
          
          return toRet.join("");
    }
    
    //------------------------------------------
    function createFieldDateTime(
            Value,
            Name,
            id,
            tabIndex,
            allowValueEdit,
            isDisabled ,
            isVisible ,
            onChange,
            isRequired,
            isRecommend
            )
            {
			
			var toRet=[];
			var i=0;
			
          toRet[i++]="<TABLE style='TABLE-LAYOUT: fixed' cellSpacing=0 cellPadding=0 ";
          toRet[i++]="width='100%'>";
          toRet[i++]="<COLGROUP/>";
        
          toRet[i++]="<COL ='100%'/>";
          toRet[i++]="<COL width=40/>";
          toRet[i++]="<COL width=100/>";
          toRet[i++]="<TBODY>";
          toRet[i++]="<TR>";

          toRet[i++]="<TD><INPUT class=dtm maxLength=10 ";
          toRet[i++]=" tabindex='";
          toRet[i++]=tabIndex;
          toRet[i++]="' ";
          if ( onChange ) {
             toRet[i++]=" onreturnvaluechange='";
             toRet[i++]=onChange ;
             toRet[i++]="'";
          }  
          
          if ( isDisabled ) {
             toRet[i++]=(" disabled ");
          }
          
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }
          
          toRet[i++]=" name = '";
          toRet[i++]=Name;
          toRet[i++]="' id = '";
          toRet[i++]=id;
          toRet[i++]="' value='";
          if(Value.length>10)   toRet[i++]=Value.substring(0,10);
          else toRet[i++]=Value;
          toRet[i++]="' returnValue='";
          toRet[i++]=Value;
          toRet[i++]="' /></TD>";
          toRet[i++]="<TD style='PADDING-LEFT: 4px'><IMG class=dtm id='id";
          toRet[i++]=Name;
          toRet[i++]="'";
          if ( isDisabled ) {
             toRet[i++]=" disabled ";
          }
          
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }
          
          toRet[i++]=" src='templates/form/std/btn_off_Cal.gif'></TD>";
          toRet[i++]="<TD style='PADDING-LEFT: 4px'><DIV class=timeedit timeVisible='True' TimeFormat='2'><SPAN class=selectBox "; 
          toRet[i++]=" value='";
          var arr=Value.split("T");
          if( arr.length >1)toRet[i++]=arr[1].substr(0,5);
          else toRet[i++]=" ";
          toRet[i++]="' name = '_ignore_";
          toRet[i++]=Name;
          toRet[i++]="' ";
          
          if ( isDisabled ) {
             toRet[i++]=" disabled ";
          }
          
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }
          
          toRet[i++]=" id = 'idtime";
          toRet[i++]=Name;
          toRet[i++]="' tabbingIndex=";
          toRet[i++]=tabIndex;
          if( allowValueEdit) toRet[i++]=" allowValueEdit='true' ";
          //else toRet[i++]=(" allowValueEdit='false' ");

          toRet[i++]=" setdisabled='1' >";
          toRet[i++]="<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2>";
          toRet[i++]="<TBODY>";
          
          var x=new Date();
          var h=x.getHours();
          var m=x.getMinutes();
                    
          toRet[i++]="<TR><TD val='"+h+":"+m+"'>"+h+":"+m+"</TD></TR>";
          for ( var j=0;j<24;j++)
            {
				toRet[i++]="<TR><TD val='"+j+":00'>"+j+":00</TD></TR>";
				toRet[i++]="<TR><TD val='"+j+":30'>"+j+":30</TD></TR>";
            }
          //toRet[i++]="<TR><TD val='11:30'>11:30 ,11:30 </TD></TR>";
          
          toRet[i++]="</TBODY></TABLE></SPAN></DIV></TD></TR></TBODY></TABLE>";
          //alert(toRet.join(""));
          return toRet.join("")
                             
    }
    
 
  function createFieldDuration2(
        Value ,
        Name ,
        id ,
        tabIndex ,
        comboValues ,
        allowValueEdit ,  
        isDisabled ,
        isVisible ,
        onChange ,
        isRequired,
        isRecommend,
        clock
        )
        
        {
        
        var toRet=[];
		var i=0;
		
        if ( clock ) 
        {
            toRet[i++]="<table style='width:100%' cellpadding='0' cellspacing='0'><tr><td>";
        }
        toRet[i++]="<INPUT class=duration type=hidden name='";
        toRet[i++]=Name;
        toRet[i++]="' id ='";
        toRet[i++]=id;
        toRet[i++]="' returnValue='";
        toRet[i++]=Value;
        toRet[i++]="'";
        toRet[i++]=" tabIndex='";
        toRet[i++]=tabIndex;
        toRet[i++]="'>";
        toRet[i++]="<SPAN class='selectBox' name='";
        toRet[i++]="_ignore_sControl";
        toRet[i++]=Name;
        toRet[i++]="'";
    //    toRet[i++]=" id='s"+id+"' ";
        if ( isDisabled ){
            toRet[i++]=" disabled  ";
        }
        if ( onChange ) {
            toRet[i++]=" changeHandler='";
            toRet[i++]= onChange;
            toRet[i++]="'";
        }
        if ( !isVisible ) {
            toRet[i++]=" style='display:none' ";
        }
        
        toRet[i++]=" tabbingIndex='";
        toRet[i++]=tabIndex;
        toRet[i++]="' value='";
        toRet[i++]=formatDuration(Value);
        
        if(allowValueEdit){
            toRet[i++]="' allowValueEdit='true' >";
        }
        else
        {
			toRet[i++]="' allowValueEdit='false' >";
        }
        
        toRet[i++]="<TABLE style='DISPLAY: none' cellSpacing=0 cellPadding=2><TBODY>";
    
        for (var j = 0; j < comboValues.length ; j++)  {
            toRet[i++]="<TR><TD val='";
            toRet[i++]= comboValues[j];
            toRet[i++]="'>";
            toRet[i++]= comboValues[j];
            toRet[i++]="</TD></TR>";
        }
        toRet[i++]="</TBODY></TABLE></SPAN>";
        
        if ( clock )
        {
        
            toRet[i++]="</td><td width=25 align=right><IMG class='clock' ";
            toRet[i++]=" id='clock_";
            toRet[i++]=id;
            toRet[i++]="' relatedID='";
            toRet[i++]=id;
            if ( isDisabled )   toRet[i++]="' disable=true src='resources/clockrun.gif' width=21 height=19 /></td></td></tr></table>";
            else toRet[i++]="' src='resources/clockrun.gif' width=21 height=19 /></td></td></tr></table>";
         
        }
        
      return toRet.join("");
        
    }


 
    function createFieldDuration(
        Value,
        Name,
        id,
        tabIndex,
        allowValueEdit,
        isDisabled ,
        isVisible ,
        onChange ,
        isRequired,
        isRecommend,
        clock
        )
        {
        var combo=[
        "1 minuto",
        "5 minutos",
        "15 minutos",
        "30 minutos",
        "45 minutos",
        "1 hora",
        "1,5 horas",
        "2 horas",
        "3 horas",
        "3,5 horas",
        "4 horas",
        "4,5 horas",
        "5 horas",
        "5,5 horas",
        "6 horas",
        "6,5 horas",
        "7 horas",
        "7,5 horas",
        "8 horas",
        "1 dia",
        "2 dias",
        "3 dias"        
        ]; 
        
        return createFieldDuration2(Value,Name,id,tabIndex,combo,allowValueEdit,isDisabled,isVisible,onChange,isRequired,isRecommend,clock);
    }
    
    
    
	function createFieldText(
        Value,
        Name,
        id,
        tabIndex,
        isDisabled ,
        isVisible ,
        onChange,
        isRequired,
        isRecommend,
        charLen
        ){
        
        var i=0;
        var toRet=[];
        
        if ( charLen < 4000 ) 
        {
               toRet[i++]="<input class='text' value='";
               toRet[i++]=Value;
               toRet[i++]="' id='";
               toRet[i++]=id ;
               toRet[i++]="'";
               if ( isRequired )
               {
                   toRet[i++]=" req=1 ";
               }
               if ( isRecommend )
               {
                   toRet[i++]=" rcm=1 ";
               }
               if ( isDisabled ) {
                   toRet[i++]=" disabled ";
               }
               if ( !isVisible ) {
                   toRet[i++]=" style='display:none' ";
               }
               
               
               if ( onChange){
                   toRet[i++]=" onchange='";
                   toRet[i++]=onChange;
                   toRet[i++]="'";
               }
               toRet[i++]=" name = '";
               toRet[i++]=Name;
               toRet[i++]="' tabindex='"+tabIndex+"'>";     
        }
        else
        {
               
               toRet[i++]="<textarea style='height=100%' maxlength='4000' class='text' ";
               toRet[i++]="' id='";
               toRet[i++]=id;
               toRet[i++]="'";
               if ( isRequired )
               {
                   toRet[i++]=" req=1 ";
               }
               if ( isRecommend )
               {
                   toRet[i++]=" rcm=1 ";
               }
               if ( isDisabled ) {
                   toRet[i++]=" disabled ";
               }
               if ( !isVisible ) {
                   toRet[i++]=" style='display:none' ";
               }
               
               
               if ( onChange){
                   toRet[i++]=" onchange='";
                   toRet[i++]=onChange;
                   toRet[i++]="'";
               }
               toRet[i++]=" name = '";
               toRet[i++]=Name;
               toRet[i++]="' tabindex='"+tabIndex+"'/>";
               toRet[i++]=Value;
               toRet[i++]="</textarea>";
        }
       
       return toRet.join("");

 
   
                             
    }


  function createFieldNumber(
            Value,
            Name,
            id,
            tabIndex,
            type,
            decimals,
            grouping,
            max,
            min,
            isDisabled ,
            isVisible ,
            onChange  ,
            isRequired,
            isRecommend
            ){
            var i=0;
            var toRet=[];
          toRet[i++]="<input style='width:100%' class='num'  "; 
          toRet[i++]=" name = '";
          toRet[i++]=Name;
          toRet[i++]="' id = '";
          toRet[i++]=id;
          toRet[i++]="' tabIndex='";
          toRet[i++]=tabIndex;
          toRet[i++]="'";
          
           if ( isDisabled ){
            toRet[i++]=" disabled  ";
          }
          if ( onChange ) {
                toRet[i++]=" onChange='this.Parse();";
                toRet[i++]= onChange ;
                toRet[i++]="'";
          }
          
          if ( !isVisible ) {
             toRet[i++]=" style='display:none' ";
          }
          
          toRet[i++]=" value='";
          toRet[i++]=Value;
          toRet[i++]="' returnValue='";
          toRet[i++]=Value;
          toRet[i++]="' ";
          toRet[i++]=" dt='";
          toRet[i++]=type;
          toRet[i++]="' minAcc='";
          toRet[i++]=decimals;
          toRet[i++]="' acc='";
          toRet[i++]=decimals;
          
          if (grouping) toRet[i++]="' grp='true' ";
          else toRet[i++]="' grp='false' ";
          toRet[i++]=" max='";
          toRet[i++]=max;
          toRet[i++]="' min='";
          toRet[i++]=min;
          toRet[i++]="' >";
          
        return toRet.join("");

    }



