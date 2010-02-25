/*Enconding=UTF-8*/
package netgest.bo.runtime;


import java.sql.Connection;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;

import java.util.Vector;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;
import javax.servlet.http.*;
import javax.servlet.jsp.PageContext;

import javax.transaction.Status;
import javax.transaction.UserTransaction;
import netgest.bo.controller.Controller;
import netgest.bo.data.Driver;
import netgest.bo.preferences.PreferenceManager;
import netgest.bo.system.boApplication;
import netgest.bo.system.boConnectionManager;
import netgest.bo.system.boPoolable;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;


public class EboContext extends boPoolable implements Cloneable
{
	
	public static final int MODE_BATCH_ACTIVE = 2;
	public static final int MODE_BATCH_VALIDATE_OBJECTS = 4;
	public static final int MODE_BATCH_EXECUTE_BEFORESAVE_EVENT = 8;
	public static final int MODE_BATCH_EXECUTE_AFTERSAVE_EVENT = 16;
	public static final int MODE_BATCH_EXECUTE_BEFORELOAD_EVENT = 32;
	public static final int MODE_BATCH_EXECUTE_AFTERLOAD_EVENT = 64;
	public static final int MODE_BATCH_EXECUTE_ATTRIBUTES_EVENT = 128;
	
	public static final int MODE_BATCH_USE_OBJECT_SAVEPOINT 	= 256;
	
    //    private PerformerWorkSpace p_performerworkspace;
    private HttpServletResponse p_response;
    
    private PageContext p_pagecontext;
    private ServletContext p_servletcontext;
    
    private HttpServletRequest p_request;
    private String p_preferredowner;
    private boSession p_bosession;
    private String p_url;
    private boConnectionManager p_connman;
    private boApplication p_app;
    public ArrayList ObjectsInContext = new ArrayList();
    private boThread p_thread;
    private boolean isInTransaction = false;
    private  Vector p_poolowners = new Vector(1);
    private Hashtable p_transactedobjects = new Hashtable();
    private boolean forceAllInTransaction = false;
    

    // Guarda todos os Objectos trsaccionados numa transacção de apenas base de dados
    private Hashtable p_dbtransactedobjects;

    // O controlador que está a ser usado neste request
    private Controller controller = null;

    private int		   modeBatchOptions = 0;


    //Espia as ligações permitindo o controlo dos preparedStatement, statements, resultSet
    // e o do tempo gasto pelas querys
    //true ligado; false desligado

    
    public void setModeBatch() {
    	setModeBatch( MODE_BATCH_ACTIVE );
    }
    
    public void setModeBatch( int options ) {
    	if( (options & MODE_BATCH_ACTIVE) == 0 )
    		options += MODE_BATCH_ACTIVE;
    	this.modeBatchOptions = options;
    }
    
    public boolean isInModeBatch() {
    	return this.modeBatchOptions > 0;
    }
    
    public boolean isInModeBatch( int option ) {
    	if( this.modeBatchOptions > 0 ) {
    		return (this.modeBatchOptions & option) == option;
    	}
    	return true;
    }
    
    public boolean isInTransaction() {
    	return this.isInTransaction;
    }
    
    public void setModeBatchOff() {
    	this.modeBatchOptions = 0;
    }
    
    public EboContext(boSession session, HttpServletRequest request,
            HttpServletResponse response, ServletContext servletContext )
        {
    		this(session,request,response, (PageContext)null);
    		
    		this.p_servletcontext = servletContext;
    		
        }
    public EboContext(boSession session, HttpServletRequest request,
        HttpServletResponse response, PageContext pagecontext)
    {
        p_request = request;
        p_response = response;
        p_pagecontext = pagecontext;
        p_app = session.getApplication();

        if (request != null)
        {
            p_url = request.getRequestURL().substring(0,
                    request.getRequestURL().lastIndexOf("/"));
                                p_url = request.getRequestURL().substring(0,
                    request.getRequestURL().lastIndexOf("/"));

                    Cookie[] mycookies = request.getCookies();
        String XeoWin32Client_address = null;
        for (int i = 0;mycookies != null && i < mycookies.length ; i++)
        {
            if ( mycookies[i].getName().equals("XeoWin32Client_address") )
            {
                if ( !mycookies[i].getValue().equals("null") )
                {
                    XeoWin32Client_address = mycookies[i].getValue();
                    request.getSession().setAttribute("XeoWin32Client_address", XeoWin32Client_address );
                }

                break;
            }
        }



        }

        p_bosession = session;
        p_connman = new boConnectionManager(this);
        p_preferredowner = this.poolUniqueId();
        p_poolowners.add(p_preferredowner);
    }

    public final String getApplicationUrl()
    {
        return p_url;
    }
    public String getXeoWin32Client_adress()
    {
        return (String)p_request.getSession().getAttribute("XeoWin32Client_address");
    }

    /* JSP Viewer Specific Methods a and properties */
    public final HttpServletRequest getRequest()
    {
        return p_request;
    }

    public final HttpServletResponse getResponse()
    {
        return p_response;
    }

    public final PageContext getPageContext()
    {
        return p_pagecontext;
    }

    public final ServletContext getServletContext()
    {
        return p_servletcontext;
    }

    public final boSession getBoSession()
    {
        return p_bosession;
    }

    public boThread getThread()
    {
        if (p_thread == null)
        {
            p_thread = new boThread();
        }

        return p_thread;
    }

    public final void close()
    {
        p_app.getMemoryArchive().getPoolManager().realeaseAllObjects(this.poolUniqueId());
        p_connman.close();
    }

    public final void releaseAllObjects()
    {
        p_app.getMemoryArchive().getPoolManager().realeaseAllObjects(this.poolUniqueId());
        p_app.getMemoryArchive().getPoolManager().realeaseAllObjects( this.getPreferredPoolObjectOwner() );
    	getThread().clear();
    }

    public final void poolObjectActivate()
    {
        // TODO:  Implement this netgest.bo.system.boPoolable abstract method
    }

    public final void poolObjectPassivate()
    {
        // TODO:  Implement this netgest.bo.system.boPoolable abstract method
    }

    public boSessionUser getSysUser()
    {
        return p_bosession.getUser();
    }

    public Connection getConnectionData()
    {
        return p_connman.getConnection();
    }

    public Driver getDataBaseDriver()
    {
        return getBoSession().getRepository().getDriver();
    }

    public Connection getConnectionDef()
    {
        return p_connman.getConnectionDef();
    }

    public Connection getConnectionSystem()
    {
        return p_connman.getConnectionSystem();
    }

    public Connection getDedicatedConnectionData()
    {
        return p_bosession.getRepository().getDedicatedConnectionDef();
    }

    public Connection getDedicatedConnectionDef()
    {
        return p_bosession.getRepository().getDedicatedConnectionDef();
    }

    public void beginContainerTransaction() throws boRuntimeException
    {
        p_connman.beginContainerTransaction();
    }

    public void commitContainerTransaction() throws boRuntimeException
    {
        p_connman.commitContainterTransaction();
    }

    public void rollbackContainerTransaction() throws boRuntimeException
    {
        p_connman.rollbackContainerTransaction();
    }

    public boApplication getApplication()
    {
        return p_app;
    }
    
    public PreferenceManager getPreferencesManager() {
    	return p_app.getPreferencesManager();
    }

    public boConnectionManager getConnectionManager()
    {
        return p_connman;
    }

    public final void setBoSession(boSession newsession)
    {
        p_bosession = newsession;
    }

    public final void setApplication(boApplication newapplication)
    {
        p_app = newapplication;
    }

    public void addShareOwner(String owner)
    {
        if (p_poolowners.indexOf(owner) == -1)
        {
            p_poolowners.add(owner);
        }
    }

    public void removeShareOwner(String owner)
    {
        p_poolowners.remove(owner);
    }

    public Vector getSharedOwners()
    {
        return p_poolowners;
    }

    public String setPreferredPoolObjectOwner(String owner)
    {
        p_poolowners.remove(p_preferredowner);
        p_poolowners.add(owner);

        String toret = p_preferredowner;
        p_preferredowner = owner;

        return toret;
    }

    public String getPreferredPoolObjectOwner()
    {
        return p_preferredowner;
    }

    public void beginTransaction()
    {
        p_transactedobjects = new Hashtable();
        p_connman.beginTransaction();
        isInTransaction = true;
    }

    public  boObject[] getOnlyDbTransactedObject()
    {
        boObject[] objects;
        if( p_dbtransactedobjects != null )
        {
            objects = new boObject[p_dbtransactedobjects.size()];
            Enumeration oEnum = p_dbtransactedobjects.elements();
            int i = 0;

            while (oEnum.hasMoreElements())
            {
                objects[i++] = (boObject) oEnum.nextElement();
            }
        }
        else
        {
            objects = new boObject[0];
        }
        return objects;
    }


    public void addTransactedObject(boObject object) throws boRuntimeException
    {
        Long xoboui = new Long(object.getBoui());
        if( this.p_connman.isInOnlyDatabaseTransaction() )
        {
	            if ( p_dbtransactedobjects == null ) 
	            	p_dbtransactedobjects = new Hashtable();
	            
            p_dbtransactedobjects.put( xoboui, object );

        }
	        
        if (p_transactedobjects.get(xoboui) == null)
        {
            object.transactionBegins();
            p_transactedobjects.put(object, object);
        }
    }

    public boObject[] getObjectsInTransaction()
    {
        boObject[] objects;
        objects = (boObject[])p_transactedobjects.keySet().toArray( new boObject[p_transactedobjects.size()] );
        return objects;
    }


    public void endTransaction()
    {
        isInTransaction = false;
        p_connman.endTransaction();
    }
    
    public void clearObjectInTransaction() {
	    if( p_transactedobjects != null )
	    	p_transactedobjects.clear();
	    if( p_dbtransactedobjects != null ) 
	    	p_transactedobjects.clear();
    }

    public boolean objectIsInTransaction(boObject object)
    {
        return p_transactedobjects.get(new Long(object.getBoui())) != null;
    }

    public void forceAllInTransaction(boolean value)
    {
        this.forceAllInTransaction = value;
    }

    public boolean getForceAllInTransaction()
    {
        return forceAllInTransaction;
    }
    public Controller getController()
    {
        return controller;
    }
    public void setController(Controller controller)
    {
        this.controller = controller;
    }
    
    public void releaseObjects()
    {
        p_app.getMemoryArchive().getPoolManager().realeaseObjects(this.poolUniqueId(),this);
    }
    
    
    
}
