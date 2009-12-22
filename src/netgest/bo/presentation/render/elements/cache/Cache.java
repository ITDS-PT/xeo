package netgest.bo.presentation.render.elements.cache;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;
import netgest.bo.system.Logger;

public class Cache
{
    private static Logger logger = Logger.getLogger(
            "netgest.bo.presentation.render.elements.cache.Cache");
    private int minutes;
    private int maxObjects;//pode ser aumentado automaticamente
    private int gap;
    private Hashtable cache;
    private int shrinkTimes = 0;
    private String cacheName = null;
    private boolean lruRunning = false;

    public Cache(String cacheName, int minutes, int maxObjects, int gap)
    {
        logger.finer("New Cache Created:" + cacheName + " minutes:" + minutes + " max_objects:" + maxObjects + " gap:" +gap);
        cache = new Hashtable();
        this.cacheName = cacheName;
        this.minutes = minutes;
        this.maxObjects = maxObjects;
        this.gap = gap;
    }

    public String getState()
    {
        logger.finer("New Cache Created:" + cacheName + " minutes:" + minutes + " max_objects:" + maxObjects + " gap:" +gap+" shrinkTimes:" +shrinkTimes);
        return "New Cache Created:" + cacheName + " minutes:" + minutes + " max_objects:" + maxObjects + " gap:" +gap+" shrinkTimes:" +shrinkTimes;
    }

    public int getSize()
    {
        return cache.size();
    }

     public String getName()
    {
        return cacheName;
    }

    public int getShrinkTimes()
    {
        return shrinkTimes;
    }

    public void clear()
    {
        logger.finer("Cache "+cacheName+" ("+cache.size()+") cleared");
        cache.clear();
    }

    public void remove(String key)
    {
        logger.finer("Cache removed Key("+key+")");
        cache.remove(key);
    }

    public void remove(long user)
    {
        logger.finer("Cache "+cacheName+" removed user("+user+")");
        Enumeration oEnum = cache.keys();
        CacheElement c = null;
        String k = null;
        while (oEnum.hasMoreElements())
        {
            k = (String)oEnum.nextElement();
            c = (CacheElement)cache.get(k);
            if(c.getUser() == user)
            {
                cache.remove(k);
            }
        }
    }

    public void removeWPrefix(String prefix)
    {
        logger.finer("Cache "+cacheName+" removed explorers w prefix("+prefix+")");
        Enumeration oEnum = cache.keys();
        CacheElement c = null;
        String k = null;
        while (oEnum.hasMoreElements())
        {
            k = (String)oEnum.nextElement();
            if(k.startsWith(prefix))
            {
                cache.remove(k);
            }
        }
    }

    public void put(String key, Object element, long user)
    {
        CacheElement ce = null;
        if((ce = (CacheElement)cache.get(key)) != null)
        {
            ce.setElement(key, element, user);
        }
        else
        {
            ce = new CacheElement(key, element, user);
            cache.put(key, ce);
        }

        if( (getSize() ) >= maxObjects )
        {
            logger.finer("["+cacheName+"] LRU Started running ");
            if( !lruRunning )
            {
                shrinkTimes++;
                runLRU();
            }
            logger.finer("["+cacheName+"] LRU ENDED running ");
        }
    }

    public Object get(String key)
    {
        CacheElement ce = (CacheElement)cache.get(key);
        if(ce != null)
        {
            return ce.getElement();
        }
        return null;
    }

    public synchronized void runLRU()//last recently use removed
    {
        try
        {
            lruRunning = true;
            synchronized( this )
            {
                long validTime = System.currentTimeMillis();
                validTime -= Math.abs( minutes * 60000 );
//                if(minutes > 0)
//                {
                    //c.roll(Calendar.MINUTE, -minutes);
//                }
//                else
//                {
//                    currentTime += ( minutes * 60000 );
                    //c.roll(Calendar.MINUTE, minutes);
//                }
                Enumeration oEnum =  cache.elements();
                CacheElement ce = null;
                int save = maxObjects - gap;
                int run = 1;
                boolean runOne = false;
                while(!runOne || (getSize() > maxObjects && run <= 3))
                {
                    logger.finer("["+cacheName+"] Started  cycle "+run+": " + getSize());
                    runOne = true;
                    while (oEnum.hasMoreElements())
                    {
                        ce = (CacheElement)oEnum.nextElement();
                        if(!ce.isValid( validTime ))
                        {
                            cache.remove(ce.getKey());
                        }
                        if( cache.size() <= maxObjects-gap )
                        {
                            break;
                        }

                    }

                    run++;
                    if ( getSize() + gap >= maxObjects )
                    {
                        //vou tirar mais minutos para libertar memoria até ao GAP.
                        validTime -= Math.abs( minutes * 60000 );
                    }
                    logger.finer("["+cacheName+"] ENDED cycle "+run+": " + getSize());
                }

                if( shrinkTimes > 20 )
                {
                    if(getSize() > maxObjects)
                    {
                        maxObjects = maxObjects + (maxObjects/2);
                        shrinkTimes = 0;
                    }
                }
            }
        }
        finally
        {
            lruRunning = false;
        }
    }
}