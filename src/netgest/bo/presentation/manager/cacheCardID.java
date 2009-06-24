/*Enconding=UTF-8*/
package netgest.bo.presentation.manager;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;

import java.util.Hashtable;
import netgest.bo.runtime.boRuntimeException;


public class cacheCardID
{
    private static Hashtable p_cards = new Hashtable();

    public static String getCardId(EboContext ctx, long boui)
    {
        Long key   = new Long( boui );
        String toRet = ( String ) p_cards.get(key);

        if (toRet == null)
        {
            
            try
            {
                boObject o = boObject.getBoManager().loadObject(ctx, boui);
                toRet = o.getCARDIDwLink().toString();
                p_cards.put(key, toRet);
            }
            catch (Exception e)
            {
                
            }
        }
        return toRet;
    }

    public static void putCardId(EboContext ctx, long boui) 
    {
        Long key = new Long( boui );
        try
        {
            boObject o = boObject.getBoManager().loadObject(ctx, boui);
            p_cards.put(key, o.getCARDIDwLink().toString() );
        }
        catch (boRuntimeException e)
        {
            
        }
    }
    public static void putCardId( boObject o ) throws boRuntimeException
    {
        Long key = new Long( o.getBoui());
         
        p_cards.put(key, o.getCARDIDwLink().toString() );
    }
}
