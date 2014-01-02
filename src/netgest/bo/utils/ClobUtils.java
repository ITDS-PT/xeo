/*Enconding=UTF-8*/
package netgest.bo.utils;
import java.util.Arrays;
import netgest.bo.runtime.*;
import netgest.bo.runtime.AttributeHandler;

/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public class ClobUtils 
{
    //UPPERCASE
    private final static String[] TAGS = {"<P", "</P", "<B", "</B", "<U", "</U", "<STRONG", 
            "</STRONG", "<EM", "</EM", "<DIV", "</DIV", "<OL", "</OL", "<FONT", "</FONT",
            "<LI", "</LI", "<BLOCKQUOTE", "</BLOCKQUOTE"};
    //UPPERCASE
    private final static String[] SPECIAL = {"&NBSP;"};
    /**
     * 
     * @see 
     */
    public ClobUtils()
    {
    }
    
    public static boolean isEmpty(AttributeHandler attHandler) throws boRuntimeException
    {
        boolean toRet = true;
        String value = attHandler.getValueString();
        if(value != null)
        {
            String aux;
            for (int i = 0; i < value.length(); i++) 
            {
                char cc = value.charAt(i); 
                if(value.charAt(i) == '<')
                {
                    aux = readTill(value, i, ">");
                    if(aux == null || !belongsTo(TAGS, aux))
                    {
                        return false;
                    }
                    i+=aux.length() - 1;
                }
                else if(value.charAt(i) == '&')
                {
                    aux = readTill(value, i, ";");
                    if(aux == null || !belongsTo(SPECIAL, aux))
                    {
                        return false;
                    }
                    i+=aux.length()  - 1;
                }
                else if(Character.isWhitespace(value.charAt(i)))
                {
                    //ignore
                }
                else
                {
                    return false;
                }
            }
        }
        return toRet;
    }
    
    private static String readTill(String s, int from, String c)
    {
        int to = s.indexOf(c, from);
        if(to == -1)
        {
            return null;
        }
        return s.substring(from, to + 1);
    }
    
    private static boolean belongsTo(String[] list, String val)
    {
        for(int i = 0; i < list.length; i++)
        {
            if(val.toUpperCase().startsWith(list[i]))
            {
                int nextChar = list[i].length();
                if(val.length() == nextChar ||
                    val.charAt(nextChar) == '>' || 
                    Character.isWhitespace(val.charAt(nextChar)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}