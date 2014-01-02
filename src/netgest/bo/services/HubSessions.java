/*Enconding=UTF-8*/
package netgest.bo.services;
import java.util.*;
import netgest.bo.system.*;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.*;

public class HubSessions
{
  public static Hashtable sessions=new Hashtable();
  public static long sessiontimeout=240;

  public HubSessions()
  {
  }

  public static void addHubSession(HubSession session)
  {

    sessions.put(session.getSessionID(),session);
  }

  public static HubSession getHubSession(String sessionid) throws boRuntimeException
  {
    HubSession session=(HubSession)sessions.get(sessionid);
    if (session!=null)
    {
      Date data=session.getTimeStamp();
      if (!isValid(data,new Date()))
      {
        throw new boRuntimeException(MessageLocalizer.getMessage("THE_SPECIFIED_SESSION_TIMED_OUT"),"", null,"");
      }
    }
    else
     throw new boRuntimeException(MessageLocalizer.getMessage("INVALID_SESSION_ID"),"", null,"");

    return session;
  }

  public static void updateSessionTimeStamp(String sessionid) throws boRuntimeException
  {
    HubSession session=(HubSession)sessions.get(sessionid);
    Date timestamp=(Date)session.getTimeStamp();
    if (timestamp!=null)
    {
      session.setTimeStamp(new Date());
    }
    else
      throw new boRuntimeException(MessageLocalizer.getMessage("INVALID_SESSION_ID"),"", null,"");
  }

  public static boolean isValid(Date d1,Date d2)
  {
    long diff=getHubSessionIdleTime(d1,d2);
    if (diff>sessiontimeout) return false;
    else return true;
  }

  public static long getHubSessionIdleTime(Date d1,Date d2)
  {
    long t1=d1.getTime();
    long t2=d2.getTime();
    long diff=t2-t1;
    diff=(diff/1000);
    return diff;
  }

  public static void cleanInvalidSessions(Date data)
  {
    Enumeration oEnum=sessions.elements();
    while(oEnum.hasMoreElements())
    {
      HubSession hsession=(HubSession)oEnum.nextElement();
      Date d1=hsession.getTimeStamp();
      if (!isValid(d1,data))sessions.remove(hsession.getSessionID());
    }
  }
}