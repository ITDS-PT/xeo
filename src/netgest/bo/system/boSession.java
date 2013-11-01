/*Enconding=UTF-8*/
package netgest.bo.system;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.system.locale.LocaleFormatter;
import netgest.bo.system.locale.LocaleSettings;
import netgest.bo.system.locale.LocaleSettings.Type;
import netgest.bo.system.locale.Localization;
import netgest.bo.system.locale.XEOLocaleProvider;
import netgest.bo.system.login.LocalePreferenceSerialization;
import netgest.bo.system.login.LoginUtil;
import netgest.utils.StringUtils;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.jcr.Session;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;
public class boSession implements Serializable {

    private static Logger logger = Logger.getLogger("netgest.bo.system.boSession");

    private boRepository        p_repos;
    private String              p_clientname;
    private boApplication       p_app;
    private boSessionUser       p_user;
    
    private long                p_createdon;
    private long                p_lastactivity;
    private String              p_sessionid;
    
    private String              p_remoteAddr;
    private String              p_remoteHost;
    private String              p_remoteUser;
    private String              p_remoteSessionId;
    
    private Properties          p_properties;
    private long                p_iprofileBoui;
    private String              p_iprofileName;
    
    private Hashtable           p_objects;
    
    private static ThreadLocal<Locale> threadLocale = new ThreadLocal<Locale>();
    
    //
    private String p_language=XEO.getCurrentLocale().toString();
    
    /**
     * A map of sessions with each repository
     */
    private HashMap<String,Session> p_ecmRepositories;

	/**
	 * The locale associated with the current session
	 */
	private Locale locale;
	
	private TimeZone timeZone;
    
    protected boSession( String id, boSessionUser user, String rep , String clientName, boApplication app, 
        String remoteAddr, String remoteHost, String remoteUser, String remoteSessionId  )
    {
        p_user          = user;
        p_clientname    = clientName;
        p_app = app;
        p_repos     = boRepository.getRepository( app, rep );
        p_clientname = clientName; 
        p_sessionid = id;
        p_objects = new Hashtable();
        
        p_remoteAddr = remoteAddr;
        p_remoteHost = remoteHost;
        p_remoteUser = remoteUser;
        p_remoteSessionId = remoteSessionId;
        p_ecmRepositories = new HashMap<String, Session>();
        init();
    }
    public Long getPerformerBouiLong()
    {
        return new Long(getPerformerBoui());
    }
  
	
	
	public String getDefaultLanguage(){	
    	return p_language;
    	
    }
    public void init()
    {
        p_createdon = System.currentTimeMillis();
        p_lastactivity = p_createdon; 
        this.locale = this.p_user.getLocale();
        this.timeZone = this.p_user.getTimeZone();
    }
    
    /**
     * 
     * Creates a new EboContext without associated request, response or pagecontext
     * 
     * @return a new ebo context ( don't forget to close the context when it's no longer needed )
     */
    public EboContext createEboContext(){
    	return createRequestContext( null , null , null );
    }
    
    public EboContext createRequestContext( HttpServletRequest request, HttpServletResponse response, PageContext pagecontext ) 
    {
        p_lastactivity = System.currentTimeMillis();
        EboContext ctx = new EboContext( this , request , response ,pagecontext );
        return ctx;
    }

    public EboContext createRequestContextInServlet( HttpServletRequest request, HttpServletResponse response, ServletContext servletContext ) 
    {
        p_lastactivity = System.currentTimeMillis();
        EboContext ctx = new EboContext( this , request , response , servletContext );
        return ctx;
    }
    
    public long getPerformerBoui() 
    {
        return p_user.boui;
    }
    
    /**
     * Loads the user locale settings
     * 
     * This method was placed here because it loads a preference (if a preference 
     * is loaded during login it would cause  an infinite loop because preferences also make
     * a login if one does not exist as is the case when you're doing the first login
     */
    public void loadUserLocaleSettings() {
    	String username = p_user.getUserName();
    	String oldLanguage = p_user.getLanguage();
    	String language = null;
    	Locale locale = null;
    	LocaleSettings settings = LocalePreferenceSerialization.loadFromPreference( username );
    	if (settings.getType() == Type.FROM_BOCONFIG || settings.getType() == Type.FROM_DEFAULT){
    		if (StringUtils.hasValue(oldLanguage)){
    			language = oldLanguage;
    			locale = Localization.createLocaleFromString(language);
    		} else {
    			locale = settings.getLocale();
    			language = settings.getLocale().toString();
    		}
    	}
    	
		this.p_user.setLocaleSettings( settings );
		this.p_user.setLanguage( language );
		this.p_user.setLocale( locale );
		this.p_user.setTimeZone( settings.getTimezone() );
		this.locale = locale;
		this.timeZone = settings.getTimezone();
    }
     
    public void setPerformerIProfileBoui(String iprofile) 
    {
        try
        {
            p_iprofileBoui = Long.parseLong(iprofile);
            
        }
        catch (Exception e)
        {
            p_iprofileBoui = 0;
        }
        
        try {
            if( boApplication.currentContext() != null &&  boApplication.currentContext().getEboContext() != null ); {
            	boObject profile = boObject.getBoManager().loadObject( boApplication.currentContext().getEboContext() , p_iprofileBoui);
            	p_iprofileName = profile.getAttribute("name").getValueString();
            }
        }
        catch ( Exception e ) {
        	
        }
    }
    
    public String getPerformerIProfileName() 
    {
        return p_iprofileName;
    }

    public long getPerformerIProfileBoui() 
    {
        return p_iprofileBoui;
    }
    public String getPerformerIProfileBouiAsString() 
    {
        return String.valueOf(p_iprofileBoui);
    }

        public void setRepository(boRepository newValue)
    {
        p_repos = newValue;
    }    
    
    public boRepository getRepository()
    {
        return p_repos;
    }
    public boApplication getApplication()
    {
        return p_app;
    }
    public void closeSession()
    {
        p_app.getSessions().removeSession( this );
        try{
	        for (String key : this.p_ecmRepositories.keySet()){
	        	Session session = p_ecmRepositories.get( key );
	        	session.logout();
	        }
        } finally {
        	p_ecmRepositories.clear();
        }
    }
    
    public java.util.Date getCreatedTime()
    {
        return new java.util.Date( p_createdon );
    }
    public java.util.Date getLastActivity()
    {
        return new java.util.Date( p_lastactivity );
    }
    
    public String getClientName()
    {
        return p_clientname;    
    }
    
    public boSessionUser getUser()
    {
        return p_user;
    }

    public void poolObjectActivate()
    {
    }

    public void poolObjectPassivate()
    {
    }
    
    public void setProperty( String name, Object value )
    {
        if( p_properties == null )
        {
            p_properties = new Properties();
        }
        p_properties.put( name, value );
            
    }
    public Object getProperty( String name )
    {
        Object ret = null;
        if( p_properties != null )
        {
            ret = p_properties.get( name );
        }
        return ret;
    }
    public void removeProperty( String name )
    {
        if(p_properties != null && p_properties.containsKey(name))
        {
            p_properties.remove(name);
        }
    }
    public Enumeration getProperties()
    {
        Enumeration ret = null;
        if( p_properties != null )
        {
            ret = p_properties.keys();
        }
        return ret;
    }
    public String getId()
    {
        return p_sessionid;
    }
    
    public void putObject(Object key, Object value)
    {
        p_objects.put(key, value);
    }
    
    public Object getObject(Object key)
    {
        return p_objects.get(key);
    }
    
    public boolean removeObject(Object key)
    {
        return (p_objects.remove(key)!=null);
    }
    
    public String getRemoteAddress()
    {
        return p_remoteAddr;
    }
    
    public String getRemoteHost()
    {
        return p_remoteHost;
    }
    
    public String getRemoteUser()
    {
        return p_remoteUser;
    }
    
    public String getRemoteSessionId()
    {
        return p_remoteSessionId;
    }
    
    public void markLogin()
    {
        LoginUtil.login(this);
    }
    
    public void markLogout()
    {
        LoginUtil.logout(this);
    }
    
    public void setDefaultLocale( Locale local )  {
    	threadLocale.set( local );  
    }
    
    public static Locale getDefaultLocale() {
    	Locale locale = XEO.getUserLocale();
    	if( locale == null ) {
    		return defaultLocale;
    	}
    	return locale;
    }
    
    static final Locale defaultLocale = XEO.getCurrentLocale();
    
    public static final ResourceBundle getResourceBundle( String bundleName )
    {
        return ResourceBundle.getBundle( bundleName, getDefaultLocale() );
    }

    
    /**
     * 
     * Retrieves a {@link Session} with a JCR repository, given the name
     * of the repository (which must be configured in boConfig.xml) 
     * 
     * @param repositoryName The name of the repository for which
     * to retrieve the <code>{@link Session}</code>
     * 
     * @return A {@link Session} with a repository or null if one does not exist
     */
    public Session getECMRepositorySession(String repositoryName)
    {
    	if (p_ecmRepositories.containsKey(repositoryName))
			return this.p_ecmRepositories.get(repositoryName);
    	
		return null;
    }
    
    
    /**
     * 
     * Stores a Repository Session of a given Repository
     * 
     * 
     * @param repositoryName The name of the repository
     * @param repositorySession Session with that repository
     */
    public void setECMRepositorySession(String repositoryName, Session repositorySession){
    	this.p_ecmRepositories.put(repositoryName, repositorySession);
    }
    
    
    public Locale getLocale() {
    	return this.locale;
    }
    
    public void setLocale(Locale newLocale) {
    	this.locale = newLocale;
    	resetFormatter();
    }
    
	public TimeZone getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
		resetFormatter();
	}
	
	public void setLocaleSettings(LocaleSettings setings) {
		getUser().setLocaleSettings( setings );
		resetFormatter();
	}
	

	private  transient LocaleFormatter formatter;
	
	private void resetFormatter() {
		formatter = null;
	}
	
	private LocaleFormatter getOrCreateFormatter() {
		if (formatter == null) {
			formatter = new XEOLocaleProvider( getLocale() , getTimeZone(), getUser().getLocaleSettings() );
		}
		return formatter;
		
	}
	
	public LocaleFormatter getLocaleFormater() {
		return getOrCreateFormatter();
	}
	
}
