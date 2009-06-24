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
        String toRet = obj.getStateAttribute("approvalState").getValueString(); //$NON-NLS-1$

        String xdir = obj.getBoDefinition().getStateNameRefer();
        return xdir + "/states/" + toRet; //$NON-NLS-1$
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String rstate = obj.getStateAttribute("approvalState").getValueString(); //$NON-NLS-1$
        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = ""; //$NON-NLS-1$
        String toRet = rstate;
        if (rstate.equals("evaluating")) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfApprovalState.5"); //$NON-NLS-1$
        }
        else if (rstate.equals("rejected")) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfApprovalState.7"); //$NON-NLS-1$
        }
        else if (rstate.equals("approved")) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfApprovalState.9"); //$NON-NLS-1$
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ".gif' height=16 width=16 />"; //$NON-NLS-1$
    }
    public void onSave( boObject object ) throws boRuntimeException
    {
        String rstate = object.getStateAttribute("approvalState").getValueString(); //$NON-NLS-1$
        if ( rstate== null || "".equals( rstate) ) //$NON-NLS-1$
        {
            object.getStateAttribute("approvalState").setValueString("evaluating"); //$NON-NLS-1$ //$NON-NLS-2$
        }
    }
}
