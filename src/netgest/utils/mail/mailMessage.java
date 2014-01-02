/*Enconding=UTF-8*/
package netgest.utils.mail;
import java.util.Vector;

public class mailMessage extends mail 
{
  private mailAddress From=null;
  private String Subject=null;
  private Vector Attach=new Vector();
  private Vector Recipient=new Vector();
  private String Body=null;
  private String BodyHtml=null;
  private Vector CC=new Vector();
  private Vector BCC=new Vector();
  private String DataRecebido="";
  private String DataEnviado="";
  private boolean isDeliverReceipt=false;
  private boolean isReadReceipt=false;
  private boolean ReturnReceipt=false;  
  private String messageID;
  boolean askForReadReceipt = false;
  boolean askForDeliverReceipt = false;
  String receiptThread = null;
  private int priority = mail.NORMAL;
  private boolean readWerror=false;
  
  public mailMessage()
  {
  }
  
  public mailAddress getFrom(){return From;}
  public String getSubject(){return Subject;}
  public Vector getAttach(){return Attach;}
  public Vector getRecipient(){return Recipient;}
  public String getBody(){return Body;}
  public String getBodyHtml(){return BodyHtml;}    
  public Vector getCC(){return CC;}
  public Vector getBCC(){return BCC;}
  public String getDataRecebido(){return DataRecebido;}
  public String getDataEnviado(){return DataEnviado;}
  public boolean getReturnReceipt(){return ReturnReceipt;}
  public boolean askForReadReceipt(){return askForReadReceipt;}
  public boolean askForDeliverReceipt(){return askForDeliverReceipt;}
  public boolean isDeliveredReceipt(){return isDeliverReceipt;}
  public boolean isReadReceipt(){return isReadReceipt;}
  public String getReceiptThread(){return receiptThread;}
  public int getPriority(){return priority;}
  public boolean getReadWerror(){return readWerror;}

  public void setFrom(mailAddress From)
  {
/*    if (From!=null)
      if (From.indexOf("<")!=-1)
        From=From.substring(From.indexOf("<")+1,From.indexOf(">")); */

    this.From=From;
  }
  public void setSubject(String Subject){this.Subject=Subject;}
  public void addAttach(Attach attach)
  {
    if (this.Attach==null)this.Attach=new Vector();
    this.Attach.add(attach);
  }
  public void addAttach(String location,String id)
  {
    if (this.Attach==null)this.Attach=new Vector();
    this.Attach.add(new Attach(location,id));
  }
  public void addAttach(String location,String id,boolean deleteAfterUse)
  {
    if (this.Attach==null)this.Attach=new Vector();
    this.Attach.add(new Attach(location,id,deleteAfterUse));
  }  

  public void addAttach(String location,String id,boolean deleteAfterUse,boolean inline)
  {
    if (this.Attach==null)this.Attach=new Vector();
    this.Attach.add(new Attach(location,id,deleteAfterUse,inline));
  }  
  
  public void addRecipient(String name,String email)
  {
    if (this.Recipient==null)this.Recipient=new Vector();
    this.Recipient.add(new mailAddress(name,email));    
  }
  public void addRecipient(mailAddress recipient)
  {
    if (this.Recipient==null)this.Recipient=new Vector();
    this.Recipient.add(recipient);    
  }

  public void addCC(String name,String email)
  {
    if (this.CC==null)this.CC=new Vector();
    this.CC.add(new mailAddress(name,email));    
  }  
  public void addCC(mailAddress cc)
  {
    if (this.CC==null)this.CC=new Vector();
    this.CC.add(cc);    
  }

  public void addBCC(String name,String email)
  {
    if (this.BCC==null)this.BCC=new Vector();
    this.BCC.add(new mailAddress(name,email));    
  }
  public void addBCC(mailAddress bcc)
  {
    if (this.BCC==null)this.BCC=new Vector();
    this.BCC.add(bcc);    
  }
  
  public void setRecipient(Vector Recipient){this.Recipient=Recipient;}    
  public void addRecipient(String strRecipient)
  {
    if (this.Recipient==null)this.Recipient=new Vector();
    this.Recipient.add(strRecipient);
  }
  public void setBody(String Body){this.Body=Body;}
  public void setBodyHtml(String BodyHtml){this.BodyHtml=BodyHtml;}    
  public void setCC(Vector CC){this.CC=CC;}
  public void addCC(String strCC)
  {
    if (this.CC==null)this.CC=new Vector();
    this.CC.add(strCC);
  }
  public void setBCC(Vector BCC){this.BCC=BCC;}
  public void addBCC(String strBCC)
  {
    if (this.BCC==null)this.BCC=new Vector();
    this.BCC.add(strBCC);      
  }    
  public void setDataRecebido(String DataRecebido){this.DataRecebido=DataRecebido;}
  public void setDataEnviado(String DataEnviado){this.DataEnviado=DataEnviado;}
  public void setReturnReceipt(boolean ReturnReceipt){this.ReturnReceipt=ReturnReceipt;}
  public void setReadWerror(boolean value){this.readWerror=value;}

  public String getMessageID()
  {
    return messageID;
  }

  public void setMessageID(String newMessageID)
  {
    messageID = newMessageID;
  }
  
  public void setAskForReadReceipt(boolean b)
  {
    askForReadReceipt = b;
  }
  
  public void setAskForDeliverReceipt(boolean b)
  {
    askForDeliverReceipt = b;
  }
  
  public void setIsDeliveredReceipt(boolean b)
  {
    isDeliverReceipt = b;
  }
  
  public void setIsReadReceipt(boolean b)
  {
    isReadReceipt = b;
  }

  public void setReceiptThread(String s)
  {
    receiptThread = s;
  }
  
  public void setPriority(int l)
  {
    priority = l;
  }  
}