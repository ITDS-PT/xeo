/*Enconding=UTF-8*/
package netgest.bo.message.server.mail;

import java.io.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import java.rmi.RemoteException;

import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import netgest.bo.message.utils.Attach;

import org.apache.log4j.Logger;
import org.bouncycastle.mail.smime.SMIMESigned;


/**
 * This class allows to send a receive emails, using several IMAP,POP3 and SMTP Servers
 * the structure of the email message must be compliant to the NETGEST table ND_MOV, this
 * table stores all the email messages, the end user dont interact with an email server
 * directly, instead he interacts with the database to see and send emails. This class his responsible to put emails in the
 * database, and to send emails to the outside world using and SMTP.
 *
 * @version 1.0
 * @author       Enlace3, Lda
 *
 */
public class Mail
{
    public static final int ALL_MESSAGES_ONSERVER = 1;
    public static final int UNREAD_MESSAGES = 2;
    public static final int NEW_MESSAGES = 3;
    public static final int ORDER_ASC = 1;
    public static final int ORDER_DESC = 2;

    //X-Priority level's
    public static final int HIGHEST = 1;
    public static final int HIGH = 2;
    public static final int NORMAL = 3;
    public static final int LOW = 4;
    public static final int LOWEST = 5;

    //priority level - rfc2156
    public static final String PR_HIGH = "urgent";
    public static final String PR_NORMAL = "normal";
    public static final String PR_LOW = "non-urgent";

    //importance level - rfc2156
    public static final String IMP_HIGH = "high";
    public static final String IMP_NORMAL = "normal";
    public static final String IMP_LOW = "low";
    private static final int RETRY_TIMES = 5;

    //Messages types
    public static final byte MESSAGE = 0;
    public static final byte READ_RECEIPT = 1;
    public static final byte DELIVERED_RECEIPT = 2;
    
    //saved EML
    private Attach savedEML = null;

    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.server.mail.Mail");

    /**
     * Class that represents and email message internally
     * in this Class, i.e a mail retrieved from a POP Server with JavaMail is parsed
     * to fit this structure and then used internally in this class.
     */
    /**
     * Auxilary vector used to store the id's of the images that are inline in the email
     */
    private Vector clsid = new Vector();
    private String p_SMTPHost = null;
    private String p_POPHost = null;
    private String p_UserName = null;
    private String p_UserPass = null;
    private boolean p_onlyXEOMessage;
    private Boolean checkAddress = Boolean.FALSE;
    private boolean deleteMessages = false;
    private Vector mailmessages = new Vector();
    private Vector errors = new Vector();
    private String protocol = "pop3";
    private int whatMessages = 1;
    private int messageOrder = 2;
    private String sendMode = "text/html";

    /**
     * Constructor of the class
     */
    public Mail()
    {
    }

    public boolean getDeleteMessages()
    {
        return this.deleteMessages;
    }

    public Vector getMailMessages()
    {
        return this.mailmessages;
    }
    
    public Vector getErrorMessages()
    {
        return this.errors;
    }

    public void setMailMessages(Vector mailmessages)
    {
        this.mailmessages = mailmessages;
    }

    public void addMailMessage(MailMessage msg)
    {
        this.mailmessages.add(msg);
    }

    public String getSMTPHost()
    {
        return p_SMTPHost;
    }

    public void setCheckAddress(boolean checkAddress)
    {
        if(checkAddress)
        {
            this.checkAddress = Boolean.TRUE;
        }
        else
        {
            this.checkAddress = Boolean.FALSE;
        }
        
    }

    public boolean getCheckAddress()
    {
        if(this.checkAddress == null || !this.checkAddress.booleanValue())
        {
            return false;
        }
        return true;
    }

    public void setSMTPHost(String p_SMTPHost)
    {
        this.p_SMTPHost = p_SMTPHost;
    }

    public String getPOPHost()
    {
        return p_POPHost;
    }
    
    public boolean onlyXEOMessage()
    {
        return p_onlyXEOMessage;
    }

    public void setOnlyXEOMessage(boolean value)
    {
        p_onlyXEOMessage = value;
    }
    public void setDeleteMessages(boolean deleteMessages)
    {
        this.deleteMessages = deleteMessages;
    }

    public void setPOPHost(String p_POPHost)
    {
        this.p_POPHost = p_POPHost;
    }

    public String getUserName()
    {
        return p_UserName;
    }

    public void setUserName(String p_UserName)
    {
        this.p_UserName = p_UserName;
    }

    public String getUserPass()
    {
        return p_UserPass;
    }

    public void setUserPass(String p_UserPass)
    {
        this.p_UserPass = p_UserPass;
    }

    public String getProtocol()
    {
        return protocol;
    }

    public void setProtocol(String newProtocol)
    {
        protocol = newProtocol;
    }

    public int getWhatMessages()
    {
        return whatMessages;
    }

    public void setwhatMessages(int newwhatMessages)
    {
        whatMessages = newwhatMessages;
    }

    public int getMessageOrder()
    {
        return messageOrder;
    }

    public void setMessageOrder(int newMessageOrder)
    {
        messageOrder = newMessageOrder;
    }

    /**
     * This method reads all emails for an user from a POP3/IMAP Server,
     * transforms the emails in XML and saves it in the DataBase
     *@return <b>True</b> - if successfull
     *        <b>False</b> - if not Successfull
     */
    public MailMessage[] readMail()
        throws RemoteException, MessagingException, IOException
    {
        return this.readMail(-1, null, false);
    }

    public MailMessage[] readMail(int numOfMessagesToRead, String foldername )
    throws RemoteException, MessagingException, IOException
    {
    	return readMail( numOfMessagesToRead, foldername, false );
    }
    public MailMessage[] readMail(int numOfMessagesToRead, String foldername, boolean faxAccount)
        throws RemoteException, MessagingException, IOException
    {
        Folder folder = null;
        Session session = null;
        Store store = null;
        MailMessage[] messages = null;

        try
        {
            Properties props = new Properties();

            session = Session.getInstance(props, null);
            store = session.getStore(this.protocol);
            store.connect(this.p_POPHost, this.p_UserName, this.p_UserPass);
            
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
            int messageCount = 0;

            if (this.whatMessages == this.ALL_MESSAGES_ONSERVER)
            {
                messageCount = folder.getMessageCount();
            }

            if (this.whatMessages == this.UNREAD_MESSAGES)
            {
                messageCount = folder.getUnreadMessageCount();
            }

            if (this.whatMessages == this.NEW_MESSAGES)
            {
                messageCount = folder.getNewMessageCount();
            }

            if (this.messageOrder == this.ORDER_DESC)
            {
                if (numOfMessagesToRead != -1)
                {
                    messages = new MailMessage[numOfMessagesToRead];
                }
                else
                {
                    messages = new MailMessage[messageCount];
                    numOfMessagesToRead = messageCount;
                }

                int j = 0;

                for (int i = messageCount;
                        i > (messageCount - numOfMessagesToRead); i--)
                {
                    j++;
                    message = (MimeMessage) folder.getMessage(i);
                    mailmsg = setMessage(session, message, faxAccount);
                    messages[j - 1] = mailmsg;
                }
            }
            else
            {
                if (numOfMessagesToRead != -1)
                {
                    messageCount = numOfMessagesToRead;
                }

                messages = new MailMessage[messageCount];

                for (int i = 1; i <= messageCount; i++)
                {
                    message = (MimeMessage) folder.getMessage(i);
                    mailmsg = setMessage(session, message, faxAccount);
                    messages[i - 1] = mailmsg;
                }
            }

            //fecha o folder e apaga as msg
            // folder.expunge();
            folder.close(true);

            return messages;
        }
         catch (Exception e)
        {
            //fecha o folder mas nao apaga as msg
            logger.error("", e);

            //   e.printStackTrace();
            if (folder != null)
            {
                folder.close(false);
            }

            return messages;
        }
         finally
        {
            store.close();
        }
    }

    public MailMessage setMessage(Session session, MimeMessage message, boolean faxAccount)
        throws Exception
    {
        MailMessage mailmsg = null;

        //get the correct type of message
        byte type = MailUtil.getMessageType(message);

        switch (type)
        {
        case MESSAGE:
            mailmsg = MailMessage.getNewReceiveMailMessage();

            break;

        case READ_RECEIPT:
            mailmsg = MailMessage.getNewReceiveReadReceiptMailMessage();

            break;

        case DELIVERED_RECEIPT:
            mailmsg = MailMessage.getNewReceiveDeliveredReceiptMailMessage();

            break;

        default:
            mailmsg = MailMessage.getNewReceiveMailMessage();

            break;
        }

        //get message subject
        mailmsg.setSubject((message.getSubject() == null) ? null
                                                          : MailUtil.convertString(
                message.getSubject()));

        //get the priority
        mailmsg.setPriority(MailUtil.getPriority(message));

        //is to return read receipt
        if (MailUtil.isToReturnReceipt(message))
        {
            mailmsg.setReturnReadReceipt(true);
        }

        //get thread-topic
        mailmsg.setContextThread(MailUtil.getContextThread(message));

        //get from
        if(faxAccount)
            mailmsg.setFrom(MailUtil.getFromFax(message));
        else
            mailmsg.setFrom(MailUtil.getFrom(message));

        //get messageid
        mailmsg.setMessageID(message.getMessageID());

        //get received date
        mailmsg.setReceivedDate((message.getReceivedDate() != null)
            ? message.getReceivedDate() : new Date());

        //get sent date
        mailmsg.setSentDate((message.getSentDate() != null)
            ? message.getSentDate() : new Date());

        //get to recipients
        mailmsg.setTO(MailUtil.getAddress(Message.RecipientType.TO, message));

        //get cc recipients
        mailmsg.setCC(MailUtil.getAddress(Message.RecipientType.CC, message));
        
        //replyTo
        mailmsg.setReplyTo(MailUtil.getReplyTo(message));

        //after the header the content
        Object content = MailUtil.getMailContent(message);

        if (content instanceof Multipart)
        {            
            if (message.isMimeType("application/pkcs7-mime"))
            {
//                logger.info("Message application/pkcs7-mime");
                SMIMESigned signedMsg = new SMIMESigned(message);
                MailUtil.processMultipart(signedMsg, mailmsg);
            }
            else
            {
                if (message.isMimeType("multipart/signed"))
                {
//                  logger.info("Message multipart/signed");
                    mailmsg.setSignedMsg(true);
                }
                try
                {
//                    logger.info("Message multipart");
                    MailUtil.processMultipart((Multipart) content, mailmsg);
                }
                catch (Exception e)
                {
                    if(e != null && 
                        e.getMessage() != null && e.getMessage().toUpperCase().indexOf("MISSING START BOUNDARY") >=0)
                    {
//                        logger.info("CORRECTING START BOUNDARY");
                        MimeMessage m = specialCases(session, message);
                        if(m != null)
                        {
//                            logger.info("START BOUNDARY CORRECTED");
                            try
                            {
                                MailUtil.processMultipart((Multipart) MailUtil.getMailContent(m), mailmsg);
                            }
                            catch (Exception _e)
                            {
                                writeToLog(message, e);
                                mailmsg.setReadError(true);
                
                                if (!MailUtil.getContentAfterError((Multipart) content, mailmsg))
                                {
//                                    MailUtil.getContentAfterError(message, mailmsg);
                                }
                            }
                        }
                        else
                        {
                            writeToLog(message, e);
                            mailmsg.setReadError(true);
            
                            if (!MailUtil.getContentAfterError((Multipart) content, mailmsg))
                            {
//                                MailUtil.getContentAfterError(message, mailmsg);
                            }
                        }
                    }
                    else
                    {
                        writeToLog(message, e);
                        mailmsg.setReadError(true);
        
                        if (!MailUtil.getContentAfterError((Multipart) content, mailmsg))
                        {
//                            MailUtil.getContentAfterError(message, mailmsg);
                        }
                    }
                }
            }            
        }
        else
        {
//            logger.info("Message processPart");
            MailUtil.processPart(message, mailmsg);
        }
        
        //binary documents
        try{mailmsg.setBinaryMail(MailUtil.getBinaryMail("email.eml", "0", message));}catch (Exception e){}
        

//        if (deleteMessages)
//        {
//            message.setFlag(Flags.Flag.DELETED, true);
//        }

        return mailmsg;
    }
    
    private static void writeToLog(MimeMessage msg, Exception e)
    {
        ByteArrayOutputStream bao = null;
        try
        {            
            bao = new ByteArrayOutputStream();
            msg.writeTo(bao);
            logger.error("Error reading email2:");
            logger.error(e);
        }
        catch(Exception _e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if (bao != null)
                {
                    bao.close();
                }
            }
             catch (Exception _e)
            {
            }
        }
    }
    
    public void send() throws AddressException, MessagingException, IOException
    {
        send(false);
    }

    public void setSendMode(String mode)
    {
        sendMode = mode;
    }
    public void send(boolean saveEML) throws AddressException, MessagingException, IOException
    {
        Session session = null;
        MailMessage mailmsg = null;
        ArrayList files = null;
        Properties props = null;
        MimeMessage msg = null;
        InternetAddress addressFrom = null;
        InternetAddress[] addressAux = null;
        Date d = null;
        boolean sendingPartial = false;

        for (int k = 0; k < this.mailmessages.size(); k++)
        {
            mailmsg = (MailMessage) this.mailmessages.get(k);
            files = new ArrayList();

            props = new Properties();
            props.put("mail.smtp.host", this.p_SMTPHost);

            if (mailmsg.partialSend() || checkAddress.booleanValue()) 
            {
                props.put("mail.smtp.sendpartial", "true");
                sendingPartial = true;
            }
            else
            {
                props.put("mail.smtp.sendpartial", "false");
                sendingPartial = false;
            }

            session = Session.getInstance(props, null);
            session.setDebug(false);

            msg = new MimeMessage(session);

            //de
            addressFrom = new InternetAddress(mailmsg.getFrom().getFullAddress());

            InternetAddress receipts = new InternetAddress(
            		mailmsg.getReadReceiptEmail()==null?mailmsg.getFrom().getFullAddress():mailmsg.getReadReceiptEmail()
            	); 
            
            if (mailmsg.askForDeliverReceipt())
            {
                //        logger.warn("ask for deliver receipt");
                MailUtil.setAskReceipt(true, props, msg, receipts );
            }

            if (mailmsg.askForReadReceipt())
            {
                //        logger.warn("ask for read receipt");
                MailUtil.setAskReceipt(false, props, msg, 
                		receipts );
            }

            //Prioridade
            MailUtil.setPriority(mailmsg.getPriority(), msg);

            msg.setFrom(addressFrom);

            //Subject da msg
            msg.setSubject(mailmsg.getSubject());

            //Return Receipt SIM/NAO
            //msg.reply(mailmsg.getReturnReceipt());
            //destinatarios
            addressAux = new InternetAddress[mailmsg.getTO().length];

            MailAddress auxaddress = null;
            for (int i = 0; i < mailmsg.getTO().length; i++)
            {
                auxaddress = (MailAddress) mailmsg.getTO()[i];
                if(auxaddress.getName() != null && auxaddress.getName().length() > 0)
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail(), auxaddress.getName());
                }
                else
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail());
                }
            }

            msg.setRecipients(Message.RecipientType.TO, addressAux);

            //destinatarios CC
            addressAux = new InternetAddress[mailmsg.getCC().length];

            for (int i = 0; i < mailmsg.getCC().length; i++)
            {
                auxaddress = (MailAddress) mailmsg.getCC()[i];
                if(auxaddress.getName() != null && auxaddress.getName().length() > 0)
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail(), auxaddress.getName());
                }
                else
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail());
                }
            }

            msg.setRecipients(Message.RecipientType.CC, addressAux);

            //destinatarios BCC
            addressAux = new InternetAddress[mailmsg.getBCC().length];

            for (int i = 0; i < mailmsg.getBCC().length; i++)
            {
                auxaddress = (MailAddress) mailmsg.getBCC()[i];
                if(auxaddress.getName() != null && auxaddress.getName().length() > 0)
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail(), auxaddress.getName());
                }
                else
                {
                    addressAux[i] = new InternetAddress(auxaddress.getEmail());
                }
            }

            msg.setRecipients(Message.RecipientType.BCC, addressAux);

            //Body da msg
            BodyPart messageBodyPart = new MimeBodyPart();

            if (mailmsg.getContent() != null)
            {
                messageBodyPart.setContent(mailmsg.getContent(), sendMode);
            }

            Multipart multipart = null;

            if (mailmsg.isReadReceipt())
            {
                multipart = new MimeMultipart(
                        "report;report-type=disposition-notification");
            }
            else
            {
                multipart = new MimeMultipart();
            }

            multipart.addBodyPart(messageBodyPart);

            // Attachments
            for (int i = 0; i < mailmsg.getAttach().length; i++)
            {
                messageBodyPart = new MimeBodyPart();

                File tmpfile = null;
                Attach auxAttach = (Attach) mailmsg.getAttach()[i];

                tmpfile = new File(auxAttach.getLocation());

                DataSource source = new FileDataSource(tmpfile);
                messageBodyPart.setDataHandler(new DataHandler(source));
                messageBodyPart.setFileName(auxAttach.getName());
                multipart.addBodyPart(messageBodyPart);

                if (auxAttach.getDeleteAfterUse())
                {
                    files.add(tmpfile);
                }
            }

            // Put parts in message
            msg.setContent(multipart);

            // Envia Msg
            int atemps = 0;
            do 
            {
            	atemps++;
                try
                {
                    Transport.send(msg);
                    errors.add(k, "");
                    break;
                }
                catch (SendFailedException e)
                {
                    //se não foi enviado nenhum e-mail então volta a tentar
                    if(e.getValidSentAddresses() == null || e.getValidSentAddresses().length == 0)
                    {
                        if (atemps > RETRY_TIMES)
                        {
                            throw e;
                        }
                    }
                    else
                    {
                        treatSendPartialError(k, e);
                        break;
                    }
                }
                catch (MessagingException _e)
                {
                    if (atemps > RETRY_TIMES)
                    {
                        throw _e;
                    }
                }
            }
            while( ( atemps <= RETRY_TIMES ) );
            	
            if(saveEML)
            {
                savedEML = MailUtil.getBinaryMail("email.eml", "0", msg);
            }

            d = new Date();

            try
            {
                if (msg.getSentDate() != null)
                {
                    d = msg.getSentDate();
                }
            }
             catch (Exception e)
            {
                /*ignore*/
            }

            mailmsg.setSentDate(d);

            //delete the used files
            for (int i = 0; i < files.size(); i++)
            {
                File auxf = (File) files.get(i);
                auxf.delete();
            }
        }

        if (session != null)
        {
            session = null;
        }
    }
    
    public Attach getSavedEML()
    {
        return savedEML;
    }
    
    private void treatSendPartialError(int k, SendFailedException e)
    {
        StringBuffer sb = new StringBuffer("Aconteceu um erro no envio deste E-Mail[");
        sb.append(e.getMessage()).append("]. ");
        Address [] add = e.getInvalidAddresses();
        for (int i = 0; add != null && i < add.length; i++) 
        {
            if(i == 0)
            {
                sb.append("E-Mails inválidos[");
            }
            sb.append(add[i].toString()).append("; ");
            if((i+1) == add.length)
            {
                sb.append("]. ");
            }
        }

        add = e.getValidUnsentAddresses();
        for (int i = 0; add != null && i < add.length; i++) 
        {
            if(i == 0)
            {
                sb.append("E-Mails válidos não enviados[");
            }
            sb.append(add[i].toString()).append("; ");
            if((i+1) == add.length)
            {
                sb.append("]. ");
            }
        }

        add = e.getValidUnsentAddresses();
        for (int i = 0; add != null && i < add.length; i++) 
        {
            if(i == 0)
            {
                sb.append("E-Mails enviados[");
            }
            sb.append(add[i].toString()).append("; ");
            if((i+1) == add.length)
            {
                sb.append("]. ");
            }
        }
        errors.add(k, sb.toString());
    }
    
    private static MimeMessage specialCases(Session s, MimeMessage msg)
    {
        MimeMessage m = rearrangeStartBoundary(s, msg);
        m = rearrangePtVirgula(s, m == null ? msg:m);        
        return m;
    }
    
    private static MimeMessage rearrangeStartBoundary(Session s, MimeMessage msg)
    {
        ByteArrayOutputStream bao = null;
        try
        {
            bao = new ByteArrayOutputStream();
            msg.writeTo(bao);
            String strMsg = bao.toString();
            int posBound = strMsg.toUpperCase().indexOf("BOUNDARY");
            if(posBound == -1) return null;
            int startSub = getBoundaryStart(strMsg, posBound);
            if(startSub == -1) return null;
            int endSub = getBoundaryEnd(strMsg, posBound);
            if(endSub == -1) return null;
            
            int posWrightBound = strMsg.indexOf("------=", endSub);
            if(startSub == -1) return null;
            int startWrightSub = posWrightBound + "------=".length();
            int endWrightSub = getFirstPartEnd(strMsg, startWrightSub);
            if(endWrightSub == -1) return null;
            
            String wrightMsg = strMsg.substring(0, startSub) + strMsg.substring(startWrightSub, endWrightSub) + strMsg.substring(endSub); 
            ByteArrayInputStream bais = new ByteArrayInputStream(wrightMsg.getBytes());
            return new MimeMessage(s, bais);
        }
        catch(Exception _e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if (bao != null)
                {
                    bao.close();
                }
            }
             catch (Exception _e)
            {
            }
        }
        return null;
    }
    private static MimeMessage rearrangePtVirgula(Session s, MimeMessage msg)
    {
        ByteArrayOutputStream bao = null;
        try
        {
            bao = new ByteArrayOutputStream();
            msg.writeTo(bao);
            String strMsg = bao.toString();
            int posBound = strMsg.toUpperCase().indexOf("BOUNDARY");
            int posAspas1 = strMsg.toUpperCase().indexOf("\"", posBound);
            int posAspas2 = strMsg.toUpperCase().indexOf("\"", posAspas1 + 1);
            if(strMsg.charAt(posAspas2 + 1) == ';')
            {
                String wrightMsg = strMsg.substring(0, posAspas2 + 1) + strMsg.substring(posAspas2 + 2);
                ByteArrayInputStream bais = new ByteArrayInputStream(wrightMsg.getBytes());
                return new MimeMessage(s, bais);
            }
            return msg;
        }
        catch(Exception _e)
        {
            //ignore
        }
        finally
        {
            try
            {
                if (bao != null)
                {
                    bao.close();
                }
            }
             catch (Exception _e)
            {
            }
        }
        return msg;
    }
    
    private static int getFirstPartEnd(String msg, int from)
    {
        for (int i = from; i < msg.length(); i++) 
        {
            if(!(msg.charAt(i)!=' ' && msg.charAt(i)!='\n' && msg.charAt(i)!='\r' && msg.charAt(i)!='\t'))
            {
                return i;
            }
        }
        return -1;
    }
    private static int getBoundaryEnd(String msg, int from)
    {

        String toRet = null;
        boolean startFindEqual = false;
        boolean startFindWord = false;
        boolean foundWord = false;
        boolean startedWAspas = false;
        for (int i = from; !foundWord && i < msg.length(); i++) 
        {
            if(!startFindEqual && msg.charAt(i)=='=')
            {
                startFindEqual = true;
            }
            else if(startFindEqual && msg.charAt(i)=='=')
            {
                startFindWord = true;
            }
            else if(!startFindWord && toRet == null && msg.charAt(i)=='"')
            {
                startedWAspas = true;
            }
            else if(startFindWord)
            {
                if(startedWAspas)
                {
                    if(msg.charAt(i)=='"')
                    {
                        return i; 
                    }
                }
                else
                {
                    if(!(msg.charAt(i)!=' ' && msg.charAt(i)!='\n' && msg.charAt(i)!='\r' && msg.charAt(i)!='\t'))
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    private static int getBoundaryStart(String msg, int from)
    {

        String toRet = null;
        boolean startFindEqual = false;
        boolean startFindWord = false;
        boolean foundWord = false;
        boolean startedWAspas = false;
        for (int i = from; !foundWord && i < msg.length(); i++) 
        {
            if(!startFindEqual && msg.charAt(i)=='=')
            {
                startFindEqual = true;
            }
            else if(startFindEqual && msg.charAt(i)=='=')
            {
                startFindWord = true;
            }
            else if(!startFindWord && toRet == null && msg.charAt(i)=='"')
            {
                startedWAspas = true;
            }
            else if(startFindWord)
            {
                if(startedWAspas)
                {
                    if(msg.charAt(i)!='"')
                    {
                        return i; 
                    }
                }
                else
                {
                    if(msg.charAt(i)!=' ' && msg.charAt(i)!='\n' && msg.charAt(i)!='\r' && msg.charAt(i)!='\t')
                    {
                        return i;
                    }
                }
            }
        }
        return -1;
    }
    
    public void clear()
    {
        clsid.clear();
        mailmessages.clear();
        sendMode = "text/html";
    }
}
