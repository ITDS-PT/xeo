package netgest.bo.impl.document.print;
import java.io.File;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

public class PrintQueueContext implements Serializable
{

    private String queueID;
    private JobPrinted currentJob;
    private String callback;
    private long id;
    private Vector jobsToPrint;
    private Vector jobsImediateToPrint;
    private String lote;
    private String printer;
    private boolean runEvents; 
             
    
    public PrintQueueContext(String queueID, String callback, String lote, String printer, boolean runEvents, long[] jobsToPrintl)
    {
        this.queueID = queueID;
        this.lote = lote;
        this.jobsToPrint = new Vector();
        this.jobsImediateToPrint = new Vector();
        this.callback = callback;
        this.printer = printer;
        this.runEvents = runEvents;
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        id = r.nextLong();
        for (int i = 0; i < jobsToPrintl.length; i++)
            this.jobsToPrint.add(new Long(jobsToPrintl[i]));
    }
    
    public double getID()
    {
        return this.id;
    }
    
    public String getQueueID()
    {
        return this.queueID;
    }
    
    
    public long getCurrentJob()
    {
        if (currentJob != null)
            return currentJob.getJobID();
        else
            return 0;
    }
    
    public String getCallBack()
    {
        return callback;
    }
    
    public String getLote()
    {
        return lote;
    }
    
    public String getPrinter()
    {
        return printer;
    }
    
    public boolean getRunEvents()
    {
        return runEvents;
    }
    
    public String getCurrentCorrelationID()
    {
        if (currentJob != null)
            return currentJob.getCorrelationID();
        else
            return null;
    }
    
   
    public void setCurrentJob(long job, String correlationID)
    {
       currentJob = new JobPrinted(job, correlationID);
    }
    
    public long removeJobsImediateToPrint(long job)
    {
        for (int i = 0; i < jobsImediateToPrint.size(); i++)
            if (job == ((Long)jobsImediateToPrint.get(i)).longValue() )
            {
                   return ((Long)jobsImediateToPrint.remove(i)).longValue(); 
            }
        return -1;    
    }
    
    
    public Vector getJobsToPrint()
    {
        return this.jobsToPrint;                
    }
    
    public Vector getJobsImediateToPrint()
    {
        return this.jobsImediateToPrint;                
    }    
    
    public void setJobsToPrint(long[] v)
    {
        this.jobsToPrint.clear();
        for (int i = 0; i < v.length; i++)
            this.jobsToPrint.add(new Long(v[i]));                   
    }
    
    public void imediatePrint (long job)
    {
      jobsImediateToPrint.add(new Long(job));
    }
    
    public boolean existsJob(long jobboui)
    {
        for (int i = 0; i < jobsToPrint.size(); i++)
            if (jobboui == ((Long)jobsToPrint.get(i)).longValue())
            {
                return true;
            }
        return false;
    }
    
    private class JobPrinted implements Serializable
    {
        private long jobID;
        private String correlationID;
        public JobPrinted(long jobID, String correlationID)
        {
            this.jobID = jobID;
            this.correlationID = correlationID;
        }
        public long getJobID()
        {
            return this.jobID;
        }
        public String getCorrelationID()
        {
            return this.correlationID;
        }
    }
    
}
