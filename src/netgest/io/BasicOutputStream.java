/*Enconding=UTF-8*/
package netgest.io;
import java.io.IOException;
import java.io.OutputStream;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.SQLException;

import netgest.bo.localizations.MessageLocalizer;


public class BasicOutputStream extends OutputStream  {
    
    private OutputStream p_os = null;
    private Connection p_cn = null;
    private Blob p_blob = null;
    private BasiciFile p_file = null;
    
    public BasicOutputStream(BasiciFile file,Blob blob,Connection cn) 
    {
        try 
        {
            p_file = file;
            p_blob = blob;
            blob.truncate(0);            
            this.p_os=blob.setBinaryStream(1);
            this.p_cn=cn;
        } 
        catch (SQLException e) 
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_INITIALIZE_BLOB")+e.getClass().getName()+"\n"+e.getMessage());    
        }
    }

    public void write(int b) throws IOException 
    {    
        try 
        {
            p_os.write(b);
        } 
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
        
    }

    public void write(byte[] b, int off, int len) throws IOException 
    {        
        try 
        {
            p_os.write(b, off, len);
        } 
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public void write(byte[] b) throws IOException 
    {
        try 
        {
            p_os.write(b);
        } 
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public void flush() throws IOException 
    {    
        try 
        {
            p_os.flush();
        } 
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public void close() throws IOException 
    {    
        try
        {
            p_os.close();
            try
            {               
              //  p_blob.free();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }                            
        }
        finally
        {
            try
            {
                if(p_cn != null)
                    p_cn.close();        
            }
            catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}