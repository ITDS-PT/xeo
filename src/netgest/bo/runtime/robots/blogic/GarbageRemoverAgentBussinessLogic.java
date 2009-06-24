package netgest.bo.runtime.robots.blogic;


import netgest.bo.runtime.*;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import org.apache.log4j.Logger;

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
        logger.debug("Starting Garbage Remover Agent ...");
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
            logger.error("Error in Garbage Remover Agent \n" + e.getMessage(), e);
        }  
        catch (boRuntimeException e)
        {
            logger.error("Error in Garbage Remover Agent \n" + e.getMessage(), e);
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
                logger.error("Cannot Remove xwfSerialObject " + object.getBoui());
            }                    
        }
    }  
}