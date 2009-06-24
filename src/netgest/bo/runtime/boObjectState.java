/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.*;
import java.util.Hashtable;
import java.util.Vector;
import netgest.bo.def.boDefHandler;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 */
public abstract class boObjectState 
{
    public abstract boObject getParent(); 

    public void fireEventOnLoad( boObject object )  throws boRuntimeException
    {
        onLoad( object );        
    }
    public void fireEventOnSave( boObject object )  throws boRuntimeException
    {
        onSave( object );
    }
    public boolean fireEventOnValid( boObject object ) throws boRuntimeException
    {
        return onValid( object );
        
    }
    public void fireEventOnCreate( boObject object ) throws boRuntimeException
    {
        onCreate ( object );
    }

    public void fireEventOnDestroy( boObject object ) throws boRuntimeException
    {
        onDestroy ( object );
    }

    
    public void onCreate( boObject object ) throws boRuntimeException
    {
    }

    public void onDestroy( boObject object ) throws boRuntimeException
    {
    }


    public void onSave( boObject object ) throws boRuntimeException
    {
    }

    
    public boolean onValid( boObject object ) throws boRuntimeException
    {
        return true;
    }
    
    public void onLoad( boObject object ) throws boRuntimeException
    {
    }
    
    public String[] getStateMethods( boObject object ) throws boRuntimeException
    {
        return null;
    }
    
    public String getStateString( boObject object ) throws boRuntimeException
    {
        return "";
    }
    
     public String getStateHTMLICON( boObject object ) throws boRuntimeException
    {
        return "";
    }
    
     public boolean getCanRemove( boObject object ) throws boRuntimeException
    {
        return true;
    }
    
    
}