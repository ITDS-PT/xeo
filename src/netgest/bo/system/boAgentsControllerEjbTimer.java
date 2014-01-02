package netgest.bo.system;

import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.ejb.CreateException;
import javax.ejb.RemoveException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import netgest.bo.boConfig;
import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.robots.ejbtimers.xeoEJBTimer;
import netgest.bo.runtime.robots.ejbtimers.xeoEJBTimerHome;
import netgest.utils.StringUtils;

public class boAgentsControllerEjbTimer implements IboAgentsController
{
  //logger
    private static Logger logger = Logger.getLogger("netgest.bo.system.boAgentsControllerEjbTimer");
    /**
     *
     * @since
     */
    /**
     *
     * @Company Enlace3
     * @since
     */

    private Hashtable    activeEJBTimers     = null; // Array with EJBTimers;


    private boApplication p_boapp;

    public boAgentsControllerEjbTimer( boApplication boapp )
    {
      this.p_boapp = boapp;
      this.activeEJBTimers = new Hashtable();        
      
    }

    public void start()
    {
        if (checkXeo())
        {
            try
            {
                String[] threads_name = p_boapp.getApplicationConfig().getThreadsName();
                for (int i = 0; i < threads_name.length ; i++)
                {
                    chekAndStartThread( i );
                }
            }
            catch (Exception e)
            {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_STARTING_XEO_EJBTIMER"),e);
            }
        }
    }

    public Object[] getThreadByName( String name )
    {
        if( activeEJBTimers.get( name ) != null )
        {
        	Object[] ret = new Object[1];
        	ret[0] = activeEJBTimers.get( name );
        	
        	return ret;
        	
        }
        return null;
    }


    public void chekAndStartThread( int id_idx )
    {
        String name = p_boapp.getApplicationConfig().getThreadsName()[ id_idx ]; //THREADS_NAME[ id_idx ];
        String ejbName=p_boapp.getApplicationConfig().getThreadsEjbName()[id_idx];
        String interval=p_boapp.getApplicationConfig().getThreadsInterval()[id_idx];
        long l_interval = 0;
        if ( interval != null && !interval.equals( "" ) ) 
        	l_interval = new Long( interval ).longValue();
        if ( !StringUtils.isEmpty( name ) && !StringUtils.isEmpty( ejbName ) )
        //if ( name != null && !name.equals( "" ) && ejbName != null && !ejbName.equals( "" ) )
        {
            try
            {
                String tname = "XEO EjbTimer [" + name  +"]";
                logger.finest(LoggerMessageLocalizer.getMessage("STARTING")+" " + tname );
                Context ic = new InitialContext();
                Object objref =
                    ic.lookup("java:comp/env/"+ ejbName);   
                xeoEJBTimerHome home =
                    (xeoEJBTimerHome) PortableRemoteObject.narrow(objref,
                        xeoEJBTimerHome.class);   
                xeoEJBTimer timerejb = home.create();
                timerejb.start(name);
                timerejb.remove();
                ic.close();
                logger.finest(LoggerMessageLocalizer.getMessage("STARTED")+""+tname);
                this.activeEJBTimers.put(name,timerejb);
            }
            catch (NamingException e) {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_LOOKING_UP_TO_XEO_EJBTIMER")+" ["+name+"]",e);
            }
            catch (CreateException e) {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_CREATING_XEO_EJBTIMER")+" ["+name+"]",e);
            }
            catch (RemoteException e) {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_EXECUTING_XEO_EJBTIMER")+" ["+name+"]",e);
            }   
            catch (RemoveException e) {
                logger.warn(LoggerMessageLocalizer.getMessage("ERROR_REMOVING_XEO_EJBTIMER")+" ["+name+"]",e);
            }               
        }
        
    }


    public boolean checkXeo()
    {
        boolean ret = true;
        try
        {
            final InitialContext ic = new InitialContext();
            ic.lookup("boLogin");  // Test if OC4J is up and running;
            ic.close();
        }
        catch( Exception e )
        {
            ret = false;
        }
        return ret && isXEOUpAndRunning();
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
    
    public void interrupt() {    
        suspendAgents();
    }
    
    public boolean isAlive() {
        if (this.activeEJBTimers!=null && !this.activeEJBTimers.isEmpty())
            return true;
        else 
            return false;
    }
    
    public void join() {
    
        System.out.println("XEO EJBTimer "+MessageLocalizer.getMessage("AGENT_CONTROLLER")+" JOIN METHOD");
    }
    
    public void suspendAgents()
    {
        Enumeration oEnum = activeEJBTimers.keys();
        while ( oEnum.hasMoreElements() )
        {            
            String currName = (String)oEnum.nextElement();            
            xeoEJBTimer currTimer=(xeoEJBTimer)this.activeEJBTimers.get(currName);
            suspendAgent(currName);
            activeEJBTimers.remove(currName);

        }
    }

    public void suspendAgent(String name)
    {
        try
        {
            xeoEJBTimer activeEJBTimer=(xeoEJBTimer)this.activeEJBTimers.get(name);

            logger.finest(LoggerMessageLocalizer.getMessage("STOPPING_XEO_EJBTIMER")+" ["+ name +"]");
            activeEJBTimer.suspend(name);    
            activeEJBTimers.remove(name);
            logger.finest(LoggerMessageLocalizer.getMessage("STOPPED_XEO_EJBTIMER")+" ["+ name +"]");
        }
        catch (RemoteException e)
        {
            logger.warn(LoggerMessageLocalizer.getMessage("ERROR_STOPPING_XEO_EJBTIMER")+" [" + name+"]");
        }      
    }
  
}