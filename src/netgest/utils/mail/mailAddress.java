/*Enconding=UTF-8*/
package netgest.utils.mail;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.Logger;

public class mailAddress 
{
  private String Name;
  private String Email;
  private String fullName;
  private static Logger logger = Logger.getLogger("netgest.utils.mail.mailAddress");

  public mailAddress(String fullemail)
  {
      try
      {
          InternetAddress ia = new InternetAddress(fullemail);
          this.Name = ia.getPersonal();
          this.Email = ia.getAddress();
          this.fullName=fullemail;
          if(this.Name == null && this.Email != null)
          {
            this.Name = fullemail;
          }
      }
      catch (Exception e)
      {
          logger.warn(LoggerMessageLocalizer.getMessage("ERROR_RESOLVING_THE_EMAIL_ADDRESS")+" (" + fullemail +")", e);
      }
      if(Email == null)
      {
          this.Name=fullemail;
          this.Email=fullemail;
          this.fullName=fullemail;
          if (fullemail.indexOf("<")!=-1)
          {
            Name=fullemail.substring(0,fullemail.indexOf("<"));
            Email=fullemail.substring(fullemail.indexOf("<")+1,fullemail.indexOf(">"));
          }
      }
  }
  public mailAddress(String name,String email)
  {
    this.Name=name;
    this.Email=email;
  }

  public String getName()
  {
    return Name;
  }

  public void setName(String newName)
  {
    Name = newName;
  }

  public String getEmail()
  {
    return Email;
  }

  public void setEmail(String newEmail)
  {
    Email = newEmail;
  }

  public String getFullName()
  {
    return fullName;
  }

  public void setFullName(String newFullName)
  {
    fullName = newFullName;
  }
}