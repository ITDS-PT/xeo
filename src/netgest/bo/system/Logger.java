package netgest.bo.system;

import java.util.Formatter;
import java.util.Locale;

import netgest.bo.system.LoggerLevels.LoggerLevel;

import org.apache.log4j.Level;
import org.apache.log4j.spi.LoggerFactory;

public class Logger {
	
	/** The Constant loggerFactory. */
	protected static final XEOLoggerFactory loggerFactory = new XEOLoggerFactory();
	
	/** The internal logger. */
	private org.apache.log4j.Logger internalLogger;
	
	/**
	 * Instantiates a new logger.
	 * 
	 * @param internalLogger the internal logger
	 */
	private Logger( org.apache.log4j.Logger internalLogger ) {
		this.internalLogger = internalLogger;
	}
	
	/**
	 * Sets the logger level for this logger.
	 * 
	 * @param level the new level based on class LoggerLevels
	 */
	public void setLevel(LoggerLevel level) {
		internalLogger.setLevel( level.getLevel() );
	}

	/**
	 * Gets the current logger level.
	 * 
	 * @return the current level for this logger
	 */
	public final Level getLevel() {
		return internalLogger.getLevel();
	}

	/**
	 * Log a message to the logger system
	 * 
	 * @param level the level of the message
	 * @param message String with the message to log
	 */
	public void log( LoggerLevel level, String message ) {
		log( level, message, (Throwable)null );
	}

	/**
	 * Log a message to the logger system
	 * 
	 * @param level the level
	 * @param message mask of the message formated as {@link Formatter}
	 * @param messageArgs the arguments for the message
	 */
	public void log( LoggerLevel level, String message, Object... messageArgs ) {
		log( level, message, (Throwable)null, messageArgs );
	}

	/**
	 * Log a message to the logger system
	 * 
	 * @param level the level
	 * @param message  String with the message to log
	 * @param t the base exception
	 */
	public void log( LoggerLevel level, String message, Throwable t ) {
		log( level, message, t, null, null );
	}

	/**
	 * Log a message to the logger system
	 * 
	 * @param level the level
	 * @param message mask of the message formated as {@link Formatter}
	 * @param t the base exception
	 * @param messageArgs  the arguments for the message
	 */
	public void log( LoggerLevel level, String message, Throwable t, Object... messageArgs ) {
		if( isLoggable( level ) ) {
			if( message != null && (messageArgs != null && messageArgs.length > 0 ) ) {
				message = format( message, messageArgs );
			}
			internalLogger.log( level.getLevel(), message, t );
		}
	}
	
	/**
	 * Checks if is finest enabled.
	 * 
	 * @return true, if is finest enabled
	 */
	public boolean isFinestEnabled() {
		return internalLogger.isTraceEnabled();
	}

	/**
	 * Checks if is finer enabled.
	 * 
	 * @return true, if is finer enabled
	 */
	public boolean isFinerEnabled() {
		return internalLogger.isDebugEnabled();
	}

	/**
	 * Checks if is fine enabled.
	 * 
	 * @return true, if is fine enabled
	 */
	public boolean isFineEnabled() {
		return internalLogger.isInfoEnabled();
	}

	/**
	 * Checks if is loggable.
	 * 
	 * @param level the level
	 * 
	 * @return true, if is loggable
	 */
	public boolean isLoggable(LoggerLevel level) {
		return internalLogger.isEnabledFor( level.getLevel() );
	}

	/**
	 * Log in Finest level.
	 * 
	 * @param t the base exception
	 */
	public void finest( Throwable t ) {
		log( LoggerLevels.FINEST, null, t );
	}

	/**
	 *  Log in Finest level.
	 * 
	 * @param message the message
	 */
	public void finest( String message ) {
		log( LoggerLevels.FINEST, message );
	}

	/**
	 *  Log in Finest level.
	 * 
	 * @param message the message
	 * @param e the base exception
	 */
	public void finest( String message, Throwable e ) {
		log( LoggerLevels.FINEST, message, e );
	}

	/**
	 * Log in Finest level.
	 * 
	 * @param message mask of the message formated as {@link Formatter}
	 * @param messageArgs the arguments for the message
	 */
	public void finest( String message, Object... messageArgs ) {
		log( LoggerLevels.FINEST, message, messageArgs );
	}

	/**
	 * Log in Finest level.
	 * 
	 * @param message mask of the message formated as {@link Formatter}
	 * @param e the base exception
	 * @param messageArgs the arguments for the message
	 */
	public void finest( String message, Throwable e, Object... messageArgs ) {
		log( LoggerLevels.FINEST, message, e, messageArgs );
	}
	

	/**
	 * Log in Finer level.
	 * 
	 * @param t the base exception
	 */
	public void finer( Throwable t ) {
		log( LoggerLevels.FINER, null, t );
	}
	
	/**
	 * Log in Finer level.
	 * 
	 * @param message the message
	 */
	public void finer( String message ) {
		log( LoggerLevels.FINER, message );
	}

	/**
	 * Log in Finer level.
	 * 
	 * @param message the message
	 * @param e the base exception
	 */
	public void finer( String message, Throwable e ) {
		log( LoggerLevels.FINER, message, e );
	}

	/**
	 * Log in Finer level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param messageArgs the arguments for the message
	 */
	public void finer( String message, Object... messageArgs ) {
		log( LoggerLevels.FINER, message, messageArgs );
	}

	/**
	 * Log in Finer level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param e the base exception
	 * @param messageArgs the arguments for the message
	 */
	public void finer( String message, Throwable e, Object... messageArgs ) {
		log( LoggerLevels.FINER, message, e, messageArgs );
	}
	
	/**
	 *  Log in Fine level.
	 * 
	 * @param t the base exception
	 */
	public void fine( Throwable t ) {
		log( LoggerLevels.FINE, null, t );
	}

	/**
	 *  Log in Fine level.
	 * 
	 * @param message the message
	 */
	public void fine( String message ) {
		log( LoggerLevels.FINE, message );
	}

	/**
	 * Log in Fine level.
	 * 
	 * @param message the message
	 * @param e the base exception
	 */
	public void fine( String message, Throwable e ) {
		log( LoggerLevels.FINE, message, e );
	}

	/**
	 * Log in Fine level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param messageArgs the message arguments
	 */
	public void fine( String message, Object... messageArgs ) {
		log( LoggerLevels.FINE, message, messageArgs );
	}

	/**
	 * Log in Fine level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param e the base exception
	 * @param messageArgs the message arguments
	 */
	public void fine( String message, Throwable e, Object... messageArgs ) {
		log( LoggerLevels.FINE, message, e, messageArgs );
	}

	/**
	 * Log in Warning level.
	 * 
	 * @param t the base exception
	 */
	public void warn( Throwable t ) {
		log( LoggerLevels.WARNING, null, t );
	}

	/**
	 * Log in Warning level.
	 * 
	 * @param message the message
	 */
	public void warn( String message ) {
		log( LoggerLevels.WARNING, message );
	}

	/**
	 * Log in Warning level.
	 * 
	 * @param message the message
	 * @param e the base exception
	 */
	public void warn( String message, Throwable e ) {
		log( LoggerLevels.WARNING, message, e );
	}

	/**
	 * Log in Warning level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param messageArgs the message arguments
	 */
	public void warn( String message, Object... messageArgs ) {
		log( LoggerLevels.WARNING, message, messageArgs );
	}

	/**
	 * Log in Warning level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param e the base exception
	 * @param messageArgs the message arguments
	 */
	public void warn( String message, Throwable e, Object... messageArgs ) {
		log( LoggerLevels.WARNING, message, e, messageArgs );
	}
	
	/**
	 * Log in Severe level.
	 * 
	 * @param t the base exception
	 */
	public void severe( Throwable t ) {
		log( LoggerLevels.WARNING, null, t );
	}
	
	/**
	 *  Log in Severe level.
	 * 
	 * @param message the message
	 */
	public void severe( String message ) {
		log( LoggerLevels.SEVERE, message );
	}

	/**
	 * Log in Severe level.
	 * 
	 * @param message the message
	 * @param e the base exception
	 */
	public void severe( String message, Throwable e ) {
		log( LoggerLevels.SEVERE, message, e );
	}

	/**
	 * Log in Severe level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param messageArgs the message arguments
	 */
	public void severe( String message, Object... messageArgs ) {
		log( LoggerLevels.SEVERE, message, messageArgs );
	}

	/**
	 * Log in Severe level.
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param e the base exception
	 * @param messageArgs the message arguments
	 */
	public void severe( String message, Throwable e, Object... messageArgs ) {
		log( LoggerLevels.SEVERE, message, e, messageArgs );
	}
	
	/**
	 * Log in Config level..
	 * 
	 * @param t the t
	 */
	public void config( String message ) {
		log( LoggerLevels.CONFIG, message );
	}

	/**
	 * Log in Config level..
	 * 
	 * @param message the mask of the message formated as {@link Formatter}
	 * @param messageArgs the message arguments
	 */
	public void config( String message, Object... messageArgs ) {
		log( LoggerLevels.CONFIG, message, messageArgs );
	}

	
	/**
	 * Gets the logger for a package or class name.
	 * NOTE: SideEffect -> Tries to initialize the XEO Application
	 * 
	 * @param name the package or class name
	 * 
	 * @return the logger
	 */
	public static Logger getLogger( String name ) {
		boApplication.getApplicationFromStaticContext("XEO");
		return new Logger( InternalLogger.getLogger(name, loggerFactory ) );
	}
	
	/**
	 * Gets the logger with localized messages.
	 * NOTE: SideEffect -> Tries to initialize the XEO Application
	 * 
	 * @param name the name
	 * @param bundleName the bundle localized bundle for the message
	 * 
	 * @return the logger
	 */
	public static Logger getLogger( String name, String bundleName ) {
		boApplication.getApplicationFromStaticContext("XEO");
		return new Logger( InternalLogger.getLogger(name, loggerFactory ) );
	}
	
	/**
	 * Gets the logger for a class
	 * NOTE: SideEffect -> Tries to initialize the XEO Application 
	 * 
	 * @param clazz the class
	 * 
	 * @return the logger
	 */
	public static Logger getLogger( Class clazz ) {
		boApplication.getApplicationFromStaticContext("XEO");
		return new Logger( InternalLogger.getLogger( clazz.getName(), loggerFactory ) );
	}
	
	/**
	 * Format a string using Formatter mask
	 * 
	 * @param message the message
	 * @param args the args
	 * 
	 * @return the string
	 */
	private static final String format( String message, Object... args ) {
		try {
			StringBuilder sb = new StringBuilder();
			Formatter f = new Formatter( sb, Locale.getDefault() );
			f.format( message , args );
			return sb.toString();
		}
		catch( Throwable e ) {
			return message;
		}
	}
	
	
	/**
	 * A factory for creating XEOLogger objects.
	 */
	private static class XEOLoggerFactory implements LoggerFactory {
		
		public org.apache.log4j.Logger makeNewLoggerInstance(String arg0) {
			return new InternalLogger( arg0 );
		}
		
	}
	
}
