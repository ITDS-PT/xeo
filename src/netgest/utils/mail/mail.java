/*Enconding=UTF-8*/
package netgest.utils.mail;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.OutputStream;
import java.rmi.RemoteException;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.*;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;
import org.apache.log4j.Logger;
import netgest.utils.ConvertUTF7;

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

public class mail {

  /**
   * Class that represents and email message internally
   * in this Class, i.e a mail retrieved from a POP Server with JavaMail is parsed
   * to fit this structure and then used internally in this class.
   */

  /**
   * Auxilary vector used to store the id's of the images that are inline in the email
   */
  private Vector clsid=new Vector();

  private String p_SMTPHost=null,
                 p_POPHost=null,
                 p_UserName=null,
                 p_UserPass=null;
  private boolean checkAddress;
  private boolean deleteMessages=false;
  private Vector mailmessages=new Vector();
  private String protocol="pop3";
  private int WhatMessages=1;
  public static int ALL_MESSAGES_ONSERVER=1;
  public static int UNREAD_MESSAGES=2;
  public static int NEW_MESSAGES=3;
  public static int ORDER_ASC=1;
  public static int ORDER_DESC=2;

  //X-Priority level's
  public static int HIGHEST=1;
  public static int HIGH=2;
  public static int NORMAL=3;
  public static int LOW=4;
  public static int LOWEST=5;

  //priority level - rfc2156
  public static String PR_HIGH="urgent";
  public static String PR_NORMAL="normal";
  public static String PR_LOW="non-urgent";

  //importance level - rfc2156
  public static String IMP_HIGH="high";
  public static String IMP_NORMAL="normal";
  public static String IMP_LOW="low";

  private int messageOrder=2;
  private static final int RETRY_TIMES = 5;

    //logger
    private static Logger logger = Logger.getLogger("netgest.utils.mail.mail");

  /**
   * Constructor of the class
   */

  public mail() {
  }

  public boolean getDeleteMessages()
  {
    return this.deleteMessages;
  }

  public Vector getMailMessages()
  {
    return this.mailmessages;
  }

  public void setMailMessages(Vector mailmessages)
  {
    this.mailmessages=mailmessages;
  }

  public void addMailMessage(mailMessage msg)
  {
    this.mailmessages.add(msg);
  }

  public String getSMTPHost()
  {
    return p_SMTPHost;
  }

  public void setCheckAddress(boolean checkAddress)
  {
    this.checkAddress=checkAddress;
  }

  public boolean getCheckAddress()
  {
    return this.checkAddress;
  }

  public void setSMTPHost(String p_SMTPHost)
  {
    this.p_SMTPHost=p_SMTPHost;
  }

  public String getPOPHost()
  {
    return p_POPHost;
  }

  public void setDeleteMessages(boolean deleteMessages)
  {
    this.deleteMessages=deleteMessages;
  }

  public void setPOPHost(String p_POPHost)
  {
    this.p_POPHost=p_POPHost;
  }

  public String getUserName()
  {
    return p_UserName;
  }

  public void setUserName(String p_UserName)
  {
    this.p_UserName=p_UserName;
  }

  public String getUserPass()
  {
    return p_UserPass;
  }

  public void setUserPass(String p_UserPass)
  {
    this.p_UserPass=p_UserPass;
  }


  /**
   * This method reads all emails for an user from a POP3/IMAP Server,
   * transforms the emails in XML and saves it in the DataBase
   *@return <b>True</b> - if successfull
   *        <b>False</b> - if not Successfull
   */
  public mailMessage[] ReadMail() throws  RemoteException,MessagingException,
                                    IOException {
    return this.ReadMail(-1,null);
  }


  public mailMessage[] ReadMail(int numOfMessagesToRead,String foldername) throws  RemoteException,MessagingException,
                                    IOException {
  Folder folder=null;
  Session session=null;
  Store store=null;
  mailMessage[] messages =null;

  try {
    Properties props = new Properties();

    session =  Session.getInstance(props, null);
    store = session.getStore(this.protocol);
    store.connect(this.p_POPHost,this.p_UserName,this.p_UserPass);
    // Get folder
    if (foldername==null || foldername.equals(""))
      folder = store.getFolder("INBOX");
    else
      folder = store.getFolder(foldername);
    folder.open(Folder.READ_WRITE);

    // Get directory
    MimeMessage message=null;
    mailMessage mailmsg=null;
    int messageCount=0;
    if (this.WhatMessages==this.ALL_MESSAGES_ONSERVER)messageCount=folder.getMessageCount();
    if (this.WhatMessages==this.UNREAD_MESSAGES)messageCount=folder.getUnreadMessageCount();
    if (this.WhatMessages==this.NEW_MESSAGES)messageCount=folder.getNewMessageCount();
    if (this.messageOrder==this.ORDER_DESC)
    {

      if (numOfMessagesToRead!=-1) messages =new mailMessage[numOfMessagesToRead];
      else {
        messages =new mailMessage[messageCount];
        numOfMessagesToRead=messageCount;
      }
      int j=0;
      for (int i=messageCount; i>messageCount-numOfMessagesToRead; i--)
      {
        j++;
        mailmsg=new mailMessage();
        message=(MimeMessage)folder.getMessage(i);
        this.setMessage(mailmsg,message);
        messages[j-1]=mailmsg;
      }
    }
    else
    {
      if (numOfMessagesToRead!=-1) messageCount=numOfMessagesToRead;
      messages =new mailMessage[messageCount];
      for (int i=1; i<=messageCount; i++)
      {
        mailmsg=new mailMessage();
        message=(MimeMessage)folder.getMessage(i);
        this.setMessage(mailmsg,message);
        messages[i-1]=mailmsg;
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
   if(folder != null)
   {
    folder.close(false);
   }
   return messages;
  }
  finally {
    store.close();
  }
 }

  private String getHeader(String header, MimeMessage msg)
  {
    try
    {
        Enumeration oEnum = msg.getAllHeaders();
        Header headerAux;
        while(oEnum.hasMoreElements())
        {
             headerAux = (Header)oEnum.nextElement();
             if(headerAux != null &&
                headerAux.getName().toLowerCase().equals(header.toLowerCase()))
             {
//                logger.warn("HEADER (" +header +": " + headerAux.getValue());
                return headerAux.getName();
             }
        }
    }
    catch (MessagingException e)
    {
        logger.info(e);
    }
    return null;
  }

  private static String convertString(String s, MimeMessage message)
  {
    String auxText = null;
    String aux = null;
    try
    {
        auxText = MimeUtility.decodeText(s);
//        logger.warn("before ->" + auxText);
        auxText = (aux = new ConvertUTF7(auxText).convertUTF7()) == null? auxText:aux;
//        logger.warn(" after -> " + auxText);
    }
    catch(Exception _e)
    {
        if(auxText != null)
        {
            return auxText;
        }
        return s;
    }
    if(auxText != null)
    {
        return auxText;
    }
    return s;
  }

  private void setMessage(mailMessage mailmsg,MimeMessage message) throws Exception
  {
      //mailmsg.setMessageID(message.getMessageID());
      //mailmsg.setId(message.getMessageNumber());

      //mailmsg.setOriginalMessage(message);

      String auxText=message.getSubject();
      if (auxText!=null)
      {
         auxText = convertString(auxText, message);
         mailmsg.setSubject(auxText);
      }

      //prioridade
      String hPr = getHeader("X-Priority", message);
      if(hPr != null)
      {
          String priority = message.getHeader(hPr, ";");
          if(priority != null)
          {
//            logger.warn(priority);
            int n = NORMAL;
            try
            {
                n = Integer.parseInt(priority);
            }
            catch (Exception e)
            {
                //ignore
            }
            if(n > NORMAL)
            {
                mailmsg.setPriority(LOWEST);
            }
            if(n < NORMAL)
            {
                mailmsg.setPriority(HIGHEST);
            }
          }
      }

      //is to return read receipt
      if(isToReturnReceipt(message))
      {
        mailmsg.setReturnReceipt(true);
      }
      if(message.getContentType() != null && message.getContentType().toLowerCase().indexOf("report") != -1)
      {
//        logger.warn("is report");
        String reportType = getHeader("report-type", message);
        if(reportType != null && message.getHeader(reportType, ";") != null &&
            message.getHeader(reportType,";").toLowerCase().indexOf("delivery-status") != -1)
        {
//            logger.warn("is delivery receipt");
            mailmsg.setIsDeliveredReceipt(true);
        }
        else if(reportType != null && message.getHeader(reportType, ";") != null &&
            message.getHeader(reportType,";").toLowerCase().indexOf("delivery-notification") != -1)
        {
//            logger.warn("is read receipt");
            mailmsg.setIsReadReceipt(true);
        }
        else if(message.getContentType().toLowerCase().indexOf("delivery-status")!= -1)
        {
//            logger.warn("is delivery receipt");
            mailmsg.setIsDeliveredReceipt(true);
        }
        else
        {
//            logger.warn("is read receipt: " + message.getContentType());
            mailmsg.setIsReadReceipt(true);
        }
        String headerAux = getHeader("Thread-Topic", message);
        String thread = headerAux == null ? null : convertString(message.getHeader(headerAux,";"), message);
        if(thread == null || "".equals(thread.trim()))
        {
            headerAux = getHeader("In-Reply-To", message);
            thread =  headerAux == null ? null : convertString(message.getHeader("In-Reply-To", ";"), message);
        }
        if(thread == null || "".equals(thread.trim()))
        {
            headerAux = getHeader("References", message);
            thread =  headerAux == null ? null : convertString(message.getHeader("References", ";"), message);
        }
        if((thread == null || "".equals(thread.trim())) && auxText != null)
        {
//            logger.warn("******** Barraca ***********");
            thread =  auxText;
        }
        if(thread != null)
        {
//            logger.warn("Thread - " +thread);
        }
        mailmsg.setReceiptThread(thread);
      }

      try
      {
        auxText=convertString(message.getFrom()[0].toString(), message);
//          auxText=message.getFrom()[0].toString();
      }
      catch (Exception e)
      {
          auxText = null;
      }

      if (auxText!=null) mailmsg.setFrom(new mailAddress(auxText));
      auxText=message.getMessageID();
      if (auxText!=null) mailmsg.setMessageID(auxText);

      java.util.Date d=new Date();
      try
      {
        if(message.getReceivedDate() != null)
        {
            d = message.getReceivedDate();
        }
      }
      catch (Exception e)
      {
          //ignore
      }
      String mydate=null;

      mydate=dateCompleteToString(d);

      if (d!=null) mailmsg.setDataRecebido(mydate);

      d=new Date();
      try
      {
        if(message.getSentDate() != null)
        {
            d = message.getSentDate();
        }
      }
      catch (Exception e)
      {
          //ignore
      }
      mydate=dateCompleteToString(d);

      if (d!=null) mailmsg.setDataEnviado(mydate);

      Address[] address;

      try
      {
          address=message.getRecipients(Message.RecipientType.TO);
      }
      catch (Exception e)
      {
          address = null;
      }
      if (address!=null)
      {
        int m=address.length;
        for (int j=0;j<m;j++)
          mailmsg.addRecipient(new mailAddress(convertString(address[j].toString(), message)));
      }


      try
      {
          address=message.getRecipients(Message.RecipientType.CC);
      }
      catch (Exception e)
      {
          address = null;
      }
      if (address!=null)
      {
        int m=address.length;
        for (int j=0;j<m;j++)
          mailmsg.addCC(new mailAddress(convertString(address[j].toString(), message)));
      }

      // le attachs e body da msg
      Object content = null;
      try
      {
          content = message.getContent();
      }
      catch (Exception e)
      {
        //caso do utf-7 ou sem encode
          ByteArrayOutputStream bao = new ByteArrayOutputStream();
          message.writeTo(bao);
          content = bao.toString();
          try
          {
            if(message.getContentType() != null &&
               message.getContentType().toUpperCase().indexOf("UTF-7") != -1)
            {
                content = new ConvertUTF7((String)content).convertUTF7();
            }
          }
          catch(Exception _e)
          {
            //ignore
          }
      }


      if (content instanceof Multipart) {
        try
        {
            processMultipart((Multipart) content,mailmsg);
        }
        catch (Exception e)
        {
            mailmsg.setReadWerror(true);
            if(!getContentAfterError((Multipart)content,mailmsg))
            {
                getContentAfterError(message,mailmsg);
            }
        }
      }
      else
      {
        processPart(message,mailmsg);
      }

//      changeimgids();
      if (deleteMessages)message.setFlag(Flags.Flag.DELETED,true);
  }

  /**
   * Change the id's of the images stored inline in the mail message
   * to an id's that NETGEST can identify, this allows the images
   * to be correctly displayed in the Browser.
   *
   */
/*  private void changeimgids()
  //rotina usada para modificar os ids das imagens inseridas directamente na msg
  //pelo caminho do servidor mais o seu nome
  {
    for (int i=0;i<clsid.size();i+=2)
    {
      if (this.getBodyHtml()!=null)
         this.setBodyHtml(tools.replacestr(this.getBodyHtml(),
                                (String)clsid.get(i),(String)clsid.get(i+1)));
    }
  }

 /**
   * Process all the parts of a mail message
   *
   *@param mp - JavaMail Multipart Object
   */
  private void processMultipart(Multipart mp,mailMessage mailmsg) throws MessagingException {
    int n=mp.getCount();
    for (int i=0; i<n;i++) {
      processPart(mp.getBodyPart(i),mailmsg);
    }
  }

    private boolean getContentAfterError(Multipart mp, mailMessage mailmsg)
    {
        try
        {
            // se for uma mensagem de email dentro de outra mensagem
            Integer attachnum= new Integer(mailmsg.getAttach().size()+1);
            File xxx = File.createTempFile("netgest",".none");
            String tmpPath = xxx.getParent();
            xxx.delete();
            xxx= new File(tmpPath+File.separator+"ngtbo");
            if (!xxx.exists())xxx.mkdirs();
            String filepath=tmpPath+File.separator+"ngtbo"+File.separator+"error.txt";
            FileOutputStream ss=new FileOutputStream(filepath);
            mp.writeTo(ss);

            ss.close();

            Attach att=new Attach(filepath,attachnum.toString(),true,false);
            att.setInlineID("");
            mailmsg.addAttach(att);
        }
        catch (FileNotFoundException e)
        {
            logger.warn(e);
            return false;
        }
        catch (IOException e)
        {
            logger.warn(e);
            return false;
        }
        catch (MessagingException e)
        {
            logger.warn(e);
            return false;
        }
        catch (Exception e)
        {
            logger.warn(e);
            return false;
        }
        return true;
    }

    private boolean getContentAfterError(Part p, mailMessage mailmsg) throws Exception
    {
        Integer attachnum= new Integer(mailmsg.getAttach().size()+1);
        File xxx = File.createTempFile("netgest",".none");
        String tmpPath = xxx.getParent();
        xxx.delete();
        xxx= new File(tmpPath+File.separator+"ngtbo");
        if (!xxx.exists())xxx.mkdirs();
        String filepath=tmpPath+File.separator+"ngtbo"+File.separator+"error.txt";
        InputStream i = null;
        ByteArrayOutputStream bao = null;
        try
        {
            i = p.getInputStream();
            bao = new ByteArrayOutputStream();
            copyFile(i, bao);
            if(mailmsg.getBody() == null || mailmsg.getBody().length() == 0)
            {
                if (mailmsg.getBodyHtml()!=null)
                {
                    String aStr=mailmsg.getBodyHtml()+"<br>Error Reading Email:<br>"+bao.toString();
                    mailmsg.setBodyHtml(aStr);
                }
                else
                    mailmsg.setBodyHtml("<br>Error Reading Email:<br>"+bao.toString());
            }
            else
            {
                if (mailmsg.getBody()!=null)
                {
                    String aStr=mailmsg.getBody()+"\nError Reading Email:\n"+bao.toString();
                    mailmsg.setBodyHtml(aStr);
                }
                else
                    mailmsg.setBodyHtml("\nError Reading Email:\n"+bao.toString());
            }
        }
        finally
        {
            try
            {
                if(i != null)
                    i.close();
            }
            catch (Exception e)
            {}
            try
            {
                if(bao != null)
                    bao.close();
            }
            catch (Exception e)
            {}
        }
        return true;
    }

 /**
   * Process a single part of a mail message this part can
   * be either an inline Text part (a body) or a binary one (an Attachment)
   *@param p - JavaMail Part Object
   *
   */
  private void processPart(Part p,mailMessage mailmsg) {

    try {
        String contenttype=p.getContentType();
        String disposition=p.getDisposition();
        String filename=p.getFileName();
        Object mp=null;

        try
        {
            mp=p.getContent();
        }
        catch (Exception e)
        {
            //caso do utf-7 ou sem encode
            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            p.writeTo(bao);
            mp = bao.toString();
        }

        // caso existam outras partes dentro desta parte
        try
        {
            if (mp instanceof Multipart) processMultipart((Multipart)mp,mailmsg);
        }
        catch (Exception e)
        {
            mailmsg.setReadWerror(true);
            if(!getContentAfterError((Multipart)mp,mailmsg))
            {
                getContentAfterError(p,mailmsg);
            }
        }

        String auxStr=contenttype.toLowerCase();
        // se vier no email como attach
        if (filename!=null)
        {
           // se for uma mensagem de email dentro de outra mensagem
           Integer attachnum= new Integer(mailmsg.getAttach().size()+1);
           if (filename.equals(".msg")) filename="email"+(attachnum)+".eml";

           // guarda o ficheiro
//           File f=new File(p_sysuser.getOutBox() +filename);

          InputStream in=new BufferedInputStream(p.getInputStream());

          File xxx = File.createTempFile("netgest",".none");
          String tmpPath = xxx.getParent();
          xxx.delete();
          xxx= new File(tmpPath+File.separator+"ngtbo");
          if (!xxx.exists())xxx.mkdirs();

          String filepath=tmpPath+File.separator+"ngtbo"+File.separator+filename;
//          FileOutputStream ss=new FileOutputStream(filepath);
          FileOutputStream ss = null;
          try
          {
            ss=new FileOutputStream(filepath);
          }
          catch (FileNotFoundException e)
          {
            String newFile = mailmsg.getMessageID().replaceAll("<","").replaceAll(">","");
            filepath=tmpPath+File.separator+"ngtbo"+File.separator+newFile;
            ss=new FileOutputStream(filepath);
          }


          try
          {
            copyFile(in, ss);
          }
          catch (Exception e)
          {
            try{ss.close();}catch (Exception _e){}
            try{in.close();}catch (Exception _e){}
          }

          boolean inline=false;

          //para o caso de ser uma imagem contida dentro do body de uma mensagem
          String inlineid="";
          if (!(Part.ATTACHMENT.equals(disposition)))
          {
            inline=true;
            String[] farraux=p.getHeader("Content-ID");
            if(farraux != null && farraux.length > 0)
            {
                inlineid=farraux[0].substring(1,farraux[0].length()-1);
            }
            else
            {
                inline = false;
            }
          }
          Attach att=new Attach(filepath,attachnum.toString(),true,inline);
          att.setInlineID(inlineid);
          mailmsg.addAttach(att);


        }
        // Body da msg texto apenas
        else if (auxStr.indexOf("text/plain")!=-1)
        {
         if (mailmsg.getBody()!=null)
         {
            String aStr=mailmsg.getBody()+(String)mp;
            mailmsg.setBody(aStr);
          }
         else
            mailmsg.setBody((String)mp);
        }
        // Body da msg quando esta e enviado em HTML
        else if (auxStr.indexOf("text/html")!=-1)
        {
         if (mailmsg.getBodyHtml()!=null)
         {
            String aStr=mailmsg.getBodyHtml()+(String)mp;
            mailmsg.setBodyHtml(aStr);
          }
         else
            mailmsg.setBodyHtml((String)mp);
        }
    }
    catch (Exception e)
    {
//        e.printStackTrace();
        logger.error("", e);
    }
  }

  public void send() throws AddressException,MessagingException,IOException
  {
    send( true );
  }

  public void send(boolean asMultiPart ) throws AddressException,MessagingException,IOException {

    Session session=null;
    for (int k=0;k<this.mailmessages.size();k++)
    {
      mailMessage mailmsg=(mailMessage)this.mailmessages.get(k);
      Vector files=new Vector();

      Properties props = new Properties();
      props.put("mail.smtp.host", this.p_SMTPHost);

      if (!this.checkAddress) props.put("mail.smtp.sendpartial", "true");

      session = Session.getInstance(props, null);
      session.setDebug(false);

      Message msg=new MimeMessage(session);
      //de
      InternetAddress addressFrom=new InternetAddress(mailmsg.getFrom().getEmail());
      if(mailmsg.askForDeliverReceipt())
      {
//        logger.warn("ask for deliver receipt");
        setAskReceipt(true, props, msg, addressFrom);
      }
      if(mailmsg.askForReadReceipt())
      {
//        logger.warn("ask for read receipt");
        setAskReceipt(false, props, msg, addressFrom);
      }
      //Prioridade
      setPriority(mailmsg.getPriority(), msg);

      msg.setFrom(addressFrom);
      //Subject da msg
      msg.setSubject(mailmsg.getSubject());

      //Return Receipt SIM/NAO
      //msg.reply(mailmsg.getReturnReceipt());

      //destinatarios
      InternetAddress[] addressTo=new InternetAddress[mailmsg.getRecipient().size()];
      for (int i=0;i<mailmsg.getRecipient().size();i++)
      {
        mailAddress auxaddress=(mailAddress)mailmsg.getRecipient().get(i);
        addressTo[i]=new InternetAddress((String)auxaddress.getEmail());
      }
      msg.setRecipients(Message.RecipientType.TO,addressTo);

      //destinatarios CC
      InternetAddress[] addressCC=new InternetAddress[mailmsg.getCC().size()];
      for (int i=0;i<mailmsg.getCC().size();i++)
      {
        mailAddress auxaddress=(mailAddress)mailmsg.getCC().get(i);
        addressCC[i]=new InternetAddress((String)auxaddress.getEmail());
      }
      msg.setRecipients(Message.RecipientType.CC,addressCC);

      //destinatarios BCC
      InternetAddress[] addressBCC=new InternetAddress[mailmsg.getBCC().size()];
      for (int i=0;i<mailmsg.getBCC().size();i++)
      {
        mailAddress auxaddress=(mailAddress)mailmsg.getBCC().get(i);
        addressBCC[i]=new InternetAddress((String)auxaddress.getEmail());
      }
      msg.setRecipients(Message.RecipientType.BCC,addressBCC);


      if( asMultiPart )
      {
          //Body da msg
          BodyPart messageBodyPart = new MimeBodyPart();
          if (mailmsg.getBodyHtml()!=null)
            messageBodyPart.setContent(mailmsg.getBodyHtml(),"text/html");
          else
            messageBodyPart.setText(mailmsg.getBody());

          Multipart multipart = null;
          if(mailmsg.isReadReceipt())
          {
            multipart = new MimeMultipart("report;report-type=disposition-notification");
          }
          else
          {
            multipart = new MimeMultipart();
          }

          multipart.addBodyPart(messageBodyPart);
    //      if(mailmsg.isReadReceipt())
    //      {
    //        BodyPart messageBodyPart2 = new MimeBodyPart();
    //        ByteArrayDataSource bads = new ByteArrayDataSource("", "message/disposition-notification");
    //        DataHandler dh = new DataHandler(bads);
    //        messageBodyPart2.setDataHandler(dh);
    //        messageBodyPart2.setHeader("Final-Recipient", "rfc822;"+ addressTo[0].getAddress());
    //        messageBodyPart2.setHeader("Disposition", "manual-action;displayed");
    //        multipart.addBodyPart(messageBodyPart2);
    //      }

          // Attachments
          for (int i=0;i<mailmsg.getAttach().size();i++)
          {
            messageBodyPart = new MimeBodyPart();

            String httpgateway=null;
            File tmpfile=null;
            Attach auxAttach=(Attach)mailmsg.getAttach().get(i);

            tmpfile=new File(auxAttach.getLocation());
            DataSource source=new FileDataSource(tmpfile);
            messageBodyPart.setDataHandler(new DataHandler(source));
            messageBodyPart.setFileName(auxAttach.getName());
            multipart.addBodyPart(messageBodyPart);
            if (auxAttach.getDeleteAfterUse()) files.add(tmpfile);
         }

          // Put parts in message
          msg.setContent(multipart);
      }
      else
      {
          msg.setText( mailmsg.getBody() );
      }
      // Envia Msg
      boolean send = false;
      int retries = 0;
      while (!send)
      {
          try
          {
              Transport.send(msg);
              send = true;
          }
          catch (SendFailedException e)
          {
              if (retries >= RETRY_TIMES)
              {
                throw e;
              }
          }
          catch(MessagingException _e)
          {
              if (retries >= RETRY_TIMES)
              {
                throw _e;
              }
          }
          retries++;
      }

      Date d=new Date();
      try{if(msg.getSentDate() != null){d = msg.getSentDate();}}catch (Exception e){/*ignore*/}
      String mydate=dateCompleteToString(d);
      mailmsg.setDataEnviado(mydate);

      //delet the used files
      for (int i=0;i<files.size();i++)
      {
        File auxf=(File)files.get(i);
        auxf.delete();
      }
    }
    if (session!=null)session=null;
  }

  private void setPriority(int priority, Message message)
  {
    try
    {
        message.setHeader("X-Priority",  String.valueOf(priority));
        if(priority > NORMAL)
        {
            message.setHeader("Priority",  PR_LOW);
            message.setHeader("Importance",  IMP_LOW);
        }
        else if(priority < NORMAL)
        {
            message.setHeader("Priority",  PR_HIGH);
            message.setHeader("Importance",  IMP_HIGH);
        }
        else
        {
            message.setHeader("Priority",  PR_NORMAL);
            message.setHeader("Importance",  IMP_NORMAL);
        }

    }
    catch (MessagingException e)
    {
        logger.info(e);
    }
  }


  private void setAskReceipt(boolean deliver, Properties props, Message message, InternetAddress from)
  {
    try
    {
        if(deliver)
        {
            StringBuffer sb = new StringBuffer("SUCCESS,FAILURE ORCPT=rfc822;");
            sb.append(from.getAddress());
            props.put("mail.smtp.dsn.notify", sb.toString());
            message.setHeader("Return-Receipt-To",  from.getAddress());
        }
        else
        {
           //RFC 2298
           message.setHeader("Disposition-Notification-To",  from.getAddress());
           message.setHeader("Read-Receipt-To", from.getAddress());
           //pegasus
           message.setHeader("X-Confirm-reading-to", from.getAddress());
        }
    }
    catch (MessagingException e)
    {
        logger.info(e);
    }
  }

  private boolean isToReturnReceipt(MimeMessage message) throws MessagingException
  {
    String headerAux = getHeader("Disposition-Notification-To", message);
    String disposition = headerAux == null ? null : message.getHeader(headerAux,";");
    if(disposition == null || "".equals(disposition.trim()))
    {
        headerAux = getHeader("Read-Receipt-To", message);
        disposition = headerAux == null ? null : message.getHeader(headerAux,";");
    }
    if(disposition == null || "".equals(disposition.trim()))
    {
        headerAux = getHeader("X-Confirm-reading-to", message);
        disposition = headerAux == null ? null : message.getHeader(headerAux,";");
    }
    if(disposition == null || "".equals(disposition.trim()))
    {
        return false;
    }
//    logger.warn("Return to: " + disposition);
    return true;
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
    return WhatMessages;
  }

  public void setWhatMessages(int newWhatMessages)
  {
    WhatMessages = newWhatMessages;
  }

  public int getMessageOrder()
  {
    return messageOrder;
  }

  public void setMessageOrder(int newMessageOrder)
  {
    messageOrder = newMessageOrder;
  }

    public static void copyFile(InputStream fis, OutputStream fos) throws IOException{
        byte[] b = new byte[1024*10];
        int numBytes = 0;

        for(long i = 0; (numBytes = fis.read(b)) != -1;i++) {
                fos.write(b,0,numBytes);
        }
    }

    public static String dateCompleteToString(Date data) {
        try {
            return DateFormat.getInstance().format(data);
        }
        catch (Exception ex) {
            return null;
        }
    }
    public static Date stringCompleteToDate(String data) throws ParseException{
            return DateFormat.getInstance().parse(data);
    }

    public void moveMessages(String fromFolder, String toFolder, int[] msgsid)
        throws RemoteException, MessagingException {
        Folder folder1 = null;
        Folder folder2 = null;
        Folder folder3 = null;
        Session session = null;
        Store store = null;

        try
        {
          Properties props = new Properties();

          session = Session.getInstance(props, null);
          store = session.getStore(this.protocol);
          store.connect(this.p_POPHost, this.p_UserName, this.p_UserPass);

          folder1 = store.getFolder(fromFolder);
          folder2 = store.getFolder(toFolder);

          folder1.open(Folder.READ_WRITE);
          folder2.open(Folder.READ_WRITE);

          int t = msgsid.length;
          Message[] m = null;

          for (int i = 0; i < t; i++) {
              m = folder1.getMessages(msgsid);
              folder1.copyMessages(m, folder2);
          }

          int tm = m.length;

          for (int i = 0; i < tm; i++) {
              m[i].setFlag(Flags.Flag.DELETED, true);
          }

          Message[] trash = folder1.expunge();
        }
        catch (Exception e)
        {
          logger.error("Erro a mover mensagens...",e);
        }
        finally
        {
          if(folder1!=null)
           folder1.close(true);
          if(folder2!=null)
           folder2.close(true);
          if(folder3!=null)
           folder3.close(true);
          store.close();
        }

    }

}