package netgest.bo.message.server;
import java.io.File;
import netgest.bo.message.Address;
import netgest.bo.message.utils.Attach;
import java.util.Date;
import netgest.bo.message.Message;
import netgest.bo.runtime.AttributeHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boObject;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.io.FSiFile;
import netgest.xwf.core.xwfManager;
import netgest.xwf.core.xwfMessage;
import netgest.bo.runtime.robots.boSchedule;
import netgest.bo.system.Logger;

public class FileMessage implements MediaServer, boSchedule
{
    private EboContext ctx = null;
    private File fileDir = new File("C:\\projects\\accenture\\cids_coseg\\inbox\\");
    private static Logger logger = Logger.getLogger( FileMessage.class );
    
    public void read() throws boRuntimeException
    {
        //TODO:Read File(s)
        File[] files = fileDir.listFiles();
        for (int i = 0;files != null && i < files.length; i++)
        {
            // Log file....
            try
            {
                read( files[i] );
            }
            catch( Throwable e )
            {
                // Erro a processar ficheiro... continuar os restantes
                // Notificar administrador.
                e.printStackTrace();
            }
        }
    }
    
    public void read( File file ) throws boRuntimeException
    {
        boObject bobj = boObject.getBoManager().createObject( ctx, "message" );
        bobj.getAttribute( "name" ).setValueString( " Ficheiro " + file.getName() );
        bobj.getAttribute( "description" ).setValueString( " ficheiro " + file.getName() );
        bobj.getAttribute( "reference" ).setValueString( file.getName() );
        bobj.getAttribute( "dtdoc" ).setValueDate( new Date() );
        bobj.getAttribute( "from" ).setValueLong( ctx.getBoSession().getPerformerBoui() );
        bobj.getBridge( "to" ).add( boObject.getBoManager().loadObject( ctx, "select Ebo_Perf where username='ROBOT'" ).getBoui() );
        bobj.getAttribute( "messageid" ).setValueObject( file.getName() );
        
        boObject doc = boObject.getBoManager().createObject( ctx, "Ebo_Document" );
        doc.getAttribute( "file" ).setValueiFile( new FSiFile( null, file, null ) );
        bobj.getBridge( "documents" ).add( doc.getBoui() );
        
        boObject program = createMessageReceiveProgram(ctx, bobj );
        program.update();

    }

    private  static boObject createMessageReceiveProgram(EboContext ctx, boObject message) throws boRuntimeException
   {
        boObject program = boObject.getBoManager().createObject(ctx, "xwfProgramRuntime");        
        String label = message.getAttribute("name").getValueString();
        if(label != null && label.length() >= 200)
        {
            label = label.substring(0, 190) + "(...)"; 
        }
        program.getAttribute("label").setValueString(label, AttributeHandler.INPUT_FROM_INTERNAL);        
        if(message.getAttribute("SYS_DTCREATE").getValueDate() != null)
        {
            program.getAttribute("beginDate").setValueDate(message.getAttribute("SYS_DTCREATE").getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL);
        }
        else
        {
            program.getAttribute("beginDate").setValueDate(new Date(), AttributeHandler.INPUT_FROM_INTERNAL);
        }        
        boObject activity = null;
        xwfManager man = new xwfManager(ctx, program);
        xwfMessage.receiveMessage(man, message, program );
        
        return program;
   }


    public boolean mergeDocuments(boObject message) throws boRuntimeException
    {
        // Processa templates
        return false;
    }

    public boolean send(boObject message, boolean saveBinary) throws boRuntimeException
    {   
        // Envia mensagem
        return false;
    }

    public boolean send(Object context, boObject message, boolean saveBinary) throws boRuntimeException
    {
        // Envia mensagem
        return false;
    }

    public boObject sendReceipt(boObject message, boObject performer) throws boRuntimeException
    {   
        // Envia recibo de recepção
        return null;
    }

    public boolean deleteMessage(String messageid) throws boRuntimeException
    {
        // Elimina Mensagem do Servidor por id de mensagem
        return false;
    }

    public boolean deleteMessage(boObject message) throws boRuntimeException
    {
        // Elimina Mensagem
        return false;
    }

    public void setParameter(String parameter)
    {
    }

    public boolean doWork(EboContext ctx, boObject objectSchedule) throws boRuntimeException
    {
        try
        {
            boSession sess = boApplication.getApplicationFromStaticContext("XEO").boLogin( "ROBOT", boLoginBean.getSystemKey() );
            EboContext xctx = sess.createRequestContext( null, null, null );
            this.ctx = xctx;
            this.read();
            xctx.close();
            sess.closeSession();
            return true;
        }
        catch (boLoginException e)
        {
            e.printStackTrace();
        }
        return true;
    }
    
    public boolean releaseMsg(boObject message, boObject performer)
    {
        return true;
    }
        
}
