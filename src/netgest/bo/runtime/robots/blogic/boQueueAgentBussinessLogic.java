package netgest.bo.runtime.robots.blogic;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import netgest.bo.impl.Ebo_QueueImpl;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.system.Logger;
public class boQueueAgentBussinessLogic 
{

  private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.boQueueAgentBussinessLogic");

  private boApplication p_boapp;

  public boQueueAgentBussinessLogic( boApplication boapp)
  {
    p_boapp=boapp;
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
                String boql = "{Ebo_Queue}SELECT BOUI from Ebo_Queue WHERE executed < 1 OR executed IS NULL AND (afterDate IS NULL OR afterDate < " + 
                	eboctx.getDataBaseDriver().getDriverUtils().fnSysDateTime() + 
                ")";
                
                boObjectList queues = boObjectList.list(eboctx,boql.toString());
                queues.beforeFirst();                                        
                // Processamento;
                boolean executed = false;
                boObject queue = null;
                while(queues.next())
                {
                    try 
                    {
                        executed = false;
                        queue = queues.getObject();
                        executed = ((Ebo_QueueImpl)queue).execute(eboctx);
                        if(executed)
                        {
                            queue.getAttribute("executed").setValueString("1");
                            queue.getAttribute("errormessage").setValueString("");
                        }
                    }
                    catch (boRuntimeException ex) 
                    {       
                        try
                        {
                            CharArrayWriter cw = new CharArrayWriter();
                            PrintWriter pw = new PrintWriter( cw );
                            ex.printStackTrace( pw );
                            pw.close();
                            cw.close();                            
                            queue.getAttribute("errormessage").setValueString(cw.toString());
                            queue.getAttribute("executed").setValueString("-1");
                        }
                        catch (Exception e)
                        {
                            
                        }
                    }               
                    catch (Throwable ex) 
                    {       
                        try
                        {
                            CharArrayWriter cw = new CharArrayWriter();
                            PrintWriter pw = new PrintWriter( cw );
                            ex.printStackTrace( pw );
                            pw.close();
                            cw.close();
                            queue.getAttribute("errormessage").setValueString( cw.toString() );
                            queue.getAttribute("executed").setValueString("-1");
                        }
                        catch (Exception e)
                        {
                            
                        }
                    }               
                    try
                    {
                        queue.update();
                    }
                    catch (boRuntimeException e)
                    {
                        try
                        {
                            queue.getAttribute("executed").setValueString("-1");
                        }
                        catch (Exception ec)
                        {
                            
                        }
                    }
                }
                //
            }
            finally
            {
                eboctx.close();
            }                
        }
        catch (boLoginException e)
        {
            logger.severe("",e);
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