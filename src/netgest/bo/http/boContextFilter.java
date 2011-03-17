package netgest.bo.http;
import java.io.File;
import javax.servlet.ServletResponse;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.FilterConfig;
import javax.servlet.ServletRequest;
import javax.servlet.FilterChain;
import java.io.IOException;

import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.system.boApplication;
import netgest.bo.system.Logger;

public class boContextFilter implements Filter 
{

    private static Logger logger = Logger.getLogger( netgest.bo.http.boContextFilter.class );

    private FilterConfig    _filterConfig = null;
    private boApplication   boApp         = null;
    
    public void init(FilterConfig filterConfig) throws ServletException
    {
        logger.finer(LoggerMessageLocalizer.getMessage("INITIALIZING_XEOCONTEXTFILTER"));
        boApp = boApplication.getApplicationFromStaticContext( "XEO" );
    }

    public void destroy()
    {
        _filterConfig = null;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
    {
        try
        {
            boApp.addAContextToThread();
            chain.doFilter(request, response);
        }
        finally
        {
            boApp.removeContextFromThread();
        }
    }
}