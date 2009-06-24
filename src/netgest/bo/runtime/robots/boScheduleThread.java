/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;

import netgest.bo.runtime.boObject;
import netgest.bo.system.boApplication;


import netgest.bo.runtime.robots.boSchedule;
import netgest.bo.runtime.robots.blogic.boScheduleThreadBussinessLogic;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boScheduleThread extends Thread 
{

    /**
     * 
     * @since 
     */
     private boObject p_schedule;
     private long     p_schedule_boui;
     private boApplication p_app;
     private boScheduleThreadBussinessLogic logic=null;
     
    public boScheduleThread( ThreadGroup group, String scheduleDesciption, boApplication app )
    { 
        super( group, scheduleDesciption ) ;
        p_app = app;
        logic = new boScheduleThreadBussinessLogic(app, scheduleDesciption);
    }
    
    public void setSchedule( boObject schedule )
    {
        p_schedule = schedule;   
        p_schedule_boui = schedule.getBoui();
        if (logic!=null) logic.setSchedule(p_schedule);
    } 
    
    public void run()
    {
      if (logic!=null)
        logic.execute();
    }
    
      
}