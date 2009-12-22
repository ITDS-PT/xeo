/*Enconding=UTF-8*/
package netgest.bo.message.server;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.impl.document.DocumentHelper;
import netgest.bo.impl.document.merge.MergeHelper;
import netgest.bo.impl.document.print.PrintHelper;

import netgest.bo.message.server.sms.SMSExpressApi;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.io.FSiFile;
import netgest.io.iFile;
import netgest.utils.HTMLRemover;
import netgest.utils.sms.SMSApi;
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
public class SMSServer implements MediaServer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.server.SMSServer");
    private static final String SMS = "SMS";
    private static final String ERROR_NO_TEMPLATE = "Para enviar um SMS é necessário escolher o modelo.";
//    private XwfController controller = null;
    private EngineGate engine = null;
    private long activityBoui = -1;
    private static final String[] bridges = {"to", "cc", "bcc"};
    private ArrayList errors = new ArrayList();
    private ArrayList sentNumber = new ArrayList();
    /**
     * 
     * @Company Enlace3
     * @since 
     */
//    public FaxServer(XwfController controller)
//    {
//        this.controller = controller;
//    }
    public SMSServer(EngineGate engine, long activityBoui)
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
//        logger.finer("SETTING TO");
        String to = null;
        boolean toRet = true;
        boolean oneSent = false;
        
        //texto
        String msg = message.getAttribute("textSMS").getValueString();

        bridgeHandler bridge = message.getBridge("to");
        boBridgeIterator bit = bridge.iterator();        
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        bridge = message.getBridge("bcc");
        bit = bridge.iterator();        
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), "BCC\n"+msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        //vou alterar a mensagem para ir CC
        bridge = message.getBridge("cc");
        bit = bridge.iterator();        
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), "CC\n"+msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        if(oneSent)
        {
            List binaries = message.getObjectBinary().getBinary();
            if(saveBinary && (binaries == null || binaries.size() == 0))
            {
                String path = getFile(getText(message));
                fillGenerated(message, path);
            }
        }
        if(toRet)
        {
            setSended(message);
        }
        return toRet;
    }
    
    public ArrayList getErrors()
    {
        return errors;
    }
    private boolean send(boObject to, String msg, ArrayList errors) throws boRuntimeException
    {
        String phone = to.getAttribute(MessageUtils.ATT_PHONE).getValueString();
        boolean toRet = false;
        if(sentNumber.indexOf(phone) == -1)
        {
            phone = (phone.trim()).replaceAll(" ", "");
            if(SMSExpressApi.isConfig())
            {
                toRet = sendBySMSExpress(phone, msg, errors);
                if(toRet)
                {
                    to.getAttribute("already_send").setValueString("1");
                    sentNumber.add(phone);
                }
            }
            else
            {
                toRet = send(phone, msg, errors);
                if(toRet)
                {
                    to.getAttribute("already_send").setValueString("1");
                    sentNumber.add(phone);
                }
            }
        }
        else
        {
            //esta msg já foi enviada para este número
            to.getAttribute("already_send").setValueString("1");
            return true;
        }
        return toRet;
    }
    
    private boolean send(String phone, String msg, ArrayList errors)
    {
        if(SMSApi.send(phone, msg))
        {
            errors.add("Mensagem enviada para o número: " + phone+".");
            return true;
        }
        else
        {
            errors.add("Não foi possível enviar a mensagem para o número: " + phone+".");
            return false;
        }
    }
    
    private boolean sendBySMSExpress(String phone, String msg, ArrayList errors)
    {
        SMSExpressApi smsExpApi = new SMSExpressApi();
        if(smsExpApi.send(phone, msg))
        {
            errors.add("Mensagem enviada para o número: " + phone+".");
            return true;
        }
        else
        {
            if(smsExpApi.getStatusCode() == null && smsExpApi.getStatusMessage() == null)
            {
                errors.add("Não foi possível enviar a mensagem para o número: " + phone+".");
            }
            else
            {
                errors.add("Não foi possível enviar a mensagem para o número: " + phone+" ["+smsExpApi.getStatusMessage()+"].");
            }
            return false;
        }
    }
    
    private static String getText(boObject msg) throws boRuntimeException
   {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        if("messageSMS".equals(msg.getName()))
        {
            sb.append("SMS enviado na data ");
            Date d = msg.getAttribute("dtdoc").getValueDate();
            if(d == null)
            {
                d = new Date();
            }
            sb.append(sdf.format(d))
            .append(" para [");
            boBridgeIterator bit = msg.getBridge("to").iterator();
            bit.beforeFirst();
            if(bit.next())
            {
                sb.append(bit.currentRow().getObject().getAttribute("name").getValueString())
                .append(", ")
                .append(bit.currentRow().getObject().getAttribute("telemovel").getValueString());
            }
            sb.append("] com o seguinte texto:\n")
            .append(msg.getAttribute("textSMS").getValueString());
        }
        else
        {
            sb.append("E-Mail enviado na data ");
            Date d = msg.getAttribute("dtdoc").getValueDate();
            if(d == null)
            {
                d = new Date();
            }
            sb.append(sdf.format(d))
            .append(" para [");
            boBridgeIterator bit = msg.getBridge("to").iterator();
            bit.beforeFirst();
            if(bit.next())
            {
                sb.append(bit.currentRow().getObject().getAttribute("name").getValueString())
                .append(", ")
                .append(bit.currentRow().getObject().getAttribute("email").getValueString());
            }
            sb.append("] :\n")
            .append("Assunto: ").append(msg.getAttribute("name").getValueString()).append("\n")
            .append(msg.getAttribute("description").getValueString());
        }
        return sb.toString();
   }
   
   private static void fillGenerated(boObject msg, String path) throws boRuntimeException
   {
        iFile ifile = new FSiFile(null,new File(path),null);
        boObject doc = boObject.getBoManager().createObject(msg.getEboContext(), "Ebo_Document");
        doc.getAttribute("file").setValueiFile(ifile);
        doc.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(ifile.length()));
        doc.getAttribute("fileName").setValueString(ifile.getName());
        msg.getObjectBinary().setBinary(doc);
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
                    if(SMS.equals(deliveryMsg.getAttribute("media").getValueString()))
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
        originalMessage.getAttribute("dtdoc").setValueDate(new Date());
        if(MessageUtils.isToWaitResponse(originalMessage) && receivers.size() > 0)
        {
//            logger.finer("Vou criar o wait para msg!");
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


    private void addErrorMessage(String error)
    {
        if(this.engine != null)
        {
            engine.addErrorMessage(error);
        }
    }    
    
    public boolean mergeDocuments(boObject message) throws boRuntimeException
    {
        return true;
    }
    
    private static String getFile(String texto) throws boRuntimeException
    {
        FileOutputStream out = null;
        File ff = null;
        String result = null;

        try
        {

            String dir = DocumentHelper.getTempDir();
            if(!dir.endsWith(File.separator))
            {
                dir += File.separator;
            }
            dir += System.currentTimeMillis();

            File ndir = new File(dir);

            if (!ndir.exists())
            {
                ndir.mkdirs();
            }

            ff = new File(dir + File.separator + "documento.txt");
            out = new FileOutputStream(ff);
            out.write(texto.getBytes());
            result = ff.getAbsolutePath();
            logger.severe("Gerou para: " +result);
        }
        catch (Exception ex)
        {
            throw new boRuntimeException("", ex.getMessage(), ex);
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (IOException e)
            {
//                logger.severe("", e);
            }
        }

        return result;
    }
    
    
    /*
     * Devolve:
     *  0 se tudo correu bem
     *  1 se consegui enviar pelo menos uma mensagem
     *  -1 senão conseguiu enviar para nenhum número
     */
    public int adHocSend(boObject message) throws boRuntimeException
    {
//        logger.finer("SETTING TO");
        String to = null;
        boolean toRet = true;
        boolean oneSent = false;
        
        //texto
        String msg = message.getAttribute("textSMS").getValueString();

        //to
        bridgeHandler bridge = message.getBridge("to");
        boBridgeIterator bit = bridge.iterator();        
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        //cc
        bridge = message.getBridge("cc");
        bit = bridge.iterator();        
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), "CC\n"+msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        //bcc
        bridge = message.getBridge("bcc");
        bit = bridge.iterator();
        bit.beforeFirst();
        while(bit.next())
        {
            if(!send(bit.currentRow().getObject(), "BCC\n"+msg, errors))
            {
                toRet = false;
            }
            else
            {
                oneSent = true;
            }
        }
        
        message.getAttribute("dtdoc").setValueDate(new Date());
        return toRet ? 0: (oneSent ? 1:-1);
    }
    
    public boolean releaseMsg(boObject message, boObject performer)
    {
        return true;
    }
}