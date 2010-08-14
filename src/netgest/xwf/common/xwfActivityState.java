/*Enconding=UTF-8*/
package netgest.xwf.common;

import netgest.bo.runtime.*;


/**
 * 
 * @author JMF
 * @version 
 * @see 
 */
public abstract class xwfActivityState extends boObjectState
{
    /**
 * 
 * @see 
 */
    public String getStateString(boObject obj)
        throws boRuntimeException
    {
        String toRet = obj.getStateAttribute("runningState").getValueString();

        String xdir = obj.getBoDefinition().getStateNameRefer();
        return xdir + "/states/" + toRet;
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String rstate = obj.getStateAttribute("runningState").getValueString();
        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = "";
        String toRet = rstate;
        if (rstate.equals("create"))
        {
            tooltip = Messages.getString("xwfActivityState.5");
        }
        else if (rstate.equals("open"))
        {
            tooltip = Messages.getString("xwfActivityState.7");
        }
        else if (rstate.equals("close"))
        {
            tooltip = Messages.getString("xwfActivityState.9");
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet +
        ".gif' height=16 width=16 />";
    }
}
