/*Enconding=UTF-8*/
package netgest.bo.impl.states;
import java.sql.*;

import java.util.*;

import netgest.bo.def.boDefClsState;
import netgest.bo.def.boDefHandler;
import netgest.bo.impl.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;

import netgest.utils.*;
/**
 * 
 * @Company Enlace3
 * @author Luís Eduardo Moscoso Barreira
 * @version 1.0
 * @since 
 */
public abstract class versioningStates extends boObjectState 
{
 
    public void onCreate( boObject object ) throws boRuntimeException
    {
    }

    public void onDestroy( boObject object ) throws boRuntimeException
    {
    }
    
    public void onSave( boObject object ) throws boRuntimeException
    {
        if(object.getStateAttribute("stateControl") != null)
        {
           if(!object.getStateAttribute("stateControl").getValue().equalsIgnoreCase("inactive")) 
                object.getStateAttribute("stateControl").setValue("active");
        }
    }    
    
    public String[] getStateMethods( boObject object ) throws boRuntimeException
    {
      String[] ret=null;
        String pstate = object.getStateAttribute( "stateControl" ).getValueString();
        if (securityRights.canDelete(object.getEboContext(),object.getName()) && securityOPL.canDelete(object))
        {
          if( pstate.equals("active") ) 
          {
              ret = new String[] { 
              object.getStateAttribute( "stateControl" ).getChildState( "inactive" ).getDefinition().getLabelAction()  + ";setState_stateControl_inactive"};
                 
          }
          else if( pstate.equals("inactive"))
          {
              ret = new String[] { 
              object.getStateAttribute( "stateControl" ).getChildState( "active" ).getDefinition().getLabelAction()  + ";setState_stateControl_active"};
           
          }
        }
        return ret;
    }
    
    public String getStateString( boObject object ) throws boRuntimeException
    {
        if(object.getStateAttribute("stateControl") == null)
            return "stateControl/active";
            
        if(object.getStateAttribute("stateControl").getValue().equalsIgnoreCase("inactive"))
            return "stateControl/inactive";
        else
            return "stateControl/active";
    }
    
    public boolean getCanRemove( boObject object ) throws boRuntimeException
    {
        if(object.getStateAttribute("stateControl") == null)
            return true;
        if(object.getStateAttribute("stateControl").getValue().equalsIgnoreCase("inactive"))
            return true;
        else if (object.getBoDefinition().getStateNameRefer()!=null && object.getBoDefinition().getStateNameRefer().equals("Ebo_history"))
            return true;
        else
            return false;
    }
    
     public String getStateHTMLICON( boObject object ) throws boRuntimeException
    {
        return ""; 
    }

    
}