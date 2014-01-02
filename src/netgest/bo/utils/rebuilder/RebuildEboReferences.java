/*Enconding=UTF-8*/
package netgest.bo.utils.rebuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.InitialContext;

import netgest.bo.def.boDefHandler;
import netgest.bo.localizations.MessageLocalizer;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boReferencesManager;
import netgest.bo.system.boRepository;



/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class RebuildEboReferences extends OperationStatus 
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private EboContext ctx;
    private String fullTableName = "";
    private String[] objectsToRebuild = null;
    
    public RebuildEboReferences(  EboContext eboctx, String[] objectsToRebuild )
    {
        ctx = eboctx;
        this.objectsToRebuild = objectsToRebuild;
        String aux = boRepository.getRepository(ctx.getApplication(), "default").getSchemaName();
        if(aux != null &&
           !"".equals(aux))
        {
            fullTableName = ctx.getBoSession().getRepository().getSchemaName() + "."; 
        }
        fullTableName = fullTableName + "EBO_REFERENCES"; 
    }
    public void run()
    {
        super.logln( MessageLocalizer.getMessage("STARTING_THE_REBUILD_OF_REFERENCES"));
        boDefHandler[] def = boDefHandler.listBoDefinitions();
        boolean first = true;
        
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        if(objectsToRebuild != null && objectsToRebuild.length > 0)
        {
            runForObj();
        }
        else
        {
            for (int j = 0; j < def.length ; j++) 
            {
                if( !(def[j].getName().equalsIgnoreCase("Ebo_Registry") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex") || def[j].getName().equalsIgnoreCase("boObject") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex")) )
                {
                    if( def[j].getClassType() == boDefHandler.TYPE_CLASS )
                    {
                        try 
                        {
                            if( first )
                            {  
                                InitialContext ic = new InitialContext(); 
    //                            cn = ((DataSource) ic.lookup(ctx.getSysUser()
    //                                                                 .getConnectionString() +
    //                                    "_nojta")).getConnection();
                                cn = ctx.getConnectionManager().getDedicatedConnection();
                                super.logln( "-----");
                                super.log(MessageLocalizer.getMessage("TRUNCATING_TABLE")+ " [ EBO_REFERENCES ] " );
                                pstm = cn.prepareStatement( "TRUNCATE TABLE " + fullTableName );
                                pstm.execute();
                                pstm.close();
                                super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" );
                                super.logln(MessageLocalizer.getMessage("DISABLING_COSTRAINTS")+":");
                                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " DISABLE CONSTRAINT FK_EBO_REFERENCES_BOUI" );
                                try
                                {
                                    super.log( MessageLocalizer.getMessage("DISABLING")+" [FK_EBO_REFERENCES_BOUI   ]" );
                                    pstm.execute();
                                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" );
                                }
                                catch (SQLException e)
                                {
                                    super.logln("    ");
                                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_DISABLING_CONSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                                }
                                pstm.close();
                                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName+ " DISABLE CONSTRAINT FK_EBO_REFERENCES_REFBOUI" );
                                try
                                {
                                    super.log( MessageLocalizer.getMessage("DISABLING")+"Disabling [FK_EBO_REFERENCES_REFBOUI]" );
                                    pstm.execute();
                                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"    ]" );
                                }
                                catch (SQLException e)
                                {
                                    super.logln("    ");
                                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_DISABLING_CONSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                                }
                                pstm.close();
                                super.logln( "------");
                                cn.close();
                                first = false;
                            }
                            cn = ctx.getConnectionData();
                            
                            super.log( MessageLocalizer.getMessage("REBUILDING")+" [ "+ def[j].getName() + "]" );
                            
                            pstm = cn.prepareStatement( "SELECT A.BOUI FROM "+def[j].getBoMasterTable()+" A,EBO_REGISTRY B WHERE A.BOUI = B.UI$ AND A.CLASSNAME=?" );
                            pstm.setString( 1, def[j].getName() );
                            rslt = pstm.executeQuery();
                            
                            int objcnt = 0;
                            
                            int i = 0;
                            
                            super.log( "[  ]" );
    
                            while( true )
                            {
                                ctx.beginContainerTransaction();
                                long[] bouis = new long[200];
                                for (i = 0; i < 200 && rslt.next() ; i++) 
                                {
                                    bouis[i] = rslt.getLong( 1 );
                                }
                                boObject.getBoManager().preLoadObjects( ctx, bouis );
                                for (int z = 0; z < i; z++ ) 
                                {
                                    boReferencesManager.rebuilReferences( ctx, bouis[z] );
                                   // super.logReplace( "[ "+(objcnt + i)+" ]", 11 );
                                    
                                } 
                                objcnt += i;
        
    
                                ctx.commitContainerTransaction();
                                
                                ctx.getThread().clear();
                                ctx.releaseAllObjects();
    
                                if( i < 200 )
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
                            super.log("");
                            super.logerrln( "-----" );
                            super.logerrln( MessageLocalizer.getMessage("ERROR_REBUILDING_OBJECT")+" ["+ def[j].getName() +"] " );
                            super.logerrln( ex.getMessage() );
                            super.logerrln( "-----" );
                            try
                            {
                                ctx.rollbackContainerTransaction();
                            }
                            catch (Exception e)
                            {
                                super.log("");
                                super.logerrln( "-----" );
                                super.logerrln(MessageLocalizer.getMessage("ERROR_ROLLINGBACK_TRANSACTION"));
                                super.logerrln( e.getMessage() );
                                super.logerrln( "-----" );
                            }
                            super.logln(MessageLocalizer.getMessage("CONTINUING"));
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
                                cn.close();
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
                InitialContext ic = new InitialContext(); 
    //            cn = ((DataSource) ic.lookup(ctx.getSysUser()
    //                                                 .getConnectionString() +
    //                    "_nojta")).getConnection();
                
                cn = ctx.getConnectionManager().getDedicatedConnection();
                super.logln( "----"  );
                super.logln(MessageLocalizer.getMessage("ENABLING_CONSTRAINTS")+":");
                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " ENABLE CONSTRAINT FK_EBO_REFERENCES_BOUI" );
                try
                {
                    super.log( MessageLocalizer.getMessage("ENABLING")+": [FK_EBO_REFERENCES_BOUI    ]" );
                    pstm.execute();
                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"    ]" );
                }
                catch (SQLException e)
                {
                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                }
                pstm.close();
                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " ENABLE CONSTRAINT FK_EBO_REFERENCES_REFBOUI" );
                try
                {
                    super.log( MessageLocalizer.getMessage("ENABLING")+": [FK_EBO_REFERENCES_REFBOUI ]" );
                    pstm.execute();
                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" );
                }
                catch (SQLException e)
                {
                    super.logln("    ");
                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                }
                pstm.close();
                cn.close();
                first = false;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                super.logerrln( "----" );
                super.logerrln( MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINTS") );
                super.logerrln( ex.getMessage() );
                super.logerrln(  "----" );
                super.logln(MessageLocalizer.getMessage("CONTINUING"));
                
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
                    cn.close();
                }
                catch (Exception e)
                {
                    
                }
                ctx.close();
            }
        }
        
        super.logln( "----" );
        super.logln(MessageLocalizer.getMessage("FINISHED_REBUILD"));
    }
    
    private void runForObj()
    {
        boDefHandler[] def = new boDefHandler[objectsToRebuild.length];
        for (int i = 0; i < objectsToRebuild.length; i++) 
        {
            def[i] = boDefHandler.getBoDefinition(objectsToRebuild[i]);
        }
        
        
        boolean first = true;
        
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
            for (int j = 0; j < def.length ; j++) 
            {
                if( !(def[j].getName().equalsIgnoreCase("Ebo_Registry") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex") || def[j].getName().equalsIgnoreCase("boObject") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex")) )
                {
                    if( def[j].getClassType() == boDefHandler.TYPE_CLASS )
                    {
                        try 
                        {
                              
                                InitialContext ic = new InitialContext(); 
    //                            cn = ((DataSource) ic.lookup(ctx.getSysUser()
    //                                                                 .getConnectionString() +
    //                                    "_nojta")).getConnection();
                                cn = ctx.getConnectionManager().getDedicatedConnection();
                            
                            
//                                super.logln( "-----");
//                                super.log( "deleting from table [ EBO_REFERENCES ] " );
//                                String s = "delete from TABLE " + fullTableName +" where boui in (select boui from ebo_registry where name = '"+def[j].getBoMasterTable()+"') or refboui$ in (select boui from ebo_registry where name = '"+def[j].getBoMasterTable()+"')"; 
//                                pstm = cn.prepareStatement( "delete from " + fullTableName +" where boui in (select boui from ebo_registry where name = '"+def[j].getBoMasterTable()+"') or refboui$ in (select boui from ebo_registry where name = '"+def[j].getBoMasterTable()+"')");
//                                pstm.execute();
//                                pstm.close();
//                                super.loggreenln( "[ Done    ]" );
                            
                            if( first )
                            {                                 
                                super.logln(MessageLocalizer.getMessage("DISABLING_COSTRAINTS")+":");
                                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " DISABLE CONSTRAINT FK_EBO_REFERENCES_BOUI" );
                                try
                                {
                                    super.log( MessageLocalizer.getMessage("DISABLING")+" [FK_EBO_REFERENCES_BOUI   ]" );
                                    pstm.execute();
                                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"    ]" );
                                }
                                catch (SQLException e)
                                {
                                    super.logln("    ");
                                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_DISABLING_CONSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                                }
                                pstm.close();
                                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName+ " DISABLE CONSTRAINT FK_EBO_REFERENCES_REFBOUI" );
                                try
                                {
                                    super.log( MessageLocalizer.getMessage("DISABLING")+" [FK_EBO_REFERENCES_REFBOUI]" );
                                    pstm.execute();
                                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"    ]" );
                                }
                                catch (SQLException e)
                                {
                                    super.logln("    ");
                                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_DISABLING_CONSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                                }
                                pstm.close();
                                super.logln( "------");                                
                                first = false;
                            }
                            cn.close();
                            cn = ctx.getConnectionData();
                            
                            super.log( MessageLocalizer.getMessage("REBUILDING")+" [ "+ def[j].getName() + "]" );
                            
                            pstm = cn.prepareStatement( "SELECT A.BOUI FROM "+def[j].getBoMasterTable()+" A,EBO_REGISTRY B WHERE A.BOUI = B.UI$ AND A.CLASSNAME=?" );
                            pstm.setString( 1, def[j].getName() );
                            rslt = pstm.executeQuery();
                            
                            int objcnt = 0;
                            
                            int i = 0;
                            
                            super.log( "[  ]" );
    
                            while( true )
                            {
                                ctx.beginContainerTransaction();
                                long[] bouis = new long[200];
                                for (i = 0; i < 200 && rslt.next() ; i++) 
                                {
                                    bouis[i] = rslt.getLong( 1 );
                                }
                                boObject.getBoManager().preLoadObjects( ctx, bouis );
                                for (int z = 0; z < i; z++ ) 
                                {
                                    boReferencesManager.rebuilReferences( ctx, bouis[z] );
                                   // super.logReplace( "[ "+(objcnt + i)+" ]", 11 );
                                    
                                } 
                                objcnt += i;
        
    
                                ctx.commitContainerTransaction();
                                
                                ctx.getThread().clear();
                                ctx.releaseAllObjects();
    
                                if( i < 200 )
                                {
                                    break;
                                }
                            }
                            rslt.close();
                            pstm.close();
                            super.loggreenln( " [ "+MessageLocalizer.getMessage("DONE")+"   ] ");
                        } 
                        catch (Exception ex) 
                        {
                            ex.printStackTrace();
                            super.log("");
                            super.logerrln( "-----" );
                            super.logerrln(  MessageLocalizer.getMessage("ERROR_REBUILDING_OBJECT")+" ["+ def[j].getName() +"] " );
                            super.logerrln( ex.getMessage() );
                            super.logerrln( "-----" );
                            try
                            {
                                ctx.rollbackContainerTransaction();
                            }
                            catch (Exception e)
                            {
                                super.log("");
                                super.logerrln( "-----" );
                                super.logerrln( MessageLocalizer.getMessage("ERROR_ROLLINGBACK_TRANSACTION") );
                                super.logerrln( e.getMessage() );
                                super.logerrln( "-----" );
                            }
                            super.logln( MessageLocalizer.getMessage("CONTINUING"));
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
                                cn.close();
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
                InitialContext ic = new InitialContext(); 
    //            cn = ((DataSource) ic.lookup(ctx.getSysUser()
    //                                                 .getConnectionString() +
    //                    "_nojta")).getConnection();
                
                cn = ctx.getConnectionManager().getDedicatedConnection();
                super.logln( "----"  );
                super.logln(MessageLocalizer.getMessage("ENABLING_CONSTRAINTS")+":");
                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " ENABLE CONSTRAINT FK_EBO_REFERENCES_BOUI" );
                try
                {
                    super.log( MessageLocalizer.getMessage("ENABLING")+": [FK_EBO_REFERENCES_BOUI    ]" );
                    pstm.execute();
                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"    ]" );
                }
                catch (SQLException e)
                {
                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                }
                pstm.close();
                pstm = cn.prepareStatement( "ALTER TABLE " + fullTableName + " ENABLE CONSTRAINT FK_EBO_REFERENCES_REFBOUI" );
                try
                {
                    super.log( MessageLocalizer.getMessage("ENABLING")+": [FK_EBO_REFERENCES_REFBOUI ]" );
                    pstm.execute();
                    super.loggreenln( "[ "+MessageLocalizer.getMessage("DONE")+"   ]" );
                }
                catch (SQLException e)
                {
                    super.logln("    ");
                    super.logerrln("[ "+MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINT")+" [ "+e.getMessage()+" ] ]");   
                }
                pstm.close();
                cn.close();
                first = false;
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
                super.logerrln( "----" );
                super.logerrln( MessageLocalizer.getMessage("ERROR_ENABLING_CNSTRAINTS") );
                super.logerrln( ex.getMessage() );
                super.logerrln(  "----" );
                super.logln( MessageLocalizer.getMessage("CONTINUING"));
                
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
                    cn.close();
                }
                catch (Exception e)
                {
                    
                }
                ctx.close();
            }
    }
}