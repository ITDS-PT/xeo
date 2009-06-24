/*Enconding=UTF-8*/
package netgest.bo.system;
import java.io.Serializable;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Properties;

import java.util.ResourceBundle;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.PageContext;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.login.LoginUtil;
import org.apache.log4j.Logger;
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
    
    private Hashtable           p_objects;
    
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
        
        init();
    }
    public Long getPerformerBouiLong()
    {
        return new Long(getPerformerBoui());
    }
    
    public void init()
    {
        p_createdon = System.currentTimeMillis();
        p_lastactivity = p_createdon; 
    }
    
    
    public EboContext createRequestContext( HttpServletRequest request, HttpServletResponse response, PageContext pagecontext ) 
    {
        p_lastactivity = System.currentTimeMillis();
        EboContext ctx = new EboContext( this , request , response ,pagecontext );
        return ctx;
    }
    
    public long getPerformerBoui() 
    {
        return p_user.boui;
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
        if(p_user != null && p_user.boui > 0)
        {
//            try
//            {
//                EboContext ctx = null;
//                ctx = createRequestContext(null, null, null);
//                LoginUtil.logout(ctx, getPerformerBoui(), new Date(), p_repos.getName(), p_iprofileBoui);
//                ctx.close();
//            }
//            catch (Exception e)
//            {
//                logger.error("",e);
//            }
        }
        p_app.getSessions().removeSession( this );
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
        // TODO:  Implement this netgest.bo.system.boPoolable abstract method
    }

    public void poolObjectPassivate()
    {
        // TODO:  Implement this netgest.bo.system.boPoolable abstract method
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
    
    
    static final Locale defaultLocale = new Locale( "pt","PT" );
    
    public static final ResourceBundle getResourceBundle( String bundleName )
    {
        return ResourceBundle.getBundle( bundleName, defaultLocale );
    }
}
