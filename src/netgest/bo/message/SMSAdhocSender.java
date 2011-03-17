package netgest.bo.message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import netgest.bo.controller.Controller;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.dochtml.*;
import netgest.bo.impl.document.print.PrintHelper;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.message.server.EmailServer;
import netgest.bo.message.server.FaxServer;
import netgest.bo.message.server.LetterServer;
import netgest.bo.message.server.MediaServer;
import netgest.bo.message.server.SMSServer;
import netgest.bo.message.server.SgisServer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;
import netgest.bo.workflow.*;
import netgest.utils.XEOUserUtils;
import netgest.xwf.core.*;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.system.Logger;
import netgest.xwf.EngineGate;
public class SMSAdhocSender 
{
    public SMSAdhocSender()
    {
    }
    
    public static boolean send(EboContext boctx, String phone1, String phone2, String phone3, String text, ArrayList errors)
    throws boRuntimeException
    {
        if(phone1 != null && !"".equals(phone1) && text != null && !"".equals(text))
        {
            boObject msg = boObject.getBoManager().createObject(boctx, "messageSMS");
            msg.getAttribute("name").setValueString( text.substring(0, Math.min( 60, text.length()) ) );
            msg.getAttribute("textSMS").setValueString( text ); 
            msg.getAttribute("from").setValueLong(boctx.getBoSession().getPerformerBoui());
            msg.getAttribute("statusMessage").setValueString( "1" ); 
            setRecipient(boctx, phone1, "to", msg);
            if(phone2 != null && !"".equals(phone2))
            {
//                vou validar se existe o mesmo número repetido se existir mando uma só vez
                if(!phone2.equals(phone1))
                {
                    setRecipient(boctx, phone2, "cc", msg);
                }
            }
            if(phone3 != null && !"".equals(phone3))
            {
//                vou validar se existe o mesmo número repetido se existir mando uma só vez
                if(!(phone3.equals(phone2) || phone3.equals(phone1)))
                {
                    setRecipient(boctx, phone3, "bcc", msg);
                }
            }
            SMSServer server = new SMSServer(null, -1); 
            if(server.adHocSend(msg)>=0)
            {
                msg.update();
                return true;
            }
            else
            {
                ArrayList sendErrors = server.getErrors();
                for (int i = 0; sendErrors != null && i < sendErrors.size(); i++) 
                {
                    errors.add(sendErrors.get(i));
                }
            }
        }
        else
        {
            errors.add(MessageLocalizer.getMessage("YOU_MUST_FILL_THE_PHONENUMBER_AND_THE_TETX_TO_SEND"));
        }
        return false;
    }
    
    private static void setRecipient(EboContext boctx, String phone,
        String attributeToAssociate, boObject mailobject) throws boRuntimeException
    {
        boObject deliver = boObject.getBoManager().createObject(boctx, "deliveryMessage");
        deliver.getAttribute("telemovel").setValueString(phone);
        deliver.getAttribute("already_send").setValueString("1");
        mailobject.getBridge(attributeToAssociate).add(deliver.getBoui());
    }
}