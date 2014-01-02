package netgest.bo.runtime.robots.blogic;

import javax.naming.Context;
import javax.naming.InitialContext;

import javax.rmi.PortableRemoteObject;

import netgest.bo.data.DataException;
import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.robots.boScheduleThread;
import netgest.bo.runtime.robots.ejbtimers.boScheduleThreadEJBLocalHome;
import netgest.bo.runtime.robots.ejbtimers.boScheduleThreadLocalEJB;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginLocalHome;
import netgest.bo.system.boSession;

import netgest.bo.system.Logger;

public class boScheduleAgentBussinessLogic 
{
  //logger
    private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.boScheduleAgentBussinessLogic");
    
    /**
     * 
     * @since 
     */
    
  private boApplication p_app;
  
  private ThreadGroup p_group =null;
  
  public boScheduleAgentBussinessLogic(boApplication boapp ,ThreadGroup group)
  {
    p_group=group;
    p_app=boapp;
  }
  
  public void execute()
  {
      logger.finest(LoggerMessageLocalizer.getMessage("STARTING_SCHEDULE_AGENT"));
      boSession session = null;
      Context ic = null;
      try 
      {
          boDefHandler perfdef = boDefHandler.getBoDefinition("iXEOUser");
          boDefHandler scheddef = boDefHandler.getBoDefinition("Ebo_Schedule");
          if( !( perfdef == null || scheddef==null ))
          {          
                  session =  p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
                  EboContext ctx = null;
                 
                  try
                  {
                	  
                      ctx = session.createRequestContext(null,null,null);
                      String sysdate = "sysdate";
                      
                      //Obedece as regras de inicio e fim do agendamento
                      String boql_query="SELECT Ebo_Schedule WHERE ACTIVESTATUS=0 AND STATE=1 AND ";
                      boql_query+="(NEXTRUNTIME<="+sysdate+"  OR LASTRUNTIME IS NULL) and ";
                      boql_query+="(("+sysdate+" >= STARTDATE AND "+sysdate+"<=ENDDATE) OR (("+sysdate+" >= STARTDATE AND ENDDATE IS NULL) OR ";
                      boql_query+="(STARTDATE IS NULL OR "+sysdate+"<=ENDDATE) or (STARTDATE IS NULL AND ENDDATE IS NULL)))";
                      
                      boObjectList list = boObjectList.list(ctx,boql_query);
                      list.beforeFirst();
                      while(list.next()) 
                      { 
                          try 
                          {
                              logger.finest( LoggerMessageLocalizer.getMessage("UPDATE_ACTIVE_SCHEDULE_CONNECTION_HASH")  + ctx.getConnectionData().hashCode() );
                              boObject sched = list.getObject();  
                              sched.getAttribute("activeStatus").setValueString("1");
                              sched.update();
                              logger.finest( LoggerMessageLocalizer.getMessage("END_UPDATE_ACTIVE_SCHEDULE_CONNECTION_HASH")  + ctx.getConnectionData().hashCode() );
                                                                
                              if (p_group!=null)
                              {
                                boScheduleThread activity = new boScheduleThread( p_group, "XEO Schedule Thread for "+sched.getAttribute("description").getValueString()+"["+sched.getBoui()+"]", p_app );
                                activity.setSchedule( sched );                        
                                
                                //Implementar Timeout (Ebo_Schedule já tem atributo) de Threads lançadas pelo boScheduleAgent
                                //Será necessário guardar todas as threads lançadas numa qq estrutura em memória e fazer a sua gestão
                                activity.start();
                              }
                              else //Implementar com EJB Timer definir timer para executar de seguida e após execução é logo suspendido
                              {
                                  String tname = "XEO Schedule EjbTimer [" + sched.getAttribute("description").getValueString()  +"]";
                                  logger.finest(LoggerMessageLocalizer.getMessage("STARTING")+" " + tname );
                                  ic = new InitialContext();
                                  boScheduleThreadEJBLocalHome home = (boScheduleThreadEJBLocalHome)ic.lookup("java:comp/env/ejb/boScheduleThreadLocal");   
                                  boScheduleThreadLocalEJB schedejb = home.create();
                                  schedejb.addSchedule(sched);
                                  schedejb.start(sched.getAttribute("description").getValueString());
                                  ic.close();
                                  logger.finest(LoggerMessageLocalizer.getMessage("STARTED")+" "+tname);  
                              }
                          } 
                          catch (Exception e) 
                          {
                              e.printStackTrace();
                              if (ic!=null) ic.close();
                          }
                      }
                  }
                  finally
                  {
                      if( ctx != null )
                      {
                          ctx.close();
                          session.closeSession();
                      }
                  }                                     
          }
          else
          {
              logger.finest(LoggerMessageLocalizer.getMessage("STOPING_AGENT_IXEOUSER_OR_EBOSCHEDULE_NOT_DEPLOYED"));
          }
      }
      catch ( DataException e )
      {
          logger.finest(LoggerMessageLocalizer.getMessage("ERROR_READING_SCHEDULE_TABLE")+e.getMessage());
      }
      catch ( Exception e )
      {
          logger.finest(LoggerMessageLocalizer.getMessage("SCHEDULE_AGENT_FINISHED_WITH_ERRORS"));
          e.printStackTrace();
      }
      finally
      {
          try
          {
              if(session != null)
              {
                  session.closeSession();
              }
          }
          catch (Exception e)
          {
              
          }
      }
      
      logger.finest(LoggerMessageLocalizer.getMessage("SCHEDULE_AGENT_FINISHED"));
  }
}