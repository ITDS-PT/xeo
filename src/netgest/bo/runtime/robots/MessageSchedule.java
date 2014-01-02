/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import netgest.bo.message.MessageServer;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.system.boMail;
import netgest.utils.*;
import netgest.bo.system.Logger;

public class MessageSchedule implements boSchedule
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.MessageSchedule");
  public MessageSchedule()
  {
  }
  
  public void setParameter(String parameter )
  {
    
  }
  
  public boolean doWork(EboContext ctx, boObject objectSchedule ) throws boRuntimeException
  {
    MessageServer.readAll(ctx);
    return true;
  }
}