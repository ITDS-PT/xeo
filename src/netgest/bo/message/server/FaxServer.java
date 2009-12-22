/*Enconding=UTF-8*/
package netgest.bo.message.server;
import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import javax.activation.FileDataSource;
import javax.activation.MimeType;
import netgest.bo.*;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.ejb.*;

import netgest.bo.impl.document.merge.MergeHelper;
import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.impl.document.print.RemoteFileConversion;
import netgest.bo.message.GarbageController;
import netgest.bo.message.server.fax.FaxParser;
import netgest.bo.message.server.mail.*;
import netgest.bo.message.utils.Attach;
import netgest.bo.message.Address;

import netgest.bo.message.utils.MessageUtils;
import netgest.bo.message.utils.XEOIDUtil;
import netgest.bo.runtime.*;

import netgest.io.*;
import netgest.io.FSiFile;

import netgest.utils.*;
import netgest.utils.ClassUtils;

import netgest.xwf.EngineGate;
import netgest.xwf.core.*;
import netgest.bo.system.Logger;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.rmi.RemoteException;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class FaxServer extends Mail implements MediaServer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.media.FaxServer");
    private static final String FAX = "Fax";
    private static final String ERROR_NO_TEMPLATE = "Para enviar um Fax é necessário escolher o modelo.";
//    private XwfController controller = null;
    private EngineGate engine = null;
    private long activityBoui = -1;
    private static final String[] bridges = {"to", "cc", "bcc"};
    private EboContext ctx = null;
    private static final String ERROR_SMTP = "Não existe nenhum servidor de SMTP definido";
    private static final String ERROR_FROM = "É impossivel enviar um email sem endereço de email definido";
    private static final String ERROR_FROM_RECEIPT = "É impossivel enviar um recibo de um email sem remetente";
    private static final String ERROR_TO = "É impossivel enviar um email sem destinatários";
    private static final String ERROR_TO_RECEIPT = "É impossivel enviar um recibo de um email sem destinatários";
    private static final String ERROR_MAIL_NOT_SAVED = "O Fax tem que ser gravado antes de poder ser enviado";
    private static final String ERROR_SENDER = "Não pode enviar emails com este endereço";
    private static final String ERROR_INV_RECIPIENT_1 = "O destinário ";
    private static final String ERROR_INV_RECIPIENT_2 = " não têm um endereço email válido";
    private static final String PRFX = "XEOFAX ["+boConfig.getMailPrefix();
    private static final String PRFX2 = " - XEOFAX ["+boConfig.getMailPrefix();
    private static final String toFind = "\\sXEOFAX \\["+boConfig.getMailPrefix()+".*?\\]";
    
    private static final String[] SEND_MIMETYPES = {"application/msword", "application/pdf", "image/tiff", "application/msexcel", "text/plain", "text/html"};
    
    private static final String FAX_ACCOUNT = "gdfax@lusitania.pt";
    private FaxParser faxParser = null;
    
    /**
     * 
     * @Company Enlace3
     * @since 
     */
//    public FaxServer(XwfController controller)
//    {
//        this.controller = controller;
//    }
    public FaxServer(EngineGate engine, long activityBoui)
    {
        this.engine = engine;
        this.activityBoui = activityBoui;
        try 
        {
            this.ctx = engine.getBoManager().getContext();    
        } 
        catch (Exception ex) 
        {
            ex.printStackTrace();
        }                
    }
    
    public FaxServer(MailMessage msg)
    {
        faxParser = new FaxParser(msg);
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
        if(boConfig.getFaxConfig() == null || boConfig.getFaxConfig().isEmpty())
        {
            return setSended(message);
        }
        else
        {
            return send_via_email(context, message, true);
        }
    }
    
    public boolean send_via_email(Object context, boObject message, boolean saveBinary) throws boRuntimeException
    {
        boolean toRetTo = false, toRetCC = false, toRetBCC = false;
        ArrayList filesTodelete = new ArrayList();
        logger.finer("Preparing to send Fax Via Email");
        try
        {
            if(engine == null || ctx == null)
            {
                return false;
            }
            //De
            boObject assigned = message.getAttribute("from").getObject();

            if (assigned == null)
            {
                this.addErrorMessage(ERROR_FROM);
                return false;
            }

            long performerBoui = ctx.getBoSession().getPerformerBoui();
            boObject performer = boObject.getBoManager().loadObject(ctx,
                    performerBoui);

            if (!verifySender(performer, assigned))
            {
                this.addErrorMessage(ERROR_SENDER);
                return false;
            }

            String address = assigned.getAttribute("email").getValueString();

            String smtphost = boConfig.getFaxConfig().getProperty("smtphost");

            if ((smtphost != null) && !smtphost.equals(""))
            {
                setSMTPHost(smtphost);
            }
            else
            {
                this.addErrorMessage(ERROR_SMTP);
                return false;
            }
            String emailaddress = boConfig.getFaxConfig().getProperty("emailAddress");
            String sendAddress =  boConfig.getFaxConfig().getProperty("sendAddress");
            if (emailaddress == null || emailaddress.length() == 0 || sendAddress == null || sendAddress.length() == 0)
            {
                this.addErrorMessage("Configuração inválida do Servidor de Fax");
                return false;
            }
            
            //vou limpar a mensagem de erro para iniciar nova tentativa
            message.getAttribute("error").setValueString("0");
            message.getAttribute("errorMsg").setValueString("");
            
            //por cada to
            boBridgeIterator bit = message.getBridge("to").iterator();
            toRetTo = send_via_email(context, message, saveBinary, address, bit, 1);
            //por cada cc
            bit = message.getBridge("cc").iterator();
            toRetCC = send_via_email(context, message, saveBinary, address, bit, 2);
            //por cada bcc 
            bit = message.getBridge("bcc").iterator();
            toRetBCC = send_via_email(context, message, saveBinary, address, bit, 3);
            
            //datas
            if(MessageUtils.alreadySend(message))
            {
                message.getAttribute("dtdoc").setValueDate(new Date());
            }
            message.update();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return toRetTo && toRetCC && toRetBCC;
    }
    
    public boolean send_via_email(Object context, boObject message, boolean saveBinary, String fromAddress, boBridgeIterator bit, int type) throws boRuntimeException
    {
        bit.beforeFirst();
        boObject auxObj;
        String subject = null;
        boolean toRet = true;
        while(bit.next())
        {
            auxObj = bit.currentRow().getObject();
            if(!"1".equals(auxObj.getAttribute("already_send").getValueString()) ||
            !hasDeliverReceipt(message, auxObj)
            )
            {
                subject = auxObj.getAttribute("fax").getValueString();
                if(!subject.startsWith("0"))
                {
                    subject = "0" + subject;
                }
                if(!send_via_email(context, message, saveBinary, fromAddress, subject, type))
                {
                    toRet = false;
                }
                else
                {
                    auxObj.getAttribute("already_send").setValueString("1");
                }
            }
        }
        return toRet;
    }
    
    public boolean send_via_email(Object context, boObject message, boolean saveBinary, String fromAddress, String faxNumber, int type) throws boRuntimeException
    {
        ArrayList filesTodelete = new ArrayList();
        logger.finer("Preparing to send Fax Via Email to number: " + faxNumber);
        try
        {
            MailMessage mailmsg = MailMessage.getNewMailMessageToSent();

            //prioridade sem interesse no fax
            mailmsg.setPriority(Mail.NORMAL);
//            mailmsg.setFrom(new MailAddress(fromAddress));
            String sendAddress = boConfig.getFaxConfig().getProperty("sendAddress");
            mailmsg.setFrom(new MailAddress(sendAddress));

            //to - é o email definido no boConfig.xml
            String emailaddress = boConfig.getFaxConfig().getProperty("emailAddress");
            mailmsg.addTO(faxNumber, emailaddress);
            
            String subject = message.getAttribute("name").getValueString();
            if (subject == null ||
                subject.toUpperCase().indexOf(PRFX + message.getBoui() +
                        ":" + engine.getBoManager().getProgBoui() + "] ") == -1)
            {
                subject = (subject == null||subject.length() == 0) ? (PRFX + message.getBoui() +
                    ":" + engine.getBoManager().getProgBoui() + "] ") : 
                    (subject + PRFX2 + message.getBoui() + ":" + engine.getBoManager().getProgBoui() + "] ");
            }
            
            mailmsg.setSubject(subject);

            // Documento
            int i = 0;
            bridgeHandler bridge = message.getBridge("binaryDocuments");
            bridge.beforeFirst();
            i = addAsAttach(bridge, mailmsg, filesTodelete, i);

            //Attachs
            bridge = message.getBridge("documents");
            bridge.beforeFirst();
            i = addAsAttach(bridge, mailmsg, filesTodelete, i);


            //XEOID 
//            String refs = (PRFX + message.getBoui() + ":" + engine.getBoManager().getProgBoui() + "] ");
            mailmsg.setContent("");
            
            mailmsg.setAskForDeliveredReceipt(false);
            mailmsg.setAskForReadReceipt(false);
            
            this.addMailMessage(mailmsg);
            this.setSendMode("text/plain");
            this.send(false);
            return true;
        }
         catch (iFilePermissionDenied e)
        {
            message.getAttribute("error").setValueString("1");
            message.getAttribute("errorMsg").setValueString("Ocorreu um erro inesperado a anexar ficheiros ao email.");
            
            this.addErrorMessage(
                "Ocorreu um erro inesperado a anexar ficheiros ao email" +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn("Error: ", e);

            return false;
        }
         catch (boRuntimeException e)
        {
            message.getAttribute("error").setValueString("1");            
            if(e.getMessage() != null && e.getMessage().indexOf("Ficheiro inválido") != -1)
            {
                message.getAttribute("errorMsg").setValueString(e.getMessage());
                this.addErrorMessage(
                    e.getMessage() +
                    "<span style='display:none'>" + e.getMessage() + "</span>");
            }
            else
            {
                message.getAttribute("errorMsg").setValueString("Ocorreu um erro inesperado a enviar o email.");
                this.addErrorMessage(
                    "Ocorreu um erro inesperado a enviar o email" +
                    "<span style='display:none'>" + e.getMessage() + "</span>");
            }
            logger.warn("Error: ", e);

            return false;
        }

         catch (MessagingException e)
        {
            message.getAttribute("error").setValueString("1");
            message.getAttribute("errorMsg").setValueString("Ocorreu um erro inesperado a enviar o fax.");
            this.addErrorMessage(
                "Ocorreu um erro inesperado a enviar o fax" +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe("Error: ", e);

            return false;
        }
         catch (IOException e)
        {
            message.getAttribute("error").setValueString("1");
            message.getAttribute("errorMsg").setValueString("Erro ao anexar ficheiro.");
            logger.warn("Error: ", e);
            this.addErrorMessage("Impossível enviar attach." +
                "<span style='display:none'>" + e.getMessage() + "</span>");

            return false;
        }
         catch (Exception e)
        {
            message.getAttribute("error").setValueString("1");
            message.getAttribute("errorMsg").setValueString("Ocorreu um erro inesperado a enviar o fax.");
            this.addErrorMessage(
                "Ocorreu um erro inesperado a enviar o email" +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe("Error: ", e);

            return false;
        }
        finally
        {
            try{deleteFiles(filesTodelete);}catch(Exception e){/*ignore*/}
            try{this.clear();}catch(Exception e){/*ignore*/}
        }
    }
    
    private int addAsAttach(bridgeHandler bridge, MailMessage mailmsg, ArrayList filesTodelete, int i ) 
        throws boRuntimeException, iFilePermissionDenied, FileNotFoundException
    {
        iFile fich = null;
        InputStream in = null;
        String filename = null;
        String suffix = null;
        String prefix = null;
        File tempFile = null;
        FileOutputStream out = null;

        while (bridge.next())
        {
            i++;
            fich = bridge.getObject().getAttribute("file")
                               .getValueiFile();

            if (fich != null)
            {
                filename = fich.getName();
                suffix = MailUtil.getExtension(filename);
                prefix = MailUtil.getFirstPart(filename);

                // Verificar se o documento é tiff para converter PDF.
                if( suffix.endsWith("tif") || suffix.endsWith("tiff") )
                {
                    RemoteFileConversion rc = new RemoteFileConversion();
                    tempFile = rc.converIFile( fich, bridge.getObject().getBoui(),"pdf", Boolean.TRUE )[0];
                }
                else
                {
                    try
                    {
                        tempFile = MailUtil.createTempFile(prefix, suffix);
                        out = new FileOutputStream(tempFile);
                        in = new BufferedInputStream(fich.getInputStream());
                        MailUtil.copyFile(in, out);
                    }
                    catch (Exception e)
                    {
                        logger.severe(e);
                    }
                    finally
                    {
                        try{out.close();}catch (Exception e){}
                        try{in.close();}catch (Exception e){}                    
                    }
                }
                
                if(!validMimeType(bridge.getEboContext(), tempFile))
                {
                    throw new boRuntimeException("", "Ficheiro inválido para envio de Fax [" + bridge.getObject().getCARDID() + "]", null);
                }
                
                mailmsg.addAttach(null, tempFile.getAbsolutePath(), new Integer(i).toString(), true);
                filesTodelete.add(tempFile.getAbsolutePath());
            }
            else
            {
                throw new boRuntimeException("", "Ficheiro inválido para envio de Fax [" + bridge.getObject().getCARDID() + "]", null);
            }
        }
        return i;
    }
    
    private static boolean validMimeType(EboContext boctx, File f)
    {
        try
        {
            String mimeType = boctx.getPageContext().getServletContext().getMimeType(f.getName().toLowerCase());
            for (int i = 0; i < SEND_MIMETYPES.length; i++) 
            {
                if(SEND_MIMETYPES[i].equalsIgnoreCase(mimeType))
                {
                    return true;
                }
            }
        }
        catch (Exception e)
        {
            logger.severe("",e);
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
                    if(FAX.equals(deliveryMsg.getAttribute("media").getValueString()))
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
                    if(FAX.equals(deliveryMsg.getAttribute("media").getValueString()))
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
        boObject template = message.getAttribute("faxFormat").getObject();
        if(template == null || template.getBoui() <= 0)
        {
            template = message.getAttribute("letterFormat").getObject();
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
        boObject template = message.getAttribute("faxFormat").getObject();
        if(template == null || template.getBoui() <= 0)
        {
            template = message.getAttribute("letterFormat").getObject();
        }
        MergeHelper.reMerge(message, template, documentGenerated);
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
        if(message.getAttribute("faxFormat").getValueObject() != null ||
            message.getAttribute("letterFormat").getValueObject() != null
        )
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
    
    private static void deleteFiles(ArrayList l)
    {
        try
        {
            File auxF;
            boolean deleteDir = false;
            String parentDir = null;
            for (int i = 0; i < l.size(); i++) 
            {
                try
                {
                    auxF = new File((String)l.get(i));
                    deleteDir = false;
                    parentDir = auxF.getParent();
                    if(parentDir != null && parentDir.length() > 0)
                    {
                        File fdir = new File(parentDir);
                        if(fdir.isDirectory() && fdir.getName().startsWith(String.valueOf((Calendar.getInstance()).get(Calendar.YEAR))))
                        {
                            deleteDir = true;
                        }
                    }
                    if(auxF.exists())
                    {
                        auxF.delete();
                        if(deleteDir)
                        {
                            new File(parentDir).delete();
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.severe(e);
                }
            }
            
        }
        catch(Exception e)
        {
            logger.severe(e);
        }
    }
    
    private boolean verifySender(boObject performer, boObject deliveryMessage)
        throws boRuntimeException
    {
        boObject sender = deliveryMessage.getAttribute("refObj").getObject();
        bridgeHandler bh;

        if (
                sender.getBoui() == performer.getBoui()
                ||
                "ROBOT".equalsIgnoreCase( performer.getAttribute("username").getValueString() )
            )
        {
            return true;
        }

        if (sender.getAttribute("administrator") != null)
        {
            bh = sender.getBridge("administrator");

            if (bridgeContainsBoui(bh, performer.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("queues") != null)
        {
            bh = performer.getBridge("queues");

            if (bridgeContainsBoui(bh, sender.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("roles") != null)
        {
            bh = performer.getBridge("roles");

            if (bridgeContainsBoui(bh, sender.getBoui()))
            {
                return true;
            }
        }

        if (performer.getAttribute("groups") != null)
        {
            bh = performer.getBridge("groups");

            if (bridgeContainsBoui(bh, sender.getBoui()))
            {
                return true;
            }
        }

        return false;
    }
    
    public static boolean bridgeContainsBoui(bridgeHandler bh, long boui)
        throws boRuntimeException
    {
        boolean toRet = false;
        boBridgeIterator bit = bh.iterator();
        bit.beforeFirst();
        while (bit.next() && !toRet)
        {
            if (bit.currentRow().getObject().getBoui() == boui)
            {
                toRet = true;
            }
        }
        return toRet;
    }
    
    public static boolean isFaxReceipt(Address from, String subject, String content)
    {
        //verificar se é um recibo do FAX Server
        
        //verificar se é um email que vem da conta do fax
        String faxaddress = boConfig.getFaxConfig().getProperty("emailAddress");
        if(from != null)
        {
            String add = from.getAddress();
            if(add != null && add.equalsIgnoreCase(faxaddress))
            {
                return true;
            }
            //Fax Daemon
            add = from.getName();
            if(add != null && add.equalsIgnoreCase("FaxMaker"))
            {
                return true;
            }
        }
        
        //Todos o recibos de FAX tem de ter XEOID que é algo do tipo
        //XEOFAX [XEO1111:2222]
        Pattern p = Pattern.compile(toFind);
        String toProcess = " " + subject + " " + content + " ";
        Matcher m = p.matcher(toProcess);
        if (m.find())
        {
            return true;
        }
        return false;
    }
    
    public boolean sucessReceipt()
    {
        return faxParser == null ? false:faxParser.sucessReport();
    }
    
    public Date getReceiptReceivedDate()
    {
        String rcvDate = faxParser.getSentDate();
        Date d = null;
        try
        {
            if(rcvDate != null && rcvDate.length() > 0)
            {
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                d = sdf.parse(rcvDate);
            }
        }
        catch (ParseException e)
        {
            e.printStackTrace();            
        }
        return d;
    }
    
    public String getReceiptDescription()
    {
        return faxParser == null ? "":faxParser.getDescription();
    }
    
    public void setReceiptMessage(boObject boMsg) throws boRuntimeException
    {
        //mensagens do Servidor de FAX
        if(faxParser == null) return;
        StringBuffer sb = new StringBuffer();
        if(!faxParser.sucessReport())
        {
            String erroMsg = null;
            if(faxParser.getFaxNumber() != null && faxParser.getFaxNumber().length() > 0)
            {
                erroMsg = "Não foi possível enviar o fax para o número: " + faxParser.getFaxNumber();
            }
            else
            {
                erroMsg = "Não foi possível enviar o fax";
            }
            sb.append(erroMsg).append(".<br>");            
            sb.append("<br>Detalhes:<br>")
              .append("Operação terminou com erros.<br>");
            
            boMsg.getAttribute("name").setValueString(erroMsg);
            boMsg.getAttribute("sucessDeliv").setValueString("0");
        }
        else
        {
            sb.append("O Fax enviado para '")
                .append(faxParser.getFaxNumber())
                .append("' foi transmitido com sucesso.<br>");
            sb.append("<br>Detalhes:<br>")
              .append("Operação terminou sem erros.<br>");
            
            boMsg.getAttribute("name").setValueString("Transmissão efectuada com sucesso para o número: "+faxParser.getFaxNumber());
            boMsg.getAttribute("sucessDeliv").setValueString("1");
        }

        sb.append("<br>Assunto: ").append(faxParser.getSubject())
          .append("<br>Remetente: ").append(faxParser.getSender())
          .append("<br>E-Mail do Remetente: ").append(faxParser.getSenderMail())
          .append("<br>Estado/Resultado: ").append(faxParser.getStatus())
          .append("<br>Enviado na data: ").append(faxParser.getSentDate())
          .append("<br>Velocidade: ").append(faxParser.getSpeed())
          .append("<br>Tempo total da ligação: ").append(faxParser.getConnectionTime())
          .append("<br>Número de Páginas para enviar: ").append(faxParser.getNumberOfPages())
          .append("<br>Número de Páginas enviadas: ").append(faxParser.getPagesSended())
          .append("<br>Descrição: ").append(faxParser.getDescription());
        
          //detalhes técnicos
        sb.append("<br><br>Detalhes Técnicos:<br>")
          .append("<br>Resolução: ").append(faxParser.getResolution())
          .append("<br>Identificador da máquina remota: ").append(faxParser.getRemoteId())
          .append("<br>Número da Linha: ").append(faxParser.getLineNumber())
          .append("<br>Número de tentativas: ").append(faxParser.getRetries());

        boMsg.getAttribute("description").setValueString(sb.toString());        
    }
    
    
    private static boolean hasDeliverReceipt(boObject message, boObject deliverMsg) throws boRuntimeException
    {
        boBridgeIterator bit = message.getBridge("receipts").iterator();
        bit.beforeFirst();
        boObject receipt = null;
        String subj, receiptFaxN, faxN;
        int pos = -1;
        while(bit.next())
        {
            receipt = bit.currentRow().getObject();
            if("messageDelivered".equals(receipt.getName()) && 
               "1".equals(receipt.getAttribute("sucessDeliv").getValueString() ))
            {
                subj = receipt.getAttribute("name").getValueString();
                faxN = deliverMsg.getAttribute("fax").getValueString();
                if(subj != null && (pos = subj.indexOf(faxN)) > -1)
                {
                    receiptFaxN = getWord(subj, pos);
                    if(faxN.equalsIgnoreCase(receiptFaxN))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static String getWord(String phrase, int from)
    {
        String toRet=""; 
        for(int i = from; i < phrase.length(); i++)
        {
            if(phrase.charAt(i) == ' ' || phrase.charAt(i) ==',' || phrase.charAt(i) =='.')
            {
                break;
            }
            else
            {
                toRet = toRet + phrase.charAt(i);  
            }
        }
        return toRet;
    }
    
    public static boolean hasDeliverReceiptForAll(boObject message) throws boRuntimeException
    {
        //por cada to
        boBridgeIterator bit = message.getBridge("to").iterator();
        bit.beforeFirst();
        while(bit.next())
        {
            if(!hasDeliverReceipt(message, bit.currentRow().getObject()))
            {
                return false;
            }
        }
        //por cada cc
        bit = message.getBridge("cc").iterator();
        bit.beforeFirst();
        while(bit.next())
        {
            if(!hasDeliverReceipt(message, bit.currentRow().getObject()))
            {
                return false;
            }
        }
        //por cada bcc 
        bit = message.getBridge("bcc").iterator();
        bit.beforeFirst();
        while(bit.next())
        {
            if(!hasDeliverReceipt(message, bit.currentRow().getObject()))
            {
                return false;
            }
        }
        return true;
    }
    
    public boolean releaseMsg(boObject message, boObject performer)
    {
        return true;
    }
    
}