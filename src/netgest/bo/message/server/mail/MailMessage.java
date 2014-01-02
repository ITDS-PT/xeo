/*Enconding=UTF-8*/
package netgest.bo.message.server.mail;

import netgest.bo.message.Address;
import netgest.bo.message.Message;

import java.util.Date;
import java.util.Vector;
import netgest.bo.message.utils.XEOIDUtil;
import netgest.bo.message.utils.Attach;


public class MailMessage extends Mail implements Message
{
    private static final byte TAM = 5;
    private Address from = null;
    private Address replyTo = null;
    private String subject = null;
    private Attach[] attaches = null;
    private Address[] recipients = null;
    private String content = null;
    private String contentHTML = null;
    private Address[] ccs = null;
    private Address[] bccs = null;
    private Date sentDate = null;
    private Date receivedDate = null;
    private String messageID = null;
    private String contextThread = null;
    private int priority = Mail.NORMAL;
    private boolean readWerror = false;
    private boolean partialSend = false;
    private boolean isReceivedMessage = false;
    private boolean isDeliveredReceipt = false;
    
    private boolean isReadReceipt = false;
    private String  readReceiptEmail = null;
    
    private boolean returnReceipt = false;
    private boolean askForReadReceipt = false;
    private boolean askForDeliveredReceipt = false;
    //mensagens no caso do email ser assinado
    private Boolean signedMsg =null;

    //arrays apt
    private int aptAttach = -1;
    private int aptRecipient = -1;
    private int aptCC = -1;
    private int aptBCC = -1;
    
    //binaryMail
    private Attach binaryMail = null;

    public MailMessage()
    {
    }

    private MailMessage(boolean isReadReceipt, boolean isDeliveredReceipt,
        boolean isReceivedMessage)
    {
        this.isReadReceipt = isReadReceipt;
        this.isDeliveredReceipt = isDeliveredReceipt;
        this.isReceivedMessage = isReceivedMessage;
    }

    public static MailMessage getNewMailMessageToSent()
    {
        return new MailMessage();
    }

    public static MailMessage getNewReadReceiptMailMessageToSent()
    {
        return new MailMessage(true, false, true);
    }

    public static MailMessage getNewReceiveMailMessage()
    {
        return new MailMessage(false, false, false);
    }

    public static MailMessage getNewReceiveReadReceiptMailMessage()
    {
        return new MailMessage(true, false, false);
    }

    public static MailMessage getNewReceiveDeliveredReceiptMailMessage()
    {
        return new MailMessage(false, true, false);
    }

    public void setFrom(Address From)
    {
        this.from = From;
    }
    
    public void setReplyTo(Address replyTo)
    {
        this.replyTo = replyTo;
    }

    public void setAttachs(Attach[] attach)
    {
        this.attaches = attach;
        aptAttach = attaches.length;
    }

    public void addAttach(Attach attach)
    {
        if ((aptAttach == -1) &&
                ((attaches == null) || (attaches.length == 0)))
        {
            aptAttach = 0;
            attaches = new Attach[TAM];
        }
        else if (aptAttach == -1)
        {
            aptAttach = attaches.length;
        }

        if (aptAttach < attaches.length)
        {
            attaches[aptAttach] = attach;
            aptAttach++;
        }
        else
        {
            attaches = expandAttachCapacity(attaches, TAM);
            attaches[aptAttach] = attach;
            aptAttach++;
        }
    }

    public void addAttach(String name, String location, String id)
    {
        addAttach(new Attach(name, location, id));
    }

    public void addAttach(String name, String location, String id, boolean deleteAfterUse)
    {
        addAttach(new Attach(name, location, id, deleteAfterUse));
    }

    public void addAttach(String name, String location, String id, boolean deleteAfterUse,
        boolean inline)
    {
        addAttach(new Attach(name, location, id, deleteAfterUse, inline));
    }

    public void setTO(Address[] address)
    {
        this.recipients = address;
        if(address != null)
        {
            aptRecipient = address.length;
        }
        else
        {
            aptRecipient = -1;
        }
    }

    public void addTO(Address address)
    {
        if ((aptRecipient == -1) &&
                ((recipients == null) || (recipients.length == 0)))
        {
            aptRecipient = 0;
            recipients = new Address[TAM];
        }
        else if (aptRecipient == -1)
        {
            aptRecipient = recipients.length;
        }

        if (aptRecipient < recipients.length)
        {
            recipients[aptRecipient] = address;
            aptRecipient++;
        }
        else
        {
            recipients = expandAddressCapacity(recipients, TAM);
            recipients[aptRecipient] = address;
            aptRecipient++;
        }
    }

    public void addTO(String name, String email)
    {
        addTO(new MailAddress(name, email));
    }

    public void setCC(Address[] addresses)
    {
        this.ccs = addresses;
        if(addresses != null)
        {
            aptCC = addresses.length;
        }
        else
        {
            aptCC = -1;
        }
    }

    public void addCC(Address address)
    {
        if ((aptCC == -1) && ((ccs == null) || (ccs.length == 0)))
        {
            aptCC = 0;
            ccs = new Address[TAM];
        }
        else if (aptCC == -1)
        {
            aptCC = ccs.length;
        }

        if (aptCC < ccs.length)
        {
            ccs[aptCC] = address;
            aptCC++;
        }
        else
        {
            ccs = expandAddressCapacity(ccs, TAM);
            ccs[aptCC] = address;
            aptCC++;
        }
    }

    public void addCC(String name, String email)
    {
        addCC(new MailAddress(name, email));
    }

    public void setBCC(Address[] addresses)
    {
        this.bccs = addresses;
        if(addresses != null)
        {
            aptBCC = addresses.length;
        }
        else
        {
            aptBCC = -1;
        }
    }

    public void addBCC(Address address)
    {
        if ((aptBCC == -1) && ((bccs == null) || (bccs.length == 0)))
        {
            aptBCC = 0;
            bccs = new Address[TAM];
        }
        else if (aptBCC == -1)
        {
            aptBCC = bccs.length;
        }

        if (aptBCC < bccs.length)
        {
            bccs[aptBCC] = address;
            aptBCC++;
        }
        else
        {
            bccs = expandAddressCapacity(bccs, TAM);
            bccs[aptBCC] = address;
            aptBCC++;
        }
    }

    public void addBCC(String name, String email)
    {
        addBCC(new MailAddress(name, email));
    }

    public void setContent(String content)
    {
        this.content = content;
    }
    
    public void setContentHTML(String content)
    {
        this.contentHTML = content;
    }
    
    public String getContentHTML()
    {
        return this.contentHTML;
    }

    public void setSentDate(Date d)
    {
        this.sentDate = d;
    }
    
    public void setReceivedDate(Date d)
    {
        this.receivedDate = d;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void setPriority(int priority)
    {
        this.priority = priority;
    }

    public void setReadError(boolean value)
    {
        this.readWerror = value;
    }

    public void setContextThread(String thread)
    {
        this.contextThread = thread;
    }

    public void setMessageID(String messageid)
    {
        this.messageID = messageid;
    }
    
    public void setReturnReadReceipt(boolean value)
    {
        this.returnReceipt = value;
    }

    public void setAskForDeliveredReceipt(boolean value)
    {
        this.askForDeliveredReceipt = value;
    }

    
    public void setAskForReadReceipt(boolean value)
    {
        this.askForReadReceipt = value;
    }
    
    public void setReadReceiptEmail(String emailAddress )
    {
        this.readReceiptEmail = emailAddress;
    }
    
    public String getReadReceiptEmail() {
    	return this.readReceiptEmail;
    }
    
    public void setPartialSend(boolean value)
    {
        this.partialSend = value;
    }
    
    public void setSignedMsg(boolean msg)
    {
        if(msg)
            this.signedMsg = Boolean.TRUE;
        else
            this.signedMsg = Boolean.FALSE;
    }

    public Address getReplyTo()
    {
        return from;
    }

    public Address getFrom()
    {
        return from;
    }

    public Address[] getTO()
    {
        if(aptRecipient == -1) return new Address[0];
        if(recipients.length != aptRecipient)
        {
            recipients = shrinkAddress(recipients, aptRecipient);
        }
        return recipients;
    }

    public Address[] getCC()
    {
        if(aptCC == -1) return new Address[0];
        if(ccs.length != aptCC)
        {
            ccs = shrinkAddress(ccs, aptCC);
        }
        return ccs;
    }

    public Address[] getBCC()
    {
        if(aptBCC == -1) return new Address[0];
        if(bccs.length != aptBCC)
        {
            bccs = shrinkAddress(bccs, aptBCC);
        }
        return bccs;
    }

    public Attach[] getAttach()
    {
        if(aptAttach == -1) return new Attach[0];
        if(attaches.length != aptAttach)
        {
            attaches = shrinkAttach(attaches, aptAttach);
        }
        return attaches;
    }

    public String getContent()
    {
        return content;
    }

    public Date getReceivedDate()
    {
        return receivedDate;
    }
    
    public Date getSentDate()
    {
        return sentDate;
    }

    public String getSubject()
    {
        return subject;
    }

    public int getPriority()
    {
        return priority;
    }

    public String getContextThread()
    {
        return contextThread;
    }

    //métodos para os emails lidos
    public boolean readError()
    {
        return readWerror;
    }
    public boolean isToReturnReadReceipt()
    {
        return returnReceipt;
    }
    public boolean isReceivedMessage()
    {
        return isReceivedMessage;
    }

    public boolean isDeliveredReceipt()
    {
        return isDeliveredReceipt;
    }

    public boolean isReadReceipt()
    {
        return isReadReceipt;
    }
    public String getMessageID()
    {
        return messageID;
    }
    public String getXEOID()
    {
        return XEOIDUtil.MessageXEOID(this);
    }

    //métodos para os emails para enviar
    public boolean isToSendMessage()
    {
        return !isReceivedMessage;
    }

    public boolean partialSend()
    {
        return partialSend;
    }

    public boolean askForDeliverReceipt()
    {
        return askForDeliveredReceipt;
    }

    public boolean askForReadReceipt()
    {
        return askForReadReceipt;
    }
    
    public Boolean getSignedMsg()
    {
        return signedMsg;
    }

    private static Attach[] expandAttachCapacity(Attach[] arr, int tam)
    {
        Attach[] newValue = new Attach[arr.length + tam];
        System.arraycopy(arr, 0, newValue, 0, arr.length);

        return newValue;
    }
    private static Address[] expandAddressCapacity(Address[] arr, int tam)
    {
        Address[] newValue = new Address[arr.length + tam];
        System.arraycopy(arr, 0, newValue, 0, arr.length);

        return newValue;
    }

    private static Address[] shrinkAddress(Address[] arr, int lastPos)
    {
        Address[] newValue = new Address[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);

        return newValue;
    }
    private static Attach[] shrinkAttach(Attach[] arr, int lastPos)
    {
        Attach[] newValue = new Attach[lastPos];
        System.arraycopy(arr, 0, newValue, 0, lastPos);

        return newValue;
    }
    
    public void setBinaryMail(Attach bMail)
    {
        this.binaryMail = bMail;
    }
    
    public Attach getBinaryMail()
    {
        return this.binaryMail;
    }
    
    public boolean isSystemMessage()
    {
        return isDeliveredReceipt() || isReadReceipt();// || isAppointmentMessage 
    }
}
