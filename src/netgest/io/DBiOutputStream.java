/*Enconding=UTF-8*/
package netgest.io;
import java.io.OutputStream;
import java.io.IOException;
import java.sql.*;

import netgest.bo.localizations.MessageLocalizer;
import oracle.sql.*;

public class DBiOutputStream extends OutputStream  {
    OutputStream p_os;
    Connection p_cn;
    Blob p_blob;
    DBiFile p_file;
    public DBiOutputStream(DBiFile file,Blob blob,Connection cn) {
        try {
            p_file = file;
            p_blob = blob;
           // blob.truncate(0);
            this.p_os=blob.setBinaryStream(0);
            this.p_cn=cn;
        } 
        catch (SQLException e) 
        {
            throw new RuntimeException(MessageLocalizer.getMessage("CANNOT_INITIALIZE_BLOB")+e.getClass().getName()+"\n"+e.getMessage());    
        }
    }

    public void write(int b) throws IOException { 
        // TODO:  Implement this java.io.OutputStream abstract method
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

    public void write(byte[] b, int off, int len) throws IOException {
        // TODO:  Override this java.io.OutputStream method
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

    public void write(byte[] b) throws IOException {
        // TODO:  Override this java.io.OutputStream method
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

    public void flush() throws IOException {
        // TODO:  Override this java.io.OutputStream method
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

    public void close() throws IOException {
        // TODO:  Override this java.io.OutputStream method
        try
        {
            p_os.close();
            try
            {
              //  p_blob.free();
            }
            catch (Exception e)
            {
                
            }
            p_file.loadFile(p_cn);
                            
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
                
            }
        }
    }
}