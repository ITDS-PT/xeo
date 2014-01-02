/*Enconding=UTF-8*/
package netgest.bo.utils.rebuilder;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public abstract class OperationStatus extends Thread
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public String operationName;
    public long initValue;
    public long endValue;
    public long currentValue;
    public String currentOperation;
    public Throwable operationError;
    
    public StringBuffer log = new StringBuffer(  ); 
    
    public synchronized void logln( String string )
    {
        log.append( string ).append('\n');
    }
    public synchronized void log( String string )
    {
        log.append( string );
    }

    public synchronized void loggreenln( String string )
    {
        log.append("<b class='green' >");
        log.append( string ).append('\n');
        log.append("</b>");
    }
    public synchronized void loggreen( String string )
    {
        log.append("<b class='green'>");
        log.append( string );
        log.append("</b>");
    }

    public synchronized void logerrln( String string )
    {
        log.append("<b class='red' >");
        log.append( string ).append('\n');
        log.append("</b>");
    }
    public synchronized void logerr( String string )
    {
        log.append("<b class='red'>");
        log.append( string );
        log.append("</b>");
    }
    
    public synchronized void logReplace( String string, int chars )
    {
    
        log.replace( log.length() - chars, log.length(), string );
    }
    
    public synchronized String clearLog()
    {
        String ret = log.toString();
        log = new StringBuffer();
        return ret;
    }
    public synchronized String getLog()
    {
        return log.toString();
    }
}