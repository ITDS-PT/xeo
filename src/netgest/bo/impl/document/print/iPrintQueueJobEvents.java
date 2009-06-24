package netgest.bo.impl.document.print;
import java.io.Serializable;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

public interface iPrintQueueJobEvents extends Serializable
{
       
    public void onBeforeJobPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    public void onAfterJobPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    public void onBeforeItemPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    public void onAfterItemPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    public void onErrorItem(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    public void onErrorJob(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    
    
        
}
