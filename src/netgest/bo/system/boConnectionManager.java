/*Enconding=UTF-8*/
package netgest.bo.system;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import netgest.bo.data.Driver;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;


/**
 *
 * @Company Enlace3
 * @author JoÃ£o Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boConnectionManager
{
    /**
     *
     * @Company Enlace3
     * @since
     */

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.boConnectionManager");

     private static final Byte DATA_CONNECTION = new Byte((byte)0);
     private static final Byte DEF_CONNECTION = new Byte((byte)1);
     private static final Byte SYS_CONNECTION = new Byte((byte)3);

    private boolean p_isInTransaction    = false;

    private boolean p_isInContainerTrans = false;

    private Hashtable p_connections;
    private Hashtable p_Tconnections;

    private boSession p_bosession;
    private EboContext p_ctx;

    private Driver p_sysdriver;
    
    private Throwable transactionStack;

    public static final boolean toSpy = false;
//    public int callTimes = 0;

    // Flag que indica que a transacÃ§Ã£o Ã© apenas ao nÃ­vel da Base de Dados.
    // Todas as alteraÃ§Ãµes feitas em objectos que estÃ£o em memÃ³ria nÃ£o feito rollbak em caso de rollback
    // na base de dados
    private boolean p_onlyDataBaseTransaction;


    public boConnectionManager( EboContext ctx )
    {
        p_ctx       = ctx;
        p_bosession = ctx.getBoSession();
        p_sysdriver = p_bosession.getApplication().getDriverManager().getDriver("SYS");

    }
    
    public final Connection getDedicatedConnection()
    {

        return p_bosession.getRepository().getDriver().getDedicatedConnection();

    }
    
    public final Connection getSystemDedicatedConnection()
    {
        return p_sysdriver.getDedicatedConnection();
    }
    
    
    public final Connection getConnection()
    {
        return getConnection( DATA_CONNECTION );
    }
    public final Connection getConnectionDef()
    {
        return getConnection( DEF_CONNECTION );
    }

    private final Connection getConnection( Byte connectionType  )
    {
        if (p_connections == null)
        {
            p_connections = new Hashtable();
        }

        if ( (p_isInTransaction||p_onlyDataBaseTransaction) && (p_Tconnections == null))
        {
            p_Tconnections = new Hashtable();
        }

        Hashtable htcn = (p_isInTransaction||p_onlyDataBaseTransaction) ? p_Tconnections : p_connections;
        Connection retcn = (Connection) htcn.get( connectionType );

        try
        {
            if( retcn == null || retcn.isClosed() )
            {
                switch ( connectionType.byteValue() )
                {
                    case 0:
                        retcn = p_bosession.getRepository().getConnection();
                        break;
                    case 1:
                        retcn = p_bosession.getRepository().getConnectionDef();
                        break;
                    case 3:
                        retcn = p_sysdriver.getConnection();
                        break;
                }
                htcn.put( connectionType, retcn );
            }
            if( p_isInTransaction || p_onlyDataBaseTransaction )
            {
            // IMBR
            /*
                try
                {
                    retcn.setAutoCommit( false );
                }
                catch( Exception e )
                {
                    logger.warn("Invalid connection in cache.. closing and getting a new one.," + e.getMessage() );
                    retcn.close();
                    htcn.remove( connectionType );
                    return getConnection( connectionType );

                }
            */
            }
        }
        catch (SQLException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("ERROR_RETRIEVING_OR_CLOSING_A_CONNECTION_FROM_DRIVER")+".\n" + e.getMessage());
        }
       
        //PostGre
        if (!p_bosession.getRepository().getDefDriver().validateConnection(retcn))
			try {
				retcn.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        return retcn;
    }

    public void close()
    {
        this.closeTrans();

        if (p_connections != null)
        {
            Enumeration oEnum = p_connections.elements();

            while (oEnum.hasMoreElements())
            {
                try
                {
                    ((Connection) oEnum.nextElement()).close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            try {
    			if (transactionStack != null && isContainerTransactionActive()) {
    				logger.warn( "Transaction not commited/rollbacked!!!!!! The transaction was created on: ", transactionStack  );
//    				rollbackContainerTransaction();
    			}
    		} catch (boRuntimeException e) {
    			logger.warn("Error forcing rollback", e);
    		}
        }
        p_connections = null;
    }

    public final void closeTrans()
    {
        if (p_Tconnections != null)
        {
            Enumeration oEnum = p_Tconnections.elements();

            while (oEnum.hasMoreElements())
            {
                try
                {
                    ((Connection) oEnum.nextElement()).close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

        p_Tconnections = null;
    }

    public final void beginTransaction()
    {
        if (!p_isInTransaction)
        {
            p_isInTransaction = true;
        }
    }

    public final void endTransaction()
    {
        if (p_isInTransaction)
        {
            this.closeTrans();
            p_isInTransaction = false;
        }
    }

    public final void beginContainerTransaction() throws boRuntimeException
    {
        if ( !p_isInContainerTrans )
        {
//            callTimes = 0;
            try
            {
                final InitialContext ic = new InitialContext();
                UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                if (ut.getStatus() == Status.STATUS_NO_TRANSACTION)
                {
                	transactionStack = new Throwable();
                	ut.begin();
                    p_isInContainerTrans = true;
                }
                p_ctx.beginTransaction();
            }
            catch (Exception e)
            {
                throw new boRuntimeException("EboContext.beginContainerTransaction( EboContext )", "BO-3150", e, MessageLocalizer.getMessage("COMMITING"));
            }
            this.beginTransaction();
        }
    }

    public final boolean isContainerTransactionActive() throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            return ut.getStatus() == Status.STATUS_ACTIVE;
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e);
        }
        catch (SystemException e)
        {
            throw new RuntimeException(e);
        }
    }

    public final void setContainerTransactionForRollback() throws boRuntimeException
    {
            try
            {
                final InitialContext ic = new InitialContext();
                UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                ut.setRollbackOnly();
            }
            catch (Exception e)
            {
                throw new boRuntimeException("EboContext.isContainerTransactionActive( EboContext )", "BO-3150", e, MessageLocalizer.getMessage("COMMITING"));
            }
    }


    public final boolean isOkToCommit() throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
            return ut.getStatus() == Status.STATUS_ACTIVE;
        }
        catch (Exception e)
        {
            p_isInContainerTrans = false;
            throw new boRuntimeException("boManagerBean.commitTransaction( EboContext )", "BO-3150", e, MessageLocalizer.getMessage("COMMITING"));
        }
    }

    public final void commitContainterTransaction() throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");

            if (p_isInContainerTrans || ut.getStatus() == Status.STATUS_ACTIVE )
            {
                if (ut.getStatus() == Status.STATUS_MARKED_ROLLBACK)
                {
                    rollbackContainerTransaction();
                    throw new boRuntimeException("boManagerBean.commitTransaction( EboContext )", "BO-3150", null, "( "+MessageLocalizer.getMessage("TRANSACTION_MARKED_FOR_ROLLBACK")+" )");
                }
                else if (ut.getStatus() == Status.STATUS_ACTIVE)
                {
                    try
                    {
                        boObject[] objects = p_ctx.getObjectsInTransaction();
                        
                        // Remove references to objects that doesn't belong to
                        // the current transaction, maybe added in a previous commit
                        // rollback
                        ArrayList<boObject> objectsList = new ArrayList<boObject>();
                        for( boObject o : objects ) {
                        	if( o != null && o.getEboContext() != null && o.isChanged()  ) {
                        		objectsList.add( o );
                        	}
                        }
                        boTextIndexAgentBussinessLogic.addToQueue( objectsList.toArray( new boObject[ objectsList.size() ] )   );
                        p_ctx.endTransaction();

                        for (int i = 0; i < objects.length; i++)
                        {
                            objects[i].onCommit();
                            objects[i].transactionEnds(true);
                        }
                        ut.commit();
                    }
                    catch( Throwable e )
                    {
                        rollbackContainerTransaction();
                        throw new RuntimeException(e);
                    }
                }
                else
                {
                    rollbackContainerTransaction();
                    throw new RuntimeException("XEO:"+MessageLocalizer.getMessage("CANNOT_COMMIT_TRANSACTION_NOT_ACTIVE_CURRENT_STATE_IS")+" ["+ut.getStatus()+"]");
                }
                p_isInContainerTrans = false;
            }
        }
        catch (Exception e)
        {
            p_isInContainerTrans = false;
            throw new boRuntimeException("boManagerBean.commitTransaction( EboContext )", "BO-3150", e, MessageLocalizer.getMessage("COMMITING"));
        }
    }
    

    private static ThreadLocal autoMarkForRollback = new ThreadLocal() 
    {
        protected synchronized Object initialValue() 
        {
            return Boolean.TRUE;
        }
    };

    public final void setAutoMarkForRollback( boolean autoMark )
    {
        autoMarkForRollback.set( new Boolean( autoMark ) );
    }

    public final boolean getAutoMarkForRollback()
    {
        return ((Boolean)autoMarkForRollback.get()).booleanValue();
    }

    public final void rollbackContainerTransaction() throws boRuntimeException
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");

            if (p_isInContainerTrans)
            {
                try
                {
                    boObject[] objects = p_ctx.getObjectsInTransaction();
                    p_ctx.endTransaction();
                    for (short i = 0; i < objects.length; i++)
                    {
                        objects[i].transactionEnds(false);
                        objects[i].onRollBack();
                    }
                    ut.rollback();
                    p_isInContainerTrans = false;
                }
                catch(Exception e)
                {
                    logger.finer("boConnectionManager.rollbackContainerTransaction( EboContext )", e);
                }
            }
            else
            {
                try
                {
                    if ( ut.getStatus() == Status.STATUS_ACTIVE )
                    {
                        ut.setRollbackOnly();
                    }
                }
                catch(Exception e)
                {
                    logger.finer("boConnectionManager.rollbackContainerTransaction( EboContext )", e);
                }
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException("boConnectionManager.rollBackTransaction( EboContext )", "BO-3150", e,MessageLocalizer.getMessage("ROLLING_BACK"));
        }
    }

    public Connection getConnectionSystem()
    {
        return getConnection( SYS_CONNECTION );
    }

    public void beginOnlyDatabaseTransaction()
    {
        try
        {
            final InitialContext ic = new InitialContext();
            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");

            this.closeTrans();
            ut.begin();

            p_onlyDataBaseTransaction = true;
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
    }

    public boolean commitOnlyDatabaseTransaction( )
    {
        boolean ret = false;
        try
        {
            final InitialContext ic = new InitialContext();

            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");

            p_onlyDataBaseTransaction = false;
            this.closeTrans();
            if( ut.getStatus() == Status.STATUS_MARKED_ROLLBACK )
            {
                rollbackOnlyDatabaseTransaction();
            }
            else
            {

                ut.commit();
                ret = true;
                boObject[] tobjs = p_ctx.getOnlyDbTransactedObject();
                
                ArrayList<boObject> objectsList = new ArrayList<boObject>();
                for( boObject o : tobjs ) {
                	if(o.isChanged()) {
                		objectsList.add( o );
                	}
                }                      
                boTextIndexAgentBussinessLogic.addToQueue(objectsList.toArray(new boObject[objectsList.size()]));

            }
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
        return ret;
    }

    public boolean isInOnlyDatabaseTransaction( )
    {
        return p_onlyDataBaseTransaction;
    }


    public void rollbackOnlyDatabaseTransaction( )
    {
        try
        {
            final InitialContext ic = new InitialContext();

            UserTransaction ut = (UserTransaction) ic.lookup(
                    "java:comp/UserTransaction");

            p_onlyDataBaseTransaction = false;
            this.closeTrans();
            ut.rollback();
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }

    }
}
