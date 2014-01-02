package netgest.bo.http;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class BufferFilter implements Filter 
{

    public static final int BUFFER_SIZE = 512*1024;


    private FilterConfig _filterConfig = null;

    public void init(FilterConfig filterConfig) throws ServletException
    {
        _filterConfig = filterConfig;
    }

    public void destroy()
    {
        _filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        String url = ((HttpServletRequest)request).getServletPath();
        if( url != null && 
                ( 
                    url.endsWith(".jsp") || url.indexOf(".") == -1
                )
                &&
                !url.endsWith("WFService")  
                &&
                !url.endsWith("WSDocument")  
                &&
                !url.endsWith("WSExplorer")  
                &&
                !url.endsWith("DMWebService")  
                &&
                !url.endsWith("XEOHub")  
            )
        {
            ResponseWrapperBuffer respBuffer = new ResponseWrapperBuffer( (HttpServletResponse)response );
            try
            {
                chain.doFilter(request, respBuffer);
            }
            finally
            {
                respBuffer.flush();
            }
        }
        else
        {
            chain.doFilter(request, response);
        }
    }
    
    public class ResponseWrapperBuffer extends HttpServletResponseWrapper
    {
        String                  content_type    = null;
        FilterServletOutputStream fout          = null;
        ByteArrayOutputStream   out             = null;
        ServletOutputStream     _out            = null;
        PrintWriter             pw              = null;
        
        public ResponseWrapperBuffer( HttpServletResponse response ) throws IOException
        {
            super( response );
            _out = response.getOutputStream();
        }
        
        
        private void initOut()
        {
            if( out == null )
            {
                out = new ByteArrayOutputStream();
            }
        }
        
        public PrintWriter getWriter() throws IOException
        {
            initOut();
            if( pw == null )
            {
                pw = new PrintWriter( out, true );               
            }
            return pw;
        }

        public ServletOutputStream getOutputStream() throws IOException
        {
            initOut();
            if( fout == null )
            {
                fout = new FilterServletOutputStream( out, this );
            }
            return fout;
        }

        public void setContentType(String p0)
        {
            // TODO:  Override this javax.servlet.ServletResponseWrapper method
            this.content_type = p0;
            super.setContentType(p0);
        }

        public void setStatus(int p0)
        {
            boolean tobreak;
            if( p0 >= 500 )
                tobreak = true;
            super.setStatus(p0);
        }

        public void setStatus(int p0, String p1)
        {
            boolean tobreak;
            if( p0 >= 500 )
                tobreak = true;
            
            super.setStatus(p0, p1);
        }
        

       public final void checkBuffer()
       {
           if( out.size() > BUFFER_SIZE )
           {
                flush();                
           }
       }

        public void flush()
        {
            try
            {
                initOut();
                out.writeTo( _out );
            }
            catch (IOException e)
            {
                throw new RuntimeException(e);
            }
            out.reset();
        }
        
    }
    
    public class FilterServletOutputStream extends ServletOutputStream 
    {
   
       private DataOutputStream         stream;
       private ResponseWrapperBuffer    wrapper;
   
       public FilterServletOutputStream(OutputStream output,ResponseWrapperBuffer wrapper) 
       {
           stream = new DataOutputStream(output);
           this.wrapper = wrapper;
       }
   
       public void write(int b) throws IOException 
       {
           stream.write(b);
           checkBuffer();
       }
  
       public void write(byte[] b) throws IOException 
       {
           stream.write(b);
           checkBuffer();
       }
  
       public void write(byte[] b, int off, int len) throws IOException 
       {
           checkBuffer();
           stream.write(b, off, len);
       }
       
       public final void checkBuffer() throws IOException
       {
            wrapper.checkBuffer();
       }
   }    
   
   public class BufferOutputStream extends ByteArrayOutputStream
   {
        private ResponseWrapperBuffer wrapper;
        
        public BufferOutputStream( ResponseWrapperBuffer wrapper )
        {
            this.wrapper = wrapper;
        }
        public synchronized void write(int b)
        {
            // TODO:  Override this java.io.ByteArrayOutputStream method
            wrapper.checkBuffer();
            super.write(b);
        }

        public synchronized void write(byte[] b, int off, int len)
        {
            // TODO:  Override this java.io.ByteArrayOutputStream method
            wrapper.checkBuffer();
            super.write(b, off, len);
        }

        public void write(byte[] b) throws IOException
        {
            // TODO:  Override this java.io.OutputStream method
            wrapper.checkBuffer();
            super.write(b);
        }
       
   }
   
}