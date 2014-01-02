/*Enconding=UTF-8*/
package netgest.bo.message;

import netgest.bo.message.utils.Attach;

import java.util.ArrayList;
import java.util.Date;


/**
 *
 * @Company Enlace3
 * @author Francisco Luís Brinó Câmara
 * @version 1.0
 * @since
 */
public interface Message
{
    public void setFrom(Address address);

    public void setTO(Address[] address);

    public void addTO(Address address);

    public void setCC(Address[] address);

    public void addCC(Address address);

    public void setBCC(Address[] address);

    public void addBCC(Address address);

    public void setAttachs(Attach[] attach);

    public void addAttach(Attach attach);

    public void setContent(String content);

    public void setSentDate(Date date);

    public void setReceivedDate(Date date);

    public void setSubject(String subject);

    public void setPriority(int priority);

    public void setReadError(boolean error);

    public void setContextThread(String context);

    public void setReturnReadReceipt(boolean value);

    public void setAskForReadReceipt(boolean value);

    public void setAskForDeliveredReceipt(boolean value);

    public String getReadReceiptEmail();

    public void setMessageID(String messageid);

    public Address getFrom();

    public Address[] getTO();

    public Address[] getCC();

    public Address[] getBCC();

    public Attach[] getAttach();

    public String getContent();

    public Date getSentDate();

    public Date getReceivedDate();

    public String getSubject();

    public int getPriority();

    public String getContextThread();

    public boolean readError();

    public boolean isReceivedMessage();

    public boolean isToSendMessage();

    public boolean isDeliveredReceipt();

    public boolean isReadReceipt();

    public boolean askForReadReceipt();

    public boolean askForDeliverReceipt();

    public boolean isToReturnReadReceipt();

    public String getMessageID();

    public String getXEOID();
}
