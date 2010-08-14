/*Enconding=UTF-8*/
package netgest.xwf.common;
import netgest.bo.runtime.*;


/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public abstract class xwfApprovalState extends boObjectState
{
    /**
 * 
 * @see 
 */
    public String getStateString(boObject obj)
        throws boRuntimeException
    {
        String toRet = obj.getStateAttribute("approvalState").getValueString();

        String xdir = obj.getBoDefinition().getStateNameRefer();
        return xdir + "/states/" + toRet;
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String rstate = obj.getStateAttribute("approvalState").getValueString();
        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = "";
        String toRet = rstate;
        if (rstate.equals("evaluating"))
        {
            tooltip = Messages.getString("xwfApprovalState.5");
        }
        else if (rstate.equals("rejected"))
        {
            tooltip = Messages.getString("xwfApprovalState.7");
        }
        else if (rstate.equals("approved"))
        {
            tooltip = Messages.getString("xwfApprovalState.9");
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet +
        ".gif' height=16 width=16 />";
    }
    public void onSave( boObject object ) throws boRuntimeException
    {
        String rstate = object.getStateAttribute("approvalState").getValueString();
        if ( rstate== null || "".equals( rstate) )
        {
            object.getStateAttribute("approvalState").setValueString("evaluating");
        }
    }
}
