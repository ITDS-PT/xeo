package netgest.bo.http;
import java.io.*; 
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.*;
import javax.servlet.http.*;

public class boCharsetFilter implements Filter {
    private String encoding;
    
    public void init(FilterConfig config) 
        throws ServletException {
        encoding = config.getInitParameter("requestEncoding");
        if( encoding==null ) encoding="UTF-8";
    }

    //Pattern p1 = Pattern.compile( "(.*)/viewers/(.*)/resources/(.*)\\.gif" );
    //Pattern p2 = Pattern.compile( "(.*)/resources/(.*)\\.gif" );
    
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
        throws IOException, ServletException {
        
        // Respect the client-specified character encoding
        // (see HTTP specification section 3.4.1)
        if(null == request.getCharacterEncoding())
            request.setCharacterEncoding(encoding);
            
    	next.doFilter(request, response);
    	
        
    }

    public void destroy(){}

}
