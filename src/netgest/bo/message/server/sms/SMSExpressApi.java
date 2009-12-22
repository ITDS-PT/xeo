package netgest.bo.message.server.sms;
import java.util.Date;
import netgest.bo.boConfig;
import netgest.bo.system.boApplication;

import netgest.utils.StringUtils;
import netgest.utils.mail.mail;
import netgest.utils.mail.mailAddress;
import netgest.utils.mail.mailMessage;
import netgest.utils.ngtXMLHandler;
import netgest.utils.ngtXMLUtils;
import netgest.bo.system.Logger; 

public class SMSExpressApi 
{
    public SMSExpressApi()
    {
    }
    
    //private static SMSOperator[] smsOperators = null;
    private static String        p_smsLogin   = null;
    private static String        p_smsPass    = null;
    private static String        p_smsSender  = null;
    private static Boolean       p_smsReport  = null;
    private static Integer       p_smsValidity= null;
    private static boolean p_isInitialized = false;
    
    
    private String p_statusMessage = null;
    private String p_statusCode    = null;
    private String p_submissionId    = null;
    
    private Exception p_generatedException = null; 
    
    //logger

    private static Logger logger = Logger.getLogger( SMSExpressApi.class );
    
    public static void main( String[] args )
    {
        SMSExpressApi sapi = new SMSExpressApi();
        sapi.send( "912181684","Hello ppl" );
    }
    
    public static boolean isConfig()
    {
        initialize();
        return p_smsLogin != null && !"".equals(p_smsLogin);
    }
    

    public static final void initialize() throws RuntimeException
    {
        if ( !p_isInitialized )
        {
            ngtXMLHandler xnode;       
            try {
                boApplication.getApplicationFromStaticContext("XEO").addAContextToThread();
                ngtXMLHandler root = new ngtXMLHandler(ngtXMLUtils.loadXMLFile( boConfig.getNgtHome()+"boconfig.xml" ).getDocumentElement());
                xnode = root.getChildNode("SMSExpress");
            } catch (Exception e) {
                xnode = null;   
            }
            
            p_isInitialized = true;

            if (xnode == null) 
            {
                throw new RuntimeException("Não foi indicado no ficheiro boconfig.xml a configuração SMSExpress.");
            }

            p_smsLogin      = xnode.getChildNodeText( "login" , null );
            p_smsPass       = xnode.getChildNodeText( "password" , null );
            p_smsSender     = xnode.getChildNodeText( "sender" , null );
            p_smsReport     = new Boolean( xnode.getChildNodeText( "report" , "true" ) );
            p_smsValidity   = new Integer(xnode.getChildNodeText( "validity" , "48" ));
            
        }
    }
    
    public String getStatusMessage()
    {
        return p_statusMessage;
    }
    
    public String getStatusCode()
    {
        return p_statusCode;
    }

    public String getSubmissionId()
    {
        return p_submissionId;
    }
    
     public boolean send(String number, String text)
     {
        long ti = 0, tf = 0;
        boolean ret = false;
        initialize();
        
        try
        {
            ti = System.currentTimeMillis();
            AuthInfoWS authInfo = new AuthInfoWS("xeo", p_smsLogin, p_smsPass );

            Submission submit = new Submission(
                text,
                null,
                new String[] { number },
                p_smsSender,
                p_smsValidity,
                p_smsReport,
                new Date()
            );
            
            for (int i = 0; i < 3; i++) 
            {
                try
                {
                    SubmissionManagerStub subm = new SubmissionManagerStub();
                    Status status = subm.sendMultiSms( authInfo, submit );
                    
                    p_statusMessage = status.getMessage();
                    p_statusCode    = String.valueOf( status.getStatus() );
                    p_submissionId  = status.getId();
                    
                    if( "1".equals( p_statusCode ) )
                    {
                        ret = true;
                    }
                    tf = System.currentTimeMillis();
                    break;
                }
                catch (Exception e)
                {
                    p_generatedException = e;
                    logger.warn("Erro na tentativa "+i+" de envoio de SMS:" +  e.getMessage() );
                }
            }
        }
        catch (Exception e) 
        {
            tf = System.currentTimeMillis();
            e.printStackTrace();
            return ret;
        }
        finally
        {
            logger.finest("Envio de SMS para o número:"+number +" tempo(ms): " + (tf - ti));
        }
        
        return ret;
     }


    public Exception getGeneratedException()
    {
        return p_generatedException;
    }
}