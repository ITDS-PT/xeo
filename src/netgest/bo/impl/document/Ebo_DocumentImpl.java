/*Enconding=UTF-8*/
package netgest.bo.impl.document;

import java.io.File;
import java.math.BigDecimal;

import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.utils.CleanDirectory;
import netgest.io.BasiciFile;
import netgest.io.DBiFile;
import netgest.io.FSiFile;
import netgest.io.iFile;

import org.apache.log4j.Logger;

/**
 * Ebo_DocumentImpl é a classe estendida pelo Ebo_Document,
 * para preencher alguns atributos.
 * 
 * @author João Paulo Trindade Carreira
 * @author Pedro Castro Campos 
 */
public abstract class Ebo_DocumentImpl extends boObject
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.impl.document.Ebo_DocumentImpl");           
    
    public void init() throws boRuntimeException
    {
        super.init();        
        if(this.getAttribute("owner").getValueObject()==null && super.getEboContext().getBoSession().getPerformerBoui()>0)  
        {            
            boolean wasChanged = isChanged();
            this.getAttribute("owner").setValueLong(super.getEboContext().getBoSession().getPerformerBoui());
            setChanged( wasChanged );
        }
    }
    public static String getTempDir() 
    {
        
        String stmpdir=System.getProperty("java.io.tmpdir","./tmp/");
        
        //,"./tmp/")+"ngtbo"+java.io.File.separator)
        char lastchar=stmpdir.charAt( stmpdir.length()-1  );
        if ( lastchar!='/' && lastchar!='\\' )
        {
            stmpdir+='/';
        }
            stmpdir=stmpdir+"ngtbo"+java.io.File.separator;
        
        java.io.File tmpdir = new java.io.File(stmpdir);
        if(!tmpdir.exists()) {
            tmpdir.mkdirs();
        }

        CleanDirectory.run( tmpdir );
                
        return tmpdir.getAbsolutePath();
    }
    public boolean onBeforeSave( boEvent event ) throws boRuntimeException
    {
        boolean result = false;
        boolean change = false;
        iFile file = this.getAttribute("file").getValueiFile();
        if(file != null)
        {
            String uri = file.getURI();
            if(file != null && uri != null && !uri.startsWith("//"+DBiFile.IFILE_SERVICE_NAME) && !uri.startsWith("//"+BasiciFile.IFILE_SERVICE_NAME) ) 


            {
                change = true;
            }
            result = super.onBeforeSave(event);
            if(change)
            {


                file = this.getAttribute("file").getValueiFile();        
                this.getAttribute("fileSize").setValueObject(BigDecimal.valueOf(file.length()));
                this.getAttribute("fileName").setValueString(file.getName());
                String formato = uri.substring(uri.lastIndexOf(".")+1,uri.length()).toUpperCase();
                if(this.getAttribute("designacao")!=null && this.getAttribute("designacao").getValueString().length() == 0)
                    this.getAttribute("designacao").setValueString(file.getName());
                if(this.getAttribute("formato")!=null && this.getAttribute("formato") != null)
                    this.getAttribute("formato").setValueString(formato);
                
                // Pedro Senos - Apagar ficheiro temporário
                //File fsf = new File(DocumentHelper.getTempDir() + File.separator + file.getName());
                //fsf.delete();
        //        this.getAttribute("lastModified").setValueObject(new Timestamp(xfile.lastModified()));
            }
        }
        else
        {
            result = true;
        }
        return result;
   }
   
   public void onCommit() throws boRuntimeException
    {
        // Pedro Senos - Apagar ficheiro temporário
        iFile file = this.getAttribute("file").getValueiFile();
        if( file != null ) {
        File fsf = new File(DocumentHelper.getTempDir() + File.separator + file.getName());
        fsf.delete();
        }
    }

    public void poolObjectPassivate()
    {
        // TODO:  Override this netgest.bo.runtime.boObject method
        try {
            try {
                FSiFile file = (FSiFile)this.getAttribute("file").getValueiFile();
                
                if(file !=null && file.getFileProvider()==null) 
                {
                    file.delete();
                }
            } 
            catch (ClassCastException e) 
            {
                
            }
        } 
        catch(Exception e) 
        {
            logger.error(e);    
        }
        super.poolObjectPassivate();
    }    
}