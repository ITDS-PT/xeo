package netgest.bo.runtime.robots.quartz;

import netgest.bo.runtime.robots.blogic.GarbageRemoverAgentBussinessLogic;
import netgest.bo.system.boApplication;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class GarbageRemoverAgentJob implements Job {
	
    public GarbageRemoverAgentJob() {
    }


    public void execute(JobExecutionContext context)
        throws JobExecutionException {

    	 GarbageRemoverAgentBussinessLogic logic = new GarbageRemoverAgentBussinessLogic(boApplication.getApplicationFromStaticContext("XEO"));
         logic.execute();
    }

}
