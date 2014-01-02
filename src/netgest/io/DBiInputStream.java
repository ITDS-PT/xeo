/*Enconding=UTF-8*/
package netgest.io;

import java.io.*;
import java.sql.*;

public class DBiInputStream extends InputStream {
    private InputStream is;
    private Connection cn;

    public DBiInputStream(InputStream is,Connection cn) {
        this.is=is;
        this.cn=cn;
    }

    public void close() {
        try {
            is.close();
        } catch (IOException e) {
        } 
        try {
            cn.close();
        } catch (SQLException e) {
        }
    }

    public int available() throws IOException {
        try {
            return is.available();
        } 
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public synchronized void mark(int readlimit) {
        is.mark(readlimit);
        
    }

    public boolean markSupported() {
        return is.markSupported();
    }

    public int read() throws IOException {
        try {
            return is.read();
        }
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public int read(byte[] b) throws IOException {
        try {
            return is.read(b);
        }
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public int read(byte[] b, int off, int len) throws IOException {
        try 
        {
            return is.read(b, off, len);
        }
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public synchronized void reset() throws IOException {
        try {
            is.reset();
        }
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }

    public long skip(long n) throws IOException {
        try {
            return is.skip(n);
        }
        catch (IOException e) 
        {
            this.close();
            e.fillInStackTrace();
            throw e;    
        }
    }
    public void finalize() 
    {
        this.close();
    }
}