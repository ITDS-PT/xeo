/*Enconding=UTF-8*/
package netgest.bo.message.server.mail;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.message.Address;
import netgest.bo.message.utils.XEOIDUtil;

import netgest.bo.system.Logger;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;


public class MailAddress implements Address
{
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.media.mail.EmailAddress");
    private String name;
    private String email;
    private String fullName;

    public MailAddress(String fullemail)
    {
        try
        {
            InternetAddress ia = new InternetAddress(fullemail);
            this.name = ia.getPersonal();
            this.email = ia.getAddress();
            this.fullName = fullemail;

            if ((this.name == null) && (this.email != null))
            {
                this.name = fullemail;
            }
        }
         catch (Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_RESOLVING_THE_EMAIL_ADDRESS")+" (" + fullemail +
                ")", e);
        }

        if (email == null)
        {
            this.name = fullemail;
            this.email = fullemail;
            this.fullName = fullemail;

            if (fullemail.indexOf("<") != -1)
            {
                name = fullemail.substring(0, fullemail.indexOf("<"));
                email = fullemail.substring(fullemail.indexOf("<") + 1,
                        fullemail.indexOf(">"));
            }
        }
    }

    public MailAddress(String name, String email)
    {
        this.name = name;
        this.email = email;        
    }

    public void setName(String newName)
    {
        name = newName;
    }

    public void setEmail(String newEmail)
    {
        email = newEmail;
    }

    public void setFullName(String newFullName)
    {
        fullName = newFullName;
    }

    public String getName()
    {
        if(name != null && !"".equals(name)) return name;
        if(fullName == null || "".equals(fullName)) return null;
        try
        {
            InternetAddress ia = new InternetAddress(fullName);
            this.name = ia.getPersonal();
            this.email = ia.getAddress();
        }
        catch (Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_RESOLVING_THE_EMAIL_ADDRESS")+" (" + fullName +
                ")", e);
        }

        if (email == null)
        {
            this.name = fullName;
            this.email = fullName;
            this.fullName = fullName;

            if (fullName.indexOf("<") != -1)
            {
                name = fullName.substring(0, fullName.indexOf("<"));
                email = fullName.substring(fullName.indexOf("<") + 1,
                        fullName.indexOf(">"));
            }
        }
        return name;
    }

    public String getAddress()
    {
        if(email != null && !"".equals(email)) return email;
        if(fullName == null || "".equals(fullName)) return null;
        try
        {
            InternetAddress ia = new InternetAddress(fullName);
            this.name = ia.getPersonal();
            this.email = ia.getAddress();
        }
        catch (Exception e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_RESOLVING_THE_EMAIL_ADDRESS")+" (" + fullName +
                ")", e);
        }

        if (email == null)
        {
            this.name = fullName;
            this.email = fullName;
            this.fullName = fullName;

            if (fullName.indexOf("<") != -1)
            {
                name = fullName.substring(0, fullName.indexOf("<"));
                email = fullName.substring(fullName.indexOf("<") + 1,
                        fullName.indexOf(">"));
            }
        }
        return email;
    }

    public String getEmail()
    {
        return getAddress();
    }

    public String getFullName()
    {
        return getFullAddress();
    }

    public String getFullAddress()
    {
        if(fullName !=null && !"".equals(fullName)) return fullName;
        if(email == null || "".equals(email)) return null;
        if(name != null)
        {
            this.fullName = "\""+ name +"\"" + " <" + email + ">";
        }
        else
        {
            this.fullName = email;
        }
        return fullName;
    }

    public String getXEOID()
    {
        return XEOIDUtil.addressXEOID(this);
    }

    public String getPostalCode()
    {
        return null;
    }

    public String getLocation()
    {
        return null;
    }

    public String getCountry()
    {
        return null;
    }

    public String getCity()
    {
        return null;
    }
}
