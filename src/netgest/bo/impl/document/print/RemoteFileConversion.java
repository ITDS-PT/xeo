package netgest.bo.impl.document.print;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import netgest.bo.impl.document.print.remote.ConvertImagesStub;
import netgest.bo.system.boApplicationConfig;
import netgest.io.iFile;
import netgest.io.iFilePermissionDenied;
import netgest.utils.IOUtils;
import netgest.utils.ngtXMLUtils;
import netgest.utils.ngtXMLHandler;
import oracle.xml.parser.v2.XMLElement;
import org.w3c.dom.Node;
import netgest.bo.system.boApplication;
import netgest.bo.boConfig;

public class RemoteFileConversion 
{
    public RemoteFileConversion()
    {
    }
    public synchronized static File[] converIFile( String oper, String user, iFile file, long docBoui, String toMimeType)
    {
        return converIFile(oper, user, file, docBoui, toMimeType, Boolean.TRUE);
    }
    
    public synchronized static File[] converIFile( String oper, String user, iFile file, long docBoui, String type, Boolean useCache )
    {
        try
        {
            byte[] buffer = new byte[8192];
            int    bread  = 0;
            InputStream is = file.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            while( (bread = is.read( buffer )) > 0 )
            {
                out.write( buffer,0,bread );
            }
            out.close();
            is.close();
            
            return converIFile( oper, user, file.getName(),out, docBoui, type, useCache );
            
        }
        catch (Exception e)
        {
            throw new RuntimeException( e );
        }
    }

    public synchronized static File[] convertFile( String oper, String user, File file, long docBoui, String type, Boolean useCache )
    {
            try
            {
                byte[] buffer = new byte[8192];
                int    bread  = 0;
                InputStream is = new FileInputStream( file );
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                while( (bread = is.read( buffer )) > 0 )
                {
                    out.write( buffer,0,bread );
                }
                out.close();
                is.close();
                return converIFile( oper, user, file.getName(),out, docBoui, type, useCache );
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
    }
    
    public synchronized static File[] converIFile(String oper, String user, String fileName, ByteArrayOutputStream out, long docBoui, String type, Boolean useCache )
    {
        try
        {           
            ConvertImagesStub cisStub = new ConvertImagesStub();
            cisStub.setEndpoint( 
                    boApplication.getApplicationFromStaticContext("XEO")
                    .getApplicationConfig()
                    .getConvertImagesEndPoint()
                );
                        
            if( fileName.toLowerCase().endsWith(".tiff") )
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('.') ) + ".tif";
            }
            else if( fileName.toLowerCase().endsWith(".jpeg") )
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('.') ) + ".jpg";
            }
            
            String[] images = cisStub.convertBytes( oper, user ,fileName, out.toByteArray(),String.valueOf( docBoui ), type );
            File[] ret = new File[ images.length ];
            for (int i = 0; i < images.length; i++) 
            {
                File tmpFile = new File( getTMPDirectory() + File.separator + images[i] );

                byte[] imgbytes = cisStub.getCachedFile( images[i] );
                FileOutputStream fout = new FileOutputStream( tmpFile );
                fout.write( imgbytes );
                fout.close();

                ret[i] = tmpFile;

            }
            return ret;
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    
    //Para manter compatibilidade
    @Deprecated
    public synchronized static byte[] convertFile(String fileName, byte[] fileData, String type, Boolean useCache )
    {
        try
        {           
            ConvertImagesStub cisStub = new ConvertImagesStub();
            cisStub.setEndpoint( 
                    boApplication.getApplicationFromStaticContext("XEO")
                    .getApplicationConfig()
                    .getConvertImagesEndPoint()
                );
                        
            if( fileName.toLowerCase().endsWith(".tiff") )
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('.') ) + ".tif";
            }
            else if( fileName.toLowerCase().endsWith(".jpeg") )
            {
                fileName = fileName.substring(0, fileName.lastIndexOf('.') ) + ".jpg";
            }
            
            String[] images = cisStub.convertBytes( null, null , fileName, fileData, null, type );

            if(images.length>1)
            	throw new RuntimeException("Multiple File Convertion not Suported");

            if(images.length==0)
            	return new byte[0];

           return cisStub.getCachedFile( images[0] );
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }    

    protected static String getTMPDirectory() 
    {
        String tmp = System.getProperty("java.io.tmpdir");
        File f = new File(tmp+File.separator+"XEOPREVIEWCACHE");
        if (!f.exists())
            f.mkdir();
        return tmp+File.separator+"XEOPREVIEWCACHE";
    }


}