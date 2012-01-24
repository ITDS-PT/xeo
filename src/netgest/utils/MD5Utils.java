/*Enconding=UTF-8*/
package netgest.utils;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

public class MD5Utils  {

    public static final String toHexMD5(byte[] toencode) {
        if(toencode==null) throw new NullPointerException(MessageLocalizer.getMessage("STRING_TO_ENCODE_CANNOT_BE_NULL"));
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            return toHexString(digest.digest(toencode));
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_ENCODING_TO_MD5")+e.getMessage());
        }
    }
    public static final String toHexMD5(String toencode) {
        return toHexMD5(toencode.getBytes());
    }
    public static final String getRandomHexKey() {
        Random rnd = new Random((long)(System.currentTimeMillis()+Math.log(System.currentTimeMillis()*2)));
        byte[] key = new byte[32];
        rnd.nextBytes(key);
        return toHexMD5(key);
    }
    public static String toHexString ( byte[] b ) 
    { 
        StringBuffer sb = new StringBuffer( b.length * 2 ); 
        for ( int i=0 ; i<b.length ; i++ ) 
        { 
            // look up high nibble char 
            sb.append( hexChar [ ( b[ i] & 0xf0 ) >>> 4 ] ) ; 
        
            // look up low nibble char 
            sb.append( hexChar [ b[ i] & 0x0f ] ) ; 
        } 
        return sb.toString() ; 
    } 

    public static long getCheckTime(long time)
    {
        // compute timeCheck
        long primes[] =
            new long[] {11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 11, 13};
        int startIndex = (int)(time % 10);
        
        long timeCheck = (((time + primes[startIndex]) * primes[startIndex + 1]) -
            primes[startIndex + 2]) % (1000000 + startIndex);
        return timeCheck;
    }

    public static boolean rebuildMD5UserCode(EboContext ctx)
    {
        try
        {
            boObjectList users = boObjectList.list(ctx,"select iXEOUser where id is not null", 999999999,999999999);
            users.beforeFirst();
            String md5Code;
            boObject user;
            int count = 0;
            String x;
            while(users.next())
            {
                user = users.getObject();
                x = user.getAttribute("id").getValueString();
                MessageDigest md = MessageDigest.getInstance("MD5");
                byte bytes[] = md.digest(x.toLowerCase().getBytes());
                user.getAttribute("MD5Code").setValueString(ClassUtils.byteArrayToHexString(bytes));
                user.update();
                count++;
            }
            return true;
        }
        catch (boRuntimeException e)
        {
            //ignore
        }
        catch(Exception e)
        {
            //ignore
        }
        return false;
    }
        
    // table to convert a nibble to a hex char. 
    static char[] hexChar = 
    { 
    '0' , '1' , '2' , '3' , 
    '4' , '5' , '6' , '7' , 
    '8' , '9' , 'A' , 'B' , 
    'C' , 'D' , 'E' , 'F' } 
    ;     
    }