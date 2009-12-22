package netgest.bo.runtime.robots.blogic;

import java.util.Date;

import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.bo.system.Logger;

public class boSessionCleanAgentBussinessLogic {
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.boSessionCleanAgentBussinessLogic");
    private boApplication p_app = null;    
    private long interval = 7200000;

    public boSessionCleanAgentBussinessLogic(boApplication app)
    {  
      p_app=app;
    }
    
    public void setInterval(long interval) {
        if (interval!=0)this.interval=interval;
    }
      public void execute()
      {    
          boSession session = null;
          EboContext eboctx = null;
          try
          {
             
              session = p_app.boLogin("SYSUSER", boLoginBean.getSystemKey() );
              eboctx = session.createRequestContext( null,null,null );
              boSession[] sessionsActive = session.getApplication().getSessions().getActiveSessions();
              long sessionsCleaned=0;
              for (int i=0;i<sessionsActive.length;i++)
              {
                boSession currSession=(boSession)sessionsActive[i];
                long lastactivity=currSession.getLastActivity().getTime();
                long now=new Date().getTime();
                if (lastactivity<now-interval-60000)
                {
                  sessionsCleaned++;
                  currSession.closeSession();
                }
              }
              logger.finest("boSessionCleanAgent Cleaned Sessions: "+sessionsCleaned);
          }
          catch (boLoginException e)
          {
              logger.severe("Cleaning boSessions XEOLogin Error: ",e);
          }
          catch(Exception e)
          {
              logger.severe("Cleaning boSessions Unexpected Error: ",e);   
          }
          finally
          {
              try
              {
                if (eboctx!=null)eboctx.close();
                  if(session != null)session.closeSession();
              }
              catch (Exception e)
              {
                  
              }
          }
      }  
}
