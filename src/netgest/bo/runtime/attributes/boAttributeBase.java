/*Enconding=UTF-8*/
package netgest.bo.runtime.attributes;
import netgest.bo.def.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.AttributeHandler;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public abstract class boAttributeBase extends AttributeHandler
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */

    public boAttributeBase( boObject parent, boDefAttribute def )
    {
        super( parent, def );
    }
}