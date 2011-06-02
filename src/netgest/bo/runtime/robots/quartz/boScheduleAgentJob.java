package netgest.bo.runtime.robots.quartz;

import netgest.bo.runtime.robots.blogic.boScheduleAgentBussinessLogic;
import netgest.bo.system.boApplication;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class boScheduleAgentJob  implements Job {
	
    public boScheduleAgentJob() {
    }


    public void execute(JobExecutionContext context)
        throws JobExecutionException {

    	boScheduleAgentBussinessLogic logic = new boScheduleAgentBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"), null);
        logic.execute();
    }

}
