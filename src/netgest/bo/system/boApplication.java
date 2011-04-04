/*Enconding=UTF-8*/
package netgest.bo.system;

import java.io.File;
import java.net.URL;
import java.util.HashSet;
import java.util.WeakHashMap;

import javax.ejb.CreateException;
import javax.naming.NamingException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import netgest.bo.data.DriverManager;
import netgest.bo.data.IXEODataManager;
import netgest.bo.def.boDefHandler;
import netgest.bo.def.boDefInterface;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.ejb.impl.boManagerBean;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.preferences.PreferenceManager;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.ObjectListManager;
import netgest.bo.runtime.SecurityManager;
import netgest.bo.runtime.boContextFactory;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.utils.XeoApplicationLanguage;

/**
 *
 * Class netgest.bo.system.boApplication

 * startup()  -->  método para inicializar o Object Application assim como iniciar os agentes e monitorizalos.
 * lock()   -- Suspende todos os pedidos de criação do EboContext (  Faz o throw de um boRuntimeException de um código ainda a definir a dizer que a aplicação não está aceitar pedidos ou que está Locked )
 * unLock()
 * suspendAgents()  -- Suspende os agentes ( Suspende todos os agendamentos )
 * startAgents()  -- Activa todos os agendamentos
 * getSessions() --->  Retorna a class netgest.bo.system.boSessions
 * getMemoryArchive() --> Retorna um arquivo de memória pa
 * getClassLoader() --> retorna o classLoader da aplicação
 *
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boApplication
{
	
	/**
	 * Whether the application is in development mode or not
	 * (default is false)
	 */
	private boolean inDevelopment = false;
	
	private static Logger logger;
	
    private static boApplication XEO_APPLICATION;

    private static 	WeakHashMap     applicationThreadsContext = new WeakHashMap();
    
    public static final String XEO_WEBCLIENT 	= "XEO_WEBCLIENT";
    public static final String XEO_ROBOT     	= "XEO_ROBOT";
    public static final String XEO_SYSTEM    	= "XEO_SYSTEM";
    private static final String XEO_WORKPLACE   = "SYSTEM";
    
    /**
     *
     * @Company Enlace3
     * @since
     */
    
    private boMemoryArchive         p_memoryarchive;
    private boSessions              p_sessions;
    private boApplicationConfig     p_config;
    private IboAgentsController     p_boagentscontroller;
    private boolean                 p_isrunning;
    private boolean                 p_islocked = false;
    private EboContext              p_lockowner = null;
    
    private boCompilerClassLoader   p_compilingclassloader  = null;
    private boClassLoader           p_classLoader           = null;
    
    private PreferenceManager p_preferencesManager = null;
    
    
    private DriverManager p_drivermanager;
    private String p_name;
    
    
    private long p_workplace_boui=0;

    public HashSet<XeoApplicationLanguage> getAllApplicationLanguages(){
    	HashSet<XeoApplicationLanguage> conf = p_config.getAllLanguages();
    	return conf;
	}

    
    public boApplication( String name, boApplicationConfig config )
    {
        p_config = config;
        p_name   = name;
    }
    
    public synchronized static final boApplication getApplicationFromConfig(String configPath)
    {
        if (XEO_APPLICATION == null)
        {
            XEO_APPLICATION = new boApplication( "XEO", new boApplicationConfig( configPath ) );
            XEO_APPLICATION.initializeApplication();
        }
        return XEO_APPLICATION;
    }

    public static final boApplication getApplicationFromStaticContext(String appName)
    {
        if (appName.equals("XEO"))
        {
            if (XEO_APPLICATION == null)
            {
            	synchronized (Object.class) {
	                String appConfigPath = System.getProperty("netgest.home");
	                
	                if( appConfigPath == null ) {
	                	appConfigPath = System.getProperty("xeo.home");
	                }
	                if( appConfigPath == null ) {
	                	URL u = Thread.currentThread().getContextClassLoader().getResource( "xeo.home" );
	                	if( u != null && u.getFile() != null ) {
		                	File file = new File(u.getFile());
		                	File homeFolder = file.getParentFile().getParentFile().getParentFile().getParentFile();
		                	File xeoHome1 = new File( homeFolder + File.separator + "boconfig.xml" ); 
		                	if( xeoHome1.exists() ) {
		                		appConfigPath = homeFolder.getAbsolutePath();
		                	}
		                	homeFolder = homeFolder.getParentFile();
		                	xeoHome1 = new File( homeFolder + File.separator + "boconfig.xml" ); 
		                	if( xeoHome1.exists() ) {
		                		appConfigPath = homeFolder.getAbsolutePath();
		                	}
	                	}
	                }
	                if( appConfigPath != null ) {
		                if( !appConfigPath.endsWith( "/" ) && !appConfigPath.endsWith( "\\" ) )
		                {
		                    appConfigPath += File.separator;
		                }
		                System.setProperty( "xeo.home" , appConfigPath );
		                System.setProperty( "netgest.home" , appConfigPath );
		                
		                appConfigPath += "boconfig.xml"; 
		                
		                if( new File(appConfigPath).exists() ) {
		                	try {
				                XEO_APPLICATION = new boApplication( "XEO", new boApplicationConfig( appConfigPath ) );
				                XEO_APPLICATION.initializeApplication();
				                logger.config( "XEO Initialized from config file" +" [%s]", appConfigPath );
		                	}
		                	catch( Throwable e ) {
			                	System.err.println("-----------------------------------------------------------------------------------");
			                	System.err.println("Failed to Initialize XEO. Error occurred loading configuration!");
			                	e.printStackTrace();
			                	System.err.println("-----------------------------------------------------------------------------------");
		                	}
		                }
		                else {
		                	System.err.println("-----------------------------------------------------------------------------------");
		                	System.err.println("Failed to Initialize XEO. boconfig.xml not found at"+" [" + appConfigPath + "]!");
		                	System.err.println("-----------------------------------------------------------------------------------");
		                }
	                }
	                else {
	                	System.err.println("-----------------------------------------------------------------------------------");
	                	System.err.println("Failed to Initialize XEO. boconfig.xml not found!");
	                	System.err.println("System property xeo.home not specified or WEB-INF/classes/xeo.home file is missing!");
	                	System.err.println("-----------------------------------------------------------------------------------");
	                }
	            }
	        }
            return XEO_APPLICATION;
    	}
        return null;
    }
/**
 * 
 * @return(String) 
 * used language from the boconfig.xml
 */
     public String getApplicationLanguage(){
    	 String language;  	
    		 language=p_config.getLanguage();   	 
    	return language;   	
    }
     /**
      * 
      * @return(HashSet)all Languages
      */
     
    public HashSet<String> getAllLanguages(){
    	HashSet<String> p_languages= p_config.getAvailableLanguages(); 
    	return p_languages;
    }
    
    
    
    
    /**
	 * 
	 * Retrieves the default application (same as invoking
	 * {@link boApplication#getApplicationFromStaticContext(String)} with "XEO"
	 * as a parameter
	 * 
	 * @return The default {@link boApplication}
	 */
    public static boApplication getDefaultApplication(){
    	return getApplicationFromStaticContext("XEO");
    }
    
    
    public void initializeApplication()
    {
    	configureLoggers();
        p_memoryarchive = new boMemoryArchive(this);
        p_sessions = new boSessions(this);
        p_drivermanager = new netgest.bo.data.DriverManager( this );
        suspendAgents();
        startAgents();
        p_preferencesManager = new PreferenceManager();
    }
    
    public void configureLoggers() {
    	boApplicationLoggerConfig.applyConfig( getApplicationConfig().getLoggersConfig() );
    	logger = Logger.getLogger( boApplication.class );
    }

    public void addAContextToThread( )
    {
        addContextToThread( Thread.currentThread() ); 
    }
    
    public void addContextToThread( Object thread )
    {
        Object context = applicationThreadsContext.get( thread );
        if( context == null )
        {
            applicationThreadsContext.put( thread, new boContext( this ) );
        }
    }
    
    public void removeContextFromThread( )
    {
        removeContextFromThread( Thread.currentThread() );
    }
    
    public void removeContextFromThread( Object thread )
    {
        Object context = applicationThreadsContext.get( thread );
        if( context != null )
        {
            applicationThreadsContext.remove( thread );
        }
    }
    
    public PreferenceManager getPreferencesManager() {
    	return p_preferencesManager;
    }

    public synchronized static boContext currentContext()
    {
        boContext context = ((boContext)applicationThreadsContext.get( Thread.currentThread() ));
        if( context == null )
        {
            getApplicationFromStaticContext("XEO").addContextToThread( Thread.currentThread() );;
            context = ((boContext)applicationThreadsContext.get( Thread.currentThread() ));
        }
        return context;
    }

    public boApplicationConfig getApplicationConfig()
    {
        return p_config;
    }
    
    public boObject getApplicationWorkplace(EboContext ectx) throws boRuntimeException
    {
        if(p_workplace_boui==0)
        {
          try 
          {
            p_workplace_boui = boObject.getBoManager().loadObject(ectx, "SELECT Sch_workplace WHERE name = '"+XEO_WORKPLACE+"'").getBoui();  
          } catch (Exception ex) 
          {
            p_workplace_boui = 0;
          }
        }
        if(p_workplace_boui==0)
          return null;
        else
          return boObject.getBoManager().loadObject(ectx, p_workplace_boui);
    }

    public void startup()
    {
        if (!p_isrunning)
        {
            initializeApplication();
        }
        
    }

    public synchronized void lock(EboContext ctx)
    {
        p_islocked = true;
        p_lockowner = ctx;
    }

    public synchronized void unlock(EboContext ctx)
    {
        if (p_islocked)
        {
            if (ctx == p_lockowner)
            {
                p_islocked = false;
                p_lockowner = null;
            }
        }
    }

    public synchronized void startAgents()
    {
        if( p_boagentscontroller != null ) 
        {
            suspendAgents();
        }
        if (this.getApplicationConfig().getThreadsType().equalsIgnoreCase("userThreads"))
        {
          p_boagentscontroller = new boAgentsControler( this );
          p_boagentscontroller.start();  // Em vez de run tem que set start ( Para iniciar a Thread ) 
        }
        else
        {
          p_boagentscontroller = new boAgentsControllerEjbTimer(this);
          p_boagentscontroller.start();
        }
    }

    public synchronized void suspendAgents()
    {
        if( p_boagentscontroller != null )
        {
            if ( p_boagentscontroller.isAlive() )
            {
              if (this.getApplicationConfig().getThreadsType().equalsIgnoreCase("userThreads"))
              {
                try
                {
                    p_boagentscontroller.interrupt();
                    p_boagentscontroller.join();                    
                }
                catch (Exception e)
                {
                    
                }
              }
                p_boagentscontroller.suspendAgents();
            }
            p_boagentscontroller = null;
        }
    }


    public boSessions getSessions()
    {
        return p_sessions;
    }

    public boMemoryArchive getMemoryArchive()
    {
        return p_memoryarchive;
    }

    public boClassLoader getClassLoader()
    {
        if ( p_classLoader == null )
        {
            p_classLoader = new boClassLoader( Thread.currentThread().getContextClassLoader() ,this );
        }
        return p_classLoader;        
    }

    public void releaseClassLoader()
    {
        this.p_classLoader = null;
    }
    
    
    public boCompilerClassLoader getCompilingClassLoader()
    {
        if ( p_compilingclassloader == null )
        {
            p_compilingclassloader = new boCompilerClassLoader( Thread.currentThread().getContextClassLoader() ,this );
        }
        return p_compilingclassloader;
    }

    public boSession boLogin(String username, String password, String repository)
        throws boLoginException
    {
        return boLogin(username, password, repository, null);
    }

    public String getDefaultRepositoryName()
    {
//        return "claim_2110214";
        return "default";
    }

    public boSession boLogin(String username, String password)
        throws boLoginException
    {
        return boLogin(username, password, getDefaultRepositoryName(), null);
    }

    public boSession boLogin(String username, String password, String repository,HttpServletRequest request)
        throws boLoginException
    {
        return boLogin(username, password, repository, boApplication.XEO_WEBCLIENT , request);
    }
    public boSession boLogin(String username, long time, long timeCheck, String repository, HttpServletRequest request)
        throws boLoginException
    {
        return boLogin(username, time, timeCheck, repository, boApplication.XEO_WEBCLIENT , request);
    }

    public boSession boLogin(String username, String password, String repository, String clientName , HttpServletRequest request)
        throws boLoginException
    {
//        try
//        {
            if( repository == null )
            {
                repository = "default";
            } 
            //boLoginLocal login = (boLoginLocal) ((boLoginLocalHome) boContextFactory.getContext().lookup("java:comp/env/ejb/boLoginLocal")).create();
            boLoginBean login = new boLoginBean();
            return login.boLogin( this, repository, clientName, username, password, request);
//        }
//        catch (NamingException e)
//        {
//            throw new RuntimeException(e.getMessage());
//        }
//        catch (CreateException e)
//        {
//            throw new RuntimeException(e.getMessage());
//        }
    }
    public boSession boLogin(String username, long time, long timeCheck, String repository, String clientName , HttpServletRequest request)
        throws boLoginException
    {
        try
        {
            if( repository == null )
            {
                repository = "default";
            } 
            boLoginLocal login = (boLoginLocal) ((boLoginLocalHome) boContextFactory.getContext().lookup("java:comp/env/ejb/boLoginLocal")).create();
            return login.boLogin( this, repository, clientName, username, time, timeCheck, request);
        }
        catch (NamingException e)
        {
            throw new RuntimeException(e.getMessage());
        }
        catch (CreateException e)
        {
            throw new RuntimeException(e.getMessage());
        }
    }
    public DriverManager getDriverManager()
    {
        return p_drivermanager;
    }
    
    public String getName()
    {
        return p_name;
    }
    
    public IboAgentsController getBoAgentsController()
    {
        return p_boagentscontroller;        
    }
    
    public IXEODataManager getXEODataManager( String objectName ) {
    	IXEODataManager dataManager = getMemoryArchive().getXEODataManager( objectName );
    	if( dataManager == null ) {
    		getXEODataManager( boDefHandler.getBoDefinition( objectName )  );
    	}
    	return dataManager;
    }
    
    /**
     * 
     * Retrieves whether or not the application is is development mode
     * 
     * @return True if the application is in development mode
     * and false otherwise
     * 
     */
    public boolean inDevelopmentMode(){
    	return inDevelopment;
    }
    
    /**
     * 
     * Sets the development mode to active or false
     * 
     * @param dev The development mode (true/false)
     */
    public void setDevelopmentMode(boolean dev){
    	inDevelopment = dev;
    }
    
    public IXEODataManager getXEODataManager( boDefHandler def ) {
    	IXEODataManager dataManager = getMemoryArchive().getXEODataManager( def.getName() );
    	if( dataManager == null ) {
			String className = def.getDataBaseManagerClassName();
			try {
				dataManager = (IXEODataManager) Class.forName( className ).newInstance();
			} catch (InstantiationException e) {
				throw new RuntimeException( e );
			} catch (IllegalAccessException e) {
				throw new RuntimeException( e );
			} catch (ClassNotFoundException e) {
				throw new RuntimeException( e );
			}
			getMemoryArchive().putXEODataManager( def.getName(), dataManager );
    	}
    	return dataManager;
    }
    
    /**
	 * 
	 * Retrieves a wrapper for the Object Model XML definition for a given Model
	 * name
	 * 
	 * @param modelName
	 *            The name of the Object Model
	 * 
	 * @return A handler to the Model Definition or null if it does not exist
	 */
	public boDefHandler getModelDefinition(String modelName) {
		return boDefHandler.getBoDefinition(modelName);
	}

	/**
	 * 
	 * Retrieves a wrapper for the Interface Object Model XML definition given
	 * the name of the interface
	 * 
	 * @param interfaceName
	 *            The name of the interface
	 * 
	 * @return A handler to the interface definition or null if it does not
	 *         exist
	 */
	public boDefInterface getInterfaceDefinition(String interfaceName) {
		return boDefHandler.getInterfaceDefinition(interfaceName);
	}

	/**
	 * 
	 * Retrieves an implementation of {@link boManagerLocal} which is
	 * responsible for managing operation with instances of boObjects, such as
	 * creating and loading instances
	 * 
	 * @return A {@link boManagerBean} which is capable of creating an loading
	 *         boObject instances
	 * 
	 * @throws boRuntimeException
	 *             If the manager cannot be retrieved
	 */
	public boManagerLocal getObjectManager() throws boRuntimeException {
		return boObject.getBoManager();
	}
	
	public boManagerLocal getSecureObjectManager() throws boRuntimeException{
			return boObject.getBoSecurityManager();
	}

	/**
	 * 
	 * Retrieves an instance of the Lov Manager (to handle operations with lovs)
	 * 
	 * @return A Lov Manager to handle operations with Lists of Values
	 */
	public LovManager getLovManager() {
		return new LovManager();
	}

	/**
	 * 
	 * Retrieves an instance of the {@link ObjectListManager} which allows to
	 * perform BOQL (XEOQL) queries and return paginated lists of
	 * {@link boObject}
	 * 
	 * @return An Object List Manager instance
	 */
	public ObjectListManager getObjectListManager() {
		return new ObjectListManager();
	}

	/**
	 * 
	 * Retrieves an instance of the SecurityManager which allows to query if a
	 * given Object Model / Object Model instance has a set privileges to
	 * perform a certain action
	 * 
	 * @return A security manager instance
	 */
	public SecurityManager getSecurityManager() {
		return new SecurityManager();
		
	}

	/**
	 * 
	 * Creates an {@link EboContext} instance from a session
	 * 
	 * @param session The user session
	 * @return An {@link EboContext} instance
	 * 
	 * @throws boRuntimeException
	 */
	public EboContext createContext(boSession session)
			throws boRuntimeException {
		return new EboContext(session, (HttpServletRequest) null,
				(HttpServletResponse) null, (ServletContext) null);
	}
}
