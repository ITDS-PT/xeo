package netgest.bo.impl.document.print;

import java.io.File;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.robots.boSchedule;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;

public class clearPrintQueuesTemporaryFiles implements boSchedule
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.print.clearPrintQueuesTemporaryFiles");

    
        
    public clearPrintQueuesTemporaryFiles()
    {

    }
    
    public void setParameter(String parameter )
    {
    
    }
  
  public boolean doWork(EboContext ctx, boObject objectSchedule ) throws boRuntimeException
  {
    String tmpPath = System.getProperty("java.io.tmpdir");
    
    boObjectList list = boObjectList.list(ctx,"select PrintQueue where 1 = 1",1,9999);
    list.beforeFirst();
    while (list.next())
    {
        String queueID = list.getObject().getAttribute("id").getValueString();
        File queuePath = new File(tmpPath+File.separator+"XEO_QUEUE_"+queueID);
        if (queuePath.exists() && queuePath.isDirectory())
        {
            deleteRecursive(queuePath, false);
        }
    }
    
    return true;
  }
  
  private void deleteRecursive(File path, boolean deleteFirstPath)
  {
      if (path.isFile())
        path.delete();
      else if (path.isDirectory())
      {
          File[] files = path.listFiles();
          for (int i = 0; i < files.length; i++)
          {
               deleteRecursive(files[i], true);
          }
          if (deleteFirstPath)
            path.delete();
      }
  }
    
}