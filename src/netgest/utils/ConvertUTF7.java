/*Enconding=UTF-8*/
package netgest.utils;
import java.lang.StringBuffer;
import java.nio.charset.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class ConvertUTF7 
{
    private static final char base64[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','+','/'};
    private static int invbase64[] = new int[128];
//    private static char direct[] = {'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z','a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z','0','1','2','3','4','5','6','7','8','9','(',')',',','-','.','/',':','?'};
//    private static char optional[] = {'!', '\\', '"', '#', '$', '%', '&', '*', ';', '<', '=', '>', '@', '[', ']', '^', '_', '`', '{','|', '}'};
//    private static char spaces[] = {'\t', '\r', '\n'};
    private boolean needtables = true;
    private static final char SHIFT_IN ='+';
    private static final char SHIFT_OUT ='-';
    private long bitBuffer = 0;
    private long bufferbits = 0, buffertemp = 0;
    private String source;

    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public ConvertUTF7(String source)
    {
        this.source = source;
    }
    
    private void tabinit()
    {
        int i, limit;

        for (i = 0; i < 128; ++i)
        {
            invbase64[i] = -1;
        }
        limit = base64.length;
        for (i = 0; i < limit; ++i)
            invbase64[base64[i]] = i;
    
        needtables = false;
    }

    private void writeNbits(int x, int n)
    {
//        (bitBuffer |= ( ((x) & ~(-1L<<(n))) << (32-(n)-bufferbits) ) );
        bufferbits += (n);
    }
    private long readNbits(long n)
    {
//        (buffertemp = (bitBuffer >> (32-(n))));
//        (bitBuffer <<= (n));
        bufferbits -= (n);
        return buffertemp;
    }
    
    public String convertUTF7()
    {
        boolean result = true;
        StringBuffer sb = new StringBuffer();
        boolean shifted = false, first = false, wroteone = false, done = false;
        boolean base64EOF = false;
        int base64value = -1;
        int c, prevc;
        long junk;
    
        if (needtables)
            tabinit();
    
        /* read an ASCII character c */
//        int i  = 0;
//        char c;
        for (int i = 0; i < source.length(); i++) 
        {
            done = false;
            c = source.charAt(i);
            char cc = source.charAt(i); 
            if(shifted)
            {
                /* We're done with a base64 string if we hit EOF, it's not a valid
                    ASCII character, or it's not in the base64 set.
                */
                base64EOF = done || (c > 0x7f) || (base64value = invbase64[c]) < 0;
                if (base64EOF)
                {
                    shifted = false;
                    /* If the character causing us to drop out was SHIFT_IN or
                       SHIFT_OUT, it may be a special escape for SHIFT_IN. The
                       test for SHIFT_IN is not necessary, but allows an alternate
                       form of UTF-7 where SHIFT_IN is escaped by SHIFT_IN. This
                       only works for some values of SHIFT_IN.
                    */
                    if (!done && (c == SHIFT_IN || c == SHIFT_OUT))
                    {
                        /* get another character c */
                        prevc = c;                        
                        if (!(i >= (source.length() - 1)))
                        {
                            i++;
                            c = source.charAt(i);
                        }
                        else
                        {
                            done = true;
                        }
                        /* If no base64 characters were encountered, and the
                           character terminating the shift sequence was
                           SHIFT_OUT, then it's a special escape for SHIFT_IN.
                         */
                        if (first && prevc == SHIFT_OUT)
                        {
                            /* write SHIFT_IN unicode */
                            sb.append(SHIFT_IN);
                        }
                        else if (!wroteone)
                        {
                            result = false;
                            /* fprintf(stderr, "UTF7: empty sequence near byte %ld in input\n",source-sourceStart) */;
                        }
                        else
                        {
                            //bufferbits = 0;
                        }   
                    }
                    else if (!wroteone)
                    {
                        result = false;
                        /* fprintf(stderr, "UTF7: empty sequence near byte %ld in input\n",source-sourceStart) */;
                    }
                }
                else
                {
                    /* Add another 6 bits of base64 to the bit buffer. */
                    writeNbits(base64value, 6);
                    first = false;
                }
                while (bufferbits >= 16)
                {
                    /* write a unicode */
                    sb.append((char)readNbits(16));
                    wroteone = true;
                }
                if (base64EOF)
                {
                    junk = readNbits(bufferbits);
                    if (bufferbits != 0)
                    {
                        result = false;
                        /* fprintf(stderr, "UTF7: non-zero pad bits near byte %ld in input\n", source-sourceStart) */;
                    }
                }
            }
            if (!shifted && !done)
            {
                if (c == SHIFT_IN)
                {
                    shifted = true;
                    first = true;
                    wroteone = false;
                }
                else
                {
                    /* It must be a directly encoded character. */
                    if (c > 0x7f)
                    {
                        result = false;
                        /* fprintf(stderr, "UTF7: non-ASCII character near byte %ld in input\n", source-sourceStart) */;
                    }
                    /* write a unicode */
                    sb.append((char)c);
                }
            }
        }
        if(result)
        {
            return sb.toString();
        }
        return null;
    }    
}