//<SCRIPT>
function LookupObjects( lookupQuery , lookupStyle, lookupObject, parentObj , parentBoui, parentAttribute , showNew, docid , look_action ,otherpar,fromSection,toSearch)
{
	try
	{
		FCK.LinkedField.whereTo = FCK.EditorDocument.selection.createRange();
	}
	catch(E){};
		
	var url;
	
	url="lookup";
	url +=lookupStyle;
	url +=".jsp";
	url +="?look_object=" + lookupObject;
	url +="&showNew=" + showNew;
	
	if(getDocId()!=null)
		url +="&docid="+getDocId();
	else if(docid !=null) 
		url +="&docid="+docid;
		
	if(fromSection)
		url +="&fromSection=y";
	
	url +="&clientIDX="+getIDX();
	url +="&look_parentObj="+parentObj;
	url +="&look_query="+lookupQuery;
	url +="&look_parentBoui="+parentBoui;
	
	if(otherpar)
		url+="&"+otherpar;
	if(toSearch)
    url+="&searchString="+encodeURIComponent(toSearch.replace(/%/g,'_*_'));
	if(look_action)
		url +="&look_action="+look_action;
	
	url +="&look_parentAttribute="+parentAttribute;
	
	if(options)
		url+="&options="+options;
	
	winmain().openDoc("tall",lookupObject,"","","lookup",url);
}

function LookupBridge( searchSourceQuery , searchDestinationQuery, lookupStyle, lookupSourceObj, lookupDestinationObj, 
    sourceBouiObj, destinationBouiObj, sourceAttribute, destinationAttribute,
    canSelectRows, bolist_query, renderOnlyCardID, docid , showNew, otherpar,  look_action )
{	    
	var url;

	url = "__lookupbridge";
	url += lookupStyle;
	url += ".jsp";
	url += "?look_SourceObj=" + lookupSourceObj;
	url += "&look_DestinationObj=" + lookupDestinationObj;
	url += "&showNew=" + showNew;
	url += "&docid="+getDocId();
	url += "&clientIDX="+getIDX(); //w.windowIDX;	
	url += "&look_sourceQuery="+searchSourceQuery;
	url += "&look_destinationQuery="+searchDestinationQuery;
	url += "&look_sourceBouiObj="+sourceBouiObj;
	url += "&look_destinationBouiObj="+destinationBouiObj;
	url += "&renderOnlyCardID="+renderOnlyCardID;
	url += "&canSelectRows="+canSelectRows;
	url += "&ctxParent="+destinationBouiObj;
	url += "&ctxParentIdx="+getDocId();
	url += "&relatedClientIDX="+getIDX(); 
	
	if ( otherpar ) url+="&"+otherpar;	
	
	if( look_action ){
		url += "&look_action="+look_action;
	}
	
	url += "&look_sourceAttribute="+sourceAttribute;
	url += "&look_destinationAttribute="+destinationAttribute;
	winmain().openDoc("tall",lookupDestinationObj,"","","lookup",url);
}
function LookupObjectsDetachField( lookupQuery , lookupStyle, divObject, lookupObject, waitingAttribute , attributeValue, validObjects , showNew, docid , look_action , otherpar,toSearch)

{	
	var url;
	waitingAttribute=""+getIDX()+"-"+waitingAttribute+"-"+divObject.id;
	
	url = "__look";
	url += lookupStyle;
	url += "_detachAttribute.jsp";
	url += "?look_object=" + lookupObject;
	url += "&showNew=" + showNew;
	url += "&docid="+getDocId();
	url += "&validObjects="+validObjects;
	url += "&attributeValue="+attributeValue;
	url += "&clientIDX="+getIDX();  //w.windowIDX;
	url += "&waitingAttribute="+waitingAttribute;
	url += "&look_query="+lookupQuery;
	if (toSearch) url+="&searchString="+encodeURIComponent(toSearch.replace(/%/g,'_*_'));
	if ( otherpar ) url+="&"+otherpar;
	
	if( look_action ){
		url += "&look_action="+look_action;
	}
	winmain().createWDA( divObject , waitingAttribute );
	
	
	winmain().openDoc("tall",lookupObject,"","","lookup",url);
}


function LookupTemplates(lookupJSP, templateBoui , clsRelatedAttribute , clsTemplate  , attributeName , relatedBoui , showNew, docid)
{	
	var url;
//	var wIDX;
//	var w=window;
//  	while (w && !w.windowIDX ){
//	 w=w.parent;
//	}
	
	
	url  = lookupJSP;
	url += "?look_object=" + clsTemplate;
	url += "&docid="+getDocId();
	url += "&clientIDX="+getIDX();   //w.windowIDX;
	url += "&look_parentClass="+clsRelatedAttribute;
	url += "&look_templateBoui="+templateBoui;
	url += "&look_relatedBoui="+relatedBoui;
	url += "&look_attributeName="+attributeName;
	
	
	winmain().openDoc("tall","ebo_template","","","lookup",url);

}


function openAttributeMap(name,docid,template_BOUI){

	var url;
//	var wIDX;
//	var w=window;
//   	while (w && !w.windowIDX ){
//	 w=w.parent;
//	}
	
	url = "templateEditMappingAttribute.jsp";
	url += "?attributeName=" + name;
	url += "&docid="+getDocId();
	url += "&templateBOUI="+template_BOUI;
	url += "&clientIDX="+getIDX();
	
	
	winmain().openDoc("tall",'','','',"formula",url);

}