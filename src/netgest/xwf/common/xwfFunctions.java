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
                beginDate = activity.getAttribute("beginDate").getValueDate();
                if(beginDate == null)
                {                    
                    String duration = activity.getAttribute("duration").getValueString();
                    if(duration != null && !"".equals(duration))
                    {
                        Calendar c = Calendar.getInstance();
                        String[] realDuration = duration.split("\\.");
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
                        activity.getAttribute("beginDate").setValueDate(c.getTime());
                    }   
                    else
                    {
                        activity.getAttribute("beginDate").setValueDate(date);  
                    }
                }                
                activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui());                
            }
            
            activity.getAttribute("percentComplete").setValueString("100");
            if("xwfWaitResponse".equals(activity.getName()))
            {
                boObject object = null;
                boObject receiveActivity = activity.getAttribute("receiveActivity").getObject();  
                if(receiveActivity != null)
                {
                    AttributeHandler attr = receiveActivity.getAttribute("message");
                    if(attr != null)
                    {
                        object = attr.getObject();
                        if(object != null)
                        {
                            attr = object.getAttribute("value");
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
                                            Date dtdoc = object.getAttribute("dtdoc").getValueDate(); 
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
                activity.getAttribute("endDate").setValueDate(date);
            }
            else
            {
                activity.getAttribute("endDate").setValueDate(date);   
            }
//            manager.finishedStep(activityBoui);   
        }
        else if(XwfKeys.ACTION_CANCEL_KEY.equals(state))
        {
            if("create".equals(activity.getStateAttribute("runningState").getValueString()))
            {
                beginDate = activity.getAttribute("beginDate").getValueDate();
                if(beginDate == null)
                {
                    activity.getAttribute("beginDate").setValueDate(date);   
                }                             
                activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui());                
            }
                    
            activity.getAttribute("endDate").setValueDate(date);
//            manager.finishedStep(activityBoui);                        
        }        
        else if(XwfKeys.ACTION_OPEN_KEY.equals(state))
        {
            activity.getAttribute("beginDate").setValueDate(date);
            activity.getAttribute("performer").setValueLong(activity.getEboContext().getBoSession().getPerformerBoui());
            activity.getStateAttribute("runningState").setValueString(state);
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
            if(label.toUpperCase().startsWith(Messages.getString("xwfFunctions.77")) || 
               label.toUpperCase().startsWith(Messages.getString("xwfFunctions.78")) ||
               label.toUpperCase().indexOf(Messages.getString("xwfFunctions.79")) > -1
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
            AttributeHandler attrLabel = activity.getAttribute("label");
            String activityName = activity.getName();
            boObject message = null;
            boObject object = null;              
            Date dtdoc = null;
            AttributeHandler attr = null;                                
            String close = null;            
                        
            if("xwfActivityReceive".equals(activityName) ||
               "xwfCreateReceivedMessage".equals(activityName) ||
               "xwfActivityCreateMessage".equals(activityName)
               )
            {
                if("xwfActivityReceive".equals(activityName) && !isCC)
                {
                    isCC = isKnowledge(activity.getAttribute("label").getValueString());
                }
                attr = activity.getAttribute("message");    
                if(attr != null)
                {
                    object = attr.getObject();
                    message = manager.getValueBoObject(object.getAttribute("value").getObject());
                    setProgramLabel(program,message);
                    
                    if(attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
                    {
                        close = activity.getStateAttribute("runningState").getValueString();
                        if(!"close".equals(close))
                        {
                            if( "xwfCreateReceivedMessage".equals(activityName)||
                                "xwfActivityCreateMessage".equals(activityName))
                            {
                                label.append(Messages.getString("xwfFunctions.76"));
                            }
                            else
                            {
                                if("open".equals(close))
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.74"));
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.75"));
                                    }
                                }
                                else
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.73"));
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.97"));
                                    }
                                }
                            }
                        }
                        label.append(xwfMessage.getPreferedMediaLabel(message));
                        if("close".equals(close))
                        {
                            String preferedMedia = message.getAttribute("preferedMedia").getValueString();
                            if( "xwfCreateReceivedMessage".equals(activityName) ||
                                "xwfActivityCreateMessage".equals(activityName))
                            {
                                if("Letter".equals(preferedMedia) ||
                                   "Prefered".equals(preferedMedia))
                                {
                                    label.append(" criada ");
                                }
                                else
                                {
                                    label.append(" criado ");
                                }                        
                                
                            }
                            else
                            {                            
                                if("Letter".equals(preferedMedia) ||
                                   "Prefered".equals(preferedMedia))
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.108"));
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.109"));
                                    }
                                }
                                else
                                {
                                    if(isCC)
                                    {
                                        label.append(Messages.getString("xwfFunctions.110"));
                                    }
                                    else
                                    {
                                        label.append(Messages.getString("xwfFunctions.111"));
                                    }
                                }
                            }                    
                        }                    
                        label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] ");
                        boObject from = message.getAttribute("from").getObject();
                        if(from != null)
                        {
                            label.append(Messages.getString("xwfFunctions.116")).append(from.getAttribute("name").getValueString());
                        }
                        dtdoc = message.getAttribute("dtdoc").getValueDate();
                        if(dtdoc != null)
                        {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            label.append(" em ");
                            label.append(df.format(dtdoc));   
                        }
                        activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                }                
            }
            else if("xwfActivitySend".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
            {
                attr = activity.getAttribute("message");    
                if(attr != null)
                {
                    object = attr.getObject();
                    message = manager.getValueBoObject(object.getAttribute("value").getObject());
                    setProgramLabel(program,message);
                    label.append(xwfMessage.getPreferedMediaLabel(message));
                    close = activity.getStateAttribute("runningState").getValueString();
                    String preferedMedia = message.getAttribute("preferedMedia").getValueString();
                    if("close".equals(close))
                    {                        
                        if("Letter".equals(preferedMedia) ||
                           "Prefered".equals(preferedMedia))
                        {
                            label.append(" enviada ");
                        }
                        else
                        {
                            label.append(" enviado ");
                        }                        
                    }
                    else
                    {
                        label.append("  por enviar ");
                    }
                    
                    label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] ");                                        
                    
                    bridgeHandler to = message.getBridge("to");
                    to.beforeFirst();
                    if(to.getRecordCount() > 0)
                    {
                        label.append(Messages.getString("xwfFunctions.155"));
                        boObject toObject = null;
                        int count = 0;
                        while(to.next() && count < 2)
                        {
                            if(count != 0)
                            {
                                label.append(" , ");    
                            }
                            toObject = to.getObject();
                            label.append(toObject.getAttribute("name").getValueString());                            
                            count++;
                        }     
                        if(to.next())
                        {
                            label.append(" ...");
                        }
                    }                                                        

                    
                    dtdoc = message.getAttribute("dtdoc").getValueDate();
                    if(dtdoc != null)
                    {
                        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                        label.append(" , em ");
                        label.append(df.format(dtdoc));   
                    }                            
                    activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);
                }                
            }     
            else if("xwfWaitResponse".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
            {
                boObject sendActivity = activity.getAttribute("sendActivity").getObject();
                if(sendActivity != null)
                {
                    attr = sendActivity.getAttribute("message");    
                    if(attr != null)
                    {
                        object = attr.getObject();
                        message = manager.getValueBoObject(object.getAttribute("value").getObject());                    
                        close = activity.getStateAttribute("runningState").getValueString();
                        if(!"close".equals(close))
                        {
                            label.append(Messages.getString("xwfFunctions.175"));    
                        }
                        else
                        {
                            label.append(Messages.getString("xwfFunctions.176"));
                        }                    
                        label.append(xwfMessage.getPreferedMediaLabel(message));                    
                        label.append(" [ ").append(message.getAttribute("name").getValueString()).append(" ] ");                                        
                        
                        bridgeHandler to = message.getBridge("to");
                        to.beforeFirst();
                        if(to.getRecordCount() > 0)
                        {
                            label.append(Messages.getString("xwfFunctions.181"));
                            boObject toObject = null;
                            int count = 0;
                            while(to.next() && count < 2)
                            {
                                if(count != 0)
                                {
                                    label.append(" , ");    
                                }
                                toObject = to.getObject();
                                label.append(toObject.getAttribute("name").getValueString());                            
                                count++;
                            }     
                            if(to.next())
                            {
                                label.append(" ...");
                            }
                        }                                                        
    
                        
                        dtdoc = message.getAttribute("dtdoc").getValueDate();
                        if(dtdoc != null)
                        {
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                            label.append(Messages.getString("xwfFunctions.72"));
                            label.append(df.format(dtdoc));   
                        }                                
                        activity.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                }
            }
            else if("xwfActivityTransfer".equals(activityName) && attrLabel.getInputType() != AttributeHandler.INPUT_FROM_USER)
            {
                String programLabel = program.getAttribute("label").getValueString();
                String de = null;
                String para = null;
                
                if(activity.getAttribute("assignedQueue").getObject() != null)
                {
                    de = activity.getAttribute("assignedQueue").getObject().getAttribute("name").getValueString();
                }
                if(activity.getAttribute("to").getObject() != null)
                {
                    para = activity.getAttribute("to").getObject().getAttribute("name").getValueString();
                }

                activity.getAttribute("label")
                            .setValueString(Messages.getString("xwfFunctions.71") + programLabel +Messages.getString("xwfFunctions.199")+de+Messages.getString("xwfFunctions.200")+para,
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
        if(program != null && program.getAttribute("label").getInputType() != AttributeHandler.INPUT_FROM_USER)
        {
            StringBuffer label = new StringBuffer(object.getAttribute("name").getValueString());
            String flow = program.getAttribute("flow").getValueString();
            if("".equals(flow))
            {
//                String progLabel = program.getAttribute("label").getValueString();
//                label.append(object.getAttribute("name").getValueString());
//                if(label != null && label.length() >= 200)
                if(label.length() >= 200) 
                {
                    label = new StringBuffer( label.toString().substring(0, 190) );
                    label.append( "(...)");
                }                                                
                program.getAttribute("label").setValueString(label.toString(),AttributeHandler.INPUT_FROM_INTERNAL);                
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
        String label = "";                
        if(objectType.endsWith("Conversation"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.230");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.69");
            }        
        }
        else if(objectType.endsWith("Fax"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.235");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.67");
            }                
        }
        else if(objectType.endsWith("Letter"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.240");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.242");
            }                    
        }
        else if(objectType.endsWith("Mail"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.245");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.247");
            }        
        }
        else if(objectType.endsWith("Phone"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.250");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.252");
            }
        }
        else if(objectType.endsWith("Sgis"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.255");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.257");
            }        
        }
        else if(objectType.endsWith("SMS"))
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.260");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.262");
            }        
        }
        else
        {
            if("xwfActivitySend".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.264");
            }
            else if("xwfCreateReceivedMessage".equals(activityName))
            {
                label = Messages.getString("xwfFunctions.266");
            }
        }
        return label;
    }
    
    public static boObject getActivityByUsid(String activity_name, String usid, xwfBoManager xwfm) throws boRuntimeException
    {
        String boql_usid = "select "+activity_name+" where unique_sid = '"+usid+"'";
        boObject boMact = xwfm.loadObject(boql_usid);
        if(xwfm.isInTest())
        {
            boObjectList ll = xwfm.listObject(boql_usid);
            ll.beforeFirst();
            while(ll.next())
            {
                if(usid.equals(ll.getObject().getAttribute("unique_sid").getValueString()))
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
        AttributeHandler msgAtt1 = actv.getAttribute("message");
        XwfController ctrl = null; 
        if("XwfController".equalsIgnoreCase(actv.getEboContext().getController().getName()))
            ctrl = (XwfController)actv.getEboContext().getController();
        if(msgAtt1 != null)
        {   
            boObject msg = null;
            if(ctrl != null)
                msg = ctrl.getEngine().getBoManager().getValueBoObject(msgAtt1.getObject().getAttribute("value").getObject());
            else
                msg = msgAtt1.getObject().getAttribute("value").getObject().getAttribute("valueObject").getObject();
            if(msg != null && msg.getAttribute("dtdoc") != null)
            {
                dt = msg.getAttribute("dtdoc").getValueDate();
            }
            if(dt != null)
                return dt;
        }
        else
        {
            dt = actv.getAttribute("beginDate").getValueDate();
            if(dt != null)
                return dt;
            else
            {
                dt = actv.getAttribute("SYS_DTCREATE").getValueDate();
                return dt;
            }
        }
        return dt;
        
    }
}