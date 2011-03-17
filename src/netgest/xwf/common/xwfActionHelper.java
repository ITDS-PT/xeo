/*Enconding=UTF-8*/
package netgest.xwf.common;

import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;
import java.io.PrintStream;
import java.io.PrintWriter;

import java.lang.Boolean;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.controller.xwf.XwfValidator;
import netgest.bo.impl.Ebo_QueueImpl;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.MessageServer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.bo.system.boConnectionManager;

import netgest.bo.utils.DateUtils;
import netgest.bo.utils.DestroyBusinessObject;
import netgest.utils.ClassUtils;

import netgest.utils.StringUtils;
import netgest.xwf.EngineGate;
import netgest.xwf.xwfEngineGate;
import netgest.xwf.core.xwfAnnounceImpl;
import netgest.xwf.core.xwfControlFlow;
import netgest.xwf.core.xwfECMAevaluator;
import netgest.xwf.core.xwfManager;
import netgest.xwf.core.xwfMessage;
import netgest.xwf.core.xwfStepExec;

import netgest.bo.system.Logger;

/**
 * <p>Title: xwfHelper </p>
 * <p>Description: Métodos comuns</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * @Company: Enlace3 </p>
 * @author Pedro Castro Campos
 * @version 1.0
 */
public final class xwfActionHelper 
{
    private static Logger logger = Logger.getLogger("netgest.xwf.common.xwfActionHelper");
   /**
     * Inicia um xwfProgram definido por um xwfStarterConfig.
     * @param ctx, EboContext contexto em que o programa é iniciado.
     * @param xwfStarterConfig, objecto de configuração onde está dinido o programa a ser executado.
     * @param object, boObjecto que deu origem ao programa 
     * @return result, TRUE caso tenha iniciado o programa, FALSE cc.
     */
    public static Hashtable OBJECTSSTARTCONFIG= new Hashtable();
    public static final String DUMMY =""; 
    
    public static void cleanCacheObjects()
    {
        OBJECTSSTARTCONFIG= new Hashtable();
    }
    public static boolean startProgram(EboContext ctx,long xwfStarterConfig, long object) throws boRuntimeException 
    {       
        boolean result = false;
        if(!xwfHelper.isInputVariableInProgram(boObject.getBoManager().loadObject(ctx,object)))
        {
            boObject starterConfig = null;
            if(xwfStarterConfig != -1 && object != -1)
            {              
                starterConfig = boObject.getBoManager().loadObject(ctx,xwfStarterConfig);
                long program = ClassUtils.convertToLong(starterConfig.getAttribute("program").getValueString(),-1);
                if(program != -1)
                {        
                    xwfManager manager = new xwfManager(ctx,null);
                    if(manager.createProgram(program,object,xwfHelper.PROGRAM_EXEC_DEFAULT_MODE) > 0)
                    {
                        result = true;   
                    }                
                }
            }
        }
        else
        {
            result = true;
        }
        return result;
    }  
    
    /**
     * Altera os previlégios de uma actividade de modo a que um participante possa ter acesso a mesma.
     * @param participant, boui do participante a dar previlégios.
     * @param activity, actividade sobre a qual p participante passa a ter previlégios.
     */     
    private static void givePrivileges(long participant, boObject activity)throws boRuntimeException
    {       
        
        boObject variable = null;            
        String activityName = activity.getName();
        if("xwfCreateReceivedMessage".equals(activityName) ||
           "xwfActivitySend".equals(activityName) ||
           "xwfActivityReceive".equals(activityName))
        {
            variable = activity.getAttribute("message").getObject();
            xwfStepExec.givePrivileges(participant,variable);
        }
        
        bridgeHandler bridge = activity.getBridge("variables");            
        bridge.beforeFirst();
        while(bridge.next())
        {
            variable = bridge.getObject();
            xwfStepExec.givePrivileges(participant,variable);
        }                    
    }
    /**
     * Dá previlégios a um programa de modo a que só os participante possam ter acesso.
     * @param activity, actividade em execução.
     */     
    public static void givePrivilegesToProgram(boObject activity, long programBoui)throws boRuntimeException
    {       
        boObject program = null;
        long assignedQueue = activity.getAttribute("assignedQueue").getValueLong();
        if(assignedQueue > 1 && programBoui > 1)
        {            
            program = boObject.getBoManager().loadObject(activity.getEboContext(),programBoui);
            if(program != null)
            {
                bridgeHandler bridge = program.getBridge("access");
                bridge.beforeFirst();
                if(!bridge.haveBoui(assignedQueue))
                {
                    bridge.add(assignedQueue);
                }
            }
        }
    }     
    /**
     * Reassigna uma xwfActivity, definida num xwfReassign.
     * @param activity, xwfActivity a reassignar.
     * @param object, xwfReassign objecto que contem a informação para a reassignação.
     */     
    public static  boolean reassignActivity(boObject activity, boObject object)  throws boRuntimeException
    {
        boolean result = false;        
        try
        {
            long reassigned = object.getAttribute("reassigned").getValueLong();
            if(reassigned < 1)
            {
                activity.getStateAttribute("runningState").setValueString("create");
                activity.getAttribute("performer").setValueString("");
                activity.getAttribute("beginDate").setValueString("");
                long to = object.getAttribute("to").getValueLong();
                activity.getAttribute("assignedQueue").setValueLong(to);
                givePrivileges(to,activity);
                
                object.getAttribute("errormessage").setValueString("");
                object.getAttribute("reassigned").setValueString("1");

                bridgeHandler bridge =  activity.getBridge("reassigns");
                bridge.beforeFirst();
                if(!bridge.haveBoui(object.getBoui()))
                {
                    bridge.add(object.getBoui());
                }                
//                getProxyManager().getBoManager().updateObject(activity);                
            }
            result = true;
        }
        catch (boRuntimeException ex) 
        {       
            try
            {
                if(object != null)
                {
                    CharArrayWriter cw = new CharArrayWriter();
                    PrintWriter pw = new PrintWriter( cw );
                    ex.printStackTrace( pw );
                    pw.close();
                    cw.close();                            
                    object.getAttribute("errormessage").setValueString(cw.toString());
                    object.getAttribute("reassigned").setValueString("-1");
                }
            }
            catch (Exception e)
            {
                
            }
        }               
        catch (Throwable ex) 
        {       
            try
            {
                CharArrayWriter cw = new CharArrayWriter();
                PrintWriter pw = new PrintWriter( cw );
                ex.printStackTrace( pw );
                pw.close();
                cw.close();                            
                object.getAttribute("errormessage").setValueString(cw.toString());
                object.getAttribute("reassigned").setValueString("-1");
            }
            catch (Exception e)
            {
                
            }
        }      
        return result;
    }    

//    public static boolean deliverMsessage(XwfController controller) throws boRuntimeException
//    {
//        return xwfMessage.deliverMessage(controller);
//    }
    public static boolean mergeMessage(EngineGate engine,boObject activity) throws boRuntimeException
    {
        return xwfMessage.mergeMessage(engine,activity);
    }  
    public static boObject replyMessage(EngineGate engine, boObject activity, boolean replyAll, String type) throws boRuntimeException
    {
        boObject newActivity = null;
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
        boObject replyMessage = null;
        if(message != null)
        {
            if(type == null) type = message.getName();
            replyMessage = MessageServer.getReplyMessage(engine.getBoManager().getContext(), message, replyAll, type);            
            if(replyAll)
            {
                newActivity = ((xwfEngineGate)engine).getManager().createMessageActivity("xwfActivitySend", "RE: " + message.getAttribute("name").getValueString(), replyMessage, type);
            }
            else
            {
                newActivity = ((xwfEngineGate)engine).getManager().createMessageActivity("xwfActivitySend", "RE: " + message.getAttribute("name").getValueString(), replyMessage, type);
            }
            newActivity.getAttribute("branch").setValueString(activity.getAttribute("sid").getValueString());
        }
        return newActivity;
    }
    public static boObject forwardMessage(EngineGate engine, boObject activity, String msgType) throws boRuntimeException
    {
        boObject newActivity = null;
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
        boObject forwardMessage = null;
        if(message != null)
        {
            if(msgType == null) msgType = message.getName();


            //TODO:Implement Interface LUSITANIA
            //forwardMessage = MessageServer.getForwardMessage(engine.getBoManager().getContext(), message, msgType);
            
            newActivity = ((xwfEngineGate)engine).getManager().createMessageActivity("xwfActivitySend", "FWD: " + message.getAttribute("name").getValueString(), forwardMessage, msgType);
            newActivity.getAttribute("branch").setValueString(activity.getAttribute("sid").getValueString());
        }
        
        return newActivity;
    }
    public static boObject sendMessage(EngineGate engine) throws boRuntimeException
    {
        return ((xwfEngineGate)engine).getManager().createMessageActivity("xwfActivitySend", "Nova mensagem", null, "message");
    } 
    public static void setRead(boObject activity, long performer) throws boRuntimeException
    {
        boObject performerObject = boObject.getBoManager().loadObject(activity.getEboContext(), performer);
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();        
        MessageUtils.setRead(message, performerObject);
    }    
    public static boolean moveProgram(EngineGate engine, boObject fromProgram,boObject toProgram) throws boRuntimeException
    {
        boolean result = false;
        boObject aux = null;
        boObject activity = null;
        AttributeHandler attr = null;
        if(fromProgram != null && toProgram != null)
        {
//            String flow = fromProgram.getAttribute("flow").getValueString();
//            if(flow != null && "".equals(flow))
            {
                boObjectList vvalues = boObjectList.list(fromProgram.getEboContext(),"SELECT xwfVarValue WHERE program="+fromProgram.getBoui());
                vvalues.beforeFirst();
                while(vvalues.next())
                {
                    vvalues.getObject().getAttribute("program").setValueLong(toProgram.getBoui());
                }
                boObjectList activitys = boObjectList.list(fromProgram.getEboContext(),"SELECT xwfActivity WHERE program="+fromProgram.getBoui());
                activitys.beforeFirst();
                while(activitys.next())
                {
                    activity = activitys.getObject();
                    activity.getAttribute("program").setValueLong(toProgram.getBoui()); 
                    activity.getAttribute("unique_sid").setValueString("-1"); 
                    attr = activity.getAttribute("message");
                    if(attr != null)
                    {
                        aux = activity.getAttribute("message").getObject().getAttribute("value").getObject().getAttribute(xwfHelper.VALUE_OBJECT).getObject();                    
                        if(!toProgram.getBridge("message").haveBoui(aux.getBoui()) &&
                           "close".equals(activity.getStateAttribute("runningState").getValueString()))
                        {
                            toProgram.getBridge("message").add(aux.getBoui());
                        }
                    }       
                    engine.getBoManager().updateObject(activity);
                }
                
//                removeProgram(engine.getBoManager(),fromProgram.getBoui(), false);       
                DestroyBusinessObject.destroy(fromProgram);
                result = true;
            }
        }
        return result;
    }
    public static boolean moveActivity(EngineGate engine, boObject fromProgram,boObject toProgram,boObject activity) throws boRuntimeException
    {
        boolean result = false;
        boObject aux = null;
        AttributeHandler attr = null;
        if(fromProgram != null && toProgram != null && activity != null)
        {
//            String flow = fromProgram.getAttribute("flow").getValueString();
//            String uniqueSid = activity.getAttribute("unique_sid").getValueString();
//            if("".equals(flow) || "-1".equals(uniqueSid))
            {
                activity.getAttribute("program").setValueLong(toProgram.getBoui());
                activity.getAttribute("unique_sid").setValueString("-1");
                bridgeHandler bvars = activity.getBridge("variables");
                bvars.beforeFirst();
                while(bvars.next())
                {
                    boObject vval = bvars.getObject().getAttribute("value").getObject();
                    if(vval != null)
                    {
                        vval.getAttribute("program").setValueLong(toProgram.getBoui());
                    }
                }
                attr = activity.getAttribute("message");
                if(attr != null)
                {        
                    boObject vval = attr.getObject();
                    if(vval != null)
                    {
                        vval = vval.getAttribute("value").getObject();
                        if(vval != null)
                        {
                            vval.getAttribute("program").setValueLong(toProgram.getBoui());
                        }
                    }
                    engine.getBoManager().updateObject(activity);
                    aux = engine.getBoManager().getValueBoObject(activity.getAttribute("message").getObject().getAttribute("value").getObject());
                    if(!toProgram.getBridge("message").haveBoui(aux.getBoui()) &&
                       "close".equals(activity.getStateAttribute("runningState").getValueString()))
                    {
                        toProgram.getBridge("message").add(aux.getBoui());
                        engine.getBoManager().updateObject(toProgram);
                    }
                }                                 
                boObjectList activitys = boObjectList.list(fromProgram.getEboContext(),"SELECT xwfActivity WHERE program = " + fromProgram.getBoui(),true,false);
                if(activitys.getRecordCount() == 0)
                {                        
//                    removeProgram(engine.getBoManager(),fromProgram.getBoui(), false);
                    DestroyBusinessObject.destroy(fromProgram);
                }                                                              
                result = true;
            }
        }
        return result;
    }
   /* 
    public static boolean AremoveProgram(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {   
        boolean result = false;
        boObject program = manager.getObject(programRuntimeBoui) ;
        boConnectionManager connManager = program.getEboContext().getConnectionManager();
        try 
        {

            connManager.beginOnlyDatabaseTransaction();

            removeParticipants(manager,programRuntimeBoui);        
            removeActivitys(manager,programRuntimeBoui);
            removeVariables(manager,programRuntimeBoui);                                
            removeWaits(manager,programRuntimeBoui);                
            removeSerialObjects(manager,programRuntimeBoui);
            manager.destroyObject(program);
            result = true;
        } 
        catch(boRuntimeException e) 
        {
            result = false;
        }
        finally 
        {
            if(result)
            {     
                result = connManager.commitOnlyDatabaseTransaction();
            }
            else
            {
                connManager.rollbackOnlyDatabaseTransaction();
            }
        }
        return result;
    }
    
    public static boolean removeProgram(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
        return removeProgram( manager, programRuntimeBoui, true);
    }
    public static boolean removeProgram(xwfBoManager manager, long programRuntimeBoui, boolean removeMessages) throws boRuntimeException
    {   
        boolean result = false;
        boObject program = manager.getObject(programRuntimeBoui) ;
//        boConnectionManager connManager = program.getEboContext().getConnectionManager();
//        try 
//        {

//            connManager.beginOnlyDatabaseTransaction();
            
            boObjectList activitys = manager.listObject("SELECT xwfActivity WHERE program = " + programRuntimeBoui,false,false);
            activitys.beforeFirst();
            ArrayList objectsToDestroy = new ArrayList();
            while ( activitys.next() )
            {
                objectsToDestroy.add( activitys.getObject() );
            }
            bridgeHandler bridgeParticipants = program.getBridge("participants");
            bridgeParticipants.beforeFirst();
            while ( bridgeParticipants.next() )
            {
                objectsToDestroy.add( bridgeParticipants.getObject() );
            }
            
            bridgeHandler bridgeVariables = program.getBridge("variables");
            bridgeVariables.beforeFirst();
            while ( bridgeVariables.next() )
            {
                boObject v = bridgeVariables.getObject();
                objectsToDestroy.add( v );
                boObject vv = v.getAttribute("value").getObject();
                if ( vv!= null )
                {
                    objectsToDestroy.add( vv );
                }
            }
            
            boObjectList list = manager.listObject("SELECT xwfWait WHERE program = " + programRuntimeBoui,false,false);
            list.beforeFirst();
            while(list.next())
            {
                objectsToDestroy.add( list.getObject() );
            }
            
//            list = manager.listObject("SELECT xwfSerialObject WHERE program = " + programRuntimeBoui,false,false);
//            list.beforeFirst();
//            while(list.next())
//            {
//                objectsToDestroy.add( list.getObject() );
//            }
//            
            
            bridgeHandler bridgeMessage = program.getBridge("message");
            bridgeMessage.beforeFirst();
            ArrayList messages = new ArrayList();
            while ( bridgeMessage.next() )
            {
                boObject v = bridgeMessage.getObject();
                messages.add( v );
            }
            bridgeHandler bridgeReadList =  program.getBridge("READLIST");
            if(bridgeReadList != null)
            {
                bridgeReadList.truncate();
            }
            program.destroyForce();
            
            for (int i = 0; i < objectsToDestroy.size(); i++) 
            {
                ( (boObject) objectsToDestroy.get(i)).destroyForce();
            }
            
            if( removeMessages )
            {
                List binary = null;
                boObject document = null;
                for (int i = 0; i < messages.size(); i++) 
                {
                    boObject msgO = ( (boObject) messages.get(i)); 
                    list = manager.listObject("SELECT xwfActivity ext WHERE message.value.valueObject = " + msgO.getBoui(), false,false );
                    if(list.getRowCount() == 0)
                    {
                        binary = msgO.getObjectBinary().getBinary();
                        bridgeHandler anexos = msgO.getBridge("documents");
                        anexos.beforeFirst();
                        List anexosToDestroy = new ArrayList();
                        while(anexos.next())
                        {
                            anexosToDestroy.add(anexos.getObject());
                        }                                                
                        msgO.destroyForce();
                        if(!msgO.exists() && binary != null)
                        {
                            for (int d = 0; d < binary.size(); d++) 
                            {
                                document = (boObject)binary.get(d);
                                if(document.getAttribute("fileName").getValueString().endsWith(".eml"))
                                {
                                    logger.finest("Destroy EML");
                                    document.destroy();                                    
                                }
                            }
                            Object creator = null;
                            for (int d = 0; d < anexosToDestroy.size(); d++) 
                            {                                
                                document = (boObject)anexosToDestroy.get(d);
                                creator = document.getAttribute("CREATOR").getValueObject();
                                if(creator == null)
                                {
                                    logger.finest("Destroy Anexo");
                                    document.destroy();                                    
                                }
                                
                            }                            
                        }                                                   
                    }
                }                
            }
            //removeParticipants(manager,programRuntimeBoui);        
            //removeActivitys(manager,programRuntimeBoui);
            //removeVariables(manager,programRuntimeBoui);                                
            //removeWaits(manager,programRuntimeBoui);                
            //removeSerialObjects(manager,programRuntimeBoui);
            
            result = true;
//        } 
//        catch(boRuntimeException e) 
//        {
//            result = false;
//        }
//        finally 
//        {
//            if(result)
//            {     
//                result = connManager.commitOnlyDatabaseTransaction();
//            }
//            else
//            {
//                connManager.rollbackOnlyDatabaseTransaction();
//            }
//        }
        return result;
    }
    

    public static boolean NremoveProgram(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {   
        boolean result = false;
        boObject program = manager.getObject(programRuntimeBoui) ;
        program.destroyForce();
        
//            removeParticipants(manager,programRuntimeBoui);        
//            removeActivitys(manager,programRuntimeBoui);
//            removeVariables(manager,programRuntimeBoui);                                
//            removeWaits(manager,programRuntimeBoui);                
//            removeSerialObjects(manager,programRuntimeBoui);
//            manager.destroyObject(program);
        return true;
    }
    
    public static void removeActivity(xwfBoManager manager, long activityBoui) throws boRuntimeException
    {
        List values = new ArrayList();
        bridgeHandler bridge = null;
        boObject activity = null;
        boObject variable = null;
        boObject value = null;
        long boui = 0;
                
            activity = manager.getObject(activityBoui);      
            if(activity.getAttribute("message") != null)
            {
                variable = activity.getAttribute("message").getObject();  
                if(variable != null)
                {
                    boui = variable.getAttribute("value").getValueLong();
                    if(boui > 0)
                    {
                        values.add(String.valueOf(boui));
                    }           
                    values.add(String.valueOf(variable.getBoui()));
                }                
            } 
            if("xwfWaitResponse".equals(activity.getName())) // activity.getAttribute("saveOn") != null)
            {
                variable = activity.getAttribute("saveOn").getObject();
                if(variable != null)
                {
//                    activity.getAttribute("sendActivity").setValueObject(null);
//                    activity.getAttribute("saveOn").setValueObject(null);    
//                    activity.getAttribute("waitFrom").setValueObject(null);
//                    activity.getAttribute("receiveActivity").setValueObject(null);
//                    manager.updateObject(activity);
                    boui = variable.getAttribute("value").getValueLong();
                    if(boui > 0)
                    {
                        values.add(String.valueOf(boui));
                    }                   
                    values.add(String.valueOf(variable.getBoui()));
//                    manager.destroyObject(variable);                                    
                }
            }
//            else if("xwfActivityReceive".equals(activity.getName()))
//            {
//                activity.getAttribute("waitingResponse").setValueObject(null);
//                activity.getAttribute("relatedProgram").setValueObject(null);
//                manager.updateObject(activity);
//            }
            else if("xwfActivityChoice".equals(activity.getName()))
            {
                bridge  = activity.getBridge("options"); 
                bridge.beforeFirst();
                while(bridge.next())
                {
                    values.add(String.valueOf(bridge.getObject().getBoui()));
                }                
//                bridge.truncate();
//                manager.updateObject(activity);
            }
            else if("xwfActivityDecision".equals(activity.getName()))
            {
                boui = activity.getAttribute("no").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
//                    activity.getAttribute("no").setValueObject(null);
                }
                boui = activity.getAttribute("yes").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
//                    activity.getAttribute("yes").setValueObject(null);
                }                                            
//                manager.updateObject(activity);
            }            
            bridge = activity.getBridge("variables");
            bridge.beforeFirst();
            while(bridge.next())
            {
                variable = bridge.getObject();
                boui = variable.getAttribute("value").getValueLong();
                if(boui > 0)
                {
                    values.add(String.valueOf(boui));
//                    variable.getAttribute("value").setValueObject(null);                    
//                    manager.updateObject(variable);                                        
                }
                values.add(String.valueOf(variable.getBoui())); 
//                  manager.destroyObject(variable);                
            }
//            bridge.truncate();   
//            manager.updateObject(activity);
            xwfAnnounceImpl.removeAnnouncer(activity);
//            try
//            {
//                manager.destroyObject(activity);
//                activity.destroyForce();
//            }            
//            catch (boRuntimeException e)
//            {
                values.add(String.valueOf(activity.getBoui()));
//            }
        
        List binary = null;
        for (int i = 0; i < values.size(); i++) 
        {            
            value = manager.getObject(Long.parseLong((String)values.get(i)));
//            if(value.getAttribute(xwfHelper.VALUE_OBJECT) != null)
//            {
//               value.getAttribute(xwfHelper.VALUE_OBJECT).setValueObject(null);
//               value.getBridge(xwfHelper.VALUE_LIST).truncate();
//               manager.updateObject(value);
//            }
            try 
            {
                if("message".equals(value.getName()) || "message".equals(value.getBoDefinition().getBoSuperBo()))
                {
                    binary = value.getObjectBinary().getBinary();
                }
                value.destroyForce(); 
                if(!value.exists() && binary != null)
                {
                    boObject document = null;
                    for (int d = 0; d < binary.size(); d++) 
                    {
                        document = (boObject)binary.get(d);
                        if(document.getAttribute("fileName").getValueString().toLowerCase().endsWith(".eml"))
                        {
                            document.destroy();                                    
                        }
                    }
                }                
            } 
            catch (Exception ex) 
            {     
                //ignore another connection to this object, will be destroy in removeVariables
            }             
        }
                
    }
    
    private static void removeActivitys(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
        List values = new ArrayList();
        bridgeHandler bridge = null;
        boObject activity = null;
        boObject variable = null;
        boObject value = null;    
        long boui = 0;
        boObjectList activitys = manager.listObject("SELECT xwfActivity WHERE program = " + programRuntimeBoui,false,false);
        activitys.beforeFirst();
        while(activitys.next())
        {        
            xwfActionHelper.removeActivity(manager, activitys.getCurrentBoui());           
        }
                
    }
    private static void removeVariables(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
//        Hashtable values = new Hashtable();
        List values = new ArrayList();
        String key = null;
        boObject object = null;
        boObject parent = null;
        boObject program = manager.getObject(programRuntimeBoui);
         bridgeHandler variables = program.getBridge("variables");
        variables.beforeFirst();
        while(variables.next())
        {
            object = variables.getObject();
            key = object.getAttribute("value").getValueString();
            if(key != null && !"".equals(key))
            {
//                values.put(key,String.valueOf(object.getBoui()));
                values.add(key);
                object.getAttribute("value").setValueObject(null);
                manager.updateObject(object);                            
            }   
            values.add(String.valueOf(object.getBoui()));            
//            manager.destroyObject(object);
        }
        variables.truncate();
        manager.updateObject(program);
//        Enumeration keys = values.keys();
//        while(keys.hasMoreElements()) 
//        {      
        for (int i = 0; i < values.size(); i++) 
        {         
//            key = (String)keys.nextElement();
//            object = manager.getObject(Long.parseLong(key));
            object = manager.getObject(Long.parseLong((String)values.get(i)));            
            if(object.getAttribute(xwfHelper.VALUE_OBJECT) != null)
            {            
                object.getAttribute(xwfHelper.VALUE_OBJECT).setValueObject(null);
                object.getBridge(xwfHelper.VALUE_LIST).truncate();
                manager.updateObject(object);
            }
            manager.destroyObject(object);                            
        }        
    }    
    
    private static void removeParticipants(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
        List values = new ArrayList();
        boObject object = null;
        long boui = 0;
        boObject program = manager.getObject(programRuntimeBoui);
        bridgeHandler participants = program.getBridge("participants");
        participants.beforeFirst();
        while(participants.next())
        {
            object = participants.getObject();
            boui = object.getAttribute("value").getValueLong();
            if(boui > 0)
            {
                values.add(String.valueOf(boui));
                object.getAttribute("value").setValueObject(null);
                manager.updateObject(object);                
            }
            values.add(String.valueOf(object.getBoui()));
//            manager.destroyObject(object);
        }
        participants.truncate();
        manager.updateObject(program);
        for (int i = 0; i < values.size(); i++) 
        {            
            object = manager.getObject(Long.parseLong((String)values.get(i)));
            if(object.getAttribute(xwfHelper.VALUE_OBJECT) != null)
            {
               object.getAttribute(xwfHelper.VALUE_OBJECT).setValueObject(null);
               object.getBridge(xwfHelper.VALUE_LIST).truncate();
               manager.updateObject(object);
            }
            try 
            {
                manager.destroyObject(object);  
            } 
            catch (Exception ex) 
            {     
                //ignore another connection to this object, will be destroy in removeVariables
            }                
        }          
    }        
    private static void removeWaits(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
        boObject object = null;
        boObjectList list = manager.listObject("SELECT xwfWait WHERE program = " + programRuntimeBoui,false,false);
        list.beforeFirst();
        while(list.next())
        {
            object = list.getObject();
            manager.destroyObject(object);
        }
    }       
    private static void removeSerialObjects(xwfBoManager manager, long programRuntimeBoui) throws boRuntimeException
    {
        boObject object = null;
        boObjectList list = manager.listObject("SELECT xwfSerialObject WHERE program = " + programRuntimeBoui,false,false);
        list.beforeFirst();
        while(list.next())
        {
            object = list.getObject();            
            manager.destroyObject(object);
        }
    }       
    */
    public static void errorHandle(xwfBoManager xm, boObject notify_user, Exception e)
    {
      try
      {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(bos);
        e.printStackTrace(ps);
        String message = bos.toString();
        if(message!= null && message.length() > 4000)
        {
            message = message.substring(0,3999);
        }
        xwfAnnounceImpl.addAnnounce(message, notify_user, xm.getProgram(), xm,true);
        if(xm.getDocHTML() != null)
        {
            xm.getDocHTML().addErrorMessage(MessageLocalizer.getMessage("ERROR_UNEXPECTED_ERROR_PLEASE_CONTACT_THE_WORKFLOW_ADMINISTRATOR"));
        }
        else
        {
            logger.severe(MessageLocalizer.getMessage("ERROR_CREATING_ANNOUNCE"),e);
        }
      }
      catch(boRuntimeException ne)
      {
      }
    }
    
    public static boolean interruptProgram(String status, xwfBoManager xbm, xwfControlFlow xcf)throws boRuntimeException
    {
      try{
        xbm.getProgram().getStateAttribute("runningState").setValueString(status);
        String prog_boui_s = Long.toString(xbm.getProgBoui());
        String cancel_boql = "select xwfActivity STATE open or create where program = "+prog_boui_s;
        xcf.interruptActivity(status, cancel_boql, true);
        boObjectList bol = xbm.listObject("select xwfWait where program = "+prog_boui_s+" and done='0'", false);
        bol.beforeFirst();
        while(bol.next())
        {
          bol.getObject().destroy();
        }
        
        xbm.updateObject(xbm.getProgram());
        return true;
      }catch(Exception e){ return false; }
    }
    /**
     * Cria um fluxo de trabalho com base num tipo de boObject.
     * @param EngineGate, ligação core do workflow.
     * @param activityName, nome do boObject a criar um fluxo de trabalho.
     * @return activity, actividade gerada no ambito do fluxo de trabalho.
     */        
    public static boObject createEmbeddedProgram(EngineGate engine,String activityName) throws boRuntimeException
    {
        String label = xwfFunctions.getIntelligentLabelEmbeddedProgram(engine,activityName,engine.getBoManager().getDocHTML().getObjectType());
        boObject program = engine.getBoManager().createObject("xwfProgramRuntime");
        program.getAttribute("labelFormula").setValueString("message.name");
        program.getAttribute("beginDate").setValueDate(new Date());
        engine.getBoManager().setProgram(program.getBoui());                
        return ((xwfEngineGate)engine).getManager().createMessageActivity(activityName,label,null, engine.getBoManager().getDocHTML().getObjectType());
    }
    /**
     * Cria um fluxo de trabalho com base num tipo de boObject.
     * @param EngineGate, ligação core do workflow.
     * @param activityName, nome do boObject a criar um fluxo de trabalho.
     * @param message, boObject a incluir na actividade
     * @return activity, actividade gerada no ambito do fluxo de trabalho.
     */    
    public static boObject createEmbeddedProgram(EngineGate engine,String activityName,boObject message) throws boRuntimeException
    {
        String label = xwfFunctions.getIntelligentLabelEmbeddedProgram(engine,activityName,message.getName());
        boObject program = engine.getBoManager().createObject("xwfProgramRuntime");
        program.getAttribute("labelFormula").setValueString("message.name");
        program.getAttribute("beginDate").setValueDate(new Date());
        engine.getBoManager().setProgram(program.getBoui());       
        engine.getBoManager().updateObject(program);
        return ((xwfEngineGate)engine).getManager().createMessageActivity(activityName,label,message, message.getName());
    }    
    /**
     * Cria um Ebo_Queue para para iniciar um program xwf para este boObject.     
     * @param starterConfig, xwfStarterConfig configurado para este boObject.
     * @param object, objecto de input para o fluxo de trabalho.
     */
    private static void queueStartProgram(boObject starterConfig, boObject object) throws boRuntimeException 
    {           
        boObject queue = boObject.getBoManager().createObject(starterConfig.getEboContext(),"Ebo_Queue");
        queue.getAttribute("toperf").setValueLong(starterConfig.getEboContext().getBoSession().getPerformerBoui());
        String code = "xwfActionHelper.startProgram(ctx," + starterConfig.getBoui() + "," + object.getBoui() + ");";
        ((Ebo_QueueImpl)queue).addImportClass("netgest.xwf.common.xwfActionHelper");
        ((Ebo_QueueImpl)queue).setContextVarName("ctx");
        ((Ebo_QueueImpl)queue).addCode(code,Ebo_QueueImpl.JAVA_CODE);                
        queue.update();                   
    }  
    /**
     * Cria um Ebo_Queue para para iniciar um program xwf para este boObject caso seja automático.
     * @param object, objecto de input para o fluxo de trabalho.
     */
    public static void autoQueueStartProgram(boObject object) throws boRuntimeException 
    {           
    	//TODO:MYSQL
//        boObject starterConfig = getStarterConfig(object);
//        if(starterConfig != null && "1".equals(starterConfig.getAttribute("automatic").getValueString()))
//        {
//            queueStartProgram(starterConfig,object);
//        }            
    }    
    /**
     * Cria um Ebo_Queue para para iniciar um program xwf para este boObject caso seja manual.
     * @param object, objecto de input para o fluxo de trabalho.
     */
    public static void manualQueueStartProgram(boObject object) throws boRuntimeException 
    {           
        boObject starterConfig = getStarterConfig(object);
        if(starterConfig != null && "0".equals(starterConfig.getAttribute("automatic").getValueString()))
        {
            queueStartProgram(starterConfig,object);        
        }            
    }      
    /**
     * Devolve um xwfStarterConfig caso exista para este boObject.
     * @param object, objecto de origem do fluxo.
     * @return xwfStarterConfig,  xwfStarterConfig caso exista , null caso contrário.  
     */
    private static boObject getStarterConfig(boObject object) throws boRuntimeException 
    {
        
        String haveStarterConfig  =  (String) OBJECTSSTARTCONFIG.get(object.getName() );
        boObject result = null;
        if ( haveStarterConfig == null )
        {
            StringBuffer sb = new StringBuffer("SELECT xwfStarterConfig WHERE ");
            sb.append(" object in (").append("SELECT Ebo_ClsReg WHERE name='").append(object.getName()).append("')");
            sb.append(" AND ( beginDate IS NULL");
            sb.append(" OR TO_DATE(TO_CHAR(beginDate,'DD-MM-YYYY HH:MI'),'DD-MM-YYYY HH:MI') < TO_DATE(TO_CHAR(SYSDATE,'DD-MM-YYYY HH:MI'),'DD-MM-YYYY HH:MI'))");
            sb.append(" AND ( endDate IS NULL");
            sb.append(" OR TO_DATE(TO_CHAR(endDate,'DD-MM-YYYY HH:MI'),'DD-MM-YYYY HH:MI') > TO_DATE(TO_CHAR(SYSDATE,'DD-MM-YYYY HH:MI'),'DD-MM-YYYY HH:MI'))");
            boObjectList starterConfigList = boObjectList.list(object.getEboContext(), sb.toString() );
            if(starterConfigList.getRecordCount() > 0)
            {
                
                xwfBoManager manager = new xwfBoManager(object.getEboContext(),null);
                boolean found = false;
                starterConfigList.beforeFirst();            
                Object val = null;
                boObject program = null;
                xwfECMAevaluator eval = null;                
                while(starterConfigList.next() && !found)
                {             
                    program = starterConfigList.getObject();
                    String xep = program.getAttribute("condition").getValueString();
                    if(xep != null && !"".equals(xep.trim()))
                    {                
                        eval = new xwfECMAevaluator();
                        eval.setVariable("Objecto",boObject.class,object, object.getName());
                        val = eval.eval(manager,xep);
                        if(val != null && val instanceof Boolean)
                        {
                            if(((Boolean)val).booleanValue() == true)
                            {
                                found = true;
                                result = starterConfigList.getObject();       
                            }
                        }                    
                    }
                    else
                    {
                        found = true;
                        result = starterConfigList.getObject();
                    }                
                }                
            }
            else
            {
                OBJECTSSTARTCONFIG.put(object.getName(), DUMMY );
            }
            
        }
        return result;
    }
    public static boolean sendReadReceipt(EngineGate engine, boObject activity, boObject performer) throws boRuntimeException
    {
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = value.getAttribute(xwfHelper.VALUE_OBJECT).getObject();
        if(message != null)
        {
//            boObject receiptMessage = MessageServer.sendReceipt(engine.getBoManager().getContext(), message, performer);
            boObject receiptMessage = MessageServer.sendReceipt(engine, message, performer,activity.getBoui());
            return true;
        }
        return false;
    }
    
    public static boObject sendDeliverReceipt(EngineGate engine, boObject receiptMessage, boObject performer) throws boRuntimeException
    {
        boObject newActivity = null;             
        if(receiptMessage != null)
        {
            newActivity = ((xwfEngineGate)engine).getManager().createMessageActivity("xwfActivitySend", receiptMessage.getAttribute("name").getValueString(), receiptMessage, receiptMessage.getName());
        }
//        newActivity.getAttribute("branch").setValueString(activity.getAttribute("sid").getValueString());
        engine.setActivityState(newActivity.getBoui(),XwfKeys.ACTION_CLOSE_KEY);
        newActivity.update();
        return newActivity;
    }
    
  /**
   * Executa negócio associado a uma xwfActivitySend, e processa o seu envio. 
   * @param activityBoui, boui da actividade a alterar.
   * @param state, novo estado da actividade.    
   */    
    public static boolean sendActivity(XwfController controller, boObject xwfActivitySend) throws boRuntimeException    
    {
        EngineGate engine = controller.getEngine();
        boolean result = false;        
        boObject object = xwfHelper.getMasterObject(engine,xwfActivitySend);                                    
        if(object != null && XwfValidator.valid(controller.getDocHTML(),object))
        {
            String register = object.getAttribute("register").getValueString();                                
            if(!"1".equals(register))
            {
                if(xwfMessage.deliverMessage((xwfEngineGate)engine,xwfActivitySend))
                {
                    controller.setOverrideRuntimeActivity(-1);
                    result = true;                                                                
                }                                    
            }
            else
            {
//                if(object.exists() || controller.getEngine().getExecutionMode() == xwfHelper.PROGRAM_EXEC_TEST_MODE)
//                {
                    xwfFunctions.setIntelligentLabel(engine,xwfActivitySend);
                    object.getAttribute("statusMessage").setValueString("1");
                    Date dtdoc = object.getAttribute("dtdoc").getValueDate();
                    if(dtdoc == null)
                    {
                        object.getAttribute("dtdoc").setValueDate(new Date());
                    }         
                    engine.getBoManager().updateObject(object);
                    bridgeHandler bridge = engine.getProgramRuntime().getBridge("message");
                    if(!bridge.haveBoui(object.getBoui()))
                    {
                        bridge.add(object.getBoui());
                    }
                    engine.getBoManager().updateObject(engine.getProgramRuntime());
                    engine.setActivityState(xwfActivitySend.getBoui(),XwfKeys.ACTION_SEND_KEY);
                    controller.setOverrideRuntimeActivity(-1);
                    result = true;
//                }
            }
            xwfFunctions.setIntelligentLabel(engine,xwfActivitySend);
        }    
        return result;
    }  
  /**
   * Altera o estado da actividade para reaberto, se o programa estiver fechado reabre este.
   * @param manager, xwfBoManager controlador de objectos no contexto do xwf.
   * @param program, fluxo de trabalho em execução.
   * @param activity, actividade a alterar o estado.    
   */      
    public static void reopenActivity(xwfBoManager manager,boObject program, boObject activity) throws boRuntimeException    
    {    
        if("close".equals(activity.getStateAttribute("runningState").getValueString()))
        {       
            if("close".equals(activity.getStateAttribute("runningState").getValueString()))
            {
                reopenProgram(manager,program);
            }
            activity.getStateAttribute("runningState").setValue("reopen");
            activity.getAttribute("endDate").setValueString(null);
            activity.getAttribute("unique_sid").setValueString("-1");            
            xwfHelper.objectStateSet(manager, "0");
        }            
    }
    public static void reopenProgram(xwfBoManager manager,boObject program) throws boRuntimeException
    {
        program.getStateAttribute("runningState").setValue("reopen");
        Date init = program.getAttribute("beginDate").getValueDate();
        if(init == null)
        {
            program.getAttribute("beginDate").setValueDate(new Date());
        }
        program.getAttribute("endDate").setValueDate(null);
    }     
    public static void openProgram(xwfBoManager xwfm, boObject program) throws boRuntimeException
    {
        program.getStateAttribute("runningState").setValue("open");
        Date init = program.getAttribute("beginDate").getValueDate();
        if(init == null)
        {
            program.getAttribute("beginDate").setValueDate(new Date());
        }
        program.getAttribute("endDate").setValueDate(null);
        xwfHelper.objectStateSet(xwfm, "0");
    }    
    
    public static boolean moveActivitiesToProgram(EngineGate engine, boObject originProg, boObject destinationProg, String activitiesToMove, String activitiesToDel) throws boRuntimeException
    {
        if(activitiesToMove != null)
        {
            String[] moveActivitiesBouis = activitiesToMove.split(",");
            for(int i=0; i<moveActivitiesBouis.length; i++)
            {
                boObject selectedActivity = originProg.getObject(Long.parseLong(moveActivitiesBouis[i]));
                if(moveActivity(engine, originProg, destinationProg, selectedActivity))
                {
                    engine.getBoManager().updateObject(selectedActivity);
                }
            }
        }
        return true;
    }
    
    /**
     * Transfere um fluxo para um outro grupo/pool/utilizador. Cancela o fluxo actual e 
     * associa o program a um novo grupo/utilizador
     * @param activity, xwfActivity a reassignar.
     * @param object, xwfActivityTransfer objecto que contem a informação para a transferência.
     */     
    public static  boolean transferProcess(boObject object)  throws boRuntimeException
    {
        boolean result = false;        
        try
        {
            if("close".equalsIgnoreCase(object.getStateAttribute("runningState").getValueString()))
            {
                return true;
            }
            long program = object.getAttribute("program").getValueLong();
            long para = object.getAttribute("to").getValueLong();
            long de = object.getAttribute("assignedQueue").getValueLong();
            long executante = object.getAttribute("performer").getValueLong();

            xwfEngineGate engine = new xwfEngineGate(object.getEboContext(),
                                    boObject.getBoManager().loadObject(object.getEboContext(), program)
            );

            xwfFunctions.setIntelligentLabel(engine, object);
            
            boObject programObj = engine.getBoManager().getProgram();
            
            bridgeHandler bhVar =  programObj.getBridge("variables");
            bhVar.truncate();
            bridgeHandler bhPar =  programObj.getBridge("participants");
            bhPar.truncate();


            object.getStateAttribute("runningState").setValue("close");
            object.getAttribute("done").setValueString("1");
            object.getAttribute("showTask").setValueString("1");
            object.getAttribute("sid").setValueString("-1");
            object.getAttribute("unique_sid").setValueString("-1");
            bridgeHandler bvars = object.getBridge("variables");
            bvars.beforeFirst();
            boObject vval = null;
            while(bvars.next())
            {
                vval = bvars.getObject().getAttribute("value").getObject();
                if(vval != null)
                {
                    vval.getAttribute("program").setValueLong(program);
                }
            }

            
            //xbm.getProgram().getStateAttribute("runningState").setValueString(status);
            String cancel_boql = "select xwfActivity STATE open or create where program = "+program;
            engine.getManager().getControlFlow().interruptActivity(XwfKeys.ACTION_CANCEL_KEY , cancel_boql, true);
            boObjectList bol = engine.getBoManager().listObject("select xwfWait where program = "+program+" and done='0'", false);
            bol.beforeFirst();
            while(bol.next())
            {
              bol.getObject().destroy();
            }
            
            //vou terminar a transferência
            engine.setActivityState(object.getBoui(),XwfKeys.ACTION_CLOSE_KEY);
            
            
            //limpar o programa            
            programObj.getAttribute("name").setValueObject(null);
            programObj.getAttribute("description").setValueObject(null);
            programObj.getAttribute("labelFormula").setValueObject(null);
            programObj.getAttribute("version").setValueObject(null);
            programObj.getAttribute("programDef").setValueObject(null);
            programObj.getAttribute("status").setValueObject(null);
            programObj.getAttribute("flow").setValueObject(null);
            programObj.getAttribute("returnToSid").setValueObject(null);
            programObj.getAttribute("returnToProg").setValueObject(null);
            programObj.getAttribute("procedures").setValueObject(null);
            programObj.getAttribute("administrator").setValueObject(null);
            
            
            //vou criar a mensagem para receber
            boObject message = boObject.getBoManager().createObject(object.getEboContext(), "messageSystem");
            message.getAttribute("from").setValueLong(de);
            message.getBridge("to").add(para);
            message.getAttribute("name").setValueString(object.getAttribute("label").getValueString());
            message.getAttribute("description").setValueString(object.getAttribute("description").getValueString());
            message.getAttribute("preferedMedia").setValueString("Sgis");
            
            xwfMessage.createRecActv(engine.getManager(),
                    para,
                    message, program);

            engine.getBoManager().updateObject(object);
            engine.getBoManager().updateObject(programObj);
            return true;

        }
        catch (boRuntimeException ex) 
        {       
            try
            {
                if(object != null)
                {
                    CharArrayWriter cw = new CharArrayWriter();
                    PrintWriter pw = new PrintWriter( cw );
                    ex.printStackTrace( pw );
                    pw.close();
                    cw.close();                            
                    object.getAttribute("errormessage").setValueString(cw.toString());
                    object.getAttribute("reassigned").setValueString("-1");
                }
            }
            catch (Exception e)
            {
                
            }
        }               
        catch (Throwable ex) 
        {       
            try
            {
                CharArrayWriter cw = new CharArrayWriter();
                PrintWriter pw = new PrintWriter( cw );
                ex.printStackTrace( pw );
                pw.close();
                cw.close();                            
                object.getAttribute("errormessage").setValueString(cw.toString());
                object.getAttribute("reassigned").setValueString("-1");
            }
            catch (Exception e)
            {
                
            }
        }      
        return result;
    }
 
    public static  boolean defActivity(boObject actv_def)  throws boRuntimeException
    {
        boolean result = false;        
        try
        {
            Controller control =actv_def.getEboContext().getController();
            xwfEngineGate engine = null;
            if("XwfController".equalsIgnoreCase(control.getName()))
            {
                engine = (xwfEngineGate)((XwfController)control).getEngine();
            }
            else
            {
                long program = actv_def.getAttribute("program").getValueLong();
                engine = new xwfEngineGate(actv_def.getEboContext(),
                                    boObject.getBoManager().loadObject(actv_def.getEboContext(), program)
                );
            }
            
            boolean periodica = "1".equals(actv_def.getAttribute("type_recursive").getValueString());
            
            if(!periodica)
            {
                //vou colocar a actividade que depende desta nova em espera no caso de não ser async
                //e vou verificar se devo ou não criar a actividade já.
                boObject actv = actv_def.getAttribute("fromActivity").getObject();
                if(actv != null)
                {                    
                    if(!"1".equals(actv_def.getAttribute("async").getValueString()))
                    {
                        actv.getStateAttribute("runningState").setValue("wait");
                        engine.getBoManager().updateObject(actv);      
                    }
//                    String unique_sid = actv.getAttribute("unique_sid").getValueString();
//                    cf.runSubProg(xwfHelper.STEP_SUB_PROGRAM, unique_sid, new_actv_xml);
                }
                else
                {
//                      cf.runSubProg(xwfHelper.STEP_SUB_PROGRAM, null, new_actv_xml);
                }
            }
            else
            {//definição de tarefas periodicas
            
            }
            actv_def.getStateAttribute("activeStatus").setValue("0");
            actv_def.update();
            result = true;
        }
        catch (boRuntimeException ex) 
        {       
            try
            {
                if(actv_def != null)
                {
                    CharArrayWriter cw = new CharArrayWriter();
                    PrintWriter pw = new PrintWriter( cw );
                    ex.printStackTrace( pw );
                    pw.close();
                    cw.close();                            
                    actv_def.getAttribute("errormessage").setValueString(cw.toString());
                    actv_def.getAttribute("reassigned").setValueString("-1");
                }
            }
            catch (Exception e)
            {
                
            }
        }               
        catch (Throwable ex) 
        {       
            try
            {
                CharArrayWriter cw = new CharArrayWriter();
                PrintWriter pw = new PrintWriter( cw );
                ex.printStackTrace( pw );
                pw.close();
                cw.close();                            
                actv_def.getAttribute("errormessage").setValueString(cw.toString());
                actv_def.getAttribute("reassigned").setValueString("-1");
            }
            catch (Exception e)
            {
                
            }
        }      
        return result;
    }
}