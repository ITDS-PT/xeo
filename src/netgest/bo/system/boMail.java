/*Enconding=UTF-8*/
package netgest.bo.system;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import netgest.utils.*;
import netgest.utils.mail.*;
import java.io.*;
import javax.mail.*;
import javax.mail.internet.*;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.*;
import netgest.io.*;
import java.util.*;
import java.util.regex.*;
import java.rmi.*;
import netgest.bo.ejb.boManagerLocal;
import netgest.bo.workflow.*;
import netgest.bo.system.Logger;


public class boMail extends mail 
{
    //
    public static final int REPLAY = 0;
    public static final int REPLAY_ALL = 1;
    public static final int FORWARD = 2;

    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.boMail");
    
  private static final String errSMTP=MessageLocalizer.getMessage("THERE_IS_NO_DEFINED_SMTP_SERVER");
  private static final String errFrom=MessageLocalizer.getMessage("UNABLE_TO_SEND_MAIL_WITHOUT_EMAIL_ADDRESS_DEFINED");
  private static final String errTo=MessageLocalizer.getMessage("UNABLE_TO_SEND_MAIL_WITHOUT_RECEIVER_DEFINED");
  private static final String errMailNotSaved=MessageLocalizer.getMessage("EMAIL_HAS_TO_BE_SAVED_BEFORE_SENDING");
  private EboContext ctx=null;
  private boObject mailfolder=null;
  private boObject mailaccount=null;
  private Hashtable emailstoCreate=new Hashtable();
  private boObject mailaccountOwner;
  
  public boMail(EboContext ctx)
  {
    this.ctx=ctx;
    this.setUserName( ctx.getBoSession().getUser().getName() );
  }
  
  public boObject getMailFolder()
  {
    return this.mailfolder;
  }
  
  public void setMailFolder(boObject mailfolder)
  {
    this.mailfolder=mailfolder;
  }

  public boObject getMailAccount()
  {
    return this.mailaccount;
  }
  
  public void setMailAccount(boObject mailaccount)
  {
    this.mailaccount=mailaccount;
  }

  public void setMailAccountOwner(boObject owner)
  {
    this.mailaccountOwner=owner;
  }
  
/*  public void markToSend(boObject mailobject)
  {
      String param=mailobject.getParameter("TOSEND");
      if (param==null || param.equals(""))
        mailobject.setParameter("TOSEND","S");
  } */

    public boolean sendReceipt(boObject mailobject)  
    {
      try
      {
            mailMessage mailmsg=new mailMessage();
            String smtphost=boConfig.getMailConfig().getProperty("smtphost");
            if ( !mailobject.exists() )
            {
                 mailobject.addErrorMessage(errMailNotSaved);
                 return false;     
            }
            if (smtphost!=null && !smtphost.equals(""))
              this.setSMTPHost(smtphost);
            else
            {
              mailobject.addErrorMessage(errSMTP);
              return false;
            }
            
            //prioridade
            String priority = mailobject.getAttribute("priority").getValueString();
            if("0".equals(priority))
            {
                mailmsg.setPriority(mail.LOWEST);
            }
            else if("2".equals(priority))
            {
                mailmsg.setPriority(mail.HIGHEST);
            }
            else
            {
                mailmsg.setPriority(mail.NORMAL);
            }
            //De
            boObject fromMail=mailobject.getAttribute("mail_from").getObject();
            boObject toMail=mailobject.getAttribute("assignedQueue").getObject(); 
            
            if(fromMail!=null) 
            {
              String address=fromMail.getAttribute("email").getValueString();
              if (address!=null && !address.equals(""))
              {
                String lastName = fromMail.getAttribute("lastname").getValueString();
                String name = fromMail.getAttribute("name").getValueString();
                String aux = ((lastName == null ? "":(lastName+ ", "))  + (name == null ? "":name)).trim();
                if(aux.length()==0)
                {
//                    logger.warn("sending receipt to:" + address);
                    mailmsg.addRecipient(new mailAddress(address));
                }
                else
                {
                    mailmsg.addRecipient(new mailAddress(name, address));
                }
              }
              else 
              {
                mailobject.addErrorMessage(errFrom);
                return false;        
              }
            }
            else
            {
              mailobject.addErrorMessage(errFrom);
              return false;                
            }
            
            if(toMail!=null) 
            {
              String address=toMail.getAttribute("email").getValueString();
              if (address!=null && !address.equals(""))
              {
                String lastName = toMail.getAttribute("lastname").getValueString();
                String name = toMail.getAttribute("name").getValueString();
                String aux = ((lastName == null ? "":(lastName+ ", "))  + (name == null ? "":name)).trim();
                if(aux.length()==0)
                {
                    mailmsg.setFrom(new mailAddress(address));
                }
                else
                {
                    mailmsg.setFrom(new mailAddress(name, address));
                }
              }
              else 
              {
                mailobject.addErrorMessage(errFrom);
                return false;        
              }
            }
            else
            {
              mailobject.addErrorMessage(errFrom);
              return false;                
            }
    
            String footprint=boConfig.getMailConfig().getProperty("footprint");
            String subject = null;
            try
            {
                subject = MessageLocalizer.getMessage("READ")+": " + mailobject.getAttribute("name").getValueString();        
            }
            catch (boRuntimeException e)
            {
                subject = MessageLocalizer.getMessage("READ")+": ";
            }
            StringBuffer msg = new StringBuffer();
            //de
            msg.append(MessageLocalizer.getMessage("YOUR_MESSAGE_TO"));
            if(toMail != null)
            {
                try
                {
                    if(toMail.getAttribute("name").getValueString() != null)
                    {
                        msg.append(toMail.getAttribute("name").getValueString());
                    }
                    if(toMail.getAttribute("lastname").getValueString() != null)
                    {
                        if(toMail.getAttribute("name").getValueString() == null || 
                            !toMail.getAttribute("name").getValueString().trim().toUpperCase()
                                .endsWith(toMail.getAttribute("lastname").getValueString().trim()
                                        .toUpperCase()))
                        {
                            msg.append(" ").append(toMail.getAttribute("lastname").getValueString());
                        }
                    }
                    msg.append(" [").append(toMail.getAttribute("email").getValueString()).append("]");
                }
                catch (boRuntimeException e)
                {
                    //ignore
                }
            }
            msg.append(MessageLocalizer.getMessage("SUBJECT"));
            try
            {
                msg.append(mailobject.getAttribute("name").getValueString());
            }
            catch (boRuntimeException e)
            {
                //ignore
            }
            msg.append(MessageLocalizer.getMessage("WAS_READ_ON_DATE"));
            Date readDate = new Date();
            SimpleDateFormat df = new SimpleDateFormat("EEEEE, dd 'de' MMMMM 'de' yyyy HH:mm:ss");            
            msg.append(df.format(readDate)).append(".");
            
            mailmsg.setBody(msg.toString());
            mailmsg.setSubject(subject);
            mailmsg.setIsReadReceipt(true);
            this.addMailMessage(mailmsg);
            this.send();
            mailobject.getAttribute("send_date_read_receipt").setValueDate(readDate);
            mailobject.update();
            return true;
        }      
        catch(boRuntimeException e)
        {
            mailobject.addErrorMessage
            ( 
            LoggerMessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
            "<span style='display:none'>" + e.getMessage() +
            "</span>"
            );
                  logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            return false;
        }
        catch(AddressException e)
        {
      
           mailobject.addErrorMessage(MessageLocalizer.getMessage("INVALID_ADDRESS")+
           "<span style='display:none'>" + e.getMessage() +
            "</span>"
            );
                  logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
           return false; 
        }
        catch(MessagingException e)
        {
           mailobject.addErrorMessage(LoggerMessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
           "<span style='display:none'>" + e.getMessage() +
            "</span>"
            );
                  logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
           return false; 
        }
        catch(IOException e)
        {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            mailobject.addErrorMessage(MessageLocalizer.getMessage("CANNOT_SEND_ATTACH")+
            "<span style='display:none'>" + e.getMessage() +
            "</span>"
            );
            return false;
        }
        catch(Exception e)
        {
                mailobject.addErrorMessage(LoggerMessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
                "<span style='display:none'>" + e.getMessage() +
                "</span>"            
            );
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
            return false;
        }
    }
  public boolean send(boObject mailobject)  {
    //String param=mailobject.getParameter("TOSEND");
//    if (param!=null && param.equals("S"))
//    {    
      try
      {
        mailMessage mailmsg=new mailMessage();
        String smtphost=boConfig.getMailConfig().getProperty("smtphost");
        if ( !mailobject.exists() )
        {
             mailobject.addErrorMessage(errMailNotSaved);
             return false;     
        }
        if (smtphost!=null && !smtphost.equals(""))
          this.setSMTPHost(smtphost);
        else
        {
          mailobject.addErrorMessage(errSMTP);
          return false;
        }
        
        //prioridade
        String priority = mailobject.getAttribute("priority").getValueString();
        if("0".equals(priority))
        {
            mailmsg.setPriority(mail.LOWEST);
        }
        else if("2".equals(priority))
        {
            mailmsg.setPriority(mail.HIGHEST);
        }
        else
        {
            mailmsg.setPriority(mail.NORMAL);
        }
        //De
        boObject assigned=mailobject.getAttribute("mail_from").getObject();
        if(assigned ==null) {
            assigned=mailobject.getAttribute("assignedQueue").getObject(); 
        }
        
        if(assigned!=null) {
          String address=assigned.getAttribute("email").getValueString();
          if (address!=null && !address.equals("")) 
            mailmsg.setFrom(new mailAddress(address));
          else 
          {
            mailobject.addErrorMessage(errFrom);
            return false;        
          }
        }
        else
        {
          mailobject.addErrorMessage(errFrom);
          return false;                
        }
        
		boolean hasRecipient = false;
		
        bridgeHandler bridge=mailobject.getBridge("mail_to");
        bridge.beforeFirst();      
              
        while(bridge.next())
		{
			String emailaddress=bridge.getObject().getAttribute("email").getValueString();
		  
			if(emailaddress!=null && !emailaddress.equals(""))
			{
				mailmsg.addRecipient(emailaddress,emailaddress);
				
				hasRecipient = true;
			}
        }
		
        bridge=mailobject.getBridge("mail_cc");
        bridge.beforeFirst();
		
        while(bridge.next())
		{
			String emailaddress=bridge.getObject().getAttribute("email").getValueString();
			
			if(emailaddress!=null && !emailaddress.equals(""))
			{
				mailmsg.addCC(emailaddress,emailaddress);
				
				hasRecipient = true;
			}
        }      
        
        bridge=mailobject.getBridge("mail_bcc");
        bridge.beforeFirst();
		
        while(bridge.next())
		{
			String emailaddress=bridge.getObject().getAttribute("email").getValueString();
			
			if(emailaddress!=null && !emailaddress.equals(""))
			{
				mailmsg.addBCC(emailaddress,emailaddress);
				
				hasRecipient = true;
			}
        }      
        
		if (!hasRecipient)
        {
			mailobject.addErrorMessage(errTo);
			return false;
        }
        
		String footprint=boConfig.getMailConfig().getProperty("footprint");
        String subject = mailobject.getAttribute("name").getValueString();
        if((footprint==null || footprint.equalsIgnoreCase("true")) &&
            (subject == null || subject.toUpperCase().indexOf(" [XEO"+mailobject.getBoui()+"] ") == -1))
        {
            subject = (subject == null) ? ("[XEO"+mailobject.getBoui()+"] ") : (subject + " - [XEO"+mailobject.getBoui()+"] ");
        }

        mailmsg.setSubject(subject);
        
        //Links Relacionados
        String links=""; 
        String link="";
        bridge=mailobject.getBridge("RO");
        if (bridge.getRowCount()>0) links="<BR><BR>"+MessageLocalizer.getMessage("RELATED_LINKS")+":<BR>";
        bridge.beforeFirst();      
        while(bridge.next())
        {
          String auxlink=bridge.getObject().getURL().toString();
          if (auxlink!=null && !auxlink.equals(""))
            link+=auxlink+"<BR>";
        }      
        links+=link;
        
        // Falta poder utilizar a conta por defeito
        //mailmsg.setBodyHtml(mailobject.getAttribute("description").getValueString()+links+"<BR><BR>"+this.mailaccount.getAttribute("footprint").getValueString());
        String refs="";
        if (footprint==null || footprint.equalsIgnoreCase("true"))
        {
          refs= "<i><hr>"+MessageLocalizer.getMessage("IF_YOU_DESIRE_TO_ANSWER_TO_THIS_MAIL_PLEASE_INCLUDE_")+" <br/></i>";
          refs+=" [XEO"+mailobject.getBoui()+"] ";
          boObject[] parents=mailobject.getParents();
          for (int i = 0; i < parents.length ; i++) 
          {
              refs+=" [XEO"+parents[i].getBoui()+"] ";
          }
          refs+="<p/>";
        }
		
        mailmsg.setBodyHtml(mailobject.getAttribute("description").getValueString()+links+refs);
        
        //Attachs
        bridge=mailobject.getBridge("documents");
        bridge.beforeFirst();      
        int i=0;
        while(bridge.next()) {
          i++;        
          iFile fich=bridge.getObject().getAttribute("file").getValueiFile();
          if(fich != null)
          {
              InputStream in=new BufferedInputStream(fich.getInputStream());
      
              File xxx = File.createTempFile("netgest",".none");
              String tmpPath = xxx.getParent();
              xxx.delete();
              xxx= new File(tmpPath+File.separator+"xeo"+File.separator+this.getUserName());
              if (!xxx.exists())xxx.mkdirs();
              
              String filepath=tmpPath+File.separator+"xeo"+File.separator+this.getUserName()+File.separator+fich.getName();
              FileOutputStream ss=new FileOutputStream(filepath);              
              try
              {
                if(fich.length() > 0)
                {
                    copyFile(in, ss);
                }
              }
              finally
              {
                try
                {
                    if(in != null)
                        in.close();
                }
                catch (Exception e){}
                try
                {
                    if(ss != null)
                        ss.close();
                }
                catch (Exception e){}
              }
              
              mailmsg.addAttach(filepath,new Integer(i).toString(),true);
          }
        } 
        this.addMailMessage(mailmsg);

        if(mailobject.getAttribute("read_receipt").getValueString() != null && 
            "1".equals(mailobject.getAttribute("read_receipt").getValueString()))
        {
            mailmsg.setAskForReadReceipt(true);
        }
        if(mailobject.getAttribute("read_receipt").getValueString() != null && 
            "1".equals(mailobject.getAttribute("read_receipt").getValueString()))
        {
            mailmsg.setAskForDeliverReceipt(true);
        }

        this.send();

        //datas
        try
        {
            if(mailmsg.getDataEnviado() != null && !"".equals(mailmsg.getDataEnviado()))
            {
                mailobject.getAttribute("dtdoc").setValueDate(DateFormat.getInstance().parse(mailmsg.getDataEnviado()));
            }
        }catch(Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_SETTING_EMAIL_DATE"), e);
        }
        mailobject.getAttribute("already_send").setValueString("1");
        //mailobject.setParameter("TOSEND","N");
        mailobject.update();
        return true;
      }
      catch(iFilePermissionDenied e)
      {
        mailobject.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_ATTACHING_FILES_TO_THE_EMAIL")+
        "<span style='display:none'>" + e.getMessage() +
        "</span>"
        );
              logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
        return false;
      }
      catch(boRuntimeException e)
      {
        mailobject.addErrorMessage
        ( 
        		MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
        "<span style='display:none'>" + e.getMessage() +
        "</span>"
        );
              logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
        return false;
      }
      catch(AddressException e)
      {
      
       mailobject.addErrorMessage(MessageLocalizer.getMessage("INVALID_ADDRESS")+
       "<span style='display:none'>" + e.getMessage() +
        "</span>"
        );
              logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
       return false; 
      }
      catch(MessagingException e)
      {
       mailobject.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
       "<span style='display:none'>" + e.getMessage() +
        "</span>"
        );
              logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
       return false; 
      }
      catch(IOException e)
      {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
        mailobject.addErrorMessage(MessageLocalizer.getMessage("UNABLE_TO_SEND_ATTACH")+
        "<span style='display:none'>" + e.getMessage() +
        "</span>"
        );
        return false;
      }
      catch(Exception e)
      {            
            mailobject.addErrorMessage(MessageLocalizer.getMessage("A_UNEXPECTED_ERROR_OCCURRED_SENDING_YOUR_MESSAGE")+
            "<span style='display:none'>" + e.getMessage() +
            "</span>"            
        );
        logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
        return false;
      }
/*    }
    else
    {
//      mailobject.addErrorMessage("O email não pode ser enviado, pois não se encontra marcado para tal.");
      return false;
    } */
  }

  public void deleteMessage(String msgid)
  {
    Folder folder=null;
    Session session=null;
    Store store=null;
    mailMessage[] messages =null;

    Properties props = new Properties();
    try
    {
      session = Session.getDefaultInstance(props, null);
      store = session.getStore(this.getProtocol());
      store.connect(this.getPOPHost(),this.getUserName(),this.getUserPass());
      // Get folder
      folder = store.getFolder("INBOX");      
      folder.open(Folder.READ_WRITE);
      // Get directory
      MimeMessage message=null;
      mailMessage mailmsg=null;
      messages =new mailMessage[folder.getMessageCount()];
      for (int i=1; i<=folder.getMessageCount(); i++)
      {
        message=(MimeMessage)folder.getMessage(i);
        String currid=message.getMessageID();
        if (msgid.equals(currid))
          message.setFlag(Flags.Flag.DELETED,true);      
      }        
      folder.close(true);            
    }
    catch(MessagingException e)
    {
      logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_DELETING_MESSAGES_ON_THE_SERVER_")+": "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);      
    }   
  }
  
  public int numOfMessagesToGet(String lastMessageID,String foldername)
  {
    Folder folder=null;
    Session session=null;
    Store store=null;
    int numofMessages=0;
    mailMessage[] messages =null;
  
    try {
      Properties props = new Properties();
  
      session = Session.getDefaultInstance(props, null);
      store = session.getStore(this.getProtocol());
      store.connect(this.getPOPHost(),this.getUserName(),this.getUserPass());
      // Get folder
      if (foldername==null || foldername.equals(""))
        folder = store.getFolder("INBOX");
      else
        folder = store.getFolder(foldername);
      folder.open(Folder.READ_WRITE);
      
      // Get directory
      MimeMessage message=null;
      mailMessage mailmsg=null;
      int messageCount=folder.getMessageCount();
      messages =new mailMessage[messageCount];
      for (int i=messageCount; i>=1; i--)
      {
//        mailmsg=new mailMessage();         
        message=(MimeMessage)folder.getMessage(i);
        String auxText=message.getMessageID();
                
        if (!auxText.equals(lastMessageID)) numofMessages++;
        else break;
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
  
  public boolean ReadMail(boolean deleteMessages,boolean onlyXEOMailMessages,String foldername)
  {
    try
    {
      this.setDeleteMessages(false);
      int messagecount=this.numOfMessagesToGet(this.mailfolder.getAttribute("lastmessageid").getValueString(),foldername);
      mailMessage[] msgs=this.ReadMail(messagecount,foldername);
      ArrayList validEmails = new ArrayList();
      Hashtable objectToUpdate = new Hashtable();
      for (int i=msgs.length-1;i>=0;i--)
      {        

        this.emailstoCreate=new Hashtable();
        boObject mailobject=boObject.getBoManager().createObject(ctx,"email");        
        //Process the message and if its an XEO one, it relates the message with the correct XEO information
        String x1=msgs[i].getBody();
        String x2=msgs[i].getBodyHtml();
        if( x1== null ) x1 = "";
        if( x2== null ) x2 = "";

        //nature
        mailobject.getAttribute("nature").setValueString("E");
        
        //datas
        try
        {
            if(msgs[i].getDataRecebido() != null && !"".equals(msgs[i].getDataRecebido()))
            {
                mailobject.getAttribute("dtdoc").setValueDate(mail.stringCompleteToDate(msgs[i].getDataRecebido()));
                mailobject.getAttribute("beginDate").setValueDate(mail.stringCompleteToDate(msgs[i].getDataRecebido()));
            }
        }catch(Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_SETTING_EMAIL_DATE"), e);
        }
        
        //prioridade
        if(msgs[i].getPriority() > mail.NORMAL)
        {
            mailobject.getAttribute("priority").setValueString("0");
        }
        else if(msgs[i].getPriority() < mail.NORMAL)
        {
            mailobject.getAttribute("priority").setValueString("2");
        }
        else
        {
            mailobject.getAttribute("priority").setValueString("1");
        }

        //foi pedido um recibo de leitura
        if(msgs[i].getReturnReceipt())
        {
            mailobject.getAttribute("send_read_receipt").setValueString("1");
        }
        else
        {
            mailobject.getAttribute("send_read_receipt").setValueString("0");
        }

        ArrayList relatedBoui=new ArrayList();
        
        boolean isXEOMessage=this.isXEOMessage(msgs[i].getSubject()+x2+x1,mailobject, relatedBoui );        
        if (isXEOMessage || !onlyXEOMailMessages) 
        {
          this.setMailRecipient(msgs[i].getFrom(),"mail_from",mailobject);
          
          for (int j=0;j<msgs[i].getRecipient().size();j++)
          {
            mailAddress mailaddress=(mailAddress)msgs[i].getRecipient().get(j);
            this.setMailRecipient(mailaddress,"mail_to",mailobject);
          }
          for (int j=0;j<msgs[i].getCC().size();j++)
          {
            mailAddress mailaddress=(mailAddress)msgs[i].getCC().get(j);
            this.setMailRecipient(mailaddress,"mail_cc",mailobject);
          }          
          //ATTACHS

          for (int j=0;j<msgs[i].getAttach().size();j++)
          {
            Attach att=(Attach)msgs[i].getAttach().get(j);                      
            boObject currDoc=mailobject.getBridge("documents").addNewObject("Ebo_Document");
            currDoc.getAttribute("file").setValueiFile(new FSiFile(null,att.getLocation(),null));
            if (att.isInline()) 
            msgs[i].setBodyHtml(StringUtils.replacestr(msgs[i].getBodyHtml(),"cid:"+att.getInlineID(),
                                StringUtils.putSlash(boConfig.getWebContextRoot())+"attachfile.jsp?look_parentBoui="+currDoc.getBoui()+"&att_display=y&att_download=y&curTime="+(new Date()).getTime()));              
          }

          String assunto=msgs[i].getSubject();
          if (assunto == null || assunto.equals("")) assunto="[Sem Assunto]";
          if(msgs[i].getReadWerror())
          {
            assunto = assunto + " - [EMAIL LIDO COM ERRO] ";
          }
          mailobject.getAttribute("name").setValueString(assunto);
          mailobject.getAttribute("messageid").setValueString(msgs[i].getMessageID());
          if(msgs[i].getBodyHtml() != null && !"".equals(msgs[i].getBodyHtml()))
          {
            mailobject.getAttribute("description").setValueString(msgs[i].getBodyHtml());
          }
          else 
          {
            if(msgs[i].getBody() != null && msgs[i].getBody().length() > 0)
            {
                mailobject.getAttribute("description").setValueString(ClassUtils.textToHtml(msgs[i].getBody()) );    
            }
          }
          mailobject.getAttribute("messageid").setValueString(msgs[i].getMessageID());     

          if(mailaccountOwner != null)
          {
            mailobject.getAttribute("assignedQueue").setValueLong(mailaccountOwner.getBoui());
          }
          else
          {
              mailobject.getAttribute("assignedQueue").setValueLong(1068);
          }
          if(!msgs[i].isDeliveredReceipt() && !msgs[i].isReadReceipt())
          {
            //não vou fazer o set pois o objecto poderá não ser gravado
            mailobject.getAttribute("folder").setValueLong(this.getMailFolder().getBoui());
          }
          
          for (int j = 0; j < relatedBoui.size() ; j++)
          {
                boObject o2= ( boObject) relatedBoui.get(j);
                try
                {
                    if(!bridgeContainsBoui(mailobject.getBridge("RO"), o2.getBoui()))
                    {
                        mailobject.getBridge("RO").add( o2.getBoui() );
                    }
                    if ( o2.getName().equals("email") )
                    {
                        if(!bridgeContainsBoui(o2.getBridge("RO"), mailobject.getBoui()))
                        {
                            o2.getBridge("RO").add( mailobject.getBoui() );
                        }
                        //mailobject.getUpdateQueue().add( o2.getBoui() , boObjectUpdateQueue.MODE_SAVE );
                        
                    }
                    else if ( o2.getName().equals("sr") || o2.getName().equals("claim")  || o2.getName().equals("information") ||
                        o2.getName().equals("suggestion") || o2.getName().equals("extGeneric"))
                    {
                        if(!bridgeContainsBoui(o2.getBridge("DAO"), mailobject.getBoui()))
                        {
                            o2.getBridge("DAO").add( mailobject.getBoui() );
                        }
                        //mailobject.getUpdateQueue().add( o2.getBoui() , boObjectUpdateQueue.MODE_SAVE );   
                    }
                }catch(Exception e)
                {
                    logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"), e);
                }
          }
          
          if(msgs[i].isDeliveredReceipt() || msgs[i].isReadReceipt())
          {
              if(treatReceipt(ctx,msgs[i], mailobject, objectToUpdate))
              {
//                logger.warn("Recibo Tratado com sucesso.");
                //se tratou com sucesso então não vou criar o objecto
                mailobject.setParameter("treatedAsReceipt", "true");
              }
              else
              {
                mailobject.getAttribute("folder").setValueLong(this.getMailFolder().getBoui());
              }
              validEmails.add(mailobject);
          }
          else
          {
            validEmails.add(mailobject);
          }
        }
      }
      
      String lastRead = null;
      boObject treatingEmail = null, auxObj;
      try
      {
          for (int i = 0; i < validEmails.size(); i++) 
          { 
            treatingEmail = ((boObject)validEmails.get(i));
            if(!"true".equals(treatingEmail.getParameter("treatedAsReceipt")))
            {                
                treatingEmail.update();                
            }
            else
            {//foi tratado como recibo
                auxObj = (boObject)objectToUpdate.get(String.valueOf(treatingEmail.getBoui()));
//                logger.warn("Update do recibo tratado com sucesso.");
                if(auxObj != null)
                {
                    auxObj.update();
                }
//                try
//                {
//                    mailfolder.getUpdateQueue().remove(treatingEmail.getBoui());
//                    treatingEmail.getAttribute("assignedQueue").getObject().getUpdateQueue().remove(treatingEmail.getBoui());
//                    boBridgeIterator it = treatingEmail.getBridge("RO").iterator();
//                    if(it != null)
//                    {
//                        bridgeHandler bh;
//                        int sv;
//                        while(it.next())
//                        {
//                            bh = it.currentRow().getObject().getBridge("RO");
//                            bh.beforeFirst();
//                            while(bh.next())
//                            {
//                                if(bh.getObject().getBoui() == treatingEmail.getBoui())
//                                {
//                                    sv = bh.getRow(); 
//                                    bh.remove();
//                                    bh.rows(sv-1);
//                                }
//                            }
//                        }
//                        treatingEmail.getBridge("RO").truncate();
//                    }
//                }
//                catch (Exception e)
//                {
//                    logger.warn(e);
//                }
            }
            lastRead = treatingEmail.getAttribute("messageid").getValueString();
            if (deleteMessages) this.deleteMessage(lastRead);
          }
          if(lastRead!=null && !"".equals(lastRead))
          {
            this.mailfolder.getAttribute("lastmessageid").setValueString(lastRead);
          }
      }
      catch (Exception e)
      {
        if(lastRead!=null && !"".equals(lastRead))
        {
            this.mailfolder.getAttribute("lastmessageid").setValueString(lastRead);    
        }
        if(treatingEmail != null)
        {
            try
            {
                logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName() + " Email(subject - messageid):" +treatingEmail.getAttribute("name").getValueString() +" - "+treatingEmail.getAttribute("messageid").getValueString() +"\n"+e.getMessage(), e);
            }
            catch (Exception _e)
            {
                logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
            }
        }
        else
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
        }
      }
      this.mailfolder.update();
      //delete the files in the server
      try
      {
          for (int i = 0; i < msgs.length; i++) 
          {
              for (int j=0;j<msgs[i].getAttach().size();j++)
              {
                Attach att=(Attach)msgs[i].getAttach().get(j);                      
                File auxf=new File(att.getLocation());
                if(auxf.exists())
                {
                    auxf.delete();
                }
              }
          }
      }
      catch (Exception e)
      {
          logger.warn(LoggerMessageLocalizer.getMessage("ERROR_REMOVING_TEMPORARY_FILES"), e);
      }
      return true;
    }
    catch (boRuntimeException e)
    {
      logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
      return false;
    }
    catch (RemoteException e)
    {
      logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
      return false;      
    }
    catch(MessagingException e)
    {
      logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
      return false;
    }   
    catch(IOException e)
    {
      logger.severe(LoggerMessageLocalizer.getMessage("ERROR_READING_MAILS_ACCOUNT_NAME")+" "+this.getPOPHost()+" "+this.getUserName()+"\n"+e.getMessage(), e);
      return false;
    }   
    
  } 


 private static boolean treatReceipt(EboContext ctx, mailMessage receiptEmail, boObject receiptBoMail, Hashtable objToUpdate)
 {
    try
    {
        Date d = new Date();
        String[] subjects = null;
        try
        {
            if(receiptEmail.getDataRecebido() != null)
            {                    
                d = mail.stringCompleteToDate(receiptEmail.getDataRecebido());                    
            }
            else
            {
//                logger.finer("Recibo sem data");
            }
        }
        catch (ParseException e)
        {
            logger.severe(e);
        }
        if(receiptEmail.getReceiptThread() != null && !"".equals(receiptEmail.getReceiptThread().trim()))
        {
            subjects = receiptEmail.getReceiptThread().split(";");
//            logger.warn(receiptEmail.getReceiptThread());
            boObjectList threadList;
            String attrName;
            String attrDt;

            if(receiptEmail.isReadReceipt())
            {
                attrName = "read_receipt";
                attrDt = "read";
            }
            else
            {
                attrName = "deliver_receipt";
                attrDt = "delivery";
            }
            return easyWay(ctx, receiptEmail, receiptBoMail, objToUpdate,subjects, d, attrName, attrDt) == true
                    ?
                    true: 
                hardWay(ctx, receiptEmail, receiptBoMail, objToUpdate,subjects, d, attrName, attrDt);
        }
    }
    catch(Exception ex)
    {
        logger.warn(ex);
    }
    return false;
 }

  private static boolean easyWay(EboContext ctx, mailMessage receiptEmail, 
                    boObject receiptBoMail, Hashtable objToUpdate, 
                    String[] subjects, Date d, String attrName, String attrDt) throws
  boRuntimeException
  {
//    logger.warn("EasyWay");
    bridgeHandler roBH = receiptBoMail.getBridge("RO");
    boBridgeIterator it = roBH.iterator();
    it.beforeFirst();
    String[] subjs;
    boObject aux, foundedObj=null;
    boolean found = false, end = false;
    while(it.next() && !end)
    {
        aux = it.currentRow().getObject();
//        logger.warn("EasyWay: " + aux.getBoui());
        if("email".equals(aux.getName()))
        {
            for (int j = 0; j < subjects.length; j++)
            {
                subjs = removeXEO(subjects[j]);
                for (int k = 0; k < subjs.length; k++) 
                {
                    if("1".equals(aux.getAttribute(attrName).getValueString()) &&
                        subjs[j].equalsIgnoreCase(aux.getAttribute("name").getValueString()))
                    {
                        if(!found)
                        {
//                            logger.warn("found um");
                            found = true;
                            foundedObj = aux;
                        }
                        else
                        {
//                            logger.warn("found mais que um");
                            found = false;
                            end = true;
                        }
                    }
                }
            }
        }
    }
    if(found)
    {
        setReceiptBridge(receiptEmail, receiptBoMail, objToUpdate, 
                    d, attrName, attrDt, foundedObj);
        return true;
    }
    return false;
  }

  private static boolean hardWay(EboContext ctx, mailMessage receiptEmail, 
                    boObject receiptBoMail, Hashtable objToUpdate, 
                    String[] subjects, Date d, String attrName, String attrDt) throws
  boRuntimeException
  {
//    logger.warn("HardWay");
    boObjectList threadList = null;
    long boui = 0;
    for (int j = 0; j < subjects.length; j++)
    {
        String[] subjs = removeXEO(subjects[j]);
        for (int k = 0; k < subjs.length; k++) 
        {
            logger.warn("SELECT email WHERE upper(name) = upper('"+ subjs[k] +"') and " + attrName +" = 1");
                
            threadList = boObjectList.list(ctx,
                            "SELECT email WHERE upper(name) = upper('"+ subjs[k] +"') and " + attrName +" = 1");
            boui = 0;
            threadList.beforeFirst();
            boObject aux, foundedObj = null;
            boolean found = false, end = false;
            boolean auxB;
            while(threadList.next() && !end)
            {
                aux = threadList.getObject();
                auxB = false;
                if(bridgeContainsBoui(receiptBoMail.getBridge("RO"), aux.getBoui()))
                {
//                    logger.warn("bridge contem");
                    auxB = true;
                }
                else
                {
//                    logger.warn("bridge nao contem");
                }
                if(auxB && !found)
                {
//                    logger.warn("found um");
                    found = true;
                    foundedObj = aux;
                }
                else if(auxB && found)
                {
//                    logger.warn("found mais que um");
                    found = false;
                    end = true;
                }
            }
            if(found)
            {
                setReceiptBridge(receiptEmail, receiptBoMail, objToUpdate, 
                    d, attrName, attrDt, foundedObj);
                return true;
            }
        }
    }
    return false;
  }
  private static void setReceiptBridge(mailMessage receiptEmail, 
                    boObject receiptBoMail, Hashtable objToUpdate, 
                    Date d, String attrName, String attrDt, boObject foundedObj) throws boRuntimeException
  {
    String fullName = receiptEmail.getFrom() == null ? "[Sem remetente]":receiptEmail.getFrom().getFullName();
    bridgeHandler bh = foundedObj.getBridge("receipts");
    boBridgeIterator it = bh.iterator();
    it.beforeFirst();
    boolean foundInBridge = false;
    boObject boj;
    while(it.next())
    {
        boj = it.currentRow().getObject();
        if(fullName.equalsIgnoreCase(boj.getAttribute("recipient").getValueString()))
        {
            if(boj.getAttribute(attrDt).getValueDate() == null)
            {
                boj.getAttribute(attrDt).setValueDate(d);                            
                foundInBridge = true;
            }
        }
    }
    if(!foundInBridge)
    {
        boObject receipt = bh.addNewObject();
        receipt.getAttribute("recipient").setValueString(fullName);
        bh.getObject().getAttribute(attrDt).setValueDate(d);
    }
    objToUpdate.put(String.valueOf(receiptBoMail.getBoui()), foundedObj);
  }

  private static String[] removeXEO(String subject)
  {
    int start = -1;
    String[] toRet = null;
    ArrayList l = new ArrayList();
    if((start = subject.toUpperCase().indexOf(" - [XEO")) != -1)
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

  private static boolean toContainsFrom(boObject mail, boObject receipt)
  {
    String email1 = getFromObject(mail);
    String email2 = getToObject(receipt);    
    if(email1 != null && email2 != null)
    {
        String[] aux = email2.split(";");
        for (int i = 0; i < aux.length; i++) 
        {
            if(aux[i].trim().toUpperCase().equals(email1.trim().toUpperCase()))
            {
                return true;
            }
        }
    }
    return false;
  }

  private static String getFromObject(boObject mail)
  {
    try
    {
        if(mail.getAttribute("mail_from").getObject() != null)
        {
            boObject runtime = mail.getAttribute("mail_from").getObject();
            if(runtime.getAttribute("refObj").getObject() != null)
            {
               return runtime.getAttribute("refObj").getValueString(); 
            }
            else
            {
               return runtime.getAttribute("email").getValueString();
            }
        }
        
        if(mail.getAttribute("assignedQueue").getObject() != null )
        {
            return mail.getAttribute("assignedQueue").getObject()
                    .getAttribute("email").getValueString();
        }
    }
    catch (boRuntimeException e)
    {
        return null;
    }
    return null;
  }

  private static String getToObject(boObject mail)
  {
    String[] br = {"mail_to", "mail_cc", "mail_bcc"};
    try
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < br.length; i++) 
        {
            bridgeHandler bh = mail.getBridge(br[i]);
            bh.beforeFirst();
            while(bh.next())
            {
                boObject runtime = bh.getObject();
                if(runtime.getAttribute("refObj").getObject() != null)
                {
                   sb.append(runtime.getAttribute("refObj").getValueString()).append(";"); 
                }
                else
                {
                   sb.append(runtime.getAttribute("email").getValueString()).append(";");
                }
            }
        }
        return sb.toString();
    }
    catch (boRuntimeException e)
    {
        return null;
    }
  }

  public static boolean bridgeContainsBoui(bridgeHandler bh, long boui) throws boRuntimeException
  {
    boolean toRet = false;
    int sv = bh.getRow();
    bh.beforeFirst();
    while(bh.next() && !toRet)
    {
        if(bh.getObject().getBoui() == boui)
        {
            toRet = true;
        }
    }
    bh.moveTo(sv);
    return toRet;
  }
  
  private boObject setMailRecipient(mailAddress email,String attributeToAssociate,boObject mailobject)
  {
    try
    {
      //Check if emailaddress exists as a performer and if so associates the email with it              
      boolean checkOk=this.checkEmailAddress("Select iXEOUser where iXEOUser.emailAccounts.email='"+email.getEmail()+"'",attributeToAssociate,mailobject,true);
      if (checkOk) return mailobject;

      //Check if emailaddress exists as a group and if so associates the email with it              
      checkOk=this.checkEmailAddress("Select Ebo_Group where Ebo_Group.emailAccounts.email='"+email.getEmail()+"'",attributeToAssociate,mailobject,true);
      if (checkOk) return mailobject;
 
      //Check if emailaddress exists as a Role and if so associates the email with it              
      checkOk=this.checkEmailAddress("Select Ebo_Role where Ebo_Role.emailAccounts.email='"+email.getEmail()+"'",attributeToAssociate,mailobject,true);
      if (checkOk) return mailobject;

      //Check if emailaddress exists as a workQueue and if so associates the email with it              
      checkOk=this.checkEmailAddress("Select workQueue where workQueue.emailAccounts.email='"+email.getEmail()+"'",attributeToAssociate,mailobject,true);
      if (checkOk) return mailobject;

      //Check if emailaddress exists as a contact and if so associates the email with it        
      checkOk=this.checkEmailAddress("Select Ebo_Contact where Ebo_Contact.email='"+email.getEmail()+"'",attributeToAssociate,mailobject,false);
      if (checkOk) return mailobject;
          
      boManagerLocal man=boObject.getBoManager();
      boObject recipient=man.loadObject(ctx,"recipient","email='"+email.getEmail()+"' and classname='recipient'");      
      if (!recipient.exists())
      {
        recipient=man.createObject(ctx,"recipient");                  
        recipient.getAttribute("email").setValueString(email.getEmail());
        recipient.getAttribute("name").setValueString(email.getName());        
        recipient.update();
        
      }
      else
      {
        String emailAux = recipient.getAttribute("email").getValueString();
        String nameAux = recipient.getAttribute("name").getValueString();
        boolean update = false;
        if((emailAux == null || emailAux.trim().length() == 0) && email.getEmail() != null)
        {
            recipient.getAttribute("email").setValueString(email.getEmail());
            update = true;
        }
        if((nameAux == null || nameAux.trim().length() == 0) && email.getName() != null)
        {
            recipient.getAttribute("name").setValueString(email.getName());
            update = true;
        }
        if(update)
        {
            recipient.update();
        }
      }
      if("mail_from".equals(attributeToAssociate))
      {
        mailobject.getAttribute(attributeToAssociate).setValueLong(recipient.getBoui());
      }
      else
      {
        mailobject.getBridge(attributeToAssociate).add(recipient.getBoui());
      }
      
      return mailobject;
    }
    catch(Exception e)
    {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
      return null;
    }
  }
  
  private boolean checkEmailAddress(String boql,String attributeToAssociate,boObject mailobject,boolean newmail) throws boRuntimeException
  {
    boolean ret=false;
    boObjectList bobj=boObjectList.list(ctx,boql);
    bobj.beforeFirst();
    while (bobj.next())
    {
      ret=true;
      boObject object=bobj.getObject();    
      if (newmail && !(this.emailstoCreate.get(new Long(object.getBoui()))!=null))
        this.emailstoCreate.put(new Long(object.getBoui()),new Long(object.getBoui()));
      if("mail_from".equals(attributeToAssociate))
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
   
  /*
  private boolean isXEOMessage(String toProcess,boObject mailobject) throws boRuntimeException
  {
    String toFind="boui:#.*?#";
    
    
    String toFind="\\[XEO.*?\\]";
     
    Pattern p=Pattern.compile(toFind);
    Matcher m=p.matcher(toProcess);
    boolean isXEOMessage=false;
    if (m.find())
    {
      isXEOMessage=true;
      String finded=m.group();
      String boui=finded.substring(finded.indexOf("#")+1,finded.lastIndexOf("#"));
      boObject relatedMail=boObject.getBoManager().loadObject(ctx,new Long(boui).longValue());        
      //it finds the boui that comes in the message
      if (relatedMail.exists())
      {
        mailobject.getAttribute("process").setValueLong(relatedMail.getAttribute("process").getValueLong());

        bridgeHandler bridge=relatedMail.getBridge("RO");
        bridge.beforeFirst();      
        while(bridge.next()) {        
           mailobject.getBridge("RO").add(bridge.getObject().getBoui());
        }
      }      
      return true;     
    }
    else return false;    
  }
  */
  
   private boolean isXEOMessage(String toProcess,boObject mailobject, ArrayList relatedBoui ) throws boRuntimeException
  {
    String toFind="\\[XEO.*?\\]";
     
    Pattern p=Pattern.compile(toFind);
    Matcher m=p.matcher(toProcess);
    boolean isXEOMessage=false;
    ArrayList bouis= new ArrayList();
    String sboui = null;
    long boui = 0;
    while (m.find())
    {
         sboui = toProcess.substring( m.start()+4, m.end()-1 );
         try
         {
             boui= ClassUtils.convertToLong( sboui );
         }
         catch (Exception e)
         {
             boui = 0;
         }
         if ( boui > 0 )
         {  
           if ( !bouis.contains(sboui) )
           {
                bouis.add( sboui );
           }
         }
    }
    
    if ( bouis.size() > 0 )
    {
        
     //   long[] bouiList= new long[ bouis.size() ];
        for (int i = 0; i < bouis.size() ; i++) 
        {
             boui= ClassUtils.convertToLong( (String)bouis.get(i) );
             
             if ( boui > 0 )
             {
                 try
                 {
                     boObject o2 = boObject.getBoManager().loadObject( mailobject.getEboContext(), boui  );                     
                     
                     if ( o2!=null )
                     {
                        relatedBoui.add( o2  );
                        isXEOMessage = true;
                     }
                 }
                 catch(Exception e)
                 {
                    logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"), e);
                 }
             }
        }
    }
    return isXEOMessage;
        
  }
  public static boolean addFolderInBox(boObject obj) throws boRuntimeException
  {
      boolean includeinbox=obj.getAttribute("includeinbox").getValueString().equals("1")?true:false;      	
      if (includeinbox)	{
	        bridgeHandler bridge=obj.getBridge("folder");
	        bridge.beforeFirst();      
	        boolean found=false;
	        while(bridge.next()) {
	          boObject folder=bridge.getObject();       
		        String foldername=bridge.getObject().getAttribute("folder").getValueString();
		        if (foldername!=null && !foldername.equals("") && foldername.equalsIgnoreCase("inbox")) found=true;
		      }      
	      if (!found)
          {        
              boObject folder=obj.getBoManager().createObject(obj.getEboContext(),"mailAccount_folder");                  
              folder.getAttribute("folder").setValueString("INBOX");
              obj.getBridge("folder").add(folder.getBoui()); 
          }
    }
	else
	{
        bridgeHandler bridge=obj.getBridge("folder");
        bridge.beforeFirst();      
        boolean found=false;
        while(bridge.next()) {
          boObject folder=bridge.getObject();       
            String foldername=bridge.getObject().getAttribute("folder").getValueString();
            if (foldername!=null && !foldername.equals("") && foldername.equalsIgnoreCase("inbox")) bridge.remove();
                 //           folder.destroy();
        //obj.update();				
          }	   	  	
    }
    return true;
  }

    public static void setForwardFields(boObject mailobject, boObject oldmailobject, int type) throws boRuntimeException
    {
        String[] cleanBridges = new String[]{"mail_to", "mail_cc", "mail_bcc"};
        String[] showBridges = new String[]{"mail_to", "mail_cc"};
        ArrayList myEmails = getMyEmails(oldmailobject);
        ArrayList replyTo = new ArrayList(5);
        ArrayList replyCc = new ArrayList(5);
        StringBuffer de = new StringBuffer();
        //de
        de.append("<br>-----Mensagem original-----<p>")
        .append("<ul>")
        .append("<li><b>De:</b> ");
        boObject runtimeAddres = oldmailobject.getAttribute("mail_from").getObject();
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
        mailobject.getAttribute("mail_from").setValueObject(null);
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
                    if("mail_to".equals(showBridges[i]))
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
                        if("mail_to".equals(showBridges[i]))
                            replyTo.add(runtimeAddres.getAttribute("refObj").getObject());
                        else
                            replyCc.add(runtimeAddres.getAttribute("refObj").getObject());
                    }
                    else
                    {
                        if("mail_to".equals(showBridges[i]))
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
            mailobject.getAttribute("name").setValueString(subject);
        }

        for (int i = 0; i < cleanBridges.length; i++) 
        {
            bridge=mailobject.getBridge(cleanBridges[i]);
            bridge.truncate();
        }
        
        if(type != FORWARD)
        {
            setSendingAddress(mailobject, "mail_to", replyTo, myEmails);
            setSendingAddress(mailobject, "mail_cc", replyCc, myEmails);
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
        mailobject.getAttribute("already_send").setValueObject(null);
        mailobject.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).setValueString(WF.STATE_CREATE);
        mailobject.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(WF.STATE_STARTED);
        mailobject.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(WF.STATE_GREEN);
        //RO
        bridgeHandler bh = mailobject.getBridge("RO");
        bh.beforeFirst();
        int sv;
        while(bh.next())
        {
            sv = bh.getRow();
            bh.remove();
            bh.moveTo(sv - 1);
        }
        mailobject.setParameter("parentMail", String.valueOf(oldmailobject.getBoui()));

//        if(!bridgeContainsBoui(oldmailobject.getBridge("RO"), mailobject.getBoui()))
//        {
//            oldmailobject.getBridge("RO").add( mailobject.getBoui() );
//        }
        
        //
        try
        {
            boObject[] parents = oldmailobject.getParents();
            if(parents != null && parents.length == 1)
            {
                if(parents[0].getBridge("DAO") != null)
                {
                   bh = parents[0].getBridge("DAO");
                   int svPos = bh.getRow();
                   bh.beforeFirst();
                   boolean found = false;
                   while(bh.next())
                   {
                     if(bh.getObject() != null && 
                        bh.getObject().getBoui() == mailobject.getBoui())
                     {
                        found = true;
                     }
                   }
                   if(!found)
                   {
                    bh.add(mailobject.getBoui());
                   } 
                }
                
            }
        }
        catch (Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DUE_TO_A_CONNECTION_TO_AN_INEXISTENT_OBJECT"), e);
        }
        
        
    }
  
    private static void setSendingAddress(boObject mailobject, String bridgeName, ArrayList replyTo, ArrayList myEmails) throws boRuntimeException
    {
        bridgeHandler bridge = mailobject.getBridge(bridgeName);
        boObject runtime;
        for (int i = 0; i < replyTo.size(); i++) 
        {
            if(replyTo.get(i) instanceof String)
            {
                if(!myEmails.contains(replyTo.get(i)))
                {
                    runtime = boObject.getBoManager().createObject(mailobject.getEboContext(), "runtimeAddress");
                    runtime.getAttribute("email").setValueObject(replyTo.get(i));
                    bridge.add(runtime.getBoui());
                }
            }
            else 
            {
                if(((boObject)replyTo.get(i)).getAttribute("email") != null)
                {
                    if(!myEmails.contains(((boObject)replyTo.get(i)).getAttribute("email").getValueString()))
                    {
                        bridge.add(((boObject)replyTo.get(i)).getBoui());
                    }
                }
            }
        }
  }
  
  private static ArrayList getMyEmails(boObject email) throws boRuntimeException
  {
    boObject obj = email.getAttribute("assignedQueue").getObject();
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
  
  public static boolean checkInBox(boObject obj) throws boRuntimeException
  {
    /*String foldername=obj.getAttribute("folder").getValueString(); 
    if (foldername.equalsIgnoreCase("inbox"))
    {
      boObject folder=boObject.getBoManager().loadObject(obj.getEboContext(),"Select mailAccount_folder where mailAccount_folder.folder='INBOX'");
      if (folder.exists())
      { 
        obj.addErrorMessage("A pasta Inbox não pode ser criada é uma pasta de sistema");
        throw new boRuntimeException("A pasta Inbox não pode ser criada é uma pasta de sistema","",null);
      } 
    }  */
    return true; 
  }
  
}