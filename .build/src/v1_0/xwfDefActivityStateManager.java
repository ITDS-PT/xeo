package v1_0;

import java.io.Serializable;
import netgest.bo.runtime.*;
import java.sql.SQLException;
import java.util.*;

public class xwfDefActivityStateManager extends netgest.xwf.common.xwfDefActivityState implements Serializable 
{
    private boObject object;
    public boObject getParent() 
    {
        return object;
    }
    

    public xwfDefActivityStateManager( boObject xobject ) 
    {
        object = xobject;
    }
   
   
    public void setState_state_active( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "state" ).setValueString( "1" );    
    }
    
    public void setState_state_inactive( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "state" ).setValueString( "0" );    
    }
    
    
   
    public void setState_activeStatus_ready( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "activeStatus" ).setValueString( "0" );    
    }
    
    public void setState_activeStatus_running( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "activeStatus" ).setValueString( "1" );    
    }
    
    public void setState_activeStatus_finished( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "activeStatus" ).setValueString( "3" );    
    }
    
    public void setState_activeStatus_witherror( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "activeStatus" ).setValueString( "2" );    
    }
    
    


        
    
}
