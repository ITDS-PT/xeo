/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public interface boSchedule 
{
    public void setParameter(String parameter );
    public boolean doWork(EboContext ctx, boObject objectSchedule ) throws boRuntimeException ;
}