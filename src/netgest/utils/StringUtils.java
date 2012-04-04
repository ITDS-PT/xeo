/*Enconding=UTF-8*/
package netgest.utils;

import java.util.ArrayList;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 */
public final class StringUtils 
{

    /**
     * Checks whether the string is empty or null
     * 
     * @return True if the string is empty or null and false otherwise
     * @param string The string to check
     */
	public static final boolean isEmpty( String string )
    {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }
	
	/**
	 * 
	 * Checks if the string has a value (is not null, neither is an empty string)
	 * 
	 * @param toCheck The string to check
	 * 
	 * @return True if the string is not null and has at least one character
	 */
	public static final boolean hasValue( String toCheck ){
		return !isEmpty(toCheck);
	}
    
    /**
     * 
     * Return an upper case String of the original
     * 
     * @return An upper case string or null for the null argument
     * 
     */
    public static String upper(Object original)
    {
        if(original != null)
        {
            return original.toString().toUpperCase();
        }
        return null;
    }

    public static boolean isNull(Object v1)
    {
        return v1 == null;
    }
    
    /**
     * 
     * Return an lower case String of the original
     * 
     * @return A lower case string or null for the null argument
     * 
     */
    public static String lower(Object v1)
    {
        if(v1 != null)
        {
            return v1.toString().toLowerCase();
        }
        return null;
    }
    
    public static final String[] parseArguments( String type )
    {
        ArrayList<String> args = new ArrayList<String>();
        StringBuilder word = new StringBuilder();
        byte deep=0;
        for (int i = 0; i < type.length(); i++) 
        {
            switch  ( type.charAt(i) )
            {
                case '(':
                case '[':
                case '{':
                    deep++;
                    break;
                case ')':
                case ']':
                case '}':
                    deep--;
                    break;
                case ',':
                    if( deep == 1 )
                    {
                        args.add( word.toString() );
                        word.delete(0,word.length());
                    }
                    break;
                default:    
                    if( deep > 0 )
                    {
                        word.append( type.charAt( i ) );
                    }
            }
        }
        if( word.length() > 0 )
        {
            args.add( word.toString() );
        }
        return (String[])args.toArray( new String[ args.size() ] );
    }
    /**
     * 
     * Checks whether the parameter is a string starts with
     * a quote (single or double) 
     * 
     * @return True if the string starts with quote and false otherwise
     */
    public static final boolean isLiteral( String stringToCheck )
    {
        return (
                    stringToCheck.trim().startsWith("\"") && stringToCheck.trim().startsWith("\"")
                    ||
                    stringToCheck.trim().startsWith("'") && stringToCheck.trim().startsWith("'")
               );
    }
    
    /**
     * Return a string without quotes
     * 
     * @param stringToClean The string to remove the quotes
     * 
     * @return A clean string
     */
    public static final String removeQuotes( String stringToClean )
    {
        if( isLiteral( stringToClean ) )
        {
            stringToClean = stringToClean.trim();
            return stringToClean.substring(1,stringToClean.length()-1);
        }
        else
        {
            throw new IllegalArgumentException(MessageLocalizer.getMessage("CANNOT_REMOVE_QUOTES_FROM_A_NOT_LITERAL_EXPRESSION")+" ["+stringToClean+"]");
        }
    }
     
    /******************* FROM tools.java *************************/

 /**
  * Count the number of times a string occurs inside another string
 */
public static int countStr(String originalToCheck,String toCount) {
    int count = 0;
    if( originalToCheck != null ) {
        int countWordSize = toCount.length();
        int indexOfWord = originalToCheck.indexOf( toCount );
        while ( indexOfWord != -1 )
        {
          count++;
          originalToCheck = originalToCheck.substring( ( indexOfWord+countWordSize), originalToCheck.length() );
          indexOfWord = originalToCheck.indexOf( toCount );
        }
    }
    return count;
  }
  
  
 public static String replacestr(String target, String from, String to) {   
	  //   target is the original string
	  //   from   is the string to be replaced
	  //   to     is the string which will used to replace
	  //  returns a new String!
	  int start = target.indexOf(from);
	  if (start == -1) return target;
	  int lf = from.length();
	  char [] targetChars = target.toCharArray();
	  StringBuffer buffer = new StringBuffer();
	  int copyFrom = 0;
	  while (start != -1) {
	    buffer.append (targetChars, copyFrom, start - copyFrom);
	    buffer.append (to);
	    copyFrom = start + lf;
	    start = target.indexOf (from, copyFrom);
	  }
	  buffer.append (targetChars, copyFrom, targetChars.length - copyFrom);
	  return buffer.toString();
 } 

  /**
   * 
   * Adds left padding to a string (nchars * padString)
   * 
   * @param stringToAddPad The string to pad
   * @param nchars The size of the string after padding
   * @param padString The pad character/string to add
   * 
   * @return A string with the padding added
   */
 public static String padl(String stringToAddPad,int nchars,String padString) {
    while (stringToAddPad.length() < nchars ) {
        stringToAddPad = padString+stringToAddPad;
    }
    if (stringToAddPad.length() > nchars) 
    	stringToAddPad = stringToAddPad.substring(0,nchars);
    return stringToAddPad;
  }

 /**
  * 
  * Adds right padding to a string (nchars * padString) 
  * 
  * @param stringToAddPad The string to pad
  * @param nchars The size of the string after padding
  * @param padString The pad character to add
  * 
  * @return A string with the padding added
  */
  public static String padr(String stringToAddPad,int nchars,String padString) {
    while (stringToAddPad.length() < nchars ) {
        stringToAddPad += padString;
    }
    if (stringToAddPad.length() > nchars) 
    	stringToAddPad = stringToAddPad.substring(0,nchars);
    return stringToAddPad;
  }

 /**
  * 
  * Adds a slash to the end of the string if it does not have one
  * 
 * @param stringToAddSlash The string to add the slash
 * @return A string with the slash added
 */
public static String putSlash(String stringToAddSlash)
  {
    if ( stringToAddSlash.lastIndexOf("/") == stringToAddSlash.length()-1 )
    	return stringToAddSlash;
    else 
    	return stringToAddSlash+="/";
  }  

 public static String fromStringToHex(String myString)
  {
      String hex="";
      byte [] bytes=myString.getBytes();
      for (int i =0; i < bytes.length; i++)
      {
        byte b = bytes[i];
        hex += Integer.toHexString(0xFF&b);
      }
      return hex;
  }


  public static String fromHexToString ( String s ) 
  { 
    int stringLength = s.length() ; 
    byte[] b = new byte[ stringLength / 2 ]; 

    for ( int i=0 ,j= 0; i< stringLength; i+= 2,j ++ ) 
    { 
      int high= charToNibble(s.charAt ( i )); 
      int low = charToNibble( s.charAt ( i+1 ) ); 
      b[ j ] = (byte ) ( ( high << 4 ) | low ); 
    } 
    return new String(b); 
  } 


  private static int charToNibble ( char c ) 
  { 
    if ( '0' <= c && c <= '9' ) 
    { 
    return c - '0' ; 
    } 
    else if ( 'a' <= c && c <= 'f' ) 
    { 
    return c - 'a' + 0xa ; 
    } 
    else if ( 'A' <= c && c <= 'F' ) 
    { 
    return c - 'A' + 0xa ; 
    }
    else return -1;
  }
  
     /**
      * 
      * Replace characters to place in a SMS message (replacing characters such 
      * as 'á' and 'à' with 'a'
      * 
     * @param in 
     * 
     * @return A string with the caracters replaced
     */
    public static final String smsReplaceChars( String in )
    {
        char aux;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < in.length(); i++) 
        {
            aux = in.charAt(i);
            switch(aux)
            {
                case 'Á':
                case 'À':
                case 'Ã':
                case 'Â':
                case 'Ä':
                   sb.append('A');
                   break;
                
                case 'á':
                case 'à':
                case 'â':
                case 'ã':
                case 'ä':
                   sb.append('a');
                   break;
                
                case 'É':
                case 'È':
                case 'Ê':
                case 'Ë':
                   sb.append('E');
                   break;
                
                case 'é':
                case 'è':
                case 'ê':
                case 'ë':
                   sb.append('e');
                   break;
                
                case 'Í':
                case 'Ì':
                case 'Î':
                case 'Ï':
                   sb.append('I');
                   break;
                
                case 'í':
                case 'ì':
                case 'î':
                case 'ï':
                   sb.append('i');
                   break;
                
                case 'Ó':
                case 'O':
                case 'Ô':
                case 'Õ':
                case 'Ö':
                   sb.append('O');
                   break;
                
                case 'ó':
                case 'ò':
                case 'ô':
                case 'õ':
                case 'ö':
                   sb.append('o');
                   break;
                
                case 'Ú':
                case 'Ù':
                case 'Û':
                case 'Ü':              
                   sb.append('U');
                   break;
                
                case 'ú':
                case 'ù':
                case 'û':
                case 'ü':
                   sb.append('u');
                   break;
                
                case 'Ç':
                   sb.append('C');
                   break;
                
                case 'ç':
                   sb.append('c');
                   break;
                
                case '€':
                   sb.append("Euros");
                   break;
                
                case '´':
                case '`':
                case '~':
                case '^':
                case '¨':
                    //Ignore these characters, saving one space
                   break;

                default:
                    sb.append(aux);
            }
        }
        return sb.toString();
    }
    
    
}