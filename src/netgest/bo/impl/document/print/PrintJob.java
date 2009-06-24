package netgest.bo.impl.document.print;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Vector;
import netgest.bo.boConfig;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.IOUtils;
import netgest.utils.StringUtils;

public class PrintJob 
{
    private long printJob;
    
    private String jobName;
    private String queueId;
    private String callBackClass;
    private String correlationId;
    
    private String queueFolder;
    
    private int    jobIndex;
    
    public PrintJob( String jobName, String queueId, String callBackClass, String correlationId )
    {   
        this.jobName = jobName;
        this.queueId = queueId;
        this.callBackClass = callBackClass;
        this.correlationId = correlationId;

    }
    
    public long getJobBoui()
    {
        return this.printJob;
    }
    
    
    public long addToQueue( EboContext context, long documentObject, String correlationID, String anexType )
    {
        return _addToQueue( context, documentObject, null , correlationID, anexType);
    }

    public long addToQueue( EboContext context, boObject documentObject, String correlationID, String anexType )
    {
        return _addToQueue( context, documentObject.getBoui(), null , correlationID, anexType );
    }

    public long addToQueue( EboContext context, File documentFile, String correlationID, String anexType )
    {
        return _addToQueue( context, 0, documentFile , correlationID, anexType );
    }

    public long addToQueue( EboContext context, iFile documentFile, String correlationID, String anexType )
    {
        return _addToQueue( context, 0, documentFile , correlationID, anexType );
    }

    public long addToQueue( EboContext context, String documentFile, String correlationID, String anexType )
    {
        return _addToQueue( context, 0,new File(documentFile), correlationID, anexType );
    }
    
    private void initialize( EboContext ctx )
    {
        try
        {
            long queueBoui = 0;
            boObjectList list = boObjectList.list( ctx, "select PrintQueue where id = ?",new Object[] { this.queueId } ,1 );
            list.beforeFirst();
            if( list.next() )
            {
                queueBoui = list.getCurrentBoui();
            }
            else
            {
                throw new RuntimeException( "A queue com o id [" + this.queueId + "] não existe." );
            }

            boObject printJobQueue = boObject.getBoManager().createObject( ctx ,"PrintQueueJob");
            printJobQueue.getAttribute("printQueue").setValueLong( queueBoui );
            printJobQueue.getAttribute("description").setValueString( this.jobName );
            printJobQueue.getAttribute("submiter").setValueLong( ctx.getBoSession().getPerformerBoui() );
            printJobQueue.getAttribute("lote").setValueString( null );
            printJobQueue.getAttribute("callBack").setValueString( this.callBackClass );
            printJobQueue.getAttribute("correlationID").setValueString( this.correlationId );
            printJobQueue.getStateAttribute("state").setValue("spooling");
            printJobQueue.update();
            
            this.printJob = printJobQueue.getBoui();
            
            queueFolder = getQueueFolder( queueId, printJobQueue.getBoui() );
            File queueFolderFile = new File( queueFolder );
            if( !queueFolderFile.exists() )
            {
                queueFolderFile.mkdirs();
            }
            
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
    }
    
    public static void dropJob(EboContext ctx, String correlationID)
    {
        
        try
        {
            if (correlationID != null && !"".equals(correlationID))
            {
                boObjectList jobList = boObjectList.list(ctx, " select PrintQueueJob where correlationID = ?", new Object[]{correlationID});
                jobList.beforeFirst();
                while (jobList.next())
                {
                    jobList.getObject().destroy();
                }
            }            
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);
        }    
    }
    
    public static boolean existsJob(EboContext ctx, String correlationID)
    {
        boolean ret = false;
        if (correlationID != null && !"".equals(correlationID))
        {
            boObjectList jobList = boObjectList.list(ctx, " select PrintQueueJob where correlationID = ?", new Object[] { correlationID } );
            jobList.beforeFirst();
            while (jobList.next())
            {
                ret = true;
            }
        }            
        return ret;
    }

    public static void dropJob(EboContext ctx, long jobBoui)
    {
        
        try
        {
            boObject job = boObject.getBoManager().loadObject(ctx, jobBoui);
            if (job.exists() )
            {
                job.destroy();
            } 
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);
        }
        
    }
    
    public void dropJob(EboContext ctx)
    {
        
        try
        {
            if (this.getJobBoui() != 0)
            {
                boObject job = boObject.getBoManager().loadObject(ctx, this.getJobBoui());
                if (job.exists() )
                {
                    job.destroy();
                }
            }
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);
        }
        
    }
    
    public static String getQueueFolder( String queueId, long objectBoui )
    {
        return getTempDir() + "XEO_QUEUE_" + queueId.toUpperCase() + File.separator + objectBoui + File.separator;        
    }
    
    public static String getTempDir()
    {
        String path = null;
        
        //TODO:Implement Interface LUSITANIA
        //boConfig.getFolderPath(boConfig.PRINTER_TEMP_FOLDER);
        
        if(path == null)
            path = System.getProperty("java.io.tmpdir");
        
        return path + File.separator;
    }
    
    private long _addToQueue(EboContext ctx, long document, Object file, String correlationID, 
                    String itemType  /* 0 - Normal, 1 - Anexo */ ) 
    {
        try
        {

            if( document != 0 )
            {
                boObject objdoc = boObject.getBoManager().loadObject( ctx, document );
                if( objdoc.getAttribute("file") == null )
                    throw new RuntimeException( "Erro, o objecto documento não tem o atributo FILE." );
            }
            else if( document == 0 )
            {
                if ( file == null )
                    throw new RuntimeException( "Não foi especificado documento ou ficheiro a ser impresso." );
                
                if( file instanceof File )
                {
                    if( !((File)file).exists() || !((File)file).isFile() )
                        throw new RuntimeException( "Ficheiro não existe ou não especifica um localização correcta." );
                }
            }
            
            if( this.printJob == 0 )
            {
                initialize( ctx );
            }
            
            boObject queueItem = boObject.getBoManager().createObject( ctx, "PrintQueueJobItem");
            queueItem.getAttribute("job").setValueLong( this.printJob );
            queueItem.getAttribute("spoolDate").setValueDate((new GregorianCalendar()).getTime());
            if( document != 0 )
            {
                queueItem.getAttribute("targetDocument").setValueLong(document);
            }
            else 
            {
                File queueFile = null;
                if( file instanceof File )
                {
                    queueFile = new File(queueFolder + (++jobIndex) + ((File)file).getName());
                    IOUtils.copy( (File)file, queueFile );
                }
                else
                {
                    try
                    {
                        queueFile = new File(queueFolder + (++jobIndex) + ((iFile)file).getName());
                        byte[] buffer = new byte[8192];
                        int br = 0;
                        InputStream is = ((iFile)file).getInputStream();
                        FileOutputStream fout = new FileOutputStream( queueFile );
                        while( (br=is.read( buffer )) > 0 )
                        {
                            fout.write( buffer, 0, br );
                        }
                        fout.close();
                        is.close(); 
                    }
                    catch (Exception e)
                    {
                        throw new RuntimeException(e);
                    }
                }
                queueItem.getAttribute("targetFile").setValueString( queueFile.getAbsolutePath() );
            }
            queueItem.getAttribute("correlationID").setValueString(correlationID);
            queueItem.getAttribute("itemType").setValueString(itemType);
            queueItem.update();
            
            return queueItem.getBoui();
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);            
        }
    }
    
    public void submitJob( EboContext ctx )
    {
        changeJobState( ctx, "ready" );
    }

    public void pauseJob( EboContext ctx )
    {
        changeJobState( ctx, "paused" );
    }

    public void cancelJob( EboContext ctx )
    {
        changeJobState( ctx, "canceled" );
    }

    protected void changeJobState( EboContext ctx, String state ) 
    {
        try
        {
            boObject obj = boObject.getBoManager().loadObject( ctx, this.printJob );
            obj.getStateAttribute("state").setValue( state );
            
            // Bug: Se forem chamados multiplos updates na mesma transacção não são gravados os dados
            // Este código serve de workaround.
            if ( obj.get_IsInOnSave() == boObject.UPDATESTATUS_WAITING_ENDTRANSACTION )
            {
                obj.set_IsInOnSave( boObject.UPDATESTATUS_IDLE );
            }
            obj.update();
            
        }
        catch (boRuntimeException e)
        {
            throw new RuntimeException(e);
        }
    }
    
}