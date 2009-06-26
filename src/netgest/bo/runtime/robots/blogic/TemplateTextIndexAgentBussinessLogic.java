package netgest.bo.runtime.robots.blogic;

import netgest.bo.def.boDefHandler;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.impl.Ebo_TextIndexImpl;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import org.apache.log4j.Logger;

public class TemplateTextIndexAgentBussinessLogic 
{

  private static Logger logger = Logger.getLogger(
            "netgest.bo.runtime.robots.blogic.TemplateTextIndexAgentBussinessLogic");
  private boApplication p_app;
    
  public TemplateTextIndexAgentBussinessLogic(boApplication app)
  {
    p_app=app;
  }
  
    public void execute()
    {    
        EboContext ctx = null;
        boSession session = null;
        boolean locked = false;
        logger.debug("Starting Template_TextIndex rebuilder agent.....");
        try
        {          
        	if ( boDefHandler.getBoDefinition("iXEOUser") != null ) {
	            session = p_app.boLogin("SYSTEM",boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName());
	            ctx = session.createRequestContext(null,null,null);
	            Ebo_TemplateImpl.rebuildIndex(ctx);
        	}
        }
        catch (boLoginException e)
        {
            logger.error("Error Rebuilding Template TextIndex \n" + e.getMessage(), e);
        }  
        catch (boRuntimeException e)
        {
            logger.error("Error Rebuilding Template TextIndex \n" + e.getMessage(), e);
        }
        finally
        {
          ctx.close();
        }
    }      
}