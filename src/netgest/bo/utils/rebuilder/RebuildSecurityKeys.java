/*Enconding=UTF-8*/
package netgest.bo.utils.rebuilder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import netgest.bo.data.ObjectDataManager;
import netgest.bo.def.boDefHandler;
import netgest.bo.runtime.EboContext;
import netgest.bo.runtime.boObject;


/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class RebuildSecurityKeys extends OperationStatus
{
    /**
     * 
     * @Company Enlace3
     * @since 
     */
    private EboContext ctx; 
    
    private boDefHandler[] objectList = new boDefHandler[0];
    
    private boolean allInSameTable = false; 
    
    
    public RebuildSecurityKeys(  EboContext eboctx, String[] listOfObjects, boolean allOnTheSameTable )
    {
        ctx = eboctx;
        this.allInSameTable = allOnTheSameTable;
        if( listOfObjects != null && listOfObjects.length > 0 )
        {
            if( listOfObjects[0].equals("[ALL]") )
            {
                this.objectList = boDefHandler.listBoDefinitions();
                this.allInSameTable = false;
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
        super.logln( "Starting the rebuild Security Keys.." );
        super.logln( "------");
        
        boDefHandler[] def = this.objectList;
        
        Connection cn = null;
        PreparedStatement pstm = null;
        ResultSet rslt = null;
        ArrayList errorsList = new ArrayList();
        
        for (int j = 0; j < def.length ; j++) 
        {
            if( !(def[j].getName().equalsIgnoreCase("Ebo_Registry") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex") || def[j].getName().equalsIgnoreCase("boObject") || def[j].getName().equalsIgnoreCase("Ebo_TextIndex")) )
            {
                if( def[j].getClassType() == boDefHandler.TYPE_CLASS )
                {
                    try 
                    {
                        cn = ctx.getConnectionData();
                        
                        super.log( "Rebuilding [ "+ def[j].getName() + "]" );
                        
                        if( this.allInSameTable )
                        {
                            pstm = cn.prepareStatement( "SELECT A.BOUI FROM "+def[j].getBoMasterTable()+" A,EBO_REGISTRY B WHERE A.BOUI = B.UI$" );
                        }
                        else
                        {
                            pstm = cn.prepareStatement( "SELECT A.BOUI FROM "+def[j].getBoMasterTable()+" A,EBO_REGISTRY B WHERE A.BOUI = B.UI$ AND A.CLASSNAME=?" );
                            pstm.setString( 1, def[j].getName() );                                 
                        }
                        rslt = pstm.executeQuery();
                        
                        int objcnt = 0;
                        
                        int i = 0;
                        
                        super.log( "[ 0 ]" );

                        while( true )
                        {
                            long[] bouis = new long[200];
                            for (i = 0; i < 200 && rslt.next() ; i++) 
                            {
                                bouis[i] = rslt.getLong( 1 );
                            }
                            ctx.beginContainerTransaction();
                            boObject.getBoManager().preLoadObjects( ctx, bouis );
                            for (int z = 0; z < i; z++ ) 
                            {
                                try
                                {
                                    boObject xobj = boObject.getBoManager().loadObject( ctx, bouis[z] );
                                    xobj.computeSecurityKeys(true);
                                    ObjectDataManager.updateObjectData( xobj );
//                                    super.logReplace( "[ "+(objcnt + i)+" ]", 11 );
                                    super.log( "[ "+(objcnt + i)+" ]");    
                                }
                                catch (Exception e)
                                {
                                    //ignore
                                    errorsList.add("Error rebuilding security keys of object [" + bouis[z] + "]");
                                }
                                
                            }
                            ctx.commitContainerTransaction();
                            ctx.getApplication().getMemoryArchive().getPoolManager().realeaseAllObjects(ctx.poolUniqueId());
                            objcnt += i;
    
                            if( i < 200 )
                            {
                                break;
                            }
                        }
                        rslt.close();
                        pstm.close();                        
                        ctx.getThread().clear();
                        super.loggreenln( " [ Done    ] ");
                    } 
                    catch (Exception ex) 
                    {
                        ex.printStackTrace();
                        super.logerrln( "____" );
                        super.logerrln( " Error rebuilding Object ["+ def[j].getName() +"] " );
                        super.logerrln( ex.getMessage() );
                        super.logerrln( "____" );
                        try
                        {
                            ctx.rollbackContainerTransaction();
                        }
                        catch (Exception e)
                        {
                            super.log("");
                            super.logerrln( "____" );
                            super.logerrln( " Error rollingback transaction " );
                            super.logerrln( e.getMessage() );
                            super.logerrln( "____" );
                        }
                        
                        super.logerrln( " Continuing.... ");
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
//                        try
//                        {
//                            cn.close();
//                        }
//                        catch (Exception e)
//                        {
//                            
//                        }
                        ctx.close();
                    }
                }
            }
        }
        super.logln( "__" );
        for (int i = 0; i < errorsList.size(); i++) 
        {
            super.logerrln((String)errorsList.get(i));    
        }
        super.logln("Finished rebuild ...");
    }
}