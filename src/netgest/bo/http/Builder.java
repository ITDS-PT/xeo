/*Enconding=UTF-8*/
package netgest.bo.http;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.transaction.UserTransaction;

import netgest.bo.boConfig;
import netgest.bo.builder.ITask;
import netgest.bo.builder.boBuilder;
import netgest.bo.builder.boBuilderOptions;
import netgest.bo.def.Tasks;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;

import org.apache.log4j.Level;
import netgest.bo.system.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import java.io.FileReader;

public class Builder extends HttpServlet  {
    //logger
    private static Logger logger = Logger.getLogger( Builder.class.getName() );
    private static org.apache.log4j.Logger log4j = org.apache.log4j.Logger.getLogger( Builder.class );

    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";

    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String appenderName = response.toString();
        Level sv = logger.getLevel();
        try {
            EboContext boctx=null;
            
            boBuilderOptions builderOptions = new boBuilderOptions(); 

            
            boolean isxeoupandrunning = isXEOUpAndRunning();
            String module = request.getParameter("module");
            // rebuild default workplaces
            if(module != null && module.indexOf("rebuildWorkplacesDefault") != -1) 
            {
            	builderOptions.setBuildWorkplaces( true );
            }

            // remove user workplaces
            if(module != null && module.indexOf("removeUserWorkplaces") != -1)
            {
            	builderOptions.setRemoveUserWorkplaces( true );
            }
            if(module != null && module.indexOf("jsp") != -1) {
            	builderOptions.setBuildDatabase( false );
            	builderOptions.setGenerateAndCompileJava( false );
            }
            	            
            try {                                                
                boApplication  boapp = boApplication.getApplicationFromStaticContext("XEO");
                boSession bosess = null;
                
                boolean useInternalAuthentication=false;

                if (!isXEOUpAndRunning()) 
                  useInternalAuthentication=true;
                
                
                WriterAppender appender = null;
                try {
                    PatternLayout layout = new PatternLayout("%d (%F:%L) - %m%n");
                    appender = new WriterAppender(layout, response.getOutputStream());
                    appender.setName(appenderName);
                    appender.activateOptions();
                } 
                catch( Exception e )
                {
                    logger.severe(LoggerMessageLocalizer.getMessage("ERROR")+": ", e );
                }
                
                log4j.addAppender(appender);
                log4j.setLevel(Level.DEBUG); 
                               
                if (useInternalAuthentication) {
                	bosess = boapp.boLogin( "SYSTEM", boLoginBean.getSystemKey(), boapp.getDefaultRepositoryName()  );
                }
                else
                {
					bosess = (boSession)request.getSession().getAttribute("boSession");
					if (bosess==null || !bosess.getUser().getUserName().equalsIgnoreCase("SYSUSER"))
					{
					    logger.severe(LoggerMessageLocalizer.getMessage("AUTHENTICATION_WAS_NOT_PERFORMED_OR_YOUR_SESSION_TIMEOUT_YOU_MUST_"));
					    return;
					}
                }
                
                boctx = bosess.createRequestContext(request,response,null);

                UserTransaction ut=null;
                boolean ok = false;
                try
                {
                    logger.finer(LoggerMessageLocalizer.getMessage("STARTING_SUSPENDING_AGENTS"));
                    boctx.getApplication().suspendAgents();
                    logger.finer(LoggerMessageLocalizer.getMessage("ENDET_SUSPENDING_AGENTS"));
                    final InitialContext ic = new InitialContext();
                    ut = (UserTransaction) ic.lookup("java:comp/UserTransaction");
                    ut.setTransactionTimeout(6000000);
                    ut.begin();
                    treatRequest(boctx, request, builderOptions, false);
                    ok = true;

                }
                catch (NamingException e)
                {
                    throw new RuntimeException(e.getClass().getName() + "\n" + e.getMessage()  );
                }
                finally
                {
                    if(ok)
                    {
                        ut.commit();
                    }
                    else
                    {
                        ut.rollback();
                    }
                    try
                    {
                        logger.finer(LoggerMessageLocalizer.getMessage("STARTING_AGENTS"));
                        boctx.getApplication().startAgents();
                        logger.finer(LoggerMessageLocalizer.getMessage("ENDET_STARTING_AGENTS"));
                    }
                    catch (Exception e)
                    {
                        //ignore
                    }
                    boctx.close();
                }
                treatRequest(boctx, request, builderOptions, true );
                String taskName = null;
                ITask task = null;

                if(module != null && module.indexOf("taskValidation") != -1)
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("STARTING_TASK_VALIDATION"));
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("STARTING")+" [" + taskName + "] "+LoggerMessageLocalizer.getMessage("VALIDATION"));
                            if(task.validation(boctx))
                            {
                                logger.finest(LoggerMessageLocalizer.getMessage("VALIDATION")+" [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.finest(LoggerMessageLocalizer.getMessage("VALIDATION")+" [" + taskName + "] : NOT OK" );
                            }
                            logger.finest(LoggerMessageLocalizer.getMessage("ENDED")+" [" + taskName + "] "+LoggerMessageLocalizer.getMessage("VALIDATION"));
                        }
                        else
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("TASK")+" [" + taskName + "] : "+LoggerMessageLocalizer.getMessage("DONE_ALREADY"));
                        }
                    }
                    logger.finest(LoggerMessageLocalizer.getMessage("ENDED_TASKS_VALIDATION"));
                }

                if(module != null && module.indexOf("taskExecution") != -1)
                {
                    logger.finest(LoggerMessageLocalizer.getMessage("STARTING_TASK_VALIDATION"));
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("STARTING")+" [" + taskName + "] "+LoggerMessageLocalizer.getMessage("TASK"));
                            if(task.execute(boctx))
                            {
                                logger.finest(LoggerMessageLocalizer.getMessage("EXECUTION") +" [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.finest(LoggerMessageLocalizer.getMessage("EXECUTION") +" [" + taskName + "] : NOT OK" );
                            }
                            logger.finest(LoggerMessageLocalizer.getMessage("ENDED") +" [" + taskName + "] "+LoggerMessageLocalizer.getMessage("TASK")+".");
                        }
                        else
                        {
                            logger.finest(LoggerMessageLocalizer.getMessage("TASK") +" [" + taskName + "] : "+LoggerMessageLocalizer.getMessage("DONE_ALREADY"));
                        }
                    }
                    logger.finest(LoggerMessageLocalizer.getMessage("ENDED_TASK_EXECUTION"));
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(boctx != null) boctx.close();
                log4j.setLevel(sv);
            }
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println("<PRE>");
            response.setStatus(response.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace(out);
            out.println("</PRE>");
        }
        finally
        {
        	log4j.removeAppender(appenderName);
        }
    }

    public void treatRequest(EboContext boctx, HttpServletRequest request, boBuilderOptions options, boolean m ) throws boRuntimeException {
        String force = request.getParameter("force");
        String clean = request.getParameter("clean");
        if("true".equalsIgnoreCase(clean) && !m)
        {
        	options.setFullBuild( true );
        }

        if(force != null && force.trim().length() > 0)
        {
            boBuilder.forceDeploy(force.trim());
        }
    }
    
    public boolean isXEOUpAndRunning()
    {
		  FileReader ixeouser = null;
		  try
		  {
			   ixeouser=new FileReader(boConfig.getDeploymentDir()+"iXEOUser.xeoimodel");
			   boApplication boapp = boApplication.getApplicationFromStaticContext("XEO");
			   boapp.boLogin( "SYSUSER" , boLoginBean.getSystemKey() , boapp.getDefaultRepositoryName() );  
			   ixeouser.close();
			   return true; 
		  }
		  catch (Exception e)
		  {
		        try
		        {
		          if (ixeouser!=null)ixeouser.close();          
		        }
		        catch (Exception ex)
		        {
		        }
		        return false;
		  }
    }
}
