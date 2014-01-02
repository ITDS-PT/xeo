/*Enconding=UTF-8*/
// Copyright (c) 2001 enlace3
package netgest.utils;


/**
 * A Class class.
 * <P>
 * @author tony
 */

import java.util.*;
import java.math.BigDecimal;
import java.io.*;

public class tools extends Object {

  /**
   * Constructor
   */
  public tools() {
  }

/*  public static String Join(Vector vet,String separator) {
      String ret = "";
      String sep = separator==null?"":separator;
      for(int i=0;vet != null && i<vet.size();i++) {
          ret += ((String)vet.get(i))+sep;
      }
      return ret;
  }
*/
  public static Vector Split(String strstr,String toFind) {
    Vector SplitAux=new Vector();
    if(strstr!= null) {
        int fL=toFind.length();
        int iIdx=strstr.indexOf(toFind);

        while (iIdx!=-1)
        {
          SplitAux.add(strstr.substring(0,iIdx));
          strstr=strstr.substring((iIdx+fL),strstr.length());
          iIdx=strstr.indexOf(toFind);
        }

        SplitAux.add(strstr);
    }
    return SplitAux;
  }

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

  /*
  public static String[] VectorToString(Vector xvector) {
    return (String[])xvector.toArray(new String[0]);
  }
  */
  public static String replacestr(String strstr,String toReplace,String replaceStr) {
//    String straux=strstr;
//    String strRemain=strstr;
//    int fL=strstr.length();
//
//    int iIdx=strstr.indexOf(toReplace);
//    if (iIdx!=-1)
//    {
//        straux=strstr.substring(0,iIdx)+replaceStr;
//        strRemain=strstr.substring(iIdx+toReplace.length(),fL);
//        if (strRemain.indexOf(toReplace)!=-1) straux+=replacestr(strRemain,toReplace,replaceStr);
//        else straux+=strRemain;
//    }
//    return straux;
	  
	  return StringUtils.replacestr(strstr, toReplace, replaceStr);
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
/*
  public static String parseMacro(String toParse)
  {
    if (toParse==null) toParse="";
    if (toParse.indexOf("#")!=-1)
    {
      String auxMacro=toParse.substring(1,toParse.length()-1);
      Calendar xcand=Calendar.getInstance();
      int daymonth=xcand.get(Calendar.DAY_OF_MONTH);
      int dayweek=xcand.get(Calendar.DAY_OF_WEEK);
      int month=xcand.get(Calendar.MONTH)+1;
      int year=xcand.get(Calendar.YEAR);
      xcand.setFirstDayOfWeek(Calendar.MONDAY);
      int maxmonth=xcand.getActualMaximum(Calendar.DAY_OF_MONTH);      
      if (auxMacro.equalsIgnoreCase("iniciomes"))toParse="01-"+month+"-"+year; 
      if (auxMacro.equalsIgnoreCase("fimmes"))toParse=maxmonth+"-"+month+"-"+year; 
      if (auxMacro.equalsIgnoreCase("inicioano"))toParse="01-01-"+year;
      if (auxMacro.equalsIgnoreCase("fimano"))toParse="31-12-"+year;
      if (auxMacro.equalsIgnoreCase("iniciosemana"))
      {
        xcand.set(year,month-1,daymonth-dayweek+1);
        toParse=xcand.get(Calendar.DAY_OF_MONTH)+"-"+(xcand.get(Calendar.MONTH)+1)+"-"+xcand.get(Calendar.YEAR);             
      }
      if (auxMacro.equalsIgnoreCase("fimsemana"))      
      {
        xcand.set(year,month-1,daymonth+(7-dayweek));
        toParse=xcand.get(Calendar.DAY_OF_MONTH)+"-"+(xcand.get(Calendar.MONTH)+1)+"-"+xcand.get(Calendar.YEAR);        
      }
      if (auxMacro.equalsIgnoreCase("agora"))toParse=xcand.get(Calendar.DAY_OF_MONTH)+"-"+(xcand.get(Calendar.MONTH)+1)+"-"+xcand.get(Calendar.YEAR);        
    }
    return toParse;
  }
*/
/*
  public static boolean parseUpdateResponse(String xml)
  {
    xml=xml.toLowerCase();
    if (xml.indexOf("status=\"ok\"")==-1) return false;
    else return true;
  }
  public static String AllTrim(String string) {
      while(string.startsWith(" ")) string = string.substring(1);
      while(string.endsWith(" ")) string = string.substring(0,string.length()-1);
      return string;
  }

  public static final BigDecimal Nvl(BigDecimal num) {
      return Nvl(num,0);
  }
 
  public static final BigDecimal Nvl(BigDecimal num,float defaultnumber) {
      if(num == null) return new BigDecimal(defaultnumber);
      return num;
  }
  public static final String encodeXMLAttribute(String string) {
      if(string==null) return null;
      if(string.indexOf("&")>-1) {
          string = tools.replacestr(string,"&","&amp;");
      }
      if(string.indexOf("\"")>-1) {
          string = tools.replacestr(string,"\"","&quot;");
      }
      if(string.indexOf("<")>-1) {
          string = tools.replacestr(string,"<","&lt;");
      }
      if(string.indexOf("'")>-1) {
          string = tools.replacestr(string,"'","&#39;");
      }
      return string;
  }
  public static String getStackTraceString(Throwable e) {
      CharArrayWriter cr = new CharArrayWriter();
      PrintWriter pr = new PrintWriter(cr);
      e.printStackTrace(pr);
      pr.close();
      cr.close();
      return cr.toString();
  }
*/
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
  
}

