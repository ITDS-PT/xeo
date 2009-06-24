/*Enconding=UTF-8*/
package netgest.xwf.common;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;

import netgest.xwf.EngineGate;
import netgest.xwf.core.xwfMessage;

/**
 * Classe onde estão registadas as funções do WorkFlow (XWF)
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public final class xwfFunctions 
{
  /**
   * Altera o os dados de uma actividade, com base no seu estado anterior. 
   * @param activityBoui boui da actividade a alterar.
   * @param state novo estado da actividade.    
   */     
    public static void setActivityData(boObject activity, String state) throws boRuntimeException
    {    
        Date date = new Date();
        Date beginDate = null;
//        boObject activity =  manager.getObject(activityBoui);        
        if(XwfKeys.ACTION_CLOSE_KEY.equals(state) || XwfKeys.ACTION_SEND_KEY.equals(state))
        {    
            if("create".equals(activity.getStateAttribute("runningState").getValueString()))
            {
                beginDate = activity.getAttribute("beginDate").getValueDate(); //$NON-NLS-1$
                if(beginDate == null)
                {                    
                    String duration = activity.getAttribute("duration").getValueString(); //$NON-NLS-1$
                    if(duration != null && !"".equals(duration)) //$NON-NLS-1$
                    {
                        Calendar c = Calendar.getInstance();
                        String[] realDuration = duration.split("\\."); //$NON-NLS-1$
                        if(realDuration.length == 1)
                        {
                            int m = Integer.parseInt(duration);                                                                    
                            c.set(Calendar.MINUTE,c.get(Calendar.MINUTE) - m);                                                     
                        }
                        else 
                        {
                            int m = Integer.parseInt(realDuration[0]);                                                                    
                            c.set(Calendar.MINUTE,c.get(Calendar.MINUTE) - m);

                            int s = Integer.parseInt(realDuration[1]);      
                            s = s*30/5;
                            c.set(Calendar.SECOND,c.get(Calendar.SECOND) - s);                                                                                 
                            
                        }
                        activity.getAttribute("beginDate").setValueDate(c.getTime()); //$NON-NLS-1$
                    }   
                    else
                    {
                        activity.getAttribute("beginDate").setValueDate(date);   //$NON-NLS-1$
                    }
                }                
                activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui());                 //$NON-NLS-1$
            }
            
            activity.getAttribute("percentComplete").setValueString("100");
            if("xwfWaitResponse".equals(activity.getName()))
            {
                boObject object = null;
                boObject receiveActivity = activity.getAttribute("receiveActivity").getObject();   //$NON-NLS-1$
                if(receiveActivity != null)
                {
                    AttributeHandler attr = receiveActivity.getAttribute("message"); //$NON-NLS-1$
                    if(attr != null)
                    {
                        object = attr.getObject();
                        if(object != null)
                        {
                            attr = object.getAttribute("value"); //$NON-NLS-1$
                            if(attr != null)
                            {
                                object = attr.getObject();
                                if(object != null)
                                {
                                    attr = object.getAttribute(xwfHelper.VALUE_OBJECT);
                                    if(attr != null)
                                    {
                                        object = attr.getObject();
                                        if(object != null)
                                        {
                                            Date dtdoc = object.getAttribute("dtdoc").getValueDate();  //$NON-NLS-1$
                                            if(dtdoc != null)
                                            {
                                                date = dtdoc;
                                            }                                        
                                        }
                                    }
                                }                        
                            }
                        }
                    }
                }
                activity.getAttribute("endDate").setValueDate(date); //$NON-NLS-1$
            }
            else
            {
                activity.getAttribute("endDate").setValueDate(date);    //$NON-NLS-1$
            }
//            manager.finishedStep(activityBoui);   
        }
        else if(XwfKeys.ACTION_CANCEL_KEY.equals(state))
        {
            if("create".equals(activity.getStateAttribute("runningState").getValueString())) //$NON-NLS-1$ //$NON-NLS-2$
            {
                beginDate = activity.getAttribute("beginDate").getValueDate(); //$NON-NLS-1$
                if(beginDate == null)
                {
                    activity.getAttribute("beginDate").setValueDate(date);    //$NON-NLS-1$
                }                             
                activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui());                 //$NON-NLS-1$
            }
                    
            activity.getAttribute("endDate").setValueDate(date);
//            manager.finishedStep(activityBoui);                        
        }        
        else if(XwfKeys.ACTION_OPEN_KEY.equals(state))
        {
            activity.getAttribute("beginDate").setValueDate(date); //$NON-NLS-1$
            activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui()); //$NON-NLS-1$
            activity.getStateAttribute("runningState").setValueString(state); //$NON-NLS-1$
//            getManager().recalcFormulas();
//            getBoManager().updateObject(activity);            
        }              
    }    
  /**
   * Altera label da actividade com base em informações da na mesma e dos objectos no contexto dessa mesma actividade. 
   * @param engine ligação ao core do workflow.
   * @param activity a xwfActivity a alterar.
   */        
    public static void setIntelligentLabel(EngineGate engine, boObject activity) throws boRuntimeException
    {
        setIntelligentLabel(engine.getBoManager(),engine.getProgramRuntime(),activity);
    }
    
    private static boolean isKnowledge(String label)
    {
        if(label != null && label.length() > 0)
        {
            if(label.toUpperCase().startsWith(Messages.getString("xwfFunctions.77")) ||  //$NON-NLS-1$
               label.toUpperCase().startsWith(Messages.getString("xwfFunctions.78")) || //$NON-NLS-1$
               label.toUpperCase().indexOf(Messages.getString("xwfFunctions.79")) > -1 //$NON-NLS-1$
            )
            {
                return true;
            }
        }
        return false;
    }
  /**
   * Altera label da actividade com base em informações da na mesma e dos objectos no contexto dessa mesma actividade. 
   * @param manager gestor do workflow.
   * @param program o xwfProgramRuntime em contexto.
   * @param activity a xwfActivity a alterar.
   */ 
    public static void setIntelligentLabel(xwfBoManager manager, boObject program,boObject activity) throws boRuntimeException
    {
        setIntelligentLabel(manager, program,activity, false);
    }
    public static void setIntelligentLabel(xwfBoManager manager, boObject program,boObject activity, boolean isCC) throws boRuntimeException
    {
        if(activity != null)
        {
            StringBuffer label = new StringBuffer();
            AttributeHandler attrLabel = activity.getAttribute("label"); //$NON-NLS-1$
            String activityName = activity.getName();
            boObject message = null;
            boObject object = null;              
            Date dtdoc = null;
            AttributeHandler attr = null;                                
            String close = null;            
                        
            if("xwfActivityReceive".equals(activityName) || //$NON-NLS-1$
               "xwfCreateReceivedMessage".equals(activityName) || //$NON-NLS-1$
               "xwfActivityCreateMessage".equals(activityName) //$NON-NLS-1$
               )
            {
                if("xwfActivityReceive".equals(activityName) && !isCC) //$NON-NLS-1$
                {
                    isCC = isKnowledge(activity.getAttribute("label").getValueString()); //$NON-NLS-1$
                }
                attr = activity.getAttribute("message");     //$NON-NLS-1$
                if(attr != null)
                {
                    object = attr.getObject();
                    message = manager.getValueBoObject(object.getAttribute("value").getObject()); //$NON-NLS-1$
                    setProgramLabel(program,message);
                    
                    if(attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
                    {
                        close = activity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
                        if(!"close".equals(close)) //$NON-NLS-1$
                        {
                            if( "xwfCreateReceivedMessage".equals(activityName)|| //$NON-NLS-1$
                                "xwfActivityCreateMessage".equals(activityName)) //$NON-NLS-1$
                            {
                                label.append(Messages.getString("xwfFunctions.76")); //$NON-NLS-1$
                            }
                            else
                            {
                                if("open".equals(close)) //$NON-NLS-1$
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.74")); //$NON-NLS-1$
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.75")); //$NON-NLS-1$
                                    }
                                }
                                else
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.73")); //$NON-NLS-1$
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.97")); //$NON-NLS-1$
                                    }
                                }
                            }
                        }
                        label.append(xwfMessage.getPreferedMediaLabel(message));
                        if("close".equals(close)) //$NON-NLS-1$
                        {
                            String preferedMedia = message.getAttribute("preferedMedia").getValueString(); //$NON-NLS-1$
                            if( "xwfCreateReceivedMessage".equals(activityName) || //$NON-NLS-1$
                                "xwfActivityCreateMessage".equals(activityName)) //$NON-NLS-1$
                            {
                                if("Letter".equals(preferedMedia) || //$NON-NLS-1$
                                   "Prefered".equals(preferedMedia)) //$NON-NLS-1$
                                {
                                    label.append(" criada "); //$NON-NLS-1$
                                }
                                else
                                {
                                    label.append(" criado "); //$NON-NLS-1$
                                }                        
                                
                            }
                            else
                            {                            
                                if("Letter".equals(preferedMedia) || //$NON-NLS-1$
                                   "Prefered".equals(preferedMedia)) //$NON-NLS-1$
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.108")); //$NON-NLS-1$
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.109")); //$NON-NLS-1$
                                    }
                                }
                                else
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.110")); //$NON-NLS-1$
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.111")); //$NON-NLS-1$
                                    }
                                }
                            }                    
                        }                    
                        label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        boObject from = message.getAttribute("from").getObject(); //$NON-NLS-1$
                        if(from != null)
                        {
                            label.append(Messages.getString("xwfFunctions.116")).append(from.getAttribute("name").getValueString()); //$NON-NLS-1$ //$NON-NLS-2$
                        }
                        dtdoc = message.getAttribute("dtdoc").getValueDate(); //$NON-NLS-1$
                        if(dtdoc != null)
                        {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
                            label.append(" em "); //$NON-NLS-1$
                            label.append(df.format(dtdoc));   
                        }
                        activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL); //$NON-NLS-1$
                    }
                }                
            }
            else if("xwfActivitySend".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER) //$NON-NLS-1$
            {
                attr = activity.getAttribute("message");     //$NON-NLS-1$
                if(attr != null)
                {
                    object = attr.getObject();
                    message = manager.getValueBoObject(object.getAttribute("value").getObject());
                    setProgramLabel(program,message);
                    label.append(xwfMessage.getPreferedMediaLabel(message));
                    close = activity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
                    String preferedMedia = message.getAttribute("preferedMedia").getValueString(); //$NON-NLS-1$
                    if("close".equals(close)) //$NON-NLS-1$
                    {                        
                        if("Letter".equals(preferedMedia) || //$NON-NLS-1$
                           "Prefered".equals(preferedMedia)) //$NON-NLS-1$
                        {
                            label.append(" enviada "); //$NON-NLS-1$
                        }
                        else
                        {
                            label.append(" enviado "); //$NON-NLS-1$
                        }                        
                    }
                    else
                    {
                        label.append("  por enviar ");
                    }
                    
                    label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] ");                                        
                    
                    bridgeHandler to = message.getBridge("to"); //$NON-NLS-1$
                    to.beforeFirst();
                    if(to.getRecordCount() > 0)
                    {
                        label.append(Messages.getString("xwfFunctions.155")); //$NON-NLS-1$
                        boObject toObject = null;
                        int count = 0;
                        while(to.next() && count < 2)
                        {
                            if(count != 0)
                            {
                                label.append(" , ");     //$NON-NLS-1$
                            }
                            toObject = to.getObject();
                            label.append(toObject.getAttribute("name").getValueString());                             //$NON-NLS-1$
                            count++;
                        }     
                        if(to.next())
                        {
                            label.append(" ..."); //$NON-NLS-1$
                        }
                    }                                                        

                    
                    dtdoc = message.getAttribute("dtdoc").getValueDate();
                    if(dtdoc != null)
                    {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
                        label.append(" , em "); //$NON-NLS-1$
                        label.append(df.format(dtdoc));   
                    }                            
                    activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);
                }                
            }     
            else if("xwfWaitResponse".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER) //$NON-NLS-1$
            {
                boObject sendActivity = activity.getAttribute("sendActivity").getObject(); //$NON-NLS-1$
                if(sendActivity != null)
                {
                    attr = sendActivity.getAttribute("message");     //$NON-NLS-1$
                    if(attr != null)
                    {
                        object = attr.getObject();
                        message = manager.getValueBoObject(object.getAttribute("value").getObject());                     //$NON-NLS-1$
                        close = activity.getStateAttribute("runningState").getValueString(); //$NON-NLS-1$
                        if(!"close".equals(close)) //$NON-NLS-1$
                        {
                            label.append(Messages.getString("xwfFunctions.175"));     //$NON-NLS-1$
                        }
                        else
                        {
                            label.append(Messages.getString("xwfFunctions.176")); //$NON-NLS-1$
                        }                    
                        label.append(xwfMessage.getPreferedMediaLabel(message));                    
                        label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] ");                                         //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                        
                        bridgeHandler to = message.getBridge("to"); //$NON-NLS-1$
                        to.beforeFirst();
                        if(to.getRecordCount() > 0)
                        {
                            label.append(Messages.getString("xwfFunctions.181")); //$NON-NLS-1$
                            boObject toObject = null;
                            int count = 0;
                            while(to.next() && count < 2)
                            {
                                if(count != 0)
                                {
                                    label.append(" , ");     //$NON-NLS-1$
                                }
                                toObject = to.getObject();
                                label.append(toObject.getAttribute("name").getValueString());                             //$NON-NLS-1$
                                count++;
                            }     
                            if(to.next())
                            {
                                label.append(" ..."); //$NON-NLS-1$
                            }
                        }                                                        
    
                        
                        dtdoc = message.getAttribute("dtdoc").getValueDate(); //$NON-NLS-1$
                        if(dtdoc != null)
                        {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy"); //$NON-NLS-1$
                            label.append(Messages.getString("xwfFunctions.72")); //$NON-NLS-1$
                            label.append(df.format(dtdoc));   
                        }                                
                        activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL); //$NON-NLS-1$
                    }
                }
            }
            else if("xwfActivityTransfer".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER) //$NON-NLS-1$
            {
                String programLabel = program.getAttribute("label").getValueString(); //$NON-NLS-1$
                String de = null;
                String para = null;
                
                if(activity.getAttribute("assignedQueue").getObject() != null) //$NON-NLS-1$
                {
                    de = activity.getAttribute("assignedQueue").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }
                if(activity.getAttribute("to").getObject() != null) //$NON-NLS-1$
                {
                    para = activity.getAttribute("to").getObject().getAttribute("name").getValueString(); //$NON-NLS-1$ //$NON-NLS-2$
                }

                activity.getAttribute("label") //$NON-NLS-1$
                            .setValueString(Messages.getString("xwfFunctions.71") + programLabel +Messages.getString("xwfFunctions.199")+de+Messages.getString("xwfFunctions.200")+para, //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                                            AttributeHandler.INPUT_FROM_INTERNAL);           
            }
            if(attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
            {
                manager.updateObject(activity);
            }        
        }        
    }
  /**
   * Altera label da programa com base em informações da no mesmo e dos objectos no contexto desse mesmo programa. 
   * @param program o xwfProgramRuntime em contexto.
   * @param activity a xwfActivity a alterar.
   */            
    private static void setProgramLabel(boObject program,boObject object)  throws boRuntimeException
    {
        if(program != null && program.getAttribute("label").getInputType() != AttributeHandler.INPUT_FROM_USER) //$NON-NLS-1$
        {
            StringBuffer label = new StringBuffer(object.getAttribute("name").getValueString()); //$NON-NLS-1$
            String flow = program.getAttribute("flow").getValueString(); //$NON-NLS-1$
            if("".equals(flow)) //$NON-NLS-1$
            {
//                String progLabel = program.getAttribute("label").getValueString();
//                label.append(object.getAttribute("name").getValueString());
//                if(label != null && label.length() >= 200)
                if(label.length() >= 200) 
                {
                    label = new StringBuffer( label.toString().substring(0, 190) );
                    label.append( "(...)"); //$NON-NLS-1$
                }                                                
                program.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);                 //$NON-NLS-1$
            }                         
        }
    }     
  /**
   * Devolve label da actividade com base em informações da na mesma e dos objectos no contexto dessa mesma actividade. 
   * @param engine ligação core do workflow.
   * @param activityName nome da actividade a criar um fluxo de trabalho.
   * @param objectType nome do boObject a criar um fluxo de trabalho.
   * @return label para actividade gerada no ambito do fluxo de trabalho.
   */       
    public static String getIntelligentLabelEmbeddedProgram(EngineGate engine,String activityName,String objectType) throws boRuntimeException
    {   
        boObject activity = null;
        String label = "";                 //$NON-NLS-1$
        if(objectType.endsWith("Conversation")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.230"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.69"); //$NON-NLS-1$
            }        
        }
        else if(objectType.endsWith("Fax")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.235"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.67"); //$NON-NLS-1$
            }                
        }
        else if(objectType.endsWith("Letter")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.240"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.242"); //$NON-NLS-1$
            }                    
        }
        else if(objectType.endsWith("Mail")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.245"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.247"); //$NON-NLS-1$
            }        
        }
        else if(objectType.endsWith("Phone")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.250"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.252"); //$NON-NLS-1$
            }
        }
        else if(objectType.endsWith("Sgis")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.255"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.257"); //$NON-NLS-1$
            }        
        }
        else if(objectType.endsWith("SMS")) //$NON-NLS-1$
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.260"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.262"); //$NON-NLS-1$
            }        
        }
        else
        {
            if("xwfActivitySend".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.264"); //$NON-NLS-1$
            }
            else if("xwfCreateReceivedMessage".equals(activityName)) //$NON-NLS-1$
            {
                label = Messages.getString("xwfFunctions.266"); //$NON-NLS-1$
            }
        }
        return label;
    }
    
    public static boObject getActivityByUsid(String activity_name, String usid, xwfBoManager xwfm) throws boRuntimeException
    {
        String boql_usid = "select "+activity_name+" where unique_sid = '"+usid+"'"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        boObject boMact = xwfm.loadObject(boql_usid);
        if(xwfm.isInTest())
        {
            boObjectList ll = xwfm.listObject(boql_usid);
            ll.beforeFirst();
            while(ll.next())
            {
                if(usid.equals(ll.getObject().getAttribute("unique_sid").getValueString())) //$NON-NLS-1$
                {
                    boMact = ll.getObject();
                    break;
                }
            }
        }
        return boMact;
    }
    
    public static Integer conpareActivtiyDates(boObject firstAct, boObject secAct) throws boRuntimeException
    {
        Date dt1 = null;
        Date dt2 = null;
        dt1 = xwfFunctions.extractActvDate(firstAct);
        dt2 = xwfFunctions.extractActvDate(secAct);
        if(dt1 != null && dt2 != null)
        {
            return new Integer(dt1.compareTo(dt2));
        }
        else
            return new Integer(0);
    }
    
    public static Date extractActvDate(boObject actv) throws boRuntimeException
    {
        Date dt = null;
        AttributeHandler msgAtt1 = actv.getAttribute("message"); //$NON-NLS-1$
        XwfController ctrl = null; 
        if("XwfController".equalsIgnoreCase(actv.getEboContext().getController().getName())) //$NON-NLS-1$
            ctrl = (XwfController)actv.getEboContext().getController();
        if(msgAtt1 != null)
        {   
            boObject msg = null;
            if(ctrl != null)
                msg = ctrl.getEngine().getBoManager().getValueBoObject(msgAtt1.getObject().getAttribute("value").getObject()); //$NON-NLS-1$
            else
                msg = msgAtt1.getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject(); //$NON-NLS-1$ //$NON-NLS-2$
            if(msg != null && msg.getAttribute("dtdoc") != null) //$NON-NLS-1$
            {
                dt = msg.getAttribute("dtdoc").getValueDate(); //$NON-NLS-1$
            }
            if(dt != null)
                return dt;
        }
        else
        {
            dt = actv.getAttribute("beginDate").getValueDate(); //$NON-NLS-1$
            if(dt != null)
                return dt;
            else
            {
                dt = actv.getAttribute("SYS_DTCREATE").getValueDate(); //$NON-NLS-1$
                return dt;
            }
        }
        return dt;
        
    }
}