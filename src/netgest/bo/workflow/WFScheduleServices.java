/*Enconding=UTF-8*/
package netgest.bo.workflow;

import netgest.bo.runtime.*;


/**
 * Classe responsável pelos serviços de controlo
 * de timings do Workflow
 * @author JMF
 * @version
 * @see
 */
public class WFScheduleServices
{
    /**
     *
     * @see
     */
    public WFScheduleServices()
    {
    }

    /**
     * Verifica se ja passou determinado LAGTIME desde o estado <code>sinceState</code> da actividade
     * refActivity
     *
     * @param activity          actividade que que tem parametrizado o LAG TIME
     * @param refActivity       actvidade de referencia
     * @param sinceState        ultimo estado da refActivity para contar o tempo
     * @param lagMinutes        tempo em minutos
     * @see
     */
    public static final boolean activityVerifyLagTime(
        boObject activity, boObject refActivity, String sinceState, long lagMinutes)
    {
        //TODO - implements
        return true;
    }
}
