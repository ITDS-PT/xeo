package netgest.bo.system;

import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.boDefHandler;
import netgest.bo.lovmanager.LovManager;
import netgest.bo.lovmanager.lovObject;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boObjectListBuilder;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.locale.LocaleFormatter;

/**
 * Facade for the most common operations
 */
public class XEO {
	
	private static final Logger logger = Logger.getLogger( XEO.class );
	
	static EboContext getEboContext(){
		return boApplication.currentContext().getEboContext();
	}
	
	//boObject Methods
	public static boObject loadWithQuery(String boql, Object... args) throws boRuntimeException {
		return boObject.getBoManager().loadObject( getEboContext() , boql , args );
	}
	
	public static boObject loadWithQuery(EboContext ctx, String boql, Object... args) throws boRuntimeException {
		return boObject.getBoManager().loadObject( ctx, boql , args );
	}
	
	public static boObject loadWithQuery(String boql) throws boRuntimeException {
		return boObject.getBoManager().loadObject( getEboContext() , boql );
	}
	
	public static boObject loadWithQuery(EboContext ctx, String boql) throws boRuntimeException {
		return boObject.getBoManager().loadObject( ctx , boql );
	}
	
	public static boObject load(long boui) throws boRuntimeException {
		return boObject.getBoManager().loadObject( getEboContext() , boui );
	}
	
	public static boObject load(EboContext ctx, long boui) throws boRuntimeException {
		return boObject.getBoManager().loadObject( ctx, boui );
	}
	
	public static boObject create(String objectName) throws boRuntimeException {
		return boObject.getBoManager().createObject( getEboContext() , objectName );
	}
	
	public static boObject create(EboContext ctx, String objectName) throws boRuntimeException {
		return boObject.getBoManager().createObject( ctx , objectName );
	}
	
	public static boObject createWithParent(String objectName, long parent) throws boRuntimeException {
		return boObject.getBoManager().createObjectWithParent( getEboContext() , objectName, parent );
	}
	
	public static boObject createWithParent(EboContext ctx, String objectName, long parent) throws boRuntimeException {
		return boObject.getBoManager().createObjectWithParent( ctx , objectName, parent );
	}
	
	// boObjectList Methods
	/**
	 * 
	 * Executes a BOQL query without arguments
	 * 
	 * @param boql The query to execute
	 * 
	 * @return The list of objects matching the query 
	 */
	public static boObjectList list(String boql){
		return boObjectList.list( getEboContext() , boql );
	}
	
	/**
	 * Executes a BOQL query without arguments in a specific context
	 * 
	 * @param ctx The context to execute the query in
	 * @param boql The query to execute
	 *  
	 * @return The list of objects matching the query
	 */
	public static boObjectList list(EboContext ctx, String boql){
		return boObjectList.list( ctx , boql );
	}
	
	/**
	 * 
	 * Executes a BOQL query with arguments and returns a {@link boObjectList} with the results
	 * 
	 * @param boql The query to execute
	 * @param args The arguments (you can pass {@link boObject} instances as arguments) for the query
	 * 
	 * @return A list of objects matching the query
	 */
	public static boObjectList list(String boql, Object... args){
		Object[] totalArgs = convertBoObjectArguments( args );
		return boObjectList.list( getEboContext() , boql,  totalArgs );
	}

	private static Object[] convertBoObjectArguments(Object... args) {
		Object[] totalArgs = new Object[args.length];
		int k = 0;
		for (Object curr : args){
			if (curr instanceof boObject){
				totalArgs[k] = ((boObject)curr).getBoui();
			} else {
				totalArgs[k] = curr;
			}
			k++;
		}
		return totalArgs;
	}
	
	/**
	 * 
	 * Executes a BOQL query with arguments in a specific context and returns a {@link boObjectList} with the results
	 * 
	 * @param ctx The context to execute the query in
	 * @param boql The query to execute
	 * @param args The arguments (you can pass {@link boObject} instances as arguments) for the query
	 * 
	 * @return A list of objects matching the query
	 */
	public static boObjectList list(EboContext ctx, String boql, Object... args){
		Object[] totalArgs = convertBoObjectArguments( args );
		return boObjectList.list( ctx , boql,  totalArgs );
	}
	
	/**
	 * 
	 * Creates a {@link boObjectListBuilder} instance with a specific query
	 * 
	 * @param boql The boql expression to create the builder with
	 * 
	 * @return A new instance of {@link boObjectListBuilder}
	 */
	public static boObjectListBuilder builder(String boql){
		return boObjectList.builder( boql );
	}
	
	/**
	 * Creates a {@link boObjectListBuilder} instance with a specific query
	 * in a specific context
	 * 
	 * @param ctx The context to create the {@link boObjectListBuilder}
	 * @param boql The boql expression to create the builder with
	 * 
	 * @return A new instance of {@link boObjectListBuilder}
	 */
	public static boObjectListBuilder builder(EboContext ctx, String boql){
		return boObjectList.builder( ctx, boql );
	}
	
	//Lov Object Methods
	/**
	 * 
	 * Loads a {@link lovObject} given its name
	 * 
	 * @param lovName The name of the lov
	 * @return A lov object instance
	 * 
	 * @throws boRuntimeException If the lov could not be loaded
	 */
	public static lovObject getLov(String lovName) throws boRuntimeException {
		return LovManager.getLovObject( getEboContext() , lovName );
	}
	
	/**
	 * 
	 * Loads a {@link lovObject} given its name in a specific context
	 * 
	 * @param ctx The context to load the lov in
	 * @param lovName The name of the lov
	 * @return A lov object instance
	 * 
	 * @throws boRuntimeException If the lov could not be loaded
	 */
	public static lovObject getLov(EboContext ctx, String lovName) throws boRuntimeException {
		return LovManager.getLovObject( ctx , lovName );
	}
	
	
	//boDefHandler Methods
	/**
	 * 
	 * Retrieve the metadata associated with a specific model
	 * 
	 * @param modelName The name of the model
	 * @return A {@link boDefHandler} instance with the model metadata
	 */
	public static boDefHandler getMetadata(String modelName){
		return boDefHandler.getBoDefinition( modelName );
	}
	
	
	/**
	 * 
	 * Create a session by loggin in as the SYSUSER
	 * 
	 * @return A {@link boSession} representing the SYSUSER session
	 */
	public static boSession loginAsSystem() {
		try {
			return boApplication.getXEO().boLogin( "SYSUSER" , boLoginBean.getSystemKey() );
		} catch ( boLoginException e ) {
			logger.warn( "Error when attempting login with SYSUSER", e );
		}
		return null;
	}
	
	/**
	 * 
	 * Create a session by loggin in as a specific user
	 * 
	 * @param username The username to login with
	 * 
	 * @return A {@link boSession} representing the user session
	 */
	public static boSession loginAs(String username) {
		try {
			return boApplication.getXEO().boLogin( username , boLoginBean.getSystemKey() );
		} catch ( boLoginException e ) {
			logger.warn( "Error when attempting login with %s", e , username );
		}
		return null;
	}
	
	
	/**
	 * Retrieve a {@link DriverUtils} instance. 
	 */
	public static DriverUtils getDriverUtils(){
		return getEboContext().getDataBaseDriver().getDriverUtils();
	}
	
	
	/**
	 * 
	 * Retrieves the current Locale associated with the logged user, or the application 
	 * default locale
	 * 
	 * @return
	 */
	public static Locale getCurrentLocale() {
		return boApplication.currentContext().getLocale(); 
	}
	
	public static Locale getUserLocale() {
		return boApplication.currentContext().getUserLocale();
	}
	
	public static List<Locale> getAvailableLocales(){
		return boApplication.getXEO().getApplicationConfig().getLocaleSettings().getAvailableLocales();
	}
 	
	/**
	 * 
	 * Retrieves the current Timezone associated with the currently logged user
	 * or the application default timezone
	 * 
	 * @return
	 */
	public static TimeZone getCurrentTimeTone() {
		return boApplication.currentContext().getTimeZone();
	}
	
	/**
	 * 
	 * Retrieves the current thread-associated instance of {@link LocaleFormatter}
	 * which deals with locale aware formatting of dates/numbers  
	 * 
	 * @return A {@link LocaleFormatter} instance
	 */
	public static LocaleFormatter getLocaleFormatter() {
		return boApplication.getLocalizationFormatter();
	}
	
	
}
