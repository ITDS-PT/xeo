/*Enconding=UTF-8*/
package netgest.bo.message.server;
import java.util.ArrayList;
import java.util.Date;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.impl.document.merge.MergeHelper;
import netgest.bo.impl.document.print.PrintHelper;

import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.xwf.EngineGate;
import netgest.bo.system.Logger;
import netgest.xwf.core.*;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class LetterServer implements MediaServer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.media.LetterServer");
    private static final String LETTER = "Letter";
    private static final String ERROR_NO_TEMPLATE = "Para enviar uma Carta é necessário escolher o modelo.";
//    private XwfController controller = null;
    private EngineGate engine = null;
    private long activityBoui = -1;    
    private static final String[] bridges = {"to", "cc", "bcc"};
    /**
     * 
     * @Company Enlace3
     * @since 
     */
//    public LetterServer(XwfController controller)
//    {
//        this.controller = controller;
//    }
    public LetterServer(EngineGate engine, long activityBoui)
    {
        this.engine = engine;
        this.activityBoui = activityBoui;        
    }
    public void read() throws boRuntimeException
    {
        //nothing read
    }

    //ao enviar é necessário efectuar o merge ao documento que será enviado
    //se existir o documento é feito o re-merge
    public boolean send(boObject message, boolean saveBinary) throws boRuntimeException
    {
        //criar um programa
        boObject program = null;
        return send(program, message, saveBinary);
    }
    public boolean send(Object context, boObject message, boolean saveBinary) throws boRuntimeException
    {
//        if(message.getAttribute("letterFormat").getValueObject() != null ||
//            message.getAttribute("faxFormat").getValueObject() != null)
//        {
          if(setSended(message))
          {
            message.getAttribute("dtdoc").setValueDate(new Date());
            return true;
          }
          return false;
            
//        }
//        else
//        {
////            message.addErrorMessage(message.getAttribute("letterFormat"), ERROR_NO_TEMPLATE);
//            this.addErrorMessage(ERROR_NO_TEMPLATE);
//        }
//        return false;
    }
    
    public boolean mergeDocuments(boObject message) throws boRuntimeException
    {
        if(message.getAttribute("letterFormat").getValueObject() != null ||
            message.getAttribute("faxFormat").getValueObject() != null)
        {
            if(treat(message, true))
            {
                return true;
            }
        }
        else
        {
            this.addErrorMessage(ERROR_NO_TEMPLATE);
        }
        return false;
    }
    
    public boObject sendReceipt(boObject message, boObject performer) throws boRuntimeException
    {
        return null;
    }

    public boolean deleteMessage(String messageid) throws boRuntimeException
    {
        return false;
    }

    public boolean deleteMessage(boObject message) throws boRuntimeException
    {
        return false;
    }
    
    private boolean setSended(boObject originalMessage) throws boRuntimeException
    {
        boBridgeIterator it;
        boObject document = null;
        ArrayList receivers = new ArrayList();
        for (int i = 0; i < bridges.length; i++) 
        {
            it = originalMessage.getBridge(bridges[i]).iterator();
            it.beforeFirst();
            boObject deliveryMsg;            
            while(it.next())
            {
                
                deliveryMsg = it.currentRow().getObject();
                if(!"1".equals(deliveryMsg.getAttribute("already_send").getValueString()))
                {
                    if(LETTER.equals(deliveryMsg.getAttribute("media").getValueString()))
                    {                        
                        deliveryMsg.getAttribute("already_send").setValueString("1");
                        if(deliveryMsg.getAttribute("refObj").getValueObject() != null)
                        {
                            receivers.add(new Long(deliveryMsg.getAttribute("refObj").getValueLong()));
                        }
                    }
                }
            }
        }
        if(MessageUtils.isToWaitResponse(originalMessage) && receivers.size() > 0)
        {
            logger.finer("Vou criar o wait para msg!");
            originalMessage.getEboContext().getBoSession().setProperty("creatingWaitMsg", Boolean.TRUE);
            try
            {
                boObject program = engine.getBoManager().getProgram();
                xwfManager man = new xwfManager(originalMessage.getEboContext(), program);
                xwfMessage.createWaitActv(man, MessageUtils.toLongReceivers(receivers), originalMessage, program.getBoui(), 
                    this.activityBoui);
                program.update();
            }
            catch(boRuntimeException e)
            {
                throw e;
            }
            finally
            {
                originalMessage.getEboContext().getBoSession().removeProperty("creatingWaitMsg");
            }
        }
        else
        {
            originalMessage.update();
        }
        return true;
    }
    
    private boolean treat(boObject originalMessage, boolean onlyPrint) throws boRuntimeException
    {
        boBridgeIterator it;
        boObject document = null;
        boObject message = originalMessage.cloneObject();
        boObject fromDeliver = originalMessage.getAttribute("from").getObject();
        message.getAttribute("from").setValueLong(fromDeliver.cloneObject().getBoui());
        //bcc não deverá aparecer
        message.getBridge("bcc").truncate();
        for (int i = 0; i < bridges.length; i++) 
        {
            it = originalMessage.getBridge(bridges[i]).iterator();
            it.beforeFirst();
            boObject deliveryMsg;            
            while(it.next())
            {
                
                deliveryMsg = it.currentRow().getObject();                
                if(!"1".equals(deliveryMsg.getAttribute("already_send").getValueString()))
                {
                    message.getBridge("to").truncate();
                    message.getBridge("to").add(deliveryMsg.cloneObject().getBoui());
                    if(LETTER.equals(deliveryMsg.getAttribute("media").getValueString()))
                    {
                        boObject generatedDoc = MessageUtils.getBinaryBySourceBoui(message, deliveryMsg.getBoui());
                        if(generatedDoc == null)
                        {
                            if(!merge(originalMessage, message, deliveryMsg))
                            {
                                this.addErrorMessage("Não possível processar o template.");
                            }
                        }
                        else
                        { 
                            remerge(message, generatedDoc);
                        }
                        if(!onlyPrint)
                        {
                            deliveryMsg.getAttribute("already_send").setValueString("1");
                        }
                    }
                }
            }
        }
        return true;
    }
    public static boolean merge(boObject originalMessage, boObject message, boObject deliveryMsg) throws boRuntimeException
    {
        boObject template = message.getAttribute("letterFormat").getObject();
        if(template == null || template.getBoui() <= 0)
        {
            template = message.getAttribute("faxFormat").getObject();
        }
        if(template != null && template.getBoui() > 0)
        {
            long boui = MergeHelper.merge(message, template);            
            if(boui > 0)
            {
                boObject toRemove = MessageUtils.getBinaryBySourceBoui(originalMessage, deliveryMsg.getBoui());
                if(toRemove != null)
                {
                    originalMessage.getObjectBinary().remove(toRemove);
                }
                
                boObject eboDoc = boObject.getBoManager().loadObject(originalMessage.getEboContext(), boui);
                eboDoc.getAttribute("srcObj").setObject(deliveryMsg);
                
                originalMessage.getObjectBinary().setBinary(eboDoc);
                return true;
            }
        }
        return false;
    }
    
    public static void remerge(boObject message, boObject documentGenerated) throws boRuntimeException
    {
        boObject template = message.getAttribute("letterFormat").getObject();
        if(template == null || template.getBoui() <= 0)
        {
            template = message.getAttribute("faxFormat").getObject();
        }
        if(template != null && template.getBoui() > 0)
        {
            MergeHelper.reMerge(message, template, documentGenerated);
        }
    }
    private void addErrorMessage(String error)
    {
        if(this.engine != null)
        {
            engine.addErrorMessage(error);
        }
    }
    
    public boolean releaseMsg(boObject message, boObject performer)
    {
        return true;
    }
}