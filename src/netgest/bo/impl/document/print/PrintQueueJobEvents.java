package netgest.bo.impl.document.print;
import java.io.File;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;

import netgest.bo.system.Logger;

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
            logger.finest("Queue Job BOUI: " + String.valueOf(queueJob.getBoui()));
            
            if(queueJob.getAttribute("printQueue").getObject() != null)
            {
                logger.finest("Attribute printQueue -> BOUI: " + String.valueOf(queueJob.getAttribute("printQueue").getObject().getBoui()));
                logger.finest("Attribute printQueue -> Attribute id: " + queueJob.getAttribute("printQueue").getObject().getAttribute( "id" ).getValueString());
                
                queueFolder = PrintJob.getQueueFolder( queueJob.getEboContext(), queueJob.getAttribute("printQueue").getObject().getAttribute("id").getValueString(), queueJob.getBoui());
            }
            else
                logger.finest("Attribute printQueue is NULL.");
        }
        else
            logger.finest("Queue Job is NULL.");
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
                    logger.severe("EXCEPTION OCCURED ON onBeforeDestroy : ");
                    logger.severe("Queue Job BOUI: " + String.valueOf(queueJob.getBoui()));
                    
                    if(queueJob.getAttribute("printQueue").getObject() != null)
                    {
                        logger.severe("Attribute printQueue -> BOUI: " + String.valueOf(queueJob.getAttribute("printQueue").getObject().getBoui()));
                        logger.severe("Attribute printQueue -> Attribute id: " + queueJob.getAttribute("printQueue").getObject().getAttribute( "id" ).getValueString());
                    }
                    else
                        logger.severe("Attribute printQueue is NULL.");
                }
                else
                    logger.severe("Queue Job is NULL.");
                
                logger.severe( ex );
            }
        }
        
        return true;
    }
}