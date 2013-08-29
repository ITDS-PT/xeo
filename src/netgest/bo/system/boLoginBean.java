/*Enconding=UTF-8*/
package netgest.bo.system;
import netgest.bo.boConfig;
import netgest.bo.configUtils.RepositoryConfig;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.login.boMD5Login;

import netgest.io.jcr.ECMRepositoryConnection;
import netgest.utils.MD5Utils;
import netgest.utils.StringUtils;

import java.util.Iterator;
import java.util.Properties;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.jcr.Session;
import javax.servlet.http.HttpServletRequest;

public class boLoginBean implements SessionBean  {

    private static final String SystemKey=MD5Utils.getRandomHexKey(); 
    public void ejbCreate() {
    }

    public void ejbActivate() {
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
    }

    public void setSessionContext(SessionContext ctx) {    
    }
    
    public boSession boLogin( boApplication app , String repository, String clientName,  String username , long time, long timeCheck ) throws boLoginException 
    {
      return this.boLogin( app, repository, clientName , username , time, timeCheck, null );
    }

    public boSession boLogin( boApplication app , String repository, String clientName,  String username , String password ) throws boLoginException 
    {
      return this.boLogin( app, repository, clientName , username , password, null );
    }
    public boSession boLogin( boApplication app, String repository ,String clientName, String username , String password , HttpServletRequest request ) throws boLoginException 
    {
        boSession toReturn = null;
        Properties prop = app.getApplicationConfig().getAuthentication();
        String authclass = prop.getProperty("authclass");
        LoginManager p_loginmanager = null;
        EboContext ctx = null;
        try
        {
          if(username.equals("SYSTEM"))  
        	  return boLoginSystem( password , repository, app, request );
          
          //Loads the login class to use for authentication if not defined it loads the default one
          if ( !StringUtils.isEmpty( authclass ) )
        	  p_loginmanager  = (LoginManager)Class.forName(authclass).newInstance();            
          else
        	  p_loginmanager  = new netgest.bo.system.login.boDefaultLogin();               
           
          long perfboui = p_loginmanager.boLogin( app, repository, username,password,request );
          if ( perfboui != 0 ){
            boSession session = boLoginSystem( SystemKey, repository, app, request );
            ctx = session.createEboContext();            
            boObject perf = XEO.load( ctx, perfboui );
   
            boSessionUser user = new BoUserCreator().create( perf );
            
            
            ctx.close();
            session.closeSession();
            
            //toReturn = new boSession( perfboui , repository ,user, app );
            toReturn = app.getSessions().createSession( repository, user, clientName, request != null ? request.getRemoteAddr():null, 
                request != null ? request.getRemoteHost():null, request != null ? request.getRemoteUser():null, request != null ? request.getRequestedSessionId():null  );
            
            //Check for every ECM Repository and create session with it
            Iterator<String> it = boConfig.getApplicationConfig().
            	getFileRepositoryNames().iterator();
            
            while (it.hasNext()){
            	String repositoryName = it.next();
            	RepositoryConfig repConfig = boConfig.getApplicationConfig().
            		getFileRepositoryConfiguration(repositoryName);
            	
            	String className = repConfig.getClassConnections();
            	//If there's no class name the only thing that matters is the file connector class which
            	//can be used
            	if (className != null){
            		ECMRepositoryConnection conn = 
                		(ECMRepositoryConnection)Class.forName(className).newInstance();
                	
                	Session rep = conn.getConnection(username,password,repConfig.connectionParameters());

                	toReturn.setECMRepositorySession(repositoryName, rep);
            	}
            }
            //Done with ECM Repositories
            
          } else {
              throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
          }
          return toReturn;
        }
        catch(ClassNotFoundException e)
        {
          throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }        
        catch (boRuntimeException e) {
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);        
        }
        catch(IllegalAccessException e)
        {
          throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);          
        }
        catch(InstantiationException e)
        {
          throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);
        }
        finally
        {
          if (ctx!=null)ctx.close();
        }
    }
    public boSession boLogin( boApplication app, String repository ,String clientName, String username , long time, long timeCheck, HttpServletRequest request ) throws boLoginException 
    {
        boSession toReturn=null;
        LoginManager p_loginmanager=null;
        EboContext ctx=null;
        try
        {
           p_loginmanager  = new boMD5Login();               
           
          long perfboui=p_loginmanager.boLogin( app, repository, username, time, timeCheck, request);
          if (perfboui!=0){
            boSession session = boLoginSystem(SystemKey,repository, app, request);
            ctx = session.createEboContext();     
            boObject perf = XEO.load( ctx , perfboui );
            
            boSessionUser user = new BoUserCreator().create( perf );

            ctx.close();
            session.closeSession();
            
            //toReturn = new boSession( perfboui , repository ,user, app );
            toReturn = app.getSessions().createSession( repository, user, clientName, 
                request != null ? request.getRemoteAddr():null, request != null ? request.getRemoteHost():null, 
                request != null ? request.getRemoteUser():null, request != null ? request.getRequestedSessionId():null );
          } else {
              throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
          }
          return toReturn;
        }        
        catch (boRuntimeException e) {
            throw new boLoginException(boLoginException.UNEXPECTED_ERROR,e);        
        }        
        finally
        {
          if (ctx!=null)ctx.close();
        }
    }
    public static final String getSystemKey() {
        return SystemKey;
    }

    private boSession boLoginSystem(String password,String rep, boApplication app, HttpServletRequest request ) throws boLoginException {
        if(!password.equals(SystemKey)) {
            throw new boLoginException(boLoginException.INVALID_CREDENCIALS);
        }
        boSessionUser user = new boSessionUser();
        
        user.userName = "SYSTEM";

        user.email = "system@system.netgest.bo";
        user.boui = 0;
        user.name =   "SYSTEM";
        user.srName = "SYSTEM";
        user.securityLevel = 99;
        
        boSession toReturn = app.getSessions().createSession( rep , user, "XEOWEBCLIENT", 
            request != null ? request.getRemoteAddr():null, request != null ? request.getRemoteHost():null, 
            request != null ? request.getRemoteUser():null, request != null ? request.getRequestedSessionId():null );
        
        return toReturn;
    }
    
    
}