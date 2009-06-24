/*Enconding=UTF-8*/
package netgest.bo.workflow;

import java.math.BigDecimal;
import java.sql.SQLException;
import netgest.bo.impl.Ebo_TemplateImpl;
import netgest.bo.runtime.BridgeObjAttributeHandler;
import netgest.bo.runtime.ObjAttHandler;
import netgest.bo.runtime.boBridgeIterator;
import netgest.bo.runtime.boBridgeMasterAttribute;
import netgest.bo.runtime.boEvent;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectUpdateQueue;
import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.bridgeHandler;
import netgest.utils.ClassUtils;


/**
 *
 * Classe destinada a gerir os eventos do WorkFlow gerados pelo cliente
 *
 * @author JMF
 * @version
 * @see
 */
public class WFClientEvents
{
    /**
     *
     * @see
     */
    public WFClientEvents()
    {
    }

    /**
    * Chamado no onAfterChange do WorkFlow
    * É chamado quando é removida ou adicionada uma nova Activity
    *
    * @param attr
    * @param event
    * @see
    */
    public static final void afterChangeWorkFlow(BridgeObjAttributeHandler attr, boEvent event)
        throws boRuntimeException
    {
        if (attr != null)
        {
            String xtype = event.getName();
            BigDecimal xAnt = ( BigDecimal ) event.getPreviousValue();
            BigDecimal xNew = ( BigDecimal ) event.getNewValue();

            boObject objParent = attr.getParent();
            String xname = objParent.getName();
            boObject objAnt = null;
            boObject objNew = null;
            bridgeHandler bridge = attr.getBridge();

            if ((xAnt != null) && (xNew == null))
            {
                //removed
                // passou para o beforeRemove
                //                objAnt = objParent.getBoManager().loadObject(objParent.getEboContext(), xAnt.longValue());
                //
                //                int removedCode = ClassUtils.convertToInt(bridge.getAttribute("code").getValueString());
                //
                //                //                String[] dependeCodesFromObjectRemove = new String[0];
                //                //                BridgeObjAttributeHandler xobj = ( BridgeObjAttributeHandler ) bridge.getAttribute("config");
                //                //                if ((xobj != null) && (xobj.getValue() != null))
                //                //                {
                //                //                    boObject objConfig = xobj.getObject();
                //                //
                //                //                    bridgeHandler xRemoveLineDepends = objConfig.getBridge("linedepends");
                //                //                    dependeCodesFromObjectRemove = new String[xRemoveLineDepends.getRowCount()];
                //                //                    xRemoveLineDepends.beforeFirst();
                //                //
                //                //                    int i = 0;
                //                //
                //                //                    while (xRemoveLineDepends.next())
                //                //                    {
                //                //                        dependeCodesFromObjectRemove[i++] = xRemoveLineDepends.getObject()
                //                //                                                                              .getAttribute("DAO_code")
                //                //                                                                              .getValueString();
                //                //                    }
                //                //                }
                //                //                int rec = bridge.getRow();
                //                //                int removedCode = ClassUtils.convertToInt(xCodeRemove);
                //                //bridge.beforeFirst();
                //                boBridgeIterator it = bridge.iterator();
                //
                //                while (it.next())
                //                {
                //                    //                    BridgeObjAttributeHandler oc = ( BridgeObjAttributeHandler ) bridge.getAttribute(
                //                    //                            "config");
                //                    boObject config = it.currentRow().getAttribute("config").getObject();
                //
                //                    if (config != null)
                //                    {
                //                        //                        boObject oConf = oc.getObject();
                //                        bridgeHandler depends = config.getBridge("linedepends");
                //
                //                        //                        oConf.setParentBridgeRow(bridge.rows(bridge.getRow()));
                //                        depends.beforeFirst();
                //
                //                        //                        boBridgeIterator itdep = depends.iterator();
                //                        //
                //                        while (depends.next())
                //                        {
                //                            boObject dependencia = depends.getObject();
                //
                //                            int code1 = ClassUtils.convertToInt(
                //                                    dependencia.getAttribute("DAO_code").getValueString());
                //
                //                            if ((code1 >= removedCode) && ((code1 - 1) > 0))
                //                            {
                //                                dependencia.getAttribute("DAO_code").setValueString("" + (code1 - 1));
                //                            }
                //                            else if (((code1 - 1) == 0) && (code1 >= removedCode))
                //                            {
                //                                depends.remove();
                //                            }
                //                        }
                //
                //                        if (depends.getRowCount() == 0)
                //                        {
                //                            bridge.getAttribute("config").setValueObject(null);
                //                        }
                //                    }
                //
                //                    int xcode = ClassUtils.convertToInt(bridge.getAttribute("code").getValueString());
                //
                //                    if (xcode > removedCode)
                //                    {
                //                        bridge.getAttribute("code").setValueString("" + (xcode - 1));
                //                    }
                //                }
                //
                //                //                bridge.moveTo(rec);
            }
            else if (xAnt == null)
            {
                //new
                // Check if the activities have performer and have PARENT 
                try
                {
                    bridge.getAttribute("code").setValueString("" + bridge.getRslt().getRow());
                }
                catch (SQLException e)
                {
                }

                if (!bridge.getObject().getName().equals("Ebo_Template"))
                {
                    if (bridge.getAttribute("DAO_assignedQueue").getValueObject() == null)
                    {
                        if (bridge.getObject().getAttribute("assignedQueue").getValueObject() != null)
                        {
                            bridge.getAttribute("DAO_assignedQueue").setValueObject(
                                bridge.getObject().getAttribute("assignedQueue").getValueObject());
                        }
                        else
                        {
                            bridge.getAttribute("DAO_assignedQueue").setValueLong(
                                bridge.getEboContext().getBoSession().getPerformerBoui());
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * Chamado depois de se alterar a ordem das linhas no Workflow
     * onAfterChangeOrder.DAO
     * Função : Reorganizar "inteligentemente " as depêndencias
     *
     * @param attr
     * @param event
     * @throws boRuntimeException
     */
    public static final void afterChangeWorkFlowSequence(BridgeObjAttributeHandler attr, boEvent event)
        throws boRuntimeException
    {
        if (attr != null)
        {
            String xtype = event.getName();

            Integer xAntRow = ( Integer ) event.getPreviousValue();
            Integer xNewRow = ( Integer ) event.getNewValue();

            boObject objParent = attr.getParent();
            if (!objParent.poolIsStateFull())
            {
                objParent.poolSetStateFull();
            }

            String xname = objParent.getName();
            boObject objAnt = null;
            boObject objNew = null;
            bridgeHandler bridge = attr.getBridge();

            if ((xAntRow != null) && (xNewRow != null))
            // passou da posiçao xAntRow para a xNewRow
            {
                boBridgeIterator it = bridge.iterator();
                boObject[] configs = new boObject[bridge.getRowCount()];
                long[] bridgebouis = new long[bridge.getRowCount()];
                String[] extendbouis = new String[bridge.getRowCount()];

                while (it.next())
                {
                    int pos = ClassUtils.convertToInt(
                            it.currentRow().getAttribute("code").getValueString(), -1);

                    if (pos != -1)
                    {
                        configs[pos - 1] = it.currentRow().getAttribute("config").getObject();
                    }
                    bridgebouis[ pos-1 ] = it.currentRow().getObject().getBoui();
                    if( configs[ pos-1 ] != null )
                    {
                       extendbouis[pos-1] = ((boBridgeMasterAttribute) configs[ pos-1].getAttribute("extAtt_parameter")).getValueString();
                    }
                    else
                    {
                       extendbouis[pos-1]=""; 
                    }

                    it.currentRow().getAttribute("code").setValueString("" + it.getRow());
                }

                it.beforeFirst();

                int pos = 0;

                while (it.next())
                {
                    long bouiObject = it.currentRow().getObject().getBoui();
                    int iDx=0;
                    for ( ; iDx < bridgebouis.length ; iDx++) 
                    {
                        if ( bouiObject== bridgebouis[iDx] )
                        {
                            break;
                        }
                    }
                    
                    
                    if (configs[pos] != null)
                    {
                        it.currentRow().getAttribute("config").setValueLong(configs[pos].getBoui() );
                        ((boBridgeMasterAttribute) it.currentRow().getAttribute("config").getObject().getAttribute("extAtt_parameter")).setValueString(extendbouis[iDx] ); ;
                    }
                    else
                    {
                       if ( extendbouis[iDx].length()==0 )
                       {
                          it.currentRow().getAttribute("config").setValueObject(null);
                       }
                       else
                       {
                          boObject configNovo = boObject.getBoManager().createObject( attr.getEboContext(),"Ebo_DAOConfig" );
                          ((boBridgeMasterAttribute) configNovo.getAttribute("extAtt_parameter")).setValueString(extendbouis[iDx] ); ;
                       }
                    }
                    
                    

                    pos++;
                }
                
                // recaular workflow
                if ( !objParent.getName().equals("Ebo_Template") )
                {
                    it.beforeFirst();
                    while ( it.next() )
                    {
                        boObject activity = it.currentRow().getObject();
                        String state = activity.getStateAttribute( WF.STATE_ATR_PRIMARY_STATE ).getValueString();
                        if( state.equals( WF.STATE_OPEN ) )
                        {
                          WFinternal.computeChildStates_STATUS_OPEN( activity );  
                        }
                        else if( state.equals( WF.STATE_CREATE ) )
                        {
                          WFinternal.computeChildStates_STATUS_CREATE( activity );   
                          
                        }
                    }
                }
            }
            
        }
    }

    /**
     * Chamado quando é adicionada uma Activity
     *
     * @param xobj
     * @param event
     * @see
     */
    public static boolean afterAddActivity(boObject xobj, boEvent event)
        throws boRuntimeException
    {
        boolean ret = true;

        if ((event.getValue() != null) && (xobj.getMode() != boObject.MODE_EDIT_TEMPLATE))
        {
            boObject activity = boObject.getBoManager().loadObject(
                    xobj.getEboContext(), (( BigDecimal ) event.getValue()).longValue());

            if (activity.getName().equals("Ebo_Template"))
            {
                boObject newobj = (( Ebo_TemplateImpl ) activity).loadTemplate(xobj, null);
                (( ObjAttHandler ) event.getSourceObject()).setValue(newobj);
                activity = newobj;
            }

            // nesta altura a activity já tem pais.Vou calcular estados
            WFinternal.workflow_AfterAddActivity(
                (( BridgeObjAttributeHandler ) event.getSourceObject()).getBridge(), activity);
        }

        return ret;
    }

    /**
     * Chamado quando e alterado o attributo CONFIG da Actividade que está no WorkFlow
     * @param attr
     * @param event
     * @see
     */
    public static final void afterChangeActivityConfig(BridgeObjAttributeHandler attr, boEvent event)
        throws boRuntimeException
    {
        Object newValue = event.getNewValue();
        Object oldValue = event.getPreviousValue();

        /**
         * se estiver null quer dizer que acabou de ser removida a linha e o config foi colocado a NULL
         * TOREVIEW
         */
        if (newValue != null) 
        {
            boObject activity = attr.getBridge().getObject();
            
            if ( activity.getMode() != boObject.MODE_EDIT_TEMPLATE )
            {
                String xname = activity.getAttribute("name").getValueString();
    
                bridgeHandler wkfl = activity.getBridge("DAO");
                WFinternal.workflow_AfterChangeActivityConfig(wkfl, activity);
            }
        }

        //        else
        //        {
        //            int x = attr.getBridge().getRowCount();
        //            int y = 0;
        //            boObject activityRemoved = (( BridgeObjAttributeHandler ) event.getSourceObject()).getParent();
        //
        //            String xname = activityRemoved.getAttribute("name").getValueString();
        //            int rr = 2;
        //        }
    }

    /**
    * Chamado antes de ser removida uma activity de um WorkFlow
    * @param att
    * @see
    */
    public static boolean beforeRemoveWorkFlowActivity(BridgeObjAttributeHandler att)
        throws boRuntimeException
    {
        //        boolean isStatefull = p.poolIsStateFull();

        
        bridgeHandler wkfl = att.getBridge();
        boObject toRemove = att.getObject();
        int codeToBeRemoved = ClassUtils.convertToInt(wkfl.getAttribute("code").getValueString());

        boBridgeIterator it = wkfl.iterator();

        //retirar as dependencias que existe para esta activity
        // e recalcular dependencias
        while (it.next())
        {
            int currentCode = ClassUtils.convertToInt(it.currentRow().getAttribute("code").getValueString());

            if (currentCode != codeToBeRemoved)
            {
                if (currentCode > codeToBeRemoved)
                {
                    // altera o Atribute CODE
                    it.currentRow().getAttribute("code").setValueString("" + (currentCode - 1));
                }

                boObject config = it.currentRow().getAttribute("config").getObject();
                if (config != null)
                {
                    bridgeHandler depends = config.getBridge("linedepends");
                    depends.beforeFirst();

                    while (depends.next())
                    {
                        boObject dependencia = depends.getObject();

                        int code1 = ClassUtils.convertToInt(
                                dependencia.getAttribute("DAO_code").getValueString());

                        if ((code1 >= codeToBeRemoved) && ((code1 - 1) > 0))
                        {
                            dependencia.getAttribute("DAO_code").setValueString("" + (code1 - 1));
                        }
                        else if (((code1 - 1) == 0) && (code1 >= codeToBeRemoved))
                        {
                            if (depends.remove())
                            {
                                depends.previous();
                            }
                        }
                    }

                    if (depends.isEmpty())
                    {
                        if (config.getBridge("extAtt_parameter").isEmpty())
                        {
                            it.currentRow().getAttribute("config").setValueObject(null);
                        }
                    }
                    else if (depends.getRowCount() > 1)
                    {
                        /*
                         * Vai verificar se ficou com dependencias em duplicado
                         */
                        depends.beforeFirst();
                        while (depends.next())
                        {
                            String xDAO_code = depends.getObject().getAttribute("DAO_code").getValueString();
                            boolean removeDepend = false;
                            boBridgeIterator itd = depends.iterator();
                            while (itd.next())
                            {
                                if (itd.getRow() != depends.getRow())
                                {
                                    if (
                                        itd.currentRow().getObject().getAttribute("DAO_code").getValueString()
                                               .equals(xDAO_code))
                                    {
                                        removeDepend = true;
                                    }
                                }
                            }

                            if (removeDepend)
                            {
                                depends.remove();
                                depends.previous();
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    /**
    * Chamado depois de ser removida uma activity de um WorkFlow
    * @param att
    * @see
    */
    public static void afterRemoveWorkFlowActivity(BridgeObjAttributeHandler att, boEvent event)
        throws boRuntimeException
    {
        bridgeHandler wkfl = att.getBridge();
        boObject activityOwner = wkfl.getParent();
        
        if (activityOwner.getMode() != boObject.MODE_EDIT_TEMPLATE)
        {
            long bouiRemoved = (( BigDecimal ) event.getValue()).longValue();
            boObject activityRemoved = boObject.getBoManager().loadObject(wkfl.getEboContext(), bouiRemoved); // wkfl.getObject(); // a linha ainda está lá!

            boObject[] parents = activityRemoved.getParents();

            String xname2 = activityRemoved.getAttribute("name").getValueString();
            String xname3 = activityOwner.getAttribute("name").getValueString();
            
           WFinternal.workflow_RebuildAllStates( wkfl );

            if (
                (
                        (activityOwner.getParameter("DESANEXAR") != null) &&
                        activityOwner.getParameter("DESANEXAR").equalsIgnoreCase("yes")
                    ) || (parents.length > 0))
            {
                //desanexar ou continua com parents .... então não vou anular vou apenas desanexar
                return;
            }

            activityOwner.getUpdateQueue().add(activityRemoved, boObjectUpdateQueue.MODE_DESTROY_FORCED);

            bridgeHandler subWkfl = activityRemoved.getBridge("DAO");
            subWkfl.first();
            while (subWkfl.getRowCount() > 0)
            {
                subWkfl.remove();
            }
        }
    }
}
