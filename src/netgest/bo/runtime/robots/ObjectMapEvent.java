/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.runtime.EboContext;
import netgest.bo.def.boDefHandler;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface ObjectMapEvent 
{
    public boolean beforeSync( EboContext ctx, boDefHandler def );
    public void afterSync( EboContext ctx, boDefHandler def );
}