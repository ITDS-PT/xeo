/*Enconding=UTF-8*/
package netgest.utils;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import netgest.bo.localizations.MessageLocalizer;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 */
public final class IOUtils 
{    
    public static final File copy(String src,String dest)
    {
        return copy(new File(src),new File(dest) );
    }
    public static final File copy(File src,String dest)
    {
        return copy(src,new File(dest) );
    }
    public static final File copy(String src,File dest)
    {
        return copy(new File(src),dest );
    }
    public static final File copy(File src, File dest)
    {
    	assert src != null : "Source file cannot be null";
    	assert dest != null : "Destination file cannot be null";
    	
        int br;
        byte[] buff = new byte[8096];
        try
        {
        	
        	if( !dest.getParentFile().exists() ) {
        		dest.getParentFile().mkdirs();
        	}
        	
            FileInputStream is = new FileInputStream(src);
            FileOutputStream os = new FileOutputStream(dest);
            while((br=is.read(buff))>0 ) {
                os.write(buff,0,br);
            }
            is.close();
            os.close();
        }
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_COPY_FILE")+e.getClass().getName() + '\n' + e.getMessage() );
        }
        catch (IOException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_COPY_FILE")+e.getClass().getName() + '\n' + e.getMessage() );
        }
        dest.setLastModified(src.lastModified());
        return dest;
    }
    
    public static final File copyProperties(File src, File dest)
    {
        try
        {
        	
        	if( !dest.getParentFile().exists() ) {
        		dest.getParentFile().mkdirs();
        	}
        	
        	Properties prop = new Properties();
			InputStream fis = new FileInputStream(src);
			InputStreamReader isr = new InputStreamReader(fis,"UTF-8");
			prop.load(isr);
			prop.store(new FileOutputStream(dest), null);
		}
        catch (FileNotFoundException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_COPY_FILE")+e.getClass().getName() + '\n' + e.getMessage() );
        }
        catch (IOException e)
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_COPY_FILE")+e.getClass().getName() + '\n' + e.getMessage() );
        }
        dest.setLastModified(src.lastModified());
        return dest;
    }
    
    
    public static byte[] copyByte(InputStream fis) throws IOException
    {
        ByteArrayOutputStream io = new ByteArrayOutputStream();
        byte[] buff = new byte[4096];
        int br=0;
        while( (br=fis.read( buff )) > 0 )
        {
            io.write( buff,0, br );
        }
        return io.toByteArray();
    }     
    
    public static final void copy( InputStream is, File out )
    {
        try
        {
            FileOutputStream fout = new FileOutputStream( out );
            byte[] buff = new byte[4096];
            int br=0;
            while( (br=is.read( buff )) > 0 )
            {
                fout.write( buff,0, br );
            }
            fout.close();        
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static String readFileAsString(File file) throws IOException {
    	 {
    		 DataInputStream in = null;
    		 StringBuilder buffer = new StringBuilder(300);
    		 try{
	    		  FileInputStream fstream = new FileInputStream(file);
	    		  in = new DataInputStream(fstream);
	    		  BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    		  String strLine = "";
	    		  //Read File Line By Line
	    		  while ((strLine = br.readLine()) != null)   {
	    		    buffer.append( strLine );
	    		  }
    		 }finally {
	    		  if (in != null)
	    			  in.close();
    		 }
    		  
    		  return buffer.toString();
    	  }
    }
}