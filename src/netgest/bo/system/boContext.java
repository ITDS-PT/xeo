package netgest.bo.system;
import netgest.bo.boConfig;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.locale.LocaleFormatter;
import netgest.bo.system.locale.LocaleSettings;
import netgest.bo.system.locale.XEOLocaleProvider;

import java.util.Locale;
import java.util.TimeZone;
import java.util.Vector;

public class boContext 
{
    private Vector<EboContext>           allEboContexts;
    
    private boApplication   application;
    private EboContext      eboContext;

	private Locale locale = null;

	private TimeZone timeZone = TimeZone.getDefault();
    
    public boContext( boApplication application )
    {
        this.application = application;
    }
    
    public boApplication getApplication()
    {
        return application;
    }
    
    public EboContext getEboContext()
    {
        return eboContext;       
    }
    
    public EboContext[] getEboContexts()
    {
        if( allEboContexts != null )
        {
            return (EboContext[])allEboContexts.toArray( new EboContext[ allEboContexts.size() ] );
        }
        return null;
    }
    
    public void addEboContext( EboContext oEboContext ) {
        if( allEboContexts == null ) {
            allEboContexts = new Vector<EboContext>();
        }
        if( allEboContexts.indexOf( oEboContext ) == -1 ) {
            allEboContexts.add( oEboContext );
        }
        if( this.eboContext == null ) {
            this.eboContext = oEboContext;
        }
    }
    
    public Locale getUserLocale(){
    	if (getEboContext() != null) {
			Locale current = getEboContext().getBoSession().getLocale();
			return current;
		}
    	return null;
    }
    
    public Locale getLocale() {
    	
    	if (this.locale == null) {
    		if (getEboContext() != null) {
    			Locale current = getEboContext().getBoSession().getLocale();
    			if (current != null)
    	    		return current;
    		}
    	} else {
    		return this.locale;
    	} 
    	
    	return boConfig.getLocaleSettings().getLocale();
    }
    
    public void setLocale(Locale newLocale) {
    	if (getEboContext() != null) {
			getEboContext().getBoSession().setLocale( newLocale );
		}
    	else {
    		this.locale = newLocale;
    		resetFormatter();
    	}
	}
    
    

	public TimeZone getTimeZone() {
		if (this.timeZone == null) {
    		if (getEboContext() != null) {
    			TimeZone userTime = getEboContext().getBoSession().getTimeZone();
    			if (userTime != null)
    				return userTime;
    		}
    	} else {
    		return this.timeZone;
    	}
		
		return boConfig.getLocaleSettings().getTimezone();
		
	}

	public void setTimeZone(TimeZone timeZone) {
		if (getEboContext() != null) {
			getEboContext().getBoSession().setTimeZone( timeZone );
		} else {
			this.timeZone = timeZone; 
			resetFormatter();
		}
	}
	
	private LocaleFormatter formatter;
	
	private void resetFormatter() {
		formatter = null;
	}
	
	private LocaleFormatter getOrCreateFormatter() {
		if (getEboContext() != null) {
			return getEboContext().getBoSession().getLocaleFormater();
		}
		if (formatter == null) {
			LocaleSettings settings = boConfig.getLocaleSettings(); 
			formatter = new XEOLocaleProvider( getLocale() , getTimeZone(), settings );
		}
		return formatter;
		
	}
	
	public LocaleFormatter getLocaleFormater() {
		return getOrCreateFormatter();
	}
    
    
    
}