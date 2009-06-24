/*Enconding=UTF-8*/
package netgest.bo.runtime;
import java.sql.SQLException;
import java.util.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import netgest.bo.data.DataRow;
import netgest.bo.data.DataSet;
import netgest.bo.system.*;
import netgest.utils.ParametersHandler;

/**
 *
 * @Company Enlace3
 * @author João Paulo Trindade Carreira
 * @version 1.0
 * @since
 */
public class boObjectSavePoint
{
    /**
     *
     * @since
     */

    // VARIABLES OF THE SAVED OBJECT
    private byte                    p_mode;
    private bridgeHandler           p_parentBridge;
    private boolean                 p_exists;
    private byte                    p_state;
    private boBridgesArray          p_bridges;
    private ParametersHandler       p_parameters;
    private boolean                 p_ischanged;
    private boObjectUpdateQueue     p_updatequeue;
    private DataSet                 p_dataSet;
    private long                    p_template;
    private String                  p_owner;
    private boolean                 p_isstatefull;
    private boolean                 p_poolissharable;

    // END

    // EXETENDED VARIABLED TO SAVE OBJECT STATE

    private Hashtable p_bridgesPositions  = new Hashtable();
    private Hashtable p_bridgesAttributes = new Hashtable();
  //  private Hashtable p_bridgesVl = new Hashtable();


    // END



    private boObject    p_object;             // Saved Object
    private String      p_savepointname;      // Save Point Name
    private boolean                p_hassavepoint;  // Save point was created
    private long                   p_referedboui;   // Boui of the refered object

    private boObjectSavePoint(  ) throws boRuntimeException
    {
    }
    public static boObjectSavePoint createSavePoint( String name, boObject object ) throws boRuntimeException
    {
        boObjectSavePoint savePoint = new boObjectSavePoint();
        savePoint.p_object        = object;
        savePoint.p_savepointname = name;

        try
        {
            if( object.isChanged() || !object.exists() )
            {
                savePoint.p_hassavepoint = true;
                savePoint.p_mode              = object.p_mode;             // Mode of the Object
//                savePoint.p_parentBridge      = object.p_parentBridge;
                savePoint.p_exists            = object.p_exists;             // Variable that tells if the object already exists in persistent store.
                savePoint.p_state             = object.p_state;             // Current state of the object
                savePoint.p_template          = object.p_template;                // Templates
                savePoint.p_ischanged         = object.isChanged();

                // Object to Save


                savePoint.p_isstatefull = object.poolIsStateFull();
//                if ( savePoint.p_isstatefull )
//                {
//                    savePoint.p_owner = object.poolOwner();
//                }
//                savePoint.p_poolissharable = object.poolIsSharable();

                // Create a new object with the bridges loaded
                savePoint.p_bridges = new boBridgesArray( );
                if( object.p_bridges.p_bridges != null ) {
                savePoint.p_bridges.p_bridges = new Hashtable( object.p_bridges.p_bridges );

                // Save the Row position of the bridges

                Enumeration oEnum = object.p_bridges.elements();

                while( oEnum.hasMoreElements() )
                {
                    bridgeHandler bridge = (bridgeHandler)oEnum.nextElement();
                    savePoint.p_bridgesPositions.put( bridge.getName(), new Integer( bridge.getRow() ) );
//                    Enumeration bridge_atts = bridge.p_lineatts.elements();
//                    savePoint.p_bridgesAttributes.put( bridge.getName() , bridge.p_lineatts.clone() );
                 //   savePoint.p_bridgesVl.put( bridge.getName(), new Boolean( bridge.p_vl ) );
	                }
                }


                // Copy update queue
                if( object.p_updatequeue != null )
                {
                    savePoint.p_updatequeue       = new boObjectUpdateQueue();
                    long[][] queue = object.p_updatequeue.getObjects();
                    for (short i = 0; i < queue.length ; i++)
                    {
                        savePoint.p_updatequeue.add( queue[i][0], (byte)queue[i][1] );
                    }
                }


                // Copy the actual parameters of the object
                savePoint.p_parameters        = new ParametersHandler();
                String[] pnames               = object.getParametersHandler().getParametersNames();
                for (short i = 0;pnames != null && i < pnames.length; i++)
                {
                    savePoint.p_parameters.setParameter( pnames [i] ,  object.getParameter( pnames[i] ) );
                }

                // Clone the data of the Object
                savePoint.p_dataSet           = (DataSet)object.getDataSet().clone();      // Current DataSet used
            }
            else
            {
                savePoint.p_referedboui = object.getBoui();
                savePoint.p_hassavepoint = false;
            }
            return savePoint;
        }
        catch (Exception e)
        {
            throw new boRuntimeException(object,"boObjectSavePoint","BO-3051",e);
        }
    }

    public void rollbackObject()  throws boRuntimeException
    {
        try
        {
            if( this.p_hassavepoint )
            {
                // Restor the object varibales to the previous state

                p_object.p_mode         = this.p_mode;
//                p_object.p_parentBridge = this.p_parentBridge;
                p_object.p_exists       = this.p_exists;
                p_object.p_state        = this.p_state;
                p_object.p_template     = this.p_template;
                //p_object.p_ischanged    = this.p_ischanged;
                p_object.setChanged( this.p_ischanged );
                p_object.p_bridges      = this.p_bridges;
                p_object.p_updatequeue  = this.p_updatequeue;
                p_object.p_parameters   = this.p_parameters;
                p_object.set_IsInOnSave( boObject.UPDATESTATUS_IDLE );
                long boui=p_object.getBoui();


//                if ( p_owner != null )
//                {
//                  p_object.poolSetStateFull( p_owner );
//                }

//                p_object.poolSetSharable( this.p_poolissharable );


                //
              //  if( p_isstatefull && !p_object.poolIsStateFull() && p_object.getEboContext() != null )
              //  {
              //      p_object.poolSetStateFull();
             //   }

                // Restore the data of the Object
                p_object.p_dataSet      = this.p_dataSet;
                p_object.p_bodata       = this.p_dataSet.rows( 1 );

                // Restore the bridges data and position
                Enumeration oEnum = p_object.p_bridges.elements();

                while( oEnum.hasMoreElements() )
                {
                    bridgeHandler bridge = (bridgeHandler)oEnum.nextElement();
                    Integer position = (Integer)this.p_bridgesPositions.get( bridge.getName() );
                    bridge.getRslt().absolute( position.intValue() );
                    //bridge.p_vl = ((Boolean)this.p_bridgesVl.get( bridge.getName() )).booleanValue();
                    if( bridge.getName().equalsIgnoreCase("workHistory" ) )
                    {
                        int xxx=2;
                    }

                    if( !(bridge instanceof bridgeReverseHandler) )
                    {
                        bridge.getRslt().newData( p_object.p_dataSet.rows( 1 ).getChildRows( p_object.getEboContext(), bridge.getName() ) , ((Integer)p_bridgesPositions.get( bridge.getName() )).intValue() );
                    }
                    bridge.refreshBridgeData();


//                    bridge.p_lineatts = ( Vector )p_bridgesAttributes.get( bridge.getName() );
//                    for (int i = 0; i < bridge.p_lineatts.size() ; i++)
//                    {
//                        Enumeration lineatts = (( boAttributesArray )bridge.p_lineatts.get( i )).elements();
//                        while ( lineatts.hasMoreElements()  )
//                        {
//                            ( ( boIBridgeAttribute ) lineatts.nextElement() ).setLine( i + 1 );
//                        }
//                    }
                }
            }
            else
            {
                try
                {
                    p_object.getEboContext().getApplication().getMemoryArchive().getPoolManager().destroyObject(p_object);
                }catch(Exception e){/*IGNORE*/}
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException(p_object,"boObjectSavePoint","BO-3052",e);
        }
    }
    public void commit() throws boRuntimeException
    {
        try
        {
            // Clear the update queue and restore the bridges position
            if( p_object.p_updatequeue != null )
            {
                p_object.p_updatequeue.clear();
            }
            p_object.setChanged(false);
            Enumeration oEnum = p_object.p_bridges.elements();
            while( oEnum.hasMoreElements() )
            {
                bridgeHandler bridge = (bridgeHandler)oEnum.nextElement();
                Integer position = (Integer)p_bridgesPositions.get( bridge.getName() );
                if( position != null )
                {
                    bridge.getRslt().absolute( position.intValue() );
                }
            }
        }
        catch (SQLException e)
        {
            throw new boRuntimeException2("boObjectSavePoint.commit() "+ e.getMessage());
        }

    }
}