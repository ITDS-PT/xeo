package v1_0;

import java.io.Serializable;
import netgest.bo.runtime.*;
import java.sql.SQLException;
import java.util.*;

public class xwfProgramRuntimeStateManager extends netgest.xwf.core.xwfProgramRuntimeImpl implements Serializable 
{
    private boObject object;
    public boObject getParent() 
    {
        return object;
    }
    

    public xwfProgramRuntimeStateManager( boObject xobject ) 
    {
        object = xobject;
    }
   
   
    public void setState_runningState_create( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "runningState" ).setValueString( "0" );    
    }
    
    public void setState_runningState_open( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "runningState" ).setValueString( "1" );    
    }
    
    public void setState_runningState_reopen( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "runningState" ).setValueString( "10" );    
    }
    
    public void setState_runningState_suspend( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "runningState" ).setValueString( "10" );    
    }
    
    public void setState_runningState_close( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "runningState" ).setValueString( "90" );    
    }
    
    


        
    
}
