package netgest.bo.runtime.robots.quartz;

import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;
import netgest.bo.system.boApplication;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class boTextIndexAgentJob implements Job {
	
    public boTextIndexAgentJob() {
    }


    public void execute(JobExecutionContext context)
        throws JobExecutionException {

    	boTextIndexAgentBussinessLogic logic = new boTextIndexAgentBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"));
        logic.execute();
    }

}
