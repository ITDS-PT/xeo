//<SCRIPT>

function elements_afterRenderCell( node , c )
{
	var p = node.getAttribute("pointer");
	if ( p!= null )
	{
		if( p=="done" )
		{
			//c.style.backgroundColor="#F5F0DB";
			var e=c.previousSibling;
			while ( e )
			{
			 e.style.backgroundColor="#AEC5E8";
			 e=e.previousSibling;
			}
			
			c.previousSibling.innerHTML = node.getAttribute("count");
		}
		else if ( p=="open" )
		{
			c.style.backgroundColor="#F5E095";
			var e=c.previousSibling;
			while ( e )
			{
			 e.style.backgroundColor="#F5E095";
			 e=e.previousSibling;
			}
			c.previousSibling.innerHTML = node.getAttribute("count");
			//c.previousSibling.innerHTML = "<img src='ieThemes/0/wkfl/pointer.gif' width=10 height=10/>"
		}
	} 
}

function elements_canRemove( node )
{
	if ( node.nodeName =="defParticipant" && ( node.getAttribute("name") =='starter' || node.getAttribute("name") =='workFlowAdministrator') )
	{
		return false;
	}
	return true;
}
function elements_showMessageCANNOTREMOVE( node )
{
	if ( node.nodeName =="defParticipant" && ( node.getAttribute("name") =='starter' || node.getAttribute("name") =='workFlowAdministrator') )
	{
		alert("Não pode remover este participante");
	}
}
function newElement(eleName , text  , xml , booleanCdata )
{
	var ele = xml.createElement( eleName );
	if ( booleanCdata )
	{
		var txte = xml.createCDATASection( text );
	}
	else
	{
		var txte = xml.createTextNode( text );
	}
	ele.appendChild(txte);
	return ele;
}

function elements_getXML( xml , type )
{
	var xmlt=[];
	var toRet = null;
	var i=0;
	var newSid = GUIDGen();
	var type = type.toUpperCase();
	if ( type =='NEWXEPCODE')
	{
		
		toRet = xml.createElement( "xepCode" );
		
		var condition = xml.createElement("condition");
		var cdata	= xml.createCDATASection("");
		
		condition.appendChild(cdata);
		
		var codetorun = xml.createElement("codetorun");
		var cdata	= xml.createCDATASection("");
		codetorun.appendChild(cdata);
		
		toRet.appendChild(condition);
		toRet.appendChild(codetorun);
	}
	else if ( type =='NEWTHREAD')
	{
		
		toRet = xml.createElement( "thread" );
		toRet.setAttribute("name","");
		var label = xml.createElement("label");
		var text = xml.createTextNode("nome da label");
		label.appendChild(text);
		toRet.appendChild(label);
		
		var participant = xml.createElement("participant");
		participant.setAttribute("name","");
		toRet.appendChild( participant );
		
		var requiredWhen = xml.createElement("requiredWhen");
		var cdata	= xml.createCDATASection(" true ");
		requiredWhen.appendChild(cdata);
		toRet.appendChild( requiredWhen );
		
		var optionalWhen = xml.createElement("optionalWhen");
		var cdata	= xml.createCDATASection(" true ");
		optionalWhen.appendChild(cdata);
		toRet.appendChild( optionalWhen );
		
		
		var code = xml.createElement("code");
		toRet.appendChild(code);
	}
	else if ( type =='NEWALERT')
	{
		var toRet = xml.createElement("alert");
		var cdata	= xml.createCDATASection("");
		toRet.appendChild(cdata);
	}
	else if ( type =='NEWCALLPROGRAM')
	{
		toRet = xml.createElement( "callProgram" );
		toRet.setAttribute("async","false");
		toRet.setAttribute("name","");
		toRet.setAttribute("mode","embedded");
		
		toRet.appendChild( xml.createElement("mappingVariables") );
		toRet.appendChild( xml.createElement("mappingParticipants") );
		var code = xml.createElement("code");
		toRet.appendChild(code);
		
	}
	else if ( type =='NEWGOTO')
	{
		toRet = xml.createElement( "goto" );
		toRet.setAttribute("label","");
		toRet.appendChild( newElement("participant","" , xml , false ));
		toRet.appendChild( newElement("condition"," " , xml , true ));
	}
	else if ( type =='NEWIF')
	{
		toRet = xml.createElement( "if" );
		toRet.appendChild( newElement("condition"," " , xml , true ));
		var answers = xml.createElement("answers");
		var xTRUE = xml.createElement("TRUE");
		xTRUE.appendChild( newElement("label","Sim" , xml , true ));
		xTRUE.appendChild( xml.createElement("code") );
		answers.appendChild( xTRUE );
		
		var xFALSE = xml.createElement("FALSE");
		xFALSE.appendChild( newElement("label","Não" , xml , true ));
		xFALSE.appendChild( xml.createElement("code") );
		answers.appendChild( xFALSE );
		toRet.appendChild(answers);
	}
	else if ( type =='NEWSWITCH')
	{
	
		toRet = xml.createElement( "switch" );
		
		var answers = xml.createElement("answers");
		
		var xOP = xml.createElement("case");
		xOP.appendChild( newElement("condition","true" , xml , true ));
		xOP.appendChild( xml.createElement("code") );
		answers.appendChild( xOP );
		
		var xOP = xml.createElement("case");
		xOP.appendChild( newElement("condition","true" , xml , true ));
		xOP.appendChild( xml.createElement("code") );
		answers.appendChild( xOP );
		
		var xOP = xml.createElement("case");
		xOP.appendChild( newElement("condition","true" , xml , true ));
		xOP.appendChild( xml.createElement("code") );
		answers.appendChild( xOP );
	
		toRet.appendChild(answers);
	}
	else if ( type =='NEWWHILE')
	{
		toRet = xml.createElement( "while" );
		toRet.appendChild( newElement("condition"," " , xml , true ));
		toRet.appendChild( xml.createElement("code") );
		
	}
	else if ( type =='NEWFOREACH')
	{
		toRet = xml.createElement( "foreach" );
		toRet.appendChild( xml.createElement("foreachvariable") );
		toRet.appendChild( xml.createElement("code") );
		
	}
	else if ( type =='NEWANSWER')
	{
		toRet = xml.createElement( "answer" );
		toRet.appendChild( newElement("label"," " , xml , true ));
		toRet.appendChild( newElement("availableWhen"," " , xml , true ));
		toRet.appendChild( newElement("disableWhen"," " , xml , true ));
		var code = xml.createElement("code");
		toRet.appendChild(code);
	}
	else if ( type =='NEWCASE')
	{
		toRet = xml.createElement( "case" );
		toRet.appendChild( newElement("condition"," " , xml , true ));
		var code = xml.createElement("code");
		toRet.appendChild(code);
	}
	else if ( type =='NEWBEGINTIME')
	{
		toRet = xml.createElement( "beginTime" );
		toRet.appendChild( newElement("label"," " , xml , true ));
		toRet.appendChild( newElement("condition"," " , xml , true ));
		toRet.appendChild( newElement("time"," " , xml , true ));
		toRet.appendChild( newElement("timerVariable"," " , xml , false ));
		toRet.appendChild( newElement("deadLineDate","" , xml , true ));
		toRet.appendChild( xml.createElement("alerts") );
		
	}
	else if ( type =='NEWSTOPTIME')
	{
		toRet = xml.createElement( "stopTime" );
		toRet.appendChild( newElement("condition"," " , xml , true ));
		toRet.appendChild( newElement("timerVariable"," " , xml , false ));
	}
	else if ( type =='NEWWAITTIME')
	{
		toRet = xml.createElement( "waitTime" );
		toRet.appendChild( newElement("condition"," " , xml , true ));
		toRet.appendChild( newElement("time"," " , xml , true ));
	}
	else if ( type =='NEWMILESTONE')
	{
		toRet = xml.createElement( "milestone" );
		toRet.setAttribute("name","");
		
	}
	else if ( type =='NEWPROGRAMLABEL')
	{
		toRet = xml.createElement( "programlabel" );
		toRet.setAttribute("name","");
		
	}
    else if ( type =='NEWEXIT')
	{
		toRet = xml.createElement( "exit" );
		toRet.setAttribute("name","");
		
	}
	else if ( type =='NEWTERMINATEPROGRAM')
	{
		toRet = xml.createElement( "terminateProgram" );
		toRet.setAttribute("name","");
		
	}
	else if ( type =='NEWCOMMENT')
	{
		toRet = xml.createElement( "comment" );
		toRet.appendChild( xml.createCDATASection("commentário"));
	}
	else if ( type =='NEWWAITTHREAD')
	{
		
		toRet = xml.createElement( "waitThread" );
		toRet.appendChild( newElement("label"," " , xml , true ));
		toRet.appendChild( newElement("logicalOperator"," " , xml , false ));
		toRet.appendChild( newElement("time"," " , xml , true ));
		toRet.appendChild( newElement("ontimeout"," " , xml , true ));
		toRet.appendChild( xml.createElement("threads"));
	
	}
	
	else if ( type.substr(0,14) == 'NEWDEFVARIABLE' )
	{
			//"NEWDEFVARIABLE_OBJECT"


		toRet = xml.createElement( "defVariable" );
		toRet.setAttribute("name","");
    toRet.setAttribute("processTemplate", "n");
		toRet.setAttribute("form","");
		toRet.setAttribute("input","false");
		var typeVar ="";
		type=type.toUpperCase();
		
		if ( type.substr(15) == 'OBJECT' ) typeVar ="object";
		else if ( type.substr(15) == 'DATE') typeVar = "date";
		else if ( type.substr(15) == 'DATETIME') typeVar = "dateTime";
		else if ( type.substr(15) == 'BOOLEAN') typeVar = "boolean";
		else if ( type.substr(15) == 'NUMBER') typeVar = "number";
		else if ( type.substr(15) == 'DURATION') typeVar = "duration";
		else if ( type.substr(15) == 'CLOB') typeVar = "clob";
		else if ( type.substr(15) == 'CHAR') typeVar = "char(40)";
    else if ( type.substr(15) == 'MESSAGE') typeVar = "message";
		
		
		toRet.appendChild( newElement("type", typeVar , xml , false ));

		toRet.appendChild( newElement("label","nome do atributo" , xml , true ));
		toRet.appendChild( newElement("description","" , xml , true ));
		toRet.appendChild( newElement("value","" , xml , true ));
		toRet.appendChild( newElement("defaultValue","" , xml , true ));
		
		var objectfilter = xml.createElement("objectfilter");
		objectfilter.appendChild( newElement("xeoql","" , xml , true ));
		toRet.appendChild( objectfilter );
		toRet.appendChild( newElement("minoccurs","0" , xml , false ));
		toRet.appendChild( newElement("maxoccurs","99999" , xml , false ));
		toRet.appendChild( newElement("formula","" , xml , true ));
		toRet.appendChild( xml.createElement("lov"));
		toRet.appendChild( newElement("valid","" , xml , true ));
		toRet.appendChild( newElement("hiddenWhen","" , xml , true ));
		
		toRet.appendChild( newElement("required","" , xml , false ));
		toRet.appendChild( newElement("validDB","y" , xml , false ));
		
		toRet.appendChild( newElement("linkVar","" , xml , false ));
		toRet.appendChild( newElement("linkAttribute","" , xml , false ));
		toRet.appendChild( newElement("haveLinkVars","n" , xml , false ));
		
		toRet.appendChild( newElement("validBusiness","y" , xml , false ));
		toRet.appendChild( newElement("showMode","" , xml , false ));
		toRet.appendChild( newElement("mode","write" , xml , false ));

		toRet.appendChild( xml.createElement("availableMethods"));
		toRet.appendChild( xml.createElement("hiddenMethods"));
		toRet.appendChild( xml.createElement("requiredMethods"));
		
			
			
	}
  else if ( type.substr(0,13) == 'NEWDEFMESSAGE' )
	{
      //"NEWDEFVARIABLE_OBJECT"

		toRet = xml.createElement( "defMessage" );
		toRet.setAttribute("name","");
    toRet.setAttribute("processTemplate","n");
		toRet.setAttribute("form","");
		toRet.setAttribute("input","false");

		toRet.appendChild( newElement("label","nome do atributo" , xml , true ));
		toRet.appendChild( newElement("description","" , xml , true ));
    toRet.appendChild( newElement("subject","" , xml , true ));
    toRet.appendChild( newElement("message","" , xml , true ));
    var objTemplate = xml.createElement("objTemplate");
    objTemplate.setAttribute("boui","");
    var keyword = xml.createElement("keyWords");
    keyword.setAttribute("boui","");
    keyword.appendChild( newElement("keyAttribute","" , xml , true ));
    objTemplate.appendChild(keyword);
		toRet.appendChild( objTemplate);
  }
	else if ( type == 'NEWDEFPARTICIPANT' )
	{
			//"NEWDEFVARIABLE_OBJECT"


		toRet = xml.createElement( "defParticipant" );
		toRet.setAttribute("name","");
		
		
		var typeVar =""
		toRet.setAttribute("input","false");
		toRet.appendChild( newElement("label","nome do participante" , xml , true ));
		toRet.appendChild( newElement("description","" , xml , true ));
		toRet.appendChild( newElement("value","" , xml , true ));
		toRet.appendChild( newElement("type","" , xml , false ));
		toRet.appendChild( newElement("defaultValue","" , xml , true ));
		toRet.appendChild( newElement("object","" , xml , false ));
		toRet.appendChild( newElement("formula","" , xml , true ));
		toRet.appendChild( newElement("showMode","" , xml , false ));
		toRet.appendChild( newElement("mode","write" , xml , false ));
		var objectfilter = xml.createElement("objectfilter");
		objectfilter.appendChild( newElement("xeoql","" , xml , true ));
		toRet.appendChild( objectfilter );
		toRet.appendChild( newElement("mode","write" , xml , false ));
		
		
			
	}
  else if ( type.substr(0,15) == 'NEWDEFPROCEDURE' )
	{
      //"NEWDEFVARIABLE_OBJECT"

		toRet = xml.createElement( "defProcedure" );
		toRet.setAttribute("name","");

		toRet.appendChild( newElement("label","nome do atributo" , xml , true ));
    var participants = xml.createElement("participants");
//    var participant = xml.createElement("participant");
//    participant.setAttribute("name","");
//    participants.appendChild( participant);
		toRet.appendChild( participants);
    var code = xml.createElement("code");
		toRet.appendChild(code);
  }
  else if ( type =='NEWCALLPROCEDURE' )
	{
    var newEleName="callProcedure";
    toRet = xml.createElement( newEleName );
    var procedure = xml.createElement("procedure");
    procedure.setAttribute("name","");
    toRet.appendChild( procedure );
  }
  else if ( type =='NEWREMOVEALLPROCEDURES' )
	{
    var newEleName="removeAllProcedures";
    toRet = xml.createElement( newEleName );    
  }
  else if ( type =='NEWREMOVEPROCEDURES' || type =='NEWADDPROCEDURES')
	{
    var newEleName="";
    if ( type == 'NEWREMOVEPROCEDURES' ) newEleName ="removeProcedures";
    else if ( type == 'NEWADDPROCEDURES' ) newEleName="addProcedures";
    toRet = xml.createElement( newEleName );
    var procedures = xml.createElement("procedures");
//    var procedure = xml.createElement("procedure");
//    procedure.setAttribute("name","");
//    if(type =='NEWADDPROCEDURES')
//    {
//      procedure.setAttribute("required","n");
//    }
//    procedures.appendChild( procedure );
    toRet.appendChild( procedures );
  }
    else if ( type =='NEWPOLL' || type =='NEWCREATEMESSAGE' || type =='NEWMENU' || type =='NEWCHOICE' || type =='NEWDECISION' || type=='NEWACTIVITY' || type=='NEWFILLVARIABLE' || type=='NEWSEND' || type=='NEWWAITRESPONSE'||type=='NEWUSERCALLPROGRAM' )
	{
		
		var newEleName="";
		if ( type == 'NEWDECISION' ) newEleName ="decision";
		else if ( type == 'NEWACTIVITY') newEleName = "activity";
		else if ( type == 'NEWCHOICE') newEleName = "choice";
		else if ( type == 'NEWMENU') newEleName = "menu";
		else if ( type == 'NEWFILLVARIABLE' ) newEleName ="fillVariable";
		else if ( type == 'NEWSEND' ) newEleName ="send";
		else if ( type == 'NEWCREATEMESSAGE' ) newEleName ="createMessage";
		else if ( type == 'NEWPOLL' ) newEleName ="poll";
		else if ( type == 'NEWWAITRESPONSE' ) newEleName ="waitResponse";
		else if ( type == 'NEWUSERCALLPROGRAM' ) newEleName ="userCallProgram";
		
		toRet = xml.createElement( newEleName );
		
		toRet.setAttribute("async","false");
		toRet.setAttribute("optional","false");
		toRet.setAttribute("name","");
		var participant = xml.createElement("participant");
		participant.setAttribute("name","");
		toRet.appendChild( participant );

    var executante = xml.createElement("executante");
		executante.setAttribute("name","");
		toRet.appendChild( executante );
		
		var assignedRule = xml.createElement("assignedRule");
		assignedRule.setAttribute("name","");
		toRet.appendChild( assignedRule );
				
		toRet.appendChild( newElement("label","nome da label" , xml , true ));
		toRet.appendChild( newElement("description","" , xml , true ));
		toRet.appendChild( newElement("condition","true" , xml , true ));
		toRet.appendChild( xml.createElement("process") );
		toRet.appendChild( xml.createElement("alerts") );
		toRet.appendChild( newElement("deadLineDate","" , xml , true ));
		toRet.appendChild( newElement("oneShotActivity","" , xml , true ));
		toRet.appendChild( newElement("showTask","" , xml , true ));
		toRet.appendChild( newElement("showReassign","" , xml , true ));
		toRet.appendChild( newElement("showWorkFlowArea","" , xml , true ));
		toRet.appendChild( newElement("forecastTimeToComplete","" , xml , true ));
		toRet.appendChild( newElement("forecastWorkDuration","" , xml , true ));
		toRet.appendChild( newElement("canAddSubActivities","" , xml , false ));
		toRet.appendChild( newElement("canDelegate","" , xml , false ));
		toRet.appendChild( xml.createElement("controlBy")); 		
		
		var variables = xml.createElement("variables");
		assignedRule.setAttribute("form","default");
		toRet.appendChild( variables );
	
		if ( type == 'NEWDECISION' )
		{
			toRet.appendChild( newElement("question","Escreva a pergunta" , xml , true ));
		
			var approval=xml.createElement("approval");
			approval.setAttribute("boui","");
			approval.setAttribute("validAnswer","TRUE");
			toRet.appendChild( approval );
			
			var answers = xml.createElement("answers");
			var xTRUE = xml.createElement("TRUE");
			xTRUE.appendChild( newElement("label","Sim" , xml , true ));
			xTRUE.appendChild( xml.createElement("code") );
			answers.appendChild( xTRUE );
		
		
		
			var xFALSE = xml.createElement("FALSE");
			xFALSE.appendChild( newElement("label","Não" , xml , true ));
			xFALSE.appendChild( xml.createElement("code") );
			answers.appendChild( xFALSE );
		
			toRet.appendChild( answers  );
		}
		else if ( type == 'NEWUSERCALLPROGRAM' )
		{
			var programfilter = xml.createElement("programFilter");
			programfilter.appendChild( newElement("xeoql","" , xml , true ));
			toRet.setAttribute("mode","embedded");
			toRet.appendChild( programfilter );
			
		}
		else if ( type == 'NEWPOLL' )
		{
			
			toRet.appendChild( newElement("question","Escreva a pergunta" , xml , true ));
			toRet.appendChild( xml.createElement("participants") );
			toRet.appendChild( xml.createElement("answersPoll") );
//			toRet.appendChild( xml.createElement("conditionsPoll") );
			
		}
		else if ( type == 'NEWCHOICE' || type == 'NEWMENU')
		{
			toRet.appendChild( newElement("question","Escreva a pergunta" , xml , true ));
			var answers = xml.createElement("answers");
			var xOP = xml.createElement("answer");
			xOP.appendChild( newElement("availableWhen","" , xml , true ));
			xOP.appendChild( newElement("disableWhen","" , xml , true ));
			xOP.appendChild( newElement("label","Opcao A" , xml , true ));
			xOP.appendChild( xml.createElement("code") );
			answers.appendChild( xOP );
			
			var xOP = xml.createElement("answer");
			xOP.appendChild( newElement("availableWhen","" , xml , true ));
			xOP.appendChild( newElement("disableWhen","" , xml , true ));
			xOP.appendChild( newElement("label","Opcao B" , xml , true ));
			xOP.appendChild( xml.createElement("code") );
			answers.appendChild( xOP );
			
			var xOP = xml.createElement("answer");
			xOP.appendChild( newElement("label","Opcao C" , xml , true ));
			xOP.appendChild( newElement("availableWhen","" , xml , true ));
			xOP.appendChild( newElement("disableWhen","" , xml , true ));
			xOP.appendChild( xml.createElement("code") );
			answers.appendChild( xOP );
		
		
			toRet.appendChild( answers  );
		}
		else if ( type == 'NEWSEND'  || type=='NEWCREATEMESSAGE')
		{
			toRet.appendChild( newElement("requireDeliveryReceipt","false" , xml , false ));
			toRet.appendChild( newElement("requireReadReceipt","false" , xml , false ));
			toRet.appendChild( newElement("priority","2" , xml , false ));
//			toRet.appendChild( newElement("subject","" , xml , true ));
//			toRet.appendChild( newElement("message","" , xml , true ));
			var msg = xml.createElement("message")
			msg.setAttribute("name","");
			toRet.appendChild( msg );
			toRet.appendChild( xml.createElement("channel") );
			
			toRet.appendChild( xml.createElement("attachVars") );
			var from= xml.createElement("from")
			from.setAttribute("name","");
			toRet.appendChild( from );
			toRet.appendChild( xml.createElement("to") );
			toRet.appendChild( xml.createElement("cc") );
			toRet.appendChild( xml.createElement("bcc") );
			
		}
		else if ( type == 'NEWWAITRESPONSE' )
		{
			toRet.appendChild( newElement("sendID","" , xml , false ));
			var from= xml.createElement("from")
			from.setAttribute("name","");
			toRet.appendChild( from );
			
			var resp= xml.createElement("responseMessageVariable");
			resp.setAttribute("name","");
			toRet.appendChild( resp );
			
		}

		
		
	}
	
	toRet.setAttribute('sid', newSid );
	return toRet;
}

function elements_getImg( node )
{
	var nodeName=node.nodeName.toUpperCase();
	if ( nodeName == "DEFVARIABLE" )
	{
		var ty=node.selectSingleNode("type").text
		var type = ty.toUpperCase();
		if ( type.substr(0,4)=="CHAR" )
		{
			return "varchar.gif";
		}
		else if( type.substr(0,6)=="NUMBER" )
		{
			return "varnumber.gif";
		}
		else if( type.substr(0,4)=="BOOL" )
		{
			return "varboolean.gif";
		}
		else if( type=="DATE" )
		{
			return "vardate.gif";
		}
		else if( type=="DURATION" )
		{
			return "varduration.gif";
		}
		else if( type=="CLOB" )
		{
			return "varclob.gif";
		}
		else if( type=="DATETIME" )
		{
			return "vardatetime.gif";
		}
		else if( type.substr(0,6)=="OBJECT" )
		{	
			if ( ty.substr(7).length==0) return "defvariable.gif";
			else return "resources/"+ty.substr(7)+"/ico16.gif";
		}
	}
  	return nodeName.toLowerCase()+".gif";

}

var performerColors=null;
var colors=[ ];

function elements_getHtml( node )
{
	var nodeName=node.nodeName.toUpperCase();
	
	var toRet = null;

	if ( performerColors == null )
	{
		performerColors=[];
		var ps=xmlSrc.selectSingleNode("//defVariables");
        for( var i=0; i<ps.childNodes.length ; i++ )
        {
            xname="perf_"+ps.childNodes[i].getAttribute('name');
            performerColors[xname]=i;
        }
        
	}
	
	switch ( nodeName )
		{
			
			case 'PROGRAM':
			{
				
				toRet = node.getAttribute("name")
				break;
			}
			case 'DEFVARIABLES':
			{
				toRet = 'Atributos';
				break;
			}
			case 'ACTIVITYALERTS':
			{
				toRet = 'Definição de Alertas';
				break;
			}
            case 'DEFPROCEDURES':
			{
				toRet = 'Procedimentos';
				break;
			}
			case 'SUBPROCEDURE':
			{
				toRet = 'Chamada Procedimento';
				break;
			}
			 case 'PROCEDURE':
			{
				toRet = 'Procedimento';
				break;
			}
			case 'DEFPARTICIPANTS':
			{
				toRet = 'Participantes';
				break;
			}
			case 'ALERT':
			{
				toRet = node.getAttribute("name");
				break;
			}
			case 'DEFVARIABLE':
			{
				
				var type = node.selectSingleNode("type").text;
				var h=[];
				var i=0;
				var isLink= ( node.selectSingleNode("linkVar").text.length>0 );
				if ( isLink ) h[i++]="<span style='color:#2C2C80'>";
				h[i++] = node.selectSingleNode("label").text;
				h[i++] = "<span class='nameAtr' >";
				h[i++] =  node.getAttribute("name")
				if ( type.substr(0,7) == 'object.' )
				{
					var sid = node.getAttribute("sid");
					h[i++]="&nbsp;&nbsp;<img title='adiciona atributos do objecto' src='"+WKFL_TEMA_DIR+"includeattr.gif' onclick='includeAttr(\""+sid+"\")'/>";
					
				}
				h[i++]="</span>";
				if ( isLink ) h[i++]="</span>";
				toRet = h.join("");
				break;
			}
      case 'DEFMESSAGE':
			{
				var h=[];
				var i=0;
				h[i++] = node.selectSingleNode("label").text;
				h[i++] = "<span class='nameAtr' >";
				h[i++] =  node.getAttribute("name");				
				h[i++]="</span>";
				if ( isLink ) h[i++]="</span>";
				toRet = h.join("");
				break;
			}
			case 'DEFPARTICIPANT':
			{
				toRet = node.selectSingleNode("label").text+"<span class='nameAtr' >"+node.getAttribute("name")+"</span>";
				break;
			}
			case 'FILLVARIABLE':
			{
		
				
				//toRet = node.selectSingleNode("label").text;
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
			}
			case 'ACTIVITY':
			{
		
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
				
			}
			case 'SEND':
			{
		
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
				
			}
			case 'CREATEMESSAGE':
			{
		
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
				
			}
			case 'POLL':
			{
		
				toRet = node.selectSingleNode("label").text+getNameHTML(node);
				break;
				
			}
			case 'WAITRESPONSE':
			{
		
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
				
			}
			case 'DECISION':
			{
		
				var texto = node.selectSingleNode("question").text+getNameHTML(node)+getPerformerHTML( node );;
				toRet = texto
				break;

				break;
			}
			case 'USERCALLPROGRAM':
			{
				toRet = node.selectSingleNode("label").text+getNameHTML(node)+getPerformerHTML( node );
				break;
			}
			case 'MENU':
			case 'CHOICE':
			{
		
				var texto = node.selectSingleNode("question").text+getNameHTML(node)+getPerformerHTML( node );
				toRet = texto;
				break;

				break;
			}
			
			case 'ANSWER':
			{
		
				var texto = node.selectSingleNode("label").text;
				//var x = "<div onblur='syncText(\""+node.getAttribute("sid")+"\",\"label\")' contentEditable=true ><div>"+texto+"</div></div>";
				toRet = texto;
				break;
			}
			case 'TRUE':
			{
		
				var texto = node.selectSingleNode("label").text;
				//var x = "<div onblur='syncText(\""+node.getAttribute("sid")+"\",\"label\")' contentEditable=true ><div>"+texto+"</div></div>";
				toRet = texto;
				
				break;
			}
			case 'FALSE':
			{
		
				var texto = node.selectSingleNode("label").text;
				//var x = "<div onblur='syncText(\""+node.getAttribute("sid")+"\",\"label\")' contentEditable=true ><div>"+texto+"</div></div>";
				
				toRet = texto;
				break;
			}
			case 'XEPCODE':
			{
			
				var texto = node.selectSingleNode("codetorun").text  ;
				texto = texto.replace(/\n/g,"<br/>");
				toRet = texto;
				break;
			
			}
			case 'THREAD':
			{
				
				toRet = node.selectSingleNode("label").text;
				break;
			}
			case 'WAITTHREAD':
			{
				
				toRet = ""+node.selectSingleNode("label").text+"";
				break;
			}
			case 'CALLPROGRAM':
			{
				toRet = ""+node.getAttribute("name")+"&nbsp;"+node.getAttribute("programName");
				break;
			}
			case 'GOTO':
			{
				toRet = ""+node.getAttribute("label")+"&nbsp;"
				break;
			}
			case 'IF':
			{
				toRet = ""+node.selectSingleNode("condition").text+"&nbsp;";
				break;
			}
			case 'CASE':
			{
				toRet = ""+node.selectSingleNode("condition").text+"&nbsp;";
				break;
			}
			case 'SWITCH':
			{
				toRet = "&nbsp;";
				break;
			}
			case 'WHILE':
			{
				toRet = ""+node.selectSingleNode("condition").text+"&nbsp;";
				break;
			}
			case 'FOREACH':
			{
				toRet = ""+node.selectSingleNode("foreachvariable").text+"&nbsp;";
				break;
			}
			case 'BEGINTIME':
			{
				
				toRet =""+node.selectSingleNode("label").text+"&nbsp;"+"<span class='nameAtr'>"+node.selectSingleNode("timerVariable").text+"</span>: "+node.selectSingleNode("time").text+"&nbsp;";
				break;
			}
			case 'STOPTIME':
			{
				
				toRet = "<span class='nameAtr'>"+node.selectSingleNode("timerVariable").text+"</span>";
				break;
			}
			case 'WAITTIME':
			{
				
				toRet =node.selectSingleNode("time").text+"&nbsp;";
				break;
			}
			case 'MILESTONE':
			{
				
				toRet =node.getAttribute("name")+"&nbsp;";
				break;
			}
			case 'PROGRAMLABEL':
			{
				
				toRet =node.getAttribute("name")+"&nbsp;";
				break;
			}
            case 'EXIT':
			{
				
				toRet =node.getAttribute("name")+"&nbsp;";
				break;
			}
            case 'TERMINATEPROGRAM':
			{
				
				toRet =node.getAttribute("name")+"&nbsp;";
				break;
			}
			case 'COMMENT':
			{
				toRet ="<i>"+node.text+"</i>";
				break;
			}
      case 'DEFPROCEDURE':
			{
				var h=[];
				var i=0;
				h[i++] = node.selectSingleNode("label").text;
				h[i++] = "<span class='nameAtr' >";
				h[i++] =  node.getAttribute("name");				
				h[i++]="</span>";
				if ( isLink ) h[i++]="</span>";
				toRet = h.join("");
				break;
			}
      case 'ADDPROCEDURES':
      {
        toRet ="Adicionar Procedimentos"+"&nbsp;";
        break;
      }
      case 'REMOVEPROCEDURES':
      {
        toRet ="Remover Procedimentos"+"&nbsp;";
        break;
      }
      case 'REMOVEALLPROCEDURES':
			{
		
				toRet ="Remover Todos Procedimentos"+"&nbsp;";
				break;
			}
      case 'CALLPROCEDURE':
			{
		    
				toRet ="Chamar Procedimento"+"&nbsp;"+node.selectSingleNode("procedure").getAttribute("name");
				break;
			}
		}
	return toRet;

}

function getNameHTML(node)
{
	return "<span class='nameAtr' >"+node.getAttribute("name")+"</span>";
}

var participantsName=[];

function getPerformerHTML( node )
{
  
  var xname = node.selectSingleNode("participant").getAttribute("name");
  var xlabel="";
  if ( participantsName[ xname ] )
  {
    xlabel=participantsName[ xname ];
  } 
  else
  {
	var search="//*/defParticipant[@name='"+xname+"']"; //NTRIMJS
	var nodeVar=xmlSrc.firstChild.selectSingleNode(search);
	if ( nodeVar )
	{
	 xlabel=nodeVar.selectSingleNode("label").text;
	 participantsName[ xname ]=xlabel;
	}
  
  }
  return "<span class='namePar'>"+xlabel+"</span>";
  
}

function syncText( sid , nodeName )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var nodeFrom=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var textos = event.srcElement.childNodes;
	var texto ="";
	for( var i=0 ; i< textos.length ; i++ )
	{
		texto += textos[i].innerText;
		if( i+1 < textos.length )
		{
			texto+="<br/>";
		}
	}
	nodeFrom.selectSingleNode( nodeName ).firstChild.text=texto;
}


function includeAttr( sid )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var nodeVar=TREE_EDIT.code.firstChild.selectSingleNode(search);
	
	 for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
	{
	    unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
	}
	var ele = window.document.getElementById("sid"+sid)
	selectRow(ele);
	
	var type=nodeVar.selectSingleNode("type").text;
	var object = type.split(".")[1];
    if ( object != null && object.length > 0 )
    {
		//var hlpFrame =  document.getElementById("helperFrame");
		var xsrc = "__xwfDesigner_expandObject.jsp?object="+object+"&sid="+sid+"&docid="+getDocId();
		window.showModalDialog(xsrc,this,"dialogHeight:300px;dialogWidth;200px;scroll=yes;status=no;resizable=yes;help=no;unadorned=yes"); 
		//hlpFrame.src=xsrc;
    }
	
}
function includeAttr_2( sid , attrs )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	 for (var i =0 ; i< TREE_EDIT.selectLines.length ; i++ )
	{
	    unselectRow ( document.getElementById("treeline"+ TREE_EDIT.selectLines[i] ));
	}
	var nodeTo=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var x=null;
	for(var i=0; i< attrs.length;i++)
	{
		var name =attrs[i][0];
		var label=attrs[i][1];
		var type = attrs[i][2];
		x = elements_getXML( xmlSrc ,"NEWDEFVARIABLE_"+type );
		
		
		
		x.selectSingleNode("label").firstChild.text = nodeTo.selectSingleNode("label").text+"."+label;
		
		x.setAttribute( "name" ,nodeTo.getAttribute("name")+"_"+name );
		x.selectSingleNode("linkVar").text = nodeTo.getAttribute("name");
		x.selectSingleNode("linkAttribute").text = name;
		
		x.selectSingleNode("type").text=type;;
		if ( nodeTo.nextSibling )
		{
			nodeTo.parentNode.insertBefore(  x , nodeTo.nextSibling );
		}
		else
		{
			nodeTo.parentNode.appendChild(  x );
			//nodeTo.parentNode.insertBefore(  removedElement , nodeTo );
		}

	}
	if(x!=null)
	{
		nodeTo.selectSingleNode("haveLinkVars").text="y";
		TREE_EDIT.refreshHTM();
		var ele = window.document.getElementById("sid"+x.getAttribute("sid"))
		selectRow(ele);
	}
}

function syncXepCode( sid )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var nodeFrom=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var textos = event.srcElement.childNodes;
	var texto ="";
	for( var i=0 ; i< textos.length ; i++ )
	{
		texto += textos[i].innerText;
		if( i+1 < textos.length )
		{
			texto+="\n";
		}
	}
	
	nodeFrom.selectSingleNode("codetorun").firstChild.text=texto;
}


function elements_GetNodeToAppendChild( nodeName )
{
	if ( !nodeName ) return null;
	var name=nodeName.toUpperCase();
	if ( name=="DEFPROCEDURE" || name=="CALLPROGRAM" || name =='THREAD' || name=='WHILE' || name=='FOREACH' || name =='CASE'|| name =='PROGRAM' || name =='TRUE' || name =='FALSE' || name=='ANSWER')
	{
		return "code";	
	}
	else if (  name=='CHOICE' || name=='SWITCH' || name == 'MENU')
	{
		return "answers";	
	}
	else if (  name=='DEFVARIABLES' || name=='DEFPARTICIPANTS' || name=="ACTIVITYALERTS" || name=="DEFPROCEDURES")
	{
		return "";
	}
	
	return null
}

function elements_AcceptChild( parentNodeName , childNodeName )
{
	var toRet = true;
	return true;
	parentNodeName=parentNodeName.toUpperCase();
	childNodeName=childNodeName.toUpperCase();
	
	if ( parentNodeName == "DEFVARIABLES" )
	{
		if ( childNodeName.substr(0,11) != "DEFVARIABLE"  && childNodeName.substr(0,10) != "DEFMESSAGE")
		{
			toRet=false;
		}
	}
  if ( parentNodeName == "DEFPARTICIPANTS" )
	{
		if ( childNodeName != "DEFPARTICIPANT"  )
		{
			toRet=false;
		}
	}
	if ( childNodeName.substr(0,11) == "DEFVARIABLE" && parentNodeName != "DEFVARIABLES" )
	{
		toRet=false;
	}
  if ( childNodeName.substr(0,10) == "DEFMESSAGE" && parentNodeName != "DEFVARIABLES" )
	{
		toRet=false;
	}
	
	
	return toRet;
}
 
function  elements_haveFixedChilds( node )
{
	nodeName=node.nodeName.toUpperCase();
	if ( nodeName =="DECISION" ||
		 nodeName =="CHOICE" ||
     nodeName =="MENU" ||
		 nodeName =="IF" ||
		 nodeName =="SWITCH" 
	)
	{
	
	   return true;
	}
	return false;
}
 
function elements_isNodeToRender( nodeName )
{
	nodeName=nodeName.toUpperCase();
		
	if ( nodeName == 'PROGRAM' ||
		 nodeName == 'CALLPROGRAM' || 
		 nodeName =='THREAD' ||
		 nodeName =='WAITTHREAD' ||
		 nodeName=='FILLVARIABLE' || 
		 nodeName=='ACTIVITY' || 
		 nodeName=='PROCEDURE' ||
		 nodeName=='SUBPROCEDURE' ||
		 nodeName=='USERCALLPROGRAM' || 
		 nodeName=='ADDPROCEDURES' || 
		 nodeName=='REMOVEPROCEDURES' || 
		 nodeName=='REMOVEALLPROCEDURES' || 
		 nodeName=='CALLPROCEDURE' || 
		 nodeName=='XEPCODE' || 
	     nodeName=='MENU' || 
		 nodeName=='CHOICE' || 
		 nodeName=='DECISION' || 
		 nodeName=='TRUE' || 
		 nodeName=='ANSWER' || 
		 nodeName=='FALSE' || 
		 nodeName=='GOTO' || 
		 nodeName=='IF' || 
		 nodeName=='SWITCH'|| 
		 nodeName=='CASE' || 
		 nodeName=='WHILE' || 
		 nodeName=='FOREACH' ||
		 nodeName=='BEGINTIME' ||
		 nodeName=='STOPTIME' ||
		 nodeName=='WAITTIME' ||
		 nodeName=='WAITRESPONSE' ||
		 nodeName=='SEND' ||
		 nodeName=='MILESTONE' ||
		 nodeName=='PROGRAMLABEL' ||
         nodeName=='EXIT' ||
		 nodeName=='TERMINATEPROGRAM' ||
		 nodeName=='COMMENT' ||
		 nodeName=='POLL' ||
		 nodeName=='CREATEMESSAGE' ||
		 nodeName=='DEFVARIABLES' || nodeName=='DEFVARIABLE'|| nodeName=='DEFMESSAGE'||
		 nodeName=='ACTIVITYALERTS' || nodeName=='ALERT'||
         nodeName=='DEFPROCEDURES'||nodeName=='DEFPROCEDURE'||
		 nodeName=='DEFPARTICIPANTS' || nodeName=='DEFPARTICIPANT'
    )
	{
	   return true;
	}
	return false;
}


//------------


function elements_select( sid )
{
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	
	if ( node )
	{
	     var nodeName = node.nodeName.toUpperCase();
	     workCell.innerHTML="&nbsp";
	     if ( nodeName == "FILLVARIABLE" || nodeName == "DECISION" || nodeName == "ACTIVITY" || nodeName=='MENU' 
            || nodeName=='CHOICE'|| nodeName=='SEND' || nodeName=='POLL' || nodeName=='CREATEMESSAGE' || nodeName=='WAITRESPONSE' || nodeName=='USERCALLPROGRAM'
        )
	     {
			elements_showFILLVARIABLE( node );
	     
	     }
       else if ( nodeName == "CALLPROCEDURE" )
	     {
			elements_showCALLPROCEDURES( node );
	     }
       else if ( nodeName == "REMOVEALLPROCEDURES" )
	     {
			elements_showREMOVEALLPROCEDURES( node );
	     }
       else if ( nodeName == "REMOVEPROCEDURES" )
	     {
			elements_showREMOVEPROCEDURES( node );
	     }
       else if ( nodeName == "ADDPROCEDURES" )
	     {
			elements_showADDPROCEDURES( node );
	     }
	     else if ( nodeName == "DEFVARIABLE" )
	     {
			elements_showDEFVARIABLE( node );
	     }
       else if ( nodeName == "DEFPROCEDURE" )
	     {
			elements_showDEFPROCEDURE( node );
	     }
       else if ( nodeName == "DEFMESSAGE" )
	     {
			elements_showDEFMESSAGE( node );
	     }
	     else if ( nodeName == "ALERT" )
	     {
			elements_showALERT( node );
	     }
	     else if ( nodeName == "DEFPARTICIPANT" )
	     {
			elements_showDEFPARTICIPANT( node );
	     }
	     else if ( nodeName == "THREAD" )
	     {
			elements_showTHREAD( node );
	     }
	     else if ( nodeName == "ANSWER" )
	     {
			elements_showANSWER( node );
	     }
	     else if ( nodeName == "WAITTHREAD" )
	     {
			elements_showWAITTHREAD( node );
	     }
	     else if ( nodeName == "GOTO" )
	     {
			elements_showGOTO( node );
	     }
	     else if ( nodeName == "IF" )
	     {
			elements_showIF( node );
	     }
	     else if ( nodeName == "CASE" )
	     {
			elements_showCASE( node );
	     }
	     else if ( nodeName == "WHILE" )
	     {
			elements_showWHILE( node );
	     }
	     else if ( nodeName == "FOREACH" )
	     {
			elements_showFOREACH( node );
	     }
	     else if ( nodeName == "BEGINTIME" )
	     {
			elements_showBEGINTIME( node );
	     }
	     else if ( nodeName == "STOPTIME" )
	     {
			elements_showSTOPTIME( node );
	     }
	     else if ( nodeName == "XEPCODE" )
	     {
			elements_showXEPCODE( node );
	     }
	     else if ( nodeName == "WAITTIME" )
	     {
			elements_showWAITTIME( node );
	     }
	     else if ( nodeName == "MILESTONE" || nodeName == "PROGRAMLABEL" || nodeName == "EXIT" || nodeName == "TERMINATEPROGRAM")
	     {
			elements_showMILESTONE( node );
	     }
	     else if ( nodeName == "COMMENT" )
	     {
			elements_showCOMMENT( node );
	     }
	     else if ( nodeName == "CALLPROGRAM" )
	     {
			elements_showCALLPROGRAM( node );
	     }
   	     else if ( nodeName == "PROGRAM" )
	     {
			elements_showPROGRAM( node );
	     }
	     else if ( (nodeName == "TRUE" || nodeName=="FALSE")&& node.parentNode.parentNode.nodeName=="decision"  )
	 	 {
				elements_showTRUEFALSE( node );
		 }
	     //workCell.innerHTML=createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	}
	else
	{
	//	alert("ERRO !!!")	
	}

}

function elements_unselect( sid )
{
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var toRet = true;
	
	if ( node )
	{
	     var nodeName = node.nodeName.toUpperCase();
	     
	     if ( nodeName == "FILLVARIABLE" || nodeName == "DECISION" || nodeName == "ACTIVITY" || nodeName=='MENU' 
            || nodeName=='CREATEMESSAGE' || nodeName=='POLL' || nodeName=='CHOICE' || nodeName=='SEND' || nodeName=='WAITRESPONSE'|| nodeName=='USERCALLPROGRAM'
        )
	     {
			var toRet =elements_closeFILLVARIABLE( node );
	     }
       else if ( nodeName == "CALLPROCEDURE" )
	     {
			var toRet =elements_closeCALLPROCEDURES( node );
	     }
       else if ( nodeName == "REMOVEALLPROCEDURES" )
	     {
			var toRet =elements_closeREMOVEALLPROCEDURES( node );
	     }
       else if ( nodeName == "REMOVEPROCEDURES" )
	     {
			var toRet =elements_closeREMOVEPROCEDURES( node );
	     }
       else if ( nodeName == "ADDPROCEDURES" )
	     {
			var toRet =elements_closeADDPROCEDURES( node );
	     }
	     else if ( nodeName == "DEFVARIABLE" )
	     {
			var toRet =elements_closeDEFVARIABLE( node );
	     }
       else if ( nodeName == "DEFPROCEDURE" )
	     {
			var toRet =elements_closeDEFPROCEDURE( node );
	     }
       else if ( nodeName == "DEFMESSAGE" )
	     {
			var toRet =elements_closeDEFMESSAGE( node );
	     }
	     else if ( nodeName == "ALERT" )
	     {
			var toRet =elements_closeALERT( node );
	     }
	     else if ( nodeName == "DEFPARTICIPANT" )
	     {
			var toRet =elements_closeDEFPARTICIPANT( node );
	     }
	     else if ( nodeName == "THREAD" )
	     {
			var toRet =elements_closeTHREAD( node );
	     }
	     else if ( nodeName == "ANSWER" )
	     {
			var toRet =elements_closeANSWER( node );
	     }
	     else if ( nodeName == "WAITTHREAD" )
	     {
			var toRet =elements_closeWAITTHREAD( node );
	     }
	     else if ( nodeName == "GOTO" )
	     {
			var toRet =elements_closeGOTO( node );
	     }
	     else if ( nodeName == "IF" )
	     {
			var toRet =elements_closeIF( node );
	     }
	     else if ( nodeName == "CASE" )
	     {
			var toRet =elements_closeCASE( node );
	     }
	     else if ( nodeName == "WHILE" )
	     {
			var toRet =elements_closeWHILE( node );
	     }
	     else if ( nodeName == "FOREACH" )
	     {
			var toRet =elements_closeFOREACH( node );
	     }
	     else if ( nodeName == "BEGINTIME" )
	     {
			var toRet =elements_closeBEGINTIME( node );
	     }
	     else if ( nodeName == "STOPTIME" )
	     {
			var toRet =elements_closeSTOPTIME( node );
	     }
	     else if ( nodeName == "XEPCODE" )
	     {
			var toRet =elements_closeXEPCODE( node );
	     }
	     else if ( nodeName == "WAITTIME" )
	     {
			var toRet =elements_closeWAITTIME( node );
	     }
	     else if ( nodeName == "MILESTONE" || nodeName == "PROGRAMLABEL" || nodeName == "EXIT" || nodeName == "TERMINATEPROGRAM")
	     {
			var toRet =elements_closeMILESTONE( node );
	     }
	     else if ( nodeName == "COMMENT" )
	     {
			var toRet =elements_closeCOMMENT( node );
	     }
	     else if ( nodeName == "CALLPROGRAM" )
	     {
			var toRet =elements_closeCALLPROGRAM( node );
	     }
	     else if ( nodeName == "PROGRAM" )
	     {
			var toRet =elements_closePROGRAM( node );
	     }
	     else if ( (nodeName == "TRUE" || nodeName=="FALSE")&& node.parentNode.parentNode.nodeName=="decision"  )
	 	 {
			
			var toRet =elements_closeTRUEFALSE( node );
		 }
	}
	else
	{
		toRet=true;
	}
	if ( toRet )
	{
	workCell.innerHTML="&nbsp";
	}
   return toRet;
}

function changeAtr()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.lastIndexOf("_")+1 );
	if ( sid == '0' )
	{
		return
	}
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	if ( nodeName == "FILLVARIABLE" || nodeName == "DECISION" || nodeName=="ACTIVITY" || nodeName=='MENU' 
      || nodeName=='CHOICE'|| nodeName=='SEND' || nodeName=='WAITRESPONSE'||nodeName=='USERCALLPROGRAM'
  )
	{
		readFromHTML_FILLVARIABLE( node , "fromChangeAtr" )
	}
  else if ( nodeName == "CALLPROCEDURE" )
	{
		readFromHTML_CALLPROCEDURES( node )
	}
	else if ( nodeName == "DEFVARIABLE" )
	{
		readFromHTML_DEFVARIABLE( node )
	}
  else if ( nodeName == "DEFPROCEDURE" )
	{
		readFromHTML_DEFPROCEDURE( node )
	}
  else if ( nodeName == "DEFMESSAGE" )
	{
		readFromHTML_DEFMESSAGE( node )
	}
	else if ( nodeName == "ALERT" )
	{
		readFromHTML_ALERT( node )
	}
	else if ( nodeName == "DEFPARTICIPANT" )
	{
		readFromHTML_DEFPARTICIPANT( node )
	}
	else if ( nodeName == "THREAD" )
	{
		readFromHTML_THREAD( node )
	}
	else if ( nodeName == "ANSWER" )
	{
		readFromHTML_ANSWER( node )
	}
	else if ( nodeName == "WAITTHREAD" )
	{
		readFromHTML_WAITTHREAD( node )
	}
	else if ( nodeName == "GOTO" )
	{
		readFromHTML_GOTO( node )
	}
	else if ( nodeName == "IF" )
	{
		readFromHTML_IF( node )
	}
	else if ( nodeName == "CASE" )
	{
		readFromHTML_CASE( node )
	}
	else if ( nodeName == "WHILE" )
	{
		readFromHTML_WHILE( node )
	}
	else if ( nodeName == "FOREACH" )
	{
		readFromHTML_FOREACH( node )
	}
	else if ( nodeName == "BEGINTIME" )
	{
		readFromHTML_BEGINTIME( node )
	}
	else if ( nodeName == "STOPTIME" )
	{
		readFromHTML_STOPTIME( node )
	}
	else if ( nodeName == "XEPCODE" )
	{
		readFromHTML_XEPCODE( node )
	}
	else if ( nodeName == "WAITTIME" )
	{
		readFromHTML_WAITTIME( node )
	}
	else if ( nodeName == "MILESTONE" || nodeName == "PROGRAMLABEL" || nodeName == "EXIT" || nodeName == "TERMINATEPROGRAM")
	{
		readFromHTML_MILESTONE( node )
	}
	
	else if ( nodeName == "COMMENT" )
	{
		readFromHTML_COMMENT( node )
	}
	else if ( (nodeName == "TRUE" || nodeName=="FALSE")&& node.parentNode.parentNode.nodeName=="decision"  )
	{
		readFromHTML_TRUEFALSE( node )
	}
	
}

function getValue( oid )
{
	var iLen = wForm.elements.length;
	for (i = 0; i < iLen; i++)
	{
		o = wForm.elements[i];
		if ( o.name==oid )
		{
      if(o.className == "selectBox")
      {
        return o.returnValue;
      }
			if (o.type == "text" || o.type == "textarea")
			{
				o.value = Trim(o.value);
			}
			if( o.returnValue) return o.returnValue;
			else return o.value;
		}
	}
	
}


/************************   ACTIVITY (FILL VARIABLE,etc )**********************************/

function elements_showFILLVARIABLE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	var optional = node.getAttribute("optional");
	var name = node.getAttribute("name");
	var async = node.getAttribute("async");
	var label = node.selectSingleNode("label").text;
	var description = node.selectSingleNode("description").text;
	var duration = node.selectSingleNode("forecastWorkDuration").text;
	var process = node.selectSingleNode("process").text;
	var deadLineDate = node.selectSingleNode("deadLineDate").text;
    var oneShotActivity = node.selectSingleNode("oneShotActivity") == null ? 'false': node.selectSingleNode("oneShotActivity").text;
    var showTask = node.selectSingleNode("showTask") == null ? 'true': node.selectSingleNode("showTask").text;
    var showReassign = node.selectSingleNode("showReassign") == null ? 'true': node.selectSingleNode("showReassign").text;
    var showWorkFlowArea = node.selectSingleNode("showWorkFlowArea") == null ? 'true': node.selectSingleNode("showWorkFlowArea").text;
	var durationTTC = node.selectSingleNode("forecastTimeToComplete").text;
	var participant = node.selectSingleNode("participant").getAttribute("name");
  var executante = node.selectSingleNode("executante").getAttribute("name");
    var procedure = node.selectSingleNode("procedure") == null ? '':node.selectSingleNode("procedure").getAttribute("name");
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	
	//var durationHTML = createFieldDuration( duration , "duration_"+sid,"duration_"+sid,"2",true,false,true,"changeAtr()",false,false,false);
	var durationHTML=createFieldText( duration , "duration_"+sid,"duration_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	//var durationTTCHTML = createFieldDuration( durationTTC , "durationTTC_"+sid,"durationTTC_"+sid,"2",true,false,true,"changeAtr()",false,false,false);
	var processHTML = createDetachFieldLookup(
			process,
			"process_"+sid,
			"process_"+sid,
			"process",
			"process",
			"Processo",
			getDocId(), //docid
			"single", //single or multi
			1,
			false, //isdisable
			true
			)
	
	var durationTTCHTML=createFieldText( durationTTC , "durationTTC_"+sid,"durationTTC_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var participants=TREE_EDIT.code.selectSingleNode("//defParticipants");
    var variables=TREE_EDIT.code.selectSingleNode("//defVariables");
	
	var partInt=[];
	var partExt=[];
  var execInt=[];
	var execExt=[];

    var msgDefInt=[];
    var msgDefExt=[];

	var optionalInt=["false","true"];
	var optionalExt=["Não","Sim"];

	
	if ( node.nodeName == 'decision' || node.nodeName =='choice' || node.nodeName =='menu')
	{
		var question = node.selectSingleNode("question").text;
		var questionHTML = createFieldText( question , "question_"+sid,"question_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		if ( node.nodeName == 'decision' )
		{
			var approval = node.selectSingleNode("approval");
			var approvalBoui = approval.getAttribute("boui");
			var bouiAppHTML = createDetachFieldLookup(
			approvalBoui,
			"approval_"+sid,
			"approval_"+sid,
			"xwfApprovalRule",
			"xwfApprovalRule",
			"Regras de Aprovação",
			getDocId(), //docid
			"single", //single or multi
			1,
			false, //isdisable
			true)
			
			
		}
	}
    else
    if ( node.nodeName == 'poll')
	{
		var question = node.selectSingleNode("question").text;
		var questionHTML = createFieldText( question , "question_"+sid,"question_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		if ( node.nodeName == 'poll' )
		{
			var approval = node.selectSingleNode("approval");
			var approvalBoui = approval.getAttribute("boui");
			var bouiAppHTML = createDetachFieldLookup(
			approvalBoui,
			"approval_"+sid,
			"approval_"+sid,
			"xwfApprovalRule",
			"xwfApprovalRule",
			"Regras de Aprovação",
			getDocId(), //docid
			"single", //single or multi
			1,
			false, //isdisable
			true)
			
			
		}
	}
	
	
	if ( node.nodeName == "userCallProgram" )
	{
		var programFilter = node.selectSingleNode("programFilter").selectSingleNode("xeoql").text;
		var mode = node.getAttribute("mode");
		var programFilterHTML = createFieldText( programFilter , "programFilter_"+sid,"programFilter_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		var modeInt=["embedded","outsource"];
		var modeExt=["Embebido","Isolado"];
		var modeHTML=createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", modeExt , modeInt ,false,false,"changeAtr" );

	}
	

  execInt[0] = "";
  execExt[0] = "&nbsp";

	for ( var p=0 ; p < participants.childNodes.length ; p++ )
	{
		partInt[ p ] = participants.childNodes(p).getAttribute("name");
		partExt[ p ] = participants.childNodes(p).selectSingleNode('label').text;
    execInt[ p+1 ] = participants.childNodes(p).getAttribute("name");
		execExt[ p+1 ] = participants.childNodes(p).selectSingleNode('label').text;
	}

   
	
	
	var participantHTML = "<b>não existem participantes definidos</b>";
	
	if ( partInt.length > 0 )
	{
		participantHTML=createFieldCombo(participant,"participant_"+sid,"participant_"+sid,"1", partExt , partInt ,false,false,"changeAtr" );	
	}

  //executante
  var executanteHTML = "<b>não existem participantes definidos</b>";
	
	if ( partInt.length > 0 )
	{
		executanteHTML=createFieldCombo(executante,"executante_"+sid,"executante_"+sid,"1", execExt , execInt ,false,false,"changeAtr" );	
	}

	var optionalHTML = createFieldCombo(optional,"optional_"+sid,"optional_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );	
    var oneshotHTML = createFieldCombo(oneShotActivity == "" ? false:oneShotActivity,"oneShotActivity_"+sid,"oneShotActivity_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );	
    var showTask = createFieldCombo(showTask == "" ? true:showTask,"showTask_"+sid,"showTask_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );	
    var showReassignHTML = createFieldCombo(showReassign == "" ? true:showReassign,"showReassign_"+sid,"showReassign_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );	
    var showWorkflowAreaHTML = createFieldCombo(showWorkFlowArea == "" ? true:showWorkFlowArea,"showWorkFlowArea_"+sid,"showWorkFlowArea_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );	
	var asyncHTML = createFieldCombo(async,"async_"+sid,"async_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );		
	var nameHTML=createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)	
	//createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	
	
	
	
	
	     htm[i++]='<TABLE id="49" class="layout" cellspacing="0" cellpadding="0">'
         htm[i++]='<TBODY>';
         htm[i++]='<TR height="25">';
         htm[i++]='<TD>';
         htm[i++]='<TABLE cellpadding="0" cellspacing="0" class="tabBar" id="49_body" onkeyup="so(\'49\');onKeyUpTab_std(event)" onmouseover="so(\'49\');onOverTab_std(event)" onmouseout="so(\'49\');onOutTab_std(event)" ondragenter="so(\'49\');ondragenterTab_std(event)" ondragover="so(\'49\');ondragoverTab_std(event)" onclick="so(\'49\');onClickTab_std(event)">';
         htm[i++]='<TBODY>';
         htm[i++]='<TR>'
         htm[i++]='<TD style="padding:0px" id="49_tabs" valign="bottom" noWrap="yes">'
         htm[i++]='<SPAN class="tab tabOn" id="49_tabheader_0" name="tab_settings" tabNumber="49" tabIndex="287">Dados da tarefa</SPAN>';
		 htm[i++]='<SPAN class="tab" id="49_tabheader_1" name="tab_vars" tabNumber="49" tabIndex="287">Atributos na tarefa</SPAN>';
		 htm[i++]='<SPAN class="tab" id="49_tabheader_2" name="tab_alerts" tabNumber="49" tabIndex="287">Alertas na tarefa</SPAN>';
    if ( node.nodeName == "send" || node.nodeName == "createMessage" )
	{     
         htm[i++]='<SPAN class="tab" id="49_tabheader_3" name="tab_delegate" tabNumber="49" tabIndex="288">Para</SPAN>';
         htm[i++]='<SPAN class="tab" id="49_tabheader_5" name="tab_message" tabNumber="49" tabIndex="289">Mensagem</SPAN>';
         htm[i++]='<SPAN class="tab" id="49_tabheader_7" name="tab_anexos" tabNumber="49" tabIndex="289">Anexos</SPAN>';
         htm[i++]='<SPAN class="tab" id="49_tabheader_9" name="tab_opcoes" tabNumber="49" tabIndex="289">Opções</SPAN>';
         
    }
    if ( node.nodeName == "waitResponse" )
	{     
         htm[i++]='<SPAN class="tab" id="49_tabheader_10" name="tab_wait" tabNumber="49" tabIndex="289">Resposta</SPAN>';
         
    }
         htm[i++]='</TD>';
         htm[i++]='</TR>';
         htm[i++]='</TBODY>';
         htm[i++]='</TABLE>';
         htm[i++]='<HR class="tabGlow" id="hrSelTab49"/>';
         htm[i++]='<HR class="tabGlow" id="hrTab49"/>';
         htm[i++]='</TD>';
         htm[i++]='</TR>';
         htm[i++]='<TR>';
         htm[i++]='<TD>';
         htm[i++]='<DIV id="49_tabbody_0" class="tab" style="overflow-y:auto">';
         
	
	

	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	if ( node.nodeName == 'decision' || node.nodeName =='choice' || node.nodeName =='menu')
	{

		htm[i++]="<tr><td>"
		htm[i++]="Questão";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=questionHTML;
		htm[i++]="</td></tr>";
		
		if ( node.nodeName == 'decision' )
		{
			
			htm[i++]="<tr><td>"
			htm[i++]="Regras da decisão";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=bouiAppHTML;
			htm[i++]="</td></tr>";
			
		}
			

	}
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Participante";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=participantHTML;
	htm[i++]="</td></tr>";
	
  //executante
  htm[i++]="<tr><td>"
	htm[i++]="Executante";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=executanteHTML;
	htm[i++]="</td></tr>";

	htm[i++]="<tr><td>"
	htm[i++]="Processo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=processHTML;
	htm[i++]="</td></tr>";


	if ( node.nodeName == 'userCallProgram')
	{

		htm[i++]="<tr><td>"
		htm[i++]="Modo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=modeHTML;
		htm[i++]="</td></tr>";

		htm[i++]="<tr><td>"
		htm[i++]="Filtro dos programas";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=programFilterHTML;
		htm[i++]="</td></tr>";
		

	}  


//deadLineDate
//
// numero;day/hour/minute;linear/util;afterCreate/afterEnd;__program/__task/[name task] 
//
//
	
	var deadLineSplit = deadLineDate.split(";")
	
	
	var deadNumber = deadLineSplit[0]?deadLineSplit[0]:"";
	var deadTime = deadLineSplit[1]?deadLineSplit[1]:"";
	var deadTimeMode = deadLineSplit[2]?deadLineSplit[2]:"";
	var deadTimeConstraint = deadLineSplit[3]?deadLineSplit[3]:"";
	var deadTimeReference = deadLineSplit[4]?deadLineSplit[4]:"";
	
	
	var deadNumberHTML = createFieldNumber( deadNumber ,"deadNumber_"+sid ,"deadNumber_"+sid ,"1","","0",false,99999,0,false,true,"changeAtr",false,false);

    var deadTimeInt=["day","hour","minute"];
	var deadTimeExt=["Dia(s)","Hora(s)","Minuto(s)"];
	var deadTimeHTML=createFieldCombo(deadTime,"deadTime_"+sid,"deadTime_"+sid,"1", deadTimeExt , deadTimeInt ,false,false,"changeAtr" );
	
	var deadTimeModeInt=["linear","util"];
	var deadTimeModeExt=["Calendário","Úteis"];
	var deadTimeModeHTML=createFieldCombo(deadTimeMode,"deadTimeMode_"+sid,"deadTimeMode_"+sid,"1", deadTimeModeExt , deadTimeModeInt ,false,false,"changeAtr" );
    
	var deadTimeConstraintInt=["afterCreate","afterEnd"];
	var deadTimeConstraintExt=["Após o lançamento","Após o fim"];
	var deadTimeConstraintHTML=createFieldCombo(deadTimeConstraint,"deadTimeConstraint_"+sid,"deadTimeConstraint_"+sid,"1", deadTimeConstraintExt , deadTimeConstraintInt ,false,false,"changeDeadTimeConstraint" );
    
    
    
	var deadTimeReferenceInt=["__program","__task"];
	var deadTimeReferenceExt=["Do programa","Desta tarefa"];

	 var nodes = TREE_EDIT.code.selectNodes("//*");
	 for ( var p = 0 ; p < nodes.length && sid!= nodes(p).getAttribute("sid");p++ )
	 {	
		var name=nodes(p).getAttribute("name");
		var nodeName =nodes(p).nodeName;
		
	    if ( name && nodes(p).selectSingleNode('label') && 
	        ( 
	        nodeName  =='menu' || nodeName  =='choice' || nodeName =='decision'  || nodeName=='activity' || nodeName=='fillVariable' 
	        || nodeName =='send' || nodeName =='createMessage'  || nodeName=='waitResponse' || nodeName=='userCallProgram' )
	    
	     )
		{
			deadTimeReferenceInt[ deadTimeReferenceInt.length ]=name;
			deadTimeReferenceExt[ deadTimeReferenceExt.length ]="TAREFA:"+nodes(p).selectSingleNode('label').text;
	    }
	}
	
	var deadTimeReferenceHTML=createFieldCombo(deadTimeReference,"deadTimeReference_"+sid,"deadTimeReference_"+sid,"1", deadTimeReferenceExt , deadTimeReferenceInt ,false,false,"changeAtr" );
    
	
	htm[i++]="<tr><td>"
	htm[i++]="Limite de Execução";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]="<table cellpadding=0 cellspacing=0 style='table-layout:fixed' >"
	
	htm[i++]="<col width=60px/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col />";
	
	htm[i++]="<tr><td>";
	htm[i++]=deadNumberHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeModeHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeConstraintHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td id='dtRfr'>";
	htm[i++]=deadTimeReferenceHTML;
	htm[i++]="</td>";
	
	htm[i++]="</td></tr></table>"
	htm[i++]="</td></tr>";
	
	

	
	htm[i++]="<tr><td>"
	htm[i++]="Opcional";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=optionalHTML;
	htm[i++]="</td></tr>";

  htm[i++]="<tr><td>"
	htm[i++]="Passo Único";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=oneshotHTML;
	htm[i++]="</td></tr>";

  htm[i++]="<tr><td>"
	htm[i++]="Mostrar Informação";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=showTask;
	htm[i++]="</td></tr>";

  htm[i++]="<tr><td>"
	htm[i++]="Reassignamentos";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=showReassignHTML;
	htm[i++]="</td></tr>";

  htm[i++]="<tr><td>"
	htm[i++]="Mostrar Opções de Fluxo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=showWorkflowAreaHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Async";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=asyncHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Nome interno";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr HEIGHT=100PX><td valign=top>"
	htm[i++]="Descrição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=descriptionHTML;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Duração do trabalho";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=durationHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Tempo total para executar tarefa";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=durationTTCHTML;
	htm[i++]="</td></tr>";

	htm[i++]="</table>";
	
	htm[i++]="</DIV>";
    
    htm[i++]='<DIV id="49_tabbody_1" class="tab">';
	//para as variaveis e participantes
	htm[i++] ="<table style='width:100%;height:100%' cellpadding=0 cellspacing=0>";
	htm[i++] ="<tr><td valign=top align=left style='width:50%'><div style='width:100%;height:100%;overflow:auto'>";
	//end hader
	
	i= renderActivityVars( node, sid , htm,i);
	
	//para as variaveis e participantes BODY DE VARIAVEIS
	htm[i++] ="</div></td>"
	
	htm[i++] ="<td width='4' ><img class='rszLeft ui-rszLeft' src='ieThemes/0/splitter/resize-dot.gif' WIDTH='1' HEIGHT='1'></td>";
	htm[i++] ="<td valign=top align=left>";
	
	htm[i++] ="<div id='defVarActivity' style='width:100%;height:100%;overflow:auto'>";
	htm[i++] ="&nbsp;"
	htm[i++] ="</div></td></tr></table>";
	//end hader

   	 htm[i++]="</DIV>";
	
	
	htm[i++]='<DIV id="49_tabbody_2" class="tab">';
	//para os alertas
	htm[i++] ="<table style='width:100%;height:100%' cellpadding=0 cellspacing=0>";
	htm[i++] ="<tr><td valign=top align=left style='width:100%'>";
	//end hader
	
	i= renderActivityAlerts( node, sid , htm,i);
	
	//para as variaveis e participantes BODY DE VARIAVEIS
	
	htm[i++] ="</td></tr></table>";
	//end hader

   	 htm[i++]="</DIV>";
   	 
	
	if ( node.nodeName == "send" || node.nodeName == "createMessage" )
	{
	
		 var from = node.selectSingleNode("from").getAttribute("name");

		 var message = node.selectSingleNode("message").getAttribute("name");
		 message = message == "null" ? "":message;
		
		 var messageFromSid = node.selectSingleNode("message").getAttribute("refered_sid");
		 if ( messageFromSid )
		 {
			message="SID:"+messageFromSid;
		 }
		 var channel = node.selectSingleNode("channel").text;
		 var requireDeliveryReceipt = node.selectSingleNode("requireDeliveryReceipt").text;
		 var requireReadReceipt = node.selectSingleNode("requireReadReceipt").text;
		 var priority = node.selectSingleNode("priority").text;
		 var channelInt=["Mail","Sgis","Letter","Fax","Phone","Conversation",""];
		 var channelExt=["Email","SGIS","Carta","Fax","Telefone","Conversa","Genérica"];

		 var fromHTML = "<b>não existem participantes definidos</b>";
		 var msgHTML = "<b>não existem mensagens definidas</b>";
		 
		 var pos = 0;
		 for ( var p=0 ; p < variables.childNodes.length ; p++ )
		 {
			if(variables.childNodes(p).nodeName == 'defMessage')
			{
			    msgDefInt[ pos ] = variables.childNodes(p).getAttribute("name");
				msgDefExt[ pos ] = variables.childNodes(p).selectSingleNode('label').text;
			    pos++;
			}
		 }
		 if ( node.nodeName == "send" )
		 {
			//acrescentar as tarefas CREATEMESSAGE
			 
			 var createMessages=TREE_EDIT.code.selectNodes("*//createMessage");
			 for ( var p=0; p< createMessages.length ; p++ )
			 {
				msgDefInt[ pos ] = "SID:"+createMessages[p].getAttribute("sid");
				msgDefExt[ pos ] = "Mensagem criada em :"+createMessages[p].selectSingleNode("label").text;
			    pos++;
				
			 }
			
		 }

			 
		 if ( partInt.length > 0 )
		 {
			fromHTML=createFieldCombo(from,"from_"+sid,"from_"+sid,"1", partExt , partInt ,false,false,"changeAtr" );	
		 }
 		 if ( msgDefInt.length > 0 )
 		 {
			msgHTML=createFieldCombo(message,"message_"+sid,"message_"+sid,"1", msgDefExt , msgDefInt ,false,false,"changeAtr" );	
		 }
		 channelHTML=createFieldCombo(channel,"channel_"+sid,"channel_"+sid,"1",channelExt , channelInt ,false,false,"changeAtr" );
	
		 htm[i++]='<DIV id="49_tabbody_3" class="tab">';
			 
		 htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
		 htm[i++]="<col width=150px />"
		 htm[i++]="<col width=100% />"
			 
		 var priorityExt=["Normal","Alta","Baixa"];
		 var priorityInt=["2","3","1"];
    
		 var requireDeliveryReceiptHTML = createFieldCombo(requireDeliveryReceipt,"requireDeliveryReceipt_"+sid,"requireDeliveryReceipt_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );
		 var requireReadReceiptHTML = createFieldCombo(requireReadReceipt,"requireReadReceipt_"+sid,"requireReadReceipt_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );
		 var priorityHTML = createFieldCombo(priority,"priority_"+sid,"priority_"+sid,"1", priorityExt , priorityInt ,false,false,"changeAtr" );
    
			 
		 htm[i++]="<tr><td colspan=2 class='readSectionCaption' >"
		 htm[i++]="Remetente";
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="<tr><td colspan=2>"
		 htm[i++]=fromHTML;
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="<tr><td colspan=2 class='readSectionCaption'>"
		 htm[i++]="Para";
		 htm[i++]="</td></tr>";
			 
		 for( j=0 ; j< partExt.length ; j++ )
		 {
			htm[i++]="<tr><td colspan=2 valign=top'>"
			htm[i++]="<input onclick='toggleSend(\"to\",\""+sid+"\",\""+partInt[j]+"\")' style='border:0' ";
			if ( existSend('to',partInt[j],node) )
			{
				htm[i++]=" checked "
			}
			htm[i++]=" type=checkbox />&nbsp;"+partExt[j];
				
			htm[i++]="</td></tr>";
			 
		 }
			 
			 
		 htm[i++]="<tr><td colspan=2 class='readSectionCaption' >"
		 htm[i++]="CC";
		 htm[i++]="</td></tr>";
			 
		 for( j=0 ; j< partExt.length ; j++ )
		 {
			htm[i++]="<tr><td colspan=2 valign=top'>"
			htm[i++]="<input onclick='toggleSend(\"cc\",\""+sid+"\",\""+partInt[j]+"\")' style='border:0' ";
			if ( existSend('cc',partInt[j],node) )
			{
				htm[i++]=" checked "
			}
			htm[i++]=" type=checkbox >&nbsp;"+partExt[j];
				
			htm[i++]="</td></tr>";
			 
		 }
			 
		 htm[i++]="<tr><td colspan=2 style='color:#D92F01;border-bottom:1px solid #CCCCCC'>"
		 htm[i++]="BCC";
		 htm[i++]="</td></tr>";
			 
		 for( j=0 ; j< partExt.length ; j++ )
		 {
			htm[i++]="<tr><td colspan=2 valign=top'>"
			htm[i++]="<input onclick='toggleSend(\"bcc\",\""+sid+"\",\""+partInt[j]+"\")' style='border:0' ";
			if ( existSend('bcc',partInt[j],node) )
			{
				htm[i++]=" checked "
			}
			htm[i++]=" type=checkbox >&nbsp;"+partExt[j];
				
			htm[i++]="</td></tr>";

			 
		 }

			 
			 
			 
		 htm[i++]="</table>";
	
		 htm[i++]="</DIV>";
		 
		 
		 htm[i++]='<DIV id="49_tabbody_5" class="tab"><table cellpadding=0 cellspacing=0 class="section">';
		 htm[i++]="<col width=150px/>";
		 htm[i++]="<col width=100%/>";
		 
		 htm[i++]="<tr height='25px'><td>";	
		 htm[i++]="Mensagem</td><td>";
		 htm[i++]=msgHTML;
		 htm[i++]="</td></tr>";	
	     htm[i++]="<tr height='25px'><td>";	
		 htm[i++]="Canal</td><td>";	
		 htm[i++]=channelHTML;
		 htm[i++]="</td></tr>";
		 htm[i++]="</table>";	
		 htm[i++]="</DIV>";
			 
		 htm[i++]='<DIV id="49_tabbody_7" class="tab">';
		 //anexos
    
		var defVars = TREE_EDIT.code.selectSingleNode("//defVariables");
	
		htm[i++] ="<table style='table-layout:fixed;width:100%' cellpadding=0 cellspacing=0>";
		htm[i++] ="<colgroup><col width=30><col width=30><col width=100%>";
		htm[i++] ="</colgroup>";
				
		for ( var p = 0 ; p < defVars.childNodes.length;p++ )
		{
      if("defMessage" != defVars.childNodes(p).nodeName)
      {
  			var nameVar = defVars.childNodes(p).getAttribute("name");
  			var sidVar = defVars.childNodes(p).getAttribute("sid");
  			var labelVar = defVars.childNodes(p).selectSingleNode("label").text
  			img = elements_getImg(defVars.childNodes(p));
  			var exists = false;
  			var variables = node.selectSingleNode("attachVars");
  			for ( j= 0; j< variables.childNodes.length ; j++ )
  			{
  				var nameVarAct = variables.childNodes(j).getAttribute("name")
  				if ( nameVarAct == nameVar )
  				{
  				   exists=true;
  				   break;
  				}
  			}
  				
  				
  			if ( img.indexOf("/")> -1 )
  			{
  				
  			}
  			else
  			{
  				img =WKFL_TEMA_DIR+img+" height=24 width=24 ";
  			}
  				
  						
  	
  				
  				
  			htm[i++] ="<tr>";
  			htm[i++] ="<td><img src=";
  			htm[i++] =img;
  			htm[i++] =" /></td>";
  				
  			htm[i++] ="<td><input class='rad' onclick='"
  			htm[i++] ="toggleVarAct(\""+sid+"\",\""+sidVar+"\",\"attachVars\")";
  			htm[i++] ="' type='checkbox' ";
  			if( exists )
  			{
  			htm[i++] = " checked ";
  			}
  				
  			htm[i++] = " / id='checkbox'1 name='checkbox'1>";
  				
  			htm[i++] ="</td>";
  				
  			htm[i++] ="<td>";
  			htm[i++] = labelVar;
  			htm[i++] ="</td>";
  				
  			htm[i++] ="</tr>";
			}
				
				
				
		}
			 
		 htm[i++] ="</table>";
			 
			 
		 //fim anexos
		 htm[i++]="</DIV>";
			 
			 
		 htm[i++]='<DIV id="49_tabbody_9" class="tab">';
		 //opções
		 htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
		 htm[i++]="<col width=150px />"
		 htm[i++]="<col width=100% />"
			 
		 htm[i++]="<tr><td>"
		 htm[i++]="Recibo de entrega";
		 htm[i++]="</td>";
		 htm[i++]="<td>";
		 htm[i++]=requireDeliveryReceiptHTML;
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="<tr><td>"
		 htm[i++]="Recibo de Leitura";
		 htm[i++]="</td>";
		 htm[i++]="<td>";
		 htm[i++]=requireReadReceiptHTML;
		 htm[i++]="</td></tr>";
			 
			 
		 htm[i++]="<tr><td>"
		 htm[i++]="Prioridade";
		 htm[i++]="</td>";
		 htm[i++]="<td>";
		 htm[i++]=priorityHTML;
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="</table";
			  
			  
		 htm[i++]='</DIV>';
			 
	}
	
	
	
	if ( node.nodeName == "waitResponse" )
	{
		 var from = node.selectSingleNode("from").getAttribute("name");
		 var responseMessageVariable = node.selectSingleNode("responseMessageVariable").getAttribute("name");
		 var sendID = node.selectSingleNode("sendID").text;
		 var fromHTML = "<b>não existem participantes definidos</b>";
		 var sendIDHTML = "<b>não existem tarefas do tipo ENVIO DE MENSAGEM ou não tem o nome interno preenchido</b>";
		 
		 if ( partInt.length > 0 )
		 {
			fromHTML=createFieldCombo(from,"from_"+sid,"from_"+sid,"1", partExt , partInt ,false,false,"changeAtr" );	
		 } 
			 
		 
		 var sendExt=[];
		 var sendInt=[];
		 
		 var nodes = TREE_EDIT.code.selectNodes("//*");
		 for ( var p = 0 ; p < nodes.length;p++ )
		 {	
			var name=nodes(p).getAttribute("name");
			var nodeName=nodes[p].nodeName
			if ( p == 0 )
			{
				sendInt[sendInt.length]="";
				sendExt[sendExt.length]="&nbsp;";
			}
		    if ( name &&  nodeName =='send' )
			{
				sendInt[sendInt.length]=name;
				sendExt[sendExt.length]=nodes(p).selectSingleNode('label').text;
		    }
		 }
		 
		 if ( sendInt.length > 0 )
		 {
			sendIDHTML=createFieldCombo(sendID,"sendID_"+sid,"sendID_"+sid,"1", sendExt , sendInt ,false,false,"changeAtr" );	
		 } 
	
	
	
		 htm[i++]='<DIV id="49_tabbody_10" class="tab">';
			 
		 htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
		 htm[i++]="<col width=150px />"
		 htm[i++]="<col width=100% />"
			 
		 
			 
		 htm[i++]="<tr><td colspan=2 class='readSectionCaption' >"
		 htm[i++]="Resposta do Participante";
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="<tr><td colspan=2>"
		 htm[i++]=fromHTML;
		 htm[i++]="</td></tr>";
		 
		 htm[i++]="<tr><td colspan=2 class='readSectionCaption' >"
		 htm[i++]="Á mensagem";
		 htm[i++]="</td></tr>";
			 
		 htm[i++]="<tr><td colspan=2>"
		 htm[i++]=sendIDHTML;
		 htm[i++]="</td></tr>";
		 
		 var defVars = TREE_EDIT.code.selectSingleNode("//defVariables");
		 
		 var varInt=[];
		 var varExt=[];		
		 
		 for ( var p = 0 ; p < defVars.childNodes.length;p++ )
		 {
			if(p==0)
			{
			varInt[ varInt.length] = "";
			varExt[ varExt.length ] = "";
			}
			var nameVar = defVars.childNodes(p).getAttribute("name");
			varInt[ varInt.length] = nameVar;
			
			
			var labelVar = defVars.childNodes(p).selectSingleNode("label").text+" ( "+defVars.childNodes(p).getAttribute('name')+" )";
		
			varExt[ varExt.length ] = labelVar;
		 }
		 responseMessageVariableHTML="<b>não existem atributos definidos</b>";
		 if ( varExt.length > 0 )
		 {
		 
			responseMessageVariableHTML=createFieldCombo(responseMessageVariable,"responseMessageVariable_"+sid,"responseMessageVariable_"+sid,"1", varExt , varInt ,false,false,"changeAtr" );	
		 } 
		 
		 htm[i++]="<tr><td>"
		 htm[i++]="Coloca mensagem resposta no atributo";
		 htm[i++]="</td>";
		 htm[i++]="<td>";
		 htm[i++]=responseMessageVariableHTML;
		 htm[i++]="</td></tr>";
		 
	
			 
		 htm[i++]="</table>";
			  
		 htm[i++]='</DIV>';
			 
	}
			 
	htm[i++]='</TD>'
	htm[i++]='</TR>'
	htm[i++]='</TBODY>'
	htm[i++]='</TABLE>';
	
	
	workCell.innerHTML= htm.join("");
	winmain().ndl[getIDX()].focusfields =null;
		toFocus=null
		_hsos['49']=null;
		
		so('49');
		_hso.oTab=document.getElementById('49_tabheader_0');
		
	if ( node.nodeName == 'decision' || node.nodeName =='choice' || node.nodeName =='menu' )
	{
		//document.getElementById( "question_"+sid ).focus();		
		window.setTimeout("try{ document.getElementById( 'question_"+sid +"').focus()}catch(e){}",100);
	}
	
	else // if ( node.nodeName == 'send'  )
	{
		
		
		
		//document.getElementById( "question_"+sid ).focus();		
		window.setTimeout("try{ document.getElementById( 'label_"+sid +"').focus()}catch(e){}",100);
	}
	
	//else  document.getElementById( "label_"+sid ).focus()
	
}

function changeDeadTimeConstraint()
{
	var sid =window.event.srcElement.name.split("_")[1];
	var deadTimeConstraint = getValue( "deadTimeConstraint_"+sid );
	var deadTimeReference = getValue( "deadTimeReference_"+sid );
	
	if ( deadTimeConstraint == 'afterEnd' )
	{
	   var deadTimeReferenceInt=[];
		var deadTimeReferenceExt=[];
		if( deadTimeReference=="__program" || deadTimeReference=="__task")
		{
		deadTimeReference="";
		}
	
	}
	else
	{
	
		var deadTimeReferenceInt=["__program","__task"];
		var deadTimeReferenceExt=["Do programa","Desta tarefa"];
	}

	 var nodes = TREE_EDIT.code.selectNodes("//*");
	 for ( var p = 0 ; p < nodes.length && sid!= nodes(p).getAttribute("sid");p++ )
	 {	
		var name=nodes(p).getAttribute("name");
		var nodeName =nodes(p).nodeName;
		
	    if ( name && nodes(p).selectSingleNode('label') && 
	        ( 
	        nodeName  =='menu' || nodeName  =='choice' || nodeName =='decision'  || nodeName=='activity' || nodeName=='fillVariable' 
	        || nodeName =='send' || nodeName =='createMessage'  || nodeName=='waitResponse' || nodeName=='userCallProgram' )
	    
	     )
		{
			deadTimeReferenceInt[ deadTimeReferenceInt.length ]=name;
			deadTimeReferenceExt[ deadTimeReferenceExt.length ]="TAREFA:"+nodes(p).selectSingleNode('label').text;
	    }
	}
	
	var deadTimeReferenceHTML=createFieldCombo(deadTimeReference,"deadTimeReference_"+sid,"deadTimeReference_"+sid,"1", deadTimeReferenceExt , deadTimeReferenceInt ,false,false,"changeAtr" );
    
	document.getElementById("dtRfr").innerHTML=deadTimeReferenceHTML;
	
	
}

function renderActivityVars( node,sid, htm , i )
{
	htm[i++] ="<table style='table-layout:fixed;' cellpadding=0 cellspacing=0>";
	htm[i++] ="<colgroup><col width=30/><col width=30/><col/>";
	htm[i++]="</colgroup>";


	var defVars = TREE_EDIT.code.selectSingleNode("//defVariables");
	for ( var p = 0 ; p < defVars.childNodes.length;p++ )
	{
    if("defMessage" == defVars.childNodes(p).nodeName)
    {

    }
    else
    {
  		var nameVar = defVars.childNodes(p).getAttribute("name");
  		var sidVar = defVars.childNodes(p).getAttribute("sid");
  		var labelVar = defVars.childNodes(p).selectSingleNode("label").text
  		img = elements_getImg(defVars.childNodes(p));
  		var exists = false;
  		var variables = node.selectSingleNode("variables");
  		
  		for ( j= 0; j< variables.childNodes.length ; j++ )
  		{
  			var nameVarAct = variables.childNodes(j).getAttribute("name")
  			if ( nameVarAct == nameVar && variables.childNodes(j).tagName=="variable" )
  			{
  			   exists=true;
  			   break;
  			}
  		}
  		if ( img.indexOf("/")> -1 )
  		{
  		
  		}
  		else
  		{
  			img =WKFL_TEMA_DIR+img+" height=24 width=24 ";
  		}
  		
      
      htm[i++]="<tr onclick='clickOnRowVarActivity(\"";
      htm[i++]=sid;
  		htm[i++]="\",\"";
  		htm[i++]=sidVar;
  		htm[i++]="\")'>";

  		htm[i++]="<td class='underline'><img src=";
  		htm[i++]=img;
  		htm[i++]=" /></td>";
  		
  		htm[i++]="<td class='underline'><input class='rad' onclick='"      
      htm[i++]="toggleVarAct(\""+sid+"\",\""+sidVar+"\")";

  		htm[i++]="' type='checkbox' ";
  		if( exists )
  		{
  		htm[i++]= " checked ";
  		}
  		
  		htm[i++]= " / id='checkbox'1 name='checkbox'1>";
  		
  		htm[i++]="</td>";
  		
  		htm[i++]="<td class='underline'>";
  		htm[i++]= labelVar;
  		htm[i++]="</td>";
  		
  		htm[i++]="</tr>";
    }
	}
	/*----- PARTICIPANTES ---*/
	
	var defVars = TREE_EDIT.code.selectSingleNode("//defParticipants");
	for ( var p = 0 ; p < defVars.childNodes.length;p++ )
	{
		var nameVar = defVars.childNodes(p).getAttribute("name");
		var sidVar = defVars.childNodes(p).getAttribute("sid");
		var labelVar = defVars.childNodes(p).selectSingleNode("label").text
		img = elements_getImg(defVars.childNodes(p));
		var exists = false;
		var variables = node.selectSingleNode("variables");
		
		for ( j= 0; j< variables.childNodes.length ; j++ )
		{
			var nameVarAct = variables.childNodes(j).getAttribute("name")
			if ( nameVarAct == nameVar && variables.childNodes(j).tagName=="participant" )
			{
			   exists=true;
			   break;
			}
		}
		if ( img.indexOf("/")> -1 )
		{
		
		}
		else
		{
			img =WKFL_TEMA_DIR+img+" height=24 width=24 ";
		}
		
		htm[i++]="<tr>";
		
		htm[i++]="<td class='underline'><img src=";
		htm[i++]=img;
		htm[i++]=" /></td>";
		
		htm[i++]="<td class='underline'><input class='rad' onclick='"
		htm[i++]="toggleParAct(\""+sid+"\",\""+sidVar+"\")";
		htm[i++]="' type='checkbox' ";
		if( exists )
		{
		htm[i++]= " checked ";
		}
		
		htm[i++]= " / id='checkbox'1 name='checkbox'1>";
		
		htm[i++]="</td>";
		
		htm[i++]="<td class='underline'>";
		htm[i++]= labelVar;
		htm[i++]="</td>";
		
		htm[i++]="</tr>";
	}
	
	htm[i++]="</table>";
	return i;
}

function renderActivityAlerts( node,sid, htm , i )
{
	htm[i++] ="<table style='table-layout:fixed;' cellpadding=0 cellspacing=0>";
	htm[i++] ="<colgroup><col width=30/><col width=30/><col/>";
	htm[i++]="</colgroup>";


	var defAlerts = TREE_EDIT.code.selectSingleNode("//activityAlerts");
	for ( var p = 0 ; p < defAlerts.childNodes.length;p++ )
	{
		var nameAlert = defAlerts.childNodes(p).getAttribute("name");
		var sidAlert = defAlerts.childNodes(p).getAttribute("sid");
		//var labelVar = defVars.childNodes(p).selectSingleNode("label").text
		img = elements_getImg(defAlerts.childNodes(p));
		var exists = false;
		var alerts = node.selectSingleNode("alerts");
		
		for ( j= 0; j< alerts.childNodes.length ; j++ )
		{
			var nameAlertAct = alerts.childNodes(j).text
			if ( nameAlertAct == nameAlert )
			{
			   exists=true;
			   break;
			}
		}
		
		img =WKFL_TEMA_DIR+img+" height=24 width=24 ";
		
		
		htm[i++]="<tr>";
		htm[i++]="<td class='underline'><img src=";
		htm[i++]=img;
		htm[i++]=" /></td>";
		
		htm[i++]="<td class='underline'><input class='rad' onclick='"
		htm[i++]="toggleAlertAct(\""+sid+"\",\""+sidAlert+"\")";
		htm[i++]="' type='checkbox' ";
		if( exists )
		{
		htm[i++]= " checked ";
		}
		
		htm[i++]= " / id='checkbox'1 name='checkbox'1>";
		
		htm[i++]="</td>";
		
		htm[i++]="<td class='underline'>";
		htm[i++]= nameAlert;
		htm[i++]="</td>";
		
		htm[i++]="</tr>";
	}
	htm[i++]="</table>";
	return i;
}

function toggleAlertAct( sidAct , sidAlert )
{
	var o = window.event.srcElement;
	var search="//*/alert[@sid='"+sidAlert+"']"; //NTRIMJS
	var nodeAlert=xmlSrc.firstChild.selectSingleNode(search);
	
	
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
	
	var alerts = nodeAct.selectSingleNode("alerts");
	

	if ( o.checked )
	{
		//insert
	
		var newalert = xmlSrc.createElement("alertname");
		newalert.text=nodeAlert.getAttribute("name") ;
		alerts.appendChild( newalert );
	}
	else
	{
		//remove
		var nameAlert = nodeAlert.getAttribute("name");
		for ( j= 0; j< alerts.childNodes.length ; j++ )
		{
			var nameAlertAct = alerts.childNodes(j).text;
			if ( nameAlertAct == nameAlert )
			{
			   alerts.removeChild( alerts.childNodes(j) );
			   break;
			}
		}
		
	}
}



function clickOnRowVarActivity( sid , sidVar )
{
   var o=window.event.srcElement;
   while ( o.tagName!='TR' ) o=o.parentElement;
   var isChecked = o.cells[1].firstChild.checked;
   var h = document.getElementById('defVarActivity');
   
   var tbl = o;
   while ( tbl.tagName!='TABLE') tbl=tbl.parentElement;
   
   if ( tbl.rowActive+""!="undefined"  )
   {
	 var r = tbl.rows[parseInt(tbl.rowActive ,10)];
	 r.runtimeStyle.backgroundColor="";
	 if( h.nodeVarActivity )
	 {
		readVarActivity( h.nodeVarActivity , h.nodeVar );
	 }
   }
   o.runtimeStyle.backgroundColor=TREEROW_COLOR_SEL;
   tbl.rowActive = o.rowIndex;
   tbl.varChecked = isChecked;
   
   if ( isChecked )
   {
		var search="//*[@sid='"+sid+"']"; //NTRIMJS
		var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
			
		var search="//*[@sid='"+sidVar+"']"; //NTRIMJS
		var nodeVarDef=xmlSrc.firstChild.selectSingleNode(search);
		var variableName = nodeVarDef.getAttribute("name");
			
		var nodeVariables = nodeAct.selectSingleNode("variables");
			
		var search="*[@name='"+variableName+"']"; //NTRIMJS
		var nodeVarActivity=nodeVariables.selectSingleNode(search);
		
		h.nodeVarActivity= nodeVarActivity;
		h.nodeVar = nodeVarDef;
   
		showRedefineVariable( h , nodeVarActivity , nodeVarDef );
			
   }
   else
   {
	  h.nodeVarActivity= null;
	  h.nodeVar = null;
	  h.innerHTML="&nbsp;";
	  
   }
}
function clickOnRowVarMessage( sid , sidVar )
{
   var o=window.event.srcElement;
   while ( o.tagName!='TR' ) o=o.parentElement;
   var isChecked = o.cells[1].firstChild.checked;
   var h = document.getElementById('defVarActivity');
   
   var tbl = o;
   while ( tbl.tagName!='TABLE') tbl=tbl.parentElement;
   
   if ( tbl.rowActive+""!="undefined"  )
   {
	 var r = tbl.rows[parseInt(tbl.rowActive ,10)];
	 r.runtimeStyle.backgroundColor="";
	 if( h.nodeVarActivity )
	 {
		readVarMessage( h.nodeVarActivity , h.nodeVar );
	 }
   }
   o.runtimeStyle.backgroundColor=TREEROW_COLOR_SEL;
   tbl.rowActive = o.rowIndex;
   tbl.varChecked = isChecked;
   
   if ( isChecked )
   {
		var search="//*[@sid='"+sid+"']"; //NTRIMJS
		var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
			
		var search="//*[@sid='"+sidVar+"']"; //NTRIMJS
		var nodeVarDef=xmlSrc.firstChild.selectSingleNode(search);
		var variableName = nodeVarDef.getAttribute("name");
			
		var nodeVariables = nodeAct.selectSingleNode("variables");
			
		var search="*[@name='"+variableName+"']"; //NTRIMJS
		var nodeVarActivity=nodeVariables.selectSingleNode(search);
		
		h.nodeVarActivity= nodeVarActivity;
		h.nodeVar = nodeVarDef;
   
		showRedefineMessage( h , nodeVarActivity , nodeVarDef );
			
   }
   else
   {
	  h.nodeVarActivity= null;
	  h.nodeVar = null;
	  h.innerHTML="&nbsp;";
	  
   }
}
function readVarActivity( nodeVarActivity, nodeVar )
{
  if("defMessage" == nodeVar.nodeName)
  {
    return readVarMessage(nodeVarActivity, nodeVar);
  }
	var sid = "0";
	var label = getValue( "label_"+sid );
	var description = getValue( "description_"+sid );
	var valid = getValue( "valid_"+sid );
	var hiddenWhen = getValue( "hiddenWhen_"+sid );
	var mode = getValue( "mode_"+sid );
	var required = getValue( "required_"+sid );
	var objectfilter = getValue("objectfilter_"+sid);
	var showMode = getValue("showMode_"+sid);
	var validBusiness = getValue("validBusiness_"+sid);
	var validDB = getValue("validDB_"+sid);
  var processTemplate = getValue("processTemplate_"+sid);
	
	var vv;
	var names=[];
	names[ names.length ] ="label";
	names[ names.length ] ="description";
	names[ names.length ] ="valid";
	names[ names.length ] ="hiddenWhen";
	// a partir daqui não é CDATA
	names[ names.length ] ="mode";
	names[ names.length ] ="required";
	names[ names.length ] ="objectfilter";
	names[ names.length ] ="showMode";
	names[ names.length ] ="validBusiness";
	names[ names.length ] ="validDB";
	
	for( var i = 0 ; i < names.length; i++ )
	{
		var hvar = getValue(  names[i] +"_"+sid );
		var vv = nodeVar.selectSingleNode( names[i] ).text;
		if ( hvar && vv != hvar )
		{
			if ( names[i] == "objectfilter" )
			{
				nodeVarActivity.selectSingleNode("objectfilter").selectSingleNode("xeoql").firstChild.text=hvar;
			}
			else if ( i <=3 )
			{
				nodeVarActivity.selectSingleNode( names[i] ).firstChild.text=hvar;
			}
			else
			{
				nodeVarActivity.selectSingleNode( names[i] ).text=hvar;
			}
		}
		else
		{
			nodeVarActivity.removeChild( nodeVarActivity.selectSingleNode( names[i] ) );
		}
	}
  nodeVarActivity.setAttribute("processTemplate", processTemplate);
	readMethods( nodeVarActivity ,true );
		
	return true;

}
function readVarMessage( nodeVarActivity, nodeVar )
{
	var sid = "0";
  var processTemplate = getValue("processTemplate_"+sid);
  nodeVarActivity.setAttribute("processTemplate", processTemplate);
	return true;

}
function showRedefineVariable( htmNode , nodeVarActivity , nodeVarDef )
{
	var nva=nodeVarActivity;
	var v = nodeVarDef;
	// check if nodeVarActivity have all elements
	if ( nva.selectSingleNode("label") == null )
	{
		var t = nodeVarDef.selectSingleNode("label").text;
		nva.appendChild( newElement("label", t , xmlSrc , true ) );
	}
	if ( nva.selectSingleNode("description") == null )
	{
		var t = nodeVarDef.selectSingleNode("description").text;
		nva.appendChild( newElement("description",t , xmlSrc , true ) );
	}
	if ( nva.selectSingleNode("objectfilter") == null )
	{
  		var xeoQL = nodeVarDef.selectSingleNode("objectfilter").firstChild.text;
  		var objectfilter = xmlSrc.createElement("objectfilter");
  		objectfilter.appendChild( newElement("xeoql", xeoQL , xmlSrc , true ));
  		nva.appendChild( objectfilter );
	}
	if ( nva.selectSingleNode("valid") == null )
	{
		var t = nodeVarDef.selectSingleNode("valid").text;
		nva.appendChild( newElement("valid", t , xmlSrc , true ) );
	}
	if ( nva.selectSingleNode("hiddenWhen") == null )
	{
		var t = nodeVarDef.selectSingleNode("hiddenWhen").text;
		nva.appendChild( newElement("hiddenWhen", t , xmlSrc , true ) );
	}
	if ( nva.selectSingleNode("required") == null )
	{
		var t = nodeVarDef.selectSingleNode("required").text;
		nva.appendChild( newElement("required", t , xmlSrc , false ) );
	}
	if ( nva.selectSingleNode("validDB") == null )
	{
		var t = nodeVarDef.selectSingleNode("validDB").text;
		nva.appendChild( newElement("validDB", t , xmlSrc , false ) );
	}
	if ( nva.selectSingleNode("validBusiness") == null )
	{
		var t = nodeVarDef.selectSingleNode("validBusiness").text;
		nva.appendChild( newElement("validBusiness", t , xmlSrc , false ) );
	}
	if ( nva.selectSingleNode("showMode") == null )
	{
		var t = nodeVarDef.selectSingleNode("showMode").text;
		nva.appendChild( newElement("showMode", t , xmlSrc , false ) );
	}
	if ( nva.selectSingleNode("mode") == null )
	{
		var t = nodeVarDef.selectSingleNode("mode").text;
		nva.appendChild( newElement("mode", t , xmlSrc , false ) );
	}
	if ( nva.selectSingleNode("availableMethods") == null )
	{
		nva.appendChild( xmlSrc.createElement("availableMethods") );
	}
  if ( nva.selectSingleNode("keyWords") == null )
	{
		nva.appendChild( xmlSrc.createElement("keyWords") );
	}
	if ( nva.selectSingleNode("hiddenMethods") == null )
	{
		nva.appendChild( xmlSrc.createElement("hiddenMethods") );
	}
	if ( nva.selectSingleNode("requiredMethods") == null )
	{
		nva.appendChild( xmlSrc.createElement("requiredMethods") );
	}    
  elements_showDEFVARIABLE( nva , htmNode , nodeVarDef );
}

function showRedefineMessage( htmNode , nodeVarActivity , nodeVarDef )
{
	var nva=nodeVarActivity;
	var v = nodeVarDef;
	// check if nodeVarActivity have all elements
	if ( nva.selectSingleNode("label") == null )
	{
		var t = nodeVarDef.selectSingleNode("label").text;
		nva.appendChild( newElement("label", t , xmlSrc , true ) );
	}
	if ( nva.selectSingleNode("description") == null )
	{
		var t = nodeVarDef.selectSingleNode("description").text;
		nva.appendChild( newElement("description",t , xmlSrc , true ) );
	}
  if ( nva.selectSingleNode("subject") == null )
	{
		var t = nodeVarDef.selectSingleNode("subject").text;
		nva.appendChild( newElement("subject",t , xmlSrc , true ) );
	}
  if ( nva.selectSingleNode("message") == null )
	{
		var t = nodeVarDef.selectSingleNode("message").text;
		nva.appendChild( newElement("message",t , xmlSrc , true ) );
	}
  if ( nva.selectSingleNode("objTemplate") == null )
	{
		nva.appendChild( xmlSrc.createElement("objTemplate") );
	}  
	
  elements_showDEFMESSAGE( nva , htmNode , nodeVarDef );
}

function toggleVarAct( sidAct , sidVar , nodeNameVar )
{
	var o = window.event.srcElement;
	var search="//*/defVariable[@sid='"+sidVar+"']"; //NTRIMJS
	var nodeVar=xmlSrc.firstChild.selectSingleNode(search);

	if ( !nodeNameVar )
	{
	  nodeNameVar = "variables";
	}
	
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
	
	var variables = nodeAct.selectSingleNode(nodeNameVar);
	var nodeNameVarSingle=nodeNameVar.substr(0,nodeNameVar.length-1);

	if ( o.checked )
	{
		//insert
	
		var newvar = xmlSrc.createElement(nodeNameVarSingle);
		newvar.setAttribute("name", nodeVar.getAttribute("name") );
		var proct = nodeVar.getAttribute("processTemplate");
		if(proct==null) proct="n";
		newvar.setAttribute("processTemplate", proct );
		variables.appendChild( newvar );
	}
	else
	{
		//remove
		var nameVar = nodeVar.getAttribute("name");
		for ( j= 0; j< variables.childNodes.length ; j++ )
		{
			var nameVarAct = variables.childNodes(j).getAttribute("name")
			if ( nameVarAct == nameVar && variables.childNodes(j).tagName==nodeNameVarSingle )
			{
			   variables.removeChild( variables.childNodes(j) );
			   break;
			}
		}
		
	}
}

function toggleParAct( sidAct , sidVar )
{
	var o = window.event.srcElement;
	var search="//*/defParticipant[@sid='"+sidVar+"']"; //NTRIMJS
	var nodeVar=xmlSrc.firstChild.selectSingleNode(search);
	
	var nodeNameVar = "variables";
	
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
	
	var variables = nodeAct.selectSingleNode(nodeNameVar);
	var nodeNameVarSingle="participant";

	if ( o.checked )
	{
		//insert
	
		var newvar = xmlSrc.createElement(nodeNameVarSingle);
		newvar.setAttribute("name", nodeVar.getAttribute("name") );
		variables.appendChild( newvar );
	}
	else
	{
		//remove
		var nameVar = nodeVar.getAttribute("name");
		for ( j= 0; j< variables.childNodes.length ; j++ )
		{
			var nameVarAct = variables.childNodes(j).getAttribute("name")
			if ( nameVarAct == nameVar && variables.childNodes(j).tagName==nodeNameVarSingle )
			{
			   variables.removeChild( variables.childNodes(j) );
			   break;
			}
		}
		
	}
}


function toggleDefMessage( sidAct , sidVar )
{
	var o = window.event.srcElement;
	var search="//*/defMessage[@sid='"+sidVar+"']"; //NTRIMJS
	var nodeVar=xmlSrc.firstChild.selectSingleNode(search);

	var nodeNameVar = "variables";
	
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var nodeAct=xmlSrc.firstChild.selectSingleNode(search);
	
	var variables = nodeAct.selectSingleNode(nodeNameVar);
	var nodeNameVarSingle=nodeNameVar.substr(0,nodeNameVar.length-1);

	if ( o.checked )
	{
		//insert
	
		var newvar = xmlSrc.createElement(nodeNameVarSingle);
		newvar.setAttribute("name", nodeVar.getAttribute("name") );
		variables.appendChild( newvar );
	}
	else
	{
		//remove
		var nameVar = nodeVar.getAttribute("name");
		for ( j= 0; j< variables.childNodes.length ; j++ )
		{
			var nameVarAct = variables.childNodes(j).getAttribute("name")
			if ( nameVarAct == nameVar && variables.childNodes(j).tagName==nodeNameVarSingle )
			{
			   variables.removeChild( variables.childNodes(j) );
			   break;
			}
		}
	}
}


function existSend( what , name , node ) // 'to',partInt[j],node)
{
	var participants = node.selectSingleNode(what);
	
	var toRet= false;
	for ( var i=0 ; i< participants.childNodes.length && !toRet; i++ )
	{
		toRet = participants.childNodes(i).getAttribute("name") == name ;
	}
	return toRet;
}


function toggleSend( what , sidAct ,parName ) // 'to',partInt[j],node)
{
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var node=xmlSrc.firstChild.selectSingleNode(search);

	var o = window.event.srcElement;
	var participants = node.selectSingleNode(what);

	if ( o.checked )
	{
		//insert
		var newpar = xmlSrc.createElement(what+"Participant");
		newpar.setAttribute("name", parName );
		participants.appendChild( newpar );
	}
	else
	{
		//remove
		for ( j= 0; j< participants.childNodes.length ; j++ )
		{
			
			if ( parName == participants.childNodes(j).getAttribute("name") )
			{
			   participants.removeChild( participants.childNodes(j) );
			   break;
			}
		}
	
	}
}

function existExecProc(name , node )
{
	var participants = node.selectSingleNode('participants');
	
	var toRet= false;
	for ( var i=0 ; i< participants.childNodes.length && !toRet; i++ )
	{
		toRet = participants.childNodes(i).getAttribute("name") == name ;
	}
	return toRet;
}

function toggleExecProc( sidAct ,parName ) // 'to',partInt[j],node)
{
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var node=xmlSrc.firstChild.selectSingleNode(search);

	var o = window.event.srcElement;
	var participants = node.selectSingleNode("participants");

	if ( o.checked )
	{
		//insert
		var newpar = xmlSrc.createElement("participant");
		newpar.setAttribute("name", parName );
		participants.appendChild( newpar );
	}
	else
	{
		//remove
		for ( j= 0; j< participants.childNodes.length ; j++ )
		{
			
			if ( parName == participants.childNodes(j).getAttribute("name") )
			{
			   participants.removeChild( participants.childNodes(j) );
			   break;
			}
		}
	}
}

function existProc(name , node ) // 'to',partInt[j],node)
{
	var procedures = node.selectSingleNode('procedures');
	
	var toRet= false;
	for ( var i=0 ; i< procedures.childNodes.length && !toRet; i++ )
	{
		toRet = procedures.childNodes(i).getAttribute("name") == name ;
	}
	return toRet;
}


function toggleProc( sidAct ,procName )
{
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var node=xmlSrc.firstChild.selectSingleNode(search);

	var o = window.event.srcElement;
	var procedures = node.selectSingleNode('procedures');

	if ( o.checked )
	{
		//insert
		var newproc = xmlSrc.createElement("procedure");
		newproc.setAttribute("name", procName );
		procedures.appendChild( newproc );
    if(node.nodeName == 'addProcedures')
    {
      document.getElementById('req' +procName ).disabled=false;
    }
	}
	else
	{
		//remove
		for ( j= 0; j< procedures.childNodes.length ; j++ )
		{

			if ( procName == procedures.childNodes(j).getAttribute("name") )
			{
			   procedures.removeChild( procedures.childNodes(j) );
         if(node.nodeName == 'addProcedures')
         {
           document.getElementById('req' +procName ).disabled=true;
           document.getElementById('req' +procName).checked = false;
           toggleReqProc(sidAct, procName);
         }
			   break;
			}
		}	
	}
}

function existReqProc(name , node ) // 'to',partInt[j],node)
{
	var procedures = node.selectSingleNode('procedures');
	
	var toRet= false;
	for ( var i=0 ; i< procedures.childNodes.length && !toRet; i++ )
	{
		if(procedures.childNodes(i).getAttribute("name") == name)
    {
      return procedures.childNodes(i).getAttribute("required") == 'y' ||
        procedures.childNodes(i).getAttribute("required") == 'yes' ||
        procedures.childNodes(i).getAttribute("required") == 'true';
    }
	}
	return toRet;
}


function toggleReqProc( sidAct ,procName )
{
	var search="//*[@sid='"+sidAct+"']"; //NTRIMJS
	var node=xmlSrc.firstChild.selectSingleNode(search);

	var o = window.event.srcElement;
	var procedures = node.selectSingleNode('procedures');

	if ( o.checked )
	{
		//insert
		for ( var i=0 ; i< procedures.childNodes.length; i++ )
  	{
  		if(procedures.childNodes(i).getAttribute("name") == procName)
      {
         procedures.childNodes(i).setAttribute("required", "yes");
      }
  	}
	}
	else
	{
		//remove
		for ( j= 0; j< procedures.childNodes.length ; j++ )
		{
			if ( procName == procedures.childNodes(j).getAttribute("name") )
			{
			   procedures.childNodes(i).setAttribute("required", "no");
			   break;
			}
		}
	}
}

function elements_closeFILLVARIABLE( node )
{
	readFromHTML_FILLVARIABLE( node )
	return true;

}
function getCdataNode( node , nodeName )
{
  var node1 = node.selectSingleNode( nodeName );
  if ( node1.firstChild )
  {
	return node1.firstChild;
  }
  else
  {
	var cdata	= xmlSrc.createCDATASection("");
	node1.appendChild(cdata);
	return node1.firstChild;
  }
}

function readFromHTML_FILLVARIABLE( node ,fromAtr)
{
	
	var sid = node.getAttribute("sid");
	var optional = getValue("optional_"+sid);
	var async = getValue("async_"+sid);
	var label = getValue( "label_"+sid );
	var name = getValue( "name_"+sid );
	var description = getValue( "description_"+sid );
	var duration = getValue( "duration_"+sid );
	var process = getValue( "process_"+sid );
	var durationTTC = getValue( "durationTTC_"+sid );
	var participant = getValue( "participant_"+sid );
  var executante = getValue( "executante_"+sid );
	
	var deadNumber = getValue( "deadNumber_"+sid );
	var deadTime = getValue( "deadTime_"+sid );
	var deadTimeMode = getValue( "deadTimeMode_"+sid );
	var deadTimeConstraint = getValue( "deadTimeConstraint_"+sid );
	var deadTimeReference = getValue( "deadTimeReference_"+sid );
	
	var deadLineDate = deadNumber+";"+deadTime+";"+deadTimeMode+";"+deadTimeConstraint+";"+deadTimeReference;
	//alert(deadLineDate);
	var oneShotActivity = getValue( "oneShotActivity_"+sid );
  var showTask  = getValue( "showTask_"+sid );
  var showReassign = getValue( "showReassign_"+sid );
  var showWorkFlowArea = getValue( "showWorkFlowArea_"+sid );

	node.setAttribute("optional",optional);
	node.setAttribute("async",async);
	node.setAttribute("name",name);
	
	getCdataNode( node , "deadLineDate" ).text = deadLineDate;
  
  getCdataNode( node , "oneShotActivity" ).text = oneShotActivity;
  getCdataNode( node , "showTask" ).text = showTask;
  getCdataNode( node , "showReassign" ).text = showReassign;
  getCdataNode( node , "showWorkFlowArea" ).text = showWorkFlowArea;

	getCdataNode( node , "description" ).text = description;
	
	//node.selectSingleNode("description").firstChild.text= description;
	getCdataNode( node , "label" ).text=label;
	
	node.selectSingleNode("process").text=process;
	if ( participant ) node.selectSingleNode("participant").setAttribute("name",participant);
	else node.selectSingleNode("participant").setAttribute("name","");
	//executante
  if ( executante ) node.selectSingleNode("executante").setAttribute("name",executante);
	else node.selectSingleNode("executante").setAttribute("executante","");
	
	getCdataNode( node , "forecastWorkDuration" ).text=duration;
	//node.selectSingleNode("forecastWorkDuration").firstChild.text=duration;
	getCdataNode( node , "forecastTimeToComplete" ).text=durationTTC;
	//node.selectSingleNode("forecastTimeToComplete").firstChild.text=durationTTC;
	if ( node.nodeName == 'decision' || node.nodeName =='choice' || node.nodeName =='menu' )
	{
		var question = getValue( "question_"+sid );
		getCdataNode( node , "question" ).text = question;
		if ( node.nodeName == 'decision' )
		{
			var ab=getValue( "approval_"+sid );
			node.selectSingleNode("approval").setAttribute("boui",ab);
		}
			
		//node.selectSingleNode("question").firstChild.text=question;
	}
	
	if ( node.nodeName == 'send' || node.nodeName =='createMessage'  )
	{
		var message = getValue( "message_"+sid );
		
		if (message != null && message.substr(0,4) =='SID:' )
		{
			message=message.substr(4);
			node.selectSingleNode("message").setAttribute("name", "");
			node.selectSingleNode("message").setAttribute("refered_sid", message);
		}
		else if (message != null)
		{
			node.selectSingleNode("message").setAttribute("name", message);	
		}
	  else
		{
			node.selectSingleNode("message").setAttribute("name", "");	
		}
		
		
		
		//getCdataNode( node , "message" ).text = message;
		
		
		//var subject = getValue( "subject_"+sid );
		//node.selectSingleNode("subject").firstChild.text=subject;
		//getCdataNode( node , "subject" ).text = subject;
		
		var channel = getValue( "channel_"+sid );
		node.selectSingleNode("channel").text=channel;
		
		var priority = getValue( "priority_"+sid );
		node.selectSingleNode("priority").text=priority;
		
		var requireReadReceipt = getValue( "requireReadReceipt_"+sid );
		node.selectSingleNode("requireReadReceipt").text=requireReadReceipt;
		
		var requireDeliveryReceipt = getValue( "requireDeliveryReceipt_"+sid );
		node.selectSingleNode("requireDeliveryReceipt").text=requireDeliveryReceipt;
		
		var from = getValue( "from_"+sid );
		node.selectSingleNode("from").setAttribute("name",from);
		
	}
	if ( node.nodeName == 'waitResponse' )
	{
		
		var sendID = getValue( "sendID_"+sid );
		node.selectSingleNode("sendID").text=sendID;
		var from = getValue( "from_"+sid );
		node.selectSingleNode("from").setAttribute("name",from);

		var responseMessageVariable = getValue( "responseMessageVariable_"+sid );
		node.selectSingleNode("responseMessageVariable").setAttribute("name",responseMessageVariable);

	}
	if ( node.nodeName == 'userCallProgram' )
	{
		
		var mode = getValue( "mode_"+sid );
		node.setAttribute("mode",mode);
		var pfilter = getValue( "programFilter_"+sid );
		var xn = node.selectSingleNode("programFilter").selectSingleNode("xeoql");
		if (!xn.firstChild)
		{
			var cdata	= xmlSrc.createCDATASection("");
			xn.appendChild(cdata);
		}
		xn.firstChild.text=pfilter;

		
	}
	
	var h = document.getElementById("defVarActivity");
	if ( !fromAtr )
	{
	if ( h && h.nodeVarActivity )
	{
    if(h.nodeVar.nodeName == "defMessage")
    {
		  readVarMessage( h.nodeVarActivity , h.nodeVar );
    }
    else
    {
      readVarActivity( h.nodeVarActivity , h.nodeVar );      
    }
	}
	}
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	
	return true;
	
}
/************************  END FILL VARIABLE ************************/


/************************   DEFVARIABLE  **********************************/

function changeVarAtrType()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS

	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	var type = getValue( "type2_"+sid );
	
	readFromHTML_DEFVARIABLE( node );
	elements_showDEFVARIABLE( node );
	
}

function changeOccurs()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	var type = getValue( "occurs_"+sid );
	readFromHTML_DEFVARIABLE( node );
	elements_showDEFVARIABLE( node );
}

function _newVariableObject( node , nodeVar, objectType )
{
	
	
	
	//var objectType = type.split(".")[1];
		var sid=node.getAttribute("sid");	
		var ifrm=[];
		var ii=0;
	
		ifrm[ii++]="<iframe id='frameMethods' scrolling=no frameborder=0 style='height:100%;width:100%' src='";
		ifrm[ii++]="__xwfDesigner_variableMethods.jsp?object=";
		ifrm[ii++]=objectType;
		ifrm[ii++]="&docid=";
		ifrm[ii++]=getDocId();
		ifrm[ii++]="&sid="
		ifrm[ii++]=sid;

		ifrm[ii++]="&aMethods=";
		var k = 0;
		var m=[];
		var mth = node.selectSingleNode("availableMethods");
		if ( mth!=null )
		{
			for ( k = 0; k< mth.childNodes.length ; k++ )
			{
				m[k] = mth.childNodes(k).getAttribute("name");
			}
		}
		ifrm[ii++]=m.join(";");
		
		ifrm[ii++]="&rMethods=";
		var k = 0;
		var m=[];
		
		var mth = node.selectSingleNode("requiredMethods");
		if ( mth!=null )
		{
			for ( k = 0; k< mth.childNodes.length ; k++ )
			{
				m[k] = mth.childNodes(k).getAttribute("name");
			}
		}
		ifrm[ii++]=m.join(";");

		
		ifrm[ii++]="&hMethods=";
		var k = 0;
		var m=[];
		var mth = node.selectSingleNode("hiddenMethods");
		if ( mth!=null )
		{
			for ( k = 0; k< mth.childNodes.length ; k++ )
			{
				m[k] = mth.childNodes(k).getAttribute("name");
			}
		}
		ifrm[ii++]=m.join(";");

        ifrm[ii++]="'";
		ifrm[ii++]=" ></iframe>";
		
        var m=document.getElementById("methods");
        
        if( m )
        {
			m.innerHTML=ifrm.join("");
        }
    
}

function newVariableObject( sid , sidVar )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var search="//*[@sid='"+sidVar+"']"; //NTRIMJS
	var nodeVar=TREE_EDIT.code.firstChild.selectSingleNode(search);
	var type3 = getValue("objName_"+sid);
	var type  = "object";
	for( var i in systemObjects )
	{
		if( type3 == systemObjects[i] )
		{
			objectType=i;
		 break;
		}
	}
	_newVariableObject( node , nodeVar, objectType )
	    
	//elements_showDEFVARIABLE( node );

}
function elements_showDEFVARIABLE( node , htmNode , nodeVar )
{
	var htm=[];
	var varHTML=[];
	var j=0;
	var i=0;
	
	var disabled = false;
	
	var yesNoOptionsExt=["Sim","Não" ] ;
	var yesNoOptionsInt=["y","n"] ;
	
	var readWriteOptionsExt=["Apenas leitura","Leitura e Escrita" ] ;
	var readWriteOptionsInt=["read","write"] ;
	
	var sid = node.getAttribute("sid");
	var redefining=false;
	if ( !sid )
	{
		// estou numa activity em mode overwrite da variables
		sid = "0";
		redefining=true;
	}
	else
	{
		nodeVar = node;
		htmNode = workCell;
	}
	var name = node.getAttribute("name");
	var processTemplate = node.getAttribute("processTemplate");
	
	
	var label = node.selectSingleNode("label").text;
	var description = node.selectSingleNode("description").text;
	var hiddenWhen = node.selectSingleNode("hiddenWhen").text;
	var valid = node.selectSingleNode("valid").text;
	var required = node.selectSingleNode("required").text;
	
	var type = nodeVar.selectSingleNode("type").text;
	
	if ( !redefining )
	{
		var input = node.getAttribute("input");
		if( input == "true"){input="y"}else{input="n"}		
		var defaultValue = node.selectSingleNode("defaultValue").text;
		var formula = node.selectSingleNode("formula").text;
		var value = node.selectSingleNode("value").text;
	}
	
	
	var typeOptionsExt=["Objecto","Texto","Numero","Data","Data Hora","Verdadeiro/falso","Texto longo","Duração","Lista de Valores", "Definição de Mensagem" ] ;
	var typeOptionsInt=["object","char(40)","number","Date","DateTime","boolean","clob","duration","lov", "message" ] ;
	
	//campos iguais qualquer que seja o tipo de variavel
	var typeHTML;
	
	var alreadyExists = true;
	
	if ( document.getElementById( "name_"+sid ) == null )
	{
		if ( !redefining )
		{
			var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		}
		var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
		var validHTML = createFieldText( valid , "valid_"+sid,"valid_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		var hiddenWhenHTML = createFieldText( hiddenWhen , "hiddenWhen_"+sid,"hiddenWhen_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		var requiredHTML = createFieldCombo(required,"required_"+sid,"required_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
		var inputHTML = createFieldCombo(input,"input_"+sid,"input_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
		var mode = node.selectSingleNode("mode").text; //read / write
		var modeHTML = createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", readWriteOptionsExt , readWriteOptionsInt ,false,false,"changeAtr" );
		
		
		
		type2 = type.split(".")[0];
		alreadyExists = false;
		
		if ( !redefining )
		{
			typeHTML= createFieldCombo(type2,"type2_"+sid,"type2_"+sid,"1", typeOptionsExt , typeOptionsInt ,false,false,"changeVarAtrType" );
			var formulaHTML = createFieldText( formula , "formula_"+sid,"formula_"+sid,"1", false ,true ,"changeAtr()",false,true,3000)
		}
	}
	
	varHTML[j++]="<table cellpadding=0 cellspacing=0 class='section' >"
	varHTML[j++]="<col width=150px />"
	varHTML[j++]="<col width=100% />"
	
	
	if ( type.substr(0,6) == "object" )
	{
		var maxoccurs = parseInt( nodeVar.selectSingleNode("maxoccurs").text,10);
		var minoccurs = parseInt( nodeVar.selectSingleNode("minoccurs").text,10 );
		var objectType = type.split(".")[1];
		if ( !redefining )
		{
			
			if( defaultValue=="") defaultValue=null;
	
			var clsRegHTML = createDetachFieldLookup(
			systemObjects[objectType],
			"objName_"+sid,
			"objName_"+sid,
			"Ebo_ClsReg",
			"Ebo_ClsReg",
			"Classe de objecto",
			getDocId(), //docid
			"single", //single or multi
			1,
			false, //isdisable
			true,
			"newVariableObject(\""+sid+"\",\""+nodeVar.getAttribute("sid")+"\")"
			)
		}

		var validDB = node.selectSingleNode("validDB").text;    
		var validBusiness = node.selectSingleNode("validBusiness").text;
		
		var showMode = node.selectSingleNode("showMode").text; //viewAttribute - viewObject - viewAsLov
		var objectfilter = node.selectSingleNode("objectfilter").selectSingleNode("xeoql").text;
	
        if ( !redefining )
		{
			varHTML[j++]="<tr><td>"
			varHTML[j++]="Objecto";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=clsRegHTML;
			varHTML[j++]="</td></tr>";
		}
		var objectfilterHTML = createFieldText( objectfilter , "objectfilter_"+sid,"objectfilter_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		
		varHTML[j++]="<tr><td>"
		varHTML[j++]="Filtro";
		varHTML[j++]="</td>";
		varHTML[j++]="<td>";
		varHTML[j++]=objectfilterHTML;
		varHTML[j++]="</td></tr>";
    if(redefining)
    {
      var processTemplateHTML = createFieldCombo(processTemplate,"processTemplate_"+sid,"processTemplate_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
      varHTML[j++]="<tr><td>"
  		varHTML[j++]="Processar Template";
  		varHTML[j++]="</td>";
  		varHTML[j++]="<td>";
  		varHTML[j++]=processTemplateHTML;
  		varHTML[j++]="</td></tr>";
    }

        
        var occursOptionsExt=["Um","Lista" ] ;
		var occursOptionsInt=["1","N"] ;
		var occurs = "1";
		if ( maxoccurs > 1 )
		{
			occurs = "N";
		}
		if ( !redefining )
		{
			var occursHTML = createFieldCombo(occurs,"occurs_"+sid,"occurs_"+sid,"1", occursOptionsExt , occursOptionsInt ,false,false,"changeOccurs" );

			varHTML[j++]="<tr><td>"
			varHTML[j++]="Cardinalidade";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=occursHTML;
			varHTML[j++]="</td></tr>";        
		}
		
		if ( occurs == "1" )
		{
			var showModeOptionsExt=["Editar Objecto","Editar atributo","Lista de Valores" ] ;
			var showModeOptionsInt=["viewObject","viewAttribute","viewAsLov"] ;
			var showModeHTML = createFieldCombo(showMode,"showMode_"+sid,"showMode_"+sid,"1", showModeOptionsExt , showModeOptionsInt ,false,false,"changeAtr" );
			
			
			varHTML[j++]="<tr><td>"
			varHTML[j++]="Forma ";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=showModeHTML;
			varHTML[j++]="</td></tr>";
			
			var validDBHTML = createFieldCombo(validDB,"validDB_"+sid,"validDB_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
		
			varHTML[j++]="<tr><td>"
			varHTML[j++]="Obrigatório validaçao de BD para completar? ";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=validDBHTML;
			varHTML[j++]="</td></tr>";        
			
		
			var validBusinessHTML = createFieldCombo(validBusiness,"validBusiness_"+sid,"validBusiness_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );		
			
			varHTML[j++]="<tr><td>"
			varHTML[j++]="Obrigatório validaçao de negócio para completar? ";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=validBusinessHTML;
			varHTML[j++]="</td></tr>"; 
		
		}
		

		
	
	
        
        if ( !redefining )
		{
			var defaultValueHTML = createDetachFieldLookup(
			defaultValue ,
			"defaultValue_"+sid,
			"defaultValue_"+sid,
			objectType,
			objectType,
			"Valor por defeito",
			getDocId(), //docid
			occurs=="1"?"single":"multi", //single or multi
			1,
			false, //isdisable
			true
			)
        
        
			varHTML[j++]="<tr><td>"
			varHTML[j++]="Valor Inicial";
			varHTML[j++]="</td>";
			varHTML[j++]="<td>";
			varHTML[j++]=defaultValueHTML;
			varHTML[j++]="</td></tr>"; 
        }
        
        
        varHTML[j++]="<tr><td colspan=2><div id='methods'>"
		varHTML[j++]="" //ifrm.join("");
		varHTML[j++]="</div></td></tr>"; 
        
        
    }    
    varHTML[j++] ="</table>";
    

	if ( !alreadyExists )
	{
		htm[i++]="<table id='readSection' cellpadding=0 cellspacing=0 class='section' >"
		if ( !redefining )
		{
		htm[i++]="<col width=150px />"
		}
		else
		{
		htm[i++]="<col width=90px />"
		}
		htm[i++]="<col width=100% />"

		if ( !redefining )
		{
			htm[i++]="<tr><td>"
			htm[i++]="Nome";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=nameHTML;
			htm[i++]="</td></tr>";
		}
	
		htm[i++]="<tr><td>"
		htm[i++]="Rótulo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=labelHTML;
		htm[i++]="</td></tr>";
		
		htm[i++]="<tr HEIGHT=100PX><td valign=top>"
		htm[i++]="Descrição";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=descriptionHTML;
		htm[i++]="</td></tr>";
	
		if ( !redefining )
		{
			htm[i++]="<tr><td>"
			htm[i++]="Tipo";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=typeHTML;
			htm[i++]="</td></tr>";
		
			htm[i++]="<tr><td>"
			htm[i++]="Obrigatória para programa começar?";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=inputHTML;
			htm[i++]="</td></tr>";
			
			htm[i++]="<tr><td>"
			htm[i++]="Formula"
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]= formulaHTML;
			htm[i++]="</td></tr>"
		}
		
		htm[i++]="<tr><td>"
		htm[i++]="Modo de apresentação"
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=  modeHTML;
		htm[i++]="</td></tr>";
		
		;
		
		htm[i++]="<tr><td>"
		htm[i++]="Obrigatório"
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=  requiredHTML;
		htm[i++]="</td></tr>";
	
		htm[i++]="<tr><td>"
		htm[i++]="Validação"
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=  validHTML;
		htm[i++]="</td></tr>";
		
		htm[i++]="<tr><td>"
		htm[i++]="Invisivel se"
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=  hiddenWhenHTML;
		htm[i++]="</td></tr>";
		
		htm[i++]="<tr><td colspan=2 >"
		htm[i++]= "<span style='width:100%;height:1px;border-top:1px dotted #CCCCCC'/>";
		htm[i++]="</td></tr>";

		
		htm[i++]="<tr><td colspan=2 id ='typeVarSpecific'>"
		htm[i++]= "&nbsp";
		htm[i++]="</td></tr>";
	
		htm[i++]="</table>";
		htmNode.innerHTML= htm.join("");
	}
	
	typeVarSpecific.innerHTML = varHTML.join("");
	
	
	
	//createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	if ( !redefining )
	{
		document.getElementById( "name_"+sid ).focus();	
	}
	else
	{
		_newVariableObject( node , nodeVar ,objectType);
		document.getElementById( "label_"+sid ).focus();	
	}
	
	
	
}

function readFromHTML_DEFVARIABLE( node )
{
	
	var sid = node.getAttribute("sid");
	var label = getValue( "label_"+sid );
	var description = getValue( "description_"+sid );
	var name = getValue( "name_"+sid );
	var input = getValue( "input_"+sid );
	if( input =="y") input="true";
	else  input="false";
	
	var valid = getValue( "valid_"+sid );
	var formula = getValue( "formula_"+sid );
	var hiddenWhen = getValue( "hiddenWhen_"+sid );
	var mode = getValue( "mode_"+sid );
	var required = getValue( "required_"+sid );
	
	var type2 = getValue( "type2_"+sid );
	
	
	
	//node.selectSingleNode("description").firstChild.text=description;
	getCdataNode( node , "description" ).text = description;
	getCdataNode( node , "label" ).text = label;
	//node.selectSingleNode("label").firstChild.text=label;
	node.selectSingleNode("mode").text=mode;
	
	getCdataNode( node , "valid" ).text = valid;
	//node.selectSingleNode("valid").firstChild.text=valid;
	
	getCdataNode( node , "formula" ).text = formula;
	//node.selectSingleNode("formula").firstChild.text=formula;
	
	getCdataNode( node , "hiddenWhen" ).text = hiddenWhen;
	//node.selectSingleNode("hiddenWhen").firstChild.text=hiddenWhen;
	
	node.selectSingleNode("required").text=required;
	
	if ( name != node.getAttribute("name"))
	{
		var search="//*/variable[@name='"+node.getAttribute("name")+"']"; //NTRIMJS
		var nodes=xmlSrc.firstChild.selectNodes(search);
		for ( var i = 0 ; i < nodes.length;  i++ )
		{
			nodes(i).setAttribute("name",name);
		}
		node.setAttribute("name",name);
	}
	
	
	
	
	node.setAttribute("input",input);
	
	
	if( type2 == 'object'  )
	{
	  if ( document.getElementById("objName_"+sid)!= null ) // verfica se os campos estão visiveis
	  {
			var type3 = getValue("objName_"+sid);
			var type  = "object";
			for( var i in systemObjects )
			{
				if( type3 == systemObjects[i] )
				{
				 type+="."+i;
				 break;
				}
			}
			node.selectSingleNode("type").text=type;
			var objectfilter = getValue("objectfilter_"+sid);
			
			//node.selectSingleNode("objectfilter").selectSingleNode("xeoql").firstChild.text=objectfilter;
			
			var xn = node.selectSingleNode("objectfilter").selectSingleNode("xeoql");
			if (!xn.firstChild)
			{
				var cdata	= xmlSrc.createCDATASection("");
				xn.appendChild(cdata);
			}
			xn.firstChild.text=objectfilter
		
			var occurs = getValue("occurs_"+sid);
			if ( occurs == "1")
			{
				if ( required == "n" )
				{
					node.selectSingleNode("minoccurs").text="0";
				}
				else
				{
					node.selectSingleNode("minoccurs").text="1";
				}
				node.selectSingleNode("maxoccurs").text="1";
				
				var showMode = getValue("showMode_"+sid);
				node.selectSingleNode("showMode").text=showMode;
				
				var validBusiness = getValue("validBusiness_"+sid);
				node.selectSingleNode("validBusiness").text=validBusiness;
				var validDB = getValue("validDB_"+sid);
				node.selectSingleNode("validDB").text=validDB;
				
			}
			else
			{
				if ( required == "n" )
				{
					node.selectSingleNode("minoccurs").text="0";
				}
				else
				{
					node.selectSingleNode("minoccurs").text="1";
				}
				node.selectSingleNode("maxoccurs").text="999999";
		
			}
		
			var defaultValue = getValue("defaultValue_"+sid);
		
			//node.selectSingleNode("defaultValue").firstChild.text=defaultValue;
			getCdataNode( node , "defaultValue" ).text = defaultValue;
			
			readMethods( node , false )
			
		}	
		else
		{
			node.selectSingleNode("type").text=type2;
			
		}
		
	}
	else
	{
		node.selectSingleNode("type").text=type2;
	}
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node ); //actualiza viwer
	
	return true;
}


function elements_closeDEFVARIABLE( node )
{
	readFromHTML_DEFVARIABLE( node )
	return true;

}

function readMethods( node , removeNode )
{
	var frm = document.getElementById("frameMethods");
	if ( frm && frm.contentWindow.document &&frm.contentWindow.document.readyState=="complete")
	{
		 var d=frm.contentWindow.document;
		 var w=frm.contentWindow;
				 
		 var mths = d.getElementById("rMethods").rows;
		 var xmth = node.selectSingleNode("requiredMethods");
		 while ( xmth.childNodes.length > 0 )
		 {
			xmth.removeChild(xmth.firstChild);
		 }
		 for( var i=0; i< mths.length ; i++ )
		 {
			if (mths[i].cells[0].firstChild.checked )
			{
				var e= xmlSrc.createElement("method");
				e.setAttribute("name", mths[i].method )
				xmth.appendChild( e);
			}
		 }
		 if ( xmth.childNodes.length == 0 && removeNode )
		 {
			xmth.parentNode.removeChild( xmth )
		 }
		 
		 var mths = d.getElementById("hMethods").rows;
		 var xmth = node.selectSingleNode("hiddenMethods");
		 while ( xmth.childNodes.length > 0 )
		 {
			xmth.removeChild(xmth.firstChild);
		 }
		 for( var i=0; i< mths.length ; i++ )
		 {
			if (mths[i].cells[0].firstChild.checked )
			{
				var e= xmlSrc.createElement("method");
				e.setAttribute("name", mths[i].method )
				xmth.appendChild( e);
			}
		 }
		 if ( xmth.childNodes.length == 0 && removeNode )
		 {
			xmth.parentNode.removeChild( xmth )
		 }
		 
		 var mths = d.getElementById("aMethods").rows;
		 var xmth = node.selectSingleNode("availableMethods");
		 while ( xmth.childNodes.length > 0 )
		 {
			xmth.removeChild(xmth.firstChild);
		 }
		 for( var i=0; i< mths.length ; i++ )
		 {
			if (mths[i].cells[0].firstChild.checked )
			{
				var e= xmlSrc.createElement("method");
				e.setAttribute("name", mths[i].method )
				xmth.appendChild( e);
			}
		 }
		 if ( xmth.childNodes.length == 0 && removeNode )
		 {
			xmth.parentNode.removeChild( xmth )
		 }
				 
	}
	else
	{
	
		if ( removeNode )
		{
			//alert("a remover"+removeNode);
			var xmth = node.selectSingleNode("requiredMethods");
			if ( xmth.childNodes.length == 0 )
			{
				xmth.parentNode.removeChild( xmth )
			}
			var xmth = node.selectSingleNode("availableMethods");
			if ( xmth.childNodes.length == 0 )
			{
				xmth.parentNode.removeChild( xmth )
			}
			var xmth = node.selectSingleNode("hiddenMethods");
			if ( xmth.childNodes.length == 0 )
			{
				xmth.parentNode.removeChild( xmth )
			}
		}
	}
	
	
}
/************************  END DEFVARIABLE ************************/


/************************   DEFMESSAGE  **********************************/


function changeMSGVarAtrType()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS

	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	
	readFromHTML_DEFMESSAGE( node );
	elements_showDEFMESSAGE( node );
	
}

function changeMSGOccurs()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	var type = getValue( "occurs_"+sid );
	readFromHTML_DEFVARIABLE( node );
	elements_showDEFVARIABLE( node );
}

function _newTemplate( node)
{
	//var objectType = type.split(".")[1];
		var sid=node.getAttribute("sid");	
		var ifrm=[];
		var ii=0;

		ifrm[ii++]="<iframe id='frameKeysWords' scrolling=no frameborder=0 style='height:100%;width:100%' src='";
		ifrm[ii++]="__xwfDesigner_keyWords.jsp?template=";
		ifrm[ii++]=node.selectSingleNode("objTemplate").getAttribute("boui");
		ifrm[ii++]="&docid=";
		ifrm[ii++]=getDocId();
		ifrm[ii++]="&sid="
		ifrm[ii++]=sid;

		ifrm[ii++]="&aKeys=";
		var k = 0;
		var key=[];
    var objTemplate = node.selectSingleNode("objTemplate");
		var keys = objTemplate.selectSingleNode("keyWords");
		if ( keys!=null )
		{
			for ( k = 0; k< keys.childNodes.length ; k++ )
			{
				key[k] = keys.childNodes(k).text;
			}
		}
		ifrm[ii++]=key.join(";");
        ifrm[ii++]="'";
		ifrm[ii++]=" ></iframe>";
		
        var m=document.getElementById("keywords");
        
        if( m )
        {
			m.innerHTML=ifrm.join("");
        }
    
}
function newTemplate( sid , sidVar )
{
	
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);
	_newTemplate( node , nodeVar)
	    
	//elements_showDEFVARIABLE( node );

}

function elements_showDEFMESSAGE( node , htmNode , nodeVar )
{
	var htm=[];
	var varHTML=[];
	var j=0;
	var i=0;
	
	var disabled = false;
	
	var yesNoOptionsExt=["Sim","Não" ] ;
	var yesNoOptionsInt=["y","n"] ;
	
	var readWriteOptionsExt=["Apenas leitura","Leitura e Escrita" ] ;
	var readWriteOptionsInt=["read","write"] ;
	
	var sid = node.getAttribute("sid");
	var redefining=false;
	if ( !sid )
	{
		// estou numa activity em mode overwrite da variables
		sid = "0";
		redefining=true;
	}
	else
	{
		nodeVar = node;
		htmNode = workCell;
	}
	var name = node.getAttribute("name");
  var processTemplate = node.getAttribute("processTemplate") == null ? "n":node.getAttribute("processTemplate");
  processTemplate = (processTemplate == "") ? 'n':processTemplate;
	
	
	
	var label = node.selectSingleNode("label").text;
	var description = node.selectSingleNode("description").text;
	var subject = node.selectSingleNode("subject").text;
	var msg = node.selectSingleNode("message").text;
	
	//campos iguais qualquer que seja o tipo de variavel
	var typeHTML;
	
	var alreadyExists = true;
   var processTemplateHTML;
	
	if ( document.getElementById( "name_"+sid ) == null )
	{
		if ( !redefining )
		{
			var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
      var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
      var subjectHTML = createFieldText( subject , "subject_"+sid,"subject_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
      var msgHTML = createFieldText( msg , "msg_"+sid,"msg_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
      var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
		}
    else
    {
      processTemplateHTML = createFieldCombo(processTemplate,"processTemplate_"+sid,"processTemplate_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
    }		
		alreadyExists = false;
	} 

  varHTML[j++]="<table cellpadding=0 cellspacing=0 class='section' >"
	varHTML[j++]="<col width=150px />"
	varHTML[j++]="<col width=100% />"
	
	varHTML[j++]="<tr><td colspan=2><div id='keywords'>"
	varHTML[j++]="" //ifrm.join("");
	varHTML[j++]="</div></td></tr>"; 
  varHTML[j++] ="</table>";
  if ( redefining )
	{
    htm[i++]="<table id='processTemplate' cellpadding=0 cellspacing=0 class='section' >"
    htm[i++]="<col width=150px />"
    htm[i++]="<col width=100% />"
    htm[i++]="<tr><td>"
		htm[i++]="Processo Template";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=processTemplateHTML;
		htm[i++]="</td></tr>";
    htm[i++]='</TBODY>'
	  htm[i++]='</TABLE>';
    htmNode.innerHTML= htm.join(""); 
  }
	else if ( !alreadyExists )
	{
    htm[i++]='<TABLE id="50" class="layout" cellspacing="0" cellpadding="0">'
    htm[i++]='<TBODY>';
    htm[i++]='<TR height="25">';
    htm[i++]='<TD>';
    htm[i++]='<TABLE cellpadding="0" cellspacing="0" class="tabBar" id="50_body" onkeyup="so(\'50\');onKeyUpTab_std(event)" onmouseover="so(\'50\');onOverTab_std(event)" onmouseout="so(\'50\');onOutTab_std(event)" ondragenter="so(\'50\');ondragenterTab_std(event)" ondragover="so(\'50\');ondragoverTab_std(event)" onclick="so(\'50\');onClickTab_std(event)">';
    htm[i++]='<TBODY>';
    htm[i++]='<TR>'
    htm[i++]='<TD style="padding:0px" id="50_tabs" valign="bottom" noWrap="yes">'
    htm[i++]='<SPAN class="tab tabOn" id="50_tabheader_0" name="tab_settings" tabNumber="50" tabIndex="287">Dados Gerais</SPAN>';
    htm[i++]='<SPAN class="tab" id="50_tabheader_1" name="tab_msg" tabNumber="50" tabIndex="287">Dados da Mensagem</SPAN>';
    htm[i++]='<SPAN class="tab" id="50_tabheader_2" name="tab_template" tabNumber="50" tabIndex="287">Modelo</SPAN>';
    htm[i++]='</TD>';
    htm[i++]='</TR>';
    htm[i++]='</TBODY>';
    htm[i++]='</TABLE>';
    htm[i++]='<HR class="tabGlow" id="hrSelTab50"/>';
    htm[i++]='<HR class="tabGlow" id="hrTab50"/>';
    htm[i++]='</TD>';
    htm[i++]='</TR>';
    htm[i++]='<TR>';
    htm[i++]='<TD>';
    htm[i++]='<DIV id="50_tabbody_0" class="tab" style="overflow-y:auto">';

    htm[i++]="<table id='readSection' cellpadding=0 cellspacing=0 class='section' >"

		if ( !redefining )
		{
		htm[i++]="<col width=150px />"
		}
		else
		{
		htm[i++]="<col width=90px />"
		}
		htm[i++]="<col width=100% />"

		if ( !redefining )
		{
			htm[i++]="<tr><td>"
			htm[i++]="Nome";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=nameHTML;
			htm[i++]="</td></tr>";
		}
	
		htm[i++]="<tr><td>"
		htm[i++]="Rótulo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=labelHTML;
		htm[i++]="</td></tr>";

    htm[i++]="<tr HEIGHT=100PX><td valign=top>"
		htm[i++]="Descrição";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=descriptionHTML;
		htm[i++]="</td></tr>";
    htm[i++]="<tr><td colspan=2 >"
		htm[i++]= "<span style='width:100%;height:1px;border-top:1px dotted #CCCCCC'/>";
		htm[i++]="</td></tr>";

    if(redefining)
    {
      htm[i++]="<tr><td>"
  		htm[i++]="Processar Template";
  		htm[i++]="</td>";
  		htm[i++]="<td>";
  		htm[i++]=processTemplateHTML;
  		htm[i++]="</td></tr>";
      window.setTimeout("try{ document.getElementById( 'processTemplate_"+sid +"').focus()}catch(e){}",100);
    }
	
		htm[i++]="</table>";
    htm[i++]='</DIV>';

    htm[i++]='<DIV id="50_tabbody_1" class="tab" style="overflow-y:auto">';
    htm[i++]="<table id='readMessage' cellpadding=0 cellspacing=0 class='section' >"
		if ( !redefining )
		{
		htm[i++]="<col width=150px />"
		}
		else
		{
		htm[i++]="<col width=90px />"
		}
		htm[i++]="<col width=100% />"

    htm[i++]="<tr><td>"
		htm[i++]="Assunto";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=subjectHTML;
		htm[i++]="</td></tr>";
		
    htm[i++]="<tr HEIGHT=100PX><td valign=top>"
		htm[i++]="Mensagem";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=msgHTML;
		htm[i++]="</td></tr>";
		
		htm[i++]="<tr><td colspan=2 >"
		htm[i++]= "<span style='width:100%;height:1px;border-top:1px dotted #CCCCCC'/>";
		htm[i++]="</td></tr>";
	
		htm[i++]="</table>";
    htm[i++]='</DIV>';
    

    htm[i++]='<DIV id="50_tabbody_2" class="tab" style="overflow-y:auto">';
    htm[i++]="<table id='readTemplate' cellpadding=0 cellspacing=0 class='section' >";
		htm[i++]="<tr><td colspan=2 id ='templateSpecific'>"
		htm[i++]= "&nbsp";
		htm[i++]="</td></tr>";
		htm[i++]="</table>";
    htm[i++]='</DIV>';
    htm[i++]='</TD>'
	  htm[i++]='</TR>'
	  htm[i++]='</TBODY>'
	  htm[i++]='</TABLE>';
		htmNode.innerHTML= htm.join("");
    templateSpecific.innerHTML = varHTML.join("");
	}

  if(!redefining)
  {
    _newTemplate(node , nodeVar);
  	
  	winmain().ndl[getIDX()].focusfields =null;
  	toFocus=null
  	_hsos['50']=null;
  	so('50');
  	_hso.oTab=document.getElementById('50_tabheader_0');	
  	window.setTimeout("try{ document.getElementById( 'name_"+sid +"').focus()}catch(e){}",100);
  }
}

function readFromHTML_DEFMESSAGE( node )
{
	var sid = node.getAttribute("sid");
	var label = getValue( "label_"+sid );
	var description = getValue( "description_"+sid );
	var name = getValue( "name_"+sid );
  var subject = getValue( "subject_"+sid );
	var msg = getValue( "msg_"+sid );
	
	
	
	//node.selectSingleNode("description").firstChild.text=description;
	getCdataNode( node , "description" ).text = description;
  getCdataNode( node , "message" ).text = msg;
  getCdataNode( node , "subject" ).text = subject;
	getCdataNode( node , "label" ).text = label;

	if ( name != node.getAttribute("name"))
	{
		var search="//*/variable[@name='"+node.getAttribute("name")+"']"; //NTRIMJS
		var nodes=xmlSrc.firstChild.selectNodes(search);
		for ( var i = 0 ; i < nodes.length;  i++ )
		{
			nodes(i).setAttribute("name",name);
		}
		node.setAttribute("name",name);
	}
  readKeyWords(node , false );
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node ); //actualiza viwer
	
	return true;
}


function elements_closeDEFMESSAGE( node )
{
	readFromHTML_DEFMESSAGE( node )
	return true;

}

function readKeyWords( node , removeNode )
{
	var frm = document.getElementById("frameKeysWords");
	if ( frm && frm.contentWindow.document &&frm.contentWindow.document.readyState=="complete")
	{
		 var d=frm.contentWindow.document;
		 var w=frm.contentWindow;
     var template = d.getElementById("template");
     var tempBoui = template.getElementsByTagName('INPUT')[0].value;
		 var keys = d.getElementById("keys").rows;
     var objTemplate = node.selectSingleNode("objTemplate");
     objTemplate.setAttribute("boui",tempBoui);
		 var xkeys = objTemplate.selectSingleNode("keyWords");
		 while ( xkeys.childNodes.length > 0 )
		 {
			xkeys.removeChild(xkeys.firstChild);
		 }
		 for( var i=0; i< keys.length ; i++ )
		 {			
				var e= xmlSrc.createElement("keyAttribute");
				e.text = keys[i].key;
				xkeys.appendChild( e);
		 }
		 if ( xkeys.childNodes.length == 0 && removeNode )
		 {
			xkeys.parentNode.removeChild( xkeys )
		 }	 
	}
	else
	{
	
		if ( removeNode )
		{
			//alert("a remover"+removeNode);
			var xkey = node.selectSingleNode("keyWords");
			if ( xkey.childNodes.length == 0 )
			{
				xkey.parentNode.removeChild( xkey )
			}
		}
	}
}
/************************  END DEFMESSAGE ************************/

/************************   DEFPARTICIPANT  **********************************/

function elements_showDEFPARTICIPANT( node )
{
	var htm=[];
	var varHTML=[];
	var j=0;
	var i=0;
	
	var disabled = false;
	
	var yesNoOptionsExt=["Sim","Não" ] ;
	var yesNoOptionsInt=["y","n"] ;
	
	var readWriteOptionsExt=["Apenas leitura","Leitura e Escrita" ] ;
	var readWriteOptionsInt=["read","write"] ;
	
	var sid = node.getAttribute("sid");
	var name = node.getAttribute("name");
	if ( name =="starter" || name == "workFlowAdministrator" )
	{
		workCell.innerHTML= "<center><b>Participante de Sistema.Edição não disponível</b</center>";
		return true;
	}
	var input = node.getAttribute("input");
	var type   =node.selectSingleNode("type").text;

//para retirar
	if( node.selectSingleNode("formula") == null )
	{
		node.appendChild( newElement("formula","" , xmlSrc , true ));
		node.appendChild( newElement("showMode","" , xmlSrc , false ));	
	}
//--end para retirar
	
	if( input == "true")
	{
		input="y";
	}
	else
	{
		input="n";
	}
	
	var showMode = node.selectSingleNode("showMode").text; //viewAttribute - viewObject - viewAsLov
	var label = node.selectSingleNode("label").text;
	var description = node.selectSingleNode("description").text;
	var defaultValue = node.selectSingleNode("defaultValue").text;
	var value = node.selectSingleNode("value").text;
	var formula = node.selectSingleNode("formula").text;
	
	var showModeOptionsExt=["Editar Objecto","Editar atributo","Lista de Valores" ] ;
	var showModeOptionsInt=["viewObject","viewAttribute","viewAsLov"] ;
	var showModeHTML = createFieldCombo(showMode,"showMode_"+sid,"showMode_"+sid,"1", showModeOptionsExt , showModeOptionsInt ,false,false,"changeAtr" );
			
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	var inputHTML = createFieldCombo(input,"input_"+sid,"input_"+sid,"1", yesNoOptionsExt , yesNoOptionsInt ,false,false,"changeAtr" );
	var formulaHTML = createFieldText( formula , "formula_"+sid,"formula_"+sid,"1", false ,true ,"changeAtr()",false,true,3000)
	var mode = node.selectSingleNode("mode").text; //read / write
	var modeHTML = createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", readWriteOptionsExt , readWriteOptionsInt ,false,false,"changeAtr" );

	var objectfilter = node.selectSingleNode("objectfilter").selectSingleNode("xeoql").text;
	var objectfilterHTML = createFieldText( objectfilter , "objectfilter_"+sid,"objectfilter_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		
	    
    var defaultValueHTML = createDetachFieldLookup(
    defaultValue ,
    "defaultValue_"+sid,
    "defaultValue_"+sid,
    'iContact',
    'iContact',
    "Valor por defeito",
    getDocId(), //docid
    "single", //single or multi
    1,
    false, //isdisable
    true
    )
        
        
    
   

	htm[i++]="<table id='readSection' cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"

	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
		
	htm[i++]="<tr HEIGHT=100PX><td valign=top>"
	htm[i++]="Descrição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=descriptionHTML;
	htm[i++]="</td></tr>";
	
		
	htm[i++]="<tr><td>"
	htm[i++]="Obrigatória para programa começar?";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=inputHTML;
	htm[i++]="</td></tr>";
		
	htm[i++]="<tr><td>"
	htm[i++]="Valor Inicial";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=defaultValueHTML;
	htm[i++]="</td></tr>"; 

	htm[i++]="<tr><td>"
	htm[i++]="Formula";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=formulaHTML;
	htm[i++]="</td></tr>"; 

			
	htm[i++]="<tr><td>"
	htm[i++]="Modo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=modeHTML;
	htm[i++]="</td></tr>"; 

	htm[i++]="<tr><td>"
	htm[i++]="Forma";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=showModeHTML;
	htm[i++]="</td></tr>"; 

	htm[i++]="<tr><td>"
	htm[i++]="Filtro";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=objectfilterHTML;
	htm[i++]="</td></tr>"; 

		
	
	htm[i++]="</table>";
	workCell.innerHTML= htm.join("");

	
	//createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	
	document.getElementById( "name_"+sid ).focus();		
	
}

function readFromHTML_DEFPARTICIPANT( node )
{
	var name = node.getAttribute("name");
	if ( name =="starter" || name == "workFlowAdministrator" )
	{
		return true;
	}
	var sid = node.getAttribute("sid");
	var label = getValue( "label_"+sid );
	var description = getValue( "description_"+sid );
	var name = getValue( "name_"+sid );
	var input = getValue( "input_"+sid );
	var showMode = getValue( "showMode_"+sid );
	var formula = getValue( "formula_"+sid );
	if( input =="yes") input="true";
	else  input="false";
	
	participantsName[ name ] = label;
	
	var mode = getValue( "mode_"+sid );
	getCdataNode( node , "formula" ).text = formula;
	node.selectSingleNode("showMode").text=showMode;
	
	//node.selectSingleNode("description").firstChild.text=description;
	getCdataNode( node , "description" ).text = description;
	getCdataNode( node , "label" ).text = label;
	
	node.selectSingleNode("mode").text=mode;
	node.selectSingleNode("type").text="object.Ebo_Perf";
	
	if ( name != node.getAttribute("name"))
	{
		var search="//*/participant[@name='"+node.getAttribute("name")+"']"; //NTRIMJS
		var nodes=xmlSrc.firstChild.selectNodes(search);
		for ( var i = 0 ; i < nodes.length;  i++ )
		{
			nodes(i).setAttribute("name",name);
		}
		node.setAttribute("name",name);
	}


	node.setAttribute("input",input);
	var objectfilter = getValue("objectfilter_"+sid);
	
	getCdataNode( node.selectSingleNode("objectfilter") , "xeoql" ).text = objectfilter;
	
	//node.selectSingleNode("objectfilter").selectSingleNode("xeoql").firstChild.text=objectfilter;
	
	var defaultValue = getValue("defaultValue_"+sid);
	
	getCdataNode( node,"defaultValue").text = defaultValue;
	//node.selectSingleNode("defaultValue").firstChild.text=defaultValue;
	
		
		
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node ); //actualiza viwer
	
	return true;
}


function elements_closeDEFPARTICIPANT( node )
{
	readFromHTML_DEFPARTICIPANT( node )
	return true;

}

/************************  END DEFVPARTICIPANT ************************/


/************************   DEFPROCEDURE  **********************************/

function changeVarAtrType()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS

	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	
	readFromHTML_DEFPROCEDURE( node );
	elements_showDEFPROCEDURE( node );
	
}

function changeOccurs()
{
	var e = window.event.srcElement;
	var sid = e.name.substr( e.name.indexOf("_")+1 );
	var search="//*[@sid='"+sid+"']"; //NTRIMJS
	var node=TREE_EDIT.code.firstChild.selectSingleNode(search);	
	var nodeName = node.nodeName.toUpperCase();
	readFromHTML_DEFPROCEDURE( node );
	elements_showDEFPROCEDURE( node );
}

function elements_showDEFPROCEDURE( node , htmNode , nodeVar )
{
	var htm=[];
	var varHTML=[];
	var j=0;
	var i=0;
	
	var disabled = false;
	
	var sid = node.getAttribute("sid");
	var redefining=false;
	if ( !sid )
	{
		// estou numa activity em mode overwrite da variables
		sid = "0";
		redefining=true;
	}
	else
	{
		nodeVar = node;
		htmNode = workCell;
	}
	var name = node.getAttribute("name");

	var label = node.selectSingleNode("label").text;
	
	//participants
	var participants=TREE_EDIT.code.selectSingleNode("//defParticipants");
  var participantHTML = "<b>não existem participantes definidos</b>";
  var partInt=[];
  var partExt=[];

  for ( var p=0 ; p < participants.childNodes.length ; p++ )
  {
  	partInt[ p ] = participants.childNodes(p).getAttribute("name");
  	partExt[ p ] = participants.childNodes(p).selectSingleNode('label').text;
  }
	
	var alreadyExists = true;
	
	if ( document.getElementById( "name_"+sid ) == null )
	{
		if ( !redefining )
		{
			var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		}
		var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
		alreadyExists = false;
	}
	
	varHTML[j++]="<table cellpadding=0 cellspacing=0 class='section' >"
	varHTML[j++]="<col width=150px />"
	varHTML[j++]="<col width=100% />"    
  varHTML[j++] ="</table>";
    

	if ( !alreadyExists )
	{
		htm[i++]="<table id='readSection' cellpadding=0 cellspacing=0 class='section' >"
		if ( !redefining )
		{
		htm[i++]="<col width=150px />"
		}
		else
		{
		htm[i++]="<col width=90px />"
		}
		htm[i++]="<col width=100% />"

		if ( !redefining )
		{
			htm[i++]="<tr><td>"
			htm[i++]="Nome";
			htm[i++]="</td>";
			htm[i++]="<td>";
			htm[i++]=nameHTML;
			htm[i++]="</td></tr>";
		}
	
		htm[i++]="<tr><td>"
		htm[i++]="Rótulo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=labelHTML;
		htm[i++]="</td></tr>";
		
		for( j=0 ; j< partExt.length ; j++ )
		{
			htm[i++]="<tr><td colspan=2 valign=top'>"
			htm[i++]="<input onclick='toggleExecProc(\""+sid+"\",\""+partInt[j]+"\")' style='border:0' ";
			if ( existExecProc(partInt[j],node) )
			{
				htm[i++]=" checked "
			}
			htm[i++]=" type=checkbox />&nbsp;"+partExt[j];
				
			htm[i++]="</td></tr>";
		}
	
		htm[i++]="</table>";
		htmNode.innerHTML= htm.join("");
	}
	
	if ( !redefining )
	{
		document.getElementById( "name_"+sid ).focus();	
	}
	else
	{
		_newVariableObject( node , nodeVar ,objectType);
		document.getElementById( "label_"+sid ).focus();	
	}
}

function readFromHTML_DEFPROCEDURE( node )
{
	
	var sid = node.getAttribute("sid");
	var label = getValue( "label_"+sid );
	var description = getValue( "code_"+sid );
	var name = getValue( "name_"+sid );

	//node.selectSingleNode("description").firstChild.text=description;
	getCdataNode( node , "label" ).text = label;
	
	if ( name != node.getAttribute("name"))
	{
		var search="//*/variable[@name='"+node.getAttribute("name")+"']"; //NTRIMJS
		var nodes=xmlSrc.firstChild.selectNodes(search);
		for ( var i = 0 ; i < nodes.length;  i++ )
		{
			nodes(i).setAttribute("name",name);
		}
		node.setAttribute("name",name);
	}
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node ); //actualiza viwer
	
	return true;
}


function elements_closeDEFPROCEDURE( node )
{
	readFromHTML_DEFPROCEDURE( node )
	return true;

}
/************************  END DEFPROCEDURE ************************/


/************************   WAITTHREAD  **********************************/
function elements_showWAITTHREAD( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	
	var label = node.selectSingleNode("label").text;
	var time = node.selectSingleNode("time").text;
	var ontimeout = node.selectSingleNode("ontimeout").text;
	var logicalOperator = node.selectSingleNode("logicalOperator").text;
	
	
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var timeHTML = createFieldText( time , "time_"+sid,"time_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var ontimeoutHTML = createFieldText( ontimeout , "ontimeout_"+sid,"ontimeout_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	var logicInt=[];
	var logicExt=[];
	
	var logicInt=["and","or"];
	var logicExt=["Todas","Uma delas"];
	
	
	var logicalOperatorHTML = createFieldCombo(logicalOperator,"logicalOperator_"+sid,"logicalOperator_"+sid,"1", logicExt , logicInt ,false,false,"changeAtr" );	
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Tempo de Espera";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=timeHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Se o tempo expirar";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=ontimeoutHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Espera por";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=logicalOperatorHTML;
	htm[i++]="</td></tr>";
	
		
	
	htm[i++]="<tr  ><td class='headerVarActivity' colspan=2>"
	htm[i++]="Esperar por :";
	htm[i++]="</td>";
	
	htm[i++]="</td></tr>";
	
	
	var nodes = TREE_EDIT.code.selectNodes("//*");
	
	//var search="//*/variable[@name='"+node.getAttribute("name")+"']"; //NTRIMJS
	
	var existsThreads=false;
	for ( var p = 0 ; p < nodes.length;p++ )
	{
	  var name=nodes(p).getAttribute("name");
	  var nodeName=nodes[p].nodeName
	  
	  if ( name && ( nodeName =='fillVariable' || nodeName =='thread' || nodeName =='activity' || nodeName =='decision' || nodeName =='menu' || nodeName =='choice' || nodeName =='send' || nodeName =='waitResponse') )
	  {
		var isAsync = nodeName =='thread' || nodes[p].getAttribute("async")=="true";
		if ( isAsync )
		{
			existsThreads=true;
			var exists = false;
			var threads = node.selectSingleNode("threads");
			for ( j= 0; j< threads.childNodes.length ; j++ )
			{
				var nameThread = threads.childNodes(j).getAttribute("name")
				if ( nameThread == name )
				{
				   exists=true;
				   break;
				}
			}
		
		
					
			var hv=[];
			var k=0;
		
			hv[k++] ="<table class=layout cellpadding=0 cellspacing=0>";
			hv[k++] ="<colgroup><col width=25><col width=100%>";
			hv[k++] ="</colgroup>";
			hv[k++] ="<tr>";
		
			hv[k++] ="<td><input class='rad' onclick='"
			hv[k++] ="toggleThreadAct(\""+sid+"\",\""+nodes(p).getAttribute("sid")+"\")";
			hv[k++] ="' type='checkbox' ";
			if( exists )
			{
			hv[k++] = " checked ";
			}
		
			hv[k++] = " / id='checkbox'1 name='checkbox'1>";
		
			hv[k++] ="</td>";
		
			hv[k++] ="<td>";
		
			hv[k++] = elements_getHtml( nodes(p) );
		
			hv[k++] ="</td>";
		
		
			hv[k++] ="</tr>";
			hv[k++] ="</table>";
		
			htm[i++]="<tr ><td class='varActivity' colspan=2>"+hv.join("")+"</td></tr>";
		
		}
	   }
	}

	if ( !existsThreads )
	{
		htm[i++]="<tr  ><td  colspan=2>"
		htm[i++]="Não existem caminhos nem tão pouco tarefas assíncronas";
		htm[i++]="</td>";
	}
		
	
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "label_"+sid ).focus();		
	
}

function toggleThreadAct( sidWait , sidActivity )
{
	
	var o = window.event.srcElement;
	var search="//*[@sid='"+sidActivity+"']"; //NTRIMJS
	
	var nodeActivity=xmlSrc.firstChild.selectSingleNode(search);
	
	var search="//*[@sid='"+sidWait+"']"; //NTRIMJS
	var nodeWait=xmlSrc.firstChild.selectSingleNode(search);
	
	var threads = nodeWait.selectSingleNode("threads");
	if ( o.checked )
	{
		//insert
	
		var newthread = xmlSrc.createElement("waitFor");
		newthread.setAttribute("name", nodeActivity.getAttribute("name") );
		threads.appendChild( newthread );
	}
	else
	{
		//remove
		var nameThread = nodeActivity.getAttribute("name");
		for ( j= 0; j< threads.childNodes.length ; j++ )
		{
			var nameThreadAct = threads.childNodes(j).getAttribute("name")
			if ( nameThreadAct == nameThread )
			{
			   threads.removeChild( threads.childNodes(j) );
			   break;
			}
		}
		
	}
}

function elements_closeWAITTHREAD( node )
{
	readFromHTML_WAITTHREAD( node )
	return true;

}
function readFromHTML_WAITTHREAD( node )
{
	
	var sid = node.getAttribute("sid");
	var time = getValue("time_"+sid);
	var ontimeout = getValue("ontimeout_"+sid);
	var label = getValue( "label_"+sid );
	var logicalOperator = getValue( "logicalOperator_"+sid );
	
	
	node.selectSingleNode("logicalOperator").text=logicalOperator;
	getCdataNode( node , "label" ).text = label;
	getCdataNode( node , "time" ).text = time;
	getCdataNode( node , "ontimeout" ).text = ontimeout;
//	node.selectSingleNode("label").firstChild.text=label;
//	node.selectSingleNode("time").firstChild.text=time;
//	node.selectSingleNode("ontimeout").firstChild.text=ontimeout;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END WAITTREAD ************************/

/************************ THREAD  **********************************/
function elements_showTHREAD( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	var optional = node.getAttribute("optional");
	var name = node.getAttribute("name");
	
	var label = node.selectSingleNode("label").text;
	var nameHTML=createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)	
	var requiredWhen = node.selectSingleNode("requiredWhen").text;
	var optionalWhen = node.selectSingleNode("optionalWhen").text;
	var participant = node.selectSingleNode("participant").getAttribute("name");
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	var requiredWhenHTML = createFieldText( requiredWhen , "requiredWhen_"+sid,"requiredWhen_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	var optionalWhenHTML = createFieldText( optionalWhen , "optionalWhen_"+sid,"optionalWhen_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	var participants=TREE_EDIT.code.selectSingleNode("//defParticipants");
	
	var partInt=[];
	var partExt=[];
	
	var optionalInt=["false","true"];
	var optionalExt=["Não","Sim"];
	
	
	for ( var p=0 ; p < participants.childNodes.length ; p++ )
	{
		partInt[ p ] = participants.childNodes(p).getAttribute("name");
		partExt[ p ] = participants.childNodes(p).selectSingleNode('label').text;
	}
	
	for ( var p=0 ; p < participants.childNodes.length ; p++ )
	{
		partInt[ p ] = participants.childNodes(p).getAttribute("name");
		partExt[ p ] = participants.childNodes(p).selectSingleNode('label').text;
	}
	
	var participantHTML = "<b>não existem participantes definidos</b>";
	
	if ( partInt.length > 0 )
	{
		participantHTML=createFieldCombo(participant,"participant_"+sid,"participant_"+sid,"1", partExt , partInt ,false,false,"changeAtr" );	
	}
	
	
	
	//createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Nome interno";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Participante";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=participantHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Opcional Quando";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=optionalWhenHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Obrigatório quando";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=requiredWhenHTML;
	htm[i++]="</td></tr>";
	
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "label_"+sid ).focus();		
	
	
}

function elements_closeTHREAD( node )
{
	readFromHTML_THREAD( node )
	return true;

}
function readFromHTML_THREAD( node )
{
	
	var sid = node.getAttribute("sid");
	
	var label = getValue( "label_"+sid );
	var name = getValue( "name_"+sid );
	var requiredWhen = getValue( "requiredWhen_"+sid );
	var optionalWhen = getValue( "optionalWhen_"+sid );
	var participant = getValue( "participant_"+sid );
	
	node.setAttribute("name",name);
	
	getCdataNode( node , "label" ).text = label;
	getCdataNode( node , "requiredWhen" ).text = requiredWhen;
	getCdataNode( node , "optionalWhen" ).text = optionalWhen;
	
	//node.selectSingleNode("label").firstChild.text=label;
	//node.selectSingleNode("requiredWhen").firstChild.text=requiredWhen;
	//node.selectSingleNode("optionalWhen").firstChild.text=optionalWhen;
	
	if ( participant )
	{
	node.selectSingleNode("participant").setAttribute("name",participant);
	}
	else
	{
	node.selectSingleNode("participant").setAttribute("name","");
	}
	
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/*********END THREAD *******/


/************************ ANSWER  **********************************/
function elements_showANSWER( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	
	var label = node.selectSingleNode("label").text;
	
	var availableWhen = node.selectSingleNode("availableWhen").text;
	var disableWhen = node.selectSingleNode("disableWhen").text;
	
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	
	var availableWhenHTML = createFieldText( availableWhen , "availableWhen_"+sid,"availableWhen_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	var disableWhenHTML = createFieldText( disableWhen , "disableWhen_"+sid,"disableWhen_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	
	//createFieldCombo("123","aaa","a1","1",["aaa","bbb","ccc"],["3","123","2"],false,false,"" );
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML;
	htm[i++]="</td></tr>";
	
		
	htm[i++]="<tr><td>"
	htm[i++]="Disponível quando";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=availableWhenHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Escondido quando";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=disableWhenHTML;
	htm[i++]="</td></tr>";
	
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "label_"+sid ).focus();		
	
	
}

function elements_closeANSWER( node )
{
	readFromHTML_ANSWER( node )
	return true;

}
function readFromHTML_ANSWER( node )
{
	
	var sid = node.getAttribute("sid");
	
	var label = getValue( "label_"+sid );
	
	var availableWhen = getValue( "availableWhen_"+sid );
	var disableWhen = getValue( "disableWhen_"+sid );

	getCdataNode( node , "label" ).text = label;
	//node.selectSingleNode("label").firstChild.text=label;
	getCdataNode( node , "availableWhen" ).text = availableWhen;
	//node.selectSingleNode("availableWhen").firstChild.text=availableWhen;
	getCdataNode( node , "disableWhen" ).text = disableWhen;
	//node.selectSingleNode("disableWhen").firstChild.text=disableWhen;
	
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/*********END ANSWER *******/


/************************   GOTO  **********************************/
function elements_showGOTO( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	var gotoLabel = node.getAttribute("label");
	
	var optionalInt=["false","true"];
	var optionalExt=["Não","Sim"];
	
	var gotoWhereInt=[]
	var gotoWhereExt=[]
	
	var nodes = TREE_EDIT.code.selectNodes("//*");
	for ( var p = 0 ; p < nodes.length;p++ )
	{
	  var name=nodes(p).getAttribute("name");
	  
	  if ( name && nodes[p].nodeName !='defParticipant' && nodes[p].nodeName !='defVariable' &&
			nodes[p].nodeName !='participant' && nodes[p].nodeName !='variable'&&
			nodes[p].nodeName !='program' && nodes[p].nodeName !='waitFor'
			)
	  {
			
			gotoWhereInt[ gotoWhereInt.length ] = name;
			gotoWhereExt[ gotoWhereExt.length ] = name;
	  }
		
	}
		
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"

	if ( gotoWhereInt.length > 0 )
	{
		var gotoLabelHTML = createFieldCombo(gotoLabel,"gotoLabel_"+sid,"gotoLabel_"+sid,"1", gotoWhereExt , gotoWhereInt ,false,false,"changeAtr" );	
		htm[i++]="<tr><td>"
		htm[i++]="Saltar para";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=gotoLabelHTML;
		htm[i++]="</td></tr>";
	
		htm[i++]="<tr><td >"
		htm[i++]="Condição";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=conditionHTML ;
		htm[i++]="</td></tr>";
	}
	else
	{
		htm[i++]="<tr><td colspan=2>"
		htm[i++]="Não existem RÓTULOS nem tão pouco actividades com <b>nome</b>";
		htm[i++]="</td></tr>";
	}
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeGOTO( node )
{
	readFromHTML_GOTO( node )
	return true;

}
function readFromHTML_GOTO( node )
{
	
	var sid = node.getAttribute("sid");
	var gotoLabel = getValue("gotoLabel_"+sid);
	var condition = getValue("condition_"+sid);
	
	node.setAttribute("label",gotoLabel);
	getCdataNode( node , "condition" ).text = condition;
	//node.selectSingleNode("condition").firstChild.text=condition;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END GOTO ************************/

/************************   IF  **********************************/
function elements_showIF( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeIF( node )
{
	readFromHTML_IF( node )
	return true;

}
function readFromHTML_IF( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	getCdataNode( node , "condition" ).text = condition;
	//node.selectSingleNode("condition").firstChild.text=condition;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END IF ************************/

/************************   WHILE  **********************************/
function elements_showWHILE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeWHILE( node )
{
	readFromHTML_WHILE( node )
	return true;

}
function readFromHTML_WHILE( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	getCdataNode( node , "condition" ).text = condition;
	//node.selectSingleNode("condition").firstChild.text=condition;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END WHILE ************************/

/************************   CASE  **********************************/
function elements_showCASE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeCASE( node )
{
	readFromHTML_CASE( node )
	return true;

}
function readFromHTML_CASE( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	getCdataNode( node , "condition" ).text = condition;
	//node.selectSingleNode("condition").firstChild.text=condition;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END CASE ************************/


/************************   FOREACH  **********************************/
function elements_showFOREACH( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var foreachvariable = node.selectSingleNode("foreachvariable").text;


	
	
	var foreachvarInt=[]
	var foreachvarExt=[]

	var defVars = TREE_EDIT.code.selectSingleNode("//defVariables");	
	
	for ( var p = 0 ; p < defVars.childNodes.length;p++ )
	{
			var name = defVars.childNodes(p).getAttribute("name");
			var type = defVars.childNodes(p).selectSingleNode("type").text;
			var maxoccurs = parseInt( defVars.childNodes(p).selectSingleNode("maxoccurs").text,10 );
			if ( type.substr(0,6)=='object' && maxoccurs >1)
			{
				var labelVar = defVars.childNodes(p).selectSingleNode("label").text
				foreachvarInt[ foreachvarInt.length ] = name;
				foreachvarExt[ foreachvarExt.length ] = labelVar+"("+name+")";
			}

		
	}
	var foreachvariableHTML = createFieldCombo(foreachvariable,"foreachvariable_"+sid,"foreachvariable_"+sid,"1", foreachvarExt , foreachvarInt ,false,false,"changeAtr" );	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	if ( foreachvarInt.length > 0)
	{
		htm[i++]="<tr><td>"
		htm[i++]="Atributo";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=foreachvariableHTML ;
		htm[i++]="</td></tr>";
	}
	else
	{
		htm[i++]="<tr><td colspan=2>"
		htm[i++]="Não existem  atributos do tipo <b>Lista de objectos</b>";
		htm[i++]="</td></tr>";
	}
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeFOREACH( node )
{
	readFromHTML_FOREACH( node )
	return true;

}
function readFromHTML_FOREACH( node )
{
	
	var sid = node.getAttribute("sid");
	
	var variable = getValue("foreachvariable_"+sid);
	if ( variable )
	{
		node.selectSingleNode("foreachvariable").text=variable;
	}
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END FOREACH ************************/


/************************   BEGINTIME  **********************************/
function elements_showBEGINTIME( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	var timerVariable = node.selectSingleNode("timerVariable").text;
	var time = node.selectSingleNode("time").text;
	var label = node.selectSingleNode("label").text;
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var timerVariableHTML = createFieldText( timerVariable , "timerVariable_"+sid,"timerVariable_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var timeHTML = createFieldText( time , "time_"+sid,"time_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,5000);
	
	htm[i++]="<table cellpadding=0 style='height:100%' cellspacing=0 class='section' >"
	htm[i++]='<TBODY>';
  htm[i++]='<TR height="25">';
  htm[i++]='<TD>';
  htm[i++]='<TABLE cellpadding="0" cellspacing="0" class="tabBar" id="49_body" onkeyup="so(\'49\');onKeyUpTab_std(event)" onmouseover="so(\'49\');onOverTab_std(event)" onmouseout="so(\'49\');onOutTab_std(event)" ondragenter="so(\'49\');ondragenterTab_std(event)" ondragover="so(\'49\');ondragoverTab_std(event)" onclick="so(\'49\');onClickTab_std(event)">';
  htm[i++]='<TBODY>';
  htm[i++]='<TR>'
  htm[i++]='<TD style="padding:0px" id="49_tabs" valign="bottom" noWrap="yes">'
  htm[i++]='<SPAN class="tab tabOn" id="49_tabheader_0" name="tab_settings" tabNumber="49" tabIndex="287">Dados da tarefa</SPAN>';
	htm[i++]='<SPAN class="tab" id="49_tabheader_1" name="tab_alerts" tabNumber="49" tabIndex="287">Alertas na tarefa</SPAN>';

	htm[i++]='</TD>';
  htm[i++]='</TR>';
  htm[i++]='</TBODY>';
  htm[i++]='</TABLE>';
  htm[i++]='<HR class="tabGlow" id="hrSelTab49"/>';
  htm[i++]='<HR class="tabGlow" id="hrTab49"/>';
  htm[i++]='</TD>';
  htm[i++]='</TR>';
  htm[i++]='<TR>';
  htm[i++]='<TD>';
  htm[i++]='<DIV id="49_tabbody_0" class="tab" style="overflow-y:auto">';

	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%' >"

	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	

	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=timerVariableHTML ;
	htm[i++]="</td></tr>";

	htm[i++]="<tr><td colspan=2 style='height:100%'>"
	htm[i++]=labelHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Tempo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=timeHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	var deadLineDate = "";
	var deadLineSplit = "";
	var deadNumber = "";
	var deadTime = "";
	var deadTimeMode = "";
	var deadTimeConstraint = "";
	var deadTimeReference = "";
	
	if(node.selectSingleNode("deadLineDate") != null)
	{
		deadLineDate = node.selectSingleNode("deadLineDate").text;
		deadLineSplit = deadLineDate.split(";")
		deadNumber = deadLineSplit[0]?deadLineSplit[0]:"";
		deadTime = deadLineSplit[1]?deadLineSplit[1]:"";
		deadTimeMode = deadLineSplit[2]?deadLineSplit[2]:"";
		deadTimeConstraint = deadLineSplit[3]?deadLineSplit[3]:"";
		deadTimeReference = deadLineSplit[4]?deadLineSplit[4]:"";
	}
	
	var deadNumberHTML = createFieldNumber( deadNumber ,"deadNumber_"+sid ,"deadNumber_"+sid ,"1","","0",false,99999,0,false,true,"changeAtr",false,false);

    var deadTimeInt=["day","hour","minute"];
	var deadTimeExt=["Dia(s)","Hora(s)","Minuto(s)"];
	var deadTimeHTML=createFieldCombo(deadTime,"deadTime_"+sid,"deadTime_"+sid,"1", deadTimeExt , deadTimeInt ,false,false,"changeAtr" );
	
	var deadTimeModeInt=["linear","util"];
	var deadTimeModeExt=["Calendário","Úteis"];
	var deadTimeModeHTML=createFieldCombo(deadTimeMode,"deadTimeMode_"+sid,"deadTimeMode_"+sid,"1", deadTimeModeExt , deadTimeModeInt ,false,false,"changeAtr" );
    
	var deadTimeConstraintInt=["afterCreate","afterEnd"];
	var deadTimeConstraintExt=["Após o lançamento","Após o fim"];
	var deadTimeConstraintHTML=createFieldCombo(deadTimeConstraint,"deadTimeConstraint_"+sid,"deadTimeConstraint_"+sid,"1", deadTimeConstraintExt , deadTimeConstraintInt ,false,false,"changeDeadTimeConstraint" );
    
    
    
	var deadTimeReferenceInt=["__program","__task"];
	var deadTimeReferenceExt=["Do programa","Desta tarefa"];

	 var nodes = TREE_EDIT.code.selectNodes("//*");
	 for ( var p = 0 ; p < nodes.length && sid!= nodes(p).getAttribute("sid");p++ )
	 {	
		var name=nodes(p).getAttribute("name");
		var nodeName =nodes(p).nodeName;
		
	    if ( name && nodes(p).selectSingleNode('label') && 
	        ( 
	        nodeName  =='menu' || nodeName  =='choice' || nodeName =='decision'  || nodeName=='activity' || nodeName=='fillVariable' 
	        || nodeName =='send' || nodeName =='createMessage'  || nodeName=='waitResponse' || nodeName=='userCallProgram' )
	    
	     )
		{
			deadTimeReferenceInt[ deadTimeReferenceInt.length ]=name;
			deadTimeReferenceExt[ deadTimeReferenceExt.length ]="TAREFA:"+nodes(p).selectSingleNode('label').text;
	    }
	}
	
	var deadTimeReferenceHTML=createFieldCombo(deadTimeReference,"deadTimeReference_"+sid,"deadTimeReference_"+sid,"1", deadTimeReferenceExt , deadTimeReferenceInt ,false,false,"changeAtr" );
    
	
	htm[i++]="<tr><td>"
	htm[i++]="Limite de Execução";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]="<table cellpadding=0 cellspacing=0 style='table-layout:fixed' >"
	
	htm[i++]="<col width=60px/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col />";
	
	htm[i++]="<tr><td>";
	htm[i++]=deadNumberHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeModeHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=deadTimeConstraintHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td id='dtRfr'>";
	htm[i++]=deadTimeReferenceHTML;
	htm[i++]="</td>";
	
	htm[i++]="</td></tr></table>"
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	htm[i++]="</DIV>";
	htm[i++]='<DIV id="49_tabbody_1" class="tab">';
	//para os alertas
	htm[i++] ="<table style='width:100%;height:100%' cellpadding=0 cellspacing=0>";
	htm[i++] ="<tr><td valign=top align=left style='width:100%'>";
	//end hader
	
	i= renderActivityAlerts( node, sid , htm,i);
	
	//para as variaveis e participantes BODY DE VARIAVEIS
	
	htm[i++] ="</td></tr></table>";
	//end hader

   	 htm[i++]="</DIV>";
  htm[i++]='</TD>'
	htm[i++]='</TR>'
	htm[i++]='</TBODY>'
	htm[i++]='</TABLE>';
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeBEGINTIME( node )
{
	readFromHTML_BEGINTIME( node )
	return true;

}
function readFromHTML_BEGINTIME( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	var timerVariable = getValue("timerVariable_"+sid);
	var time = getValue("time_"+sid);
	var label = getValue("label_"+sid);
	var deadNumber = getValue( "deadNumber_"+sid );
	var deadTime = getValue( "deadTime_"+sid );
	var deadTimeMode = getValue( "deadTimeMode_"+sid );
	var deadTimeConstraint = getValue( "deadTimeConstraint_"+sid );
	var deadTimeReference = getValue( "deadTimeReference_"+sid );
	
	var deadLineDate = deadNumber+";"+deadTime+";"+deadTimeMode+";"+deadTimeConstraint+";"+deadTimeReference;
	
	//node.selectSingleNode("condition").firstChild.text=condition;
	getCdataNode( node , "condition" ).text = condition;
	//node.selectSingleNode("label").firstChild.text=label;
	getCdataNode( node , "label" ).text = label;
	//node.selectSingleNode("time").firstChild.text=time;
	getCdataNode( node , "time" ).text = time;
	getCdataNode( node , "deadLineDate" ).text = deadLineDate;
	node.selectSingleNode("timerVariable").text=timerVariable;
	
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END BEGINTIME ************************/


/************************   STOPTIME  **********************************/
function elements_showSTOPTIME( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	var timerVariable = node.selectSingleNode("timerVariable").text;
	
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	
	
	var timervarInt=[]
	var timervarExt=[]

	
	var nodes = TREE_EDIT.code.selectNodes("//*");
	
	for (  var p = 0 ; p < nodes.length;p++ )
	{
			var nodeName=nodes[p].nodeName
			if ( nodeName=="beginTime" )
			{
				var name=nodes(p).selectSingleNode("timerVariable").text;
				timervarInt[ timervarInt.length ] = name;
				timervarExt[ timervarExt.length ] = name;
			}
	}
	

	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	if ( timervarInt.length > 0 )
	{
		var timervarHTML = createFieldCombo(timerVariable,"timerVariable_"+sid,"timerVariable_"+sid,"1", timervarExt , timervarInt ,false,false,"changeAtr" );
		htm[i++]="<tr><td>"
		htm[i++]="Nome";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=timervarHTML ;
		htm[i++]="</td></tr>";

	
		htm[i++]="<tr><td>"
		htm[i++]="Condição";
		htm[i++]="</td>";
		htm[i++]="<td>";
		htm[i++]=conditionHTML ;
		htm[i++]="</td></tr>";
	}
	else
	{
		htm[i++]="<tr><td colspan=2>"
		htm[i++]="Não existem variáveis do tipo CONTAGEM TEMPO";
		htm[i++]="</td>";
		htm[i++]="</tr>";
	}
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeSTOPTIME( node )
{
	readFromHTML_STOPTIME( node )
	return true;

}
function readFromHTML_STOPTIME( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	var timerVariable = getValue("timerVariable_"+sid);
	
	if ( timerVariable )
	{
		//node.selectSingleNode("condition").firstChild.text=condition;
		getCdataNode( node , "condition" ).text = condition;
		
		node.selectSingleNode("timerVariable").text=timerVariable;
	}
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END STOPTIME ************************/


/************************   WAITTIME  **********************************/
function elements_showWAITTIME( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	var time = node.selectSingleNode("time").text;
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var timeHTML = createFieldText( time , "time_"+sid,"time_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Tempo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=timeHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	//document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeWAITTIME( node )
{
	readFromHTML_WAITTIME( node )
	return true;

}
function readFromHTML_WAITTIME( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	
	var time = getValue("time_"+sid);
	//node.selectSingleNode("condition").firstChild.text=condition;
	getCdataNode( node , "condition" ).text = condition;
	getCdataNode( node , "time" ).text = time;
	//node.selectSingleNode("time").firstChild.text=time;
	
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END WAITTIME ************************/

/************************   MILESTONE E PROGRAMLABEL EXIT E TERMINATE **********************************/
function elements_showMILESTONE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var name = node.getAttribute("name");
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "name_"+sid ).focus();		
	
}


function elements_closeMILESTONE( node )
{
	readFromHTML_MILESTONE( node )
	return true;

}
function readFromHTML_MILESTONE( node )
{
	
	var sid = node.getAttribute("sid");
	
	var name = getValue("name_"+sid);
	node.setAttribute("name",name);
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END MILESTONE E LABEL EXIT e TERMINATE ************************/

/************************   COMMENT  **********************************/
function elements_showCOMMENT( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	
	
	var comm = node.text
	var commHTML = createFieldText( comm , "comm_"+sid,"comm_"+sid,"1", false ,true ,"changeAtr()",false,true,5000);
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%'>"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td colspan=2 style='height:100%;width:100%'>"
	htm[i++]=commHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "comm_"+sid ).focus();		
	
}


function elements_closeCOMMENT( node )
{
	readFromHTML_COMMENT( node )
	return true;

}
function readFromHTML_COMMENT( node )
{
	
	var sid = node.getAttribute("sid");
	
	var comm=getValue("comm_"+sid);
	node.firstChild.text = comm;
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END COMMENT************************/

/************************   XEPCODE  **********************************/
function elements_showXEPCODE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var condition = node.selectSingleNode("condition").text;
	var xepcode = node.selectSingleNode("codetorun").text;
	
	
	var conditionHTML = createFieldText( condition , "condition_"+sid,"condition_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	var xepcodeHTML = createFieldText( xepcode , "xepcode_"+sid,"xepcode_"+sid,"1", false ,true ,"changeAtr()",false,true,5000);
	
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	

	htm[i++]="<tr><td height='100%' colspan=2>"
	htm[i++]= xepcodeHTML ;
	htm[i++]="</td></tr>";

	
	htm[i++]="<tr><td>"
	htm[i++]="Condição";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=conditionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "xepcode_"+sid ).focus();		
	
}


function elements_closeXEPCODE( node )
{
	readFromHTML_XEPCODE( node )
	return true;

}
function readFromHTML_XEPCODE( node )
{
	
	var sid = node.getAttribute("sid");
	
	var condition = getValue("condition_"+sid);
	var xepcode = getValue("xepcode_"+sid);
	
	//node.selectSingleNode("condition").firstChild.text=condition;
	getCdataNode( node , "condition" ).text = condition;
	
	//node.selectSingleNode("codetorun").firstChild.text=xepcode;
	getCdataNode( node , "codetorun" ).text = xepcode;
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END XEPCODE ************************/

/************************   TRUE FALSE DECISION  **********************************/
function elements_showTRUEFALSE( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var label = node.selectSingleNode("label").text;
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,5000);
		
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	

	htm[i++]="<tr><td height='100%' colspan=2>"
	htm[i++]= labelHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closeTRUEFALSE( node )
{
	readFromHTML_TRUEFALSE( node )
	return true;

}
function readFromHTML_TRUEFALSE( node )
{
	
	var sid = node.getAttribute("sid");
	var label = getValue("label_"+sid);
	getCdataNode( node , "label" ).text = label;
	//node.selectSingleNode("label").firstChild.text=label;
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END TRUEFALSE DECISION ************************/

/************************   CALLPROGRAM  **********************************/
function elements_showCALLPROGRAM( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var name = node.getAttribute("name");
	var program = node.getAttribute("programName");
	var mode = node.getAttribute("mode");
	var async = node.getAttribute("async");
	
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);

	
	
	
	var varInt=[];
	var varExt=[];
	
	var modeInt=["embedded","outsource"];
	var modeExt=["Embebido","Isolado"];
    var modeHTML=createFieldCombo(mode,"mode_"+sid,"mode_"+sid,"1", modeExt , modeInt ,false,false,"changeAtr" );
    
   	var optionalInt=["false","true"];
	var optionalExt=["Não","Sim"];
	var asyncHTML = createFieldCombo(async,"async_"+sid,"async_"+sid,"1", optionalExt , optionalInt ,false,false,"changeAtr" );

		
	var programHTML = createDetachFieldLookup(
        programObjects[ program ],
        "programName_"+sid,
        "programName_"+sid,
        "xwfProgram",
        "xwfProgram",
        "Programa",
        getDocId(), //docid
        "single", //single or multi
        1,
        false, //isdisable
        true,
        "newCallProgram(\""+sid+"\")"
        )
        
        
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' style='height:100%' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"

	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Programa";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= programHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Modo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= modeHTML;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Async";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]= asyncHTML;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="<tr id='mapping' height=250px><td colspan=2 id='setPar' >"
	htm[i++]= "";
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr height=100%><td>&nbsp;"
	htm[i++]="</td></tr>";

	htm[i++]="</table>";
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "name_"+sid ).focus();		
	
}

function newCallProgram(sid)
{
 
	var programName = getValue("programName_"+sid);
	var ifrm=[];
	var ii=0;
	
	ifrm[ii++]="<iframe id='frameMapping' scrolling=no frameborder=0 style='height:100%;width:100%' src='";
	ifrm[ii++]="__xwfDesigner_callProgram.jsp?programBoui=";
	ifrm[ii++]=programName;
	ifrm[ii++]="&docid=";
	ifrm[ii++]=getDocId();
	ifrm[ii++]="&sid="
	ifrm[ii++]=sid;
	ifrm[ii++]="'";
	ifrm[ii++]=" ></iframe>";
	document.getElementById("setPar").innerHTML=ifrm.join("");
	
}

function elements_closeCALLPROGRAM( node )
{
	readFromHTML_CALLPROGRAM( node )
	return true;

}
function readFromHTML_CALLPROGRAM( node )
{
	
	var sid = node.getAttribute("sid");
	var name = getValue("name_"+sid);
	var mode = getValue("mode_"+sid);
	var async = getValue("async_"+sid);
	var programName = getValue("programName_"+sid);
	
	node.setAttribute("name", name);
	node.setAttribute("mode", mode);
	node.setAttribute("async", async);
	
	var xp= "";
	for( var i in programObjects )
	{
		if( programName == programObjects[i] )
		{
		 xp=i;
		 break;
		}
	}
	
	
	node.setAttribute("programName", xp );
	
	var frm = document.getElementById("frameMapping");
	if ( frm && frm.contentWindow.document &&frm.contentWindow.document.readyState=="complete")
	{
		 var d=frm.contentWindow.document;
		 var w=frm.contentWindow;
		 var mapVarRows = d.getElementById("mapVar").rows;
		 var mapParRows = d.getElementById("mapPar").rows;
		 
		 var mapVars=node.selectSingleNode("mappingVariables");
		 while ( mapVars.childNodes.length > 0 )
		 {
			mapVars.removeChild( mapVars.firstChild );
		 }
		 for ( var i=1 ; i< mapVarRows.length ; i++ )
		 {
			var o_var = w.getValue("variable_"+(i-1));	
			var hlink = w.getValue("hlink_"+(i-1));	
			if (o_var && o_var.length > 0 )
			{
				var mapVar = xmlSrc.createElement("mappingVariable");
				mapVar.setAttribute("mainVar",o_var);
				mapVar.setAttribute("progVar", w.d_varInt[i-1] );
				mapVar.setAttribute("hardLink",hlink);
				mapVars.appendChild( mapVar );
			}
		 }
		 
		 var mapPars=node.selectSingleNode("mappingParticipants");
		 while ( mapPars.childNodes.length > 0 )
		 {
			mapPars.removeChild( mapPars.firstChild );
		 }
		 for ( var i=1 ; i< mapParRows.length ; i++ )
		 {
			var o_par = w.getValue("participant_"+(i-1));	
			
			if (o_par&& o_par.length > 0 )
			{
				var mapPar = xmlSrc.createElement("mappingParticipant");
				mapPar.setAttribute("mainPar",o_par);
				mapPar.setAttribute("progPar", w.d_partInt[i-1] );
				mapPars.appendChild( mapPar );
			}
		 }
		 
		 
		 
		 
	}
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}



/************************   PROGRAM DECISION  **********************************/
function elements_showPROGRAM( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	

	var name = node.getAttribute("name");
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,2000);
	
	var label = node.selectSingleNode("label").text;
	var labelHTML = createFieldText( label , "label_"+sid,"label_"+sid,"1", false ,true ,"changeAtr()",false,true,2000);
	
	var labelFormula = node.selectSingleNode("labelFormula").text;
	var labelFormulaHTML = createFieldText( labelFormula , "labelFormula_"+sid,"labelFormula_"+sid,"1", false ,true ,"changeAtr()",false,true,2000);
		
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section'  >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelHTML ;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Rótulo(formula)";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=labelFormulaHTML ;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "label_"+sid ).focus();		
	
}


function elements_closePROGRAM( node )
{
	readFromHTML_PROGRAM( node )
	return true;

}
function readFromHTML_PROGRAM( node )
{
	
	var sid = node.getAttribute("sid");
	var label = getValue("label_"+sid);
	getCdataNode( node , "label" ).text = label;
	//node.selectSingleNode("label").firstChild.text=label;

	var name = getValue("name_"+sid);
	node.setAttribute("name",name);
	
	var labelFormula = getValue("labelFormula_"+sid);
	getCdataNode( node , "labelFormula" ).text = labelFormula;
	//node.selectSingleNode("labelFormula").firstChild.text=labelFormula;

	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END TRUEFALSE DECISION ************************/


/************************   ALERT **********************************/

function elements_showALERT( node )
{
	var htm=[];
	var i=0;
	var sid = node.getAttribute("sid");
	var name = node.getAttribute("name");
//	var label = node.selectSingleNode("label").text;
//	var description = node.selectSingleNode("description").text;
//  var descriptionHTML = createFieldText( description , "description_"+sid,"description_"+sid,"1", false ,true ,"changeAtr()",false,true,5000)
	
	
	
	
	var nameHTML=createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)	
	var content = "";
	if ( node.firstChild )
	{
		content=node.firstChild.text;
	}
	
//content
//
// numero;day/hour/minute;linear/util;afterDeadline/beforeDeadline;alert/reassign;[name Participant] ;"[Mensagem]"
//
//
	
	var contentSplit = content.split(";")
	
	
	var contentNumber = contentSplit[0]?contentSplit[0]:"";
	var content = contentSplit[1]?contentSplit[1]:"";
	var contentMode = contentSplit[2]?contentSplit[2]:"";
	var contentConstraint = contentSplit[3]?contentSplit[3]:"";
	var contentAction = contentSplit[4]?contentSplit[4]:"";
	var contentParticipant = contentSplit[5]?contentSplit[5]:"";
	var contentProcedure = contentSplit[6]?contentSplit[6]:"";
	var contentMessage = contentSplit[7]?contentSplit[7]:"";
	
	
	var contentNumberHTML = createFieldNumber( contentNumber ,"contentNumber_"+sid ,"contentNumber_"+sid ,"1","","0",false,99999,0,false,true,"changeAtr",false,false);

    var contentInt=["day","hour","minute"];
	var contentExt=["Dia(s)","Hora(s)","Minuto(s)"];
	var contentHTML=createFieldCombo(content,"content_"+sid,"content_"+sid,"1", contentExt , contentInt ,false,false,"changeAtr" );
	
	var contentModeInt=["linear","util"];
	var contentModeExt=["Calendário","Úteis"];
	var contentModeHTML=createFieldCombo(contentMode,"contentMode_"+sid,"contentMode_"+sid,"1", contentModeExt , contentModeInt ,false,false,"changeAtr" );
    
	var contentConstraintInt=["afterDeadLine","beforeDeadLine"];
	var contentConstraintExt=["Depois do Limite","Antes do Limite"];
	var contentConstraintHTML=createFieldCombo(contentConstraint,"contentConstraint_"+sid,"contentConstraint_"+sid,"1", contentConstraintExt , contentConstraintInt ,false,false,"changeAtr" );
    
    
	var contentActionInt=["alert","reassign"];
	var contentActionExt=["Alerta","Reassigna"];
	var contentActionHTML=createFieldCombo(contentAction,"contentAction_"+sid,"contentAction_"+sid,"1", contentActionExt , contentActionInt ,false,false,"changeAtr" );
	
	
	var participants=TREE_EDIT.code.selectSingleNode("//defParticipants");	
	var partInt=[];
	var partExt=[];
	partInt[ 0 ] = "__assignedUser";
	partExt[ 0 ] = "A QUEM ESTA ASSIGNADA A TAREFA";
	for ( var p=0 ; p < participants.childNodes.length ; p++ )
	{
		partInt[ p+1 ] = participants.childNodes(p).getAttribute("name");
		partExt[ p+1 ] = participants.childNodes(p).selectSingleNode('label').text;
	}
	
	var participantHTML = "<b>não existem participantes definidos</b>";
	
	if ( partInt.length > 0 )
	{
		participantHTML=createFieldCombo(contentParticipant,"participant_"+sid,"participant_"+sid,"1", partExt , partInt ,false,false,"changeAtr" );	
	}
	var contentMessageHTML = createFieldText( contentMessage , "contentMessage_"+sid,"contentMessage_"+sid,"1", false ,true ,"changeAtr()",false,true,1000)	


	//procedimentos
	var procedures=TREE_EDIT.code.selectSingleNode("//defProcedures");
  var procInt=[];
  var procExt=[];

  for ( var p=0 ; p < procedures.childNodes.length ; p++ )
  {
  	if(p == 0)
  	{
  		procInt[ p ] = "";
  		procExt[ p ] = "&nbsp;";
  	}
  	procInt[ p + 1 ] = procedures.childNodes(p).getAttribute("name");
  	procExt[ p  + 1] = procedures.childNodes(p).selectSingleNode('label').text;
  }
  var procedureHTML="<b>não existem participantes definidos</b>";
  if ( procInt.length > 0 )
	{
		procedureHTML=createFieldCombo(contentProcedure,"procedure_"+sid,"procedure_"+sid,"1", procExt , procInt ,false,false,"changeAtr" );	
	}


	htm[i++]="<table cellpadding=0 cellspacing=0 class='section'  >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr><td>"
	htm[i++]="Nome";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=nameHTML ;
	htm[i++]="</td></tr>";
	
	
	htm[i++]="<tr><td>"
	htm[i++]="Quando:";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]="<table cellpadding=0 cellspacing=0 style='table-layout:fixed' >"
	
	htm[i++]="<col width=60px/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col width=20%/>";
	htm[i++]="<col width=2px/>";
	htm[i++]="<col />";
	
	htm[i++]="<tr><td>";
	htm[i++]=contentNumberHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=contentHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=contentModeHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	htm[i++]="<td>";
	htm[i++]=contentConstraintHTML;
	htm[i++]="</td>";
	htm[i++]="<td/>";
	
	htm[i++]="</td></tr></table>"
	
	
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Acção";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=contentActionHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="<tr><td>"
	htm[i++]="Participante";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=participantHTML ;
	htm[i++]="</td></tr>";
	htm[i++]="<tr><td>"
	
	htm[i++]="<tr><td>"
	htm[i++]="Procedimento";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=procedureHTML ;
	htm[i++]="</td></tr>";
	htm[i++]="<tr><td>"
	
	htm[i++]="Mensagem";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=contentMessageHTML ;
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	document.getElementById( "name_"+sid ).focus();		
	
	window.setTimeout("try{ document.getElementById( 'name_"+sid +"').focus()}catch(e){}",100);
	
	
}




function elements_closeALERT( node )
{
	readFromHTML_ALERT( node )
	return true;

}

function readFromHTML_ALERT( node ,fromAtr)
{
	
	var sid = node.getAttribute("sid");
	
	//var label = getValue( "label_"+sid );
	var name = getValue( "name_"+sid );
	//var description = getValue( "description_"+sid );
	
	var contentNumber = getValue( "contentNumber_"+sid );
	var contentDays = getValue( "content_"+sid );
	var contentMode = getValue( "contentMode_"+sid );
	var contentConstraint = getValue( "contentConstraint_"+sid );
	
	var contentAction = getValue( "contentAction_"+sid );
	var participant = getValue( "participant_"+sid );
	var procedure = getValue( "procedure_"+sid );
	var contentMessage = getValue( "contentMessage_"+sid );

	//numero;day/hour/minute;linear/util;afterDeadline/beforeDeadline;alert/reasign;[name Participant] ;"[Mensagem]"
	var content = contentNumber+";"+contentDays+";"+contentMode+";"+contentConstraint+";"+contentAction+";"+participant+";"+procedure+";"+contentMessage;
	
	
	node.setAttribute("name",name);
	
	//getCdataNode( node , "deadLineDate" ).text = deadLineDate;
	node.firstChild.text = content;
	//getCdataNode( node , "description" ).text = description;
	
	//node.selectSingleNode("description").firstChild.text= description;
	//getCdataNode( node , "label" ).text=label;
	
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	
	return true;
	
}
/************************  END ALERT ************************/

/************************   ADDPROCEDURES  **********************************/
function elements_showADDPROCEDURES( node )
{
	var htm=[];

	var i=0;
	var sid = node.getAttribute("sid");

  var procedures=TREE_EDIT.code.selectSingleNode("//defProcedures");
  var procInt=[];
  var procExt=[];

  for ( var p=0 ; p < procedures.childNodes.length ; p++ )
  {
  	procInt[ p ] = procedures.childNodes(p).getAttribute("name");
  	procExt[ p ] = procedures.childNodes(p).selectSingleNode('label').text;
  }

	var name = node.getAttribute("name");
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=200px />"
  htm[i++]="<col width=100% />"
	
	htm[i++]="<tr colspan=3 ><td>"
	htm[i++]="<b>Procedimentos</b>";
	htm[i++]="</td>";
	htm[i++]="<tr>";
  var b = false;
	for( j=0 ; j< procExt.length ; j++ )
  {
    htm[i++]="<tr><td colspan=2 valign=top'>"
    htm[i++]="<input onclick='toggleProc(\""+sid+"\",\""+procInt[j]+"\")' style='border:0' ";
    if ( existProc(procInt[j],node) )
    {
      b = true;
    	htm[i++]=" checked "
    }
    else
    {
      b = false;
    }
    htm[i++]=" type=checkbox />&nbsp;"+procExt[j];
    	
    htm[i++]="</td>"
    htm[i++]="<td style='align:left'>"
    htm[i++]="<input id='req"+procInt[j]+"' onclick='toggleReqProc(\""+sid+"\",\""+procInt[j]+"\")' style='border:0' ";
    if(b)
    {
      if ( existReqProc(procInt[j],node) )
      {
      	htm[i++]=" checked "
      }
    }
    else
    {      
      	htm[i++]=" disabled "
    }
    htm[i++]=" type=checkbox />&nbsp;Obrigatório";
    htm[i++]="</td></tr>";
  }
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
	
	
}


function elements_closeADDPROCEDURES( node )
{
	readFromHTML_ADDPROCEDURES( node )
	return true;

}
function readFromHTML_ADDPROCEDURES( node )
{
  var sid = node.getAttribute("sid");
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END ADDPROCEDURES************************/

/************************   REMOVEPROCEDURES  **********************************/
function elements_showREMOVEPROCEDURES( node )
{
	var htm=[];

	var i=0;
	var sid = node.getAttribute("sid");

  var procedures=TREE_EDIT.code.selectSingleNode("//defProcedures");
  var procInt=[];
  var procExt=[];

  for ( var p=0 ; p < procedures.childNodes.length ; p++ )
  {
  	procInt[ p ] = procedures.childNodes(p).getAttribute("name");
  	procExt[ p ] = procedures.childNodes(p).selectSingleNode('label').text;
  }

	var name = node.getAttribute("name");
	var nameHTML = createFieldText( name , "name_"+sid,"name_"+sid,"1", false ,true ,"changeAtr()",false,true,1000);
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	
	htm[i++]="<tr colspan=2 ><td>"
	htm[i++]="<b>Procedimentos</b>";
	htm[i++]="</td>";
	htm[i++]="<tr>";
  var b = false;
	for( j=0 ; j< procExt.length ; j++ )
  {
    htm[i++]="<tr><td colspan=2 valign=top'>"
    htm[i++]="<input onclick='toggleProc(\""+sid+"\",\""+procInt[j]+"\")' style='border:0' ";
    if ( existProc(procInt[j],node) )
    {
      b = true;
    	htm[i++]=" checked "
    }
    htm[i++]=" type=checkbox />&nbsp;"+procExt[j];
    htm[i++]="</td></tr>";
  }
	htm[i++]="</td></tr>";
	
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
}


function elements_closeREMOVEPROCEDURES( node )
{
	readFromHTML_REMOVEPROCEDURES( node )
	return true;

}
function readFromHTML_REMOVEPROCEDURES( node )
{
  var sid = node.getAttribute("sid");
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END REMOVEPROCEDURES************************/
/************************   REMOVEALLPROCEDURES  **********************************/
function elements_showREMOVEALLPROCEDURES( node )
{
	var htm=[];
	var sid = node.getAttribute("sid");
	var i=0;

	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
}


function elements_closeREMOVEALLPROCEDURES( node )
{
	readFromHTML_REMOVEALLPROCEDURES( node )
	return true;

}
function readFromHTML_REMOVEALLPROCEDURES( node )
{
  var sid = node.getAttribute("sid");
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END REMOVEALLPROCEDURES************************/
/************************   CALLPROCEDURES  **********************************/
function elements_showCALLPROCEDURES( node )
{
	var htm=[];

	var i=0;
	var sid = node.getAttribute("sid");

  var procedures=TREE_EDIT.code.selectSingleNode("//defProcedures");
  var procInt=[];
  var procExt=[];

  for ( var p=0 ; p < procedures.childNodes.length ; p++ )
  {
  	procInt[ p ] = procedures.childNodes(p).getAttribute("name");
  	procExt[ p ] = procedures.childNodes(p).selectSingleNode('label').text;
  }
  var procedure = node.selectSingleNode("procedure").getAttribute("name");
  var procedureHTML="<b>não existem participantes definidos</b>";
  if ( procInt.length > 0 )
	{
		procedureHTML=createFieldCombo(procedure,"procedure_"+sid,"procedure_"+sid,"1", procExt , procInt ,false,false,"changeAtr" );	
	}
	
	htm[i++]="<table cellpadding=0 cellspacing=0 class='section' >"
	htm[i++]="<col width=150px />"
	htm[i++]="<col width=100% />"
	

  htm[i++]="<tr><td>"
	htm[i++]="Procedimento";
	htm[i++]="</td>";
	htm[i++]="<td>";
	htm[i++]=procedureHTML;
	htm[i++]="</td></tr>";
	htm[i++]="</table>";
	
	workCell.innerHTML= htm.join("");
}


function elements_closeCALLPROCEDURES( node )
{
	readFromHTML_CALLPROCEDURES( node )
	return true;

}
function readFromHTML_CALLPROCEDURES( node )
{
  var sid = node.getAttribute("sid");
  var procedure = getValue( "procedure_"+sid );
  if ( procedure ) node.selectSingleNode("procedure").setAttribute("name",procedure);
	else node.selectSingleNode("procedure").setAttribute("name","");
	document.getElementById("cont"+sid).innerHTML = elements_getHtml( node );
	return true;
	
}
/************************  END CALLPROCEDURE************************/