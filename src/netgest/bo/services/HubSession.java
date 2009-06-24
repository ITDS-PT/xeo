/*Enconding=UTF-8*/
package netgest.bo.services;
import java.util.*;
import netgest.bo.system.*;
import netgest.bo.runtime.*;

public class HubSession 
{
  public HubSession(boSession session,String sessionid,EboContext ectx,Date timestamp)
  {
    this.session=session;
    this.sessionid=sessionid;
    this.ectx=ectx;
    this.timestamp=timestamp;
  }
    private boSession session=null;
    private String sessionid=null;
    private EboContext ectx=null;
    private Date timestamp=null;

    public boSession getSession()
    {
      return this.session;
    }

    public void setSession(boSession session)
    {
      this.session=session;
    }

    public String getSessionID()
    {
      return this.sessionid;
    }

    public void setSessionID(String sessionid)
    {
      this.sessionid=sessionid;
    }
    
    public Date getTimeStamp()
    {
      return this.timestamp;
    }

    public void setTimeStamp(Date timestamp)
    {
      this.timestamp=timestamp;
    }
    
    public EboContext getEboContext()
    {
      return this.ectx;
    }

    public void setEboContext(EboContext ectx)
    {
      this.ectx=ectx;
    }
}