/*Enconding=UTF-8*/
package netgest.bo.runtime.robots;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import netgest.bo.data.DriverUtils;
import netgest.bo.def.*;
import netgest.bo.runtime.*;

/**
 * 
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since 
 */
public class boTextIndexQueue 
{
    /**
     * 
     * @since 
     */
    //Hashtable  p_objects = new Hashtable();
    boolean    p_rebuild = false;
    
    public boTextIndexQueue()
    {
    }
    
    public void clear()
    {
        //p_queue.clear();
        //p_objects.clear();
    }

    public void addItem( boObject object ) throws boRuntimeException
    {
        if ( (
                 (object.getBoDefinition().isTextIndexActive() && object.getBoDefinition().getBoCanBeOrphan()) 
                 ||
                 (object.getBoDefinition().isTextIndexActive() )
              )  
             )
        {

            Long xboui = new Long( object.getBoui());

            //p_queue.add( xboui );
            addToQueue( object.getEboContext(), xboui.longValue() );
            
            //p_objects.put( xboui, object );
            p_rebuild = true;
        }
    }
    
    public void addItem( EboContext ctx, long boui ) 
    {
        addToQueue( ctx, boui );
        p_rebuild = true;
    }
    public void addItens( EboContext ctx,ArrayList bouis, String className ) 
    {
        boDefHandler bodef = boDefHandler.getBoDefinition(className);
        if(bodef != null)
        {
            if ( !isTextIndex(bodef) && ((bodef.isTextIndexActive() && bodef.getBoCanBeOrphan()) 
                 ||
                 (bodef.isTextIndexActive() ))
               )
            {
                addToQueue( ctx, bouis );
                p_rebuild = true;
            }
        }
    }

    public void addItens( boObject[] objects) throws boRuntimeException
    {
        ArrayList list = new ArrayList();
        for (int i = 0; i < objects.length ; i++) 
        {
        	if( objects [i] != null ) {
	           if ( ( !isTextIndex(objects[i].getBoDefinition()) &&
	                 ((objects[i].getBoDefinition().isTextIndexActive() && objects[i].getBoDefinition().getBoCanBeOrphan()) 
	                 ||
	                     (objects[i].getBoDefinition().isTextIndexActive() )
	                    ) 
	              ))
	           {
	              
	              Long xboui = new Long( objects[i].getBoui());
	              
	              list.add( xboui );
	              //p_objects.put( xboui, objects[i] );
	
	              p_rebuild = true;
	              
	           }
        	}
        }
        
        if( list.size() > 0 )
        {
            addToQueue( objects[0].getEboContext(), list );
        }
        
    }

    public synchronized long[] pop( EboContext ctx, Connection cn, int nrItens )
    {
    
        long[] ret = null;

        PreparedStatement   pstm_qboui = null;
        
        PreparedStatement   pstm = null;
        ResultSet           rslt = null;
        
        ArrayList bouis = new ArrayList();
        
        try 
        {
            DriverUtils dutl = ctx.getDataBaseDriver().getDriverUtils();
            String limitOnSelect = "";
            String limitOnWhere = "";
            String limitOnEnd = "";
            switch( dutl.getQueryLimitStatementPosition() ) {
            	case DriverUtils.QUERY_LIMIT_ON_END_OF_STATEMENT:
            		limitOnEnd = dutl.getQueryLimitStatement( 120 );
            		break;
            	case DriverUtils.QUERY_LIMIT_ON_SELECT_CLAUSE:
            		limitOnSelect = dutl.getQueryLimitStatement( 120 );
            		break;
            	case DriverUtils.QUERY_LIMIT_ON_WHERE_CLAUSE:
            		limitOnWhere = " AND " + dutl.getQueryLimitStatement( 120 );
            		break;
            }
            String sql = "select " + limitOnSelect + " distinct boui,(select min(ENQUEUETIME) from ebo_textindex_queue q where q1.boui=q.boui )" + 
            			 "	from ebo_textindex_queue q1 where state = 0  " + limitOnWhere + " order by 2 " + limitOnEnd ;
            pstm        = cn.prepareStatement( sql );
            rslt        = pstm.executeQuery();
            while( rslt.next() )
            {
                long boui = rslt.getLong( 1 );
//                try
//                {
                    /*
                    pstm_qboui.setLong( 1, boui );
                    rslt_qboui = pstm_qboui.executeQuery();
                    
                    // Fetch results
                    while(rslt_qboui.next()){};
                    
                    rslt_qboui.close();
                    */
                    BigDecimal toAdd = BigDecimal.valueOf( boui );
                    if( bouis.indexOf( toAdd ) == -1 )
                    {
                        bouis.add( toAdd );
                    }
                    if( bouis.size() >= nrItens )
                    {
                        break;
                    }
/*                }
                catch(SQLException e)
                {
                    int errorCode = e.getErrorCode();
                    if( e.getErrorCode() != 54 )
                    {
                        throw new RuntimeException(e);
                    }
                    if( rslt_qboui != null )
                    {
                        rslt_qboui.close();
                    }
                }*/
            }
        } 
        catch(SQLException e)
        {
            int errorCode = e.getErrorCode();
            if( errorCode != 54 )
            {
                throw new RuntimeException(e);
            }
            
        }
        catch (Exception ex) 
        {
            throw new RuntimeException(ex);
        } 
        finally 
        {
            try
            {
                                
                if( pstm_qboui != null ) pstm_qboui.close();
                
                if( rslt != null ) rslt.close();
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }

        if( bouis.size() > 0 )
        {
            BigDecimal[] bbouis = (BigDecimal[])bouis.toArray( new BigDecimal[ bouis.size() ] );
            
            ret   = new long[ bbouis.length ];
            for (int i = 0; i < bbouis.length; i++) 
            { 
                ret[i] = bbouis[i].longValue();
            }
        }
        
        return ret;
    }
    public synchronized boolean rebuildIndex() 
    {
        if( p_rebuild )
        {
            p_rebuild = false;
            return true;
        }
        return false;
    }
    
    public final int getQueueSize( EboContext ctx )
    {
        PreparedStatement pstm = null;
        ResultSet rslt         = null;
        try 
        {
            pstm = ctx.getConnectionData().prepareStatement( SQL_COUNT );
            rslt = pstm.executeQuery();
            rslt.next();
            return rslt.getInt(1);
        } 
        catch (Exception ex) 
        {
            throw new RuntimeException(ex);
        } 
        finally 
        {
            try
            {
                if( rslt != null ) rslt.close();
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    private static final String SQL_INSERT   = "insert into ebo_textindex_queue (ENQUEUETIME,BOUI,STATE) values (?,?,0)";

    private static final String SQL_MARK     = "update ebo_textindex_queue set state=?,message=? where boui=? and state = 0";
    private static final String SQL_DELETE   = "delete from ebo_textindex_queue where boui=?";

    private static final String SQL_COUNT    = "select count(*) from ebo_textindex_queue where state=0";
    
    public void addToQueue( EboContext ctx, long boui )
    {
        PreparedStatement pstm = null;
        try 
        {
        	if (boui!=0)
        	{
	            pstm = ctx.getConnectionData().prepareStatement( SQL_INSERT );
	            pstm.setTimestamp( 1, new java.sql.Timestamp( System.currentTimeMillis() ) );
	            pstm.setLong( 2, boui );
	            pstm.executeUpdate();
        	}
        } 
        catch (Exception ex) 
        {
            throw new RuntimeException(ex);
        } 
        finally 
        {
            try
            {
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }

    public void addToQueue( EboContext ctx, ArrayList bouis )
    {
        PreparedStatement pstm = null;
        try 
        {
            pstm = ctx.getConnectionData().prepareStatement( SQL_INSERT );
            for (int i = 0; i < bouis.size(); i++) 
            {
            	if (((Long)bouis.get( i )).longValue()!=0) //Safety apenas
            	{
	                pstm.setTimestamp( 1, new java.sql.Timestamp( System.currentTimeMillis() ) );
	                pstm.setLong( 2, ((Long)bouis.get( i )).longValue() );
	                pstm.addBatch();
            	}
            }
            pstm.executeBatch();
            
        } 
        catch (Exception ex) 
        {
            throw new RuntimeException(ex);
        } 
        finally 
        {
            try
            {
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
        
        
    }
    
    
    public void markAsProcessed( Connection cn, long boui,int state, String message )
    {
        PreparedStatement pstm = null;
        try 
        {
            if( state != 1 )
            {
                pstm = cn.prepareStatement( SQL_MARK );
                pstm.setLong( 1, state);
                pstm.setString( 2, message );
                pstm.setLong( 3, boui );
            }
            else
            {
                pstm = cn.prepareStatement( SQL_DELETE );
                pstm.setLong( 1, boui );
            }
            pstm.executeUpdate();
        }  
        catch (Exception ex) 
        {
            throw new RuntimeException(ex);
        } 
        finally 
        {
            try
            {
                if( pstm != null ) pstm.close();
            }
            catch (Exception e)
            {
                
            }
        }
    }
    
    private boolean isTextIndex(boDefHandler bodef)
    {
      boolean toRet=false;
      if (bodef!=null)
      {
        if (bodef.getName().equals("Ebo_TextIndex") || (bodef.getBoExtendsClass()!=null &&
        bodef.getBoExtendsClass().equals("Ebo_TextIndex")))
          toRet=true;
      }
      
      return toRet;
    }
}