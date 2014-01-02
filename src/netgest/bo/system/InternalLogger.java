package netgest.bo.system;

import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggingEvent;

class InternalLogger extends org.apache.log4j.Logger {

	private static final String callerfqdn = Logger.class.getName();
	
	protected InternalLogger( String name ) {
		super( name );
	}

	public void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
	    callAppenders(new LoggingEvent(callerfqdn, this, level, message, t));
	}

	
}
