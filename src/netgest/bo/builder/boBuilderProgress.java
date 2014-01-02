package netgest.bo.builder;

import java.io.CharArrayWriter;
import java.io.PrintWriter;

import netgest.bo.system.Logger;

public class boBuilderProgress {

	private static final 	Logger 	logger = Logger.getLogger( boBuilderProgress.class );
	
	private String 			overallTaskName 	= "";

	private int				overallTasks 		= 0;
	private int				overallCurrentValue = 0;
	private float 			overallProgress 	= 0;

	private String			currentTaskName		= "";
	private float 			currentTaskProgress = 0f;
	private int 			currentTasks 		= 0;
	private int 			currentTaskValue 	= 0;
	
	private boolean			logToConsole		= false;
	
	private CharArrayWriter logBuffer = new CharArrayWriter();
	private PrintWriter 	logWriter = new PrintWriter( logBuffer );
	
	public boolean isLogToConsole() {
		return logToConsole;
	}

	public void setLogToConsole(boolean logToConsole) {
		this.logToConsole = logToConsole;
	}

	public void addOverallProgress() { 
		float rslt = (float)Math.round( ((float)++overallCurrentValue / (float)overallTasks)*100 ) / 100f ;
		overallProgress = Math.min( rslt, 1);
	}
	
	public void addCurrentTaskProgress() {
		float rslt = (float)Math.round( ((float)++currentTaskValue / (float)currentTasks)*100 ) / 100f ;
		currentTaskProgress = Math.min( rslt, 1);
	}
	
	public int getCurrentTasks() {
		return currentTasks;
	}

	public void setCurrentTasks(int currentTasks) {
		this.currentTaskProgress = 0;
		this.currentTaskValue = 0;
		this.currentTasks = currentTasks;
	}

	public void finish() {
		overallProgress = 1;
		currentTaskProgress = 1;
		currentTaskName = "";
	}
	
	public void setOverallTasks( int tasks ) {
		this.overallTasks = tasks;
	}
	
	public float getOverallProgress() {
		return overallProgress;
	}
	
	public float getCurrentTaskProgress() {
		return currentTaskProgress;
	}
	
	public void setCurrentTaskProgress(int currentTaskProgress) {
		this.currentTaskProgress = currentTaskProgress;
	}
	
	public String getOverallTaskName() {
		return overallTaskName;
	}
	
	public void setOverallTaskName(String overallTaskName) {
		this.overallTaskName = overallTaskName;
	}
	
	public String getCurrentTaskName() {
		return currentTaskName;
	}
	
	public void setCurrentTaskName(String CurrentTaskName) {
		this.currentTaskName = CurrentTaskName;
	}
	
	public String getLog() {
		return this.logBuffer.toString();
	}

	public PrintWriter getLogWriter() {
		return logWriter;
	}
	
	public void appendInfoLog( String logMessage ) {
		if( isLogToConsole() )
			System.out.println( logMessage );
		else
			logger.finer( logMessage );
		getLogWriter().println( logMessage );
	}

	public void appendWarningLog( String logMessage ) {
		if( isLogToConsole() )
			System.out.println( logMessage );
		else
			logger.severe( logMessage );
		
		getLogWriter().println( logMessage );
	}

	public void appendErrorLog( String logMessage ) {
		if( isLogToConsole() )
			System.err.println( logMessage );
		else
			logger.severe( logMessage );
		
		getLogWriter().println( logMessage );
	}

	public void appendErrorLog( String message, Throwable e ) {
		if( isLogToConsole() ) {
			System.err.println( message );
			e.printStackTrace( System.err );
		} else
			logger.severe( message, e );
		
		getLogWriter().println( message );  
		e.printStackTrace( getLogWriter() );
	}
	
	public void appendErrorLog( Throwable e ) {
		if( isLogToConsole() )
			e.printStackTrace( System.err );
		else
			logger.severe( e );
		
		e.printStackTrace( getLogWriter() );
	}
	
}
