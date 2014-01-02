/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.Hashtable;

import netgest.bo.data.XEODataManagerKey;

/**
 * 
 * @author JMF
 */
public final class cacheBouis 
{
	private static long		 remote_boui_cntr = Long.MAX_VALUE + 1000;
	
    private static Hashtable bouis;
    
    private static Hashtable remote_bouis = new Hashtable();
    private static Hashtable remote_keys = new Hashtable();
    
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
    
    public static void createRemoteBoui( EboContext ctx, XEODataManagerKey remoteKey ) {
    	Long boui;
    	synchronized( cacheBouis.class ) {
    		XEODataManagerKey key = null; 
    		
    		String objName 			= remoteKey.getObjectName();
    		String serializedKey	= remoteKey.serialize();
    		
    		Hashtable ht = (Hashtable)remote_keys.get( objName );
    		if( ht != null ) {
    			key = (XEODataManagerKey)ht.get( serializedKey );
    		}
    		if( key == null ) {
    			key = remoteKey;
        		boui = new Long( cacheBouis.remote_boui_cntr++ );
        		key.setBoui( boui.longValue() );
        		if( ht == null ) {
        			ht = new Hashtable();
        			remote_keys.put( objName, ht );
        		}
        		ht.put(  serializedKey, key );
        		remote_bouis.put( boui, remoteKey );
    		}
    		else {
    			boui = new Long( key.getBoui() );
    			remoteKey.setBoui( key.getBoui() );
        		remote_bouis.put( boui, remoteKey );
        		ht.put( key.serialize(), remoteKey );
    		}
    	}
    }
    
    public static XEODataManagerKey getRemoteBouiKey( Long boui ) {
    	return (XEODataManagerKey)remote_bouis.get( boui  );
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