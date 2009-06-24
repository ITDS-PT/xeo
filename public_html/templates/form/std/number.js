//<SCRIPT>
function FormatInteger(vValue, iMinValue, iMaxValue, bGrouping, sFormat)
{
	try
	{
		vValue = parseInt(RemoveFormatting(vValue, sFormat), 10);
		
		if (isNaN(vValue) || vValue < iMinValue || vValue > iMaxValue)
		{
			//throw  "O valor tem que estar entre "  + AddFormatting(iMinValue, false, 0, true, sFormat) + " e " + AddFormatting(iMaxValue, false, 0, true, sFormat) + ".";
			throw  "O valor tem que estar entre "  + iMinValue + " e " + iMaxValue + ".";
		}
		
		return AddFormatting(vValue, false, 0, bGrouping, sFormat);
	}
	catch(e)
	{
		alert(e);

		return false;
	}
}

function FormatFloat(vValue, iMinValue, iMaxValue, bGrouping, sFormat, iAccuracy, minDec)
{
	try
	{		
		var sNewNum = RemoveFormatting(vValue, sFormat);
		
		sNewNum = MakeValidFloat(sNewNum, iAccuracy, minDec);
		
		var iVal = parseFloat(sNewNum);
		
		sNewNum = AddFormatting(sNewNum, true, iAccuracy, bGrouping, sFormat);
		
		if ( (iVal < iMinValue) || (iVal > iMaxValue) )
		{
			//iMinValue = MakeValidFloat( RemoveFormatting( iMinValue, sFormat ), minDec);
			//iMaxValue = MakeValidFloat( RemoveFormatting( iMaxValue, sFormat ), minDec);
			//throw "O valor tem que estar entre " + AddFormatting(iMinValue, true, iAccuracy, true, sFormat) + " e " + AddFormatting(iMaxValue, true, iAccuracy, true, sFormat) + ".";
			throw "O valor tem que estar entre " + iMinValue + " e " + iMaxValue + ".";
		}
	
		return sNewNum;
	}
	catch(e)
	{
		alert(e);

		return false;
	}
}


function AddFormatting(vValue, bIsFloat, iAccuracy, bGrouping, sFormat)
{
	var i;
	var iLen;
	var sFormatted = "";
	var sValue = vValue.toString();
	var sSign = ("-" == sValue.charAt(1)) ? "-": "";

	// Remove leading zero's
	//

	while (sValue.charAt(0) == "0" && sValue.length > 1)
	{
		sValue = sValue.substring(1, sValue.length);
	}

	// Guarantee leading zero for decimal values and "accurated" 0
	//
	if (isNaN(sValue.charAt(0)))
	{
		if(	sValue.charAt(0) != "-" )
		{
			sValue = "0" + sValue;
		}
	}

	// Remove trailing dot on floats with 0 Accuracy
	//
	if (isNaN(sValue.charAt(sValue.length-1)))
	{
		sValue = sValue.substr(0,sValue.length-1);
	}

	// add back regional formatting
	//
	//fcamara: alterado de modo a colocar 
	//a formatação decimal sem ser necessario
	//agrupar
//	if (bGrouping)
//	{
		var sGrpSym	= (sFormat == "us") ? "," : ".";
		var	sDecSym	= (sFormat == "us") ? "." : ",";
		

		if (sSign.length)
		{
			sValue = sValue.slice(sSign.length);
		}
		
		if ( !bIsFloat || (-1 == (iLen = sValue.indexOf(".")))  )
		{
			iLen = sValue.length;
		}

        
		for (i=0; i < iLen; i++)
		{
			sFormatted = sValue.charAt(iLen - 1 - i) + sFormatted;
		
            if (bGrouping)
	        {
    			if ((i + 1) % 3 == 0 && (iLen - 1 - i) != 0 && sValue.charAt(iLen - 2 - i) != "-")
    			{
    				sFormatted = sGrpSym + sFormatted;
    			}
		    }
		}
		// tack back on any precision
		//
		if (bIsFloat && iAccuracy > 0)
		{
		    if(sValue.slice(iLen + 1) != null && sValue.slice(iLen + 1) != "")
		    {
			    sFormatted += ( sDecSym + sValue.slice(iLen + 1) );
			}
		}
		
		// tack back on any '-' sSign
		//
		sValue = sSign + sFormatted;
//	}
	
	return sValue;
}


function RemoveFormatting(vValue, sFormat)
{
	vValue = new String(vValue);
	
	if (sFormat == "us")
	{
		vValue = vValue.replace(/\,/g, "");
	}
	else
	{
		vValue = vValue.replace(/\./g, "");
		vValue = vValue.replace(/\,/g, ".");
	}

	if (!IsValid(vValue))
	{
		throw  "Número inválido" ;
	}

	return vValue;
}


function IsValid(sValue)
{
	var sPattern = new RegExp(/^-?(\d|\.|\,){0,}$/);	

	var validPattern = sValue.match(sPattern);

	return (null != validPattern);
}


function MakeValidFloat(sValue, iAccuracy, minDec)
{
	var nResults = sValue.split(".");
	if(nResults.length == 1 && minDec == 0)
	{
	    return nResults[0];
	}
	var sNewNum = nResults[0] + ".";
	var decNum;
	if(nResults.length == 2)
	{
	    decNum = nResults[1];    
	}
    else
    {
        decNum = "";
    }
    
    if(decNum.length >= iAccuracy)
    {
        var sPattern = "-?\\d*\\.\\d{" + iAccuracy + "}";
        sNewNum += decNum;
        nResults = sNewNum.match(sPattern);	
	    return nResults[0];
    }
    
    for (i = 0; i < iAccuracy; i++)
	{
    	if(decNum.length >= minDec)
    	{
    	    return sNewNum + decNum;
    	}
        else
        {
            decNum += "0";
        }
    }
	
	
	
	return sNewNum + decNum;
}