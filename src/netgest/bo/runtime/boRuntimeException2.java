/*Enconding=UTF-8*/
package netgest.bo.runtime;
import netgest.utils.*;
import org.apache.log4j.Logger;

/**
 * 
 * @author JMF
 */
public final class boRuntimeException2 extends RuntimeException
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.boRuntimeException2");
    
    public boRuntimeException2()
    {
    super();
    logger.fatal( " execpção sem mensagem!!! " );  
    
	
    
    }

    public boRuntimeException2(String message) {
        
        super(message);
        logger.fatal( message );
    
    }

    public boRuntimeException2(String message, Throwable cause) {
        
        super(message, cause);
        logger.fatal( message );
    }

    public boRuntimeException2(Throwable cause) {
        
        super(cause);
        logger.fatal( cause.toString() );
    }
}