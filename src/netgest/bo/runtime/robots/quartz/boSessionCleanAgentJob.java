package netgest.bo.runtime.robots.quartz;

import netgest.bo.runtime.robots.blogic.boSessionCleanAgentBussinessLogic;
import netgest.bo.system.boApplication;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class boSessionCleanAgentJob  implements Job {
	
    public boSessionCleanAgentJob() {
    }


    public void execute(JobExecutionContext context)
        throws JobExecutionException {

    	boSessionCleanAgentBussinessLogic logic = new boSessionCleanAgentBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"));
        logic.setInterval(15000);
        logic.execute();
    }

}
