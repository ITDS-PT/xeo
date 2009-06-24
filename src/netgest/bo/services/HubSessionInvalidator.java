/*Enconding=UTF-8*/
package netgest.bo.services;
import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import java.util.*;
import netgest.bo.runtime.robots.*;


public class HubSessionInvalidator implements boSchedule 
{
  public HubSessionInvalidator()
  {
  }
  public void setParameter(String parameter)
  {
    
  }
  
  public boolean doWork(EboContext ctx, boObject objectSchedule ) throws boRuntimeException
  {
    long duration=objectSchedule.getAttribute("duration").getValueLong();
    HubSessions.sessiontimeout=duration/60;
    HubSessions.cleanInvalidSessions(new Date());
    return true;
  }
}