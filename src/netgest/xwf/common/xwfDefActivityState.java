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
        String toRet = obj.getStateAttribute("state").getValueString();

        if("active".equalsIgnoreCase(toRet))
        {
            toRet = obj.getStateAttribute("activeStatus").getValueString();
        }

        String xdir = obj.getBoDefinition().getStateNameRefer();
        return xdir + "/states/" + toRet;
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String rstate = obj.getStateAttribute("state").getValueString();
        if("active".equalsIgnoreCase(rstate))
        {
            rstate = obj.getStateAttribute("activeStatus").getValueString();
        }

        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = "";
        String toRet = rstate;
        if ("config".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.9");
        }
        else if ("ready".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.11");
        }
        else if ("running".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.13");
        }
        else if ("finished".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.15");
        }
        else if ("witherror".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.17");
        }
        else if ("inactive".equals(rstate))
        {
            tooltip = Messages.getString("xwfDefActivityState.19");
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet +
        ".gif' height=16 width=16 />";
    }
}
