package netgest.bo.runtime.robots.blogic;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.Types;

import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.Vector;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;


import netgest.bo.system.Logger;
import netgest.bo.runtime.robots.boSchedule;

public class boScheduleThreadBussinessLogic 
{
  //logger
  private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.boScheduleThreadBussinessLogic");

  /**
   * 
   * @since 
   */
   private boObject p_schedule;
   private long     p_schedule_boui;
   private boApplication p_app;
   private String p_scheduleDescription=null;
   
  public boScheduleThreadBussinessLogic(boApplication boapp, String scheduleDescription)
  {
   p_app=boapp;
   p_scheduleDescription=scheduleDescription;
  }
  
  public void setSchedule( boObject schedule )
  {
      p_schedule = schedule;   
      p_schedule_boui = schedule.getBoui();
  }
  
  public void execute()
  {
      EboContext ctx = null;
      boSession session =null;
      long iniTime = System.currentTimeMillis();
      boolean ok = false;
      boolean deactivate = false;
      //Para os agendamentos não repetitivos o calculo da próxima execução deverá ser baseado na
      //data/hora inicio do agendamento antes da sua execução que poderá ser demorada e alterar
      //o comportamento do algoritmo de calculo da próxima execução
      Date startTime=null;
      String error = null;
      try
      {
          logger.finest(LoggerMessageLocalizer.getMessage("STARTING_AGENT_FOR_SCHEDULE")+" "+p_scheduleDescription );
          
          //Executa o agendamento com o utilizador definido no mesmo
          
          boObject performer = p_schedule.getAttribute( "performer" ).getObject();
          if (performer!=null)
            session = p_app.boLogin( performer.getAttribute( "username" ).getValueString(), boLoginBean.getSystemKey(),p_app.getDefaultRepositoryName() );
          else
            session = p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(),p_app.getDefaultRepositoryName() );
          
          ctx = session.createRequestContext( null,null,null );
          
          startTime = ScheduleCalcNextRun.getActualSynchonizedDate( ctx );
          
          logger.finest( LoggerMessageLocalizer.getMessage("STARTING_SCHEDULE_CONNECTION_HASH")  + ctx.getConnectionData().hashCode() );
          
          try
          {
              String classname =  p_schedule.getAttribute( "javaclass" ).getValueString();
              boSchedule itemsched = (boSchedule)Class.forName( classname ).newInstance();
              
              itemsched.setParameter( p_schedule.getAttribute( "parameters" ).getValueString() );
              ok = itemsched.doWork( ctx, p_schedule );
              
              logger.finest( p_scheduleDescription + " "+LoggerMessageLocalizer.getMessage("FINISHED_WITHOUT_ERRORS"));
              
          }
          finally
          {
              if( ctx != null )
              {
                  ctx.close();                    
              }
              if(session != null)
              {
                  session.closeSession();
              }
          }
          // Release the objects
      }
      catch (IllegalAccessException e)
      {
          deactivate = true;
          error = logError( e.getClass().getName(), e );
          logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ",e);
      }
      catch (InstantiationException e)
      {
          deactivate = true;
          error = logError( e.getClass().getName(), e ); 
          logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
      }
      catch (ClassNotFoundException e)
      {
          deactivate = true;
          error = logError( e.getClass().getName(), e ); 
          logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
      }
      catch (boLoginException e)
      {
          deactivate = true;
          error = logError( e.getClass().getName(), e ); 
          logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
      }
      catch (Exception e)
      {
          error = logError( e.getClass().getName(), e ); 
          logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e);
      }
      
      boolean activeStatusWasSet = false;
      
      try
      {
          session = p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(),p_app.getDefaultRepositoryName() );            
          ctx = session.createRequestContext( null,null,null );

          logger.finest( LoggerMessageLocalizer.getMessage("UPDATE_NEXTRUNTIME_CONNECTION_HASH") + ctx.getConnectionData().hashCode() );

          
          boObject reloadedsched = boObject.getBoManager().loadObject( ctx, p_schedule_boui );
          // Calc the next runtime            
          ScheduleCalcNextRun.CalcNextRun( ctx, reloadedsched,startTime );

          // Set the schedule attributes            
          reloadedsched.getAttribute( "activeStatus").setValueString( "0" );
          reloadedsched.getAttribute("executiontime").setValueLong( System.currentTimeMillis() - iniTime );
          reloadedsched.getAttribute("lastresultcode").setValueString( ok?"OK":"NOK" );
          
          if( error != null )
          {
              reloadedsched.getAttribute("errormessage").setValueString( error );
          }
//          if( deactivate )
//          {
//              reloadedsched.getAttribute( "state" ).setValueString( "0" );
//          }
          reloadedsched.update();
          
          activeStatusWasSet = true;
          
          
      }
      catch (boRuntimeException e)
      {
          logger.warn(LoggerMessageLocalizer.getMessage("ERROR_SETTING_OBJECT_TO_STATE_READY"), e);
      }
      catch (Exception e)
      {
          logger.warn(LoggerMessageLocalizer.getMessage("ERROR_SETTING_OBJECT_TO_STATE_READY"), e);
      }
      finally
      {
          if( ctx != null )
          {
              if( !activeStatusWasSet )
              {
                  // Workaround if object fails to update via  reloadedsched.update();
                  // In some case happened.
                  logger.warn(LoggerMessageLocalizer.getMessage("FAIL_TO_UPDATE_BOSCHEDULE")+"["+p_schedule_boui+"]");
                  ctx.close();
                  try 
                  {
                      PreparedStatement pstm = ctx.getConnectionData().prepareStatement("update ebo_schedule set activestatus = 0 where boui=?");
                      pstm.setLong( 1, p_schedule_boui );
                      pstm.executeUpdate();
                      pstm.close();
                  }
                  catch (Exception e)
                  {
                      logger.severe(LoggerMessageLocalizer.getMessage("FAIL_TO_UPDATE_VIA_SQL_BOSCHEDULE")+"["+p_schedule_boui+"]",e);
                  }
              }
              ctx.close();                    
          }
          if(session != null)
          {
              session.closeSession();
          }
      }    
  }
  
  private static final String logError( String message , Throwable error )
  {
      CharArrayWriter cw = new CharArrayWriter();
      PrintWriter pw = new PrintWriter( cw );
      
      pw.write( message );
      pw.write( '\n' );
      error.printStackTrace( pw );
      
      pw.close();
      cw.close();
      return cw.toString();
  }

 public static class ScheduleCalcNextRun  {
    
        public static final String TYPE_REPEATLY="REPEATLY";
        public static final String TYPE_DAILY="DAILY";
        public static final String TYPE_WEEKLY="WEEKLY";
        public static final String TYPE_MONTHLY="MONTHLY";
    
    
        private static final String WEEKDAYS = "WEEKDAYS";
        private static final String ALLDAYS  = "ALLDAYS";
        private static final String WEEKEND  = "WEEKEND";
        
        private static final String LASTDAYOFMONTH = "LAST";
        private static final String SCHEDULETYPE      = "REPEATLY,DAILY,WEEKLY,MONTHLY";
    
        private static Hashtable SCHEDULETEMPLATES;
        
        

        public static final void CalcNextRun(EboContext ctx,boObject sched,Date startDate) throws netgest.bo.runtime.boRuntimeException {
            Date nextrun = GetNextRun(ctx,sched,getScheduleProps(sched),startDate);
            SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss");
            logger.finest(LoggerMessageLocalizer.getMessage("NEXT_RUN_DATE")+ ":" + df.format(nextrun) );
            sched.getAttribute("nextruntime").setValueDate( nextrun );
        }
        private static Date GetNextRun(EboContext ctx,boObject sched,boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps props,Date startDate) throws boRuntimeException {

            Date lastrun   = sched.getAttribute("lastruntime").getValueDate();
            String start   = sched.getAttribute("startdate").getValueString();
            String end     = sched.getAttribute("enddate").getValueString();
    
            Calendar nextrun=Calendar.getInstance();
            Date currentdate=getActualSynchonizedDate(ctx);
            

                
            if(props.type.equalsIgnoreCase(TYPE_REPEATLY)) {
              nextrun=getNextRunForRepeat(props);
            } else {
              nextrun=getNextRunForCalendarBased(props,startDate);                 
            }
            sched.getAttribute("lastruntime").setValueDate(currentdate);
            return nextrun.getTime();
        }

        public static Date getActualSynchonizedDate(EboContext ctx)
        {
            // Code for Application server clock not need to be synchronized with the Database
            Date currentdate=null;
            try 
            {
                currentdate=new Date();
                CallableStatement cstm = ctx.getConnectionData().prepareCall("begin ? := sysdate; end;");
                cstm.registerOutParameter(1,Types.TIMESTAMP);
                cstm.execute();
                currentdate.setTime(cstm.getTimestamp(1).getTime());
                cstm.close();
            } 
            catch (java.sql.SQLException e) 
            {
                e=e;
            }
            return currentdate;
        }
        private static Calendar getNextRunForRepeat(boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps props)
        {
          Calendar nextrun=Calendar.getInstance();
          Date currentdate=new Date();
          String interval = props.every;
          int vtype = Calendar.MINUTE;
          int pdif=0;
          if(interval.indexOf("s")>-1) { // Value in seconds;
             vtype = Calendar.SECOND;
             pdif = Integer.parseInt(interval.substring(0,interval.toLowerCase().indexOf("s")).trim());
          } else if(interval.indexOf("m")>-1){ // Value in minutes
             vtype = Calendar.MINUTE;
             pdif = Integer.parseInt(interval.substring(0,interval.toLowerCase().indexOf("m")).trim());
          } else if(interval.indexOf("h")>-1) { // Value in hours
             vtype = Calendar.HOUR_OF_DAY;
             pdif = Integer.parseInt(interval.substring(0,interval.toLowerCase().indexOf("h")).trim());
          } else if(interval.indexOf("d")>-1) { // Value in days
             vtype = Calendar.DAY_OF_MONTH;
             pdif = Integer.parseInt(interval.substring(0,interval.toLowerCase().indexOf("d")).trim());
          } 
          else
          {
             pdif = Integer.parseInt( props.interval ); 
          }                  
         nextrun.setTime(new Date(currentdate.getTime()));
         nextrun.add(vtype,pdif);          
         return nextrun;
        }

        private static Calendar getNextRunForCalendarBased(boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps props,Date startDate)
        {
           Calendar nextrun=Calendar.getInstance();

           nextrun.setTime(startDate );
           //Coloca a hora a que o agendamento está definido correr.
           nextrun.set(Calendar.HOUR_OF_DAY,props.getWhenHours());
           nextrun.set(Calendar.MINUTE,props.getWhenMinutes());
           nextrun.set(Calendar.SECOND,0);
           
           //Calcula o próximo dia do Calendário para cada um dos tipos de agendamento
           if(props.type.equals(TYPE_DAILY)) {            
               if (props.every.equals(ScheduleCalcNextRun.WEEKEND))
               {                 
                 int dayOfWeek = nextrun.get(Calendar.DAY_OF_WEEK);          
                 if (dayOfWeek != Calendar.SATURDAY) {
                     int delta = Calendar.SATURDAY - dayOfWeek;
                     nextrun.add(Calendar.DATE, delta);
                 }
                  //Domingo
                 else nextrun.add(Calendar.DATE,Calendar.SUNDAY);   
               }
               else if (props.every.equals(ScheduleCalcNextRun.WEEKDAYS))
               {
                  int dayOfWeek = nextrun.get(Calendar.DAY_OF_WEEK);
                  
                  boolean Saturday=(dayOfWeek == Calendar.SATURDAY);
                  boolean Sunday=(dayOfWeek==Calendar.SUNDAY);
                  nextrun.add(Calendar.DAY_OF_MONTH,1);
                  while (Saturday || Sunday) {
                      nextrun.add(Calendar.DAY_OF_MONTH,1);
                      dayOfWeek=nextrun.get(Calendar.DAY_OF_WEEK);
                      Saturday=(dayOfWeek == Calendar.SATURDAY);
                      Sunday=(dayOfWeek==Calendar.SUNDAY);
                  }                     
               }
               else //Default ALLDAYS
                  nextrun.add(Calendar.DAY_OF_MONTH,1); 
               
           } else if (props.type.equals(TYPE_WEEKLY)) {
               int dayOfWeek = nextrun.get(Calendar.DAY_OF_WEEK);    
               int day =dayToInt(props.everyWeek);
               if (dayOfWeek != day) {
                   int delta = (day - dayOfWeek)+7;
                   nextrun.add(Calendar.DATE, delta);
               } 
               else nextrun.add(Calendar.DAY_OF_WEEK,7);
                
           } else if (props.type.equals(TYPE_MONTHLY)) {
               int day=1;
               if (props.everyMonth.equals(LASTDAYOFMONTH))
               {
                 Calendar aux=Calendar.getInstance();
                 aux.setTime(startDate);
                 aux.set(Calendar.DAY_OF_MONTH,1);
                 aux.add(Calendar.MONTH,1);
                 day=aux.getActualMaximum(Calendar.DAY_OF_MONTH); ;
                 nextrun.set(Calendar.MONTH,aux.get(Calendar.MONTH));
                 nextrun.set(Calendar.DAY_OF_MONTH,day);                 
               }
               else
               {
                 day=new Integer(props.everyMonth).intValue();
                 nextrun.set(Calendar.DAY_OF_MONTH,day);
                 nextrun.add(Calendar.MONTH,1);
               }
                              
           }             
           return nextrun;
        }
        
        private static boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps getScheduleProps(boObject sched) throws boRuntimeException {
            String schedtype = sched.getAttribute("type").getValueString();
            
            boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps schedprops;
            
            schedprops = new boScheduleThreadBussinessLogic.ScheduleCalcNextRun.ScheduleProps();
            schedprops.type=schedtype;
            
            if(sched.getAttribute("every").getValueString()!=null)
            {
                schedprops.every = sched.getAttribute("every").getValueString();
            }
            if(sched.getAttribute("everyweek").getValueString()!=null)
            {
                schedprops.everyWeek = sched.getAttribute("everyweek").getValueString();
            }
            if(sched.getAttribute("everymonth").getValueString()!=null)
            {
                schedprops.everyMonth = sched.getAttribute("everymonth").getValueString();
            }            
            if(sched.getAttribute("interval").getValueString()!=null)
            {
            	if (sched.getAttribute("interval").getValueString().equals(""))
            		schedprops.interval ="";	
            	else
	                schedprops.interval = String.valueOf(sched.getAttribute("interval").getValueLong());
            }
            if(schedprops.every == null || schedprops.every.length()==0 || schedprops.every.equalsIgnoreCase("ALLDAYS")) {
                schedprops.every = ALLDAYS;
            } else if(schedprops.every.equalsIgnoreCase("WEEKDAYS")) {
                schedprops.every = WEEKDAYS;
            } else if(schedprops.every.equalsIgnoreCase("WEEKEND")) {
                schedprops.every = WEEKEND;
            } else {
                // Throws exception
            }
            
            if(sched.getAttribute("when").getValueString()!=null && sched.getAttribute("when").getValueString().length()>0)
            {
                schedprops.when = sched.getAttribute("when").getValueString();
            }
            return schedprops;
        }
        private static class ScheduleProps {
            public String type;
            public String every;
            public String everyWeek;
            public String everyMonth;
            public String interval;
            public String when;
            public ScheduleProps() {
            }
            
            public int getWhenMinutes()
            {
              int minutes=0;
              if (this.when!=null && this.when.indexOf(":")>-1)
              {
                minutes=new Integer(this.when.split(":")[1]).intValue();
              }
              return minutes;              
            }
            
            public int getWhenHours()
            {
              int hour=0;
              if (this.when!=null && this.when.indexOf(":")>-1)
              {
                hour=new Integer(this.when.split(":")[0]).intValue();
              }
              return hour;              
            }
        }
        private static final int dayToInt(String day) {
            //MO,TH,WE,TR,FR,SA,SU
            if(day.equalsIgnoreCase("MO"))  return Calendar.MONDAY;
            if(day.equalsIgnoreCase("TU"))  return Calendar.TUESDAY;
            if(day.equalsIgnoreCase("WE"))  return Calendar.WEDNESDAY;
            if(day.equalsIgnoreCase("TH"))  return Calendar.THURSDAY;
            if(day.equalsIgnoreCase("FR"))  return Calendar.FRIDAY;
            if(day.equalsIgnoreCase("SA"))  return Calendar.SATURDAY;
            if(day.equalsIgnoreCase("SU"))  return Calendar.SUNDAY;
            // throw exception
            return 0;
        }
    }      
}