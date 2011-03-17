/*Enconding=UTF-8*/
package netgest.bo.utils.rebuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;

import javax.naming.InitialContext;

import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.robots.boTextIndexAgent;
import netgest.bo.runtime.robots.blogic.boTextIndexAgentBussinessLogic;



/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class RebuildTextIndex extends OperationStatus
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private EboContext ctx; 
    
    private boDefHandler[] objectList = new boDefHandler[0];
    
    private boolean allObjects = false;
    
    
    public RebuildTextIndex(  EboContext eboctx, String[] listOfObjects )
    {
        ctx = eboctx;
        if( listOfObjects != null && listOfObjects.length > 0 )
        {
            if( listOfObjects[0].equals("[ALL]") )
            {
                this.objectList = boDefHandler.listBoDefinitions();
                this.allObjects = true;
            }
            else
            {
                this.objectList = new boDefHandler[ listOfObjects.length ];
                for (int i = 0; i < listOfObjects.length; i++) 
                {
                    this.objectList[i] = boDefHandler.getBoDefinition( listOfObjects[i] );                         
                }
            }
        }

    }
    public void run()
    {
        super.logln( MessageLocalizer.getMessage("STARTING_REBUILD_OF_EBO_TEXTINDEX") ); 
        super.logln( "----" );
        
        boDefHandler[] def = this.objectList;
        
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        boolean abort = false;

        //Suspend only boTextIndexAgent (NOK)
        ctx.getApplication().suspendAgents();

        try
        {
            String ebo_textindexFullTableName = "";
            String sys_im_ebo_textindexFullTableName = "";
            if(ctx.getBoSession().getRepository().getSchemaName() != null &&
                !"".equals(ctx.getBoSession().getRepository().getSchemaName()))
            {
                ebo_textindexFullTableName = ctx.getBoSession().getRepository().getSchemaName() + "." ;
                sys_im_ebo_textindexFullTableName  = ctx.getBoSession().getRepository().getSchemaName() + "." ;
            }
            ebo_textindexFullTableName +="EBO_TEXTINDEX";
            sys_im_ebo_textindexFullTableName  += "SYS_IM_EBO_TEXTINDEX";
        
            if( this.allObjects )
            {
                
            
                try 
                {
                    InitialContext ic = new InitialContext(); 
                    
//                    cn = ((DataSource) ic.lookup(ctx.getSysUser()
//                                                         .getConnectionString() +
//                            "_nojta")).getConnection();
                    cn = ctx.getDedicatedConnectionData();                    
                    super.log( MessageLocalizer.getMessage("TRUNCATING_TABLE")+" [ EBO_TEXTINDEX ] " );
                    pstm = cn.prepareStatement( "TRUNCATE TABLE " + ebo_textindexFullTableName );
                    pstm.execute();
                    
                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" );
    
                    super.logln(  "----");
                }
                catch( Exception ex )
                {
                    ex.printStackTrace();
                    super.logerrln(  "----" );
                    super.logerrln( MessageLocalizer.getMessage("ERROR_TRUNCATING_TABLE")+" [ EBO_TEXTINDEX ] " );
                    super.logerrln( ex.getMessage() );
                    super.logerrln(  "----" );
                    super.logerrln( MessageLocalizer.getMessage("OPERATION_ABORTED"));
                    abort = true;
                }
                finally
                {
                    try
                    {
                        if( pstm != null ) pstm.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                    try
                    {
                        if( cn != null ) cn.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }
            
            
            
            for (int j = 0;!abort && j < def.length ; j++) 
            {
                if( !(def[j].getName().equalsIgnoreCase("Ebo_Registry") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex") || def[j].getName().equalsIgnoreCase("boObject") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex")) )
                {
                    if( def[j].getClassType() == boDefHandler.TYPE_CLASS )
                    {
                        try 
                        {
                            ctx.beginContainerTransaction();
                            cn = ctx.getConnectionData();
                            
                            super.log( MessageLocalizer.getMessage("REBUILDING")+" [ "+def[j].getName() + "]" );
                            
                            if( !this.allObjects )
                            {
                                pstm = cn.prepareStatement(" DELETE FROM " + ebo_textindexFullTableName  + " WHERE UICLASS=?");
                                pstm.setString( 1 , def[j].getName() );
                                int nrows = pstm.executeUpdate();
                                pstm.close();
                                super.log( "[ "+nrows+" ] "+MessageLocalizer.getMessage("DELETED") );
                            }
                            ctx.commitContainerTransaction();
                            if(cn.isClosed())
                            {
                            //    cn = ctx.getConnectionData();                            
                            }
                            ctx.close();
                            cn = ctx.getConnectionData();                             
                            pstm = cn.prepareStatement( "SELECT A.BOUI FROM "+def[j].getBoMasterTable()+" A, OEBO_REGISTRY B WHERE A.BOUI = B.UI$ AND A.CLASSNAME=?" );
                            pstm.setString( 1, def[j].getName() );                                 
                            rslt = pstm.executeQuery();
    
                            
                            int objcnt = 0;
                            
                            int i = 0;
                            
                            super.log( "[ ]" );
    
    
                            ArrayList bouis = new ArrayList(); 
                            while( true )
                            {
                                bouis.clear();
                                for (i = 0; i < 200 && rslt.next() ; i++) 
                                {
                                    bouis.add( new Long( rslt.getLong( 1 ) ) );
                                }
                                
                                boTextIndexAgentBussinessLogic.addToQueue( ctx, bouis, def[j].getName() );
                                while( boTextIndexAgentBussinessLogic.queue.getQueueSize( ctx ) > 0 )
                                {                                    
                                    boTextIndexAgentBussinessLogic logic = new boTextIndexAgentBussinessLogic(ctx.getApplication());
                                    logic.execute();
                                    //super.log( "[ "+( objcnt + bouis.size() - boTextIndexAgentBussinessLogic.queue.getQueueSize( ctx ) )+" ]" );
                                }
                                objcnt += bouis.size();
                                super.log( "[ "+objcnt +" ]");
                                
                                ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects( ctx.poolUniqueId() );
                                ctx.getThread().clear();
                                
                                if( bouis.size() < 200 )
                                {
                                    break;
                                }
                            }
                            rslt.close();
                            pstm.close();
                            super.loggreenln( " [ "+MessageLocalizer.getMessage("DONE")+"    ] ");
                        } 
                        catch (Exception ex) 
                        {
                            ex.printStackTrace();
                            super.logerrln(  "----" );
                            super.logerrln( MessageLocalizer.getMessage("ERROR_REBUILDING_OBJECT")+" ["+ def[j].getName() +"] " );
                            super.logerrln( ex.getMessage() );
                            super.logerrln( "----" );
                            super.logerrln( MessageLocalizer.getMessage("CONTINUING"));
                        } 
                        finally 
                        {
                            try
                            {
                                rslt.close();
                            }
                            catch (Exception e)
                            {
                                
                            }
                            try
                            {
                                pstm.close();
                            }
                            catch (Exception e)
                            {
                                
                            }
                            try
                            {
                                if(!cn.isClosed())
                                {
                                    cn.close();
                                }
                            }
                            catch (Exception e)
                            {
                                
                            }
                            ctx.close();
                        }
                    }
                }
            }

            try 
            {
                //InitialContext ic = new InitialContext(); 
                
//                cn = ((DataSource) ic.lookup(ctx.getSysUser()
//                                                     .getConnectionString() +
//                        "_nojta")).getConnection();
                //cn = ctx.getDedicatedConnectionData();
                //super.log( "FAÇA o rebuild [ SYS_IM_EBO_TEXTINDEX ] " );
              //  pstm = cn.prepareStatement( "ALTER INDEX " + sys_im_ebo_textindexFullTableName + " REBUILD" );
              //  pstm.execute();
                
                super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" ); 

                super.logln( "----" );
            }
            catch( Exception ex )
            {
                ex.printStackTrace();
                super.logerrln( "----" );
                super.logerrln( MessageLocalizer.getMessage("ERROR_REBUILDING_INDEX")+" [ SYS_IM_EBO_TEXTINDEX ] " );
                super.logerrln( ex.getMessage() );
                super.logerrln(  "----"  );
                abort = true;
            }
            finally
            {
                try
                {
                    if( pstm != null ) pstm.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                try
                {
                    if( cn != null ) cn.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        finally
        {
            ctx.getApplication().startAgents();
        }
        super.logln(  "----" );
        super.logln(MessageLocalizer.getMessage("FINISHED_REBUILD"));
    }
}