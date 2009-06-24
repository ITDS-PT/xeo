/*Enconding=UTF-8*/
package netgest.bo.system;
import java.util.*;

/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public class boSessionLessManager 
{
    /**
     * 
     * @see 
     */
    private static Hashtable p_bosessions= new Hashtable();
    public boSessionLessManager()
    {
    }
    public static boSession getSessionById( String id )
    {
        if( id == null ) return null;
        return ( boSession)p_bosessions.get( id );
    }
    public static void putSession( boSession s )
    {
        
        p_bosessions.put( s.getId() , s );
    }
    public static void removeSessionById( String id )
    {
        p_bosessions.remove( id );
    }
}