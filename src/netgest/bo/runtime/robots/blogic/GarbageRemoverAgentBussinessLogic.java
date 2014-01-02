package netgest.bo.runtime.robots.blogic;


import netgest.bo.localizations.LoggerMessageLocalizer;
import netgest.bo.runtime.*;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.bo.system.Logger;

public class GarbageRemoverAgentBussinessLogic 
{

  //logger
  private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.GarbageRemoverAgentBussinessLogic");
  private boApplication p_app = null;    
  private static long WAIT_TIME = 5000000;
  public static long totalWorkTime = 0;

  public GarbageRemoverAgentBussinessLogic(boApplication app)
  {  
    p_app=app;
  }
  
    public void execute()
    {    
        EboContext ctx = null;
        boSession session = null;
        boolean locked = false;
        logger.finest(LoggerMessageLocalizer.getMessage("STARTING_GARBAGE_REMOVER_AGENT"));
        try
        {
            session = p_app.boLogin("SYSTEM",boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName());
            
            try 
            {
                ctx = session.createRequestContext(null,null,null);
                destroyGarbage(ctx);                    
            }
            finally
            {
                ctx.close();
            }
        }
        catch (boLoginException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_GARBAGE_REMOVER_AGENT") + e.getMessage(), e);
        }  
        catch (boRuntimeException e)
        {
            logger.severe(LoggerMessageLocalizer.getMessage("ERROR_IN_GARBAGE_REMOVER_AGENT") + e.getMessage(), e);
        }
    }   
    /**
     * Remove os objectos de negócio que já não são necessários.
     * @param ctx contexto.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static void destroyGarbage(EboContext ctx) throws boRuntimeException
    {
        destroySerialObjects(ctx);
    }    
    /**
     * Remove os xwfSerialObjects que já não estão a ser utilizados.
     * @param ctx contexto.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    private static void destroySerialObjects(EboContext ctx) throws boRuntimeException
    {
        boObject object = null;
        boObjectList list = boObjectList.list(ctx,"SELECT xwfSerialObject WHERE uid IN ( SELECT Ebo_Registry )",false,false);
        list.beforeFirst();
        while(list.next())
        {
            try 
            {
                object = list.getObject();
                object.destroyForce();   
            }
            catch (Exception ex) 
            {
                logger.severe(LoggerMessageLocalizer.getMessage("CANNOT_REMOVE_XWFSERIALOBJECT")+" " + object.getBoui());
            }                    
        }
    }  
}