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
        
        /*
        String path = ((HttpServletRequest)request).getRequestURI();
        
        Matcher m1 = p1.matcher( path );
        Matcher m2 = p2.matcher( path );
        
        if( m1.matches() && request.getAttribute("__forwardResourceGIF") == null ) { 
        	String newUril = "/.xeodeploy/resources/"+m1.group(3)+".gif";
            RequestDispatcher dispatch = request.getRequestDispatcher( newUril );
            if( dispatch != null ) {
            	request.setAttribute("__forwardResourceGIF",Boolean.TRUE);
            	dispatch.forward( request, response );
            }
            else {
            	next.doFilter(request, response);            	
            }
		}
        else if( m2.matches() && request.getAttribute("__forwardResourceGIF") == null ) { 
        	String newUril = "/.xeodeploy/resources/"+m2.group(2)+".gif";
            RequestDispatcher dispatch = request.getRequestDispatcher( newUril );
            if( dispatch != null ) {
            	request.setAttribute("__forwardResourceGIF",Boolean.TRUE);
            	dispatch.forward( request, response );
            }
            else {
            	next.doFilter(request, response);            	
            }
		}
        else {
        */
            // Respect the client-specified character encoding
            // (see HTTP specification section 3.4.1)
            if(null == request.getCharacterEncoding())
                request.setCharacterEncoding(encoding);
        	next.doFilter(request, response);
        //}
        
    }

    public void destroy(){}

}
