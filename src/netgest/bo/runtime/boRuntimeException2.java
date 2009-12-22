/*Enconding=UTF-8*/
package netgest.bo.runtime;
import netgest.utils.*;
import netgest.bo.system.Logger;

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
    logger.severe( " execpção sem mensagem!!! " );  
    
	
    
    }

    public boRuntimeException2(String message) {
        
        super(message);
        logger.severe( message );
    
    }

    public boRuntimeException2(String message, Throwable cause) {
        
        super(message, cause);
        logger.severe( message );
    }

    public boRuntimeException2(Throwable cause) {
        
        super(cause);
        logger.severe( cause.toString() );
    }
}