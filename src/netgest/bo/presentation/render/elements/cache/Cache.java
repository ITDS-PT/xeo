package netgest.bo.presentation.render.elements.cache;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Hashtable;

import netgest.bo.localizations.LoggerMessageLocalizer;
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
        logger.finer(LoggerMessageLocalizer.getMessage("NEW_CACHE_CREATED")+":" + cacheName + " "+LoggerMessageLocalizer.getMessage("MINUTES")+":" + minutes + " "+LoggerMessageLocalizer.getMessage("MAX_OBJECTS")+":" + maxObjects + " "+LoggerMessageLocalizer.getMessage("GAP")+":" +gap);
        cache = new Hashtable();
        this.cacheName = cacheName;
        this.minutes = minutes;
        this.maxObjects = maxObjects;
        this.gap = gap;
    }

    public String getState()
    {
        logger.finer(LoggerMessageLocalizer.getMessage("NEW_CACHE_CREATED")+":" + cacheName + " "+LoggerMessageLocalizer.getMessage("MINUTES")+":" + minutes + " "+LoggerMessageLocalizer.getMessage("MAX_OBJECTS")+":" + maxObjects + " "+LoggerMessageLocalizer.getMessage("GAP")+":" +gap+" "+LoggerMessageLocalizer.getMessage("SHRINKTIMES")+":" +shrinkTimes);
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
        logger.finer("Cache "+cacheName+" ("+cache.size()+") "+LoggerMessageLocalizer.getMessage("CLEARED"));
        cache.clear();
    }

    public void remove(String key)
    {
        logger.finer(LoggerMessageLocalizer.getMessage("CACHE_REMOVED_KEY")+"("+key+")");
        cache.remove(key);
    }

    public void remove(long user)
    {
        logger.finer("Cache "+cacheName+" "+LoggerMessageLocalizer.getMessage("REMOVED_USER")+"("+user+")");
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
        logger.finer("Cache "+cacheName+" "+LoggerMessageLocalizer.getMessage("REMOVED_EXPLORERS_W_PREFIX")+"("+prefix+")");
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
            logger.finer("["+cacheName+"] LRU "+LoggerMessageLocalizer.getMessage("STARTED_RUNNING"));
            if( !lruRunning )
            {
                shrinkTimes++;
                runLRU();
            }
            logger.finer("["+cacheName+"] LRU "+LoggerMessageLocalizer.getMessage("ENDED_RUNNING"));
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
                    logger.finer("["+cacheName+"] "+LoggerMessageLocalizer.getMessage("STARTED_CYCLE")+" "+run+": " + getSize());
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
                    logger.finer("["+cacheName+"] "+LoggerMessageLocalizer.getMessage("STARTED_CYCLE")+" "+run+": " + getSize());
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