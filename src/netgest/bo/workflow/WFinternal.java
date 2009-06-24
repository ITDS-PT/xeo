/*Enconding=UTF-8*/
package netgest.bo.workflow;

import netgest.bo.data.*;

import netgest.bo.runtime.*;

import netgest.utils.*;

import java.sql.*;

import java.util.*;
import org.apache.log4j.Logger;


/**
 * Esta classe é utilizada internamente pelo sistema de WorkFlow
 * Só tém metodos estáticos
 *
 * @author JMF
 * @version 1.00, 2004-07-07
 *
 * @see     WorkFlowMethods
 *
 * @since Xeo1.0
 */
public class WFinternal
{
    //logger
    private static Logger logger = Logger.getLogger("netgest.bo.workflow.WFinternal");
    
    public WFinternal()
    {
    }

    /**
     *
     * @param b     workflow Bridge
     * @param Code  Linha do WorkFlow
     *
     * @return Activity     Se encontrar uma actividade na Bridge Wkfl com
     *                      o Code (LIN ) retorna essa actividade em caso contrário
     *                      retorna null
     * @see
     */
    protected static final boObject getActivityByCODE(bridgeHandler b, String Code)
        throws boRuntimeException
    {
        boObject toRet = null;
        try
        {
            DataResultSet data = b.getRslt();
            long longCode = ClassUtils.convertToLong(Code);
            int pos = data.getRow();
            data.beforeFirst();

            while (data.next())
            {
                if (data.getLong("LIN") == longCode)
                {
                    toRet = b.getObject();

                    break;
                }
            }

            data.absolute(pos);
        }
        catch (SQLException e)
        {
            throw new boRuntimeException("WFInternal.getActivityByCODE", "WKFL-000", e);
        }

        return toRet;
    }

    /**
     * Verifica se a actvidade está em STATE_CLOSE ou STATE_ARCHIVE
     * @param activity
     * @see
     */
    protected static final boolean activity_IsClosedOrArchive(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler state = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        if (state.getValueString().equals(WF.STATE_CLOSE) || state.getValueString().equals(WF.STATE_ARCHIVE))
        {
            return true;
        }

        return false;
    }

    /**
     * Verfica se a actividade está em STATE_OPEN-STATE_WORKING ou STATE_CREATE-STATE_STARTED
     *
     *
     * @param activity
     * @see
     */
    protected static final boolean activity_IsRunning(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler state = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        if (state.getValueString().equals(WF.STATE_OPEN))
        {
            boObjectStateHandler stateOpen = state.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS);
            if (stateOpen.getValueString().equals(WF.STATE_WORKING))
            {
                return true;
            }
        }
        else if (state.getValueString().equals(WF.STATE_CREATE))
        {
            boObjectStateHandler stateCreate = state.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);

            if (stateCreate.getValueString().equals(WF.STATE_STARTED))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se a actividade está em STATE_CREATE-STATE_STARTED
     * @param activity
     * @see
     */
    protected static final boolean activity_IsStarted(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler state = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        if (state.getValueString().equals(WF.STATE_CREATE))
        {
            boObjectStateHandler stateCreate = state.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);

            if (stateCreate.getValueString().equals(WF.STATE_STARTED))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Verifica se tem dependencias FF ou SF nos Workflows onde a actividade está inserida
     * @param activity
     *
     * @return int  0- Não tem dependencias , 1 - tem dependencias 2 - tem dependencias de tempo apenas
     *
     * WF.DEPENDS_NO 0
     * WF.DEPENDS_YES 1
     * WF.DEPENDS_LAG 2
     * @see WF@see
     */
    public static final int activity_haveDependences_FF_SF(boObject activity, WFInf inf)
        throws boRuntimeException
    {
        boObject[] parents = activity.getParents();
        boolean haveDirectDependences = false;
        boolean haveLagTimeDependences = false;

        if (parents != null)
        {
            for (int i = 0; (i < parents.length) && !haveDirectDependences; i++)
            {
                bridgeHandler w = parents[i].getBridge("DAO");
                boBridgeRow br = null;
                if (w != null)
                {
                    br = w.getRow(activity.bo_boui);
                }

                if (br != null)
                {
                    boObject config = br.getAttribute("config").getObject();

                    if (config != null)
                    {
                        bridgeHandler lineDepends = config.getBridge("linedepends");
                        boBridgeIterator it = lineDepends.iterator();

                        while (it.next() && !haveDirectDependences)
                        {
                            boObject lineDepend = it.currentRow().getObject();
                            String CODE = lineDepend.getAttribute("DAO_code").getValueString();

                            long lag = ClassUtils.convertToLong(
                                    lineDepend.getAttribute("lag").getValueString());
                            String type = lineDepend.getAttribute("type").getValueString();

                            boObject refActivity = getActivityByCODE(w, CODE);

                            boolean refActivity_IsClosed = activity_IsClosedOrArchive(refActivity);
                            boolean refActivity_IsRunning = activity_IsRunning(refActivity);

                            if (type.equals("FS"))
                            {
                                /**
                                 * FINISH refActivity to START this activity
                                 * Verifica se a refActivity está fechada
                                 *
                                 * não interessa para o caso
                                 */
                            }
                            else if (type.equals("SF"))
                            {
                                /**
                                 * START refActivity to FINISH this activity
                                 *
                                 * vai verificar se a refActivity IsRunning
                                 * ou se está já close ( para garantir que pode ser fechada )
                                 *
                                 * SERÀ QUE NÃO DEVE SER ?!!!!
                                 * ------
                                 * if ( WorkFlowMethods.canStartActivity( refAtivity )
                                 * ------
                                 */
                                if (!(refActivity_IsRunning || refActivity_IsClosed))
                                {
                                    haveDirectDependences = true;
                                }
                                else if (lag > 0)
                                {
                                    haveLagTimeDependences = WFScheduleServices.activityVerifyLagTime(
                                            activity, refActivity, WF.STATE_STARTED, lag);
                                }
                            }
                            else if (type.equals("SS"))
                            {
                                /*
                                 * START refActivity to START this activity
                                 *
                                 * não interessa para o caso
                                 */
                            }
                            else if (type.equals("FF"))
                            {
                                /**
                                 *
                                 * FINISH refActivity to FINISH this activity
                                 *
                                 * Neste caso é uma relaçao para fechar
                                 *
                                 * Verifica se a ref activity está fechada
                                 */
                                if (!refActivity_IsClosed)
                                {
                                    haveDirectDependences = true;
                                }
                                else if (lag > 0)
                                {
                                    haveLagTimeDependences = WFScheduleServices.activityVerifyLagTime(
                                            activity, refActivity, WF.STATE_CLOSE, lag);
                                }
                            }
                        }
                    }
                }
            }
        }

        if (haveDirectDependences)
        {
            return WF.DEPENDS_YES;
        }
        else if (haveLagTimeDependences)
        {
            return WF.DEPENDS_LAGTIME;
        }
        else
        {
            return WF.DEPENDS_NO;
        }
    }

    /**
    * Verifica se tem dependencias FS ou SS nos Workflows onde a actividade está inserida
    * @param activity
    *
    * @return int  0- Não tem dependencias , 1 - tem dependencias 2 - tem dependencias de tempo apenas
    *
    * WF.DEPENDS_NO 0
    * WF.DEPENDS_YES 1
    * WF.DEPENDS_LAG 2
    * @see WF
    */
    protected static final int activity_haveDependences_FS_SS(boObject activity, WFInf inf)
        throws boRuntimeException
    {
        boObject[] parents = activity.getParents();
        boolean haveDirectDependences = false;
        boolean haveLagTimeDependences = false;

        if (parents != null)
        {
            for (int i = 0; (i < parents.length) && !haveDirectDependences; i++)
            {
                bridgeHandler w = parents[i].getBridge("DAO");
                boBridgeRow br = null;
                if (w != null)
                {
                    br = w.getRow(activity.bo_boui);
                }

                if (br != null)
                {
                    boObject config = br.getAttribute("config").getObject();

                    if (config != null)
                    {
                        bridgeHandler lineDepends = config.getBridge("linedepends");
                        boBridgeIterator it = lineDepends.iterator();

                        while (it.next() && !haveDirectDependences)
                        {
                            boObject lineDepend = it.currentRow().getObject();
                            String CODE = lineDepend.getAttribute("DAO_code").getValueString();
                            long lag = ClassUtils.convertToLong(
                                    lineDepend.getAttribute("lag").getValueString());

                            String type = lineDepend.getAttribute("type").getValueString();

                            boObject refActivity = netgest.bo.workflow.WFinternal.getActivityByCODE(w, CODE);

                            boolean refActivity_IsClosed = activity_IsClosedOrArchive(refActivity);
                            boolean refActivity_IsRunning = activity_IsRunning(refActivity);

                            if (type.equals("FS"))
                            {
                                /**
                                 * FINISH refActivity to START this activity
                                 * Verifica se a refActivity está fechada
                                 */
                                if (!refActivity_IsClosed)
                                {
                                    haveDirectDependences = true;
                                }
                                else if (lag > 0)
                                {
                                    // se estiver fechada e exitir lag time , vai verificar
                                    // se já passou o lag TIME
                                    haveLagTimeDependences = WFScheduleServices.activityVerifyLagTime(
                                            activity, refActivity, WF.STATE_CLOSE, lag);
                                }
                            }
                            else if (type.equals("SF"))
                            {
                                /**
                                 * START refActivity to FINISH this activity
                                 *
                                 * é uma relação de finalização
                                 *
                                 */
                            }
                            else if (type.equals("SS"))
                            {
                                /*
                                 * START refActivity to START this activity
                                 *
                                 * vou ver se a refActivity está running e ainda
                                 * para garantir que esta actividade pode arrancar vou ver tb
                                 * se a refActivity esta fechada
                                 *
                                 * SERÀ QUE NÃO DEVE SER ?!!!!
                                 * ------
                                 * if ( WorkFlowMethods.canStartActivity( refAtivity )
                                 * ------
                                 */
                                if (!(refActivity_IsRunning || refActivity_IsClosed))
                                {
                                    haveDirectDependences = true;
                                }
                                else if (lag > 0)
                                {
                                    haveLagTimeDependences = WFScheduleServices.activityVerifyLagTime(
                                            activity, refActivity, WF.STATE_STARTED, lag);
                                }
                            }
                            else if (type.equals("FF"))
                            {
                                /**
                                 *
                                 * FINISH refActivity to FINISH this activity
                                 *
                                 * Neste caso é uma relaçao para fechar
                                 * não interessa para o caso
                                 */
                            }
                        }
                    }
                }
            }
        }

        if (haveDirectDependences)
        {
            return WF.DEPENDS_YES;
        }
        else if (haveLagTimeDependences)
        {
            return WF.DEPENDS_LAGTIME;
        }
        else
        {
            return WF.DEPENDS_NO;
        }
    }

    /**
    * Faz o Evaluate da(s) condicão(ões)
    * para efectuar a actividade
    *
    * O metodo vai buscar os parents e verifica a condição
    * por cada WorkFlow onde está a actividade
    * NOTA : A condição não está na actividade mas sim no
    * Attribute <code>config.conditionFormula</code>
    *
    * @param activity      Objecto Actividade
    * @param parents       Array de Objectos pai
    *
    */
    public static final boolean activity_ConditionIsValid(boObject[] parents, boObject activity)
        throws boRuntimeException
    {
        boolean toRet = true;

        if (parents != null)
        {
            for (int i = 0; (i < parents.length) && toRet; i++)
            {
                bridgeHandler w = parents[i].getBridge("DAO");
                boBridgeRow br = null;
                if (w != null)
                {
                    br = w.getRow(activity.bo_boui);
                }

                if (br != null)
                {
                    boObject config = br.getAttribute("config").getObject();

                    if (config != null)
                    {
                        String condition = config.getAttribute("conditionFormula").getValueString();
                        Boolean isValid = WFinternal.activity_EvalCondition(
                                parents[i], w, br.getObject(), condition);

                        if (isValid == null)
                        {
                            // não foi possível fazer o EVALUATE
                            //TODO repensar ?!!
                            toRet = false;
                        }
                        else
                        {
                            toRet = isValid.booleanValue();
                        }
                    }
                }
            }
        }

        return toRet;
    }

    /**
     * Faz o eval de uma condição no WORKFLOW
     * @param parentActivity   actividade Pai
     * @param wkfl             WorkFlow Bridge
     * @param activity         actividade que tem a condição
     * @param condition        condiçao para fazer o evaluate
     *
     * @return <code>null</code> se não for possível fazer o evaluate ou se o evaluate der erro
     *         <code>Boolean true</code> se a condição for verdade
     *         <code>Boolean false</code> se a condição for false
     */
    protected static final Boolean activity_EvalCondition(
        boObject parentActivity, bridgeHandler wkfl, boObject activity, String condition)
        throws boRuntimeException
    {
        //TODO implement this
        return new Boolean(true);
    }

    /**
     * Muda o estado da actividade para STATE_CREATE
     *
     *
     * No caso de estar em STATE_OPEN .. mantém os subestados equivalentes.
     * Se quisermos forçar o recalculo deve-se
     * chamar o metodo WFinternal.computeChildStates_STATUS_CREATE
     *
     * No caso de estar em STATE_CLOSE o methodo chama
     * computeChildStates_STATUS_CREATE
     *
     * No caso de estar em STATE_ARCHIVE o metodo nao faz nada
     *
     * @param activity
     * @return boolean  Retorna false no caso de estar STATE_ARCHIVE , true nos outros casos
     *
     * @see computeChildStates_STATUS_CREATE
     */
    protected static final boolean activity_ChangeStateToCreate(boObject activity)
        throws boRuntimeException
    {
        boolean toRet = true;
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        String pstate = statePrimary.getValueString();

        if (pstate.equals(WF.STATE_OPEN))
        {
            //reset state 
            boObjectStateHandler openStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS);

            String alert = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS).getValueString();
            statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS).setValueString(null);

            if (openStatus.getValueString().equals(WF.STATE_WAITFOR))
            {
                boObjectStateHandler stateWaitFor = openStatus.getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS);

                if (stateWaitFor.getValueString().equals(WF.STATE_WAITINGQUEUE))
                {
                    stateWaitFor.setValueString(null);
                    openStatus.setValue(null);

                    statePrimary.setValueString(WF.STATE_CREATE);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(
                        WF.STATE_NOTSTARTED);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(
                        WF.STATE_WAITINGQUEUE);
                }
                else if (stateWaitFor.getValueString().equals(WF.STATE_CONDITIONNOTVALID))
                {
                    stateWaitFor.setValueString(null);
                    openStatus.setValueString(null);
                    statePrimary.setValueString(WF.STATE_CREATE);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(
                        WF.STATE_NOTSTARTED);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(
                        WF.STATE_CONDITIONNOTVALID);
                }
                else if (stateWaitFor.getValueString().equals(WF.STATE_WAITINGTIME))
                {
                    stateWaitFor.setValueString(null);
                    openStatus.setValueString(null);
                    statePrimary.setValueString(WF.STATE_CREATE);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(
                        WF.STATE_NOTSTARTED);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(
                        WF.STATE_WAITINGTIME);
                }

                statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(alert);
            }
            else if (openStatus.getValueString().equals(WF.STATE_WORKING))
            {
                openStatus.setValueString(null);
                statePrimary.setValueString(WF.STATE_CREATE);
                statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(WF.STATE_STARTED);
                statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(alert);
            }
            else if (openStatus.getValueString().equals(WF.STATE_HOLD))
            {
                openStatus.setValueString(null);
                statePrimary.setValueString(WF.STATE_CREATE);
                statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS).setValueString(WF.STATE_STARTED);
                statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(alert);
            }
        }
        else if (pstate.equals(WF.STATE_CREATE))
        {
            //logger.debug("Activity already in mode " + WF.STATE_CREATE);
               WFinternal.computeChildStates_STATUS_CREATE( activity );
        }
        else if (pstate.equals(WF.STATE_CLOSE))
        {
            boObjectStateHandler closeStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CLOSE_STATUS);
            closeStatus.setValueString(null);
            statePrimary.setValueString(WF.STATE_CREATE);
            computeChildStates_STATUS_CREATE(activity);
        }
        else if (pstate.equals(WF.STATE_ARCHIVE))
        {
        
           logger.debug("Activity in mode " + WF.STATE_ARCHIVE + " cannot change to " + WF.STATE_CREATE);
            toRet = false;
        }
        else
        {
            throw new boRuntimeException(
                activity, "WFInternal.changeStateToCreate", "WKFL-001", null,
                new String[] { pstate, WF.STATE_CREATE });
        }

        return toRet;
    }

    /**
     * Calcula os sub estados do estado STATE_CREATE
     *
     * @param activity
     * @see
     */
    public static final void computeChildStates_STATUS_CREATE(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);
        boObjectStateHandler createStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);

        /*
         * Passos a executar
         * 1- Verificar estados do pai
         * 2- Verificar se tem dependencias
         * 3- Verificar se a condicão e valida
         *
         */
        String name = activity.getAttribute("name").getValueString();
        boObject[] parents = activity.getParents();
        boolean validParents = true;
        int parentsHaveDependences = WF.DEPENDS_NO;
        for (int i = 0; i < parents.length; i++)
        {
            if (parentsHaveDependences == WF.DEPENDS_NO)
            {
                parentsHaveDependences = WFinternal.activity_haveDependences_FS_SS(parents[i], null);
            }

            validParents = validParents ||
                WFinternal.activity_ConditionIsValid(parents[i].getParents(), parents[i]);
        }

        if (validParents) // pelo menos um dos pais tem uma condição valida
        {
            if (parentsHaveDependences == WF.DEPENDS_NO)
            {
                int depends = activity_haveDependences_FS_SS(activity, null);
                if (depends == WF.DEPENDS_YES)
                {
                    createStatus.setValueString(WF.STATE_NOTSTARTED);

                    createStatus.getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(
                        WF.STATE_WAITINGQUEUE);
                }
                else if (depends == WF.DEPENDS_LAGTIME)
                {
                    createStatus.setValueString(WF.STATE_NOTSTARTED);

                    boObjectStateHandler notStartStatus = createStatus.getChildStateAtr(
                            WF.STATE_ATR_NOTSTART_STATUS);
                    notStartStatus.setValueString(WF.STATE_WAITINGTIME);
                }
                else if (depends == WF.DEPENDS_NO)
                {
                    if (WFinternal.activity_ConditionIsValid(activity.getParents(), activity))
                    {
                        if (WF.workFlow_IsComplete(activity.getBridge("DAO")))
                        {
                            createStatus.setValueString(WF.STATE_STARTED);
                        }
                        else
                        {
                            createStatus.setValueString(WF.STATE_NOTSTARTED);
                            createStatus.getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(
                                WF.STATE_WAITINGQUEUE);
                        }
                    }
                    else
                    {
                        boObjectStateHandler notStartStatus = createStatus.getChildStateAtr(
                                WF.STATE_ATR_NOTSTART_STATUS);
                        notStartStatus.setValueString(WF.STATE_CONDITIONNOTVALID);
                    }
                }
            }
            else
            {
                //os pais teem dependencias
                createStatus.setValueString(WF.STATE_NOTSTARTED);

                boObjectStateHandler notStartStatus = createStatus.getChildStateAtr(
                        WF.STATE_ATR_NOTSTART_STATUS);
                notStartStatus.setValueString(WF.STATE_WAITINGQUEUE);
            }
        }
        else
        {
            // os pais não estão Válido ... daí marca esta tb como invalida
            boObjectStateHandler notStartStatus = createStatus.getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS);
            notStartStatus.setValueString(WF.STATE_CONDITIONNOTVALID);
        }
    }

    /**
    * Muda o estado da actividade para STATE_OPEN
    *
    * No caso de estar em STATE_CREATE .. mantém os subestados equivalentes.
    * Se quisermos forçar o recalculo deve-se
    * chamar o metodo WFinternal.computeChildStates_STATUS_OPEN
    *
    * No caso de estar em STATE_CLOSE o methodo chama
    * computeChildStates_STATUS_OPEN
    *
    * No caso de estar em STATE_ARCHIVE o metodo nao faz nada
    *
    * @param activity
    * @return boolean  Retorna false no caso de estar STATE_ARCHIVE , true nos outros casos
    *
    * @see activity_ChangeStateToOpen
    */
    protected static final boolean activity_ChangeStateToOpen(boObject activity)
        throws boRuntimeException
    {
        boolean toRet = true;
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        String pstate = statePrimary.getValueString();

        if (pstate.equals(WF.STATE_CREATE))
        {
            //reset state 
            boObjectStateHandler createStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);

            String alert = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).getValueString();

            statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS).setValueString(null);

            if (createStatus.getValueString().equals(WF.STATE_NOTSTARTED))
            {
                boObjectStateHandler noStartStatus = createStatus.getChildStateAtr(
                        WF.STATE_ATR_NOTSTART_STATUS);

                if (noStartStatus.getValueString().equals(WF.STATE_WAITINGQUEUE))
                {
                    noStartStatus.setValueString(null);
                    createStatus.setValue(null);

                    statePrimary.setValueString(WF.STATE_OPEN);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_WAITFOR);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS).setValueString(
                        WF.STATE_WAITINGQUEUE);
                }
                else if (noStartStatus.getValueString().equals(WF.STATE_CONDITIONNOTVALID))
                {
                    noStartStatus.setValueString(null);
                    createStatus.setValueString(null);

                    statePrimary.setValueString(WF.STATE_OPEN);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_WAITFOR);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS).setValueString(
                        WF.STATE_CONDITIONNOTVALID);
                }
                else if (noStartStatus.getValueString().equals(WF.STATE_WAITINGTIME))
                {
                    noStartStatus.setValueString(null);
                    createStatus.setValueString(null);

                    statePrimary.setValueString(WF.STATE_OPEN);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_WAITFOR);
                    statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS)
                                .getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS).setValueString(
                        WF.STATE_WAITINGTIME);
                }

                statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS).setValueString(alert);
            }
            else if (createStatus.getValueString().equals(WF.STATE_STARTED))
            {
                createStatus.setValueString(null);
                statePrimary.setValueString(WF.STATE_OPEN);
                statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS).setValueString(WF.STATE_WORKING);
                statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS).setValueString(alert);
            }
        }
        else if (pstate.equals(WF.STATE_OPEN))
        {
            //logger.debug("Activity already in mode " + WF.STATE_OPEN);
            WFinternal.computeChildStates_STATUS_OPEN( activity );
        }
        else if (pstate.equals(WF.STATE_CLOSE))
        {
            // vai ver se os pais estão fechados
            boObject parents[] = activity.getParents();
            for (int i = 0; i < parents.length ; i++) 
            {
                String staParent = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
                if ( staParent.equals( WF.STATE_CLOSE ) || staParent.equals( WF.STATE_ARCHIVE ) )
                {
                    WFinternal.activity_ChangeStateToOpen( parents[i] );
                }
            }
            
            
            boObjectStateHandler closeStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CLOSE_STATUS);
            closeStatus.setValueString(null);
            statePrimary.setValueString(WF.STATE_OPEN);
            computeChildStates_STATUS_OPEN(activity);
            boObjectStateHandler openAlerts = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS);
            openAlerts.setValueString( WF.STATE_GREEN );
            
            boAttributesArray xatr = activity.getAttributes();
            Enumeration xenum = xatr.elements();
            while (xenum.hasMoreElements())
            {
                AttributeHandler xat;
                xat = ( AttributeHandler ) xenum.nextElement();
                xat.setEnabled();
            }
            
            // vai actualizar os pais
            
            
            for (int i = 0; i < parents.length ; i++) 
            {
                String staParent = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
                if ( staParent.equals( WF.STATE_OPEN ) ) 
                {
                    WFinternal.computeChildStates_STATUS_OPEN( parents[i] );
                }
                else if (  staParent.equals( WF.STATE_CREATE ) )
                {
                    WFinternal.computeChildStates_STATUS_CREATE( parents[i] );
                }
               WFinternal.workflow_RebuildAllStates( parents[i].getBridge("DAO") );
            }
        }
        else if (pstate.equals(WF.STATE_ARCHIVE))
        {
            logger.debug("Activity in mode " + WF.STATE_ARCHIVE + " cannot change to " + WF.STATE_OPEN);
            toRet = false;
        }
        else
        {
            throw new boRuntimeException(
                activity, "WFInternal.changeStateToOpen", "WKFL-001", null,
                new String[] { pstate, WF.STATE_OPEN });
        }

        return toRet;
    }

    /**
     * Calcula os sub estados do estado STATE_OPEN
     *
     * @param activity
     * @see
     */
    public static final void computeChildStates_STATUS_OPEN(boObject activity)
        throws boRuntimeException
    {
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);
        boObjectStateHandler openStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS);

        if (openStatus.getValueString().equals(WF.STATE_HOLD))
        {
            //do nothing -- foi suspenso pelo utilizador
        }
        else
        {
            /*
             * Passos a executar
             * 1- Verificar se tem dependencias
             * 2- Verificar se a condicão e valida
             *
             */
            boObject[] parents = activity.getParents();
            boolean validParents = true;
            int parentsHaveDependences = WF.DEPENDS_NO;
            for (int i = 0; i < parents.length; i++)
            {
                if (parentsHaveDependences == WF.DEPENDS_NO)
                {
                    parentsHaveDependences = WFinternal.activity_haveDependences_FS_SS(parents[i], null);
                }

                validParents = validParents ||
                    WFinternal.activity_ConditionIsValid(parents[i].getParents(), parents[i]);
            }

            if (validParents) // pelo menos um dos pais tem uma condição valida
            {
                if (parentsHaveDependences == WF.DEPENDS_NO)
                {
                    int depends = activity_haveDependences_FS_SS(activity, null);
                    

                    if (depends == WF.DEPENDS_YES  || !WF.workFlow_IsComplete( activity.getBridge("DAO") ) )
                    {
                        openStatus.setValueString(WF.STATE_WAITFOR);
String xname=activity.getAttribute("name").getValueString();
                        boObjectStateHandler waitForStatus = openStatus.getChildStateAtr(
                                WF.STATE_ATR_WAITFOR_STATUS);
                        waitForStatus.setValueString(WF.STATE_WAITINGQUEUE);
                    }
                    else if (depends == WF.DEPENDS_LAGTIME)
                    {
                        openStatus.setValueString(WF.STATE_WAITFOR);

                        boObjectStateHandler waitForStatus = openStatus.getChildStateAtr(
                                WF.STATE_ATR_WAITFOR_STATUS);
                        waitForStatus.setValueString(WF.STATE_WAITINGTIME);
                    }
                    else if (depends == WF.DEPENDS_NO)
                    {
                        if (WFinternal.activity_ConditionIsValid(activity.getParents(), activity))
                        {
                            openStatus.setValueString(WF.STATE_WORKING);
                            
                        }
                        else
                        {
                            openStatus.setValueString(WF.STATE_WAITFOR);
                            boObjectStateHandler waitForStatus = openStatus.getChildStateAtr(
                                    WF.STATE_ATR_WAITFOR_STATUS);
                            waitForStatus.setValueString(WF.STATE_CONDITIONNOTVALID);
                        }
                    }
                }
                else
                {
                    //os pais teem dependencias
                    openStatus.setValueString(WF.STATE_WAITFOR);

                    boObjectStateHandler waitForStatus = openStatus.getChildStateAtr(
                            WF.STATE_ATR_WAITFOR_STATUS);
                    waitForStatus.setValueString(WF.STATE_WAITINGQUEUE);
                }
            }
            else
            {
                // os pais não estão Válido ... daí marca esta tb como invalida
                boObjectStateHandler waitForStatus = openStatus.getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS);
                waitForStatus.setValueString(WF.STATE_CONDITIONNOTVALID);
            }
        }
    }

    /**
    * Muda o estado da actividade para STATE_CLOSE
    *
    * No caso de estar em STATE_ARCHIVE o metodo nao faz nada
    *
    * @param activity
    * @return boolean  Retorna false no caso de estar STATE_ARCHIVE , true nos outros casos
    *
    * @see activity_ChangeStateToOpen
    */
    protected static final boolean activity_ChangeStateToClose(boObject activity, String statusClose)
        throws boRuntimeException
    {
        boolean toRet = true;
        boObjectStateHandler statePrimary = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE);

        String pstate = statePrimary.getValueString();

        if (pstate.equals(WF.STATE_CREATE))
        {
            //reset state 
            boObjectStateHandler createStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_STATUS);
            boObjectStateHandler createAlerts = statePrimary.getChildStateAtr(WF.STATE_ATR_CREATED_ALERTS);

            createStatus.getChildStateAtr(WF.STATE_ATR_NOTSTART_STATUS).setValueString(null);
            createStatus.setValueString(null);
            createAlerts.setValueString(null);
        }
        else if (pstate.equals(WF.STATE_OPEN))
        {
            //reset state 
            boObjectStateHandler openStatus = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_STATUS);
            boObjectStateHandler openAlerts = statePrimary.getChildStateAtr(WF.STATE_ATR_OPEN_ALERTS);

            openStatus.getChildStateAtr(WF.STATE_ATR_WAITFOR_STATUS).setValueString(null);
            openStatus.setValueString(null);
            openAlerts.setValueString(null);
        }
        else if (pstate.equals(WF.STATE_CLOSE))
        {
        }
        else if (pstate.equals(WF.STATE_ARCHIVE))
        {
            logger.debug("Activity in mode " + WF.STATE_ARCHIVE + " cannot change to " + WF.STATE_OPEN);
            toRet = false;
        }
        else
        {
            throw new boRuntimeException(
                activity, "WFInternal.changeStateToOpen", "WKFL-001", null,
                new String[] { pstate, WF.STATE_CLOSE });
        }

        if (toRet)
        {
            statePrimary.setValueString(WF.STATE_CLOSE);
            statePrimary.getChildStateAtr(WF.STATE_ATR_CLOSE_STATUS).setValueString(statusClose);
        }

        return toRet;
    }

    /**
     * Chamado após ter sido adicionada uma activity ao Workflow
     * @param wkfl      Bridge Workflow á qual a activity foi adicionada neste momento
     * @param activity
     * @see
     */
    public static final void workflow_AfterAddActivity(bridgeHandler wkfl, boObject activity)
        throws boRuntimeException
    {
        String strState = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();

        if (strState.equals(WF.STATE_CREATE) || strState.equals(WF.STATE_OPEN))
        {
            if (strState.equals(WF.STATE_CREATE))
            {
                computeChildStates_STATUS_CREATE(activity);
            }
            else
            {
                computeChildStates_STATUS_OPEN(activity);
            }

            boObject[] parents = activity.getParents();

            for (int i = 0; i < parents.length; i++)
            {
                String stateParent = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
                if (stateParent.equals(WF.STATE_CREATE))
                {
                    computeChildStates_STATUS_CREATE(parents[i]);
                }
                else if (stateParent.equals(WF.STATE_OPEN))
                {
                    computeChildStates_STATUS_OPEN(parents[i]);
                }
                else if (stateParent.equals(WF.STATE_CLOSE))
                {
                    activity_ChangeStateToOpen(parents[i]);
                }
                else
                {
                    //TODO - arranjar ... pq a actividade ja está adicionada
                    throw new boRuntimeException(
                        parents[i], "WFInternal.workflow_AfterAddActivity", "WKFL-002", null,
                        new String[] { stateParent });
                }
            }
        }
    }

    /**
     * Chamado após ter sido alterado o attributo CONFIG do workflow
     * @param wkfl      Bridge Workflow na qual o config esta alterado
     * @param activity
     * @see
     */
    public static final void workflow_AfterChangeActivityConfig(bridgeHandler wkfl, boObject activity)
        throws boRuntimeException
    {
        String strState = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();

        if (strState.equals(WF.STATE_CREATE) || strState.equals(WF.STATE_OPEN))
        {
            if (strState.equals(WF.STATE_CREATE))
            {
                computeChildStates_STATUS_CREATE(activity);
            }
            else
            {
                computeChildStates_STATUS_OPEN(activity);
            }

            boObject[] parents = activity.getParents();

            for (int i = 0; i < parents.length; i++)
            {
                String stateParent = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
                if (stateParent.equals(WF.STATE_CREATE))
                {
                    computeChildStates_STATUS_CREATE(parents[i]);
                }
                else if (stateParent.equals(WF.STATE_OPEN))
                {
                    computeChildStates_STATUS_OPEN(parents[i]);
                }
                else if (stateParent.equals(WF.STATE_CLOSE))
                {
                    activity_ChangeStateToOpen(parents[i]);
                }
                else
                {
                    //TODO - arranjar ... pq a actividade ja está adicionada
                    throw new boRuntimeException(
                        parents[i], "WFInternal.workflow_AfterAddActivity", "WKFL-002", null,
                        new String[] { stateParent });
                }
            }
        }
    }

    /**
     * Recalcula estados do WorkFlow após operação de delete
     * @param wkfl      Bridge Workflow
     * @see
     */
    public static final void workflow_RebuildAllStates(bridgeHandler wkfl)
        throws boRuntimeException
    {
        if (wkfl.getRowCount() > 0)
        {
            boBridgeIterator it = wkfl.iterator();

            while (it.next())
            {
                boObject activity = it.currentRow().getObject();
                String strState = activity.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();

                if (strState.equals(WF.STATE_CREATE) || strState.equals(WF.STATE_OPEN))
                {
                    if (strState.equals(WF.STATE_CREATE))
                    {
                        computeChildStates_STATUS_CREATE(activity);
                    }
                    else
                    {
                        computeChildStates_STATUS_OPEN(activity);
                    }

                    boObject[] parents = activity.getParents();

                    for (int i = 0; i < parents.length; i++)
                    {
                        String stateParent = parents[i].getStateAttribute(WF.STATE_ATR_PRIMARY_STATE)
                                                       .getValueString();
                        if (stateParent.equals(WF.STATE_CREATE))
                        {
                            computeChildStates_STATUS_CREATE(parents[i]);
                        }
                        else if (stateParent.equals(WF.STATE_OPEN))
                        {
                            computeChildStates_STATUS_OPEN(parents[i]);
                        }
                        else if (stateParent.equals(WF.STATE_CLOSE))
                        {
                            activity_ChangeStateToOpen(parents[i]);
                        }
                        else
                        {
                            throw new boRuntimeException(
                                parents[i], "WFInternal.workflow_RebuildAllStates", "WKFL-002", null,
                                new String[] { stateParent });
                        }
                    }
                }
            }
        }
        else
        {
            /*
             * O workflow está vazio
             * vai analisar a activity pai
             */
            boObject activityParent = wkfl.getParent();

            String stateParent = activityParent.getStateAttribute(WF.STATE_ATR_PRIMARY_STATE).getValueString();
            if (stateParent.equals(WF.STATE_CREATE))
            {
                computeChildStates_STATUS_CREATE(activityParent);
            }
            else if (stateParent.equals(WF.STATE_OPEN))
            {
                computeChildStates_STATUS_OPEN(activityParent);
            }
            else if (stateParent.equals(WF.STATE_CLOSE))
            {
             //   activity_ChangeStateToOpen(parents[i]);
            }
            else
            {
                throw new boRuntimeException(
                    activityParent, "WFInternal.workflow_RebuildAllStates", "WKFL-002", null,
                    new String[] { stateParent });
            }
        }
    }
}
