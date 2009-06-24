package netgest.bo.impl.document.print;
import java.io.Serializable;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;

public interface iPrintQueueEvents extends Serializable
{
       
    public void onBeginPrint(EboContext boctx, PrintQueueContext ctx) throws boRuntimeException;
    public void onEndPrint(EboContext boctx, PrintQueueContext ctx) throws boRuntimeException;
    public void onCancelPrint(EboContext boctx, PrintQueueContext ctx) throws boRuntimeException;
    public void onRePrint(EboContext boctx, PrintQueueContext ctx) throws boRuntimeException;

    public void onBeforeJobPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    public void onAfterJobPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    public void onBeforeItemPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    public void onAfterItemPrint(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    public void onErrorJob(EboContext boctx, PrintQueueContext ctx) throws boRuntimeException;
    public void onErrorItem(EboContext boctx, PrintQueueJobContext ctx) throws boRuntimeException;
    
    
    
    
      
    
        
}
