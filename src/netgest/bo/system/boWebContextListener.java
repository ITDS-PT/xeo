package netgest.bo.system;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import netgest.bo.system.Logger;

public class boWebContextListener implements ServletContextListener
{

    private static Logger logger = Logger.getLogger(boWebContextListener.class.getName());

    public boWebContextListener()
    {
    }

    public void contextInitialized(ServletContextEvent oServletContext )
    {
        logger.finer("Initializing XEO.... ");
        boApplication.getApplicationFromStaticContext("XEO");
    }

    public void contextDestroyed(ServletContextEvent oServletContext )
    {
    }
}