/*Enconding=UTF-8*/
package netgest.bo.system;
import java.math.BigDecimal;

import java.util.Properties;

import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import javax.servlet.http.HttpServletRequest;

import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boLoginException;

import netgest.bo.system.login.*;
import netgest.utils.MD5Utils;

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
        boSession toReturn=null;
        Properties prop=app.getApplicationConfig().getAuthentication();
        String authclass=prop.getProperty("authclass");
        LoginManager p_loginmanager=null;
        EboContext ctx=null;
        try
        {
//          String repos=boConfig.getDefaultRepository();
          if(username.equals("SYSTEM"))  return boLoginSystem( password , repository, app, request );
          
          //Loads the login class to use for authentication if not defined it loads the default one
          if (authclass!=null && !authclass.equals(""))
           p_loginmanager  = (LoginManager)Class.forName(authclass).newInstance();            
          else
           p_loginmanager  = new netgest.bo.system.login.boDefaultLogin();               
           
          long perfboui=p_loginmanager.boLogin( app, repository, username,password,request);
          if (perfboui!=0)
          {
//            boConfigRepository rep = boConfig.getConfigRepository(repos);
            boSession session = boLoginSystem(SystemKey,repository, app, request);
            ctx = session.createRequestContext(null,null,null);            
            boObject perf=boObject.getBoManager().loadObject(ctx,perfboui);

            boSessionUser user = new boSessionUser( );
            user.userName = perf.getAttribute("username").getValueString();
            user.boui = perfboui;
            user.email = perf.getAttribute("email").getValueString();
            user.groups = bridgeToArray( perf.getBridge("groups") );
            if ( user.userName.equals("SYSUSER") )
            {
                user.isAdministrator = true;
            }
            user.mailboxes = bridgeToArray( perf.getBridge("emailAccounts") );
            user.name  = perf.getAttribute( "name" ).getValueString();
            user.notify = perf.getAttribute("notifica").getValueString();
            user.queues = bridgeToArray( perf.getBridge( "queues" ) );
            user.roles  = bridgeToArray( perf.getBridge( "roles" ) );
            if( perf.getAttribute("securityLevel").getValueObject() != null )
            {
                user.securityLevel =((BigDecimal)perf.getAttribute("securityLevel").getValueObject()).byteValue();
            }
            user.srName = perf.getAttribute("lastname").getValueString();
            
            ctx.close();
            session.closeSession();
            
            //toReturn = new boSession( perfboui , repository ,user, app );
            toReturn = app.getSessions().createSession( repository, user, clientName, request != null ? request.getRemoteAddr():null, 
                request != null ? request.getRemoteHost():null, request != null ? request.getRemoteUser():null, request != null ? request.getRequestedSessionId():null  );
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
        Properties prop=app.getApplicationConfig().getAuthentication();
        String authclass=prop.getProperty("authclass");
        LoginManager p_loginmanager=null;
        EboContext ctx=null;
        try
        {
           p_loginmanager  = new boMD5Login();               
           
          long perfboui=p_loginmanager.boLogin( app, repository, username, time, timeCheck, request);
          if (perfboui!=0)
          {
//            boConfigRepository rep = boConfig.getConfigRepository(repos);
            boSession session = boLoginSystem(SystemKey,repository, app, request);
            ctx = session.createRequestContext(null,null,null);            
            boObject perf=boObject.getBoManager().loadObject(ctx,perfboui);

            boSessionUser user = new boSessionUser( );
            user.userName = perf.getAttribute("username").getValueString();
            user.boui = perfboui;
            user.email = perf.getAttribute("email").getValueString();
            user.groups = bridgeToArray( perf.getBridge("groups") );
            user.mailboxes = bridgeToArray( perf.getBridge("emailAccounts") );
            user.name  = perf.getAttribute( "name" ).getValueString();
            user.notify = perf.getAttribute("notifica").getValueString();
            user.queues = bridgeToArray( perf.getBridge( "queues" ) );
            user.roles  = bridgeToArray( perf.getBridge( "roles" ) );
            if( perf.getAttribute("securityLevel").getValueObject() != null )
            {
                user.securityLevel =((BigDecimal)perf.getAttribute("securityLevel").getValueObject()).byteValue();
            }
            user.srName = perf.getAttribute("lastname").getValueString();

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
    
    private static final long[] bridgeToArray( bridgeHandler bridge ) throws boRuntimeException
    {
        long[] ret = null;
        if( !bridge.isEmpty() )
        {
            ret = new long[ bridge.getRowCount()];
            int rec = bridge.getRow();
            bridge.beforeFirst();
            
            while( bridge.next() )
            {
                ret[ bridge.getRow() - 1 ] = bridge.getValueLong();
            }
            
            bridge.moveTo( rec );
        }
        return ret;
    }
    
}