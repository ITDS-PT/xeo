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

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain next)
        throws IOException, ServletException {
        
        
        String path = ((HttpServletRequest)request).getRequestURI();
        
        Pattern p = Pattern.compile( "(.*)/viewers/(.*)/resources/(.*)\\.gif" );
        Matcher m = p.matcher( path );
        if( m.matches() && request.getAttribute("__forward") == null ) { 
        	String newUril = "/.xeodeploy/resources/"+m.group(3)+".gif";
            RequestDispatcher dispatch = request.getRequestDispatcher( newUril );
            if( dispatch != null ) {
            	request.setAttribute("__forward",Boolean.TRUE);
            	dispatch.forward( request, response );
            }
            else {
            	next.doFilter(request, response);            	
            }
		}
        else {
            // Respect the client-specified character encoding
            // (see HTTP specification section 3.4.1)
            if(null == request.getCharacterEncoding())
                request.setCharacterEncoding(encoding);
        	next.doFilter(request, response);
        }
        
    }

    public void destroy(){}

}
