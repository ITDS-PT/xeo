package netgest.bo.runtime.robots.blogic;
import netgest.bo.controller.ControllerFactory;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.controller.xwf.XwfKeys;
import netgest.bo.dochtml.docHTML;
import netgest.bo.message.MessageServer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.xwf.*;
import netgest.xwf.common.*;
import netgest.xwf.core.*;

import netgest.bo.system.Logger;

public class xwfActivityRobotBussinessLogic 
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
    
  public xwfActivityRobotBussinessLogic(boApplication boapp)
  {
    p_boapp=boapp;
  }
  
    public void execute()
    {
        boSession session = null;
        try
        {
            session = p_boapp.boLogin("ROBOT", boLoginBean.getSystemKey() );          
            EboContext eboctx = null;
            try
            {

                eboctx = session.createRequestContext( null,null,null );
                // Select dos objectos pendentes                         
                boObjectList bol = boObjectList.list(eboctx, "select xwfActivity where CTX_ALLPERFORMERGROUPS and runningState<>90 and runningState<>20", true, false);
                bol.beforeFirst();
                /*
                docHTML bodoc=null;
                if(!bol.isEmpty())
                {
                  bodoc = new docHTML(eboctx, 1111);
                  bodoc.poolSetStateFull();
               //   bodoc.getEboContext().setPreferredPoolObjectOwner( bodoc.poolUniqueId() );
                }*/
                while(bol.next())
                {
                  boObject act = bol.getObject();
                  try{
//                          XwfController xc = new XwfController(bodoc,  act.getAttribute("program").getValueLong());
//                          ControllerFactory.setControllerByForce(bodoc,xc);    
                      xwfEngineGate engine = new xwfEngineGate(eboctx,act.getAttribute("program").getObject());
//                          act = xc.getEngine().getBoManager().getObject(bol.getCurrentBoui());
                      act = engine.getObject(bol.getCurrentBoui());
//                          xc.setOverrideRuntimeActivity(act.getBoui());
                      
                      if("xwfActivitySend".equals(act.getName()))
                      {
                        boObject var = act.getAttribute("message").getObject();
//                            boObject msg = xc.getEngine().getBoManager().getValueBoObject(var.getAttribute("value").getObject());
                        boObject msg = engine.getBoManager().getValueBoObject(var.getAttribute("value").getObject());
//                            msg.getAttribute("preferedMedia").setValueString("Sgis");
//                            xc.getEngine().getBoManager().updateObject(act);
                        engine.getBoManager().updateObject(act);
//                            xwfActionHelper.sendActivity(xc, act);
                        xwfMessage.deliverMessage(engine,act);
                      } 
                      else
//                            engine.setActivityState(act.getBoui(), XwfKeys.ACTION_CLOSE_KEY); // devia ser este codigo

                        // Rever este código deve ser o mesmo do metodo anterior
                        xwfFunctions.setActivityData(act, XwfKeys.ACTION_CLOSE_KEY);
                        act.getStateAttribute("runningState").setValue(XwfKeys.ACTION_CLOSE_KEY);
                        act.getAttribute("done").setValueString("1");
                        act.update();                            
                        engine.getManager().getControlFlow().finishedStep(act.getAttribute("unique_sid").getValueString());
                        
                  }catch(boRuntimeException boE){
                        act.getStateAttribute("runningState").setValue(XwfKeys.ACTION_CANCEL_KEY);
                        act.update();
                    }
                }
                /*
                if(bodoc != null)
                  bodoc.releaseObjects();
                */
                //
            }
            catch (boRuntimeException rte)
            {
                rte.printStackTrace();
            }
            finally
            {
                eboctx.close();
            }
                
        }
        catch (boLoginException rte)
        {
            rte.printStackTrace();
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