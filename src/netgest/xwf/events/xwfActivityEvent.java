/*Enconding=UTF-8*/
package netgest.xwf.events;


import java.math.BigDecimal;

import java.util.Date;

import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.faces.FacesHelper;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.bridgeHandler;
import netgest.xwf.common.*;
import netgest.bo.system.Logger;

/**
 * <p>Title: xwfActivityEvent </p>
 * <p>Description: </p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class xwfActivityEvent 
{
    private static Logger logger = Logger.getLogger("netgest.xwf.events.xwfActivityEvent");


    public static boolean beforeSaveTransferActivity(boObject transferActivity)
    {
        try
        {
            Controller control = transferActivity.getEboContext().getController();
            
            if("BasicController".equals(control.getName()))
            {
                return xwfActionHelper.transferProcess(transferActivity);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe("", e);
            transferActivity.addErrorMessage(e.getMessage());
            return false;
        }
        return true;
    }
    
    
    
    public static boolean beforeAddReceiveActivity(boObject object, boEvent event) throws boRuntimeException
    {        
        boolean result = true;
        long receiveInWait = object.getAttribute("receiveActivity").getValueLong();        
        long activityReceivedBoui = (( BigDecimal ) event.getValue()).longValue();
        if(receiveInWait != activityReceivedBoui)
        {
            boObject activityReceived = boObject.getBoManager().loadObject(object.getEboContext(), activityReceivedBoui);
            bridgeHandler bridge = activityReceived.getBridge("waitingResponse");
            if(bridge.haveBoui(object.getBoui()))
            {
                bridge.beforeFirst();
                boObject aux = null;
                while(bridge.next() && result)
                {
                    aux = bridge.getObject();
                    if(aux.getBoui() == receiveInWait)
                    {
                        bridge.remove();
                    }
                }
            }
        }
        return result;
    }

    public static void afterAddReceiveActivity(boObject object, boEvent event) throws boRuntimeException
    {        
        long activityReceivedBoui = (( BigDecimal ) event.getValue()).longValue();
        boObject activityReceived = boObject.getBoManager().loadObject(object.getEboContext(), activityReceivedBoui);
        bridgeHandler bridge = activityReceived.getBridge("waitingResponse");
        if(!bridge.haveBoui(object.getBoui()))
        {
            bridge.beforeFirst();
            bridge.add(object.getBoui());
        }
    }
    
    public static void afterAddWaitingResponse(boObject object, boEvent event) throws boRuntimeException
    {
        long waitingResponseBoui = (( BigDecimal ) event.getValue()).longValue();
        boObject waitingResponse = boObject.getBoManager().loadObject(object.getEboContext(), waitingResponseBoui);                
        waitingResponse.getAttribute("receiveActivity").setValueLong(object.getBoui());        
    }
    public static void afterRemoveWaitingResponse(boObject object, boEvent event) throws boRuntimeException
    {
        long waitingResponseBoui = (( BigDecimal ) event.getValue()).longValue();
        boObject waitingResponse = boObject.getBoManager().loadObject(object.getEboContext(), waitingResponseBoui);
        waitingResponse.getAttribute("receiveActivity").setValueString(null);        
    }    
    
    public static boolean beforeSave(boObject object) throws boRuntimeException
    {
        boolean result = true;
        boolean workDone = false;
        Date workDate = null;
        bridgeHandler wrk = object.getBridge("workHistory");
        AttributeHandler dur = object.getAttribute("duration");
        AttributeHandler journal = object.getAttribute("journal");
        AttributeHandler totalDuration = object.getAttribute("totalDuration");
        Date beginDate = object.getAttribute("histBeginDate").getValueDate();
        Date endDate = object.getAttribute("histEndDate").getValueDate();
        
        double v = dur.getValueDouble();

        if ((v != 0) && !Double.isNaN(v))
        {
            dur.setValueDouble(0);
            if(beginDate == null)
            {
                workDate = new java.util.Date();
            }
            else
            {
                workDate = beginDate;
            }
            workDone = true;       
        }
        else if( v == 0 &&
             beginDate != null &&
             endDate != null)
        {            
            if((endDate.getTime() - beginDate.getTime()) > 0)
            {
                v = (endDate.getTime() - beginDate.getTime())/60/1000;
                workDate = beginDate;
                workDone = true;
            }
        }
        if(workDone)
        {
            double xdurt = totalDuration.getValueDouble();
            if (Double.isNaN(xdurt))
            {
                xdurt = 0;
            }
            totalDuration.setValueDouble(xdurt + v);
        
            boObject it = wrk.addNewObject();
            it.getAttribute("duration").setValueDouble(v);
            if(journal!=null)
            {
              it.getAttribute("journal").setValueString(journal.getValueString());
              journal.setValueString(null);
            }
            if(object.getEboContext().getBoSession().getPerformerBoui() > 0)
            {
                it.getAttribute("performer").setValueLong(object.getEboContext().getBoSession().getPerformerBoui());
            }
            it.getAttribute("workDate").setValueDate(workDate);
            
            if(object.getAttribute("beginDate").getValueDate() == null)
            {
               object.getAttribute("beginDate").setValueDate(workDate); 
            }
            else
            {
                if(workDate.before(object.getAttribute("beginDate").getValueDate()))
                {
                    object.getAttribute("beginDate").setValueDate(workDate);
                }
            }
        }
        //vou limpar os campos
        object.getAttribute("histBeginDate").setValueDate(null);
        object.getAttribute("histEndDate").setValueDate(null);
        object.getAttribute("duration").setValueDouble(0);
        object.getAttribute("journal").setValueString(null);
        
        if(object.getEboContext().getController() == null || !"XwfController".equals(object.getEboContext().getController().getName()))
        {
            if("".equals(object.getAttribute("showTask").getValueString()))
            {
                object.getAttribute("showTask").setValueString("1");                
            }
            if("".equals(object.getStateAttribute("runningState").getValueString()))
            {
                object.getStateAttribute("runningState").setValueString("create");
            }
            if(object.getBoDefinition().getBoInterfaces() != null && 
                object.getBoDefinition().getBoInterfaces().length > 0)
            {
                FacesHelper.facesDispatcher(object);
            }
        }
        
        //verificar primeiro se não é um reassignamento se fôr não pode correr este código
        Boolean b = (Boolean)object.getEboContext().getBoSession().getProperty("settingReassign");
        if(b == null || !b.booleanValue())
        {
            reassignHistory(object);
        }
        
        return result;
    }
    
    private static boolean reassignHistory(boObject activity) throws boRuntimeException
    {
        //Vou verificar se houve alterações no performer
        histAssignedAndExecuter(activity, "performer", "reassigns");
        histAssignedAndExecuter(activity, "assignedQueue", "reassigns");
        return true;
    }
    
    private static void histAssignedAndExecuter(boObject activity, String attName, String histBridge) throws boRuntimeException
    {
        //Vou verificar se houve alterações no performer
        if( activity.getDataRow().getFlashBackRow() != null )
        {
            String actual = activity.getDataRow().getString( 
                                activity.getAttribute(attName).getDefAttribute().getDbName() );
            String original = activity.getDataRow().getFlashBackRow().getString( 
                                activity.getAttribute(attName).getDefAttribute().getDbName() );
            actual = (actual == null) ? "":actual;
            original = (original == null) ? "":original;
            if(!original.equals(actual))
            {
                //vou colocar o novo valor na bridge de responsáveis
                bridgeHandler bh = activity.getBridge(histBridge);
                boObject xwfReassign = bh.addNewObject();
                
                if("performer".equals(attName))
                {
                    xwfReassign.getAttribute("assignType").setValueString("2");
                }
                else
                {
                    xwfReassign.getAttribute("assignType").setValueString("1");
                }
                //estava associado a quem
                if(actual != null && !"".equals(actual))
                {
                    xwfReassign.getAttribute("from").setValueString(original);
                }
                xwfReassign.getAttribute("to").setValueString(actual);
                
                xwfReassign.getAttribute("reassigned").setValueString("1");
                    
            }
            
        }
    }
    
    public static boolean beforeSaveReassign(boObject reassign) throws boRuntimeException
    {
        //vou criar o histórico
        boObjectList list =  boObjectList.list(reassign.getEboContext(), "select xwfReassignHist where reassignBoui = " + reassign.getBoui(), 1, 1, true);
        list.beforeFirst();
        boObject reassignHist = null;
        if(list.next())
        {
            reassignHist = list.getObject();
        }
        else
        {
            reassignHist = boObject.getBoManager().createObject(reassign.getEboContext(), "xwfReassignHist");
        }
        Controller control =reassign.getEboContext().getController();
        String progLabel="", actvLabel="";
        long progBoui=-1, actvboui=-1;
        if(control != null && "XwfController".equalsIgnoreCase(control.getName()))
        {
            progBoui=((XwfController)control).getRuntimeProgramBoui();
            actvboui=((XwfController)control).getRuntimeActivityBoui();
            if(progBoui == -1 || actvboui == -1)
            {//Modo eliminação
                return true;
            }
            progLabel=((XwfController)control).getRuntimeProgram().getAttribute("label").getValueString(); 
            actvLabel=((XwfController)control).getRuntimeActivity().getAttribute("label").getValueString();
        }
        else
        {
            //tenho que descobrir como ir buscar o programa e a actividade
            boObject actv = reassign.getParent();
            if(actv == null)
            {//Modo eliminação
                return true;
            }
            progBoui=actv.getAttribute("program").getValueLong();
            actvboui=actv.getBoui();
            
            progLabel=actv.getAttribute("program").getObject().getAttribute("label").getValueString(); 
            actvLabel=actv.getAttribute("label").getValueString();
        }
        
        reassignHist.getAttribute("de").setValueLong(reassign.getAttribute("from").getValueLong());
        reassignHist.getAttribute("to").setValueLong(reassign.getAttribute("to").getValueLong());
        reassignHist.getAttribute("SYS_DTCREATE").setValueDate(reassign.getAttribute("SYS_DTCREATE").getValueDate());
        reassignHist.getAttribute("actvLabel").setValueString(actvLabel);
        reassignHist.getAttribute("progLabel").setValueString(progLabel);
        reassignHist.getAttribute("actvBoui").setValueLong(actvboui);
        reassignHist.getAttribute("progBoui").setValueLong(progBoui);
        reassignHist.getAttribute("assignType").setValueString(reassign.getAttribute("assignType").getValueString());
        reassignHist.getAttribute("description").setValueString(reassign.getAttribute("description").getValueString());
        reassignHist.getAttribute("reassignBoui").setValueLong(reassign.getBoui());
        reassign.getUpdateQueue().add(reassignHist, boObjectUpdateQueue.MODE_SAVE_FORCED);
        return true;
    }
}