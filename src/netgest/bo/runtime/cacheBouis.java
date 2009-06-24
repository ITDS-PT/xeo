/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.Hashtable;
import netgest.utils.*;

/**
 * 
 * @author JMF
 */
public final class cacheBouis 
{
    private static Hashtable bouis;
    private static Hashtable remote_bouis;
    private static Hashtable users;
    private static Hashtable bouisInvalid; 
    private static Hashtable classDefaultTemplate = new Hashtable(); 
    
    private static final Boolean YES = new Boolean( true );
    private static final Boolean NO = new Boolean( false );
    
    private static long hits;
    static
    {
        bouis  = new Hashtable( 3000 );
        remote_bouis = new Hashtable(  );
        
        users  = new Hashtable( 20 );
        bouisInvalid = new Hashtable( 1500 );
        hits=0;
    }
    
    public cacheBouis()
    {
    }
    public static int getSizeCacheBouis()
    {
        return bouis.size();
    }
    
    public static int getSizeUsers()
    {
        return users.size();
    }
    
    public static void registerRemoteBoui( long boui, boObjectFactory f, boObjectFactoryData fd ) {
    	remote_bouis.put( new Long( boui ) , new Object[] { f, fd } );
    }
    
    public static Object[] getRemoteBoui( long boui ) {
    	return getRemoteBoui( new Long( boui ));
    }

    public static Object[] getRemoteBoui( Long boui ) {
    	return (Object[])remote_bouis.get( boui  );
    }
    
    public static String getClassName( long boui ) 
    {
        if(bouisInvalid.get(new Long( boui )) == null || 
            !((Boolean)bouisInvalid.get(new Long( boui ))).booleanValue())
        {
            hits++;
            return (String) bouis.get( new Long( boui ) );
        }
        return null;
//      String ret=(String) bouis.get( new Long( boui ) );
//      if( ret != null)
//      {
//          hits++;
//          logger.debug("hits:"+hits );
//      }
//      return ret;
    }
    public static String getClassName( Long boui ) 
    {
      return (String) bouis.get( boui );
    }
    public static void putBoui( long boui , String className ) 
    {
        if(bouisInvalid.get(new Long( boui )) == null || 
            !((Boolean)bouisInvalid.get(new Long( boui ))).booleanValue())
        {
            bouis.put( new Long(boui),className  );
        }
        else
        {
            //se for um inválido
            //vou veriificar se confirma a nova classe se confimar passa para válido
            String newClass = (String) bouis.get( new Long( boui ) );
            if(newClass != null)
            {
                if(newClass.equals(className))
                {
                    bouisInvalid.remove(new Long( boui ));
                }
            }
        }
       
    }
    
    public static void putBoui( long boui , String className, boolean invalid ) 
    {
       bouis.put( new Long(boui),className  );
       bouisInvalid.put( new Long(boui), new Boolean(invalid));
    }
    
    public static void putBoui( Long boui , String className ) 
    {
       bouis.put( boui,className  );
       
    }
    
    public static void put_userReadThisBoui( Long performerBoui , long boui )
    {
        Hashtable u = (Hashtable)users.get( performerBoui );
        if ( u == null )
        {
            u=new Hashtable(100);
            users.put( performerBoui , u );
        }
        
        u.put( new Long(boui), YES );
    }
    
    public static void put_userUnReadThisBoui( Long performerBoui , long boui )
    {
        
        Hashtable u = (Hashtable)users.get( performerBoui );
        if ( u == null )
        {
            u=new Hashtable(100);
            users.put( performerBoui , u );
            
        }
        u.put( new Long(boui), NO );
    }
    
    public static Boolean get_userReadThisBoui( Long performerBoui , long boui )
    {
        
        Hashtable u = (Hashtable)users.get( performerBoui );
        if ( u != null )
        {
           return (Boolean) u.get( new Long(boui) ) ;
            
        }
        return null;
    }

    public static void cleanCacheBoui()
    {
        bouis.clear();
        bouisInvalid.clear();
    }
    
    public static long cacheBouisHits()
    {
        return hits;
    }
    
    public static int cacheBouisSize()
    {
        return bouis.size();
    }
    
    public static int cacheBouisInvalidsSize()
    {
        return bouisInvalid.size();
    }
    
    public static void putClassDefaultTemplate( String className, Long templateBoui )
    {
        classDefaultTemplate.put( className, templateBoui );
    }
    
    public static Long getClassDefaultTemplate( String className )
    {
        Long ret = (Long)classDefaultTemplate.get( className );
        return ret;
    }
}