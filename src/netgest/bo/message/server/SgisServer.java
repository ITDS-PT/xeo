/*Enconding=UTF-8*/
package netgest.bo.message.server;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import netgest.bo.boConfig;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.utils.XEOUserUtils;

import netgest.xwf.EngineGate;
import netgest.xwf.core.xwfManager;
import netgest.xwf.core.xwfMessage;
import netgest.xwf.xwfEngineGate;

import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class SgisServer implements MediaServer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.media.FaxServer");
    private static final String SGIS = "Sgis";
    private static final String ERROR_FROM = "É impossivel enviar uma mensagem sem endereço de email definido";
    private static final String ERROR_FROM_RECEIPT = "É impossivel enviar um recibo de uma mensagem sem remetente";
    private static final String ERROR_TO = "É impossivel enviar uma mensagem sem destinatários";
    private static final String ERROR_TO_RECEIPT = "É impossivel enviar um recibo de uma mensagem sem destinatários";
    private static final String ERROR_MAIL_NOT_SAVED = "A mensagem tem que ser gravada antes de poder ser enviada";
    private static final String ERROR_SENDER = "Não pode enviar mensagens com este endereço";
    private static final String ERROR_INV_RECIPIENT_1 = "O destinário ";
    private static final String ERROR_INV_RECIPIENT_2 = " não têm um endereço email válido";
//    private XwfController controller = null;
    private EngineGate engine = null;
    private long activityBoui = -1;        
    private static final String[] bridges = {"to", "cc", "bcc"};
    /**
     * 
     * @Company Enlace3
     * @since 
     */
//    public SgisServer(XwfController controller)
//    {
//        this.controller = controller;
//    }
    public SgisServer(EngineGate engine, long activityBoui)
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
        boBridgeIterator it;
        boObject document = null;
        ArrayList receivers = new ArrayList();
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();
            boObject deliveryMsg;            
            while(it.next())
            {
                
                deliveryMsg = it.currentRow().getObject();
                if(!"1".equals(deliveryMsg.getAttribute("already_send").getValueString()))
                {
                    if(SGIS.equals(deliveryMsg.getAttribute("media").getValueString()))
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
        if(MessageUtils.SGIS.equals(message.getAttribute("preferedMedia").getValueString()))
        {
            message.getAttribute("statusMessage").setValueString("1");
            message.getAttribute("dtdoc").setValueDate(new Date());
//            controller.getEngine().setActivityState(controller.getRuntimeActivityBoui(), XwfKeys.ACTION_SEND_KEY);
            xwfMessage.receiveMessage(((xwfEngineGate)this.engine).getManager(), message, engine.getBoManager().getProgram());
            if(MessageUtils.isToWaitResponse(message) && receivers.size() > 0)
            {
                logger.finer(LoggerMessageLocalizer.getMessage("GOING_TO_CREATE_WAIT_FOR_MSG"));
                message.getEboContext().getBoSession().setProperty("creatingWaitMsg", Boolean.TRUE);
                try
                {
                    boObject program = engine.getBoManager().getProgram();
                    xwfManager man = new xwfManager(message.getEboContext(), program);
                    xwfMessage.createWaitActv(man, MessageUtils.toLongReceivers(receivers), message, program.getBoui(), 
                        this.activityBoui);
                    program.update();
                }
                catch(boRuntimeException e)
                {
                    throw e;
                }
                finally
                {
                    message.getEboContext().getBoSession().removeProperty("creatingWaitMsg");
                }
            }
        }
        if("1".equals(message.getAttribute("requestReadReceipt").getValueString()))
        {
            message.getAttribute("send_read_receipt").setValueString("1");
        }

        if("1".equals(message.getAttribute("requestDeliveryReceipt").getValueString()))
        {
            boObject performer;
            for (int i = 0; i < receivers.size(); i++) 
            {
                performer = boObject.getBoManager().loadObject(message.getEboContext(), ((Long)receivers.get(i)).longValue());
                sendReceipt(message, performer, "messageDelivered");
            }
        }
        return true;
    }

    public boObject sendReceipt(boObject message, boObject performer) throws boRuntimeException
    {
        return sendReceipt(message, performer, "messageReceipt");
    }

    private boObject sendReceipt(boObject message, boObject performer, String type) throws boRuntimeException
    {
        try
        {
            EboContext ctx = message.getEboContext();
            boObject receiptToSend = boObject.getBoManager().createObject(ctx, type);
            receiptToSend.getAttribute("responseTo").setObject(message);

            //Para
            boObject toObject = message.getAttribute("replyTo").getObject();

            if (toObject == null)
            {
                toObject = message.getAttribute("from").getObject().getAttribute("refObj").getObject();

                if (toObject == null || (!XEOUserUtils.isXEOUser(toObject)
                    && !"workQueue".equals(toObject.getName()) && !"Ebo_Group".equals(toObject.getName()) 
                    && !"Ebo_Role".equals(toObject.getName())
                    && !"workQueue".equals(toObject.getBoDefinition().getBoSuperBo())
                    && !"Ebo_Group".equals(toObject.getBoDefinition().getBoSuperBo()) 
                    && !"Ebo_Role".equals(toObject.getBoDefinition().getBoSuperBo())
                    )
                )
                {
                    this.addErrorMessage(ERROR_FROM_RECEIPT);
                    return null;
                }
            }
            receiptToSend.getBridge("to").add(toObject.getBoui());

            //De
            String address = null;
            if (performer == null)
            {
                this.addErrorMessage(ERROR_TO);
                return null;
            }

            if (!XEOUserUtils.isXEOUser(performer)
                && !"workQueue".equals(performer.getName()) &&
                !"Ebo_Group".equals(performer.getName()) && 
                !"Ebo_Role".equals(performer.getName())&&
                !"workQueue".equals(performer.getBoDefinition().getBoSuperBo()) &&
                !"Ebo_Group".equals(performer.getBoDefinition().getBoSuperBo()) && 
                !"Ebo_Role".equals(performer.getBoDefinition().getBoSuperBo())
            )
            {
                this.addErrorMessage(ERROR_TO);
                return null;
            }
            receiptToSend.getAttribute("from").setObject(performer);

            //prioridade
            receiptToSend.getAttribute("priority").setValueObject(message.getAttribute("priority").getValueObject());

            //subject
            String footprint = boConfig.getMailConfig().getProperty("footprint");
            String subject = null;

            try
            {
                if("messageReceipt".equals(type))
                {
                    subject = "LIDO: " +
                    message.getAttribute("name").getValueString();
                }
                else
                {
                    subject = "ENTREGUE: " +
                    message.getAttribute("name").getValueString();
                }
            }
             catch (boRuntimeException e)
            {
                if("messageReceipt".equals(type))
                {
                    subject = "LIDO: " +
                    message.getAttribute("name").getValueString();
                }
                else
                {
                    subject = "ENTREGUE: " +
                    message.getAttribute("name").getValueString();
                }
            }
            receiptToSend.getAttribute("name").setValueString(subject);

            //conteúdo
            StringBuffer msg = new StringBuffer();
            msg.append("A sua mensagem<br>&nbsp;&nbsp;&nbsp;&nbsp;Para: ");

            try
            {
                if (performer.getAttribute("name").getValueString() != null)
                {
                    msg.append(performer.getAttribute("name").getValueString());
                }

                if ((performer.getAttribute("lastname") != null) &&
                        (performer.getAttribute("lastname").getValueString() != null))
                {
                    if ((performer.getAttribute("name").getValueString() == null) ||
                            !performer.getAttribute("name").getValueString()
                                         .trim().toUpperCase().endsWith(performer.getAttribute(
                                    "lastname").getValueString().trim()
                                                                                    .toUpperCase()))
                    {
                        msg.append(" ").append(performer.getAttribute("lastname")
                                                       .getValueString());
                    }
                }

                msg.append(" [")
                   .append(performer.getAttribute("email").getValueString())
                   .append("]");
            }
             catch (boRuntimeException e)
            {
                //ignore
            }

            msg.append("<br>&nbsp;&nbsp;&nbsp;&nbsp;Assunto: ");

            try
            {
                msg.append(message.getAttribute("name").getValueString());
            }
             catch (boRuntimeException e)
            {
                //ignore
            }

            if("messageReceipt".equals(type))
            {
                msg.append("<br>foi lida na data: ");
            }
            else
            {
                msg.append("<br>foi entregue na data: ");
            }

            Date readDate = new Date();
            SimpleDateFormat df = new SimpleDateFormat(
                    "EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");
            msg.append(df.format(readDate)).append(".");

            receiptToSend.getAttribute("description").setValueString(msg.toString());
            
            ArrayList r = MessageUtils.getDeliveryMessageOnlyfromObject(message, performer);
            for (int i = 0; i < r.size(); i++) 
            {
                if(((boObject)r.get(i)).getAttribute("send_date_read_receipt") != null &&
                    ((boObject)r.get(i)).getAttribute("send_date_read_receipt").getValueDate() == null)
                {
                    ((boObject)r.get(i)).getAttribute("send_date_read_receipt").setValueDate(readDate);
                }
            }
            
            receiptToSend.getAttribute("statusMessage").setValueString("1");
            receiptToSend.getAttribute("preferedMedia").setValueString(MessageUtils.SGIS);
            receiptToSend.getAttribute("dtdoc").setValueDate(readDate);
            
            if("messageReceipt".equals(type))
            {
                receiptToSend.getAttribute("read").setValueDate(readDate);
            }
            else
            {
                receiptToSend.getAttribute("delivery").setValueDate(readDate);
            }
            message.getBridge("receipts").add(receiptToSend.getBoui());            

            message.update();
            
//            xwfActionHelper.sendDeliverReceipt(controller.getEngine(), 
//                receiptToSend, performer);
            
            
            xwfMessage.receiveMessage(((xwfEngineGate)this.engine).getManager(), receiptToSend, this.engine.getBoManager().getProgram());
            
            if("messageReceipt".equals(type))
            {
                ArrayList deliver = MessageUtils.getDeliveryMessageOnlyfromObject(message, performer);
                for (int i = 0; i < deliver.size(); i++) 
                {
                    ((boObject)deliver.get(i)).getAttribute("date_sent_read_receipt").setValueDate(readDate);
                }
                message.update();
            }

            return message;
        }
         catch (boRuntimeException e)
        {
            this.addErrorMessage(
                MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return null;
        }
         catch (Exception e)
        {
            this.addErrorMessage(
            		MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return null;
        }
    }


    public boolean deleteMessage(String messageid) throws boRuntimeException
    {
        return false;
    }

    public boolean deleteMessage(boObject message) throws boRuntimeException
    {
        return false;
    }
    public boolean mergeDocuments(boObject message) throws boRuntimeException
    {
        return true;
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