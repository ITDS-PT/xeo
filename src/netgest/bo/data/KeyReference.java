/*Enconding=UTF-8*/
package netgest.bo.data;
import java.util.Arrays;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class KeyReference extends Exception 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     
     Object[] p_value = new Object[0];
     String[] p_name = new String[0];
     int[]    p_type = new int[0];
     
     
     
    public KeyReference( String[] keys, Object[] values )
    {
        this.p_name  = keys;
        this.p_value = values;
    }
    public Object getKeyValue( int pos )
    {
        pos--;
        if( pos > -1  &&  pos < p_name.length )
        {
            return this.p_value[pos];
        }
        throw new boRuntimeException2(MessageLocalizer.getMessage("INVALID_KEY_INDEX_VALID_RANGE_IS")+" [ 1.."+(this.p_name.length-1)+" ]");
    }
    
    public void setValue(String key, Object value)
    {
        int pos = findKey(key);
        if(pos != -1)
        {
            p_value[pos] = value;
        }
    }
    
    public Object getKeyValue( String key )
    {
        int pos = findKey( key );
        if( pos > -1 )
        {
            return this.p_value[pos];
        }
        throw new boRuntimeException2(MessageLocalizer.getMessage("KEY_NOT_FOUND"));
    }
    public String[] getKeys()
    {
        return this.p_name;
    }
    
    public int findKey( String name )
    {
        int ret = -1;
        for (int i = 0; i < this.p_name.length; i++) 
        {
            if( this.p_name[i].equals(name) )
            {
                ret = i;
                break;
            }
        }
        return ret;
    }
    
//    private void addKey( String name, int type, Object value )
//    {
//        int pos = p_value.length + 1;
//        Object[] xvalue = new Object[pos];
//        String[] xname  = new String[pos];
//        int[]  xtype = new int[pos];
//        
//        System.arraycopy( p_value, 0, xvalue, 0, p_value.length );
//        System.arraycopy( p_name, 0, xname, 0, p_name.length );
//        System.arraycopy( p_type, 0, xtype, 0, p_type.length );
//        
//        p_value[pos] = value;
//        p_type[pos] = type;
//        p_name[pos] = name;
//        
//    }
}