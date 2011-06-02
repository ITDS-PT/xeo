package netgest.bo.runtime.robots.quartz;

import netgest.bo.runtime.robots.blogic.boQueueAgentBussinessLogic;
import netgest.bo.system.boApplication;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class boQueueAgentJob implements Job {
	
	    public boQueueAgentJob() {
	    }


	    public void execute(JobExecutionContext context)
	        throws JobExecutionException {

	    	boQueueAgentBussinessLogic logic = new boQueueAgentBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"));
            logic.execute();
	    }

}
