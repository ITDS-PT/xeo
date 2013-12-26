package netgest.bo.security;

import netgest.bo.system.Logger;

/**
 * Class to log security problems
 *
 */
public class SecurityLogger {
	
	private static final Logger logger = Logger.getLogger(SecurityLogger.class);
	
	/**
	 * Logs a severe security problem
	 * 
	 * @param reason An explanation of the problem
	 */
	public static void severe(String reason){
		logger.severe(reason);
	}

}
