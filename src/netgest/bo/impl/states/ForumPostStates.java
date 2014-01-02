/*Enconding=UTF-8*/
package netgest.bo.impl.states;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public abstract class ForumPostStates extends netgest.bo.runtime.boObjectState
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public void onSave(boObject object) throws boRuntimeException
    {
        // TODO:  Override this netgest.bo.runtime.boObjectState method
        boolean needApproval = false;
        String ps = object.getStateAttribute("primaryState").getValue();        
        if( object.getAttribute("forumtopic").getValueObject() != null )
        {
            boObject topic = object.getAttribute("forumtopic").getObject();
            if( topic.getAttribute("category").getValueObject() != null )
            {
                boObject cat = topic.getAttribute("category").getObject();
                if( cat.getAttribute("needapproval").getValueObject() != null )
                {
                    needApproval = cat.getAttribute("needapproval").getValueString().equals("1"); 
                }
            }
        }
        if( needApproval )
        {
            if( ps.equals("created") || ps.length() == 0 )
            {
                object.getStateAttribute("primaryState").setValue("created");
                object.getStateAttribute("primaryState").getCurrentState().getChildState("createdStatus").setValue("waitingForApproval");
            }
        }
        else
        {
            if( ps.equals("open") || ps.length() == 0 )
            {
                object.getStateAttribute("primaryState").setValue("open");
                object.getStateAttribute("primaryState").getCurrentState().getChildState("openStatus").setValue("approved");
            }
        }
        
//        if ( object.getAttribute("forumtopic").getValueObject() != null )
//        {
//            boObject topic = object.getAttribute("forumtopic").getObject();
//            String ps = object.getStateAttribute("primaryState").getValue();
//            if( topic.getAttribute("performer").getValueObject() != null )
//            {
//                if( ps.equals("created") || ps.length() == 0 )
//                {
//                    object.getStateAttribute("primaryState").setValue("created");
//                    object.getStateAttribute("primaryState").getCurrentState().getChildState("createdStatus").setValue("waitingForApproval");
//                }
//            }
//            else
//            {
//                if( ps.equals("open") || ps.length() == 0 )
//                {
//                    object.getStateAttribute("primaryState").setValue("open");
//                    object.getStateAttribute("primaryState").getCurrentState().getChildState("openStatus").setValue("approved");
//                }
////                String os = object.getStateAttribute("primaryState").getCurrentState().getChildState("openStatus").getValue();
////                if( os.length() == 0 && ps.equals("open") )
////                {
////                    object.getStateAttribute("primaryState").getCurrentState().getChildState("openStatus").setValue("approved");
////                }
//            }
//        }
    }

}