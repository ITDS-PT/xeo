package netgest.bo.system;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

public class boWebContextListener implements ServletContextListener
{

    private static Logger logger = Logger.getLogger(boWebContextListener.class.getName());

//    private boApplication   boApp         = null;

    public boWebContextListener()
    {
    }

    public void contextInitialized(ServletContextEvent oServletContext )
    {
        logger.info("Initializing XEO.... ");
        boApplication.getApplicationFromStaticContext("XEO");

/*
        String configPath = System.getProperty("netgest.home");
        if( configPath == null )
        {
            configPath = System.getProperty("xeo.home");
        }
        
        if( configPath == null )
        {
            String rpath = oServletContext.getServletContext().getRealPath("WEB-INF");
            configPath = rpath + File.separator + "xeoHome" ;
        }

        File configFile = new File( configPath + File.separator + "boconfig.xml" );
        if( !configFile.exists() )
        {
            logger.error("Cannot find boconfig.xml, not found in WEB-INF/xeoHome/ and netgest.home not specified or invalid");
        }
        logger.info("Initializing XEO with config ["+configFile.getAbsolutePath()+"].... ");
        
        boApp = boApplication.getApplicationFromConfig( configFile.getAbsolutePath() );
*/
                
    }

    public void contextDestroyed(ServletContextEvent oServletContext )
    {
    }
}