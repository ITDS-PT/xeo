/*Enconding=UTF-8*/
package netgest.utils;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 */
public final class StringUtils 
{

    /**
     * Retorna se uma String está a null ou vazia ou só com espaços
     * @return 
     * @param string
     */
    public static final boolean isEmpty( String string )
    {
        return string == null || string.length() == 0 || string.trim().length() == 0;
    }
    
    public static String upper(Object v1)
    {
        if(v1 != null)
        {
            return v1.toString().toUpperCase();
        }
        return null;
    }

    public static boolean isNull(Object v1)
    {
        return v1 == null;
    }
    
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
        ArrayList args = new ArrayList();
        StringBuffer word = new StringBuffer();
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
    public static final boolean isLiteral( String arg )
    {
        return (
                    arg.trim().startsWith("\"") && arg.trim().startsWith("\"")
                    ||
                    arg.trim().startsWith("'") && arg.trim().startsWith("'")
               );
    }
    
    public static final String removeQuotes( String arg )
    {
        if( isLiteral( arg ) )
        {
            arg = arg.trim();
            return arg.substring(1,arg.length()-1);
        }
        else
        {
            throw new IllegalArgumentException("Cannot remove quotes from a not literal expression ["+arg+"]");
        }
    }
     
/******************* FROM tools.java *************************/

  public static int countStr(String strstr,String toCount) {
    int vv=0;
    if(strstr!= null) {
        int fL=toCount.length();
        int iIdx=strstr.indexOf(toCount);
        while (iIdx!=-1)
        {
          vv++;
          strstr=strstr.substring((iIdx+fL),strstr.length());
          iIdx=strstr.indexOf(toCount);
        }
    }
    return vv;
  }
 public static String replacestr(String strstr,String toReplace,String replaceStr) {
    String straux=strstr;
    String strRemain=strstr;
    int fL=strstr.length();

    int iIdx=strstr.indexOf(toReplace);
    if (iIdx!=-1)
    {
        straux=strstr.substring(0,iIdx)+replaceStr;
        strRemain=strstr.substring(iIdx+toReplace.length(),fL);
        if (strRemain.indexOf(toReplace)!=-1) straux+=replacestr(strRemain,toReplace,replaceStr);
        else straux+=strRemain;
    }
    return straux;
 }

  public static String padl(String xstring,int nrchars,String padstr) {
    while (xstring.length() < nrchars ) {
        xstring = padstr+xstring;
    }
    if (xstring.length() > nrchars) xstring = xstring.substring(0,nrchars);
    return xstring;
  }

  public static String padr(String xstring,int nrchars,String padstr) {
    while (xstring.length() < nrchars ) {
        xstring += padstr;
    }
    if (xstring.length() > nrchars) xstring = xstring.substring(0,nrchars);
    return xstring;
  }

  public static String putSlash(String straux)
  {
    if (straux.lastIndexOf("/")==straux.length()-1)return straux;
    else return straux+="/";
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
  
     public static final String smsReplaceChars( String in )
    {
        char aux;
        StringBuffer sb = new StringBuffer();
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
                    //ignorar este caracter poupando um espaço
                   break;

                default:
                    sb.append(aux);
            }
        }
        return sb.toString();
    }
    
    public static void main(String[] args) throws Exception
    {
//        System.out.println(Arrays.binarySearch(CHAR_TO_RPLC, 'ó'));
//        System.out.println(StringUtils.smsReplaceChars("Agradecemos contacto telefó"));
        System.out.println(StringUtils.smsReplaceChars("Agradecemos contacto telefó"));
    }
}