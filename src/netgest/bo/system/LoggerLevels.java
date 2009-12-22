package netgest.bo.system;

import java.util.HashMap;


public class LoggerLevels {
	
	private static final HashMap<String, LoggerLevel> levels = new HashMap<String, LoggerLevel>();
	
	public static final LoggerLevel FINEST = new LoggerLevel( 
			new LogLevelForLog4j( org.apache.log4j.Level.TRACE, "FINST"), "FINEST"
		);
	
	public static final LoggerLevel FINER = new LoggerLevel( 
			new LogLevelForLog4j( org.apache.log4j.Level.DEBUG, "FINER"), "FINER"
		);
	
	public static final LoggerLevel FINE = new LoggerLevel( 
			new LogLevelForLog4j( org.apache.log4j.Level.INFO, "FINE"), "FINE"
		);

	public static final LoggerLevel CONFIG = new LoggerLevel( 
			new LogLevelForLog4j( 
					org.apache.log4j.Level.WARN.toInt() - 10, "CONF",
					org.apache.log4j.Level.WARN.getSyslogEquivalent()-1 
			),
			"CONFIG"
		);
	
	public static final LoggerLevel WARNING = new LoggerLevel( 
			new LogLevelForLog4j( org.apache.log4j.Level.WARN, "WARN"), "WARNING"
		);

	public static final LoggerLevel SEVERE = new LoggerLevel( 
			new LogLevelForLog4j( org.apache.log4j.Level.FATAL, "SEVER"), "SEVERE"
		);
	
	public static LoggerLevel getLevel( String levelName ) {
		return levels.get( levelName );
	}
	
	public static class LoggerLevel {
		
		public int value;
		public org.apache.log4j.Level log4jLevel;
		public String name; 
		
		private LoggerLevel( org.apache.log4j.Level log4jLevel, String extendedName ) {
			this.log4jLevel = log4jLevel; 
			this.name = log4jLevel.toString();
			levels.put(  extendedName, this );
		}

		/**
		 * @return the log4jValue
		 */
		protected org.apache.log4j.Level getLevel() {
			return log4jLevel;
		}

		/**
		 * @return the name
		 */
		public String getName() {
			return name;
		}

		
	}
	
	protected static class LogLevelForLog4j extends org.apache.log4j.Level {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public LogLevelForLog4j( int i, String name, int sysLogEqui ) {
			super( i, name, sysLogEqui );
		}

		public LogLevelForLog4j( org.apache.log4j.Level l, String levelName ) {
			super( l.toInt(), levelName, l.getSyslogEquivalent() );
		}
		
	}
	
}
