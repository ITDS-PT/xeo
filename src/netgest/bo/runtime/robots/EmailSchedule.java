/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.system.boMail;
import netgest.utils.*;
import netgest.bo.system.Logger;

public class EmailSchedule implements boSchedule
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.EmailSchedule");
  public EmailSchedule()
  {
  }
  
  public void setParameter(String parameter )
  {
    
  }
  
  public boolean doWork(EboContext ctx, boObject objectSchedule ) throws boRuntimeException
  {
    boObject object = null;
    boObjectList bobj=boObjectList.list(ctx,"Select mailAccount where active = 1 or active is null");
    bobj.beforeFirst(); 
    while (bobj.next())
    {
      try
      {
          object=bobj.getObject();
          
          boMail mail=new boMail(ctx);            
          mail.setPOPHost(object.getAttribute("receivehost").getValueString());
          String protocol=object.getAttribute("mailprotocol").getValueLong()==1?"pop3":"imap";
          mail.setProtocol(protocol);      
          mail.setUserName(object.getAttribute("username").getValueString());
          mail.setUserPass(object.getAttribute("password").getValueString());
          boolean delmsg=object.getAttribute("deletemessages").getValueString().equals("1")?true:false;
          mail.setDeleteMessages(delmsg);
          mail.setSMTPHost(object.getAttribute("smtphost").getValueString());
          boolean onlyXEO=object.getAttribute("processonlyXEOMessages").getValueString().equals("1")?true:false;      
          bridgeHandler bridge=object.getBridge("folder");
          bridge.beforeFirst();      
          while(bridge.next()) {
            boObject folder=bridge.getObject();       
            String foldername=bridge.getObject().getAttribute("folder").getValueString();
            if (foldername!=null && !foldername.equals(""))
            {
                if(object.getParent() != null)
                {
                    mail.setMailAccountOwner(object.getParent());
                }
                mail.setMailAccount(object);
                mail.setMailFolder(folder);
                mail.ReadMail(mail.getDeleteMessages(),onlyXEO,foldername);          
            }
          }      
          
          mail.setMessageOrder(mail.ORDER_DESC);
      }
      catch(Exception e)
      {
        //write the exception to the log
        //and continue to next mailAccount
         logger.severe(LoggerMessageLocalizer.getMessage("ERROR_SYNCHRONIZING_MAILBOX_FROM_USER")+": " + object.getAttribute("username").getValueString(), e);
      }
    }        
    return true;
  }
}