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
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.WriterAppender;
import java.io.FileReader;

public class Builder extends HttpServlet  {
    //logger
    private static Logger logger = Logger.getRootLogger();

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
                } 
                catch( Exception e )
                {
                    logger.error("Error: ", e );
                }
                
                logger.addAppender(appender);
                logger.setLevel(Level.DEBUG); 
                               
                if (useInternalAuthentication) {
                	bosess = boapp.boLogin( "SYSTEM", boLoginBean.getSystemKey(), boapp.getDefaultRepositoryName()  );
                }
                else
                {
					bosess = (boSession)request.getSession().getAttribute("boSession");
					if (bosess==null || !bosess.getUser().getUserName().equalsIgnoreCase("SYSUSER"))
					{
					    logger.error("Authentication was not performed or your Session TimedOut. You must authenticate before you can run Builder.");
					    return;
					}
                }
                
                boctx = bosess.createRequestContext(request,response,null);

                UserTransaction ut=null;
                boolean ok = false;
                try
                {
                    logger.info("Starting suspending Agents");
                    boctx.getApplication().suspendAgents();
                    logger.info("Ended suspending Agents");
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
                        logger.info("Starting Agents");
                        boctx.getApplication().startAgents();
                        logger.info("Ended starting Agents");
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
                    logger.debug("Starting Tasks Validation ...");
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.debug("Starting [" + taskName + "] Validation");
                            if(task.validation(boctx))
                            {
                                logger.debug("Validation [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.debug("Validation [" + taskName + "] : NOT OK" );
                            }
                            logger.debug("Ended [" + taskName + "] Validation");
                        }
                        else
                        {
                            logger.debug("Task [" + taskName + "] : DONE ALREADY");
                        }
                    }
                    logger.debug("Ended Tasks Validation");
                }

                if(module != null && module.indexOf("taskExecution") != -1)
                {
                    logger.debug("Starting Tasks Execution ...");
                    Enumeration oEnum = Tasks.getInstance().getActiveTasksNames();
                    while(oEnum.hasMoreElements())
                    {
                        taskName = (String)oEnum.nextElement();
                        task = Tasks.getInstance().getClass(taskName);
                        if(!task.done(boctx))
                        {
                            logger.debug("Starting [" + taskName + "] Task");
                            if(task.execute(boctx))
                            {
                                logger.debug("Execution [" + taskName + "] : OK" );
                            }
                            else
                            {
                                logger.debug("Execution [" + taskName + "] : NOT OK" );
                            }
                            logger.debug("Ended [" + taskName + "] Task.");
                        }
                        else
                        {
                            logger.debug("Task [" + taskName + "] : DONE ALREADY");
                        }
                    }
                    logger.debug("Ended Tasks Execution");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if(boctx != null) boctx.close();
                logger.setLevel(sv);
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
            logger.removeAppender(appenderName);
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
