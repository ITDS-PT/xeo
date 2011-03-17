/*Enconding=UTF-8*/
package netgest.bo.message.server;

import java.io.CharArrayWriter;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.*;
import netgest.bo.*;

import netgest.bo.controller.xwf.XwfController;
import netgest.bo.ejb.*;

import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.GarbageController;
import netgest.bo.message.server.mail.*;
import netgest.bo.message.utils.Attach;

import netgest.bo.message.utils.MessageUtils;
import netgest.bo.message.utils.XEOIDUtil;
import netgest.bo.runtime.*;

import netgest.bo.utils.boEncrypter;
import netgest.io.*;

import netgest.utils.*;

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
public class EmailServer extends Mail implements MediaServer
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.media.EmailServer");

    //
    public static final int REPLAY = 0;
    public static final int REPLAY_ALL = 1;
    public static final int FORWARD = 2;
    private static final String ERROR_SMTP = "Não existe nenhum servidor de SMTP definido";
    private static final String ERROR_FROM = "É impossivel enviar um email sem endereço de email definido";
    private static final String ERROR_FROM_RECEIPT = "É impossivel enviar um recibo de um email sem remetente";
    private static final String ERROR_TO = "É impossivel enviar um email sem destinatários";
    private static final String ERROR_TO_RECEIPT = "É impossivel enviar um recibo de um email sem destinatários";
    private static final String ERROR_MAIL_NOT_SAVED = "O Email tem que ser gravado antes de poder ser enviado";
    private static final String ERROR_SENDER = "Não pode enviar emails com este endereço";
    private static final String ERROR_INV_RECIPIENT_1 = "O destinário ";
    private static final String ERROR_INV_RECIPIENT_2 = " não têm um endereço email válido";
    private EboContext ctx = null;
//    private XwfController controller = null;
    private EngineGate engine = null;
    private long activityBoui = -1;

    /**
     *
     * @Company Enlace3
     * @since
     */
//    public EmailServer(XwfController controller)
//    {
//        this.controller = controller; 
//        this.ctx = controller.getDocHTML().getEboContext();
//    }
    public EmailServer(EngineGate engine, long activityBoui)
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
    public EmailServer(EboContext ctx)
    {
        this.ctx = ctx;
    }

    public void read() throws boRuntimeException
    {
        boObject object = null;
        boObjectList bobj = boObjectList.list(ctx,
                "Select mailAccount where active = 1 or active is null");
        bobj.beforeFirst();

        while (bobj.next())
        {
            readAccount(bobj.getObject());
        }
    }
    public boolean mergeDocuments(boObject message) throws boRuntimeException
    {
        return true;
    }

    public void readAccount(boObject mailAccount) throws boRuntimeException
    {
        try
        {
            logger.finer(LoggerMessageLocalizer.getMessage("SYNCHRONIZING_MAILBOX_FROM_USER")+": " +
                mailAccount.getAttribute("username").getValueString());

            setPOPHost(mailAccount.getAttribute("receivehost").getValueString());

            String protocol = (mailAccount.getAttribute("mailprotocol")
                                          .getValueLong() == 1) ? "pop3" : "imap";
            setProtocol(protocol);
            setUserName(mailAccount.getAttribute("username").getValueString());
            setUserPass(boEncrypter.staticEncrypt(mailAccount.getAttribute("password").getValueString()));

            boolean delmsg = mailAccount.getAttribute("deletemessages")
                                        .getValueString().equals("1") ? true
                                                                      : false;
            setDeleteMessages(delmsg);
            setSMTPHost(mailAccount.getAttribute("smtphost").getValueString());

            boolean onlyXEO = mailAccount.getAttribute("processonlyXEOMessages")
                                         .getValueString().equals("1") ? true
                                                                       : false;
            setOnlyXEOMessage(onlyXEO);

            bridgeHandler bridge = mailAccount.getBridge("folder");
            bridge.beforeFirst();

            while (bridge.next())
            {
                boObject folder = bridge.getObject();
                String foldername = bridge.getObject().getAttribute("folder")
                                          .getValueString();

                if ((foldername != null) && !foldername.equals(""))
                {
                    readAccountFolder(mailAccount, folder);
                }
            }

            setMessageOrder(ORDER_DESC);
        }
         catch (Exception e)
        {
            //write the exception to the log
            //and continue to next mailAccount
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_SYNCHRONIZING_MAILBOX_FROM_USER")+": " +
                mailAccount.getAttribute("username").getValueString(), e);
        }
    }
    public void readAccountFolder(boObject mailAccount, boObject mailFolder)
        throws boRuntimeException
    {
        try
        {
            String folderName = mailFolder.getAttribute("folder").getValueString();
            boolean faxReceipt = false;
//            mailAccount.getAttribute("fax") == null ? false:"1".equals(mailAccount.getAttribute("fax").getValueString());
            int messagecount = numOfMessagesToGet(mailFolder.getAttribute(
                            "lastmessageid").getValueString(), folderName);
            MailMessage[] messages = readMail(messagecount, folderName, false);
            String accountEmail = mailAccount.getAttribute("email").getValueString().toLowerCase();
            ArrayList relatedBoui = null;
            ArrayList relatedProgs = null;
            ArrayList relatedObject = null;
            ArrayList objectToUpdate = null;
            ArrayList filesTodelete = new ArrayList();
            MailMessage auxMsg;
            ArrayList treatedXEOid = new ArrayList();
            String xeoid=null;
            boolean next = false;
            for (int i = messages.length - 1; i >= 0; i--)
            {
                auxMsg = messages[i];                
                try
                {
                    xeoid =auxMsg.getXEOID();
                    logger.finest(LoggerMessageLocalizer.getMessage("LOOKING_FOR_XEOID")+"= "+xeoid + " "+LoggerMessageLocalizer.getMessage("OF_MESSAGEID")+"= "+auxMsg.getMessageID());

                    boObjectList list = boObjectList.list(ctx, 
                        "select message where XEOID='" + xeoid +"' and classname <> 'messageDelivered' and classname <> 'messageSystem' and classname <> 'messageReceipt'",false,false);
                    list.beforeFirst();
                    
                    if(auxMsg.getFrom() != null && 
                        (GarbageController.isSpamEmail(mailAccount, auxMsg.getFrom().getAddress()) ||
                        GarbageController.isSpamServer(mailAccount, auxMsg.getFrom().getAddress()))
                    )
                    {
                        logger.finer(LoggerMessageLocalizer.getMessage("START_TREATING_A_SPAM_MESSAGE"));
                        mailFolder.getAttribute("lastmessageid").setValueString(auxMsg.getMessageID());
                        mailFolder.update();
                        if (getDeleteMessages())
                        {
                            deleteMessage(auxMsg.getMessageID());
                        }
                        addFilesTodelete(auxMsg, filesTodelete);
                        logger.finer(LoggerMessageLocalizer.getMessage("END_TREATING_A_SPAM_MESSAGE"));
                    }
                    else if ((next = list.next()) || treatedXEOid.contains(xeoid))
                    {
                        if(next)
                        {
                            boObject mailObject = list.getObject();
                            logger.finest(LoggerMessageLocalizer.getMessage("ATTENTION_MSG_ALREADY_EXISTS_WITH_BOUI")+" = " + mailObject.getBoui());
                                                    
                              
                            //object já existe e já foi tratado
                            //é necessário ir buscar o objecto criado verificar se este tem este email no
                            //to ou cc se não tiver terá que ser criado no bcc                    
                            //marca como última mensagem lida para não voltar a ler
                            //passa para a próxima mensagem
        
                            //to
                            MailAddress mailaddress = null;                    
                            boolean exists = false;
                            for (int j = 0; !exists && j < auxMsg.getTO().length; j++)
                            {
                                mailaddress = (MailAddress) auxMsg.getTO()[j];
                                logger.finest(LoggerMessageLocalizer.getMessage("COMPARING")+" ("+mailaddress.getFullAddress().toLowerCase()+", "+ accountEmail+")");
                                
                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail) > -1)
                                {
                                    exists = true;
                                }
                            }
                            //cc
                            for (int j = 0; !exists && j < auxMsg.getCC().length; j++)
                            {
                                mailaddress = (MailAddress) auxMsg.getCC()[j];
                                logger.finest(LoggerMessageLocalizer.getMessage("COMPARING")+" ("+mailaddress.getFullAddress().toLowerCase()+", "+ accountEmail+")");
                                
                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail) > -1)
                                {
                                    exists = true;
                                }
                            }
                            //bcc
                            for (int j = 0; !exists && j < auxMsg.getBCC().length; j++)
                            {
                                mailaddress = (MailAddress) auxMsg.getBCC()[j];
                                logger.finest(LoggerMessageLocalizer.getMessage("COMPARING")+" ("+mailaddress.getFullAddress().toLowerCase()+", "+ accountEmail+")");
    
                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail) > -1)
                                {
                                    exists = true;
                                }
                            }
                            if(!exists)
                            {
                                if(mailAccount.getParent() != null)
                                {
                                    logger.finest(LoggerMessageLocalizer.getMessage("GOING_TO_ADD_THE_BCC"));
                                    mailObject.getBridge("bcc").add(mailAccount.getParent().getBoui());
                                    mailObject.getAttribute("already_read").setValueString("0");
                                }
                                boObjectList listProgram = boObjectList.list(ctx, 
                                    "select xwfProgramRuntime where message.boui="+mailObject.getBoui());
                                xwfManager man = null; 
                                boObject program;
                                listProgram.beforeFirst();
                                while(listProgram.next())
                                {
                                    logger.finest(LoggerMessageLocalizer.getMessage("GOING_TO_CREATE_THE_RECV_MSG_FOR_THE_BCC"));
                                    program = listProgram.getObject();
                                    man = new xwfManager(ctx, program);
                                    xwfMessage.createRecActv(man,mailAccount.getParent().getBoui(), mailObject, program.getBoui());
                                    program.update();
                                }
                                //é preciso recalcular as security keys;                            
                                mailObject.update();                            
                            }
                        }
                        mailFolder.getAttribute("lastmessageid").setValueString(auxMsg.getMessageID());
                        mailFolder.update();
                        if (getDeleteMessages())
                        {
                            deleteMessage(auxMsg.getMessageID());
                        }
                        addFilesTodelete(auxMsg, filesTodelete);
                    }
                    else
                    {   
                        logger.finest(LoggerMessageLocalizer.getMessage("DOESNT_EXIST"));
                        //verificar se é para ler apenas mensagens XEO e se trata de uma mensagem XEO
                        if (relatedBoui == null)
                        {
                            relatedBoui = new ArrayList();
                            relatedProgs = new ArrayList();
                        }
                        else
                        {
                            relatedBoui.clear();
                            relatedProgs.clear();
                        }
                        logger.finest("XEO MESSAGE");
                        String auxs = (auxMsg.getContent() == null || auxMsg.getContent().length() == 0 ) ?
                                        auxMsg.getContentHTML():
                                        auxMsg.getContent();
                        boolean isXEOMessage = isXEOMessage(auxMsg.getSubject() +
                                " " + auxs , relatedBoui, relatedProgs);

                        if (isXEOMessage || !onlyXEOMessage())
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("SIM_XEO_MESSAGE"));
                            boObject mailobject = null;
                            faxReceipt = FaxServer.isFaxReceipt(auxMsg.getFrom(), auxMsg.getSubject(), auxs);
                            if(auxMsg.isSystemMessage() || faxReceipt)
                            {
                                if(!faxReceipt && auxMsg.isReadReceipt())
                                {
                                    mailobject = boObject.getBoManager()
                                                              .createObject(ctx,
                                        "messageReceipt");
                                    mailobject.getAttribute("preferedMedia").setValueString("E-Mail");
                                }
                                else
                                {
                                    mailobject = boObject.getBoManager()
                                                              .createObject(ctx,
                                        "messageDelivered");
                                    mailobject.getAttribute("preferedMedia").setValueString("E-Mail");
                                    String auxAssunto = auxMsg.getSubject();
                                    if(auxAssunto != null && (auxAssunto.toUpperCase().startsWith("NÃO")  
                                                              || auxAssunto.toUpperCase().startsWith("UNDELIVERABLE")
                                                              || auxAssunto.toUpperCase().startsWith("ERRO")
                                                              )
                                    )
                                    {
                                        mailobject.getAttribute("sucessDeliv").setValueString("0");
                                    }
                                    else
                                    {
                                        mailobject.getAttribute("sucessDeliv").setValueString("1");
                                    }
                                }
                                //falta reunião etc;
                                
                                if (relatedObject == null)
                                {
                                    relatedObject = new ArrayList();
                                }
                                else
                                {
                                    relatedObject.clear();
                                }
                            }
                            else
                            {
                                //mensagem normal
                                mailobject = boObject.getBoManager()
                                                              .createObject(ctx,
                                        "messageMail");
                            }
                            
                            //datas
                            try
                            {
                                if (auxMsg.getReceivedDate() != null)
                                {
                                    mailobject.getAttribute("dtdoc")
                                              .setValueDate(auxMsg.getSentDate());
                                }
                            }
                            catch (Exception e)
                            {
                                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DOING_THE_EMAIL_DATESET"),
                                    e);
                            }
                            
                            //prioridade
                            if (auxMsg.getPriority() > Mail.NORMAL)
                            {
                                mailobject.getAttribute("priority")
                                          .setValueString("0");
                            }
                            else if (auxMsg.getPriority() < Mail.NORMAL)
                            {
                                mailobject.getAttribute("priority")
                                          .setValueString("2");
                            }
                            else
                            {
                                mailobject.getAttribute("priority")
                                          .setValueString("1");
                            }
                            
                            //foi pedido um recibo de leitura
                            if (auxMsg.isToReturnReadReceipt())
                            {
                                mailobject.getAttribute("send_read_receipt")
                                          .setValueString("1");
                            }
                            else
                            {
                                mailobject.getAttribute("send_read_receipt")
                                          .setValueString("0");
                            }
                            
                            //from
                            setMailRecipient((MailAddress) auxMsg.getFrom(),
                                "from", mailobject);
                            
                            //replyTo
                            setMailRecipient((MailAddress) auxMsg.getFrom(),
                                "replyTo", mailobject);
                            
                            //vou verificar se este email encontra-se no to ou no cc
                            //ao mesmo tempo que preencho estes campos
                            //senão existir introduz-se no bcc
                            //to
                            logger.finest(LoggerMessageLocalizer.getMessage("TO"));
                            MailAddress mailaddress = null;
                            boolean exists = false;
                            for (int j = 0; j < auxMsg.getTO().length; j++)
                            {
                                mailaddress = (MailAddress) auxMsg.getTO()[j];
                                logger.finest(LoggerMessageLocalizer.getMessage("COMPARING")+": " + mailaddress.getEmail().toLowerCase() + " - " + accountEmail);
                                if(mailaddress.getEmail().toLowerCase().indexOf(accountEmail)>=0)
                                {
                                    exists = true;
                                }
                                this.setMailRecipient(mailaddress, "to",
                                    mailobject);
                            }
                            
                            //cc
                            logger.finest("CC");
                            for (int j = 0; j < auxMsg.getCC().length; j++)
                            {
                                mailaddress = (MailAddress) auxMsg.getCC()[j];
                                logger.finest(LoggerMessageLocalizer.getMessage("COMPARING")+": " + mailaddress.getEmail().toLowerCase() + " - " + accountEmail);
                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail)>=0)
                                {
                                    exists = true;
                                }
                                this.setMailRecipient(mailaddress, "cc",
                                    mailobject);
                            }
                            if(!exists)
                            {                        
                                if(mailAccount.getParent() != null)
                                {
                                    mailobject.getBridge("bcc").add(mailAccount.getParent().getBoui());
                                }
                            }
                            
                            //attach
                            logger.finest(LoggerMessageLocalizer.getMessage("ATTACH"));
                            for (int j = 0; j < auxMsg.getAttach().length;
                                    j++)
                            {
                                Attach att = auxMsg.getAttach()[j];
                                boObject currDoc = mailobject.getBridge(
                                        "documents").addNewObject("Ebo_Document");
                                currDoc.getAttribute("file").setValueiFile(new FSiFile(
                                        null, att.getLocation(), att.getLocation()));
                                if(att.getName() != null)
                                {
                                    currDoc.getAttribute("description").setValueString(att.getName());
                                }

                                if (att.isInline() && auxMsg.getContentHTML() != null && auxMsg.getContentHTML().length() > 0 )
                                {
                                    auxMsg.setContentHTML(StringUtils.replacestr(
                                            auxMsg.getContentHTML(),
                                            "cid:" + att.getInlineID(),
                                            StringUtils.putSlash(
                                                boConfig.getWebContextRoot()) +
                                            "attachfile.jsp?look_parentBoui=" +
                                            currDoc.getBoui() +
                                            "&att_display=y&att_download=y&curTime="+(new Date()).getTime()));
                                }
                                if(att.getDeleteAfterUse())
                                {
                                    filesTodelete.add(att.getLocation());
                                }
                            }
                            //subject
                            //tenho que remover [XEO...] do subject
                            logger.finest(LoggerMessageLocalizer.getMessage("SUBJECT"));
                            String assunto = auxMsg.getSubject();  
                            if(assunto != null && assunto.length() >= 3000)
                            {
                                assunto = assunto.substring(0, 2950);
                                assunto = assunto + "(...)";
                            }                            
                            if ((assunto == null) || assunto.equals(""))
                            {
                                assunto = "[Sem Assunto]";
                            }
                            else if(isXEOMessage)
                            {
                                assunto = removeXEOTags(assunto);
                            }

                            if (auxMsg.readError())
                            {
                                assunto = assunto +
                                    " - [EMAIL LIDO COM ERRO] ";
                            }
                            
                            mailobject.getAttribute("name").setValueString(assunto);
                            
                            //messageID
                            mailobject.getAttribute("messageid").setValueString(auxMsg.getMessageID());
                            
                            //content
                            logger.finest(LoggerMessageLocalizer.getMessage("CONTENT"));
                            if(faxReceipt)
                            {
                                //TODO: Lusitania??
                                //FaxServer.setMessage(auxMsg, mailobject);
                            }
                            else if ((auxMsg.getContent() != null) &&
                                    !"".equals(auxMsg.getContent()))
                            {
                                mailobject.getAttribute("description")
                                          .setValueString(removeXEOTags(auxMsg.getContent()));
                            }
                            else if ((auxMsg.getContentHTML() != null) &&
                                    !"".equals(auxMsg.getContentHTML()))
                            {
                                logger.finest(LoggerMessageLocalizer.getMessage("XEO_TAGS"));
                                String descr = removeXEOTags(auxMsg.getContentHTML());
                                logger.finest(LoggerMessageLocalizer.getMessage("XEO_TAGS2"));
                                descr = removeSRCTags(descr);
                                logger.finest(LoggerMessageLocalizer.getMessage("XEO_TAGS3"));
                                mailobject.getAttribute("description")
                                          .setValueString(descr);
                            }
                            
                            if(auxMsg.getSignedMsg() != null)
                            {
                                if (auxMsg.getSignedMsg().booleanValue())
                                {
                                    mailobject.getAttribute("signedMsg")
                                              .setValueString("1");
                                }
                                else
                                {
                                    mailobject.getAttribute("signedMsg")
                                              .setValueString("0");
                                }
                            }
                            
                            //binarydocuments
                            if(auxMsg.getBinaryMail() != null)
                            {
                                Attach att = auxMsg.getBinaryMail();
                                boObject document = boObject.getBoManager().createObject(ctx,"Ebo_Document");
                                document.getAttribute("file").setValueiFile(new FSiFile(null, att.getLocation(), att.getLocation()));
                                if(att.getName() != null)
                                {
                                    document.getAttribute("description").setValueString(att.getName());
                                }
                                mailobject.getObjectBinary().setBinary(document);
                                if(att.getDeleteAfterUse())
                                {
                                    filesTodelete.add(att.getLocation());
                                }
                            }
                                
                            mailobject.getAttribute("XEOID").setValueString(xeoid);

                            //ligações entre os vários objectos relaciondos
                            for (int j = 0; j < relatedBoui.size(); j++)
                            {
                                boObject o2 = (boObject) relatedBoui.get(j);

                                try
                                {
                                    if (!bridgeContainsBoui(
                                                mailobject.getBridge("attachedObjects"),
                                                o2.getBoui()))
                                    {
                                        mailobject.getBridge("attachedObjects").add(o2.getBoui());
                                    }

                                    if (o2.getName().startsWith("message"))
                                    {
                                        if (!bridgeContainsBoui(o2.getBridge(
                                                        "attachedObjects"),
                                                    mailobject.getBoui()))
                                        {
                                            o2.getBridge("attachedObjects").add(mailobject.getBoui());
                                        }
                                        if(auxMsg.isSystemMessage() || faxReceipt)
                                        {
                                            relatedObject.add(o2);
                                        }
                                    }
                                    else if (o2.getName().equals("sr") ||
                                            o2.getName().equals("claim") ||
                                            o2.getName().equals("information") ||
                                            o2.getName().equals("suggestion"))
                                    {
                                        if(relatedProgs == null || relatedProgs.size() == 0)
                                        {
                                            boObjectList llist = boObjectList.list(ctx,
                                                "SELECT xwfProgramRuntime WHERE variables.value.valueObject = "+o2.getBoui());
                                            llist.beforeFirst();
                                            if(llist.next())
                                            {
                                                relatedProgs.add(llist.getObject());
                                            }
                                        }
                                    }
                                }
                                catch (Exception e)
                                {
                                    logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"),
                                        e);
                                }
                            }
                            //ligações entre os vários programas relaciondos
                            for (int j = 0; j < relatedProgs.size(); j++)
                            {
                                boObject program = (boObject) relatedProgs.get(j);

                                try
                                {
                                    logger.finest(LoggerMessageLocalizer.getMessage("EI")+" :" + program.getBoDefinition().getName());
                                    if (!bridgeContainsBoui(
                                                program.getBridge("message"),
                                                mailobject.getBoui()))
                                    {
                                        if(!faxReceipt)
                                        {
                                            logger.finest(LoggerMessageLocalizer.getMessage("EI_GOING_TO_CREATE_THE_RECEIVERS"));                                            
                                            xwfManager man = new xwfManager(ctx, program);
                                            xwfMessage.receiveMessage(man, mailobject, program);
                                        }
                                        else
                                        {
                                            program.getBridge("message").add(mailobject.getBoui());
                                        }
                                    }
                                }
                                 catch (Exception e)
                                {
                                    logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_PROGRAM"),
                                        e);
                                }
                            }
                            
                            if(auxMsg.isSystemMessage() && !faxReceipt)
                            {
                                objectToUpdate = new ArrayList();

                                boolean treatedAsReceipt = treatReceipt(ctx, auxMsg,
                                        relatedObject, objectToUpdate, mailobject);
                                if (treatedAsReceipt)
                                {
                                    for (int j = 0; j < objectToUpdate.size(); j++) 
                                    {
                                        ((boObject)objectToUpdate.get(j)).update();
                                    }
                                }
                            }
                            else if(faxReceipt)
                            {
                                objectToUpdate = new ArrayList();
                                if(relatedObject != null && relatedObject.size() == 1)
                                {
                                    boObject relMsg =  (boObject)relatedObject.get(0);
                                    //TODO: Lusitania??
                                    Date d = null;//FaxServer.getReceivedDate(auxMsg);
                                    if (auxMsg.getReceivedDate() != null)
                                    {
                                        d = auxMsg.getReceivedDate();
                                    }
                                    else if (auxMsg.getSentDate() != null)
                                    {
                                        d = auxMsg.getSentDate();
                                    }
                                    setReceiptBridge(objectToUpdate, ((d == null) ? new Date():d), "delivery", 
                                        relMsg, mailobject);
                                    if(!"1".equals(mailobject.getAttribute("sucessDeliv").getValueString()))
                                    {
                                        if(!FaxServer.hasDeliverReceiptForAll(relMsg))
                                        {
                                            relMsg.getAttribute("error").setValueString("1");
                                            relMsg.getAttribute("errorMsg").setValueString(auxMsg.getSubject());
                                        }
                                    }
                                    else
                                    {//sucesso
                                        if(FaxServer.hasDeliverReceiptForAll(relMsg))
                                        {
                                            relMsg.getAttribute("error").setValueString("0");
                                            relMsg.getAttribute("errorMsg").setValueString("");
                                            
                                            //TODO: Lusitania??
                                            Date auxd = null; //FaxServer.getReceivedDate(auxMsg);
                                            if(auxd != null)
                                            {
                                                relMsg.getAttribute("dtEfectiv").setValueDate(auxd);
                                            }
                                            else
                                            {
                                                relMsg.getAttribute("dtEfectiv").setValueDate(new Date());
                                            }
                                        }
                                    }
                                }
                                for (int j = 0; j < objectToUpdate.size(); j++) 
                                {
                                    ((boObject)objectToUpdate.get(j)).update();
                                }
                            }
                            
                            
                            mailobject.update();                                
                            if(relatedProgs == null || relatedProgs.size() == 0)
                            {
                                boObject program = createMessageReceiveProgram(ctx, mailobject);
                                program.update();
                            }
                            //ligações entre os vários programas relacionados
                            for (int j = 0; j < relatedProgs.size(); j++)
                            {
                                ((boObject) relatedProgs.get(j)).update();
                            }

                            mailFolder.getAttribute("lastmessageid").setValueString(auxMsg.getMessageID());
                            mailFolder.update();
                            //XEOID
                            if(!treatedXEOid.contains(xeoid))
                            {
                                treatedXEOid.add(xeoid);
                            }
                            if (getDeleteMessages())
                            {
                                deleteMessage(auxMsg.getMessageID());
                            }                        
                        }
                        else
                        {
                            //ler apenas mensagens XEO e não é uma mensagem XEO não vou lêr mas vou marcar como lida
                            //para não voltar a lêr
    //                        validEmails.add(auxMsg.getMessageID());
                            mailFolder.getAttribute("lastmessageid").setValueString(auxMsg.getMessageID());
                            mailFolder.update();
                        }
                    }
                }
                catch (Exception e)
                {
                    logger.severe("", e);
                    //erro ao lêr um email
                    //marcar no mailfolder e passar para o próximo
                    treatError(auxMsg, mailFolder,e);
                    if(auxMsg != null)
                    {
                        addFilesTodelete(auxMsg, filesTodelete);   
                    }                    
                }
            }
            deleteFiles(filesTodelete);
        }
        catch (Exception e)
        {
            logger.severe( LoggerMessageLocalizer.getMessage("ERROR_SYNCHRONIZING_MAILBOX_FROM_USER")+": " +
                mailAccount.getAttribute("username").getValueString(), e);
        }
    }
    
    private static void addFilesTodelete(MailMessage auxMsg, ArrayList filesTodelete)
    {
        //binaryDocument
        if(auxMsg.getBinaryMail() != null)
        {
            Attach att = auxMsg.getBinaryMail();
            if(att.getDeleteAfterUse())
            {
                if(!filesTodelete.contains(att.getLocation()))
                {
                    filesTodelete.add(att.getLocation());
                }
            }
        }
        //attachs
        for (int j = 0; j < auxMsg.getAttach().length;
                                        j++)
        {
            Attach att = auxMsg.getAttach()[j];            
            if(att.getDeleteAfterUse())
            {
                if(!filesTodelete.contains(att.getLocation()))
                {
                    filesTodelete.add(att.getLocation());
                }
            }
        }
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

    public static void treatError(MailMessage mailMsg, boObject mailFolder, Exception e)
    {
        EboContext ctx = mailFolder.getEboContext();
        try
        {
            boObjectList list = boObjectList.list(ctx, "select mailError where messageid = '"+mailMsg.getMessageID()+"' and parent$="+mailFolder.getBoui(), 1, 1);
            list.beforeFirst();
            if(!list.next())
            {
                boObject mailError = boObject.getBoManager().createObject(ctx, "mailError");
                mailError.getAttribute("messageid").setValueString(mailMsg.getMessageID());
                mailError.getAttribute("subject").setValueString(removeXEOTags(mailMsg.getSubject()));
                String error = "";
                CharArrayWriter cw = new CharArrayWriter();
                PrintWriter pw = new PrintWriter( cw );
            
                pw.write( e.getClass().getName() );
                pw.write( '\n' );
                e.printStackTrace( pw );
                
                pw.close();            
                error = cw.toString();
                cw.close();
                mailError.getAttribute("error").setValueString(error);
                mailFolder.getBridge("errors").add(mailError.getBoui());
                mailFolder.update();
            }
        }
        catch (Exception _e)
        {
            //ignore
        }
    }
    public static String removeXEOTags(String msg)
    {
        if(msg == null || "".equals(msg) ) return "";
        String toFind = "- \\[XEO.*?\\]";

        Pattern p = Pattern.compile(toFind);
        Matcher m = p.matcher(msg);
        return m.replaceAll("");
    }
    
    public static String removeSRCTags(String msg)
    {
        if(msg == null || "".equals(msg) ) return "";
        return msg.replaceAll(" src", " xsrc");
    }

//    public void readAccountFolder(boObject mailAccount, boObject mailFolder)
//        throws boRuntimeException
//    {
//        try
//        {
//            String folderName = mailFolder.getAttribute("folder")
//                                          .getValueString();
//            int messagecount = numOfMessagesToGet(mailFolder.getAttribute(
//                        "lastmessageid").getValueString(), folderName);
//            MailMessage[] messages = readMail(messagecount, folderName);
//            ArrayList objectToUpdate = new ArrayList();
//            ArrayList validEmails = new ArrayList();
//            ArrayList relatedBoui = null;
//            ArrayList relatedObject = null;
//            MailMessage auxMsg;
//            boolean treatedAsReceipt = false;
//            String accountEmail = mailAccount.getAttribute("email").getValueString();
//            accountEmail = accountEmail.toLowerCase();
//
//            for (int i = messages.length - 1; i >= 0; i--)
//            {
//                auxMsg = messages[i];
//
//                boObjectList list = boObjectList.list(ctx,
//                        "select message where XEOID=" + auxMsg.getXEOID());
//
//                if (list.getRowCount() > 0)
//                {
//                    //object já existe e já foi tratado
//                    //é necessário ir buscar o objecto criado verificar se este tem este email no
//                    //to ou cc se não tiver terá que ser criado no bcc                    
//                    //marca como última mensagem lida para não voltar a ler
//                    //passa para a próxima mensagem
//
//                    //to
//                    MailAddress mailaddress = null;                    
//                    boolean exists = false;
//                    for (int j = 0; j < auxMsg.getTO().length; j++)
//                    {
//                        mailaddress = (MailAddress) auxMsg.getTO()[j];
//                        if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail) > 0)
//                        {
//                            exists = true;
//                        }
//                    }
//
//                    //cc
//                    for (int j = 0; j < auxMsg.getCC().length; j++)
//                    {
//                        mailaddress = (MailAddress) auxMsg.getCC()[j];
//                        if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail) > 0)
//                        {
//                            exists = true;
//                        }
//                    }
//                    if(!exists)
//                    {
//                        list.beforeFirst();
//                        list.next();
//                        boObject mailObject = list.getObject();                        
//                        if(mailAccount.getParent() != null)
//                        {
//                            mailObject.getBridge("bcc").add(mailAccount.getParent().getBoui());
//                        }
//                        mailObject.update();
//                    }                    
//                    validEmails.add(auxMsg.getMessageID());
//                }
//                else
//                {
//                    //verificar se é para ler apenas mensagens XEO e se trata de uma mensagem XEO
//                    if (relatedBoui == null)
//                    {
//                        relatedBoui = new ArrayList();
//                    }
//                    else
//                    {
//                        relatedBoui.clear();
//                    }
//
//                    boolean isXEOMessage = isXEOMessage(auxMsg.getSubject() +
//                            " " + auxMsg.getContent(), relatedBoui);
//
//                    if (isXEOMessage || !onlyXEOMessage())
//                    {
//                        treatedAsReceipt = false;
//
//                        //vou verificar se trata-se de um recibo
//                        if (auxMsg.isDeliveredReceipt() ||
//                                auxMsg.isReadReceipt())
//                        {
//                            if (relatedObject == null)
//                            {
//                                relatedObject = new ArrayList();
//                            }
//                            else
//                            {
//                                relatedObject.clear();
//                            }
//
//                            for (int j = 0; j < relatedBoui.size(); j++)
//                            {
//                                boObject o2 = (boObject) relatedBoui.get(j);
//
//                                try
//                                {
//                                    if (o2.getName().equals("message"))
//                                    {
//                                        relatedObject.add(o2);
//                                    }
//                                }
//                                 catch (Exception e)
//                                {
//                                    logger.warn("Erro devido a ligação a um objecto inexistente",
//                                        e);
//                                }
//                            }
//
//                            treatedAsReceipt = treatReceipt(ctx, auxMsg,
//                                    relatedObject, objectToUpdate);
//
//                            if (treatedAsReceipt)
//                            {
//                                validEmails.add(auxMsg.getMessageID());
//                            }
//                        }
//
//                        if (!treatedAsReceipt)
//                        {
//                            boObject mailobject = boObject.getBoManager()
//                                                          .createObject(ctx,
//                                    "message");
//
//                            //natureza
//                            mailobject.getAttribute("nature").setValueString("E");
//
//                            //datas
//                            try
//                            {
//                                if (auxMsg.getReceivedDate() != null)
//                                {
//                                    mailobject.getAttribute("dtdoc")
//                                              .setValueDate(auxMsg.getReceivedDate());
//                                    mailobject.getAttribute("beginDate")
//                                              .setValueDate(auxMsg.getReceivedDate());
//                                }
//                            }
//                             catch (Exception e)
//                            {
//                                logger.warn("Erro ao efectuar o set das datas do email.",
//                                    e);
//                            }
//
//                            //prioridade
//                            if (auxMsg.getPriority() > Mail.NORMAL)
//                            {
//                                mailobject.getAttribute("priority")
//                                          .setValueString("0");
//                            }
//                            else if (auxMsg.getPriority() < Mail.NORMAL)
//                            {
//                                mailobject.getAttribute("priority")
//                                          .setValueString("2");
//                            }
//                            else
//                            {
//                                mailobject.getAttribute("priority")
//                                          .setValueString("1");
//                            }
//
//                            //foi pedido um recibo de leitura
//                            if (auxMsg.isToReturnReadReceipt())
//                            {
//                                mailobject.getAttribute("send_read_receipt")
//                                          .setValueString("1");
//                            }
//                            else
//                            {
//                                mailobject.getAttribute("send_read_receipt")
//                                          .setValueString("0");
//                            }
//                            
//                            
//                            //from
//                            setMailRecipient((MailAddress) auxMsg.getFrom(),
//                                "from", mailobject);
//
//                            //vou verificar se este email encontra-se no to ou no cc
//                            //ao mesmo tempo que preencho estes campos
//                            //senão existir introduz-se no bcc
//                            //to
//                            MailAddress mailaddress = null;
//                            boolean exists = false;
//                            for (int j = 0; j < auxMsg.getTO().length; j++)
//                            {
//                                mailaddress = (MailAddress) auxMsg.getTO()[j];
//                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail)>0)
//                                {
//                                    exists = true;
//                                }
//                                this.setMailRecipient(mailaddress, "to",
//                                    mailobject);
//                            }
//
//                            //cc
//                            for (int j = 0; j < auxMsg.getCC().length; j++)
//                            {
//                                mailaddress = (MailAddress) auxMsg.getCC()[j];
//                                if(mailaddress.getFullAddress().toLowerCase().indexOf(accountEmail)>0)
//                                {
//                                    exists = true;
//                                }
//                                this.setMailRecipient(mailaddress, "cc",
//                                    mailobject);
//                            }
//
//                            if(!exists)
//                            {                        
//                                if(mailAccount.getParent() != null)
//                                {
//                                    mailobject.getBridge("bcc").add(mailAccount.getParent().getBoui());
//                                }
//                            }    
//
//                            //attach
//                            for (int j = 0; j < auxMsg.getAttach().length;
//                                    j++)
//                            {
//                                Attach att = (Attach) auxMsg.getAttach()[j];
//                                boObject currDoc = mailobject.getBridge(
//                                        "documents").addNewObject("Ebo_Document");
//                                currDoc.getAttribute("file").setValueiFile(new FSiFile(
//                                        null, att.getLocation(), null));
//
//                                if (att.isInline())
//                                {
//                                    auxMsg.setContent(tools.replacestr(
//                                            auxMsg.getContent(),
//                                            "cid:" + att.getInlineID(),
//                                            tools.putSlash(
//                                                boConfig.getWebContextRoot()) +
//                                            "attachfile.jsp?look_parentBoui=" +
//                                            currDoc.getBoui() +
//                                            "&att_display=y&att_download=y"));
//                                }
//                            }
//
//                            //subject
//                            String assunto = auxMsg.getSubject();
//
//                            if ((assunto == null) || assunto.equals(""))
//                            {
//                                assunto = "[Sem Assunto]";
//                            }
//
//                            if (auxMsg.readError())
//                            {
//                                assunto = assunto +
//                                    " - [EMAIL LIDO COM ERRO] ";
//                            }
//
//                            mailobject.getAttribute("name").setValueString(assunto);
//
//                            //messageID
//                            mailobject.getAttribute("messageid").setValueString(auxMsg.getMessageID());
//
//                            //content
//                            if ((auxMsg.getContent() != null) &&
//                                    !"".equals(auxMsg.getContent()))
//                            {
//                                mailobject.getAttribute("description")
//                                          .setValueString(auxMsg.getContent());
//                            }
//
//                            //XEOID
//                            mailobject.getAttribute("XEOID").setValueString(auxMsg.getXEOID());
//
//                            //ligações entre os vários objectos relaciondos
//                            for (int j = 0; j < relatedBoui.size(); j++)
//                            {
//                                boObject o2 = (boObject) relatedBoui.get(j);
//
//                                try
//                                {
//                                    if (!bridgeContainsBoui(
//                                                mailobject.getBridge("RO"),
//                                                o2.getBoui()))
//                                    {
//                                        mailobject.getBridge("RO").add(o2.getBoui());
//                                    }
//
//                                    if (o2.getName().equals("email"))
//                                    {
//                                        if (!bridgeContainsBoui(o2.getBridge(
//                                                        "RO"),
//                                                    mailobject.getBoui()))
//                                        {
//                                            o2.getBridge("RO").add(mailobject.getBoui());
//                                        }
//                                    }
//                                    else if (o2.getName().equals("sr") ||
//                                            o2.getName().equals("claim") ||
//                                            o2.getName().equals("information") ||
//                                            o2.getName().equals("suggestion"))
//                                    {
//                                        if (!bridgeContainsBoui(o2.getBridge(
//                                                        "DAO"),
//                                                    mailobject.getBoui()))
//                                        {
//                                            o2.getBridge("DAO").add(mailobject.getBoui());
//                                        }
//                                    }
//                                }
//                                 catch (Exception e)
//                                {
//                                    logger.warn("Erro devido a ligação a um objecto inexistente",
//                                        e);
//                                }
//                            }
//
//                            validEmails.add(mailobject);
//                        }
//                    }
//                    else
//                    {
//                        //ler apenas mensagens XEO e não é uma mensagem XEO não vou lêr mas vou marcar como lida
//                        //para não voltar a lêr
//                        validEmails.add(auxMsg.getMessageID());
//                    }
//                }
//            }
//
//            String lastRead = null;
//            boObject treatingEmail = null;
//            boObject auxObj;
//
//            try
//            {
//                for (int i = 0; i < validEmails.size(); i++)
//                {
//                    if (validEmails.get(i) instanceof String)
//                    {
//                        lastRead = (String) validEmails.get(i);
//
//                        if (getDeleteMessages())
//                        {
//                            deleteMessage(lastRead);
//                        }
//                    }
//                    else
//                    {
//                        treatingEmail = ((boObject) validEmails.get(i));
//                        treatingEmail.update();
//                        lastRead = treatingEmail.getAttribute("messageid")
//                                                .getValueString();
//
//                        if (getDeleteMessages())
//                        {
//                            deleteMessage(lastRead);
//                        }
//                    }
//                }
//
//                if ((lastRead != null) && !"".equals(lastRead))
//                {
//                    mailFolder.getAttribute("lastmessageid").setValueString(lastRead);
//                }
//            }
//             catch (Exception e)
//            {
//                if ((lastRead != null) && !"".equals(lastRead))
//                {
//                    mailFolder.getAttribute("lastmessageid").setValueString(lastRead);
//                }
//
//                if (treatingEmail != null)
//                {
//                    try
//                    {
//                        logger.severe("Erro reading mails: \n Account name: " +
//                            this.getPOPHost() + " " + this.getUserName() +
//                            " Email(subject - messageid):" +
//                            treatingEmail.getAttribute("name").getValueString() +
//                            " - " +
//                            treatingEmail.getAttribute("messageid")
//                                         .getValueString() + "\n" +
//                            e.getMessage(), e);
//                    }
//                     catch (Exception _e)
//                    {
//                        logger.severe("Erro reading mails: \n Account name: " +
//                            this.getPOPHost() + " " + this.getUserName() +
//                            "\n" + e.getMessage(), e);
//                    }
//                }
//                else
//                {
//                    logger.severe("Erro reading mails: \n Account name: " +
//                        this.getPOPHost() + " " + this.getUserName() + "\n" +
//                        e.getMessage(), e);
//                }
//            }
//        }
//         catch (Exception e)
//        {
//            logger.severe("Error synchronizing mailBox from user: " +
//                mailAccount.getAttribute("username").getValueString(), e);
//        }
//    }

    private int numOfMessagesToGet(String lastMessageID, String foldername)
    {
        Folder folder = null;
        Session session = null;
        Store store = null;
        int numofMessages = 0;
        MailMessage[] messages = null;

        try
        {
            Properties props = new Properties();

            session = Session.getDefaultInstance(props, null);
            store = session.getStore(this.getProtocol());
            store.connect(this.getPOPHost(), this.getUserName(),
                this.getUserPass());

            // Get folder
            if ((foldername == null) || foldername.equals(""))
            {
                folder = store.getFolder("INBOX");
            }
            else
            {
                folder = store.getFolder(foldername);
            }

            folder.open(Folder.READ_WRITE);

            // Get directory
            MimeMessage message = null;
            MailMessage mailmsg = null;
            int messageCount = folder.getMessageCount();
            messages = new MailMessage[messageCount];

            for (int i = messageCount; i >= 1; i--)
            {
                message = (MimeMessage) folder.getMessage(i);

                String auxText = message.getMessageID();

                if (!auxText.equals(lastMessageID))
                {
                    numofMessages++;
                }
                else
                {
                    break;
                }
            }

            folder.close(false);
            store.close();

            return numofMessages;
        }
         catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return 0;
        }
    }

    private boolean isXEOMessage(String toProcess, ArrayList relatedBoui, ArrayList relatedProgram)
        throws boRuntimeException
    {
        String toFind = "\\[XEO.*?\\]";

        Pattern p = Pattern.compile(toFind);
        Matcher m = p.matcher(toProcess);
        boolean isXEOMessage = false;
        ArrayList msgbouis = new ArrayList();        
        ArrayList progbouis = new ArrayList();
        String msgStrboui = null;
        String prgStrboui = null;
        String sboui = null;
        long boui = -1;
        long msgboui = 0;
        long prgboui = 0;

        while (m.find())
        {
            int start = m.start();
            int progSeparator = toProcess.indexOf(":",start);
            if(progSeparator > -1 && progSeparator < m.end())
            {
//                logger.finer("Trata-se de uma mensagem XEO com programa");
                msgStrboui = toProcess.substring(start + 4, progSeparator);
                prgStrboui = toProcess.substring(progSeparator + 1, m.end()-1);
    
                try
                {
                    msgboui = ClassUtils.convertToLong(msgStrboui);
                    prgboui = ClassUtils.convertToLong(prgStrboui);
                    
                }
                 catch (Exception e)
                {
                    msgboui = 0;
                }
    
                if (msgboui > 0)
                {
                    if (!msgbouis.contains(msgStrboui))
                    {
//                        logger.finer("adicionei o attached object: "+ msgStrboui);
                        msgbouis.add(msgStrboui);
                    }
                    if (!progbouis.contains(prgStrboui))
                    {
//                        logger.finer("adicionei o programa: "+ prgStrboui);
                        progbouis.add(prgStrboui);
                    }
                }
            }
            else
            {
//                 logger.finer("Trata-se de uma mensagem XEO sem programa");
                 boui = -1;
                 try
                 {
                    sboui = toProcess.substring( m.start()+4, m.end()-1 );
                    boui= ClassUtils.convertToLong( sboui );
                 }
                 catch (Exception e)
                 {
                     boui = 0;
                 }
                 if ( boui > 0 )
                 {  
                     if ( !msgbouis.contains(sboui) )
                     {
                        msgbouis.add( sboui );
                     }
                 }
            }
        }

        if (msgbouis.size() > 0)
        {
            //   long[] bouiList= new long[ bouis.size() ];
            for (int i = 0; i < msgbouis.size(); i++)
            {
                msgboui = ClassUtils.convertToLong((String) msgbouis.get(i));
                try
                {
                    prgboui = ClassUtils.convertToLong((String) progbouis.get(i));
                }
                catch(Exception e) {prgboui = 0;}

                if (msgboui > 0)
                {
                    try
                    {
                        boObject o2 = boObject.getBoManager().loadObject(ctx,
                                msgboui);
                        if (o2 != null)
                        {
                            relatedBoui.add(o2);
                            isXEOMessage = true;
                        }
                    }
                     catch (Exception e)
                    {
                        logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"),
                            e);
                    }
                }
                
                if (prgboui > 0)
                {
                    try
                    {

                        boObject po2 = boObject.getBoManager().loadObject(ctx,
                                prgboui);
                        if (po2 != null)
                        {
                            relatedProgram.add(po2);
                            isXEOMessage = true;
                            
                        }
                    }
                     catch (Exception e)
                    {
                        logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"),
                            e);
                    }
                }
            }
        }

        return isXEOMessage;
    }

    private static boolean treatReceipt(EboContext ctx,
        MailMessage receiptEmail, ArrayList relatedObject, ArrayList objToUpdate, boObject mailObject)
    {
        try
        {
            Date d = new Date();
            String[] subjects = null;

            if (receiptEmail.getReceivedDate() != null)
            {
                d = receiptEmail.getReceivedDate();
            }
            else if (receiptEmail.getSentDate() != null)
            {
                d = receiptEmail.getSentDate();
                //                logger.finer("Recibo sem data");
            }

            if ((receiptEmail.getContextThread() != null) &&
                    !"".equals(receiptEmail.getContextThread().trim()))
            {
                subjects = receiptEmail.getContextThread().split(";");
            }
            else if(receiptEmail.getSubject() != null && (!"".equals(receiptEmail.getSubject())))
            {
                subjects = new String[1];
                subjects[0] = receiptEmail.getSubject();
            }
            
            if(subjects != null && subjects.length > 0)
            {
                String attrName;
                String attrDt;

                if (receiptEmail.isReadReceipt())
                {
                    attrName = "requestReadReceipt";
                    attrDt = "read";
                }
                else
                {
                    attrName = "requestDeliveryReceipt";
                    attrDt = "delivery";
                }

                return (easyWay(ctx, receiptEmail, relatedObject, objToUpdate,
                    subjects, d, attrName, attrDt, mailObject) == true) ? true
                                                            : hardWay(ctx,
                    receiptEmail, relatedObject, objToUpdate, subjects, d,
                    attrName, attrDt, mailObject);
            }
        }
         catch (boRuntimeException ex)
        {
            logger.warn(ex);
        }

        return false;
    }

    private static boolean easyWay(EboContext ctx, MailMessage receiptEmail,
        ArrayList relatedObjs, ArrayList objToUpdate, String[] subjects,
        Date d, String attrName, String attrDt, boObject mailObject) throws boRuntimeException
    {
        String[] subjs;
        boObject aux;
        boObject foundedObj = null;
        boolean found = false;
        boolean end = false;

        for (int i = 0; (i < relatedObjs.size()) && !end; i++)
        {
            aux = (boObject) relatedObjs.get(i);

            if (aux.getName().startsWith("message"))
            {
                for (int j = 0; j < subjects.length; j++)
                {
                    if (receiptEmail.isDeliveredReceipt() || "1".equals(aux.getAttribute(attrName)
                                            .getValueString()))
                    {
                        if (!found)
                        {
                            found = true;
                            foundedObj = aux;
                        }
                        else
                        {
                            if(foundedObj.getBoui() < aux.getBoui())
                            {
                                foundedObj = aux;
                            }
                        }
                    }
                }
            }
        }

        if (found)
        {
            setReceiptBridge(objToUpdate, d, attrDt,
                foundedObj, mailObject);

            return true;
        }

        return false;
    }

    private static boolean hardWay(EboContext ctx, MailMessage receiptEmail,
        ArrayList relatedObjs, ArrayList objToUpdate, String[] subjects,
        Date d, String attrName, String attrDt, boObject mailObject) throws boRuntimeException
    {
//            logger.warn("HardWay");
        boObjectList threadList = null;
        long boui = 0;
        String auxStr;

        for (int j = 0; j < subjects.length; j++)
        {
            String[] subjs = removeXEO(subjects[j]);

            for (int k = 0; k < subjs.length; k++)
            {
//                logger.warn("SELECT message WHERE upper(name) = upper('" +
//                    subjs[k] + "') and " + attrName + " = 1");

                if(receiptEmail.isDeliveredReceipt())
                {
                    auxStr = "SELECT message WHERE upper(name) = upper('" +
                        subjs[k] + "') ";
                }
                else
                {
                    auxStr = "SELECT message WHERE upper(name) = upper('" +
                        subjs[k] + "') and " + attrName + " = 1";
                }
                threadList = boObjectList.list(ctx,
                        auxStr);
                boui = 0;
                threadList.beforeFirst();

                boObject aux;
                boObject foundedObj = null;
                boolean found = false;
                boolean end = false;
                boolean auxB;

                while (threadList.next() && !end)
                {
                    aux = threadList.getObject();
                    auxB = false;

                    if (arrayContainsBoui(relatedObjs, aux.getBoui()))
                    {
                        auxB = true;
                    }

                    if (auxB && !found)
                    {
                        found = true;
                        foundedObj = aux;
                    }
                    else if (auxB && found)
                    {
                        if(foundedObj.getBoui() < aux.getBoui())
                        {
                            foundedObj = aux;
                        }
                    }
                }

                if (found)
                {
                    setReceiptBridge(objToUpdate, d,
                        attrDt, foundedObj, mailObject);

                    return true;
                }
            }
        }

        return false;
    }

    private static void setReceiptBridge(
        ArrayList objToUpdate, Date d, String attrDt,
        boObject foundedObj, boObject mailObject) throws boRuntimeException
    {
        bridgeHandler bh = foundedObj.getBridge("receipts");
        if(!bh.haveBoui(mailObject.getBoui()))
        {
            bh.add(mailObject.getBoui());
        }
        if (objToUpdate.indexOf(foundedObj) == -1)
        {
            objToUpdate.add(foundedObj);
        }
        mailObject.getAttribute(attrDt).setValueDate(d);        
    }

    private static String[] removeXEO(String subject)
    {
        int start = -1;
        String[] toRet = null;
        ArrayList l = new ArrayList();

        if ((start = subject.toUpperCase().indexOf(" - [XEO")) != -1)
        {
            toRet = new String[2];
            toRet[0] = subject.substring(0, start);
            toRet[1] = subject;
        }
        else
        {
            toRet = new String[1];
            toRet[0] = subject.substring(0);
        }

        return toRet;
    }

//    private static boolean toContainsFrom(boObject mail, boObject receipt)
//    {
//        String email1 = getFromObject(mail);
//        String email2 = getToObject(receipt);
//
//        if ((email1 != null) && (email2 != null))
//        {
//            String[] aux = email2.split(";");
//
//            for (int i = 0; i < aux.length; i++)
//            {
//                if (aux[i].trim().toUpperCase().equals(email1.trim()
//                                                                 .toUpperCase()))
//                {
//                    return true;
//                }
//            }
//        }
//
//        return false;
//    }

//    private static String getFromObject(boObject mail)
//    {
//        try
//        {
//            if (mail.getAttribute("mail_from").getObject() != null)
//            {
//                boObject runtime = mail.getAttribute("mail_from").getObject();
//
//                if (runtime.getAttribute("refObj").getObject() != null)
//                {
//                    return runtime.getAttribute("refObj").getValueString();
//                }
//                else
//                {
//                    return runtime.getAttribute("email").getValueString();
//                }
//            }
//
//            if (mail.getAttribute("assignedQueue").getObject() != null)
//            {
//                return mail.getAttribute("assignedQueue").getObject()
//                           .getAttribute("email").getValueString();
//            }
//        }
//         catch (boRuntimeException e)
//        {
//            return null;
//        }
//
//        return null;
//    }
//
//    private static String getToObject(boObject mail)
//    {
//        String[] br = { "mail_to", "mail_cc", "mail_bcc" };
//
//        try
//        {
//            StringBuffer sb = new StringBuffer();
//
//            for (int i = 0; i < br.length; i++)
//            {
//                bridgeHandler bh = mail.getBridge(br[i]);
//                bh.beforeFirst();
//
//                while (bh.next())
//                {
//                    boObject runtime = bh.getObject();
//
//                    if (runtime.getAttribute("refObj").getObject() != null)
//                    {
//                        sb.append(runtime.getAttribute("refObj").getValueString())
//                          .append(";");
//                    }
//                    else
//                    {
//                        sb.append(runtime.getAttribute("email").getValueString())
//                          .append(";");
//                    }
//                }
//            }
//
//            return sb.toString();
//        }
//         catch (boRuntimeException e)
//        {
//            return null;
//        }
//    }

    public static boolean arrayContainsBoui(ArrayList list, long boui)
        throws boRuntimeException
    {
        boolean toRet = false;

        for (int i = 0; (i < list.size()) && !toRet; i++)
        {
            if (((boObject) list.get(i)).getBoui() == boui)
            {
                toRet = true;
            }
        }

        return toRet;
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

    private boObject setMailRecipient(MailAddress email,
        String attributeToAssociate, boObject mailobject) throws boRuntimeException
    {
        try
        {
            //Check if emailaddress exists as a performer and if so associates the email with it
            if(email == null || email.getEmail() == null || "".equals(email.getEmail()))
            {
                boObject recipient = boObject.getBoManager().createObject(ctx, "recipient");
                recipient.getAttribute("email").setValueString("[Undisclaimed]");
                if(email != null && email.getName() != null && !"".equals(email.getName()))
                {
                    String name = email.getName().toUpperCase();
                    if(name.startsWith("'"))
                    {
                        name = name.substring(1,name.length());
                    }
                    if(name.endsWith("'"))
                    {
                        name = name.substring(0,name.length()-1);
                    }
                    recipient.getAttribute("name").setValueString(email.getName());
                }
                else
                {
                    recipient.getAttribute("name").setValueString("[Undisclaimed]");
                }
                recipient.update();
                if ("from".equals(attributeToAssociate) || "replyTo".equals(attributeToAssociate))
                {
                    mailobject.getAttribute(attributeToAssociate).setValueLong(recipient.getBoui());
                }
                else
                {
                    mailobject.getBridge(attributeToAssociate).add(recipient.getBoui());
                }
            }
            else
            {
                String emailAddress = email.getEmail().toUpperCase();
                if(emailAddress.startsWith("'"))
                {
                    emailAddress = emailAddress.substring(1,emailAddress.length());
                }
                if(emailAddress.endsWith("'"))
                {
                    emailAddress = emailAddress.substring(0,emailAddress.length()-1);
                }
                
                emailAddress = emailAddress.replaceAll("'", "''");
                
                boolean checkOk = this.checkEmailAddress(
                        "Select iXEOUser where upper(iXEOUser.emailAccounts.email)= '" +
                        emailAddress + "' and (iXEOUser.emailAccounts.active <> '0')", attributeToAssociate, mailobject,
                        true);
    
                if (checkOk)            
                {
                    return mailobject;
                }
                
                try
                {
                    checkOk = this.checkEmailAddress(
                            "Select Ebo_PerfAnacom where upper(Ebo_PerfAnacom.email)= '" +
                            emailAddress + "'", attributeToAssociate, mailobject,
                            true);
        
                    if (checkOk)            
                    {
                        return mailobject;
                    }
                }catch(Exception e)
                {
                    //não é um objecto específico da plataforma vou ignorar
                }
    
                //Check if emailaddress exists as a group and if so associates the email with it
                checkOk = this.checkEmailAddress(
                        "Select Ebo_Group where upper(Ebo_Group.emailAccounts.email)= '" +
                        emailAddress + "' and (Ebo_Group.emailAccounts.active <> '0')", attributeToAssociate, mailobject,
                        true);
    
                if (checkOk)
                {
                    return mailobject;
                }
    
                //Check if emailaddress exists as a Role and if so associates the email with it
                checkOk = this.checkEmailAddress(
                        "Select Ebo_Role where upper(Ebo_Role.emailAccounts.email)= '" +
                        emailAddress + "' and (Ebo_Role.emailAccounts.active <> '0')", attributeToAssociate, mailobject,
                        true);
    
                if (checkOk)
                {
                    return mailobject;
                }
    
                //Check if emailaddress exists as a workQueue and if so associates the email with it
                checkOk = this.checkEmailAddress(
                        "Select workQueue where upper(workQueue.emailAccounts.email)= '" +
                        emailAddress + "' and (workQueue.emailAccounts.active <> '0')", attributeToAssociate, mailobject,
                        true);
    
                if (checkOk)
                {
                    return mailobject;
                }
    
                //Check if emailaddress exists as a contact and if so associates the email with it
                checkOk = this.checkEmailAddress(
                        "Select Ebo_Contact where upper(Ebo_Contact.email)= '" +
                        emailAddress + "'", attributeToAssociate, mailobject,
                        false);
    
                if (checkOk)
                {
                    return mailobject;
                }
    
                boManagerLocal man = boObject.getBoManager();
                boolean found = false;
                boObject recipient = null;
                String name = "[Undisclaimed]";
                if(email.getName() != null && !"".equals(email.getName()))
                {
                    name = email.getName().toUpperCase();
                    if(name.startsWith("'"))
                    {
                        name = name.substring(1,name.length());
                    }
                    if(name.endsWith("'"))
                    {
                        name = name.substring(0,name.length()-1);
                    }
                    if(name.length() > 100)
                    {
                        name = name.substring(0, 90) + "(...)";
                    }                    
                }
                boObjectList listRec = boObjectList.list(ctx, "select recipient where upper(email)=upper('" + emailAddress +"') and upper(name) = upper('" +name.replaceAll("'", "''")+ "')");
                listRec.beforeFirst();
                if(listRec.next())
                {
                    recipient = listRec.getObject();
                    found = true;
                }

                if(!found)
                {
                    recipient = man.createObject(ctx, "recipient");
                    recipient.getAttribute("email").setValueString(email.getEmail());
                    recipient.getAttribute("name").setValueString(name);
                    recipient.update();
                }
                if ("from".equals(attributeToAssociate) || "replyTo".equals(attributeToAssociate))
                {
                    mailobject.getAttribute(attributeToAssociate).setValueLong(recipient.getBoui());
                }
                else
                {
                    mailobject.getBridge(attributeToAssociate).add(recipient.getBoui());
                }
            }

            return mailobject;
        }
         catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            throw new boRuntimeException("", "", e);
        }
    }

    private boolean checkEmailAddress(String boql, String attributeToAssociate,
        boObject mailobject, boolean newmail) throws boRuntimeException
    {
        boolean ret = false;
        logger.finest(boql);
        boObjectList bobj = boObjectList.list(ctx, boql);
        bobj.beforeFirst();

        while (bobj.next())
        {            

            boObject object = bobj.getObject();


            ret = true;
            if ("from".equals(attributeToAssociate) || "replyTo".equals(attributeToAssociate))
            {
                mailobject.getAttribute(attributeToAssociate).setValueLong(object.getBoui());
            }
            else
            {
                mailobject.getBridge(attributeToAssociate).add(object.getBoui());
            }
        }

        return ret;
    }

    public boolean deleteMessage(boObject message) throws boRuntimeException
    {
        return deleteMessage(message.getAttribute("messageid").getValueString());
    }

    public boolean deleteMessage(String msgid)
    {
        Folder folder = null;
        Session session = null;
        Store store = null;
        MailMessage[] messages = null;
        Properties props = null;

        if (msgid != null)
        {
            props = new Properties();

            try
            {
                session = Session.getDefaultInstance(props, null);
                store = session.getStore(this.getProtocol());
                store.connect(this.getPOPHost(), this.getUserName(),
                    this.getUserPass());

                // Get folder
                folder = store.getFolder("INBOX");
                folder.open(Folder.READ_WRITE);

                // Get directory
                MimeMessage message = null;
                MailMessage mailmsg = null;
                messages = new MailMessage[folder.getMessageCount()];

                for (int i = 1; i <= folder.getMessageCount(); i++)
                {
                    message = (MimeMessage) folder.getMessage(i);

                    String currid = message.getMessageID();

                    if (msgid.equals(currid))
                    {
                        message.setFlag(Flags.Flag.DELETED, true);
                    }
                }

                folder.close(true);

                return true;
            }
             catch (MessagingException e)
            {
                logger.severe(
                    "Erro reading mails: Deleting messages on the server \n Account name: " +
                    this.getPOPHost() + " " + this.getUserName() + "\n" +
                    e.getMessage(), e);
            }
        }

        return false;
    }
//    public boolean send(Object controller, boObject message)
    public boolean send(Object engine, boObject message, boolean saveBinary)
    {
        this.engine = (EngineGate)engine;
        return send(message, saveBinary);
    }

    public boolean send(boObject message, boolean saveBinary)
    {
        ArrayList filesTodelete = new ArrayList();
//        logger.finer("Preparing to send");
        try
        {
//            logger.finer("1");
            if(engine == null || ctx == null)
            {
//                logger.finer("2");
                return false;
            }
//            logger.finer("3");
//            if (!message.exists())
//            {
//                logger.finer("4");
//                message.addErrorMessage(ERROR_MAIL_NOT_SAVED);
//
//                return false;
//            }

            //De
//            logger.finer("5");
            boObject assigned = message.getAttribute("from").getObject();

            if (assigned == null)
            {
                this.addErrorMessage(ERROR_FROM);
//                logger.finer("Error no FROM");
                return false;
            }
//            logger.finer("6");
            long performerBoui = ctx.getBoSession().getPerformerBoui();
            boObject performer = boObject.getBoManager().loadObject(ctx,
                    performerBoui);

//            logger.finer("5");
            if (!verifySender(performer, assigned))
            {
                this.addErrorMessage(ERROR_SENDER);
//                logger.finer("Error Verifying send");
                return false;
            }
//            logger.finer("8");
            String address = assigned.getAttribute("email").getValueString();

            if ((address == null) || address.equals(""))
            {
                this.addErrorMessage(ERROR_FROM);
//                logger.finer("FROM no email");
                return false;
            }
//            logger.finer("9");
            String smtphost = boConfig.getMailConfig().getProperty("smtphost");

            if ((smtphost != null) && !smtphost.equals(""))
            {
//                logger.finer("10");
                setSMTPHost(smtphost);
//                logger.finer("11");
            }
            else
            {
                this.addErrorMessage(ERROR_SMTP);
//                logger.finer("NO SMTP HOST");
                return false;
            }

//            logger.finer("11");
            MailMessage mailmsg = MailMessage.getNewMailMessageToSent();

            //prioridade
//            logger.finer("SETTING PRIORITY");
            String priority = message.getAttribute("priority").getValueString();

            if ("0".equals(priority))
            {
                mailmsg.setPriority(Mail.LOWEST);
            }
            else if ("2".equals(priority))
            {
                mailmsg.setPriority(Mail.HIGHEST);
            }
            else
            {
                mailmsg.setPriority(Mail.NORMAL);
            }
//            logger.finer("FINISHED SETTING PRIORITY");
            //de
//            logger.finer("SETTING FROM");
            mailmsg.setFrom(new MailAddress(address));

            boolean hasRecipient = false;

            //to
//            logger.finer("SETTING TO");
            bridgeHandler bridge = message.getBridge("to");
            bridge.beforeFirst();

            boObject auxObj;
            String emailaddress;

            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if(MessageUtils.EMAIL.equals(auxObj.getAttribute("media").getValueString()))
                {
//                    logger.finer("-----------------EMAIL---------------------");
                    emailaddress = auxObj.getAttribute("email").getValueString();
    
                    if ((emailaddress != null) && !emailaddress.equals(""))
                    {
                        if (auxObj.getAttribute("name") != null)
                        {
                            mailmsg.addTO(auxObj.getAttribute("name")
                                                .getValueString(), emailaddress);
                        }
    
                        hasRecipient = true;
                    }
                    else
                    {
                        this.addErrorMessage(ERROR_INV_RECIPIENT_1 +
                            bridge.getObject().getCARDIDwLink() +
                            ERROR_INV_RECIPIENT_2);
    
                        return false;
                    }
                }
            }
//            logger.finer("FINISHED SETTING TO");
            //cc
            bridge = message.getBridge("cc");
            bridge.beforeFirst();
//            logger.finer("SETTING CC");
            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if(MessageUtils.EMAIL.equals(auxObj.getAttribute("media").getValueString()))
                {
                    emailaddress = auxObj.getAttribute("email").getValueString();
    
                    if ((emailaddress != null) && !emailaddress.equals(""))
                    {
                        if (auxObj.getAttribute("name") != null)
                        {
                            mailmsg.addCC(auxObj.getAttribute("name")
                                                .getValueString(), emailaddress);
                        }
    
                        hasRecipient = true;
                    }
                    else
                    {
                        this.addErrorMessage(ERROR_INV_RECIPIENT_1 +
                            bridge.getObject().getCARDIDwLink() +
                            ERROR_INV_RECIPIENT_2);
    
                        return false;
                    }
                }
            }
//            logger.finer("FINISHED SETTING CC");
            //bcc
//            logger.finer("SETTING BCC");
            bridge = message.getBridge("bcc");
            bridge.beforeFirst();

            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if(MessageUtils.EMAIL.equals(auxObj.getAttribute("media").getValueString()))
                {
                    emailaddress = auxObj.getAttribute("email").getValueString();
    
                    if ((emailaddress != null) && !emailaddress.equals(""))
                    {
                        if (auxObj.getAttribute("name") != null)
                        {
                            mailmsg.addBCC(auxObj.getAttribute("name")
                                                 .getValueString(), emailaddress);
                        }
    
                        hasRecipient = true;
                    }
                    else
                    {
                        this.addErrorMessage(ERROR_INV_RECIPIENT_1 +
                            bridge.getObject().getCARDIDwLink() +
                            ERROR_INV_RECIPIENT_2);
    
                        return false;
                    }
                }
            }
//            logger.finer("FINISHED SETTING BCC");
            //tem que ter pelo menos um destinatário
            if (!hasRecipient)
            {
                this.addErrorMessage(ERROR_TO);
//                logger.finer("ERROR NO RECIPIENT");
                return false;
            }

            //subject
//            logger.finer("SETTING SUBJECT");
            String subject = message.getAttribute("name").getValueString();

            //footprint
//            logger.finer("SETTING FOOTPRINT");
            String footprint = boConfig.getMailConfig().getProperty("footprint");

            if (((footprint == null) || footprint.equalsIgnoreCase("true")) &&
                    ((subject == null) ||
                    (subject.toUpperCase().indexOf(" [XEO" + message.getBoui() +
                        ":" + engine.getBoManager().getProgBoui() + "] ") == -1)))
            {
                subject = (subject == null) ? ("[XEO" + message.getBoui() +
                    ":" + engine.getBoManager().getProgBoui() + "] ") : 
                    (subject + " - [XEO" + message.getBoui() + ":" + engine.getBoManager().getProgBoui() + "] ");
            }

            mailmsg.setSubject(subject);

            //Links Relacionados
//            logger.finer("SETTING attachedObjects");
            String links = "";
            String link = "";
//            bridge = message.getBridge("attachedObjects");
//
//            if (bridge.getRowCount() > 0)
//            {
//                links = "<BR><BR>Links Relacionados:<BR>";
//            }

//            bridge.beforeFirst();
//
//            while (bridge.next())
//            {
//                String auxlink = bridge.getObject().getURL().toString();
//
//                if ((auxlink != null) && !auxlink.equals(""))
//                {
//                    link += (auxlink + "<BR>");
//                }
//            }
//
//            links += link;

            // Falta poder utilizar a conta por defeito
            //mailmsg.setBodyHtml(mailobject.getAttribute("description").getValueString()+links+"<BR><BR>"+this.mailaccount.getAttribute("footprint").getValueString());
            String refs = "";

            if ((footprint == null) || footprint.equalsIgnoreCase("true"))
            {
                refs = "<i><hr>Se desejar responder a este mail inclua por favor no texto ou no assunto do email a(s) seguinte(s) referencias: <br/></i>";
                refs += (" [XEO" + message.getBoui() + ":" + engine.getBoManager().getProgBoui() + "] ");

                boObject[] parents = message.getParents();

                for (int i = 0; i < parents.length; i++)
                {
                    refs += (" [XEO" + parents[i].getBoui() + ":" + engine.getBoManager().getProgBoui() + "] ");
                }

                refs += "<p/>";
            }

            mailmsg.setContent(message.getAttribute("description")
                                      .getValueString() /*+ links*/ + refs);

            //Attachs
//            logger.finer("SETTING ATTACH");
            int i = 0;
            bridge = message.getBridge("documents");
            bridge.beforeFirst();
            i = addAsAttach(bridge, mailmsg, filesTodelete, i);
            
            bridge = message.getBridge("binaryDocuments");
            bridge.beforeFirst();
            i = addAsAttach(bridge, mailmsg, filesTodelete, i);

            
            
//          Envio parcial
            if ( message.getAttribute("partialSend") != null && (message.getAttribute("partialSend").getValueString() != null) &&
                    "1".equals(message.getAttribute("partialSend")
                                          .getValueString()))
            {
                mailmsg.setPartialSend(true);
            }
            else
            {
                mailmsg.setPartialSend(false);
            }
            
//            logger.finer("FINISHED SETTING ATTACH");
            this.addMailMessage(mailmsg);

            if ((message.getAttribute("requestDeliveryReceipt").getValueString() != null) &&
                    "1".equals(message.getAttribute("requestDeliveryReceipt")
                                          .getValueString()))
            {
//                logger.finer("SETTING ASK FOR RECEIPT");                
                mailmsg.setAskForDeliveredReceipt(true);
            }

            if ((message.getAttribute("requestReadReceipt").getValueString() != null) &&
                    "1".equals(message.getAttribute("requestReadReceipt")
                                          .getValueString()))
            {
//                logger.finer("SETTING ASK FOR DELIVERY RECEIPT");
                mailmsg.setAskForReadReceipt(true);
            }
//            logger.finer("SENDING-----");
            this.send(saveBinary);
            
            if(saveBinary && this.getSavedEML() != null)
            {
                //binary documents
                try{
                    boObject document = boObject.getBoManager().createObject(ctx,"Ebo_Document");
                    document.getAttribute("file").setValueiFile(new FSiFile(null, this.getSavedEML().getLocation(), this.getSavedEML().getLocation()));
                    if(this.getSavedEML().getName() != null)
                    {
                        document.getAttribute("description").setValueString(this.getSavedEML().getName());
                    }
                    message.getObjectBinary().setBinary(document);
                    if(this.getSavedEML().getDeleteAfterUse())
                    {
                        filesTodelete.add(this.getSavedEML().getLocation());
                    }
                }catch (Exception e){}
            }
//            logger.finer("FINISHED SENDING-----");
            //datas
            try
            {
                if ((mailmsg.getSentDate() != null) &&
                        !"".equals(mailmsg.getSentDate()))
                {
                    message.getAttribute("dtdoc").setValueDate(mailmsg.getSentDate());
                }
            }
             catch (Exception e)
            {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DOING_THE_EMAIL_DATESET"), e);
            }

            //to
//            logger.finer("TRETING TO ");
            bridge = message.getBridge("to");
            bridge.beforeFirst();
            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if("E-Mail".equals(auxObj.getAttribute("media").getValueString()))
                {
                    auxObj.getAttribute("already_send").setValueString("1");                    
                }
            }

            //cc
//            logger.finer("TREATING CC ");
            bridge = message.getBridge("cc");
            bridge.beforeFirst();
            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if("E-Mail".equals(auxObj.getAttribute("media").getValueString()))
                {
                    auxObj.getAttribute("already_send").setValueString("1");                    
                }
            }

            //bcc
//            logger.finer("TREATING BCC ");
            bridge = message.getBridge("bcc");
            bridge.beforeFirst();
            while (bridge.next())
            {
                auxObj = bridge.getObject();
                if("E-Mail".equals(auxObj.getAttribute("media").getValueString()))
                {
                    auxObj.getAttribute("already_send").setValueString("1");                    
                }
            }
//            logger.finer("TRETING XEOID ");
            message.getAttribute("XEOID").setValueString(XEOIDUtil.MessageXEOID(mailmsg));
            long receivers[] = MessageUtils.getToReceivers(message);
            if(MessageUtils.isToWaitResponse(message) && receivers != null && receivers.length > 0)
            {
                logger.finer(LoggerMessageLocalizer.getMessage("GOING_TO_CREATE_WAIT_FOR_MSG"));
                message.getEboContext().getBoSession().setProperty("creatingWaitMsg", Boolean.TRUE);
                try
                {
                    boObject program = engine.getProgramRuntime();
                    xwfManager man = new xwfManager(ctx, program);
                    xwfMessage.createWaitActv(man, receivers, message, program.getBoui(), 
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
            else
            {
                message.update();
            }

            return true;
        }
         catch (iFilePermissionDenied e)
        {
            this.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_ATTACHING_FILES_TO_THE_EMAIL") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return false;
        }
         catch (boRuntimeException e)
        {
            this.addErrorMessage(
                MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return false;
        }

        //      catch(AddressException e)
        //      {
        //      
        //       this.addErrorMessage("Endereço inválido"+
        //       "<span style='display:none'>" + e.getMessage() +
        //        "</span>"
        //        );
        //              logger.warn("Error: ", e);
        //       return false; 
        //      }
         catch (MessagingException e)
        {
            this.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL")+
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return false;
        }
         catch (IOException e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            this.addErrorMessage(MessageLocalizer.getMessage("UNABLE_TO_SEND_ATTACH") +
                "<span style='display:none'>" + e.getMessage() + "</span>");

            return false;
        }
         catch (Exception e)
        {
            this.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL")+
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return false;
        }
        finally
        {
            try{deleteFiles(filesTodelete);}catch(Exception e){/*ignore*/}
        }

        /*    }
            else
            {
        //      mailobject.addErrorMessage("O email não pode ser enviado, pois não se encontra marcado para tal.");
              return false;
            } */
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
    private ArrayList answerAs(boObject performer)
        throws boRuntimeException
    {
        bridgeHandler bh;
        ArrayList r = new ArrayList(); 
        r.add(String.valueOf(performer.getBoui()));
        boBridgeIterator it;

        if (performer.getAttribute("queues") != null)
        {
            bh = performer.getBridge("queues");
            it = bh.iterator();
            it.beforeFirst();
            while(it.next())
            {
                if (!r.contains(String.valueOf(it.currentRow().getObject().getBoui())))
                {
                    r.add(String.valueOf(it.currentRow().getObject().getBoui()));
                }
            }
        }

        if (performer.getAttribute("roles") != null)
        {
            bh = performer.getBridge("roles");
            it = bh.iterator();
            it.beforeFirst();
            while(it.next())
            {
                if (!r.contains(String.valueOf(it.currentRow().getObject().getBoui())))
                {
                    r.add(String.valueOf(it.currentRow().getObject().getBoui()));
                }
            }
        }

        if (performer.getAttribute("groups") != null)
        {
            bh = performer.getBridge("groups");
            it = bh.iterator();
            it.beforeFirst();
            while(it.next())
            {
                if (!r.contains(String.valueOf(it.currentRow().getObject().getBoui())))
                {
                    r.add(String.valueOf(it.currentRow().getObject().getBoui()));
                }
            }
        }

        return r;
    }

    public boolean sendReceipt(boObject message)
    {
        //criar um programa
        try
        {
            boObject program = null;
            boObject performer = boObject.getBoManager().loadObject(message.getEboContext(), 
                message.getEboContext().getBoSession().getPerformerBoui());
            sendReceipt(message, performer);
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return false;
    }

//    public boolean sendReceipt(Object context, boObject message)
//    {
//        return false;
//    }

    public boObject sendReceipt(boObject message, boObject performer)
    {
        try
        {
            EboContext ctx = message.getEboContext();
//            ArrayList aswerAs = answerAs(performer);

            //Para
            boObject toObject = message.getAttribute("replyTo").getObject();

            if (toObject == null)
            {
                toObject = message.getAttribute("from").getObject();

                if (toObject == null)
                {
                    this.addErrorMessage(ERROR_FROM_RECEIPT);

                    return null;
                }
            }

            //De
            String address = null;
            if (performer != null)
            {
                address = performer.getAttribute("email").getValueString();
            }
            else
            {
                this.addErrorMessage(ERROR_TO);
                return null;
            }

            if ((address == null) || address.equals(""))
            {
                this.addErrorMessage(ERROR_TO);

                return null;
            }

            String smtphost = boConfig.getMailConfig().getProperty("smtphost");

            if ((smtphost != null) && !smtphost.equals(""))
            {
                setSMTPHost(smtphost);
            }
            else
            {
                this.addErrorMessage(ERROR_SMTP);

                return null;
            }

            MailMessage mailmsg = MailMessage.getNewReadReceiptMailMessageToSent();

            if ((smtphost != null) && !smtphost.equals(""))
            {
                this.setSMTPHost(smtphost);
            }
            else
            {
                this.addErrorMessage(ERROR_SMTP);

                return null;
            }

            //prioridade
            mailmsg.setPriority(Mail.NORMAL);

            address = toObject.getAttribute("email").getValueString();

            if ((address != null) && !address.equals(""))
            {
                String lastName = toObject.getAttribute("lastname")
                                          .getValueString();
                String name = toObject.getAttribute("name").getValueString();
                String aux = (((lastName == null) ? "" : (lastName + ", ")) +
                    ((name == null) ? "" : name)).trim();

                if (aux.length() == 0)
                {
                    mailmsg.addTO(new MailAddress(address));
                }
                else
                {
                    mailmsg.addTO(new MailAddress(name, address));
                }
            }
            else
            {
                this.addErrorMessage(ERROR_FROM);

                return null;
            }
            
            if ((address != null) && !address.equals(""))
            {
                String lastName = (performer.getAttribute("lastname") != null)
                    ? performer.getAttribute("lastname").getValueString() : null;
                String name = performer.getAttribute("name").getValueString();
                String aux = (((lastName == null) ? "" : (lastName + ", ")) +
                    ((name == null) ? "" : name)).trim();

                if (aux.length() == 0)
                {
                    mailmsg.setFrom(new MailAddress(address));
                }
                else
                {
                    mailmsg.setFrom(new MailAddress(name, address));
                }
            }
            else
            {
                this.addErrorMessage(ERROR_TO_RECEIPT);
                return null;
            }

            //subject
            String footprint = boConfig.getMailConfig().getProperty("footprint");
            String subject = null;

            try
            {
                subject = "LIDO: " +
                    message.getAttribute("name").getValueString();
            }
             catch (boRuntimeException e)
            {
                subject = "LIDO: ";
            }

            //conteúdo
            StringBuffer msg = new StringBuffer();
            msg.append("A sua mensagem\n\tPara: ");

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

            msg.append("\n\tAssunto: ");

            try
            {
                msg.append(message.getAttribute("name").getValueString());
            }
             catch (boRuntimeException e)
            {
                //ignore
            }

            msg.append("\nfoi lida na data: ");

            Date readDate = new Date();
            SimpleDateFormat df = new SimpleDateFormat(
                    "EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");
            msg.append(df.format(readDate)).append(".");

            mailmsg.setContent(msg.toString());
            mailmsg.setSubject(subject);
            this.addMailMessage(mailmsg);
            this.send();

            ArrayList r = MessageUtils.getDeliveryMessageOnlyfromObject(message, performer);
            for (int i = 0; i < r.size(); i++) 
            {
                if(((boObject)r.get(i)).getAttribute("send_date_read_receipt") != null &&
                    ((boObject)r.get(i)).getAttribute("send_date_read_receipt").getValueDate() == null)
                {
                    ((boObject)r.get(i)).getAttribute("send_date_read_receipt").setValueDate(readDate);
                }
                
                if(((boObject)r.get(i)).getAttribute("date_sent_read_receipt") != null &&
                    ((boObject)r.get(i)).getAttribute("date_sent_read_receipt").getValueDate() == null)
                {
                    ((boObject)r.get(i)).getAttribute("date_sent_read_receipt").setValueDate(readDate);
                }
            }

            message.update();

            return null;
        }
         catch (boRuntimeException e)
        {
            this.addErrorMessage(
            		MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return null;
        }
         catch (AddressException e)
        {
            this.addErrorMessage(MessageLocalizer.getMessage("INVALID_ADDRESS") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return null;
        }
         catch (MessagingException e)
        {
            this.addErrorMessage(
            		MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);

            return null;
        }
         catch (IOException e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            this.addErrorMessage(MessageLocalizer.getMessage("UNABLE_TO_SEND_ATTACH") +
                "<span style='display:none'>" + e.getMessage() + "</span>");

            return null;
        }
         catch (Exception e)
        {
            this.addErrorMessage(
            		MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_THE_EMAIL") +
                "<span style='display:none'>" + e.getMessage() + "</span>");
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+"r: ", e);

            return null;
        }
    }
    private void addErrorMessage(String error)
    {
        if(this.engine != null)
        {
            engine.addErrorMessage(error);
        }
    }
    private  static boObject createMessageReceiveProgram(EboContext ctx, boObject message) throws boRuntimeException
   {
        boObject program = boObject.getBoManager().createObject(ctx, "xwfProgramRuntime");        
        String label = message.getAttribute("name").getValueString();
        if(label != null && label.length() >= 200)
        {
            label = label.substring(0, 190) + "(...)"; 
        }
        program.getAttribute("label").setValueString(label, AttributeHandler.INPUT_FROM_INTERNAL);        
        if(message.getAttribute("SYS_DTCREATE").getValueDate() != null)
        {
            program.getAttribute("beginDate").setValueDate(message.getAttribute("SYS_DTCREATE").getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        else
        {
            program.getAttribute("beginDate").setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }        
        boObject activity = null;
        xwfManager man = new xwfManager(ctx, program);
        xwfMessage.receiveMessage(man, message, program);
        return program;
   }
   
   private int addAsAttach(bridgeHandler bridge, MailMessage mailmsg, ArrayList filesTodelete, int i) 
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
                in = new BufferedInputStream(fich.getInputStream());
                
                filename = fich.getName();
                suffix = MailUtil.getExtension(filename);
                prefix = MailUtil.getFirstPart(filename);
                
                tempFile = MailUtil.createTempFile(prefix, suffix);
                out = new FileOutputStream(tempFile);
                try
                {
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
                mailmsg.addAttach(null, tempFile.getAbsolutePath(), new Integer(i).toString(), true);
                filesTodelete.add(tempFile.getAbsolutePath());
            }
        }
        return i;
    }

    public boolean releaseMsg(boObject message, boObject performer) throws boRuntimeException
    {
        return false;
    }
}
