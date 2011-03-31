package netgest.bo.localizations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.boApplication;
import netgest.bo.system.boContext;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;
/**
 * Class that loads the exception messages from the properties file
 * in the current language
 * 
 * @author ngrosskopf
 *
 */
public class MessageLocalizer {
	
	
	/**
	 * 
	 * 
	 * @param whichMessage
	 * @return(String) the exception message
	 */
public static String getMessage(String whichMessage){
		String language;
		Properties properties= new Properties();
	String message = null;
	language=getLanguage();
	if (language.length()>2){
		language=language.substring(0, 1);
	}
	File file;	
	String local=("C:\\xeostudio_novo\\workspace\\xeo_v3_core\\src\\netgest\\bo\\localizations\\"+"MessageLocalizer_");
	
	try {
		String s=("MessageLocalizer_"+language+".properties");
		if( MessageLocalizer.class.getResourceAsStream(s)!=null)
		properties.load( MessageLocalizer.class.getResourceAsStream(s));	
		//file=new File(local+language+".properties");
		//properties.load(new FileInputStream(file));
		if (properties.getProperty(whichMessage)!=null)
		message=properties.getProperty(whichMessage);
		else{
			
		 s=("MessageLocalizer_"+boApplication.getDefaultApplication().getApplicationLanguage()+".properties");
		properties.load( MessageLocalizer.class.getResourceAsStream(s));	
		message=properties.getProperty(whichMessage);
		}
	} catch (FileNotFoundException e) {		
		e.printStackTrace();
	} catch (IOException e) {		
		e.printStackTrace();
	}	
	if(message==null){
	 String s=("MessageLocalizer_"+boApplication.getDefaultApplication().getApplicationLanguage()+".properties");
	try {
		properties.load( MessageLocalizer.class.getResourceAsStream(s));
	} catch (IOException e) {
		
		e.printStackTrace();
	}	
	message=properties.getProperty(whichMessage);
		}
	
	return message;
}
	
	/**
	 * 
	 * 
	 * @return(String) the user language if not null, else the default application language
	 */
	private static String getLanguage(){
		String language;
		boApplication boApp = boApplication.currentContext().getApplication();
		language = boApp.getApplicationLanguage();
		if(boApp.getSessions()!=null){
		
			boContext boctx;
			EboContext ctx;
			boSession session;
			boSessionUser user;
			boctx = boApplication.currentContext();
			
				if (boApp.getSessions().getActiveSessions().length > 0)
					 if(boctx != null){
						 ctx = boctx.getEboContext();
						  if(ctx != null){
							  session = ctx.getBoSession();
							  if(session != null){
								  user = session.getUser();
								  if(user.getLanguage() != null)
									  language = user.getLanguage();
							  }
					 }
				}
		}
		if(language==null||language=="")
			language=boApplication.getDefaultApplication().getApplicationLanguage();
		return language;
	}
}
