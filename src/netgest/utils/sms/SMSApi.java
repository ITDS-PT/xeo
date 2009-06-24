package netgest.utils.sms;
import netgest.bo.boConfig;
import netgest.bo.system.boApplication;

import netgest.utils.StringUtils;
import netgest.utils.mail.mail;
import netgest.utils.mail.mailAddress;
import netgest.utils.mail.mailMessage;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;

import org.apache.log4j.Logger;



public class SMSApi 
{
    private static SMSOperator[] smsOperators = null;
    //logger
    private static Logger logger = Logger.getLogger("netgest.utils.sms.SMSApi");
    

    public static SMSOperator[] getSMSOperators() throws RuntimeException
    {
        if (smsOperators == null)
        {
            ngtXMLHandler xnode;       
            try {
                boApplication.getApplicationFromStaticContext("XEO").addAContextToThread();
                ngtXMLHandler root = new ngtXMLHandler(ngtXMLUtils.loadXMLFile( boConfig.getNgtHome()+"boconfig.xml" ).getDocumentElement());
                xnode = root.getChildNode("SMS");
            } catch (Exception e) {
                xnode = null;   
            }
            
            if (xnode == null) 
            {
                throw new RuntimeException("Não foi indicado no ficheiro boconfig.xml a localização dos proxys SMS.");
            }
            
            ngtXMLHandler[] childs = xnode.getChildNodes();
            SMSOperator[] result = new SMSOperator[childs.length];
            for (int i = 0; i <childs.length; i++)
            {
                String name = childs[i].getAttribute("name");
                int prefix;
                if (childs[i].getAttribute("prefix") == null)
                    prefix = -1;
                else
                    prefix = Integer.parseInt(childs[i].getAttribute("prefix"));
                String proxyIP = childs[i].getText();
                result[i] = new SMSApi.SMSOperator(name, prefix, proxyIP);
            }
                           
            smsOperators = result;
        }
        return smsOperators;
    }
    
    private static String getProxyIPbyPrefix(int prefix) 
    {
        SMSOperator[] smso = getSMSOperators();
        for (int i = 0; i < smso.length; i++)
        {
            if (smso[i].prefix == prefix)
                return smso[i].proxyIP;
        }
        return null;
    }

     
     public static boolean send(String number, String text)
     {
        long ti = 0, tf = 0;
        try
        {
            ti = System.currentTimeMillis();
            mail m = new mail();

            if (number.startsWith("96"))
                m.setSMTPHost(getProxyIPbyPrefix(96));
            else
                m.setSMTPHost(getProxyIPbyPrefix(-1));
                      
            
            mailMessage msg = new mailMessage();
            
            msg.setFrom(new mailAddress("QuescomVirtualFax@Quescom.com"));
            msg.addRecipient(new mailAddress("gateway@lusitania-cs.pt"));
            
            msg.setSubject("SMS Message");
            
            String smsText = StringUtils.smsReplaceChars("\n\rSMS="+number+"\r\n"+text);
            msg.setBody(smsText);
            
            m.addMailMessage(msg);
            
            m.send(false);
            tf = System.currentTimeMillis();
        }
        catch (Exception e) 
        {
            tf = System.currentTimeMillis();
            e.printStackTrace();
            return false;
        }
        finally
        {
            logger.debug("Envio de SMS para o número:"+number +" tempo(ms): " + (tf - ti));
        }
        
        return true;
     }
     
//    public static void main(String[] args) throws Exception
//    {
//        send("969964516","ola mundo cruel 7");
//    }
    
    private static class SMSOperator 
    {
        private String name;
        private int prefix;
        private String proxyIP;
        
        public SMSOperator(String name, int prefix, String proxyIP)
        {
            this.name = name;
            this.prefix = prefix;
            this.proxyIP = proxyIP;
        }
    }
}