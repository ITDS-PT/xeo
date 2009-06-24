/*Enconding=UTF-8*/
package netgest.xwf.core;

import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.message.GarbageController;
import netgest.bo.message.MessageServer;
import netgest.xwf.*;
import netgest.xwf.common.*;
import java.util.*;
import netgest.bo.runtime.*;
import netgest.bo.security.*;
import netgest.utils.*;
import netgest.bo.def.*;
import netgest.bo.dochtml.*;
import netgest.bo.utils.*;


public class xwfMessage 
{
    public static boolean deliverMessage(xwfEngineGate engine,boObject activity) throws boRuntimeException
    {
        boolean result = false;
        boObject prog = engine.getBoManager().getProgram();
//        boObject activity = controller.getRuntimeActivity();
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = engine.getBoManager().getValueBoObject(value);
//        boObject message = value.getAttribute("valueObject").getObject();        
        if(message != null)
        {
            //rever se fica em wait ou não 
            result = MessageServer.deliverMessage(engine,message,activity.getBoui());
            if(result)
            {
                if(!prog.getBridge("message").haveBoui(message.getBoui()))
                {
                    prog.getBridge("message").add(message.getBoui());
                    engine.getBoManager().updateObject(prog);
                }
            }
        }
        return result;
    }
/*
    public static boolean deliverMessage(XwfController controller) throws boRuntimeException
    {
        boolean result = false;
        boObject prog = controller.getRuntimeProgram();
        boObject activity = controller.getRuntimeActivity();
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = controller.getEngine().getBoManager().getValueBoObject(value);
//        boObject message = value.getAttribute("valueObject").getObject();        
        if(message != null)
        {
            //rever se fica em wait ou não 
            result = MessageServer.deliverMessage(controller,message);
            if(result)
            {
                if(!prog.getBridge("message").haveBoui(message.getBoui()))
                {
                    prog.getBridge("message").add(message.getBoui());
                    controller.getEngine().getBoManager().updateObject(prog);
                }
            }
        }
        return result;
    }
    */
//    public static boolean mergeMessage(XwfController controller) throws boRuntimeException
    public static boolean mergeMessage(EngineGate engine,boObject activity) throws boRuntimeException
    {
        boolean result = false;
//        boObject prog = controller.getRuntimeProgram();
//        boObject activity = controller.getRuntimeActivity();
        boObject variable = activity.getAttribute("message").getObject();
        boObject value = variable.getAttribute("value").getObject();
        boObject message = value.getAttribute("valueObject").getObject();        
        if(message != null)
        {
            result = MessageServer.mergeMessage(engine,message,activity.getBoui());
        }
        return result;
    } 
  public static void receiveMessage(xwfManager manager, long msg_boui, boObject prog) throws boRuntimeException
  {
    EboContext ctx = prog.getEboContext();
    boObject msg = boObject.getBoManager().loadObject(ctx, msg_boui);
    receiveMessage(manager, msg, prog);    
  }
  public static void receiveMessage(xwfManager manager, boObject msg, boObject prog) throws boRuntimeException
  {  
    EboContext ctx = prog.getEboContext();
    
    Hashtable ht = new Hashtable();
    bridgeHandler brdg = msg.getBridge("to");
    processTo(manager, brdg, ht, msg, prog, false);
    brdg = msg.getBridge("cc");
    processTo(manager, brdg, ht, msg, prog, true);
    brdg = msg.getBridge("bcc");
    processTo(manager, brdg, ht, msg, prog, true);
    if(!prog.getBridge("message").haveBoui(msg.getBoui()))
    {
        prog.getBridge("message").add(msg.getBoui());
        manager.getBoManager().updateObject(prog);
    }    
  }
  
  private static void processTo(xwfManager manager, bridgeHandler brdg, Hashtable ht, boObject msg, boObject prog, boolean isCC) throws boRuntimeException
  {
    brdg.beforeFirst();
    while(brdg.next())
    {
      long boui = brdg.getCurrentBoui();
      boObject recv = brdg.getObject().getAttribute("refObj").getObject();
      if(ht.get(new Long(recv.getBoui()))== null)
      {
        if(checkReceiver(recv) && !GarbageController.hasReported(msg,brdg.getObject()))
        {
          createRecActv(manager, recv.getBoui(), msg, prog.getBoui(),isCC);
          ht.put(new Long(recv.getBoui()), new Boolean(true));
        }
        else
          ht.put(new Long(recv.getBoui()), new Boolean(false));
      }      
    }
  }
  
  public static boObject createRecActv(xwfManager manager, long receiver, boObject msg, long prog) throws boRuntimeException
  {    
    return createRecActv(manager, receiver, msg, prog, false);
  }
  
  public static boObject createRecActv(xwfManager manager, long receiver, boObject msg, long prog, boolean isCC) throws boRuntimeException
  {
    boObject actv = manager.createMessageActivity("xwfActivityReceive", msg.getAttribute("name").getValueString(), msg, 
    msg.getBoDefinition().getName(), prog, receiver);
//    actv.getAttribute("assignedQueue").setValueLong(receiver);    
    xwfFunctions.setIntelligentLabel(manager.getBoManager(),manager.getBoManager().getObject(prog),actv, isCC);
    //    actv.update();
    manager.getBoManager().updateObject(actv);
    return actv;
  }
  public static boObject createRecActv(xwfManager manager, long receiver, boObject msg) throws boRuntimeException
  {
    boObject actv = manager.createMessageActivity("xwfActivityReceive", msg.getAttribute("name").getValueString(), msg, 
    msg.getBoDefinition().getName(), -1, receiver);
//    actv.getAttribute("assignedQueue").setValueLong(receiver);
//    actv.update();
    manager.getBoManager().updateObject(actv);
    return actv;
  }
  public static boObject createSendActv(xwfManager manager, long receiver, boObject msg, long prog) throws boRuntimeException
  {
    boObject actv = manager.createMessageActivity("xwfActivitySend", msg.getAttribute("name").getValueString(), msg, 
    msg.getBoDefinition().getName(), prog, receiver);
//    actv.getAttribute("assignedQueue").setValueLong(receiver);
//    actv.update();
    manager.getBoManager().updateObject(actv);
    return actv;
  }
  
  public static boObject[] createWaitActv(xwfManager manager, long[] receiver, boObject msg, long prog, long sendActv) throws boRuntimeException
  {
    boObject actv = null;
    boObject toRet[] = new boObject[receiver.length];
    for (int i = 0; i < receiver.length; i++) 
    {
        actv = manager.createWaitRespActivity(msg.getAttribute("name").getValueString(), msg, 
            msg.getBoDefinition().getName(), receiver[i], prog, sendActv);
//        actv.update();
        manager.getBoManager().updateObject(actv);
        toRet[i] = actv;
    }    
    return toRet;
    
  }
  
  private static boolean checkReceiver(boObject recv)throws boRuntimeException
  { 
     if(XEOUserUtils.isXEOUser(recv))
    {
        return true;
    }
    else
    {
      if("Ebo_Role".equals(recv.getName()) || "Ebo_Group".equals(recv.getName()) || "workQueue".equals(recv.getName())
        || "Ebo_Role".equals(recv.getBoDefinition().getBoSuperBo()) || "Ebo_Group".equals(recv.getBoDefinition().getBoSuperBo())
        || "workQueue".equals(recv.getBoDefinition().getBoSuperBo())
      )
        return true;
      else
        return false;
    }
  }
  public static String getPreferedMediaLabel(boObject message) throws boRuntimeException
  {
    String label = null;
    String preferedMedia = message.getAttribute("preferedMedia").getValueString();
    if("E-Mail".equals(preferedMedia))
    {
        label = "Email";
    }
    else if("Fax".equals(preferedMedia))
    {
        label = "Fax";                
    }
    else if("Letter".equals(preferedMedia))
    {
        label = "Carta";                
    }    
    else if("Sgis".equals(preferedMedia))
    {
        label = "XEO";                
    }
    else if("BV".equals(preferedMedia))
    {
        label = "Balcão Virtual";                
    }
    else if("SMS".equals(preferedMedia))
    {
        label = "SMS";                
    }  
    else
    {
        label = "Mensagem";
    }
    return label;
  }
  public static boolean sendMessage(boObject message) throws boRuntimeException
  {
//      docHTML dochtml = new docHTML(message.getEboContext(),9999);
//      dochtml.poolSetStateFull();
//      XwfController controller = new XwfController(dochtml);
//      ControllerFactory.setControllerByForce(dochtml,controller);
      xwfEngineGate engine = new xwfEngineGate(message.getEboContext());  
      boObject activity = xwfActionHelper.createEmbeddedProgram(engine,"xwfActivitySend",message);
      engine.getBoManager().updateObject(activity);
      // Fazer uma leitura, para se poder gravar novamente
      activity = engine.getBoManager().getObject(activity.getBoui());
//      controller.setOverrideRuntimeActivity(activity.getBoui());      
//      result = xwfActionHelper.sendActivity(controller,activity);    
      return xwfMessage.deliverMessage(engine,activity);      
  }
}