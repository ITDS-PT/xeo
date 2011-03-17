/*Enconding=UTF-8*/
package netgest.bo.message;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.*;
import netgest.bo.*;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.message.server.mail.MailAddress;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.utils.XEOUserUtils;
import netgest.bo.system.Logger;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class GarbageController 
{

    public static ArrayList tableEmails = null;
        //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.GarbageController");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public GarbageController()
    {
    }
    
    public static boolean isSpamEmail(boObject mailAccount, String email)
    {
        try
        {
            if(email != null)
            {
                boBridgeIterator it = mailAccount.getBridge("spam").iterator();
                it.beforeFirst();
                while(it.next())
                {
                    if(email.equalsIgnoreCase(it.currentRow().getObject().getAttribute("email").getValueString()))
                    {
                        logger.finer(LoggerMessageLocalizer.getMessage("ITS_SPAM"));
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.severe(e);
        }
        logger.finest(LoggerMessageLocalizer.getMessage("NOT_SPAM"));
        return false;
    }
    
    public static boolean isSpamServer(boObject mailAccount, String email)
    {
        try
        {
            if(email != null)
            {
                String server = MessageUtils.getEmailServer(email);
                server = server != null ? server:email;
                boBridgeIterator it = mailAccount.getBridge("spam").iterator();
                it.beforeFirst();
                while(it.next())
                {
                    if(server.equalsIgnoreCase(it.currentRow().getObject().getAttribute("server").getValueString()))
                    {
                       logger.finer(LoggerMessageLocalizer.getMessage("ITS_SPAM"));
                        return true;
                    }
                }
            }
        }
        catch (Exception e)
        {
            logger.severe(e);
        }
        logger.finest(LoggerMessageLocalizer.getMessage("NOT_SPAM"));
        return false;
    }
    
    public static boolean hasReported(boObject msg, boObject deliverMsg)
    {
        String email = null;
        try
        {
            if(msg.getAttribute("from") != null)
            {
                if(msg.getAttribute("from").getObject().getAttribute("email").getValueString() != null)
                {
                    email = msg.getAttribute("from").getObject().getAttribute("email").getValueString();
                    if(email != null && email.trim().length() > 0)
                    {
                        email = email.trim();
                        if(email.startsWith("'"))
                        {
                            email = email.substring(1,email.length());
                        }
                        if(email.endsWith("'"))
                        {
                            email = email.substring(0,email.length()-1);
                        }
                        MailAddress mA = new MailAddress(email);
                        return hasReported(mA.getEmail(), deliverMsg);
                    }
                }
            }
        }
        catch (Exception e)
        {
            if(email != null)
            {
                logger.finest(LoggerMessageLocalizer.getMessage("ERROR_RESOLVING_THE_EMAIL")+": " + email);
            }
            try{logger.finest("",e);}catch(Exception _e){};
        }
        return false;
    }
    
    public static boolean hasReported(String emailFrom, boObject deliverMsg)
    {
        try
        {
            boObject refObj = deliverMsg.getAttribute("refObj").getObject();
            String server = emailFrom.indexOf("@") > -1 ? emailFrom.substring(emailFrom.indexOf("@") + 1): emailFrom;
            boolean found = false;
            if(refObj != null)
            {
                if(XEOUserUtils.isXEOUser(refObj) || 
                    "workQueue".equals(refObj.getName()) || 
                    "Ebo_Group".equals(refObj.getName()) || 
                    "Ebo_Role".equals(refObj.getName()) ||
                    
                    "workQueue".equals(refObj.getBoDefinition().getBoSuperBo()) || 
                    "Ebo_Group".equals(refObj.getBoDefinition().getBoSuperBo()) || 
                    "Ebo_Role".equals(refObj.getBoDefinition().getBoSuperBo()) 
                    
                    )
                {
                    boBridgeIterator it = refObj.getBridge("emailAccounts").iterator();
                    it.beforeFirst();
                    boObject mailAccount;
                    while(it.next() && !found)
                    {
                        mailAccount = it.currentRow().getObject();
//                        if(mailAccount.getAttribute("email").getValueString().equalsIgnoreCase(email))
//                        {
                            found = true;
                            boBridgeIterator it2 = mailAccount.getBridge("spam").iterator();
                            boObject spam;
                            it2.beforeFirst();
                            while(it2.next())
                            {
                                spam = it2.currentRow().getObject();
                                if(emailFrom.equalsIgnoreCase(spam.getAttribute("email").getValueString()))
                                {
                                    return true;
                                }
                                if(server.equalsIgnoreCase(spam.getAttribute("server").getValueString()))
                                {
                                    return true;
                                }
                            }
//                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            try{logger.severe("",e);}catch(Exception _e){};
        }
        return false;
    }
    
//    public static void addEmail(String email)
//    {
//        if(!tableEmails.contains(email))
//        {
//            tableEmails.add(email);
//        }
//    }
//    
//    static
//    {
//        tableEmails = new ArrayList();
//        String ngtHome = boConfig.getNgtHome();
//        File f = new File(ngtHome +"spamEmails.txt");
//        if(f.exists())
//        {
//            BufferedReader br = null;
//            InputStreamReader ir = null;
//            FileInputStream fi = null;
//            try
//            {
//                fi = new FileInputStream(f);
//                ir = new InputStreamReader(fi);
//                br = new BufferedReader(new InputStreamReader(fi));
//                String line = null, aux = null;
//                StringTokenizer st = null;
//                while((line = br.readLine()) != null)
//                {
//                    st = new StringTokenizer(line, ";");
//                    while(st.hasMoreElements())
//                    {
//                        aux = (String)st.nextElement();
//                        if(!tableEmails.contains(aux.toUpperCase()))
//                        {
//                            tableEmails.add(aux.toUpperCase());
//                        }
//                    }
//                }
//            }
//            catch (Exception e)
//            {
//                logger.severe(e);
//            }
//        }
//    }
}