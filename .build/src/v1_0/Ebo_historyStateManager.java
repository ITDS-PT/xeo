package v1_0;

import java.io.Serializable;
import netgest.bo.runtime.*;
import java.sql.SQLException;
import java.util.*;

public class Ebo_historyStateManager extends netgest.bo.impl.states.versioningStates implements Serializable 
{
    private boObject object;
    public boObject getParent() 
    {
        return object;
    }
    

    public Ebo_historyStateManager( boObject xobject ) 
    {
        object = xobject;
    }
   
   
    public void setState_stateControl_active( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "stateControl" ).setValueString( "0" );    
    }
    
    public void setState_stateControl_inactive( ) throws boRuntimeException
    {
        object.getStateAttributes().get( "stateControl" ).setValueString( "1" );    
    }
    
    


        
    
}
