/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import java.sql.Blob;
import java.sql.SQLException;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataBlob implements Blob, Serializable  
{
    /**
     * 
     * @since 
     */
    public DataBlob()
    {
    }

    public long length() throws SQLException
    {
        return 0;
    }

    public byte[] getBytes(long pos, int length) throws SQLException
    {
        return null;
    }

    public InputStream getBinaryStream() throws SQLException
    {
        return null;
    }

    public long position(byte[] pattern, long start) throws SQLException
    {
        return 0;
    }

    public long position(Blob pattern, long start) throws SQLException
    {
        return 0;
    }

    public int setBytes(long pos, byte[] bytes) throws SQLException
    {
        return 0;
    }

    public int setBytes(long pos, byte[] bytes, int offset, int len) throws SQLException
    {
        return 0;
    }

    public OutputStream setBinaryStream(long pos) throws SQLException
    {
        return null;
    }

    public void truncate(long len) throws SQLException
    {
    }
    
    // Since JDK 1.6
    public void free() {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public InputStream getBinaryStream(long pos,
                                       long length)  {
        throw new RuntimeException("Not Implemented");
    }
}
