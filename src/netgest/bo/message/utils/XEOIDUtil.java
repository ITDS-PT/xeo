/*Enconding=UTF-8*/
package netgest.bo.message.utils;

import java.util.Calendar;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.message.Address;
import netgest.bo.message.Message;

import java.util.Date;
import netgest.bo.message.server.mail.MailMessage;
import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class XEOIDUtil
{
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.utils.XEOIDUtil");
    /**
     *
     * @Company Enlace3
     * @since
     */
    private XEOIDUtil()
    {
    }

    public static String MessageXEOID(MailMessage message)
    {
        return String.valueOf(verifyValue(message).hashCode());
    }

    public static String MessageXEOID(MailMessage[] messages)
    {
        return String.valueOf(verifyValue(messages).hashCode());
    }

    public static String addressXEOID(Address address)
    {
        return String.valueOf(verifyValue(address).hashCode());
    }

    public static String addressXEOID(Address[] addresses)
    {
        return String.valueOf(verifyValue(addresses).hashCode());
    }

    public static String addressXEOID(Attach attach)
    {
        return String.valueOf(verifyValue(attach).hashCode());
    }

    public static String addressXEOID(Attach[] attaches)
    {
        return String.valueOf(verifyValue(attaches).hashCode());
    }

    private static String verifyValue(String value)
    {
        try
        {
            if(value == null || value.length() == 0) return "";
            String aux = new String(value);
            Calendar c = Calendar.getInstance();
            String year = String.valueOf(c.get(Calendar.YEAR));
            if(aux.indexOf("_" + year +"_") >= 0)
            {
                int pos = 0;
                int index = -1;
                while((index=aux.indexOf("_" + year +"_", pos)) >= 0)
                {
                    if(aux.length() >= (index + 24) &&
                       aux.charAt(index + 5) == '_' &&
                       aux.charAt(index + 5 + 2  + 1) == '_' &&
                       aux.charAt(index + 5 + 4 + 2) == '_' &&
                       aux.charAt(index + 5 + 6 + 3) == '_' &&
                       aux.charAt(index + 5 + 8 + 4) == '_' &&
                       aux.charAt(index + 5 + 10 + 5) == '_'
                       )
                    {
                        aux = aux.substring(0, index) + aux.substring(index + 24);
                    }
                    pos++;
                }
            }
            return (aux == null) ? "" : aux;
        }
        catch (Exception e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_TREATING")+": " + value, e);
        }
        
        return (value == null) ? "" : value;
    }

    private static String verifyValue(Address[] address)
    {
        StringBuffer sb = new StringBuffer("");

        if (address != null)
        {
            for (int i = 0; i < address.length; i++)
            {
                sb.append(verifyValue(address[i]));
            }
        }

        return sb.toString();
    }

    private static String verifyValue(Address address)
    {
        StringBuffer sb = new StringBuffer("");

        if (address != null)
        {
            sb.append(verifyValue(address.getFullAddress()))
              .append(verifyValue(address.getName()))
              .append(verifyValue(address.getAddress()))
              .append(verifyValue(address.getPostalCode()))
              .append(verifyValue(address.getLocation()))
              .append(verifyValue(address.getCountry())).append(verifyValue(
                    address.getCity()));
        }

        return sb.toString();
    }

    private static String verifyValue(MailMessage[] messages)
    {
        StringBuffer sb = new StringBuffer("");

        if (messages != null)
        {
            for (int i = 0; i < messages.length; i++)
            {
                sb.append(verifyValue(messages[i]));
            }
        }

        return sb.toString();
    }

    private static String verifyValue(MailMessage message)
    {
        StringBuffer sb = new StringBuffer("");

        if (message != null)
        {
            sb.append(verifyValue(message.getFrom()))
              .append(verifyValue(message.getTO()))
              .append(verifyValue(message.getCC()))
              .append(verifyValue(message.getBCC()))
              .append(verifyValue(message.getAttach()))
              .append(verifyValue(message.getContent() == null ? "":message.getContent()))
              .append(verifyValue(message.getContentHTML() == null ? "":message.getContentHTML()))
              .append(verifyValue(message.getSubject()))
              .append(verifyValue(message.getPriority())).append(verifyValue(
                    message.getContextThread()));
        }

        return sb.toString();
    }

    private static String verifyValue(Attach[] attaches)
    {
        StringBuffer sb = new StringBuffer("");

        if (attaches != null)
        {
            for (int i = 0; i < attaches.length; i++)
            {
                sb.append(verifyValue(attaches[i]));
            }
        }

        return sb.toString();
    }

    private static String verifyValue(Attach attach)
    {
        StringBuffer sb = new StringBuffer("");

        if (attach != null)
        {
            sb.append(verifyValue(attach.getId()))
              .append(verifyValue(attach.getInlineID()))
              .append(verifyValue(attach.getLocation())).append(verifyValue(
                    attach.getName()));
        }

        return sb.toString();
    }

    private static String verifyValue(Date value)
    {
        return (value == null) ? "" : value.toString();
    }

    private static String verifyValue(int value)
    {
        return String.valueOf(value);
    }

    private static String verifyValue(long value)
    {
        return String.valueOf(value);
    }
}
