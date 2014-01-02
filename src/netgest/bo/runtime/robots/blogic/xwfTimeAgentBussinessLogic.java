package netgest.bo.runtime.robots.blogic;

import netgest.bo.dochtml.docHTML;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;

import netgest.xwf.common.xwfBoManager;
import netgest.xwf.core.xwfControlFlow;

import netgest.bo.system.Logger;

public class xwfTimeAgentBussinessLogic 
{
  //logger
  private static Logger logger = Logger.getLogger(
          "netgest.bo.runtime.robots.blogic.xwfTimeAgentBussinessLogic");
  /**
   * 
   * @Company Enlace3
   * @since 
   */
  private boApplication p_boapp;
  public xwfTimeAgentBussinessLogic(boApplication boapp)
  {
    p_boapp = boapp;
  }
  
    public void execute()
    {
        boSession session = null;
        try
        {           
            session = p_boapp.boLogin("SYSUSER", boLoginBean.getSystemKey() );
            EboContext eboctx = null;
            try
            {

                eboctx = session.createRequestContext( null,null,null );
                // Select dos objectos pendentes     
                boObjectList bol = boObjectList.list(eboctx, "select xwfWait where endTime is not null and endTime <= sysdate and done = '0'");
                bol.beforeFirst();
                docHTML bodoc=null;
                if(!bol.isEmpty())
                {
                  bodoc = new docHTML(eboctx, 1111);
                }
                while(bol.next())
                {
                  boObject wt = bol.getObject();
                  xwfBoManager xbm = new xwfBoManager(bodoc, wt.getAttribute("program").getObject());
                  xwfControlFlow cf = new xwfControlFlow(xbm);
                  wt.getAttribute("done").setValueString("1");
                  wt.update();
                  cf.timeoutStep(wt.getAttribute("unique_sid").getValueString(), wt.getAttribute("name").getValueString());
                  
                  
                }
                if(bodoc != null)
                  bodoc.releaseObjects();
                
                //
            }
            catch (boRuntimeException rte)
            {
                rte.printStackTrace();
            }
            catch(Exception e)
            {
            }
            finally
            {
                eboctx.close();
            }                                              
        }
        catch (boLoginException e)
        {
            logger.severe("", e);
        }
        finally
        {
            try
            {
                if(session != null)
                {
                    session.closeSession();
                }
            }
            catch (Exception e)
            {
                
            }
        }
    }  
}