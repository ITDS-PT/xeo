/*Enconding=UTF-8*/
package netgest.bo.builder;
import netgest.bo.runtime.EboContext;

/**
 * Interface a implementar para criar tarefas, para apartir do Builder poderem ser executadas.  
 * @author Pedro Castro Campos ( pedro.campos@itds.pt )
 * @version 1.0
 */
public interface ITask
{
    /**
     * Deve devolver se já foi feita ou não a tarefa.
     * @return true caso já tenha sido executada, false caso contário.
     * @param context Contexto em questão.
     */
    public boolean done(EboContext context);
    /**
     * Devolve se está valido para a execução.
     * @return true caso esteja valida, false caso contário.
     * @param context Contexto em questão.
     */
    public boolean validation(EboContext context);
    /**
     * Devolve se a tarefa foi executada com sucessso.
     * @return true caso tenha sido executada com sucesso, false caso contário.
     * @param context Contexto em questão.
     */    
    public boolean execute(EboContext context);
    /**
     * Devolve se o rollback foi executado com sucessso.
     * @return true caso tenha sido executado com sucesso, false caso contário.
     * @param context Contexto em questão.
     */      
    public boolean rollback(EboContext context);
}