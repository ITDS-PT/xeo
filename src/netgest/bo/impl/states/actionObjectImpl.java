/*Enconding=UTF-8*/
package netgest.bo.impl.states;

import java.util.Date;
import java.util.Enumeration;

import netgest.bo.impl.Ebo_AnnounceImpl;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boAttributesArray;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectState;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.workflow.WF;

/**
 *
 *
 * @author João Paulo Trindade Carreira
 * @author JMF
 * @version 2.0
 *
 */
public abstract class actionObjectImpl extends boObjectState
{
    public void onCreate(boObject activity)
        throws boRuntimeException
    {
        //      
        // incializa o estado
        boObject[] parents = activity.getParents();
        WF.activity_init(activity);
    }

    public void onSave(boObject object)
        throws boRuntimeException
    {
        String pstate = object.getStateAttribute("primaryState").getValueString();
        String message = "";
        if (pstate.equals("create"))
        {
            String pst1 = object.getStateAttribute("createdStatus").getValueString();
            String pst2 = object.getStateAttribute("createdAlerts").getValueString();

            if (pst1.equals("started"))
            {
                if (object.exists())
                {
                    message = "Pronta a ser aceite";
                }
                else
                {
                    message = "NOVA.Pronta a ser aceite";
                }
            }
            else
            {
                if (object.exists())
                {
                    message = "Ficou em espera";
                }
                else
                {
                    message = "NOVA.Ficou em espera";
                }
            }
        }
        else if (pstate.equals("open"))
        {
            String pst1 = object.getStateAttribute("openStatus").getValueString();
            String pst2 = object.getStateAttribute("openAlerts").getValueString();

            if (pst1.equals("waitFor"))
            {
                if (object.exists())
                {
                    message = "Aceite.Ficou em espera";
                }
                else
                {
                    message = "NOVA.Aceite.Ficou em espera";
                }
            }
            else if (pst1.equals("hold"))
            {
                
                if (object.exists())
                {
                    message = "Aceite.Ficou em suspensa";
                }
                else
                {
                    message = "NOVA.Aceite.Ficou suspensa";
                }
            }
            else
            {
                if (object.exists())
                {
                    message = "Aceite.Pronta a executar";
                }
                else
                {
                    message = "NOVA.Aceite.Pronta a executar";
                }
            }

            
        }
        else if (pstate.equals("close"))
        {
            
        }
        else if (pstate.equals("archive"))
        {
            
        }

        if (message.length() > 0)
        {
            Ebo_AnnounceImpl.addAnnounce(
                object, object.getAttribute("assignedQueue").getObject(), object, message);
        }
    }

    public boolean onValid(boObject object)
        throws boRuntimeException
    {
        //object.addErrorMessage(" Não pode fechar sem guardar a data de fim ");
        if(object.getMode() != boObject.MODE_EDIT_TEMPLATE)
        {
            if(WF.isRequireWithCondition(object, 1, true))
            {
                return false;
            }
        }
        return true;
    }

    public void onLoad(boObject object)
        throws boRuntimeException
    {
        String pstate = object.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();

        if (pstate.equals(WF.STATE_ARCHIVE) || pstate.equals(WF.STATE_CLOSE))
        {
            object.setDisabled();
            boAttributesArray xatr = object.getAttributes();
            Enumeration xenum = xatr.elements();
            while (xenum.hasMoreElements())
            {
                AttributeHandler xat;
                xat = ( AttributeHandler ) xenum.nextElement();
                xat.setDisabled();
            }
        }
    }

    public String[] getStateMethods(boObject object)
        throws boRuntimeException
    {
        return WF.activity_getAvailableActions(object);
    }

   /* public void activity_setValuesForAccept() 
        throws boRuntimeException
    {
        boObject activity = this.getParent();
        long perf = activity.getEboContext().getBoSession().getPerformerBoui();
        AttributeHandler performer = activity.getAttribute("performer");
        if (performer.getValueObject() == null)
        {
            performer.setValueLong(perf, AttributeHandler.INPUT_FROM_INTERNAL);
        }

        AttributeHandler beginDate = activity.getAttribute("beginDate");
        AttributeHandler endDate = activity.getAttribute("endDate");

        if (beginDate.getValueObject() == null)
        {
            if (endDate.getValueDate() == null)
            {
                beginDate.setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
            else
            {
                beginDate.setValueDate(endDate.getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
        }
    }*/

    public void activity_Take()
        throws boRuntimeException
    {
        WF.activity_setValuesForAccept(this.getParent());

        WF.activity_Open(this.getParent());
    }

    public void activity_unTake()
        throws boRuntimeException
    {                
        WF.activity_unTake(this.getParent());
    }
    public void activity_CompleteCancel()
        throws boRuntimeException
    {
        boObject activity = this.getParent();

        AttributeHandler beginDate = activity.getAttribute("beginDate");
        AttributeHandler endDate = activity.getAttribute("endDate");
        if (endDate.getValueDate() == null)
        {
            endDate.setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }

        WF.activity_Complete(activity, WF.STATE_CANCEL);
    }

    public void activity_CompleteSoft()
        throws boRuntimeException
    {
        boObject activity = this.getParent();

        AttributeHandler beginDate = activity.getAttribute("beginDate");
        AttributeHandler endDate = activity.getAttribute("endDate");
        if (endDate.getValueDate() == null)
        {
            endDate.setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        WF.activity_Complete(activity, WF.STATE_SOFTCLOSE);   
    }

    public void activity_CompleteHard()
        throws boRuntimeException
    {
        boObject activity = this.getParent();

        AttributeHandler beginDate = activity.getAttribute("beginDate");
        AttributeHandler endDate = activity.getAttribute("endDate");
        if (endDate.getValueDate() == null)
        {
            endDate.setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }

        WF.activity_Complete(activity, WF.STATE_HARDCLOSE);
    }

    public void activity_Hold()
        throws boRuntimeException
    {
        WF.activity_Suspend(this.getParent());
    }

    public void activity_Unhold()
        throws boRuntimeException
    {
        WF.activity_unSuspend(this.getParent());
    }

    public void activity_Reopen()
        throws boRuntimeException
    {
        AttributeHandler endDate = this.getParent().getAttribute("endDate");
        if (endDate.getValueDate() != null)
        {
            endDate.setValueObject(null, AttributeHandler.INPUT_FROM_INTERNAL);
        }

        WF.activity_Open(this.getParent());
    }

    public String getStateString(boObject obj)
        throws boRuntimeException
    {
        String pstate = obj.getStateAttribute("primaryState").getValueString();
        String xdir = obj.getBoDefinition().getStateNameRefer();

        String toRet = "";
        if (pstate.equals("create"))
        {
            String pst1 = obj.getStateAttribute("createdStatus").getValueString();
            String pst2 = obj.getStateAttribute("createdAlerts").getValueString();
            toRet = "create_" + pst1 + "-" + pst2;
        }
        else if (pstate.equals("open"))
        {
            String pst1 = obj.getStateAttribute("openStatus").getValueString();
            String pst2 = obj.getStateAttribute("openAlerts").getValueString();

            toRet = "open_" + pst1 + "-" + pst2;
        }
        else if (pstate.equals("close"))
        {
            String pst1 = obj.getStateAttribute("closeStatus").getValueString();
            toRet = "close_" + pst1;
        }
        else if (pstate.equals("archive"))
        {
            toRet = pstate;
        }

        return xdir + "/states/" + toRet;
    }

    public String getStateHTMLICON(boObject obj)
        throws boRuntimeException
    {
        String pstate = obj.getStateAttribute("primaryState").getValueString();
        String xdir = obj.getBoDefinition().getStateNameRefer();
        String tooltip = "";
        String toRet = "";
        if (pstate.equals("create"))
        {
            String pst1 = obj.getStateAttribute("createdStatus").getValueString();
            String pst2 = obj.getStateAttribute("createdAlerts").getValueString();

            toRet = "create_" + pst1 + "-" + pst2;
            if (pst1.equals("started"))
            {
                tooltip = " Actividade ainda não aceite ";
            }
            else
            {
                tooltip = " Actividade ainda não aceite e dependente de outras actividades ";
            }
        }
        else if (pstate.equals("open"))
        {
            String pst1 = obj.getStateAttribute("openStatus").getValueString();
            String pst2 = obj.getStateAttribute("openAlerts").getValueString();

            if (pst1.equals("waitFor"))
            {
                tooltip = " Actividade aceite mas depende de outras para poder ser finalizada ";
            }
            else if (pst1.equals("hold"))
            {
                tooltip = " Actividade aceite e suspensa ";
            }
            else
            {
                tooltip = " Actividade aceite e a decorrer normalmente ";
            }

            toRet = "open_" + pst1 + "-" + pst2;
        }
        else if (pstate.equals("close"))
        {
            String pst1 = obj.getStateAttribute("closeStatus").getValueString();
            toRet = "close_" + pst1;
            tooltip = " Actividade fechada ";
            if (pst1.equals("cancel"))
            {
                tooltip += " ( cancelada ) ";
            }
        }
        else if (pstate.equals("archive"))
        {
            toRet = pstate;
        }

        return "<IMG title='" + tooltip + "' src='resources/" + xdir + "/states/" + toRet +
        ".gif' height=16 width=16 />";
    }

    public void onDestroy(boObject object)
        throws boRuntimeException
    {
        Ebo_AnnounceImpl.removeAnnouncer(object);
    }
}
