/*Enconding=UTF-8*/
package netgest.xwf.common;

import netgest.bo.runtime.*;


/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public abstract class xwfDefActivityState extends boObjectState
{
    /**
 * 
 * @see 
 */
    public String getStateString(boObject obj)
        throws boRuntimeException
    {
        String toRet = obj.getStateAttribute("state").getValueString(); //$NON-NLS-1$

        if("active".equalsIgnoreCase(toRet)) //$NON-NLS-1$
        {
            toRet = obj.getStateAttribute("activeStatus").getValueString(); //$NON-NLS-1$
        }

        String xdir = obj.getBoDefinition().getStateNameRefer();
        return xdir + "/states/" + toRet; //$NON-NLS-1$
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String rstate = obj.getStateAttribute("state").getValueString(); //$NON-NLS-1$
        if("active".equalsIgnoreCase(rstate)) //$NON-NLS-1$
        {
            rstate = obj.getStateAttribute("activeStatus").getValueString(); //$NON-NLS-1$
        }

        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = ""; //$NON-NLS-1$
        String toRet = rstate;
        if ("config".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.9"); //$NON-NLS-1$
        }
        else if ("ready".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.11"); //$NON-NLS-1$
        }
        else if ("running".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.13"); //$NON-NLS-1$
        }
        else if ("finished".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.15"); //$NON-NLS-1$
        }
        else if ("witherror".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.17"); //$NON-NLS-1$
        }
        else if ("inactive".equals(rstate)) //$NON-NLS-1$
        {
            tooltip = Messages.getString("xwfDefActivityState.19"); //$NON-NLS-1$
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet + //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ".gif' height=16 width=16 />"; //$NON-NLS-1$
    }
}
