package netgest.bo.impl.document.print;
import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import org.apache.log4j.Logger;

public class PrintQueueJobEvents 
{
    public PrintQueueJobEvents()
    {
    }
    
    public static final void onAfterLoad( boObject object ) throws boRuntimeException
    {
        try
        {
            PreparedStatement pstm = object.getEboContext().getConnectionData().prepareStatement( " select count(*) from PrintQueueJobItem where job$ = ? and printDate is not null " );
            pstm.setLong( 1, object.getBoui() );
            ResultSet rslt = pstm.executeQuery();
            
            long waiting = 0;
            long printed = 0;
            
            if( rslt.next() )
            {
                printed = rslt.getLong(1);
            }
            
            object.getAttribute("progress").setValueString( printed + " de " + object.getAttribute("pages").getValueLong() );
            
        }
        catch (SQLException e)
        {
            throw new RuntimeException(e);
        }
        
    }
    
    public static boolean onBeforeDestroy( boObject queueJob ) throws boRuntimeException
    {
        Logger logger      = Logger.getLogger("netgest.bo.impl.document.print.PrintQueueJobEvents");
        String queueFolder = null;
        
        // DEBUG
        if(queueJob != null)
        {
            logger.debug("Queue Job BOUI: " + String.valueOf(queueJob.getBoui()));
            
            if(queueJob.getAttribute("printQueue").getObject() != null)
            {
                logger.debug("Attribute printQueue -> BOUI: " + String.valueOf(queueJob.getAttribute("printQueue").getObject().getBoui()));
                logger.debug("Attribute printQueue -> Attribute id: " + queueJob.getAttribute("printQueue").getObject().getAttribute( "id" ).getValueString());
                
                queueFolder = PrintJob.getQueueFolder(queueJob.getAttribute("printQueue").getObject().getAttribute("id").getValueString(), queueJob.getBoui());
            }
            else
                logger.debug("Attribute printQueue is NULL.");
        }
        else
            logger.debug("Queue Job is NULL.");
        // DEBUG
        
        boObjectList queueItems = boObjectList.list( queueJob.getEboContext(), "select PrintQueueJobItem where job = ? ", new Object[] { new Long( queueJob.getBoui() ) },1,99999, false);

        while( queueItems.next() )
        {
            queueItems.getObject().destroy();
        }
        
        if(queueFolder != null)
        {
            try
            {
                File queueFolderFile = new File( queueFolder );
                queueFolderFile.delete();
            }
            catch(Exception ex)
            {
                if(queueJob != null)
                {
                    logger.error("EXCEPTION OCCURED ON onBeforeDestroy : ");
                    logger.error("Queue Job BOUI: " + String.valueOf(queueJob.getBoui()));
                    
                    if(queueJob.getAttribute("printQueue").getObject() != null)
                    {
                        logger.error("Attribute printQueue -> BOUI: " + String.valueOf(queueJob.getAttribute("printQueue").getObject().getBoui()));
                        logger.error("Attribute printQueue -> Attribute id: " + queueJob.getAttribute("printQueue").getObject().getAttribute( "id" ).getValueString());
                    }
                    else
                        logger.error("Attribute printQueue is NULL.");
                }
                else
                    logger.error("Queue Job is NULL.");
                
                logger.error(ex, ex);
            }
        }
        
        return true;
    }
}