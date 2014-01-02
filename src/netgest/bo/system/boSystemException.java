/*Enconding=UTF-8*/
package netgest.bo.system;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boSystemException extends RuntimeException 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
     
     
     
    public boSystemException( int type, String reason )
    {
        
    }
    private static final String getBoExceptionMessage( int type, String reason )
    {
        return reason;
    }
}