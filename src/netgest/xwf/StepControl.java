/*Enconding=UTF-8*/
package netgest.xwf;

import netgest.bo.runtime.boRuntimeException;
import netgest.bo.runtime.boObject;
import org.w3c.dom.Node;
import netgest.xwf.core.xwfECMAevaluator;
import netgest.xwf.core.xwfControlFlow;
import netgest.xwf.core.xwfManager;
import netgest.xwf.common.xwfBoManager;
import netgest.utils.ngtXMLHandler;

/**
 * Interface que todos os controladores de steps do workflow devem implementar de forma a ser possível lançar,
 * finalizar, e tratar as actividades e o fluxo a que pertencem.
 */
public interface StepControl 
{
  
   /**
   * Rotina principal que está responsável pelo lançamento do respectivo passo
   * @return <code>true</code> se a execução estiver sido realizada em modo sincrono ou <code>false</code> caso tenha sido realizada em modo assincrono
   * @throws netgest.bo.runtime.boRuntimeException Excepções ao nível da execução do passo
   */
  public boolean execStep() throws boRuntimeException;
  
  /**
     * Chamada a quando da terminação do step por parte do utilizador. Vai permitir gerir o fluxo
     * preparando as próximas actividades a serem executadas
     * @throws netgest.bo.runtime.boRuntimeException Excepções ao nível do processamento dos próximos passos
     * @param cf    Controlo de fluxos responsável pela gestão do fluxo no momento da finalização do step
     * @param ngtn  Nó XML correpsondente a actividade terminada pelo utilizador
     */
  public void finishedStep(ngtXMLHandler ngtn, xwfControlFlow cf) throws boRuntimeException;
  
  /**
     * Chamada a quando da terminação do step de escolha por parte do utilizador. Vai permitir gerir o fluxo
     * preparando as próximas actividades a serem executadas
     * @throws netgest.bo.runtime.boRuntimeException Excepções ao nível do processamento dos próximos passos
     * @param cf    Controlo de fluxos responsável pela gestão do fluxo no momento da finalização do step
     * @param ngtn  Nó XML correpsondente a actividade terminada pelo utilizador
     * @param option Opção escolhida pelo utilizador neste step
     */
  public void finishedStep(ngtXMLHandler ngtn, String option, xwfControlFlow cf) throws boRuntimeException;
  
  /**
     * Antes de dar por concluido o step é chamado este método que fará o tratamento necessario à actividade
     * @throws netgest.bo.runtime.boRuntimeException Excepção na manipulação dos objectos no tratamento
     * @return <code>true</code> caso a tarefa deva ser terminada e se proceda ao avanço do fluxo, <code>false</code>
     * caso apenas deva ser finalizada mas não avançar com o fluxo 
     * @param manag Manager que está a processar o pedido de fim da actividade
     * @param actv  Actividade concluida pelo utilizador
     */
  public boolean specialTreatment(boObject actv, xwfManager manag) throws boRuntimeException;
  
  /**
     * Antes de dar por concluido o step é chamado este método que fará o tratamento necessario à actividade
     * @throws netgest.bo.runtime.boRuntimeException Excepção na manipulação dos objectos no tratamento
     * @return <code>true</code> caso a tarefa deva ser terminada e se proceda ao avanço do fluxo, <code>false</code>
     * caso apenas deva ser finalizada mas não avançar com o fluxo 
     * @param manag Manager que está a processar o pedido de fim da actividade
     * @param actv  Actividade concluida pelo utilizador
     * @param option Opção escolhida pelo utilizador neste step
     */
  public boolean specialTreatment(boObject actv, String option, xwfManager manag) throws boRuntimeException;
}