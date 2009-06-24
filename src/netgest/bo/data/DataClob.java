/*Enconding=UTF-8*/
package netgest.bo.data;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;

import java.sql.Clob;
import java.sql.SQLException;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class DataClob implements Clob, Serializable
{
    /**
     * 
     * @since 
     */
    
    protected String p_clobdata;
    public DataClob( Reader reader, int chunkSize )
    {
        try
        {
            StringBuffer sb = new StringBuffer( chunkSize );
            char[] buffer = new char[ chunkSize ];
            int br;
            while( (br=reader.read( buffer )) > 0 )
            {
                sb.append( buffer, 0 , br );
            }
            p_clobdata = sb.toString();
        }
        catch (IOException e)
        {
            throw new DataException("0000","IOException reading Clob from stream.\n"+e.getClass()+"\n"+e.getMessage());
        }
    }
    
    public DataClob( String clobData )
    {
        p_clobdata = clobData;
    }

    public final long length() throws SQLException
    {
        return 0;
    }

    public final String getSubString(long pos, int length) throws SQLException
    {
        return null;
    }

    public final Reader getCharacterStream() throws SQLException
    {
        return null;
    }

    public final InputStream getAsciiStream() throws SQLException
    {
        return null;
    }

    public final long position(String searchstr, long start) throws SQLException
    {
        return 0;
    }

    public final long position(Clob searchstr, long start) throws SQLException
    {
        return 0;
    }

    public final int setString(long pos, String str) throws SQLException
    {
        return 0;
    }

    public final int setString(long pos, String str, int offset, int len) throws SQLException
    {
        return 0;
    }

    public final OutputStream setAsciiStream(long pos) throws SQLException
    {
        return null;
    }

    public final Writer setCharacterStream(long pos) throws SQLException
    {
        return null;
    }

    public final void truncate(long len) throws SQLException
    {
    }
    
    public final String toString()
    {
        return p_clobdata;
    }
    public final boolean equals( Object to )
    {
        return p_clobdata != null && p_clobdata.equals( to.toString() );
    }
    
    // Since JDK 1.6
    public void free() {
        throw new RuntimeException("Not Implemented");
    }
    
    // Since JDK 1.6
    public Reader getCharacterStream(long pos,
                                     long length) {
        throw new RuntimeException("Not Implemented");
    }
}
