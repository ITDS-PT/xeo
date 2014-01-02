/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.data.*;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public abstract class ObjectMap 
{

    public boolean onInsertTrigger( EboContext ctx, boObject object, KeyReference keys ) throws boRuntimeException
    {
        return true;
    }
    
    public boolean onUpdateTrigger(  EboContext ctx, boObject object, KeyReference keys  ) throws boRuntimeException
    {
        return true;
    }
    
    public boolean onDeleteTrigger(  EboContext ctx, boObject object, KeyReference keys  ) throws boRuntimeException
    {
        return true;
    }
    public boolean onUpdateObject( EboContext ctx, boObject object, KeyReference keys ) throws boRuntimeException
    {
        return true;
    }
    public boolean onInsertObject( EboContext ctx, boObject object, KeyReference keys ) throws boRuntimeException
    {
        return true;
    }
    public boolean onDeleteObject( EboContext ctx, boObject object, KeyReference keys ) throws boRuntimeException
    {
        return true;
    }
    
    
}