/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.util.*;
import netgest.utils.*;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boObjectUpdateQueue
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boObjectUpdateQueue");

    public static final byte MODE_SAVE_FORCED=4;
    public static final byte MODE_SAVE=2;
    public static final byte MODE_DESTROY=3;
    public static final byte MODE_DESTROY_FORCED=5;

    public static final byte MODE_SAVE_REVERSE=6; // Caso das reverse Bridges

    private ArrayList 	p_queueList = new ArrayList();
    private Map 		p_queueMap  = new HashMap();

    public void add(boObject object, byte mode)
    {
        if( object.bo_boui == 0 )
        {
            error();
        }
        else
        {
            add( object.bo_boui , mode );
        }
    }


    private void error()
    {
        Throwable x = new Throwable();
        logger.finest(LoggerMessageLocalizer.getMessage("ERROR_ADDING_TO_SAVE_QUEUE_A_OBJECT_WITH_BOUI_CHECK_"), x);
    }
    public void add(long boui, byte mode)
    {
        if( boui==0 )
        {
            error();
        }
        else
        {
        	
            Long toadd = new Long(boui);
            
            Object[] map = (Object[])p_queueMap.get( toadd  );
            
            
            int idx = -1;
            
            if( map != null ) {
                Long lidx   = (Long)(map)[0];
            	idx = lidx.intValue();
            }
            
            Byte xmode;
            if( idx > -1 )
            {
                xmode = (Byte)map[1];
                if( xmode.byteValue() != mode && !(xmode.byteValue() == MODE_SAVE_FORCED || xmode.byteValue() == MODE_DESTROY_FORCED ) )
                {
                    //p_queue.set( toadd  );
                	p_queueMap.put( toadd, new Object[] { map[1], new Byte( mode ) } );
                    //p_queueMode.set( idx, new Byte( mode ) );
                }
            }
            else
            {
                p_queueMap.put( toadd, new Object[] { new Long(p_queueList.size()), new Byte( mode ) } );
                p_queueList.add( toadd );
//                p_queue.put(new Long(boui), new Byte( mode ) );
            }
        }
    }

    public boolean haveBoui( long boui )
    {
        return p_queueMap.containsKey( new Long(boui) );
    }
    public byte getUpdateMode( long boui )
    {
    	Long lboui = new Long( boui );
        Object[] map = (Object[])p_queueMap.get( lboui  );
        if( map != null ) {
            Byte xm = (Byte)map[1];
            if( xm != null )
            {
                return xm.byteValue();
            }
        }
        return -1;
    }

    public void remove(long boui)
    {
    	Long lboui = new Long( boui );
        Object[] map = (Object[])p_queueMap.remove( lboui  );
        if( map != null ) {
        	p_queueList.remove( ((Long)map[0]).intValue() );
        }
      
    }
    public void clear()
    {
        p_queueList.clear();
        p_queueMap.clear();
    }
    public long[][] getObjects()
    {
        long[][] ret = new long[p_queueList.size()][2];
        Iterator oEnum = p_queueList.iterator();
        short i =0;
        while( oEnum.hasNext() )
        {
            Long boui = (Long)oEnum.next();
            ret[i][0] = boui.longValue();
            
            Object[] map = (Object[])p_queueMap.get( boui  );
            
            ret[i][1] = ((Byte)map[1]).longValue();
            i++;
        }
        return ret;
    }
    public Long[] getObjectsToRemove()
    {
    	Iterator oEnum = p_queueList.iterator();
    	ArrayList x= new ArrayList();
        while( oEnum.hasNext() )
        {
            Long boui = (Long)oEnum.next();
            
            Object[] map = (Object[])p_queueMap.get( boui  );
            Byte xm = (Byte)map[1];
            
            if ( xm.byteValue() == boObjectUpdateQueue.MODE_DESTROY_FORCED )
            {
               x.add( boui );
            }
        }
        return (Long[]) x.toArray( new Long[ x.size() ] );

    }

    public Long[] getObjectsToSave()
    {
        return getObjectsToSave( boObjectUpdateQueue.MODE_SAVE_FORCED );
    }
    public Long[] getObjectsToSave( byte mode )
    {
        Iterator oEnum = p_queueList.iterator();
        ArrayList x= new ArrayList();
        while( oEnum.hasNext() )
        {
            Long boui = (Long)oEnum.next();
            Object[] map = (Object[])p_queueMap.get( boui  );
            Byte xm = (Byte)map[1];
            if ( xm.byteValue() == mode )
            {
               x.add( boui );
            }
        }
        return (Long[]) x.toArray( new Long[ x.size() ] );

    }
}