/*Enconding=UTF-8*/
package netgest.bo.workflow;

import netgest.bo.impl.Ebo_AnnounceImpl;
import netgest.bo.impl.states.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.sql.*;

import java.util.*;


/**
 *
 * @author JMF
 */
public class WF
{
    public static final String STATE_ATR_PRIMARY_STATE = "primaryState";
    public static final String STATE_ATR_OPEN_STATUS = "openStatus";
    public static final String STATE_ATR_OPEN_ALERTS = "openAlerts";
    public static final String STATE_ATR_WAITFOR_STATUS = "waitForStatus";
    public static final String STATE_ATR_CREATED_STATUS = "createdStatus";
    public static final String STATE_ATR_NOTSTART_STATUS = "noStartStatus";
    public static final String STATE_ATR_CREATED_ALERTS = "createdAlerts";
    public static final String STATE_ATR_CLOSE_STATUS = "closeStatus";
    public static final String STATE_CREATE = "create";
    public static final String STATE_OPEN = "open";
    public static final String STATE_CLOSE = "close";
    public static final String STATE_ARCHIVE = "archive";
    public static final String STATE_STARTED = "started";
    public static final String STATE_NOTSTARTED = "notStart";
    public static final String STATE_WORKING = "working";
    public static final String STATE_WAITFOR = "waitfor";
    public static final String STATE_HOLD = "hold";
    public static final String STATE_WAITINGQUEUE = "waitingQueue";
    public static final String STATE_WAITINGTIME = "waitingTime";
    public static final String STATE_CONDITIONNOTVALID = "conditionNotValid";
    public static final String STATE_GREEN = "Green";
    public static final String STATE_ORANGEALERT = "OrangeAlert";
    public static final String STATE_REDALERT = "redAlert";
    public static final String STATE_CANCEL = "cancel";
    public static final String STATE_SOFTCLOSE = "softclose";
    public static final String STATE_HARDCLOSE = "hardclose";
    public static final int DEPENDS_NO = 0;
    public static final int DEPENDS_YES = 1;
    public static final int DEPENDS_LAGTIME = 2;

    public WF()
    {
    }

  
  public static void activity_setValuesForAccept( boObject activity ) throws boRuntimeException
    {
       
       long perf = activity.getEboContext().getBoSession().getPerformerBoui();
       AttributeHandler performer = activity.getAttribute("performer");
       if ( performer.getValueObject() == null )
       {
            performer.setValueLong( perf , AttributeHandler.INPUT_FROM_INTERNAL );
       }
       AttributeHandler beginDate = activity.getAttribute("beginDate");
       AttributeHandler endDate = activity.getAttribute("endDate");
       
	   if( beginDate.getValueObject() ==null) 
       {
			if( endDate.getValueDate()==null )
            {
                beginDate.setValueDate(new java.util.Date(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
			else
            {
                beginDate.setValueDate( endDate.getValueDate(), AttributeHandler.INPUT_FROM_INTERNAL);
            }
	   } 
    }
    
    private static boolean activity_setValuesForUnTake( boObject activity ) throws boRuntimeException
    {              
        boolean result = false;
        if(activity.valid())
        {
            AttributeHandler performer = activity.getAttribute("performer");
            if (performer.getValueObject() != null)
            {
                performer.setValueObject(null,AttributeHandler.INPUT_FROM_INTERNAL);
                boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);                               
                String pstate = statePrimary.getValueString();
                if (pstate.equals(WF.STATE_OPEN))
                {
                    boObjectStateHandler openStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS);
                    if (openStatus.getValueString().equalsIgnoreCase(WF.STATE_WORKING))                    
                    {   
                        activity_Create(activity);
                    }
                    else if (openStatus.getValueString().equalsIgnoreCase(WF.STATE_WAITFOR))
                    {
                        boObjectStateHandler createStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);
                        createStatus.setValueString(WF.STATE_NOTSTARTED);
                        createStatus.getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(WF.STATE_WAITINGQUEUE);
                        statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(WF.STATE_GREEN);                                                                
                    }
                }                        
                activity.update();
                result = true;
            }            
        }
        else
        {
            activity.setHaveObjectErrors(true);
        }
        return result;
    }    
    
    public static void activity_Take( boObject activity ) throws boRuntimeException
    {
      activity_setValuesForAccept( activity );            
      WF.activity_Open( activity.getParent() );    
    }
    
    public static void activity_unTake( boObject activity ) throws boRuntimeException
    {
        activity_setValuesForUnTake( activity );      
    }
  
    /**
     * Este methodo e chamado no onCreate da activity
     *
     * @param activity
     * @see
     */
    public static final void activity_init(boObject activity)
        throws boRuntimeException
    {
        //  
        try
        {
            if (activity.getAttribute("performer").getValueObject() == null)
            {
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).setValueString(WF.STATE_CREATE);
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE)
                        .getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(WF.STATE_STARTED);
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE)
                        .getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(WF.STATE_GREEN);
            }
            else
            {
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).setValueString(WF.STATE_OPEN);
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE)
                        .getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_OPEN);
                activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE)
                        .getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS).setValueString(WF.STATE_GREEN);
                
                
            }
        }
        catch (Exception e)
        {
            throw new boRuntimeException(activity, "WF.activity_init", "WKFL-000", e);
        }
    }

    /**
      * Coloca a actividade em estado STATE_CREATE
      * @param activity
      * @see
      */
    public static final boolean activity_Create(boObject activity)
        throws boRuntimeException
    {
        return WFinternal.activity_ChangeStateToCreate(activity);
    }

    /**
      * Coloca a actividade em estado STATE_OPEN
      * @param activity
      * @see
      */
    public static final boolean activity_Open(boObject activity)
        throws boRuntimeException
    {
        return WFinternal.activity_ChangeStateToOpen(activity);
    }

    /**
     * Coloca a actividade em estado STATE_OPEN_HOLD
     * @param activity
     * @see
     */
    public static final void activity_Suspend(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        String pstate = statePrimary.getValueString();
        if (pstate.equals(WF.STATE_OPEN))
        {
            statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS)
                        .getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS).setValueString(null);

            statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_HOLD);
        }
    }

    /**
    * Coloca a actividade em estado STATE_OPEN
    * @param activity
    * @see
    */
    public static final void activity_unSuspend(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler openStatus = activity.getStateAttribute(WF.STATE_ATR_OPEN_STATUS);
        openStatus.setValueString(WF.STATE_WORKING);
        WFinternal.activity_ChangeStateToOpen(activity);
    }

    /**
    * Completa uma actividade
    *
    * NOTA :
    * Depois de completar a actividade vai
    * "fazer andar" o workFlow
    *
    * @param   activity      Objecto Actividade para Completar
    * @param   statusClose   Sub Estado do Close com os seguintes valores
    *
    * STATE_SOFTCLOSE ou STATE_HARDCLOSE ou STATE_CANCEL*
    *
    * @return sucess
    *
    */
    public static final boolean activity_Complete(boObject activity, String statusClose)
        throws boRuntimeException
    {
        WFInf inf = new WFInf();

        //        if (activity_CanComplete(activity, statusClose, inf))
        //        {
        WFinternal.activity_ChangeStateToClose(activity, statusClose);

        boObject[] parents = activity.getParents();
        for (int i = 0; i < parents.length; i++)
        {
			if(parents[i].getBridge("DAO")==null)
              continue;
            WFinternal.workflow_RebuildAllStates(parents[i].getBridge("DAO"));
            if (!WFinternal.activity_IsRunning(parents[i]))
            {
                if (activity_CanStart(parents[i], null))
                {
                    String xsta = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
                    if (xsta.equals(WF.STATE_CREATE))
                    {
                        WFinternal.activity_ChangeStateToCreate(parents[i]);
                    }
                    else if (xsta.equals(WF.STATE_OPEN))
                    {
                        WFinternal.activity_ChangeStateToOpen(parents[i]);
                    }
                }
            }
        }

        //        }
        return true;
    }

    /**
     * Verifica se a actividade pode arrancar
     * ié - Se pode ficar :
     *      CREATES - STARTED
     *      ou
     *      OPEN - RUNNING
     * @param   activity      Objecto Actividade
     * @param   inf           Objecto onde o metodo vai escrever informações ( pode ser passado a null )
     * @return  <code>true</code> se for verdade;
     *          <code>false</code> se for false.
     */
    public static final boolean activity_CanStart(boObject activity, WFInf inf)
        throws boRuntimeException
    {
        boolean toRet = true;

        //first -> view direct dependences for each parent
        int haveDependences = WFinternal.activity_haveDependences_FS_SS(activity, inf);

        toRet = (haveDependences == WF.DEPENDS_NO);

        boolean conditionIsValid = true;

        if (toRet)
        {
            /*
             * - Se entrar aqui quer dizer que pelos WorkFlow's onde esta activity entra
             *   não tem dependencias directas a inibir o START desta
             *
             * - agora vou verificar se tem condição e se esta é verdadeira
             *   Nota : tenho que verificar as condições em todos os protcolos dos parents
             */
            toRet = WFinternal.activity_ConditionIsValid(activity.getParents(), activity);
        }

        if (toRet)
        {
            /*
             * Até aqui foram verificadas os WorkFlows bridge onde o objecto está
             * e as condições para a Activity ser executada
             *
             * agora vou verificar se o WorkFlow desta actividade ja está completo
             *
             */
            bridgeHandler wkfl = activity.getBridge("DAO");
            toRet = workFlow_IsComplete(wkfl);
        }

        return toRet;
    }

    /**
     * Verifica se as actividades contidas na WorkfFlow Bridge
     * ja estao todas completas ou arquivadas
     *
     * @param wkfl      Bridge representativa do WorkFlow
     *
     */
    public static final boolean workFlow_IsComplete(bridgeHandler wkfl)
        throws boRuntimeException
    {
        boolean toRet = true;

        if ((wkfl != null) && !wkfl.isEmpty())
        {
            boBridgeIterator it = wkfl.iterator();

            while (it.next() && toRet)
            {
                toRet = WFinternal.activity_IsClosedOrArchive(it.currentRow().getObject());
            }
        }

        return toRet;
    }

    /**
    * Verifica se uma actividade pode ser completada
    * 1- Verifica se está RUNNING ( aceite e aberta )
    * 2- Verifica se tem os ExtendedAtributes correctamente preenchidos
    * 3- Verifica se tem WorkFlow e este já está todo completo
    * 4- Verifica se tem alguma dependencia FF ( Finish to Finish ).Isto é
    * se a Actividade depender de outra para fechar e esta não está fechada
    * então a Actividade não poderá ser fechada.
    * Verifica tb se tem alguma dependencia SF ( Start to Finish ). Isto é
    * se a actividade depender que outra arranque para esta fechar.
    *
    *
    * @param   activity      Objecto Actividade a ser completada
    * @param   statusClose   Sub Estado do Close com os seguintes valores
    *                        STATE_SOFTCLOSE ou STATE_HARDCLOSE ou STATE_CANCEL
    * @param   inf           Objecto onde o metodo vai escrever informações ( pode ser passado a null )
    *
    * @return sucess   Se o metodo retornar <code>false</code> deverá ser consultado o objecto
    *                  <code>inf</code> para saber as razões
    *
    */
    public static final boolean activity_CanComplete(boObject activity, String statusClose, WFInf inf)
        throws boRuntimeException
    {
        boolean step1 = true;
        boolean step2 = true;
        boolean step3 = true;
        boolean step4 = true;

        if (statusClose.equals(STATE_HARDCLOSE))
        {
        }
        else if (statusClose.equals(STATE_SOFTCLOSE))
        {
        }
        else if (statusClose.equals((STATE_CANCEL)))
        {
        }

        /*-------- STEP 1 ----------------------------*/
        if (!WFinternal.activity_IsRunning(activity))
        {
            step1 = false;
            if (WFinternal.activity_IsClosedOrArchive(activity))
            {
                if (inf != null)
                {
                    inf.addWarning(
                        activity, netgest.bo.workflow.WFInf.OP_CANCOMPLETE,
                        netgest.bo.workflow.WFInf.WF_ACTIVITY_ALREADY_COMPLETE);
                }
            }
            else
            {
                if (inf != null)
                {
                    inf.addError(
                        activity, netgest.bo.workflow.WFInf.OP_CANCOMPLETE,
                        netgest.bo.workflow.WFInf.WF_ACTIVITY_UNKNOWN_STATE);
                }
            }
        }

        /*-------- STEP 2 - Verificar extended attributes ----------------------------*/
        //TODO
        step2 = true;

        /*-------- STEP 3 - Verificar Workflow ----------------------------*/
        step3 = workFlow_IsComplete(activity.getBridge("DAO"));

        /*-------- STEP 4 - Verificar se tem dependencia FF ou SF ----------------------------*/
        step4 = WFinternal.activity_haveDependences_FF_SF(activity, inf) == WF.DEPENDS_NO;

        return step1 && step2 && step3 && step4;
    }

    public static String[] activity_getAvailableActions(boObject object)
        throws boRuntimeException
    {
        String[] ret = null;
        String pstate = object.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
		if(object.getBoDefinition().getName().equals("simplemail"))
          return ret;
        if (pstate.equals("open"))
        {
            String pst1 = object.getStateAttribute("openStatus").getValueString();
            String pst2 = object.getStateAttribute("openAlerts").getValueString();

            if (pst1.equals("working"))
            {
                if (isRequireWithCondition(object, 1, false) || isRequireWithCondition(object, 2, false))
                {                                                          
                    ret = new String[]
                        {
                            "Libertar" + ";activity_unTake",
                            
                            object.getStateAttribute("closeStatus").getChildState("cancel").getDefinition()
                                  .getLabelAction() + ";activity_CompleteCancel",
                            
                            object.getStateAttribute("openStatus").getChildState("hold").getDefinition()
                                  .getLabelAction() + ";activity_Hold",
                        };
                }
                else
                {
                    AttributeHandler performer = object.getAttribute("performer");
                    if (performer.getValueObject() != null)
                    {                    
                        ret = new String[]
                            {
                                "Libertar" + ";activity_unTake",
                                
                                object.getStateAttribute("closeStatus").getChildState("softclose").getDefinition()
                                      .getLabelAction() + ";activity_CompleteSoft",
                                
                                object.getStateAttribute("closeStatus").getChildState("cancel").getDefinition()
                                      .getLabelAction() + ";activity_CompleteCancel",
                                
                                object.getStateAttribute("openStatus").getChildState("hold").getDefinition()
                                      .getLabelAction() + ";activity_Hold",
                            };
                    }
                    else
                    {
                        ret = new String[]
                            {
                                
                                object.getStateAttribute("closeStatus").getChildState("softclose").getDefinition()
                                      .getLabelAction() + ";activity_CompleteSoft",
                                
                                object.getStateAttribute("closeStatus").getChildState("cancel").getDefinition()
                                      .getLabelAction() + ";activity_CompleteCancel",
                                
                                object.getStateAttribute("openStatus").getChildState("hold").getDefinition()
                                      .getLabelAction() + ";activity_Hold",
                            };
                        
                    }
                }
            }
            else if (pst1.equals("hold"))
            {
            
                ret = new String[]
                    {
                        object.getStateAttribute("openStatus").getChildState("working").getDefinition()
                              .getLabelAction() + ";activity_Unhold",
                    };
            }
            else
            {
                AttributeHandler performer = object.getAttribute("performer");
                if (performer.getValueObject() != null)
                {
                    ret = new String[]
                        {
                            "Libertar" + ";activity_unTake"
                        };            
                }
            }            
        }
        else if (pstate.equals("create")) //&& (object.getAttribute("performer").getValueObject() == null))
        {
            if (!object.getStateAttribute("createdStatus").getValueString().equals("notStart"))
            {
                if (isRequireWithCondition(object, 1, false) || isRequireWithCondition(object, 2, false))
                {
                    ret = new String[]
                        {
                            "Aceitar" + ";activity_Take",
                            
                            "Aceitar e " +
                            object.getStateAttribute("closeStatus").getChildState("cancel").getDefinition()
                                  .getLabelAction() + ";activity_CompleteCancel",
                            
                            "Aceitar e " +
                            object.getStateAttribute("openStatus").getChildState("hold").getDefinition()
                                  .getLabelAction() + ";activity_Hold",
                        };
                }
                else
                {
                    ret = new String[]
                        {
                            "Aceitar" + ";activity_Take",
                            
                            "Aceitar e " +
                            object.getStateAttribute("closeStatus").getChildState("softclose").getDefinition()
                                  .getLabelAction() + ";activity_Take:activity_CompleteSoft",
                            
                            "Aceitar e " +
                            object.getStateAttribute("closeStatus").getChildState("cancel").getDefinition()
                                  .getLabelAction() + ";activity_Take:activity_CompleteCancel",
                            
                            "Aceitar e " +
                            object.getStateAttribute("openStatus").getChildState("hold").getDefinition()
                                  .getLabelAction() + ";activity_Take:activity_CompleteHold",
                        };
                }
            }
            else
            {
                ret = new String[] { "Aceitar" + ";activity_Take", };
            }
        }

        else if (pstate.equals("close"))
        {
            ret = new String[] { "Reabrir" + ";activity_Reopen", };
        }


        return ret;
    }

    public static boolean isRequireWithCondition(boObject object, int requireCondition, boolean message)
        throws boRuntimeException
    {
        boolean condition = false;
        String errorMsg = "";
        bridgeHandler bridgeExtAttr = object.getBridge("extendAttribute");
        if (!bridgeExtAttr.isEmpty())
        {
            bridgeExtAttr.beforeFirst();
            while (bridgeExtAttr.next())
            {
                boObject extObj = bridgeExtAttr.getObject();
                long aux = extObj.getAttribute("attributeRequire").getValueLong();
                if (aux == requireCondition)
                {
                    long attributeType = extObj.getAttribute("attributeType").getValueLong();
                    String attrName = DocWfHTML.getExtendAttributeName(extObj);
                    if (attributeType == 0)
                    {
                        long type = extObj.getAttribute("attributeCardinal").getValueLong();
                        if (type == 1)
                        {
                            boObject obj = ( boObject ) extObj.getAttribute(attrName).getObject();
                            if (obj == null)
                            {
                                errorMsg = extObj.getAttribute("alias").getValueString() +
                                    " : Campo de preenchimento obrigatório, para a actividade ser ";
                                condition = true;
                            }
                        }
                        else
                        {
                            bridgeHandler bh = extObj.getBridge(attrName);
                            if (bh.isEmpty())
                            {
                                errorMsg = extObj.getAttribute("alias").getValueString() +
                                    " : Lista de preenchimento obrigatório, para a actividade ser ";
                                condition = true;
                            }
                        }
                    }
                    else
                    {
                        String value = extObj.getAttribute(attrName).getValueString();
                        if ("".equals(value))
                        {
                            errorMsg = extObj.getAttribute("alias").getValueString() +
                                " : Campo de preenchimento obrigatório, para a actividade ser ";
                            condition = true;
                        }
                    }

                    if (condition == true)
                    {
                        if (requireCondition == 1)
                        {
                            errorMsg += "criada";
                        }
                        else if (requireCondition == 2)
                        {
                            errorMsg += "completada";
                        }

                        if (message)
                        {
                            object.addErrorMessage(errorMsg);
                        }
                    }
                }
            }
        }

        return condition;
    }

    public static void moveExtendedAttribute(AttributeHandler attr, boEvent event)
        throws boRuntimeException
    {
        
        boObject extendedAttr = attr.getParent();
        boObject activity = extendedAttr.getParent();
        if ( activity == null ) // esta a anular
        {
            return;
        }
        boObjectStateHandler pState=activity.getStateAttribute( "primaryState" );
        if ( pState==null ) // esta a anular
        {
            return;
        }        
//        String xname = activity.getAttribute("name").getValueString();
        if (activity.getMode() != boObject.MODE_EDIT_TEMPLATE)
        {
            movingInformation(activity, event);

            boObject[] parents = activity.getParents(); // deverá ser mais inteleigente
            for (int i = 0; i < parents.length; i++)
            {
                String xname2 = parents[i].getAttribute("name").getValueString();
                movingInformation(parents[i], event);
            }
        }
    }

    private static void movingInformation(boObject parentObject, boEvent event)
        throws boRuntimeException
    {
        AttributeHandler srcAtt = (AttributeHandler)event.getSourceObject();
        boObject srcObj = srcAtt.getParent().getParent();
        
        if ( parentObject.getParameter("moving")!= null )
        {
            //para evitar recursividade em definições erradas
            return;
        }
        parentObject.setParameter("moving","yes");
        bridgeHandler bridgeWkfl = parentObject.getBridge("DAO");
        bridgeHandler parentExtendedAttributes = parentObject.getBridge("extendAttribute");
        boBridgeIterator itParentExtended = parentExtendedAttributes.iterator();

        //        int rec = bridgeDAO.getRow();
        //        bridgeDAO.beforeFirst();
        boObject childObject;
        boBridgeIterator it = bridgeWkfl.iterator();
        
        //está ser ordenado de forma a que o objecto alterado seja efectuado
        //primeiro que os restantes de forma a propagar as alterações aos outros filhos 
        ArrayList r = new ArrayList();
        while(it.next())
        {
            childObject = it.currentRow().getObject();
            if(childObject.getBoui() == srcObj.getBoui())
            {
                r.add(0,it.currentRow());
            }
            else
            {
                r.add(it.currentRow());
            }
        }

        for (int i = 0; i < r.size(); i++) 
        {
            childObject =  ((boBridgeRow)r.get(i)).getObject();

            //    if ( childObjectgetParameter("noOnsave")==null && childObjectgetStateManager() != null && 
            //        !childObject.getStateAttribute( "primaryState" ).getValueString().equals("close") ) &&
            //        !object.getStateAttribute("primaryState").getValueString().equals("close")
            //    {
            boObject config = ((boBridgeRow)r.get(i)).getAttribute("config").getObject();
            if (config != null)
            {
                config.setParentBridgeRow( ((boBridgeRow)r.get(i)) );

                bridgeHandler bridgeConfigParameters = config.getBridge("extAtt_parameter");

                boBridgeIterator it2 = bridgeConfigParameters.iterator();

                while (it2.next())
                {
                    boObject mapExtendedAttribute = it2.currentRow().getObject();
                    if (mapExtendedAttribute != null)
                    {
                        String extAttrAliasParent = mapExtendedAttribute.getAttribute("extAttrAliasParent")
                                                                        .getValueString();

                        //extentended attributo do boObject(pai)
                        if (!"".equals(extAttrAliasParent))
                        {
                            // procurar no parentExtendedAttributes do PAI para colocar no o
                            //parentExtendedAttributes.beforeFirst();
                            itParentExtended.beforeFirst();
                            while (itParentExtended.next())
                            {
                                boObject parentExtendedAttribute = itParentExtended.currentRow().getObject();
                                String shortAlias = parentExtendedAttribute.getAttribute("shortAlias")
                                                                           .getValueString();
                                if (shortAlias.equals(extAttrAliasParent))
                                {
                                    movingExtendAttribute(
                                        parentObject, parentExtendedAttribute, mapExtendedAttribute,
                                        childObject, srcObj);
                                }
                            }
                        }
                    }
                }
            }

            //   }
        }

        //        bridgeDAO.moveTo(rec);
        
        parentObject.removeParameter("moving");
    }

    private static void movingExtendAttribute(
        boObject parentObject, boObject parentExtendedAttribute, boObject mapExtendedAttribute,
        boObject childObject, boObject srcObj)
        throws boRuntimeException
    {
        String typeAction = mapExtendedAttribute.getAttribute("inout").getValueString();
        String extAttrAliasChild = mapExtendedAttribute.getAttribute("extAttrAliasChild").getValueString();

        bridgeHandler childExtendedAttributes = childObject.getBridge("extendAttribute");
        boolean found = false;
        boolean wasSend = false;
        boBridgeIterator it = childExtendedAttributes.iterator();
//        childExtendedAttributes.beforeFirst();
        while ( it.next() )
        {
            boObject childExtendedAttribute = it.currentRow().getObject();
            String shortAlias = childExtendedAttribute.getAttribute("shortAlias").getValueString();
            if (shortAlias.equals(extAttrAliasChild))
            {
                found = true;

                if (("inout".equals(typeAction)))
                {
                    //vou enviar para o child ou vou receber do child ?
                    if (parentExtendedAttribute.isChanged() && isFilled(parentExtendedAttribute)) 
                    {
                        if(parentExtendedAttribute.getParent().getBoui() == 
                            srcObj.getBoui())
                        {
                            //vou enviar para os filhos
                            wasSend = sendInformation(parentExtendedAttribute, childExtendedAttribute);
                        }
                        else if(childExtendedAttribute.getParent().getBoui() != 
                            srcObj.getBoui())
                        {
                            //vou enviar para os outros filhos
                            wasSend = sendInformation(parentExtendedAttribute, childExtendedAttribute);
                        }
                    }
                    if (!wasSend && childExtendedAttribute.isChanged() 
                        && isFilled(childExtendedAttribute) && 
                        childExtendedAttribute.getParent().getBoui() == 
                            srcObj.getBoui()) 
                    {
                        //vou receber do fllho
                        wasSend = sendInformation(childExtendedAttribute, parentExtendedAttribute);
                        parentExtendedAttribute.setParameter("isReceiveing", "true");
                        parentExtendedAttribute.setParameter("isReceiveingFromBoui", 
                            String.valueOf(childExtendedAttribute.getParent().getBoui()));
                    }
                    else
                    {
                        //vou enviar para o filho NAO VOU FAZER NADA JMF 
//                        wasSend = sendInformation(parentExtendedAttribute, childExtendedAttribute);

//                        if (wasSend)
//                        {
//                            childExtendedAttributes.edit();
//                        }
                    }
                }
                else if (("out".equals(typeAction)))
                {
                    //vou enviar para o filho
                    wasSend = sendInformation(parentExtendedAttribute, childExtendedAttribute);
//                    if (wasSend)
//                    {
//                        childExtendedAttributes.edit();
//                    }
                }
                else if ("in".equals(typeAction))
                {
                    //vou receber do filho
                    wasSend = sendInformation(childExtendedAttribute, parentExtendedAttribute);
                }
            }
        }

        // não encontrou no filho .... adiciona ? NESTE MOMENTO ADICIONA SEMPRE   
        if (!found)
        {
            if (true) //isFilled(parentExtendedAttribute))
            {
                boObject newExtObj = parentExtendedAttribute.getBoManager().createObject(
                        parentExtendedAttribute.getEboContext(), parentExtendedAttribute);

                if ("inout".equals(typeAction))
                {
                    newExtObj.getAttribute("attributeRequire").setValueString("3");

                    //O pai está a dizer que vai enviar/receber então required == não Obrigatório
                    newExtObj.getAttribute("changed").setValueString("1");

                    // pode ser alterado == sim
                }
                else if ("in".equals(typeAction))
                {
                    newExtObj.getAttribute("attributeRequire").setValueString("2");

                    //O pai está a dizer que quer receber então required == Obrigatório para ser completado
                    newExtObj.getAttribute("changed").setValueString("1");

                    // pode ser alterado == sim
                }
                else
                {
                    newExtObj.getAttribute("attributeRequire").setValueString("3");

                    //O pai está a dizer que vai enviar então required == não Obrigatório
                    newExtObj.getAttribute("changed").setValueString("0");

                    // pode ser alterado == nao
                }

                newExtObj.getAttribute("attributeConstraints").setValueString("3");

                //não interessa onde é definido
                childExtendedAttributes.add(newExtObj.getBoui());
//                childExtendedAttributes.edit();

                String xshortAlias = newExtObj.getAttribute("shortAlias").getValueString();

                //             String xaliasp=mapExtendedAttribute.getAttribute("extAttrAliasParent").getValueString();
                //              String xalis=newExtObj.getAttribute("shortAlias").getValueString();
                mapExtendedAttribute.getAttribute("extAttrAliasChild").setValueString(
                    newExtObj.getAttribute("shortAlias").getValueString());

                //                mapExtendedAttribute.edit();
                //
                //                mapExtendedAttribute.getParentBridgeRow().edit();
                //                boObject daoConfig = mapExtendedAttribute.getParentBridgeRow().getParent();
                //                daoConfig.getParentBridgeRow().edit();
                //                daoConfig.setChanged(true);
                //                parentObject.getUpdateQueue().add(daoConfig, boObjectUpdateQueue.MODE_SAVE);
                //         String namechild=childObject.getAttribute("name").getValueString();
                //                parentObject.setChanged(true);
                //         String xname=parentObject.getAttribute("name").getValueString();
                //           xname+=" .2";
                //           parentObject.getAttribute("name").setValueString(xname);
            }
        }
    }


    private static boolean sendInformation(boObject fromObj, boObject toObj)
        throws boRuntimeException
    {
        boolean wasSend = false;
        long cardinalidade = fromObj.getAttribute("attributeCardinal").getValueLong();
        long attributeType = fromObj.getAttribute("attributeType").getValueLong();
        String attrName = DocWfHTML.getExtendAttributeName(fromObj);
        if (attributeType == 0)
        {
            if (cardinalidade == 1)
            {
                boObject valueObject = fromObj.getAttribute(attrName).getObject();
                if (valueObject != null)
                {
                    toObj.getAttribute(attrName).setValueLong(valueObject.getBoui());
                    wasSend = true;
                }
            }
            else
            {
                bridgeHandler toBridge = toObj.getBridge(attrName);
                bridgeHandler valueList = fromObj.getBridge(attrName);
                toBridge.beforeFirst();
                valueList.beforeFirst();

                if (!toBridge.isEmpty())
                {
                    while (toBridge.next())
                    {
                        toBridge.remove();
                        toBridge.previous();
                    }

                    wasSend = true;
                }

                if (!valueList.isEmpty())
                {
                    while (valueList.next())
                    {
                        toBridge.add(valueList.getValue(), AttributeHandler.INPUT_FROM_INTERNAL);
                    }

                    wasSend = true;
                }
            }
        }
        else
        {
//            boObject o = fromObj.getParentBridgeRow().getParent();
//            String fromName =o.getAttribute("name").getValueString();
//            
//            boObject o2 = toObj.getParentBridgeRow().getParent();
//            String toName =o2.getAttribute("name").getValueString();
            
            String value = fromObj.getAttribute(attrName).getValueString();
            toObj.getAttribute(attrName).setValueString(value);
            wasSend = true;
        }

        return wasSend;
    }

    private static boolean isFilled(boObject extAttr)
        throws boRuntimeException
    {
        boolean filled = false;
        long cardinalidade = extAttr.getAttribute("attributeCardinal").getValueLong();
        long attributeType = extAttr.getAttribute("attributeType").getValueLong();
        String attrName = DocWfHTML.getExtendAttributeName(extAttr);
        if (attributeType == 0)
        {
            if (cardinalidade == 1)
            {
                boObject valueObject = extAttr.getAttribute(attrName).getObject();
                if (valueObject != null)
                {
                    filled = true;
                }
            }
            else
            {
                bridgeHandler valueList = extAttr.getBridge(attrName);
                if (!valueList.isEmpty())
                {
                    filled = true;
                }
            }
        }
        else
        {
            String value = extAttr.getAttribute(attrName).getValueString();
            if (!"".equals(value))
            {
                filled = true;
            }
        }

        return filled;
    }
}
