/*Enconding=UTF-8*/
package netgest.bo.http;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import netgest.bo.dochtml.docHTML_controler;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessions;
import netgest.bo.system.Logger;

public class boHTTPSessionListener implements javax.servlet.http.HttpSessionListener 
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.http.boHTTPSessionListener");
    
    // Last clean Sessions
    private long   LastClean     = System.currentTimeMillis();
    
    public void sessionCreated(HttpSessionEvent event)
    {
//        logger.finest("Session created..");
        HttpSession session = event.getSession(); 
        
    }

    public void sessionDestroyed(HttpSessionEvent event)
    {
        try
        {
            HttpSession session = event.getSession();
    
            boSession bosession = (boSession)session.getAttribute("boSession");
            if( bosession != null )
            {
                try
                {
                    bosession.markLogout();
                }
                catch (Exception e)
                {
                    
                }
                try
                {
                    bosession.closeSession();
                }
                catch (Exception e)
                {
                    
                }
    
                docHTML_controler doc = ( docHTML_controler )session.getAttribute( "DOCLIST" );
                if( doc != null )
                {
                    bosession.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( doc.poolUniqueId() );
                    session.setAttribute("DOCLIST",null);
                    session.setAttribute("boSession",null);
                }
    
            }
        }
        catch (Exception e)
        {
            logger.severe("Error closing session.",e);
        }
        
        if( ( System.currentTimeMillis() - LastClean ) > (600000) )
        {
            LastClean = System.currentTimeMillis();
            cleanSessions();            
        }
    }
    
    public static synchronized void cleanSessions()
    {
        long lmillis = System.currentTimeMillis();
        int  icounter = 0;
        try
        {
            boSessions sessions = boApplication.getApplicationFromStaticContext("XEO").getSessions();
            
            if(sessions != null)
            {
                boSession[] aSessions = sessions.getActiveSessions();
                
                if(aSessions != null && aSessions.length > 0)
                {
                    for (int i = 0; i < aSessions.length ; i++) 
                    {
                        if(aSessions[i] != null)
                        {
                            if( (lmillis - aSessions[i].getLastActivity().getTime()) > (1000*60*60*24) )
                            {
                                aSessions[i].closeSession();
                                icounter++;
                            }
                        }
                        else
                            logger.finest("boHTTPSessionListener.cleanSessions(): Session was Null, shouldn't happen!");
                    }
                    
                    if( logger.isFinestEnabled() )
                        logger.finest( icounter + " Sessions forced to close..." );
                    
                }//if(aSessions != null && aSessions.length > 0)
                else
                    logger.finest("boHTTPSessionListener.cleanSessions(): Active Sessions returned Null or has No Active Sessions");
                
            }//if(sessions != null)
            else
                logger.finest("boHTTPSessionListener.cleanSessions(): Sessions returned Null");
        }
        catch(Exception e)
        {
            e.printStackTrace();
            logger.severe("Error cleaning sessions", e);
        }
    }
}