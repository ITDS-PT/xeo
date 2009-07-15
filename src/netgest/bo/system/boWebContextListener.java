package netgest.bo.system;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.log4j.Logger;

public class boWebContextListener implements ServletContextListener
{

    private static Logger logger = Logger.getLogger(boWebContextListener.class.getName());

    public boWebContextListener()
    {
    }

    public void contextInitialized(ServletContextEvent oServletContext )
    {
        logger.info("Initializing XEO.... ");
        boApplication.getApplicationFromStaticContext("XEO");
    }

    public void contextDestroyed(ServletContextEvent oServletContext )
    {
    }
}