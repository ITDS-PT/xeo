/*Enconding=UTF-8*/
package netgest.bo.message.utils;

import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import netgest.bo.boConfig;
import netgest.bo.runtime.*;
import netgest.bo.security.securityRights;
import netgest.utils.*;
import netgest.bo.system.Logger;

import org.w3c.dom.Attr;
import org.w3c.dom.Comment;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

/**
 * 
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since 
 */
public class MessageUtils 
{
    public static final String ATT_PRF_MEDIA = "preferedMedia";
    public static final String EMAIL = "E-Mail";
    public static final String FAX = "Fax";
    public static final String LETTER = "Letter";
    public static final String PHONE = "Phone";
    public static final String CONVERSATION = "Conversation";
    public static final String PREFERED = "Prefered";
    public static final String SGIS = "Sgis";
    public static final String BV = "BV";
    public static final String SMS = "SMS";
    
    
    public static final String NOT_DEFINED = "Prefered";
    public static final String ATT_EMAIL = "email";
    
    public static final String ATT_FROM= "from";
    public static final String ATT_TO = "to";
    public static final String ATT_CC = "cc";
    public static final String ATT_BCC = "bcc";
    public static final String ATT_PHONE = "telemovel";
    public static final String ATT_FAX = "fax";
    public static final String ATT_RECEIPTS = "receipts";
    public static final String ATT_FROMRef= "fromRef";
    public static final String ATT_TORef = "toRef";
    public static final String ATT_CCRef = "ccRef";
    public static final String ATT_BCCRef = "bccRef";
    
    public static final String ATT_STREET = "rua";
    public static final String ATT_LOCAL = "localidade";
    public static final String ATT_POSTAL = "cpostal";
    public static final String ATT_CPLOCAL = "localcpostal";
    public static final String ATT_COUNTRY = "country";

    public static final String[] bridges = {"to", "cc", "bcc"};
    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.message.utils.MessageUtils");
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    public MessageUtils()
    {
    }
    
    public static boolean alreadySend(boObject message) throws boRuntimeException
    {
        boBridgeIterator it;
        boObject deliveryMsg;
        boolean existRecipient = false;
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();            
            while(it.next())
            {
                existRecipient = true;
                deliveryMsg = it.currentRow().getObject();
                String aux = deliveryMsg.getAttribute("already_send").getValueString();
                if(!"1".equals(deliveryMsg.getAttribute("already_send").getValueString()))
                {
                    return false;
                }
            }
        }
        return existRecipient;
    }
    
    public static boolean letterFormatDisabledWhen(boObject message)
    {
        try {
            if(message.getMode() == boObject.MODE_EDIT_TEMPLATE)
            {
                return false;
            }
            if (netgest.bo.utils.Calculate.compare(message.getAttribute("preferedMedia").getValueString(), LETTER, 2)) 
            {
                return false;
            }
            ArrayList r = getChannelsMessage(message);
            if(r.indexOf(LETTER) != -1 && netgest.bo.utils.Calculate.compare(message.getAttribute("preferedMedia").getValueString(), PREFERED, 2))
            {
                return false;
            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public static boolean faxFormatDisabledWhen(boObject message)
    {
        try {
            if(message.getMode() == boObject.MODE_EDIT_TEMPLATE)
            {
                return false;
            }
            if (netgest.bo.utils.Calculate.compare(message.getAttribute("preferedMedia").getValueString(), FAX, 2)) 
            {
                return false;
            }
            ArrayList r = getChannelsMessage(message);
            if(r.indexOf(FAX) != -1 && netgest.bo.utils.Calculate.compare(message.getAttribute("preferedMedia").getValueString(), PREFERED, 2))
            {
                return false;
            }
            return true;
        }
        catch(Exception e)
        {
            return false;
        }
    }
    public static ArrayList getChannelsMessage(boObject message) throws boRuntimeException
    {
        ArrayList toRet = new ArrayList();
        boBridgeIterator it = null;
        String mediaType = message.getAttribute(ATT_PRF_MEDIA).getValueString();
        if(!mediaType.equals(NOT_DEFINED))
        {
            if(MessageUtils.BV.equals(mediaType))
            {
                toRet.add(MessageUtils.EMAIL);            
            }
            else
            {
                toRet.add(mediaType);
            }
        }
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();
            boObject deliveryMsg;
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();
                if(deliveryMsg.getAttribute("media").getValueString() != null &&
                    !"".equals(deliveryMsg.getAttribute("media").getValueString())
                )
                {
                    if(!toRet.contains(deliveryMsg.getAttribute("media").getValueString()))
                    {
                        toRet.add(deliveryMsg.getAttribute("media").getValueString());
                    }
                }
            }
        }
        return toRet;
    }
    public static ArrayList getGeneratedDoc(boObject message) throws boRuntimeException
    {
        boBridgeIterator it;
        ArrayList toRet = new ArrayList();
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();
            boObject deliveryMsg;
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();
                if(LETTER.equals(deliveryMsg.getAttribute("media").getValueString()))
                {
                    if(deliveryMsg.getAttribute("document").getValueObject() != null)
                    {
                        toRet.add(deliveryMsg.getAttribute("document").getObject());
                    }
                }
            }
            
        }
        return toRet;
    }
    public static void setRead(boObject message, boObject readBy)
    {
        try
        {
            ArrayList deliverMessages = getDeliveryMessage(message, readBy);
            for (int i = 0; i < deliverMessages.size(); i++) 
            {
                ((boObject)deliverMessages.get(i)).getAttribute("already_read").setValueString("1");
            }

            if(allRead(message))
            {
                message.getAttribute("already_read").setValueString("1");
            }
        }
        catch(Exception e)
        {
            logger.severe("Error ao efectuar o set do already_read", e);
        }
    }
    
    public static ArrayList getDeliveryMessageOnlyfromObject(boObject message, boObject fromUser) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        boBridgeIterator it;
        boObject deliveryMsg, auxObj;
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();            
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();
                auxObj = deliveryMsg.getAttribute("refObj").getObject();
                //ebo_perf
                if(auxObj != null && fromUser.getBoui() == auxObj.getBoui())
                {
                    list.add(deliveryMsg);
                }
                else
                {
                    String email = deliveryMsg.getAttribute("email").getValueString();
                    String userEmail = fromUser.getAttribute("email").getValueString();
                    if(userEmail != null && userEmail.toUpperCase().equals(email))
                    {
                        list.add(deliveryMsg);
                    }
                    else
                    {
                        boBridgeIterator itAccount = fromUser.getBridge("emailAccounts").iterator();
                        itAccount.beforeFirst();
                        String accountEmail;
                        while(itAccount.next())
                        {
                            accountEmail = itAccount.currentRow().getObject().getAttribute("email").getValueString();
                            if(accountEmail != null && accountEmail.equalsIgnoreCase(email))
                            {
                                list.add(deliveryMsg);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public static ArrayList getDeliveryMessage(boObject message, boObject fromUser) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        boBridgeIterator it;
        boObject deliveryMsg, auxObj;
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();            
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();
                auxObj = deliveryMsg.getAttribute("refObj").getObject();
                //ebo_perf
                if(auxObj != null && fromUser.getBoui() == auxObj.getBoui())
                {
                    list.add(deliveryMsg);
                }
                else if(auxObj != null && 
                       ("Ebo_Group".equals(auxObj.getName()) ||"Ebo_Group".equals(auxObj.getBoDefinition().getBoSuperBo())) 
                )
                {
                    if(fromUser.getBridge("groups").haveBoui(auxObj.getBoui()))
                    {
                        list.add(deliveryMsg);
                    }
                }
                else if(auxObj != null && 
                    ("workQueue".equals(auxObj.getName()) || "workQueue".equals(auxObj.getBoDefinition().getBoSuperBo()))
                )
                {
                    if(fromUser.getBridge("queues").haveBoui(auxObj.getBoui()))
                    {
                        list.add(deliveryMsg);
                    }
                }
                else if(auxObj != null && 
                    ("Ebo_Role".equals(auxObj.getName()) || "Ebo_Role".equals(auxObj.getBoDefinition().getBoSuperBo()))
                )
                {
                    if(fromUser.getBridge("roles").haveBoui(auxObj.getBoui()))
                    {
                        list.add(deliveryMsg);
                    }
                }
                else
                {
                    String email = deliveryMsg.getAttribute("email").getValueString();
                    String userEmail = fromUser.getAttribute("email").getValueString();
                    if(userEmail != null && userEmail.toUpperCase().equals(email))
                    {
                        list.add(deliveryMsg);
                    }
                    else
                    {
                        boBridgeIterator itAccount = fromUser.getBridge("emailAccounts").iterator();
                        itAccount.beforeFirst();
                        String accountEmail;
                        while(itAccount.next())
                        {
                            accountEmail = itAccount.currentRow().getObject().getAttribute("email").getValueString();
                            if(accountEmail != null && accountEmail.equalsIgnoreCase(email))
                            {
                                list.add(deliveryMsg);
                            }
                        }
                    }
                }
            }
        }
        return list;
    }
    
    public static boolean allRead(boObject message) throws boRuntimeException
    {
        boBridgeIterator it;
        boObject deliveryMsg;
        for (int i = 0; i < bridges.length; i++) 
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();            
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();
                if(!"1".equals(deliveryMsg.getAttribute("already_read").getValueString()))
                {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static boObject getBinaryBySourceBoui(boObject msg, long boui)
    {
        try
        {
            boBridgeIterator it = msg.getBridge("binaryDocuments").iterator();
            boObject auxObj;
            it.beforeFirst();
            while(it.next())
            {
                auxObj = it.currentRow().getObject(); 
                if(auxObj.getName() == "Ebo_Document" &&
                    auxObj.getAttribute("srcObj").getValueLong() == boui)
                {
                    return auxObj; 
                }
            }
        }
        catch (boRuntimeException e)
        {
            
        }
        return null;
    }
    
    public static ArrayList sendReceiptsAs(boObject message, boObject performer)
    {
        ArrayList toRet = new ArrayList();
        ArrayList auxL = new ArrayList();
        try
        {
            ArrayList deliveries = MessageUtils.getDeliveryMessage(message, performer);
            boObject aux;
            for (int i = 0; i < deliveries.size(); i++) 
            {
                if(((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").getValueDate() == null)
                {
                    aux = ((boObject)deliveries.get(i)).getAttribute("refObj").getObject();
                    if(aux != null)
                    {
                        if(!auxL.contains(String.valueOf(aux.getBoui())))
                        {
                            toRet.add(aux);
                            auxL.add(String.valueOf(aux.getBoui()));
                        }
                    }
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return toRet;
    }
    
    public static Date sendReceiptDate(boObject message, boObject performer)
    {
        try
        {
            ArrayList deliveries = MessageUtils.getDeliveryMessage(message, performer);
            boObject aux;
            for (int i = 0; i < deliveries.size(); i++) 
            {
                if(((boObject)deliveries.get(i)).getAttribute("refObj").getValueLong() == performer.getBoui())
                {
                    return ((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").getValueDate();
                }
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return null;
    }
    
    public static void setSentDateReceitp(boObject message, boObject performer, Date d)
    {
        try
        {
            ArrayList deliveries = MessageUtils.getDeliveryMessage(message, performer);
            boObject aux;
            for (int i = 0; i < deliveries.size(); i++) 
            {
                aux = (boObject)deliveries.get(i);
                aux.getAttribute("date_sent_read_receipt").setValueDate(d);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
    }
    
    public static Element getReadReceiptsElement(ngtXMLHandler xmlToPrint, Element root, boObject message, boObject fromUser) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        boBridgeIterator it;
        boObject deliveryMsg, auxObj;
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int n = 0;
        
        if("1".equals(message.getAttribute("send_read_receipt").getValueString()) && !"messageFax".equals(message.getName()))
        {
            root.appendChild( xmlToPrint.getDocument().createTextNode("O remetente desta mensagem solicitou recibo de leitura.") );
            root.appendChild( xmlToPrint.getDocument().createElement("br") );
            for (int i = 0; i < bridges.length; i++) 
            {
                it = message.getBridge(bridges[i]).iterator();
                it.beforeFirst();            
                while(it.next())
                {
                    deliveryMsg = it.currentRow().getObject();
                    auxObj = deliveryMsg.getAttribute("refObj").getObject();
                    //ebo_perf
                    if(auxObj != null && fromUser.getBoui() == auxObj.getBoui())
                    {
                        if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                        {
                            root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                            root = auxObj.getCARDID(xmlToPrint, root, false);
                            root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                            Element span = xmlToPrint.getDocument().createElement("span");                            
                            span.setAttribute("class", "lui");
                            span.setAttribute("style", "cursor:hand");
                            //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                            span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                            span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                            root.appendChild(span);
                            root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                            n++;
                        }
                        else
                        {
                            root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                            root = auxObj.getCARDID(xmlToPrint, root, false);
                            Element span = xmlToPrint.getDocument().createElement("span");
                            span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                            root.appendChild(span);
                            root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                        }
                    }
                    else if(auxObj != null && 
                            ("Ebo_Group".equals(auxObj.getName()) ||"Ebo_Group".equals(auxObj.getBoDefinition().getBoSuperBo()))
                    )
                    {

                        boolean bUserIsInGroup = false;
                        
                        try
                        {
                            long[] laGroups =securityRights.getPerformerGroupsKeys( auxObj.getEboContext(), fromUser.getBoui() );
                            for (int i1 = 0; i1 < laGroups.length; i1++) 
                            {
                                if( laGroups[i1] == auxObj.getBoui() )
                                {
                                    bUserIsInGroup = true;
                                    break;
                                }
                            }
                        }
                        catch (SQLException e)
                        {
                            
                        }
                        
                    
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                            if( bUserIsInGroup /*fromUser.getBridge("groups").haveBoui(auxObj.getBoui())*/ )
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = auxObj.getCARDID(xmlToPrint, root, false);
                                root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                                Element span = xmlToPrint.getDocument().createElement("span");                            
                                span.setAttribute("class", "lui");
                                span.setAttribute("style", "cursor:hand");
                                //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                                span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                                span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                                n++;
                            }
                        }
                            else
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = auxObj.getCARDID(xmlToPrint, root, false);
                                Element span = xmlToPrint.getDocument().createElement("span");
                                span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                        }
                    }
                    else if(auxObj != null && 
                        ("workQueue".equals(auxObj.getName()) ||"workQueue".equals(auxObj.getBoDefinition().getBoSuperBo()))
                    )
                    {
                        if(fromUser.getBridge("queues").haveBoui(auxObj.getBoui()))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = auxObj.getCARDID(xmlToPrint, root, false);
                                root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                                Element span = xmlToPrint.getDocument().createElement("span");                            
                                span.setAttribute("class", "lui");
                                span.setAttribute("style", "cursor:hand");
                                //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                                span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                                span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                                n++;
                            }
                            else
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = auxObj.getCARDID(xmlToPrint, root, false);
                                Element span = xmlToPrint.getDocument().createElement("span");
                                span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                            }
                        }
                    }
                    else if(auxObj != null && 
                        ("Ebo_Role".equals(auxObj.getName()) ||"Ebo_Role".equals(auxObj.getBoDefinition().getBoSuperBo()))
                    )
                    {
                        if(fromUser.getBridge("roles").haveBoui(auxObj.getBoui()))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                               root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                               root = auxObj.getCARDID(xmlToPrint, root, false);
                               root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                               Element span = xmlToPrint.getDocument().createElement("span");                            
                               span.setAttribute("class", "lui");
                               //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                               span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                               span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                               root.appendChild(span);
                               root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                               n++;

                            }
                            else
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = auxObj.getCARDID(xmlToPrint, root, false);
                                Element span = xmlToPrint.getDocument().createElement("span");
                                span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                            }
                        }
                    }
                    else
                    {
                        String email = deliveryMsg.getAttribute("email").getValueString();
                        String userEmail = fromUser.getAttribute("email").getValueString();
                        if(userEmail != null && userEmail.toUpperCase().equals(email))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = fromUser.getCARDID(xmlToPrint, root, false);
                                root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                                Element span = xmlToPrint.getDocument().createElement("span");                            
                                span.setAttribute("class", "lui");
                                //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                                span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                                span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                                n++;
                            }
                            else
                            {
                                root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                root = fromUser.getCARDID(xmlToPrint, root, false);
                                Element span = xmlToPrint.getDocument().createElement("span");
                                span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                                root.appendChild(span);
                                root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                            }
                        }
                        else
                        {
                            boBridgeIterator itAccount = fromUser.getBridge("emailAccounts").iterator();
                            itAccount.beforeFirst();
                            String accountEmail;
                            while(itAccount.next())
                            {
                                accountEmail = itAccount.currentRow().getObject().getAttribute("email").getValueString();
                                if(accountEmail != null && accountEmail.equalsIgnoreCase(email))
                                {
                                    if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                                    {
                                         root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                         root = fromUser.getCARDID(xmlToPrint, root, false);
                                         root.appendChild( xmlToPrint.getDocument().createTextNode("NBSP NBSP") );
                                         Element span = xmlToPrint.getDocument().createElement("span");                            
                                         span.setAttribute("class", "lui");
                                         //span.setAttribute("onclick", "parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();");
                                         span.setAttribute("onclick", "try{parent.setActionCode('sendReadReceipt["+auxObj.getBoui()+"]');parent.setStateActivity('');parent.boForm.BindValues();}catch(e){alert('Neste ecran não pode enviar recibo de leitura. Para tal, deve voltar ao ecran anterior antes de encaminhar o documento.')};");
                                         span.appendChild( xmlToPrint.getDocument().createTextNode("clique aqui para enviar.") );
                                         root.appendChild(span);
                                         root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                                         n++;
                                    }
                                    else
                                    {
                                        root.appendChild( xmlToPrint.getDocument().createTextNode("[") );
                                        root = fromUser.getCARDID(xmlToPrint, root, false);
                                        Element span = xmlToPrint.getDocument().createElement("span");
                                        span.appendChild(xmlToPrint.getDocument().createTextNode(" - Enviado em: " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate())));
                                        root.appendChild(span);
                                        root.appendChild( xmlToPrint.getDocument().createTextNode(" ] NBSP NBSP") );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return root;
    }

    public static String getReadReceiptsString(boObject message, boObject fromUser) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        boBridgeIterator it;
        boObject deliveryMsg, auxObj;
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int n = 0;
        
        if("1".equals(message.getAttribute("send_read_receipt").getValueString()))
        {
            for (int i = 0; i < bridges.length; i++) 
            {
                it = message.getBridge(bridges[i]).iterator();
                it.beforeFirst();            
                while(it.next())
                {
                    deliveryMsg = it.currentRow().getObject();
                    auxObj = deliveryMsg.getAttribute("refObj").getObject();
                    //ebo_perf
                    if(auxObj != null && fromUser.getBoui() == auxObj.getBoui())
                    {
                        if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                        {
                            sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                              .append(" >"+auxObj.getCARDID()+"</span>]");
                            n++;
                        }
                        else
                        {
                            sb.append("["+auxObj.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                        }
                    }
                    else if(auxObj != null && "Ebo_Group".equals(auxObj.getName()))
                    {
                        if(fromUser.getBridge("groups").haveBoui(auxObj.getBoui()))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                                  .append(" >"+auxObj.getCARDID()+"</span>]");
                                n++;
                            }
                            else
                            {
                                sb.append("["+auxObj.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                            }
                        }
                    }
                    else if(auxObj != null && "workQueue".equals(auxObj.getName()))
                    {
                        if(fromUser.getBridge("queues").haveBoui(auxObj.getBoui()))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                                  .append(" >"+auxObj.getCARDID()+"</span>]");
                                n++;
                            }
                            else
                            {
                                sb.append("["+auxObj.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                            }
                        }
                    }
                    else if(auxObj != null && "Ebo_Role".equals(auxObj.getName()))
                    {
                        if(fromUser.getBridge("roles").haveBoui(auxObj.getBoui()))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                                  .append(" >"+auxObj.getCARDID()+"</span>]");
                                n++;
                            }
                            else
                            {
                                sb.append("["+auxObj.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                            }
                        }
                    }
                    else
                    {
                        String email = deliveryMsg.getAttribute("email").getValueString();
                        String userEmail = fromUser.getAttribute("email").getValueString();
                        if(userEmail != null && userEmail.toUpperCase().equals(email))
                        {
                            if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                            {
                                sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                                  .append(" >"+fromUser.getCARDID()+"</span>]");
                                n++;
                            }
                            else
                            {
                                sb.append("["+fromUser.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                            }
                        }
                        else
                        {
                            boBridgeIterator itAccount = fromUser.getBridge("emailAccounts").iterator();
                            itAccount.beforeFirst();
                            String accountEmail;
                            while(itAccount.next())
                            {
                                accountEmail = itAccount.currentRow().getObject().getAttribute("email").getValueString();
                                if(accountEmail != null && accountEmail.equalsIgnoreCase(email))
                                {
                                    if(deliveryMsg.getAttribute("date_sent_read_receipt").getValueObject() == null)
                                    {
                                        sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
                                          .append(" >"+fromUser.getCARDID()+"</span>]");
                                        n++;
                                    }
                                    else
                                    {
                                        sb.append("["+fromUser.getCARDID()+" - " + sdf.format(deliveryMsg.getAttribute("date_sent_read_receipt").getValueDate()) +"]" );
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if(n > 1)
        {
            sb.append("[<span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\"")
              .append(" >Todos</span>]");
        }
        return sb.toString();
    }
    
    public static boolean isToWaitResponse(boObject msg)
    {
        try
        {
            return "1".equals(msg.getAttribute("waitForResponse").getValueString());
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return false;
    }
    public static long[] toLongReceivers (ArrayList receivers)
    {
        Long[] l = new Long[receivers.size()];
        l = (Long[])receivers.toArray(l);
        long[] toRet = new long[l.length];
        for (int i = 0; i < l.length; i++) 
        {
            toRet[i] = l[i].longValue();
        }
        return toRet;
    }
    
    public static long[] getReceivers(boObject message)
    {
        long[] toRet = null;
        try
        {
            boBridgeIterator it;
            boObject document = null;
            ArrayList receivers = new ArrayList();
            boObject deliveryMsg;
            for (int i = 0; i < bridges.length; i++) 
            {
                it = message.getBridge(bridges[i]).iterator();
                it.beforeFirst();
                while(it.next())
                {
                    deliveryMsg = it.currentRow().getObject();                    
                    if(deliveryMsg.getAttribute("refObj").getValueObject() != null)
                    {
                        receivers.add(new Long(deliveryMsg.getAttribute("refObj").getValueLong()));
                    }
                }
            }
            if(receivers.size() > 0)
            {
                toRet = toLongReceivers(receivers);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return toRet;
    }
    
    public static long[] getToReceivers(boObject message)
    {
        long[] toRet = null;
        try
        {
            boBridgeIterator it;
            boObject document = null;
            ArrayList receivers = new ArrayList();
            boObject deliveryMsg;
            it = message.getBridge("to").iterator();
            it.beforeFirst();
            while(it.next())
            {
                deliveryMsg = it.currentRow().getObject();                    
                if(deliveryMsg.getAttribute("refObj").getValueObject() != null)
                {
                    receivers.add(new Long(deliveryMsg.getAttribute("refObj").getValueLong()));
                }
            }
            if(receivers.size() > 0)
            {
                toRet = toLongReceivers(receivers);
            }
        }
        catch (boRuntimeException e)
        {
            logger.severe(e);
        }
        return toRet;
    }
    
    public static String getEmailServer(String email)
    {
        if(email != null && email.indexOf("@") >= 0)
        {
            int i = email.indexOf("@") + 1;
            if(email.length() > i)
            {
                return email.substring(email.indexOf("@") + 1);
            }
        }
        return null;
    }
    
    public static boolean isBlockMsg(boObject message) throws boRuntimeException
    {
        String assunto = message.getAttribute("name").getValueString().toUpperCase();
        String blockMail = boConfig.getMailConfig().getProperty("blockMail");
        String blockMailPrefix = boConfig.getMailConfig().getProperty("blockMailPefix"); 
        if(blockMail != null && !"".equals(blockMail) && blockMailPrefix != null && !"".equals(blockMailPrefix))
        {
            if(assunto != null && assunto.startsWith(blockMailPrefix.toUpperCase()))
            {
                String fromMail = message.getAttribute("from").getObject() == null ? "":message.getAttribute("from").getObject().getAttribute("email").getValueString();
                fromMail = (fromMail == null ? "":fromMail.trim());
                String[] mails = blockMail.split(";");
                for (int i = 0; i < mails.length; i++) 
                {
                    if(mails[i].equalsIgnoreCase(fromMail))
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public static Element getBlockMsgElement(ngtXMLHandler xmlToPrint, Element root, boObject message) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        boBridgeIterator it;
        boObject deliveryMsg, auxObj;
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int n = 0;
        
        root.appendChild( xmlToPrint.getDocument().createTextNode("Esta mensagem ficou retida.") );
        if(message.getAttribute("date_releaseMsg").getValueObject() == null)
        {
            Element span = xmlToPrint.getDocument().createElement("span");                            
            span.setAttribute("class", "lui");
            span.setAttribute("style", "cursor:hand");
            span.setAttribute("onclick", "parent.setActionCode('releaseMsg');parent.setStateActivity('');parent.boForm.BindValues();");
            span.appendChild( xmlToPrint.getDocument().createTextNode("Clique aqui para desloquear.") );
            root.appendChild(span);
        }
        else
        {
            String releaseText = "Foi pedido envio na data " + sdf.format(message.getAttribute("date_releaseMsg").getValueDate()) + ".";
            root.appendChild( xmlToPrint.getDocument().createTextNode(releaseText));
        }
        return root;
    }
    
    public static String getReleaseMsgText(boObject oldmailobject) throws boRuntimeException
    {
        String[] showBridges = new String[]{MessageUtils.ATT_TO, MessageUtils.ATT_CC};
        StringBuffer de = new StringBuffer();
        //de
        de.append("Vimos por este meio solicitar o envio do email retido.<p>Cumprimentos.");
        de.append("<br>-----Mensagem original-----<p>")
        .append("<ul>")
        .append("<li><b>De:</b> ");
        boObject runtimeAddres = oldmailobject.getAttribute(MessageUtils.ATT_FROM).getObject();
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
        }
//        mailobject.getAttribute(MessageUtils.ATT_FROM).setValueObject(null);
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
                    if(MessageUtils.ATT_TO.equals(showBridges[i]))
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
                ciclo++;
            }
            de.append("</li>");
        }
        
        //assunto        
        String subject = oldmailobject.getAttribute("name").getValueString();
        de.append("<li><b>Assunto:</b> ").append(subject).append("</li></ul>");
        //texto
        //-----Mensagem original-----
        //De: XXXXXX
        //Enviada: XXXXX
        //Para: XXXXX
        //Assunto: FW: XXXX
        de.append(oldmailobject.getAttribute("description").getValueString());
        return de.toString();
    }
}