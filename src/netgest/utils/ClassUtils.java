/*Enconding=UTF-8*/
package netgest.utils;

import java.math.*;
import java.math.BigDecimal;

import java.sql.Timestamp;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.text.html.*;
import javax.swing.text.html.HTMLDocument;
import netgest.bo.runtime.boRuntimeException;
import java.io.*;
import javax.swing.text.*;
import javax.swing.text.rtf.*;

public abstract class ClassUtils
{
    public static final byte YES = 1;
    public static final byte NO = 0;
    public static final int radix = 10;

    public static final Class parseArgument(String argtype)
        throws boRuntimeException
    {
        if (argtype.endsWith("String"))
        {
            return String.class;
        }
        else if (argtype.endsWith("Integer"))
        {
            return Integer.class;
        }
        else if (argtype.endsWith("Long"))
        {
            return Long.class;
        }
        else if (argtype.endsWith("Float"))
        {
            return Float.class;
        }
        else if (argtype.endsWith("Double"))
        {
            return Double.class;
        }
        else if (argtype.endsWith("int"))
        {
            return Integer.TYPE;
        }
        else if (argtype.endsWith("long"))
        {
            return Long.TYPE;
        }
        else if (argtype.endsWith("float"))
        {
            return Float.TYPE;
        }
        else if (argtype.endsWith("double"))
        {
            return Double.TYPE;
        }
        else if (argtype.endsWith("boolean"))
        {
            return Boolean.TYPE;
        }
        else
        {
            try
            {
                return Class.forName(argtype);
            }
            catch (ClassNotFoundException e)
            {
                
            }
        }
        throw new boRuntimeException(ClassUtils.class.getName() + "parseArgument(String)", "BO-3011", null, argtype);
    }

    public static final String byteArrayToHexString( byte[] bytes )
    {
         StringBuffer toRet=new StringBuffer();
         BigInteger i = new BigInteger( bytes );
         return i.toString( 16 );
//         
//         for(int i=0;i<bytes.length;i++){
//           
//           
//            String x=Integer.toHexString(bytes[i] & 0xFF ); 
//            if(x.length()<2) 
//            {
//                toRet.append('0').append(x);
//            }
//            else toRet.append(x); 
//            
//         }
// 
//        return toRet.toString();
    }
    public static final String capitalize( String toCapitalize )
    {
        if(toCapitalize == null) return null;
        // An array of characters, initialized somewhere else
        char[] text = new char[toCapitalize.length()];
        // Our current position in the array of characters
        int p = 0;  
        // Capitalize the first word of text
        boolean lastWasBlank = true;
        while(p < text.length) 
        {
            if(Character.isLetterOrDigit(toCapitalize.charAt(p)))
            {
                if(lastWasBlank)
                {
                    text[p] = Character.toUpperCase(toCapitalize.charAt(p));
                }
                else
                {
                    text[p] = Character.toLowerCase(toCapitalize.charAt(p));
                }
                lastWasBlank = false;
            }
            else if(Character.isWhitespace(toCapitalize.charAt(p)))
            {
                lastWasBlank = true;
                text[p] = ' ';
            }
            else
            {
                text[p] = Character.toLowerCase(toCapitalize.charAt(p));
            }
            p++;
        }
        return String.valueOf(text);
    }
    public static final int convertToInt(String x, int defaultValue)
    {
        if (x == null)
        {
            return defaultValue;
        }

        try
        {
            return (Integer.valueOf(x).intValue());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static final int convertToInt(String x)
    {
        if (x == null)
        {
            return 0;
        }

        try
        {
            return (Integer.valueOf(x).intValue());
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static final byte convertToByte(String x, byte defaultValue)
    {
        if (x == null)
        {
            return defaultValue;
        }

        try
        {
            return (Byte.valueOf(x).byteValue());
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static final byte convertToByte(String x)
    {
        if (x == null)
        {
            return 0;
        }

        try
        {
            return (Byte.valueOf(x).byteValue());
        }
        catch (Exception e)
        {
            return 0;
        }
    }

    public static final Integer convertToInteger(String x, int defaultValue)
    {
        if (x == null)
        {
            return new Integer(defaultValue);
        }

        try
        {
            return new Integer(x);
        }
        catch (Exception e)
        {
            return new Integer(defaultValue);
        }
    }

    public static final Integer convertToInteger(String x)
    {
        if (x == null)
        {
            return new Integer(0);
        }

        try
        {
            return new Integer(x);
        }
        catch (Exception e)
        {
            return new Integer(0);
        }
    }
    
    public static final boolean isLong(String s)
    {
        if (s == null)
        {
            return false;
        }

        long result = 0;
        boolean negative = false;
        int i = 0;
        int max = s.length();
        long limit;
        long multmin;
        int digit;

        if (max > 0)
        {
            if (s.charAt(0) == '-')
            {
                negative = true;
                limit = Long.MIN_VALUE;
                i++;
            }
            else
            {
                limit = -Long.MAX_VALUE;
            }

            multmin = limit / radix;

            if (i < max)
            {
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return false;
                }
                else
                {
                    result = -digit;
                }
            }

            while (i < max)
            {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return false;
                }

                if (result < multmin)
                {
                    return false;
                }

                result *= radix;

                if (result < (limit + digit))
                {
                    return false;
                }

                result -= digit;
            }
        }
        else
        {
            return false;
        }

        if (negative)
        {
            if (i > 1)
            {
                return true;
            }
            else
            { /* Only got "-" */

                return false;
            }
        }
        else
        {
            return true;
        }
    }

    public static final long convertToLong(String x)
    {
        return ClassUtils.convertToLong(x, 0);
    }

    public static final long convertToLong(StringBuffer x)
    {
        return ClassUtils.convertToLong(x, 0);
    }

    public static final long convertToLong(StringBuffer s, long defaultValue)
    {
        if (s == null)
        {
            return defaultValue;
        }

        long result = 0;
        boolean negative = false;
        int i = 0;
        int max = s.length();
        long limit;
        long multmin;
        int digit;

        if (max > 0)
        {
            if (s.charAt(0) == '-')
            {
                negative = true;
                limit = Long.MIN_VALUE;
                i++;
            }
            else
            {
                limit = -Long.MAX_VALUE;
            }

            multmin = limit / radix;

            if (i < max)
            {
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return defaultValue;
                }
                else
                {
                    result = -digit;
                }
            }

            while (i < max)
            {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return defaultValue;
                }

                if (result < multmin)
                {
                    return defaultValue;
                }

                result *= radix;

                if (result < (limit + digit))
                {
                    return defaultValue;
                }

                result -= digit;
            }
        }
        else
        {
            return defaultValue;
        }

        if (negative)
        {
            if (i > 1)
            {
                return result;
            }
            else
            { /* Only got "-" */

                return defaultValue;
            }
        }
        else
        {
            return -result;
        }
    }

    public static final long convertToLong(String s, long defaultValue)
    {
        if (s == null)
        {
            return defaultValue;
        }

        long result = 0;
        boolean negative = false;
        int i = 0;
        int max = s.length();
        long limit;
        long multmin;
        int digit;

        if (max > 0)
        {
            if (s.charAt(0) == '-')
            {
                negative = true;
                limit = Long.MIN_VALUE;
                i++;
            }
            else
            {
                limit = -Long.MAX_VALUE;
            }

            multmin = limit / radix;

            if (i < max)
            {
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return defaultValue;
                }
                else
                {
                    result = -digit;
                }
            }

            while (i < max)
            {
                // Accumulating negatively avoids surprises near MAX_VALUE
                digit = Character.digit(s.charAt(i++), radix);

                if (digit < 0)
                {
                    return defaultValue;
                }

                if (result < multmin)
                {
                    return defaultValue;
                }

                result *= radix;

                if (result < (limit + digit))
                {
                    return defaultValue;
                }

                result -= digit;
            }
        }
        else
        {
            return defaultValue;
        }

        if (negative)
        {
            if (i > 1)
            {
                return result;
            }
            else
            { /* Only got "-" */

                return defaultValue;
            }
        }
        else
        {
            return -result;
        }
    }

    public static final boolean convertToBoolean(String x)
    {
        if (x == null)
        {
            return false;
        }

        try
        {
            return Boolean.valueOf(x).booleanValue();
        }
        catch (Exception e)
        {
            return false;
        }
    }

    public static final boolean convertToBoolean(String x, boolean defaultValue)
    {
        try
        {
            return Boolean.valueOf(x).booleanValue();
        }
        catch (Exception e)
        {
            return defaultValue;
        }
    }

    public static final java.util.Date convertToDate(String xfldval)
    {
        if ((xfldval != null) && (xfldval.trim().length() > 0))
        {
            java.util.Date flddate = null;
            SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS");
            ParsePosition pos = new ParsePosition(0);
            formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            flddate = formatter.parse(xfldval, pos);

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH");
                flddate = formatter.parse(xfldval, pos);
            }

            if (flddate == null)
            {
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                flddate = formatter.parse(xfldval, pos);
            }

            return flddate;
        }

        return null;
    }

    public static final String[] splitToArray(String input, String str)
    {
        int strLength = str.length();
        int tokenCount = 0;

        if ((input == null) || input.equals(""))
        {
            return new String[0];
        }

        int strIndex = -strLength;

        do
        {
            strIndex = input.indexOf(str, strIndex + strLength);
            tokenCount++;
        }
        while (strIndex >= 0);

        String[] tokens = new String[tokenCount];
        int tokenIndex = 0;
        strIndex = -strLength;

        do
        {
            int index = input.indexOf(str, strIndex + strLength);

            if (index < 0)
            {
                tokens[tokenIndex] = input.substring(strIndex + strLength);
            }
            else
            {
                tokens[tokenIndex] = input.substring(strIndex + strLength, index);
            }

            strIndex = index;
            tokenIndex++;
        }
        while (strIndex >= 0);

        return tokens;
    }

    public static final boolean cmpString(String left, String right)
    {
        if (left == null)
        {
            left = "";
        }

        if (right == null)
        {
            right = "";
        }

        return left.equals(right);
    }

    public static final boolean cmpJavaDate(java.util.Date left, java.util.Date right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else
        {
            ret = left.getTime() == right.getTime();
        }

        return ret;
    }

    public static final boolean cmpDouble(double left, double right)
    {
        return left == right;
    }

    public static final boolean cmpLong(long left, long right)
    {
        return left == right;
    }

    public static final boolean compare(Timestamp left, Timestamp right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else
        {
            ret = left.compareTo(right) == 0;
        }

        return ret;
    }

    public static final boolean compare(BigDecimal[] left, BigDecimal[] right)
    {
        boolean ret = true;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else if (left.length != right.length)
        {
            ret = false;
        }
        else
        {
            for (short i = 0; ret && (i < left.length); i++)
            {
                if ((left[i] == null) && (right[i] == null))
                {
                    ret = true;
                }
                else if ((left[i] == null) || (right[i] == null))
                {
                    ret = false;

                    break;
                }
                else if (left[i].compareTo(right[i]) != 0)
                {
                    ret = false;

                    break;
                }
            }
        }

        return ret;
    }

    public static final boolean compare(Timestamp[] left, Timestamp[] right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else if (left.length != right.length)
        {
            ret = false;
        }
        else
        {
            ret = false;

            for (short i = 0; i < left.length; i++)
            {
                if (left[i].compareTo(right[i]) != 0)
                {
                    ret = true;

                    break;
                }
            }
        }

        return ret;
    }

    public static final boolean compare(BigDecimal left, BigDecimal right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else
        {
            ret = left.compareTo(right) == 0;
        }

        return ret;
    }

    public static final boolean compare(String[] left, String[] right)
    {
        boolean ret = true;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else if (left.length != right.length)
        {
            ret = false;
        }
        else
        {
            for (short i = 0; ret && (i < left.length); i++)
            {
                ret = compare(left[i], right[i]);
            }
        }

        return ret;
    }

    public static final boolean compare(String left, String right)
    {
        boolean ret;

        if ((left == null) && (right == null))
        {
            ret = true;
        }
        else if ( right == null && ( left != null && left.length() == 0) )
        {
            ret = true;
        }
        else if ( left == null && ( right != null && right.length() == 0) )
        {
            ret = true;
        }
        else if ((left == null) || (right == null))
        {
            ret = false;
        }
        else
        {
            ret = left.equals(right);
        }

        return ret;
    }

    public static final int[] growIntArray(int[] Array, int inc)
    {
        int[] toRet = new int[Array.length + inc];
        System.arraycopy(Array, 0, toRet, 0, Array.length);

        return toRet;
    }

    public static final long[] growLongArray(long[] Array, int inc)
    {
        long[] toRet = new long[Array.length + inc];
        System.arraycopy(Array, 0, toRet, 0, Array.length);

        return toRet;
    }

    public static final long[] setsizeLongArray(long[] Array, int size)
    {
        long[] toRet = new long[size];
        System.arraycopy(Array, 0, toRet, 0, size);

        return toRet;
    }

    public static final String[] growStringArray(String[] Array, int inc)
    {
        String[] toRet = new String[Array.length + inc];
        System.arraycopy(Array, 0, toRet, 0, Array.length);

        return toRet;
    }
    
    /**
     * COmpara 2 objectos mesmo que eles sejam <code>null</code>
     * @param obj1
     * @param obj2
     * @see 
     */
     public static boolean isEqual(Object obj1, Object obj2)
    {
        if(obj1 == null && obj2 == null)
            return true;
        if(obj1 != null && obj1.equals(obj2))
            return true;
        if(obj2 != null && obj2.equals(obj1))
            return true;
        return false;
    }
    
   /**
    * Transforma o HTML em Texto
    * 
    * @param html para transformar em texto
    * @return result texto transformado
    */
    public static String htmlToText(String html)
    {
        return htmlToText(html, false);
    }
    public static String htmlToText(String html, boolean force)
    {
        String result="";        
        
        String patterns = "\n";
        Pattern p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
        Matcher m = p.matcher(html);
        result = m.replaceAll("");
        
        patterns = "<li>";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("\n- ");
        
        patterns = "<blockquote.*><p>";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("\n\t");
        
        if(force)
        {
            patterns = "<p.*?>|<h.>|<tr>|<br>|<ul>|</ul>";
        }
        else
        {
            patterns = "<p.*?>|<h.>|<table.*>|<tr>|<br>|<ul>|</ul>";
        }
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("\n");    
        
        patterns = "<td>|<blockquote.*>";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("\t");        

        patterns = "&nbsp;";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll(" ");  
        
        patterns = "<[^>]*>";
        p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
        m = p.matcher(result);
        result= m.replaceAll("");
        
        patterns = "&lt;";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("<");
        
        patterns = "&gt;";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll(">");              

        patterns = "&amp;";
        p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
        m = p.matcher(result);
        result= m.replaceAll("&");  
        
        if(force)
        {
            try{result = HTMLRemover.remove(result);}catch(Exception e){/*IGNORE*/}
        }
        
        return result;    
    }

	public static String RTF2Text(String text) throws Exception
	{
		StringBufferInputStream sbis = new StringBufferInputStream(text);
		DataInputStream din = new DataInputStream(sbis);

		//creating a default blank styled document
		DefaultStyledDocument styledDoc = new DefaultStyledDocument();

		//Creating a RTF Editor kit
		RTFEditorKit rtfKit = new RTFEditorKit();

		//Populating the contents in the blank styled document
		rtfKit.read(din,styledDoc,0);

		// Getting the root document
		Document doc = styledDoc.getDefaultRootElement().getDocument();

		//Printing out the contents of the RTF document as plain text
		return doc.getText(0,doc.getLength());
	}
    
    public static String HTML2Text(String text) throws Exception
	{
        StringBufferInputStream sbis = new StringBufferInputStream(text);
		DataInputStream din = new DataInputStream(sbis);

		//creating a default blank styled document
		DefaultStyledDocument styledDoc = new DefaultStyledDocument();

		//Creating a HTML Editor kit
		HTMLEditorKit htmlKit = new HTMLEditorKit();

		//Populating the contents in the blank styled document
		htmlKit.read(din,styledDoc,0);

		// Getting the root document
		Document doc = styledDoc.getDefaultRootElement().getDocument();

		//Printing out the contents of the HTML document as plain text
		return doc.getText(0,doc.getLength());
	}
    
    public static String HTML2Rtf(String strText) throws Exception
	{
        HTMLDocument tempHTMLDoc = new HTMLDocument();
        HTMLEditorKit htmlKit = new HTMLEditorKit();
        RTFEditorKit rtfKit = new RTFEditorKit();
        String strResult = "";
        StringReader reader = new StringReader(strText);
        try {
            tempHTMLDoc.remove(0,tempHTMLDoc.getLength());
            htmlKit.read(reader,tempHTMLDoc,0);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            rtfKit.write(byteArrayOutputStream,tempHTMLDoc,0,tempHTMLDoc.getLength());
            strResult = byteArrayOutputStream.toString();
        }
        catch(IOException ie)
        {}
        catch(BadLocationException ble){}
        return strResult;
	}
    public static String textToHtml(String text)
    {
        String result="";
        if(text != null && !"".equals(text))
        {
            
            String patterns = "&";
            Pattern p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
            Matcher m = p.matcher(text);
            result= m.replaceAll("&amp;");
            
            patterns = "<";
            p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
            m = p.matcher(result);
            result= m.replaceAll("&lt;");
    
            patterns = ">";
            p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
            m = p.matcher(result);
            result= m.replaceAll("&gt;");
            
            patterns = "\n";
            p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
            m = p.matcher(result);
            result = m.replaceAll("<br>");
            
        }
        return result;
    }
     public static String html2TextLov(String text)
    {
        try
        {
            String result="";
            if(text != null && !"".equals(text))
            {
                
                String patterns = "&";
                Pattern p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
                Matcher m = p.matcher(text);
                result= m.replaceAll("&amp;");
                
                patterns = "<";
                p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
                m = p.matcher(result);
                result= m.replaceAll("&lt;");
        
                patterns = ">";
                p = Pattern.compile(patterns, Pattern.CASE_INSENSITIVE);
                m = p.matcher(result);
                result= m.replaceAll("&gt;");
                
                patterns = "\n";
                p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
                m = p.matcher(result);
                result = m.replaceAll("&nbsp;");
                
                patterns = "\"";
                p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
                m = p.matcher(result);
                result = m.replaceAll("&quot;");
                
                patterns = "'";
                p = Pattern.compile(patterns/*, Pattern.DOTALL*/);
                m = p.matcher(result);
                result = m.replaceAll("&#039;");
            }
            return result;
        }
        catch (Exception e)
        {
            return text;
        }
    }
    public static String removeHtmlGarbage(String html)
    {
        // Tem de fazer bem melhor!!!!!
        String result = null;
//        String patterns = "<.*></.*>"; // Tira mais do que devia
        String patterns = "<P></P>|<PRE>|</PRE>";
        Pattern p = Pattern.compile(patterns,Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(html);
        result = m.replaceAll("");
        
        if(result != null)
        {
            result = result.replaceAll("height=\"100%\"", "");
            result = result.replaceAll("height=100%", "");
        }
        return result;
    }
}
