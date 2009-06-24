/*Enconding=UTF-8*/
package netgest.bo.workflow;

import netgest.bo.def.*;

import netgest.bo.impl.*;

import netgest.bo.runtime.*;

import netgest.utils.*;



import java.math.BigDecimal;

import java.sql.*;

import java.util.*;
import java.util.Vector;


/**
 *
 * @author JMF
 * @version
 * @see
 */
public class WFClientMethods
{
    /**
     *
     * @see
     */
     private static boolean onEvent = false; //deve acabar?
    public WFClientMethods()
    {
    }

    public static void removeFromProtocol(BridgeObjAttributeHandler att)
        throws boRuntimeException
    {
        String selectedLines = att.getParent().getEboContext().getRequest().getParameter("selectedLines");

        if ((selectedLines != null) && (selectedLines.length() > 0))
        {
            String[] lines = selectedLines.split(";");
            bridgeHandler bridge = att.getBridge();
            boObject p = bridge.getParent();
            bridge.getParent().setParameter("DESANEXAR", "yes");

            try
            {
                int processed = 0;

                for (int i = 0; i < lines.length; i++)
                {
                    if (lines[i].length() > 0)
                    {
                        int line = Integer.parseInt(lines[i]);
                        bridge.moveTo(line - processed);
                        bridge.remove();
                        processed++;
                    }
                }
            }
            finally
            {
                bridge.getParent().removeParameter("DESANEXAR");
            }
        }
    }

    public static void aceitarProtocolo(BridgeObjAttributeHandler attr)
        throws boRuntimeException
    {
        boObject toSave = null;
        bridgeHandler bridge = attr.getBridge();
        toSave = bridge.getParent();

        boolean ok = false;
        bridge.beforeFirst();

        while (bridge.next())
        {
            if (bridge.getAttribute("DAO_assignedQueue").getValueObject() != null)
            {
                boObject object = bridge.getObject();

                if (!object.getName().equalsIgnoreCase("Ebo_Template"))
                {
                    long xqueue = bridge.getAttribute("DAO_assignedQueue").getValueLong();
                    boObject perf = attr.getParent().getObject(
                            object.getEboContext().getBoSession().getPerformerBoui());
                    boolean toadd = false;

                    if (perf.getBoui() == xqueue)
                    {
                        toadd = true;
                    }
                    else if (
                        perf.getBridge("queues").haveBoui(xqueue) ||
                            perf.getBridge("groups").haveBoui(xqueue) ||
                            perf.getBridge("roles").haveBoui(xqueue))
                    {
                        toadd = true;
                    }

                    if (toadd && (object.getAttribute("performer").getValueObject() == null))
                    {
                        //(( actionObject ) object).takeOwner();
                        WF.activity_Take( object );
                        
                        ok = true;

                        // object.poolSetStateFull();
                        toSave.getUpdateQueue().add(object, boObjectUpdateQueue.MODE_SAVE);

                        String pstate = object.getStateAttribute("primaryState").getValueString();

                        if (pstate.equals("create"))
                        {
                            //object.getStateAttributes().get("primaryState").setValueString("open");
                            //object.getStateAttributes().get("createdStatus").setValueString("started");
                            //object.getStateAttributes().get("openStatus").setValueString("waitFor");
                            //object.getStateAttributes().get("openAlerts").setValueString("Green");
                        }
                    }
                }
            }
        }

      
    }
    
    
    
      /**
         * setSequenceDependence
         *
         * Metodo da bridge DAO.
         *
         * É chamado para colocar todas as actividades em dependencia linear
         *  2 depende da 1
         *  3 depende da 2
         *  4 depende da 3
         *  etc...
         *
         *
         * @param attr
         * @throws boRuntimeException
         */
    public synchronized static void setSequenceDependence(BridgeObjAttributeHandler attr)
        throws boRuntimeException
    {
        try
        {
            bridgeHandler bridge = attr.getBridge();
            bridge.beforeFirst();

            int i = 0;

            while (bridge.next() )
            {
                BridgeObjAttributeHandler oc = (BridgeObjAttributeHandler) bridge.getAttribute("config");

                try
                {
                    bridge.getAttribute("code").setValueString("" + bridge.getRslt().getRow());
                }
                catch (SQLException e)
                {
                }

                if (i == 0)
                {
                    boObject oConf = oc.getObject();

                    if ((oc != null) && (oc.getValue() != null))
                    {
                        bridgeHandler depends = oConf.getBridge("linedepends");
                        depends.beforeFirst();

                        while (depends.next() )
                        {
                            depends.remove();
                        }

                        bridge.getAttribute("config").setValueObject(null);
                    }
                }
                else
                {
                    if ((oc != null) && (oc.getValue() != null))
                    {
                        boObject oConf = oc.getObject();
                        bridgeHandler depends = oConf.getBridge("linedepends");

                        //   depends.addNewObject();
                        depends.beforeFirst();

//                        if (depends.haveVL())
//                        {
//                            depends.remove();
//                        }

                        while (depends.next() )
                        {
                            String xdaocode = depends.getObject().getAttribute("DAO_code").getValueString();

                            if (ClassUtils.convertToInt(xdaocode) != i)
                            {
                                depends.remove();
                            }
                        }

                        if (depends.getRowCount() == 0)
                        {
                            onEvent = true;

                            boObject objc = depends.addNewObject("Ebo_DAOdepends");
                            objc.getAttribute("DAO_code").setValueString("" + (i));
                            objc.getAttribute("type").setValueString("FS");
                            objc.getAttribute("lag").setValueString(null);
                            ;
                            onEvent = false;
                        }
                    }
                    else
                    {
                        onEvent = true;

                        boObject oConf = attr.getParent().getBoManager().createObject( attr.getEboContext() , "Ebo_DAOConfig"  ); // boObject oConf = oc.getObject();
                        
                        bridgeHandler depends = oConf.getBridge("linedepends");
                        //boObject objc = depends.addNewObject();
                        boObject objc = attr.getParent().getBoManager().createObject( attr.getEboContext() , "Ebo_DAOdepends"  ); // boObject oConf = oc.getObject();
                        
                        objc.getAttribute("DAO_code").setValueString("" + (i));
                        objc.getAttribute("type").setValueString("FS");
                        objc.getAttribute("lag").setValueString(null);
                    
                        bridge.getAttribute("config").setValueLong( oConf.getBoui()  );
                        
                        oConf.setParentBridgeRow( bridge.getRow( bridge.getObject().getBoui() ) );
                        boBridgeRow pp= oConf.getParentBridgeRow();
                        depends.add( objc.getBoui() );
                        
                        onEvent = false;
                    }
                }

                i++;
            }
        }
        finally
        {
            onEvent = false;
        }
    }

}
