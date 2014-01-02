package netgest.bo.http;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
public class PerformanceConfigBean 
{
    public boolean metricJSP = true;
    public boolean metricHTML = true;
    
    public String  prfconfigid = null;

    public Vector excludeJSP = new Vector();
    public Vector excludeHTML = new Vector();
    
    public PerformanceConfigBean()
    {
        loadConfig();
    }
    public void loadConfig()
    {
        
        metricJSP = PerformanceFilter.METRIC_JSP;
        metricHTML = PerformanceFilter.METRIC_HTML;
        
        excludeJSP.clear();
        excludeHTML.clear();
        
        Enumeration enumExcludeHTML = PerformanceFilter.excludeHTML.keys();
        Enumeration enumExcludeJSP = PerformanceFilter.excludeJSP.keys();

        while( enumExcludeJSP.hasMoreElements() )
        {
            excludeJSP.add( enumExcludeJSP.nextElement() );
        }

        while( enumExcludeHTML.hasMoreElements() )
        {
            excludeHTML.add( enumExcludeHTML.nextElement() );
        }
    
        
    }
    
    public static final PerformanceConfigBean processRequest( HttpServletRequest request )
    {
        String prfconfigId = (String)request.getParameter("prfconfigid");
        if( prfconfigId == null )
        {
            prfconfigId = String.valueOf( System.currentTimeMillis() );
        }
        PerformanceConfigBean ret = (PerformanceConfigBean)request.getSession().getAttribute("PRFCONFIGBEAN_"+prfconfigId );
        if( ret == null )
        {
            ret = new PerformanceConfigBean();
            ret.prfconfigid = prfconfigId;
            request.getSession().setAttribute( "PRFCONFIGBEAN_"+prfconfigId , ret );
        }
        else
        {
            ret.bindValues( request );
        }
        ret.doActions( request );
        return ret;
    }
    
    public void bindValues( HttpServletRequest request )
    {
        this.metricJSP = request.getParameter("metricjsp")!=null;
        this.metricHTML = request.getParameter("metrichtml")!=null;
    }
    
    public void doActions( HttpServletRequest request )
    {
        if( request.getParameter( "addExcludeJSP" ) != null )
        {
            String jsp = request.getParameter( "excludeJSPAdd" );
            if( jsp != null && !"".equals( jsp ))
            {
                this.excludeJSP.add( jsp );
            }
        }
        if( request.getParameter( "removeExcludeJSP" ) != null )
        {
            String jsp = request.getParameter( "excludeJSP" );
            if( jsp != null && !"".equals( jsp ))
            {
                this.excludeJSP.remove( jsp );
            }
        }
        if( request.getParameter( "addExcludeHTML" ) != null )
        {
            String jsp = request.getParameter( "excludeHTMLAdd" );
            if( jsp != null && !"".equals( jsp ))
            {
                this.excludeHTML.add( jsp );
            }
            
        }
        if( request.getParameter( "removeExcludeHTML" ) != null )
        {
            String jsp = request.getParameter( "excludeHTML" );
            if( jsp != null && !"".equals( jsp ))
            {
                this.excludeHTML.remove( jsp );
            }
        }
        
        if( request.getParameter( "revert" ) != null )
        {
            PerformanceFilter.loadFromFile();
            loadConfig();
        }
        if( request.getParameter( "apply" ) != null )
        {
            PerformanceFilter.loadFromProperties( generateProperties() );
            loadConfig();
        }        
        if( request.getParameter( "save" ) != null )
        {
            PerformanceFilter.loadFromProperties( generateProperties() );
            PerformanceFilter.saveToFile();
            loadConfig();
        }
        
    }
    
    public Properties generateProperties()
    {
        Properties props = new Properties();
        props.setProperty( "metricJSP", String.valueOf( metricJSP ) );
        props.setProperty( "metricHTML", String.valueOf( metricHTML ) );
        
        StringBuffer sbexludedJSP ;

        sbexludedJSP = new StringBuffer();
        for (int i = 0; i < excludeJSP.size(); i++) 
        {
            if( i > 0  ) sbexludedJSP.append("|");
            sbexludedJSP.append( excludeJSP.get( i ) );
        }
        if( excludeJSP.size() > 0 ) props.setProperty( "excludedJSP", sbexludedJSP.toString() );
        
        StringBuffer sbexludedHTML = new StringBuffer();
        for (int i = 0; i < excludeHTML.size(); i++) 
        {
            if( i > 0  ) sbexludedHTML.append("|");
            sbexludedHTML.append( excludeHTML.get( i ) );
        }
        if( excludeHTML.size() > 0 ) props.setProperty( "excludedHTML", sbexludedHTML.toString() );

        return props;
    }
    
    
    public String getHTMLExcludes()   
    {
        return getOptionsString( excludeHTML );
    }
    public String getJspExcludes()   
    {
        return getOptionsString( excludeJSP );
    }
    private String getOptionsString( Vector opts )
    {
        StringBuffer sb = new StringBuffer();
        Enumeration enumOpts = opts.elements();
        while( enumOpts.hasMoreElements() )
        {
            String name = enumOpts.nextElement().toString();
            sb.append("<option value='"+name+"'>");
            sb.append( name );
            sb.append("</option>");
        }
        return sb.toString();
    }
}