/*Enconding=UTF-8*/
package netgest.xwf;

import java.util.Hashtable;
import java.util.List;
import netgest.bo.controller.xwf.XwfController;
import netgest.bo.runtime.boObject;
import netgest.bo.runtime.boObjectList;
import netgest.bo.runtime.boRuntimeException;
import netgest.xwf.common.xwfBoManager;
import netgest.xwf.core.xwfManager;


/**
 * Interface a implementar para aceder ao engine do WorkFlow  
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 2.0
 */
public interface EngineGate 
{   
    /**
     * Inicia um processo do workflow com base na definição de um programa,uma variável e modo de execução.
     * @param defProgramBoui  identificador da definição de um programa.
     * @param variable identificador de uma variável para iniciar o programa, < 0 caso contrário.
     * @param mode forma de execução do programa.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public void startProgramRuntime(long defProgramBoui,long variable, byte mode) throws boRuntimeException;
    /**
     * Inicia um processo do workflow com base na definição de um programa e de uma lista de variáveis. 
     * @param defProgramBoui identificador de definição de um programa.
     * @param variables lista de variaveis para iniciar o processo, null caso contrário.   
     * @param mode forma de execução do programa.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public void startProgramRuntime(long defProgramBoui,Hashtable variables, byte mode) throws boRuntimeException;
    /**
     * Devolve o programa a decorrer neste contexto.
     * @return objecto referente ao programa em execução.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObject getProgramRuntime() throws boRuntimeException;
    /**
     * Devolve o estado em que o workflow se encontra.
     * @return estado em execução.
     */
    public byte getExecutionMode();
    /**
     * Devolve um <code>boObject</code> no contexto do workflow. 
     * @param boui identificador do objecto a devolver.
     * @return objecto no contexto do engine.  
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObject getObject(long boui) throws boRuntimeException;    
    /**
     * Altera o estado e os dados de uma actividade, com base no seu estado anterior.
     * @param activityBoui identificador da actividade a alterar.
     * @param state novo estado da actividade.    
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public void setActivityState(long activityBoui, String state) throws boRuntimeException;
    /**
     * Termina uma actividade com um valor. 
     * @param activityBoui boui da actividade a alterar.
     * @param value valor da actividade.  
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public void setActivityValue(long activityBoui, String value) throws boRuntimeException;
    /**
     * Devolve a próxima actividade do workflow. 
     * @return próxima actividade do workflow.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObject getNextActivity() throws boRuntimeException;
    /**
     * Devolve a próxima actividade com base num ramo do workflow. 
     * @param branch ramo do workflow, null caso contrário.
     * @return próxima actividade do workflow.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObject getNextActivity(String branch) throws boRuntimeException;
    /**
     * Devolve a lista de actividades pendentes.
     * @return lista de actividades pendentes do workflow.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObjectList getPendingActivityList() throws boRuntimeException;
    /**
     * Devolve a lista de actividades criadas.
     * @return lista de actividades criadas do workflow. 
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObjectList getCreateActivityList() throws boRuntimeException;
    /**
     * Devolve a lista de actividades fechadas. 
     * @return lista de actividades fechadas do workflow. 
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boObjectList getCloseActivityList() throws boRuntimeException;
    /**
     * Chama o core do workflow para executar as acções pretendidas.
     * @param action acção a executar pelo workflow. 
     * @param object Objecto sobre o qual recai a acção.
     * @return true se tiver sucesso, false caso contrário.
     * @throws netgest.bo.runtime.boRuntimeException
     */
    public boolean doAction(String action, boObject object) throws boRuntimeException;
    /**
     * Adiciona mensagem de erro.
     * @param message mensagem de erro a adicionar.
     */
    public void addErrorMessage(String message);
    /**
     * Devolve a lista de erros.
     * @return lista de erros.
     */
    public List getErrorMessages();
    /**
     * Remove as mensagem de erro internas.
     */
    public void clearErrorMessages();
    
    public boObject getRuntimeActivity(XwfController controller) throws boRuntimeException;    
    public xwfBoManager getBoManager();      
}