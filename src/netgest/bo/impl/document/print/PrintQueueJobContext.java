package netgest.bo.impl.document.print;
import java.io.Serializable;
import java.util.Random;
import java.util.Vector;

public class PrintQueueJobContext implements Serializable
{

    private long jobID;
    private Vector itemsToPrint;
    private String callback;
    private PrintQueueContext parent;
    private long id;
    private ItemPrinted currentItem;
         
    
    public PrintQueueJobContext(long jobID, String callback, PrintQueueContext parent, long[] itemsToPrint)
    {
        this.jobID = jobID;
        this.itemsToPrint = new Vector();
        this.callback = callback;
        this.parent = parent;
        Random r = new Random();
        r.setSeed(System.currentTimeMillis());
        id = r.nextLong();
        for (int i = 0; i < itemsToPrint.length; i++)
            this.itemsToPrint.add(new Long(itemsToPrint[i]));
    }
    
     public double getID()
    {
        return this.id;
    }
    
    public long getJobID()
    {
        return this.jobID;
    }
    
    
    public PrintQueueContext getParent()
    {
        return this.parent;
    }
    
    public String getCallBack()
    {
        return callback;
    }
    
    public String getCurrentCorrelationID()
    {        
        if (currentItem != null)
            return currentItem.getCorrelationID();
        else
            return null;
    }
    
    
    public void setCurrentItem(long item, String correlationID)
    {
        this.currentItem = new ItemPrinted(item, correlationID);
    }
    
    
    public long removeItem(long item)
    {
        for (int i = 0; i < itemsToPrint.size(); i++)
            if (item == ((Long)itemsToPrint.get(i)).longValue())
            {
                   return ((Long)itemsToPrint.remove(i)).longValue(); 
            }
        return -1;   
    }
    
            
    public Vector getItemsToPrint()
    {
        return this.itemsToPrint;                
    }
    
    public void setItemsToPrint(long[] v)
    {        
        this.itemsToPrint.clear();
        for (int i = 0; i < v.length; i++)
            this.itemsToPrint.add(new Long(v[i]));                   
                
    }
        
    private class ItemPrinted implements Serializable
    {
        private long itemID;
        private String correlationID;
        public ItemPrinted(long itemID, String correlationID)
        {
            this.itemID = itemID;
            this.correlationID = correlationID;
        }
        public long getItemID()
        {
            return this.itemID;
        }
        public String getCorrelationID()
        {
            return this.correlationID;
        }
    }        
    
}
