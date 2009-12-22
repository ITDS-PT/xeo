package netgest.bo.runtime.robots.blogic;
import java.io.CharArrayWriter;
import java.io.PrintWriter;

import java.sql.Connection;
import java.util.ArrayList;

import netgest.bo.impl.Ebo_TextIndexImpl;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.system.boApplication;
import netgest.bo.system.boLogin;
import netgest.bo.system.boLoginBean;
import netgest.bo.system.boLoginException;
import netgest.bo.system.boSession;
import netgest.bo.system.Logger;
import netgest.bo.runtime.robots.boTextIndexQueue;


public class boTextIndexAgentBussinessLogic 
{

  private static Logger logger = Logger.getLogger("netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic");

    /**
     * 
     * @since 
     */
  private boApplication p_app;
  
  public static String ThreadID = String.valueOf( System.currentTimeMillis() );
  
  public boTextIndexAgentBussinessLogic(boApplication boapp)
  {
    p_app=boapp;
  }

   private static long WAIT_TIME = 15000;
    
   public static boTextIndexQueue queue = new boTextIndexQueue();

    public static void addToQueue( EboContext ctx, ArrayList objects, String className )
    {
        if( !Thread.currentThread().getName().endsWith( ThreadID ) )
            queue.addItens( ctx, objects, className );
    }
    
    public static void addToQueue( boObject[] objects)  throws boRuntimeException
    {
        if( !Thread.currentThread().getName().endsWith( ThreadID ) )
            queue.addItens( objects );
    }

    public long execute()
    {
        boSession       session = null;
        Connection      cn   = null; 
        long workTime=0;
        try
        {
//            logger.finest("Starting EboTextIndex schedule agent.....");
            session =  p_app.boLogin( "SYSTEM", boLoginBean.getSystemKey(), p_app.getDefaultRepositoryName() );
            EboContext ctx = null;
            try 
            {

              ctx = session.createRequestContext(null,null,null);
              
              if( cn == null )
              {
                  cn = ctx.getConnectionData();
              }
              
              boolean wasupdated = false;
                                             
              
              
              
              long[] itens = null;
              long init = System.currentTimeMillis();
              int counter = 0; 
              itens = queue.pop( ctx, cn, 100 );
              ctx.close();
              cn = null;
              ctx = null;
              while( itens != null && counter < itens.length )
              {
                  try
                  {
                          if (ctx==null)ctx = session.createRequestContext(null,null,null);
                          if( cn == null )
                          {
                              cn = ctx.getConnectionData();
                          }                          
                          boObject.getBoManager().preLoadObjects( ctx, itens );
                          ctx.beginContainerTransaction();
                          long startTime = System.currentTimeMillis();
                          boolean commit = false;
                          try
                          {
                              wasupdated = true;

                              boObject object = boObject.getBoManager().loadObject( ctx, itens[counter] );
                              long boui = object.getBoui();
                              String name = object.getName();                              
                              if( object.exists() )
                              {                                  
                                  boObject textIndex = boObject.getBoManager().loadObject( ctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { new Long( boui )} );
                                  textIndex.getAttribute("ui").setValueLong( boui );
                                  textIndex.getAttribute("uiClass").setValueString( name );
                                  textIndex.update();
                                  ctx.clearObjectInTransaction();
                              }
                          }
                          catch (boRuntimeException e)
                          {
                              if ( e.getErrorCode().equals("BO-3015") )
                              {
                                  boObject textIndex = boObject.getBoManager().loadObject( ctx, "SELECT Ebo_TextIndex WHERE UI=?",new Object[] { new Long( itens[counter] )});
                                  if(textIndex.exists()) 
                                  {
                                      textIndex.destroy();
                                  }
                              }
                              else
                              {
                                  throw e;
                              }
                          }

                          workTime = System.currentTimeMillis()-startTime;                                                                
                          queue.markAsProcessed( cn, itens[ counter ], 1, null );
                          ctx.commitContainerTransaction();
                          counter++;

                  }
                  catch( Throwable e )
                  {
                      try
                      {
                          ctx.rollbackContainerTransaction();
                          ctx.releaseAllObjects();
                      }
                      catch (Exception ex)
                      {                      
                      }
                      if( itens != null )
                      {
                          ctx.beginContainerTransaction();
                          CharArrayWriter cw = new CharArrayWriter(  );
                          PrintWriter     pw = new PrintWriter( cw );
                          logger.severe("Error indexing object:["+itens[counter]+"]",e);
                          e.printStackTrace( pw );
                          try
                          {
                            queue.markAsProcessed( cn, itens[0],9, cw.toString() );
                            ctx.commitContainerTransaction();
                          }
                          catch (Exception ex1)
                          {
                            ctx.rollbackContainerTransaction();
                          }
                      }
                      throw e;
                  }
                  finally
                  {
                    if( itens.length <= counter )
                    {
                        if( itens != null && itens.length > 0 ) 
                        {
                            logger.finer(  "TextIndex Demorou ["+ (System.currentTimeMillis()-init) +" ms] a indexar [" + itens.length + "] itens...");
                        }
                        ctx.releaseAllObjects();
                        if ( ctx.getThread() != null ) 
                        {
                            ctx.getThread().clear();
                        }
                        init = System.currentTimeMillis();
                        counter = 0;
                        itens = queue.pop( ctx, cn, 100 );
                    }
                    ctx.close();
                    ctx.releaseAllObjects();
                    cn.close();
                    ctx=null;
                    cn=null;
                  }
              }
              if( itens != null && itens.length > 0 ) 
              {
                  logger.finer(  "TextIndex Demorou ["+ (System.currentTimeMillis()-init) +" ms] a indexar [" + itens.length + "] itens...");
              }
              // Clean objects in queue
              // queue.p_objects.clear();
                  
              // Rebuild the index                
              if ( wasupdated &&  WAIT_TIME > 0 )
              {
                  long startTime = System.currentTimeMillis( );
                  Ebo_TextIndexImpl.rebuildIndex( ctx, true );
                  workTime = System.currentTimeMillis( ) - startTime;
              }              
            }
            finally
            {
                if (ctx!=null)ctx.close();
                if (ctx!=null)ctx.releaseAllObjects();
                if (cn!=null)cn.close();
                cn = null;                
            }
//            logger.finer("Finished EboTextIndex schedule agent.....");
        }
        catch (Throwable e)
        {
            logger.severe( "Error building TextIndex \n"+e.getMessage(), e );
            e.printStackTrace();
        }
        finally
        {           
            try
            {
                if( cn != null )                
                {                    
                    cn.close();
                }
            }
            catch (Exception e)
            {
            }
            
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
        return workTime;
    }      
}
