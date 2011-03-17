/*Enconding=UTF-8*/
package netgest.utils;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class HexUtils 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */

    public static void main(String[] args )
    {
        int val = -298337219;

        String hexString = Integer.toString( val , 16 );
        Integer.valueOf( hexString, 16 );
        byte[] yy = HexUtils.intToByteArray( val );
        
    }

    public static final byte[] intToByteArray( int val )
    {
        
        boolean negative =  val < 0;
        if( negative ) val = -val;
        
        String hexString = Integer.toString( val , 16 );
        
        byte[] ret = new byte[4];
        byte octect = (byte)(4 - hexString.length()/2);
        for (int i = 0; i < hexString.length(); i+=2 ) 
        {
            if( i == 0 && (hexString.length() % 2) == 1 )
            {
                i--;
                octect--;
            }
            else
            {
                ret[octect] = (byte)(ret[octect] | decodeCharToHex( hexString.charAt( i ) ));
            }
            ret[octect] = (byte)(ret[octect] << 4);
            ret[octect] = (byte)(ret[octect] | decodeCharToHex( hexString.charAt( i+1 ) ));
            octect++;
        }
        if( negative ) ret[0] = (byte)(ret[0] | 0x80);
        return ret;
        
    }
    public static final int intFromByteArray( byte[] val )
    {
        if( val==null ) throw new NullPointerException(MessageLocalizer.getMessage("THE_ARRAY_IS_NULL_CONVERSION_NOT_POSSIBLE"));
        if( val.length < 4 ) throw new IllegalArgumentException(MessageLocalizer.getMessage("INTEGER_VALUSE_MUST_BE_FOUR_BYTE_ARRAY"));
        
        StringBuffer newHexString = new StringBuffer();
        if( (val[0] & 0x80) != 0 )
        {   
            val[0] = (byte)(val[0] & 0x7F);
            newHexString.append('-');
        }
//        boolean start = false;
        for (int i = 0; i < 4; i++) 
        {
            byte cb = (byte)(val[i]>>>4);
            cb = (byte)( cb  & 0x0F );
//            if( start || cb != 0 )
//            {
//                start = true;
                newHexString.append( charVal[cb] );
//            }
            cb = (byte)( val[i] & 0x0F );
//            if( start || cb != 0 )
//            {
//                start = true;
                newHexString.append( charVal[cb] );
//            }
        }
        return Integer.parseInt( newHexString.toString(), 16 );
    }
    
    private static final byte decodeCharToHex( char xchar )
    {
        switch ( xchar )
        {
            case '0':
                return 0x0; 
            case '1':
                return 0x1; 
            case '2':
                return 0x2; 
            case '3':
                return 0x3; 
            case '4':
                return 0x4; 
            case '5':
                return 0x5; 
            case '6':
                return 0x6; 
            case '7':
                return 0x7; 
            case '8':
                return 0x8; 
            case '9':
                return 0x9; 
            case 'a':
            case 'A':
                return 0xA; 
            case 'b':
            case 'B':
                return 0xB; 
            case 'c':
            case 'C':
                return 0xC; 
            case 'd':
            case 'D':
                return 0xD; 
            case 'e':
            case 'E':
                return 0xE; 
            case 'f':
            case 'F':
                return 0xF;
            default:
                throw new IllegalArgumentException(String.valueOf( xchar ) + " "+MessageLocalizer.getMessage("CANNOT_BE_CONVERTED_TO_HEX"));
        }
    }
    static final char[] charVal = { '0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f' };

}