/*Enconding=UTF-8*/
package netgest.bo.system;

import java.util.WeakHashMap;

import netgest.utils.MD5Utils;


/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boSessions
{
    /**
     *
     * @Company Enlace3
     * @since
     */
    private WeakHashMap p_activeSessions = new WeakHashMap();
    private boApplication p_app;

    protected boSessions(boApplication app)
    {
        p_app = app;
    }

    public synchronized boSession[] getActiveSessions()
    {
        boSession[] sessions = ( boSession[] ) p_activeSessions.values().toArray(
                new boSession[p_activeSessions.size()]
            );

        return sessions;
    }
    
    
    

    public synchronized boSession createSession(String repository, boSessionUser user, String clientName, String remoteAddr, String remoteHost, String remoteUser, String remoteSessionId )
    {
        try
        {
            Thread.sleep(1);
        }
        catch (InterruptedException e)
        {
        }

        String xid    = MD5Utils.getRandomHexKey();
        boSession ret;
        p_activeSessions.put(xid, ret = new boSession(xid, user, repository, clientName, p_app, remoteAddr, 
            remoteHost, remoteUser, remoteSessionId));

        return ret;
    }

    public void removeSession(boSession session)
    {
        p_activeSessions.remove(session.getId());
    }

    public boSession getSessionById(String id)
    {
        return ( boSession ) p_activeSessions.get(id);
    }
}
