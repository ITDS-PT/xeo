/*Enconding=UTF-8*/
package netgest.bo.utils.lock;

import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import java.util.Hashtable;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class DBLock {
    private static final long LOCK_EXPIRE = 300; //300 SEGUNDOS - 5 MINUTOS
    private static final long WAIT_FOR_LOCK = 0; //0 SEGUNDOS
    private static final boolean RELEASE_ON_COMMIT = false;
    private static Hashtable lockHandlers = new Hashtable();

    /**
     *
     * @Company Enlace3
     * @since
     */
    private DBLock() {
    }

    private static String getLockID(String lockid) {
        if (!lockid.startsWith("XEO_")) {
            return "XEO_" + lockid;
        }

        return lockid;
    }

    public static boolean lock(EboContext ctx, String lockID, long expire,
        long waitForLock, boolean releaseCommit) throws boRuntimeException {
        Connection conn = null;
        CallableStatement stat = null;

        try {
            conn = ctx.getConnectionData();

            String lockHandler = (String) lockHandlers.get(getLockID(lockID));

            if (lockHandler == null) 
            {
                stat = conn.prepareCall(
                        "{ call dbms_lock.allocate_unique(?, ?, ?) }");
                stat.setString(1, getLockID(lockID) );
                stat.registerOutParameter(2, Types.VARCHAR);
                stat.setLong(3, expire);
                
                stat.execute();
                
                lockHandler = stat.getString(2);
                lockHandlers.put(getLockID(lockID), lockHandler);
                
                stat.close();
            }
            if(releaseCommit)
            {
                stat = conn.prepareCall(
                    "{ ? = call dbms_lock.request( ?, 6, ?, release_on_commit => FALSE ) }");
            }
            else
            {
                stat = conn.prepareCall(
                    "{ ? = call dbms_lock.request( ?, 6, ?, release_on_commit => TRUE ) }");                    
            }
            stat.registerOutParameter(1, Types.NUMERIC);
            stat.setString(2, lockHandler );
            stat.setLong(3, waitForLock);
            stat.execute();
            
            return stat.getLong(1) == 0;
        }
        catch (SQLException e) 
        {
            throw new boRuntimeException(MessageLocalizer.getMessage("SQL_EXCEPTION_SETTING_NEW_LOCK"), "", e);
        } 
        finally 
        {
            try 
            {
                if (stat != null) 
                {
                    stat.close();
                }
            }
            catch (SQLException ex) 
            {
                // Ignorar}
            }
        }
    }

    public static boolean lock(EboContext ctx, String lockID)
        throws boRuntimeException {
        return lock(ctx, lockID, LOCK_EXPIRE, WAIT_FOR_LOCK, RELEASE_ON_COMMIT);
    }

    public static boolean releaseLock(EboContext ctx, String lockID)
        throws boRuntimeException {
        Connection conn = null;
        CallableStatement stat = null;

        try {
            conn = ctx.getConnectionData();

            String lockHandler = (String) lockHandlers.get(getLockID(lockID));

            if (lockHandler != null) 
            {
                stat = conn.prepareCall(
                        "{ ? = call dbms_lock.release(?) }");
                stat.registerOutParameter(1, Types.NUMERIC);
                stat.setString(2, lockHandler);
                stat.execute();

                return stat.getLong(1) == 0;
            }
            return false;
        } catch (SQLException e) {
            throw new boRuntimeException(MessageLocalizer.getMessage("SQL_EXCEPTION_SETTING_NEW_LOCK"), "", e);
        } finally {
            try {
                if (stat != null) {
                    stat.close();
                }
            } catch (SQLException ex) {
                // Ignorar}
            }
        }
    }
}
