/*Enconding=UTF-8*/
package netgest.bo.message;

import java.text.SimpleDateFormat;

import java.util.ArrayList;

import java.util.Date;
import netgest.bo.controller.Controller;
import netgest.bo.controller.basic.BasicController;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.dochtml.docHTML;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.message.server.FaxServer;
import netgest.bo.message.server.LetterServer;
import netgest.bo.message.utils.MessageUtils;
import netgest.bo.runtime.*;

import netgest.utils.XEOUserUtils;
import netgest.xwf.EngineGate;
import netgest.bo.system.Logger;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public class PostInformation
{
    /**
    *
    * @Company Enlace3
    * @since
    */
    private static final String EMAIL = "E-Mail";
    private static final String BV = "BV";
    private static final String FAX = "Fax";
    private static final String LETTER = "Letter";
    private static final String PHONE = "Phone";
    private static final String SMS = "SMS";
    private static final String CONVERSATION = "Conversation";
    private static final String NOT_DEFINED = "Prefered";
    private static final String NO_SENDER = "</td></tr><tr><td colspan='4'>Preencha o remetente da mensagem.";
    private static final String NO_RECEIVER = "</td></tr><tr><td colspan='4'>Preencha o(s) destinatário(s) da mensagem.";
    private static final String CHANNEL_NOT_DEFINED_2 = "] não tem endereço definido para o canal escolhido.";
    private static final String DESTINATARIO = "</td></tr><tr><td colspan='4'>O destinatário [";
    private static final String PREFERED_CHANNEL_NOT_DEFINED_2 = "] não tem um canal preferido definido.";
    private static final String PREFERED_CHANNEL_NOT_DEFINED_3 = "] não tem o endereço para o canal pretendido.";
    private static final String CHOOSE_FAX_FORMAT = "</td></tr><tr><td colspan='4'>Preencha o campo Modelo p/ Fax.";
    private static final String CHOOSE_LETTER_FORMAT = "</td></tr><tr><td colspan='4'>Preencha o campo Modelo p/ Carta.";
    private static final String LINESTART = "</td></tr><tr><td colspan='4'>";
    private static final String ACTIVITY_1 = "Para atribuir a actividade  <span class='lui' onclick=\"winmain().openDocUrl('tall','__applyTemplate.jsp','?clientIDX='+getIDX()+'&operation=applyTemplate&docid='+getDocId()+'&bouiToApplyTemplate=";
    private static final String ACTIVITY_2 = "','lookup') \" >clique aqui</span>";
    private static final String TEMPLATE = " Este objecto foi criado com o modelo ";
    private static final String TEMPLATE_PRV = " <span>Este objecto foi criado com o modelo ";
    private static final String MSG_NOT_SENT = "</td></tr><tr><td colspan='4'><font color=\"#FF0000\">Esta mensagem não foi enviada.</font>";
    private static final String MSG_NOT_SENT_PRV = "<br/><span><font color=\"#FF0000\">Esta mensagem não foi enviada.</font></span>";
    private static final String MSG_SENT = "</td></tr><tr><td colspan='4'>Esta mensagem foi enviada.";
    private static final String MSG_SENT_PRV = "<br/><span>Esta mensagem foi enviada.</span>";
    private static final String LOW_IMP = "</td></tr><tr><td colspan='4'>Esta mensagem foi enviada com o grau de importância baixa.";
    private static final String LOW_IMP_PRV = "<br/><span>Esta mensagem foi enviada com o grau de importância baixa.</span>";
    private static final String HIGH_IMP = "</td></tr><tr><td colspan='4'>Esta mensagem foi enviada com o grau de importância alta.";
    private static final String HIGH_IMP_PRV = "<br/><span>Esta mensagem foi enviada com o grau de importância alta.</span>";
    private static final String SEND_RECEIPT = "</td></tr><tr><td colspan='4'>O remetente desta mensagem solicitou um recibo de leitura. <span class='lui' onclick=\"boForm.executeMethod('sendReceipt')\">Clique aqui para enviar um recibo</span>";
    private static final String SEND_RECEIPT_PRV = "<br/><span>O remetente desta mensagem solicitou um recibo de leitura. <span class='lui' onclick=\"parent.parent.boForm.executeMethod('sendReceipt')\">Clique aqui para enviar um recibo</span></span>";
    private static final String RECEIPT_SENT = "</td></tr><tr><td colspan='4'>Recibo de leitura enviado na data ";
    private static final String RECEIPT_SENT_PRV = "<br/><span>Recibo de leitura enviado na data ";
    private static final String BAD_ADDRESS = "] não tem o endereço definido correctamente para o canal escolhido.";
    private static final String BAD_TLM = "] tem um número telemóvel/telefone inválido.";
    private static final String BAD_SGIS_USER = "] não tem conta definida no SGIS.";
    private static final String BAD_MSG_TYPE = "] não é válido para este tipo de mensagem.";
    private static final String ATT_EMAIL = "email";
    private static final String ATT_PRF_MEDIA = "preferedMedia";
    private static final String ATT_FROM = "from";
    private static final String ATT_TO = "to";
    private static final String ATT_CC = "cc";
    private static final String ATT_BCC = "bcc";
    private static final String ATT_PHONE = "telemovel";
    private static final String ATT_FAX = "fax";
    private static final String ATT_STREET = "rua";
    private static final String ATT_LOCAL = "localidade";
    private static final String ATT_POSTAL = "cpostal";
    private static final String ATT_CPLOCAL = "localcpostal";
    private static final String ATT_COUNTRY = "country";
    private static final String MSG_NOT_PRINT = "</td></tr><tr><td colspan='4'><font color=\"#FF0000\">Esta mensagem ainda não foi impressa.</font>";
    private static final String MSG_PRINT = "</td></tr><tr><td colspan='4'><font color=\"#000000\">Esta mensagem foi impressa.</font>";

    //logger
    private static Logger logger = Logger.getLogger(
            "netgest.bo.parser.CodeJavaConstructor");

    public PostInformation()
    {
    }


    public static boolean generateMessageInformationforPreview(StringBuffer toPrint,
        boObject ctxObj, docHTML doc) throws boRuntimeException
    {
        boolean isToSend = false;
        XwfController controller;
        Controller auxController = doc.getController();        
        if("BasicController".equals(auxController.getName()))
        {
            if(doc.getControllerQueue().containsKey(XwfKeys.CONTROLLER_NAME_KEY))
            {
                controller = (XwfController)doc.getControllerQueue().get(XwfKeys.CONTROLLER_NAME_KEY);
            }
            else
            {
                return true;
            }
        }
        else
        {
            controller = (XwfController)auxController;
        }
        
        boolean createdReceiveMessage = "xwfCreateReceivedMessage".equals(controller.getRuntimeActivity().getName());

        boObject activity = controller.getRuntimeActivity();
        isToSend = false;

        if ("xwfActivitySend".equals(activity.getName()))
        {
            isToSend = true;
        }

        if (ctxObj.getAttribute("TEMPLATE") != null && ctxObj.getAttribute("TEMPLATE").getValueLong() > 0)
        {
            addMessage(toPrint,
                TEMPLATE_PRV +
                ctxObj.getObject(ctxObj.getAttribute("TEMPLATE")
                                       .getValueLong()).getCARDIDwLink());
            toPrint.append("</span>");
        }

        if (ctxObj.exists() && isToSend)
        {
            if("messageLetter".equals(ctxObj.getName()))
            {
                if(ctxObj.getAttribute("impCentral") != null &&
                !"1".equals(ctxObj.getAttribute("impCentral").getValueString())
                )
                {
                    if(ctxObj.getAttribute("dtEfectiv") != null && 
                       ctxObj.getAttribute("dtEfectiv").getValueDate() == null)
                    {
                        addMessage(toPrint, MSG_NOT_PRINT);
                    }
//                    else
//                    {
//                        addMessage(toPrint, MSG_PRINT);                        
//                    }
                }
            }
        }
        if (ctxObj.exists() && isToSend)
        {
            if (!MessageUtils.alreadySend(ctxObj))
            {
                addMessage(toPrint, MSG_NOT_SENT_PRV);
            }
            else
            {
                addMessage(toPrint, MSG_SENT_PRV);
            }
        }

        if (ctxObj.exists() && !isToSend)
        {
            //prioridade
            if ("0".equals(ctxObj.getAttribute("priority")
                                     .getValueString()))
            {
                addMessage(toPrint, LOW_IMP_PRV);
            }

            if ("2".equals(ctxObj.getAttribute("priority")
                                     .getValueString()))
            {
                addMessage(toPrint, HIGH_IMP_PRV);
            }

            if ("1".equals(ctxObj.getAttribute("send_read_receipt")
                                     .getValueString()))
            {
                //vou ter que ir vêr se este utilizador já enviou o recibo
                boObject performer = boObject.getBoManager().loadObject(ctxObj.getEboContext(), 
                                doc.getEboContext().getBoSession().getPerformerBoui());
                ArrayList deliveries = MessageUtils.getDeliveryMessage(ctxObj, performer);
                boolean alreadySendReceipt = true;
                Date sendReceipt = null;
                for (int i = 0; i < deliveries.size(); i++) 
                {
                    if(((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").getValueDate() == null)
                    {
                        alreadySendReceipt = false;
                    }
                    else
                    {
                        sendReceipt = ((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").
                            getValueDate();
                    }
                }
                
                if (!alreadySendReceipt)
                {
                    //ainda não enviou
                    addMessage(toPrint, SEND_RECEIPT_PRV);
                }
                else
                {
                    //ja enviou vou apresentar a data
                    String dt = null;

                    try
                    {
                        SimpleDateFormat df = new SimpleDateFormat(
                                "dd/MM/yyyy HH:mm");
                        dt = df.format(sendReceipt);
                    }
                     catch (Exception e)
                    {
                        logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DOING_THE_EMAIL_DATESET"),
                            e);
                    }

                    if (dt != null)
                    {
                        addMessage(toPrint, RECEIPT_SENT_PRV + dt + ".");
                        toPrint.append("</span>");
                    }
                }
            }
            if("1".equals(ctxObj.getAttribute("signedMsg")
                                     .getValueString()))
            {
                addMessage(toPrint, "<br/><span>O email foi assinado e a assinatura foi verificada.</span>");
            }
            if("0".equals(ctxObj.getAttribute("signedMsg")
                                     .getValueString()))
            {
                addMessage(toPrint, "<br/><span>O email foi assinado no entanto a assinatura falhou.</span>");
            }
        }

        //verificar
        if (isToSend && !MessageUtils.alreadySend(ctxObj))
        {
            if (deliverMessage(toPrint, ctxObj, createdReceiveMessage))
            {
                if (ctxObj.valid())
                {
                    return true;
                }
                else
                {
                    doc.renderErrors(ctxObj);

                    return false;
                }
            }
        }
        else
        {
            return true;
        }

        return false;
    }

//    public static boolean generateMessageInformation(StringBuffer toPrint,
//        boObject ctxObj, docHTML doc) throws boRuntimeException
    public static boolean generateMessageInformation(StringBuffer toPrint,
        boObject ctxObj, EboContext context) throws boRuntimeException
        
    {
        boolean isToSend = false;
        Controller auxController = context.getController();
		
        if( ctxObj.exists() && auxController != null && !"XwfController".equals(auxController.getName()) )
        {        
            StringBuffer pbouis= new StringBuffer();
            StringBuffer boql = new StringBuffer("SELECT xwfProgramRuntime WHERE message = ");
            boql.append( ctxObj.getBoui() );
            boql.append(" ORDER BY SYS_DTCREATE");
            boObjectList bolist = boObjectList.list( context, boql.toString(),1, false );
            bolist.beforeFirst();
            while ( bolist.next() )
            {   
                if ( pbouis.indexOf( bolist.getCurrentBoui()+";") == -1 )
                {
                    addMessage(toPrint,
                        bolist.getObject().getCARDIDwLink().toString() + "&nbsp;&nbsp;");
                    pbouis.append( bolist.getCurrentBoui()+";" );
                }
            }
            if(pbouis.length() > 0)
            {
                addMessage(toPrint,LINESTART);
            }
        }
        XwfController controller;
//        Controller auxController = doc.getController();
        
        docHTML doc = null;
                 
        if(auxController != null)
        {
            doc = auxController.getDocHTML();
            if("BasicController".equals(auxController.getName()))
            {
                if(doc.getControllerQueue().containsKey(XwfKeys.CONTROLLER_NAME_KEY))
                {
                    controller = (XwfController)doc.getControllerQueue().get(XwfKeys.CONTROLLER_NAME_KEY);
                }
                else
                {
                    return true;
                }
            }
            else
            {
                controller = (XwfController)auxController;
            }
        }
        else
        {
            return true;
        }

        boObject activity = controller.getRuntimeActivity();        
        boolean createdReceiveMessage = "xwfCreateReceivedMessage".equals(activity.getName());

        if (ctxObj.getMode() == boObject.MODE_EDIT_TEMPLATE)
        {
            return true;
        }


        isToSend = false;

        if ("xwfActivitySend".equals(activity.getName()))
        {
            isToSend = true;
        }

        if ((ctxObj.getMode() == boObject.MODE_EDIT) ||
                (ctxObj.getMode() == boObject.MODE_NEW))
        {
            if (ctxObj.getAttribute("TEMPLATE") != null)
            {
//                if (ctxObj.getAttribute("TEMPLATE").getValueLong() == 0)
//                {
//                    addMessage(toPrint,
//                        ACTIVITY_1 + ctxObj.getBoui() + ACTIVITY_2);
//                }
//                else
//                {
//                    addMessage(toPrint,
//                        TEMPLATE +
//                        ctxObj.getObject(ctxObj.getAttribute("TEMPLATE")
//                                               .getValueLong()).getCARDIDwLink());
//                }
                if (ctxObj.exists() && isToSend)
                {
                    if("messageLetter".equals(ctxObj.getName()))
                    {
                        if(ctxObj.getAttribute("impCentral") != null &&
                        !"1".equals(ctxObj.getAttribute("impCentral").getValueString())
                        )
                        {
                            if(ctxObj.getAttribute("dtEfectiv") != null && 
                               ctxObj.getAttribute("dtEfectiv").getValueDate() == null)
                            {
                                addMessage(toPrint, MSG_NOT_PRINT);
                            }
//                            else
//                            {
//                                addMessage(toPrint, MSG_PRINT);                        
//                            }
                        }
                    }
                }
                if (ctxObj.exists() && isToSend)
                {
                    if (!MessageUtils.alreadySend(ctxObj))
                    {
                        addMessage(toPrint, MSG_NOT_SENT);
                    }
                    else
                    {
                        addMessage(toPrint, MSG_SENT);
                    }
                }

                if (ctxObj.exists() && !isToSend)
                {
                    //prioridade
                    if ("0".equals(ctxObj.getAttribute("priority")
                                             .getValueString()))
                    {
                        addMessage(toPrint, LOW_IMP);
                    }

                    if ("2".equals(ctxObj.getAttribute("priority")
                                             .getValueString()))
                    {
                        addMessage(toPrint, LOW_IMP);
                    }

                    if ("1".equals(ctxObj.getAttribute("send_read_receipt")
                                             .getValueString()))
                    {
                        //vou ter que ir vêr se este utilizador já enviou o recibo
                        boObject performer = boObject.getBoManager().loadObject(context, 
                                        context.getBoSession().getPerformerBoui());
                        ArrayList deliveries = MessageUtils.getDeliveryMessage(ctxObj, performer);
                        boolean alreadySendReceipt = true;
                        Date sendReceipt = null;
                        for (int i = 0; i < deliveries.size(); i++) 
                        {
                            if(((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").getValueDate() == null)
                            {
                                alreadySendReceipt = false;
                            }
                            else
                            {
                                sendReceipt = ((boObject)deliveries.get(i)).getAttribute("date_sent_read_receipt").
                                    getValueDate();
                            }
                        }
                        
                        if (!alreadySendReceipt)
                        {
                            //ainda não enviou
                            addMessage(toPrint, SEND_RECEIPT);
                        }
                        else
                        {
                            //ja enviou vou apresentar a data
                            String dt = null;

                            try
                            {
                                SimpleDateFormat df = new SimpleDateFormat(
                                        "dd/MM/yyyy HH:mm");
                                dt = df.format(sendReceipt);
                            }
                             catch (Exception e)
                            {
                                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_DOING_THE_EMAIL_DATESET"),
                                    e);
                            }

                            if (dt != null)
                            {
                                addMessage(toPrint, RECEIPT_SENT + dt + ".");
                            }
                        }
                    }
                    if(ctxObj.getAttribute("signedMsg").getValueString() != null &&
                       ctxObj.getAttribute("signedMsg").getValueString().length() > 0 
                    )
                    {
                        addMessage(toPrint, "</td></tr><tr><td colspan='4'>" + ctxObj.getAttribute("signedMsg").getValueString());
                    }
                }                
            }
        }

        //verificar
        if (isToSend && !MessageUtils.alreadySend(ctxObj))
        {
            if (deliverMessage(toPrint, ctxObj, createdReceiveMessage))
            {
                if (ctxObj.valid())
                {
                    return true;
                }
                else
                {
                    if(doc != null)
                    {
                        doc.renderErrors(ctxObj);
                        return false;
                    }
                }
            }
        }
        else
        {
            return true;
        }

        return false;
    }

    public static boolean deliverMessage(StringBuffer toPrint, boObject message, boolean createdReceiveMsg)
    {
        //verificar o modo preferencial de entrega da mensagem
        //acertar o delivery message de cada um dos destinatários
        //se a mensagem tiver um canal definido então todos o recepientes
        //deverão receber por esse canal caso tiverem o endereço do canal definido
        //senão tiverem o remetente deverá ser informado de modo a alterar o canal para esse
        //utilizador.
        //caso o delivery message tenha um canal definido este sobrepõem-se ao canal 
        //definido na mensagem 
        try
        {
            String mediaType = message.getAttribute(ATT_PRF_MEDIA)
                                      .getValueString();
            AttributeHandler attFrom = message.getAttribute(ATT_FROM);
            boObject deliverObj;

            if (attFrom.getValueObject() == null)
            {
                addMessage(toPrint, NO_SENDER);

                return false;
            }
            else
            {
                deliverObj = attFrom.getObject();
            }

            //tenho que verificar todos os canais usados para esta mensagem e 
            //verificar se quem envia tem um endereço para esses canais.
            //            if(verifyDeliverObject(toPrint, deliverObj, message, mediaType, ATT_FROM))
            //            {
            if (EMAIL.equals(mediaType) || BV.equals(mediaType))
            {
                return email(toPrint, message);
            }
            else if (FAX.equals(mediaType))
            {
                return fax(toPrint, message, createdReceiveMsg);
            }
            else if (LETTER.equals(mediaType))
            {
                return letter(toPrint, message, createdReceiveMsg);
            }
            else if (PHONE.equals(mediaType) || SMS.equals(mediaType))
            {
                return phone(toPrint, message);
            }
            else if (MessageUtils.CONVERSATION.equals(mediaType))
            {
                return conversation(toPrint, message);
            }
            else if (MessageUtils.SGIS.equals(mediaType))
            {
                return sgis(toPrint, message);
            }
            else
            {
                return deliver(toPrint, message, createdReceiveMsg);
            }

            //            }
        }
         catch (boRuntimeException e)
        {
        }

        return false;
    }

    private static boolean email(StringBuffer toPrint, boObject message)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        AttributeHandler attFrom = message.getAttribute(ATT_FROM);
        boBridgeIterator itTO = message.getBridge(ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(ATT_BCC).iterator();
        StringBuffer existTO = new StringBuffer("false");
        StringBuffer existCC = new StringBuffer("false");
        StringBuffer existBCC = new StringBuffer("false");
        ArrayList channels;

        if (verifyChannelAddress(toPrint, message, itTO, ATT_EMAIL, EMAIL,
                    existTO) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_EMAIL, EMAIL,
                    existCC) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_EMAIL, EMAIL,
                    existBCC))
        {
            channels = getChannelsMessage(message);

            //verificar 
            if (!("true".equals(existTO.toString()) ||
                    "true".equals(existCC.toString()) ||
                    "true".equals(existBCC.toString())))
            {
                addMessage(toPrint, NO_RECEIVER);

                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean fax(StringBuffer toPrint, boObject message, boolean createdReceiveMsg)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;

//        if (message.getAttribute("faxFormat").getValueObject() == null && !createdReceiveMsg)
//        {
//            addMessage(toPrint, CHOOSE_FAX_FORMAT);
//
//            return false;
//        }

        boBridgeIterator itTO = message.getBridge(ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(ATT_BCC).iterator();
        StringBuffer existTO = new StringBuffer("false");
        StringBuffer existCC = new StringBuffer("false");
        StringBuffer existBCC = new StringBuffer("false");
        ArrayList channels;

        if (verifyChannelAddress(toPrint, message, itTO, ATT_FAX, FAX, existTO) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_FAX, FAX,
                    existCC) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_FAX, FAX,
                    existBCC))
        {
            //verificar
            if (!("true".equals(existTO.toString()) ||
                    "true".equals(existCC.toString()) ||
                    "true".equals(existBCC.toString())))
            {
                addMessage(toPrint, NO_RECEIVER);

                return false;
            }

            return true;
        }

        return false;
    }

    private static boolean letter(StringBuffer toPrint, boObject message, boolean createdReceiveMsg)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;

//        if (message.getAttribute("letterFormat").getValueObject() == null && !createdReceiveMsg)
//        {
//            addMessage(toPrint, CHOOSE_LETTER_FORMAT);
//
//            return false;
//        }

        boBridgeIterator itTO = message.getBridge(ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(ATT_BCC).iterator();
        StringBuffer existTO = new StringBuffer("false");
        StringBuffer existCC = new StringBuffer("false");
        StringBuffer existBCC = new StringBuffer("false");
        ArrayList channels;

        if (verifyChannelAddress(toPrint, message, itTO, ATT_STREET, LETTER,
                    existTO) &&
                verifyChannelAddress(toPrint, message, itTO, ATT_POSTAL,
                    LETTER, new StringBuffer("false")) &&
                verifyChannelAddress(toPrint, message, itTO, ATT_CPLOCAL,
                    LETTER, new StringBuffer("false")) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_STREET,
                    LETTER, existCC) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_POSTAL,
                    LETTER, new StringBuffer("false")) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_CPLOCAL,
                    LETTER, new StringBuffer("false")) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_STREET,
                    LETTER, existBCC) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_POSTAL,
                    LETTER, new StringBuffer("false")) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_CPLOCAL,
                    LETTER, new StringBuffer("false")))
        {
            boBridgeIterator it = null;
            boolean hasRecipeint = false;

            for (int i = 0; (i < 3) && !hasRecipeint; i++)
            {
                if (i == 0)
                {
                    it = itTO;
                }

                if (i == 1)
                {
                    it = itCC;
                }

                if (i == 2)
                {
                    it = itBCC;
                }

                it.beforeFirst();

                while (it.next() && !hasRecipeint)
                {
                    hasRecipeint = true;
                }
            }

            if (hasRecipeint)
            {
                //                generatedDocuments(message, itTO);
                //                generatedDocuments(message, itCC);
                //                generatedDocuments(message, itBCC);
                return true;
            }
            else
            {
                addMessage(toPrint, NO_RECEIVER);

                return false;
            }
        }

        return false;
    }

    private static boolean phone(StringBuffer toPrint, boObject message)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boolean existsRecipient = false;
        String mediaType = message.getAttribute(ATT_PRF_MEDIA).getValueString();
        StringBuffer existRecTO = new StringBuffer("false");
        boBridgeIterator itTO = message.getBridge(ATT_TO).iterator();
        StringBuffer existRecCC = new StringBuffer("false");
        boBridgeIterator itCC = message.getBridge(ATT_CC).iterator();
        StringBuffer existRecBCC = new StringBuffer("false");
        boBridgeIterator itBCC = message.getBridge(ATT_BCC).iterator();
        ArrayList channels;

        if (verifyChannelAddress(toPrint, message, itTO, ATT_PHONE, PHONE.equals(mediaType) ? PHONE:SMS,
                    existRecTO) &&
                verifyChannelAddress(toPrint, message, itCC, ATT_PHONE, PHONE.equals(mediaType) ? PHONE:SMS,
                    existRecCC) &&
                verifyChannelAddress(toPrint, message, itBCC, ATT_PHONE, PHONE.equals(mediaType) ? PHONE:SMS,
                    existRecBCC))
        {
            boBridgeIterator it = null;

            for (int i = 0; i < 3; i++)
            {
                if (i == 0)
                {
                    it = itTO;
                }

                if (i == 1)
                {
                    it = itCC;
                }

                if (i == 2)
                {
                    it = itBCC;
                }

                it.beforeFirst();

                while (it.next())
                {
                    correct = true;
                    it.currentRow().getObject().getAttribute("media")
                      .setValueObject(null, AttributeHandler.INPUT_FROM_INTERNAL);
                }
            }

            if (!correct)
            {
                addMessage(toPrint, NO_RECEIVER);
            }
        }

        return correct;
    }

    private static boolean deliver(StringBuffer toPrint, boObject message, boolean createdReceiveMsg)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um canal preferencial definido
        //e se têm um endereço para esse canal
        boolean correct = true;
        boBridgeIterator itTO = message.getBridge(ATT_TO).iterator();
        boBridgeIterator itCC = message.getBridge(ATT_CC).iterator();
        boBridgeIterator itBCC = message.getBridge(ATT_BCC).iterator();

        if (verifyUserPreferedChannelAddress(toPrint, message, itTO, createdReceiveMsg) &&
                verifyUserPreferedChannelAddress(toPrint, message, itCC, createdReceiveMsg) &&
                verifyUserPreferedChannelAddress(toPrint, message, itBCC, createdReceiveMsg))
        {
            boBridgeIterator it = null;
            boolean hasRecipeint = false;

            for (int i = 0; (i < 3) && !hasRecipeint; i++)
            {
                if (i == 0)
                {
                    it = itTO;
                }

                if (i == 1)
                {
                    it = itCC;
                }

                if (i == 2)
                {
                    it = itBCC;
                }

                it.beforeFirst();

                while (it.next() && !hasRecipeint)
                {
                    hasRecipeint = true;
                }
            }

            if (hasRecipeint)
            {
                return true;
            }
            else
            {
                addMessage(toPrint, NO_RECEIVER);

                return false;
            }
        }

        return false;
    }

    private static boolean verifyChannelAddress(StringBuffer toPrint,
        boObject message, boBridgeIterator it, String attName, String type,
        StringBuffer existRecipient) throws boRuntimeException
    {
        boObject auxDeliver = null;
        boolean correct = true;

        if (it != null)
        {
            it.beforeFirst();

            while (it.next())
            {
                existRecipient.delete(0, existRecipient.length());
                existRecipient.append("true");
                auxDeliver = it.currentRow().getObject();
                correct = verifyDeliverObject(toPrint, auxDeliver, message,
                        type, attName);
            }
        }

        return correct;
    }

    //    private static boolean verifyMessage(StringBuffer toPrint, boObject message, String type, String attName) throws boRuntimeException
    //    {
    //        boObject auxDeliver;
    //        if(type == PHONE || type == CONVERSATION)
    //        {
    //            return true;
    //        }
    //        else if(type == EMAIL)
    //        {
    //            auxDeliver = message.getAttribute(attName).getObject(); 
    //            if(auxDeliver.get
    //        }
    //    }
    private static boolean verifyDeliverObject(StringBuffer toPrint,
        boObject auxDeliver, boObject message, String type, String attName)
        throws boRuntimeException
    {
        if ((auxDeliver.getAttribute("media").getValueString() == null) ||
                "".equals(auxDeliver.getAttribute("media").getValueString()))
        {
            //senão existir um canal definido para este destinatário 
            //então uso o da mensagem
            if ((auxDeliver.getAttribute(attName).getValueString() == null) ||
                    "".equals(auxDeliver.getAttribute(attName).getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO + auxDeliver.getCARDIDwLink() + BAD_ADDRESS);

                return false;
            }
            else if(MessageUtils.SMS == type && !MessageServer.validSMSNumber(auxDeliver.getAttribute(attName).getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO +  auxDeliver.getCARDIDwLink() + BAD_TLM);
                return false;
            }
            else
            {
                auxDeliver.getAttribute("media").setValueString(type,
                    AttributeHandler.INPUT_FROM_INTERNAL);
            }
        }
        else
        {
            if ((auxDeliver.getAttribute("media").getInputType() != AttributeHandler.INPUT_FROM_USER) 
                &&
                    !type.equals(auxDeliver.getAttribute("media")
                                               .getValueString()))
            {
                auxDeliver.getAttribute("media").setValueString(type,
                    AttributeHandler.INPUT_FROM_INTERNAL);
            }

            //existe um canal definido para este destinatário vou verificar
            //se existe um endereço definido correctamente para o canal
            if (!verifyChannelAddress(toPrint, message, auxDeliver,
                        auxDeliver.getAttribute("media").getValueString()))
            {
                return false;
            }
        }

        return true;
    }

    private static void generatedDocuments(boObject message, boBridgeIterator it)
        throws boRuntimeException
    {
        boObject auxDeliver;
        boObject cloneMessage = null;
        String type = message.getAttribute("preferedMedia").getValueString();

        if ((type == LETTER) || (type == FAX))
        {
            it.beforeFirst();

            while (it.next())
            {
                auxDeliver = it.currentRow().getObject();

                cloneMessage = message.cloneObject();
                cloneMessage.getBridge("to").truncate();
                cloneMessage.getBridge("to").add(auxDeliver.getBoui());
                

                if (type == LETTER)
                {
                    LetterServer.merge(message, cloneMessage,
                        auxDeliver);
                }
                else
                {
                    FaxServer.merge(message, cloneMessage,
                        auxDeliver);
                }
            }
        }
    }

    private static boolean verifyChannelAddress(StringBuffer toPrint,
        boObject message, boObject deliveryMessage, String mediaType)
        throws boRuntimeException
    {
        if (EMAIL.equals(mediaType) || BV.equals(mediaType))
        {
            if ((deliveryMessage.getAttribute(ATT_EMAIL).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_EMAIL)
                                                 .getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO + deliveryMessage.getCARDIDwLink() +
                    PREFERED_CHANNEL_NOT_DEFINED_3);

                return false;
            }
        }
        else if (LETTER.equals(mediaType))
        {
            if ((deliveryMessage.getAttribute(ATT_STREET).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_STREET)
                                                 .getValueString()) ||
                    (deliveryMessage.getAttribute(ATT_POSTAL).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_POSTAL)
                                                 .getValueString()) ||
                    (deliveryMessage.getAttribute(ATT_CPLOCAL).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_CPLOCAL)
                                                 .getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO + deliveryMessage.getCARDIDwLink() +
                    PREFERED_CHANNEL_NOT_DEFINED_3);

                return false;
            }
        }
        else if (FAX.equals(mediaType))
        {
            if ((deliveryMessage.getAttribute(ATT_FAX).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_FAX)
                                                 .getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO + deliveryMessage.getCARDIDwLink() +
                    PREFERED_CHANNEL_NOT_DEFINED_2);

                return false;
            }
        }
        else if (PHONE.equals(mediaType) || SMS.equals(mediaType))
        {
            if ((deliveryMessage.getAttribute(ATT_PHONE).getValueString() == null) ||
                    "".equals(deliveryMessage.getAttribute(ATT_PHONE)
                                                 .getValueString()))
            {
                addMessage(toPrint,
                    DESTINATARIO + deliveryMessage.getCARDIDwLink() +
                    PREFERED_CHANNEL_NOT_DEFINED_2);

                return false;
            }
        }

        return true;
    }

    private static boolean verifyUserPreferedChannelAddress(
        StringBuffer toPrint, boObject message, boBridgeIterator it, boolean createdReceiveMsg)
        throws boRuntimeException
    {
        boObject auxDeliver = null;
        boolean correct = true;
        String mediaType;

        if (it != null)
        {
            it.beforeFirst();

            while (it.next())
            {
                auxDeliver = it.currentRow().getObject();
                mediaType = auxDeliver.getAttribute("refObj").getObject()
                                      .getAttribute("preferedMedia")
                                      .getValueString();

                if ((mediaType == null) || "".equals(mediaType))
                {
                    addMessage(toPrint,
                        DESTINATARIO + auxDeliver.getCARDIDwLink() +
                        PREFERED_CHANNEL_NOT_DEFINED_2);
                    correct = false;
                }
                else
                {
                    correct = verifyChannelAddress(toPrint, message,
                            auxDeliver, mediaType);

                    if (correct)
                    {
                        ArrayList r = getChannelsMessage(message);

                        if ((r.indexOf(LETTER) != -1) &&
                                (message.getAttribute("letterFormat")
                                            .getValueObject() == null) && !createdReceiveMsg)
                        {
                            addMessage(toPrint, CHOOSE_LETTER_FORMAT);
                            correct = false;
                        }

                        if ((r.indexOf(FAX) != -1) &&
                                (message.getAttribute("faxFormat")
                                            .getValueObject() == null) && !createdReceiveMsg)
                        {
                            addMessage(toPrint, CHOOSE_FAX_FORMAT);
                            correct = false;
                        }
                    }
                }
            }
        }

        return correct;
    }

    private static ArrayList getChannelsMessage(boObject message)
        throws boRuntimeException
    {
        ArrayList toRet = new ArrayList();
        String[] bridges = { ATT_TO, ATT_CC, ATT_BCC };
        boBridgeIterator it = null;
        String mediaType = message.getAttribute(ATT_PRF_MEDIA).getValueString();

        if (!mediaType.equals(NOT_DEFINED))
        {
            toRet.add(mediaType);
        }

        for (int i = 0; i < bridges.length; i++)
        {
            it = message.getBridge(bridges[i]).iterator();
            it.beforeFirst();

            boObject deliveryMsg;

            while (it.next())
            {
                deliveryMsg = it.currentRow().getObject();

                if ((deliveryMsg.getAttribute("media").getValueString() != null) &&
                        !"".equals(deliveryMsg.getAttribute("media")
                                                  .getValueString()))
                {
                    if (!toRet.contains(deliveryMsg.getAttribute("media")
                                                       .getValueString()))
                    {
                        toRet.add(deliveryMsg.getAttribute("media")
                                             .getValueString());
                    }
                }
            }
        }

        return toRet;
    }

    private static boolean conversation(StringBuffer toPrint, boObject message)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boBridgeIterator it = null;
        boolean existsRecipient = false;

        for (int i = 0; i < MessageUtils.bridges.length; i++)
        {
            it = message.getBridge(MessageUtils.bridges[i]).iterator();
            it.beforeFirst();

            while (it.next())
            {
                existsRecipient = true;
                it.currentRow().getObject().getAttribute("media")
                  .setValueObject(null, AttributeHandler.INPUT_FROM_INTERNAL);
            }
        }

        if (!existsRecipient)
        {
            addMessage(toPrint, NO_RECEIVER);
        }

        return existsRecipient;
    }

    private static boolean sgis(StringBuffer toPrint, boObject message)
        throws boRuntimeException
    {
        //vou verificar se todos os destinatários têm um endereço para este canal
        boolean correct = true;
        boolean existsRecipient = false;
        boBridgeIterator it = null;
        boObject auxObj;

        for (int i = 0; i < MessageUtils.bridges.length; i++)
        {
            it = message.getBridge(MessageUtils.bridges[i]).iterator();
            it.beforeFirst();

            while (it.next())
            {
                existsRecipient = true;
                auxObj = it.currentRow().getObject().getAttribute("refObj")
                           .getObject();

                if (XEOUserUtils.isXEOUser(auxObj))
                {
                    if ((auxObj.getAttribute("username").getValueObject() == null) ||
                            (auxObj.getAttribute("password").getValueObject() == null))
                    {
                        addMessage(toPrint,
                            DESTINATARIO +
                            it.currentRow().getObject().getCARDIDwLink() +
                            BAD_SGIS_USER);
                        correct = false;
                    }
                }
                else if (!"workQueue".equals(auxObj.getName()) &&
                        !"Ebo_Group".equals(auxObj.getName()) &&
                        !"Ebo_Role".equals(auxObj.getName()))
                {
                    addMessage(toPrint,
                        DESTINATARIO +
                        it.currentRow().getObject().getCARDIDwLink() +
                        BAD_MSG_TYPE);
                    correct = false;
                }

                it.currentRow().getObject().getAttribute("media")
                  .setValueString(MessageUtils.SGIS, AttributeHandler.INPUT_FROM_INTERNAL);
            }
        }

        if (correct && !existsRecipient)
        {
            addMessage(toPrint, NO_RECEIVER);
        }

        return correct;
    }

    private static void addMessage(StringBuffer sb, String msg)
    {
        if (sb.indexOf(msg) == -1)
        {
            sb.append(msg);
        }
    }
}
