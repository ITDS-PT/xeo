//<SCRIPT>
//
// Library for handling user<-->system time format conversions.
//
//
// See the UTC time format definition at http://www.w3.org/TR/NOTE-datetime
//

// time formats
//
// 0 - h:mm tt
// 1 - hh:mm tt
// 2 - H:mm
// 3 - HH:mm
// 4 - h:mm:ss tt
// 5 - hh:mm:ss tt
// 6 - H:mm:ss
// 7 - HH:mm:ss

// Localizable part of time
var sAM = "AM";
var sPM = "PM";

// Converts a string in one of the supported time formats to a date object.
// sTime - the time in string format
// iFormat - the time format the time is in.
// Returns - a Date() object with the given time.  The date portion should be ignored.

function parseTime( sTime, iFormat )
{
	// convert time string to lowercase so we can test for am/pm accurately.
	var sTime = sTime.toLowerCase( );
	var rValidTimeElement = /^[0-9]{1,2}$/
	
	var iHour;
	var iMinute;
	var iSecond;
   
	var iHourDiv = sTime.indexOf( ":" );
	var sHour = sTime.substring( 0, iHourDiv );

	if( ! sHour.match( rValidTimeElement ) )
	{
		// Invalid time!
		return new Date( NaN );
	}
	
	var iEndOfDigits;
	if( iFormat == 4 ||
	    iFormat == 5 ||
	    iFormat == 6 ||
	    iFormat == 7 )
	{
		// Seconds
		
		var iMinDiv = sTime.indexOf( ":", iHourDiv+1 );
		var sMinute = sTime.substring( iHourDiv+1, iMinDiv );
		
		if( ! sMinute.match( rValidTimeElement ) )
		{
			// Invalid time!
			return new Date( NaN );
		}
		
		iMinute = parseInt( sMinute, 10 );

		var sSecond;
		if( iFormat == 4 ||
			iFormat == 5 )
		{
			// AM/PM is on the end, make sure to trim that off
			var iSecDiv = sTime.indexOf( sAM.toLowerCase( ), iMinDiv+1 );
			if( iSecDiv == -1 )
			{
				iSecDiv = sTime.indexOf( sPM.toLowerCase( ), iMinDiv+1 );
			}
			
			sSecond = sTime.substring( iMinDiv+1, iSecDiv );
		}
		else
		{
			// no AM/PM, just take the rest of the string.
			sSecond = sTime.substring( iMinDiv+1, sTime.length );
		}
		
		// Remove any blanks
		sSecond = sSecond.replace( / /, "" );
		if( ! sSecond.match( rValidTimeElement ) )
		{
			// Invalid time!
			return new Date( NaN );
		}
		
		iSecond = parseInt( sSecond, 10 );
		iEndOfDigits = iSecDiv + sSecond.length + 1;
	}
	else
	{
		// No seconds;
		var sMinute;
		if( iFormat == 2 ||
			iFormat == 3 )
		{
			// Minutes terminated by end of string
			sMinute = sTime.substring( iHourDiv+1, sTime.length );
		}
		else
		{
			// Minutes terminated by AM/PM
			var iMinDiv = sTime.indexOf( sAM.toLowerCase( ), iHourDiv+1 );
			if( iMinDiv == -1 )
			{
				iMinDiv = sTime.indexOf( sPM.toLowerCase( ), iHourDiv+1 );
			}
			sMinute = sTime.substring( iHourDiv+1, iMinDiv );
		}
		
		// Remove any blanks
		sMinute = sMinute.replace( / /, "" );
		if( ! sMinute.match( rValidTimeElement ) )
		{
			// Invalid time!
			return new Date( NaN );
		}

		iMinute = parseInt( sMinute, 10 );

		iSecond = 0;
		iEndOfDigits = iMinDiv + sMinute.length + 1;
	}
	
	if( iFormat == 0 ||
	    iFormat == 1 ||
	    iFormat == 4 ||
	    iFormat == 5 )
	{
		// 12 hour time with AM/PM at the end.
		iHour = parseInt( sHour, 10 );

		// Check specific to 12 hour times; we want to further constrain
		// the time from 0..23 to 1..12 hours for these formats.		
		if( iHour <= 0 || iHour > 12 )
		{
			return new Date( NaN );
		}
		
		// 12:30 AM --> 0:30 UTC
		if( iHour == 12 )
		{
			iHour = 0;
		}
		
		// Pull off the last two chars
		sAmPm = sTime.substring( sTime.length - 2, sTime.length );
		
		if( sAmPm.toLowerCase( ) == sPM.toLowerCase( ) )
		{	
			// Here we have an hour between 0 and 11; if there was a 'PM' tag, move the
			// clock forward 12 hours to be 12-23 hours.
			iHour += 12;
		}
		else if( sAmPm.toLowerCase( ) == sAM.toLowerCase( ) )
		{
			// Do Nothing.
		}
		else
		{
			// Invalid string!
			return new Date( NaN );
		}
	}
	else
	{
		// 24 hour time 
		iHour = parseInt( sHour, 10 );
	}
	
	if( iHour > 23 || iHour < 0 || 
	    iMinute > 59 || iMinute < 0 ||
	    iSecond > 59 || iSecond < 0 )
	{
		// The user has entered an invalid time.
		return new Date( NaN );
	}
	
	// Reparse the string to take advantage of the Date objects built in error detection features
	// If we don't do the above work, though, we cannot gaurentee that the physical format is correct;
	// the date object will accept a time like "12:022:24", which is not actually valid.
	return new Date( 2000, 0, 1, iHour, iMinute, iSecond, 0 );
}

// Converts the time portion of a UTC time to a date object.  This function
// ignores the offset if it is present.
// sTime - the time in UTC format.  This is the piece of the string following the 'T'.
// Returns - a Date() object with the given time.
function parseUTCTime( sTime )
{
	
	// If there is a UTC offset, ignore it.
	// This may be 00:00:00Z or 00:00:00-1:00
	var ss = sTime.split( '-' );
	var s = ss[ 0 ];
	
	// Split again on '+' to cover positive UTC offsets.
	ss = s.split( '+' );
	s = ss[ 0 ];
	
	// split again on '.' to remove any miliseconds which may be present.
	ss = s.split( '.' );
	var s = ss[ 0 ];
		
	// Remove the 'Z' if present.
	s = s.replace( /Z/, "" );
	
	var oDate = new Date( "1/1/00 " + s );
	
	// If there were miliseconds, we should add them back in at this point.
	if( ss.length == 2 )
	{
		sMilliseconds = ss[ 1 ];
		oDate.setMilliseconds( parseInt( sMilliseconds ) );
	}
	
	return oDate;
}

// Converts a Date() object to a time string using the given format.
// oTime - a Date() object.  The date portion is ignored.
// iFormat - the date format to use.
// Returns - the date as a string in the requested format.
function timeToString( oTime, iFormat )
{
	switch( iFormat )
	{
		case 0:
		{
			return get12HourClockHours( oTime ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + " " + makeAMPM( oTime );
		}
		
		case 1:
		{
			return makeTwoDigitString( get12HourClockHours( oTime ) ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + " " + makeAMPM( oTime );
		}
		
		case 2:
		{
			return oTime.getHours() + ":" + makeTwoDigitString( oTime.getMinutes() );
		}
		
		case 3:
		{
			return makeTwoDigitString( oTime.getHours() ) + ":" + makeTwoDigitString( oTime.getMinutes() );
		}
		
		case 4:
		{
			return get12HourClockHours( oTime ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + ":" + makeTwoDigitString( oTime.getSeconds() ) + " " + makeAMPM( oTime );
		}
		
		case 5:
		{
			return makeTwoDigitString( get12HourClockHours( oTime ) ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + ":" + makeTwoDigitString( oTime.getSeconds( ) ) + " " + makeAMPM( oTime );
		}
		
		case 6:
		{
			return oTime.getHours( ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + ":" + makeTwoDigitString( oTime.getSeconds( ) );
		}
		
		case 7:
		{
			return makeTwoDigitString( oTime.getHours( ) ) + ":" + makeTwoDigitString( oTime.getMinutes() ) + ":" + makeTwoDigitString( oTime.getSeconds( ) );
		}
	}
}

// Converts a Date() object to a time string using the UTC format.
// oTime - a Date() object.  The date portion is ignored.
// Returns - a string or null.
function timeToUTCString( oTime )
{
	if (isNaN(oTime))
	{
		return null;
	}
	
	var sRVal = makeTwoDigitString( oTime.getHours() ) + ":" 
	          + makeTwoDigitString( oTime.getMinutes() ) 
	          + ":" + makeTwoDigitString( oTime.getSeconds() );
	          
	// If there are milliseconds, append them.  Otherwise don't
	// Commented out since the platform doesn't support milliseconds
	/*if( oTime.getMilliseconds( ) > 0 )
	{
		sRVal += "." + oTime.getMilliseconds( );  // Milliseconds can be 0..n digits
	}*/
	
	return sRVal;
}

// Takes a number that is 1 or 2 digits and outputs a string that is 2 digits long.
// iNumber = a number between 0 and 99.  Other numbers, the results are undefined.
// Returns - a two-digit string.
function makeTwoDigitString( iNumber )
{
	if( iNumber > 9 )
	{
		return iNumber.toString( );
	}
	else
	{
		return "0" + iNumber.toString( );
	}
}

// Returns hours 1..12 instead of 0..23
// oTime - time to get hours from
// Returns an hour string 1..12
function get12HourClockHours( oTime )
{
	if( oTime.getHours() > 12 )
	{
		return oTime.getHours() - 12;
	}
	else if( oTime.getHours() == 0 )
	{
		return 12;
	}
	else 
	{
		return oTime.getHours();
	}
}

// Returns 'AM' or 'PM' depending on the time
// oTime - the time to evaluate for AM/PM
// returns - 'AM' or 'PM'
function makeAMPM( oTime )
{
	if( oTime.getHours() >= 12 )
	{
		return sPM;
	}
	else
	{
		return sAM;
	}
}


//	format float, kill > a 10th
function formatFloat(sNum) 
{
	var sBase = "";
	var sRem = "";
	var bRem = false;;
				
	for(i = 0; i < sNum.length; i++) 
	{
				
		curChar = sNum.charAt(i);

		if(bRem) 
		{
			sRem += curChar;
			if(sRem.length == 2) break;
		} 
		else 
		{
			sBase += curChar;
		}

		if(curChar == '.') 
		{ 
			bRem = true;
			continue;
		}
	}

	return sBase + sRem;
}

// Converts input value to a user-friendly time string and returns a string with the updated duration.
// If this logic changes, the logic in duration.htc must also change this was copied from there 
function formatDuration( iMinutes ) 
{
	if( isNaN( parseInt(iMinutes, 10) ) || ( iMinutes < 0 ) ) 
	{
		iMinutes = 0;
	}

	var rVal;
	if(iMinutes < 60) 
	{
		if(iMinutes == 1) 
		{
			rVal = ""+iMinutes +" minute";
		} 
		else 
		{
			rVal = ""+iMinutes+" minutes";
		}
	} else if( iMinutes >= 60 && iMinutes < 1440 ) 
	{
		var iHours = iMinutes / 60;
			
		if(iHours == 1) 
		{
			rVal = ""+formatFloat( iHours.toString() )+" hour";
		} 
		else 
		{
			rVal = ""+formatFloat( iHours.toString() )+" hours";
		}
	} else if( iMinutes >= 1440 ) 
	{
		var iHours = iMinutes / 60;
		var iDays = iHours / 24;
			
		if(iDays == 1) 
		{
			rVal = ""+formatFloat( iDays.toString() )+" day";
		} 
		else 
		{
			rVal = ""+formatFloat( iDays.toString() )+" days";
		}
	}
	
		return rVal;
}
