package netgest.bo.runtime.actions;

import netgest.bo.runtime.EboContext;
import netgest.bo.system.Logger;
import netgest.bo.system.XEO;
import netgest.bo.system.boApplication;
import netgest.bo.system.boSession;
import netgest.bo.system.boSessionUser;

/**
 * Wraps an action with a login (useful for JSPs / Servlets ) for instance
 */
public abstract class ActionWithLoginWrap {
	
	private static final Logger logger = Logger.getLogger( ActionWithLoginWrap.class );
	
	/**
	 * Login with SYSUSER and no arguments
	 */
	public ActionWithLoginWrap(){
		this( new Object[0] );
	}
	
	/**
	 * 
	 * Login with SYSUSER and passes a set of arguments
	 * 
	 * NOTE: First argument cannot be a String or else the 
	 * {@link ActionWithLoginWrap#ActionWithLoginWrap(String, Object...)} will be invoked instead
	 * 
	 * @param args The arguments to pass to the action
	 */
	public ActionWithLoginWrap(Object... args){
		this( XEO.SYSUSER , args );
	}

	/**
	 * 
	 * Login with a specific user
	 * 
	 * @param user The username to login
	 * @param args The arguments for the action
	 */
	public ActionWithLoginWrap(String user, Object... args){
		
		boSession session = null;
		EboContext ctx = null;
		try {
			session = XEO.loginAs( user );
			session.getApplication().removeContextFromThread();
			ctx = session.createEboContext();
			boApplication.currentContext().addEboContext( ctx );
			onLogin(session.getUser());
			doAction( args );
		} catch (Exception e ){
			logger.warn( "Could not run tests", e );
			onException( e , args );
		} finally {
			if (ctx != null){
				ctx.close();
			}
			if (session != null){
				session.getApplication().removeContextFromThread();    
				session.closeSession();
			}
		}
		
	}
	
	/**
	 * Action to execute
	 * 
	 * @param args Arguments passed in the constructor
	 **/
	public abstract void doAction(Object... args);
	
	/**
	 * 
	 * Invoked when an exception occurs
	 * 
	 * @param e The exception that occurred
	 * @param args Arguments passed in the constructor
	 */
	public void onException(Exception e, Object... args){}
	
	/**
	 * 
	 * Invoked after a successful login 
	 * 
	 * @param user The logged user
	 * @param args The arguments passed in the constructor
	 */
	public void onLogin(boSessionUser user, Object... args){ }

}
