package netgest.bo.system;

import netgest.bo.system.LoggerLevels.LoggerLevel;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.apache.log4j.net.SMTPAppender;


public class boApplicationLoggerConfig {
	
	private String 	forClasses;
	private String 	level;
	private String 	pattern;
	private boolean active;
	
	private EmailProperties 	emailProperties;
	private FileProperties 		fileProperties;
	private ConsoleProperties 	ConsoleProperties;
	
	
	
	/**
	 * @return the forClasses
	 */
	public String getForClasses() {
		return forClasses;
	}

	/**
	 * @param forClasses the forClasses to set
	 */
	public void setForClasses(String forClasses) {
		this.forClasses = forClasses;
	}

	/**
	 * @return the level
	 */
	public String getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(String level) {
		this.level = level;
	}

	/**
	 * @return the pattern
	 */
	public String getPattern() {
		return pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	/**
	 * @return the enabled
	 */
	public boolean isActive() {
		return active;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setActive(boolean enabled) {
		this.active = enabled;
	}

	/**
	 * @return the emailProperties
	 */
	public EmailProperties getEmailProperties() {
		return emailProperties;
	}

	/**
	 * @param emailProperties the emailProperties to set
	 */
	public void setEmailProperties(EmailProperties emailProperties) {
		this.emailProperties = emailProperties;
	}

	/**
	 * @return the fileProperties
	 */
	public FileProperties getFileProperties() {
		return fileProperties;
	}

	/**
	 * @param fileProperties the fileProperties to set
	 */
	public void setFileProperties(FileProperties fileProperties) {
		this.fileProperties = fileProperties;
	}

	/**
	 * @return the consoleProperties
	 */
	public ConsoleProperties getConsoleProperties() {
		return ConsoleProperties;
	}

	/**
	 * @param consoleProperties the consoleProperties to set
	 */
	public void setConsoleProperties(ConsoleProperties consoleProperties) {
		ConsoleProperties = consoleProperties;
	}
	
    public static void shutDownLoggers(boApplicationLoggerConfig[] logConfig ) {
    	if( logConfig != null ) {
	    	for( int i=0; i < logConfig.length; i++ ) {
	    		if( logConfig[i].isActive() ) {
		    		String domainClasses = logConfig[i].getForClasses();
		    		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( domainClasses, Logger.loggerFactory );
		    		logger.removeAllAppenders();
	    		}
	    	}
    	}
    }

	
	public static void applyConfig(boApplicationLoggerConfig[] logConfig ) {
    	if( logConfig != null ) {
	    	for( int i=0; i < logConfig.length; i++ ) {
	    		
	    		if( logConfig[i].isActive() ) {
	    			
		    		String domainClasses = logConfig[i].getForClasses();
		    		
		    		if( logConfig[i].getPattern() == null || "".equals( logConfig[i].getPattern() ) ) {
		    			logConfig[i].setPattern("%d %5p [%t] (%F:%L) - %m%n");	    			
		    		}
		    		
		    		org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( domainClasses, Logger.loggerFactory );
		    		
		    		LoggerLevel level = LoggerLevels.getLevel( logConfig[i].getLevel() );
		    		if( level != null ) {
		    			logger.setLevel( level.getLevel() );
		    		}
		    		
		    		boApplicationLoggerConfig.ConsoleProperties console = logConfig[i].getConsoleProperties();
		    		if( console != null && console.isActive() ) {
		    			ConsoleAppender capp = new ConsoleAppender();
		    			capp.setName("XEO Console Logger");
		    			capp.setLayout( new PatternLayout( logConfig[i].getPattern() ) );
		    			capp.activateOptions();
		    			logger.addAppender( capp );
		    		}
		    		
		    		boApplicationLoggerConfig.FileProperties file = logConfig[i].getFileProperties();
		    		if ( file != null && file.isActive() ) {
		    			RollingFileAppender fapp = new RollingFileAppender();
		    			fapp.setName("XEO RollingFile Logger");
		    			fapp.setFile( file.getLogFile() );
		    			fapp.setMaxFileSize( file.getMaxSize() );
		    			fapp.setMaxBackupIndex( file.getHistoryFiles() );
		    			fapp.setLayout( new PatternLayout( logConfig[i].getPattern() ) );
		    			fapp.activateOptions();
		    			
		    			// Implement console capture.
		    			logger.addAppender( fapp );
		    		}
		
		    		boApplicationLoggerConfig.EmailProperties email = logConfig[i].getEmailProperties();
		    		if ( email != null && email.isActive() ) {
		    			SMTPAppender smtApp = new SMTPAppender();
		    			smtApp.setName("XEO Email Logger");
		    			smtApp.setBufferSize( email.getBuffer() );
		    			smtApp.setSMTPHost( email.getSmtpHost() );
		    			smtApp.setFrom( email.getFrom() );
		    			smtApp.setTo( email.getTo() );
		    			smtApp.setCc( email.getCc() );
		    			smtApp.setBcc( email.getBcc() );
		    			smtApp.setSubject( email.getSubject() );
		    			smtApp.setLayout( new PatternLayout( logConfig[i].getPattern() ) );
		    			smtApp.activateOptions();
		    			logger.addAppender( smtApp );
		    		}
	    		}
	    	}
    	}
    }
    
	
    
	public static class EmailProperties {
		boolean active;
		int		buffer;
		String	smtpHost;
		String	from;
		String 	to;
		String 	cc;
		String 	bcc;
		String 	subject; 
		/**
		 * @return the cc
		 */
		public String getCc() {
			return cc;
		}
		/**
		 * @param cc the cc to set
		 */
		public void setCc(String cc) {
			this.cc = cc;
		}
		/**
		 * @return the bcc
		 */
		public String getBcc() {
			return bcc;
		}
		/**
		 * @param bcc the bcc to set
		 */
		public void setBcc(String bcc) {
			this.bcc = bcc;
		}
		/**
		 * @return the active
		 */
		public boolean isActive() {
			return active;
		}
		/**
		 * @param active the active to set
		 */
		public void setActive(boolean active) {
			this.active = active;
		}
		/**
		 * @return the buffer
		 */
		public int getBuffer() {
			return buffer;
		}
		/**
		 * @param buffer the buffer to set
		 */
		public void setBuffer(int buffer) {
			this.buffer = buffer;
		}
		/**
		 * @return the smtpHost
		 */
		public String getSmtpHost() {
			return smtpHost;
		}
		/**
		 * @param smtpHost the smtpHost to set
		 */
		public void setSmtpHost(String smtpHost) {
			this.smtpHost = smtpHost;
		}
		/**
		 * @return the from
		 */
		public String getFrom() {
			return from;
		}
		/**
		 * @param from the from to set
		 */
		public void setFrom(String from) {
			this.from = from;
		}
		/**
		 * @return the to
		 */
		public String getTo() {
			return to;
		}
		/**
		 * @param to the to to set
		 */
		public void setTo(String to) {
			this.to = to;
		}
		/**
		 * @return the subject
		 */
		public String getSubject() {
			return subject;
		}
		/**
		 * @param subject the subject to set
		 */
		public void setSubject(String subject) {
			this.subject = subject;
		}
		
		
	}
	
	public static class FileProperties {
		boolean active;
		String 	logFile;
		int		historyFiles;
		String	maxSize;

		String	logStandardOutput;  /* REDIRECT / CAPTURE / NO */
		String	logErrorOutput; /* REDIRECT / CAPTURE/ NO */
		
		/**
		 * @return the active
		 */
		public boolean isActive() {
			return active;
		}
		/**
		 * @param active the active to set
		 */
		public void setActive(boolean active) {
			this.active = active;
		}
		/**
		 * @return the logFile
		 */
		public String getLogFile() {
			return logFile;
		}
		/**
		 * @param logFile the logFile to set
		 */
		public void setLogFile(String logFile) {
			this.logFile = logFile;
		}
		/**
		 * @return the historyFiles
		 */
		public int getHistoryFiles() {
			return historyFiles;
		}
		/**
		 * @param historyFiles the historyFiles to set
		 */
		public void setHistoryFiles(int historyFiles) {
			this.historyFiles = historyFiles;
		}
		/**
		 * @return the maxSize
		 */
		public String getMaxSize() {
			return maxSize;
		}
		/**
		 * @param maxSize the maxSize to set
		 */
		public void setMaxSize(String maxSize) {
			this.maxSize = maxSize;
		}
		
		/**
		 * @return the logStardardOutput
		 */
		public String getLogStandardOutput() {
			return logStandardOutput;
		}
		/**
		 * @param logStardardOutput the logStardardOutput to set
		 */
		public void setLogStandardOutput(String logStardardOutput) {
			this.logStandardOutput = logStardardOutput;
		}
		/**
		 * @return the logStandardErrorOutput
		 */
		public String getLogErrorOutput() {
			return logErrorOutput;
		}
		/**
		 * @param logStandardErrorOutput the logStandardErrorOutput to set
		 */
		public void setLogErrorOutput(String logStandardErrorOutput) {
			this.logErrorOutput = logStandardErrorOutput;
		}
		
	}
	
	public static class ConsoleProperties {
		boolean active;
		/**
		 * @return the active
		 */
		public boolean isActive() {
			return active;
		}
		/**
		 * @param active the active to set
		 */
		public void setActive(boolean active) {
			this.active = active;
		}
		
		
	}
	
	
	
}
