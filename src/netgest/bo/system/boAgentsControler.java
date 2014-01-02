/*Enconding=UTF-8*/
package netgest.bo.system;
import java.lang.reflect.*;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;
import javax.naming.*;
import java.util.*;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boAgentsControler extends Thread implements IboAgentsController
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.boAgentsControler");
    /**
     *
     * @since
     */
    /**
     *
     * @Company Enlace3
     * @since
     */

    private ThreadGroup  tGroup = null;  // Group of threads
    private Hashtable    tHreads     = new Hashtable(); // Array with Threads;
    private Hashtable    deadThreads = new Hashtable(); // Array with threads that have errors;

    public static final int     maxWaitTime = 60000;  // Max time to wait when concurrent message hapen.

   // public static  String[] THREADS_CLASS =  {"netgest.bo.runtime.robots.boScheduleAgent" ,
   //"netgest.bo.runtime.robots.boTextIndexAgent" , "netgest.bo.runtime.robots.boQueueAgent",
   //"netgest.bo.runtime.robots.xwfTimeAgent", "netgest.bo.runtime.robots.boAcsAlertAgent"};
   // public static  String[] THREADS_NAME  = {"boSchedule Agent" , "boTextIndex Agent", "boQueue Agent", "xwfTimeAgent", "ACS_Alert Agent" };


    private boApplication p_boapp;

    public boAgentsControler( boApplication boapp )
    {

        super(
            new ThreadGroup("XEO Agents"),
            "XEO Agents Controller"
        );
        this.p_boapp = boapp;
        tGroup = this.getThreadGroup();
    }

    public void run()
    {
        try
        {
        	logger.config("boAgents Controller started");
        	Thread.sleep(3000); //was 30.000
            while( !isInterrupted() && checkXeo() )
            {
                String[] threads_name = p_boapp.getApplicationConfig().getThreadsName();
                for (int i = 0; i < threads_name.length ; i++)
                {
                    chekAndStartThread( i );
                }
                int wcnt = 0;
                while( wcnt < 60 )
                {
                    if (!checkXeo())
                    {
                        super.interrupt();
                    }
                    else
                    {
                        Thread.sleep(1000);
                    }
                    wcnt++;
                }
            }
        }
        catch (InterruptedException e)
        {
           
        }
        try
        {
            suspendAgents();
            tGroup.interrupt();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Object[] getThreadByName( String name )
    {
        if( tHreads.get( name ) != null )
        {

            return (Object[])((ArrayList)tHreads.get( name )).toArray(  );
        }
        return null;
    }



    public void chekAndStartThread( int id_idx )
    {
        String name = p_boapp.getApplicationConfig().getThreadsName()[ id_idx ]; //THREADS_NAME[ id_idx ];
        ArrayList list = (ArrayList)tHreads.get( name );
        if( list == null )
        {
            tHreads.put( name, list = new ArrayList() );
        }

        if( deadThreads.get( name ) != null && ((Boolean)deadThreads.get( name )).booleanValue() )
        {
            // Thread connot started
        }
        else
        {
            // Check Dead Listeners
            for (int i = 0; i < list.size(); i++)
            {
                Thread activeListener = (Thread)list.get( i );
                if( !activeListener.isAlive() )
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("DEAD_XEO_THREAD_FOUND_REMOVING_IT"));
                    list.remove( i );
                    p_boapp.removeContextFromThread( activeListener );

                }
            }

    //        // Start Threads
            Thread xthread = null;
            while( list.size() < 1 ) // this is create concurrent Threads
            {
                try
                {
                    String tname = "XEO Thread [" + name  +"]";
                    logger.finest(LoggerMessageLocalizer.getMessage("STARTING")+" " + tname );
                    Class threadClass = Class.forName(  p_boapp.getApplicationConfig().getThreadsClass()[ id_idx ]  );
                    Constructor constructor = threadClass.getConstructor( new Class[] {ThreadGroup.class,boApplication.class,String.class} );
                    xthread = (Thread)constructor.newInstance( new Object[] { this.tGroup, this.p_boapp, tname } );
                    p_boapp.addContextToThread( xthread );
                    list.add( xthread );
                    xthread.start();
                }
                catch (InstantiationException e)
                {
                    logger.warn( e );
                }
                catch (InvocationTargetException e)
                {
                    logger.warn( e );

                }
                catch (NoSuchMethodException e)
                {
                    logger.warn( e );

                }
                catch (IllegalAccessException e)
                {
                    logger.warn( e );

                }
                catch (ClassNotFoundException e)
                {
                    logger.warn( e );
                }
                if( xthread == null )
                {
                    deadThreads.put( name, new Boolean( true ) );
                }
            }
        }
    }

    public static final boolean checkXeo()
    {
        boolean ret = true;
        try
        {
            final InitialContext ic = new InitialContext();
            ic.lookup("boLogin");  // Test if OC4J is up and running;
        }
        catch( Throwable e )
        {
            ret = false;
        }
        return ret;
    }

    public void suspendAgents()
    {
        Enumeration oEnum = tHreads.elements();
        while ( oEnum.hasMoreElements() )
        {
            ArrayList active = (ArrayList)oEnum.nextElement();
            for (int i = 0;active != null && i < active.size(); i++)
            {
                try
                {
                    Thread thread = ((Thread)active.get( i ));
                    logger.finest(LoggerMessageLocalizer.getMessage("STOPPING_XEO_THREAD")+" ["+ thread.getName() +"]");
                    thread.interrupt();
                    int wait = 0;                  
                    while( thread.isAlive() && !thread.isInterrupted() && wait < this.maxWaitTime )
                    {
                        Thread.sleep(1000);
                        wait += 1000;
                        thread.interrupt();                        
                    }
                    if( wait >= this.maxWaitTime )
                    {                        
                        logger.warn(LoggerMessageLocalizer.getMessage("CANNOT_STOP_THREAD")+" ["+thread.getName()+"]");
                    }
                }
                catch (Exception e)
                {

                }
            }
        }
        this.interrupt();
    }

}