/*Enconding=UTF-8*/
package netgest.bo.message;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.dochtml.*;
import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.message.server.EmailServer;
import netgest.bo.message.server.FaxServer;
import netgest.bo.message.server.LetterServer;
import netgest.bo.message.server.MediaServer;
import netgest.bo.message.server.SMSServer;
import netgest.bo.message.server.SgisServer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boObject;
import netgest.bo.workflow.*;
import netgest.utils.XEOUserUtils;
import netgest.xwf.core.*;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.system.Logger;
import netgest.xwf.EngineGate;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class MessageServer 
{

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.message.MessageServer");   
    
    private static final String CHANNEL_NOT_DEFINED_1 = "O destinatário [";
    private static final String CHANNEL_NOT_DEFINED_2 = "] não tem endereço definido para o canal escolhido.";
    private static final String PREFERED_CHANNEL_NOT_DEFINED_1 = "O destinatário [";
    private static final String PREFERED_CHANNEL_NOT_DEFINED_2 = "] não tem um canal preferido definido.";
    
    public static final int REPLAY = 0;
    public static final int REPLAY_ALL = 1;
    public static final int FORWARD = 2;
    
    public static final boolean SAVEBINARY = true;
    
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public MessageServer()
    {
    }
    public static void readAll(EboContext ctx)
    {
        try
        {
            MediaServer man = new EmailServer(ctx);
            man.read();
        }
        catch (boRuntimeException e)
        {
            logger.severe("Error synchronizing mailBox from user: ", e);
        }
    }

//    public static boObject sendReceipt(EboContext ctx, boObject message, boObject performer)
    public static boObject sendReceipt(EngineGate engine,boObject message, boObject performer,long activityBoui)
    {
        //verificar o modo preferencial de entrega da mensagem
        //acertar o delivery message de cada um dos destinatários
        //se a mensagem tiver um canal definido então todos o recepientes
        //deverão receber por esse canal caso tiverem o endereço do canal definido
        //senão tiverem o remetente deverá ser informado de modo a alterar o canal para esse
        //utilizador.
        //caso o delivery message tenha um canal definido este sobrepõem-se ao canal 
        //definido na mensagem 
        boObject toRet = null;
//        XwfController controller = (XwfController)ctx.getController();
        try
        {
            String mediaType = message.getAttribute(MessageUtils.ATT_PRF_MEDIA).getValueString();
//            controller = (XwfController)message.getEboContext().getController();
            if(MessageUtils.EMAIL.equals(mediaType) || MessageUtils.BV.equals(mediaType))
            {
                logger.finest("SENDING RECEIPT");
                MediaServer man = new EmailServer(engine,activityBoui);
                toRet = man.sendReceipt(message, performer);
            }
            else if(MessageUtils.FAX.equals(mediaType))
            {
                logger.finest("SENDING RECEIPT");
                MediaServer man = new FaxServer(engine,activityBoui);
                toRet = man.sendReceipt(message, performer);
            }
            else if(MessageUtils.LETTER.equals(mediaType) ||
                MessageUtils.PHONE.equals(mediaType) ||
                MessageUtils.CONVERSATION.equals(mediaType) ||
                MessageUtils.SMS.equals(mediaType)
                )
            {
                //não faz nada
            }
            else if(MessageUtils.SGIS.equals(mediaType))
            {
                logger.finest("SENDING RECEIPT");
                MediaServer man = new SgisServer(engine,activityBoui);
                toRet = man.sendReceipt(message, performer);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
            engine.addErrorMessage("Erro inesperado. Tente novamente." );
        }
        return toRet;
    }
    
    public static boolean mergeMessage(boObject message) throws boRuntimeException
    { 
        boolean printed = false;

        try
        {
            StringBuffer toPrint = new StringBuffer();
            String mediaType = message.getAttribute(MessageUtils.ATT_PRF_MEDIA).getValueString();            
            if(MessageUtils.FAX.equals(mediaType))
            {
                printed = faxPrint(null, message,-1);
            }
            else if(MessageUtils.LETTER.equals(mediaType))
            {
                printed = letterPrint(null, message,-1);
            }
            else if(MessageUtils.PHONE.equals(mediaType) ||
                MessageUtils.CONVERSATION.equals(mediaType) ||
                MessageUtils.SGIS.equals(mediaType) ||
                MessageUtils.BV.equals(mediaType) ||
                MessageUtils.SMS.equals(mediaType) ||
                MessageUtils.EMAIL.equals(mediaType)
            )
            {
                //não faz nada
            }
            else
            {
                printed = print(null, message,-1);
            }       
        }
        catch (Exception e)
        {
            logger.severe(e);
            String msg = e.getMessage();
            if(msg != null)
            {
                msg = msg.replaceAll("<", "&lt;");
                msg = msg.replaceAll(">", "&gt;");
                message.addErrorMessage(msg);
            }
        }
        return printed;
    }
    
//    public static boolean mergeMessage(XwfController controller, boObject message)
    public static boolean mergeMessage(EngineGate engine, boObject message,long activityBoui)
    { 
        boolean printed = false;
        try
        {
            StringBuffer toPrint = new StringBuffer();             
            if(!PostInformation.generateMessageInformation(toPrint, message, engine.getBoManager().getContext()))
            {
                return false;
            }            
            String mediaType = message.getAttribute(MessageUtils.ATT_PRF_MEDIA).getValueString();            
            if(MessageUtils.FAX.equals(mediaType))
            {
                printed = faxPrint(engine, message,activityBoui);
            }
            else if(MessageUtils.LETTER.equals(mediaType))
            {
                printed = letterPrint(engine, message,activityBoui);
            }
            else if(MessageUtils.PHONE.equals(mediaType) ||
                MessageUtils.CONVERSATION.equals(mediaType) ||
                MessageUtils.SGIS.equals(mediaType) ||
                MessageUtils.BV.equals(mediaType) ||
                MessageUtils.SMS.equals(mediaType) ||
                MessageUtils.EMAIL.equals(mediaType)
            )
            {
                //não faz nada
            }
            else
            {
                printed = print(engine, message,activityBoui);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
            String msg = e.getMessage();
            if(msg != null)
            {
                msg = msg.replaceAll("<", "&lt;");
                msg = msg.replaceAll(">", "&gt;");
                engine.addErrorMessage(msg );
            }
        }
        return printed;
    }
    
//    public static boolean deliverMessage(XwfController controller, boObject message)
    public static boolean deliverMessage(EngineGate engine, boObject message, long activityBoui)
    {
        //verificar o modo preferencial de entrega da mensagem
        //acertar o delivery message de cada um dos destinatários
        //se a mensagem tiver um canal definido então todos o recepientes
        //deverão receber por esse canal caso tiverem o endereço do canal definido
        //senão tiverem o remetente deverá ser informado de modo a alterar o canal para esse
        //utilizador.
        //caso o delivery message tenha um canal definido este sobrepõem-se ao canal 
        //definido na mensagem 
        boolean delivered = false;
        try
        {
            StringBuffer toPrint = new StringBuffer();             
            if(!PostInformation.generateMessageInformation(toPrint, message, engine.getBoManager().getContext()))
            {
                return false;
            }            
            String mediaType = message.getAttribute(MessageUtils.ATT_PRF_MEDIA).getValueString();            
            if(MessageUtils.EMAIL.equals(mediaType))
            {
                logger.finest("SENDING EMAIL");
                delivered = email(engine, message,activityBoui);
                logger.finest("SENDING EMAIL FINISHED ->"+(delivered ? "SUCESS":"FAILED"));
            }
            else if(MessageUtils.FAX.equals(mediaType))
            {
                delivered = fax(engine, message,activityBoui);
            }
            else if(MessageUtils.LETTER.equals(mediaType))
            {
                delivered = letter(engine, message,activityBoui);
            }
            else if(MessageUtils.PHONE.equals(mediaType))
            {
                delivered = phone(engine, message,activityBoui);
            }
            else if(MessageUtils.CONVERSATION.equals(mediaType))
            {
                delivered = conversation(engine, message,activityBoui);
            }
            else if(MessageUtils.SGIS.equals(mediaType))
            {
                delivered = sgis(engine, message,activityBoui);
            }
            else if(MessageUtils.BV.equals(mediaType))
            {
                logger.finest("SENDING BV EMAIL");
                delivered = email(engine, message,activityBoui);
                logger.finest("SENDING BV EMAIL FINISHED ->"+(delivered ? "SUCESS":"FAILED"));
            }
            else if(MessageUtils.SMS.equals(mediaType))
            {
                logger.finest("SENDING SMS EMAIL");
                delivered = sms(engine, message,activityBoui);
                logger.finest("SENDING SMS EMAIL FINISHED ->"+(delivered ? "SUCESS":"FAILED"));
            }
            else
            {
                delivered = deliver(engine, message,activityBoui);
            }
            if(delivered && MessageUtils.alreadySend(message))
            {
                message.getAttribute("statusMessage").setValueString("1");
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
            engine.addErrorMessage("Erro inesperado. Tente novamente." );
        }
        return delivered;
    }

    public static boObject getForwardMessage(EboContext ctx, boObject message)
    {
        try
        {
//            EboContext ctx = controller.getDocHTML().getEboContext();
            boObject newObject = boObject.getBoManager().createObject(ctx, message);
            setForwardFields(newObject, message, FORWARD);
            return newObject;
        }
        catch (boRuntimeException e)
        {
            
        }
        return null;
    }

    public static boObject getReplyMessage(EboContext ctx, boObject message, boolean replyAll, String type)
    {
        try
        {
//            EboContext ctx = controller.getDocHTML().getEboContext();
//            boObject newObject = boObject.getBoManager().createObject(ctx, message);
            boObject newObject = boObject.getBoManager().createObject(ctx, type);
            if(!replyAll)
            {
                setForwardFields(newObject, message, REPLAY);
            }
            else
            {
                setForwardFields(newObject, message, REPLAY_ALL);
            }
            return newObject;
        }
        catch (boRuntimeException e)
        {
            
        }
        return null;
    }

//    private static boolean email(XwfController controller, boObject message) throws boRuntimeException
    private static boolean email(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL)
        )
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new EmailServer(engine,activityBoui);
                
                if(man.send(message, SAVEBINARY))
                {
//                    controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
                    engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
                    return true;
                }
            }
            else
            {
                 return multiChannelDeliver(engine, message, channels,activityBoui);
            }
        }
        return false;
    }
    
    
//    private static boolean fax(XwfController controller, boObject message) throws boRuntimeException
    private static boolean fax(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_FAX, MessageUtils.FAX)
        )
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new FaxServer(engine,activityBoui);
                if(man.send(message, SAVEBINARY))
                {
//                    controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
                    engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
                    return true;
                }
            }
            else
            {
                 return multiChannelDeliver(engine, message, channels,activityBoui);
            }
        }
        return false;
    }
//    private static boolean faxPrint(XwfController controller, boObject message) throws boRuntimeException
    private static boolean faxPrint(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        ArrayList channels;
        if(engine == null)
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new FaxServer(null,activityBoui);
                if(man.mergeDocuments(message))
                {
                    return true;
                }
            }
            else
            {
                 return multiChannelPrint(engine, message, channels,activityBoui);
            }
        }
        else
        {
            boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
            boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
            boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
            if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
               verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
               verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_FAX, MessageUtils.FAX)
            )
            {
                channels = MessageUtils.getChannelsMessage(message);
                if(channels.size() == 1)
                {
                    MediaServer man = new FaxServer(engine,activityBoui);
                    if(man.mergeDocuments(message))
                    {
                        return true;
                    }
                }
                else
                {
                     return multiChannelPrint(engine, message, channels,activityBoui);
                }
            }
        }
        return false;
    }

//    private static boolean letterPrint(XwfController controller, boObject message) throws boRuntimeException
    private static boolean letterPrint(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        ArrayList channels;
        if(engine == null)
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new LetterServer(null,activityBoui);
                if(man.mergeDocuments(message))
                {
                    return true;
                }
            }
            else
            {
                 return multiChannelPrint(engine, message, channels,activityBoui);
            }
        }
        else
        {
            boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
            boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
            boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();

            if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
               verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER)
            )
            {
                channels = MessageUtils.getChannelsMessage(message);
                if(channels.size() == 1)
                {
                    MediaServer man = new LetterServer(engine,activityBoui);
                    if(man.mergeDocuments(message))
                    {
                        return true;
                    }
                }
                else
                {
                     return multiChannelPrint(engine, message, channels,activityBoui);
                }
            }
        }
        return false;
    }
//    private static boolean letter(XwfController controller, boObject message) throws boRuntimeException
    private static boolean letter(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER)
        )
        {
            if(message.getAttribute("impCentral") != null &&
            !"1".equals(message.getAttribute("impCentral").getValueString())
            )
            {
                if(message.getAttribute("dtEfectiv") != null && 
                   message.getAttribute("dtEfectiv").getValueDate() == null)
                {
                    engine.addErrorMessage("Deverá imprimir pelo menos uma cópia desta mensagem para poder efectuar esta operação.");
                    return false;
                }
            }
        
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new LetterServer(engine,activityBoui);
                if(man.send(message, SAVEBINARY))
                {
//                    controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
                    engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
                    return true;
                }
            }
            else
            {
                 return multiChannelDeliver(engine, message, channels,activityBoui);
            }
        }
        return false;
    }
//    private static boolean phone(XwfController controller, boObject message) throws boRuntimeException
    private static boolean phone(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_PHONE, MessageUtils.PHONE) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_PHONE, MessageUtils.PHONE) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_PHONE, MessageUtils.PHONE)
        )
        {
            boBridgeIterator it = null;
            for (int i = 0; i < 3; i++) 
            {
                if(i == 0) it = itTO;
                if(i == 1) it = itCC;
                if(i == 2) it = itBCC;  
                it.beforeFirst();

                while (it.next())
                {
                    correct = true;
                    it.currentRow().getObject().getAttribute("media")
                      .setValueObject(null);
                }
            }
//            controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
            engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
            return true;
        }
        return false;
    }
//    private static boolean conversation(XwfController controller, boObject message) throws boRuntimeException
    private static boolean conversation(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean existsRecipient = false;
        boBridgeIterator it = null;
        for (int i = 0; i < MessageUtils.bridges.length; i++) 
        {
            it = message.getBridge(MessageUtils.bridges[i]).iterator();
            it.beforeFirst();
            while(it.next())
            {
                existsRecipient = true;
                it.currentRow().getObject().getAttribute("media").setValueObject(null);
                it.currentRow().getObject().getAttribute("already_send").setValueString("1");
            }
        }
        if(existsRecipient)
        {
//            controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
            engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
        }
        return existsRecipient;
    }
//    private static boolean sgis(XwfController controller, boObject message) throws boRuntimeException
    private static boolean sgis(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, null, MessageUtils.SGIS) &&
           verifyChannelAddress(engine,message, itCC, null, MessageUtils.SGIS) &&
           verifyChannelAddress(engine,message, itBCC, null, MessageUtils.SGIS)
        )
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new SgisServer(engine,activityBoui);
                if(man.send(message, SAVEBINARY))
                {
//                    controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
                    engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
                    return true;
                }
            }
            else
            {
                 return multiChannelDeliver(engine, message, channels,activityBoui);
            }
        }
        return false;        
    }
    
     private static boolean sms(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(engine,message, itTO, MessageUtils.ATT_PHONE, MessageUtils.SMS) &&
           verifyChannelAddress(engine,message, itCC, MessageUtils.ATT_PHONE, MessageUtils.SMS) &&
           verifyChannelAddress(engine,message, itBCC, MessageUtils.ATT_PHONE, MessageUtils.SMS)
        )
        {
            channels = MessageUtils.getChannelsMessage(message);
            if(channels.size() == 1)
            {
                MediaServer man = new SMSServer(engine,activityBoui);
                if(man.send(message, SAVEBINARY))
                {
//                    controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
                    engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
                    return true;
                }
            }
            else
            {
                 return multiChannelDeliver(engine, message, channels,activityBoui);
            }
        }
        return false;        
    }
    
//    private static boolean deliver(XwfController controller, boObject message) throws boRuntimeException
    private static boolean deliver(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um canal preferencial definido
        //e se têm um enderço para esse canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        if(verifyUserPreferedChannelAddress(engine,message, itTO) &&
           verifyUserPreferedChannelAddress(engine,message, itCC) &&
           verifyUserPreferedChannelAddress(engine,message, itBCC)
        )
        {
            return multiChannelDeliver(engine, message, MessageUtils.getChannelsMessage(message),activityBoui);
        }
        return false;
    }
    
//    private static boolean print(XwfController controller, boObject message) throws boRuntimeException
    private static boolean print(EngineGate engine, boObject message,long activityBoui) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um canal preferencial definido
        //e se têm um enderço para esse canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        if(verifyUserPreferedChannelAddress(engine,message, itTO) &&
           verifyUserPreferedChannelAddress(engine,message, itCC) &&
           verifyUserPreferedChannelAddress(engine,message, itBCC)
        )
        {
            return multiChannelPrint(engine, message, MessageUtils.getChannelsMessage(message),activityBoui);
        }
        return false;
    }

//    private static boolean multiChannelPrint(XwfController controller, boObject message, ArrayList channels) throws boRuntimeException
    private static boolean multiChannelPrint(EngineGate engine, boObject message, ArrayList channels,long activityBoui) throws boRuntimeException
    {
        MediaServer server = null;
        String mediaType;
        boolean toRet = true;
        for (int i = 0; i < channels.size(); i++) 
        {
            mediaType = (String)channels.get(i);
            if(MessageUtils.FAX.equals(mediaType))
            {
                server = new FaxServer(engine,activityBoui);                
                toRet = toRet ==false?false:server.mergeDocuments(message);;
            }
            else if(MessageUtils.LETTER.equals(mediaType))
            {
                server = new LetterServer(engine,activityBoui);
                toRet = toRet ==false?false:server.mergeDocuments(message);
            }
            else
            {
                //não faz nada
            }
        }
        return toRet;
        //os restantes
    }

//    private static boolean multiChannelDeliver(XwfController controller, boObject message, ArrayList channels) throws boRuntimeException
    private static boolean multiChannelDeliver(EngineGate engine, boObject message, ArrayList channels,long activityBoui) throws boRuntimeException
    {
        MediaServer server = null;
        String mediaType;
        boolean toRet = true;
        for (int i = 0; i < channels.size(); i++) 
        {
            mediaType = (String)channels.get(i);
            if(MessageUtils.EMAIL.equals(mediaType))
            {
                server = new EmailServer(engine,activityBoui);
                toRet = toRet ==false?false:server.send(message, SAVEBINARY);
            }
            else if(MessageUtils.FAX.equals(mediaType))
            {
                server = new FaxServer(engine,activityBoui);
                toRet = toRet ==false?false:server.send(message, SAVEBINARY);
            }
            else if(MessageUtils.LETTER.equals(mediaType))
            {
                server = new LetterServer(engine,activityBoui);
                toRet = toRet ==false?false:server.send(message, SAVEBINARY);
            }
            else if(MessageUtils.SGIS.equals(mediaType))
            {
                sgis(engine, message,activityBoui);
            }
            else if(MessageUtils.SMS.equals(mediaType))
            {
                server = new SMSServer(engine,activityBoui);
                toRet = toRet ==false?false:server.send(message, SAVEBINARY);
            }
            else
            {
                toRet = false;
            }
//            else if(MessageUtils.PHONE.equals(mediaType))
//            {
////                new PhoneMan(message.getEboContext());
//                toRet = toRet ==false?false:man.send(message);
//            }
//            else
//            {
//                return deliver(message);
//            }
        }
        if(toRet)
        {
            engine.setActivityState(activityBoui, XwfKeys.ACTION_SEND_KEY);
//            controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), controller.getActionCode());
        }
        return toRet;
        //os restantes
    }
    
//    private static boolean verifyChannelAddress(Controller controller, boObject message, boBridgeIterator it, String attName, String type) throws boRuntimeException
    private static boolean verifyChannelAddress(EngineGate engine, boObject message, boBridgeIterator it, String attName, String type) throws boRuntimeException
    {
        boObject auxDeliver = null;
        boolean correct = true;
        if(it != null)
        {
            it.beforeFirst();
            while(it.next())
            {
                auxDeliver = it.currentRow().getObject();
                if(auxDeliver.getAttribute("media").getValueString() == null ||
                    "".equals(auxDeliver.getAttribute("media").getValueString())
                )
                {
                    //senão existir um canal definido para este destinatário 
                    //então uso o da mensagem
                    if(attName != null)
                    {
                        if(auxDeliver.getAttribute(attName).getValueString() == null ||
                            "".equals(auxDeliver.getAttribute(attName).getValueString())
                        )
                        {
                            engine.addErrorMessage("O destinatário [" + auxDeliver.getCARDIDwLink() + "] não tem o endereço definido correctamente para o canal escolhido.");
                            correct = false;
                        }
                        else if(MessageUtils.SMS == type && !validSMSNumber(auxDeliver.getAttribute(attName).getValueString()))
                        {
                            engine.addErrorMessage("O destinatário [" + auxDeliver.getCARDIDwLink() + "] tem um número telemóvel/telefone inválido.");
                            correct = false;
                        }
                        else
                        {
                            auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                        }
                    }
                    else if(MessageUtils.SGIS.equals(type))
                    {
                        if(verifyChannelAddress(engine,message, auxDeliver, MessageUtils.SGIS))
                        {
                            auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                        }
                    }
                }
                else
                {
                    if(auxDeliver.getAttribute("media").getInputType() == AttributeHandler.INPUT_FROM_INTERNAL &&
                        !type.equals(auxDeliver.getAttribute("media").getValueString())
                    )
                    {
                        auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                    //existe uma canal definido para este destinatário vou verificar
                    //se existe um endereço definido correctamente para o canal
                    if(!verifyChannelAddress(engine,message, auxDeliver, auxDeliver.getAttribute("media").getValueString()))
                    {
                        correct = false;
                    }
                }
            }
        }
        return correct;
    }
//    private static boolean verifyChannelAddress(Controller controller, boObject message, boObject deliveryMessage, String mediaType) throws boRuntimeException
    private static boolean verifyChannelAddress(EngineGate engine, boObject message, boObject deliveryMessage, String mediaType) throws boRuntimeException
    {
        if(MessageUtils.EMAIL.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_EMAIL).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_EMAIL).getValueString())
                    )
            {
                engine.addErrorMessage(PREFERED_CHANNEL_NOT_DEFINED_1 + deliveryMessage.getCARDIDwLink() + PREFERED_CHANNEL_NOT_DEFINED_2);
                return false;
            }
        }
        else if(MessageUtils.LETTER.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_STREET).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_STREET).getValueString())||
                deliveryMessage.getAttribute(MessageUtils.ATT_POSTAL).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_POSTAL).getValueString())||
                deliveryMessage.getAttribute(MessageUtils.ATT_CPLOCAL).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_CPLOCAL).getValueString())
            )
            {
                engine.addErrorMessage(PREFERED_CHANNEL_NOT_DEFINED_1 + deliveryMessage.getCARDIDwLink() + PREFERED_CHANNEL_NOT_DEFINED_2);
                return false;
            }
        }
        else if(MessageUtils.FAX.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_FAX).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_FAX).getValueString())
                    )
            {
                engine.addErrorMessage(PREFERED_CHANNEL_NOT_DEFINED_1 + deliveryMessage.getCARDIDwLink() + PREFERED_CHANNEL_NOT_DEFINED_2);
                return false;
            }
        }
        else if(MessageUtils.PHONE.equals(mediaType) || MessageUtils.SMS.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_PHONE).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_PHONE).getValueString())
                    )
            {
                engine.addErrorMessage(PREFERED_CHANNEL_NOT_DEFINED_1 + deliveryMessage.getCARDIDwLink() + PREFERED_CHANNEL_NOT_DEFINED_2);
                return false;
            }
        }
        else if(MessageUtils.SGIS.equals(mediaType))
        {
            boObject auxObj = deliveryMessage.getAttribute("refObj").getObject();
            if(!XEOUserUtils.isXEOUser(auxObj) && !"workQueue".equals(auxObj.getName()) &&
                !"Ebo_Group".equals(auxObj.getName()) && !"Ebo_Role".equals(auxObj.getName())
            )
            {
                engine.addErrorMessage("O destinatário [" + auxObj.getCARDIDwLink() + "] não é válido para este tipo de mensagem.");
                return false;
            }
            else
            {
                if("workQueue".equals(auxObj.getName()) || "Ebo_Group".equals(auxObj.getName()) || 
                    "Ebo_Role".equals(auxObj.getName()))
                {
                    return true;
                }
                if(auxObj.getAttribute("username").getValueObject() == null ||
                        auxObj.getAttribute("password").getValueObject() == null)
                {
                    engine.addErrorMessage("O destinatário [" + auxObj.getCARDIDwLink() + "] não tem conta definida no SGIS.");
                    return false;
                }
            }
            
        }
        return true;
    }
//    private static boolean verifyUserPreferedChannelAddress(Controller controller, boObject message, boBridgeIterator it) throws boRuntimeException
    private static boolean verifyUserPreferedChannelAddress(EngineGate engine, boObject message, boBridgeIterator it) throws boRuntimeException
    {
        boObject auxDeliver = null;
        boolean correct = true;
        String mediaType;
        if(it != null)
        {
            it.beforeFirst();
            while(it.next())
            {
                auxDeliver = it.currentRow().getObject();
                mediaType = auxDeliver.getAttribute("refObj").getObject().getAttribute("preferedMedia").getValueString();
                if(mediaType == null || "".equals(mediaType))
                {
                    engine.addErrorMessage(PREFERED_CHANNEL_NOT_DEFINED_1 + auxDeliver.getCARDIDwLink() + PREFERED_CHANNEL_NOT_DEFINED_2);
                    correct = false;
                }
                else
                {
                    correct = verifyChannelAddress(engine,message, auxDeliver, mediaType);
                }
            }
        }
        return correct;
    }
    
//    private static void treatDeliver(boObject messageContext, boObject message) throws boRuntimeException
//    {
//        if(messageContext == null)
//        {
//            //criar um programa para tratar da mensagem
//            boObject progRuntime = boObject.getBoManager().createObject(message.getEboContext(), "xwfProgramRuntime");
//            progRuntime.getAttribute("name").setValueString("receiving message");
//            xwfMessage.receiveMessage(controller, message, progRuntime);
//        }
//        else
//        {
//            xwfMessage.receiveMessage(message, messageContext);
//        }
//    }
    
    private static void setForwardFields(boObject mailobject, boObject oldmailobject, int type) throws boRuntimeException
    {
        String[] cleanBridges = new String[]{MessageUtils.ATT_TO, MessageUtils.ATT_CC, MessageUtils.ATT_BCC,
                                             MessageUtils.ATT_TORef, MessageUtils.ATT_CCRef, MessageUtils.ATT_BCCRef,
                                             MessageUtils.ATT_RECEIPTS, "fwdObjects", "binaryDocuments"};
        String[] showBridges = new String[]{MessageUtils.ATT_TO, MessageUtils.ATT_CC};
        ArrayList myEmails = getMyEmails(mailobject);
        ArrayList replyTo = new ArrayList(5);
        ArrayList replyCc = new ArrayList(5);
        StringBuffer de = new StringBuffer();
        
        //limpar campos
        if(mailobject.getAttribute("resId") != null)
            mailobject.getAttribute("resId").setValueObject(null);
        if(mailobject.getAttribute("register") != null)
            mailobject.getAttribute("register").setValueObject("0");
        mailobject.getAttribute("dtdoc").setValueObject(null);
        mailobject.getAttribute("letterFormat").setValueObject(null);
        mailobject.getAttribute("faxFormat").setValueObject(null);
        mailobject.getAttribute("messageid").setValueObject(null);
        mailobject.getAttribute("requestDeliveryReceipt").setValueObject(null);
        mailobject.getAttribute("requestReadReceipt").setValueObject(null);
        mailobject.getAttribute("type").setValueObject(null);
        mailobject.getAttribute("arquive").setValueObject(null);
        mailobject.getAttribute("localArquive").setValueObject(null);
        mailobject.getAttribute("createdSystemOrigin").setValueObject(null);
        if(mailobject.getAttribute("forwarding") != null)
            mailobject.getAttribute("forwarding").setValueObject(null);
        if(mailobject.getAttribute("readyForRes") != null)
            mailobject.getAttribute("readyForRes").setValueObject(null);
        mailobject.getAttribute("signedMsg").setValueObject(null);
        mailobject.getAttribute("createdReceiveMsg").setValueObject(null);
        mailobject.getAttribute("already_read").setValueObject(null);
        mailobject.getAttribute("XEOID").setValueObject(null);
        mailobject.getAttribute("send_read_receipt").setValueObject(null);
        if(mailobject.getAttribute("radical") != null)
            mailobject.getAttribute("radical").setValueObject(null);
        mailobject.getAttribute("waitForResponse").setValueObject(null);
        mailobject.getAttribute("preferedMedia").setValueObject(
            oldmailobject.getAttribute("preferedMedia").getValueObject());
        mailobject.getAttribute("TEMPLATE").setValueObject(null);
        
        
        //acerto dos campos forwardFrom e responseTo
        mailobject.getAttribute("forwardFrom").setValueObject(null);
        mailobject.getAttribute("responseTo").setValueObject(null);
        if(type == FORWARD)
        {
            mailobject.getAttribute("forwardFrom").setValueLong(oldmailobject.getBoui());
        }
        else
        {
            mailobject.getAttribute("responseTo").setValueLong(oldmailobject.getBoui());
        }
        
        //de
        de.append("<br>-----Mensagem original-----<p>")
        .append("<ul>")
        .append("<li><b>De:</b> ");
        boObject runtimeAddres = oldmailobject.getAttribute(MessageUtils.ATT_FROM).getObject();
        if(runtimeAddres != null)
        {
            if(runtimeAddres.getAttribute("name").getValueString() != null)
            {
                de.append(runtimeAddres.getAttribute("name").getValueString());
            }
            if(runtimeAddres.getAttribute("lastname").getValueString() != null)
            {
                if(runtimeAddres.getAttribute("name").getValueString() == null || 
                    !runtimeAddres.getAttribute("name").getValueString().trim().toUpperCase()
                        .endsWith(runtimeAddres.getAttribute("lastname").getValueString().trim()
                                .toUpperCase()))
                {
                    de.append(" ").append(runtimeAddres.getAttribute("lastname").getValueString());
                }
            }
            de.append(" [").append(runtimeAddres.getAttribute("email").getValueString()).append("]");
            if(type != FORWARD)
            {
                if(runtimeAddres.getAttribute("refObj").getObject() != null)
                {
                    replyTo.add(runtimeAddres.getAttribute("refObj").getObject());
                }
                else
                {
                    replyTo.add(runtimeAddres.getAttribute("email").getValueString());
                }
            }
        }
        else
        {
            boObject assignValue = oldmailobject.getAttribute("assignedQueue").getObject();
            if(assignValue.getAttribute("name").getValueString() != null)
            {
                de.append(assignValue.getAttribute("name").getValueString());
            }
            if(assignValue.getAttribute("lastname").getValueString() != null)
            {
                de.append(" ").append(assignValue.getAttribute("lastname").getValueString());
            }
            de.append(" [").append(assignValue.getAttribute("email").getValueString()).append("]");
            if(type != FORWARD)
            {               
                replyTo.add(assignValue);                
            }
        }
//        mailobject.getAttribute(MessageUtils.ATT_FROM).setValueObject(null);
        de.append("</li>");
        
        //enviada
        de.append("<li><b>Enviada:</b> ");
        SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");
        if(oldmailobject.getAttribute("dtdoc").getValueDate() != null)
        {            
            de.append(df.format(
                oldmailobject.getAttribute("dtdoc").getValueDate()));
        }
        else
        {
            de.append(df.format(
                oldmailobject.getAttribute("SYS_DTCREATE").getValueDate()));
        }
        de.append("</li>");
        //para//CC
        bridgeHandler bridge=null;
        for (int i = 0; i < showBridges.length; i++) 
        {
            bridge=oldmailobject.getBridge(showBridges[i]);
            bridge.beforeFirst();
            int ciclo = 0;
            while (bridge.next())
            {
                if(ciclo > 0)
                {                    
                    de.append("; ");
                }
                else
                {
                    if(MessageUtils.ATT_TO.equals(showBridges[i]))
                    {
                        de.append("<li><b>Para:</b> ");           
                    }
                    else
                    {
                        de.append("<li><b>CC:</b> ");
                    }
                }
                runtimeAddres = bridge.getObject();
                if(runtimeAddres.getAttribute("name").getValueString() != null)
                {
                    de.append(runtimeAddres.getAttribute("name").getValueString());
                }
                if(runtimeAddres.getAttribute("lastname").getValueString() != null)
                {
                    if(runtimeAddres.getAttribute("name").getValueString() == null || 
                    !runtimeAddres.getAttribute("name").getValueString().trim().toUpperCase()
                        .endsWith(runtimeAddres.getAttribute("lastname").getValueString().trim()
                                .toUpperCase()))
                    {
                        de.append(" ").append(runtimeAddres.getAttribute("lastname").getValueString());
                    }
                }
                de.append(" [").append(runtimeAddres.getAttribute("email").getValueString()).append("]");
                if(type == REPLAY_ALL)
                {
                    if(runtimeAddres.getAttribute("refObj").getObject() != null)
                    {
                        if(MessageUtils.ATT_TO.equals(showBridges[i]))
                            replyTo.add(runtimeAddres.getAttribute("refObj").getObject());
                        else
                            replyCc.add(runtimeAddres.getAttribute("refObj").getObject());
                    }
                    else
                    {
                        if(MessageUtils.ATT_TO.equals(showBridges[i]))
                            replyTo.add(runtimeAddres.getAttribute("email").getValueString());
                        else
                            replyCc.add(runtimeAddres.getAttribute("email").getObject());
                    }
                }
                ciclo++;
            }
            de.append("</li>");
        }
        
        //assunto        
        String subject = oldmailobject.getAttribute("name").getValueString();
        de.append("<li><b>Assunto:</b> ").append(subject).append("</li></ul>");        
        String prefix = getPRefix(type);        
        if(subject== null || !subject.toUpperCase().startsWith(prefix))
        {
            subject = subject==null?prefix:prefix + " " + subject;            
        }
        mailobject.getAttribute("name").setValueString(subject);

        for (int i = 0; i < cleanBridges.length; i++) 
        {
            bridge=mailobject.getBridge(cleanBridges[i]);
            bridge.truncate();
        }
        
        if(type != FORWARD)
        {
            setSendingAddress(mailobject, MessageUtils.ATT_TO, replyTo, myEmails);
            setSendingAddress(mailobject, MessageUtils.ATT_CC, replyCc, myEmails);
        }
        
        //texto
        //-----Mensagem original-----
        //De: XXXXXX
        //Enviada: XXXXX
        //Para: XXXXX
        //Assunto: FW: XXXX
        de.append(oldmailobject.getAttribute("description").getValueString());
        mailobject.getAttribute("description").setValueString(de.toString());
        //limpar a flag
        mailobject.getAttribute("statusMessage").setValueObject(null);
    }
   private static ArrayList getMyEmails(boObject email) throws boRuntimeException
  {
    boObject obj = email.getAttribute(MessageUtils.ATT_FROM).getObject();
    if(obj != null)
    {
        obj = obj.getAttribute("refObj").getObject();
    }
    ArrayList emailsToReturn = new ArrayList();
    if(obj != null)
    {
        if(obj.getAttribute("email") != null && obj.getAttribute("email").getValueString() != null)
        emailsToReturn.add(obj.getAttribute("email").getValueString());
        if(obj.getBridge("emailAccounts") != null)
        {
            bridgeHandler bh = obj.getBridge("emailAccounts");
            boBridgeIterator it = bh.iterator();
            it.beforeFirst();
            while(it.next())
            {
                if(it.currentRow().getObject().getAttribute("email") != null)
                {
                    if(it.currentRow().getObject().getAttribute("email").getValueString() != null)
                    {
                        emailsToReturn.add(obj.getAttribute("email").getValueString());
                    }
                }
            }
        }
    }
    return emailsToReturn;
  }
  private static String getPRefix(int type)
  {
    if(type == FORWARD)
    {
        return "FW:";
    }
    return "RE:";
  }
  private static void setSendingAddress(boObject mailobject, String bridgeName, ArrayList replyTo, ArrayList myEmails) throws boRuntimeException
    {
        bridgeHandler bridge = mailobject.getBridge(bridgeName);
        boObject runtime;
        boolean onlyMe = true;
        for (int i = 0; i < replyTo.size(); i++) 
        {
            if(replyTo.get(i) instanceof String)
            {
                if(!myEmails.contains(replyTo.get(i)))
                {
                    runtime = boObject.getBoManager().createObject(mailobject.getEboContext(), "deliveryMessage");
                    runtime.getAttribute("email").setValueObject(replyTo.get(i));
                    bridge.add(runtime.getBoui());
                    onlyMe = false;
                }
            }
            else 
            {
                if(((boObject)replyTo.get(i)).getAttribute("email") != null)
                {
                    if(!myEmails.contains(((boObject)replyTo.get(i)).getAttribute("email").getValueString()))
                    {
                        bridge.add(((boObject)replyTo.get(i)).getBoui());
                        onlyMe = false;
                    }
                }
            }
        }
        if(onlyMe)
        {
            for (int i = 0; i < replyTo.size(); i++) 
            {
                if(replyTo.get(i) instanceof String)
                {
                    runtime = boObject.getBoManager().createObject(mailobject.getEboContext(), "deliveryMessage");
                    runtime.getAttribute("email").setValueObject(replyTo.get(i));
                    bridge.add(runtime.getBoui());
                }
                else 
                {
                    if(((boObject)replyTo.get(i)).getAttribute("email") != null)
                    {
                        bridge.add(((boObject)replyTo.get(i)).getBoui());
                        onlyMe = false;
                    }
                }
            }
        }
  }
  
    public static boolean verifyChannel(boObject message) throws boRuntimeException
    {
        String mediaType = message.getAttribute(MessageUtils.ATT_PRF_MEDIA).getValueString();            
        if(MessageUtils.EMAIL.equals(mediaType))
        {
            return verifyEmail(message);
        }
        else if(MessageUtils.FAX.equals(mediaType))
        {
            return verifyFax(message);
        }
        else if(MessageUtils.LETTER.equals(mediaType))
        {
            return verifyLetter(message);
        }
        else if(MessageUtils.PHONE.equals(mediaType))
        {
            return verifyPhone(message);
        }
        else if(MessageUtils.CONVERSATION.equals(mediaType))
        {
            return true;
        }
        else if(MessageUtils.SGIS.equals(mediaType))
        {
            return verifySGIS(message);
        }
        else if(MessageUtils.BV.equals(mediaType))
        {
            return verifyEmail(message);
        }
        else if(MessageUtils.SMS.equals(mediaType))
        {
            return verifyPhone(message);
        }
        return false;
    }
    
    public static boolean verifyEmail(boObject message) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(message, itTO, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_EMAIL, MessageUtils.EMAIL)
        )
        {
            return true;
        }
        return false;
    }
    public static boolean verifyLetter(boObject message) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(message, itTO, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itTO, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itTO, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_STREET, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_POSTAL, MessageUtils.LETTER) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_CPLOCAL, MessageUtils.LETTER)
        )
        {
            return true;
        }
        return false;
    }
    public static boolean verifyFax(boObject message) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(message, itTO, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_FAX, MessageUtils.FAX) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_FAX, MessageUtils.FAX)
        )
        {
            return true;
        }
        return false;
    }
    public static boolean verifyPhone(boObject message) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(message, itTO, MessageUtils.ATT_PHONE, MessageUtils.SMS) &&
           verifyChannelAddress(message, itCC, MessageUtils.ATT_PHONE, MessageUtils.SMS) &&
           verifyChannelAddress(message, itBCC, MessageUtils.ATT_PHONE, MessageUtils.SMS)
        )
        {
            return true;
        }
        return false;
    }
    public static boolean verifySGIS(boObject message) throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(MessageUtils.ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(MessageUtils.ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(MessageUtils.ATT_BCC).iterator();
        ArrayList channels;
        if(verifyChannelAddress(message, itTO, null, MessageUtils.SGIS) &&
           verifyChannelAddress(message, itCC, null, MessageUtils.SGIS) &&
           verifyChannelAddress(message, itBCC, null, MessageUtils.SGIS)
        )
        {
            return true;
        }
        return false;
    }

    private static boolean verifyChannelAddress(boObject message, boBridgeIterator it, String attName, String type) throws boRuntimeException
    {
        boObject auxDeliver = null;
        boolean correct = true;
        if(it != null)
        {
            it.beforeFirst();
            while(it.next())
            {
                auxDeliver = it.currentRow().getObject();
                if(auxDeliver.getAttribute("media").getValueString() == null ||
                    "".equals(auxDeliver.getAttribute("media").getValueString())
                )
                {
                    //senão existir um canal definido para este destinatário 
                    //então uso o da mensagem
                    if(attName != null)
                    {
                        if(auxDeliver.getAttribute(attName).getValueString() == null ||
                            "".equals(auxDeliver.getAttribute(attName).getValueString())
                        )
                        {
                            correct = false;
                        }
                        else
                        {
                            auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                        }
                    }
                    else if(MessageUtils.SGIS.equals(type))
                    {
                        if(verifyChannelAddress(message, auxDeliver, MessageUtils.SGIS))
                        {
                            auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                        }
                    }
                }
                else
                {
                    if(auxDeliver.getAttribute("media").getInputType() == AttributeHandler.INPUT_FROM_INTERNAL &&
                        !type.equals(auxDeliver.getAttribute("media").getValueString())
                    )
                    {
                        auxDeliver.getAttribute("media").setValueString(type, AttributeHandler.INPUT_FROM_INTERNAL);
                    }
                    //existe uma canal definido para este destinatário vou verificar
                    //se existe um endereço definido correctamente para o canal
                    if(!verifyChannelAddress(message, auxDeliver, auxDeliver.getAttribute("media").getValueString()))
                    {
                        correct = false;
                    }
                }
            }
        }
        return correct;
    }
    
    private static boolean verifyChannelAddress(boObject message, boObject deliveryMessage, String mediaType) throws boRuntimeException
    {
        if(MessageUtils.EMAIL.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_EMAIL).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_EMAIL).getValueString())
                    )
            {
                return false;
            }
        }
        else if(MessageUtils.LETTER.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_STREET).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_STREET).getValueString())||
                deliveryMessage.getAttribute(MessageUtils.ATT_POSTAL).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_POSTAL).getValueString())||
                deliveryMessage.getAttribute(MessageUtils.ATT_CPLOCAL).getValueString() == null ||
                "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_CPLOCAL).getValueString())
            )
            {
                return false;
            }
        }
        else if(MessageUtils.FAX.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_FAX).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_FAX).getValueString())
                    )
            {
                return false;
            }
        }
        else if(MessageUtils.PHONE.equals(mediaType) || MessageUtils.SMS.equals(mediaType))
        {
            if(deliveryMessage.getAttribute(MessageUtils.ATT_PHONE).getValueString() == null ||
                        "".equals(deliveryMessage.getAttribute(MessageUtils.ATT_PHONE).getValueString())
                    )
            {
                return false;
            }
        }
        else if(MessageUtils.SGIS.equals(mediaType))
        {
            boObject auxObj = deliveryMessage.getAttribute("refObj").getObject();
            if(!XEOUserUtils.isXEOUser(auxObj) && !"workQueue".equals(auxObj.getName()) &&
                !"Ebo_Group".equals(auxObj.getName()) && !"Ebo_Role".equals(auxObj.getName())
            )
            {
                return false;
            }
            else
            {
                if("workQueue".equals(auxObj.getName()) || "Ebo_Group".equals(auxObj.getName()) || 
                    "Ebo_Role".equals(auxObj.getName()))
                {
                    return true;
                }
                if(auxObj.getAttribute("username").getValueObject() == null ||
                        auxObj.getAttribute("password").getValueObject() == null)
                {
                    return false;
                }
            }
            
        }
        return true;
    }
    
    public static boolean validSMSNumber(String phone)
    {
        phone = (phone.trim()).replaceAll(" ", "");
        if(!(
            phone.startsWith("+") || 
            phone.startsWith("00") || 
            (phone.startsWith("2") && phone.length() == 9) ||
            (phone.startsWith("93") && phone.length() == 9) ||
            (phone.startsWith("96") && phone.length() == 9) ||
            (phone.startsWith("91") && phone.length() == 9)
         )
      )
      {
         return false;
      }
      else
      {
         String toValid = null;
         if(phone.startsWith("+"))
         {
            toValid = phone.substring(1);
         }
         else
         {
            toValid = phone;
         }
         try
         {
            Long.parseLong(toValid);
         }catch(Exception e){return false;}
      }
       return true;
    }
}